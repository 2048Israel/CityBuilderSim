package ham.citybuildersim;

import ham.citybuildersim.sectors.Oil;
import ham.citybuildersim.sectors.Oil.Vintage;
import ham.citybuildersim.sectors.Oil.WellKind;
import ham.citybuildersim.sectors.RefineryFlow;
import ham.citybuildersim.sectors.RefineryFlow.Kind;
import ham.citybuildersim.sectors.Refining;
import ham.citybuildersim.sectors.SpreadPlanner;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The oil industry screen (0.7.96, batch O12; runs/spec-oil.md 2.13):
 * OilView's figures, words, chart and levers, held to the model's own reads
 * in played towns.
 *
 * WHY. Oil's Operations page draws the whole chain since 0.7.96 - the wells
 * by kind and what they would lift over ten years, the refinery's units with
 * their spreads and gates, every product's month, the city's reserve and its
 * two levers. A forecast that did not decline as the wells do, a pool lifted
 * past its oil, a unit's gate that was not the planner's, a product row that
 * disagreed with Refining's picture, or a Fill that ordered what the game
 * would not, would be a confident wrong page. The screen is checked by eye on
 * the PC; this holds what it shows.
 *
 * What this has to prove:
 *   1. THE WELLS AHEAD, PURE: the platform wells are dealt to the platforms
 *      oldest to oldest, each its wells in slots; a vintage asks its wells x
 *      the nameplate x its profile, nothing once worn out; a land well's year
 *      is nine tenths of the year before and a platform's holds three years
 *      then keeps PLATFORM_KEEPS_A_YEAR; each pool's lift stops at its oil,
 *      to the tonne, and the offshore pool is shared by the platforms' asks.
 *   2. THE WELLS IN A TOWN: the counts, the pools and the fields are Oil's
 *      and the land office's; the month's lift by series is the month's by
 *      pool; the vintages dealt to the platforms ask the nameplate the month
 *      was struck on, and month by month the wells lift that at the month's
 *      rate - the forecast's own arithmetic - with nothing new built; and the
 *      years ahead never lift a pool past its oil.
 *   3. THE UNITS: the crude units, then every kind, standing first; a
 *      standing kind's run, share and gate are the refinery picture's; every
 *      kind's spread is RefineryFlow.spread() at the city's values; a kind
 *      not standing has the planner's appraisal of one more, its spare feed
 *      the outlook's; the crude units' fuel short and crude spare the
 *      outlook's; and each gate is worded by its own pill.
 *   4. THE PRODUCTS: crude made is the wells' month, used the refiners', its
 *      imports the clearing's; each product's month and takers are the
 *      refinery picture's, to the bit; the prices are the markets'; and each
 *      product's world price over crude's is the research's ladder.
 *   5. THE FIGURES: the lift in barrels a day, the two pools' tonnes, the
 *      crude bought abroad, and the products across the edge as the Trade tab
 *      reads them (Game.getTradeByGood()), to the bit.
 *   6. THE RESERVE AND ITS LEVERS: the reserve is StrategicReserve's; Fill's
 *      reach is what Game.fillReserve() orders - the room left, or what the
 *      treasury can pay at crude's import price - and an order past it is cut
 *      to it; what a lever would do is the arithmetic of the order; a month
 *      on, the crude held is what the clearing bought; with no reserve none
 *      is shown.
 *   7. THE CHART TO SCALE: each year's bar foots to its series' barrels a
 *      day times the scale; the month solid and the years ahead pale; inside
 *      the chart; the years under every fifth bar; with nothing to lift, the
 *      chart says so.
 *   8. NOT COUNTED, AND PURE: a city just loaded counts no month - the lift,
 *      the crude and the products say so - while its wells, pools, units and
 *      reserve read; a month on it counts; read twice the same page; reading
 *      it every month leaves a twin's month unchanged.
 *
 * Every fixture causes its condition: the sea town buys its sea field and
 * is handed dry oil by fiat (WellCheck's), the campus town stands its units
 * (RefineryViewCheck's), the reserve is stood and filled.
 */
public class OilViewCheck {

    static int fails = 0;
    static PrintStream out = System.out;
    static PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-100s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-100s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    /** Within a billionth of the larger, or of one. */
    static boolean near(double a, double b) {
        return Math.abs(a - b) <= 1e-9 * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    /** The oil handed to the sea town's two dry sites by fiat: the ground pool, plenty for the sections' months. */
    static final double GROUND = 2_000_000;

    /** The fill the sea town orders for its reserve. */
    static final double FILL = 40_000;

    /** Months the sea town plays before it is read: past a year, so its platform's wells are in their plateau's second year. */
    static final int SEA_MONTHS = 14;

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        WellCheck.out = out;
        WellCheck.quiet = quiet;

        theWellsAhead();

        Game sea = seaTown();
        if (sea != null) {
            OilView.View v = OilView.of(sea);
            printTown("The sea town", sea, v);
            theWellsInATown(sea, v);
            theReserve(sea);
            theChart(OilView.of(sea));
        }

        Game campus = RefineryViewCheck.town("oilviewcheck-campus", RefineryViewCheck.WELLS, RefineryViewCheck.CAMPUS);
        quietly(() -> campus.simulateMonths(RefineryViewCheck.MONTHS));
        OilView.View c = OilView.of(campus);
        printTown("The campus town", campus, c);
        theUnits(campus, c);
        theProducts(campus, c);
        theFigures(campus, c);
        notCountedAndPure(campus);

        for (GameFiles f : WellCheck.FILES.values()) LongPlaytest.cleanUp(f.getDirectory().getParent());
        for (GameFiles f : RefineryViewCheck.FILES.values()) LongPlaytest.cleanUp(f.getDirectory().getParent());
        out.println();
        if (fails == 0) {
            out.println("All checks passed.");
        } else {
            out.println(fails + " FAILED");
            System.exit(fails);
        }
    }

    static void printTown(String name, Game g, OilView.View v) {
        out.printf("%n%s at month %,d: %,d people; %d land wells, %d platform wells on %d platform(s); the ground pool %s, the"
                        + " offshore pool %s; lifted %s; %d unit rows, %d product rows%n", name, g.getMonth(),
                g.getPopulationManager().getPopulation(), v.landWells(), v.platformWells(), v.platforms().size(),
                OilView.megaTonnes(v.groundLeft()), OilView.megaTonnes(v.seaLeft()), OilView.barrelsWords(v.lifted()),
                v.units().size(), v.products().size());
    }

    /**
     * WellCheck's sea town (its sea field bought, every sector held), two dry
     * sites handed GROUND by fiat beside it, a jacket and a well in each of
     * its slots, two land wells, a Strategic Reserve with FILL ordered;
     * SEA_MONTHS played. Null when the field cannot be bought.
     */
    static Game seaTown() {
        Game g = WellCheck.seaTown("oilviewcheck-sea");
        if (g == null) {
            report("fixture: the sea town buys the ground over its sea field", false, "could not");
            return null;
        }
        LandManager land = g.getLandManager();
        BuildingManager b = g.getBuildingManager();
        int seaSites = land.getSites(Resource.OIL, false);
        double bought = g.getCityLand().purchasedAmount(Resource.OIL);
        quietly(() -> {
            land.restoreSites(Resource.OIL, seaSites + 2, bought + GROUND);
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.buildStack(b.getTemplateByName("Offshore Platform"), 1, true);
            g.simulateMonths(1);
            g.buildStack(b.getTemplateByName("Platform Well"), seaSites, true);
            g.buildStack(b.getTemplateByName("Oil Well"), 2, true);
            g.buildStack(b.getTemplateByName("Strategic Reserve"), 1, true);
            g.simulateMonths(1);
            g.fillReserve(FILL);
            g.simulateMonths(SEA_MONTHS - 2);
        });
        Oil oil = g.getSectors().oil();
        report("fixture: a platform with a well in each slot, two land wells on the dry sites, a reserve holding its fill",
                oil.jacketsStanding() == 1 && oil.platformWellsInSlots() == seaSites && oil.landWellsStanding() == 2
                        && g.getReserve().getTonnes() > 0,
                String.format("%d platform wells, %d land wells, %,.0f t in the reserve", oil.platformWellsInSlots(),
                        oil.landWellsStanding(), g.getReserve().getTonnes()));
        return g;
    }

    /* ============================ 1. THE WELLS AHEAD ============================ */

    static void theWellsAhead() {
        out.println("\n--- 1. the wells ahead, pure: dealt, asked, declined, worn out, and no pool lifted past its oil ---");
        int month = 1_000;
        double each = 415;
        List<Vintage> v = List.of(new Vintage(990, 5, WellKind.PLATFORM), new Vintage(995, 4, WellKind.PLATFORM),
                new Vintage(996, 3, WellKind.LAND), new Vintage(998, 6, WellKind.PLATFORM));
        List<Oil.Platform> ps = List.of(new Oil.Platform(1, 1, 7, 980), new Oil.Platform(1, 2, 8, 985));
        List<List<Vintage>> dealt = OilView.deal(v, ps);
        report("the platform wells are dealt oldest to the oldest platform, each its wells in slots: 5 + 2 to the first, 2 + 6"
                        + " to the second; the land wells are none of theirs",
                dealt.equals(List.of(List.of(new Vintage(990, 5, WellKind.PLATFORM), new Vintage(995, 2, WellKind.PLATFORM)),
                        List.of(new Vintage(995, 2, WellKind.PLATFORM), new Vintage(998, 6, WellKind.PLATFORM)))),
                dealt.toString());

        double ask = OilView.ask(List.of(new Vintage(month - 30, 3, WellKind.LAND)), each, WellKind.LAND, month);
        report("a vintage asks its wells x the nameplate x its profile at its age, to the bit",
                ask == 3 * each * Oil.profile(WellKind.LAND, 30), String.format("%,.6f t", ask));
        int life = Oil.lifeMonths(WellKind.LAND);
        assertTrue("...and nothing once worn out: at its life's month it asks nothing, the month before its profile",
                OilView.ask(List.of(new Vintage(month - life, 1, WellKind.LAND)), each, WellKind.LAND, month) == 0
                        && OilView.ask(List.of(new Vintage(month - life + 1, 1, WellKind.LAND)), each, WellKind.LAND, month)
                        == each * Oil.profile(WellKind.LAND, life - 1));

        // A land vintage new this month and a platform's new too, with plenty of oil, at a rate.
        double rate = .9;
        List<Vintage> land = List.of(new Vintage(month, 10, WellKind.LAND));
        List<List<Vintage>> sea = List.of(List.of(new Vintage(month, 12, WellKind.PLATFORM)));
        double[][] f = OilView.forecast(land, sea, each, each, 1e12, 1e12, rate, month, OilView.FORECAST_YEARS);
        double year1 = 0;
        for (int t = month + 1; t <= month + 12; t++) year1 += 10 * each * Oil.profile(WellKind.LAND, t - month) * rate / 12;
        report("a land vintage's first year ahead is its twelve months' mean at the rate, from the month after this one",
                near(f[0][0], year1), String.format("%,.6f t a month", f[0][0]));
        boolean tenth = true;
        for (int y = 1; y < OilView.FORECAST_YEARS; y++) tenth &= Math.abs(f[0][y] / f[0][y - 1] - Oil.LAND_KEEPS_A_YEAR) < 1e-12;
        assertTrue("...and each year after nine tenths of the year before (LAND_KEEPS_A_YEAR)", tenth);
        int plateauYears = Oil.PLATFORM_PLATEAU_MONTHS / 12;
        boolean flat = true;
        for (int y = 0; y < plateauYears; y++) flat &= near(f[1][y], 12 * each * rate);
        boolean after = true;
        for (int y = plateauYears + 1; y < OilView.FORECAST_YEARS; y++) after &= Math.abs(f[1][y] / f[1][y - 1] - Oil.PLATFORM_KEEPS_A_YEAR) < 1e-12;
        report("a platform's wells hold their whole nameplate through the plateau's three years, then keep PLATFORM_KEEPS_A_YEAR",
                flat && after && f[1][plateauYears] < f[1][plateauYears - 1], String.format("%,.1f, then %,.1f t a month", f[1][0],
                        f[1][plateauYears]));

        // A vintage a month short of its life the month after this one lifts that month, then wears out.
        double[][] worn = OilView.forecast(List.of(new Vintage(month + 2 - life, 2, WellKind.LAND)), List.of(), each, each, 1e12, 0,
                1, month, 2);
        report("a vintage a month from its life lifts that month and none after: a twelfth of its month in year one, nothing in"
                        + " year two",
                near(worn[0][0], 2 * each * Oil.profile(WellKind.LAND, life - 1) / 12) && worn[0][1] == 0,
                String.format("%,.6f, %,.6f t a month", worn[0][0], worn[0][1]));

        // Pools that run dry.
        double ground = 50_000, offshore = 30_000;
        double[][] dry = OilView.forecast(land, List.of(List.of(new Vintage(month, 5, WellKind.PLATFORM)),
                List.of(new Vintage(month, 7, WellKind.PLATFORM))), each, each, ground, offshore, 1, month, OilView.FORECAST_YEARS);
        double l = 0, a = 0, b = 0;
        for (int y = 0; y < OilView.FORECAST_YEARS; y++) {
            l += dry[0][y] * 12;
            a += dry[1][y] * 12;
            b += dry[2][y] * 12;
        }
        report("each pool's lift stops at its oil, to the tonne: the land wells the ground pool's, the platforms the offshore pool's",
                near(l, ground) && near(a + b, offshore), String.format("%,.6f of %,.0f t; %,.6f of %,.0f t", l, ground, a + b, offshore));
        report("...the offshore pool shared by the platforms' asks: 5 wells to 7, to the end",
                near(a / b, 5.0 / 7), String.format("%,.3f t and %,.3f t", a, b));
    }

    /* ============================ 2. THE WELLS IN A TOWN ============================ */

    static void theWellsInATown(Game g, OilView.View v) {
        out.println("\n--- 2. the wells in a town: Oil's counts, pools and lift; the vintages' ask is the nameplate, lifted at the rate ---");
        Oil oil = g.getSectors().oil();
        LandManager land = g.getLandManager();
        report("the wells are Oil's: its land wells, its platform wells in slots, its platforms, its pipe",
                v.landWells() == oil.landWellsStanding() && v.platformWells() == oil.platformWellsInSlots()
                        && v.platforms().size() == oil.platformsNow().size() && v.pipeKm() == oil.pipeKmStanding(),
                v.landWells() + " land, " + v.platformWells() + " at sea on " + v.platforms().size());
        LandManager.SeaField field = oil.shallowFields().get(0);
        OilView.Platform p = v.platforms().get(0);
        report("...each platform its field's depth and distance (Oil.lengthKm()), its wells and slots, its name",
                p.name().equals("Platform A") && p.depth() == field.depth() && p.wells() == oil.platformsNow().get(0).wells()
                        && p.slots() == oil.slotsStanding()
                        && p.km() == Oil.lengthKm(field.field(), g.getCityLand().siteX(), g.getCityLand().siteY()),
                String.format("%s: %.1f m deep, %d km out, %d wells", p.name(), p.depth(), p.km(), p.wells()));
        report("the pools are the land office's, to the bit: the ground's and the sea's left; the fields each pool's",
                v.groundLeft() == land.getOilLeftOnGround() && v.seaLeft() == land.getOilLeftAtSea() && v.seaFields() == 1
                        && v.groundFields() == 0 && v.shallowSites() == field.sites(),
                String.format("%,.0f t on the ground (by fiat, no field), %,.0f t at sea on %d field", v.groundLeft(), v.seaLeft(),
                        v.seaFields()));
        double atSea = 0;
        for (int i = 1; i < v.series().size(); i++) atSea += v.series().get(i).now();
        report("the month's lift by series is the month's by pool: the land wells' the ground's, the platforms' together the sea's",
                v.counted() && v.series().get(0).now() == oil.getLiftedOnGround() && near(atSea, oil.getLiftedAtSea())
                        && near(v.lifted(), oil.output(Good.CRUDE).produced),
                String.format("%,.3f t + %,.3f t", v.series().get(0).now(), atSea));

        // Month by month, nothing new built: the vintages' ask is the nameplate struck, and the lift that at the rate.
        boolean struck = true, lifted = true;
        String last = "";
        for (int m = 0; m < 6; m++) {
            quietly(() -> g.simulateMonths(1));
            List<Vintage> vs = oil.vintagesNow();
            double askLand = OilView.ask(landOf(vs), oil.wellNameplate(), WellKind.LAND, g.getMonth());
            double askSea = 0;
            for (List<Vintage> d : OilView.deal(vs, oil.platformsNow())) {
                askSea += OilView.ask(d, oil.platformWellNameplate(), WellKind.PLATFORM, g.getMonth());
            }
            Sector.Output o = oil.output(Good.CRUDE);
            double rate = o.capacity > 0 ? o.produced / o.capacity : 0;
            struck &= near(askLand + askSea, o.capacity);
            lifted &= near(oil.getLiftedOnGround(), askLand * rate) && near(oil.getLiftedAtSea(), askSea * rate);
            last = String.format("month %d: asked %,.3f + %,.3f t, struck %,.3f t at %.4f", g.getMonth(), askLand, askSea,
                    o.capacity, rate);
        }
        report("each month the vintages, dealt to the platforms, ask the nameplate the month was struck on (the production row's)",
                struck, last);
        assertTrue("...and each pool's wells lift their ask at the month's rate - the forecast's arithmetic - nothing new built",
                lifted);

        OilView.View w = OilView.of(g);
        double[][] again = OilView.forecast(landOf(oil.vintagesNow()), OilView.deal(oil.vintagesNow(), oil.platformsNow()),
                oil.wellNameplate(), oil.platformWellNameplate(), land.getOilLeftOnGround(), land.getOilLeftAtSea(),
                oil.getOperatingRate(), g.getMonth(), OilView.FORECAST_YEARS);
        boolean same = true;
        for (int s = 0; s < w.series().size(); s++) same &= java.util.Arrays.equals(w.series().get(s).ahead(), again[s]);
        double landYears = 0, seaYears = 0;
        for (int y = 0; y < OilView.FORECAST_YEARS; y++) {
            landYears += w.series().get(0).ahead()[y] * 12;
            for (int s = 1; s < w.series().size(); s++) seaYears += w.series().get(s).ahead()[y] * 12;
        }
        report("the chart's years ahead are the forecast of the wells standing at the wells' rate, each pool within its oil",
                same && landYears <= w.groundLeft() * (1 + 1e-12) && seaYears <= w.seaLeft() * (1 + 1e-12),
                String.format("year one %s on land, %s at sea", OilView.barrelsWords(w.series().get(0).ahead()[0]),
                        OilView.barrelsWords(w.series().get(1).ahead()[0])));
        List<OilView.Fact> facts = OilView.landFacts(w);
        report("the land card's decline is LAND_KEEPS_A_YEAR's tenth, its reserves the ground pool's",
                facts.get(2).value().equals("−10% a year") && facts.get(3).value().startsWith(OilView.megaTonnes(w.groundLeft())),
                facts.get(2).value() + "; " + facts.get(3).value());
        List<OilView.PlatformLine> lines = OilView.platformLines(w);
        report("...the platform's line its depth, its distance and its plateau's year, its lift in barrels a day",
                lines.size() == 1 && lines.get(0).words().contains("plateau, year 2 of 3")
                        && lines.get(0).figure().equals(OilView.barrelsWords(w.platforms().get(0).lifted())),
                lines.get(0).words() + " · " + lines.get(0).figure());
    }

    static List<Vintage> landOf(List<Vintage> vs) {
        List<Vintage> out = new ArrayList<>();
        for (Vintage x : vs) if (x.kind() == WellKind.LAND) out.add(x);
        return out;
    }

    /* ============================ 3. THE UNITS ============================ */

    static void theUnits(Game g, OilView.View v) {
        out.println("\n--- 3. the units: the refinery picture's runs and gates, the planner's spreads and appraisals ---");
        Refining r = g.getSectors().refining();
        RefineryView.View rv = v.refinery();
        List<OilView.UnitRow> rows = v.units();
        boolean order = rows.get(0).crude() && rows.size() == 1 + Kind.values().length;
        boolean standingFirst = true, seenRest = false;
        for (int i = 1; i < rows.size(); i++) {
            boolean on = rows.get(i).standing() > 0;
            if (!on) seenRest = true;
            else if (seenRest) standingFirst = false;
        }
        report("a row for the crude units, then one for every kind, those standing first",
                order && standingFirst && rows.get(rows.size() - 1).kind() == Kind.ASPHALT,
                rows.size() + " rows; last " + rows.get(rows.size() - 1).kind());
        boolean same = true;
        for (OilView.UnitRow u : rows) {
            if (u.crude() || u.standing() == 0) continue;
            RefineryView.Unit x = rv.unit(u.kind());
            same &= x != null && u.run() == x.run() && u.share() == x.share() && u.standing() == x.standing()
                    && u.feed() == x.feed() && u.gate() == x.gate();
        }
        assertTrue("a standing kind's run, share, nameplate and one more are the refinery picture's", same);
        double[] values = r.cityValues();
        boolean spreads = true;
        for (OilView.UnitRow u : rows) if (!u.crude()) spreads &= u.citySpread() == RefineryFlow.spread(u.kind(), values);
        assertTrue("every kind's spread is RefineryFlow.spread() at the city's own values, to the bit", spreads);
        OilView.UnitRow crude = rows.get(0);
        report("the crude units: the picture's count, its crude at nameplate and its rate",
                crude.standing() == rv.crudeUnits() && crude.feed() == rv.flow().crude() && crude.share() == rv.rate()
                        && crude.run() == rv.flow().crude() * rv.rate(),
                String.format("%d at %.4f, %s", crude.standing(), crude.share(), RefineryView.barrels(crude.feed())));
        Refining.Outlook o = r.outlook(g.getBusinessInvestment());
        RefineryFlow.Flow weighed = RefineryFlow.solve(o.feed(), o.crude(), o.mix(), o.values());
        report("...their fuel short and crude spare the planner's outlook's",
                crude.fuelShort() == Math.max(0, o.room()) && crude.spare() == Math.max(0, o.spareCrude()),
                String.format("%,.0f L short, %,.0f t spare", crude.fuelShort(), crude.spare()));
        OilView.UnitRow asphalt = v.unit(Kind.ASPHALT);
        List<BuildingsTemplate> sizes = new ArrayList<>();
        for (BuildingsTemplate t : g.getBuildingManager().getTemplatesBySector(Sectors.REFINING)) {
            if (t.refineryUnit() == Kind.ASPHALT) sizes.add(t);
        }
        SpreadPlanner.Candidate one = RefineryView.oneMore(r, g, sizes);
        report("a kind not standing (the asphalt unit, on medium crude) has the planner's appraisal of one more and its spare feed",
                asphalt.standing() == 0 && asphalt.gate() != null && asphalt.gate().failed() == one.failed()
                        && asphalt.gate().earns() == one.earns() && asphalt.gate().cost() == one.cost()
                        && asphalt.spare() == Math.max(0, Refining.spareFeed(weighed, Kind.ASPHALT)),
                asphalt.gate().failed() + ", " + RefineryView.litres(asphalt.spare()) + " spare");
        OilView.UnitLine line = OilView.unitLine(v, asphalt);
        report("...worded by its gate: no feed, its spare against what one would need",
                line.state().equals("no feed") && line.stateColour().equals(OilView.PILL_GATE)
                        && line.words().equals(RefineryView.litres(asphalt.spare()) + " spare, needs "
                        + RefineryView.litres(SpreadPlanner.FEED_GATE * asphalt.gate().template().feedPerMonth())),
                line.words());

        // Each gate's pill, on a candidate that fails it.
        BuildingsTemplate t = sizes.get(0);
        String[][] pills = {
                state(t, null, false, 1, 0, .1), state(t, null, false, 0, 1, .1), state(t, null, false, 0, 0, .1),
                state(t, SpreadPlanner.Gate.FEED, false, 0, 0, .1), state(t, SpreadPlanner.Gate.FEED, true, 0, 0, .1),
                state(t, SpreadPlanner.Gate.LAND, false, 0, 0, .1), state(t, SpreadPlanner.Gate.STAFF, false, 0, 0, .1),
                state(t, SpreadPlanner.Gate.MONEY, false, 0, 0, .1), state(t, SpreadPlanner.Gate.MONEY, false, 0, 0, -.1) };
        String[] want = { "standing", "building", "would pay", "no feed", "no feed", "no ground", "no staff", "does not pay", "would lose" };
        String[] colours = { OilView.PILL, OilView.PILL_BUILDING, OilView.PILL_GOOD, OilView.PILL_GATE, OilView.PILL_GATE,
                OilView.PILL_GATE, OilView.PILL_GATE, OilView.PILL_BAD, OilView.PILL_BAD };
        boolean worded = true;
        StringBuilder said = new StringBuilder();
        for (int i = 0; i < pills.length; i++) {
            worded &= pills[i][0].equals(want[i]) && pills[i][1].equals(colours[i]);
            said.append(pills[i][0]).append(i < pills.length - 1 ? ", " : "");
        }
        report("each state its pill: standing, building, would pay, no feed (a unit's and the crude units'), no ground, no staff,"
                + " and money: does not pay, or would lose with no spread", worded && pills[4][2].startsWith("fuel short"), said.toString());
    }

    /** The pill a fixture row gets: `standing` and `onSite` buildings, one more failing `gate` (null: passes), the crude units' if `crude`, at a spread. */
    static String[] state(BuildingsTemplate t, SpreadPlanner.Gate gate, boolean crude, int standing, int onSite, double spread) {
        SpreadPlanner.Candidate c = new SpreadPlanner.Candidate(t, 10, 1000, gate, gate == null ? null : "fixture",
                gate == null ? Map.of() : Map.of(gate, "fixture"), List.of());
        OilView.UnitRow u = new OilView.UnitRow(crude ? null : Kind.ASPHALT, standing, onSite, 0, 0, Double.NaN, spread, c, 0,
                crude ? 0 : Double.NaN, onSite > 0 ? 3 : 0);
        return OilView.stateOf(u);
    }

    /* ============================ 4. THE PRODUCTS ============================ */

    static void theProducts(Game g, OilView.View v) {
        out.println("\n--- 4. the products: the refinery picture's month and takers, the markets' prices, the research's ladder ---");
        Oil oil = g.getSectors().oil();
        Refining r = g.getSectors().refining();
        OilView.Product crude = v.products().get(0);
        Sector.Input in = r.inputRow(Good.CRUDE);
        GoodsMarket cm = g.getMarkets().get(Good.CRUDE);
        report("crude first: made the wells' month, used the refiners' purchase, imported the clearing's",
                crude.good() == Good.CRUDE && crude.made() == oil.getLiftedOnGround() + oil.getLiftedAtSea()
                        && near(crude.made(), oil.output(Good.CRUDE).produced)
                        && near(crude.used(), in.boughtLocal + in.imported) && near(crude.imported(), cm.getImported()),
                String.format("made %,.0f t, used %,.0f t, imported %,.0f t", crude.made(), crude.used(), crude.imported()));
        boolean rows = v.products().size() == 1 + RefineryView.TANK_ORDER.length, month = true, takers = true;
        for (int i = 0; i < RefineryView.TANK_ORDER.length; i++) {
            Good gd = RefineryView.TANK_ORDER[i];
            OilView.Product p = v.products().get(i + 1);
            RefineryView.Product x = v.refinery().product(gd);
            rows &= p.good() == gd;
            month &= x == null ? p.made() == 0 && p.used() == 0 && p.imported() == 0 && p.exported() == 0
                    : p.made() == x.made() && p.used() == x.used() && p.imported() == x.imported() && p.exported() == x.exported();
            List<String> mine = new ArrayList<>(), theirs = new ArrayList<>();
            for (OilView.Taker tk : p.takers()) mine.add(tk.name() + "=" + tk.units());
            for (RefineryView.Buyer b : v.refinery().buyers()) {
                if (b.kind() != RefineryView.BuyerKind.ABROAD && b.of(gd) > 0) theirs.add(RefineryView.buyerName(b) + "=" + b.of(gd));
            }
            takers &= mine.size() == theirs.size() && mine.containsAll(theirs);
        }
        assertTrue("then the nine products in the tank's order", rows);
        assertTrue("each product's made, used, imported and exported are the refinery picture's, to the bit", month);
        assertTrue("...and its takers the picture's - every buyer, the tanks and the idled; the world is the exported column", takers);
        boolean prices = true;
        for (OilView.Product p : v.products()) {
            GoodsMarket m = g.getMarkets().get(p.good());
            prices &= p.price() == m.getLocalPrice() && p.world() == (m.importPrice() + m.exportPrice()) / 2;
        }
        assertTrue("every price here is the market's, the world's halfway between its import and its export price", prices);
        // The research's ladder (spec-oil 2.1; Good's THE PRICES), to the two places Good's header writes it.
        Object[][] ladder = { { Good.CRUDE, 1.0 }, { Good.LPG, .46 }, { Good.NAPHTHA, 1.0 }, { Good.PETROL, 1.20 }, { Good.JET, 1.28 },
                { Good.DIESEL, 1.35 }, { Good.LUBRICANTS, 1.89 }, { Good.FUEL_OIL, .98 }, { Good.BITUMEN, 1.08 }, { Good.COKE, .155 } };
        boolean onLadder = true;
        StringBuilder s = new StringBuilder();
        for (Object[] l : ladder) {
            double x = OilView.ladder((Good) l[0]);
            onLadder &= Math.abs(x - (Double) l[1]) < .005;
            s.append(String.format("%s %.3f ", ((Good) l[0]).name().toLowerCase(), x));
        }
        report("each product's world price over crude's is the research's ladder: a litre against a litre, bitumen and coke a tonne",
                onLadder, s.toString().trim());
        OilView.ProductLine petrol = OilView.productLines(v).get(1 + java.util.Arrays.asList(RefineryView.TANK_ORDER).indexOf(Good.PETROL));
        report("...and where petrol went names the cars first, as Refining's picture does", petrol.where().startsWith("cars "),
                petrol.where());
    }

    /* ============================ 5. THE FIGURES ============================ */

    static void theFigures(Game g, OilView.View v) {
        out.println("\n--- 5. the figures: the lift, the pools, the crude bought abroad, the products across the edge ---");
        List<OilView.Figure> f = OilView.figures(v);
        report("the lift in barrels a day: the month's tonnes x Refining.CRUDE_LITRES_PER_TONNE over a barrel a day's month",
                f.get(0).value().equals(OilView.barrelsWords(v.lifted())) && OilView.barrelsADay(v.lifted())
                        == v.lifted() * Refining.CRUDE_LITRES_PER_TONNE / RefineryFlow.LITRES_A_MONTH_PER_BARREL_A_DAY,
                f.get(0).value() + " · " + f.get(0).line());
        report("the reserves left are the two pools'", f.get(1).value().equals(OilView.megaTonnes(v.groundLeft() + v.seaLeft())),
                f.get(1).value() + " · " + f.get(1).line());
        double in = 0, bill = 0;
        for (Trade t : g.getMarkets().get(Good.CRUDE).getTrades()) {
            if (t.isImport()) {
                in += t.units();
                bill += t.value();
            }
        }
        report("the crude bought abroad is crude's clearing's, tonnes and money", v.crudeImported() == in && v.crudeImportBill() == bill
                && in > 0 && f.get(2).value().equals(OilView.barrelsWords(in)), f.get(2).value() + " · " + f.get(2).line());
        Map<Good, Sectors.GoodTrade> edge = g.getTradeByGood().goods();
        double sold = 0, bought = 0;
        for (Good gd : RefineryView.TANK_ORDER) {
            Sectors.GoodTrade t = edge.get(gd);
            if (t == null) continue;
            sold += t.sold();
            bought += t.bought();
        }
        report("the products across the edge are the Trade tab's nine rows (Game.getTradeByGood()), to the bit",
                v.soldAbroad() == sold && v.boughtAbroad() == bought && sold > 0 && f.get(3).colour().equals(OilView.GOOD),
                f.get(3).value() + " · " + f.get(3).line());
    }

    /* ============================ 6. THE RESERVE AND ITS LEVERS ============================ */

    static void theReserve(Game g) {
        out.println("\n--- 6. the reserve and its levers: StrategicReserve's figures; Fill's reach is what the game orders ---");
        StrategicReserve res = g.getReserve();
        OilView.Reserve r = OilView.of(g).reserve();
        report("the reserve is StrategicReserve's: one standing, its room, what it holds and cost, its fill and release",
                r.shown() && r.standing() == 1 && r.room() == StrategicReserve.room(g.getBuildingManager()) && r.held() == res.getTonnes()
                        && r.book() == res.getCost() && r.fill() == res.getFill() && r.release() == res.getRelease(),
                String.format("%,.0f t of %,.0f t, book %s", r.held(), r.room(), OilView.money(r.book())));
        GoodsMarket crude = g.getMarkets().get(Good.CRUDE);
        double room = StrategicReserve.room(g.getBuildingManager()) - res.getTonnes(), afford = g.discretionaryRoom() / crude.importPrice();
        report("Fill's reach is the room its tanks have left or what the treasury could pay at crude's import price, the less",
                r.fillMost() == Math.min(room, afford) && r.fillMost() == g.reserveFillMost() && r.importPrice() == crude.importPrice(),
                String.format("%,.0f t: room %,.0f t, the treasury %,.0f t", r.fillMost(), room, afford));
        double most = r.fillMost();
        double cut = quietlyGet(() -> g.fillReserve(2 * most));
        report("...and an order past it is cut to it, as the lever's Apply orders it (Game.fillReserve())",
                cut == most && res.getFill() == most, String.format("%,.3f t ordered", cut));
        OilView.Reserve ordered = OilView.of(g).reserve();
        List<OilView.Effect> e = OilView.fillEffects(ordered, most / 2);
        report("what a fill would do: the crude held and the room left after it, its cost at today's import price - from the order"
                        + " standing to the dial's",
                e.get(0).before() == ordered.held() + ordered.fill() && e.get(0).after() == ordered.held() + most / 2
                        && e.get(1).after() == ordered.roomLeft() - most / 2 && e.get(2).after() == most / 2 * ordered.importPrice()
                        && OilView.fillEffects(ordered, 3 * most).get(0).after() == ordered.held() + ordered.fillMost(),
                String.format("held %,.0f -> %,.0f t", e.get(0).before(), e.get(0).after()));
        assertTrue("...the lever's step a hundredth of its reach", OilView.step(most) == most / OilView.LADDER_STEPS && OilView.step(0) == 0);
        double held = res.getTonnes();
        quietly(() -> g.simulateMonths(1));
        double came = res.getBoughtHomeTonnes() + res.getBoughtAbroadTonnes();
        OilView.Reserve after = OilView.of(g).reserve();
        report("a month on, the crude held is what it held and what the clearing bought it, and the page says so",
                came > 0 && near(res.getTonnes(), held + came) && after.held() == res.getTonnes() && after.bought() == came,
                String.format("%,.0f t + %,.0f t bought", held, came));
        double release = 1_000;
        quietly(() -> g.releaseReserve(release));
        OilView.Reserve rel = OilView.of(g).reserve();
        List<OilView.Effect> re = OilView.releaseEffects(rel, 2 * release);
        report("Release is set as the lever applies it; its reach what is held; what it would do a month's release from the held",
                rel.release() == release && rel.releaseMost() == rel.held() && re.get(0).before() == release
                        && re.get(0).after() == 2 * release && re.get(1).after() == rel.held() - 2 * release
                        && re.get(2).after() == 2 * release * rel.localPrice(),
                OilView.releaseReading(rel) + "; " + OilView.releaseStatus(rel));
        quietly(() -> g.releaseReserve(0));

        Game bare = bareTown();
        OilView.Reserve none = OilView.of(bare).reserve();
        assertTrue("with no Strategic Reserve standing and none held, none is shown, and Fill says why it can take nothing",
                !none.shown() && none.fillMost() == 0 && OilView.fillStatus(none).equals("no Strategic Reserve stands to fill"));
    }

    static <T> T quietlyGet(java.util.function.Supplier<T> s) {
        Object[] r = new Object[1];
        quietly(() -> r[0] = s.get());
        @SuppressWarnings("unchecked") T t = (T) r[0];
        return t;
    }

    static Game bare;

    /** A city founded and played a month: no well, no refinery, no reserve. */
    static Game bareTown() {
        if (bare != null) return bare;
        GameFiles files = GameFiles.scratch("oilviewcheck-bare");
        Game g = new Game(files);
        RefineryViewCheck.FILES.put(g, files);
        quietly(() -> {
            g.newGame();
            g.simulateMonths(1);
        });
        return bare = g;
    }

    /* ============================ 7. THE CHART ============================ */

    static void theChart(OilView.View v) {
        out.println("\n--- 7. the chart to scale ---");
        RefineryView.Picture p = v.chart(OilView.CHART_W, OilView.CHART_HEIGHT);
        int n = 1 + OilView.FORECAST_YEARS;
        double bottom = OilView.CHART_HEIGHT - OilView.YEARS_H;
        boolean foots = true, inside = true, opacity = true;
        String worst = "";
        double worstGap = 0;
        for (int i = 0; i < n; i++) {
            RefineryView.Place bar = p.place("bar:" + i);
            double want = 0;
            for (OilView.Series s : v.series()) {
                double t = i == 0 ? s.now() : s.ahead()[i - 1];
                want += OilView.barrelsADay(t) * p.scale();
            }
            double drawn = 0;
            for (RefineryView.Box b : p.boxes()) {
                if (Math.abs(b.x() - bar.x()) > 1e-9) continue;
                drawn += b.h();
                inside &= b.x() >= 0 && b.x() + b.w() <= p.width() + 1e-9 && b.y() >= OilView.CHART_TOP - 1e-9 && b.y() + b.h() <= bottom + 1e-9;
                opacity &= b.opacity() == (i == 0 ? OilView.NOW_OPACITY : OilView.AHEAD_OPACITY);
            }
            foots &= Math.abs(drawn - want) <= 1e-6 && Math.abs(bar.h() - want) <= 1e-6;
            if (Math.abs(drawn - want) >= worstGap) {
                worstGap = Math.abs(drawn - want);
                worst = String.format("bar %d: %.6f px of %.6f", i, drawn, want);
            }
        }
        report("each year's bar foots to its series' barrels a day times the scale, a box a series", foots && p.places().size() == n, worst);
        assertTrue("...every box inside the chart, the month solid and the years ahead pale", inside && opacity);
        List<String> years = new ArrayList<>();
        for (RefineryView.Words w : p.words()) if (w.align() == RefineryView.Align.MIDDLE && w.text().matches("\\d+")) years.add(w.text());
        List<String> want = new ArrayList<>();
        for (int i = 0; i < n; i += OilView.YEAR_EVERY) want.add(String.valueOf(CityCalendar.yearOf(v.month() + i * 12)));
        report("the years under every fifth bar, from this month's", years.equals(want), years.toString());
        boolean now = false;
        for (RefineryView.Words w : p.words()) now |= w.text().equals("now · " + OilView.barrelsWords(v.lifted()));
        assertTrue("...and the now line says the month's lift", now);
        OilView.View none = OilView.of(bareTown());
        RefineryView.Picture empty = none.chart(OilView.CHART_W, OilView.CHART_HEIGHT);
        boolean says = false;
        for (RefineryView.Words w : empty.words()) says |= w.text().equals(OilView.NOTHING_LIFTED);
        assertTrue("with no well, no bar is drawn and the chart says nothing would lift", empty.boxes().isEmpty() && says);
    }

    /* ============================ 8. NOT COUNTED, AND PURE ============================ */

    static void notCountedAndPure(Game g) throws Exception {
        out.println("\n--- 8. not counted, and pure ---");
        quietly(() -> g.saveGame(10, "oilview"));
        Game a = new Game(RefineryViewCheck.FILES.get(g)), b = new Game(RefineryViewCheck.FILES.get(g));
        quietly(() -> {
            a.loadGameSave(10);
            b.loadGameSave(10);
        });
        OilView.View loaded = OilView.of(a), was = OilView.of(g);
        boolean month = Double.isNaN(loaded.lifted()) && Double.isNaN(loaded.crudeImported());
        for (OilView.Product p : loaded.products()) month &= Double.isNaN(p.made()) && Double.isNaN(p.used());
        List<OilView.Figure> f = OilView.figures(loaded);
        report("a city just loaded counts no month: the lift, the crude bought, every product's month - and the page says so",
                !loaded.counted() && month && f.get(0).value().equals("—") && f.get(0).line().startsWith(OilView.NOT_COUNTED)
                        && OilView.productLines(loaded).get(1).where().equals(OilView.NOT_COUNTED),
                f.get(0).line());
        report("...while its wells, pools, units standing and reserve read as they stood",
                loaded.landWells() == was.landWells() && loaded.groundLeft() == was.groundLeft() && loaded.seaLeft() == was.seaLeft()
                        && loaded.units().get(0).standing() == was.units().get(0).standing() && loaded.reserve().held() == was.reserve().held(),
                loaded.landWells() + " land wells, " + OilView.megaTonnes(loaded.groundLeft()));
        quietly(() -> {
            a.getBusinessInvestment().holdSector(Sectors.REFINING);
            b.getBusinessInvestment().holdSector(Sectors.REFINING);
        });
        String[] read = new String[1];
        for (int m = 0; m < 2; m++) {
            quietly(() -> {
                a.simulateMonths(1);
                b.simulateMonths(1);
            });
            read[0] = words(OilView.of(a));
            OilView.of(a).chart(OilView.CHART_W, OilView.CHART_HEIGHT);
        }
        OilView.View av = OilView.of(a);
        assertTrue("a month on it counts", av.counted() && Double.isFinite(av.lifted()));
        assertTrue("read twice, the same page, figure for figure and word for word", words(av).equals(words(OilView.of(a))));
        report("...and a twin that never read it ends its months the same: the treasury, the wells' crude, the refiners' products",
                a.getCash() == b.getCash() && a.getSectors().oil().output(Good.CRUDE).produced == b.getSectors().oil().output(Good.CRUDE).produced
                        && words(av).equals(words(OilView.of(b))),
                String.format("cash %s and %s", OilView.money(a.getCash()), OilView.money(b.getCash())));
        Sector homes = a.getSectors().realEstate();
        boolean before = homes.inputRow(Good.PETROL) == null && homes.outputRow(Good.DIESEL) == null;
        OilView.of(a);
        assertTrue("...and reading it makes no production row (asked of a sector that buys no product)", before
                && homes.inputRow(Good.PETROL) == null && homes.outputRow(Good.DIESEL) == null);
    }

    /** A page as text: every figure and every word it writes. */
    static String words(OilView.View v) {
        StringBuilder s = new StringBuilder();
        for (OilView.Figure f : OilView.figures(v)) s.append(f).append('|');
        for (OilView.Fact f : OilView.landFacts(v)) s.append(f).append('|');
        for (OilView.PlatformLine p : OilView.platformLines(v)) s.append(p).append('|');
        for (OilView.Fact f : OilView.seaFacts(v)) s.append(f).append('|');
        for (OilView.Series x : v.series()) s.append(x.name()).append(x.now()).append(java.util.Arrays.toString(x.ahead()));
        for (OilView.UnitLine u : OilView.unitLines(v)) s.append(u).append('|');
        for (OilView.ProductLine p : OilView.productLines(v)) s.append(p).append('|');
        for (OilView.Fact f : OilView.reserveFacts(v)) s.append(f).append('|');
        for (RefineryView.Words w : v.chart(OilView.CHART_W, OilView.CHART_HEIGHT).words()) s.append(w.line()).append(w.x()).append(w.y());
        return s.toString();
    }
}
