package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A city played for four thousand months, the way a person plays.
 *
 * WHY NOT JUST simulateMonths(4000)
 *
 * A single long skip is one input. It exercises the month loop and almost
 * nothing else: the player never intervenes, so the city settles into whatever
 * equilibrium it finds in the first fifty months and then repeats it. Every bug
 * that lives in the interaction between DECIDING and SIMULATING - the ones that
 * need something to be built, taxed, borrowed against, saved or reloaded partway
 * through - is invisible to it.
 *
 * So this alternates. Short hands-on stretches where an advisor looks at the
 * city and does something about it, long skips where it just runs, policy
 * changes, borrowing, land purchases, demolitions, and save/reload round trips
 * partway through. Roughly the shape of a real session, repeated a hundred and
 * forty times.
 *
 * WHAT IT IS LOOKING FOR
 *
 * Not "did it finish". A simulation that runs forever while quietly printing
 * nonsense is worse than one that throws. Every month is audited against a list
 * of things that must be true of any city in any state (below), and every
 * reload is checked against the game it came from to the cent. Findings are
 * deduplicated and reported with the month they first appeared, because a
 * problem that starts at month 900 and a problem that starts at month 3 are
 * different problems.
 */
public class LongPlaytest {

    /** Where the report goes. The game's own console output is discarded. */
    static PrintStream out;

    static final int TARGET_MONTHS = 4000;

    /* ===================================================================
       FINDINGS

       Deduplicated by message. A broken invariant usually breaks every month
       after it first breaks, and four thousand identical lines hide the second
       problem underneath the first.
       =================================================================== */

    static final Map<String, Finding> findings = new LinkedHashMap<>();

    static final class Finding {
        int count;
        int firstMonth;
        int lastMonth;
        String worst;
        double worstSize = Double.NEGATIVE_INFINITY;
        int worstMonth;
    }

    /* -------------------------------------------------------------------
       `worst` MEANT `first`, AND THE PRINTOUT SAID SO (2026-09-09).

       The field was called worst and only ever assigned once - on the month a
       finding was first seen - so a fault that started as a rounding wobble and
       grew into a catastrophe was reported at its mildest. The summary line
       hedged it as "first seen as", which is honest about the value and hides
       how little it tells you: a finding raised 92 times has 92 magnitudes and
       this kept the least useful one.

       It cost real time the day it was found. A household-with-no-home finding
       reported "1 households nowhere" - the format string rounded to the
       integer as well - and deciding whether that was a housing famine or the
       fractional-people-versus-integer-homes noise the flag's own comment
       allows for needed the actual number and the actual peak. It was 1.236,
       and it never got worse.

       flag() now takes the SIZE of the fault, so the worst month is the one
       reported and the first month is kept separately. The string overload
       stays for the findings that have no natural magnitude.
       ------------------------------------------------------------------- */

    static void flag(int month, String what, String detail) {
        flag(month, what, detail, 0);
    }

    static void flag(int month, String what, String detail, double size) {
        Finding f = findings.get(what);
        if (f == null) {
            f = new Finding();
            f.firstMonth = month;
            findings.put(what, f);
        }
        if (size > f.worstSize) {
            f.worstSize = size;
            f.worst = detail;
            f.worstMonth = month;
        }
        f.count++;
        f.lastMonth = month;
    }

    /* ===================================================================
       THE AUDIT

       Things that have to be true of a city in ANY state. Not balance
       opinions - if one of these is false, something is broken.
       =================================================================== */

    /** How far past the buildings' comfortable capacity the city was pulled. */
    static double worstCrowding = 0;
    static int worstCrowdingMonth = 0;

    /*
     * DOES THE DECLINE GATE EVER ACTUALLY OPEN?
     *
     * Jerus's rule is that people only leave once a pay tier has been shrinking
     * for twelve CONSECUTIVE months or has gone to zero. Twelve consecutive
     * anythings is a strong condition in a noisy series - one good month resets
     * the count - so it is entirely possible the gate never opens in four
     * thousand months, which would make out-migration a mechanic that exists
     * only in the harness. That is worth knowing rather than assuming, in either
     * direction, so it is counted.
     */
    /*
     * Unemployment, watched rather than asserted. It is a balance figure, not an
     * invariant - but it is the figure that silently went from 11% to 23% when
     * the workforce became the actual adults and `residents per job` stayed a
     * constant, and nothing noticed for a batch. Now it is printed.
     */
    static double worstUnemployment = 0;
    static double lastUnemployment = 0;

    static int monthsAnyTierDeclining = 0;
    static int monthsPeopleLeft = 0;

    /* Sickness. Counted rather than assumed - a mechanic that never fires over
       four thousand months looks exactly like one that does not exist, which is
       how out-migration got shipped inert. */
    static int outbreaks = 0;
    static int monthsInOutbreak = 0;
    static boolean wasInOutbreak = false;
    static double worstSickRate = 0;
    static double lastSickRate = 0;
    static double workLostToIllness = 0;
    static int monthsObserved = 0;
    static double totalDepartures = 0;
    static double totalArrivals = 0;

