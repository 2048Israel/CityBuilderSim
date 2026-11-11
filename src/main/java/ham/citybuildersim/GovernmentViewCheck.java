package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;

/**
 * The government's wealth bands and advice, measured without moving its books (GV1).
 *
 * WHY. A ratio that omits the vault, counts the fund's cash twice, annualises
 * an unfinished history or changes the market by being read misleads a player.
 * 1. Boundaries and missing revenue. 2. Every asset and the debt counted once.
 * 3. The last twelve months, including folded history. 4. Existing policy
 * previews and buyback quotes, without changing cash, dials or debt.
 * 5. A save restores the same reading before another month passes.
 */
public final class GovernmentViewCheck {
    static int fails;
    static void check(String label, boolean ok) {
        System.out.printf("%-100s %s%n", label, ok ? "OK" : "FAIL");
        if (!ok) fails++;
    }
    static void near(String label, double a, double b) {
        check(label, Double.isFinite(a) && Double.isFinite(b)
                && Math.abs(a - b) <= 1e-9 * Math.max(1, Math.abs(b)));
    }
    static void quiet(Runnable body) {
        PrintStream out = System.out;
        try (PrintStream sink = new PrintStream(OutputStream.nullOutputStream())) {
            System.setOut(sink);
            body.run();
        } finally { System.setOut(out); }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("--- 1. the five bands, their exact boundaries, and no comparable year ---");
        check("negative net wealth is broke", GovernmentView.bandFor(-1) == GovernmentView.Band.BROKE);
        check("a tenth is thin", GovernmentView.bandFor(GovernmentView.THIN_MIN) == GovernmentView.Band.THIN);
        check("half a year is sound", GovernmentView.bandFor(GovernmentView.SOUND_MIN) == GovernmentView.Band.SOUND);
        check("two years is still sound", GovernmentView.bandFor(GovernmentView.RICH_MIN) == GovernmentView.Band.SOUND);
        check("just over two is rich", GovernmentView.bandFor(Math.nextUp(GovernmentView.RICH_MIN)) == GovernmentView.Band.RICH);
        check("five years is still rich", GovernmentView.bandFor(GovernmentView.HOARDING_MIN) == GovernmentView.Band.RICH);
        check("just over five is hoarding", GovernmentView.bandFor(Math.nextUp(GovernmentView.HOARDING_MIN)) == GovernmentView.Band.HOARDING);
        check("NaN and infinity are not a wealth band", GovernmentView.bandFor(Double.NaN) == null
                && GovernmentView.bandFor(Double.POSITIVE_INFINITY) == null);
        GameFiles files = GameFiles.scratch("governmentviewcheck");
        Game g = new Game(files);
        quiet(g::run);
        check("a new city has no invented annual revenue", Double.isNaN(GovernmentView.of(g).wealthYears()));
        HistorySave h = g.getHistorySave();
        for (int m = 1; m < GovernmentView.REVENUE_MONTHS; m++) h.appendMonth(m, key -> "revenue".equals(key) ? 100 : 0);
        check("eleven recorded months are not treated as twelve", GovernmentView.of(g).band() == null);
        h.appendMonth(GovernmentView.REVENUE_MONTHS, key -> "revenue".equals(key) ? 100 : 0);
        near("the first full year is its twelve receipts", GovernmentView.of(g).annualRevenue(), 100 * GovernmentView.REVENUE_MONTHS);

        System.out.println("--- 2. the asset bridge and the debt counted once ---");
        quiet(() -> {
            g.setCashForTest(100_000);
            g.fundPayIn(10_000);
            g.getEquity().issueToCityRescue(Equity.BANK, 100);
            g.getDebtManager().getDebt().add(new LongTermBond(1_000, 120, g.getMonth(), .04));
        });
        GovernmentView v = GovernmentView.of(g);
        check("fixture: treasury, fund cash, vault and paper are all material", v.treasury() > 0 && v.fundCash() > 0
                && v.vault() > 0 && v.debt() > 0);
        check("fixture: the fund owns a marked holding, not cash alone", v.fundHoldings() > 0);
        near("the fund's cash is counted once alongside its marked holdings", v.fundCash() + v.fundHoldings(), g.fundValue());
        near("the vault is converted to local money", v.vault(), g.getForeignAccounts().getReserves());
        near("net wealth is the full bridge", v.netWealth(), g.getCash() + g.fundValue()
                + g.getForeignAccounts().getReserves() - g.getDebtManager().getAllPrincipal());
        near("years uses the recorded revenue", v.wealthYears(), v.netWealth() / v.annualRevenue());
        double oldNet = v.netWealth();
        quiet(() -> g.fundPayIn(500));
        near("a treasury-to-fund transfer does not make government wealth", GovernmentView.of(g).netWealth(), oldNet);

        System.out.println("--- 3. trailing revenue, zero receipts, and old folded history ---");
        h.appendMonth(13, key -> "revenue".equals(key) ? 400 : 0);
        near("month thirteen replaces the oldest month", GovernmentView.of(g).annualRevenue(), 11 * 100 + 400);
        for (int m = 14; m <= 25; m++) h.appendMonth(m, key -> 0);
        check("twelve zero receipts yield no band, not infinite wealth", GovernmentView.of(g).annualRevenue() == 0
                && GovernmentView.of(g).band() == null);
        for (int m = 26; m <= HistorySave.MONTHLY_KEPT + 24; m++) {
            final double receipt = m;
            h.appendMonth(m, key -> "revenue".equals(key) ? receipt : 0);
        }
        v = GovernmentView.of(g);
        double lastReceipt = HistorySave.MONTHLY_KEPT + 24;
        double tailReceipts = GovernmentView.REVENUE_MONTHS * (2 * lastReceipt - GovernmentView.REVENUE_MONTHS + 1) / 2;
        near("old folded years are excluded from the last twelve months", v.annualRevenue(),
                tailReceipts);

        System.out.println("--- 4. advice reads the current policy and quote, never moving either ---");
        // The stored last strike deliberately differs from a new strike. A
        // reader calling quoteBuyback() would overwrite this rate and inputs.
        double[] savedMarket = g.getDebtManager().marketToSave();
        double[] market = savedMarket.clone();
        market[4] += 1;
        g.getDebtManager().restoreMarket(market);
        double cash = g.getCash(), fundCash = g.getFund().getCash(), owed = g.getDebtManager().getAllPrincipal();
        TaxPolicy live = g.getEconomyManager().getTaxPolicy();
        double profit = live.getProfitTaxRate(), sales = live.getSalesTaxRate(), pension = live.getPensionReplacement();
        v = GovernmentView.of(g);
        check("fixture: coupon-bearing paper and cash above debt make a buyback available", v.buybackAvailable());
        if (v.buybackAvailable()) {
            Debt paper = v.buybackDebt();
            Buyback.Result q = g.quoteBuybackAtRest(paper, g.buybackOfferable(paper), Double.NaN);
            near("monthly saving is the quoted face's coupon stopped", v.buybackMonthlySaving(), g.honestLine(paper, q).couponStopped());
        }
        check("a cut and a transfer preview are never negative", v.taxCutMonthly() >= 0 && v.transferRaiseMonthly() >= 0);
        near("advice leaves treasury cash alone", g.getCash(), cash);
        near("advice leaves fund cash alone", g.getFund().getCash(), fundCash);
        near("advice leaves debt alone", g.getDebtManager().getAllPrincipal(), owed);
        near("advice leaves profit tax alone", live.getProfitTaxRate(), profit);
        near("advice leaves sales tax alone", live.getSalesTaxRate(), sales);
        near("advice leaves pensions alone", live.getPensionReplacement(), pension);
        near("advice leaves a deliberately stale standing rate alone", g.getInterestRate(), market[4]);
        check("advice preserves every saved market input and its last strike", Arrays.equals(market, g.getDebtManager().marketToSave()));
        g.getDebtManager().restoreMarket(savedMarket);
        g.setCashForTest(owed);
        check("cash equal to debt is not an excess-cash buyback", !GovernmentView.of(g).buybackAvailable());
        g.getDebtManager().getDebt().clear();
        ShortTermTBill bill = new ShortTermTBill(1_000, 12, g.getMonth());
        g.getDebtManager().getDebt().add(bill);
        g.setCashForTest(100_000);
        Buyback.Result billQuote = g.quoteBuybackAtRest(bill, g.buybackOfferable(bill), Double.NaN);
        check("fixture: a bill offers no coupon and costs less than its maturity payment", bill.getMonthlyCoupon() == 0
                && billQuote.takenFace() > 0 && billQuote.paid() < billQuote.takenFace());
        check("positive-yield discount paper is offered for buyback despite no coupon", GovernmentView.of(g).buybackDebt() == bill);
        near("a bill invents no monthly coupon saving", GovernmentView.of(g).buybackMonthlySaving(), 0);

        g.getDebtManager().getDebt().clear();
        LongTermBond premium = new LongTermBond(1_000, 120, g.getMonth(), g.getDebtManager().curveRate(120) + .10);
        g.getDebtManager().getDebt().add(premium);
        Buyback.Result whole = g.quoteBuybackAtRest(premium, g.buybackOfferable(premium), Double.NaN);
        Buyback.Result quarter = g.quoteBuybackAtRest(premium, premium.getOustandingPrincipal() / 4, Double.NaN);
        double affordableFloor = Math.max(g.getDebtManager().getAllPrincipal(), quarter.paid());
        g.setCashForTest((affordableFloor + whole.paid()) / 2);
        System.out.printf("  premium quotes: whole %.3f, quarter %.3f, principal %.3f, cash %.3f%n",
                whole.paid(), quarter.paid(), g.getDebtManager().getAllPrincipal(), g.getCash());
        check("fixture: cash exceeds face debt, but only part of a premium bond is affordable", g.getCash() > g.getDebtManager().getAllPrincipal()
                && whole.paid() > g.getCash() && quarter.takenFace() > 0 && quarter.paid() <= g.getCash());
        cash = g.getCash();
        v = GovernmentView.of(g);
        check("an affordable partial premium buyback is advised", v.buybackDebt() == premium
                && v.buybackMonthlySaving() > 0 && v.buybackMonthlySaving() < premium.getMonthlyCoupon());
        near("quoting the affordable part spends no cash", g.getCash(), cash);
        g.setCashForTest(0);
        check("no treasury cash means no buyback advice", !GovernmentView.of(g).buybackAvailable());
        g.setCashForTest(cash);
        policyAdvice();

        System.out.println("--- 5. the same reading after a save, before another month ---");
        GovernmentView before = GovernmentView.of(g);
        quiet(() -> g.saveGame(10, "GV1 scratch"));
        Game loaded = new Game(files);
        quiet(() -> loaded.loadGameSave(10));
        GovernmentView after = GovernmentView.of(loaded);
        near("the loaded city has the same net wealth", after.netWealth(), before.netWealth());
        near("the loaded history has the same annual revenue", after.annualRevenue(), before.annualRevenue());
        check("the loaded city starts in the same band", after.band() == before.band());
        System.out.println(fails == 0 ? "All checks passed." : fails + " FAILED");
        if (fails != 0) System.exit(1);
    }

