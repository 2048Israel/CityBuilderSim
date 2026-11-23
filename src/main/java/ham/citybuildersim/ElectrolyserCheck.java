package ham.citybuildersim;

import ham.citybuildersim.sectors.HeavyIndustry;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Electrolysis at 46 kWh a kilogram (R6d, FE10: the US energy department's target for a PEM system, 55 today; projected 2034, window 2031-2037): the Hydrogen
 * Steel Works' piece of equipment, the 46 kWh electrolyser, and what a works that has it draws from the grid.
 *
 * WHY. The row changes one figure of FE4's works, the grid's power a tonne of steel, and nothing else, so everything it can get wrong is a number: which
 * works carry the piece (EquipmentForms counts them from the stacks), how much each saves (the chemistry's 54 kg of hydrogen a tonne of iron at 9 kWh less
 * each), and where that saving is taken off (HeavyIndustry's draw, which EconomyManager sets each month from the templates and which has to keep the saving
 * across that setting). It must also change nothing where the piece does not exist: a city founded today's way never knows a near-future row, and a 1900
 * city before the row's date has no works with it. Each figure is held to the model's own constants (HeavyIndustry.HYDROGEN_KG_A_TONNE,
 * ELECTROLYSIS_KWH_A_KG and ELECTROLYSIS_KWH_A_KG_FE10, HydroSites.KWH_PER_UNIT, LicenceFees.SHARE), never a number typed here, and each condition is
 * caused from both sides: the month before and the month of the row's date, a works with the piece and a works without it, power dear and power cheap, a
 * city founded the 1900 way and one founded today's.
 *
 * What this has to prove:
 *
 *   1. THE ROW AND ITS DIALS: the works carries the "electrolyser" piece, since FE10 and ending ELECTROLYSERS; the saving a tonne is the chemistry's kilograms
 *      at the nine kilowatt-hours the target takes off each, 0.486 MWh, less than the works' whole 3.48; what a works saves in the game's units is its steel a
 *      month at that saving; the template's own draw is HYDROGEN_MWH_A_TONNE's, unchanged by the piece
 *   2. THE ENTRY: FE10 is a projected engineering equipment row, its date inside its window and after history's end, needing FE4 (which is dated before it),
 *      a near-future row, patented; the piece is known from its month and not before, a month inside the window
 *   3. KNOWN IN ITS WINDOW AND NOT BEFORE: in a 1900 city with works standing, the month before FE10 the piece is NOT_KNOWN and no works carries it; at
 *      FE10's month it is known and priced; a city whose research found FE10 sooner knows it from the month it found it
 *   4. ITS kWh READ BY FE4'S WORKS: with some of the works fitted, Heavy Industry's draw is the works' draws less the saving of each fitted works, the
 *      same after EconomyManager sets the draw again, and after months are played; a works fitted draws HYDROGEN_MWH_A_TONNE less the saving a tonne of its
 *      steel; none fitted, the buildings' own figure to the bit; a count that comes back takes the saving off again
 *   5. THE SAVING AGAINST THE MODEL'S OWN CONSTANTS: the piece's gross gain is the units it saves at the unit's price; its verdict is PAYS exactly where
 *      the gain earns the cost back over the piece's life and carries the interest hurdle, the break-even tariff read off the verdict's own cost, factor and
 *      hurdle: past it the verdict is PAYS, short of it NOT_YET
 *   6. A CITY FOUNDED BEFORE THE WINDOW, AND ONE FOUNDED TODAY'S WAY, ARE UNTOUCHED: the 1900 city a month before FE10 draws the buildings' figure to
 *      the bit; a city founded today's way, past FE10's month, with works standing and every count reading fitted, draws it too and carries no works with the
 *      piece, even where a count is put on its stack by hand
 *   7. THE GRID IS SIZED ON WHAT IS DRAWN (REV-R6d P1): the utility's demand and the draw it invoices fall by the saving of the works that have the piece,
 *      the same units the sector's own bill falls by; with the grid's supply between the two demands, the fitted city is fully served and the same
 *      city with the piece off its works is rationed (the energy ratio under 1), the sector's ratio left free, never forced; a city before the window
 *      and one founded today's way have no saving to hand the grid
 *
 * Every fixture is HydrogenSteelCheck.town1900(), a city founded the 1900 way with its clock at FE4's month, its works placed by hand and said over by
 * unfitTheFounding(), in a GameFiles.scratch() directory (no save slot is written); its clock is moved by ResearchCheck.atMonth() where a date is the premise.
 */
public class ElectrolyserCheck {

    static int fails;
    static int passes;

    static final PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());
    static final String PIECE = "electrolyser";
    /** The works section 3 to 5 stand: enough that "some of them" is more than none and less than all. */
    static final int WORKS = 3;

    static void check(String label, boolean ok) {
        System.out.println((ok ? "  PASS " : "  FAIL ") + label);
        if (ok) passes++; else fails++;
    }

    static void check(String label, boolean ok, String found) {
        check(label, ok);
        if (!ok && found != null) System.out.println("       found: " + found);
    }

    static void near(String label, double actual, double expected, double tolerance) {
        check(label, Math.abs(actual - expected) <= tolerance, String.format("%.9g against %.9g (tolerance %g)", actual, expected, tolerance));
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    /** One press of the clock, quietly. */
    static void play(Game g, int months) {
        quietly(() -> { for (int i = 0; i < months; i++) g.toggleNextMonth(); });
    }

    /** FE10's month in history: the model's own, never a literal. */
    static int fe10() {
        return ResearchCatalog.get().entry("FE10").worldMonth();
    }

    static void at(Game g, int month) {
        try { ResearchCheck.atMonth(g, month); } catch (Exception e) { throw new IllegalStateException(e); }
    }

    static BuildingsStacks works(Game g) {
        return g.getBuildingManager().getStack(g.getBuildingManager().getTemplate(HeavyIndustry.HYDROGEN_WORKS));
    }

    static Equipment piece(Game g) {
        return g.getBuildingManager().getTemplate(HeavyIndustry.HYDROGEN_WORKS).getEquipment(PIECE);
    }

    /** A 1900 city at FE4's month with `n` Hydrogen Steel Works placed by hand and every piece unfitted on them; the sector billed one unit of power a unit at the grid's whole ratio, so that its bill is its draw. */
    static Game city(String label, int n) {
        Game g = HydrogenSteelCheck.town1900(label);
        BuildingManager b = g.getBuildingManager();
        quietly(() -> {
            b.addStack(b.getTemplate(HeavyIndustry.HYDROGEN_WORKS), n, true);
            g.unfitTheFounding();
            g.getEconomyManager().setElectricityConsumption();
        });
        billAtOne(g);
        return g;
    }

    static void billAtOne(Game g) {
        HeavyIndustry heavy = g.getSectors().heavyIndustry();
        heavy.setPricePerWatt(1);
        heavy.setEnergyRatio(1);
    }

    /** What the buildings' templates sum to for Heavy Industry, in units: every works it has standing at the draw its template bills (EconomyManager's own sum), whatever the played months have added. */
    static double raw(Game g) {
        return g.getBuildingManager().totalBySector(g.getSectors().heavyIndustry().key(), t -> BuildingsTemplate.isBilledForUtilities(t) ? t.powerBought() : 0);
    }

    /** Heavy Industry's draw at nameplate in units: its bill at one unit of power a unit and the grid's whole ratio. */
    static double draw(Game g) {
        billAtOne(g);
        return g.getSectors().heavyIndustry().getElectricityCost();
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        one();
        two();
        Game g = city("electrolyser-1900", WORKS);
        three(g);
        four(g);
        five();
        six();
        seven();
        System.out.println(fails == 0 ? "ElectrolyserCheck: all " + passes + " ok" : "ElectrolyserCheck: " + fails + " FAILED of " + (passes + fails));
        System.exit(fails);
    }

    /* ============================ 1. the row and its dials ============================ */

    static void one() {
        System.out.println("\n--- 1. the row and its dials: the piece, the chemistry's saving, what a works saves in the game's units ---");
        BuildingManager b = new BuildingManager();
        b.initializeTemplates();
        BuildingsTemplate t = b.getTemplate(HeavyIndustry.HYDROGEN_WORKS);
        Equipment e = t == null ? null : t.getEquipment(PIECE);
        check("fixture: the Hydrogen Steel Works carries the piece, since FE10, ending ELECTROLYSERS, buying machinery and lasting",
                e != null && e.getSince().equals("FE10") && e.getEnds() == Equipment.Form.ELECTROLYSERS && e.getBuys().containsKey(Good.MACHINERY)
                        && e.getBuys().get(Good.MACHINERY) > 0 && e.getLifeMonths() > 0, String.valueOf(e));
        if (e == null) return;
        check("fixture: the target is under today's figure and over nothing (46 < 55)", HeavyIndustry.ELECTROLYSIS_KWH_A_KG_FE10 > 0
                && HeavyIndustry.ELECTROLYSIS_KWH_A_KG_FE10 < HeavyIndustry.ELECTROLYSIS_KWH_A_KG);
        near("the hydrogen a tonne of iron takes at today's figure is HYDROGEN_KG_A_TONNE kilograms at ELECTROLYSIS_KWH_A_KG: 2.97 MWh (the study's)",
                HeavyIndustry.HYDROGEN_KG_A_TONNE * HeavyIndustry.ELECTROLYSIS_KWH_A_KG / 1000, 2.97, 1e-9);
        near("...and at the target 2.484 MWh (the study's 2.48)", HeavyIndustry.HYDROGEN_KG_A_TONNE * HeavyIndustry.ELECTROLYSIS_KWH_A_KG_FE10 / 1000, 2.484, 1e-9);
        near("the saving a tonne is the difference of the two, 0.486 MWh",
                HeavyIndustry.HYDROGEN_MWH_SAVED_A_TONNE, HeavyIndustry.HYDROGEN_KG_A_TONNE * (HeavyIndustry.ELECTROLYSIS_KWH_A_KG - HeavyIndustry.ELECTROLYSIS_KWH_A_KG_FE10) / 1000, 1e-12);
        near("...0.486 MWh", HeavyIndustry.HYDROGEN_MWH_SAVED_A_TONNE, 0.486, 1e-9);
        check("...less than the works' whole draw a tonne, and the hydrogen at today's figure is part of that whole (the rest is the shaft furnace and the arc furnace)",
                HeavyIndustry.HYDROGEN_MWH_SAVED_A_TONNE < HeavyIndustry.HYDROGEN_MWH_A_TONNE
                        && HeavyIndustry.HYDROGEN_KG_A_TONNE * HeavyIndustry.ELECTROLYSIS_KWH_A_KG / 1000 < HeavyIndustry.HYDROGEN_MWH_A_TONNE);
        double steel = t.makes(Good.STEEL);
        double saved = HeavyIndustry.unitsSavedAWorks(t);
        near("a works saves its steel a month at that saving, in the game's units (HydroSites.KWH_PER_UNIT kWh a unit)",
                saved, steel * HeavyIndustry.HYDROGEN_MWH_SAVED_A_TONNE * 1000 / HydroSites.KWH_PER_UNIT, 1e-9);
        double draw = t.powerBought();
        near("...which is the saving's share of the works' whole draw (HYDROGEN_MWH_SAVED_A_TONNE over HYDROGEN_MWH_A_TONNE), to a unit's rounding",
                saved / draw, HeavyIndustry.HYDROGEN_MWH_SAVED_A_TONNE / HeavyIndustry.HYDROGEN_MWH_A_TONNE, 1.0 / draw);
        check("the template's own draw is HYDROGEN_MWH_A_TONNE a tonne, untouched by the piece (the piece is a fitting, not a figure of the row)",
                Math.abs(draw - steel * HeavyIndustry.HYDROGEN_MWH_A_TONNE * 1000 / HydroSites.KWH_PER_UNIT) <= .5 && saved < draw && HeavyIndustry.unitsSavedAWorks(null) == 0,
                draw + " units");
    }

    /* ============================ 2. the entry ============================ */

    static void two() {
        System.out.println("\n--- 2. the entry: FE10, projected, its date inside its window, after FE4 ---");
        ResearchCatalog.Entry e = ResearchCatalog.get().entry("FE10"), fe4 = ResearchCatalog.get().entry("FE4");
        check("fixture: FE10 and FE4 are in the catalogue", e != null && fe4 != null);
        if (e == null || fe4 == null) return;
        check("FE10 is an ENGINEERING row that opens equipment, of the equipment's size, patented, its source the department's page (X8)",
                e.field() == ResearchCatalog.Field.ENGINEERING && e.kinds().equals(List.of("equipment")) && e.size() == ResearchCatalog.Size.EQUIPMENT && e.fee()
                        && e.sources().contains("X8"), e.field() + " " + e.kinds() + " " + e.size() + " " + e.sources());
        check("it is a projection (dated after history's end, " + CityCalendar.format(ResearchCatalog.HISTORY_ENDS) + ") and a near-future row, which a city founded today's way never knows",
                e.projected() && e.worldMonth() > ResearchCatalog.HISTORY_ENDS && e.isNearFuture() && ResearchCatalog.nearFuture("FE10"));
        String[] years = e.window().split("-");
        int from = Integer.parseInt(years[0]), to = Integer.parseInt(years[1]);
        check("its window " + e.window() + " holds its year " + e.year() + ", which is neither end (the study's 2031 + 3 slip, 2031-2037)",
                from <= e.year() && e.year() <= to && from < e.year() && e.year() < to && e.year() - from == 3, "");
        check("it needs FE4, the works the piece is fitted to, and FE4 is dated before it", e.needs().equals(List.of("FE4")) && fe4.worldMonth() < e.worldMonth());
        check("the piece is known from FE10's month and not before: knownFrom() is the entry's, a year inside the window",
                ResearchCatalog.knownFrom("FE10") == e.worldMonth() && e.worldMonth() >= PolicyDates.monthOf(from, 1) && e.worldMonth() <= PolicyDates.monthOf(to, 12));
    }

    /* ============================ 3. known in its window and not before ============================ */

    static void three(Game g) {
        System.out.println("\n--- 3. known in its window and not before: NOT_KNOWN a month before FE10, priced at it, earlier in a city that found it ---");
        BuildingsStacks stack = works(g);
        Equipment e = piece(g);
        check("fixture: a 1900 city, " + WORKS + " works standing and every one lacking the piece, FE10 after the clock", g.foundedUnfitted() && stack.getQuantity() == WORKS
                && stack.getUnfitted(PIECE) == WORKS && fe10() > g.getMonth(), stack.getQuantity() + " works, " + stack.getUnfitted(PIECE) + " lacking");
        int open = PolicyDates.monthOf(Integer.parseInt(ResearchCatalog.get().entry("FE10").window().split("-")[0]), 1);
        at(g, open - 1);
        quietly(() -> g.getEquipmentForms().apply(g));
        check("the month before its window opens the piece is NOT_KNOWN on the works, and none carries it",
                EquipmentFit.verdict(g, stack, e).kind() == EquipmentFit.Kind.NOT_KNOWN && g.getSectors().heavyIndustry().getElectrolyserWorks() == 0);
        at(g, fe10() - 1);
        quietly(() -> g.getEquipmentForms().apply(g));
        check("...and the month before FE10's own", EquipmentFit.verdict(g, stack, e).kind() == EquipmentFit.Kind.NOT_KNOWN && !e.knownAt(g.getMonth()));
        at(g, fe10());
        quietly(() -> g.getEquipmentForms().apply(g));
        EquipmentFit.Verdict v = EquipmentFit.verdict(g, stack, e);
        check("at FE10's month it is known, priced and judged: PAYS or NOT_YET, its gain, cost, rate and hurdle numbers", e.knownAt(g.getMonth())
                && (v.kind() == EquipmentFit.Kind.PAYS || v.kind() == EquipmentFit.Kind.NOT_YET)
                && Double.isFinite(v.gain()) && Double.isFinite(v.cost()) && Double.isFinite(v.realRate()) && Double.isFinite(v.hurdle()), v.toString());

        Game early = city("electrolyser-found", 1);
        int found = fe10() - 12;
        ResearchViewCheck.setFinds(early, Map.of("FE10", found));
        BuildingsStacks eWorks = works(early);
        Equipment eE = piece(early);
        at(early, found - 1);
        check("a city that finds FE10 in month " + found + " has not the piece the month before it", EquipmentFit.verdict(early, eWorks, eE).kind() == EquipmentFit.Kind.NOT_KNOWN);
        at(early, found);
        EquipmentFit.Verdict known = EquipmentFit.verdict(early, eWorks, eE);
        check("...and has it in the month it found it, twelve months before history's (" + known.kind() + ")",
                known.kind() == EquipmentFit.Kind.PAYS || known.kind() == EquipmentFit.Kind.NOT_YET, known.kind().toString());
        check("...owing no patent rent on what it found itself", !LicenceFees.payable("FE10", early.getMonth(), early.getResearch())
                && LicenceFees.onGain("FE10", early.getMonth(), early.getResearch(), 1000) == 0);
    }

    /* ============================ 4. its kWh read by FE4's works ============================ */

    static void four(Game g) {
        System.out.println("\n--- 4. its kWh read by FE4's works: each works with the piece draws HYDROGEN_MWH_SAVED_A_TONNE less a tonne of its steel ---");
        BuildingsStacks stack = works(g);
        HeavyIndustry heavy = g.getSectors().heavyIndustry();
        BuildingsTemplate t = stack.getBuilding();
        at(g, fe10());
        double each = t.powerBought(), saved = HeavyIndustry.unitsSavedAWorks(t), whole = WORKS * each;
        quietly(() -> g.getEquipmentForms().apply(g));
        near("none fitted, Heavy Industry draws the buildings' own figure, to the bit: every works at its template's draw", draw(g), whole, 0);
        check("...and no works is counted with the piece", heavy.getElectrolyserWorks() == 0 && heavy.getElectrolyserUnitsSaved() == 0);

        stack.setUnfitted(PIECE, 1);
        quietly(() -> g.getEquipmentForms().apply(g));
        check("two of the " + WORKS + " works fitted, two are counted (the works standing less those lacking the piece)", heavy.getElectrolyserWorks() == WORKS - 1,
                "" + heavy.getElectrolyserWorks());
        near("Heavy Industry draws the works' draws less the saving of each fitted works", draw(g), whole - (WORKS - 1) * saved, 1e-6);
        near("...a fitted works drawing the whole 3.48 MWh a tonne less the 0.486, in units: draw - saved is HYDROGEN_MWH_A_TONNE - HYDROGEN_MWH_SAVED_A_TONNE a tonne of its steel",
                each - saved, t.makes(Good.STEEL) * (HeavyIndustry.HYDROGEN_MWH_A_TONNE - HeavyIndustry.HYDROGEN_MWH_SAVED_A_TONNE) * 1000 / HydroSites.KWH_PER_UNIT, 1);
        quietly(() -> g.getEconomyManager().setElectricityConsumption());
        near("EconomyManager setting the draw again from the templates keeps the saving off it", draw(g), whole - (WORKS - 1) * saved, 1e-6);
        quietly(() -> g.getEquipmentForms().apply(g));
        near("...and the forms said again move nothing (the saving is taken off the buildings' figure once, not each time)", draw(g), whole - (WORKS - 1) * saved, 1e-6);

        stack.setUnfitted(PIECE, 0);
        quietly(() -> g.getEquipmentForms().apply(g));
        near("every works fitted, the saving is of every one", draw(g), whole - WORKS * saved, 1e-6);
        check("...and the sector still draws power: the saving is part of the draw and never all of it", draw(g) > 0);

        stack.setUnfitted(PIECE, WORKS);
        quietly(() -> g.getEquipmentForms().apply(g));
        near("the count that comes back takes the saving off again: the buildings' figure, to the bit", draw(g), whole, 0);

        stack.setUnfitted(PIECE, 1);
        quietly(() -> g.getEquipmentForms().apply(g));
        play(g, 2);
        check("fixture: two months played, the works stand and carry the count they were given", stack.getQuantity() == WORKS && heavy.getElectrolyserWorks() == WORKS - stack.getUnfitted(PIECE),
                stack.getQuantity() + " works, " + heavy.getElectrolyserWorks() + " counted, " + stack.getUnfitted(PIECE) + " lacking");
        long fitted = WORKS - stack.getUnfitted(PIECE);
        near("two months played, the draw is still the buildings' sum less the fitted works' saving (the month's own setting of the draw kept it)", draw(g), raw(g) - fitted * saved, 1e-6);
    }

    /* ============================ 5. the saving against the model's own constants ============================ */

    static void five() {
        System.out.println("\n--- 5. the saving against the model's own constants: it pays where power is dear, and the break-even is the model's own arithmetic ---");
        Game g = city("electrolyser-pays", WORKS);
        BuildingsStacks stack = works(g);
        BuildingsTemplate t = stack.getBuilding();
        Equipment e = piece(g);
        at(g, fe10());
        quietly(() -> g.getEquipmentForms().apply(g));
        double units = HeavyIndustry.unitsSavedAWorks(t);
        double own = g.getServicesManager().getPricePerWatt();   // the model's own tariff, the utility's (HydrogenSteelCheck.paysTown() reads it so)
        g.getEconomyManager().setPricePerWatt(own);
        check("fixture: the works saves units, and the grid has a price", units > 0 && own > 0, units + " units at " + own);
        near("the piece's gross gain is the units it saves at the unit's price (EquipmentFit.electrolyserGain())",
                EquipmentFit.grossGainOf(g, t, e), units * own, 1e-9 * units * own);
        near("...which is a function of the price alone: twice the price, twice the gain", twice(g, t, e, own), 2 * units * own, 1e-9 * units * own);

        EquipmentFit.Verdict atOwn = EquipmentFit.verdict(g, stack, e);
        double whole = HydrogenSteelCheck.figure(g).c().cost();
        System.out.printf("  the piece costs %.2f; the works' whole cost to the planner is %.2f: the piece is %.2f%% of it%n", atOwn.cost(), whole, 100 * atOwn.cost() / whole);
        check("the piece costs about the stacks' share of the works' whole cost, 8% (an estimate: the electrolysers a fifth of such a works, the stacks two fifths of them, the energy department's own 100 of 250 dollars a kilowatt for 2026)",
                atOwn.cost() > .06 * whole && atOwn.cost() < .10 * whole, String.format("%.2f%%", 100 * atOwn.cost() / whole));
        double net = 1 - LicenceFees.SHARE;
        check("FE10 is payable at its own month, so the verdict's gain is the gross less the patent's share of it",
                LicenceFees.payable("FE10", g.getMonth(), g.getResearch()), "");
        near("the verdict's gain is the gross less that rent (1 - LicenceFees.SHARE of it)", atOwn.gain(), net * units * own, 1e-9 * units * own);
        double needed = Math.max(atOwn.cost() / atOwn.factor(), atOwn.hurdle());
        double breakEven = needed / (net * units);
        System.out.printf("  the piece at the model's own tariff %.6f: gain %.2f a month on a cost of %.2f, life %d months (earns back %.2f), hurdle %.2f: %s; break-even tariff %.6f (%.2f times)%n",
                own, atOwn.gain(), atOwn.cost(), atOwn.lifeMonths(), atOwn.earnsBack(), atOwn.hurdle(), atOwn.kind(), breakEven, breakEven / own);
        check("fixture: the break-even is a price above nothing", breakEven > 0 && Double.isFinite(breakEven), "" + breakEven);
        check("the verdict at the model's own tariff is the one that tariff's side of the break-even gives: PAYS exactly when the tariff is at least it",
                (atOwn.kind() == EquipmentFit.Kind.PAYS) == (own >= breakEven), atOwn.kind() + " at " + own + ", break-even " + breakEven);

        g.getEconomyManager().setPricePerWatt(breakEven * 1.25);
        EquipmentFit.Verdict dear = EquipmentFit.verdict(g, stack, e);
        g.getEconomyManager().setPricePerWatt(breakEven * 0.8);
        EquipmentFit.Verdict cheap = EquipmentFit.verdict(g, stack, e);
        check("a quarter above the break-even tariff the piece PAYS: its gain earns the cost back over its life and clears the hurdle", dear.kind() == EquipmentFit.Kind.PAYS
                && dear.gain() * dear.factor() >= dear.cost() && dear.gain() >= dear.hurdle(), dear.toString());
        check("a fifth short of it the piece does NOT: NOT_YET, the same works, the same cost, the same life", cheap.kind() == EquipmentFit.Kind.NOT_YET && cheap.cost() == dear.cost()
                && cheap.lifeMonths() == dear.lifeMonths() && (cheap.gain() < cheap.cost() / cheap.factor() || cheap.gain() < cheap.hurdle()), cheap.toString());
        near("the gain between the two tariffs differs by the units saved at the difference of the prices, net of the rent", dear.gain() - cheap.gain(),
                net * units * breakEven * (1.25 - 0.8), 1e-9 * dear.gain());
        g.getEconomyManager().setPricePerWatt(0);
        check("at no price for power the piece saves nothing and is not fitted: NOT_YET", EquipmentFit.verdict(g, stack, e).kind() == EquipmentFit.Kind.NOT_YET
                && EquipmentFit.grossGainOf(g, t, e) == 0);
    }

    static double twice(Game g, BuildingsTemplate t, Equipment e, double price) {
        g.getEconomyManager().setPricePerWatt(2 * price);
        double v = EquipmentFit.grossGainOf(g, t, e);
        g.getEconomyManager().setPricePerWatt(price);
        return v;
    }

    /* ============================ 7. the grid is sized on what is drawn ============================ */

    /** The works section 7 stands: enough that the saving of all of them is a good part of a Coal Power Plant. */
    static final int GRID_WORKS = 6;

    /** The city's demand, the draw invoiced, the grid's ratio and the sector's own draw, as a counted state leaves them; and the merit order's month (PW1), its half-blocks' loads. */
    record Grid(double demand, double billed, double ratio, double drawn, PowerDispatch.Month month) {
        /** The peak block's load, the losses in: what a grid must carry to serve the month whole (PW1, PowerDispatch). */
        double peak() { return month.load[2 * PowerDispatch.PEAK]; }
    }

    /**
     * The city with `fitted` of the works carrying the piece: the counts set, the forms said, the services updated and, where `supply` is a number, the grid's
     * production written over the buildings' so that it is exactly that (the merit order then gives the ratio: never forced here).
     * PW1, 2026-11-20: the dispatch reads the plants standing, not the production written, so the grid is handed no plants (the bare fixture's
     * path, UtilitiesHandler.updateEnergyRatio()): the production written runs as one free row of exactly that size.
     */
    static Grid grid(Game g, long fitted, double supply) {
        BuildingsStacks stack = works(g);
        stack.setUnfitted(PIECE, stack.getQuantity() - fitted);
        quietly(() -> {
            g.getEquipmentForms().apply(g);
            g.getEconomyManager().setElectricityConsumption();
            g.getServicesManager().updateServices();
        });
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        if (!Double.isNaN(supply)) {
            u.setPlants(null);
            u.setWattsProduction(0);
            u.updateEnergyRatio();
            double base = u.getProduction(), fill = u.getAverageUtilityFill();
            u.setWattsProduction((supply - base) / fill);
            u.updateEnergyRatio();
        }
        return new Grid(u.getConsumption(), u.getBilledElectricityDraw(), u.getEnergyRatio(), draw(g), u.getLatestMonth());
    }

    static void seven() {
        System.out.println("\n--- 7. the grid is sized on what is drawn: the utility's demand and invoiced draw fall with the works' bill, and the ratio follows ---");
        Game g = city("electrolyser-grid", GRID_WORKS);
        BuildingsTemplate t = works(g).getBuilding();
        at(g, fe10());
        double saved = HeavyIndustry.unitsSavedAWorks(t);
        Grid off = grid(g, 0, Double.NaN);
        Grid on = grid(g, GRID_WORKS, Double.NaN);
        SubstationCheck.wiresFor(g, off.peak());
        check("fixture: substations carry the unfitted peak, so only generation can ration this comparison",
                Substations.costs(g).carryKw() >= off.peak());
        near("every works fitted, the city's demand falls by the saving of every one (ServicesManager reads what the sector is billed)", off.demand() - on.demand(), GRID_WORKS * saved, 1e-6);
        near("...and so does the draw the utility invoices", off.billed() - on.billed(), GRID_WORKS * saved, 1e-6);
        near("...the same units the sector's own bill falls by", off.drawn() - on.drawn(), GRID_WORKS * saved, 1e-6);
        near("...which the services manager holds as the saving it was handed", g.getServicesManager().getElectrolyserSaving(), GRID_WORKS * saved, 1e-6);
        Grid some = grid(g, GRID_WORKS / 2, Double.NaN);
        near("half of them fitted, half the saving comes off the demand", off.demand() - some.demand(), (GRID_WORKS / 2) * saved, 1e-6);
        double fill = g.getServicesManager().getUtilitiesHandler().getAverageUtilityFill();
        check("fixture: the utility has staff, so its supply scales with its fill and can be written to a number", fill > 0, "" + fill);
        // PW1, 2026-11-20: the ratio is the merit order's, by half-blocks, so the grid is sized between the two demands' peak blocks (the dispatch's own loads).
        double supply = (off.peak() + on.peak()) / 2;
        Grid served = grid(g, GRID_WORKS, supply);
        Grid rationed = grid(g, 0, supply);
        System.out.printf("  a grid of %,.0f units against peak blocks of %,.0f (works fitted) and %,.0f (not): the ratio %.4f and %.4f%n", supply, on.peak(), off.peak(), served.ratio(), rationed.ratio());
        check("a grid sized between the two demands' peak blocks serves the city whose works have the piece in full: the energy ratio is 1", served.ratio() == 1 && served.peak() < supply, "" + served.ratio());
        check("...and rations the same city with the piece off its works: the ratio is under 1, the supply under the peak block it has", rationed.ratio() < 1 && rationed.peak() > supply,
                rationed.ratio() + " at peak " + rationed.peak());
        // ...the supply's share of what the half-blocks asked, each served up to the grid's size (PW1: the merit order's served over asked).
        double asked = 0, met = 0;
        for (int s = 0; s < PowerDispatch.HALVES; s++) {
            asked += rationed.month().load[s] * PowerDispatch.halfHours(s);
            met += Math.min(supply, rationed.month().load[s]) * PowerDispatch.halfHours(s);
        }
        near("...at exactly what the supply serves of the half-blocks' loads (the merit order's served over asked)", rationed.ratio(), met / asked, 1e-12);
        Grid again = grid(g, GRID_WORKS, Double.NaN);
        near("the count that comes back takes the saving off the demand again", again.demand(), on.demand(), 1e-6);
        Grid none = grid(g, 0, Double.NaN);
        check("...and the count taken off hands the grid none: the demand is the buildings' own, to the bit, and the saving 0",
                none.demand() == off.demand() && g.getServicesManager().getElectrolyserSaving() == 0, none.demand() + " against " + off.demand());
    }

    /* ============================ 6. before the window, and today's way ============================ */

    static void six() {
        System.out.println("\n--- 6. a city founded before the window, and one founded today's way, are untouched ---");
        Game g = city("electrolyser-before", WORKS);
        HeavyIndustry heavy = g.getSectors().heavyIndustry();
        BuildingsTemplate t = works(g).getBuilding();
        int open = PolicyDates.monthOf(Integer.parseInt(ResearchCatalog.get().entry("FE10").window().split("-")[0]), 1);
        at(g, open - 1);
        quietly(() -> g.getEquipmentForms().apply(g));
        near("a 1900 city a month before the window opens draws the buildings' figure, to the bit", draw(g), WORKS * t.powerBought(), 0);
        check("...with no works carrying the piece, and the grid handed no saving", heavy.getElectrolyserWorks() == 0 && g.getServicesManager().getElectrolyserSaving() == 0);
        play(g, 3);
        near("...three months played, the buildings' sum to the bit", draw(g), raw(g), 0);
        check("...and none carries the piece", heavy.getElectrolyserWorks() == 0);

        // A works placed by hand with no count at all (a harness's city, an old save) reads "fitted" by its counts, but a piece nobody knows yet is on no works.
        Game bare = HydrogenSteelCheck.town1900("electrolyser-bare");
        BuildingManager bb = bare.getBuildingManager();
        quietly(() -> bb.addStack(bb.getTemplate(HeavyIndustry.HYDROGEN_WORKS), WORKS, true));
        BuildingsStacks bs = works(bare);
        at(bare, fe10() - 1);
        quietly(() -> { bare.getEconomyManager().setElectricityConsumption(); bare.getEquipmentForms().apply(bare); });
        check("fixture: a 1900 city with " + WORKS + " works placed by hand, no count of any piece on them", bare.foundedUnfitted() && bs.getQuantity() == WORKS && !bs.hasUnfitted());
        check("the month before FE10 no count-less works is counted with a piece nobody knows, and the draw is the buildings' own, to the bit",
                bare.getSectors().heavyIndustry().getElectrolyserWorks() == 0 && draw(bare) == raw(bare) && raw(bare) == WORKS * bs.getBuilding().powerBought());
        at(bare, fe10());
        quietly(() -> bare.getEquipmentForms().apply(bare));
        check("...and at FE10's month the same stack, lacking nothing by its counts, is counted with it, all of them", bare.getSectors().heavyIndustry().getElectrolyserWorks() == WORKS,
                "" + bare.getSectors().heavyIndustry().getElectrolyserWorks());

        Game today = ResearchCheck.datedCity("electrolyser-today", false);
        BuildingManager b = today.getBuildingManager();
        BuildingsTemplate tt = b.getTemplate(HeavyIndustry.HYDROGEN_WORKS);
        at(today, fe10() + 12);
        quietly(() -> {
            b.addStack(tt, WORKS, true);
            today.getEconomyManager().setElectricityConsumption();
            today.getEquipmentForms().apply(today);
        });
        billAtOne(today);
        BuildingsStacks stack = b.getStack(tt);
        check("fixture: founded today's way, a year past FE10's month, " + WORKS + " works standing all fitted (no count at all)", !today.foundedUnfitted()
                && today.getMonth() > fe10() && stack.getQuantity() == WORKS && !stack.hasUnfitted(), today.getMonth() + " " + fe10());
        HeavyIndustry modern = today.getSectors().heavyIndustry();
        near("the works draw the buildings' figure, to the bit: no saving in a city that never knows FE10", draw(today), WORKS * tt.powerBought(), 0);
        check("...and none is counted with the piece, every count there reading fitted, and the grid is handed no saving",
                modern.getElectrolyserWorks() == 0 && modern.getElectrolyserUnitsSaved() == 0 && today.getServicesManager().getElectrolyserSaving() == 0);
        stack.setUnfitted(PIECE, WORKS);
        quietly(() -> today.getEquipmentForms().apply(today));
        stack.setUnfitted(PIECE, 0);
        quietly(() -> today.getEquipmentForms().apply(today));
        near("a count of the piece put on the stack by hand, and taken off, moves nothing: the buildings' figure still", draw(today), WORKS * tt.powerBought(), 0);
        check("...no works carries the piece", modern.getElectrolyserWorks() == 0);
        play(today, 2);
        near("two months played, the buildings' sum to the bit", draw(today), raw(today), 0);
    }
}
