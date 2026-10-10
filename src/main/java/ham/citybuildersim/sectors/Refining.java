package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.BusinessInvestment;
import ham.citybuildersim.Deposit;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.GoodsMarket;
import ham.citybuildersim.LandManager;
import ham.citybuildersim.Sector;
import ham.citybuildersim.Sectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * The refinery. THE SEVENTEENTH SECTOR (0.7.62, batch K; the project's
 * spec-land.md 2.7), and since 0.7.76 a crude unit that cuts a barrel into
 * what a real one does (batch O1; runs/spec-oil.md 2.1 and 2.3) - and since
 * 0.7.80 a campus: crude units, and the conversion units behind them that
 * turn the cheap cuts into dear products (batch O4; THE FLOW, below).
 *
 * HEAVY INDUSTRY'S SHAPE, ON OIL. A refinery buys crude - the city's wells'
 * first, the world's for the rest (CRUDE is importable, so the shortfall is
 * the template's) - and makes the products of its slate (THE SLATE, below)
 * into its tanks: petrol, diesel, jet fuel, naphtha, petroleum gas and fuel
 * oil - and since 0.7.80 its conversion units' lubricants, bitumen and coke.
 * The city's drivers (Motoring, at 6d; through the grocers' pumps since
 * 0.7.83) draw the petrol, and its railway (Rail.haul()) and since 0.7.83
 * its vans the diesel, off those tanks before the world, as the factories
 * do the lubricants and the builders the bitumen (0.7.83); the rest leaves
 * by the export-bound line until something here buys it.
 *
 * WHY. Until 0.7.76 it made FUEL, a thousand litres from a tonne - 86% of a
 * barrel as transport fuels and the rest left out - so one refinery was
 * 8.3M litres of whatever the city burned, and its gate asked for a whole
 * plant's worth of that. A tonne of medium crude makes 70 L of petrol and
 * 169 L of diesel, and the rest is products the city does not burn.
 *
 * WHAT TO BUILD is the spread planner's since 0.7.82 (batch O5; THE SPREAD
 * PLANNER, below, and the shared SpreadPlanner): of the refiners' buildings
 * that pass the gates - feed, ground, staff, money - the one that earns most
 * on its cost at the city's own prices: a conversion unit on its spread on
 * the stream it would find spare, a crude unit (for the city's own petrol
 * and diesel or its wells' spare crude) with the units its cuts would feed.
 * Until then it was one building, the Oil Refinery, for a whole plant's
 * worth of the city's own petrol and diesel or of its wells' spare crude,
 * on the investors' estimate over its slate (K's rule, 0.7.62 to 0.7.81).
 *
 * AND ITS TANK FARM (0.7.85, batch O8; spec-oil 2.8; THE TANK FARM, below):
 * with one standing the refiners keep a month of their crude on hand and
 * run on what they have and what they bought.
 */
public final class Refining extends Sector {

    /* =====================================================================
       THE SLATE (0.7.76, batch O1; spec-oil 2.2 and 2.3)

       A crude unit makes no product of its own: its template says only the
       crude it takes (BuildingsTemplate.uses()), and what that crude becomes
       is this - the straight-run cuts of the crude, and each cut to the
       product it is worth with no conversion unit to upgrade it:

           gas       -> PETROLEUM GAS (LPG)
           light naphtha -> PETROL
           heavy naphtha -> NAPHTHA
           kerosene  -> JET
           diesel    -> DIESEL
           gas oil (VGO) -> FUEL_OIL
           residue   -> FUEL_OIL, cut three to one with the diesel (Q5);
                        whatever the diesel cannot cut is burned in the
                        furnaces at no value

       A tonne of medium crude is 1,165 litres: 17 of gas, 70 of petrol, 140 of
       naphtha, 140 of jet, 169 of diesel (256 less the 87 that cut the
       residue) and 629 of fuel oil. The litres balance to the litre.

       THE ARITHMETIC IS THE PROTOTYPE'S, IN ITS ORDER (scratch-oil's
       spread.py, its solve), so that the flow of O4 (RefineryFlow) with no
       conversion unit equals this to the bit: each cut is the run's litres
       times the grade's share times the cut's fraction, the residue's cut
       is min(residue, 3 x diesel), the diesel loses cut / 3 and the fuel
       oil gains cut x 4 / 3.

       BY GRADE SINCE 0.7.79 (batch O3; spec-oil 2.2). An oil field is light,
       medium or heavy (Deposit.grade()), and each grade cuts by its own
       column (CUTS): light is Brent's [R1], medium the research's blend,
       heavy Maya's [R2]. A run is a mix of the grades (the month's crude
       mix, below), each grade's share of the run cut by its column, summed
       in Grade's order - LIGHT, MEDIUM, HEAVY - and the heavy crude's
       residue kept apart until the cut, as the prototype keeps it for the
       asphalt units (O4), then added to the rest. A tonne of light crude
       makes 97 L of petrol and 252 L of diesel; of heavy, 59 L of petrol
       and no diesel, its 110 L all spent cutting residue and 101 L of
       residue burned for want of more - which is why heavy crude wants a
       coker. When the diesel cuts all it can, what is left of it is held
       at nothing (star O3-4: three times a litre over three can come back
       an ulp over it). On medium crude alone every figure is O1's to the
       bit.
       ===================================================================== */

    /** Litres in a tonne of crude [P35]: the slate is struck in litres, the crude bought in tonnes. */
    public static final double CRUDE_LITRES_PER_TONNE = 1165;

    /**
     * The straight-run cuts of a MEDIUM crude, vol% (the research's 1.1, the
     * blend): gas, light naphtha, heavy naphtha, kerosene, diesel, gas oil and
     * residue, in that order (the CUT_ indexes); normalised to 1 in MEDIUM_CUTS.
     */
    static final double[] MEDIUM_VOL_PCT = { 1.5, 6.0, 12.0, 12.0, 22.0, 24.0, 22.5 };

    /** ...each a share of the barrel, MEDIUM_VOL_PCT over its sum (the column normalised, as the prototype does). */
    public static final double[] MEDIUM_CUTS = normalised(MEDIUM_VOL_PCT);

    /** The cuts' places in MEDIUM_CUTS. */
    public static final int CUT_GAS = 0, CUT_LIGHT_NAPHTHA = 1, CUT_HEAVY_NAPHTHA = 2, CUT_KEROSENE = 3,
            CUT_DIESEL = 4, CUT_GAS_OIL = 5, CUT_RESIDUE = 6;

    /** The straight-run cuts of a LIGHT crude, vol%, in the CUT_ order: Brent, 38 degrees API [R1] (the research's 1.1; they sum to 100.6). */
    static final double[] LIGHT_VOL_PCT = { 4.1, 8.4, 15.9, 13.9, 25.6, 21.3, 11.4 };

    /** ...of a HEAVY crude: Maya, 21.5 degrees API [R2], its 15.3 of naphtha split one to two light to heavy as the blend's is (spec-oil 2.2). */
    static final double[] HEAVY_VOL_PCT = { 0.3, 5.1, 10.2, 13.8, 9.4, 24.3, 36.9 };

    /** Each grade's cuts, in Deposit.Grade's order, each column normalised to one (0.7.79): LIGHT_VOL_PCT's, MEDIUM_CUTS itself, HEAVY_VOL_PCT's. */
    public static final double[][] CUTS = { normalised(LIGHT_VOL_PCT), MEDIUM_CUTS, normalised(HEAVY_VOL_PCT) };

    /** A run all of MEDIUM crude, the research's blend: the mix of imports, of a refinery that took no crude, and of every crude unit until 0.7.79. */
    public static final double[] MEDIUM_MIX = mixOf(Deposit.Grade.MEDIUM);

    /** A run all of one grade, as a mix in Deposit.Grade's order. */
    public static double[] mixOf(Deposit.Grade g) {
        double[] mix = new double[Deposit.Grade.values().length];
        mix[g.ordinal()] = 1;
        return mix;
    }

    /** Litres of residue a litre of diesel cuts into fuel oil (Q5): three to one, so four litres of fuel oil. */
    public static final double RESIDUE_PER_DIESEL = 3;

    /** The products a crude unit makes, in the order a screen lists them: the two the city burns first. */
    static final Good[] PRODUCTS = { Good.PETROL, Good.DIESEL, Good.LPG, Good.NAPHTHA, Good.JET, Good.FUEL_OIL,
            Good.LUBRICANTS, Good.BITUMEN, Good.COKE };

    /** The products something in the city buys (spec-oil 2.5, O1): the drivers' petrol and the railway's diesel - what the gate is struck on. */
    public static final Good[] BOUGHT_HERE = { Good.PETROL, Good.DIESEL };

    private static double[] normalised(double[] pct) {
        double sum = 0;
        for (double x : pct) sum += x;
        double[] out = new double[pct.length];
        for (int i = 0; i < pct.length; i++) out[i] = pct[i] / sum;
        return out;
    }

    /** One run's products, in litres (BITUMEN and COKE in tonnes), and the residue burned for want of diesel to cut it. */
    public record Slate(Map<Good, Double> products, double burned) {
        /** What the run makes of one product; 0 for one it makes none of. */
        public double of(Good g) { return products.getOrDefault(g, 0.0); }
    }

    /**
     * What `tonnes` of medium crude a month makes (pure): its cuts at
     * MEDIUM_CUTS, each to its product, the residue cut three to one with the
     * diesel and the rest burned. See THE SLATE.
     */
    public static Slate slate(double tonnes) {
        return slate(tonnes, MEDIUM_MIX);
    }

    /**
     * What `tonnes` of crude a month makes at a mix of the grades (pure,
     * 0.7.79): each grade's share of the run cut by its column of CUTS,
     * summed in Deposit.Grade's order, the heavy crude's residue kept apart
     * and added to the rest's before the cut. `mix` holds each grade's share
     * of the run in Grade's order, summing to one; a grade with none is not
     * read. See THE SLATE.
     */
    public static Slate slate(double tonnes, double[] mix) {
        double run = Math.max(0, tonnes) * CRUDE_LITRES_PER_TONNE;
        double[] s = new double[MEDIUM_CUTS.length];
        double heavyResidue = 0;
        for (Deposit.Grade grade : Deposit.Grade.values()) {
            double share = mix[grade.ordinal()];
            if (!(share > 0)) continue;
            double[] c = CUTS[grade.ordinal()];
            for (int i = 0; i < s.length; i++) {
                if (i == CUT_RESIDUE && grade == Deposit.Grade.HEAVY) heavyResidue += run * share * c[i];
                else s[i] += run * share * c[i];
            }
        }
        Map<Good, Double> out = new EnumMap<>(Good.class);
        double lpg = 0, petrol = 0, naphtha = 0, jet = 0, diesel = 0, fuelOil = 0;
        lpg += s[CUT_GAS];
        petrol += s[CUT_LIGHT_NAPHTHA];
        naphtha += s[CUT_HEAVY_NAPHTHA];
        jet += s[CUT_KEROSENE];
        diesel += s[CUT_DIESEL];
        fuelOil += s[CUT_GAS_OIL];
        double residue = s[CUT_RESIDUE] + heavyResidue;
        double cut = Math.min(residue, RESIDUE_PER_DIESEL * diesel);
        diesel = Math.max(0, diesel - cut / RESIDUE_PER_DIESEL);
        fuelOil += cut * (RESIDUE_PER_DIESEL + 1) / RESIDUE_PER_DIESEL;
        out.put(Good.LPG, lpg);
        out.put(Good.NAPHTHA, naphtha);
        out.put(Good.PETROL, petrol);
        out.put(Good.JET, jet);
        out.put(Good.DIESEL, diesel);
        out.put(Good.FUEL_OIL, fuelOil);
        return new Slate(Collections.unmodifiableMap(out), residue - cut);
    }

    /** Whether a building is a crude unit (0.7.76): one of the refiners' that takes crude - its products are the slate of it. */
    public static boolean isCrudeUnit(BuildingsTemplate t) {
        return t != null && Sectors.REFINING.equals(t.getSector()) && t.uses(Good.CRUDE) > 0;
    }

    /** Whether a building is one of the refiners' conversion units (0.7.80): a reformer, a cracking unit and the rest, fed a stream of the crude units' run (BuildingsTemplate.refineryUnit()). */
    public static boolean isConversionUnit(BuildingsTemplate t) {
        return t != null && Sectors.REFINING.equals(t.getSector()) && t.refineryUnit() != null;
    }

    /**
     * What a building makes a month at nameplate, by good (pure, 0.7.76): a
     * crude unit's slate of its crude, a conversion unit's products of its
     * whole feed with nothing downstream to take them (0.7.80,
     * RefineryFlow.madeAlone()), and every other building's template
     * (BuildingsTemplate.goodsMade()). What the build card values and lists.
     */
    public static Map<Good, Double> madeBy(BuildingsTemplate t) {
        if (t == null) return Collections.emptyMap();
        if (isConversionUnit(t)) return RefineryFlow.madeAlone(t.refineryUnit(), t.feedPerMonth());
        return isCrudeUnit(t) ? slate(t.uses(Good.CRUDE)).products() : t.goodsMade();
    }

