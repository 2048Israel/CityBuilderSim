package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * The statistics office's debt figures against distributions and their component sums (HC2).
 * WHY: holders' medians and adults' denominators cannot be recovered from a city average.
 */
public final class DebtStatisticsCheck {
    static int fails;
    static void check(String label, boolean ok) {
        System.out.println((ok ? "OK   " : "FAIL ") + label); if (!ok) fails++;
    }
    static void close(String label, double got, double want, double relative) {
        check(label + " (" + got + "; expected " + want + ")",
                Math.abs(got - want) <= relative * Math.max(1e-12, Math.abs(want)));
    }
    static HouseholdBalance fixture() {
        HouseholdBalance b = new HouseholdBalance();
        Household a = b.cell(FamilyStructure.SINGLE_ADULT, PayTier.UNSKILLED, AdultStage.YOUNG);
        a.households = 100; a.debt = 2; a.revolving = .5; a.disposable = 3; a.savings = 4;
        a.lateDebt = .1; a.lateShare = .02; a.cardWrittenOff = .01; a.interest = .03; a.minimumPaid = .03; a.minimumDue = .06;
        Household c = b.cell(FamilyStructure.COUPLE, PayTier.COLLEGE);
        c.households = 200; c.debt = 4; c.revolving = .4; c.disposable = 8; c.savings = 20;
        c.studentDebt = 2; c.studentRepaid = 2.0 / Household.STUDENT_LOAN_MONTHS;
        c.lateDebt = .2; c.lateShare = .04; c.cardWrittenOff = .02; c.interest = .07; c.minimumPaid = .05; c.minimumDue = .12;
        return b;
    }
    static void quantiles() {
        System.out.println("--- Grid quantiles against bisection and drawn holders ---");
        List<DebtStatistics.Component> mixture = List.of(
                new DebtStatistics.Component(100, .5, 4, CellSpread.CARD_SIGMA),
                new DebtStatistics.Component(200, .4, 10, CellSpread.CARD_SIGMA));
        double lo = 0, hi = 10_000;
        for (int i = 0; i < 80; i++) {
            double mid = (lo + hi) / 2;
            if (DebtStatistics.cdf(mixture, mid) < .5) lo = mid; else hi = mid;
        }
        close("32-point grid median is within 1% of independent bisection", DebtStatistics.median(mixture), (lo + hi) / 2, .01);
        Random random = new Random(219);
        double[] amounts = new double[400_000];
        for (int i = 0; i < amounts.length; i++) {
            double mean = random.nextDouble() < 50.0 / 130 ? 4 : 10;
            double sigma = CellSpread.CARD_SIGMA;
            amounts[i] = mean * Math.exp(sigma * random.nextGaussian() - sigma * sigma / 2);
        }
        Arrays.sort(amounts);
        close("mixture median matches drawn cardholders", DebtStatistics.median(mixture), amounts[amounts.length / 2], .01);
        // The published total is explicitly an approximate lognormal, as the brief specifies.
        List<DebtStatistics.Component> totals = List.of(new DebtStatistics.Component(1, .7, 15, CellSpread.TOTAL_SIGMA));
        close("total approximation retains its own SCF mean/median", DebtStatistics.median(totals),
                15 * Math.exp(-CellSpread.TOTAL_SIGMA * CellSpread.TOTAL_SIGMA / 2), .01);
        close("no holders have no median", DebtStatistics.median(new ArrayList<>()), 0, 0);
    }
    static void figures() {
        System.out.println("--- Ratios use the same sums, with households and adults counted separately ---");
        HouseholdBalance b = fixture();
        DebtStatistics.Snapshot s = b.debtStatistics();
        DebtStatistics.Measure card = s.perHousehold().get(DebtStatistics.Kind.CARD);
        close("card holding share counts holders", card.shareHolding(), 130.0 / 300, 1e-12);
        close("card mean divides balances by holders", card.meanAmongHolders(), 1000.0 / 130, 1e-12);
        close("adult mean divides by the actual holder adults", s.perAdult().get(DebtStatistics.Kind.CARD).meanAmongHolders(), 1000.0 / 210, 1e-12);
        close("total debt to disposable income is the stock over annual income", s.debtToDisposableIncome(), 1400.0 / (12 * 1900), 1e-12);
        double service = 0;
        for (Household c : b.cells()) service += (c.minimumDue + c.studentRepaid + c.studentInterest) * c.households;
        close("debt service equals the sum of actual debt-service inputs", s.debtServiceRatio(), service / 1900, 1e-12);
        close("delinquency is late balances over all card balances", card.delinquency(), .05, 1e-12);
        close("late household share is people behind, not their balance weight", card.lateShare(), .1 / 3, 1e-12);
        close("annual charge-offs are twelve times this month's loss", card.annualChargeOff(), .06, 1e-12);
        check("holder-only mean is above the median", card.meanAmongHolders() > card.medianAmongHolders());
        close("cars' card borrowing is not counted again as a separate loan", s.perHousehold().get(DebtStatistics.Kind.CAR).shareHolding(), 0, 0);
        close("owner mortgages await HO1", s.perHousehold().get(DebtStatistics.Kind.MORTGAGE).shareHolding(), 0, 0);
        DebtStatistics.Snapshot couple = DebtStatistics.of(b, PayTier.COLLEGE, DebtStatistics.Stage.ESTABLISHED);
        close("per-adult median halves the couple's household median", couple.perAdult().get(DebtStatistics.Kind.CARD).medianAmongHolders(),
                couple.perHousehold().get(DebtStatistics.Kind.CARD).medianAmongHolders() / 2, .001);
        close("stage filter selects young families", DebtStatistics.of(b, null, DebtStatistics.Stage.YOUNG).households(), 100, 0);
        close("tier filter selects college families", DebtStatistics.of(b, PayTier.COLLEGE, null).households(), 200, 0);
        check("survey contains every tier and stage cross-tab", DebtStatistics.survey(b, 12).groups().containsKey("COLLEGE:ESTABLISHED"));
        Household poor = b.cell(FamilyStructure.SINGLE_ADULT, PayTier.UNSKILLED, AdultStage.YOUNG);
        poor.disposable = .01;
        check("income below the service line raises the over-40 share", b.debtStatistics().payingOver40() > s.payingOver40());
        check("debtor burden uses holders as its denominator", s.debtorsPayingOver40() >= s.payingOver40());
        b.setCarAssetPrice(() -> 10);
        poor.cars = .5;
        close("debt over assets includes the cars as well as financial holdings", b.debtStatistics().debtToAssets(), 1400.0 / 4900, 1e-12);
        poor.disposable = 3;
        poor.investmentIncome = 1; poor.depositIncome = .2;
        close("disposable income includes net property and actual deposit receipts", b.debtStatistics().debtToDisposableIncome(),
                1400.0 / (12 * 2020), 1e-12);
        DebtStatistics.Survey survey = DebtStatistics.survey(b, 12);
        DebtStatistics.Survey scaled = survey.scaled(.01);
        close("history's amounts follow a currency reform", scaled.groups().get("All").perHousehold().get(DebtStatistics.Kind.CARD).meanAmongHolders(),
                survey.groups().get("All").perHousehold().get(DebtStatistics.Kind.CARD).meanAmongHolders() * .01, 1e-12);
        close("history's debt/income ratio survives a currency reform", scaled.groups().get("All").debtToDisposableIncome(),
                survey.groups().get("All").debtToDisposableIncome(), 0);
        b.students().households = 10; b.students().studentDebt = 1;
        DebtStatistics.Survey withStudents = DebtStatistics.survey(b, 24);
        double byTier = withStudents.groups().get("UNTIERED").households();
        for (PayTier tier : PayTier.values()) byTier += withStudents.groups().get(tier.name()).households();
        close("tier groups plus the explicitly untiered residents foot to the city", byTier,
                withStudents.groups().get("All").households(), 0);
    }
    public static void main(String[] args) {
        quantiles(); figures();
        System.out.println("DebtStatisticsCheck: " + fails + " failures");
        System.exit(fails == 0 ? 0 : 1);
    }
}