    static void audit(Game g) {

        int month = g.getMonth();
        EconomyManager e = g.getEconomyManager();
        PopulationManager p = g.getPopulationManager();
        BuildingManager b = g.getBuildingManager();
        LandManager l = g.getLandManager();
        InfrastructureManager roads = g.getInfrastructureManager();

        finite(month, "cash", g.getCash());
        finite(month, "next-month income", g.getIncome());
        finite(month, "monthly GDP", e.getMonthGdp());
        finite(month, "annual GDP", e.getNationalAccounts().getAnnualGdp());
        finite(month, "total wage", p.getTotalWage());
        finite(month, "food price", e.getIndustrialHandler().getFoodPrice());
        finite(month, "materials price", b.getConstructionMaterialPrice());
        finite(month, "land price", l.getPricePerSqFt());

        if (p.getPopulation() < 0) {
            flag(month, "negative population", "" + p.getPopulation());
        }

        /*
         * MONEY IS CONSERVED. Every dollar that left a pool this month arrived
         * in another, or crossed the city's boundary in a way MoneyAudit can
         * name. A residual is a dollar from nowhere - the profit tax the
         * sectors never paid, the VAT nobody was debited, the utility booking
         * a different bill from the one its customers paid: all found the
         * first time this was struck, 2026-09-06, none of them by any of the
         * twenty-eight harnesses that came before. A cent, on any month, is a
         * finding.
         */
        MoneyAudit.Result money = g.getLastMoneyAudit();
        if (Math.abs(money.residual) > .01 && money.relative() > 1e-7) {
            flag(month, "money was not conserved", money.toString());
        }

        /*
         * Output cannot be negative. Jerus, after a hand-played city reported
         * -$446,424 a month for a century: "negative GDP should just be
         * impossible, some arithmetic is wrong."
         *
         * It was - twice. Imported materials were subtracted as an import and
         * the yard they went into was not counted as inventory, so buying in
         * bulk read as a collapse in output; and inventory was measured by VALUE
         * rather than by volume, so a price move booked as production. Both are
         * fixed in NationalAccounts, and this is the invariant that keeps them
         * fixed across four thousand months of a city doing everything.
         */
        if (e.getMonthGdp() < 0) {
            NationalAccounts na = e.getNationalAccounts();
            flag(month, "negative monthly GDP", String.format(
                    "%.2f  (C %.0f  Iconstr %.0f  Istock %.0f  G %.0f  NX %.0f)"
                    + "  Ifood %.0f  Imatl %.0f  Iwip %.0f",
                    e.getMonthGdp(), na.getConsumption(),
                    na.getInvestmentConstruction(), na.getInvestmentInventories(),
                    na.getGovernment(), na.getNetExports(),
                    na.getInventoryFood(), na.getInventoryMaterials(),
                    na.getInventoryWorkInProgress()));
        }
        if (e.getNationalAccounts().getAnnualGdp() < 0) {
            flag(month, "negative annual GDP",
                    String.format("%.2f", e.getNationalAccounts().getAnnualGdp()));
        }

        /*
         * NOBODY IS HOMELESS. This used to read "more residents than housing",
         * comparing the population against household CAPACITY - correct while
         * updatePop() capped the one at the other, and meaningless the moment
         * migration replaced that cap. A city that is full but hiring is now
         * supposed to keep taking people; they crowd, per Jerus, and capacity is
         * a comfort figure rather than a wall.
         *
         * What is still a wall is FRONT DOORS. FamilyModel crowds households
         * until they fit - flatshares first, then doubling up - and migration is
         * damped to zero at the point those valves run out. If households ever
         * outnumber homes after that squeeze, one of those two mechanisms has
         * failed and somebody in this city is sleeping outside.
         *
         * A whole household of slack is allowed for rounding: the pyramid holds
         * fractions of people and the homes are integers.
         */
        /*
         * ASKED OF THE MODEL, NOT OF THE DOOR COUNT.
         *
         * This compared households against homes, which was the right question
         * while a home was a home. Since 2026-09-07 homes have SIZES: a city
         * can have five hundred spare studios and still not house a family, and
         * it can equally have one household more than it has doors and place
         * every one of them, because five singles went into one flatshare.
         * Counting doors answers neither question. FamilyModel now reports what
         * both valves failed to place, which is the thing this flag is about.
         */
        double homeless = g.getFamilies().getStillUnplaced();
        if (homeless > 1) {
            flag(month, "households with no home",
                    String.format("%.3f households nowhere, %d homes of %d sizes",
                            homeless, b.getTotalHomes(), b.homesBySize().length - 1),
                    homeless);
        }

        // Worth watching even though it is no longer a fault: how far past what
        // the buildings comfortably hold the city has been pulled by its jobs.
        if (b.getTotalHouseCapacity() > 0) {
            double over = p.getPopulation() / (double) b.getTotalHouseCapacity();
            if (over > worstCrowding) {
                worstCrowding = over;
                worstCrowdingMonth = month;
            }
        }

        lastUnemployment = p.getUnemploymentRate();
        if (lastUnemployment > worstUnemployment) worstUnemployment = lastUnemployment;

        monthsObserved++;
        /*
         * -Dplaytest.fx=true: the currency and what is pushing it, yearly. The
         * instrument that found the surplus with nowhere to go; kept because
         * the next currency question will want it again.
         */
        if (Boolean.getBoolean("playtest.fx") && g.getMonth() % 12 == 0) {
            ForeignAccounts fa = g.getForeignAccounts(); OutwardInvestment oi = g.getOutwardInvestment();
            double tills = 0;
            for (String s : BusinessDebtManager.SECTORS) tills += Math.max(0, g.getEconomyManager().getSectorCash(s));
            out.printf("FX m%-4d rate %.3f parity %.3f pressure %+.2f openness %.2f | current %,.0f financial %,.0f | tills %,.0f abroad US$%,.0f ($%,.0f) share %.0f%% | deposit %.2f%% world %.2f%% | pop %d GDP %,.0f%n",
                g.getMonth(), fa.getRate(), fa.getParity(), fa.getLastPressure(), fa.getOpenness(),
                fa.monthlyCurrentAccount(), fa.monthlyFinancialAccount(),
                tills, oi.totalUsd(), oi.totalLocalValue(), oi.getTargetShare() * 100,
                g.getBank().depositRate() * 100, DebtManager.WORLD_BASE_RATE * 100,
                g.getPopulationManager().getPopulation(), g.getEconomyManager().getMonthGdp());
            Exchange ex = g.getExchange();
            out.printf("   desk: sold abroad %,.0f bought abroad %,.0f (emigrants %,.0f) | buybacks to hh %,.0f abroad %,.0f special %,.0f | dividends hh %,.0f abroad %,.0f | sent abroad %,.0f home %,.0f | hh saved %,.0f abroad US$%,.0f (sent %,.0f home %,.0f) divs %,.0f sold %,.0f | securities %,.0f%n",
                ex.getSoldAbroad(), ex.getBoughtFromAbroad(), ex.getEmigrantsPaid(), ex.getBuybackToHouseholds(), ex.getBuybackAbroad(), ex.getSpecialDividend(),
                g.getEquity().getDividendHomeThisMonth(), g.getEquity().getDividendAbroadThisMonth(),
                oi.getInvestedAbroadThisMonth(), oi.getBroughtHomeThisMonth(),
                g.getHouseholdBalance().totalSavings(), g.getHouseholdBalance().totalAbroadUsd(), g.getHouseholdBalance().getSentAbroad(), g.getHouseholdBalance().getBroughtHome(),
                g.getHouseholdBalance().totalDividends(), g.getHouseholdBalance().totalSold(), g.getBank().getSecurities());
            StringBuilder co = new StringBuilder("   companies:");
            for (int c = 0; c < Equity.COMPANIES.length; c++) {
                SectorBooks.SectorMonth sm = c == Equity.BANK ? null : g.getSectorBooks().get(PolicySector.byCreditName(Equity.COMPANIES[c]));
                co.append(String.format(" %s %s sh %,.1f eq/assets %.2f tgt %.2f mid %.3f fair %.3f yield %.1f%% abroad %.0f%% bought back %,.0f |",
                    Equity.COMPANIES[c].substring(0, 3), g.getEquity().getRegime(c), g.getEquity().getShares(c),
                    sm == null ? 0 : (sm.totalAssets() > 0 ? sm.equity() / sm.totalAssets() : 0), g.getEquity().getTargetEquityShare(c),
                    ex.mid(c), ex.fair(c), ex.yieldAt(g.getEquity(), c, ex.ask(c)) * 100, g.getEquity().foreignShare(c) * 100,
                    g.getEquity().getLifetimeBoughtBack(c)));
            }
            out.println(co);
        }
        /*
         * -Dplaytest.cells=true: the households going short, cell by cell,
         * yearly. The instrument that says WHICH families the budget
         * constraint is biting now that every shape at every tier keeps its
         * own books - a row average cannot.
         */
        if (Boolean.getBoolean("playtest.cells") && g.getMonth() % 12 == 0) {
            HouseholdBalance hb = g.getHouseholdBalance();
            out.printf("HH m%-4d households %,.0f saved $%,.0fk owed $%,.0fk | hungry %.1f%% | written off $%,.1fk taken away $%,.1fk leaving %.1f%n",
                g.getMonth(), hb.sum(Household::households), hb.totalSavings(), hb.totalDebt(),
                hb.getHungerRate() * 100, hb.getWrittenOff(), hb.getTakenAway(), hb.getLeavingCity());
            for (Household c : hb.cells()) {
                if (c.households() < .5) continue;
                if (!c.isGoingShort() && !c.isCutOff() && !c.isLockedOut() && c.debt() <= 0) continue;
                out.printf("   %-40s x%,6.0f  take-home %7.3f  after bills %7.3f  basket %6.3f  saved %7.3f  owed %7.3f  rate %4.1f%%%s%s%s%n",
                    c.label(), c.households(), c.disposable(), c.afterFixed(), c.subsistence(),
                    c.savings(), c.debt(), c.rate() * 100,
                    c.isGoingShort() ? "  SHORT" : "", c.isCutOff() ? "  CUT OFF" : "",
                    c.isLockedOut() ? "  LOCKED " + c.lockout() : "");
            }
        }
        Health health = g.getHealth();
        lastSickRate = health.getSickRate();
        if (lastSickRate > worstSickRate) worstSickRate = lastSickRate;
        workLostToIllness += lastSickRate;
        if (health.isOutbreak()) {
            monthsInOutbreak++;
            if (!wasInOutbreak) outbreaks++;
        }
        wasInOutbreak = health.isOutbreak();

        Migration mig = g.getMigration();
        if (mig.decliningShare() > 0) monthsAnyTierDeclining++;
        if (mig.getLastDepartures() > 0) monthsPeopleLeft++;
        totalDepartures += mig.getLastDepartures();
        totalArrivals += mig.getLastArrivals();

        if (p.getWorkforce() > p.getPopulation()) {
            flag(month, "more workers than residents",
                    p.getWorkforce() + " of " + p.getPopulation());
        }

        // Backlog item 7: getStoreIncome() sells without capping at stock.
        if (e.getStoreInventory() < 0) {
            flag(month, "negative store inventory", "" + e.getStoreInventory());
        }

        if (e.getIndustryFoodInventory() < 0) {
            flag(month, "negative food inventory", "" + e.getIndustryFoodInventory());
        }

        // Land committed can never exceed land owned; the difference is what a
        // build order is allowed to draw on.
        if (l.getAllocatedSqFt() > l.getOwnedSqFt() + 1e-6) {
            flag(month, "more land committed than owned",
                    String.format("%.0f of %.0f", l.getAllocatedSqFt(), l.getOwnedSqFt()));
        }

        if (g.getDebtManager().getAllPrincipal() < 0) {
            flag(month, "negative city debt", "" + g.getDebtManager().getAllPrincipal());
        }

        if (roads.getCapacity() < InfrastructureManager.BASE_CAPACITY - 1e-9) {
            flag(month, "road capacity below the base network", "" + roads.getCapacity());
        }
        if (roads.getLoad() < 0) {
            flag(month, "negative road load", "" + roads.getLoad());
        }
        double ratio = roads.getThroughputRatio();
        if (ratio < InfrastructureManager.MIN_THROUGHPUT - 1e-9 || ratio > 1 + 1e-9) {
            flag(month, "road throughput outside its bounds", "" + ratio);
        }

        double energy = g.getEnergyRatio();
        double water = g.getWaterRatio();
        if (energy < 0 || energy > 1 + 1e-9) {
            flag(month, "energy ratio outside 0..1", "" + energy);
        }
        if (water < 0 || water > 1 + 1e-9) {
            flag(month, "water ratio outside 0..1", "" + water);
        }

        for (BuildingsTemplate t : b.getTemplates()) {
            if (b.getQuantity(t.getId()) < 0) {
                flag(month, "negative building count", t.getName());
            }
        }

        for (String sector : BusinessDebtManager.SECTORS) {
            BusinessDebtManager credit = e.getBusinessDebtManager();
            double principal = credit.getPrincipal(sector);
            finite(month, "business debt (" + sector + ")", principal);
            finite(month, "business rate (" + sector + ")", credit.getRate(sector));
            finite(month, "business leverage (" + sector + ")", credit.getLeverage(sector));
            if (principal < -1e-6) {
                flag(month, "negative business debt", sector + " " + principal);
            }
        }

        /*
         * Everything the save actually writes.
         *
         * Gson refuses to serialise NaN or Infinity - it throws
         * IllegalArgumentException rather than writing it - so ONE field going
         * non-finite anywhere in the city means every save from that moment on
         * fails, including the autosave, which fires inside a skip where the
         * player cannot see the error. Finding the first month it happens and
         * the name of the field is the whole job; a save that throws is not a
         * degraded save, it is no save at all.
         */
        finite(month, "SAVE: construction cash",
                g.getServicesManager().getConstructionHandler().getCash());
        finite(month, "SAVE: construction unearned revenue",
                g.getServicesManager().getConstructionHandler().getUnearnedRevenue());
        finite(month, "SAVE: construction backlog",
                g.getServicesManager().getConstructionHandler().getBacklogPoints());
        finite(month, "SAVE: commercial cash", e.getCommercialHandler().getCommercialCash());
        finite(month, "SAVE: real estate cash", e.getCommercialHandler().getRealEstateCash());
        finite(month, "SAVE: industrial cash", e.getIndustrialCash());
        finite(month, "SAVE: heavy industry cash", e.getHeavyIndustryHandler().getCash());
        finite(month, "SAVE: household savings", g.getHouseholds().getCumulativeSaving());
        finite(month, "SAVE: land owned", l.getOwnedSqFt());
        finite(month, "SAVE: land price", l.getPricePerSqFt());
        finite(month, "SAVE: property tax charged", e.getTotalPropertyTax());
        finite(month, "SAVE: accrued city interest", e.getExpenses());
        finite(month, "SAVE: retail cost of goods", e.getRetailCostOfGoods());
        finite(month, "SAVE: retail fill basis", e.getRetailFillBasis());
        finite(month, "SAVE: retail import tax", e.getRetailImportTax());
        finite(month, "SAVE: industry demand", e.getIndustryDemand());

        finiteArray(month, "SAVE: property tax charges", e.getPropertyTaxCharges());
        finiteArray(month, "SAVE: interest charges", e.getInterestCharges());
        finiteArray(month, "SAVE: national accounts", e.getNationalAccountsState());
        finiteArray(month, "SAVE: commercial report", e.getCommercialReportState());
        finiteArray(month, "SAVE: industrial report", e.getIndustrialReportState());
        finiteArray(month, "SAVE: heavy industry report", e.getHeavyIndustryReportState());
        finiteArray(month, "SAVE: mining report", e.getMiningReportState());
        finiteArray(month, "SAVE: land listing", l.getMarket().getListingState());

        finite(month, "ore price", e.getIronMarket().getLocalPrice());
        finite(month, "iron reserves", l.getIronReserveTonnes());
        finite(month, "mining cash", e.getMiningHandler().getCash());

        if (l.getIronReserveTonnes() < 0) {
            flag(month, "negative iron reserves", "" + l.getIronReserveTonnes());
        }
        if (g.minesCommitted() > l.getIronDeposits()) {
            flag(month, "more mines than deposits",
                    g.minesCommitted() + " on " + l.getIronDeposits());
        }
        if (l.getListing().size() != LandMarket.LISTING_SIZE) {
            flag(month, "the land office window is the wrong size",
                    "" + l.getListing().size());
        }

        double ore = e.getIronMarket().getLocalPrice();
        if (ore < e.getIronMarket().getExportPrice() - 1e-9
                || ore > e.getIronMarket().getScrapPrice() + 1e-9) {
            flag(month, "ore price outside its band", "" + ore);
        }
    }

