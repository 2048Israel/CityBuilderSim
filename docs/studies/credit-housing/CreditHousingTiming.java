package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;

/**
 * What the credit and housing design costs a month (spec-credit-housing.md
 * section 4): the per-cell kernel of sections 2.2 to 2.4 timed at today's 78
 * cells and at the households study's likely 390 (five adult age bands) and
 * 1,560, against a real month of LongPlaytest's city on GameFiles.scratch.
 *
 * Not game code: compiled against target/classes into a scratch folder.
 *
 *   javac -cp target/classes -d <scratch> docs/studies/credit-housing/CreditHousingTiming.java
 *   java -cp "<scratch>;target/classes;<jars>" ham.citybuildersim.CreditHousingTiming
 */
public class CreditHousingTiming {

    static final PrintStream NULL = new PrintStream(OutputStream.nullOutputStream());

    static double cdf(double x) { return BusinessDebtManager.normalCdf(x); }
    static double below(double x, double mean, double s) {
        return x <= 0 ? 0 : cdf((Math.log(x) - Math.log(mean) + .5 * s * s) / s);
    }
    static double meanAbove(double x, double mean, double s) {
        return x <= 0 ? mean : mean * cdf((Math.log(mean) - .5 * s * s + s * s - Math.log(x)) / s);
    }
    static double shortfall(double g, double mean, double s) {
        if (g <= 0) return 0;
        double d1 = (Math.log(mean / g) + .5 * s * s) / s;
        return g * cdf(-(d1 - s)) - mean * cdf(-d1);
    }
    static double annuity(double b, double annual, double n) {
        double r = annual / 12;
        return r <= 0 ? b / n : b * r / (1 - Math.pow(1 + r, -n));
    }

    /* the cells, one array a figure */
    final int n;
    final double[] hh, y, Y, fixed, food, sav, k, want;
    final double[] R, r, L, late, lock, own, M, rate, left, arrears;
    double price = 80, cost = 80;
    double sink;

    CreditHousingTiming(int n, long seed) {
        this.n = n;
        java.util.Random rng = new java.util.Random(seed);
        hh = new double[n]; y = new double[n]; Y = new double[n]; fixed = new double[n]; food = new double[n];
        sav = new double[n]; k = new double[n]; want = new double[n];
        R = new double[n]; r = new double[n]; L = new double[n]; late = new double[n]; lock = new double[n];
        own = new double[n]; M = new double[n]; rate = new double[n]; left = new double[n]; arrears = new double[n];
        for (int i = 0; i < n; i++) {
            hh[i] = 1_000 + rng.nextDouble() * 100_000;
            Y[i] = 3 + rng.nextDouble() * 20; y[i] = .8 * Y[i];
            fixed[i] = .35 * y[i]; food[i] = .9;
            sav[i] = y[i] * Math.exp(rng.nextGaussian() * 1.5 + 1);
            k[i] = 1 + rng.nextInt(4); want[i] = .3 + .5 * rng.nextDouble();
            R[i] = .5 * y[i] * .3; r[i] = .3; own[i] = .4; M[i] = 200; rate[i] = .045; left[i] = 200;
        }
    }

    /** Cards, section 2.2: per cell, interest, shocks, the minimum and the late line, paydown, the pipeline. */
    void cards(double cardRate) {
        double im = cardRate / 12, minShare = Math.max(.03, im + .01);
        for (int i = 0; i < n; i++) {
            R[i] += R[i] * im;
            double free = Math.max(0, 1 - r[i] - late[i] - lock[i]);
            double shock = y[i];
            double hits = free / 12.0;
            R[i] += hits * shortfall(shock, sav[i], 1.5);
            r[i] += hits * below(shock, sav[i], 1.5);
            if (r[i] > 1e-12) {
                double B = R[i] / r[i], cap = Math.max(1e-9, y[i] - fixed[i] - food[i]);
                double line = cap / minShare;
                double lb = meanAbove(line, B, 1.28) * r[i] / 12, ls = (1 - below(line, B, 1.28)) * r[i] / 12;
                R[i] -= lb; r[i] -= ls; L[i] += lb; late[i] += ls;
                B = R[i] / r[i];
                double pay = Math.min(B, minShare * B + .25 * Math.max(0, cap - minShare * B));
                double clear = below(pay, B, 1.28);
                double paid = r[i] * ((B - meanAbove(pay, B, 1.28)) + pay * (1 - clear));
                R[i] = Math.max(0, R[i] - paid); r[i] *= 1 - clear;
            }
            double off = L[i] / 6, offS = late[i] / 6;
            L[i] -= off + L[i] * .1; late[i] -= offS + late[i] * .1;
            lock[i] += offS - lock[i] / 12;
            sink += off;
        }
    }

