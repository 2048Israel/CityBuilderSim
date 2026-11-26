package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * A building stack that holds more than 2^31 - 1 buildings reads back whole: through an order, through
 * the save and the load, through ScaleCheck's scaler, and in the sums a city is read by.
 * FS6 finishes the ordered House stack directly before saving the standing town;
 * its known count supplies both sides of the integer ceiling.
 *
 * WHY (SQ1, found by ST3). BuildingsStacks kept its quantity, its on-site count and its unfitted counts in
 * an int, and a building type's units merge into one stack. Jerus's plans are cities of 5 and 10 billion
 * people (ScaleCheck.FREE_PEOPLE); a town housed in Houses (6 people each) holds about 0.2 Houses a person,
 * so at 10 billion its House stack is about 2e9, against an int's 2,147,483,647. ST3 met the wall in
 * ScaleCheck's ten-billion copy: where the stack times K passed it the copy did not load ("the copy loads"
 * FAIL), and near it the copy's largest order was one building. The counts are a long since SQ1; an old
 * save's int reads into one, so there is no format bump.
 *
 * What this has to prove:
 *  1. a stack driven past 2^31 - 1 by an order, one on site and one standing, reads back whole, and
 *     scrapping floors at none;
 *  2. its unfitted count rides with it, never more than stand;
 *  3. the manager's sums, a city's homes, posts and household places, are its quantity times the
 *     template's, worked out apart in BigInteger;
 *  4. a city copied K times over by ScaleCheck.scale() so that its House stack passes 2^31 loads, and the
 *     stack, its homes, its posts and its places are K times the city's;
 *  5. ...and its on-site count too, from a save written with an order on site;
 *  6. an order placed on the loaded copy carries it past 2^31, and a save of it reads back whole;
 *  7. below the ceiling nothing reads differently (section 1);
 *  8. an order through Game.buildStack (not BuildingManager.addStack) on a copy whose House stack stands just under the
 *     ceiling, its material past 2^31 units: its quote, the ground it takes, the cash it charges, the receipt, the yard's
 *     share delivered to the sites, the material the sites owe, and the invoice of a run of two, whose second order is quoted
 *     on the yard the first leaves;
 *  9. a month's material draw past 2^31 units is drawn (the sites' and a repair's, yard first), booked in materialsConsumed,
 *     and saved and loaded whole, while a draw of infinite units (Math.round's Long.MAX_VALUE) takes nothing;
 * 10. a template has ONE stack, however it is ordered, played, saved and loaded, and a shed takes the count it asks of it, up to what
 *     stands, through Game's own closing and shedding paths, leaving the emptied stack in place and the books each keeps (the count, the
 *     ground, the posts, the salvage) at what was scrapped and no more (SB1).
 *
 * WHY (SB1, the premise HS1's notes queued and this section proves false). BuildingManager.retire() scraps from the first stack of the
 * template it finds and returns, and HS1's OutsideCheck fixture, closing a works, read that as a fault: "a template built in several
 * stacks would be only partly closed". No template is. addStack() is the one place a stack is made, and it merges an order into the stack
 * of the template's name, so a second order, a building wave, an order on site and every load (Game's restore goes through addStack too)
 * add to the one stack; ids are unique (BuildingDataCheck), and a save keys a stack by its id. retire()'s early return is exact, and a
 * loop over more stacks would have no city to change. Section 10 is the finding and its tripwire: the day a second stack of one template
 * can stand, its stack count goes red here, and retire() is the first thing to mend.
 *
 * WHY (SQ1b, the two notes of SQ1's review). Section 6 ordered through BuildingManager.addStack, so the quote, the ground and the
 * charge of Game.buildStack had no harness past the ceiling; and Game.drawMaterials rounded a month's draw into an int, where a
 * month's draw at ten-billion scale passes 2^31 units (this fixture's own month draws tens of millions, so section 9 drives the draw
 * there): the whole draw wrapped to a negative count and went unbought and unbooked, and the yard, materialsConsumed and the run's
 * invoice carried the same int. They are a long since SQ1b (an old save's int reads
 * into one, so no format bump); the harness is red with any of them an int again.
 *
 * The orders here are int-sized, as the game's are; a stack passes the ceiling by two of them, or by the
 * scaler. Written so that it compiles against main's int stack, where it is red: the sums wrap.
 */