    /** A played policy fixture supplies positive taxable books, followed by explicit pension and EI populations. */
    static void policyAdvice() throws Exception {
        System.out.println("--- positive tax and transfer advice on the model's own previews ---");
        Game city;
        PrintStream priorOut = PolicyPreviewCheck.out, priorQuiet = PolicyPreviewCheck.quiet;
        try (PrintStream sink = new PrintStream(OutputStream.nullOutputStream())) {
            PolicyPreviewCheck.out = System.out;
            PolicyPreviewCheck.quiet = sink;
            city = PolicyPreviewCheck.city(TempDirs.create("governmentview-advice"));
        } finally {
            PolicyPreviewCheck.out = priorOut;
            PolicyPreviewCheck.quiet = priorQuiet;
        }
        city.setCashForTest(0);
        TaxPolicy live = city.getEconomyManager().getTaxPolicy();
        String[] names = { "Business tax", "Sales tax", "Wage tax", "Property tax" };
        for (int family = 0; family < names.length; family++) {
            live.setProfitTaxRate(0); live.setSalesTaxRate(0); live.setWageTaxRate(0); live.setPropertyTaxRate(0);
            setTax(live, family, 3 * GovernmentView.TAX_CUT_STEP);
            PolicyPreview.TaxTake take = PolicyPreview.taxTake(city, live);
            double maximum = Math.max(Math.max(take.profitTotal(), take.salesTotal()), Math.max(take.wageTotal(), take.propertyTotal()));
            TaxPolicy cut = live.copy();
            setTax(cut, family, 2 * GovernmentView.TAX_CUT_STEP);
            double expectedCut = take.total() - PolicyPreview.taxTake(city, cut).total();
            check("fixture: " + names[family] + " has a positive take and a positive one-point cut", maximum > 0 && expectedCut > 0);
            double[] policy = live.getPolicyState();
            GovernmentView view = GovernmentView.of(city);
            check("the largest tax is selected: " + names[family], names[family].equals(view.largestTaxName()));
            near("the selected tax's current take: " + names[family], view.largestTaxMonthly(), maximum);
            near("the exact one-point revenue change: " + names[family], view.taxCutMonthly(), expectedCut);
            check("the tax preview leaves every policy dial alone: " + names[family], Arrays.equals(policy, live.getPolicyState()));
        }
        live.setProfitTaxRate(0); live.setSalesTaxRate(0); live.setWageTaxRate(0); live.setPropertyTaxRate(0);
        GovernmentView noTax = GovernmentView.of(city);
        check("zero tax receipts offer no invented tax cut", noTax.largestTaxName() == null && noTax.taxCutMonthly() == 0);

        city.getEconomyManager().setSeniors(100);
        Unemployment pool = city.getUnemployment();
        pool.reset();
        double[] empty = new double[PayTier.values().length];
        double wage = PayTier.UNSKILLED.getMonthlyWage();
        double[] wages = new double[PayTier.values().length];
        Arrays.fill(wages, wage);
        live.setPensionReplacement(TaxPolicy.MAX_REPLACEMENT / 2);
        live.setEiBenefitRate(TaxPolicy.MAX_EI_BENEFIT / 2);
        pool.advanceMonth(100, empty, empty, wages, wage, 0, live.getEiBenefitRate());
        check("fixture: pensioners and EI claimants both draw positive transfers", city.getEconomyManager().pensionsPaidUnder(live) > 0
                && pool.benefitsAt(live.getEiBenefitRate()) > 0);
        checkTransfer(city, null);
        live.setPensionReplacement(TaxPolicy.MAX_REPLACEMENT);
        checkTransfer(city, "EI");
        live.setPensionReplacement(TaxPolicy.MAX_REPLACEMENT / 2);
        live.setEiBenefitRate(TaxPolicy.MAX_EI_BENEFIT);
        checkTransfer(city, "Pensions");
        live.setPensionReplacement(TaxPolicy.MAX_REPLACEMENT);
        GovernmentView capped = GovernmentView.of(city);
        check("capped transfers offer no invented increase", capped.transferName() == null && capped.transferRaiseMonthly() == 0);
    }

