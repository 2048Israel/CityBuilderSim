import java.util.*;

/**
 * The electricity study's prototype (docs/studies/spec-electricity.md): a month's merit order
 * over a handful of plant rows and three demand blocks, the planner's two rules (the reserve
 * test, then the spread), the tariff at cost, and the timing. Standalone; nothing in the game
 * reads it.
 *
 *     java docs/studies/electricity/Dispatch.java          the scenarios, then the timing
 *     java docs/studies/electricity/Dispatch.java time     the timing alone
 *
 * WHY a month and three blocks: the city's month is the game's clock, and a load duration curve
 * cut in three (base, shoulder, peak) keeps what decides which plant runs - the hours each slice
 * of capacity is needed - at about a hundred multiplications a month. No hour is simulated.
 *
 * Units: power in MW (a block's average), energy in MWh, money in US$ at about 2025's level
 * (the game strikes it at the founding's, times PriceLevel.FACTOR). Each figure carries its kind
 * as the study does: [S n] a source in the study's section 7, [D] derived (the sum beside it),
 * [M] measured, [E] a named estimate (the study's section 6 lists them).
 */
public class Dispatch {

    /* ===== FIGURES ===== */

    /** Hours in the game's month: production1 is kWh over 720 (HY1's arithmetic), 730 h. */
    static final double HOURS = 730;

    /** The blocks: their share of a month's hours [E: the 10/40/50 cut], and their load over the month's mean
     *  [D: EIA-930 2025, the lower 48, each month's own hours cut 10/40/50 and averaged: 1.20, 1.07, 0.90; the base
     *  set to 0.904 so the energy sums to the month's: 0.1 x 1.20 + 0.4 x 1.07 + 0.5 x 0.904 = 1.000]. */
    static final double[] BLOCK_HOURS = {0.50, 0.40, 0.10};
    static final double[] BLOCK_LOAD  = {0.904, 1.07, 1.20};
    static final String[] BLOCK = {"base", "shoulder", "peak"};

    /** Each calendar month's mean over the year's [D: EIA-930 2025, the lower 48], January first. */
    static final double[] SEASON = {1.076, 0.998, 0.879, 0.880, 0.919, 1.088, 1.193, 1.119, 1.015, 0.911, 0.907, 1.012};

    /** The year's peak hour over its mean [D: EIA-930 2025, 760,649 / 491,034 MW]; the margin over it [E: NERC's
     *  reference level, cited as evidence]. */
    static final double PEAK_OVER_MEAN = 1.549, RESERVE = 0.15;

    /** $/MWh a short block clears at [E]: only the spread rule reads it, never a bill. */
    static final double VOLL = 3000;

    /** The real rate a plant's capital is spread at [E: the advice's real rate, 4% standing in]. */
    static final double R = 0.04;

    /** The grid: what is lost [S EIA FAQ 105: about 5%], and the wires' cost a MWh delivered [D: AEO2026 table 8,
     *  transmission 1.8851 + distribution 4.0525 = 5.94 cents a kWh, 2025]. */
    static final double LOSSES = 0.05, GRID = 59.4;

    /**
     * The fuels, $ a MJ (HHV): the game's band for the good at today's level, its middle [E: the city's price lies in
     * the band], over its heat content.
     */
    enum Fuel {
        COAL((75 + 50) / 2.0 / 25_120),               // THERMAL_COAL .075/.050 a t; 6,000 kcal/kg = 25.12 GJ/t [D, Pink Sheet]
        FUEL_OIL((0.4997 + 0.4256) / 2 / 41.81),      // FUEL_OIL a L; 0.150 MMBtu/gal = 41.81 MJ/L [D, 40 CFR 98 C-1]
        DIESEL((0.6883 + 0.5864) / 2 / 38.46),        // DIESEL a L; 0.138 MMBtu/gal = 38.46 MJ/L [D, 40 CFR 98 C-1]
        URANIUM(6.64 / 11_020.0),                     // the whole fuel, $6.64/MWh [S EIA EPA 8.4] at 11.02 MJ/kWh
        PELLETS((220 + 150) / 2.0 / 18_700),          // WOOD_PELLETS a t; 18.70 GJ/t at 8% moisture [D, 40 CFR 98 C-1; E]
        WATER(0), WIND(0), SUN(0);
        double perMJ;
        Fuel(double perMJ) { this.perMJ = perMJ; }
    }

