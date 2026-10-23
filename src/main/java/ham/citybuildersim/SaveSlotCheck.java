package ham.citybuildersim;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
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
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

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
     * change. The subsections are numbered 7c.1, 7c.2... and each prints its
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

    /**
     * 7c.1: the walk. Two snapshots taken back to back on one grown city share
     * no mutable object (DataSave, and separately HistorySave), and no class
     * in them is one the walk cannot see into.
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
        } finally {
            cleanUp(root);
        }

        copiesOnFilledStates();
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
