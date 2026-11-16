package ham.citybuildersim;

import ham.citybuildersim.CausesOfDeath.Cause;
import ham.citybuildersim.CausesOfDeath.Step;
import ham.citybuildersim.ResearchCatalog.Entry;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Proves the research catalogue's medical rows (R2f) move the constants they map to: a medical row a 1900 city finds ahead of the world brings
 * its step on a cause of death forward, and a row it lacks leaves its constant undone until the month history had it or the month the city found
 * it - and a city that found nothing, or was founded the default way, is the city it was before R2f, to the bit.
 *
 * WHY. Before R2f a found medical row changed nothing: the catalogue (R2) dated the sixteen rows and the city's schools could find them (R3),
 * but no constant read the finds, so a 1900 city that found streptomycin ten years early died of TB on history's dates all the same, and its
 * general care cured the sick at today's rate in 1900. Research-design Q17 (Jerus, "rec all", 2026-10-18) maps each medical discovery to the
 * constants it moves. A row mapped to the wrong constant, or to none, or a step that does not come home to history's own, would move the
 * dying silently, and the default city (which finds nothing) must not move at all. So the map is held to the catalogue, the steps to history,
 * the undone constants to the row's month, and a real city to the model's own readings.
 *
 * What this has to prove:
 *
 *   1. every medical row is mapped: the catalogue's medicine rows are MedicalRows.CODES, each has a group (a step, a stays row, the TB drugs'
 *      long slot, the breathing machine, the building, or nothing), and the rows that move nothing are in no other group
 *   2. the steps are history's: each step's factor is what history's knots do over its dates
 *   3. a city that found nothing is history's path to the bit (TODAY, and a 1900 city with an empty research): ahead() is 1 for every cause
 *      and month, rates() equals the rates of no rows cell for cell, and Sickness's static factors are today's
 *   4. a row found ahead of the world brings its step forward, and history then has it: the product of the step's factors before history's
 *      ramp, 1 after it, one cell of the fixture's rates times the factor, and the formula written out for a find only a year ahead
 *   5. the undone constants switch at the row's month, and at a find: the long slot's recovery (M1), the served long sick's deaths (M2) and
 *      general care's stays (PM5 and the later rows)
 *   6. a ring reads them: two Sickness twins seeded and turned a month (the last slot's tail before M1, the deaths before M2, the recovery
 *      before the stays rows, and TODAY the ring of before R2f)
 *   7. a real city reads them: a 1900 village and a default village played, M1 found here, a twin with PM1 found at month 1, and M8's building
 *
 * The 1900 city of sections 3 and 4 is CausesOfDeathCheck's: Canada's 1901 pyramid at 100,000 people with no childcare, general care for
 * everybody, piped water, nobody hungry, and the mean outbreak running.
 */
public class MedicineCheck {

    static int fails = 0;

    /** The real output: quietly() swaps System.out, and the log is this. */
    static final PrintStream out = System.out;

    static PrintStream quiet;

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    static void check(String label, boolean ok) {
        check(label, ok, null);
    }

    /** The same, with what was found printed under a failure. */
    static void check(String label, boolean ok, String found) {
        if (!ok) fails++;
        out.println(label + " ... " + (ok ? "OK" : "FAIL"));
        if (!ok && found != null) out.println("       found: " + found);
    }

    static void near(String label, double actual, double expected, double tolerance) {
        check(label, Math.abs(actual - expected) <= tolerance,
                String.format("%.12g against %.12g (tolerance %g)", actual, expected, tolerance));
    }

    /** Within a relative tolerance of the expected value. */
    static void nearRel(String label, double actual, double expected, double rel) {
        near(label, actual, expected, rel * Math.abs(expected));
    }

    /* ----- the check's own readings ----- */

    static int month(int year, int monthOfYear) {
        return PolicyDates.monthOf(year, monthOfYear);
    }

    static int world(String code) {
        return ResearchCatalog.get().worldMonth(code);
    }

    /** A game month's decimal year, as history's knots and the steps read it. */
    static double decimalYear(int month) {
        return CityCalendar.yearOf(month) + (CityCalendar.monthOfYear(month) - 1) / 12.0;
    }

    /** The first game month whose decimal year is at or after a decimal year. */
    static int monthAtOrAfter(double decimalYear) {
        return 1 + (int) Math.ceil((decimalYear - CityCalendar.EPOCH_YEAR) * 12 - 1e-9);
    }