    /** Btu/kWh to MJ/kWh. */
    static double mj(double btu) { return btu * 1.055056 / 1000; }

    /**
     * A row as a city has it: MW standing and its figures. heat is MJ of fuel a kWh; cf[] a variable
     * row's output over nameplate in each block (null for one dispatched); credit its share of
     * nameplate the reserve test counts before its share of the city's energy lowers it (credit());
     * budget a hydro row's month of water as a capacity factor (NaN for none).
     */
    static final class Plant {
        final String name; final Fuel fuel; final double heat, vom, fom, capex, life, avail, credit, budget, unit;
        final double[] cf;
        double[] swing;
        double mw;
        final double[] gen = new double[SUB];
        double fuelMJ, revenue;
        Plant(String name, Fuel fuel, double heat, double vom, double fom, double capex, double life,
              double avail, double credit, double[] cf, double budget, double unit) {
            this.name = name; this.fuel = fuel; this.heat = heat; this.vom = vom; this.fom = fom; this.capex = capex;
            this.life = life; this.avail = avail; this.credit = credit; this.cf = cf; this.budget = budget; this.unit = unit;
        }
        double marginal() { return heat * 1000 * fuel.perMJ + vom; }
        Plant copy(double mw) {
            Plant p = new Plant(name, fuel, heat, vom, fom, capex, life, avail, credit, cf, budget, unit);
            p.swing = swing; p.mw = mw; return p;
        }
    }

    /** Wind's output over its mean in each block, base to peak [M: EIA-930 2025, the lower 48]; its capacity factor [S EPM 6.07.B, 2025]. */
    static final double[] WIND_BLOCK = {1.081, 0.948, 0.806}, SUN_BLOCK = {0.680, 1.136, 2.054};

    /** How far a variable row's output swings, hour to hour, inside a block, over its block's average: each block
     *  is split into two halves of its hours, one at (1 + swing) and one at (1 - swing) [M: EIA-930 2025, the
     *  coefficient of variation of hourly output inside each load block, median of 11 regions with wind, 12 with
     *  solar: wind 0.59 / 0.61 / 0.64, solar 0.66 / 1.06 / 1.32 (peak / shoulder / base), a swing past 1 held at 1]. */
    static final double[] WIND_SWING = {0.64, 0.61, 0.59}, SUN_SWING = {1.0, 1.0, 0.66};

    /** The six half-blocks: half-block s is block s / 2, its first half windy and sunny, its second calm and dark. */
    static final int SUB = 6;
    static int blockOf(int s) { return s / 2; }
    static double subHours(int s) { return BLOCK_HOURS[s / 2] / 2; }
    static final double WIND_CF = 0.338, SUN_CF = 0.240;

    static double[] times(double[] a, double k) { double[] o = a.clone(); for (int i = 0; i < o.length; i++) o[i] *= k; return o; }

