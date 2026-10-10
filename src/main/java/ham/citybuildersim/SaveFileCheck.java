package ham.citybuildersim;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Verifies where saves go and how they are written.
 *
 * This is the only part of the game where a bug destroys something the player
 * cannot get back. Everything else can be re-simulated; a save written over
 * badly is a city that no longer exists. So the tests here are deliberately
 * mean: they fill the target with junk, point the writer at a path it cannot
 * write to, and check that in every case the file that was already on disk is
 * still exactly what it was.
 *
 * Path resolution is tested through the pure resolveDirectory() rather than the
 * real environment, because a chooser that reads System.getenv can only be
 * tested on the machine it is running on - which is precisely the machine where
 * a mistake in the other two branches would never show up.
 */
public class SaveFileCheck {

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

    /** Two readings of one month, which must not differ at all. */
    static void same(String label, double actual, double expected) {
        boolean ok = Math.abs(actual - expected) < 1e-9;
        if (!ok) {
            fails++;
            System.out.printf("%-58s FAIL  %,.6f != %,.6f%n", label, actual, expected);
        } else {
            System.out.printf("%-58s OK%n", label);
        }
    }

    /** Forward slashes, so the assertions read the same on any host. */
    static String flat(Path p) {
        return p.toString().replace('\\', '/');
    }

