package ham.citybuildersim.sectors;

import ham.citybuildersim.Deposit;
import ham.citybuildersim.Good;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;

/**
 * The refinery as a campus of units, and a month's flow through them (0.7.80,
 * batch O4; runs/spec-oil.md 2.3): the crude units' run cut into its streams,
 * the conversion units taking their feed stream by stream, and what is left
 * of each stream sold as the product it is worth with no unit to upgrade it.
 *
 * WHY. Until 0.7.80 a refinery was only its crude units (Refining, THE
 * SLATE): a barrel of medium crude made 70 L of petrol, 169 L of diesel and
 * a fuel oil worth less than the crude, and nothing could turn the cheap
 * cuts into dear ones. A real refinery is a crude unit and the units behind
 * it - a reformer, a cracking unit, a hydrocracker, alkylation, a coker, a
 * lube plant, an asphalt unit - each taking one cut and making more of the
 * products the city wants (the research's 1.2). Each is a building here
 * (ids 77 to 90, a small and a large of each kind; BuildingsTemplate's
 * refineryUnit() and feedPerMonth()), and this is what they do together.
 *
 * PURE. solve() reads only its arguments - each kind's feed a month, the
 * crude, its mix of grades and the products' values - so Refining caches it
 * on those (Refining.flow()) and a check can call it with any it likes.
 *
 * THE PROTOTYPE'S, PORTED (scratch-oil's spread.py, its solve()), in its
 * order, so that with no conversion unit the flow is Refining.slate() of the
 * same crude and mix to the bit (RefineryCheck asserts it):
 *   1. the crude's litres split into the cuts, grade by grade in
 *      Deposit.Grade's order, the heavy crude's residue kept apart;
 *   2. hydrogen: the reformers' make on the straight-run heavy naphtha, at
 *      HYDROGEN_MADE a barrel, which caps the hydrocrackers at
 *      HYDROGEN_USED a barrel (Q12: no other hydrogen);
 *   3. the streams in flow order - gas oil, residue, heavy naphtha, cracked
 *      gas - each taken by its units in order of spread, the widest first; a
 *      unit at a spread of nothing or less takes none and idles; what a unit
 *      makes of another unit's feed (cracked gas, heavy naphtha, diesel,
 *      gas) joins that stream, so it may be taken further down the order;
 *   4. what is left of each stream to its product: gas and cracked gas to
 *      PETROLEUM GAS, light naphtha to PETROL, heavy naphtha to NAPHTHA,
 *      kerosene to JET, diesel to DIESEL, gas oil to FUEL_OIL;
 *   5. the residue to FUEL_OIL only when cut three to one with the pool's
 *      diesel (Q5), and what the diesel cannot cut burned at no value.
 *
 * ★ O4-2: THE RESIDUE IS ONE STREAM. The prototype solved the coker's
 * residue and then, apart, the asphalt units' heavy residue, so a coker took
 * heavy residue ahead of an asphalt unit whatever their spreads - and at the
 * world's middle prices the asphalt unit's is the wider (its bitumen is worth
 * $0.559 a litre of residue, the coker's products $0.392). The spec's rule
 * is that within a stream the units take feed in order of spread, the heavy
 * part kept apart: so the coker and the asphalt units share one stream in
 * spread order, the coker taking the rest of the residue before the heavy
 * part and an asphalt unit the heavy part only.
 *
 * ★ O4-3: AN IDLE REFORMER MAKES NO HYDROGEN. The prototype counted the
 * reformers' hydrogen from their capacity whether they ran or not; here it
 * is counted only when they run (a spread above nothing), as they then take
 * at least the straight-run naphtha the hydrogen was counted on.
 *
 * Litres throughout, but for the coke and the bitumen (tonnes, at the
 * residue's RESIDUE_LITRES_PER_TONNE): the crude's tonnes are turned into
 * litres at Refining.CRUDE_LITRES_PER_TONNE, as the slate turns them.
 */
public final class RefineryFlow {

    private RefineryFlow() { }

