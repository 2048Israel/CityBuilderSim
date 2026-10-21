import java.nio.file.*;
import java.util.*;

/**
 * A throwaway prototype for docs/studies/spec-nations.md: the world market's
 * January solve on Jerus's partner rule, timed, and the city's month by the
 * slopes. Not game code: nothing in the game or the harnesses reads it.
 *
 * WHY. Jerus, 2026-10-20, on 197 x 197: "wtf? thats not good". The study
 * says the solve is small; this measures it. The nations are 196 nodes at the
 * most populous place of each country in Natural Earth's place list (the
 * Earth file's, dumped beside this as places-by-country.tsv), weighted by a
 * rough GDP: a hand table for the largest sixty-odd (IMF, 2024, US$ bn,
 * rounded), the rest their places' people at US$2,500 a head. The goods are
 * fifty synthetic goods, each with its own bulk (freight as a share of its
 * price), producers and demand; good 0 is drawn like sawn wood, with its
 * producers named. Every figure it prints is the model's, not the world's.
 *
 * THE MODEL (spec-nations 4). For each good: every producer i has a mill
 * price p_i and a supply S_i = S0_i (p_i / P0)^ES; every buyer j has a price
 * index P_j = -THETA ln sum_i exp(-(p_i + t_ij) / THETA) over its allowed
 * sellers, a demand D_j = D0_j (P_j / P0)^-ED, and buys x_ij = D_j
 * exp((P_j - p_i - t_ij) / THETA) from each (the logit, Wilson's
 * doubly-constrained gravity model when the curves are fixed). The solve
 * scales u_i = exp(-p_i / THETA) until every producer's sales meet its
 * supply: Sinkhorn's iteration, with the curves bent each sweep.
 *
 * Run: java NationsModel.java places-by-country.tsv
 */
public class NationsModel {

    static final double P0 = 100;          // every good's reference price, US$ a tonne
    static final double THETA = 8;          // the buyers' spread, 8% of the price (est.)
    static final double ED = 0.5, ES = 0.4; // demand and supply elasticities (est.)
    static final double DETOUR = 1.25;      // sea or road distance over the great circle (est.)
    static final double HOME_KM = 300;      // a nation's haul to itself (est.)
    static final double HANDLING = 2;       // a border-crossing tonne's port and handling, US$ (est.)
    static final int GOODS = 50, NATIONS = 196, TOP = 20, EXPORTERS = 10, NEAREST = 10;
    /** The nearest partners: "nations" (Jerus's words: the 10 nearest nations) or "producers" (the 10 nearest that make the good). */
    static String RULE = System.getProperty("rule", "producers");
    static final double OMEGA = Double.parseDouble(System.getProperty("omega", "1.0"));

    static final Set<String> SKIP = Set.of("Antarctica", "Svalbard and Jan Mayen Islands", "Aland",
            "Chatham Islands", "Kermadec Islands", "South Georgia and the Islands", "Tokelau", "Niue",
            "Falkland Islands", "Western Sahara", "Somaliland", "Northern Cyprus", "Necw Zealand", "Gibraltar",
            "Puerto Rico", "Guam", "United States Virgin Islands", "American Samoa", "Northern Mariana Islands",
            "French Polynesia", "New Caledonia", "Curacao", "Aruba", "Bermuda", "Cayman Islands",
            "Turks and Caicos Islands", "Isle of Man", "Faroe Islands", "Greenland", "Cook Islands");

