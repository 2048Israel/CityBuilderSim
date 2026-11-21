package ham.citybuildersim;

import ham.citybuildersim.sectors.Agriculture;
import ham.citybuildersim.sectors.FoodIndustry;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * The mills counted in the heads their retirement measure serves (RT2).
 *
 * WHY. The measure counted both baked goods in heads, while unitsOf() counted
 * only kilograms of bread. Two Bakeries and an Industrial Bakery, with no
 * customers, could not sell even one Bakery after the required losses.
 * Prove the buildings add up in the measure's unit and the rule sells the
 * most-held kind by its own headroom and gradual-retirement constants.
 */
public class FoodIndustryCheck {
    static int fails;
    static final PrintStream out = System.out;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-100s %s%n", label, ok ? "OK" : "FAIL");
    }

    public static void main(String[] args) {
        out.println("--- 1. a mill is counted in the heads its sector's retirement measure serves ---");
        Game game = new Game(GameFiles.scratch("foodindustrycheck-retirement"));
        PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());
        System.setOut(quiet);
        try { game.newGame(); } finally { System.setOut(out); quiet.close(); }
        BuildingManager bm = game.getBuildingManager();
        FoodIndustry industry = game.getSectors().industry();
        BuildingsTemplate bakery = bm.getTemplateByName("Bakery"), mill = bm.getTemplateByName("Industrial Bakery");
        int heldBakery = 2, heldMill = 1;
        bm.addStack(bakery, heldBakery, true);
        bm.addStack(mill, heldMill, true);
        double[] measure = industry.retirementDemandAndCapacity(game);
        assertTrue("fixture: no customers, two kinds of mill, the Bakery the more-held kind",
                measure != null && measure[0] == 0 && bm.getQuantity(bakery.getId()) == heldBakery
                        && bm.getQuantity(mill.getId()) == heldMill && heldBakery > heldMill);
        double counted = 0, heads = 0, bread = 0;
        for (BuildingsTemplate t : bm.getTemplatesBySector(industry.key())) {
            long held = bm.getQuantity(t.getId());
            counted += held * industry.unitsOf(t);
            heads += held * (t.makes(Good.BREAD) + t.makes(Good.BAKERY)) / Agriculture.BAKED_KG_A_HEAD;
            bread += held * t.makes(Good.BREAD);
        }
        out.printf("   buildings: %,.0f counted, %,.0f heads, %,.0f kg bread; measure %,.0f%n", counted, heads, bread, measure[1]);
        assertTrue("each mill counts both baked goods in heads, and the buildings add up to the retirement capacity",
                Math.abs(counted - heads) <= 1e-12 * heads && Math.abs(counted - measure[1]) <= 1e-12 * heads);
        assertTrue("a missing template counts nothing", industry.unitsOf(null) == 0);
        assertTrue("without a city there is no shop headcount to measure", industry.retirementDemandAndCapacity(null) == null);
        BusinessInvestment plans = game.getBusinessInvestment();
        for (int i = 0; i < BusinessInvestment.RETIREMENT_LOSS_MONTHS; i++) plans.recordSectorResult(industry.key(), -1);
        double each = (bakery.makes(Good.BREAD) + bakery.makes(Good.BAKERY)) / Agriculture.BAKED_KG_A_HEAD;
        int fits = (int) Math.floor((heads - measure[0] * (1 + BusinessInvestment.TARGET_HEADROOM)) / each);
        int allowed = Math.min(fits, Math.min((int) Math.ceil(heldBakery * BusinessInvestment.MAX_RETIREMENT_FRACTION), heldBakery));
        BusinessInvestment.Decision shed = plans.planRetirement(industry, measure[0], measure[1], 0);
        out.printf("   retires %d of %d Bakeries (%s)%n", shed.quantity, heldBakery, shed.reason);
        assertTrue("after RETIREMENT_LOSS_MONTHS losses the rule sells the most-held mill and the share its constants allow",
                allowed > 0 && shed.build && shed.template == bakery && shed.quantity == allowed);
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
