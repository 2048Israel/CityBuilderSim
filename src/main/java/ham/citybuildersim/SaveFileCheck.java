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

        GameFiles.Result wrote = data.saveGame(trip, 1);
        assertTrue("DataSave wrote itself", wrote.ok);

        com.google.gson.Gson gson = new com.google.gson.Gson();
        DataSave read = gson.fromJson(Files.readString(trip.saveFile(1)), DataSave.class);

        assertEquals("cash survived", read.getCash(), 123456.75);
        assertEquals("month survived", read.getMonth(), 87);
        assertEquals("population survived", read.getPopulation(), 1904);
        assertEquals("buildings survived", read.getBuildingQuantity(0), 141);
        assertEquals("household savings survived", read.getHouseholdSavings(), -2200.5);
        assertEquals("land survived", read.getLandOwned(), 4000000.0);
        assertEquals("tax rates survived", read.getPropertyTaxRate(), .015);

        // The transient path fields that used to live on DataSave were dropped;
        // make sure nothing crept into the file that should not be in it.
        String json = Files.readString(trip.saveFile(1));
        assertTrue("no file paths leaked into the save",
                !json.contains("YourGame") && !json.contains("userHome"));

        HistorySave history = new HistorySave();
        history.recordMonth(1, 300000, 0, 0, .01, 0, 0, 0);
        history.recordMonth(2, 299000, 20.83, 0, .01, 12, 4, 4);
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
        BuildingsTemplate store = template(city, "Convience Store");
        BuildingsTemplate depot = template(city, "Construction Depot");

        // Industry included on purpose. The first version of this city had none,
        // and a city with no mills cannot catch the flow bugs in section 9 -
        // every industrial figure is zero on both sides of the save and the
        // assertions pass without proving anything.
        city.buildStack(house, 120, false);
        city.buildStack(store, 3, false);
        city.buildStack(template(city, "Texttile Mill"), 1, false);
        city.simulateMonths(90);

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
        int peopleBefore = city.getPopulationManager().getPopulation();

        // History the city cannot recompute from its present. Recorded here
        // rather than waited for, so the assertion does not depend on whether
        // this particular city happens to demolish something.
        city.getDemolitionLog().record("Construction Depot", 2, "Construction",
                city.getMonth(), 480);
        int demolitionsBefore = city.getDemolitionLog().size();
        double writtenOffBefore =
                city.getEconomyManager().getBusinessDebtManager().getTotalWrittenOff();

        assertTrue("saved", city.saveGame(1, "the test city").ok);

        Game reloaded = new Game(cityFiles);
        reloaded.loadGameSave(1);

        double incomeAfter = reloaded.getIncome();
        int peopleAfter = reloaded.getPopulationManager().getPopulation();

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
        warned.setAutoSubsidised(PolicySector.CONSTRUCTION, false);
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
                stillWarned.isAutoSubsidised(PolicySector.CONSTRUCTION) ? 1 : 0, 0);
        assertEquals("...so the player is still being warned",
                stillWarned.isConstructionShedding(), warningUp);

        // ...and protecting the sector is what actually answers the warning.
        stillWarned.setAutoSubsidised(PolicySector.CONSTRUCTION, true);
        assertTrue("protecting construction takes the warning down",
                !stillWarned.isConstructionShedding());
        stillWarned.setAutoSubsidised(PolicySector.CONSTRUCTION, false);

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
        CommercialHandler beforeShops = city.getEconomyManager().getCommercialHandler();
        CommercialHandler afterShops = reloaded.getEconomyManager().getCommercialHandler();

        assertTrue("the city actually charges retail something",
                beforeShops.getRetailPropertyTax() > 0);
        assertEquals("retail's property tax expense came back",
                Math.round(afterShops.getRetailPropertyTax() * 10000),
                Math.round(beforeShops.getRetailPropertyTax() * 10000));
        assertEquals("real estate's did too",
                Math.round(afterShops.getRealEstatePropertyTax() * 10000),
                Math.round(beforeShops.getRealEstatePropertyTax() * 10000));

        // And the parts add up to the whole the city collected.
        double parts = afterShops.getRetailPropertyTax()
                + afterShops.getRealEstatePropertyTax();
        assertTrue("the sector charges are part of the city's total",
                parts > 0 && parts <= reloaded.getEconomyManager().getTotalPropertyTax() + 0.01);

        /* ============ 10. the whole figure, to the cent ============ */
        System.out.println("\n--- and the number on the button ---");

        // The bug as Jerus reported it: note the income, save, quit, load, and
        // the income has changed. It is the end-to-end assertion the other nine
        // sections exist to make possible, and it holds only because every
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

        assertTrue("industry is actually trading, so this proves something",
                city.getEconomyManager().getIndustryDemand() > 0);

        EconomyManager e1 = city.getEconomyManager();
        EconomyManager e2 = reloaded.getEconomyManager();

        // Exact, and they have to stay exact.
        assertEquals("business tax", Math.round(e2.getBusinessTax() * 10000),
                Math.round(e1.getBusinessTax() * 10000));
        assertEquals("wage tax", Math.round(e2.getWageTax() * 10000),
                Math.round(e1.getWageTax() * 10000));
        assertEquals("retail cost of goods",
                Math.round(e2.getRetailCostOfGoods() * 10000),
                Math.round(e1.getRetailCostOfGoods() * 10000));

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

        // The order book, which is what makes the GDP line above hold.
        ConstructionHandler b1 = city.getServicesManager().getConstructionHandler();
        ConstructionHandler b2 = reloaded.getServicesManager().getConstructionHandler();
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
        growing.buildStack(template(growing, "House"), 300, false);
        growing.buildStack(template(growing, "Convience Store"), 6, false);
        growing.buildStack(template(growing, "Texttile Mill"), 1, false);
        growing.simulateMonths(40);

        PopulationManager gp = growing.getPopulationManager();
        CommercialHandler gc = growing.getEconomyManager().getCommercialHandler();

        // The city has to be genuinely mid-stride, or this proves nothing. Both
        // of these are the specific things that were being re-derived.
        assertTrue("the city really is still growing",
                gp.getWorkforce() != (int) (gp.getPopulation() * .5));
        assertTrue("...and its statement describes a smaller city than it is now",
                gc.getReportPopulation() != gc.getPopulation());

        EconomyManager ge = growing.getEconomyManager();
        double gIncome = growing.getIncome();
        int gWorkforce = gp.getWorkforce();
        double gWageTax = ge.getWageTax();
        double gSalesTax = ge.getSalesTax();
        double gRetail = gc.getGrossRevenue();
        double gIndustry = ge.getIndustrialHandler().getGrossRevenue();
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
                Math.round(me.getCommercialHandler().getGrossRevenue() * 10000),
                Math.round(gRetail * 10000));
        assertEquals("industrial gross revenue",
                Math.round(me.getIndustrialHandler().getGrossRevenue() * 10000),
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
        indebted.buildStack(template(indebted, "Convience Store"), 3, false);
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
