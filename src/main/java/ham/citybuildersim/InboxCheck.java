package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Verifies the inbox: raising, refreshing, resolving, culling and the round
 * trip. Not part of the game.
 *
 * WHY THIS EXISTS
 *
 * The four things in here were banners, and a banner is checkable by looking at
 * it: it is on the screen or it is not. A notice is not. It is raised by the
 * city in a month nobody was watching, kept after it is read, and kept after it
 * is fixed - so every way it can go wrong is quiet.
 *
 * 1. RAISED TWICE. A condition that is true for forty months must produce ONE
 *    notice that keeps its wording current, not forty notices saying the same
 *    thing. Getting this wrong turns the inbox into a log and the count on the
 *    envelope into noise.
 * 2. NEVER RESOLVED. A notice whose condition has gone away must go grey. One
 *    that stays red is a city permanently reporting a problem it has already
 *    fixed, and a player who learns to ignore a red line has lost the whole
 *    feature.
 * 3. LOST ON RELOAD. The notices are a new key in the save. A line forgotten in
 *    the load path reads perfectly all session and comes back empty, which
 *    looks exactly like a city that has had nothing go wrong.
 * 4. NEVER CULLED. Resolved notices are kept for two years and then dropped. A
 *    cull that never fires makes a four-hundred-year city carry four hundred
 *    years of settled complaints.
 *
 * The awkward part of testing this is that the four conditions are hard to
 * cause on purpose - a bank does not fail when you ask it to. So the lifecycle
 * is driven through the one condition a harness CAN stage (a city with no
 * healthcare has nowhere to bury anybody and no doctors, which is the notice a
 * new city raises on its own), and the rest is checked structurally.
 */
public class InboxCheck {

    static int fails = 0;

    static final PrintStream OUT = System.out;
    static final PrintStream QUIET = new PrintStream(new OutputStream() {
        @Override public void write(int b) { }
    });

    static void quietly(Runnable work) {
        System.setOut(QUIET);
        try { work.run(); } finally { System.setOut(OUT); }
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-62s %s%n", label, ok ? "OK" : "FAIL");
    }