    /** Today's rows (the study's table in 2.3); the sources are there. */
    static List<Plant> today() {
        List<Plant> l = new ArrayList<>();
        //               name                    fuel           MJ/kWh       VOM    FOM     capex  life avail credit cf                               water unit MW
        l.add(new Plant("Coal Power Plant",      Fuel.COAL,     mj(8_490),   9.37,  89.0,  3_271, 40, 0.85, 0.85, null, Double.NaN, 325));
        l.add(new Plant("Oil-Fired Plant",       Fuel.FUEL_OIL, mj(11_227),  9.68,  83.0,  3_054, 40, 0.85, 0.85, null, Double.NaN, 100));
        l.add(new Plant("Diesel Station",        Fuel.DIESEL,   mj(10_847),  5.69,  35.16, 1_810, 30, 0.90, 0.90, null, Double.NaN, 21));
        l.add(new Plant("Oil-Fired Turbine",     Fuel.DIESEL,   mj(9_447),   5.97,  9.99,  2_275, 40, 0.90, 0.90, null, Double.NaN, 100));
        l.add(new Plant("Nuclear Plant",         Fuel.URANIUM,  mj(10_445),  2.62, 163.40, 7_862, 60, 0.91, 0.91, null, Double.NaN, 1_100));
        l.add(new Plant("Biomass Plant",         Fuel.PELLETS,  mj(13_300),  5.93, 154.26, 4_843, 40, 0.85, 0.85, null, Double.NaN, 50));
        l.add(new Plant("Hydro Station",         Fuel.WATER,    0,           1.71,  51.30, 3_000, 50, 0.95, 0.45, null, 0.45, 10));
        l.add(new Plant("Wind Farm",             Fuel.WIND,     0,           0,     34.57, 1_712, 25, 1.00, WIND_CF * 0.730, times(WIND_BLOCK, WIND_CF), Double.NaN, 20));
        l.add(new Plant("Solar Farm",            Fuel.SUN,      0,           0,     23.60, 1_484, 35, 1.00, SUN_CF * 2.621, times(SUN_BLOCK, SUN_CF), Double.NaN, 50));
        l.get(7).swing = WIND_SWING; l.get(8).swing = SUN_SWING;
        return l;
    }

    /** A variable row's output over nameplate in half-block s. */
    static double cfAt(Plant p, int s) { return p.cf[s / 2] * (1 + (s % 2 == 0 ? 1 : -1) * p.swing[s / 2]); }

    /**
     * A variable row's credit at the share of the city's energy the variable rows make: its credit
     * alone [M: output at the 100 highest-load hours of 2025 over nameplate, wind 0.730 x 33.8%,
     * solar 2.621 x 24.0%] falling in a straight line to a tenth of that at half the city's energy
     * [E: the direction and ends from NREL TP-7A40-89587 and ERCOT's first-in/last-in study].
     */
    static double credit(Plant p, double share) {
        if (p.cf == null) return p.credit;
        return p.credit * Math.max(0.1, 1 - share / 0.5 * 0.9);
    }

    /* ===== THE MONTH ===== */

    static final class Month {
        final double[] price = new double[SUB], unserved = new double[SUB], load = new double[SUB];
        double energy, cost, avgPrice, curtailed;
    }

    /**
     * One month's merit order: `mean` MW of demand (losses included), `season` the month's load
     * over the year's mean. Each half-block: the variable rows first (what it cannot take is
     * curtailed), then hydro levels the load with its month of water, then the fuelled rows by
     * marginal cost. A half-block clears at its last row's marginal cost, or VOLL short. `order`
     * is the fuelled rows' indices, kept sorted here by insertion.
     */
    static Month dispatch(Plant[] plants, int[] order, double mean, double season, Month m) {
        for (Plant p : plants) { java.util.Arrays.fill(p.gen, 0); p.fuelMJ = 0; p.revenue = 0; }
        double[] residual = new double[SUB];
        for (int s = 0; s < SUB; s++) residual[s] = m.load[s] = mean * season * BLOCK_LOAD[blockOf(s)];
        m.curtailed = 0;
        for (Plant p : plants) {
            if (p.cf == null) continue;
            for (int s = 0; s < SUB; s++) {
                double out = p.mw * cfAt(p, s);
                double take = Math.min(out, residual[s]);
                p.gen[s] = take; residual[s] -= take; m.curtailed += (out - take) * subHours(s) * HOURS;
            }
        }
        for (Plant p : plants) {
            if (Double.isNaN(p.budget) || p.mw == 0) continue;
            double water = p.mw * p.budget * HOURS, cap = p.mw * p.avail;
            double lo = 0, hi = 0;
            for (double r : residual) hi = Math.max(hi, r);
            if (use(residual, cap, 0) <= water) hi = 0;
            for (int it = 0; it < 40 && hi - lo > 1e-6; it++) {
                double mid = (lo + hi) / 2;
                if (use(residual, cap, mid) > water) lo = mid; else hi = mid;
            }
            for (int s = 0; s < SUB; s++) {
                double mw = Math.min(cap, Math.max(0, residual[s] - hi));
                p.gen[s] = mw; residual[s] -= mw;
            }
        }
        for (int i = 1; i < order.length; i++) {
            int k = order[i]; double mk = plants[k].marginal(); int j = i - 1;
            while (j >= 0 && plants[order[j]].marginal() > mk) { order[j + 1] = order[j]; j--; }
            order[j + 1] = k;
        }
        m.energy = m.cost = 0;
        for (int s = 0; s < SUB; s++) {
            double price = 0;
            for (int k : order) {
                if (residual[s] <= 0) break;
                Plant p = plants[k];
                double mw = Math.min(p.mw * p.avail, residual[s]);
                if (mw <= 0) continue;
                p.gen[s] = mw; residual[s] -= mw; price = p.marginal();
            }
            m.unserved[s] = Math.max(0, residual[s]);
            m.price[s] = m.unserved[s] > 1e-9 ? VOLL : price;
        }
        for (Plant p : plants) {
            for (int s = 0; s < SUB; s++) {
                double mwh = p.gen[s] * subHours(s) * HOURS;
                p.fuelMJ += mwh * 1000 * p.heat;
                p.revenue += mwh * m.price[s];
                m.energy += mwh;
                m.cost += mwh * 1000 * p.heat * p.fuel.perMJ + mwh * p.vom;
            }
        }
        double paid = 0, served = 0;
        for (int s = 0; s < SUB; s++) {
            double mwh = (m.load[s] - m.unserved[s]) * subHours(s) * HOURS;
            paid += mwh * m.price[s]; served += mwh;
        }
        m.avgPrice = served > 0 ? paid / served : 0;
        return m;
    }