    /** One cell's buyers at a price, section 2.3: the two tests under the spread. */
    double buyers(int i, double p, double mrate) {
        double gap = want[i] - own[i] - arrears[i];
        if (gap <= 0) return 0;
        double V = p * k[i], need = .065 * V;
        double aSav = 1 - below(need, sav[i], 1.5);
        double loan = .95 * V * 1.04;
        double needY = (annuity(loan, mrate + .02, 300) + V * .015 / 12) / .39 + .01 * Y[i] / .39;
        return gap * hh[i] * aSav * (1 - below(needY, Y[i], .35)) / 36 * k[i];
    }

    /** Homes, sections 2.3-2.4: renewals, the clearing price by bisection, the owners' month and arrears. */
    void homes(double mrate) {
        double fixedSupply = 0;
        for (int i = 0; i < n; i++) {
            rate[i] += (mrate - rate[i]) / 60;
            fixedSupply += hh[i] * own[i] * .001 * k[i] + hh[i] * arrears[i] / 9 * k[i];
        }
        double lo = price / 4, hi = price * 4;
        for (int it = 0; it < 40; it++) {
            double mid = Math.sqrt(lo * hi), d = 0;
            for (int i = 0; i < n; i++) d += buyers(i, mid, mrate);
            double landlord = .004 * 1e6 * Math.max(-1, Math.min(1, (mid - cost) / cost / .1));
            if (d - fixedSupply - landlord > 0) lo = mid; else hi = mid;
        }
        price += (Math.sqrt(lo * hi) - price) / 12;
        for (int i = 0; i < n; i++) {
            double b = buyers(i, price, mrate) / k[i];
            double owners = hh[i] * own[i], tot = owners + b;
            if (tot > 0) {
                M[i] = (owners * M[i] + b * price * k[i] * .95) / tot;
                rate[i] = (owners * rate[i] + b * mrate) / tot;
                left[i] = (owners * left[i] + b * 300) / tot;
                own[i] = Math.min(1, tot / hh[i]);
            }
            double pay = annuity(M[i], rate[i], Math.max(1, left[i]));
            M[i] = Math.max(0, M[i] - (pay - M[i] * rate[i] / 12)); left[i] = Math.max(1, left[i] - 1);
            double need = pay + price * k[i] * .025 / 12;
            double risk = below(need + .9, y[i], .35) * below(6 * need, sav[i], 1.5);
            arrears[i] += (own[i] - arrears[i]) * risk / 12 - arrears[i] * .08;
            own[i] *= 1 - .004;     // owners leave (est.: about 5% a year), so buyers keep coming and the book stays
        }
    }

    /** The statistics, section 2.6: three medians of the cells' mixture, 60 bisection steps each. */
    double quantile(double q, double[] share, double[] mean, double sigma, boolean holdersOnly) {
        double lo = 1e-9, hi = 1e7, tot = 0;
        for (int i = 0; i < n; i++) tot += holdersOnly ? hh[i] * share[i] : hh[i];
        for (int it = 0; it < 60; it++) {
            double mid = Math.sqrt(lo * hi), acc = 0;
            for (int i = 0; i < n; i++) {
                double p = share[i];
                if (p <= 0 || mean[i] <= 0) { if (!holdersOnly) acc += hh[i]; continue; }
                double f = below(mid, mean[i] / p, sigma);
                acc += holdersOnly ? hh[i] * p * f : hh[i] * ((1 - p) + p * f);
            }
            if (acc / tot < q) lo = mid; else hi = mid;
        }
        return Math.sqrt(lo * hi);
    }

    void stats() {
        double[] mortPer = new double[n];
        for (int i = 0; i < n; i++) mortPer[i] = own[i] * M[i];
        sink += quantile(.5, r, R, 1.28, true) + quantile(.5, own, mortPer, .68, true) + quantile(.5, r, R, 1.28, false);
    }

