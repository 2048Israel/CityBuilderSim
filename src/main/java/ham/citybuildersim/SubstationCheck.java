package ham.citybuildersim;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Map;

/**
 * Substations at both sides of their carry, the order that keeps the peak covered, and their costs in the bills (PW5).
 * WHY. A flat wires charge and unlimited wires passed PW1's price checks. These fixtures change real city stock,
 * the dispatch's demand input, and automation's demand input; each rule also fails against the pre-PW5 model.
 */
public final class SubstationCheck {
    static int fails;
    static final double CARRY = Substations.SUBSTATION_MW * 1000;
    static final Gson JSON = new Gson();
    static final PrintStream OUT = System.out, QUIET = new PrintStream(OutputStream.nullOutputStream());

    static void check(String label, boolean ok) { OUT.println((ok ? "  OK   " : "  FAIL ") + label); if (!ok) fails++; }
    static boolean near(double a, double b) { return Math.abs(a - b) <= 1e-9 * Math.max(1, Math.max(Math.abs(a), Math.abs(b))); }
    static void quietly(Runnable work) { System.setOut(QUIET); try { work.run(); } finally { System.setOut(OUT); } }

    /** Physical wires for fixtures isolating generation, prices or fitting rather than grid scarcity. */
    static void wiresFor(Game g, double peakKw) {
        if (!Double.isFinite(peakKw) || peakKw < 0) throw new IllegalArgumentException("fixture peak " + peakKw);
        BuildingManager b = g.getBuildingManager();
        long wanted = Math.max(1, (long) Math.ceil(peakKw / CARRY));
        long standing = b.getQuantity(Substations.ID);
        if (wanted > standing) b.addStack(b.getTemplate(Substations.ID), wanted - standing, true);
    }

    /** Enough wires for every standing generator's nameplate, including the founding grid. */
    static void generationWires(Game g) {
        BuildingManager b = g.getBuildingManager();
        double kw = g.getServicesManager().getUtilitiesHandler().getFoundingGridKw();
        for (BuildingsTemplate t : b.getTemplates())
            if (t.getCategory() == BuildingType.ELECTRICITY) kw += b.getQuantity(t.getId()) * t.getProduction1();
        wiresFor(g, kw);
    }

    /** Districts for the standing draw before steam/electrolyser savings, at its highest season; unused generation needs no wires. */
    static void peakWires(Game g) {
        double draw = g.getBuildingManager().getTotalDouble(BuildingsTemplate::powerBought);
        double peak = Substations.peak(draw, Arrays.stream(PowerDispatch.SEASON).max().orElseThrow());
        if (peak > g.getServicesManager().getUtilitiesHandler().getFoundingGridKw()) wiresFor(g, peak);
    }