    /** A block's price over the month: its two halves' average. */
    static double blockPrice(Month m, int b) { return (m.price[2 * b] + m.price[2 * b + 1]) / 2; }

    static double use(double[] residual, double cap, double level) {
        double e = 0;
        for (int s = 0; s < SUB; s++) e += Math.min(cap, Math.max(0, residual[s] - level)) * subHours(s) * HOURS;
        return e;
    }

    static int[] fuelled(Plant[] plants) {
        int c = 0;
        for (Plant p : plants) if (p.cf == null && Double.isNaN(p.budget)) c++;
        int[] o = new int[c]; c = 0;
        for (int i = 0; i < plants.length; i++) if (plants[i].cf == null && Double.isNaN(plants[i].budget)) o[c++] = i;
        return o;
    }

    /* ===== THE PLANNER ===== */

    static double crf(double r, double n) { return r / (1 - Math.pow(1 + r, -n)); }

    /** A kW of `p` a year run `hours` a year: capital at r over its life, fixed upkeep, running. */
    static double yearCost(Plant p, double hours) {
        return p.capex * crf(R, p.life) + p.fom + p.marginal() * hours / 1000;
    }

    static double[] sliceHours() { return new double[]{8760, 8760 * (BLOCK_HOURS[1] + BLOCK_HOURS[2]), 8760 * BLOCK_HOURS[2]}; }

    /** The variable rows' share of a year's energy at `mean` MW. */
    static double variableShare(Plant[] ps, double mean) {
        double e = 0;
        for (Plant p : ps) if (p.cf != null) for (int b = 0; b < 3; b++) e += p.mw * p.cf[b] * BLOCK_HOURS[b];
        return Math.min(1, e / mean);
    }

    static double firm(Plant[] ps, double mean) {
        double share = variableShare(ps, mean), f = 0;
        for (Plant p : ps) f += p.mw * credit(p, share);
        return f;
    }

    static double needed(double mean) { return mean * PEAK_OVER_MEAN * (1 + RESERVE); }