    public static void main(String[] args) throws Exception {

        /* ==================== 1. where it goes ==================== */
        System.out.println("--- the folder, per platform ---");

        Path win = GameFiles.resolveDirectory(
                "Windows 11", "/users/jerus/AppData/Roaming", null, "/users/jerus");
        assertEquals("Windows follows %APPDATA%",
                flat(win), "/users/jerus/AppData/Roaming/CityBuilderSim");

        // The variable is not always there - a stripped service environment, a
        // process started oddly. It must still land somewhere sane.
        Path winNoEnv = GameFiles.resolveDirectory(
                "Windows 10", null, null, "/users/jerus");
        assertEquals("...and falls back when it is missing",
                flat(winNoEnv), "/users/jerus/AppData/Roaming/CityBuilderSim");

        Path winBlank = GameFiles.resolveDirectory(
                "Windows 10", "   ", null, "/users/jerus");
        assertEquals("...blank counts as missing, not as a folder named nothing",
                flat(winBlank), "/users/jerus/AppData/Roaming/CityBuilderSim");

        Path mac = GameFiles.resolveDirectory(
                "Mac OS X", null, null, "/Users/jerus");
        assertEquals("macOS uses Application Support",
                flat(mac), "/Users/jerus/Library/Application Support/CityBuilderSim");

        Path linux = GameFiles.resolveDirectory(
                "Linux", null, null, "/home/jerus");
        assertEquals("Linux uses the XDG default",
                flat(linux), "/home/jerus/.local/share/CityBuilderSim");

        Path xdg = GameFiles.resolveDirectory(
                "Linux", null, "/home/jerus/.data", "/home/jerus");
        assertEquals("...and honours XDG_DATA_HOME when set",
                flat(xdg), "/home/jerus/.data/CityBuilderSim");

        // An unknown os.name must not throw. Treating it as Linux is the safe
        // guess: every remaining platform this could run on is unix-shaped.
        Path odd = GameFiles.resolveDirectory("Plan 9", null, null, "/home/jerus");
        assertTrue("an unrecognised platform still resolves",
                flat(odd).endsWith("/.local/share/CityBuilderSim"));

        Path noHome = GameFiles.resolveDirectory("Linux", null, null, null);
        assertTrue("no home directory still resolves rather than throwing",
                noHome != null && flat(noHome).endsWith("CityBuilderSim"));

        assertTrue("it is never the old folder",
                !flat(win).contains("YourGame") && !flat(linux).contains("YourGame"));

        /* ==================== 2. writing safely ==================== */
        System.out.println("\n--- writing ---");

        Path root = Files.createTempDirectory("savecheck");
        Path dir = root.resolve("data");
        Path legacy = root.resolve("YourGame");

        GameFiles files = new GameFiles(dir, legacy);

        assertEquals("save file", flat(files.saveFile(1)), flat(dir) + "/saves/slot-01.json");
        assertEquals("history file", flat(files.historyFile(1)), flat(dir) + "/saves/slot-01-history.json");

        // The folder does not exist yet. Writing has to create it.
        assertTrue("folder does not exist yet", !Files.exists(dir));

        GameFiles.Result first = files.write(files.saveFile(1), "{\"city\":1}");
        assertTrue("first write succeeds", first.ok);
        assertTrue("...and says where", first.message().contains("slot-01.json"));
        assertEquals("...and the contents are right",
                Files.readString(files.saveFile(1)), "{\"city\":1}");

        assertTrue("no .tmp left behind",
                !Files.exists(files.saveFile(1).resolveSibling("slot-01.json.tmp")));
        assertTrue("nothing to back up on the first write",
                !Files.exists(files.saveFile(1).resolveSibling("slot-01.json.bak")));

        GameFiles.Result second = files.write(files.saveFile(1), "{\"city\":2}");
        assertTrue("second write succeeds", second.ok);
        assertEquals("...the new city is on disk",
                Files.readString(files.saveFile(1)), "{\"city\":2}");
        assertEquals("...and the previous one survives as .bak",
                Files.readString(files.saveFile(1).resolveSibling("slot-01.json.bak")), "{\"city\":1}");

        /* ============ 3. a write that cannot possibly work ============ */
        System.out.println("\n--- when the disk says no ---");

        // A directory standing where the save file should be. Every write to it
        // fails, which is the closest thing to "disk full" that can be staged
        // reliably, and it exercises the same path.
        Path blockedDir = root.resolve("blocked");
        GameFiles blocked = new GameFiles(blockedDir, legacy);
        Files.createDirectories(blocked.saveFile(1));

        GameFiles.Result refused = blocked.write(blocked.saveFile(1), "{\"city\":3}");
        assertTrue("a failed write is reported as failed", !refused.ok);
        assertTrue("...with a reason attached",
                refused.error != null && !refused.error.isBlank());
        assertTrue("...and the message says so plainly",
                refused.message().startsWith("Could not save"));
        assertTrue("...and does not claim to have saved",
                !refused.message().contains("Saved to"));

        assertTrue("no .tmp litter after a failure",
                !Files.exists(blocked.saveFile(1).resolveSibling("slot-01.json.tmp")));

        // The point of the whole exercise: a failure must not have eaten the
        // save that was already there.
        assertEquals("the existing save is untouched by a later failure",
                Files.readString(files.saveFile(1)), "{\"city\":2}");

        /* ==================== 4. the old folder ==================== */
        System.out.println("\n--- bringing the old saves over ---");

        Path freshDir = root.resolve("fresh");
        Files.createDirectories(legacy);
        Files.writeString(legacy.resolve("save.json"), "{\"old\":true}");
        Files.writeString(legacy.resolve("history.json"), "{\"months\":12}");

        GameFiles fresh = new GameFiles(freshDir, legacy);
        List<String> copied = fresh.migrateLegacy();

        // Two hops now: the old home folder into the app folder's flat save,
        // and the flat save into Slot 1. Four copies, and the city ends up
        // where a player can actually find it.
        assertEquals("both files came over, both hops", copied.size(), 4);
        assertEquals("the save arrived in Slot 1",
                Files.readString(fresh.saveFile(1)), "{\"old\":true}");
        assertEquals("the history arrived with it",
                Files.readString(fresh.historyFile(1)), "{\"months\":12}");
        assertTrue("...and the intermediate flat save is left behind too",
                Files.exists(fresh.legacyFlatSave()));

        // The single most important assertion in this file. A migration that
        // moves is a migration that can lose a city if it half-runs.
        assertTrue("the originals are still there",
                Files.exists(legacy.resolve("save.json"))
                        && Files.exists(legacy.resolve("history.json")));
        assertEquals("...and unchanged",
                Files.readString(legacy.resolve("save.json")), "{\"old\":true}");

        // Running twice must not do anything the second time.
        assertEquals("running it again copies nothing", fresh.migrateLegacy().size(), 0);

        /* ============ 5. it never overwrites a newer save ============ */
        System.out.println("\n--- and does not clobber a city in progress ---");

        Path playedDir = root.resolve("played");
        GameFiles played = new GameFiles(playedDir, legacy);
        Files.createDirectories(played.savesDirectory());
        Files.writeString(played.saveFile(1), "{\"current\":true}");

        List<String> intoPlayed = played.migrateLegacy();

        assertEquals("the save being played is untouched",
                Files.readString(played.saveFile(1)), "{\"current\":true}");
        assertTrue("...and the old history still came over",
                Files.exists(played.historyFile(1)));
        assertTrue("...without the old save overwriting Slot 1",
                intoPlayed.stream().noneMatch(s -> s.equals("save into Slot 1")));

        // No old folder at all is the normal case for a new player.
        GameFiles noLegacy = new GameFiles(root.resolve("brandnew"),
                root.resolve("nothing-here"));
        assertEquals("no old folder, nothing to do", noLegacy.migrateLegacy().size(), 0);

        // A legacy folder that IS the save folder must not copy files onto
        // themselves. The first hop is skipped; the second still promotes the
        // flat save into Slot 1, which is right - that is where it belongs.
        GameFiles same = new GameFiles(legacy, legacy);
        List<String> intoSame = same.migrateLegacy();

        assertTrue("no file copied onto itself",
                intoSame.stream().noneMatch(s -> s.contains("old folder")));
        assertEquals("...but the flat save was promoted to Slot 1",
                Files.readString(same.saveFile(1)), "{\"old\":true}");
        assertEquals("...and the original is intact",
                Files.readString(legacy.resolve("save.json")), "{\"old\":true}");

        /* ==================== 6. a real round trip ==================== */
        System.out.println("\n--- an actual save, written and read back ---");

        Path tripDir = root.resolve("trip");
        GameFiles trip = new GameFiles(tripDir, root.resolve("no-legacy"));

        DataSave data = new DataSave();
        data.setBuildingNum(13);
        data.setCash(123456.75);
        data.setMonth(87);
        data.setPopulation(1904);
        data.setBuildingQuantity(0, 141);
        data.setHouseholdSavings(-2200.5);
        data.setLandOwned(4000000);
        data.setIncomeTaxRate(.15);
        data.setPropertyTaxRate(.015);
        // ...and how the city was founded (0.7.10): a name, money named by
        // hand, and figures no preset has, so nothing can pass by default -
        // and since 0.7.56 a world that is not the default's.
        Founding arden = Founding.custom("Arden", 62_500, 12_500, .02)
                .withCurrency(Currency.typed("Arden crown", "ARC")).withWorldSeed(987_654_321L);
        data.setFounding(arden);

        GameFiles.Result wrote = data.saveGame(trip, 1);
        assertTrue("DataSave wrote itself", wrote.ok);

        com.google.gson.Gson gson = new com.google.gson.Gson();
        DataSave read = gson.fromJson(Files.readString(trip.saveFile(1)), DataSave.class);

        assertEquals("cash survived", read.getCash(), 123456.75);
        assertEquals("month survived", read.getMonth(), 87);
        assertEquals("population survived", read.getPopulation(), 1904L);
        assertEquals("buildings survived", read.getBuildingQuantity(0), 141);
        assertEquals("household savings survived", read.getHouseholdSavings(), -2200.5);
        assertEquals("land survived", read.getLandOwned(), 4000000.0);
        assertEquals("tax rates survived", read.getPropertyTaxRate(), .015);
        Founding readBack = read.getFounding(.02);
        assertEquals("the city's name survived", readBack.getCityName(), "Arden");
        assertEquals("...its money, all five of its names", readBack.getCurrency(), arden.getCurrency());
        assertEquals("...the treasury it was founded with", readBack.getCash(), 62_500.0);
        assertEquals("...and the vault", readBack.getReserveUsd(), 12_500.0);
        assertEquals("...and the world's mean is handed back, not saved twice",
                readBack.getMeanInflation(), .02);
        assertEquals("...and the world it stands on, its seed (0.7.56)", readBack.getWorldSeed(), 987_654_321L);
        assertEquals("...saved under worldSeed", read.getWorldSeed(), 987_654_321L);

        // THE LAND ON THE WORLD (0.7.57): a city's land, its offers and what
        // it took out, written and read back field for field - a land office
        // that has bought an offer and lifted some of its ground.
        LandManager office = new LandManager();
        office.updateMarket(0);
        office.buyParcel(office.getMarket().bestValue().getId(), 1e12, 0);
        office.restoreIron(3, 4_000_000.5);
        office.extractIron(1_000.25);
        CityLand ground = office.getCityLand();
        DataSave landSave = new DataSave();
        landSave.setBuildingNum(13);
        landSave.setCityLand(ground.centreState(), ground.centreRectsState(), ground.holdingsState(), ground.partFieldsState(),
                ground.convertedState(), office.getMarket().getOffersState(), office.getMarket().getNextOfferId(),
                office.getDepletionState(), office.getWorldTotalsState(), office.getWorldSeaTheta());
        assertTrue("fixture: the land wrote itself", landSave.saveGame(trip, 2).ok);
        String landJson = Files.readString(trip.saveFile(2));
        DataSave landRead = gson.fromJson(landJson, DataSave.class);
        assertTrue("the centre survived, every field", java.util.Arrays.equals(landRead.getLandCentre(), ground.centreState()));
        assertTrue("...its blocks (0.7.67)", java.util.Arrays.deepEquals(landRead.getLandCentreRects(), ground.centreRectsState()));
        assertTrue("...the purchase, its rectangle and all (0.7.67)", java.util.Arrays.deepEquals(landRead.getLandHoldings(), ground.holdingsState()));
        assertTrue("...the twenty-four offers", java.util.Arrays.deepEquals(landRead.getLandOffers(), office.getMarket().getOffersState()));
        assertTrue("...and no lanes, which only a format-31 save is read for (0.7.67)",
                landRead.getLandLanes() == null && landRead.getLandPurchases() == null);
        assertEquals("...the next offer's id", landRead.getNextOfferId(), office.getMarket().getNextOfferId());
        assertTrue("...what was taken out", java.util.Arrays.equals(landRead.getDepletion(), office.getDepletionState()));
        assertTrue("...the offshore pool's E null on a save without it, as one from before 0.7.93 (the load charges its one pool to"
                + " the ground)", landRead.getOilDepletionAtSea() == null);
        assertTrue("...the world's totals", java.util.Arrays.equals(landRead.getWorldTotals(), office.getWorldTotalsState()));
        assertEquals("...and its sea's level", landRead.getWorldSeaTheta(), office.getWorldSeaTheta());
        // ...and the offshore pool's E (0.7.93, SAVE_FORMAT 35), beside the depletion's ground pool.
        landSave.setOilDepletionAtSea(1_234.5);
        assertTrue("fixture: the offshore pool's E wrote itself", landSave.saveGame(trip, 2).ok);
        Double atSeaRead = gson.fromJson(Files.readString(trip.saveFile(2)), DataSave.class).getOilDepletionAtSea();
        assertTrue("the offshore pool's E survived, to the bit (0.7.93)", atSeaRead != null && atSeaRead == 1_234.5);
        // ...and the city's water rights (0.7.59), boxed: absent on an older save.
        landSave.setFreshRights(12_345.5);
        assertTrue("fixture: the rights wrote themselves", landSave.saveGame(trip, 2).ok);
        assertEquals("the water rights survived (0.7.59)",
                gson.fromJson(Files.readString(trip.saveFile(2)), DataSave.class).getFreshRights(), 12_345.5);
        // ...and the city map's sidecar stamp (0.7.60), boxed: absent when the city had no map.
        landSave.setMapStamp(-1_234_567_890_123_456_789L);
        assertTrue("fixture: the map's stamp wrote itself", landSave.saveGame(trip, 2).ok);
        assertEquals("the city map's stamp survived, every bit (0.7.60)",
                gson.fromJson(Files.readString(trip.saveFile(2)), DataSave.class).getMapStamp(), -1_234_567_890_123_456_789L);
        landSave.setMapStamp(null);
        // ...and the drivers' fuel as 6d drew it (0.7.62): the bill, its imported part and the litres.
        landSave.setHouseholdFuel(new double[] { 1_234.567, 456.789, 987_654.321 });
        assertTrue("fixture: the fuel month wrote itself", landSave.saveGame(trip, 2).ok);
        assertTrue("the drivers' fuel month survived, every figure (0.7.62)", java.util.Arrays.equals(
                gson.fromJson(Files.readString(trip.saveFile(2)), DataSave.class).getHouseholdFuel(),
                new double[] { 1_234.567, 456.789, 987_654.321 }));
        assertTrue("fixture: ...and a save with no map wrote itself", landSave.saveGame(trip, 2).ok);
        assertTrue("...and a save with no map carries no stamp", !Files.readString(trip.saveFile(2)).contains("\"mapStamp\""));
        CityLand rebuilt = CityLand.restore(ground.seed(), landRead.getLandCentre(), landRead.getLandCentreRects(),
                landRead.getLandHoldings(), landRead.getLandPartFields(), landRead.getLandConverted());
        assertTrue("...and the land it rebuilds is the land written", ground.same(rebuilt));
        assertTrue("a save written now carries no parcels and no iron pool, which only an older save is read for",
                !landJson.contains("\"landListing\"") && !landJson.contains("\"ironDeposits\"")
                        && !landJson.contains("\"ironReserveTonnes\""));
        DataSave format30 = gson.fromJson("{\"ironDeposits\": 155, \"ironReserveTonnes\": 508088779.4794291}", DataSave.class);
        assertEquals("...and an older save's iron pool reads, for the conversion: its sites", format30.getIronDeposits(), 155);
        assertEquals("...and its tonnes", format30.getIronReserveTonnes(), 508088779.4794291);
        assertTrue("...and no land of its own", format30.getLandCentre() == null && format30.getWorldSeaTheta() == null);
        assertTrue("...and no water rights, which the load derives (0.7.59)", format30.getFreshRights() == null);
        assertTrue("...and no city map, which waits to be asked for (0.7.60)", format30.getMapStamp() == null);
        assertTrue("...and no fuel month, which the load derives as that build struck it (0.7.62)",
                format30.getHouseholdFuel() == null);
        assertEquals("the slot list reads the same name off the same file",
                trip.readHeader(1).getCityName(), "Arden");
        DataSave older = gson.fromJson("{\"cash\": 5.0}", DataSave.class);
        Founding before0710 = older.getFounding(WorldEconomy.LEGACY_MEAN_INFLATION);
        assertTrue("a save with none of the eight reads as the founding every city had before 0.7.10",
                before0710.getCityName().equals(Founding.DEFAULT_CITY_NAME)
                        && before0710.getCurrency().equals(Currency.DANZIK)
                        && before0710.getCash() == Founding.WEALTHY_CASH
                        && before0710.getReserveUsd() == Founding.WEALTHY_RESERVE_USD);
        assertTrue("...and its header says Danzik", new com.google.gson.Gson()
                .fromJson("{\"month\": 3}", SaveHeader.class).getCityName().equals(Founding.DEFAULT_CITY_NAME));
        assertTrue("...and its world is the one its name, treasury, ground and month make (0.7.56)",
                older.getWorldSeed() == null && before0710.getWorldSeed() == Founding.derivedWorldSeed(
                        Founding.DEFAULT_CITY_NAME, Founding.WEALTHY_CASH, older.getLandOwned(), older.getMonth()));
        DataSave before0756 = gson.fromJson("{\"cityName\": \"Arden\", \"foundingCash\": 62500.0,"
                + " \"landOwned\": 4000000.0, \"month\": 87}", DataSave.class);
        assertEquals("a save from 0.7.10 to 0.7.55 reads the seed its own fields make",
                before0756.getFounding(.02).getWorldSeed(), Founding.derivedWorldSeed("Arden", 62_500, 4_000_000, 87));

        // The transient path fields that used to live on DataSave were dropped;
        // make sure nothing crept into the file that should not be in it.
        String json = Files.readString(trip.saveFile(1));
        assertTrue("no file paths leaked into the save",
                !json.contains("YourGame") && !json.contains("userHome"));

        // Recorded off a real city rather than from typed-in numbers: the
        // recorder takes the Game now, because twenty-three positional
        // arguments is a machine for transposing two of them.
        Game recorder = new Game(GameFiles.scratch("savecheck"));
        recorder.newGame();
        HistorySave history = new HistorySave();
        history.recordMonth(recorder);
        recorder.simulateMonths(1);
        history.recordMonth(recorder);
        assertEquals("two months recorded", history.months(), 2);
        assertTrue("HistorySave wrote itself", history.saveHistory(trip, 1).ok);
        assertTrue("...to its own file, not over the save",
                Files.readString(trip.saveFile(1)).contains("123456"));

        /* ============ 7. construction survives a save ============ */
        System.out.println("\n--- work still on site ---");

        // The bug this section exists for: construction was stored one entry
        // per STACK in build order, while the load recreated stacks in template
        // id order and only for templates with something already finished. A
        // building that was purely under construction left no stack at all, so
        // every later position shifted and the whole array was thrown away. A
        // player who saved with depots part-built reloaded to find the work
        // gone, and the load said nothing.
        Path cityDir = root.resolve("city");
        GameFiles cityFiles = new GameFiles(cityDir, root.resolve("no-legacy"));

        Game city = new Game(cityFiles);
        city.run();

        BuildingsTemplate house = template(city, "House");
        BuildingsTemplate store = template(city, "Convenience Store");
        BuildingsTemplate depot = template(city, "Construction Depot");

        // Industry included on purpose. The first version of this city had none,
        // and a city with no mills cannot catch the flow bugs in section 9 -
        // every industrial figure is zero on both sides of the save and the
        // assertions pass without proving anything.
        city.buildStack(house, 120, false);
        city.buildStack(store, 3, false);
        city.buildStack(template(city, "Industrial Bakery"), 1, false);
        city.simulateMonths(90);

        // Ground for them by fiat (0.7.67): a new city owns its drawn dry
        // plots now, 1.7% more than STARTING_SQ_FT, and its ninety months fill
        // all of it - so the depots and the stores are given their own room.
        LandManager room = city.getLandManager();
        room.setOwnedSqFt(room.getAllocatedSqFt() + 2 * depot.getLandSqFt() + 4 * store.getLandSqFt()
                + Math.max(0, room.getOwnedSqFt() - room.getAllocatedSqFt()));

        // Started now and deliberately NOT finished, so the save is taken with
        // real work in progress.
        city.buildStack(depot, 2, false);
        city.buildStack(store, 4, false);
        city.simulateMonths(1);

        int depotsUnderWay = city.getBuildingManager().getUnderConstructionById()[depot.getId()];
        int storesUnderWay = city.getBuildingManager().getUnderConstructionById()[store.getId()];
        double depotProgress = city.getBuildingManager()
                .getConstructionProgressById()[depot.getId()];
        double footprint = city.getBuildingManager().getTotalLandFootprint();

        assertTrue("something is genuinely mid-build", depotsUnderWay > 0);
        System.out.printf("   %d depot(s) and %d store(s) on site, %.1f points in%n",
                depotsUnderWay, storesUnderWay, depotProgress);

        // Captured either side of the round trip and nowhere else. Reading it
        // later would compare against a city that other sections have since
        // touched, and getIncome() recomputes as it goes.
        double incomeBefore = city.getIncome();
        long peopleBefore = city.getPopulationManager().getPopulation();

        // History the city cannot recompute from its present. Recorded here
        // rather than waited for, so the assertion does not depend on whether
        // this particular city happens to demolish something.
        city.getDemolitionLog().record("Construction Depot", 2, "Construction",
                city.getMonth(), 480);
        int demolitionsBefore = city.getDemolitionLog().size();

        // The other half of that history. Recorded by hand for the same reason:
        // whether this particular city finishes a building in these months is
        // not something the assertion should be hostage to.
        city.getBuildLog().record("Coal Power Plant", 1, city.getMonth());
        city.getBuildLog().record("House", 12, city.getMonth());
        int buildsBefore = city.getBuildLog().size();
        double writtenOffBefore =
                city.getEconomyManager().getBusinessDebtManager().getTotalWrittenOff();

        assertTrue("saved", city.saveGame(1, "the test city").ok);

        Game reloaded = new Game(cityFiles);
        reloaded.loadGameSave(1);

        double incomeAfter = reloaded.getIncome();
        long peopleAfter = reloaded.getPopulationManager().getPopulation();

        BuildingManager after = reloaded.getBuildingManager();
        assertEquals("depots still under construction",
                after.getUnderConstructionById()[depot.getId()], depotsUnderWay);
        assertEquals("stores still under construction",
                after.getUnderConstructionById()[store.getId()], storesUnderWay);
        assertEquals("and the part-finished work came back",
                after.getConstructionProgressById()[depot.getId()], depotProgress);

        // The quiet half of the same bug. Land is allocated when construction
        // STARTS, and the load derives allocation from quantity plus
        // under-construction - so dropping the in-progress work silently handed
        // the city back land it had already committed, and let it overbuild.
        assertEquals("land committed to unfinished sites is still committed",
                after.getTotalLandFootprint(), footprint);

        /* ============ 7b. the warning survives a save ============ */
        System.out.println("\n--- and the warning the city is under ---");

        /*
         * A WARNING IS STATE, NOT DECORATION
         *
         * When the private sector starts scrapping construction capacity the
         * start screen shows a banner offering to pay a retainer and stop it.
         * The banner is driven by two fields on Game, and neither was saved -
         * so reloading silently cleared it. The city was still dismantling its
         * builders, the player had never answered the question, and the game
         * had quietly stopped asking.
         *
         * That is worse than never having warned: the save-and-reload a player
         * does mid-crisis is exactly when they need the warning most. The
         * retainer itself is checked here too, because it is the answer to the
         * question the banner asks and it lives beside it.
         */
        Path warnedDir = root.resolve("warned");
        GameFiles warnedFiles = new GameFiles(warnedDir, root.resolve("no-legacy"));

        Game warned = new Game(warnedFiles);
        warned.run();
        warned.toggleReports();      // eight months of city reports is not the point
        warned.toggleGraphs();
        warned.buildStack(template(warned, "House"), 60, false);
        warned.buildStack(depot, 2, false);
        warned.simulateMonths(8);

        /*
         * UNPROTECTED, deliberately.
         *
         * This used to set a partial dollar retainer and check the warning
         * survived alongside it. That state no longer exists: the retainer was a
         * slice of capacity bought with a fixed sum, and the standing policy
         * that replaced it protects the sector or does not. So the warning is
         * now tested in both of its two real positions instead - up when nothing
         * is protecting the crews, and down the moment something is.
         */
        warned.setAutoSubsidised(Sectors.CONSTRUCTION, false);
        warned.restoreConstructionShedding(warned.getMonth(), 900);

        boolean warningUp = warned.isConstructionShedding();
        assertTrue("an unprotected city with a fresh shed IS warned", warningUp);

        int shedMonth = warned.getConstructionShedMonth();
        double shedPoints = warned.getConstructionShedPoints();

        warned.saveGame(6, "warned city");

        Game stillWarned = new Game(warnedFiles);
        stillWarned.loadGameSave(6);

        assertEquals("the month construction last shed came back",
                stillWarned.getConstructionShedMonth(), shedMonth);
        assertEquals("...and the capacity it has sold since",
                stillWarned.getConstructionShedPoints(), shedPoints);
        assertEquals("...and it is still unprotected",
                stillWarned.isAutoSubsidised(Sectors.CONSTRUCTION) ? 1 : 0, 0);
        assertEquals("...so the player is still being warned",
                stillWarned.isConstructionShedding(), warningUp);

        // ...and protecting the sector is what actually answers the warning.
        stillWarned.setAutoSubsidised(Sectors.CONSTRUCTION, true);
        assertTrue("protecting construction takes the warning down",
                !stillWarned.isConstructionShedding());
        stillWarned.setAutoSubsidised(Sectors.CONSTRUCTION, false);

        // Acknowledging it has to stick too, or the banner comes back on reload
        // for a player who has already said no.
        stillWarned.acknowledgeConstructionShedding();
        stillWarned.saveGame(6, "warned city");

        Game dismissed = new Game(warnedFiles);
        dismissed.loadGameSave(6);
        assertTrue("a dismissed warning stays dismissed across a reload",
                !dismissed.isConstructionShedding());

        /* ============ 8. a save with nothing else built ============ */
        System.out.println("\n--- a template that exists ONLY on site ---");

        // The exact case the old format could not represent: a building with
        // zero finished and some under construction has no stack to line up
        // against, so it vanished entirely.
        Path freshCityDir = root.resolve("fresh-city");
        GameFiles freshFiles = new GameFiles(freshCityDir, root.resolve("no-legacy"));

        Game brandNew = new Game(freshFiles);
        brandNew.run();
        brandNew.buildStack(depot, 1, false);

        assertEquals("nothing is finished yet",
                brandNew.getBuildingManager().getQuantity(depot.getId()), 0);
        assertEquals("but one is being built",
                brandNew.getBuildingManager().getUnderConstructionById()[depot.getId()], 1);
        assertTrue("saved", brandNew.saveGame(2).ok);

        Game reopened = new Game(freshFiles);
        reopened.loadGameSave(2);
        assertEquals("it did not vanish",
                reopened.getBuildingManager().getUnderConstructionById()[depot.getId()], 1);

        /* ============ 9. the headline income does not move ============ */
        System.out.println("\n--- and the next-month figure holds still ---");

        // Property tax is CHARGED during a month and read back rather than
        // recomputed, so it is state. It was not saved, and a reloaded city
        // showed a next-month income short by the entire property-tax line,
        // which then corrected itself the moment a month was simulated.
        assertEquals("property tax survived the round trip",
                Math.round(reloaded.getEconomyManager().getTotalPropertyTax() * 10000),
                Math.round(city.getEconomyManager().getTotalPropertyTax() * 10000));

        // The half that was missed the first time round. chargePropertyTax() is
        // also the only thing that tells each SECTOR what it owes, and none of
        // those figures were restored - so every loaded city had retail and
        // real estate reporting income statements with no property-tax expense
        // line at all, which made them look more profitable than they were and
        // pushed the city's business tax up with them.
        Sector beforeShops = city.getSectors().retail();
        Sector afterShops = reloaded.getSectors().retail();
        Sector beforeLandlords = city.getSectors().realEstate();
        Sector afterLandlords = reloaded.getSectors().realEstate();

        assertTrue("the city actually charges retail something",
                beforeShops.getPropertyTaxExpense() > 0);
        assertEquals("retail's property tax expense came back",
                Math.round(afterShops.getPropertyTaxExpense() * 10000),
                Math.round(beforeShops.getPropertyTaxExpense() * 10000));
        assertEquals("real estate's did too",
                Math.round(afterLandlords.getPropertyTaxExpense() * 10000),
                Math.round(beforeLandlords.getPropertyTaxExpense() * 10000));
        assertEquals("...and the line on the statement with it",
                Math.round(afterShops.statement().propertyTax * 10000),
                Math.round(beforeShops.statement().propertyTax * 10000));

        // And the parts add up to the whole the city collected.
        double parts = 0;
        for (Sector s : reloaded.getSectors().all()) parts += s.getPropertyTaxExpense();
        assertTrue("the sector charges are part of the city's total",
                parts > 0 && parts <= reloaded.getEconomyManager().getTotalPropertyTax() + 0.01);

        /* ============ 10. the whole figure, to the cent ============ */
        System.out.println("\n--- and the number on the button ---");

        // The bug as Jerus reported it: note the income, save, quit, load, and
        // the income has changed. It is the end-to-end assertion that sections
        // 1 to 9 exist to make possible, and it holds only because every
        // FLOW - not just every balance - now survives the round trip. An income
        // statement covers a period; it cannot be rebuilt from the instant that
        // period ended.
        // The one that is not about money at all, and the worst of the set: the
        // load path was recomputing population instead of restoring it, and
        // feeding updatePop() a different job count than a month does. A city
        // saved with 214 residents came back with 259 - 45 people conjured out
        // of nothing, every time it was opened.
        System.out.printf("   population %d -> %d%n", peopleBefore, peopleAfter);
        assertTrue("the city has people at all", peopleBefore > 0);
        assertEquals("population is not invented by loading", peopleAfter, peopleBefore);

        EconomyManager e1 = city.getEconomyManager();
        EconomyManager e2 = reloaded.getEconomyManager();

        /*
         * THIS USED TO ASSERT getIndustryDemand() > 0, AND THAT WAS A PHASE OF
         * A CYCLE, NOT A PROPERTY.
         *
         * Demand is a REORDER: what the shops are about to buy from the mills.
         * A shop with full shelves orders nothing this month and is trading
         * perfectly well, so the assertion held only while the restock happened
         * to land on the month the fixture stopped. It went red the first time
         * anything shifted the city's consumption by a few people - here, a
         * change to the skill mix of who moves in - and the simulation was
         * fine.
         *
         * What this section actually needs is that the figures compared below
         * are not all zero, because two zeros match perfectly and prove
         * nothing. So that is what it asks: goods exist, they reached the
         * shops, and the taxes being compared are real money.
         */
        System.out.printf("   food stock %.0f, shop stock %d, taxes $%.2f%n",
                city.getSectors().industry().getStock(Good.BREAD),
                city.getSectors().retail().getStoreInventory(), e1.getTaxIncome());

        /*
         * AND THE SAME MISTAKE ONE LINE DOWN, found 2026-09-13. There was a
         * third assertion here - food in the MILLS' warehouse - and it was the
         * same phase-of-a-cycle reading the note above retired, one shelf
         * along. A mill that sold its month's output to the shops ends the
         * month with an empty shed and has been trading perfectly well;
         * measured here at food stock 0 against shop stock 1,940, every unit
         * made had already moved. Restating it as "the mills billed somebody"
         * was no better, because the statement is struck at the TOP of a month
         * off the month before and a fixture can stop anywhere in that cycle.
         *
         * The two below are the ones that carry the section: goods reached the
         * shops, which cannot happen unless the mills made them, and the taxes
         * being compared are real money. Neither is a reading of where the
         * restock happened to land.
         */
        assertTrue("goods reached the shops, which the mills had to make first",
                city.getSectors().retail().getStoreInventory() > 0);
        assertTrue("...so the figures compared below are not all zero",
                e1.getTaxIncome() > 0);

        // Exact, and they have to stay exact.
        assertEquals("business tax", Math.round(e2.getBusinessTax() * 10000),
                Math.round(e1.getBusinessTax() * 10000));
        assertEquals("wage tax", Math.round(e2.getWageTax() * 10000),
                Math.round(e1.getWageTax() * 10000));
        assertEquals("retail cost of goods",
                Math.round(reloaded.getSectors().retail().statement().inputs * 10000),
                Math.round(city.getSectors().retail().statement().inputs * 10000));
        assertEquals("...and the month in progress",
                Math.round(reloaded.getSectors().retail().pending().purchases() * 10000),
                Math.round(city.getSectors().retail().pending().purchases() * 10000));

        /*
         * Exact, to four decimal places.
         *
         * This was a bounded assertion for a while - "under 1%" - because every
         * flow carried fixed one term and revealed another underneath it. What
         * ended the chase was measuring the whole state rather than one number:
         * the load path was calling finalEconUpdate(), which PRODUCES food,
         * moves inventory and has the shops trade. Opening a save ran a month of
         * the economy with the calendar standing still, and everything else was
         * downstream of that.
         */
        System.out.printf("   income $%.4f -> $%.4f%n", incomeBefore, incomeAfter);
        assertEquals("next-month income is identical across a save",
                Math.round(incomeAfter * 10000), Math.round(incomeBefore * 10000));

        assertEquals("sales tax", Math.round(e2.getSalesTax() * 10000),
                Math.round(e1.getSalesTax() * 10000));
        assertEquals("monthly GDP", Math.round(e2.getMonthGdp() * 10000),
                Math.round(e1.getMonthGdp() * 10000));

        /*
         * ...AND THE YEAR OF IT (0.7.31, the Government spec's B1). The
         * national accounts' rolling history was not restored at all, so a
         * loaded city had one month recorded - the rebuild's - and every "of
         * annual GDP" read that month times twelve for a year. The load path
         * seeds it from the graph history's GDP series now - and since 0.7.81
         * from the save's exact copy (DataSave.gdpRolling): automatic
         * building's debt limit reads the year in the month, and the graph
         * keeps thousands to two places. Until 0.7.81 the label below read
         * "...and its year is the history's last twelve months" of the loaded
         * city; that is now the older save's (no copy), asserted after it.
         */
        java.util.List<Double> keptGdp = reloaded.getHistorySave().getGdp();
        NationalAccounts naBack = reloaded.getEconomyManager().getNationalAccounts();
        double lastTwelve = 0;
        for (int i = Math.max(0, keptGdp.size() - 12); i < keptGdp.size(); i++) lastTwelve += keptGdp.get(i);
        assertTrue("fixture: the city kept more than a year of GDP", keptGdp.size() > 12);
        assertEquals("a loaded city has the months its history kept, up to ten years",
                naBack.getMonthsRecorded(), Math.min(120, keptGdp.size()));
        NationalAccounts naLive = city.getEconomyManager().getNationalAccounts();
        assertTrue("...and its year is the live city's to the bit: the rolling year saved exactly (0.7.81)",
                Double.doubleToLongBits(naBack.getAnnualGdp()) == Double.doubleToLongBits(naLive.getAnnualGdp())
                        && naBack.getHistory().equals(naLive.getHistory()));
        com.google.gson.JsonObject withYear = com.google.gson.JsonParser.parseString(
                Files.readString(cityFiles.saveFile(1))).getAsJsonObject();
        assertTrue("fixture: the save carries the rolling year", withYear.has("gdpRolling"));
        withYear.remove("gdpRolling");
        Files.writeString(cityFiles.saveFile(1), withYear.toString());
        Game olderYear = new Game(cityFiles);
        olderYear.loadGameSave(1);
        same("...and a save without it (before 0.7.81) reads its year from the history's last twelve months",
                olderYear.getEconomyManager().getNationalAccounts().getAnnualGdp(), lastTwelve);

        // The order book, which is what makes the GDP line above hold.
        ham.citybuildersim.sectors.Construction b1 = city.getSectors().construction();
        ham.citybuildersim.sectors.Construction b2 = reloaded.getSectors().construction();
        assertEquals("construction backlog", Math.round(b2.getBacklogPoints() * 10000),
                Math.round(b1.getBacklogPoints() * 10000));
        assertEquals("construction unearned revenue",
                Math.round(b2.getUnearnedRevenue() * 10000),
                Math.round(b1.getUnearnedRevenue() * 10000));
        assertEquals("construction cash", Math.round(b2.getCash() * 10000),
                Math.round(b1.getCash() * 10000));

        /*
         * History survives too.
         *
         * Neither of these was saved, so every load emptied the demolition log
         * and reset the write-off record to zero - a city came back looking as
         * though it had never lost a building or defaulted on anything. History
         * is the one kind of state that CANNOT be recomputed from the present,
         * which makes forgetting it the least recoverable thing a load can do.
         */
        assertTrue("the city did lose something", demolitionsBefore > 0);
        assertEquals("the demolition log came back",
                reloaded.getDemolitionLog().size(), demolitionsBefore);
        assertEquals("...with its entries intact",
                reloaded.getDemolitionLog().all().get(0).building,
                city.getDemolitionLog().all().get(0).building);
        assertEquals("the write-off record came back",
                Math.round(reloaded.getEconomyManager()
                        .getBusinessDebtManager().getTotalWrittenOff() * 10000),
                Math.round(writtenOffBefore * 10000));

        /*
         * And the build log, which is format 10.
         *
         * Worth stating the quantity and not just the count: BuildLog merges
         * same-building-same-month rows on record(), and restore() replays
         * through record(), so a restore that re-merged or double-counted would
         * come back with the right number of ROWS and the wrong number of
         * buildings in them - which is exactly the shape of bug a size check
         * sails past.
         */
        assertTrue("the city did finish something", buildsBefore > 0);
        assertEquals("the build log came back",
                reloaded.getBuildLog().size(), buildsBefore);
        assertEquals("...with its entries intact",
                reloaded.getBuildLog().all().get(0).building,
                city.getBuildLog().all().get(0).building);
        assertEquals("...and the merged quantities unchanged",
                reloaded.getBuildLog().all().get(0).quantity,
                city.getBuildLog().all().get(0).quantity);

        /* ============ 11. a city that is still MOVING ============ */
        System.out.println("\n--- and a city that has not settled down ---");

        /*
         * Everything above this point is measured on a city at 90+ months,
         * which by then is barely changing month to month - and a city that is
         * not moving cannot catch a bug about things that move. Three of them
         * hid behind exactly that:
         *
         *   - the month's income statements were REBUILT on load from the state
         *     the save was taken in, not the state they were written against
         *   - the workforce was re-derived from the population the save was
         *     taken with, while updatePop() derives it from the population the
         *     month STARTED with. 418 workers restored against the 386 that
         *     actually worked, on a city of 836
         *   - the utilisation ratios the month traded at were likewise recomputed
         *
         * All three are invisible in a steady city and all three are a straight
         * few percent of the wage bill, the sales tax and next month's income in
         * a growing one. So this section deliberately saves a city mid-growth,
         * and asserts it was still growing before trusting anything it says.
         */
        Path growingDir = root.resolve("growing");
        GameFiles growingFiles = new GameFiles(growingDir, root.resolve("no-legacy"));

        Game growing = new Game(growingFiles);
        growing.run();
        // THE TREASURY THIS FIXTURE WAS WRITTEN AGAINST (0.7.10): its build list
        // is bought out of cash, and a city founds on D$100M since 0.7.10, not the
        // D$2.5B it assumed - so it is given that, the Wealthy preset's, explicitly.
        growing.setCashForTest(Founding.WEALTHY_CASH);
        growing.buildStack(template(growing, "House"), 300, false);
        growing.buildStack(template(growing, "Convenience Store"), 6, false);
        growing.buildStack(template(growing, "Industrial Bakery"), 1, false);
        growing.simulateMonths(40);

        /*
         * CAUSE THE CONDITION, DO NOT WAIT FOR IT.
         *
         * This section needs a city genuinely mid-stride - one whose statement
         * was written against a smaller population than it now has - and it used
         * to get there by building a city and hoping forty months left it in
         * that state. It did, until housing got cheap enough to build that the
         * city filled up and settled by month forty, and then the fixture failed
         * while nothing it was testing had changed.
         *
         * So: drop a block of shops in instantly, which raises the jobs the city
         * is pulling against, and run one month. Migration moves people during
         * that month by construction, and the month's statement necessarily
         * describes the city as it was before they arrived. Standing rule in
         * this codebase, hit again.
         */
        BuildingManager gbm = growing.getBuildingManager();
        gbm.addStack(gbm.getTemplateByName("Convenience Store"), 30, true);
        growing.simulateMonths(1);

        PopulationManager gp = growing.getPopulationManager();
        ham.citybuildersim.sectors.Retail gc = growing.getSectors().retail();

        // The city has to be genuinely mid-stride, or this proves nothing. Both
        // of these are the specific things that were being re-derived.
        assertTrue("the city really is still growing",
                gp.getWorkforce() != (int) (gp.getPopulation() * .5));
        assertTrue("...and its statement describes a smaller month than the one in progress",
                Math.abs(gc.statement().revenue - gc.pending().revenue()) > 1e-9);

        EconomyManager ge = growing.getEconomyManager();
        double gIncome = growing.getIncome();
        long gWorkforce = gp.getWorkforce();
        double gWageTax = ge.getWageTax();
        double gSalesTax = ge.getSalesTax();
        double gRetail = gc.statement().revenue;
        double gRetailPending = gc.pending().revenue();
        double gIndustry = growing.getSectors().industry().statement().revenue;
        double gGdp = ge.getMonthGdp();

        assertTrue("saved mid-growth", growing.saveGame(1, "still moving").ok);

        Game moved = new Game(growingFiles);
        moved.loadGameSave(1);
        EconomyManager me = moved.getEconomyManager();

        System.out.printf("   income $%.4f -> $%.4f%n", gIncome, moved.getIncome());

        assertEquals("the workforce that worked the month came back",
                moved.getPopulationManager().getWorkforce(), gWorkforce);
        assertEquals("wage tax", Math.round(me.getWageTax() * 10000),
                Math.round(gWageTax * 10000));
        assertEquals("retail gross revenue",
                Math.round(moved.getSectors().retail().statement().revenue * 10000),
                Math.round(gRetail * 10000));
        assertEquals("...and the month the shops are in",
                Math.round(moved.getSectors().retail().pending().revenue() * 10000),
                Math.round(gRetailPending * 10000));
        assertEquals("industrial gross revenue",
                Math.round(moved.getSectors().industry().statement().revenue * 10000),
                Math.round(gIndustry * 10000));
        assertEquals("sales tax", Math.round(me.getSalesTax() * 10000),
                Math.round(gSalesTax * 10000));
        assertEquals("monthly GDP", Math.round(me.getMonthGdp() * 10000),
                Math.round(gGdp * 10000));
        assertEquals("and next month's income, to the cent",
                Math.round(moved.getIncome() * 10000), Math.round(gIncome * 10000));

        /* ============ 12. a city that OWES money ============ */
        System.out.println("\n--- and a city with bonds outstanding ---");

        /*
         * Every city above this line is debt-free, so none of them could catch
         * this: the interest the city's own bonds accrued was never restored.
         *
         * DebtManager.processAllDebts() runs AFTER the month's income has been
         * banked, so the accrual sits on the books waiting to be charged next
         * month. A save taken in between came back with it at zero - a city on
         * $219,700 of bonds reloaded showing a $7 surplus in place of a $103
         * deficit, and never paid that month's interest at all. Free money, and
         * repeatable: save, reload, skip a bill.
         *
         * EconomyManager.setInterest() had been written for exactly this, with
         * a comment explaining why it was needed, and nothing ever called it.
         * A setter with no caller is not a fix.
         */
        Path debtDir = root.resolve("indebted");
        GameFiles debtFiles = new GameFiles(debtDir, root.resolve("no-legacy"));

        Game indebted = new Game(debtFiles);
        indebted.run();
        indebted.buildStack(template(indebted, "House"), 80, false);
        indebted.buildStack(template(indebted, "Convenience Store"), 3, false);
        indebted.simulateMonths(5);
        indebted.handleLongBondLogic(200000, 20, 100);
        indebted.simulateMonths(6);

        EconomyManager de = indebted.getEconomyManager();

        assertTrue("the city really does owe something",
                indebted.getDebtManager().getAllPrincipal() > 0);
        assertTrue("...and has interest on the books waiting to be charged",
                de.getExpenses() > 0);

        double dInterest = de.getExpenses();
        double dIncome = indebted.getIncome();

        assertTrue("saved in debt", indebted.saveGame(1, "in the red").ok);

        Game paidUp = new Game(debtFiles);
        paidUp.loadGameSave(1);

        System.out.printf("   income $%.4f -> $%.4f on $%s of bonds%n",
                dIncome, paidUp.getIncome(),
                Math.round(indebted.getDebtManager().getAllPrincipal()));

        assertEquals("the debt came back",
                Math.round(paidUp.getDebtManager().getAllPrincipal() * 100),
                Math.round(indebted.getDebtManager().getAllPrincipal() * 100));
        /*
         * ...AND WHO HOLDS IT (0.7.1): the households' paper is a new slot on
         * every cell and the holders are new fields on every piece of paper.
         * A slot that made it into the writer and not the reader - or the
         * other way - reloads a city whose households own a bond nobody is
         * paying them for.
         */
        assertTrue("fixture: the households hold some of it",
                indebted.getDebtManager().householdPrincipal() > 0);
        assertEquals("...and still do after the reload, cell by cell",
                Math.round(paidUp.getHouseholdBalance().totalPaper() * 10000),
                Math.round(indebted.getHouseholdBalance().totalPaper() * 10000));
        assertEquals("...which is what the paper says they hold",
                Math.round(paidUp.getDebtManager().householdPrincipal() * 10000),
                Math.round(indebted.getDebtManager().householdPrincipal() * 10000));
        assertEquals("...and the discount still to accrete on it came back",
                Math.round(paidUp.getDebtManager().getDebt().get(0).getDiscountLeft() * 10000),
                Math.round(indebted.getDebtManager().getDebt().get(0).getDiscountLeft() * 10000));
        assertEquals("...and so did the interest it had already accrued",
                Math.round(paidUp.getEconomyManager().getExpenses() * 10000),
                Math.round(dInterest * 10000));
        assertEquals("...so next month's income still shows the deficit",
                Math.round(paidUp.getIncome() * 10000), Math.round(dIncome * 10000));

        // The half that actually costs money: the bill has to be PAID, once.
        // Restoring the figure and then not charging it would look right on the
        // screen and still hand the player a free month.
        double cashBefore = indebted.getCash();
        indebted.simulateMonths(1);
        paidUp.simulateMonths(1);

        System.out.printf("   a month on: $%.4f vs $%.4f (started $%.4f)%n",
                indebted.getCash(), paidUp.getCash(), cashBefore);
        assertEquals("and a month later both cities have paid the same bill",
                Math.round(paidUp.getCash() * 10000),
                Math.round(indebted.getCash() * 10000));

        /* ============ 12b. ...AND ONE THAT OWES ABROAD (2026-09-21) ============

           What the currency did to the city's dollar debt this month -
           ForeignAccounts.getLastRevaluation(), printed by the trade and the
           finance pages beside what is owed - was struck inside the month and
           carried by nothing, so a freshly loaded city read "the currency did
           nothing" beside a debt the rate had just moved. Slot 24 of the
           foreign accounts' array now.

           The fixture CAUSES a revaluation rather than finding one: the city
           borrows dollars on its first morning, and is played until a month in
           which the rate actually moved them - which it cannot do before
           ForeignAccounts.SETTLING_MONTHS of trade, so that many months first.
           ================================================================= */
        System.out.println("\n--- and a city with dollars owed abroad ---");

        GameFiles abroadFiles = new GameFiles(root.resolve("abroad"), root.resolve("no-legacy"));
        Game abroad = new Game(abroadFiles);
        java.io.PrintStream shown = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        try {
            abroad.run();
            abroad.buildStack(template(abroad, "House"), 80, false);
            abroad.buildStack(template(abroad, "Convenience Store"), 3, false);
            abroad.handleForeignLogic("Term", 5_000, 30, 100, false);   // one of the five (0.7.1)
            abroad.simulateMonths(ForeignAccounts.SETTLING_MONTHS + 2);
            for (int extra = 0; extra < 120
                    && Math.abs(abroad.getForeignAccounts().getLastRevaluation()) < 1e-6; extra++) {
                abroad.simulateMonths(1);
            }
        } finally {
            System.setOut(shown);
        }
        double revalued = abroad.getForeignAccounts().getLastRevaluation();
        System.out.printf("   US$%,.0fk owed; the currency moved it by $%,.4fk this month%n",
                abroad.getDebtManager().getForeignPrincipalUsd(), revalued);
        assertTrue("fixture: the city really does owe dollars",
                abroad.getDebtManager().getForeignPrincipalUsd() > 0);
        assertTrue("fixture: ...and the rate really did move them this month",
                Math.abs(revalued) > 1e-6);

        assertTrue("saved owing abroad", abroad.saveGame(1, "owes abroad").ok);
        Game abroadBack = new Game(abroadFiles);
        abroadBack.loadGameSave(1);
        same("what the currency did to the debt this month reloads",
                abroadBack.getForeignAccounts().getLastRevaluation(), revalued);
        same("...beside the rest of the foreign accounts, which always did",
                abroadBack.getForeignAccounts().getLifetimeRevaluation(),
                abroad.getForeignAccounts().getLifetimeRevaluation());

        /* ============ 13. AND NOTHING READS ZERO ON A FRESHLY LOADED CITY ============

           Finding #15 in claude/simulation-findings.md, as an assertion.

           A whole family of readings are struck inside nextMonth() and are in
           nobody's getState(). On a save that has just been loaded they were all
           zero, and the screens that draw them had no way of telling "zero" from
           "not struck yet": the Policies landing read `savers are paid 0.00%`
           beside a 5.16% policy rate, the Tuition page had schools that cost
           nothing to run, and the Household cash flow's tier table showed every
           pay tier spending $0 in the shops while the grid above it showed
           $216-$1,793 - which was filed separately as finding #4.

           They are carried now, every one, and this is what stops them being
           quietly dropped again. The city below has a bank, schools, clinics,
           mills, mines and the subsidy dial on, because every one of those is
           needed to make one of these readings non-zero in the first place - a
           fixture that cannot tell zero from missing proves nothing.

           AND FIELDS, SINCE 2026-09-13, because "one of everything" stopped
           being true the day the mills started buying crops. Four Textile Mills
           want 3,400 tonnes a month and a city with no farms buys every one of
           them from the world at the ceiling - which in a city already squeezed
           to its income-tax ceiling with a punitive offset on every band is
           enough to take the bank down, and a failed bank pays its savers
           nothing for ever after. That is the SAME failure this fixture's own
           note records from the day the material import bill started being
           charged, from the same cause: a new cost on the sectors, in a city
           deliberately built too poor to absorb one. Four Mixed Farms cover
           three fifths of what the mills eat and the rest is imported, which is
           an ordinary city rather than a broken one.
           ================================================================= */
        System.out.println("\n--- and a freshly loaded city reads what the live one reads ---");

        Game full = new Game(new GameFiles(root.resolve("everything"),
                root.resolve("no-legacy")));
        full.run();
        // THE TREASURY THIS FIXTURE WAS WRITTEN AGAINST (0.7.10): its build list
        // is bought out of cash, and a city founds on D$100M since 0.7.10, not the
        // D$2.5B it assumed - so it is given that, the Wealthy preset's, explicitly.
        full.setCashForTest(Founding.WEALTHY_CASH);
        full.getGovernmentInvestor().spend(-2_000_000);
        full.getLandManager().setOwnedSqFt(full.getLandManager().getOwnedSqFt() + 200_000_000L);
        for (String[] order : new String[][] {
                {"House", "400"}, {"Convenience Store", "2"}, {"Construction Depot", "6"},
                {"Coal Power Plant", "2"}, {"Water Treatment Plant", "2"},
                {"Industrial Bakery", "4"}, {"Mixed Farm", "4"}, {"Iron Mine", "2"},
                {"Steel Foundry", "2"},
                {"Commercial Bank", "1"}, {"Elementary School", "3"},
                {"Walk-in Clinic", "3"}, {"Paved Road", "20"} }) {
            /*
             * STANDING ON MONTH ONE since 2026-09-10. Queued, this list is a
             * 437,000-point backlog behind two power plants, and what that
             * backlog does is the interesting part of a different test: the
             * builders expand into it, the landlord borrows against it, and
             * when the material import bill started being charged the bank
             * had failed three times by month 122 and paid its savers
             * nothing for ever after. This section is about what a SAVE
             * carries, and a city with one of everything standing is the
             * fixture it always meant to be.
             */
            full.buildStack(template(full, order[0]), Integer.parseInt(order[1]), true);
        }
        for (String sector : Sectors.KEYS) full.setAutoSubsidised(sector, true);

        /*
         * AND SOMEBODY HAS TO BE HUNGRY, WHICH NOW TAKES DOING.
         *
         * getHungerRate() is the share of people who could not afford their
         * subsistence basket, and until 2026-09-09 this city produced one for
         * free: an unskilled wage of $800 a month against a $300 basket left
         * nothing over once rent was paid. The rebalance put the ladder on real
         * Job Bank medians, and a city where the lowest full-time wage is
         * $3,460 does not go hungry - which is the right answer and the end of
         * this reading as a free fixture.
         *
         * IT TAKES BOTH HALVES OF THE READING, and finding that out was the
         * useful part. getHungerRate() has two doors into it - a household with
         * no money and a city with no stock - and on its own neither one opens
         * any more. Cutting the shops to a sixth failed because the retail
         * planner simply builds more, the same way the housing planner defeated
         * HousingCheck's studio city on the same day; taxing take-home to the
         * ceiling failed because 40% of a living wage still buys a basket. Two
         * shops AND the tax ceiling together do it: the squeeze leaves the
         * retail sector too poor to build its way out, and the missing stock is
         * what the households then cannot eat.
         *
         * So the hunger is CAUSED, from both directions at once: two
         * Convenience Stores for four hundred houses, and the income tax at its
         * own ceiling with the maximum punitive offset on every wage band. That is not a balance
         * proposal and is not meant to be - it is the fixture making the
         * reading non-zero so that the SAVE of it can be tested, which is the
         * only thing this section is about. A fixture that cannot tell zero
         * from missing proves nothing, as the note above says.
         *
         * It took two goes. A flat 78% was the first attempt and did nothing,
         * because setIncomeTaxRate() clamps at MAX_INCOME_TAX - 60%, "above
         * this, income tax stops being a policy and starts being confiscation"
         * - and 40% of a living wage still buys a basket. The ceiling is read
         * from the constant now rather than guessed past.
         */
        /*
         * ...AND THE SQUEEZE GOES ON LAST, WHICH IT DID NOT UNTIL 2026-09-13.
         *
         * The two conditions were fighting each other and the tenth sector is
         * what made them lose. The squeeze exists to cause HUNGER and it does;
         * what it also does is make every sector in the city too poor to
         * borrow, and a bank with no borrowers earns nothing, and a bank that
         * earns nothing pays its savers nothing - chooseDepositRate() never
         * pays out more than leaves the bank at net zero (and until 0.7.7 it
         * paid a share of the month's INTEREST INCOME). So "somebody is
         * hungry" and "savers are being paid"
         * were being asked of the same city at the same moment, and one of them
         * is the other's opposite.
         *
         * It held anyway while the mills made food out of nothing, because a
         * sector with no input bill stays rich through any squeeze. Give them a
         * crop to buy and it stops holding: measured at month 391, the bank had
         * failed once, sat on $5k of equity against a $3.1m book, and had paid
         * nothing for two hundred months. That is the same failure this
         * fixture's own note records from the day the material import bill
         * started being charged, from the same cause.
         *
         * So each condition is caused in an order where they do not fight. The
         * city is run healthy until the bank is profitable and paying, and only
         * then is it squeezed - the deposit rate is set off a month's income
         * and survives the few months it takes the shelves to empty. Both
         * readings are transient and both are read at the save, which is all
         * this section is about.
         */
        full.simulateMonths(150);

        for (int extra = 0; extra < 240 && full.getBank().depositRate() <= 0; extra++) {
            full.simulateMonths(1);
        }
        assertTrue("fixture: the bank opened and started paying its savers",
                full.getBank().depositRate() > 0);

        TaxPolicy squeeze = full.getEconomyManager().getTaxPolicy();
        squeeze.setIncomeTaxRate(TaxPolicy.MAX_INCOME_TAX);
        for (WageBand band : WageBand.values()) squeeze.setWageOffset(band, 1);
        // ...and the clinic's price and premium off their defaults, so a
        // reload that forgot either would show (2026-09-19).
        squeeze.setHealthFeeScale(1.75);
        squeeze.setHealthPremiumRate(.02);
        // A month at the new dials, so the service has charged at the scale
        // and the premium has been collected once, whatever the loop below
        // still has to wait for.
        full.simulateMonths(1);

        /*
         * ...AND THEN UNTIL THE BANK IS ACTUALLY OPEN.
         *
         * The Commercial Bank above is ordered with the rest and goes into the
         * same construction queue as four hundred houses, so when it opens is
         * a function of how fast this particular city builds. At 150 months it
         * opened in the same month the fixture stopped, which is not a fixture,
         * it is a coincidence - the housing pass moved the city by a month and
         * "savers really were being paid something" started failing, on a test
         * about SAVE FILES.
         *
         * Rolled forward until the condition the assertions need is true, with
         * a bound so a city that never opens one fails loudly rather than
         * hanging. Same fix LabourCheck's arrivals month got, for the same
         * reason: a fixture has to CAUSE the condition under test.
         *
         * IT WAITS FOR THE HUNGER TOO NOW. Both readings are set inside a tick
         * and both are transient, and the rebalance moved the month each one
         * lands in three separate times in one night - once for the wage
         * ladder, once for the building costs and once for the endowment. A
         * loop that waits for the state is the only version of this fixture
         * that survives somebody moving a price.
         *
         * AND FOR THE DIAL TO PAY, since the crews started drawing material
         * as they build (2026-09-11): the subsidy is this month's figure,
         * and the sector that used to run a loss in the month the loop
         * stopped was construction, buying an order's whole material the
         * day it was placed. It no longer does; some sector still loses
         * money in some month, and the loop waits for that month.
         */
        /*
         * ...AND FOR THE SHOPS TO FALL SHORT AT THE PRICE (0.7.43): groceries
         * are sold as baskets wanted at a price now, and a city whose shelf
         * has climbed to what clears it can hand over every basket asked for
         * while its poorest are priced out - hungry, with the delivered share
         * at one. The share is read below, so the loop waits for it too.
         */
        /*
         * ...AND A MOVE OF THE PLAYER'S IN EVERY MONTH IT WAITS (0.7.102): a
         * thousand of reserves bought before each month, so the month the
         * loop stops on has a line in the treasury's journal to lose. Its
         * repairs were that line until Jerus's A15 made them a budget line;
         * a fixture has to cause what it tests. At least one month, so the
         * month after the dials is never the one read without it.
         *
         * ...AND THE SHOPS' SHELF AT HALF WHAT WAS ASKED FOR (0.7.102), for
         * the same reason: once 0.7.102's books moved this city its shops
         * handed over every basket asked for in every one of the 240 months
         * the loop may wait (a probe, runs/fixP3-notes.md), so "the share the
         * shops handed over" would cross the save at the whole - the value a
         * save that forgot it loads with. Half of last month's ask on the
         * shelf before each waited month makes the month the loop stops on
         * one the shops fell short in.
         */
        int extraMonths = 0;
        do {
            full.buyForeignCurrency(1);
            ham.citybuildersim.sectors.Retail shops = full.getSectors().retail();
            shops.setStoreInventory((int) Math.min(Integer.MAX_VALUE, shops.getHouseholdWant() / 2));
            full.simulateMonths(1);
        } while (++extraMonths < 240
                && (full.getHealth().getHungerRate() <= 0
                    || full.getTotalSubsidyPaid() <= 0
                    || full.getBank().depositRate() <= 0
                    || full.getHouseholdBalance().getDeliveredShare() >= 1));

        // The treasury's rollover (0.7.13), set after the city has played to
        // the one setting no city founds with, so the round trip can fail.
        full.setRolloverMode(Rollover.Mode.TWELVE_MONTH_BILL);
        // ...and a refinery's crude mix no city founds with (0.7.79): a quarter light, half medium, a quarter heavy.
        double[] crudeMix = { .25, .5, .25 };
        full.getSectors().refining().setCrudeMixForTest(crudeMix);
        // ...and the spread planner's idle months (0.7.82), which no city founds with: two kinds idle, the rest working.
        full.getSectors().refining().planner().setIdleMonthsForTest("ASPHALT", 4);
        full.getSectors().refining().planner().setIdleMonthsForTest("COKER", 9);
        // ...and the wells' vintages (0.7.84), which no city founds with: two batches, struck as given.
        java.util.List<ham.citybuildersim.sectors.Oil.Vintage> vintages = java.util.List.of(
                new ham.citybuildersim.sectors.Oil.Vintage(full.getMonth() - 30, 3, ham.citybuildersim.sectors.Oil.WellKind.LAND),
                new ham.citybuildersim.sectors.Oil.Vintage(full.getMonth() - 5, 1, ham.citybuildersim.sectors.Oil.WellKind.LAND));
        full.getSectors().oil().setVintagesForTest(vintages);
        // ...and the oil at sea's records (0.7.91), which no city founds with: two platforms, one on no field, and a pipe.
        java.util.List<ham.citybuildersim.sectors.Oil.Platform> seaPlatforms = java.util.List.of(
                new ham.citybuildersim.sectors.Oil.Platform(4_242, 3, 7, full.getMonth() - 40),
                new ham.citybuildersim.sectors.Oil.Platform(-1, -1, 0, full.getMonth() - 2));
        java.util.List<ham.citybuildersim.sectors.Oil.Pipeline> seaPipes = java.util.List.of(
                new ham.citybuildersim.sectors.Oil.Pipeline(4_242, 3, 17, full.getMonth() - 30));
        full.getSectors().oil().setOffshoreForTest(seaPlatforms, seaPipes);
        // ...and the strategic reserve (0.7.85), which no city founds with: crude, a book, a release standing and a
        // month the strike has yet to settle. (The refiners' crude kept in a Tank Farm crosses a save in OilCheck 15:
        // it is a sector's stock, struck into what the bank reads at the close, so it cannot be handed in here.)
        StrategicReserve.State reserveState = new StrategicReserve.State();
        reserveState.tonnes = 12_345.25; reserveState.cost = 7_777.125; reserveState.release = 1_000;
        reserveState.boughtHome = 11.5; reserveState.boughtAbroad = 22.25; reserveState.soldHome = 33.75;
        reserveState.soldAbroad = 44.5; reserveState.settledImports = 55.25; reserveState.settledExports = 66.75;
        full.getReserve().restore(reserveState);
        // ...and the ports' month (0.7.86), which no city founds with: shares in force, a kind after the railway,
        // crude in a VLCC, the road's two shares, and the month's tonnes by kind and direction.
        Ports.State portState = new Ports.State();
        portState.share = new double[] { .25, .5, .125, 1 };
        portState.first = new boolean[] { true, true, false, true };
        portState.firstOfStream = new double[] { 0, .0625, .375 };
        portState.seaOfStream = new double[] { 0, .125, .5 };
        portState.seaIn = new double[] { 1_000.5, 2_000.25, 3_000.125, 4_000.75 };
        portState.seaOut = new double[] { 5_000.5, 6_000.25, 7_000.125, 8_000.75 };
        portState.tradeIn = new double[] { 10_000.5, 20_000.25, 30_000.125, 40_000.75 };
        portState.tradeOut = new double[] { 50_000.5, 60_000.25, 70_000.125, 80_000.75 };
        portState.crudeShip = "VLCC";
        portState.billedCrudeShip = "SUEZMAX";
        portState.heldBack = 123.5; portState.crudeSeaIn = 900.25; portState.crudeSeaOut = 100.125; portState.crudeTrade = 9_999.5;
        full.getPorts().restore(portState);

        assertTrue("saved a city with one of everything in it",
                full.saveGame(1, "everything").ok);

        Game back = new Game(full.getGameFiles());
        back.loadGameSave(1);

        /* --- the fixture has to be able to fail before it can pass --- */
        assertTrue("fixture: savers really were being paid something",
                full.getBank().depositRate() > 0);
        assertTrue("fixture: the schools really were running",
                full.getEducation().getPayroll() > 0);
        assertTrue("fixture: the dial really did pay out",
                full.getTotalSubsidyPaid() > 0);
        assertTrue("fixture: somebody really was hungry",
                full.getHealth().getHungerRate() > 0);
        assertTrue("fixture: the tiers really were shopping",
                full.getHouseholds().getRowShopping(0) > 0);

        /*
         * THE CITY'S REPAIR BILL, added 2026-09-09 when every building in the
         * city started paying to stand. It is struck inside the tick and is
         * exactly the shape this section exists for: the standing stock says
         * what the bill WOULD be, not what was settled, so a reloaded city that
         * rebuilt it from the buildings would disagree with the one that paid
         * it for a month after anything was put up or pulled down.
         */
        assertTrue("fixture: the city really was paying for repairs",
                full.getCityMaintenancePaid() > 0);
        same("what the city paid to keep its buildings up",
                back.getCityMaintenancePaid(), full.getCityMaintenancePaid());
        // ...a line of the budget since 0.7.102 (A15), on the government block's end: it crosses with it.
        same("...the budget's repairs line, which is what it paid",
                back.getEconomyManager().getNationalAccounts().getCityRepairs(),
                full.getEconomyManager().getNationalAccounts().getCityRepairs());
        assertTrue("...and the line is the bill it paid, not nothing",
                full.getEconomyManager().getNationalAccounts().getCityRepairs() == full.getCityMaintenancePaid());
        same("...so the budget's spending, with it, is the same", back.getEconomyManager().getNationalAccounts().getTotalExpenses(),
                full.getEconomyManager().getNationalAccounts().getTotalExpenses());

        /*
         * THE INVESTORS' LAST WORD (0.7.102, Jerus's A22): what the month's
         * investment pass decided for each sector and why, written inside
         * the tick - the Sectors screen's investors' line. Not saved until
         * now, so a reloaded city read "nothing recorded" until it ticked.
         */
        assertTrue("fixture: the investors left a word on some sector",
                full.getLastInvestments().values().stream().anyMatch(w -> !w.isEmpty()));
        assertEquals("the investors' last word survives a save, slot for slot, word for word",
                back.getLastInvestments(), full.getLastInvestments());

        same("what savers are paid", back.getBank().depositRate(),
                full.getBank().depositRate());

        // How the city was founded (0.7.10), read through the game's getters.
        assertEquals("the founding record reads the same: the name", back.getCityName(), full.getCityName());
        assertEquals("...the money", back.getCurrency(), full.getCurrency());
        same("...the treasury it was founded with", back.getFoundingCash(), full.getFoundingCash());
        same("...the vault", back.getFoundingReserveUsd(), full.getFoundingReserveUsd());
        same("...and the world it was founded into", back.getFounding().getMeanInflation(),
                full.getFounding().getMeanInflation());
        assertEquals("...and the ground it stands on (0.7.56)", back.getWorldSeed(), full.getWorldSeed());
        assertTrue("the refinery's crude mix, light, medium and heavy (0.7.79)",
                java.util.Arrays.equals(back.getSectors().refining().getCrudeMix(), crudeMix));
        boolean idle = true;
        for (ham.citybuildersim.sectors.RefineryFlow.Kind k : ham.citybuildersim.sectors.RefineryFlow.Kind.values()) {
            idle &= back.getSectors().refining().idleMonths(k) == full.getSectors().refining().idleMonths(k);
        }
        assertTrue("the refiners' idle months by kind, the spread planner's (0.7.82)", idle
                && back.getSectors().refining().idleMonths(ham.citybuildersim.sectors.RefineryFlow.Kind.COKER) == 9);
        assertTrue("the wells' vintages, each batch's month, count and kind (0.7.84)",
                back.getSectors().oil().vintages().equals(vintages));
        assertTrue("the oil at sea's platforms - each one's field, wells and month - and pipelines, each one's field,"
                        + " kilometres and month (0.7.91)",
                back.getSectors().oil().platforms().equals(seaPlatforms) && back.getSectors().oil().pipelines().equals(seaPipes));
        // ...and the strategic reserve (0.7.85).
        StrategicReserve resThen = full.getReserve(), resNow = back.getReserve();
        assertTrue("fixture: the reserve really did hold crude, with a release standing and a month to settle",
                resThen.getTonnes() > 0 && resThen.getRelease() > 0 && resThen.getSoldAbroad() > 0);
        same("the strategic reserve's crude (0.7.85)", resNow.getTonnes(), resThen.getTonnes());
        same("...its book", resNow.getCost(), resThen.getCost());
        same("...its release", resNow.getRelease(), resThen.getRelease());
        same("...what it bought at home and abroad, for the strike", resNow.getBoughtHome() + resNow.getBoughtAbroad(),
                resThen.getBoughtHome() + resThen.getBoughtAbroad());
        same("...what it sold and shipped", resNow.getSoldHome() + resNow.getSoldAbroad(),
                resThen.getSoldHome() + resThen.getSoldAbroad());
        same("...and what the last strike settled abroad", resNow.getSettledImports() + resNow.getSettledExports(),
                resThen.getSettledImports() + resThen.getSettledExports());
        // ...and the ports' month (0.7.86): the band and the road are struck from it on the load, the boats from its tonnes.
        Ports portThen = full.getPorts(), portNow = back.getPorts();
        boolean portSame = portThen.anyAtSea() && portNow.crudeShip() == Ports.Ship.VLCC
                && portNow.billedCrudeShip() == Ports.Ship.SUEZMAX && portNow.getHeldBack() == portThen.getHeldBack()
                && portNow.crudeSeaIn() == portThen.crudeSeaIn() && portNow.crudeSeaOut() == portThen.crudeSeaOut()
                && portNow.crudeTrade() == portThen.crudeTrade()
                && java.util.Arrays.equals(portNow.seaOfStream(), portThen.seaOfStream());
        for (Ports.Cargo c : Ports.Cargo.values()) {
            portSame &= portNow.share(c) == portThen.share(c) && portNow.seaFirst(c) == portThen.seaFirst(c)
                    && portNow.seaIn(c) == portThen.seaIn(c) && portNow.seaOut(c) == portThen.seaOut(c)
                    && portNow.tradeIn(c) == portThen.tradeIn(c) && portNow.tradeOut(c) == portThen.tradeOut(c);
        }
        assertTrue("the ports' month: each kind's share in force and whether it goes before the railway, crude's class,"
                + " the road's shares, and the month's tonnes by kind and direction (0.7.86)", portSame);
        // ...and the forecourts' month (0.7.83): the planner reads its litres next month, the page its prices.
        ham.citybuildersim.sectors.Retail pumpThen = full.getSectors().retail(), pumpNow = back.getSectors().retail();
        assertTrue("fixture: the forecourts really were selling the drivers' petrol",
                pumpThen.getPumpLitres() + pumpThen.getQueueLitres() > 0 && pumpThen.getFuelBill() > 0);
        same("the forecourts' litres at the pump (0.7.83)", pumpNow.getPumpLitres(), pumpThen.getPumpLitres());
        same("...past the stations", pumpNow.getQueueLitres(), pumpThen.getQueueLitres());
        same("...what the stations could sell", pumpNow.getPumpCapacity(), pumpThen.getPumpCapacity());
        same("...the pump price", pumpNow.getPumpPrice(), pumpThen.getPumpPrice());
        same("...the queue's", pumpNow.getQueuePrice(), pumpThen.getQueuePrice());
        same("...the wholesale a litre", pumpNow.getWholesaleLitre(), pumpThen.getWholesaleLitre());
        same("...the drivers' bill", pumpNow.getFuelBill(), pumpThen.getFuelBill());
        same("...and what of the wholesale the world was paid", pumpNow.getFuelImported(), pumpThen.getFuelImported());

        /*
         * WHAT A LOAN COSTS, STRUCK AT THE CLOSE (0.7.7). The bank prices
         * every loan from four parts, two of them struck when the month
         * closed from flows no reloaded city can re-read - the share of its
         * funding at the window and a trailing year of running costs - so
         * the save carries the parts and the year of costs,
         * and a reloaded city quotes the prime the live one does. And the
         * profit the bank booked after its close, which next month's tax
         * and dividend count, is a flow with the same problem.
         */
        double dial = full.getDebtManager().getPolicyRate();
        assertTrue("fixture: the bank really had a price with every part in it",
                full.getBank().runningCostRate() > 0 && full.getBank().expectedLossRate() > 0
                        && full.getBank().capitalCharge(dial, Bank.PRIME_TERM_MONTHS, Bank.RISK_BUSINESS) > 0);
        same("the bank's prime", back.getBank().prime(dial), full.getBank().prime(dial));
        same("...what a household pays it", back.getBank().householdRate(dial),
                full.getBank().householdRate(dial));
        same("...what the carry trade is lent at", back.getBank().carryRate(dial),
                full.getBank().carryRate(dial));
        same("...its running costs per dollar lent", back.getBank().runningCostRate(),
                full.getBank().runningCostRate());
        same("...what it expects to lose", back.getBank().expectedLossRate(),
                full.getBank().expectedLossRate());
        same("...and how much of its money came from the window", back.getBank().windowShare(),
                full.getBank().windowShare());
        double[] recordLived = full.getBank().pricingHistoryToSave();
        double[] recordBack = back.getBank().pricingHistoryToSave();
        boolean recordSame = recordLived.length == recordBack.length;
        for (int i = 0; recordSame && i < recordLived.length; i++) {
            recordSame = Math.abs(recordLived[i] - recordBack[i]) < 1e-9;
        }
        assertTrue("its year of costs came back whole", recordSame);
        same("the profit it booked after its close", back.getBank().lateProfit(),
                full.getBank().lateProfit());
        /*
         * WHAT IT SET ASIDE, THE TARGET IT CHOSE AND ITS MONTH (0.7.8). The
         * allowance is a stock the month's provision struck from borrowers as
         * they stood then; the target is struck from a record of provisions
         * no end of month can rebuild; and the month's statement lines are
         * flows - a reloaded Income page read zeroes until a month was played.
         */
        Bank livedBank = full.getBank(), backBank = back.getBank();
        assertTrue("fixture: the bank really had set something aside", livedBank.getAllowance() > 0);
        same("what the bank has set aside against its loans", backBank.getAllowance(), livedBank.getAllowance());
        /*
         * THE QUARTER (0.7.8): the readings the bank rates a sector on are
         * saved by name - so every sector's quarter and the borrower's own
         * risk a new loan is priced at come back as they were. (Not the whole
         * quote: prime is struck again on the load path before the bank is
         * back, and next month's top prices it afresh.)
         */
        BusinessDebtManager liveCredit = full.getEconomyManager().getBusinessDebtManager();
        BusinessDebtManager backCredit = back.getEconomyManager().getBusinessDebtManager();
        for (String k : liveCredit.sectors()) {
            same("  " + k + ": its quarter's debt, as the bank reads it", backCredit.quarterPrincipal(k), liveCredit.quarterPrincipal(k));
            same("  ...and its assets", backCredit.quarterAssets(k), liveCredit.quarterAssets(k));
            same("  ...and the risk its next loan is priced at, over prime", backCredit.getRiskSpread(k), liveCredit.getRiskSpread(k));
        }
        /*
         * THE LANDLORDS' MORTGAGES (0.7.11): each one whole - its balance, the
         * rate it was written at for its term, the term and the amortization
         * left - so the next payment and the next renewal are the live
         * city's; and the flows and records no reloaded city can re-read: the
         * principal the month's payments took, the insurance's premiums and
         * claims over the city's life, and the budget's two lines.
         */
        String landlords = Sectors.REAL_ESTATE;
        assertTrue("fixture: the city's landlords owe insured mortgages, and paid them down this month",
                !liveCredit.getMortgages(landlords).isEmpty() && liveCredit.getMortgageRepaidThisMonth(landlords) > 0);
        same("what the landlords owe on their mortgages", backCredit.getMortgagePrincipal(landlords),
                liveCredit.getMortgagePrincipal(landlords));
        same("...at the rate each was written at for its term", backCredit.getMortgageRate(landlords),
                liveCredit.getMortgageRate(landlords));
        same("...their next payment", backCredit.getMortgagePayment(landlords), liveCredit.getMortgagePayment(landlords));
        same("...their next renewal", backCredit.getNextRenewalMonth(landlords), liveCredit.getNextRenewalMonth(landlords));
        same("...the principal the month's payments took", backCredit.getMortgageRepaidThisMonth(landlords),
                liveCredit.getMortgageRepaidThisMonth(landlords));
        same("...the premiums the insurance has taken over the city's life", backCredit.getPremiumsTotal(),
                liveCredit.getPremiumsTotal());
        same("...and the claims it has paid", backCredit.getInsuredWrittenOffTotal(), liveCredit.getInsuredWrittenOffTotal());
        same("...the budget's premium line", back.getEconomyManager().getNationalAccounts().getMortgagePremiums(),
                full.getEconomyManager().getNationalAccounts().getMortgagePremiums());
        same("...and its claims line", back.getEconomyManager().getNationalAccounts().getMortgageClaims(),
                full.getEconomyManager().getNationalAccounts().getMortgageClaims());
        same("...and the bank's book of them", back.getBank().getMortgageBook(), full.getBank().getMortgageBook());
        /*
         * THE BUSINESSES' BONDS (0.7.12): every bond whole - its face, its
         * coupon, its term and who holds it - and the orders resting on its
         * book; each cell's own bonds and what a unit of them is worth this
         * month, which their plan reads; the bank's bonds and its book's
         * concentration, struck again on the load path from the market; the
         * month's flows the Bonds page, the Trade tab and the sector screen
         * read; and the lender's record of what bondholders lost.
         */
        BondMarket liveBonds = full.getBondMarket(), backBonds = back.getBondMarket();
        assertTrue("fixture: the city's businesses owe bonds, and orders rest on their books",
                !liveBonds.getBonds().isEmpty() && liveBonds.getLifeIssues() > 0);
        same("the bonds outstanding", backBonds.getBonds().size(), liveBonds.getBonds().size());
        same("...their face", backBonds.totalFace(), liveBonds.totalFace());
        same("...the households' of it", backBonds.faceHeldByHouseholds(), liveBonds.faceHeldByHouseholds());
        same("...the bank's", backBonds.faceHeldByBank(), liveBonds.faceHeldByBank());
        same("...the companies'", backBonds.faceHeldByCompanies(), liveBonds.faceHeldByCompanies());
        same("...the world's", backBonds.faceHeldByWorld(), liveBonds.faceHeldByWorld());
        same("...their coupon, weighted", backBonds.averageCoupon(), liveBonds.averageCoupon());
        double liveOrders = 0, backOrders = 0;
        for (CorporateBond b : liveBonds.getBonds()) {
            liveOrders += liveBonds.bookOf(b).depth(OrderBook.Side.BUY, 1e-9) + liveBonds.bookOf(b).depth(OrderBook.Side.SELL, 1e9);
            CorporateBond t = backBonds.bond(b.id());
            if (t != null) backOrders += backBonds.bookOf(t).depth(OrderBook.Side.BUY, 1e-9) + backBonds.bookOf(t).depth(OrderBook.Side.SELL, 1e9);
        }
        same("...the orders resting on their books", backOrders, liveOrders);
        same("the households' bonds", back.getHouseholdBalance().totalBonds(), full.getHouseholdBalance().totalBonds());
        // ...each cell's own, bond by bond, by the cell's name (0.7.12 round 2).
        int cellsWithBonds = 0, cellsBack = 0;
        for (Household c : full.getHouseholdBalance().cells()) {
            if (c.bondFace.isEmpty()) continue;
            cellsWithBonds++;
            Household t = back.getHouseholdBalance().cellByKey(c.key());
            if (t != null && t.bondFace.equals(c.bondFace)) cellsBack++;
        }
        same("...each cell's own, bond by bond, by its name (" + cellsWithBonds + " cells)", cellsBack, cellsWithBonds);
        // ...and the shares' books (round 2): the last trade, fair value and what rests.
        Exchange liveEx = full.getExchange(), backEx = back.getExchange();
        double livePrices = 0, backPrices = 0, liveResting = 0, backResting = 0;
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            livePrices += liveEx.price(c) + liveEx.fair(c);
            backPrices += backEx.price(c) + backEx.fair(c);
            liveResting += liveEx.bookOf(c).depth(OrderBook.Side.BUY, 1e-12) + liveEx.bookOf(c).depth(OrderBook.Side.SELL, 1e12);
            backResting += backEx.bookOf(c).depth(OrderBook.Side.BUY, 1e-12) + backEx.bookOf(c).depth(OrderBook.Side.SELL, 1e12);
        }
        same("the shares' last trades and fair values", backPrices, livePrices);
        same("...and the orders resting on their books", backResting, liveResting);
        same("...and what a unit of them is worth this month", back.getHouseholdBalance().getBondRatio(),
                full.getHouseholdBalance().getBondRatio());
        same("the bank's bonds, at what they cost it", back.getBank().getBondBook(), full.getBank().getBondBook());
        same("...weighed as loans", back.getBank().getBondWeighted(), full.getBank().getBondWeighted());
        same("...and the capital its book's concentration adds", back.getBank().getConcentrationAddOn(),
                full.getBank().getConcentrationAddOn());
        same("the month's issues, for the Bonds page", backBonds.getIssuedFace(), liveBonds.getIssuedFace());
        same("...the coupons paid abroad, for the Trade tab", backBonds.getCouponsAbroad(), liveBonds.getCouponsAbroad());
        same("...the world's purchases", backBonds.getWorldPurchases(), liveBonds.getWorldPurchases());
        same("...last month's book", backBonds.getLastPostedSell(), liveBonds.getLastPostedSell());
        same("...and over the city's life, the coupons the households were paid", backBonds.getLifeCouponsHouseholds(),
                liveBonds.getLifeCouponsHouseholds());
        for (String k : liveCredit.sectors()) {
            same("  " + k + ": its bonds", backCredit.getBondPrincipal(k), liveCredit.getBondPrincipal(k));
            same("  ...what bondholders have lost on them", backCredit.getBondWrittenOffTotal(k), liveCredit.getBondWrittenOffTotal(k));
            same("  ...its month's bond lines on the sector screen", backBonds.getIssued(k) + backBonds.getCouponsTo(k)
                    + backBonds.getBoughtNet(k), liveBonds.getIssued(k) + liveBonds.getCouponsTo(k) + liveBonds.getBoughtNet(k));
        }
        // ...and round 2's: the months its book has not kept its branches'
        // staff (Bank.lastMonthToSave(); MortgageCheck section 11 saves a
        // city with the streak running), and what its leverage ratio reads.
        same("...how long the bank's book has not kept its branches' staff",
                back.getBank().getUncoveredMonths(), full.getBank().getUncoveredMonths());
        same("...and its capital against everything it has lent", back.getBank().leverageRatio(),
                full.getBank().leverageRatio());
        same("the builders' salvage at what they paid for it", back.getSectors().construction().getSalvageCost(),
                full.getSectors().construction().getSalvageCost());
        same("...the month's provision", backBank.provisions(), livedBank.provisions());
        same("...the capital target it chose", backBank.capitalTarget(), livedBank.capitalTarget());
        same("...its interest income, on the reloaded Profit page", backBank.interestIncome(), livedBank.interestIncome());
        same("...its fees", backBank.feeIncome(), livedBank.feeIncome());
        same("...its profit before tax", backBank.profitBeforeTax(), livedBank.profitBeforeTax());
        same("...what it kept", backBank.getNetIncome(), livedBank.getNetIncome());
        same("...what it paid its owners, this month and over the year", backBank.getDividendsPaid()
                + backBank.dividendsOverYear(), livedBank.getDividendsPaid() + livedBank.dividendsOverYear());
        same("...and the equity it opened the month with", backBank.getOpeningEquity(), livedBank.getOpeningEquity());
        for (String record : new String[]{ "lines", "capital" }) {
            double[] a = record.equals("lines") ? livedBank.monthLinesToSave() : livedBank.capitalRecordToSave();
            double[] b = record.equals("lines") ? backBank.monthLinesToSave() : backBank.capitalRecordToSave();
            boolean whole = a.length == b.length;
            for (int i = 0; whole && i < a.length; i++) whole = Math.abs(a[i] - b[i]) < 1e-9;
            assertTrue("its month's " + record + (record.equals("lines") ? "" : " record") + " came back whole", whole);
        }
        java.util.Map<String, double[]> booksLived = livedBank.allowanceToSave(), booksBack = backBank.allowanceToSave();
        boolean booksWhole = booksLived.keySet().equals(booksBack.keySet());
        for (String k : booksLived.keySet()) {
            if (!booksWhole) break;
            booksWhole = java.util.Arrays.equals(booksLived.get(k), booksBack.get(k));
        }
        assertTrue("...and its allowance, book by book", booksWhole);
        /*
         * ...AND ITS YEAR OF STATEMENTS (0.7.9). The Bank tab's last-month
         * column and its last twelve months are read from months that no end
         * of month can give back; so is the interest by who paid it, which
         * rides the month's lines above.
         */
        assertTrue("fixture: the bank had last month on file", livedBank.knowsLastMonth());
        same("last month's profit, beside this month's, on the reloaded Bank tab",
                backBank.lastMonth(Bank.Line.NET), livedBank.lastMonth(Bank.Line.NET));
        same("...the last twelve months' interest", backBank.overYear(Bank.Line.INTEREST),
                livedBank.overYear(Bank.Line.INTEREST));
        same("...this month's interest from the businesses", backBank.getInterestFromBusinesses(),
                livedBank.getInterestFromBusinesses());
        assertTrue("...and its year of statements came back whole",
                java.util.Arrays.equals(backBank.statementYearToSave(), livedBank.statementYearToSave()));
        /*
         * ...AND ITS BALANCE SHEET A YEAR BACK (0.7.13), which the Balance
         * sheet page's second column reads: a stock twelve months gone that
         * no end of month can give back.
         */
        assertTrue("fixture: the bank had a year of balance sheets on file", livedBank.knowsYearAgo());
        same("its equity a year ago, on the reloaded Balance sheet page",
                backBank.yearAgo(Bank.Sheet.EQUITY), livedBank.yearAgo(Bank.Sheet.EQUITY));
        same("...what the businesses owed it then", backBank.yearAgo(Bank.Sheet.BUSINESS_LOANS),
                livedBank.yearAgo(Bank.Sheet.BUSINESS_LOANS));
        assertTrue("...and the year of sheets came back whole",
                java.util.Arrays.equals(backBank.sheetYearToSave(), livedBank.sheetYearToSave()));
        same("...and this month's sheet leaves the same unexplained", backBank.sheetResidual(),
                livedBank.sheetResidual());
        /*
         * ...AND ITS EQUITY IN TWO PARTS (0.7.13, round 2): two counters no
         * end of month can give back, saved by name at the top of the month,
         * the month's own causes riding its statement lines.
         */
        assertTrue("fixture: the bank keeps its equity in two parts", livedBank.knowsEquitySplit());
        same("its paid-in capital, on the reloaded Balance sheet page", backBank.paidInCapital(),
                livedBank.paidInCapital());
        same("...its retained earnings", backBank.retainedEarnings(), livedBank.retainedEarnings());
        same("...and the two still add up to its equity", backBank.equitySplitResidual(),
                livedBank.equitySplitResidual());
        /*
         * ...AND THE TREASURY'S ROLLOVER (0.7.13): the setting by name, the
         * ledger of what it netted and its record. This city borrows nothing,
         * so its ledger is empty; TreasuryCheck section 7 round-trips one
         * with months in it.
         */
        assertTrue("the rollover's setting survives a save",
                back.getRolloverMode() == Rollover.Mode.TWELVE_MONTH_BILL);
        assertTrue("...its ledger and its record with it",
                java.util.Arrays.equals(back.getRollover().ledgerToSave(), full.getRollover().ledgerToSave())
                        && java.util.Arrays.equals(back.getRollover().recordToSave(), full.getRollover().recordToSave()));
        same("...and what a reloaded city says falls due next month", back.rolloverPlan().due(),
                full.rolloverPlan().due());
        same("...and the year's surplus it would net from", back.surplusOverLastYear(), full.surplusOverLastYear());
        // ...and the account fee in the households' books, a line of its own.
        assertTrue("fixture: the households really paid account fees",
                full.getHouseholds().getAccountFees() > 0);
        same("what the households paid in account fees", back.getHouseholds().getAccountFees(),
                full.getHouseholds().getAccountFees());
        for (int r = 0; r < full.getHouseholds().getRowCount(); r++) {
            same("tier " + r + " account fees", back.getHouseholds().getRowAccountFees(r),
                    full.getHouseholds().getRowAccountFees(r));
        }
        same("what the dial paid out", back.getTotalSubsidyPaid(),
                full.getTotalSubsidyPaid());
        same("the schools' payroll", back.getEducation().getPayroll(),
                full.getEducation().getPayroll());
        same("...their upkeep", back.getEducation().getUpkeep(),
                full.getEducation().getUpkeep());
        same("...the fees they waived", back.getEducation().getSubsidy(),
                full.getEducation().getSubsidy());
        same("...and the fees they collected", back.getEducation().getFees(),
                full.getEducation().getFees());
        same("the hunger inside the sick rate", back.getHealth().getHungerRate(),
                full.getHealth().getHungerRate());
        // The clinic's price and premium, and what they did (2026-09-19).
        assertTrue("fixture: the health premium really was collected",
                full.getEconomyManager().getHealthPremiums() > 0);
        same("the fee scale", back.getEconomyManager().getTaxPolicy().getHealthFeeScale(),
                full.getEconomyManager().getTaxPolicy().getHealthFeeScale());
        same("...and the health premium", back.getEconomyManager().getTaxPolicy().getHealthPremiumRate(),
                full.getEconomyManager().getTaxPolicy().getHealthPremiumRate());
        same("...the scale the service charges at", back.getHealthcare().getFeeScale(),
                full.getHealthcare().getFeeScale());
        same("...what the premium raised", back.getEconomyManager().getNationalAccounts().getHealthPremiums(),
                full.getEconomyManager().getNationalAccounts().getHealthPremiums());
        same("...what the households paid of it", back.getHouseholds().getHealthPremiums(),
                full.getHouseholds().getHealthPremiums());
        same("...the treatment bill at full service", back.getHealthcare().fullTreatmentFees(),
                full.getHealthcare().fullTreatmentFees());
        /*
         * ...AND THE FEES BY KIND (A3, 0.7.46): each is the people that kind
         * treated times its fee, and the people were not saved, so a reloaded
         * Care page read 0 for every kind under a treatment line carried
         * whole. Added in the order the month adds them, the three are the
         * line - in the live city, and in its reload.
         */
        Healthcare careLive = full.getHealthcare(), careBack = back.getHealthcare();
        double kindsLive = careLive.feesFrom(CareType.GENERAL) + careLive.feesFrom(CareType.CHILDCARE)
                + careLive.feesFrom(CareType.SENIOR);
        double kindsBack = careBack.feesFrom(CareType.GENERAL) + careBack.feesFrom(CareType.CHILDCARE)
                + careBack.feesFrom(CareType.SENIOR);
        assertTrue("fixture: the live city's care charged fees, and they add up to its line",
                careLive.getTreatmentFees() > 0 && careLive.feesFrom(CareType.GENERAL) > 0
                        && Math.abs(kindsLive - careLive.getTreatmentFees()) <= 1e-9 * careLive.getTreatmentFees());
        boolean kindsKept = Math.abs(kindsBack - careBack.getTreatmentFees()) <= 1e-9 * Math.max(1, careBack.getTreatmentFees());
        for (CareType care : new CareType[] { CareType.GENERAL, CareType.CHILDCARE, CareType.SENIOR }) {
            kindsKept &= Math.abs(careBack.feesFrom(care) - careLive.feesFrom(care)) < 1e-9;
        }
        assertTrue("just loaded, care's fees by kind add up to its line as the live city's do", kindsKept);
        for (int r = 0; r < full.getHouseholds().getRowCount(); r++) {
            same("row " + r + " care bill", back.getHouseholds().getRowCareBilled(r),
                    full.getHouseholds().getRowCareBilled(r));
            same("row " + r + " health premium", back.getHouseholds().getRowHealthPremiums(r),
                    full.getHouseholds().getRowHealthPremiums(r));
        }
        for (Household c : full.getHouseholdBalance().cells()) {
            Household again = null;
            for (Household d : back.getHouseholdBalance().cells()) if (d.key().equals(c.key())) again = d;
            if (again == null || c.households() < .5) continue;
            same(c.label() + ": the share who paid for care", again.carePaid(), c.carePaid());
        }
        same("households doubled up", back.getFamilies().getDoubledUpHouseholds(),
                full.getFamilies().getDoubledUpHouseholds());
        same("...and the ones a studio turned away",
                back.getFamilies().getRefusedByStudio(),
                full.getFamilies().getRefusedByStudio());
        for (int r = 0; r < full.getHouseholds().getRowCount(); r++) {
            same("tier " + r + " shopping", back.getHouseholds().getRowShopping(r),
                    full.getHouseholds().getRowShopping(r));
            same("tier " + r + " rent", back.getHouseholds().getRowRent(r),
                    full.getHouseholds().getRowRent(r));
        }
        same("the government's surplus",
                back.getEconomyManager().getNationalAccounts().getBalance(),
                full.getEconomyManager().getNationalAccounts().getBalance());
        same("...and the business tax inside it",
                back.getEconomyManager().getNationalAccounts().getTaxBusiness(),
                full.getEconomyManager().getNationalAccounts().getTaxBusiness());