    static final Map<String, Double> GDP = new HashMap<>();
    static void g(String n, double v) { GDP.put(n, v); }
    static {
        g("United States of America", 29180); g("China", 18750); g("Germany", 4660); g("Japan", 4030);
        g("India", 3890); g("United Kingdom", 3640); g("France", 3160); g("Italy", 2380); g("Canada", 2240);
        g("Brazil", 2180); g("Russia", 2160); g("South Korea", 1870); g("Mexico", 1850); g("Australia", 1800);
        g("Spain", 1730); g("Indonesia", 1400); g("Turkey", 1320); g("Netherlands", 1230);
        g("Saudi Arabia", 1100); g("Switzerland", 940); g("Poland", 910); g("Taiwan", 780);
        g("Belgium", 660); g("Argentina", 630); g("Sweden", 600); g("Ireland", 580); g("Singapore", 550);
        g("Israel", 540); g("United Arab Emirates", 540); g("Thailand", 530); g("Austria", 520);
        g("Norway", 480); g("Vietnam", 470); g("Philippines", 460); g("Bangladesh", 450); g("Malaysia", 420);
        g("Colombia", 420); g("Denmark", 410); g("Hong Kong S.A.R.", 410); g("Iran", 400);
        g("South Africa", 400); g("Egypt", 390); g("Romania", 380); g("Pakistan", 370); g("Czechia", 350);
        g("Chile", 330); g("Portugal", 310); g("Finland", 300); g("Peru", 290); g("Kazakhstan", 290);
        g("Iraq", 270); g("Greece", 260); g("New Zealand", 260); g("Algeria", 260); g("Nigeria", 250);
        g("Qatar", 220); g("Hungary", 220); g("Ukraine", 190); g("Kuwait", 160); g("Morocco", 150);
        g("Ethiopia", 150); g("Slovakia", 140); g("Ecuador", 120); g("Dominican Republic", 125);
        g("Kenya", 110); g("Oman", 110); g("Guatemala", 110); g("Bulgaria", 110); g("Luxembourg", 90);
        g("Croatia", 90); g("Costa Rica", 95); g("Panama", 85); g("Lithuania", 80); g("Serbia", 80);
        g("Uruguay", 80); g("Slovenia", 70); g("Latvia", 43); g("Estonia", 42); g("Iceland", 33);
        g("Cyprus", 35); g("Malta", 24);
    }

    /** Good 0's big sawmilling nations and their shares of the world's output (est., the shape of FAO's sawnwood table, not its figures); every other nation saws a little for itself, 15% of the world's output by GDP^0.7. */
    static final Map<String, Double> WOOD = Map.ofEntries(Map.entry("Canada", .18), Map.entry("United States of America", .17),
            Map.entry("Russia", .14), Map.entry("China", .10), Map.entry("Sweden", .05), Map.entry("Germany", .05),
            Map.entry("Finland", .04), Map.entry("Brazil", .04), Map.entry("Austria", .03), Map.entry("Chile", .02),
            Map.entry("New Zealand", .015), Map.entry("Poland", .015));

    record Nation(String name, double lat, double lon, double gdp) { }

    /** One good's market: its freight, its curves, its allowed pairs and its solution. */
    static final class Good {
        double bulk;                  // freight a tonne at 10,000 km, as a share of P0
        double[] s0, d0;              // reference supply and demand, tonnes
        boolean[] maker;
        // the allowed pairs twice, by buyer (for the index) and by seller (for the sales)
        int[] byBuyerStart, byBuyerSeller; double[] byBuyerK;
        int[] bySellerStart, bySellerBuyer; double[] bySellerK;
        double[] p, phi, P, D;        // the solution
        int pairs;
    }

    static int n;                     // nations
    static List<Nation> nations;
    static double[][] km;             // freight distance, km

    public static void main(String[] a) throws Exception {
        nations = load(Path.of(a[0]));
        n = nations.size();
        km = new double[n][n];
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++)
            km[i][j] = i == j ? HOME_KM : DETOUR * gc(nations.get(i), nations.get(j));
        int[][] nearest = new int[n][];
        for (int i = 0; i < n; i++) {
            final int ii = i;
            Integer[] o = new Integer[n];
            for (int j = 0; j < n; j++) o[j] = j;
            Arrays.sort(o, Comparator.comparingDouble(j -> km[ii][j]));
            nearest[i] = new int[NEAREST];
            for (int k = 0; k < NEAREST; k++) nearest[i][k] = o[k + 1];
        }

        Random rng = new Random(20261020L);
        Good[] goods = new Good[GOODS];
        for (int gi = 0; gi < GOODS; gi++) goods[gi] = makeGood(gi, rng);

