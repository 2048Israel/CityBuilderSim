package ham.citybuildersim;

/**
 * Proves the shared readable percentage rule (SX1), without the toolkit.
 * WHY: centuries of growth printed gains with sixteen digits; cost-basis
 * returns and ratios need multiples, while an observed price span can have a CAGR.
 */
public class FormatsCheck {
    static int fails;

    static void check(String label, boolean ok) {
        System.out.printf("%-85s %s%n", label, ok ? "OK" : "FAIL");
        if (!ok) fails++;
    }

    public static void main(String[] args) {
        Formats f = Formats.INSTANCE;
        System.out.println("--- 1. ordinary percentages keep their precision and sign ---");
        for (double v : new double[] {0, .0001, -.125, 1, Formats.LARGE_PERCENT}) {
            check("an ordinary percentage is unchanged: " + v,
                    f.percentage(v, "%,.1f%%").equals(String.format("%,.1f%%", v * 100)));
            check("an ordinary change is unchanged: " + v,
                    f.change(v, 7800, "%+,.1f%%").equals(String.format("%+,.1f%%", v * 100)));
        }
        check("a missing figure is a dash", f.percentage(Double.NaN, "%.1f%%").equals("—")
                && f.change(Double.POSITIVE_INFINITY, 12, "%+.1f%%").equals("—"));

        System.out.println("--- 2. yearly growth matches the closed form over the actual months ---");
        double gain = 4_548_682_160_882_199.0 / 100;
        int months = 7799;
        double annual = Math.pow(1 + gain, 12.0 / months) - 1;
        check("the centuries-long gain is its compound yearly rate",
                f.change(gain, months, "%+,.1f%%").equals(String.format("about %.1f%% a year", annual * 100)));
        check("an exact doubling every year reads as 100% a year",
                f.change(Math.pow(2, 20) - 1, 240, "%+.1f%%").equals("about 100.0% a year"));
        check("a short observation is a multiple, never extrapolated to a year",
                f.change(99, 1, "%+.1f%%").equals("×100"));

        System.out.println("--- 3. ratios and cost-basis returns are different multiples ---");
        check("a 12-fold ratio is twelve times its base", f.percentage(12, "%.1f%%").equals("×12"));
        check("a gain of eleven times the cost leaves twelve times the cost", f.change(11, 0, "%+.1f%%").equals("×12"));
        check("cost-basis gains have no invented investment date", !f.change(gain, 0, "%+.1f%%").contains("year"));
        check("trillions have a named scale", f.change(45e12 - 1, 0, "%+.1f%%").equals("×45 trillion"));
        check("a huge one-year gain is still bounded in length", f.change(1e200, 12, "%+.1f%%").length() < 30);
        check("a loss past the original cost keeps its negative meaning", f.change(-12, 0, "%+.1f%%").equals("×−12 of the base"));
        System.out.println(fails == 0 ? "All checks passed." : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
