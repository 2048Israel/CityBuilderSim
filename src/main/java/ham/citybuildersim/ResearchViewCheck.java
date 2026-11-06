package ham.citybuildersim;

import ham.citybuildersim.ResearchCatalog.Entry;
import ham.citybuildersim.ResearchCatalog.Field;
import ham.citybuildersim.ResearchView.Discovery;
import ham.citybuildersim.ResearchView.Source;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * What the research screens read (R5, slice A: ResearchView, three series in HistorySave, ChartModel.flags(log, more), Recipes.chosen()).
 *
 * WHY. The screens draw only what the model hands them, and a wrong month in the timeline, a piece shown before it is known, or a NEW mark one month
 * too long would sit in a screen where no assertion in the model would see it. So every read is held to the model's own constants and functions
 * (FIRST_MONTH, NEW_MONTHS, Entry.worldMonth(), EquipmentFit.verdict(), Recipes.chosen()), each threshold is caused from both sides, and a city
 * founded the default way is shown to have nothing new on any of them (research runs only in a city founded the 1900 way, so every played city keeps
 * its screens). The cores that take a catalogue, a research and a month are driven at any month with a research of their own, and the Game-level reads
 * on cities the harness plays; researchTown() is the one fixture the next slices' screens share.
 *
 * What this has to prove:
 *
 *   1. A CITY FOUNDED THE DEFAULT WAY. The timeline, the equipment, the recipes in use, the new templates and the flags are all empty, every origin is null
 *      and no template is new even where a find sits in its research, knownAtFounding() is the catalogue's size, and the history has none of the three
 *      series (the share of the equipment fitted reads NaN in every month).
 *   2. A 1900 CITY AT ITS FOUNDING. An empty timeline; knownAtFounding() counts exactly the entries history dates at FIRST_MONTH or before, and an entry
 *      of the month after is a discovery, from both sides of FIRST_MONTH; its equipment stands, every piece unfitted.
 *   3. THE TIMELINE. A find here is HERE at the month it was found and years ahead of the world by (worldMonth - found) / 12; every other entry
 *      history dates after FIRST_MONTH and by now is ABROAD at its date; none after now; a find after history's date is the world's; a find at the founding
 *      month is no discovery; ordered by month, then the catalogue's order; byField partitions it.
 *   4. THE CUMULATIVE COUNTS. Monotone; the last entries are count(HERE) and count(ABROAD); entry 0 counts the first month itself and every month before it; the
 *      length rule, the empty case included.
 *   5. THE FLAGS. One entry a discovery, of its kind, at its month, in the words of its source; not in the decision log; and ChartModel.flags(log, more) puts a
 *      month's decisions and discoveries in one flag, decisions first, and keeps other months apart.
 *   6. THE EQUIPMENT. fitted + unfitted = buildings; the verdict is the Fit pill's; a piece not known at the month is absent and appears once it is; what is
 *      on site is counted; the sector filter keeps only its sector's templates; the discovery that made a piece known is on its row.
 *   7. THE RECIPES IN USE. Each listed recipe is non-default and known; running is Recipes.chosen() over the parts' sizes (a template with no piece is all
 *      one part); a recipe known and not chosen is listed with nothing running.
 *   8. WHERE A BUILDING CAME FROM. A template opened by a code found here is HERE at the month found, new for NEW_MONTHS - 1 months after it and not at
 *      NEW_MONTHS; from abroad the same; one known at the founding, a code not in the catalogue and a year not yet come have none; a year is ABROAD at its
 *      January; the four labs are labs and a school is not.
 *   9. THE SERIES. A 1900 city with grants and a prize records each field's researchers, the month's bill and prize and the share of the equipment fitted,
 *      every month and as the model says it, the same length as the axis; a late-beginning series begins at its first month and reads NaN before; all
 *      survive a save and load; the year book rules each; a reform scales the spend and nothing else; a cut drops a series that began after it.
 *  10. researchTown(). The fixture's own guarantees, one at a time, and what it cost to build.
 */
public class ResearchViewCheck {

    static int fails;
    static int passes;

    static void check(String label, boolean ok) {
        System.out.println((ok ? "  PASS " : "  FAIL ") + label);
        if (ok) passes++; else fails++;
    }

    /** The same, with what was found printed under a failure. */
    static void check(String label, boolean ok, String found) {
        check(label, ok);
        if (!ok && found != null) System.out.println("       found: " + found);
    }

    static PrintStream quiet;

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    /** The streams the borrowed fixtures (FittingCheck.village(), DiscoveryCheck.cityWithSchools()) are quiet through, set once and never over another harness's. */
    static void ensureQuiet() {
        if (quiet == null) quiet = new PrintStream(OutputStream.nullOutputStream());
        if (FittingCheck.quiet == null) FittingCheck.quiet = quiet;
        if (EquipmentCheck.quiet == null) EquipmentCheck.quiet = quiet;
        if (DiscoveryCheck.quiet == null) DiscoveryCheck.quiet = quiet;
    }

    /** One press of the clock, quietly (the player's button: the auto-builder's pass first). */
    static void play(Game g, int months) {
        quietly(() -> { for (int i = 0; i < months; i++) g.toggleNextMonth(); });
    }

    static final ResearchCatalog CAT = ResearchCatalog.get();

    static BuildingsTemplate template(Game g, String name) {
        return g.getBuildingManager().getTemplateByName(name);
    }

    /** Puts a city's finds back as given (and keeps its grants, posts and piles): the cheapest honest way to have a find, as DiscoveryCheck reaches them. */
    static void setFinds(Game g, Map<String, Integer> finds) {
        Research r = g.getResearch();
        r.restore(r.grantsForSave(), r.postsForSave(), r.piles(), finds);
    }

    /** The index of an entry in the catalogue's order. */
    static int index(Entry e) {
        return CAT.entries().indexOf(e);
    }

    /**
     * A village of FittingCheck.village() founded the 1900 way. The village is placed after newGame() without construction, so it comes fitted as placed;
     * the harness says the founding over it (Game.unfitTheFounding(), as FittingCheck does): every building without every piece, the recipes chosen.
     */
    static Game foundedVillage(String label) {
        Game g = FittingCheck.village(label, Founding.defaults().withUnfitted(true), false, false);
        quietly(g::unfitTheFounding);
        return g;
    }

    /* =====================================================================
       researchTown(): the 1900 city the research screens' checks share
       ===================================================================== */

    /**
     * A city founded the 1900 way, played 13 months (NEW_MONTHS + 1) from the founding to month 14, and ready to be read by the research screens. The
     * village is FittingCheck.village() (roads, homes, groceries, farms, bakeries, a power and a water plant) with a Pulp Mill, built without construction
     * and then stood up as a 1900 founding leaves it (Game.unfitTheFounding(): every building without its pieces, the recipes chosen).
     * It is PLAYED and not moved: the clock is the model's own, so the month is a month the city lived (the cheapest honest way found: a month costs a
     * fraction of a second in a village, and a save edited to another month would carry a history and a state of another age).
     *
     * GUARANTEES (section 10 asserts each; a change that breaks one fails here first):
     *
     *   (a) THE TIMELINE has at least one discovery found HERE and at least two from ABROAD: the electric arc steel furnace's code PE1 is restored as found in
     *       month 3, nine months ahead of history's month 12 (Research.restore(), as DiscoveryCheck reaches a find), and history dates the high-speed tool
     *       steel (PE2) in month 6 and rotary drilling (PE3) in month 13, both before the city's month 14, found by no one.
     *   (b) THE EQUIPMENT has a row on the Bakeries' stack, in sector "Industry": a piece known at the founding (the electric drive, 1900) that some of the
     *       stack's buildings lack (unfitted above 0, a city founded the 1900 way stands them up so), and whose verdict is not FITTED. The farms' tractor
     *       (PE14, history's month 210) is not known, so the Mixed Farm and the Grain Farm have no row.
     *   (c) A TEMPLATE IS NEW: the Electric Arc Furnace (opened by PE1) is NEW, its origin HERE at month 3, 11 months (NEW_MONTHS - 1) old, the last month
     *       it is new.
     *   (d) A RECIPE IS IN USE: the farms' horses (the tractor is the farms' default, not yet known) run on the Mixed Farm and the Grain Farm, whole stacks
     *       (every farm of a city founded the 1900 way lacks its tractor), and the Pulp Mill, a template with no recipe piece, runs a non-default way.
     *
     * Leaves no save slot written (the city is made with GameFiles.scratch()). Runs in about two to four seconds on the PC; section 10 prints what it took.
     * Static state is not touched but the borrowed fixtures' quiet streams, set only when unset.
     */
    static Game researchTown() throws Exception {
        ensureQuiet();
        Game g = foundedVillage("research-town");
        quietly(() -> {
            g.buildStack(template(g, "Pulp Mill"), 2, true);
            g.unfitTheFounding();
        });
        int months = ResearchView.NEW_MONTHS + 1;
        // PE1 found here at the month that makes the Electric Arc Furnace NEW_MONTHS - 1 months old when the clock gets there (month 1 + months).
        int foundAt = 1 + months - (ResearchView.NEW_MONTHS - 1);
        setFinds(g, Map.of("PE1", foundAt));
        play(g, months);
        return g;
    }