        System.out.println("NationsModel: " + n + " nations, " + GOODS + " goods, theta " + THETA
                + "% of the price, elasticities demand " + ED + " supply " + ES + "; nearest partners: the 10 nearest " + RULE + "; omega " + OMEGA);

        // ---- the graph: Jerus's rule against the full table ----
        long pairsRule = 0, pairsFull = 0;
        for (Good gd : goods) { wire(gd, nearest, false); pairsRule += gd.pairs; }
        Good[] full = new Good[GOODS];
        Random rng2 = new Random(20261020L);
        for (int gi = 0; gi < GOODS; gi++) { full[gi] = makeGood(gi, rng2); wire(full[gi], nearest, true); pairsFull += full[gi].pairs; }
        System.out.printf("pairs a good: Jerus's rule %,d, the full table %,d (producer x buyer, home pairs included)%n",
                pairsRule / GOODS, pairsFull / GOODS);

        // ---- cold solve, then the timing of a January ----
        int sweepsCold = 0;
        for (Good gd : goods) sweepsCold += solve(gd, 1e-9, 5000);
        for (Good gd : full) solve(gd, 1e-9, 5000);
        double worst = 0, mean = 0; int cnt = 0;
        for (int gi = 0; gi < GOODS; gi++) for (int j = 0; j < n; j++) {
            double r = Math.abs(goods[gi].P[j] / full[gi].P[j] - 1);
            worst = Math.max(worst, r); mean += r; cnt++;
        }
        System.out.printf("cold solve: %.0f sweeps a good on average; the rule's prices against the full table's: mean %.3f%%, worst %.2f%%%n",
                sweepsCold / (double) GOODS, 100 * mean / cnt, 100 * worst);

        // a year: every nation's demand and supply move by its growth, the solve starts from last year's answer
        for (double tol : new double[] { 1e-3, 1e-4, 1e-7 }) {
            Good[] gs = new Good[GOODS], fs = new Good[GOODS];
            for (int gi = 0; gi < GOODS; gi++) { gs[gi] = copy(goods[gi]); fs[gi] = copy(full[gi]); }
            long[] ns = new long[12], nsFull = new long[12]; int sweepsWarm = 0;
            Random grow = new Random(7);
            for (int year = 0; year < 12; year++) {
                double[] gr = new double[n];
                for (int j = 0; j < n; j++) gr[j] = 1.03 + 0.02 * grow.nextGaussian();
                for (int gi = 0; gi < GOODS; gi++) for (int j = 0; j < n; j++) {
                    gs[gi].d0[j] *= gr[j]; gs[gi].s0[j] *= gr[j] * (1 + 0.04 * grow.nextGaussian());
                    fs[gi].d0[j] = gs[gi].d0[j]; fs[gi].s0[j] = gs[gi].s0[j];
                }
                long t0 = System.nanoTime();
                int sw = 0;
                for (Good gd : gs) sw += solve(gd, tol, 5000);
                ns[year] = System.nanoTime() - t0;
                if (year > 0) sweepsWarm += sw;
                long t1 = System.nanoTime();
                for (Good gd : fs) solve(gd, tol, 5000);
                nsFull[year] = System.nanoTime() - t1;
            }
            double off = 0;
            for (int gi = 0; gi < GOODS; gi++) for (int j = 0; j < n; j++) off = Math.max(off, Math.abs(gs[gi].P[j] / fs[gi].P[j] - 1));
            long[] sorted = ns.clone(); Arrays.sort(sorted);
            long[] sortedF = nsFull.clone(); Arrays.sort(sortedF);
            System.out.printf("a January to %.0e (all %d goods, warm): median %.1f ms on the rule, %.1f ms on the full table; %.0f sweeps a good; worst price off the full table %.2f%%%n",
                    tol, GOODS, sorted[6] / 1e6, sortedF[6] / 1e6, sweepsWarm / (11.0 * GOODS), 100 * off);
        }