    static void finiteArray(int month, String what, double[] values) {
        if (values == null) return;
        for (int i = 0; i < values.length; i++) {
            finite(month, what + "[" + i + "]", values[i]);
        }
    }

    static void finite(int month, String what, double value) {
        if (Double.isNaN(value)) {
            flag(month, what + " went NaN", "NaN");
        } else if (Double.isInfinite(value)) {
            flag(month, what + " went infinite", "" + value);
        }
    }

    /* ===================================================================
       THE ADVISOR

       What a player would do on looking at the city. One or two moves per
       stop, in priority order, because that is what a person does - they fix
       the thing that is obviously wrong and press on.
       =================================================================== */

    /**
     * ONE THING THE CITY COULD DO, AND WHAT IT IS WORTH.
     *
     * Everything is priced in the same unit - monthly output, gained or
     * restored - so a road and a clinic and a grocery can be compared without
     * anybody deciding in advance which matters more. That comparison is the
     * whole point; see advise().
     */
    record Move(String label, double lost, java.util.function.BooleanSupplier act) { }

    /**
     * WHAT THE CITY DOES NEXT, AND WHY THIS IS NOT A LIST OF RULES ANY MORE.
     *
     * THE OLD SHAPE AND WHAT IT COST. This was an ordered sequence of ifs, each
     * with a hard cap on it - build roads while there are fewer than 40, build
     * a food plant while there are fewer than 8, and so on. Every one of those
     * numbers was invented, none of them was ever revisited, and three of them
     * turned out in a single afternoon to have been silently deciding what the
     * SIMULATION appeared to do:
     *
     *   - no clinic rule at all, so healthcare coverage sat at 1% for four
     *     thousand months and the city ran permanently at the untreated sick
     *     rate. Thirty of the forty points of "the sickness doom loop" - written
     *     up three times as a defect in the model - were this.
     *   - no cemetery rule, so 10,754 bodies went unburied and the death-care
     *     mechanic quietly stopped existing.
     *   - `Paved Road < 40`, in a city of 198,000, so roads sat at 35%
     *     throughput, the shops could deliver a fifth of what households planned
     *     to buy, and two thirds of the city went hungry. Read as a retail
     *     problem for weeks.
     *
     * A cap that never binds costs nothing. A cap that binds is a conclusion
     * about the game, written years earlier by somebody who was thinking about
     * something else, and it is indistinguishable from the model's own
     * behaviour when you read the output.
     *
     * WHAT REPLACES IT. Every candidate is priced in the same unit - the
     * monthly output it restores or unlocks - and divided by what it costs. The
     * best score wins. Nothing has a cap, because nothing needs one: a road
     * stops being worth buying when roads stop being what is throttling the
     * city, and that is measured rather than asserted.
     *
     * AND IT RANKS CONSTRAINTS, NOT PURCHASES. The first attempt scored every
     * building by gain-per-dollar and was measurably worse than the rule list
     * it replaced - population 198,000 -> 90,000, with power sitting at 8% for
     * three centuries. The probe said why in one line:
     *
     *     Coal Power Plant    cost 197,640   gain 531   score 0.0027
     *     Gravel Road         cost   1,800   gain  75   score 0.0417
     *
     * A power plant costs a hundred and ten times a gravel road. Crediting each
     * candidate with the whole gap it addresses is fine when the candidates are
     * the same size and nonsense when they are not: the road was credited with
     * closing a road shortfall it could never close, won every month for ever,
     * and the city ran on 8% power while buying its eighty-third gravel road.
     * Per-dollar comparison across two orders of magnitude of scale is not a
     * comparison, it is a preference for cheap things.
     *
     * So the ranking is over CONSTRAINTS - what is this shortage costing the
     * city a month - and the purchase is chosen inside the winner. Which is
     * what a player does: see that power is at 8%, decide power is the problem,
     * then ask which power building. Two decisions, neither of which requires
     * pricing a hospital against a road.
     *
     * The caps are still gone, which was the point of the rewrite. Nothing says
     * "at most 40 roads"; roads stop winning when roads stop being what the
     * city is losing most output to, and that is measured every month.
     */
    static String advise(Game g) {

        EconomyManager e = g.getEconomyManager();
        PopulationManager p = g.getPopulationManager();
        BuildingManager b = g.getBuildingManager();
        LandManager land = g.getLandManager();
        Health health = g.getHealth();

        double gdp = Math.max(1, e.getMonthGdp());
        int population = p.getPopulation();
        double perHead = population > 0 ? gdp / population : 0;

        /* ================================================================
           THE TWO THINGS THAT ARE NOT PURCHASES, done first and for free.

           Neither costs a move, because neither is a month's work. Setting a
           subsidy and topping up a reserve are decisions you make while doing
           something else - and the retainer in particular has to be set BEFORE
           the depot rule can ever be reached, which is the ordering fault that
           cost a whole playtest the last time this file was rewritten.
           ================================================================ */

        if (!g.isAutoSubsidised(PolicySector.CONSTRUCTION)) {
            g.setAutoSubsidised(PolicySector.CONSTRUCTION, true);
        }

        /*
         * ...AND IT SETS THE POLICY RATE.
         *
         * K1, earned by shipping out-migration that fired zero times in 4,002
         * months: a mechanic the fixture never touches is a mechanic no run has
         * ever tested. The advisor ranks constraints by output lost and
         * inflation is not one of those, so without this rule the rate dial
         * would sit at 3% for three centuries and the whole of phase 5's
         * monetary half would go unexercised.
         *
         * It follows the advisory rule, moving a quarter of the way each time,
         * because that is what the screen recommends and a fixture should play
         * the game the way it is taught rather than better than it is taught.
         */
        DebtManager market = g.getDebtManager();
        if (g.getPriceIndex().hasRate()) {
            double advised = market.advisedPolicyRate(g.getPriceIndex().inflation());
            double now = market.getPolicyRate();
            market.setPolicyRate(now + (advised - now) * .25);
        }

        /*
         * THE WAR CHEST. Hot money that is not backed by reserves is one shock
         * away from leaving, and reserves are the only defence the city has -
         * so a careful treasury tops them up while it still can. Deliberately
         * a rule rather than a scored move: the return on insurance is a loss
         * that does not happen, and scoring that against a grocery store would
         * be inventing a number to compare with a measured one.
         */
        CapitalFlows hot = g.getCapitalFlows();
        double wanted = hot.getStock() * CapitalFlows.PANIC_BACKING * 1.5;
        double held = g.getForeignAccounts().getReserves();
        if (wanted > held) {
            double top = Math.min(wanted - held, g.getCash() * .10);
            if (top > 0) g.buyForeignCurrency(top);
        }

        /* ================================================================
           AND EVERYTHING THAT IS A PURCHASE.
           ================================================================ */

        java.util.List<Move> moves = new java.util.ArrayList<>();

        /* --- room to grow. Not a building, and it gates every building. --- */
        double utilisation = land.getOwnedSqFt() > 0
                ? land.getAllocatedSqFt() / land.getOwnedSqFt() : 1;
        if (utilisation > .85) {
            LandParcel room = land.getMarket().bestValue();
            if (room != null) {
                /*
                 * Priced as the whole city's output, because a city with no
                 * ground stops entirely - measured, in an earlier playtest: it
                 * ran down to 8,000 spare square feet with $106M in the bank
                 * and then shed its construction sector from 2,900 capacity to
                 * 100 over four months.
                 */
                moves.add(new Move("bought room to grow",
                        gdp * (utilisation - .85) / .15,
                        () -> g.buyLandParcel(room.getId())));
            }
        }

        /* --- the four throttles, each priced at the output it is costing --- */
        /*
         * POWER, AND BOTH OF IT, for exactly the reason the roads below are
         * offered three ways. This rule only ever built Coal Power Plants,
         * which was fine while a coal plant was the only generator in the game
         * and stopped being fine on 2026-09-09 when the Wind Farm arrived.
         *
         * The two are costed so each wins a band of LAND prices - wind is about
         * a quarter cheaper per unit of energy delivered and sits on thirty
         * times the ground, so it wins on cheap land and loses on dear. A rule
         * that names one of them decides that question in advance and the band
         * may as well not exist; offering both lets the price of land pick, and
         * that is the whole point of having two.
         *
         * It also matters more than the roads did: a Wind Farm is $31.8M and
         * 1,800 construction points against a coal plant's $1.43B and 120,000,
         * so it is the only power a young city can actually reach.
         */
        double powerGain = gdp * (1 - g.getEnergyRatio());
        addThrottle(moves, g, "Wind Farm", "wind farm", powerGain);
        addThrottle(moves, g, "Coal Power Plant", "power plant", powerGain);
        addThrottle(moves, g, "Water Treatment Plant", "water plant",
                gdp * (1 - g.getWaterRatio()));

        /*
         * ROADS, AND ALL THREE OF THEM. The old rule only ever built Paved
         * Roads and stopped at forty; three_roads.md costed a Gravel Road, a
         * Paved Road and an Elevated Highway so that each is the cheapest per
         * trip across a band of land prices, and the advisor never used two of
         * them. Offered as three moves now, so the price of land picks.
         */
        double roadGain = gdp * (1 - g.getRoadRatio());
        addThrottle(moves, g, "Gravel Road", "gravel road", roadGain);
        addThrottle(moves, g, "Paved Road", "roads", roadGain);
        addThrottle(moves, g, "Elevated Highway", "highway", roadGain);

        /* --- and the two health terms, which are throttles wearing a hat --- */
        double treatable = Math.max(0, health.getBaselineRate() - Health.WELL_SERVED_RATE);
        addThrottle(moves, g, "Walk-in Clinic", "clinics", gdp * treatable);
        addThrottle(moves, g, "Community Health Centre", "health centre", gdp * treatable);
        addThrottle(moves, g, "General Hospital", "hospital", gdp * treatable);

        if (g.getHealthcare().getUnburied() > 0) {
            double buryGain = gdp * health.getUnburiedRate();
            addThrottle(moves, g, "Municipal Cemetery", "cemetery", buryGain);
            addThrottle(moves, g, "Crematorium", "crematorium", buryGain);
        }

        /* --- somewhere to live, and somewhere to shop --- */
        double homesShort = population - b.getTotalHouseCapacity();
        if (homesShort > -4) {
            /*
             * Priced on the output the people it houses would produce, less a
             * discount because they arrive over the years rather than next
             * month. Growth is worth less per dollar than restoration, and it
             * should be - a city that builds houses while its power is out has
             * simply moved the shortage.
             */
            double housingGain = perHead * Math.max(20, homesShort) * GROWTH_DISCOUNT;
            addThrottle(moves, g, "House", "houses", housingGain);
            addThrottle(moves, g, "Low-Rise Apartments", "apartments", housingGain);
            addThrottle(moves, g, "Studio Apartments", "studios", housingGain);
        }

        double shopShort = population - b.getTotalStoreCoverage();
        if (shopShort > 0) {
            double shopGain = perHead * shopShort * GROWTH_DISCOUNT;
            addThrottle(moves, g, "Convenience Store", "shops", shopGain);
            addThrottle(moves, g, "Small Grocery Store", "grocery", shopGain);
        }

        /* --- the builders, without whom none of the above arrives --- */
        double capacity = b.getTotalConstructionCapacity();
        double wantedCapacity = Math.max(1500, population * .02);
        if (capacity < wantedCapacity) {
            addThrottle(moves, g, "Construction Depot", "depot",
                    gdp * (wantedCapacity - capacity) / wantedCapacity * GROWTH_DISCOUNT);
        }
        /*
         * A unit of material was repriced 9x on 2026-09-10 and the counts
         * with it, so the yard the advisor wants is a ninth of the count it
         * used to want - the same value of stock per resident as before.
         */
        if (b.getConstructionMaterials() < population * .0056) {
            addThrottle(moves, g, "Construction Materials Plant", "materials plant",
                    gdp * .05 * GROWTH_DISCOUNT);
        }

        /* --- food the city grows rather than buys --- */
        /*
         * ON WHAT THE MILLS CAN MAKE, NOT ON WHAT IS IN THE SHED (2026-09-10).
         *
         * This read `foodInventory < population x 2`, which was a reading of
         * need while a plant ran at nameplate and the shed filled. Since the
         * distress batch a plant idles at STOCK_MONTHS (two) of the shops'
         * demand - and the shops' demand is min(coverage, population), which
         * is below population whenever a shop is short and the shed is drawn
         * down by a month's sales before this reads it - so the condition
         * was true at almost every stop of every run: 163 to 185 Food
         * Processing Plants a run, one per stop, $2bn of public money handed
         * to the food sector, which exported the spare nameplate at the
         * world's price and ended runs holding $25-51bn. Every "big" city in
         * the first ensembles was this, and it was the harness, not the game.
         *
         * A player builds a mill when the city cannot feed itself: when the
         * mills' nameplate is below what the shops will want. That is the
         * figure the planner and the throttle both work from.
         */
        if (b.getFoodProduction() < Math.min(b.getTotalStoreCoverage(), population)) {
            addThrottle(moves, g, "Food Processing Plant", "food plant",
                    gdp * .05);
        }

        /* --- ore, which is the one thing the private sector cannot buy --- */
        if (land.getIronDeposits() <= g.minesCommitted()) {
            LandParcel deposit = land.getMarket().richestDeposit();
            if (deposit != null) {
                moves.add(new Move("bought a deposit", gdp * .03,
                        () -> g.buyLandParcel(deposit.getId())));
            }
        }
        /*
         * ...AND ONLY A MINE THAT WOULD PAY (2026-09-10). A deposit the city
         * owns is not a reason to sink a mine on it, and this used to be one:
         * the city built a mine, handed it to the mining sector, watched it
         * lose money at the export floor with the currency at half of
         * parity, watched the distress rule scrap it, saw the deposit
         * unworked again, and built another - 136 to 155 Iron Mines a run,
         * $4.5-6.5bn written off, twenty-two write-downs on the sector that
         * was given them. The screen the private sector uses is the one a
         * player would read: what one of these would clear, at the price
         * and the staffing the mines actually get.
         */
        if (land.hasUnminedDeposit(g.minesCommitted()) && wouldPay(g, "Iron Mine", BusinessDebtManager.MINING)) {
            addThrottle(moves, g, "Iron Mine", "mine", gdp * .05);
        }

        /* --- and jobs, when there are people with nothing to do --- */
        double idle = p.getWorkforce() - p.getTotalJobs();
        if (idle > 0) {
            double jobGain = perHead * idle * GROWTH_DISCOUNT;
            // Same test as the mine: 72 foundries went up in one run for the
            // jobs alone, into a market that could not pay for the steel.
            if (wouldPay(g, "Steel Foundry", BusinessDebtManager.HEAVY_INDUSTRY)) {
                addThrottle(moves, g, "Steel Foundry", "foundry", jobGain);
            }
            addThrottle(moves, g, "Textile Mill", "mill", jobGain);
        }

        /* ================================================================
           AND THE BEST OF THEM WINS.
           ================================================================ */

        moves.sort((x, y) -> Double.compare(y.lost(), x.lost()));
        for (Move m : moves) {
            if (m.lost() <= 0) break;
            if (m.act().getAsBoolean()) return m.label();
        }
        return null;
    }