    static void setTax(TaxPolicy policy, int family, double rate) {
        switch (family) {
            case 0 -> policy.setProfitTaxRate(rate);
            case 1 -> policy.setSalesTaxRate(rate);
            case 2 -> policy.setWageTaxRate(rate);
            case 3 -> policy.setPropertyTaxRate(rate);
            default -> throw new AssertionError(family);
        }
    }

    /** Both alternatives are positive in the initial fixture; caps then force each choice independently. */
    static void checkTransfer(Game city, String forced) {
        EconomyManager em = city.getEconomyManager();
        TaxPolicy live = em.getTaxPolicy(), pension = live.copy(), ei = live.copy();
        pension.setPensionReplacement(live.getPensionReplacement() + GovernmentView.TRANSFER_RAISE_STEP);
        ei.setEiBenefitRate(live.getEiBenefitRate() + GovernmentView.TRANSFER_RAISE_STEP);
        double pensionNow = em.pensionsPaidUnder(live), eiNow = city.getUnemployment().benefitsAt(live.getEiBenefitRate());
        double pensionRaise = em.pensionsPaidUnder(pension) - pensionNow;
        double eiRaise = city.getUnemployment().benefitsAt(ei.getEiBenefitRate()) - eiNow;
        if (forced == null) check("both five-point transfer previews have a positive cost", pensionRaise > 0 && eiRaise > 0);
        String expected = forced == null ? (eiRaise > pensionRaise ? "EI" : "Pensions") : forced;
        double[] policy = live.getPolicyState();
        GovernmentView view = GovernmentView.of(city);
        check("the positive transfer alternative is selected: " + expected, expected.equals(view.transferName()));
        near("the selected transfer's current monthly bill: " + expected, view.transferMonthly(), "EI".equals(expected) ? eiNow : pensionNow);
        near("the exact five-point transfer increase: " + expected, view.transferRaiseMonthly(), "EI".equals(expected) ? eiRaise : pensionRaise);
        check("the transfer preview leaves every policy dial alone: " + expected, Arrays.equals(policy, live.getPolicyState()));
    }
}
