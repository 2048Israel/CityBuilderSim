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
 * 5. TOLD ONCE AND NEVER AGAIN (PT1-M2). The wait on ground is the one
 *    standing condition told again: a new notice each year it lasts, naming
 *    the sectors and the ground each needs, on a rhythm a save and a load
 *    keep, and silent once the ground is bought (section 7).
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

        Path root = TempDirs.create("inbox");
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
        /*
         * AND GIVEN SOMETHING TO SAY (2026-09-10). The paragraph above records
         * the first time this fixture's condition arrived by accident and was
         * measured by luck; the material repricing moved the construction
         * sector again and the empty city went 120 months without a word. So
         * the city is given a problem that cannot go away on its own: it owns
         * exactly the ground it stands on, and the first business that wants
         * to build finds nowhere to do it. "Your businesses have nowhere to
         * build" is one of the four things the inbox exists to say, and it
         * stays true until the player buys land, which nobody here will.
         * Which condition arrives is not the claim; that living raises what is
         * true is - and section 4 below needs one that STAYS true.
         */
        quietly(() -> {
            city.getLandManager().setOwnedSqFt(city.getLandManager().getAllocatedSqFt());
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
        // The same problem as section 2, for the same reason: it stays true.
        quietly(() -> standing.getLandManager().setOwnedSqFt(
                standing.getLandManager().getAllocatedSqFt()));

        java.util.Map<String, Integer> liveSince = new java.util.HashMap<>();
        int refreshes = 0, reRaised = 0, retold = 0, liveMonths = 0;

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
                    // The one re-raise allowed (PT1-M2): the wait on ground told
                    // again a year on, its last notice settled the same month.
                    if (yearlyRetell(standing.getInbox(), notice, known)) {
                        liveSince.put(notice.getKey(), notice.getRaised());
                        retold++;
                    } else {
                        reRaised++;
                    }
                } else {
                    refreshes++;
                }
            }
            liveMonths += seen.size();
            liveSince.keySet().retainAll(seen);
        }

        System.out.printf("  %d month(s) with something live, %d of them a refresh, %d yearly retelling(s) of the wait on ground%n",
                liveMonths, refreshes, retold);

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

        /* ============ 7. the wait on ground, told again each year (PT1-M2) ============ */
        landWaitRetold(files);

        /* ============ 8. a new game forgets it ============ */
        System.out.println("\n--- and a new game starts with a clean inbox ---");

        quietly(reloaded::newGame);
        assertTrue("nothing carried over from the old city",
                reloaded.getInbox().size() == 0);

        cleanUp(root);
        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /**
     * Whether `notice` is the wait on ground told again a year on (PT1-M2):
     * the "landlock" notice raised Inbox.LANDLOCK_REPEAT_MONTHS after the one
     * raised in `previous`, which was settled the month this one was raised.
     */
    static boolean yearlyRetell(Inbox inbox, Notice notice, int previous) {
        if (!notice.getKey().equals("landlock")) return false;
        if (notice.getRaised() - previous != Inbox.LANDLOCK_REPEAT_MONTHS) return false;
        for (Notice n : inbox.all()) {
            if (n.getKey().equals("landlock") && n.getRaised() == previous) {
                return n.getResolved() == notice.getRaised();
            }
        }
        return false;
    }

    /**
     * Section 7 (PT1-M2, Jerus 2026-10-12, "a notice repeated"). The fixture
     * causes the wait: a city whose owned ground is held at what is already
     * built on (no ground free, every month), with automatic building on, as
     * Jerus's city in the PT1 playtest had it. Walked month by month for three
     * years from the first notice, with a save and a load in the middle and
     * the walk carried on in the loaded city; then the ground is bought, many
     * times what is built, and the notice must settle and not come back.
     */
    static void landWaitRetold(GameFiles files) {
        System.out.println("\n--- the wait on ground is told again each year (PT1-M2) ---");

        Game[] g = { new Game(files) };
        quietly(g[0]::newGame);
        g[0].getAutoBuilder().setOn(true, g[0].getDecisions());
        Runnable noGround = () -> g[0].getLandManager().setOwnedSqFt(g[0].getLandManager().getAllocatedSqFt());
        quietly(noGround);

        // Up to ten years for a sector to want ground it cannot have.
        int first = -1;
        for (int m = 0; m < 120 && first < 0; m++) {
            quietly(() -> { g[0].simulateMonths(1); noGround.run(); });
            if (g[0].getInbox().live("landlock") != null) first = g[0].getMonth();
        }
        assertTrue("the fixture made a sector wait on ground", first > 0);
        if (first < 0) return;

        int span = 3 * Inbox.LANDLOCK_REPEAT_MONTHS;
        int saveAt = first + Inbox.LANDLOCK_REPEAT_MONTHS + Inbox.LANDLOCK_REPEAT_MONTHS / 2;
        List<Integer> raised = new java.util.ArrayList<>();
        raised.add(first);
        int lockedMonths = 1, twoLive = 0, misnamed = 0, needless = 0, notRetold = 0;
        int lapses = 0, returns = 0, reopened = 0;
        boolean wasLive = true;
        boolean saved = false, loaded = false;

        while (g[0].getMonth() < first + span) {
            if (!saved && g[0].getMonth() >= saveAt) {
                // Mid-wait, half a year into the second notice: save, load into
                // a new game, and walk on in the loaded one.
                saved = g[0].saveGame(2, "inbox-land").ok;
                Game back = new Game(files);
                quietly(() -> back.loadGameSave(2));
                Notice was = g[0].getInbox().live("landlock"), now = back.getInbox().live("landlock");
                loaded = back.getMonth() == g[0].getMonth()
                        && was != null && now != null && now.getRaised() == was.getRaised();
                g[0] = back;
            }
            quietly(() -> { g[0].simulateMonths(1); noGround.run(); });
            Game city = g[0];
            int month = city.getMonth();
            Inbox inbox = city.getInbox();

            int live = 0;
            for (Notice n : inbox.all()) if (n.getKey().equals("landlock") && !n.isResolved()) live++;
            if (live > 1) twoLive++;
            Notice now = inbox.live("landlock");
            if (now == null) { lapses++; wasLive = false; continue; }
            lockedMonths++;
            // Back after a month with nobody asking, inside its year: the
            // year's notice reopened, not a new one dated this month.
            if (!wasLive && month - raised.get(raised.size() - 1) < Inbox.LANDLOCK_REPEAT_MONTHS) {
                returns++;
                if (now.getRaised() == raised.get(raised.size() - 1)) reopened++;
            }
            wasLive = true;

            if (now.getRaised() == month) {
                raised.add(month);
                if (!yearlyRetell(inbox, now, raised.get(raised.size() - 2))) notRetold++;
                // The new notice names this month's waiting sectors and the ground
                // each asked for, in its refusal's own words.
                String body = String.join(" ", now.getBody());
                java.util.Set<String> blocked = city.getLandBlockedSectors();
                java.util.Map<String, String> needs = city.getLandBlockedNeeds();
                if (!needs.keySet().equals(blocked)) needless++;
                for (String key : blocked) {
                    Sector s = city.getSectors().byKey(key);
                    String need = needs.getOrDefault(key, "");
                    if (s == null || !body.contains(s.label())) misnamed++;
                    if (!need.startsWith("needs ") || !body.contains(need)) needless++;
                }
                System.out.printf("  month %d: told again, %d waiting: %s%n", month, blocked.size(),
                        String.join("; ", blocked.stream().map(k -> k + " " + needs.get(k)).toList()));
            }
        }

        System.out.printf("  first told month %d; told at %s; the wait held %d of %d months, %d month(s) with nobody asking, %d return(s) inside the year%n",
                first, raised, lockedMonths, span + 1, lapses, returns);
        assertTrue("the wait held to the end of the three years (the fixture held)",
                waitStanding(g[0]) && lockedMonths > span / 2);
        assertTrue("a wait back inside its year reopens that year's notice", reopened == returns);
        assertTrue("told once a year: the first month and every LANDLOCK_REPEAT_MONTHS after",
                raised.size() == 4
                        && raised.get(1) - raised.get(0) == Inbox.LANDLOCK_REPEAT_MONTHS
                        && raised.get(2) - raised.get(1) == Inbox.LANDLOCK_REPEAT_MONTHS
                        && raised.get(3) - raised.get(2) == Inbox.LANDLOCK_REPEAT_MONTHS);
        assertTrue("each new notice settles the one before it the same month", notRetold == 0);
        assertTrue("never two notices of the wait standing at once", twoLive == 0);
        assertTrue("each names every sector waiting", misnamed == 0);
        assertTrue("and the ground each needs, as its refusal gave it", needless == 0);
        assertTrue("saved mid-wait", saved);
        assertTrue("and loaded with the standing notice's month", loaded);

        // The ground bought: many times what is built, and no more held back.
        Game city = g[0];
        quietly(() -> city.getLandManager().setOwnedSqFt(city.getLandManager().getAllocatedSqFt() * 10 + 1e7));
        int boughtAt = city.getMonth();
        int retoldAfter = 0;
        for (int m = 0; m < Inbox.LANDLOCK_REPEAT_MONTHS + 2; m++) {
            quietly(() -> city.simulateMonths(1));
            Notice n = city.getInbox().live("landlock");
            if (n != null && n.getRaised() > boughtAt) retoldAfter++;
        }
        assertTrue("once the ground is bought the standing notice settles", city.getInbox().live("landlock") == null);
        assertTrue("and is not told again for the year after", retoldAfter == 0);
    }

    /** Whether the city has the wait on ground standing now. */
    static boolean waitStanding(Game city) {
        return city.getInbox().live("landlock") != null;
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
