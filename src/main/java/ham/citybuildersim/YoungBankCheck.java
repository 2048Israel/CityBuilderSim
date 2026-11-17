package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * A young town's bank through its first ten years with no sector held: the
 * harness of DQ1, "young towns borrow ahead of their people and default".
 *
 * WHY: GW1's staffing gate counts a year's draw, so a young town's sectors
 * order their first works earlier, and six fixtures held sectors so that their
 * banks stood (GW1's notes, section 5: BankCheck 19's town went into
 * resolution at month 44). The town below is BankCheck 19's, held nowhere. On
 * 0.9.128 its Cement & Concrete was lent $1,328k for a Lime Kiln at month 11,
 * 168% of the bank's equity, on an estimate struck at an operating rate of
 * 0.97; the kiln ran at 0.52-0.61, was sold at month 58, six months open, and
 * the bank wrote off $1,601k: under its 8% minimum for ten months, down to
 * 0.7%, but never in resolution. DQ1 measured four ways of holding or pricing
 * a lone plant's loan at the bank and took none (docs/queue/DQ1-notes.md):
 * each stunted the young towns or did no better over the sixteen AUTO2 seeds.
 * So this pins the condition, and the premise the holds were there for.
 *
 * What this has to prove:
 *  1. The fixture causes the condition: a young town whose first works cost
 *     more than its bank's whole equity, weighed by a sector with no plant,
 *     and a lone plant's default that puts its bank under its minimum
 *     (Bank.CAPITAL_RATIO). Every spell under the minimum is printed with the
 *     write-off that began it and whether its sector was one plant or many.
 *  2. With no sector held, the bank stands: it is never in resolution
 *     through month MONTHS. The town's people are printed beside it.
 */
public final class YoungBankCheck {

    static int fails = 0;
    static PrintStream out = System.out;

    /** The young town's first ten years: the months the bank is watched for. */
    static final int MONTHS = 120;

    /** By this month the town has weighed its first works (BankCheck 19's Lime Kiln was weighed from month 9). */
    static final int WEIGHED_BY = 24;

    static void check(String label, boolean ok) {
        out.println((ok ? "   ok   " : "   FAIL ") + label);
        if (!ok) fails++;
    }

    /** A sector of one plant at most, standing or on site: one borrower, whose failure empties it. A sector that owns no building type borrows as many. */
    static boolean onePlant(Game g, String sector) {
        BuildingManager bm = g.getBuildingManager();
        java.util.List<BuildingsTemplate> types = bm.getTemplatesBySector(sector);
        if (types == null || types.isEmpty()) return false;
        double plants = bm.getUnderConstructionBySector(sector);
        for (BuildingsTemplate t : types) plants += bm.getQuantity(t.getId());
        return plants <= 1;
    }

    public static void main(String[] args) throws Exception {
        PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());
        Game town = new Game(GameFiles.scratch("youngbank"));
        System.setOut(quiet);
        try {
            town.run();
            // BankCheck 19's town (0.7.x, GW1), every sector free to order.
            town.setCashForTest(Founding.WEALTHY_CASH);
            town.getForeignAccounts().pinRate(1.0);
            town.getLandManager().setOwnedSqFt(30_000_000);
            town.buildStack(BankCheck.template(town, "House"), 400, true);
            town.buildStack(BankCheck.template(town, "Convenience Store"), 8, true);
            town.buildStack(BankCheck.template(town, "Small Grocery Store"), 2, true);
            town.buildStack(BankCheck.template(town, "Bakery"), 1, true);
            town.buildStack(BankCheck.template(town, "Paved Road"), 20, true);
            town.buildStack(BankCheck.template(town, "Industrial Bakery"), 2, true);
            town.buildStack(BankCheck.template(town, "Construction Depot"), 4, true);
            town.buildStack(BankCheck.template(town, "Coal Power Plant"), 1, true);
            town.buildStack(BankCheck.template(town, "Water Treatment Plant"), 1, true);
        } finally {
            System.setOut(out);
        }