        /*
         * THE TREASURY'S JOURNAL, since 2026-09-18: last month's non-budget
         * movements by name, which the bridge on the Government tab opens
         * into, and the residual under them. A flow, and a list of them, so
         * a reloaded city cannot rebuild it. This city buys a thousand of
         * reserves before every month the fixture waits (its repairs were the
         * line until 0.7.102 made them a budget line), so the journal has at
         * least one line to lose.
         */
        java.util.List<TreasuryJournal.Entry> lived = full.getTreasuryJournal();
        java.util.List<TreasuryJournal.Entry> loaded = back.getTreasuryJournal();
        assertTrue("fixture: the treasury's journal had lines in it", !lived.isEmpty());
        assertTrue("the treasury's journal has as many lines as it had", loaded.size() == lived.size());
        for (int i = 0; i < Math.min(lived.size(), loaded.size()); i++) {
            assertTrue("...line " + (i + 1) + " is still \"" + lived.get(i).label() + "\"",
                    lived.get(i).label().equals(loaded.get(i).label()));
            same("...and still says " + String.format("%,.2f", lived.get(i).amount()),
                    loaded.get(i).amount(), lived.get(i).amount());
        }
        same("...and what the journal left unexplained",
                back.getTreasuryResidual(), full.getTreasuryResidual());

