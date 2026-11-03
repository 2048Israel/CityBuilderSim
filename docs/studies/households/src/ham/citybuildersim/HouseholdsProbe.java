package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Scratch probe (not in the tree; docs/studies/households): the household ledgers of a grown city, for the
 * households study. Changes no game code and writes no numbered save slot (the city is on GameFiles.scratch()).
 *
 * The city is exactly UiProbe's (LongPlaytest.probeCity() and probePlay(), copied here so that every month is a
 * step this probe can hook and time): same founding, same first builds, same loop. Modes:
 *   main  [outfile]            checkpoints 21..4000, the founders' grant at month 21, the flows at 600 and 2400
 *   jfr   [outfile] [jfrfile]  the same city to month 2300, a flight recording to 2400, and its reading
 *   check N                    the city to month N under this probe's loop, three figures, for a diff
 *   theirs N                   the same under LongPlaytest.probeCity()/probePlay(), for that diff
 * More modes: small H (the claim in a founding of H houses, to stdout), time 300,600,.. (exact inclusive time per method over 100-month windows; needs the agent, below),
 * jfr (a flight recording from month 2300 for -Djfr.span=N months, default 100; -Djfr.period=ms to override the 10 ms profile period; -XX:FlightRecorderOptions=stackdepth=512).
 * Every mode plays the city in one playTo(g, 4000) and acts through the month hook, so the city is the same in all of them (the loop's looks depend on the target month).
 *
 * Build and run, from the worktree root, with C = target/classes;$(cat target/classpath.txt) after ./batch-check.sh --fast has compiled and listed the jars; OUT = a scratch folder:
 *   javac --add-exports java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED --add-exports java.base/jdk.internal.org.objectweb.asm.commons=ALL-UNNAMED -d OUT/agent docs/studies/households/src/ham/citybuildersim/Hh*.java
 *       (HhTimer + HhAgent; then jar them with Premain-Class: ham.citybuildersim.HhAgent in the manifest as OUT/hhagent.jar)
 *   javac -cp "OUT/agent;$C" -d OUT docs/studies/households/src/ham/citybuildersim/HouseholdsProbe.java
 *   java -cp "OUT;OUT/agent;$C" ham.citybuildersim.HouseholdsProbe main probe-main.txt        (writes docs/studies/households/out/probe-main.txt)
 *   java --add-exports (the two above) "-javaagent:OUT/hhagent.jar=Game.simulateMonths,HouseholdBalance.advanceMonth,..." -cp "OUT;OUT/agent;$C" ham.citybuildersim.HouseholdsProbe time 300,600,1200,2300,3900 probe-time.txt
 *
 * Units: the model's money is in thousands of dollars ($k); every money figure printed is $k, and per MONTH where
 * it is a flow (take-home, dividends), per household unless a line says total.
 */
public final class HouseholdsProbe {

    static final PrintStream QUIET = new PrintStream(new OutputStream() {
        @Override public void write(int b) { }
        @Override public void write(byte[] b, int off, int len) { }
    });

    static PrintStream P;
    static Game G;
    static HouseholdBalance HB;
    static Exchange EX;
    static Equity EQ;
    static final int NC = Equity.COMPANIES.length;
    static final long[] NANOS = new long[20_000];
    static int stop;

    static final int[] CHECKS = {21, 60, 120, 240, 600, 1200, 2400, 4000};
    static final int[] WINDOW_AT = {600, 2400};
    static int nextCheck = 0;
    static int windowLeft = 0;
    static boolean done = false;
    static int hookUntil = Integer.MAX_VALUE;     // jfr mode: no checkpoints at all
    static boolean quietChecks = false;
    static final int PERIOD_MS = Integer.getInteger("jfr.period", 0);
    static final int SPAN = Integer.getInteger("jfr.span", 100);

    static final String[] ROWN = {"Unskilled", "Skilled", "College", "Professional", "Sr professional", "Elite",
            "RETIRED", "Unemployed", "Student", "Orphan", "Prison"};

    /* ============================ the city, as UiProbe makes it ============================ */

    static Game buildCity(GameFiles files) {
        LongPlaytest.resetTheFloat();
        Game g = new Game(files, LongPlaytest.founding());
        bind(g);
        g.run();
        LongPlaytest.childcareWhenNeeded(g);
        g.getDebtManager().setAutopilot(LongPlaytest.AUTOPILOT);
        g.setRolloverMode(LongPlaytest.ROLLOVER);
        g.getCityMap();
        g.setRescueMode(LongPlaytest.RESCUE_AUTO ? TreasuryFund.RescueMode.AUTOMATIC : TreasuryFund.RescueMode.BUTTON);
        g.setFundDial(LongPlaytest.FUND_DIAL);
        LongPlaytest.seedMarket(g, 0);
        LongPlaytest.villageBuild(g, "House", 40);
        LongPlaytest.villageBuild(g, "Convenience Store", 3);
        LongPlaytest.villageBuild(g, "Mixed Farm", 2);
        runMonths(g, 3);
        LongPlaytest.villageBuild(g, "House", 20);
        runMonths(g, 4);
        LongPlaytest.villageBuild(g, "Convenience Store", 2);
        LongPlaytest.villageBuild(g, "Construction Depot", 1);
        runMonths(g, 5);
        LongPlaytest.advise(g);
        runMonths(g, 6);
        LongPlaytest.ensureSchools(g);
        return g;
    }

    /** LongPlaytest.probePlay(), month by month through step(). */
    static void playTo(Game g, int toMonth) {
        while (g.getMonth() < toMonth && !done) {
            stop++;
            int skip = switch (stop % 6) {
                case 0 -> 100; case 1 -> 12; case 2 -> 24; case 3 -> 60; case 4 -> 6; default -> 120;
            };
            skip = Math.min(skip, LongPlaytest.longestSkip());
            runMonths(g, Math.min(skip, Math.max(1, toMonth - g.getMonth())));
            LongPlaytest.ensureSchools(g);
            LongPlaytest.childcareWhenNeeded(g);
            for (int move = 0; move < LongPlaytest.movesPerLook(); move++) {
                if (LongPlaytest.advise(g) == null) break;
                runMonths(g, 1);
            }
            runMonths(g, 2);
        }
    }

    static void runMonths(Game g, int months) {
        for (int i = 0; i < months && !done; i++) step(g);
    }

    static void step(Game g) {
        int m = g.getMonth();
        Snap before = windowLeft > 0 ? snap(null) : null;
        CellState cb = windowLeft > 0 ? cellState(before.p) : null;
        long t0 = System.nanoTime();
        LongPlaytest.run(g, 1);
        if (m >= 0 && m < NANOS.length) NANOS[m] = System.nanoTime() - t0;
        if (before != null) { windowRecord(before, snap(before.p), cb); windowLeft--; if (windowLeft == 0) windowSummary(); }
        afterMonth(g);
    }

    static double meanMs(int from, int to) {
        long sum = 0; int n = 0;
        for (int m = Math.max(0, from); m < to && m < NANOS.length; m++) if (NANOS[m] > 0) { sum += NANOS[m]; n++; }
        return n == 0 ? Double.NaN : sum / 1e6 / n;
    }

    /* ============================ the hook, once a month ============================ */

    static void afterMonth(Game g) {
        int m = g.getMonth();
        if (m <= 40) trackFounders(m);
        if (HOOK != null) HOOK.accept(m);
        if (quietChecks) return;
        if (nextCheck < CHECKS.length && m == CHECKS[nextCheck]) {
            checkpoint(m);
            nextCheck++;
            for (int w : WINDOW_AT) if (w == m) windowStart(m);
            if (m == 2400 && meanMs(m - 20, m) > 150) { lateStop = m + 12; P.println("NOTE: a month at 2400 averaged over 150 ms; the run stops after the window at " + lateStop + " and 4000 is dropped."); }
        }
        if (lateStop > 0 && m >= lateStop && windowLeft == 0) done = true;
    }

    static int lateStop = -1;
    static int winIndex = 0;

    /* ============================ the founders, month by month ============================ */

    static double[] prevShares = new double[NC], prevSf = new double[NC], foundMoney = new double[NC], foundU = new double[NC],
            subHome = new double[NC], subAbroad = new double[NC], cancelled = new double[NC], unexplained = new double[NC];
    static final List<double[]> CELLTRAIL = new ArrayList<>();

    static void bind(Game g) {
        G = g; HB = g.getHouseholdBalance(); EX = g.getExchange(); EQ = g.getEquity();
        for (int c = 0; c < NC; c++) { prevShares[c] = EQ.getShares(c); prevSf[c] = EX.getSplitFactor(c); }
    }

    /**
     * Every count here is in FOUNDING UNITS: a share as it was when the company was listed, before any split (Exchange.splitFactor is cumulative
     * and multiplies every holder's count; price x splitFactor is the price of a founding unit). Equity.offered is MONEY, not shares: the shares
     * an offering issued are (raisedHome + raisedAbroad) / price, and the month's change in shares in issue, less the split, is those plus the founders.
     * In a founding month with no offering the change IS the founders' shares (exact); with one, the founders are money founded / foundingPrice
     * and the offering is the rest. Offering shares are divided home/abroad by the money each side put in (one price). A fall is a buyback cancelled.
     */
    static void trackFounders(int m) {
        for (int c = 0; c < NC; c++) {
            double f = EQ.getFoundedThisMonth(c), fp = EQ.foundingPrice(), sh = EQ.getShares(c), sf = EX.getSplitFactor(c);
            double rh = EQ.getRaisedHomeThisMonth(c), ra = EQ.getRaisedAbroadThisMonth(c);
            double dU = (sh - prevShares[c] * (sf / prevSf[c])) / sf;
            prevShares[c] = sh; prevSf[c] = sf;
            double offU = dU;
            if (f > 0) {
                foundMoney[c] += f;
                double fU = rh + ra > 0 ? f / fp : dU;
                foundU[c] += fU;
                offU = dU - fU;
            }
            if (offU < -1e-9) cancelled[c] -= offU;
            else if (rh + ra > 0) { subHome[c] += offU * rh / (rh + ra); subAbroad[c] += offU * ra / (rh + ra); }
            else if (offU > 1e-9) unexplained[c] += offU;
        }
        Household cell = cellByKey("COUPLE:SKILLED");
        if (cell != null) {
            Agg a = new Agg(); Prices p = prices(); a.add(cell, p);
            CELLTRAIL.add(new double[]{m, G.getCohorts().total(), cell.households(), cell.people(), a.per(a.take),
                    a.per(a.sav), a.per(a.shr), a.per(a.div)});
        }
    }

    /* ============================ aggregates ============================ */

    static double fld(Object o, String name) {
        try {
            Field f = o.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.getDouble(o);
        } catch (Exception e) { return Double.NaN; }
    }

    static final class Prices {
        double[] px = new double[NC];
        double usd, paper, bond;
    }

    static Prices prices() {
        Prices p = new Prices();
        for (int c = 0; c < NC; c++) p.px[c] = EX.price(c);
        p.usd = fld(HB, "localPerUsd"); p.paper = fld(HB, "paperRatio"); p.bond = fld(HB, "bondRatio");
        if (!(p.usd > 0)) p.usd = 1;
        if (!(p.paper > 0)) p.paper = 1;
        if (!(p.bond > 0)) p.bond = 1;
        return p;
    }

    /** A set of cells, summed with the households as weights. All the totals; per() divides by households. */
    static final class Agg {
        double h, ups, ppl, take, sav, debt, shr, abr, stu, pb, div;
        void add(Household c, Prices p) {
            double w = c.households();
            if (w <= 0) return;
            h += w; ups += w * c.grownUps(); ppl += c.people();
            take += w * c.disposable(); sav += w * c.savings(); debt += w * c.debt();
            double s = 0;
            for (int k = 0; k < NC; k++) s += c.shares(k) * p.px[k];
            shr += w * s;
            abr += w * c.abroad() * p.usd; stu += w * c.studentDebt();
            pb += w * (c.paper() * p.paper + c.bonds() * p.bond);
            div += w * c.dividends();
        }
        void add(Agg o) {
            h += o.h; ups += o.ups; ppl += o.ppl; take += o.take; sav += o.sav; debt += o.debt; shr += o.shr;
            abr += o.abr; stu += o.stu; pb += o.pb; div += o.div;
        }
        double per(double t) { return h > 0 ? t / h : 0; }
        double nwSv() { return sav - debt; }
        double nwFull() { return sav + shr + abr + pb - debt - stu; }
    }

    static final class Snap {
        Agg[] row = new Agg[Household.ROWS];
        Agg work = new Agg(), all = new Agg();
        Prices p;
    }

    static Snap snap(Prices fixed) {
        Snap s = new Snap();
        s.p = fixed != null ? fixed : prices();
        for (int r = 0; r < Household.ROWS; r++) s.row[r] = new Agg();
        for (Household c : HB.cells()) {
            s.row[c.row()].add(c, s.p);
        }
        for (int r = 0; r < PayTier.values().length; r++) s.work.add(s.row[r]);
        for (int r = 0; r < Household.ROWS; r++) s.all.add(s.row[r]);
        return s;
    }

    static Household cellByKey(String key) {
        for (Household c : HB.cells()) if (key.equals(c.key())) return c;
        return null;
    }

    /* ============================ the checkpoint ============================ */

    static final List<double[]> RATIOS = new ArrayList<>();

    static void line(Agg a, String name) {
        P.printf("%-16s %9.1f %8.1f %9.1f %8.3f %9.3f %8.3f %9.3f %8.3f %8.3f %8.3f %10.3f %8.3f %7.2f%n",
                name, a.h, a.ups, a.ppl, a.per(a.take), a.per(a.sav), a.per(a.debt), a.per(a.shr), a.per(a.abr),
                a.per(a.stu), a.per(a.pb), a.per(a.nwFull()), a.per(a.div),
                a.take > 0 ? a.per(a.nwFull()) / (12 * a.per(a.take)) : 0.0);
    }

    static void header() {
        P.printf("%-16s %9s %8s %9s %8s %9s %8s %9s %8s %8s %8s %10s %8s %7s%n",
                "row", "hh", "grownUps", "people", "takeHome", "savings", "debt", "shares", "abroad", "stuDebt",
                "paper+bd", "netWorth", "dividend", "NW/yrTk");
    }

    static void checkpoint(int m) {
        Snap s = snap(null);
        PopulationCohorts pc = G.getCohorts();
        double meanMs = meanMs(m - 20, m);
        double[] ms20 = new double[20]; int k20 = 0;
        for (int q = Math.max(0, m - 20); q < m; q++) ms20[k20++] = NANOS[q] / 1e6;
        double[] srt = java.util.Arrays.copyOf(ms20, k20); java.util.Arrays.sort(srt);
        double med = k20 > 0 ? srt[k20 / 2] : Double.NaN, mx = k20 > 0 ? srt[k20 - 1] : Double.NaN;
        P.println();
        P.printf("=== MONTH %d  population %.0f   mean run(g,1) over months %d..%d: %.1f ms (median %.1f, max %.1f; mean of the last 100: %.1f)%s ===%n", m, pc.total(), m - 20, m - 1, meanMs, med, mx, meanMs(m - 100, m),
                m <= 21 ? " (includes the first-months JIT warm-up)" : "");
        StringBuilder sb = new StringBuilder("pyramid:");
        for (AgeBand b : AgeBand.values()) sb.append(String.format(" %s %.0f", b.name().charAt(0) + b.name().substring(1, 3).toLowerCase(), pc.get(b)));
        sb.append(String.format("   | last month: births %.1f deaths %.1f promoted ADULT->SENIOR %.1f",
                pc.getLastBirths(), pc.getLastDeaths(), pc.getPromoted(AgeBand.ADULT)));
        P.println(sb);
        if (m == 21) P.println("units: $k; hh, grownUps, people are TOTALS of the row; every other column is per household of the row (takeHome, dividend per MONTH); netWorth = savings+shares+abroad+paper+bonds-debt-stuDebt; NW/yrTk = netWorth / (12 x takeHome)");
        header();
        for (int r = 0; r < Household.ROWS; r++) if (s.row[r].h > 0) line(s.row[r], ROWN[r]);
        line(s.work, "WORKING (6 tiers)");
        line(s.all, "ALL");
        Agg R = s.row[Household.RETIRED_ROW], W = s.work;
        // the single cell the brief names, and each retired cell
        Household cs = cellByKey("COUPLE:SKILLED");
        if (cs != null) { Agg a = new Agg(); a.add(cs, s.p); line(a, "cell COUPLE:SKILLED"); }
        for (Household c : HB.cells()) if (c.row() == Household.RETIRED_ROW && c.households() > 0) { Agg a = new Agg(); a.add(c, s.p); line(a, "cell " + c.key()); }
        double rw = W.per(W.nwFull()) != 0 ? R.per(R.nwFull()) / W.per(W.nwFull()) : Double.NaN;
        double rwSv = W.per(W.nwSv()) != 0 ? R.per(R.nwSv()) / W.per(W.nwSv()) : Double.NaN;
        double rwGa = W.ups > 0 && R.ups > 0 ? (R.nwFull() / R.ups) / (W.nwFull() / W.ups) : Double.NaN;
        P.printf("retired / working per household: netWorth %.3f  (savings-debt only %.3f)  per grown-up %.3f   | NW per yr of take-home: retired %.2f working %.2f%n",
                rw, rwSv, rwGa, R.per(R.nwFull()) / (12 * R.per(R.take)), W.per(W.nwFull()) / (12 * W.per(W.take)));
        RATIOS.add(new double[]{m, pc.total(), R.h, W.h, R.per(R.nwFull()), W.per(W.nwFull()), rw, rwSv, rwGa,
                R.per(R.nwFull()) / (12 * R.per(R.take)), W.per(W.nwFull()) / (12 * W.per(W.take)), meanMs});
        if (m == 21) claimSection(s);
        if (m == 240 || m == 600 || m == 1200 || m == 2400 || m == 4000) costCounts();
    }

    /* ============================ item 2: the month-21 claim and the founders ============================ */

    static void claimSection(Snap s) {
        P.println();
        P.println("--- the claim: \"with 55 people, a skilled couple taking home $281 a month held $563k of shares and got $1,344 a month in dividends\" ---");
        Household cs = cellByKey("COUPLE:SKILLED");
        PopulationCohorts pc = G.getCohorts();
        P.printf("city population at month 21: %.1f people; households (all rows) %.1f%n", pc.total(), s.all.h);
        if (cs == null) { P.println("no COUPLE:SKILLED cell"); }
        else {
            Agg a = new Agg(); a.add(cs, s.p);
            P.printf("cell COUPLE:SKILLED: households %.2f, people %.2f (%.1f each), take-home per hh %.3f $k/mo, savings %.3f, debt %.3f, shares (mkt) %.3f, abroad %.3f, dividends %.3f $k/mo per hh%n",
                    cs.households(), cs.people(), cs.headcount(), a.per(a.take), a.per(a.sav), a.per(a.debt), a.per(a.shr), a.per(a.abr), a.per(a.div));
            P.printf("  as the claim phrases it: take-home $%.0f/mo, shares $%.0fk, dividends $%.0f/mo   (claim: $281, $563k, $1,344; unit $k so 0.281 = $281)%n",
                    a.per(a.take) * 1000, a.per(a.shr), a.per(a.div) * 1000);
            P.printf("  the whole cell (all %.2f households together): shares %.1f $k, dividends %.3f $k/mo, take-home %.3f $k/mo%n", cs.households(), a.shr, a.div, a.take);
            double ratio = a.per(a.take) > 0 ? a.per(a.div) / a.per(a.take) : Double.NaN;
            P.printf("  dividends / take-home = %.2f ; dividend yield on its shares = %.2f%% a month%n", ratio, a.per(a.shr) > 0 ? 100 * a.per(a.div) / a.per(a.shr) : Double.NaN);
        }
        P.println("cell COUPLE:SKILLED month by month (per household; $k):  month  city pop  cell hh  cell people  takeHome  savings  shares(mkt)  dividend/mo");
        for (double[] t : CELLTRAIL) {
            int m = (int) t[0];
            if (m == 6 || m == 12 || m == 18 || m == 21) P.printf("  %5d %9.1f %8.2f %10.2f %9.3f %8.3f %11.3f %10.3f%n", m, t[1], t[2], t[3], t[4], t[5], t[6], t[7]);
        }
        // the households' shares against the households' savings
        double hhShares = s.all.shr, hhSav = s.all.sav;
        P.printf("all households at month 21: shares at market %.1f $k, savings %.1f $k, shares / savings = %.2f; EX.marketValueOfShares() = %.1f%n",
                hhShares, hhSav, hhSav > 0 ? hhShares / hhSav : Double.NaN, HB.marketValueOfShares());
        // the founders' grant
        P.println("--- founders' grant vs subscriptions, tracked month by month from the register (foundedThisMonth, shares in issue, money raised) ---");
        P.println("counts are in FOUNDING UNITS (a share before any split); 'unitPrice' = price x splitFactor, the price of one founding unit today; 'gain' = unitPrice / EQ.foundingPrice() (both in today's money)");
        P.println("founders = the grant (EQ founded), subHome/subAbroad = shares the offerings issued, by who put the money in, cancelled = buybacks, unexpl = issued with no offering money behind it; hhHold/foreign = units held now");
        double fMV = 0, fHeldMV = 0, subMV = 0, hhMV = 0, issueMV = 0, abroadMV = 0, unexMV = 0, canMV = 0, subAbMV = 0;
        Integer[] idx = new Integer[NC];
        for (int c = 0; c < NC; c++) idx[c] = c;
        final double[] mv = new double[NC];
        for (int c = 0; c < NC; c++) mv[c] = HB.sharesHeld(c) * s.p.px[c];
        java.util.Arrays.sort(idx, (x, y) -> Double.compare(mv[y], mv[x]));
        P.printf("%-22s %8s %6s %9s %7s | %9s %9s %9s %9s %7s | %9s %9s %10s%n", "company (top 8 by hh value)", "price", "split", "unitPrice", "gain", "founders", "subHome", "subAbroad", "cancelled", "unexpl", "hhHold", "foreign", "hhValue");
        int shown = 0, listed = 0;
        double fp = EQ.foundingPrice();
        for (int i = 0; i < NC; i++) {
            int c = idx[i];
            double p = s.p.px[c], sf = EX.getSplitFactor(c), up = EX.pricePerFoundingShare(c);
            double heldU = HB.sharesHeld(c) / sf;
            if (EQ.getShares(c) > 0) listed++;
            fMV += foundU[c] * up; fHeldMV += Math.min(foundU[c], heldU) * up; subMV += subHome[c] * up; subAbMV += subAbroad[c] * up; canMV += cancelled[c] * up; unexMV += unexplained[c] * up;
            hhMV += HB.sharesHeld(c) * p; issueMV += EQ.getShares(c) * p; abroadMV += EQ.getForeignShares(c) * p;
            if (shown < 8 && EQ.getShares(c) > 0) {
                shown++;
                P.printf("%-22s %8.4f %6.1f %9.3f %7.1f | %9.1f %9.1f %9.1f %9.1f %7.1f | %9.1f %9.1f %10.1f%n", Equity.COMPANIES[c], p, sf, up, up / fp, foundU[c], subHome[c], subAbroad[c], cancelled[c], unexplained[c], heldU, EQ.getForeignShares(c) / sf, HB.sharesHeld(c) * p);
            }
        }
        P.printf("founding price today %.4f; companies listed %d of %d. At today's prices, summed over all companies ($k):%n", fp, listed, NC);
        P.printf("  all shares in issue %.1f | held by households %.1f | held abroad %.1f%n", issueMV, hhMV, abroadMV);
        P.printf("  every founders' unit ever granted %.1f = %.0f%% of everything in issue; of the households' %.1f at most %.1f (%.0f%%) can still be a founder's grant (sum over companies of min(founders granted, held))%n",
                fMV, issueMV > 0 ? 100 * fMV / issueMV : 0, hhMV, fHeldMV, hhMV > 0 ? 100 * fHeldMV / hhMV : 0);
        P.printf("  issued at the offerings: to households %.1f, to the world %.1f; cancelled by buybacks %.1f; issued without offering money %.3f%n", subMV, subAbMV, canMV, unexMV);
        double sumU = 0, mon = 0;
        for (int c = 0; c < NC; c++) { sumU += foundU[c]; mon += foundMoney[c]; }
        P.printf("  founders' units, all companies: %.1f ; founders' book (money, $k) %.1f = EQ.getFoundersBook sum %.1f%n", sumU, mon, sumFoundersBook());
        int top = idx[0];
        P.printf("  its quote: %s; fair value per share %.4f (EX.fair), price %.4f; regime %s%n", EX.quoteWords(top), EX.fair(top), s.p.px[top], EQ.getRegime(top));
        P.printf("  the largest holding, %s: %.0f founding units issued against a book of %.1f $k; a unit is worth %.3f today against %.4f at the founding (%.0fx); households hold %.1f units = %.1f $k = %.0f%% of all their shares%n",
                Equity.COMPANIES[top], foundU[top], foundMoney[top], EX.pricePerFoundingShare(top), fp, EX.pricePerFoundingShare(top) / fp, HB.sharesHeld(top) / EX.getSplitFactor(top),
                HB.sharesHeld(top) * s.p.px[top], hhMV > 0 ? 100 * HB.sharesHeld(top) * s.p.px[top] / hhMV : 0);
    }

    static double sumFoundersBook() {
        double t = 0;
        for (int c = 0; c < NC; c++) if (c != Equity.BANK) t += EQ.getFoundersBook(c);
        return t;
    }

    /* ============================ item 3: the flows ============================ */

    /** The cells' counts and per-household stocks at one moment, in HB.cells() order. */
    static final class CellState {
        int n;
        double[] hh, sav, nw, gu;
        int[] grp, row;
    }

    /** nw = savings + shares + abroad + paper + bonds - debt (no student loan, which moves on its own); sav = savings - debt; both per household. */
    static CellState cellState(Prices p) {
        List<Household> cs = HB.cells();
        CellState s = new CellState();
        s.n = cs.size();
        s.hh = new double[s.n]; s.sav = new double[s.n]; s.nw = new double[s.n]; s.gu = new double[s.n];
        s.grp = new int[s.n]; s.row = new int[s.n];
        for (int i = 0; i < s.n; i++) {
            Household c = cs.get(i);
            double sh = 0;
            for (int k = 0; k < NC; k++) sh += c.shares(k) * p.px[k];
            s.hh[i] = c.households(); s.gu[i] = c.grownUps(); s.grp[i] = c.stockGroup(); s.row[i] = c.row();
            s.sav[i] = c.savings() - c.debt();
            s.nw[i] = c.savings() + sh + c.abroad() * p.usd + c.paper() * p.paper + c.bonds() * p.bond - c.debt();
        }
        return s;
    }

    /** What HouseholdBalance.moveStock() hands each cell that gained households: a line-for-line copy of its pooling, run on one stock. */
    static final class Move {
        double[] recvRow, recvCity, newcomers;
        double gone;
    }

    static Move replica(double[] before, double[] fresh, double[] stock, double[] gu, int[] grp) {
        int n = before.length, ROWS = Household.ROWS;
        double[] rowLoss = new double[ROWS], rowGain = new double[ROWS], rowPool = new double[ROWS], delta = new double[n];
        for (int i = 0; i < n; i++) {
            delta[i] = fresh[i] - before[i];
            double weight = Math.abs(delta[i]) * gu[i];
            if (delta[i] < 0) { rowLoss[grp[i]] += weight; rowPool[grp[i]] += stock[i] * -delta[i]; }
            else if (delta[i] > 0) rowGain[grp[i]] += weight;
        }
        double[] rowMoved = new double[ROWS];
        double cityPool = 0, cityLoss = 0, cityGain = 0;
        for (int r = 0; r < ROWS; r++) {
            double moved = Math.min(rowLoss[r], rowGain[r]);
            rowMoved[r] = moved;
            if (rowLoss[r] > 0) {
                double kept = Math.min(1, moved / rowLoss[r]);
                cityPool += rowPool[r] * (1 - kept);
                rowPool[r] *= kept;
                cityLoss += Math.max(0, rowLoss[r] - moved);
            }
            cityGain += Math.max(0, rowGain[r] - moved);
        }
        double cityMoved = Math.min(cityLoss, cityGain);
        double cityKept = cityLoss > 0 ? Math.min(1, cityMoved / cityLoss) : 0;
        Move mv = new Move();
        mv.gone = cityPool * (1 - cityKept);
        cityPool *= cityKept;
        double cityShare = cityGain > 0 ? Math.min(1, cityMoved / cityGain) : 0;
        mv.recvRow = new double[n]; mv.recvCity = new double[n]; mv.newcomers = new double[n];
        for (int i = 0; i < n; i++) {
            if (delta[i] <= 0 || gu[i] <= 0) continue;
            int r = grp[i];
            double weight = delta[i] * gu[i];
            double fromRow = rowGain[r] > 0 ? Math.min(weight, weight * rowMoved[r] / rowGain[r]) : 0;
            double rest = Math.max(0, weight - fromRow);
            double fromCity = Math.min(rest, rest * cityShare);
            mv.newcomers[i] = Math.max(0, rest - fromCity) / gu[i];
            mv.recvRow[i] = rowMoved[r] > 0 ? rowPool[r] * fromRow / rowMoved[r] : 0;
            mv.recvCity[i] = cityMoved > 0 ? cityPool * fromCity / cityMoved : 0;
        }
        return mv;
    }

    static double sumDH, sumDppl, sumDNW, sumProm, sumDeathsRet, wNWhh, rNWhh, wNWga, rNWga, sumTaken, sumShr, sumAbr, sumFold;
    static double sumDHcity, sumCityWorkNW, rPplPerHh;
    static int wN;
    // the transfer, summed over the window: retired cells' households gained / lost, what they received, by source
    static double tGain, tLost, tRecvRowNw, tRecvCityNw, tRecvRowSv, tRecvCitySv, tNew, tAvgRetNw, tAvgWorkNw, tAvgRetSv, tAvgWorkSv;
    static double tWGain, tWRecvNw, tWRecvSv;
    static final List<String> mrows = new ArrayList<>(), trows = new ArrayList<>();

    static boolean legend;

    static String n3(double v) { return Double.isNaN(v) || Double.isInfinite(v) ? "      -" : String.format("%7.1f", v); }

    static void windowStart(int m) {
        windowLeft = 12;
        sumDH = sumDppl = sumDNW = sumProm = sumDeathsRet = wNWhh = rNWhh = wNWga = rNWga = sumTaken = sumShr = sumAbr = sumFold = 0;
        sumDHcity = sumCityWorkNW = rPplPerHh = 0;
        tGain = tLost = tRecvRowNw = tRecvCityNw = tRecvRowSv = tRecvCitySv = tNew = tAvgRetNw = tAvgWorkNw = tAvgRetSv = tAvgWorkSv = 0;
        tWGain = tWRecvNw = tWRecvSv = 0;
        wN = 0;
        P.println();
        P.println("--- FLOWS, 12 consecutive months from month " + m + " (the playtest loop's own looks and builds go on between them); one row a month, $k ---");
        if (!legend) P.println("people: retire = promoted ADULT->SENIOR (turning 70, the model's retirement); D-x = deaths by band; arr/dep = Migration's last arrivals/departures; bk = of the departures, bankruptcies");
        if (!legend) P.println("left the city = HouseholdBalance's own last-month figures (savings; shares at the opening quote; dollars abroad); fold = cells folded. retired hh/NW = the RETIRED row's four cells, before -> after, NW at the OPENING prices (a quote moving is not money gained)");
        if (!legend) P.println("transfer = HouseholdBalance.moveStock()'s pooling copied and run on the month's opening stocks: households the retired cells gained / lost, and what each gained household was handed (NW; savings), against the retired and the working averages (NW/hh)");
        legend = true;
        P.printf("%5s %6s %5s %5s %5s %6s %6s %6s %6s %6s %5s | %9s %9s %7s %4s | %9s %9s %7s %9s | %7s %7s %8s %8s | %8s %8s %8s%n",
                "month", "retire", "D-bab", "D-chi", "D-tee", "D-adu", "D-sen", "D-eld", "births", "arr", "dep/bk", "savLeft", "shrLeft", "abrUSD", "fold",
                "retHH0", "retHH1", "dHH", "dNW", "gained", "lost", "recv/gHH", "recvSav", "retAvg", "workAvg", "workSav");
    }

    static void windowRecord(Snap b, Snap a, CellState cb) {
        PopulationCohorts pc = G.getCohorts();
        Migration mg = G.getMigration();
        int m = G.getMonth();
        // money: the retired row's before/after, both at the BEFORE prices so that a share price moving is not counted as money gained
        Agg R0 = b.row[Household.RETIRED_ROW], R1 = a.row[Household.RETIRED_ROW], W0 = b.work, W1 = a.work;
        double shrGone = 0;
        for (int k = 0; k < NC; k++) shrGone += HB.getSharesTakenAway(k) * b.p.px[k];
        double dH = R1.h - R0.h, dP = R1.ppl - R0.ppl, dNW = R1.nwFull() - R0.nwFull();
        double dNWw = W1.nwFull() - W0.nwFull();
        sumDH += dH; sumDppl += dP; sumDNW += dNW; sumProm += pc.getPromoted(AgeBand.ADULT);
        sumDeathsRet += pc.getDeaths(AgeBand.SENIOR) + pc.getDeaths(AgeBand.ELDER);
        rNWhh += R0.per(R0.nwFull()); wNWhh += W0.per(W0.nwFull());
        rNWga += R0.ups > 0 ? R0.nwFull() / R0.ups : 0; wNWga += W0.ups > 0 ? W0.nwFull() / W0.ups : 0;
        sumTaken += HB.getTakenAway(); sumShr += shrGone; sumAbr += HB.getAbroadTakenAway(); sumFold += HB.getCellsFolded();
        sumDHcity += a.all.h - b.all.h; sumCityWorkNW += dNWw;
        rPplPerHh += R0.h > 0 ? R0.ppl / R0.h : 0;
        wN++;
        // the transfer, by the model's own pooling run on this month's before/after
        List<Household> cs = HB.cells();
        double[] fresh = new double[cb.n];
        for (int i = 0; i < cb.n; i++) fresh[i] = cs.get(i).households();
        Move mNw = replica(cb.hh, fresh, cb.nw, cb.gu, cb.grp);
        Move mSv = replica(cb.hh, fresh, cb.sav, cb.gu, cb.grp);
        double gain = 0, lost = 0, rrN = 0, rcN = 0, rrS = 0, rcS = 0, newc = 0, hhR = 0, nwR = 0, svR = 0, hhW = 0, nwW = 0, svW = 0;
        double wGain = 0, wRecvN = 0, wRecvS = 0;
        for (int i = 0; i < cb.n; i++) {
            double d = fresh[i] - cb.hh[i];
            if (cb.row[i] == Household.RETIRED_ROW) {
                if (d > 0) gain += d; else lost -= d;
                rrN += mNw.recvRow[i]; rcN += mNw.recvCity[i]; rrS += mSv.recvRow[i]; rcS += mSv.recvCity[i]; newc += mNw.newcomers[i];
                hhR += cb.hh[i]; nwR += cb.hh[i] * cb.nw[i]; svR += cb.hh[i] * cb.sav[i];
            } else if (cb.row[i] < Household.RETIRED_ROW) {
                if (d > 0) wGain += d;
                wRecvN += mNw.recvRow[i] + mNw.recvCity[i]; wRecvS += mSv.recvRow[i] + mSv.recvCity[i];
                hhW += cb.hh[i]; nwW += cb.hh[i] * cb.nw[i]; svW += cb.hh[i] * cb.sav[i];
            }
        }
        double recvN = rrN + rcN;
        P.printf("%5d %6.1f %5.1f %5.1f %5.1f %6.1f %6.1f %6.1f %6.1f %6.0f %2.0f/%-2.0f | %9.1f %9.1f %7.1f %4d | %9.1f %9.1f %7.2f %9.1f | %7.2f %7.2f %8s %8s | %8.1f %8.1f %8.1f%n",
                m, pc.getPromoted(AgeBand.ADULT), pc.getDeaths(AgeBand.BABY), pc.getDeaths(AgeBand.CHILD), pc.getDeaths(AgeBand.TEEN), pc.getDeaths(AgeBand.ADULT),
                pc.getDeaths(AgeBand.SENIOR), pc.getDeaths(AgeBand.ELDER), pc.getLastBirths(), mg.getLastArrivals(), mg.getLastDepartures(), mg.getLastBankruptcyDepartures(),
                HB.getTakenAway(), shrGone, HB.getAbroadTakenAway(), HB.getCellsFolded(),
                R0.h, R1.h, dH, dNW, gain, lost, gain > 0 ? n3(recvN / gain) : "       -", gain > 0 ? n3((rrS + rcS) / gain) : "       -",
                nwR / hhR, nwW / hhW, svW / hhW);
        tGain += gain; tLost += lost; tRecvRowNw += rrN; tRecvCityNw += rcN; tRecvRowSv += rrS; tRecvCitySv += rcS; tNew += newc;
        tAvgRetNw += nwR / hhR; tAvgWorkNw += nwW / hhW; tAvgRetSv += svR / hhR; tAvgWorkSv += svW / hhW;
        tWGain += wGain; tWRecvNw += wRecvN; tWRecvSv += wRecvS;
        tMonthsGain += gain > 0 ? 1 : 0;
        tGoneCheck += mSv.gone; tGoneModel += HB.getTakenAway();
    }

    static int tMonthsGain;
    static double tGoneCheck, tGoneModel;

    static void windowSummary() {
        double n = wN;
        double avgPplPerRet = rPplPerHh / n;
        P.printf("WINDOW SUMMARY (%d months): promoted ADULT->SENIOR %.1f people; retired deaths (sen+eld) %.1f; net change in retired people %+.1f, in retired households %+.1f (%.2f people per retired household)%n",
                wN, sumProm, sumDeathsRet, sumDppl, sumDH, avgPplPerRet);
        P.printf("  net, retired row: NW at constant prices changed %+.1f $k = %s per added household, %s per person promoted. Left the city in the window: savings %.1f, shares %.1f, abroad %.1f USD; cells folded %.0f%n",
                sumDNW, Math.abs(sumDH) > 1e-9 ? String.format("%.1f", sumDNW / sumDH) : "n/a", sumProm > 1e-9 ? String.format("%.1f", sumDNW / sumProm) : "n/a",
                sumTaken, sumShr, sumAbr, sumFold);
        P.printf("  TRANSFER: the retired cells GAINED households in %d of %d months (%.2f in all) and lost %.2f; per household gained they were handed NW %s (from the retired's own pool %s, from the city's %s), savings %s; genuinely new households %.2f%n",
                tMonthsGain, wN, tGain, tLost, tGain > 0 ? n3((tRecvRowNw + tRecvCityNw) / tGain) : "n/a", tGain > 0 ? n3(tRecvRowNw / tGain) : "n/a", tGain > 0 ? n3(tRecvCityNw / tGain) : "n/a",
                tGain > 0 ? n3((tRecvRowSv + tRecvCitySv) / tGain) : "n/a", tNew);
        P.printf("  against, averaged over the months' openings: retired NW/hh %.1f (savings-debt %.1f); the six working rows NW/hh %.1f (savings-debt %.1f); what the WORKING cells that gained households were handed per household gained: NW %s (savings %s)%n",
                tAvgRetNw / n, tAvgRetSv / n, tAvgWorkNw / n, tAvgWorkSv / n, tWGain > 0 ? n3(tWRecvNw / tWGain) : "n/a", tWGain > 0 ? n3(tWRecvSv / tWGain) : "n/a");
        if (tGain > 0)
            P.printf("  so a household that comes into the retired row is handed %.0f%% of the working average and %.0f%% of the retired average (full NW); on savings alone %.0f%% and %.0f%%%n",
                    100 * (tRecvRowNw + tRecvCityNw) / tGain / (tAvgWorkNw / n), 100 * (tRecvRowNw + tRecvCityNw) / tGain / (tAvgRetNw / n),
                    100 * (tRecvRowSv + tRecvCitySv) / tGain / (tAvgWorkSv / n), 100 * (tRecvRowSv + tRecvCitySv) / tGain / (tAvgRetSv / n));
        P.printf("  before each month, retired NW per household %.1f (per grown-up %.1f), working %.1f (per grown-up %.1f); retired/working %.2f per household, %.2f per grown-up%n",
                rNWhh / n, rNWga / n, wNWhh / n, wNWga / n, rNWhh / wNWhh, rNWga / wNWga);
        P.printf("  check of the copy: savings the copy says left the city %.1f vs HouseholdBalance.getTakenAway() %.1f (the model's also counts the city's paper sold on the way out); working rows' NW changed %+.1f at constant prices, city households %+.1f%n",
                tGoneCheck, tGoneModel, sumCityWorkNW, sumDHcity);
        tMonthsGain = 0; tGoneCheck = tGoneModel = 0;
    }

    /* ============================ item 4c: the counts ============================ */

    static final List<String> COSTROWS = new ArrayList<>();

    static void costCounts() {
        int cells = HB.cellCount(), non = 0;
        for (Household c : HB.cells()) if (!c.isEmpty()) non++;
        int listed = 0;
        for (int c = 0; c < NC; c++) if (EQ.getShares(c) > 0) listed++;
        int bonds = -1, stocks = -1;
        try {
            Method sm = HouseholdBalance.class.getDeclaredMethod("stocks", int[].class);
            sm.setAccessible(true);
            stocks = ((List<?>) sm.invoke(HB, (Object) null)).size();
            Field f = HouseholdBalance.class.getDeclaredField("bondStockIds");
            f.setAccessible(true);
            bonds = ((int[]) f.get(HB)).length;
        } catch (Exception e) { P.println("reflection failed: " + e); }
        COSTROWS.add(String.format("%6d %6d %9d %8d %11d %9d %14d %18d %16d", G.getMonth(), cells, non, listed, bonds, stocks, stocks * cells, cells * NC, cells * bonds));
    }

    /* ============================ the end ============================ */

    static void ratioTable() {
        P.println();
        P.println("--- COST COUNTS: what the household code is multiplied by (HouseholdBalance.CELL_SLOTS = " + HouseholdBalance.CELL_SLOTS + ", Equity.COMPANIES.length = " + NC + ") ---");
        P.printf("%6s %6s %9s %8s %11s %9s %14s %18s %16s%n", "month", "cells", "nonEmpty", "listed", "bondStocks", "stocks", "cell x stock", "cell x company", "cell x bond");
        for (String r : COSTROWS) P.println(r);
        P.println();
        P.println("=== RETIRED vs WORKING, net worth per household, over time (full net worth: savings+shares+abroad+paper+bonds-debt-studentDebt, $k) ===");
        P.printf("%6s %8s %9s %9s | %10s %10s | %6s %6s %6s | NW per yr of take-home: %7s %7s | %8s%n", "month", "pop", "retHH", "workHH", "retired", "working", "ratio", "sv/sv", "perGU", "retired", "working", "run ms");
        for (double[] r : RATIOS)
            P.printf("%6.0f %8.0f %9.1f %9.1f | %10.3f %10.3f | %6.3f %6.3f %6.3f |                       %7.2f %7.2f | %8.1f%n", r[0], r[1], r[2], r[3], r[4], r[5], r[6], r[7], r[8], r[9], r[10], r[11]);
        P.println("ratio = retired NW per household / working NW per household; sv/sv = the same on savings-debt only; perGU = per grown-up. run ms = mean LongPlaytest.run(g,1) over the 20 months before (includes the playtest's own audit after each month).");
    }

    public static void main(String[] a) throws Exception {
        String mode = a.length > 0 ? a[0] : "main";
        PrintStream real = System.out;
        System.setOut(QUIET);
        LongPlaytest.out = QUIET;
        Path outDir = Path.of("docs/studies/households/out");
        Files.createDirectories(outDir);
        try {
            switch (mode) {
                case "main" -> {
                    P = new PrintStream(Files.newOutputStream(outDir.resolve(a.length > 1 ? a[1] : "probe-main.txt")), true, StandardCharsets.UTF_8);
                    runMain();
                }
                case "jfr" -> {
                    P = new PrintStream(Files.newOutputStream(outDir.resolve(a.length > 1 ? a[1] : "probe-jfr.txt")), true, StandardCharsets.UTF_8);
                    runJfr(a.length > 2 ? Path.of(a[2]) : Path.of("hh.jfr"));
                }
                case "time" -> {
                    P = new PrintStream(Files.newOutputStream(outDir.resolve(a.length > 2 ? a[2] : "probe-time.txt")), true, StandardCharsets.UTF_8);
                    runTime(a[1]);
                }
                case "small" -> {
                    P = real;
                    quietChecks = true;
                    runSmall(Integer.parseInt(a[1]));
                }
                case "check" -> {
                    P = real;
                    int target = Integer.parseInt(a[1]);
                    quietChecks = true;
                    Game g = buildCity(GameFiles.scratch("households-probe-check"));
                    playTo(g, target);
                    checkLine(g);
                }
                case "theirs" -> {
                    P = real;
                    int target = Integer.parseInt(a[1]);
                    Game g = LongPlaytest.probeCity(GameFiles.scratch("households-probe-check"), null);
                    bind(g);
                    LongPlaytest.probePlay(g, target);
                    checkLine(g);
                }
                default -> throw new IllegalArgumentException(mode);
            }
        } finally {
            System.setOut(real);
            if (P != null && P != real) P.close();
        }
    }

    /** A smaller founding than UiProbe's: only `houses` houses, one store, one farm, then 21 months with no other build; the claim's cell at month 21. */
    static void runSmall(int houses) {
        LongPlaytest.resetTheFloat();
        Game g = new Game(GameFiles.scratch("households-probe-small"), LongPlaytest.founding());
        bind(g);
        g.run();
        LongPlaytest.childcareWhenNeeded(g);
        g.getDebtManager().setAutopilot(LongPlaytest.AUTOPILOT);
        g.setRolloverMode(LongPlaytest.ROLLOVER);
        g.getCityMap();
        g.setRescueMode(LongPlaytest.RESCUE_AUTO ? TreasuryFund.RescueMode.AUTOMATIC : TreasuryFund.RescueMode.BUTTON);
        g.setFundDial(LongPlaytest.FUND_DIAL);
        LongPlaytest.seedMarket(g, 0);
        LongPlaytest.villageBuild(g, "House", houses);
        LongPlaytest.villageBuild(g, "Convenience Store", 1);
        LongPlaytest.villageBuild(g, "Mixed Farm", 1);
        while (g.getMonth() < 21) step(g);
        Snap s = snap(null);
        Household cs = cellByKey("COUPLE:SKILLED");
        Agg a = new Agg(); if (cs != null) a.add(cs, s.p);
        double top = 0; int topC = 0;
        for (int c = 0; c < NC; c++) if (HB.sharesHeld(c) * s.p.px[c] > top) { top = HB.sharesHeld(c) * s.p.px[c]; topC = c; }
        P.printf("houses %d: month %d population %.0f; COUPLE:SKILLED hh %.2f take-home %.3f savings %.3f shares(mkt) %.1f dividends %.3f $k/mo per hh | all hh: shares %.1f savings %.1f | largest holding %s %.0f%% of households' shares, unit price %.2f x founding price%n",
                houses, g.getMonth(), g.getCohorts().total(), cs == null ? 0 : cs.households(), a.per(a.take), a.per(a.sav), a.per(a.shr), a.per(a.div), s.all.shr, s.all.sav,
                Equity.COMPANIES[topC], s.all.shr > 0 ? 100 * top / s.all.shr : 0, EX.pricePerFoundingShare(topC) / EQ.foundingPrice());
    }

    static void checkLine(Game g) {
        Snap s = snap(null);
        P.printf("month %d population %.6f shares(mkt) %.6f savings %.6f%n", g.getMonth(), g.getCohorts().total(), s.all.shr, s.all.sav);
    }

    static void runMain() {
        long t0 = System.nanoTime();
        P.println("HouseholdsProbe: Civic Ledger household ledgers. City = LongPlaytest.probeCity()'s (Danzik founding, first builds), played with probePlay()'s loop, month by month. Units: $k (thousands of dollars).");
        P.println("Checkpoints are the exact month numbers; flows are the 12 months after 600 and 2400; the probe changes no game code; the city lives on GameFiles.scratch(\"households-probe\").");
        P.println("model money is $k: a take-home of 3.5 is $3,500 a month.");
        Game g = buildCity(GameFiles.scratch("households-probe"));
        P.printf("the build ended at month %d (the probe city's first call)%n", g.getMonth());
        playTo(g, 4000);
        ratioTable();
        P.printf("%nwall time %.1f min; ended at month %d; mean run(g,1) over the whole run %.1f ms%n", (System.nanoTime() - t0) / 6e10, g.getMonth(), meanMs(0, g.getMonth()));
        System.err.println("done main at month " + g.getMonth());
    }

    /* ============================ item 4b: exact time per method, from HhAgent ============================ */

    static java.util.function.IntConsumer HOOK;

    /** Each window of 100 months starting at the given months, timed by the instrumented methods (needs -javaagent:hhagent.jar=...). The city is played in one playTo(g, 4000), as in main mode, so it is the same city. */
    static void runTime(String list) {
        quietChecks = true;
        HhTimer.main = Thread.currentThread();
        P.println("INCLUSIVE WALL TIME per instrumented method (HhAgent: nanoTime at entry and exit of every overload, outermost call only), windows of 100 months of the probe city.");
        P.println("'model' = Game.simulateMonths (the month the game runs); 'run' = LongPlaytest.run(g,1) = model + the playtest's audit of the month, which probeMonthMs() times. Methods nest: percentages do not add.");
        final int[] starts = java.util.Arrays.stream(list.split(",")).mapToInt(t -> Integer.parseInt(t.trim())).toArray();
        HOOK = m -> {
            for (int s : starts) {
                if (m == s) HhTimer.reset();
                if (m == s + 100) {
                    int months = 100;
                    int sim = HhTimer.idOf("Game.simulateMonths"), run = HhTimer.idOf("LongPlaytest.run");
                    double simMs = HhTimer.ms(sim), runMs = HhTimer.ms(run);
                    P.println();
                    P.printf("=== months %d..%d (%d months), population %.0f, %d cells ===  mean model month (Game.simulateMonths) %.2f ms, mean run(g,1) %.2f ms (the probe's own array over the same months: %.2f)%n", s, m, months, G.getCohorts().total(), HB.cellCount(),
                            simMs / months, runMs / months, meanMs(s, m));
                    P.printf("%-34s %9s %10s %10s %9s %9s%n", "method (inclusive)", "calls", "ms total", "ms/month", "% model", "% run");
                    Integer[] ids = new Integer[HhTimer.count()];
                    for (int i = 0; i < ids.length; i++) ids[i] = i;
                    java.util.Arrays.sort(ids, (x, y) -> Double.compare(HhTimer.ms(y), HhTimer.ms(x)));
                    for (int id : ids) if (HhTimer.calls(id) > 0)
                        P.printf("%-34s %9d %10.1f %10.3f %8.2f%% %8.2f%%%n", HhTimer.NAMES[id], HhTimer.calls(id), HhTimer.ms(id), HhTimer.ms(id) / months, 100 * HhTimer.ms(id) / simMs, 100 * HhTimer.ms(id) / runMs);
                    if (s == starts[starts.length - 1]) done = true;
                }
            }
        };
        Game g = buildCity(GameFiles.scratch("households-probe-time"));
        playTo(g, 4000);
    }

    /* ============================ item 4b: the flight recording ============================ */

    static jdk.jfr.Recording REC;
    static long recT0;
    static int recFrom;

    /** The same city, one playTo(g, 4000); a flight recording from month 2300 for SPAN months. */
    static void runJfr(Path jfr) throws Exception {
        quietChecks = true;
        HOOK = m -> {
            try {
                if (m == 2300) {
                    jdk.jfr.Configuration cfg = jdk.jfr.Configuration.getConfiguration("profile");
                    REC = new jdk.jfr.Recording(cfg);
                    if (PERIOD_MS > 0) REC.enable("jdk.ExecutionSample").withPeriod(Duration.ofMillis(PERIOD_MS)).withStackTrace();
                    REC.start();
                    recT0 = System.nanoTime(); recFrom = m;
                }
                if (m == 2300 + SPAN && REC != null) {
                    REC.stop();
                    REC.dump(jfr);
                    REC.close();
                    REC = null;
                    long wall = System.nanoTime() - recT0;
                    P.printf("recording: months %d..%d, %d months, wall %.1f s, %.1f ms a month; execution sampling every %s; stack depth set by -XX:FlightRecorderOptions=stackdepth=%s%n",
                            recFrom, m, m - recFrom, wall / 1e9, wall / 1e6 / (m - recFrom), PERIOD_MS > 0 ? PERIOD_MS + " ms" : "the profile setting (10 ms)", System.getProperty("jfr.depth", "?"));
                    P.println("(the months are played by the playtest loop, whose run(g,1) also audits the month; Game.simulateMonths is the model proper, so the shares are given both ways)");
                    analyse(jfr);
                    done = true;
                }
            } catch (Exception e) { throw new RuntimeException(e); }
        };
        Game g = buildCity(GameFiles.scratch("households-probe-jfr"));
        playTo(g, 4000);
    }

    static boolean isHh(String cls) {
        return cls.startsWith("ham.citybuildersim.HouseholdBalance") || cls.equals("ham.citybuildersim.Household")
                || cls.startsWith("ham.citybuildersim.Household$") || cls.matches("ham\\.citybuildersim\\.[A-Za-z]*Household(\\$.*)?")
                || cls.startsWith("ham.citybuildersim.FamilyModel");
    }

    static String shortName(String cls, String method) {
        String c = cls.startsWith("ham.citybuildersim.") ? cls.substring("ham.citybuildersim.".length()) : cls;
        return c + "." + method;
    }

    static void analyse(Path file) throws Exception {
        int total = 0, main = 0, sim = 0, topHh = 0, topHhSim = 0, incAdvance = 0, incAdvanceSim = 0, incHh = 0, incHhSim = 0, truncated = 0;
        Map<String, Integer> threads = new HashMap<>(), selfHb = new HashMap<>(), selfAll = new HashMap<>(), selfCls = new HashMap<>(), incl = new HashMap<>();
        Map<String, Integer> topHhByClass = new HashMap<>();
        try (jdk.jfr.consumer.RecordingFile rf = new jdk.jfr.consumer.RecordingFile(file)) {
            while (rf.hasMoreEvents()) {
                jdk.jfr.consumer.RecordedEvent e = rf.readEvent();
                if (!e.getEventType().getName().equals("jdk.ExecutionSample")) continue;
                total++;
                jdk.jfr.consumer.RecordedThread t = e.getThread("sampledThread");
                String tn = t == null ? "?" : t.getJavaName();
                threads.merge(tn, 1, Integer::sum);
                if (!"main".equals(tn)) continue;
                jdk.jfr.consumer.RecordedStackTrace st = e.getStackTrace();
                if (st == null) continue;
                if (st.isTruncated()) truncated++;
                List<jdk.jfr.consumer.RecordedFrame> fr = st.getFrames();
                if (fr.isEmpty()) continue;
                main++;
                boolean inSim = false, inAdvance = false, anyHh = false;
                Set<String> seen = new HashSet<>();
                for (jdk.jfr.consumer.RecordedFrame f : fr) {
                    String cls = f.getMethod().getType().getName(), mn = f.getMethod().getName();
                    if (cls.equals("ham.citybuildersim.Game") && mn.equals("simulateMonths")) inSim = true;
                    if (cls.equals("ham.citybuildersim.HouseholdBalance") && mn.equals("advanceMonth")) inAdvance = true;
                    if (isHh(cls)) anyHh = true;
                    if (cls.startsWith("ham.citybuildersim.") && !cls.startsWith("ham.citybuildersim.HouseholdsProbe") && !cls.startsWith("ham.citybuildersim.LongPlaytest")) {
                        String key = shortName(cls, mn);
                        if (seen.add(key)) incl.merge(key, 1, Integer::sum);
                    }
                }
                jdk.jfr.consumer.RecordedFrame top = fr.get(0);
                String tc = top.getMethod().getType().getName(), tm = top.getMethod().getName();
                selfAll.merge(shortName(tc, tm), 1, Integer::sum);
                selfCls.merge(tc, 1, Integer::sum);
                if (inSim) sim++;
                if (isHh(tc)) { topHh++; if (inSim) topHhSim++; topHhByClass.merge(tc, 1, Integer::sum); }
                if (tc.startsWith("ham.citybuildersim.HouseholdBalance")) selfHb.merge(shortName(tc, tm), 1, Integer::sum);
                if (inAdvance) { incAdvance++; if (inSim) incAdvanceSim++; }
                if (anyHh) { incHh++; if (inSim) incHhSim++; }
            }
        }
        P.println();
        P.println("--- JFR samples (jdk.ExecutionSample) ---");
        P.println("threads sampled: " + threads);
        P.printf("samples %d, on the model thread (main) %d, of which under Game.simulateMonths %d (%.1f%%), truncated stacks %d%n", total, main, sim, 100.0 * sim / Math.max(1, main), truncated);
        P.printf("TOP frame in HouseholdBalance/Household/*Household/FamilyModel: %d = %.2f%% of main-thread samples, %.2f%% of samples under simulateMonths (%d)%n", topHh, 100.0 * topHh / Math.max(1, main), 100.0 * topHhSim / Math.max(1, sim), topHhSim);
        P.printf("stack CONTAINS HouseholdBalance.advanceMonth: %d = %.2f%% of main-thread samples, %.2f%% of samples under simulateMonths%n", incAdvance, 100.0 * incAdvance / Math.max(1, main), 100.0 * incAdvanceSim / Math.max(1, sim));
        P.printf("stack CONTAINS any frame of those classes: %d = %.2f%% of main-thread samples, %.2f%% under simulateMonths%n", incHh, 100.0 * incHh / Math.max(1, main), 100.0 * incHhSim / Math.max(1, sim));
        P.println("top frames by class within that set: " + sorted(topHhByClass, 8));
        P.println("top 10 methods by self samples under HouseholdBalance (all of the class's samples: " + selfHb.values().stream().mapToInt(Integer::intValue).sum() + "):");
        for (Map.Entry<String, Integer> en : top(selfHb, 10)) P.printf("  %5d  %5.2f%%  %s%n", en.getValue(), 100.0 * en.getValue() / Math.max(1, main), en.getKey());
        P.println("top 12 classes by self samples overall (share of main-thread samples):");
        for (Map.Entry<String, Integer> en : top(selfCls, 12)) P.printf("  %5d  %5.2f%%  %s%n", en.getValue(), 100.0 * en.getValue() / Math.max(1, main), en.getKey());
        P.println("top 25 ham.citybuildersim methods by INCLUSIVE samples (stack contains it; the playtest wrapper frames left out):");
        for (Map.Entry<String, Integer> en : top(incl, 25)) P.printf("  %5d  %5.1f%%  %s%n", en.getValue(), 100.0 * en.getValue() / Math.max(1, main), en.getKey());
    }

    static List<Map.Entry<String, Integer>> top(Map<String, Integer> m, int n) {
        List<Map.Entry<String, Integer>> l = new ArrayList<>(m.entrySet());
        l.sort((x, y) -> y.getValue() - x.getValue());
        return l.subList(0, Math.min(n, l.size()));
    }

    static String sorted(Map<String, Integer> m, int n) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> e : top(m, n)) sb.append(e.getKey().replace("ham.citybuildersim.", "")).append('=').append(e.getValue()).append(' ');
        return sb.toString().trim();
    }
}