    /* =====================================================================
       main
       ===================================================================== */

    public static void main(String[] args) throws Exception {
        ensureQuiet();
        long started = System.nanoTime();
        Game town = researchTown();
        long built = (System.nanoTime() - started) / 1_000_000;
        sectionOne();
        sectionTwo();
        sectionThree();
        sectionFour();
        sectionFive(town);
        sectionSix(town);
        sectionSeven(town);
        sectionEight(town);
        sectionNine();
        sectionTen(town, built);
        System.out.println(fails == 0 ? "ResearchViewCheck: all " + passes + " ok" : "ResearchViewCheck: " + fails + " FAILED of " + (passes + fails));
        System.exit(fails);
    }

    /* ==================== 1. a city founded the default way ==================== */

    static void sectionOne() {
        System.out.println("\n--- 1. a city founded the default way: nothing new on any read, and the history has none of the series ---");
        Game d = FittingCheck.village("research-view-default", Founding.defaults(), false, false);
        play(d, 2);
        check("fixture: founded the default way, and the village stands: a Bakery carrying a piece, a Mixed Farm with a choice of recipes",
                !d.foundedUnfitted() && FittingCheck.stack(d, "Bakery").getQuantity() > 0 && !template(d, "Bakery").getEquipment().isEmpty()
                        && FittingCheck.stack(d, "Mixed Farm").getQuantity() > 0 && template(d, "Mixed Farm").hasRecipeChoice());
        check("the timeline is empty", ResearchView.timeline(d).isEmpty());
        check("...the equipment (whole city and a sector) and the recipes in use are empty though the stacks stand",
                ResearchView.equipment(d, null).isEmpty() && ResearchView.equipment(d, template(d, "Bakery").getSector()).isEmpty() && ResearchView.recipesInUse(d).isEmpty());
        check("...no flag, no new template, and the share of the equipment fitted is not a number",
                ResearchView.flagEntries(d).isEmpty() && ResearchView.newTemplates(d).isEmpty() && Double.isNaN(ResearchView.fittedShare(d)));
        boolean noOrigin = true, noneNew = true;
        for (BuildingsTemplate t : d.getBuildingManager().getTemplates()) {
            noOrigin &= ResearchView.origin(d, t) == null;
            noneNew &= !ResearchView.isNew(d, t);
        }
        check("...every template's origin is null and none is new", noOrigin && noneNew);
        check("...and it knew every discovery at its founding: knownAtFounding() is the catalogue's " + CAT.entries().size(), ResearchView.knownAtFounding(d) == CAT.entries().size());

        // The guard is what makes it so: the same research read as a 1900 city's would have a discovery, an origin and a NEW template.
        BuildingsTemplate furnace = template(d, "Electric Arc Furnace");
        setFinds(d, Map.of("PE1", ResearchView.FIRST_MONTH + 1));
        List<Discovery> as1900 = ResearchView.timeline(CAT, d.getResearch(), d.getMonth());
        ResearchView.Origin o1900 = ResearchView.origin(CAT, d.getResearch(), d.getMonth(), furnace);
        check("with a find in its research (PE1 in month " + (ResearchView.FIRST_MONTH + 1) + ") read as a 1900 city's it would have a discovery, an origin and a NEW furnace",
                as1900.size() == 1 && o1900 != null && ResearchView.isNew(o1900, d.getMonth()));
        check("...and read as the city it is, still nothing: no discovery, no origin, not new, no flag",
                ResearchView.timeline(d).isEmpty() && ResearchView.origin(d, furnace) == null && !ResearchView.isNew(d, furnace)
                        && ResearchView.newTemplates(d).isEmpty() && ResearchView.flagEntries(d).isEmpty());
        setFinds(d, Map.of());

        HistorySave h = d.getHistorySave();
        List<String> names = new ArrayList<>(List.of(HistorySave.RESEARCH_SPEND, HistorySave.EQUIPMENT_FITTED));
        for (Field f : Field.values()) names.add(HistorySave.researchersKey(f));
        boolean absent = true, allNaN = true;
        for (String name : names) {
            absent &= !h.seriesByName().containsKey(name);
            double[] v = h.aligned(name);
            allNaN &= v.length == h.months();
            for (double x : v) allNaN &= Double.isNaN(x);
        }
        check("its history has none of the " + names.size() + " series, so its file and its year book are as they were, and each reads NaN in every one of its " + h.months() + " months",
                h.months() >= 2 && absent && allNaN);
    }

    /* ==================== 2. a 1900 city at its founding ==================== */

    /** One entry of a catalogue text for the harness's own catalogue: January or the given month of 1900. */
    static String entryText(String code, int month) {
        return "{\"code\":\"" + code + "\",\"name\":\"Test " + code + "\",\"field\":\"ENGINEERING\",\"kind\":[\"building\"],\"size\":\"building\","
                + "\"year\":1900,\"month\":" + month + ",\"monthKnown\":true,\"work\":100}";
    }

    /** A catalogue of the harness's own: T1, T2, T3 dated 1900's first three months (history's months 1, 2, 3). */
    static ResearchCatalog threeMonths() {
        return ResearchCatalog.parse("{\"entries\":[" + entryText("T1", 1) + "," + entryText("T2", 2) + "," + entryText("T3", 3) + "]}");
    }

    /** A month well past the first discoveries, for the cores of section 2. */
    static final int NOW_SECTION_TWO = 50;

    static void sectionTwo() {
        System.out.println("\n--- 2. a city founded the 1900 way, at its founding: no discovery yet, and FIRST_MONTH is where the line is ---");
        Game f = foundedVillage("research-view-founding");
        int known = 0;
        for (Entry e : CAT.entries()) if (e.worldMonth() <= ResearchView.FIRST_MONTH) known++;
        check("fixture: founded the 1900 way, in the founding month", f.foundedUnfitted() && f.getMonth() == ResearchView.FIRST_MONTH);
        check("the timeline is empty", ResearchView.timeline(f).isEmpty() && ResearchView.flagEntries(f).isEmpty() && ResearchView.newTemplates(f).isEmpty());
        check("knownAtFounding() counts exactly the entries history dates at FIRST_MONTH or before (" + known + ") - not the whole catalogue of " + CAT.entries().size(),
                ResearchView.knownAtFounding(f) == known && known < CAT.entries().size());

        // From both sides of FIRST_MONTH, with a catalogue of the harness's own: T1 is history's month 1, T2 month 2, T3 month 3.
        ResearchCatalog three = threeMonths();
        check("fixture: the harness's catalogue has three entries in months 1, 2 and 3",
                three.entries().size() == 3 && three.entries().get(0).worldMonth() == ResearchView.FIRST_MONTH && three.entries().get(1).worldMonth() == ResearchView.FIRST_MONTH + 1
                        && three.entries().get(2).worldMonth() == ResearchView.FIRST_MONTH + 2);
        Research none = new Research();
        check("an entry history dates AT FIRST_MONTH was known at the founding: counted by knownAtFounding(), never on the timeline",
                ResearchView.knownAtFounding(three, none) == 1 && ResearchView.timeline(three, none, ResearchView.FIRST_MONTH).isEmpty());
        List<Discovery> second = ResearchView.timeline(three, none, ResearchView.FIRST_MONTH + 1);
        check("...the entry of the month AFTER it is a discovery, from abroad, when the clock gets there and not before",
                second.size() == 1 && second.get(0).entry().code().equals("T2") && second.get(0).month() == ResearchView.FIRST_MONTH + 1 && second.get(0).source() == Source.ABROAD);
        // A find, from both sides of FIRST_MONTH, with the real catalogue (a find is restored only for a code it has): PE3, history's month 13.
        Entry pe3 = CAT.entry("PE3");
        Research early = new Research();
        early.restore(null, null, null, Map.of("PE3", ResearchView.FIRST_MONTH));
        List<Discovery> earlyTimeline = ResearchView.timeline(CAT, early, NOW_SECTION_TWO);
        check("a find AT FIRST_MONTH is known at the founding too: PE3 found in month " + ResearchView.FIRST_MONTH + " is counted by knownAtFounding() (" + (known + 1) + ") and is no discovery of the city's life",
                ResearchView.knownAtFounding(CAT, early) == known + 1 && earlyTimeline.stream().noneMatch(d -> d.entry() == pe3));
        Research later = new Research();
        later.restore(null, null, null, Map.of("PE3", ResearchView.FIRST_MONTH + 1));
        Discovery laterFind = ResearchView.timeline(CAT, later, NOW_SECTION_TWO).stream().filter(d -> d.entry() == pe3).findFirst().orElse(null);
        check("...found the month after, it is one: HERE in month " + (ResearchView.FIRST_MONTH + 1) + ", " + (pe3.worldMonth() - ResearchView.FIRST_MONTH - 1) + " months ahead of history's, and knownAtFounding() is as it was",
                laterFind != null && laterFind.source() == Source.HERE && laterFind.month() == ResearchView.FIRST_MONTH + 1
                        && laterFind.yearsAhead() == (pe3.worldMonth() - ResearchView.FIRST_MONTH - 1) / 12.0 && ResearchView.knownAtFounding(CAT, later) == known);

        List<ResearchView.PieceRow> rows = ResearchView.equipment(f, null);
        boolean allUnfitted = !rows.isEmpty();
        for (ResearchView.PieceRow row : rows) allUnfitted &= row.unfitted() == row.buildings() && row.fitted() == 0;
        check("its equipment stands: " + rows.size() + " rows, every building without its piece (a city founded the 1900 way stands them up unfitted), so the share fitted is 0",
                allUnfitted && ResearchView.fittedShare(f) == 0);
    }