    /**
     * A year's orders for a fleet facing `mean` MW: while the firm MW fall short of the reserve
     * test, one unit of the row cheapest a firm kW-year for the slice the gap lies in; then up to
     * `spreadOrders` units of the row whose year at the dispatch's block prices earns most over
     * its own year, per dollar, if any earns above nothing. `allowed[i]`: may row i be built.
     */
    static String plan(Plant[] ps, boolean[] allowed, double mean, int spreadOrders) {
        StringBuilder said = new StringBuilder();
        double[] hrs = sliceHours();
        int guard = 0;
        while (firm(ps, mean) < needed(mean) && guard++ < 400) {
            double cover = 0;
            for (Plant p : ps) if (p.cf == null) cover += p.mw * p.avail;
            int slice = cover < mean * BLOCK_LOAD[0] ? 0 : cover < mean * BLOCK_LOAD[1] ? 1 : 2;
            // a unit bigger than the gap pays for its idle part: its firm kW-year over the firm kW the gap can use
            double gap = needed(mean) - firm(ps, mean);
            int best = -1; double bestCost = Double.MAX_VALUE;
            for (int i = 0; i < ps.length; i++) {
                if (!allowed[i] || ps[i].cf != null) continue;
                double firmUnit = ps[i].unit * ps[i].credit;
                double y = yearCost(ps[i], hrs[slice] * Math.min(1, ps[i].avail)) / ps[i].credit * firmUnit / Math.min(firmUnit, gap);
                if (y < bestCost) { bestCost = y; best = i; }
            }
            if (best < 0) break;
            ps[best].mw += ps[best].unit;
            said.append(ps[best].name).append(" (").append(BLOCK[slice]).append(") ");
        }
        // the spread: what one more unit saves the year's running (its fuel displaced, the shortfall
        // it serves at VOLL), found by dispatching the year again with it, less its own year
        for (int k = 0; k < spreadOrders; k++) {
            double before = yearRunning(ps, mean);
            int best = -1; double bestRatio = 0;
            for (int i = 0; i < ps.length; i++) {
                if (!allowed[i]) continue;
                Plant p = ps[i];
                p.mw += p.unit;
                double saved = before - yearRunning(ps, mean);
                p.mw -= p.unit;
                double net = saved - p.unit * 1000 * (p.capex * crf(R, p.life) + p.fom);
                double ratio = net / (p.unit * 1000 * p.capex);
                if (ratio > bestRatio) { bestRatio = ratio; best = i; }
            }
            if (best < 0) break;
            ps[best].mw += ps[best].unit;
            said.append(ps[best].name).append(" (spread) ");
        }
        return said.toString().trim();
    }

    /** A year's running cost on `ps`: fuel and variable upkeep, and the energy short at VOLL. */
    static double yearRunning(Plant[] ps, double mean) {
        Month m = new Month();
        double c = 0;
        for (int mo = 0; mo < 12; mo++) {
            dispatch(ps, fuelled(ps), mean, SEASON[mo], m);
            c += m.cost;
            for (int s = 0; s < SUB; s++) c += m.unserved[s] * subHours(s) * HOURS * VOLL;
        }
        return c;
    }

    /** The tariff at cost, $ a MWh delivered: fuel and running, fixed upkeep and capital, over the energy served, grossed up for the losses, plus the grid. */
    static double tariff(Plant[] ps, double fuelAndRunning, double served) {
        double fixed = 0;
        for (Plant p : ps) fixed += p.mw * 1000 * (p.capex * crf(R, p.life) + p.fom);
        return (fuelAndRunning + fixed) / served / (1 - LOSSES) + GRID;
    }

    /* ===== SCENARIOS ===== */

    static String f(double x, int d) { return String.format(Locale.ROOT, "%,." + d + "f", x); }

    static Plant[] fleet(List<Plant> c, double... mw) {
        Plant[] ps = new Plant[c.size()];
        for (int i = 0; i < ps.length; i++) ps[i] = c.get(i).copy(i < mw.length ? mw[i] : 0);
        return ps;
    }

    static boolean[] allow(List<Plant> c, String... names) {
        boolean[] a = new boolean[c.size()];
        for (int i = 0; i < a.length; i++) for (String n : names) if (c.get(i).name.equals(n)) a[i] = true;
        return a;
    }

    /** The city's demand in the scenarios, MW delivered on the year's mean. */
    static double CITY = 1000;

    /** The wires' part a MWh delivered paid by industry: transmission alone [D: AEO2026 table 8, 1.885 cents]. */
    static final double GRID_INDUSTRY = 18.85;