    /** A barrel's hydrogen a reformer makes, in standard cubic feet, the middle of the research's 1.3 range [R6]. */
    public static final double HYDROGEN_MADE = 1400;

    /** ...and a barrel of gas oil a hydrocracker uses [R7]. */
    public static final double HYDROGEN_USED = 1850;

    /** Litres in a tonne of residue: fuel oil's [P35] (Good.FUEL_OIL), what the coke's and the bitumen's weights are struck at. */
    public static final double RESIDUE_LITRES_PER_TONNE = 1010;

    /** A litre a month of a barrel a day: 30.44 days of 158.987 L - how the units' sizes in barrels a day become their feed in litres (spec-oil 2.3). */
    public static final double LITRES_A_MONTH_PER_BARREL_A_DAY = 30.44 * 158.987;

    /**
     * The streams a crude unit's run is cut into and the units pass between
     * them. The first seven are the straight-run cuts in the slate's CUT_
     * order (Refining.CUT_GAS to CUT_RESIDUE); HEAVY_RESIDUE is the heavy
     * crude's residue kept apart (only an asphalt unit can make bitumen of
     * it, [R17][R18]), and CRACKED_GAS the olefin-rich gas a cracking unit
     * and a coker make, the only feed alkylation takes [R6].
     */
    public enum Stream {
        GAS("refinery gas", Good.LPG),
        LIGHT_NAPHTHA("light naphtha", Good.PETROL),
        HEAVY_NAPHTHA("heavy naphtha", Good.NAPHTHA),
        KEROSENE("kerosene", Good.JET),
        DIESEL("diesel", Good.DIESEL),
        GAS_OIL("gas oil", Good.FUEL_OIL),
        RESIDUE("residue", null),
        HEAVY_RESIDUE("heavy-crude residue", null),
        CRACKED_GAS("cracked gas", Good.LPG);

        private final String words;
        private final Good leftover;

        Stream(String words, Good leftover) {
            this.words = words;
            this.leftover = leftover;
        }

        /** The stream in a sentence: "heavy naphtha". */
        public String words() { return words; }

        /** The product what is left of it is sold as (4 in the header); null for the residue, which is cut with diesel (5). */
        public Good leftover() { return leftover; }

        /** Whether it is residue, the heavy part or the rest. */
        public boolean residue() { return this == RESIDUE || this == HEAVY_RESIDUE; }
    }

    /** One line of what a unit makes of a litre of its feed: into a stream, or a product (litres; tonnes of coke and bitumen). */
    public record Yield(Stream stream, Good good, double perLitre) {
        static Yield of(Stream s, double f) { return new Yield(s, null, f); }
        static Yield of(Good g, double f) { return new Yield(null, g, f); }

        /** What the line is sold as when nothing downstream takes it: its good, or its stream's leftover. */
        public Good product() { return good != null ? good : stream.leftover(); }
    }

