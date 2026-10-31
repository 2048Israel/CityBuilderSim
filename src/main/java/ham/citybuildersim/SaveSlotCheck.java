package ham.citybuildersim;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Verifies the slot system: ten saves plus an autosave, the version stamp, and
 * the labels the menu is drawn from.
 *
 * The thing this is really guarding is INDEPENDENCE. A save menu that shows ten
 * slots and quietly writes them all to the same file, or draws slot 3's label
 * from slot 7's city, is worse than a single save - it invites a player to
 * spread a hundred hours across ten slots that were never really there.
 *
 * AND THE AUTOSAVE HOLDS A WHOLE MONTH (0.7.52). Until 0.7.51 the twelfth
 * month's autosave was written near the top of Game.nextMonth(), after the
 * calendar had turned and before the month ran: the file said month N, its
 * history ended at N - 1, and a city loaded from it never ran N. Each of the
 * three autosaves - the twelfth month's, the one before a skip and the one on
 * quit - is loaded here and played on: its month is its history's last, and
 * the history has no gap and no month twice.
 *
 * AND THE AUTOSAVE IS WRITTEN OFF THE CLOCK (UP1, 2026-10-22). The autosave
 * is filled on the FX thread and written on SaveWriter's, from a snapshot, and
 * section 7c holds each half of that on one grown city: 7c.1 that the two
 * snapshots taken of it share no object a month could change (and 7c.1b the
 * same of the states the city cannot reach by itself); 7c.2 that the month
 * which autosaves, on a history of MONTHLY_KEPT months, holds the clock at
 * most SaveWriter.CLOCK_MS - the one timing assertion in this file, through
 * Timing; 7c.3 that the files a snapshot writes behind three months are the
 * files the old way writes on this thread, field for field, and load at the
 * month asked; 7c.4 that two autosaves asked for back to back write once or
 * twice, never at once, and the file is whole after each; 7c.5 that a load, a
 * numbered save, a skip and Quit each wait for an autosave still held in the
 * writer; 7c.6 that a write which fails is reported on the calling thread,
 * leaves the earlier autosave whole, and is followed by one that writes; and
 * 7c.7 that the floor of real time (SaveWriter.FLOOR_SECONDS) holds the
 * autosaves to one in thirty seconds however fast the months come, on a clock
 * the check moves and on the real one. Section 7d (LB3) holds a file open while
 * a write swaps over it, as the Rankings board's read and an antivirus scan do on
 * Windows: the write waits for the handle (GameFiles.SWAP_RETRIES) and lands, one
 * held past every retry fails whole and says so, and the next lands.
 */
public class SaveSlotCheck {

    static int fails = 0;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-58s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void assertEquals(String label, Object actual, Object expected) {
        boolean ok = (actual == null) ? expected == null : actual.equals(expected);
        if (!ok) {
            fails++;
            System.out.printf("%-58s FAIL%n     got: %s%n     expected: %s%n",
                    label, actual, expected);
        } else {
            System.out.printf("%-58s OK%n", label);
        }
    }

    static BuildingsTemplate template(Game game, String name) {
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException(name);
    }

