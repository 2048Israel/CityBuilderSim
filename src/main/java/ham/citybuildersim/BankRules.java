package ham.citybuildersim;

/**
 * The city's saved banking rules, shared by lenders and both policy pages.
 * WHY (CBK3): the bank's prudential constants were invisible to the player;
 * exposing a rule must not quietly change its default or create a second model.
 */
public final class BankRules {
    /** No discretionary countercyclical buffer in the existing rule. */
    public static final double COUNTERCYCLICAL_DEFAULT = 0;
    /** No reserve requirement, matching the Fed since March 2020. */
    public static final double RESERVE_DEFAULT = 0;
    /** No additional debt-to-annual-income limit in the existing mortgage product. */
    public static final double MORTGAGE_DTI_DEFAULT = 0;

    /** Saved order: append new rules, never reorder existing ones. All rates are fractions. */
    public enum Dial {
        WINDOW_SPREAD("Window spread", CentralBank.WINDOW_PENALTY, 0, .05, .0025,
                "Added to the policy rate on collateral-backed overnight borrowing."),
        WINDOW_LIMIT("Window collateral share", Bank.WINDOW_COLLATERAL_SHARE, 0, 1, .05,
                "Maximum advance as a share of eligible collateral after haircuts; equity is ineligible."),
        CAPITAL_MINIMUM("Capital minimum", Bank.CAPITAL_RATIO, .04, .20, .005,
                "Minimum equity against the risk-weighted book. The conservation buffer remains on top."),
        COUNTERCYCLICAL("Countercyclical buffer", COUNTERCYCLICAL_DEFAULT, 0, .025, .0025,
                "Extra capital above the minimum and conservation buffer; Basel III's 0–2.5% range."),
        LEVERAGE_MINIMUM("Leverage floor", Bank.LEVERAGE_RATIO_MIN, .01, .10, .0025,
                "Minimum equity against all assets, including insured mortgages."),
        RESERVE_REQUIREMENT("Reserve requirement", RESERVE_DEFAULT, 0, .25, .01,
                "Deposits withheld from lending and the desk's core cash budget."),
        MORTGAGE_LTC("Mortgage loan / cost", Mortgage.MORTGAGE_MAX_LOAN_TO_COST, .50, .95, .01,
                "Maximum mortgage before its insurance premium as a share of the building's cost."),
        MORTGAGE_DTI("Mortgage debt / annual income", MORTGAGE_DTI_DEFAULT, 0, 20, .5,
                "Off at zero; otherwise mortgage principal divided by annual net rent. The payment coverage test still applies."),
        BUSINESS_LTA("Business loan / assets", BusinessDebtManager.MAX_LOAN_TO_ASSETS, .25, .95, .05,
                "Maximum business debt against the borrower's assets, alongside capital and default tests.");

        public final String label, explanation;
        public final double initial, min, max, step;
        Dial(String label, double initial, double min, double max, double step, String explanation) {
            this.label = label; this.initial = initial; this.min = min;
            this.max = max; this.step = step; this.explanation = explanation;
        }
    }

    private final double[] values = new double[Dial.values().length];
    private transient DecisionLog decisions;
    public BankRules() { reset(); }
    public void recordTo(DecisionLog log) { decisions = log; }
    public double get(Dial dial) { return values[dial.ordinal()]; }
    public void set(Dial dial, double value) {
        double before = get(dial);
        values[dial.ordinal()] = bounded(dial, value);
        if (decisions != null && before != get(dial))
            decisions.record("bankRules", dial.label + " → " + get(dial));
    }
    private static double bounded(Dial dial, double value) {
        return Double.isFinite(value) ? Math.max(dial.min, Math.min(dial.max, value)) : dial.initial;
    }
    public void reset() { for (Dial dial : Dial.values()) values[dial.ordinal()] = dial.initial; }
    public double[] toSave() { return values.clone(); }
    public void restore(double[] saved) {
        reset();
        if (saved != null) for (Dial dial : Dial.values())
            if (dial.ordinal() < saved.length) values[dial.ordinal()] = bounded(dial, saved[dial.ordinal()]);
    }

}