    static double clamp01(double v) {
        return Math.max(0, Math.min(1, v));
    }

    /** A research that found each code at the given month, and nothing else. */
    static Research finds(Map<String, Integer> found) {
        Research r = new Research();
        r.restore(Map.of(), Map.of(), Map.of(), found);
        return r;
    }

    /** A research that found one code this many months before the world. */
    static Research foundAhead(String code, int monthsAhead) {
        return finds(Map.of(code, world(code) - monthsAhead));
    }

    /** What a 1900-way city that found nothing knows. */
    static MedicalRows nothingFound() {
        return MedicalRows.of(new Research(), true);
    }

    /** The product of the factors of one row's steps on one cause: 1 where it has none. */
    static double stepsProduct(String code, Cause cause) {
        double f = 1;
        for (Step s : CausesOfDeath.STEPS) if (s.code().equals(code) && s.cause() == cause) f *= s.factor();
        return f;
    }

    /** The 1900 city's month with the rows given: CausesOfDeathCheck's fixture, at the mean outbreak, in the best of conditions. */
    static double[][] cityWith(CausesOfDeath m, int month, MedicalRows rows) {
        return m.rates(month, CausesOfDeathCheck.people(), CausesOfDeathCheck.topOfSwing(), 1, 0, 1,
                CausesOfDeath.OUTBREAK_MEAN_SEVERITY, CausesOfDeathCheck.births(), rows);
    }

    /** The same with no rows: the call that was before R2f. */
    static double[][] cityWithout(CausesOfDeath m, int month) {
        return CausesOfDeathCheck.city(m, month);
    }

    /** Where a row's group sits in the check's table. */
    enum Group { STEP, STAYS, LONG, SERVED, BUILDING, NOTHING }

    /** Every medical row's group or groups, typed from research-design Q17 and R2f's notes; section 1 holds the model to it. */
    static final Map<String, EnumSet<Group>> TABLE = new LinkedHashMap<>();

    static void row(String code, Group first, Group... more) {
        TABLE.put(code, EnumSet.of(first, more));
    }

    static {
        row("PM1", Group.STEP);
        row("PM2", Group.NOTHING);
        row("PM3", Group.STEP);
        row("PM4", Group.STEP);
        row("PM5", Group.STEP, Group.STAYS);
        row("PM6", Group.STEP);
        row("M1", Group.LONG);
        row("M2", Group.SERVED);
        row("M3", Group.NOTHING);
        row("M4", Group.NOTHING);
        row("M5", Group.STAYS);
        row("M6", Group.STAYS);
        row("M7", Group.NOTHING);
        row("M8", Group.BUILDING);
        row("M9", Group.STAYS);
        row("M10", Group.STAYS);
    }

    /** The rows that shorten general care's stays: PM5 first, then the later four. */
    static List<String> staysRows() {
        List<String> rows = new ArrayList<>();
        rows.add("PM5");
        rows.addAll(Sickness.LATER_STAYS_ROWS);
        return rows;
    }

    static double staysShare(String code) {
        return code.equals("PM5") ? Sickness.STAYS_BEFORE_PM5 : Sickness.STAYS_BEFORE_LATER_ROW;
    }

