package ham.citybuildersim;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Verifies the slot system: ten saves plus an autosave, the version stamp, and
 * the labels the menu is drawn from.
 *
 * The thing this is really guarding is INDEPENDENCE. A save menu that shows ten
 * slots and quietly writes them all to the same file, or draws slot 3's label
 * from slot 7's city, is worse than a single save - it invites a player to
 * spread a hundred hours across ten slots that were never really there.
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

        Path root = Files.createTempDirectory("slotcheck");
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
        int popAt1 = game.getPopulationManager().getPopulation();

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

        Path autoRoot = Files.createTempDirectory("autocheck");
        GameFiles autoFiles = new GameFiles(autoRoot.resolve("data"),
                autoRoot.resolve("no-legacy"));

        Game auto = new Game(autoFiles);
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

    static void cleanUp(Path root) {
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }
    }
}