    /**
     * The cheaper statistics: the mixture's CDF on GRID points spaced evenly in
     * log between the smallest and largest cell means / 100 and x 100, one pass
     * over the cells, every quantile read off it by interpolating in log.
     */
    static final int GRID = 32;
    double[] gridQuantiles(double[] share, double[] mean, double sigma, boolean holdersOnly, double[] qs) {
        double lo = Double.MAX_VALUE, hi = 0, tot = 0;
        for (int i = 0; i < n; i++) {
            if (share[i] <= 0 || mean[i] <= 0) continue;
            double m = mean[i] / share[i];
            lo = Math.min(lo, m); hi = Math.max(hi, m);
        }
        double a = Math.log(lo / 100), b = Math.log(hi * 100);
        double[] F = new double[GRID];
        for (int i = 0; i < n; i++) {
            double p = share[i];
            tot += holdersOnly ? hh[i] * p : hh[i];
            if (p <= 0 || mean[i] <= 0) { if (!holdersOnly) for (int g = 0; g < GRID; g++) F[g] += hh[i]; continue; }
            double mu = Math.log(mean[i] / p) - .5 * sigma * sigma;
            for (int g = 0; g < GRID; g++) {
                double f = cdf((a + (b - a) * g / (GRID - 1) - mu) / sigma);
                F[g] += holdersOnly ? hh[i] * p * f : hh[i] * ((1 - p) + p * f);
            }
        }
        double[] out = new double[qs.length];
        for (int k = 0; k < qs.length; k++) {
            double q = qs[k] * tot;
            if (F[0] >= q) { out[k] = F[0] >= tot * qs[k] && !holdersOnly ? 0 : Math.exp(a); continue; }
            int g = 1;
            while (g < GRID - 1 && F[g] < q) g++;
            double t = (q - F[g - 1]) / Math.max(1e-300, F[g] - F[g - 1]);
            out[k] = Math.exp(a + (b - a) * (g - 1 + t) / (GRID - 1));
        }
        return out;
    }

    void statsGrid() {
        double[] mortPer = new double[n];
        for (int i = 0; i < n; i++) mortPer[i] = own[i] * M[i];
        double[] qs = {.5};
        sink += gridQuantiles(r, R, 1.28, true, qs)[0] + gridQuantiles(own, mortPer, .68, true, qs)[0];
    }

    /** The clearing price by false position (Illinois) from last month's price: a dozen steps at most. */
    void homesFast(double mrate) {
        double fixedSupply = 0;
        for (int i = 0; i < n; i++) {
            rate[i] += (mrate - rate[i]) / 60;
            fixedSupply += hh[i] * own[i] * .001 * k[i] + hh[i] * arrears[i] / 9 * k[i];
        }
        final double fs = fixedSupply;
        java.util.function.DoubleUnaryOperator ex = p -> {
            double d = 0;
            for (int i = 0; i < n; i++) d += buyers(i, p, mrate);
            return d - fs - .004 * 1e6 * Math.max(-1, Math.min(1, (p - cost) / cost / .1));
        };
        double a = price / 2, b = price * 2, fa = ex.applyAsDouble(a), fb = ex.applyAsDouble(b);
        double c = price;
        if (fa * fb < 0) {
            int side = 0;
            for (int it = 0; it < 12; it++) {
                c = (a * fb - b * fa) / (fb - fa);
                double fc = ex.applyAsDouble(c);
                if (Math.abs(fc) < 1e-6 * (1 + Math.abs(fa))) break;
                if (fc * fb > 0) { b = c; fb = fc; if (side == -1) fa /= 2; side = -1; }
                else { a = c; fa = fc; if (side == 1) fb /= 2; side = 1; }
            }
        } else c = fa > 0 ? b : a;
        price += (c - price) / 12;
        homesOwners(mrate);
    }

    void homesOwners(double mrate) {
        for (int i = 0; i < n; i++) {
            double b = buyers(i, price, mrate) / k[i];
            double owners = hh[i] * own[i], tot = owners + b;
            if (tot > 0) {
                M[i] = (owners * M[i] + b * price * k[i] * .95) / tot;
                rate[i] = (owners * rate[i] + b * mrate) / tot;
                left[i] = (owners * left[i] + b * 300) / tot;
                own[i] = Math.min(1, tot / hh[i]);
            }
            double pay = annuity(M[i], rate[i], Math.max(1, left[i]));
            M[i] = Math.max(0, M[i] - (pay - M[i] * rate[i] / 12)); left[i] = Math.max(1, left[i] - 1);
            double need = pay + price * k[i] * .025 / 12;
            double risk = below(need + .9, y[i], .35) * below(6 * need, sav[i], 1.5);
            arrears[i] += (own[i] - arrears[i]) * risk / 12 - arrears[i] * .08;
            own[i] *= 1 - .004;     // owners leave (est.: about 5% a year), so buyers keep coming and the book stays
        }
    }

    boolean fast;
    void month(boolean withStats) {
        cards(.2099);
        if (fast) homesFast(.0455); else homes(.0455);
        if (withStats) { if (fast) statsGrid(); else stats(); }
    }