    /** What a conversion unit's whole feed is worth a month with no unit to take it, at `value` (RefineryFlow.values()): what its value added is struck against. 0 for any other building. */
    public static double feedValueOf(BuildingsTemplate t, double[] value) {
        if (!isConversionUnit(t)) return 0;
        return t.feedPerMonth() * RefineryFlow.feedValue(t.refineryUnit().feed(), value);
    }

    public Refining() {
        super("Refining", "Refining", BuildingType.HEAVY_INDUSTRY);
        for (Good g : PRODUCTS) makes(g);
        uses(Good.CRUDE);
        blurb("Refines crude into petrol, diesel and the rest of the barrel. Buys the "
                + "city's crude when its wells lift any and imports the rest; sells the "
                + "petrol and diesel at home first and ships what the city does not want.");
    }

    /* =====================================================================
       THE MONTH'S CRUDE MIX (0.7.79, batch O3; spec-oil 2.2)

       What the crude units ran is local crude, from the city's wells, and
       imports. The local crude is of the grades the wells lifted that month
       (LandManager.getOilLiftedByGrade(): their walk of the fields from E to
       E + lifted), the imports are MEDIUM - the world's blend - so the
       month's mix is the local fill at the lift's mix plus the imports at
       medium, each a share of what the units bought (the prototype's month,
       scratch-oil's spread.py). It is struck when the month's
       markets have cleared (endOfMonth()), saved (crudeMix.LIGHT, .MEDIUM,
       .HEAVY), and next month's slate is struck on it: the nameplate, good
       by good, and the crude units on site. A month that bought no crude
       strikes MEDIUM, as the prototype's empty mix is; a save from before
       0.7.79 reads MEDIUM, which is what its refineries ran.

       The tanks' shares stay the slate of a tonne of medium crude (O1's
       star 4). A crude unit not yet built is judged on the crude it would
       run (0.7.82, THE SPREAD PLANNER): the wells' spare at the grades they
       lift next (gradesAhead()), the rest at medium.
       ===================================================================== */

    /** The crude units' mix this month, each grade's share of their run in Deposit.Grade's order: last month's purchases. */
    private final double[] crudeMix = MEDIUM_MIX.clone();

    /** The crude mix the month's slate is struck on (a copy), each grade's share in Deposit.Grade's order. */
    public double[] getCrudeMix() { return crudeMix.clone(); }

    /** A fixture's crude mix, each grade's share in Deposit.Grade's order (SaveFileCheck's city, which runs no refinery): what endOfMonth() would strike. */
    public void setCrudeMixForTest(double[] mix) {
        System.arraycopy(mix, 0, crudeMix, 0, crudeMix.length);
    }

    /** What `tonnes` of crude make at this month's mix: slate(tonnes, getCrudeMix()). */
    public Slate slateOf(double tonnes) { return slate(tonnes, crudeMix); }

    /**
     * The month's mix of what a refinery bought (pure, 0.7.79): `local`
     * tonnes at the lift's mix (`liftedByGrade`, tonnes by grade; MEDIUM when
     * the wells lifted none) and `imported` at MEDIUM, each a share of the
     * two; MEDIUM_MIX when it bought neither.
     */
    public static double[] monthsMix(double local, double[] liftedByGrade, double imported) {
        double bought = Math.max(0, local) + Math.max(0, imported);
        if (!(bought > 0)) return MEDIUM_MIX.clone();
        double[] mix = new double[Deposit.Grade.values().length];
        double lifted = 0;
        if (liftedByGrade != null) for (double t : liftedByGrade) lifted += Math.max(0, t);
        int medium = Deposit.Grade.MEDIUM.ordinal();
        if (local > 0 && lifted > 0) {
            for (int g = 0; g < mix.length; g++) mix[g] += Math.max(0, liftedByGrade[g]) / lifted * local / bought;
        } else if (local > 0) {
            mix[medium] += local / bought;
        }
        if (imported > 0) mix[medium] += imported / bought;
        return mix;
    }

    /**
     * The month's markets have cleared: its crude mix struck from what the
     * crude units bought, at home and abroad - and (0.7.82) each kind of
     * unit standing counted idle or working in the month's flow, for the
     * spread planner's shedding (THE SPREAD PLANNER).
     */
    @Override
    public void endOfMonth(Game game) {
        // The month as it ran (0.7.95, THE MONTH AS IT RAN): its flow, mix and rate, before the mix is struck anew.
        monthsFlow = flow();
        monthsMix = crudeMix.clone();
        monthsRate = getOperatingRate();
        monthsRun = runShare;
        Sector.Input in = inputRow(Good.CRUDE);
        double local = in == null ? 0 : in.boughtLocal, imported = in == null ? 0 : in.imported;
        double[] lifted = game == null || game.getLandManager() == null ? null : game.getLandManager().getOilLiftedByGrade();
        double[] mix = monthsMix(local, lifted, imported);
        System.arraycopy(mix, 0, crudeMix, 0, crudeMix.length);
        noteIdleUnits();
        // ...and the crude kept in a Tank Farm (0.7.85): what it had, and the month's fill, less the month's run.
        if (keepsCrude()) setPantry(Good.CRUDE, crudeAfterRun(getPantry(Good.CRUDE), monthsCrudeFill(),
                getInputAtCapacity(Good.CRUDE) * getOperatingRate()));
        crudeCleared = false;
        // ...and next month's run (0.7.105, THE RUN), on this month's prices, once the month as it ran is kept.
        strikeRun();
    }

    @Override
    protected void saveExtras(Map<String, Double> extras) {
        for (Deposit.Grade g : Deposit.Grade.values()) extras.put("crudeMix." + g.name(), crudeMix[g.ordinal()]);
        planner.saveIdle(extras, KIND_NAMES);
        // ...and the run (0.7.105, THE RUN): its share, and the shares the last months ran at, the oldest first.
        extras.put(RUN_KEY, runShare);
        for (int i = 0; i < runHistory.size(); i++) extras.put(RUN_HISTORY_KEY + i, runHistory.get(i));
    }

    /** The run's share in the saved extras (THE RUN). */
    static final String RUN_KEY = "run.share";

    /** ...and each of the last months' shares, the oldest at 0 (THE RUN). */
    static final String RUN_HISTORY_KEY = "run.history.";

    /** A save from before 0.7.79, or one whose mix is not a mix, reads MEDIUM. */
    @Override
    protected void restoreExtras(Map<String, Double> extras) {
        double[] mix = new double[crudeMix.length];
        double sum = 0;
        boolean whole = true;
        for (Deposit.Grade g : Deposit.Grade.values()) {
            Double v = extras.get("crudeMix." + g.name());
            whole &= v != null && Double.isFinite(v) && v >= 0;
            if (whole) {
                mix[g.ordinal()] = v;
                sum += v;
            }
        }
        System.arraycopy(whole && sum > 0 ? mix : MEDIUM_MIX, 0, crudeMix, 0, crudeMix.length);
        // ...and each kind's idle months (0.7.82); a save from before them reads none idle.
        planner.restoreIdle(extras, KIND_NAMES);
        // ...and the run (0.7.105): a save from before it ran the whole of nameplate, and has no months of it.
        Double share = extras.get(RUN_KEY);
        runShare = share != null && Double.isFinite(share) ? Math.max(0, Math.min(1, share)) : 1;
        runHistory.clear();
        for (int i = 0; i < SpreadPlanner.IDLE_MONTHS; i++) {
            Double h = extras.get(RUN_HISTORY_KEY + i);
            if (h == null || !Double.isFinite(h)) break;
            runHistory.add(Math.max(0, Math.min(1, h)));
        }
    }

    @Override
    public void reset() {
        super.reset();
        System.arraycopy(MEDIUM_MIX, 0, crudeMix, 0, crudeMix.length);
        planner.reset();
        crudeCleared = false;
        runShare = 1;
        runHistory.clear();
        monthsFlow = null;
        monthsMix = null;
        monthsRate = Double.NaN;
        monthsRun = Double.NaN;
    }

    /* ----- THE MONTH AS IT RAN (0.7.95, batch O11; runs/spec-oil.md 2.12) -----
     * The flow the month's products were made on (Sector.produceStock() reads
     * getCapacity(), the flow's, after every market has cleared), its crude
     * mix and its operating rate - kept at the month's end before the mix is
     * struck anew from what the month bought, which is next month's. The
     * refinery's pictogram (RefineryView) draws them: its products are then
     * the production rows' to the bit. Nothing reads them in the month; not
     * saved - a city just loaded has none until a month runs, as the rows. */

    /** The flow at nameplate the month's products were made on; null before a month has run. */
    private RefineryFlow.Flow monthsFlow;

    /** ...the crude mix it was struck on, each grade's share in Deposit.Grade's order; null before a month has run. */
    private double[] monthsMix;

    /** ...and the operating rate the month's production ran at; NaN before a month has run. */
    private double monthsRate = Double.NaN;

    /** ...and the run's share of nameplate it ran (THE RUN); NaN before a month has run. */
    private double monthsRun = Double.NaN;

    /** The flow at nameplate this month's products were made on (THE MONTH AS IT RAN); null before a month has run since the city was founded or loaded. */
    public RefineryFlow.Flow monthsFlow() { return monthsFlow; }

    /** ...its crude mix, a copy, each grade's share in Deposit.Grade's order; null before a month has run. */
    public double[] monthsMix() { return monthsMix == null ? null : monthsMix.clone(); }

    /** ...and the operating rate it ran at; NaN before a month has run. */
    public double monthsRate() { return monthsRate; }

    /** ...and the run's share of nameplate in it (0.7.105, THE RUN); NaN before a month has run. */
    public double monthsRun() { return monthsRun; }

    /* =====================================================================
       THE FLOW (0.7.80, batch O4; spec-oil 2.3)

       The crude units' run goes through the conversion units the refinery
       has standing (RefineryFlow.solve(): a reformer takes the heavy
       naphtha, a cracking unit the gas oil, and so on, each stream to its
       units widest spread first), and what comes out is the nameplate of
       each product - so the slate is now the flow with no unit, to the bit.

       AT NAMEPLATE, AND THE RATE ON TOP. The flow is struck on the crude
       units' whole run; the month's operating rate then multiplies every
       product's nameplate, as it does every maker's (Sector.produceStock()).
       The units are one crew with the crude units at one rate, and the flow
       is homogeneous - every step a sum, a share or the least of two runs -
       so the flow at nameplate times the rate is the flow at the rate.

       AT THE CITY'S OWN PRICES (★ O4-1): a unit's spread is struck on each
       product market's local price (GoodsMarket.getLocalPrice()), the price
       the build card values the products at, which the save restores - so a
       loaded city runs the flow its saved month did, with nothing new saved.
       Only the order of the spreads and their signs reach the products, so
       a currency reform moves nothing. The spread planner (0.7.82, below)
       prices what one more litre would actually fetch (cityValues()): a
       unit it orders on a spread above nothing at those values runs here
       only while its spread at the local prices is too, and idles else
       (★ O5-3; the idle months then count).

       CACHED ON ITS INPUTS: each kind's feed standing, the crude, the mix
       and the values; the same four give the same flow, unsolved again.

       THE TANKS FOLLOW THE FLOW once a unit stands (getStockCapacity()):
       every product has the same months of its own nameplate as room - the
       tankage over the flow's litres - so lubricants, bitumen and coke,
       which only a unit makes, have room as the rest do, in their own
       units. With no unit standing they stay shared as a tonne of medium
       crude's slate is (O1's star 4), to the bit.
       ===================================================================== */

    /** The flow last solved, and what it was solved on. */
    private RefineryFlow.Flow flowSolved;
    private double[] flowFeed, flowMix, flowPrices;
    private double flowCrude = Double.NaN;

    /** Litres a month of feed each kind of conversion unit could take, by RefineryFlow.Kind's ordinal: its buildings standing, and with `onSite` those on site too. */
    public double[] unitFeed(boolean onSite) {
        double[] feed = new double[RefineryFlow.Kind.values().length];
        if (buildings == null) return feed;
        for (RefineryFlow.Kind k : RefineryFlow.Kind.values()) {
            feed[k.ordinal()] = buildings.totalBySector(key(), t -> t.refineryUnit() == k ? t.feedPerMonth() : 0);
            if (onSite) feed[k.ordinal()] += buildings.underConstructionBySector(key(), t -> t.refineryUnit() == k ? t.feedPerMonth() : 0);
        }
        return feed;
    }

    /** The values the flow's spreads are struck at, by Good's ordinal: each product market's local price (★ O4-1); nothing for one with none. */
    public double[] flowValues() {
        return RefineryFlow.values(g -> {
            GoodsMarket m = markets == null ? null : markets.get(g);
            double p = m == null ? 0 : m.getLocalPrice();
            return Double.isFinite(p) ? p : 0;
        });
    }

