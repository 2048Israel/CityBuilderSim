package ham.citybuildersim;

import java.util.Arrays;

/**
 * Hand-summed monthly cash ledgers for every after-cost qualification (WG3).
 * WHY. A closed-form annuity repeated in a check could agree with the same
 * indexing error in the model; these sums book each month's wage and fee.
 * No city is played and no save folder is opened.
 */
public class SchoolingLadderCheck {
    static int fails;

    static void check(String label, boolean ok) {
        System.out.println(label + (ok ? " OK" : " FAIL"));
        if (!ok) fails++;
    }

    static void close(String label, double actual, double expected) {
        check(label + " (" + actual + ", expected " + expected + ")",
                Math.abs(actual - expected) < 1e-10 * Math.max(1, Math.abs(expected)));
    }

    static LabourMarket wages() {
        LabourMarket market = new LabourMarket();
        double[] wages = {1, 2, 2.8, 3, 2.9, 8, 7, 6, 4, 6.5, 3.8};
        System.arraycopy(wages, 0, market.getWages(), 0, wages.length);
        return market;
    }

    /**
     * The independent ledger of one path's cash: a payment in month m is worth
     * payment/(1+r)^m at 18, a teen's month (m <= 0) carried forward. The wage is
     * earned after month earnFrom; each course is {first month, last month, monthly fee}.
     * Nothing is booked for wages given up: a student earns nothing, which is the cost.
     */
    static double ledger(int life, double annual, double wage, int earnFrom, double[]... courses) {
        double value = 0, monthsValue = 0;
        for (int month = 1; month <= life; month++) {
            double discount = Math.pow(1 + annual / 12, -month);
            monthsValue += discount;
            if (month > earnFrom) value += wage * discount;
        }
        for (double[] course : courses)
            for (int month = (int) course[0]; month <= (int) course[1]; month++)
                value -= course[2] * Math.pow(1 + annual / 12, -month);
        return value / monthsValue;
    }

    /** An adult course taken at 18 from a free diploma, the shape EducationCheck borrows. */
    static double handSum(int life, int duration, double wage, double monthlyFee, double annual) {
        return ledger(life, annual, wage, duration, new double[]{1, duration, monthlyFee});
    }

    static PopulationCohorts stages(double young, double established) {
        PopulationCohorts pyramid = new PopulationCohorts();
        double[] state = pyramid.toSaveArray();
        int n = PopulationCohorts.saveBands().length;
        state[n + 7] = young;
        state[n + 8] = established;
        pyramid.restore(PopulationCohorts.saveBands(), state);
        return pyramid;
    }

