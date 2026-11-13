package ham.citybuildersim;

import java.nio.file.Path;
import java.util.List;

/**
 * The played city's path premises, reported without counting a failure.
 *
 * WHY (PO1): a model change moved 465 path lines in 132 harnesses. A missed
 * premise must skip its dependent laws, while every law that runs stays hard.
 * Nightly records both reached and missed premises to retain each last hold.
 */
public final class Observation {
    private Observation() { }

    /** Nightly's mode records every reading, including holds, for merge attribution. */
    static final String PROPERTY = "cbs.observations";

    /** The shared nightly record, harness, label and REACHED or MISSED, tab-separated. */
    static final String TSV_PROPERTY = "cbs.observations.tsv";

    /** Every harness with path observations, also run nightly alongside HA1's tier. */
    static final List<String> HARNESSES = List.of(
            "ScaleCheck", "HoldersCheck", "SaveFileCheck", "CurrencyRegimeCheck",
            "AutoBuildCheck", "MonetaryCheck", "BondCheck", "FundCheck", "PriceLevelCheck",
            "DeskCheck", "FittingCheck", "LabourCheck", "BuildAdviceCheck", "AuctionCheck",
            "ConstructionControlCheck", "MapCheck", "CentralBankCheck", "KeptCheck",
            "PolicyPreviewCheck", "BankCheck", "HealthCheck", "ForeignCheck", "FloatLinkCheck",
            "CapitalFlowCheck", "CallCheck", "HomeRailCheck", "HousingCheck", "CurveCheck", "PrizeCheck");

    /** A path reading with no dependent laws, such as a sample share. */
    static boolean reached(String label, boolean reached) {
        return reached(label, reached, "");
    }

    /** Returns the actual premise, so callers run only laws whose premise held. */
    static boolean reached(String label, boolean reached, String laws) {
        return reached(label, label, reached, laws);
    }

    /** A stable line identity when the printed label contains that run's measured figures. */
    static boolean reached(String identity, String label, boolean reached, String laws) {
        String caller = StackWalker.getInstance().walk(frames -> frames
                .filter(f -> !f.getClassName().equals(Observation.class.getName()))
                .map(f -> f.getClassName().substring(f.getClassName().lastIndexOf('.') + 1))
                .findFirst().orElse("?"));
        if ("hard".equals(System.getProperty(PROPERTY))) {
            String file = System.getProperty(TSV_PROPERTY);
            if (file != null && !file.isBlank())
                Timing.append(Path.of(file), caller + "\t" + identity.replaceAll("[\\t\\r\\n]+", " ")
                        + "\t" + (reached ? "REACHED" : "MISSED") + "\n");
        }
        if (!reached) {
            System.out.println("OBSERVED - not reached: " + label);
            if (!laws.isBlank()) System.out.println("SKIPPED - " + laws + " because not reached: " + label);
        }
        return reached;
    }

    /** Forwards the mode and record to each harness JVM. */
    static void forwardTo(List<String> command) {
        for (String property : List.of(PROPERTY, TSV_PROPERTY)) {
            String value = System.getProperty(property);
            if (value != null) command.add("-D" + property + "=" + value);
        }
    }
}
