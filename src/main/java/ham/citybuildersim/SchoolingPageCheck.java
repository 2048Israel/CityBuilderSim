package ham.citybuildersim;

import ham.citybuildersim.ui.SchoolingProbe;
import java.util.Arrays;
import java.util.List;

/**
 * Compares Wages' actual qualification bars with the pure model (WG3/WS2).
 * WHY. Course costs may invert a ladder; a screen must preserve the figures,
 * the sign, the common scale, the no-posts state and the model's verdicts.
 */
public class SchoolingPageCheck {
    public static void main(String[] args) {
        int fails = 0;
        System.out.println("--- Qualification bars read the model, including inversions and no posts ---");
        LabourMarket market = SchoolingLadderCheck.wages();
        Education ed = new Education();
        TaxPolicy tax = new TaxPolicy();
        long[] posts = new long[JobType.values().length];
        Arrays.fill(posts, 10);
        double[] heads = new double[WageBand.values().length];
        Arrays.fill(heads, 100);
        PopulationManager people = LabourCheck.handFed(1_000, heads, posts, new double[JobType.values().length]);
        for (AdultStage stage : AdultStage.values()) {
            for (int scenario = 0; scenario < 3; scenario++) {
                if (scenario == 1) {
                    // A gross premium too small to cover time out of work, and one outright negative level.
                    Arrays.fill(market.getWages(), 2);
                    market.getWages()[JobType.UNIV_DOCTOR.ordinal()] = .01;
                    ed.setTuitionSubsidy(0);
                }
                if (scenario == 2) people.updateJobs(new long[JobType.values().length]);
                SchoolingLadder.View view = SchoolingLadder.of(market, ed, tax, .06, stage);
                boolean premise = scenario != 1 || view.row(EducationType.MEDICAL).afterCost() < 0
                        && view.row(EducationType.COLLEGE).afterCost() < view.row(EducationType.HIGH).afterCost();
                System.out.println("fixture: " + stage + " scenario " + scenario + (premise ? " OK" : " FAIL"));
                if (!premise) fails++;
                List<String> differences = SchoolingProbe.differences(view, people);
                System.out.println(stage + " scenario " + scenario + " bars, details and verdicts" + (differences.isEmpty() ? " OK" : " FAIL " + differences));
                if (!differences.isEmpty()) fails++;
                market = SchoolingLadderCheck.wages();
                people.updateJobs(posts);
            }
        }
        System.out.println(fails == 0 ? "All checks passed." : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