    /**
     * A year of the city (losses grossed up) on `ps`: prints the fleet and its year, the market's
     * bills (Jerus's Q1: each half-block's clearing price on what it delivered, plus the wires at
     * cost) and, beside them, the tariff at cost; returns the commerce bill a MWh.
     */
    static double year(String title, Plant[] ps) {
        double mean = CITY / (1 - LOSSES);
        double cost = 0, energy = 0, unserved = 0, asked = 0, curtailed = 0, served = 0, paid = 0;
        double[] fuel = new double[Fuel.values().length];
        double[] made = new double[ps.length];
        Month m = new Month();
        double[] priceSum = new double[3];
        for (int mo = 0; mo < 12; mo++) {
            dispatch(ps, fuelled(ps), mean, SEASON[mo], m);
            cost += m.cost; energy += m.energy; curtailed += m.curtailed;
            for (int b = 0; b < 3; b++) {
                priceSum[b] += blockPrice(m, b) / 12;
            }
            for (int s = 0; s < SUB; s++) {
                unserved += m.unserved[s] * subHours(s) * HOURS;
                asked += m.load[s] * subHours(s) * HOURS;
                served += (m.load[s] - m.unserved[s]) * subHours(s) * HOURS;
                paid += (m.load[s] - m.unserved[s]) * subHours(s) * HOURS * m.price[s];
            }
            for (int i = 0; i < ps.length; i++) {
                fuel[ps[i].fuel.ordinal()] += ps[i].fuelMJ;
                for (int s = 0; s < SUB; s++) made[i] += ps[i].gen[s] * subHours(s) * HOURS;
            }
        }
        double t = tariff(ps, cost, served);
        double energyPrice = paid / served / (1 - LOSSES);            // a MWh delivered: the losses are bought too
        double fixed = 0;
        for (Plant p : ps) fixed += p.mw * 1000 * (p.capex * crf(R, p.life) + p.fom);
        System.out.println("--- " + title + " ---");
        StringBuilder fl = new StringBuilder("  fleet:");
        for (int i = 0; i < ps.length; i++) if (ps[i].mw > 0)
            fl.append(String.format(Locale.ROOT, " %s %s MW (runs %s%%, %s%% of the energy);", ps[i].name, f(ps[i].mw, 0),
                    f(100 * made[i] / (ps[i].mw * 8760), 0), f(100 * made[i] / energy, 0)));
        System.out.println(fl);
        System.out.printf(Locale.ROOT, "  firm %s MW against %s needed; block prices a year $%s / %s / %s a MWh; fuel and running $%s a MWh made%n",
                f(firm(ps, mean), 0), f(needed(mean), 0), f(priceSum[0], 1), f(priceSum[1], 1), f(priceSum[2], 1), f(cost / energy, 1));
        System.out.printf(Locale.ROOT, "  unserved %s%%, curtailed %s%% of the variable rows' output; TARIFF AT COST $%s a MWh (%s cents a kWh)%n",
                f(100 * unserved / asked, 3), f(100 * curtailed / Math.max(1, curtailed + variableMade(ps, made)), 1), f(t, 1), f(t / 10, 2));
        System.out.printf(Locale.ROOT, "  MARKET: energy $%s a MWh delivered; commerce pays %s cents a kWh, industry %s; the margin over running pays %s%% of the plants' capital and fixed upkeep%n",
                f(energyPrice, 1), f((energyPrice + GRID) / 10, 2), f((energyPrice + GRID_INDUSTRY) / 10, 2), f(100 * (paid - cost) / fixed, 0));
        double coalT = fuel[Fuel.COAL.ordinal()] / 25_120;
        if (coalT > 0) System.out.printf(Locale.ROOT, "  coal burned %s t a year (%s t a MWh of coal power)%n", f(coalT, 0),
                f(coalT / made[0], 3));
        return energyPrice + GRID;
    }

    static double variableMade(Plant[] ps, double[] made) {
        double v = 0;
        for (int i = 0; i < ps.length; i++) if (ps[i].cf != null) v += made[i];
        return v;
    }