    /**
     * How much of a gain arrives later rather than now.
     *
     * Restoring a throttle pays this month; a house pays when somebody moves
     * into it and takes a job. Without a discount the advisor builds for a city
     * that does not exist yet while the one that does sits in the dark - which
     * is the failure mode the old ordering was hand-built to avoid, and this is
     * the one number that replaces that ordering.
     */
    static final double GROWTH_DISCOUNT = .15;

    /**
     * Adds one building as a way of relieving a constraint worth `lost` a month.
     *
     * Candidates for the SAME constraint are given the same figure and are
     * separated by a nudge in listing order, so the list stays a ranking of
     * shortages with the alternatives for each sitting together. Cost does not
     * enter here at all - that was the mistake this rewrite exists to undo -
     * beyond build() refusing what the city cannot fund.
     */
    static void addThrottle(java.util.List<Move> moves, Game g,
                            String name, String label, double lost) {
        addThrottle(moves, g, name, label, lost, lost / Math.max(1, g.getEconomyManager().getMonthGdp()));
    }

    /**
     * @param gap the share of output this shortage is costing, 0-1, which sets
     *            how many of the building to order
     */
    static void addThrottle(java.util.List<Move> moves, Game g,
                            String name, String label, double lost, double gap) {
        if (lost <= 0) return;
        BuildingsTemplate t = template(g, name);
        if (t == null) return;

        /*
         * ORDERED IN PROPORTION TO THE SHORTAGE, and this was the second thing
         * the rewrite got wrong. Buying ONE of whatever wins meant roads sat at
         * 64% for three centuries in a growing city: the advisor bought a road
         * every time roads won, roads won every month, and one road a month
         * never caught a city adding thousands of people a year. It was
         * perfectly responsive and far too small.
         *
         * A player looking at 36% of their output going missing does not buy
         * one road. Twenty-five is the scale at which a full outage orders a
         * serious block of building and a 4% shortfall orders one.
         */
        int quantity = (int) Math.max(1, Math.min(25, Math.ceil(gap * 25)));
        moves.add(new Move(label, lost * (1 - moves.size() * 1e-6),
                () -> build(g, name, quantity)));
    }

    /** Whether one of these would clear its own running costs, on the private sector's screen. */
    static boolean wouldPay(Game g, String name, String sector) {
        BuildingsTemplate t = template(g, name);
        return t != null && g.getBusinessInvestment().estimatedMonthlyProfit(sector, t) > 0;
    }

    static int qty(Game g, String name) {
        BuildingsTemplate t = template(g, name);
        return t == null ? 0 : g.getBuildingManager().getQuantity(t.getId());
    }