public class BuildingStackCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    /** The House order the fixture's town has on site when its first save is written. */
    static final int ORDER_HOUSES = 40;

    /** How far below the ceiling section 6's copy stands, so that one order of the next size passes it. */
    static final int ROOM_BELOW = 100;

    static void same(String label, long actual, long expected) {
        boolean ok = actual == expected;
        if (!ok) fails++;
        out.printf("%-96s %s  %,d against %,d%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-96s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** A figure read against the one it should be, relative to the larger of the two or to one; both printed (SB1). */
    static void near(String label, double actual, double expected, double tolerance) {
        boolean ok = Math.abs(actual - expected) <= tolerance * Math.max(1, Math.max(Math.abs(actual), Math.abs(expected)));
        if (!ok) fails++;
        out.printf("%-96s %s  %,.9g against %,.9g%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    /** The newest line of a city's demolition log (the log lists newest first). */
    static DemolitionLog.Entry newest(Game g) {
        return g.getDemolitionLog().all().get(0);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        Path root = TempDirs.create("buildingstack");
        try {
            run(root);
        } finally {
            ScaleCheck.cleanUp(root);
        }
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    static void run(Path root) throws Exception {

        /* ============== the fixture: a young town, saved with its houses on site and again once they stand ============== */
        GameFiles small = new GameFiles(root.resolve("small"), root.resolve("small-no-legacy"));
        GameFiles ordered = new GameFiles(root.resolve("ordered"), root.resolve("ordered-no-legacy"));
        Game founded = new Game(small, LongPlaytest.founding());
        PrintStream was = LongPlaytest.out;
        LongPlaytest.out = quiet;
        try {
            quietly(() -> {
                founded.run();
                LongPlaytest.build(founded, "House", ORDER_HOUSES);
                // ...the order on site: its save is section 5's.
                founded.saveGame(10, "buildingstack ordered");
                try {
                    Files.createDirectories(ordered.saveFile(10).getParent());
                    Files.copy(small.saveFile(10), ordered.saveFile(10));
                    Files.copy(small.historyFile(10), ordered.historyFile(10));
                } catch (java.io.IOException e) { throw new IllegalStateException(e); }
                BuildingsStacks built = stackOf(founded, "House");
                built.advanceConstruction(2.0 * ORDER_HOUSES * built.getBuilding().getConstructionPoints());
                same("fixture: the ordered Houses finish before the standing save", built.getQuantity(), ORDER_HOUSES);
                founded.saveGame(10, "buildingstack");
            });
        } finally {
            LongPlaytest.out = was;
        }
        BuildingManager fixture = founded.getBuildingManager();
        BuildingsTemplate house = fixture.getTemplateByName("House");
        BuildingsTemplate shop = fixture.getTemplateByName("Convenience Store");
        BuildingsTemplate bakery = fixture.getTemplateByName("Bakery");
        assertTrue("fixture: the catalogue has a House, a Convenience Store and a Bakery, and the Bakery carries a piece of equipment",
                house != null && shop != null && bakery != null && bakery.getEquipment(EquipmentCheck.DRIVE) != null);

        /* ============== 1. a stack takes orders past 2^31 - 1 ============== */
        out.println("--- a stack driven past 2^31 - 1 by orders reads back whole ---");
        long ceiling = Integer.MAX_VALUE;

        BuildingsStacks shops = new BuildingsStacks(shop, Integer.MAX_VALUE - 3);
        shops.startConstruction(10);
        quietly(() -> shops.advanceConstruction(2.0 * 10 * shop.getConstructionPoints()));
        same("three short of the ceiling and an order of ten finished: the stack stands 7 past it", shops.getQuantity(), ceiling + 7);
        same("...the ten were this month's finished", shops.getLastFinished(), 10);
        same("...none is left on site", shops.getUnderConstruction(), 0);
        assertTrue("...and it stands past what an int holds", shops.getQuantity() > Integer.MAX_VALUE);

        BuildingsStacks site = new BuildingsStacks(shop, 0);
        site.startConstruction(Integer.MAX_VALUE);
        site.startConstruction(Integer.MAX_VALUE);
        same("two orders of 2^31 - 1 on one stack: the sites hold twice that", site.getUnderConstruction(), 2 * ceiling);
        double owedMaterial = 2.0 * ceiling * shop.getConstructionMaterials();
        assertTrue("...and owe the material of every one of them, not of a wrapped count",
                Math.abs(site.getMaterialsOwed() - owedMaterial) <= 1e-9 * owedMaterial);
        quietly(() -> site.advanceConstruction(2.0 * (2 * ceiling) * shop.getConstructionPoints()));
        same("...and when the crews finish them the stack stands twice 2^31 - 1", site.getQuantity(), 2 * ceiling);
        same("...with all of them this month's finished", site.getLastFinished(), 2 * ceiling);
        same("...none left on site", site.getUnderConstruction(), 0);

        site.addQuantity(Integer.MAX_VALUE);
        same("a third order of 2^31 - 1 added at once: three times the ceiling", site.getQuantity(), 3 * ceiling);
        site.removeQuantity(Integer.MAX_VALUE);
        same("...one scrapped: twice", site.getQuantity(), 2 * ceiling);
        site.removeQuantity(Integer.MAX_VALUE);
        site.removeQuantity(Integer.MAX_VALUE);
        same("...two more scrapped: none, not a negative count", site.getQuantity(), 0);
        site.removeQuantity(Integer.MAX_VALUE);
        same("...scrapping what is not there floors at none", site.getQuantity(), 0);

        BuildingsStacks eight = new BuildingsStacks(shop, 5);
        eight.addQuantity(3);
        same("below the ceiling nothing reads differently: five and three are eight", eight.getQuantity(), 8);
        eight.removeQuantity(100);
        same("...and scrapping a hundred of eight leaves none", eight.getQuantity(), 0);

        /* ============== 2. the unfitted count rides with the stack ============== */
        out.println("\n--- the buildings without a piece of equipment, past 2^31 - 1 ---");
        BuildingsStacks bakeries = new BuildingsStacks(bakery, 0);
        bakeries.addQuantity(Integer.MAX_VALUE);
        bakeries.addQuantity(Integer.MAX_VALUE);
        bakeries.setUnfitted(EquipmentCheck.DRIVE, bakeries.getQuantity());
        same("two orders of 2^31 - 1 Bakeries, all without the piece: the count is the stack's", bakeries.getUnfitted(EquipmentCheck.DRIVE), 2 * ceiling);
        bakeries.removeQuantity(Integer.MAX_VALUE);
        same("...one order's worth scrapped: the count falls with it, never above what stands",
                bakeries.getUnfitted(EquipmentCheck.DRIVE), bakeries.getQuantity());
        same("...and the Bakeries that stand are one order's", bakeries.getQuantity(), ceiling);

        /* ============== 3. the sums: homes, posts and household places ============== */
        out.println("\n--- homes, posts and household places are the quantity times the template's ---");
        BuildingManager bm = fixture;
        long homes0 = bm.getTotalHomes(), places0 = bm.getTotalHouseCapacity();
        long[] posts0 = new long[JobType.values().length];
        for (JobType j : JobType.values()) posts0[j.ordinal()] = bm.getTotalJobsAtEveryPost(j);
        bm.addStack(house, Integer.MAX_VALUE, true);
        bm.addStack(house, Integer.MAX_VALUE, true);
        bm.addStack(shop, Integer.MAX_VALUE, true);
        bm.addStack(shop, Integer.MAX_VALUE, true);
        BigInteger twice = BigInteger.valueOf(2 * ceiling);
        int perHome = house.getDwellings() > 0 ? house.getDwellings() : Math.max(1, house.getCapacity() / 4);
        same("two orders of 2^31 - 1 Houses add their dwellings times 2 x (2^31 - 1) homes",
                bm.getTotalHomes() - homes0, twice.multiply(BigInteger.valueOf(perHome)).longValueExact());
        same("...and their capacity times that to the household places",
                bm.getTotalHouseCapacity() - places0, twice.multiply(BigInteger.valueOf(house.getCapacity())).longValueExact());
        boolean everyPost = true, somePost = false;
        for (JobType j : JobType.values()) {
            long added = bm.getTotalJobsAtEveryPost(j) - posts0[j.ordinal()];
            long expected = twice.multiply(BigInteger.valueOf(shop.getJobs(j))).longValueExact();
            if (added != expected) everyPost = false;
            if (expected > Integer.MAX_VALUE) somePost = true;
        }
        assertTrue("two orders of 2^31 - 1 Convenience Stores add each kind of post times 2 x (2^31 - 1)", everyPost);
        assertTrue("...and that is past what an int holds in at least one kind of post: the fixture makes the condition", somePost);
        assertTrue("...and the Houses' places passed the ceiling too", twice.multiply(BigInteger.valueOf(house.getCapacity())).longValueExact() > Integer.MAX_VALUE);

        /* ============== 4. ScaleCheck.scale(): the copy's House stack past 2^31 ============== */
        out.println("\n--- a copy K times over, its House stack past 2^31 - 1, loads and reads back whole ---");
        Game one = new Game(small);
        quietly(() -> one.loadGameSave(10));
        long houses1 = one.getBuildingManager().getQuantity(house.getId());
        long homes1 = one.getBuildingManager().getTotalHomes();
        long capacity1 = one.getHouseholdCapacity();
        long posts1 = ScaleCheck.everyPost(one);
        assertTrue("fixture: the founded town has Houses", houses1 > 0);
        // K that puts the stack just under the ceiling: section 6's order is what passes it.
        long k = (ceiling - ROOM_BELOW) / houses1;
        long kPast = ceiling / houses1 + 2;
        GameFiles big = new GameFiles(root.resolve("big"), root.resolve("big-no-legacy"));
        ScaleCheck.scale(small.saveFile(10), big.saveFile(10), kPast);
        Files.copy(small.historyFile(10), big.historyFile(10));
        Game g = new Game(big);
        quietly(() -> g.loadGameSave(10));
        assertTrue("the copy loads (an int stack could not read it)", g.getLoadFailure() == null && g.getMonth() == one.getMonth());
        BuildingManager bigM = g.getBuildingManager();
        same("its House stack is K times the founded town's", bigM.getQuantity(house.getId()), kPast * houses1);
        assertTrue("...and stands past 2^31 - 1", bigM.getQuantity(house.getId()) > Integer.MAX_VALUE);
        same("every home is K times the founded town's", bigM.getTotalHomes(), kPast * homes1);
        same("the household places are K times the founded town's", g.getHouseholdCapacity(), kPast * capacity1);
        same("every post is K times the founded town's", ScaleCheck.everyPost(g), kPast * posts1);
        assertTrue("...the homes and the places past what an int holds", bigM.getTotalHomes() > Integer.MAX_VALUE && g.getHouseholdCapacity() > Integer.MAX_VALUE);

        /* ============== 5. the same from a save written with an order on site ============== */
        out.println("\n--- a copy whose order is on site: the on-site count past 2^31 - 1 too ---");
        Game orderedOne = new Game(ordered);
        quietly(() -> orderedOne.loadGameSave(10));
        BuildingsStacks onSiteOne = stackOf(orderedOne, "House");
        long onSite1 = onSiteOne == null ? 0 : onSiteOne.getUnderConstruction();
        long homesOn1 = orderedOne.getBuildingManager().getHomesUnderConstruction();
        assertTrue("fixture: the town's save has Houses on site", onSite1 > 0 && homesOn1 > 0);
        long kOn = ceiling / onSite1 + 2;
        GameFiles bigOn = new GameFiles(root.resolve("bigOn"), root.resolve("bigOn-no-legacy"));
        ScaleCheck.scale(ordered.saveFile(10), bigOn.saveFile(10), kOn);
        Files.copy(ordered.historyFile(10), bigOn.historyFile(10));
        Game gOn = new Game(bigOn);
        quietly(() -> gOn.loadGameSave(10));
        assertTrue("the copy with its order on site loads", gOn.getLoadFailure() == null && gOn.getMonth() == orderedOne.getMonth());
        BuildingsStacks onSite = stackOf(gOn, "House");
        same("its Houses on site are K times the town's", onSite == null ? -1 : onSite.getUnderConstruction(), kOn * onSite1);
        assertTrue("...and past 2^31 - 1", onSite != null && onSite.getUnderConstruction() > Integer.MAX_VALUE);
        same("the homes on site are K times the town's", gOn.getBuildingManager().getHomesUnderConstruction(), kOn * homesOn1);

        /* ============== 6. an order carries the loaded copy past 2^31 - 1, and its save reads back whole ============== */
        out.println("\n--- an order on a copy just under the ceiling carries it past, and the save gives it back ---");
        GameFiles under = new GameFiles(root.resolve("under"), root.resolve("under-no-legacy"));
        ScaleCheck.scale(small.saveFile(10), under.saveFile(10), k);
        Files.copy(small.historyFile(10), under.historyFile(10));
        Game gu = new Game(under);
        quietly(() -> gu.loadGameSave(10));
        BuildingManager um = gu.getBuildingManager();
        long standing = um.getQuantity(house.getId());
        assertTrue("fixture: the copy's House stack stands under the ceiling, by less than the town's own Houses and the room left",
                gu.getLoadFailure() == null && standing <= ceiling && ceiling - standing < houses1 + ROOM_BELOW);
        if (gu.getLoadFailure() != null) return;   // main's int stack ends here: the copy did not load, and the rest reads its stacks
        // An order of the room left and ROOM_BELOW more: an int, as every order is.
        int order = (int) (ceiling - standing) + 1 + ROOM_BELOW;
        BuildingsTemplate underHouse = um.getTemplateByName("House");
        BuildingsStacks before = um.getStack(underHouse);
        long onBefore = before == null ? 0 : before.getUnderConstruction();   // the town's own sites, K times over, are on site already
        um.addStack(underHouse, order, false);
        BuildingsStacks stack = um.getStack(underHouse);
        same("an order of the room left and " + ROOM_BELOW + " more is on site beside the standing Houses and the copy's own sites", stack.getUnderConstruction(), onBefore + order);
        quietly(() -> stack.advanceConstruction(2.0 * (onBefore + order) * underHouse.getConstructionPoints()));
        same("...and all of it finished, the stack stands what stood, the sites and the order", stack.getQuantity(), standing + onBefore + order);
        assertTrue("...which is past 2^31 - 1: an order carried it there", stack.getQuantity() > Integer.MAX_VALUE);
        long carried = stack.getQuantity();
        long homesBefore = um.getTotalHomes();
        boolean[] saved = new boolean[1];
        quietly(() -> saved[0] = gu.saveGame(10, "buildingstack past").ok);
        assertTrue("the city with a stack past the ceiling saves", saved[0]);
        Game back = new Game(under);
        quietly(() -> back.loadGameSave(10));
        assertTrue("...and the save loads", back.getLoadFailure() == null);
        same("the House stack came back whole", back.getBuildingManager().getQuantity(house.getId()), carried);
        same("...its homes", back.getBuildingManager().getTotalHomes(), homesBefore);
        same("...its household places", back.getHouseholdCapacity(), gu.getHouseholdCapacity());
        quietly(() -> back.simulateMonths(1));
        assertTrue("a month at that size runs, and the stack still stands past 2^31 - 1",
                back.getMonth() == gu.getMonth() + 1 && back.getBuildingManager().getQuantity(house.getId()) > Integer.MAX_VALUE);

        /* ============== 7. Game.buildStack on a copy just under the ceiling: its quote, its ground and its invoice (SQ1b) ============== */
        out.println("\n--- an order through Game.buildStack, its material past 2^31 units: the quote, the ground, the yard and the invoice ---");
        GameFiles buy = new GameFiles(root.resolve("buy"), root.resolve("buy-no-legacy"));
        ScaleCheck.scale(small.saveFile(10), buy.saveFile(10), k);
        Files.copy(small.historyFile(10), buy.historyFile(10));
        Game gb = new Game(buy);
        quietly(() -> gb.loadGameSave(10));
        assertTrue("fixture: the copy loads", gb.getLoadFailure() == null);
        if (gb.getLoadFailure() != null) return;
        BuildingManager bb = gb.getBuildingManager();
        BuildingsTemplate bh = bb.getTemplateByName("House");
        BuildingsTemplate bs = bb.getTemplateByName("Convenience Store");
        long standingB = bb.getQuantity(bh.getId());
        assertTrue("fixture: its House stack stands under the ceiling, as section 6's does", standingB <= ceiling && ceiling - standingB < houses1 + ROOM_BELOW);
        // An order whose material alone is a unit and a half of what an int holds: the draw's int wrapped it negative.
        long orderB = (long) Math.ceil(1.5 * ceiling / bh.getConstructionMaterials());
        double needed = (double) bh.getConstructionMaterials() * orderB;
        assertTrue("fixture: the order's material (" + orderB + " Houses at " + bh.getConstructionMaterials() + " units each) is past what an int holds, and the order itself fits one",
                needed > Integer.MAX_VALUE && orderB < Integer.MAX_VALUE);
        gb.getBuildingManager().setConstructionMaterials(bh.getConstructionMaterials());
        long yard0 = gb.getConstructionMaterials();
        assertTrue("fixture: the yard holds its own small stock, which the order's material dwarfs", yard0 > 0 && yard0 < needed / 1_000_000);
        long onBeforeB = bb.getStack(bh) == null ? 0 : bb.getStack(bh).getUnderConstruction();
        double owed0 = bb.getStack(bh) == null ? 0 : bb.getStack(bh).getMaterialsOwed();

        // The quote.
        Game.BuildQuote q = gb.quoteBuildOn(bh, orderB, Landmasses.CHOSEN);
        same("the quote is for the order's Houses", q.quantity, orderB);
        assertTrue("...its material is the units each House takes times the order, not an int's wrap of them", q.materialsNeeded == needed);
        assertTrue("...the yard covers its own stock and the plant and the world the rest, which is past what an int holds",
                q.materialsInStock == yard0 && q.materialsFromPlant + q.materialsImported > Integer.MAX_VALUE
                        && Math.abs(q.materialsInStock + q.materialsFromPlant + q.materialsImported - needed) <= 1e-9 * needed);
        assertTrue("...its ground is the square feet each House takes times the order", q.landNeeded == bh.getLandSqFt() * orderB);
        assertTrue("...and its price is a finite sum past 2^31 dollars", Double.isFinite(q.total) && q.total > Integer.MAX_VALUE);

        // The invoice of a run: the second order is quoted on the yard the first leaves, which its material empties.
        long storeOrder = (long) Math.ceil(100.0 * yard0 / bs.getConstructionMaterials()) + 1;   // its material is more than the yard's stock, so its price reads the yard
        java.util.Map<BuildingsTemplate, Long> run = BuildCardCheck.run(bh, orderB, bs, storeOrder);
        double firstOnYard = gb.quoteBuild(bh, orderB).total;
        double invoiceOnYard = gb.buildRunInvoice(run);
        gb.getBuildingManager().setConstructionMaterials(0);
        double firstOnNone = gb.quoteBuild(bh, orderB).total;
        double invoiceOnNone = gb.buildRunInvoice(run);
        gb.getBuildingManager().setConstructionMaterials(yard0);
        double yardPays = firstOnNone - firstOnYard;
        assertTrue("fixture: the yard's stock changes the first order's price (" + yardPays + ")", yardPays > 0);
        assertTrue("the run's invoice moves with the yard by exactly the first order's share: the second is quoted on the yard the first emptied, in both",
                Math.abs((invoiceOnNone - invoiceOnYard) - yardPays) <= 1e-3 * yardPays);
        assertTrue("...and a run's first order is quoted as it is alone", Math.abs(gb.buildRunInvoice(BuildCardCheck.run(bh, orderB)) - firstOnYard) <= 1e-9 * firstOnYard);

        // The order.
        double need = q.landNeeded;
        double freeBefore = gb.getLandManager().freeFor(bh.getSector(), Game.groundUse(bh));
        assertTrue("fixture: the ground is there for the order", freeBefore >= need);
        gb.setCashForTest(2 * q.total);   // the money is the fixture's: the order is not a funding question
        double cash0 = gb.getCash();
        long consumed0 = gb.materialsConsumed;
        Game.BuildResult result = null;
        { Game.BuildResult[] r = new Game.BuildResult[1]; quietly(() -> r[0] = gb.buildStack(bh, orderB, false)); result = r[0]; }
        assertTrue("buildStack takes the order (" + result + ")", result == Game.BuildResult.SUCCESS);
        assertTrue("...and charges what the quote said, in full", Math.abs((cash0 - gb.getCash()) - q.total) <= 1e-9 * q.total);
        assertTrue("...the receipt reads the quote's total and its material", gb.getTotalBuildingCost() == q.total && gb.getMaterialsUsed() == q.materialsNeeded);
        same("...and the order's Houses", gb.getBuildQuantity(), orderB);
        assertTrue("...the ground it took is the quote's: the free ground fell by the square feet of every House",
                Math.abs((freeBefore - gb.getLandManager().freeFor(bh.getSector(), Game.groundUse(bh))) - need) <= 1e-9 * need);
        BuildingsStacks onSiteB = bb.getStack(bh);
        same("...the order stands on site beside the copy's own sites", onSiteB.getUnderConstruction(), onBeforeB + orderB);
        assertTrue("...which with what stands is past 2^31 - 1", onSiteB.getUnderConstruction() + onSiteB.getQuantity() > Integer.MAX_VALUE);
        same("...the yard's whole stock went to the sites the day it was placed", gb.getConstructionMaterials(), 0);
        same("...and the books took it, as the yard's share of the units", gb.materialsConsumed - consumed0, yard0);
        assertTrue("...the sites owe the rest of the order's material, past what an int holds",
                Math.abs((onSiteB.getMaterialsOwed() - owed0) - (needed - yard0)) <= 1e-11 * needed && onSiteB.getMaterialsOwed() - owed0 > Integer.MAX_VALUE);

        /* ============== 8. a month's draw past 2^31 units: the books it feeds read it whole ============== */
        out.println("\n--- a month's material draw past 2^31 units is drawn, booked and saved whole ---");
        // The month the order is placed in: the crews draw what they build, the copy's own sites and the order's (SimulationEngine -> drawSiteMaterials()).
        // A copy K times a founded town draws K times its few hundred units a month, tens of millions here: a month of this fixture is not past
        // the ceiling by itself (ten billion people's is), so the draws past it are driven below, through the same methods the month calls.
        GoodsMarket materials = gb.getMarkets().get(Good.MATERIALS);
        quietly(() -> gb.simulateMonths(1));
        double[] takenHistory = materials.getTakenHistory();
        double monthTaken = takenHistory[takenHistory.length - 1];
        assertTrue("the month after the order runs, and the crews drew on the market for it (" + monthTaken + " units, K times the founded town's)", monthTaken > 0);
        // The builders' stock of scrapped plant's material is drawn first, then the market: a draw of the stock and three int ranges and a bit more,
        // so that what is bought is itself past what an int holds. Its int wrapped to a negative count, so the whole draw went unbought and unbooked.
        double held = gb.getSectors().construction().getSalvage();
        double bought = 3.0 * (ceiling + 1) + 12_345;
        double units = held + bought;
        long consumed1 = gb.materialsConsumed;
        double drawn1 = materials.getDrawn(), salvage1 = gb.getSalvageUsedThisMonth();
        quietly(() -> gb.drawSiteMaterials(units));
        same("the sites' draw is booked whole: materialsConsumed rose by the units drawn", gb.materialsConsumed - consumed1, Math.round(units));
        assertTrue("...the builders' own stock of scrapped plant's material went first, whole (" + held + " units)",
                Math.abs((gb.getSalvageUsedThisMonth() - salvage1) - held) <= 1e-9 * units);
        assertTrue("...and the materials market was drawn on for the rest, past what an int holds",
                Math.abs((materials.getDrawn() - drawn1) - bought) <= 1e-9 * units && materials.getDrawn() - drawn1 > Integer.MAX_VALUE);
        assertTrue("...and the books stand past what an int holds", gb.materialsConsumed > Integer.MAX_VALUE);
        boolean[] savedB = new boolean[1];
        quietly(() -> savedB[0] = gb.saveGame(10, "buildingstack drawn").ok);
        assertTrue("the city with a draw past 2^31 booked saves", savedB[0]);
        Game drawn = new Game(buy);
        quietly(() -> drawn.loadGameSave(10));
        assertTrue("...and the save loads", drawn.getLoadFailure() == null);
        same("...materialsConsumed came back whole", drawn.materialsConsumed, gb.materialsConsumed);
        // The month's maintenance draws the yard first (drawMaterials(units, true)): the yard's stock is taken whole and the rest is bought.
        bb.setConstructionMaterials(yard0);
        long consumed3 = gb.materialsConsumed;
        Markets.Draw[] repaired = new Markets.Draw[1];
        quietly(() -> repaired[0] = gb.drawMaterials(bought, true));
        same("a repair draw past 2^31 units takes the yard first: the yard is emptied", gb.getConstructionMaterials(), 0);
        same("...the books took the whole draw, the yard's share and the rest", gb.materialsConsumed - consumed3, Math.round(bought));
        assertTrue("...and the market was drawn on for the units less the yard's, past what an int holds (the salvage is spent)",
                Math.abs(repaired[0].units() - (bought - yard0)) <= 1e-9 * bought && repaired[0].units() > Integer.MAX_VALUE);
        // A draw that is not a finite number takes nothing, on purpose (REV-SQ1b): Math.round(+Infinity) is Long.MAX_VALUE, which the old int cast read as -1.
        bb.setConstructionMaterials(yard0);
        long consumed4 = gb.materialsConsumed;
        double drawn4 = materials.getDrawn();
        Markets.Draw[] endless = new Markets.Draw[1];
        quietly(() -> {
            endless[0] = gb.drawMaterials(Double.POSITIVE_INFINITY, true);
            gb.drawSiteMaterials(Double.POSITIVE_INFINITY);
        });
        assertTrue("a draw of infinitely many units takes nothing: the yard keeps its stock, nothing is booked and the market is not drawn on",
                endless[0].units() == 0 && gb.getConstructionMaterials() == yard0 && gb.materialsConsumed == consumed4 && materials.getDrawn() == drawn4);
        quietly(() -> gb.drawMaterials(Double.NaN, true));
        assertTrue("...and neither does one of not a number", gb.getConstructionMaterials() == yard0 && gb.materialsConsumed == consumed4 && materials.getDrawn() == drawn4);

        /* ============== 10. a template has one stack, and a shed takes what it asks of it (SB1) ============== */
        oneStackPerTemplate(root);
    }

    /* ======================= 10. A TEMPLATE HAS ONE STACK, AND A SHED TAKES WHAT IT ASKS OF IT (SB1) ======================= */

    /** Months the played city runs in section 10, a stack count read at the end of each. */
    static final int PLAYED_MONTHS = 24;

    /**
     * What section 10's fixtures are given to pay a demolition and a plot with: enough for them, and small enough that a double still
     * tells the plot's price from the treasury (a 1e12 added to a treasury of millions lost the price's last four digits).
     */
    static final double FIXTURE_CASH = 1e6;

    /** The most stacks any one id has among these (SB1): the tripwire's own reading, proved on lists it can tell apart. */
    static int mostPerId(java.util.List<Integer> ids) {
        java.util.Map<Integer, Integer> seen = new java.util.HashMap<>();
        int most = 0;
        for (int id : ids) most = Math.max(most, seen.merge(id, 1, Integer::sum));
        return most;
    }

    /** The most stacks any one template has in this manager. */
    static int mostStacksOfOne(BuildingManager bm) {
        java.util.List<Integer> ids = new java.util.ArrayList<>();
        for (BuildingsStacks stack : bm.getStacksView()) ids.add(stack.getBuilding().getId());
        return mostPerId(ids);
    }

    /** Every post the buildings offer, over every kind of job (BuildingManager.getTotalJobs(JobType)). */
    static long postsOf(BuildingManager bm) {
        long n = 0;
        for (long posts : bm.getTotalJobs()) n += posts;
        return n;
    }

    /** A fixture city on a folder of its own, founded the way the playtest's is. */
    static Game cityOn(Path root, String label) {
        Game g = new Game(new GameFiles(root.resolve(label), root.resolve(label + "-no-legacy")), LongPlaytest.founding());
        quietly(g::run);
        return g;
    }

    /** Game's own shedding path, called as Game calls it: a decision of the sector's template and a count, the sector's investor selling, the distress rule not the one deciding. */
    static long shed(Game g, java.lang.reflect.Method retire, java.lang.reflect.Method investor, BuildingsTemplate t, long count) throws Exception {
        BusinessInvestment.Decision d = new BusinessInvestment.Decision(t.getSector(), t, count, "SB1 fixture", true);
        long[] gone = new long[1];
        Exception[] failed = new Exception[1];
        quietly(() -> {
            try { gone[0] = (long) retire.invoke(g, d, investor.invoke(g, t.getSector()), false); }
            catch (Exception e) { failed[0] = e; }
        });
        if (failed[0] != null) throw failed[0];
        return gone[0];
    }

    static void oneStackPerTemplate(Path root) throws Exception {
        out.println("\n--- a template has one stack, ordered, played, saved and loaded; and a shed takes what it asks of it, through Game's own paths ---");

        assertTrue("the tripwire counts a second stack of one id, none among distinct ids, and nothing in an empty list",
                mostPerId(java.util.List.of(3, 5, 3)) == 2 && mostPerId(java.util.List.of(3, 5, 8)) == 1 && mostPerId(java.util.List.of()) == 0);

        /* ---- 10a. every template of the catalogue, ordered three ways, stands in one stack ---- */
        BuildingManager catalogue = new BuildingManager();
        catalogue.initializeTemplates();
        int templates = catalogue.getTemplates().size();
        for (BuildingsTemplate t : catalogue.getTemplates()) {
            catalogue.addStack(t, 3, true);
            catalogue.addStack(t, 4, false);
            catalogue.addStack(t, 5, true);
        }
        boolean merged = true;
        for (BuildingsTemplate t : catalogue.getTemplates()) {
            BuildingsStacks stack = catalogue.getStack(t);
            merged &= stack != null && stack.getQuantity() == 8 && stack.getUnderConstruction() == 4;
        }
        same("every template, ordered finished, on site and finished again, has one stack: as many stacks as templates",
                catalogue.getStackCount(), templates);
        assertTrue("...each holding the three orders' buildings (8 standing, 4 on site), and no template has two", merged && mostStacksOfOne(catalogue) == 1);
        assertTrue("fixture: the catalogue is not one template", templates > 100);

        /* ---- 10b. a played city, every month ---- */
        Game g = cityOn(root, "onestack");
        BuildingManager bm = g.getBuildingManager();
        int[] worst = new int[1];
        int[] read = new int[1];
        int[] mostStacks = new int[1];
        boolean[] ordered = { true };
        PrintStream was = LongPlaytest.out;
        LongPlaytest.out = quiet;
        try {
            for (int month = 0; month < PLAYED_MONTHS; month++) {
                final int m = month;
                quietly(() -> {
                    // ...two orders of one template and a third a year on, and others beside them: a template ordered again joins its stack.
                    if (m == 0 || m == 6 || m == 12) ordered[0] &= LongPlaytest.build(g, "House", 20);
                    if (m == 1 || m == 7) ordered[0] &= LongPlaytest.build(g, "Convenience Store", 3);
                    if (m == 2) ordered[0] &= LongPlaytest.build(g, "Bakery", 1);
                    g.simulateMonths(1);
                });
                worst[0] = Math.max(worst[0], mostStacksOfOne(bm));
                mostStacks[0] = Math.max(mostStacks[0], bm.getStackCount());
                read[0]++;
            }
        } finally {
            LongPlaytest.out = was;
        }
        assertTrue("fixture: every order of the played city was placed", ordered[0]);
        same("the played city's stack count was read at the end of every month", read[0], PLAYED_MONTHS);
        assertTrue("fixture: the city stood more than a handful of kinds of building, so the reading is not of an empty city", mostStacks[0] > 5);
        same("...and in none of them did a template have more than one stack", worst[0], 1);
        out.printf("   %d months, up to %d stacks, %d sheds the played city made itself%n", read[0], mostStacks[0], g.getDemolitionLog().size());

        /* ---- 10c. ...saved, loaded, and played on ---- */
        boolean[] saved = new boolean[1];
        quietly(() -> saved[0] = g.saveGame(10, "buildingstack one stack").ok);
        assertTrue("the played city saves", saved[0]);
        Game back = new Game(g.getGameFiles());
        quietly(() -> back.loadGameSave(10));
        assertTrue("...and its save loads", back.getLoadFailure() == null);
        BuildingManager bb = back.getBuildingManager();
        same("the loaded city has the saved city's stacks, as many", bb.getStackCount(), bm.getStackCount());
        boolean sameStacks = bb.getStackCount() == bm.getStackCount();
        for (int i = 0; sameStacks && i < bm.getStackCount(); i++) {
            BuildingsStacks before = bm.getStacksView().get(i), now = bb.getStacksView().get(i);
            sameStacks = before.getBuilding().getId() == now.getBuilding().getId() && before.getQuantity() == now.getQuantity()
                    && before.getUnderConstruction() == now.getUnderConstruction();
        }
        assertTrue("...in the same order, each of the saved quantity and on-site count (an emptied stack stands again where it stood)", sameStacks);
        same("...and no template has two", mostStacksOfOne(bb), 1);
        quietly(() -> back.simulateMonths(2));
        same("...nor after two more months", mostStacksOfOne(bb), 1);

        /* ---- 10d. the closing path: the city orders a works demolished, and the next month starts with it closed ---- */
        Game c = cityOn(root, "closing");
        BuildingManager cm = c.getBuildingManager();
        BuildingsTemplate station = cm.getTemplateByName("Police Station");
        c.setCashForTest(c.getCash() + FIXTURE_CASH);
        cm.addStack(station, 6, true);
        long standing = cm.getQuantity(station.getId());
        int stacksBefore = cm.getStackCount();
        int loggedBefore = c.getDemolitionLog().size();
        assertTrue("fixture: the city holds the Police Stations it demolishes, and they are the city's to demolish", standing >= 6 && c.isCitysToDemolish(station));
        assertTrue("the city orders two demolished", c.demolish(station, 2));
        same("...they stand until the month starts", cm.getQuantity(station.getId()), standing);
        quietly(() -> c.simulateMonths(1));
        same("the month closes the two, off the stack they stood on, and no others", cm.getQuantity(station.getId()), standing - 2);
        assertTrue("...no stack has left the list", cm.getStackCount() >= stacksBefore);
        assertTrue("...and the closing is in the log, two of them", c.getDemolitionLog().size() == loggedBefore + 1
                && newest(c).quantity == 2 && newest(c).building.equals(station.getName()));
        double steelOfTwo = c.getOldMetal().lastArisen(OldMetal.Source.DEMOLITIONS);
        assertTrue("...with their steel in the city's old metal", steelOfTwo > 0);
        long left = cm.getQuantity(station.getId());
        assertTrue("the city orders the rest demolished", c.demolish(station, left));
        quietly(() -> c.simulateMonths(1));
        same("the next month closes them: none stand", cm.getQuantity(station.getId()), 0);
        near("...and their steel, in the old metal, is the two's times as many as the two's (" + left + " against 2)",
                c.getOldMetal().lastArisen(OldMetal.Source.DEMOLITIONS), steelOfTwo * left / 2.0, 1e-9);
        assertTrue("...and the emptied stack stays in place, where a save indexes it, and no stack has left the list",
                cm.getStack(station) != null && cm.getStackCount() >= stacksBefore);
        same("retiring more of an emptied stack scraps none", cm.retire(station, 3), 0);

        /* ---- 10e. the shedding path: Game.retire(), as the investment pass and the distress rule call it ---- */
        Game s = cityOn(root, "shedding");
        BuildingManager sm = s.getBuildingManager();
        BuildingsTemplate shop = sm.getTemplateByName("Convenience Store");
        long founded = sm.getQuantity(shop.getId());
        s.setCashForTest(s.getCash() + FIXTURE_CASH);
        s.getLandManager().setOwnedSqFt(s.getLandManager().getOwnedSqFt() + 20_000_000L);
        sm.addStack(shop, 8, true);
        sm.addStack(shop, 2, false);
        assertTrue("fixture: the ground of the eight new shops is taken from the land office", s.getLandManager().allocate(shop.getLandSqFt() * 8, shop.getSector()));
        java.lang.reflect.Method investor = Game.class.getDeclaredMethod("sectorInvestor", String.class);
        investor.setAccessible(true);
        java.lang.reflect.Method retire = Game.class.getDeclaredMethod("retire", BusinessInvestment.Decision.class, Investor.class, boolean.class);
        retire.setAccessible(true);
        int shopStacks = sm.getStackCount();
        long stands = sm.getQuantity(shop.getId());
        same("fixture: the city's shops are its founding's and the eight", stands, founded + 8);

        double cash0 = s.getCash(), ground0 = s.getLandManager().getAllocatedSqFt();
        long posts0 = postsOf(sm);
        int logged0 = s.getDemolitionLog().size(), salvaged0 = s.getSalvageThisMonth().size();
        double price = s.getLandManager().buybackPriceFor(shop.getLandSqFt() * 3, shop.getSector());
        long gone = shed(s, retire, investor, shop, 3);
        same("a shed of three scraps three", gone, 3);
        String word = s.getLastInvestment(shop.getSector());
        out.println("      retirement word: " + word);
        assertTrue("the actual retirement says Scrapped with its quantity and plot",
                word.startsWith("Scrapped 3 " + shop.getName() + " - ")
                        && word.contains(" (plot back to the city for "));
        assertTrue("...and still reads as SELLING ahead of land, or BUILDING for an order on site",
                BuildCard.wordKind(word, false, true) == BuildCard.WordKind.SELLING
                        && BuildCard.wordKind(word, true, true) == BuildCard.WordKind.BUILDING);
        same("...off the stack: three fewer stand", sm.getQuantity(shop.getId()), stands - 3);
        same("...the two on site are not touched", sm.getStack(shop).getUnderConstruction(), 2);
        same("...and the stack count is what it was", sm.getStackCount(), shopStacks);
        assertTrue("...the ground of the three goes back to the land office", Math.abs((ground0 - s.getLandManager().getAllocatedSqFt()) - shop.getLandSqFt() * 3) <= 1e-6);
        assertTrue("...the posts of the three are gone", postsOf(sm) == posts0 - shop.getTotalJobs() * 3);
        near("...the treasury paid the plot back at the zone's price, priced before the release", cash0 - s.getCash(), price, 1e-6);
        assertTrue("...the log has the three", s.getDemolitionLog().size() == logged0 + 1 && newest(s).quantity == 3);
        near("...and what was paid for the plot", newest(s).proceeds, price, 1e-6);
        Game.Salvage sold = s.getSalvageThisMonth().size() > salvaged0 ? s.getSalvageThisMonth().get(s.getSalvageThisMonth().size() - 1) : null;
        assertTrue("...its material-sale wording follows what the builders actually paid",
                sold != null && word.contains(" (its material to the builders for ") == (sold.paid() > 0));
        assertTrue("...and the material of the three, a share of it, is sold to the builders: three buildings, units to the share",
                sold != null && sold.buildings() == 3
                        && Math.abs(sold.units() - shop.getConstructionMaterials() * 3.0 * ConstructionControl.SALVAGE_SHARE) <= 1e-9 * shop.getConstructionMaterials());

        // ...asked for more than stands: the rest, never more.
        long rest = sm.getQuantity(shop.getId());
        double ground1 = s.getLandManager().getAllocatedSqFt();
        long posts1 = postsOf(sm);
        int logged1 = s.getDemolitionLog().size();
        long all = shed(s, retire, investor, shop, rest + 50);
        same("a shed asked for fifty more than stand scraps what stands", all, rest);
        same("...none stand, and the two on site are still on site", sm.getQuantity(shop.getId()) + sm.getStack(shop).getUnderConstruction(), 2);
        same("...the stack is left in place, emptied", sm.getStackCount(), shopStacks);
        assertTrue("...its posts are the standing's, not the asking's", postsOf(sm) == posts1 - shop.getTotalJobs() * rest);
        assertTrue("...its ground is no more than the standing's", ground1 - s.getLandManager().getAllocatedSqFt() <= shop.getLandSqFt() * rest + 1e-6);
        assertTrue("...the log has what stood", s.getDemolitionLog().size() == logged1 + 1 && newest(s).quantity == rest);

        // ...asked of an emptied stack: nothing, and nothing is booked.
        double cash2 = s.getCash(), ground2 = s.getLandManager().getAllocatedSqFt();
        int logged2 = s.getDemolitionLog().size(), salvaged2 = s.getSalvageThisMonth().size();
        same("a shed of an emptied stack scraps none", shed(s, retire, investor, shop, 4), 0);
        assertTrue("...and books nothing: no cash, no ground, no log line and no salvage",
                s.getCash() == cash2 && s.getLandManager().getAllocatedSqFt() == ground2
                        && s.getDemolitionLog().size() == logged2 && s.getSalvageThisMonth().size() == salvaged2);
    }

    /** A game's stack of the template named, or null: each game's templates are its own, so a stack is found by the game's own. */
    static BuildingsStacks stackOf(Game g, String name) {
        BuildingManager m = g.getBuildingManager();
        BuildingsTemplate t = m.getTemplateByName(name);
        return t == null ? null : m.getStack(t);
    }
}