        // ---- the city: Halifax, linked to every nation ----
        Nation city = new Nation("the city (Halifax)", 44.65, -63.57, 5);
        Good wood = goods[0];
        double[] tc = new double[n];
        for (int j = 0; j < n; j++) tc[j] = freight(wood, DETOUR * gc(city, nations.get(j))) + HANDLING;
        double[] kappa = new double[n], net = new double[n];
        double worldD = 0, worldSlope = 0;
        for (int j = 0; j < n; j++) {
            net[j] = wood.P[j] - tc[j];
            kappa[j] = wood.D[j] / THETA + ED * wood.D[j] / wood.P[j];
            worldD += wood.D[j];
            worldSlope += ED * wood.D[j] / wood.P[j];
            if (wood.maker[j]) worldSlope += ES * wood.s0[j] * Math.pow(wood.p[j] / P0, ES) / wood.p[j];
        }
        System.out.printf("%nGood 0, drawn like sawn wood: %.0f%% of its price is freight at 10,000 km; world use %,.0f t a year%n",
                100 * wood.bulk, worldD);
        Integer[] order = new Integer[n];
        for (int j = 0; j < n; j++) order[j] = j;
        Arrays.sort(order, (x, y) -> Double.compare(net[y], net[x]));
        System.out.println("the city's best buyers before it sells (price there less the freight from Halifax):");
        for (int k = 0; k < 8; k++) {
            int j = order[k];
            System.out.printf("  %-26s price %6.2f  freight %5.2f  nets %6.2f  uses %,9.0f t%n",
                    nations.get(j).name(), wood.P[j], tc[j], net[j], wood.D[j]);
        }
        System.out.println("the city sells X a year: the month's step by the slopes (water-filling), then a full re-solve with the city's tonnes in:");
        for (double share : new double[] { 0.001, 0.01, 0.05, 0.10 }) {
            double X = share * worldD;
            double lambda = fill(X, net, kappa, order);
            double worldFall = X / worldSlope;
            // the re-solve: the city's tonnes go where the fill sent them, and the buyers' demand for everyone else falls by as much
            double[] in = new double[n]; int served = 0;
            StringBuilder where = new StringBuilder();
            Integer[] byTonnes = new Integer[n];
            for (int j = 0; j < n; j++) { in[j] = Math.max(0, kappa[j] * (net[j] - lambda)); if (in[j] > 0) served++; byTonnes[j] = j; }
            Arrays.sort(byTonnes, (x, y) -> Double.compare(in[y], in[x]));
            for (int k = 0; k < Math.min(4, served); k++)
                where.append(String.format("%s %.0f%%  ", short3(nations.get(byTonnes[k]).name()), 100 * in[byTonnes[k]] / X));
            Good re = copy(wood);
            double[] keep = re.d0.clone();
            double resolved = resolveWithCity(re, in, tc);
            re.d0 = keep;
            double est = lambda - worldFall;
            System.out.printf("  X = %4.1f%% of world use: nets %6.2f this month (%d buyers: %s) -> %6.2f with the world's slope; re-solved %6.2f (off %.1f%%)%n",
                    100 * share, lambda, served, where.toString().trim(), est, resolved, 100 * (est / resolved - 1));
        }

