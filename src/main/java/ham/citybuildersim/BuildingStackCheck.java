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
 *  7. below the ceiling nothing reads differently (section 1).
 *
 * The orders here are int-sized, as the game's are; a stack passes the ceiling by two of them, or by the
 * scaler. Written so that it compiles against main's int stack, where it is red: the sums wrap.
 */
public class BuildingStackCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    /** The fixture's months before it is saved with its houses finished: a young town, which keeps the run short. */
    static final int FIXTURE_MONTHS = 3;

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
                founded.simulateMonths(FIXTURE_MONTHS);
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
    }

    /** A game's stack of the template named, or null: each game's templates are its own, so a stack is found by the game's own. */
    static BuildingsStacks stackOf(Game g, String name) {
        BuildingManager m = g.getBuildingManager();
        BuildingsTemplate t = m.getTemplateByName(name);
        return t == null ? null : m.getStack(t);
    }
}