    /**
     * The kinds of conversion unit, each with the stream it is fed from and
     * what it makes of a litre of it: the research's 1.2 (vol% of the feed),
     * in the prototype's order. A small and a large of each kind are
     * buildings (BuildingsTemplate.refineryUnit()); the flow sees a kind's
     * feed a month, all its buildings together.
     */
    public enum Kind {
        /** Heavy naphtha into reformate (petrol) and gas, making the hydrogen a hydrocracker needs. */
        REFORMER("Reformer", Stream.HEAVY_NAPHTHA,
                Yield.of(Good.PETROL, .82), Yield.of(Stream.GAS, .08)),
        /** Gas oil cracked into petrol, diesel and fuel oil, and the cracked gas alkylation takes. */
        CRACKER("Cracking Unit", Stream.GAS_OIL,
                Yield.of(Stream.CRACKED_GAS, .25), Yield.of(Good.PETROL, .55), Yield.of(Stream.DIESEL, .18),
                Yield.of(Good.FUEL_OIL, .07)),
        /** Gas oil and hydrogen into diesel and jet fuel, some heavy naphtha and gas. */
        HYDROCRACKER("Hydrocracker", Stream.GAS_OIL,
                Yield.of(Stream.DIESEL, .50), Yield.of(Good.JET, .40), Yield.of(Stream.HEAVY_NAPHTHA, .15),
                Yield.of(Stream.GAS, .05)),
        /** Cracked gas into alkylate, a petrol. */
        ALKYLATION("Alkylation Unit", Stream.CRACKED_GAS,
                Yield.of(Good.PETROL, .80)),
        /** Residue into gas, naphtha, diesel and fuel oil, and coke of 30% of the residue's weight. */
        COKER("Coker", Stream.RESIDUE,
                Yield.of(Stream.CRACKED_GAS, .08), Yield.of(Stream.HEAVY_NAPHTHA, .15), Yield.of(Stream.DIESEL, .35),
                Yield.of(Good.FUEL_OIL, .12), Yield.of(Good.COKE, .30 / RESIDUE_LITRES_PER_TONNE)),
        /** Gas oil into lubricants, the rest fuel oil. */
        LUBE("Lube Plant", Stream.GAS_OIL,
                Yield.of(Good.LUBRICANTS, .40), Yield.of(Good.FUEL_OIL, .60)),
        /** Heavy-crude residue into bitumen, 95% of it, by weight at the residue's litres a tonne. */
        ASPHALT("Asphalt Unit", Stream.HEAVY_RESIDUE,
                Yield.of(Good.BITUMEN, .95 / RESIDUE_LITRES_PER_TONNE));

        private final String unitName;
        private final Stream feed;
        private final List<Yield> yields;

        Kind(String unitName, Stream feed, Yield... yields) {
            this.unitName = unitName;
            this.feed = feed;
            this.yields = List.of(yields);
        }

        /** The large unit's name: "Cracking Unit" (the small one's is "Small " before it). */
        public String unitName() { return unitName; }

        /** The stream it is fed from. */
        public Stream feed() { return feed; }

        /** What it makes of a litre of its feed, in the research's order. */
        public List<Yield> yields() { return yields; }

        /** The stream it is solved in: the residue's for the coker and the asphalt unit alike (★ O4-2). */
        public Stream solvedIn() { return feed.residue() ? Stream.RESIDUE : feed; }
    }

    /** The streams in the order they are solved (3 in the header). */
    public static final Stream[] FLOW_ORDER = { Stream.GAS_OIL, Stream.RESIDUE, Stream.HEAVY_NAPHTHA, Stream.CRACKED_GAS };

    /* ----- what a litre is worth (spec-oil 2.4's arithmetic, pure) ----- */

    /** Each product's value, by Good's ordinal, from a price for each: what the flow and the spread are struck at. */
    public static double[] values(ToDoubleFunction<Good> price) {
        double[] v = new double[Good.values().length];
        for (Good g : Refining.PRODUCTS) v[g.ordinal()] = price.applyAsDouble(g);
        return v;
    }

    /**
     * What a litre of a stream is worth with no unit to take it: the product
     * its leftover is sold as, and residue (4 x fuel oil - diesel) / 3 - the
     * fuel oil three litres of it make with a litre of diesel, less that
     * litre (spec-oil 2.4, feedValue). The prototype's fallback().
     */
    public static double feedValue(Stream s, double[] value) {
        if (s.residue()) return (4 * value[Good.FUEL_OIL.ordinal()] - value[Good.DIESEL.ordinal()]) / 3;
        return value[s.leftover().ordinal()];
    }

    /** A unit's spread a litre of its feed: what it makes of the litre at the values, less what the litre is worth with no unit (the prototype's spread()). */
    public static double spread(Kind k, double[] value) {
        double made = 0;
        for (Yield y : k.yields()) made += y.perLitre() * value[y.product().ordinal()];
        return made - feedValue(k.feed(), value);
    }

