package ham.citybuildersim;

/**
 * Proves the labour figures held for a pass and across a month survive every
 * writer, with the original walks compared as raw bits (AP5/AP6).
 *
 * WHY. LabourCheck's census and wage replay need not be repeated for each
 * invalidation mutation. This entry point runs its two controlled held-figure
 * sections, including the people's inputs, in a normal named harness. The
 * full LabourCheck retains both sections too; no assertion is removed.
 */
public final class MonthPostsCheck {
    private MonthPostsCheck() { }

    public static void main(String[] args) {
        int before = LabourCheck.fails;
        LabourCheck.labourHeldForAPass();
        LabourCheck.sectorWalksAndPostsPast();
        int fails = LabourCheck.fails - before;
        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
