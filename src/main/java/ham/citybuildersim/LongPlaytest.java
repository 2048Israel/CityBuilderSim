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
    // The people outside the families, over the run (2026-09-11).
    static double totalEvicted = 0, totalLeftBroke = 0, worstUnhoused = 0, worstOrphans = 0;
    /**
     * The second-hand car market over the run (2026-09-17). Counted rather than
     * asserted, and counted at all because a mechanic that never fires in four
     * thousand months looks exactly like one that does not exist - which is how
     * out-migration got shipped inert and how the transit fare was collected
     * from nobody for a whole batch.
     */
    static double usedOffered = 0, usedSold = 0, usedCheapest = 1, usedDearest = 0;
    static int worstUnhousedMonth = 0;

    static int monthsAnyTierDeclining = 0;
    static int monthsPeopleLeft = 0;

    /* Sickness. Counted rather than assumed - a mechanic that never fires over
       four thousand months looks exactly like one that does not exist, which is
       how out-migration got shipped inert. */
    static int outbreaks = 0;
    static int monthsInOutbreak = 0;
    static boolean wasInOutbreak = false;
    static double worstSickRate = 0;
    /** The long sick (2026-09-11): who died of staying sick, by band, and the most ever ill past two months. */
    static final double[] illnessDeathsByBand = new double[AgeBand.values().length];
    /** Everyone who died, by band, and the orphans and the unhoused among them (2026-09-11). */
    static final double[] deathsByBandRun = new double[AgeBand.values().length];
    static double orphanDeathsRun = 0, unhousedDeathsRun = 0;
    static double allDeaths = 0, worstLongSick = 0;
    static int worstLongSickMonth = 0;
    /**
     * The price at the clinic door (2026-09-19): person-months lived, so the
     * deaths can be read per thousand a year; the people the fee turned
     * away, summed for the mean; and the fees the households skipped, summed
     * over the run - the summary prints the run's total beside last month's.
     */
    static double personMonths = 0, pricedOutSum = 0, careSkippedSum = 0;
    /** The price of a place (2026-09-21): the interest the graduates paid and the grants paid, summed over the run. */
    static double studentInterestSum = 0, grantsSum = 0;
    /** Crime (2026-09-11): the rate against Canada's summed for the mean, the worst, and what it did over the run. */
    static double crimeVsSum = 0, worstCrimeVs = 0, coverageSum = 0, killedRun = 0, stolenRun = 0;

    /* -----------------------------------------------------------------------
       A mechanic that is not counted over a real run is a mechanic nobody
       knows fires. Business Services is a boom-and-bust by design - viable
       near founding parity, closed once the currency appreciates - so the END
       STATE says "0 seats" and hides eight hundred months of a working
       sector. These are what make it visible.
       ----------------------------------------------------------------------- */
    static double peakSeats = 0;
    static int peakSeatsMonth = 0;
    static int monthsWithSeats = 0;
    static int lastSeatMonth = 0;
    static double serviceExportsRun = 0;
    static double caughtRun = 0, notHeldRun = 0, worstPrisoners = 0, crimeMonths = 0;
    /**
     * The currency over the run (2026-09-21): its dearest dollar and when, and
     * how many months it spent far from parity. The end state says where the
     * rate finished, and a currency that spent forty years at three times its
     * parity and came home reads exactly like one that never left.
     */
    static double peakRate = 0;
    static int peakRateMonth = 0, monthsFarFromParity = 0;

    /** How far from parity, as a multiple of it, a weak currency has to be to count as far from it. */
    static final double FAR_FROM_PARITY = 2.0;

    /*
     * THE CURRENCY'S GUARD AND THE DIAL'S PATH, over the run (0.7.2). The
     * months the rate sat at its guard (ForeignAccounts.MAX_RATE - a hundred
     * until 0.7.3, a billion since: a numerical guard against a rate run to
     * infinity, which no run is expected to reach - the count is kept as a
     * measurement and should read zero) and every month's policy rate and
     * inflation,
     * for the dial's min / median / max and the months it spent past the old
     * stop - the figures the uncapped dial is measured in. A path, not an
     * endpoint: an autopilot that went to 67% for a decade and came home
     * reads 3% at month 4,002.
     */
    static int monthsAtGuard = 0;
    /** Every month's policy rate, for the dial's min, median and max over the run. */
    static final java.util.List<Double> dialPath = new java.util.ArrayList<>();
    /** Every month's inflation reading, once the index has a year to read, for its median. */
    static final java.util.List<Double> inflationPath = new java.util.ArrayList<>();

    /**
     * Every month's spend factor (0.7.3): the share of their spending above
     * subsistence the households planned at, on the month's real deposit
     * rate - HouseholdBalance.getSpendFactor() after the month. How often the
     * demand channel fires, and how hard: its min, median and max over the
     * run, beside the dial's.
     */
    static final java.util.List<Double> spendPath = new java.util.ArrayList<>();
    /** ...its lowest and highest, and the first month each was struck in. */
    static double spendLow = Double.POSITIVE_INFINITY, spendHigh = Double.NEGATIVE_INFINITY;
    static int spendLowMonth = 0, spendHighMonth = 0;
    /** Months the bank's interest margin could not pay the deposit rate it chose, and paid what the margin had (Bank.isDepositPayoutHeld(), 0.7.7), and the first and last of them. It counted the months the quoted rate was capped at the lending rate until 0.7.7 (isDepositRateCapped(), 0.7.3), which a chosen rate under the window's cannot be. */
    static int depositCappedMonths = 0, depositCappedFirst = 0, depositCappedLast = 0;

    /*
     * THE BANK AS A BUSINESS (0.7.7): a trailing year of its statement, for
     * the checkpoint line that prints its price build-up beside its margin,
     * its cost ratio, its fee share and its return on equity - and the run's
     * deposit share of the policy rate, what the real banks' ~0.4 is read
     * against. The margin is over total assets, reserves and paper included,
     * as the published NIM is: interest earned on reserves is in the income,
     * so over the loan book alone a flush bank read 64%. And what the book
     * actually lost over the year, reported beside the base loss prime is
     * priced on, since the price stopped reading it.
     */
    static final double[] bankNii = new double[12], bankFees = new double[12], bankOther = new double[12],
            bankOpex = new double[12], bankNet = new double[12], bankEquityRing = new double[12],
            bankAssetsRing = new double[12], bankLossRing = new double[12], bankAtRiskRing = new double[12];
    static int bankMonths = 0;
    static double shareSum = 0, sharePolicy = 0, sharePolicySq = 0, shareDeposit = 0, sharePolicyDeposit = 0;
    static int shareMonths = 0;

    /*
     * THE BANK'S CAPITAL, YEAR BY YEAR (0.7.8): its capital ratio and its own
     * target averaged over each year, its return on the equity it opened the
     * year with, its provisions over the loans it made (the businesses' and
     * the families' books, averaged over the year), and whether the city grew.
     * A GROWTH YEAR is one the city ended bigger than it began with a bank
     * standing all year - the years a real bank's ~0.4% of loans in
     * provisions, ~10-16% return and 11-13.5% capital are read against. The
     * summary prints the medians over them and the extremes.
     */
    static final java.util.List<double[]> bankYears = new java.util.ArrayList<>();
    static double yrRatio, yrTarget, yrNet, yrProv, yrBook, yrDiv, yrBuyback, yrIssued, yrOpenEquity, yrOpenPop;
    static int yrMonths, yrFailuresOpen, yrFrozen, yrBranchMonths;
    static int rationedMonths, keepGoingMonths, payingMonths, returningMonths, rebuildingMonths;
    static double runDividends, runBuybacks, runIssued, runProvisions;
    /** Every month's loans (the businesses' and the families') summed, and every write-off: the run's provisions over its average book, through the cycle rather than over the growth years alone. */
    static double runLoanMonths, runWrittenOff;

    /*
     * HOW THE DEFAULTS ARRIVE (0.7.8, a sector defaults a slice at a time):
     * the backstop's whole-sector write-downs; the months any slice was
     * written off, and the months one was news (BusinessDebtManager
     * .defaultsAreNews()); the largest month's write-off against the equity
     * the bank opened it with; the sector-months past the watch line and past
     * the default point, the longest spell one sector spent past the watch
     * line, and what was lent to sectors while they were past it (the loop
     * the ban used to stop); how much of what was written off the allowance
     * already held; and the notices the defaults raised.
     */
    static int backstops, sliceMonths, newsMonths, pastWatchMonths, pastTriggerMonths, longestWatchSpell,
            longestWatchMonth, worstWriteOffMonth, defaultNotices;
    static String longestWatchSector = "-";
    static double sliceWrittenOff, worstWriteOffShare, worstWriteOff, lentPastWatch, runAllowanceUsed;
    static final java.util.Map<String, Integer> watchSpell = new java.util.HashMap<>();
    static Notice lastDefaultNotice;

    /*
     * ROUND 2 (0.7.8): the price off the curve and the plant sold as
     * materials. Loans written past the watch line, past 1.0 and past the
     * default point, their money and their rates; the plans declined on
     * their price, by sector; and the buildings retired, their material, what
     * the builders paid for it and what they could not, by the rule that
     * retired them, and the material the builders built with from it.
     */
    static int loansPastWatch, loansPastOne, loansPastT, projectsPastOne;
    static double lentPastOne, lentPastT, rateSumPastOne, rateMaxPastOne, rateSumPastT, rateMaxPastT;
    static final java.util.Map<String, Integer> refusedOnPrice = new java.util.TreeMap<>();

    /*
     * THE LANDLORDS' MORTGAGES (0.7.11): what they wrote, what the insurance
     * took and paid, and every month's reason the landlords built or did not,
     * counted by its words (houseReason()) - the batch's ensembles were read
     * off these and the house trace.
     */
    static int mortgagesWritten;
    static double mortgagesLent, premiumsRun, claimsRun;
    static final java.util.Map<String, Integer> houseReasons = new java.util.TreeMap<>();

    /** The landlords' month in a word, off the advisor's line: what built it, or what stopped it. */
    static String houseReason(String line) {
        if (line == null || line.isEmpty()) return "not asked";
        // A smaller home than the best, because the best would not fit (0.7.17).
        if (line.startsWith("Built") && line.contains("would not fit in"))
            return line.contains("on an insured mortgage") ? "built a smaller home, on a mortgage" : "built a smaller home, paid for";
        if (line.startsWith("Built") && line.contains("on an insured mortgage")) return "built on a mortgage";
        if (line.startsWith("Built")) return "built, paid for";
        if (line.contains("down payment")) return "held: the down payment";
        if (line.contains("the lender asks")) return "declined: the lender's test";
        if (line.contains("cover its interest")) return "declined: the interest test";
        if (line.contains("rent does not cover")) return "held: rent under the hurdle";
        if (line.contains("housing ahead of jobs")) return "held: housing ahead of jobs";
        if (line.contains("already building")) return "held: already building";
        if (line.contains("months of work on site")) return "held: months of work on site";
        if (line.contains("no land") || line.startsWith("Could not build")) return "held: no land";
        if (line.contains("borrowing ban")) return "held: borrowing ban";
        if (line.contains("short of capital against everything")) return "held: the bank's leverage";
        if (line.contains("bank")) return "held: the bank";
        if (line.startsWith("Declined")) return "declined: other";
        return "held: other";
    }

    static void countMortgages(Game g) {
        BusinessDebtManager cr = g.getEconomyManager().getBusinessDebtManager();
        mortgagesWritten += cr.getMortgagesWrittenThisMonth();
        for (Mortgage m : cr.getMortgages()) if (m.getMonthStarted() == g.getMonth()) mortgagesLent += m.getLoan();
        premiumsRun += cr.getPremiumsThisMonth();
        claimsRun += cr.getInsuredWrittenOffThisMonth();
        houseReasons.merge(houseReason(g.getLastInvestment(g.getSectors().realEstate().key())), 1, Integer::sum);
    }
    static int salvageBuildingsDistress, salvageBuildingsSpare;
    static double salvageUnits, salvageUnitsBought, salvagePaid, salvagePaidDistress, salvageUsed;

    /*
     * THE LEVERAGE RATIO AND THE BRANCHES (0.7.11, round 2): how often the
     * leverage requirement was the one that bound, the ratio at its lowest,
     * and the branches opened - by where the bank's capital stood the month
     * each one opened - and closed. Plus a row of <prefix>-bank.csv a month
     * when the trace is on.
     */
    static int leverageMonths, levBankMonths, branchesOpened, branchesClosedSeen, lastBranches = -1;
    static final java.util.Map<String, Integer> openedByStance = new java.util.TreeMap<>();
    static double lowestLeverage = Double.MAX_VALUE;
    static int lowestLeverageMonth;
    static int mortgagesRefusedForCapital;

    static void countLeverage(Game g) {
        Bank bk = g.getBank();
        int standing = (int) Math.round(bk.getBranches());
        if (standing > 0) {
            levBankMonths++;
            if (bk.leverageBinds()) leverageMonths++;
            double lev = bk.leverageRatio();
            if (!bk.isInsolvent() && bk.exposure() > 0 && lev < lowestLeverage) {
                lowestLeverage = lev;
                lowestLeverageMonth = g.getMonth();
            }
        }
        if (lastBranches >= 0 && standing > lastBranches) {
            branchesOpened += standing - lastBranches;
            openedByStance.merge(String.valueOf(bk.payoutStance()), standing - lastBranches, Integer::sum);
        }
        lastBranches = standing;
        branchesClosedSeen = g.getBranchesClosed();
        BusinessDebtManager cr = g.getEconomyManager().getBusinessDebtManager();
        String why = g.getLastInvestment(g.getSectors().realEstate().key());
        if (why != null && why.contains("short of capital against everything")) mortgagesRefusedForCapital++;
        if (traceBank != null) {
            double pol = g.getDebtManager().getPolicyRate();
            traceBank.printf(java.util.Locale.ROOT,
                    "%d,%.3f,%.3f,%.3f,%.6f,%.6f,%d,%.3f,%.3f,%s,%d,%d,%d,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%d,%.6f,%.6f,%.3f,%.3f,%d,"
                            + "%.1f,%.3f,%.3f,%.3f,%.6f,%.3f,%.3f,%.3f,%.3f,%.3f,%s%n",
                    g.getMonth(), bk.equity(), bk.exposure(), bk.getWeightedBook(),
                    Math.min(99, bk.leverageRatio()), Math.min(99, bk.capitalRatio()), bk.leverageBinds() ? 1 : 0,
                    bk.minimumEquity(), bk.targetEquity(), bk.payoutStance(), standing, bk.getUncoveredMonths(),
                    g.getBranchesClosed(), bk.insuredMortgageRate(pol),
                    bk.fundsTransferPrice(pol, Mortgage.MORTGAGE_TERM_MONTHS), bk.runningCostRate(),
                    bk.capitalCharge(pol, Mortgage.MORTGAGE_TERM_MONTHS, Bank.RISK_INSURED_MORTGAGE),
                    bk.prime(pol), pol, bk.getFailures(), bk.leverageTarget(), bk.capitalTarget(),
                    bk.getMortgageBook(), bk.getNetIncome(), cr.isInsuredRationed() ? 1 : 0,
                    // ...and (0.7.19) the branches by their customers: the fee-paying
                    // households, their fees, what the branches cost to run this month and
                    // the cover; the savings it can lend against, all of them, against the
                    // households' whole savings; its capacity and what it owes the window.
                    bk.getCustomers(), bk.getAccountFees(), bk.getPayroll() + bk.getUpkeep(),
                    bk.getOperatingCost(), Math.min(1e6, bk.feeCover()), bk.getDeposits(),
                    g.getHouseholdBalance().totalSavings(), bk.capacity(),
                    g.getCentralBank().getAdvancesToBank(),
                    // ...and (revised) what a branch past the charter cost last month, which the rule reads.
                    bk.laterBranchCost(),
                    String.valueOf(g.getLastInvestment("Bank")).replace(',', ';'));
        }
    }

    /*
     * THE BONDS (0.7.12): what defaulted in each class and what it lost -
     * the recoveries the sources are read against - the months a sector
     * owed bonds, and the peak share of the businesses' debt in bonds. Plus
     * a row of <prefix>-bonds.csv a month when the trace is on.
     */
    static double bondRunLoansDefaulted, bondRunLoansLost, bondRunBondsDefaulted, bondRunBondsLost;
    static double bondPeakShare;
    static int bondPeakShareMonth, bondMonthsWithBonds;

    static void countBonds(Game g) {
        BusinessDebtManager cr = g.getEconomyManager().getBusinessDebtManager();
        BondMarket bm = g.getBondMarket();
        double loans = 0, bonds = 0;
        for (String s : Sectors.KEYS) {
            bondRunLoansDefaulted += cr.getLoansDefaultedThisMonth(s);
            bondRunBondsDefaulted += cr.getBondsDefaultedThisMonth(s);
            bondRunLoansLost += cr.getWrittenOffThisMonth(s);
            bondRunBondsLost += cr.getBondWrittenOffThisMonth(s);
            loans += cr.getLoanPrincipal(s);
            bonds += cr.getBondPrincipal(s);
        }
        if (bonds > 0) bondMonthsWithBonds++;
        double share = loans + bonds > 0 ? bonds / (loans + bonds) : 0;
        if (share > bondPeakShare) { bondPeakShare = share; bondPeakShareMonth = g.getMonth(); }
        if (traceBonds == null) return;
        Bank bk = g.getBank();
        StringBuilder row = new StringBuilder();
        row.append(String.format(java.util.Locale.ROOT,
                "%d,%d,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%d,%.3f,%.6f,"
                        + "%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,"
                        + "%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%d,%d,%.3f,%d,"
                        + "%.6f,%.3f,%.3f,%.3f,%.6f,%.6f,%.6f",
                g.getMonth(), bm.getBonds().size(), bm.totalFace(), bm.faceHeldByHouseholds(), bm.faceHeldByBank(),
                bm.faceHeldByCompanies(), bm.faceHeldByWorld(), loans, bm.getIssues(), bm.getIssuedFace(),
                bm.averageCoupon(),
                bm.getCouponsToHouseholds(), bm.getCouponsToBank(), bm.getCouponsToCompanies(), bm.getCouponsAbroad(),
                bm.getPrincipalToHouseholds(), bm.getPrincipalToBank(), bm.getPrincipalToCompanies(), bm.getPrincipalAbroad(),
                bm.getLossHouseholds(), bm.getLossBank(), bm.getLossCompanies(), bm.getWorldWrittenOff(),
                bm.getWorldBought(), bm.getWorldSold(), bm.getHouseholdsBought(), bm.getHouseholdsSold(),
                bm.getHouseholdsBoughtAbroad(), bm.getHouseholdsSoldAbroad(),
                bm.getLastPostedBuy(), bm.getLastPostedSell(), bm.getLastFilled(), bm.getLastSellsPosted(),
                bm.getLastSellsWaited(), bm.getLastSellQuantityWaited(), bm.getLastTrades(),
                bk.getConcentrationHerfindahl(), bk.getConcentrationAddOn(), bk.getConcentrationWeighted(),
                bk.getBondBook(), cr.getPrimeRate(), g.getDebtManager().getPolicyRate(),
                g.getHouseholdBalance().getBondRatio()));
        int month = g.getMonth();
        for (String s : Sectors.KEYS) {
            double yield = Double.NaN;
            double face = 0, wy = 0;
            for (CorporateBond b : bm.getBonds(s)) {
                double y = bm.lastYield(b, month);
                if (Double.isFinite(y)) { face += b.face(); wy += b.face() * y; }
            }
            if (face > 0) yield = wy / face;
            row.append(String.format(java.util.Locale.ROOT, ",%.3f,%.3f,%.6f,%.6f,%.6f,%.6f,%.3f,%.3f,%.3f,%.3f",
                    cr.getLoanPrincipal(s), cr.getBondPrincipal(s), cr.getRate(s), bm.averageCoupon(s),
                    Double.isNaN(yield) ? 0 : yield, cr.getConcentrationCharge(s),
                    cr.getLoansDefaultedThisMonth(s), cr.getBondsDefaultedThisMonth(s),
                    cr.getWrittenOffThisMonth(s), cr.getBondWrittenOffThisMonth(s)));
        }
        traceBonds.println(row);
    }

    static void countPriceAndPlant(Game g) {
        for (BusinessDebtManager.Written w : g.getEconomyManager().getBusinessDebtManager().getWrittenThisMonth()) {
            if (w.leverage() > Bank.SECTOR_WATCH_LEVERAGE) loansPastWatch++;
            if (w.leverage() > 1.0) {
                loansPastOne++;
                if (w.project()) projectsPastOne++;
                lentPastOne += w.amount();
                rateSumPastOne += w.rate();
                rateMaxPastOne = Math.max(rateMaxPastOne, w.rate());
            }
            if (w.leverage() >= BusinessDebtManager.INSOLVENCY_TRIGGER) {
                loansPastT++;
                lentPastT += w.amount();
                rateSumPastT += w.rate();
                rateMaxPastT = Math.max(rateMaxPastT, w.rate());
            }
        }
        for (String s : g.getRefusedOnPrice()) refusedOnPrice.merge(s, 1, Integer::sum);
        for (Game.Salvage s : g.getSalvageThisMonth()) {
            if (s.distress()) { salvageBuildingsDistress += s.buildings(); salvagePaidDistress += s.paid(); }
            else salvageBuildingsSpare += s.buildings();
            salvageUnits += s.units();
            salvageUnitsBought += s.unitsBought();
            salvagePaid += s.paid();
        }
        salvageUsed += g.getSalvageUsedThisMonth();
    }

    /*
     * ROUND 4 (0.7.8): the desk's inventory at the close against the bank's
     * equity, month by month while a bank stands out of resolution - one in
     * it holds its old inventory against the few dollars it has earned back,
     * which read as 10^17 per cent on the first run of this. (What the
     * dealer's capital limits turned away went with the dealer, 0.7.12
     * round 2: a desk on the book is not obliged to buy, so it refuses
     * nobody - it bids what its capital carries.)
     */
    static int deskOverEquityMaxMonth;
    static final java.util.List<Double> deskOverEquity = new java.util.ArrayList<>();
    static double deskOverEquityMax;

    /*
     * 0.7.12 ROUND 2: the shares on the book, the dividends, and the default
     * point - month by month, for the report's round-2 lines. The last trade
     * over fair value for every listed company that has traded; the months a
     * company traded at all; the desk's inventory and its trading result; the
     * ordinary dividends (the ring Equity keeps; no special one since round 4); and
     * what the default point refused - the shortfall desk's loans, the
     * mortgages that fell due rather than renew, the projects.
     */
    static final java.util.List<Double> sharePriceOverFair = new java.util.ArrayList<>();
    static int shareListedMonths, shareTradedMonths, deskInventoryMonths, refusedAtLineMonths, projectsRefusedAtLine;
    static double deskPnlRun, deskInventorySum, ordinaryDividendsRun;
    static double refusedAtLineRun, notRenewedAtLineRun;
    static final java.util.Map<String, Double> refusedAtLineBySector = new java.util.LinkedHashMap<>();

    static double pct(double part, double whole) { return whole > 0 ? part / whole * 100 : 0; }

    static void countShares(Game g) {
        Exchange ex = g.getExchange();
        Equity eq = g.getEquity();
        Bank bk = g.getBank();
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            if (eq.getShares(c) <= 0 || !(ex.fair(c) > 0)) continue;
            shareListedMonths++;
            if (ex.getVolume(c) > 0) shareTradedMonths++;
            if (ex.hasTraded(c)) sharePriceOverFair.add(ex.price(c) / ex.fair(c));
        }
        deskPnlRun += bk.getTradingIncome();
        if (bk.getBranches() > 0) { deskInventorySum += bk.getSecurities(); deskInventoryMonths++; }
        // The desk's caps (round 3): its book and its largest holding at fair value over the bank's equity.
        if (bk.getBranches() > 0 && !bk.isInsolvent() && bk.equity() > 0) {
            deskBookAtFair.add(ex.deskBookShare(eq));
            double most = 0;
            for (int c = 0; c < Equity.COMPANIES.length; c++) if (c != Equity.BANK) most = Math.max(most, ex.deskPositionShare(eq, c));
            deskLargestPosition.add(most);
        }
        ordinaryDividendsRun += eq.getOrdinaryDividendsLastClose();
        BusinessDebtManager cr = g.getEconomyManager().getBusinessDebtManager();
        double refused = cr.getRefusedAtDefaultPoint();
        if (refused > 0) { refusedAtLineMonths++; refusedAtLineRun += refused; }
        for (String s : Sectors.KEYS) {
            double r = cr.getRefusedAtDefaultPoint(s);
            if (r > 0) refusedAtLineBySector.merge(s, r, Double::sum);
            notRenewedAtLineRun += cr.getNotRenewedAtDefaultPoint(s);
            if (cr.wasRefusedAtDefaultPoint(s)) projectsRefusedAtLine++;
        }
    }

    static final java.util.List<Double> deskBookAtFair = new java.util.ArrayList<>(), deskLargestPosition = new java.util.ArrayList<>();

    static void countCapitalLimits(Game g) {
        Bank bk = g.getBank();
        countShares(g);
        double eq = bk.equity();
        if (bk.getBranches() > 0 && !bk.isInsolvent() && eq > 0) {
            double r = bk.getSecurities() / eq;
            deskOverEquity.add(r);
            if (r > deskOverEquityMax) { deskOverEquityMax = r; deskOverEquityMaxMonth = g.getMonth(); }
        }
    }

    /** The largest of a list, 0 when it is empty. */
    static double maxOf(java.util.List<Double> v) {
        double m = 0;
        for (double x : v) m = Math.max(m, x);
        return m;
    }

    /** The p-th quantile of a list, 0 when it is empty. */
    static double quantile(java.util.List<Double> v, double p) {
        if (v.isEmpty()) return 0;
        java.util.List<Double> s = new java.util.ArrayList<>(v);
        java.util.Collections.sort(s);
        return s.get((int) Math.min(s.size() - 1, Math.floor(p * s.size())));
    }

    static void countDefaults(Game g) {
        Bank bk = g.getBank();
        BusinessDebtManager cr = g.getEconomyManager().getBusinessDebtManager();
        boolean sliced = false, news = false;
        for (String s : cr.sectors()) {
            if (cr.wasRestructuredThisMonth(s)) backstops++;
            // Each class at its own loss (0.7.12, round 2); a backstop's month
            // has no slice (it leaves nothing owing), and its classes are its own.
            double slice = cr.wasRestructuredThisMonth(s) ? 0
                    : cr.getLoansDefaultedThisMonth(s) * BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT
                    + cr.getBondsDefaultedThisMonth(s) * BusinessDebtManager.BOND_LOSS_GIVEN_DEFAULT;
            if (slice > 0) { sliced = true; sliceWrittenOff += slice; }
            if (cr.defaultsAreNews(s)) news = true;
            double owed = cr.getPrincipal(s), assets = cr.getAssets(s);
            boolean watched = owed > 0 && (assets <= 0 || owed > assets * Bank.SECTOR_WATCH_LEVERAGE);
            if (watched) {
                pastWatchMonths++;
                lentPastWatch += cr.getLentThisMonth(s);
                int spell = watchSpell.merge(s, 1, Integer::sum);
                if (spell > longestWatchSpell) {
                    longestWatchSpell = spell; longestWatchSector = s; longestWatchMonth = g.getMonth();
                }
            } else {
                watchSpell.put(s, 0);
            }
            if (owed > 0 && (assets <= 0 || owed >= assets * BusinessDebtManager.INSOLVENCY_TRIGGER)) pastTriggerMonths++;
        }
        if (sliced) sliceMonths++;
        if (news) newsMonths++;
        double open = bk.getOpeningEquity();
        if (bk.getWriteOffs() > worstWriteOff) worstWriteOff = bk.getWriteOffs();
        if (open > 0 && bk.getWriteOffs() / open > worstWriteOffShare) {
            worstWriteOffShare = bk.getWriteOffs() / open;
            worstWriteOffMonth = g.getMonth();
        }
        runAllowanceUsed += bk.getAllowanceUsed();
        Notice live = g.getInbox().live("defaults");
        if (live != null && live != lastDefaultNotice) defaultNotices++;
        lastDefaultNotice = live;
    }

    /* ---- 0.7.12 round 4: the companies' cash, the desk's excess, and can't pay means default ---- */
    static final java.util.List<Double> companyTillsRun = new java.util.ArrayList<>();
    static double companyDepositInterestRun, companiesSoldShortRun, buybackRestingRun;
    static int overdraftMonths, overdraftNoLenderMonths, overdraftProjectMonths;
    static double overdraftSum, overdraftNoLenderSum, overdraftMax, overdraftProjectSum;
    static int cannotPayMonths, cannotPayBankUnderLine;
    static double cannotPayForgivenRun, cannotPayDebtDefaultedRun, cannotPayForgivenBanned;
    /* ---- round 5: the lines that stay open, the growth doors the capital rule still shuts, and interim financing ---- */
    static int ruleOnMonths, projectRefusedForCapital, mortgageRefusedForCapital, lineRationedMonths;
    static double lineLentRationedRun, lineLentPastOldRuleRun;
    static int interimWritten, interimRepaid, interimDefaultedAgain, interimRefusedPast, interimRefusedShut, backstopForced, backstopInBanMonths;
    static double interimWrittenSum, interimRepaidSum, interimWrittenOffSum, backstopInBanSum;
    static final java.util.Map<InterimLoan, Double> interimSeen = new java.util.IdentityHashMap<>();
    static final java.util.Set<InterimLoan> interimLost = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
    static final java.util.Map<String, int[]> cannotPayBySector = new java.util.TreeMap<>();
    static final java.util.Map<String, Double> cannotPayForgivenBySector = new java.util.TreeMap<>();
    static final java.util.Map<BusinessDebtManager.ShortReason, int[]> cannotPayByReason = new java.util.EnumMap<>(BusinessDebtManager.ShortReason.class);
    static final java.util.Map<BusinessDebtManager.ShortReason, Double> cannotPayForgivenByReason = new java.util.EnumMap<>(BusinessDebtManager.ShortReason.class);
    static final java.util.Map<String, Integer> cannotPaySpell = new java.util.HashMap<>();
    static int longestCannotPaySpell, longestCannotPayStart, longestCannotPayBanned;
    static String longestCannotPaySector = "-";
    static final java.util.Map<String, Integer> cannotPaySpellBanned = new java.util.HashMap<>();

    /* ---- round 6: buy only what it can pay for ---- */
    static final java.util.Map<String, int[]> limitedBySector = new java.util.TreeMap<>();
    static final java.util.Map<String, double[]> forgoneBySector = new java.util.TreeMap<>();   // stock, inputs, fleet
    static final java.util.Map<String, double[]> shelfShortBySector = new java.util.TreeMap<>(); // all, after a cut
    static final java.util.Set<String> limitedLastMonth = new java.util.HashSet<>();
    static int defaultsAfterCut, defaultsOnStock, defaultsOnBondCost;
    static double defaultsAfterCutSum, defaultsOnStockSum, defaultsOnBondCostSum;
    static final java.util.Map<String, double[]> defaultsOnStockBySector = new java.util.TreeMap<>();
    static final java.util.Map<String, int[]> defaultsAfterCutBySector = new java.util.TreeMap<>();
    static final java.util.List<Double> luxuryLeverage = new java.util.ArrayList<>();
    static final java.util.Set<String> RETAILERS = java.util.Set.of("Retail", "Restaurants", "Luxury Retail");

    static void countRound6(Game g) {
        BusinessDebtManager cr = g.getEconomyManager().getBusinessDebtManager();
        java.util.Set<String> limitedNow = new java.util.HashSet<>();
        for (Sector s : g.getSectors().all()) {
            String k = s.key();
            // The shelf this month's sales met was stocked at last month's clearing.
            if (s.getShelfShortValue() > 0) {
                double[] sh = shelfShortBySector.computeIfAbsent(k, x -> new double[2]);
                sh[0] += s.getShelfShortValue();
                if (limitedLastMonth.contains(k)) sh[1] += s.getShelfShortValue();
            }
            // A cash-flow default, and how much of it the stock the strike
            // just billed could account for: the unpaid up to what the sector
            // paid for stock this month (its shelves, its larder, its fleet).
            double shortNow = cr.getCannotPayShort(k);
            if (shortNow > 0) {
                double stockPaid = 0;
                for (java.util.Map.Entry<Good, Sector.Split> e : s.statement().bought.entrySet()) {
                    if (s.hasPantry(e.getKey())) stockPaid += e.getValue().total();
                }
                double onStock = Math.min(shortNow, stockPaid);
                if (onStock > 0) {
                    defaultsOnStock++;
                    defaultsOnStockSum += onStock;
                    double[] d = defaultsOnStockBySector.computeIfAbsent(k, x -> new double[2]);
                    d[0]++;
                    d[1] += onStock;
                }
                // ...and the ones a shortfall bond's own costs left: the desk
                // raised the hole less what the bond cost to bring.
                BusinessDebtManager.Plan plan = cr.getShortfallPlan(k);
                if (plan != null && plan.hasBond() && shortNow <= plan.costs() * (1 + 1e-9)) {
                    defaultsOnBondCost++;
                    defaultsOnBondCostSum += shortNow;
                }
            }
            // ...and so was the bill the settle at this month's top found.
            if (limitedLastMonth.contains(k) && cr.getCannotPayShort(k) > 0) {
                defaultsAfterCut++;
                defaultsAfterCutSum += cr.getCannotPayShort(k);
                defaultsAfterCutBySector.computeIfAbsent(k, x -> new int[1])[0]++;
            }
            if (s.wasPurchaseLimited()) {
                limitedNow.add(k);
                limitedBySector.computeIfAbsent(k, x -> new int[1])[0]++;
                double[] f = forgoneBySector.computeIfAbsent(k, x -> new double[3]);
                for (Good gd : Good.values()) {
                    double v = s.getPurchasesForgone(gd);
                    if (!(v > 0)) continue;
                    if (gd == Good.VANS || gd == Good.ROLLING_STOCK) f[2] += v;
                    else if (RETAILERS.contains(k)) f[0] += v;
                    else f[1] += v;
                }
            }
        }
        limitedLastMonth.clear();
        limitedLastMonth.addAll(limitedNow);
        if (cr.getPrincipal("Luxury Retail") > 0) luxuryLeverage.add(cr.defaultPointLeverage("Luxury Retail"));
    }

    /* ---- round 7: a sector paying out more in wages than it takes in, on interim loans; a new sector's first year ---- */
    static final java.util.Map<String, Integer> wageSpell = new java.util.HashMap<>();
    static final java.util.Map<String, int[]> wageSpells = new java.util.TreeMap<>();   // spells of a year or more, longest, its start
    static final java.util.Map<String, Integer> firstPlant = new java.util.TreeMap<>();
    static final java.util.Map<String, Integer> bannedFirstYear = new java.util.TreeMap<>();
    /* ---- round 8: a cash-flow default on a new sector's first bill: {the month, 1 if it was lent in the interim} ---- */
    static final java.util.Map<String, int[]> firstBillDefault = new java.util.TreeMap<>();
    /* ...a ceiling default in the month after the sector bought plant with a loan; and a shell: a sector holding no
       plant, nothing built and nothing on site, that owes something - its defaults, what they left unpaid and what
       the interim lender lent it, its months, and its longest stretch */
    static final java.util.Map<String, Integer> lastProject = new java.util.HashMap<>();
    static int afterProjectCeiling;
    static final java.util.Map<String, Integer> shellDefaults = new java.util.TreeMap<>();
    static final java.util.Map<String, Integer> shellSpell = new java.util.HashMap<>();
    static double shellUnpaid, shellInterim;
    static int shellMonths, shellLongest, shellLongestFrom;
    static String shellLongestWho = "none";

    static void countRound7(Game g) {
        BusinessDebtManager cr = g.getEconomyManager().getBusinessDebtManager();
        int month = g.getMonth();
        for (Sector s : g.getSectors().all()) {
            String k = s.key();
            // Payroll over revenue, a month at a time, while it owes interim financing.
            Sector.Statement st = s.statement();
            if (st.payroll > st.revenue && cr.getInterimPrincipal(k) > 0) {
                int spell = wageSpell.merge(k, 1, Integer::sum);
                int[] w = wageSpells.computeIfAbsent(k, x -> new int[3]);
                if (spell == 12) w[0]++;
                if (spell > w[1]) { w[1] = spell; w[2] = month - spell + 1; }
            } else {
                wageSpell.put(k, 0);
            }
            // Its first month-end holding plant, and whether a ban came in the year after it.
            if (!firstPlant.containsKey(k) && cr.getAssets(k) - cr.getCash(k) > 0) firstPlant.put(k, month);
            Integer first = firstPlant.get(k);
            if (first != null && month - first < 12 && cr.isBorrowingBlocked(k)) bannedFirstYear.putIfAbsent(k, month);
            // ...and a default on its first bill (round 8): in its first month-end holding plant or the next.
            if (first != null && month - first <= 1 && cr.getCannotPayShort(k) > 0)
                firstBillDefault.putIfAbsent(k, new int[] { month, cr.getInterimCount(k) > 0 ? 1 : 0 });
            // Round 8: a ceiling default in the settle after a project loan, and a shell.
            double shortNow = cr.getCannotPayShort(k);
            if (shortNow > 0 && cr.getCannotPayReason(k) == BusinessDebtManager.ShortReason.CEILING
                    && lastProject.getOrDefault(k, -9) == month - 1) afterProjectCeiling++;
            boolean noPlant = !(g.getBuildingManager().getBuildingsValueBySector(k) > 0);
            if (noPlant && cr.getPrincipal(k) > 0) {
                int spell = shellSpell.merge(k, 1, Integer::sum);
                shellMonths++;
                if (spell > shellLongest) { shellLongest = spell; shellLongestWho = k; shellLongestFrom = month - spell + 1; }
            } else {
                shellSpell.put(k, 0);
            }
            if (noPlant && shortNow > 0) {
                shellDefaults.merge(k, 1, Integer::sum);
                shellUnpaid += shortNow;
                shellInterim += cr.getInterimLentThisMonth(k);
            }
        }
        for (BusinessDebtManager.Written w : cr.getWrittenThisMonth()) if (w.project()) lastProject.put(w.sector(), month);
    }

    static void countRound4(Game g) {
        EconomyManager em = g.getEconomyManager();
        BusinessDebtManager cr = em.getBusinessDebtManager();
        Exchange ex = g.getExchange();
        double tills = 0;
        for (String s : Sectors.KEYS) {
            double cash = em.getSectorCash(s);
            tills += Math.max(0, cash);
            companyDepositInterestRun += em.getDepositInterestPaid(s);
            // The till at the month's end: short with a lender behind it, or with none.
            // The investment desk stands behind a till its project loan left
            // short by the loan's fee and a bond's costs this month (round 4).
            if (cash < 0) {
                overdraftMonths++;
                overdraftSum += -cash;
                overdraftMax = Math.max(overdraftMax, -cash);
                boolean project = false;
                for (BusinessDebtManager.Written w : cr.getWrittenThisMonth()) if (w.project() && w.sector().equals(s)) project = true;
                if (project) { overdraftProjectMonths++; overdraftProjectSum += -cash; }
                boolean lender = project || (!cr.isBorrowingBlocked(s) && !cr.pastDefaultPoint(s, cash) && cr.borrowingRoom(s) >= -cash);
                if (!lender) { overdraftNoLenderMonths++; overdraftNoLenderSum += -cash; }
            }
            // The month's cash-flow defaults: what they found unpaid, lent as
            // interim financing since round 5 or the backstop's.
            double forgiven = cr.getCannotPayShort(s);
            if (cr.getInterimRefusal(s) != null) {
                if (BusinessDebtManager.INTERIM_BANK_SHUT.equals(cr.getInterimRefusal(s))) interimRefusedShut++;
                else interimRefusedPast++;
                if (cr.wasRestructuredThisMonth(s)) backstopForced++;
            }
            if (cr.getBackstopInBanThisMonth(s) > 0) { backstopInBanMonths++; backstopInBanSum += cr.getBackstopInBanThisMonth(s); }
            interimWrittenOffSum += cr.getInterimWrittenOffThisMonth(s);
            // Round 5: the growth doors the capital rule shut, and the line it no longer does.
            if (cr.wasProjectRefusedForCapital(s)) projectRefusedForCapital++;
            if (cr.wasMortgageRefusedForCapital(s)) mortgageRefusedForCapital++;
            if (cr.getLineLentRationed(s) > 0) {
                lineRationedMonths++;
                lineLentRationedRun += cr.getLineLentRationed(s);
                lineLentPastOldRuleRun += cr.getLineLentPastOldRule(s);
            }
            if (forgiven > 0) {
                cannotPayMonths++;
                cannotPayForgivenRun += forgiven;
                cannotPayBySector.computeIfAbsent(s, k -> new int[1])[0]++;
                cannotPayForgivenBySector.merge(s, forgiven, Double::sum);
                BusinessDebtManager.ShortReason why = cr.getCannotPayReason(s);
                // -Dplaytest.defaults: a line for every one the bank's own state
                // caused (since round 5 only a bank shut), and every one past
                // $5M - the cascade's trace (round 4).
                boolean bankState = why == BusinessDebtManager.ShortReason.BANK_SHUT;
                if (Boolean.getBoolean("playtest.defaults") && (bankState || forgiven > 5_000)) {
                    Bank bk = g.getBank();
                    out.printf("CFD m%d %s %s short %,.0f share %.3f of $%,.0f due | interim %,.0f refused %s backstop %b | owes %,.0f quarter %.2f | bank eq %,.0f min %,.0f target %,.0f growth %.4f keepGoing %b failures %d%n",
                            g.getMonth(), s, why, forgiven, cr.getCannotPayShare(s), cr.getMonthObligations(s),
                            cr.getInterimLentThisMonth(s), cr.getInterimRefusal(s), cr.wasRestructuredThisMonth(s),
                            cr.getPrincipal(s), cr.defaultPointLeverage(s), bk.equity(), bk.minimumEquity(), bk.targetEquity(),
                            cr.getCapitalGrowth(), cr.isKeepGoingOnly(), bk.getFailures());
                }
                if (why != null) {
                    cannotPayByReason.computeIfAbsent(why, k -> new int[1])[0]++;
                    cannotPayForgivenByReason.merge(why, forgiven, Double::sum);
                    if (why == BusinessDebtManager.ShortReason.BANK_SHUT) cannotPayBankUnderLine++;
                }
                cannotPayDebtDefaultedRun += cr.getLoansDefaultedThisMonth(s) + cr.getBondsDefaultedThisMonth(s);
                boolean banned = cr.isBorrowingBlocked(s);
                if (banned) cannotPayForgivenBanned += forgiven;
                int spell = cannotPaySpell.merge(s, 1, Integer::sum);
                int bannedSpell = cannotPaySpellBanned.merge(s, banned ? 1 : 0, Integer::sum);
                if (spell > longestCannotPaySpell) {
                    longestCannotPaySpell = spell; longestCannotPaySector = s;
                    longestCannotPayStart = g.getMonth() - spell + 1; longestCannotPayBanned = bannedSpell;
                }
            } else {
                cannotPaySpell.put(s, 0);
                cannotPaySpellBanned.put(s, 0);
            }
        }
        companyTillsRun.add(tills);
        if (cr.capitalRuleOn()) ruleOnMonths++;
        // Every interim loan by itself (round 5): written, repaid at its term,
        // or written down again in a later default.
        java.util.Set<InterimLoan> now = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
        for (BusinessDebt d : cr.getLoans()) {
            if (!(d instanceof InterimLoan il)) continue;
            now.add(il);
            Double last = interimSeen.get(il);
            if (last == null) { interimWritten++; interimWrittenSum += il.getFaceValue(); }
            else if (il.getOutstandingPrincipal() < last - 1e-9 && interimLost.add(il)) interimDefaultedAgain++;
            interimSeen.put(il, il.getOutstandingPrincipal());
        }
        for (java.util.Iterator<java.util.Map.Entry<InterimLoan, Double>> it = interimSeen.entrySet().iterator(); it.hasNext(); ) {
            java.util.Map.Entry<InterimLoan, Double> e = it.next();
            if (now.contains(e.getKey())) continue;
            // Fell due with something left to pay: a loan a backstop wrote
            // to nothing runs out its term at nothing, and is not "repaid".
            if (e.getKey().isMatured() && e.getValue() > 1e-9) { interimRepaid++; interimRepaidSum += e.getValue(); }
            it.remove();
        }
        companiesSoldShortRun += g.getBondMarket().getCompaniesSoldShort();
        for (int c = 0; c < Equity.COMPANIES.length; c++) if (c != Equity.BANK) buybackRestingRun += ex.getBuybackBudget(c);
    }

    /** Every overdraft forgiven over the run: the backstop's, the one path left that forgives (round 4's cash-flow test forgave too; since round 5 it lends the rest as interim financing). */
    static double forgivenTotal(EconomyManager em) {
        double t = 0;
        for (String s : Sectors.KEYS) t += em.getOverdraftForgivenTotal(s);
        return t;
    }

    /** The share of a list's entries over a line, percent. */
    static double shareOver(java.util.List<Double> v, double line) {
        if (v.isEmpty()) return 0;
        int n = 0;
        for (double x : v) if (x > line) n++;
        return 100.0 * n / v.size();
    }

    static void bankYear(Game g) {
        Bank bk = g.getBank();
        long pop = g.getPopulationManager().getPopulation();
        // -Dplaytest.capital=true (0.7.8): a line for every month the bank
        // provided or wrote anything off, and every five years - the trace the
        // batch's capital target was measured with (Bank.MAX_BUFFER).
        if (Boolean.getBoolean("playtest.capital") && (Math.abs(bk.provisions()) > 1 || bk.getWriteOffs() > 1 || g.getMonth() % 60 == 0)) {
            out.printf("CAP m%d br %.0f eq %,.0f book %,.0f sect %,.0f hh %,.0f rwa %,.0f wo %,.0f prov %,.0f allow %,.0f used %,.0f worst %.4f trail %.4f tgt %.4f ratio %.4f prime %.4f stance %s watched %s%n",
                    g.getMonth(), bk.getBranches(), bk.equity(), bk.getBook(), bk.getSectorBook(), bk.getHouseholdBook(), bk.getWeightedBook(),
                    bk.getWriteOffs(), bk.provisions(), bk.getAllowance(), bk.getAllowanceUsed(), bk.getWorstLossRate(), bk.trailingLossRate(),
                    bk.capitalTarget(), Math.min(99, bk.capitalRatio()), bk.prime(g.getDebtManager().getPolicyRate()), bk.payoutStance(), bk.getSectorsWatched());
        }
        // ...and a line for every month it went under, with the month's flows:
        // what took it there (the held-10 stress runs were read with this).
        // (The hole since 0.7.14 is the bank's own record of it: nothing absorbs it any more.)
        if (Boolean.getBoolean("playtest.capital") && bk.getShortfallThisMonth() > 0) {
            out.printf("FAIL m%d br %.0f open %,.0f hole %,.0f | interest %,.0f trading %,.0f pbt %,.0f net %,.0f | prov %,.0f wo %,.0f used %,.0f allow %,.0f | div %,.0f bought %,.0f issued %,.0f | payroll %,.0f upkeep %,.0f | book %,.0f rwa %,.0f failures %d | securities %,.0f mark %,.0f%n",
                    g.getMonth(), bk.getBranches(), bk.getOpeningEquity(), bk.getShortfallThisMonth(),
                    bk.getInterestEarned(), bk.getTradingIncome(), bk.getProfitLastMonth(), bk.getNetIncome(),
                    bk.provisions(), bk.getWriteOffs(), bk.getAllowanceUsed(), bk.getAllowance(),
                    bk.getDividendsPaid(), bk.getSharesBoughtBack(), bk.getSharesIssued(),
                    bk.getPayroll(), bk.getUpkeep(), bk.getBook(), bk.getWeightedBook(), bk.getFailures(),
                    bk.getSecurities(), bk.getMarkChange());
        }
        countDefaults(g);
        countRound4(g);
        countRound6(g);
        countRound7(g);
        countPriceAndPlant(g);
        countCapitalLimits(g);
        countMortgages(g);
        countLeverage(g);
        countBonds(g);
        countFuel(g);
        // -Dplaytest.defaults=true (0.7.8): every month the bank failed, a
        // sector went under whole, or the provision took a quarter of the
        // equity it opened with - and the sectors behind it: what each owes
        // against what it owns, its default rate, what the month wrote off
        // (the slice and the backstop apart) and what its book holds.
        if (Boolean.getBoolean("playtest.defaults")) {
            BusinessDebtManager cr = g.getEconomyManager().getBusinessDebtManager();
            boolean whole = false;
            for (String s : cr.sectors()) whole |= cr.wasRestructuredThisMonth(s);
            double open = bk.getOpeningEquity();
            if (bk.getShortfallThisMonth() > 0 || whole || (open > 0 && bk.provisions() > .25 * open)) {
                StringBuilder sb = new StringBuilder();
                for (String s : cr.sectors()) {
                    double owed = cr.getPrincipal(s), wo = cr.getWrittenOffThisMonth(s);
                    if (owed <= 0 && wo <= 0 && bk.getSectorAllowance(s) <= 0) continue;
                    if (bk.getStage(s) < 2 && wo < .01 * Math.max(1, open) && !cr.wasRestructuredThisMonth(s)) continue;
                    sb.append(String.format(" | %s owes %,.0f qL %.2f assets %,.0f L %.2f pd %.3f %s %,.0f allow %,.0f",
                            s.length() > 6 ? s.substring(0, 6) : s, owed, cr.getQuarterLeverage(s), cr.getAssets(s),
                            cr.getAssets(s) > 0 ? owed / cr.getAssets(s) : Double.POSITIVE_INFINITY,
                            cr.getDefaultRate(s), cr.wasRestructuredThisMonth(s) ? "WHOLE" : "slice", wo,
                            bk.getSectorAllowance(s)));
                }
                out.printf("DEF m%d %s eq open %,.0f -> %,.0f | wo %,.0f prov %,.0f allow %,.0f (open %,.0f) | book %,.0f rwa %,.0f%s%n",
                        g.getMonth(), bk.getShortfallThisMonth() > 0 ? "FAILED" : whole ? "whole" : "provision",
                        open, bk.equity(), bk.getWriteOffs(), bk.provisions(), bk.getAllowance(), bk.getOpeningAllowance(),
                        bk.getBook(), bk.getWeightedBook(), sb);
            }
        }
        if (yrMonths == 0) {
            yrOpenEquity = bk.getOpeningEquity();
            yrOpenPop = pop;
            yrFailuresOpen = bk.getFailures();
        }
        double w = bk.getWeightedBook();
        yrRatio += w > 0 ? Math.min(10, bk.capitalRatio()) : 10;
        yrTarget += bk.capitalTarget();
        yrNet += bk.getNetIncome();
        yrProv += bk.provisions();
        yrBook += bk.getSectorBook() + bk.getHouseholdBook();
        yrDiv += bk.getDividendsPaid();
        yrBuyback += bk.getSharesBoughtBack();
        yrIssued += bk.getSharesIssued();
        if (bk.isInsolvent()) yrFrozen++;
        if (bk.getBranches() > 0) yrBranchMonths++;
        yrMonths++;
        runDividends += bk.getDividendsPaid();
        runBuybacks += bk.getSharesBoughtBack();
        runIssued += bk.getSharesIssued();
        runProvisions += bk.provisions();
        runWrittenOff += bk.getWriteOffs();
        runLoanMonths += bk.getSectorBook() + bk.getHouseholdBook();
        if (bk.getBranches() > 0 && !bk.isInsolvent()) {
            if (bk.lendsOnlyToKeepBorrowersGoing()) keepGoingMonths++;
            else if (!Double.isInfinite(bk.lendingGrowthLimit())) rationedMonths++;
            switch (bk.payoutStance()) {
                case REBUILDING -> rebuildingMonths++;
                case PAYING -> payingMonths++;
                case RETURNING -> returningMonths++;
                default -> { }
            }
        }
        if (yrMonths < 12) return;
        boolean grew = pop > yrOpenPop && bk.getFailures() == yrFailuresOpen && yrFrozen == 0
                && yrBranchMonths == 12;
        double avgBook = yrBook / 12;
        bankYears.add(new double[]{
                g.getMonth(), grew ? 1 : 0,
                yrRatio / 12, yrTarget / 12,
                yrOpenEquity > 0 ? yrNet / yrOpenEquity : Double.NaN,
                avgBook > 0 ? yrProv / avgBook : Double.NaN,
                yrNet > 0 ? yrDiv / yrNet : Double.NaN,
                yrDiv, yrBuyback, yrIssued,
                yrOpenEquity > 0 ? yrBuyback / yrOpenEquity : Double.NaN });
        yrMonths = 0;
        yrRatio = yrTarget = yrNet = yrProv = yrBook = yrDiv = yrBuyback = yrIssued = 0;
        yrFrozen = 0;
        yrBranchMonths = 0;
    }

    /** The median, least and most of one column over the growth years, as "m% (lo-hi%)". */
    static String yearSpread(int col, double scale) {
        java.util.List<Double> v = new java.util.ArrayList<>();
        for (double[] y : bankYears) if (y[1] > 0 && Double.isFinite(y[col])) v.add(y[col]);
        if (v.isEmpty()) return "n/a";
        java.util.Collections.sort(v);
        int n = v.size();
        double med = n % 2 == 1 ? v.get(n / 2) : (v.get(n / 2 - 1) + v.get(n / 2)) / 2;
        return String.format("%.2f%% (%.2f to %.2f%%)", med * scale, v.get(0) * scale, v.get(n - 1) * scale);
    }

    /** The dial's stop before 0.7.2, for counting the months the uncapped dial spends past it. */
    static final double OLD_DIAL_STOP = .25;

    /** The median of a path, or zero for an empty one. */
    static double median(java.util.List<Double> path) {
        if (path.isEmpty()) return 0;
        double[] sorted = path.stream().mapToDouble(Double::doubleValue).sorted().toArray();
        int n = sorted.length;
        return n % 2 == 1 ? sorted[n / 2] : (sorted[n / 2 - 1] + sorted[n / 2]) / 2;
    }
    static int worstCrimeMonth = 0;
    static double lastSickRate = 0;
    static double workLostToIllness = 0;
    static int monthsObserved = 0;
    static double totalDepartures = 0;
    static double totalArrivals = 0;

    /** M0 at the last audit, so the next can say what it moved by. NaN until the first. */
    static double lastM0 = Double.NaN;

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
        finite(month, "food price", g.getSectors().retail().getFoodPrice());
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
         * ...AND THE MONEY THE CENTRAL BANK MADE IS THE MONEY THE AUDIT SAW
         * (0.7.0). Three figures that must be one, every month: the audit's
         * MONEY lines in less out, the central bank's own issued less retired,
         * and what M0 moved by. And a central bank keeps none of its profit:
         * its equity, the vault aside, is what it owes the treasury less the
         * loss it is still carrying. The defence (0.7.2) leaves both alone: it
         * takes the vault and equity down together and M0 not at all
         * (CentralBank, THE DEFENCE).
         */
        CentralBank cb = g.getCentralBank();
        double made = cb.getIssued() - cb.getRetired();
        if (Math.abs(money.moneyMade() - made) > .01) {
            flag(month, "the audit and the central bank disagree about the money made",
                    String.format("audit $%,.2f, central bank $%,.2f", money.moneyMade(), made));
        }
        if (!Double.isNaN(lastM0) && Math.abs((cb.m0() - lastM0) - made) > .01
                && Math.abs((cb.m0() - lastM0) - made) > 1e-9 * Math.abs(cb.m0())) {
            flag(month, "M0 moved by something other than the money made",
                    String.format("M0 moved $%,.2f, made $%,.2f", cb.m0() - lastM0, made));
        }
        lastM0 = cb.m0();
        double kept = cb.equity() - cb.getVault() - (cb.getRemittanceDue() - cb.getLossCarried());
        if (Math.abs(kept) > .01 && Math.abs(kept) > 1e-9 * Math.max(1, cb.m0())) {
            flag(month, "the central bank kept a profit", String.format("$%,.2f", kept));
        }

        /*
         * ...AND EVERY HOLDER'S TWO BOOKS AGREE (0.7.1): what the cells hold of
         * the city's paper is what the paper says the households hold, and
         * what the central bank carries is what the paper says it holds. Two
         * records of one holding that drift apart are a coupon paid to nobody.
         */
        double cellsHold = g.getHouseholdBalance().totalPaper();
        double paperSays = g.getDebtManager().householdPrincipal();
        if (Math.abs(cellsHold - paperSays) > 1e-6 * Math.max(1, paperSays)) {
            flag(month, "the households' paper and the paper disagree",
                    String.format("cells $%,.4f, paper $%,.4f", cellsHold, paperSays));
        }
        double cbSays = g.getDebtManager().centralBankPrincipal();
        if (Math.abs(cb.getPaperHeld() - cbSays) > 1e-6 * Math.max(1, cbSays)) {
            flag(month, "the central bank's paper and the paper disagree",
                    String.format("balance sheet $%,.4f, paper $%,.4f", cb.getPaperHeld(), cbSays));
        }

        /* -------------------------------------------------------------------
           AND NOTHING MOVED AFTER THE AUDIT STRUCK.

           Added 2026-09-12. The residual above reconciles WITHIN a month, so
           money that moves after the strike is invisible to it twice over -
           missing from the flows and already in the pools next month. The two
           errors cancel and the residual stays at $0.00, which is how hot money
           sat outside the balance of payments for the whole life of the
           mechanic without 4,002 audited months noticing.

           Game computes it, because only Game knows where its own month ends.
           The first version of this check compared one month's close with the
           next month's open and flagged the PLAYER - an advisor placing a build
           order between turns moves the treasury's cash into the builders'
           order book, fifty-four times in one run. Between months is where the
           game is played; after the strike is where nothing should happen.
           ------------------------------------------------------------------- */
        double drift = g.getPostAuditDrift();
        if (Math.abs(drift) > .01) {
            flag(month, "money moved after the audit struck", String.format(
                    "$%,.2f moved once the month was already reconciled, most of it in %s ($%,.2f)",
                    drift, g.getPostAuditDriftPool(), g.getPostAuditDriftWorst()));
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
                    + "  Ifood %.0f  Imatl %.0f  Iheld %.0f  | imports food %.0f matl %.0f raw %.0f exports %.0f",
                    e.getMonthGdp(), na.getConsumption(),
                    na.getInvestmentConstruction(), na.getInvestmentInventories(),
                    na.getGovernment(), na.getNetExports(),
                    na.getInventoryFood(), na.getInventoryMaterials(), na.getInventoryHeld(),
                    na.getImportsFood(), na.getImportsMaterials(), na.getImportsRawMaterial(), na.getExports()));
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

        /*
         * NO CELL CARRIES A NEGATIVE POSITION (2026-09-16).
         *
         * A household cannot own minus three dollars, owe minus a dollar, or
         * hold minus a share - and for months on end four retired cells owed
         * MINUS 2.7e-29, which is not money and was never meant to be there.
         * It came out of the monthly rebuild, where a cell's share of the pool
         * was `weight * moved / gain` and could land one ulp above `weight`,
         * leaving the remainder a hair negative. Nothing read it as money.
         * Something read its SIGN: investAbroad() asks `debt <= 0` to decide
         * who may keep money abroad, and a currency reform flipped four cells
         * to the other side of zero and took the city's exchange rate with it
         * (see HouseholdBalance.moveStock, and DenominationCheck, which caught
         * it 158 months after a reform).
         *
         * ZERO TOLERANCE, DELIBERATELY. The clamps that fix it are exact, so
         * any negative at all is the arithmetic leaking again - and a band
         * here would be a figure in dollars, which is the other mistake.
         */
        for (Household cell : g.getHouseholdBalance().cells()) {
            double worstShare = 0;
            for (int co = 0; co < Equity.COMPANIES.length; co++) worstShare = Math.min(worstShare, cell.shares(co));
            double worst = Math.min(Math.min(Math.min(cell.savings(), cell.debt()),
                    Math.min(cell.abroad(), cell.studentDebt())), worstShare);
            if (worst < 0) {
                flag(month, "a household cell carries a negative position",
                        String.format("%s: savings %.3g, debt %.3g, abroad %.3g, student %.3g, worst share %.3g",
                                cell.label(), cell.savings(), cell.debt(), cell.abroad(),
                                cell.studentDebt(), worstShare),
                        -worst);
            }
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
        {
            Unemployment pool = g.getUnemployment();
            totalEvicted += g.getHouseholdBalance().getEvicted();
            if (g.getUsedCarsOffered() > 0) {
                usedOffered += g.getUsedCarsOffered();
                usedSold += g.getUsedCarsTraded();
                if (g.getUsedCarsTraded() > 0) {
                    // The market's own denominator, not today's showroom - see
                    // HouseholdBalance.getUsedCarShare().
                    double share = g.getHouseholdBalance().getUsedCarShare();
                    usedCheapest = Math.min(usedCheapest, share);
                    usedDearest = Math.max(usedDearest, share);
                }
            }
            totalLeftBroke += pool.getLeftWhenBroke();
            double noDoor = pool.getUnhoused();
            for (double v : g.getFamilies().unhousedPeopleByBand()) noDoor += v;
            if (noDoor > worstUnhoused) { worstUnhoused = noDoor; worstUnhousedMonth = g.getMonth(); }
            worstOrphans = Math.max(worstOrphans, g.getFamilies().getOrphansTotal());
        }

        monthsObserved++;
        /*
         * -Dplaytest.fx=true: the currency and what is pushing it, yearly. The
         * instrument that found the surplus with nowhere to go; kept because
         * the next currency question will want it again.
         */
        /*
         * -Dplaytest.bank=true: the bank's month and the seventh sector's,
         * yearly. The instrument that found the materials plant taking the
         * bank down thirteen times a run (2026-09-11).
         */
        int traceFrom = Integer.getInteger("playtest.bankfrom", -1), traceTo = Integer.getInteger("playtest.bankto", -1);
        boolean inTrace = traceFrom >= 0 && g.getMonth() >= traceFrom && g.getMonth() <= traceTo;
        if (inTrace) {
            Sector mat = g.getSectors().byKey(System.getProperty("playtest.sector", Sectors.MATERIALS));
            Sector.Statement st = mat.statement();
            SectorBooks.SectorMonth bm = g.getSectorBooks().get(mat.key());
            BusinessDebtManager cr = g.getEconomyManager().getBusinessDebtManager();
            out.printf("MATm%-4d cash %,.0f debt %,.0f assets %,.0f | rev %,.0f inp %,.0f pay %,.0f int %,.0f ptax %,.0f mnt %,.0f vat %,.0f pre %,.0f | flows: open %,.0f net %,.0f borrowed %,.0f repaid %,.0f city %,.0f forgiven %,.0f abroad %,.0f eq %,.0f div %,.0f bb %,.0f spent %,.0f unexpl %,.0f | plants %d/%d stock %,.0f sold %,.0f exp %,.0f | restr %d ban %d insolvent %s | mkt want %,.0f loc %,.0f imp %,.0f trend %,.0f yard %d price %.2f | bank eq %,.0f wo %,.0f resl %,.0f fails %d trading %,.0f securities %,.0f net %,.0f | %s%n",
                g.getMonth(), mat.getCash(), cr.getPrincipal(mat.key()), cr.getAssets(mat.key()), st.revenue, st.inputs, st.payroll, st.interest, st.propertyTax, st.maintenance, st.salesTax, st.preTaxIncome,
                bm.openingCash(), bm.netIncome(), bm.borrowed(), bm.repaid(), bm.fromTheCity(), bm.forgiven(), bm.investedAbroad(), bm.equityRaised(), bm.dividendsPaid(), bm.sharesBoughtBack(), bm.spentOnBuildings(), bm.unexplained(),
                g.getBuildingManager().getQuantity(g.getBuildingManager().getTemplateByName("Construction Materials Plant").getId()), g.getBuildingManager().getUnderConstructionBySector(mat.key()),
                mat.getStock(Good.MATERIALS), mat.output(Good.MATERIALS).soldLocal, mat.output(Good.MATERIALS).exported,
                cr.getRestructureCount(mat.key()), cr.getBlockedMonths(mat.key()), cr.isInsolvent(mat.key()),
                g.getMarkets().get(Good.MATERIALS).getDemand(), g.getMarkets().get(Good.MATERIALS).getLocalFilled(), g.getMarkets().get(Good.MATERIALS).getImported(),
                g.getMarkets().get(Good.MATERIALS).getDemandTrend(), g.getBuildingManager().getConstructionMaterials(), g.getMarkets().get(Good.MATERIALS).getLocalPrice(),
                g.getBank().equity(), g.getBank().getWriteOffs(), g.getBank().getResolutionLossThisMonth(), g.getBank().getFailures(),
                g.getBank().getTradingIncome(), g.getBank().getSecurities(), g.getBank().getNetIncome(),
                g.getLastInvestment(mat.key()));
            StringBuilder sb = new StringBuilder(String.format("   CREDIT m%-4d", g.getMonth()));
            for (Sector s : g.getSectors().all()) {
                sb.append(String.format(" %s cash %,.0f debt %,.0f assets %,.0f wo %,.0f restr %d ban %d |", s.key().substring(0, 4), s.getCash(), cr.getPrincipal(s.key()), cr.getAssets(s.key()), cr.getWrittenOffThisMonth(s.key()), cr.getRestructureCount(s.key()), cr.getBlockedMonths(s.key())));
            }
            out.println(sb);
            {
                // The people outside the families, and the shops that feed them (2026-09-11).
                PopulationManager pm = g.getPopulationManager();
                Unemployment u = g.getUnemployment();
                FamilyModel fm = g.getFamilies();
                ham.citybuildersim.sectors.Retail shop = g.getSectors().retail();
                out.printf("   PEOPLE m%-4d pop %,d wf %,d lf %,.0f filled %,d pool %,.0f onEI %,.0f offEI %,.0f unhoused %,.0f orphans %,.0f | hh %,.0f unplaced %,.1f doubled %,.1f seekers %,.0f share %,.0f | shop cov %,d want %,d dem %,d sold %,d pantry %,.0f op %.2f health %.2f supply %.2f cap %,.0f wantSpend %,.0f price %.3f | hunger %.1f%% sick %.1f%% | jobsLost %,.0f open %,.0f hires %,.0f arrUnhired %,.0f entrants %,.0f exits %,.0f EI %,.0f%n",
                        g.getMonth(), pm.getPopulation(), pm.getWorkforce(), pm.getLabourForce(), pm.getJobsFilled(),
                        u.getPool(), u.onEi(), u.getOffEi(), u.getUnhoused(), fm.getOrphansTotal(),
                        fm.totalHouseholds(), fm.getStillUnplaced(), fm.getDoubledUpHouseholds(),
                        fm.getSeekers(FamilyModel.Seeker.UNEMPLOYED), fm.getSeekersSharing(FamilyModel.Seeker.UNEMPLOYED),
                        shop.getStoreCoverage(), shop.getWantedDemand(), shop.getDemand(), shop.getProductsSold(),
                        (double) shop.getStoreInventory(), shop.getOperatingRate(), shop.getHealthRatio(), shop.getSupplyRatio(),
                        shop.getSpendingCapacity(), shop.getWantedSpend(), shop.getStoreSellPrice(),
                        g.getHouseholdBalance().getHungerRate() * 100, g.getHealth().getSickRate() * 100,
                        u.getJobsLost(), u.getOpenings(), u.getLocalHires(), u.getArrivalsUnhired(), u.getEntrants(), u.getOtherExits(),
                        u.getBenefitsPaid());
            }
            StringBuilder bl = new StringBuilder(String.format("   BUILT m%-4d", g.getMonth()));
            for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
                int q = g.getBuildingManager().getQuantity(t.getId());
                if (q > 0) bl.append(' ').append(t.getName()).append('=').append(q);
            }
            out.println(bl);
        }
        if (Boolean.getBoolean("playtest.bank") && g.getMonth() % 12 == 0) {
            Bank bk = g.getBank();
            Sector mat = g.getSectors().materials();
            BusinessDebtManager cr = g.getEconomyManager().getBusinessDebtManager();
            out.printf("BANK m%-4d eq %,.0f cash %,.0f dep %,.0f book %,.0f (sect %,.0f city %,.0f hh %,.0f) | earned %,.0f depInt %,.0f funding %,.0f writeoffs %,.0f payroll %,.0f tax %,.0f net %,.0f | depRate %.2f%% strain %.2f fails %d | MAT cash %,.0f debt %,.0f assets %,.0f pre %,.0f plants %d/%d stock %,.0f demand %,.0f trend %,.0f price %.2f restr %d ban %d | %s%n",
                g.getMonth(), bk.equity(), bk.getCash(), bk.getDeposits(), bk.getBook(), bk.getSectorBook(), bk.getCityBook(), bk.getHouseholdBook(),
                bk.getInterestEarned(), bk.depositInterest(), bk.getFundingCost(), bk.getWriteOffs(), bk.getPayroll(), bk.getTaxPaid(), bk.getNetIncome(),
                bk.depositRate() * 100, Math.min(99, bk.strain()), bk.getFailures(),
                mat.getCash(), cr.getPrincipal(mat.key()), cr.getAssets(mat.key()), mat.getNetIncome(),
                g.getBuildingManager().getQuantity(g.getBuildingManager().getTemplateByName("Construction Materials Plant").getId()),
                g.getBuildingManager().getUnderConstructionBySector(mat.key()), mat.getStock(Good.MATERIALS),
                g.getMarkets().get(Good.MATERIALS).getDemand(), g.getMarkets().get(Good.MATERIALS).getDemandTrend(), g.getMarkets().get(Good.MATERIALS).getLocalPrice(),
                cr.getRestructureCount(mat.key()), cr.getBlockedMonths(mat.key()), g.getLastInvestment(mat.key()));
        }
        if (Boolean.getBoolean("playtest.fx") && g.getMonth() % 12 == 0) {
            ForeignAccounts fa = g.getForeignAccounts(); OutwardInvestment oi = g.getOutwardInvestment();
            double tills = 0;
            for (String s : Sectors.KEYS) tills += Math.max(0, g.getEconomyManager().getSectorCash(s));
            out.printf("FX m%-4d rate %.3f parity %.3f pressure %+.2f openness %.2f | current %,.0f financial %,.0f | tills %,.0f abroad US$%,.0f ($%,.0f) share %.0f%% | deposit %.2f%% world %.2f%% | pop %d GDP %,.0f%n",
                g.getMonth(), fa.getRate(), fa.getParity(), fa.getLastPressure(), fa.getOpenness(),
                fa.monthlyCurrentAccount(), fa.monthlyFinancialAccount(),
                tills, oi.totalUsd(), oi.totalLocalValue(), oi.getTargetShare() * 100,
                g.getBank().depositRate() * 100, DebtManager.WORLD_BASE_RATE * 100,
                g.getPopulationManager().getPopulation(), g.getEconomyManager().getMonthGdp());
            Exchange ex = g.getExchange();
            out.printf("   desk: sold abroad %,.0f bought abroad %,.0f (emigrants %,.0f) | buybacks to hh %,.0f abroad %,.0f | dividends hh %,.0f abroad %,.0f | sent abroad %,.0f home %,.0f | hh saved %,.0f abroad US$%,.0f (sent %,.0f home %,.0f) divs %,.0f sold %,.0f | securities %,.0f%n",
                ex.getSoldAbroad(), ex.getBoughtFromAbroad(), ex.getEmigrantsPaid(), ex.getBuybackToHouseholds(), ex.getBuybackAbroad(),
                g.getEquity().getDividendHomeThisMonth(), g.getEquity().getDividendAbroadThisMonth(),
                oi.getInvestedAbroadThisMonth(), oi.getBroughtHomeThisMonth(),
                g.getHouseholdBalance().totalSavings(), g.getHouseholdBalance().totalAbroadUsd(), g.getHouseholdBalance().getSentAbroad(), g.getHouseholdBalance().getBroughtHome(),
                g.getHouseholdBalance().totalDividends(), g.getHouseholdBalance().totalSold(), g.getBank().getSecurities());
            StringBuilder co = new StringBuilder("   companies:");
            for (int c = 0; c < Equity.COMPANIES.length; c++) {
                SectorBooks.SectorMonth sm = c == Equity.BANK ? null : g.getSectorBooks().get(Equity.COMPANIES[c]);
                co.append(String.format(" %s %s sh %,.1f eq/assets %.2f tgt %.2f price %.3f fair %.3f yield %.1f%% abroad %.0f%% bought back %,.0f |",
                    Equity.COMPANIES[c].substring(0, 3), g.getEquity().getRegime(c), g.getEquity().getShares(c),
                    sm == null ? 0 : (sm.totalAssets() > 0 ? sm.equity() / sm.totalAssets() : 0), g.getEquity().getTargetEquityShare(c),
                    ex.price(c), ex.fair(c), ex.yieldAt(g.getEquity(), c, ex.price(c)) * 100, g.getEquity().foreignShare(c) * 100,
                    g.getEquity().getLifetimeBoughtBack(c)));
            }
            out.println(co);
        }
        /*
         * -Dplaytest.care=true: every month somebody was priced out of care
         * (2026-09-19), cell by cell - which households skipped the clinic's
         * bill to eat, what they skipped, and what the clinics did about it.
         * The instrument that traces a diverging playtest to the households
         * at the eat-less step.
         */
        if (Boolean.getBoolean("playtest.care")) {
            HouseholdBalance hb = g.getHouseholdBalance();
            Healthcare hc = g.getHealthcare();
            if (hc.getPricedOutTotal() > 0 || hb.getCareSkipped() > 0) {
                out.printf("CARE m%-4d priced out %,.1f (childcare %,.1f / general %,.1f / senior %,.1f)"
                        + " skipped $%,.3fk | could pay childcare %.4f general %.4f senior %.4f%n",
                        g.getMonth(), hc.getPricedOutTotal(), hc.getPricedOut(CareType.CHILDCARE),
                        hc.getPricedOut(CareType.GENERAL), hc.getPricedOut(CareType.SENIOR),
                        hb.getCareSkipped(), hc.getAffordability(CareType.CHILDCARE),
                        hc.getAffordability(CareType.GENERAL), hc.getAffordability(CareType.SENIOR));
                for (Household c : hb.cells()) {
                    if (c.households() < .5 || c.carePaid() >= 1) continue;
                    out.printf("     %-38s x%-8.1f paid %.3f of its care bill, skipped $%.4fk each;"
                            + " spendable $%.3fk against a basket of $%.3fk%n",
                            c.label(), c.households(), c.carePaid(), c.careSkipped(),
                            c.spendable(), c.subsistence());
                }
            }
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
        Sickness sickness = g.getSickness();
        for (AgeBand band : AgeBand.values()) illnessDeathsByBand[band.ordinal()] += sickness.getLastDeaths(band);
        for (AgeBand band : AgeBand.values()) deathsByBandRun[band.ordinal()] += g.getCohorts().getDeaths(band);
        orphanDeathsRun += g.getLastOrphanDeaths();
        unhousedDeathsRun += g.getLastUnhousedDeaths();
        allDeaths += g.getCohorts().getLastDeaths();
        personMonths += g.getPopulationManager().getPopulation();
        pricedOutSum += g.getHealthcare().getPricedOutTotal();
        careSkippedSum += g.getHouseholdBalance().getCareSkipped();
        studentInterestSum += g.getStudentLoanInterest();
        grantsSum += g.getEconomyManager().getStudentGrants();
        double longSick = sickness.peoplePastTwoMonths(g.getCohorts());
        if (longSick > worstLongSick) { worstLongSick = longSick; worstLongSickMonth = g.getMonth(); }
        Crime crime = g.getCrime();
        ham.citybuildersim.sectors.BusinessServices bsm = g.getSectors().businessServices();
        double seatsNow = bsm.getSeats();
        if (seatsNow > peakSeats) { peakSeats = seatsNow; peakSeatsMonth = g.getMonth(); }
        if (seatsNow > 0) { monthsWithSeats++; lastSeatMonth = g.getMonth(); }
        serviceExportsRun += bsm.statement().exports;

        crimeMonths++;
        crimeVsSum += crime.getRateVsCanada();
        coverageSum += crime.getCoverage();
        if (crime.getRateVsCanada() > worstCrimeVs) { worstCrimeVs = crime.getRateVsCanada(); worstCrimeMonth = g.getMonth(); }
        killedRun += g.getCohorts().getKilled(AgeBand.ADULT);
        stolenRun += crime.getStolen();
        caughtRun += crime.getCaught();
        notHeldRun += crime.getNotHeld();
        worstPrisoners = Math.max(worstPrisoners, crime.prisoners());
        ForeignAccounts fxRun = g.getForeignAccounts();
        if (fxRun.getRate() > peakRate) { peakRate = fxRun.getRate(); peakRateMonth = g.getMonth(); }
        if (fxRun.getRate() > FAR_FROM_PARITY * fxRun.getParity()) monthsFarFromParity++;
        if (fxRun.getRate() >= fxRun.getMaxRate() * (1 - 1e-9)) monthsAtGuard++;
        double vaultNow = fxRun.getReservesUsd();
        if (vaultNow < vaultLow) { vaultLow = vaultNow; vaultLowMonth = g.getMonth(); }
        // Against THIS city's founders' dollars (0.7.10), which the preset chose.
        if (halfGoneMonth == 0 && vaultNow < g.getFoundingReserveUsd() / 2) halfGoneMonth = g.getMonth();
        if (emptyMonth == 0 && vaultNow < g.getFoundingReserveUsd() / 100) emptyMonth = g.getMonth();
        // The land office's month, struck with the budget's (0.7.6).
        if (fxRun.getLandUsdThisMonth() > 0) {
            monthsLandBought++;
            landLocalRun += fxRun.getLandLocalThisMonth();
        }
        double soldUsd = fxRun.getDefenceUsd();
        if (soldUsd > 0) {
            monthsDefended++;
            defendedUsdRun += soldUsd;
            if (soldUsd > peakDefenceUsd) { peakDefenceUsd = soldUsd; peakDefenceMonth = g.getMonth(); }
        }
        dialPath.add(g.getDebtManager().getPolicyRate());
        double spendNow = g.getHouseholdBalance().getSpendFactor();
        if (spendNow < spendLow) { spendLow = spendNow; spendLowMonth = g.getMonth(); }
        if (spendNow > spendHigh) { spendHigh = spendNow; spendHighMonth = g.getMonth(); }
        spendPath.add(spendNow);
        if (g.getBank().isDepositPayoutHeld()) {
            depositCappedMonths++;
            if (depositCappedFirst == 0) depositCappedFirst = g.getMonth();
            depositCappedLast = g.getMonth();
        }
        {
            Bank bk = g.getBank();
            int k = bankMonths % 12;
            bankNii[k] = bk.netInterestIncome();
            bankFees[k] = bk.feeIncome();
            bankOther[k] = bk.getTradingIncome() + bk.getPaperGains();
            bankOpex[k] = bk.operatingExpenses();
            bankNet[k] = bk.getNetIncome();
            bankEquityRing[k] = bk.equity();
            bankAssetsRing[k] = bk.totalAssets();
            bankLossRing[k] = bk.getWriteOffs();
            bankAtRiskRing[k] = bk.getSectorBook() + bk.getHouseholdBook();
            bankMonths++;
            double pol = g.getDebtManager().getPolicyRate();
            if (pol > .001 && bk.getBranches() > 0) {
                shareSum += bk.depositRate() / pol;
                shareMonths++;
            }
            sharePolicy += pol;
            sharePolicySq += pol * pol;
            shareDeposit += bk.depositRate();
            sharePolicyDeposit += pol * bk.depositRate();
            bankYear(g);
        }
        if (g.getPriceIndex().hasRate()) inflationPath.add(g.getPriceIndex().inflation());
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
        // Every sector's every stock and pantry, since the sector template.
        for (Sector s : g.getSectors().all()) {
            for (Good good : Good.values()) {
                if (s.getStock(good) < -1e-9) {
                    flag(month, "negative stock", s.key() + " " + good + " " + s.getStock(good));
                }
                if (s.getPantry(good) < -1e-9) {
                    flag(month, "negative pantry", s.key() + " " + good + " " + s.getPantry(good));
                }
            }
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

        for (String sector : Sectors.KEYS) {
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
        finite(month, "SAVE: construction unearned revenue",
                g.getSectors().construction().getUnearnedRevenue());
        finite(month, "SAVE: construction backlog",
                g.getSectors().construction().getBacklogPoints());
        // Every sector, whole, the way the save carries it.
        for (Sector s : g.getSectors().all()) {
            String k = "SAVE: " + s.key() + " ";
            finite(month, k + "cash", s.getCash());
            finite(month, k + "interest", s.getInterestExpense());
            finite(month, k + "property tax", s.getPropertyTaxExpense());
            finite(month, k + "maintenance", s.getMaintenanceExpense());
            for (Good good : Good.values()) {
                finite(month, k + "stock " + good, s.getStock(good));
                finite(month, k + "pantry " + good, s.getPantry(good));
            }
            Sector.Statement st = s.statement();
            finite(month, k + "revenue", st.revenue);
            finite(month, k + "inputs", st.inputs);
            finite(month, k + "payroll", st.payroll);
            finite(month, k + "sales tax", st.salesTax);
            finite(month, k + "net income", st.netIncome);
            finite(month, k + "pending revenue", s.pending().revenue());
            finite(month, k + "pending purchases", s.pending().purchases());
            for (java.util.Map.Entry<String, Double> x : s.toState().extras.entrySet()) {
                finite(month, k + x.getKey(), x.getValue());
            }
        }
        for (GoodsMarket m : g.getMarkets().all()) {
            String k = "SAVE: market " + m.good() + " ";
            finite(month, k + "price", m.getLocalPrice());
            finite(month, k + "demand", m.getDemand());
            finite(month, k + "supply", m.getSupply());
        }
        finite(month, "SAVE: household savings", g.getHouseholds().getCumulativeSaving());
        finite(month, "SAVE: land owned", l.getOwnedSqFt());
        finite(month, "SAVE: land price", l.getPricePerSqFt());
        finite(month, "SAVE: property tax charged", e.getTotalPropertyTax());
        finite(month, "SAVE: accrued city interest", e.getExpenses());

        finiteArray(month, "SAVE: national accounts", e.getNationalAccountsState());
        for (double[] offer : l.getMarket().getOffersState()) finiteArray(month, "SAVE: land offers", offer);

        finite(month, "iron reserves", l.getIronReserveTonnes());

        if (l.getIronReserveTonnes() < 0) {
            flag(month, "negative iron reserves", "" + l.getIronReserveTonnes());
        }
        if (g.minesCommitted() > l.getIronDeposits()) {
            flag(month, "more mines than deposits",
                    g.minesCommitted() + " on " + l.getIronDeposits());
        }
        // ...and the wells on the oil (0.7.62).
        if (l.getOilReserveTonnes() < 0) flag(month, "negative oil reserves", "" + l.getOilReserveTonnes());
        if (g.wellsCommitted() > l.getOilSites()) {
            flag(month, "more wells than oil sites", g.wellsCommitted() + " on " + l.getOilSites());
        }
        // Six places a side (0.7.67): a place may wait empty, but only while its side has no room for it.
        int roomy = l.getMarket().emptyWithRoom();
        if (roomy > 0) {
            flag(month, "the land office leaves a place empty with room for it",
                    roomy + " of " + (LandMarket.OFFERS - l.getListing().size()) + " empty");
        }

        // Every band-priced good stays inside its band.
        for (GoodsMarket m : g.getMarkets().all()) {
            if (m.good().pricing() != Good.Pricing.BAND) continue;
            double price = m.getLocalPrice();
            if (price < m.floor() - 1e-9 || price > m.ceiling() + 1e-9) {
                flag(month, m.good() + " price outside its band", "" + price);
            }
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

    /* =====================================================================
       WHO IS PLAYING (2026-09-17)

       Jerus asked why the shops run at half rate. The answer turned out to be
       nothing to do with the shops: measured over six hundred months of the
       same city, an attentive player takes the operating rate from 0.62 to
       0.89, hunger from 42% to 22% and sickness from 27.6% to 9.5%, purely by
       putting up power, water, streets, clinics and cemeteries as the city
       grows. The model is fine. THE PLAYER WAS THE PROBLEM.

       AND NOT BECAUSE advise() IS BAD - it ranks constraints by the output
       each shortage is costing, which is the right rule and has its own essay
       above. It is the RHYTHM. The loop below skips six to a hundred and
       twenty months at a stretch, averaging fifty-four, and then allows three
       moves. That is deliberate and the comment there says why: it is the
       length a person actually clicks. But a city left alone for a century
       between decisions is permanently behind its own growth, so every number
       this harness has ever reported was measured in a city in crisis from
       neglect - which is a fine robustness test and a poor instrument for
       measuring anything else.

       SO THERE ARE TWO PLAYERS NOW, and the default is unchanged. Without the
       flag this file plays exactly as it always has, so every ensemble in the
       project docs stays comparable to the digit - which is the whole reason
       for a flag rather than a fix. With -Dplaytest.player=attentive it checks
       in yearly and gets eight moves: still no cleverness, still the same
       advise(), just somebody who looks at the city more than twice a century.
       ===================================================================== */

    /** True when this run is played by somebody paying attention. */
    static final boolean ATTENTIVE = "attentive".equalsIgnoreCase(
            System.getProperty("playtest.player", "occasional"));

    /** Months the player will let pass before looking, at most. */
    static int longestSkip() { return ATTENTIVE ? 12 : Integer.MAX_VALUE; }

    /** -Dplaytest.schools=true: the city builds schools, which the advisor never does. See the founding. */
    static final boolean SCHOOLS = Boolean.getBoolean("playtest.schools");

    /** -Dplaytest.childcare=true (0.7.71, batch N2): the city builds childcare by the build advice's own card, which the advisor never does. See childcareWhenNeeded(). */
    static final boolean CHILDCARE = Boolean.getBoolean("playtest.childcare");

    /**
     * -Dplaytest.autobuild=true (0.7.73, batch N4): the player turns automatic
     * building on at the founding (AutoBuilder, at its defaults) and leaves it
     * the city's works - the advisor's power, water, road and transit,
     * healthcare, burial, police and cells moves are not offered, and the
     * schools and childcare flags are its - while it does the rest as it
     * always has: the ground, iron, homes, shops, the builders, food and jobs.
     * A flag, as the schools are, and off: the default run is unchanged. Not
     * final: AutoBuildCheck plays its decades by this player with it set, and
     * puts it back.
     */
    static boolean AUTOBUILD = Boolean.getBoolean("playtest.autobuild");

    /** Whether any education dial or the schools flag was set, so the summary says what they did. */
    static boolean educationSet = false;

    /* =====================================================================
       THE RATE, HELD (2026-09-21) - the measurement 7.0 turns green

       Jerus, on the central bank: "the short term rates, aka the one you
       choose, those should be basically the tbill rate". Before the central
       bank is built there has to be a number that says what the policy rate
       does to inflation TODAY, or 7.0 has nothing to turn from red to green.

       -Dplaytest.policyRate=<annual> holds the dial at that rate from the
       month the currency is allowed to move - the first month past
       ForeignAccounts.SETTLING_MONTHS - to the end of the run, instead of the
       advisor's quarter-steps toward the rule; the summary says it was held.
       The way the education flags hold theirs: set, and left. Unset, the
       advisor sets the dial as it always has and the run is the run it
       always was, to the byte. MonetaryCheck section 6 is the same question
       asked of one founding for five years - and since 0.7.3 it is asserted
       there, the demand channel in: inflation falls with the rate. Here it is
       still a measurement, over a whole run.
       ===================================================================== */

    /** The rate the dial is held at under -Dplaytest.policyRate, or null when the advisor sets it. */
    static final Double POLICY_RATE = System.getProperty("playtest.policyRate") == null
            ? null : Double.valueOf(System.getProperty("playtest.policyRate"));

    /** True once the dial is the flag's rather than the advisor's: the flag is set and the currency may move. */
    static boolean holdsPolicyRate(Game g) {
        return POLICY_RATE != null && g.getMonth() > ForeignAccounts.SETTLING_MONTHS;
    }

    /* =====================================================================
       THE FOUNDING, AS A CHOICE (0.7.10)

       -Dplaytest.founding=lean|standard|wealthy founds the city on one of
       Founding's presets: Lean's D$25M and US$10M, Standard's D$100M and
       US$25M (Game's two constants, the default), or Wealthy's D$2.5B and
       US$1B - the start every city had from 0.6.10 to 0.7.9. The name stays
       Danzik and the world the default, so the only thing the flag moves is
       the money. A run on wealthy is the 0.7.9 run to the byte, which is the
       proof that making the founding a choice changed nothing else.

       ...AND insane (0.7.14): D$0 and US$0, the ground owed abroad on a
       dollar bond. Its player borrows the village's invoice on the build
       screen's bond before placing a house (borrowForTheVillage()), and
       every later stage of the village the treasury is short of on the
       funding page's bond (villageBuild()); every other founding builds its
       village as before, to the byte.
       ===================================================================== */

    /** The founding preset under -Dplaytest.founding, standard when unset. */
    static final Founding.Preset FOUNDING = Founding.Preset.valueOf(
            System.getProperty("playtest.founding", "standard").trim().toUpperCase(java.util.Locale.ROOT));

    /** The city the run founds: Danzik, on the flag's preset, in the default world. */
    static Founding founding() {
        return Founding.named(Founding.DEFAULT_CITY_NAME, FOUNDING, WorldEconomy.DEFAULT_MEAN_INFLATION);
    }

    /* =====================================================================
       THE TRACE, BESIDE THE REPORT (0.7.10)

       -Dplaytest.trace=<prefix> writes eight files (two at 0.7.10, the rest
       below) and never a line of the report, so a traced run's report is
       the untraced run's to the byte:
       <prefix>-month.csv, one row a month - the treasury, its debt and what
       servicing it costs against revenue, the four ways a treasury goes short
       (the central bank's advances, arrears, notes and T-bills, the
       overdraft), the vault and the defence, the currency against its guard,
       the price level, the bank's failures, the refused skips and the
       advisor's refusals for money - and <prefix>-borrow.csv, one row for
       every piece of city paper the month it first appears: what it is, its
       face in local money, the yield it was issued at, and what the advisor
       was trying to build when it borrowed, if it was. The founding batch's
       ensembles are read off these (the project's founding-a-city.md).

       ...AND A THIRD, <prefix>-house.csv (0.7.11): the landlords' month -
       the homes, the people, the jobs and the pressure; both rents against
       what a new home requires and the break-even; the landlord's till,
       assets and debt, its mortgages and its bullets apart; the insured
       rate beside prime and its own quote; the insurance's premiums and
       claims; the principal its mortgages paid and the terms renewed or
       fallen due; and the advisor's line, whose words say why it built or
       did not. The mortgage batch's ensembles are read off it.

       ...AND A FOURTH AND A FIFTH (0.7.11, round 2): <prefix>-pop.csv, the
       people's month - who lives here by age, what migration aimed at and
       did and the pulls behind it, the two housing markets, the jobs and
       the households (tracePop()); and <prefix>-bank.csv, the bank's month -
       its equity against the exposure and the weighted book, both ratios
       and both targets and which requirement binds, its stance, its
       branches and the months they have not paid, and the insured rate's
       parts (countLeverage()).

       ...AND A SIXTH, <prefix>-bonds.csv (0.7.12): a row a month of THE
       BONDS (countBonds()).

       ...AND A SEVENTH, <prefix>-build.csv (0.7.17): the builders, the
       doors and the wage bill - the homes against the households who want
       a door, who is doubled up and who has none, the unskilled premium,
       the builders' plant, staffing, output, repairs, what the sites were
       left and how much of it they used, their month's result, the
       landlords' orders on site, output parked on a stack past what it
       owes, every employer's payroll against what the households were
       paid, and the builders' posts offered against their posts and the
       need they were struck on (traceBuild()). Round 1 of 0.7.17 is read
       off it.

       ...AND AN EIGHTH, <prefix>-labour.csv (0.7.18): the labour market by
       band - posts offered, the band's own workers, the supply the wage is
       priced against, posts filled, tightness, multiple and premium - the
       graduates holding unskilled posts, the month's arrivals and the
       unskilled among them, and the city's own hospitals' and schools'
       posts and how many of them are staffed (traceLabour()). Round 2 of
       0.7.18 is read off it.
       ===================================================================== */

    /** The trace's prefix under -Dplaytest.trace, or null. */
    static final String TRACE = System.getProperty("playtest.trace");
    static java.io.PrintWriter traceMonths, traceBorrow, traceHouse, tracePop, traceBank, traceBonds, traceBuild, traceLabour;
    /** The paper already written to the borrow file, by identity. */
    static final java.util.Set<Debt> paperSeen =
            java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
    /**
     * Writes every piece of paper not yet written, with what it paid for -
     * called the moment the advisor borrows, so the purpose is its own, and
     * at the end of every month for whatever the game issued by itself.
     */
    static void notePaper(Game g, String purpose) {
        if (traceBorrow == null) return;
        for (Debt d : g.getDebtManager().getDebt()) {
            if (paperSeen.add(d)) {
                // The yield it was issued at; paper issued abroad records none, so its coupon.
                boolean yielded = d.getIssueYield() > 0;
                double rate = yielded ? d.getIssueYield()
                        : d instanceof LongTermBond term ? term.getCouponRate() : 0;
                traceBorrow.printf(java.util.Locale.ROOT, "%d,%s,%s,%b,%.3f,%.6f,%s,%d,%s%n",
                        g.getMonth(), d.getType().replace(',', ';'), d.getClass().getSimpleName(),
                        d.isForeign(), d.getOustandingPrincipal(), rate, yielded ? "yield" : "coupon",
                        d.getDuration(), purpose.replace(',', ';'));
            }
        }
    }

    static void traceOpen() {
        if (TRACE == null) return;
        try {
            traceMonths = new java.io.PrintWriter(Files.newBufferedWriter(Path.of(TRACE + "-month.csv")));
            traceBorrow = new java.io.PrintWriter(Files.newBufferedWriter(Path.of(TRACE + "-borrow.csv")));
            traceHouse = new java.io.PrintWriter(Files.newBufferedWriter(Path.of(TRACE + "-house.csv")));
            tracePop = new java.io.PrintWriter(Files.newBufferedWriter(Path.of(TRACE + "-pop.csv")));
            traceBank = new java.io.PrintWriter(Files.newBufferedWriter(Path.of(TRACE + "-bank.csv")));
            traceBonds = new java.io.PrintWriter(Files.newBufferedWriter(Path.of(TRACE + "-bonds.csv")));
            traceBuild = new java.io.PrintWriter(Files.newBufferedWriter(Path.of(TRACE + "-build.csv")));
            traceLabour = new java.io.PrintWriter(Files.newBufferedWriter(Path.of(TRACE + "-labour.csv")));
        } catch (java.io.IOException e) {
            throw new IllegalStateException("cannot write the trace at " + TRACE, e);
        }
        traceMonths.println("month,pop,cash,gdp,taxIncome,interest,cityDebt,foreignDebt,notes,advances,"
                + "arrears,overdraft,rate,parity,atGuard,index,inflation,vaultUsd,defenceUsd,"
                + "bankFails,policy,cityRate,refusedSkips,noMoney,stake,fundValue,fundCash,preferred,"
                + "floor,bankFloor,cbPaperShare,cbHeld,m0,cbBoughtHH,"
                + "cbParAtIssue,cbPaidAtIssue,cbBought,cbSold,bankPaidForPaper,"
                + "grant,revenue,luxNet,luxRevenue,luxMargin,sectorsPreTax");
        traceBorrow.println("month,type,kind,foreign,face,rate,rateIs,months,for");
        traceHouse.println("month,pop,homes,households,capacity,jobs,latent,pressure,rentF,rentS,reqF,reqS,"
                + "breakEven,reCash,reAssets,rePrincipal,mortgages,mortgagePrincipal,bulletPrincipal,"
                + "mortgageRate,insuredRate,prime,reQuote,policy,index,inflation,unemployment,"
                + "premiums,claims,mortgageRepaid,renewed,fallenDue,reNet,reRaised,reDividends,why");
        tracePop.println("month,pop,babies,children,teens,adults,seniors,births,deaths,target,arrivals,departures,"
                + "crowding,residentsPerJob,affordPull,crimePull,crimeDepart,seniorPull,decliningShare,"
                + "familyHomes,studioHomes,familyPressure,studioPressure,jobs,unemployment,households");
        traceBank.println("month,equity,exposure,weighted,leverage,capitalRatio,levBinds,minimum,target,stance,"
                + "branches,uncovered,closed,insuredRate,insFtp,insRun,insCap,prime,policy,failures,levTarget,"
                + "capTarget,mortgageBook,netIncome,insuredRationed,"
                + "customers,accountFees,runCost,operating,feeCover,deposits,savings,capacity,window,laterCost,bankWhy");
        StringBuilder bondsHeader = new StringBuilder("month,bonds,face,faceHH,faceBank,faceCo,faceWorld,loans,issues,"
                + "issuedFace,avgCoupon,couponsHH,couponsBank,couponsCo,couponsAbroad,principalHH,principalBank,"
                + "principalCo,principalAbroad,lossHH,lossBank,lossCo,lossWorld,worldBought,worldSold,hhBought,hhSold,"
                + "hhBoughtAbroad,hhSoldAbroad,postedBuy,postedSell,filled,sellsPosted,sellsWaited,sellWaitedQty,trades,"
                + "herfindahl,addOn,addOnWeighted,bankBonds,prime,policy,bondRatio");
        for (int i = 0; i < Sectors.KEYS.length; i++) {
            bondsHeader.append(String.format(",L%d,B%d,rate%d,cpn%d,yld%d,conc%d,dL%d,dB%d,woL%d,woB%d", i, i, i, i, i, i, i, i, i, i));
        }
        traceBonds.println(bondsHeader);
        traceBuild.println("month,homes,households,seeking,doubledUp,stillUnplaced,arrivals,crowding,"
                + "unskFill,uPremium,consCap,consFill,consOut,maint,siteOut,consUtil,remainingPts,"
                + "consNet,consPayroll,depots,reOnSite,reOwed,banked,empPayroll,hhWage,consPostsOffered,consPosts,consNeed,"
                + "consRevenue,consPreTax,consSalesTax,escalation,workPricePerPoint,labourIndex,repairsBill,"
                + "rentalRebate,reInvested,cityOrderTax");
        StringBuilder labourHeader = new StringBuilder("month,labourForce,unemployment,arrivals,arrivalsNone,gradsInNone");
        for (WageBand band : WageBand.values()) {
            int b = band.ordinal();
            labourHeader.append(String.format(",posts%d,own%d,supply%d,filled%d,tight%d,mult%d,prem%d,wage%d", b, b, b, b, b, b, b, b));
        }
        labourHeader.append(",wageDoctor,healthPosts,healthFilled,schoolPosts,schoolFilled");
        traceLabour.println(labourHeader);
        // Closed however the run ends - a finding exits early too.
        Runtime.getRuntime().addShutdownHook(new Thread(LongPlaytest::traceClose));
    }

    static void traceMonth(Game g) {
        if (traceMonths == null) return;
        DebtManager paper = g.getDebtManager();
        notePaper(g, "");
        double notes = 0;
        for (Debt d : paper.getDebt()) {
            if (d instanceof ShortTermTBill) notes += d.getOustandingPrincipal();
        }
        ForeignAccounts fx = g.getForeignAccounts();
        int noMoney = 0;
        for (Map.Entry<String, Integer> r : refusals.entrySet()) {
            if (r.getKey().endsWith(": no money")) noMoney += r.getValue();
        }
        traceMonths.printf(java.util.Locale.ROOT,
                "%d,%d,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.6f,%.6f,%d,%.6f,%.6f,%.3f,%.3f,%d,%.6f,%.6f,%d,%d,%.6f,%.3f,%.3f,%.3f,"
                + "%.6f,%.6f,%.6f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.4f,%.3f%n",
                g.getMonth(), g.getPopulationManager().getPopulation(), g.getCash(),
                g.getEconomyManager().getMonthGdp(), g.getEconomyManager().getTaxIncome(),
                g.getEconomyManager().getNationalAccounts().getInterestExpense(),
                paper.getAllPrincipal(), paper.getForeignPrincipal(), notes,
                g.getCentralBank().getAdvancesToTreasury(), g.getArrearsTotal(), paper.getOverdraft(),
                fx.getRate(), fx.getParity(), fx.getRate() >= fx.getMaxRate() * (1 - 1e-9) ? 1 : 0,
                g.getPriceIndex().getIndex(), g.getPriceIndex().inflation(),
                fx.getReservesUsd(), fx.getDefenceUsd(), g.getBank().getFailures(),
                paper.getPolicyRate(), paper.getRate(), refusedSkips, noMoney,
                // ...and the city's bank and fund (0.7.14), on the end.
                g.cityStakeInBank(), g.fundValue(), g.getFund().getCash(), g.getBank().preferredOutstanding(),
                // ...and the floor split by who holds the paper (0.7.15): the floor, the
                // bank's unsplit, the central bank's share of all the city's paper, its
                // book at face, M0, and what it paid the households for theirs this month.
                paper.floorRate(), paper.bankFloorRate(), paper.centralBankShareOfPaper(),
                g.getCentralBank().getPaperHeld(), g.getCentralBank().m0(),
                g.getCentralBank().getBoughtFromHouseholds(),
                // ...and (0.7.15, round 2) the par it rolled of its own at issue this
                // month and what it paid for it, all it bought and sold this month,
                // and what the bank paid for the city's paper at this month's settle.
                g.getCentralBank().getParAtIssue(), g.getCentralBank().getBoughtAtIssue(),
                g.getCentralBank().getBoughtPaper(), g.getCentralBank().getSoldPaper(),
                g.getCityPaperSettled(),
                // ...and (0.7.19) the student grant against the city's revenue, Luxury's
                // month and markup, and every sector's month before profit tax.
                g.getEconomyManager().getStudentGrants(),
                g.getEconomyManager().getNationalAccounts().getTotalRevenue(),
                g.getSectors().luxuryRetail().statement().preTaxIncome,
                g.getSectors().luxuryRetail().statement().revenue,
                g.getSectors().luxuryRetail().getMargin(), sectorsPreTax(g));
        traceHouse(g);
        tracePop(g);
        traceBuild(g);
        traceLabour(g);
    }

    /**
     * The labour market's month, one row of <prefix>-labour.csv (0.7.18).
     * Posts are the posts offered, by band; filled is each job type's posts
     * times its fill, summed over the band; a band's wage is its first
     * ungated job type's (LabourMarket.bandPremium() reads the same one).
     * The graduates in unskilled posts are the unskilled posts filled less
     * the unskilled workers in them - an unskilled worker can hold no other
     * post, and the posts take them first.
     */
    static void traceLabour(Game g) {
        if (traceLabour == null) return;
        PopulationManager p = g.getPopulationManager();
        LabourMarket lm = g.getLabourMarket();
        long[] jobs = p.getJobs();
        double[] fill = p.getJobFillRate();
        double[] own = p.workforceByBand();
        double[] supply = p.supplyByBand();
        int nb = WageBand.values().length;
        double[] posts = new double[nb], filled = new double[nb], wage = new double[nb];
        for (JobType j : JobType.values()) {
            int b = WageBand.of(j).ordinal();
            posts[b] += jobs[j.ordinal()];
            filled[b] += jobs[j.ordinal()] * fill[j.ordinal()];
        }
        for (WageBand band : WageBand.values()) {
            for (JobType j : JobType.values()) {
                if (WageBand.of(j) == band && !LabourMarket.isGated(j)) { wage[band.ordinal()] = lm.getWage(j); break; }
            }
        }
        int none = WageBand.NONE.ordinal();
        double grads = Math.max(0, filled[none] - Math.min(own[none], filled[none]));
        double[] mix = g.getMigration().getLastArrivalMix();
        double[] cat = new double[4];
        long[] hp = g.getBuildingManager().getJobArrayPerCategory(BuildingType.HEALTHCARE);
        long[] ep = g.getBuildingManager().getJobArrayPerCategory(BuildingType.EDUCATION);
        for (int i = 0; i < hp.length && i < fill.length; i++) {
            cat[0] += hp[i]; cat[1] += hp[i] * fill[i];
            cat[2] += ep[i]; cat[3] += ep[i] * fill[i];
        }
        StringBuilder row = new StringBuilder();
        row.append(String.format(java.util.Locale.ROOT, "%d,%.1f,%.5f,%.2f,%.2f,%.1f", g.getMonth(), p.getLabourForce(),
                p.getUnemploymentRate(), g.getMigration().getLastArrivals(),
                mix == null || mix.length == 0 ? 0 : mix[none], grads));
        for (WageBand band : WageBand.values()) {
            int b = band.ordinal();
            row.append(String.format(java.util.Locale.ROOT, ",%.1f,%.1f,%.1f,%.1f,%.5f,%.5f,%.5f,%.5f", posts[b], own[b],
                    supply[b], filled[b], lm.getTightness(band), lm.getBandMultiple(band), lm.bandPremium(band), wage[b]));
        }
        row.append(String.format(java.util.Locale.ROOT, ",%.5f,%.1f,%.1f,%.1f,%.1f", lm.getWage(JobType.UNIV_DOCTOR),
                cat[0], cat[1], cat[2], cat[3]));
        traceLabour.println(row);
    }

    /**
     * The builders, the doors and the wage bill, one row of <prefix>-build.csv
     * (0.7.17). The employers' payroll is every payer's own figure as it
     * stands - the sectors, the bank, the utilities and the four services
     * the city staffs - and the households' is the wage bill they are paid
     * off, both read at the same moment so the two are the same month's.
     */
    static void traceBuild(Game g) {
        if (traceBuild == null) return;
        BuildingManager b = g.getBuildingManager();
        FamilyModel fm = g.getFamilies();
        PopulationManager p = g.getPopulationManager();
        ham.citybuildersim.sectors.Construction c = g.getSectors().construction();
        String reKey = g.getSectors().realEstate().key();
        int reOnSite = 0;
        double reOwed = 0, banked = 0;
        for (BuildingsStacks s : b.getStacksUnderConstruction()) {
            if (!reKey.equals(s.getBuilding().getSector())) continue;
            reOnSite += s.getUnderConstruction();
            reOwed += s.getUnderConstruction() * (double) s.getBuilding().getConstructionPoints()
                    - s.getConstructionProgress();
        }
        for (BuildingsTemplate t : b.getTemplates()) {
            BuildingsStacks s = b.getStack(t);
            if (s == null) continue;
            banked += Math.max(0, s.getConstructionProgress()
                    - s.getUnderConstruction() * (double) t.getConstructionPoints());
        }
        double[] wages = p.getWagesPerType();
        double[] fill = p.getJobFillRate();
        double employers = g.getEconomyManager().getBankPayroll()
                + g.getServicesManager().getUtilitiesHandler().getUtilityPayroll();
        for (Sector s : g.getSectors().all()) employers += s.getPayroll();
        for (BuildingType cat : new BuildingType[] { BuildingType.HEALTHCARE, BuildingType.EDUCATION,
                BuildingType.SAFETY, BuildingType.INFRASTRUCTURE }) {
            employers += b.getCategoryPayroll(cat, wages, fill);
        }
        traceBuild.printf(java.util.Locale.ROOT,
                "%d,%d,%.2f,%.2f,%.3f,%.3f,%.2f,%.5f,%.5f,%.5f,%d,%.5f,%d,%.2f,%d,%.5f,%.1f,%.3f,%.3f,%d,%d,%.1f,%.1f,%.3f,%.3f,%d,%d,%.4f,"
                        + "%.3f,%.3f,%.3f,%.3f,%.5f,%.5f,%.3f,%.3f,%.3f,%.3f%n",
                g.getMonth(), b.getTotalHomes(), fm.totalHouseholds(), fm.householdsSeekingDoors(),
                fm.getDoubledUpHouseholds(), fm.getStillUnplaced(), g.getMigration().getLastArrivals(),
                g.getMigration().getLastCrowding(), fill[JobType.NO_DIPLOMA.ordinal()],
                g.getLabourMarket().premium(JobType.NO_DIPLOMA),
                b.getTotalConstructionCapacity(), c.getAverageFill(), g.getConstructionOutput(),
                g.getMaintenancePoints(), g.getBuildingOutput(), c.getUtilisation(),
                b.getRemainingConstructionPoints(), c.statement().netIncome, c.statement().payroll,
                b.countByName("Construction Depot"), reOnSite, reOwed, banked, employers, p.getTotalWage(),
                c.getPostsOffered(), c.getPostsStanding(), c.getCrewsNeeded(),
                // ...and (0.7.19) their month before tax, the tax they remitted, the owners'
                // material escalation, what a point of work is paid beyond material and tax,
                // their wage index and the city's repair bill.
                c.statement().revenue, c.statement().preTaxIncome, c.statement().salesTax,
                c.getEscalationThisMonth(), b.nonMaterialPricePerPoint(), b.buildersWageIndex(),
                g.getEconomyManager().getMaintenanceBillTotal(),
                // ...and (revised) the landlords' rebate on their new homes this month, what
                // they spent on buildings net of it, and the tax in the city's own orders
                // that the builders have not yet billed.
                g.getSectors().realEstate().statement().capitalTaxCredit,
                g.getInvestedThisMonth(g.getSectors().realEstate().key()), cityOrderTax(g));
    }

    /** The builders' tax in the city's orders still on site - paid with the order, not yet billed (revised 0.7.19). */
    static double cityOrderTax(Game g) {
        double value = 0;
        for (BuildingManager.ContractRecord r : g.getBuildingManager().getContractRecords()) {
            if ("City".equals(r.payer)) value += r.value;
        }
        return value * g.getEconomyManager().buildersSalesRate();
    }

    /** Every sector's month before profit tax, summed - what Luxury's share of the city's profit is read against (0.7.19). */
    static double sectorsPreTax(Game g) {
        double t = 0;
        for (Sector s : g.getSectors().all()) t += s.statement().preTaxIncome;
        return t;
    }

    /**
     * The people's month, one row of <prefix>-pop.csv (0.7.11, round 2): who
     * lives here by age, what migration aimed at and did and why, and the two
     * housing markets - the trace that asks why a city with the same jobs
     * holds fewer people.
     */
    static void tracePop(Game g) {
        if (tracePop == null) return;
        PopulationCohorts c = g.getCohorts();
        Migration mg = g.getMigration();
        ham.citybuildersim.sectors.RealEstate re = g.getSectors().realEstate();
        tracePop.printf(java.util.Locale.ROOT,
                "%d,%d,%.1f,%.1f,%.1f,%.1f,%.1f,%.3f,%.3f,%.1f,%.3f,%.3f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%d,%d,%.4f,%.4f,%d,%.4f,%.1f%n",
                g.getMonth(), g.getPopulationManager().getPopulation(),
                c.get(AgeBand.BABY), c.get(AgeBand.CHILD), c.get(AgeBand.TEEN), c.get(AgeBand.ADULT),
                c.get(AgeBand.SENIOR), c.getLastBirths(), c.getLastDeaths(),
                mg.getLastTarget(), mg.getLastArrivals(), mg.getLastDepartures(), mg.getLastCrowding(),
                mg.getLastResidentsPerJob(), mg.getLastAffordabilityPull(), mg.getLastCrimePull(),
                mg.getLastCrimeDepartures(), mg.getLastSeniorPull(), mg.getLastDecliningShare(),
                re.getFamilyHomes(), re.getStudioHomes(), re.familyPressure(), re.studioPressure(),
                g.getPopulationManager().getTotalJobs(), g.getPopulationManager().getUnemploymentRate(),
                re.getHouseholdCount());
    }

    /** The landlords' month, one row of <prefix>-house.csv (0.7.11). */
    static void traceHouse(Game g) {
        if (traceHouse == null) return;
        ham.citybuildersim.sectors.RealEstate re = g.getSectors().realEstate();
        BusinessDebtManager cr = g.getEconomyManager().getBusinessDebtManager();
        String key = re.key();
        long jobs = g.getPopulationManager().getTotalJobs();
        long capacity = g.getHouseholdCapacity();
        double mortgages = cr.getMortgagePrincipal(key);
        String why = String.valueOf(g.getLastInvestment(key)).replace(',', ';');
        traceHouse.printf(java.util.Locale.ROOT,
                "%d,%d,%d,%.1f,%d,%d,%.4f,%.4f,%.5f,%.5f,%.5f,%.5f,%.5f,%.3f,%.3f,%.3f,%d,%.3f,%.3f,"
                        + "%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.3f,%.3f,%.3f,%d,%d,%.3f,%.3f,%.3f,%s%n",
                g.getMonth(), g.getPopulationManager().getPopulation(), re.getHomes(), re.getHouseholdCount(),
                capacity, jobs, jobs * 2.25 / Math.max(1, capacity), re.housingPressure(),
                re.getRentPrice(), re.getStudioRentPrice(), re.rentRequired(true), re.rentRequired(false),
                re.rentBreakEven(), g.getEconomyManager().getSectorCash(key), cr.getAssets(key),
                cr.getPrincipal(key), cr.getMortgageCount(key), mortgages, cr.getPrincipal(key) - mortgages,
                cr.getMortgageRate(key), cr.getInsuredMortgageRate(), cr.getPrimeRate(), cr.getRate(key),
                g.getDebtManager().getPolicyRate(), g.getPriceIndex().getIndex(), g.getPriceIndex().inflation(),
                g.getPopulationManager().getUnemploymentRate(),
                cr.getPremiumsThisMonth(), cr.getInsuredWrittenOffThisMonth(), cr.getMortgageRepaidThisMonth(key),
                cr.getRenewedThisMonth(), cr.getFallenDueThisMonth(), re.statement().netIncome,
                g.getEconomyManager().getEquityRaised(key), g.getEconomyManager().getDividendsPaid(key), why);
    }

    static synchronized void traceClose() {
        if (traceMonths != null) traceMonths.close();
        if (traceBorrow != null) traceBorrow.close();
        if (traceHouse != null) traceHouse.close();
        if (tracePop != null) tracePop.close();
        if (traceBank != null) traceBank.close();
        if (traceBonds != null) traceBonds.close();
        if (traceBuild != null) traceBuild.close();
        if (traceLabour != null) traceLabour.close();
        traceMonths = traceBorrow = traceHouse = tracePop = traceBank = traceBonds = traceBuild = traceLabour = null;
    }

    /**
     * -Dplaytest.autopilot=true (0.7.0): the rule holds the dial from founding,
     * through the game's own autopilot (DebtManager), and the advisor keeps
     * its hands off it. Off by default, which is the run as it always was. A
     * held rate (-Dplaytest.policyRate) takes the dial back from the rule the
     * month it starts holding, as a player's hand would.
     */
    static final boolean AUTOPILOT = Boolean.getBoolean("playtest.autopilot");

    /**
     * -Dplaytest.rollover=MANUAL|SAME_STRUCTURE|TWELVE_MONTH_BILL (0.7.13):
     * the treasury's rollover for the run (Rollover). SAME_STRUCTURE when
     * unset - what a new game founds with, so a run is played the way a
     * player now plays; MANUAL is every run before 0.7.13, to the byte.
     */
    static final Rollover.Mode ROLLOVER = Rollover.Mode.valueOf(
            System.getProperty("playtest.rollover", "SAME_STRUCTURE").trim().toUpperCase(java.util.Locale.ROOT));

    /* =====================================================================
       THE CITY'S FUND AND THE BANK'S RESCUE, STATED (0.7.14)

       -Dplaytest.rescue=AUTO|BUTTON, AUTO when unset - what a new game has
       (Game.newGame()): the city resolves a failed bank for its shares the
       month it fails. BUTTON presses the Bank tab's button the month the
       bank is found frozen, which is the same resolution a month's lag
       later. Either replaces the loop this harness ran until 0.7.14, which
       every month put min(needed, a quarter of the treasury) into a failed
       bank or a standing one under its minimum as a gift: the quarter cap
       is gone with the gift - a resolution is a promise, paid whole and
       advanced by the central bank past the treasury's cash, and the
       preferred is bought whole, the funding page's bond raising what the
       treasury lacks first.

       -Dplaytest.preferred=ACCEPT|DECLINE, ACCEPT when unset: what the
       player answers when a standing bank under its minimum asks the city to
       buy its preferred.

       -Dplaytest.fund=<dial>, 0 when unset: the fund's dial as a share of
       the year's surplus - 1 for 100%, 3 for 300% ("100%" reads 1).
       ===================================================================== */

    /** The rescue setting under -Dplaytest.rescue, AUTO when unset. */
    static final boolean RESCUE_AUTO = !"BUTTON".equalsIgnoreCase(System.getProperty("playtest.rescue", "AUTO").trim());

    /** The answer to the bank's offer under -Dplaytest.preferred, ACCEPT when unset. */
    static final boolean PREFERRED_ACCEPT = !"DECLINE".equalsIgnoreCase(System.getProperty("playtest.preferred", "ACCEPT").trim());

    /** The fund's dial under -Dplaytest.fund, 0 when unset. */
    static final double FUND_DIAL = fundDial(System.getProperty("playtest.fund", "0"));

    static double fundDial(String s) {
        String t = s.trim();
        if (t.endsWith("%")) return Double.parseDouble(t.substring(0, t.length() - 1).trim()) / 100;
        return Double.parseDouble(t);
    }

    /* =====================================================================
       WAGES AGAINST THE INDEX (2026-09-21)

       A measurement on 2026-09-15 put the wage index, costOfLiving, at 2.648
       against a price index of 1.993 on a played city: wages a third above
       the level they are written to chase, a free real-wage gain compounding
       for the life of a run. -Dplaytest.wages=true prints, at every
       checkpoint and at the end, the three figures that say whether that is
       still true: costOfLiving, the price index, and the level the two-year
       lag IMPLIES - this harness's own copy of LabourMarket's recurrence as
       it stood until 0.7.42, walked a DRIFT_PER_MONTH of the way each month
       toward the index the month was handed. A second indexation, a jump on
       a reform or a load, or any path that moves the wage index without the
       lag, shows as the two drifting apart. Unset, nothing is computed or
       printed. SINCE 0.7.42 THE COPY IS THE CHASE ALONE: once the basket is
       based a wage takes half its indexing from expected inflation and a
       forty-eighth of the gap (LabourMarket, HALF WHAT PEOPLE EXPECT, HALF
       THE CHASE), which this copy does not, so the two part by that too -
       LabourCheck.wagesAgainstTheIndex() holds the new recurrence.
       ===================================================================== */

    /** -Dplaytest.wages=true: the wage index, the price index and the lag-implied level at each checkpoint. */
    static final boolean WAGES = Boolean.getBoolean("playtest.wages");

    /* =====================================================================
       THE CITY'S OWN PAPER, FOR THE ENSEMBLE (2026-09-21)

       The fix that makes the bank pay for the city's paper was measured on
       the eight seeds and moved none of them, because none of them ever
       owes a dollar at home: the advisor borrows only where the money is
       cheaper, and the world's is - one dollar bond around month 1,092 in
       six seeds of eight (3 and 5 never borrow), and an attentive player
       $2.4B of them. A mechanic the fixture never touches is a mechanic no
       run has tested (K1). -Dplaytest.borrowAtHome=true sends the same
       borrowing, at the same moments and for the same money, to the city's
       own term bond instead - the naive player who never looked at the
       dollar rate - so the bank has paper to pay for. Unset, the run is the
       run it always was.
       ===================================================================== */

    /** -Dplaytest.borrowAtHome=true: the advisor's borrowing goes to the city's own term bonds, never abroad. */
    static final boolean BORROW_AT_HOME = Boolean.getBoolean("playtest.borrowAtHome");

    /* =====================================================================
       THE HOLDINGS DIAL, HELD (0.7.1)

       Jerus: "the central bank would buy gbonds or sell gbonds from thin air
       ... like QE and QT". -Dplaytest.qeShare=<share> sets the central bank's
       holdings dial - the share of the city's term paper it aims to hold,
       CentralBank.getTargetShare() - from the month the policy flag holds
       its rate, the first past ForeignAccounts.SETTLING_MONTHS, to the end of
       the run: set, and left. It only has paper to buy in a run that issues
       it at home (-Dplaytest.borrowAtHome). Unset, the dial stays at zero and
       the run is the run it always was.
       ===================================================================== */

    /** The holdings dial under -Dplaytest.qeShare, or null when nobody sets it. */
    static final Double QE_SHARE = System.getProperty("playtest.qeShare") == null
            ? null : Double.valueOf(System.getProperty("playtest.qeShare"));

    /* =====================================================================
       THE CEILING, SET (0.7.2)

       Batch B: "the ceiling is small in a small city" - six months of
       revenue binds within months of first drawing. The ceiling is the
       player's dial now (CentralBank.setAdvancesCeilingMonths()), and
       -Dplaytest.advancesMonths=<n> sets it at founding and leaves it, so the
       broke seeds can be run at 12 and 24. Unset, it is
       CentralBank.DEFAULT_ADVANCES_MONTHS and the run is the run it always was.
       ===================================================================== */

    /** The advances ceiling under -Dplaytest.advancesMonths, in months of revenue, or null for the default. */
    static final Double ADVANCES_MONTHS = System.getProperty("playtest.advancesMonths") == null
            ? null : Double.valueOf(System.getProperty("playtest.advancesMonths"));

    /* =====================================================================
       THE TARGET, SET (0.7.4)

       The inflation target is the player's dial since 0.7.4
       (DebtManager.setInflationTarget()), and -Dplaytest.inflationTarget=
       <fraction> sets it at founding and leaves it, beside the held rate and
       the autopilot, so an ensemble can hold it: the advisor's rule and the
       autopilot's both aim at it. Unset, it is
       DebtManager.DEFAULT_INFLATION_TARGET and the run is the run it always
       was, to the byte.
       ===================================================================== */

    /**
     * THE STRICTER MONEY GATE, FOR THE COUNTERFACTUAL (0.7.82, batch O5):
     * -Dplaytest.moneyGate=whole has the refiners' spread planner test every
     * order on its whole cost, whoever pays (SpreadPlanner.ON_ITS_WHOLE_COST,
     * runs/spec-materials.md's item A, which Jerus has not answered), where
     * the rule in force tests what the order borrows. Unset, a no-op.
     */
    static final boolean MONEY_GATE_WHOLE = "whole".equalsIgnoreCase(System.getProperty("playtest.moneyGate", "").trim());

    /** -Dplaytest.refinery=true (0.7.82): the refiners' planner yearly - its outlook and its best candidates under both money gates (refineryLine()). */
    static final boolean REFINERY_TRACE = Boolean.getBoolean("playtest.refinery");

    /** The inflation target under -Dplaytest.inflationTarget, a fraction a year, or null for the default. */
    static final Double INFLATION_TARGET = System.getProperty("playtest.inflationTarget") == null
            ? null : Double.valueOf(System.getProperty("playtest.inflationTarget"));

    /*
     * THE DEFENCE OVER THE RUN (0.7.2): the dollars the central bank sold
     * defending the currency, in total and in its biggest month, and how many
     * months it sold anything. The vault at the end says what is left; these
     * say whether it was ever used, which the vault's endpoint cannot - a
     * vault spent and rebuilt by the advisor reads like one never touched.
     */
    static int monthsDefended, peakDefenceMonth;
    static double defendedUsdRun, peakDefenceUsd;
    /**
     * THE LAND OFFICE OVER THE RUN (0.7.6): months the city bought land abroad
     * and what it cost in local money at the day's rates - against the dollars
     * ForeignAccounts counts, which is what the same land would have cost at
     * the founding rate of 1.00. The difference is what the currency did to
     * the price of land.
     */
    static int monthsLandBought;
    static double landLocalRun;
    /** The vault's life: its lowest reading and when, and the first month the founders' dollars were half gone and all but gone - the question the defence's dials are asked. */
    static int vaultLowMonth, halfGoneMonth, emptyMonth;
    static double vaultLow = Double.MAX_VALUE;

    /** The lag-implied wage index, walked month by month beside the game's own; NaN until the first month. */
    static double lagImplied = Double.NaN;

    /** One month of the lag, on the index that month was handed. */
    static void walkLag(Game g, double indexHanded) {
        if (Double.isNaN(lagImplied)) return;
        double target = 1 + (indexHanded - 1) * LabourMarket.COST_OF_LIVING_PASS_THROUGH;
        lagImplied += (target - lagImplied) * LabourMarket.DRIFT_PER_MONTH;
    }

    /** The three figures on one line, for a checkpoint or the end. */
    static String wageEra(Game g) {
        LabourMarket wages = g.getLabourMarket();
        double index = g.getPriceIndex().getIndex();
        return String.format("       wages  costOfLiving %.4f  price index %.4f  lag-implied %.4f"
                        + " (off by %+.2e)  target %.4f  wages/index %.3f",
                wages.getCostOfLiving(), index, lagImplied,
                wages.getCostOfLiving() - lagImplied, wages.getLivingTarget(),
                index > 0 ? wages.getCostOfLiving() / index : 0);
    }

    /**
     * Under the schools flag: the basic ladder, then a college, then a
     * university, each when the city is big enough to carry it, and more of
     * each as it grows - asked at every stop, so the schools arrive with the
     * people rather than a quarter-century late.
     *
     * PROPORTIONATE, BECAUSE THE FIRST DRAFT WAS NOT. It put one of each up
     * with the founding, and a University is $210M, three hundred and
     * sixty-one posts and $620k a month of upkeep on a town of three hundred
     * people making $1.8M a month: both control seeds went $180M-$290M
     * overdrawn by month 263 and $3-4B by month 600, and seed 1's overdraft
     * compounded without bound - $1.4T at month 801, $1,378T at 1,033, and
     * a NaN at 1,632 - which is a finding about the treasury's floor, filed,
     * and not what this flag is for. The thresholds below are what a city
     * can pay for: the ladder from a thousand people, a high school from two
     * thousand, a college from five, a university from ten, and one more of
     * each per the people it serves after that.
     */
    static void ensureSchools(Game g) {
        if (!SCHOOLS) return;
        long people = g.getPopulationManager().getPopulation();
        ensure(g, "Elementary School", people < 1_000 ? 0 : (int) (1 + people / 10_000));
        ensure(g, "Middle School", people < 1_000 ? 0 : (int) (1 + people / 10_000));
        ensure(g, "High School", people < 2_000 ? 0 : (int) (1 + people / 15_000));
        ensure(g, "Community College", people < 5_000 ? 0 : (int) (1 + people / 40_000));
        ensure(g, "University", people < 10_000 ? 0 : (int) (1 + people / 40_000));
    }

    /**
     * Under the childcare flag (0.7.71, batch N2): when NEEDS YOU lists
     * childcare, the build advice's own card for it - the size that fits the
     * need (BuildAdvice.perPlaceNeeded()) and the count that keeps it ahead
     * at its projection, as "Build all three" would place it - through
     * build(), which buys the ground and borrows as for any order. Nothing
     * while what is on site keeps it ahead (suggestFor() says nothing).
     *
     * A FLAG, AS THE SCHOOLS ARE, AND OFF BY DEFAULT. The advisor ranks
     * constraints by the output each costs, and childcare costs none: what
     * it moves is births and the young's deaths (Healthcare.CHILDCARE_SWING,
     * CHILDCARE_BIRTH_BONUS). So the default player has never built any,
     * and its city ran on the founding's 201 places to the end (coverage
     * 3.3% at m1,000, 0.3% at m4,000). With this flag on, measured
     * (runs/fixN2-notes.md): at 0.7.70's sizes the city held 262 Home
     * Daycares at m1,000 and 3,134 childcare buildings against 2,123 homes at
     * m4,000 (1.48 a home building, Jerus's "5k daycares and 2k residential
     * buildings"); at 0.7.71's, 35 and 247 (0.10), 235 of them Large - and
     * the city 309,000 at m4,000 where the default's is 477,000, a choice
     * about the default player that is Jerus's, so the default stays.
     */
    static void childcareWhenNeeded(Game g) {
        if (CHILDCARE) orderChildcare(g);
    }

    /** The rule itself, flag or none (ChildcareCheck plays it): the advice's childcare card when NEEDS YOU lists childcare, placed; that suggestion, or null when nothing was ordered. */
    static BuildAdvice.Suggestion orderChildcare(Game g) {
        BuildAdvice.Measure m = BuildAdvice.Measure.care(CareType.CHILDCARE);
        java.util.List<CityNeeds.Need> all = CityNeeds.measure(g, CityNeeds.PLAIN);
        CityNeeds.Need need = BuildAdvice.needFor(all, m);
        if (need == null || !CityNeeds.biting(all).contains(need)) return null;
        BuildAdvice.Suggestion s = BuildAdvice.suggestFor(g, need, m, g.getCash(),
                g.getLandManager().getAvailableSqFt());
        if (s == null || !build(g, s.template().getName(), s.count())) return null;
        childcareOrders++;
        childcareBuilt.merge(s.template().getName(), s.count(), Integer::sum);
        return s;
    }

    /**
     * Under the autobuild flag (0.7.73, N4): the ground automatic building
     * said it was held back for, bought as the inbox's notice asks ("Buy land
     * at the land office") - each such order's ground less what is free, by
     * the build shortcut's rule that build() buys with (Game.bestOffer(
     * LandNeed.shortfall())). Until 0.7.77 automatic building bought no land
     * itself and a player read the notice and bought it; since then it buys
     * its orders' own bare ground (AutoBuilder, THE GROUND ITS ORDERS NEED),
     * and holds one for ground only where no bare offer is to be had - which
     * the player, reading the notice, still buys by the shortcut's rule.
     */
    static void groundForAutoBuild(Game g) {
        double wanted = 0;
        for (AutoBuilder.Step st : g.getAutoBuilder().steps()) {
            if (st.outcome() != AutoBuilder.Outcome.HELD || st.cut() != AutoBuilder.Cut.GROUND) continue;
            BuildingsTemplate t = template(g, st.building());
            if (t != null) wanted += t.getLandSqFt() * (double) st.wanted();
        }
        int guard = 0;
        while (wanted > g.getLandManager().getAvailableSqFt() && guard++ < 60) {
            LandParcel covers = g.bestOffer(Game.LandNeed.shortfall(wanted - g.getLandManager().getAvailableSqFt()));
            if (covers == null || !g.buyLandParcel(covers.getId())) break;
            autoBuildGroundBought++;
        }
    }

    /** Offers bought for automatic building's ground (the autobuild flag). */
    static int autoBuildGroundBought = 0;

    /** What the childcare flag has ordered: orders, and buildings by name. */
    static int childcareOrders = 0;
    static final java.util.Map<String, Integer> childcareBuilt = new java.util.TreeMap<>();

    /** What the flag has ordered of each school, so one under construction is not ordered twice. */
    static final java.util.Map<String, Integer> schoolsOrdered = new java.util.HashMap<>();

    static void ensure(Game g, String name, int want) {
        int have = Math.max(qty(g, name), schoolsOrdered.getOrDefault(name, 0));
        if (have < want && build(g, name, want - have)) schoolsOrdered.put(name, want);
    }

    /** ...and how many things it will fix when it does look. */
    static int movesPerLook() { return ATTENTIVE ? 8 : 3; }

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
        long population = p.getPopulation();
        double perHead = population > 0 ? gdp / population : 0;

        /* ================================================================
           THE TWO THINGS THAT ARE NOT PURCHASES, done first and for free.

           Neither costs a move, because neither is a month's work. Setting a
           subsidy and topping up a reserve are decisions you make while doing
           something else - and the construction subsidy in particular has to
           be set BEFORE the depot rule can ever be reached, which is the
           ordering fault that cost a whole playtest the last time this file
           was rewritten.
           ================================================================ */

        if (!g.isAutoSubsidised(Sectors.CONSTRUCTION)) {
            g.setAutoSubsidised(Sectors.CONSTRUCTION, true);
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
        // ...unless -Dplaytest.policyRate holds it - see THE RATE, HELD - or
        // the rule has the dial (-Dplaytest.autopilot, see AUTOPILOT).
        if (g.getPriceIndex().hasRate() && !holdsPolicyRate(g)
                && !g.getDebtManager().isAutopilot()) {
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

        /*
         * GROUND KEPT AHEAD (0.7.58, batch J1d), the way the build advice
         * keeps slack - Jerus, of the advice: "it doesnt build any slack".
         * When a look finds more of the city's dry ground built on than
         * groundAheadUtilisation() allows - the ground the city will be using
         * BuildAdvice.HORIZON months out, by the businesses' growthFactor(),
         * with BuildAdvice.SLACK past it - the player buys the best value
         * (Game.bestOffer(LandNeed.room())) until it does not, spending no
         * more than GROUND_AHEAD_CASH_SHARE of the cash. A rule, as the war
         * chest is, not a scored move; the room-to-grow move below is still
         * scored when the ground is past the same line (roomToGrow(); past .85
         * until 0.7.67, batch M3b).
         *
         * WHY: that move buys one offer a move, three moves a look, and a look
         * comes six to a hundred and twenty months apart. On the world's land
         * (0.7.57 to 0.7.66) an offer was a multiple of 1% of the city, where
         * 0.7.55's parcels had a floor of a block more for every forty owned,
         * and the cheapest a square foot is the oldest, listed when the city
         * was smaller: in months 300-1,000 a look's offers added 8.7% to the
         * city's ground where 0.7.55's added 17.1%, and from month 1,000 to
         * 3,000 the city was half 0.7.55's size (19,846 and 59,965 people on
         * average against 41,222 and 126,562). Keeping its ground ahead, it is
         * 43,217 and 119,800 (runs/fixJ1d-notes.md).
         */
        keepGroundAhead(g);
        // ...and the ground automatic building was held back for (0.7.73, -Dplaytest.autobuild).
        if (AUTOBUILD) groundForAutoBuild(g);

        // Iron, a whole field at a time (0.7.64, batch L): a rule, as the
        // ground kept ahead is - see ironWhenNeeded().
        ironWhenNeeded(g);

        // ...and a sea terminal when a kind's trade would fill most of a berth (0.7.86, batch O9): portsWhenNeeded().
        portsWhenNeeded(g);

        /* --- room to grow. Not a building, and it gates every building. --- */
        double roomLost = roomToGrow(g);
        if (roomLost > 0) {
            LandParcel room = land.getMarket().bestValue();
            if (room != null) {
                moves.add(new Move("bought room to grow", roomLost, () -> {
                    boolean bought = g.buyLandParcel(room.getId());
                    if (bought) roomMovesBought++;
                    return bought;
                }));
            }
        }

        // ...unless automatic building has the city's works (0.7.73, -Dplaytest.autobuild).
        if (!AUTOBUILD) {
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
            addWater(moves, g, gdp * (1 - g.getWaterRatio()));

            /*
             * ROADS, AND ALL THREE OF THEM. The old rule only ever built Paved
             * Roads and stopped at forty; three_roads.md costed a Gravel Road, a
             * Paved Road and an Elevated Highway so that each is the cheapest per
             * trip across a band of land prices, and the advisor never used two of
             * them. Offered as three moves now, so the price of land picks.
             */
            /*
             * ...AND THE THREE THAT ARE NOT ROADS AT ALL (2026-09-16).
             *
             * A Bus Network, a Light Rail Line and a Metro Line have been in the
             * catalogue since transit was built and this advisor had never once
             * bought one - so four thousand months of playtest measured a city
             * whose only answer to congestion was tarmac. That did not matter while
             * a commuter cost the road one trip and nothing could change it. Cars
             * changed it, and the eight seeds said so in one number: population
             * 191,000 -> 132,000, with the advisor doubling its road building and
             * still finishing at 66% throughput on land that was 90% used.
             *
             * A city that cannot build its way out with roads and will not build a
             * bus is not a pessimistic measurement, it is the wrong one. A player
             * would build the bus.
             *
             * WHAT DECIDES BETWEEN THEM IS THE CATALOGUE, not an ordering written
             * here, which is the same promise the three roads above are supposed to
             * keep. Every candidate is costed in TRIPS TAKEN OFF THE ROAD PER
             * DOLLAR and they are offered best-first:
             *
             *     Gravel Road      900 trips / $2,326k   = 0.387
             *     Bus Network    2,500 riders / $12,000k = 0.208 x the car factor
             *
             * ...so a bus loses outright in a city where nobody drives, and wins
             * once about half the households own a car, because a rider taken off
             * the street is worth whatever a driver was costing it. Nobody chose
             * that crossover; it falls out of two prices that were set months
             * apart for other reasons, and it is exactly where it should be.
             *
             * AND A BUS RELIEVES NOTHING IN A CITY THAT IS ALREADY FULL OF BUSES.
             * The room below is the real one - the transit share ceiling, and the
             * road that has to exist underneath it - so the advisor stops buying
             * them when they stop working rather than when a cap says to.
             */
            double roadGain = gdp * (1 - g.getRoadRatio());
            addRoadThrottle(moves, g, roadGain);

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

            /*
             * --- crime, and the police and the cells for it (2026-09-11) ---
             *
             * What crime costs a month: what is stolen, the work the injured do not
             * do, and the output of the people who do not come - the size the
             * crime pull takes off the city. A police station is bought when what
             * it would take off that is more than the station costs to run, which
             * is the rule a player would use; and the cells when the police are
             * catching people with nowhere to put them, sized to what they catch.
             */
            Crime crime = g.getCrime();
            if (crime.getCrimes() > 0 && crime.getPopulation() > 0) {
                double crimeCost = crime.getStolen() + gdp * crime.getInjuredShare()
                        + gdp * (1 - Migration.crimePull(crime.getRateVsCanada()));
                for (String police : new String[] {"Police Station", "Police Headquarters"}) {
                    BuildingsTemplate t = template(g, police);
                    if (t == null || crime.getCoverage() >= 1) continue;
                    double after = Crime.coverageOf(crime.getOfficers() + t.getCapacity(),
                            crime.getPopulation());
                    double saved = crimeCost * (1 - crime.crimesAt(after) / crime.getCrimes());
                    if (saved > runningCost(g, t)) {
                        // As many as full coverage needs and no more: past it, a
                        // station takes nothing off. Ordered by the gap in
                        // officers, not by the share of output crime is costing -
                        // which ordered six stations for a city of sixteen
                        // thousand that needed one.
                        double short_ = crime.getPopulation() * Crime.FULL_OFFICERS_PER_100K / 100_000.0
                                - crime.getOfficers();
                        int needed = (int) Math.max(1, Math.ceil(short_ / t.getCapacity()));
                        addThrottle(moves, g, police, "police", saved, (needed - .5) / 25.0);
                    }
                }
                // A quarter of a jail's worth of people with nowhere to go, before a
                // $360M building: a town that catches one a month sends them to the county.
                double cellsWanted = crime.getNotHeld() * Crime.SENTENCE_MONTHS;
                if (cellsWanted >= 75) {
                    String prison = cellsWanted >= 300 ? "Penitentiary" : "Jail";
                    addThrottle(moves, g, prison, "prison", crimeCost);
                }
            }
        }

        /* --- somewhere to live, and somewhere to shop --- */

        /*
         * TWO SHORTAGES, AND THIS ADVISOR COULD ONLY SEE ONE OF THEM.
         *
         * getTotalHouseCapacity() is BEDS. Since homes got SIZES (2026-09-07) a
         * city can have a bed for everybody and nowhere for a family to live -
         * FamilyModel's own comment says so in as many words: "A city with a
         * studio for every household is at one-home-each by the count below and
         * returns 1 - room to spare - while every family in it has nowhere to
         * go." The advisor never caught up, and so it read one number that
         * cannot answer the question.
         *
         * MEASURED, in the transport ensemble: a city of 42,548 people with
         * 46,856 beds, 14,348 doors and 14,621 households. homesShort was
         * MINUS four thousand, so housing was never even considered - while a
         * thousand households had nowhere and Real Estate, deep in debt after
         * the bust, could not finance a block either. Nobody built anything for
         * a hundred and forty-one months and the run flagged people sleeping
         * outside.
         *
         * A player looking at "1,137 households with nowhere" builds houses.
         * FamilyModel already knows that number as a fact rather than an
         * estimate: whoever BOTH valves failed to place.
         *
         * THE DOUBLED-UP ARE DELIBERATELY NOT IN IT, and the first draft had
         * them and was wrong. Sharing a flat is the second valve WORKING, not
         * failing, and it is positive in any young city with people arriving -
         * so an advisor that read it built housing every month of every run,
         * and FoodProcessingCheck's fixture city went from month 98 to month
         * 185 with ten times the price level. The failure to react to is the
         * one the run flags: somebody with nowhere at all.
         */
        double homesShort = population - b.getTotalHouseCapacity();
        FamilyModel fam = g.getFamilies();
        double doorsShort = fam.getStillUnplaced();
        if (homesShort > -4 || doorsShort > 0) {
            /*
             * Priced on the output the people it houses would produce, less a
             * discount because they arrive over the years rather than next
             * month. Growth is worth less per dollar than restoration, and it
             * should be - a city that builds houses while its power is out has
             * simply moved the shortage.
             *
             * A DOOR SHORTAGE IS COUNTED IN HOUSEHOLDS, so it is put on the
             * same footing as the bed shortage by the city's own average household
             * size before the two are compared.
             */
            double housingGain = perHead * Math.max(20,
                    Math.max(homesShort, doorsShort * Math.max(1, fam.averageHouseholdSize()))) * GROWTH_DISCOUNT;
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
         * NO MATERIALS PLANT FROM THE TREASURY (2026-09-11). The advisor used
         * to buy one whenever the yard ran short of a stock per resident,
         * which was the city stocking its own works yard. The plant is the
         * seventh sector's now, and since the crews draw material as they
         * build the yard is short in every month anything is being built -
         * so the rule bought four plants a run for a private sector that
         * was building its own, and the sector, handed plant it had not
         * planned for, defaulted on the ones it had. A player may still
         * gift one from the build screen; the advisor lets the market do it.
         */

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
            addThrottle(moves, g, "Bakery", "food plant",
                    gdp * .05);
        }

        /*
         * --- ore, which is the one thing the private sector cannot buy ---
         * A rule since 0.7.64, called with the ground kept ahead above
         * (ironWhenNeeded()). Until then a move here, "bought a deposit",
         * bought the richest offer in iron (sites a dollar) out of the cash
         * whenever every site the city owned had a mine on it or ordered,
         * whether or not a mine would pay, weighed at gdp x .03 against the
         * look's other moves.
         */
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
        if (land.hasUnminedDeposit(g.minesCommitted()) && wouldPay(g, "Iron Mine", Sectors.MINING)) {
            addThrottle(moves, g, "Iron Mine", "mine", gdp * .05);
        }

        /*
         * --- and oil, when the city's fuel is a bill abroad (0.7.62, batch K;
         * spec-land 3's K entry) ---
         *
         * The richest offer in oil, sites a dollar, while the fuel the world
         * sold the city last month - its drivers' and its railway's - passes
         * OIL_FUEL_IMPORTS_SHARE of a month's GDP and every oil site it owns
         * has a well on it or ordered. The wells and the refinery are left to
         * the investors (spec star 13), so what follows a purchase is the
         * private response this tests. The deposit's weight, as iron's.
         */
        if (fuelBoughtAbroad(g) > OIL_FUEL_IMPORTS_SHARE * gdp && land.getOilSites() <= g.wellsCommitted()) {
            LandParcel oil = land.getMarket().richest(Resource.OIL);
            if (oil != null) {
                moves.add(new Move("bought oil", gdp * .03, () -> {
                    boolean bought = g.buyLandParcel(oil.getId());
                    if (bought) oilBought++;
                    return bought;
                }));
            }
        }

        /* --- and jobs, when there are people with nothing to do --- */
        double idle = p.getWorkforce() - p.getTotalJobs();
        if (idle > 0) {
            double jobGain = perHead * idle * GROWTH_DISCOUNT;
            // Same test as the mine: 72 foundries went up in one run for the
            // jobs alone, into a market that could not pay for the steel.
            if (wouldPay(g, "Steel Foundry", Sectors.HEAVY_INDUSTRY)) {
                addThrottle(moves, g, "Steel Foundry", "foundry", jobGain);
            }
            addThrottle(moves, g, "Industrial Bakery", "mill", jobGain);
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

    /*
     * IRON, A WHOLE FIELD AT A TIME (0.7.64, batch L). Jerus, 2026-10-07:
     * "Yes whole iron fields as one offer, yes that means significant
     * investment." An offer holds every field centred on its ground whole
     * (CityLand), so the default world's founding field is 35 sites and
     * 449 Mt in one offer of about US$180M, and its nearest one-site fields
     * about US$5.1M of ore each with their ground. The player's rule, a
     * sensible player's:
     *
     *   - WHEN IT NEEDS IRON: an Iron Mine would pay on the private sector's
     *     screen (wouldPay()) - the test it already reads before it sinks a
     *     mine; a field nobody would work is not worth a field's price;
     *   - AND OWNS NO UNWORKED SITE: every iron site it owns has a mine on it
     *     or ordered (Game.minesCommitted()), the old move's test;
     *   - IT BUYS THE CHEAPEST WHOLE-FIELD OFFER STANDING
     *     (LandMarket.cheapestWith()), whatever its size;
     *   - WHEN THE MINES IT WILL BUILD PAY THE FIELD BACK (0.7.67, batch M3b):
     *     the mines the field would carry earn in a month at least the level
     *     payment that repays its price over BUILD_BOND_YEARS, the funding
     *     page's own term, at the rate the market quotes for that much money
     *     (fieldPayment()). The mines are the ones the city could staff
     *     (Sector.staffableCount(), every planner's test before it builds
     *     posts), up to the field's sites, each that would pay on the mining
     *     sector's own screen with the ones before it lifting: the mills'
     *     projected demand first, the rest at the export price
     *     (fieldEarnings()). Jerus, of the build advice: "take into account
     *     the same as businesses do, aka a projection". Not paying back, it
     *     asks again at the next look, counted;
     *   - ONCE IT CAN PAY FOR IT: out of the cash when the cash covers it, as
     *     the land office's Buy does; short, with the land office's funding
     *     page's bond - BUILD_BOND_YEARS, sized to Game.landCashGap() - when
     *     the player's own test for borrowing passes (canService(): the
     *     interest on all it owes with the bond in it within
     *     DEBT_SERVICE_LIMIT of the tax take, the test every building it
     *     borrows for passes). Neither, it asks again at the next look: it
     *     waits only until the city can carry the cheapest field standing,
     *     and never for a cheaper one to be listed.
     *
     * A rule, as the ground kept ahead is, not a scored move: a look carries
     * out its one weightiest move that succeeds, and when the city buys its
     * iron should not hang on what else the look found.
     *
     * WHY THE PAYBACK (M3b): wouldPay() asks of one more mine, and the
     * borrowing test of the city's tax take; neither asks whether the field
     * is worth its price. On the block grid the founding field's offer can
     * be the cheapest holding iron, and the rule bought all 35 sites for
     * what the borrowing test would carry: the 0.7.67 playtest at month 356
     * with 2,759 people, US$206.2M on a D$251.6M bond, worked by two mines
     * at month 608 and three at month 759; with the room move priced from
     * the line, at month 123 with 666 people on a D$271.4M bond, the tax
     * take the test read that month raised sevenfold (D$5.1M against
     * D$0.74M) by the building the city had borrowed for the month before.
     * On 0.7.67's ensemble seeds 0 and 4 took it on the bond with 869 and
     * 2,273 people (runs/fixM3b-notes.md).
     */
    static void ironWhenNeeded(Game g) {
        LandManager land = g.getLandManager();
        if (land.getIronDeposits() > g.minesCommitted()) return;
        if (!wouldPay(g, "Iron Mine", Sectors.MINING)) return;
        if (firstIronNeedMonth == 0) firstIronNeedMonth = g.getMonth();
        LandParcel field = land.getMarket().cheapestWith(Resource.IRON);
        if (field == null) {
            ironNoneListed++;
            return;
        }
        if (!(fieldEarnings(g, field.getDeposits())[1] >= fieldPayment(g, field))) {
            ironDoesNotPay++;
            return;
        }
        int sitesBefore = land.getIronDeposits();
        String paid = buyWhole(g, field, "an iron field");
        if (paid == null) {
            ironCouldNotPay++;
            return;
        }
        ironFieldsBought++;
        if (paid.equals("bond")) ironFieldsOnBond++;
        if (firstIronMonth == 0) {
            firstIronMonth = g.getMonth();
            firstIron = String.format("m%d: %s, %d site(s), %,.1f Mt, US$%,.0fk, %s (the city %,d people)",
                    g.getMonth(), field.where(), land.getIronDeposits() - sitesBefore, field.getIronTonnes() / 1e6,
                    field.getPriceUsd(), paid.equals("bond") ? "the funding page's bond for the rest" : "out of the cash",
                    g.getPopulationManager().getPopulation());
        }
    }

    /**
     * Buys an offer as the land office does (0.7.64): out of the cash when it
     * covers it - "cash" - or, converting and short, the funding page's
     * BUILD_BOND_YEARS bond for the gap when canService() carries it, then
     * the offer - "bond"; null when nothing was bought. Paying from the vault
     * is not this rule's (the playtest converts): null.
     */
    static String buyWhole(Game g, LandParcel p, String purpose) {
        java.util.List<Integer> ids = java.util.List.of(p.getId());
        if (!g.landNeedsFunding(ids)) return g.buyLandParcel(p.getId()) ? "cash" : null;
        if (g.isLandPaidFromVault()) return null;
        double gap = g.landCashGap(ids);
        if (!canService(g, gap)) return null;
        g.handleLongBondForCash(gap, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);
        notePaper(g, purpose + " (the land office's funding page)");   // the trace; nothing else reads it
        if (g.landNeedsFunding(ids)) return null;
        return g.buyLandParcel(p.getId()) ? "bond" : null;
    }

    /** Over the run (0.7.64): iron offers bought, how many on the funding page's bond, the first look that needed iron, the first purchase's month and words, and the looks that needed iron and could not pay for the cheapest field or found none listed. */
    static int ironFieldsBought, ironFieldsOnBond, firstIronNeedMonth, firstIronMonth, ironCouldNotPay, ironNoneListed;
    static String firstIron = "never";

    /** Over the run (0.7.67, M3b): the looks that needed iron whose cheapest field would not pay itself back (fieldEarnings() under fieldPayment()). */
    static int ironDoesNotPay;

    /**
     * What a field of `sites` iron sites would earn the city's mines a month
     * (0.7.67, M3b): {mines, their monthly profit}. The mines are no more
     * than the sites and than the mining sector could staff
     * (Sector.staffableCount()), and each counted would pay on the sector's
     * own screen (BusinessInvestment.estimatedMonthlyProfit()) with the ones
     * before it lifting: the first is that figure, and each after it sells
     * at home what the planners' forecast of the mills' demand leaves of the
     * room (BusinessInvestment.forecast(), less the mines standing and on
     * site) and the rest at the export price, as estimatedMakerProfit()
     * splits it. The first that would not pay ends the count.
     */
    static double[] fieldEarnings(Game g, int sites) {
        BuildingsTemplate mine = template(g, "Iron Mine");
        Sector mining = g.getSectors().byKey(Sectors.MINING);
        if (mine == null || mining == null || sites <= 0) return new double[] { 0, 0 };
        BusinessInvestment plans = g.getBusinessInvestment();
        GoodsMarket ore = g.getEconomyManager().getMarkets().get(Good.IRON);
        double units = mine.makes(Good.IRON);
        double room = Math.max(0, plans.forecast(mining, ore) - mining.getCapacity(Good.IRON) - mining.getPipeline(Good.IRON));
        double first = plans.estimatedMonthlyProfit(Sectors.MINING, mine);
        double homeFirst = Math.min(units, room);
        double abroad = Good.IRON.exportable() ? Math.max(0, ore.netExportPrice()) : 0;
        double perTonneAtHome = (ore.getLocalPrice() - abroad) * BusinessInvestment.operatingRateOf(mining.getOperatingRate());
        int staffable = mining.staffableCount(mine, sites);
        int mines = 0;
        double monthly = 0;
        for (int i = 0; i < staffable; i++) {
            double home = Math.max(0, Math.min(units, room - i * units));
            double profit = first + (home - homeFirst) * perTonneAtHome;
            if (!(profit > 0)) break;
            mines++;
            monthly += profit;
        }
        return new double[] { mines, monthly };
    }

    /**
     * The month's payment that repays a field's price here over
     * Game.BUILD_BOND_YEARS (0.7.67, M3b): the level payment at the rate the
     * market quotes for that much money at that term
     * (DebtManager.quoteRate()) - the funding page's bond, whether or not the
     * cash would cover it, because cash sunk in ground is cash the city does
     * not lend or spend.
     */
    static double fieldPayment(Game g, LandParcel field) {
        double price = field.localPrice(fxRate(g));
        int months = Game.BUILD_BOND_YEARS * 12;
        double monthlyRate = g.getDebtManager().quoteRate(price, months) / 12;
        return monthlyRate > 0 ? price * monthlyRate / (1 - Math.pow(1 + monthlyRate, -months)) : price / months;
    }

    /** The share of a month's GDP the fuel bought abroad has to pass before the test player buys oil (0.7.62): spec-land 3's K entry, 1%. */
    static final double OIL_FUEL_IMPORTS_SHARE = .01;

    /** Offers with oil the test player bought over the run (0.7.62). */
    static int oilBought;

    /**
     * The fuel the world sold the city last month (0.7.62): its drivers'
     * (Game.getHouseholdFuelImports()) and its railway's (Rail.getFuelImported())
     * - their petrol and its diesel since 0.7.76 - and since 0.7.83 (batch O6)
     * the drivers' petrol the grocers' forecourts imported for them
     * (Retail.getFuelImported(); the households' own is nothing from then on)
     * and the diesel the businesses' vans burned (Sector.getFleetDieselImported(),
     * star O6: fuel bought abroad is fuel bought abroad, whoever burns it).
     */
    static double fuelBoughtAbroad(Game g) {
        double vans = 0;
        for (Sector s : g.getSectors().all()) vans += s.getFleetDieselImported();
        return g.getHouseholdFuelImports() + g.getSectors().rail().getFuelImported()
                + g.getSectors().retail().getFuelImported() + vans;
    }

    /** Over the run (0.7.62): the drivers' and the railway's fuel, what of it was bought abroad, the first well and refinery, and the most refineries standing. */
    static double fuelBillRun, fuelAbroadRun;
    static int firstWellMonth, firstRefineryMonth, mostRefineries;

    /** ...and the wells' decline over the run (0.7.84, batch O7; spec-oil 2.6): the crude they lifted and the most standing. */
    static double crudeLiftedRun;
    static int mostWells;

    /** ...and the refinery's products over the run (0.7.76, batch O1): made, by product, and what of them its tanks could not hold (spec-oil 6's joint-products risk). */
    static final java.util.Map<Good, Double> refinedRun = new java.util.EnumMap<>(Good.class);
    static double refinedWrittenOff;

    /**
     * THE PHASE-1 BUYERS OVER THE RUN (0.7.83, batch O6; spec-oil 2.5 and 6's
     * measure-first 4 and 5): the vans' diesel against the businesses'
     * payrolls; the drivers' petrol at the pump against what it cost at
     * wholesale, and the litres past the stations; the stations at the end and
     * at most, and the cars a station at the end; the lubricants the factories
     * bought and the bitumen the roads took; and the lubricants the refiners
     * made against the crude they ran.
     */
    static double vanDieselRun, vanDieselLitresRun, payrollRun, pumpBillRun, pumpWholesaleRun, pumpLitresRun, queueLitresRun;
    static double lubricantsBoughtRun, lubricantsImportedRun, crudeRefinedLitresRun;
    static int mostStations, firstStationMonth;

    static void countFuel(Game g) {
        // The drivers' petrol at what it cost wholesale since 0.7.83 (their bill is the pump's), so the share
        // bought abroad stays a share of what the fuel cost the city; the vans' diesel below.
        ham.citybuildersim.sectors.Retail pump = g.getSectors().retail();
        double drivers = Double.isFinite(pump.getWholesaleLitre())
                ? pump.getWholesaleLitre() * (pump.getPumpLitres() + pump.getQueueLitres()) : g.getHouseholdFuel();
        fuelBillRun += drivers + g.getSectors().rail().getFuelBill();
        fuelAbroadRun += fuelBoughtAbroad(g);
        for (Sector s : g.getSectors().all()) {
            vanDieselRun += s.getFleetDieselCost();
            vanDieselLitresRun += s.getFleetDieselLitres();
            fuelBillRun += s.getFleetDieselCost();
            payrollRun += Math.max(0, s.getPayroll());
            Sector.Input lub = s.inputRow(Good.LUBRICANTS);
            if (lub != null) {
                lubricantsBoughtRun += Math.max(0, lub.boughtLocal) + Math.max(0, lub.imported);
                lubricantsImportedRun += Math.max(0, lub.imported);
            }
        }
        ham.citybuildersim.sectors.Retail shops = g.getSectors().retail();
        pumpBillRun += shops.getFuelBill();
        if (Double.isFinite(shops.getWholesaleLitre())) {
            pumpWholesaleRun += shops.getWholesaleLitre() * (shops.getPumpLitres() + shops.getQueueLitres());
        }
        pumpLitresRun += shops.getPumpLitres();
        queueLitresRun += shops.getQueueLitres();
        mostStations = Math.max(mostStations, shops.stationsStanding());
        if (firstStationMonth == 0 && shops.stationsStanding() > 0) firstStationMonth = g.getMonth();
        Sector.Input crude = g.getSectors().refining().inputRow(Good.CRUDE);
        if (crude != null) crudeRefinedLitresRun += (Math.max(0, crude.boughtLocal) + Math.max(0, crude.imported))
                * ham.citybuildersim.sectors.Refining.CRUDE_LITRES_PER_TONNE;
        Sector refiners = g.getSectors().refining();
        for (Good p : refiners.goodsMade()) {
            Sector.Output o = refiners.outputRow(p);
            if (o == null) continue;
            refinedRun.merge(p, o.produced + o.exportBound, Double::sum);
            refinedWrittenOff += o.writtenOff;
        }
        int wells = qty(g, "Oil Well"), refineries = qty(g, "Oil Refinery");
        if (firstWellMonth == 0 && wells > 0) firstWellMonth = g.getMonth();
        mostWells = Math.max(mostWells, wells);
        Sector.Output lifted = g.getSectors().oil().outputRow(Good.CRUDE);
        if (lifted != null) crudeLiftedRun += Math.max(0, lifted.produced);
        if (firstRefineryMonth == 0 && refineries > 0) firstRefineryMonth = g.getMonth();
        mostRefineries = Math.max(mostRefineries, refineries);
        countRefiners(g);
        if (REFINERY_TRACE && g.getMonth() % 12 == 0) out.println(refineryLine(g));
        countPorts(g);
    }

    /* =====================================================================
       THE PORTS (0.7.86, batch O9; runs/spec-oil.md 5's O9 row)

       The test player's rule, at each look: while the city owns coast
       (Game.hasCoastFor()), when a kind's tonnes across the boundary that
       could go by sea last month (Ports.carriable(): crude left out while
       the refiners' tanks have no room for a tanker's cargo) are more than
       the berths of that kind standing and on site by WORTH_A_BERTH of a
       berth's month, it orders one terminal of that kind through build(), as
       every city order is placed (ground bought, the bond when the cash is
       short). One a kind each time it is advised - up to movesPerLook() times
       a look - the berths on site counted against the next. A rule, as the
       iron is, not a scored move:
       what a terminal is worth is the band it narrows, which no move here
       prices.
       ===================================================================== */

    /** Terminals the test player ordered, by name; the looks that found a berth's worth with no coast; the first terminal's month. */
    static final java.util.Map<String, Integer> terminalsOrdered = new java.util.TreeMap<>();
    static int portsNoCoast, firstTerminalMonth;

    /** The boats over the run: calls, the most in a month and when; the tonnes by sea and across the boundary; months crude was held back for room. */
    static long callsRun;
    static int mostCalls, mostCallsMonth, lastCalls, heldBackMonths;
    static double seaTonnesRun, boundaryTonnesRun;

    /** -Dplaytest.ports=true (0.7.86): the ports yearly - each kind's tonnes across the boundary, its berths, its share at sea and the month's calls (portsLine()). */
    static final boolean PORTS_TRACE = Boolean.getBoolean("playtest.ports");

    static void portsWhenNeeded(Game g) {
        Ports ports = g.getPorts();
        BuildingManager b = g.getBuildingManager();
        double[] standing = Ports.berths(b), onSite = Ports.berthsOnSite(b);
        for (Ports.Cargo k : Ports.Cargo.values()) {
            BuildingsTemplate t = terminalFor(g, k);
            if (t == null) continue;
            double uncovered = ports.carriable(k) - standing[k.ordinal()] - onSite[k.ordinal()];
            if (!(uncovered >= Ports.WORTH_A_BERTH * Ports.berthMonth(t))) continue;
            if (!g.hasCoastFor(t, 1)) {
                portsNoCoast++;
                return;
            }
            if (build(g, t.getName(), 1)) {
                terminalsOrdered.merge(t.getName(), 1, Integer::sum);
                if (firstTerminalMonth == 0) firstTerminalMonth = g.getMonth();
            }
        }
    }

    /** The sea terminals standing, all kinds. */
    static int terminalsStanding(Game g) {
        int n = 0;
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) if (Ports.isPort(t)) n += g.getBuildingManager().getQuantity(t.getId());
        return n;
    }

    /** The catalogue's terminal for a kind of cargo, or null. */
    static BuildingsTemplate terminalFor(Game g, Ports.Cargo k) {
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) if (Ports.isPort(t) && t.berthCargo() == k) return t;
        return null;
    }

    /** The month's boats and sea tonnes, counted (the calls without their quays: the map places them, O13). */
    static void countPorts(Game g) {
        Ports ports = g.getPorts();
        int calls = BoatSchedule.of(g.getMonth(), ports, java.util.List.of(), 0, 0).callCount();
        callsRun += calls;
        lastCalls = calls;
        if (calls > mostCalls) {
            mostCalls = calls;
            mostCallsMonth = g.getMonth();
        }
        seaTonnesRun += ports.seaTonnes();
        for (Ports.Cargo k : Ports.Cargo.values()) boundaryTonnesRun += ports.tradeIn(k) + ports.tradeOut(k);
        if (ports.getHeldBack() > 0) heldBackMonths++;
        if (PORTS_TRACE && g.getMonth() % 12 == 0) out.println(portsLine(g, calls));
    }

    /** The ports this month, a line: by kind, tonnes in and out across the boundary, the berths standing, the share in force and the sea tonnes; the calls. */
    static String portsLine(Game g, int calls) {
        Ports ports = g.getPorts();
        double[] berths = Ports.berths(g.getBuildingManager());
        StringBuilder s = new StringBuilder(String.format("  PORTS m%d coast %s:", g.getMonth(),
                g.getLandManager().getSeaKm2() > 0 ? String.format("%.2f km2", g.getLandManager().getSeaKm2()) : "none"));
        for (Ports.Cargo k : Ports.Cargo.values()) {
            s.append(String.format(" %s in %,.0f out %,.0f berths %,.0f share %.3f sea %,.0f;", k.name(),
                    ports.tradeIn(k), ports.tradeOut(k), berths[k.ordinal()], ports.share(k), ports.seaIn(k) + ports.seaOut(k)));
        }
        s.append(String.format(" crude %,.0f held back %,.0f; calls %d", ports.crudeTrade(), ports.getHeldBack(), calls));
        return s.toString();
    }

    /**
     * THE SPREAD PLANNER'S BUILDINGS OVER THE RUN (0.7.82, batch O5): each of
     * the refiners' buildings opened and sold back, and the crude units and
     * the conversion units standing at most. Which rule sold one follows from
     * Refining.mayRetire(): a crude unit only the distress rule sells (the
     * spare-capacity rule has no measure while no kind of unit stands idle),
     * a conversion unit only once its kind has stood idle six months, which
     * the spare-capacity rule sells first.
     */
    static final java.util.Map<String, Integer> refinersOpened = new java.util.TreeMap<>(), refinersSold = new java.util.TreeMap<>();
    static int[] refinersLast;
    static int mostCrudeUnits, mostConversionUnits, firstRefinerMonth;

    static void countRefiners(Game g) {
        BuildingManager b = g.getBuildingManager();
        List<BuildingsTemplate> all = b.getTemplatesBySector(Sectors.REFINING);
        if (refinersLast == null) refinersLast = new int[all.size()];
        int crude = 0, units = 0;
        for (int i = 0; i < all.size(); i++) {
            BuildingsTemplate t = all.get(i);
            int q = b.getQuantity(t.getId());
            if (ham.citybuildersim.sectors.Refining.isCrudeUnit(t)) crude += q; else units += q;
            if (q > refinersLast[i]) refinersOpened.merge(t.getName(), q - refinersLast[i], Integer::sum);
            if (q < refinersLast[i]) refinersSold.merge(t.getName(), refinersLast[i] - q, Integer::sum);
            refinersLast[i] = q;
        }
        if (firstRefinerMonth == 0 && crude + units > 0) firstRefinerMonth = g.getMonth();
        mostCrudeUnits = Math.max(mostCrudeUnits, crude);
        mostConversionUnits = Math.max(mostConversionUnits, units);
    }

    /** The refiners' planner this month, a line: what is short and spare, and the best-earning candidate (and the Oil Refinery's package) with the hurdle each money gate asks of it. */
    static String refineryLine(Game g) {
        ham.citybuildersim.sectors.Refining r = g.getSectors().refining();
        BusinessInvestment plans = g.getBusinessInvestment();
        ham.citybuildersim.sectors.Refining.Outlook o = r.outlook(plans);
        ham.citybuildersim.sectors.SpreadPlanner p = r.planner();
        StringBuilder sb = new StringBuilder(String.format("REFINERY m%-4d room %,.0f L spare %,.0f t cash %,.0fk losses %d |",
                g.getMonth(), o.room(), o.spareCrude(), r.getCash(), plans.getLossMonths(r.key())));
        ham.citybuildersim.sectors.SpreadPlanner.MoneyGate was = p.moneyGate();
        for (ham.citybuildersim.sectors.SpreadPlanner.MoneyGate mg : new ham.citybuildersim.sectors.SpreadPlanner.MoneyGate[] {
                ham.citybuildersim.sectors.SpreadPlanner.ON_ITS_BORROWING, ham.citybuildersim.sectors.SpreadPlanner.ON_ITS_WHOLE_COST }) {
            p.setMoneyGate(mg);
            List<ham.citybuildersim.sectors.SpreadPlanner.Candidate> all = ham.citybuildersim.sectors.Refining.appraise(o,
                    g.getBuildingManager().getTemplatesBySector(r.key()), p.city(r, plans, g));
            ham.citybuildersim.sectors.SpreadPlanner.Candidate top = null;
            for (ham.citybuildersim.sectors.SpreadPlanner.Candidate c : all) {
                if (c.failed() == ham.citybuildersim.sectors.SpreadPlanner.Gate.FEED) continue;
                if (top == null || c.score() > top.score()) top = c;
            }
            for (ham.citybuildersim.sectors.SpreadPlanner.Candidate c : all) {
                if (c != top && !"Oil Refinery".equals(c.template().getName())) continue;
                if (mg == ham.citybuildersim.sectors.SpreadPlanner.ON_ITS_WHOLE_COST && c != top) continue;
                double hurdle = p.hurdle(r, plans, g, c.cost());
                sb.append(String.format(" %s %s: earns %,.1fk on %,.0fk (%.4f%%/mo), hurdle %,.1fk (%.4f%%/mo) %s |",
                        mg, c.template().getName() + (c.with().isEmpty() ? "" : "+" + c.with().size()), c.earns(), c.cost(),
                        100 * c.score(), hurdle, c.cost() > 0 ? 100 * hurdle / c.cost() : 0,
                        c.passes() ? "PASS" : c.failed().name()));
            }
        }
        p.setMoneyGate(was);
        return sb.toString();
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
     * The share of the treasury's cash one look spends keeping ground ahead
     * (0.7.58, J1d): a tenth, the share the war chest tops the reserves up
     * from and the every-13th-stop purchase is held under.
     */
    static final double GROUND_AHEAD_CASH_SHARE = .10;

    /** Over the run: offers bought to keep ground ahead, their dry square feet and US dollars (thousands), and the looks the cash share stopped short. */
    static int groundAheadBought, groundAheadShort;
    static double groundAheadSqFt, groundAheadUsd;

    /**
     * The most of its dry ground a look leaves built on (0.7.58, J1d): the
     * ground in use grown BuildAdvice.HORIZON months by the businesses'
     * growthFactor(), with BuildAdvice.SLACK past it - the build advice's
     * own sizing (0.7.51), applied to the ground. About .95 in a city that
     * is not growing, lower in one that is.
     */
    static double groundAheadUtilisation(Game g) {
        return 1 / (g.getBusinessInvestment().growthFactor(BuildAdvice.HORIZON) * (1 + BuildAdvice.SLACK));
    }

    /**
     * ROOM TO GROW, PRICED FROM THE LINE (0.7.67, batch M3b): the output a
     * look's room-to-grow move is weighed at - none while no more of the
     * dry ground is built on than groundAheadUtilisation(), rising to the
     * whole month's GDP when all of it is, because a city with no ground
     * stops entirely (measured, in an earlier playtest: it ran down to 8,000
     * spare square feet with $106M in the bank and then shed its
     * construction sector from 2,900 capacity to 100 over four months).
     *
     * WHY: the move was priced from .85, gdp x (u - .85) / .15, from before
     * the ground was kept ahead (0.7.58). keepGroundAhead() leaves a look at
     * the line, .92 to .95, where the projection says the ground holds six
     * months' growth with the slack - and there the old price claimed 45 to
     * 67% of the month's output was being lost for want of ground, and beat
     * the power and the roads the city was short of. On 0.7.67's ensemble the
     * move won 670 of 1,036 moves to month 1,200 (0.7.63: 711 of 1,048), 388
     * of them over a throttle costing 10% of output or more. Seed 14 drew it
     * at months 477-490: all six moves bought ground (8 offers, 0.66 km2)
     * while power stood at 73% and the roads at 83%; a 120-month skip later
     * they ran at 54% and 45%, its plants at 23% of nameplate, and its jobs
     * fell from 4,337 to 1,614 (runs/fixM3b-notes.md). One line now says how
     * much ground is enough: the build advice's, which keepGroundAhead()
     * already keeps.
     */
    static double roomToGrow(Game g) {
        LandManager land = g.getLandManager();
        double used = land.getOwnedSqFt() > 0 ? land.getAllocatedSqFt() / land.getOwnedSqFt() : 1;
        double line = groundAheadUtilisation(g);
        if (!(used > line)) return 0;
        return Math.max(1, g.getEconomyManager().getMonthGdp()) * Math.min(1, (used - line) / (1 - line));
    }

    /** Over the run (0.7.67, M3b): offers the room-to-grow move bought. */
    static int roomMovesBought;

    /**
     * GROUND KEPT AHEAD (see advise()): buys the best value for room while
     * more of the dry ground is built on than groundAheadUtilisation(), the
     * offers together costing no more than GROUND_AHEAD_CASH_SHARE of the
     * cash at the look. Stops at the first offer past what is left of that
     * share, and counts the look as stopped short.
     */
    static void keepGroundAhead(Game g) {
        LandManager land = g.getLandManager();
        double most = groundAheadUtilisation(g);
        double budget = Math.max(0, g.getCash()) * GROUND_AHEAD_CASH_SHARE;
        int guard = 0;
        while (land.getOwnedSqFt() > 0 && land.getAllocatedSqFt() / land.getOwnedSqFt() > most
                && guard++ < 60) {
            LandParcel best = g.bestOffer(Game.LandNeed.room());
            if (best == null) break;
            double local = best.localPrice(g.getForeignAccounts().getRate());
            if (local > budget || !g.canAffordParcel(best)) {
                groundAheadShort++;
                break;
            }
            if (!g.buyLandParcel(best.getId())) break;
            budget -= local;
            groundAheadBought++;
            groundAheadSqFt += best.getSizeSqFt();
            groundAheadUsd += best.getPriceUsd();
        }
    }

    /**
     * WATER, AND WHERE IT COMES FROM (0.7.59, batch J2; spec-land 3, J2's
     * playtest rule). A Water Treatment Plant treats no more than the city's
     * lakes and river yield (UtilitiesHandler, THE FRESH WATER LIMIT), so a
     * shortage is met with a water plant only while one more would deliver:
     * while the fresh water left under the limit, after the plants standing
     * and on site, covers what is short or a whole plant, and then no more
     * plants than that water feeds. When it does not - the limit binds -
     * the player builds desalination if the city owns sea; otherwise buys
     * the offer with the most lake or river a dollar
     * (LandMarket.bestFresh()); and with no fresh water on offer, the
     * cheapest offer with sea (Game.bestOffer(LandNeed.coast())), the
     * desalination plant following at a later move. Each priced, as the
     * plant always was, at the output the shortage is costing.
     */
    static void addWater(java.util.List<Move> moves, Game g, double lost) {
        if (lost <= 0) return;
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        BuildingsTemplate plant = template(g, "Water Treatment Plant");
        if (plant == null) return;
        double fill = u.getAverageUtilityFill();
        double onSite = 0;
        for (BuildingsStacks s : g.getBuildingManager().getStacksUnderConstruction()) {
            if (s.getBuilding().isFreshWater()) onSite += s.getUnderConstruction() * s.getBuilding().getProduction1();
        }
        double headroom = Math.max(0, u.getFreshCap() - (u.getFreshNameplate() + onSite) * fill);
        double shortage = Math.max(0, u.getWaterConsumption() - u.getWaterProduction());
        double onePlant = plant.getProduction1() * fill;
        if (headroom >= Math.min(shortage, onePlant)) {
            // No more plants than the fresh water left feeds, the last of them in part.
            int fed = (int) Math.max(1, Math.min(25, Math.ceil(headroom / Math.max(1, onePlant))));
            double gap = lost / Math.max(1, g.getEconomyManager().getMonthGdp());
            addThrottle(moves, g, "Water Treatment Plant", "water plant", lost, Math.min(gap, (fed - .5) / 25.0));
            return;
        }
        LandManager land = g.getLandManager();
        if (land.getSeaKm2() > 0) {
            addThrottle(moves, g, "Desalination Plant", "desalination", lost);
            return;
        }
        LandParcel lake = land.getMarket().bestFresh();
        LandParcel buy = lake != null ? lake : g.bestOffer(Game.LandNeed.coast());
        if (buy == null) return;
        waterLandOffered++;
        moves.add(new Move(lake != null ? "bought fresh water" : "bought a coast",
                lost * (1 - moves.size() * 1e-6), () -> {
                    if (!g.buyLandParcel(buy.getId())) return false;
                    if (lake != null) freshBought++; else coastsBought++;
                    return true;
                }));
    }

    /** Over the run: offers bought for their lakes and river past the fresh water limit, offers bought for a coast, and the moves that offered either (0.7.59). */
    static int freshBought, coastsBought, waterLandOffered;

    /**
     * THE CITY MAP, WATCHED (0.7.60, batch J3): the run asks for its city's
     * map at the founding, so every month keeps it up (Game.reconcileMap())
     * and every save writes its sidecar - nothing in the model reads it, so
     * the traces are the proof. The months it was kept, the months its
     * districts did not sum to the buildings (a finding), and the reloads
     * that read it back the same.
     */
    static int mapMonths, mapMismatches, mapReloads, mapReloadsSame;

    /**
     * The road constraint, and every way there is of easing it.
     *
     * Three roads and three transit lines, each costed in the trips a dollar
     * takes off the street, offered best-first so that the tie-break inside
     * addThrottle picks the best rather than the first one somebody typed. See
     * the note at the call site for why transit is in here at all.
     */
    static void addRoadThrottle(java.util.List<Move> moves, Game g, double lost) {
        if (lost <= 0) return;
        InfrastructureManager roads = g.getInfrastructureManager();
        double gdp = Math.max(1, g.getEconomyManager().getMonthGdp());
        double roadGap = lost / gdp;

        /*
         * WHAT A RIDER IS WORTH, which is the whole of why transit is offered
         * beside tarmac rather than instead of it: taking one commuter off the
         * street saves whatever that commuter was costing it, and a driver
         * costs carRoadFactor times what a walker does.
         */
        double perRider = roads.carRoadFactor();
        double ceiling = roads.getLoad(Traffic.COMMUTERS) * InfrastructureManager.TRANSIT_MAX_SHARE;
        double underneath = roads.getCapacity() * InfrastructureManager.TRANSIT_NEEDS_ROAD;
        double room = Math.max(0, Math.min(ceiling, underneath) - roads.getUsableTransit());

        java.util.List<double[]> order = new java.util.ArrayList<>();
        java.util.List<String[]> names = new java.util.ArrayList<>();
        String[][] candidates = {
            { "Gravel Road", "gravel road" }, { "Paved Road", "roads" },
            { "Elevated Highway", "highway" }, { "Bus Network", "buses" },
            { "Light Rail Line", "light rail" }, { "Metro Line", "metro" },
        };
        for (String[] candidate : candidates) {
            BuildingsTemplate t = template(g, candidate[0]);
            if (t == null || t.getCashCost() <= 0) continue;
            double seats = t.getTransitCapacity();
            double trips, gap;
            if (seats > 0) {
                if (room <= 0) continue;                 // the buses are already full
                trips = Math.min(seats, room) * perRider;
                // ...and never order more of them than the city could use:
                // room / seats LINES, and gap is a share of addThrottle()'s 25
                // at a full outage, so the lines over 25 (0.7.67, M3b). Until
                // then the cap was 25 times too loose: ensemble seed 14, its
                // roads at 66% after its plants closed, ordered 16 Bus Networks
                // in two moves (months 613-614) for a city of 8,000, and their
                // crews' wages, the treasury's since 0.7.49, ran from D$28M to
                // D$130M a month - half to all of its revenue - with the cash
                // below nothing from month 654 (runs/fixM3b-notes.md).
                gap = Math.min(roadGap, room / seats / 25.0);
                /*
                 * ...NOR MORE THAN THE OUTPUT AND THE FARES THEY GIVE BACK PAY
                 * THE WAGES OF (0.7.49, D3). The treasury pays transit's crews
                 * since B9, and a Bus Network is a post per ten seats: this
                 * player had 25 of them at m1000 in a city of 15,740, and at
                 * the transit spec's checkpoints their bill, unpaid, was up to
                 * 54% of a month's output. Jerus chose to teach it to count
                 * the wages: the police station's rule above - bought when
                 * what it takes off is more than it costs to run - with the
                 * riders' fares counted (linesThatPay()).
                 */
                int lines = linesThatPay(g, t, (int) Math.max(1, Math.min(25, Math.ceil(gap * 25))));
                if (lines <= 0) continue;
                gap = Math.min(gap, (lines - .5) / 25.0);
            } else {
                trips = t.getCapacity();
                gap = roadGap;
            }
            if (trips <= 0) continue;
            order.add(new double[] { trips / t.getCashCost(), gap });
            names.add(candidate);
        }
        /*
         * ...AND WHICH ROAD IS THE BUILD ADVICE'S (0.7.70, N1). Jerus: "the
         * game still recommends gravel roads, even when i think paved roads
         * are better". This player ranked the three roads by their capacity
         * over the template's founding cash cost - gravel first in every
         * city, at any price of ground: 124 of the 126 road orders of the
         * 0.7.69 run were gravel. A sensible player asks the advice which
         * road: the one that costs least over its life a trip it takes off
         * the road, its ground and its repairs in it
         * (BuildAdvice.lifetime()), and paving the gravel roads it has when
         * that beats a new Paved Road (BuildAdvice.pavingBeatsPaved()). So
         * the roads keep their place among the moves - the best road's trips
         * a founding dollar, as before, against the lines' - and are offered
         * in the advice's order inside it. Whether a road or a line eases
         * the road stays this player's own rule (star N1-6): the lines' place,
         * their caps and linesThatPay() are untouched.
         */
        BuildAdvice.Measure roadM = BuildAdvice.Measure.of(BuildAdvice.Kind.ROADS);
        java.util.Map<BuildingsTemplate, Integer> site = BuildAdvice.onSite(g, roadM);
        double free = g.getLandManager().getAvailableSqFt();
        double roadsKey = Double.NEGATIVE_INFINITY;
        java.util.List<double[]> roadOrder = new java.util.ArrayList<>();
        java.util.List<String[]> roadNames = new java.util.ArrayList<>();
        for (int i = order.size() - 1; i >= 0; i--) {
            BuildingsTemplate t = template(g, names.get(i)[0]);
            if (t.getTransitCapacity() > 0) continue;
            roadsKey = Math.max(roadsKey, order.get(i)[0]);
            double unit = BuildAdvice.unit(g, roadM, t);
            roadOrder.add(0, new double[] { unit > 0 ? BuildAdvice.lifetime(g, t, free, site) / unit
                    : Double.POSITIVE_INFINITY, order.get(i)[1], 0 });
            roadNames.add(0, names.get(i));
            order.remove(i);
            names.remove(i);
        }
        if (g.paveable() > 0 && BuildAdvice.pavingBeatsPaved(g, free, site)) {
            double unit = BuildAdvice.pavingUnit(g, site);
            roadOrder.add(new double[] { BuildAdvice.pavingLifetime(g, free, site) / unit, roadGap, 1 });
            roadNames.add(new String[] { ConstructionControl.PAVE_FROM, "paved gravel" });
        }
        // The roads by the advice's figure, least first; a tie keeps the catalogue's order.
        for (int i = 1; i < roadOrder.size(); i++) {
            for (int k = i; k > 0 && roadOrder.get(k)[0] < roadOrder.get(k - 1)[0]; k--) {
                java.util.Collections.swap(roadOrder, k, k - 1);
                java.util.Collections.swap(roadNames, k, k - 1);
            }
        }
        // ...each at the best road's key, ahead of the lines as the roads were listed, so a tie with a line keeps the roads first.
        for (int i = 0; i < roadOrder.size(); i++) {
            double[] road = roadOrder.get(i);
            order.add(i, new double[] { roadsKey, road[1], road[2] });
            names.add(i, roadNames.get(i));
        }
        // Best trips per dollar first. A plain insertion sort: six candidates.
        for (int i = 1; i < order.size(); i++) {
            for (int k = i; k > 0 && order.get(k)[0] > order.get(k - 1)[0]; k--) {
                java.util.Collections.swap(order, k, k - 1);
                java.util.Collections.swap(names, k, k - 1);
            }
        }
        for (int i = 0; i < order.size(); i++) {
            if (order.get(i).length > 2 && order.get(i)[2] == 1) addPaving(moves, g, lost, order.get(i)[1]);
            else addThrottle(moves, g, names.get(i)[0], names.get(i)[1], lost, order.get(i)[1]);
        }
    }

    /**
     * Paving gravel roads as a way of easing the road (0.7.70, N1), as
     * addThrottle() offers a building: as many as addThrottle() would order of
     * a road at this gap, and no more than the gravel roads there are to pave.
     */
    static void addPaving(java.util.List<Move> moves, Game g, double lost, double gap) {
        if (lost <= 0) return;
        int quantity = (int) Math.max(1, Math.min(Math.min(25, Math.ceil(gap * 25)), g.paveable()));
        moves.add(new Move("paved gravel", lost * (1 - moves.size() * 1e-6), () -> pave(g, quantity)));
    }

    /**
     * Paves n gravel roads (0.7.70, N1): out of the cash, or, short of it,
     * with the long bond build() borrows on, sized to what the cash is short
     * of, when the city can service it (canService()).
     */
    static boolean pave(Game g, int n) {
        Game.BuildQuote q = g.quotePave(n);
        if (q == null) return false;
        if (q.total > g.getCash()) {
            double needed = Math.max(q.total - g.getCash(), 5000);
            if (canService(g, needed)) {
                g.handleLongBondLogic(needed, 20, 100);
                notePaper(g, n + " x paving");
            }
        }
        boolean paved = g.paveRoads(n);
        refusal(ConstructionControl.PAVE_FROM + (paved ? ": PAVED" : ": not paved"));
        if (paved) { pavings++; roadsPaved += n; }
        return paved;
    }

    /** Over the run (0.7.70): the pavings ordered and the gravel roads in them. */
    static int pavings, roadsPaved;

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

    /**
     * The most of these transit lines, up to most, that pay their way this
     * month (0.7.49, D3): the output the road gives back with them standing
     * (the month's GDP times the throughput they add, on the network as it
     * would be - InfrastructureManager.with()) and a month of fares from the
     * riders they add, against their running cost (runningCost()), all k of
     * them. The first k from most down that pays; 0 when none does. Myopic
     * on purpose, and blind to lines already on site: Jerus asked for the
     * wages, and counting the sites was measured to cost a tenth of the
     * city at m4000 (the transit spec's ★10).
     */
    static int linesThatPay(Game g, BuildingsTemplate t, int most) {
        InfrastructureManager roads = g.getInfrastructureManager();
        double gdp = Math.max(1, g.getEconomyManager().getMonthGdp());
        double now = roads.getThroughputRatio(), ridersNow = roads.getTransitRiders();
        double pass = g.getEconomyManager().getTaxPolicy().monthlyFare();
        double run = runningCost(g, t);
        for (int k = most; k >= 1; k--) {
            double[] streams = new double[Traffic.values().length];
            for (Traffic s : Traffic.values()) streams[s.ordinal()] = k * t.loadOf(s);
            InfrastructureManager after = roads.with(0, 0, k * t.getTransitCapacity(), k * t.getRoadLoad(), streams);
            double gain = gdp * (after.getThroughputRatio() - now);
            double fares = (after.getTransitRiders() - ridersNow) * pass;
            if (gain + fares >= k * run) return k;
        }
        return 0;
    }

    /** What one of these costs the city a month to run, fully staffed at today's wages. */
    static double runningCost(Game g, BuildingsTemplate t) {
        double[] wages = g.getPopulationManager().getWagesPerType();
        double pay = 0;
        for (JobType job : JobType.values()) {
            int n = t.getJobs(job);
            if (n > 0 && wages != null && job.ordinal() < wages.length) pay += n * wages[job.ordinal()];
        }
        return pay + t.getUpkeep();
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

    /** ...of a building by its permanent id (0.7.71: the childcare ids, renamed). */
    static int qtyOf(Game g, int id) {
        return g.getBuildingManager().getQuantity(id);
    }

    /** The city's home buildings standing, every RESIDENTIAL type (0.7.71: what childcare is counted against). */
    static int homeBuildings(Game g) {
        int n = 0;
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            if (t.getCategory() == BuildingType.RESIDENTIAL) n += g.getBuildingManager().getQuantity(t.getId());
        }
        return n;
    }

    /** The founding village's builds: build(), and on an Insane founding the funding page's bond for a stage the treasury is short of (0.7.14). Every other founding, build() to the byte. */
    static boolean villageBuild(Game g, String name, int quantity) {
        return FOUNDING == Founding.Preset.INSANE ? buildOnTheFundingPage(g, name, quantity) : build(g, name, quantity);
    }

    /**
     * Orders a building the way the screens do: check land, buy some if short,
     * borrow if the treasury cannot cover it, then place the order.
     */
    static boolean build(Game g, String name, int quantity) {

        BuildingsTemplate t = template(g, name);
        if (t == null) return false;

        /*
         * Land first, exactly as buildStack() checks it first - AND WHAT THE
         * BUILD SHORTCUT BUYS (0.7.58, batch J1c; the shortcut since 0.7.61): the
         * cheapest offer of bare ground, no ore to pay for, whose dry ground
         * covers the shortfall (Game.bestOffer(LandNeed.shortfall())),
         * and, when none covers it, the most dry ground a dollar the city can
         * afford, then the shortfall that is left. Until 0.7.58 this bought
         * the cheapest offer standing, which on forty offers is nearly always
         * the smallest: purchases fell to 0.04-0.15 km2 a time in the middle
         * of the run, and the city was half the size from month 1,000 to
         * 3,000 (fixJ1b-notes).
         */
        int guard = 0;
        while (g.getLandManager().getAvailableSqFt() < t.getLandSqFt() * quantity
                && guard++ < 60) {
            double shortfall = t.getLandSqFt() * quantity - g.getLandManager().getAvailableSqFt();
            LandParcel covers = g.bestOffer(Game.LandNeed.shortfall(shortfall));
            if (covers == null) {
                g.getLandManager().updateMarket(g.getPopulationManager().getPopulation());
                covers = g.bestOffer(Game.LandNeed.shortfall(shortfall));
            }
            if (covers == null || !g.buyLandParcel(covers.getId())) break;
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
                if (!BORROW_AT_HOME && m.foreignWindowOpen() && m.foreignRate() < m.getRate() * .8) {
                    g.handleForeignLogic("Term", needed / Math.max(.01, fxRate(g)),
                            20, 100, false);
                } else {
                    g.handleLongBondLogic(needed, 20, 100);
                }
                notePaper(g, quantity + " x " + name);   // the trace (0.7.10); nothing else reads it
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

    /*
     * THE CITY'S PAPER AND THE BANK THAT HOLDS IT, over the run (2026-09-21).
     *
     * Until 0.6.11 the bank took every bond the city sold onto its book and
     * never handed over the money - the settlement was snapshotted after the
     * top-of-month clear, so it was always zero (see Game, THE BANK PAYS FOR
     * THE CITY'S PAPER). The endpoint lines cannot show what that did: a city
     * owes nothing at month 4,002 in most seeds, and the bank's strain at the
     * end is one month of four thousand. So the run counts the months the
     * city owed anything, how much at the most, what the bank actually paid
     * for the paper, and the bank's strain month by month - the four figures
     * the measurement of the fix is written in.
     */
    static int monthsOwing, monthsOwingAtHome, worstStrainMonth, peakCityDebtMonth;
    static int strainMonths, monthsWithoutCapacity;
    static double strainSum, worstStrain, peakCityDebt, paperSettledRun;

    /*
     * THE CENTRAL BANK OVER THE RUN (0.7.0): the most the treasury owed it,
     * how long the ceiling bound, and the most the arrears rule left unpaid.
     * Counted a month at a time, because the endpoint of a city that ran dry
     * for a century and recovered looks like one that never did.
     */
    static int monthsAtCeiling, monthsOnAdvances, peakAdvancesMonth, peakArrearsMonth, firstAdvanceMonth;
    static double peakAdvances, peakArrears;

    /*
     * WHO HELD THE CITY'S PAPER OVER THE RUN (0.7.1): what the households paid
     * at the settles and in how many months, what the bank's desk bought back
     * from them and in how many, and what the central bank bought and sold -
     * counted a month at a time, because a mechanic nobody counts is a
     * mechanic nobody knows fired (the README's third rule).
     */
    static int monthsHouseholdsBought, monthsDeskBought, monthsCentralBankTraded;
    static double householdsBoughtRun, deskBoughtRun, couponsToHouseholdsRun;

    /*
     * The most the central bank's holdings took off the long end, and when:
     * the twenty-year paper a borrowing seed sells matures inside the run, so
     * by month 4,002 the central bank usually holds none of it and the
     * endpoint's compression reads zero. The peak is the figure the holdings
     * dial is measured by.
     */
    static int peakCompressionMonth;
    static double peakCompression, longRateAtPeak, heldShareAtPeak;

    static void countTheHolders(Game g) {
        double bought = g.getHouseholdsBoughtPaper();
        if (bought > 0) { monthsHouseholdsBought++; householdsBoughtRun += bought; }
        double desk = g.getBank().getPaperBoughtFromHouseholds();
        if (desk > 0) { monthsDeskBought++; deskBoughtRun += desk; }
        CentralBank cb = g.getCentralBank();
        if (cb.getBoughtPaper() > 0 || cb.getSoldPaper() > 0) monthsCentralBankTraded++;
        couponsToHouseholdsRun += g.getCouponsToHouseholds();
        DebtManager dm = g.getDebtManager();
        double off = dm.compression(600);
        if (off > peakCompression) {
            peakCompression = off;
            peakCompressionMonth = g.getMonth();
            longRateAtPeak = dm.curveRate(600);
            heldShareAtPeak = dm.centralBankShareOfTerm();
        }
    }

    static void countTheCentralBank(Game g) {
        CentralBank cb = g.getCentralBank();
        double owed = cb.getAdvancesToTreasury();
        if (owed > 0) monthsOnAdvances++;
        if (owed > 0 && firstAdvanceMonth == 0) firstAdvanceMonth = g.getMonth();
        if (cb.ceilingBound()) monthsAtCeiling++;
        if (owed > peakAdvances) { peakAdvances = owed; peakAdvancesMonth = g.getMonth(); }
        double unpaid = g.getArrearsTotal();
        if (unpaid > peakArrears) { peakArrears = unpaid; peakArrearsMonth = g.getMonth(); }
    }

    static void countTheCitysPaper(Game g) {
        DebtManager paper = g.getDebtManager();
        double owed = paper.getAllPrincipal();
        if (owed > 0) monthsOwing++;
        if (paper.getDomesticPrincipal() > 0) monthsOwingAtHome++;
        if (owed > peakCityDebt) { peakCityDebt = owed; peakCityDebtMonth = g.getMonth(); }
        paperSettledRun += g.getCityPaperSettled();
        double strain = g.getBank().strain();
        if (strain == Double.MAX_VALUE) { monthsWithoutCapacity++; return; }
        strainSum += strain;
        strainMonths++;
        if (strain > worstStrain) { worstStrain = strain; worstStrainMonth = g.getMonth(); }
    }

    static void run(Game g, int months) {
        for (int i = 0; i < months; i++) {

            int before = g.getMonth();
            // The two instruments, both off by default: the dial held for the
            // month about to run, and the index that month will hand the wages.
            if (holdsPolicyRate(g)) g.getDebtManager().takeTheDial(POLICY_RATE);
            if (QE_SHARE != null && g.getMonth() > ForeignAccounts.SETTLING_MONTHS
                    && g.getCentralBank().getTargetShare() != QE_SHARE) {
                g.getCentralBank().setTargetShare(QE_SHARE);
            }
            double indexHanded = g.getPriceIndex().getIndex();
            if (WAGES && Double.isNaN(lagImplied)) lagImplied = g.getLabourMarket().getCostOfLiving();
            g.simulateMonths(1);
            if (g.hasCityMap()) {
                mapMonths++;
                if (!java.util.Arrays.equals(g.getCityMap().totals(), g.getMapCounts())) {
                    mapMismatches++;
                    flag(g.getMonth(), "the city map's districts do not sum to the buildings", "month " + g.getMonth());
                }
            }
            lifetimeWriteOffs += g.getBank().getWriteOffs();
            lifetimeHouseholdWriteOffs += g.getHouseholdBalance().getWrittenOff();

            /*
             * THE PLAYER RESOLVES A FAILED BANK AND ANSWERS ITS OFFER (0.7.14),
             * because a player would - a frozen bank means no credit, and no
             * credit means nothing gets built. On the button (-Dplaytest.rescue=
             * BUTTON) it presses the month it finds the bank frozen; automatic,
             * the month did it. A standing bank under its minimum asks the city
             * for preferred, and the player answers (-Dplaytest.preferred). See
             * THE CITY'S FUND AND THE BANK'S RESCUE, STATED.
             */
            if (!RESCUE_AUTO && g.canResolveBank()) {
                double paid = g.resolveBank();
                if (paid > 0) buttonPresses++;
            }
            if (g.isPreferredOfferPending()) {
                if (PREFERRED_ACCEPT) acceptTheOffer(g);
                else g.declinePreferredOffer();
            }
            watchTheStake(g);
            watchTheLedger(g);

            // ...and its equity's two parts (0.7.13, round 2): how low paid in
            // runs - a buyback comes off it at all it cost - and the most the
            // two ever leave its equity unexplained.
            Bank parts = g.getBank();
            if (parts.knowsEquitySplit()) {
                if (parts.paidInCapital() < lowestPaidIn) {
                    lowestPaidIn = parts.paidInCapital();
                    lowestPaidInMonth = g.getMonth();
                }
                worstSplitOff = Math.max(worstSplitOff, Math.abs(parts.equitySplitResidual()));
            }

            if (g.getMonth() == before) {
                /*
                 * A SKIP THAT DID NOT RUN THE MONTH. Until 0.7.14 this was the
                 * empty treasury: simulateMonths() refused to run while cash
                 * <= 0, the play clock ran on (nextMonth(), drawing the
                 * central bank's advance), and this stepped as a player would,
                 * because a city that cannot pay its bills is a state the game
                 * has to keep working in, not one to stop testing at. Since
                 * 0.7.15 the skip runs through an empty treasury on the
                 * advances, as play does (Jerus: "Skip runs too"), so the
                 * month the playtest ran is the month a skip runs, and this is
                 * left for a skip that ran nothing at all - a month that threw
                 * inside it (Game.takeSkipFailure()), stepped again below where
                 * nothing catches it. The count stays in the trace; 0.7.14's
                 * ensembles had thousands of these, in Insane and at a held 25%.
                 */
                refusedSkips++;
                g.toggleNextMonth();

                if (g.getMonth() == before) {
                    flag(before, "the city could not advance even one month",
                            "cash " + g.getCash());
                    return;
                }
            }
            if (WAGES) walkLag(g, indexHanded);
            countTheCitysPaper(g);
            countTheCentralBank(g);
            countTheHolders(g);
            audit(g);
            traceMonth(g);
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
        long pop = g.getPopulationManager().getPopulation();
        long workforce = g.getPopulationManager().getWorkforce();
        double gdp = g.getEconomyManager().getMonthGdp();
        double retail = g.getSectors().retail().statement().revenue;
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
        // The city map (0.7.60): read back from its sidecar, the same map.
        if (g.hasCityMap()) {
            mapReloads++;
            if (back.hasCityMap() && back.getCityMap().same(g.getCityMap())) mapReloadsSame++;
            else flag(month, "the city map across a save", "slot " + slot);
        }

        same(month, "cash across a save", back.getCash(), cash);
        same(month, "next-month income across a save", back.getIncome(), income);
        same(month, "monthly GDP across a save", back.getEconomyManager().getMonthGdp(), gdp);
        same(month, "retail revenue across a save",
                back.getSectors().retail().statement().revenue, retail);
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
        for (Sector s : g.getSectors().all()) {
            Sector t = back.getSectors().byKey(s.key());
            same(month, "  ..." + s.key() + " net income", t.getNetIncome(), s.getNetIncome());
            same(month, "  ..." + s.key() + " cash", t.getCash(), s.getCash());
            same(month, "  ..." + s.key() + " month in progress", t.pending().revenue(), s.pending().revenue());
            /*
             * The named halves of revenue that are not a good - the builders'
             * work recognised against their repair bill. Saved like the rest
             * of the struck month, so compared like the rest of it.
             */
            for (String part : s.otherRevenueParts().keySet()) {
                same(month, "  ..." + s.key() + " " + part,
                        t.otherRevenueParts().getOrDefault(part, 0.0),
                        s.otherRevenueParts().get(part));
            }
            same(month, "  ..." + s.key() + " named revenue parts",
                    t.otherRevenueParts().size(), s.otherRevenueParts().size());
            for (Good good : Good.values()) {
                same(month, "  ..." + s.key() + " " + good + " stock", t.getStock(good), s.getStock(good));
                /*
                 * AND THE PER-GOOD MONEY THE STATEMENT OPENS INTO. A breakdown
                 * that is written but not restored is invisible until somebody
                 * reloads a game and clicks Revenue, which is the worst place
                 * to find it. Both maps, both sides of each, on the struck
                 * month AND on the month in progress - the ledger is saved for
                 * the same reason the statement is, and either one going
                 * missing loses a screen. See Sector.Split and SectorState.
                 */
                Sector.Split soldWas = s.statement().sold.get(good);
                Sector.Split soldIs = t.statement().sold.get(good);
                same(month, "  ..." + s.key() + " " + good + " sold at home",
                        soldIs == null ? 0 : soldIs.atHome, soldWas == null ? 0 : soldWas.atHome);
                same(month, "  ..." + s.key() + " " + good + " sold abroad",
                        soldIs == null ? 0 : soldIs.abroad, soldWas == null ? 0 : soldWas.abroad);
                Sector.Split boughtWas = s.statement().bought.get(good);
                Sector.Split boughtIs = t.statement().bought.get(good);
                same(month, "  ..." + s.key() + " " + good + " bought here",
                        boughtIs == null ? 0 : boughtIs.atHome, boughtWas == null ? 0 : boughtWas.atHome);
                same(month, "  ..." + s.key() + " " + good + " imported",
                        boughtIs == null ? 0 : boughtIs.abroad, boughtWas == null ? 0 : boughtWas.abroad);
                Sector.Split pendWas = s.pending().sold.get(good), pendIs = t.pending().sold.get(good);
                same(month, "  ..." + s.key() + " " + good + " sold, unstruck",
                        pendIs == null ? 0 : pendIs.total(), pendWas == null ? 0 : pendWas.total());
                Sector.Split pbWas = s.pending().bought.get(good), pbIs = t.pending().bought.get(good);
                same(month, "  ..." + s.key() + " " + good + " bought, unstruck",
                        pbIs == null ? 0 : pbIs.total(), pbWas == null ? 0 : pbWas.total());
            }
        }
        for (GoodsMarket m : g.getMarkets().all()) {
            same(month, "  ..." + m.good() + " price",
                    back.getMarkets().get(m.good()).getLocalPrice(), m.getLocalPrice());
        }

        /*
         * THE BORROWER'S RECORD. The loans were always restored; the count of
         * write-downs and the months of ban left were not, and nothing here
         * compared them - which is why 49 reloads came back "matched" with a
         * sector's twelve restructures reading zero. A record that is not
         * compared is not carried.
         */
        BusinessDebtManager creditWas = was.getBusinessDebtManager();
        BusinessDebtManager creditNow = now.getBusinessDebtManager();
        for (String sector : Sectors.KEYS) {
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
        // ...and what it has set aside, the target it chose, and its month (0.7.8).
        same(month, "the bank's allowance across a save",
                back.getBank().getAllowance(), g.getBank().getAllowance());
        same(month, "...the capital target it chose",
                back.getBank().capitalTarget(), g.getBank().capitalTarget());
        same(month, "...the month's provision",
                back.getBank().provisions(), g.getBank().provisions());
        same(month, "...what it kept this month",
                back.getBank().getNetIncome(), g.getBank().getNetIncome());
        same(month, "...and what it paid its owners over the year",
                back.getBank().dividendsOverYear(), g.getBank().dividendsOverYear());
        same(month, "what the sectors hold abroad across a save",
                back.getOutwardInvestment().totalUsd(), g.getOutwardInvestment().totalUsd());
        same(month, "...and the rate it was valued at",
                back.getOutwardInvestment().getLastRate(), g.getOutwardInvestment().getLastRate());
        same(month, "...and the financial account the rate is priced on",
                back.getForeignAccounts().monthlyFinancialAccount(), g.getForeignAccounts().monthlyFinancialAccount());
        // The vault is kept in dollars since 2026-09-21, in a slot of its own
        // at the end of the array; the local figure in slot 19 is derived.
        same(month, "the vault, in dollars, across a save",
                back.getForeignAccounts().getReservesUsd(), g.getForeignAccounts().getReservesUsd());
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
            // The exchange: the desk's inventory, the last trade and fair
            // value, and what rests on the book - the next month's waterfall
            // sells into it (0.7.12 round 2).
            same(month, "...and on the desk",
                    back.getEquity().getDealerShares(c), g.getEquity().getDealerShares(c));
            same(month, "...and its price, the last trade",
                    back.getExchange().price(c), g.getExchange().price(c));
            same(month, "...and its fair value",
                    back.getExchange().fair(c), g.getExchange().fair(c));
            same(month, "...and what is bid on its book",
                    back.getExchange().bookOf(c).depth(OrderBook.Side.BUY, 1e-12), g.getExchange().bookOf(c).depth(OrderBook.Side.BUY, 1e-12));
            same(month, "...and asked",
                    back.getExchange().bookOf(c).depth(OrderBook.Side.SELL, 1e300), g.getExchange().bookOf(c).depth(OrderBook.Side.SELL, 1e300));
            same(month, "...and its split factor",
                    back.getExchange().getSplitFactor(c), g.getExchange().getSplitFactor(c));
        }
        same(month, "the bank's securities at the mark across a save",
                back.getBank().getSecurities(), g.getBank().getSecurities());
        // Paper sold since the last settle, which the bank pays for at the
        // next one (2026-09-21) - a stop that borrows and then saves carries it.
        same(month, "the city's paper its bank has not yet paid for, across a save",
                back.getCityPaperUnsettled(), g.getCityPaperUnsettled());
        same(month, "what the households hold abroad across a save",
                back.getHouseholdBalance().totalAbroadUsd(), g.getHouseholdBalance().totalAbroadUsd());
        // The businesses' bonds (0.7.12): every holder's face, the cells' own
        // face of the households' (round 2), what the bank's cost it, and the
        // resting orders.
        BondMarket bondsWas = g.getBondMarket(), bondsNow = back.getBondMarket();
        same(month, "the bonds outstanding across a save", bondsNow.totalFace(), bondsWas.totalFace());
        same(month, "...held by the households", bondsNow.faceHeldByHouseholds(), bondsWas.faceHeldByHouseholds());
        same(month, "...and their claims on them", back.getHouseholdBalance().totalBonds(), g.getHouseholdBalance().totalBonds());
        same(month, "...held by the bank, at what they cost it", bondsNow.bankCost(), bondsWas.bankCost());
        same(month, "...held by the companies", bondsNow.faceHeldByCompanies(), bondsWas.faceHeldByCompanies());
        same(month, "...held abroad", bondsNow.faceHeldByWorld(), bondsWas.faceHeldByWorld());
        same(month, "...and the bank's book of them", back.getBank().getBondBook(), g.getBank().getBondBook());
        same(month, "...and the book's concentration", back.getBank().getConcentrationAddOn(), g.getBank().getConcentrationAddOn());
        int restingWas = 0, restingNow = 0;
        for (CorporateBond b : bondsWas.getBonds()) restingWas += bondsWas.bookOf(b).bids().size() + bondsWas.bookOf(b).asks().size();
        for (CorporateBond b : bondsNow.getBonds()) restingNow += bondsNow.bookOf(b).bids().size() + bondsNow.bookOf(b).asks().size();
        same(month, "...and the orders resting on their books", restingNow, restingWas);
        // The city's fund and the bank's preferred (0.7.14): the fund's cash, its settings and what
        // it is worth, the register's city shares in both books, the bonds' city face, the preferred
        // with its arrears and warrants, and the budget's line for the transfer.
        TreasuryFund fundWas = g.getFund(), fundNow = back.getFund();
        same(month, "the city's fund's cash across a save", fundNow.getCash(), fundWas.getCash());
        same(month, "...its dial", fundNow.getDial(), fundWas.getDial());
        if (fundNow.getRescueMode() != fundWas.getRescueMode() || fundNow.isOfferPending() != fundWas.isOfferPending()
                || fundNow.getResolutions().size() != fundWas.getResolutions().size()) {
            flag(month, "the fund's RESCUE SETTING, OFFER OR RESCUES did not survive the save",
                    fundWas.getRescueMode() + "/" + fundWas.isOfferPending() + "/" + fundWas.getResolutions().size() + " -> "
                    + fundNow.getRescueMode() + "/" + fundNow.isOfferPending() + "/" + fundNow.getResolutions().size());
        }
        same(month, "...what it is worth", back.fundValue(), g.fundValue());
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            same(month, Equity.COMPANIES[c] + ": the city's shares across a save",
                    back.getEquity().getCityShares(c), g.getEquity().getCityShares(c));
            same(month, "...its rescue book's", back.getEquity().getCityRescueShares(c), g.getEquity().getCityRescueShares(c));
        }
        same(month, "the bonds' city face across a save", bondsNow.faceHeldByCity(), bondsWas.faceHeldByCity());
        same(month, "the bank's preferred across a save", back.getBank().preferredOutstanding(), g.getBank().preferredOutstanding());
        same(month, "...its arrears", back.getBank().getPreferredArrears(), g.getBank().getPreferredArrears());
        same(month, "...its warrants' shares", back.getBank().warrantSharesOut(), g.getBank().warrantSharesOut());
        // ...and what each of its holdings cost, and its record (0.7.39, FundLedger).
        same(month, "the fund's cost basis across a save", fundNow.getLedger().acbHeld(), fundWas.getLedger().acbHeld());
        same(month, "...what it realized", fundNow.getLedger().realized(), fundWas.getLedger().realized());
        same(month, "...its rows", fundNow.getLedger().getActivity().size(), fundWas.getLedger().getActivity().size());
        same(month, "the fund's transfer on the budget across a save",
                now.getNationalAccounts().getFundTransfer(), was.getNationalAccounts().getFundTransfer());
        // ...and the city's own rate and its curve, which the fund's bonds are marked on (0.7.14): the
        // debt market's last strike, carried whole (DebtManager.marketToSave()). In millionths of a point.
        same(month, "the city's rate across a save", back.getDebtManager().getRate() * 1e6, g.getDebtManager().getRate() * 1e6);
        same(month, "...and its ten-year rate", back.getDebtManager().curveRate(120) * 1e6, g.getDebtManager().curveRate(120) * 1e6);
        // The long sick (2026-09-11): the ring is a stock, and so is who it killed last month.
        double[] ringNow = back.getSickness().getState(), ringWas = g.getSickness().getState();
        for (int i = 0; i < ringWas.length; i++) {
            same(month, "the sick ring across a save (slot " + i + ")",
                    i < ringNow.length ? ringNow[i] * 1e4 : Double.NaN, ringWas[i] * 1e4);
        }

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
        same(month, "the land office's block level across a save",
                back.getLandManager().getMarket().getLevel(),
                g.getLandManager().getMarket().getLevel());
        for (WageBand band : WageBand.values()) {
            same(month, "the labour market's tightness across a save (" + band + ")",
                    back.getLabourMarket().getTightness(band),
                    g.getLabourMarket().getTightness(band));
        }
        /*
         * WAGES AGAINST THE INDEX, across a save (2026-09-21). The wage index
         * is a stock walked by a lag, the target it walks toward is struck in
         * the month, and the price index is restruck from its history - three
         * places a reload could leave wages chasing a different level from
         * the one the live city chases. See WAGES AGAINST THE INDEX above.
         */
        same(month, "the wage index across a save",
                back.getLabourMarket().getCostOfLiving(), g.getLabourMarket().getCostOfLiving());
        same(month, "...the level it is walking toward",
                back.getLabourMarket().getLivingTarget(), g.getLabourMarket().getLivingTarget());
        same(month, "...and the price index it is handed",
                back.getPriceIndex().getIndex(), g.getPriceIndex().getIndex());

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
    /** The bank's paid-in capital at its lowest, the month, and the most its two parts were ever off its equity (0.7.13, round 2). */
    static double lowestPaidIn = Double.POSITIVE_INFINITY, worstSplitOff;
    static int lowestPaidInMonth = -1;
    static double lifetimeHouseholdWriteOffs;

    /* ---------------- the city's fund and the bank's rescue (0.7.14) ---------------- */

    /** Presses of the Bank tab's button that resolved the bank (-Dplaytest.rescue=BUTTON). */
    static int buttonPresses;
    /** Offers accepted after borrowing for them on the funding page's bond, and what that bond raised. */
    static int offersFunded;
    static double offerFundingRaised;

    /**
     * ACCEPTS THE BANK'S OFFER, the way the page lets a player: a treasury
     * short of it takes the funding page's first offer - the build screen's
     * twenty-year bond, sized to the gap - and then pays. If it still cannot,
     * the offer waits for next month.
     */
    static void acceptTheOffer(Game g) {
        double gap = g.preferredOfferShortBy();
        if (gap > 0) {
            double before = g.getCash();
            g.handleLongBondForCash(gap, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);
            notePaper(g, "the bank's preferred (the offer's funding page)");
            if (g.getCash() > before) { offersFunded++; offerFundingRaised += g.getCash() - before; }
        }
        g.acceptPreferredOffer();
    }

    /**
     * THE CITY'S STAKE IN ITS BANK AFTER EACH RESCUE: at the resolution, then
     * five, ten and twenty-five years on, and the first months it is under
     * half and under a tenth - until the next resolution takes it back to
     * everything. One row a resolution.
     */
    static final class Stake {
        int month;
        double at = Double.NaN, five = Double.NaN, ten = Double.NaN, twentyFive = Double.NaN;
        int under50 = -1, under10 = -1, endedBy = -1;
        double lowest = 1;
    }
    static final List<Stake> stakes = new ArrayList<>();
    static int resolutionsSeen;
    /** The fund's value, its cash and the most of any company its market book held, over the run. */
    static double fundPeak, fundCashShareSum;
    static int fundMonths;
    /** The warrants' shares taken at expiry, at the price the month they were taken (share counts move with the exchange's splits, so the count alone does not add up across a run). */
    static double warrantSharesSeen, warrantValueTaken;
    static int warrantExercises;
    static final double[] mostHeld = new double[Equity.COMPANIES.length];

    static void watchTheStake(Game g) {
        List<TreasuryFund.Resolution> all = g.getFund().getResolutions();
        while (resolutionsSeen < all.size()) {
            if (!stakes.isEmpty() && stakes.get(stakes.size() - 1).endedBy < 0) {
                stakes.get(stakes.size() - 1).endedBy = all.get(resolutionsSeen).month();
            }
            Stake s = new Stake();
            s.month = all.get(resolutionsSeen).month();
            stakes.add(s);
            resolutionsSeen++;
        }
        double stake = g.cityStakeInBank();
        double taken = g.getFund().getWarrantSharesTaken();
        if (taken > warrantSharesSeen) {
            warrantValueTaken += (taken - warrantSharesSeen) * g.getExchange().price(Equity.BANK);
            warrantExercises++;
            warrantSharesSeen = taken;
        }
        if (!stakes.isEmpty()) {
            Stake s = stakes.get(stakes.size() - 1);
            int since = g.getMonth() - s.month;
            if (Double.isNaN(s.at)) s.at = stake;
            if (since >= 60 && Double.isNaN(s.five)) s.five = stake;
            if (since >= 120 && Double.isNaN(s.ten)) s.ten = stake;
            if (since >= 300 && Double.isNaN(s.twentyFive)) s.twentyFive = stake;
            if (stake < .5 && s.under50 < 0) s.under50 = g.getMonth();
            if (stake < .1 && s.under10 < 0) s.under10 = g.getMonth();
            s.lowest = Math.min(s.lowest, stake);
        }
        double value = g.fundValue();
        if (value > 0) {
            fundPeak = Math.max(fundPeak, value);
            fundCashShareSum += g.getFund().getCash() / value;
            fundMonths++;
            for (int c = 0; c < Equity.COMPANIES.length; c++) {
                double shares = g.getEquity().getShares(c);
                if (shares > 0) mostHeld[c] = Math.max(mostHeld[c], g.getEquity().getCityMarketShares(c) / shares);
            }
        }
    }

    /* ---------------- the fund's cost basis (0.7.39) ---------------- */

    /**
     * THE FUND'S LEDGER, MONTH BY MONTH (FundLedger; the project's
     * spec-fund-0739.md, 3.7, rule 3 - count a mechanic in a real run): the
     * identity FundLedgerCheck holds on a fixture - realized + the change in
     * unrealized = the change in what the shares and bonds are worth + what
     * sales and maturities brought in - what purchases and rescues cost -
     * read every month of the run, its worst kept; and the rows the month
     * wrote, by kind.
     */
    static double[] ledgerBefore;
    static double ledgerWorst;
    static int ledgerRuleRows, ledgerHandRows, ledgerMatured, ledgerWrittenDown, ledgerRescues;

    static double[] ledgerSnap(Game g) {
        TreasuryFund f = g.getFund();
        FundLedger l = f.getLedger();
        return new double[] { g.fundSharesValue() + g.fundBondsValue(), l.acbHeld() + f.getRescueCost(), l.realized(),
                f.getSharesBought() + f.getBondsBought(), f.getSharesSold() + f.getBondsSold() + f.getPrincipal(),
                f.getRescuesPaid() };
    }

    static void watchTheLedger(Game g) {
        double[] now = ledgerSnap(g);
        if (ledgerBefore != null) {
            double[] was = ledgerBefore;
            double lhs = (now[2] - was[2]) + ((now[0] - now[1]) - (was[0] - was[1]));
            double rhs = (now[0] - was[0]) + (now[4] - was[4]) - (now[3] - was[3]) - (now[5] - was[5]);
            ledgerWorst = Math.max(ledgerWorst, Math.abs(lhs - rhs) / Math.max(1, Math.abs(now[0])));
        }
        ledgerBefore = now;
        List<FundLedger.Activity> rows = g.getFund().getLedger().getActivity();
        for (int i = rows.size() - 1; i >= 0 && rows.get(i).month() == g.getMonth(); i--) {
            FundLedger.Activity a = rows.get(i);
            switch (a.kind()) {
                case FundLedger.BUY, FundLedger.SELL -> { if (a.hand()) ledgerHandRows++; else ledgerRuleRows++; }
                case FundLedger.MATURED -> ledgerMatured++;
                case FundLedger.WRITTEN_DOWN -> ledgerWrittenDown++;
                case FundLedger.RESCUE -> ledgerRescues++;
                default -> { }
            }
        }
    }

    /** A stake, in words: "84.4%", or "-" before it was read. */
    static String pct(double v) { return Double.isNaN(v) ? "-" : String.format("%.1f%%", v * 100); }

    /* ---------------- an Insane founding (0.7.14) ---------------- */

    /** What an Insane city was quoted on day 0 for its village, and what it borrowed in its first year. */
    static DebtQuote dayZeroBond, dayZeroNote;
    static double villageInvoice, firstYearBorrowed;
    static int insaneBorrowings;

    /**
     * THE PLAYER OF AN INSANE CITY BORROWS FIRST: the build screen's
     * twenty-year bond, sized to the village's invoice at a new city's prices
     * (Founding.whatItBuys()), before it places a house - the treasury holds
     * nothing and the clock will not run. What the day-0 quote was is kept
     * for the report.
     */
    static void borrowForTheVillage(Game g) {
        villageInvoice = Founding.whatItBuys(g.getBuildingManager().getTemplates(), 0, 0).village();
        dayZeroBond = g.quoteLongBondForCash(villageInvoice, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);
        dayZeroNote = g.quoteTBill(villageInvoice, Game.BUILD_NOTE_MONTHS, Game.BUILD_NOTE_GRANULE);
        double before = g.getCash();
        g.handleLongBondForCash(villageInvoice, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);
        notePaper(g, "the village, borrowed for first (Insane)");
        firstYearBorrowed += Math.max(0, g.getCash() - before);
        insaneBorrowings++;
    }

    /**
     * ...AND BUILDS THE REST OF IT THE SAME WAY: a stage the treasury is
     * short of is borrowed for on the funding page's bond, sized to the gap
     * (Game.buildFundingGap()), and placed - the page a player is shown,
     * where the advisor's own rule (canService()) will not lend to a city
     * with no revenue. Only for an Insane founding's hand-built village.
     */
    static boolean buildOnTheFundingPage(Game g, String name, int quantity) {
        if (build(g, name, quantity)) return true;
        BuildingsTemplate t = template(g, name);
        if (t == null) return false;
        double gap = g.buildFundingGap(t, quantity);
        if (!(gap > 0)) return false;
        double before = g.getCash();
        g.handleLongBondForCash(gap, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);
        notePaper(g, quantity + " x " + name + " (the funding page, Insane)");
        if (g.getMonth() <= 13) firstYearBorrowed += Math.max(0, g.getCash() - before);
        insaneBorrowings++;
        return build(g, name, quantity);
    }

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

        // On the flag's preset (0.7.10): Standard when unset. See THE FOUNDING, AS A CHOICE.
        Game g = new Game(files, founding());
        List<String> log = new ArrayList<>();
        long started = System.currentTimeMillis();
        traceOpen();

        System.setOut(quiet);
        try {
            g.run();
            /*
             * THE DIAL'S HAND AND THE ROLLOVER, STATED (0.7.13). A new game
             * founds on the autopilot and rolls what falls due in the same
             * structure (Game.newGame()); this run founds through the
             * constructor, which does neither, so the setups say which they
             * are: the default setup is the dial by hand at its default rate,
             * the autopilot setup the autopilot - what they always meant - and
             * the rollover is ROLLOVER's, as a player now gets it.
             */
            g.getDebtManager().setAutopilot(AUTOPILOT);
            g.setRolloverMode(ROLLOVER);
            // ...and its map, kept up from here on (0.7.60): THE CITY MAP, WATCHED.
            g.getCityMap();
            // ...and the rescue setting and the fund's dial (0.7.14), as the
            // flags say: the constructor founds on the button at 0.
            g.setRescueMode(RESCUE_AUTO ? TreasuryFund.RescueMode.AUTOMATIC : TreasuryFund.RescueMode.BUTTON);
            g.setFundDial(FUND_DIAL);
            if (ADVANCES_MONTHS != null) g.getCentralBank().setAdvancesCeilingMonths(ADVANCES_MONTHS);
            if (INFLATION_TARGET != null) g.getDebtManager().setInflationTarget(INFLATION_TARGET);
            // ...and the refiners' money gate (0.7.82): MONEY_GATE_WHOLE, the counterfactual; unset, the rule in force.
            if (MONEY_GATE_WHOLE) g.getSectors().refining().planner()
                    .setMoneyGate(ham.citybuildersim.sectors.SpreadPlanner.ON_ITS_WHOLE_COST);

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
            /*
             * THE HEALTH DIALS, FOR THE ENSEMBLE (2026-09-19):
             * -Dplaytest.healthFee=<scale> and -Dplaytest.healthPremium=<rate>
             * set the two TaxPolicy dials at the founding and leave them for
             * the run, so eight seeds at a setting can be laid beside eight
             * at the default. Unset, the defaults stand and this is a no-op.
             */
            String feeScale = System.getProperty("playtest.healthFee");
            String premium = System.getProperty("playtest.healthPremium");
            if (feeScale != null) {
                g.getEconomyManager().getTaxPolicy().setHealthFeeScale(Double.parseDouble(feeScale));
            }
            if (premium != null) {
                g.getEconomyManager().getTaxPolicy().setHealthPremiumRate(Double.parseDouble(premium));
            }
            /*
             * THE PRICE OF A PLACE, FOR THE ENSEMBLE (2026-09-21):
             * -Dplaytest.tuitionScale=<x>, -Dplaytest.grantBasis=<WAGE_SHARE |
             * FIXED | SURPLUS_SHARE | TUITION_SHARE>, -Dplaytest.grantAmount=<in
             * the basis's unit> and -Dplaytest.loanRate=<annual> set the four
             * education dials at the founding and leave them for the run.
             *
             * AND -Dplaytest.schools=true BUILDS THE SCHOOLS, because the
             * advisor never does: the default run ends its 4,002 months with
             * "students 0", so a dial on a grant, a loan or a price has
             * nothing to reach in any seed - a mechanic the fixture never
             * touches is a mechanic no run has ever tested, the K1 rule the
             * policy rate above was added under. Under the flag the schools
             * go up as the city grows big enough to carry them - asked at the
             * end of the founding and at every stop after it (ensureSchools) -
             * and the same flag at the default dials is the control. Unset,
             * none of this runs and the output is the run as it has always
             * been.
             */
            String tuitionScale = System.getProperty("playtest.tuitionScale");
            String grantBasis = System.getProperty("playtest.grantBasis");
            String grantAmount = System.getProperty("playtest.grantAmount");
            String loanRate = System.getProperty("playtest.loanRate");
            if (tuitionScale != null) {
                g.getEconomyManager().getTaxPolicy().setTuitionScale(Double.parseDouble(tuitionScale));
            }
            if (grantBasis != null || grantAmount != null) {
                TaxPolicy dials = g.getEconomyManager().getTaxPolicy();
                TaxPolicy.GrantBasis basis = grantBasis == null ? dials.getGrantBasis()
                        : TaxPolicy.GrantBasis.valueOf(grantBasis.trim().toUpperCase());
                double amount = grantAmount == null ? dials.getGrantAmount() : Double.parseDouble(grantAmount);
                dials.setGrant(basis, amount);
            }
            if (loanRate != null) {
                g.getEconomyManager().getTaxPolicy().setStudentLoanRate(Double.parseDouble(loanRate));
            }
            educationSet = tuitionScale != null || grantBasis != null || grantAmount != null
                    || loanRate != null || SCHOOLS;
            // An Insane city borrows first (0.7.14): see borrowForTheVillage().
            if (FOUNDING == Founding.Preset.INSANE) borrowForTheVillage(g);
            // ...and automatic building on from the founding (0.7.73, -Dplaytest.autobuild).
            if (AUTOBUILD) g.getAutoBuilder().setOn(true, g.getDecisions());
            villageBuild(g, "House", 40 + seed % 4);
            villageBuild(g, "Convenience Store", 3);
            /*
             * AND A FIELD, SINCE 2026-09-13. A town is founded where there is
             * food, and since the tenth sector exists the mills the advisor
             * builds have to buy their crops - from the world, at the ceiling,
             * if nobody here grows any. That import bill lands on a city of
             * four hundred people and it shows: without a field the founding
             * transient runs four months and 2.7 households with nowhere,
             * with one it runs a single month and 1.3. A Mixed Farm is $312k
             * and six city blocks, which is what a founding settlement can
             * afford and roughly what it would do.
             */
            villageBuild(g, "Mixed Farm", 2);
            run(g, 3 + (seed / 4) % 3);
            villageBuild(g, "House", 20 + (seed / 12) % 3);
            run(g, 4);
            villageBuild(g, "Convenience Store", 2);
            villageBuild(g, "Construction Depot", 1);
            run(g, 5);
            advise(g);
            run(g, 6);
            ensureSchools(g);
            childcareWhenNeeded(g);

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
                // ...and an attentive player will not let a century go by. See
                // the note on ATTENTIVE above; without the flag this is the
                // same unbounded skip it always was.
                skip = Math.min(skip, longestSkip());
                run(g, Math.min(skip, TARGET_MONTHS - g.getMonth()));
                ensureSchools(g);
                childcareWhenNeeded(g);

                // Look at the city, fix the worst thing, then a few hands-on
                // months watching what that did - the way anyone plays.
                for (int move = 0; move < movesPerLook(); move++) {
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
                        /*
                         * TWENTY YEARS, home or abroad, since 0.7.1: term
                         * loans are issued at 10, 20, 30, 40 or 50 years
                         * (LongTermBond.MATURITIES), and the twenty-five this
                         * asked for until then is refused. Twenty is the term
                         * the build-funding path above has always used.
                         */
                        if (BORROW_AT_HOME) {
                            // The same money, at home - see THE CITY'S OWN PAPER.
                            double local = ask * Math.max(.01, fxRate(g));
                            g.handleLongBondLogic(local, 20, 100);
                            log.add(String.format("  m%-5d borrowed $%,.0fk at home at %.2f%%",
                                    g.getMonth(), local, m.getRate() * 100));
                        } else {
                            g.handleForeignLogic("Term", ask, 20, 100, false);
                            log.add(String.format("  m%-5d borrowed US$%,.0fk abroad at %.2f%%",
                                    g.getMonth(), ask, m.foreignRate() * 100));
                        }
                        notePaper(g, "cheap money taken because it was cheap (every 19th stop)");
                    }
                }

                // Land price used to be a dial the player turned. It is the
                // market's now, so what a player actually does instead is go
                // and buy some - which changes the price by changing how full
                // the city is.
                if (stop % 13 == 0) {
                    LandParcel spare = g.getLandManager().getMarket().bestValue();
                    // What it costs in local money today (0.7.6): priced in dollars.
                    if (spare != null && spare.localPrice(g.getForeignAccounts().getRate())
                            < g.getCash() * .1) {
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
                    log.add(bankEra(g));
                    if (WAGES) log.add(wageEra(g));
                    nextCheckpoint += 250;
                }
            }

            log.add(era(g, "final"));
            if (WAGES) log.add(wageEra(g));

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
        out.println("  PLAYED BY: " + (ATTENTIVE
                ? "somebody paying attention - yearly, eight moves a look"
                : "somebody who checks in every few decades - the default"));
        out.println("  LONG PLAYTEST - " + g.getMonth() + " months ("
                + (g.getMonth() / 12) + " years) in " + seconds + "s");
        out.println("=================================================================\n");

        for (String line : log) {
            out.println(line);
        }

        out.println();
        out.println("  months a skip did not run (stepped instead; an empty treasury until 0.7.15): "
                + refusedSkips);
        out.printf("  most crowded the city ever got: %.0f%% of what its buildings"
                + " comfortably hold (month %d)%n", worstCrowding * 100, worstCrowdingMonth);
        out.printf("  people who moved in: %,.0f   people who left: %,.0f%n",
                totalArrivals, totalDepartures);
        out.printf("  unemployment: %.1f%% at the end, worst %.1f%%%n",
                lastUnemployment * 100, worstUnemployment * 100);
        {
            // The people outside the families (2026-09-11).
            Unemployment u = g.getUnemployment();
            FamilyModel f = g.getFamilies();
            double unhousedFamilies = 0;
            for (double v : f.unhousedPeopleByBand()) unhousedFamilies += v;
            out.printf("  outside the families: on EI %,.0f  off EI %,.0f  unhoused %,.0f (evicted %,.0f)"
                    + "  students %,.0f  orphans %,.0f%n",
                    u.onEi(), u.getOffEi(), unhousedFamilies + u.getUnhoused(), u.getUnhoused(),
                    f.getSeekers(FamilyModel.Seeker.STUDENT), f.getOrphansTotal());
            out.printf("  over the run: %,.0f evicted, %,.0f of them left the city; most with no home %,.0f (month %d); most orphans %,.0f%n",
                    totalEvicted, totalLeftBroke, worstUnhoused, worstUnhousedMonth, worstOrphans);
            out.printf("  EI: $%,.0fk paid last month, $%,.0fk of premiums; grants $%,.0fk;"
                    + " student loans owed $%,.0fk%n",
                    g.getEconomyManager().getEiBenefits(), g.getEconomyManager().getEiPremiums(),
                    g.getEconomyManager().getStudentGrants(), g.getHouseholdBalance().totalStudentDebt());
            HouseholdBalance hb = g.getHouseholdBalance();
            Household poorest = hb.cell(FamilyStructure.SINGLE_ADULT, PayTier.UNSKILLED);
            out.printf("  what one of them has: on EI $%,.1fk saved $%,.1fk owed | EI run out $%,.1fk / $%,.1fk"
                    + " | lost their home $%,.1fk / $%,.1fk | unskilled single $%,.1fk / $%,.1fk%n",
                    hb.unemployed(UnemployedHousehold.Status.ON_EI).savings(), hb.unemployed(UnemployedHousehold.Status.ON_EI).debt(),
                    hb.unemployed(UnemployedHousehold.Status.OFF_EI).savings(), hb.unemployed(UnemployedHousehold.Status.OFF_EI).debt(),
                    hb.unemployed(UnemployedHousehold.Status.UNHOUSED).savings(), hb.unemployed(UnemployedHousehold.Status.UNHOUSED).debt(),
                    poorest.savings(), poorest.debt());
        }
        out.printf("  months with a pay tier in decline: %d   months anybody left: %d%n",
                monthsAnyTierDeclining, monthsPeopleLeft);
        out.printf("  off sick: %.1f%% at the end, worst %.1f%%, %.1f%% averaged over the run%n",
                lastSickRate * 100, worstSickRate * 100,
                monthsObserved > 0 ? workLostToIllness / monthsObserved * 100 : 0);
        out.printf("  outbreaks: %d, ill for %d months of %d%n",
                outbreaks, monthsInOutbreak, monthsObserved);
        {
            Sickness sick = g.getSickness();
            double illTotal = 0;
            for (double v : illnessDeathsByBand) illTotal += v;
            out.printf("  the long sick: %,.0f ill more than two months at the end (worst %,.0f, month %d),"
                    + " %.1f died of it last month, recovery %.0f%%%n",
                    sick.peoplePastTwoMonths(g.getCohorts()), worstLongSick, worstLongSickMonth,
                    sick.getLastDeaths(), sick.getLastRecovery() * 100);
            out.printf("  died of illness over the run: %,.0f of %,.0f deaths (%.1f%%) - babies %,.0f"
                    + "  children %,.0f  teens %,.0f  adults %,.0f  seniors %,.0f%n",
                    illTotal, allDeaths, allDeaths > 0 ? illTotal / allDeaths * 100 : 0,
                    illnessDeathsByBand[0], illnessDeathsByBand[1], illnessDeathsByBand[2],
                    illnessDeathsByBand[3], illnessDeathsByBand[4]);
            out.printf("  everyone who died over the run: babies %,.0f  children %,.0f  teens %,.0f  adults %,.0f"
                    + "  seniors %,.0f  - of whom orphans %,.0f and with no home %,.0f%n",
                    deathsByBandRun[0], deathsByBandRun[1], deathsByBandRun[2], deathsByBandRun[3],
                    deathsByBandRun[4], orphanDeathsRun, unhousedDeathsRun);
        }
        {
            Crime crime = g.getCrime();
            out.printf("  crime: %.2fx Canada's at the end (%,.0f a year per 100k), mean %.2fx, worst %.2fx in month %d;"
                    + " police coverage %.0f%% at the end, mean %.0f%%%n",
                    crime.getRateVsCanada(), crime.getRatePer100k(),
                    crimeMonths > 0 ? crimeVsSum / crimeMonths : 0, worstCrimeVs, worstCrimeMonth,
                    crime.getCoverage() * 100, crimeMonths > 0 ? coverageSum / crimeMonths * 100 : 0);
            out.printf("  the reasons at the end: no home %.0f  past EI %.0f  short %.0f  on EI %.0f  crowded %.0f"
                    + "  no reason %.0f  too few police %.0f crimes a month%n",
                    crime.getCrimes(Crime.Cause.NO_HOME), crime.getCrimes(Crime.Cause.PAST_EI),
                    crime.getCrimes(Crime.Cause.SHORT_OF_MONEY), crime.getCrimes(Crime.Cause.ON_EI),
                    crime.getCrimes(Crime.Cause.CROWDED), crime.getCrimes(Crime.Cause.NO_CAUSE),
                    crime.getCrimes(Crime.Cause.FEW_POLICE));
            out.printf("  prisons: %,.0f inside at the end (worst %,.0f, %,.0f per 100k), %,.0f caught over the run,"
                    + " %,.0f of them not held; killed %,.0f; stolen $%,.0f; police stations %d, HQs %d, jails %d,"
                    + " penitentiaries %d%n",
                    crime.prisoners(), worstPrisoners, crime.getPrisonersPer100k(), caughtRun, notHeldRun,
                    killedRun, stolenRun * 1000, qty(g, "Police Station"), qty(g, "Police Headquarters"),
                    qty(g, "Jail"), qty(g, "Penitentiary"));
        }

        /* -------------------------------------------------------------------
           BUSINESS SERVICES - and the point of printing it is the MECHANISM,
           not the size of the city.

           The eight-seed median cannot resolve a ten percent effect: the crime
           batch established that seven hundredths of a point on the sick rate
           ends two seeds nine percent smaller. So what has to be visible here
           is whether the thing WORKS - does it open, does it employ the band it
           is meant to, does the wage bill rise as it hires, does the currency
           close it, and did the licence gate ever bind.
           ------------------------------------------------------------------- */
        {
            ham.citybuildersim.sectors.BusinessServices bs = g.getSectors().businessServices();
            double seats = bs.getSeats();
            out.printf("  business services: %,.0f seats (%,.0f support, %,.0f back office, %,.0f engineering)"
                    + " in %d centres, %d offices%n",
                    seats, bs.getCapacity(Good.SUPPORT_WORK), bs.getCapacity(Good.BACK_OFFICE_WORK),
                    bs.getCapacity(Good.ENGINEERING_WORK),
                    qty(g, "Contact Centre") + qty(g, "Shared Services Centre"),
                    qty(g, "Engineering Services Office"));
            if (seats > 0) {
                out.printf("  what the world pays a seat: support $%,.0f  back office $%,.0f  engineering $%,.0f"
                        + " - wages take %.0f%% of it%n",
                        bs.priceOfSeat(Good.SUPPORT_WORK) * 1000,
                        bs.priceOfSeat(Good.BACK_OFFICE_WORK) * 1000,
                        bs.priceOfSeat(Good.ENGINEERING_WORK) * 1000,
                        bs.payrollShare() * 100);
                out.printf("  its books: revenue $%,.0fk, payroll $%,.0fk, net $%,.0fk a month;"
                        + " %d write-down(s), %d month(s) of losses%n",
                        bs.statement().revenue, bs.statement().payroll, bs.statement().netIncome,
                        g.getEconomyManager().getBusinessDebtManager()
                                .getRestructureCount(Sectors.BUSINESS_SERVICES),
                        g.getBusinessInvestment().getLossMonths(Sectors.BUSINESS_SERVICES));
            }
            out.printf("  over the run: %,.0f seats at the peak (month %d), seats standing for %,d months of %d,"
                    + " last in month %,d; $%,.0fk sold abroad%n",
                    peakSeats, peakSeatsMonth, monthsWithSeats, g.getMonth(), lastSeatMonth,
                    serviceExportsRun);
            out.printf("  could it staff one? contact %.0f%%  shared %.0f%%  engineering %.0f%% (it wants %.0f%%)%n",
                    bs.staffableShare(g.getBuildingManager().getTemplateByName("Contact Centre")) * 100,
                    bs.staffableShare(g.getBuildingManager().getTemplateByName("Shared Services Centre")) * 100,
                    bs.staffableShare(g.getBuildingManager().getTemplateByName("Engineering Services Office")) * 100,
                    ham.citybuildersim.sectors.BusinessServices.MIN_STAFFABLE_TO_ORDER * 100);
            double[] sup = g.getPopulationManager().supplyByBand();
            double[] pst = g.getPopulationManager().staffablePostsByBand();
            StringBuilder sb = new StringBuilder("  spare workers by band:");
            for (WageBand b : WageBand.values()) {
                sb.append(String.format("  %s %,.0f", b.name().toLowerCase(),
                        Math.max(0, sup[b.ordinal()] - pst[b.ordinal()])));
            }
            out.println(sb);
            CapitalFlows cf = g.getCapitalFlows();
            out.printf("  the carry trade: $%,.0fk borrowed and standing (peak $%,.0fk) on a %.2f-point spread;"
                    + " $%,.0fk lent and $%,.0fk repaid over the run, $%,.0fk of coupons%n",
                    cf.getCarryStock() * 1000, cf.getPeakCarryStock() * 1000,
                    cf.getCarrySpread() * 100,
                    cf.getLifetimeCarryBorrowed() * 1000, cf.getLifetimeCarryRepaid() * 1000,
                    cf.getLifetimeCarryInterest() * 1000);
            out.printf("  ...against the bank: book $%,.0fk of which carry $%,.0fk, headroom $%,.0fk%n",
                    g.getBank().getBook() * 1000, g.getBank().getCarryBook() * 1000,
                    g.getBank().headroom() * 1000);
            double spare = g.getPopulationManager().spareLicences(JobType.UNIV_HIGHTECH_ENG);
            out.printf("  engineering licences: %,.0f held, %,.0f spare, %,.0f needed to open an office%n",
                    g.getPopulationManager().getLicensed(JobType.UNIV_HIGHTECH_ENG), spare,
                    120 * Game.LICENCE_COVER_TO_OPEN);
        }

        /*
         * The health service, which in this run is a service the advisor never
         * builds - the private sector correctly will not touch healthcare, and
         * nobody is playing. So these lines are the DO-NOTHING case, and that is
         * what makes them worth printing: they are the floor a player is
         * measured against.
         */
        /*
         * THE SECOND-HAND CAR MARKET (2026-09-17), which is a poverty measure
         * dressed as a transport one. A household puts the car up when the wage
         * no longer buys the food and half a year of that gap is more than it
         * has saved or can borrow, so a run where cars change hands every month
         * is a run with families in trouble every month - and the SHARE that
         * found a buyer is the other half of it, because the month everybody is
         * selling is the month nobody is buying.
         */
        out.printf("  the used-car market: %,.0f offered over the run, %,.0f sold"
                + " (%.0f%% found a buyer), %,.0f households gave one up last month%n",
                usedOffered, usedSold, usedOffered > 0 ? usedSold / usedOffered * 100 : 0,
                g.getUsedCarsTraded());
        if (usedSold > 0) {
            out.printf("  ...and it went for %.0f%% of a new car at best, %.0f%% at worst"
                    + " (the floor is %.0f%%, and a city all selling at once reaches it)%n",
                    usedDearest * 100, usedCheapest * 100,
                    HouseholdBalance.USED_CAR_FLOOR * 100);
        }

        Healthcare hc = g.getHealthcare();
        out.printf("  healthcare: $%,.0fk a month, %.0f%% covered by fees%n",
                hc.getGrossCost(), hc.getCostRecovery() * 100);
        /*
         * THE PRICE AT THE CLINIC DOOR (2026-09-19): the two dials, what the
         * premium raised, who the fee turned away, the coverage the sick rate
         * actually read, and the deaths per thousand a year - the figures the
         * ensemble table is read off.
         */
        {
            TaxPolicy dials = g.getEconomyManager().getTaxPolicy();
            HouseholdBalance hb = g.getHouseholdBalance();
            out.printf("  the price at the door: fees x%.2f (break-even x%.2f), premium %.2f%% raised $%,.0fk;"
                    + " priced out last month %,.0f (childcare %,.0f / general %,.0f / senior %,.0f),"
                    + " %,.0f a month over the run, fees skipped $%,.0fk last month and $%,.0fk over the run;"
                    + " could pay: childcare %.0f%% general %.0f%% senior %.0f%%, general coverage %.0f%%;"
                    + " deaths %.2f per 1,000 a year%n",
                    dials.getHealthFeeScale(), hc.breakEvenScale(),
                    dials.getHealthPremiumRate() * 100, g.getEconomyManager().getHealthPremiums(),
                    hc.getPricedOutTotal(), hc.getPricedOut(CareType.CHILDCARE),
                    hc.getPricedOut(CareType.GENERAL), hc.getPricedOut(CareType.SENIOR),
                    monthsObserved > 0 ? pricedOutSum / monthsObserved : 0,
                    hb.getCareSkipped(), careSkippedSum,
                    hc.getAffordability(CareType.CHILDCARE) * 100,
                    hc.getAffordability(CareType.GENERAL) * 100,
                    hc.getAffordability(CareType.SENIOR) * 100,
                    g.getHealth().getCoverage() * 100,
                    personMonths > 0 ? allDeaths / personMonths * 12 * 1000 : 0);
        }
        out.printf("  funerals: %,.0f buried and %,.0f cremated last month,"
                + " %,.0f plots used, %,.0f lying unburied%n",
                hc.getBurials(), hc.getCremations(), hc.getPlotsUsed(), hc.getUnburied());
        /*
         * THE PRICE OF A PLACE (2026-09-21): the four dials, who is studying,
         * who has graduated, what is owed and what the loans brought in -
         * the figures the setting's ensemble table is read off. Only when an
         * education property was set, so the default run's output is the
         * run as it has always been.
         */
        if (educationSet) {
            TaxPolicy dials = g.getEconomyManager().getTaxPolicy();
            Education schools = g.getEducation();
            HouseholdBalance hb = g.getHouseholdBalance();
            double graduated = 0;
            for (double v : schools.getEverGraduated()) graduated += v;
            double studying = 0;
            for (EducationType type : EducationType.values()) {
                if (type.isAdult()) studying += schools.studentBody(type);
            }
            out.printf("  the price of a place: tuition x%.2f, grant %s at %s, loans at %.2f%%;"
                    + " %,.0f adults studying, %,.0f ever graduated to a higher band;"
                    + " student debt $%,.0fk owed (%,.0fk by graduates), grants $%,.0fk last month and $%,.0fk over the run,"
                    + " interest $%,.1fk last month and $%,.0fk over the run; schools %,.0f fees, %,.0f forgiven, hunger %.1f%%%n",
                    dials.getTuitionScale(), dials.getGrantBasis(),
                    dials.getGrantBasis() == TaxPolicy.GrantBasis.FIXED
                            ? String.format("$%,.0f at founding prices", dials.getGrantAmount() * 1000)
                            : String.format("%.0f%%", dials.getGrantAmount() * 100),
                    dials.getStudentLoanRate() * 100,
                    studying, graduated,
                    hb.totalStudentDebt(), hb.totalGraduateDebt(),
                    g.getEconomyManager().getStudentGrants(), grantsSum,
                    g.getStudentLoanInterest(), studentInterestSum,
                    schools.getFees(), schools.getSubsidy(), hb.getHungerRate() * 100);
        }

        /*
         * THE BANK, which nobody in this harness ever builds.
         *
         * Printed because it is the one building the private sector is meant to
         * put up entirely on its own initiative - the player here never orders
         * one - so this line is the only place a run says whether that actually
         * happens. A run ending with no branches (and, until 0.7.7, a full
         * premium) is the advisor failing to notice, and it is invisible in
         * every other figure.
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
        // The rule and the dial, both, when the dial's stop is what binds -
        // the monetary page's reading since 2026-09-21 (DebtManager.ruleRate).
        double ruleSays = g.getDebtManager().ruleRate(g.getPriceIndex().inflation());
        double dialStops = g.getDebtManager().advisedPolicyRate(g.getPriceIndex().inflation());
        out.printf("  monetary policy: rate %.2f%% (%s on %.1f%% inflation);"
                + " the real rate differential is %+.2f points and pulls the currency %+.2f%n",
                g.getDebtManager().getPolicyRate() * 100,
                Math.abs(ruleSays - dialStops) > 1e-9
                        ? String.format("the rule would set %.2f%%, the dial stops at %.2f%%",
                                ruleSays * 100, dialStops * 100)
                        : String.format("the rule advises %.2f%%", dialStops * 100),
                g.getPriceIndex().inflation() * 100,
                fx.getRealRateDifferential() * 100, fx.ratePressure());
        if (POLICY_RATE != null) {
            out.printf("  ...HELD at %.2f%% from month %d to the end by -Dplaytest.policyRate,"
                    + " not set by the advisor's rule (see THE RATE, HELD)%n",
                    POLICY_RATE * 100, ForeignAccounts.SETTLING_MONTHS + 1);
        } else if (g.getDebtManager().isAutopilot()) {
            out.printf("  ...SET BY THE RULE every month by the game's own autopilot"
                    + " (-Dplaytest.autopilot), not by the advisor%n");
        }
        // Only when set, so an unset run prints the lines it always did.
        if (INFLATION_TARGET != null) {
            out.printf("  ...the rule AIMING AT %s inflation, set by -Dplaytest.inflationTarget"
                    + " (see THE TARGET, SET)%n",
                    DebtManager.targetWords(g.getDebtManager().getInflationTarget()));
        }
        // Both instruments, side by side on purpose: the headline is what the
        // band is doing and the realised figure is what the level did. They
        // disagreed for weeks on this line and nobody put them together.
        out.printf("  the world: prices %.3f since founding, headline %.1f%%/yr,"
                + " realised %.1f%%/yr; parity is %.3f and the rate is %.3f (%+.0f%% off it)%n",
                w.getPriceLevel(), w.getInflation() * 100, w.realisedInflation() * 100,
                fx.getParity(), fx.getRate(), fx.deviationFromParity() * 100);
        // Averaged as the index compounds, not as a mean of twelve-month
        // readings: the rate that takes founding prices to these.
        out.printf("  the currency over the run: its dearest dollar %.3f local (m%d), %d months weaker than"
                + " %.0fx parity; prices averaged %+.2f%%/yr%n",
                peakRate, peakRateMonth, monthsFarFromParity, FAR_FROM_PARITY,
                (Math.pow(Math.max(1e-9, g.getPriceIndex().getIndex()), 12.0 / Math.max(1, g.getMonth())) - 1) * 100);
        // The guard and the dial's path (0.7.2): see THE CURRENCY'S GUARD AND THE DIAL'S PATH.
        int pastOldStop = 0;
        for (double d : dialPath) if (d > OLD_DIAL_STOP + 1e-9) pastOldStop++;
        out.printf("  ...at its guard (%.0f local per USD) %d month(s); the dial over the run: min %.2f%%, median %.2f%%,"
                + " max %.2f%%, %d month(s) above %.0f%%; inflation's median reading %+.1f%%/yr%n",
                fx.getMaxRate(), monthsAtGuard,
                dialPath.stream().mapToDouble(Double::doubleValue).min().orElse(0) * 100,
                median(dialPath) * 100,
                dialPath.stream().mapToDouble(Double::doubleValue).max().orElse(0) * 100,
                pastOldStop, OLD_DIAL_STOP * 100, median(inflationPath) * 100);
        /*
         * THE DEMAND CHANNEL AND WHAT IT LEAVES SAVED (0.7.3). The spend
         * factor's path over the run, and the households' saving rate at the
         * end: everything they hold - savings, the city's paper at their
         * book, the dollars abroad at the month's rate - over a year of their
         * disposable income. A stock over a flow, so it says how many years
         * of income the city's families have put by.
         */
        HouseholdBalance putBy = g.getHouseholdBalance();
        double heldPaper = putBy.totalPaper() * putBy.getPaperRatio();
        double heldAbroad = putBy.totalAbroadUsd() * fx.getRate();
        double disposableYear = g.getHouseholds().getDisposableIncome() * 12;
        out.printf("  the demand channel: savers earn %+.2f%% real at the end, so the households plan %.3f of"
                        + " what they would spend above a basket at zero; over the run min %.3f, median %.3f,"
                        + " max %.3f (lowest m%d, highest m%d)%n",
                g.realDepositRate() * 100, g.spendFactor(),
                spendPath.isEmpty() ? 1 : spendLow, median(spendPath), spendPath.isEmpty() ? 1 : spendHigh,
                spendLowMonth, spendHighMonth);
        out.printf("  ...the bank's margin held its savers under the rate it chose in %d month(s) (first m%d, last m%d);"
                        + " it passed on %.2f of the policy rate on average (%d months with a dial over 0.1%%),"
                        + " a slope of %.2f over the run%n",
                depositCappedMonths, depositCappedFirst, depositCappedLast,
                shareMonths > 0 ? shareSum / shareMonths : 0, shareMonths, depositBeta());
        out.printf("  the households' saving rate: %.2f years of disposable income - $%,.0fk saved, $%,.0fk of"
                        + " the city's paper, $%,.0fk abroad, against $%,.0fk of disposable income a month%n",
                disposableYear > 0 ? (putBy.totalSavings() + heldPaper + heldAbroad) / disposableYear : 0,
                putBy.totalSavings(), heldPaper, heldAbroad, disposableYear / 12);
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
                g.getSectors().retail().getStoreSellPrice(),
                g.getSectors().retail().getImportPrice(),
                g.getSectors().retail().getFoodPrice());
        /*
         * THE FOOD CHAIN, FROM THE GROUND UP (2026-09-13). Which farms are
         * standing tells the whole story of the tenth sector: fields while the
         * ground is cheap, glass once it is not. See sectors.Agriculture.
         */
        ham.citybuildersim.sectors.Agriculture fields = g.getSectors().agriculture();
        BuildingManager bm2 = g.getBuildingManager();
        out.printf("  the fields: %d mixed, %d grain, %d under glass on %,.0f acres"
                + " - %.0f%% of what the city eats, crops $%.4f (floor %.4f ceiling %.4f)%n",
                bm2.getQuantity(bm2.getTemplateByName("Mixed Farm").getId()),
                bm2.getQuantity(bm2.getTemplateByName("Grain Farm").getId()),
                bm2.getQuantity(bm2.getTemplateByName("Greenhouse Complex").getId()),
                fields.getLandSqFt() / 43560,
                fields.getSelfSufficiency(g) * 100,
                g.getMarkets().get(Good.CROPS).getLocalPrice(),
                g.getMarkets().get(Good.CROPS).floor(),
                g.getMarkets().get(Good.CROPS).ceiling());
        out.printf("  ...land tax is %.0f%% of what they sold and wages %.0f%%;"
                + " the mills' crop bill is %.0f%% of theirs; ground is $%.2f/sqft%n",
                fields.groundShare() * 100, fields.payrollShare() * 100,
                g.getSectors().industry().cropShare() * 100,
                g.getEconomyManager().getLandPricePerSqFt() * 1000);
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
        out.printf("  the level has been between %.3f (m%d) and %.3f (m%d) - a %.2fx swing%n",
                px.getTrough(), px.getTroughMonth(), px.getPeak(), px.getPeakMonth(), px.swing());
        out.printf("  prices: index %.3f since founding (%.0f%% food / %.0f%% rent),"
                + " inflation %+.1f%%/yr; shelf carries a %.2fx scarcity mark-up%n",
                px.getIndex(), px.getFoodWeight() * 100, px.getRentWeight() * 100,
                px.inflation() * 100,
                g.getSectors().retail().getScarcityMultiple());
        ham.citybuildersim.sectors.RealEstate rentCh = g.getSectors().realEstate();
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
                String.format("%,.0f", rentCh.statement().maintenance));
        out.printf("  wages lifted %.1f%%, the floor is worth %s a month in today's money%n",
                (g.getLabourMarket().getCostOfLiving() - 1) * 100,
                String.format("%.3f", g.getLabourMarket().cashMinimumWage()));
        // ...and against the index they chase, under -Dplaytest.wages.
        if (WAGES) out.println(wageEra(g));
        /*
         * TWO DELIVERY NUMBERS, AND THE GAP BETWEEN THEM IS THE POINT
         * (2026-09-17). The first is what the city ASKED for against what it
         * got; the second is the shops' own performance against the queue they
         * decided they could serve. This line used to print only the second and
         * call it "of what was planned", which read as a comfortable three
         * quarters in a city asking for a hundred and twenty-two times what it
         * received. A basket is one person-month of food, so the shops cap
         * demand in PEOPLE - nobody eats twice - and every dollar past that has
         * nowhere in this game to go. See Retail.getHouseholdShare().
         */
        ham.citybuildersim.sectors.Retail shop =
                (ham.citybuildersim.sectors.Retail) g.getSectors().byKey("Retail");
        out.printf("  hunger: %.0f%% of people short; the city got %.1f%% of the groceries it asked for"
                + " (%,.0f baskets wanted, %,d sold to %,d people)%n",
                g.getHouseholdBalance().getHungerRate() * 100,
                shop.getHouseholdShare() * 100, shop.getHouseholdWant(),
                shop.getProductsSold(), g.getPopulationManager().getPopulation());
        out.printf("  ...and the shops filled %.0f%% of the queue they could serve at all,"
                + " running at %.0f%% (roads %.0f%%)%n",
                shop.getSupplyRatio() * 100, shop.getOperatingRate() * 100,
                g.getInfrastructureManager().getThroughputRatio() * 100);
        /*
         * ...AND THE SECOND DOOR, on the line under the first one because that
         * is the comparison worth making: a city short of shops can be a city
         * with kitchens instead, and a city with neither is a city eating at
         * home whether it wants to or not.
         */
        ham.citybuildersim.sectors.Restaurants kitchens = g.getSectors().restaurants();
        out.printf("  ...and %,.0f meals out, %,.0f asked for against %,d of tables"
                + " at %.2fx the food (%,.0f person-months fed, %.1f%% of the city)%n",
                kitchens.getServed(), kitchens.getWanted(), kitchens.seats(),
                kitchens.getMargin(),
                kitchens.getServed() * ham.citybuildersim.sectors.Restaurants.PERSON_MONTHS_PER_MEAL,
                g.getPopulationManager().getPopulation() > 0
                        ? kitchens.getServed()
                            * ham.citybuildersim.sectors.Restaurants.PERSON_MONTHS_PER_MEAL
                            / g.getPopulationManager().getPopulation() * 100 : 0);
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
        // Both, since the vault is kept in dollars (2026-09-21): what it IS,
        // and what it is worth at home today.
        out.printf("  the vault: %s%,.0fk (%s%,.0fk at today's rate), %s months of imports at %s%,.0fk/mo%n",
                Currency.FOREIGN_SYMBOL, fx.getReservesUsd(),
                g.getCurrency().qualifiedSymbol(), fx.getReserves(),
                fx.importCover() == Double.MAX_VALUE ? "inf"
                        : String.format("%.1f", fx.importCover()),
                g.getCurrency().qualifiedSymbol(), fx.monthlyImports());
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
        /*
         * THE CITY'S CAPITAL IN ITS BANK, ITS STAKE AND ITS FUND (0.7.14),
         * each line stating its setting, as the rollover's does.
         */
        TreasuryFund fundEnd = g.getFund();
        double resolvedPaid = 0, resolvedFromCash = 0, resolvedAdvanced = 0, ownersLostHh = 0, ownersLostWorld = 0,
                ownersLostFund = 0;
        for (TreasuryFund.Resolution r : fundEnd.getResolutions()) {
            resolvedPaid += r.paid(); resolvedFromCash += r.fromCash(); resolvedAdvanced += r.advanced();
            ownersLostHh += r.householdsValue(); ownersLostWorld += r.worldValue(); ownersLostFund += r.fundValue();
        }
        out.printf("  it failed %d time(s); the city put $%,.0fk of capital back in: $%,.0fk resolving it and $%,.0fk"
                        + " of preferred%n",
                bnk.getFailures(), resolvedPaid + fundEnd.getPreferredBought(), resolvedPaid, fundEnd.getPreferredBought());
        out.printf("  the rescues over the run (%s): %d resolution(s)%s paid $%,.0fk, $%,.0fk from the treasury's cash"
                        + " and $%,.0fk for the central bank to advance; the old owners lost $%,.0fk at the last price"
                        + " (households $%,.0fk, abroad $%,.0fk, the fund's own $%,.0fk); holes $%,.0fk in all%n",
                RESCUE_AUTO ? "AUTOMATIC" : "BUTTON", fundEnd.getResolutions().size(),
                RESCUE_AUTO ? "" : String.format(" (%d press(es) of the button)", buttonPresses),
                resolvedPaid, resolvedFromCash, resolvedAdvanced, ownersLostHh + ownersLostWorld + ownersLostFund,
                ownersLostHh, ownersLostWorld, ownersLostFund, bnk.getShortfallLifetime());
        {
            StringBuilder st = new StringBuilder();
            for (Stake s : stakes) {
                st.append(String.format(" m%d %s -> 5y %s, 10y %s, 25y %s, lowest %s%s%s%s;", s.month, pct(s.at),
                        pct(s.five), pct(s.ten), pct(s.twentyFive), pct(s.lowest),
                        s.under50 >= 0 ? String.format(", under half m%d", s.under50) : ", never under half",
                        s.under10 >= 0 ? String.format(", under a tenth m%d", s.under10) : ", never under a tenth",
                        s.endedBy >= 0 ? String.format(" (resolved again m%d)", s.endedBy) : ""));
            }
            out.printf("  the city's stake in the bank: %s at the end, %,.3f of %,.3f shares;%s%n",
                    pct(g.cityStakeInBank()), g.getEquity().getCityShares(Equity.BANK), g.getEquity().getShares(Equity.BANK),
                    stakes.isEmpty() ? " never rescued" : st.toString());
        }
        out.printf("  the preferred over the run (%s): %d offer(s), %d accepted (%d after borrowing $%,.0fk on the"
                        + " funding page), %d declined; bought $%,.0fk; dividends $%,.0fk paid, $%,.0fk in arrears at"
                        + " the end; redeemed $%,.0fk; warrants bought back $%,.0fk, exercised %d time(s) into shares"
                        + " worth $%,.0fk when taken; new shares sold to repay $%,.0fk (%d repayment(s) left part-paid for a month);"
                        + " $%,.0fk outstanding at the end%n",
                PREFERRED_ACCEPT ? "ACCEPT" : "DECLINE", fundEnd.getOffersMade(), fundEnd.getOffersAccepted(),
                offersFunded, offerFundingRaised, fundEnd.getOffersDeclined(), fundEnd.getPreferredBought(),
                fundEnd.getPreferredDividends(), bnk.getPreferredArrears(), fundEnd.getPreferredRedeemed(),
                fundEnd.getWarrantsBoughtBack(), warrantExercises, warrantValueTaken, bnk.getRepaymentRaisedLifetime(),
                bnk.getRepaymentShortLifetime(), bnk.preferredOutstanding());
        {
            StringBuilder book = new StringBuilder();
            for (int c = 0; c < Equity.COMPANIES.length; c++) {
                double shares = g.getEquity().getShares(c);
                double now = shares > 0 ? g.getEquity().getCityMarketShares(c) / shares : 0;
                if (now > 0 || mostHeld[c] > 0) {
                    book.append(String.format(" %s %.1f%% (most %.1f%%);", Equity.COMPANIES[c], now * 100, mostHeld[c] * 100));
                }
            }
            out.printf("  the city's fund over the run (dial %.0f%%): worth $%,.0fk at the end (cash $%,.0fk, shares"
                            + " $%,.0fk, bonds $%,.0fk, the rescue book $%,.0fk), at most $%,.0fk; paid in $%,.0fk from"
                            + " the surplus and $%,.0fk from the treasury's cash; its withdrawal at %s a month $%,.0fk paid and"
                            + " $%,.0fk short; dividends $%,.0fk (the rescue book's $%,.0fk), coupons $%,.0fk; bought"
                            + " shares $%,.0fk and bonds $%,.0fk, sold $%,.0fk and $%,.0fk; its cash a mean %.1f%% of"
                            + " its value; its market book at the end:%s%n",
                    FUND_DIAL * 100, g.fundValue(), fundEnd.getCash(), g.fundMarketSharesValue(), g.fundBondsValue(),
                    g.fundRescueValue(), fundPeak, fundEnd.getPaidInFromSurplus(), fundEnd.getPaidInFromCash(),
                    DecisionLog.pct2(fundEnd.getWithdrawal()), fundEnd.getTransfersPaid(), fundEnd.getTransfersShort(),
                    fundEnd.getDividendsMarket() + fundEnd.getDividendsRescue(), fundEnd.getDividendsRescue(),
                    fundEnd.getCoupons(), fundEnd.getSharesBought(), fundEnd.getBondsBought(),
                    fundEnd.getSharesSold(), fundEnd.getBondsSold(),
                    fundMonths > 0 ? fundCashShareSum / fundMonths * 100 : 0,
                    book.length() > 0 ? book.toString() : " nothing");
            // ...and what its holdings cost (0.7.39, FundLedger): counted over the run, and the identity's worst month.
            FundLedger ledger = fundEnd.getLedger();
            double unrealized = 0, cost = 0;
            for (FundView.Position p : FundView.positions(g)) {
                if (!(p.isShare() || p.isBond()) || Double.isNaN(p.unrealized())) continue;
                unrealized += p.unrealized();
                cost += p.acb();
            }
            out.printf("  the fund's cost basis over the run: %d lot-month(s) traded by the rule and %d by the hand;"
                            + " %d bond lot(s) repaid and %d written down; %d rescue(s); realized $%,.0fk; unrealized"
                            + " $%,.0fk at the end on a cost of $%,.0fk; its purchases and proceeds off the fund's"
                            + " counters by $%,.2fk and $%,.2fk; the identity's worst month off by %.1e of what it held%n",
                    ledgerRuleRows, ledgerHandRows, ledgerMatured, ledgerWrittenDown, ledgerRescues, ledger.realized(),
                    unrealized, cost, ledger.purchases() - fundEnd.getSharesBought() - fundEnd.getBondsBought(),
                    ledger.proceeds() - fundEnd.getSharesSold() - fundEnd.getBondsSold() - fundEnd.getPrincipal(),
                    ledgerWorst);
        }
        if (FOUNDING == Founding.Preset.INSANE) {
            out.printf("  the Insane founding: the land bond US$%,.0fk at %.0f%% for %d years; the village invoiced"
                            + " $%,.0fk; the day-0 quote for it: the %d-year bond %s, the %d-month note %s; borrowed"
                            + " $%,.0fk in its first year in %d issue(s) on the funding page%n",
                    Founding.landBondUsd(), Founding.INSANE_LAND_COUPON * 100, Founding.INSANE_LAND_YEARS,
                    villageInvoice, Game.BUILD_BOND_YEARS,
                    dayZeroBond == null ? "-" : String.format("at %.2f%% for $%,.0fk of face, bringing $%,.0fk",
                            dayZeroBond.marketRate() * 100, dayZeroBond.faceValue(), dayZeroBond.cashReceived()),
                    Game.BUILD_NOTE_MONTHS,
                    dayZeroNote == null ? "-" : String.format("at %.2f%% for $%,.0fk of face, bringing $%,.0fk",
                            dayZeroNote.marketRate() * 100, dayZeroNote.faceValue(), dayZeroNote.cashReceived()),
                    firstYearBorrowed, insaneBorrowings);
        }
        out.printf("  its equity at the end: paid in $%,.0fk, retained $%,.0fk; paid in at its lowest $%,.0fk (m%d);"
                        + " the two off its equity by at most $%.6fk%n",
                bnk.paidInCapital(), bnk.retainedEarnings(),
                lowestPaidInMonth < 0 ? 0 : lowestPaidIn, Math.max(0, lowestPaidInMonth), worstSplitOff);
        out.printf("  banking: %,.0f branch(es), $%,.0fk deposited, $%,.0fk lent,"
                + " %.0f%% of capacity, prime %.2f%% on a dial of %.2f%%%n",
                bnk.getBranches(), bnk.getDeposits(), bnk.getBook(),
                Math.min(999, bnk.strain()) * 100,
                bnk.prime(g.getDebtManager().getPolicyRate()) * 100,
                g.getDebtManager().getPolicyRate() * 100);
        out.println(bankEra(g));
        {
            // Its capital over the run (0.7.8) - see THE BANK'S CAPITAL, YEAR BY YEAR.
            int growth = 0;
            for (double[] y : bankYears) if (y[1] > 0) growth++;
            out.printf("  the bank's capital over %d growth year(s) of %d: ratio %s, target %s, return on equity %s,"
                    + " provisions over its loans %s, paid out %s of its profit%n",
                    growth, bankYears.size(), yearSpread(2, 100), yearSpread(3, 100), yearSpread(4, 100),
                    yearSpread(5, 100), yearSpread(6, 100));
            out.printf("  ...over the run: dividends $%,.0fk, its own shares bought back $%,.0fk and issued $%,.0fk,"
                    + " provisions $%,.0fk (%.2f%% of its average loans a year; written off %.2f%%);"
                    + " lending rationed %d month(s), only to keep borrowers going %d;"
                    + " rebuilding %d, paying out %d, returning excess %d; worst year on record %.2f%% of its book,"
                    + " so a target of %.2f%% (%s); allowance $%,.0fk at the end%n",
                    runDividends, runBuybacks, runIssued, runProvisions,
                    runLoanMonths > 0 ? runProvisions / (runLoanMonths / 12) * 100 : 0,
                    runLoanMonths > 0 ? runWrittenOff / (runLoanMonths / 12) * 100 : 0,
                    rationedMonths, keepGoingMonths,
                    rebuildingMonths, payingMonths, returningMonths, bnk.getWorstLossRate() * 100,
                    bnk.capitalTarget() * 100, bnk.payoutDecision(), bnk.getAllowance());
            // How the defaults arrived (0.7.8) - see HOW THE DEFAULTS ARRIVE.
            out.printf("  the defaults over the run: %d whole-sector backstop(s); a slice written off in %d month(s),"
                    + " news in %d, $%,.0fk in slices; the largest month $%,.0fk, %.1f%% of the equity it opened"
                    + " with (m%d); %d sector-month(s) past the watch line and %d past the default point, the"
                    + " longest spell %d months (%s, to m%d), $%,.0fk lent to sectors past the line;"
                    + " %.1f%% of what was written off was already set aside; %d notice(s) raised%n",
                    backstops, sliceMonths, newsMonths, sliceWrittenOff, worstWriteOff, worstWriteOffShare * 100,
                    worstWriteOffMonth, pastWatchMonths, pastTriggerMonths, longestWatchSpell, longestWatchSector,
                    longestWatchMonth, lentPastWatch,
                    runWrittenOff > 0 ? runAllowanceUsed / runWrittenOff * 100 : 0, defaultNotices);
            // The price and the plant (0.7.8, round 2) - see countPriceAndPlant().
            out.printf("  the price and the plant over the run: %d loan(s) written past the watch line, %d past 1.0"
                    + " ($%,.0fk, %d of them projects; rates averaging %.2f%%, at most %.2f%%) and %d past the default"
                    + " point ($%,.0fk, averaging %.2f%%, at most %.2f%%); plans declined on their price %s;"
                    + " plant retired and sold as material: %d building(s) by the distress rule and %d by the spare-capacity"
                    + " rule, %,.0f units of which the builders bought %,.0f for $%,.0fk ($%,.0fk of it the distress rule's)"
                    + " and built with %,.0f, holding %,.0f at the end%n",
                    loansPastWatch, loansPastOne, lentPastOne, projectsPastOne,
                    loansPastOne > 0 ? rateSumPastOne / loansPastOne * 100 : 0, rateMaxPastOne * 100,
                    loansPastT, lentPastT, loansPastT > 0 ? rateSumPastT / loansPastT * 100 : 0, rateMaxPastT * 100,
                    refusedOnPrice.isEmpty() ? "never" : refusedOnPrice.toString(),
                    salvageBuildingsDistress, salvageBuildingsSpare, salvageUnits, salvageUnitsBought, salvagePaid,
                    salvagePaidDistress, salvageUsed, g.getSectors().construction().getSalvage());
            // The landlords' mortgages and the city's insurance (0.7.11) - see countMortgages().
            BusinessDebtManager lenderAtEnd = g.getEconomyManager().getBusinessDebtManager();
            String re = g.getSectors().realEstate().key();
            out.printf("  the landlords' mortgages over the run: %d written for $%,.0fk of loans, %d renewed, %d fell due"
                    + " unrenewed; at the end %d owed, $%,.0fk at %.2f%% (and $%,.0fk of other debt); the insurance took"
                    + " $%,.0fk of premiums and paid $%,.0fk of claims (%s $%,.0fk); what the landlords did, by month: %s%n",
                    mortgagesWritten, mortgagesLent, lenderAtEnd.getRenewedLifetime(), lenderAtEnd.getFallenDueLifetime(),
                    lenderAtEnd.getMortgageCount(re), lenderAtEnd.getMortgagePrincipal(re),
                    lenderAtEnd.getMortgageRate(re) * 100,
                    lenderAtEnd.getPrincipal(re) - lenderAtEnd.getMortgagePrincipal(re), premiumsRun, claimsRun,
                    premiumsRun >= claimsRun ? "ahead" : "behind", Math.abs(premiumsRun - claimsRun), houseReasons);
            // The leverage ratio and the branches (0.7.11, round 2) - see countLeverage().
            Bank bankAtEnd = g.getBank();
            out.printf("  the leverage ratio over the run: the larger requirement in %d of %d month(s) with a bank;"
                    + " at its lowest %.2f%% (m%d); at the end %.2f%% against %.2f%% on the risk-weighted book,"
                    + " targets %.2f%% and %.2f%%; the landlords held for the bank's leverage in %d month(s);"
                    + " branches opened %d %s, closed %d, standing %d%n",
                    leverageMonths, levBankMonths, lowestLeverage < Double.MAX_VALUE ? lowestLeverage * 100 : 0,
                    lowestLeverageMonth, Math.min(999, bankAtEnd.leverageRatio()) * 100,
                    Math.min(999, bankAtEnd.capitalRatio()) * 100, bankAtEnd.leverageTarget() * 100,
                    bankAtEnd.capitalTarget() * 100, mortgagesRefusedForCapital, branchesOpened, openedByStance,
                    branchesClosedSeen, Math.round(bankAtEnd.getBranches()));
            // The businesses' bonds (0.7.12) - see countBonds().
            BondMarket bmEnd = g.getBondMarket();
            out.printf("  the bonds over the run: %d issued for $%,.0fk (costs $%,.0fk); the businesses' debt at most %.1f%%"
                    + " bonds (m%d), bonds owed in %d month(s); at the end %d outstanding, $%,.0fk: households $%,.0fk,"
                    + " the bank $%,.0fk, companies $%,.0fk, the world $%,.0fk; coupons paid households $%,.0fk, the bank"
                    + " $%,.0fk, companies $%,.0fk, abroad $%,.0fk; written off households $%,.0fk, the bank $%,.0fk,"
                    + " companies $%,.0fk, the world $%,.0fk; recovered on defaulted loans %.1f%% of $%,.0fk and on"
                    + " defaulted bonds %.1f%% of $%,.0fk; the book traded $%,.0fk: %.1f%% of what was offered for sale"
                    + " filled, and %d of %d sell orders waited unfilled; the world bought $%,.0fk and sold $%,.0fk;"
                    + " households traded with households %,d times for $%,.0fk%n",
                    bmEnd.getLifeIssues(), bmEnd.getLifeIssued(), bmEnd.getLifeCosts(), bondPeakShare * 100,
                    bondPeakShareMonth, bondMonthsWithBonds, bmEnd.getBonds().size(), bmEnd.totalFace(),
                    bmEnd.faceHeldByHouseholds(), bmEnd.faceHeldByBank(), bmEnd.faceHeldByCompanies(),
                    bmEnd.faceHeldByWorld(), bmEnd.getLifeCouponsHouseholds(), bmEnd.getLifeCouponsBank(),
                    bmEnd.getLifeCouponsCompanies(), bmEnd.getLifeCouponsAbroad(), bmEnd.getLifeLossHouseholds(),
                    bmEnd.getLifeLossBank(), bmEnd.getLifeLossCompanies(), bmEnd.getLifeLossWorld(),
                    bondRunLoansDefaulted > 0 ? (1 - bondRunLoansLost / bondRunLoansDefaulted) * 100 : 0,
                    bondRunLoansDefaulted,
                    bondRunBondsDefaulted > 0 ? (1 - bondRunBondsLost / bondRunBondsDefaulted) * 100 : 0,
                    bondRunBondsDefaulted, bmEnd.getLifeVolume(),
                    bmEnd.getLifePostedSell() > 0 ? bmEnd.getLifeFilledSell() / bmEnd.getLifePostedSell() * 100 : 0,
                    bmEnd.getLifeSellsWaited(), bmEnd.getLifeSellsPosted(), bmEnd.getLifeWorldBought(),
                    bmEnd.getLifeWorldSold(), bmEnd.getLifeBetweenHouseholdsTrades(), bmEnd.getLifeBetweenHouseholds());
            // Its own shares and its desk against its capital (0.7.8, round 4) - see countCapitalLimits().
            out.printf("  its own shares and its desk against its capital: bought back $%,.0fk a year over the run"
                    + " (a growth year's a median %s of the equity it opened with); the desk's inventory at the close"
                    + " a median %.1f%% of its equity (90th percentile %.1f%%, most %.1f%%, m%d), a mean $%,.0fk while"
                    + " a bank stood; its trading result over the run $%,.0fk%n",
                    g.getMonth() > 0 ? runBuybacks / (g.getMonth() / 12.0) : 0, yearSpread(10, 100),
                    quantile(deskOverEquity, .5) * 100, quantile(deskOverEquity, .9) * 100,
                    deskOverEquityMax * 100, deskOverEquityMaxMonth,
                    deskInventoryMonths > 0 ? deskInventorySum / deskInventoryMonths : 0, deskPnlRun);
            // The shares on the book (0.7.12 round 2) - see countShares().
            Exchange exEnd = g.getExchange();
            out.printf("  the shares over the run: the book traded $%,.0fk in %,d trades; %.1f%% of what was offered for"
                    + " sale filled (by value at the price), and %d of %d sell orders waited unfilled; households traded"
                    + " with households %,d times for $%,.0fk; leavers offered $%,.0fk and were paid $%,.0fk; a listed"
                    + " company traded in %.1f%% of its months; the last trade over fair value a median %.3f (10th"
                    + " percentile %.3f, 90th %.3f); ordinary dividends $%,.0fk (no path pays a special one since round 4);"
                    + " what sold, by seller, of what it asked: the desk %.1f%% of $%,.0fk, the world %.1f%% of $%,.0fk,"
                    + " leavers %.1f%% of $%,.0fk, households %.1f%% of $%,.0fk%n",
                    exEnd.getLifeTurnover(), exEnd.getLifeTrades(),
                    exEnd.getLifePostedSellValue() > 0 ? exEnd.getLifeFilledValue() / exEnd.getLifePostedSellValue() * 100 : 0,
                    exEnd.getLifeSellsWaited(), exEnd.getLifeSellsPosted(),
                    exEnd.getLifeBetweenHouseholdsTrades(), exEnd.getLifeBetweenHouseholds(),
                    exEnd.getLifeEmigrantsOffered(), exEnd.getLifeEmigrantsPaid(),
                    shareListedMonths > 0 ? 100.0 * shareTradedMonths / shareListedMonths : 0,
                    quantile(sharePriceOverFair, .5), quantile(sharePriceOverFair, .1), quantile(sharePriceOverFair, .9),
                    ordinaryDividendsRun,
                    pct(exEnd.getLifeFilledSellBy(Exchange.BY_DESK), exEnd.getLifePostedSellBy(Exchange.BY_DESK)), exEnd.getLifePostedSellBy(Exchange.BY_DESK),
                    pct(exEnd.getLifeFilledSellBy(Exchange.BY_WORLD), exEnd.getLifePostedSellBy(Exchange.BY_WORLD)), exEnd.getLifePostedSellBy(Exchange.BY_WORLD),
                    pct(exEnd.getLifeFilledSellBy(Exchange.BY_EMIGRANTS), exEnd.getLifePostedSellBy(Exchange.BY_EMIGRANTS)), exEnd.getLifePostedSellBy(Exchange.BY_EMIGRANTS),
                    pct(exEnd.getLifeFilledSellBy(Exchange.BY_HOUSEHOLDS), exEnd.getLifePostedSellBy(Exchange.BY_HOUSEHOLDS)), exEnd.getLifePostedSellBy(Exchange.BY_HOUSEHOLDS));
            // The default point (0.7.12 round 2) - see countShares().
            StringBuilder bySector = new StringBuilder();
            for (java.util.Map.Entry<String, Double> e : refusedAtLineBySector.entrySet()) {
                bySector.append(String.format(" %s $%,.0fk;", e.getKey(), e.getValue()));
            }
            out.printf("  the default point over the run: the shortfall desk refused $%,.0fk in %d month(s)%s;"
                    + " $%,.0fk of mortgages fell due rather than renew; %d project month(s) refused%n",
                    refusedAtLineRun, refusedAtLineMonths, bySector.length() > 0 ? " (" + bySector.toString().trim() + ")" : "",
                    notRenewedAtLineRun, projectsRefusedAtLine);
            // The desk's caps and the cells' rebalancing (0.7.12 round 3).
            out.printf("  the desk's caps over the run: its bid held by its capital in %,d company-month(s), by POSITION_LIMIT"
                    + " in %,d, by BOOK_LIMIT in %,d, by the float in %,d; its book at fair value a median %.1f%% of the"
                    + " bank's equity (90th percentile %.1f%%, most %.1f%%) against BOOK_LIMIT %.0f%%, its largest holding"
                    + " a median %.1f%% (90th percentile %.1f%%, most %.1f%%) against POSITION_LIMIT %.0f%%; the cells"
                    + " offered $%,.0fk of shares to rebalance%n",
                    exEnd.getLifeDeskBound(Exchange.BOUND_CAPITAL), exEnd.getLifeDeskBound(Exchange.BOUND_POSITION),
                    exEnd.getLifeDeskBound(Exchange.BOUND_BOOK), exEnd.getLifeDeskBound(Exchange.BOUND_FLOAT),
                    quantile(deskBookAtFair, .5) * 100, quantile(deskBookAtFair, .9) * 100, maxOf(deskBookAtFair) * 100,
                    Exchange.BOOK_LIMIT * 100, quantile(deskLargestPosition, .5) * 100, quantile(deskLargestPosition, .9) * 100,
                    maxOf(deskLargestPosition) * 100, Exchange.POSITION_LIMIT * 100, exEnd.getLifeRebalanceSold());
            // ...and what it offered over them at fair value (0.7.12 round 4).
            out.printf("  the desk over its caps: month-ends over BOOK_LIMIT %.1f%%, over POSITION_LIMIT %.1f%%; it offered"
                    + " $%,.0fk over its caps at fair value and sold $%,.0fk of it; its trading result $%,.0fk%n",
                    shareOver(deskBookAtFair, Exchange.BOOK_LIMIT), shareOver(deskLargestPosition, Exchange.POSITION_LIMIT),
                    exEnd.getLifeExcessOffered(), exEnd.getLifeExcessSold(), deskPnlRun);
            // The companies' cash (0.7.12 round 4): what the buybacks leave in the till, and where it sits.
            EconomyManager emEnd = g.getEconomyManager();
            double tillsEnd = 0;
            for (String s : Sectors.KEYS) tillsEnd += Math.max(0, emEnd.getSectorCash(s));
            out.printf("  the companies' cash: at the end $%,.0fk in their tills, $%,.0fk abroad, $%,.0fk of other sectors'"
                    + " bonds, no shares; their tills a median $%,.0fk over the run (90th percentile $%,.0fk); over the"
                    + " run they earned $%,.0fk of deposit interest, $%,.0fk of bond coupons and $%,.0fk abroad; their"
                    + " buyback bids left a mean $%,.0fk unspent resting at the month's end%n",
                    tillsEnd, g.getOutwardInvestment().totalLocalValue(), g.getBondMarket().faceHeldByCompanies(),
                    quantile(companyTillsRun, .5), quantile(companyTillsRun, .9),
                    companyDepositInterestRun, g.getBondMarket().getLifeCouponsCompanies(),
                    g.getOutwardInvestment().getLifetimeInterest(),
                    g.getMonth() > 0 ? buybackRestingRun / g.getMonth() : 0);
            // Can't pay means default (0.7.12 round 4).
            StringBuilder why = new StringBuilder();
            for (java.util.Map.Entry<BusinessDebtManager.ShortReason, int[]> e : cannotPayByReason.entrySet()) {
                why.append(String.format(" %s %d ($%,.0fk);", e.getKey(), e.getValue()[0],
                        cannotPayForgivenByReason.getOrDefault(e.getKey(), 0.0)));
            }
            StringBuilder who = new StringBuilder();
            for (java.util.Map.Entry<String, int[]> e : cannotPayBySector.entrySet()) {
                who.append(String.format(" %s %d ($%,.0fk);", e.getKey(), e.getValue()[0],
                        cannotPayForgivenBySector.getOrDefault(e.getKey(), 0.0)));
            }
            // Buy only what it can pay for (round 6).
            int limitedN = 0;
            double[] forgone = new double[3];
            StringBuilder lim = new StringBuilder();
            for (java.util.Map.Entry<String, int[]> e : limitedBySector.entrySet()) {
                double[] f = forgoneBySector.getOrDefault(e.getKey(), new double[3]);
                limitedN += e.getValue()[0];
                for (int i = 0; i < 3; i++) forgone[i] += f[i];
                lim.append(String.format(" %s %d ($%,.0fk: stock $%,.0fk, inputs $%,.0fk, fleet $%,.0fk);",
                        e.getKey(), e.getValue()[0], f[0] + f[1] + f[2], f[0], f[1], f[2]));
            }
            StringBuilder shelf = new StringBuilder();
            for (java.util.Map.Entry<String, double[]> e : shelfShortBySector.entrySet()) {
                shelf.append(String.format(" %s $%,.0fk ($%,.0fk after a cut);", e.getKey(), e.getValue()[0], e.getValue()[1]));
            }
            StringBuilder after = new StringBuilder();
            for (java.util.Map.Entry<String, int[]> e : defaultsAfterCutBySector.entrySet()) {
                after.append(String.format(" %s %d;", e.getKey(), e.getValue()[0]));
            }
            StringBuilder onStock = new StringBuilder();
            for (java.util.Map.Entry<String, double[]> e : defaultsOnStockBySector.entrySet()) {
                onStock.append(String.format(" %s %.0f ($%,.0fk);", e.getKey(), e.getValue()[0], e.getValue()[1]));
            }
            out.printf("  what the cash-flow defaults were for: %,d of them unpaid within what the month paid for stock,"
                    + " $%,.0fk of it:%s %,d no larger than the costs of a bond the shortfall desk sold that month,"
                    + " $%,.0fk%n",
                    defaultsOnStock, defaultsOnStockSum, onStock.length() > 0 ? onStock.toString() : " none;",
                    defaultsOnBondCost, defaultsOnBondCostSum);
            out.printf("  bought only what it could pay for: the limit bound in %,d sector-month(s), $%,.0fk of orders"
                    + " not placed (stock $%,.0fk, inputs $%,.0fk, fleet $%,.0fk); by sector:%s the shelves could not"
                    + " meet $%,.0fk of sales:%s cash-flow defaults in the settle after a cut %,d ($%,.0fk unpaid):%s%n",
                    limitedN, forgone[0] + forgone[1] + forgone[2], forgone[0], forgone[1], forgone[2],
                    lim.length() > 0 ? lim.toString() : " none;",
                    shelfShortBySector.values().stream().mapToDouble(v -> v[0]).sum(),
                    shelf.length() > 0 ? shelf.toString() : " none;",
                    defaultsAfterCut, defaultsAfterCutSum, after.length() > 0 ? after.toString() : " none;");
            int overCeiling = 0, pastLine = 0;
            for (double x : luxuryLeverage) {
                if (x > BusinessDebtManager.MAX_LOAN_TO_ASSETS) overCeiling++;
                if (x > BusinessDebtManager.INSOLVENCY_TRIGGER) pastLine++;
            }
            out.printf("  Luxury Retail's leverage on its quarter: median %.2f, 10th percentile %.2f, 90th %.2f, most %.2f"
                    + " over %,d month(s) with debt; over MAX_LOAN_TO_ASSETS in %,d, past INSOLVENCY_TRIGGER in %,d%n",
                    quantile(luxuryLeverage, .5), quantile(luxuryLeverage, .1), quantile(luxuryLeverage, .9),
                    luxuryLeverage.isEmpty() ? 0 : maxOf(luxuryLeverage), luxuryLeverage.size(), overCeiling, pastLine);
            // Round 7: the money created, by sector; wages over revenue on interim loans; a new sector's first year.
            StringBuilder made = new StringBuilder();
            for (String s : Sectors.KEYS) {
                double f = g.getEconomyManager().getOverdraftForgivenTotal(s);
                if (f > 0) made.append(String.format(" %s $%,.0fk;", s, f));
            }
            StringBuilder spells = new StringBuilder();
            for (java.util.Map.Entry<String, int[]> e : wageSpells.entrySet()) {
                if (e.getValue()[1] >= 12) spells.append(String.format(" %s %d (longest %d months from m%d);",
                        e.getKey(), e.getValue()[0], e.getValue()[1], e.getValue()[2]));
            }
            StringBuilder banned = new StringBuilder();
            for (java.util.Map.Entry<String, Integer> e : bannedFirstYear.entrySet()) {
                banned.append(String.format(" %s (plant m%d, banned m%d);", e.getKey(), firstPlant.get(e.getKey()), e.getValue()));
            }
            out.printf("  the money created, by sector:%s spells of a year or more paying more in wages than it took in, on"
                    + " interim loans:%s new sectors: %d held plant, %d of them banned in their first year:%s%n",
                    made.length() > 0 ? made.toString() : " none;", spells.length() > 0 ? spells.toString() : " none;",
                    firstPlant.size(), bannedFirstYear.size(), banned.length() > 0 ? banned.toString() : " none");
            // Round 8: a new sector's first bill, read on a sheet valued before the lender reads it.
            StringBuilder firstBills = new StringBuilder();
            int firstBillInterim = 0;
            for (java.util.Map.Entry<String, int[]> e : firstBillDefault.entrySet()) {
                firstBillInterim += e.getValue()[1];
                firstBills.append(String.format(" %s (plant m%d, default m%d%s);", e.getKey(), firstPlant.get(e.getKey()),
                        e.getValue()[0], e.getValue()[1] > 0 ? ", lent in the interim" : ""));
            }
            out.printf("  a new sector's first bill: %d of the %d sectors that held plant defaulted on it, %d lent it in the interim:%s%n",
                    firstBillDefault.size(), firstPlant.size(), firstBillInterim, firstBills.length() > 0 ? firstBills.toString() : " none");
            StringBuilder shells = new StringBuilder();
            int shellN = 0;
            for (java.util.Map.Entry<String, Integer> e : shellDefaults.entrySet()) {
                shellN += e.getValue();
                shells.append(String.format(" %s %,d;", e.getKey(), e.getValue()));
            }
            out.printf("  ceiling defaults in the settle after a project loan: %,d; shells, a sector holding no plant that owes something:"
                    + " %,d sector-month(s), the longest %d months (%s from m%d); %,d cash-flow default(s) among them, $%,.1fk unpaid,"
                    + " $%,.1fk lent in the interim, by sector:%s%n",
                    afterProjectCeiling, shellMonths, shellLongest, shellLongestWho, shellLongestFrom, shellN, shellUnpaid, shellInterim,
                    shells.length() > 0 ? shells.toString() : " none");
            // Credit lines stay open (round 5): the doors the capital rule still shuts, and what the line lent past it.
            out.printf("  credit lines stay open: the capital rule on in %,d month(s); it refused a building's loan in"
                    + " %,d sector-month(s) and a new mortgage in %,d; the working-capital line lent $%,.0fk in %,d"
                    + " sector-month(s) while it was on, $%,.0fk of it past what the 0.7.8 rule would have lent%n",
                    ruleOnMonths, projectRefusedForCapital, mortgageRefusedForCapital, lineLentRationedRun,
                    lineRationedMonths, lineLentPastOldRuleRun);
            out.printf("  interim financing: %,d loan(s) written for $%,.0fk; %,d repaid at their term ($%,.0fk),"
                    + " %,d written down again in a later default ($%,.0fk of them written off), %,d outstanding"
                    + " ($%,.0fk); refused %,d time(s) past the line after the write-down and %,d with the bank shut,"
                    + " %,d of them to the backstop; the backstop closed $%,.0fk for a sector inside its ban in %,d"
                    + " month(s)%n",
                    interimWritten, interimWrittenSum, interimRepaid, interimRepaidSum, interimDefaultedAgain,
                    interimWrittenOffSum, emEnd.getBusinessDebtManager().getInterimCount(),
                    emEnd.getBusinessDebtManager().getInterimPrincipal(), interimRefusedPast, interimRefusedShut,
                    backstopForced, backstopInBanSum, backstopInBanMonths);
            out.printf("  can't pay means default: %,d sector-month(s) defaulted for want of cash, $%,.0fk"
                    + " unpaid and $%,.0fk of their debt defaulted in those months; by reason:%s by sector:%s the bank's"
                    + " own state %,d of them; the longest run %d month(s), %s from m%d, %d of them banned; $%,.0fk"
                    + " unpaid while banned; companies sold $%,.0fk of bonds short of money; the till at month-ends short"
                    + " in %,d sector-month(s), $%,.0fk in all (most $%,.0fk), %,d of them ($%,.0fk) left by a project"
                    + " loan's fee that month, with no lender behind it in %,d ($%,.0fk);"
                    + " every overdraft forgiven over the run, the backstop's, $%,.0fk%n",
                    cannotPayMonths, cannotPayForgivenRun, cannotPayDebtDefaultedRun,
                    why.length() > 0 ? why.toString() : " none;", who.length() > 0 ? who.toString() : " none;",
                    cannotPayBankUnderLine, longestCannotPaySpell, longestCannotPaySector, longestCannotPayStart,
                    longestCannotPayBanned, cannotPayForgivenBanned, companiesSoldShortRun,
                    overdraftMonths, overdraftSum, overdraftMax, overdraftProjectMonths, overdraftProjectSum,
                    overdraftNoLenderMonths, overdraftNoLenderSum,
                    forgivenTotal(emEnd));
        }
        // The run, not the endpoint - see THE CITY'S PAPER AND THE BANK THAT HOLDS IT.
        out.printf("  the city's paper over the run: owed in %,d month(s) (%,d of them at home),"
                + " at most $%,.0fk (m%d); the bank handed over $%,.0fk for it;"
                + " its strain averaged %.2f (worst %.2f, m%d; %d month(s) with no capacity)%n",
                monthsOwing, monthsOwingAtHome, peakCityDebt, peakCityDebtMonth, paperSettledRun,
                strainMonths > 0 ? strainSum / strainMonths : 0, worstStrain, worstStrainMonth,
                monthsWithoutCapacity);
        // ...and what the treasury's rollover did with what fell due (0.7.13).
        Rollover roll = g.getRollover();
        out.printf("  the rollover over the run (%s): %,d issue(s) raised $%,.0fk, %,d of them dollar paper"
                        + " rolled at home with the window shut; $%,.0fk netted from the year's surplus;"
                        + " the last %s; $%,.0fk of face issued%n",
                roll.getMode(), roll.getIssuesLifetime(), roll.getRaisedLifetime(),
                roll.getAtHomeForDollarsLifetime(), roll.getNettedLifetime(),
                roll.getLastMonth() < 0 ? "never"
                        : String.format("m%d: $%,.0fk fell due, $%,.0fk netted, $%,.0fk raised",
                                roll.getLastMonth(), roll.getLastDue(), roll.getLastNetted(), roll.getLastRaised()),
                roll.getIssuedLifetime());
        /*
         * WHO HOLDS IT, AND WHAT IT COSTS BY MATURITY (0.7.1): the four holders
         * at the end at face, what the households bought and sold over the run,
         * and the curve's two ends - the ten- and fifty-year rates the city
         * would pay today, and what the central bank's holdings take off the
         * long end.
         */
        DebtManager dm = g.getDebtManager();
        CentralBank holder = g.getCentralBank();
        out.printf("  who holds it at the end: households $%,.0fk, the bank $%,.0fk, the central bank"
                + " $%,.0fk, abroad $%,.0fk (face); the households paid $%,.0fk at %d settle(s),"
                + " sold $%,.0fk back to the desk in %d month(s) and were paid $%,.0fk of coupons;"
                + " the central bank traded in %d month(s), bought $%,.0fk and sold $%,.0fk%n",
                dm.householdPrincipal(), dm.bankPrincipal(), dm.centralBankPrincipal(),
                dm.getForeignPrincipal(), householdsBoughtRun, monthsHouseholdsBought,
                deskBoughtRun, monthsDeskBought, couponsToHouseholdsRun, monthsCentralBankTraded,
                holder.getBoughtPaperLifetime(), holder.getSoldPaperLifetime());
        out.printf("  the curve at the end: note %.2f%%, 10-year %.2f%%, 50-year %.2f%%;"
                + " the central bank's holdings take %.3f points off the 50-year (dial %.0f%%,"
                + " holding %.1f%% of the term paper)%n",
                dm.curveRate(6) * 100, dm.curveRate(120) * 100, dm.curveRate(600) * 100,
                dm.compression(600) * 100, holder.getTargetShare() * 100,
                dm.centralBankShareOfTerm() * 100);
        out.printf("  the holdings at their most: %.3f points off the 50-year (m%d, holding %.1f%% of the"
                + " term paper, the 50-year at %.2f%%)%n",
                peakCompression * 100, peakCompressionMonth, heldShareAtPeak * 100, longRateAtPeak * 100);
        // ...and the floor split by who holds the paper (0.7.15): the central bank's
        // share at the policy rate, the rest at the bank's floor (DebtManager.floorRate()).
        out.printf("  the floor at the end: %.2f%% (the policy rate %.2f%%, the bank's floor %.2f%%; the central"
                + " bank holds %.1f%% of all the city's paper); it paid the households $%,.0fk for their paper"
                + " over the run, of the $%,.0fk it paid for paper%n",
                dm.floorRate() * 100, dm.getPolicyRate() * 100, dm.bankFloorRate() * 100,
                dm.centralBankShareOfPaper() * 100, holder.getBoughtFromHouseholdsLifetime(),
                holder.getBoughtPaperLifetime());
        // ...and its own maturing paper, rolled at issue (0.7.15, round 2).
        out.printf("  the central bank rolled $%,.0fk of its own maturing par at issue over the run, paying"
                + " $%,.0fk%n", holder.getParAtIssueLifetime(), holder.getBoughtAtIssueLifetime());

        /*
         * THE CENTRAL BANK (0.7.0): its balance sheet at the end, the money
         * supply, and what the treasury drew on it and left unpaid over the
         * run. The two money figures are the Money page's getters.
         */
        CentralBank cb = g.getCentralBank();
        out.printf("  the central bank: M0 $%,.0fk (reserves $%,.0fk, currency $%,.0fk), M2 $%,.0fk;"
                + " lent the bank $%,.0fk at the window and the treasury $%,.0fk; vault $%,.0fk;"
                + " equity $%,.0fk (a loss of $%,.0fk carried)%n",
                cb.m0(), cb.getReserves(), cb.getCurrency(), g.getM2(),
                cb.getAdvancesToBank(), cb.getAdvancesToTreasury(), cb.getVault(),
                cb.equity(), cb.getLossCarried());
        out.printf("  ...made $%,.0fk and destroyed $%,.0fk since founding; paid the bank $%,.0fk"
                + " on reserves this month at %.2f%%; remitted $%,.0fk to the treasury over the run%n",
                cb.getIssuedLifetime(), cb.getRetiredLifetime(), cb.getInterestOnReserves(),
                g.getDebtManager().getPolicyRate() * 100, cb.getRemittedLifetime());
        // The defence (0.7.2): see THE DEFENCE OVER THE RUN.
        out.printf("  the defence: sold US$%,.0fk of the vault defending the currency over the run, in %,d"
                + " month(s), the most US$%,.0fk (m%d); $%,.0fk of the central bank's equity spent on it; the vault ends at"
                + " US$%,.0fk%n",
                defendedUsdRun, monthsDefended, peakDefenceUsd, peakDefenceMonth,
                cb.getDefendedLifetime(), fx.getReservesUsd());
        out.printf("  the land office: US$%,.0fk paid abroad over the run in %,d month(s), US$%,.0fk of it"
                + " out of the vault; $%,.0fk here at the day's rates, %s%n",
                fx.getLandUsdLifetime(), monthsLandBought, fx.getLandUsdFromVaultLifetime(), landLocalRun,
                fx.getLandUsdLifetime() <= 0 ? "none bought"
                        : String.format("%.1f%% %s than at the founding rate", Math.abs(landLocalRun
                                / fx.getLandUsdLifetime() - 1) * 100,
                                landLocalRun < fx.getLandUsdLifetime() ? "cheaper" : "dearer"));
        out.printf("  ...ground kept ahead (0.7.58): %,d offer(s) bought over the run, %.2f km2 dry, US$%,.0fk; the"
                + " cash share stopped a look short %,d time(s)%n", groundAheadBought,
                LandManager.km2(groundAheadSqFt), groundAheadUsd, groundAheadShort);
        out.printf("  ...room to grow past the ground-ahead line (0.7.67, M3b): %,d offer(s) bought by the move; %,d Bus"
                + " Network(s), %,d Light Rail Line(s), %,d Metro Line(s) standing at the end%n", roomMovesBought,
                qty(g, "Bus Network"), qty(g, "Light Rail Line"), qty(g, "Metro Line"));
        out.printf("  ...the road by its life (0.7.70, N1): %,d Gravel Road(s), %,d Paved Road(s), %,d Elevated Highway(s)"
                + " standing at the end; %,d paving(s) of %,d gravel road(s) ordered, %,d being paved%n",
                qty(g, "Gravel Road"), qty(g, "Paved Road"), qty(g, "Elevated Highway"), pavings, roadsPaved,
                g.pavingNow());
        if (AUTOBUILD) {
            AutoBuilder ab = g.getAutoBuilder();
            out.printf("  ...automatic building (0.7.73, N4, -Dplaytest.autobuild): %,d order(s), %,d building(s), $%,.0fk"
                    + " spent, $%,.0fk borrowed on %,d bond(s); %,d offer(s) of ground bought for it; held back at the last"
                    + " pass: %s%n", ab.getOrders(), ab.getBuildings(), ab.getSpent(), ab.getBorrowed(), ab.getBonds(),
                    autoBuildGroundBought,
                    ab.held().isEmpty() ? "nothing" : String.join(" | ", ab.held()));
        }
        if (CHILDCARE) {
            out.printf("  ...childcare by the advice's card (0.7.71, N2, -Dplaytest.childcare): %,d order(s) %s; %,d Small"
                    + " Childcare Centre(s), %,d Childcare Centre(s), %,d Large Childcare Centre(s) standing at the end"
                    + " against %,d home building(s)%n", childcareOrders, childcareBuilt,
                    qtyOf(g, 15), qtyOf(g, 16), qtyOf(g, 17), homeBuildings(g));
        }
        out.printf("  ...iron a whole field at a time (0.7.64): %,d offer(s) bought, %,d on the funding page's bond; first"
                + " needed %s, the first bought %s; looks that needed iron and could not pay %,d, found none listed %,d,"
                + " and whose cheapest field would not pay itself back (0.7.67) %,d; %,d iron site(s) and %d mine(s) at the"
                + " end%n", ironFieldsBought, ironFieldsOnBond,
                firstIronNeedMonth > 0 ? "m" + firstIronNeedMonth : "never", firstIron,
                ironCouldNotPay, ironNoneListed, ironDoesNotPay, g.getLandManager().getIronDeposits(), qty(g, "Iron Mine"));
        UtilitiesHandler water = g.getServicesManager().getUtilitiesHandler();
        if (g.hasCityMap()) {
            CityMap map = g.getCityMap();
            out.printf("  the city map (0.7.60): kept up %,d month(s), its districts off the buildings in %,d; %,d of %,d"
                    + " reload(s) read it back the same; %d district(s) at the end, %,d of its %,d owned iron sites"
                    + " under mines; dropped %d time(s)%n", mapMonths, mapMismatches, mapReloadsSame, mapReloads,
                    map.districts().size(), qty(g, "Iron Mine"), map.ownedSites(Resource.IRON),
                    g.getMapFailures());
        }
        // A map dropped mid-run is gone from the city, so this is asked whether or not one stands now.
        if (g.getMapFailures() > 0) flag(g.getMonth(), "the city map was dropped", g.getMapFailures() + " time(s)");
        out.printf("  ...water past the fresh water limit (0.7.59): %,d offer(s) bought for lake or river and %,d for a"
                + " coast, of %,d move(s) offered; the city ends with %.2f km2 of lake and river and %.2f of sea, %d water"
                + " plant(s) and %d desalination plant(s), a limit of %,.0f units (rights %,.0f), %.0f%% of the fresh"
                + " plants idle%n", freshBought, coastsBought, waterLandOffered, g.getLandManager().getFreshKm2(),
                g.getLandManager().getSeaKm2(), qty(g, "Water Treatment Plant"), qty(g, "Desalination Plant"),
                water.getFreshCap(), g.getFreshRights(), water.getFreshIdleShare() * 100);
        out.printf("  ...fuel (0.7.62): %,d offer(s) with oil bought; the first well %s, the first refinery %s; at the end"
                + " %d well(s) on %,d oil site(s) with %,.0f t of crude left, and %d refinery(ies), at most %d; the"
                + " drivers', the railway's and (0.7.83) the vans' fuel $%,.0fk over the run at wholesale, %.1f%% of it"
                + " bought abroad%n", oilBought,
                firstWellMonth > 0 ? "m" + firstWellMonth : "never", firstRefineryMonth > 0 ? "m" + firstRefineryMonth : "never",
                qty(g, "Oil Well"), g.getLandManager().getOilSites(), g.getLandManager().getOilReserveTonnes(),
                qty(g, "Oil Refinery"), mostRefineries, fuelBillRun,
                fuelBillRun > 0 ? fuelAbroadRun / fuelBillRun * 100 : 0);
        StringBuilder refined = new StringBuilder();
        for (java.util.Map.Entry<Good, Double> e : refinedRun.entrySet()) {
            if (!(e.getValue() > 0)) continue;
            refined.append(refined.length() == 0 ? "" : ", ").append(e.getKey().label().toLowerCase())
                    .append(String.format(" %,.0f", e.getValue()));
        }
        out.printf("  ...the refinery's products (0.7.76): made over the run %s; written off for want of tank room %,.0f%n",
                refined.length() == 0 ? "none" : refined.toString(), refinedWrittenOff);
        out.printf("  ...the spread planner (0.7.82, money gate %s): the first refiners' building %s; opened %s; sold back"
                        + " %s; at most %d crude unit(s) and %d conversion unit(s)%n",
                g.getSectors().refining().planner().moneyGate(), firstRefinerMonth > 0 ? "m" + firstRefinerMonth : "never",
                refinersOpened.isEmpty() ? "none" : refinersOpened, refinersSold.isEmpty() ? "none" : refinersSold,
                mostCrudeUnits, mostConversionUnits);
        ham.citybuildersim.sectors.Oil oilWells = g.getSectors().oil();
        double wellsAsNew = oilWells.newWellsCapacity(), wellsLift = oilWells.getCapacity(Good.CRUDE);
        out.printf("  ...the wells' decline (0.7.84): %,.0f t of crude lifted over the run, at most %d well(s); %d retired worn"
                        + " out (after %d months); at the end %d on %d dry oil site(s) of %d, lifting %,.0f t a month, %s of new%n",
                crudeLiftedRun, mostWells, g.getWellsWornOut(),
                ham.citybuildersim.sectors.Oil.lifeMonths(ham.citybuildersim.sectors.Oil.WellKind.LAND),
                oilWells.landWellsStanding(), g.getLandManager().getSites(Resource.OIL, true), g.getLandManager().getOilSites(),
                wellsLift, wellsAsNew > 0 ? String.format("%.0f%%", wellsLift / wellsAsNew * 100) : "none");
        ham.citybuildersim.sectors.Retail forecourts = g.getSectors().retail();
        double cars = g.getHouseholdBalance().totalCars();
        out.printf("  ...the phase-1 buyers (0.7.83): the vans' diesel $%,.0fk over the run (%,.0f L), %.2f%% of the"
                        + " businesses' payrolls; lubricants %,.0f L bought (%.1f%% abroad); bitumen %,.1f t drawn for roads"
                        + " ($%,.0fk); the refiners' lubricants %s of the crude they ran%n",
                vanDieselRun, vanDieselLitresRun, payrollRun > 0 ? vanDieselRun / payrollRun * 100 : 0,
                lubricantsBoughtRun, lubricantsBoughtRun > 0 ? lubricantsImportedRun / lubricantsBoughtRun * 100 : 0,
                g.getBitumenTonnes(), g.getBitumenCost(),
                crudeRefinedLitresRun > 0
                        ? String.format("%.2f%%", refinedRun.getOrDefault(Good.LUBRICANTS, 0.0) / crudeRefinedLitresRun * 100)
                        : "none (no crude run)");
        out.printf("  ...the forecourts (0.7.83): the first filling station %s; %d at the end, at most %d; %,.0f car(s) a"
                        + " station at the end; the drivers' petrol %,.0f L at the pump and %,.0f L past the stations over the"
                        + " run, paid %.4fx its wholesale cost; the last month a litre %s at the pump on %s wholesale%n",
                firstStationMonth > 0 ? "m" + firstStationMonth : "never", forecourts.stationsStanding(), mostStations,
                forecourts.stationsStanding() > 0 ? cars / forecourts.stationsStanding() : 0,
                pumpLitresRun, queueLitresRun, pumpWholesaleRun > 0 ? pumpBillRun / pumpWholesaleRun : 0,
                Double.isFinite(forecourts.getPumpPrice()) ? String.format("%.6f", forecourts.getPumpPrice()) : "none",
                Double.isFinite(forecourts.getWholesaleLitre()) ? String.format("%.6f", forecourts.getWholesaleLitre()) : "none");
        out.printf("  ...the ports (0.7.86): %s ordered, the first %s; %d terminal(s) standing at the end; a look found a"
                        + " berth's worth and no coast %d time(s); %,.0f t by sea over the run, %.2f%% of the %,.0f t across"
                        + " the boundary; the boats: %,d calls over the run, at most %d a month (%s), %d the last month; crude"
                        + " held back for room %d month(s)%n",
                terminalsOrdered.isEmpty() ? "no terminal" : terminalsOrdered.toString(),
                firstTerminalMonth > 0 ? "m" + firstTerminalMonth : "never", terminalsStanding(g), portsNoCoast,
                seaTonnesRun, boundaryTonnesRun > 0 ? seaTonnesRun / boundaryTonnesRun * 100 : 0, boundaryTonnesRun,
                callsRun, mostCalls, mostCallsMonth > 0 ? "m" + mostCallsMonth : "never", lastCalls, heldBackMonths);
        out.printf("  ...the vault at its lowest US$%,.0fk (m%d); half the founders' dollars gone %s; all but a"
                + " hundredth gone %s%n", vaultLow == Double.MAX_VALUE ? 0 : vaultLow, vaultLowMonth,
                halfGoneMonth > 0 ? "by month " + halfGoneMonth : "never",
                emptyMonth > 0 ? "by month " + emptyMonth : "never");
        out.printf("  the treasury's advances: $%,.0fk owed at the end against a ceiling of $%,.0fk"
                + " (%.0f months of revenue%s);"
                + " $%,.0fk printed for it since founding; on advances %,d month(s), at the ceiling"
                + " %,d; at most $%,.0fk (m%d); first drawn %s%n",
                cb.getAdvancesToTreasury(), cb.ceiling(), cb.getAdvancesCeilingMonths(),
                ADVANCES_MONTHS != null ? ", set by -Dplaytest.advancesMonths" : ", the default",
                cb.getPrintedLifetime(),
                monthsOnAdvances, monthsAtCeiling, peakAdvances, peakAdvancesMonth,
                firstAdvanceMonth > 0 ? "in month " + firstAdvanceMonth : "never");
        StringBuilder byLine = new StringBuilder();
        for (java.util.Map.Entry<TreasuryLine, Double> owedOn : g.getArrearsByLine().entrySet()) {
            byLine.append(String.format("%s $%,.0fk, ", owedOn.getKey().label, owedOn.getValue()));
        }
        out.printf("  arrears: $%,.0fk owed and unpaid at the end%s; refused $%,.0fk and paid down"
                + " $%,.0fk over the run; at most $%,.0fk (m%d)%n",
                g.getArrearsTotal(),
                byLine.length() > 0 ? " (" + byLine.substring(0, byLine.length() - 2) + ")" : "",
                g.getArrearsRefusedLifetime(), g.getArrearsPaidLifetime(),
                peakArrears, peakArrearsMonth);

        /*
         * CREDIT, BY SECTOR. The bank's line above says what it holds; this
         * says who owes it and who cannot borrow, which is the half nobody
         * could see without writing a probe. A bank with equity and no book
         * is either a city that needs no credit or six sectors that are
         * barred from it, and those are different findings.
         */
        // "went under": the whole-sector backstop only since 0.7.8 - a
        // sector's firms defaulting a slice at a time is not on its record.
        out.println("  credit by sector (cash / assets / owes / rate / went under whole / ban left / loss streak):");
        BusinessDebtManager credit = g.getEconomyManager().getBusinessDebtManager();
        java.util.Map<String, Integer> streaks = g.getBusinessInvestment().getLossMonthsState();
        for (String sector : Sectors.KEYS) {
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
     * A sector's name in three or four characters, for the checkpoint line.
     *
     * Derived from the key so that adding a sector cannot break it: initials
     * for a two-word name, the first three letters otherwise. Retail -> Ret,
     * Real Estate -> RE, Business Services -> BS.
     */
    static String shortTag(String key) {
        if (key == null || key.isEmpty()) return "?";
        String[] words = key.trim().split("\\s+");
        if (words.length > 1) {
            StringBuilder t = new StringBuilder();
            for (String w : words) if (!w.isEmpty()) t.append(Character.toUpperCase(w.charAt(0)));
            return t.toString();
        }
        return key.substring(0, Math.min(3, key.length()));
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
        // Derived, not listed. A hand-written array of seven was indexed over
        // Sectors.KEYS.length and hard-crashed the whole playtest the day an
        // eighth sector was added - the one thing in the codebase that did.
        for (int i = 0; i < Sectors.KEYS.length; i++) {
            String sec = Sectors.KEYS[i];
            b.append(String.format("%s %s/%d/%d  ", shortTag(sec),
                    money(g.getEconomyManager().getSectorCash(sec)),
                    c.getRestructureCount(sec), c.getBlockedMonths(sec)));
        }
        b.append(String.format("| hunger %.0f%% shelf %.0f%%",
                g.getHouseholdBalance().getHungerRate() * 100,
                g.getHouseholdBalance().getDeliveredShare() * 100));
        return b.toString();
    }

    /**
     * The bank as a business at a checkpoint, on one line (0.7.7): its price
     * build-up at the dial - funds-transfer price, running costs, expected
     * loss and capital charge, adding to prime - then what it paid savers and
     * what share of the dial that was, and over the trailing year its net
     * interest margin, its costs over its revenue, its fees' share of its
     * revenue and its return on equity: the figures a real bank is read by.
     */
    static String bankEra(Game g) {
        Bank bk = g.getBank();
        double pol = g.getDebtManager().getPolicyRate();
        int n = Math.min(bankMonths, 12);
        double nii = 0, fees = 0, other = 0, opex = 0, net = 0, eq = 0, assets = 0, lost = 0, atRisk = 0;
        for (int i = 0; i < n; i++) {
            nii += bankNii[i]; fees += bankFees[i]; other += bankOther[i];
            opex += bankOpex[i]; net += bankNet[i]; eq += bankEquityRing[i]; assets += bankAssetsRing[i];
            lost += bankLossRing[i]; atRisk += bankAtRiskRing[i];
        }
        double revenue = nii + fees + other;
        double years = n / 12.0;
        return String.format("       bank    dial %.2f%%: ftp %.2f + run %.2f + loss %.2f + capital %.2f = prime %.2f%%"
                        + " (household %.2f%%) | savers %.2f%% = %.2f of the dial, held %d mo"
                        + " | trailing year: NIM %.2f%%, costs %.0f%% of revenue, fees %.0f%% of it, ROE %.1f%%,"
                        + " written off %.2f%% of the book"
                        + " | capital %.1f%% (target %.1f%%, top %.1f%%), %s, allowance $%,.0fk (%d sector(s) in stage 2),"
                        + " provisions %.2f%% of its book over its last year",
                pol * 100,
                bk.fundsTransferPrice(pol, Bank.PRIME_TERM_MONTHS) * 100, bk.runningCostRate() * 100,
                bk.expectedLossRate() * 100, bk.capitalCharge(pol, Bank.PRIME_TERM_MONTHS, Bank.RISK_BUSINESS) * 100,
                bk.prime(pol) * 100, bk.householdRate(pol) * 100,
                bk.depositRate() * 100, pol > 0 ? bk.depositRate() / pol : 0, depositCappedMonths,
                assets > 0 && years > 0 ? nii / (assets / n) / years * 100 : 0,
                revenue > 0 ? opex / revenue * 100 : 0,
                revenue > 0 ? fees / revenue * 100 : 0,
                // clamped: a failed bank's equity is a few dollars and its ROE a meaningless eighteen digits
                eq > 0 && years > 0 ? Math.max(-999, Math.min(999, net / (eq / n) / years * 100)) : 0,
                atRisk > 0 && years > 0 ? lost / (atRisk / n) / years * 100 : 0,
                Math.min(999, bk.capitalRatio()) * 100, bk.capitalTarget() * 100, bk.capitalTop() * 100,
                bk.payoutDecision(), bk.getAllowance(), bk.getSectorsWatched().size(),
                bk.trailingLossRate() * 100);
    }

    /** The run's deposit rate regressed on the dial: the share of a move in the policy rate savers saw, over every month. */
    static double depositBeta() {
        if (bankMonths < 2) return 0;
        double nMonths = bankMonths;
        double cov = sharePolicyDeposit / nMonths - (sharePolicy / nMonths) * (shareDeposit / nMonths);
        double var = sharePolicySq / nMonths - (sharePolicy / nMonths) * (sharePolicy / nMonths);
        return var > 1e-12 ? cov / var : 0;
    }

    static String era(Game g, String label) {
        EconomyManager e = g.getEconomyManager();
        PopulationManager p = g.getPopulationManager();
        BuildingManager b = g.getBuildingManager();
        InfrastructureManager roads = g.getInfrastructureManager();

        return String.format(
                "m%-5d %-22s pop %-7d cash %-14s GDP/mo %-11s jobs %-6d "
                + "fill %3.0f%% roads %3.0f%% cars %3.0f%% ride %3.0f%% power %3.0f%% water %3.0f%% "
                + "cityDebt %-12s bizDebt %-11s land %3.0f%% mines %d/%d ore $%.2f fields %3.0f%% crops $%.2f"
                + " px %.2f (%.2f-%.2f)",
                g.getMonth(), label,
                p.getPopulation(),
                money(g.getCash()),
                money(e.getMonthGdp()),
                p.getTotalJobs(),
                (p.getTotalJobs() > 0
                        ? 100.0 * Math.min(p.getWorkforce(), p.getTotalJobs()) / p.getTotalJobs()
                        : 100),
                roads.getThroughputRatio() * 100,
                /*
                 * THE MOTORING, IN TWO FIGURES (2026-09-16), and they belong
                 * beside the road ratio rather than anywhere else because they
                 * are what that ratio is now mostly about. The first is cars
                 * per household; the second is the share of commuters actually
                 * carried off the street. The same argument the price level's
                 * note two fields down makes: a line that carries everything
                 * except the number the mechanic is about cannot be read.
                 */
                roads.getCarOwnership() * 100,
                roads.getLoad(Traffic.COMMUTERS) > 0
                        ? roads.getTransitRiders() / roads.getLoad(Traffic.COMMUTERS) * 100 : 0,
                g.getEnergyRatio() * 100,
                g.getWaterRatio() * 100,
                money(g.getDebtManager().getAllPrincipal()),
                money(e.getBusinessDebtManager().getTotalPrincipal()),
                g.getLandManager().getUtilisation() * 100,
                g.minesCommitted(), g.getLandManager().getIronDeposits(),
                g.getMarkets().get(Good.IRON).getLocalPrice(),
                // What share of its own dinner the city grows, and what a tonne
                // is fetching. The tenth sector in two numbers; see Agriculture.
                Math.min(1, g.getSectors().agriculture().getSelfSufficiency(g)) * 100,
                g.getMarkets().get(Good.CROPS).getLocalPrice(),
                /*
                 * THE PRICE LEVEL, AND THE TWO IT HAS LIVED BETWEEN.
                 *
                 * This line carried population, cash, GDP, jobs, debt, land,
                 * ore and crops and not the one number the whole monetary side
                 * of the game is about - which is why nobody noticed that
                 * cities finishing at 0.98 had spent a century near 2.0. Three
                 * fields, and the pair in brackets is what an endpoint cannot
                 * say. See claude/the-cities-that-empty-out.md.
                 */
                g.getPriceIndex().getIndex(),
                g.getPriceIndex().getTrough(),
                g.getPriceIndex().getPeak());
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
