package ham.citybuildersim;

import ham.citybuildersim.sectors.CoalMining;
import ham.citybuildersim.sectors.CopperMining;
import ham.citybuildersim.sectors.CopperRefining;
import ham.citybuildersim.sectors.ExtractionSector;
import ham.citybuildersim.sectors.Forestry;
import ham.citybuildersim.sectors.HeavyIndustry;
import ham.citybuildersim.sectors.Mining;
import ham.citybuildersim.sectors.ProcessingSector;
import ham.citybuildersim.sectors.Quarrying;
import ham.citybuildersim.sectors.SpreadPlanner;
import ham.citybuildersim.sectors.WoodProducts;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * The build card (0.7.25): BuildCard's figures for every building in
 * buildings.json (139 since 0.7.140) held to
 * the model's own reads - the quote, the land, the staffing tests, the
 * markets, the investors' words and their gates - in a played city.
 *
 * WHY. The card is what a player reads before spending the treasury on a
 * building, and since 0.7.25 every card carries figures the model never
 * printed before: a price per resident or per thousand meals, a maker's
 * value added, land per unit, the investors' word and the first gate this
 * building fails for them. A bar that divided by the wrong figure, a tag on
 * a card that is not the best, an investors' line that claimed an order on
 * site nobody placed, or a "this one:" that named a gate the building
 * passes would be a confident wrong answer on the screen that spends. The
 * screen is checked by eye; this holds what it is drawn from.
 *
 * What this has to prove:
 *   1. EVERY BUILDING HAS A CARD: each of the catalogue's buildings sits in
 *      a group on its category's page - a market building on exactly one -
 *      with a unit above zero or the "adds nothing" state.
 *   2. THE HERO AND THE BARS ARE THE MODEL'S: the hero's figure is the
 *      template's own; bar 1 is the quote at one over the unit, to the bit;
 *      bar 2 is land per unit, the posts the city likely cannot fill per
 *      10,000 served, or the share of an office's posts it could not staff;
 *      value added is the market's prices on what one makes and uses; the
 *      running cost is the upkeep and the posts at today's wages.
 *   3. THE TAGS: only the strict best on a bar, only in a group of two or
 *      more, and never in a group of one.
 *   4. THE NOTES: each group's note reads the sector's own figures.
 *   5. THE INVESTORS' LINE: "on site" exactly when an investor's order with
 *      value is on the site (a fixture order by Luxury Retail), not for the
 *      city's own; the word is Game.getLastInvestment() under the slot the
 *      month files it in, the bank's under "Bank"; and after a load, the
 *      bank's planner sees the bank (the load-path fix of 0.7.25).
 *   6. THE GATES: each "this one:" gate caused by a fixture - no deposit,
 *      nobody licensed, a building the city could not staff, no land, a
 *      loss in the investors' estimate - and none for one that passes them.
 *   7. THE VERDICT: the quote line's verdict in buildStack()'s order, and
 *      for each refusal the same answer buildStack() gives.
 *   8. THE WORD'S KIND (0.7.30): every sector's word in the town has a kind
 *      that is not OTHER; an example of every phrase gets its kind, matched
 *      as whole words and in WordKind's order; and a sector's investors are
 *      its own buildings' lines, its word, and "building" while investors
 *      have an order on site.
 *   9. A RUN OF ORDERS (0.7.40): Build's funding page is sized to a run -
 *      its invoice, each order priced on the yard the ones before it leave,
 *      is what placing them in turn charges, to the bit, and more than the
 *      orders quoted alone when the yard covers part; the gap is that less
 *      the cash, an overdraft in full; and the run is walked as buildStack()
 *      checks it - an order short of ground once the ones before it have
 *      theirs, of a deposit or of licences stops it, with buildStack()'s own
 *      answer there.
 *   10. SERVED (0.7.41): every Build ring and NEEDS YOU row that is a
 *       supply against a demand reads supply over demand, unclamped - each
 *       row its owner's getter, to the bit - and CityNeeds' one verdict on
 *       it is NEEDS YOU's own lines turned over: the same colour as the
 *       row's level wherever the row is listed and nothing is on the way, a
 *       tick only at 100% or more, and Jerus's case - general care at 90%,
 *       off the list - amber and "short", shown on Build's tile before its
 *       tick.
 *   11. THE INTEREST ON ITS WHOLE COST (0.7.122): the gate after the loss
 *       is consider()'s brake - PROFIT_OVER_INTEREST times a month's
 *       interest on the whole cost at the rate the order would be judged at
 *       (Game.interestTest()). Every market building the town's investors
 *       would ask it gets the answer consider()'s own search gives an order
 *       of one (Game.sliceFor()), and a landlord's home, which the mortgage
 *       lender decides, has no such gate; a Bakery paid for from its till,
 *       earning over nothing but under its interest at a raised prime, fails
 *       it with the test's own figures and consider() refuses it, and at
 *       today's prime passes it and consider() builds it; a loss fails it
 *       behind the loss; and under a borrowing ban it fails in the ban's
 *       words.
 *   12. THE MATERIALS' CARDS (0.7.142): every materials building - the nine
 *       industries' - is weighed by the rule its sector orders by, to the
 *       bit: a works by its sector's own appraisal (its whole cost with its
 *       lorries, the planner's bar, its first gate and its words), a mine,
 *       quarry or washing plant as its plan weighs one, a crew under E, a
 *       planted stand not at all; on three hematite sites by fiat an Iron
 *       Mine fits and a Large Open-Pit Mine and a magnetite works do not, in
 *       the plan's words, each resource named by its type; a Quarry on
 *       limestone makes limestone; a Pelletising Plant on imported ore fails
 *       the money gate and the card's "this one:" is the planner's; a Blast
 *       Furnace's, an Oxygen Steel Shop's and a Pulp Mill's own power are the
 *       template's at the tariff; a Scrap Yard standing buys the city's old
 *       steel at Iron & Steel's price, refused for want of it, and a Scrap
 *       Copper Refinery its copper; every good goes to the sectors whose
 *       buildings use it - the smelter's acid to the leach plants - or ships,
 *       and each of the nine pages says so; on a forest town E's refusal is
 *       Forestry's plan's own words, and its room what E allows; and the
 *       land office's hover by type adds up to every offer's fields, an
 *       offer on each copper type's nearest field all of that type; and
 *       Build's field payback fills a magnetite field with magnetite works,
 *       a hematite field under twenty sites with Iron Mines at the old
 *       rule's figure to the bit.
 *
 * Every fixture causes its condition.
 */
public class BuildCardCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-96s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-96s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    static void bits(String label, double actual, double expected) {
        boolean ok = Double.doubleToLongBits(actual) == Double.doubleToLongBits(expected);
        if (!ok) fails++;
        out.printf("%-96s %s  %s against %s%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    static BuildingsTemplate template(Game g, String name) {
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException("no template named " + name);
    }