    /** This month's flow at nameplate: the standing crude units' crude, at the month's crude mix, through the standing conversion units, at the city's prices. Pure in those; cached. */
    public RefineryFlow.Flow flow() {
        double[] feed = unitFeed(false), values = flowValues();
        double crude = getInputAtCapacity(Good.CRUDE);
        if (flowSolved == null || Double.compare(crude, flowCrude) != 0 || !java.util.Arrays.equals(feed, flowFeed)
                || !java.util.Arrays.equals(crudeMix, flowMix) || !java.util.Arrays.equals(values, flowPrices)) {
            flowSolved = RefineryFlow.solve(feed, crude, crudeMix, values);
            flowFeed = feed;
            flowMix = crudeMix.clone();
            flowPrices = values;
            flowCrude = crude;
        }
        return flowSolved;
    }

    /* ----- the reads, off the flow (spec-oil 2.3, 6) ----- */

    /** Nameplate output of a product a month: the flow's (0.7.80) - with no conversion unit the slate of the crude its standing crude units take, at the month's crude mix (0.7.79). */
    @Override
    public double getCapacity(Good g) {
        return flow().of(g);
    }

    /**
     * ...and what the units on site will add to it, which counts as supply
     * for the planner: the flow with them less the flow without, never below
     * nothing (a reformer on site makes more petrol and less naphtha). With
     * no conversion unit standing or on site, the slate of the crude units'
     * crude on site, as before 0.7.80: the slate is linear in its crude, so
     * it is the same figure.
     */
    @Override
    public double getPipeline(Good g) {
        double[] all = unitFeed(true);
        boolean units = false;
        for (double f : all) units |= f > 0;
        if (!units) return slateOf(crudeOnSite()).of(g);
        double with = RefineryFlow.solve(all, getInputAtCapacity(Good.CRUDE) + crudeOnSite(), crudeMix, flowValues()).of(g);
        return Math.max(0, with - flow().of(g));
    }

    /**
     * Tank room for a product: the tankage of its buildings (each template's
     * `stock`, litres) times the product's share of the slate - an Oil
     * Refinery's 25M L shared as its run is, so each product has the room its
     * share of the run would fill. A product the slate makes none of has none.
     * Once a conversion unit stands (0.7.80), the flow's products, each the
     * same months of its own nameplate (THE FLOW). With a Tank Farm (0.7.85),
     * the crude kept on hand takes its room first, crudeKept() - CRUDE's own
     * room, in tonnes - and the products share the rest (THE TANK FARM).
     */
    @Override
    public double getStockCapacity(Good g) {
        double tankage = super.getStockCapacity(g);
        if (!(tankage > 0)) return 0;
        // ...less the crude kept in a Tank Farm's room (0.7.85), with none the tankage as it was.
        if (keepsCrude()) {
            double crude = crudeKept();
            if (g == Good.CRUDE) return crude;
            tankage = Math.max(0, tankage - crude * CRUDE_LITRES_PER_TONNE);
            if (!(tankage > 0)) return 0;
        }
        // ...the flow's since 0.7.80 once a unit stands: each product the same months of its nameplate (THE FLOW).
        boolean units = false;
        for (double f : unitFeed(false)) units |= f > 0;
        if (units) {
            RefineryFlow.Flow f = flow();
            double litres = 0;
            for (Map.Entry<Good, Double> e : f.slate().products().entrySet()) {
                if (Double.isFinite(e.getKey().litresPerTonne())) litres += e.getValue();
            }
            return litres > 0 ? tankage * f.of(g) / litres : 0;
        }
        Slate one = slate(1);
        double all = 0;
        for (double v : one.products().values()) all += v;
        return all > 0 ? tankage * one.of(g) / all : 0;
    }

    /**
     * The month's production into the tanks - after shipping, from the tanks,
     * whatever of a product they no longer have room for (0.7.76, ★ O1).
     *
     * WHY. A product nobody here buys fills its share of the tanks to the dump
     * line and stays there (Sector.getPlannedOutput(): with no demand the
     * target is DUMP_THRESHOLD of its room), so a refinery always holds dead
     * stock - 15.9M L of an Oil Refinery's 25M. The template's rule writes off
     * stock past the room (Sector.produceStock(): "a demolished warehouse is
     * a loss of assets"), and two things shrink the room under it: a refinery
     * sold back, whose whole 15.9M L went (the first O1 playtest wrote off
     * 159M L over ten sales), and a save from before 0.7.76 whose FUEL split
     * puts more diesel in the tanks than diesel's share holds. Oil does not
     * spoil and a tank farm is emptied before it is pulled down: what is past
     * the room leaves at the export price, as the rest of the surplus does
     * (spec-oil 2.5, "any surplus leaves by produceStock's export-bound line").
     */
    @Override
    public void produceStock(Good g, GoodsMarket market) {
        double past = getStock(g) - getStockCapacity(g);
        if (past > 0 && g.exportable() && market != null) {
            bookSale(market.record(key(), ham.citybuildersim.Trade.WORLD, past, market.exportPrice()));
            setStock(g, getStockCapacity(g));
        }
        super.produceStock(g, market);
    }

    /*
     * THE RUN'S PRODUCTS ALL LEAVE (0.7.98, batch O14; spec-oil 2.3, 2.5 and
     * 6). A maker's line ships what the city will not take when the net
     * export price clears the line's marginal cost (Sector.
     * getExportBoundOutput()), and offers its stock at home at or above it
     * (Sector.offer()). That cost is the energy, the water and the inputs at
     * the rate, shared among its goods by their value at the local prices
     * (Sector.costShareOf()) - a plant's input taken as what one more unit
     * of its good would need.
     *
     * NOT THE REFINERS'. Their crude is the run's: the crude units buy it at
     * nameplate x the rate (Sector.bid(); spec-oil 2.3, "crude runs at
     * nameplate x the rate") and the flow makes every product of it at once,
     * whatever any one line plans - so a line that idled saved none of the
     * crude; it was bought and turned into nothing. On imported crude a slate
     * at the city's prices is worth about its crude, and each product's share
     * of the bill came out a percent or two over its net export price (the
     * export price less the railway's charge): Jerus's 1008 city bought
     * 1.65M t of crude a month, sold its petrol, diesel and lubricants at
     * home, shipped a tenth of its slate and idled 83%, and its refiners
     * lost $2.49B in 24 months on $0.49B of sales. The spec ships
     * it: "Everything else, and any surplus, leaves by produceStock's
     * export-bound line" (2.5); a product with no buyer here "fills 80% of
     * its tank share before it ships" (6).
     *
     * So a refined product's marginal cost is its share of the power and
     * water - the generic rule's "energy and water and nothing else" - and
     * what the city does not take ships at the net export price. Whether
     * the crude should be run at all is the crude units' question: the
     * planner's when it builds them (THE SPREAD PLANNER), the distress
     * rule's when they lose money.
     */

    /** A product's marginal cost to sell or ship: its share of the month's power and water over what the line makes at the rate - not of the crude, which is the run's (THE RUN'S PRODUCTS ALL LEAVE). Nothing made (0.7.105: a run the rule has stopped, THE RUN), nothing: what is in the tanks costs nothing more to sell, the power and water billed either way. */
    @Override
    public double getMarginalCostPerUnit(Good g) {
        double output = getCapacity(g) * getOperatingRate();
        if (output <= 0) return 0;
        return (getElectricityCost() + getWaterCost()) * costShareOf(g) / output;
    }

    /* =====================================================================
       THE WORLD'S DEMAND FOR LUBRICANTS (0.7.103, batch P4; Jerus's A9,
       2026-10-10; runs/spec-oil.md 6's risk, fixO5-notes 7)

       A lube plant upgrades gas oil, and lubricants are the gas oil's best
       use (1.89 times crude), so a Small Lube Plant beside an Oil Refinery
       turned 9.6% of its crude into lubricants (6.0% on light crude), where
       the world's refineries make about 1% [R37] - and every litre the city
       did not use shipped at the export price, as if the world would take
       any amount of them. It will not: the world buys lubricants in the
       share its refineries make them, so it takes a city's up to
       LUBRICANTS_WORLD_SHARE of the crude the city's crude units run, litre
       for litre, in the month (★ the cap's form and its source: the real
       share, not the research's risk line of 2%). What the city's own
       buyers take is theirs on top.

       THE SHIP'S LINE IS CUT TO IT (getExportBoundOutput()): past the cap
       the plant's lubricants idle, as any maker's line idles past what it
       can sell. The tanks emptied past their room (produceStock()) are a
       refinery's being pulled down, not the run, and still ship. AND THE
       PLANNER READS IT: a lube plant is weighed on no more gas oil than
       makes the lubricants the world and the city would still take
       (lubricantsRoom(), the Outlook's lubricantsHome), so none is ordered
       for a market that is not there.
       ===================================================================== */

    /** The share of a city's crude, litre for litre, the world takes in lubricants a month: the real share of crude that becomes lubricants, about 1% [R37] (runs/spec-oil.md 6; the research's risk line was 2%). */
    public static final double LUBRICANTS_WORLD_SHARE = .01;

    /** Litres of lubricants the world takes a month of a city whose crude units run `crudeLitres`: LUBRICANTS_WORLD_SHARE of them. Pure. */
    public static double lubricantsWorldTakes(double crudeLitres) {
        return crudeLitres > 0 && Double.isFinite(crudeLitres) ? LUBRICANTS_WORLD_SHARE * crudeLitres : 0;
    }

    /** ...this month's: of the crude the crude units run at the rate, in litres. */
    public double lubricantsWorldTakes() {
        return lubricantsWorldTakes(getInputAtCapacity(Good.CRUDE) * getOperatingRate() * CRUDE_LITRES_PER_TONNE);
    }

    /** The lubricants a month the world and the city would still take past what `flow` makes: the world's share of its crude and `home`, less its lubricants; never below nothing. Infinite `home`, no cap (the prototype's frame). Pure. */
    public static double lubricantsRoom(RefineryFlow.Flow flow, double home) {
        if (!(home < Double.POSITIVE_INFINITY)) return Double.POSITIVE_INFINITY;
        return Math.max(0, lubricantsWorldTakes(flow.crude()) + Math.max(0, home) - flow.of(Good.LUBRICANTS));
    }

    /** Lubricants a lube unit makes of a litre of its gas oil: its kind's yield (RefineryFlow.Kind.LUBE). */
    public static double lubricantsPerLitreOfFeed() {
        for (RefineryFlow.Yield y : RefineryFlow.Kind.LUBE.yields()) if (y.good() == Good.LUBRICANTS) return y.perLitre();
        return 0;
    }

    /** What the ship's line takes of a product: the maker's rule (Sector.getExportBoundOutput()), lubricants no more than the world takes this month. */
    @Override
    public double getExportBoundOutput(Good g) {
        double direct = super.getExportBoundOutput(g);
        return g == Good.LUBRICANTS ? Math.min(direct, lubricantsWorldTakes()) : direct;
    }

    /** The crude the crude units on site will take, in tonnes a month. */
    private double crudeOnSite() {
        return buildings == null ? 0 : buildings.underConstructionBySector(key(), t -> t.uses(Good.CRUDE));
    }

    /** Tonnes of crude the refineries want this month, at the rate they are running. */
    public double getCrudeDemand() {
        return getInputAtCapacity(Good.CRUDE) * getOperatingRate();
    }

    /**
     * What a building is measured in, for the retirement rules: a crude
     * unit's crude it takes (its products are its slate), and since 0.7.82 a
     * conversion unit's feed, the litres a month of its stream it takes - so
     * the spare-capacity rule can sell a kind that stands idle (THE SPREAD
     * PLANNER).
     */
    @Override
    public double unitsOf(BuildingsTemplate t) {
        if (t == null) return 0;
        return isConversionUnit(t) ? t.feedPerMonth() : t.uses(Good.CRUDE);
    }

    /** The products the city buys, a month, at a crude unit's nameplate: its petrol and its diesel. */
    public static double boughtHere(Slate s) {
        double v = 0;
        for (Good g : BOUGHT_HERE) v += s.of(g);
        return v;
    }

    /**
     * The crude the city's wells could lift this month that no refinery
     * standing or on site will take, in tonnes (never below nothing).
     */
    public double spareCrude() {
        Oil wells = game == null ? null : game.getSectors().oil();
        if (wells == null) return 0;
        // ...at the plant's rate (0.7.105): what the run leaves idle (THE RUN) is not the wells' to spare - the units stand.
        return Math.max(0, wells.getPotentialOutput() - getInputAtCapacity(Good.CRUDE) * plantRate() - crudeOnSite());
    }

