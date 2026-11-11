package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * One month's merit order for the city's grid: the rows that make power, the load a month asks of them cut in six half-blocks,
 * and the price each half-block clears at.
 *
 * WHY. The utility used to set the month's power by one sum - production over consumption, capped at 1 - so a Coal Power Plant
 * burned nothing, a Wind Farm counted as if the wind never stopped, a short month was short by the same fraction at every hour,
 * and the tariff was a constant (D$10 a kW a month). The electricity study (docs/studies/spec-electricity.md, sections 2.2, 2.5,
 * 2.6, 2.7; prototype in docs/studies/electricity) cuts the month in three load blocks, each in a windy and a calm
 * half, runs the works' surplus first, wind next, hydro levelling the load with its month of water, and the fuelled rows by
 * their marginal cost, and prices each half-block at the last row it ran (or at VOLL when it is left short). Jerus took that
 * study with the price the market clears (his Q1, 2026-11-02: "Market price"), so the tariff follows from the fleet and the
 * fuel's price. This class is the model alone: no Game, no I/O; UtilitiesHandler builds the rows, strikes the book, and bills.
 *
 * Power in kW (a half-block's average), energy in kWh, money in the city's, per kWh; a fixture with no calendar passes season 1.
 */
public final class PowerDispatch {

    private PowerDispatch() { }

    /* ===== FIGURES ===== */

    /** Hours in the game's month [D: a row's production1 is its month's kWh over 720, the average kW; HY1's arithmetic]. */
    public static final int MONTH_HOURS = 720;

    /** The three load blocks, base to peak [D: spec 2.2]. */
    public static final int BASE = 0, SHOULDER = 1, PEAK = 2;

    /** Three blocks, each in two halves (the first windy, the second calm): six half-blocks, half-block s being block s / 2 [D: spec 2.2]. */
    public static final int BLOCKS = 3, HALVES = 6;

    /** The blocks' share of a month's hours, base to peak [E: the 10/40/50 cut, spec 2.2; the prototype, line 28]. */
    public static final double[] BLOCK_HOURS = {0.50, 0.40, 0.10};

    /** The blocks' load over the month's mean [D: EIA-930 2025, the lower 48, each month's own hours cut 10/40/50 and averaged: 1.20, 1.07, 0.90; the base set to 0.904 so the energy sums to the month's: 0.5 x 0.904 + 0.4 x 1.07 + 0.1 x 1.20 = 1.000]. */
    public static final double[] BLOCK_LOAD = {0.904, 1.07, 1.20};

    /** Each calendar month's mean over the year's [D: EIA-930 2025, the lower 48, the prototype, line 36], January first. */
    public static final double[] SEASON = {1.076, 0.998, 0.879, 0.880, 0.919, 1.088, 1.193, 1.119, 1.015, 0.911, 0.907, 1.012};

    /** Wind's output over its mean in each block, base to peak [M: EIA-930 2025, the lower 48; the prototype, line 95]. */
    public static final double[] WIND_BLOCK = {1.081, 0.948, 0.806};

    /** How far wind swings inside a block, over the block's average: the windy half at (1 + swing), the calm half at (1 - swing) [M: EIA-930 2025, the coefficient of variation of hourly output inside each load block, median of 11 regions: 0.64 / 0.61 / 0.59, base to peak]. */
    public static final double[] WIND_SWING = {0.64, 0.61, 0.59};

    /** The share of the grid lost between the plant and the meter [S: EIA FAQ 105, about 5%; spec 2.7]. */
    public static final double GRID_LOSSES = 0.05;

    /** The most a short half-block clears at, $3,000 a MWh [E: the value of lost load, spec 6], in thousands of founding dollars a kWh (the game's money is thousands: today's tariff .01 x FACTOR is D$10). */
    public static final double VOLL_A_KWH = 3.0e-3 * PriceLevel.FACTOR;

    /** The long wires' charge a kWh delivered, 1.885 cents [S: AEO2026 table 8, 2025; spec 2.6-2.7], in thousands of founding dollars. */
    public static final double TRANSMISSION_A_KWH = 0.01885e-3 * PriceLevel.FACTOR;

    /** The street wires' charge a kWh delivered, 4.053 cents [S: AEO2026 table 8, 2025; spec 2.6-2.7], in thousands of founding dollars. */
    public static final double DISTRIBUTION_A_KWH = 0.04053e-3 * PriceLevel.FACTOR;

    /** Megajoules in a Btu, so a heat rate in Btu a kWh is MJ a kWh [D: 1,055.056 J a Btu]. */
    public static final double MJ_PER_BTU = 0.001055056;

    /** Megajoules in a tonne of thermal coal [E: the Pink Sheet's Newcastle grade, 6,000 kcal/kg = 25.12 GJ a tonne, spec 2.5]. */
    public static final double COAL_MJ_PER_TONNE = 25_120;

    /** The draw a firm kW must cover in the peak block, losses in [D: BLOCK_LOAD at the peak over (1 - GRID_LOSSES)]: what the build advice plans power against. */
    public static final double PLAN_PEAK = BLOCK_LOAD[PEAK] / (1 - GRID_LOSSES);

    /** What a wind farm's average delivers in its calmest half-block [D: the least of WIND_BLOCK x (1 - WIND_SWING) over the blocks: 0.806 x 0.41 = 0.330]. */
    public static final double WIND_FIRM = windFirm();

    private static double windFirm() {
        double least = Double.POSITIVE_INFINITY;
        for (int b = 0; b < BLOCKS; b++) least = Math.min(least, WIND_BLOCK[b] * (1 - WIND_SWING[b]));
        return least;
    }

    /** MJ of fuel energy in a unit of a good (HHV), NaN for a good the model does not yet burn (PW2 adds the rest). */
    public static double heatContentMJ(Good g) {
        return g == Good.THERMAL_COAL ? COAL_MJ_PER_TONNE : Double.NaN;
    }

    /* ===== THE ROWS ===== */

    /** How a row is dispatched: the works' surplus first, wind at its weather, hydro levelling the load, the rest by marginal cost. */
    public enum Kind { MUST_RUN, WIND, HYDRO, FUELLED }

    /** One row of the month's fleet: what it offers and, after dispatch(), what it ran. */
    public static final class Row {
        public final String name;
        public final Kind kind;
        /** MUST_RUN and FUELLED: kW offered in every half-block; WIND: its average kW; HYDRO: its cap, kW. */
        public final double kw;
        /** HYDRO: its month of water, kWh; 0 for the rest. */
        public final double waterKwh;
        /** FUELLED: city money a kWh it costs to run (fuel + variable upkeep); 0 for the rest. */
        public final double marginal;
        /** FUELLED with a fuel; null for the rest. */
        public final Good fuel;
        /** Units of fuel it burns a kWh; 0 without. */
        public final double fuelPerKwh;
        /** Its variable upkeep, thousands of the WORLD's money a kWh (paid abroad later); 0 without. */
        public final double upkeepPerKwh;
        /** kW it ran in each half-block (set by dispatch()). */
        public final double[] gen = new double[HALVES];
        /** The month's kWh, fuel units, and kWh x its half-blocks' prices (set by dispatch()). */
        public double kwh, fuelUnits, earned;

        private Row(String name, Kind kind, double kw, double waterKwh, double marginal, Good fuel, double fuelPerKwh, double upkeepPerKwh) {
            this.name = name; this.kind = kind; this.kw = nonNegative(kw); this.waterKwh = nonNegative(waterKwh);
            this.marginal = nonNegative(marginal); this.fuel = fuel; this.fuelPerKwh = nonNegative(fuelPerKwh);
            this.upkeepPerKwh = nonNegative(upkeepPerKwh);
        }

        /** The works' surplus: it takes the load first, whatever the price. */
        public static Row mustRun(String name, double kw) { return new Row(name, Kind.MUST_RUN, kw, 0, 0, null, 0, 0); }

        /** Wind at its average kW; the weather (windAt) shapes it. */
        public static Row wind(String name, double averageKw) { return new Row(name, Kind.WIND, averageKw, 0, 0, null, 0, 0); }

        /** Hydro under a cap with a month of water to spend where the load is highest. */
        public static Row hydro(String name, double capKw, double waterKwh, double upkeepPerKwh) {
            return new Row(name, Kind.HYDRO, capKw, waterKwh, 0, null, 0, upkeepPerKwh);
        }

        /** A fuelled row, run by marginal cost, each half-block up to its kW. */
        public static Row fuelled(String name, double kw, double marginal, Good fuel, double fuelPerKwh, double upkeepPerKwh) {
            return new Row(name, Kind.FUELLED, kw, 0, marginal, fuel, fuelPerKwh, upkeepPerKwh);
        }
    }

    /** What a month's dispatch came to. */
    public static final class Month {
        /** Per half-block: the load asked (kW, losses in), what was left unserved (kW), and the price it cleared at (city money a kWh). */
        public final double[] load = new double[HALVES], unserved = new double[HALVES], price = new double[HALVES];
        public double askedKwh, servedKwh, curtailedKwh;
        /** servedKwh / askedKwh, 1 when nothing is asked. */
        public double ratio = 1;
        /** City money a kWh DELIVERED: the sum over half-blocks of served kWh x price, over servedKwh, over (1 - GRID_LOSSES); 0 when nothing is served. */
        public double energyPrice;
    }

    /* ===== THE CALENDAR AND THE WEATHER ===== */

    /** Hours in half-block s: its block's share of the month, halved. */
    public static double halfHours(int s) { return BLOCK_HOURS[s / 2] / 2 * MONTH_HOURS; }

    /** The calendar month's load over the year's mean, for 1..12; 1 for anything else (a fixture with no calendar). */
    public static double season(int monthOfYear) {
        return monthOfYear >= 1 && monthOfYear <= 12 ? SEASON[monthOfYear - 1] : 1;
    }

    /** Wind's output over its average in half-block s: its block's share, windy in the first half and calm in the second. */
    public static double windAt(int s) {
        return WIND_BLOCK[s / 2] * (1 + (s % 2 == 0 ? 1 : -1) * WIND_SWING[s / 2]);
    }

    /* ===== THE MONTH ===== */

    private static double nonNegative(double x) { return Double.isFinite(x) && x > 0 ? x : 0; }

    /** A half-block left with more than this much unserved is short, kW. */
    private static final double SHORT_KW = 1e-9;

    /** Halvings of the water's level, as the prototype loops them. */
    private static final int LEVEL_STEPS = 40;

    /** The level's resolution, kW. */
    private static final double LEVEL_EPS = 1e-6;

    /**
     * One month's merit order: drawKw the grid's draw (kW, before losses), season the calendar month's load over the year's mean, voll
     * what a short half-block clears at. Each half-block, in this order: the must-run rows take the load, wind runs at its weather
     * (what the half-block cannot take is curtailed), hydro levels the load with its month of water, then the fuelled rows by
     * marginal cost, cheapest first, each up to its kW. A half-block clears at the marginal cost of the last fuelled row it ran
     * (0 if none), or at voll when it is left short. Sets every row's gen, kwh, fuelUnits and earned.
     */
    public static Month dispatch(List<Row> rows, double drawKw, double season, double voll) {
        Month m = new Month();
        double draw = nonNegative(drawKw) * nonNegative(season) / (1 - GRID_LOSSES), cap = nonNegative(voll);
        double[] residual = new double[HALVES];
        for (int s = 0; s < HALVES; s++) m.load[s] = residual[s] = draw * BLOCK_LOAD[s / 2];
        for (Row r : rows) { Arrays.fill(r.gen, 0); r.kwh = r.fuelUnits = r.earned = 0; }

        // 1. The works' surplus, then 2. the wind: what a half-block cannot take is curtailed.
        for (int pass = 0; pass < 2; pass++) {
            Kind steady = pass == 0 ? Kind.MUST_RUN : Kind.WIND;
            for (Row r : rows) {
                if (r.kind != steady) continue;
                for (int s = 0; s < HALVES; s++) {
                    double out = steady == Kind.WIND ? r.kw * windAt(s) : r.kw, take = Math.min(out, residual[s]);
                    r.gen[s] = take; residual[s] -= take; m.curtailedKwh += (out - take) * halfHours(s);
                }
            }
        }

        // 3. Hydro levels the load: its water runs wherever the residual stands above a level found by halving.
        for (Row r : rows) {
            if (r.kind != Kind.HYDRO || r.kw <= 0 || r.waterKwh <= 0) continue;
            double lo = 0, hi = 0;
            for (double x : residual) hi = Math.max(hi, x);
            if (use(residual, r.kw, 0) <= r.waterKwh) hi = 0;
            for (int it = 0; it < LEVEL_STEPS && hi - lo > LEVEL_EPS; it++) {
                double mid = (lo + hi) / 2;
                if (use(residual, r.kw, mid) > r.waterKwh) lo = mid; else hi = mid;
            }
            for (int s = 0; s < HALVES; s++) {
                double out = Math.min(r.kw, Math.max(0, residual[s] - hi));
                r.gen[s] = out; residual[s] -= out;
            }
        }

        // 4. The fuelled rows, cheapest marginal cost first (a stable sort of a copy: ties keep list order).
        List<Row> order = new ArrayList<>();
        for (Row r : rows) if (r.kind == Kind.FUELLED) order.add(r);
        order.sort(Comparator.comparingDouble(r -> r.marginal));
        for (int s = 0; s < HALVES; s++) {
            double price = 0;
            for (Row r : order) {
                if (residual[s] <= SHORT_KW) break;
                double out = Math.min(r.kw, residual[s]);
                if (out <= 0) continue;
                r.gen[s] = out; residual[s] -= out; price = r.marginal;
            }
            m.unserved[s] = residual[s] > SHORT_KW ? residual[s] : 0;
            m.price[s] = m.unserved[s] > 0 ? cap : price;
        }

        double paid = 0;
        for (Row r : rows) {
            for (int s = 0; s < HALVES; s++) {
                double kwh = r.gen[s] * halfHours(s);
                r.kwh += kwh; r.earned += kwh * m.price[s];
            }
            r.fuelUnits = r.kwh * r.fuelPerKwh;
        }
        for (int s = 0; s < HALVES; s++) {
            double hours = halfHours(s), served = (m.load[s] - m.unserved[s]) * hours;
            m.askedKwh += m.load[s] * hours; m.servedKwh += served; paid += served * m.price[s];
        }
        m.ratio = m.askedKwh > 0 ? m.servedKwh / m.askedKwh : 1;
        m.energyPrice = m.servedKwh > 0 ? paid / m.servedKwh / (1 - GRID_LOSSES) : 0;
        return m;
    }

    /** The water kWh a hydro row of this cap runs above this level of the residual, half-block by half-block. */
    private static double use(double[] residual, double cap, double level) {
        double e = 0;
        for (int s = 0; s < HALVES; s++) e += Math.min(cap, Math.max(0, residual[s] - level)) * halfHours(s);
        return e;
    }
}
