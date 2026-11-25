package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Why the 1900 city stalls (R1g's finding), and what R1g's two switches change: the playtest's village founded the 1900 way, played the
 * playtest's way for five years beside its twin founded today's way. Not part of the game.
 *
 * WHAT THIS HAS TO PROVE (R1g, 2026-11-19; docs/queue/R1g-notes.md, the cause and the fix). R1d's 1900 playtest stalled: 472 people at month
 * 60 and 159 at month 900, against today's city's 1,674 and 23,180. It stalls on two things together, each of which the controls in the notes
 * show is not enough alone, and neither is the dated money. Sections 1-4 are the village as R1g found it, both switches off:
 *
 *   1. THE TWO CITIES: one founding, the playtest's (LongPlaytest.founding()), one of them founded the 1900 way, both founded and played the
 *      way LongPlaytest.main() does it (its switches, the village by hand, the advisor at the run's stops, the automatic works)
 *   2. THE RENT: 1901's wages pay for today's homes, so the 1900 village's households pay well over TARGET_RENT_BURDEN of their
 *      take-home in rent, the affordability pull cuts its draw below today's twin's, and the rent is what binds: without the pull the
 *      city the village's jobs and homes would support is over UNPULLED times its people, and with it the room the village has to grow
 *      (the target over its people, less one) is under today's twin's; today's twin pays under DEAR_RENT times the target, the line
 *      the village is over (0.331 at month 60 on 0.9.174, 0.232 on 0.9.159)
 *   3. THE SKILLS: posts drawn for today's diploma-holders meet 1901's workers, who mostly hold none: the 1900 village's diploma posts
 *      outnumber its diploma workers many times over while today's twin's are covered, and the exporters' first plant (the
 *      Fabrication Shop, whose 1900 recipe asks several times today's diploma crew) meets it at the gate month in the diploma band:
 *      today's twin's diploma workers cover the plant's diploma posts and the 1900 village's do not, and the village cannot pass the
 *      staffing gate, Sector.MIN_STAFFABLE_TO_ORDER
 *   4. THE STALL: the 1900 village draws a smaller share of today's twin's arrivals at the end of the five years than its
 *      affordability pull is of the twin's, so the rent holds it (section 2), and no exporter stands there, its manufacturing held
 *      at the staffing gate; and the 1900 village's
 *      treasury holds its cash and owes nothing, more cash a head than today's growing twin holds, so the dated money (no central
 *      bank until 1935) is not what holds it
 *
 * ...and the fix, at its switches (on unless -Dr1g.home=off, -Dr1g.apprentices=off or -Dr1g.walk=off; each section fails with its own off):
 *
 *   5. THE 1900 HOME (Recipes.HOMES_1900): each of the 1900 village's homes is built at the scale it stands at (Recipes.datedScale();
 *      homeScale() with the walk off) of today's twin's (the cash cost as the catalogue states it, the materials and the points; the land not
 *      moved), its households pay under the stalled village's rent, at most DEAR_RENT times the target, and it draws at least STALLED of
 *      today's twin's arrivals at the end of the five years
 *   6. THE APPRENTICES (PopulationManager.APPRENTICES_1900): the five years' apprentices are the teens who came of age times the year's
 *      trade share (ArrivalMix.tradeShareAt()), there being no school, and the village holds more diploma workers than the stalled one
 *   7. TODAY'S CITY DOES NOT MOVE: today's twin played with both switches on is today's twin played with both off, to the bit
 *   8. THE WALK (Recipes.HOME_WALK; Jerus, 2026-11-20: the home's "cost moves with WAGES"): each month of the 1900 home's village takes the
 *      households' pay once (Recipes.homeScaleOfPay() as the month before ended), the window holding the last Recipes.PAY_MONTHS, and its
 *      House stands at their mean, which the village's pay has moved off homeScale(); the months are saved, so the village loaded into a
 *      fresh Game from its scratch folder stands where it stood, and a month on, where the unsaved one does, to the bit
 *   9. THE LAST HORSE: a farm's crop feeds its horses, then fitting every tractor takes the horse share to zero and the reused output
 *      row clears its captive feed (the 1900 played city's reload fault, mended in Sector).
 *  10. SCHOOL DIPLOMAS COVER THE QUOTA BEFORE DEGREE OUTFLOW: on the actual Game month, gross school completions cover the apprentice
 *      quota while college/university completions take net diploma movement below it; apprentices add nobody and the skilled heads
 *      equal an identically founded control's month with apprentices off.
 *
 * Every fixture CAUSES its condition: the cities are founded on scratch folders by the playtest's own founding and played by the
 * playtest's own advisor and automatic works; section 9 sets fitted counts and section 10 seeds a funded school town and finishing
 * students through the existing fixtures' model restores. Figures are asserted against the
 * model's own constants and methods (RealEstate.TARGET_RENT_BURDEN, Sector.MIN_STAFFABLE_TO_ORDER, Recipes.homeScale(),
 * ArrivalMix.tradeShareAt()) and the twins' figures, with the margins below stated once.
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

    /** The last months the arrivals are summed over: the sixth year's first months, when the 1900 village has all but stopped drawing anybody. */
    static final int QUIET_MONTHS = 5;

    /** The share of today's twin's arrivals in the quiet months a village draws at least once its homes are dated, section 5's floor: a tenth (R1g measured 0 arrivals on 0.9.157 with the switches off, 2.6 of 35.5 on 0.9.159; 13.1 of 47.4 on 0.9.200, where section 4 reads the stall against the rent pull instead). */
    static final double STALLED = 0.1;

    /** The least multiple of its people the 1900 village's jobs and homes would support before any pull is struck (Migration.getLastDrawBeforePulls()): two, so its room is not what holds it. */
    static final double UNPULLED = 2;

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

    /** The house the homes are weighed by: the village's own, 60 of them laid by hand at the founding. */
    static final String HOUSE = "House";

    /** The switches as this run was started: each fix section's city plays at its own, so -Dr1g.home=off or -Dr1g.apprentices=off takes it out. */
    static final boolean HOME = Recipes.HOMES_1900, APPRENTICES = PopulationManager.APPRENTICES_1900;

    /** What one city shows at the gate month and at the end. */
    static final class Seen {
        final boolean the1900Way;
        double gateShare = Double.NaN, gateDiplomaWanted = Double.NaN, gateDiplomaWorkers = Double.NaN;
        double arrivalsLate;
        /** The apprentices of every month, and the coming-of-age teens times the year's trade share less the schools' diploma-holders. */
        double apprentices, apprenticesExpected;
        /** Each month played, the pay the walk took at its top (NaN with none), its window's length, and homeScaleOfPay() as the month ended. */
        final java.util.List<Double> took = new java.util.ArrayList<>(), after = new java.util.ArrayList<>();
        final java.util.List<Integer> windowed = new java.util.ArrayList<>();
        Game g;
        Seen(boolean the1900Way) { this.the1900Way = the1900Way; }
    }

    public static void main(String[] args) {
        out = System.out;
        out.println("Village1900Check - why the 1900 city stalls, and the two switches (R1g)");
        out.printf("   the switches: the 1900 home %s, the apprentices %s%n", HOME ? "on" : "OFF", APPRENTICES ? "on" : "OFF");

        Seen y1900 = play("village1900-1900", true, false, false);
        Seen today = play("village1900-today", false, false, false);

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

        Seen home = play("village1900-home", true, HOME, false);
        home(home, y1900, today);
        Seen taught = play("village1900-apprentices", true, false, APPRENTICES);
        apprentices(taught, y1900);
        Seen todayOn = play("village1900-today-on", false, true, true);
        todayUnmoved(todayOn, today);
        walk(home);
        lastHorse(home.g);
        schoolPlusDegrees();

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
        check("today's twin's households pay under " + DEAR_RENT + " times TARGET_RENT_BURDEN, the line the 1900 village's are over",
                bToday < DEAR_RENT * target, String.format("%.3f", bToday));
        check("so the affordability pull cuts the 1900 village's draw, as Migration.affordabilityPull() strikes it",
                m1900.getLastAffordabilityPull() < 1 && m1900.getLastAffordabilityPull() < mToday.getLastAffordabilityPull(),
                String.format("%.3f against %.3f", m1900.getLastAffordabilityPull(), mToday.getLastAffordabilityPull()));
        long pop1900 = y1900.g.getPopulationManager().getPopulation(), popToday = today.g.getPopulationManager().getPopulation();
        double bare1900 = m1900.getLastDrawBeforePulls(), bareToday = mToday.getLastDrawBeforePulls();
        double room1900 = m1900.getLastTarget() / pop1900 - 1, roomToday = mToday.getLastTarget() / popToday - 1;
        out.printf("   the city the jobs and homes would support before the pulls: 1900 %.0f for %d, today %.0f for %d; the room left to grow (the target over the people, less one): 1900 %.3f, today %.3f%n",
                bare1900, pop1900, bareToday, popToday, room1900, roomToday);
        check("the rent is what binds: before the pulls, the city the 1900 village's jobs and homes would support is over "
                + UNPULLED + " times its people", bare1900 > UNPULLED * pop1900, String.format("%.0f for %d", bare1900, pop1900));
        check("...so the room it leaves the village to grow (the target over its people, less one) is under today's twin's",
                room1900 < roomToday, String.format("%.3f against %.3f", room1900, roomToday));
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
        out.printf("   diploma workers against the %s's diploma posts at month %d: 1900 %.1f for %.0f, today %.1f for %.0f%n",
                FIRST_PLANT, GATE_MONTH, y1900.gateDiplomaWorkers, y1900.gateDiplomaWanted, today.gateDiplomaWorkers, today.gateDiplomaWanted);
        check("today's twin's diploma workers at month " + GATE_MONTH + " cover its " + FIRST_PLANT + "'s diploma posts",
                today.gateDiplomaWorkers >= today.gateDiplomaWanted, String.format("%.1f for %.0f", today.gateDiplomaWorkers, today.gateDiplomaWanted));
        check("while the 1900 village's do not cover its own", y1900.gateDiplomaWorkers < y1900.gateDiplomaWanted,
                String.format("%.1f for %.0f", y1900.gateDiplomaWorkers, y1900.gateDiplomaWanted));
    }

    /* ---------------------------------------------------------------- 4 */

    static void stall(Seen y1900, Seen today) {
        out.println("\n--- 4. the stall: the rent holds the draw, no exporter is built, and the money is not what holds it ---");
        long ex1900 = standing(y1900.g, EXPORTER), exToday = standing(today.g, EXPORTER);
        String word = String.valueOf(y1900.g.getLastInvestment(EXPORTER));
        out.printf("   arrivals in months %d-%d: 1900 %.1f, today %.1f; %s buildings standing: 1900 %d, today %d%n",
                MONTHS - QUIET_MONTHS + 1, MONTHS, y1900.arrivalsLate, today.arrivalsLate, EXPORTER, ex1900, exToday);
        out.printf("   %s's word: 1900 \"%s\"; today \"%s\"%n", EXPORTER, word, today.g.getLastInvestment(EXPORTER));
        // The stall against its cause (R1g, 0.9.200): the village draws a smaller share of the twin's arrivals than its rent pull is of the twin's. On
        // 0.9.157-0.9.177 it drew none at all here (under STALLED); on today's main a trickle closes the gap to the city its rent leaves (section 2).
        double drawShare = today.arrivalsLate > 0 ? y1900.arrivalsLate / today.arrivalsLate : Double.NaN;
        double pullShare = y1900.g.getMigration().getLastAffordabilityPull() / today.g.getMigration().getLastAffordabilityPull();
        check("the 1900 village draws a smaller share of today's twin's arrivals in the last " + QUIET_MONTHS + " months than its affordability pull is of the twin's",
                today.arrivalsLate >= 1 && drawShare < pullShare, String.format("%.3f against %.3f (%.1f against %.1f arrivals)", drawShare, pullShare, y1900.arrivalsLate, today.arrivalsLate));
        check("while people still arrive in today's twin", today.arrivalsLate >= 1, String.format("%.1f", today.arrivalsLate));
        check("no " + EXPORTER + " plant stands in the 1900 village after " + MONTHS + " months", ex1900 == 0, ex1900 + " standing");
        check("and its " + EXPORTER + " is held at the staffing gate (\"could staff\")", word.contains("could staff"), word);
        double cash = y1900.g.getCash(), owed = y1900.g.getDebtManager().getAllPrincipal();
        double owedToday = today.g.getDebtManager().getAllPrincipal();
        out.printf("   the treasury at month %d: 1900 cash %.1f, owed %.1f; today cash %.1f, owed %.1f%n", MONTHS, cash, owed,
                today.g.getCash(), owedToday);
        check("the 1900 village's treasury holds cash and owes nothing, so the dated money is not what holds it", cash > 0 && owed == 0,
                String.format("cash %.1f, owed %.1f", cash, owed));
        double head1900 = cash / Math.max(1, y1900.g.getPopulationManager().getPopulation());
        double headToday = today.g.getCash() / Math.max(1, today.g.getPopulationManager().getPopulation());
        check("and it holds more cash a head than today's twin, which grows on less", head1900 > headToday,
                String.format("%.3f against %.3f", head1900, headToday));
    }

    /* ---------------------------------------------------------------- 5 */

    static void home(Seen home, Seen stalled, Seen today) {
        out.println("\n--- 5. the 1900 home (Recipes.HOMES_1900): built to 1900's standard, and the rent a 1901 household pays ---");
        double scale = Recipes.homeScale(), dated = home.g.getRecipes().datedScale(home.g.getBuildingManager().getTemplateByName(HOUSE));
        BuildingsTemplate h = home.g.getBuildingManager().getTemplateByName(HOUSE), t = today.g.getBuildingManager().getTemplateByName(HOUSE);
        out.printf("   the %s: cash %.4f of today's (the scale it stands at %.4f, homeScale() %.4f), materials %d for %d, points %d for %d, land %.0f for %.0f sq ft%n", HOUSE,
                h.getFoundingCashCost() / t.getFoundingCashCost(), dated, scale, h.getConstructionMaterials(), t.getConstructionMaterials(),
                h.getConstructionPoints(), t.getConstructionPoints(), h.getLandSqFt(), t.getLandSqFt());
        check("the 1900 home is built at about three tenths of today's: Recipes.homeScale() between a fifth and two fifths", scale > .2 && scale < .4,
                String.format("%.4f", scale));
        check("the village's " + HOUSE + " is struck at the scale it stands at (Recipes.datedScale(); homeScale() with the walk off) of today's twin's",
                Math.abs(h.getFoundingCashCost() / t.getFoundingCashCost() - dated) < 1e-9 && (Recipes.HOME_WALK || dated == scale),
                String.format("%.6f against %.6f", h.getFoundingCashCost() / t.getFoundingCashCost(), dated));
        check("its materials and its points are today's times the scale, rounded",
                h.getConstructionMaterials() == Math.max(1, Math.round(t.getConstructionMaterials() * dated))
                        && h.getConstructionPoints() == Math.max(1, Math.round(t.getConstructionPoints() * dated)),
                h.getConstructionMaterials() + " and " + h.getConstructionPoints());
        check("and its plot is today's", h.getLandSqFt() == t.getLandSqFt(), h.getLandSqFt() + " for " + t.getLandSqFt());
        check("the city knows the home is dated", home.g.getRecipes().datedHome(h) && !stalled.g.getRecipes().datedHome(
                stalled.g.getBuildingManager().getTemplateByName(HOUSE)));
        double target = ham.citybuildersim.sectors.RealEstate.TARGET_RENT_BURDEN;
        double bHome = home.g.getHouseholds().getRentBurden(), bStalled = stalled.g.getHouseholds().getRentBurden();
        out.printf("   at month %d: rent as a share of take-home %.3f (stalled %.3f, today %.3f), pull %.3f, %d people (stalled %d), arrivals in the last %d months %.1f%n",
                MONTHS, bHome, bStalled, today.g.getHouseholds().getRentBurden(), home.g.getMigration().getLastAffordabilityPull(),
                home.g.getPopulationManager().getPopulation(), stalled.g.getPopulationManager().getPopulation(), QUIET_MONTHS, home.arrivalsLate);
        check("the 1900 home's households pay at most " + DEAR_RENT + " times TARGET_RENT_BURDEN, under the stalled village's",
                bHome <= DEAR_RENT * target && bHome < bStalled, String.format("%.3f against %.3f", bHome, bStalled));
        check("and it draws at least " + STALLED + " of today's twin's arrivals at the end of the five years",
                home.arrivalsLate >= STALLED * today.arrivalsLate, String.format("%.1f against %.1f", home.arrivalsLate, today.arrivalsLate));
    }

    /* ---------------------------------------------------------------- 6 */

    static void apprentices(Seen taught, Seen stalled) {
        out.println("\n--- 6. the apprentices (PopulationManager.APPRENTICES_1900): the young learn the year's trade at work ---");
        int diploma = WageBand.DIPLOMA.ordinal();
        double wTaught = taught.g.getPopulationManager().workforceByBand()[diploma], wStalled = stalled.g.getPopulationManager().workforceByBand()[diploma];
        out.printf("   five years' apprentices %.2f (the coming of age at the trade share %.2f, the share %.5f); diploma workers at month %d: %.1f (stalled %.1f)%n",
                taught.apprentices, taught.apprenticesExpected, ArrivalMix.tradeShareAt(MONTHS), MONTHS, wTaught, wStalled);
        check("the young learn trades in the 1900 village", taught.apprentices > 0, String.format("%.3f", taught.apprentices));
        check("as many as came of age times the year's trade share, there being no school",
                Math.abs(taught.apprentices - taught.apprenticesExpected) <= 1e-9 * Math.max(1, taught.apprenticesExpected),
                String.format("%.9f against %.9f", taught.apprentices, taught.apprenticesExpected));
        check("so the village holds more diploma workers than the stalled one", wTaught > wStalled,
                String.format("%.2f against %.2f", wTaught, wStalled));
        check("and the stalled village, the switch off, made none", stalled.apprentices == 0, String.format("%.3f", stalled.apprentices));
    }

    /* ---------------------------------------------------------------- 7 */

    static void todayUnmoved(Seen on, Seen off) {
        out.println("\n--- 7. today's city does not move: both switches on and both off, the same city to the bit ---");
        Game a = on.g, b = off.g;
        check("the same people", a.getPopulationManager().getPopulation() == b.getPopulationManager().getPopulation(),
                a.getPopulationManager().getPopulation() + " and " + b.getPopulationManager().getPopulation());
        check("the same cash and the same month's GDP", a.getCash() == b.getCash()
                && a.getEconomyManager().getMonthGdp() == b.getEconomyManager().getMonthGdp(),
                a.getCash() + " and " + b.getCash());
        check("the same rent burden and the same workers by band", a.getHouseholds().getRentBurden() == b.getHouseholds().getRentBurden()
                && java.util.Arrays.equals(a.getPopulationManager().workforceByBand(), b.getPopulationManager().workforceByBand()));
        check("the same " + HOUSE + ", undated", a.getBuildingManager().getTemplateByName(HOUSE).getFoundingCashCost()
                == b.getBuildingManager().getTemplateByName(HOUSE).getFoundingCashCost()
                && !a.getRecipes().datedHome(a.getBuildingManager().getTemplateByName(HOUSE)));
        check("and no apprentice", on.apprentices == 0, String.format("%.3f", on.apprentices));
    }


    /* ---------------------------------------------------------------- 8 */

    static void walk(Seen home) {
        out.println("\n--- 8. the walk (Recipes.HOME_WALK): the home's cost moves with the households' pay, on its last twelve months' mean ---");
        Game g = home.g;
        BuildingsTemplate h = g.getBuildingManager().getTemplateByName(HOUSE);
        Recipes recipes = g.getRecipes();
        int played = home.took.size();
        double[] window = recipes.payIndicesForSave();
        out.printf("   at month %d: the %s stands at %.4f of today's (homeScale() %.4f); the window %s%n", g.getMonth(), HOUSE,
                recipes.datedScale(h), Recipes.homeScale(), window == null ? "empty" : window.length + " months, " + String.format("%.4f to %.4f",
                        java.util.Arrays.stream(window).min().orElse(0), java.util.Arrays.stream(window).max().orElse(0)));
        boolean once = played > Recipes.PAY_MONTHS;
        for (int m = 0; m < played && once; m++) once = home.windowed.get(m) == Math.min(Recipes.PAY_MONTHS, home.windowed.get(0) + m);
        check("each month takes the households' pay once: the window grows by a month a month to PAY_MONTHS and holds there", once,
                home.windowed.toString());
        double worst = 0;
        for (int m = 1; m < played; m++) worst = Math.max(worst, Math.abs(home.took.get(m) - home.after.get(m - 1)));
        check("the pay each month takes is Recipes.homeScaleOfPay() as the month before ended (the households' pay, founding money, over today's)",
                played > 1 && worst < 1e-12, String.format("worst %.3g", worst));
        double sum = 0;
        for (int m = Math.max(0, played - Recipes.PAY_MONTHS); m < played; m++) sum += home.took.get(m);
        double mean = played == 0 ? Double.NaN : sum / Math.min(played, Recipes.PAY_MONTHS);
        check("the " + HOUSE + " stands at the mean of the last PAY_MONTHS months taken", Math.abs(recipes.datedScale(h) - mean) < 1e-12,
                String.format("%.6f against %.6f", recipes.datedScale(h), mean));
        check("...which the village's pay has moved off homeScale(), 1901's household, by more than a hundredth",
                Math.abs(recipes.datedScale(h) - Recipes.homeScale()) > .01, String.format("%.4f against %.4f", recipes.datedScale(h), Recipes.homeScale()));

        // The months are saved: the village saved on its scratch folder and loaded into a fresh Game stands where it stood, and a month on, where it does.
        GameFiles files = g.getGameFiles();
        quietly(() -> g.saveGame(10, "village1900-walk"));
        Game back = new Game(files);
        quietly(() -> back.loadGameSave(10));
        BuildingsTemplate hb = back.getBuildingManager().getTemplateByName(HOUSE);
        check("the village saved and loaded into a fresh Game holds the same months, to the bit, and its " + HOUSE + " the same scale and cost",
                java.util.Arrays.equals(back.getRecipes().payIndicesForSave(), window) && back.getRecipes().datedScale(hb) == recipes.datedScale(h)
                        && hb.getCashCost() == h.getCashCost(),
                String.format("%.6f against %.6f, cost %.6f against %.6f", back.getRecipes().datedScale(hb), recipes.datedScale(h), hb.getCashCost(), h.getCashCost()));
        quietly(() -> LongPlaytest.runPlayer(g, 1));
        quietly(() -> LongPlaytest.runPlayer(back, 1));
        check("...and a month on, both have taken the same month's pay and stand at the same scale, to the bit",
                java.util.Arrays.equals(back.getRecipes().payIndicesForSave(), recipes.payIndicesForSave())
                        && back.getRecipes().datedScale(hb) == recipes.datedScale(h),
                String.format("%.9f against %.9f", back.getRecipes().datedScale(hb), recipes.datedScale(h)));
    }

    /** Runs a step with the console quiet, the model's chatter left out of the report. */
    static void quietly(Runnable step) {
        PrintStream real = System.out, report = LongPlaytest.out;
        System.setOut(QUIET);
        LongPlaytest.out = QUIET;
        try {
            step.run();
        } finally {
            LongPlaytest.out = report;
            System.setOut(real);
        }
    }
    /* ---------------------------------------------------------------- the fixture */

    /**
     * A city founded on the playtest's founding, the 1900 way or today's, founded and played as LongPlaytest.main() founds and plays it: the
     * same switches, the automatic works on before the village is laid, the founding's months by hand, then the run's rhythm (a skip, a look
     * with up to movesPerLook() moves of the advisor, two months), up to MONTHS. main() founds the run's own way only (LongPlaytest.played()),
     * so its sequence is followed here with the founding chosen; its audits (the layout's watch, the retirement watch) are left out, as they
     * change nothing in the model (LongPlaytest, the layout's watch: "the eight CSVs are the proof it changed nothing"). R1g's two switches
     * are set for the city's play and put back after it.
     */
    static Seen play(String label, boolean the1900Way, boolean home, boolean apprentices) {
        Seen seen = new Seen(the1900Way);
        PrintStream real = System.out, report = LongPlaytest.out;
        System.setOut(QUIET);
        LongPlaytest.out = QUIET;
        boolean wasHome = Recipes.HOMES_1900, wasApprentices = PopulationManager.APPRENTICES_1900;
        Recipes.HOMES_1900 = home;
        PopulationManager.APPRENTICES_1900 = apprentices;
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
            Recipes.HOMES_1900 = wasHome;
            PopulationManager.APPRENTICES_1900 = wasApprentices;
            System.setOut(real);
        }
    }

    /** Up to n of the player's months (LongPlaytest.runPlayer()), never past MONTHS: the gate weighed at the end of GATE_MONTH, the last arrivals summed. */
    static void months(Seen seen, int n) {
        Game g = seen.g;
        for (int i = 0; i < n && g.getMonth() < MONTHS; i++) {
            LongPlaytest.runPlayer(g, 1);
            double[] window = g.getRecipes().payIndicesForSave();
            seen.took.add(window == null ? Double.NaN : window[window.length - 1]);
            seen.windowed.add(window == null ? 0 : window.length);
            seen.after.add(Recipes.homeScaleOfPay(g));
            seen.apprentices += g.getPopulationManager().getLastApprentices();
            if (seen.the1900Way && PopulationManager.APPRENTICES_1900) seen.apprenticesExpected += Math.max(0,
                    g.getCohorts().getPromoted(AgeBand.TEEN) * ArrivalMix.tradeShareAt(g.getMonth())
                            - Math.max(0, g.getEducation().getNewDiplomas()));
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
            seen.gateDiplomaWorkers = g.getPopulationManager().workforceByBand()[WageBand.DIPLOMA.ordinal()];
        }
    }

    /** The city's posts by wage band, as the playtest's labour trace adds them. */
    static double[] postsByBand(Game g) {
        long[] jobs = g.getPopulationManager().getJobs();
        double[] posts = new double[WageBand.values().length];
        for (JobType j : JobType.values()) posts[WageBand.of(j).ordinal()] += jobs[j.ordinal()];
        return posts;
    }

    /** R1g: the last horse leaves no feed behind on a reused output row (the played city's reload fault). */
    static void lastHorse(Game g) {
        out.println("\n--- 9. the last horse: fitting every farm clears the captive crop ---");
        var farms = g.getSectors().agriculture();
        for (BuildingsTemplate t : g.getBuildingManager().getTemplatesBySector(farms.key())) {
            BuildingsStacks st = g.getBuildingManager().getStack(t);
            if (st != null && t.getEquipment("tractor") != null) st.setUnfitted("tractor", st.getQuantity());
        }
        g.getRecipes().apply(g);
        g.getEquipmentForms().apply(g);
        farms.produceStock(Good.CROPS, null);
        Sector.Output crops = farms.outputRow(Good.CROPS);
        check("fixture: unfitted farms feed their horses from the crop", farms.getHorseShare() > 0 && crops != null && crops.captive > 0,
                crops == null ? "no crop row" : "horse share " + farms.getHorseShare() + ", feed " + crops.captive);
        for (BuildingsTemplate t : g.getBuildingManager().getTemplatesBySector(farms.key())) {
            BuildingsStacks st = g.getBuildingManager().getStack(t);
            if (st != null && t.getEquipment("tractor") != null) st.setUnfitted("tractor", 0);
        }
        g.getRecipes().apply(g);
        g.getEquipmentForms().apply(g);
        check("fixture: every farm now has its tractor, so no crop is captive", farms.getHorseShare() == 0 && !farms.captive(Good.CROPS),
                "horse share " + farms.getHorseShare());
        farms.produceStock(Good.CROPS, null);
        check("the next crop keeps none of the last horse's feed", crops != null && crops.captive == 0,
                crops == null ? "no crop row" : "feed " + crops.captive);
    }

    /** REV-R1gc2-1: gross school leavers cover the quota even when degrees make the diploma band's net flow smaller. */
    static void schoolPlusDegrees() {
        out.println("\n--- 10. school diplomas cover the apprentice quota before degree outflow ---");
        boolean wasApprentices = PopulationManager.APPRENTICES_1900;
        Game[] twins = new Game[2];
        boolean[] seeded = new boolean[4];
        try {
            // A six-month school town, not the long played village. EducationCheck's normal pipeline restore causes the completions.
            PopulationManager.APPRENTICES_1900 = false;
            quietly(() -> { for (int i = 0; i < twins.length; i++) {
                LongPlaytest.resetTheFloat();
                Game g = new Game(GameFiles.scratch("village1900-school-degrees-" + i), LongPlaytest.founding().withUnfitted(true));
                twins[i] = g;
                g.run();
                g.getGovernmentInvestor().spend(-900_000_000 * PriceLevel.FACTOR);
                g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 400_000_000L);
                for (String sector : Sectors.KEYS) g.getBusinessInvestment().holdSector(sector);
                EducationCheck.build(g, "Low-Rise Apartments", 120);
                EducationCheck.build(g, "General Hospital", 3);
                EducationCheck.build(g, "Small Grocery Store", 14);
                EducationCheck.build(g, "Coal Power Plant", 4);
                EducationCheck.build(g, "Water Treatment Plant", 4);
                EducationCheck.build(g, "Paved Road", 60);
                EducationCheck.build(g, "Construction Depot", 6);
                EducationCheck.schools(g);
                g.getEconomyManager().getTaxPolicy().setTuitionScale(0);
                g.simulateMonths(EducationCheck.TOWN_MONTHS - 1);
                EducationCheck.skillsSet(g);
                double leavers = g.getCohorts().get(AgeBand.TEEN) / AgeBand.TEEN.spanMonths();
                seeded[2 * i] = EducationCheck.seeded(g, EducationType.COLLEGE, leavers, 1);
                seeded[2 * i + 1] = EducationCheck.seeded(g, EducationType.UNIVERSITY, leavers, 1);
            } });
            Game g = twins[0], off = twins[1];
            check("fixture: both school towns are founded the 1900 way with the same skilled heads", g.foundedUnfitted() && off.foundedUnfitted()
                    && java.util.Arrays.equals(g.getPopulationManager().getSkilledHeads(), off.getPopulationManager().getSkilledHeads()));
            check("fixture: college and university students are due to finish next month", seeded[0] && seeded[1] && seeded[2] && seeded[3]
                    && g.getEducation().cohortsInFlight(EducationType.COLLEGE)[0] > 0
                    && g.getEducation().cohortsInFlight(EducationType.UNIVERSITY)[0] > 0);
            PopulationManager.APPRENTICES_1900 = true;
            quietly(() -> g.simulateMonths(1));
            PopulationManager.APPRENTICES_1900 = false;
            quietly(() -> off.simulateMonths(1));
            Education school = g.getEducation();
            double gross = school.getNewDiplomas(), net = school.getGraduates()[WageBand.DIPLOMA.ordinal()];
            double degrees = school.getGraduates()[WageBand.COLLEGE.ordinal()] + school.getGraduates()[WageBand.UNIVERSITY.ordinal()];
            double quota = g.getCohorts().getPromoted(AgeBand.TEEN) * ArrivalMix.tradeShareAt(g.getMonth());
            out.printf("   m%d: gross school diplomas %.6f, degree completions %.6f, net diploma movement %.6f, apprentice quota %.6f%n",
                    g.getMonth(), gross, degrees, net, quota);
            check("fixture: gross school diplomas already cover a positive apprentice quota", quota > 0 && gross >= quota);
            check("fixture: college and university completions leave the diploma band and put net movement below the quota",
                    school.getGraduates()[WageBand.COLLEGE.ordinal()] > 0 && school.getGraduates()[WageBand.UNIVERSITY.ordinal()] > 0 && net < quota
                    && Math.abs(gross - net - degrees) < 1e-9 * Math.max(1, degrees));
            check("fixture: both controls have the same gross diplomas and net school/degree flows", off.getEducation().getNewDiplomas() == gross
                    && java.util.Arrays.equals(school.getGraduates(), off.getEducation().getGraduates()));
            check("Game adds no extra trade qualification after the schools cover the quota", g.getPopulationManager().getLastApprentices() == 0,
                    "added " + g.getPopulationManager().getLastApprentices());
            check("the actual Game month keeps the school-only control's skilled heads, including degree outflow",
                    java.util.Arrays.equals(g.getPopulationManager().getSkilledHeads(), off.getPopulationManager().getSkilledHeads()),
                    java.util.Arrays.toString(g.getPopulationManager().getSkilledHeads()) + " against "
                            + java.util.Arrays.toString(off.getPopulationManager().getSkilledHeads()));
        } finally {
            PopulationManager.APPRENTICES_1900 = wasApprentices;
        }
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