    /**
     * A played town with workers to spare: the founding, then a bank, shops,
     * two bakeries, six hundred houses, the three basic schools and a college,
     * builders, roads, a clinic, police, power, water and one rail spur
     * standing - and no Institute of Technology, so nobody holds an
     * engineering licence - with land to spare, played two years with every
     * sector held, so no investor's order takes up the people the houses
     * bring (Measured: about 2,400 people, 300 of them out of work.) Then
     * saved and loaded, which lets every sector go again (a hold is a
     * fixture's, not the city's, and is not saved), and played one month, so
     * each sector has a word for the month - the first month after a load,
     * the one the bank's planner used to spend with no bank.
     */
    static Game town(Path root, String name) {
        GameFiles files = new GameFiles(root.resolve(name), root.resolve(name + "-no-legacy"));
        Game built = new Game(files);
        quietly(() -> {
            built.run();
            built.setCashForTest(Founding.WEALTHY_CASH);
            built.getLandManager().setOwnedSqFt(built.getLandManager().getOwnedSqFt() + 80_000_000L);
            for (Sector s : built.getSectors().all()) built.getBusinessInvestment().holdSector(s.key());
            for (String[] w : new String[][] {
                    { "Industrial Bakery", "2" }, { "Coal Power Plant", "1" }, { "Water Treatment Plant", "1" },
                    { "Commercial Bank", "1" }, { "Convenience Store", "6" }, { "House", "600" },
                    { "Elementary School", "1" }, { "Middle School", "1" }, { "High School", "1" },
                    { "Community College", "1" }, { "Construction Depot", "3" }, { "Paved Road", "2" },
                    { "Walk-in Clinic", "1" }, { "Police Station", "1" }, { "Rail Spur", "1" } }) {
                built.buildStack(template(built, w[0]), Integer.parseInt(w[1]), true);
            }
            built.simulateMonths(24);
            built.setCashForTest(Founding.WEALTHY_CASH);
            built.saveGame(10, "buildcardcheck town");
        });
        Game g = new Game(files);
        quietly(() -> {
            g.loadGameSave(10);
            g.simulateMonths(1);
        });
        return g;
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        Path root = Files.createTempDirectory("buildcardcheck");

        Game g = town(root, "town");
        printTown(g);
        everyBuilding(g);
        heroAndBars(g);
        theTags(g);
        theNotes(g);
        theInvestors(root, g);
        theGates(g);
        theVerdict(g);
        theKinds(g);
        theRun(g);
        theServed(g);
        theInterest(g);
        theMaterials(g, root);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** Every page's groups: the market's nine, and the city's five ring by ring. */
    static List<BuildCard.Group> allGroups(Game g) {
        List<BuildCard.Group> all = new ArrayList<>();
        for (BuildAdvice.Category c : BuildAdvice.categories()) {
            if (c.cityBuilds()) {
                for (BuildAdvice.Measure m : BuildAdvice.measuresOf(c.name())) all.addAll(BuildCard.groups(g, c.name(), m));
            } else {
                all.addAll(BuildCard.groups(g, c.name(), null));
            }
        }
        return all;
    }

    /** The town as the cards read it, for the record. */
    static void printTown(Game g) {
        out.printf("%n      the town: month %d, %,d people, cash %s, %,.0f sq ft free%n", g.getMonth(),
                g.getPopulationManager().getPopulation(), Formats.INSTANCE.amount(g.getCash()),
                g.getLandManager().getAvailableSqFt());
        for (BuildCard.Group gr : BuildCard.groups(g, BuildAdvice.INDUSTRY, null)) {
            for (BuildCard.Figures f : gr.cards()) {
                BuildCard.Investors i = BuildCard.investors(g, f.template());
                out.printf("      %-26s %-28s %s%,.0f%s · bar1 %.4g · bar2 %.4g · own %s%n", gr.title(),
                        f.template().getName(), f.verb(), f.figure(), f.words(), f.bar1(), f.bar2(),
                        i.own() == null ? "none" : i.own().kind());
            }
        }
    }

    /* ============================ 1. EVERY BUILDING HAS A CARD ============================ */

    static void everyBuilding(Game g) {
        out.println("\n--- 1. every building has a card: a group on its page, and a unit or \"adds nothing\" ---");
        Set<String> market = new HashSet<>(), city = new HashSet<>();
        int marketTwice = 0;
        boolean units = true, named = true;
        for (BuildAdvice.Category c : BuildAdvice.categories()) {
            List<BuildAdvice.Measure> ms = c.cityBuilds() ? BuildAdvice.measuresOf(c.name()) : java.util.Collections.singletonList(null);
            for (BuildAdvice.Measure m : ms) {
                for (BuildCard.Group gr : BuildCard.groups(g, c.name(), m)) {
                    named &= gr.title() != null && !gr.title().isEmpty();
                    for (BuildCard.Figures f : gr.cards()) {
                        String name = f.template().getName();
                        if (c.cityBuilds()) city.add(name);
                        else if (!market.add(name)) marketTwice++;
                        boolean ok = f.unit() > 0 || f.addsNothing();
                        if (!ok) out.println("      no unit: " + name);
                        units &= ok;
                        named &= BuildCard.group(f.template()) != null && !BuildCard.group(f.template()).isEmpty();
                    }
                }
            }
        }
        int catalogue = g.getBuildingManager().getTemplates().size();
        Set<String> every = new HashSet<>(market);
        every.addAll(city);
        assertTrue("every building in the catalogue (" + catalogue + ") is on a card", every.size() == catalogue);
        assertTrue("...a market building on exactly one, in one group (" + market.size() + " market buildings)",
                marketTwice == 0 && market.size() + city.size() == catalogue);
        assertTrue("...and every card and group has a name", named);
        assertTrue("every card has a unit above zero, or reads \"adds nothing\"", units);
        // The market's groups are the design note's: Industry seven, Shops two - Industry nine since 0.7.62, the
        // wells' and the refinery's after the mines' - and since 0.7.85 (batch O8) oil storage after the refinery's,
        // the refiners' Tank Farm and the city's Strategic Reserve measured in tank room, as the filling stations
        // are their own group in litres (0.7.83) - and since 0.7.86 (batch O9) the ports after oil storage, the
        // city's sea terminals measured in their berths' tonnes (spec-oil 4: "plus Ports") - and since 0.7.91 (batch O10) the
        // oil at sea after the wells', the platforms measured in their wells' slots and the pipelines in kilometres.
        List<String> industry = new ArrayList<>(), shops = new ArrayList<>();
        for (BuildCard.Group gr : BuildCard.groups(g, BuildAdvice.INDUSTRY, null)) industry.add(gr.title());
        for (BuildCard.Group gr : BuildCard.groups(g, BuildAdvice.SHOPS, null)) shops.add(gr.title());
        // ...the mills' and the mines' named for their sectors since 0.7.120 (batch I3, Jerus's Q2): "Steel" and "Iron" until then.
        assertTrue("Industry is fourteen groups by owning sector - Coal Mining's \"Coal\" since 0.7.126, Quarrying's \"Stone\" and Cement &"
                        + " Concrete's \"Cement\" since 0.7.131, copper's \"Copper\" since 0.7.133, Forestry's \"Wood\" since 0.7.138 - and"
                        + " oil storage (0.7.85), the ports (0.7.86) and the oil at sea's platforms and pipelines (0.7.91): " + industry,
                industry.equals(List.of("Food mills", "Food processing", "Iron & Steel", "Fabrication & machinery", "Iron Mining", "Coal",
                        "Stone", "Cement", "Copper", "Wood", "Oil", "Oil platforms", "Oil pipelines", "Refining", "Oil storage", "Ports",
                        "Building materials", "Builders")));
        // ...and the Coal group (0.7.126, batch I6; spec-materials 4: "+ Coal, Copper, Stone, Wood, Cement"): Coal Mining's four.
        List<String> coal = new ArrayList<>();
        for (BuildCard.Group gr : BuildCard.groups(g, BuildAdvice.INDUSTRY, null)) {
            if (gr.title().equals("Coal")) for (BuildCard.Figures f : gr.cards()) coal.add(f.template().getName());
        }
        assertTrue("...the Coal group is Coal Mining's four, in the catalogue's order (0.7.126): " + coal,
                coal.equals(List.of("Surface Coal Mine", "Large Surface Mine", "Underground Coal Mine", "Coal Preparation Plant")));
        // ...and stone's two groups (0.7.131, batch I7; spec-materials 4): Quarrying's three, then Cement & Concrete's three.
        java.util.Map<String, List<String>> stone = new java.util.LinkedHashMap<>();
        for (BuildCard.Group gr : BuildCard.groups(g, BuildAdvice.INDUSTRY, null)) {
            if (!gr.title().equals("Stone") && !gr.title().equals("Cement")) continue;
            List<String> names = new ArrayList<>();
            for (BuildCard.Figures f : gr.cards()) names.add(f.template().getName());
            stone.put(gr.title(), names);
        }
        assertTrue("...the Stone group is Quarrying's three and the Cement group Cement & Concrete's three, in the catalogue's order"
                        + " (0.7.131): " + stone,
                stone.equals(java.util.Map.of("Stone", List.of("Quarry", "Large Quarry", "Dimension Stone Quarry"),
                        "Cement", List.of("Cement Plant", "Large Cement Plant", "Lime Kiln"))));
        // ...and copper's one group (0.7.133, batch I8; spec-materials 4's one word for the chain): Copper Mining's three and Copper
        // Refining's four together, the Wire & Cable Works with Manufacturing's.
        List<String> copper = new ArrayList<>(), fab = new ArrayList<>();
        for (BuildCard.Group gr : BuildCard.groups(g, BuildAdvice.INDUSTRY, null)) {
            if (gr.title().equals("Copper")) for (BuildCard.Figures f : gr.cards()) copper.add(f.template().getName());
            if (gr.title().equals("Fabrication & machinery")) for (BuildCard.Figures f : gr.cards()) fab.add(f.template().getName());
        }
        assertTrue("...the Copper group is Copper Mining's three and Copper Refining's four, in the catalogue's order, and the Wire & Cable"
                        + " Works sits with Manufacturing's (0.7.133): " + copper,
                copper.equals(List.of("Copper Mine & Mill", "Large Copper Mine", "Leach & SX-EW Plant", "Copper Smelter",
                        "Electrolytic Refinery", "Scrap Copper Refinery", "Rod Mill")) && fab.contains("Wire & Cable Works"));
        // ...and Forestry's one group (0.7.138, batch I9; spec-materials 4: "Wood" for Forestry): its two crews and its two
        // planted stands, in the catalogue's order - and since 0.7.140 (batch I10) Wood Products' six mills after them, the chain's
        // one word as copper's two sectors share theirs.
        List<String> wood = new ArrayList<>();
        for (BuildCard.Group gr : BuildCard.groups(g, BuildAdvice.INDUSTRY, null)) {
            if (gr.title().equals("Wood")) for (BuildCard.Figures f : gr.cards()) wood.add(f.template().getName());
        }
        assertTrue("...the Wood group is Forestry's four (0.7.138) and Wood Products' six (0.7.140), in the catalogue's order: " + wood,
                wood.equals(List.of("Logging Camp", "Harvesting Operation", "Tree Plantation", "Woodlot", "Sawmill", "Large Sawmill",
                        "Panel Mill", "Pulp Mill", "Paper Mill", "Wood Pellet Mill")));
        // ...the oil at sea (0.7.91): the wells' group the Oil Well and the Platform Well, the platforms' the jacket in its wells'
        // slots, the pipelines' a kilometre of pipe - all the investors'.
        java.util.Map<String, List<String>> oilGroups = new java.util.LinkedHashMap<>();
        boolean sea = true;
        for (BuildCard.Group gr : BuildCard.groups(g, BuildAdvice.INDUSTRY, null)) {
            if (!gr.title().startsWith("Oil") || gr.title().equals("Oil storage")) continue;
            List<String> names = new ArrayList<>();
            for (BuildCard.Figures f : gr.cards()) {
                names.add(f.template().getName());
                sea &= f.market() && !BuildCard.citys(f.template())
                        && (f.template().isPlatform() ? f.kind() == BuildCard.Kind.SLOTS && f.figure() == f.template().platformSlots()
                        : f.template().isPipeline() ? f.kind() == BuildCard.Kind.PIPE && f.figure() == 1
                        : f.kind() == BuildCard.Kind.MAKER);
            }
            oilGroups.put(gr.title(), names);
        }
        assertTrue("...the wells' group is the Oil Well and the Platform Well, makers both; the platforms' the Offshore Platform"
                + " in its wells' slots; the pipelines' the Crude Pipeline a kilometre: " + oilGroups,
                sea && oilGroups.equals(java.util.Map.of("Oil", List.of("Oil Well", "Platform Well"),
                        "Oil platforms", List.of("Offshore Platform"), "Oil pipelines", List.of("Crude Pipeline"))));
        // ...oil storage the Tank Farm and the Strategic Reserve, the reserve the city's alone (0.7.85).
        BuildCard.Group storage = null;
        for (BuildCard.Group gr : BuildCard.groups(g, BuildAdvice.INDUSTRY, null)) if (gr.title().equals("Oil storage")) storage = gr;
        List<String> stored = new ArrayList<>();
        boolean tanks = storage != null;
        if (storage != null) for (BuildCard.Figures f : storage.cards()) {
            stored.add(f.template().getName());
            tanks &= f.kind() == BuildCard.Kind.TANKS && f.market() == f.template().isOwnedBySector();
        }
        assertTrue("...oil storage is the refiners' Tank Farm and the city's Strategic Reserve, measured in tank room, the"
                + " reserve built by the city alone: " + stored, tanks && stored.equals(List.of("Tank Farm", "Strategic Reserve")));
        // ...the ports the four sea terminals, the city's alone, measured in their berths' tonnes a year (0.7.86).
        List<String> berthed = new ArrayList<>();
        boolean ports = false;
        for (BuildCard.Group gr : BuildCard.groups(g, BuildAdvice.INDUSTRY, null)) {
            if (!gr.title().equals("Ports")) continue;
            ports = true;
            for (BuildCard.Figures f : gr.cards()) {
                berthed.add(f.template().getName());
                ports &= f.kind() == BuildCard.Kind.BERTHS && !f.market() && BuildCard.citys(f.template())
                        && f.figure() == f.template().berthTonnesAYear();
            }
        }
        assertTrue("...the ports are the Tanker, Bulk, Container and General Cargo Terminals, measured in their berths' tonnes"
                + " a year, built by the city alone: " + berthed, ports && berthed.equals(List.of("Tanker Terminal", "Bulk Terminal",
                "Container Terminal", "General Cargo Terminal")));
        // ...and the grocers' filling stations their own group since 0.7.83 (batch O6), measured in litres.
        assertTrue("Shops is the groceries, the filling stations (0.7.83) and the bank's branches: " + shops,
                shops.equals(List.of("Groceries", "Filling stations", "The bank's branches")));
    }

    /* ============================ 2. THE HERO AND THE BARS ============================ */

    static void heroAndBars(Game g) {
        out.println("\n--- 2. the hero and the bars are the model's: the quote, the land, the staffing, the markets ---");
        double[] fill = g.getPopulationManager().getJobFillRate();
        double[] wages = g.getPopulationManager().getWagesPerType();
        int cards = 0, makers = 0, offices = 0, cityCards = 0, landBars = 0, roadCards = 0, conversion = 0, washers = 0;
        boolean hero = true, bar1 = true, bar2 = true, va = true, run = true, sticker = true, head = true;
        for (BuildCard.Group gr : allGroups(g)) {
            for (BuildCard.Figures f : gr.cards()) {
                cards++;
                BuildingsTemplate t = f.template();
                Game.BuildQuote q = g.quoteBuild(t, 1);
                sticker &= f.price() == q.total && f.sticker() == q.sticker;
                BuildingsStacks st = g.getBuildingManager().getStack(t);
                int site = st == null ? 0 : st.getUnderConstruction();
                head &= f.owned() == g.getBuildingManager().getQuantity(t.getId()) && f.onSite() == site
                        && f.siteMonths() == (site > 0 ? g.onSiteMonths(t) : 0)
                        && f.landFree() == g.getLandManager().getAvailableSqFt();
                double scale = f.kind() == BuildCard.Kind.MEALS || f.kind() == BuildCard.Kind.PUMP
                        || f.kind() == BuildCard.Kind.TANKS || f.kind() == BuildCard.Kind.BERTHS ? 1000 : 1;

                // The hero, off the template's own fields.
                double expectFigure;
                switch (f.kind()) {
                    case HOMES:     expectFigure = t.getCapacity(); break;
                    case CUSTOMERS: case MEALS: expectFigure = t.getCoverage(); break;
                    case BRANCH:    expectFigure = Bank.CUSTOMERS_PER_BRANCH; break;
                    case RAIL:      expectFigure = t.getRailCapacity(); break;
                    // ...a filling station's litres a month at its typical throughput (0.7.83).
                    case PUMP:      expectFigure = t.pumpLitres(); break;
                    // ...oil storage's tank room, in litres (0.7.85).
                    case TANKS:     expectFigure = t.getStock(); break;
                    // ...a sea terminal's berth's tonnes a year (0.7.86).
                    case BERTHS:    expectFigure = t.berthTonnesAYear(); break;
                    // ...an offshore platform's wells' slots, and a pipeline's kilometre (0.7.91).
                    case SLOTS:     expectFigure = t.platformSlots(); break;
                    case PIPE:      expectFigure = 1; break;
                    case POINTS:    expectFigure = t.makes(Good.BUILDING_WORK); break;
                    // ...a crude unit's the crude it refines (0.7.76): its products are the slate of it; a conversion
                    // unit's the feed it upgrades (0.7.80), a stream of the crude units' run.
                    // ...and a coal preparation plant's the raw coal it washes (0.7.126), a stream of the coking mines'.
                    case MAKER: case OFFICE: expectFigure = ham.citybuildersim.sectors.Refining.isCrudeUnit(t)
                            ? t.uses(Good.CRUDE) : ham.citybuildersim.sectors.Refining.isConversionUnit(t)
                            ? t.feedPerMonth() : t.getWash() > 0 ? t.getWash() : t.makes(f.good()); break;
                    default: {
                        BuildAdvice.Measure m = measureOf(gr);
                        expectFigure = m.kind() == BuildAdvice.Kind.ROADS ? t.getCapacity() : BuildAdvice.unit(g, m, t);
                    }
                }
                if (f.figure() != expectFigure) { hero = false; out.println("      hero: " + t.getName()); }

                // Value added, by the markets' own prices - an office's work at what the world pays a seat.
                if (f.inMonths()) {
                    double v = 0;
                    // What it makes: the template's, a crude unit's slate (0.7.76, Refining.madeBy()).
                    for (Map.Entry<Good, Double> e : ham.citybuildersim.sectors.Refining.madeBy(t).entrySet()) {
                        Good good = e.getKey();
                        if (!good.traded()) continue;
                        v += e.getValue() * (f.kind() == BuildCard.Kind.OFFICE
                                ? g.getSectors().businessServices().priceOfSeat(good)
                                : g.getMarkets().get(good).getLocalPrice());
                    }
                    for (Map.Entry<Good, Double> e : t.goodsUsed().entrySet()) {
                        if (e.getKey().traded()) v -= e.getValue() * g.getMarkets().get(e.getKey()).getLocalPrice();
                    }
                    // ...a conversion unit's feed (0.7.80) at what it would make with no unit: residue (4 x fuel oil - diesel) / 3,
                    // the rest its own product's price.
                    if (ham.citybuildersim.sectors.Refining.isConversionUnit(t)) {
                        ham.citybuildersim.sectors.RefineryFlow.Stream feed = t.refineryUnit().feed();
                        double each = feed.residue()
                                ? (4 * g.getMarkets().get(Good.FUEL_OIL).getLocalPrice() - g.getMarkets().get(Good.DIESEL).getLocalPrice()) / 3
                                : g.getMarkets().get(feed.leftover()).getLocalPrice();
                        v -= t.feedPerMonth() * each;
                        conversion++;
                    }
                    // ...and a coal preparation plant's (0.7.126): what it adds to Coal Mining's flow at the markets' prices.
                    if (t.getWash() > 0) {
                        for (Map.Entry<Good, Double> e : g.getSectors().coalMining().madeBy(t).entrySet()) {
                            v += e.getValue() * g.getMarkets().get(e.getKey()).getLocalPrice();
                        }
                        washers++;
                    }
                    if (f.valueAdded() != v || f.unit() != v) { va = false; out.println("      value added: " + t.getName()); }
                    if (f.kind() == BuildCard.Kind.OFFICE) offices++; else makers++;
                }

                // Bar 1: the quote at one over the unit, to the bit - and a road's (0.7.70), its life
                // (BuildAdvice.lifetime()) over the trips it takes off the road (BuildAdvice.unit()).
                BuildAdvice.Measure ring = f.kind() == BuildCard.Kind.CITY ? measureOf(gr) : null;
                boolean road = ring != null && ring.kind() == BuildAdvice.Kind.ROADS;
                double off = road ? BuildAdvice.unit(g, ring, t) : Double.NaN;
                double expect1 = road
                        ? (off > 0 ? BuildAdvice.lifetime(g, t, f.landFree(), BuildAdvice.onSite(g, ring)) / off : Double.POSITIVE_INFINITY)
                        : f.kind() == BuildCard.Kind.CITY
                        ? (f.unit() > 0 ? q.total / f.unit() : Double.POSITIVE_INFINITY)
                        : f.unit() > 0 ? q.total / f.unit() * scale : Double.NaN;
                if (road) roadCards++;
                if (Double.doubleToLongBits(f.bar1()) != Double.doubleToLongBits(expect1)) {
                    bar1 = false;
                    out.println("      bar 1: " + t.getName() + " " + f.bar1() + " against " + expect1);
                }

                // Bar 2, by its kind.
                double expect2;
                switch (f.bar2Kind()) {
                    case LAND:
                        landBars++;
                        expect2 = road ? (off > 0 ? t.getLandSqFt() / off : Double.POSITIVE_INFINITY)
                                : f.unit() > 0 ? t.getLandSqFt() / f.unit() * scale : Double.NaN;
                        break;
                    case UNSTAFFABLE:
                        expect2 = f.unit() > 0 ? (1 - BuildCard.ownerOf(g, t).staffing(t).share) * 100 : Double.NaN;
                        break;
                    case UNFILLED:
                        cityCards++;
                        expect2 = f.unit() > 0 ? BuildAdvice.unfilledPosts(t, fill) * 10_000.0 / f.unit() : Double.POSITIVE_INFINITY;
                        break;
                    default:
                        // NONE: a city building with no posts draws no staff bar.
                        cityCards++;
                        expect2 = Double.NaN;
                }
                if (Double.doubleToLongBits(f.bar2()) != Double.doubleToLongBits(expect2)) {
                    bar2 = false;
                    out.println("      bar 2: " + t.getName() + " " + f.bar2() + " against " + expect2);
                }
                boolean kindOk = road ? f.bar2Kind() == BuildCard.Bar2.LAND
                        : f.kind() == BuildCard.Kind.CITY
                        ? f.bar2Kind() == (BuildAdvice.hasPosts(t) ? BuildCard.Bar2.UNFILLED : BuildCard.Bar2.NONE)
                        : f.bar2Kind() == (f.kind() == BuildCard.Kind.OFFICE ? BuildCard.Bar2.UNSTAFFABLE : BuildCard.Bar2.LAND);
                if (!kindOk) { bar2 = false; out.println("      bar 2 kind: " + t.getName()); }

                // What one costs to run: the upkeep and the posts at today's wages.
                double bill = t.getUpkeep();
                for (JobType job : JobType.values()) {
                    if (t.getJobs(job) > 0 && wages != null && job.ordinal() < wages.length) bill += t.getJobs(job) * wages[job.ordinal()];
                }
                if (Math.abs(f.running() - bill) > 1e-9 * Math.max(1, bill)) { run = false; out.println("      runs: " + t.getName()); }
            }
        }
        out.printf("      %d cards: %d makers and %d offices in value added, %d with a land bar, %d city cards%n",
                cards, makers, offices, landBars, cityCards);
        assertTrue("every card's price and sticker are Game.quoteBuild(t, 1)'s", sticker);
        assertTrue("...its head the count standing, what is on site and its wait, and the land free the city's own", head);
        assertTrue("the hero's figure is the template's own (capacity, coverage, rail, points, its first good, a crude unit's crude,"
                + " a conversion unit's feed, a preparation plant's raw coal, oil storage's tank room, a terminal's berth)"
                + " or a city building's served at today's staffing", hero);
        assertTrue("value added is what one makes less what it uses at the markets' prices, an office's at"
                + " the price of a seat, a conversion unit's less its feed at what that makes unworked, a preparation plant's what it"
                + " adds to coal's flow (" + makers + " makers, " + offices + " offices, " + conversion + " conversion units, " + washers
                + " plant)", va && makers > 0 && offices == 3 && conversion == 14 && washers == 1);
        assertTrue("bar 1 is the quote at one over the unit, to the bit, on every card but a road's, whose is its life"
                + " over the trips it takes off the road (0.7.70; " + roadCards + " road cards)", bar1 && roadCards > 0);
        assertTrue("bar 2 is land per unit on the market's cards and per trip off on a road's, unstaffable posts of 100"
                + " on an office's, and unfilled posts per 10,000 served on the other city cards'", bar2 && landBars > 0);
        assertTrue("the running cost is the upkeep and the posts at today's wages", run);
    }

    static int onSite(BuildingManager bm, BuildingsTemplate t) {
        BuildingsStacks s = bm.getStack(t);
        return s == null ? 0 : s.getUnderConstruction();
    }

    /** The measure a city group was drawn for: the ring whose label is its title. */
    static BuildAdvice.Measure measureOf(BuildCard.Group gr) {
        BuildingsTemplate t = gr.cards().get(0).template();
        BuildAdvice.Category c = BuildAdvice.categoryOf(t.getCategory());
        for (BuildAdvice.Measure m : BuildAdvice.measuresOf(c.name())) if (m.label().equals(gr.title())) return m;
        throw new IllegalStateException("no ring called " + gr.title());
    }

    /* ============================ 3. THE TAGS ============================ */

    static void theTags(Game g) {
        out.println("\n--- 3. the tags: only the strict best on a bar, only in a group of two or more ---");
        int tags = 0, ones = 0, groupsOfMany = 0;
        boolean strict = true, lonely = true;
        for (BuildCard.Group gr : allGroups(g)) {
            if (gr.cards().size() > 1) groupsOfMany++;
            for (int bar = 1; bar <= 2; bar++) {
                double least = Double.NaN, most = Double.NaN;
                for (BuildCard.Figures f : gr.cards()) {
                    double v = bar == 1 ? f.bar1() : f.bar2();
                    if (!Double.isFinite(v)) continue;
                    least = Double.isNaN(least) ? v : Math.min(least, v);
                    most = Double.isNaN(most) ? v : Math.max(most, v);
                }
                for (BuildCard.Figures f : gr.cards()) {
                    double v = bar == 1 ? f.bar1() : f.bar2();
                    boolean tagged = bar == 1 ? gr.best1(f) : gr.best2(f);
                    boolean should = gr.cards().size() > 1 && Double.isFinite(v) && v == least && least < most;
                    if (tagged) tags++;
                    if (tagged != should) { strict = false; out.println("      " + gr.title() + ": " + f.template().getName() + " bar " + bar); }
                    if (gr.cards().size() == 1 && tagged) lonely = false;
                }
            }
            if (gr.cards().size() == 1) {
                ones++;
                BuildCard.Figures f = gr.cards().get(0);
                lonely &= !gr.track() && !gr.best1(f) && !gr.best2(f);
            }
        }
        out.printf("      %d tags in %d groups of two or more; %d groups of one%n", tags, groupsOfMany, ones);
        assertTrue("a tag is on the lowest figure of a bar, strictly below the highest, and on no other", strict && tags > 0);
        assertTrue("a group of one has no tag and draws no track (" + ones + " groups of one)", lonely && ones > 0);
    }

    /* ============================ 4. THE NOTES ============================ */

    static void theNotes(Game g) {
        out.println("\n--- 4. the notes: each group's note reads the sector's own figures ---");
        Sectors s = g.getSectors();
        boolean ok = true;
        int made = 0;
        for (BuildCard.Group gr : allGroups(g)) {
            BuildCard.Note n = gr.note();
            switch (n.kind()) {
                case DOORS:  ok &= n.a() == s.realEstate().doorShortfall(false) && n.b() == s.realEstate().doorShortfall(true); break;
                case SHOPS:  ok &= n.a() == s.retail().getStoreCoverage() && n.b() == s.retail().getPopulation(); break;
                case LUXURY: ok &= n.a() == s.luxuryRetail().getWanted() && n.b() == s.luxuryRetail().coverage(); break;
                case MEALS:  ok &= n.a() == s.restaurants().getWanted() && n.b() == s.restaurants().seats(); break;
                case RAIL:   ok &= n.a() == s.rail().getTradeTonnes() && n.b() == s.rail().getCapacityTonnes(); break;
                case PUMP:   ok &= n.a() == s.retail().getPumpLitres() + s.retail().getQueueLitres()
                                     && n.b() == s.retail().getPumpCapacity() && n.good() == Good.PETROL; break;
                case MADE: {
                    made++;
                    Sector owner = s.ownerOf(gr.cards().get(0).template());
                    ok &= n.good() == owner.planningGood() && n.a() == g.getMarkets().get(n.good()).getDemand()
                            && n.b() == owner.getCapacity(n.good());
                    break;
                }
                default: break;
            }
        }
        boolean homes = BuildCard.groups(g, BuildAdvice.HOMES, null).get(0).note().kind() == BuildCard.NoteKind.DOORS;
        assertTrue("homes' doors, the shops' coverage, the counters', the kitchens', the rail's and each maker"
                + " group's planning good are the sectors' own (" + made + " maker groups)", ok && homes && made > 0);
    }

    /* ============================ 5. THE INVESTORS' LINE ============================ */

    static void theInvestors(Path root, Game g) {
        out.println("\n--- 5. the investors' line: on site only for an investor's order; the word is the month's ---");
        BuildingsTemplate store = template(g, "Department Store"), grocery = template(g, "Small Grocery Store");
        // The words: each market building's is the month's, under the slot Game files it in.
        boolean words = true;
        int said = 0;
        for (BuildAdvice.Category c : BuildAdvice.categories()) {
            if (c.cityBuilds()) continue;
            for (BuildCard.Group gr : BuildCard.groups(g, c.name(), null)) {
                for (BuildCard.Figures f : gr.cards()) {
                    // ...a market card's: the city's own Strategic Reserve (0.7.85) has no investors.
                    if (!f.market()) continue;
                    BuildCard.Investors i = BuildCard.investors(g, f.template());
                    words &= i.word().equals(g.getLastInvestment(i.slot()))
                            && i.slot().equals(ham.citybuildersim.sectors.Retail.isStation(f.template()) ? Game.STATIONS_SLOT
                                    : f.template().isOwnedBySector() ? f.template().getSector() : "Bank");
                    if (!i.word().isEmpty()) said++;
                }
            }
        }
        assertTrue("every market card's word is Game.getLastInvestment() under its sector, the branch's under"
                + " \"Bank\" and a filling station's under the forecourts' own (0.7.83; " + said
                + " of them with a word, the month after the load)", words && said > 0);
        assertTrue("...and the bank has a word of its own, filed under \"Bank\": "
                + g.getLastInvestment("Bank"), !g.getLastInvestment("Bank").isEmpty());

        // Fixture: three department stores ordered by Luxury Retail, and two
        // grocery stores by the city - neither on site before.
        BuildingManager bm = g.getBuildingManager();
        assertTrue("fixture: no department store and no grocery store on site before the orders",
                onSite(bm, store) == 0 && onSite(bm, grocery) == 0 && !BuildCard.investors(g, store).theirs());
        boolean[] placed = new boolean[1];
        quietly(() -> {
            g.getSectors().luxuryRetail().addCash(Founding.WEALTHY_CASH);
            placed[0] = g.buildFor(g.getSectorInvestor(g.getSectors().luxuryRetail().key()), store, 3);
            g.buildStack(grocery, 2, false);
        });
        assertTrue("fixture: Luxury Retail's order of three department stores went on site",
                placed[0] && onSite(bm, store) == 3);
        BuildCard.Investors b = BuildCard.investors(g, store), d = BuildCard.investors(g, grocery);
        assertTrue("the department store reads \"Investors are building\", with the three on site and their wait",
                b.theirs() && !b.yoursToo() && b.onSite() == 3
                && Double.doubleToLongBits(b.months()) == Double.doubleToLongBits(g.onSiteMonths(store)));
        assertTrue("...and says no \"this one:\" while they are, whatever its gate", !b.showOwn());
        assertTrue("the city's own two grocery stores on site are not an investors' order", !d.theirs() && d.onSite() == 2);
        quietly(() -> g.buildStack(store, 1, false));
        assertTrue("a department store of the city's beside theirs reads \"yours among them\"",
                BuildCard.investors(g, store).yoursToo() && BuildCard.investors(g, store).onSite() == 4);

        // After a load the word is the one saved (0.7.102, A22) - and the bank's planner sees the bank.
        quietly(() -> g.saveGame(10, "buildcardcheck"));
        Game twin = new Game(new GameFiles(root.resolve("town"), root.resolve("town-no-legacy")));
        quietly(() -> twin.loadGameSave(10));
        /*
         * BEFORE 0.7.102: "loaded, no market card has a word until a month runs (the words are not saved)". Jerus's
         * A22 saves them (DataSave.lastInvestment), so a loaded card reads the word its city had, card for card.
         */
        boolean same = true;
        int worded = 0;
        for (BuildingsTemplate t : twin.getBuildingManager().getTemplates()) {
            if (BuildCard.kindOf(t) == BuildCard.Kind.CITY) continue;
            String word = BuildCard.investors(twin, t).word();
            same &= word.equals(BuildCard.investors(g, t).word());
            if (!word.isEmpty()) worded++;
        }
        assertTrue("fixture: the town's market cards had words before the save (" + worded + " after it)", worded > 0);
        assertTrue("loaded, every market card has the word it had: the words are saved (0.7.102)", same
                && twin.getLastInvestments().equals(g.getLastInvestments()));
        assertTrue("...while no month has run since the load (Game.hasMonthRun()), as the month's flows are not saved",
                !twin.hasMonthRun() && g.hasMonthRun());
        BuildCard.Note lux = BuildCard.groups(twin, BuildAdvice.LUXURY, null).get(0).note();
        BuildCard.Note meals = BuildCard.groups(twin, BuildAdvice.RESTAURANTS, null).get(0).note();
        assertTrue("loaded, the customers who came and the meals wanted are not counted yet, not zero (the save"
                + " does not carry the month's flows); the counters and the kitchens are",
                Double.isNaN(lux.a()) && Double.isNaN(meals.a()) && lux.b() == twin.getSectors().luxuryRetail().coverage()
                && meals.b() == twin.getSectors().restaurants().seats());
        String bankNow = twin.getBusinessInvestment().planBank().reason;
        assertTrue("loaded, the bank's planner sees the bank: not \"no bank\" (" + bankNow + ")", !"no bank".equals(bankNow));
        quietly(() -> twin.simulateMonths(1));
        lux = BuildCard.groups(twin, BuildAdvice.LUXURY, null).get(0).note();
        assertTrue("...and a month later they are counted: " + Formats.INSTANCE.count(lux.a()) + " customers came",
                lux.a() == twin.getSectors().luxuryRetail().getWanted());
        assertTrue("...and the month after the load files a word for the bank that is not \"no bank\": "
                + twin.getLastInvestment("Bank"), !twin.getLastInvestment("Bank").isEmpty()
                && !twin.getLastInvestment("Bank").endsWith("no bank"));
    }

    /* ============================ 6. THE GATES ============================ */

    static void theGates(Game g) {
        out.println("\n--- 6. the gates: each \"this one:\" caused by a fixture, in buildStack()'s order ---");
        LandManager land = g.getLandManager();

        // No deposit: the ground's deposits taken away.
        BuildingsTemplate mine = g.getBuildingManager().getTemplate(Mining.IRON_MINE);
        int deposits = land.getIronDeposits();
        double reserve = land.getIronReserveTonnes();
        land.restoreIron(0, reserve);
        BuildCard.Gate gate = BuildCard.investors(g, mine).own();
        assertTrue("with no deposit, the mine's gate is the deposit, with what the city owns and has committed",
                gate != null && gate.kind() == BuildCard.GateKind.DEPOSIT && gate.a() == 0 && gate.b() == g.minesCommitted());
        land.restoreIron(deposits, reserve);

        // Nobody licensed: no Institute of Technology, so no engineering licence.
        BuildingsTemplate eso = template(g, "Engineering Services Office");
        double spare = g.getPopulationManager().spareLicences(eso.getRequiresLicence());
        gate = BuildCard.investors(g, eso).own();
        assertTrue("fixture: the town has no spare " + eso.getRequiresLicence() + " licences (" + spare + ")",
                spare < g.licencesNeededFor(eso, 1));
        assertTrue("the engineering office's gate is the licence: what one needs and what the city has",
                gate != null && gate.kind() == BuildCard.GateKind.LICENCE && gate.licence() == eso.getRequiresLicence()
                && gate.a() == g.licencesNeededFor(eso, 1) && gate.b() == spare);

        // A building the town could not staff: a rail terminal's 1,482 posts.
        BuildingsTemplate terminal = template(g, "Rail Terminal");
        Sector.Staffing staffing = BuildCard.ownerOf(g, terminal).staffing(terminal);
        gate = BuildCard.investors(g, terminal).own();
        assertTrue("fixture: the town could staff " + Math.round(staffing.share * 100) + "% of a rail terminal",
                !staffing.passes());
        assertTrue("its gate is the staffing test, in the test's own words: " + (gate == null ? null : gate.why()),
                gate != null && gate.kind() == BuildCard.GateKind.STAFFING && gate.why().equals(staffing.why(terminal.getName())));

        // No land: the free ground cut below a house's.
        BuildingsTemplate house = template(g, "House");
        double owned = land.getOwnedSqFt();
        land.setOwnedSqFt(land.getAllocatedSqFt() + house.getLandSqFt() / 2);
        gate = BuildCard.investors(g, house).own();
        assertTrue("with half a house's ground free, the house's gate is the land, with what it needs and what is free",
                gate != null && gate.kind() == BuildCard.GateKind.LAND && gate.a() == house.getLandSqFt()
                && gate.b() == land.getAvailableSqFt());
        land.setOwnedSqFt(owned);

        // A loss: another spur where the rail already carries every tonne the town trades.
        BuildingsTemplate spur = template(g, "Rail Spur");
        ham.citybuildersim.sectors.Rail rail = g.getSectors().rail();
        BuildCard.Investors i = BuildCard.investors(g, spur);
        assertTrue(String.format("fixture: the standing spur carries every tonne the town trades (%,.0f of %,.0f t)",
                rail.getTradeTonnes(), rail.getCapacityTonnes()), rail.getCapacityTonnes() >= rail.getTradeTonnes());
        assertTrue("another spur's gate is a loss, what the investors' estimate says it would lose: "
                + Formats.INSTANCE.amount(-i.estimate()), i.own() != null && i.own().kind() == BuildCard.GateKind.LOSS
                && i.own().a() == -i.estimate() && i.estimate() <= 0
                && i.estimate() == g.getBusinessInvestment().estimatedMonthlyProfit(rail.key(), spur));
        assertTrue("...worded as a loss, not as no feed: the spur is no refinery's unit (0.7.103)",
                i.own() != null && i.own().why() == null && !BuildCard.unfed(g, spur));

        // THE TWO CARD WORDS (0.7.103, batch P4; Jerus's B). An Oil Well's count is the dry sites alone: "dry" on it.
        BuildingsTemplate well = template(g, "Oil Well");
        gate = BuildCard.investors(g, well).own();
        assertTrue("an Oil Well with no oil site free: its gate is the deposit, its count the dry sites (Game.sitesFor()),"
                + " named so (0.7.103)", !g.hasDepositFor(well, 1) && gate != null
                && gate.kind() == BuildCard.GateKind.DEPOSIT && BuildCard.DRY_OIL.equals(gate.why())
                && gate.a() == g.sitesFor(well) && gate.b() == g.committedFor(well));
        assertTrue("...every gate's report the same, and its card's count says \"dry\" where no other building's does",
                BuildCard.DRY_OIL.equals(BuildCard.appraise(g, well).first().why())
                && " dry".equals(BuildCard.siteCountWord(well)) && BuildCard.siteCountWord(mine).isEmpty()
                && BuildCard.siteCountWord(template(g, "Platform Well")).isEmpty()
                && "iron".equals(BuildCard.depositWordFor(mine, Resource.IRON)));
        // ...and a conversion unit with no crude unit standing: no feed, not a loss.
        BuildingsTemplate lube = template(g, "Small Lube Plant");
        BuildCard.Investors fed = BuildCard.investors(g, lube);
        double crudeUnits = g.getBuildingManager().totalBySector(Sectors.REFINING,
                t -> ham.citybuildersim.sectors.Refining.isCrudeUnit(t) ? 1 : 0);
        assertTrue("fixture: no crude unit stands in the town", crudeUnits == 0 && BuildCard.unfed(g, lube));
        assertTrue("a Small Lube Plant with nothing to feed it: its gate says no feed where it read a loss of "
                + Formats.INSTANCE.amount(-fed.estimate()) + " a month (0.7.103)", fed.own() != null
                && fed.own().kind() == BuildCard.GateKind.LOSS && BuildCard.NO_FEED.equals(fed.own().why())
                && fed.estimate() <= 0 && fed.own().a() == -fed.estimate());
        assertTrue("...and every gate's report the same",
                BuildCard.NO_FEED.equals(BuildCard.appraise(g, lube).first().why()));

        // ...and a building that passes them all has none - since 0.7.122 the last of them consider()'s, the interest on
        // its whole cost, which a landlord's home is not asked (the mortgage lender decides it).
        int clear = 0;
        boolean none = true;
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            if (BuildCard.kindOf(t) == BuildCard.Kind.CITY) continue;
            BuildCard.Investors inv = BuildCard.investors(g, t);
            String owner = inv.owner().key();
            boolean passes = g.hasDepositFor(t, 1) && g.hasLicencesFor(t, 1) && inv.owner().staffing(t).passes()
                    && t.getLandSqFt() <= land.getAvailableSqFt() && inv.estimate() > 0
                    && (g.buysOnMortgage(owner, t)
                        || g.carriesItsWholeCost(owner, inv.estimate(), g.getBusinessInvestment().getCostOf(t, 1)));
            if (passes) clear++;
            none &= passes == (inv.own() == null);
        }
        assertTrue("a market building that passes every gate - the interest on its whole cost the last - has no \"this"
                + " one:\", and one that fails one has it (" + clear + " pass)", none && clear > 0);
    }