    static double medianMicros(CreditHousingTiming t, boolean stats, int months) {
        double[] rounds = new double[7];
        for (int w = 0; w < 2_000; w++) t.month(stats);
        for (int round = 0; round < 7; round++) {
            long t0 = System.nanoTime();
            for (int m = 0; m < months; m++) t.month(stats);
            rounds[round] = (System.nanoTime() - t0) / 1e3 / months;
        }
        Arrays.sort(rounds);
        return rounds[3];
    }

    public static void main(String[] args) {
        PrintStream out = System.out;
        out.println("--- 1. the kernel a month, median of 7 rounds of 1,000 months after 2,000 to warm ---");
        for (int cells : new int[]{78, 390, 1560}) {
            CreditHousingTiming t = new CreditHousingTiming(cells, 7);
            double without = medianMicros(t, false, 1_000);
            double with = medianMicros(t, true, 1_000);
            out.printf("  %,5d cells: cards + homes %8.1f us, with the three medians %8.1f us (sink %.3g)%n",
                    cells, without, with, t.sink);
        }
        out.println("--- 1b. as designed: the price by false position, the medians off a 32-point grid ---");
        for (int cells : new int[]{78, 390, 1560}) {
            CreditHousingTiming t = new CreditHousingTiming(cells, 7);
            t.fast = true;
            double without = medianMicros(t, false, 1_000);
            double with = medianMicros(t, true, 1_000);
            double[] mortPer = new double[t.n];
            for (int i = 0; i < t.n; i++) mortPer[i] = t.own[i] * t.M[i];
            double exactC = t.quantile(.5, t.r, t.R, 1.28, true), gridC = t.gridQuantiles(t.r, t.R, 1.28, true, new double[]{.5})[0];
            double exactM = t.quantile(.5, t.own, mortPer, .68, true), gridM = t.gridQuantiles(t.own, mortPer, .68, true, new double[]{.5})[0];
            out.printf("  %,5d cells: cards + homes %8.1f us, with the medians %8.1f us; grid against bisection: card %+.2f%%, mortgage %+.2f%%%n",
                    cells, without, with, 100 * (gridC / exactC - 1), 100 * (gridM / exactM - 1));
        }
        if (args.length > 1 && args[1].equals("kernel")) return;
        int months = args.length > 0 ? Integer.parseInt(args[0]) : 1_200;
        out.printf("--- 2. a real month: LongPlaytest's city on automatic building to month %,d, then 60 months timed ---%n", months);
        long started = System.currentTimeMillis();
        LongPlaytest.out = NULL;
        LongPlaytest.AUTOBUILD = true;
        System.setOut(NULL);
        Game g;
        double[] times = new double[60];
        try {
            g = new Game(GameFiles.scratch("credit-housing-timing"), LongPlaytest.founding());
            g.run();
            g.getAutoBuilder().setOn(true, g.getDecisions());
            LongPlaytest.villageBuild(g, "House", 40);
            LongPlaytest.villageBuild(g, "Convenience Store", 3);
            LongPlaytest.villageBuild(g, "Mixed Farm", 2);
            LongPlaytest.run(g, 3);
            LongPlaytest.villageBuild(g, "House", 20);
            LongPlaytest.run(g, 4);
            LongPlaytest.villageBuild(g, "Convenience Store", 2);
            LongPlaytest.villageBuild(g, "Construction Depot", 1);
            LongPlaytest.run(g, 5);
            LongPlaytest.advise(g);
            LongPlaytest.run(g, 6);
            int stop = 0;
            while (g.getMonth() < months) {
                stop++;
                int skip = switch (stop % 6) { case 0 -> 100; case 1 -> 12; case 2 -> 24; case 3 -> 60; case 4 -> 6; default -> 120; };
                LongPlaytest.run(g, Math.min(skip, months - g.getMonth()));
                if (g.getMonth() >= months) break;
                for (int move = 0; move < LongPlaytest.movesPerLook(); move++) {
                    if (LongPlaytest.advise(g) == null) break;
                    LongPlaytest.run(g, 1);
                }
                LongPlaytest.run(g, 2);
            }
            for (int m = 0; m < 60; m++) {
                long t0 = System.nanoTime();
                g.simulateMonths(1);
                times[m] = (System.nanoTime() - t0) / 1e6;
            }
        } finally {
            System.setOut(out);
        }
        Arrays.sort(times);
        out.printf("  month %,d, population %,d, %d household cells: a month takes %.2f ms (median of 60; the run took %.0f s)%n",
                g.getMonth(), g.getPopulationManager().getPopulation(), g.getHouseholdBalance().cellCount(), times[30],
                (System.currentTimeMillis() - started) / 1e3);
    }
}
