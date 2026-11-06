package ham.citybuildersim;

import ham.citybuildersim.PolicyDates.Lever;
import ham.citybuildersim.ResearchCatalog.Entry;
import ham.citybuildersim.ResearchCatalog.Field;
import ham.citybuildersim.ResearchCatalog.Size;

import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The research catalogue is read whole from research.json, every discovery is on history's date with what it needs first, and the reader refuses
 * what is not a discovery (R2, slice A: ResearchCatalog and research.json; docs/studies/research-design.md 4, research-1900.md 8).
 *
 * WHY. research.json is 89 entries typed from two studies' tables, and a wrong figure in it is no error anywhere: a discovery a year late is an
 * oxygen converter that arrives in 1953, and nobody sees it until the ensemble moves. So the dates, the needs, the work and the fees are held to
 * the studies' tables here, with spot values typed out below as the specification and not read back from the class under test; the law rows are
 * held to PolicyDates, whose dates the Policy gate already reads, so the catalogue and the gate cannot disagree about when a lever opens.
 *
 * What it has to prove:
 *
 *   1. THE FILE READS. The packaged file gives 89 entries and a fresh reading of its text reports nothing (no entry skipped); the codes are
 *      unique and in the studies' order; the counts by prefix and by field are the studies'; every entry has a name, a world, a touches and an
 *      effect; the kinds, the kind notes and the names of a few are the tables'.
 *   2. NEEDS. Every need is a code in the catalogue, no entry needs itself, history dates each need no later than its entry; the tables' 26
 *      needs of 23 entries are there; M1 needs PM6 (research-1900 8.6), E18 needs E13 and E9, PE17 needs PE14.
 *   3. HISTORY'S DATES. Every entry's world month is PolicyDates.monthOf() of its year and month; values typed from the tables (a dated month, and
 *      the study's model's month where the table names none); every PolicyDates lever with an entry key is a LAW entry on the lever's own year,
 *      month and monthKnown; PL1 and PL11, which have no lever, are in their year's January; nothing is known at the founding.
 *   4. THE WORK. Every entry's work is the rule's, rounded, to within 1; values typed from the tables; the size of the work is the table's (a
 *      Walk-in Clinic is a recipe's work); the rule's constants are the study's and a discovery costs more every year.
 *   5. KNOWN FROM. A year is known from its January, a code from its entry's month, a blank "since" or a code the catalogue lacks never; every
 *      code buildings.json uses is in the catalogue; the catalogue is one list, unmodifiable, and the same for every thread.
 *   6. THE READER'S REFUSALS, AND THE FEES. A duplicate code keeps the first and reports; an unknown field, size or kind, a month 13, a year
 *      before 1900, a negative work and a blank code are skipped and reported; a need that is not in the catalogue and one dated after its entry
 *      are reported and the entry stays; a text that is not JSON gives an empty catalogue. And the fees: no law entry carries one but L4 (the DNA lab, equipment), and the tables'
 *      yes and no of a few engineering, medicine and business entries are as typed.
 *   7. BUILDINGS.JSON'S CODES (slice B). Every "since" that is not a year, on a template, a recipe or a piece, is a code in the catalogue: a
 *      template's one that unlocks a building, a piece's one that unlocks equipment, a recipe's one that is not a building alone. The
 *      code-dated templates are exactly research-1900.md 1.2's list typed out by id (29), the Oxygen Steel Shop is dated 1900, the Panel Mill 1980
 *      and the Wood Pellet Mill 1970, every other row undated or 1900; every building entry opens a template but PE12 and B8; a template's year
 *      is its entry's. A building file read from text keeps a code as written, reports one the catalogue lacks and never knows it.
 *   8. KNOWN FROM HISTORY'S MONTH. The tractor's piece and recipe are known from PE14's month and not the month before, the oxygen converter's
 *      recipe from E1's (November 1952), the Container Terminal from E5's, the Electric Arc Furnace from PE1's December 1900, the Pulp Mill's kraft
 *      recipe from January 1907; an undated row, and one dated 1900, from the first month; and asking by the year (knownIn) agrees with asking
 *      by the month of that year's January, 1900 to 1960, and nothing dated 1900 is known the year before the start.
 *   9. THE VERDICT AND THE CHOICE ON HISTORY'S MONTH. EquipmentFit.judge() for the tractor with figures that pay is NOT_KNOWN the month before
 *      PE14's and PAYS at it (and figures a thousandth short of paying are NOT_YET at it, so the rule runs), and Recipes.canRun() for the
 *      oxygen converter's recipe is false the month before E1's and true at it, for the part that may run it (the one without the open hearth).
 */
public class ResearchCheck {

    private static int fails;
    private static int passes;

    static void check(String label, boolean ok) {
        System.out.println((ok ? "  PASS " : "  FAIL ") + label);
        if (ok) passes++; else fails++;
    }

    /** The same, with what was found printed under a failure. */
    static void check(String label, boolean ok, String found) {
        check(label, ok);
        if (!ok && found != null) System.out.println("       found: " + found);
    }

    /* ----- the specification, typed from the studies' tables ----- */

    /** One date typed from a table: the year, the month and whether the table's World column names the month (else the study's model's month). */
    record Spot(String code, int year, int month, boolean monthKnown) { }

    /** research-design.md 4 and research-1900.md 8: the World column's month where it names one, the prototype's where it does not. */
    static final List<Spot> DATES = List.of(
            new Spot("E1", 1952, 11, true),   // Nov 1952, Linz
            new Spot("E5", 1956, 4, true),    // Apr 1956, Ideal-X
            new Spot("E12", 1968, 3, true),   // Mar 1968, Bluebird AZ
            new Spot("E16", 1980, 12, true),  // Dec 1980, Crotched Mountain
            new Spot("E18", 1989, 7, true),   // Jul 1989, Crawfordsville
            new Spot("M2", 1952, 9, true),    // Sep 1952, Copenhagen
            new Spot("M3", 1955, 4, true),    // Apr 1955
            new Spot("M6", 1971, 10, true),   // Oct 1971, London
            new Spot("M7", 1977, 9, true),    // Sep 1977, Zurich
            new Spot("B1", 1951, 11, true),   // Nov 1951, LEO
            new Spot("B3", 1967, 6, true),    // Jun 1967, Enfield
            new Spot("B5", 1974, 6, true),    // Jun 1974, Troy OH
            new Spot("L1", 1967, 3, true),    // Mar 1967 (CDIC)
            new Spot("PE1", 1900, 12, true),  // Dec 1900, Heroult
            new Spot("PE3", 1901, 1, true),   // 10 Jan 1901, Spindletop
            new Spot("PE6", 1903, 10, true),  // 2 Oct 1903, Fisk Street
            new Spot("PE9", 1908, 9, true),   // Sep 1908, Jersey City
            new Spot("PE11", 1913, 1, true),  // Jan 1913, Whiting
            new Spot("PE13", 1913, 10, true), // 7 Oct 1913, Highland Park
            new Spot("PE15", 1917, 12, true), // 1 Dec 1917, the first Joy loader
            new Spot("PE24", 1936, 6, true),  // Jun 1936, Paulsboro
            new Spot("PM2", 1922, 1, true),   // Jan 1922, Toronto General Hospital
            new Spot("PM6", 1946, 3, true),   // Mar 1946, first cure
            new Spot("PB2", 1916, 9, true),   // 11 Sep 1916, Memphis
            new Spot("PB3", 1930, 8, true),   // Aug 1930, Queens
            // the year alone: the prototype's month
            new Spot("E2", 1953, 6, false),   // 1953, Beckum
            new Spot("E4", 1955, 6, false),   // 1955, Silver Bay
            new Spot("E9", 1962, 6, false),   // early 1960s
            new Spot("E17", 1984, 4, false),  // 1984, APL/UP
            new Spot("M1", 1952, 6, false),   // 1952
            new Spot("M4", 1960, 3, false),   // 1960, Seattle
            new Spot("M9", 1987, 3, false),   // 1987, Lyon
            new Spot("M10", 1987, 9, false),  // 1987
            new Spot("B6", 1982, 6, false),   // early 1980s
            new Spot("B7", 1984, 12, false),  // 1984, NUMMI
            new Spot("L2", 1978, 10, false),  // 1978 (Chapter 11)
            new Spot("PE14", 1917, 6, false), // 1917, Fordson
            new Spot("PE18", 1918, 6, false), // about 1902-18
            new Spot("PE25", 1938, 6, false), // 1938-40
            new Spot("PM5", 1943, 6, false),  // 1943-44
            new Spot("PB1", 1905, 6, false)); // 1905, St. Louis

