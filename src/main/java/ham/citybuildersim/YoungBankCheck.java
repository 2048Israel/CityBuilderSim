package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * A young town's bank through its first ten years with no sector held: the
 * harness of DQ1, "young towns borrow ahead of their people and default", and
 * since OE1 of the first plants they borrow for.
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
 * The miss was the estimate's: a sector with nothing running weighed its
 * first plant at the city's health alone, its staffing read 1 for want of
 * posts. OE1 (docs/queue/OE1-notes.md) weighs a first plant at the rate the
 * month reads for it (Sector.plannedRate()); on 0.9.162 before it, the town's
 * Materials ordered a second plant before its first had opened, weighed at
 * 0.85, and its sale emptied the sector for a $2,744k write-off at month 103
 * and 19 months in resolution. Section 1's second line asserted that spell
 * until OE1; it asserts its absence since, as DQ1's notes left it to.
 *
 * What this has to prove:
 *  1. The fixture causes the condition: a young town whose first works cost
 *     more than its bank's whole equity, weighed by a sector with no plant;
 *     and since OE1 no lone plant's default puts its bank under its minimum
 *     (Bank.CAPITAL_RATIO). Every spell under the minimum is printed with the
 *     write-off that began it and whether its sector was one plant or many.
 *  2. With no sector held, the bank stands: it is never in resolution
 *     through month MONTHS. The town's people are printed beside it.
 *  3. A first plant is weighed at the rate it runs at (OE1): each order the
 *     town places in a sector with nothing running is read at the month's
 *     start for the rate the order weighs it at (Sector.plannedRate()) and
 *     the sector's own (the estimate before OE1, BusinessInvestment
 *     .operatingRateOf() of getOperatingRate()), and against the rate the
 *     plant ran at while it stood alone in its first RUN_MONTHS. None is
 *     weighed further from its run than its sector's own rate, and in all
 *     they are weighed nearer: with the old estimate the two are one figure
 *     and the second line fails.
 */
public final class YoungBankCheck {

    static int fails = 0;
    static PrintStream out = System.out;

    /** The young town's first ten years: the months the bank is watched for. */
    static final int MONTHS = 120;

    /** By this month the town has weighed its first works (BankCheck 19's Lime Kiln was weighed from month 9). */
    static final int WEIGHED_BY = 24;

    /** The months a first plant's run is read over: its first year standing alone in its sector. */
    static final int RUN_MONTHS = 12;

    /** ...and the fewest of them that make a run to weigh it against. */
    static final int RUN_MONTHS_AT_LEAST = 3;

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

    /** One of `t` on site. */
    static long onSite(BuildingManager bm, BuildingsTemplate t) {
        BuildingsStacks stack = bm.getStack(t);
        return stack == null ? 0 : stack.getUnderConstruction();
    }

    /** A sector's first order, placed while it ran nothing: the rates it was weighed at when the month began, and the rates its plant ran at. */
    static final class First {
        final String sector;
        final BuildingsTemplate building;
        final int month;
        final double weighed, own;
        final java.util.List<Double> ran = new java.util.ArrayList<>();
        boolean done;

        First(String sector, BuildingsTemplate building, int month, double weighed, double own) {
            this.sector = sector;
            this.building = building;
            this.month = month;
            this.weighed = weighed;
            this.own = own;
        }

