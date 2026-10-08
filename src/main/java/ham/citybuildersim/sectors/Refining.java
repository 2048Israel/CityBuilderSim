package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.BusinessInvestment;
import ham.citybuildersim.Deposit;
import ham.citybuildersim.Formats;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.GoodsMarket;
import ham.citybuildersim.Sector;
import ham.citybuildersim.Sectors;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * The refinery. THE SEVENTEENTH SECTOR (0.7.62, batch K; the project's
 * spec-land.md 2.7), and since 0.7.76 a crude unit that cuts a barrel into
 * what a real one does (batch O1; runs/spec-oil.md 2.1 and 2.3).
 *
 * HEAVY INDUSTRY'S SHAPE, ON OIL. A refinery buys crude - the city's wells'
 * first, the world's for the rest (CRUDE is importable, so the shortfall is
 * the template's) - and makes the products of its slate (THE SLATE, below)
 * into its tanks: petrol, diesel, jet fuel, naphtha, petroleum gas and fuel
 * oil. The city's drivers (Motoring, at 6d) draw the petrol and its railway
 * (Rail.haul()) the diesel, off those tanks before the world; the rest
 * leaves by the export-bound line until something here buys it.
 *
 * WHY. Until 0.7.76 it made FUEL, a thousand litres from a tonne - 86% of a
 * barrel as transport fuels and the rest left out - so one refinery was
 * 8.3M litres of whatever the city burned, and its gate asked for a whole
 * plant's worth of that. A tonne of medium crude makes 70 L of petrol and
 * 169 L of diesel, and the rest is products the city does not burn.
 *
 * WHEN TO BUILD ONE is the one thing it knows (plan()): only for a whole
 * plant's worth of the city's own petrol and diesel not yet covered, or of
 * crude its wells lift that no refinery takes - HeavyIndustry's rule - and
 * then while what its slate would fetch clears above what it costs to make
 * at nameplate: the investors' own estimate over the slate
 * (BusinessInvestment.estimatedMakerProfit()), which values what the city
 * will take at the local price and the rest at the export price, with its
 * crude at the local price for the wells' spare and at what an import costs
 * landed and hauled for the rest (estimatedMonthlyProfit()). The interest
 * test (BusinessInvestment.servicesItsOwnDebt()) holds it as it holds any
 * plant.
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

    /**
     * What a building makes a month at nameplate, by good (pure, 0.7.76): a
     * crude unit's slate of its crude, and every other building's template
     * (BuildingsTemplate.goodsMade()). What the build card values and lists.
     */
    public static Map<Good, Double> madeBy(BuildingsTemplate t) {
        if (t == null) return Collections.emptyMap();
        return isCrudeUnit(t) ? slate(t.uses(Good.CRUDE)).products() : t.goodsMade();
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
       star 4), and the planner still judges a new crude unit on medium
       crude (madeBy(), the gate's plant's worth): the crude a unit not yet
       built would run is O5's to price (spec-oil 2.4, the package).
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

    /** The month's markets have cleared: its crude mix struck from what the crude units bought, at home and abroad. */
    @Override
    public void endOfMonth(Game game) {
        Sector.Input in = inputRow(Good.CRUDE);
        double local = in == null ? 0 : in.boughtLocal, imported = in == null ? 0 : in.imported;
        double[] lifted = game == null || game.getLandManager() == null ? null : game.getLandManager().getOilLiftedByGrade();
        double[] mix = monthsMix(local, lifted, imported);
        System.arraycopy(mix, 0, crudeMix, 0, crudeMix.length);
    }

    @Override
    protected void saveExtras(Map<String, Double> extras) {
        for (Deposit.Grade g : Deposit.Grade.values()) extras.put("crudeMix." + g.name(), crudeMix[g.ordinal()]);
    }

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
    }

    @Override
    public void reset() {
        super.reset();
        System.arraycopy(MEDIUM_MIX, 0, crudeMix, 0, crudeMix.length);
    }

    /* ----- the reads, off the slate (spec-oil 2.3, 6) ----- */

    /** Nameplate output of a product a month: the slate of the crude its standing crude units take, at the month's crude mix (0.7.79). */
    @Override
    public double getCapacity(Good g) {
        return slateOf(getInputAtCapacity(Good.CRUDE)).of(g);
    }

    /** ...and of the crude units on site, which counts as supply for the planner. */
    @Override
    public double getPipeline(Good g) {
        return slateOf(crudeOnSite()).of(g);
    }

    /**
     * Tank room for a product: the tankage of its buildings (each template's
     * `stock`, litres) times the product's share of the slate - an Oil
     * Refinery's 25M L shared as its run is, so each product has the room its
     * share of the run would fill. A product the slate makes none of has none.
     */
    @Override
    public double getStockCapacity(Good g) {
        double tankage = super.getStockCapacity(g);
        if (!(tankage > 0)) return 0;
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

    /** The crude the crude units on site will take, in tonnes a month. */
    private double crudeOnSite() {
        return buildings == null ? 0 : buildings.underConstructionBySector(key(), t -> t.uses(Good.CRUDE));
    }

    /** Tonnes of crude the refineries want this month, at the rate they are running. */
    public double getCrudeDemand() {
        return getInputAtCapacity(Good.CRUDE) * getOperatingRate();
    }

    /** What a crude unit is measured in, for the retirement rules: the crude it takes (its products are its slate). */
    @Override
    public double unitsOf(BuildingsTemplate t) {
        return t == null ? 0 : t.uses(Good.CRUDE);
    }

    /** The products the city buys, a month, at a crude unit's nameplate: its petrol and its diesel. */
    public static double boughtHere(Slate s) {
        double v = 0;
        for (Good g : BOUGHT_HERE) v += s.of(g);
        return v;
    }

    /**
     * Whether to build another refinery: the best template by what the
     * investors' estimate says it would clear a month over its cost, while
     * that clears at all (spec-land 2.7) - and only for one of the city's own
     * two reasons, HeavyIndustry's shape, each a whole plant's worth: the
     * city's own petrol and diesel not yet covered by as much as the plant
     * makes of them (the drivers and the railway draw that much more than the
     * refineries standing and on site make), or its wells lifting as much
     * crude as the plant takes that no refinery is taking - as a mill is built
     * only into ore the mines have spare, all of its input.
     *
     * THE GATE IS THE MEASUREMENT. Without it the playtest's city stood 120
     * refineries at month 4,002 (scratch-k pt1): a tonne of crude bought at
     * .60 and sold abroad as a thousand litres at .0007 leaves $100 a tonne,
     * and a refinery's crew and power took less than that, so every refinery
     * the interest test passed was one more exporter on imported crude - the
     * unbounded export market at a fixed floor Good's header names (the van
     * plants, the locomotive works), here at a hundred posts a plant. With
     * the gate at any fuel not covered (pt2) the first refinery stood at
     * month 438 in a town of 800 people, exporting all but a few thousand of
     * its 8.3M litres a month, and a 300-house fixture town built one.
     *
     * ...IN PETROL AND DIESEL SINCE 0.7.76 (O1): a plant's worth is the 1.98M
     * litres of the two its 8,300 t make, where it was 8.3M litres of FUEL -
     * so the gate opens about four times as early in a city's demand.
     */
    @Override
    public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game) {

        String sector = key();
        if (buildings.getUnderConstructionBySector(sector) >= BusinessInvestment.MAX_CONCURRENT_ORDERS) {
            return BusinessInvestment.Decision.no(sector, "already building");
        }

        // The city's own petrol and diesel, not yet covered, and its own crude, not yet taken.
        double room = 0;
        for (Good g : BOUGHT_HERE) room += plans.forecast(this, markets.get(g)) - getCapacity(g) - getPipeline(g);
        double spare = spareCrude();
        if (!(room > 0) && !(spare > 0)) {
            return BusinessInvestment.Decision.no(sector,
                    "the city's petrol and diesel are covered already, and its wells have no crude to spare");
        }
        boolean anyWhole = false;

        BuildingsTemplate best = null;
        double bestScore = 0;
        Staffing staffingHold = null;
        String staffingHoldName = null;
        for (BuildingsTemplate t : buildings.getTemplatesBySector(sector)) {
            if (!isCrudeUnit(t)) continue;
            // A whole plant's worth of the city's own petrol and diesel, or of the wells' spare crude (HeavyIndustry's rule).
            if (boughtHere(slate(t.uses(Good.CRUDE))) > room && t.uses(Good.CRUDE) > spare) continue;
            anyWhole = true;
            // ...and one the city could staff (0.7.18; see Sector.staffing()).
            Staffing staffing = staffing(t);
            if (!staffing.passes()) {
                if (staffingHold == null || staffing.share > staffingHold.share) {
                    staffingHold = staffing;
                    staffingHoldName = t.getName();
                }
                continue;
            }
            double cost = plans.getCostOf(t, 1);
            if (cost <= 0) continue;
            double score = estimatedMonthlyProfit(t, plans) / cost;
            if (score > bestScore) {
                bestScore = score;
                best = t;
            }
        }

        if (best == null) {
            if (staffingHold != null) {
                return BusinessInvestment.Decision.no(sector, staffingHold.why(staffingHoldName));
            }
            if (!anyWhole) {
                return BusinessInvestment.Decision.no(sector, String.format(
                        "%,.0f L of petrol and diesel and %,.0f t of crude to spare: none for another refinery",
                        Math.max(0, room), spare));
            }
            return BusinessInvestment.Decision.no(sector, "its products at today's prices would not clear a refinery's cost");
        }
        if (plans.plotsAvailableFor(best) < 1) {
            return BusinessInvestment.Decision.noLand(sector, plans.landReason(best));
        }
        GoodsMarket petrol = markets.get(Good.PETROL), diesel = markets.get(Good.DIESEL);
        return new BusinessInvestment.Decision(sector, best, 1,
                String.format("petrol at %s and diesel at %s a litre clear a refinery's cost",
                        Formats.INSTANCE.amount(petrol.getLocalPrice()), Formats.INSTANCE.amount(diesel.getLocalPrice())), true);
    }

    /**
     * What one more refinery would clear a month: the investors' estimate
     * over its slate (BusinessInvestment.estimatedMakerProfit()), with its
     * crude at what it would actually cost - the wells' spare crude at the
     * local price and the rest at what an import costs landed and hauled
     * (netImportPrice()).
     *
     * HEAVY INDUSTRY'S SPARE-ORE RULE AS A PRICE RATHER THAN A GATE. A mill is
     * not built past the ore the mines have spare; a refinery can import its
     * crude, so it is not held back - but the estimate read crude at the local
     * price, which with nothing traded is the middle of the band (0.55, where
     * the city would pay 0.60), and with the wells' crude all spoken for is the
     * price that crude clears at, not what this plant's 8,300 t more would
     * fetch. Measured in a 1,500-person town (scratch-k KProbe): the
     * template's estimate ordered a refinery for 2,455 litres a month of the
     * city's own fuel, an exporter on crude it would have had to import.
     */
    @Override
    public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans) {
        double estimate = plans.estimatedMakerProfit(this, t, madeBy(t));
        if (t == null || markets == null) return estimate;
        double need = t.uses(Good.CRUDE);
        if (!(need > 0)) return estimate;
        double imported = Math.max(0, need - spareCrude());
        GoodsMarket crude = markets.get(Good.CRUDE);
        double dearer = crude.netImportPrice() - crude.getLocalPrice();
        if (!(imported > 0) || !(dearer > 0) || !Double.isFinite(dearer)) return estimate;
        return estimate - imported * dearer * BusinessInvestment.operatingRateOf(getOperatingRate());
    }

    /**
     * The crude the city's wells could lift this month that no refinery
     * standing or on site will take, in tonnes (never below nothing).
     */
    public double spareCrude() {
        Oil wells = game == null ? null : game.getSectors().oil();
        if (wells == null) return 0;
        return Math.max(0, wells.getPotentialOutput() - getCrudeDemand() - crudeOnSite());
    }

    /** A price-taking exporter always sells what it makes: it shrinks on distress only (HeavyIndustry's rule). */
    @Override
    public double[] retirementDemandAndCapacity(Game game) { return null; }
}