    public static void main(String[] args) {
        quiet = new PrintStream(OutputStream.nullOutputStream());
        FittingCheck.quiet = quiet;
        EquipmentCheck.quiet = quiet;
        sectionOne();
        sectionTwo();
        sectionThree();
        sectionFour();
        sectionFive();
        sectionSix();
        sectionSeven();

        out.println();
        out.println(fails == 0 ? "The medical rows move what they map to, and a city that finds nothing is history's." : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ==================== 1. every medical row is mapped ==================== */

    static void sectionOne() {
        out.println("\n--- 1. every medical row is mapped ---");
        List<String> catalogue = new ArrayList<>();
        for (Entry e : ResearchCatalog.get().entries()) if (e.field() == ResearchCatalog.Field.MEDICINE) catalogue.add(e.code());
        check("the catalogue's medicine rows, in file order, are exactly MedicalRows.CODES", catalogue.equals(MedicalRows.CODES),
                catalogue + " against " + MedicalRows.CODES);
        check("...and are the same sixteen codes whatever the order", new HashSet<>(catalogue).equals(new HashSet<>(MedicalRows.CODES))
                && catalogue.size() == MedicalRows.CODES.size(), catalogue + " against " + MedicalRows.CODES);
        check("the check's own table lists the catalogue's medicine rows", TABLE.keySet().equals(new HashSet<>(catalogue)) && TABLE.size() == catalogue.size(),
                TABLE.keySet().toString());

        Set<String> stepCodes = new HashSet<>();
        for (Step s : CausesOfDeath.STEPS) stepCodes.add(s.code());
        List<String> stays = staysRows();
        for (String code : catalogue) {
            EnumSet<Group> groups = TABLE.get(code);
            check(code + " has at least one group", groups != null && !groups.isEmpty(), String.valueOf(groups));
            if (groups == null) continue;
            check(code + " is a STEP row exactly when CausesOfDeath.STEPS has a step for it",
                    groups.contains(Group.STEP) == stepCodes.contains(code), groups + ", in STEPS: " + stepCodes.contains(code));
            check(code + " is a STAYS row exactly when it is PM5 or in Sickness.LATER_STAYS_ROWS",
                    groups.contains(Group.STAYS) == stays.contains(code), groups + ", in the stays rows: " + stays.contains(code));
            if (groups.contains(Group.NOTHING)) {
                check(code + " moves nothing: in no other group, in neither STEPS nor LATER_STAYS_ROWS",
                        groups.size() == 1 && !stepCodes.contains(code) && !Sickness.LATER_STAYS_ROWS.contains(code), groups.toString());
            }
        }
        boolean stepsAreMedical = true, staysAreMedical = true;
        for (String code : stepCodes) stepsAreMedical &= catalogue.contains(code);
        for (String code : Sickness.LATER_STAYS_ROWS) staysAreMedical &= catalogue.contains(code);
        check("every code in CausesOfDeath.STEPS is a medical row", stepsAreMedical, stepCodes.toString());
        check("every code in Sickness.LATER_STAYS_ROWS is a medical row", staysAreMedical, Sickness.LATER_STAYS_ROWS.toString());
    }

    /* ==================== 2. the steps are history's ==================== */

    static void sectionTwo() {
        out.println("\n--- 2. the steps are history's ---");
        double tol = 1e-9;
        near("history's pneumonia in January 1940 over January 1937 is sulfa's factor on pneumonia",
                CausesOfDeath.history(Cause.PNEUMONIA, month(1940, 1)) / CausesOfDeath.history(Cause.PNEUMONIA, month(1937, 1)),
                CausesOfDeath.SULFA_PNEUMONIA, tol);
        near("history's childbirth in January 1940 over January 1937 is sulfa's factor on mothers",
                CausesOfDeath.history(Cause.CHILDBIRTH, month(1940, 1)) / CausesOfDeath.history(Cause.CHILDBIRTH, month(1937, 1)),
                CausesOfDeath.SULFA_CHILDBIRTH, tol);
        near("history's childbirth in January 1944 over January 1940 is penicillin's factor on mothers",
                CausesOfDeath.history(Cause.CHILDBIRTH, month(1944, 1)) / CausesOfDeath.history(Cause.CHILDBIRTH, month(1940, 1)),
                CausesOfDeath.PENICILLIN_CHILDBIRTH, tol);
        near("history's TB in January 1952 over January 1948 is streptomycin's factor on TB",
                CausesOfDeath.history(Cause.TB, month(1952, 1)) / CausesOfDeath.history(Cause.TB, month(1948, 1)),
                CausesOfDeath.STREPTOMYCIN_TB, tol);
        for (Step s : CausesOfDeath.STEPS) {
            check(s.code() + "'s step on " + s.cause().name() + " leaves a factor strictly between 0 and 1 and takes more than no years",
                    s.factor() > 0 && s.factor() < 1 && s.years() > 0, "factor " + s.factor() + ", years " + s.years());
        }
    }

    /* ==================== 3. a city that found nothing is history's path to the bit ==================== */

    static void sectionThree() {
        out.println("\n--- 3. a city that found nothing is history's path to the bit ---");
        int last = month(1980, 12);
        CausesOfDeath model = new CausesOfDeath();
        MedicalRows[] readings = {MedicalRows.TODAY, nothingFound()};
        String[] names = {"TODAY", "a 1900 city with an empty research"};
        for (int i = 0; i < readings.length; i++) {
            MedicalRows rows = readings[i];
            String first = null;
            for (Cause c : Cause.values()) {
                for (int m = 1; m <= last && first == null; m++) {
                    if (CausesOfDeath.ahead(c, m, rows) != 1.0) first = c.name() + " in month " + m + " is " + CausesOfDeath.ahead(c, m, rows);
                }
            }
            check("for " + names[i] + " ahead() is exactly 1 for every cause and every month to December 1980", first == null, first);

            int[] years = {1914, 1926, 1938, 1946, 1950};
            for (int year : years) {
                int m = month(year, 1);
                double[][] with = cityWith(model, m, rows);
                double[][] without = cityWithout(model, m);
                String differs = null;
                for (int c = 0; c < with.length && differs == null; c++) {
                    for (int b = 0; b < with[c].length; b++) {
                        if (with[c][b] != without[c][b]) { differs = Cause.values()[c] + " band " + b + ": " + with[c][b] + " against " + without[c][b]; break; }
                    }
                }
                check("for " + names[i] + " every rates() cell of January " + year + " equals the cell with no rows, with ==", differs == null, differs);
            }
        }

        MedicalRows today = MedicalRows.TODAY;
        String stray = null;
        for (int m = 1; m <= last && stray == null; m++) {
            if (Sickness.staysFactor(today, m) != 1 || Sickness.longRecoveryFactor(today, m) != 1 || Sickness.servedDeathsBefore(today, m) != 0) {
                stray = "month " + m;
            }
        }
        check("Sickness's three static factors read 1, 1 and 0 for TODAY in every month to December 1980", stray == null, stray);

        int caughtUp = 1;
        for (String code : MedicalRows.CODES) caughtUp = Math.max(caughtUp, world(code));
        MedicalRows empty = nothingFound();
        check("a 1900 city that found nothing reads 1, 1 and 0 from the month of the last medical row on",
                Sickness.staysFactor(empty, caughtUp) == 1 && Sickness.longRecoveryFactor(empty, caughtUp) == 1
                        && Sickness.servedDeathsBefore(empty, caughtUp) == 0,
                Sickness.staysFactor(empty, caughtUp) + ", " + Sickness.longRecoveryFactor(empty, caughtUp) + ", "
                        + Sickness.servedDeathsBefore(empty, caughtUp));
    }

    /* ==================== 4. a row found ahead of the world brings its step forward, and history then has it ==================== */

    static void sectionFour() {
        out.println("\n--- 4. a row found ahead of the world brings its step forward, and history then has it ---");
        CausesOfDeath model = new CausesOfDeath();
        int ahead = 120;
        for (Step s : CausesOfDeath.STEPS) {
            String name = s.code() + "'s step on " + s.cause().name();
            Research research = foundAhead(s.code(), ahead);
            MedicalRows rows = MedicalRows.of(research, true);
            check(name + ": the city found it " + ahead + " months before the world", rows.monthsAhead(s.code()) == ahead,
                    String.valueOf(rows.monthsAhead(s.code())));

            int before = monthAtOrAfter(s.from()) - 1;
            double p = clamp01((decimalYear(before) - s.from()) / s.years());
            check(name + ": a month before history's ramp has none of it done, and the city's ramp is done (a step takes under ten years)",
                    p == 0 && s.years() < ahead / 12.0, "p " + p + ", years " + s.years());
            double expected = stepsProduct(s.code(), s.cause());
            near(name + ": a month before history's ramp, ahead() is the product of the row's factors on the cause",
                    CausesOfDeath.ahead(s.cause(), before, rows), expected, 1e-12);

            boolean others = true;
            for (Cause c : Cause.values()) {
                if (c == s.cause()) continue;
                others &= Math.abs(CausesOfDeath.ahead(c, before, rows) - stepsProduct(s.code(), c)) <= 1e-12;
            }
            check(name + ": every other cause is its own steps' product for the row, and 1 where it has none", others);

            double[][] base = cityWithout(model, before);
            double[][] found = cityWith(model, before, rows);
            int ci = s.cause().ordinal();
            int band = 0;
            for (int b = 1; b < base[ci].length; b++) if (base[ci][b] > base[ci][band]) band = b;
            check(name + ": the fixture has a rate for the cause in its largest band (" + AgeBand.values()[band] + ")", base[ci][band] > 0,
                    String.valueOf(base[ci][band]));
            nearRel(name + ": the rates() cell of that band is the cell with no rows times the factor", found[ci][band], base[ci][band] * expected, 1e-9);

            int after = monthAtOrAfter(s.from() + s.years()) + 1;
            near(name + ": a month after history's ramp ends, ahead() is 1", CausesOfDeath.ahead(s.cause(), after, rows), 1, 1e-12);

            /* a find a year ahead, in the middle of history's ramp: the formula written out from the step's fields */
            MedicalRows aYear = MedicalRows.of(foundAhead(s.code(), 12), true);
            int mid = monthAtOrAfter(s.from() + s.years() / 2);
            double y = decimalYear(mid);
            double history = clamp01((y - s.from()) / s.years());
            double city = clamp01((y - s.from() + 12 / 12.0) / s.years());
            double formula = (1 - (1 - s.factor()) * city) / (1 - (1 - s.factor()) * history);
            check(name + ": a find 12 months ahead, midway through history's ramp, has the city's ramp ahead of history's", city > history && formula < 1,
                    "history " + history + ", city " + city + ", formula " + formula);
            near(name + ": ...and ahead() is the step's formula, (1 - (1 - factor) q) over (1 - (1 - factor) p)",
                    CausesOfDeath.ahead(s.cause(), mid, aYear), formula, 1e-12);
        }
    }

    /* ==================== 5. the undone constants switch at the row's month, and at a find ==================== */

    static void sectionFive() {
        out.println("\n--- 5. the undone constants switch at the row's month, and at a find ---");
        MedicalRows rows = nothingFound();
        int m1 = world("M1"), m2 = world("M2");
        near("the month before M1 the long slot recovers at LONG_RECOVERY_BEFORE_M1 of the month's recovery",
                Sickness.longRecoveryFactor(rows, m1 - 1), Sickness.LONG_RECOVERY_BEFORE_M1, 1e-12);
        near("...and in full from M1's month", Sickness.longRecoveryFactor(rows, m1), 1, 1e-12);
        near("the served long sick die SERVED_DEATHS_BEFORE_M2 more the month before M2",
                Sickness.servedDeathsBefore(rows, m2 - 1), Sickness.SERVED_DEATHS_BEFORE_M2, 1e-12);
        near("...and no more from M2's month", Sickness.servedDeathsBefore(rows, m2), 0, 1e-12);

        List<String> stays = staysRows();
        Set<Integer> months = new HashSet<>();
        boolean after1900 = true;
        for (String code : stays) { months.add(world(code)); after1900 &= world(code) > 1; }
        check("fixture: the stays rows are dated in different months, all after the first", months.size() == stays.size() && after1900, months.toString());
        near("general care's part of the recovery in month 1 is STAYS_BEFORE_PM5 times STAYS_BEFORE_LATER_ROW for each later row",
                Sickness.staysFactor(rows, 1),
                Sickness.STAYS_BEFORE_PM5 * Math.pow(Sickness.STAYS_BEFORE_LATER_ROW, Sickness.LATER_STAYS_ROWS.size()), 1e-12);
        for (String code : stays) {
            int w = world(code);
            nearRel(code + ": the stays factor in its month over the month before is one over its share",
                    Sickness.staysFactor(rows, w) / Sickness.staysFactor(rows, w - 1), 1 / staysShare(code), 1e-12);
        }
        int lastStays = 1;
        for (String code : stays) lastStays = Math.max(lastStays, world(code));
        near("...and once every stays row is known the factor is 1", Sickness.staysFactor(rows, lastStays), 1, 1e-12);

        /* found here 60 months before the world */
        int back = 60;
        Research research = finds(Map.of("M1", m1 - back, "M2", m2 - back, "M5", world("M5") - back));
        MedicalRows found = MedicalRows.of(research, true);
        int f1 = m1 - back, f2 = m2 - back, f5 = world("M5") - back;
        check("M1, M2 and M5 each found " + back + " months before the world",
                found.monthsAhead("M1") == back && found.monthsAhead("M2") == back && found.monthsAhead("M5") == back,
                found.monthsAhead("M1") + ", " + found.monthsAhead("M2") + ", " + found.monthsAhead("M5"));
        near("with M1 found here the long slot is undone the month before the find", Sickness.longRecoveryFactor(found, f1 - 1), Sickness.LONG_RECOVERY_BEFORE_M1, 1e-12);
        near("...and recovers in full from the month of the find", Sickness.longRecoveryFactor(found, f1), 1, 1e-12);
        near("with M2 found here the served long sick die more the month before the find", Sickness.servedDeathsBefore(found, f2 - 1), Sickness.SERVED_DEATHS_BEFORE_M2, 1e-12);
        near("...and no more from the month of the find", Sickness.servedDeathsBefore(found, f2), 0, 1e-12);
        nearRel("with M5 found here the stays factor in the month of the find over the month before is one over STAYS_BEFORE_LATER_ROW",
                Sickness.staysFactor(found, f5) / Sickness.staysFactor(found, f5 - 1), 1 / Sickness.STAYS_BEFORE_LATER_ROW, 1e-12);
        near("...while a city that found nothing is still undone the month of M5's find",
                Sickness.staysFactor(rows, f5), Sickness.staysFactor(rows, f5 - 1), 1e-12);
    }

    /* ==================== 6. a ring reads them ==================== */

    /** Every code in the list found at month 1: those rows are known in any month. */
    static MedicalRows knowing(List<String> codes) {
        Map<String, Integer> found = new LinkedHashMap<>();
        for (String code : codes) found.put(code, 1);
        return MedicalRows.of(finds(found), true);
    }

    /** A ring seeded at the rate with the month-1 medicine of the rows, general care at the coverage. */
    static Sickness twin(MedicalRows rows, double coverage) {
        Sickness s = new Sickness();
        s.setMedicine(rows, 1, coverage);
        s.seed(RATE, RATE, coverage, 1, 1);
        return s;
    }

    /** The sick rate the twins are seeded at. */
    static final double RATE = .05;

    static double ringSum(Sickness s, AgeBand band) {
        double sum = 0;
        for (int k = 0; k < Sickness.RING; k++) sum += s.slot(band, k);
        return sum;
    }

    /** One advanceMonth on a twin keeps every slot finite and every ring at its band's share. */
    static void keepsItsShare(String name, Sickness s, double coverage) {
        s.advanceMonth(RATE, coverage, 1, 1);
        boolean finite = true;
        String off = null;
        for (AgeBand b : AgeBand.values()) {
            for (int k = 0; k < Sickness.RING; k++) finite &= Double.isFinite(s.slot(b, k)) && s.slot(b, k) >= 0;
            double share = Sickness.shareFor(b, RATE, 1, 1);
            if (off == null && Math.abs(ringSum(s, b) - share) > 1e-12) off = b + ": " + ringSum(s, b) + " against " + share;
        }
        check(name + ": a month on keeps every slot finite and not below nothing", finite);
        check(name + ": ...and every band's ring at the band's share", off == null, off);
    }

    static void sectionSix() {
        out.println("\n--- 6. a ring reads them ---");
        List<String> allButM1 = staysRows();
        allButM1.add("M2");
        MedicalRows noM1 = knowing(allButM1);
        List<String> everything = new ArrayList<>(allButM1);
        everything.add("M1");
        MedicalRows everyRow = knowing(everything);

        Sickness a = twin(noM1, 1), b = twin(everyRow, 1);
        check("fixture: the M1-unknown twin has the long slot undone and the other has it whole",
                a.getLongRecovery() == Sickness.LONG_RECOVERY_BEFORE_M1 && b.getLongRecovery() == 1,
                a.getLongRecovery() + " and " + b.getLongRecovery());
        AgeBand adult = AgeBand.ADULT;
        double h = Sickness.deathChance(adult);
        double r = a.getLastRecovery();
        double q = (1 - h) * (1 - r);
        double lastOver = a.slot(adult, Sickness.RING - 1) / a.slot(adult, Sickness.RING - 2);
        nearRel("the M1-unknown ring's last slot over the one before is q over one less (1 - h)(1 - r x LONG_RECOVERY_BEFORE_M1)",
                lastOver, q / (1 - (1 - h) * (1 - r * Sickness.LONG_RECOVERY_BEFORE_M1)), 1e-12);
        double rB = b.getLastRecovery();
        nearRel("the ring with M1 known has its last slot over the one before q over one less q",
                b.slot(adult, Sickness.RING - 1) / b.slot(adult, Sickness.RING - 2), (1 - h) * (1 - rB) / (1 - (1 - h) * (1 - rB)), 1e-12);
        check("the M1-unknown ring keeps more of its sick in the year-or-more slot", a.aYearOrMore(adult) > b.aYearOrMore(adult),
                a.aYearOrMore(adult) + " against " + b.aYearOrMore(adult));
        double share = Sickness.shareFor(adult, RATE, 1, 1);
        nearRel("the M1-unknown ring's slots sum to the adults' share", ringSum(a, adult), share, 1e-12);
        nearRel("the ring with M1 known has its slots sum to the adults' share", ringSum(b, adult), share, 1e-12);

        /* M2 unknown, general care at half */
        List<String> allButM2 = staysRows();
        allButM2.add("M1");
        Sickness c = twin(knowing(allButM2), .5);
        nearRel("with only M2 unknown at half coverage the death lift is SERVED_DEATHS_BEFORE_M2 times a half",
                c.getDeathLift(), Sickness.SERVED_DEATHS_BEFORE_M2 * .5, 1e-12);
        nearRel("...and the adults' death rate is the share sick past two months times their chance times one plus the lift",
                c.deathRates()[adult.ordinal()],
                c.pastTwoMonths(adult) * Sickness.deathChance(adult) * (1 + Sickness.SERVED_DEATHS_BEFORE_M2 * .5), 1e-12);

        /* the stays rows, and TODAY the ring of before R2f */
        Sickness d = twin(nothingFound(), 1);
        double stays = Sickness.STAYS_BEFORE_PM5 * Math.pow(Sickness.STAYS_BEFORE_LATER_ROW, Sickness.LATER_STAYS_ROWS.size());
        nearRel("a twin on 1900's rows at month 1 recovers at the untreated share plus the served part times the stays factors, general care at 1",
                d.getLastRecovery(), Sickness.RECOVERY_UNTREATED + (Sickness.RECOVERY_SERVED - Sickness.RECOVERY_UNTREATED) * stays, 1e-12);
        Sickness today = twin(MedicalRows.TODAY, 1);
        near("a TODAY twin recovers at RECOVERY_SERVED", today.getLastRecovery(), Sickness.RECOVERY_SERVED, 1e-12);
        Sickness before = new Sickness();
        before.seed(RATE, RATE, 1, 1, 1);
        boolean same = true;
        for (AgeBand band : AgeBand.values()) for (int k = 0; k < Sickness.RING; k++) same &= today.slot(band, k) == before.slot(band, k);
        check("a TODAY twin's ring is, slot for slot with ==, a ring that was never given its medicine", same);
        check("...and its stays factor, long slot and death lift are 1, 1 and 0",
                today.getStaysFactor() == 1 && today.getLongRecovery() == 1 && today.getDeathLift() == 0);

        keepsItsShare("the M1-unknown twin", a, 1);
        keepsItsShare("the twin with M1 known", b, 1);
        keepsItsShare("the M2-unknown twin", c, .5);
        keepsItsShare("the twin on 1900's rows", d, 1);
        keepsItsShare("the TODAY twin", today, 1);
    }

    /* ==================== 7. a real city reads them ==================== */

    /** A village (FittingCheck.village) with a General Hospital stack standing and the staff handed; founded the 1900 way or the default way. */
    static Game hospitalVillage(String label, boolean the1900Way) {
        Game g = FittingCheck.village(label, the1900Way ? Founding.defaults().withUnfitted(true) : Founding.defaults(), false, false);
        quietly(() -> {
            BuildingManager b = g.getBuildingManager();
            g.buildStack(b.getTemplateByName("General Hospital"), 2, true);
            MaterialsCheck.handWorkers(g);
        });
        return g;
    }

    static void play(Game g, int months) {
        quietly(() -> { for (int i = 0; i < months; i++) g.toggleNextMonth(); });
    }

    static boolean stands(Game g, String name) {
        for (BuildingsStacks s : g.getBuildingManager().getStacksView()) if (s.getName().equals(name) && s.getQuantity() > 0) return true;
        return false;
    }

    static void sectionSeven() {
        out.println("\n--- 7. a real city reads them ---");
        Game old = hospitalVillage("medicinecheck-1900", true);
        Game today = hospitalVillage("medicinecheck-default", false);
        check("fixture: both villages have a General Hospital standing", stands(old, "General Hospital") && stands(today, "General Hospital"));
        check("fixture: the 1900 village is founded the 1900 way and reads its research, the default one reads TODAY",
                old.foundedUnfitted() && !old.medicalRows().isToday() && !today.foundedUnfitted() && today.medicalRows().isToday());
        play(old, 2);
        play(today, 2);

        double stays = Sickness.STAYS_BEFORE_PM5 * Math.pow(Sickness.STAYS_BEFORE_LATER_ROW, Sickness.LATER_STAYS_ROWS.size());
        Sickness ring = old.getSickness();
        double coverage = old.getHealthcare().getCoverage(CareType.GENERAL);
        near("the 1900 village's stays factor after two months is STAYS_BEFORE_PM5 times STAYS_BEFORE_LATER_ROW for each later row",
                ring.getStaysFactor(), stays, 1e-12);
        near("...its long slot recovers at LONG_RECOVERY_BEFORE_M1", ring.getLongRecovery(), Sickness.LONG_RECOVERY_BEFORE_M1, 1e-12);
        check("fixture: its general care covers some of the people", coverage > 0, String.valueOf(coverage));
        near("...and its death lift is SERVED_DEATHS_BEFORE_M2 times the month's general coverage", ring.getDeathLift(),
                Sickness.SERVED_DEATHS_BEFORE_M2 * coverage, 1e-12);
        check("...and above nothing", ring.getDeathLift() > 0, String.valueOf(ring.getDeathLift()));

        Sickness plain = today.getSickness();
        check("the default village's factors are 1, 1 and 0 and it reads TODAY",
                plain.getStaysFactor() == 1 && plain.getLongRecovery() == 1 && plain.getDeathLift() == 0 && today.medicalRows().isToday(),
                plain.getStaysFactor() + ", " + plain.getLongRecovery() + ", " + plain.getDeathLift());

        /* M1 found here: the long slot recovers in full from the next month */
        Research r = old.getResearch();
        r.restore(r.grantsForSave(), Map.of(), Map.of(), Map.of("M1", old.getMonth()));
        play(old, 1);
        check("with M1 found here the 1900 village's long slot recovers in full the next month", old.getSickness().getLongRecovery() == 1,
                String.valueOf(old.getSickness().getLongRecovery()));
        near("...while the stays are still undone", old.getSickness().getStaysFactor(), stays, 1e-12);

        /* twins, one with PM1 found at month 1 */
        Game other = hospitalVillage("medicinecheck-twin", true);
        Game with = hospitalVillage("medicinecheck-found", true);
        play(other, 3);
        play(with, 3);
        CausesOfDeath bookOther = other.getCausesOfDeath(), bookWith = with.getCausesOfDeath();
        check("fixture: the twins are identical before the find, in people and in the TB dead last month",
                other.getCohorts().total() == with.getCohorts().total()
                        && bookOther.getLastDeaths(Cause.TB) == bookWith.getLastDeaths(Cause.TB) && bookOther.getLastDeaths(Cause.TB) > 0,
                other.getCohorts().total() + " and " + with.getCohorts().total() + ", TB " + bookOther.getLastDeaths(Cause.TB) + " and "
                        + bookWith.getLastDeaths(Cause.TB));
        Research ro = other.getResearch(), rw = with.getResearch();
        ro.restore(ro.grantsForSave(), Map.of(), Map.of(), Map.of());
        rw.restore(rw.grantsForSave(), Map.of(), Map.of(), Map.of("PM1", 1));
        play(other, 1);
        play(with, 1);
        double factor = CausesOfDeath.ahead(Cause.TB, with.getMonth(), with.medicalRows());
        check("PM1 found at month 1 brings milk's step on TB forward: the factor in the month played is below 1", factor < 1, String.valueOf(factor));
        check("fixture: the twins played the same month", other.getMonth() == with.getMonth(), other.getMonth() + " and " + with.getMonth());
        double base = bookOther.getLastDeaths(Cause.TB);
        check("fixture: the twin without the find booked TB dead", base > 0, String.valueOf(base));
        nearRel("the found twin's TB dead over the other's is CausesOfDeath.ahead() for the month", bookWith.getLastDeaths(Cause.TB) / base, factor, 1e-6);

        /* M8 is a building */
        BuildingsTemplate residence = old.getBuildingManager().getTemplateByName("Assisted Living Residence");
        check("the Assisted Living Residence is dated by M8", residence != null && residence.getSinceText().equals("M8"),
                residence == null ? "no such template" : residence.getSinceText());
        if (residence == null) return;
        int m8 = world("M8");
        check("it is unknown in a city that found nothing the month before M8's", !residence.knownAt(m8 - 1, new Research()));
        check("...and known from M8's month", residence.knownAt(m8, new Research()));
        Research foundM8 = finds(Map.of("M8", 100));
        check("with M8 found at month 100 it is known at month 100", residence.knownAt(100, foundM8));
        check("...and not the month before", !residence.knownAt(99, foundM8));
    }
}