    /* =====================================================================
       THE TANK FARM (0.7.85, batch O8; spec-oil 2.8, the research's 4.5)

       Crude was a flow here: the crude units bought what they ran, at the
       rate, the month they ran it - a well ships what it lifts. A Tank Farm
       (buildings.json: the refiners' 500,000 m3 of tanks) makes it a stock.
       With one standing the refiners keep CRUDE_COVER_MONTHS of their crude
       units' run on hand (crudeKept(): a month at nameplate, as far as the
       farms' room holds it, ★ spec-oil 2.8), and their products share the
       rest of the room (getStockCapacity()). The tanks are what a tanker's
       cargo will land in (the ports, batch O9: a farm is built for them
       when a port holds crude back for room; until then the investors
       never order one, and the player may).

       BOUGHT AS STOCK. While they keep crude, their order for it is the
       month's run at the rate and what brings the store back to what they
       keep (bid()), and it is stock (buysAhead()): cut, with the rest of
       their stock, to what they can pay for (Sector, BUY ONLY WHAT IT CAN
       PAY FOR), where a maker's inputs are bought whole.

       AND THE RUN IS HELD TO THE CRUDE: once crude's market has cleared
       (afterClearing()), the operating rate is no more than the crude on
       hand and the month's fill will run (crudeRunCap()), so a refinery
       short of cash runs down its tanks, and then runs below its rate - its
       output reads the inputs it bought, which a maker's does not (Sector,
       BUY ONLY WHAT IT CAN PAY FOR). The month's end books the store: what
       it had, and the fill, less the run (crudeAfterRun()). The crude on
       hand is the sector's own (Sector's pantry map, saved as its pantry
       is), valued at crude's price like any stock, and counted in the
       national accounts' held goods (NationalAccounts.HELD).

       WITH NO FARM, NOTHING MOVES: no crude kept, the tankage the products'
       as it was, crude bought whole at the rate and run the month it lands
       - every figure what it was. A farm sold off leaves its crude to be
       run down before the old rule returns (keepsCrude()).
       ===================================================================== */

    /** Months of the crude units' run kept on hand in a Tank Farm's room (★ spec-oil 2.8): one, a month's run. */
    public static final double CRUDE_COVER_MONTHS = 1;

    /** Whether a building is a Tank Farm (0.7.85): one of the refiners' that runs nothing and has tanks - no crude unit, no conversion unit. */
    public static boolean isTankFarm(BuildingsTemplate t) {
        return t != null && Sectors.REFINING.equals(t.getSector()) && !isCrudeUnit(t) && !isConversionUnit(t)
                && t.stocks(Good.CRUDE) > 0;
    }

    /** Litres of tank room the refiners' Tank Farms standing give. */
    public double tankFarmRoom() {
        return buildings == null ? 0 : buildings.totalBySector(key(), t -> isTankFarm(t) ? t.stocks(Good.CRUDE) : 0);
    }

    /**
     * Tonnes of crude the refiners keep on hand: CRUDE_COVER_MONTHS of their
     * crude units' run - since 0.7.105 the run's share of nameplate (THE
     * RUN) - as far as their Tank Farms' room holds it with an MR's cargo
     * kept free (THE NEXT CARGO'S ROOM, below); none without a farm.
     */
    public double crudeKept() {
        double room = tankFarmRoom();
        if (!(room > 0)) return 0;
        double free = room / CRUDE_LITRES_PER_TONNE - CARGO_KEPT_FREE;
        return Math.max(0, Math.min(CRUDE_COVER_MONTHS * getInputAtCapacity(Good.CRUDE) * runShare, free));
    }

    /*
     * THE NEXT CARGO'S ROOM (0.7.105, batch P6; Jerus's R2). A tanker lands
     * crude only while the farms have room free for an MR's cargo
     * (ham.citybuildersim.Ports.freeCrudeRoom()), and the refiners kept a
     * month of their run as far as the room held it - so one Crude Unit
     * (415,000 t a month) with one Tank Farm (429,185 t of room) left 14,185
     * t free and no tanker ever landed, and the investors order a farm only
     * while none stands (farmCandidate()): the port held their crude back
     * for good. ★ Now the room an MR's cargo needs is kept free whatever the
     * run: a farm stores the refiners' month as far as the rest of it holds,
     * and a tanker always fits once one stands - the ullage a real farm keeps
     * for the next parcel - where ordering farms until one fits would buy a
     * second farm to cure the first one's count. PortCheck 7 holds it.
     */

    /** Tonnes of a farm's room kept free for the next cargo (R2): an MR's, the smallest tanker's (Ports.Ship.MR). */
    public static final double CARGO_KEPT_FREE = ham.citybuildersim.Ports.Ship.MR.tonnes();

    /** Whether they keep crude: a Tank Farm stands, or crude is left on hand from one. With neither, crude is a flow, as it was. */
    public boolean keepsCrude() {
        return tankFarmRoom() > 0 || getPantry(Good.CRUDE) > 0;
    }

    /*
     * THE FARM'S ORDER (0.7.86, batch O9; spec-oil 2.8: "built only when a
     * port holds crude back for room"). While a tanker berth stands and the
     * refiners' tanks have no room for a tanker's cargo, the crude it would
     * take is held back (ham.citybuildersim.Ports.getHeldBack()), and the
     * refiners' part of it - their share of the crude across the boundary,
     * which is what they import - pays its lorry and rail freight where a
     * ship would carry it at the sea's. A Tank Farm is weighed (plan()) on
     * that freight saved, less its own running and standing, by the spread
     * planner's gates; with nothing held back it is not weighed at all, and
     * the planner is what it was.
     */

    /** Freight a tonne of crude brought in would save going by sea, in city money: crude's lorry freight at the band's and the railway's shares in force, less the ship's (Ports.SEA_FREIGHT_SHARE_LIQUID). */
    public double crudeFreightSavedByShip() {
        GoodsMarket m = markets == null ? null : markets.get(Good.CRUDE);
        if (m == null) return 0;
        double saved = Good.CRUDE.baseFreight() * m.getExchangeRate()
                * (m.getFreightFactor() + m.getRailCharge() - ham.citybuildersim.Ports.SEA_FREIGHT_SHARE_LIQUID);
        return Double.isFinite(saved) ? Math.max(0, saved) : 0;
    }

    /** Tonnes a month of the refiners' own imported crude a port holds back for room: their share of the crude across the boundary, of what is held back. */
    public double crudeHeldBackForThem(ham.citybuildersim.Ports ports) {
        if (ports == null || !(ports.getHeldBack() > 0) || !(ports.crudeTrade() > 0)) return 0;
        Sector.Input in = inputRow(Good.CRUDE);
        double imported = in == null ? 0 : Math.max(0, in.imported);
        return ports.getHeldBack() * Math.min(1, imported / ports.crudeTrade());
    }

    /** A Tank Farm weighed (pure): `tonnes` of crude a month at `saved` a tonne, less its running and standing; refused at its feed with nothing held back. */
    public static SpreadPlanner.Candidate farmEstimate(BuildingsTemplate farm, double tonnes, double saved, SpreadPlanner.City city) {
        double earns = tonnes * saved - city.running(farm) - city.standing(farm);
        String feed = tonnes > 0 ? null : "no crude of theirs is held back for room at a port";
        return SpreadPlanner.judge(farm, earns, city.cost(farm), feed, city, null);
    }

    /** The Tank Farm's candidate this month: null with no farm in the catalogue, one standing or on site, or none of their crude held back. */
    public SpreadPlanner.Candidate farmCandidate(BusinessInvestment plans, Game game) {
        if (game == null || buildings == null) return null;
        double tonnes = crudeHeldBackForThem(game.getPorts());
        if (!(tonnes > 0) || tankFarmRoom() > 0) return null;
        for (BuildingsTemplate t : buildings.getTemplatesBySector(key())) {
            if (!isTankFarm(t)) continue;
            if (buildings.getQuantity(t.getId()) > 0) return null;
            for (ham.citybuildersim.BuildingsStacks s : buildings.getStacksUnderConstruction()) {
                if (s.getBuilding() == t && s.getUnderConstruction() > 0) return null;
            }
            return farmEstimate(t, tonnes, crudeFreightSavedByShip(), planner.city(this, plans, game));
        }
        return null;
    }

    /*
     * ...AND THE PORT'S TANKERS LAND IN IT (0.7.86, batch O9; spec-oil 2.9):
     * a tanker berth takes crude only while the farms have room free for at
     * least an MR's cargo (ham.citybuildersim.Ports.freeCrudeRoom()). The
     * page says what the berth would take and the room holds back, or the
     * class the crude goes in; nothing with no tanker berth.
     */
    @Override
    public List<Sector.Line> ownLines(Game game) {
        List<Sector.Line> lines = super.ownLines(game);
        ham.citybuildersim.Formats f = ham.citybuildersim.Formats.INSTANCE;
        // THE RUN (0.7.105): the six throttles are not all the rate here - the run multiplies them - and why it runs what it runs.
        if (game != null && getInputAtCapacity(Good.CRUDE) > 0 && buildingsStanding() > 0) {
            for (int i = 0; i < lines.size(); i++) {
                Sector.Line l = lines.get(i);
                if (l.kind() == Line.Kind.NOTE && l.label() != null && l.label().startsWith("Staffed ")) {
                    lines.set(i, Line.note(String.format("Staffed %s \u00d7 power %s \u00d7 water %s \u00d7 roads %s \u00d7 well %s"
                                    + " \u00d7 vans %s \u00d7 the run %s \u2014 all seven multiply: the six are the plant's, the run"
                                    + " is the share of its crude whose barrels pay.", f.pct(getAverageFill()), f.pct(getEnergyRatio()),
                            f.pct(getWaterRatio()), f.pct(getRoadRatio()), f.pct(getHealthRatio()), f.pct(getVanRatio()),
                            f.pct(runShare))));
                }
            }
            lines.add(Line.head("The run"));
            lines.add(Line.of("Crude run", f.pct(runShare) + " of nameplate",
                    runShare >= 1 ? Line.Tone.GOOD : runShare > 0 ? Line.Tone.WARN : Line.Tone.BAD));
            lines.add(Line.note(runWords(runVerdict(), runShare)));
        }
        ham.citybuildersim.Ports ports = game == null ? null : game.getPorts();
        if (ports == null) return lines;
        if (ports.getHeldBack() > 0) {
            lines.add(Line.head("The port"));
            lines.add(Line.of("Crude held back for room", f.count(ports.getHeldBack()) + " t a month", Line.Tone.WARN));
            lines.add(Line.note(String.format("A tanker lands its whole cargo at once. With room free in a Tank Farm"
                    + " for an MR's %s t, the tanker berth would take this crude off the railway and the lorries.",
                    f.count(ham.citybuildersim.Ports.Ship.MR.tonnes()))));
        } else if (ports.crudeShip() != null && ports.share(ham.citybuildersim.Ports.Cargo.LIQUID) > 0) {
            lines.add(Line.head("The port"));
            lines.add(Line.of("Crude by sea", ports.crudeShip().label() + ", the largest the room takes"));
        }
        return lines;
    }

    /** Their crude, while they keep it, is stock (Sector.buysAhead()). */
    @Override
    public boolean buysAhead(Good g) {
        return g == Good.CRUDE ? keepsCrude() : super.buysAhead(g);
    }

    /** ...and their order for it is the month's run at the rate, and what brings the store back to crudeKept(). Every other good the template's. */
    @Override
    public double bid(Good g) {
        if (g != Good.CRUDE || !keepsCrude()) return super.bid(g);
        double run = getInputAtCapacity(Good.CRUDE) * getOperatingRate();
        return Math.max(0, run + crudeKept() - getPantry(Good.CRUDE));
    }

    /** Whether the month's crude has cleared with a store kept, so the rate is held to the crude (afterClearing()); until the month's end. */
    private boolean crudeCleared;

    @Override
    protected void afterClearing(Good g) {
        if (g == Good.CRUDE && keepsCrude()) crudeCleared = true;
    }

    /** The crude bought this month, at home and from the world, in tonnes. */
    double monthsCrudeFill() {
        Sector.Input in = inputRow(Good.CRUDE);
        return in == null ? 0 : in.boughtLocal + in.imported;
    }

    /**
     * The share of nameplate the crude on hand and the month's fill will run
     * (pure): the two over the crude units' nameplate run, in tonnes; no cap
     * (infinite) with no crude unit.
     */
    public static double crudeRunCap(double onHand, double fill, double nameplate) {
        if (!(nameplate > 0)) return Double.POSITIVE_INFINITY;
        return (Math.max(0, onHand) + Math.max(0, fill)) / nameplate;
    }

    /** What is left on hand after the month's run (pure): what it had and the fill, less the run, never below nothing. */
    public static double crudeAfterRun(double onHand, double fill, double run) {
        return Math.max(0, Math.max(0, onHand) + Math.max(0, fill) - Math.max(0, run));
    }

    /**
     * The plant's rate times the run (THE RUN, 0.7.105: runShare()) - and,
     * once crude has cleared with a store kept, no more than the crude will
     * run (crudeRunCap()).
     */
    @Override
    public double getOperatingRate() {
        double rate = super.getOperatingRate() * runShare;
        if (!crudeCleared) return rate;
        double cap = crudeRunCap(getPantry(Good.CRUDE), monthsCrudeFill(), getInputAtCapacity(Good.CRUDE));
        return cap < rate ? cap : rate;
    }

