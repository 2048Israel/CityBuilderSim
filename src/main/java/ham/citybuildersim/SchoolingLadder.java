package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * What each qualification pays after its course's cost, as a level monthly wage.
 *
 * WHY (WG3/WS2). Gross wages promised a doctor's salary to an unlicensed degree
 * holder and made four years out of work free. This is spec-wages 3.5's ladder.py,
 * with HS2's remaining adult stages in place of its single adult lifetime.
 * WageLadder remains the historical founding wage table, with its APIs intact.
 * Prices and wages are held at today's level: this is a comparison, not a forecast.
 *
 * A row is its whole path's value: every fee along it (a degree's high-school bill, a
 * licence's university bill) and its earnings once its courses end. The wages a student
 * gives up are what the step below earns meanwhile, so they are in the gap between two
 * rows already; WG3's first cut also took them off the row, as ladder.py did, which
 * counted them twice (Becker 1964: the cost of schooling is its fees and the earnings
 * forgone, once) and left a degree paying 1.2 diplomas worth less than the diploma.
 */
public final class SchoolingLadder {
    private SchoolingLadder() { }

    /** A family's annual price of waiting when borrowing is cheaper, an estimate (spec-wages 3.5). */
    public static final double FLOOR_RATE = .03;

    /** A return reaching full willingness counts as far above the prerequisite (Education's curve). */
    public static final double FAR_RETURN = 1;

    public enum Verdict {
        BASE("No course to pay for"),
        NO_POSTS("No posts here"),
        UNDER("At or under the step below: few will take it, and its holders will get scarce"),
        FAR("Far above the step below after cost: too few seats, or too dear"),
        ABOVE("Above the step below after cost");

        private final String words;
        Verdict(String words) { this.words = words; }
        public String words() { return words; }
    }

    /** One qualification; NONE is no diploma, HIGH is a diploma. fees and forgone are this course's own bill; afterCost is the path's. */
    public record Row(EducationType qualification, String label, JobType job,
                      double paid, double target, double afterCost, int months,
                      double fees, double forgone, int workingMonths, boolean eligible) { }

    /** An immutable snapshot, so every bar and verdict reads the same month's figures. */
    public record View(AdultStage stage, double annualRate, int workingMonths, List<Row> rows) {
        public View { rows = List.copyOf(rows); }

        public Row row(EducationType type) {
            return rows.stream().filter(r -> r.qualification() == type).findFirst().orElseThrow();
        }

        public Row before(EducationType type) {
            return row(prior(type));
        }

        /** The enrollment curve's input, before its exponent and cap. */
        public double gain(EducationType type) {
            Row next = row(type), current = before(type);
            return next.eligible() && current.afterCost() > 0
                    ? next.afterCost() / current.afterCost() - 1 : 0;
        }

        public Verdict verdict(EducationType type, long posts) {
            if (posts <= 0) return Verdict.NO_POSTS;
            if (type == EducationType.NONE) return Verdict.BASE;
            double gain = gain(type);
            return gain <= 0 ? Verdict.UNDER : gain >= FAR_RETURN ? Verdict.FAR : Verdict.ABOVE;
        }
    }

    /** The step a course is taken from: a licence from a degree, a degree or college from a diploma. */
    static EducationType prior(EducationType type) {
        return type.isProfessional() ? EducationType.UNIVERSITY
                : type == EducationType.HIGH ? EducationType.NONE : EducationType.HIGH;
    }

    /** Expected work left in HS2's compartments: a young stage still has the established stage ahead. */
    public static int workingMonths(AdultStage stage) {
        int months = 0;
        for (AdultStage s = stage; s != null; s = s.next()) months += s.spanMonths();
        return months;
    }

    /** A degree earns the best ungated wage in its band; a licence earns its own job's wage. */
    public static JobType bestJob(LabourMarket market, WageBand band) {
        JobType best = null;
        for (JobType job : JobType.values()) {
            if (WageBand.of(job) != band || PopulationManager.isGated(job)) continue;
            if (best == null || market.getWage(job) > market.getWage(best)) best = job;
        }
        return best;
    }

