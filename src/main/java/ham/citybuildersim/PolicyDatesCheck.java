package ham.citybuildersim;

import ham.citybuildersim.PolicyDates.Lever;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Every Policy lever opens on the date Canada first had it, and no lever may open more than the law field's four years early (R0, slice 1:
 * PolicyDates).
 *
 * WHY. PolicyDates is pure data that the gate in the model and the Policy screen will read, and a wrong date is no error anywhere: a tax that
 * opens a year late is a city that goes untaxed for a year, and nobody sees it until the ensemble moves. So the dates are held to the study's
 * table here, row for row, with the table typed out below as the specification and not read back from the class under test; research-1900.md
 * 7.1 is the source. Rows the study dates that the game has no lever for (PL1 the 1915 workers' compensation levy, PL11 care subsidies, L1
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
 *      year (and its month where the study gives one); an open lever has no key, its reason, and no notice.
 *   6. HISTORY'S ORDER. The dates run in the table's order, which is the declared order, and no two dated levers share an entry key.
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
        }
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

        System.out.println(fails == 0 ? "PolicyDatesCheck: all " + passes + " ok" : "PolicyDatesCheck: " + fails + " FAILED of " + (passes + fails));
        System.exit(fails);
    }
}