    /* =====================================================================
       THE RUN (0.7.105, batch P6; Jerus's A2 (b), 2026-10-10, on the
       project's why-the-refinery-loses.md: "go for the recommended")

       Until 0.7.105 the crude units ran flat out whatever the barrel paid:
       the operating rate is staffing x power x water x roads x health x
       vans, and none of them is a price, so they bought their whole crude
       every month and lost on every tonne - Jerus's 1008 city, a topping
       plant on imported crude by rail, about $15M a month.

       A CRUDE UNIT RUNS A BARREL ONLY WHILE THE BARREL PAYS: while its
       products, at what they would actually fetch, beat its crude and its
       haulage (runRule(), pure). What they fetch (RunPrices): the city's
       own take of petrol and diesel at the import price - the net import
       price, landed and hauled, what the city pays for each litre a
       refinery here does not make (★ the study's price and SpreadPlanner.
       cityValue()'s; the band's ceiling, the most a seller here is paid, is
       the railway's charge less) - and everything past that take, and
       every other product, at the net export price; lubricants past what
       the world and the city take, at nothing (THE WORLD'S DEMAND FOR
       LUBRICANTS). Its crude: the wells' at the local price while they
       lift it, the rest at the net import price, landed and hauled. Only
       what the run moves counts - the crude, the haulage in and out (inside
       those prices) and the vans' diesel; the wages, the power and the
       water (billed at nameplate, Sector.getElectricityCost()) and the
       capital are paid whether the barrel runs or not.

       THE BARRELS IN ORDER. The campus's barrel is its flow at nameplate a
       tonne of crude (THE FLOW: one crew at one rate, so every tonne of
       the run makes the same products), the first on the wells' crude and
       replacing imports. The run is every barrel up to the first that does
       not beat its crude: all of them, none, or the share at a step - where
       the city's take of petrol or diesel is covered, where the wells'
       crude runs out, where the world's lubricants are taken.

       THE PACE: the run moves at most RUN_PACE of nameplate a month toward
       that share (★ MAX_RETIREMENT_FRACTION's quarter, the brief's example,
       not measured), so a month of odd prices moves it a quarter and no
       more. Struck at the month's end (endOfMonth()), after the markets
       have cleared, on that month's prices; next month's operating rate is
       the plant's times it, so the crude units buy, and every unit makes,
       that share of nameplate. Saved (extras run.share); a save from before
       it reads 1, the whole run, as it ran.

       WHAT IT LEAVES IDLE IS SPARE: the crude units' nameplate past the
       most the run used in the last IDLE_MONTHS months is spare capacity,
       which the six-month rule may sell, as it sells a conversion kind
       idle that long (idle, then shed; retirementDemandAndCapacity()).
       ===================================================================== */

    /** The most of nameplate the crude units' run moves in a month (★ the brief's example, not measured): BusinessInvestment.MAX_RETIREMENT_FRACTION, the quarter the spare-capacity rule sheds at most. */
    public static final double RUN_PACE = BusinessInvestment.MAX_RETIREMENT_FRACTION;

    /** The share of the crude units' nameplate they run this month (THE RUN): 1 until the rule moves it. */
    private double runShare = 1;

    /** The shares the last months ran at, the oldest first, at most SpreadPlanner.IDLE_MONTHS of them: what the spare is read off. */
    private final List<Double> runHistory = new ArrayList<>();

    /** The share of the crude units' nameplate they run this month (THE RUN). */
    public double runShare() { return runShare; }

    /** The plant's own rate - staffing, power, water, roads, health and vans (Sector.getOperatingRate()) - without the run: what the planner weighs an order at. */
    public double plantRate() { return super.getOperatingRate(); }

    /** A fixture's run, as a save would give it (nothing in the game calls it). */
    public void setRunShareForTest(double share) {
        runShare = Math.max(0, Math.min(1, share));
    }

    /**
     * What a barrel's products fetch, and what its crude costs (THE RUN).
     *
     * @param home        by Good's ordinal: a litre of the city's own take of petrol and diesel - the net import price
     * @param away        by Good's ordinal: a litre past it, and of every other product - the net export price (0 for none)
     * @param take        by Good's ordinal: litres a month the city takes of petrol and diesel; nothing for the rest
     * @param lubricantsHome litres a month of lubricants the city's own buyers take (the world's share on top)
     * @param wellsCrude  tonnes a month of the wells' crude the refiners can buy
     * @param crudeLocal  a tonne of it: crude's local price
     * @param crudeImport a tonne imported, landed and hauled: crude's net import price
     * @param perTonne    what else a tonne of the run costs that the run moves: the vans' diesel
     */
    public record RunPrices(double[] home, double[] away, double[] take, double lubricantsHome,
                            double wellsCrude, double crudeLocal, double crudeImport, double perTonne) { }

    /** Where the run stops (THE RUN): at no barrel, where the city's take of petrol or diesel is covered, where the wells' crude runs out, where the world's lubricants are taken, or at nameplate. */
    public enum RunStop { FIRST, PETROL, DIESEL, WELLS, LUBRICANTS, NAMEPLATE }

    /**
     * The run's verdict (runRule()).
     *
     * @param share     the share of nameplate it runs, 0 to 1
     * @param stop      where it stops
     * @param first     the first barrel's margin, a share of its crude's cost (NaN with no crude unit)
     * @param last      the last barrel run's margin, a share of its crude's cost; the first's when none runs
     * @param next      the first barrel not run's, a share of its crude's cost; NaN at nameplate
     */
    public record RunVerdict(double share, RunStop stop, double first, double last, double next) { }

    /**
     * The run (pure; THE RUN): the share of nameplate whose barrels each beat
     * their crude - the flow's products a tonne of its crude at `p`'s prices,
     * the city's take of petrol and diesel at home and the rest away, less
     * the crude, the wells' first, and `p`'s other cost a tonne. `f` is the
     * campus at nameplate.
     */
    public static RunVerdict runRule(RefineryFlow.Flow f, RunPrices p) {
        double crude = f.crude() / CRUDE_LITRES_PER_TONNE;
        if (!(crude > 0)) return new RunVerdict(0, RunStop.FIRST, Double.NaN, Double.NaN, Double.NaN);
        double[] q = new double[Good.values().length];
        for (Good g : PRODUCTS) q[g.ordinal()] = f.of(g) / crude;
        // The steps, in tonnes of the run, each with what it is.
        List<double[]> steps = new ArrayList<>();
        steps.add(new double[] { crude, RunStop.NAMEPLATE.ordinal() });
        if (q[Good.PETROL.ordinal()] > 0) steps.add(new double[] { p.take()[Good.PETROL.ordinal()] / q[Good.PETROL.ordinal()], RunStop.PETROL.ordinal() });
        if (q[Good.DIESEL.ordinal()] > 0) steps.add(new double[] { p.take()[Good.DIESEL.ordinal()] / q[Good.DIESEL.ordinal()], RunStop.DIESEL.ordinal() });
        steps.add(new double[] { p.wellsCrude(), RunStop.WELLS.ordinal() });
        double world = lubricantsWorldTakes(CRUDE_LITRES_PER_TONNE), lube = q[Good.LUBRICANTS.ordinal()];
        if (lube > world) steps.add(new double[] { Math.max(0, p.lubricantsHome()) / (lube - world), RunStop.LUBRICANTS.ordinal() });
        steps.removeIf(s -> !(s[0] > 0) || !(s[0] <= crude));
        steps.sort((a, b) -> Double.compare(a[0], b[0]));
        double from = 0, ran = 0, first = Double.NaN, last = Double.NaN;
        for (double[] s : steps) {
            if (!(s[0] > from)) continue;
            double at = (from + s[0]) / 2, cost = crudeCostAt(at, p), m = marginAt(at, q, p);
            double share = cost > 0 ? m / cost : m > 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
            if (Double.isNaN(first)) first = share;
            if (!(m > 0)) {
                RunStop stop = ran > 0 ? RunStop.values()[(int) stepAt(steps, ran)] : RunStop.FIRST;
                return new RunVerdict(ran / crude, stop, first, ran > 0 ? last : first, share);
            }
            ran = s[0];
            last = share;
            from = s[0];
        }
        return new RunVerdict(1, RunStop.NAMEPLATE, first, last, Double.NaN);
    }

    /** The step at `tonnes` (the first there, in steps' order). */
    private static double stepAt(List<double[]> steps, double tonnes) {
        for (double[] s : steps) if (s[0] == tonnes) return s[1];
        return RunStop.NAMEPLATE.ordinal();
    }

    /** A tonne's crude at the `at`-th tonne of the run: the wells' while they last, then the world's. */
    static double crudeCostAt(double at, RunPrices p) {
        return at < p.wellsCrude() ? p.crudeLocal() : p.crudeImport();
    }

    /** A tonne's margin at the `at`-th tonne of the run: its products (`q`, a tonne's, by Good's ordinal) at what they fetch there, less its crude and its other cost. */
    static double marginAt(double at, double[] q, RunPrices p) {
        double v = 0, world = lubricantsWorldTakes(CRUDE_LITRES_PER_TONNE);
        for (Good g : PRODUCTS) {
            int i = g.ordinal();
            double made = q[i];
            if (!(made > 0)) continue;
            double away = p.away()[i];
            if (g == Good.PETROL || g == Good.DIESEL) {
                double home = p.home()[i];
                v += made * (made * at < p.take()[i] && Double.isFinite(home) ? home : away);
            } else if (g == Good.LUBRICANTS && made > world && made * at > Math.max(0, p.lubricantsHome()) + world * at) {
                v += world * away;
            } else {
                v += made * away;
            }
        }
        return v - crudeCostAt(at, p) - p.perTonne();
    }

    /** This month's prices for the run (THE RUN), read after the clearing: petrol and diesel's net import price and the city's take of each on the trend (BusinessInvestment.forecast()), every product's net export price, the lubricants the city takes, the wells' crude and crude's two prices, and the vans' diesel a tonne of nameplate. */
    public RunPrices runPrices() {
        int n = Good.values().length;
        double[] home = new double[n], away = new double[n], take = new double[n];
        java.util.Arrays.fill(home, Double.NaN);
        BusinessInvestment plans = game == null ? null : game.getBusinessInvestment();
        for (Good g : PRODUCTS) {
            GoodsMarket m = markets == null ? null : markets.get(g);
            if (m == null) continue;
            double out = m.netExportPrice();
            away[g.ordinal()] = Double.isFinite(out) ? Math.max(0, out) : 0;
            if (g == Good.PETROL || g == Good.DIESEL) {
                double in = m.netImportPrice();
                home[g.ordinal()] = Double.isFinite(in) ? in : Double.NaN;
                double t = plans == null ? 0 : plans.forecast(this, m);
                take[g.ordinal()] = Double.isFinite(t) ? Math.max(0, t) : 0;
            }
        }
        GoodsMarket lub = markets == null ? null : markets.get(Good.LUBRICANTS);
        double lubricants = plans == null || lub == null ? 0 : plans.forecast(this, lub);
        Oil wells = game == null ? null : game.getSectors().oil();
        double lifted = wells == null ? 0 : wells.getPotentialOutput();
        GoodsMarket crude = markets == null ? null : markets.get(Good.CRUDE);
        double local = crude == null ? Double.NaN : crude.getLocalPrice(), imported = crude == null ? Double.NaN : crude.netImportPrice();
        GoodsMarket diesel = markets == null ? null : markets.get(Good.DIESEL);
        double nameplate = getInputAtCapacity(Good.CRUDE), dieselPrice = diesel == null ? 0 : diesel.getLocalPrice();
        double vans = nameplate > 0 && Double.isFinite(dieselPrice) ? vansNeeded() * DIESEL_LITRES_A_VAN_MONTH * dieselPrice / nameplate : 0;
        return new RunPrices(home, away, take, Double.isFinite(lubricants) ? Math.max(0, lubricants) : 0,
                Double.isFinite(lifted) ? Math.max(0, lifted) : 0, Double.isFinite(local) ? local : 0,
                Double.isFinite(imported) ? imported : 0, Double.isFinite(vans) ? Math.max(0, vans) : 0);
    }

    /** The run's verdict this month (runRule() on the campus at nameplate and runPrices()): what the month's end moves the run toward. */
    public RunVerdict runVerdict() {
        return runRule(flow(), runPrices());
    }

    /** The share the run moves to from `share` toward `toward` (pure): at most RUN_PACE either way, within nothing and nameplate. */
    public static double paced(double share, double toward) {
        double next = Math.max(share - RUN_PACE, Math.min(share + RUN_PACE, toward));
        return Math.max(0, Math.min(1, next));
    }

    /**
     * The month's end for the run: the share it ran at kept (at most
     * IDLE_MONTHS of them), and next month's struck toward the verdict at the
     * pace. Nothing with no crude unit standing, or no game; and a fixture
     * that holds the refiners (BusinessInvestment.holdSector()) holds their
     * run where it put it - the run is their decision, as what they build is.
     */
    private void strikeRun() {
        if (game == null || !(getInputAtCapacity(Good.CRUDE) > 0)) {
            runHistory.clear();
            return;
        }
        runHistory.add(runShare);
        while (runHistory.size() > SpreadPlanner.IDLE_MONTHS) runHistory.remove(0);
        BusinessInvestment plans = game.getBusinessInvestment();
        if (plans != null && plans.isHeld(key())) return;
        runShare = paced(runShare, runVerdict().share());
    }

    /* ----- the run in words (THE RUN): the refiners' page, the refinery's picture and the oil page ----- */