    /* ==================== 3. the timeline ==================== */

    /** The finds of section 3's research: PE1 and PE14 before history's date, PE2 after it, PE3 at the founding month and PL5 the month after it. */
    static final Map<String, Integer> FINDS = Map.of("PE1", 4, "PE14", 150, "PE2", 20, "PE3", ResearchView.FIRST_MONTH, "PL5", ResearchView.FIRST_MONTH + 1);

    static Research someFinds() {
        Research r = new Research();
        r.restore(null, null, null, FINDS);
        return r;
    }

    static final int NOW = 400;

    static void sectionThree() {
        System.out.println("\n--- 3. the timeline: found here at the month found, from abroad on history's date, none after now, in order ---");
        Research r = someFinds();
        List<Discovery> t = ResearchView.timeline(CAT, r, NOW);
        Map<String, Discovery> byCode = new LinkedHashMap<>();
        for (Discovery d : t) byCode.put(d.entry().code(), d);

        Entry pe1 = CAT.entry("PE1"), pe14 = CAT.entry("PE14"), pe2 = CAT.entry("PE2"), pe3 = CAT.entry("PE3"), pl5 = CAT.entry("PL5");
        check("fixture: the finds are before history's date, but PE2's is after it (found in month 20, history's month " + pe2.worldMonth() + ")",
                4 < pe1.worldMonth() && 150 < pe14.worldMonth() && 20 > pe2.worldMonth() && pl5.worldMonth() > ResearchView.FIRST_MONTH + 1);
        Discovery d1 = byCode.get("PE1"), d14 = byCode.get("PE14"), d5 = byCode.get("PL5");
        check("a find here is HERE at the month it was found, and years ahead by (worldMonth - found) / 12",
                d1 != null && d1.source() == Source.HERE && d1.month() == 4 && d1.yearsAhead() == (pe1.worldMonth() - 4) / 12.0 && d1.worldMonth() == pe1.worldMonth()
                        && d14 != null && d14.source() == Source.HERE && d14.month() == 150 && d14.yearsAhead() == (pe14.worldMonth() - 150) / 12.0);
        check("...a find the month after FIRST_MONTH counts (PL5 in month " + (ResearchView.FIRST_MONTH + 1) + "), a find AT FIRST_MONTH does not (PE3 is not on the timeline)",
                d5 != null && d5.source() == Source.HERE && d5.month() == ResearchView.FIRST_MONTH + 1 && !byCode.containsKey("PE3"));
        check("...a find after history's date is the world's: PE2 is ABROAD at history's month " + pe2.worldMonth() + ", 0 years ahead",
                byCode.get("PE2") != null && byCode.get("PE2").source() == Source.ABROAD && byCode.get("PE2").month() == pe2.worldMonth() && byCode.get("PE2").yearsAhead() == 0);

        // The harness's own reading of the catalogue: an entry is found here when its find is before history's date, and then it is known from the find.
        int expected = 0, wrongSource = 0, wrongMonth = 0, abroad = 0, here = 0, atFounding = 0;
        for (Entry e : CAT.entries()) {
            Integer found = FINDS.get(e.code());
            boolean ahead = found != null && found < e.worldMonth();
            int month = ahead ? found : e.worldMonth();
            if (month <= ResearchView.FIRST_MONTH) atFounding++;
            if (month <= ResearchView.FIRST_MONTH || month > NOW) {
                if (byCode.containsKey(e.code())) wrongMonth++;
                continue;
            }
            expected++;
            Discovery d = byCode.get(e.code());
            if (d == null) { wrongMonth++; continue; }
            if (d.month() != month) wrongMonth++;
            if (d.source() != (ahead ? Source.HERE : Source.ABROAD)) wrongSource++;
            if (ahead) here++; else abroad++;
        }
        check("every other entry history dates after FIRST_MONTH and by month " + NOW + " is ABROAD at its date: " + abroad + " of them, and " + here + " found here, " + t.size() + " in all",
                t.size() == expected && wrongMonth == 0 && wrongSource == 0 && ResearchView.count(t, Source.ABROAD) == abroad && ResearchView.count(t, Source.HERE) == here && here == 3);
        check("...the one found at the founding month (PE3) is counted as known at the founding instead: " + atFounding, atFounding == 1 && ResearchView.knownAtFounding(CAT, r) == atFounding);

        boolean noneAfter = true, ordered = true;
        for (int i = 0; i < t.size(); i++) {
            noneAfter &= t.get(i).month() <= NOW;
            if (i > 0) {
                Discovery a = t.get(i - 1), b = t.get(i);
                ordered &= a.month() < b.month() || (a.month() == b.month() && index(a.entry()) < index(b.entry()));
            }
        }
        check("none is after the city's month, and they are ordered by month and, inside a month, by the catalogue's order", noneAfter && ordered);
        boolean tied = false;
        for (int i = 1; i < t.size(); i++) tied |= t.get(i - 1).month() == t.get(i).month();
        check("fixture: some two share a month, so the tie-break was caused", tied);
        List<Discovery> earlier = ResearchView.timeline(CAT, r, 100);
        boolean prefix = earlier.size() < t.size();
        for (int i = 0; i < earlier.size(); i++) prefix &= earlier.get(i).equals(t.get(i));
        check("at an earlier month the timeline is the shorter run of the same: month 100 has " + earlier.size() + " discoveries, the first " + earlier.size() + " of month " + NOW + "'s", prefix);

        EnumMap<Field, List<Discovery>> split = ResearchView.byField(t);
        int total = 0;
        boolean inEnumOrder = new ArrayList<>(split.keySet()).equals(List.of(Field.values())), right = true, keepsOrder = true;
        for (Map.Entry<Field, List<Discovery>> e : split.entrySet()) {
            total += e.getValue().size();
            int last = -1;
            for (Discovery d : e.getValue()) {
                right &= d.field() == e.getKey();
                int at = t.indexOf(d);
                keepsOrder &= at > last;
                last = at;
            }
        }
        check("byField() partitions the timeline: every discovery in its field's list (" + total + " of " + t.size() + "), the fields in the enum's order, each list in the timeline's order",
                total == t.size() && inEnumOrder && right && keepsOrder);
        EnumMap<Field, List<Discovery>> emptySplit = ResearchView.byField(List.of());
        check("...and an empty timeline still has all four fields, each with an empty list", emptySplit.size() == Field.values().length && emptySplit.values().stream().allMatch(List::isEmpty));
    }

    /* ==================== 4. the cumulative counts ==================== */