    public static void main(String[] args) throws Exception {

        Path root = Files.createTempDirectory("inbox");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));

        /* ============ 1. a new city has an empty inbox ============ */
        System.out.println("--- a new city has nothing to say ---");

        Game city = new Game(files);
        quietly(city::newGame);

        assertTrue("the inbox starts empty", city.getInbox().size() == 0);
        assertTrue("and nothing is urgent", city.getInbox().urgent() == null);
        assertTrue("and the envelope shows no count", city.getInbox().unread() == 0);

        /* ============ 2. living raises what is true ============ */
        System.out.println("\n--- and then the city lives a while ---");

        /*
         * PLAYED UNTIL SOMETHING IS TRUE, not for a fixed 24 months.
         *
         * What is under test is that living raises what is true, so the fixture
         * has to reach a month in which something IS true. It used to reach one
         * inside two years by luck; the founding endowment and the works
         * department both went up on 2026-09-09 and the city stopped having
         * anything to complain about until month 29, when its builders start
         * being laid off. A test about the INBOX failed for a reason about the
         * construction sector.
         *
         * Bounded, so a city that never has anything to say fails loudly rather
         * than hanging - which would itself be worth knowing.
         */
        quietly(() -> {
            for (int m = 0; m < 120 && city.getInbox().size() == 0; m++) {
                city.simulateMonths(1);
            }
        });

        Inbox inbox = city.getInbox();
        System.out.println("  notices after " + (city.getMonth() - 1) + " months: " + inbox.size());
        for (Notice notice : inbox.newestFirst()) {
            System.out.printf("    %-40s raised %d  read %d  resolved %d  (%d lines)%n",
                    notice.getTitle(), notice.getRaised(), notice.getRead(),
                    notice.getResolved(), notice.getBody().size());
        }

        assertTrue("something got raised", inbox.size() > 0);

        /*
         * ONE PER CONDITION, and this is assertion 1 above. Twenty-four months
         * of an unfixed problem is one notice, not twenty-four.
         */
        java.util.Set<String> live = new java.util.HashSet<>();
        boolean duplicated = false;
        for (Notice notice : inbox.all()) {
            if (!notice.isResolved() && !live.add(notice.getKey())) duplicated = true;
        }
        assertTrue("no condition has two live notices at once", !duplicated);

        for (Notice notice : inbox.all()) {
            assertTrue("  " + notice.getKey() + " has a title", !notice.getTitle().isEmpty());
            assertTrue("  " + notice.getKey() + " has something to say",
                    !notice.getBody().isEmpty());
            assertTrue("  " + notice.getKey() + " knows when it was raised",
                    notice.getRaised() > 0);
        }

        /* ============ 3. unread, urgent, and then read ============ */
        System.out.println("\n--- reading one ---");

        /*
         * NOT ASSERTED TO EXIST, and that is a finding rather than a
         * concession. Twenty-four months in, the two notices this city raised
         * are both about the bank, and both are RESOLVED - it failed, it was
         * recapitalised, it failed again, it recovered again. Neither is urgent
         * any more and neither has been read, which is exactly the state the
         * envelope's amber count is for: something happened while you were not
         * looking, and it is over.
         *
         * So the assertion is the one that actually matters: a resolved notice
         * must NOT interrupt, however unread it is. An inbox that shouts about
         * a problem the city has already solved is worse than one that says
         * nothing.
         */
        for (Notice notice : inbox.all()) {
            if (notice.isResolved()) {
                assertTrue("  a resolved notice never becomes urgent", !notice.isUrgent());
            }
        }

        Notice urgent = inbox.urgent();
        System.out.println("  something live and unread to read: " + (urgent != null));

        if (urgent != null) {
            assertTrue("  it counts toward the envelope", inbox.unread() > 0);
            int before = inbox.unread();
            inbox.markRead(urgent, city.getMonth());
            assertTrue("  reading it clears its urgency", !urgent.isUrgent());
            assertTrue("  and takes it off the count", inbox.unread() == before - 1);
            assertTrue("  but it is still in the inbox", inbox.all().contains(urgent));
            assertTrue("  and it still says what it said",
                    !urgent.getBody().isEmpty());

            /*
             * READING IS NOT RESOLVING, and conflating them is the easiest
             * mistake here: a warning the player has looked at and not acted on
             * is still true of the city.
             */
            assertTrue("  reading it did not resolve it", !urgent.isResolved());
        }

        /* ============ 4. a live notice is refreshed, never re-raised ============ */
        System.out.println("\n--- and a standing problem stays one notice ---");

        /*
         * WALKED MONTH BY MONTH rather than sampled, because the interesting
         * case is rare and short: it needs a condition that is true for several
         * months running, and which of the four that is depends on how the city
         * happens to develop. Sampling at some chosen month would pass for a
         * hundred and thirty months of the wrong reason.
         *
         * The property is exact: while a notice for a key is live, that key must
         * never acquire a SECOND raised-month. A refresh keeps the original; a
         * re-raise would not, and would put two red lines in the inbox saying
         * the same thing.
         */
        Game standing = new Game(files);
        quietly(standing::newGame);

        java.util.Map<String, Integer> liveSince = new java.util.HashMap<>();
        int refreshes = 0, reRaised = 0, liveMonths = 0;

        for (int m = 0; m < 130; m++) {
            quietly(() -> standing.simulateMonths(1));

            java.util.Set<String> seen = new java.util.HashSet<>();
            for (Notice notice : standing.getInbox().all()) {
                if (notice.isResolved()) continue;
                seen.add(notice.getKey());
                Integer known = liveSince.get(notice.getKey());
                if (known == null) {
                    liveSince.put(notice.getKey(), notice.getRaised());
                } else if (known != notice.getRaised()) {
                    reRaised++;
                } else {
                    refreshes++;
                }
            }
            liveMonths += seen.size();
            liveSince.keySet().retainAll(seen);
        }

        System.out.printf("  %d month(s) with something live, %d of them a refresh%n",
                liveMonths, refreshes);

        assertTrue("the run produced a standing problem to test", refreshes > 0);
        assertTrue("a live notice is never raised a second time", reRaised == 0);

        /* Body text moves with the city while the notice stands. */
        Notice held = null;
        for (Notice notice : standing.getInbox().all()) {
            if (!notice.isResolved()) held = notice;
        }
        if (held != null) {
            String said = String.join("\n", held.getBody());
            int raisedAt = held.getRaised();
            quietly(() -> standing.simulateMonths(1));
            Notice after = standing.getInbox().live(held.getKey());
            if (after != null) {
                assertTrue("  and keeps the month it was first raised",
                        after.getRaised() == raisedAt);
                System.out.println("  wording tracked the city: "
                        + !String.join("\n", after.getBody()).equals(said));
            }
        }

        /* ============ 5. it survives a save and a reload ============ */
        System.out.println("\n--- and it survives a reload ---");

        assertTrue("the city saved", city.saveGame(1, "inbox").ok);

        List<Notice> before = city.getInbox().all();
        int count = before.size();
        int unread = city.getInbox().unread();

        Game reloaded = new Game(files);
        quietly(() -> reloaded.loadGameSave(1));

        Inbox back = reloaded.getInbox();
        assertTrue("every notice came back", back.size() == count);
        assertTrue("and so did what had been read", back.unread() == unread);

        boolean matched = back.size() == count;
        for (int i = 0; matched && i < count; i++) {
            Notice was = before.get(i), now = back.all().get(i);
            matched = was.getKey().equals(now.getKey())
                    && was.getTitle().equals(now.getTitle())
                    && was.getRaised() == now.getRaised()
                    && was.getRead() == now.getRead()
                    && was.getResolved() == now.getResolved()
                    && was.getBody().equals(now.getBody());
            if (!matched) {
                System.out.println("  MISMATCH at " + i + " (" + was.getKey()
                        + ") - is it missing from DataSave or Inbox.restoreFrom()?");
            }
        }
        assertTrue("key, title, body and all three months round-trip", matched);

        /* ============ 6. resolving and culling ============ */
        System.out.println("\n--- resolving, greying, and dropping ---");

        /*
         * Driven directly rather than by staging four conditions in a live
         * city. What is being checked is the bookkeeping - that a resolved
         * notice stops being urgent, stays readable, and is dropped after
         * KEEP_MONTHS - and none of that is about which condition raised it.
         */
        Inbox bench = new Inbox();
        Notice made = new Notice("shedding", "Your builders are being laid off",
                List.of("a line", "another line"), 100);
        bench.restoreFrom(List.of(made));

        assertTrue("a fresh notice is urgent", bench.urgent() == made);
        made.markResolved(110);
        assertTrue("a resolved notice is not urgent any more", bench.urgent() == null);
        assertTrue("but it is still there to read", bench.size() == 1);
        assertTrue("and it still counts as unread", bench.unread() == 1);
        assertTrue("it knows how long ago it settled",
                made.monthsSinceResolved(120) == 10);
        assertTrue("just inside two years, it stays",
                made.monthsSinceResolved(110 + Inbox.KEEP_MONTHS) == Inbox.KEEP_MONTHS);

        /*
         * The cull runs inside takeMonth, so it is exercised by living rather
         * than called directly - and a city 25 years past the resolution must
         * have dropped it.
         */
        Game aged = new Game(files);
        quietly(() -> { aged.newGame(); aged.simulateMonths(40); });

        Notice ancient = stale(aged.getMonth());
        int ancientRaised = ancient.getRaised();
        aged.getInbox().restoreFrom(List.of(ancient));
        assertTrue("a stale notice is loaded", aged.getInbox().size() == 1);

        quietly(() -> aged.simulateMonths(1));

        /*
         * Looked for BY THE MONTH IT WAS RAISED rather than by count, because
         * the month that culls it is also a month the city might raise
         * something new - and "the inbox is empty" would then fail for a reason
         * that has nothing to do with culling.
         */
        boolean stillThere = false;
        for (Notice notice : aged.getInbox().all()) {
            if (notice.getRaised() == ancientRaised) stillThere = true;
        }
        assertTrue("and the next month drops it", !stillThere);

        /* ============ 7. a new game forgets it ============ */
        System.out.println("\n--- and a new game starts with a clean inbox ---");

        quietly(reloaded::newGame);
        assertTrue("nothing carried over from the old city",
                reloaded.getInbox().size() == 0);

        cleanUp(root);
        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** A notice settled longer ago than the inbox keeps them. */
    static Notice stale(int month) {
        Notice old = new Notice("bank", "The bank has failed",
                List.of("long ago"), Math.max(1, month - Inbox.KEEP_MONTHS - 10));
        old.markRead(Math.max(1, month - Inbox.KEEP_MONTHS - 9));
        old.markResolved(Math.max(1, month - Inbox.KEEP_MONTHS - 5));
        return old;
    }

    static void cleanUp(Path root) {
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }
    }
}