    /** A margin, a share of the crude's cost, in words: "loses 1.7%", "earns 2.3%". */
    static String marginWords(double share) {
        if (!Double.isFinite(share)) return share > 0 ? "earns more than its crude" : "loses all its crude";
        return String.format("%s %.1f%%", share > 0 ? "earns" : "loses", Math.abs(100 * share));
    }

    /**
     * Why the crude units run what they run, in a sentence or two (THE RUN):
     * where the run stops and what the barrels either side of it earn - and,
     * while the run is still moving there, the share it is heading for.
     * Empty with no crude unit (`v` from a campus with none).
     */
    public static String runWords(RunVerdict v, double share) {
        if (v == null || Double.isNaN(v.first())) return "";
        String why = switch (v.stop()) {
            case FIRST -> "No barrel pays: the first " + marginWords(v.next()) + " of what its crude costs, so the crude"
                    + " units stand - the wages, power and water paid all the same.";
            case NAMEPLATE -> "Every barrel pays: the last " + marginWords(v.last()) + " over its crude.";
            case PETROL, DIESEL -> String.format("It runs the barrels whose %s the city takes - the last %s over its crude;"
                    + " one past them, %s at the export price, %s.", v.stop() == RunStop.PETROL ? "petrol" : "diesel",
                    marginWords(v.last()), v.stop() == RunStop.PETROL ? "its petrol" : "its diesel", marginWords(v.next()));
            case WELLS -> "It runs the wells' crude - the last barrel " + marginWords(v.last()) + " over it; an imported barrel "
                    + marginWords(v.next()) + ".";
            case LUBRICANTS -> "It runs until the world's lubricants are taken - the last barrel " + marginWords(v.last())
                    + " over its crude; one past them " + marginWords(v.next()) + ".";
        };
        if (Math.abs(share - v.share()) > 1e-9) {
            why += String.format(" Heading for %.0f%% of nameplate, at most %.0f%% of it a month.", 100 * v.share(), 100 * RUN_PACE);
        }
        return why;
    }

    /** ...in a few words, for a table's cell: "every barrel pays", "first barrel loses 1.7%", "past the city's petrol, loses 0.7%". */
    public static String runShortWords(RunVerdict v) {
        if (v == null || Double.isNaN(v.first())) return "";
        return switch (v.stop()) {
            case FIRST -> "first barrel " + marginWords(v.next());
            case NAMEPLATE -> "every barrel pays";
            case PETROL -> "past the city's petrol, " + marginWords(v.next());
            case DIESEL -> "past the city's diesel, " + marginWords(v.next());
            case WELLS -> "past the wells' crude, " + marginWords(v.next());
            case LUBRICANTS -> "past the lubricants, " + marginWords(v.next());
        };
    }

    /** The most of nameplate the run used over the last IDLE_MONTHS months; NaN until it has run that long (THE RUN: what the spare is read off). */
    public double runUsed() {
        if (runHistory.size() < SpreadPlanner.IDLE_MONTHS) return Double.NaN;
        double most = 0;
        for (double s : runHistory) most = Math.max(most, s);
        return most;
    }

    /* =====================================================================
       THE SPREAD PLANNER (0.7.82, batch O5; spec-oil 2.4)

       What the refiners order, each month (plan()): of their buildings that
       pass the gates, the one that earns most on its cost at the city's own
       prices - SpreadPlanner's rule, which the materials chains will share.
       Two kinds of candidate (appraise()):

       A CONVERSION UNIT on its spread (RefineryFlow.spread()) at the city's
       values (cityValues(): each product at SpreadPlanner.cityValue()), on
       the feed it would find spare (spareFeed()) in the flow of the units
       standing struck at those values - the coker the residue and the heavy
       residue, the asphalt unit the heavy residue, a hydrocracker no more
       gas oil than the spare hydrogen treats - at the rate, less its running
       and standing costs. Its feed gate: spare for FEED_GATE of one.

       A CRUDE UNIT (rule 6) only while the city's own petrol and diesel not
       covered on the trend come to FEED_GATE of what it would make of them,
       or the wells' spare crude to FEED_GATE of what it runs - and then AS A
       PACKAGE (★ spec-oil 2.4, packageEstimate()). On imported crude a crude
       unit alone fails at the research's prices in any city (scratch-oil's
       topping-gate.txt), and then its units never come. So it is weighed
       with the conversion units of its own size (the smallest crude unit
       with each kind's smallest unit, a larger one with each kind's
       largest) whose spread and feed gates its cuts would pass, added one at
       a time, the widest margin first, at most PACKAGE_MOST_UNITS. The
       margin is its products' gain through the units standing at the city's
       values less its crude at K's prices - the wells' spare at the local
       price, the rest at the net import price - plus each unit's; the cost
       is all of theirs. Only the crude unit is ordered; its units follow on
       their own spreads. Its crude is the wells' spare at the grades they
       lift next (gradesAhead()) and the rest MEDIUM, the world's - the
       prototype's hypothetical mix.

       THE MONEY GATE is the planner's, one replaceable piece (SpreadPlanner.
       MoneyGate): in force, 1.25 times the interest at the real rate on what
       the order would borrow - Game.consider()'s own test. A package is
       tested on its whole margin against its whole cost.

       WHAT THE INVESTORS' TEST READS (estimatedMonthlyProfit()): a unit's
       earnings, and a crude unit's share of its package's by cost, so
       Game.consider()'s test on the crude unit's own cost is the package's
       on its, margin over cost being the same. The build card reads it too.

       PURE ON ITS OUTLOOK: appraise() reads the city through an Outlook (the
       units standing, the crude and its mix, the values, what is short, the
       wells' spare and its grades, the crude's two prices) and a
       SpreadPlanner.City (costs and gates), so RefineryCheck runs it in the
       prototype's frame as well as in a town.

       IDLE, THEN SHED: a kind whose units stood with no feed in the month's
       flow SpreadPlanner.IDLE_MONTHS running (extras idleMonths.<KIND>) is
       the one kind mayRetire() passes; retirementDemandAndCapacity() is then
       {0, its feed}, so the spare-capacity rule sells it while Refining
       loses money. ★ The crude units stay the distress rule's, as every
       maker's plant is, while no kind of unit stands idle long enough to go
       first.
       ===================================================================== */

    /** The most conversion units a crude unit is weighed with: the prototype's twelve rounds (spread.py, its package) - more than one of each of the seven kinds. */
    public static final int PACKAGE_MOST_UNITS = 12;

    /** Each kind's name, in Kind's order: what the idle months are saved by. */
    static final List<String> KIND_NAMES;
    static {
        List<String> names = new ArrayList<>();
        for (RefineryFlow.Kind k : RefineryFlow.Kind.values()) names.add(k.name());
        KIND_NAMES = Collections.unmodifiableList(names);
    }

    /** The refiners' spread planner: their money gate and their kinds' idle months. */
    private final SpreadPlanner planner = new SpreadPlanner();

    /** The refiners' spread planner (a probe sets its money gate for the counterfactual). */
    public SpreadPlanner planner() { return planner; }

    /**
     * Each product at the city's own price at the margin (SpreadPlanner.
     * cityValue()), by Good's ordinal: what the planner's spreads are struck
     * at. Since 0.7.105 (R1, THE PLANNER'S PRICES AT THE MARGIN, below)
     * petrol and diesel at the net import price, landed and hauled, while
     * the city imports any on
     * the trend past what its refinery makes and has coming (importsOf()),
     * and at the net export price once it does not: the market's own tally
     * misses the drivers' and the railway's draws (★ O5-1), so it cannot say.
     */
    public double[] cityValues() {
        double[] v = RefineryFlow.values(g -> SpreadPlanner.cityValue(markets == null ? null : markets.get(g)));
        if (markets == null) return v;
        for (Good g : BOUGHT_HERE) {
            GoodsMarket m = markets.get(g);
            if (m == null) continue;
            double value = importsOf(g) > 0 ? m.netImportPrice() : m.netExportPrice();
            v[g.ordinal()] = Double.isFinite(value) ? value : 0;
        }
        return v;
    }

    /** Each product's net export price, by Good's ordinal (0 for one the world will not take): what a litre past the city's imports fetches (R1). */
    public double[] awayValues() {
        return RefineryFlow.values(g -> {
            GoodsMarket m = markets == null ? null : markets.get(g);
            double p = m == null ? 0 : m.netExportPrice();
            return Double.isFinite(p) ? Math.max(0, p) : 0;
        });
    }

    /** Litres a month of a product the city imports on the trend past what its refinery makes at nameplate and has coming (R1; the Outlook's room, one product); never below nothing. */
    public double importsOf(Good g) {
        GoodsMarket m = markets == null ? null : markets.get(g);
        if (m == null) return 0;
        BusinessInvestment plans = game == null ? null : game.getBusinessInvestment();
        double wants = plans == null ? m.getImported() + getCapacity(g) : plans.forecast(this, m);
        double imports = wants - getCapacity(g) - getPipeline(g);
        return Double.isFinite(imports) ? Math.max(0, imports) : 0;
    }

    /*
     * THE PLANNER'S PRICES AT THE MARGIN (0.7.105, batch P6; Jerus's R1, on
     * why-the-refinery-loses.md). The planner valued a product nobody here
     * trades at its local price - the band's middle - and petrol and diesel
     * at the import price on every litre, so it saw a topping barrel by rail
     * earn 2.9% of its crude where a running one earned -1.7% to -3.9%, and
     * would order a plant that lost from its first month. Now:
     *
     *   - a product nobody here trades at its net export price - where its
     *     every litre goes (SpreadPlanner.cityValue());
     *   - petrol and diesel at the import price only up to what the city
     *     imports (the Outlook's imports, importsOf()), the litres past it at
     *     the net export price (worth());
     *   - AND AT THE RUN: an order is weighed at the share of nameplate the
     *     run would take with it standing (THE RUN: runRule() on the campus
     *     with it, the Outlook's run), so a unit behind crude units the run
     *     has stopped earns nothing until it, or the units with it, would
     *     make the barrel pay. The plant's rate is the planner's as before
     *     (plantRate(): the run is not in it).
     *
     * In the prototype's frame (spec-oil 2.4: the world's middle on every
     * litre, no run) nothing moves: the Outlook carries no run, its imports
     * have no end, and every estimate is O5's to the bit.
     */

    /** Petrol and diesel past the city's imports at the net export price (R1, pure): what `delta` (litres by Good's ordinal, a change in the products) is worth at the outlook's values, less, for each of the two, the litres past what the city imports at their value less their net export price. */
    static double worth(Outlook o, double[] delta) {
        double v = 0;
        for (Good g : PRODUCTS) v += delta[g.ordinal()] * o.values()[g.ordinal()];
        return v - pastImports(o, delta);
    }

    /** ...the part of it lost past the city's imports: petrol and diesel past what it imports, at their value less their net export price; nothing in the prototype's frame. */
    static double pastImports(Outlook o, double[] delta) {
        double lost = 0;
        for (Good g : BOUGHT_HERE) {
            int i = g.ordinal();
            double past = delta[i] - o.imports()[i];
            if (past > 0 && Double.isFinite(past)) lost += past * Math.max(0, o.values()[i] - o.away()[i]);
        }
        return lost;
    }

    /** The share of nameplate the run would take with the campus `f` standing (R1, THE RUN): runRule()'s, or the whole with no run in the outlook (the prototype's frame). */
    static double runWith(Outlook o, RefineryFlow.Flow f) {
        return o.run() == null ? 1 : runRule(f, o.run()).share();
    }

    /**
     * What the planner reads of the city (spec-oil 2.4).
     *
     * @param feed        litres a month each kind of unit standing takes, by RefineryFlow.Kind's ordinal
     * @param crude       tonnes a month the crude units standing run
     * @param mix         their crude mix, each grade's share in Deposit.Grade's order
     * @param values      each product's value, by Good's ordinal (cityValues())
     * @param room        litres a month of petrol and diesel the city wants on the trend past what its
     *                    refinery makes and has coming: what it imports
     * @param spareCrude  tonnes a month the wells could lift that no refinery takes (spareCrude())
     * @param spareMix    the grades of the wells' next crude (gradesAhead())
     * @param crudeLocal  a tonne of the wells' crude at home: crude's local price
     * @param crudeImport a tonne imported, landed and hauled: crude's net import price
     * @param lubricantsHome litres a month of lubricants the city's own buyers take on the trend (0.7.103):
     *                    with the world's share of the crude, what a lube plant may make (lubricantsRoom());
     *                    infinite in the prototype's frame, which has no cap
     * @param away        each product's net export price, by Good's ordinal (0.7.105, R1): what a litre of petrol
     *                    or diesel past the city's imports fetches; `values` in the prototype's frame
     * @param imports     litres a month of each product the city imports on the trend, by Good's ordinal (R1: petrol
     *                    and diesel are worth their value up to it); infinite in the prototype's frame
     * @param run         the run's prices (THE RUN), an order weighed at the run the campus would take with it;
     *                    null in the prototype's frame, which runs every barrel
     */
    public record Outlook(double[] feed, double crude, double[] mix, double[] values, double room,
                          double spareCrude, double[] spareMix, double crudeLocal, double crudeImport, double lubricantsHome,
                          double[] away, double[] imports, RunPrices run) {

        /** ...in the prototype's frame (spec-oil 2.4): no cap on lubricants. */
        public Outlook(double[] feed, double crude, double[] mix, double[] values, double room,
                       double spareCrude, double[] spareMix, double crudeLocal, double crudeImport) {
            this(feed, crude, mix, values, room, spareCrude, spareMix, crudeLocal, crudeImport, Double.POSITIVE_INFINITY);
        }

        /** ...in the prototype's frame with the lubricants capped (0.7.103): every litre at `values`, no run. */
        public Outlook(double[] feed, double crude, double[] mix, double[] values, double room,
                       double spareCrude, double[] spareMix, double crudeLocal, double crudeImport, double lubricantsHome) {
            this(feed, crude, mix, values, room, spareCrude, spareMix, crudeLocal, crudeImport, lubricantsHome,
                    values, endless(), null);
        }

        private static double[] endless() {
            double[] e = new double[Good.values().length];
            java.util.Arrays.fill(e, Double.POSITIVE_INFINITY);
            return e;
        }
    }