        /*
         * THE MONTH THE PEOPLE PAGE DRAWS (0.7.27, SAVE_FORMAT 30). Migration's
         * last month, the pyramid's dead by cause, and the two halves of the
         * hunger: flows nothing in the next month reads, so every section
         * above passed without them - and a reloaded People page read "a city
         * this good draws 0", moved in 0, moved out 0, the dead with no cause
         * under them and the shelves full. Each is caused before it is
         * compared.
         */
        Migration drew = full.getMigration(), drawsAgain = back.getMigration();
        assertTrue("fixture: the city drew people and somebody moved this month",
                drew.getLastTarget() > 0 && drew.getLastArrivals() + drew.getLastDepartures() > 0);
        same("the draw the People page shows", drawsAgain.getLastTarget(), drew.getLastTarget());
        same("...its jobs' half", drawsAgain.getLastJobDraw(), drew.getLastJobDraw());
        same("...and its homes' half", drawsAgain.getLastHomeDraw(), drew.getLastHomeDraw());
        same("...the posts it was struck on", drawsAgain.getLastJobs(), drew.getLastJobs());
        same("...the people the homes hold", drawsAgain.getLastHomeCapacity(), drew.getLastHomeCapacity());
        same("...the residents a post supports", drawsAgain.getLastResidentsPerJob(), drew.getLastResidentsPerJob());
        same("...senior care's pull", drawsAgain.getLastSeniorPull(), drew.getLastSeniorPull());
        same("...the rent's", drawsAgain.getLastAffordabilityPull(), drew.getLastAffordabilityPull());
        same("...and crime's", drawsAgain.getLastCrimePull(), drew.getLastCrimePull());
        same("how much of the draw housing let in", drawsAgain.getLastCrowding(), drew.getLastCrowding());
        same("the payroll in trades whose people may leave", drawsAgain.getLastDecliningShare(),
                drew.getLastDecliningShare());
        same("the month's arrivals", drawsAgain.getLastArrivals(), drew.getLastArrivals());
        same("...and its departures", drawsAgain.getLastDepartures(), drew.getLastDepartures());
        same("...the ones the work pushed out", drawsAgain.getLastWorkDepartures(), drew.getLastWorkDepartures());
        same("...the ones who went broke", drawsAgain.getLastBankruptcyDepartures(),
                drew.getLastBankruptcyDepartures());
        same("...and the ones crime drove out", drawsAgain.getLastCrimeDepartures(), drew.getLastCrimeDepartures());
        for (WageBand band : WageBand.values()) {
            same("who arrived: " + band.label(), drawsAgain.getLastArrivalMix()[band.ordinal()],
                    drew.getLastArrivalMix()[band.ordinal()]);
            same("who left: " + band.label(), drawsAgain.getLastDepartureMix()[band.ordinal()],
                    drew.getLastDepartureMix()[band.ordinal()]);
        }
        boolean licencesKept = true;
        for (int j = 0; j < drew.getLastArrivalLicences().length; j++) {
            licencesKept &= Math.abs(drawsAgain.getLastArrivalLicences()[j] - drew.getLastArrivalLicences()[j]) < 1e-9;
        }
        assertTrue("...and which of the arrivals held a licence", licencesKept);