        // ---- for the mock-up: the export curve, and the buyers at 5% ----
        System.out.println("the export curve (share of world use, the netback this month, with the world's slope):");
        StringBuilder curve = new StringBuilder();
        for (int k = 0; k <= 20; k++) {
            double share = 0.0075 * k, X = share * worldD;
            double lam = X == 0 ? net[order[0]] : fill(X, net, kappa, order);
            curve.append(String.format("%.4f:%.2f:%.2f ", share, lam, lam - X / worldSlope));
        }
        System.out.println("  " + curve.toString().trim());
        System.out.println("where each buyer joins (the city's sales a year at which the fill reaches it, as a share of world use; its slope, t a $):");
        double cum = 0;
        for (int k = 0; k < 60; k++) {
            int j = order[k];
            double joins = 0;
            for (int m = 0; m < k; m++) joins += kappa[order[m]] * (net[order[m]] - net[j]);
            System.out.printf("  %-26s joins at %.4f%%  nets %6.2f  slope %,.0f%n", nations.get(j).name(), 100 * joins / worldD, net[j], kappa[j]);
        }
        StringBuilder small = new StringBuilder();
        for (int k = 0; k <= 20; k++) {
            double share = 0.001 * k, X = share * worldD;
            double lam = X == 0 ? net[order[0]] : fill(X, net, kappa, order);
            small.append(String.format("%.3f:%.2f:%.2f ", share, lam, lam - X / worldSlope));
        }
        System.out.println("the curve to 2% of world use: " + small.toString().trim());
        double X5 = 0.05 * worldD, lam5 = fill(X5, net, kappa, order), fall5 = X5 / worldSlope;
        System.out.println("the buyers at 5% of world use (tonnes, price before, freight, nets after the month's step):");
        Integer[] byT = new Integer[n];
        double[] in5 = new double[n];
        for (int j = 0; j < n; j++) { in5[j] = Math.max(0, kappa[j] * (net[j] - lam5)); byT[j] = j; }
        Arrays.sort(byT, (x, y) -> Double.compare(in5[y], in5[x]));
        for (int k = 0; k < 8; k++) {
            int j = byT[k];
            System.out.printf("  %-26s %,9.0f t  price %6.2f  freight %5.2f  nets %6.2f  (%,.0f km)%n", nations.get(j).name(), in5[j],
                    wood.P[j], tc[j], wood.P[j] - in5[j] / kappa[j] - tc[j] - fall5, DETOUR * gc(city, nations.get(j)));
        }