    /* ============================ 7. THE VERDICT ============================ */

    static void theVerdict(Game g) {
        out.println("\n--- 7. the verdict: in buildStack()'s order, and buildStack()'s own answer ---");
        LandManager land = g.getLandManager();

        BuildingsTemplate mine = g.getBuildingManager().getTemplate(Mining.IRON_MINE);
        int deposits = land.getIronDeposits();
        double reserve = land.getIronReserveTonnes();
        land.restoreIron(0, reserve);
        BuildCard.Verdict v = BuildCard.verdict(g, mine, 1);
        assertTrue("no deposit: the verdict is NO_DEPOSIT, and buildStack() says NO_DEPOSIT",
                v.kind() == BuildCard.VerdictKind.NO_DEPOSIT && g.buildStack(mine, 1, false) == Game.BuildResult.NO_DEPOSIT);
        land.restoreIron(deposits, reserve);

        BuildingsTemplate eso = template(g, "Engineering Services Office");
        v = BuildCard.verdict(g, eso, 1);
        assertTrue("nobody licensed: NO_LICENCE, with the licences it needs, and buildStack() says NO_LICENCE",
                v.kind() == BuildCard.VerdictKind.NO_LICENCE && v.figure() == g.licencesNeededFor(eso, 1)
                && g.buildStack(eso, 1, false) == Game.BuildResult.NO_LICENCE);

        BuildingsTemplate house = template(g, "House");
        double owned = land.getOwnedSqFt();
        land.setOwnedSqFt(land.getAllocatedSqFt() + house.getLandSqFt() * 2.5);
        v = BuildCard.verdict(g, house, 3);
        assertTrue("short of land for three houses: NO_LAND by the quote's shortfall, and buildStack() says NO_LAND",
                v.kind() == BuildCard.VerdictKind.NO_LAND && v.figure() == v.quote().landNeeded - v.quote().landFree
                && v.figure() == house.getLandSqFt() / 2 && g.buildStack(house, 3, false) == Game.BuildResult.NO_LAND);
        land.setOwnedSqFt(owned);

        double cash = g.getCash();
        Game.BuildQuote q = g.quoteBuild(house, 3);
        g.setCashForTest(q.total / 2);
        v = BuildCard.verdict(g, house, 3);
        assertTrue("short of cash: BILL by the quote less the cash, and buildStack() says NEEDS_FUNDING",
                v.kind() == BuildCard.VerdictKind.BILL && v.figure() == v.quote().total - g.getCash()
                && g.buildStack(house, 3, false) == Game.BuildResult.NEEDS_FUNDING);
        g.setCashForTest(cash);

        v = BuildCard.verdict(g, house, 3);
        assertTrue("with all of it: MONTHS, the quote's own wait at today's queue",
                v.kind() == BuildCard.VerdictKind.MONTHS
                && Double.doubleToLongBits(v.figure()) == Double.doubleToLongBits(g.quoteBuild(house, 3).months));
        // The order of the checks: a mine with no deposit and no land is a deposit refusal first.
        land.restoreIron(0, reserve);
        land.setOwnedSqFt(land.getAllocatedSqFt());
        assertTrue("with neither ore nor land, the mine's verdict is the deposit, as buildStack() checks ore first",
                BuildCard.verdict(g, mine, 1).kind() == BuildCard.VerdictKind.NO_DEPOSIT
                && g.buildStack(mine, 1, false) == Game.BuildResult.NO_DEPOSIT);
        land.setOwnedSqFt(owned);
        land.restoreIron(deposits, reserve);
    }

