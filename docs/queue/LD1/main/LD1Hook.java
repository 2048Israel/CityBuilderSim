package ham.citybuildersim;

import java.io.PrintStream;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * LD1's probe (scratch, not shipped): after each month, every sector the
 * staffing gate held, classified - the builders' laid-off crews (it would pass
 * with them counted spare: XS1's trap, IC1's), or a shortage; whether the held
 * building would carry the interest on its whole cost at prime (consider()'s
 * own test) at today's wage; which bands are short; and the month's migration
 * gap, arrivals and band multiples. One TSV line per hold, one per month.
 */
public final class LD1Hook {
    static final PrintStream OUT = open();
    static PrintStream open() {
        String f = System.getProperty("ld1.file");
        try { return f == null ? null : new PrintStream(new java.io.FileOutputStream(f), false, "UTF-8"); }
        catch (java.io.IOException e) { throw new RuntimeException(e); }
    }
    static final Pattern COULD = Pattern.compile("the city could staff (\\d+)% of an? (.+); it wants");
    static final Pattern NOONE = Pattern.compile("no one could staff an? (.+)'s (\\w+) posts");

    public static void month(Game g) {
        if (OUT == null) return;
        int m = g.getMonth();
        PopulationManager pm = g.getPopulationManager();
        Migration mig = g.getMigration();
        LabourMarket lm = g.getLabourMarket();
        int bands = WageBand.values().length;
        double pop = pm.getPopulation();
        double gap = mig.getLastTarget() - pop;
        StringBuilder mult = new StringBuilder();
        for (WageBand b : WageBand.values()) mult.append(String.format(Locale.ROOT, "%.3f/%.3f ", lm.getBandMultiple(b), lm.bandPremium(b)));
        double[] mix = mig.getLastArrivalMix();
        StringBuilder mixs = new StringBuilder();
        for (int b = 0; mix != null && b < mix.length; b++) mixs.append(String.format(Locale.ROOT, "%.1f ", mix[b]));
        int holds = 0;
        for (Map.Entry<String, String> e : g.getLastInvestments().entrySet()) {
            String v = e.getValue();
            if (v == null) continue;
            String name = null; String pct = "";
            Matcher a = COULD.matcher(v), b = NOONE.matcher(v);
            if (a.find()) { name = a.group(2); pct = a.group(1); }
            else if (b.find()) { name = b.group(1); pct = "none"; }
            if (name == null) continue;
            holds++;
            Sector s = g.getSectors().byKey(e.getKey());
            if (s == null) s = g.getSectors().retail();
            BuildingsTemplate t = g.getBuildingManager().getTemplateByName(name);
            if (t == null) { OUT.println(m + "\tHOLD\t" + e.getKey() + "\t" + name + "\t?"); continue; }
            double withCrews = s.staffing(t).share;
            // the same test with the builders' laid-off crews counted spare
            double[] posts = pm.staffablePostsByBand();
            double[] supply = pm.supplyByBand(posts);
            double[] own = pm.workforceByBand();
            double[] wanted = new double[bands];
            double total = 0;
            for (JobType job : JobType.values()) { double n = t.getJobs(job); if (n > 0) { wanted[WageBand.of(job).ordinal()] += n; total += n; } }
            double fill = 0;
            StringBuilder shortB = new StringBuilder();
            for (int k = 0; k < bands; k++) {
                double spare = supply[k] - posts[k];
                if (wanted[k] > 0) {
                    fill += Math.min(wanted[k], Math.max(0, spare));
                    if (wanted[k] > Math.max(0, spare)) shortB.append(String.format(Locale.ROOT, "%s:%.0f/%.0f(own %.0f,posts %.0f) ",
                            WageBand.values()[k], wanted[k] - Math.max(0, spare), wanted[k], own[k], posts[k]));
                }
            }
            double noCrews = total > 0 ? fill / total : 1;
            BusinessInvestment bi = g.getBusinessInvestment();
            double earns = bi.estimatedMonthlyProfit(s.key(), t);
            double cost = bi.getCostOf(t, 1);
            BusinessDebtManager credit = g.getEconomyManager().getBusinessDebtManager();
            double rate = credit.getPrimeRate() + credit.getRecordSurcharge(s.key());
            boolean pays = bi.servicesItsOwnDebt(earns, cost, rate);
            double bill = bi.wageBillFor(t);
            OUT.println(String.format(Locale.ROOT, "%d\tHOLD\t%s\t%s\t%s\t%.3f\t%.3f\t%s\t%s\t%.1f\t%.1f\t%.1f\t%s",
                    m, e.getKey(), name, pct, withCrews, noCrews, noCrews >= Sector.MIN_STAFFABLE_TO_ORDER ? "crews" : "short",
                    pays ? "pays" : "nopay", earns, cost, bill, shortB.toString().trim()));
        }
        OUT.println(String.format(Locale.ROOT, "%d\tMONTH\tpop %.0f\tunemp %d\twork %d\twithheld %d\ttarget %.0f\tgap %.0f\tarr %.1f\tdep %.1f\tafford %.3f\tholds %d\tmult %s\tmix %s",
                m, pop, pm.getUnemployed(), pm.getWorkforce(), g.getBuildingManager().getPostsWithheld(), mig.getLastTarget(), gap,
                mig.getLastArrivals(), mig.getLastDepartures(), mig.getLastAffordabilityPull(), holds, mult.toString().trim(), mixs.toString().trim()));
        OUT.flush();
    }
}