    /**
     * Orders a building the way the screens do: check land, buy some if short,
     * borrow if the treasury cannot cover it, then place the order.
     */
    static boolean build(Game g, String name, int quantity) {

        BuildingsTemplate t = template(g, name);
        if (t == null) return false;

        // Land first, exactly as buildStack() checks it first.
        int guard = 0;
        while (g.getLandManager().getAvailableSqFt() < t.getLandSqFt() * quantity
                && guard++ < 60) {
            if (!g.buyLandBlock()) break;
        }

        Game.BuildResult result = g.buildStack(t, quantity, false);

        if (result == Game.BuildResult.NO_LAND) {
            refusal(name + ": no land");
        }

        if (result == Game.BuildResult.NEEDS_FUNDING) {
            // A city borrows for capital projects. Long bonds, because the
            // whole point of the instrument is small monthly payments.
            double needed = Math.max(t.getCashCost() * quantity * 1.6, 5000);
            if (canService(g, needed)) {
                /*
                 * AND IT TAKES THE CHEAPER MONEY, which is what a treasury
                 * does and what makes this a test rather than a demonstration.
                 *
                 * The playtest is the only thing that runs the foreign
                 * instruments for four thousand months against a real currency,
                 * a real export base and a real bank; a rule that never
                 * borrowed abroad would leave every one of those months
                 * unexercised. Taking whichever is cheaper is also the naive
                 * strategy a first-time player will follow, which is exactly
                 * the one worth knowing the consequences of.
                 */
                DebtManager m = g.getDebtManager();
                if (m.foreignWindowOpen() && m.foreignRate() < m.getRate() * .8) {
                    g.handleForeignLogic("Term", needed / Math.max(.01, fxRate(g)),
                            20, 100, false);
                } else {
                    g.handleLongBondLogic(needed, 20, 100);
                }
                result = g.buildStack(t, quantity, false);
            }
        }

        if (result == Game.BuildResult.NEEDS_FUNDING) {
            refusal(name + ": no money");
        } else if (result == Game.BuildResult.SUCCESS) {
            refusal(name + ": BUILT");
        }

        return result == Game.BuildResult.SUCCESS;
    }

    /**
     * Whether the advisor can afford the PAYMENTS, not whether it likes the size.
     *
     * WHY THE PRINCIPAL LIMIT HAD TO GO
     *
     * This used to be "borrow while principal < 60 months of tax revenue".
     * Before the debt market was repriced, that was a workable proxy, because
     * every loan cost about the same: a city with no debt was quoted 1% whatever
     * it asked for. It is meaningless now. The same principal can cost 1% or
     * 20% depending on how large it is relative to the city, so a limit written
     * in units of principal says nothing about whether the city can pay.
     *
     * Left alone through the repricing it did what you would expect: the advisor
     * kept borrowing five years of revenue at rates approaching the ceiling, the
     * interest ate the budget, and the 4,000-month run finished at 6,372 people
     * and 3,453 months in emergency funding - against 39,875 before. Every
     * number in that run was about this line.
     *
     * A debt-service ratio is what a real municipal borrower actually tests, and
     * it self-scales: as the quoted rate rises, the amount that passes falls.
     * A quarter of revenue going to interest is at the aggressive end of what
     * real cities carry, which suits an advisor that is supposed to push.
     */
    static final double DEBT_SERVICE_LIMIT = .25;

    /** Local per USD, for turning a local-money need into a dollar ask. */
    static double fxRate(Game g) { return g.getForeignAccounts().getRate(); }

    static boolean canService(Game g, double extra) {

        double monthlyRevenue = Math.max(g.getEconomyManager().getTaxIncome(), 0);
        if (monthlyRevenue <= 0) {
            return false;   // nothing to service it with
        }

        DebtManager market = g.getDebtManager();

        // Quoted WITH the new loan in it, which is the whole point of the
        // repricing: ask what this specific borrowing would cost, not what the
        // balance sheet happens to look like before the money arrives.
        double rate = market.quoteRate(extra);
        double projected = market.getAllPrincipal() + extra;
        double monthlyInterest = projected * rate / 12.0;

        return monthlyInterest <= monthlyRevenue * DEBT_SERVICE_LIMIT;
    }


    /**
     * Why the advisor could not do the thing it wanted to.
     *
     * A city that stops growing is the single most interesting thing a long run
     * can show, and "it stopped" is not a diagnosis. Counting the refusals says
     * whether it ran out of land, money, or ideas.
     */
    static final Map<String, Integer> refusals = new LinkedHashMap<>();

    static void refusal(String why) {
        refusals.merge(why, 1, Integer::sum);
    }

    static BuildingsTemplate template(Game g, String name) {
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        return null;
    }

    /* ===================================================================
       RUNNING MONTHS

       Every month is audited whether it was played by hand or inside a skip.
       A hundred-month skip that quietly breaks something at month 40 and
       repairs it by month 100 would otherwise look identical to one that
       never had a problem - which is the same reason TimeSkipReport samples
       month by month instead of diffing the endpoints.
       =================================================================== */

    static int refusedSkips = 0;

    static void run(Game g, int months) {
        for (int i = 0; i < months; i++) {

            int before = g.getMonth();
            g.simulateMonths(1);
            lifetimeWriteOffs += g.getBank().getWriteOffs();
            lifetimeHouseholdWriteOffs += g.getHouseholdBalance().getWrittenOff();

            /*
             * THE PLAYER RECAPITALISES ITS BANK, because a player would.
             *
             * A frozen bank means no credit, and no credit means nothing gets
             * built - so a treasury sitting on billions while its banking system
             * is shut is not a simulated player, it is a simulated bystander.
             * This exercises the one lever the failure mechanic has, which is
             * otherwise never pulled in four thousand months.
             *
             * Capped at a quarter of the treasury so it stays a decision with a
             * cost rather than a reflex.
             */
            double needed = g.bankRecapitalisationNeeded();
            if (needed > 0 && g.getCash() > 0) {
                double put = g.recapitaliseBank(Math.min(needed, g.getCash() * .25));
                if (put > 0) lifetimeBailouts += put;
                if (put > 0 && Boolean.getBoolean("playtest.fx")) {
                    Bank b = g.getBank();
                    out.printf("   bank m%-4d put %,.0f (%s) | equity now %,.0f, weighted book %,.0f, loans %,.0f, securities %,.0f | month: net income %,.0f trading %,.0f write-offs %,.0f dividend %,.0f | failed %d times, lifetime %,.0f%n",
                        g.getMonth(), put, b.getFailures() > failuresSeen ? "FAILED" : "topped up", b.equity(), b.getWeightedBook(), b.getBook(), b.getSecurities(),
                        b.getNetIncome(), b.getTradingIncome(), b.getWriteOffs(), b.getDividendsPaid(), b.getFailures(), lifetimeBailouts);
                    failuresSeen = b.getFailures();
                }
            }

            if (g.getMonth() == before) {
                /*
                 * simulateMonths() refuses to run at all while cash <= 0, but
                 * the Next Month button calls nextMonth() directly, which
                 * issues emergency debt and carries on. So a broke player can
                 * step but cannot skip - and stepping is exactly what they
                 * would do next. Doing the same here rather than giving up,
                 * because a city that cannot pay its bills is a state the game
                 * has to keep working in, not one to stop testing at.
                 */
                refusedSkips++;
                g.toggleNextMonth();

                if (g.getMonth() == before) {
                    flag(before, "the city could not advance even one month",
                            "cash " + g.getCash());
                    return;
                }
            }
            audit(g);
        }
    }

    /* ===================================================================
       SAVE / RELOAD, MID-RUN

       The strongest single check here. Everything the city holds has to
       survive a round trip, on whatever state a real run happens to be in -
       which is a far wider range of states than any fixture builds
       deliberately.
       =================================================================== */

    static void roundTrip(Game g, GameFiles files, int slot) {

        double cash = g.getCash();
        double income = g.getIncome();
        int pop = g.getPopulationManager().getPopulation();
        int workforce = g.getPopulationManager().getWorkforce();
        double gdp = g.getEconomyManager().getMonthGdp();
        double retail = g.getEconomyManager().getCommercialHandler().getGrossRevenue();
        double interest = g.getEconomyManager().getExpenses();
        int month = g.getMonth();

        if (!g.saveGame(slot, "playtest m" + month).ok) {
            flag(month, "a save failed", "slot " + slot);
            return;
        }

        Game back = new Game(files);
        back.loadGameSave(slot);

        if (back.getLoadFailure() != null) {
            flag(month, "a load failed", back.getLoadFailure());
            return;
        }

        same(month, "cash across a save", back.getCash(), cash);
        same(month, "next-month income across a save", back.getIncome(), income);
        same(month, "monthly GDP across a save", back.getEconomyManager().getMonthGdp(), gdp);
        same(month, "retail revenue across a save",
                back.getEconomyManager().getCommercialHandler().getGrossRevenue(), retail);
        same(month, "accrued city interest across a save",
                back.getEconomyManager().getExpenses(), interest);

        // Component by component, so a drift names itself instead of being
        // reported as "the income moved" three thousand months from its cause.
        EconomyManager was = g.getEconomyManager();
        EconomyManager now = back.getEconomyManager();
        same(month, "  ...business tax", now.getBusinessTax(), was.getBusinessTax());
        same(month, "  ...wage tax", now.getWageTax(), was.getWageTax());
        same(month, "  ...sales tax", now.getSalesTax(), was.getSalesTax());
        same(month, "  ...property tax", now.getTotalPropertyTax(), was.getTotalPropertyTax());
        same(month, "  ...industrial tax", now.getIndustrialTax(), was.getIndustrialTax());
        same(month, "  ...utility income",
                back.getServicesManager().getServiceNetIncome(),
                g.getServicesManager().getServiceNetIncome());
        same(month, "  ...mining net income",
                now.getMiningHandler().getReportNetIncome(),
                was.getMiningHandler().getReportNetIncome());
        same(month, "  ...heavy industry net income",
                now.getHeavyIndustryHandler().getReportNetIncome(),
                was.getHeavyIndustryHandler().getReportNetIncome());
        same(month, "  ...ore price",
                now.getIronMarket().getLocalPrice(), was.getIronMarket().getLocalPrice());

        /*
         * THE BORROWER'S RECORD. The loans were always restored; the count of
         * write-downs and the months of ban left were not, and nothing here
         * compared them - which is why 49 reloads came back "matched" with a
         * sector's twelve restructures reading zero. A record that is not
         * compared is not carried.
         */
        BusinessDebtManager creditWas = was.getBusinessDebtManager();
        BusinessDebtManager creditNow = now.getBusinessDebtManager();
        for (String sector : BusinessDebtManager.SECTORS) {
            if (creditNow.getRestructureCount(sector) != creditWas.getRestructureCount(sector)) {
                flag(month, "a sector's DEFAULT RECORD did not survive the save",
                        sector + ": " + creditWas.getRestructureCount(sector)
                        + " restructures -> " + creditNow.getRestructureCount(sector));
            }
            if (creditNow.getBlockedMonths(sector) != creditWas.getBlockedMonths(sector)) {
                flag(month, "a sector's BORROWING BAN did not survive the save",
                        sector + ": " + creditWas.getBlockedMonths(sector)
                        + " months left -> " + creditNow.getBlockedMonths(sector));
            }
        }
        same(month, "the bank's lifetime resolution loss across a save",
                back.getBank().getResolutionLoss(), g.getBank().getResolutionLoss());
        same(month, "what the sectors hold abroad across a save",
                back.getOutwardInvestment().totalUsd(), g.getOutwardInvestment().totalUsd());
        same(month, "...and the rate it was valued at",
                back.getOutwardInvestment().getLastRate(), g.getOutwardInvestment().getLastRate());
        same(month, "...and the financial account the rate is priced on",
                back.getForeignAccounts().monthlyFinancialAccount(), g.getForeignAccounts().monthlyFinancialAccount());
        // The register: every company's shares in issue and abroad, and the
        // households' own count of what they hold - three stocks nothing can
        // re-derive from the month a save was taken in.
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            same(month, Equity.COMPANIES[c] + "'s shares in issue across a save",
                    back.getEquity().getShares(c), g.getEquity().getShares(c));
            same(month, "...and held abroad",
                    back.getEquity().getForeignShares(c), g.getEquity().getForeignShares(c));
            same(month, "...and held by the households",
                    back.getHouseholdBalance().sharesHeld(c), g.getHouseholdBalance().sharesHeld(c));
            // The exchange: the desk's inventory, the quote it closed at and
            // the demand it still carries - the next month trades at them.
            same(month, "...and on the desk",
                    back.getEquity().getDealerShares(c), g.getEquity().getDealerShares(c));
            same(month, "...and its quote",
                    back.getExchange().mid(c), g.getExchange().mid(c));
            same(month, "...and the demand its quote carries",
                    back.getExchange().getDemand(c), g.getExchange().getDemand(c));
            same(month, "...and its split factor",
                    back.getExchange().getSplitFactor(c), g.getExchange().getSplitFactor(c));
        }
        same(month, "the bank's securities at the mark across a save",
                back.getBank().getSecurities(), g.getBank().getSecurities());
        same(month, "what the households hold abroad across a save",
                back.getHouseholdBalance().totalAbroadUsd(), g.getHouseholdBalance().totalAbroadUsd());