    static double plannedYear(String title, List<Plant> c, boolean[] allowed) {
        Plant[] ps = fleet(c);
        String said = plan(ps, allowed, CITY / (1 - LOSSES), 40);
        System.out.println("  (planner: " + said.replaceAll("(\\b[\\w-]+(?: [\\w-]+)*) \\((\\w+)\\)( \\1 \\(\\2\\))+", "$1 ($2) xN") + ")");
        return year(title, ps);
    }

    static void scenarios() {
        List<Plant> c = today();
        System.out.println("=== The merit order: each row's marginal cost, $ a MWh, at the middle of the game's bands ===");
        for (Plant p : c) System.out.printf(Locale.ROOT, "  %-18s %7s  (heat %s MJ/kWh, efficiency %s%%)%n", p.name, f(p.marginal(), 1),
                f(p.heat, 2), p.heat > 0 ? f(360 / p.heat, 1) : "-");
        System.out.printf(Locale.ROOT, "  today's tariff: D$10 a kW a month = $%s a MWh%n", f(10.0 / 720 * 1000, 1));

        System.out.println("=== The screening: a firm kW-year, $, for each slice of the load (r = 4%) ===");
        double[] hrs = sliceHours();
        for (int s = 0; s < 3; s++) {
            StringBuilder line = new StringBuilder();
            Plant best = null; double bestCost = Double.MAX_VALUE;
            for (Plant p : c) {
                if (p.cf != null) continue;
                double y = yearCost(p, hrs[s] * p.avail) / p.credit;
                line.append(String.format(Locale.ROOT, " %s %s;", p.name, f(y, 0)));
                if (y < bestCost && Double.isNaN(p.budget)) { bestCost = y; best = p; }
            }
            System.out.printf(Locale.ROOT, "  %-8s (%s h):%s -> %s%n", BLOCK[s], f(hrs[s], 0), line, best.name);
        }

        System.out.println("=== A 1 GW city, its fleet planned from nothing by the two rules ===");
        String[] all = {"Coal Power Plant", "Oil-Fired Plant", "Diesel Station", "Oil-Fired Turbine", "Nuclear Plant", "Biomass Plant", "Wind Farm", "Solar Farm"};
        double base = plannedYear("Today's menu, no hydro site", c, allow(c, all));

        double coal = Fuel.COAL.perMJ;
        Fuel.COAL.perMJ = 75.0 / 25_120;
        plannedYear("Coal at its import price (D$75 a t: no mine near)", c, allow(c, all));
        Fuel.COAL.perMJ = 50.0 / 25_120;
        plannedYear("Coal at its export price (D$50 a t: the city's own mine)", c, allow(c, all));
        Fuel.COAL.perMJ = coal;

        String[] hydro = Arrays.copyOf(all, all.length + 1); hydro[all.length] = "Hydro Station";
        Plant[] h = fleet(c); h[6].mw = 400;          // 40 stations: the river's slots
        String said = plan(h, allow(c, all), 1000 / (1 - LOSSES), 40);
        year("With 400 MW of hydro sites taken first", h);

        System.out.println("=== A shock on the first city's fleet: oil x2.76 and coal x1.23 for a year (2022 over 2020, EIA EPA 7.4) ===");
        Plant[] ps = fleet(c);
        plan(ps, allow(c, all), 1000 / (1 - LOSSES), 40);
        double oil = Fuel.FUEL_OIL.perMJ, die = Fuel.DIESEL.perMJ;
        Fuel.FUEL_OIL.perMJ *= 2.76; Fuel.DIESEL.perMJ *= 2.76; Fuel.COAL.perMJ *= 1.23;
        double shocked = year("The same fleet in the shock year", ps);
        Fuel.FUEL_OIL.perMJ = oil; Fuel.DIESEL.perMJ = die; Fuel.COAL.perMJ = coal;
        System.out.printf(Locale.ROOT, "  commerce's bill moves %s%%%n", f(100 * (shocked / base - 1), 1));

        System.out.println("=== Wind alone: Rotterdam's fleet, 2.2 GW of wind farms for 1 GW (its 8.86 GW average for 4.43 GW, scaled) ===");
        Plant[] w = fleet(c); w[7].mw = 2_200;
        year("2,200 MW of wind and nothing else", w);
        plan(w, allow(c, all), 1000 / (1 - LOSSES), 0);
        year("...with what the reserve test then orders", w);

        System.out.println("=== An island with no coal: oil and what else it may build ===");
        String[] island = {"Oil-Fired Plant", "Diesel Station", "Oil-Fired Turbine", "Biomass Plant", "Wind Farm", "Solar Farm"};
        Plant[] is = fleet(c);
        plan(is, allow(c, island), CITY / (1 - LOSSES), 60);
        double islandBase = year("The island, planned", is);
        oil = Fuel.FUEL_OIL.perMJ; die = Fuel.DIESEL.perMJ;
        Fuel.FUEL_OIL.perMJ *= 2.76; Fuel.DIESEL.perMJ *= 2.76;
        double islandShock = year("The island in the shock year (oil x2.76)", is);
        Fuel.FUEL_OIL.perMJ = oil; Fuel.DIESEL.perMJ = die;
        System.out.printf(Locale.ROOT, "  the island's commerce bill moves %s%%%n", f(100 * (islandShock / islandBase - 1), 1));

        System.out.println("=== A town of 10 MW, today's menu ===");
        CITY = 10;
        plannedYear("The town, planned (a unit bigger than its need pays for its idle part)", c, allow(c, all));
        CITY = 1000;

        System.out.println("=== The 1900 city (Q2) ===");
        plannedYear("B: 1900's kinds (coal and oil steam, no hydro site) at today's figures", c, allow(c, "Coal Power Plant", "Oil-Fired Plant"));
        List<Plant> c1900 = new ArrayList<>(c);
        c1900.set(0, new Plant("Coal Power Plant", Fuel.COAL, mj(92_500), 9.37, 89.0, 3_271, 40, 0.85, 0.85, null, Double.NaN, 325));
        c1900.set(1, new Plant("Oil-Fired Plant", Fuel.FUEL_OIL, mj(92_500 * 11_227 / 8_490.0), 9.68, 83.0, 3_054, 40, 0.85, 0.85, null, Double.NaN, 100));
        plannedYear("C: the same at 1902's heat rate (92,500 Btu/kWh), capital as today's", c1900, allow(c1900, "Coal Power Plant", "Oil-Fired Plant"));
    }