    public static View of(LabourMarket market, Education schools, TaxPolicy tax,
                          double policyRate, AdultStage stage) {
        return atRates(market, schools, tax == null ? 0 : tax.getStudentLoanRate(), policyRate, stage);
    }

    static View atRates(LabourMarket market, Education schools, double loanRate,
                        double policyRate, AdultStage stage) {
        double annual = Math.max(FLOOR_RATE, Math.max(loanRate, policyRate));
        double rate = annual / 12;
        int life = workingMonths(stage);
        double divisor = annuity(life, rate);
        List<Row> rows = new ArrayList<>();
        // Every fee along the path to each qualification, valued at 18: its prerequisites come first in the list.
        double[] pathFees = new double[EducationType.values().length];
        for (EducationType type : List.of(EducationType.NONE, EducationType.HIGH,
                EducationType.COLLEGE, EducationType.UNIVERSITY, EducationType.MEDICAL,
                EducationType.LAW, EducationType.BUSINESS, EducationType.ENGINEERING)) {
            WageBand band = type == EducationType.NONE ? WageBand.NONE : type.produces();
            JobType job = type.isProfessional() ? type.licenses() : bestJob(market, band);
            double wage = job == null ? 0 : market.getWage(job);
            int months = type == EducationType.NONE ? 0 : type.months();
            // As in ladder.py: a licence follows a degree's adult study; a diploma precedes adulthood.
            int start = type.isProfessional() ? EducationType.UNIVERSITY.months() : 0;
            int delay = type.isAdult() ? start + months : 0;
            int working = Math.max(0, life - delay);
            boolean eligible = !type.isAdult() || working > 0;
            double fee = type == EducationType.NONE ? 0 : schools.outOfPocket(type);
            JobType from = type.isAdult() ? bestJob(market, type.requires()) : null;
            double before = from == null ? 0 : market.getWage(from);
            double feesPV = fee * annuity(months, rate) * Math.pow(1 + rate,
                    type == EducationType.HIGH ? months : -start)
                    + (type == EducationType.NONE ? 0 : pathFees[prior(type).ordinal()]);
            pathFees[type.ordinal()] = feesPV;
            // No wages forgone here: the months out of work earn nothing below, and the step below earns them.
            double earningsPV = wage * annuity(working, rate) * Math.pow(1 + rate, -delay);
            double after = type == EducationType.NONE ? wage : (earningsPV - feesPV) / divisor;
            String label = type == EducationType.NONE ? "No diploma"
                    : type == EducationType.HIGH ? "Diploma" : type.getLabel();
            rows.add(new Row(type, label, job, wage, job == null ? 0
                    : market.targetWageAt(job, market.cashMinimumWage()), after, months,
                    fee * months, before * months, working, eligible));
        }
        return new View(stage, annual, life, rows);
    }

    private static double annuity(int months, double rate) {
        return months <= 0 ? 0 : -Math.expm1(-months * Math.log1p(rate)) / rate;
    }

    /** Posts for this qualification, excluding licensed jobs from a degree's prospects. */
    public static long posts(Row row, PopulationManager people) {
        long posts = 0;
        for (JobType job : JobType.values()) {
            if (row.qualification().isProfessional() ? job == row.job()
                    : WageBand.of(job) == WageBand.of(row.job()) && !PopulationManager.isGated(job)) {
                posts += people.getJobs()[job.ordinal()];
            }
        }
        return posts;
    }

    /** Band supply or recorded licence holders, matching the population model's existing inventories. */
    public static double holders(Row row, PopulationManager people) {
        return row.qualification().isProfessional() ? people.getLicensed(row.job())
                : people.workforceByBand()[WageBand.of(row.job()).ordinal()];
    }
}