        PopulationCohorts died = full.getCohorts(), diedAgain = back.getCohorts();
        assertTrue("fixture: people died this month", died.getLastDeaths() > 0);
        same("the dead of age", diedAgain.getLastDeathsOfAge(), died.getLastDeathsOfAge());
        same("...the killed", diedAgain.getLastKilled(), died.getLastKilled());
        same("...and the ones who aged out at 120", diedAgain.getLastAgedOut(), died.getLastAgedOut());
        double causes = died.getLastDeathsOfAge() + died.getLastKilled() + died.getLastAgedOut()
                + full.getSickness().getLastDeaths();
        assertTrue("...which with illness's are the month's dead",
                Math.abs(causes - died.getLastDeaths()) <= 1e-9 * Math.max(1, died.getLastDeaths()));

        HouseholdBalance fed = full.getHouseholdBalance(), fedAgain = back.getHouseholdBalance();
        assertTrue("fixture: the shops handed over less than the households planned",
                fed.getDeliveredShare() < 1);
        same("the share the shops handed over", fedAgain.getDeliveredShare(), fed.getDeliveredShare());
        assertTrue("fixture: somebody could not afford a basket even at full shelves",
                fed.getHungryAtFullShelves() > 0);
        same("...the hungry even at full shelves", fedAgain.getHungryAtFullShelves(), fed.getHungryAtFullShelves());
        assertTrue("...who are some of the hungry, never more",
                fed.getHungryAtFullShelves() <= fed.getHungryPeople() + 1e-9);

