package ham.citybuildersim;

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
 * The build card (0.7.25): BuildCard's figures for all 73 buildings held to
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
        // The market's groups are the design note's: Industry seven, Shops two.
        List<String> industry = new ArrayList<>(), shops = new ArrayList<>();
        for (BuildCard.Group gr : BuildCard.groups(g, BuildAdvice.INDUSTRY, null)) industry.add(gr.title());
        for (BuildCard.Group gr : BuildCard.groups(g, BuildAdvice.SHOPS, null)) shops.add(gr.title());
        assertTrue("Industry is seven groups by owning sector: " + industry, industry.equals(List.of("Food mills",
                "Food processing", "Steel", "Fabrication & machinery", "Iron", "Building materials", "Builders")));
        assertTrue("Shops is the groceries and the bank's branches: " + shops,
                shops.equals(List.of("Groceries", "The bank's branches")));
    }

    /* ============================ 2. THE HERO AND THE BARS ============================ */

    static void heroAndBars(Game g) {
        out.println("\n--- 2. the hero and the bars are the model's: the quote, the land, the staffing, the markets ---");
        double[] fill = g.getPopulationManager().getJobFillRate();
        double[] wages = g.getPopulationManager().getWagesPerType();
        int cards = 0, makers = 0, offices = 0, cityCards = 0, landBars = 0;
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
                double scale = f.kind() == BuildCard.Kind.MEALS ? 1000 : 1;

                // The hero, off the template's own fields.
                double expectFigure;
                switch (f.kind()) {
                    case HOMES:     expectFigure = t.getCapacity(); break;
                    case CUSTOMERS: case MEALS: expectFigure = t.getCoverage(); break;
                    case BRANCH:    expectFigure = Bank.CUSTOMERS_PER_BRANCH; break;
                    case RAIL:      expectFigure = t.getRailCapacity(); break;
                    case POINTS:    expectFigure = t.makes(Good.BUILDING_WORK); break;
                    case MAKER: case OFFICE: expectFigure = t.makes(f.good()); break;
                    default: {
                        BuildAdvice.Measure m = measureOf(gr);
                        expectFigure = m.kind() == BuildAdvice.Kind.ROADS ? t.getCapacity() : BuildAdvice.unit(g, m, t);
                    }
                }
                if (f.figure() != expectFigure) { hero = false; out.println("      hero: " + t.getName()); }

                // Value added, by the markets' own prices - an office's work at what the world pays a seat.
                if (f.inMonths()) {
                    double v = 0;
                    for (Map.Entry<Good, Double> e : t.goodsMade().entrySet()) {
                        Good good = e.getKey();
                        if (!good.traded()) continue;
                        v += e.getValue() * (f.kind() == BuildCard.Kind.OFFICE
                                ? g.getSectors().businessServices().priceOfSeat(good)
                                : g.getMarkets().get(good).getLocalPrice());
                    }
                    for (Map.Entry<Good, Double> e : t.goodsUsed().entrySet()) {
                        if (e.getKey().traded()) v -= e.getValue() * g.getMarkets().get(e.getKey()).getLocalPrice();
                    }
                    if (f.valueAdded() != v || f.unit() != v) { va = false; out.println("      value added: " + t.getName()); }
                    if (f.kind() == BuildCard.Kind.OFFICE) offices++; else makers++;
                }

                // Bar 1: the quote at one over the unit, to the bit.
                double expect1 = f.kind() == BuildCard.Kind.CITY
                        ? (f.unit() > 0 ? q.total / f.unit() : Double.POSITIVE_INFINITY)
                        : f.unit() > 0 ? q.total / f.unit() * scale : Double.NaN;
                if (Double.doubleToLongBits(f.bar1()) != Double.doubleToLongBits(expect1)) {
                    bar1 = false;
                    out.println("      bar 1: " + t.getName() + " " + f.bar1() + " against " + expect1);
                }

                // Bar 2, by its kind.
                double expect2;
                switch (f.bar2Kind()) {
                    case LAND:
                        landBars++;
                        expect2 = f.unit() > 0 ? t.getLandSqFt() / f.unit() * scale : Double.NaN;
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
                boolean kindOk = f.kind() == BuildCard.Kind.CITY
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
        assertTrue("the hero's figure is the template's own (capacity, coverage, rail, points, its first good)"
                + " or a city building's served at today's staffing", hero);
        assertTrue("value added is what one makes less what it uses at the markets' prices, an office's at"
                + " the price of a seat (" + makers + " makers, " + offices + " offices)", va && makers > 0 && offices == 3);
        assertTrue("bar 1 is the quote at one over the unit, to the bit, on every card", bar1);
        assertTrue("bar 2 is land per unit on the market's cards, unstaffable posts of 100 on an office's,"
                + " and unfilled posts per 10,000 served on the city's", bar2 && landBars > 0);
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
                    BuildCard.Investors i = BuildCard.investors(g, f.template());
                    words &= i.word().equals(g.getLastInvestment(i.slot()))
                            && i.slot().equals(f.template().isOwnedBySector() ? f.template().getSector() : "Bank");
                    if (!i.word().isEmpty()) said++;
                }
            }
        }
        assertTrue("every market card's word is Game.getLastInvestment() under its sector, the branch's under"
                + " \"Bank\" (" + said + " of them with a word, the month after the load)", words && said > 0);
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

        // After a load the word is gone until a month runs - and the bank's planner sees the bank.
        quietly(() -> g.saveGame(10, "buildcardcheck"));
        Game twin = new Game(new GameFiles(root.resolve("town"), root.resolve("town-no-legacy")));
        quietly(() -> twin.loadGameSave(10));
        boolean empty = true;
        for (BuildingsTemplate t : twin.getBuildingManager().getTemplates()) {
            if (BuildCard.kindOf(t) == BuildCard.Kind.CITY) continue;
            empty &= BuildCard.investors(twin, t).word().isEmpty();
        }
        assertTrue("loaded, no market card has a word until a month runs (the words are not saved)", empty);
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
        BuildingsTemplate mine = template(g, "Iron Mine");
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

        // ...and a building that passes them all has none.
        int clear = 0;
        boolean none = true;
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            if (BuildCard.kindOf(t) == BuildCard.Kind.CITY) continue;
            BuildCard.Investors inv = BuildCard.investors(g, t);
            boolean passes = g.hasDepositFor(t, 1) && g.hasLicencesFor(t, 1) && inv.owner().staffing(t).passes()
                    && t.getLandSqFt() <= land.getAvailableSqFt() && inv.estimate() > 0;
            if (passes) clear++;
            none &= passes == (inv.own() == null);
        }
        assertTrue("a market building that passes every gate has no \"this one:\", and one that fails one has it ("
                + clear + " pass)", none && clear > 0);
    }

    /* ============================ 7. THE VERDICT ============================ */

    static void theVerdict(Game g) {
        out.println("\n--- 7. the verdict: in buildStack()'s order, and buildStack()'s own answer ---");
        LandManager land = g.getLandManager();

        BuildingsTemplate mine = template(g, "Iron Mine");
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
            { "Holding: no land - needs 480,000 sq ft, 324,000 free", BuildCard.WordKind.LAND },
            { "Holding: the city is short of homes - ground is worth more to live on", BuildCard.WordKind.LAND },
            { "Could not build Machine Works - needs 600,000 sq ft, 324,000 free", BuildCard.WordKind.LAND },
            { "Holding: the city could staff 78% of a Machine Works; it wants 80%", BuildCard.WordKind.STAFF },
            { "Holding: no one could staff an Engineering Office's engineering posts", BuildCard.WordKind.STAFF },
            { "Holding: the city could staff 38% of a Commercial Bank; it wants 80%", BuildCard.WordKind.STAFF },
            { "Holding: 2.0 months of work queued, but the city could staff 40% of a Construction Depot; it wants 80%", BuildCard.WordKind.STAFF },
            { "Holding: 39 more engineering licences before a practice could open", BuildCard.WordKind.LICENCE },
            { "Holding: 1 more medical licence before a practice could open", BuildCard.WordKind.LICENCE },
            { "Holding: no deposit to dig", BuildCard.WordKind.ORE },
            { "Holding: no spare local ore to smelt", BuildCard.WordKind.ORE },
            { "Holding: the ore is worked out", BuildCard.WordKind.ORE },
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
                if (BuildCard.kindOf(t) != BuildCard.Kind.CITY && BuildCard.ownerOf(g, t) == s) mine++;
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
        BuildingsTemplate mine = template(g, "Iron Mine"), eso = template(g, "Engineering Services Office");
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
        assertTrue("...a school's seats, on who would come over them - and a crowd off the list but over its seats is amber", crowds);
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
}