        BusinessDebtManager credit = town.getEconomyManager().getBusinessDebtManager();
        boolean weighedBeyondEquity = false;
        String weighed = "";
        double lowest = Double.POSITIVE_INFINITY;
        int lowestMonth = -1, monthsUnder = 0, monthsInResolution = 0, loneSpells = 0, spells = 0;
        boolean under = false;
        StringBuilder spellLines = new StringBuilder();
        // Each sector's write-off last month, and whether it was one plant then (before a sale emptied it).
        java.util.Map<String, Double> lastWrittenOff = new java.util.HashMap<>();
        java.util.Map<String, Boolean> wasOnePlant = new java.util.HashMap<>();
        for (int m = 0; m < MONTHS; m++) {
            System.setOut(quiet);
            try { town.simulateMonths(1); } finally { System.setOut(out); }
            Bank bank = town.getBank();
            int month = town.getMonth();
            double equity = bank.equity();
            // 1: a sector of one plant at most weighing a works that costs more than the bank's equity.
            for (String s : credit.sectors()) {
                if (month > WEIGHED_BY || weighedBeyondEquity || !onePlant(town, s)) continue;
                String why = town.getLastInvestment(s);
                for (BuildingsTemplate t : town.getBuildingManager().getTemplatesBySector(s)) {
                    if (why == null || !why.contains(t.getName())) continue;
                    double cost = town.getBusinessInvestment().getCostOf(t, 1);
                    if (!(equity > 0) || cost <= equity) continue;
                    weighedBeyondEquity = true;
                    weighed = String.format(java.util.Locale.ROOT, "%s's %s at month %d, $%.0fk against the bank's equity of $%.0fk",
                            s, t.getName(), month, cost, equity);
                }
            }
            // ...and the bank's ratio, once it has a book to weigh; a spell
            // under the minimum is put down to the largest write-off of its
            // first month and the one before.
            boolean nowUnder = false;
            if (bank.getWeightedBook() > 0) {
                double ratio = bank.capitalRatio();
                if (ratio < lowest) { lowest = ratio; lowestMonth = month; }
                nowUnder = ratio < Bank.CAPITAL_RATIO;
                if (nowUnder) monthsUnder++;
            }
            if (nowUnder && !under) {
                spells++;
                String cause = null;
                double most = 0;
                for (String s : credit.sectors()) {
                    double w = bank.getWrittenOff(s) + lastWrittenOff.getOrDefault(s, 0.0);
                    if (w > most) { most = w; cause = s; }
                }
                boolean lone = cause != null && wasOnePlant.getOrDefault(cause, false);
                if (lone) loneSpells++;
                spellLines.append(String.format(java.util.Locale.ROOT, "   month %d: ratio %.4f, %s%n", month, bank.capitalRatio(),
                        cause == null ? "no write-off (the desk's marks or the year's losses)"
                                : String.format(java.util.Locale.ROOT, "%s wrote off $%.0fk, a sector of %s", cause, most, lone ? "one plant" : "many plants")));
            }
            under = nowUnder;
            // 2: the bank stands.
            if (bank.isInResolution()) monthsInResolution++;
            for (String s : credit.sectors()) {
                lastWrittenOff.put(s, bank.getWrittenOff(s));
                wasOnePlant.put(s, onePlant(town, s));
            }
        }
        long people = (long) town.getPopulationManager().getPopulation();

        out.println("--- 1. the fixture: a young town borrows ahead of its people, and a lone plant's default puts its bank under its minimum ---");
        out.println("   " + (weighed.isEmpty() ? "none weighed" : weighed));
        out.printf(java.util.Locale.ROOT, "   lowest ratio %.4f at month %d; %d months under %.2f in %d spells, %d begun by a lone plant%n",
                lowest, lowestMonth, monthsUnder, Bank.CAPITAL_RATIO, spells, loneSpells);
        out.print(spellLines);
        check("by month " + WEIGHED_BY + " a sector of one plant at most weighs a works that costs more than the bank's whole equity",
                weighedBeyondEquity);
        check("a spell under Bank.CAPITAL_RATIO begins with the write-off of a sector of one plant", loneSpells > 0);

        out.println("--- 2. with no sector held, the young town's bank stands through month " + MONTHS + " ---");
        out.printf(java.util.Locale.ROOT, "   %d months in resolution; %d people at month %d%n",
                monthsInResolution, people, town.getMonth());
        check("the bank is never in resolution", monthsInResolution == 0);

        out.println(fails == 0 ? "YoungBankCheck: all passed" : "YoungBankCheck: " + fails + " FAILED");
        System.exit(fails);
    }
}
