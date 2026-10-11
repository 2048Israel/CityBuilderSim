package ham.citybuildersim;

import ham.citybuildersim.sectors.RefineryFlow;
import ham.citybuildersim.sectors.RefineryFlow.Kind;
import ham.citybuildersim.sectors.RefineryFlow.Stream;
import ham.citybuildersim.sectors.Refining;
import ham.citybuildersim.sectors.SpreadPlanner;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * The refinery's units and the flow through them (0.7.80, batch O4;
 * runs/spec-oil.md 2.3, and 4's RefineryCheck), and since 0.7.82 the spread
 * planner that orders them (batch O5; spec-oil 2.4, SpreadPlanner).
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. WITH NO CONVERSION UNIT THE FLOW IS THE SLATE, to the bit: crude units
 *      alone make what Refining.slate() of the same crude and mix makes, on
 *      every grade and mix - and so does a refinery whose units all stand
 *      at a spread of nothing, which idle.
 *
 *   2. THE SPREADS ARE THE SPEC'S, to the bit: each kind's products of a
 *      litre at the values, less what the litre is worth with no unit - its
 *      leftover's value, or residue's (4 x fuel oil - diesel) / 3.
 *
 *   3. THE UNITS BALANCE TO THE LITRE: the crude's litres, plus what each
 *      unit gains or loses of its feed's volume, are the products' litres
 *      and the residue burned; the coke and the bitumen are each unit's run
 *      at its weight a litre; no unit takes more than its feed or than its
 *      stream holds; and the flow at nameplate times a rate is the flow at
 *      that rate (the reads' rate on top).
 *
 *   4. HYDROGEN CAPS THE HYDROCRACKERS at the reformers' make on the
 *      straight-run heavy naphtha (Q12): with no reformer, or idle ones
 *      (star O4-3), none runs.
 *
 *   5. THE RESIDUE IS CUT THREE TO ONE WITH THE DIESEL and the rest burned
 *      (Q5): on heavy crude with no unit, residue burns; a coker or an
 *      asphalt unit takes residue the furnaces would have burned.
 *
 *   6. WITHIN A STREAM THE WIDEST SPREAD TAKES FIRST, and a unit at a spread
 *      of nothing or less takes none: the gas oil to the unit that makes
 *      the most of it, the heavy residue to the asphalt unit ahead of the
 *      coker when its spread is the wider (star O4-2), the coker first when
 *      it is not.
 *
 *   7. THE REFINERY'S NAMEPLATE IS THE FLOW: in a town with an Oil Refinery
 *      and its units standing, each product's capacity is the flow of the
 *      refinery's own crude, mix, units and prices, to the bit; the units
 *      on site are its pipeline; the audit closes every month, nothing is
 *      written off; and a saved city loads to the same flow.
 *
 *   8. THE PLAYER MAY ORDER A UNIT: the city's own order for one goes on
 *      site (the town's refiners held, so no investor's order is among it).
 *
 *   THE SPREAD PLANNER (0.7.82):
 *
 *   9. THE CITY'S OWN PRICE: a good is worth its net import price while its
 *      market imported any this month, its net export price otherwise - its
 *      local price until 0.7.105 when it exported none (R1) - and a good the
 *      world will not buy its local price (SpreadPlanner.cityValue()).
 *
 *  10. A UNIT'S EARNINGS ARE ITS SPREAD, to the bit: at the values, on the
 *      feed it would find spare, at most its own, at the rate, less its
 *      running and standing costs - each kind finding its own stream (the
 *      coker the residue and the heavy residue, a hydrocracker the gas oil
 *      the spare hydrogen treats).
 *
 *  11. EACH GATE REFUSES ON A FIXTURE THAT CAUSES IT: the feed (no crude unit,
 *      no stream; a crude unit a hair under rule 6's FEED_GATE), the ground
 *      and the staff (a frame that refuses them; ground the only refusal is
 *      the land office's case), the money (on imported crude the playtest's
 *      Oil Refinery loses money; a hair under the hurdle). The game's money
 *      gate in force since 0.7.112 (11b; Jerus's decision A) tests the whole
 *      cost whoever pays - PROFIT_OVER_INTEREST times the interest at the
 *      real rate, as servicesItsOwnDebt() strikes it, to the bit, the same
 *      with the cost in the till as with nothing in it; the rule to 0.7.111,
 *      kept as a named history, tested what an order borrows, and with the
 *      cost in the till asked nothing but that it earn something.
 *
 *  12. THE PACKAGE REPRODUCES spec-oil 2.4's FOUR ROWS in the prototype's
 *      frame (spread.py: fixed demand, the world's prices, wages at 4.84 a
 *      post, 0.5% a month on the whole cost, six months to open), deciding as
 *      it did on each month's state before its opening; and deciding as the
 *      game does, after it, what spread.py orders when it decides so.
 *
 *  13. ONLY A KIND IDLE FOR SIX MONTHS IS SHED: a Small Asphalt Unit on
 *      medium crude counts idle months, a working Small Lube Plant none;
 *      under IDLE_MONTHS neither may be sold, then the asphalt unit alone -
 *      not the crude unit while it idles - the spare-capacity rule's measure
 *      is its feed and the rule sells it; the months cross a save.
 *
 *  14. THE INVESTORS ORDER A UNIT: a town with an Oil Refinery on its own
 *      wells' crude (since 0.7.105: on imported crude by lorry its barrel
 *      loses, the run stops, and no unit is ordered behind it) and nothing
 *      held - since 0.7.103 the cost of any unit in the refiners' till, the
 *      lube plant it ordered until then capped out (15), and since 0.7.112
 *      their planner on the rule to 0.7.111, under which that cash orders
 *      (11b) - orders the planner's best, a conversion unit; Game.consider()
 *      tests it on the planner's earnings; with no ground free it is refused
 *      at the ground and the land office hears of it; a crude unit's
 *      estimate is its share of its package's by cost.
 *
 *  15. THE WORLD'S DEMAND FOR LUBRICANTS (0.7.103, batch P4; Jerus's A9):
 *      the world takes a city's lubricants up to LUBRICANTS_WORLD_SHARE of
 *      the crude its crude units run, litre for litre; the planner weighs a
 *      lube plant on no more gas oil than makes what the world and the city
 *      would still take - refused at its feed in the words of the
 *      lubricants where an Oil Refinery's share is under FEED_GATE of a
 *      Small Lube Plant's, weighed again once the city's own demand is
 *      there, and left out of a package - the prototype's frame uncapped;
 *      and in a town with an Oil Refinery and a Small Lube Plant standing
 *      the ship's line takes no more than the world's share of the month's
 *      crude, the rest idled, the audit closing.
 *
 *   THE RUN, AND THE PLANNER AT THE MARGIN (0.7.105, batch P6; Jerus's A2 (b),
 *   R1 and R3, on the project's why-the-refinery-loses.md):
 *
 *  16. A CRUDE UNIT RUNS A BARREL ONLY WHILE IT PAYS (Refining.runRule()):
 *      the study's margins from the model's own prices and freight - a
 *      topping barrel of medium crude, all exported, by lorry -10.0%, by rail
 *      at .60 -4.7%, at the floor -0.7%, by tanker +2.3% of crude's world
 *      middle, and through conversion units by tanker +9.3% - each to its
 *      tenth; a barrel's margin to the bit; none run by lorry or rail, the
 *      whole by tanker; at the floor the barrels the city's petrol and diesel
 *      take, at the net import price (+1.4%), and none past them (-0.7%); at
 *      .60 not even those (-1.7%); the wells' crude, and not the imported;
 *      no lubricants past the world's and the city's; the pace.
 *
 *  17. THE RUN IN A TOWN: an Oil Refinery on imported crude by lorry, the
 *      refiners not held: the month's end moves the run RUN_PACE a month to
 *      nothing; the rate is the plant's times it, the crude bought and the
 *      products made at it, the audit closing; a held town's stays; the run
 *      crosses a save; the page and the picture say why; and once it has
 *      stood under nameplate IDLE_MONTHS months the spare-capacity rule
 *      measures it, and sells the crude unit when a whole one is spare.
 *
 *  18. THE PLANNER'S PRICES AT THE MARGIN (R1): an order is weighed at the
 *      run it would take - behind a run that stops it earns nothing, by
 *      tanker its spread as in the prototype's frame, to the bit - and petrol
 *      past what the city imports at the net export price, to the bit.
 *
 *  19. ONE ORDER IN FLIGHT, CRUDE UNITS ON SITE ASIDE (R3): the city's own
 *      Crude Unit on site does not hold the refiners' planner, which weighs
 *      its units and no other crude unit; a conversion unit on site does;
 *      and the spare-capacity rule still reads a site as building.
 */
public class RefineryCheck {

    static int fails = 0;
    /** Where the harness prints: the console, kept while a fixture town's prose is sent to `quiet`. */
    static final PrintStream out = System.out;
    /** A stream that drops what is written to it: the game's prose while a fixture town is founded and played. */
    static final PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());

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

    /** Each product's world middle, by Good's ordinal: (import + export) / 2, the prototype's prices. */
    static double[] worldMid() {
        return RefineryFlow.values(g -> (g.worldImportPrice() + g.worldExportPrice()) / 2);
    }

    static double v(double[] values, Good g) { return values[g.ordinal()]; }

    /** Each kind's feed: its small and its large unit, `n` of each, from the catalogue. */
    static double[] campus(BuildingManager b, int n) {
        double[] feed = new double[Kind.values().length];
        for (BuildingsTemplate t : b.getTemplates()) {
            if (t.refineryUnit() != null) feed[t.refineryUnit().ordinal()] += n * t.feedPerMonth();
        }
        return feed;
    }

    static double[] only(Kind k, double litres) {
        double[] feed = new double[Kind.values().length];
        feed[k.ordinal()] = litres;
        return feed;
    }

    static double[] mix(double light, double medium, double heavy) {
        return new double[] { light, medium, heavy };
    }

    public static void main(String[] args) throws Exception {
        BuildingManager catalogue = new BuildingManager();
        catalogue.initializeBuiltInTemplates();
        double[] world = worldMid();
        double[] none = new double[Kind.values().length];
        double[][] mixes = { Refining.MEDIUM_MIX, Refining.mixOf(Deposit.Grade.LIGHT), Refining.mixOf(Deposit.Grade.HEAVY),
                mix(1.0 / 3, 1.0 / 3, 1.0 / 3), mix(0, .552, .448), mix(.25, .5, .25) };
        double[] tonnes = { 0, 1, 8300, 415000, 12345.678, 3 * 8300 + 415000 };

        /* ============================ 1. NO UNIT: THE SLATE ============================ */
        out.println("--- 1. with no conversion unit the flow is the slate, to the bit ---");
        boolean slate = true, idle = true;
        double[] zero = new double[Good.values().length];
        double[] all = campus(catalogue, 1);
        for (double[] m : mixes) {
            for (double t : tonnes) {
                Refining.Slate s = Refining.slate(t, m);
                slate &= RefineryFlow.solve(none, t, m, world).slate().equals(s);
                // Every unit standing, every spread nothing: each idles.
                RefineryFlow.Flow f = RefineryFlow.solve(all, t, m, zero);
                idle &= f.slate().equals(s);
                for (Kind k : Kind.values()) idle &= f.run(k) == 0;
            }
        }
        assertTrue("crude units alone make Refining.slate() of their crude and mix, every product and the residue burned, to"
                + " the bit (" + mixes.length + " mixes x " + tonnes.length + " runs)", slate);
        assertTrue("...and so does a campus of every unit at a spread of nothing: each takes none and idles", idle);
        Refining.Slate medium = Refining.slate(8300);
        assertTrue("...the Oil Refinery's 8,300 t of medium crude, O1's slate: 70 L of petrol and 169 L of diesel a tonne",
                RefineryFlow.solve(none, 8300, Refining.MEDIUM_MIX, world).slate().equals(medium)
                        && Math.round(medium.of(Good.PETROL) / 8300) == 70 && Math.round(medium.of(Good.DIESEL) / 8300) == 169);

        /* ============================ 2. THE SPREADS ============================ */
        out.println("\n--- 2. the spreads, to the bit ---");
        double petrol = v(world, Good.PETROL), diesel = v(world, Good.DIESEL), jet = v(world, Good.JET),
                naphtha = v(world, Good.NAPHTHA), lpg = v(world, Good.LPG), fuelOil = v(world, Good.FUEL_OIL),
                lubes = v(world, Good.LUBRICANTS), bitumen = v(world, Good.BITUMEN), coke = v(world, Good.COKE);
        double residue = (4 * fuelOil - diesel) / 3, perTonne = RefineryFlow.RESIDUE_LITRES_PER_TONNE;
        double[] expected = new double[Kind.values().length];
        expected[Kind.REFORMER.ordinal()] = .82 * petrol + .08 * lpg - naphtha;
        expected[Kind.CRACKER.ordinal()] = .25 * lpg + .55 * petrol + .18 * diesel + .07 * fuelOil - fuelOil;
        expected[Kind.HYDROCRACKER.ordinal()] = .50 * diesel + .40 * jet + .15 * naphtha + .05 * lpg - fuelOil;
        expected[Kind.ALKYLATION.ordinal()] = .80 * petrol - lpg;
        expected[Kind.COKER.ordinal()] = .08 * lpg + .15 * naphtha + .35 * diesel + .12 * fuelOil + .30 / perTonne * coke - residue;
        expected[Kind.LUBE.ordinal()] = .40 * lubes + .60 * fuelOil - fuelOil;
        expected[Kind.ASPHALT.ordinal()] = .95 / perTonne * bitumen - residue;
        boolean spreads = true;
        RefineryFlow.Flow some = RefineryFlow.solve(all, 415000, Refining.MEDIUM_MIX, world);
        StringBuilder sp = new StringBuilder();
        for (Kind k : Kind.values()) {
            spreads &= RefineryFlow.spread(k, world) == expected[k.ordinal()] && some.spread(k) == expected[k.ordinal()];
            sp.append(String.format("%s %+.4f  ", k.name().toLowerCase(), expected[k.ordinal()] * 1000));
        }
        report("each kind's spread is its yields at the values less its feed's own value (spec-oil 2.4), to the bit", spreads,
                "$ a litre at the world's middle: " + sp.toString().trim());
        assertTrue("...a feed's own value: gas oil fuel oil's, heavy naphtha naphtha's, gas and cracked gas petroleum gas's,"
                        + " residue (4 x fuel oil - diesel) / 3",
                RefineryFlow.feedValue(Stream.GAS_OIL, world) == fuelOil && RefineryFlow.feedValue(Stream.HEAVY_NAPHTHA, world) == naphtha
                        && RefineryFlow.feedValue(Stream.GAS, world) == lpg && RefineryFlow.feedValue(Stream.CRACKED_GAS, world) == lpg
                        && RefineryFlow.feedValue(Stream.RESIDUE, world) == residue
                        && RefineryFlow.feedValue(Stream.HEAVY_RESIDUE, world) == residue);
        boolean alone = true;
        for (Kind k : Kind.values()) {
            double made = 0, value = 0;
            for (java.util.Map.Entry<Good, Double> e : RefineryFlow.madeAlone(k, 1000).entrySet()) {
                value += e.getValue() * v(world, e.getKey());
            }
            for (RefineryFlow.Yield y : k.yields()) made += 1000 * y.perLitre() * v(world, y.product());
            alone &= Math.abs(value - made) <= 1e-12 * Math.max(1, Math.abs(made))
                    && Math.abs((value - 1000 * RefineryFlow.feedValue(k.feed(), world)) - 1000 * RefineryFlow.spread(k, world))
                    <= 1e-12 * Math.max(1, Math.abs(made));
        }
        assertTrue("...and a unit's products alone (its card's, Refining.madeBy()) are worth its feed's value and its spread", alone);

        /* ============================ 3. THE BALANCE ============================ */
        out.println("\n--- 3. the units balance to the litre ---");
        double[] campus = campus(catalogue, 1);
        boolean balance = true, solids = true, bounded = true, homogeneous = true;
        double worstMiss = 0;
        for (double[] m : mixes) {
            for (double t : new double[] { 8300, 415000, 3 * 8300 + 415000 }) {
                RefineryFlow.Flow f = RefineryFlow.solve(campus, t, m, world);
                double in = f.crude(), outL = f.burned();
                for (Kind k : Kind.values()) {
                    double gain = -1;
                    for (RefineryFlow.Yield y : k.yields()) {
                        if (y.good() == Good.COKE || y.good() == Good.BITUMEN) continue;
                        gain += y.perLitre();
                    }
                    in += f.run(k) * gain;
                    bounded &= f.run(k) >= 0 && f.run(k) <= campus[k.ordinal()];
                }
                for (Good g : f.slate().products().keySet()) {
                    if (g != Good.COKE && g != Good.BITUMEN) outL += f.of(g);
                }
                double miss = Math.abs(in - outL);
                worstMiss = Math.max(worstMiss, miss);
                balance &= miss <= 1;
                solids &= f.of(Good.COKE) == f.run(Kind.COKER) * (.30 / perTonne)
                        && f.of(Good.BITUMEN) == f.run(Kind.ASPHALT) * (.95 / perTonne);
                for (Stream s : Stream.values()) bounded &= f.spare(s) >= 0;
                // ...the rate on top: the flow at nameplate times a rate is the flow at the rate.
                double rate = .37;
                double[] at = campus.clone();
                for (int i = 0; i < at.length; i++) at[i] *= rate;
                RefineryFlow.Flow slow = RefineryFlow.solve(at, t * rate, m, world);
                for (Good g : Refining.BOUGHT_HERE) {
                    homogeneous &= Math.abs(slow.of(g) - f.of(g) * rate) <= 1e-9 * Math.max(1, f.of(g));
                }
                for (Good g : new Good[] { Good.LPG, Good.NAPHTHA, Good.JET, Good.FUEL_OIL, Good.LUBRICANTS, Good.BITUMEN, Good.COKE }) {
                    homogeneous &= Math.abs(slow.of(g) - f.of(g) * rate) <= 1e-9 * Math.max(1, f.of(g));
                }
            }
        }
        report("a campus of every unit: the crude's litres and each unit's gain or loss of its feed's volume are the"
                + " products' litres and the residue burned, to the litre", balance, String.format("worst %.2e L", worstMiss));
        assertTrue("...the coke and the bitumen are the coker's and the asphalt unit's runs at their weight a litre, to the bit", solids);
        assertTrue("...no unit takes more than its feed, and no stream is taken below nothing", bounded);
        assertTrue("...and the flow at nameplate times a rate is the flow at that rate (the reads' rate on top)", homogeneous);

        /* ============================ 4. HYDROGEN ============================ */
        out.println("\n--- 4. hydrogen caps the hydrocrackers ---");
        BuildingsTemplate smallReformer = catalogue.getTemplateByName("Small Reformer"),
                hydrocracker = catalogue.getTemplateByName("Hydrocracker");
        double[] h = new double[Kind.values().length];
        h[Kind.REFORMER.ordinal()] = smallReformer.feedPerMonth();
        h[Kind.HYDROCRACKER.ordinal()] = hydrocracker.feedPerMonth();
        RefineryFlow.Flow hf = RefineryFlow.solve(h, 415000, Refining.MEDIUM_MIX, world);
        double made = Math.min(smallReformer.feedPerMonth(), hf.cut(Stream.HEAVY_NAPHTHA)) * RefineryFlow.HYDROGEN_MADE;
        report("fixture: a Small Reformer beside a Hydrocracker on a Crude Unit's medium crude - the gas oil and the naphtha"
                        + " past both units' feed", hf.cut(Stream.GAS_OIL) > hydrocracker.feedPerMonth()
                        && hf.cut(Stream.HEAVY_NAPHTHA) > smallReformer.feedPerMonth(),
                String.format("%,.0f L of gas oil, %,.0f L of heavy naphtha", hf.cut(Stream.GAS_OIL), hf.cut(Stream.HEAVY_NAPHTHA)));
        report("the reformer's hydrogen is its run on the straight-run naphtha at HYDROGEN_MADE a barrel, to the bit",
                hf.hydrogen() == made, String.format("%,.0f scf", hf.hydrogen()));
        report("...and the hydrocracker takes only the gas oil that hydrogen treats at HYDROGEN_USED a barrel, well under its feed",
                hf.run(Kind.HYDROCRACKER) == made / RefineryFlow.HYDROGEN_USED && hf.run(Kind.HYDROCRACKER) < hydrocracker.feedPerMonth()
                        && hf.spareHydrogen() <= 1e-9 * made,
                String.format("%,.0f L of %,.0f", hf.run(Kind.HYDROCRACKER), hydrocracker.feedPerMonth()));
        RefineryFlow.Flow noH = RefineryFlow.solve(only(Kind.HYDROCRACKER, hydrocracker.feedPerMonth()), 415000,
                Refining.MEDIUM_MIX, world);
        double[] dearNaphtha = world.clone();
        dearNaphtha[Good.NAPHTHA.ordinal()] = petrol;   // a reformer's spread below nothing: .82 + .08 x LPG < 1 x naphtha
        RefineryFlow.Flow idleH = RefineryFlow.solve(h, 415000, Refining.MEDIUM_MIX, dearNaphtha);
        assertTrue("with no reformer no hydrocracker runs, and with idle ones (a spread below nothing) none does: no hydrogen"
                        + " (star O4-3)", noH.run(Kind.HYDROCRACKER) == 0 && noH.hydrogen() == 0
                        && idleH.spread(Kind.REFORMER) < 0 && idleH.run(Kind.REFORMER) == 0 && idleH.run(Kind.HYDROCRACKER) == 0);

        /* ============================ 5. THE CUT ============================ */
        out.println("\n--- 5. the residue cut three to one with the diesel, and the rest burned ---");
        double[] heavy = Refining.mixOf(Deposit.Grade.HEAVY);
        RefineryFlow.Flow bare = RefineryFlow.solve(none, 8300, heavy, world);
        double res = bare.spare(Stream.RESIDUE) + bare.spare(Stream.HEAVY_RESIDUE), dsl = bare.spare(Stream.DIESEL);
        double cut = Math.min(res, Refining.RESIDUE_PER_DIESEL * dsl);
        report("on heavy crude with no unit the diesel cuts three times itself of residue into fuel oil, and the rest burns,"
                        + " to the bit", bare.burned() == res - cut && cut == Refining.RESIDUE_PER_DIESEL * dsl && bare.burned() > 0
                        && bare.of(Good.DIESEL) == Math.max(0, dsl - cut / Refining.RESIDUE_PER_DIESEL),
                String.format("%,.0f L of residue, %,.0f cut, %,.0f burned", res, cut, bare.burned()));
        BuildingsTemplate smallAsphalt = catalogue.getTemplateByName("Small Asphalt Unit"),
                smallCoker = catalogue.getTemplateByName("Small Coker");
        /*
         * A coker's spread is below nothing at the world's middle (section 2):
         * its residue's own value assumes diesel to cut it. So its fixtures
         * price as a city does that imports its diesel and ships its fuel oil
         * (spec-oil 2.4's cityValue()): diesel at its import price, fuel oil
         * at its export price, the rest at the middle.
         */
        double[] city = world.clone();
        city[Good.DIESEL.ordinal()] = Good.DIESEL.worldImportPrice();
        city[Good.FUEL_OIL.ordinal()] = Good.FUEL_OIL.worldExportPrice();
        RefineryFlow.Flow asphalt = RefineryFlow.solve(only(Kind.ASPHALT, smallAsphalt.feedPerMonth()), 8300, heavy, world);
        RefineryFlow.Flow coker = RefineryFlow.solve(only(Kind.COKER, smallCoker.feedPerMonth()), 8300, heavy, city);
        report("...an asphalt unit makes bitumen of residue the furnaces would have burned, and so does a coker its products"
                        + " (in a city importing its diesel)",
                asphalt.run(Kind.ASPHALT) > 0 && Math.abs(asphalt.burned() - Math.max(0, bare.burned() - asphalt.run(Kind.ASPHALT))) <= 1
                        && asphalt.of(Good.BITUMEN) > 0 && coker.spread(Kind.COKER) > 0 && coker.run(Kind.COKER) > 0
                        && coker.burned() < bare.burned() && coker.of(Good.COKE) > 0,
                String.format("burned %,.0f -> %,.0f L (asphalt), %,.0f L (coker)", bare.burned(), asphalt.burned(), coker.burned()));
        RefineryFlow.Flow light = RefineryFlow.solve(only(Kind.ASPHALT, smallAsphalt.feedPerMonth()), 8300,
                Refining.mixOf(Deposit.Grade.LIGHT), world);
        assertTrue("...but an asphalt unit takes heavy crude's residue only: on light crude it runs nothing [R17][R18]",
                light.run(Kind.ASPHALT) == 0 && light.of(Good.BITUMEN) == 0);

        /* ============================ 6. THE ORDER ============================ */
        out.println("\n--- 6. within a stream the widest spread takes first; at nothing or less, none ---");
        BuildingsTemplate smallLube = catalogue.getTemplateByName("Small Lube Plant"),
                smallCracker = catalogue.getTemplateByName("Small Cracking Unit");
        double[] vgo = new double[Kind.values().length];
        vgo[Kind.LUBE.ordinal()] = smallLube.feedPerMonth();
        vgo[Kind.CRACKER.ordinal()] = smallCracker.feedPerMonth();
        RefineryFlow.Flow gasOil = RefineryFlow.solve(vgo, 8300, Refining.MEDIUM_MIX, world);
        Kind wide = gasOil.spread(Kind.LUBE) > gasOil.spread(Kind.CRACKER) ? Kind.LUBE : Kind.CRACKER;
        Kind narrow = wide == Kind.LUBE ? Kind.CRACKER : Kind.LUBE;
        report("fixture: an Oil Refinery's gas oil, short of a Small Lube Plant and a Small Cracking Unit together",
                gasOil.cut(Stream.GAS_OIL) < vgo[Kind.LUBE.ordinal()] + vgo[Kind.CRACKER.ordinal()],
                String.format("%,.0f L against %,.0f", gasOil.cut(Stream.GAS_OIL), vgo[Kind.LUBE.ordinal()] + vgo[Kind.CRACKER.ordinal()]));
        report("the wider spread takes its feed first and the other what is left", gasOil.run(wide)
                        == Math.min(vgo[wide.ordinal()], gasOil.cut(Stream.GAS_OIL))
                        && gasOil.run(narrow) == Math.min(vgo[narrow.ordinal()], gasOil.cut(Stream.GAS_OIL) - gasOil.run(wide)),
                String.format("%s %,.0f L, %s %,.0f L", wide, gasOil.run(wide), narrow, gasOil.run(narrow)));
        double[] cheapLubes = world.clone();
        cheapLubes[Good.LUBRICANTS.ordinal()] = fuelOil;   // a lube plant's spread: nothing
        RefineryFlow.Flow noLube = RefineryFlow.solve(vgo, 8300, Refining.MEDIUM_MIX, cheapLubes);
        assertTrue("...a unit at a spread of nothing takes none, and the next takes the stream",
                noLube.spread(Kind.LUBE) == 0 && noLube.run(Kind.LUBE) == 0
                        && noLube.run(Kind.CRACKER) == Math.min(vgo[Kind.CRACKER.ordinal()], noLube.cut(Stream.GAS_OIL)));
        double[] res2 = new double[Kind.values().length];
        res2[Kind.ASPHALT.ordinal()] = smallAsphalt.feedPerMonth();
        res2[Kind.COKER.ordinal()] = 10 * smallCoker.feedPerMonth();
        RefineryFlow.Flow both = RefineryFlow.solve(res2, 8300, heavy, city);
        report("the heavy residue to the asphalt unit ahead of the coker while its spread is the wider, both above nothing"
                        + " (star O4-2)", both.spread(Kind.ASPHALT) > both.spread(Kind.COKER) && both.spread(Kind.COKER) > 0
                        && both.run(Kind.ASPHALT) == Math.min(res2[Kind.ASPHALT.ordinal()], both.cut(Stream.HEAVY_RESIDUE))
                        && both.run(Kind.ASPHALT) > 0,
                String.format("asphalt %+.4f, coker %+.4f $ a litre; asphalt %,.0f L, coker %,.0f L", both.spread(Kind.ASPHALT) * 1000,
                        both.spread(Kind.COKER) * 1000, both.run(Kind.ASPHALT), both.run(Kind.COKER)));
        // ...bitumen priced so the asphalt unit's spread is half the coker's: both run, the coker first.
        double[] cheapBitumen = city.clone();
        cheapBitumen[Good.BITUMEN.ordinal()] = (RefineryFlow.feedValue(Stream.RESIDUE, city)
                + RefineryFlow.spread(Kind.COKER, city) / 2) * perTonne / .95;
        RefineryFlow.Flow cokerFirst = RefineryFlow.solve(res2, 8300, heavy, cheapBitumen);
        assertTrue("...and the coker first when its spread is the wider: it takes the residue, the heavy part last, and the"
                        + " asphalt unit none", cokerFirst.spread(Kind.COKER) > cokerFirst.spread(Kind.ASPHALT)
                        && cokerFirst.spread(Kind.ASPHALT) > 0
                        && cokerFirst.run(Kind.COKER) == Math.min(res2[Kind.COKER.ordinal()],
                        cokerFirst.cut(Stream.RESIDUE) + cokerFirst.cut(Stream.HEAVY_RESIDUE))
                        && cokerFirst.run(Kind.ASPHALT) == 0);

        /* ============================ 7. THE REFINERY'S NAMEPLATE ============================ */
        town();

        /* ============================ 9-14. THE SPREAD PLANNER ============================ */
        cityValue();
        earnings(catalogue);
        gates(catalogue);
        theGameMoneyGates();
        fourRows(catalogue);
        idleThenShed();
        theInvestorsOrderAUnit();
        lubricantsCapped(catalogue);

        /* ============================ 16-19. THE RUN, THE PLANNER AT THE MARGIN, ONE ORDER (0.7.105) ============================ */
        theRun(catalogue);
        theRunInATown();
        thePlannerAtTheMargin(catalogue);
        oneOrderInFlight();

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** The units the town stands beside its Oil Refinery: one small one of every kind but the asphalt unit, which medium crude cannot feed. */
    static final String[] UNITS = { "Small Reformer", "Small Cracking Unit", "Small Hydrocracker", "Small Alkylation Unit",
            "Small Coker", "Small Lube Plant" };

    static void town() throws Exception {
        out.println("\n--- 7. the refinery's nameplate is the flow ---");
        GameFiles files = GameFiles.scratch("refinerycheck");
        Game g = new Game(files);
        double[] ground = new double[1];
        quietly(() -> {
            g.newGame();
            g.setCashForTest(Founding.WEALTHY_CASH);
            BuildingManager b = g.getBuildingManager();
            String[] names = { "House", "Convenience Store", "Small Grocery Store", "Industrial Bakery", "Paved Road",
                    "Coal Power Plant", "Water Treatment Plant", "Construction Depot", "Oil Refinery" };
            int[] counts = { 600, 13, 4, 4, 40, 2, 1, 4, 1 };
            for (int i = 0; i < names.length; i++) ground[0] += b.getTemplateByName(names[i]).getLandSqFt() * counts[i];
            for (String u : UNITS) ground[0] += 2 * b.getTemplateByName(u).getLandSqFt();
            g.getLandManager().setOwnedSqFt(LandManager.STARTING_SQ_FT + ground[0] * 1.25);
            // The refinery's sector is held: its subject here is what it makes, not whether the investors would build more.
            g.getBusinessInvestment().holdSector(Sectors.REFINING);
            for (int i = 0; i < names.length; i++) g.buildStack(b.getTemplateByName(names[i]), counts[i], true);
            for (String u : UNITS) g.buildStack(b.getTemplateByName(u), 1, true);
            g.simulateMonths(3);
        });
        Refining refiners = g.getSectors().refining();
        RefineryFlow.Flow f = refiners.flow();
        StringBuilder running = new StringBuilder();
        int runs = 0;
        for (Kind k : Kind.values()) {
            if (f.run(k) > 0) { runs++; running.append(k.name().toLowerCase()).append(' '); }
        }
        report("fixture: an Oil Refinery and six small units standing, the refinery's crude bought, units running", refiners
                        .getInputAtCapacity(Good.CRUDE) == 8300 && runs > 0,
                String.format("%d of 6 run: %s", runs, running.toString().trim()));
        double[] feed = new double[Kind.values().length];
        for (String u : UNITS) {
            BuildingsTemplate t = g.getBuildingManager().getTemplateByName(u);
            feed[t.refineryUnit().ordinal()] += g.getBuildingManager().getQuantity(t.getId()) * t.feedPerMonth();
        }
        RefineryFlow.Flow own = RefineryFlow.solve(feed, refiners.getInputAtCapacity(Good.CRUDE), refiners.getCrudeMix(),
                refiners.flowValues());
        boolean capacity = true, values = true;
        for (Good p : refiners.goodsMade()) {
            capacity &= refiners.getCapacity(p) == own.of(p) && refiners.getCapacity(p) == f.of(p);
            Sector.Output o = refiners.outputRow(p);
            capacity &= o == null || o.capacity == own.of(p);
            values &= refiners.flowValues()[p.ordinal()] == g.getMarkets().get(p).getLocalPrice();
        }
        report("its nameplate, product by product, is the flow of its own crude, mix and units, to the bit", capacity,
                String.format("petrol %,.0f L (the slate's %,.0f), lubricants %,.0f L", refiners.getCapacity(Good.PETROL),
                        Refining.slate(8300, refiners.getCrudeMix()).of(Good.PETROL), refiners.getCapacity(Good.LUBRICANTS)));
        assertTrue("...its values each product market's local price (star O4-1)", values);
        assertTrue("...and the flow is solved once for the same inputs: read twice, the same flow", refiners.flow() == f);

        // The units on site are its pipeline: the flow with them less the flow without.
        BuildingsTemplate reformer = g.getBuildingManager().getTemplateByName("Small Reformer");
        int onSite = g.getBuildingManager().getStack(reformer).getUnderConstruction();
        quietly(() -> {
            g.setCashForTest(g.getCash() + 1e9);
            g.buildStack(reformer, 1, false);
        });
        int after = g.getBuildingManager().getStack(reformer).getUnderConstruction();
        double[] with = feed.clone();
        with[Kind.REFORMER.ordinal()] += reformer.feedPerMonth();
        RefineryFlow.Flow future = RefineryFlow.solve(with, 8300, refiners.getCrudeMix(), refiners.flowValues());
        boolean pipe = true;
        for (Good p : refiners.goodsMade()) pipe &= refiners.getPipeline(p) == Math.max(0, future.of(p) - refiners.flow().of(p));
        report("the player orders a Small Reformer and it goes on site (spec-oil 5: the player may order units)", after == onSite + 1,
                String.format("%d on site", after));
        assertTrue("...and what it will add is the refinery's pipeline: the flow with it less the flow without, never below nothing",
                pipe);

        // The months: the audit closes, and nothing is written off - the lubricants a unit makes among it.
        double worst = 0, writtenOff = 0, lubes = 0;
        boolean closes = true, named = true;
        for (int m = 0; m < 12; m++) {
            quietly(() -> g.simulateMonths(1));
            MoneyAudit.Result r = g.getLastMoneyAudit();
            closes &= Math.abs(r.residual) <= MoneyAudit.tolerance(r.moved());
            worst = Math.max(worst, r.relative());
            for (Good p : refiners.goodsMade()) {
                Sector.Output o = refiners.outputRow(p);
                if (o != null) writtenOff += o.writtenOff;
            }
            Sector.Output lo = refiners.outputRow(Good.LUBRICANTS);
            if (lo != null) lubes += lo.produced + lo.exportBound;
            String word = g.getLastInvestment(Sectors.REFINING);
            for (String u : UNITS) named &= word == null || !word.contains(u);
        }
        report("a year: the audit closes every month", closes, String.format("worst %.2e of what moved", worst));
        report("...and nothing the refinery makes is written off - the lubricants only a unit makes among it, in their"
                + " tank share", writtenOff == 0 && lubes > 0 && refiners.getStockCapacity(Good.LUBRICANTS) > 0,
                String.format("written off %,.0f; lubricants %,.0f L made, %,.0f L of room", writtenOff, lubes,
                        refiners.getStockCapacity(Good.LUBRICANTS)));
        assertTrue("...and the investors' word names no unit (the sector is held)", named);

        // A saved city loads to the same flow.
        quietly(() -> g.saveGame(10, "refinery"));
        Game twin = new Game(files);
        quietly(() -> twin.loadGameSave(10));
        Refining back = twin.getSectors().refining();
        boolean same = back.flow().slate().equals(refiners.flow().slate());
        for (Kind k : Kind.values()) same &= back.flow().run(k) == refiners.flow().run(k);
        assertTrue("the city saved and loaded runs the same flow, every product and unit to the bit (nothing new saved)", same);
        quietly(() -> {
            twin.getBusinessInvestment().holdSector(Sectors.REFINING);
            g.simulateMonths(1);
            twin.simulateMonths(1);
        });
        boolean month = true;
        for (Good p : refiners.goodsMade()) month &= back.getCapacity(p) == refiners.getCapacity(p);
        assertTrue("...and a month on, the same nameplate", month);
    }

    /* ===================================================================== THE SPREAD PLANNER (0.7.82, batch O5) ===================================================================== */

    /** The prototype's frame (spread.py): wages a post-month, world $k (m4000's Refining, 736k over 200 posts at 760). */
    static final double PROTOTYPE_WAGE = 4.84;

    /** ...and its money test, a month on the whole cost (its RATE). */
    static final double PROTOTYPE_RATE = .005;

    /** ...and its Oil Refinery's capital: D$60M and its 1,000 materials at 18 (spread.py's TOPPING); every other building its cash. */
    static final double PROTOTYPE_REFINERY_CAPITAL = 78_000;

    /** ...and the months an order takes to open. */
    static final int PROTOTYPE_BUILD_MONTHS = 6;

    /** ...and the months it runs. */
    static final int PROTOTYPE_MONTHS = 240;

    /** The prototype's demands, litres a month of petrol and diesel: the playtest's city at m4000 (spread.py's cities). */
    static final double M4000_PETROL = 7.63e6, M4000_DIESEL = 16.13e6;

    /** ...and its wells in the 40-well rows: 40 land wells' 415 t a month. */
    static final double FORTY_WELLS = 40 * 415;

    /** The prototype's frame as the planner's City: its capital, its wages, no standing costs, nameplate, and its money test; `land` and `staff` refuse the buildings named. */
    static SpreadPlanner.City frame(String noLand, String noStaff) {
        return new SpreadPlanner.City() {
            @Override public double cost(BuildingsTemplate t) {
                return "Oil Refinery".equals(t.getName()) ? PROTOTYPE_REFINERY_CAPITAL : t.getCashCost();
            }
            @Override public double running(BuildingsTemplate t) { return t.getTotalJobs() * PROTOTYPE_WAGE; }
            @Override public double standing(BuildingsTemplate t) { return 0; }
            @Override public double rate() { return 1; }
            @Override public String land(BuildingsTemplate t) { return t.getName().equals(noLand) ? "no land - fixture" : null; }
            @Override public String staff(BuildingsTemplate t) { return t.getName().equals(noStaff) ? "no one could staff it - fixture" : null; }
            @Override public String money(double earns, double cost) {
                return earns > 0 && earns >= BusinessInvestment.PROFIT_OVER_INTEREST * PROTOTYPE_RATE * cost ? null : "under the hurdle";
            }
        };
    }

    /** The prototype's city_values: a product at its import price while the city wants more than it makes, its export price while it makes more, the middle otherwise. */
    static double[] prototypeValues(RefineryFlow.Flow f, double petrol, double diesel) {
        return RefineryFlow.values(g -> {
            double made = f == null ? 0 : f.of(g), want = g == Good.PETROL ? petrol : g == Good.DIESEL ? diesel : 0;
            return want > made + 1e-9 ? g.worldImportPrice() : made > want + 1e-9 ? g.worldExportPrice()
                    : (g.worldImportPrice() + g.worldExportPrice()) / 2;
        });
    }

    /** Two runs' mixes as one, each grade's share of the two. */
    static double[] blend(double[] a, double wa, double[] b, double wb) {
        double all = wa + wb;
        double[] m = new double[a.length];
        for (int i = 0; i < m.length; i++) m[i] = (wa > 0 ? a[i] * wa / all : 0) + (wb > 0 ? b[i] * wb / all : 0);
        return m;
    }

    /** The outlook of a city in the prototype's frame: its units and crude units standing, the wells' crude at their grade, its values settled in three passes. */
    static Refining.Outlook prototypeOutlook(double[] feed, double crude, double petrol, double diesel, double wells, double[] wellMix) {
        double local = Math.min(wells, crude);
        double[] mix = crude > 0 ? blend(wellMix, local, Refining.MEDIUM_MIX, crude - local) : Refining.MEDIUM_MIX;
        double[] v = prototypeValues(null, petrol, diesel);
        for (int i = 0; i < 3; i++) v = prototypeValues(RefineryFlow.solve(feed, crude, mix, v), petrol, diesel);
        RefineryFlow.Flow f = RefineryFlow.solve(feed, crude, mix, v);
        double room = Math.max(0, petrol - f.of(Good.PETROL)) + Math.max(0, diesel - f.of(Good.DIESEL));
        return new Refining.Outlook(feed.clone(), crude, mix, v, room, Math.max(0, wells - crude), wellMix,
                Good.CRUDE.worldExportPrice(), Good.CRUDE.worldImportPrice());
    }

    /**
     * The planner run PROTOTYPE_MONTHS months in the prototype's frame: an
     * order opens PROTOTYPE_BUILD_MONTHS on, one at a time; `beforeOpening`
     * decides each month on its state before the month's opening, as
     * the prototype's month (spread.py) does, else after it, as the game does. Returns
     * what stands at the end, by name.
     */
    static java.util.Map<String, Integer> prototypeRun(java.util.List<BuildingsTemplate> ts, double petrol, double diesel,
                                                     double wells, double[] wellMix, boolean beforeOpening) {
        double[] feed = new double[Kind.values().length];
        double[] crude = { 0 };
        java.util.Map<String, Integer> standing = new java.util.TreeMap<>();
        SpreadPlanner.City city = frame(null, null);
        BuildingsTemplate pending = null;
        int left = 0;
        for (int m = 0; m < PROTOTYPE_MONTHS; m++) {
            BuildingsTemplate opened = null;
            if (pending != null && --left <= 0) {
                opened = pending;
                pending = null;
            }
            if (opened != null && !beforeOpening) open(opened, feed, crude, standing);
            Refining.Outlook o = prototypeOutlook(feed, crude[0], petrol, diesel, wells, wellMix);
            if (opened != null && beforeOpening) open(opened, feed, crude, standing);
            if (pending == null) {
                SpreadPlanner.Candidate best = SpreadPlanner.best(Refining.appraise(o, ts, city));
                if (best != null) {
                    pending = best.template();
                    left = PROTOTYPE_BUILD_MONTHS;
                }
            }
        }
        return standing;
    }

    static void open(BuildingsTemplate t, double[] feed, double[] crude, java.util.Map<String, Integer> standing) {
        standing.merge(t.getName(), 1, Integer::sum);
        if (t.refineryUnit() != null) feed[t.refineryUnit().ordinal()] += t.feedPerMonth();
        else crude[0] += t.uses(Good.CRUDE);
    }

    static java.util.Map<String, Integer> row(Object... nameAndCount) {
        java.util.Map<String, Integer> m = new java.util.TreeMap<>();
        for (int i = 0; i < nameAndCount.length; i += 2) m.put((String) nameAndCount[i], (Integer) nameAndCount[i + 1]);
        return m;
    }

    static void cityValue() {
        out.println("\n--- 9. the city's own price ---");
        GoodsMarket bought = new GoodsMarket(Good.PETROL), sold = new GoodsMarket(Good.FUEL_OIL), neither = new GoodsMarket(Good.JET);
        for (GoodsMarket m : new GoodsMarket[] { bought, sold, neither }) {
            m.setExchangeRate(1.37);
            m.setRailCharge(.3);
            m.startMonth();
        }
        bought.record(Trade.WORLD, Trade.CITY, 1000, bought.importPrice());
        sold.record(Sectors.REFINING, Trade.WORLD, 1000, sold.exportPrice());
        neither.strike(500, 0, 200);
        report("a good the city imported any of this month is worth its net import price - landed and hauled - to the bit",
                SpreadPlanner.cityValue(bought) == bought.netImportPrice() && bought.netImportPrice() > bought.importPrice(),
                String.format("petrol %.7f against %.7f at the border", SpreadPlanner.cityValue(bought), bought.importPrice()));
        report("...one it exported, its net export price", SpreadPlanner.cityValue(sold) == sold.netExportPrice()
                        && sold.netExportPrice() < sold.exportPrice(),
                String.format("fuel oil %.7f against %.7f", SpreadPlanner.cityValue(sold), sold.exportPrice()));
        report("...and one it neither imported nor exported, its net export price - where its every unit goes (0.7.105, R1: at"
                        + " the margin; its local price, the band's middle, until then)", SpreadPlanner.cityValue(neither)
                        == neither.netExportPrice() && neither.getImported() == 0 && neither.getExported() == 0
                        && neither.netExportPrice() < neither.getLocalPrice(),
                String.format("jet fuel %.7f against %.7f here", SpreadPlanner.cityValue(neither), neither.getLocalPrice()));
        GoodsMarket kept = new GoodsMarket(Good.VANS);
        kept.setExchangeRate(1.37);
        kept.startMonth();
        kept.strike(10, 0, 4);
        report("...and one the world will not buy, its local price", !Good.VANS.exportable()
                        && SpreadPlanner.cityValue(kept) == kept.getLocalPrice() && kept.getLocalPrice() > 0,
                String.format("vans %.4f", SpreadPlanner.cityValue(kept)));
    }

    static void earnings(BuildingManager catalogue) {
        out.println("\n--- 10. a unit's earnings are its spread on the feed it would find spare ---");
        double[] world = worldMid();
        double[] two = new double[Kind.values().length];
        two[Kind.REFORMER.ordinal()] = catalogue.getTemplateByName("Small Reformer").feedPerMonth();
        two[Kind.HYDROCRACKER.ordinal()] = catalogue.getTemplateByName("Small Hydrocracker").feedPerMonth();
        RefineryFlow.Flow f = RefineryFlow.solve(two, 3 * 8300, Refining.mixOf(Deposit.Grade.HEAVY), world);
        final double running = 7, standing = 3, rate = .8;
        SpreadPlanner.City city = new SpreadPlanner.City() {
            @Override public double cost(BuildingsTemplate t) { return t.getCashCost(); }
            @Override public double running(BuildingsTemplate t) { return running; }
            @Override public double standing(BuildingsTemplate t) { return standing; }
            @Override public double rate() { return rate; }
            @Override public String land(BuildingsTemplate t) { return null; }
            @Override public String staff(BuildingsTemplate t) { return null; }
            @Override public String money(double earns, double cost) { return null; }
        };
        boolean exact = true, spare = true;
        int units = 0;
        for (BuildingsTemplate t : catalogue.getTemplates()) {
            if (!Refining.isConversionUnit(t)) continue;
            units++;
            Kind k = t.refineryUnit();
            double own = k == Kind.COKER ? f.spare(Stream.RESIDUE) + f.spare(Stream.HEAVY_RESIDUE)
                    : k == Kind.HYDROCRACKER ? Math.min(f.spare(Stream.GAS_OIL), f.spareHydrogen() / RefineryFlow.HYDROGEN_USED)
                    : f.spare(k.feed());
            spare &= Refining.spareFeed(f, k) == own;
            SpreadPlanner.Candidate c = Refining.unitEstimate(f, t, city);
            exact &= c.earns() == RefineryFlow.spread(k, world) * Math.min(own, t.feedPerMonth()) * rate - running - standing
                    && c.cost() == t.getCashCost();
        }
        report("fixture: three Oil Refineries' heavy crude, a Small Reformer and a Small Hydrocracker standing", f.run(Kind.HYDROCRACKER) > 0
                        && f.spare(Stream.HEAVY_RESIDUE) > 0,
                String.format("hydrogen %,.0f scf spare, %,.0f L of heavy residue", f.spareHydrogen(), f.spare(Stream.HEAVY_RESIDUE)));
        assertTrue("each kind finds its own stream spare: its feed's, the coker's the residue and the heavy residue, a hydrocracker's"
                + " no more gas oil than the spare hydrogen treats", spare);
        assertTrue("each unit earns its spread at the values on the feed it would find, at most its own, at the rate, less its running"
                + " and standing costs, to the bit (" + units + " units)", exact && units == 14);
    }

    static void gates(BuildingManager catalogue) {
        out.println("\n--- 11. each gate refuses on a fixture that causes it ---");
        java.util.List<BuildingsTemplate> ts = catalogue.getTemplatesBySector(Sectors.REFINING);
        double[] none = new double[Kind.values().length];
        // FEED: no crude unit standing - no stream for any unit - and the playtest's fuel all imported.
        Refining.Outlook bare = prototypeOutlook(none, 0, M4000_PETROL, M4000_DIESEL, 0, Refining.MEDIUM_MIX);
        java.util.List<SpreadPlanner.Candidate> all = Refining.appraise(bare, ts, frame(null, null));
        boolean unitsFed = false;
        for (SpreadPlanner.Candidate c : all) {
            if (Refining.isConversionUnit(c.template())) unitsFed |= c.failed() != SpreadPlanner.Gate.FEED;
        }
        assertTrue("with no crude unit standing no unit has a stream to take: every unit refused at its feed", !unitsFed);
        BuildingsTemplate refinery = catalogue.getTemplateByName("Oil Refinery");
        double makes = Refining.boughtHere(Refining.slate(refinery.uses(Good.CRUDE)));
        SpreadPlanner.City city = frame(null, null);
        RefineryFlow.Flow nothing = RefineryFlow.solve(none, 0, Refining.MEDIUM_MIX, bare.values());
        Refining.Outlook shy = new Refining.Outlook(none, 0, Refining.MEDIUM_MIX, bare.values(),
                Math.nextDown(SpreadPlanner.FEED_GATE * makes), 0, Refining.MEDIUM_MIX, Good.CRUDE.worldExportPrice(), Good.CRUDE.worldImportPrice());
        Refining.Outlook enough = new Refining.Outlook(none, 0, Refining.MEDIUM_MIX, bare.values(),
                SpreadPlanner.FEED_GATE * makes, 0, Refining.MEDIUM_MIX, Good.CRUDE.worldExportPrice(), Good.CRUDE.worldImportPrice());
        Refining.Outlook wells = new Refining.Outlook(none, 0, Refining.MEDIUM_MIX, bare.values(),
                0, SpreadPlanner.FEED_GATE * refinery.uses(Good.CRUDE), Refining.MEDIUM_MIX, Good.CRUDE.worldExportPrice(), Good.CRUDE.worldImportPrice());
        SpreadPlanner.Candidate a = Refining.packageEstimate(shy, nothing, refinery, ts, city),
                b = Refining.packageEstimate(enough, nothing, refinery, ts, city),
                c = Refining.packageEstimate(wells, nothing, refinery, ts, city);
        report("a crude unit is a candidate only while the city's petrol and diesel short on the trend are FEED_GATE of what it would make"
                        + " of them (rule 6): a hair under, refused at its feed; at it, not",
                a.failed() == SpreadPlanner.Gate.FEED && a.why().contains("none for another") && b.refusal(SpreadPlanner.Gate.FEED) == null,
                String.format("%,.0f L of %,.0f", SpreadPlanner.FEED_GATE * makes, makes));
        assertTrue("...or the wells' spare crude is FEED_GATE of what it runs (K's rule)", c.refusal(SpreadPlanner.Gate.FEED) == null);
        // LAND and STAFF: the frame refuses a building it is told to.
        Refining.Outlook light = prototypeOutlook(none, 0, M4000_PETROL, M4000_DIESEL, FORTY_WELLS, Refining.mixOf(Deposit.Grade.LIGHT));
        SpreadPlanner.Candidate open = SpreadPlanner.best(Refining.appraise(light, ts, frame(null, null)));
        java.util.List<SpreadPlanner.Candidate> noGround = Refining.appraise(light, ts, frame("Oil Refinery", null));
        java.util.List<SpreadPlanner.Candidate> noStaff = Refining.appraise(light, ts, frame(null, "Oil Refinery"));
        SpreadPlanner.Candidate blocked = SpreadPlanner.bestBlockedByLand(noGround);
        report("fixture: on its own light crude the playtest's city orders an Oil Refinery", open != null
                        && "Oil Refinery".equals(open.template().getName()),
                String.format("%s, %,.1fk a month on %,.0fk", open == null ? "nothing" : open.template().getName(),
                        open == null ? 0 : open.earns(), open == null ? 0 : open.cost()));
        assertTrue("with no ground for it, it is refused at the ground, ground its only refusal - the land office's case - and nothing"
                + " else is ordered", SpreadPlanner.best(noGround) == null && blocked != null && blocked.template() == refinery
                && blocked.failed() == SpreadPlanner.Gate.LAND && blocked.onlyLand());
        SpreadPlanner.Candidate staffed = null;
        for (SpreadPlanner.Candidate x : noStaff) if (x.template() == refinery) staffed = x;
        assertTrue("...with no one to staff it, refused at the staff, and nothing ordered", staffed != null
                && staffed.failed() == SpreadPlanner.Gate.STAFF && SpreadPlanner.best(noStaff) == null);
        // MONEY: the playtest's city on imported crude, and a unit standing at a spread of nothing.
        SpreadPlanner.Candidate imported = null;
        for (SpreadPlanner.Candidate x : all) if (x.template() == refinery) imported = x;
        report("on imported crude the playtest's city's Oil Refinery (with the units its cuts would feed) is refused at the money: it"
                        + " would lose money", imported != null && imported.failed() == SpreadPlanner.Gate.MONEY && imported.earns() < 0
                        && !imported.with().isEmpty(),
                String.format("%,.1fk a month on %,.0fk with %d unit(s)", imported == null ? 0 : imported.earns(),
                        imported == null ? 0 : imported.cost(), imported == null ? 0 : imported.with().size()));
        double hurdle = BusinessInvestment.PROFIT_OVER_INTEREST * PROTOTYPE_RATE * open.cost();
        SpreadPlanner.Candidate under = SpreadPlanner.judge(refinery, Math.nextDown(hurdle), open.cost(), null, city, null),
                at = SpreadPlanner.judge(refinery, hurdle, open.cost(), null, city, null);
        assertTrue("...and an order earning a hair under its hurdle is refused at the money, one earning it is not",
                under.failed() == SpreadPlanner.Gate.MONEY && at.passes());
    }

    static void theGameMoneyGates() throws Exception {
        /*
         * THE PREMISE DECISION A MOVED (0.7.112, batch I2; Jerus, 2026-10-08:
         * "test every order on its whole cost, whoever pays"). To 0.7.111 the
         * rule in force was ON_ITS_BORROWING, and this section asserted that
         * cash in the till asks only that the order earn something. Since then
         * the whole cost is tested whoever pays: the same hurdle with the
         * order's cost in the till as with nothing in it. The old rule stays,
         * named, for the counterfactual, and is asserted as such.
         */
        out.println("\n--- 11b. the game's money gate: on the order's whole cost, whoever pays (in force since 0.7.112, decision A);"
                + " on what it borrows, the rule to 0.7.111 ---");
        GameFiles files = GameFiles.scratch("refinerycheck-money");
        Game g = new Game(files);
        quietly(() -> {
            g.newGame();
            g.simulateMonths(2);
        });
        Refining r = g.getSectors().refining();
        BusinessInvestment plans = g.getBusinessInvestment();
        SpreadPlanner p = r.planner();
        BuildingsTemplate lube = g.getBuildingManager().getTemplateByName("Small Lube Plant");
        double cost = plans.getCostOf(lube, 1);
        BusinessDebtManager credit = g.getEconomyManager().getBusinessDebtManager();
        double rate = credit.projectRate(r.key(), cost);
        r.setCash(0);
        double h = p.hurdle(r, plans, g, cost);
        report("the rule in force (ON_ITS_WHOLE_COST, SpreadPlanner.DEFAULT_MONEY_GATE): with nothing in the till, the hurdle is"
                        + " PROFIT_OVER_INTEREST times a month's interest at the real rate on the whole cost, as servicesItsOwnDebt()"
                        + " strikes it, to the bit",
                p.moneyGate() == SpreadPlanner.ON_ITS_WHOLE_COST && SpreadPlanner.DEFAULT_MONEY_GATE == SpreadPlanner.ON_ITS_WHOLE_COST
                        && h == cost * plans.realTestRate(rate) / 12 * BusinessInvestment.PROFIT_OVER_INTEREST
                        && h > 0 && p.passesMoney(r, plans, g, h, cost) && !p.passesMoney(r, plans, g, Math.nextDown(h), cost)
                        && plans.servicesItsOwnDebt(h, cost, rate) && !plans.servicesItsOwnDebt(Math.nextDown(h), cost, rate),
                String.format("%,.2fk a month on %,.0fk, %.4f%% (rate %.4f, real %.4f)", h, cost, 100 * h / cost, rate, plans.realTestRate(rate)));
        r.setCash(cost);
        double withCash = p.hurdle(r, plans, g, cost);
        boolean wholeWhoeverPays = withCash == h && !p.passesMoney(r, plans, g, Math.nextDown(h), cost) && p.passesMoney(r, plans, g, h, cost);
        p.setMoneyGate(SpreadPlanner.ON_ITS_BORROWING);
        boolean cashPays = p.hurdle(r, plans, g, cost) == 0 && p.passesMoney(r, plans, g, 1e-9, cost) && !p.passesMoney(r, plans, g, 0, cost);
        p.setMoneyGate(null);
        report("...and with the order's cost in the till the same hurdle, to the bit: the whole cost is tested whoever pays, so a"
                        + " cash-rich sector's order earning a hair under it is refused", wholeWhoeverPays,
                String.format("%,.2fk with %,.0fk in the till", withCash, r.getCash()));
        assertTrue("the rule to 0.7.111, kept as a named history (ON_ITS_BORROWING): with the order's cost in the till it asked nothing"
                + " but that the order earn something (Game.consider()'s own test) - and null puts the default back", cashPays
                && p.moneyGate() == SpreadPlanner.DEFAULT_MONEY_GATE);
    }

    static void fourRows(BuildingManager catalogue) {
        out.println("\n--- 12. the package reproduces spec-oil 2.4's four rows, in the prototype's frame ---");
        java.util.List<BuildingsTemplate> ts = catalogue.getTemplatesBySector(Sectors.REFINING);
        double[] medium = Refining.MEDIUM_MIX, light = Refining.mixOf(Deposit.Grade.LIGHT), heavy = Refining.mixOf(Deposit.Grade.HEAVY);
        double bigP = 10 * M4000_PETROL, bigD = 10 * M4000_DIESEL;
        java.util.Map<String, Integer> big = prototypeRun(ts, bigP, bigD, 0, medium, true),
                m4000 = prototypeRun(ts, M4000_PETROL, M4000_DIESEL, 0, medium, true),
                own = prototypeRun(ts, M4000_PETROL, M4000_DIESEL, FORTY_WELLS, light, true),
                ownHeavy = prototypeRun(ts, M4000_PETROL, M4000_DIESEL, FORTY_WELLS, heavy, true);
        report("a city with 10x the playtest's fuel, on imported crude: 2 Crude Units, 2 Cracking Units, 2 Alkylation Units and 10"
                        + " Lube Plants", big.equals(row("Crude Unit", 2, "Cracking Unit", 2, "Alkylation Unit", 2, "Lube Plant", 10)),
                big.toString());
        report("...the playtest's city (m4000), on imported crude: nothing", m4000.isEmpty(), m4000.toString());
        report("...on 40 wells of its own light crude: 2 Oil Refineries and 3 Small Lube Plants",
                own.equals(row("Oil Refinery", 2, "Small Lube Plant", 3)), own.toString());
        report("...on its own heavy crude: nothing", ownHeavy.isEmpty(), ownHeavy.toString());
        /*
         * ★ O5-2: spread.py decides each month on the state before the month's
         * opening, so the month an order opens it orders again for the stream
         * the opening has just taken. The game decides on the state after it.
         * Run so, the planner orders what spread.py does when it is changed to
         * decide so (scratch-o5/py/spread_fresh.py): one of each it ordered twice.
         */
        java.util.Map<String, Integer> bigAfter = prototypeRun(ts, bigP, bigD, 0, medium, false),
                ownAfter = prototypeRun(ts, M4000_PETROL, M4000_DIESEL, FORTY_WELLS, light, false);
        report("deciding as the game does, on the month's state after its opening: the 10x city 1 Crude Unit, 1 Cracking Unit, 1"
                        + " Alkylation Unit, 4 Lube Plants, 2 Cokers and 2 Small Lube Plants (spread.py so decided)",
                bigAfter.equals(row("Crude Unit", 1, "Cracking Unit", 1, "Alkylation Unit", 1, "Lube Plant", 4, "Coker", 2,
                        "Small Lube Plant", 2)), bigAfter.toString());
        report("...and on its own light crude 1 Oil Refinery and 1 Small Lube Plant; on imported or its own heavy crude still nothing",
                ownAfter.equals(row("Oil Refinery", 1, "Small Lube Plant", 1))
                        && prototypeRun(ts, M4000_PETROL, M4000_DIESEL, 0, medium, false).isEmpty()
                        && prototypeRun(ts, M4000_PETROL, M4000_DIESEL, FORTY_WELLS, heavy, false).isEmpty(), ownAfter.toString());
    }

    /** The town the planner sections stand: the section 7 town's houses and works, an Oil Refinery and `units`; Refining held or not. */
    static Game plannerTown(String label, boolean held, String... units) {
        GameFiles files = GameFiles.scratch(label);
        Game g = new Game(files);
        PLANNER_FILES.put(g, files);
        quietly(() -> {
            g.newGame();
            g.setCashForTest(Founding.WEALTHY_CASH);
            BuildingManager b = g.getBuildingManager();
            String[] names = { "House", "Convenience Store", "Small Grocery Store", "Industrial Bakery", "Paved Road",
                    "Coal Power Plant", "Water Treatment Plant", "Construction Depot", "Oil Refinery" };
            int[] counts = { 600, 13, 4, 4, 40, 2, 1, 4, 1 };
            double ground = 0;
            for (int i = 0; i < names.length; i++) ground += b.getTemplateByName(names[i]).getLandSqFt() * counts[i];
            for (String u : units) ground += b.getTemplateByName(u).getLandSqFt();
            // ...and ground for four of the largest unit more, so a unit the planner orders has somewhere to stand.
            ground += 4 * b.getTemplateByName("Coker").getLandSqFt();
            g.getLandManager().setOwnedSqFt(LandManager.STARTING_SQ_FT + ground * 1.25);
            if (held) g.getBusinessInvestment().holdSector(Sectors.REFINING);
            for (int i = 0; i < names.length; i++) g.buildStack(b.getTemplateByName(names[i]), counts[i], true);
            for (String u : units) g.buildStack(b.getTemplateByName(u), 1, true);
        });
        return g;
    }

    /** Each planner town's save folder (plannerTown()), for the sections that save one and load its twin. */
    static final java.util.Map<Game, GameFiles> PLANNER_FILES = new java.util.IdentityHashMap<>();

    static void idleThenShed() throws Exception {
        out.println("\n--- 13. only a kind idle for six months is shed ---");
        Game g = plannerTown("refinerycheck-idle", true, "Small Asphalt Unit", "Small Lube Plant");
        Refining r = g.getSectors().refining();
        BuildingManager b = g.getBuildingManager();
        BuildingsTemplate asphalt = b.getTemplateByName("Small Asphalt Unit"), lube = b.getTemplateByName("Small Lube Plant"),
                refinery = b.getTemplateByName("Oil Refinery");
        boolean counts = true, before = true;
        for (int m = 1; m <= SpreadPlanner.IDLE_MONTHS; m++) {
            quietly(() -> g.simulateMonths(1));
            counts &= r.idleMonths(Kind.ASPHALT) == m && r.idleMonths(Kind.LUBE) == 0;
            if (m < SpreadPlanner.IDLE_MONTHS) {
                before &= !r.mayRetire(asphalt) && !r.mayRetire(lube) && r.mayRetire(refinery) && r.retirementDemandAndCapacity(g) == null;
            }
        }
        report("fixture: medium crude has no heavy residue, so the Small Asphalt Unit takes none, and the Small Lube Plant runs",
                r.flow().run(Kind.ASPHALT) == 0 && r.flow().run(Kind.LUBE) > 0 && r.getCrudeMix()[Deposit.Grade.MEDIUM.ordinal()] == 1,
                String.format("lube %,.0f L, asphalt %,.0f L", r.flow().run(Kind.LUBE), r.flow().run(Kind.ASPHALT)));
        assertTrue("each month it stands idle the asphalt unit's kind counts one more, and the working lube plant's none", counts);
        assertTrue("...under IDLE_MONTHS the refiners may sell neither unit (the crude unit, the distress rule's, they may), and the"
                + " spare-capacity rule has no measure", before);
        double[] measure = r.retirementDemandAndCapacity(g);
        assertTrue("at IDLE_MONTHS the asphalt unit may be sold and the lube plant may not, nor the crude unit while a kind idles",
                r.mayRetire(asphalt) && !r.mayRetire(lube) && !r.mayRetire(refinery));
        report("...the spare-capacity rule's measure is nothing used against the idle kind's feed", measure != null && measure[0] == 0
                        && measure[1] == asphalt.feedPerMonth() && r.unitsOf(asphalt) == asphalt.feedPerMonth(),
                measure == null ? "none" : String.format("{%,.0f, %,.0f L}", measure[0], measure[1]));
        BusinessInvestment plans = g.getBusinessInvestment();
        for (int i = 0; i < BusinessInvestment.RETIREMENT_LOSS_MONTHS; i++) plans.recordSectorResult(r.key(), -1);
        BusinessInvestment.Decision d = plans.planRetirement(r, measure[0], measure[1], 0);
        report("...so, losing money six months, the rule sells the asphalt unit and nothing else", d.build && d.template == asphalt
                        && d.quantity == 1, d.reason);
        // The idle months cross a save.
        GameFiles files = PLANNER_FILES.get(g);
        quietly(() -> g.saveGame(10, "idle"));
        Game twin = new Game(files);
        quietly(() -> twin.loadGameSave(10));
        Refining back = twin.getSectors().refining();
        boolean same = true;
        for (Kind k : Kind.values()) same &= back.idleMonths(k) == r.idleMonths(k);
        assertTrue("the idle months cross a save, every kind (extras idleMonths.<KIND>)", same && back.idleMonths(Kind.ASPHALT)
                == SpreadPlanner.IDLE_MONTHS);
    }

    static void theInvestorsOrderAUnit() throws Exception {
        out.println("\n--- 14. the investors order a unit, and Game.consider() tests the planner's estimate ---");
        Game g = plannerTown("refinerycheck-plan", false);
        // ...its orders asked on the rule to 0.7.112 (0.7.113, I2b): on the whole cost no unit here pays (11b), and the section is the order's path.
        g.setOrderGate(SpreadPlanner.ON_ITS_BORROWING);
        Refining r = g.getSectors().refining();
        BusinessInvestment plans = g.getBusinessInvestment();
        BuildingManager b = g.getBuildingManager();
        /*
         * ...ON THE WELLS' CRUDE (0.7.105, batch P6): until then the town's
         * Oil Refinery ran on imported crude by lorry and lost on every
         * barrel; since the run rule (Refining, THE RUN) such a refinery
         * stands idle, and the planner weighs a unit at the run it would
         * take with it standing - none, so nothing is ordered (section 16).
         * A unit is ordered behind crude units whose barrels pay: so the town
         * lifts its own crude, past what the refinery runs, and its barrel
         * pays at the wells' price (OilCheck 4's thirty wells).
         */
        BuildingsTemplate well = b.getTemplateByName("Oil Well");
        quietly(() -> {
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + TOWN_WELLS * well.getLandSqFt());
            g.getLandManager().restoreSites(Resource.OIL, TOWN_WELLS, 50_000_000);
            g.buildStack(well, TOWN_WELLS, true);
        });
        /*
         * Watched where Game.consider() weighs the refiners' order, before it
         * builds: the planner's best on the city as it then stands, the
         * estimate consider() read, and the same plan with no ground free
         * (BusinessInvestment's land as refreshLand() sets it, then put back).
         */
        Object[] seen = new Object[6];
        g.watchOrders(new BusinessInvestment.OrderWatch() {
            @Override public void invested(BusinessInvestment.Decision d, double cash, double perUnitProfit, Game.Afford found) {
                if (seen[0] != null || !Sectors.REFINING.equals(d.sector)) return;
                Refining.Outlook o = r.outlook(plans);
                java.util.List<SpreadPlanner.Candidate> all = Refining.appraise(o, b.getTemplatesBySector(r.key()),
                        r.planner().city(r, plans, g));
                boolean values = true;
                for (Good p : new Good[] { Good.LPG, Good.NAPHTHA, Good.JET, Good.FUEL_OIL, Good.LUBRICANTS }) {
                    values &= o.values()[p.ordinal()] == SpreadPlanner.cityValue(g.getMarkets().get(p));
                }
                // ...petrol and diesel at the net import price while the city imports any on the trend, else the net export price (R1).
                for (Good p : Refining.BOUGHT_HERE) {
                    GoodsMarket m = g.getMarkets().get(p);
                    values &= o.values()[p.ordinal()] == (r.importsOf(p) > 0 ? m.netImportPrice() : m.netExportPrice())
                            && o.imports()[p.ordinal()] == r.importsOf(p) && o.away()[p.ordinal()] == m.netExportPrice();
                }
                values &= o.run() != null;
                plans.setLandAvailable(0, g.getLandManager().getPricePerSqFt());
                BusinessInvestment.Decision noGround = r.plan(plans, g);
                plans.setLandAvailable(g.getLandManager().getAvailableSqFt(), g.getLandManager().getPricePerSqFt());
                seen[0] = d;
                seen[1] = perUnitProfit;
                seen[2] = SpreadPlanner.best(all);
                seen[3] = values;
                seen[4] = noGround;
                seen[5] = found.quantity();
            }
        });
        /*
         * ...AND THE COST OF ANY UNIT IN THEIR TILL (0.7.103, batch P4): until
         * then the town's order was a Small Lube Plant, its lubricants all
         * shipped; the world's demand for them capped (Jerus's A9; section
         * 15), none is wanted on this crude, and the best unit left earns
         * too little to pay the interest on what it would borrow. With what
         * one costs in hand the money gate asked only that it earn something
         * (11b), so the order was still the planner's to place.
         *
         * ...AND THE RULE TO 0.7.111 ITS GATE (0.7.112, batch I2): since
         * Jerus's decision A the gate in force tests the whole cost whoever
         * pays (11b), and in this town no unit earns that - its best, a Small
         * Cracking Unit, $0.4k a month on $30.2M against $161k, measured at
         * 0.7.112 - so the cash
         * in the till no longer orders one. This section is the order's path
         * (the planner's values, its best, Game.consider()'s estimate, the
         * ground), not the gate, so its fixture keeps the lever it was built
         * on: the refiners' planner on ON_ITS_BORROWING, the named history,
         * with a unit's cost in their till; put back after.
         */
        r.planner().setMoneyGate(SpreadPlanner.ON_ITS_BORROWING);
        double most = 0;
        for (BuildingsTemplate t : b.getTemplatesBySector(r.key())) {
            if (Refining.isConversionUnit(t)) most = Math.max(most, plans.getCostOf(t, 1));
        }
        final double till = most;
        int[] months = { 0 };
        while (seen[0] == null && months[0] < 24) {
            r.setCash(Math.max(r.getCash(), till));
            quietly(() -> g.simulateMonths(1));
            months[0]++;
        }
        g.watchOrders(null);
        boolean onTheOldRule = r.planner().moneyGate() == SpreadPlanner.ON_ITS_BORROWING;
        r.planner().setMoneyGate(null);
        BusinessInvestment.Decision d = (BusinessInvestment.Decision) seen[0];
        SpreadPlanner.Candidate best = (SpreadPlanner.Candidate) seen[2];
        report("fixture: an Oil Refinery standing on its wells' crude in a town of 600 houses (0.7.105; on imported crude until"
                        + " then), the refiners not held, the cost of any unit in their till (0.7.103) and their planner on the"
                        + " rule to 0.7.111 (ON_ITS_BORROWING; on the whole cost no unit here pays, 11b), orders within two years",
                onTheOldRule && d != null && r.runShare() == 1 && r.inputRow(Good.CRUDE) != null && r.inputRow(Good.CRUDE).imported == 0,
                d == null ? "no order" : String.format("m%d: %s", g.getMonth(), d.reason));
        assertTrue("the planner's values are each product at the city's own price at the margin, petrol and diesel at the net import"
                + " price only while the city imports them (0.7.105, R1), and the run's prices with them, to the bit",
                d != null && (Boolean) seen[3]);
        report("the order is the planner's best, a conversion unit, one of it", d != null && best != null && d.template == best.template()
                        && d.quantity == 1 && Refining.isConversionUnit(best.template()),
                best == null ? "none" : String.format("%s, %,.1fk a month on %,.0fk (%.3f%% a month)", best.template().getName(),
                        best.earns(), best.cost(), 100 * best.score()));
        assertTrue("...what Game.consider() tested it on is the planner's earnings for it (estimatedMonthlyProfit())",
                d != null && best != null && (Double) seen[1] == best.earns());
        BusinessInvestment.Decision noGround = (BusinessInvestment.Decision) seen[4];
        report("...with no ground free the same month it is refused at the ground and the land office hears of it (landBlocked)",
                noGround != null && !noGround.build && noGround.landBlocked && noGround.reason.startsWith("no land"),
                noGround == null ? "" : noGround.reason);
        report("...and with ground the order went on site", d != null && (Integer) seen[5] == 1
                        && b.getStack(d.template).getUnderConstruction() + b.getQuantity(d.template.getId()) >= 1,
                g.getLastInvestment(Sectors.REFINING));
        BuildingsTemplate crude = b.getTemplateByName("Crude Unit");
        SpreadPlanner.Candidate pkg = r.appraise(crude, plans);
        assertTrue("a crude unit's estimate is its share of its package's earnings, by cost", pkg.cost() > 0
                && r.estimatedMonthlyProfit(crude, plans) == pkg.earns() * plans.getCostOf(crude, 1) / pkg.cost());
    }

    /* ============================ 15. THE WORLD'S DEMAND FOR LUBRICANTS (0.7.103) ============================ */

    static void lubricantsCapped(BuildingManager catalogue) throws Exception {
        out.println("\n--- 15. the world takes a city's lubricants up to LUBRICANTS_WORLD_SHARE of its crude, and no more (0.7.103) ---");
        double[] world = worldMid();
        double[] none = new double[Kind.values().length];
        BuildingsTemplate small = catalogue.getTemplateByName("Small Lube Plant"), refinery = catalogue.getTemplateByName("Oil Refinery");
        double crude = refinery.uses(Good.CRUDE), litres = crude * Refining.CRUDE_LITRES_PER_TONNE;
        RefineryFlow.Flow f = RefineryFlow.solve(none, crude, Refining.MEDIUM_MIX, world);
        assertTrue("the world takes LUBRICANTS_WORLD_SHARE of a crude run's litres - 1%, the real share [R37] - and nothing of none",
                Refining.LUBRICANTS_WORLD_SHARE == .01 && Refining.lubricantsWorldTakes(litres) == .01 * litres
                        && Refining.lubricantsWorldTakes(0) == 0 && f.crude() == litres);
        double home = 20_000;
        assertTrue("...the room past a flow's own lubricants: the world's share of its crude and the city's own, less what it makes;"
                        + " in the prototype's frame none is capped",
                Refining.lubricantsRoom(f, home) == Refining.lubricantsWorldTakes(litres) + home - f.of(Good.LUBRICANTS)
                        && Refining.lubricantsRoom(f, Double.POSITIVE_INFINITY) == Double.POSITIVE_INFINITY);
        double perLitre = Refining.lubricantsPerLitreOfFeed(), plant = small.feedPerMonth() * perLitre;
        SpreadPlanner.City city = frame(null, null);
        SpreadPlanner.Candidate free = Refining.unitEstimate(f, small, city), alone = Refining.unitEstimate(f, small, city, 0);
        report("fixture: an Oil Refinery's gas oil would feed a Small Lube Plant in the prototype's frame, and the world's share of its"
                        + " crude is under FEED_GATE of the plant's lubricants",
                free.refusal(SpreadPlanner.Gate.FEED) == null && perLitre == .40
                        && Refining.lubricantsWorldTakes(litres) < SpreadPlanner.FEED_GATE * plant,
                String.format("%,.0f L the world takes against %,.0f L a plant makes", Refining.lubricantsWorldTakes(litres), plant));
        report("with no lubricants bought in the city the Small Lube Plant is refused at its feed, in the lubricants' words",
                alone.failed() == SpreadPlanner.Gate.FEED && alone.why().contains("lubricants the world and the city would still take"),
                alone.why());
        SpreadPlanner.Candidate wanted = Refining.unitEstimate(f, small, city, plant);
        assertTrue("...and with a plant's worth bought in the city it is weighed as in the prototype's frame: the same earnings",
                wanted.refusal(SpreadPlanner.Gate.FEED) == null && wanted.earns() == free.earns());
        java.util.List<BuildingsTemplate> ts = catalogue.getTemplatesBySector(Sectors.REFINING);
        Refining.Outlook bare = prototypeOutlook(none, 0, M4000_PETROL, M4000_DIESEL, 0, Refining.MEDIUM_MIX);
        Refining.Outlook capped = new Refining.Outlook(bare.feed(), bare.crude(), bare.mix(), bare.values(), bare.room(),
                bare.spareCrude(), bare.spareMix(), bare.crudeLocal(), bare.crudeImport(), 0);
        RefineryFlow.Flow nothing = RefineryFlow.solve(none, 0, Refining.MEDIUM_MIX, bare.values());
        SpreadPlanner.Candidate open = Refining.packageEstimate(bare, nothing, refinery, ts, city),
                shut = Refining.packageEstimate(capped, nothing, refinery, ts, city);
        boolean lubeOpen = false, lubeShut = false;
        for (BuildingsTemplate u : open.with()) lubeOpen |= u.refineryUnit() == Kind.LUBE;
        for (BuildingsTemplate u : shut.with()) lubeShut |= u.refineryUnit() == Kind.LUBE;
        report("an Oil Refinery's package takes a lube plant in the prototype's frame and none where the world and the city take"
                        + " no more lubricants than its crude's share", lubeOpen && !lubeShut,
                names(open.with()) + "; capped: " + names(shut.with()));

        // In a town: an Oil Refinery and a Small Lube Plant standing, the refiners held.
        Game g = plannerTown("refinerycheck-lubricants", true, "Small Lube Plant");
        Refining r = g.getSectors().refining();
        double worst = 0, made = 0, shipped = 0, ran = 0, idled = 0;
        boolean within = true;
        for (int m = 0; m < 12; m++) {
            quietly(() -> g.simulateMonths(1));
            worst = Math.max(worst, g.getLastMoneyAudit().relative());
            Sector.Output o = r.outputRow(Good.LUBRICANTS);
            Sector.Input c = r.inputRow(Good.CRUDE);
            double run = (Math.max(0, c.boughtLocal) + Math.max(0, c.imported)) * Refining.CRUDE_LITRES_PER_TONNE;
            within &= o.exportBound <= Refining.lubricantsWorldTakes(run) * (1 + 1e-12);
            made += o.produced + o.exportBound + o.idled;
            shipped += o.exportBound;
            idled += o.idled;
            ran += run;
        }
        report("fixture: the Small Lube Plant runs, its lubricants past what the world takes",
                made > 0 && made > Refining.lubricantsWorldTakes(ran) && idled > 0,
                String.format("its line %.2f%% of the crude run, the world's share %.0f%%", 100 * made / ran,
                        100 * Refining.LUBRICANTS_WORLD_SHARE));
        report("the ship's line takes no more lubricants than the world's share of the month's crude, every month; the rest idles",
                within && shipped <= Refining.lubricantsWorldTakes(ran) * (1 + 1e-12),
                String.format("shipped %.2f%% of the crude, idled %,.0f L", 100 * shipped / ran, idled));
        report("...and the audit closes every month", worst < 1e-10, String.format("worst %.2e", worst));
    }

    /* ============================ 16. THE RUN (0.7.105) ============================ */

    /** Wells the town of section 14 stands under its Oil Refinery (OilCheck 4's thirty): more crude than it runs, its barrel paying at their price. */
    static final int TOWN_WELLS = 30;

    /** The study's margins (the project's why-the-refinery-loses.md, on 0.7.99), a share of crude's world middle: a topping barrel of medium crude on imported crude, all exported, by lorry, by rail at .60 and at the floor, and by tanker; and by tanker through two Cracking Units, two Alkylation Units, two Reformers and a Hydrocracker behind one Crude Unit. */
    static final double STUDY_LORRY = -.100, STUDY_RAIL = -.047, STUDY_FLOOR = -.007, STUDY_TANKER = .023, STUDY_CONVERTED = .093;

    /** ...and the barrels the city's own petrol and diesel take, at the net import price: at the floor, and at .60. */
    static final double STUDY_HOME_FLOOR = .014, STUDY_HOME_RAIL = -.017;

    /** The study's railway above its floor: a quote of .60 of a lorry's rate. */
    static final double STUDY_QUOTE = .60;

    /** The study's figures are to a tenth of a point, and its tanker's +2.3% is +2.25% here: within six hundredths. */
    static final double STUDY_ROUNDING = .0006;

    /** Jerus's campus: five Crude Units and an Oil Refinery - the study's 500,000 b/d topping plant. */
    static final double HIS_CRUDE = 5 * 415_000 + 8_300;

    /** The nine products, as Refining lists them. */
    static final Good[] NINE = { Good.PETROL, Good.DIESEL, Good.LPG, Good.NAPHTHA, Good.JET, Good.FUEL_OIL, Good.LUBRICANTS,
            Good.BITUMEN, Good.COKE };

    /** How the oil moves in a fixture: by lorry, all by rail at a quote, or by sea. */
    enum Way { LORRY, RAIL, SEA }

    /** A good's market as it moves one way (exchange 1): the band's share of a lorry's freight and the railway's charge, as sectors.Rail.haul() sets them (Ports.factor()). */
    static GoodsMarket moved(Good g, Way way, double quote) {
        GoodsMarket m = new GoodsMarket(g);
        m.setExchangeRate(1);
        m.setFreightFactor(way == Way.LORRY ? 1 : way == Way.RAIL ? Ports.factor(1, 0, 0)
                : Ports.factor(0, 1, Ports.seaFreightShareOf(g)));
        m.setRailCharge(way == Way.RAIL ? quote : 0);
        return m;
    }

    /** The run's prices, every good moved one way: petrol and diesel's `petrol` and `diesel` litres at the net import price, the rest of every product at its net export price, the wells' `wells` tonnes at crude's export price there and the rest imported at its net import price; nothing else a tonne. */
    static Refining.RunPrices movedPrices(Way way, double quote, double petrol, double diesel, double lubricants, double wells) {
        int n = Good.values().length;
        double[] home = new double[n], away = new double[n], take = new double[n];
        java.util.Arrays.fill(home, Double.NaN);
        for (Good g : NINE) {
            GoodsMarket m = moved(g, way, quote);
            away[g.ordinal()] = m.netExportPrice();
            if (g == Good.PETROL || g == Good.DIESEL) home[g.ordinal()] = m.netImportPrice();
        }
        take[Good.PETROL.ordinal()] = petrol;
        take[Good.DIESEL.ordinal()] = diesel;
        GoodsMarket crude = moved(Good.CRUDE, way, quote);
        return new Refining.RunPrices(home, away, take, lubricants, wells, crude.exportPrice(), crude.netImportPrice(), 0);
    }

    /** Crude's world middle: what the study's margins are a share of. */
    static double crudeMiddle() { return (Good.CRUDE.worldImportPrice() + Good.CRUDE.worldExportPrice()) / 2; }

    /** A verdict's barrel - `share`, a share of what its crude cost - as a share of crude's world middle. */
    static double ofMiddle(double share, double crudeCost) { return share * crudeCost / crudeMiddle(); }

    static void theRun(BuildingManager catalogue) {
        out.println("\n--- 16. the run: a crude unit runs a barrel only while it pays, at what its products fetch (0.7.105) ---");
        double[] none = new double[Kind.values().length];
        RefineryFlow.Flow topping = RefineryFlow.solve(none, HIS_CRUDE, Refining.MEDIUM_MIX, worldMid());
        double crudeImport = moved(Good.CRUDE, Way.LORRY, 0).netImportPrice();
        Refining.RunVerdict lorry = Refining.runRule(topping, movedPrices(Way.LORRY, 0, 0, 0, 0, 0)),
                rail = Refining.runRule(topping, movedPrices(Way.RAIL, STUDY_QUOTE, 0, 0, 0, 0)),
                floor = Refining.runRule(topping, movedPrices(Way.RAIL, ham.citybuildersim.sectors.Rail.RAIL_FLOOR, 0, 0, 0, 0)),
                sea = Refining.runRule(topping, movedPrices(Way.SEA, 0, 0, 0, 0, 0));
        double mLorry = ofMiddle(lorry.first(), crudeImport),
                mRail = ofMiddle(rail.first(), moved(Good.CRUDE, Way.RAIL, STUDY_QUOTE).netImportPrice()),
                mFloor = ofMiddle(floor.first(), moved(Good.CRUDE, Way.RAIL, ham.citybuildersim.sectors.Rail.RAIL_FLOOR).netImportPrice()),
                mSea = ofMiddle(sea.first(), moved(Good.CRUDE, Way.SEA, 0).netImportPrice());
        report("THE STUDY'S MARGINS, from the model's own prices and freight: a topping barrel of medium crude on imported crude,"
                        + " all exported - by lorry -10.0%, by rail at .60 -4.7%, at the floor -0.7%, by tanker +2.3% of crude's"
                        + " world middle, each to the study's tenth of a point",
                Math.abs(mLorry - STUDY_LORRY) <= STUDY_ROUNDING && Math.abs(mRail - STUDY_RAIL) <= STUDY_ROUNDING
                        && Math.abs(mFloor - STUDY_FLOOR) <= STUDY_ROUNDING && Math.abs(mSea - STUDY_TANKER) <= STUDY_ROUNDING,
                String.format("%+.2f%%, %+.2f%%, %+.2f%%, %+.2f%%", 100 * mLorry, 100 * mRail, 100 * mFloor, 100 * mSea));
        double hand = -crudeImport;
        Refining.RunPrices lp = movedPrices(Way.LORRY, 0, 0, 0, 0, 0);
        for (Good g : NINE) hand += topping.of(g) / HIS_CRUDE * lp.away()[g.ordinal()];
        assertTrue("...a barrel's margin is its products a tonne at what they fetch less its crude, to the bit",
                Math.abs(lorry.first() * crudeImport - hand) <= 1e-12 * crudeImport);
        assertTrue("THE RUN: none of it by lorry or by rail at either quote - the first barrel loses - and the whole of it by"
                        + " tanker, every barrel paying",
                lorry.share() == 0 && rail.share() == 0 && floor.share() == 0 && lorry.stop() == Refining.RunStop.FIRST
                        && rail.stop() == Refining.RunStop.FIRST && floor.stop() == Refining.RunStop.FIRST
                        && sea.share() == 1 && sea.stop() == Refining.RunStop.NAMEPLATE && sea.last() > 0);

        // ...through his conversion units, by tanker.
        double[] his = new double[Kind.values().length];
        his[Kind.CRACKER.ordinal()] = 2 * catalogue.getTemplateByName("Cracking Unit").feedPerMonth();
        his[Kind.ALKYLATION.ordinal()] = 2 * catalogue.getTemplateByName("Alkylation Unit").feedPerMonth();
        his[Kind.REFORMER.ordinal()] = 2 * catalogue.getTemplateByName("Reformer").feedPerMonth();
        his[Kind.HYDROCRACKER.ordinal()] = catalogue.getTemplateByName("Hydrocracker").feedPerMonth();
        Refining.RunPrices sp = movedPrices(Way.SEA, 0, 0, 0, 0, 0);
        RefineryFlow.Flow converted = RefineryFlow.solve(his, HIS_CRUDE, Refining.MEDIUM_MIX, sp.away()),
                behindOne = RefineryFlow.solve(his, catalogue.getTemplateByName("Crude Unit").uses(Good.CRUDE), Refining.MEDIUM_MIX, sp.away());
        Refining.RunVerdict conv = Refining.runRule(converted, sp), one = Refining.runRule(behindOne, sp);
        double seaCrude = moved(Good.CRUDE, Way.SEA, 0).netImportPrice();
        double mConv = ofMiddle(conv.first(), seaCrude), mOne = ofMiddle(one.first(), seaCrude);
        report("...through conversion units (two Cracking Units, two Alkylation Units, two Reformers, a Hydrocracker) by tanker,"
                        + " behind one Crude Unit +9.3% (the study's) and behind Jerus's 2,083,300 t less, each run whole",
                Math.abs(mOne - STUDY_CONVERTED) <= STUDY_ROUNDING && mConv > mSea && mOne > mConv && conv.share() == 1 && one.share() == 1,
                String.format("%+.2f%% and %+.2f%% of crude's world middle", 100 * mOne, 100 * mConv));

        // THE HOME MARKET: the city takes a third of the run's petrol and two thirds of its diesel; at .60 none pays.
        double third = HIS_CRUDE / 3;
        double petrolTake = topping.of(Good.PETROL) / HIS_CRUDE * third, dieselTake = topping.of(Good.DIESEL) / HIS_CRUDE * third;
        Refining.RunVerdict home = Refining.runRule(topping, movedPrices(Way.RAIL, ham.citybuildersim.sectors.Rail.RAIL_FLOOR,
                petrolTake, 2 * dieselTake, 0, 0));
        double floorCrude = moved(Good.CRUDE, Way.RAIL, ham.citybuildersim.sectors.Rail.RAIL_FLOOR).netImportPrice();
        double mHome = ofMiddle(home.first(), floorCrude);
        report("THE HOME MARKET AT THE FLOOR: the barrels whose petrol and diesel the city takes, at the net import price, pay"
                        + " (+1.4%, the study's); the run takes them, and the barrels past the city's diesel - at the export"
                        + " price - it does not (-0.7%)",
                Math.abs(mHome - STUDY_HOME_FLOOR) <= STUDY_ROUNDING && home.stop() == Refining.RunStop.DIESEL
                        && Math.abs(home.share() * HIS_CRUDE - 2 * third) <= 1e-9 * HIS_CRUDE
                        && home.last() > 0 && Math.abs(home.next() - floor.first()) <= 1e-12,
                String.format("%+.2f%% at home; runs %.4f of nameplate, stops past the diesel at %+.2f%%", 100 * mHome,
                        home.share(), 100 * ofMiddle(home.next(), floorCrude)));
        Refining.RunVerdict homeRail = Refining.runRule(topping, movedPrices(Way.RAIL, STUDY_QUOTE, petrolTake, 2 * dieselTake, 0, 0));
        double mHomeRail = ofMiddle(homeRail.first(), moved(Good.CRUDE, Way.RAIL, STUDY_QUOTE).netImportPrice());
        report("...at .60 even the barrels that replace the city's imports lose (-1.7%, the study's): it runs none",
                Math.abs(mHomeRail - STUDY_HOME_RAIL) <= STUDY_ROUNDING && homeRail.share() == 0
                        && homeRail.stop() == Refining.RunStop.FIRST,
                String.format("%+.2f%%", 100 * mHomeRail));

        // THE WELLS: their crude at its export price there, the rest imported.
        double wells = HIS_CRUDE / 4;
        Refining.RunVerdict own = Refining.runRule(topping, movedPrices(Way.RAIL, STUDY_QUOTE, 0, 0, 0, wells));
        report("THE WELLS' CRUDE: at its export price the barrel pays where an imported one does not; the run stops where the"
                        + " wells' crude runs out", own.stop() == Refining.RunStop.WELLS && Math.abs(own.share() - wells / HIS_CRUDE) <= 1e-12
                        && own.last() > 0 && own.next() < 0,
                String.format("runs %.4f; the wells' barrel %+.2f%%, an imported one %+.2f%% of its crude", own.share(),
                        100 * own.last(), 100 * own.next()));

        // THE WORLD'S LUBRICANTS: an Oil Refinery's gas oil through a Small Lube Plant, the city taking half the run's past the world's share.
        double[] lube = only(Kind.LUBE, catalogue.getTemplateByName("Small Lube Plant").feedPerMonth());
        RefineryFlow.Flow lf = RefineryFlow.solve(lube, 8300, Refining.MEDIUM_MIX, worldMid());
        double perTonne = lf.of(Good.LUBRICANTS) / 8300, world = Refining.lubricantsWorldTakes(Refining.CRUDE_LITRES_PER_TONNE);
        double homeLubes = (perTonne - world) * 8300 / 2;
        Refining.RunVerdict lv = Refining.runRule(lf, movedPrices(Way.RAIL, ham.citybuildersim.sectors.Rail.RAIL_FLOOR, 0, 0, homeLubes, 0));
        report("THE WORLD'S LUBRICANTS: past what the world and the city take a lube plant's lubricants fetch nothing, and the run"
                        + " stops there", perTonne > world && lv.stop() == Refining.RunStop.LUBRICANTS
                        && Math.abs(lv.share() - .5) <= 1e-9 && lv.last() > 0 && lv.next() < 0,
                String.format("%.1f L a tonne against the world's %.1f; runs %.4f", perTonne, world, lv.share()));

        // THE PACE.
        assertTrue("THE PACE: the run moves at most RUN_PACE of nameplate a month - MAX_RETIREMENT_FRACTION, a quarter - and"
                        + " never past nothing or nameplate",
                Refining.RUN_PACE == BusinessInvestment.MAX_RETIREMENT_FRACTION && Refining.paced(1, 0) == 1 - Refining.RUN_PACE
                        && Refining.paced(0, 1) == Refining.RUN_PACE && Refining.paced(.5, .6) == .6 && Refining.paced(.1, 0) == 0
                        && Refining.paced(.9, 1) == 1);
        Refining.RunVerdict empty = Refining.runRule(RefineryFlow.solve(none, 0, Refining.MEDIUM_MIX, worldMid()), lp);
        assertTrue("...and with no crude unit there is no run, and no barrel to weigh", empty.share() == 0 && Double.isNaN(empty.first())
                && Refining.runWords(empty, 1).isEmpty());
    }

    /* ============================ 17. THE RUN IN A TOWN (0.7.105) ============================ */

    static void theRunInATown() throws Exception {
        out.println("\n--- 17. the run in a town: struck at the month's end, at the pace, the rate times it; spare once idle"
                + " six months, and sold (0.7.105) ---");
        Game g = plannerTown("refinerycheck-run", false);
        Game held = plannerTown("refinerycheck-run-held", true);
        Refining r = g.getSectors().refining(), h = held.getSectors().refining();
        BuildingManager b = g.getBuildingManager();
        BuildingsTemplate refinery = b.getTemplateByName("Oil Refinery"), farm = b.getTemplateByName("Tank Farm"),
                lube = b.getTemplateByName("Small Lube Plant");
        double nameplate = r.getInputAtCapacity(Good.CRUDE);
        boolean paced = true, rate = true, bought = true, made = true, stopped = true;
        double worst = 0;
        String path = "";
        for (int m = 1; m <= 4; m++) {
            double before = r.runShare();
            quietly(() -> g.simulateMonths(1));
            quietly(() -> held.simulateMonths(1));
            worst = Math.max(worst, g.getLastMoneyAudit().relative());
            Refining.RunVerdict v = r.runVerdict();
            stopped &= v.share() == 0 && v.stop() == Refining.RunStop.FIRST;
            paced &= r.runShare() == Refining.paced(before, v.share()) && r.monthsRun() == before;
            rate &= r.getOperatingRate() == r.plantRate() * r.runShare();
            Sector.Input in = r.inputRow(Good.CRUDE);
            double crude = in == null ? 0 : Math.max(0, in.boughtLocal) + Math.max(0, in.imported);
            bought &= Math.abs(crude - nameplate * r.monthsRate()) <= 1e-9 * nameplate;
            Sector.Output o = r.outputRow(Good.DIESEL);
            double out = o == null ? 0 : o.produced + o.exportBound + o.idled;
            made &= Math.abs(out - r.getCapacity(Good.DIESEL) * r.monthsRate()) <= 1e-9 * Math.max(1, r.getCapacity(Good.DIESEL));
            path += String.format("%.2f ", r.runShare());
        }
        report("fixture: an Oil Refinery on imported crude by lorry, the refiners not held: every month's verdict is that no"
                + " barrel pays", stopped, Refining.runWords(r.runVerdict(), r.runShare()));
        report("the month's end moves the run toward it RUN_PACE of nameplate a month, and the month ran at the share it was",
                paced && r.runShare() == 0, "after each month: " + path.trim());
        assertTrue("...the operating rate is the plant's times the run, to the bit", rate);
        assertTrue("...the crude units buy nameplate at that rate, and make each product's nameplate at it", bought && made);
        report("...and the audit closes every month", worst < 1e-10, String.format("worst %.2e", worst));
        report("A HELD REFINERS' RUN STAYS WHERE THE FIXTURE LEFT IT (BusinessInvestment.holdSector())", h.runShare() == 1
                && h.runVerdict().share() == 0, String.format("%.2f", h.runShare()));
        // The run crosses a save.
        GameFiles files = PLANNER_FILES.get(g);
        quietly(() -> g.saveGame(10, "run"));
        Game twin = new Game(files);
        quietly(() -> twin.loadGameSave(10));
        Refining back = twin.getSectors().refining();
        boolean same = back.runShare() == r.runShare();
        quietly(() -> g.simulateMonths(2));
        quietly(() -> twin.simulateMonths(2));
        same &= back.runShare() == r.runShare() && Double.compare(back.runUsed(), r.runUsed()) == 0 && r.runUsed() == 1;
        assertTrue("the run crosses a save, its share and its months (extras run.share, run.history.<n>)", same);
        boolean said = false, why = false;
        for (Sector.Line l : r.ownLines(g)) {
            said |= "Crude run".equals(l.label()) && l.value().startsWith("0") && l.tone() == Sector.Line.Tone.BAD;
            why |= l.kind() == Sector.Line.Kind.NOTE && l.label().equals(Refining.runWords(r.runVerdict(), r.runShare()))
                    && l.label().startsWith("No barrel pays");
        }
        RefineryView.View pic = RefineryView.of(g);
        report("THE WORDS: Refining's page says the run and why; the refinery's picture, of a month that ran nothing, why",
                said && why && !pic.drawn() && RefineryView.emptyWords(pic).contains("No barrel pays"), RefineryView.emptyWords(pic));

        // THE SPARE, SOLD: idle IDLE_MONTHS months, losing money, the spare-capacity rule sells the crude unit.
        double[] measure = null;
        boolean before = true;
        int months = 6;
        while (b.getQuantity(refinery.getId()) > 0 && months < 24) {
            if (r.retirementDemandAndCapacity(g) != null && measure == null) {
                measure = r.retirementDemandAndCapacity(g);
                before &= r.mayRetire(refinery) && !r.mayRetire(farm) && !r.mayRetire(lube);
            }
            quietly(() -> g.simulateMonths(1));
            months++;
        }
        report("THE SPARE: once the run has stood under nameplate IDLE_MONTHS months the spare-capacity rule's measure is the most"
                        + " it used in them against nameplate, in tonnes; the crude unit may be sold, a Tank Farm or a working unit not",
                measure != null && measure[0] == (1 - Refining.RUN_PACE) * nameplate && measure[1] == nameplate && before,
                measure == null ? "none" : String.format("{%,.0f, %,.0f t}", measure[0], measure[1]));
        report("...and the rule sells it, losing money, the month after six at nothing - where what is spare is a whole unit - and"
                        + " not the distress rule, which waits DISTRESS_LOSS_MONTHS",
                b.getQuantity(refinery.getId()) == 0 && months == 4 + SpreadPlanner.IDLE_MONTHS + 1
                        && g.getBusinessInvestment().getLossMonths(Sectors.REFINING) < BusinessInvestment.DISTRESS_LOSS_MONTHS,
                String.format("sold in month %d", months));
    }

    /* ============================ 18. THE PLANNER'S PRICES AT THE MARGIN (0.7.105) ============================ */

    static void thePlannerAtTheMargin(BuildingManager catalogue) {
        out.println("\n--- 18. the planner's prices at the margin: petrol and diesel past the city's imports at the export price,"
                + " an order weighed at the run it would take (0.7.105) ---");
        double[] none = new double[Kind.values().length];
        BuildingsTemplate cracking = catalogue.getTemplateByName("Small Cracking Unit"), reformer = catalogue.getTemplateByName("Small Reformer");
        SpreadPlanner.City city = frame(null, null);
        Refining.RunPrices lorry = movedPrices(Way.LORRY, 0, 0, 0, 0, 0), sea = movedPrices(Way.SEA, 0, 0, 0, 0, 0);
        double[] endless = new double[Good.values().length];
        java.util.Arrays.fill(endless, Double.POSITIVE_INFINITY);
        Refining.Outlook proto = new Refining.Outlook(none, 8300, Refining.MEDIUM_MIX, lorry.away(), 0, 0, Refining.MEDIUM_MIX,
                0, lorry.crudeImport(), Double.POSITIVE_INFINITY);
        Refining.Outlook stopped = new Refining.Outlook(none, 8300, Refining.MEDIUM_MIX, lorry.away(), 0, 0, Refining.MEDIUM_MIX,
                0, lorry.crudeImport(), Double.POSITIVE_INFINITY, lorry.away(), endless, lorry);
        Refining.Outlook paying = new Refining.Outlook(none, 8300, Refining.MEDIUM_MIX, sea.away(), 0, 0, Refining.MEDIUM_MIX,
                0, sea.crudeImport(), Double.POSITIVE_INFINITY, sea.away(), endless, sea);
        RefineryFlow.Flow lf = RefineryFlow.solve(none, 8300, Refining.MEDIUM_MIX, lorry.away()),
                sf = RefineryFlow.solve(none, 8300, Refining.MEDIUM_MIX, sea.away());
        SpreadPlanner.Candidate free = Refining.unitEstimate(proto, lf, cracking, city), idle = Refining.unitEstimate(stopped, lf, cracking, city),
                runs = Refining.unitEstimate(paying, sf, cracking, city), plain = Refining.unitEstimate(sf, cracking, city);
        report("fixture: a Small Cracking Unit's spread is above nothing at the export prices, on an Oil Refinery's gas oil",
                lf.spread(Kind.CRACKER) > 0 && free.refusal(SpreadPlanner.Gate.FEED) == null,
                String.format("%s a litre", RefineryView.spreadWords(lf.spread(Kind.CRACKER))));
        report("AN ORDER IS WEIGHED AT THE RUN IT WOULD TAKE: behind an Oil Refinery on imported crude by lorry - no barrel pays,"
                        + " with the unit or without - it earns nothing, its wages and its standing lost, and the money gate refuses it",
                idle.earns() == -city.running(cracking) - city.standing(cracking) && idle.failed() == SpreadPlanner.Gate.MONEY
                        && free.earns() > idle.earns(),
                String.format("%,.1f a month; in the prototype's frame, every barrel run, %,.1f", idle.earns(), free.earns()));
        assertTrue("...by tanker, every barrel paying, it is weighed as in the prototype's frame, its spread on its feed, to the bit",
                runs.earns() == plain.earns() && runs.passes() == plain.passes());
        // Petrol past the city's imports: a Small Reformer, the city importing half the petrol it would make.
        RefineryFlow.Flow vf0 = RefineryFlow.solve(none, 8300, Refining.MEDIUM_MIX, sea.away());
        double takes = Math.min(Refining.spareFeed(vf0, Kind.REFORMER), reformer.feedPerMonth());
        double makes = RefineryFlow.madeAlone(Kind.REFORMER, takes).get(Good.PETROL);
        double[] imports = endless.clone();
        imports[Good.PETROL.ordinal()] = makes / 2;
        double[] values = sea.away().clone();
        values[Good.PETROL.ordinal()] = sea.home()[Good.PETROL.ordinal()];
        Refining.Outlook half = new Refining.Outlook(none, 8300, Refining.MEDIUM_MIX, values, 0, 0, Refining.MEDIUM_MIX,
                0, sea.crudeImport(), Double.POSITIVE_INFINITY, sea.away(), imports, sea);
        Refining.Outlook all = new Refining.Outlook(none, 8300, Refining.MEDIUM_MIX, values, 0, 0, Refining.MEDIUM_MIX,
                0, sea.crudeImport(), Double.POSITIVE_INFINITY, sea.away(), endless, sea);
        RefineryFlow.Flow vf = RefineryFlow.solve(none, 8300, Refining.MEDIUM_MIX, values);
        SpreadPlanner.Candidate past = Refining.unitEstimate(half, vf, reformer, city), within = Refining.unitEstimate(all, vf, reformer, city);
        double lost = makes / 2 * (values[Good.PETROL.ordinal()] - sea.away()[Good.PETROL.ordinal()]);
        report("PETROL PAST THE CITY'S IMPORTS AT THE EXPORT PRICE: a Small Reformer's petrol, the city importing half of it,"
                        + " is worth the net import price on that half and the net export price on the rest, to the bit",
                makes > 0 && Math.abs((within.earns() - past.earns()) - lost) <= 1e-9 * Math.max(1, lost) && lost > 0,
                String.format("%,.0f L of petrol; %,.2f a month less", makes, lost));
    }

    /* ============================ 19. ONE ORDER IN FLIGHT, CRUDE UNITS ON SITE ASIDE (0.7.105) ============================ */

    static void oneOrderInFlight() throws Exception {
        out.println("\n--- 19. one order in flight, crude units on site aside: the city's Crude Unit on site does not hold the"
                + " refiners' planner (0.7.105) ---");
        Game g = plannerTown("refinerycheck-r3", true);
        Refining r = g.getSectors().refining();
        BuildingManager b = g.getBuildingManager();
        BusinessInvestment plans = g.getBusinessInvestment();
        BuildingsTemplate crude = b.getTemplateByName("Crude Unit"), lube = b.getTemplateByName("Small Lube Plant");
        Game.BuildResult[] put = new Game.BuildResult[2];
        quietly(() -> {
            g.setCashForTest(Founding.WEALTHY_CASH * 100);
            put[0] = g.buildStack(crude, 1, false);
            g.simulateMonths(1);
        });
        BuildingsStacks site = b.getStack(crude);
        report("fixture: the city's own Crude Unit on site beside the town's Oil Refinery, nothing else of the refiners'",
                put[0] == Game.BuildResult.SUCCESS && site != null && site.getUnderConstruction() == 1 && site.isCitysOwn()
                        && b.getUnderConstructionBySector(Sectors.REFINING) == 1, String.valueOf(put[0]));
        BusinessInvestment.Decision d = r.plan(plans, g);
        report("THE PLANNER WEIGHS ITS UNITS: not \"already building\" (MAX_CONCURRENT_ORDERS counted it until 0.7.105), and no"
                        + " other crude unit", !"already building".equals(d.reason) && (d.template == null || !Refining.isCrudeUnit(d.template)),
                d.reason);
        quietly(() -> {
            put[1] = g.buildStack(lube, 1, false);
        });
        BusinessInvestment.Decision held = r.plan(plans, g);
        report("...and with a conversion unit on site as well it holds to one order: \"already building\"",
                put[1] == Game.BuildResult.SUCCESS && "already building".equals(held.reason), held.reason);
        assertTrue("...while the spare-capacity rule still reads a refiners' site as building, not shrinking",
                !plans.planRetirement(r, 0, 1e9, b.getUnderConstructionBySector(Sectors.REFINING)).build);
    }

    /** The buildings a package was weighed with, by name. */
    static String names(java.util.List<BuildingsTemplate> with) {
        java.util.List<String> n = new java.util.ArrayList<>();
        for (BuildingsTemplate t : with) n.add(t.getName());
        return n.toString();
    }
}