    /** This month's outlook, as plan() reads it. */
    public Outlook outlook(BusinessInvestment plans) {
        double room = 0;
        for (Good g : BOUGHT_HERE) room += plans.forecast(this, markets.get(g)) - getCapacity(g) - getPipeline(g);
        double spare = spareCrude();
        GoodsMarket crude = markets.get(Good.CRUDE);
        double local = crude.getLocalPrice(), imported = crude.netImportPrice();
        double home = plans.forecast(this, markets.get(Good.LUBRICANTS));
        // ...and since 0.7.105 (R1) what a litre past the city's imports fetches, what it imports, and the run's prices.
        double[] imports = new double[Good.values().length];
        java.util.Arrays.fill(imports, Double.POSITIVE_INFINITY);
        for (Good g : BOUGHT_HERE) imports[g.ordinal()] = importsOf(g);
        return new Outlook(unitFeed(false), getInputAtCapacity(Good.CRUDE), crudeMix.clone(), cityValues(), room,
                spare, spare > 0 ? wellsGradesAhead() : MEDIUM_MIX.clone(),
                Double.isFinite(local) ? local : 0, Double.isFinite(imported) ? imported : 0,
                Double.isFinite(home) ? Math.max(0, home) : 0, awayValues(), imports, runPrices());
    }

    /**
     * The grades the wells lift next: their month's potential laid along the
     * city's oil from what has been lifted (LandManager.oilRuns()); MEDIUM
     * with no wells. Since 0.7.93 each pool's wells along its own oil (THE
     * TWO OIL POOLS): the land wells' part on the ground pool's runs, the
     * platform wells' on the sea's (oilRunsAtSea()), the two blended by
     * their tonnes.
     */
    double[] wellsGradesAhead() {
        Oil wells = game == null ? null : game.getSectors().oil();
        LandManager land = game == null ? null : game.getLandManager();
        if (wells == null || land == null) return MEDIUM_MIX.clone();
        double all = wells.getPotentialOutput(), atSea = Math.min(all, wells.getCapacityAtSea() * wells.getOperatingRate());
        if (!(atSea > 0)) return gradesAhead(land.oilRuns(), land.getOilExtractedOnGround(), all);
        double onLand = all - atSea;
        return blend(gradesAhead(land.oilRuns(), land.getOilExtractedOnGround(), onLand), onLand,
                gradesAhead(land.oilRunsAtSea(), land.getOilExtractedAtSea(), atSea), atSea);
    }

    /**
     * The grades of the city's oil from `from` to `from + tonnes` tonnes, as
     * it is worked out (pure): each run's part of it ({end, grade's ordinal},
     * LandManager.oilRuns()), past the last run MEDIUM, as a mix in
     * Deposit.Grade's order; MEDIUM_MIX for no tonnes.
     */
    public static double[] gradesAhead(double[][] runs, double from, double tonnes) {
        if (!(tonnes > 0)) return MEDIUM_MIX.clone();
        double[] mix = new double[Deposit.Grade.values().length];
        double to = from + tonnes, start = 0, counted = 0;
        for (double[] r : runs) {
            double part = Math.min(r[0], to) - Math.max(start, from);
            if (part > 0) {
                mix[(int) r[1]] += part;
                counted += part;
            }
            start = r[0];
            if (start >= to) break;
        }
        if (tonnes - counted > 0) mix[Deposit.Grade.MEDIUM.ordinal()] += tonnes - counted;
        for (int i = 0; i < mix.length; i++) mix[i] /= tonnes;
        return mix;
    }

    /** Two runs' mixes as one: `a` over `wa` tonnes and `b` over `wb`, each grade's share of the two; MEDIUM_MIX for none. */
    static double[] blend(double[] a, double wa, double[] b, double wb) {
        double all = Math.max(0, wa) + Math.max(0, wb);
        if (!(all > 0)) return MEDIUM_MIX.clone();
        double[] mix = new double[Deposit.Grade.values().length];
        for (int i = 0; i < mix.length; i++) {
            if (wa > 0) mix[i] += a[i] * wa / all;
            if (wb > 0) mix[i] += b[i] * wb / all;
        }
        return mix;
    }

    /** The feed a kind of unit would find spare in a flow (the prototype's): its stream's; the coker's the residue and the heavy residue; a hydrocracker's no more gas oil than the spare hydrogen treats. */
    public static double spareFeed(RefineryFlow.Flow f, RefineryFlow.Kind k) {
        return switch (k) {
            case COKER -> f.spare(RefineryFlow.Stream.RESIDUE) + f.spare(RefineryFlow.Stream.HEAVY_RESIDUE);
            case HYDROCRACKER -> Math.min(f.spare(RefineryFlow.Stream.GAS_OIL), f.spareHydrogen() / RefineryFlow.HYDROGEN_USED);
            default -> f.spare(k.feed());
        };
    }

    /** What a kind's feed is called in a refusal: its stream, and a hydrocracker's the hydrogen with it. */
    static String feedWords(RefineryFlow.Kind k) {
        return k == RefineryFlow.Kind.HYDROCRACKER ? "gas oil with hydrogen to treat it"
                : k == RefineryFlow.Kind.COKER ? "residue" : k.feed().words();
    }

    /**
     * Every building of the refiners' weighed at the gates, in `templates`'
     * order (pure in its arguments): each conversion unit on its spread, each
     * crude unit as a package. The flow they are weighed in is the units
     * standing at the outlook's values.
     */
    public static List<SpreadPlanner.Candidate> appraise(Outlook o, List<BuildingsTemplate> templates, SpreadPlanner.City city) {
        RefineryFlow.Flow flow = RefineryFlow.solve(o.feed(), o.crude(), o.mix(), o.values());
        List<SpreadPlanner.Candidate> out = new ArrayList<>();
        for (BuildingsTemplate t : templates) {
            if (isConversionUnit(t)) out.add(unitEstimate(o, flow, t, city));
            else if (isCrudeUnit(t)) out.add(packageEstimate(o, flow, t, templates, city));
        }
        return out;
    }

    /** One conversion unit weighed at an outlook (pure): unitEstimate()'s, its lubricants capped at the outlook's, and since 0.7.105 (R1) its petrol and diesel past the city's imports at the net export price, at the run the campus would take with it standing. */
    public static SpreadPlanner.Candidate unitEstimate(Outlook o, RefineryFlow.Flow flow, BuildingsTemplate t, SpreadPlanner.City city) {
        return unitEstimate(flow, t, city, o.lubricantsHome(), o);
    }

    /** One conversion unit weighed (pure): its spread on the feed it would find spare in `flow`, at most its own, at the rate less its costs; its feed gate FEED_GATE of one. In the prototype's frame: no cap on lubricants. */
    public static SpreadPlanner.Candidate unitEstimate(RefineryFlow.Flow flow, BuildingsTemplate t, SpreadPlanner.City city) {
        return unitEstimate(flow, t, city, Double.POSITIVE_INFINITY);
    }

    /** ...a lube unit on no more gas oil than makes the lubricants the world and the city's `lubricantsHome` would still take (0.7.103; lubricantsRoom()). */
    public static SpreadPlanner.Candidate unitEstimate(RefineryFlow.Flow flow, BuildingsTemplate t, SpreadPlanner.City city,
                                                      double lubricantsHome) {
        return unitEstimate(flow, t, city, lubricantsHome, null);
    }

    /** ...and with an outlook (0.7.105, R1), past the city's imports at the net export price and at the run with it standing; none, as before. */
    private static SpreadPlanner.Candidate unitEstimate(RefineryFlow.Flow flow, BuildingsTemplate t, SpreadPlanner.City city,
                                                       double lubricantsHome, Outlook o) {
        RefineryFlow.Kind k = t.refineryUnit();
        double one = t.feedPerMonth(), spare = Math.max(0, spareFeed(flow, k));
        String feed = spare >= SpreadPlanner.FEED_GATE * one ? null
                : String.format("%,.0f L a month of %s spare, under %.0f%% of %s's %,.0f L", spare, feedWords(k),
                        SpreadPlanner.FEED_GATE * 100, a(t.getName()), one);
        if (k == RefineryFlow.Kind.LUBE && feed == null) {
            double room = lubricantsRoom(flow, lubricantsHome), most = room / lubricantsPerLitreOfFeed();
            if (most < spare) {
                spare = most;
                if (spare < SpreadPlanner.FEED_GATE * one) {
                    feed = String.format("%,.0f L a month of lubricants the world and the city would still take, under %.0f%% of"
                                    + " %s's %,.0f L", room, SpreadPlanner.FEED_GATE * 100, a(t.getName()),
                            one * lubricantsPerLitreOfFeed());
                }
            }
        }
        double gross = flow.spread(k) * Math.min(spare, one);
        if (o != null && (o.run() != null || pastImports(o, madeOf(k, Math.min(spare, one))) > 0)) {
            // ...petrol and diesel past the city's imports at the net export price, at the run with it standing (R1).
            gross -= pastImports(o, madeOf(k, Math.min(spare, one)));
            double[] with = o.feed().clone();
            with[k.ordinal()] += one;
            gross *= runWith(o, RefineryFlow.solve(with, o.crude(), o.mix(), o.values()));
        }
        double earns = SpreadPlanner.earns(t, gross, city);
        return SpreadPlanner.judge(t, earns, city.cost(t), feed, city, null);
    }

    /** What `litres` of a kind's feed make with nothing downstream, by Good's ordinal (RefineryFlow.madeAlone()). */
    static double[] madeOf(RefineryFlow.Kind k, double litres) {
        double[] out = new double[Good.values().length];
        for (Map.Entry<Good, Double> e : RefineryFlow.madeAlone(k, litres).entrySet()) out[e.getKey().ordinal()] += e.getValue();
        return out;
    }