        /*
         * ...AND A SAVE FROM BEFORE THEM STILL LOADS, with those figures at 0
         * as a reloaded page always showed them - the arrays at the lengths a
         * format-29 build wrote. A length neither build wrote is refused whole.
         */
        int tiers = PayTier.values().length;
        int wageHistory = tiers * Migration.DECLINE_MONTHS + tiers + 1;
        double[] migrationNow = drew.toSaveArray();
        assertTrue("fixture: the migration array is longer than the history it opens with",
                migrationNow.length > wageHistory);
        Migration format29 = new Migration();
        format29.restore(java.util.Arrays.copyOf(migrationNow, wageHistory));
        boolean streaks = format29.hasFullHistory() == drew.hasFullHistory();
        for (PayTier tier : PayTier.values()) streaks &= format29.getDecliningStreak(tier) == drew.getDecliningStreak(tier);
        assertTrue("a format-29 migration array loads its wage history", streaks);
        same("...with the month's draw at 0", format29.getLastTarget(), 0);
        same("...and its arrivals at 0", format29.getLastArrivals(), 0);
        Migration unknown = new Migration();
        unknown.restore(java.util.Arrays.copyOf(migrationNow, wageHistory + 1));
        assertTrue("...and a length it does not know is refused whole",
                !unknown.hasFullHistory() && unknown.getLastTarget() == 0);
        double[] pyramidNow = died.toSaveArray();
        PopulationCohorts olderPyramid = new PopulationCohorts();
        olderPyramid.restore(PopulationCohorts.saveBands(),
                java.util.Arrays.copyOf(pyramidNow, AgeBand.values().length + 3));
        same("a format-29 pyramid loads its people", olderPyramid.total(), died.total());
        same("...and its dead", olderPyramid.getLastDeaths(), died.getLastDeaths());
        same("...with no cause under them", olderPyramid.getLastDeathsOfAge() + olderPyramid.getLastAgedOut()
                + olderPyramid.getLastKilled(), 0);
        double[] rowsNow = fed.toSaveArray();
        HouseholdBalance olderRows = new HouseholdBalance();
        olderRows.restore(java.util.Arrays.copyOf(rowsNow, Household.ROWS * 8 + 3));
        same("a format-29 row array loads the shelves as full", olderRows.getDeliveredShare(), 1);
        same("...and nobody hungry at full shelves", olderRows.getHungryAtFullShelves(), 0);
        same("...but still its hungry", olderRows.getHungryPeople(), fed.getHungryPeople());

        /*
         * THE MONTH'S TRADE IN UNITS (A1, 0.7.46): what every sector shipped
         * and landed this month, which the railway bills at the top of the
         * next one before the strike clears it. The rows were never saved,
         * so a reloaded railway's first haul read nothing; the save carries
         * the units beside the rows now (Sector, THE MONTH'S TRADE ACROSS A
         * SAVE), and the two getters the railway reads add them.
         */
        double shipped = 0, landed = 0;
        boolean unitsKept = true;
        for (Sector was : full.getSectors().all()) {
            Sector now = back.getSectors().byKey(was.key());
            for (Good good : Good.values()) {
                shipped += was.unitsExported(good);
                landed += was.unitsImported(good);
                unitsKept &= now != null && Double.compare(now.unitsExported(good), was.unitsExported(good)) == 0
                        && Double.compare(now.unitsImported(good), was.unitsImported(good)) == 0;
            }
        }
        assertTrue("fixture: the city shipped goods and landed goods this month", shipped > 0 && landed > 0);
        assertTrue("every sector's units shipped and landed this month cross a save", unitsKept);

        /*
         * ...AND THE MONTH'S BALANCE OF PAYMENTS (0.7.46, A2; the Trade spec's
         * D4): the flows takeMonth() struck, and the treasury's purchases and
         * sales of dollars, saved with the foreign accounts - so the Trade tab
         * of a city just loaded reads the month, and says so.
         */
        ForeignAccounts paid = full.getForeignAccounts(), paidAgain = back.getForeignAccounts();
        assertTrue("fixture: the city sold abroad and bought from abroad this month",
                paid.getExports() > 0 && paid.tradeImports() > 0 && paid.isMonthCounted());
        assertTrue("a freshly loaded city reads the month's balance of payments the live one read",
                Double.compare(paidAgain.getExports(), paid.getExports()) == 0
                        && Double.compare(paidAgain.tradeImports(), paid.tradeImports()) == 0
                        && Double.compare(paidAgain.getForeignInterest(), paid.getForeignInterest()) == 0
                        && Double.compare(paidAgain.getFinancialIn(), paid.getFinancialIn()) == 0
                        && Double.compare(paidAgain.getFinancialOut(), paid.getFinancialOut()) == 0
                        && Double.compare(paidAgain.valuationChange(), paid.valuationChange()) == 0
                        && Double.compare(paidAgain.getBoughtThisMonth(), paid.getBoughtThisMonth()) == 0
                        && Double.compare(paidAgain.getSoldThisMonth(), paid.getSoldThisMonth()) == 0);
        assertTrue("the month is counted after a load of a save that carried it", paidAgain.isMonthCounted());

        /*
         * ...AND EACH CELL'S INCOME AFTER ITS FIXED BILLS (A4, 0.7.46): struck
         * by the month from that month's disposable income, rent and fees, and
         * read between presses by the food assistance's means test. A reload
         * struck it again from the moment of loading, which is not the month's.
         */
        int cellsStruck = 0, cellsKept = 0;
        for (Household c : full.getHouseholdBalance().cells()) {
            if (c.households() < .5) continue;
            cellsStruck++;
            Household again = back.getHouseholdBalance().cellByKey(c.key());
            if (again != null && Double.compare(again.afterFixed(), c.afterFixed()) == 0) cellsKept++;
        }
        assertTrue("fixture: the city's households had incomes after their fixed bills", cellsStruck > 0);
        assertTrue("each cell's income after its fixed bills crosses a save (" + cellsStruck + " cells)",
                cellsKept == cellsStruck);

        /* ============ 14. a reloaded city PLAYS ON as the one it was saved from ============ */
        System.out.println("\n--- and a reloaded city plays on as the one it was saved from ---");

        /*
         * EVERY SECTION ABOVE COMPARES THE LOADED STATE, AND THAT IS NOT THE
         * SAME QUESTION (0.7.12, round 2). A field the next month reads and
         * no screen shows passes every one of them and still parts the two
         * cities a month on. Two did, found by playing a reload beside the
         * city it came from:
         *
         *   - the baskets the households asked the shops for, which
         *     Retail.getHouseholdShare() divides the month's sales by and the
         *     next month's hunger reads. Unsaved, the share came back as one:
         *     nobody hungry, the sick rate a third lower, every sector's
         *     operating rate a tenth higher for the first month after a load.
         *   - the price index's settling count, which rode in its based flag.
         *     A city saved in its first two years started counting again and
         *     based its index two years after the city it was saved from.
         *
         * So the fixture causes both - shops that cannot serve the queue, and
         * a save taken before the index is based - and the two cities play
         * six months each. Built with buildStack(), which tells the land
         * office, because a city stacked with addStack() stands on ground the
         * ledger never allocated and the load path heals that on purpose
         * (Game.foundingBank()); a twin of THAT city differs by design.
         */
        Game early = new Game(new GameFiles(root.resolve("early"), root.resolve("no-legacy")));
        early.run();
        early.setCashForTest(Founding.WEALTHY_CASH);
        /*
         * TWO SHOPS, AND THE SHOPS' PLANNER HELD (0.7.43). Twelve stood here,
         * and at a head count capped by coverage they could not serve the
         * queue; with groceries sold as baskets wanted at a price, twelve
         * hand over everything this town eats, and two are answered by the
         * planner inside a year (it builds against what its shops can hand
         * over, Retail.plan()). Two shops and no more, in the city and in
         * its reload alike - the hold is the harness's, not the save's.
         */
        early.getBusinessInvestment().holdSector(Sectors.RETAIL);
        early.buildStack(template(early, "House"), 300, true);
        early.buildStack(template(early, "Convenience Store"), 2, true);
        early.buildStack(template(early, "Bakery"), 3, true);
        early.buildStack(template(early, "Construction Depot"), 3, true);
        early.buildStack(template(early, "Coal Power Plant"), 2, true);
        early.buildStack(template(early, "Water Treatment Plant"), 1, true);
        early.simulateMonths(PriceIndex.SETTLING_MONTHS - 4);