    /** The entries whose World column names a month: the table's count. */
    static final int MONTH_KNOWN = 30;

    /** The prefixes in the studies' order, and how many entries each has. */
    static final String[] PREFIXES = {"E", "M", "B", "L", "PE", "PM", "PB", "PL"};
    static final int[] PREFIX_COUNTS = {18, 10, 8, 5, 27, 6, 3, 12};

    /** Work typed from the tables (researcher-years at the date); PL1 and PL12 are the 8.4 text's, L5 the 8.6 rule's. */
    static final Object[][] WORK = {
            {"E1", 444}, {"E5", 545}, {"E12", 1013}, {"E18", 2998}, {"M3", 594}, {"M10", 1786}, {"B1", 421}, {"B4", 656}, {"B8", 4086},
            {"L4", 2567}, {"PE1", 30}, {"PE14", 73}, {"PE27", 380}, {"PM2", 191}, {"PM6", 436}, {"PB1", 20}, {"PL1", 33}, {"PL12", 171}, {"L5", 457}};

    /** The game month of a real calendar month, worked out here and not by PolicyDates. */
    static int monthOf(int year, int monthOfYear) {
        return 1 + 12 * (year - CityCalendar.EPOCH_YEAR) + monthOfYear - 1;
    }

    static String prefixOf(String code) {
        return code.replaceAll("\\d+$", "");
    }

    /* ----- fixtures for the reader ----- */

    private static String quoted(String... items) {
        List<String> out = new ArrayList<>();
        for (String s : items) out.add("\"" + s + "\"");
        return String.join(",", out);
    }

    /** One entry's JSON, with the fields the reader refuses on; the rest it reads as absent. */
    static String entryJson(String code, String field, String size, String kind, int year, int month, int work, String... needs) {
        return "{\"code\":\"" + code + "\",\"name\":\"name " + code + "\",\"field\":\"" + field + "\",\"kind\":[" + quoted(kind) + "],\"size\":\""
                + size + "\",\"year\":" + year + ",\"month\":" + month + ",\"monthKnown\":true,\"work\":" + work + ",\"needs\":[" + quoted(needs) + "]}";
    }

    static String file(String... entries) {
        return "{\"entries\":[" + String.join(",", entries) + "]}";
    }

    /** A good entry: an engineering building in 1960. */
    static String good(String code, String... needs) {
        return entryJson(code, "ENGINEERING", "building", "building", 1960, 6, 100, needs);
    }

    /** What a reading wrote and what it made. */
    record Read(ResearchCatalog catalogue, String log) { }

    /** Reads a text through the class's parse() with System.out captured, so that what it reports can be asserted. */
    static Read read(String json) {
        PrintStream real = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ResearchCatalog catalogue;
        System.setOut(new PrintStream(bytes, true, StandardCharsets.UTF_8));
        try {
            catalogue = ResearchCatalog.parse(json);
        } finally {
            System.setOut(real);
        }
        return new Read(catalogue, bytes.toString(StandardCharsets.UTF_8));
    }