        // ---- the month's step, timed ----
        long t0 = System.nanoTime();
        double sink = 0;
        for (int rep = 0; rep < 1000; rep++) for (int gi = 0; gi < GOODS; gi++) sink += fill(0.01 * worldD, net, kappa, order);
        System.out.printf("%nthe city's month for all %d goods (sorted once a January): %.1f microseconds (%s)%n",
                GOODS, (System.nanoTime() - t0) / 1e3 / 1000, sink > 0 ? "ok" : "?");
    }

    /** Solves one good to a relative clearing error under tol; returns the sweeps it took. Starts from g.p. */
    static int solve(Good g, double tol, int max) {
        double[] u = new double[n];
        for (int sweep = 1; sweep <= max; sweep++) {
            for (int i = 0; i < n; i++) if (g.maker[i]) u[i] = Math.exp(-(g.p[i] - P0) / THETA);
            for (int j = 0; j < n; j++) {
                double s = 0;
                for (int k = g.byBuyerStart[j]; k < g.byBuyerStart[j + 1]; k++) s += u[g.byBuyerSeller[k]] * g.byBuyerK[k];
                g.phi[j] = s;
                g.P[j] = P0 - THETA * Math.log(s);
                g.D[j] = g.d0[j] * Math.pow(g.P[j] / P0, -ED);
            }
            double err = 0;
            for (int i = 0; i < n; i++) {
                if (!g.maker[i]) continue;
                double psi = 0;
                for (int k = g.bySellerStart[i]; k < g.bySellerStart[i + 1]; k++) {
                    int j = g.bySellerBuyer[k];
                    psi += g.bySellerK[k] * g.D[j] / g.phi[j];
                }
                double supply = g.s0[i] * Math.pow(g.p[i] / P0, ES);
                double sales = u[i] * psi;
                err = Math.max(err, Math.abs(sales / supply - 1));
                double target = P0 + THETA * (Math.log(psi) - Math.log(supply));
                g.p[i] = Math.max(1, g.p[i] + OMEGA * (target - g.p[i]));
            }
            if (err < tol) return sweep;
        }
        return max;
    }

    /** The city's netback for X tonnes, filling the best-netting buyers first, each one's price falling by its slope. */
    static double fill(double X, double[] net, double[] kappa, Integer[] order) {
        double sk = 0, skn = 0;
        for (int m = 0; m < order.length; m++) {
            int j = order[m];
            sk += kappa[j]; skn += kappa[j] * net[j];
            double lambda = (skn - X) / sk;
            double next = m + 1 < order.length ? net[order[m + 1]] : Double.NEGATIVE_INFINITY;
            if (lambda >= next) return lambda;
        }
        return (skn - X) / sk;
    }

    /** Re-solves with the city's tonnes delivered (each buyer's demand for everyone else falls by them), and returns the city's netback then: the best buyer's price less its freight, where it still sells. */
    static double resolveWithCity(Good g, double[] in, double[] tc) {
        double[] base = g.d0.clone();
        // demand for others = D(P) - in: approximated by lowering the reference demand by in at the current price, iterated
        for (int it = 0; it < 30; it++) {
            for (int j = 0; j < n; j++) {
                double atP = base[j] * Math.pow(g.P[j] / P0, -ED);
                g.d0[j] = Math.max(1e-9, (atP - in[j]) / Math.pow(g.P[j] / P0, -ED));
            }
            solve(g, 1e-9, 5000);
        }
        double best = Double.NEGATIVE_INFINITY, served = 0, sum = 0;
        for (int j = 0; j < n; j++) if (in[j] > 0) { sum += in[j] * (g.P[j] - tc[j]); served += in[j]; }
        for (int j = 0; j < n; j++) best = Math.max(best, g.P[j] - tc[j]);
        return served > 0 ? sum / served : best;
    }

    static Good makeGood(int gi, Random r) {
        Good g = new Good();
        g.bulk = gi == 0 ? 0.30 : 0.03 + 0.32 * r.nextDouble();
        g.s0 = new double[n]; g.d0 = new double[n]; g.maker = new boolean[n];
        double sd = 0, ss = 0;
        for (int j = 0; j < n; j++) { g.d0[j] = Math.pow(nations.get(j).gdp(), 0.9); sd += g.d0[j]; }
        if (gi == 0) {
            double small = 0;
            for (int i = 0; i < n; i++) if (!WOOD.containsKey(nations.get(i).name())) small += Math.pow(nations.get(i).gdp(), 0.7);
            for (int i = 0; i < n; i++) {
                String nm = nations.get(i).name();
                g.maker[i] = true;
                g.s0[i] = WOOD.containsKey(nm) ? WOOD.get(nm) : .15 * Math.pow(nations.get(i).gdp(), 0.7) / small;
                ss += g.s0[i];
            }
        } else for (int i = 0; i < n; i++) {
            boolean makes = r.nextDouble() < 0.4;
            g.maker[i] = makes;
            if (makes) { g.s0[i] = Math.pow(nations.get(i).gdp(), 0.7) * Math.exp(1.2 * r.nextGaussian()); ss += g.s0[i]; }
        }
        for (int j = 0; j < n; j++) { g.d0[j] *= 1e7 / sd; g.s0[j] *= 1e7 / ss; }
        g.p = new double[n]; g.phi = new double[n]; g.P = new double[n]; g.D = new double[n];
        Arrays.fill(g.p, P0);
        return g;
    }

    static double freight(Good g, double kmRoute) { return g.bulk * P0 * kmRoute / 10000.0; }

    /** Lays the allowed pairs: a pair is allowed when either end is in the top 20 by GDP, or the seller is one of the good's top 10 net exporters, or either is among the other's 10 nearest; or every pair when full. */
    static void wire(Good g, int[][] nearest, boolean full) {
        Integer[] ex = new Integer[n];
        for (int i = 0; i < n; i++) ex[i] = i;
        Arrays.sort(ex, (x, y) -> Double.compare(g.s0[y] - g.d0[y], g.s0[x] - g.d0[x]));
        boolean[] topEx = new boolean[n];
        for (int k = 0; k < EXPORTERS; k++) topEx[ex[k]] = true;
        boolean[][] near = new boolean[n][n];
        if (RULE.equals("nations")) for (int i = 0; i < n; i++) for (int k : nearest[i]) { near[i][k] = true; near[k][i] = true; }
        else for (int j = 0; j < n; j++) {
            final int jj = j;
            Integer[] o = new Integer[n];
            for (int i = 0; i < n; i++) o[i] = i;
            Arrays.sort(o, Comparator.comparingDouble(i -> km[jj][i]));
            int got = 0;
            for (int k = 0; k < n && got < NEAREST; k++) if (o[k] != j && g.maker[o[k]]) { near[o[k]][j] = true; near[j][o[k]] = true; got++; }
        }
        List<int[]> pairs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (!g.maker[i]) continue;
            for (int j = 0; j < n; j++) {
                boolean ok = full || i == j || i < TOP || j < TOP || topEx[i] || near[i][j];
                if (ok) pairs.add(new int[] { i, j });
            }
        }
        g.pairs = pairs.size();
        g.byBuyerStart = new int[n + 1]; g.bySellerStart = new int[n + 1];
        for (int[] pr : pairs) { g.bySellerStart[pr[0] + 1]++; g.byBuyerStart[pr[1] + 1]++; }
        for (int k = 0; k < n; k++) { g.bySellerStart[k + 1] += g.bySellerStart[k]; g.byBuyerStart[k + 1] += g.byBuyerStart[k]; }
        g.byBuyerSeller = new int[pairs.size()]; g.byBuyerK = new double[pairs.size()];
        g.bySellerBuyer = new int[pairs.size()]; g.bySellerK = new double[pairs.size()];
        int[] bf = Arrays.copyOf(g.byBuyerStart, n), sf = Arrays.copyOf(g.bySellerStart, n);
        for (int[] pr : pairs) {
            int i = pr[0], j = pr[1];
            double t = freight(g, km[i][j]) + (i == j ? 0 : HANDLING);
            double k = Math.exp(-t / THETA);
            g.byBuyerSeller[bf[j]] = i; g.byBuyerK[bf[j]++] = k;
            g.bySellerBuyer[sf[i]] = j; g.bySellerK[sf[i]++] = k;
        }
    }

    static Good copy(Good s) {
        Good g = new Good();
        g.bulk = s.bulk; g.s0 = s.s0.clone(); g.d0 = s.d0.clone(); g.maker = s.maker;
        g.byBuyerStart = s.byBuyerStart; g.byBuyerSeller = s.byBuyerSeller; g.byBuyerK = s.byBuyerK;
        g.bySellerStart = s.bySellerStart; g.bySellerBuyer = s.bySellerBuyer; g.bySellerK = s.bySellerK;
        g.p = s.p.clone(); g.phi = s.phi.clone(); g.P = s.P.clone(); g.D = s.D.clone(); g.pairs = s.pairs;
        return g;
    }

    static String short3(String s) {
        return switch (s) { case "United States of America" -> "US"; case "United Kingdom" -> "UK"; default -> s; };
    }

    static double gc(Nation a, Nation b) {
        double la1 = Math.toRadians(a.lat()), la2 = Math.toRadians(b.lat());
        double dl = Math.toRadians(b.lon() - a.lon());
        double h = Math.pow(Math.sin((la2 - la1) / 2), 2) + Math.cos(la1) * Math.cos(la2) * Math.pow(Math.sin(dl / 2), 2);
        return 2 * 6371 * Math.asin(Math.min(1, Math.sqrt(h)));
    }

    static List<Nation> load(Path tsv) throws Exception {
        List<Nation> all = new ArrayList<>();
        List<String> lines = Files.readAllLines(tsv);
        for (String line : lines.subList(1, lines.size())) {
            String[] f = line.split("\t");
            if (SKIP.contains(f[0])) continue;
            double gdp = GDP.getOrDefault(f[0], Long.parseLong(f[6]) * 2500 / 1e9);
            all.add(new Nation(f[0], Double.parseDouble(f[3]), Double.parseDouble(f[4]), Math.max(0.2, gdp)));
        }
        all.sort((x, y) -> Double.compare(y.gdp(), x.gdp()));
        return new ArrayList<>(all.subList(0, Math.min(NATIONS, all.size())));
    }
}