        assertTrue("fixture: the shops handed over less than the households asked for",
                early.getSectors().retail().getHouseholdShare() < 1);
        assertTrue("fixture: the price index has not been based yet",
                !early.getPriceIndex().isBased());
        assertTrue("saved a city in its first two years", early.saveGame(2, "early").ok);

        Game replay = new Game(early.getGameFiles());
        replay.loadGameSave(2);
        replay.getBusinessInvestment().holdSector(Sectors.RETAIL);
        same("the share the shops handed over came back",
                replay.getSectors().retail().getHouseholdShare(),
                early.getSectors().retail().getHouseholdShare());

        early.simulateMonths(6);
        replay.simulateMonths(6);
        assertTrue("fixture: the index was based in those six months, in the city saved",
                early.getPriceIndex().isBased());
        assertTrue("...and in its reload", replay.getPriceIndex().isBased());
        same("six months on: the price index", replay.getPriceIndex().getIndex(),
                early.getPriceIndex().getIndex());
        same("...the sick rate", replay.getHealth().getSickRate(), early.getHealth().getSickRate());
        same("...the treasury", replay.getCash(), early.getCash());
        same("...the households' savings", replay.getHouseholdBalance().totalSavings(),
                early.getHouseholdBalance().totalSavings());
        same("...the bank's equity", replay.getBank().equity(), early.getBank().equity());
        for (String k : Sectors.KEYS) {
            same("  " + k + ": its till", replay.getEconomyManager().getSectorCash(k),
                    early.getEconomyManager().getSectorCash(k));
        }

        /*
         * ...AND A CITY WHOSE RAILWAY HAULS (A1, 0.7.46). The railway bills
         * the month a save was taken in at the top of the next one, from the
         * units every sector shipped and landed; the rows they were read from
         * were not saved, so the first month back billed almost nothing,
         * repriced on it, and the reload parted from the city it came from
         * in its people and its prices. A steel town with its own track - the
         * railway's planner held, as RailCheck holds it, so what hauls is the
         * track laid here - played until the trains run, saved, and both
         * pressed once.
         */
        Game hauls = new Game(new GameFiles(root.resolve("railway"), root.resolve("no-legacy")));
        hauls.run();
        hauls.setCashForTest(Founding.WEALTHY_CASH);
        hauls.getBusinessInvestment().holdSector(Sectors.RAIL);
        hauls.getLandManager().setOwnedSqFt(hauls.getLandManager().getOwnedSqFt() + 400_000_000L);
        for (String[] order : new String[][] {
                {"House", "900"}, {"Convenience Store", "20"}, {"Small Grocery Store", "6"},
                {"Paved Road", "60"}, {"Coal Power Plant", "1"}, {"Water Treatment Plant", "1"},
                {"Construction Depot", "4"}, {"Steel Foundry", "30"} }) {
            hauls.buildStack(template(hauls, order[0]), Integer.parseInt(order[1]), true);
        }
        // ...and two spurs, paid for: the town above leaves the treasury short of
        // them, and an order refused for funding would leave a city with no track.
        // Two carry its steel and its groceries both, so a first haul that read
        // nothing would move the groceries' freight, and the shelves with it.
        BuildingsTemplate spur = template(hauls, "Rail Spur");
        hauls.setCashForTest(hauls.getCash() + 2 * spur.getCashCost());
        assertTrue("fixture: the town laid two spurs of its own",
                hauls.buildStack(spur, 2, true) == Game.BuildResult.SUCCESS
                        && hauls.getBuildingManager().countByName("Rail Spur") == 2);
        // ...played until the trains run AND the price index is based: an index
        // still settling reads 1 whatever the shelves did, and could not part.
        for (int m = 0; m < 120 && (hauls.getSectors().rail().getHauledTonnes() <= 0
                || !hauls.getPriceIndex().isBased()); m++) hauls.simulateMonths(1);
        hauls.simulateMonths(1);
        assertTrue("fixture: the railway hauled in the month the city was saved",
                hauls.getSectors().rail().getHauledTonnes() > 0 && hauls.getSectors().rail().getHaulageBilled() > 0);
        assertTrue("fixture: ...its groceries among the freight, and its price index based",
                hauls.getSectors().rail().getCarried()[Traffic.GOODS.ordinal()] > 0 && hauls.getPriceIndex().isBased());
        assertTrue("saved a city whose railway hauls", hauls.saveGame(1, "railway").ok);
        Game twin = new Game(hauls.getGameFiles());
        twin.loadGameSave(1);
        /*
         * ...AND THE SAME SAVE AS A BUILD FROM BEFORE THE UNITS WROTE IT
         * (0.7.63, batch L): no sector carries them, only the ledger's money
         * split home and abroad. The first haul of such a save read nothing -
         * city2400 (0.7.38) hauled 2,515 t the month after its load against
         * 153,161 the month it was saved in - and the load now derives the
         * units from the money (Sector.deriveCarriedTrade()). Held to the
         * city that never reloaded within MoneyAudit.tolerance(), not to the
         * bit: each good's units are one division of its money by its price,
         * which rounds. The history goes with it, as a player's would.
         */
        com.google.gson.JsonObject unitless = com.google.gson.JsonParser
                .parseString(Files.readString(hauls.getGameFiles().saveFile(1))).getAsJsonObject();
        int unitMaps = 0;
        for (com.google.gson.JsonElement e : unitless.getAsJsonArray("sectors")) {
            if (e.getAsJsonObject().remove("exported") != null) unitMaps++;
            if (e.getAsJsonObject().remove("imported") != null) unitMaps++;
        }
        assertTrue("fixture: every sector's save carried the month's units, and the copy carries none, as before 0.7.46",
                unitMaps == 2 * Sectors.KEYS.length);
        Files.writeString(hauls.getGameFiles().saveFile(4), new com.google.gson.Gson().toJson(unitless));
        Files.copy(hauls.getGameFiles().historyFile(1), hauls.getGameFiles().historyFile(4));
        Game unitlessTwin = new Game(hauls.getGameFiles());
        unitlessTwin.loadGameSave(4);
        hauls.simulateMonths(1);
        twin.simulateMonths(1);
        unitlessTwin.simulateMonths(1);
        // To the bit, not to SaveFileCheck's usual 1e-9: a twin's month is the same arithmetic.
        boolean twinSame = twin.getPopulationManager().getPopulation() == hauls.getPopulationManager().getPopulation()
                && Double.compare(twin.getMigration().getLastArrivals(), hauls.getMigration().getLastArrivals()) == 0
                && Double.compare(twin.getPriceIndex().getIndex(), hauls.getPriceIndex().getIndex()) == 0;
        assertTrue("a reloaded city's first month is its unsaved twin's: population, arrivals and the price index", twinSame);
        if (!twinSame) System.out.printf("     population %d against %d, arrivals %s against %s, index %s against %s%n",
                twin.getPopulationManager().getPopulation(), hauls.getPopulationManager().getPopulation(),
                twin.getMigration().getLastArrivals(), hauls.getMigration().getLastArrivals(),
                twin.getPriceIndex().getIndex(), hauls.getPriceIndex().getIndex());
        ham.citybuildersim.sectors.Rail liveRail = hauls.getSectors().rail(), unitlessRail = unitlessTwin.getSectors().rail();
        System.out.printf("   the first haul from a save without its units: %,.6f t, %,.6fk billed; the live city's %,.6f t, %,.6fk%n",
                unitlessRail.getTradeTonnes(), unitlessRail.getHaulageBilled(), liveRail.getTradeTonnes(), liveRail.getHaulageBilled());
        assertTrue("...and from its save without the month's units, the first haul is the month's: its tonnes and its bill",
                liveRail.getTradeTonnes() > 0
                        && Math.abs(unitlessRail.getTradeTonnes() - liveRail.getTradeTonnes())
                                <= MoneyAudit.tolerance(liveRail.getTradeTonnes())
                        && Math.abs(unitlessRail.getHaulageBilled() - liveRail.getHaulageBilled())
                                <= MoneyAudit.tolerance(liveRail.getHaulageBilled()));
        double[] livePools = MoneyAudit.pools(hauls), unitlessPools = MoneyAudit.pools(unitlessTwin);
        boolean poolsNear = unitlessTwin.getPopulationManager().getPopulation() == hauls.getPopulationManager().getPopulation();
        for (int p = 0; p < livePools.length; p++) {
            poolsNear &= Math.abs(unitlessPools[p] - livePools[p]) <= MoneyAudit.tolerance(livePools[p]);
        }
        assertTrue("...a month on, every pool the money audit reads, and the people", poolsNear);

        /*
         * ...AND A TOWN SHORT OF POWER WHOSE STUDENTS FINISH (0.7.63, batch L).
         * Two figures only the month sets parted a reload from the city it
         * came from, found by playing three saved cities a month past a reload
         * beside the city that never reloaded (city2400's treasury 0.23 units
         * adrift, Jerus's 7,447):
         *
         *   - the ratios every sector's month was run at. The month is struck
         *     at the top of the next, its power and water bills on the
         *     sector's own ratio, and the load set every sector's from the
         *     services, which have struck next month's by then - so a city
         *     short of power billed its saved month at the next month's ratio
         *     (SectorState.energyRatio).
         *   - the students who finished a course and wait for the next census
         *     to carry their loans to the working families
         *     (HouseholdBalance.setGraduates()). Unsaved, the first census back
         *     carried none, and the families repaid that much less.
         *
         * So the fixture causes both - no power plant and twelve foundries, so
         * the grid's ratio moves as the town fills, and a community college
         * whose first students have finished - and either alone parts the twin
         * (measured with the other copied across). Held to the bit: a twin's
         * month is the same arithmetic as the month it was saved from.
         */
        Game college = new Game(new GameFiles(root.resolve("college"), root.resolve("no-legacy")));
        college.run();
        college.setCashForTest(Founding.WEALTHY_CASH);
        college.getLandManager().setOwnedSqFt(college.getLandManager().getOwnedSqFt() + 100_000_000L);
        for (String[] order : new String[][] {
                {"House", "600"}, {"Convenience Store", "8"}, {"Bakery", "3"}, {"Construction Depot", "3"},
                {"Water Treatment Plant", "1"}, {"Community College", "1"}, {"Steel Foundry", "12"} }) {
            college.buildStack(template(college, order[0]), Integer.parseInt(order[1]), true);
        }
        Sector grocers = college.getSectors().retail();
        for (int m = 0; m < 120 && !(college.getHouseholdBalance().getGraduating() > 0
                && grocers.getEnergyRatio() < 1 && grocers.getEnergyRatio() != college.getEnergyRatio()); m++) {
            college.simulateMonths(1);
        }
        assertTrue("fixture: the college town is short of power, and its services have struck next month's ratio over the one its month ran at",
                grocers.getEnergyRatio() < 1 && grocers.getEnergyRatio() != college.getEnergyRatio());
        assertTrue("fixture: ...and students who finished a course wait for the next census to carry what they borrowed",
                college.getHouseholdBalance().getGraduating() > 0 && college.getHouseholdBalance().totalStudentDebt() > 0);
        assertTrue("saved the college town", college.saveGame(3, "college").ok);
        Game collegeTwin = new Game(college.getGameFiles());
        collegeTwin.loadGameSave(3);
        assertTrue("the reload holds the month's power ratio and the graduates waiting",
                Double.compare(collegeTwin.getSectors().retail().getEnergyRatio(), grocers.getEnergyRatio()) == 0
                        && Double.compare(collegeTwin.getHouseholdBalance().getGraduating(),
                                college.getHouseholdBalance().getGraduating()) == 0);
        college.simulateMonths(1);
        collegeTwin.simulateMonths(1);
        double[] collegePools = MoneyAudit.pools(college), twinPools = MoneyAudit.pools(collegeTwin);
        boolean poolsSame = collegeTwin.getPopulationManager().getPopulation() == college.getPopulationManager().getPopulation();
        for (int p = 0; p < collegePools.length; p++) {
            if (Double.compare(twinPools[p], collegePools[p]) != 0) {
                poolsSame = false;
                System.out.printf("     %s: %.9f against %.9f%n", MoneyAudit.POOL_NAMES[p], twinPools[p], collegePools[p]);
            }
        }
        assertTrue("a month on, its twin is the town that never reloaded: every pool the money audit reads, and the people, to the bit",
                poolsSame);
        same("...the student loans repaid", collegeTwin.getStudentLoansRepaid(), college.getStudentLoansRepaid());

        /*
         * ...AND A CITY WITH BUSES (0.7.49, D1). The month's transit bill is
         * struck at 6d, at the month's fill, and the treasury pays it at the
         * month's end; the rebuild struck it again at the fill the month
         * ENDED on, so a reload could report a different bill for the month
         * it was saved in. A town with two Bus Networks, played until its
         * buses carry people and their staff draw wages, saved and loaded;
         * and the same save without the key, which derives it as before.
         *
         * ...AND THE COMMUTE (D4): the share of the workers with no car of
         * their own and a journey's fuel, as 6d struck them, so a reload
         * carries the riders, the fuel price and the drivers' fuel bill the
         * month did - the town played on until its households own cars and
         * drive them.
         */
        Game buses = new Game(new GameFiles(root.resolve("buses"), root.resolve("no-legacy")));
        buses.run();
        buses.setCashForTest(Founding.WEALTHY_CASH);
        buses.getLandManager().setOwnedSqFt(buses.getLandManager().getOwnedSqFt() + 100_000_000L);
        for (String[] order : new String[][] {
                {"House", "600"}, {"Convenience Store", "8"}, {"Bakery", "3"}, {"Construction Depot", "3"},
                {"Coal Power Plant", "2"}, {"Water Treatment Plant", "1"}, {"Bus Network", "2"} }) {
            buses.buildStack(template(buses, order[0]), Integer.parseInt(order[1]), true);
        }
        for (int m = 0; m < 120 && !(buses.getInfrastructureManager().getTransitRiders() > 0
                && buses.getEconomyManager().getTransitBill() > 0 && buses.getMotoring().getFuelBill() > 0); m++) {
            buses.simulateMonths(1);
        }
        buses.simulateMonths(1);
        assertTrue("fixture: the buses carried people and their staff drew wages",
                buses.getInfrastructureManager().getTransitRiders() > 0 && buses.getEconomyManager().getTransitBill() > 0);
        assertTrue("fixture: ...and its households own cars, and their drivers burn fuel",
                buses.getInfrastructureManager().getCaptiveShare() < 1 && buses.getMotoring().getFuelBill() > 0);
        assertTrue("saved a city with buses", buses.saveGame(1, "buses").ok);
        Game rode = new Game(buses.getGameFiles());
        rode.loadGameSave(1);
        System.out.printf("   the bus town's bill: $%,.4fk saved, $%,.4fk reloaded%n",
                buses.getEconomyManager().getTransitBill(), rode.getEconomyManager().getTransitBill());
        assertTrue("a reloaded city has the month's transit bill",
                Double.compare(rode.getEconomyManager().getTransitBill(), buses.getEconomyManager().getTransitBill()) == 0);
        InfrastructureManager rodeRoads = rode.getInfrastructureManager(), busRoads = buses.getInfrastructureManager();
        System.out.printf("   riders %,.4f saved, %,.4f reloaded; fuel $%.6fk a journey, $%.6fk; the drivers' fuel $%,.4fk, $%,.4fk%n",
                busRoads.getTransitRiders(), rodeRoads.getTransitRiders(), busRoads.getFuelPerJourney(),
                rodeRoads.getFuelPerJourney(), buses.getMotoring().getFuelBill(), rode.getMotoring().getFuelBill());
        assertTrue("a reloaded city has the same riders, fuel price and bill",
                Double.compare(rodeRoads.getTransitRiders(), busRoads.getTransitRiders()) == 0
                        && Double.compare(rodeRoads.getCaptiveShare(), busRoads.getCaptiveShare()) == 0
                        && Double.compare(rodeRoads.getFuelPerJourney(), busRoads.getFuelPerJourney()) == 0
                        && Double.compare(rode.getMotoring().getFuelBill(), buses.getMotoring().getFuelBill()) == 0
                        && Double.compare(rode.getEconomyManager().getTransitBill(), buses.getEconomyManager().getTransitBill()) == 0);
        com.google.gson.JsonObject unbilled = com.google.gson.JsonParser
                .parseString(Files.readString(buses.getGameFiles().saveFile(1))).getAsJsonObject();
        assertTrue("fixture: the save carries the bill and the commute by name",
                unbilled.has("transitBill") && unbilled.has("captiveShare") && unbilled.has("fuelPerJourney"));
        unbilled.remove("transitBill");
        unbilled.remove("captiveShare");
        unbilled.remove("fuelPerJourney");
        Files.writeString(buses.getGameFiles().saveFile(2), new com.google.gson.Gson().toJson(unbilled));
        Game derived = new Game(buses.getGameFiles());
        derived.loadGameSave(2);
        same("...and a save from before 0.7.49 derives it, as the load always did",
                derived.getEconomyManager().getTransitBill(),
                derived.getBuildingManager().getCategoryPayroll(BuildingType.INFRASTRUCTURE,
                        derived.getPopulationManager().getWagesPerType(), derived.getPopulationManager().getJobFillRate())
                        + derived.getBuildingManager().getUpkeepByCategory(BuildingType.INFRASTRUCTURE));

        /* ============ the city's fund, its rescue setting and the bank's preferred (0.7.14) ============ */
        System.out.println("\n--- and the city's fund, its rescue setting and the bank's preferred ---");