    static String text(String resource) throws Exception {
        try (InputStream in = ResearchCheck.class.getResourceAsStream(resource)) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    static Entry of(String code) {
        return ResearchCatalog.get().entry(code);
    }

    static boolean has(Entry e, List<String> kinds) {
        return e != null && e.kinds().equals(kinds);
    }

    public static void main(String[] args) throws Exception {

        ResearchCatalog cat = ResearchCatalog.get();
        String real = text("/" + ResearchCatalog.FILE_NAME);

        /* ==================== 1. the file reads ==================== */
        System.out.println("--- the packaged file gives 89 entries and reports nothing ---");
        Read fresh = read(real);
        int inFile = JsonParser.parseString(real).getAsJsonObject().getAsJsonArray("entries").size();
        check("the file has 89 entries and the catalogue read " + cat.entries().size() + " of them", inFile == 89 && cat.entries().size() == 89);
        check("a fresh reading of the file's text reads all 89 and writes nothing to the log", fresh.catalogue().entries().size() == 89 && fresh.log().isEmpty());
        check("nothing was skipped: the fresh reading has the same codes, in the same order, as the file",
                codes(fresh.catalogue()).equals(codes(cat)));
        Set<String> unique = new HashSet<>(codes(cat));
        check("the 89 codes are unique", unique.size() == 89);
        check("the order is the studies': E1-E18, M1-M10, B1-B8, L1-L4, PE1-PE27, PM1-PM6, PB1-PB3, PL1-PL12, then L5", codes(cat).equals(orderOf()));
        Map<String, Integer> byPrefix = new HashMap<>();
        for (Entry e : cat.entries()) byPrefix.merge(prefixOf(e.code()), 1, Integer::sum);
        for (int p = 0; p < PREFIXES.length; p++) {
            check(PREFIXES[p] + " has " + PREFIX_COUNTS[p] + " entries (" + byPrefix.get(PREFIXES[p]) + ")", byPrefix.getOrDefault(PREFIXES[p], 0) == PREFIX_COUNTS[p]);
        }
        Map<Field, Integer> byField = new HashMap<>();
        for (Entry e : cat.entries()) byField.merge(e.field(), 1, Integer::sum);
        check("ENGINEERING has 45, MEDICINE 16, BUSINESS 11 and LAW 17 (" + byField + ")", byField.get(Field.ENGINEERING) == 45
                && byField.get(Field.MEDICINE) == 16 && byField.get(Field.BUSINESS) == 11 && byField.get(Field.LAW) == 17);
        boolean fieldOfPrefix = true;
        for (Entry e : cat.entries()) {
            String p = prefixOf(e.code());
            Field expected = switch (p) {
                case "E", "PE" -> Field.ENGINEERING;
                case "M", "PM" -> Field.MEDICINE;
                case "B", "PB" -> Field.BUSINESS;
                default -> Field.LAW;
            };
            fieldOfPrefix &= e.field() == expected;
        }
        check("the field follows the code: E and PE engineering, M and PM medicine, B and PB business, L and PL law", fieldOfPrefix);
        boolean words = true;
        for (Entry e : cat.entries()) {
            words &= !e.name().isBlank() && !e.world().isBlank() && !e.touches().isBlank() && !e.effect().isBlank() && !e.study().isBlank();
        }
        check("every entry has a name, a world, a touches, an effect and the study it comes from", words);
        check("the fields are read by the school that researches them", Field.ENGINEERING.school().equals("the Institute of technology")
                && Field.MEDICINE.school().equals("the Medical school") && Field.BUSINESS.school().equals("the Business school")
                && Field.LAW.school().equals("the Law school"));
        check("names are the tables': Cash machine (not the prototype's \"(ATM)\"), the tractor, the long pulverised-coal name, the by-law",
                of("B3").name().equals("Cash machine") && of("PE14").name().equals("Gasoline tractor")
                        && of("PE16").name().equals("Pulverised coal, high-pressure steam") && of("PM1").name().equals("Pasteurised milk (a by-law)"));
        check("a table's kind is read: E1 equipment, E4 building, E3 recipe, M1 and M3 and PM2 a drug",
                has(of("E1"), List.of("equipment")) && has(of("E4"), List.of("building")) && has(of("E3"), List.of("recipe"))
                        && has(of("M1"), List.of("drug")) && has(of("M3"), List.of("drug")) && has(of("PM2"), List.of("drug")));
        check("two kinds where the table says two: PE4 building + equipment, PE13 building + recipe, M9 equipment + recipe",
                has(of("PE4"), List.of("building", "equipment")) && has(of("PE13"), List.of("building", "recipe"))
                        && has(of("M9"), List.of("equipment", "recipe")));
        check("is() asks the kinds: PE4 is a building and a piece of equipment and no recipe",
                of("PE4").is("building") && of("PE4").is("equipment") && !of("PE4").is("recipe") && !of("PE4").is("drug"));
        check("PM1 (a by-law) is a recipe; the law rows are recipes with their notes: L1 a Policy dial, L2 a market rule, L3 a sentencing option, every PL and L5 a Policy lever; L4 is equipment",
                has(of("PM1"), List.of("recipe")) && of("L1").kindNote().equals("a Policy dial") && of("L2").kindNote().equals("a market rule")
                        && of("L3").kindNote().equals("a sentencing option") && has(of("L4"), List.of("equipment")) && of("L4").kindNote().isEmpty()
                        && allLaw(cat, "PL", "a Policy lever") && of("L5").kindNote().equals("a Policy lever"));

        /* ==================== 2. needs ==================== */
        System.out.println("\n--- every need is a code in the catalogue, dated no later than its entry ---");
        int links = 0;
        int withNeeds = 0;
        boolean allCodes = true;
        boolean noSelf = true;
        boolean notLater = true;
        for (Entry e : cat.entries()) {
            if (!e.needs().isEmpty()) withNeeds++;
            for (String need : e.needs()) {
                links++;
                allCodes &= cat.entry(need) != null && need.matches("[A-Z]+\\d+");
                noSelf &= !need.equals(e.code());
                notLater &= cat.worldMonth(need) <= e.worldMonth();
            }
        }
        check("every need is a code in the catalogue", allCodes);
        check("no entry needs itself", noSelf);
        check("history dates every need no later than the entry that needs it", notLater);
        check("the tables give 26 needs to 23 entries (" + links + " and " + withNeeds + ")", links == 26 && withNeeds == 23);
        check("M1 needs PM6, streptomycin (research-1900 8.6)", of("M1").needs().equals(List.of("PM6")));
        check("E18 needs E13 and E9, and E13 needs E9", new HashSet<>(of("E18").needs()).equals(Set.of("E13", "E9")) && of("E18").needs().size() == 2
                && of("E13").needs().equals(List.of("E9")));
        check("PE17 needs PE14 (the combine and the tractor); PE16 needs PE6; PB3 needs PB2; B6 needs B1 and B2; B8 needs B1 and B5",
                of("PE17").needs().equals(List.of("PE14")) && of("PE16").needs().equals(List.of("PE6")) && of("PB3").needs().equals(List.of("PB2"))
                        && new HashSet<>(of("B6").needs()).equals(Set.of("B1", "B2")) && new HashSet<>(of("B8").needs()).equals(Set.of("B1", "B5")));
        check("what is not a discovery is in words: E1 needs hot iron from a blast furnace, E5 a coast, E4 a magnetite field, E10 fertiliser",
                of("E1").requires().contains("blast furnace") && of("E5").requires().equals("a coast") && of("E4").requires().contains("magnetite")
                        && of("E10").requires().contains("fertiliser") && of("E3").requires().isEmpty());

        /* ==================== 3. history's dates ==================== */
        System.out.println("\n--- every discovery is on history's date ---");
        boolean formula = true;
        boolean afterFounding = true;
        boolean inRange = true;
        for (Entry e : cat.entries()) {
            formula &= e.worldMonth() == PolicyDates.monthOf(e.year(), e.monthOfYear()) && cat.worldMonth(e.code()) == e.worldMonth();
            afterFounding &= e.worldMonth() > 1;
            inRange &= e.year() >= 1900 && e.year() <= 1995 && e.monthOfYear() >= 1 && e.monthOfYear() <= 12;
        }
        check("every entry's world month is PolicyDates.monthOf(year, month), the game's one formula", formula);
        check("no discovery is known at the founding (every world month is after month 1)", afterFounding);
        check("every year is 1900 to 1995 and every month 1 to 12", inRange);
        for (Spot s : DATES) {
            Entry e = of(s.code());
            check(s.code() + " is " + s.year() + ", month " + s.month() + (s.monthKnown() ? " (the table names the month)" : " (the table names the year; the model's month)"),
                    e != null && e.year() == s.year() && e.monthOfYear() == s.month() && e.monthKnown() == s.monthKnown()
                            && e.worldMonth() == monthOf(s.year(), s.month()));
        }
        int known = 0;
        for (Entry e : cat.entries()) if (e.monthKnown()) known++;
        check("the tables name a month for " + MONTH_KNOWN + " entries (" + known + ")", known == MONTH_KNOWN);
        check("E1 is November 1952: " + CityCalendar.format(of("E1").worldMonth()), CityCalendar.yearOf(of("E1").worldMonth()) == 1952
                && CityCalendar.monthOfYear(of("E1").worldMonth()) == 11);
        int levers = 0;
        boolean leversRead = true;
        for (Lever lever : Lever.values()) {
            if (lever.entry().isBlank()) continue;
            levers++;
            Entry e = of(lever.entry());
            boolean ok = e != null && e.field() == Field.LAW && e.worldMonth() == PolicyDates.openingMonth(lever)
                    && e.year() == lever.year() && e.monthOfYear() == lever.monthOfYear() && e.monthKnown() == lever.monthKnown();
            check(lever + " (" + lever.entry() + ") is a LAW entry on the lever's own date: " + lever.year() + ", month " + lever.monthOfYear()
                    + (lever.monthKnown() ? "" : " (January, the year alone)"), ok);
            leversRead &= ok;
        }
        check("PolicyDates has levers with entry keys, and every one of them was held to the catalogue (" + levers + ")", levers >= 11 && leversRead);
        check("PL1 (1915) and PL11 (1942) have no lever: each is in its year's January, the year alone",
                of("PL1").year() == 1915 && of("PL1").monthOfYear() == 1 && !of("PL1").monthKnown() && of("PL1").worldMonth() == monthOf(1915, 1)
                        && of("PL11").year() == 1942 && of("PL11").monthOfYear() == 1 && !of("PL11").monthKnown() && of("PL11").worldMonth() == monthOf(1942, 1));
        check("every law entry is a recipe of the Law school: PL1-PL12 and L5 are Policy levers", allLaw(cat, "PL", "a Policy lever") && of("L5").field() == Field.LAW);

        /* ==================== 4. the work ==================== */
        System.out.println("\n--- the work is the rule's, and the tables' ---");
        boolean ruled = true;
        for (Entry e : cat.entries()) {
            long rule = Math.round(ResearchCatalog.workByRule(e.size(), e.field(), e.year()));
            boolean ok = Math.abs(e.work() - rule) <= 1;
            if (!ok) check(e.code() + "'s work " + e.work() + " is the rule's " + rule + " to within 1", false);
            ruled &= ok;
        }
        check("every entry's work is within 1 of the rule, rounded (workByRule of its size, field and year)", ruled);
        for (Object[] row : WORK) {
            String code = (String) row[0];
            int work = (Integer) row[1];
            check(code + " takes " + work + " researcher-years (the table)", of(code) != null && of(code).work() == work);
        }
        check("B4, B6 and PB1 are buildings with a recipe's work", of("B4").size() == Size.RECIPE && of("B6").size() == Size.RECIPE
                && of("PB1").size() == Size.RECIPE && of("B4").is("building") && of("B6").is("building") && of("PB1").is("building"));
        check("M1, M3, M10 and PM2 to PM6 are a drug's work", of("M1").size() == Size.DRUG && of("M3").size() == Size.DRUG
                && of("M10").size() == Size.DRUG && drugs(cat));
        check("M9, PE4 and L4 are equipment's work; PE13 a building's; PM1 and M5 a recipe's",
                of("M9").size() == Size.EQUIPMENT && of("PE4").size() == Size.EQUIPMENT && of("L4").size() == Size.EQUIPMENT
                        && of("PE13").size() == Size.BUILDING && of("PM1").size() == Size.RECIPE && of("M5").size() == Size.RECIPE);
        check("the rule's 1950 figures are the study's: a building 400, equipment 400, a recipe 200, a drug 500",
                ResearchCatalog.workAtRuleYear(Size.BUILDING) == 400 && ResearchCatalog.workAtRuleYear(Size.EQUIPMENT) == 400
                        && ResearchCatalog.workAtRuleYear(Size.RECIPE) == 200 && ResearchCatalog.workAtRuleYear(Size.DRUG) == 500);
        boolean atRuleYear = true;
        boolean dearer = true;
        for (Size size : Size.values()) {
            for (Field field : Field.values()) {
                atRuleYear &= Math.abs(ResearchCatalog.workByRule(size, field, ResearchCatalog.WORK_RULE_YEAR) - ResearchCatalog.workAtRuleYear(size)) < 1e-9;
                dearer &= ResearchCatalog.workByRule(size, field, 1951) > ResearchCatalog.workByRule(size, field, 1950)
                        && ResearchCatalog.workByRule(size, field, 1900) < ResearchCatalog.workByRule(size, field, 1950);
            }
        }
        check("in the rule's year the work is the kind's own figure, and a discovery costs more every year", ResearchCatalog.WORK_RULE_YEAR == 1950
                && atRuleYear && dearer);
        double a = ResearchCatalog.workByRule(Size.BUILDING, Field.ENGINEERING, 1960) / ResearchCatalog.workByRule(Size.BUILDING, Field.ENGINEERING, 1950);
        double m = ResearchCatalog.workByRule(Size.DRUG, Field.MEDICINE, 1960) / ResearchCatalog.workByRule(Size.DRUG, Field.MEDICINE, 1950);
        check("work grows 5.3% a year, and 3.5% in medicine (ten years: x" + String.format("%.3f", a) + " and x" + String.format("%.3f", m) + ")",
                Math.abs(a - Math.pow(1 + ResearchCatalog.WORK_GROWTH, 10)) < 1e-9 && Math.abs(m - Math.pow(1 + ResearchCatalog.MEDICINE_WORK_GROWTH, 10)) < 1e-9
                        && ResearchCatalog.WORK_GROWTH == 0.053 && ResearchCatalog.MEDICINE_WORK_GROWTH == 0.035
                        && Field.LAW.workGrowth() == ResearchCatalog.WORK_GROWTH && Field.MEDICINE.workGrowth() == ResearchCatalog.MEDICINE_WORK_GROWTH);

        /* ==================== 5. known from ==================== */
        System.out.println("\n--- a \"since\" is a year or a code, and the catalogue says the month ---");
        check("a year is known from its January: \"1900\" is month 1, \"1907\" is PolicyDates.monthOf(1907, 1)",
                ResearchCatalog.knownFrom("1900") == 1 && ResearchCatalog.knownFrom("1907") == PolicyDates.monthOf(1907, 1)
                        && ResearchCatalog.knownFrom(" 1907 ") == monthOf(1907, 1));
        check("a code is known from its entry's month: E1 is November 1952", ResearchCatalog.knownFrom("E1") == of("E1").worldMonth()
                && ResearchCatalog.knownFrom("E1") == PolicyDates.monthOf(1952, 11));
        check("PE14 (the tractor) is known from its entry's month, June 1917", ResearchCatalog.knownFrom("PE14") == of("PE14").worldMonth()
                && ResearchCatalog.knownFrom("PE14") == monthOf(1917, 6));
        check("a code the catalogue lacks, a blank and null are never known", ResearchCatalog.knownFrom("ZZ9") == Integer.MAX_VALUE
                && ResearchCatalog.knownFrom("") == Integer.MAX_VALUE && ResearchCatalog.knownFrom("  ") == Integer.MAX_VALUE
                && ResearchCatalog.knownFrom(null) == Integer.MAX_VALUE);
        check("worldMonth(code) is never for a code the catalogue lacks, and entry() is null for it, for a blank and for null",
                cat.worldMonth("ZZ9") == Integer.MAX_VALUE && cat.entry("ZZ9") == null && cat.entry("") == null && cat.entry(null) == null
                        && cat.worldMonth(null) == Integer.MAX_VALUE);
        Matcher since = Pattern.compile("\"since\"\\s*:\\s*\"([A-Za-z][A-Za-z0-9]*)\"").matcher(text("/buildings.json"));
        int codesUsed = 0;
        boolean resolved = true;
        while (since.find()) {
            codesUsed++;
            boolean ok = cat.entry(since.group(1)) != null && ResearchCatalog.knownFrom(since.group(1)) < Integer.MAX_VALUE;
            if (!ok) check("buildings.json's since \"" + since.group(1) + "\" is a code in the catalogue", false);
            resolved &= ok;
        }
        check("every code buildings.json carries in a \"since\" is a discovery in the catalogue (" + codesUsed + " of them)", codesUsed > 0 && resolved);
        boolean unmodifiable = false;
        try {
            cat.entries().add(cat.entries().get(0));
        } catch (UnsupportedOperationException expected) {
            unmodifiable = true;
        }
        check("entries() is unmodifiable", unmodifiable);
        ResearchCatalog[] seen = new ResearchCatalog[4];
        Thread[] threads = new Thread[seen.length];
        for (int i = 0; i < threads.length; i++) {
            int slot = i;
            threads[i] = new Thread(() -> seen[slot] = ResearchCatalog.get());
            threads[i].start();
        }
        for (Thread t : threads) t.join();
        boolean one = true;
        for (ResearchCatalog c : seen) one &= c == cat;
        check("get() is one catalogue, the same for every thread", one);

        /* ==================== 6. the reader's refusals, and the fees ==================== */
        System.out.println("\n--- the reader reports and skips what is not a discovery ---");
        Read clean = read(file(good("X1"), good("X2", "X1")));
        check("a clean file is read whole and reports nothing", clean.catalogue().entries().size() == 2 && clean.log().isEmpty()
                && clean.catalogue().entry("X2").needs().equals(List.of("X1")));
        Entry x1 = clean.catalogue().entry("X1");
        check("a fixture entry is read as written: engineering, a building's size, 1960 month 6, worldMonth = PolicyDates'",
                x1.field() == Field.ENGINEERING && x1.size() == Size.BUILDING && x1.year() == 1960 && x1.monthOfYear() == 6 && x1.work() == 100
                        && x1.worldMonth() == PolicyDates.monthOf(1960, 6));

        Read dup = read(file(entryJson("D1", "ENGINEERING", "building", "building", 1950, 3, 10), entryJson("D1", "MEDICINE", "drug", "drug", 1990, 9, 20), good("D2")));
        check("a duplicate code keeps the first entry and reports it", dup.catalogue().entries().size() == 2
                && dup.catalogue().entry("D1").year() == 1950 && dup.catalogue().entry("D1").field() == Field.ENGINEERING
                && dup.catalogue().entry("D2") != null && dup.log().contains("D1") && dup.log().contains("second entry"));

        Read badField = read(file(entryJson("F1", "ASTROLOGY", "building", "building", 1960, 6, 100), good("F2")));
        check("an unknown field is skipped and reported, and the next entry is still read", badField.catalogue().entry("F1") == null
                && badField.catalogue().entry("F2") != null && badField.log().contains("F1") && badField.log().contains("ASTROLOGY"));

        Read badSize = read(file(entryJson("S1", "ENGINEERING", "ship", "building", 1960, 6, 100), good("S2")));
        check("an unknown size is skipped and reported", badSize.catalogue().entry("S1") == null && badSize.catalogue().entry("S2") != null
                && badSize.log().contains("S1") && badSize.log().contains("ship"));

        Read badKind = read(file(entryJson("K1", "ENGINEERING", "building", "spell", 1960, 6, 100), good("K2")));
        check("an unknown kind is skipped and reported", badKind.catalogue().entry("K1") == null && badKind.catalogue().entry("K2") != null
                && badKind.log().contains("K1") && badKind.log().contains("spell"));

        Read noKind = read("{\"entries\":[{\"code\":\"K3\",\"field\":\"LAW\",\"size\":\"recipe\",\"year\":1960,\"month\":6,\"kind\":[]}]}");
        check("an entry with no kind is skipped and reported", noKind.catalogue().entries().isEmpty() && noKind.log().contains("K3"));

        Read month13 = read(file(entryJson("T1", "ENGINEERING", "building", "building", 1960, 13, 100), entryJson("T2", "ENGINEERING", "building", "building", 1960, 0, 100), good("T3")));
        check("a month 13 and a month 0 are skipped and reported", month13.catalogue().entry("T1") == null && month13.catalogue().entry("T2") == null
                && month13.catalogue().entry("T3") != null && month13.log().contains("T1") && month13.log().contains("T2"));

        Read early = read(file(entryJson("Y1", "ENGINEERING", "building", "building", CityCalendar.EPOCH_YEAR - 1, 6, 100), good("Y2")));
        check("a year before the epoch (" + CityCalendar.EPOCH_YEAR + ") is skipped and reported", early.catalogue().entry("Y1") == null
                && early.catalogue().entry("Y2") != null && early.log().contains("Y1"));

        Read negative = read(file(entryJson("W1", "ENGINEERING", "building", "building", 1960, 6, -1), entryJson("W2", "ENGINEERING", "building", "building", 1960, 6, 0)));
        check("a negative work is skipped and reported; a work of 0 is read", negative.catalogue().entry("W1") == null
                && negative.catalogue().entry("W2") != null && negative.log().contains("W1"));

        Read blank = read(file(entryJson("", "ENGINEERING", "building", "building", 1960, 6, 100), entryJson("  ", "ENGINEERING", "building", "building", 1960, 6, 100), good("B9")));
        check("a blank code is skipped and reported", blank.catalogue().entries().size() == 1 && blank.catalogue().entry("B9") != null
                && blank.log().contains("no code"));

        Read missing = read(file(good("N1", "NOPE"), good("N2")));
        check("a need that is not in the catalogue is reported, and the entry stays", missing.catalogue().entry("N1") != null
                && missing.catalogue().entry("N1").needs().equals(List.of("NOPE")) && missing.log().contains("N1") && missing.log().contains("NOPE")
                && missing.log().contains("not a code") && missing.catalogue().entries().size() == 2);

        Read later = read(file(entryJson("A1", "ENGINEERING", "building", "building", 1950, 6, 100, "A2"), entryJson("A2", "ENGINEERING", "building", "building", 1960, 6, 100)));
        check("a need dated after its entry is reported, and the entry stays", later.catalogue().entries().size() == 2 && later.log().contains("A1")
                && later.log().contains("A2") && later.log().contains("later"));

        Read sameMonth = read(file(entryJson("C1", "ENGINEERING", "building", "building", 1960, 6, 100, "C2"), entryJson("C2", "ENGINEERING", "building", "building", 1960, 6, 100)));
        check("a need in the entry's own month is no fault and reports nothing", sameMonth.log().isEmpty());

        Read self = read(file(good("Q1", "Q1")));
        check("an entry that needs itself is reported and stays", self.catalogue().entry("Q1") != null && self.log().contains("needs itself"));

        Read notJson = read("this is not json");
        check("a text that is not JSON gives an empty catalogue and a report", notJson.catalogue().entries().isEmpty() && !notJson.log().isEmpty());
        Read noArray = read("{\"buildings\":[]}");
        check("a text with no entries array gives an empty catalogue and a report", noArray.catalogue().entries().isEmpty() && noArray.log().contains("entries"));
        Read junk = read("{\"entries\":[3, \"x\", " + good("J1") + "]}");
        check("an entry that is not an object is skipped and reported, the rest read", junk.catalogue().entries().size() == 1
                && junk.catalogue().entry("J1") != null && !junk.log().isEmpty());
        check("an empty catalogue answers like the real one: no entry, never known", notJson.catalogue().entry("E1") == null
                && notJson.catalogue().worldMonth("E1") == Integer.MAX_VALUE);

        System.out.println("\n--- the fees the tables carry ---");
        boolean lawFree = true;
        for (Entry e : cat.entries()) if (e.field() == Field.LAW && !e.code().equals("L4")) lawFree &= !e.fee();
        check("no law entry carries a licence fee (law is not patented, research-design 2.4) but L4's DNA lab, which is equipment, as its table says",
                lawFree && of("L4").fee() && of("L4").is("equipment"));
        check("E1 carries one and E10 does not; M3 does not (not patented, unverified); B1 does and B5 does not",
                of("E1").fee() && !of("E10").fee() && !of("M3").fee() && of("M3").feeNote().equals("not patented; unverified")
                        && of("B1").fee() && !of("B5").fee() && of("B5").feeNote().equals("open standard, est."));
        check("PM4 carries one \"partly\"; M9's equipment does; E5 does not (est.); PE3 does (est.); PM2 does, University of Toronto, for $1; PE14 does not",
                of("PM4").fee() && of("PM4").feeNote().equals("partly") && of("M9").fee() && of("M9").feeNote().equals("equipment yes")
                        && !of("E5").fee() && of("E5").feeNote().equals("est.") && of("PE3").fee() && of("PE3").feeNote().equals("est.")
                        && of("PM2").fee() && of("PM2").feeNote().equals("University of Toronto, for $1") && !of("PE14").fee());
        check("the business methods are free: B7 lean production, B8 the online shop, PB2 the self-service grocery, PB3 the supermarket",
                !of("B7").fee() && !of("B8").fee() && !of("PB2").fee() && !of("PB3").fee() && !of("PB2").feeNote().isEmpty());

        sectionSeven();
        sectionEightAndNine();

        System.out.println(fails == 0 ? "ResearchCheck: all " + passes + " ok" : "ResearchCheck: " + fails + " FAILED of " + (passes + fails));
        System.exit(fails);
    }

    /* ==================== 7. buildings.json's codes (R2, slice B) ==================== */

    /** research-1900.md 1.2 and the R2 brief: the templates dated by a code, id to the entry that opens each; typed here as the specification. */
    static final Map<Integer, String> CODED = new TreeMap<>(Map.ofEntries(
            Map.entry(11, "PE1"), Map.entry(119, "PE4"), Map.entry(91, "PB1"), Map.entry(122, "PE8"),
            Map.entry(79, "PE11"), Map.entry(80, "PE11"), Map.entry(66, "PE13"), Map.entry(85, "PE23"), Map.entry(86, "PE23"),
            Map.entry(83, "PE25"), Map.entry(84, "PE25"), Map.entry(77, "PE27"), Map.entry(78, "PE27"),
            Map.entry(102, "E4"), Map.entry(109, "E4"), Map.entry(110, "E4"), Map.entry(96, "E5"), Map.entry(73, "E6"),
            Map.entry(81, "E7"), Map.entry(82, "E7"), Map.entry(46, "B2"), Map.entry(123, "E12"), Map.entry(12, "E13"),
            Map.entry(107, "E13"), Map.entry(130, "E14"), Map.entry(18, "B4"), Map.entry(41, "E16"), Map.entry(23, "M8"),
            Map.entry(47, "B6")));

    static BuildingsTemplate byId(List<BuildingsTemplate> templates, int id) {
        for (BuildingsTemplate t : templates) if (t.getId() == id) return t;
        return null;
    }

    static void sectionSeven() {
        System.out.println("\n--- every code buildings.json carries is the right kind of discovery, and the dated rows are the studies' ---");
        List<BuildingsTemplate> shipped = new BuildingCatalog().load();
        check("fixture: buildings.json loads", shipped != null && !shipped.isEmpty());
        if (shipped == null) return;

        Map<Integer, String> codeDated = new TreeMap<>();
        String templateFault = null, pieceFault = null, recipeFault = null;
        int pieces = 0, recipes = 0;
        for (BuildingsTemplate t : shipped) {
            String text = t.getSinceText();
            if (!text.isEmpty() && Equipment.yearOf(text) == null) {
                codeDated.put(t.getId(), text);
                Entry e = of(text);
                if (templateFault == null && (e == null || !e.is("building"))) templateFault = t.getName() + " " + text;
                if (templateFault == null && t.getSince() != e.year()) templateFault = t.getName() + " " + text + " year " + t.getSince();
            }
            for (Equipment piece : t.getEquipment()) {
                if (piece.isYear()) continue;
                pieces++;
                Entry e = of(piece.getSince());
                if (pieceFault == null && (e == null || !e.is("equipment"))) pieceFault = t.getName() + "/" + piece.getId() + " " + piece.getSince();
            }
            for (Recipe r : t.getRecipes()) {
                if (r.isYear()) continue;
                recipes++;
                Entry e = of(r.getSince());
                if (recipeFault == null && (e == null || e.kinds().equals(List.of("building")))) recipeFault = t.getName() + "/" + r.getId() + " " + r.getSince();
            }
        }
        check("every template whose since is not a year has a code that is a discovery in the catalogue unlocking a building, and the template's year is the entry's ("
                + codeDated.size() + " of them)", templateFault == null && !codeDated.isEmpty(), templateFault);
        check("...every piece of equipment whose since is a code has one that unlocks equipment (" + pieces + " of them)", pieceFault == null && pieces > 0, pieceFault);
        check("...and every recipe whose since is a code has one that is not a building alone (" + recipes + " of them)", recipeFault == null && recipes > 0, recipeFault);
        check("the templates dated by a code are exactly research-1900.md 1.2's list, 29 of them, each by the entry that opens it",
                codeDated.equals(CODED) && CODED.size() == 29, codeDated + " vs " + CODED);

        BuildingsTemplate oxygen = byId(shipped, 106), panel = byId(shipped, 135), pellets = byId(shipped, 138);
        check("the Oxygen Steel Shop is dated 1900 (its Bessemer and open-hearth recipes are its 1900 ways; E1 dates its recipe, not the building)",
                oxygen != null && oxygen.getSinceText().equals("1900") && oxygen.getSince() == 1900);
        check("the Panel Mill stays 1980 and the Wood Pellet Mill 1970, years: research-1900.md's words, no entry yet (Q13)",
                panel != null && panel.getSinceText().equals("1980") && panel.getSince() == 1980
                        && pellets != null && pellets.getSinceText().equals("1970") && pellets.getSince() == 1970);
        List<String> odd = new ArrayList<>();
        for (BuildingsTemplate t : shipped) {
            if (CODED.containsKey(t.getId()) || t == panel || t == pellets) continue;
            if (!t.getSinceText().isEmpty() && !t.getSinceText().equals("1900")) odd.add(t.getName() + " " + t.getSinceText());
        }
        check("every other template is undated or 1900", odd.isEmpty(), odd.toString());

        List<String> unopened = new ArrayList<>();
        int buildingEntries = 0;
        for (Entry e : ResearchCatalog.get().entries()) {
            if (!e.is("building")) continue;
            buildingEntries++;
            if (!codeDated.containsValue(e.code())) unopened.add(e.code());
        }
        check("every discovery that unlocks a building (" + buildingEntries + ") opens at least one template but PE12 (no fertiliser works: no FERTILISER good yet) and B8 (the Fulfilment Centre, not built yet)",
                buildingEntries == 23 && new HashSet<>(unopened).equals(Set.of("PE12", "B8")) && unopened.size() == 2, unopened.toString());

        // A file read from text: a year, a code, nothing, and a code the catalogue lacks.
        String json = "{\"buildings\": [{\"id\": 1, \"name\": \"Coded Works\", \"category\": \"INDUSTRIAL\", \"since\": \"PE4\"},"
                + "{\"id\": 2, \"name\": \"Year Works\", \"category\": \"INDUSTRIAL\", \"since\": 1902},"
                + "{\"id\": 3, \"name\": \"Undated Works\", \"category\": \"INDUSTRIAL\"},"
                + "{\"id\": 4, \"name\": \"Ghost Works\", \"category\": \"INDUSTRIAL\", \"since\": \"ZZ9\"}]}";
        PrintStream real = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        List<BuildingsTemplate> read;
        System.setOut(new PrintStream(bytes, true, StandardCharsets.UTF_8));
        try {
            read = new BuildingCatalog().parse(new StringReader(json));
        } finally {
            System.setOut(real);
        }
        String said = bytes.toString(StandardCharsets.UTF_8);
        check("fixture: a file of four buildings, a code, a year, none and a ghost code, reads all four", read != null && read.size() == 4);
        if (read == null || read.size() != 4) return;
        BuildingsTemplate coded = read.get(0), year = read.get(1), none = read.get(2), ghost = read.get(3);
        int pe4 = of("PE4").worldMonth();
        check("a code is kept as written, its year is the entry's, and it is known from the entry's month (PE4, June 1902) and not the month before",
                coded.getSinceText().equals("PE4") && coded.getSince() == 1902 && coded.knownAt(pe4) && !coded.knownAt(pe4 - 1) && pe4 == PolicyDates.monthOf(1902, 6));
        check("a year is its digits, and is known from its January (the file's \"since\": 1902 and the code's year are the same year, not the same month)",
                year.getSinceText().equals("1902") && year.getSince() == 1902 && year.knownAt(PolicyDates.monthOf(1902, 1)) && !year.knownAt(PolicyDates.monthOf(1902, 1) - 1));
        check("no since is undated: empty text, year 0, known from the first month", none.getSinceText().isEmpty() && none.getSince() == 0 && none.knownAt(1));
        check("a code the catalogue lacks is reported by name, kept as written, year 0, and never known",
                ghost.getSinceText().equals("ZZ9") && ghost.getSince() == 0 && !ghost.knownAt(Integer.MAX_VALUE - 1) && said.contains("ZZ9") && said.contains(ResearchCatalog.FILE_NAME)
                        && !said.contains("PE4") && !said.contains("Coded Works") && !said.contains("Year Works"), said);
        BuildingsTemplate by = new BuildingsTemplate("By hand", BuildingType.INDUSTRIAL);
        check("setSince(int) is setSince(String) of the digits, and 0 or less is undated; setSince(String) takes a code, trims, and blank or null is undated",
                by.setSince(1902).getSinceText().equals("1902") && by.getSince() == 1902 && by.setSince(0).getSinceText().isEmpty() && by.getSince() == 0
                        && by.setSince(-5).getSinceText().isEmpty() && by.setSince(" E5 ").getSinceText().equals("E5") && by.getSince() == 1956
                        && by.setSince("  ").getSinceText().isEmpty() && by.setSince((String) null).getSinceText().isEmpty() && by.getSince() == 0);
    }

    /* ==================== 8. known from history's month, 9. the verdict and the choice on it (R2, slice B) ==================== */

    static void sectionEightAndNine() {
        System.out.println("\n--- a piece, a recipe and a building are known from history's month, and asking by the year agrees ---");
        List<BuildingsTemplate> shipped = new BuildingCatalog().load();
        if (shipped == null) return;
        BuildingsTemplate grain = byId(shipped, 53), shop = byId(shipped, 106), terminal = byId(shipped, 96), house = byId(shipped, 0),
                pulp = byId(shipped, 136), furnace = byId(shipped, 11), yard = byId(shipped, 101), bakery = byId(shipped, 7);
        check("fixture: the Grain Farm, the Oxygen Steel Shop, the Container Terminal, the House, the Pulp Mill, the Electric Arc Furnace, the Scrap Yard and the Bakery are in the file",
                grain != null && shop != null && terminal != null && house != null && pulp != null && furnace != null && yard != null && bakery != null);
        if (grain == null || shop == null || terminal == null || house == null || pulp == null || furnace == null || yard == null || bakery == null) return;

        Equipment tractor = grain.getEquipment("tractor"), drive = bakery.getEquipment("electric-drive");
        Recipe tractorRecipe = grain.getRecipe("tractor"), e1Recipe = shop.getRecipe("basic-oxygen"), kraft = pulp.getRecipe("kraft"), horses = grain.getRecipe("horses");
        check("fixture: the tractor piece and recipe, the oxygen converter's recipe, kraft, the horses' recipe and the drive are read",
                tractor != null && tractorRecipe != null && e1Recipe != null && kraft != null && horses != null && drive != null);
        if (tractor == null || tractorRecipe == null || e1Recipe == null || kraft == null || horses == null || drive == null) return;

        int pe14 = of("PE14").worldMonth(), e1 = of("E1").worldMonth(), e5 = of("E5").worldMonth(), pe1 = of("PE1").worldMonth();
        check("the Grain Farm's tractor piece is known from PE14's month, June 1917, and not the month before; its tractor recipe the same",
                pe14 == PolicyDates.monthOf(1917, 6) && tractor.knownFrom() == pe14 && tractor.knownAt(pe14) && !tractor.knownAt(pe14 - 1)
                        && tractorRecipe.knownFrom() == pe14 && tractorRecipe.knownAt(pe14) && !tractorRecipe.knownAt(pe14 - 1));
        check("the Oxygen Steel Shop's recipe dated \"E1\" is known from E1's month, November 1952, and not the month before",
                e1 == PolicyDates.monthOf(1952, 11) && e1Recipe.knownAt(e1) && !e1Recipe.knownAt(e1 - 1));
        check("the Container Terminal (96) is known from E5's month, April 1956, and not before; its year is 1956 and its text \"E5\"",
                e5 == PolicyDates.monthOf(1956, 4) && terminal.knownAt(e5) && !terminal.knownAt(e5 - 1) && terminal.getSince() == 1956 && terminal.getSinceText().equals("E5"));
        check("the Electric Arc Furnace (11) is known from PE1's month, December 1900, and not the month before: a building a first-year discovery opens",
                pe1 == PolicyDates.monthOf(1900, 12) && furnace.knownAt(pe1) && !furnace.knownAt(pe1 - 1) && furnace.getSince() == 1900 && furnace.getSinceText().equals("PE1"));
        check("the Pulp Mill's kraft recipe (\"1907\") is known from January 1907 and not the month before",
                kraft.knownAt(PolicyDates.monthOf(1907, 1)) && !kraft.knownAt(PolicyDates.monthOf(1907, 1) - 1) && kraft.knownFrom() == PolicyDates.monthOf(1907, 1));
        List<String> undatedFault = new ArrayList<>();
        int undated = 0;
        for (BuildingsTemplate t : shipped) {
            if (!t.getSinceText().isEmpty()) continue;
            undated++;
            if (!t.knownAt(1) || t.getSince() != 0) undatedFault.add(t.getName());
        }
        check("an undated template (a House, and each of the " + undated + " the file leaves undated) is known from the first month", undated > 0 && undatedFault.isEmpty() && house.knownAt(1), undatedFault.toString());
        check("a template dated 1900 (the Scrap Yard) is known from the first month too", yard.getSince() == 1900 && yard.knownAt(1));

        // Asking by the year is asking by the month of that year's January.
        record Asked(String what, java.util.function.IntPredicate byYear, java.util.function.IntPredicate byMonth) { }
        List<Asked> askers = List.of(new Asked("the tractor piece", tractor::knownIn, tractor::knownAt), new Asked("the tractor recipe", tractorRecipe::knownIn, tractorRecipe::knownAt),
                new Asked("the oxygen recipe", e1Recipe::knownIn, e1Recipe::knownAt), new Asked("kraft", kraft::knownIn, kraft::knownAt),
                new Asked("the horses' recipe", horses::knownIn, horses::knownAt), new Asked("the drive", drive::knownIn, drive::knownAt));
        String disagree = null, beforeStart = null;
        for (Asked a : askers) {
            for (int y = 1900; y <= 1960; y++) {
                if (Equipment.januaryOf(y) != PolicyDates.monthOf(y, 1)) disagree = "januaryOf(" + y + ")";
                if (a.byYear().test(y) != a.byMonth().test(PolicyDates.monthOf(y, 1)) && disagree == null) disagree = a.what() + " " + y;
            }
            if (a.byYear().test(1899) || a.byMonth().test(Equipment.januaryOf(1899))) beforeStart = a.what();
        }
        check("knownIn(y) is knownAt(PolicyDates.monthOf(y, 1)) for each of these, 1900 to 1960", disagree == null, disagree);
        check("...and none of them, dated 1900 or later, is known in 1899, the year before the start (PolicyDates.monthOf clamps 1899 to month 1; Equipment.januaryOf does not)", beforeStart == null, beforeStart);
        check("a year since reads the same way at the boundary: the 1907 recipe is known in 1907 and not in 1906; the tractor in 1918 and not in 1917 (its June is after that January)",
                kraft.knownIn(1907) && !kraft.knownIn(1906) && tractor.knownIn(1918) && !tractor.knownIn(1917) && e1Recipe.knownIn(1953) && !e1Recipe.knownIn(1952));

        System.out.println("\n--- the verdict and the choice are made on history's month ---");
        // Figures that pay, from the model's own functions as EquipmentCheck's rule section builds them: a gain a thousandth over both lines, and under.
        double r = 0.06, cost = 100;
        double factor = EquipmentFit.annuityFactor(tractor.getLifeMonths(), r);
        double hurdle = cost * r / 12 * BusinessInvestment.PROFIT_OVER_INTEREST;
        double line = Math.max(cost / factor, hurdle);
        EquipmentFit.Verdict before = EquipmentFit.judge(tractor, grain, pe14 - 1, 1, line * 1.001, cost, r, hurdle);
        EquipmentFit.Verdict at = EquipmentFit.judge(tractor, grain, pe14, 1, line * 1.001, cost, r, hurdle);
        EquipmentFit.Verdict short_ = EquipmentFit.judge(tractor, grain, pe14, 1, line * 0.999, cost, r, hurdle);
        check("fixture: a gain a thousandth over both lines (the annuity's and the hurdle) earns back the cost and passes the gate", line * 1.001 * factor >= cost && line * 1.001 >= hurdle
                && (line * 0.999 * factor < cost || line * 0.999 < hurdle));
        check("the tractor with figures that pay is NOT_KNOWN the month before PE14's, pricing nothing", before.kind() == EquipmentFit.Kind.NOT_KNOWN && Double.isNaN(before.gain()), before.toString());
        check("...and PAYS at PE14's month, with one building unfitted", at.kind() == EquipmentFit.Kind.PAYS && at.gain() == line * 1.001 && at.pieceId().equals("tractor"), at.toString());
        check("...while figures a thousandth short of paying are NOT_YET at that month: the rule runs once the piece is known", short_.kind() == EquipmentFit.Kind.NOT_YET, short_.toString());
        check("...and with no building unfitted it is FITTED before it is known, as it always was",
                EquipmentFit.judge(tractor, grain, pe14 - 1, 0, line * 1.001, cost, r, hurdle).kind() == EquipmentFit.Kind.FITTED);

        // The part that may run the oxygen converter's recipe: the oxygen shop's recipe piece is the open hearth, which the recipe does not need, so the part without it.
        check("fixture: the Oxygen Steel Shop's recipe piece is the open hearth, which the basic-oxygen recipe does not need, and the open-hearth recipe does",
                "open-hearth".equals(shop.recipePieceId()) && e1Recipe.getNeedsEquipment() == null && "open-hearth".equals(shop.getRecipe("open-hearth").getNeedsEquipment()));
        check("the basic-oxygen recipe cannot be run by the part without the open hearth the month before E1's, and can at it",
                !Recipes.canRun(shop, e1Recipe, false, e1 - 1) && Recipes.canRun(shop, e1Recipe, false, e1));
        check("...and the part with the open hearth never runs it, known or not; the open-hearth recipe is the fitted part's from the first month",
                !Recipes.canRun(shop, e1Recipe, true, e1 - 1) && !Recipes.canRun(shop, e1Recipe, true, e1)
                        && Recipes.canRun(shop, shop.getRecipe("open-hearth"), true, 1) && !Recipes.canRun(shop, shop.getRecipe("open-hearth"), false, e1));
        check("the Grain Farm's tractor recipe is the fitted part's, from PE14's month and not before, and the horses' recipe is the unfitted part's throughout",
                !Recipes.canRun(grain, tractorRecipe, true, pe14 - 1) && Recipes.canRun(grain, tractorRecipe, true, pe14) && !Recipes.canRun(grain, tractorRecipe, false, pe14)
                        && Recipes.canRun(grain, horses, false, 1) && Recipes.canRun(grain, horses, false, pe14));
    }

    /* ----- helpers ----- */

    static List<String> codes(ResearchCatalog c) {
        List<String> out = new ArrayList<>();
        for (Entry e : c.entries()) out.add(e.code());
        return out;
    }

    /** The studies' order: E, M, B, L1-L4, PE, PM, PB, PL, then L5. */
    static List<String> orderOf() {
        List<String> out = new ArrayList<>();
        for (int p = 0; p < PREFIXES.length; p++) {
            int n = PREFIX_COUNTS[p] - (PREFIXES[p].equals("L") ? 1 : 0);
            for (int i = 1; i <= n; i++) out.add(PREFIXES[p] + i);
        }
        out.add("L5");
        return out;
    }

    /** Whether every entry whose code starts with a prefix and a digit is a recipe with that kind note. */
    static boolean allLaw(ResearchCatalog c, String prefix, String note) {
        int n = 0;
        for (Entry e : c.entries()) {
            if (!prefixOf(e.code()).equals(prefix)) continue;
            n++;
            if (!e.kinds().equals(List.of("recipe")) || !e.kindNote().equals(note) || e.size() != Size.RECIPE || e.fee()) return false;
        }
        return n == 12;
    }

    static boolean drugs(ResearchCatalog c) {
        for (int i = 2; i <= 6; i++) {
            Entry e = c.entry("PM" + i);
            if (e == null || e.size() != Size.DRUG || !e.is("drug")) return false;
        }
        return true;
    }
}