        /*
         * PRICES THAT ARE CACHES. Each of these is struck once a month and
         * read all month; the load path does not run the strike, so each has
         * to be carried. None was compared here until 2026-09-10, and three
         * were not carried: the economy's exchange rate read the founding 1.0
         * (every import at its founding price, next month's GDP 6.8% low),
         * the land office read its founding ground price (160x too cheap),
         * and the labour market's tightness read a flat 1.00 down the table.
         */
        same(month, "the exchange rate the economy trades at, across a save",
                now.getExchangeRate(), was.getExchangeRate());
        same(month, "the land office's ground price across a save",
                back.getLandManager().getAcquisitionCostPerSqFt(),
                g.getLandManager().getAcquisitionCostPerSqFt());
        same(month, "the land office's minimum lot across a save",
                back.getLandManager().getMarket().getMinBlocks(),
                g.getLandManager().getMarket().getMinBlocks());
        for (WageBand band : WageBand.values()) {
            same(month, "the labour market's tightness across a save (" + band + ")",
                    back.getLabourMarket().getTightness(band),
                    g.getLabourMarket().getTightness(band));
        }

        if (back.getPopulationManager().getPopulation() != pop) {
            flag(month, "population across a save",
                    back.getPopulationManager().getPopulation() + " != " + pop);
        }
        if (back.getPopulationManager().getWorkforce() != workforce) {
            flag(month, "workforce across a save",
                    back.getPopulationManager().getWorkforce() + " != " + workforce);
        }
    }

    static void same(int month, String what, double actual, double expected) {
        if (Math.round(actual * 10000) != Math.round(expected * 10000)) {
            flag(month, what, String.format("%.4f != %.4f", actual, expected));
        }
    }

    /* =================================================================== */

    static double lifetimeWriteOffs;
    static double lifetimeBailouts;
    static int failuresSeen;
    static double lifetimeHouseholdWriteOffs;

    public static void main(String[] args) throws Exception {

        out = System.out;

        Path root = Files.createTempDirectory("playtest");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));

        // The game narrates every month. Four thousand months of it is neither
        // fast nor readable, so it goes nowhere.
        PrintStream quiet = new PrintStream(new OutputStream() {
            @Override public void write(int b) { }
            @Override public void write(byte[] b, int off, int len) { }
        });

        Game g = new Game(files);
        List<String> log = new ArrayList<>();
        long started = System.currentTimeMillis();

        System.setOut(quiet);
        try {
            g.run();

            /* ---------- founding: a few months at a time, by hand ---------- */
            /*
             * ENSEMBLE MODE: -Dplaytest.seed=N nudges the founding order - a few
             * houses more, a month or two longer - so that a set of runs can be
             * compared as a distribution. The game is deterministic and four
             * thousand months amplify any change into a different city, so a
             * single run before and after a change measures the change plus the
             * weather. Six seeds either side is the least that separates them.
             * Seed 0 (the default) is the run as it has always been.
             */
            int seed = Integer.getInteger("playtest.seed", 0);
            build(g, "House", 40 + seed % 4);
            build(g, "Convenience Store", 3);
            run(g, 3 + (seed / 4) % 3);
            build(g, "House", 20 + (seed / 12) % 3);
            run(g, 4);
            build(g, "Convenience Store", 2);
            build(g, "Construction Depot", 1);
            run(g, 5);
            advise(g);
            run(g, 6);

            log.add(era(g, "founded, played by hand"));

            /* ---------- then the real rhythm ---------- */
            int stop = 0;
            int slot = 1;
            int nextCheckpoint = 250;

            while (g.getMonth() < TARGET_MONTHS) {

                stop++;

                // A skip, of the length a person actually clicks: mostly a
                // year or a decade, occasionally a century.
                int skip = switch (stop % 6) {
                    case 0 -> 100;
                    case 1 -> 12;
                    case 2 -> 24;
                    case 3 -> 60;
                    case 4 -> 6;
                    default -> 120;
                };
                run(g, Math.min(skip, TARGET_MONTHS - g.getMonth()));

                // Look at the city, fix the worst thing, then a few hands-on
                // months watching what that did - the way anyone plays.
                for (int move = 0; move < 3; move++) {
                    if (advise(g) == null) break;
                    run(g, 1);
                }
                run(g, 2);

                // Policy changes, occasionally, the way a player fiddles.
                if (stop % 11 == 0) {
                    TaxPolicy tax = g.getEconomyManager().getTaxPolicy();
                    double income = tax.getIncomeTaxRate();
                    tax.setIncomeTaxRate(income > .25 ? .12 : income + .06);
                    log.add(String.format("  m%-5d income tax -> %.0f%%",
                            g.getMonth(), tax.getIncomeTaxRate() * 100));
                }
                if (stop % 17 == 0) {
                    TaxPolicy tax = g.getEconomyManager().getTaxPolicy();
                    double prop = tax.getPropertyTaxRate();
                    tax.setPropertyTaxRate(prop > .03 ? .01 : prop + .01);
                    log.add(String.format("  m%-5d property tax -> %.1f%%",
                            g.getMonth(), tax.getPropertyTaxRate() * 100));
                }
                /*
                 * ...AND IT BORROWS ABROAD NOW AND AGAIN.
                 *
                 * The funding path above only reaches for a bond when a build
                 * is refused, and this city is rich enough by year forty that
                 * it never is - so four thousand months went by with the whole
                 * foreign-debt mechanism untouched, and the run proved exactly
                 * nothing about it. This is not the advisor being clever; it is
                 * the harness making sure the code under test actually runs.
                 *
                 * Deliberately naive, in the way a first-time player is naive:
                 * take the cheap dollars when they are on offer, convert them,
                 * spend them at home, and find out later what that costs.
                 */
                if (stop % 19 == 0) {
                    DebtManager m = g.getDebtManager();
                    if (m.foreignWindowOpen() && m.foreignRate() < m.getRate()) {
                        double ask = Math.max(5_000,
                                g.getEconomyManager().getMonthGdp() * .5
                                        / Math.max(.01, fxRate(g)));
                        g.handleForeignLogic("Term", ask, 25, 100, false);
                        log.add(String.format("  m%-5d borrowed US$%,.0fk abroad at %.2f%%",
                                g.getMonth(), ask, m.foreignRate() * 100));
                    }
                }

                // Land price used to be a dial the player turned. It is the
                // market's now, so what a player actually does instead is go
                // and buy some - which changes the price by changing how full
                // the city is.
                if (stop % 13 == 0) {
                    LandParcel spare = g.getLandManager().getMarket().bestValue();
                    if (spare != null && spare.getPrice() < g.getCash() * .1) {
                        g.buyLandParcel(spare.getId());
                    }
                }

                // Scrap something now and then. Demolition is the one thing a
                // player does that makes the city smaller, and the load path
                // has been wrong about it before.
                if (stop % 19 == 0) {
                    BuildingsTemplate house = template(g, "House");
                    if (house != null && g.getBuildingManager().getQuantity(house.getId()) > 60) {
                        g.getBuildingManager().retire(house, 10);
                    }
                }

                // Save and reload against a live city, on whatever state the
                // run happens to be in.
                if (stop % 7 == 0) {
                    roundTrip(g, files, slot);
                    slot = (slot % 9) + 1;
                }

                if (g.getMonth() >= nextCheckpoint) {
                    log.add(era(g, "checkpoint"));
                    log.add(creditEra(g));
                    nextCheckpoint += 250;
                }
            }

            log.add(era(g, "final"));

        } catch (Throwable t) {
            System.setOut(out);
            out.println("\n!!! THREW at month " + g.getMonth() + ": " + t);
            for (StackTraceElement s : t.getStackTrace()) {
                out.println("      " + s);
                if (s.getClassName().startsWith("ham.")) { }
            }
            flag(g.getMonth(), "the simulation threw", t.toString());
        } finally {
            System.setOut(out);
        }

        long seconds = (System.currentTimeMillis() - started) / 1000;

        /* ---------------------------- the report ---------------------------- */

        out.println("=================================================================");
        out.println("  LONG PLAYTEST - " + g.getMonth() + " months ("
                + (g.getMonth() / 12) + " years) in " + seconds + "s");
        out.println("=================================================================\n");

        for (String line : log) {
            out.println(line);
        }

        out.println();
        out.println("  months a skip refused to run (treasury empty, stepped instead): "
                + refusedSkips);
        out.printf("  most crowded the city ever got: %.0f%% of what its buildings"
                + " comfortably hold (month %d)%n", worstCrowding * 100, worstCrowdingMonth);
        out.printf("  people who moved in: %,.0f   people who left: %,.0f%n",
                totalArrivals, totalDepartures);
        out.printf("  unemployment: %.1f%% at the end, worst %.1f%%%n",
                lastUnemployment * 100, worstUnemployment * 100);
        out.printf("  months with a pay tier in decline: %d   months anybody left: %d%n",
                monthsAnyTierDeclining, monthsPeopleLeft);
        out.printf("  off sick: %.1f%% at the end, worst %.1f%%, %.1f%% averaged over the run%n",
                lastSickRate * 100, worstSickRate * 100,
                monthsObserved > 0 ? workLostToIllness / monthsObserved * 100 : 0);
        out.printf("  outbreaks: %d, ill for %d months of %d%n",
                outbreaks, monthsInOutbreak, monthsObserved);

        /*
         * The health service, which in this run is a service the advisor never
         * builds - the private sector correctly will not touch healthcare, and
         * nobody is playing. So these lines are the DO-NOTHING case, and that is
         * what makes them worth printing: they are the floor a player is
         * measured against.
         */
        Healthcare hc = g.getHealthcare();
        out.printf("  healthcare: $%,.0fk a month, %.0f%% covered by fees%n",
                hc.getGrossCost(), hc.getCostRecovery() * 100);
        out.printf("  funerals: %,.0f buried and %,.0f cremated last month,"
                + " %,.0f plots used, %,.0f lying unburied%n",
                hc.getBurials(), hc.getCremations(), hc.getPlotsUsed(), hc.getUnburied());

        /*
         * THE BANK, which nobody in this harness ever builds.
         *
         * Printed because it is the one building the private sector is meant to
         * put up entirely on its own initiative - the player here never orders
         * one - so this line is the only place a run says whether that actually
         * happens. A run ending with no branches and a full premium is the
         * advisor failing to notice, and it is invisible in every other figure.
         */
        /*
         * THE CITY'S ACCOUNT WITH THE WORLD.
         *
         * Printed because the exchange rate in phase two will be driven off it,
         * and a run that ends deep in deficit is a run that would have devalued
         * continuously. Better to know that from the accounting pass than to
         * discover it after the rate starts moving.
         */
        ForeignAccounts fx = g.getForeignAccounts();
        NationalAccounts na = g.getEconomyManager().getNationalAccounts();
        out.printf("  NX breakdown: exports %,.0f | food %,.0f  materials %,.0f  scrap %,.0f  => NX %,.0f%n",
                na.getExports(), na.getImportsFood(), na.getImportsMaterials(),
                na.getImportsRawMaterial(), na.getNetExports());
        /*
         * THE SAME MONTH, AS THE TWO CLASSES THAT MEASURE IT SEE IT.
         *
         * The line above is NationalAccounts - the GDP identity's view of net
         * exports. The line below is ForeignAccounts, which is what the
         * exchange rate is actually priced off. They are two measurements of
         * one quantity and they are printed together because a run has just
         * reported a trade SURPLUS of 264,898 next to a depreciation pressure
         * of +0.95, which is what a large DEFICIT looks like. At most one of
         * them is right.
         */
        WorldEconomy w = g.getWorldEconomy();
        out.printf("  monetary policy: rate %.2f%% (the rule advises %.2f%% on %.1f%% inflation);"
                + " the rate differential is %+.2f points and pulls the currency %+.2f%n",
                g.getDebtManager().getPolicyRate() * 100,
                g.getDebtManager().advisedPolicyRate(g.getPriceIndex().inflation()) * 100,
                g.getPriceIndex().inflation() * 100,
                fx.getRateDifferential() * 100, fx.ratePressure());
        // Both instruments, side by side on purpose: the headline is what the
        // band is doing and the realised figure is what the level did. They
        // disagreed for weeks on this line and nobody put them together.
        out.printf("  the world: prices %.3f since founding, headline %.1f%%/yr,"
                + " realised %.1f%%/yr; parity is %.3f and the rate is %.3f (%+.0f%% off it)%n",
                w.getPriceLevel(), w.getInflation() * 100, w.realisedInflation() * 100,
                fx.getParity(), fx.getRate(), fx.deviationFromParity() * 100);
        out.printf("  the same month, per ForeignAccounts: exports %,.0f  imports %,.0f"
                + "  interest %,.0f  => current account %,.0f%n",
                fx.getExports(), fx.tradeImports(), fx.getForeignInterest(),
                fx.currentAccount());
        out.printf("  ...and trailing: exports %,.0f  imports %,.0f  current %,.0f"
                + "  (pressure is -current/volume = %+.3f)%n",
                fx.monthlyExports(), fx.monthlyImports(), fx.monthlyCurrentAccount(),
                (fx.monthlyExports() + fx.monthlyImports()) > 0
                        ? -fx.monthlyCurrentAccount()
                                / (fx.monthlyExports() + fx.monthlyImports()) : 0);
        out.printf("  shelf price %.4f, food import price %.4f, local food %.4f%n",
                g.getEconomyManager().getCommercialHandler().getStoreSellPrice(),
                g.getEconomyManager().getFoodMarket().getImportPrice(),
                g.getEconomyManager().getFoodMarket().getLocalPrice());
        out.printf("  the currency: %.3f local per USD (pressure %+.2f, openness %.2f,"
                + " cover %s), wages lifted %.1f%%%n",
                fx.getRate(), fx.getLastPressure(), fx.getOpenness(),
                fx.importCover() == Double.MAX_VALUE ? "inf"
                        : String.format("%.1f mo", fx.importCover()),
                (g.getLabourMarket().getCostOfLiving() - 1) * 100);
        out.printf("  abroad: exports $%,.0fk/mo, imports $%,.0fk/mo,"
                + " foreign interest $%,.0fk/mo%n",
                fx.getExports(), fx.tradeImports(), fx.getForeignInterest());
        out.printf("  since founding: sold $%,.0fk abroad, bought $%,.0fk,"
                + " paid $%,.0fk of foreign interest, took $%,.0fk of capital%n",
                fx.getLifetimeExports(), fx.getLifetimeImports(),
                fx.getLifetimeInterest(), fx.getLifetimeFinancial());
        DebtManager fxMarket = g.getDebtManager();
        out.printf("  borrowed abroad: US$%,.0fk owed, $%,.0fk at home;"
                + " the world charges %.2f%% against %.2f%% at home%s%n",
                fxMarket.getForeignPrincipalUsd(), fxMarket.getForeignPrincipal(),
                fxMarket.foreignRate() * 100, fxMarket.getRate() * 100,
                fxMarket.foreignWindowOpen() ? "" : " (WINDOW SHUT: "
                        + fxMarket.foreignWindowReason() + ")");
        out.printf("  the currency did $%,.0fk to that debt over the run;"
                + " walked away from $%,.0fk; scar %.2f points%n",
                fx.getLifetimeRevaluation(), fx.getRepudiated(),
                fxMarket.getDefaultScar() * 100);
        /*
         * WHAT THE SICKNESS IS MADE OF, because the total on its own cannot be
         * acted on. Jerus, looking at 40.9%: "i think more healthcare takes care
         * of the first one or no?" - and the only honest answer is the split,
         * since only the baseline term responds to clinics at all.
         */
        Health hh = g.getHealth();
        PriceIndex px = g.getPriceIndex();
        out.printf("  prices: index %.3f since founding (%.0f%% food / %.0f%% rent),"
                + " inflation %+.1f%%/yr; shelf carries a %.2fx scarcity mark-up%n",
                px.getIndex(), px.getFoodWeight() * 100, px.getRentWeight() * 100,
                px.inflation() * 100,
                g.getEconomyManager().getCommercialHandler().getScarcityMultiple());
        CommercialHandler rentCh = g.getEconomyManager().getCommercialHandler();
        out.printf("  rent: family %s / studio %s per person of capacity"
                + " - break-even %s, required return %s%n",
                String.format("%.4f", rentCh.getRentPrice()),
                String.format("%.4f", rentCh.getStudioRentPrice()),
                String.format("%.4f", rentCh.rentBreakEven()),
                String.format("%.4f", rentCh.rentRequired()));
        out.printf("  the two markets: %,d family doors at %.2f households each,"
                + " %,d studio doors at %.2f (%,.0f households, %,d doors)%n",
                rentCh.getFamilyHomes(), rentCh.familyPressure(),
                rentCh.getStudioHomes(), rentCh.studioPressure(),
                rentCh.getHouseholdCount(), rentCh.getHomes());
        out.printf("  housing costs %s per person of capacity to supply"
                + " - %s of structure and %s of ground"
                + " (materials %s, land %s/sqft); repairs %s/mo%n",
                String.format("%.2f", rentCh.getStructurePerCapacity()
                        + rentCh.getLandPerCapacity()),
                String.format("%.2f", rentCh.getStructurePerCapacity()),
                String.format("%.2f", rentCh.getLandPerCapacity()),
                String.format("%.3f", g.getBuildingManager().getConstructionMaterialPrice()),
                String.format("%.6f", g.getLandManager().getPricePerSqFt()),
                String.format("%,.0f", rentCh.getReportPropertyMaintenance()));
        out.printf("  wages lifted %.1f%%, the floor is worth %s a month in today's money%n",
                (g.getLabourMarket().getCostOfLiving() - 1) * 100,
                String.format("%.3f", g.getLabourMarket().cashMinimumWage()));
        out.printf("  hunger: %.0f%% of people short, shops delivered %.0f%% of what was planned"
                + " (roads %.0f%%)%n",
                g.getHouseholdBalance().getHungerRate() * 100,
                g.getHouseholdBalance().getDeliveredShare() * 100,
                g.getInfrastructureManager().getThroughputRatio() * 100);
        out.printf("  sickness %.1f%% = baseline %.1f%% (coverage %.0f%%) + outbreak %.1f%%"
                + " + unburied %.1f%% + hunger %.1f%%%s%n",
                hh.getSickRate() * 100, hh.getBaselineRate() * 100, hh.getCoverage() * 100,
                hh.getOutbreakSeverity() * 100, hh.getUnburiedRate() * 100,
                hh.getHungerRate() * 100,
                hh.getSickRate() >= Health.MAX_SICK_RATE - 1e-9 ? "  (AT THE CAP)" : "");
        CapitalFlows hot = g.getCapitalFlows();
        out.printf("  hot money: $%,.0fk here (target $%,.0fk on a %.2f-point spread),"
                + " %.0f%% of the bank's funding%s%n",
                hot.getStock(), hot.getTarget(), hot.getSpread() * 100,
                g.getBank().hotFundingShare() * 100,
                hot.isStopped() ? "  (STOPPED: " + hot.getStopReason() + ")" : "");
        out.printf("  since founding: $%,.0fk came in, $%,.0fk left, across %d sudden stop(s);"
                + " peak $%,.0fk on a peak spread of %.2f points%n",
                hot.getLifetimeArrived(), hot.getLifetimeDeparted(), hot.getStopsSuffered(),
                hot.getPeakStock(), hot.getPeakSpread() * 100);
        out.printf("  the vault: $%,.0fk held abroad (%s months of imports at $%,.0fk/mo)%n",
                fx.getReserves(),
                fx.importCover() == Double.MAX_VALUE ? "inf"
                        : String.format("%.1f", fx.importCover()),
                fx.monthlyImports());
        OutwardInvestment abroad = g.getOutwardInvestment();
        out.printf("  abroad, the sectors' own: US$%,.0fk (%s at today's rate), %.0f%% of their wealth wanted"
                + " on a %.2f-point spread; sent $%,.0fk, brought home $%,.0fk, earned $%,.0fk since founding; peak US$%,.0fk%n",
                abroad.totalUsd(), String.format("$%,.0fk", abroad.totalLocalValue()),
                abroad.getTargetShare() * 100, abroad.getSpread() * 100,
                abroad.getLifetimeOut(), abroad.getLifetimeHome(), abroad.getLifetimeInterest(), abroad.getPeakUsd());
        HouseholdBalance savers = g.getHouseholdBalance();
        out.printf("  abroad, the households' own: US$%,.0fk ($%,.0fk at today's rate) against $%,.0fk saved at home;"
                + " this month sent $%,.0fk, brought home $%,.0fk, earned $%,.0fk there%n",
                savers.totalAbroadUsd(), savers.totalAbroadValue(), savers.totalSavings(),
                savers.getSentAbroad(), savers.getBroughtHome(), savers.getForeignInterest());
        Equity owners = g.getEquity();
        StringBuilder held = new StringBuilder();
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            if (owners.getShares(c) <= 0) continue;
            held.append(String.format("%s %.0f%% abroad, ", Equity.COMPANIES[c], owners.foreignShare(c) * 100));
        }
        out.printf("  the owners: raised $%,.0fk at home and $%,.0fk abroad in %d offerings;"
                + " paid $%,.0fk of dividends to the households and $%,.0fk abroad; %s%n",
                owners.getLifetimeRaisedHome(), owners.getLifetimeRaisedAbroad(), owners.getOfferings(),
                owners.getLifetimeDividendsHome(), owners.getLifetimeDividendsAbroad(),
                held.length() > 0 ? held.substring(0, held.length() - 2) : "nobody owns anything");
        out.printf("  the record: $%,.0fk cumulative balance since founding;"
                + " net position $%,.0fk (%s)%n",
                fx.getCumulativeBalance(), fx.netForeignPosition(),
                fx.inDeficit() ? "owes the world more than it holds there" : "in the clear");

        Bank bnk = g.getBank();
        out.printf("  the bank: equity $%,.0fk against a book of $%,.0fk"
                + " (%.1f%% - the ratio requires %.0f%%)%n",
                bnk.equity(), bnk.getBook(),
                Math.min(999, bnk.capitalRatio()) * 100, Bank.CAPITAL_RATIO * 100);
        out.printf("  written off over the run: $%,.0fk, of which $%,.0fk was families%n",
                lifetimeWriteOffs, lifetimeHouseholdWriteOffs);
        out.printf("  it failed %d time(s); the city put $%,.0fk of capital back in%n",
                bnk.getFailures(), lifetimeBailouts);
        out.printf("  banking: %,.0f branch(es), $%,.0fk deposited, $%,.0fk lent,"
                + " %.0f%% of capacity, %.1f points of premium%n",
                bnk.getBranches(), bnk.getDeposits(), bnk.getBook(),
                Math.min(999, bnk.strain()) * 100, bnk.ratePremium() * 100);

        /*
         * CREDIT, BY SECTOR. The bank's line above says what it holds; this
         * says who owes it and who cannot borrow, which is the half nobody
         * could see without writing a probe. A bank with equity and no book
         * is either a city that needs no credit or six sectors that are
         * barred from it, and those are different findings.
         */
        out.println("  credit by sector (cash / assets / owes / rate / write-downs / ban left / loss streak):");
        BusinessDebtManager credit = g.getEconomyManager().getBusinessDebtManager();
        java.util.Map<String, Integer> streaks = g.getBusinessInvestment().getLossMonthsState();
        for (String sector : BusinessDebtManager.SECTORS) {
            out.printf("    %-15s $%,14.0fk  $%,14.0fk  $%,12.0fk  %5.2f%%  %3d  %4d mo  %5d mo%n",
                    sector,
                    g.getEconomyManager().getSectorCash(sector),
                    credit.getAssets(sector),
                    credit.getPrincipal(sector),
                    credit.getRate(sector) * 100,
                    credit.getRestructureCount(sector),
                    credit.getBlockedMonths(sector),
                    streaks.getOrDefault(sector, 0));
        }

        out.println("\n--- what the advisor tried, and what happened ---\n");
        refusals.entrySet().stream()
                .sorted((x, y) -> y.getValue() - x.getValue())
                .limit(24)
                .forEach(en -> out.printf("  %-46s %6d%n", en.getKey(), en.getValue()));

        out.println("\n--- findings ---\n");
        if (findings.isEmpty()) {
            out.println("  Nothing. Every month passed the audit and every reload matched.");
        } else {
            for (Map.Entry<String, Finding> entry : findings.entrySet()) {
                Finding f = entry.getValue();
                out.printf("  %-46s %5d time(s), months %d-%d%n",
                        entry.getKey(), f.count, f.firstMonth, f.lastMonth);
                if (f.worst != null) {
                    out.printf("        worst, month %d: %s%n", f.worstMonth, f.worst);
                }
            }
        }

        cleanUp(root);
        System.exit(findings.isEmpty() ? 0 : 1);
    }

    /**
     * The business economy at a checkpoint, on one line: per sector its cash,
     * write-downs and months of ban left, then hunger and the shelf.
     *
     * The end-of-run table says where a run finished; this says how it got
     * there, which is the half that was missing when a rule that liquidated
     * sectors was first measured only at month 4,000.
     */
    static String creditEra(Game g) {
        BusinessDebtManager c = g.getEconomyManager().getBusinessDebtManager();
        StringBuilder b = new StringBuilder(String.format("       credit  "));
        String[] tags = { "Ret", "RE", "Ind", "Con", "HI", "Min" };
        for (int i = 0; i < BusinessDebtManager.SECTORS.length; i++) {
            String sec = BusinessDebtManager.SECTORS[i];
            b.append(String.format("%s %s/%d/%d  ", tags[i],
                    money(g.getEconomyManager().getSectorCash(sec)),
                    c.getRestructureCount(sec), c.getBlockedMonths(sec)));
        }
        b.append(String.format("| hunger %.0f%% shelf %.0f%%",
                g.getHouseholdBalance().getHungerRate() * 100,
                g.getHouseholdBalance().getDeliveredShare() * 100));
        return b.toString();
    }

    static String era(Game g, String label) {
        EconomyManager e = g.getEconomyManager();
        PopulationManager p = g.getPopulationManager();
        BuildingManager b = g.getBuildingManager();
        InfrastructureManager roads = g.getInfrastructureManager();

        return String.format(
                "m%-5d %-22s pop %-7d cash %-14s GDP/mo %-11s jobs %-6d "
                + "fill %3.0f%% roads %3.0f%% power %3.0f%% water %3.0f%% "
                + "cityDebt %-12s bizDebt %-11s land %3.0f%% mines %d/%d ore $%.2f",
                g.getMonth(), label,
                p.getPopulation(),
                money(g.getCash()),
                money(e.getMonthGdp()),
                p.getTotalJobs(),
                (p.getTotalJobs() > 0
                        ? 100.0 * Math.min(p.getWorkforce(), p.getTotalJobs()) / p.getTotalJobs()
                        : 100),
                roads.getThroughputRatio() * 100,
                g.getEnergyRatio() * 100,
                g.getWaterRatio() * 100,
                money(g.getDebtManager().getAllPrincipal()),
                money(e.getBusinessDebtManager().getTotalPrincipal()),
                g.getLandManager().getUtilisation() * 100,
                g.minesCommitted(), g.getLandManager().getIronDeposits(),
                e.getIronMarket().getLocalPrice());
    }

    static String money(double v) {
        double abs = Math.abs(v);
        if (abs >= 1e9) return String.format("$%.1fT", v / 1e9);
        if (abs >= 1e6) return String.format("$%.1fB", v / 1e6);
        if (abs >= 1e3) return String.format("$%.1fM", v / 1e3);
        return String.format("$%.0fk", v);
    }

    static void cleanUp(Path root) {
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(path -> {
                try { Files.deleteIfExists(path); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }
    }
}