    static void sectionFour() {
        System.out.println("\n--- 4. cumulative(): monotone, ending at the counts, the first entry the first month and all before it, the length rule ---");
        List<Discovery> t = ResearchView.timeline(CAT, someFinds(), NOW);
        int here = ResearchView.count(t, Source.HERE), abroad = ResearchView.count(t, Source.ABROAD);
        int[][] c = ResearchView.cumulative(t, 1, NOW);
        boolean monotone = true;
        for (int i = 1; i < c[0].length; i++) monotone &= c[0][i] >= c[0][i - 1] && c[1][i] >= c[1][i - 1];
        check("over months 1 to " + NOW + ": two arrays of " + NOW + ", monotone", c.length == 2 && c[0].length == NOW && c[1].length == NOW && monotone);
        check("...the last entries are the counts: " + here + " found here, " + abroad + " from abroad", c[0][NOW - 1] == here && c[1][NOW - 1] == abroad);

        int first = t.get(0).month();
        int atFirst = 0;
        for (Discovery d : t) if (d.month() <= first) atFirst++;
        int[][] fromFirst = ResearchView.cumulative(t, first, NOW);
        check("starting at the first discovery's month (" + first + "), entry 0 counts that month itself: " + atFirst, fromFirst[0][0] + fromFirst[1][0] == atFirst && atFirst >= 1);
        int[][] justBefore = ResearchView.cumulative(t, first - 1, NOW);
        check("...starting one month before it, entry 0 does not and entry 1 does", justBefore[0][0] + justBefore[1][0] == 0 && justBefore[0][1] + justBefore[1][1] == atFirst);
        int mid = 200, before = 0, here200 = 0;
        for (Discovery d : t) {
            if (d.month() <= mid) before++;
            if (d.month() <= mid && d.source() == Source.HERE) here200++;
        }
        int[][] fromMid = ResearchView.cumulative(t, mid, NOW);
        check("starting after some, entry 0 holds every discovery of that month and before it (" + before + " of them, " + here200 + " found here)", fromMid[0][0] + fromMid[1][0] == before && fromMid[0][0] == here200);
        int[][] cut = ResearchView.cumulative(t, 1, 100);
        int upTo100 = 0;
        for (Discovery d : t) if (d.month() <= 100) upTo100++;
        check("a window that ends early leaves the later ones out: months 1 to 100 total " + upTo100, cut[0].length == 100 && cut[0][99] + cut[1][99] == upTo100 && upTo100 < t.size());

        check("one month is one entry", ResearchView.cumulative(t, 7, 7)[0].length == 1 && ResearchView.cumulative(t, 7, 7)[1].length == 1);
        int[][] none = ResearchView.cumulative(t, 8, 7);
        check("a window that ends before it starts is empty, two empty arrays", none.length == 2 && none[0].length == 0 && none[1].length == 0);
        int[][] noTimeline = ResearchView.cumulative(List.of(), 1, 5);
        check("an empty timeline counts nothing in any month", noTimeline[0].length == 5 && noTimeline[0][4] == 0 && noTimeline[1][4] == 0);
    }

    /* ==================== 5. the flags ==================== */

    static void sectionFive(Game town) {
        System.out.println("\n--- 5. the flags: one a discovery in the words of its source, never in the decision log, and one flag a month with the decisions ---");
        List<Discovery> t = ResearchView.timeline(town);
        List<DecisionLog.Entry> flags = ResearchView.flagEntries(town);
        boolean words = flags.size() == t.size() && !t.isEmpty();
        for (int i = 0; words && i < t.size(); i++) {
            Discovery d = t.get(i);
            DecisionLog.Entry e = flags.get(i);
            String expected = d.source() == Source.HERE
                    ? "Found here: " + d.entry().name() + ", " + String.format(java.util.Locale.ROOT, "%.1f", d.yearsAhead()) + " years before the world"
                    : "From abroad: " + d.entry().name();
            words &= e.kind().equals(ResearchView.FLAG_KIND) && e.month() == d.month() && e.label().equals(expected);
        }
        check("one entry for each of the " + t.size() + " discoveries, oldest first, of kind \"" + ResearchView.FLAG_KIND + "\" at its month, in the words of its source", words);
        DecisionLog.Entry here = flags.stream().filter(e -> e.label().startsWith("Found here")).findFirst().orElse(null);
        DecisionLog.Entry abroad = flags.stream().filter(e -> e.label().startsWith("From abroad")).findFirst().orElse(null);
        check("the finds here say \"Found here: ... 0.8 years before the world\" (nine months is 0.75: one decimal, and always \"years\"); the world's say \"From abroad: ...\"",
                here != null && here.label().endsWith("0.8 years before the world") && abroad != null && abroad.label().matches("From abroad: .+") && !abroad.label().contains("before the world"),
                here + " / " + abroad);
        boolean inLog = false;
        for (DecisionLog.Entry e : town.getDecisions().entries()) inLog |= e.kind().equals(ResearchView.FLAG_KIND);
        check("none of them is in the decision log: a discovery is not a decision", !inLog);

        int[] clock = { 3 };
        DecisionLog log = new DecisionLog(() -> clock[0]);
        log.record(DecisionLog.TAX, "taxes to 17%");
        log.record(DecisionLog.PROMISE, "minimum wage up");
        clock[0] = 6;
        log.record(DecisionLog.TAX, "sales tax to 5%");
        clock[0] = 9;
        log.record(DecisionLog.BANK, "bank rescued");
        List<DecisionLog.Entry> more = List.of(new DecisionLog.Entry(9, ResearchView.FLAG_KIND, "From abroad: D"), new DecisionLog.Entry(3, ResearchView.FLAG_KIND, "Found here: A"),
                new DecisionLog.Entry(3, ResearchView.FLAG_KIND, "From abroad: B"), new DecisionLog.Entry(4, ResearchView.FLAG_KIND, "From abroad: C"),
                new DecisionLog.Entry(2, ResearchView.FLAG_KIND, "From abroad: Z"));
        List<ChartModel.Flag> merged = ChartModel.flags(log, more);
        check("the decisions and the discoveries make one flag a month, oldest month first: months 2, 3, 4, 6 and 9",
                merged.size() == 5 && merged.get(0).month() == 2 && merged.get(1).month() == 3 && merged.get(2).month() == 4 && merged.get(3).month() == 6 && merged.get(4).month() == 9);
        check("...a decision and a discovery of month 3 are ONE flag of four, the month's decisions first in the log's order, then the discoveries in theirs",
                merged.get(1).count() == 4 && labels(merged.get(1)).equals(List.of("taxes to 17%", "minimum wage up", "Found here: A", "From abroad: B")), labels(merged.get(1)).toString());
        check("...other months are kept apart: 2 and 4 only a discovery, 6 only a decision, 9 the decision first and then the discovery",
                labels(merged.get(0)).equals(List.of("From abroad: Z")) && labels(merged.get(2)).equals(List.of("From abroad: C")) && labels(merged.get(3)).equals(List.of("sales tax to 5%"))
                        && labels(merged.get(4)).equals(List.of("bank rescued", "From abroad: D")));
        List<ChartModel.Flag> plain = ChartModel.flags(log);
        List<ChartModel.Flag> noMore = ChartModel.flags(log, (List<DecisionLog.Entry>) null);
        boolean same = plain.size() == noMore.size();
        for (int i = 0; same && i < plain.size(); i++) same = plain.get(i).month() == noMore.get(i).month() && labels(plain.get(i)).equals(labels(noMore.get(i)));
        check("no discoveries (null, or empty) reads as the log's own flags", same && ChartModel.flags(log, List.<DecisionLog.Entry>of()).size() == plain.size());
        List<ChartModel.Flag> noLog = ChartModel.flags((DecisionLog) null, more);
        check("no log (null) reads as the discoveries alone, one flag a month, and nothing at all as no flag", noLog.size() == 4 && noLog.get(1).month() == 3 && noLog.get(1).count() == 2
                && ChartModel.flags((DecisionLog) null, (List<DecisionLog.Entry>) null).isEmpty());
        boolean cannotChange = false;
        try {
            merged.get(1).entries().add(new DecisionLog.Entry(3, "x", "y"));
        } catch (UnsupportedOperationException e) {
            cannotChange = true;
        }
        check("a flag's entries are read-only, as the log's flags' are", cannotChange);
    }

    static List<String> labels(ChartModel.Flag f) {
        List<String> out = new ArrayList<>();
        for (DecisionLog.Entry e : f.entries()) out.add(e.label());
        return out;
    }

    /* ==================== 6. the equipment ==================== */