        double run() { return ran.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN); }
        boolean counted() { return ran.size() >= RUN_MONTHS_AT_LEAST; }
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
        BuildingManager bm = town.getBuildingManager();
        boolean weighedBeyondEquity = false;
        String weighed = "";
        double lowest = Double.POSITIVE_INFINITY;
        int lowestMonth = -1, monthsUnder = 0, monthsInResolution = 0, loneSpells = 0, spells = 0;
        boolean under = false;
        StringBuilder spellLines = new StringBuilder();
        // Each sector's write-off last month, and whether it was one plant then (before a sale emptied it).
        java.util.Map<String, Double> lastWrittenOff = new java.util.HashMap<>();
        java.util.Map<String, Boolean> wasOnePlant = new java.util.HashMap<>();
        // 3: each sector's first order, by key in the order placed.
        java.util.Map<String, First> firsts = new java.util.LinkedHashMap<>();
        for (int m = 0; m < MONTHS; m++) {
            // 3: what each sector with nothing running would weigh each of its buildings at as the month begins, and its own rate.
            java.util.Map<BuildingsTemplate, double[]> weighing = new java.util.LinkedHashMap<>();
            java.util.Map<BuildingsTemplate, Long> sitesBefore = new java.util.HashMap<>();
            for (Sector s : town.getSectors().all()) {
                if (!s.runsNothing() || firsts.containsKey(s.key())) continue;
                for (BuildingsTemplate t : bm.getTemplatesBySector(s.key())) {
                    weighing.put(t, new double[] { s.plannedRate(town.getBusinessInvestment().newUnit(t)),
                            BusinessInvestment.operatingRateOf(s.getOperatingRate()) });
                    sitesBefore.put(t, onSite(bm, t));
                }
            }
            System.setOut(quiet);
            try { town.simulateMonths(1); } finally { System.setOut(out); }
            Bank bank = town.getBank();
            int month = town.getMonth();
            double equity = bank.equity();
            // 3: an order placed this month by a sector that ran nothing as it began...
            for (java.util.Map.Entry<BuildingsTemplate, double[]> e : weighing.entrySet()) {
                BuildingsTemplate t = e.getKey();
                String s = t.getSector();
                if (firsts.containsKey(s) || onSite(bm, t) <= sitesBefore.get(t)) continue;
                firsts.put(s, new First(s, t, month, e.getValue()[0], e.getValue()[1]));
            }
            // ...and its plant's rate each month it stands alone in its sector, for its first RUN_MONTHS.
            for (First f : firsts.values()) {
                if (f.done) continue;
                Sector s = town.getSectors().byKey(f.sector);
                long standing = s.buildingsStanding();
                if (standing == 0) {
                    if (!f.ran.isEmpty()) f.done = true;
                    continue;
                }
                if (standing > 1) { f.done = true; continue; }
                f.ran.add(s.getOperatingRate());
                if (f.ran.size() >= RUN_MONTHS) f.done = true;
            }
            // 1: a sector of one plant at most weighing a works that costs more than the bank's equity.
            for (String s : credit.sectors()) {
                if (month > WEIGHED_BY || weighedBeyondEquity || !onePlant(town, s)) continue;
                String why = town.getLastInvestment(s);
                for (BuildingsTemplate t : bm.getTemplatesBySector(s)) {
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

        out.println("--- 1. the fixture: a young town borrows ahead of its people, and no lone plant's default puts its bank under its minimum ---");
        out.println("   " + (weighed.isEmpty() ? "none weighed" : weighed));
        out.printf(java.util.Locale.ROOT, "   lowest ratio %.4f at month %d; %d months under %.2f in %d spells, %d begun by a lone plant%n",
                lowest, lowestMonth, monthsUnder, Bank.CAPITAL_RATIO, spells, loneSpells);
        out.print(spellLines);
        check("by month " + WEIGHED_BY + " a sector of one plant at most weighs a works that costs more than the bank's whole equity",
                weighedBeyondEquity);
        check("no spell under Bank.CAPITAL_RATIO begins with the write-off of a sector of one plant", loneSpells == 0);

        out.println("--- 2. with no sector held, the young town's bank stands through month " + MONTHS + " ---");
        out.printf(java.util.Locale.ROOT, "   %d months in resolution; %d people at month %d%n",
                monthsInResolution, people, town.getMonth());
        check("the bank is never in resolution", monthsInResolution == 0);

        out.println("--- 3. a first plant is weighed at the rate it runs at ---");
        int counted = 0, further = 0;
        double missWeighed = 0, missOwn = 0;
        for (First f : firsts.values()) {
            String ran = f.ran.isEmpty() ? "not open by month " + MONTHS
                    : String.format(java.util.Locale.ROOT, "ran at %.3f-%.3f, %.3f on average, over %d month(s) alone",
                            f.ran.stream().mapToDouble(Double::doubleValue).min().orElse(Double.NaN),
                            f.ran.stream().mapToDouble(Double::doubleValue).max().orElse(Double.NaN), f.run(), f.ran.size());
            out.printf(java.util.Locale.ROOT, "   month %d: %s's %s weighed at %.3f (its sector's own rate %.3f); %s%n",
                    f.month, f.sector, f.building.getName(), f.weighed, f.own, ran);
            if (!f.counted()) continue;
            counted++;
            double w = Math.abs(f.weighed - f.run()), o = Math.abs(f.own - f.run());
            missWeighed += w;
            missOwn += o;
            if (w > o) further++;
        }
        out.printf(java.util.Locale.ROOT, "   %d first plants ran %d months or more: missed their runs by %.3f in all as weighed, by %.3f at their sectors' own rates%n",
                counted, RUN_MONTHS_AT_LEAST, missWeighed, missOwn);
        check("the young town orders a first plant in a sector with nothing running, and it runs " + RUN_MONTHS_AT_LEAST + " months alone",
                counted > 0);
        check("no first plant is weighed further from the rate it ran at than its sector's own rate", further == 0);
        check("in all the first plants are weighed nearer the rates they ran at than their sectors' own rates (the estimate before OE1)",
                missWeighed < missOwn);

        out.println(fails == 0 ? "YoungBankCheck: all passed" : "YoungBankCheck: " + fails + " FAILED");
        System.exit(fails);
    }
}
