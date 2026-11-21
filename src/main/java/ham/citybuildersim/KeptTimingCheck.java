package ham.citybuildersim;

import ham.citybuildersim.ui.KeptBench;

import java.util.ArrayList;
import java.util.List;

import static ham.citybuildersim.KeptCheck.fx;
import static ham.citybuildersim.KeptCheck.median;
import static ham.citybuildersim.KeptCheck.month;

/**
 * What the kept pages cost: KeptCheck's timed sections, each kept month on a page against the same month built new, within its
 * budget through Timing.
 *
 * WHY (FS4, KT1). KeptCheck timed its pages among the sections that prove them right, so the whole harness was in
 * Timing.HARNESSES and the timing step ran all of it hard (408 s of the step's 941 s on 2026-11-06) to judge a few medians, and a
 * page slow under a loaded PC was a red in the harness whose verdict is that the pages are right (KEPT_ALL_CHROME_MS, KS1-rest).
 * Split, KeptCheck is correctness alone and this is the clocks alone. In a soft run it does nothing at all (HA1: no rounds whose only
 * verdict is time); hard, it builds KeptCheck's city (KeptCheck.playedCity()), plays it on without drawing to the month each budget
 * was measured at, and costs each page there with the premise its section in KeptCheck causes, the page first drawn kept for
 * WARM_MONTHS months untimed, as KeptCheck's correctness sections drew it before its cost was taken. The budgets, the sentences
 * above them and the lines' labels are KeptCheck's as they were, and the sections keep KeptCheck's numbers.
 *
 * What it has to prove, each figure through Timing with the same month built new printed beside it:
 *   5. the kept People month is within KEPT_MONTH_MS;
 *  10. the kept chrome's month (the header and the rail) is within KEPT_CHROME_MS;
 *  13. the whole chrome kept (the header, the rail and the three panels) is within KEPT_ALL_CHROME_MS;
 *  20. the kept City History month is within KEPT_HISTORY_MS;
 *  24, 28, 32. the kept month on Infrastructure (with transit on it, KeptCheck.ensureTransit()), on Services and on the land office
 *      is within KEPT_INFRASTRUCTURE_MS, KEPT_SERVICES_MS and KEPT_LAND_MS;
 *  39, 43. on Build, the Overview and a city category with two orders dialled up and a card pinned, within KEPT_BUILD_MS, and on the
 *      construction page with KeptCheck.SITE_ROWS sites held on, within KEPT_CONSTRUCTION_MS;
 *  52. on the Sectors list within KEPT_SECTORS_MS and on the three business pages that weigh most within KEPT_SECTOR_MS, the plants
 *      held and a works standing (KeptCheck.ensureWorks());
 *  68. at MONEY_MONTH: on the heaviest page of the Bank and of Finances, on Borrow with an ask, and on the fund's Search and Activity
 *      (the fund filled as KeptCheck's section 61 fills it) within KEPT_BANK_MS, KEPT_FINANCES_MS and KEPT_FUND_MS; and the fund's
 *      heaviest page is one of the two costed;
 *  81. at SCREENS_MONTH: on Government's Overview within KEPT_GOVERNMENT_MS, on Trade's Overview and its heaviest page within
 *      KEPT_TRADE_MS, and on Policy's hub and its dial-heavy pages within KEPT_POLICY_MS;
 *  90. on Household money (as it opens and with a cell's books open) within KEPT_HOUSEHOLD_MS and on the Rankings tab (as it opens
 *      and with every measure unfolded) within KEPT_RANKINGS_MS.
 *
 * It needs the JavaFX toolkit but no window, as KeptCheck does; where the toolkit cannot start it says so in one line and exits 0.
 */
public final class KeptTimingCheck {

    private KeptTimingCheck() { }