    /* ===== TIMING ===== */

    static volatile double sink;

    static double time(int rows, int months) {
        List<Plant> base = today();
        Plant[] ps = new Plant[rows];
        Random rnd = new Random(1);
        for (int i = 0; i < rows; i++) ps[i] = base.get(i % base.size()).copy(50 + rnd.nextInt(500));
        int[] order = fuelled(ps);
        Month m = new Month();
        double coal = Fuel.COAL.perMJ, die = Fuel.DIESEL.perMJ;
        long t0 = System.nanoTime();
        double acc = 0;
        for (int k = 0; k < months; k++) {
            Fuel.COAL.perMJ = coal * (1 + 0.1 * Math.sin(k * 0.1));
            Fuel.DIESEL.perMJ = die * (1 + 0.3 * Math.sin(k * 0.07));
            dispatch(ps, order, 1000 + (k % 100), SEASON[k % 12], m);
            acc += m.avgPrice;
        }
        long t1 = System.nanoTime();
        sink = acc;
        Fuel.COAL.perMJ = coal; Fuel.DIESEL.perMJ = die;
        return (t1 - t0) / (double) months;
    }

    static void timing() {
        System.out.println("=== Timing: ns a month, median of 7 rounds after 5 of warm-up ===");
        for (int rows : new int[]{9, 18, 64, 512}) {
            for (int w = 0; w < 5; w++) time(rows, 200_000);
            double[] r = new double[7];
            for (int i = 0; i < 7; i++) r[i] = time(rows, 200_000);
            Arrays.sort(r);
            System.out.printf(Locale.ROOT, "  %4d rows: %s ns a month (min %s, max %s)%n", rows, f(r[3], 0), f(r[0], 0), f(r[6], 0));
        }
    }

    public static void main(String[] a) {
        if (a.length == 0 || !a[0].equals("time")) scenarios();
        if (a.length > 0 && a[0].equals("scenarios")) return;
        timing();
    }
}
