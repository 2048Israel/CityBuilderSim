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
 *      market imported any this month, its net export price while it
 *      exported any, its local price otherwise (SpreadPlanner.cityValue()).
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
 *      gate in force tests what an order borrows - with nothing in the till
 *      the whole cost, PROFIT_OVER_INTEREST times the interest at the real
 *      rate, as servicesItsOwnDebt() strikes it, to the bit; with the cost in
 *      the till nothing but that it earn something - and the stricter rule
 *      (spec-materials A) the whole cost whoever pays.
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
 *  14. THE INVESTORS ORDER A UNIT: a town with an Oil Refinery and nothing
 *      held orders the planner's best, a conversion unit; Game.consider()
 *      tests it on the planner's earnings; with no ground free it is refused
 *      at the ground and the land office hears of it; a crude unit's
 *      estimate is its share of its package's by cost.
 */
public class RefineryCheck {

    static int fails = 0;
    static final PrintStream out = System.out;
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
        report("...and one it neither imported nor exported, its local price", SpreadPlanner.cityValue(neither)
                        == neither.getLocalPrice() && neither.getImported() == 0 && neither.getExported() == 0,
                String.format("jet fuel %.7f", SpreadPlanner.cityValue(neither)));
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
        out.println("\n--- 11b. the game's money gates: on what the order borrows (in force), or on its whole cost ---");
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
        report("the rule in force (ON_ITS_BORROWING): with nothing in the till, the hurdle is PROFIT_OVER_INTEREST times a month's"
                        + " interest at the real rate on the whole cost, as servicesItsOwnDebt() strikes it, to the bit",
                p.moneyGate() == SpreadPlanner.ON_ITS_BORROWING && h == cost * plans.realTestRate(rate) / 12 * BusinessInvestment.PROFIT_OVER_INTEREST
                        && h > 0 && p.passesMoney(r, plans, g, h, cost) && !p.passesMoney(r, plans, g, Math.nextDown(h), cost)
                        && plans.servicesItsOwnDebt(h, cost, rate) && !plans.servicesItsOwnDebt(Math.nextDown(h), cost, rate),
                String.format("%,.2fk a month on %,.0fk, %.4f%% (rate %.4f, real %.4f)", h, cost, 100 * h / cost, rate, plans.realTestRate(rate)));
        r.setCash(cost);
        boolean cashPays = p.hurdle(r, plans, g, cost) == 0 && p.passesMoney(r, plans, g, 1e-9, cost) && !p.passesMoney(r, plans, g, 0, cost);
        p.setMoneyGate(SpreadPlanner.ON_ITS_WHOLE_COST);
        double whole = p.hurdle(r, plans, g, cost);
        boolean wholeRefuses = whole == h && !p.passesMoney(r, plans, g, Math.nextDown(whole), cost) && p.passesMoney(r, plans, g, whole, cost);
        p.setMoneyGate(null);
        assertTrue("...with the order's cost in the till it asks nothing but that the order earn something (Game.consider()'s own test)",
                cashPays);
        assertTrue("the stricter rule (ON_ITS_WHOLE_COST, spec-materials A) tests the whole cost whoever pays: it refuses a cash-rich"
                + " sector's order the rule in force passes", wholeRefuses && p.moneyGate() == SpreadPlanner.ON_ITS_BORROWING);
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
        Refining r = g.getSectors().refining();
        BusinessInvestment plans = g.getBusinessInvestment();
        BuildingManager b = g.getBuildingManager();
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
                for (Good p : new Good[] { Good.PETROL, Good.DIESEL, Good.LPG, Good.NAPHTHA, Good.JET, Good.FUEL_OIL, Good.LUBRICANTS }) {
                    values &= o.values()[p.ordinal()] == SpreadPlanner.cityValue(g.getMarkets().get(p));
                }
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
        int[] months = { 0 };
        while (seen[0] == null && months[0] < 24) {
            quietly(() -> g.simulateMonths(1));
            months[0]++;
        }
        g.watchOrders(null);
        BusinessInvestment.Decision d = (BusinessInvestment.Decision) seen[0];
        SpreadPlanner.Candidate best = (SpreadPlanner.Candidate) seen[2];
        report("fixture: an Oil Refinery standing on imported crude in a town of 600 houses, the refiners not held, orders within"
                        + " two years", d != null, d == null ? "no order" : String.format("m%d: %s", g.getMonth(), d.reason));
        assertTrue("the planner's values are each product at the city's own price, to the bit", d != null && (Boolean) seen[3]);
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
}