    /** What `litres` of a kind's feed make with nothing downstream to take any of it, by product (litres; tonnes of coke and bitumen): a unit's card. */
    public static Map<Good, Double> madeAlone(Kind k, double litres) {
        Map<Good, Double> out = new EnumMap<>(Good.class);
        for (Yield y : k.yields()) out.merge(y.product(), Math.max(0, litres) * y.perLitre(), Double::sum);
        return Collections.unmodifiableMap(out);
    }

    /* ----- the flow ----- */

    /** One month's flow: the crude's litres, the cuts, each kind's run and spread, the streams left, the hydrogen, and the products. */
    public static final class Flow {
        private final double crude;
        private final double[] cuts, runs, spreads, spare;
        private final double hydrogen, spareHydrogen;
        private final Refining.Slate slate;

        Flow(double crude, double[] cuts, double[] runs, double[] spreads, double[] spare, double hydrogen,
             double spareHydrogen, Refining.Slate slate) {
            this.crude = crude;
            this.cuts = cuts;
            this.runs = runs;
            this.spreads = spreads;
            this.spare = spare;
            this.hydrogen = hydrogen;
            this.spareHydrogen = spareHydrogen;
            this.slate = slate;
        }

        /** The crude run, in litres. */
        public double crude() { return crude; }

        /** A straight-run cut of the crude, before any unit: litres (CRACKED_GAS none). */
        public double cut(Stream s) { return cuts[s.ordinal()]; }

        /** Litres of feed a kind of unit took. */
        public double run(Kind k) { return runs[k.ordinal()]; }

        /** A kind's spread a litre of its feed, at the flow's values. */
        public double spread(Kind k) { return spreads[k.ordinal()]; }

        /** What was left of a stream after the units, before it went to its product. */
        public double spare(Stream s) { return spare[s.ordinal()]; }

        /** Hydrogen the reformers made for the hydrocrackers, standard cubic feet. */
        public double hydrogen() { return hydrogen; }

        /** ...and what the hydrocrackers left of it. */
        public double spareHydrogen() { return spareHydrogen; }

        /** The products and the residue burned, as the slate reads them. */
        public Refining.Slate slate() { return slate; }

        /** A product made: litres, or tonnes of coke and bitumen. */
        public double of(Good g) { return slate.of(g); }

        /** Residue burned for want of diesel to cut it, litres. */
        public double burned() { return slate.burned(); }
    }