    public static void main(String[] args) {
        System.out.println("--- Every qualification against a monthly cash ledger ---");
        LabourMarket market = wages();
        Education schools = new Education();
        schools.setTuitionSubsidy(.25);
        TaxPolicy tax = new TaxPolicy();
        tax.setStudentLoanRate(.06);
        EducationType[] steps = {EducationType.NONE, EducationType.HIGH, EducationType.COLLEGE,
                EducationType.UNIVERSITY, EducationType.MEDICAL, EducationType.LAW,
                EducationType.BUSINESS, EducationType.ENGINEERING};
        // These prospects are deliberately listed independently of the helper's job selection.
        double[] paid = {1, 2, 3, 4, 8, 7, 6, 6.5};
        double[] before = {0, 0, 2, 2, 4, 4, 4, 4};
        int hs = EducationType.HIGH.months(), uni = EducationType.UNIVERSITY.months();
        double hsFee = schools.feeFor(EducationType.HIGH) * (1 - schools.getTuitionSubsidy());
        double uniFee = schools.feeFor(EducationType.UNIVERSITY) * (1 - schools.getTuitionSubsidy());
        double[] teen = {1 - hs, 0, hsFee}, degreeBill = {1, uni, uniFee};
        check("fixture: the bills along each path are not free", hsFee > 0 && uniFee > 0);
        for (AdultStage stage : AdultStage.values()) {
            int life = stage == AdultStage.YOUNG
                    ? AdultStage.YOUNG.spanMonths() + AdultStage.ESTABLISHED.spanMonths()
                    : AdultStage.ESTABLISHED.spanMonths();
            SchoolingLadder.View view = SchoolingLadder.of(market, schools, tax, .04, stage);
            close(stage + " remaining work", view.workingMonths(), life);
            close(stage + " loan is dearer than policy", view.annualRate(), tax.getStudentLoanRate());
            for (int i = 0; i < steps.length; i++) {
                EducationType type = steps[i];
                SchoolingLadder.Row row = view.row(type);
                int duration = type == EducationType.NONE ? 0 : type.months();
                int start = i >= 4 ? EducationType.UNIVERSITY.months() : 0;
                double fee = i == 0 ? 0 : schools.feeFor(type) * (1 - schools.getTuitionSubsidy());
                close(stage + " " + type + " paid", row.paid(), paid[i]);
                close(stage + " " + type + " fee bill", row.fees(), fee * duration);
                close(stage + " " + type + " forgone wages", row.forgone(), before[i] * duration);
                double[] own = {start + 1, start + duration, fee};
                double r = tax.getStudentLoanRate();
                double path = i == 0 ? ledger(life, r, paid[i], 0)
                        : i == 1 ? ledger(life, r, paid[i], 0, teen)
                        : i < 4 ? ledger(life, r, paid[i], duration, teen, own)
                        : ledger(life, r, paid[i], uni + duration, teen, degreeBill, own);
                close(stage + " " + type + " after cost, every bill on its path", row.afterCost(), path);
            }
        }

        System.out.println("--- The wages given up are counted once ---");
        schools.setTuitionSubsidy(1);
        tax.setStudentLoanRate(0);
        int life = AdultStage.YOUNG.spanMonths() + AdultStage.ESTABLISHED.spanMonths();
        double r = SchoolingLadder.FLOOR_RATE, diploma = market.getWage(JobType.DIPLOMA);
        market.getWages()[JobType.UNIV_SCIENCE.ordinal()] = diploma;
        market.getWages()[JobType.UNIV_POLICY.ordinal()] = diploma;
        SchoolingLadder.View even = SchoolingLadder.of(market, schools, tax, 0, AdultStage.YOUNG);
        double fourYears = ledger(life, r, diploma, 0) - ledger(life, r, diploma, uni);
        close("a free degree paying the diploma's wage costs four years of it, once",
                even.row(EducationType.HIGH).afterCost() - even.row(EducationType.UNIVERSITY).afterCost(), fourYears);
        double breakEven = diploma * ledger(life, r, 1, 0) / ledger(life, r, 1, uni);
        market.getWages()[JobType.UNIV_SCIENCE.ordinal()] = breakEven;
        SchoolingLadder.View par = SchoolingLadder.of(market, schools, tax, 0, AdultStage.YOUNG);
        check("a degree paying the diploma's lifetime in fewer years breaks even (" + par.gain(EducationType.UNIVERSITY) + ")",
                Math.abs(par.gain(EducationType.UNIVERSITY)) < 1e-9);
        market.getWages()[JobType.UNIV_SCIENCE.ordinal()] = breakEven * 1.01;
        check("and a degree paying a little more is a gain", SchoolingLadder.of(market, schools, tax, 0,
                AdultStage.YOUNG).gain(EducationType.UNIVERSITY) > 0);
        market = wages();
        schools.setTuitionSubsidy(.25);

        System.out.println("--- Fees, time, wages and all three discount inputs cause a change ---");
        schools.setTuitionSubsidy(1);
        tax.setStudentLoanRate(0);
        SchoolingLadder.View free = SchoolingLadder.of(market, schools, tax, 0, AdultStage.YOUNG);
        check("fixture: the three percent floor applies", free.annualRate() == SchoolingLadder.FLOOR_RATE);
        check("no diploma has no course to pay for", free.verdict(EducationType.NONE, 1) == SchoolingLadder.Verdict.BASE);
        check("a modest lead is above, without claiming a far-above shortage", free.verdict(EducationType.COLLEGE, 1) == SchoolingLadder.Verdict.ABOVE);
        market.getWages()[JobType.COLLEGE_BUSINESS.ordinal()] = 8;
        check("a larger lead crosses the existing curve's full-return threshold", SchoolingLadder.of(market, schools, tax, 0,
                AdultStage.YOUNG).verdict(EducationType.COLLEGE, 1) == SchoolingLadder.Verdict.FAR);
        market.getWages()[JobType.COLLEGE_BUSINESS.ordinal()] = 3;
        schools.setTuitionSubsidy(0);
        SchoolingLadder.View paidFees = SchoolingLadder.of(market, schools, tax, 0, AdultStage.YOUNG);
        check("family fees lower the college's value", paidFees.row(EducationType.COLLEGE).afterCost()
                < free.row(EducationType.COLLEGE).afterCost());
        schools.setTuitionScaleOf(EducationType.COLLEGE, 3);
        check("a higher college fee lowers it again", SchoolingLadder.of(market, schools, tax, 0,
                AdultStage.YOUNG).row(EducationType.COLLEGE).afterCost() < paidFees.row(EducationType.COLLEGE).afterCost());
        schools.setTuitionSubsidy(1);
        // Equal wages isolate duration, with college and university both studied from a diploma.
        market.getWages()[JobType.UNIV_SCIENCE.ordinal()] = 3;
        market.getWages()[JobType.UNIV_POLICY.ordinal()] = 3;
        SchoolingLadder.View duration = SchoolingLadder.of(market, schools, tax, 0, AdultStage.YOUNG);
        check("fixture: university takes longer at the same pay", EducationType.UNIVERSITY.months() > EducationType.COLLEGE.months()
                && duration.row(EducationType.COLLEGE).paid() == duration.row(EducationType.UNIVERSITY).paid());
        check("the longer course is worth less", duration.row(EducationType.UNIVERSITY).afterCost()
                < duration.row(EducationType.COLLEGE).afterCost());
        double oldCollege = duration.row(EducationType.COLLEGE).afterCost();
        double oldGain = duration.gain(EducationType.COLLEGE), oldForgone = duration.row(EducationType.COLLEGE).forgone();
        market.getWages()[JobType.DIPLOMA.ordinal()] += 1;
        SchoolingLadder.View dearer = SchoolingLadder.of(market, schools, tax, 0, AdultStage.YOUNG);
        check("a higher studied-from wage raises the wages forgone and lowers the step's gain",
                dearer.row(EducationType.COLLEGE).forgone() > oldForgone && dearer.gain(EducationType.COLLEGE) < oldGain);
        close("while the college path's own value, which never earns the diploma's wage, holds",
                dearer.row(EducationType.COLLEGE).afterCost(), oldCollege);
        market = wages();
        double floorValue = SchoolingLadder.of(market, schools, tax, 0, AdultStage.YOUNG).row(EducationType.MEDICAL).afterCost();
        SchoolingLadder.View dearPolicy = SchoolingLadder.of(market, schools, tax, .09, AdultStage.YOUNG);
        check("policy above the floor discounts the long course more", dearPolicy.annualRate() == .09
                && dearPolicy.row(EducationType.MEDICAL).afterCost() < floorValue);
        tax.setStudentLoanRate(.12);
        SchoolingLadder.View dearLoan = SchoolingLadder.of(market, schools, tax, .09, AdultStage.YOUNG);
        check("loan above policy discounts it further", dearLoan.annualRate() == tax.getStudentLoanRate()
                && dearLoan.row(EducationType.MEDICAL).afterCost() < dearPolicy.row(EducationType.MEDICAL).afterCost());
        tax.setStudentLoanRate(0);
        SchoolingLadder.View older = SchoolingLadder.of(market, schools, tax, 0, AdultStage.ESTABLISHED);
        check("established adults can study but have less time to earn it back", older.row(EducationType.MEDICAL).eligible()
                && older.row(EducationType.MEDICAL).afterCost() < floorValue);
        double degree = older.row(EducationType.UNIVERSITY).afterCost();
        market.getWages()[JobType.UNIV_DOCTOR.ordinal()] *= 10;
        SchoolingLadder.View doctor = SchoolingLadder.of(market, schools, tax, 0, AdultStage.ESTABLISHED);
        close("a dear doctor does not promise a degree a licence", doctor.row(EducationType.UNIVERSITY).afterCost(), degree);
        check("the medical licence earns its own higher pay", doctor.row(EducationType.MEDICAL).afterCost()
                > older.row(EducationType.MEDICAL).afterCost());
        market.getWages()[JobType.UNIV_POLICY.ordinal()] = 5;
        check("an ungated policy job can become the degree's best prospect", SchoolingLadder.of(market, schools, tax, 0,
                AdultStage.YOUNG).row(EducationType.UNIVERSITY).job() == JobType.UNIV_POLICY);

        System.out.println("--- Posts, holders and verdicts ---");
        long[] posts = new long[JobType.values().length];
        posts[JobType.UNIV_SCIENCE.ordinal()] = 7;
        posts[JobType.UNIV_POLICY.ordinal()] = 11;
        posts[JobType.UNIV_DOCTOR.ordinal()] = 13;
        double[] heads = new double[WageBand.values().length];
        heads[WageBand.UNIVERSITY.ordinal()] = 80;
        double[] licences = new double[JobType.values().length];
        licences[JobType.UNIV_DOCTOR.ordinal()] = 9;
        PopulationManager people = LabourCheck.handFed(100, heads, posts, licences);
        SchoolingLadder.View view = SchoolingLadder.of(market, schools, tax, 0, AdultStage.YOUNG);
        close("degree posts exclude licensed professions", SchoolingLadder.posts(view.row(EducationType.UNIVERSITY), people), 18);
        close("medical posts are its own job", SchoolingLadder.posts(view.row(EducationType.MEDICAL), people), 13);
        close("degree holders read the band's supply", SchoolingLadder.holders(view.row(EducationType.UNIVERSITY), people), 80);
        close("medical holders read the licence", SchoolingLadder.holders(view.row(EducationType.MEDICAL), people), 9);
        check("no posts wins over a high return", view.verdict(EducationType.MEDICAL, 0) == SchoolingLadder.Verdict.NO_POSTS);
        check("a very dear doctor is far above", view.verdict(EducationType.MEDICAL, 13) == SchoolingLadder.Verdict.FAR);
        Arrays.fill(market.getWages(), 2);
        view = SchoolingLadder.of(market, schools, tax, 0, AdultStage.YOUNG);
        check("equal gross pay puts adult study below its prerequisite", view.verdict(EducationType.COLLEGE, 1) == SchoolingLadder.Verdict.UNDER);
        System.out.println(fails == 0 ? "All checks passed." : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