    static void sectionSix(Game town) {
        System.out.println("\n--- 6. the equipment: what stands and is known, with the Fit pill's verdict, from both sides of a piece being known ---");
        int month = town.getMonth();
        List<ResearchView.PieceRow> rows = ResearchView.equipment(town, null);
        check("the town's equipment has rows", !rows.isEmpty());
        boolean arithmetic = true, verdicts = true, known = true, stands = true, order = true;
        int lastTemplate = -1;
        for (ResearchView.PieceRow row : rows) {
            BuildingsStacks stack = town.getBuildingManager().getStack(row.template());
            stands &= stack != null && stack.getQuantity() > 0 && row.buildings() == stack.getQuantity();
            arithmetic &= row.fitted() + row.unfitted() == row.buildings() && row.unfitted() == Math.min(stack.getQuantity(), stack.getUnfitted(row.piece().getId()))
                    && row.onSite() == town.getFittings().onSite(row.template().getId(), row.piece().getId());
            EquipmentFit.Verdict v = EquipmentFit.verdict(town, stack, row.piece());
            verdicts &= row.verdict().kind() == v.kind() && row.verdict().templateId() == v.templateId() && row.verdict().pieceId().equals(v.pieceId());
            known &= row.piece().knownAt(month, town.getResearch()) && row.verdict().kind() != EquipmentFit.Kind.NOT_KNOWN;
            int at = town.getBuildingManager().getTemplates().indexOf(row.template());
            order &= at >= lastTemplate;
            lastTemplate = at;
        }
        check("every row is a stack that stands, with fitted + unfitted = buildings, unfitted the stack's count (at most its buildings) and on site what the fittings hold",
                stands && arithmetic);
        check("...its verdict is EquipmentFit.verdict() on that stack and piece - the Fit pill's call - and every piece is known at the city's month", verdicts && known);
        check("...in the templates' order", order);
        double buildings = 0, fitted = 0;
        for (ResearchView.PieceRow row : rows) {
            buildings += row.buildings();
            fitted += row.fitted();
        }
        check("the share fitted is the rows' fitted buildings over their buildings (" + fitted + " of " + buildings + ")", ResearchView.fittedShare(town) == fitted / buildings);

        BuildingsTemplate bakery = template(town, "Bakery"), mixed = template(town, "Mixed Farm"), grain = template(town, "Grain Farm");
        Equipment tractor = mixed.getEquipment("tractor"), drive = bakery.getEquipment("electric-drive");
        boolean farmsStand = FittingCheck.stack(town, "Mixed Farm").getQuantity() > 0 && FittingCheck.stack(town, "Grain Farm").getQuantity() > 0;
        check("a piece not known at the month is not shown: the farms' tractor (" + tractor.getSince() + ", history's month " + tractor.knownFrom() + ") is not known in month " + month + ", though the farms stand",
                farmsStand && !tractor.knownAt(month, town.getResearch()) && rows.stream().noneMatch(r -> r.template() == mixed || r.template() == grain));
        ResearchView.PieceRow driveRow = rows.stream().filter(r -> r.template() == bakery && r.piece() == drive).findFirst().orElse(null);
        check("...the Bakery's electric drive (known from a year) is, with no discovery behind it", driveRow != null && driveRow.from() == null && driveRow.unfitted() > 0);

        // The same piece, once a find makes it known: PE14 found here in the month before. The farms' tractor rows appear, and with them a second sector.
        int pe1Month = town.getResearch().foundMonth("PE1");
        setFinds(town, Map.of("PE1", pe1Month, "PE14", month - 1));
        List<ResearchView.PieceRow> withTractor = ResearchView.equipment(town, null);
        List<ResearchView.PieceRow> farmRows = new ArrayList<>();
        for (ResearchView.PieceRow row : withTractor) if (row.template() == mixed || row.template() == grain) farmRows.add(row);
        boolean farms = farmRows.size() == 2;
        for (ResearchView.PieceRow row : farmRows) {
            farms &= row.piece().getId().equals(tractor.getId()) && row.verdict().kind() != EquipmentFit.Kind.NOT_KNOWN && row.verdict().kind() != EquipmentFit.Kind.FITTED
                    && row.unfitted() == row.buildings() && row.from() != null && row.from().entry().code().equals("PE14") && row.from().source() == Source.HERE
                    && row.from().month() == month - 1;
        }
        check("...found here (PE14 in month " + (month - 1) + "), the tractor's two rows appear, unfitted, with a verdict that is no longer NOT_KNOWN and the discovery that made it known on them",
                farms && withTractor.size() == rows.size() + 2);

        // The sector filter, over the city with both sectors' rows.
        String sector = bakery.getSector(), farmSector = mixed.getSector();
        List<ResearchView.PieceRow> mine = ResearchView.equipment(town, sector);
        List<ResearchView.PieceRow> theirs = ResearchView.equipment(town, farmSector);
        boolean onlyMine = !mine.isEmpty() && !theirs.isEmpty() && !sector.equals(farmSector);
        for (ResearchView.PieceRow row : mine) onlyMine &= row.template().isOwnedBySector() && sector.equals(row.template().getSector());
        for (ResearchView.PieceRow row : theirs) onlyMine &= row.template().isOwnedBySector() && farmSector.equals(row.template().getSector());
        int summed = 0;
        java.util.Set<String> sectors = new java.util.LinkedHashSet<>();
        for (ResearchView.PieceRow row : withTractor) sectors.add(row.template().getSector());
        for (String key : sectors) summed += ResearchView.equipment(town, key).size();
        check("the sector filter keeps only its sector's templates (" + sector + ": " + mine.size() + " rows, " + farmSector + ": " + theirs.size() + " of " + withTractor.size() + "), and the sectors' rows add up to the city's",
                onlyMine && summed == withTractor.size() && sectors.size() == 2 && mine.size() + theirs.size() == withTractor.size());
        check("...a sector with no stack, or no sector at all, has none", ResearchView.equipment(town, "No Such Sector").isEmpty());
        setFinds(town, Map.of("PE1", pe1Month));

        // On site: a fitting ordered is counted, and cleared.
        BuildingsStacks bakeryStack = FittingCheck.stack(town, "Bakery");
        town.getFittings().add(bakery.getId(), drive.getId(), "research-view-check", 100);
        ResearchView.PieceRow fitting = ResearchView.equipment(town, null).stream().filter(r -> r.template() == bakery && r.piece() == drive).findFirst().orElse(null);
        check("a fitting on site is counted on its row: one Bakery drive being fitted", fitting != null && fitting.onSite() == 1 && fitting.buildings() == bakeryStack.getQuantity());
        town.getFittings().clear();
    }

    /* ==================== 7. the recipes in use ==================== */

    static void sectionSeven(Game town) {
        System.out.println("\n--- 7. the recipes in use: non-default, known, and running as Recipes.chosen() and the parts' sizes say ---");
        int month = town.getMonth();
        List<ResearchView.RecipeUse> uses = ResearchView.recipesInUse(town);
        boolean listed = !uses.isEmpty(), plain = true, counts = true;
        for (ResearchView.RecipeUse use : uses) {
            BuildingsStacks stack = town.getBuildingManager().getStack(use.template());
            plain &= !use.recipe().isDefault() && use.recipe().knownAt(month, town.getResearch()) && use.template().hasRecipeChoice();
            String piece = use.template().recipePieceId();
            int unfitted = piece == null ? 0 : Math.min(stack.getQuantity(), stack.getUnfitted(piece));
            String[] picked = town.getRecipes().chosen(use.template().getId());
            int expected = (picked != null && picked[0].equals(use.recipe().getId()) ? stack.getQuantity() - unfitted : 0) + (picked != null && picked[1].equals(use.recipe().getId()) ? unfitted : 0);
            counts &= use.buildings() == stack.getQuantity() && use.running() == expected && use.running() >= 0 && use.running() <= use.buildings();
        }
        check("the town has recipes in use: " + uses.size() + " rows", listed);
        check("...each listed recipe is not its template's default and is known at the city's month", plain);
        check("...running is the buildings of the parts that chose it (Recipes.chosen()): the fitted part is the stack less its unfitted count of the recipe piece, the unfitted part that count", counts);

        BuildingsTemplate mixed = template(town, "Mixed Farm");
        BuildingsStacks farms = FittingCheck.stack(town, "Mixed Farm");
        String[] picked = town.getRecipes().chosen(mixed.getId());
        ResearchView.RecipeUse horses = uses.stream().filter(u -> u.template() == mixed).findFirst().orElse(null);
        check("fixture: the Mixed Farm's choice is kept - the tractor (its default, not known yet) for the fitted part, the horses for the unfitted - and its recipe piece is the tractor",
                picked != null && picked.length == 2 && picked[1].equals("horses") && "tractor".equals(mixed.recipePieceId()) && !mixed.getRecipe("tractor").knownAt(month, town.getResearch()), String.valueOf(picked == null ? null : picked[0] + "/" + picked[1]));
        check("the horses run on every farm of the stack: each lacks its tractor, so the unfitted part is the whole stack (" + farms.getQuantity() + ")",
                horses != null && horses.recipe().getId().equals("horses") && horses.running() == farms.getQuantity() && horses.buildings() == farms.getQuantity() && horses.from() == null);

        // The parts' sizes: one farm with the tractor, one without.
        int total = farms.getQuantity();
        farms.setUnfitted("tractor", total - 1);
        ResearchView.RecipeUse oneLeft = ResearchView.recipesInUse(town).stream().filter(u -> u.template() == mixed).findFirst().orElse(null);
        check("with one farm of " + total + " fitted, the unfitted part is " + (total - 1) + " and the horses run on that many",
                oneLeft != null && oneLeft.running() == total - 1 && oneLeft.buildings() == total);
        farms.setUnfitted("tractor", total);

        // A template with no recipe piece is all one part.
        BuildingsTemplate pulp = template(town, "Pulp Mill");
        BuildingsStacks mills = FittingCheck.stack(town, "Pulp Mill");
        String[] pulpPicked = town.getRecipes().chosen(pulp.getId());
        List<ResearchView.RecipeUse> pulpUses = new ArrayList<>();
        for (ResearchView.RecipeUse use : uses) if (use.template() == pulp) pulpUses.add(use);
        int running = 0, listedWays = 0;
        for (Recipe r : pulp.getRecipes()) if (!r.isDefault() && r.knownAt(month, town.getResearch())) listedWays++;
        for (ResearchView.RecipeUse use : pulpUses) running += use.running();
        check("a template with no recipe piece is all one part: the Pulp Mill (" + mills.getQuantity() + " mills, no piece) lists its " + listedWays + " known non-default ways, its choice runs on every mill and the others on none",
                pulp.recipePieceId() == null && pulpPicked != null && pulpUses.size() == listedWays && listedWays >= 2 && running == mills.getQuantity()
                        && pulpUses.stream().filter(u -> u.running() > 0).count() == 1);
        check("...a way known and not chosen is listed with nothing running", pulpUses.stream().anyMatch(u -> u.running() == 0));

        // A template with no stack lists nothing.
        boolean noStackNoRow = true;
        for (ResearchView.RecipeUse use : uses) noStackNoRow &= town.getBuildingManager().getStack(use.template()) != null && town.getBuildingManager().getStack(use.template()).getQuantity() > 0;
        BuildingsTemplate ironMine = template(town, "Iron Mine");
        check("a template with a choice of recipes and no stack lists nothing: the Iron Mine has " + (ironMine.getRecipes().size() - 1) + " other ways and no mine standing",
                noStackNoRow && ironMine.hasRecipeChoice() && uses.stream().noneMatch(u -> u.template() == ironMine));

        // Every recipe's discovery is the timeline's entry of its code, or none.
        boolean from = true;
        for (ResearchView.RecipeUse use : uses) from &= use.from() == null ? ResearchView.timeline(town).stream().noneMatch(d -> d.entry().code().equals(use.recipe().getSince()))
                : use.from().entry().code().equals(use.recipe().getSince());
        check("a listed recipe carries the discovery of its code where that is on the timeline, and none where it is a year (no shipped non-default recipe is dated by a code yet)", from);
    }