        /*
         * Everything new is saved by name: the fund's cash and its hand's
         * orders, the dial, the rescue setting, a pending offer and its
         * clocks, the preferred with its arrears, anniversary and warrants,
         * and the city's shares on the register (FundCheck section 11 plays
         * a fund that has traded through a save). The fixture sets every
         * one away from what a city founds with, so the round trip can fail.
         */
        Game fundCity = back;
        Bank fb = fundCity.getBank();
        assertTrue("fixture: the city has a bank to ask it for preferred", fb.getBranches() > 0);
        fundCity.setFundDial(1.5);
        fundCity.setRescueMode(TreasuryFund.RescueMode.AUTOMATIC);
        fundCity.setCashForTest(fundCity.getCash() + 50_000);
        fundCity.fundPayIn(20_000);
        fb.setCash(fb.getCash() - (fb.equity() - .9 * fb.minimumEquity()));
        fundCity.getFund().noteOffered(fundCity.getMonth());
        assertTrue("fixture: the bank is under its minimum and its offer waits",
                fb.wantsPreferred() && fundCity.isPreferredOfferPending());
        assertTrue("saved it with the offer waiting", fundCity.saveGame(3, "offer").ok);
        Game waiting = new Game(fundCity.getGameFiles());
        waiting.loadGameSave(3);
        assertTrue("a pending offer survives a save, with the month it was made",
                waiting.isPreferredOfferPending()
                        && waiting.getFund().getOfferMonth() == fundCity.getFund().getOfferMonth());
        same("...asking for the same", waiting.preferredOfferSize(), fundCity.preferredOfferSize());

        assertTrue("fixture: the city bought the preferred", fundCity.acceptPreferredOffer());
        // ...and a share on the book from the world, so a lot has a value at the step to keep (0.7.39, FundLedger).
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            if (c == Equity.BANK || !(fundCity.getEquity().getForeignShares(c) > 1)) continue;
            Exchange ex = fundCity.getExchange();
            double p = ex.price(c), q = Math.min(1, fundCity.getFund().getCash() / Math.max(1e-9, 2 * p));
            ex.bookOf(c).withdrawAll();
            ex.tradeForCheck(c, Exchange.WORLD, OrderBook.Side.SELL, p, q);
            ex.tradeForCheck(c, Exchange.FUND, OrderBook.Side.BUY, p, q);
            break;
        }
        fundCity.fundBuyShares(0, 1_000);
        assertTrue("saved it with the preferred bought and an order waiting", fundCity.saveGame(4, "preferred").ok);
        Game bought = new Game(fundCity.getGameFiles());
        bought.loadGameSave(4);
        assertTrue("the dial and the rescue setting survive a save",
                bought.getFundDial() == 1.5 && bought.getRescueMode() == TreasuryFund.RescueMode.AUTOMATIC);
        same("...the fund's cash", bought.getFund().getCash(), fundCity.getFund().getCash());
        same("...what the hand paid in", bought.getFund().getHandPaidIn(), fundCity.getFund().getHandPaidIn());
        assertTrue("...the hand's order, waiting for the step", bought.getFund().getHandOrders().size() == 1
                && bought.getFund().getHandOrders().get(0).amount() == fundCity.getFund().getHandOrders().get(0).amount());
        assertTrue("...the answer and its month", !bought.isPreferredOfferPending()
                && bought.getFund().getAcceptedMonth() == fundCity.getFund().getAcceptedMonth()
                && bought.getFund().getOffersAccepted() == fundCity.getFund().getOffersAccepted());
        same("...the preferred outstanding", bought.getBank().preferredOutstanding(), fb.preferredOutstanding());
        same("...its arrears", bought.getBank().getPreferredArrears(), fb.getPreferredArrears());
        Bank.Preferred was = fb.getPreferred().get(0), is = bought.getBank().getPreferred().get(0);
        assertTrue("...its anniversary, its cap on the dividend and its warrants",
                is.issued() == was.issued() && is.capPerShare() == was.capPerShare()
                        && is.warrantShares() == was.warrantShares() && is.strike() == was.strike()
                        && is.warrantsExpire() == was.warrantsExpire() && is.warrantsOut() == was.warrantsOut());
        same("...and the bank's equity with it in", bought.getBank().equity(), fb.equity());
        same("...its three parts still adding up", bought.getBank().equitySplitResidual(), fb.equitySplitResidual());
        same("...and what the fund is worth", bought.fundValue(), fundCity.fundValue());
        /*
         * WHAT EACH HOLDING COST (0.7.39, FundLedger): saved inside the
         * fund's state, no new key - its lots, its rows and the month it
         * began - and the fund's worth on the history beside the rest. A
         * save from 0.7.38, the fund with no ledger in it, is seeded at the
         * end of its load: tracking from that month, one row to say so.
         */
        FundLedger ledgerWas = fundCity.getFund().getLedger(), ledgerIs = bought.getFund().getLedger();
        assertTrue("fixture: the fund's record has rows to keep", ledgerWas.getActivity().size() >= 2);
        assertTrue("its cost basis survives a save: lots, rows, its start",
                ledgerIs.getLots().size() == ledgerWas.getLots().size()
                        && ledgerIs.getActivity().size() == ledgerWas.getActivity().size()
                        && ledgerIs.getTrackingSince() == ledgerWas.getTrackingSince()
                        && !bought.getFund().needsLedgerSeed());
        same("...what its holdings cost", ledgerIs.acbHeld(), ledgerWas.acbHeld());
        same("...what they realized", ledgerIs.realized(), ledgerWas.realized());
        double valueWas = 0, valueIs = 0;
        for (FundLedger.Lot l : ledgerWas.getLots()) valueWas += l.boughtValue();
        for (FundLedger.Lot l : ledgerIs.getLots()) valueIs += l.boughtValue();
        assertTrue("fixture: its buys were worth something at the step's value", valueWas > 0);
        same("...what its buys were worth at the step, a bargain's measure", valueIs, valueWas);
        double[] worthWas = fundCity.getHistorySave().aligned("fundValue"), worthIs = bought.getHistorySave().aligned("fundValue");
        assertTrue("...and the fund's worth on the history, month by month",
                worthIs.length == worthWas.length && java.util.Arrays.equals(worthIs, worthWas)
                        && worthWas.length > 0 && !Double.isNaN(worthWas[worthWas.length - 1]));
        /*
         * THE NEW PRICE MODEL, READ (0.7.45): the twenty-three series City
         * History draws of it, the month's move in credibility (the anchor's
         * eighth slot) and each price component's chained level (the index's
         * tail, after its ring) - each through a save, or a reloaded city's
         * NEEDS YOU and its chart would read another city's.
         */
        java.util.List<String> priceSeries = new java.util.ArrayList<>(java.util.List.of("expectedLevel"));
        for (int k = 0; k < PriceIndex.COMPONENTS; k++) priceSeries.add(HistorySave.indexKey(k));
        for (int k = 0; k < PriceIndex.COMPONENTS; k++) priceSeries.add(HistorySave.weightKey(k));
        priceSeries.addAll(java.util.List.of("basketLinkedAt", "shelfPrice", "shelfFloor", "basketsAsked", "basketsHanded",
                "hunger", "hungerPricedOut", "householdsAssisted",
                "mealMargin", "mealTargetMargin", "luxuryMargin", "luxuryTargetMargin"));
        boolean priceSeriesBack = priceSeries.size() == 23;
        for (String name : priceSeries) {
            double[] seriesWas = fundCity.getHistorySave().aligned(name), seriesIs = bought.getHistorySave().aligned(name);
            priceSeriesBack &= seriesWas.length > 0 && java.util.Arrays.equals(seriesWas, seriesIs)
                    && !Double.isNaN(seriesWas[seriesWas.length - 1]);
        }
        assertTrue("the new price model's twenty-three series come back on the history, month by month", priceSeriesBack);
        same("...the month's move in credibility, the anchor's eighth slot",
                bought.getExpectations().getCredibilityStep(), fundCity.getExpectations().getCredibilityStep());
        double worstComponent = 0;
        for (int k = 0; k < PriceIndex.COMPONENTS; k++) {
            worstComponent = Math.max(worstComponent,
                    Math.abs(bought.getPriceIndex().getComponentLevel(k) - fundCity.getPriceIndex().getComponentLevel(k)));
        }
        same("...and every price component's chained level, the index's tail", worstComponent, 0);
        com.google.gson.JsonObject noLedger = com.google.gson.JsonParser
                .parseString(Files.readString(fundCity.getGameFiles().saveFile(4))).getAsJsonObject();
        assertTrue("fixture: the save carries the ledger inside the fund",
                noLedger.getAsJsonObject("fund").has("ledger"));
        noLedger.getAsJsonObject("fund").remove("ledger");
        Files.writeString(fundCity.getGameFiles().saveFile(8), new com.google.gson.Gson().toJson(noLedger));
        Game seeded = new Game(fundCity.getGameFiles());
        seeded.loadGameSave(8);
        FundLedger seed = seeded.getFund().getLedger();
        assertTrue("a 0.7.38 save's ledger is seeded at its load",
                !seeded.getFund().needsLedgerSeed() && seed.getTrackingSince() == seeded.getMonth()
                        && seed.getActivity().size() == 1
                        && seed.getActivity().get(0).kind().equals(FundLedger.TRACKING));
        // (Its history is not written beside it, so the warrants' volatility is not there: the holdings, not the whole.)
        same("...its holdings and cash what they were",
                seeded.getFund().getCash() + seeded.fundSharesValue() + seeded.fundBondsValue() + seeded.fundPreferredValue(),
                bought.getFund().getCash() + bought.fundSharesValue() + bought.fundBondsValue() + bought.fundPreferredValue());
        /*
         * THE WITHDRAWAL DIAL (0.7.48, C1): the dial by name, and over the
         * default what the month's cash could not cover - which the step
         * sells the market book for and the next month's top pays - and what
         * that top paid late, the month's slots 7 and 8. A save from before
         * it has neither: Norway's rule, owing nothing.
         */
        Game drawing = new Game(fundCity.getGameFiles());
        drawing.loadGameSave(4);
        drawing.fundDrawOut(drawing.fundCashFree());
        drawing.setFundWithdrawal(TreasuryFund.MAX_WITHDRAWAL_STEPS * TreasuryFund.WITHDRAWAL_STEP);
        // ...and nothing paid in (0.7.55): under the crowding premium this
        // city's treasury runs a surplus whose pay-in met the transfer every
        // month, so the fund never sold (its cash $136.6M after a year).
        drawing.setFundDial(0);
        // A month for what it held to run short, and one to pay what it sold late: pressed until both, no more than a year.
        for (int m = 0; m < 12 && !(drawing.getFund().getToRaise() > 0 && drawing.getFund().getTransferPaidLate() > 0); m++) {
            drawing.simulateMonths(1);
        }
        System.out.printf("   drawing %s a month: due $%,.2fk, paid $%,.2fk, to raise $%,.2fk, paid late $%,.2fk, cash $%,.2fk%n",
                DecisionLog.pct2(drawing.getFundWithdrawal()), drawing.getFund().getTransferDue(), drawing.getFund().getTransferPaid(),
                drawing.getFund().getToRaise(), drawing.getFund().getTransferPaidLate(), drawing.getFund().getCash());
        assertTrue("fixture: over the default it owes what it sells for, and paid some late",
                drawing.getFund().sellsToPay() && drawing.getFund().getToRaise() > 0 && drawing.getFund().getTransferPaidLate() > 0);
        assertTrue("saved it drawing 10% a month", drawing.saveGame(9, "drawing").ok);
        Game drawn = new Game(fundCity.getGameFiles());
        drawn.loadGameSave(9);
        assertTrue("the withdrawal and what it owes cross a save",
                drawn.getFundWithdrawal() == drawing.getFundWithdrawal()
                        && drawn.getFund().getWithdrawalSteps() == TreasuryFund.MAX_WITHDRAWAL_STEPS
                        && drawn.getFund().getToRaise() == drawing.getFund().getToRaise()
                        && drawn.getFund().getTransferPaidLate() == drawing.getFund().getTransferPaidLate());
        com.google.gson.JsonObject undialled = com.google.gson.JsonParser
                .parseString(Files.readString(fundCity.getGameFiles().saveFile(9))).getAsJsonObject();
        com.google.gson.JsonObject undialledFund = undialled.getAsJsonObject("fund");
        assertTrue("fixture: the save carries the dial and the month's two new slots",
                undialledFund.has("withdrawalSteps") && undialledFund.getAsJsonArray("month").size() == 9);
        undialledFund.remove("withdrawalSteps");
        com.google.gson.JsonArray sevenSlots = new com.google.gson.JsonArray();
        for (int i = 0; i < 7; i++) sevenSlots.add(undialledFund.getAsJsonArray("month").get(i));
        undialledFund.add("month", sevenSlots);
        Files.writeString(fundCity.getGameFiles().saveFile(9), new com.google.gson.Gson().toJson(undialled));
        Game beforeTheDial = new Game(fundCity.getGameFiles());
        beforeTheDial.loadGameSave(9);
        assertTrue("...an older save loads Norway's rule, owing nothing",
                beforeTheDial.getFund().getWithdrawalSteps() == TreasuryFund.DEFAULT_WITHDRAWAL_STEPS
                        && beforeTheDial.getFundWithdrawal() == TreasuryFund.TRANSFER_RATE / TreasuryFund.YEAR_MONTHS
                        && beforeTheDial.getFund().getToRaise() == 0 && beforeTheDial.getFund().getTransferPaidLate() == 0
                        && beforeTheDial.getFund().getTransferDue() == drawing.getFund().getTransferDue());

        /*
         * THE CITY'S OWN RATE AS THE MONTH LAST STRUCK IT (0.7.14). The debt
         * market is handed its inputs and strikes the rate inside the month,
         * and the treasury's cash can move after that - here by the fixture's
         * hand, into a deficit, between presses. A reload that re-struck the
         * market off the saved cash would price the city's paper on an
         * overdraft the live city never priced on.
         */
        double struckRate = fundCity.getDebtManager().getRate();
        double struckTen = fundCity.getDebtManager().curveRate(120);
        fundCity.setCashForTest(-fundCity.getEconomyManager().getMonthGdp() * 60);
        assertTrue("saved it with its treasury gone into deficit since the market's strike", fundCity.saveGame(6, "rate").ok);
        Game rated = new Game(fundCity.getGameFiles());
        rated.loadGameSave(6);
        com.google.gson.JsonObject restrike = com.google.gson.JsonParser
                .parseString(Files.readString(fundCity.getGameFiles().saveFile(6))).getAsJsonObject();
        restrike.remove("debtMarket");
        Files.writeString(fundCity.getGameFiles().saveFile(7), new com.google.gson.Gson().toJson(restrike));
        Game restruck = new Game(fundCity.getGameFiles());
        restruck.loadGameSave(7);
        assertTrue("fixture: re-struck off the saved cash, the city's rate would come back different",
                Math.abs(restruck.getDebtManager().getRate() - struckRate) > 1e-9);
        same("the city's rate comes back as the month struck it", rated.getDebtManager().getRate(), struckRate);
        same("...and its ten-year rate, which the fund's bonds are marked on", rated.getDebtManager().curveRate(120), struckTen);
        same("...and the overdraft the market priced", rated.getDebtManager().getOverdraft(),
                fundCity.getDebtManager().getOverdraft());

        /*
         * AN OLDER SAVE: the city's first save above, with what 0.7.14 added
         * taken out - the fund, the preferred and its record, the register's
         * three city slots a company, and the city's face on each bond - and
         * loaded OVER the city that has all of them, so a loader that left a
         * missing key alone would leave that city's fund where it was.
         */
        com.google.gson.JsonObject before = com.google.gson.JsonParser
                .parseString(Files.readString(full.getGameFiles().saveFile(1))).getAsJsonObject();
        before.remove("fund");
        before.remove("bankPreferred");
        before.remove("bankPreferredRecord");
        com.google.gson.JsonArray keys = before.getAsJsonArray("equityKeys");
        com.google.gson.JsonArray slots = before.getAsJsonArray("equity");
        com.google.gson.JsonArray register = new com.google.gson.JsonArray();
        for (int k = 0; k < keys.size(); k++) {
            for (int s = 0; s < Equity.SLOTS_BEFORE_CITY; s++) register.add(slots.get(k * Equity.SLOTS + s));
        }
        before.add("equity", register);
        for (com.google.gson.JsonElement b : before.getAsJsonObject("bondMarket").getAsJsonArray("bonds")) {
            b.getAsJsonObject().remove("city");
        }
        assertTrue("fixture: the register was saved at this build's length",
                slots.size() == keys.size() * Equity.SLOTS);
        Files.writeString(fundCity.getGameFiles().saveFile(5), new com.google.gson.Gson().toJson(before));
        fundCity.loadGameSave(5);
        assertTrue("an older save loads with an empty fund, the dial at 0 and the rescue on the button",
                fundCity.getFund().isEmpty() && fundCity.getFundDial() == 0
                        && fundCity.getRescueMode() == TreasuryFund.RescueMode.BUTTON
                        && !fundCity.isPreferredOfferPending() && fundCity.getFund().getHandOrders().isEmpty());
        assertTrue("...no preferred and nothing owed on it",
                fundCity.getBank().getPreferred().isEmpty() && fundCity.getBank().preferredOutstanding() == 0
                        && fundCity.getBank().getPreferredArrears() == 0);
        boolean noShares = fundCity.getBondMarket().faceHeldByCity() == 0;
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            noShares &= fundCity.getEquity().getCityShares(c) == 0 && fundCity.getEquity().getCityRescueShares(c) == 0;
        }
        assertTrue("...and no city shares or bonds", noShares);
        assertTrue("...and its ledger begins at the load, nothing to seed",
                !fundCity.getFund().needsLedgerSeed() && fundCity.getFund().getLedger().getTrackingSince() == fundCity.getMonth()
                        && fundCity.getFund().getLedger().getLots().isEmpty());
        same("...and its register otherwise the save's: the bank's shares in issue",
                fundCity.getEquity().getShares(Equity.BANK), full.getEquity().getShares(Equity.BANK));

        cleanUp(root);

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    static BuildingsTemplate template(Game game, String name) {
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException("no template named " + name);
    }

    /** Temp directory only - nothing here ever points at a real save folder. */
    static void cleanUp(Path root) {
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (IOException ignored) { }
            });
        } catch (IOException ignored) { }
    }
}