    static int fails = 0;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-72s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** The month the money budgets were measured at (KEPT_BANK_MS's sentence: "a city played to month 2,100 as KeptCheck's is when it comes to them"). */
    static final int MONEY_MONTH = 2_100;

    /** The month the Government, Trade, Policy, Household money and Rankings budgets were measured at (KEPT_GOVERNMENT_MS's sentence: "at month 3,500 of a played city"). */
    static final int SCREENS_MONTH = 3_500;

    /** The months played in one call on the toolkit's thread while the city is played on to a budget's month: well inside fx()'s ten minutes. */
    static final int PLAY_CHUNK = 100;

    /** The budget for the kept People page's month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC. */
    static final double KEPT_MONTH_MS = 12;

    /** The budget for the kept chrome's month - the header's and the rail's draw, then the pulse's CSS and layout over both - at CITY_MONTHS, on the PC. */
    static final double KEPT_CHROME_MS = 4;

    /** The budget for the whole chrome kept, in one month: the header, the rail and the three panels, drawn and laid out. */
    static final double KEPT_ALL_CHROME_MS = 8;

    /**
     * The budget for the kept City History month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC:
     * twice the kept month's median, rounded up (5.0 to 8.2 ms in five runs, 6.2 between them; the same month built new
     * 20.6 to 26.3 ms).
     */
    static final double KEPT_HISTORY_MS = 13;

    /**
     * The budget for the kept Infrastructure month - its draw, then the pulse's CSS and layout, on the Roads page - at CITY_MONTHS, on
     * the PC: twice the kept month's median, rounded up (2.7 to 6.3 ms in five runs, 4.4 between them; the same month built new
     * 10.2 to 19.1 ms).
     */
    static final double KEPT_INFRASTRUCTURE_MS = 9;

    /**
     * The budget for the kept Services month - its draw, then the pulse's CSS and layout, on the Health overview - at CITY_MONTHS, on
     * the PC: twice the kept month's median, rounded up (4.8 to 10.5 ms in five runs, 6.5 between them; the same month built new
     * 9.9 to 19.5 ms).
     */
    static final double KEPT_SERVICES_MS = 13;

    /**
     * The budget for the kept land office month - its draw, then the pulse's CSS and layout, on the office as it opens - at CITY_MONTHS,
     * on the PC: twice the kept month's median, rounded up (2.1 to 3.3 ms in five runs, 2.4 between them; the same month built new
     * 9.1 to 16.0 ms).
     */
    static final double KEPT_LAND_MS = 5;

    /**
     * The budget for the kept Build month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC, on the Overview and on a
     * city category with two orders dialled up and a card's stats pinned (the heavier of the two is the figure): twice the kept month's median,
     * rounded up (3.8 to 6.3 ms in five runs on the Overview, 4.3 between them; 3.7 to 9.0 ms on the category, 6.0 between them; the same
     * months built new 11.6 to 23.7 and 15.0 to 30.0 ms).
     */
    static final double KEPT_BUILD_MS = 12;

    /**
     * The budget for the kept construction page's month - its draw, then the pulse's CSS and layout, on the Sites tab with the city's
     * sites on - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (1.4 to 1.9 ms in five runs, 1.7 between them; the
     * same month built new 8.5 to 16.5 ms).
     */
    static final double KEPT_CONSTRUCTION_MS = 4;

    /**
     * The budget for the kept Sectors list month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC: twice the
     * kept month's median, rounded up (8.3 to 9.2 ms in five runs, 8.8 between them; the same month built new 23.8 to 26.0 ms).
     */
    static final double KEPT_SECTORS_MS = 18;

    /**
     * The budget for the kept month on a business's page - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC, the
     * heaviest of the three pages costed (the shops' Operations; a plain business's Income statement and wood products' Operations
     * weigh less): twice the kept month's median, rounded up (the shops' Operations 5.1 to 7.8 ms in five runs, 7.1 between them, the
     * same month built new 25.3 to 41.0 ms; the Income statement 2.7 to 3.9 ms, 2.9 between them, built new 10.1 to 11.5; wood products'
     * 5.4 to 9.3 ms, 5.5 between them, built new 19.0 to 25.1).
     */
    static final double KEPT_SECTOR_MS = 15;

    /**
     * The budget for the kept Bank month - its draw, then the pulse's CSS and layout - on the Bank's heaviest page (the Overview), on the PC: twice the
     * kept month's median, rounded up (5.2 to 8.3 ms in five runs of the money sections alone, in a city played to month 2,100 as KeptCheck's is when
     * it comes to them, 5.6 between them; the same month built new 15.9 to 19.2 ms).
     */
    static final double KEPT_BANK_MS = 12;

    /**
     * The budget for the kept Finances month on the heaviest of the two pages costed (Borrow at home, the page the auction draws, and the page with the
     * most nodes), on the PC: twice the kept month's median, rounded up (the same five runs: Borrow 7.6 to 10.2 ms, 8.6 between them, built new 16.7 to
     * 23.8; The book 7.2 to 9.0 ms, 8.6 between them, built new 16.0 to 21.9).
     */
    static final double KEPT_FINANCES_MS = 18;

    /**
     * The PC's budget for the kept month on the fund's heaviest page is twice the median in five sequential money-section runs, rounded up
     * (FP1's Activity 41.7 to 47.1 ms, 45.8 between them, built new 93.5 to 129.7, and Search 10.1 to 10.7 ms, 10.5 between them, built new 20.3 to 23.7).
     * Search starts with SEARCH_ROWS results and Activity with its latest twelve months and the older years shut. The five readings are in FP1-notes.md.
     */
    static final double KEPT_FUND_MS = 92;

    /**
     * The budget for the kept Government month - its draw, then the pulse's CSS and layout - on the Overview, at month 3,500 of a played city (about
     * where KeptCheck is when it comes to the Government tab), on the PC: twice the kept month's median, rounded up (3.1 to 4.9 ms in five runs of the
     * KP6 sections alone, 3.4 between them; the same month built new 12.0 to 20.6 ms).
     */
    static final double KEPT_GOVERNMENT_MS = 7;

    /**
     * The budget for the kept Trade month - its draw, then the pulse's CSS and layout - on the Overview and on the heaviest of its pages (the reserves),
     * the larger of the two being the figure, at month 3,500 of a played city, on the PC: twice the kept month's median, rounded up (the Overview 4.3
     * to 9.0 ms in five runs of the KP6 sections alone, 4.7 between them, the same month built new 8.3 to 14.8 ms; the reserves page 2.7 to 4.3 ms,
     * 3.0 between them, built new 10.6 to 13.8).
     */
    static final double KEPT_TRADE_MS = 10;

    /**
     * The budget for the kept Policy month - its draw, then the pulse's CSS and layout - on the hub and on the pages heavy with dials (Taxes' Profit
     * page and the policy rate's), the largest of them being the figure, at month 3,500 of a played city, on the PC: twice the kept month's median,
     * rounded up (the hub 4.3 to 5.2 ms in five runs of the KP6 sections alone, 4.6 between them, built new 9.7 to 14.9 ms; Profit 2.0 to 3.3 ms, 2.1
     * between them, built new 6.9 to 8.9; the policy rate 4.2 to 7.2 ms, 4.5 between them, built new 15.9 to 26.5).
     */
    static final double KEPT_POLICY_MS = 10;

    /**
     * The budget for the kept Household money month - its draw, then the pulse's CSS and layout - on the page as it opens and with the first family
     * cell's books open (the larger of the two being the figure), at month 3,400 to 4,000 of a played city (about where KeptCheck is when it comes to
     * this page), on the PC: twice the kept month's median, rounded up (the page as it opens 2.7 to 3.1 ms in five runs of the KP7 sections alone, 2.8
     * between them, the same month built new 15.9 to 17.7 ms; with a cell open 3.6 to 7.0 ms, 3.8 between them, built new 19.2 to 29.7).
     */
    static final double KEPT_HOUSEHOLD_MS = 8;

    /**
     * The budget for the kept Rankings month - its draw, then the pulse's CSS and layout - on the tab as it opens and with every measure unfolded
     * (Browse all: a line a measure; the larger of the two being the figure), at month 3,400 to 4,000 of a played city, on the PC: twice the kept
     * month's median, rounded up (the tab as it opens 2.6 to 3.0 ms in five runs of the KP7 sections alone, 2.8 between them, the same month built new
     * 9.0 to 10.9 ms; unfolded 3.1 to 3.5 ms, 3.3 between them, built new 16.8 to 20.3).
     */
    static final double KEPT_RANKINGS_MS = 7;

    public static void main(String[] args) throws Exception {
        System.out.println("=== KeptTimingCheck: what the kept pages cost, each against the same month built new (KeptCheck's timed sections, FS4) ===");
        if (Timing.skip("KeptTimingCheck: every section is a clock's verdict")) System.exit(0);
        String down = KeptCheck.startJavaFx();
        if (down != null) {
            System.out.println("skipped: the JavaFX toolkit cannot start here (" + down + "); the page needs it to be laid out");
            System.exit(0);
        }
        Game g = KeptCheck.playedCity(GameFiles.scratch("kepttimingcheck"));
        KeptBench bench = fx(() -> new KeptBench(g));
        System.out.printf("a city played to month %d: %,d people%n", g.getMonth(), g.getPopulationManager().getPopulation());

        peopleCost(g, bench);
        chromeCost(g, bench);
        wholeChromeCost(g, bench);
        historyCost(g, bench);
        kp2Cost(g, bench);
        kp3Cost(g, bench);
        sectorsCost(g, bench);
        playTo(g, MONEY_MONTH);
        moneyCost(g, bench);
        playTo(g, SCREENS_MONTH);
        kp6Cost(g, bench);
        kp7Cost(g, bench);

        System.out.println("\n" + (fails == 0 ? "ALL OK" : fails + " FAILED"));
        javafx.application.Platform.exit();
        System.exit(fails);
    }

    /** The city played on to `to` with no page drawn, a chunk of months to a call on the toolkit's thread, as KeptCheck's sections play it (KeptCheck.month()). */
    static void playTo(Game g, int to) throws Exception {
        KeptCheck.stopSites();
        int from = g.getMonth();
        while (g.getMonth() < to) {
            fx(() -> {
                for (int i = 0; i < PLAY_CHUNK && g.getMonth() < to; i++) month(g);
                return null;
            });
        }
        System.out.printf("%nplayed on from month %d to month %d, no page drawn: %,d people%n", from, g.getMonth(), g.getPopulationManager().getPopulation());
    }

    /**
     * The months a page is drawn kept, untimed, before its rounds are timed: KeptCheck's MONTHS, the months its correctness section
     * drew the page across before the cost was taken there. The budgets were measured on pages the JIT had compiled; timed cold, after
     * one draw, the first pages cost several times their budgets (FS4's first hard run here: the People month 60.6 ms against
     * KEPT_MONTH_MS's 12, the Sectors list 43.1 against 18), and the pages after them, whose code was compiled by then, did not.
     */
    static final int WARM_MONTHS = KeptCheck.MONTHS;

    /** A page's kept redraw, its CSS and layout passed, for WARM_MONTHS months on the toolkit's thread, nothing timed. */
    private static void warm(Game g, Runnable redraw) {
        for (int i = 0; i < WARM_MONTHS; i++) {
            month(g);
            redraw.run();
        }
    }

    /** One timed line: the figure against its budget through Timing, printed with its verdict, counted when it fails. */
    private static void timed(double figure, double budget, String label) {
        boolean ok = Timing.within(figure, budget);
        System.out.printf("%-72s %s%n", label, Timing.verdict(ok, label));
        if (!ok) fails++;
    }

    /** Section 5: what the kept People month costs, against the same month built new. */
    static void peopleCost(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 5: what the kept month costs ---");
        double[] cost = fx(() -> {
            bench.drawKept();
            for (int i = 0; i < 3; i++) bench.passes();
            warm(g, () -> {
                bench.drawKept();
                bench.passes();
            });
            List<Double> kept = new ArrayList<>(), fresh = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                month(g);
                double draw = bench.drawKept();
                double[] p = bench.passes();
                kept.add(draw + p[0] + p[1]);
            }
            // The same month built new: a page made from nothing in the column, as the redraw was before UP4.
            for (int i = 0; i < 9; i++) {
                month(g);
                double[] f = bench.showFresh();
                fresh.add(f[0] + f[1] + f[2]);
                bench.restoreKept();
            }
            return new double[]{median(kept), median(fresh)};
        });
        System.out.printf("  the page's month (its draw, CSS and layout), median of 9: kept %.1f ms, built new %.1f ms%n", cost[0], cost[1]);
        timed(cost[0], KEPT_MONTH_MS, "the kept month is within KEPT_MONTH_MS (" + KEPT_MONTH_MS + " ms)");
    }