    /**
     * One crude unit weighed as a package (pure; spec-oil 2.4, rule 6 and the
     * package): a candidate only while the city's petrol and diesel short on
     * the trend come to FEED_GATE of what it would make of them, or the
     * wells' spare to FEED_GATE of its crude. Its margin: the products its
     * crude adds through the units standing (`before` is their flow) at the
     * outlook's values, less the crude at K's prices, at the rate, less its
     * costs - then each unit of its own size its cuts would feed, the widest
     * margin first, added with its margin and cost. The candidate is the
     * crude unit's, its earnings and cost the package's.
     */
    public static SpreadPlanner.Candidate packageEstimate(Outlook o, RefineryFlow.Flow before, BuildingsTemplate t,
                                                          List<BuildingsTemplate> templates, SpreadPlanner.City city) {
        double add = t.uses(Good.CRUDE);
        double local = Math.min(add, Math.max(0, o.spareCrude()));
        double[] own = blend(o.spareMix(), local, MEDIUM_MIX, add - local);
        double makes = boughtHere(slate(add, own));
        String feed = o.room() >= SpreadPlanner.FEED_GATE * makes || o.spareCrude() >= SpreadPlanner.FEED_GATE * add ? null
                : String.format("%,.0f L of petrol and diesel and %,.0f t of crude to spare: none for another refinery",
                        Math.max(0, o.room()), Math.max(0, o.spareCrude()));
        double run = o.crude() + add;
        double[] mix = blend(o.mix(), o.crude(), own, add);
        double[] values = o.values();
        RefineryFlow.Flow after = RefineryFlow.solve(o.feed(), run, mix, values);
        double gain = 0;
        for (Good g : PRODUCTS) gain += (after.of(g) - before.of(g)) * values[g.ordinal()];
        double crude = local * o.crudeLocal() + (add - local) * o.crudeImport();
        double earns = SpreadPlanner.earns(t, gain - crude, city), cost = city.cost(t);
        // ...each building's gross, for the run and the city's imports (0.7.105, R1) below.
        List<Double> gross = new ArrayList<>();
        gross.add(gain - crude);
        // ...and the units of its own size its cuts would feed, one at a time.
        int size = sizeOf(t, templates);
        double[] with = o.feed().clone();
        List<BuildingsTemplate> units = new ArrayList<>();
        for (int round = 0; round < PACKAGE_MOST_UNITS; round++) {
            RefineryFlow.Flow f = RefineryFlow.solve(with, run, mix, values);
            BuildingsTemplate next = null;
            double best = 0;
            for (RefineryFlow.Kind k : RefineryFlow.Kind.values()) {
                BuildingsTemplate u = unitOfSize(k, size, templates);
                if (u == null || !(f.spread(k) > 0)) continue;
                double spare = Math.max(0, spareFeed(f, k));
                // ...a lube unit on no more gas oil than the lubricants the world and the city would take (0.7.103).
                if (k == RefineryFlow.Kind.LUBE) spare = Math.min(spare, lubricantsRoom(f, o.lubricantsHome()) / lubricantsPerLitreOfFeed());
                if (spare < SpreadPlanner.FEED_GATE * u.feedPerMonth()) continue;
                double margin = SpreadPlanner.earns(u, f.spread(k) * Math.min(spare, u.feedPerMonth()), city);
                if (margin > best) {
                    best = margin;
                    next = u;
                }
            }
            if (next == null) break;
            gross.add(bestGross(f, next, o));
            with[next.refineryUnit().ordinal()] += next.feedPerMonth();
            earns += best;
            cost += city.cost(next);
            units.add(next);
        }
        if (o.run() != null) {
            // ...the package's petrol and diesel past the city's imports at the net export price, and every
            // building's gross at the run the campus would take with it all standing (0.7.105, R1).
            RefineryFlow.Flow all = RefineryFlow.solve(with, run, mix, values);
            double[] delta = new double[Good.values().length];
            for (Good g : PRODUCTS) delta[g.ordinal()] = all.of(g) - before.of(g);
            double share = runWith(o, all);
            earns = SpreadPlanner.earns(t, (gross.get(0) - pastImports(o, delta)) * share, city);
            for (int i = 0; i < units.size(); i++) earns += SpreadPlanner.earns(units.get(i), gross.get(i + 1) * share, city);
        }
        return SpreadPlanner.judge(t, earns, cost, feed, city, units);
    }

    /** A unit's gross in a package round: its spread on the feed it would find spare in `f`, at most its own, its lubricants capped at the outlook's (packageEstimate()). */
    private static double bestGross(RefineryFlow.Flow f, BuildingsTemplate u, Outlook o) {
        RefineryFlow.Kind k = u.refineryUnit();
        double spare = Math.max(0, spareFeed(f, k));
        if (k == RefineryFlow.Kind.LUBE) spare = Math.min(spare, lubricantsRoom(f, o.lubricantsHome()) / lubricantsPerLitreOfFeed());
        return f.spread(k) * Math.min(spare, u.feedPerMonth());
    }

    /** A crude unit's size among the crude units of `templates`: 0 for the one taking the least crude, and so on. */
    static int sizeOf(BuildingsTemplate t, List<BuildingsTemplate> templates) {
        int rank = 0;
        for (BuildingsTemplate c : templates) {
            if (isCrudeUnit(c) && c.uses(Good.CRUDE) < t.uses(Good.CRUDE)) rank++;
        }
        return rank;
    }

    /** A kind's unit of a size: its templates by feed, the smallest first, the one at `size` (its largest past them); null for a kind with none. */
    static BuildingsTemplate unitOfSize(RefineryFlow.Kind k, int size, List<BuildingsTemplate> templates) {
        List<BuildingsTemplate> of = new ArrayList<>();
        for (BuildingsTemplate u : templates) if (isConversionUnit(u) && u.refineryUnit() == k) of.add(u);
        if (of.isEmpty()) return null;
        of.sort(java.util.Comparator.comparingDouble(BuildingsTemplate::feedPerMonth));
        return of.get(Math.min(size, of.size() - 1));
    }

    /** "a Small Coker", "an Oil Refinery". */
    static String a(String name) {
        return (name != null && !name.isEmpty() && "AEIOUaeiou".indexOf(name.charAt(0)) >= 0 ? "an " : "a ") + name;
    }

    /** A candidate named, with the units it was weighed with: "an Oil Refinery with a Small Lube Plant and a Small Coker behind it". */
    static String named(SpreadPlanner.Candidate c) {
        String name = a(c.template().getName());
        List<BuildingsTemplate> with = c.with();
        if (with.isEmpty()) return name;
        StringBuilder s = new StringBuilder(name).append(" with ");
        for (int i = 0; i < with.size(); i++) {
            if (i > 0) s.append(i == with.size() - 1 ? " and " : ", ");
            s.append(a(with.get(i).getName()));
        }
        return s.append(" behind it").toString();
    }

    /**
     * Whether to build, and what (THE SPREAD PLANNER): one order in flight;
     * of the refiners' buildings that pass the gates, the one that earns
     * most on its cost; with none, ground if ground was all that stood in
     * the way, else the best-fed candidate's refusal.
     */
    @Override
    public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game) {
        String sector = key();
        // One order in flight, crude units on site aside (0.7.105, R3: ONE ORDER IN FLIGHT, below).
        int crudeOnSite = crudeUnitsOnSite();
        if (buildings.getUnderConstructionBySector(sector) - crudeOnSite >= BusinessInvestment.MAX_CONCURRENT_ORDERS) {
            return BusinessInvestment.Decision.no(sector, "already building");
        }
        Outlook o = outlook(plans);
        List<BuildingsTemplate> templates = new ArrayList<>(buildings.getTemplatesBySector(sector));
        if (crudeOnSite > 0) templates.removeIf(Refining::isCrudeUnit);
        List<SpreadPlanner.Candidate> all = appraise(o, templates, planner.city(this, plans, game));
        SpreadPlanner.Candidate best = SpreadPlanner.best(all);
        // ...and a Tank Farm while a port holds their crude back for room (0.7.86), weighed beside the units on its cost.
        SpreadPlanner.Candidate farm = farmCandidate(plans, game);
        if (farm != null && farm.passes() && farm.earns() > 0 && farm.cost() > 0 && (best == null || farm.score() > best.score())) {
            return new BusinessInvestment.Decision(sector, farm.template(), 1, String.format(
                    "a Tank Farm saves $%,.1fk a month of freight on the crude a port holds back for room, on $%,.0fk, %.2f%%"
                            + " a month", farm.earns(), farm.cost(), 100 * farm.score()), true);
        }
        if (best != null) {
            return new BusinessInvestment.Decision(sector, best.template(), 1, String.format(
                    "%s earns $%,.1fk a month on $%,.0fk at the city's prices, %.2f%% a month", named(best), best.earns(),
                    best.cost(), 100 * best.score()), true);
        }
        SpreadPlanner.Candidate land = SpreadPlanner.bestBlockedByLand(all);
        if (land != null) return BusinessInvestment.Decision.noLand(sector, plans.landReason(land.template()));
        SpreadPlanner.Candidate fed = SpreadPlanner.bestFed(all);
        if (fed == null && crudeOnSite > 0) {
            return BusinessInvestment.Decision.no(sector, "a crude unit on site, and no stream spare for a unit");
        }
        if (fed == null) {
            return BusinessInvestment.Decision.no(sector, o.room() > 0 || o.spareCrude() > 0
                    ? String.format("%,.0f L of petrol and diesel and %,.0f t of crude to spare: none for another refinery,"
                            + " and no stream spare for a unit", Math.max(0, o.room()), o.spareCrude())
                    : "the city's petrol and diesel are covered already, its wells have no crude to spare, and no stream"
                            + " is spare for a unit");
        }
        // ...worded by what matters most: that it would not pay, then the staff, then the ground.
        String money = fed.refusal(SpreadPlanner.Gate.MONEY), staff = fed.refusal(SpreadPlanner.Gate.STAFF);
        return BusinessInvestment.Decision.no(sector, money != null
                ? named(fed) + " " + money + (fed.earns() > 0 ? "" : ": it does not pay")
                : staff != null ? staff : fed.why());
    }

    /*
     * ONE ORDER IN FLIGHT, CRUDE UNITS ON SITE ASIDE (0.7.105, batch P6;
     * Jerus's R3). The refiners held to MAX_CONCURRENT_ORDERS as every
     * sector does, counting every refiners' building on site, whoever
     * ordered it - and Jerus's own order of 14 Crude Units, on site for
     * want of builders (runs/fixT1-notes.md: 93.6% of all the points his
     * sites owe), held their planner "already building" whatever any unit's
     * spread. ★ So a crude unit on site does not count against the one
     * order: while only crude units are on site the refiners may order a
     * conversion unit or a Tank Farm - one at a time, as before - and no
     * other crude unit (rule 6 counts those on site as coming anyway). It
     * is the refiners' rule alone: every other sector keeps its one order,
     * however its sites are held (T1's nine sectors each wait behind a site
     * of their own, which counting only a sector's own orders would not
     * have freed either).
     */

    /** The crude units on site, anyone's order (R3). */
    int crudeUnitsOnSite() {
        int n = 0;
        if (buildings == null) return 0;
        for (ham.citybuildersim.BuildingsStacks s : buildings.getStacksUnderConstruction()) {
            if (isCrudeUnit(s.getBuilding()) && s.getUnderConstruction() > 0) n += s.getUnderConstruction();
        }
        return n;
    }

    /** One building of the refiners' weighed now (THE SPREAD PLANNER), at this month's outlook and the game's gates. */
    public SpreadPlanner.Candidate appraise(BuildingsTemplate t, BusinessInvestment plans) {
        SpreadPlanner.City city = planner.city(this, plans, game);
        Outlook o = outlook(plans);
        RefineryFlow.Flow flow = RefineryFlow.solve(o.feed(), o.crude(), o.mix(), o.values());
        return isConversionUnit(t) ? unitEstimate(o, flow, t, city)
                : packageEstimate(o, flow, t, buildings.getTemplatesBySector(key()), city);
    }

    /**
     * What one more building of the refiners' would earn its owner a month
     * (THE SPREAD PLANNER): a conversion unit's earnings on the feed it would
     * find, and a crude unit's share of its package's earnings by cost - what
     * Game.consider() tests the order on, and the build card shows.
     */
    @Override
    public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans) {
        if (t == null || markets == null || buildings == null || !(isConversionUnit(t) || isCrudeUnit(t))) {
            return t == null ? 0 : plans.estimatedMakerProfit(this, t, madeBy(t));
        }
        SpreadPlanner.Candidate c = appraise(t, plans);
        if (isConversionUnit(t) || !(c.cost() > 0)) return c.earns();
        return c.earns() * plans.getCostOf(t, 1) / c.cost();
    }

    /* ----- idle, then shed ----- */

    /** The month's end for the units: each kind standing counted idle (no feed in the month's flow) or working. */
    private void noteIdleUnits() {
        double[] standing = unitFeed(false);
        boolean any = false;
        for (double f : standing) any |= f > 0;
        RefineryFlow.Flow f = any ? flow() : null;
        for (RefineryFlow.Kind k : RefineryFlow.Kind.values()) {
            planner.noteMonth(k.name(), standing[k.ordinal()] > 0, f == null || !(f.run(k) > 0));
        }
    }

    /** Litres a month the kinds idle long enough to shed would take: their units standing. */
    public double idleFeed() {
        double[] standing = unitFeed(false);
        double idle = 0;
        for (RefineryFlow.Kind k : RefineryFlow.Kind.values()) {
            if (planner.mayShed(k.name())) idle += standing[k.ordinal()];
        }
        return idle;
    }

    /** Months a kind of unit has stood idle running (extras idleMonths.<KIND>). */
    public int idleMonths(RefineryFlow.Kind k) { return planner.idleMonths(k.name()); }

    /** A conversion unit only once its kind has stood idle long enough; a crude unit only while no kind has (THE SPREAD PLANNER); a Tank Farm never (it makes nothing either rule could measure, and neither sold one). */
    @Override
    public boolean mayRetire(BuildingsTemplate t) {
        if (isConversionUnit(t)) return planner.mayShed(t.refineryUnit().name());
        if (isTankFarm(t)) return false;
        return !(idleFeed() > 0);
    }

    /**
     * The spare-capacity rule's measure: {0, the idle kinds' feed} while a
     * kind stands idle long enough to shed - and otherwise, since 0.7.105
     * (THE RUN), {what the run used, the crude units' nameplate} in tonnes
     * once the run has gone IDLE_MONTHS months without using all of it: the
     * most of nameplate it ran in those months is what is used, and the rest
     * is spare, which the rule sells while Refining loses money - a quarter
     * of the most-held crude unit a month at most, never into what the run
     * used. Neither, none: a price-taking exporter shrinks on distress only
     * (HeavyIndustry's rule).
     */
    @Override
    public double[] retirementDemandAndCapacity(Game game) {
        double idle = idleFeed();
        if (idle > 0) return new double[] { 0, idle };
        double used = runUsed(), nameplate = getInputAtCapacity(Good.CRUDE);
        return used < 1 && nameplate > 0 ? new double[] { used * nameplate, nameplate } : null;
    }
}