    /**
     * One month's flow (pure): `crudeTonnes` of crude at `mix` (each grade's
     * share of the run in Deposit.Grade's order) through units able to take
     * `feed` litres a month of each Kind (by its ordinal), their spreads
     * struck at `value` (each product's, by Good's ordinal). See the header.
     */
    public static Flow solve(double[] feed, double crudeTonnes, double[] mix, double[] value) {
        int nStreams = Stream.values().length, nKinds = Kind.values().length;
        // 1. The crude into its cuts: Refining.slate()'s arithmetic, to the bit.
        double run = Math.max(0, crudeTonnes) * Refining.CRUDE_LITRES_PER_TONNE;
        double[] s = new double[nStreams];
        int vr = Stream.RESIDUE.ordinal(), hvr = Stream.HEAVY_RESIDUE.ordinal();
        for (Deposit.Grade grade : Deposit.Grade.values()) {
            double share = mix[grade.ordinal()];
            if (!(share > 0)) continue;
            double[] c = Refining.CUTS[grade.ordinal()];
            for (int i = 0; i < c.length; i++) {
                if (i == Refining.CUT_RESIDUE && grade == Deposit.Grade.HEAVY) s[hvr] += run * share * c[i];
                else s[i] += run * share * c[i];
            }
        }
        double[] cuts = s.clone();
        double[] out = new double[Good.values().length];
        double[] runs = new double[nKinds];
        double[] spreads = new double[nKinds];
        for (Kind k : Kind.values()) spreads[k.ordinal()] = spread(k, value);

        // 2. Hydrogen: the reformers' make on the straight-run heavy naphtha, before anything is cracked (★ O4-3: when they run).
        int hn = Stream.HEAVY_NAPHTHA.ordinal();
        double reformers = feed[Kind.REFORMER.ordinal()];
        double h2 = reformers > 0 && spreads[Kind.REFORMER.ordinal()] > 0 ? Math.min(reformers, s[hn]) * HYDROGEN_MADE : 0;
        double hydrogen = h2;

        // 3. The streams in flow order, each taken by its units widest spread first.
        for (Stream stream : FLOW_ORDER) {
            List<Kind> users = new ArrayList<>();
            for (Kind k : Kind.values()) if (k.solvedIn() == stream && feed[k.ordinal()] > 0) users.add(k);
            users.sort((a, b) -> Double.compare(spreads[b.ordinal()], spreads[a.ordinal()]));   // stable: Kind's order on a tie
            for (Kind k : users) {
                int ki = k.ordinal();
                if (!(spreads[ki] > 0)) { runs[ki] = 0; continue; }
                double avail = k == Kind.COKER ? s[vr] + s[hvr] : s[k.feed().ordinal()];
                double take = Math.min(feed[ki], avail);
                if (k == Kind.HYDROCRACKER) take = Math.min(take, h2 / HYDROGEN_USED);
                take = Math.max(0, take);
                if (k == Kind.COKER) {
                    // The rest of the residue first, then the heavy part.
                    double a = Math.min(take, s[vr]);
                    s[vr] -= a;
                    s[hvr] = Math.max(0, s[hvr] - (take - a));
                } else {
                    s[k.feed().ordinal()] -= take;
                }
                if (k == Kind.HYDROCRACKER) h2 -= take * HYDROGEN_USED;
                runs[ki] = take;
                for (Yield y : k.yields()) {
                    if (y.stream() != null) s[y.stream().ordinal()] += take * y.perLitre();
                    else out[y.good().ordinal()] += take * y.perLitre();
                }
            }
        }
        double[] spare = s.clone();

        // 4. The leftovers to their products.
        int lpg = Good.LPG.ordinal(), petrol = Good.PETROL.ordinal(), naphtha = Good.NAPHTHA.ordinal(),
                jet = Good.JET.ordinal(), diesel = Good.DIESEL.ordinal(), fuelOil = Good.FUEL_OIL.ordinal();
        out[lpg] += s[Stream.GAS.ordinal()] + s[Stream.CRACKED_GAS.ordinal()];
        out[petrol] += s[Stream.LIGHT_NAPHTHA.ordinal()];
        out[naphtha] += s[hn];
        out[jet] += s[Stream.KEROSENE.ordinal()];
        out[diesel] += s[Stream.DIESEL.ordinal()];
        out[fuelOil] += s[Stream.GAS_OIL.ordinal()];

        // 5. The residue cut three to one with the diesel (Q5), the rest burned; the diesel held at nothing (O3's star 4).
        double residue = s[vr] + s[hvr];
        double cut = Math.min(residue, Refining.RESIDUE_PER_DIESEL * out[diesel]);
        out[diesel] = Math.max(0, out[diesel] - cut / Refining.RESIDUE_PER_DIESEL);
        out[fuelOil] += cut * (Refining.RESIDUE_PER_DIESEL + 1) / Refining.RESIDUE_PER_DIESEL;

        // The products as the slate keeps them: its six always, the three only units make when they make any.
        Map<Good, Double> products = new EnumMap<>(Good.class);
        products.put(Good.LPG, out[lpg]);
        products.put(Good.NAPHTHA, out[naphtha]);
        products.put(Good.PETROL, out[petrol]);
        products.put(Good.JET, out[jet]);
        products.put(Good.DIESEL, out[diesel]);
        products.put(Good.FUEL_OIL, out[fuelOil]);
        for (Good g : new Good[] { Good.LUBRICANTS, Good.BITUMEN, Good.COKE }) {
            if (out[g.ordinal()] > 0) products.put(g, out[g.ordinal()]);
        }
        return new Flow(run, cuts, runs, spreads, spare, hydrogen, Math.max(0, h2),
                new Refining.Slate(Collections.unmodifiableMap(products), residue - cut));
    }
}