    /* ==================== 8. where a building came from ==================== */

    static void sectionEight(Game town) {
        System.out.println("\n--- 8. origin and the NEW mark: found here or from abroad, a year is its January, new for NEW_MONTHS - 1 months and not at NEW_MONTHS ---");
        int month = town.getMonth();
        BuildingsTemplate furnace = template(town, "Electric Arc Furnace");
        Entry pe1 = CAT.entry("PE1");
        int foundAt = town.getResearch().foundMonth("PE1");
        check("fixture: the furnace is opened by PE1, found here in month " + foundAt + " ahead of history's " + pe1.worldMonth() + "; the city is in month " + month,
                "PE1".equals(furnace.getSinceText()) && foundAt < pe1.worldMonth() && foundAt > ResearchView.FIRST_MONTH);
        ResearchView.Origin o = ResearchView.origin(town, furnace);
        check("a template opened by a code found here is HERE at the month found, with the entry and the years ahead of the world",
                o != null && o.source() == Source.HERE && o.month() == foundAt && o.entry() == pe1 && o.template() == furnace && o.yearsAhead() == (pe1.worldMonth() - foundAt) / 12.0);
        check("...and NEW, " + (month - o.month()) + " months after it, with NEW_MONTHS - 1 = " + (ResearchView.NEW_MONTHS - 1) + " months the last it is",
                month - o.month() == ResearchView.NEW_MONTHS - 1 && ResearchView.isNew(town, furnace) && ResearchView.newTemplates(town).contains(furnace));

        setFinds(town, Map.of("PE1", foundAt - 1));
        ResearchView.Origin old = ResearchView.origin(town, furnace);
        check("one month older, at NEW_MONTHS, it is not new - and it still has its origin: HERE in month " + (foundAt - 1) + ", " + (month - old.month()) + " months old",
                old != null && old.month() == foundAt - 1 && month - old.month() == ResearchView.NEW_MONTHS && !ResearchView.isNew(town, furnace) && !ResearchView.newTemplates(town).contains(furnace));

        setFinds(town, Map.of());
        ResearchView.Origin world = ResearchView.origin(town, furnace);
        check("found by no one, the world's month is the origin: ABROAD at history's month " + pe1.worldMonth() + ", the entry, 0 years ahead, " + (month - pe1.worldMonth()) + " months old and new",
                world != null && world.source() == Source.ABROAD && world.month() == pe1.worldMonth() && world.entry() == pe1 && world.yearsAhead() == 0
                        && month - world.month() < ResearchView.NEW_MONTHS && ResearchView.isNew(town, furnace));
        setFinds(town, Map.of("PE1", foundAt));

        // The newest list is the templates that are new, in the templates' order.
        List<BuildingsTemplate> expected = new ArrayList<>();
        for (BuildingsTemplate t : town.getBuildingManager().getTemplates()) if (ResearchView.isNew(town, t)) expected.add(t);
        check("newTemplates() is every template that isNew(), in the templates' order (" + expected.size() + ": the furnace among them)", ResearchView.newTemplates(town).equals(expected) && expected.contains(furnace));

        // Known at the founding, never known, a year not yet come.
        BuildingsTemplate house = template(town, "House"), quarry = template(town, "Quarry"), panel = template(town, "Panel Mill");
        check("a template known at the founding has no origin: an undated House, and a Quarry dated 1900, the founding's January",
                house.getSinceText().isEmpty() && ResearchView.origin(town, house) == null && "1900".equals(quarry.getSinceText()) && ResearchView.origin(town, quarry) == null
                        && !ResearchView.isNew(town, house) && !ResearchView.isNew(town, quarry));
        check("...nor one not known yet: the Large Cement Plant (PE4, history's month " + CAT.entry("PE4").worldMonth() + ") in month " + month + ", nor the Panel Mill (" + panel.getSinceText() + ")",
                ResearchView.origin(town, template(town, "Large Cement Plant")) == null && ResearchView.origin(town, panel) == null);

        ResearchCatalog three = threeMonths();
        Research r = new Research();
        BuildingsTemplate probe = new BuildingsTemplate("Probe", BuildingType.INDUSTRIAL);
        probe.setSince("T2");
        check("a code history has not reached is not an origin yet, and is on the month it does: T2 in month " + ResearchView.FIRST_MONTH + " none, in month " + (ResearchView.FIRST_MONTH + 1) + " ABROAD",
                ResearchView.origin(three, r, ResearchView.FIRST_MONTH, probe) == null && ResearchView.origin(three, r, ResearchView.FIRST_MONTH + 1, probe) != null
                        && ResearchView.origin(three, r, ResearchView.FIRST_MONTH + 1, probe).source() == Source.ABROAD);
        probe.setSince("T1");
        check("...a code history dates AT FIRST_MONTH (T1) was known at the founding: no origin in any month", ResearchView.origin(three, r, 1, probe) == null && ResearchView.origin(three, r, 99, probe) == null);
        probe.setSince("ZZ99");
        check("a code the catalogue lacks is never known, so never an origin", ResearchView.origin(three, r, 10_000, probe) == null && ResearchView.origin(CAT, r, 10_000, probe) == null);
        probe.setSince("");
        check("an undated row has none either", ResearchView.origin(three, r, 10_000, probe) == null);

        // A year: ABROAD at its January, from both sides of it.
        int january = PolicyDates.monthOf(1980, 1);
        ResearchView.Origin before = ResearchView.origin(CAT, new Research(), january - 1, panel);
        ResearchView.Origin on = ResearchView.origin(CAT, new Research(), january, panel);
        check("a since that is a year is ABROAD at its January: the Panel Mill (1980) none in month " + (january - 1) + ", in month " + january + " ABROAD, no entry, 0 years ahead",
                before == null && on != null && on.source() == Source.ABROAD && on.month() == january && on.entry() == null && on.yearsAhead() == 0);
        check("...and new for NEW_MONTHS - 1 months after it and not at NEW_MONTHS",
                ResearchView.isNew(on, january + ResearchView.NEW_MONTHS - 1) && !ResearchView.isNew(on, january + ResearchView.NEW_MONTHS) && !ResearchView.isNew((ResearchView.Origin) null, january));
        probe.setSince(1900);
        check("a year of the founding's January (1900) is known at the founding: none", ResearchView.origin(CAT, new Research(), 99, probe) == null);

        // The labs.
        boolean labs = true;
        for (Field f : Field.values()) labs &= ResearchView.isLab(town.labTemplate(f)) && town.labTemplate(f).getResearches() == f;
        check("the four labs are labs, and a school (the University), a house and nothing at all are not",
                labs && !ResearchView.isLab(template(town, "University")) && !ResearchView.isLab(house) && !ResearchView.isLab(null));
    }

