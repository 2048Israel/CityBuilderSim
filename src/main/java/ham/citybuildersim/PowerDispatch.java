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

    /** The draw a firm kW must cover in the peak block of the year's highest month, losses in [D: PLAN_PEAK times SEASON's highest, July's 1.193 on the north's calendar, so 1.263 x 1.193 = 1.507 of the mean draw; the south's calendar is six months on and season(peakMonth()) is SEASON's maximum either way]: what the month's dispatch serves at its worst, the draw below which some month leaves its peak block short (PW3 R). */
    public static final double PLAN_PEAK_SEASON = PLAN_PEAK * season(peakMonth());

    /** What a wind farm's average delivers in its calmest half-block [D: the least of WIND_BLOCK x (1 - WIND_SWING) over the blocks: 0.806 x 0.41 = 0.330]. */
    public static final double WIND_FIRM = windFirm();

    private static double windFirm() {
        return firm(WIND_BLOCK, WIND_SWING);
    }

    /** A variable row's least share of its average over the six half-blocks: its block's share x (1 - its swing), the calmest or darkest. */
    private static double firm(double[] block, double[] swing) {
        double least = Double.POSITIVE_INFINITY;
        for (int b = 0; b < BLOCKS; b++) least = Math.min(least, block[b] * (1 - swing[b]));
        return least;
    }

    /** The sun's output over its mean in each block, base to peak [M: EIA-930 2025, the lower 48, the study's vblocks.py; spec 2.4; the prototype, line 96]. */
    public static final double[] SOLAR_BLOCK = {0.680, 1.136, 2.054};

    /** How far the sun swings inside a block, over the block's average, the first half sunny and the second dark (the wind's halves) [M: EIA-930 2025, the coefficient of variation of hourly solar output inside each load block, median of 12 regions: 1.32 / 1.06 / 0.66 base to peak, a swing past 1 held at 1; the prototype, line 102]. */
    public static final double[] SOLAR_SWING = {1.0, 1.0, 0.66};

    /** What a solar farm's average delivers in its darkest half-block [D: the least of SOLAR_BLOCK x (1 - SOLAR_SWING) over the blocks: the base's and the shoulder's dark halves, 0]; the reserve test credits the sun by the peak hours instead (PW3, solarCredit()). */
    public static final double SOLAR_FIRM = firm(SOLAR_BLOCK, SOLAR_SWING);

    /* ===== THE YEAR, THE PLACE AND THE CREDIT (PW3, 2026-11-23) =====
       A month's wind, sun and river are the US fleet's in that calendar month (EIA, 2025), each month's index its
       capacity factor over the twelve's mean; south of the equator a city reads the month six on (its own season,
       the load's with them). The sun also has a place: the farm's yield at the city's latitude over the US fleet's,
       and its own months there (PVGIS), on a fixed rack until the tracker (research E25) is known. A variable row's
       credit is its output in the year's highest-load hours, falling with the variable rows' share of the energy. */

    /** Wind's capacity factor in each calendar month, January first, percent [S: EIA, Electric Power Monthly, table 6.07.B, 2025; spec 2.4]: a month's index is its figure over the twelve's mean. */
    public static final double[] WIND_MONTH_CF = {37.4, 37.2, 43.1, 40.7, 32.1, 32.2, 27.5, 23.8, 23.1, 33.8, 35.4, 39.2};

    /** Solar photovoltaic's capacity factor in each calendar month, January first, percent [S: EIA, Electric Power Monthly, table 6.07.B, 2025; spec 2.4]: the US fleet's months, a generated world's. */
    public static final double[] SOLAR_MONTH_CF = {16.2, 18.7, 23.6, 27.5, 29.3, 31.9, 31.9, 29.7, 26.7, 22.4, 16.9, 13.1};

    /** Conventional hydro's capacity factor in each calendar month, January first, percent [S: EIA, Electric Power Monthly, table 6.07.B, 2025; spec 2.4]: a station's month of water is its average times the month's index. */
    public static final double[] HYDRO_MONTH_CF = {34.7, 35.1, 38.2, 40.8, 42.8, 39.8, 34.2, 32.3, 26.1, 24.9, 28.5, 36.1};

    /** The year's peak hour over its mean [D: EIA-930 2025, the lower 48, 760,649 / 491,034 MW; spec 2.2]. */
    public static final double PEAK_HOUR = 760_649.0 / 491_034.0;

    /** The draw the firm supply must cover, losses in [D: PEAK_HOUR over (1 - GRID_LOSSES)]: what the reserve test plans power against (PLAN_PEAK, the peak block's, stays the substations'). */
    public static final double PLAN_PEAK_HOUR = PEAK_HOUR / (1 - GRID_LOSSES);

    /** The firm supply's margin over the peak hour the reserve test asks [E: NERC's reference margin level, cited as evidence; spec 2.8, 6]: power's NEEDS YOU line is 1 / (1 + RESERVE). */
    public static final double RESERVE = 0.15;

    /** Wind's output in the year's 100 highest-load hours over its average [M: the study's dispatch of EIA-930 2025, 0.247 of nameplate over 33.8%; spec 2.4]: its credit alone. */
    public static final double WIND_PEAK_CREDIT = 0.730;

    /** Solar's output in the year's 100 highest-load hours over its average [M: the study's dispatch of EIA-930 2025, 0.629 of nameplate over 24.0%; spec 2.4]: its credit alone, at the US fleet's place. */
    public static final double SOLAR_PEAK_CREDIT = 2.621;

    /** What a variable row's credit falls to, over its credit alone, once the variable rows make CREDIT_HALF of the city's energy [E: the study's straight line, spec 2.4; NREL's and ERCOT's direction as evidence only]. */
    public static final double CREDIT_FLOOR = 0.1;

    /** The variable rows' share of the city's energy at which their credit has fallen to CREDIT_FLOOR [E: spec 2.4]. */
    public static final double CREDIT_HALF = 0.5;

    /** The latitude bands' centres the sun's tables are read at, degrees from the equator either way [D: PVGIS's places averaged in 10-degree bands, docs/studies/electricity/pvgis.py]. */
    public static final double SOLAR_BAND_DEGREES = 10;

    /** A fixed rack's yield a kWp in each band, 0 to 70 degrees, over the tracked US fleet's [S: PVGIS 5.3 (c) European Communities, CC BY 4.0, ERA5, 14% losses; the US reference seven places at 32.8-35.5 N, 2,073.6 kWh a kWp tracked; out/pvgis.txt]. */
    public static final double[] SOLAR_PLACE_FIXED = new double[] {0.6537, 0.7379, 0.7714, 0.7921, 0.7396, 0.6128, 0.5027, 0.3959};

    /** A single-axis tracker's yield a kWp in each band, 0 to 70 degrees, over the tracked US fleet's [S: PVGIS 5.3 (c) European Communities, CC BY 4.0; out/pvgis.txt]. */
    public static final double[] SOLAR_PLACE_TRACKED = new double[] {0.8359, 0.9611, 0.9930, 1.0011, 0.8909, 0.7125, 0.5910, 0.4825};

    /** A fixed rack's yield at the US fleet's places over a tracker's there [S: PVGIS 5.3, out/pvgis.txt]: a generated world's farm before the tracker. */
    public static final double SOLAR_US_FIXED = 0.7940;

    /** A fixed rack's months in each band, on the north's calendar, each over the twelve's mean [S: PVGIS 5.3, out/pvgis.txt; the south's places turned six months]. */
    public static final double[][] SOLAR_SHAPE_FIXED = new double[][] {
            {1.030, 1.037, 1.080, 1.035, 0.957, 0.936, 0.992, 0.982, 1.049, 0.994, 0.949, 0.959},
            {1.087, 1.072, 1.130, 1.072, 0.979, 0.900, 0.895, 0.884, 0.956, 0.995, 1.007, 1.023},
            {1.009, 1.037, 1.104, 1.090, 1.006, 0.929, 0.926, 0.921, 0.978, 1.017, 1.006, 0.977},
            {0.895, 0.951, 1.066, 1.104, 1.089, 1.063, 1.060, 1.015, 1.031, 0.989, 0.896, 0.843},
            {0.837, 0.908, 1.095, 1.134, 1.141, 1.109, 1.133, 1.103, 1.054, 0.949, 0.790, 0.747},
            {0.615, 0.840, 1.150, 1.297, 1.326, 1.261, 1.283, 1.206, 1.082, 0.852, 0.596, 0.493},
            {0.294, 0.670, 1.302, 1.571, 1.670, 1.528, 1.469, 1.307, 1.029, 0.697, 0.305, 0.156},
            {0.024, 0.439, 1.427, 1.916, 2.037, 1.813, 1.690, 1.273, 0.882, 0.442, 0.056, 0.000}};

    /** A tracker's months in each band, on the north's calendar, each over the twelve's mean [S: PVGIS 5.3, out/pvgis.txt]. */
    public static final double[][] SOLAR_SHAPE_TRACKED = new double[][] {
            {1.035, 1.044, 1.090, 1.039, 0.957, 0.936, 1.006, 0.996, 1.049, 0.971, 0.923, 0.954},
            {1.028, 1.048, 1.153, 1.132, 1.048, 0.952, 0.933, 0.898, 0.940, 0.961, 0.954, 0.953},
            {0.873, 0.953, 1.110, 1.180, 1.143, 1.058, 1.030, 0.984, 0.991, 0.961, 0.888, 0.830},
            {0.691, 0.805, 1.032, 1.195, 1.290, 1.313, 1.279, 1.130, 1.043, 0.881, 0.711, 0.629},
            {0.592, 0.739, 1.044, 1.232, 1.363, 1.393, 1.412, 1.267, 1.063, 0.817, 0.579, 0.500},
            {0.363, 0.618, 1.046, 1.401, 1.606, 1.605, 1.610, 1.379, 1.054, 0.678, 0.375, 0.266},
            {0.120, 0.400, 1.066, 1.627, 2.000, 1.946, 1.821, 1.435, 0.923, 0.468, 0.140, 0.054},
            {0.005, 0.192, 1.019, 1.840, 2.300, 2.258, 2.056, 1.344, 0.725, 0.244, 0.017, 0.000}};

    /** The calendar month on the north's calendar for a city at this latitude: six months on south of the equator [spec 2.2]; the north's at the equator and in a generated world (NaN); a month outside 1..12 as given. */
    public static int northern(int monthOfYear, double lat) {
        if (monthOfYear < 1 || monthOfYear > 12 || !(lat < 0)) return monthOfYear;
        return (monthOfYear + 5) % 12 + 1;
    }

    /** The calendar month's load over the year's mean for a city at this latitude: its own season. */
    public static double season(int monthOfYear, double lat) { return season(northern(monthOfYear, lat)); }

    /** A month's capacity factor over the twelve's mean, for 1..12 on the north's calendar; 1 for anything else. */
    private static double index(double[] cf, int northMonth) {
        if (northMonth < 1 || northMonth > 12) return 1;
        double sum = 0;
        for (double x : cf) sum += x;
        return cf[northMonth - 1] / (sum / cf.length);
    }

    /** Wind's output in the calendar month over its average, for a city at this latitude. */
    public static double windMonth(int monthOfYear, double lat) { return index(WIND_MONTH_CF, northern(monthOfYear, lat)); }

    /** A hydro station's water in the calendar month over its average, for a city at this latitude. */
    public static double hydroMonth(int monthOfYear, double lat) { return index(HYDRO_MONTH_CF, northern(monthOfYear, lat)); }

    /** A table read at |lat| between its bands' centres, held at its ends. */
    private static double band(double[] table, double lat) {
        double x = Math.min(table.length - 1, Math.abs(lat) / SOLAR_BAND_DEGREES);
        int i = (int) Math.floor(x);
        if (i >= table.length - 1) return table[table.length - 1];
        return table[i] + (x - i) * (table[i + 1] - table[i]);
    }

    /** A solar farm's year at this latitude over its production1 (the tracked US fleet's 24.0%): on a fixed rack or tracked; a generated world's (NaN) the US fleet's. */
    public static double solarYear(double lat, boolean tracked) {
        if (Double.isNaN(lat)) return tracked ? 1 : SOLAR_US_FIXED;
        return band(tracked ? SOLAR_PLACE_TRACKED : SOLAR_PLACE_FIXED, lat);
    }

    /** ...its month on the north's calendar over its own year there: the band's shape, or a generated world's the US fleet's (EIA). */
    private static double solarShape(int northMonth, double lat, boolean tracked) {
        if (northMonth < 1 || northMonth > 12) return 1;
        if (Double.isNaN(lat)) return index(SOLAR_MONTH_CF, northMonth);
        double[][] shape = tracked ? SOLAR_SHAPE_TRACKED : SOLAR_SHAPE_FIXED;
        double[] month = new double[shape.length];
        for (int b = 0; b < shape.length; b++) month[b] = shape[b][northMonth - 1];
        return band(month, lat);
    }

    /** A solar farm's output in the calendar month over its production1, for a city at this latitude: its place's year times its month there. */
    public static double solarMonth(int monthOfYear, double lat, boolean tracked) {
        return solarYear(lat, tracked) * solarShape(northern(monthOfYear, lat), lat, tracked);
    }

    /** The month of the year's highest load, on the north's calendar: SEASON's highest. */
    public static int peakMonth() {
        int best = 0;
        for (int i = 1; i < SEASON.length; i++) if (SEASON[i] > SEASON[best]) best = i;
        return best + 1;
    }

    /** What a variable row's credit alone keeps at this share of the city's energy from the variable rows: 1 at none, falling in a straight line to CREDIT_FLOOR at CREDIT_HALF, and held there. */
    public static double creditFall(double share) {
        double s = Double.isFinite(share) ? Math.max(0, share) : 0;
        return Math.max(CREDIT_FLOOR, 1 - s / CREDIT_HALF * (1 - CREDIT_FLOOR));
    }

    /** A wind farm's firm kW over its production1 at this share: its peak hours' output, falling with the share. */
    public static double windCredit(double share) { return WIND_PEAK_CREDIT * creditFall(share); }

    /** A solar farm's firm kW over its production1 at this latitude and share: its peak hours' output at the US fleet's place, scaled by its output in the peak month here over the US fleet's then, falling with the share. */
    public static double solarCredit(double lat, boolean tracked, double share) {
        int peak = peakMonth();
        double here = solarYear(lat, tracked) * solarShape(peak, lat, tracked);
        return SOLAR_PEAK_CREDIT * here / index(SOLAR_MONTH_CF, peak) * creditFall(share);
    }

    /** Megajoules in a litre of fuel oil [D: 40 CFR 98 subpart C table C-1, residual fuel oil No. 6, 0.150 MMBtu a gallon: 150,000 x 1,055.056 J over 3.785412 L = 41.81 MJ; spec 2.5]. */
    public static final double FUEL_OIL_MJ_PER_LITRE = 0.150e6 * MJ_PER_BTU / 3.785412;

    /** Megajoules in a litre of diesel [D: 40 CFR 98 subpart C table C-1, distillate fuel oil No. 2, 0.138 MMBtu a gallon: 138,000 x 1,055.056 J over 3.785412 L = 38.46 MJ; spec 2.5]. */
    public static final double DIESEL_MJ_PER_LITRE = 0.138e6 * MJ_PER_BTU / 3.785412;

    /** Megajoules in a tonne of wood pellets [E: 40 CFR 98 table C-1's wood and wood residuals, 17.48 MMBtu a short ton dry, 20.33 GJ a tonne, at 8% moisture (x 0.92) = 18.70 GJ; no government figure for pellets was found; spec 2.5 and 6]. */
    public static final double PELLETS_MJ_PER_TONNE = 17.48e6 * MJ_PER_BTU / 0.90718474 * 0.92;

    /** MJ of fuel energy in a unit of a good (HHV, the unit the good is sold in), NaN for a good no row burns: thermal coal (PW1), fuel oil, diesel and wood pellets (PW2). */
    public static double heatContentMJ(Good g) {
        if (g == null) return Double.NaN;
        return switch (g) {
            case THERMAL_COAL -> COAL_MJ_PER_TONNE;
            case FUEL_OIL -> FUEL_OIL_MJ_PER_LITRE;
            case DIESEL -> DIESEL_MJ_PER_LITRE;
            case WOOD_PELLETS -> PELLETS_MJ_PER_TONNE;
            default -> Double.NaN;
        };
    }

    /* ===== THE ROWS ===== */

    /** How a row is dispatched: the works' surplus first, wind and sun at their weather, hydro levelling the load, the rest by marginal cost. Saved by name (a record's kind): append only. */
    public enum Kind { MUST_RUN, WIND, HYDRO, FUELLED, SOLAR }

    /** One row of the month's fleet: what it offers and, after dispatch(), what it ran. */
    public static final class Row {
        public final String name;
        public final Kind kind;
        /** MUST_RUN and FUELLED: kW offered in every half-block; WIND and SOLAR: its average kW; HYDRO: its cap, kW. */
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

        /** The sun at its average kW (PW2); the day and the weather (solarAt) shape it. */
        public static Row solar(String name, double averageKw) { return new Row(name, Kind.SOLAR, averageKw, 0, 0, null, 0, 0); }

        /** Hydro under a cap with a month of water to spend where the load is highest. */
        public static Row hydro(String name, double capKw, double waterKwh, double upkeepPerKwh) {
            return new Row(name, Kind.HYDRO, capKw, waterKwh, 0, null, 0, upkeepPerKwh);
        }

        /** A fuelled row, run by marginal cost, each half-block up to its kW. */
        public static Row fuelled(String name, double kw, double marginal, Good fuel, double fuelPerKwh, double upkeepPerKwh) {
            return new Row(name, Kind.FUELLED, kw, 0, marginal, fuel, fuelPerKwh, upkeepPerKwh);
        }

        /** The same row offering another kW and month of water, nothing run yet: a dispatch run again on it leaves this one as it ran (PW1-L). */
        public Row offering(double kw, double waterKwh) {
            return new Row(name, kind, kw, waterKwh, marginal, fuel, fuelPerKwh, upkeepPerKwh);
        }
    }

    /** What a month's dispatch came to. */
    public static final class Month {
        /** The actual dispatch order, including idle rows; observation only, never read by the dispatch. */
        private List<Integer> meritOrder = List.of();

        /** Immutable indices into the supplied rows, in the actual dispatch order, including idle rows. */
        public List<Integer> getMeritOrder() { return meritOrder; }

        /** Detached saved flow, so load need not dispatch an already completed month again. */
        public Month copy() {
            Month m = new Month();
            System.arraycopy(load, 0, m.load, 0, HALVES);
            System.arraycopy(unserved, 0, m.unserved, 0, HALVES);
            System.arraycopy(price, 0, m.price, 0, HALVES);
            m.meritOrder = List.copyOf(meritOrder);
            m.askedKwh = askedKwh; m.servedKwh = servedKwh; m.curtailedKwh = curtailedKwh;
            m.ratio = ratio; m.energyPrice = energyPrice;
            return m;
        }
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

    /** The sun's output over its average in half-block s (PW2): its block's share, sunny in the first half and dark in the second. */
    public static double solarAt(int s) {
        return SOLAR_BLOCK[s / 2] * (1 + (s % 2 == 0 ? 1 : -1) * SOLAR_SWING[s / 2]);
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
     * what a short half-block clears at. Each half-block, in this order: the must-run rows take the load, wind and then the sun run at
     * their weather (what the half-block cannot take is curtailed), hydro levels the load with its month of water, then the fuelled rows by
     * marginal cost, cheapest first, each up to its kW. A half-block clears at the marginal cost of the last fuelled row it ran
     * (0 if none), or at voll when it is left short. Sets every row's gen, kwh, fuelUnits and earned.
     */
    public static Month dispatch(List<Row> rows, double drawKw, double season, double voll) {
        return dispatch(rows, drawKw, season, voll, Double.POSITIVE_INFINITY);
    }

    /** The same dispatch, with the city's wires limiting generation before fuel or water is spent. */
    public static Month dispatch(List<Row> rows, double drawKw, double season, double voll, double carryKw) {
        Month m = new Month();
        List<Integer> observedOrder = new ArrayList<>();
        double draw = nonNegative(drawKw) * nonNegative(season) / (1 - GRID_LOSSES), cap = nonNegative(voll);
        double[] residual = new double[HALVES];
        for (int s = 0; s < HALVES; s++) {
            m.load[s] = draw * BLOCK_LOAD[s / 2];
            residual[s] = Math.min(m.load[s], Math.max(0, carryKw));
        }
        for (Row r : rows) { Arrays.fill(r.gen, 0); r.kwh = r.fuelUnits = r.earned = 0; }

        // 1. The works' surplus, then 2. the wind, then the sun (PW2): what a half-block cannot take is curtailed.
        for (int pass = 0; pass < 3; pass++) {
            Kind steady = pass == 0 ? Kind.MUST_RUN : pass == 1 ? Kind.WIND : Kind.SOLAR;
            for (Row r : rows) {
                if (r.kind != steady) continue;
                observedOrder.add(rows.indexOf(r));
                for (int s = 0; s < HALVES; s++) {
                    double out = steady == Kind.WIND ? r.kw * windAt(s) : steady == Kind.SOLAR ? r.kw * solarAt(s) : r.kw;
                    double take = Math.min(out, residual[s]);
                    r.gen[s] = take; residual[s] -= take; m.curtailedKwh += (out - take) * halfHours(s);
                }
            }
        }

        // 3. Hydro levels the load: its water runs wherever the residual stands above a level found by halving.
        for (Row r : rows) {
            if (r.kind == Kind.HYDRO) observedOrder.add(rows.indexOf(r));
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
        for (Row r : order) observedOrder.add(rows.indexOf(r));
        m.meritOrder = List.copyOf(observedOrder);
        for (int s = 0; s < HALVES; s++) {
            double price = 0;
            for (Row r : order) {
                if (residual[s] <= SHORT_KW) break;
                double out = Math.min(r.kw, residual[s]);
                if (out <= 0) continue;
                r.gen[s] = out; residual[s] -= out; price = r.marginal;
            }
            double shortfall = residual[s] + Math.max(0, m.load[s] - Math.max(0, carryKw));
            m.unserved[s] = shortfall > SHORT_KW ? shortfall : 0;
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