    /** Section 10: what the kept chrome's month costs (the header and the rail), with the clock running at the third rung on the People tab. */
    static void chromeCost(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 10: what the kept chrome costs ---");
        double[] cost = fx(() -> {
            bench.clock(true, null);
            bench.speed(3);
            bench.screen(KeptCheck.CHROME_SCREENS[0]);
            bench.drawChromeKept();
            bench.chromePasses();
            warm(g, () -> {
                bench.drawChromeKept();
                bench.chromePasses();
            });
            List<Double> kept = new ArrayList<>(), fresh = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                month(g);
                double draw = bench.drawChromeKept();
                double[] p = bench.chromePasses();
                kept.add(draw + p[0] + p[1]);
            }
            // The same month built new: every node made again in the header and the rail, as the redraw was before UP2.
            for (int i = 0; i < 9; i++) {
                month(g);
                double[] f = bench.showFreshChrome();
                fresh.add(f[0] + f[1] + f[2]);
                bench.restoreKeptChrome();
            }
            return new double[]{median(kept), median(fresh)};
        });
        System.out.printf("  the chrome's month (its draw, CSS and layout), median of 9: kept %.1f ms, built new %.1f ms%n", cost[0], cost[1]);
        timed(cost[0], KEPT_CHROME_MS, "the kept chrome's month is within KEPT_CHROME_MS (" + KEPT_CHROME_MS + " ms)");
    }

    /** Section 13: what the whole chrome costs kept, the header, the rail and the three panels, the city panel on its dashboard with its economy section open. */
    static void wholeChromeCost(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 13: what the whole chrome costs, kept ---");
        double[] cost = fx(() -> {
            bench.cityMode(true, false);
            bench.sectionsAll(false);
            bench.section("econ", true);
            bench.inboxList(true, "");
            bench.clock(true, null);
            bench.speed(3);
            bench.screen(KeptCheck.CHROME_SCREENS[0]);
            bench.drawChromeKept();
            bench.drawPanelsKept();
            bench.chromePasses();
            bench.panelPasses();
            warm(g, () -> {
                bench.drawChromeKept();
                bench.drawPanelsKept();
                bench.chromePasses();
                bench.panelPasses();
            });
            List<Double> kept = new ArrayList<>(), fresh = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                month(g);
                double draw = bench.drawChromeKept() + bench.drawPanelsKept();
                double[] c = bench.chromePasses(), p = bench.panelPasses();
                kept.add(draw + c[0] + c[1] + p[0] + p[1]);
            }
            // The same month built new: every node of the header, the rail and the three panels made again.
            for (int i = 0; i < 9; i++) {
                month(g);
                double[] a = bench.showFreshChrome(), b = bench.showFreshPanels();
                fresh.add(a[0] + a[1] + a[2] + b[0] + b[1] + b[2]);
                bench.restoreKeptChrome();
                bench.restoreKeptPanels();
            }
            return new double[]{median(kept), median(fresh)};
        });
        System.out.printf("  the whole chrome's month (its draws, CSS and layout), median of 9: kept %.1f ms, built new %.1f ms%n", cost[0], cost[1]);
        timed(cost[0], KEPT_ALL_CHROME_MS, "the whole kept chrome's month is within KEPT_ALL_CHROME_MS (" + KEPT_ALL_CHROME_MS + " ms)");
    }

    /** Section 20: what the kept City History month costs, against the same month built new. */
    static void historyCost(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 20: what the kept City History month costs ---");
        double[] cost = fx(() -> {
            // The page as it opens: the first preset, the default range, nothing folded open.
            bench.historyPreset(0);
            bench.historyRange(ham.citybuildersim.ChartModel.DEFAULT_RANGE);
            bench.drawHistoryKept();
            for (int i = 0; i < 3; i++) bench.passes();
            warm(g, () -> {
                bench.drawHistoryKept();
                bench.passes();
            });
            List<Double> kept = new ArrayList<>(), fresh = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                month(g);
                double draw = bench.drawHistoryKept();
                double[] p = bench.passes();
                kept.add(draw + p[0] + p[1]);
            }
            // The same month built new: a page made from nothing in the column, as the redraw was before KP1.
            for (int i = 0; i < 9; i++) {
                month(g);
                double[] f = bench.showFreshHistory();
                fresh.add(f[0] + f[1] + f[2]);
                bench.restoreKeptHistory();
            }
            return new double[]{median(kept), median(fresh)};
        });
        System.out.printf("  the page's month (its draw, CSS and layout), median of 9: kept %.1f ms, built new %.1f ms%n", cost[0], cost[1]);
        timed(cost[0], KEPT_HISTORY_MS, "the kept City History month is within KEPT_HISTORY_MS (" + KEPT_HISTORY_MS + " ms)");
    }

    /** Sections 24, 28 and 32: the kept month on Infrastructure (with transit on it, as KeptCheck's 21 causes it), on Services and on the land office. */
    static void kp2Cost(Game g, KeptBench bench) throws Exception {
        System.out.println("\n  " + fx(() -> KeptCheck.ensureTransit(g)));
        pageCost(g, bench, fx(bench::infrastructure), 24, KEPT_INFRASTRUCTURE_MS, "KEPT_INFRASTRUCTURE_MS");
        pageCost(g, bench, fx(bench::services), 28, KEPT_SERVICES_MS, "KEPT_SERVICES_MS");
        pageCost(g, bench, fx(bench::land), 32, KEPT_LAND_MS, "KEPT_LAND_MS");
    }

    /**
     * Sections 39 and 43: the kept month on Build's Overview and on a city category with two orders dialled up and a card pinned, and on
     * the construction page's Sites tab, with SITE_ROWS sites held on through the months as KeptCheck's 36 to 43 hold them.
     */
    static void kp3Cost(Game g, KeptBench bench) throws Exception {
        System.out.println("\n  " + fx(() -> KeptCheck.ensureSites(g, KeptCheck.SITE_ROWS)));
        int onSite = fx(bench::sitesOn);
        check("the city has " + KeptCheck.SITE_ROWS + " sites on to draw (" + onSite + ")", onSite >= KeptCheck.SITE_ROWS);
        KeptCheck.keepSites(g);
        final String cat = BuildAdvice.HEALTHCARE;
        KeptBench.Page probe = fx(() -> bench.build("probe", cat, java.util.Map.of()));
        List<String> names = fx(() -> {
            bench.home(probe);
            bench.drawKept(probe);
            bench.passes();
            return bench.buildCardNames();
        });
        check("a city category has cards to dial an order on (" + names.size() + ")", names.size() >= 2);
        if (names.size() >= 2) {
            final String first = names.get(0), second = names.get(1);
            KeptBench.Page overview = fx(() -> bench.build("the Build Overview", BuildAdvice.OVERVIEW, java.util.Map.of()));
            KeptBench.Page ordered = fx(() -> bench.build("Build, " + cat + ", orders dialled up, a card pinned", cat,
                    java.util.Map.of(first, 3L, second, 2L), java.util.Set.of(first)));
            pageCost(g, bench, overview, 39, KEPT_BUILD_MS, "KEPT_BUILD_MS");
            pageCost(g, bench, ordered, 39, KEPT_BUILD_MS, "KEPT_BUILD_MS");
        }
        pageCost(g, bench, fx(bench::construction), 43, KEPT_CONSTRUCTION_MS, "KEPT_CONSTRUCTION_MS");
        KeptCheck.stopSites();
    }

    /**
     * Section 52: the kept month on the Sectors list and on the three business pages that weigh most, with the plants held and a works
     * standing (KeptCheck's 47 to 54), the clock running at the ladder's top rung as section 51 leaves it.
     */
    static void sectorsCost(Game g, KeptBench bench) throws Exception {
        System.out.println("\n  " + fx(() -> KeptCheck.ensureWorks(g)));
        KeptCheck.SectorKinds kinds = fx(() -> KeptCheck.sectorKinds(g));
        String plain = kinds.plain(), works = kinds.materials();
        check("the city has a plain business with a plant standing (" + plain + ")", plain != null);
        check("...and a materials industry with works standing (" + works + ")", works != null);
        if (plain == null || works == null) return;
        List<String> heldHere = fx(() -> {
            List<String> held = new ArrayList<>();
            for (Sector sector : g.getSectors().all()) {
                if (!g.getBusinessInvestment().isHeld(sector.key())) {
                    g.getBusinessInvestment().holdSector(sector.key());
                    held.add(sector.key());
                }
            }
            return held;
        });
        final String plainName = KeptCheck.called(g, plain), worksName = KeptCheck.called(g, works), shopsName = KeptCheck.called(g, Sectors.RETAIL);
        final String ops = KeptBench.sectorPages().get(0);
        fx(() -> {
            bench.clock(true, null);
            bench.speed(KeptBench.speedLadder().length - 1);
            return null;
        });
        KeptBench.Page list = fx(bench::sectors);
        KeptCheck.arrive(bench, list);
        pageCost(g, bench, list, 52, KEPT_SECTORS_MS, "KEPT_SECTORS_MS");
        for (KeptBench.Page p : List.of(
                fx(() -> bench.sectors(KeptCheck.possessive(shopsName) + " Operations page", Sectors.RETAIL, ops, false)),
                fx(() -> bench.sectors(KeptCheck.possessive(plainName) + " Income page as a statement", plain, "Income", true)),
                fx(() -> bench.sectors(KeptCheck.possessive(worksName) + " Operations page", works, ops, false)))) {
            pageCost(g, bench, p, 52, KEPT_SECTOR_MS, "KEPT_SECTOR_MS");
        }
        fx(() -> {
            for (String key : heldHere) g.getBusinessInvestment().releaseSector(key);
            return null;
        });
    }

    /** The page of a tab that weighs most (the nodes its first draw makes), by name: printed with the others. */
    static KeptBench.Page heaviest(KeptBench bench, String tab, List<KeptBench.Page> pages) throws Exception {
        KeptBench.Page best = null;
        int most = -1;
        StringBuilder line = new StringBuilder();
        for (KeptBench.Page p : pages) {
            int nodes = fx(() -> {
                bench.home(p);
                bench.drawKept(p);
                bench.passes();
                return bench.counts(p)[0];
            });
            line.append(line.length() == 0 ? "" : "; ").append(p.name()).append(" ").append(String.format("%,d", nodes));
            if (nodes > most) {
                most = nodes;
                best = p;
            }
        }
        System.out.println("  " + tab + ", nodes a page makes: " + line + " -> the heaviest is " + best.name());
        return best;
    }

    /**
     * Section 68: what the kept month costs on the heaviest page of the Bank, of Finances and of the fund (and the fund's Activity and
     * Search), the clock paused so that every month counts up, as KeptCheck's 55 to 67 draw them; the fund filled as section 61 fills it
     * when it holds nothing.
     */
    static void moneyCost(Game g, KeptBench bench) throws Exception {
        fx(() -> {
            bench.clock(false, null);
            bench.speed(0);
            return null;
        });
        if (fx(bench::fundHoldings).isEmpty()) {
            // The fund has nothing: paid in, and told to buy through the model as the ticket's button does (KeptCheck.fund()).
            fx(() -> KeptCheck.quietly(() -> {
                g.fundPayIn(Math.max(0, g.getCash() * .5));
                double each = g.fundCashFree() / 6;
                for (int c : new int[]{0, 1, 2, 3}) g.fundBuyShares(c, each, 0);
                List<CorporateBond> bonds = new ArrayList<>(g.getBondMarket().getBonds());
                for (int i = 0; i < Math.min(2, bonds.size()); i++) g.fundBuyBond(bonds.get(i).id(), each, 0);
                return null;
            }));
            fx(() -> {
                for (int i = 0; i < 3; i++) month(g);
                return null;
            });
        }
        System.out.println("\n  the fund holds " + fx(bench::fundHoldings).size() + " lots");
        KeptBench.Page bankOverview = fx(bench::bank);
        List<KeptBench.Page> bankPages = new ArrayList<>();
        bankPages.add(bankOverview);
        for (String p : KeptBench.bankPages()) bankPages.add(fx(() -> bench.bank("the Bank's " + p + " page", "pages", p, false)));
        bankPages.add(fx(() -> bench.bank("the Bank's Profit page as a statement", "pages", "Profit", true)));
        bankPages.add(fx(() -> bench.bank("the Bank's Balance sheet as a statement", "pages", "Balance sheet", true)));
        pageCost(g, bench, heaviest(bench, "the Bank", bankPages), 68, KEPT_BANK_MS, "KEPT_BANK_MS");

        KeptBench.Page financesHub = fx(bench::finances);
        List<KeptBench.Page> financePages = new ArrayList<>();
        financePages.add(financesHub);
        for (String area : KeptBench.financeAreas()) {
            if (area.equals("The city's fund")) continue;
            for (String p : KeptBench.financePages(area)) {
                if (p.equals("A bond's book")) continue;
                financePages.add(fx(() -> bench.finances(area + ", " + p, area, p)));
            }
        }
        pageCost(g, bench, heaviest(bench, "Finances", financePages), 68, KEPT_FINANCES_MS, "KEPT_FINANCES_MS");
        // ...and Borrow with an ask, which draws the auction and its picture besides.
        KeptBench.Page borrow = fx(() -> bench.finances("Borrow at home, asking for a sixth of the cash", "Borrow", "At home"));
        double ask = fx(() -> Math.max(g.minimumIssueSize() * 4, g.getCash() / 6));
        fx(() -> {
            bench.financesBorrow("Term", 10, KeptBench.borrowMoneys().get(0), ask, false, false);
            return null;
        });
        pageCost(g, bench, borrow, 68, KEPT_FINANCES_MS, "KEPT_FINANCES_MS");

        fx(() -> {
            bench.fundSearch("", "All");
            bench.fundActivity("All", KeptCheck.fundDial("ACTIVITY_MONTHS"));
            bench.fundFoldsClosed();
            return null;
        });
        List<KeptBench.Page> fundPages = new ArrayList<>();
        for (String p : KeptBench.financePages("The city's fund")) fundPages.add(fx(() -> bench.finances("the fund's " + p, "The city's fund", p)));
        fx(() -> {
            bench.fundOnShow(true);
            return null;
        });
        KeptBench.Page heavy = heaviest(bench, "the fund", fundPages);
        for (KeptBench.Page p : List.of(fundPages.get(1), fundPages.get(2))) {
            pageCost(g, bench, p, 68, KEPT_FUND_MS, "KEPT_FUND_MS", 5);
        }
        check("the fund's heaviest page is Search or Activity, the two costed (" + heavy.name() + ")", heavy == fundPages.get(1) || heavy == fundPages.get(2));
        fx(() -> {
            bench.fundOnShow(false);
            return null;
        });
    }

    /** Section 81: what the kept month costs on the Government Overview, on Trade's Overview and its heaviest page, and on Policy's hub and its dial-heavy pages. */
    static void kp6Cost(Game g, KeptBench bench) throws Exception {
        fx(() -> {
            bench.clock(false, null);
            bench.speed(0);
            return null;
        });
        pageCost(g, bench, fx(bench::government), 81, KEPT_GOVERNMENT_MS, "KEPT_GOVERNMENT_MS");

        List<KeptBench.Page> tradePages = new ArrayList<>();
        for (String p : KeptBench.tradePages()) tradePages.add(fx(() -> bench.trade("the Trade tab's " + p + " page", p)));
        pageCost(g, bench, tradePages.get(0), 81, KEPT_TRADE_MS, "KEPT_TRADE_MS");
        KeptBench.Page heavy = heaviest(bench, "Trade", tradePages);
        if (heavy != tradePages.get(0)) pageCost(g, bench, heavy, 81, KEPT_TRADE_MS, "KEPT_TRADE_MS");

        pageCost(g, bench, fx(bench::policy), 81, KEPT_POLICY_MS, "KEPT_POLICY_MS");
        pageCost(g, bench, fx(() -> bench.policy("Policy, Taxes' Profit page", "Taxes", "Profit")), 81, KEPT_POLICY_MS, "KEPT_POLICY_MS");
        pageCost(g, bench, fx(() -> bench.policy("Policy, Money's policy rate page", "Money", "The policy rate")), 81, KEPT_POLICY_MS, "KEPT_POLICY_MS");
    }

    /**
     * Section 90: what the kept month costs on Household money - as it opens and with the first family cell's books open - and on the Rankings tab - as
     * it opens and with every measure unfolded - against the same month built new.
     */
    static void kp7Cost(Game g, KeptBench bench) throws Exception {
        fx(() -> {
            bench.clock(false, null);
            bench.speed(0);
            return null;
        });
        String cell = fx(bench::householdCells).get(0);
        KeptBench.Page money = fx(bench::householdMoney);
        KeptBench.Page open = fx(() -> bench.withHome(money, "Household money with the cell " + cell + " open", () -> bench.householdCell(cell)));
        pageCost(g, bench, money, 90, KEPT_HOUSEHOLD_MS, "KEPT_HOUSEHOLD_MS");
        pageCost(g, bench, open, 90, KEPT_HOUSEHOLD_MS, "KEPT_HOUSEHOLD_MS");
        KeptBench.Page rankings = fx(bench::rankings);
        KeptBench.Page unfolded = fx(() -> bench.withHome(rankings, "the Rankings tab with every measure open", () -> bench.rankingsBrowseAll(true)));
        pageCost(g, bench, rankings, 90, KEPT_RANKINGS_MS, "KEPT_RANKINGS_MS");
        pageCost(g, bench, unfolded, 90, KEPT_RANKINGS_MS, "KEPT_RANKINGS_MS");
        fx(() -> {
            bench.home(money);
            bench.home(rankings);
            return null;
        });
    }

    /** Section n: what the kept month on the page's home costs, against the same month built new. */
    static void pageCost(Game g, KeptBench bench, KeptBench.Page page, int n, double budget, String dial) throws Exception {
        pageCost(g, bench, page, n, budget, dial, 9);
    }

    /** ...the median of `samples` months, also used to calibrate the fund's two list pages. */
    static void pageCost(Game g, KeptBench bench, KeptBench.Page page, int n, double budget, String dial, int samples) throws Exception {
        System.out.println("\n--- " + n + ": what the kept month on " + page.name() + " costs ---");
        double[] cost = fx(() -> {
            bench.home(page);
            bench.drawKept(page);
            for (int i = 0; i < 3; i++) bench.passes();
            warm(g, () -> {
                bench.drawKept(page);
                bench.passes();
            });
            List<Double> kept = new ArrayList<>(), fresh = new ArrayList<>();
            for (int i = 0; i < samples; i++) {
                month(g);
                double draw = bench.drawKept(page);
                double[] p = bench.passes();
                kept.add(draw + p[0] + p[1]);
            }
            // The same month built new: a page made from nothing in the column, as the redraw was before KP2.
            for (int i = 0; i < samples; i++) {
                month(g);
                double[] f = bench.showFresh(page);
                fresh.add(f[0] + f[1] + f[2]);
                bench.restoreKeptPage();
            }
            return new double[]{median(kept), median(fresh)};
        });
        System.out.printf("  the page's month (its draw, CSS and layout), median of %d: kept %.1f ms, built new %.1f ms%n", samples, cost[0], cost[1]);
        timed(cost[0], budget, "the kept month on " + page.name() + " is within " + dial + " (" + budget + " ms)");
    }
}