    /* ==================== 9. the series ==================== */

    /** The history's last `n` figures of a series. */
    static double[] tail(HistorySave h, String name, int n) {
        double[] all = h.aligned(name);
        double[] out = new double[n];
        System.arraycopy(all, all.length - n, out, 0, n);
        return out;
    }

    static double round2(double v) { return Math.round(v * 100.0) / 100.0; }

    static double round4(double v) { return Math.round(v * 10000.0) / 10000.0; }

    /** The share fitted as the harness works it out from the stacks (not from ResearchView's rows), rounded as the history rounds it; NaN with no piece known on a stack that stands. */
    static double shareFromStacks(Game g) {
        double buildings = 0, fitted = 0;
        for (BuildingsStacks stack : g.getBuildingManager().getStacksView()) {
            if (stack.getQuantity() <= 0) continue;
            for (Equipment piece : stack.getBuilding().getEquipment()) {
                if (!piece.knownAt(g.getMonth(), g.getResearch())) continue;
                buildings += stack.getQuantity();
                fitted += stack.getQuantity() - Math.min(stack.getQuantity(), stack.getUnfitted(piece.getId()));
            }
        }
        return buildings > 0 ? round4(fitted / buildings) : Double.NaN;
    }

    static void sectionNine() throws Exception {
        System.out.println("\n--- 9. the series: the researchers, the spend and the share fitted, every month as the model says, through a save, the year book and a reform ---");
        // A village with a University and a Law School, played month by month as PrizeCheck.labCity() plays it to the month the model first wants an
        // Engineering lab, a Law grant set; then a lab, a prize, a month with the lab at work, the month of a find (the prize paid) and one after.
        Field lab = Field.ENGINEERING;
        Game g = DiscoveryCheck.cityWithSchools("research-view-series", true, true);
        Research r = g.getResearch();
        EconomyManager e = g.getEconomyManager();
        List<double[]> researchers = new ArrayList<>();
        List<Double> spend = new ArrayList<>(), share = new ArrayList<>();
        Runnable press = () -> {
            PrizeCheck.playHanded(g, 1);
            double[] now = new double[Field.values().length];
            for (Field f : Field.values()) now[f.ordinal()] = round2(r.researchers(f) + r.labResearchers(f));
            researchers.add(now);
            spend.add(round2(e.getResearchBill() + g.getPrizePaid()));
            share.add(shareFromStacks(g));
        };
        press.run();
        r.setGrant(Field.LAW, 3.5 * Research.postCost(Field.LAW, g.getPopulationManager().getWagesPerType().clone()));
        int upTo = PrizeCheck.monthWantingALab(lab, 1) - 1;
        while (g.getMonth() < upTo) press.run();
        BuildingsTemplate labTemplate = g.labTemplate(lab);
        quietly(() -> g.buildStack(labTemplate, 1, true));
        r.setPrize(lab, PrizeCheck.justWantsALab(g, lab));
        press.run();
        double labsWork = r.labResearchers(lab), ownWork = r.researchers(lab);
        int labMonth = researchers.size() - 1;
        Entry target = r.labTarget(lab, g.getMonth() + 1, g.getPopulationManager().getWagesPerType().clone());
        if (target != null) PrizeCheck.pileJustUnder(g, target);
        press.run();
        double prizePaid = g.getPrizePaid();
        int findMonth = researchers.size() - 1;
        double billInFind = round2(e.getResearchBill());
        press.run();
        int months = researchers.size();
        HistorySave h = g.getHistorySave();
        check("fixture: " + months + " months played; the Law grant bought posts and a bill (" + billInFind + " in the find's month); a lab worked (" + labsWork + " researchers of its own, the city's " + ownWork + "); a find paid a prize (" + prizePaid + "), and none was paid the month after",
                r.posts(Field.LAW) > 0 && billInFind > 0 && labsWork > 0 && target != null && prizePaid > 0 && g.getPrizePaid() == 0 && h.months() >= months,
                "target " + target + ", posts " + r.posts(Field.LAW));

        boolean lengths = true, researchersRight = true;
        for (Field f : Field.values()) {
            String key = HistorySave.researchersKey(f);
            lengths &= h.seriesByName().get(key) != null && h.seriesByName().get(key).size() == h.months();
            double[] got = tail(h, key, months);
            for (int i = 0; i < months; i++) researchersRight &= Math.abs(got[i] - researchers.get(i)[f.ordinal()]) < 1e-9;
        }
        check("each field's researchers are recorded every month, the same length as the axis (" + h.months() + "), and are the city's own and the labs' as the model said, in each of the " + months + " months played",
                lengths && researchersRight);
        double[] gotEngineering = tail(h, HistorySave.researchersKey(lab), months);
        check("...the lab's are in them: in the month the lab first worked, engineering's " + gotEngineering[labMonth] + " is the city's own " + ownWork + " and the lab's " + labsWork,
                Math.abs(gotEngineering[labMonth] - round2(ownWork + labsWork)) < 1e-9 && gotEngineering[labMonth] > round2(ownWork));
        double[] gotSpend = tail(h, HistorySave.RESEARCH_SPEND, months);
        boolean spendRight = h.seriesByName().get(HistorySave.RESEARCH_SPEND).size() == h.months();
        for (int i = 0; i < months; i++) spendRight &= Math.abs(gotSpend[i] - spend.get(i)) < 1e-9;
        check("researchSpend is every month's own figure, the month's research bill and prize paid, in each of the " + months + " months played", spendRight);
        check("...in the find's month it is the bill and the prize together, " + gotSpend[findMonth] + ", and the month after the bill alone, " + gotSpend[months - 1],
                Math.abs(gotSpend[findMonth] - round2(billInFind + prizePaid)) < 1e-9 && gotSpend[findMonth] > billInFind && Math.abs(gotSpend[months - 1] - round2(e.getResearchBill())) < 1e-9);
        double[] gotShare = tail(h, HistorySave.EQUIPMENT_FITTED, months);
        boolean shareRight = h.seriesByName().get(HistorySave.EQUIPMENT_FITTED).size() == h.months();
        for (int i = 0; i < months; i++) shareRight &= Math.abs(gotShare[i] - share.get(i)) < 1e-9 && gotShare[i] >= 0 && gotShare[i] <= 1;
        check("equipmentFitted is the share of the equipment fitted, between 0 and 1, in each month, as the stacks say (the last " + gotShare[months - 1] + ")", shareRight && gotShare[months - 1] > 0);

        // Through a save and a load, and a month on.
        EquipmentCheck.save(g, "research view series");
        Game back = new Game(g.getGameFiles());
        quietly(() -> back.loadGameSave(1));
        HistorySave loaded = back.getHistorySave();
        check("a load restores the history whole: the three kinds of series, the same length and the same figures",
                back.getLoadFailure() == null && loaded.months() == h.months() && sameSeries(h, loaded));
        quietly(() -> loaded.recordMonth(back));
        check("...and a month recorded on it follows: every series is still the axis' length",
                loaded.months() == h.months() + 1 && loaded.seriesByName().get(HistorySave.RESEARCH_SPEND).size() == loaded.months()
                        && loaded.seriesByName().get(HistorySave.EQUIPMENT_FITTED).size() == loaded.months()
                        && loaded.seriesByName().get(HistorySave.researchersKey(Field.LAW)).size() == loaded.months());

        // The year book rules each.
        check("the year book rules each: the researchers are levels, the spend a flow, the share fitted a rate, each with a note, and nothing in the history is left without a rule",
                levelsOf(g) && YearBook.kindOf(HistorySave.RESEARCH_SPEND) == YearBook.Kind.FLOW && YearBook.kindOf(HistorySave.EQUIPMENT_FITTED) == YearBook.Kind.RATE
                        && YearBook.noteOf(HistorySave.RESEARCH_SPEND) != null && YearBook.noteOf(HistorySave.EQUIPMENT_FITTED) != null && YearBook.unruled(h).isEmpty());

        // A reform scales the money.
        HistorySave probe = h.snapshot();
        probe.redenominate(0.5);
        boolean halved = true, kept = true;
        double[] before = h.aligned(HistorySave.RESEARCH_SPEND), after = probe.aligned(HistorySave.RESEARCH_SPEND);
        for (int i = 0; i < before.length; i++) halved &= Math.abs(after[i] - before[i] * 0.5) < 1e-9;
        for (Field f : Field.values()) kept &= java.util.Arrays.equals(h.aligned(HistorySave.researchersKey(f)), probe.aligned(HistorySave.researchersKey(f)));
        kept &= java.util.Arrays.equals(h.aligned(HistorySave.EQUIPMENT_FITTED), probe.aligned(HistorySave.EQUIPMENT_FITTED));
        check("a reform of the money halves the spend and leaves the researchers and the share fitted alone", halved && kept && before[before.length - 1] > 0);

        lateSeries();
    }