    static Game city(String label, int substations) {
        Game g = new Game(GameFiles.scratch("substation-" + label));
        quietly(() -> {
            g.newGame();
            g.setCashForTest(1_000_000_000 * PriceLevel.FACTOR);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 400_000_000L);
            BuildingsTemplate t = g.getBuildingManager().getTemplateByName("Substation");
            if (t != null && substations > 0 && g.buildStack(t, substations, true) != Game.BuildResult.SUCCESS)
                throw new AssertionError("fixture substation did not stand");
            g.getServicesManager().updateServices();
        });
        return g;
    }

    /** A fully staffed coal row removes generation scarcity; the city stock still supplies the physical wires. */
    static UtilitiesHandler dispatch(Game g, double peak) {
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        BuildingsTemplate coal = g.getBuildingManager().getTemplateByName("Coal Power Plant");
        u.setPlants(Map.of(coal, 10L));
        double[] filled = new double[JobType.values().length]; Arrays.fill(filled, 1);
        u.updateJobFillRate(filled);
        u.updateUtilitiyWages(new double[filled.length], new long[filled.length], new long[filled.length]);
        u.setCalendarMonth(() -> 0);
        u.setWorksPower(0);
        u.setWattsConsumption(peak / PowerDispatch.PLAN_PEAK);
        u.updateEnergyRatio();
        u.strikePower();
        return u;
    }

    static double generated(UtilitiesHandler u, int half) {
        double sum = 0; for (PowerDispatch.Row r : u.getLatestRows()) sum += r.gen[half]; return sum;
    }

    static void carry() {
        OUT.println("--- the carry's two edges and the shared shortfall ---");
        Game g = city("carry", 1);
        UtilitiesHandler u = dispatch(g, CARRY * .99);
        boolean under = u.getEnergyRatio() == 1 && u.getHalfBlocksShort() == 0;
        dispatch(g, CARRY * 1.01);
        boolean over = u.getEnergyRatio() < 1 && u.getHalfBlocksShort() == 2
                && near(generated(u, 4), CARRY) && near(generated(u, 5), CARRY);
        check("a peak below one substation is served; above it only the two peak halves brown out at its carry", under && over);
        boolean conserved = true;
        for (int s = 0; s < PowerDispatch.HALVES; s++) {
            PowerDispatch.Month m = u.getLatestMonth();
            conserved &= near(generated(u, s) + m.unserved[s], m.load[s]) && generated(u, s) <= CARRY + 1e-7;
            if (m.unserved[s] > 0) conserved &= near(m.price[s], PowerDispatch.VOLL_A_KWH);
        }
        check("a short grid conserves each half's energy, stops generation before burning fuel, and reports the cap price", conserved && over);
        Game empty = city("starter", 0);
        UtilitiesHandler without = dispatch(empty, 2 * CARRY);
        double starter = without.getLatestRows().stream().filter(r -> "grid".equals(r.name)).mapToDouble(r -> r.kw).sum();
        check("a city without substations carries only its existing free 10 MW starter grid", without.getEnergyRatio() < 1
                && near(generated(without, 4), starter));
    }

    static JsonObject costs(UtilitiesHandler.Book book) {
        JsonObject json = JSON.toJsonTree(book).getAsJsonObject();
        return json.has("gridCosts") ? json.getAsJsonObject("gridCosts") : new JsonObject();
    }
    static double number(JsonObject o, String key) { return o.has(key) ? o.get(key).getAsDouble() : 0; }

    static void tariff() {
        OUT.println("--- the substations' own costs, struck once ---");
        Game g = city("costs", 1);
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName("Substation");
        // ASH1 finding 9: the grid unit's posts are offered at the share of the carry the last dispatch used; the founding town used a thousandth of it, so
        // the load is put on the wires first, and the dispatch below captures its costs at that share.
        dispatch(g, CARRY * .9);
        double used = g.getServicesManager().getUtilitiesHandler().getWiresUsed();
        UtilitiesHandler u = dispatch(g, CARRY * .9);
        UtilitiesHandler.Book book = u.getStruckBook();
        JsonObject costs = costs(book);
        double value = t == null ? 0 : g.getEconomyManager().withBuildersTax(g.getBuildingManager().structureCost(t));
        // The card's crew is every post; the grid unit's payroll is struck on the posts offered at that share (the model's rounding, postsOffered), each at the wage and fill.
        double fullPayroll = t == null ? 0 : BuildCard.runningCost(g, t) - t.getUpkeep();
        double payroll = 0;
        if (t != null) {
            double[] wage = g.getPopulationManager().getWagesPerType(), fill = g.getPopulationManager().getJobFillRate();
            for (JobType j : JobType.values())
                payroll += BuildingManager.postsOffered(g.getBuildingManager().getQuantity(Substations.ID) * t.getJobs(j), used) * wage[j.ordinal()] * fill[j.ordinal()];
        }
        OUT.printf("   the wires used %.6f of the carry; crew paid %.6f of the card's %.6f; the book's payroll %.6f%n", used, payroll, fullPayroll, number(costs, "payroll"));
        check("a town drawing 90% of the Substation's carry pays its crew on the posts offered at that share, fewer than the card's every post",
                used > .8 && used < 1 && payroll > 0 && payroll < fullPayroll && near(number(costs, "payroll"), payroll));
        // Independent worked study figure: $440.004M at 4% over 40 years costs $1,852,544.78305859 a month.
        // Pin the coefficient rather than repeating production's annuity expression or reading its rate/life dials.
        double capital = value / (440004 * PriceLevel.FACTOR) * (1852.5447830585897 * PriceLevel.FACTOR);
        double repairs = t == null ? 0 : value * t.repairsPerYear() / 12;
        double wires = (capital + repairs + payroll) / (book.servedKwh * (1 - PowerDispatch.GRID_LOSSES));
        check("the bill's wires recover this substation's capital, repairs and filled posts over twelve months' delivered kWh",
                value > 0 && near(number(costs, "capital"), capital) && near(number(costs, "repairs"), repairs)
                && near(number(costs, "payroll"), payroll)
                && near(book.commercePrice, (book.energyPrice + wires) * PowerDispatch.MONTH_HOURS)
                && near(book.industryPrice, (book.energyPrice + wires * Substations.TRANSMISSION_SHARE) * PowerDispatch.MONTH_HOURS));
        double before = book.commercePrice;
        if (t != null) t.setCashCost(2 * t.getCashCost());
        dispatch(g, CARRY * .9);
        check("changing the substation's cost changes the next bill; the previously struck book keeps its price",
                u.getStruckBook().commercePrice > before && book.commercePrice == before);
    }

    static void reform() {
        OUT.println("--- currency reform preserves the wires' breakdown ---");
        Game g = city("reform", 1);
        UtilitiesHandler u = dispatch(g, CARRY * .9);
        UtilitiesHandler.Book before = u.getStruckBook().copy();
        var pending = u.toPowerState();
        u.redenominate(.5);
        UtilitiesHandler.Book after = u.getStruckBook();
        var state = u.toPowerState();
        boolean once = before.transmissionPrice > 0 && before.distributionPrice > 0
                && near(after.transmissionPrice, before.transmissionPrice * .5)
                && near(after.distributionPrice, before.distributionPrice * .5)
                && near(after.commercePrice, before.commercePrice * .5) && near(after.industryPrice, before.industryPrice * .5)
                && near(after.gridCosts.total(), before.gridCosts.total() * .5)
                && near(state.pendingGridCosts.total(), pending.pendingGridCosts.total() * .5)
                && after.servedKwh == before.servedKwh && after.gridCosts.carryKw() == before.gridCosts.carryKw();
        UtilitiesHandler back = new UtilitiesHandler(); back.restorePowerState(state); back.restorePendingPower();
        check("currency reform scales saved transmission, distribution, costs and tariffs exactly once, keeping physical energy and carry",
                once && near(back.getStruckBook().transmissionPrice, before.transmissionPrice * .5)
                        && near(back.getStruckBook().distributionPrice, before.distributionPrice * .5));
    }

    static void workedTariff() {
        OUT.println("--- independent worked study tariff ---");
        Game g = city("worked-study", 1);
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate t = bm.getTemplateByName("Substation");
        g.getEconomyManager().getTaxPolicy().setSalesTaxRate(0);
        bm.setBuildersWages(null); bm.setExchangeRate(1);
        // An input fixture at the study's $440.004M: no sales tax, no wage drift, no filled posts.
        // The ordinary real-city tariff checks above and PowerPriceCheck exercise filled payroll separately.
        t.setCashCost(264000 * PriceLevel.FACTOR);
        for (JobType job : JobType.values()) t.setJobs(job, 0);
        UtilitiesHandler u = dispatch(g, 90_000);
        UtilitiesHandler.Book b = u.getStruckBook();
        JsonObject costs = costs(b);
        check("the study's $440.004M grid recovers $1,852,544.78305859 capital and $366,670 repairs a month",
                near(number(costs, "capital") / PriceLevel.FACTOR, 1852.5447830585897)
                        && near(number(costs, "repairs") / PriceLevel.FACTOR, 366.67) && number(costs, "payroll") == 0);
        // 90 MW peak / 1.20 x 720 h x .95 = 51.3M delivered kWh. The independently worked
        // wires bills are $31.1468741481907 and $9.88748025755129 a kW-month (1.885/5.938 transmission).
        double commerce = (b.commercePrice - b.energyPrice * 720) / PriceLevel.FACTOR;
        double industry = (b.industryPrice - b.energyPrice * 720) / PriceLevel.FACTOR;
        check("51.3M delivered kWh bills the worked $31.1468741481907 commerce and $9.88748025755129 industry wires per kW-month",
                near(b.servedKwh * .95, 51_300_000) && near(commerce, .031146874148190733)
                        && near(industry, .009887480257551285));
    }

    /** What one fixture's passes ordered: the standing grid's supply and peak, then the sites of each unit before the edge, past it, and after a repeated pass. */
    record Ordered(double supply, double peak, long districtBefore, long districtAfter, long districtRepeated,
                   long smallBefore, long smallAfter, long smallRepeated, boolean placed, String building) { }

    /** A grid at its edge (.99 of one district's carry asked, nothing ordered), then `over` of it asked: what the automatic builder orders, and that a repeat orders no more. */
    static Ordered orderAt(Game g, BuildAdvice.Measure grid, double over) {
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        AutoBuilder a = g.getAutoBuilder(); a.setSlack(0, null); a.setOn(true, null);
        double season = PowerDispatch.season(CityCalendar.monthOfYear(g.getMonth()));
        u.setWattsConsumption(.99 * CARRY / (PowerDispatch.PLAN_PEAK * season));
        quietly(() -> a.pass(g));
        BuildingManager bm = g.getBuildingManager();
        long districtBefore = bm.getUnderConstructionById()[Substations.ID], smallBefore = bm.getUnderConstructionById()[Substations.DISTRIBUTION_ID];
        u.setWattsConsumption(over * CARRY / (PowerDispatch.PLAN_PEAK * season));
        double[] standing = BuildAdvice.supplyDemand(g, grid, Map.of());
        quietly(() -> a.pass(g));
        long district = bm.getUnderConstructionById()[Substations.ID], small = bm.getUnderConstructionById()[Substations.DISTRIBUTION_ID];
        var placed = a.steps().stream().filter(s -> s.measure().equals(grid) && s.ordered() > 0).findFirst();
        quietly(() -> a.pass(g));
        return new Ordered(standing[0], standing[1], districtBefore, district, bm.getUnderConstructionById()[Substations.ID],
                smallBefore, small, bm.getUnderConstructionById()[Substations.DISTRIBUTION_ID], placed.isPresent(),
                placed.map(AutoBuilder.Step::building).orElse(""));
    }

    static void order() {
        OUT.println("--- the same order machinery as the roads ---");
        BuildAdvice.Measure grid;
        try { grid = BuildAdvice.Measure.of(BuildAdvice.Kind.valueOf("GRID")); }
        catch (IllegalArgumentException absent) {
            check("peak growth orders the least distribution substations that cover it, counts its site once, and does not order below the edge", false);
            check("a catalogue without the distribution substation orders the 100 MW Substation at the edge, counts its site once, and does not order below it", false);
            return;
        }
        // ASH1: with both units known the order is the grid size the load needs - the least 5 MW units that cover the peak, never a 100 MW unit.
        double unit = Substations.DISTRIBUTION_MW * 1000;
        for (double over : new double[] {1.01, 1.12}) {
            Ordered o = orderAt(city("order-" + over, 1), grid, over);
            long wanted = (long) Math.ceil((o.peak() - o.supply()) / unit);
            OUT.printf("   past the edge by %.0f%%: peak %.0f kW over %.0f kW carried; distribution %d/%d/%d wanted %d, districts %d/%d/%d%n", (over - 1) * 100,
                    o.peak(), o.supply(), o.smallBefore(), o.smallAfter(), o.smallRepeated(), wanted, o.districtBefore(), o.districtAfter(), o.districtRepeated());
            check(String.format("peak growth %.0f%% past the edge orders the least distribution substations that cover the peak (%d of %.0f kW), counts its site once, and does not order below the edge",
                            (over - 1) * 100, wanted, unit),
                    o.smallBefore() == 0 && wanted >= 1 && o.smallAfter() == wanted && o.smallRepeated() == o.smallAfter()
                            && o.placed() && "Distribution Substation".equals(o.building()));
            check(String.format("...and no 100 MW Substation is ordered for it (%.0f%% past the edge)", (over - 1) * 100),
                    o.districtBefore() == 0 && o.districtAfter() == 0 && o.districtRepeated() == 0);
        }
        // PW5's law, where the small unit is not on the menu: the Substation is the only step, ordered once at the edge.
        Game g = city("order-district", 1);
        BuildingManager bm = g.getBuildingManager();
        bm.setTemplateKnown(t -> t.getId() != Substations.DISTRIBUTION_ID && t.offeredAt(g.getMonth()) && !ResearchCatalog.nearFuture(t.getSinceText()));
        bm.setTemplateKnownIn((t, months) -> t.getId() != Substations.DISTRIBUTION_ID && t.offeredAt(g.getMonth() + months) && !ResearchCatalog.nearFuture(t.getSinceText()));
        Ordered o = orderAt(g, grid, 1.01);
        OUT.printf("   sites below/above/repeated: %d/%d/%d (distribution %d)%n", o.districtBefore(), o.districtAfter(), o.districtRepeated(), o.smallAfter());
        check("a catalogue without the distribution substation orders the 100 MW Substation at the edge, counts its site once, and does not order below it",
                o.districtBefore() == 0 && o.districtAfter() == 1 && o.districtRepeated() == o.districtAfter() && o.placed()
                        && "Substation".equals(o.building()) && o.smallAfter() == 0);
    }

    static void saved() throws Exception {
        OUT.println("--- closing costs survive the load rebuild ---");
        Game g = city("save", 1);
        // ASH1 finding 9: the grid units' posts follow the wires' load. A district Substation and two distribution substations stand, and a load of half
        // their carry is put on the wires as the month's closing dispatch, which the save carries as the half-blocks and the carry they ran against.
        BuildingManager gb = g.getBuildingManager();
        gb.addStack(gb.getTemplate(Substations.DISTRIBUTION_ID), 2, true);
        quietly(() -> g.simulateMonths(2));
        UtilitiesHandler x = g.getServicesManager().getUtilitiesHandler();
        x.setWattsConsumption(Substations.carryKw(x.getFoundingGridKw(), 1, 2) * .5 / PowerDispatch.PLAN_PEAK);
        // ...dispatched twice: the first puts the load on the wires, and the second captures the grid units' crew at the share it used.
        quietly(x::updateEnergyRatio); quietly(x::updateEnergyRatio);
        String pending = JSON.toJson(x.toPowerState());
        boolean written = g.saveGame(10, "PW5 closing grid").ok;
        Game back = new Game(g.getGameFiles()); quietly(() -> back.loadGameSave(10));
        String restored = JSON.toJson(back.getServicesManager().getUtilitiesHandler().toPowerState());
        check("DataSave retains both the struck cost book and pending costs through the derived-services rebuild", written && pending.equals(restored));
        UtilitiesHandler y = back.getServicesManager().getUtilitiesHandler();
        BuildingManager bb = back.getBuildingManager();
        double used = x.getWiresUsed();
        boolean sameOffer = true, someWithheld = false, someOffered = false;
        for (JobType j : JobType.values()) {
            long every = gb.getQuantity(Substations.ID) * gb.getTemplate(Substations.ID).getJobs(j)
                    + gb.getQuantity(Substations.DISTRIBUTION_ID) * gb.getTemplate(Substations.DISTRIBUTION_ID).getJobs(j);
            sameOffer &= gb.getGridPostsOffered(j) == bb.getGridPostsOffered(j);
            someWithheld |= gb.getGridPostsOffered(j) < every;
            someOffered |= gb.getGridPostsOffered(j) > 0;
        }
        double crew = x.toPowerState().pendingGridCosts.payroll(), crewLoaded = y.toPowerState().pendingGridCosts.payroll();
        OUT.printf("   the wires used %.6f of the carry (loaded %.6f); the grid units' crew captured %.6f (loaded %.6f)%n", used, y.getWiresUsed(), crew, crewLoaded);
        check("a loaded city's grid units offer the same posts per job type, read the same share of the wires used and keep the same captured crew, to the bit",
                sameOffer && Double.doubleToRawLongBits(used) == Double.doubleToRawLongBits(y.getWiresUsed())
                && Double.doubleToRawLongBits(crew) == Double.doubleToRawLongBits(crewLoaded));
        check("...of wires partly used: a share of the carry between none and all, some of the grid units' posts withheld and some offered",
                used > 0 && used < 1 && someWithheld && someOffered && crew > 0);
        quietly(() -> { g.simulateMonths(1); back.simulateMonths(1); });
        check("the first post-load strike bills exactly the same grid costs as the uninterrupted city", JSON.toJson(x.getStruckBook()).equals(JSON.toJson(y.getStruckBook())));
        check("...and the crew Substations.costs() pays the month after the load is the uninterrupted city's, to the bit, at the same share of the wires used",                Double.doubleToRawLongBits(Substations.costs(g).payroll()) == Double.doubleToRawLongBits(Substations.costs(back).payroll())                && Double.doubleToRawLongBits(x.getWiresUsed()) == Double.doubleToRawLongBits(y.getWiresUsed()));
        check("a native format-60 city and history are refused without changing either file or a running twin", SaveFileCheck.nativeCard60Refusal());
        check("a native format-61 city and history are refused without changing either file or a running twin", SaveFileCheck.nativeGrid61Refusal(false));
        check("a native shipped format-62 city and history are refused without changing either file or a running twin", SaveFileCheck.nativeGrid62Refusal(false));
    }

    /**
     * ASH1 finding 9: the grid units' raw posts are held for a pass with the sectors' (BuildingManager.HeldPosts.grid), keyed on the same
     * count of edits, so a stack that gains a Substation inside a scope is offered at the share at once, as the bare reading offers it.
     * The fixture puts a load on the wires, reads the posts held, adds a unit through the stack, and reads them again.
     */
    static void heldGridPosts() {
        OUT.println("--- the grid units' posts held for a pass follow the stack ---");
        Game g = city("held-grid", 1);
        BuildingManager bm = g.getBuildingManager();
        UtilitiesHandler u = dispatch(g, CARRY * .5);
        BuildingsStacks stack = bm.getStack(bm.getTemplate(Substations.ID));
        long before, after, bare, withheld, bareWithheld;
        try (WorksValue.Scope scope = WorksValue.hold(g)) {
            before = bm.getGridPostsOffered(JobType.DIPLOMA);
            bm.getPostsWithheld(JobType.DIPLOMA);
            stack.addQuantity(1);
            after = bm.getGridPostsOffered(JobType.DIPLOMA);
            withheld = bm.getPostsWithheld(JobType.DIPLOMA);
            bare = WorksValue.bareWhile(() -> bm.getGridPostsOffered(JobType.DIPLOMA));
            bareWithheld = WorksValue.bareWhile(() -> bm.getPostsWithheld(JobType.DIPLOMA));
        }
        OUT.printf("   the wires used %.6f; diploma posts offered %d before, %d after a second Substation (bare %d); withheld %d (bare %d)%n",
                u.getWiresUsed(), before, after, bare, withheld, bareWithheld);
        check("a Substation added inside a pass's scope is read at once in the grid's offered posts and its withheld, the same as the bare reading",
                before > 0 && after > before && after == bare && withheld == bareWithheld);
    }

    record WireFinding(long districts, double peak, double cents, double energyCents,
                       double ratio, double dieselBid, double cokingHome,
                       double thermalMargin, double cokingMargin, Substations.Costs costs, boolean literals) { }

    /** Current stock and both real dispatch snapshots, for a fixture that loses its intended condition. */
    static String gridReading(Game g) {
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        UtilitiesHandler.Book b = u.getStruckBook();
        double cents = 1000 * 100 * PowerPriceCheck.unitOf(g) / PriceLevel.FACTOR;
        return String.format("m%d districts=%d carry=%.3f closingPeak=%.3f closingRatio=%.9f billedDistricts=%d billedCarry=%.3f billedPeak=%.3f billedRatio=%.9f wiresCents=%.6f energyCents=%.6f",
                g.getMonth(), g.getBuildingManager().getQuantity(Substations.ID), Substations.costs(g).carryKw(),
                Arrays.stream(u.getLatestMonth().load).max().orElse(0), u.getEnergyRatio(),
                b.gridCosts == null ? -1 : b.gridCosts.count(), b.gridCosts == null ? 0 : b.gridCosts.carryKw(),
                Arrays.stream(b.load).max().orElse(0), b.ratio,
                (b.transmissionPrice + b.distributionPrice) * cents, b.energyPrice * cents);
    }

    static boolean gridCovered(Game g) {
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        UtilitiesHandler.Book b = u.getStruckBook();
        return Substations.costs(g).carryKw() >= Arrays.stream(u.getLatestMonth().load).max().orElse(0)
                && b.gridCosts != null && b.gridCosts.carryKw() >= Arrays.stream(b.load).max().orElse(0);
    }

    /** PW5-F1: whole districts have a utilization cost; omitted wires cause a different, physical squeeze. */
    static void smallTownFinding() {
        OUT.println("--- PW5-F1: low-utilization wires and the honest works fixture ---");
        WireFinding right = townFinding("works-peak-sized", IndustryViewCheck.works().game());
        WireFinding three = townFinding("works-overbuilt", IndustryViewCheck.works(g -> wiresFor(g, 3 * CARRY)).game());
        WireFinding flatThree = townFinding("works-overbuilt-flat", IndustryViewCheck.works(g -> {
            wiresFor(g, 3 * CARRY);
            g.getServicesManager().getUtilitiesHandler().setGridCostSource(null);
        }).game());
        WireFinding main = townFinding("works-main-flat", IndustryViewCheck.works(g -> {
            g.getBuildingManager().retire(g.getBuildingManager().getTemplate(Substations.ID), g.getBuildingManager().getQuantity(Substations.ID));
            g.getServicesManager().getUtilitiesHandler().setGridCostSource(null);
        }).game());
        check("PW5-F1: the ordinary works fixture uses one district and native world quotes, with its original coal chain working",
                right.districts == 1 && right.peak < CARRY && !right.literals && right.ratio == 1
                        && right.dieselBid > 0 && right.cokingHome > 0);
        check("PW5-F1: a whole underused district costs more per kWh than main's flat wires; its capital is not prorated to the load",
                right.cents > main.cents && near(three.costs.capital(), 3 * right.costs.capital())
                        && near(three.peak, right.peak) && three.cents > right.cents);
        check("PW5-F1: three unnecessary districts price the coal mines out of their home market; flat wires restore the same-stock chain",
                three.thermalMargin < 0 && three.cokingMargin < 0 && three.dieselBid == 0 && three.cokingHome == 0
                        && flatThree.thermalMargin > 0 && flatThree.cokingMargin > 0
                        && flatThree.dieselBid > 0 && flatThree.cokingHome > 0);

        double[] tonnes = new double[3];
        WireFinding[] rail = new WireFinding[3];
        String[] kinds = {"omitted", "flat", "actual"};
        for (int i = 0; i < kinds.length; i++) {
            String kind = kinds[i];
            Game g = new Game(GameFiles.scratch("pw5-rail-wire-finding"));
            RailCheck.prepareTradeTown(g, x -> {
                if (!kind.equals("actual")) x.getBuildingManager().retire(x.getBuildingManager().getTemplate(Substations.ID),
                        x.getBuildingManager().getQuantity(Substations.ID));
                if (kind.equals("flat")) x.getServicesManager().getUtilitiesHandler().setGridCostSource(null);
            });
            rail[i] = townFinding("rail-" + kind, g);
            var f = g.getSectors().freight();
            tonnes[i] = f.getTradeTonnes();
            OUT.printf("RAIL %s m%d tonnes=%.6f truckBill=%.6f%n", kind, g.getMonth(), tonnes[i], f.getTruckBill());
            LongPlaytest.cleanUp(g.getGameFiles().getDirectory().getParent());
        }
        check("PW5-F1: RailCheck's m37 squeeze is missing carry and VOLL, not a whole-substation charge; physical wires restore supply and trade",
                rail[0].districts == 0 && rail[0].cents == 0 && rail[0].ratio < 1
                        && rail[0].energyCents > rail[1].energyCents
                        && rail[2].districts == 1 && rail[2].ratio == 1 && tonnes[2] > tonnes[0]);
        for (GameFiles f : IndustryViewCheck.FILES) LongPlaytest.cleanUp(f.getDirectory().getParent());
        IndustryViewCheck.FILES.clear();
    }

    static WireFinding townFinding(String label, Game g) {
        UtilitiesHandler.Book b = g.getServicesManager().getUtilitiesHandler().getStruckBook();
        double flat = (PowerDispatch.TRANSMISSION_A_KWH + PowerDispatch.DISTRIBUTION_A_KWH) / PowerPriceCheck.unitOf(g);
        double dollars = 1000 * PowerPriceCheck.unitOf(g) / PriceLevel.FACTOR;
        double cents = (b.transmissionPrice + b.distributionPrice) * dollars * 100;
        double peak = Arrays.stream(b.load).max().orElse(0), delivered = b.servedKwh * (1 - PowerDispatch.GRID_LOSSES);
        Substations.Costs c = b.gridCosts;
        OUT.printf("WIRES %s m%d pop=%d n=%d peak=%.6f delivered=%.6f cents=%.9f flat=%.9f ratio=%.9f energyCents=%.9f capitalUSD=%.6f repairsUSD=%.6f payrollUSD=%.6f%n",
                label, g.getMonth(), g.getPopulationManager().getPopulation(), g.getBuildingManager().getQuantity(Substations.ID),
                peak, delivered, cents, flat * dollars * 100, b.ratio, b.energyPrice * dollars * 100,
                c == null ? 0 : c.capital() * dollars, c == null ? 0 : c.repairs() * dollars, c == null ? 0 : c.payroll() * dollars);
        var coal = g.getSectors().coalMining();
        double[] local = new double[2];
        Good[] goods = {Good.THERMAL_COAL, Good.COKING_COAL};
        for (int i = 0; i < goods.length; i++) {
            Good good = goods[i];
            double marginal = coal.getMarginalCostPerUnit(good);
            local[i] = g.getMarkets().get(good).getLocalPrice() - marginal;
            OUT.printf("MARGIN %s %s costUSD=%.6f localUSD=%.6f exportUSD=%.6f homeMarginUSD=%.6f exportMarginUSD=%.6f%n", label, good,
                    marginal * dollars, g.getMarkets().get(good).getLocalPrice() * dollars,
                    g.getMarkets().get(good).netExportPrice() * dollars, local[i] * dollars,
                    (g.getMarkets().get(good).netExportPrice() - marginal) * dollars);
        }
        var cm = IndustryView.of(g, coal);
        var diesel = cm.feed(Good.DIESEL);
        var heavy = IndustryView.of(g, g.getSectors().heavyIndustry());
        var coking = heavy.feed(Good.COKING_COAL);
        var coke = heavy.product(Good.MET_COKE);
        double bid = coal.inputRow(Good.DIESEL).bid, home = coking == null ? 0 : coking.home();
        OUT.printf("CHAIN %s dieselNeed=%.6f dieselBought=%.6f dieselBid=%.6f cokingHome=%.6f cokeMade=%.6f cokeCaptive=%.6f%n", label,
                diesel == null ? 0 : diesel.needed(), diesel == null ? 0 : diesel.received(), bid, home,
                coke == null ? 0 : coke.made(), coke == null ? 0 : coke.captive());
        return new WireFinding(g.getBuildingManager().getQuantity(Substations.ID), peak, cents, b.energyPrice * dollars * 100,
                b.ratio, bid, home, local[0], local[1], c, g.isOnLiterals());
    }

    static void historicalTownFinding() {
        RecipeCheck.out = FittingCheck.out = OUT;
        RecipeCheck.quiet = FittingCheck.quiet = QUIET;
        Game ordinary = HistoricalLicenceCheck.town("pw5-historical-free", true);
        UtilitiesHandler u = ordinary.getServicesManager().getUtilitiesHandler();
        double peak = Substations.peak(ordinary.getBuildingManager().getTotalDouble(BuildingsTemplate::powerBought),
                Arrays.stream(PowerDispatch.SEASON).max().orElseThrow());
        double[] grid = BuildAdvice.supplyDemand(ordinary, BuildAdvice.Measure.of(BuildAdvice.Kind.GRID), Map.of(), BuildAdvice.NOW);
        OUT.printf("HISTORICAL ordinary grossSeasonalPeak=%.6f foundingCarry=%.6f adviceCarry=%.6f advicePeak=%.6f %s%n",
                peak, u.getFoundingGridKw(), grid[0], grid[1], gridReading(ordinary));
        check("the shared historical town's measured peak fits its free founding wires and GRID needs no district",
                peak <= u.getFoundingGridKw() && grid[0] >= grid[1]
                        && ordinary.getBuildingManager().getQuantity(Substations.ID) == 0
                        && u.getStruckBook().energyPrice == 0 && u.getStruckBook().transmissionPrice == 0
                        && u.getStruckBook().distributionPrice == 0);
        Game priced = HistoricalLicenceCheck.pricedTown("pw5-historical-priced", true);
        UtilitiesHandler p = priced.getServicesManager().getUtilitiesHandler();
        double extra = priced.getMaintenancePoints() - ordinary.getMaintenancePoints();
        OUT.printf("HISTORICAL priced extraRepairPoints=%.6f %s%n", extra, gridReading(priced));
        check("PW5-F1: the explicitly priced district adds the study's whole 110 repair points and a wires price to a town below its carry",
                priced.getBuildingManager().getQuantity(Substations.ID) == 1
                        && p.getStruckBook().transmissionPrice + p.getStruckBook().distributionPrice > 0
                        && near(extra, 110));
    }

    static void bondTownWires() {
        Game g = LongPlaytest.MoneyFixtures.foundingCity(GameFiles.scratch("substation-bond-town"));
        OUT.println("BOND_TOWN " + gridReading(g));
        check("the money audit's hand-built bond town carries its billed and closing peaks after its full warmup", gridCovered(g));
    }

    public static void main(String[] args) throws Exception {
        carry(); tariff(); workedTariff(); order(); reform(); smallTownFinding();
        historicalTownFinding();
        bondTownWires();
        heldGridPosts();
        if (args.length == 0) saved();
        OUT.println("SubstationCheck: " + fails + " failures");
        if (fails > 0) System.exit(1);
    }
}