    /* ============================ 8. THE WORD'S KIND ============================ */

    /** The examples: a word shaped as the model files it, and the kind it must read as. */
    static final Object[][] KIND_EXAMPLES = {
            { "Built 2 Industrial Bakery - output short of demand", BuildCard.WordKind.BUILDING },
            { "Built 1 House on an insured mortgage - 73 households need a door a child is allowed in", BuildCard.WordKind.BUILDING },
            { "Sold 3 Fabrication Shop - six months of losses (plot back to the city for $1,200)", BuildCard.WordKind.SELLING },
            { "Sold 1 Diner to the city by compulsory purchase, for $40,000", BuildCard.WordKind.SELLING },
            { "Holding: no land - needs 0.0446 km², 0.0301 km² free", BuildCard.WordKind.LAND },
            { "Holding: the city is short of homes - ground is worth more to live on", BuildCard.WordKind.LAND },
            { "Could not build Machine Works - needs 0.0557 km², 0.0301 km² free", BuildCard.WordKind.LAND },
            { "Holding: the city could staff 78% of a Machine Works; it wants 80%", BuildCard.WordKind.STAFF },
            { "Holding: no one could staff an Engineering Office's engineering posts", BuildCard.WordKind.STAFF },
            { "Holding: the city could staff 38% of a Commercial Bank; it wants 80%", BuildCard.WordKind.STAFF },
            { "Holding: 2.0 months of work queued, but the city could staff 40% of a Construction Depot; it wants 80%", BuildCard.WordKind.STAFF },
            { "Holding: 39 more engineering licences before a practice could open", BuildCard.WordKind.LICENCE },
            { "Holding: 1 more medical licence before a practice could open", BuildCard.WordKind.LICENCE },
            { "Holding: no deposit to dig", BuildCard.WordKind.ORE },
            { "Holding: no spare local ore to smelt", BuildCard.WordKind.ORE },
            { "Holding: the ore is worked out", BuildCard.WordKind.ORE },
            { "Holding: the coal is worked out", BuildCard.WordKind.ORE },
            { "Holding: no coking coal left in the city's fields for an Underground Coal Mine", BuildCard.WordKind.ORE },
            { "Holding: no hematite field with 20 free sites for a Large Open-Pit Mine", BuildCard.WordKind.ORE },
            // ...and the wells' (0.7.62).
            { "Holding: no oil deposit to drill", BuildCard.WordKind.ORE },
            { "Holding: every oil deposit is worked out", BuildCard.WordKind.ORE },
            // ...and Forestry's (0.7.138): no forest it may cut, or too little for E's 120 months.
            { "Holding: no forest it may cut", BuildCard.WordKind.ORE },
            { "Holding: the forest it may cut, 137k m³, would last 69 months of the crews' cut with a Logging Camp; it asks 120",
                    BuildCard.WordKind.ORE },
            { "Holding: nothing here fabricates steel, and the world will not sell a beam at any price", BuildCard.WordKind.SUPPLY },
            { "Holding: the city fabricates 900 tonnes a month; the smallest plant would want more than the 300 one plant may take", BuildCard.WordKind.SUPPLY },
            { "Holding: borrowing ban, 12 more months - Steel Foundry would need credit", BuildCard.WordKind.CREDIT },
            { "Holding: the bank is rebuilding its capital and lets a borrower's debt grow 0.50% this month - House would need more credit than that", BuildCard.WordKind.CREDIT },
            { "Holding: it owes 1.62 times what it owns, over its last quarter, past the default point of 1.50 times - the bank lends it nothing, Diner included, until it is under", BuildCard.WordKind.CREDIT },
            { "Holding: needs more of its own for the 15% down payment on 4 House", BuildCard.WordKind.CREDIT },
            { "Declined House - its rent would cover the mortgage 0.92×; the lender asks 1.10×", BuildCard.WordKind.CREDIT },
            { "Holding: the lender will not write the mortgage on House (shut)", BuildCard.WordKind.CREDIT },
            { "Declined Steel Foundry - not even one would cover its interest", BuildCard.WordKind.MONEY },
            { "Holding: price below cost", BuildCard.WordKind.MONEY },
            { "Holding: no margin in it", BuildCard.WordKind.MONEY },
            { "Holding: nothing worth sinking", BuildCard.WordKind.MONEY },
            { "Holding: nothing worth building", BuildCard.WordKind.MONEY },
            { "Holding: the parts cost more than the vehicle fetches", BuildCard.WordKind.MONEY },
            { "Holding: the city eats what a Meat Works makes, but at $4.95 a kilo for meat there is nothing left in it after the wages", BuildCard.WordKind.MONEY },
            { "Holding: haulage at 31% of the lorry rate does not pay for track", BuildCard.WordKind.MONEY },
            { "Holding: rent does not cover a new Studio Apartments", BuildCard.WordKind.MONEY },
            { "Holding: a Meat Works would not clear its own costs at the price", BuildCard.WordKind.MONEY },
            { "Holding: fuel at today's price would not clear a refinery's cost", BuildCard.WordKind.MONEY },
            { "Holding: the ground under a Mixed Farm costs $12,000 and a bad year would leave $3,000 a month on it", BuildCard.WordKind.MONEY },
            { "Holding: the wage bill is above what the world pays for the work", BuildCard.WordKind.MONEY },
            { "Holding: another branch's customers would pay $1.2k a month in fees against the $3.4k a branch costs", BuildCard.WordKind.MONEY },
            { "Holding: coverage ahead of demand", BuildCard.WordKind.ENOUGH },
            { "Holding: already building", BuildCard.WordKind.ENOUGH },
            { "Holding: the network already covers all 2,695 tonnes a month the city trades", BuildCard.WordKind.ENOUGH },
            { "Holding: 0.0 months of work queued", BuildCard.WordKind.ENOUGH },
            { "Holding: 4.2 months of work on site already, of the builders' output", BuildCard.WordKind.ENOUGH },
            { "Holding: 0 units/mo is not enough for a first Construction Materials Plant", BuildCard.WordKind.ENOUGH },
            { "Holding: nothing that adds capacity", BuildCard.WordKind.ENOUGH },
            { "Holding: 2,388 customers at 1 branch - a branch serves 16,000, so there are none for another", BuildCard.WordKind.ENOUGH },
            { "Holding: nothing crosses the city boundary to carry", BuildCard.WordKind.ENOUGH },
            { "Holding: 900 tonnes a month go by lorry; the smallest line wants 40,000 to be worth laying", BuildCard.WordKind.ENOUGH },
            // ...and the refinery's two (0.7.62).
            { "Holding: the city's fuel is covered already, and its wells have no crude to spare", BuildCard.WordKind.ENOUGH },
            { "Holding: 1,234,567 L of fuel and 4,321 t of crude to spare: none for another refinery", BuildCard.WordKind.ENOUGH },
            // ...and the spread planner's (0.7.82): a unit or a package under its hurdle or losing money, and nothing to refine.
            { "Holding: a Small Lube Plant would earn $305.2k a month on $29,145k, which would not clear 1.25 times the interest on"
                    + " what it would borrow ($512.3k)", BuildCard.WordKind.MONEY },
            { "Holding: an Oil Refinery with a Small Lube Plant behind it would lose $523.8k a month on it: it does not pay",
                    BuildCard.WordKind.MONEY },
            { "Holding: 2,910 L of petrol and diesel and 0 t of crude to spare: none for another refinery, and no stream spare for"
                    + " a unit", BuildCard.WordKind.ENOUGH },
            { "Holding: the city's petrol and diesel are covered already, its wells have no crude to spare, and no stream is spare"
                    + " for a unit", BuildCard.WordKind.ENOUGH },
            { "", BuildCard.WordKind.NONE },
            { "Holding: nobody to build it", BuildCard.WordKind.OTHER } };