    /** Whether the three researchers' kinds are levels, with notes. */
    static boolean levelsOf(Game g) {
        boolean ok = true;
        for (Field f : Field.values()) ok &= YearBook.kindOf(HistorySave.researchersKey(f)) == YearBook.Kind.LEVEL && YearBook.noteOf(HistorySave.researchersKey(f)) != null;
        return ok;
    }

    /** Whether two histories hold the same figures in the research's series. */
    static boolean sameSeries(HistorySave a, HistorySave b) {
        List<String> names = new ArrayList<>(List.of(HistorySave.RESEARCH_SPEND, HistorySave.EQUIPMENT_FITTED));
        for (Field f : Field.values()) names.add(HistorySave.researchersKey(f));
        for (String name : names) {
            double[] x = a.aligned(name), y = b.aligned(name);
            if (x.length != y.length) return false;
            for (int i = 0; i < x.length; i++) {
                if (!(Double.isNaN(x[i]) && Double.isNaN(y[i])) && !(Math.abs(x[i] - y[i]) < 1e-9)) return false;
            }
        }
        return true;
    }

    /** A 1900 city of roads and houses only: no equipment stands until a bakery is built, then it does, then it is pulled down. */
    static void lateSeries() throws Exception {
        System.out.println("  (a city that has no equipment at first: the share fitted begins with the first equipment, and is 0 if all is pulled down)");
        Game g = new Game(GameFiles.scratch("research-view-late"));
        quietly(() -> {
            g.newGame(Founding.defaults().withUnfitted(true));
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 400_000_000L);
            BuildingManager b = g.getBuildingManager();
            g.buildStack(b.getTemplateByName("Gravel Road"), 2, true);
            g.buildStack(b.getTemplateByName("House"), 20, true);
        });
        check("fixture: no equipment stands, so the share fitted is not a number", ResearchView.equipment(g, null).isEmpty() && Double.isNaN(ResearchView.fittedShare(g)));
        play(g, 2);
        HistorySave h = g.getHistorySave();
        int blank = h.months();
        List<? extends Number> early = h.seriesByName().get(HistorySave.EQUIPMENT_FITTED);
        check("two months with nothing to count: the share fitted has not begun (no series), while the researchers and the spend have, month for month",
                early == null && h.seriesByName().get(HistorySave.RESEARCH_SPEND).size() == blank && h.seriesByName().get(HistorySave.researchersKey(Field.LAW)).size() == blank);
        quietly(() -> {
            g.buildStack(template(g, "Bakery"), 2, true);
            g.unfitTheFounding();
        });
        play(g, 3);
        double[] share = h.aligned(HistorySave.EQUIPMENT_FITTED);
        boolean blanks = true, numbers = true;
        for (int i = 0; i < blank; i++) blanks &= Double.isNaN(share[i]);
        for (int i = blank; i < share.length; i++) numbers &= !Double.isNaN(share[i]) && share[i] >= 0 && share[i] <= 1;
        check("a bakery built, it begins at the first month with equipment: NaN (not counting) in the " + blank + " months before, a share from then on",
                share.length == h.months() && h.seriesByName().get(HistorySave.EQUIPMENT_FITTED).size() == h.months() - blank && blanks && numbers);

        // Cut and kept: a replay's cut before it began drops it, and a cut after keeps it.
        HistorySave cutEarly = h.cutAt(h.getMonth().get(blank - 1));
        HistorySave cutLate = h.cutAt(h.getMonth().get(h.months() - 1));
        check("a cut at a month before the series began leaves no such series (not an empty one), the others kept; a cut at the end keeps it whole",
                !cutEarly.seriesByName().containsKey(HistorySave.EQUIPMENT_FITTED) && cutEarly.seriesByName().containsKey(HistorySave.RESEARCH_SPEND)
                        && cutLate.seriesByName().get(HistorySave.EQUIPMENT_FITTED).size() == h.months() - blank);

        // Through a save: the late series comes back late.
        EquipmentCheck.save(g, "research view late");
        Game back = new Game(g.getGameFiles());
        quietly(() -> back.loadGameSave(1));
        check("through a save it comes back as it began: the same months blank, the same figures", back.getLoadFailure() == null && sameSeries(h, back.getHistorySave()));

        // Pulled down: a month with no equipment after the series began records 0, not a gap.
        BuildingsStacks bakery = FittingCheck.stack(g, "Bakery");
        quietly(() -> bakery.removeQuantity(bakery.getQuantity()));
        boolean none = ResearchView.equipment(g, null).isEmpty();
        play(g, 1);
        double[] after = h.aligned(HistorySave.EQUIPMENT_FITTED);
        check("every bakery pulled down, the month after records 0 on the series that has begun (a gap cannot be kept: the series lines up with the axis' end)",
                none && Double.isNaN(ResearchView.fittedShare(g)) && after.length == h.months() && after[after.length - 1] == 0
                        && h.seriesByName().get(HistorySave.EQUIPMENT_FITTED).size() == h.months() - blank);
    }

    /* ==================== 10. researchTown() ==================== */

    static void sectionTen(Game town, long builtMillis) {
        System.out.println("\n--- 10. researchTown(): each of its guarantees ---");
        System.out.println("  researchTown() was built in " + builtMillis + " ms");
        check("founded the 1900 way, in month " + (1 + ResearchView.NEW_MONTHS + 1) + " (NEW_MONTHS + 1 months played)", town.foundedUnfitted() && town.getMonth() == 1 + ResearchView.NEW_MONTHS + 1);

        // (a)
        List<Discovery> t = ResearchView.timeline(town);
        check("(a) a discovery found HERE and at least two from ABROAD are on the timeline: " + ResearchView.count(t, Source.HERE) + " here, " + ResearchView.count(t, Source.ABROAD) + " from abroad",
                ResearchView.count(t, Source.HERE) >= 1 && ResearchView.count(t, Source.ABROAD) >= 2);
        check("...the find is PE1 at month 3, the world's are PE2 (month 6) and PE3 (month 13), in that order of months",
                t.size() == 3 && t.get(0).entry().code().equals("PE1") && t.get(0).month() == 3 && t.get(0).source() == Source.HERE
                        && t.get(1).entry().code().equals("PE2") && t.get(1).month() == CAT.entry("PE2").worldMonth() && t.get(1).source() == Source.ABROAD
                        && t.get(2).entry().code().equals("PE3") && t.get(2).month() == CAT.entry("PE3").worldMonth() && t.get(2).source() == Source.ABROAD);

        // (b)
        ResearchView.PieceRow row = null;
        for (ResearchView.PieceRow candidate : ResearchView.equipment(town, null)) {
            if (row == null && candidate.unfitted() > 0 && candidate.verdict().kind() != EquipmentFit.Kind.FITTED) row = candidate;
        }
        boolean inSector = false;
        if (row != null) for (ResearchView.PieceRow other : ResearchView.equipment(town, row.template().getSector())) inSector |= other.template() == row.template() && other.piece() == row.piece();
        check("(b) a stack stands with a known piece some of its buildings lack, and its sector's equipment lists it with a verdict that is not FITTED: "
                        + (row == null ? "none" : row.template().getName() + " " + row.piece().getId() + " " + row.unfitted() + " of " + row.buildings() + ", " + row.verdict().kind()),
                row != null && inSector && row.piece().knownAt(town.getMonth(), town.getResearch()));

        // (c)
        List<BuildingsTemplate> fresh = ResearchView.newTemplates(town);
        boolean allNew = !fresh.isEmpty();
        for (BuildingsTemplate n : fresh) {
            ResearchView.Origin o = ResearchView.origin(town, n);
            allNew &= o != null && ResearchView.isNew(town, n) && town.getMonth() - o.month() < ResearchView.NEW_MONTHS;
        }
        check("(c) a template is NEW, its origin within NEW_MONTHS of the month: " + fresh.stream().map(BuildingsTemplate::getName).toList(), allNew && fresh.contains(template(town, "Electric Arc Furnace")));

        // (d)
        List<ResearchView.RecipeUse> uses = ResearchView.recipesInUse(town);
        boolean farmsRun = false, millsRun = false;
        for (ResearchView.RecipeUse use : uses) {
            farmsRun |= use.template().getName().equals("Mixed Farm") && use.running() > 0;
            millsRun |= use.template().getName().equals("Pulp Mill") && use.running() > 0;
        }
        check("(d) a recipe is in use on a template with a recipe piece (the farms' horses) and on one with none (the Pulp Mill's): " + uses.size() + " rows", farmsRun && millsRun);
    }
}
