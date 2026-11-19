package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Why the 1900 city stalls (R1g's finding): the playtest's village founded the 1900 way, played the playtest's way for five years beside
 * its twin founded today's way. Not part of the game.
 *
 * WHAT THIS HAS TO PROVE (R1g, 2026-11-19; docs/queue/R1g-notes.md, the cause). R1d's 1900 playtest stalled: 472 people at month 60 and
 * 159 at month 900, against today's city's 1,674 and 23,180. It stalls on two things together, each of which the controls in the notes
 * show is not enough alone, and neither is the dated money:
 *
 *   1. THE TWO CITIES: one founding, the playtest's (LongPlaytest.founding()), one of them founded the 1900 way, both played the way
 *      LongPlaytest.probeCity() and main() play it (the same village, the advisor and the automatic works every month)
 *   2. THE RENT: 1901's wages pay for today's homes, so the 1900 village's households pay well over TARGET_RENT_BURDEN of their
 *      take-home in rent, the affordability pull cuts the city its conditions support under the people already in it, and today's
 *      twin pays no more than the target
 *   3. THE SKILLS: posts drawn for today's diploma-holders meet 1901's workers, who mostly hold none: the 1900 village's diploma posts
 *      outnumber its diploma workers many times over while today's twin's are covered, and the exporters' first plant (the
 *      Fabrication Shop, whose 1900 recipe asks several times today's diploma crew) cannot pass the staffing gate,
 *      Sector.MIN_STAFFABLE_TO_ORDER, which today's twin's passes
 *   4. THE STALL: nobody arrives in the 1900 village at the end of the five years while people still arrive in today's twin, and no
 *      exporter stands there, its manufacturing held at the staffing gate; and the 1900 village's treasury holds its cash and owes
 *      nothing while today's twin has borrowed, so the dated money (no central bank until 1935) is not what holds it
 *
 * Every fixture CAUSES its condition: both cities are founded on scratch folders by the playtest's own founding and played by the
 * playtest's own advisor and automatic works; nothing in them is set by hand. Figures are asserted against the model's own constants
 * (RealEstate.TARGET_RENT_BURDEN, Sector.MIN_STAFFABLE_TO_ORDER) and the twin's figures, with the margins below stated once.
 */
public class Village1900Check {

    static int fails = 0;
    static PrintStream out;
    static final PrintStream QUIET = new PrintStream(OutputStream.nullOutputStream());

    static void check(String label, boolean ok) { check(label, ok, null); }

    static void check(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-118s %s%s%n", label, ok ? "OK" : "FAIL", ok || detail == null ? "" : "  " + detail);
    }

    /** The months the two cities are played: five years, the stall's start (R1d: 472 people at month 60, nobody arriving from month 55). */
    static final int MONTHS = 60;

    /** The month the exporters' first plant is weighed: the end of the playtest's first skip, its first look after the founding's months by hand. */
    static final int GATE_MONTH = 30;

    /** The last months the arrivals are summed over: the sixth year's first months, when the 1900 village has stopped drawing anybody. */
    static final int QUIET_MONTHS = 5;

    /** How many times TARGET_RENT_BURDEN the 1900 village's households pay at the least: one and a half (R1g measured 2.0 at month 60). */
    static final double DEAR_RENT = 1.5;

    /** How many times its diploma workers the 1900 village's diploma posts number at the least: two (R1g measured 2.4 at month 60). */
    static final double SHORT_OF_DIPLOMAS = 2;

    /** How many times today's twin's diploma crew the 1900 Fabrication Shop wants at the least: three (R2d's cutting crews, 248 for 62). */
    static final double CUTTING_CREWS = 3;

    /** The exporters' sector, the advisor's first word on the 1900 village's stall (R1g's probe: "could staff 0% of a Wire & Cable Works now"). */
    static final String EXPORTER = "Manufacturing";

    /** The exporters' first plant. */
    static final String FIRST_PLANT = "Fabrication Shop";

    /** What one city shows at the gate month and at the end. */
    static final class Seen {
        final boolean the1900Way;
        double gateShare = Double.NaN, gateDiplomaWanted = Double.NaN;
        double arrivalsLate;
        Game g;
        Seen(boolean the1900Way) { this.the1900Way = the1900Way; }
    }

    public static void main(String[] args) {
        out = System.out;
        out.println("Village1900Check - why the 1900 city stalls (R1g)");

        Seen y1900 = play("village1900-1900", true);
        Seen today = play("village1900-today", false);

        out.println("\n--- 1. the two cities: the playtest's founding, the 1900 way and today's, played the playtest's way ---");
        check("the 1900 village is founded the 1900 way", y1900.g.foundedUnfitted());
        check("today's twin is founded today's way", !today.g.foundedUnfitted());
        check("both are played to month " + MONTHS, y1900.g.getMonth() >= MONTHS && today.g.getMonth() >= MONTHS,
                y1900.g.getMonth() + " and " + today.g.getMonth());
        out.printf("   population at month %d: 1900 %d, today %d%n", MONTHS,
                y1900.g.getPopulationManager().getPopulation(), today.g.getPopulationManager().getPopulation());

        rent(y1900, today);
        skills(y1900, today);
        stall(y1900, today);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ---------------------------------------------------------------- 2 */

    static void rent(Seen y1900, Seen today) {
        out.println("\n--- 2. the rent: 1901's wages pay for today's homes ---");
        double target = ham.citybuildersim.sectors.RealEstate.TARGET_RENT_BURDEN;
        double b1900 = y1900.g.getHouseholds().getRentBurden(), bToday = today.g.getHouseholds().getRentBurden();
        Migration m1900 = y1900.g.getMigration(), mToday = today.g.getMigration();
        out.printf("   rent as a share of take-home: 1900 %.3f, today %.3f (the target %.2f)%n", b1900, bToday, target);
        out.printf("   affordability pull: 1900 %.3f, today %.3f; the city the conditions support: 1900 %.0f for %d, today %.0f for %d%n",
                m1900.getLastAffordabilityPull(), mToday.getLastAffordabilityPull(),
                m1900.getLastTarget(), y1900.g.getPopulationManager().getPopulation(),
                mToday.getLastTarget(), today.g.getPopulationManager().getPopulation());
        check("the 1900 village's households pay over " + DEAR_RENT + " times TARGET_RENT_BURDEN of their take-home in rent",
                b1900 > DEAR_RENT * target, String.format("%.3f", b1900));
        check("today's twin's households pay no more than TARGET_RENT_BURDEN", bToday <= target, String.format("%.3f", bToday));
        check("so the affordability pull cuts the 1900 village's draw, as Migration.affordabilityPull() strikes it",
                m1900.getLastAffordabilityPull() < 1 && m1900.getLastAffordabilityPull() < mToday.getLastAffordabilityPull(),
                String.format("%.3f against %.3f", m1900.getLastAffordabilityPull(), mToday.getLastAffordabilityPull()));
        check("and the city the 1900 village's conditions support is smaller than the people already in it",
                m1900.getLastTarget() < y1900.g.getPopulationManager().getPopulation(),
                String.format("%.0f for %d", m1900.getLastTarget(), y1900.g.getPopulationManager().getPopulation()));
        check("while today's twin's supports more than its people", mToday.getLastTarget() > today.g.getPopulationManager().getPopulation(),
                String.format("%.0f for %d", mToday.getLastTarget(), today.g.getPopulationManager().getPopulation()));
    }

    /* ---------------------------------------------------------------- 3 */

    static void skills(Seen y1900, Seen today) {
        out.println("\n--- 3. the skills: posts drawn for today's diploma-holders, and 1901's workers ---");
        int diploma = WageBand.DIPLOMA.ordinal();
        double[] p1900 = postsByBand(y1900.g), pToday = postsByBand(today.g);
        double[] w1900 = y1900.g.getPopulationManager().workforceByBand(), wToday = today.g.getPopulationManager().workforceByBand();
        out.printf("   diploma posts and workers at month %d: 1900 %.0f for %.1f, today %.0f for %.1f%n", MONTHS,
                p1900[diploma], w1900[diploma], pToday[diploma], wToday[diploma]);
        check("the 1900 village's diploma posts outnumber its diploma workers over " + SHORT_OF_DIPLOMAS + " times",
                p1900[diploma] > SHORT_OF_DIPLOMAS * w1900[diploma], String.format("%.0f for %.1f", p1900[diploma], w1900[diploma]));
        check("today's twin's diploma workers cover its diploma posts", wToday[diploma] >= pToday[diploma],
                String.format("%.1f for %.0f", wToday[diploma], pToday[diploma]));
        out.printf("   the %s at month %d: the share the city could staff 1900 %.3f, today %.3f (the gate %.2f); diploma posts wanted 1900 %.0f, today %.0f%n",
                FIRST_PLANT, GATE_MONTH, y1900.gateShare, today.gateShare, Sector.MIN_STAFFABLE_TO_ORDER,
                y1900.gateDiplomaWanted, today.gateDiplomaWanted);
        check("the 1900 " + FIRST_PLANT + " wants over " + CUTTING_CREWS + " times today's diploma crew (R2d's 1900 cutting crews)",
                y1900.gateDiplomaWanted > CUTTING_CREWS * today.gateDiplomaWanted,
                String.format("%.0f against %.0f", y1900.gateDiplomaWanted, today.gateDiplomaWanted));
        check("the 1900 village could staff under MIN_STAFFABLE_TO_ORDER of a " + FIRST_PLANT + " at month " + GATE_MONTH,
                y1900.gateShare < Sector.MIN_STAFFABLE_TO_ORDER, String.format("%.3f", y1900.gateShare));
        check("today's twin could staff at least MIN_STAFFABLE_TO_ORDER of one", today.gateShare >= Sector.MIN_STAFFABLE_TO_ORDER,
                String.format("%.3f", today.gateShare));
    }

    /* ---------------------------------------------------------------- 4 */

    static void stall(Seen y1900, Seen today) {
        out.println("\n--- 4. the stall: nobody comes, no exporter is built, and the money is not what holds it ---");
        long ex1900 = standing(y1900.g, EXPORTER), exToday = standing(today.g, EXPORTER);
        String word = String.valueOf(y1900.g.getLastInvestment(EXPORTER));
        out.printf("   arrivals in months %d-%d: 1900 %.1f, today %.1f; %s buildings standing: 1900 %d, today %d%n",
                MONTHS - QUIET_MONTHS + 1, MONTHS, y1900.arrivalsLate, today.arrivalsLate, EXPORTER, ex1900, exToday);
        out.printf("   %s's word: 1900 \"%s\"; today \"%s\"%n", EXPORTER, word, today.g.getLastInvestment(EXPORTER));
        check("nobody arrives in the 1900 village in the last " + QUIET_MONTHS + " months", y1900.arrivalsLate < 1,
                String.format("%.1f", y1900.arrivalsLate));
        check("while people still arrive in today's twin", today.arrivalsLate >= 1, String.format("%.1f", today.arrivalsLate));
        check("no " + EXPORTER + " plant stands in the 1900 village after " + MONTHS + " months", ex1900 == 0, ex1900 + " standing");
        check("and its " + EXPORTER + " is held at the staffing gate (\"could staff\")", word.contains("could staff"), word);
        double cash = y1900.g.getCash(), owed = y1900.g.getDebtManager().getAllPrincipal();
        double owedToday = today.g.getDebtManager().getAllPrincipal();
        out.printf("   the treasury at month %d: 1900 cash %.1f, owed %.1f; today cash %.1f, owed %.1f%n", MONTHS, cash, owed,
                today.g.getCash(), owedToday);
        check("the 1900 village's treasury holds cash and owes nothing, so the dated money is not what holds it", cash > 0 && owed == 0,
                String.format("cash %.1f, owed %.1f", cash, owed));
        check("while today's twin, building, has spent its cash and borrowed", owedToday > 0, String.format("owed %.1f", owedToday));
    }

    /* ---------------------------------------------------------------- the fixture */

    /**
     * A city founded on the playtest's founding, the 1900 way or today's, founded and played as LongPlaytest.main() founds and plays it: the
     * same switches, the automatic works on before the village is laid, the founding's months by hand, then the run's rhythm (a skip, a look
     * with up to movesPerLook() moves of the advisor, two months), up to MONTHS. main() founds the run's own way only (LongPlaytest.played()),
     * so its sequence is followed here with the founding chosen; its audits (the layout's watch, the retirement watch) are left out, as they
     * change nothing in the model (LongPlaytest, the layout's watch: "the eight CSVs are the proof it changed nothing").
     */
    static Seen play(String label, boolean the1900Way) {
        Seen seen = new Seen(the1900Way);
        PrintStream real = System.out, report = LongPlaytest.out;
        System.setOut(QUIET);
        LongPlaytest.out = QUIET;
        try {
            Founding founding = the1900Way ? LongPlaytest.founding().withUnfitted(true) : LongPlaytest.founding();
            LongPlaytest.resetTheFloat();
            Game g = new Game(GameFiles.scratch(label), founding);
            seen.g = g;
            g.run();
            g.getDebtManager().setAutopilot(LongPlaytest.AUTOPILOT);
            g.setRolloverMode(LongPlaytest.ROLLOVER);
            g.getCityMap();
            g.setRescueMode(LongPlaytest.RESCUE_AUTO ? TreasuryFund.RescueMode.AUTOMATIC : TreasuryFund.RescueMode.BUTTON);
            g.getExchange().setShortSelling(LongPlaytest.SHORTS);
            g.setFundDial(LongPlaytest.FUND_DIAL);
            LongPlaytest.seedMarket(g, 0);
            LongPlaytest.startAutomaticWorks(g);
            LongPlaytest.villageBuild(g, "House", 40);
            LongPlaytest.villageBuild(g, "Convenience Store", 3);
            LongPlaytest.villageBuild(g, "Mixed Farm", 2);
            months(seen, 3);
            LongPlaytest.villageBuild(g, "House", 20);
            months(seen, 4);
            LongPlaytest.villageBuild(g, "Convenience Store", 2);
            LongPlaytest.villageBuild(g, "Construction Depot", 1);
            months(seen, 5);
            LongPlaytest.advise(g);
            months(seen, 6);
            LongPlaytest.ensureSchools(g);
            LongPlaytest.childcareWhenNeeded(g);
            int stop = 0;
            while (g.getMonth() < MONTHS) {
                stop++;
                int skip = switch (stop % 6) {
                    case 0 -> 100;
                    case 1 -> 12;
                    case 2 -> 24;
                    case 3 -> 60;
                    case 4 -> 6;
                    default -> 120;
                };
                months(seen, Math.min(skip, LongPlaytest.longestSkip()));
                if (g.getMonth() >= MONTHS) break;
                LongPlaytest.ensureSchools(g);
                LongPlaytest.childcareWhenNeeded(g);
                for (int move = 0; move < LongPlaytest.movesPerLook() && g.getMonth() < MONTHS; move++) {
                    if (LongPlaytest.advise(g) == null) break;
                    months(seen, 1);
                }
                months(seen, 2);
            }
            if (Double.isNaN(seen.gateShare)) weigh(seen);
            return seen;
        } finally {
            LongPlaytest.out = report;
            System.setOut(real);
        }
    }

    /** Up to n of the player's months (LongPlaytest.runPlayer()), never past MONTHS: the gate weighed at the end of GATE_MONTH, the last arrivals summed. */
    static void months(Seen seen, int n) {
        Game g = seen.g;
        for (int i = 0; i < n && g.getMonth() < MONTHS; i++) {
            LongPlaytest.runPlayer(g, 1);
            if (g.getMonth() == GATE_MONTH) weigh(seen);
            if (g.getMonth() > MONTHS - QUIET_MONTHS) seen.arrivalsLate += g.getMigration().getLastArrivals();
        }
    }

    /** The staffing gate on the exporters' first plant, and the diploma posts it wants. */
    static void weigh(Seen seen) {
        Game g = seen.g;
        Sector s = g.getSectors().byKey(EXPORTER);
        for (BuildingsTemplate t : g.getBuildingManager().getTemplatesBySector(EXPORTER)) {
            if (!t.getName().equals(FIRST_PLANT)) continue;
            Sector.Staffing st = s.staffing(t);
            seen.gateShare = st.share;
            seen.gateDiplomaWanted = st.wanted()[WageBand.DIPLOMA.ordinal()];
        }
    }

    /** The city's posts by wage band, as the playtest's labour trace adds them. */
    static double[] postsByBand(Game g) {
        long[] jobs = g.getPopulationManager().getJobs();
        double[] posts = new double[WageBand.values().length];
        for (JobType j : JobType.values()) posts[WageBand.of(j).ordinal()] += jobs[j.ordinal()];
        return posts;
    }

    /** How many buildings of a sector stand. */
    static long standing(Game g, String sector) {
        long n = 0;
        for (BuildingsTemplate t : g.getBuildingManager().getTemplatesBySector(sector)) {
            BuildingsStacks st = g.getBuildingManager().getStack(t);
            if (st != null) n += st.getQuantity();
        }
        return n;
    }
}
