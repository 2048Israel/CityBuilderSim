package ham.citybuildersim;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * What the game does when something is already broken.
 *
 * Every other harness checks that the game works. This one checks that it fails
 * legibly - which matters more in a shipped build than in development, because
 * the developer has a console and the player has a window that either explains
 * itself or does not.
 *
 * THE BUG THIS WAS WRITTEN FOR
 *
 * A truncated save threw JsonSyntaxException out of loadGame(). That is a
 * RuntimeException, so it went straight through the catch (IOException) sitting
 * right there. readHeader() failed safely and the menu labelled the slot
 * "Empty" - but slotIsEmpty() asks whether the FILE exists, and it did, so the
 * Load button stayed enabled.
 *
 * The player clicked Load on a slot that said Empty, and the game did nothing.
 * No message, no error, no load: the exception reached the FX thread's default
 * handler and a stderr that does not exist in a packaged build.
 */
public class RobustnessCheck {

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

        Path root = Files.createTempDirectory("robustness");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));

        Game game = new Game(files);
        game.run();
        game.buildStack(template(game, "House"), 30, false);
        game.buildStack(template(game, "Convience Store"), 2, false);
        game.simulateMonths(20);
        assertTrue("a good save to work from", game.saveGame(1, "good").ok);

        String good = Files.readString(files.saveFile(1));

        /* ============ 1. a save cut in half ============ */
        System.out.println("\n--- a save that was cut in half ---");

        Files.createDirectories(files.savesDirectory());
        Files.writeString(files.saveFile(2), good.substring(0, good.length() / 2));

        assertTrue("the file is there", !files.slotIsEmpty(2));
        assertTrue("...and is NOT reported as empty", !files.slotIsEmpty(2));
        assertTrue("...it is reported as unreadable", files.slotIsUnreadable(2));
        assertTrue("...and therefore not loadable", !files.slotIsLoadable(2));

        Game victim = new Game(files);
        String failure = null;
        try {
            victim.loadGameSave(2);
            failure = victim.getLoadFailure();
        } catch (Throwable t) {
            fails++;
            System.out.println("loading a corrupt save THREW: " + t + "  FAIL");
        }

        assertTrue("loading it does not throw", failure != null || fails == 0);
        assertTrue("...it reports a failure instead", failure != null);
        assertEquals("...and nothing was applied", victim.getMonth(), 1);

        /* ============ 2. a file that is not JSON at all ============ */
        System.out.println("\n--- and one that is not a save at all ---");

        Files.writeString(files.saveFile(3), "this is not json, it is a shopping list");

        assertTrue("also unreadable", files.slotIsUnreadable(3));
        assertTrue("also not loadable", !files.slotIsLoadable(3));

        Game victim2 = new Game(files);
        victim2.loadGameSave(3);
        assertTrue("reports a failure", victim2.getLoadFailure() != null);
        assertEquals("nothing applied", victim2.getMonth(), 1);

        /* ============ 3. an empty file, and an empty slot ============ */
        System.out.println("\n--- the edges around empty ---");

        Files.writeString(files.saveFile(4), "");
        assertTrue("a zero-byte file is unreadable, not empty",
                files.slotIsUnreadable(4) && !files.slotIsEmpty(4));

        assertTrue("a slot with no file IS empty", files.slotIsEmpty(5));
        assertTrue("...and is not 'unreadable'", !files.slotIsUnreadable(5));
        assertTrue("...and is not loadable either", !files.slotIsLoadable(5));

        Game victim3 = new Game(files);
        victim3.loadGameSave(5);
        assertEquals("loading an empty slot changes nothing", victim3.getMonth(), 1);

        /* ============ 4. the good save still loads ============ */
        System.out.println("\n--- and none of that broke the good one ---");

        assertTrue("slot 1 is loadable", files.slotIsLoadable(1));

        Game recovered = new Game(files);
        recovered.loadGameSave(1);
        assertEquals("it loaded", recovered.getLoadFailure(), null);
        assertTrue("with the city in it", recovered.getMonth() > 1);
        assertTrue("...and its people", recovered.getPopulationManager().getPopulation() > 0);

        // The corrupt files are still on disk. Nothing deleted a player's file
        // just because it could not read it - it might be recoverable by hand,
        // and it is not the game's to throw away.
        assertTrue("the damaged files were not deleted",
                Files.exists(files.saveFile(2)) && Files.exists(files.saveFile(3)));

        /* ============ 5. a save from a newer build ============ */
        System.out.println("\n--- and one from the future ---");

        Files.writeString(files.saveFile(6), good.replace(
                "\"saveFormat\": " + GameVersion.SAVE_FORMAT,
                "\"saveFormat\": " + (GameVersion.SAVE_FORMAT + 5)));

        assertTrue("it reads as a file", !files.slotIsEmpty(6));
        assertTrue("...its header parses fine", files.readHeader(6) != null);
        assertTrue("...but it is not loadable", !files.slotIsLoadable(6));

        Game future = new Game(files);
        future.loadGameSave(6);
        assertTrue("and the loader says why", future.getLoadFailure() != null);
        assertEquals("nothing applied", future.getMonth(), 1);

        /* ============ 5b. a save from an OLDER build ============ */
        System.out.println("\n--- and one from the past, which is the common case ---");

        /*
         * The direction nothing was testing.
         *
         * Every pass adds fields to the save, and every one of them has to have
         * a sensible value when an older file simply doesn't contain it. That
         * is not automatic: Gson can construct an object without running its
         * field initializers, in which case a "-1 means absent" sentinel
         * silently arrives as 0 - and 0 is a legal workforce, so the city would
         * come back with nobody working and every fill rate at zero. It happens
         * to work here; this is what keeps it working.
         *
         * Built by stripping the newest fields out of a real save rather than
         * by checking in an old file, so it keeps testing the current loader
         * against the current save rather than against a fossil.
         */
        com.google.gson.JsonObject old4 =
                com.google.gson.JsonParser.parseString(good).getAsJsonObject();

        for (String field : new String[] {
                "workforce", "commercialReport", "industrialReport",
                "heavyIndustryReport", "energyRatioBasis", "waterRatioBasis",
                "roadRatioBasis", "hasRatioBasis" }) {
            old4.remove(field);
        }
        old4.addProperty("saveFormat", 4);

        Files.writeString(files.saveFile(7), old4.toString());

        assertTrue("an older save is loadable", files.slotIsLoadable(7));

        Game older = new Game(files);
        older.loadGameSave(7);

        assertEquals("it loads without complaint", older.getLoadFailure(), null);
        assertTrue("with its city", older.getMonth() > 1);
        assertTrue("...and its people", older.getPopulationManager().getPopulation() > 0);

        // The specific trap: a missing sentinel arriving as 0 rather than -1.
        assertTrue("a missing workforce is recomputed, not left at zero",
                older.getPopulationManager().getWorkforce() > 0);
        assertEquals("...to half the population, which is the documented fallback",
                older.getPopulationManager().getWorkforce(),
                (int) (older.getPopulationManager().getPopulation() * .5));

        // A statement rebuilt rather than restored is allowed to differ from
        // the one the old save was taken with - that is the bug those saves
        // already had. What is not allowed is for it to be empty or absurd.
        assertTrue("its income statement is rebuilt, not blank", older.getIncome() > 0);

        older.simulateMonths(3);
        assertTrue("and it keeps running", older.getPopulationManager().getPopulation() > 0);

        /* ============ 5c. a city whose numbers have overflowed ============ */
        System.out.println("\n--- and a city that has run out of double ---");

        /*
         * Gson refuses to serialise NaN or Infinity - it throws rather than
         * writing them - and that throw used to escape Game.save() entirely.
         * One overflowed number anywhere in the city took the whole process
         * down, from the AUTOSAVE, in the middle of a skip, where the player is
         * not even looking. The city itself was still perfectly playable; it
         * just could not be written down.
         *
         * Found by the long playtest. An insolvent city rolls its emergency
         * T-Bills at 25% every four months, which is exponential, so a few
         * centuries of it leaves what a double can hold. The spiral is a
         * separate, open design question. This is not one: whatever state the
         * city reaches, failing to save is a message, not a crash.
         */
        Game overflowed = new Game(files);
        overflowed.run();
        overflowed.buildStack(template(overflowed, "House"), 30, false);
        overflowed.simulateMonths(6);

        assertTrue("a healthy city saves fine", overflowed.saveGame(8, "before").ok);

        overflowed.subtractCash(Double.MAX_VALUE);
        overflowed.subtractCash(Double.MAX_VALUE);

        GameFiles.Result overflowResult = null;
        try {
            overflowResult = overflowed.saveGame(9, "overflowed");
        } catch (Throwable t) {
            fails++;
            System.out.println("saving an overflowed city THREW: " + t + "  FAIL");
        }

        assertTrue("saving it does not throw", overflowResult != null);
        assertTrue("...it reports a failure instead",
                overflowResult != null && !overflowResult.ok);
        assertTrue("...and names the field, so the log is a bug report",
                overflowResult != null && overflowResult.error != null
                        && overflowResult.error.contains("cash"));

        // The two things that matter after a failed save: the game is still
        // running, and the last good save is still there.
        overflowed.simulateMonths(1);
        assertTrue("the game is still alive afterwards", overflowed.getMonth() > 1);
        assertTrue("...and the earlier good save is untouched", files.slotIsLoadable(8));

        Game beforeOverflow = new Game(files);
        beforeOverflow.loadGameSave(8);
        assertEquals("...and still loads", beforeOverflow.getLoadFailure(), null);

        /* ============ 5d. a parameter that used to do nothing ============ */
        System.out.println("\n--- buildStack's noConstruction flag (backlog item 24) ---");

        /*
         * buildStack() has always taken this flag and processBuildOrder() always
         * ignored it, hardcoding false. Nothing passed true, so nothing broke -
         * and that is what made it worth wiring rather than leaving: the first
         * caller to trust it would have got a queued building, no error, and no
         * way to tell.
         *
         * The two orders below are identical except for the flag, so anything
         * that differs is the flag doing its job.
         */
        Game flagged = new Game(files);
        flagged.run();
        flagged.getLandManager().setOwnedSqFt(4_000_000);

        BuildingsTemplate house = template(flagged, "House");
        ConstructionHandler crew = flagged.getServicesManager().getConstructionHandler();

        int standingBefore = flagged.getBuildingManager().getQuantity(house.getId());
        double crewBacklogBefore = crew.getBacklogPoints();
        double crewUnearnedBefore = crew.getUnearnedRevenue();
        double cashBefore = flagged.getCash();

        flagged.buildStack(house, 10, true);

        assertTrue("an instant build is standing the same month",
                flagged.getBuildingManager().getQuantity(house.getId())
                        == standingBefore + 10);
        assertTrue("...and nothing was put in the construction queue",
                Math.abs(crew.getBacklogPoints() - crewBacklogBefore) < 1e-9);
        assertTrue("...and the crews were not paid for work they did not do",
                Math.abs(crew.getUnearnedRevenue() - crewUnearnedBefore) < 1e-9);
        assertTrue("...and it cost the cash price and no materials",
                Math.abs((cashBefore - flagged.getCash())
                        - house.getCashCost() * 10) < 1e-9);

        // The ordinary order, for contrast: queued, not standing.
        int standingNow = flagged.getBuildingManager().getQuantity(house.getId());
        flagged.buildStack(house, 10, false);

        assertTrue("an ordinary order is NOT standing yet",
                flagged.getBuildingManager().getQuantity(house.getId()) == standingNow);
        assertTrue("...and it did go into the queue",
                crew.getBacklogPoints() > crewBacklogBefore);

        // And the flag does not skip the checks - land is still land.
        flagged.getLandManager().setOwnedSqFt(flagged.getLandManager().getAllocatedSqFt());
        assertTrue("an instant build still needs somewhere to stand",
                flagged.buildStack(house, 10, true) == Game.BuildResult.NO_LAND);

        /* ============ 6. the log ============ */
        System.out.println("\n--- the log ---");

        Path logRoot = Files.createTempDirectory("logcheck");
        GameFiles logFiles = new GameFiles(logRoot.resolve("data"),
                logRoot.resolve("no-legacy"));

        // Not started here - GameLog replaces System.out process-wide, and a
        // test that redirects the streams it is printing its own results to is
        // a test that cannot report a failure. What CAN be checked is the file
        // layout it will use and that its helpers never throw.
        assertEquals("the log lives beside the saves",
                logFiles.getDirectory().resolve(GameLog.LOG_FILE).getParent(),
                logFiles.getDirectory());
        assertTrue("the previous run's log has its own name",
                !GameLog.LOG_FILE.equals(GameLog.PREVIOUS_LOG_FILE));

        // failure() with a null cause must not become a second crash inside the
        // crash handler, which is the worst possible place for one.
        GameLog.failure("a deliberate test entry, with no exception", null);
        GameLog.failure("a deliberate test entry, with one",
                new IllegalStateException("expected - this is the robustness check"));
        GameLog.note("if these three lines are the last thing in a log, "
                + "RobustnessCheck wrote them");
        System.out.printf("%-58s %s%n", "logging helpers survive a null cause", "OK");

        cleanUp(root);
        cleanUp(logRoot);

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    static void cleanUp(Path root) {
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }
    }
}