    static void theKinds(Game g) {
        out.println("\n--- 8. the word's kind: none of the town's words is OTHER; every phrase, whole words, in order ---");
        StringBuilder said = new StringBuilder();
        boolean known = true;
        int words = 0;
        for (Sector s : g.getSectors().all()) {
            String w = g.getLastInvestment(s.key());
            BuildCard.WordKind k = BuildCard.wordKind(w);
            out.printf("      %-18s %-9s %s%n", s.label(), k, w);
            if (w.isEmpty()) continue;
            words++;
            known &= k != BuildCard.WordKind.OTHER && k != BuildCard.WordKind.NONE;
        }
        assertTrue("every sector's word in the town has a kind, none OTHER (" + words + " words)", known && words == g.getSectors().size());
        for (BuildCard.WordKind kind : BuildCard.WordKind.values()) {
            int n = 0;
            boolean ok = true;
            for (Object[] e : KIND_EXAMPLES) {
                if (e[1] != kind) continue;
                n++;
                BuildCard.WordKind got = BuildCard.wordKind((String) e[0]);
                if (got != kind) {
                    ok = false;
                    out.println("      read as " + got + ": " + e[0]);
                }
            }
            assertTrue("every example of " + kind + " reads as " + kind + " (" + n + ")", ok && n > 0);
        }
        assertTrue("whole words: \"more\", \"before\" and \"Convenience Store\" are not ore, \"banks\" is not a bank",
                BuildCard.wordKind("Holding: before more Convenience Store") == BuildCard.WordKind.OTHER
                && BuildCard.wordKind("Holding: the riverbanks") == BuildCard.WordKind.OTHER);
        assertTrue("an order on site is BUILDING whatever the word, and the land-blocked list is LAND",
                BuildCard.wordKind("Holding: no margin in it", true, false) == BuildCard.WordKind.BUILDING
                && BuildCard.wordKind("Holding: no margin in it", false, true) == BuildCard.WordKind.LAND
                && BuildCard.wordKind("Sold 1 Diner - losses", false, true) == BuildCard.WordKind.SELLING);

        // One sector's investors: its own buildings, its word, and building while investors are.
        boolean own = true;
        for (Sector s : g.getSectors().all()) {
            BuildCard.SectorInvestors si = BuildCard.sectorInvestors(g, s);
            int mine = 0;
            for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
                // ...its market buildings: not the city's own, the Strategic Reserve among them (0.7.85, BuildCard.citys()).
                if (!BuildCard.citys(t) && BuildCard.ownerOf(g, t) == s) mine++;
            }
            own &= si.word().equals(g.getLastInvestment(s.key())) && si.buildings().size() == mine
                    && si.landBlocked() == g.getLandBlockedSectors().contains(s.key());
            for (BuildCard.Investors i : si.buildings()) own &= i.owner() == s;
        }
        BuildCard.SectorInvestors shops = BuildCard.sectorInvestors(g, g.getSectors().retail());
        boolean branch = false;
        for (BuildCard.Investors i : shops.buildings()) branch |= i.slot().equals("Bank");
        assertTrue("a sector's investors are its own market buildings' lines and its word - Retail's with the bank's branch",
                own && branch);
        BuildingsTemplate store = template(g, "Department Store");
        BuildCard.SectorInvestors lux = BuildCard.sectorInvestors(g, g.getSectors().luxuryRetail());
        assertTrue("fixture (section 5): Luxury Retail's three department stores are still on site", onSite(g.getBuildingManager(), store) >= 3);
        int theirSites = 0;
        double wait = 0;
        for (BuildCard.Investors i : lux.buildings()) {
            if (!i.theirs()) continue;
            theirSites += i.onSite();
            wait = Math.max(wait, i.months());
        }
        assertTrue("...so its investors read BUILDING, with what is on site of what they are building ("
                + theirSites + ", the stores among them) and the longest wait, the city's order among them",
                lux.theirs() && lux.kind() == BuildCard.WordKind.BUILDING && lux.yoursToo()
                && lux.onSite() == theirSites && theirSites >= onSite(g.getBuildingManager(), store)
                && Double.doubleToLongBits(lux.months()) == Double.doubleToLongBits(wait) && wait >= g.onSiteMonths(store)
                && lux.line().theirs() && lux.line().own() == null && !lux.line().showOwn());
        BuildCard.SectorInvestors mines = BuildCard.sectorInvestors(g, g.getSectors().mining());
        assertTrue("...and a sector with nothing on site does not: " + mines.kind() + ", " + mines.word(),
                !mines.theirs() && mines.kind() == BuildCard.wordKind(mines.word(), false, mines.landBlocked()));
    }
    /* ============================ 9. A RUN OF ORDERS ============================ */

    static Map<BuildingsTemplate, Integer> run(Object... pairs) {
        Map<BuildingsTemplate, Integer> r = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) r.put((BuildingsTemplate) pairs[i], (Integer) pairs[i + 1]);
        return r;
    }

    static void theRun(Game g) {
        out.println("\n--- 9. a run of orders: priced and checked as placing them in turn would be, and charged that ---");
        BuildingManager bm = g.getBuildingManager();
        LandManager land = g.getLandManager();
        BuildingsTemplate house = template(g, "House"), road = template(g, "Paved Road"), water = template(g, "Water Treatment Plant");
        BuildingsTemplate mine = g.getBuildingManager().getTemplate(Mining.IRON_MINE), eso = template(g, "Engineering Services Office");
        Map<BuildingsTemplate, Integer> three = run(house, 3, road, 2, water, 1);
        double cash = g.getCash();

        // The yard holds the first order's material and half the second's.
        int yard = (int) Math.round(house.getConstructionMaterials() * 3.0 + road.getConstructionMaterials() * 2.0 / 2);
        bm.setConstructionMaterials(yard);
        assertTrue("fixture: the yard holds all of the first order's material and half the second's (" + yard + " units)",
                bm.getConstructionMaterials() == yard && yard > house.getConstructionMaterials() * 3.0
                && yard < house.getConstructionMaterials() * 3.0 + road.getConstructionMaterials() * 2.0);
        double alone = 0;
        for (Map.Entry<BuildingsTemplate, Integer> e : three.entrySet()) alone += g.quoteBuild(e.getKey(), e.getValue()).total;
        double invoice = g.buildRunInvoice(three);
        assertTrue("...so the run's invoice is more than its orders quoted alone, which each count the same yard free",
                invoice > alone);
        bits("the run's first order is quoted as it is alone: a run of one is its quote", g.buildRunInvoice(run(house, 3)),
                g.quoteBuild(house, 3).total);

        g.setCashForTest(invoice / 2);
        bits("short of cash: the run's gap is its invoice less the cash", g.buildFundingGap(three), invoice - invoice / 2);
        g.setCashForTest(-1_000);
        bits("overdrawn: the gap counts the overdraft in full, as buildFundingGap() for one order does",
                g.buildFundingGap(three), invoice + 1_000);
        assertTrue("...and what the treasury is overdrawn by is the cash below nothing", g.cashShortfall() == 1_000);
        g.setCashForTest(cash);
        bits("with the cash to cover it, the gap is nothing", g.buildFundingGap(three), 0);
        assertTrue("every order of it passes the checks money cannot fix: buildRunAhead() is all three, buildRunStop() SUCCESS",
                g.buildRunAhead(three) == 3 && g.buildRunStop(three) == Game.BuildResult.SUCCESS);

        // Placed in turn, as Build's goAhead() places a run: what each was charged, from its receipt.
        double[] charged = { 0 };
        boolean[] placed = { true };
        quietly(() -> {
            for (Map.Entry<BuildingsTemplate, Integer> e : three.entrySet()) {
                placed[0] &= g.buildStack(e.getKey(), e.getValue(), false) == Game.BuildResult.SUCCESS;
                charged[0] += g.getTotalBuildingCost();
            }
        });
        assertTrue("fixture: the three orders were placed", placed[0]);
        bits("...and what they were charged, added in turn, is the run's invoice to the bit", charged[0], invoice);

        // Ground: the second order short once the first has taken its own, though each fits alone.
        double owned = land.getOwnedSqFt();
        Map<BuildingsTemplate, Integer> ground = run(house, 3, water, 1, road, 1);
        land.setOwnedSqFt(land.getAllocatedSqFt() + water.getLandSqFt() + house.getLandSqFt() * 3 / 2);
        assertTrue("fixture: the free ground holds either of the first two orders alone and not both ("
                + (long) land.getAvailableSqFt() + " sq ft)",
                land.canAllocate(house.getLandSqFt() * 3) && land.canAllocate(water.getLandSqFt())
                && !land.canAllocate(house.getLandSqFt() * 3 + water.getLandSqFt()));
        assertTrue("...so the run goes one order ahead and stops at the second for ground",
                g.buildRunAhead(ground) == 1 && g.buildRunStop(ground) == Game.BuildResult.NO_LAND);
        g.setCashForTest(Founding.WEALTHY_CASH);
        Game.BuildResult[] inTurn = new Game.BuildResult[2];
        quietly(() -> {
            inTurn[0] = g.buildStack(house, 3, false);
            inTurn[1] = g.buildStack(water, 1, false);
        });
        assertTrue("...and placed in turn, buildStack() places the first and says NO_LAND to the second",
                inTurn[0] == Game.BuildResult.SUCCESS && inTurn[1] == Game.BuildResult.NO_LAND);
        land.setOwnedSqFt(owned);

        // A deposit, and licences.
        int deposits = land.getIronDeposits();
        double reserve = land.getIronReserveTonnes();
        land.restoreIron(g.minesCommitted() + 1, Math.max(1, reserve));
        Map<BuildingsTemplate, Integer> ore = run(house, 1, mine, 2);
        assertTrue("a run whose second order is two mines, with one deposit free, goes one order ahead and stops for the deposit",
                g.buildRunAhead(ore) == 1 && g.buildRunStop(ore) == Game.BuildResult.NO_DEPOSIT
                && !g.hasDepositFor(mine, 2) && g.hasDepositFor(mine, 1));
        land.restoreIron(deposits, reserve);
        Map<BuildingsTemplate, Integer> licensed = run(house, 1, eso, 1);
        assertTrue("a run with an office nobody is licensed for stops there for licences, as buildStack() would",
                g.buildRunAhead(licensed) == 1 && g.buildRunStop(licensed) == Game.BuildResult.NO_LICENCE
                && !g.hasLicencesFor(eso, 1));
        assertTrue("a run whose first order is refused goes nowhere", g.buildRunAhead(run(eso, 1, house, 1)) == 0);
        g.setCashForTest(cash);
    }

    /* ---------------------------------------------------------------------
       10. SERVED (0.7.41). Jerus, playing 0.7.39: the roads read "180%" and
       that was bad, general care read "90%" and that was bad, and Build
       ticked the care. One rule since: supply over demand, "served", higher
       is better, a tick only at 100% or more. The lines are NEEDS YOU's; the
       verdict is them read the other way up, so where a row is listed it
       must be the row's colour - checked on the constants at every load and
       crowd from 25% to 300% and every cover from 25% to 100% - and the
       figures are the owners' own.
       --------------------------------------------------------------------- */
    static void theServed(Game g) {
        out.println("\n--- 10. served: every gauge is supply over demand, and one verdict, NEEDS YOU's lines turned over (0.7.41) ---");

        // The lines, read the other way up: a load's level is the verdict on one over it.
        boolean networks = true, roads = true, crowds = true, covers = true, words = true;
        for (int k = 250; k <= 3000; k++) {
            double x = k / 1000.0;
            CityNeeds.Served net = CityNeeds.verdict(CityNeeds.Kind.POWER, CareType.NONE, 1 / x);
            networks &= net.level() == CityNeeds.level(x, CityNeeds.NETWORK_YELLOW, CityNeeds.NETWORK_RED, true);
            CityNeeds.Served road = CityNeeds.verdict(CityNeeds.Kind.ROADS, CareType.NONE, 1 / x);
            roads &= road.level() == CityNeeds.level(x, InfrastructureManager.STRAINED, InfrastructureManager.FREE_FLOW, true);
            CityNeeds.Served seats = CityNeeds.verdict(CityNeeds.Kind.HIGHER_SCHOOL, CareType.NONE, 1 / x);
            int listed = CityNeeds.level(x, CityNeeds.SEATS_YELLOW, CityNeeds.SEATS_RED, true);
            // ...a crowd off the list but over its seats (1 < x < SEATS_YELLOW) is short of 100%: amber, not listed
            crowds &= x > 1 && x < CityNeeds.SEATS_YELLOW ? seats.level() == 1 && listed == 0 : seats.level() == listed;
            if (x <= 1) {
                for (CareType care : new CareType[] {CareType.GENERAL, CareType.CHILDCARE}) {
                    CityNeeds.Served c = CityNeeds.verdict(CityNeeds.Kind.CARE, care, x);
                    int row = care == CareType.GENERAL ? CityNeeds.level(x, CityNeeds.GENERAL_YELLOW, CityNeeds.GENERAL_RED, false)
                            : CityNeeds.level(x, CityNeeds.OTHER_CARE_YELLOW, CityNeeds.OTHER_CARE_RED, false);
                    // ...listed, the row's colour; off the list, green only at 100%, amber short under it
                    covers &= row > 0 ? c.level() == row : c.level() == (x >= 1 ? 0 : 1);
                }
                CityNeeds.Served b = CityNeeds.verdict(CityNeeds.Kind.BASIC_SCHOOLS, CareType.NONE, x);
                int row = CityNeeds.level(x, CityNeeds.SCHOOLS_YELLOW, CityNeeds.SCHOOLS_RED, false);
                covers &= row > 0 ? b.level() == row : b.level() == (x >= 1 ? 0 : 1);
            }
            for (CityNeeds.Served v : new CityNeeds.Served[] {net, road, seats}) {
                words &= v.word().equals(v.level() == 0 ? CityNeeds.ENOUGH : v.share() < 1 ? CityNeeds.SHORT : CityNeeds.TIGHT)
                        && (v.level() != 0 || v.share() >= 1);
            }
        }
        assertTrue("a network's verdict on what it serves is NEEDS YOU's level on its load, at every load from 25% to 300%", networks);
        assertTrue("...the road's, on STRAINED and FREE_FLOW", roads);
        assertTrue("...a school's seats, on who would come and be hired over them - and a crowd off the list but over its seats is amber", crowds);
        assertTrue("...care and the basic ladder, listed, the row's level; off the list, green only at 100% and amber under it", covers);
        assertTrue("...and every word is the verdict's: enough only green and at 100% or more, short under 100%, tight from it", words);

        CityNeeds.Served jerus = CityNeeds.verdict(CityNeeds.Kind.CARE, CareType.GENERAL, .9);
        assertTrue("general care at 90%, past GENERAL_YELLOW and off the list, is amber and short - no tick (Jerus's case)",
                .9 > CityNeeds.GENERAL_YELLOW && jerus.level() == 1 && CityNeeds.SHORT.equals(jerus.word()));
        CityNeeds.Served over = CityNeeds.verdict(CityNeeds.Kind.POWER, CareType.NONE, 1.2);
        assertTrue("a network serving 120%, a fifth in hand, is tight: amber, though over 100%",
                over.level() == 1 && CityNeeds.TIGHT.equals(over.word()));
        CityNeeds.Served full = CityNeeds.verdict(CityNeeds.Kind.ROADS, CareType.NONE, 1 / 1.8);
        assertTrue("the road 180% full serves 56%: red, short",
                CityNeeds.servedPct(full.share()).equals("56%") && full.level() == 2 && CityNeeds.SHORT.equals(full.word()));
        assertTrue("nothing asked is all of it met, and nothing supplying an ask is nothing; not a number has no verdict",
                CityNeeds.servedShare(5, 0) == Double.POSITIVE_INFINITY && CityNeeds.servedShare(0, 5) == 0
                && CityNeeds.verdict(CityNeeds.Kind.WATER, CareType.NONE, Double.POSITIVE_INFINITY).enough()
                && CityNeeds.verdict(CityNeeds.Kind.WATER, CareType.NONE, Double.NaN).level() == -1);
        assertTrue("a share just under 100% never prints as 100% (the word would say short beside it)",
                CityNeeds.servedPct(.9996).equals("99%") && CityNeeds.servedPct(1).equals("100%"));

        // The figures are the owners' own, in the town.
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        InfrastructureManager road = g.getInfrastructureManager();
        List<CityNeeds.Need> all = CityNeeds.measure(g, CityNeeds.PLAIN);
        boolean rows = true;
        int servedRows = 0, listedRows = 0;
        for (CityNeeds.Need n : all) {
            CityNeeds.Served v = n.served();
            if (v == null) continue;
            servedRows++;
            if (n.level() > 0) listedRows++;
            double want = switch (n.kind()) {
                case POWER -> u.getPowerServed();
                case WATER -> u.getWaterServed();
                case ROADS -> road.getServed();
                case CARE -> CityNeeds.careServed(g, n.care(), g.getCohorts(), g.getPopulationManager().getJobFillRate());
                case BASIC_SCHOOLS -> g.getEducation().basicCoverage();
                default -> CityNeeds.servedShare(n.a(), n.b());
            };
            rows &= Double.doubleToLongBits(v.share()) == Double.doubleToLongBits(want);
            // listed with nothing on the way: the row's colour; never red off the list; on the way only ever lowers the level
            rows &= n.onSite() == 0 && n.level() > 0 ? v.level() == n.level()
                    : n.level() == 0 ? v.level() < 2 : v.level() >= n.level();
            rows &= n.reading().startsWith(n.kind() == CityNeeds.Kind.BASIC_SCHOOLS ? n.school().getLabel().toLowerCase() + " "
                    + CityNeeds.servedPct(v.share()) + " served" : n.kind() == CityNeeds.Kind.HIGHER_SCHOOL ? ""
                    : CityNeeds.servedPct(v.share()) + " served") || n.reading().startsWith("nothing supplying");
        }
        assertTrue("every served NEEDS YOU row (" + servedRows + " in the town, " + listedRows + " listed) reads its owner's "
                + "figure, to the bit, as served, in the colour of its level where it is listed", rows && servedRows >= 6);

        boolean measures = true;
        int counted = 0;
        Set<BuildAdvice.Kind> notServed = new HashSet<>();
        for (String category : new String[] {BuildAdvice.UTILITIES, BuildAdvice.ROADS, BuildAdvice.HEALTHCARE,
                BuildAdvice.EDUCATION, BuildAdvice.SAFETY}) {
            for (BuildAdvice.Measure m : BuildAdvice.measuresOf(category)) {
                double served = BuildAdvice.served(g, m, Map.of());
                if (!BuildAdvice.isServed(m)) {
                    notServed.add(m.kind());
                    measures &= Double.isNaN(served) && BuildAdvice.verdict(g, m, Map.of()) == null;
                    continue;
                }
                double[] sd = BuildAdvice.supplyDemand(g, m, Map.of());
                measures &= Double.doubleToLongBits(served) == Double.doubleToLongBits(CityNeeds.servedShare(sd[0], sd[1]))
                        && (!(sd[1] > 0) || BuildAdvice.cover(g, m, Map.of()) == Math.max(0, Math.min(1, served)));
                counted++;
            }
        }
        measures &= notServed.equals(Set.of(BuildAdvice.Kind.DEATH, BuildAdvice.Kind.POLICE, BuildAdvice.Kind.CELLS));
        assertTrue("every Build ring of power, water, the road, transit, care and the schools (" + counted + ") reads its "
                + "supply over its demand, the arc that held to 0-1; the police, the cells and the dead are not served", measures);
        BuildAdvice.Measure power = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);
        BuildAdvice.Measure roadM = BuildAdvice.Measure.of(BuildAdvice.Kind.ROADS);
        assertTrue("...power's ring is the grid's own figure (to 1e-9) and the road's the road's, to the bit; transit has no verdict",
                Math.abs(BuildAdvice.served(g, power, Map.of()) / u.getPowerServed() - 1) < 1e-9
                && Double.doubleToLongBits(BuildAdvice.served(g, roadM, Map.of())) == Double.doubleToLongBits(road.getServed())
                && BuildAdvice.verdict(g, BuildAdvice.Measure.of(BuildAdvice.Kind.TRANSIT), Map.of()).level() == -1
                && Double.doubleToLongBits(BuildAdvice.served(g, BuildAdvice.Measure.of(BuildAdvice.Kind.TRANSIT), Map.of()))
                        == Double.doubleToLongBits(road.getTransitServed()));
        assertTrue("the curve the Roads page draws against served is the road's flow at what it serves",
                Math.abs(InfrastructureManager.throughputAtServed(road.getServed()) - road.getThroughputRatio()) < 1e-12);

        // Build's tile: a served row off the list that is not enough is shown before the tick.
        CityNeeds.Need short90 = new CityNeeds.Need("GENERAL CARE", "90% served", 0, .89, CityNeeds.Kind.CARE,
                CityNeeds.Go.HEALTHCARE, CareType.GENERAL, EducationType.NONE, .9, CityNeeds.GENERAL_YELLOW,
                CityNeeds.GENERAL_RED, false, 0, Double.NaN, 900, 1000);
        CityNeeds.Need enough = new CityNeeds.Need("CHILDCARE", "100% served", 0, .7, CityNeeds.Kind.CARE,
                CityNeeds.Go.HEALTHCARE, CareType.CHILDCARE, EducationType.NONE, 1, CityNeeds.OTHER_CARE_YELLOW,
                CityNeeds.OTHER_CARE_RED, false, 0, Double.NaN, 1000, 1000);
        assertTrue("fixture: with general care at 90% off the list, NEEDS YOU lists nothing for Healthcare, and the tile shows "
                        + "the care, amber - with every care at 100%, nothing, and the tick",
                CityNeeds.worst(List.of(short90, enough), CityNeeds.Go.HEALTHCARE) == null
                && CityNeeds.worstUnlisted(List.of(short90, enough), CityNeeds.Go.HEALTHCARE) == short90
                && short90.verdictLevel() == 1 && enough.verdictLevel() == 0
                && CityNeeds.worstUnlisted(List.of(enough), CityNeeds.Go.HEALTHCARE) == null);
    }

    /* ============================ 11. THE INTEREST ON ITS WHOLE COST ============================ */

    /**
     * consider()'s brake on the card (0.7.122, batch C1). Since 0.7.113 every
     * order is asked to earn PROFIT_OVER_INTEREST times a month's interest on
     * its whole cost, whoever pays, at the rate it would be judged at; the
     * card's gates ended at the loss, so a card could pass every one while
     * the investors declined it. The card asks it through Game.interestTest(),
     * and consider()'s own answer for an order of one is Game.sliceFor() -
     * its search, with the till the card's test counts (the sector's cash and
     * what it holds abroad) and the investors' estimate.
     */
    static void theInterest(Game g) {
        out.println("\n--- 11. the interest on its whole cost: consider()'s brake, the card's last gate ---");
        BusinessInvestment plans = g.getBusinessInvestment();
        BusinessDebtManager credit = g.getEconomyManager().getBusinessDebtManager();

        // The town as it stands: every market building's card against consider()'s answer to an order of one.
        int asked = 0, agree = 0, homes = 0, homesNone = 0, short_ = 0;
        List<String> shortOnes = new ArrayList<>();
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            if (BuildCard.citys(t)) continue;
            BuildCard.Appraisal a = BuildCard.appraise(g, t);
            String owner = a.owner().key();
            if (g.buysOnMortgage(owner, t)) {
                homes++;
                if (a.at(BuildCard.GateKind.INTEREST) == BuildCard.Mark.NONE) homesNone++;
                continue;
            }
            asked++;
            boolean card = a.at(BuildCard.GateKind.INTEREST) == BuildCard.Mark.PASS;
            boolean built = g.sliceFor(new BusinessInvestment.Decision(owner, t, 1, "the check's", true), tillOf(g, owner),
                    a.estimate()).quantity() == 1;
            if (card == built) agree++;
            if (a.estimate() > 0 && !card) {
                short_++;
                shortOnes.add(t.getName());
            }
        }
        out.printf("      %d market buildings asked; %d earn over nothing and under their interest: %s%n", asked, short_,
                shortOnes.isEmpty() ? "none" : String.join(", ", shortOnes));
        assertTrue(String.format("every market building's interest gate is consider()'s answer to an order of one (%d of %d)",
                agree, asked), asked > 0 && agree == asked);
        assertTrue(String.format("...and a landlord's home has no interest gate: the mortgage lender's test decides it (%d of %d)",
                homesNone, homes), homes > 0 && homesNone == homes);

        // Caused: a Bakery the bakers' till pays for, at a prime raised so it earns over nothing but under its interest.
        String bakers = Sectors.INDUSTRY;
        BuildingsTemplate bakery = template(g, "Bakery");
        double cost = plans.getCostOf(bakery, 1);
        double cash = g.getEconomyManager().getSectorCash(bakers), prime = credit.getPrimeRate();
        g.getEconomyManager().setSectorCash(bakers, 2 * cost);
        BusinessInvestment.Decision one = new BusinessInvestment.Decision(bakers, bakery, 1, "the check's", true);
        try {
            double earns = plans.estimatedMonthlyProfit(bakers, bakery);
            BuildCard.Investors before = BuildCard.investors(g, bakery);
            assertTrue(String.format("fixture: at today's prime a Bakery from the bakers' till earns %s a month and passes"
                    + " every gate", Formats.INSTANCE.amount(earns)), earns > 0 && before.own() == null
                    && BuildCard.appraise(g, bakery).at(BuildCard.GateKind.INTEREST) == BuildCard.Mark.PASS);
            assertTrue("...and consider() would build it", g.sliceFor(one, tillOf(g, bakers), earns).quantity() == 1);

            // The rate its earnings would just carry, a year, on its whole cost: prime raised by twice that and by the
            // inflation people expect, so the real rate the test reads is over it whatever the sector's spread.
            double carried = 12 * earns / (BusinessInvestment.PROFIT_OVER_INTEREST * cost);
            credit.setPrimeRate(prime + 2 * carried + plans.getExpectedInflation());
            Game.InterestTest test = g.interestTest(bakers, earns, cost);
            BuildCard.Investors inv = BuildCard.investors(g, bakery);
            BuildCard.Gate gate = inv.own();
            out.printf("      a Bakery: %s from the till; prime %.4f%% -> %.4f%%; earns %s a month, its interest %s a month"
                    + " at %.4f%% (real %.4f%%), the bar %s%n", Formats.INSTANCE.amount(cost), prime * 100,
                    credit.getPrimeRate() * 100, Formats.INSTANCE.amount(earns), Formats.INSTANCE.amount(test.interest()),
                    test.rate() * 100, test.real() * 100, Formats.INSTANCE.amount(test.needs()));
            assertTrue("fixture: it still earns over nothing, the same estimate, and its interest bar is over that",
                    plans.estimatedMonthlyProfit(bakers, bakery) == earns && earns > 0 && test.needs() > earns
                    && !test.banned() && !test.carries());
            assertTrue("A BAKERY THAT PAYS BUT NOT ITS INTEREST ON ITS WHOLE COST FAILS THE CARD'S INTEREST GATE, the last",
                    gate != null && gate.kind() == BuildCard.GateKind.INTEREST);
            assertTrue("...with what one would earn and what it must: PROFIT_OVER_INTEREST times a month's interest",
                    gate != null && gate.a() == earns && gate.b() == test.needs()
                    && gate.b() == test.interest() * BusinessInvestment.PROFIT_OVER_INTEREST && gate.why() == null);
            bits("...its interest struck on its whole cost though the till pays it all, at the real rate the test reads",
                    gate == null || gate.test() == null ? Double.NaN : gate.test().interest(),
                    cost * plans.realTestRate(credit.projectRate(bakers, 0)) / 12);
            bits("...the rate it is judged at the bakers' own, the till paying (projectRate() of nothing)",
                    gate == null || gate.test() == null ? Double.NaN : gate.test().rate(), credit.projectRate(bakers, 0));
            BuildCard.Appraisal a = BuildCard.appraise(g, bakery);
            assertTrue("...and every gate's report the same: it pays, it fails the interest, and that is its first failure",
                    a.at(BuildCard.GateKind.LOSS) == BuildCard.Mark.PASS && a.at(BuildCard.GateKind.INTEREST) == BuildCard.Mark.FAIL
                    && a.failures().size() == 1 && a.first() != null && a.first().kind() == BuildCard.GateKind.INTEREST
                    && a.first().a() == gate.a() && a.first().b() == gate.b());
            Game.Afford refused = g.sliceFor(one, tillOf(g, bakers), earns);
            assertTrue("THE CARD AGREES WITH consider(): the same order of one is refused", refused.quantity() == 0);

            // ...and at today's prime again: the card passes it, and consider() builds it.
            credit.setPrimeRate(prime);
            assertTrue("at today's prime again the card passes it and consider() builds the same order",
                    BuildCard.investors(g, bakery).own() == null && g.sliceFor(one, tillOf(g, bakers), earns).quantity() == 1
                    && BuildCard.appraise(g, bakery).at(BuildCard.GateKind.INTEREST) == BuildCard.Mark.PASS);

            // A borrowing ban: the till cut below its cost, the bakers banned - consider() refuses it, and the card says why.
            g.getEconomyManager().setSectorCash(bakers, cost / 2);
            Map<String, Integer> record = credit.getBlockedMonthsAll();
            Map<String, Integer> banned = new LinkedHashMap<>(record);
            banned.put(bakers, 12);   // a default's ban (BusinessDebtManager.BORROWING_BLOCKED_MONTHS, private)
            credit.restoreCreditRecord(credit.getRestructureCounts(), banned);
            try {
                BuildCard.Gate ban = BuildCard.investors(g, bakery).own();
                assertTrue("fixture: the bakers are under a borrowing ban, and their till holds half a Bakery",
                        credit.isBorrowingBlocked(bakers) && tillOf(g, bakers) < cost);
                assertTrue("under a ban the gate is the interest's, in consider()'s words: \"" + (ban == null ? null : ban.why())
                        + "\"", ban != null && ban.kind() == BuildCard.GateKind.INTEREST && ban.test() != null
                        && ban.test().banned() && ban.why() != null && ban.why().startsWith("borrowing ban, "
                                + credit.getBlockedMonths(bakers) + " more months")
                        && BuildCard.wordKind(ban.why()) == BuildCard.WordKind.CREDIT);
                assertTrue("...and consider() refuses the same order", g.sliceFor(one, tillOf(g, bakers), earns).quantity() == 0);
            } finally {
                credit.restoreCreditRecord(credit.getRestructureCounts(), record);
            }
        } finally {
            credit.setPrimeRate(prime);
            g.getEconomyManager().setSectorCash(bakers, cash);
        }
        assertTrue("...and the bakers' till, prime and record put back", g.getEconomyManager().getSectorCash(bakers) == cash
                && credit.getPrimeRate() == prime && !credit.isBorrowingBlocked(bakers));

        // A loss fails it too, behind the loss: another spur where the rail carries every tonne already.
        BuildCard.Appraisal spur = BuildCard.appraise(g, template(g, "Rail Spur"));
        assertTrue("a spur that would lose money fails the interest too, behind the loss, which stays its first",
                spur.estimate() <= 0 && spur.at(BuildCard.GateKind.LOSS) == BuildCard.Mark.FAIL
                && spur.at(BuildCard.GateKind.INTEREST) == BuildCard.Mark.FAIL
                && spur.first() != null && spur.first().kind() == BuildCard.GateKind.LOSS);
    }

    /** What the card's test counts as the sector's till: its cash and what it holds abroad (Game.interestTest()). */
    static double tillOf(Game g, String sector) {
        return g.getEconomyManager().getSectorCash(sector) + Math.max(0, g.getEconomyManager().getForeignAssets(sector));
    }

    /* ============================ 12. THE MATERIALS' CARDS ============================ */

    /**
     * The materials buildings' cards (0.7.142, batch I13; spec-materials 2.13):
     * each line a fixture causes, each figure against the model's own. Every
     * materials building is weighed by the rule its sector orders by - a
     * works by its sector's own appraisal, a mine, a crew or a washing plant
     * as its plan weighs one; a typed field puts a 20-site works off it in
     * the plan's words, each resource named; a quarry on limestone makes
     * limestone; a works on imported feed fails the money gate, and the
     * card's "this one:" is the planner's; a self-powered works and a yard
     * on the city's old metal read their template and their owner; each good
     * goes to the sectors here whose buildings use it, or ships; E on a
     * forest town is Forestry's plan's own words; and the land office's
     * hover by type adds up to its offer's fields.
     */
    static void theMaterials(Game g, Path root) {
        out.println("\n--- 12. the materials' cards: where it stands, how its sector weighs it, its power, old metal, buyers, E ---");
        BuildingManager bm = g.getBuildingManager();
        LandManager land = g.getLandManager();
        BusinessInvestment plans = g.getBusinessInvestment();
        Sectors sectors = g.getSectors();

        // a. Every materials building weighed by its sector's own rule, to the bit.
        int works = 0, mines = 0, crews = 0, stands = 0, washers = 0, others = 0;
        boolean worksSame = true, minesSame = true, onlyThem = true, crewsSame = true;
        List<String> off = new ArrayList<>();
        for (BuildingsTemplate t : bm.getTemplates()) {
            Sector owner = sectors.ownerOf(t);
            BuildCard.Weighed w = BuildCard.weighed(g, t);
            boolean m = owner instanceof ProcessingSector || owner instanceof ExtractionSector;
            if (BuildCard.materials(g, t) != m) onlyThem = false;
            if (!m) {
                if (w != null || !BuildCard.outlets(g, t).isEmpty()) onlyThem = false;
                others++;
                continue;
            }
            if (owner instanceof ProcessingSector p) {
                works++;
                SpreadPlanner.Candidate c = p.appraise(t, plans);
                boolean same = w != null && Double.doubleToLongBits(w.earns()) == Double.doubleToLongBits(c.earns())
                        && Double.doubleToLongBits(w.cost()) == Double.doubleToLongBits(c.cost()) && w.failed() == c.failed()
                        && java.util.Objects.equals(w.why(), c.why())
                        && Double.doubleToLongBits(w.hurdle()) == Double.doubleToLongBits(p.planner().hurdle(p, plans, g, c.cost()))
                        && Math.abs(c.cost() - plans.getCostOf(t, 1) - w.fleet()) <= 1e-9 * c.cost()
                        && w.vans() == ProcessingSector.vansFor(t);
                if (!same) { worksSame = false; off.add(t.getName()); }
                continue;
            }
            ExtractionSector e = (ExtractionSector) owner;
            if (owner instanceof Forestry) {
                if (Forestry.isCrew(t)) crews++; else stands++;
                if (Forestry.isCrew(t) == (w == null)) crewsSame = false;
                if (w != null && w.failed() == SpreadPlanner.Gate.FEED
                        == Forestry.roomFor(land.cuttableForest(), g.getSectors().forestry().crewsCut(true), t)) crewsSame = false;
                continue;
            }
            double cost = plans.getCostOf(t, 1) + e.fleetCapital(t);
            boolean same = w != null && Double.doubleToLongBits(w.earns()) == Double.doubleToLongBits(e.estimatedMonthlyProfit(t, plans))
                    && Double.doubleToLongBits(w.cost()) == Double.doubleToLongBits(cost)
                    && Double.doubleToLongBits(w.hurdle()) == Double.doubleToLongBits(e.planner().hurdle(e, plans, g, cost));
            if (t.getWash() > 0) {
                washers++;
                same &= (w.failed() == SpreadPlanner.Gate.FEED) == !(g.getSectors().coalMining().unwashed() > 0)
                        && (w.failed() != SpreadPlanner.Gate.FEED || BuildCard.NOTHING_TO_WASH.equals(w.why()));
            } else {
                mines++;
                String wanting = e.fieldWanting(t);
                same &= (wanting == null) == g.hasDepositFor(t, 1)
                        && (wanting == null ? w.failed() != SpreadPlanner.Gate.FEED : w.failed() == SpreadPlanner.Gate.FEED && wanting.equals(w.why()));
            }
            if (!same) { minesSame = false; off.add(t.getName()); }
        }
        report("fixture: the catalogue's materials buildings - the nine industries' - and the rest", works > 0 && mines > 0
                && crews == 2 && stands == 2 && washers == 1, String.format("%d works, %d mines and quarries, %d crews, %d stands,"
                + " %d washing plant; %d other buildings", works, mines, crews, stands, washers, others));
        assertTrue("only a materials building is one (its owner a ProcessingSector or an ExtractionSector), and only it is weighed",
                onlyThem);
        assertTrue("a works is weighed as its sector's appraisal weighs it: what it earns, its whole cost with its lorries, the"
                + " planner's bar, its first gate and its words, to the bit", worksSame);
        assertTrue("...a mine, a quarry and the washing plant as their plan weighs one - ExtractionSector's estimate on its cost"
                + " and lorries, its field (or the raw coal it washes) the feed", minesSame);
        assertTrue("...a crew under E, and a planted stand not at all (Forestry plants it, nothing orders it)", crewsSame);
        if (!off.isEmpty()) out.println("      off: " + off);

        typedField(g);
        moneyGate(g);
        ownPowerAndOldMetal(g);
        whoBuys(g);
        crewRule(root);
        hoverByType(g);
        paybackByType(g);
    }

    /** b. A typed field: three hematite sites by fiat - the one-site mine fits, the 20-site works not, each resource named. */
    static void typedField(Game g) {
        LandManager land = g.getLandManager();
        BuildingManager bm = g.getBuildingManager();
        Mining mining = (Mining) g.getSectors().byKey(Sectors.MINING);
        int deposits = land.getIronDeposits();
        double reserve = land.getIronReserveTonnes();
        // Ground listed by fiat is the resource's first type (LandManager.restoreSites()).
        land.restoreSites(Resource.IRON, 3, 3 * Resource.IRON.amountPerSite());
        BuildingsTemplate mine = bm.getTemplate(Mining.IRON_MINE), pit = bm.getTemplate(Mining.LARGE_OPEN_PIT),
                magnetite = bm.getTemplate(Mining.MAGNETITE_MINE);
        BuildCard.Stands one = BuildCard.stands(g, mine), big = BuildCard.stands(g, pit), mag = BuildCard.stands(g, magnetite);
        report("fixture: the town holds three iron sites by fiat, all hematite (the first type)",
                land.getSites(Resource.IRON, Deposit.FieldType.HEMATITE) == 3 && land.getSites(Resource.IRON, Deposit.FieldType.MAGNETITE) == 0,
                String.format("%d hematite, %d magnetite", land.getSites(Resource.IRON, Deposit.FieldType.HEMATITE),
                        land.getSites(Resource.IRON, Deposit.FieldType.MAGNETITE)));
        assertTrue("an Iron Mine's card: one hematite site, the city's three by type, its own and those spoken for, what is left -"
                + " each Game's own (sitesFor(), committedFor(), remainingFor()) - and it fits",
                one != null && one.resource() == Resource.IRON && one.type() == Deposit.FieldType.HEMATITE && one.sites() == 1
                && one.byType()[0] == 3 && one.byType()[1] == 0 && one.owned() == g.sitesFor(mine) && one.committed() == g.committedFor(mine)
                && one.left() == g.remainingFor(mine) && one.fits() && g.hasDepositFor(mine, 1) && one.wanting() == null);
        assertTrue("a Large Open-Pit Mine's: twenty sites of one hematite field, which three do not hold - it does not fit"
                + " (Game.hasDepositFor()), in the plan's words: " + (big == null ? null : big.wanting()),
                big != null && big.sites() == 20 && !big.fits() && !g.hasDepositFor(pit, 1)
                && big.wanting() != null && big.wanting().equals(mining.fieldWanting(pit))
                && big.wanting().contains("hematite field with 20 free sites"));
        assertTrue("...a magnetite works on a city of hematite: none of its type, in its words: " + (mag == null ? null : mag.wanting()),
                mag != null && !mag.fits() && mag.byType()[1] == 0 && mag.wanting() != null
                && mag.wanting().equals(mining.fieldWanting(magnetite)) && mag.wanting().contains("magnetite ore"));
        BuildCard.Weighed w = BuildCard.weighed(g, pit);
        assertTrue("...and how its sector weighs it: the field its feed, refused first in the same words",
                w != null && w.failed() == SpreadPlanner.Gate.FEED && w.why().equals(big.wanting()));
        land.restoreIron(deposits, reserve);

        // Each resource named where it read "ore" (the docs pass's flag, CONFIRM 0.7.136-0.7.137 item 8).
        CoalMining coal = g.getSectors().coalMining();
        BuildingsTemplate deep = bm.getTemplate(CoalMining.UNDERGROUND_MINE);
        String coking = coal.fieldWanting(deep);
        assertTrue("the words name each resource by its type: hematite ore, coking coal, sulphide copper ore, limestone - and"
                + " their fields - where coal, stone and copper read \"ore\"",
                "hematite ore".equals(ExtractionSector.oreWords(Resource.IRON, Deposit.FieldType.HEMATITE))
                && "coking coal".equals(ExtractionSector.oreWords(Resource.COAL, Deposit.FieldType.COKING))
                && "sulphide copper ore".equals(ExtractionSector.oreWords(Resource.COPPER, Deposit.FieldType.SULPHIDE))
                && "limestone".equals(ExtractionSector.oreWords(Resource.STONE, Deposit.FieldType.LIMESTONE))
                && "thermal coal field".equals(ExtractionSector.fieldWords(Resource.COAL, Deposit.FieldType.THERMAL))
                && "hard rock field".equals(ExtractionSector.fieldWords(Resource.STONE, Deposit.FieldType.HARD_ROCK))
                && "coal".equals(ExtractionSector.oreWords(Resource.COAL, null)));
        assertTrue("an Underground Coal Mine on a town with no coking coal says so in coal's words, read as the ground (ORE): "
                + coking, coking != null && coking.contains("coking coal") && !coking.contains(" ore ")
                && BuildCard.wordKind(coking) == BuildCard.WordKind.ORE
                && BuildCard.wordKind(big.wanting()) == BuildCard.WordKind.ORE);

        // A quarry on limestone makes limestone: two stone sites by fiat, limestone (the first type).
        Quarrying q = g.getSectors().quarrying();
        int stoneSites = land.getSites(Resource.STONE);
        double stoneLeft = land.getOwnedAmount(Resource.STONE);
        land.restoreSites(Resource.STONE, 2, 2 * Resource.STONE.amountPerSite());
        BuildingsTemplate quarry = bm.getTemplate(Quarrying.QUARRY);
        BuildCard.Stands at = BuildCard.stands(g, quarry);
        double row = quarry.makes(Good.AGGREGATE);
        report("fixture: two stone sites by fiat, limestone; a Quarry's row makes aggregate",
                land.getSites(Resource.STONE, Deposit.FieldType.LIMESTONE) == 2 && row > 0, Formats.INSTANCE.count(row) + " t");
        assertTrue("a Quarry's card: either type, and one more stands on limestone - it makes the row's tonnes of limestone,"
                + " not aggregate (Quarrying.madeBy())", at != null && at.anyType() && at.fits()
                && at.makes().equals(q.madeBy(quarry)) && at.makes().getOrDefault(Good.LIMESTONE, 0.0) == row
                && !at.makes().containsKey(Good.AGGREGATE));
        boolean limeOut = false;
        for (BuildCard.Outlet o : BuildCard.outlets(g, quarry)) limeOut |= o.good() == Good.LIMESTONE;
        assertTrue("...and where it goes is limestone's buyers", limeOut);
        land.restoreSites(Resource.STONE, stoneSites, stoneLeft);
    }

    /** c. A works on imported feed fails the money gate: the card's "this one:" is the planner's, in its words. */
    static void moneyGate(Game g) {
        BusinessInvestment plans = g.getBusinessInvestment();
        BusinessDebtManager credit = g.getEconomyManager().getBusinessDebtManager();
        HeavyIndustry steel = g.getSectors().heavyIndustry();
        BuildingsTemplate pellets = g.getBuildingManager().getTemplate(HeavyIndustry.PELLETISING_PLANT);
        double prime = credit.getPrimeRate();
        BuildCard.Weighed w = BuildCard.weighed(g, pellets);
        // Caused: if it earns over the bar at today's prime, prime raised until it does not.
        if (w.failed() == null && w.earns() > 0) {
            credit.setPrimeRate(prime + 2 * (12 * w.earns() / w.cost()) + plans.getExpectedInflation());
            w = BuildCard.weighed(g, pellets);
        }
        report("fixture: a Pelletising Plant in a town that digs no ore - its feed imported - fails only the money gate",
                g.getSectors().byKey(Sectors.MINING).getCapacity(Good.IRON) == 0 && w.failed() == SpreadPlanner.Gate.MONEY,
                String.format("earns %s on %s, the bar %s; %s", Formats.INSTANCE.amount(w.earns()), Formats.INSTANCE.amount(w.cost()),
                        Formats.INSTANCE.amount(w.hurdle()), w.why()));
        SpreadPlanner.Candidate c = steel.appraise(pellets, plans);
        assertTrue("its refusal is the planner's money gate in its own words (SpreadPlanner.City.money()), on its whole cost"
                + " with its lorries", w.why().equals(c.refusal(SpreadPlanner.Gate.MONEY))
                && w.why().equals(steel.planner().city(steel, plans, g).money(w.earns(), w.cost())) && w.fleet() > 0
                && w.cost() > plans.getCostOf(pellets, 1));
        BuildCard.Investors inv = BuildCard.investors(g, pellets);
        assertTrue("the card's \"this one:\" is it - the planner asks before consider() - unless the sector's word already"
                + " names it or investors are building one: " + inv.word(), inv.planned() != null
                && inv.showPlanned() == (!inv.theirs() && !inv.word().contains(pellets.getName()))
                && BuildCard.appraise(g, pellets).planned().why().equals(w.why()));
        credit.setPrimeRate(prime);
    }

    /** d. A self-powered works and a yard on the city's old metal: the template's and the owner's own figures. */
    static void ownPowerAndOldMetal(Game g) {
        BuildingManager bm = g.getBuildingManager();
        HeavyIndustry steel = g.getSectors().heavyIndustry();
        BuildingsTemplate furnace = bm.getTemplate(HeavyIndustry.BLAST_FURNACE), shop = bm.getTemplate(HeavyIndustry.OXYGEN_STEEL_SHOP),
                pulp = bm.getTemplate(WoodProducts.PULP_MILL), yard = bm.getTemplate(HeavyIndustry.SCRAP_YARD);
        BuildCard.Power bf = BuildCard.power(g, furnace), os = BuildCard.power(g, shop), pm = BuildCard.power(g, pulp);
        assertTrue(String.format("a Blast Furnace makes %,.0f kW of its own, draws %,d and sells the rest at the tariff: the"
                + " template's (powerSold()) times Iron & Steel's tariff", furnace.getGen(), furnace.getElectricityConsumption()),
                bf != null && bf.gen() == furnace.getGen() && bf.draw() == furnace.getElectricityConsumption()
                && bf.sold() == furnace.powerSold() && bf.sold() > 0 && bf.bought() == 0
                && Double.doubleToLongBits(bf.soldValue()) == Double.doubleToLongBits(furnace.powerSold() * steel.tariff()));
        assertTrue("...an Oxygen Steel Shop makes less than it draws, and buys the rest; a Pulp Mill draws none and sells it all;"
                + " a Scrap Yard makes none", os != null && os.sold() == 0 && os.bought() == shop.powerBought() && os.bought() > 0
                && pm != null && pm.draw() == 0 && pm.sold() == pulp.getGen() && BuildCard.power(g, yard) == null);

        OldMetal.Pool pool = g.getOldMetal().steel();
        quietly(() -> g.buildStack(yard, 1, true));
        BuildCard.OldMetalBought o = BuildCard.oldMetal(g, yard);
        report("fixture: a Scrap Yard standing in the town", bm.getQuantity(yard.getId()) >= 1,
                Formats.INSTANCE.count(pool.lastArisen()) + " t of old steel arose last month");
        assertTrue("its card buys the city's old steel: its intake (oldMetal()), Iron & Steel's price - PAY_SHARE of scrap's"
                + " export price - what arose last month, what the yards standing take and the rest, each the model's",
                o != null && o.metal() == Good.STEEL && o.intake() == yard.oldMetal(Good.STEEL)
                && Double.doubleToLongBits(o.price()) == Double.doubleToLongBits(steel.oldSteelPrice())
                && Math.abs(o.price() - OldMetal.PAY_SHARE * g.getMarkets().get(Good.SCRAP).exportPrice()) <= 1e-12 * o.price()
                && o.arisen() == pool.lastArisen() && o.standingTake() == steel.oldSteelIntake()
                && o.standingTake() == bm.getQuantity(yard.getId()) * yard.oldMetal(Good.STEEL)
                && o.spare() == Math.max(0, pool.lastArisen() - steel.oldSteelIntake()));
        BuildCard.Weighed w = BuildCard.weighed(g, yard);
        assertTrue("...and with less old steel arising past the yards standing than one more takes, its sector's feed gate"
                + " refuses it, in its words: " + w.why(), o.spare() >= SpreadPlanner.FEED_GATE * o.intake()
                || w.failed() == SpreadPlanner.Gate.FEED && w.why().startsWith("too little old steel"));
        CopperRefining copper = g.getSectors().copperRefining();
        BuildingsTemplate scrapCu = bm.getTemplate(CopperRefining.SCRAP_REFINERY);
        BuildCard.OldMetalBought cu = BuildCard.oldMetal(g, scrapCu);
        assertTrue("a Scrap Copper Refinery's buys the city's old copper the same way: Copper Refining's price and spare",
                cu != null && cu.metal() == Good.COPPER && cu.intake() == scrapCu.oldMetal(Good.COPPER)
                && Double.doubleToLongBits(cu.price()) == Double.doubleToLongBits(copper.oldCopperPrice())
                && cu.arisen() == g.getOldMetal().copper().lastArisen() && cu.spare() == copper.spareOldCopper()
                && BuildCard.oldMetal(g, furnace) == null);
    }

    /** e. Who buys what it makes: the sectors here whose buildings use a good, its owner's own works first, or it ships. */
    static void whoBuys(Game g) {
        BuildingManager bm = g.getBuildingManager();
        // buyers() against a count of its own: every sector whose catalogue holds a building that uses the good.
        boolean same = true;
        for (Good good : Good.values()) {
            Set<String> expect = new HashSet<>();
            for (BuildingsTemplate t : bm.getTemplates()) {
                Sector s = g.getSectors().ownerOf(t);
                if (t.uses(good) > 0 && s != null && s.key().equals(t.getSector())) expect.add(s.key());
            }
            Set<String> got = new HashSet<>();
            for (BuildCard.Buyer b : BuildCard.buyers(g, good)) {
                got.add(b.sector().key());
                same &= b.takes() == b.sector().getInputAtCapacity(good);
            }
            same &= got.equals(expect);
        }
        assertTrue("every good's buyers are the sectors whose buildings use it, each taking its standing buildings' need at"
                + " nameplate (Sector.getInputAtCapacity())", same);
        BuildingsTemplate smelter = bm.getTemplate(CopperRefining.SMELTER), panels = bm.getTemplate(WoodProducts.PANEL_MILL),
                saw = bm.getTemplate(WoodProducts.SAWMILL);
        BuildCard.Outlet acid = null, anode = null, board = null, chips = null;
        for (BuildCard.Outlet o : BuildCard.outlets(g, smelter)) { if (o.good() == Good.ACID) acid = o; if (o.good() == Good.ANODE) anode = o; }
        for (BuildCard.Outlet o : BuildCard.outlets(g, panels)) if (o.good() == Good.PANELS) board = o;
        for (BuildCard.Outlet o : BuildCard.outlets(g, saw)) if (o.good() == Good.RESIDUES) chips = o;
        CopperMining cm = g.getSectors().copperMining();
        assertTrue("a Copper Smelter's acid goes to Copper Mining's leach plants, its take theirs at nameplate; its anode to its"
                + " own refineries first", acid != null && acid.made() == smelter.makes(Good.ACID) && !acid.ships()
                && acid.others().size() == 1 && acid.others().get(0).sector() == cm
                && acid.others().get(0).takes() == cm.getInputAtCapacity(Good.ACID)
                && anode != null && anode.captive() && anode.ownTakes() == g.getSectors().copperRefining().getInputAtCapacity(Good.ANODE));
        assertTrue("a Panel Mill's panels: nobody here buys them, they ship (until the construction upgrade); a Sawmill's"
                + " residues go to its own mills first", board != null && board.ships() && chips != null && chips.captive()
                && !chips.ships());
        // ...and each of the nine pages ends on who buys what it makes: a line a good, "it ships" exactly when nobody does.
        int pages = 0;
        boolean lines = true;
        for (Sector s : g.getSectors().all()) {
            if (!(s instanceof ProcessingSector || s instanceof ExtractionSector)) continue;
            pages++;
            List<Sector.Line> chain = BuildCard.chainLines(g, s);
            int at = -1;
            for (int i = 0; i < chain.size(); i++) if (chain.get(i).label().startsWith("Who buys it here")) at = i;
            if (at < 0 || chain.size() - at - 1 != s.goodsMade().size()) { lines = false; continue; }
            int i = at + 1;
            for (Good good : s.goodsMade()) {
                boolean nobody = !s.captive(good);
                for (BuildCard.Buyer b : BuildCard.buyers(g, good)) nobody &= b.sector() == s;
                lines &= chain.get(i).label().equals(good.label()) && nobody == chain.get(i).value().endsWith("it ships");
                i++;
            }
        }
        assertTrue("each of the nine industries' pages says who here buys each good it makes, or that it ships", pages == 9 && lines);
    }

    /** f. E on a forest town: the card's crew rule is Forestry's, and its refusal the plan's own words. */
    static void crewRule(Path root) {
        Game f = ForestryCheck.forestTown(new GameFiles(root.resolve("forest"), root.resolve("forest-no-legacy")));
        Forestry fo = f.getSectors().forestry();
        BuildingsTemplate camp = f.getBuildingManager().getTemplate(Forestry.LOGGING_CAMP);
        MaterialsCheck.handWorkers(f);
        // The law leaves 10 km2 cuttable: less than a Logging Camp's 120 months.
        ForestryCheck.protect(f, 10 * ForestryCheck.M3_A_KM2);
        BusinessInvestment.Decision d = fo.plan(f.getBusinessInvestment(), f);
        BuildCard.CrewRule rule = BuildCard.crewRule(f, camp);
        report("fixture: a forest town with 10 km2 cuttable - its plan holds in E's words", !d.build && d.reason.contains("would last")
                && camp == BuildCard.smallestCrew(f), d.reason);
        BuildCard.Weighed w = BuildCard.weighed(f, camp);
        assertTrue("a Logging Camp's card: no room under E, none more, and its refusal the plan's own words, to the letter",
                !rule.room() && rule.more() == 0 && rule.cuttable() == f.getLandManager().cuttableForest()
                && rule.committed() == fo.crewsCut(true) && rule.cut() == Forestry.cutOf(camp)
                && w != null && w.failed() == SpreadPlanner.Gate.FEED && w.why().equals(d.reason));
        ForestryCheck.protect(f, f.getLandManager().getRemaining(Resource.FOREST));
        rule = BuildCard.crewRule(f, camp);
        double horizon = Forestry.FOREST_CREW_MONTHS;
        report("the whole forest cuttable: room for N more Logging Camps exactly where E allows N and not N + 1, and it carries"
                        + " cuttable / (120 x a camp's cut) in all", rule.room() && rule.more() > 0
                        && Forestry.roomFor(rule.cuttable(), rule.committed() + (rule.more() - 1) * rule.cut(), camp)
                        && !Forestry.roomFor(rule.cuttable(), rule.committed() + rule.more() * rule.cut(), camp)
                        && rule.carries() == (int) Math.floor(rule.cuttable() / (horizon * rule.cut()))
                        && rule.monthsWith() == rule.cuttable() / (rule.committed() + rule.cut()),
                String.format("%,.0f m3: room for %d more, carries %d", rule.cuttable(), rule.more(), rule.carries()));
    }

    /** g. The land office's hover by type: an offer's fields by type add up to its fields. */
    static void hoverByType(Game g) {
        CityLand land = g.getCityLand();
        World w = World.of(land.key());
        boolean adds = true;
        int offers = 0;
        for (LandParcel p : g.getLandManager().getMarket().getListing()) {
            offers++;
            for (Resource r : Resource.values()) {
                if (!r.inFields()) continue;
                long[] by = land.sitesOnByType(p, r);
                int[] sites = new int[CityLand.KINDS];
                land.fieldsOn(p.getX0(), p.getY0(), p.getX1(), p.getY1(), sites, new double[CityLand.KINDS]);
                long sum = 0;
                for (long n : by) sum += n;
                adds &= sum == sites[r.ordinal()] && sum == p.getSites(r) && by.length == r.typesOf().size();
            }
        }
        assertTrue("every offer standing: its fields of each resource by type add up to its sites (CityLand.fieldsOn()), as"
                + " listed (" + offers + " offers)", offers > 0 && adds);
        // Caused: an offer drawn round the oxide and the sulphide copper fields nearest the town, its ground unowned.
        for (Deposit.FieldType type : new Deposit.FieldType[] { Deposit.FieldType.SULPHIDE, Deposit.FieldType.OXIDE }) {
            Deposit field = unownedNearest(g, w, Resource.COPPER, type, Integer.MAX_VALUE);
            LandParcel p = g.getLandManager().getMarket().measure(new GridOffers.Rect(0, 0, 0, field.x(), field.y(),
                    field.x() + 1, field.y() + 1));
            long[] by = land.sitesOnByType(p, Resource.COPPER);
            int at = Resource.COPPER.typesOf().indexOf(type);
            report("an offer on the " + type.name().toLowerCase(Locale.ROOT) + " field nearest the town: its copper sites, all of"
                            + " that type", by[at] == field.sites() && by[1 - at] == 0 && p.getSites(Resource.COPPER) == field.sites(),
                    String.format("%d site(s) at %d,%d: %d sulphide, %d oxide", field.sites(), field.x(), field.y(), by[0], by[1]));
        }
    }

    /** h. Build's field payback by type (0.7.142): an offer's iron fields weighed by the mine that fits each, not Iron Mines on all. */
    static void paybackByType(Game g) {
        World w = World.of(g.getCityLand().key());
        Deposit hem = unownedNearest(g, w, Resource.IRON, Deposit.FieldType.HEMATITE, 19);
        Deposit mag = unownedNearest(g, w, Resource.IRON, Deposit.FieldType.MAGNETITE, Integer.MAX_VALUE);
        LandMarket market = g.getLandManager().getMarket();
        LandParcel onHem = market.measure(new GridOffers.Rect(0, 0, 0, hem.x(), hem.y(), hem.x() + 1, hem.y() + 1));
        LandParcel onMag = market.measure(new GridOffers.Rect(0, 0, 0, mag.x(), mag.y(), mag.x() + 1, mag.y() + 1));
        List<BuildAdvice.FieldMines> h = BuildAdvice.fieldMines(g, onHem), m = BuildAdvice.fieldMines(g, onMag);
        report("fixture: offers on the nearest hematite field under twenty sites and the nearest magnetite field",
                onHem.getDeposits() == hem.sites() && onMag.getDeposits() == mag.sites(),
                String.format("%d hematite site(s), %d magnetite", hem.sites(), mag.sites()));
        boolean magOnly = !m.isEmpty();
        for (BuildAdvice.FieldMines f : m) magOnly &= f.type() == Deposit.FieldType.MAGNETITE && f.mine() != null
                && f.mine().site().type() == Deposit.FieldType.MAGNETITE && f.count() * f.mine().site().sites() <= f.sites();
        assertTrue("the magnetite field is filled with magnetite works, never an Iron Mine, no more sites than it holds", magOnly);
        double[] byType = BuildAdvice.fieldEarnings(g, onHem), old = BuildAdvice.fieldEarnings(g, hem.sites());
        assertTrue("the hematite field under twenty sites takes Iron Mines alone, and its earnings are the old rule's for its"
                + " sites, to the bit", h.size() == 1 && h.get(0).mine().getId() == Mining.IRON_MINE && h.get(0).count() == hem.sites()
                && byType[0] == old[0] && Double.doubleToLongBits(byType[1]) == Double.doubleToLongBits(old[1]));
        BuildAdvice.Payback p = BuildAdvice.ironPayback(g, onMag);
        double[] e = BuildAdvice.fieldEarnings(g, onMag);
        assertTrue("Build's payback on the magnetite offer is its by-type earnings", p.mines() == (int) e[0] && p.earns() == e[1]);
    }

    /** The field of a type nearest the town's founding site whose centre plot nobody owns, of at most `maxSites` sites. */
    static Deposit unownedNearest(Game g, World w, Resource r, Deposit.FieldType type, int maxSites) {
        CityLand land = g.getCityLand();
        long fx = w.foundingX(), fy = w.foundingY();
        int cx = (int) (fx / World.CELL), cy = (int) (fy / World.CELL);
        for (int ring = 0; ring < World.CELLS; ring++) {
            Deposit best = null;
            double far = Double.MAX_VALUE;
            for (int y = cy - ring; y <= cy + ring; y++) {
                for (int x = cx - ring; x <= cx + ring; x++) {
                    if (Math.max(Math.abs(x - cx), Math.abs(y - cy)) != ring) continue;
                    if (x < 0 || y < 0 || x >= World.CELLS || y >= World.CELLS) continue;
                    for (Deposit d : w.fieldsInCell(y * World.CELLS + x, r)) {
                        if (d.type() != type || d.sites() > maxSites || land.ownsPlot(d.x(), d.y())) continue;
                        double dist = Math.hypot(d.x() - fx, d.y() - fy);
                        if (dist < far) { far = dist; best = d; }
                    }
                }
            }
            if (best != null) return best;
        }
        throw new IllegalStateException("no unowned " + type + " field on " + w);
    }
}