    public static void main(String[] args) throws Exception {

        Path root = TempDirs.create("slotcheck");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));

        /* ==================== 1. eleven distinct files ==================== */
        System.out.println("--- eleven slots, eleven files ---");

        java.util.Set<String> paths = new java.util.HashSet<>();
        for (int slot = GameFiles.AUTOSAVE_SLOT; slot <= GameFiles.SLOT_COUNT; slot++) {
            paths.add(files.saveFile(slot).toString());
            paths.add(files.historyFile(slot).toString());
        }
        assertEquals("22 distinct paths, no two slots sharing", paths.size(), 22);

        assertTrue("the autosave is named for what it is",
                files.saveFile(GameFiles.AUTOSAVE_SLOT).getFileName()
                        .toString().equals("autosave.json"));

        // Zero padded, so the folder sorts the way a person reads it.
        assertTrue("slot 2 sorts before slot 10",
                files.saveFile(2).getFileName().toString()
                        .compareTo(files.saveFile(10).getFileName().toString()) < 0);

        assertTrue("slot 0 is valid", GameFiles.isValidSlot(0));
        assertTrue("slot 10 is valid", GameFiles.isValidSlot(10));
        assertTrue("slot 11 is not", !GameFiles.isValidSlot(11));
        assertTrue("slot -1 is not", !GameFiles.isValidSlot(-1));

        /* ==================== 2. empty means empty ==================== */
        System.out.println("\n--- before anything is saved ---");

        for (int slot = GameFiles.AUTOSAVE_SLOT; slot <= GameFiles.SLOT_COUNT; slot++) {
            if (!files.slotIsEmpty(slot)) {
                fails++;
                System.out.println("slot " + slot + " should be empty  FAIL");
            }
        }
        System.out.printf("%-58s %s%n", "all eleven read as empty", "OK");
        assertEquals("and an empty slot has no header", files.readHeader(3), null);

        /* ============ 3. three cities that do not touch ============ */
        System.out.println("\n--- three different cities in three slots ---");

        Game game = new Game(files);
        game.run();
        game.buildStack(template(game, "House"), 20, false);
        game.simulateMonths(30);

        assertTrue("saved to slot 1", game.saveGame(1, "early").ok);

        int monthAt1 = game.getMonth();
        long popAt1 = game.getPopulationManager().getPopulation();

        game.buildStack(template(game, "House"), 60, false);
        game.buildStack(template(game, "Convenience Store"), 2, false);
        game.simulateMonths(40);
        assertTrue("saved to slot 7", game.saveGame(7, "bigger").ok);

        int monthAt7 = game.getMonth();

        game.simulateMonths(20);
        assertTrue("saved to slot 10", game.saveGame(10, null).ok);

        assertTrue("slot 1 exists", !files.slotIsEmpty(1));
        assertTrue("slot 7 exists", !files.slotIsEmpty(7));
        assertTrue("slot 10 exists", !files.slotIsEmpty(10));
        assertTrue("slot 4 is still empty", files.slotIsEmpty(4));
        assertTrue("slot 2 is too, for now", files.slotIsEmpty(2));

        // The assertion that matters: writing slot 7 did not disturb slot 1.
        SaveHeader h1 = files.readHeader(1);
        SaveHeader h7 = files.readHeader(7);
        SaveHeader h10 = files.readHeader(10);

        assertEquals("slot 1 still holds the month it was saved at",
                h1.getMonth(), monthAt1);
        assertEquals("slot 7 holds its own", h7.getMonth(), monthAt7);
        assertTrue("and slot 10 is later still", h10.getMonth() > monthAt7);

        assertEquals("slot 1's population is its own", h1.getPopulation(), popAt1);

        /* ==================== 4. names ==================== */
        System.out.println("\n--- names ---");

        assertEquals("the name was kept", h1.getSlotName(), "early");
        assertTrue("...and reported as present", h1.hasName());
        assertTrue("an unnamed slot says so", !h10.hasName());

        // Saving again without a name keeps the one the slot had - overwriting a
        // save should not silently strip its label.
        //
        // Slot 2, not slot 1. Doing this to slot 1 is what the first draft did,
        // and it quietly re-saved the early city at the CURRENT month - so the
        // round-trip assertions further down were checking a slot this section
        // had already overwritten. The test was wrong, not the code, and it took
        // three failures that all looked like load bugs to see it.
        game.saveGame(2, "keep me");
        game.saveGame(2);
        assertEquals("re-saving keeps the existing name",
                files.readHeader(2).getSlotName(), "keep me");

        /* ==================== 5. the version stamp ==================== */
        System.out.println("\n--- the stamp ---");

        assertEquals("the build that wrote it", h1.getGameVersion(), GameVersion.VERSION);
        assertEquals("the save format", h1.getSaveFormat(), GameVersion.SAVE_FORMAT);
        assertTrue("and when", h1.getSavedAt() > 0);
        assertTrue("this build is not from the future", !h1.isFromNewerBuild());

        // The case the stamp exists for.
        assertTrue("a save from a newer build is recognised",
                GameVersion.isFromNewerBuild(GameVersion.SAVE_FORMAT + 1));
        assertTrue("an older one is not - those still load",
                !GameVersion.isFromNewerBuild(GameVersion.SAVE_FORMAT - 1));

        // And the loader must refuse it rather than half-read it.
        String tampered = Files.readString(files.saveFile(7))
                .replace("\"saveFormat\": " + GameVersion.SAVE_FORMAT,
                         "\"saveFormat\": " + (GameVersion.SAVE_FORMAT + 99));
        Files.writeString(files.saveFile(7), tampered);

        assertTrue("the header sees it", files.readHeader(7).isFromNewerBuild());

        Game refuses = new Game(files);
        refuses.loadGameSave(7);
        assertTrue("and the load path refuses it", refuses.getLoadFailure() != null);
        assertEquals("with nothing loaded", refuses.getMonth(), 1);

        /* ============ 6. each slot round-trips into its own city ============ */
        System.out.println("\n--- loading them back ---");

        Game fromSlot1 = new Game(files);
        fromSlot1.loadGameSave(1);
        assertEquals("slot 1 loads the early city", fromSlot1.getMonth(), monthAt1);
        assertEquals("...with its own population",
                fromSlot1.getPopulationManager().getPopulation(), popAt1);
        assertEquals("...and no failure", fromSlot1.getLoadFailure(), null);

        Game fromSlot10 = new Game(files);
        fromSlot10.loadGameSave(10);
        assertTrue("slot 10 loads a later city", fromSlot10.getMonth() > monthAt7);

        // Loading slot 1 must not have disturbed slot 10 on disk.
        assertTrue("loading one slot does not touch another",
                files.readHeader(10).getMonth() > monthAt7);

        /* ==================== 7. the autosave ==================== */
        System.out.println("\n--- the autosave ---");

        Path autoRoot = TempDirs.create("autocheck");
        GameFiles autoFiles = new GameFiles(autoRoot.resolve("data"),
                autoRoot.resolve("no-legacy"));

        Game auto = new Game(autoFiles);
        // The floor (UP1) has passed at every look, as 30 s of play would pass it: the city gets a
        // save clock that runs past it each time it is read, so the months that are due write (the
        // skip's periodic autosaves below, the twelfth month's in 7b), and each holds a whole month.
        long[] now = { System.nanoTime() };
        auto.useSaveClockForChecks(() -> now[0] += (long) (SaveWriter.FLOOR_SECONDS * 1e9) + 1);
        auto.run();
        auto.buildStack(template(auto, "House"), 10, false);

        assertTrue("nothing autosaved yet", autoFiles.slotIsEmpty(GameFiles.AUTOSAVE_SLOT));
        assertEquals("twelve months to go", auto.getMonthsUntilAutosave(),
                Game.AUTOSAVE_MONTHS);

        // Single steps, so nothing but the interval can trigger it.
        for (int i = 0; i < Game.AUTOSAVE_MONTHS - 1; i++) {
            auto.simulateMonths(1);
        }
        assertTrue("still nothing at month " + Game.AUTOSAVE_MONTHS,
                autoFiles.slotIsEmpty(GameFiles.AUTOSAVE_SLOT));
        assertEquals("one month to go", auto.getMonthsUntilAutosave(), 1);

        auto.simulateMonths(1);
        assertTrue("the twelfth month writes it",
                !autoFiles.slotIsEmpty(GameFiles.AUTOSAVE_SLOT));
        assertEquals("and the counter starts again",
                auto.getMonthsUntilAutosave(), Game.AUTOSAVE_MONTHS);

        SaveHeader autoHeader = autoFiles.readHeader(GameFiles.AUTOSAVE_SLOT);
        assertTrue("it says it is an autosave",
                autoHeader.hasName() && autoHeader.getSlotName().startsWith("Autosave"));
        assertTrue("...and holds the month it fired at",
                autoHeader.getMonth() >= Game.AUTOSAVE_MONTHS);

        // A skip is one click that can undo a century, so it saves first.
        int beforeSkip = autoFiles.readHeader(GameFiles.AUTOSAVE_SLOT).getMonth();
        auto.simulateMonths(30);
        int afterSkip = autoFiles.readHeader(GameFiles.AUTOSAVE_SLOT).getMonth();
        assertTrue("a multi-month skip autosaves on the way in", afterSkip != beforeSkip);

        // And it never lands in a numbered slot.
        for (int slot = 1; slot <= GameFiles.SLOT_COUNT; slot++) {
            if (!autoFiles.slotIsEmpty(slot)) {
                fails++;
                System.out.println("autosave leaked into slot " + slot + "  FAIL");
            }
        }
        System.out.printf("%-58s %s%n", "the autosave never touches a numbered slot", "OK");

        /* ============ 7b. the autosave holds a whole month (0.7.52) ============ */
        System.out.println("\n--- the autosave holds a whole month, and plays on from it ---");

        // Single steps to the twelfth, so the interval is what writes it.
        while (auto.getMonthsUntilAutosave() > 1) auto.simulateMonths(1);
        auto.simulateMonths(1);
        wholeMonth("the twelfth month's", auto, autoFiles);
        // A skip writes it on the way in, before its first month...
        auto.simulateMonths(5);
        wholeMonth("the one before a skip", auto, autoFiles, auto.getMonth() - 5);
        // ...and quitting writes it where the city stands (toggleQuit() then exits).
        auto.saveForQuit();
        wholeMonth("the one on quit", auto, autoFiles);

        /* ============ 7c. the autosave off the clock (UP1, 2026-10-22) ============ */
        offTheClock();

        /* ============ 7d. a file held open while a write swaps over it (LB3) ============ */
        heldOpen();

        /* ==================== 8. histories are per slot ==================== */
        System.out.println("\n--- and each city keeps its own past ---");

        assertTrue("slot 1 has a history", Files.exists(files.historyFile(1)));
        assertTrue("slot 10 has its own", Files.exists(files.historyFile(10)));
        assertTrue("...and they are different files",
                !files.historyFile(1).equals(files.historyFile(10)));
        assertTrue("...with different contents",
                !Files.readString(files.historyFile(1))
                        .equals(Files.readString(files.historyFile(10))));

        cleanUp(root);
        cleanUp(autoRoot);

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ===== 7c. THE AUTOSAVE OFF THE CLOCK (UP1, 2026-10-22) =====================
     *
     * The autosave is written on SaveWriter's thread from a snapshot, while the
     * next months run on the FX thread. This section holds that the snapshot is
     * one: nothing in it is an object the model, or the next snapshot, could
     * change (7c.1, 7c.1b). The rest holds the writer itself, on that one
     * city, its history padded to MONTHLY_KEPT months: what it costs the
     * clock (7c.2), that it writes the old way's files (7c.3), one at a time
     * and whole (7c.4), that a load, a numbered save, a skip and Quit wait for
     * it (7c.5), what a failed write does (7c.6) and the floor of real time
     * (7c.7). The subsections are numbered 7c.1, 7c.2... and each prints its
     * own heading.
     * ====================================================================== */

    /**
     * How many months 7c grows its city: long enough that the lists and books
     * a city reaches by itself hold something - the inbox is empty at month 124
     * of this fixture and has a notice by 183, the bonds' books have orders
     * from the first years - short enough to take seconds (about 3 s).
     * What a city does not reach by itself (a bank rescue, a site on
     * overtime) is 7c.1b's, on states built field by field.
     */
    static final int GROWN_MONTHS = 180;

    /** How long 7d holds the file open before letting go: well inside the swap's retries (GameFiles.SWAP_RETRIES times SWAP_RETRY_MS). */
    static final long HOLD_MS = 100;

    /**
     * 7d (LB3's gate review, 2026-10-31): a write's swap over a file another handle has open. On Windows the move is
     * refused while the handle is open - the Rankings board reading a slot, an antivirus scan - and the slot kept its
     * old history beside its new save. The fixture causes it: a reader holds the target open, and a plain move over it
     * is refused. Then a write whose handle is let go after HOLD_MS lands, having waited; one whose handle outlasts every
     * retry fails, says so, and leaves the file whole; and the next write lands. Another filesystem lets a file be
     * replaced while it is open, and there is nothing to wait for: the section says so and asserts nothing.
     */
    static void heldOpen() throws Exception {
        System.out.println("\n--- 7d. a file held open while a write swaps over it: the write waits for it ---");
        Path root = TempDirs.create("heldopen");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Path target = files.historyFile(4);
        Path tmp = target.resolveSibling(target.getFileName() + ".tmp"), bak = target.resolveSibling(target.getFileName() + ".bak");
        Files.createDirectories(target.getParent());
        Files.writeString(target, "old");

        Path probe = target.resolveSibling("probe.json");
        Files.writeString(probe, "probe");
        boolean refused;
        try (java.io.Reader in = Files.newBufferedReader(target)) {
            try {
                Files.move(probe, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING, java.nio.file.StandardCopyOption.ATOMIC_MOVE);
                refused = false;
            } catch (java.nio.file.FileSystemException e) {
                refused = true;
            }
        }
        if (!System.getProperty("os.name", "").startsWith("Windows")) {
            System.out.println("not Windows: a file " + (refused ? "is" : "is not") + " refused a move while it is open here; 7d asserts nothing");
            cleanUp(root);
            return;
        }
        assertTrue("fixture: on Windows a reader holding the file open makes a plain move over it fail", refused);
        assertTrue("fixture: the hold (" + HOLD_MS + " ms) is inside the retries (" + GameFiles.SWAP_RETRIES * GameFiles.SWAP_RETRY_MS + " ms)",
                HOLD_MS < GameFiles.SWAP_RETRIES * GameFiles.SWAP_RETRY_MS / 2);
        Files.deleteIfExists(probe);

        java.io.Reader held = Files.newBufferedReader(target);
        Thread release = new Thread(() -> {
            try {
                Thread.sleep(HOLD_MS);
                held.close();
            } catch (Exception ignored) {
                // The write's assertions below say what happened.
            }
        }, "slotcheck-release");
        long from = System.nanoTime();
        release.start();
        GameFiles.Result waited = files.write(target, out -> out.write("new"));
        long took = (System.nanoTime() - from) / 1_000_000;
        release.join();
        assertTrue("a write over a file let go after " + HOLD_MS + " ms lands (" + took + " ms)", waited.ok && Files.readString(target).equals("new"));
        assertTrue("...after waiting for it: no sooner than the handle was let go", took >= HOLD_MS * 9 / 10);
        assertTrue("...with the .tmp gone and the file before it in the .bak", !Files.exists(tmp) && Files.readString(bak).equals("old"));

        GameFiles.Result refusedWrite;
        try (java.io.Reader stays = Files.newBufferedReader(target)) {
            refusedWrite = files.write(target, out -> out.write("newer"));
        }
        assertTrue("a write over a file held past every retry fails, and says so (" + refusedWrite.error + ")", !refusedWrite.ok && refusedWrite.error != null);
        assertTrue("...leaving the file whole as it was, and no .tmp", Files.readString(target).equals("new") && !Files.exists(tmp));
        GameFiles.Result after = files.write(target, out -> out.write("newest"));
        assertTrue("...and once it is let go the next write lands", after.ok && Files.readString(target).equals("newest"));
        cleanUp(root);
    }

    /**
     * 7c: grows the city and runs the subsections on it. 7c.1 is the walk:
     * two snapshots taken back to back on one grown city share no mutable
     * object (DataSave, and separately HistorySave), and no class in them is
     * one the walk cannot see into. 7c.1b follows it and 7c.2 to 7c.7 after,
     * each below; they share the city, and 7c.2 pads its history, so they run
     * in this order.
     */
    static void offTheClock() throws Exception {

        Path root = TempDirs.create("offclock");
        try {
            GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
            long grownAt = System.nanoTime();
            Game city = grownCity(files, GROWN_MONTHS);
            System.out.printf("      fixture: a city grown to month %d in %.1f s, %,d people%n", city.getMonth(),
                    (System.nanoTime() - grownAt) / 1e9, city.getPopulationManager().getPopulation());

            /* ---- 7c.1 two snapshots share nothing ---- */
            System.out.println("--- the autosave off the clock: two snapshots share nothing a month could change ---");

            assertTrue("fixture: the grown city has a map (the save carries its stamp)", city.hasCityMap());

            // Back to back, no month between: both are filled from the one live model, so a setter that
            // kept a live object in the first kept the same object in the second.
            SaveWriter.Job a = city.prepareSaveForChecks(GameFiles.AUTOSAVE_SLOT, "7c A");
            SaveWriter.Job b = city.prepareSaveForChecks(GameFiles.AUTOSAVE_SLOT, "7c B");
            assertTrue("fixture: both jobs carry the city map's bytes", a.map() != null && b.map() != null);

            Walk dataA = Walk.of(a.data(), "save"), dataB = Walk.of(b.data(), "save");
            Walk historyA = Walk.of(a.history(), "history"), historyB = Walk.of(b.history(), "history");
            System.out.printf("      the walk: save A %,d mutable objects, B %,d; history A %,d, B %,d%n",
                    dataA.mutable.size(), dataB.mutable.size(), historyA.mutable.size(), historyB.mutable.size());

            assertTrue("fixture: the walk reaches the save's lists and books, and the history's series",
                    dataA.mutable.size() > 100 && historyA.mutable.size() > 100);
            assertNoneUnseen("every class in the save is one the walk can see into", dataA, dataB);
            assertNoneUnseen("...and in the history", historyA, historyB);
            assertNoneShared("the save: no mutable object is in both snapshots", dataA, dataB);
            assertNoneShared("the history: no mutable object is in both snapshots", historyA, historyB);

            // The walk has to be able to find sharing, or a green result means nothing.
            Walk live = Walk.of(city.liveSaveForChecks(), "save");
            int withLive = Walk.shared(dataB, live).size();
            assertTrue("the walk sees sharing: a snapshot shares " + withLive
                    + " objects with the live save, as a shallow clone must", withLive > 0);

            List<String> sameSave = Walk.shared(dataA, Walk.of(a.data().snapshot(), "save"));
            assertTrue("...a snapshot of a snapshot shares all but the two objects it makes: itself and its buildings array ("
                    + sameSave.size() + " of " + dataA.mutable.size() + ")",
                    sameSave.size() == dataA.mutable.size() - 2
                    && sameSave.stream().noneMatch(s -> s.startsWith("save.buildings ") || s.equals("save : DataSave")));

            assertTrue("...and the history walked twice shares every one of its " + historyA.mutable.size() + " objects",
                    Walk.shared(historyA, Walk.of(a.history(), "history")).size() == historyA.mutable.size());

            // A deliberate leak, as a setter that kept the model's list would make: one list in two saves.
            DataSave leakyA = a.data().snapshot(), leakyB = b.data().snapshot();
            Field notices = DataSave.class.getDeclaredField("notices");
            notices.setAccessible(true);
            List<Notice> theModelsInbox = new ArrayList<>();
            notices.set(leakyA, theModelsInbox);
            notices.set(leakyB, theModelsInbox);
            assertTrue("...and one list held by both saves is named by its field",
                    Walk.shared(Walk.of(leakyA, "save"), Walk.of(leakyB, "save")).stream()
                            .anyMatch(s -> s.startsWith("save.notices ")));

            // The copies lose nothing: the state the model hands the setter and the state the save holds write the same JSON.
            com.google.gson.Gson gson = new com.google.gson.Gson();
            assertTrue("the save holds what the bond market writes",
                    gson.toJson(city.getBondMarket().toState()).equals(gson.toJson(b.data().getBondMarket())));
            assertTrue("...what the exchange writes",
                    gson.toJson(city.getExchange().toState()).equals(gson.toJson(b.data().getExchangeState())));
            assertTrue("...what the construction control writes",
                    gson.toJson(city.getBuildingManager().getControl().toState()).equals(gson.toJson(b.data().getConstructionControl())));
            assertTrue("...what the fund writes",
                    gson.toJson(city.getFund().toState()).equals(gson.toJson(b.data().getFund())));
            assertTrue("...what the inbox holds (" + b.data().getNotices().size() + " notices)",
                    !b.data().getNotices().isEmpty()
                    && gson.toJson(city.getInbox().all()).equals(gson.toJson(b.data().getNotices())));
            assertTrue("...what the build log holds (" + b.data().getBuilds().size() + " entries)",
                    !b.data().getBuilds().isEmpty()
                    && gson.toJson(city.getBuildLog().all()).equals(gson.toJson(b.data().getBuilds())));

            copiesOnFilledStates();

            /* ---- 7c.2 to 7c.7: the writer itself, on a city whose history is as long as a kept one ---- */
            // The city's floor clock, from here on this check's: the real one runs at 1x, and a floor
            // of 30 s of real time is not something a check waits for. Each subsection says how it moves it.
            long[] now = { System.nanoTime() };
            city.useSaveClockForChecks(() -> now[0]);

            clockCost(city, files, now);
            writtenBehind(city, files);
            oneAtATimeAlwaysWhole(city, files);
            theWaits(city, files);
            aWriteThatFails(city, files);
            theFloor(city, files, now);
        } finally {
            // A check that threw halfway must not leave the shared writer held for whatever runs next.
            SaveWriter.shared().release();
            cleanUp(root);
        }
    }

    /**
     * 7c.1b: the setters that copy, on states the grown city cannot reach (a
     * bank rescue's preferred, the fund's resolutions and orders, a site on
     * overtime): each is given an object with every field filled, and what the
     * save holds afterwards shares nothing with it and writes the same JSON. A
     * field added to one of these classes is filled by the same reflection, so
     * a copy() that forgets it, or a setter that stops copying, fails here.
     */
    static void copiesOnFilledStates() {
        System.out.println("\n--- the setters that copy, on states filled in every field ---");

        Filler filler = new Filler();
        DataSave save = new DataSave();

        ConstructionControl.State control = filler.make(ConstructionControl.State.class);
        save.setConstructionControl(control);
        copiesOf("the construction control's state (setConstructionControl)", control, save.getConstructionControl());

        BondMarket.State bonds = filler.make(BondMarket.State.class);
        save.setBondMarket(bonds);
        copiesOf("the bond market's state, its bonds and books (setBondMarket)", bonds, save.getBondMarket());

        Exchange.State exchange = filler.make(Exchange.State.class);
        save.setExchangeState(exchange);
        copiesOf("the exchange's state, its books (setExchangeState)", exchange, save.getExchangeState());

        TreasuryFund.State fund = filler.make(TreasuryFund.State.class);
        save.setFund(fund);
        copiesOf("the fund's state, its resolutions, orders and ledger (setFund)", fund, save.getFund());

        List<Bank.Preferred> preferred = new ArrayList<>(List.of(filler.make(Bank.Preferred.class), filler.make(Bank.Preferred.class)));
        save.setBankPreferred(preferred, new double[] { 1, 2, 3 });
        copiesOf("the bank's preferred blocks (setBankPreferred)", preferred, save.getBankPreferred());

        List<Notice> notices = new ArrayList<>(List.of(filler.make(Notice.class), filler.make(Notice.class)));
        save.setNotices(notices);
        copiesOf("the inbox's notices (setNotices)", notices, save.getNotices());

        List<BuildLog.Entry> builds = new ArrayList<>(List.of(new BuildLog.Entry("Farm", 3, 12), new BuildLog.Entry("Mine", 1, 13)));
        save.setBuilds(builds);
        copiesOf("the build log's entries (setBuilds)", builds, save.getBuilds());

        double[] heads = { 1.5, 2.5 };
        save.setLicences(heads);
        save.setSkilledWorkforce(heads);
        copiesOf("the licensed heads (setLicences)", heads, save.getLicences());
        copiesOf("the skilled heads (setSkilledWorkforce)", heads, save.getSkilledWorkforce());

        List<AutoBuilder.Entry> log = new ArrayList<>(List.of(filler.make(AutoBuilder.Entry.class), filler.make(AutoBuilder.Entry.class)));
        copiesOf("the automatic builder's log, as its toState() copies it", log, SaveCopy.list(log, AutoBuilder.Entry::copy));

        // The landmasses' placements (ET5) are built fresh by toState() on every save, as the zoning's are: a state filled in every field, put into the
        // table and written out again, shares no object with the one it came from and writes the same JSON.
        Landmasses.State placed = filler.make(Landmasses.State.class);
        Landmasses table = new Landmasses();
        table.restore(placed);
        copiesOf("the landmasses' placements, built fresh by toState() (DataSave.setLandmasses)", placed, table.toState());
        assertTrue("...and two states of one table share nothing: each save is its own", Walk.shared(Walk.of(table.toState(), "first"), Walk.of(table.toState(), "second")).isEmpty());

        // The long bridges (ET6) are built fresh by toState() on every save too: a state filled in every field (its kind and state made into a bridge's
        // own, which restore() passes over else), put into the list and written out again, shares no object with the one it came from and writes the same JSON.
        Bridges.State built = filler.make(Bridges.State.class);
        for (Bridges.Row row : built.bridges) {
            row.kind = BridgeWorks.Kind.RAIL.name();
            row.state = Bridges.ON_SITE;
        }
        Bridges list = new Bridges();
        list.restore(built);
        copiesOf("the long bridges, built fresh by toState() (DataSave.setBridges)", built, list.toState());
        assertTrue("...and two states of one list share nothing: each save is its own", Walk.shared(Walk.of(list.toState(), "first"), Walk.of(list.toState(), "second")).isEmpty());

        // ...and the check can tell: the same state held without a copy shares every one of its objects.
        Walk whole = Walk.of(control, "source");
        assertTrue("a state held uncopied is caught: all " + whole.mutable.size() + " of its objects are shared",
                whole.mutable.size() > 0 && Walk.shared(whole, Walk.of(control, "held")).size() == whole.mutable.size());

        assertTrue("every field of every class above was given a value (" + filler.unfilled.size() + " it could not fill)",
                filler.unfilled.isEmpty());
        for (String u : filler.unfilled) System.out.println("         unfilled: " + u);
    }

    /** `held` writes the JSON `source` does and shares no mutable object with it; `source` has something in it to share. */
    static void copiesOf(String label, Object source, Object held) {
        Walk theSource = Walk.of(source, "source"), theCopy = Walk.of(held, "held");
        List<String> shared = Walk.shared(theSource, theCopy);
        com.google.gson.Gson gson = new com.google.gson.Gson();
        assertTrue(label + ": holds a copy (" + shared.size() + " shared of " + theSource.mutable.size() + ")",
                theSource.mutable.size() > 0 && shared.isEmpty() && theCopy.opaque.isEmpty());
        for (int i = 0; i < Math.min(10, shared.size()); i++) System.out.println("         shared: " + shared.get(i));
        assertTrue("...and writes what the source writes", gson.toJson(source).equals(gson.toJson(held)));
    }

    /**
     * An instance of a model class with every Gson-visible field set to a
     * value that is not its default: a number, a string, the last constant of
     * an enum, two elements in each array, list and map, a nested model
     * object filled the same way. It builds through the no-argument
     * constructor, which every saved class has for Gson. A field of a type it
     * does not know is left alone and named in {@link #unfilled}.
     */
    static final class Filler {

        final List<String> unfilled = new ArrayList<>();
        private int next = 1;
        private int depth;

        <T> T make(Class<T> type) {
            try {
                java.lang.reflect.Constructor<T> constructor = type.getDeclaredConstructor();
                constructor.setAccessible(true);
                T object = constructor.newInstance();
                if (++depth > 8) {
                    depth--;
                    return object;
                }
                for (Field f : Walk.fieldsOf(type)) {
                    Object value = value(f.getGenericType());
                    if (value == null) {
                        unfilled.add(type.getSimpleName() + "." + f.getName() + " : " + f.getGenericType());
                    } else {
                        f.set(object, value);
                    }
                }
                depth--;
                return object;
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException(type.getName(), e);
            }
        }

        private Object value(java.lang.reflect.Type type) {
            if (type instanceof Class<?> c) {
                if (c == int.class || c == Integer.class) return next++;
                if (c == long.class || c == Long.class) return (long) next++;
                if (c == double.class || c == Double.class) return next++ + 0.5;
                if (c == boolean.class || c == Boolean.class) return true;
                if (c == String.class) return "s" + next++;
                if (c.isEnum()) {
                    Object[] constants = c.getEnumConstants();
                    return constants[constants.length - 1];
                }
                if (c.isArray()) {
                    Object array = Array.newInstance(c.getComponentType(), 2);
                    for (int i = 0; i < 2; i++) Array.set(array, i, value(c.getComponentType()));
                    return array;
                }
                if (Walk.isModel(c) && !c.isRecord() && !c.isInterface() && !Modifier.isAbstract(c.getModifiers())) return make(c);
                return null;
            }
            if (type instanceof java.lang.reflect.ParameterizedType p) {
                Class<?> raw = (Class<?>) p.getRawType();
                java.lang.reflect.Type[] arguments = p.getActualTypeArguments();
                if (raw == List.class || raw == ArrayList.class) {
                    List<Object> list = new ArrayList<>();
                    for (int i = 0; i < 2; i++) list.add(value(arguments[0]));
                    return list;
                }
                if (raw == Map.class || raw == java.util.LinkedHashMap.class || raw == java.util.HashMap.class) {
                    Map<Object, Object> map = new java.util.LinkedHashMap<>();
                    for (int i = 0; i < 2; i++) map.put(value(arguments[0]), value(arguments[1]));
                    return map;
                }
            }
            return null;
        }
    }

    static void assertNoneShared(String label, Walk first, Walk second) {
        List<String> shared = Walk.shared(first, second);
        assertTrue(label + " (" + shared.size() + " shared)", shared.isEmpty());
        for (int i = 0; i < Math.min(30, shared.size()); i++) System.out.println("         shared: " + shared.get(i));
        if (shared.size() > 30) System.out.println("         ... and " + (shared.size() - 30) + " more");
    }

    static void assertNoneUnseen(String label, Walk first, Walk second) {
        java.util.Set<String> unseen = new java.util.TreeSet<>(first.opaque);
        unseen.addAll(second.opaque);
        assertTrue(label + " (" + unseen.size() + " it cannot see into)", unseen.isEmpty());
        int n = 0;
        for (String u : unseen) {
            if (n++ == 30) { System.out.println("         ... and " + (unseen.size() - 30) + " more"); break; }
            System.out.println("         opaque: " + u);
        }
    }

    /* ----- 7c.2 to 7c.7: the writer, on a city whose history is as long as a kept one ----- */

    /** The floor in nanoseconds, as Game.nextMonth() reads it. */
    static long floorNanos() {
        return (long) (SaveWriter.FLOOR_SECONDS * 1e9);
    }

    /** A timing assertion's line: Timing.verdict says OK, FAIL, or SOFT with the figure and its budget. */
    static void assertTimed(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-58s %s%n", label, Timing.verdict(ok, label));
    }

    /** The slot name the autosave file carries, or null when there is no readable autosave. */
    static String autosaveName(GameFiles files) {
        SaveHeader header = files.readHeader(GameFiles.AUTOSAVE_SLOT);
        return header == null ? null : header.getSlotName();
    }

    /** A file parsed as one JSON object, or null when it is missing or is not whole JSON. */
    static JsonObject parsed(Path file) {
        try {
            JsonElement tree = JsonParser.parseString(Files.readString(file));
            return tree.isJsonObject() ? tree.getAsJsonObject() : null;
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }

    /** The stream the model's per-month printing goes to while 7c steps months: each month prints some forty lines, and nobody reads them here. */
    private static final PrintStream SILENT = new PrintStream(OutputStream.nullOutputStream());

    /** Runs {@code work} with System.out swallowed, and restores it. The figure 7c.2 times is the month's, not the console's. */
    static void quietly(Runnable work) {
        PrintStream real = System.out;
        System.setOut(SILENT);
        try {
            work.run();
        } finally {
            System.setOut(real);
        }
    }

    /** One month on the clock, as the window's clock steps it (toggleNextMonth(), no wait), the model's printing swallowed. */
    static void step(Game city) {
        quietly(city::toggleNextMonth);
    }

    /** The median of a set of figures. */
    static double median(double[] figures) {
        double[] sorted = figures.clone();
        Arrays.sort(sorted);
        return sorted[sorted.length / 2];
    }

    /**
     * Grows a history to MONTHLY_KEPT months through its own recording path
     * (HistorySave.appendMonth(), which ends in the fold recordMonth() ends
     * in), each series given its own last recorded value, or 0 for an empty
     * one. The months added sit after the city's real ones on the axis: they
     * are length, not play. Nothing in 7c.2 to 7c.7 reads what the axis says,
     * only how long it is (7c.4's and 7c.6's read-backs, 7c.3's load); they
     * time the clock and compare files.
     */
    static void padHistory(HistorySave history) {
        Map<String, Double> last = new HashMap<>();
        for (Map.Entry<String, List<? extends Number>> series : history.seriesByName().entrySet()) {
            List<? extends Number> values = series.getValue();
            double v = values == null || values.isEmpty() ? 0 : values.get(values.size() - 1).doubleValue();
            last.put(series.getKey(), Double.isFinite(v) ? v : 0.0);
        }
        List<Integer> axis = history.getMonth();
        int next = axis.get(axis.size() - 1) + 1;
        while (history.months() < HistorySave.MONTHLY_KEPT) {
            history.appendMonth(next++, name -> last.getOrDefault(name, 0.0));
        }
    }

    /**
     * The autosave on disk, read back: its save and its history both whole
     * JSON, the save at the city's month, the history as long as the city's
     * (no month has run since it was asked for).
     */
    static void wholeOnDisk(String which, Game city, GameFiles files) {
        JsonObject save = parsed(files.saveFile(GameFiles.AUTOSAVE_SLOT));
        JsonObject history = parsed(files.historyFile(GameFiles.AUTOSAVE_SLOT));
        assertTrue(which + ": the save reads back as whole JSON", save != null);
        assertTrue(which + ": the history reads back as whole JSON", history != null);
        assertTrue(which + ": the save holds the city's month " + city.getMonth(),
                save != null && save.has("month") && save.get("month").getAsInt() == city.getMonth());
        assertTrue(which + ": the history holds all the city's " + city.getHistorySave().months() + " months",
                history != null && history.has("month") && history.getAsJsonArray("month").size() == city.getHistorySave().months());
    }

    /** Lets the held writer go after {@code ms}, on a thread of its own; {@code releasedAt} reads 0 until it has (nanoTime, set just before the release). */
    static Thread releaseLater(long ms, AtomicLong releasedAt) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            releasedAt.set(System.nanoTime());
            SaveWriter.shared().release();
        }, "7c-release");
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    /**
     * 7c.2: the clock. A grown city's history is padded to MONTHLY_KEPT
     * months - the length at which the autosave held the clock longest before
     * UP1 - and the month that autosaves is timed, the whole
     * toggleNextMonth() the window's clock calls, five times after two
     * warm-ups: its median holds the clock at most SaveWriter.CLOCK_MS. Each
     * measured month is shown to have autosaved, or the figure is the cost of
     * a month that did not. The clock the floor reads is moved past the floor
     * before each, so the due month is the one that writes.
     */
    static void clockCost(Game city, GameFiles files, long[] now) throws Exception {
        System.out.println("\n--- the autosave off the clock: the month that autosaves holds the clock at most CLOCK_MS ---");

        SaveWriter writer = SaveWriter.shared();
        HistorySave history = city.getHistorySave();
        city.awaitSaves();

        int own = history.months();
        assertTrue("fixture: the city's own history (" + own + " months) is shorter than a kept one", own < HistorySave.MONTHLY_KEPT);
        assertEquals("fixture: ...and has folded no year", history.yearlyPoints(), 0);
        history.saveHistory(files, 9);
        long ownBytes = Files.size(files.historyFile(9));

        padHistory(history);
        assertEquals("fixture: the history holds MONTHLY_KEPT months", history.months(), HistorySave.MONTHLY_KEPT);

        // The old way, for the figure that is the condition: the whole save written on this thread.
        long start = System.nanoTime();
        GameFiles.Result oldWay = city.saveGame(3, "7c old way");
        double oldMs = (System.nanoTime() - start) / 1e6;
        long bytes = Files.size(files.historyFile(3));
        System.out.printf("      the old way, written on this thread: %.0f ms, the history file %.1f MB (informational: this is what the clock held)%n",
                oldMs, bytes / 1e6);
        assertTrue("fixture: the old way saved", oldWay.ok);
        double grewBy = (double) bytes / ownBytes, monthsGrewBy = (double) HistorySave.MONTHLY_KEPT / own;
        assertTrue(String.format("fixture: the history file is %.1f MB, %.0fx the city's own %.2f MB (its months grew %.0fx)",
                bytes / 1e6, grewBy, ownBytes / 1e6, monthsGrewBy), grewBy >= monthsGrewBy / 2);

        final int warmUps = 2, measured = 5;
        double[] dueMs = new double[measured];
        List<Double> plainMs = new ArrayList<>();
        boolean eachWrote = true;
        for (int round = 0; round < warmUps + measured; round++) {
            city.awaitSaves();
            // Past the floor, so the month that is due is the month that writes.
            now[0] += floorNanos() + 1_000_000_000L;
            while (city.getMonthsUntilAutosave() > 1) {
                long began = System.nanoTime();
                step(city);
                plainMs.add((System.nanoTime() - began) / 1e6);
            }
            int writesBefore = writer.writes();
            long began = System.nanoTime();
            step(city);
            double took = (System.nanoTime() - began) / 1e6;
            city.awaitSaves();
            eachWrote &= writer.writes() - writesBefore == 1 && ("Autosave - month " + city.getMonth()).equals(autosaveName(files));
            if (round >= warmUps) dueMs[round - warmUps] = took;
        }

        double median = median(dueMs);
        double[] plain = plainMs.stream().mapToDouble(Double::doubleValue).toArray();
        System.out.printf("      the %d due months, ms: %s; median %.1f ms%n", measured,
                Arrays.stream(dueMs).mapToObj(v -> String.format("%.1f", v)).collect(java.util.stream.Collectors.joining(", ")), median);
        System.out.printf("      for comparison, the %d months between them (no autosave): median %.1f ms%n", plain.length, median(plain));

        assertTrue("every due month, warm-ups too, wrote one autosave named for its month", eachWrote);
        assertTimed(String.format("the median due month, %.1f ms, holds the clock at most CLOCK_MS (%d ms)", median, SaveWriter.CLOCK_MS),
                Timing.within(median, SaveWriter.CLOCK_MS));
    }

    /**
     * 7c.3: what the writer writes is what the old way writes. Slot 3 is the
     * city saved on this thread from its live objects; the autosave is a
     * snapshot of the same state taken straight after and held in the writer
     * while three months change the city. Released, the two sets of files
     * match - the save in every field but its name and its time, the history
     * as text and as a tree, the map byte for byte - and the autosave loads
     * at the month it was asked at. The save clock stands still, so the floor
     * binds and no month of the three writes anything of its own.
     */
    static void writtenBehind(Game city, GameFiles files) throws Exception {
        System.out.println("\n--- the autosave off the clock: written behind the months, the same files as the old way ---");

        SaveWriter writer = SaveWriter.shared();
        city.awaitSaves();
        int writesBefore = writer.writes();

        GameFiles.Result oldWay = city.saveGame(3, "7c.3");
        assertTrue("the old way saved slot 3 from the live objects", oldWay.ok);

        int monthAsked = city.getMonth();
        double cashAsked = city.getCash();
        int historyAsked = city.getHistorySave().months();
        String nameBefore = autosaveName(files);

        writer.hold();
        city.autosave("7c.3");
        for (int i = 0; i < 3; i++) step(city);

        assertEquals("fixture: three months ran while the snapshot waited", city.getMonth(), monthAsked + 3);
        assertTrue("fixture: and they changed the city (cash " + (long) cashAsked + " to " + (long) city.getCash() + ")", city.getCash() != cashAsked);
        assertEquals("fixture: the writer was held: the autosave on disk is still the one before", autosaveName(files), nameBefore);
        assertEquals("fixture: ...and nothing was written meanwhile", writer.writes(), writesBefore);

        writer.release();
        city.awaitSaves();
        assertEquals("released, the writer wrote the one snapshot", writer.writes() - writesBefore, 1);
        assertEquals("...named as it was asked", autosaveName(files), "Autosave - 7c.3");

        JsonObject slot = parsed(files.saveFile(3)), auto = parsed(files.saveFile(GameFiles.AUTOSAVE_SLOT));
        assertTrue("both saves read back as whole JSON", slot != null && auto != null);
        if (slot != null && auto != null) {
            for (String stamp : new String[] { "slotName", "savedAt" }) {
                slot.remove(stamp);
                auto.remove(stamp);
            }
            java.util.Set<String> keys = new java.util.TreeSet<>(slot.keySet());
            keys.addAll(auto.keySet());
            List<String> differ = new ArrayList<>();
            for (String key : keys) {
                if (!java.util.Objects.equals(slot.get(key), auto.get(key))) differ.add(key);
            }
            assertTrue("the save: all " + keys.size() + " fields match the old way's but its name and its time"
                    + (differ.isEmpty() ? "" : " - differ: " + differ.subList(0, Math.min(8, differ.size()))), differ.isEmpty());
            assertTrue("...and the two trees are equal", slot.equals(auto));
        }

        String slotHistory = Files.readString(files.historyFile(3));
        String autoHistory = Files.readString(files.historyFile(GameFiles.AUTOSAVE_SLOT));
        assertTrue(String.format("the history is the same text, %.1f MB written compact by the same code", slotHistory.length() / 1e6),
                slotHistory.equals(autoHistory));
        assertTrue("...and the same tree", JsonParser.parseString(slotHistory).equals(JsonParser.parseString(autoHistory)));

        assertTrue("the city map exists beside both", Files.isRegularFile(files.mapFile(3)) && Files.isRegularFile(files.mapFile(GameFiles.AUTOSAVE_SLOT)));
        assertTrue("...and is the same bytes",
                Arrays.equals(Files.readAllBytes(files.mapFile(3)), Files.readAllBytes(files.mapFile(GameFiles.AUTOSAVE_SLOT))));

        Game back = new Game(files);
        back.loadGameSave(GameFiles.AUTOSAVE_SLOT);
        assertEquals("the autosave loads with no failure", back.getLoadFailure(), null);
        assertEquals("...at the month it was asked at, not three on", back.getMonth(), monthAsked);
        assertEquals("...with the cash it was asked at", back.getCash(), cashAsked);
        assertEquals("...and the history it was asked at", back.getHistorySave().months(), historyAsked);
    }

    /**
     * 7c.4: two autosaves asked for back to back. Held, the second replaces
     * the first before anything is written: one write. Not held, the writer
     * may have begun the first: one write or two, never two at once, and the
     * file is the second in both. After each the save and the history are
     * read back whole.
     */
    static void oneAtATimeAlwaysWhole(Game city, GameFiles files) {
        System.out.println("\n--- the autosave off the clock: back to back, one at a time, always whole ---");

        SaveWriter writer = SaveWriter.shared();
        city.awaitSaves();

        int writesBefore = writer.writes();
        writer.hold();
        city.autosave("a");
        city.autosave("b");
        writer.release();
        city.awaitSaves();
        assertEquals("held, two autosaves asked for write once", writer.writes() - writesBefore, 1);
        assertEquals("...and the file holds the second", autosaveName(files), "Autosave - b");
        wholeOnDisk("held", city, files);

        writesBefore = writer.writes();
        city.autosave("c");
        city.autosave("d");
        city.awaitSaves();
        int wrote = writer.writes() - writesBefore;
        assertTrue("not held, two autosaves asked for write once or twice (" + wrote + ")", wrote == 1 || wrote == 2);
        assertEquals("...and the file holds the second", autosaveName(files), "Autosave - d");
        assertEquals("...and the writer never wrote two at once, in the whole run", writer.mostAtOnce(), 1);
        wholeOnDisk("not held", city, files);
    }

    /**
     * 7c.5: a load, a numbered save, a skip and Quit each wait for an
     * autosave still in the writer. Each case holds the writer, asks for an
     * autosave, and starts a thread that lets it go 300 ms later; the case
     * then runs on this thread. One that did not wait would see the slot
     * before the held autosave, or return before the release.
     */
    static void theWaits(Game city, GameFiles files) throws Exception {
        System.out.println("\n--- the autosave off the clock: a load, a numbered save, a skip and Quit each wait for it ---");

        SaveWriter writer = SaveWriter.shared();
        final long letGoAfterMs = 300;
        AtomicLong releasedAt = new AtomicLong();
        city.awaitSaves();

        // The autosave on disk holds an earlier month than the held one, so the two are told apart by month as well as by name.
        int diskMonth = files.readHeader(GameFiles.AUTOSAVE_SLOT).getMonth();
        quietly(() -> {
            for (int i = 0; i < 3; i++) city.simulateMonths(1);
        });
        assertTrue("fixture: the autosave on disk (month " + diskMonth + ") is earlier than the city (month " + city.getMonth() + ")", diskMonth < city.getMonth());

        /* ---- a load ---- */
        Game fresh = new Game(files);      // built first: its construction is not what is waited on
        releasedAt.set(0);
        writer.hold();
        city.autosave("7c.5 load");
        assertEquals("fixture: the held autosave is not on disk yet", files.readHeader(GameFiles.AUTOSAVE_SLOT).getMonth(), diskMonth);
        Thread letGo = releaseLater(letGoAfterMs, releasedAt);
        fresh.loadGameSave(GameFiles.AUTOSAVE_SLOT);
        long returnedAt = System.nanoTime();
        letGo.join();
        assertTrue("a load straight after an autosave returns after the writer was released", releasedAt.get() != 0 && returnedAt >= releasedAt.get());
        assertEquals("...and loads that autosave: its month", fresh.getMonth(), city.getMonth());
        assertEquals("...its name", files.readHeader(GameFiles.AUTOSAVE_SLOT).getSlotName(), "Autosave - 7c.5 load");
        assertEquals("...with no failure", fresh.getLoadFailure(), null);

        /* ---- a numbered save ---- */
        releasedAt.set(0);
        writer.hold();
        city.autosave("7c.5 numbered");
        assertEquals("fixture: the held autosave is not on disk yet", autosaveName(files), "Autosave - 7c.5 load");
        letGo = releaseLater(letGoAfterMs, releasedAt);
        GameFiles.Result numbered = city.saveGame(4, "7c.5");
        returnedAt = System.nanoTime();
        letGo.join();
        assertTrue("a numbered save returns after the writer was released", releasedAt.get() != 0 && returnedAt >= releasedAt.get());
        assertTrue("...slot 4 is saved", numbered.ok && "7c.5".equals(files.readHeader(4).getSlotName()));
        assertEquals("...and the held autosave is on disk", autosaveName(files), "Autosave - 7c.5 numbered");

        /* ---- a skip ---- */
        // The save clock stands still and a skip's counter starts at its own autosave: no month of the
        // three is due, so the only writes are the held autosave and the skip's own.
        releasedAt.set(0);
        int writesBefore = writer.writes();
        writer.hold();
        city.autosave("7c.5 skip");
        letGo = releaseLater(letGoAfterMs, releasedAt);
        quietly(() -> city.simulateMonths(3));
        returnedAt = System.nanoTime();
        letGo.join();
        assertTrue("a skip returns after the writer was released", releasedAt.get() != 0 && returnedAt >= releasedAt.get());
        assertEquals("...the held autosave and the skip's own were both written (without the wait the skip's replaces the held)",
                writer.writes() - writesBefore, 2);
        assertEquals("...and the file holds the skip's", autosaveName(files), "Autosave - before skipping 3 months");

        /* ---- Quit ---- */
        releasedAt.set(0);
        writer.hold();
        city.autosave("7c.5 before quit");
        letGo = releaseLater(letGoAfterMs, releasedAt);
        city.saveForQuit();
        returnedAt = System.nanoTime();
        letGo.join();
        assertTrue("Quit returns after the writer was released", releasedAt.get() != 0 && returnedAt >= releasedAt.get());
        assertEquals("...and its autosave is on disk", autosaveName(files), "Autosave - on quit");
        assertEquals("...at the city's month", files.readHeader(GameFiles.AUTOSAVE_SLOT).getMonth(), city.getMonth());
    }

    /**
     * 7c.6: a write that fails. The autosave's .tmp - the file GameFiles.write
     * streams into - is a folder with a file in it, which no write can open
     * (a read-only folder would not stop one on Windows). The failure is
     * reported on this thread, the autosave on disk is still the one before,
     * whole, and the next autosave, with the obstacle gone, writes.
     */
    static void aWriteThatFails(Game city, GameFiles files) throws Exception {
        System.out.println("\n--- the autosave off the clock: a write that fails is reported, and the next one writes ---");

        city.awaitSaves();
        Path target = files.saveFile(GameFiles.AUTOSAVE_SLOT);
        Path obstacle = target.resolveSibling(target.getFileName() + ".tmp");
        Files.createDirectories(obstacle);
        Files.writeString(obstacle.resolve("in-the-way.txt"), "a folder with something in it cannot be replaced by a file");
        assertTrue("fixture: the autosave's .tmp is a folder with a file in it", Files.isDirectory(obstacle) && Files.exists(obstacle.resolve("in-the-way.txt")));
        String nameBefore = autosaveName(files);

        java.io.ByteArrayOutputStream captured = new java.io.ByteArrayOutputStream();
        PrintStream real = System.out;
        try {
            System.setOut(new PrintStream(captured, true, java.nio.charset.StandardCharsets.UTF_8));
            city.autosave("7c.6 fails");
            city.awaitSaves();
        } finally {
            System.setOut(real);
        }
        String said = captured.toString(java.nio.charset.StandardCharsets.UTF_8);

        GameFiles.Result result = city.getLastSaveResult();
        assertTrue("the failed write is the last save result, not ok", result != null && !result.ok);
        assertTrue("...naming the autosave file", result != null && target.equals(result.file));
        assertTrue("...and the calling thread said \"Autosave failed:\"", said.contains("Autosave failed:"));
        assertEquals("the autosave on disk is still the one before", autosaveName(files), nameBefore);
        wholeOnDisk("after the failure", city, files);

        try (var walk = Files.walk(obstacle)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        }
        assertTrue("fixture: the obstacle is gone", !Files.exists(obstacle));

        city.autosave("7c.6 writes");
        city.awaitSaves();
        assertTrue("with the obstacle gone, the next autosave writes", city.getLastSaveResult() != null && city.getLastSaveResult().ok);
        assertEquals("...and the file holds it", autosaveName(files), "Autosave - 7c.6 writes");
        wholeOnDisk("after the next", city, files);
    }

    /**
     * 7c.7: the floor of real time. On the 7c city, with a clock this check
     * moves: after an autosave, 4 * AUTOSAVE_MONTHS months with the clock
     * still write nothing, each due month starting the counter again; with
     * the clock just short of the floor the next due month writes nothing;
     * past it, the next writes exactly one, at its month. Then the real
     * clock, on a young city whose months are fast: five due months in far
     * less than the floor write the first and none after it.
     */
    static void theFloor(Game city, GameFiles files, long[] now) throws Exception {
        System.out.println("\n--- the autosave off the clock: the floor of real time ---");

        SaveWriter writer = SaveWriter.shared();
        city.awaitSaves();
        city.autosave("7c.7 first");
        city.awaitSaves();
        final long base = now[0];      // the clock as the autosave read it: the floor counts from here
        int writesBefore = writer.writes();

        boolean eachDueCounted = true, eachStartedAgain = true;
        for (int due = 0; due < 4; due++) {
            for (int i = 0; i < Game.AUTOSAVE_MONTHS; i++) {
                if (i == Game.AUTOSAVE_MONTHS - 1) eachDueCounted &= city.getMonthsUntilAutosave() == 1;
                step(city);
            }
            eachStartedAgain &= city.getMonthsUntilAutosave() == Game.AUTOSAVE_MONTHS;
        }
        city.awaitSaves();
        assertTrue("fixture: each of the " + 4 * Game.AUTOSAVE_MONTHS + " months was counted, the last of each twelve due", eachDueCounted);
        assertEquals("with the clock still, " + 4 * Game.AUTOSAVE_MONTHS + " months write no autosave", writer.writes() - writesBefore, 0);
        assertTrue("...each due month started its counter again, at " + Game.AUTOSAVE_MONTHS, eachStartedAgain);

        now[0] = base + floorNanos() - 1_000_000_000L;
        for (int i = 0; i < Game.AUTOSAVE_MONTHS; i++) step(city);
        city.awaitSaves();
        assertEquals("one second short of the floor, the next due month writes nothing", writer.writes() - writesBefore, 0);
        assertEquals("...and its counter starts again", city.getMonthsUntilAutosave(), Game.AUTOSAVE_MONTHS);

        now[0] = base + floorNanos() + 1_000_000_000L;
        for (int i = 0; i < Game.AUTOSAVE_MONTHS; i++) step(city);
        city.awaitSaves();
        assertEquals("one second past the floor, the next due month writes exactly one", writer.writes() - writesBefore, 1);
        assertEquals("...at its month", files.readHeader(GameFiles.AUTOSAVE_SLOT).getMonth(), city.getMonth());
        assertEquals("...named for it", autosaveName(files), "Autosave - month " + city.getMonth());

        /* ---- the real clock ---- */
        Path root = TempDirs.create("floorclock");
        try {
            GameFiles youngFiles = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
            Game young = new Game(youngFiles);
            young.run();
            young.buildStack(template(young, "House"), 10, false);
            int startMonth = young.getMonth();
            int before = writer.writes();

            long began = System.nanoTime();
            for (int i = 0; i < 5 * Game.AUTOSAVE_MONTHS; i++) step(young);
            double seconds = (System.nanoTime() - began) / 1e9;
            young.awaitSaves();

            boolean premise = seconds < SaveWriter.FLOOR_SECONDS;
            System.out.printf("      %d months on the real clock took %.2f s of real time (the floor is %.0f s)%n",
                    5 * Game.AUTOSAVE_MONTHS, seconds, SaveWriter.FLOOR_SECONDS);
            assertTrue("fixture: the five due months came inside one floor of real time"
                    + (premise ? "" : " - they did not, so the count below proves nothing"), premise);
            assertEquals("on the real clock, " + 5 * Game.AUTOSAVE_MONTHS + " months wrote exactly one autosave",
                    writer.writes() - before, premise ? 1 : -1);
            SaveHeader header = youngFiles.readHeader(GameFiles.AUTOSAVE_SLOT);
            assertTrue("...the first due month's, month " + (startMonth + Game.AUTOSAVE_MONTHS) + ", none of the four after it",
                    header != null && header.getMonth() == startMonth + Game.AUTOSAVE_MONTHS);
        } finally {
            cleanUp(root);
        }
    }

    /**
     * The playtest's city, founded and played to `months`, on `files` (a scratch
     * folder): the founding, the first builds, the schools and the advice, as
     * LongPlaytest plays them, with its map made so the save carries one.
     * 7c's fixture, and the one the rest of 7c grows its city with. The
     * playtest's printing is silenced while it plays and restored after.
     */
    static Game grownCity(GameFiles files, int months) {
        PrintStream real = System.out, wasOut = LongPlaytest.out;
        PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());
        LongPlaytest.out = quiet;
        System.setOut(quiet);
        try {
            LongPlaytest.resetTheFloat();
            Game g = new Game(files, LongPlaytest.founding());
            g.run();
            LongPlaytest.childcareWhenNeeded(g);
            g.getDebtManager().setAutopilot(LongPlaytest.AUTOPILOT);
            g.setRolloverMode(LongPlaytest.ROLLOVER);
            g.getCityMap();
            g.setRescueMode(LongPlaytest.RESCUE_AUTO ? TreasuryFund.RescueMode.AUTOMATIC : TreasuryFund.RescueMode.BUTTON);
            g.setFundDial(LongPlaytest.FUND_DIAL);
            LongPlaytest.seedMarket(g, 0);
            LongPlaytest.villageBuild(g, "House", 40);
            LongPlaytest.villageBuild(g, "Convenience Store", 3);
            LongPlaytest.villageBuild(g, "Mixed Farm", 2);
            LongPlaytest.run(g, 3);
            LongPlaytest.villageBuild(g, "House", 20);
            LongPlaytest.run(g, 4);
            LongPlaytest.villageBuild(g, "Convenience Store", 2);
            LongPlaytest.villageBuild(g, "Construction Depot", 1);
            LongPlaytest.run(g, 5);
            LongPlaytest.advise(g);
            LongPlaytest.run(g, 6);
            LongPlaytest.ensureSchools(g);
            // Then the playtest's rhythm: a stretch, the schools seen to, the moves one a month, two months after.
            while (g.getMonth() < months) {
                LongPlaytest.run(g, Math.min(12, Math.max(1, months - g.getMonth())));
                LongPlaytest.ensureSchools(g);
                LongPlaytest.childcareWhenNeeded(g);
                for (int move = 0; move < LongPlaytest.movesPerLook(); move++) {
                    if (LongPlaytest.advise(g) == null) break;
                    LongPlaytest.run(g, 1);
                }
                LongPlaytest.run(g, 2);
            }
            return g;
        } finally {
            LongPlaytest.out = wasOut;
            System.setOut(real);
        }
    }

    /**
     * What Gson would write of an object, as sets of identities: every object
     * in it that someone could still change (a list, a map, an array that is
     * not empty, a model object with a non-final field), each with the path it
     * was first reached by, and every object of a class the walk cannot see
     * into. Two snapshots whose walks share no identity share nothing a month
     * could change.
     *
     * <p>Gson's view of a ham.citybuildersim object is its declared fields up
     * the superclass chain, except static, transient and synthetic ones. Not
     * recorded, not descended: null, strings, boxed numbers, enums, classes,
     * empty arrays, Gson's JsonPrimitive and JsonNull, and a model object
     * whose every field is final and holds only such things (a record of
     * numbers). Recorded and descended: arrays (a primitive array recorded and
     * not descended), the JDK's ordinary lists, queues, maps and sets, and
     * Gson's JsonArray and JsonObject. Not recorded but descended: the JDK's
     * immutable collections (List.of, Collections.emptyList). Anything else - an
     * unmodifiable view, Arrays.asList, a subList, a type from outside the
     * model - goes to {@link #opaque}: a view of a live list is exactly the
     * race this hunts, and the fix for it is a copy at the setter.
     */
    static final class Walk {

        /** Every mutable object reached, by identity, with the path it was first reached by. */
        final IdentityHashMap<Object, String> mutable = new IdentityHashMap<>();

        /** Each object of a class the walk cannot see into: its path and its class. */
        final List<String> opaque = new ArrayList<>();

        /** The containers already walked (mutable or not): a cycle, or a shared one, is walked once. */
        private final IdentityHashMap<Object, Boolean> walked = new IdentityHashMap<>();

        /** What isImmutable() has settled for a model object: true while it is being settled, which is how a cycle ends. */
        private final IdentityHashMap<Object, Boolean> settled = new IdentityHashMap<>();

        static Walk of(Object root, String name) {
            Walk walk = new Walk();
            walk.visit(root, name);
            return walk;
        }

        /** The paths, in the first walk, of every mutable object in both: "path (and where the second reached it): class". */
        static List<String> shared(Walk first, Walk second) {
            List<String> paths = new ArrayList<>();
            for (Map.Entry<Object, String> e : first.mutable.entrySet()) {
                String other = second.mutable.get(e.getKey());
                if (other != null) {
                    paths.add(e.getValue() + (other.equals(e.getValue()) ? "" : "  (also " + other + ")")
                            + " : " + e.getKey().getClass().getSimpleName());
                }
            }
            java.util.Collections.sort(paths);
            return paths;
        }

        private static boolean isLeaf(Object o) {
            return o == null || o instanceof String || o instanceof Integer || o instanceof Long
                    || o instanceof Double || o instanceof Float || o instanceof Short || o instanceof Byte
                    || o instanceof Boolean || o instanceof Character || o instanceof BigInteger
                    || o instanceof BigDecimal || o instanceof Enum || o instanceof Class
                    || o instanceof JsonPrimitive || o instanceof JsonNull;
        }

        private static boolean isModel(Class<?> c) {
            return c.getName().startsWith("ham.citybuildersim.");
        }

        /** The JDK's collections that Gson writes and anyone can still change. */
        private static boolean isMutableJdk(Class<?> c) {
            return c == ArrayList.class || c == java.util.LinkedList.class || c == java.util.ArrayDeque.class
                    || c == java.util.HashMap.class || c == java.util.LinkedHashMap.class
                    || c == java.util.TreeMap.class || c == java.util.EnumMap.class
                    || c == java.util.HashSet.class || c == java.util.LinkedHashSet.class
                    || c == java.util.TreeSet.class;
        }

        /** List.of and its kin, and Collections' empty and singleton collections: nobody can change them. */
        private static boolean isImmutableJdk(Class<?> c) {
            String n = c.getName();
            return n.startsWith("java.util.ImmutableCollections$")
                    || n.equals("java.util.Collections$EmptyList") || n.equals("java.util.Collections$EmptyMap")
                    || n.equals("java.util.Collections$EmptySet") || n.equals("java.util.Collections$SingletonList")
                    || n.equals("java.util.Collections$SingletonMap") || n.equals("java.util.Collections$SingletonSet");
        }

        /** Gson's fields of a model object: declared, up the chain, not static, transient or synthetic. */
        private static List<Field> fieldsOf(Class<?> c) {
            List<Field> fields = new ArrayList<>();
            for (Class<?> k = c; k != null && k != Object.class && isModel(k); k = k.getSuperclass()) {
                for (Field f : k.getDeclaredFields()) {
                    int m = f.getModifiers();
                    if (Modifier.isStatic(m) || Modifier.isTransient(m) || f.isSynthetic()) continue;
                    f.setAccessible(true);
                    fields.add(f);
                }
            }
            return fields;
        }

        private static Object get(Field f, Object o) {
            try {
                return f.get(o);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(f.toString(), e);
            }
        }

        /** True when nobody can change this value: the same test as the walk's, without recording anything. */
        private boolean isImmutable(Object o) {
            if (isLeaf(o)) return true;
            Class<?> c = o.getClass();
            if (c.isArray()) return Array.getLength(o) == 0;
            if (isImmutableJdk(c)) {
                if (o instanceof Map<?, ?> map) {
                    for (Map.Entry<?, ?> e : map.entrySet()) if (!isImmutable(e.getKey()) || !isImmutable(e.getValue())) return false;
                } else {
                    for (Object element : (Iterable<?>) o) if (!isImmutable(element)) return false;
                }
                return true;
            }
            if (!isModel(c)) return false;
            Boolean known = settled.get(o);
            if (known != null) return known;
            settled.put(o, true);
            boolean immutable = true;
            for (Field f : fieldsOf(c)) {
                if (!Modifier.isFinal(f.getModifiers()) || !isImmutable(get(f, o))) { immutable = false; break; }
            }
            settled.put(o, immutable);
            return immutable;
        }

        /** Records `o` under `path`; false when it was reached before (its first path stands, and it is not walked again). */
        private boolean record(Object o, String path) {
            if (walked.put(o, true) != null) return false;
            mutable.put(o, path);
            return true;
        }

        private void visit(Object o, String path) {
            if (isLeaf(o)) return;
            Class<?> c = o.getClass();

            if (c.isArray()) {
                if (Array.getLength(o) == 0 || !record(o, path)) return;
                if (!c.getComponentType().isPrimitive()) {
                    Object[] elements = (Object[]) o;
                    for (int i = 0; i < elements.length; i++) {
                        if (!isLeaf(elements[i])) visit(elements[i], path + "[" + i + "]");
                    }
                }
            } else if (o instanceof JsonArray array) {
                if (!record(o, path)) return;
                int i = 0;
                for (JsonElement element : array) {
                    if (!isLeaf(element)) visit(element, path + "[" + i + "]");
                    i++;
                }
            } else if (o instanceof JsonObject object) {
                if (!record(o, path)) return;
                for (Map.Entry<String, JsonElement> e : object.entrySet()) {
                    if (!isLeaf(e.getValue())) visit(e.getValue(), path + "." + e.getKey());
                }
            } else if (isMutableJdk(c) || isImmutableJdk(c)) {
                if (isMutableJdk(c) ? !record(o, path) : walked.put(o, true) != null) return;
                if (o instanceof Map<?, ?> map) {
                    for (Map.Entry<?, ?> e : map.entrySet()) {
                        if (!isLeaf(e.getKey())) visit(e.getKey(), path + ".key");
                        if (!isLeaf(e.getValue())) visit(e.getValue(), path + "{" + e.getKey() + "}");
                    }
                } else {
                    int i = 0;
                    for (Object element : (Iterable<?>) o) {
                        if (!isLeaf(element)) visit(element, path + "[" + i + "]");
                        i++;
                    }
                }
            } else if (isModel(c)) {
                if (isImmutable(o) || !record(o, path)) return;
                for (Class<?> k = c.getSuperclass(); k != null && k != Object.class; k = k.getSuperclass()) {
                    if (!isModel(k)) opaque.add(path + " : " + c.getName() + " extends " + k.getName());
                }
                for (Field f : fieldsOf(c)) {
                    Object value = get(f, o);
                    if (!isLeaf(value)) visit(value, path + "." + f.getName());
                }
            } else {
                opaque.add(path + " : " + c.getName());
            }
        }
    }

    /** The autosave as `city` stands now: wholeMonth(which, city, files, city.getMonth()). */
    static void wholeMonth(String which, Game city, GameFiles files) {
        wholeMonth(which, city, files, city.getMonth());
    }

    /**
     * Loads the autosave and plays two single months on (single, so neither
     * writes it again): it carries `month`, its history ends at that month,
     * and after the two its history runs one month at a time to the city's.
     */
    static void wholeMonth(String which, Game city, GameFiles files, int month) {
        Game back = new Game(files);
        back.loadGameSave(GameFiles.AUTOSAVE_SLOT);
        java.util.List<Integer> axis = back.getHistorySave().getMonth();
        assertEquals(which + ": it holds month " + month, back.getMonth(), month);
        assertEquals("...and its history ends at it", axis.isEmpty() ? null : axis.get(axis.size() - 1), month);
        back.simulateMonths(1);
        back.simulateMonths(1);
        axis = back.getHistorySave().getMonth();
        int gaps = 0, twice = 0;
        for (int i = 1; i < axis.size(); i++) {
            int step = axis.get(i) - axis.get(i - 1);
            if (step > 1) gaps++;
            if (step < 1) twice++;
        }
        assertEquals("...two months on, its history ends at the city's", axis.get(axis.size() - 1), back.getMonth());
        assertTrue("...with no month missing (" + gaps + ") and none twice (" + twice + ")", gaps == 0 && twice == 0);
    }

    static void cleanUp(Path root) {
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }
    }
}
