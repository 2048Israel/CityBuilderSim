package ham.citybuildersim;

import ham.citybuildersim.PolicyDates.Lever;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Every Policy lever opens on the date Canada first had it, and no lever may open more than the law field's four years early (R0, slice 1:
 * PolicyDates), and the levers TaxPolicy holds are held shut at zero until then and open on their date: the three income taxes at the city's
 * founding rate, the promises at zero (R0, slice 2a: TaxPolicy.setMonth() and the setters' refusal; call 16: the founding rate).
 *
 * WHY. PolicyDates is pure data that the gate in the model and the Policy screen will read, and a wrong date is no error anywhere: a tax that
 * opens a year late is a city that goes untaxed for a year, and nobody sees it until the ensemble moves. So the dates are held to the study's
 * table here, row for row, with the table typed out below as the specification and not read back from the class under test; research-1900.md
 * 7.1 is the source; the gate's half (sections 7 to 9) takes its dates from PolicyDates and never as a bare month number. Rows the study dates that the game has no lever for (PL1 the 1915 workers' compensation levy, PL11 care subsidies, L1
 * deposit insurance) are not here; the day the game grows one, its row is added to TABLE and to PolicyDates together.
 *
 * What it has to prove:
 *
 *   1. THE TABLE'S DATES. Every dated lever opens in the study's year and month, a year-only date in January, and CityCalendar gives that year
 *      and month back from its opening month; the table and the open list between them name every lever once.
 *   2. FOUNDING. In month 1 (January of the epoch) the open levers are open and every dated lever is closed.
 *   3. THE DAY. Each dated lever is closed every month before its opening month and open on it and after (a sweep of the first months, then
 *      +1, +12 and +600).
 *   4. THE EARLY-OPENING BOUND. A lever's earliest opening is MAX_EARLY_MONTHS before its date, never before month 1; the floor holds on
 *      fixtures; an open lever's earliest is month 1.
 *   5. WHAT THE PLAYER IS TOLD. A dated lever has its entry key (but the policy rate: the table gives it none), its terms and a notice naming its
 *      year (and its month where the study gives one) and where it opens (an income tax at the founding rate, built from the model's constant;
 *      the minimum wage's floor at the going unskilled wage; the rest at zero); an open lever has no key, its reason, and no notice.
 *   6. HISTORY'S ORDER. The dates run in the table's order, which is the declared order, and no two dated levers share an entry key.
 *   7. THE GATE ON A BARE POLICY. A policy told it is month 1 holds every gated lever's dials at zero from their defaults and refuses their
 *      setters (writing nothing to the decision log); the property tax is settable; for each gated lever the dial reads zero the month before
 *      it opens; on its month an income tax reads the founding rate (DEFAULT_INCOME_TAX, its offsets zero) and a promise reads zero; the
 *      setter takes a setting that differs from what it opened at and the months after neither hold nor reset it; "every tax" sets only the
 *      open ones; the effective rates, the payslip and the pension read zero while shut; openAll() opens everything and a later month leaves
 *      the rates a fixture set; a copy of a shut policy is shut and opens as the original would, an income tax at the founding rate.
 *   8. A CITY. A new city founds with profit, sales and wage tax at zero and the property tax at its default, every gated lever shut in
 *      month 1; the month is pushed to the policy each month (a lever moved to month 3 is shut in month 2 and open in month 3, where an
 *      income tax reads the founding rate and the inbox raises its notice once, titled and worded from PolicyDates).
 *   9. THE SAVE. The openings round-trip through a restore, keyed by the lever's name; a null or empty map opens every lever; an unknown key
 *      is skipped; a lever the map does not name keeps history's month; a saved policy state comes back as saved into a policy that has only
 *      heard month 1, and is held again only when the month says so; a restore and a reset empty what the policy held, so a rate they put
 *      back is never overwritten by a later opening; a reset policy is back on history's dates.
 *  10. THE MINIMUM WAGE'S FLOOR (R0, call 12). The dial was also the wage ladder's anchor, so it was not zeroed (since WG1, 2026-11-10 the
 *      going rate anchors it): before the law the floor binds
 *      no wage (a glut takes the unskilled wage under the going rate, to the band's own bottom) while the anchor and every base wage stand; the
 *      player's dial and its nudge, through Game, are refused in month 1 and taken from the opening month; a load pushes the month to the
 *      labour market; the inbox raises the opening notice once; a city saved the month before the wage tax opens, read back and told the
 *      next month, opens it at the founding rate (the load tells the month after the restore, not before).
 *  11. THE TWO READERS AND THE LOANS (R0, calls 14 and 15). The founding quote of the builders' sales tax is a month-1 city's, not a bare
 *      policy's; a city's households start on the policy's pension, not the 45% default; before student aid opens a student is lent nothing
 *      and the loan rate is refused, and the household book is told so each month.
 *  12. ZONING'S GATE (R0, call 13). Until zoning opens (PL6, 1924 on history's dates) Game.setZone() draws no zone and writes nothing to the
 *      decision log, while the forest law takes a setting (it is not the study's zoning); a city whose zoning is moved is still refused the month
 *      before and takes a zone in its month, where the inbox raises one notice, titled by Inbox.leverTitle() (no article, history's date);
 *      TaxPolicy.openNow() opens one lever and no other, and an income tax so opened still opens at the founding rate when the month is told.
 *  13. THE SHUT LEVER'S WORDS (R0, slice 3). The Policy screen's card for a shut lever is composed here and not in the screen: its first line
 *      names the date, the lever with the article Inbox.leverTitle() uses and the terms, its second where the lever opens, and a city whose
 *      opening a find moved is told history's date and the find.
 */
public class PolicyDatesCheck {

    private static int fails;
    private static int passes;

    static void check(String label, boolean ok) {
        System.out.println((ok ? "  PASS " : "  FAIL ") + label);
        if (ok) passes++; else fails++;
    }

    /** One row of research-1900.md 7.1: the year and month Canada first had the lever (monthKnown false: the study gives the year only). */
    record Row(Lever lever, int year, int month, boolean monthKnown, String entry) { }

    /** research-1900.md 7.1 and 7.2, in history's order: the specification the class is held to. */
    static final List<Row> TABLE = List.of(
            new Row(Lever.POLICY_RATE,          1914, 8, true,  ""),       // the Finance Act's Advance Rate, Aug 1914
            new Row(Lever.PROFIT_TAX,           1916, 1, false, "PL2"),
            new Row(Lever.WAGE_TAX,             1917, 1, false, "PL3"),
            new Row(Lever.MINIMUM_WAGE,         1918, 1, false, "PL4"),
            new Row(Lever.SALES_TAX,            1920, 1, false, "PL5"),
            new Row(Lever.ZONING,               1924, 1, false, "PL6"),
            new Row(Lever.PENSION_REPLACEMENT,  1927, 1, false, "PL7"),
            new Row(Lever.CENTRAL_BANK,         1935, 3, true,  "PL8"),    // the Bank of Canada, 11 Mar 1935
            new Row(Lever.STUDENT_AID,          1939, 1, false, "PL9"),
            new Row(Lever.EI,                   1940, 8, true,  "PL10"),   // the Unemployment Insurance Act, 7 Aug 1940
            new Row(Lever.HEALTH_PREMIUM,       1947, 1, true,  "PL12"),   // Saskatchewan, 1 Jan 1947
            new Row(Lever.PENSION_CONTRIBUTION, 1966, 1, true,  "L5"));    // the Canada Pension Plan, 1 Jan 1966

    /** The levers the study leaves open from the first day (7.1: "open"; the others by its 1900 rule, R0's notes, call 5). */
    static final List<Lever> OPEN = List.of(Lever.PROPERTY_TAX, Lever.FARMLAND_RELIEF, Lever.FOOD_ASSISTANCE, Lever.TRANSIT_FARE,
            Lever.TUITION_SCALES, Lever.HEALTH_FEE_SCALE, Lever.FUEL_DUTY, Lever.PORT_DUES, Lever.SECTOR_SUBSIDIES);

    /** The game month of a real calendar month, worked out here and not by PolicyDates. */
    static int monthOf(int year, int monthOfYear) {
        return 1 + 12 * (year - CityCalendar.EPOCH_YEAR) + monthOfYear - 1;
    }

    /* ----- the gate's fixtures ----- */

    /** Every gated dial set to a non-zero setting through the setters: what stays zero is what the gate refused. */
    static void setEveryGatedDial(TaxPolicy p) {
        WageBand band = WageBand.values()[0];
        p.setProfitTaxRate(.20);
        p.setSalesTaxRate(.10);
        p.setWageTaxRate(.12);
        p.setWageOffset(band, .02);
        p.setProfitOffset(TaxPolicy.FARM_SECTOR, .03);
        p.setSalesOffset(TaxPolicy.FARM_SECTOR, .01);
        p.setPensionReplacement(.30);
        p.setContributionRate(.04);
        p.setEiPremiumRate(.02);
        p.setEiBenefitRate(.40);
        p.setHealthPremiumRate(.03);
        p.setGrant(TaxPolicy.GrantBasis.WAGE_SHARE, .50);
    }

    /** What one lever owns, as one number: the sum of the dials and offsets the gate holds for it (zero while it is held). */
    static double dialOf(TaxPolicy p, Lever lever) {
        WageBand band = WageBand.values()[0];
        switch (lever) {
            case WAGE_TAX:             return p.getWageTaxRate() + p.getWageOffset(band);
            case PROFIT_TAX:           return p.getProfitTaxRate() + p.getProfitOffset(TaxPolicy.FARM_SECTOR);
            case SALES_TAX:            return p.getSalesTaxRate() + p.getSalesOffset(TaxPolicy.FARM_SECTOR);
            case PENSION_REPLACEMENT:  return p.getPensionReplacement();
            case PENSION_CONTRIBUTION: return p.getContributionRate();
            case EI:                   return p.getEiPremiumRate() + p.getEiBenefitRate();
            case HEALTH_PREMIUM:       return p.getHealthPremiumRate();
            case STUDENT_AID:          return p.getGrantAmount();
            default:                   return 0;
        }
    }

    /** How many lines the city's decision log holds under the zoning kind: what a zone, or the forest law, writes when it is taken. */
    static int zoningLines(Game g) {
        int n = 0;
        for (DecisionLog.Entry e : g.getDecisions().entries()) if (DecisionLog.ZONING.equals(e.kind)) n++;
        return n;
    }

    /** The three income taxes, typed here as the specification of PolicyDates.opensAtFoundingRate() (call 16): the levers that fund the city. */
    static final List<Lever> INCOME_TAXES = List.of(Lever.PROFIT_TAX, Lever.WAGE_TAX, Lever.SALES_TAX);

    /** An income tax's base rate alone, which is what opens at the founding rate; an income tax's offsets open at zero. */
    static double baseRateOf(TaxPolicy p, Lever tax) {
        switch (tax) {
            case WAGE_TAX:   return p.getWageTaxRate();
            case PROFIT_TAX: return p.getProfitTaxRate();
            case SALES_TAX:  return p.getSalesTaxRate();
            default:         throw new IllegalArgumentException(tax + " is not an income tax");
        }
    }

    /** An income tax's offsets summed over the fixture's band and sector: dialOf() less the base rate. */
    static double offsetOf(TaxPolicy p, Lever tax) {
        return dialOf(p, tax) - baseRateOf(p, tax);
    }

    /** Sets every gated dial on a policy and returns what the gated levers own after it, summed. */
    static double dialsAfterSetting(TaxPolicy p) {
        setEveryGatedDial(p);
        double sum = 0;
        for (Lever l : TaxPolicy.GATED) sum += dialOf(p, l);
        return sum;
    }

    /** A wage tax rate that is neither zero nor the rate an income tax opens at: a setting that took is told from an opening. */
    static final double SETTING = TaxPolicy.DEFAULT_INCOME_TAX + .10;

    /** Tries the wage tax base at SETTING and returns what it reads. */
    static double wageRateAfterSetting(TaxPolicy p) {
        p.setWageTaxRate(SETTING);
        return p.getWageTaxRate();
    }

    public static void main(String[] args) {

        /* ==================== 1. the table's dates ==================== */
        System.out.println("--- every dated lever opens in the study's year and month ---");
        for (Row r : TABLE) {
            int expected = monthOf(r.year(), r.month());
            int opens = PolicyDates.openingMonth(r.lever());
            check(r.lever() + " opens in game month " + expected + " (" + r.year() + (r.monthKnown() ? ", month " + r.month() : ", January") + ")",
                    opens == expected);
            check(r.lever() + ": CityCalendar gives year " + r.year() + " back from month " + opens, CityCalendar.yearOf(opens) == r.year());
            check(r.lever() + ": CityCalendar gives month " + r.month() + " back from month " + opens, CityCalendar.monthOfYear(opens) == r.month());
            check(r.lever() + " " + (r.monthKnown() ? "is dated to its month" : "is dated by the year alone"),
                    r.lever().monthKnown() == r.monthKnown());
        }
        Set<Lever> dated = new HashSet<>();
        for (Row r : TABLE) dated.add(r.lever());
        Set<Lever> open = new HashSet<>(OPEN);
        Set<Lever> both = new HashSet<>(dated);
        both.addAll(open);
        check("the table and the open list name every lever exactly once (" + Lever.values().length + " levers)",
                dated.size() == TABLE.size() && open.size() == OPEN.size() && both.size() == dated.size() + open.size()
                        && both.size() == Lever.values().length);

        /* ==================== 2. founding ==================== */
        System.out.println("\n--- in the founding month the open levers are open and the dated ones shut ---");
        int founding = 1;
        check("month 1 is January " + CityCalendar.EPOCH_YEAR, CityCalendar.yearOf(founding) == CityCalendar.EPOCH_YEAR
                && CityCalendar.monthOfYear(founding) == 1);
        for (Lever l : OPEN) {
            check(l + " opens at month 1 and is open in it", PolicyDates.openingMonth(l) == 1 && PolicyDates.isOpen(l, founding)
                    && PolicyDates.openAtFounding(l));
        }
        for (Row r : TABLE) {
            check(r.lever() + " is shut in month 1", !PolicyDates.isOpen(r.lever(), founding) && !PolicyDates.openAtFounding(r.lever()));
        }

        /* ==================== 3. the day ==================== */
        System.out.println("\n--- each dated lever is shut until its month and open from it ---");
        for (Row r : TABLE) {
            Lever l = r.lever();
            int opens = PolicyDates.openingMonth(l);
            int firstOpen = -1;
            for (int m = 1; m <= opens + 1 && firstOpen < 0; m++) if (PolicyDates.isOpen(l, m)) firstOpen = m;
            check(l + ": the first open month in a sweep from month 1 is its opening month, " + opens, firstOpen == opens);
            check(l + " is shut the month before (" + (opens - 1) + ")", !PolicyDates.isOpen(l, opens - 1));
            check(l + " is open on its month, +1, +12 and +600", PolicyDates.isOpen(l, opens) && PolicyDates.isOpen(l, opens + 1)
                    && PolicyDates.isOpen(l, opens + 12) && PolicyDates.isOpen(l, opens + 600));
        }
        check("an open lever stays open far on", PolicyDates.isOpen(Lever.PROPERTY_TAX, 1) && PolicyDates.isOpen(Lever.PROPERTY_TAX, 1200));

        /* ==================== 4. the early-opening bound ==================== */
        System.out.println("\n--- a law find brings a lever forward by at most MAX_EARLY_MONTHS ---");
        check("the bound is positive and a whole number of years (the law field's)", PolicyDates.MAX_EARLY_MONTHS > 0
                && PolicyDates.MAX_EARLY_MONTHS % 12 == 0);
        int boundYears = PolicyDates.MAX_EARLY_MONTHS / 12;
        for (Row r : TABLE) {
            Lever l = r.lever();
            int opens = PolicyDates.openingMonth(l);
            boolean afterBound = r.year() > CityCalendar.EPOCH_YEAR + boundYears;
            check(l + " is dated after the epoch plus the bound (" + (CityCalendar.EPOCH_YEAR + boundYears) + "), so the floor never moves it",
                    afterBound);
            check(l + "'s earliest opening is exactly MAX_EARLY_MONTHS before " + opens,
                    PolicyDates.earliestOpening(l) == opens - PolicyDates.MAX_EARLY_MONTHS);
            check(l + "'s earliest is not before month 1 and is before its date", PolicyDates.earliestOpening(l) >= 1
                    && PolicyDates.earliestOpening(l) < opens);
        }
        for (Lever l : OPEN) check(l + " (open) has month 1 as its earliest", PolicyDates.earliestOpening(l) == 1);
        check("the floor: a date inside the bound floors at month 1", PolicyDates.earliestFor(1) == 1
                && PolicyDates.earliestFor(PolicyDates.MAX_EARLY_MONTHS) == 1 && PolicyDates.earliestFor(PolicyDates.MAX_EARLY_MONTHS + 1) == 1);
        check("the floor: a date one month past it is month 2, and a far one keeps the whole bound",
                PolicyDates.earliestFor(PolicyDates.MAX_EARLY_MONTHS + 2) == 2
                        && PolicyDates.earliestFor(600) == 600 - PolicyDates.MAX_EARLY_MONTHS);

        /* ==================== 5. what the player is told ==================== */
        System.out.println("\n--- terms, a key and a notice naming the year ---");
        for (Row r : TABLE) {
            Lever l = r.lever();
            String notice = PolicyDates.notice(l);
            check(l + " has the table's entry key" + (r.entry().isEmpty() ? " (none: the table gives the policy rate no key)" : " " + r.entry()),
                    l.entry().equals(r.entry()));
            check(l + " has a label, first terms with no full stop, and sources", !l.label().isEmpty() && !l.terms().isEmpty()
                    && !l.terms().endsWith(".") && l.sources().equals(PolicyDates.SOURCES));
            check(l + "'s notice names " + r.year() + (r.monthKnown() ? " and its month" : "") + " and says its terms",
                    notice.contains(Integer.toString(r.year())) && notice.contains(l.terms())
                            && (!r.monthKnown() || notice.contains(CityCalendar.monthName(PolicyDates.openingMonth(l)))));
            check(l + "'s opening date prints as the Policy screen will show it", PolicyDates.openingDate(l).equals(
                    r.monthKnown() ? CityCalendar.format(PolicyDates.openingMonth(l)) : Integer.toString(r.year())));
            // Where it opens, typed here and not read from the class: an income tax at the founding rate (the percent from the model's own
            // constant), the minimum wage's floor at the going unskilled wage (call 12), zoning with no ground zoned until a zone is drawn
            // (call 13: it has no dial to open at zero), every other dated lever at zero (call 16).
            String opensAt = INCOME_TAXES.contains(l)
                    ? "The tax opens at the city's founding rate, " + DecisionLog.pct(TaxPolicy.DEFAULT_INCOME_TAX) + "; the setting is yours."
                    : l == Lever.MINIMUM_WAGE ? "The floor opens at the going unskilled wage; the setting is yours."
                    : l == Lever.ZONING ? "No ground is zoned until you draw a zone; the setting is yours."
                    : "The lever opens at zero; the setting is yours.";
            check(l + "'s notice closes with where it opens: \"" + opensAt + "\"", notice.endsWith(l.terms() + ". " + opensAt));
        }
        boolean foundingRateLevers = true;
        for (Lever l : Lever.values()) foundingRateLevers &= PolicyDates.opensAtFoundingRate(l) == INCOME_TAXES.contains(l);
        check("exactly the three income taxes open at the founding rate (PolicyDates.opensAtFoundingRate()): the promises and the rest do not",
                foundingRateLevers);
        boolean taxesAreGated = true;
        for (Lever l : INCOME_TAXES) taxesAreGated &= List.of(TaxPolicy.GATED).contains(l) && !PolicyDates.openAtFounding(l);
        check("...and each of them is a dated lever TaxPolicy holds, so the rate they open at is TaxPolicy's to write", taxesAreGated);
        check("the founding rate is a rate a player would set (above zero, within the income tax's cap), so an opening at it is told from one at zero",
                TaxPolicy.DEFAULT_INCOME_TAX > 0 && TaxPolicy.DEFAULT_INCOME_TAX <= TaxPolicy.MAX_INCOME_TAX);
        for (Lever l : OPEN) {
            check(l + " (open) has no key, a label, a reason and its source, and no notice", l.entry().isEmpty() && !l.label().isEmpty()
                    && !l.terms().isEmpty() && l.sources().equals(PolicyDates.OPEN_SOURCES) && PolicyDates.notice(l).isEmpty());
        }

        /* ==================== 6. history's order ==================== */
        System.out.println("\n--- the dates run in history's order ---");
        boolean inOrder = true;
        for (int i = 1; i < TABLE.size(); i++) {
            inOrder &= PolicyDates.openingMonth(TABLE.get(i - 1).lever()) < PolicyDates.openingMonth(TABLE.get(i).lever());
        }
        check("each dated lever opens after the one before it in the table", inOrder);
        check("profit tax, then income tax, then the minimum wage, then the sales tax, then zoning, then the old-age pension",
                PolicyDates.openingMonth(Lever.PROFIT_TAX) < PolicyDates.openingMonth(Lever.WAGE_TAX)
                        && PolicyDates.openingMonth(Lever.WAGE_TAX) < PolicyDates.openingMonth(Lever.MINIMUM_WAGE)
                        && PolicyDates.openingMonth(Lever.MINIMUM_WAGE) < PolicyDates.openingMonth(Lever.SALES_TAX)
                        && PolicyDates.openingMonth(Lever.SALES_TAX) < PolicyDates.openingMonth(Lever.ZONING)
                        && PolicyDates.openingMonth(Lever.ZONING) < PolicyDates.openingMonth(Lever.PENSION_REPLACEMENT));
        boolean firstAndLast = true;
        for (Row r : TABLE) {
            if (r.lever() != Lever.POLICY_RATE) firstAndLast &= PolicyDates.openingMonth(Lever.POLICY_RATE) < PolicyDates.openingMonth(r.lever());
            if (r.lever() != Lever.PENSION_CONTRIBUTION) {
                firstAndLast &= PolicyDates.openingMonth(Lever.PENSION_CONTRIBUTION) > PolicyDates.openingMonth(r.lever());
            }
        }
        check("the policy rate (1914) is the first dated lever to open and the pension contribution (1966) the last", firstAndLast);
        List<Lever> declared = new ArrayList<>();
        for (Lever l : Lever.values()) if (!PolicyDates.openAtFounding(l)) declared.add(l);
        List<Lever> tabled = new ArrayList<>();
        for (Row r : TABLE) tabled.add(r.lever());
        check("the declared order of the dated levers is the table's", declared.equals(tabled));
        Set<String> keys = new HashSet<>();
        boolean distinct = true;
        for (Lever l : Lever.values()) if (!l.entry().isEmpty()) distinct &= keys.add(l.entry());
        check("no two levers share an entry key (" + keys.size() + " keys)", distinct);
        Set<String> labels = new HashSet<>();
        boolean labelsDistinct = true;
        for (Lever l : Lever.values()) labelsDistinct &= labels.add(l.label());
        check("no two levers share a label", labelsDistinct);

        /* ==================== 7. the gate on a bare policy ==================== */
        System.out.println("\n--- a policy that is told it is month 1 holds the gated levers at zero and refuses their setters ---");
        TaxPolicy bare = new TaxPolicy();
        boolean allOpen = true;
        for (Lever l : Lever.values()) allOpen &= bare.isOpen(l) && bare.openingMonth(l) == PolicyDates.openingMonth(l);
        check("a policy no city holds (month 0) has every lever open, each with history's opening month", allOpen);
        TaxPolicy opened = new TaxPolicy();
        setEveryGatedDial(opened);
        boolean fixtureReaches = true;
        for (Lever l : TaxPolicy.GATED) fixtureReaches &= dialOf(opened, l) > 0;
        check("the fixture reaches every gated dial on a policy no city holds (so a zero below is the gate, not a setter that did nothing)",
                fixtureReaches);
        boolean settingsTold = SETTING > 0 && SETTING != TaxPolicy.DEFAULT_INCOME_TAX && SETTING <= TaxPolicy.MAX_INCOME_TAX;
        for (Lever l : TaxPolicy.GATED) settingsTold &= dialOf(opened, l) != TaxPolicy.DEFAULT_INCOME_TAX;
        for (Lever l : INCOME_TAXES) settingsTold &= baseRateOf(opened, l) != TaxPolicy.DEFAULT_INCOME_TAX;
        check("...and no setting the fixture makes (nor the one the restore checks use) is the rate an income tax opens at, so a setting that took is told from an opening",
                settingsTold);

        TaxPolicy fresh = new TaxPolicy();
        boolean defaultsLive = true;
        for (Lever l : TaxPolicy.GATED) defaultsLive &= dialOf(fresh, l) > 0 || l == Lever.HEALTH_PREMIUM;
        check("before it is told a month, a fresh policy holds the founding defaults (the 15% taxes, the pension, EI, the grant; the health premium's is zero)",
                defaultsLive);
        DecisionLog log = new DecisionLog(() -> 1);
        fresh.recordTo(log);
        fresh.setMonth(1);
        boolean allHeld = true;
        for (Lever l : TaxPolicy.GATED) allHeld &= !fresh.isOpen(l) && dialOf(fresh, l) == 0;
        check("told month 1, every gated lever is shut and every dial it owns reads zero: the defaults are held down", allHeld);
        check("...and the holding wrote nothing to the decision log", log.size() == 0);
        setEveryGatedDial(fresh);
        boolean refused = true;
        for (Lever l : TaxPolicy.GATED) refused &= dialOf(fresh, l) == 0;
        check("every setter of a gated dial is refused in month 1 (every tax, the offsets, the promises, the grant)", refused);
        check("...and a refusal wrote nothing to the decision log either", log.size() == 0);
        WageBand band = WageBand.values()[0];
        check("the effective rates, the payslip and the pension read zero while the levers are shut",
                fresh.effectiveWageRate(band) == 0 && fresh.effectiveProfitRate(TaxPolicy.FARM_SECTOR) == 0
                        && fresh.effectiveSalesRate(TaxPolicy.FARM_SECTOR) == 0 && fresh.payslipShare(band) == 0
                        && fresh.pensionPerSenior() == 0);
        fresh.setPropertyTaxRate(.03);
        fresh.setPropertyOffset(TaxPolicy.FARM_SECTOR, .01);
        fresh.setFoodAssistance(.5);
        fresh.setTuitionScale(2);
        check("the property tax, its offsets, food assistance and the tuition scale are open from founding and take a setting in month 1",
                fresh.getPropertyTaxRate() == .03 && fresh.getPropertyOffset(TaxPolicy.FARM_SECTOR) == .01
                        && fresh.getFoodAssistance() == .5 && fresh.getTuitionScale() == 2);
        check("...and those settings were decisions, so the log is wired (a positive control for the two silences above)", log.size() >= 1);

        for (Lever l : TaxPolicy.GATED) {
            int opens = PolicyDates.openingMonth(l);
            TaxPolicy p = new TaxPolicy();
            p.setMonth(opens - 1);
            check(l + ": the month before it opens (" + (opens - 1) + ") it is shut and its dials read zero from their defaults",
                    !p.isOpen(l) && dialOf(p, l) == 0);
            setEveryGatedDial(p);
            check(l + ": ...and its setters refuse", dialOf(p, l) == 0);
            p.setMonth(opens);
            if (INCOME_TAXES.contains(l)) {
                check(l + ": on its month (" + opens + ") it is open and its base rate reads the founding rate, DEFAULT_INCOME_TAX: a tax opens at it, not at zero",
                        p.isOpen(l) && baseRateOf(p, l) == TaxPolicy.DEFAULT_INCOME_TAX);
                check(l + ": ...with its offsets still at zero (they open at zero)", offsetOf(p, l) == 0);
            } else {
                check(l + ": on its month (" + opens + ") it is open and still reads zero: a promise opens at zero", p.isOpen(l) && dialOf(p, l) == 0);
            }
            double opened0 = dialOf(p, l);
            setEveryGatedDial(p);
            check(l + ": ...and its setters take a setting, one that differs from what it opened at", dialOf(p, l) > 0 && dialOf(p, l) != opened0);
            double set = dialOf(p, l);
            p.setMonth(opens + 1);
            p.setMonth(opens + 12);
            check(l + ": ...which the months after do not hold again, nor reset to what it opened at", dialOf(p, l) == set);
        }

        // The promises, by name: EI, the pensions and student aid cost the city, so they open at zero (call 16) - the premium and the benefit,
        // the replacement, the grant and the student loan's rate - where the taxes that fund it open at the founding rate.
        TaxPolicy eiDay = new TaxPolicy();
        eiDay.setMonth(PolicyDates.openingMonth(Lever.EI) - 1);
        eiDay.setMonth(PolicyDates.openingMonth(Lever.EI));
        check("on EI's month the premium and the benefit both read zero",
                eiDay.isOpen(Lever.EI) && eiDay.getEiPremiumRate() == 0 && eiDay.getEiBenefitRate() == 0);
        TaxPolicy aidDay = new TaxPolicy();
        aidDay.setMonth(PolicyDates.openingMonth(Lever.STUDENT_AID) - 1);
        aidDay.setMonth(PolicyDates.openingMonth(Lever.STUDENT_AID));
        TaxPolicy pensionDay = new TaxPolicy();
        pensionDay.setMonth(PolicyDates.openingMonth(Lever.PENSION_REPLACEMENT) - 1);
        pensionDay.setMonth(PolicyDates.openingMonth(Lever.PENSION_REPLACEMENT));
        check("on student aid's month the grant and the loan's rate read zero, and on the pension's the replacement does",
                aidDay.isOpen(Lever.STUDENT_AID) && aidDay.getGrantAmount() == 0 && aidDay.getStudentLoanRate() == 0
                        && pensionDay.isOpen(Lever.PENSION_REPLACEMENT) && pensionDay.getPensionReplacement() == 0);

        // A city's policy is told every month, so an income tax is opened by the month it opens in and never by a later one: it opens once.
        TaxPolicy onceOnly = new TaxPolicy();
        onceOnly.setMonth(PolicyDates.openingMonth(Lever.WAGE_TAX) - 1);
        onceOnly.setMonth(PolicyDates.openingMonth(Lever.WAGE_TAX));
        onceOnly.setWageTaxRate(SETTING);
        onceOnly.setWageTaxRate(0);
        for (int m = PolicyDates.openingMonth(Lever.WAGE_TAX) + 1; m <= PolicyDates.openingMonth(Lever.PENSION_CONTRIBUTION); m++) onceOnly.setMonth(m);
        check("a player who sets the opened wage tax to zero finds it still at zero in every month to 1966: the founding rate is an opening, not a floor",
                onceOnly.getWageTaxRate() == 0);

        TaxPolicy wageDay = new TaxPolicy();
        wageDay.setMonth(PolicyDates.openingMonth(Lever.WAGE_TAX));
        wageDay.setIncomeTaxRate(.2);
        check("\"every tax\" on the income tax's day sets the profit and wage bases (both open) and leaves the sales tax (shut) at zero",
                wageDay.getProfitTaxRate() == .2 && wageDay.getWageTaxRate() == .2 && wageDay.getSalesTaxRate() == 0);
        wageDay.setWageOffset(band, .05);
        wageDay.setProfitOffset(TaxPolicy.FARM_SECTOR, .02);
        wageDay.setSalesOffset(TaxPolicy.FARM_SECTOR, .02);
        check("...and the offsets take where open (wage, profit) and not where shut (sales)",
                wageDay.getWageOffset(band) == .05 && wageDay.getProfitOffset(TaxPolicy.FARM_SECTOR) == .02
                        && wageDay.getSalesOffset(TaxPolicy.FARM_SECTOR) == 0);

        TaxPolicy all = new TaxPolicy();
        all.setMonth(1);
        all.openAll();
        boolean everyOpen = true;
        for (Lever l : Lever.values()) everyOpen &= all.isOpen(l) && all.openingMonth(l) == 1;
        check("openAll() opens every lever, each with opening month 1, in month 1", everyOpen);
        setEveryGatedDial(all);
        boolean allTake = true;
        for (Lever l : TaxPolicy.GATED) allTake &= dialOf(all, l) > 0;
        check("...and every gated setter then takes a setting in month 1", allTake);
        all.setMonth(2);
        boolean notHeld = true;
        for (Lever l : TaxPolicy.GATED) notHeld &= dialOf(all, l) > 0;
        check("...and the next month does not hold them", notHeld);

        // openAll() empties what the policy held: a fixture opens everything in month 1 and sets its rates in the same month, and a later
        // month (here, past the income tax's date, where it would have opened) must leave those rates exactly as set.
        TaxPolicy fixture = new TaxPolicy();
        fixture.setMonth(1);
        fixture.openAll();
        fixture.setWageTaxRate(SETTING);
        fixture.setProfitTaxRate(0);
        fixture.setSalesTaxRate(SETTING);
        fixture.setMonth(PolicyDates.openingMonth(Lever.WAGE_TAX) + 1);
        check("openAll() after month 1, the rates set, then a month past the income tax's date: the rates stand as set (a zero stays zero)",
                fixture.getWageTaxRate() == SETTING && fixture.getProfitTaxRate() == 0 && fixture.getSalesTaxRate() == SETTING);
        fixture.setMonth(PolicyDates.openingMonth(Lever.SALES_TAX) + 1);
        check("...and a month past the sales tax's date too", fixture.getWageTaxRate() == SETTING && fixture.getProfitTaxRate() == 0
                && fixture.getSalesTaxRate() == SETTING);

        // A copy keeps what the policy held, so a preview of a policy a month short of an income tax's date opens it as the city will.
        for (Lever l : INCOME_TAXES) {
            int opens = PolicyDates.openingMonth(l);
            TaxPolicy orig = new TaxPolicy();
            orig.setMonth(opens - 1);
            TaxPolicy twin = orig.copy();
            twin.setMonth(opens);
            check(l + ": a copy taken the month before it opens opens it at DEFAULT_INCOME_TAX on its month, its offsets at zero",
                    twin.isOpen(l) && baseRateOf(twin, l) == TaxPolicy.DEFAULT_INCOME_TAX && offsetOf(twin, l) == 0);
            check(l + ": ...and the original, not told yet, is still shut at zero (the copy is detached)", !orig.isOpen(l) && dialOf(orig, l) == 0);
            orig.setMonth(opens);
            check(l + ": ...and opens as its copy did when it is told", baseRateOf(orig, l) == baseRateOf(twin, l));
        }
        TaxPolicy openedCopy = new TaxPolicy();
        openedCopy.setMonth(PolicyDates.openingMonth(Lever.WAGE_TAX) - 1);
        openedCopy.setMonth(PolicyDates.openingMonth(Lever.WAGE_TAX));
        openedCopy.setWageTaxRate(SETTING);
        TaxPolicy later = openedCopy.copy();
        later.setMonth(PolicyDates.openingMonth(Lever.WAGE_TAX) + 1);
        check("a copy of a policy whose tax has opened and been set keeps the setting: it does not open twice", later.getWageTaxRate() == SETTING);

        TaxPolicy shutCopy = fresh.copy();
        boolean sameDates = true;
        for (Lever l : Lever.values()) sameDates &= shutCopy.isOpen(l) == fresh.isOpen(l) && shutCopy.openingMonth(l) == fresh.openingMonth(l);
        check("a copy of a policy that is shut in month 1 has the same dates and shut levers", sameDates && !shutCopy.isOpen(Lever.WAGE_TAX));
        check("...and refuses as its original does (a preview of a shut lever stays shut)", dialsAfterSetting(shutCopy) == 0);
        TaxPolicy openCopy = all.copy();
        check("...and a copy of an open one is open, with its settings", openCopy.isOpen(Lever.WAGE_TAX) && dialOf(openCopy, Lever.WAGE_TAX) > 0
                && openCopy.openingMonth(Lever.WAGE_TAX) == 1);

        /* ==================== 8. a city ==================== */
        System.out.println("\n--- a new city founds with its income taxes at zero, the month is pushed to the policy, and a tax opens at the founding rate ---");
        PrintStream out = System.out;
        PrintStream quiet = new PrintStream(new OutputStream() {
            @Override public void write(int b) { }
            @Override public void write(byte[] b, int off, int len) { }
        });
        Game city = new Game(GameFiles.scratch("r0-gate"));
        System.setOut(quiet);
        try { city.run(); } finally { System.setOut(out); }
        TaxPolicy cityPolicy = city.getEconomyManager().getTaxPolicy();
        check("the city is in month 1", city.getMonth() == 1);
        check("profit, sales and wage tax are at zero at founding", cityPolicy.getProfitTaxRate() == 0 && cityPolicy.getSalesTaxRate() == 0
                && cityPolicy.getWageTaxRate() == 0);
        check("the property tax is at its default", cityPolicy.getPropertyTaxRate() == TaxPolicy.DEFAULT_PROPERTY_TAX);
        boolean cityZero = true;
        boolean cityShut = true;
        for (Lever l : TaxPolicy.GATED) {
            cityZero &= dialOf(cityPolicy, l) == 0;
            cityShut &= !city.isLeverOpen(l) && city.leverOpeningMonth(l) == PolicyDates.openingMonth(l);
        }
        check("every dial a gated lever owns reads zero at founding (the pension, EI, the health premium and the grant too)", cityZero);
        check("Game.isLeverOpen() is false for each gated lever in month 1, and Game.leverOpeningMonth() is history's", cityShut);
        check("the income tax is shut in month 1", !city.isLeverOpen(Lever.WAGE_TAX));
        check("the property tax lever is open from the first day", city.isLeverOpen(Lever.PROPERTY_TAX) && city.leverOpeningMonth(Lever.PROPERTY_TAX) == 1);
        check("every gated setter is refused on the city's own policy", dialsAfterSetting(cityPolicy) == 0);

        // The month reaches the policy each month: the wage tax is moved to month 3, the way a find (R3) will move it.
        Map<String, Integer> earlier = new HashMap<>();
        earlier.put(Lever.WAGE_TAX.name(), 3);
        cityPolicy.restoreOpenings(earlier);
        check("the income tax moved to month 3 is shut in month 1", !city.isLeverOpen(Lever.WAGE_TAX) && city.leverOpeningMonth(Lever.WAGE_TAX) == 3);
        System.setOut(quiet);
        try { city.toggleNextMonth(); } finally { System.setOut(out); }
        check("in month 2 it is still shut, reads zero and refuses a rate", city.getMonth() == 2 && !city.isLeverOpen(Lever.WAGE_TAX)
                && cityPolicy.getWageTaxRate() == 0 && wageRateAfterSetting(cityPolicy) == 0);
        System.setOut(quiet);
        try { city.toggleNextMonth(); } finally { System.setOut(out); }
        check("in month 3, the month the city pushes, it is open and reads the founding rate (DEFAULT_INCOME_TAX), not zero, with its offsets at zero",
                city.getMonth() == 3 && city.isLeverOpen(Lever.WAGE_TAX) && cityPolicy.getWageTaxRate() == TaxPolicy.DEFAULT_INCOME_TAX
                        && offsetOf(cityPolicy, Lever.WAGE_TAX) == 0);
        check("...while the profit and sales taxes, still shut, stay at zero: one tax opening opens no other",
                !city.isLeverOpen(Lever.PROFIT_TAX) && !city.isLeverOpen(Lever.SALES_TAX)
                        && cityPolicy.getProfitTaxRate() == 0 && cityPolicy.getSalesTaxRate() == 0);
        check("...and it takes a rate, which differs from the one it opened at", wageRateAfterSetting(cityPolicy) == SETTING
                && SETTING != TaxPolicy.DEFAULT_INCOME_TAX);
        List<Notice> raised = new ArrayList<>();
        for (Notice n : city.getInbox().all()) if (n.getKey().startsWith("lever:")) raised.add(n);
        check("the inbox has raised exactly one lever notice by month 3, keyed to the income tax", raised.size() == 1
                && raised.get(0).getKey().equals("lever:" + Lever.WAGE_TAX.name()));
        if (!raised.isEmpty()) {
            Notice n = raised.get(0);
            check("...titled from the lever and Canada's date (\"" + n.getTitle() + "\")",
                    n.getTitle().equals("An income tax: " + PolicyDates.openingDate(Lever.WAGE_TAX)));
            check("...with Canada's first terms for its body, raised in the month it opened",
                    String.join(" ", n.getBody()).equals(PolicyDates.notice(Lever.WAGE_TAX)) && n.getRaised() == 3);
        }
        System.setOut(quiet);
        try { city.toggleNextMonth(); } finally { System.setOut(out); }
        int again = 0;
        for (Notice n : city.getInbox().all()) if (n.getKey().equals("lever:" + Lever.WAGE_TAX.name())) again++;
        check("in month 4 it is still one notice (raised once), and the rate set in month 3 is not held again",
                again == 1 && cityPolicy.getWageTaxRate() == SETTING);

        /* ==================== 9. the save ==================== */
        System.out.println("\n--- the openings are saved by name, and a save from before R0 loads with every lever open ---");
        TaxPolicy moved = new TaxPolicy();
        Map<String, Integer> found = new HashMap<>();
        found.put(Lever.WAGE_TAX.name(), 100);
        found.put(Lever.EI.name(), 40);
        moved.restoreOpenings(found);
        Map<String, Integer> saved = moved.openingsForSave();
        boolean named = true;
        int datedCount = 0;
        for (Lever l : Lever.values()) {
            boolean isDated = !PolicyDates.openAtFounding(l);
            if (isDated) datedCount++;
            named &= saved.containsKey(l.name()) == isDated;
        }
        check("a save names every dated lever by Lever.name() and none of the open ones (" + datedCount + " keys)", named && saved.size() == datedCount);
        check("...with the moved months in it", saved.get(Lever.WAGE_TAX.name()) == 100 && saved.get(Lever.EI.name()) == 40);
        boolean history = true;
        for (Lever l : Lever.values()) {
            if (l != Lever.WAGE_TAX && l != Lever.EI) history &= moved.openingMonth(l) == PolicyDates.openingMonth(l);
        }
        check("a lever the map does not name keeps history's month", history);
        TaxPolicy back = new TaxPolicy();
        back.restoreOpenings(saved);
        boolean roundTrip = true;
        for (Lever l : Lever.values()) roundTrip &= back.openingMonth(l) == moved.openingMonth(l);
        check("the openings round-trip through restoreOpenings", roundTrip);

        TaxPolicy oldSave = new TaxPolicy();
        oldSave.restoreOpenings(null);
        TaxPolicy emptySave = new TaxPolicy();
        emptySave.restoreOpenings(new HashMap<>());
        boolean everyLeverOpen = true;
        for (Lever l : Lever.values()) everyLeverOpen &= oldSave.openingMonth(l) == 1 && emptySave.openingMonth(l) == 1;
        check("a null map (a save from before R0) and an empty one open every lever", everyLeverOpen);
        oldSave.setMonth(1);
        boolean oldTakes = dialsAfterSetting(oldSave) > 0;
        for (Lever l : TaxPolicy.GATED) oldTakes &= oldSave.isOpen(l) && dialOf(oldSave, l) > 0;
        check("...so an old save's taxes stand, and its setters take, in month 1", oldTakes);

        TaxPolicy odd = new TaxPolicy();
        Map<String, Integer> withUnknown = new HashMap<>();
        withUnknown.put("TIME_TRAVEL", 5);
        withUnknown.put(Lever.WAGE_TAX.name(), 77);
        withUnknown.put(Lever.EI.name(), 0);
        odd.restoreOpenings(withUnknown);
        check("a key this build does not know is skipped, the known ones are read, and a month below 1 is held at 1",
                odd.openingMonth(Lever.WAGE_TAX) == 77 && odd.openingMonth(Lever.EI) == 1
                        && odd.openingMonth(Lever.PROFIT_TAX) == PolicyDates.openingMonth(Lever.PROFIT_TAX));

        TaxPolicy late = new TaxPolicy();
        late.setMonth(PolicyDates.openingMonth(Lever.PENSION_CONTRIBUTION));
        setEveryGatedDial(late);
        double[] lateState = late.getPolicyState();
        List<TaxPolicy.SectorOffsets> lateOffsets = late.getSectorOffsets();
        TaxPolicy loaded = new TaxPolicy();
        loaded.setMonth(1);   // what a new city's policy has heard when a load begins
        loaded.restoreOpenings(late.openingsForSave());
        loaded.restorePolicyState(lateState);
        loaded.restoreSectorOffsets(lateOffsets);
        boolean sameDials = true;
        for (Lever l : TaxPolicy.GATED) sameDials &= dialOf(loaded, l) == dialOf(late, l) && dialOf(late, l) > 0;
        check("a saved policy state and its offsets come back as saved into a policy that has only heard month 1", sameDials);
        loaded.setMonth(PolicyDates.openingMonth(Lever.PENSION_CONTRIBUTION));
        boolean kept = true;
        for (Lever l : TaxPolicy.GATED) kept &= dialOf(loaded, l) == dialOf(late, l);
        check("...and the loaded month, past every date, holds nothing back", kept);
        loaded.setMonth(1);
        boolean heldAgain = true;
        for (Lever l : TaxPolicy.GATED) heldAgain &= dialOf(loaded, l) == 0;
        check("...whereas month 1 would hold them again (the hold is the month's, not the restore's)", heldAgain);

        // Call 16: an income tax opens at the founding rate only when the policy held it shut and then hears it open, so a restore, a reset
        // and openAll() (section 7) each empty what was held, and a rate they put back is never overwritten by a later opening.
        TaxPolicy src = new TaxPolicy();
        src.setIncomeTaxRate(SETTING);
        TaxPolicy restored = new TaxPolicy();
        restored.setMonth(1);   // held every dated lever shut, as a new city's policy has when a load begins
        restored.restoreOpenings(src.openingsForSave());
        restored.restorePolicyState(src.getPolicyState());
        restored.setMonth(PolicyDates.openingMonth(Lever.SALES_TAX) + 1);
        boolean restoredKept = true;
        for (Lever l : INCOME_TAXES) restoredKept &= baseRateOf(restored, l) == SETTING;
        check("a policy that restores a saved state with the three income taxes at a setting after hearing month 1, then hears a month past their dates, keeps it: the restore emptied what it held",
                restoredKept);
        TaxPolicy lived = new TaxPolicy();
        lived.setMonth(1);
        lived.reset();
        lived.setWageTaxRate(SETTING);
        lived.setMonth(PolicyDates.openingMonth(Lever.WAGE_TAX) + 1);
        check("a reset policy (a new city) that has a rate set before it is told a month keeps it when the month comes: the reset emptied what it held",
                lived.getWageTaxRate() == SETTING);
        lived.reset();
        lived.setMonth(1);
        boolean newCityHeld = true;
        for (Lever l : TaxPolicy.GATED) newCityHeld &= dialOf(lived, l) == 0;
        lived.setMonth(PolicyDates.openingMonth(Lever.WAGE_TAX));
        check("...and told month 1 straight after, it holds the gated levers afresh, so that the income tax opens at the founding rate on its date",
                newCityHeld && lived.getWageTaxRate() == TaxPolicy.DEFAULT_INCOME_TAX);

        TaxPolicy reborn = late.copy();
        reborn.reset();
        boolean resetDates = true;
        for (Lever l : Lever.values()) resetDates &= reborn.openingMonth(l) == PolicyDates.openingMonth(l);
        check("a reset policy (a new city) is back on history's dates and, hearing no month, holds the founding defaults",
                resetDates && reborn.getWageTaxRate() == TaxPolicy.DEFAULT_INCOME_TAX && reborn.isOpen(Lever.WAGE_TAX));

        /* ==================== 10. the minimum wage's floor ==================== */
        System.out.println("\n--- before the minimum wage is law the floor binds no wage and the dial is refused; the anchor stands ---");
        // The labour market alone, driven straight (a claim about one function): an unskilled glut, with and without the law.
        int bandCount = WageBand.values().length;
        double[] gluttedPosts = new double[bandCount];
        double[] gluttedSupply = new double[bandCount];
        for (int b = 0; b < bandCount; b++) { gluttedPosts[b] = 1000; gluttedSupply[b] = 4000; }
        LabourMarket bound = new LabourMarket();
        LabourMarket unbound = new LabourMarket();
        unbound.setFloorBinding(false);
        check("a labour market no city holds binds its floor, as it always did", bound.isFloorBinding() && !unbound.isFloorBinding());
        for (int m = 0; m < 200; m++) { bound.advanceMonth(gluttedPosts, gluttedSupply); unbound.advanceMonth(gluttedPosts, gluttedSupply); }
        check("with the law, an unskilled glut is held at the floor",
                bound.getWage(JobType.NO_DIPLOMA) >= bound.cashMinimumWage() - 1e-9 && bound.isPinned(WageBand.NONE));
        // WG1 (2026-11-10): the figure that anchors the ladder is the going rate, and the floor is a share of it (it was the anchor until then):
        // the glut's wage is the band's own bottom, under the going rate and under the floor the law would have held it at.
        check("...without it the same glut takes the wage to the bottom of the band's own range, well under the going rate that anchors the ladder (3/4 of it, as before WG1) and under the floor",
                unbound.getWage(JobType.NO_DIPLOMA) < unbound.cashMinimumWage()
                        && unbound.getWage(JobType.NO_DIPLOMA) < unbound.cashGoingRate() * .75
                        && unbound.getWage(JobType.NO_DIPLOMA) >= unbound.baseWage(JobType.NO_DIPLOMA) * LabourMarket.MIN_MULTIPLE - 1e-9);
        boolean anchored = unbound.cashMinimumWage() == bound.cashMinimumWage() && unbound.getMinimumWage() == bound.getMinimumWage();
        for (JobType job : JobType.values()) anchored &= unbound.baseWage(job) == bound.baseWage(job) && unbound.baseWage(job) > 0;
        check("...and the anchor, every base wage and the dial's figure are the same with and without the law (nothing is zeroed)", anchored);

        // A city: the floor is not law in month 1; the dial and its nudge are refused through the player's path (Game).
        Game floorCity = new Game(GameFiles.scratch("r0-floor"));
        System.setOut(quiet);
        try { floorCity.run(); } finally { System.setOut(out); }
        LabourMarket floorMarket = floorCity.getLabourMarket();
        double dialBefore = floorMarket.getMinimumWage();
        double nudgeBefore = floorMarket.getMinimumWageAdjustment();
        check("in month 1 the minimum wage is shut and the floor binds nothing",
                floorCity.getMonth() == 1 && !floorCity.isLeverOpen(Lever.MINIMUM_WAGE) && !floorMarket.isFloorBinding());
        check("the player's minimum-wage dial is refused in month 1, and the figure stands",
                !floorCity.setMinimumWage(dialBefore * 2) && floorMarket.getMinimumWage() == dialBefore);
        check("...and so is its nudge", !floorCity.setMinimumWageAdjustment(.10) && floorMarket.getMinimumWageAdjustment() == nudgeBefore);

        // Moved to month 3 the way a find (R3) will move it; saved in month 2 and read back, then the month it opens.
        Map<String, Integer> floorEarlier = new HashMap<>();
        floorEarlier.put(Lever.MINIMUM_WAGE.name(), 3);
        floorEarlier.put(Lever.WAGE_TAX.name(), 3);   // ...and the wage tax, for the load below (call 16)
        floorCity.getEconomyManager().getTaxPolicy().restoreOpenings(floorEarlier);
        System.setOut(quiet);
        try { floorCity.toggleNextMonth(); } finally { System.setOut(out); }
        check("in month 2 it is still shut", floorCity.getMonth() == 2 && !floorCity.isLeverOpen(Lever.MINIMUM_WAGE) && !floorMarket.isFloorBinding());
        System.setOut(quiet);
        boolean floorSaved;
        Game floorBack = new Game(floorCity.getGameFiles());
        try {
            floorSaved = floorCity.saveGame(1, "r0 floor").ok;
            floorBack.loadGameSave(1);
        } finally { System.setOut(out); }
        check("a city saved in month 2 reads back with the floor not binding (the load pushes the month to the labour market)",
                floorSaved && floorBack.getMonth() == 2 && !floorBack.isLeverOpen(Lever.MINIMUM_WAGE) && floorBack.leverOpeningMonth(Lever.MINIMUM_WAGE) == 3
                        && !floorBack.getLabourMarket().isFloorBinding());
        System.setOut(quiet);
        try { floorCity.toggleNextMonth(); } finally { System.setOut(out); }
        check("in month 3 the floor binds, and the dial and the nudge are taken",
                floorCity.getMonth() == 3 && floorCity.isLeverOpen(Lever.MINIMUM_WAGE) && floorMarket.isFloorBinding()
                        && floorCity.setMinimumWage(dialBefore * 1.2) && floorMarket.getMinimumWage() > dialBefore
                        && floorCity.setMinimumWageAdjustment(.10) && floorMarket.getMinimumWageAdjustment() == .10);
        int floorNotices = 0;
        Notice floorNotice = null;
        for (Notice n : floorCity.getInbox().all()) if (n.getKey().equals("lever:" + Lever.MINIMUM_WAGE.name())) { floorNotices++; floorNotice = n; }
        check("the inbox raised the minimum wage's opening notice once, in month 3, titled and worded from PolicyDates",
                floorNotices == 1 && floorNotice.getRaised() == 3
                        && floorNotice.getTitle().equals("A minimum wage: " + PolicyDates.openingDate(Lever.MINIMUM_WAGE))
                        && String.join(" ", floorNotice.getBody()).equals(PolicyDates.notice(Lever.MINIMUM_WAGE)));

        // Call 16 over a load: the same city read back in month 2, the month before the wage tax opens, and then told month 3. The load puts the
        // saved rates back and then tells the month, so the policy still knows the wage tax is shut and opens it at the founding rate.
        TaxPolicy floorPolicy = floorCity.getEconomyManager().getTaxPolicy();
        TaxPolicy backPolicy = floorBack.getEconomyManager().getTaxPolicy();
        check("the city read back in month 2 has its wage tax shut and at zero", floorBack.getMonth() == 2 && !floorBack.isLeverOpen(Lever.WAGE_TAX)
                && backPolicy.getWageTaxRate() == 0);
        System.setOut(quiet);
        try { floorBack.toggleNextMonth(); } finally { System.setOut(out); }
        check("...and in month 3 it opens at the founding rate, as the city that was never saved did (a load does not lose an income tax's opening)",
                floorBack.getMonth() == 3 && floorBack.isLeverOpen(Lever.WAGE_TAX) && backPolicy.getWageTaxRate() == TaxPolicy.DEFAULT_INCOME_TAX
                        && floorPolicy.getWageTaxRate() == TaxPolicy.DEFAULT_INCOME_TAX);

        /* ==================== 11. two readers that quoted a shut lever, and the student loans ==================== */
        System.out.println("\n--- the builders' quote and the pensioner's first month read the shut levers; no student loan before student aid ---");
        Game monthOne = new Game(GameFiles.scratch("r0-readers"));
        System.setOut(quiet);
        try { monthOne.run(); } finally { System.setOut(out); }
        TaxPolicy monthOnePolicy = monthOne.getEconomyManager().getTaxPolicy();
        check("fixture: a bare policy, which hears no month, still quotes the builders an open sales tax",
                new TaxPolicy().effectiveSalesRate("Construction") > 0);
        check("the founding quote of the builders' sales tax is the one a city in its first month charges: nothing, the tax being shut",
                monthOne.getMonth() == 1 && !monthOne.isLeverOpen(Lever.SALES_TAX) && monthOnePolicy.effectiveSalesRate("Construction") == 0
                        && Founding.foundingBuildersRate() == monthOnePolicy.effectiveSalesRate("Construction"));
        check("fixture: a household book that hears nothing starts on the old pension default",
                new HouseholdAccounts().getPensionPerSenior() > 0);
        check("a city's households are paying no pension in month 1 (the pension is shut), before the month's own push",
                !monthOne.isLeverOpen(Lever.PENSION_REPLACEMENT) && monthOne.getHouseholds().getPensionPerSenior() == 0);

        StudentHousehold student = new StudentHousehold();
        student.studentLoansOpen = false;
        check("a student is lent nothing while the treasury makes no loans, and plans on none",
                student.fundShortfall(100, 0) == 0 && student.studentBorrowed() == 0 && student.planningRoom() == 0);
        student.studentLoansOpen = true;
        check("...and is lent what is short, on a plan of unlimited room, once it does",
                student.fundShortfall(100, 0) == 100 && student.studentBorrowed() == 100 && student.planningRoom() > 0);
        check("a household book no city holds lends, as it always did", new HouseholdBalance().isStudentLoansOpen());

        monthOnePolicy.setStudentLoanRate(.05);
        check("the student loan's rate is refused while student aid is shut", monthOnePolicy.getStudentLoanRate() == 0);
        Map<String, Integer> aidEarlier = new HashMap<>();
        aidEarlier.put(Lever.STUDENT_AID.name(), 3);
        monthOnePolicy.restoreOpenings(aidEarlier);
        System.setOut(quiet);
        try { monthOne.toggleNextMonth(); } finally { System.setOut(out); }
        check("in month 2 the treasury still makes no student loan: the book is told so", monthOne.getMonth() == 2
                && !monthOne.getHouseholdBalance().isStudentLoansOpen());
        System.setOut(quiet);
        try { monthOne.toggleNextMonth(); } finally { System.setOut(out); }
        monthOnePolicy.setStudentLoanRate(.05);
        check("in month 3, student aid opened, the book is told it lends and the rate takes", monthOne.getMonth() == 3
                && monthOne.getHouseholdBalance().isStudentLoansOpen() && monthOnePolicy.getStudentLoanRate() == .05);

        /* ==================== 12. zoning's gate ==================== */
        System.out.println("\n--- zoning is shut until its date: no zone is drawn and none logged, the forest law stays open, and the inbox raises its notice ---");
        Game zoneCity = new Game(GameFiles.scratch("r0-zoning"));
        System.setOut(quiet);
        try { zoneCity.run(); } finally { System.setOut(out); }
        TaxPolicy zonePolicy = zoneCity.getEconomyManager().getTaxPolicy();
        int zoningMonth = PolicyDates.openingMonth(Lever.ZONING);
        int zoneMoved = 3;
        check("fixture: zoning is shut in month 1, opens on history's month, and the month it is moved to is earlier than that",
                zoneCity.getMonth() == 1 && !zoneCity.isLeverOpen(Lever.ZONING) && zoneCity.leverOpeningMonth(Lever.ZONING) == zoningMonth
                        && zoneMoved < zoningMonth);
        int zoneDecided = zoningLines(zoneCity);
        System.setOut(quiet);
        try { zoneCity.setZone(Sectors.RETAIL, Zoning.Size.SHARE, .05, .5); } finally { System.setOut(out); }
        check("a zone drawn in month 1 is refused: no zone stands and nothing is written to the decision log",
                zoneCity.getZoning().zone(Sectors.RETAIL) == null && !zoneCity.getZoning().hasZones()
                        && zoningLines(zoneCity) == zoneDecided);
        System.setOut(quiet);
        try { zoneCity.setForestLaw(.10, .25); } finally { System.setOut(out); }
        check("...while the forest law takes a setting in month 1 and logs it: it is not the study's zoning",
                zoneCity.getZoning().protectedShare() == .10 && zoneCity.getZoning().keptShare() == .25
                        && zoningLines(zoneCity) == zoneDecided + 1);

        Map<String, Integer> zoningEarlier = new HashMap<>();
        zoningEarlier.put(Lever.ZONING.name(), zoneMoved);
        zonePolicy.restoreOpenings(zoningEarlier);
        System.setOut(quiet);
        try { zoneCity.toggleNextMonth(); zoneCity.setZone(Sectors.RETAIL, Zoning.Size.SHARE, .05, .5); } finally { System.setOut(out); }
        check("a city whose zoning is moved is still refused in the month before it: no zone, and the decision log as it was",
                zoneCity.getMonth() == zoneMoved - 1 && !zoneCity.isLeverOpen(Lever.ZONING) && !zoneCity.getZoning().hasZones()
                        && zoningLines(zoneCity) == zoneDecided + 1);
        System.setOut(quiet);
        try { zoneCity.toggleNextMonth(); } finally { System.setOut(out); }
        int zoneLogged = zoningLines(zoneCity);
        System.setOut(quiet);
        try { zoneCity.setZone(Sectors.RETAIL, Zoning.Size.SHARE, .05, .5); } finally { System.setOut(out); }
        Zoning.Zone drawn = zoneCity.getZoning().zone(Sectors.RETAIL);
        check("in its month zoning is open and takes the zone, logged as a zoning decision",
                zoneCity.getMonth() == zoneMoved && zoneCity.isLeverOpen(Lever.ZONING) && drawn != null && drawn.size() == .05
                        && drawn.priceShare() == .5 && zoningLines(zoneCity) == zoneLogged + 1);
        int zoningNotices = 0;
        Notice zoningNotice = null;
        for (Notice n : zoneCity.getInbox().all()) if (n.getKey().equals("lever:" + Lever.ZONING.name())) { zoningNotices++; zoningNotice = n; }
        check("the inbox raised zoning's opening notice once, in the month it opened, titled by Inbox.leverTitle() with no article and history's date",
                zoningNotices == 1 && zoningNotice.getRaised() == zoneMoved && zoningNotice.getTitle().equals(Inbox.leverTitle(Lever.ZONING))
                        && zoningNotice.getTitle().equals("Zoning: " + PolicyDates.openingDate(Lever.ZONING)));
        check("...with Canada's first terms for its body, which say no ground is zoned until you draw a zone",
                zoningNotice != null && String.join(" ", zoningNotice.getBody()).equals(PolicyDates.notice(Lever.ZONING))
                        && PolicyDates.notice(Lever.ZONING).contains("No ground is zoned until you draw a zone"));
        System.setOut(quiet);
        try { zoneCity.toggleNextMonth(); } finally { System.setOut(out); }
        int zoningAgain = 0;
        for (Notice n : zoneCity.getInbox().all()) if (n.getKey().equals("lever:" + Lever.ZONING.name())) zoningAgain++;
        check("in the month after it is still one notice, and the zone stands", zoningAgain == 1 && zoneCity.getZoning().zone(Sectors.RETAIL) != null);

        // TaxPolicy.openNow(): one lever, and no other.
        Game nowCity = new Game(GameFiles.scratch("r0-zoning-now"));
        System.setOut(quiet);
        try { nowCity.run(); } finally { System.setOut(out); }
        TaxPolicy nowPolicy = nowCity.getEconomyManager().getTaxPolicy();
        nowPolicy.openNow(Lever.ZONING);
        boolean othersShut = true;
        for (Lever l : TaxPolicy.GATED) othersShut &= !nowCity.isLeverOpen(l);
        check("openNow(ZONING) opens zoning from month 1 and nothing else: the wage tax, the minimum wage and every gated lever stay shut",
                nowCity.getMonth() == 1 && nowCity.isLeverOpen(Lever.ZONING) && nowCity.leverOpeningMonth(Lever.ZONING) == 1
                        && !nowCity.isLeverOpen(Lever.WAGE_TAX) && !nowCity.isLeverOpen(Lever.MINIMUM_WAGE) && othersShut);
        System.setOut(quiet);
        try { nowCity.setZone(Sectors.RETAIL, Zoning.Size.SHARE, .05, .5); } finally { System.setOut(out); }
        check("...and the zone is taken in month 1", nowCity.getZoning().zone(Sectors.RETAIL) != null);
        nowPolicy.openNow(Lever.WAGE_TAX);
        check("a tax opened so is open and, until the month is told, still reads zero", nowCity.isLeverOpen(Lever.WAGE_TAX) && nowPolicy.getWageTaxRate() == 0);
        System.setOut(quiet);
        try { nowCity.toggleNextMonth(); } finally { System.setOut(out); }
        check("...and the month after it opens at the founding rate, as history's would, while the profit and sales taxes stay shut at zero",
                nowPolicy.getWageTaxRate() == TaxPolicy.DEFAULT_INCOME_TAX && !nowCity.isLeverOpen(Lever.PROFIT_TAX)
                        && !nowCity.isLeverOpen(Lever.SALES_TAX) && nowPolicy.getProfitTaxRate() == 0 && nowPolicy.getSalesTaxRate() == 0);

        /* ==================== 13. the shut lever's words ==================== */
        System.out.println("\n--- a shut lever's card: the date, the lever with its article and the terms; then where it opens ---");
        boolean namesAgree = true;
        boolean linesRead = true;
        boolean whysRead = true;
        for (Lever l : TaxPolicy.GATED) {
            namesAgree &= Inbox.leverTitle(l).equals(Character.toUpperCase(PolicyDates.named(l).charAt(0)) + PolicyDates.named(l).substring(1)
                    + ": " + PolicyDates.openingDate(l));
            linesRead &= PolicyDates.shutLine(l).equals("Opens " + PolicyDates.openingDate(l) + " - " + PolicyDates.named(l) + ": " + l.terms())
                    && PolicyDates.shutLine(l).equals(PolicyDates.shutLine(l, PolicyDates.openingMonth(l)));
            whysRead &= PolicyDates.shutWhy(l).startsWith("Canada had none until then; ") && PolicyDates.shutWhy(l).endsWith(".")
                    && PolicyDates.shutWhy(l).toLowerCase().contains(PolicyDates.opensAt(l).toLowerCase());
        }
        check("the inbox's title is built from the same article rule the card uses (PolicyDates.named()), for every gated lever", namesAgree);
        check("a shut lever's first line is \"Opens <date> - <the lever>: <Canada's terms>\", and the plain form is the city on history's month", linesRead);
        check("its second line is \"Canada had none until then; \" and where the lever opens (the founding rate, zero, or the going wage)", whysRead);
        check("an income tax has its article and the other forms their own: an income tax, a profit tax, an old-age pension, student aid, EI, zoning",
                PolicyDates.named(Lever.WAGE_TAX).equals("an income tax") && PolicyDates.named(Lever.PROFIT_TAX).equals("a profit tax")
                        && PolicyDates.named(Lever.PENSION_REPLACEMENT).equals("an old-age pension")
                        && PolicyDates.named(Lever.STUDENT_AID).equals("student aid") && PolicyDates.named(Lever.EI).equals(Lever.EI.label())
                        && PolicyDates.named(Lever.ZONING).equals("zoning"));
        check("an income tax's card says it opens at the city's founding rate, built from the model's constant",
                PolicyDates.shutWhy(Lever.WAGE_TAX).contains("founding rate, " + DecisionLog.pct(TaxPolicy.DEFAULT_INCOME_TAX))
                        && PolicyDates.shutWhy(Lever.EI).contains("opens at zero")
                        && PolicyDates.shutWhy(Lever.MINIMUM_WAGE).contains("going unskilled wage"));
        int ei = PolicyDates.openingMonth(Lever.EI);
        check("a lever the study dates to a month prints it (the unemployment insurance act's month), one it dates to a year prints the year",
                PolicyDates.shutLine(Lever.EI).startsWith("Opens " + CityCalendar.format(ei) + " - ")
                        && PolicyDates.shutLine(Lever.WAGE_TAX).startsWith("Opens " + CityCalendar.yearOf(PolicyDates.openingMonth(Lever.WAGE_TAX)) + " - "));
        int earlierEi = PolicyDates.earliestOpening(Lever.EI);
        check("a city whose opening a find has moved is shown its own date, and told history's and the find where the card says \"then\"",
                earlierEi < ei && PolicyDates.shutLine(Lever.EI, earlierEi).startsWith("Opens " + CityCalendar.format(earlierEi) + " - ")
                        && PolicyDates.shutWhy(Lever.EI, earlierEi).startsWith("Canada had none until " + PolicyDates.openingDate(Lever.EI)
                                + ", and a find has brought it forward; ")
                        && PolicyDates.shutWhy(Lever.EI, ei).equals(PolicyDates.shutWhy(Lever.EI)));

        System.out.println(fails == 0 ? "PolicyDatesCheck: all " + passes + " ok" : "PolicyDatesCheck: " + fails + " FAILED of " + (passes + fails));
        System.exit(fails);
    }
}
