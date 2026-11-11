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
 *
 * Policy > Research's Overview and Funding pages (R3b, slice A: the reads their screen draws, which sections 11 to 15 hold to the model's own, in a played
 * 1900 town with two Universities, an Institute of Technology and a Medical School, and grants that bought posts):
 *
 *  11. THE OVERVIEW. Each field's row is Research's reads: its researchers and the labs', the idle among them, the problems in reach and what they touch, what it
 *      found here and had come from abroad, the schools that host it; the month's bill and the prizes paid; the researchers to each 1,000 at work; the words.
 *  12. THE FUNDING CARD. Each field's grant, prize, posts (the grant's and the pot's), room, the grant that fills it, the researchers, the bill and the part
 *      of the grant unspent are the model's; the labs standing and on site; the least prize that brings a lab, from both sides; what a staged grant buys.
 *  13. THE PREVIEW THROUGH THE GAME. The as-is and staged previews are ResearchPreview's over the model's own researchers, cached, and a staged dial equal
 *      to the one in force is the answer already kept.
 *  14. R5'S GATE REVIEW. The found-here and abroad counts laid on History's months, what is still to come, and the bars' shares.
 *  15. A CITY FOUNDED THE DEFAULT WAY. Nothing in reach, nothing to find, no lab to come, whatever the catalogue's dates say of the month.
 *
 * The screens' own reads (R3b, slice B: what the Overview's four figures, the Funding dials, a field card's rows and a school's research line are drawn from, so that
 * the screens add up nothing):
 *
 *  16. THE FIGURES AND THE DIALS. The researchers at work and the problems in reach added up; the words of the problems by field; the room in whole posts; a grant
 *      dial in steps of a post, past the room and the grant in force; a prize dial to a round number past 1,000 researcher-years, twice the least prize that
 *      brings a lab and the prize in force; a lab's posts.
 *  17. THE CARD. Nothing staged is one figure on both sides; a staged grant is atGrant's posts, researchers, bill and unspent, the preview the staged one's; a
 *      grant past the room buys the room and says it is full; a staged prize brings the labs the staged preview wants, at a lab's posts each.
 *  18. THE SCHOOLS AND THE BUDGET. The kinds of school stand up the game's own staff and room; a kind has its share of each field's posts; a kind that hosts none, or of
 *      which none stands, carries nothing; THE BUDGET moves by the staged grants' posts and not by a prize.
 *  19. A LAB STANDING. The card counts the lab and its researchers with the field's, and the same lab is on both sides of a card that moves a grant.
 *  20. A DEFAULT CITY'S FACULTY (R3b's gate review). A city founded the default way with a University and no grant: its researchers are the faculty's
 *      own and no post is held (the Overview's posts), and the month's counts survive a save and a load though the research has nothing of its own.
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

    static void near(String label, double actual, double expected, double tolerance) {
        check(label, Math.abs(actual - expected) <= tolerance, String.format("%.9g against %.9g (tolerance %g)", actual, expected, tolerance));
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
        Game funded = fundedTown();
        sectionEleven(funded);
        sectionTwelve(funded);
        sectionThirteen(funded);
        sectionFourteen(funded);
        sectionFifteen();
        sectionSixteen(funded);
        sectionSeventeen(funded);
        sectionEighteen(funded);
        sectionNineteen(funded);
        sectionTwenty();
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
        /*
         * WG2, {next}, 2026-11-10: A HARNESS ROUNDING SLIP THE NEW PATH EXPOSED. The claim below compares the series' find-month figure with the bill and the
         * prize together. The series rounds that sum once (spend, in press); the expected value rounded the already rounded bill (billInFind) plus the prize,
         * which is the same only when the prize is a whole number of cents. It was on main's path; on WG2's the find month's bill is 0.5533857961519341 and
         * the prize 8.724307968408754, so round2(0.55 + prize) read 9.27 where the sum, 9.2777, rounds to 9.28, the series' figure. The expected value now
         * rounds once, from the bill as it stood in the find's month (billExactInFind) and the prize.
         */
        double billExactInFind = e.getResearchBill();
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
                Math.abs(gotSpend[findMonth] - round2(billExactInFind + prizePaid)) < 1e-9 && gotSpend[findMonth] > billInFind && Math.abs(gotSpend[months - 1] - round2(e.getResearchBill())) < 1e-9);
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

    /* =====================================================================
       R3b: Policy > Research's Overview and Funding pages (sections 11 to 15)
       ===================================================================== */

    /**
     * researchTown() with schools and grants: two Universities, an Institute of Technology and a Medical School stand, the hands are handed, and grants in
     * engineering (30.5 posts' worth), medicine (12.5) and business (4.5) have bought posts that the labour market has filled in part, played three months.
     * Law has no grant and no school of its own. A fresh town, so the sections of 1 to 10 keep their fixture as it is.
     */
    static Game fundedTown() throws Exception {
        Game g = researchTown();
        quietly(() -> {
            g.buildStack(template(g, "University"), 2, true);
            g.buildStack(template(g, "Institute of Technology"), 1, true);
            g.buildStack(template(g, "Medical School"), 1, true);
            MaterialsCheck.handWorkers(g);
        });
        Research r = g.getResearch();
        double[] wages = g.getPopulationManager().getWagesPerType().clone();
        r.setGrant(Field.ENGINEERING, 30.5 * Research.postCost(Field.ENGINEERING, wages));
        r.setGrant(Field.MEDICINE, 12.5 * Research.postCost(Field.MEDICINE, wages));
        r.setGrant(Field.BUSINESS, 4.5 * Research.postCost(Field.BUSINESS, wages));
        for (int i = 0; i < 3; i++) {
            quietly(() -> MaterialsCheck.handWorkers(g));
            play(g, 1);
        }
        return g;
    }

    /** The distinct things a list of open problems touch, in order, as the harness counts them (a set of the entries' own texts). */
    static List<String> touchedBy(List<Entry> open) {
        java.util.LinkedHashSet<String> out = new java.util.LinkedHashSet<>();
        for (Entry e : open) if (!e.touches().trim().isEmpty()) out.add(e.touches().trim());
        return new ArrayList<>(out);
    }

    /** Whether two previews are exactly the same answer. */
    static boolean sameResult(ResearchPreview.Result a, ResearchPreview.Result b) {
        boolean same = a.draws() == b.draws() && a.total() == b.total() && a.totalSe() == b.totalSe();
        for (Field f : Field.values()) {
            same &= a.finds(f) == b.finds(f) && a.findsSe(f) == b.findsSe(f) && a.chance(f) == b.chance(f) && a.purse(f) == b.purse(f) && a.labsFirstMonth(f) == b.labsFirstMonth(f);
        }
        return same;
    }

    /* ==================== 11. the Overview ==================== */

    static void sectionEleven(Game g) {
        System.out.println("\n--- 11. the Overview: each field's row is the model's own reads, and the words say them ---");
        Research r = g.getResearch();
        int month = g.getMonth();
        check("fixture: a 1900 city with schools and grants that bought posts in engineering, medicine and business, none in law, and the posts struck at a fill",
                g.foundedUnfitted() && r.posts(Field.ENGINEERING) > 0 && r.posts(Field.MEDICINE) > 0 && r.posts(Field.BUSINESS) > 0 && r.posts(Field.LAW) == 0 && g.getResearchFill() != null,
                r.posts(Field.ENGINEERING) + "/" + r.posts(Field.MEDICINE) + "/" + r.posts(Field.BUSINESS) + "/" + r.posts(Field.LAW));
        ResearchView.Overview o = ResearchView.overview(g);
        List<Discovery> timeline = ResearchView.timeline(g);
        check("a row for each field, in the enum's order", o.fields().size() == Field.values().length && o.fields().get(0).field() == Field.ENGINEERING && o.fields().get(3).field() == Field.LAW);

        String off = "";
        boolean someInReach = false, someNone = false;
        for (Field f : Field.values()) {
            ResearchView.FieldReach row = o.fields().get(f.ordinal());
            List<Entry> open = r.openProblems(f, month);
            someInReach |= !open.isEmpty();
            someNone |= open.isEmpty();
            if (row.field() != f) off += f + " is not its row; ";
            if (row.researchers() != r.researchers(f)) off += f + " researchers " + row.researchers() + " not " + r.researchers(f) + "; ";
            if (row.labResearchers() != r.labResearchers(f)) off += f + " lab researchers; ";
            if (row.idle() != r.idle(f) + r.labsIdle(f)) off += f + " idle " + row.idle() + " not " + (r.idle(f) + r.labsIdle(f)) + "; ";
            if (row.inReach() != open.size() || row.inReach() != r.open(f)) off += f + " in reach " + row.inReach() + " of " + open.size() + "/" + r.open(f) + "; ";
            if (!row.touches().equals(touchedBy(open))) off += f + " touches " + row.touches() + " not " + touchedBy(open) + "; ";
            String words = open.isEmpty() ? "nothing in reach" : open.size() + " in reach: " + String.join("; ", touchedBy(open));
            if (!row.reachWords().equals(words)) off += f + " says \"" + row.reachWords() + "\" not \"" + words + "\"; ";
            int here = 0, abroad = 0;
            for (Discovery d : timeline) {
                if (d.field() != f) continue;
                if (d.source() == Source.HERE) here++;
                else abroad++;
            }
            if (row.foundHere() != here || row.fromAbroad() != abroad) off += f + " found " + row.foundHere() + "/" + row.fromAbroad() + " not " + here + "/" + abroad + "; ";
            if (row.total() != r.researchers(f) + r.labResearchers(f) || row.working() != Math.max(0, row.total() - row.idle())) off += f + " total or working; ";
        }
        check("each field's researchers, lab researchers, idle (the city's and the labs'), problems in reach, what they touch, and finds here and from abroad are the model's reads",
                off.isEmpty(), off);
        check("fixture: some field has problems in reach and some has none, so \"nothing in reach\" and \"N in reach\" are both on the page", someInReach && someNone);
        check("the page's totals: found here and from abroad are the timeline's, and the rows add up",
                o.foundHere() == ResearchView.count(timeline, Source.HERE) && o.fromAbroad() == ResearchView.count(timeline, Source.ABROAD)
                        && o.foundHere() == o.fields().stream().mapToInt(ResearchView.FieldReach::foundHere).sum()
                        && o.fromAbroad() == o.fields().stream().mapToInt(ResearchView.FieldReach::fromAbroad).sum());

        double people = 0, idle = 0;
        for (Field f : Field.values()) {
            people += r.researchers(f) + r.labResearchers(f);
            idle += r.idle(f) + r.labsIdle(f);
        }
        near("the researchers of all the fields are the model's (the city's and the labs')", o.researchers(), people, 1e-9);
        near("...and the idle among them", o.idle(), idle, 1e-9);
        check("...the researchers at work are above nothing (the faculty and the filled posts)", people > 0, "" + people);
        near("the money a month: the research bill is the economy's", o.researchBill(), g.getEconomyManager().getResearchBill(), 1e-9);
        check("...and is above nothing, the grants having bought posts the labour market filled", o.researchBill() > 0, "" + o.researchBill());
        near("...the prizes paid the labs are Game.getPrizePaid() (none: no prize is set)", o.prizesPaid(), g.getPrizePaid(), 0);
        long workers = g.getPopulationManager().getJobsFilled();
        check("researchers to each 1,000 at work: the researchers over the jobs filled, times 1,000 (" + o.perThousandWorkers() + " from " + workers + " jobs)",
                workers > 0 && Math.abs(o.perThousandWorkers() - 1000 * people / workers) <= 1e-9 * o.perThousandWorkers() && o.perThousandWorkers() > 0);
        check("...and none where nobody works, whatever the researchers", Double.isNaN(ResearchView.perThousandWorkers(5, 0)) && ResearchView.perThousandWorkers(3.6, 1000) == 3.6
                && ResearchView.perThousandWorkers(0, 1000) == 0);

        // The schools that host each field, by their names and counts.
        check("engineering is hosted by an Institute of Technology and two Universities: \"" + o.fields().get(0).schoolsWords() + "\"",
                o.fields().get(0).schoolsWords().equals("1 Institute of Technology, 2 Universities")
                        && o.fields().get(0).schools().equals(List.of(new ResearchView.SchoolCount("Institute of Technology", 1), new ResearchView.SchoolCount("University", 2))));
        check("medicine by a Medical School and the Universities, business and law by the Universities alone (a school of the field first, the Universities after)",
                o.fields().get(1).schoolsWords().equals("1 Medical School, 2 Universities") && o.fields().get(2).schoolsWords().equals("2 Universities")
                        && o.fields().get(3).schoolsWords().equals("2 Universities"), o.fields().get(1).schoolsWords() + " / " + o.fields().get(2).schoolsWords());
        int hosted = 0;
        for (BuildingsStacks s : g.getBuildingManager().getStacksView()) {
            if (s.getQuantity() > 0 && s.getBuilding().getTeaches() != null && Research.staffByField(s.getBuilding().getTeaches(), null).containsKey(Field.ENGINEERING)) hosted += s.getQuantity();
        }
        check("...the engineering count is every standing school the model's staffByField() gives it a share of: " + hosted,
                hosted == o.fields().get(0).schools().stream().mapToInt(ResearchView.SchoolCount::count).sum());

        // The words, on their own.
        check("\"nothing in reach\" for none, \"1 in reach: rail\" for one, \"2 in reach: a; b\" for two, the count alone where the texts are blank",
                ResearchView.reachWords(0, List.of()).equals("nothing in reach") && ResearchView.reachWords(0, List.of("rail")).equals("nothing in reach")
                        && ResearchView.reachWords(1, List.of("rail")).equals("1 in reach: rail") && ResearchView.reachWords(2, List.of("a", "b")).equals("2 in reach: a; b")
                        && ResearchView.reachWords(3, List.of()).equals("3 in reach"));
        check("the schools' words: none is \"no school\", one is singular, many plural (\"Universities\", \"Institutes of Technology\", \"Law Schools\")",
                ResearchView.schoolsWords(List.of()).equals("no school") && ResearchView.schoolsWords(null).equals("no school")
                        && ResearchView.schoolsWords(List.of(new ResearchView.SchoolCount("Law School", 1))).equals("1 Law School")
                        && ResearchView.schoolsWords(List.of(new ResearchView.SchoolCount("Law School", 2))).equals("2 Law Schools")
                        && ResearchView.schoolsWords(List.of(new ResearchView.SchoolCount("Institute of Technology", 3), new ResearchView.SchoolCount("University", 2)))
                                .equals("3 Institutes of Technology, 2 Universities"));
    }

    /* ==================== 12. the Funding card ==================== */

    static void sectionTwelve(Game g) {
        System.out.println("\n--- 12. the Funding card: grant, prize, posts, room, researchers, bill, unspent grant, labs and the least prize that brings one, at the grant and at a staged one ---");
        Research r = g.getResearch();
        int month = g.getMonth();
        double[] wages = g.getPopulationManager().getWagesPerType().clone(), fill = g.getResearchFill();
        EnumMap<Field, Double> room = DiscoveryCheck.roomOf(g);
        String off = "";
        double bills = 0;
        boolean someUnspent = false;
        for (Field f : Field.values()) {
            ResearchView.Funding fu = ResearchView.funding(g, f);
            double cost = Research.postCost(f, wages);
            if (fu.field() != f || fu.grant() != r.grant(f) || fu.prize() != r.prize(f)) off += f + " grant or prize; ";
            if (fu.posts() != r.posts(f) || fu.grantPosts() != r.grantPosts(f) || fu.potPosts() != 0) off += f + " posts " + fu.posts() + "/" + fu.grantPosts() + "/" + fu.potPosts() + "; ";
            if (Math.abs(fu.room() - room.getOrDefault(f, 0.0)) > 1e-9) off += f + " room " + fu.room() + " not " + room.getOrDefault(f, 0.0) + "; ";
            if (fu.postCost() != cost || Math.abs(fu.grantToFillRoom() - room.getOrDefault(f, 0.0) * cost) > 1e-6) off += f + " post cost or the grant that fills the room; ";
            double filled = Research.filledResearchers(f, r.posts(f), fill);
            if (fu.postResearchers() != filled || Math.abs(fu.researchers() - r.researchers(f)) > 1e-9 || Math.abs(fu.faculty() - (r.researchers(f) - filled)) > 1e-9) {
                off += f + " researchers " + fu.researchers() + " (faculty " + fu.faculty() + " and posts " + fu.postResearchers() + ") against the model's " + r.researchers(f) + "; ";
            }
            double bill = Research.bill(f, r.posts(f), wages, fill);
            if (fu.bill() != bill) off += f + " bill; ";
            if (Math.abs(fu.unspent() - Math.max(0, r.grant(f) - bill)) > 1e-9) off += f + " unspent " + fu.unspent() + " not " + Math.max(0, r.grant(f) - bill) + "; ";
            bills += fu.bill();
            someUnspent |= fu.unspent() > 0;
            if (fu.labsStanding() != g.labsStanding(f) || fu.labsOnSite() != g.labsOnSite(f)) off += f + " labs; ";
        }
        check("each field's card is the model's: grant, prize, posts struck and the grant's own (no pot here), room, post cost, the grant that fills the room, researchers "
                + "(the faculty and the filled posts), the bill and the part of the grant unspent", off.isEmpty(), off);
        near("the cards' bills add to the economy's research bill: the whole month's bill is the posts'", bills, g.getEconomyManager().getResearchBill(), 1e-9 * bills);
        check("some of a grant is unspent: a grant of half a post over its whole posts leaves most of that half unspent, never more than the grant",
                someUnspent && ResearchView.funding(g, Field.ENGINEERING).unspent() > 0.4 * ResearchView.funding(g, Field.ENGINEERING).postCost()
                        && ResearchView.funding(g, Field.ENGINEERING).unspent() < ResearchView.funding(g, Field.ENGINEERING).grant());
        check("law has no grant and the card says so: no posts, nothing unspent, and the room its Universities give it",
                ResearchView.funding(g, Field.LAW).grant() == 0 && ResearchView.funding(g, Field.LAW).posts() == 0 && ResearchView.funding(g, Field.LAW).unspent() == 0
                        && ResearchView.funding(g, Field.LAW).room() > 0);

        // The pot: posts above the grant's are the pot's.
        Research potted = ResearchPreviewCheck.copyOf(r);
        potted.restorePrizes(null, Map.of(Field.MEDICINE.name(), 3.5 * Research.postCost(Field.MEDICINE, wages)), null, null, null);
        potted.strikePosts(DiscoveryCheck.roomOf(g), wages);
        ResearchView.Pieces pieces = new ResearchView.Pieces(potted, month, true, wages, fill, g.researchStaff(null), g.researchStaff(fill));
        ResearchView.Funding withPot = ResearchView.funding(pieces, Field.MEDICINE, 0, 0);
        check("a field whose pot bought posts beyond the grant says so: the posts are the grant's and the pot's, the potPosts the difference, and the unspent grant is the grant less the grant's own posts' bill",
                withPot.potPosts() > 0 && withPot.posts() == potted.posts(Field.MEDICINE) && withPot.grantPosts() == potted.grantPosts(Field.MEDICINE)
                        && withPot.posts() == withPot.grantPosts() + withPot.potPosts()
                        && Math.abs(withPot.unspent() - Math.max(0, potted.grant(Field.MEDICINE) - Research.bill(Field.MEDICINE, potted.grantPosts(Field.MEDICINE), wages, fill))) < 1e-9
                        && withPot.bill() > Research.bill(Field.MEDICINE, potted.grantPosts(Field.MEDICINE), wages, fill),
                withPot.posts() + "/" + withPot.grantPosts());

        // The least prize that brings a lab: from both sides, on the model's own chase.
        int finite = 0, none = 0;
        String edge = "";
        for (Field f : Field.values()) {
            double t = ResearchView.funding(g, f).labThreshold();
            boolean couldFinish = false;
            for (Entry e : r.openProblems(f, month)) couldFinish |= Research.beforeTheWorld(e, r.pile(e.code()), month);
            if (Double.isNaN(t) == couldFinish) edge += f + " threshold " + t + " but " + (couldFinish ? "a problem could be finished" : "none could") + "; ";
            if (Double.isNaN(t)) {
                none++;
                continue;
            }
            finite++;
            Research probe = ResearchPreviewCheck.copyOf(r);
            probe.setPrize(f, t);
            boolean at = !probe.chase(f, month, wages).isEmpty();
            probe.setPrize(f, Math.nextDown(t));
            boolean below = probe.chase(f, month, wages).isEmpty();
            if (!at || !below) edge += f + " threshold " + t + ": the chase at it " + (at ? "is" : "is not") + " there and just under it " + (below ? "is not" : "is") + "; ";
        }
        System.out.println("         the least prize that brings a lab: " + java.util.Arrays.stream(Field.values()).map(f -> f + " " + ResearchView.funding(g, f).labThreshold()).toList());
        check("the least prize that brings a lab to a field: at it the field's chase has a problem, a unit under it none, and none where no open problem could be finished before the world",
                edge.isEmpty(), edge);
        check("fixture: some field has such a prize and some has none (" + finite + " and " + none + ")", finite > 0 && none > 0);
        check("no wages, no cost of a post, no threshold", Double.isNaN(ResearchView.labThreshold(r, month, Field.ENGINEERING, null)));

        // A staged grant.
        String staged = "";
        for (Field f : Field.values()) {
            ResearchView.Funding fu = ResearchView.funding(g, f);
            double cost = Research.postCost(f, wages), faculty = r.researchers(f) - Research.filledResearchers(f, r.posts(f), fill);
            ResearchView.AtGrant same = ResearchView.atGrant(g, f, r.grant(f));
            if (same.posts() != r.posts(f) || same.grantPosts() != r.grantPosts(f) || Math.abs(same.researchers() - r.researchers(f)) > 1e-9 || same.bill() != fu.bill()) {
                staged += f + " the grant in force buys " + same.posts() + " posts, " + same.researchers() + " researchers; ";
            }
            for (double grant : new double[] { 0, 2 * r.grant(f) + 3.3 * cost, 1000 * (r.grant(f) + cost) }) {
                ResearchView.AtGrant at = ResearchView.atGrant(g, f, grant);
                int posts = Research.posts(grant + r.pot(f), cost, fu.room()), own = Research.posts(grant, cost, fu.room());
                double filled = Research.filledResearchers(f, posts, fill);
                if (at.grant() != grant || at.posts() != posts || at.grantPosts() != own || at.postResearchers() != filled || Math.abs(at.researchers() - (faculty + filled)) > 1e-9
                        || at.bill() != Research.bill(f, posts, wages, fill)) {
                    staged += f + " at " + grant + ": " + at + "; ";
                }
            }
            if (ResearchView.atGrant(g, f, 1000 * (r.grant(f) + cost)).posts() != (int) Math.floor(fu.room())) staged += f + " a grant past the room does not stop at the room; ";
            if (ResearchView.atGrant(g, f, 0).posts() != 0 || Math.abs(ResearchView.atGrant(g, f, 0).researchers() - faculty) > 1e-9 || ResearchView.atGrant(g, f, 0).bill() != 0) staged += f + " no grant; ";
        }
        check("a staged grant buys the posts Research.posts() says within the room (none for none, the room's whole posts for a grant past it), gives the faculty and the filled posts' "
                + "researchers at the fill, and costs the posts' bill; the grant in force buys the posts struck", staged.isEmpty(), staged);

        // The labs standing and on site.
        BuildingsTemplate lab = g.labTemplate(Field.MEDICINE);
        check("fixture: the catalogue has a Research Lab for medicine", lab != null);
        if (lab != null) {
            quietly(() -> g.buildStack(lab, 1, true));
            ResearchView.Funding medicine = ResearchView.funding(g, Field.MEDICINE);
            check("a lab built is a lab standing in its field's card, and only there: " + medicine.labsStanding() + " standing, " + medicine.labsOnSite() + " on site",
                    medicine.labsStanding() == g.labsStanding(Field.MEDICINE) && medicine.labsStanding() >= 1 && medicine.labsOnSite() == g.labsOnSite(Field.MEDICINE)
                            && ResearchView.funding(g, Field.ENGINEERING).labsStanding() == 0);
        }
    }

    /* ==================== 13. the preview through the game ==================== */

    /** The preview the way the model is asked it for the city as it stands, with some dials replaced: the maps the harness builds from the model's own reads. */
    static ResearchPreview.Result modelsPreview(Game g, Map<Field, Double> researchers, Map<Field, Double> prize) {
        Research r = g.getResearch();
        double[] fill = g.getResearchFill();
        EnumMap<Field, Double> people = new EnumMap<>(Field.class), prizes = new EnumMap<>(Field.class), labFill = new EnumMap<>(Field.class);
        for (Field f : Field.values()) {
            people.put(f, researchers.containsKey(f) ? researchers.get(f) : r.researchers(f));
            prizes.put(f, prize.containsKey(f) ? prize.get(f) : r.prize(f));
            labFill.put(f, Research.filledResearchers(f, Research.LAB_POSTS, fill) / Research.LAB_POSTS);
        }
        return ResearchPreview.preview(r, g.getMonth(), people, prizes, g.getPopulationManager().getWagesPerType(), labFill, ResearchPreview.DRAWS, ResearchPreview.SEED);
    }

    static void sectionThirteen(Game g) {
        System.out.println("\n--- 13. the preview through the game: the model's researchers, ResearchPreview's draws, cached, and a staged dial equal to the one in force is the answer kept ---");
        Research r = g.getResearch();
        double[] wages = g.getPopulationManager().getWagesPerType().clone();
        int runs = ResearchPreview.runs();
        ResearchPreview.Result asIs = ResearchView.preview(g, ResearchView.Staged.none());
        check("the first read of the preview runs it once", ResearchPreview.runs() == runs + 1, "" + (ResearchPreview.runs() - runs));
        check("the as-is preview is ResearchPreview's over the researchers the model has now and the prizes set, from ResearchPreview.DRAWS draws of ResearchPreview.SEED",
                asIs.draws() == ResearchPreview.DRAWS && sameResult(asIs, modelsPreview(g, Map.of(), Map.of())));
        check("...and finds something to say: the small problems of a young century are found in a good share of its draws (" + asIs.total() + " in all)", asIs.total() > 0);
        runs = ResearchPreview.runs();
        check("read again with nothing staged, or a null stage: the very same Result, no run", ResearchView.preview(g, ResearchView.Staged.none()) == asIs
                && ResearchView.preview(g, null) == asIs && ResearchPreview.runs() == runs, "" + (ResearchPreview.runs() - runs));

        // A staged grant: more engineering posts, and none.
        Field eng = Field.ENGINEERING;
        double cost = Research.postCost(eng, wages), grant = r.grant(eng) + 20 * cost;
        ResearchView.AtGrant more = ResearchView.atGrant(g, eng, grant), none = ResearchView.atGrant(g, eng, 0);
        ResearchPreview.Result expectedMore = modelsPreview(g, Map.of(eng, more.researchers()), Map.of());
        runs = ResearchPreview.runs();
        ResearchPreview.Result bigger = ResearchView.preview(g, ResearchView.Staged.none().withGrant(eng, grant));
        check("a staged grant runs the preview once, with that field's researchers the faculty and the filled posts it buys (" + more.posts() + " posts) and the others as they are",
                ResearchPreview.runs() == runs + 1 && sameResult(bigger, expectedMore), "" + (ResearchPreview.runs() - runs));
        check("...and the as-is is still kept: no run", ResearchView.preview(g, ResearchView.Staged.none()) == asIs && ResearchPreview.runs() == runs + 1);
        check("...and the same stage read again is kept too", ResearchView.preview(g, ResearchView.Staged.none().withGrant(eng, grant)) == bigger && ResearchPreview.runs() == runs + 1);
        ResearchPreview.Result expectedNone = modelsPreview(g, Map.of(eng, none.researchers()), Map.of());
        runs = ResearchPreview.runs();
        ResearchPreview.Result smaller = ResearchView.preview(g, ResearchView.Staged.none().withGrant(eng, 0));
        check("a staged grant of nothing leaves the faculty alone to research (" + String.format("%.1f", none.researchers()) + " against " + String.format("%.1f", r.researchers(eng)) + "), as the model has it",
                ResearchPreview.runs() == runs + 1 && sameResult(smaller, expectedNone) && none.researchers() < r.researchers(eng), "" + (ResearchPreview.runs() - runs));
        System.out.printf("         engineering finds, the same draws: none %.3f, as is %.3f, grant +20 posts %.3f%n", smaller.finds(eng), asIs.finds(eng), bigger.finds(eng));
        check("more researchers in a field find no less in it, fewer no more, from the same draws, and the three differ",
                bigger.finds(eng) >= asIs.finds(eng) && smaller.finds(eng) <= asIs.finds(eng) && bigger.finds(eng) > smaller.finds(eng));

        // A staged grant that is the grant in force changes nothing: the answer already kept.
        runs = ResearchPreview.runs();
        ResearchPreview.Result unchanged = ResearchView.preview(g, ResearchView.Staged.none().withGrant(Field.MEDICINE, r.grant(Field.MEDICINE)).withGrant(eng, r.grant(eng)));
        check("a staged grant equal to the grant in force is the as-is answer itself, kept, with no run", unchanged == asIs && ResearchPreview.runs() == runs, "" + (ResearchPreview.runs() - runs));

        // A staged prize.
        double threshold = ResearchView.funding(g, Field.ENGINEERING).labThreshold(), prize = Double.isNaN(threshold) ? 1e6 : 2 * threshold;
        ResearchPreview.Result expectedPrized = modelsPreview(g, Map.of(), Map.of(eng, prize));
        runs = ResearchPreview.runs();
        ResearchPreview.Result prized = ResearchView.preview(g, ResearchView.Staged.none().withPrize(eng, prize));
        check("a staged prize is the field's prize in the model's question, and runs once more",
                ResearchPreview.runs() == runs + 1 && sameResult(prized, expectedPrized), "" + (ResearchPreview.runs() - runs));
        check("...and a prize pays: the field's purse is the prize times its finds", Math.abs(prized.purse(Field.ENGINEERING) - prize * prized.finds(Field.ENGINEERING)) < 1e-6);

        // A new month runs again.
        runs = ResearchPreview.runs();
        play(g, 1);
        ResearchView.preview(g, ResearchView.Staged.none());
        check("a new month runs the preview again", ResearchPreview.runs() == runs + 1, "" + (ResearchPreview.runs() - runs));
    }

    /* ==================== 14. R5's gate review ==================== */

    static void sectionFourteen(Game g) {
        System.out.println("\n--- 14. R5's gate review: the found-here and abroad counts laid on History's months, what is still to come, the bars' shares ---");
        List<Discovery> timeline = ResearchView.timeline(g);
        HistorySave h = g.getHistorySave();
        List<Integer> axis = h.getMonth();
        double[][] laid = ResearchView.cumulativeOn(timeline, axis);
        check("fixture: the history has an axis of at least two months and the timeline has finds both ways", axis.size() >= 2 && ResearchView.count(timeline, Source.HERE) >= 1 && ResearchView.count(timeline, Source.ABROAD) >= 1,
                axis.size() + " months");
        String off = "";
        for (int i = 0; i < axis.size(); i++) {
            int here = 0, abroad = 0;
            for (Discovery d : timeline) {
                if (d.month() > axis.get(i)) continue;
                if (d.source() == Source.HERE) here++;
                else abroad++;
            }
            if (laid[0].length != axis.size() || laid[0][i] != here || laid[1][i] != abroad) off += "month " + axis.get(i) + ": " + laid[0][i] + "/" + laid[1][i] + " not " + here + "/" + abroad + "; ";
        }
        check("the counts laid on History's months are, at each month of its axis, the discoveries at or before it, here and from abroad (" + axis.size() + " months)", off.isEmpty(), off);
        check("...and the last of them is the timeline's counts", laid[0][axis.size() - 1] == ResearchView.count(timeline, Source.HERE) && laid[1][axis.size() - 1] == ResearchView.count(timeline, Source.ABROAD));
        double[][] gapped = ResearchView.cumulativeOn(timeline, List.of(3, 5, 6, 40));
        check("an axis with gaps is read at each of its own months: 3, 5, 6 and 40 (PE1 found in 3, PE2 abroad in 6, PE3 abroad in 13)",
                gapped[0].length == 4 && gapped[0][0] == 1 && gapped[0][3] == 1 && gapped[1][0] == 0 && gapped[1][1] == 0 && gapped[1][2] == 1 && gapped[1][3] >= 2,
                java.util.Arrays.toString(gapped[0]) + java.util.Arrays.toString(gapped[1]));
        double[][] empty = ResearchView.cumulativeOn(timeline, List.of()), one = ResearchView.cumulativeOn(timeline, List.of(10)), backwards = ResearchView.cumulativeOn(timeline, List.of(10, 4));
        check("an empty axis is empty, one month is one entry, and an axis that runs backwards is not a number throughout",
                empty[0].length == 0 && empty[1].length == 0 && one[0].length == 1 && one[0][0] == 1 && one[1][0] == 1 && backwards[0].length == 2
                        && Double.isNaN(backwards[0][0]) && Double.isNaN(backwards[1][1]));

        int all = CAT.entries().size(), toCome = 0;
        for (Entry e : CAT.entries()) if (!g.getResearch().known(e.code(), g.getMonth())) toCome++;
        check("what is still to come is the catalogue's entries the city does not yet know (" + toCome + " of " + all + "): all less those known at the founding, found here and come from abroad",
                ResearchView.stillToCome(g) == toCome && toCome > 0 && toCome < all, ResearchView.stillToCome(g) + " against " + toCome);
        check("...and the pure core subtracts and never goes below none", ResearchView.stillToCome(89, 5, 10, 20) == 54 && ResearchView.stillToCome(10, 8, 5, 5) == 0
                && ResearchView.stillToCome(10, 0, 0, 10) == 0 && ResearchView.stillToCome(10, 0, 0, 9) == 1);

        // The bars.
        boolean piecesOk = true;
        int rows = 0;
        for (ResearchView.PieceRow p : ResearchView.equipment(g, null)) {
            rows++;
            piecesOk &= p.share() == (p.buildings() > 0 ? p.fitted() / (double) p.buildings() : 0) && p.share() >= 0 && p.share() <= 1;
        }
        boolean recipesOk = true;
        int uses = 0;
        for (ResearchView.RecipeUse u : ResearchView.recipesInUse(g)) {
            uses++;
            recipesOk &= u.share() == (u.buildings() > 0 ? u.running() / (double) u.buildings() : 0) && u.share() >= 0 && u.share() <= 1;
        }
        check("the equipment bars' share is the fitted buildings over the buildings, 0 to 1, for each of the " + rows + " rows, and the recipes' the running over the buildings, for each of the " + uses,
                rows > 0 && uses > 0 && piecesOk && recipesOk);
        check("...and a stack of no buildings has the share 0 on either bar, and a whole one 1",
                new ResearchView.PieceRow(null, null, 0, 0, 0, null, null).share() == 0 && new ResearchView.RecipeUse(null, null, 0, 0, null).share() == 0
                        && new ResearchView.PieceRow(null, null, 4, 0, 0, null, null).share() == 1 && new ResearchView.RecipeUse(null, null, 4, 4, null).share() == 1
                        && new ResearchView.PieceRow(null, null, 4, 3, 0, null, null).share() == 0.25 && new ResearchView.RecipeUse(null, null, 1, 4, null).share() == 0.25);
    }

    /* ==================== 15. a city founded the default way ==================== */

    static void sectionFifteen() throws Exception {
        System.out.println("\n--- 15. a city founded the default way: nothing in reach, nothing to find, no lab to come, though the catalogue's dates say otherwise of the month ---");
        ensureQuiet();
        Game d = FittingCheck.village("research-view-default-funding", Founding.defaults(), false, false);
        quietly(() -> {
            d.buildStack(template(d, "University"), 1, true);
            MaterialsCheck.handWorkers(d);
        });
        Research r = d.getResearch();
        r.setGrant(Field.ENGINEERING, 8.5 * Research.postCost(Field.ENGINEERING, d.getPopulationManager().getWagesPerType()));
        play(d, 3);
        check("fixture: founded the default way, with a University and a grant that bought posts; the catalogue, read by the date, would have problems open in engineering",
                !d.foundedUnfitted() && r.posts(Field.ENGINEERING) > 0 && !r.openProblems(Field.ENGINEERING, d.getMonth()).isEmpty(), r.posts(Field.ENGINEERING) + " posts, "
                        + r.openProblems(Field.ENGINEERING, d.getMonth()).size() + " open by the date");
        ResearchView.Overview o = ResearchView.overview(d);
        boolean quiet = true;
        for (ResearchView.FieldReach row : o.fields()) {
            quiet &= row.inReach() == 0 && row.touches().isEmpty() && row.reachWords().equals("nothing in reach") && row.foundHere() == 0 && row.fromAbroad() == 0 && row.idle() == row.total();
        }
        check("the Overview has nothing in reach, nothing found and nothing from abroad, and every researcher idle (the posts are jobs and the faculty is there, with nothing to work on)",
                quiet && o.foundHere() == 0 && o.fromAbroad() == 0 && o.researchers() > 0 && o.idle() == o.researchers());
        check("...and the Funding cards have no lab threshold", Double.isNaN(ResearchView.funding(d, Field.ENGINEERING).labThreshold())
                && Double.isNaN(ResearchView.funding(d, Field.MEDICINE).labThreshold()));
        check("...the posts a grant buys are still the model's (the grant is paid and the posts are jobs)",
                ResearchView.funding(d, Field.ENGINEERING).posts() == r.posts(Field.ENGINEERING) && ResearchView.atGrant(d, Field.ENGINEERING, 2 * r.grant(Field.ENGINEERING)).posts() >= r.posts(Field.ENGINEERING));
        check("the preview has nothing to find, as is and staged", ResearchView.preview(d, ResearchView.Staged.none()) == ResearchPreview.NOTHING
                && ResearchView.preview(d, ResearchView.Staged.none().withGrant(Field.ENGINEERING, 1e9)) == ResearchPreview.NOTHING && ResearchPreview.NOTHING.total() == 0);
        check("what is still to come is none, the city having known them all at its founding", ResearchView.stillToCome(d) == 0, "" + ResearchView.stillToCome(d));
    }

    /* =====================================================================
       R3b, slice B: the reads the Overview and Funding screens draw their figures, dials, rows and school lines from (sections 16 to 18)
       ===================================================================== */

    /* ==================== 16. the Overview's figures and the dials' ranges ==================== */

    static void sectionSixteen(Game g) {
        System.out.println("\n--- 16. the figures across the top and the dials' ranges: the sums, the words, the posts the room holds, and a range that reaches past what it marks ---");
        ResearchView.Overview o = ResearchView.overview(g);
        double working = 0;
        int inReach = 0;
        for (ResearchView.FieldReach row : o.fields()) {
            working += row.total() - row.idle();
            inReach += row.inReach();
        }
        near("the researchers at work are everyone less the idle, field by field", o.working(), working, 1e-9);
        check("...and no more than all of them, and above nothing in the town", o.working() <= o.researchers() + 1e-9 && o.working() > 0, "" + o.working());
        check("the problems in reach are the fields' added (" + inReach + ")", o.inReach() == inReach && inReach > 0);

        // The words of the lead's last figure, on synthetic rows so that every shape is caused.
        ResearchView.FieldReach none = new ResearchView.FieldReach(Field.LAW, 0, 0, 0, 0, List.of(), 0, 0, List.of());
        List<ResearchView.FieldReach> mixed = new ArrayList<>();
        for (Field f : Field.values()) {
            mixed.add(new ResearchView.FieldReach(f, 0, 0, 0, f == Field.ENGINEERING ? 6 : f == Field.MEDICINE ? 2 : 0, List.of(), 0, 0, List.of()));
        }
        check("in reach by field: \"engineering 6 · medicine 2 · business and law none\" for two with some", ResearchView.reachByFieldWords(mixed).equals("engineering 6 · medicine 2 · business and law none"),
                ResearchView.reachByFieldWords(mixed));
        List<ResearchView.FieldReach> three = new ArrayList<>(mixed);
        three.set(2, new ResearchView.FieldReach(Field.BUSINESS, 0, 0, 0, 1, List.of(), 0, 0, List.of()));
        List<ResearchView.FieldReach> all = new ArrayList<>(), nothing = new ArrayList<>();
        for (Field f : Field.values()) {
            all.add(new ResearchView.FieldReach(f, 0, 0, 0, 1, List.of(), 0, 0, List.of()));
            nothing.add(new ResearchView.FieldReach(f, 0, 0, 0, 0, List.of(), 0, 0, List.of()));
        }
        check("...\"law none\" for one with none, no \"none\" for all with some, \"nothing in reach in any field\" for none",
                ResearchView.reachByFieldWords(three).equals("engineering 6 · medicine 2 · business 1 · law none") && ResearchView.reachByFieldWords(all).equals("engineering 1 · medicine 1 · business 1 · law 1")
                        && ResearchView.reachByFieldWords(nothing).equals("nothing in reach in any field") && ResearchView.reachByFieldWords(List.of(none)).equals("nothing in reach in any field"),
                ResearchView.reachByFieldWords(three) + " / " + ResearchView.reachByFieldWords(all));
        check("...and the town's own: it names the fields with something in reach with their counts", ResearchView.reachByFieldWords(o.fields()).contains("engineering " + o.fields().get(0).inReach()),
                ResearchView.reachByFieldWords(o.fields()));

        // The room, in whole posts.
        boolean roomOk = true;
        for (Field f : Field.values()) {
            ResearchView.Funding fu = ResearchView.funding(g, f);
            roomOk &= fu.roomPosts() == (int) Math.floor(fu.room()) && fu.roomPosts() >= 0;
        }
        check("the room in whole posts is the room floored, field by field", roomOk);
        ResearchView.Funding some = ResearchView.funding(g, Field.ENGINEERING);
        ResearchView.Funding odd = new ResearchView.Funding(Field.LAW, 0, 0, 0, 0, 58.7, 1, 58.7, 0, 0, 0, 0, 0, 0, Double.NaN);
        ResearchView.Funding noRoom = new ResearchView.Funding(Field.LAW, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, Double.NaN);
        ResearchView.Funding strange = new ResearchView.Funding(Field.LAW, 0, 0, 0, 0, Double.NaN, 1, Double.NaN, 0, 0, 0, 0, 0, 0, Double.NaN);
        check("...58.7 is 58 whole posts (Research.posts() floors, so a grant that fills the room buys 58), none is none, and what is not a number is none",
                odd.roomPosts() == 58 && Research.posts(odd.grantToFillRoom(), 1, odd.room()) == 58 && noRoom.roomPosts() == 0 && strange.roomPosts() == 0);

        // The grant dial.
        boolean grantOk = true;
        String grantOff = "";
        for (Field f : Field.values()) {
            ResearchView.Funding fu = ResearchView.funding(g, f);
            ResearchView.Dial d = ResearchView.grantDial(fu);
            double cost = fu.postCost();
            boolean ok = d.min() == 0 && d.step() == ResearchView.niceFloor(cost) && d.step() <= cost && d.step() * 2.5 > cost && d.max() > fu.grantToFillRoom() && d.max() >= fu.grant()
                    && d.max() >= fu.grantToFillRoom() + ResearchView.GRANT_REACH_POSTS * cost - 1e-9 && Math.abs(d.max() / d.step() - Math.rint(d.max() / d.step())) < 1e-9;
            if (!ok) grantOff += f + " " + d + " room " + fu.grantToFillRoom() + " grant " + fu.grant() + "; ";
            grantOk &= ok;
        }
        check("the grant dial runs from nothing in round steps (1, 2 or 5 times a power of ten, not above a post's cost and over two fifths of it), a whole number of them, past the grant that fills the room by at least "
                + ResearchView.GRANT_REACH_POSTS + " posts and past the grant in force", grantOk, grantOff);
        ResearchView.Funding big = new ResearchView.Funding(Field.LAW, 5000, 0, 0, 0, 100, 2, 200, 0, 0, 0, 0, 0, 0, Double.NaN);
        ResearchView.Funding free = new ResearchView.Funding(Field.LAW, 7, 0, 0, 0, 40, Double.NaN, Double.NaN, 0, 0, 0, 0, 0, 0, Double.NaN);
        check("...a grant in force beyond what the room asks for is still on the dial (2,500 posts), 20% past the room otherwise (100 posts: 120), and a post with no cost is a plain dial of a hundred",
                ResearchView.grantDial(big).max() == 5000 && ResearchView.grantDial(new ResearchView.Funding(Field.LAW, 0, 0, 0, 0, 100, 2, 200, 0, 0, 0, 0, 0, 0, Double.NaN)).max() == 240
                        && ResearchView.grantDial(free).step() == 1 && ResearchView.grantDial(free).max() == 100,
                ResearchView.grantDial(big) + " " + ResearchView.grantDial(free));
        check("niceFloor: the greatest of 1, 2 and 5 times a power of ten not above the figure; a step stays put while the cost walks (0.5158 and 0.53 are both 0.5; 0.99 is 0.5, 1 is 1, 4.9 is 2, 5 is 5)",
                ResearchView.niceFloor(0.5158) == 0.5 && ResearchView.niceFloor(0.53) == 0.5 && ResearchView.niceFloor(0.99) == 0.5 && ResearchView.niceFloor(1) == 1 && ResearchView.niceFloor(4.9) == 2
                        && ResearchView.niceFloor(5) == 5 && ResearchView.niceFloor(0.0123) == 0.01 && ResearchView.niceFloor(0) == 0 && ResearchView.niceFloor(Double.NaN) == 0);
        ResearchView.Funding walked = new ResearchView.Funding(Field.LAW, 0, 0, 0, 0, 100, 0.5158, 51.58, 0, 0, 0, 0, 0, 0, Double.NaN);
        ResearchView.Funding later = new ResearchView.Funding(Field.LAW, 0, 0, 0, 0, 100, 0.5191, 51.91, 0, 0, 0, 0, 0, 0, Double.NaN);
        check("...so a month's wages moving a post's cost by a little leave the grant dial's step where it was (" + ResearchView.grantDial(walked).step() + " and " + ResearchView.grantDial(later).step() + ")",
                ResearchView.grantDial(walked).step() == 0.5 && ResearchView.grantDial(later).step() == 0.5);

        // The prize dial.
        check("niceCeil: the least of 1, 2 and 5 times a power of ten not below the figure",
                ResearchView.niceCeil(1) == 1 && ResearchView.niceCeil(1.0001) == 2 && ResearchView.niceCeil(2) == 2 && ResearchView.niceCeil(3) == 5 && ResearchView.niceCeil(5.1) == 10
                        && ResearchView.niceCeil(70) == 100 && ResearchView.niceCeil(0.03) == 0.05 && ResearchView.niceCeil(0) == 0 && ResearchView.niceCeil(Double.NaN) == 0);
        boolean prizeOk = true;
        String prizeOff = "";
        for (Field f : Field.values()) {
            ResearchView.Funding fu = ResearchView.funding(g, f);
            ResearchView.Dial d = ResearchView.prizeDial(fu);
            double reach = Math.max(ResearchView.PRIZE_DIAL_YEARS * Research.MONTHS_A_YEAR * fu.postCost(), Double.isNaN(fu.labThreshold()) ? 0 : ResearchView.PRIZE_REACH * fu.labThreshold());
            boolean ok = d.min() == 0 && d.max() >= reach && d.max() >= fu.prize() && ResearchView.niceCeil(d.max()) == d.max() && Math.abs(d.step() * ResearchView.PRIZE_STEPS - d.max()) <= 1e-9 * d.max();
            if (!ok) prizeOff += f + " " + d + " reach " + reach + "; ";
            prizeOk &= ok;
        }
        check("the prize dial runs from nothing to a round number that reaches " + ResearchView.PRIZE_DIAL_YEARS + " researcher-years of posts, " + ResearchView.PRIZE_REACH
                + " times the least prize that brings a lab and the prize in force, in " + ResearchView.PRIZE_STEPS + " steps", prizeOk, prizeOff);
        ResearchView.Funding paid = new ResearchView.Funding(Field.LAW, 0, 9e9, 0, 0, 10, 1, 10, 0, 0, 0, 0, 0, 0, 1e9);
        ResearchView.Funding lab = new ResearchView.Funding(Field.LAW, 0, 0, 0, 0, 10, 1, 10, 0, 0, 0, 0, 0, 0, 3e9);
        check("...a prize in force past it, and a threshold past it, are on the dial",
                ResearchView.prizeDial(paid).max() >= 9e9 && ResearchView.prizeDial(lab).max() >= 6e9, ResearchView.prizeDial(paid) + " " + ResearchView.prizeDial(lab));
        check("the researchers in labs are the posts of a lab for each, none for none or fewer", ResearchView.labPosts(2) == 2 * Research.LAB_POSTS && ResearchView.labPosts(0) == 0 && ResearchView.labPosts(-1) == 0
                && some.postCost() > 0);
    }

    /* ==================== 17. the card's rows ==================== */

    static void sectionSeventeen(Game g) {
        System.out.println("\n--- 17. the card: nothing staged is the same figure on both sides; a staged grant and prize are the model's posts, bill, labs and preview ---");
        Research r = g.getResearch();
        ResearchView.Overview o = ResearchView.overview(g);
        ResearchPreview.Result asIs = ResearchView.preview(g, ResearchView.Staged.none());
        boolean same = true, words = true;
        String off = "";
        for (ResearchView.FieldReach row : o.fields()) {
            ResearchView.Card c = ResearchView.card(g, row.field(), ResearchView.Staged.none(), row, asIs, asIs);
            boolean ok = !c.moved() && c.researchersNow() == c.researchersAfter() && c.grantPostsNow() == c.grantPostsAfter() && c.labsNow() == c.labsAfter()
                    && c.labResearchersNow() == c.labResearchersAfter() && c.billNow() == c.billAfter() && c.unspentNow() == c.unspentAfter() && c.findsNow() == c.findsAfter()
                    && c.chanceNow() == c.chanceAfter() && c.grant() == r.grant(row.field()) && c.prize() == r.prize(row.field());
            if (!ok) off += row.field() + "; ";
            same &= ok;
            words &= c.findsNowWords().equals(c.findsAfterWords());
        }
        check("nothing staged: every row has the same figure on both sides, the grant and the prize are the ones in force", same, off);
        check("...and the preview's words are one on both sides", words);

        // The reads of one field, by hand: engineering's card at its own grant is the funding's.
        ResearchView.FieldReach eng = o.fields().get(0);
        ResearchView.Funding fu = ResearchView.funding(g, Field.ENGINEERING);
        ResearchView.Card now = ResearchView.card(g, Field.ENGINEERING, ResearchView.Staged.none(), eng, asIs, asIs);
        check("the card's \"now\" is the Funding read and the Overview's: researchers = the faculty and the posts' (" + fu.researchers() + ") and the labs' (" + eng.labResearchers() + "), the posts, the bill, the unspent, the room in posts",
                now.researchersNow() == fu.researchers() + eng.labResearchers() && now.grantPostsNow() == fu.grantPosts() && now.billNow() == fu.bill() && now.unspentNow() == fu.unspent()
                        && now.roomPosts() == fu.roomPosts() && now.labsNow() == fu.labsStanding() && now.funding().equals(fu) && now.researching());
        check("...and the preview's figures for the field are the Result's: finds " + now.findsNow() + ", chance " + now.chanceNow(), now.findsNow() == asIs.finds(Field.ENGINEERING) && now.chanceNow() == asIs.chance(Field.ENGINEERING));

        // A staged grant, two posts more.
        double cost = fu.postCost();
        // ...two posts above the posts struck: the wages walk, so the grant in force plus two posts' cost can buy fewer than the posts already struck.
        double grant = (fu.grantPosts() + 2) * cost;
        ResearchView.Staged grantStaged = ResearchView.Staged.none().withGrant(Field.ENGINEERING, grant);
        ResearchPreview.Result grantResult = ResearchView.preview(g, grantStaged);
        ResearchView.Card more = ResearchView.card(g, Field.ENGINEERING, grantStaged, eng, asIs, grantResult);
        ResearchView.AtGrant at = ResearchView.atGrant(g, Field.ENGINEERING, grant);
        check("a staged grant: the card is moved, the grant shown is the staged one, the prize is not", more.moved() && more.grantMoved() && !more.prizeMoved() && more.grant() == grant && more.prize() == r.prize(Field.ENGINEERING));
        check("...its posts, researchers and bill are atGrant()'s (" + at.grantPosts() + " posts, " + at.researchers() + " researchers, " + at.bill() + " a month), the labs' researchers unmoved",
                more.grantPostsAfter() == at.grantPosts() && more.researchersAfter() == at.researchers() + eng.labResearchers() && more.billAfter() == at.bill() && more.labsAfter() == more.labsNow()
                        && more.grantPostsAfter() > more.grantPostsNow(), more.grantPostsNow() + " -> " + more.grantPostsAfter() + " posts of " + more.roomPosts() + ", researchers " + more.researchersAfter() + " against " + (at.researchers() + eng.labResearchers()) + ", bill " + more.billAfter() + " against " + at.bill() + ", labs " + more.labsNow() + "/" + more.labsAfter());
        near("...the unspent is what the grant leaves of the posts' pay (the grant less the grant's posts' bill, never below none)", more.unspentAfter(),
                Math.max(0, grant - Research.bill(Field.ENGINEERING, at.grantPosts(), g.getPopulationManager().getWagesPerType(), g.getResearchFill())), 1e-9);
        check("...and the preview is the staged Result's, the \"now\" the one in force", more.findsAfter() == grantResult.finds(Field.ENGINEERING) && more.chanceAfter() == grantResult.chance(Field.ENGINEERING)
                && more.findsNow() == asIs.finds(Field.ENGINEERING) && more.asChance() == ResearchView.asChance(asIs.finds(Field.ENGINEERING), grantResult.finds(Field.ENGINEERING)));
        check("...and the grant in force staged is no change (nothing is moved by staging what is set)",
                !ResearchView.card(g, Field.ENGINEERING, ResearchView.Staged.none().withGrant(Field.ENGINEERING, r.grant(Field.ENGINEERING)), eng, asIs, asIs).moved());

        // The room full: a grant far past it buys the room's posts and no more, and says so.
        double past = fu.grantToFillRoom() + 10 * cost;
        ResearchView.Staged pastStaged = ResearchView.Staged.none().withGrant(Field.ENGINEERING, past);
        ResearchView.Card full = ResearchView.card(g, Field.ENGINEERING, pastStaged, eng, asIs, ResearchView.preview(g, pastStaged));
        check("a grant past the room buys the room's posts (" + full.grantPostsAfter() + " of " + full.roomPosts() + ") and the card says the room is full; the one in force does not",
                full.grantPostsAfter() == fu.roomPosts() && full.roomFull() && !now.roomFull(), full.grantPostsAfter() + " of " + fu.roomPosts());
        check("...and what it leaves unspent is above nothing (the grant over the posts' pay), the card says so", full.unspentAfter() > 0 && full.unspends());
        check("...and a card with a hundredth of a dollar unspent says nothing of it", !new ResearchView.Card(Field.LAW, null, null, null, true, 0, 0, false, false, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0.00001, 0, 0, 0, 0).unspends());

        // A prize past the least that brings a lab.
        double threshold = fu.labThreshold();
        check("fixture: engineering has a least prize that brings a lab in this town (" + threshold + ")", !Double.isNaN(threshold) && threshold > 0);
        double bigPrize = Double.isNaN(threshold) ? 1e9 : 4 * threshold;
        ResearchView.Staged prizeStaged = ResearchView.Staged.none().withPrize(Field.ENGINEERING, bigPrize);
        ResearchPreview.Result prizeResult = ResearchView.preview(g, prizeStaged);
        ResearchView.Card paying = ResearchView.card(g, Field.ENGINEERING, prizeStaged, eng, asIs, prizeResult);
        check("a staged prize: the card is moved by the prize alone, the labs are the staged preview's first month (" + prizeResult.labsFirstMonth(Field.ENGINEERING) + "), not the grant's posts",
                paying.moved() && paying.prizeMoved() && !paying.grantMoved() && paying.prize() == bigPrize && paying.labsAfter() == prizeResult.labsFirstMonth(Field.ENGINEERING)
                        && paying.grantPostsAfter() == paying.grantPostsNow() && paying.billAfter() == paying.billNow());
        check("...whatever labs it brings are a lab's posts each (" + ResearchView.labPosts(paying.labsAfter()) + " researchers for " + paying.labsAfter() + "), and the card's researchers are the field's and the labs' (a lab set going by the dial that stands as it did: the town's own)",
                paying.labResearchersAfter() == (paying.labsAfter() == paying.labsNow() ? paying.labResearchersNow() : ResearchView.labPosts(paying.labsAfter()))
                        && paying.researchersAfter() == fu.researchers() + paying.labResearchersAfter());
        check("...and the card without a prize move keeps the labs the town has (" + now.labsNow() + ") and their researchers as the model counts them (" + now.labResearchersNow() + ")",
                now.labResearchersAfter() == eng.labResearchers() && now.labsAfter() == fu.labsStanding());

        // The words of the preview, on both sides.
        check("a chance under one is a percent and a count from one up: asChance() of the pair, and previewWords() says \"none\", \"under 1%\", \"25%\", \"1.10\"",
                ResearchView.asChance(0.2, 0.9) && !ResearchView.asChance(0.2, 1.1) && ResearchView.previewWords(0, 0, true).equals("none") && ResearchView.previewWords(0.001, 0.001, true).equals("under 1%")
                        && ResearchView.previewWords(0.3, 0.25, true).equals("25%") && ResearchView.previewWords(1.1, 0.7, false).equals("1.10"));
    }

    /* ==================== 18. the schools' lines, and THE BUDGET ==================== */

    static void sectionEighteen(Game g) {
        System.out.println("\n--- 18. the schools' research posts: a kind's share of each field's posts by its share of the room, and THE BUDGET with the staged grants' posts ---");
        Research r = g.getResearch();
        ResearchView.Pieces p = ResearchView.piecesOf(g);
        EnumMap<Field, Double> room = new EnumMap<>(Field.class), kinds = new EnumMap<>(Field.class), filledSum = new EnumMap<>(Field.class);
        double postsSum = 0;
        for (EducationType kind : EducationType.values()) {
            // The kinds' staffs together are the game's own.
            for (Map.Entry<Field, Double> e : ResearchView.staffOfKind(g, kind).entrySet()) kinds.merge(e.getKey(), e.getValue(), Double::sum);
            for (ResearchView.SchoolPosts sp : ResearchView.schoolPosts(g, kind)) {
                room.merge(sp.field(), sp.room(), Double::sum);
                filledSum.merge(sp.field(), sp.filled(), Double::sum);
                postsSum += sp.posts();
            }
        }
        boolean staffOk = true, roomOk = true, filledOk = true;
        double posts = 0;
        for (Field f : Field.values()) {
            staffOk &= Math.abs(kinds.getOrDefault(f, 0.0) - p.nominalStaff().getOrDefault(f, 0.0)) <= 1e-9;
            roomOk &= Math.abs(room.getOrDefault(f, 0.0) - ResearchView.funding(g, f).room()) <= 1e-9;
            filledOk &= Math.abs(filledSum.getOrDefault(f, 0.0) - Research.filledResearchers(f, r.posts(f), p.fill())) <= 1e-9;
            posts += r.posts(f);
        }
        check("the kinds of school together stand up the game's own staff in each field (Game.researchStaff()), and give it their rooms' sum as the field's room", staffOk && roomOk);
        check("...the posts struck, over the kinds that carry them, are the research's own (" + posts + "), and the researchers they give each field's", Math.abs(postsSum - posts) <= 1e-9 && filledOk,
                postsSum + " against " + posts);
        List<ResearchView.SchoolPosts> uni = ResearchView.schoolPosts(g, EducationType.UNIVERSITY), medical = ResearchView.schoolPosts(g, EducationType.MEDICAL);
        check("the University hosts all four fields (" + uni.size() + " lines), the Medical School medicine alone, its room the staff there times Research.room()",
                uni.size() == 4 && medical.size() == 1 && medical.get(0).field() == Field.MEDICINE
                        && Math.abs(medical.get(0).room() - Research.room(ResearchView.staffOfKind(g, EducationType.MEDICAL).get(Field.MEDICINE))) <= 1e-9);
        check("...the Medical School, with the Universities, carries medicine's posts between them: its share is its room over the field's",
                Math.abs(medical.get(0).posts() - r.posts(Field.MEDICINE) * medical.get(0).room() / ResearchView.funding(g, Field.MEDICINE).room()) <= 1e-6, medical.get(0).toString());
        check("a kind of school that hosts none (a high school, a college), and one of which none stands (a Law school), carries nothing",
                ResearchView.schoolPosts(g, EducationType.HIGH).isEmpty() && ResearchView.schoolPosts(g, EducationType.COLLEGE).isEmpty() && ResearchView.schoolPosts(g, EducationType.LAW).isEmpty()
                        && ResearchView.schoolPosts(g, null).isEmpty());
        check("the kinds that host research are the University, the Institute of technology and the Medical, Business and Law schools, and no other",
                ResearchView.hostsResearch(EducationType.UNIVERSITY) && ResearchView.hostsResearch(EducationType.ENGINEERING) && ResearchView.hostsResearch(EducationType.MEDICAL)
                        && ResearchView.hostsResearch(EducationType.BUSINESS) && ResearchView.hostsResearch(EducationType.LAW) && !ResearchView.hostsResearch(EducationType.HIGH)
                        && !ResearchView.hostsResearch(EducationType.COLLEGE) && !ResearchView.hostsResearch(null));
        List<ResearchView.SchoolRow> rows = ResearchView.schoolRows(g);
        check("the Overview's schools are the hosting kinds that stand, with how many: the University (2), the Medical School (1) and the Institute of technology (1) - in the kinds' order",
                rows.size() == 3 && rows.get(0).kind() == EducationType.UNIVERSITY && rows.get(0).count() == 2 && rows.get(1).kind() == EducationType.MEDICAL && rows.get(1).count() == 1
                        && rows.get(2).kind() == EducationType.ENGINEERING && rows.get(2).count() == 1 && rows.get(0).posts().equals(uni), rows.toString());

        // THE BUDGET.
        double balance = g.getEconomyManager().getNationalAccounts().getBalance();
        ResearchView.Budget none = ResearchView.budget(g, ResearchView.Staged.none());
        check("THE BUDGET with nothing staged is the books' balance on both sides (" + balance + ")", none.before() == balance && none.after() == balance && none.change() == 0);
        double cost = ResearchView.funding(g, Field.BUSINESS).postCost();
        double more = (ResearchView.funding(g, Field.BUSINESS).grantPosts() + 3) * cost;
        ResearchView.AtGrant atMore = ResearchView.atGrant(g, Field.BUSINESS, more), atNow = ResearchView.atGrant(g, Field.BUSINESS, r.grant(Field.BUSINESS));
        ResearchView.Budget staged = ResearchView.budget(g, ResearchView.Staged.none().withGrant(Field.BUSINESS, more));
        check("a staged grant adds its posts' bill to the research bill and takes it off the balance: " + (atMore.bill() - atNow.bill()) + " a month more",
                staged.before() == balance && Math.abs(staged.change() + (atMore.bill() - atNow.bill())) <= 1e-9 && atMore.bill() > atNow.bill(), staged.toString());
        check("...a prize moves nothing (it is paid on a find), and two grants together move it by the two bills",
                ResearchView.budget(g, ResearchView.Staged.none().withPrize(Field.BUSINESS, 1e6)).change() == 0
                        && Math.abs(ResearchView.budget(g, ResearchView.Staged.none().withGrant(Field.BUSINESS, more).withGrant(Field.ENGINEERING, 0)).change()
                        + (atMore.bill() - atNow.bill()) + ResearchView.atGrant(g, Field.ENGINEERING, 0).bill() - ResearchView.atGrant(g, Field.ENGINEERING, r.grant(Field.ENGINEERING)).bill()) <= 1e-9);
    }

    /* ==================== 20. a default city's faculty, through a save ==================== */

    /** The month's five counts of a research, field by field, in words a harness can compare. */
    static String monthCounts(Research r) {
        StringBuilder out = new StringBuilder();
        for (Field f : Field.values()) {
            out.append(f.name()).append(' ').append(r.researchers(f)).append('/').append(r.idle(f)).append('/').append(r.open(f)).append('/')
                    .append(r.labResearchers(f)).append('/').append(r.labsIdle(f)).append("; ");
        }
        return out.toString();
    }

    static void sectionTwenty() throws Exception {
        System.out.println("\n--- 20. a city founded the default way with a University and no grant now: its researchers are the faculty's own, and they survive a save ---");
        ensureQuiet();
        // KeptCheck 99's default town: a village with a University whose grant's posts drew the graduates its staff are, the grant then taken away and a
        // month played, so no post is held and the faculty are at research (a University built in a village with no grant ever is not staffed in months).
        Game d = KeptCheck.researchDefaultTown();
        d.getResearch().setGrant(Field.ENGINEERING, 0);
        play(d, 1);
        Research r = d.getResearch();
        ResearchView.Overview ov = ResearchView.overview(d);
        check("fixture: founded the default way with a University, its grant taken away a month ago, its faculty at research (" + ov.researchers() + ") and every one of them idle",
                !d.foundedUnfitted() && ov.researchers() > 0 && ov.idle() == ov.researchers() && r.grant(Field.ENGINEERING) == 0 && r.grant(Field.MEDICINE) == 0);
        check("the Overview counts no post where no grant bought one (" + ov.posts() + "), so the page claims none", ov.posts() == 0);
        check("the research has nothing of its own (empty) and still has the month's counts to show", r.isEmpty() && r.hasMonthCounts());
        String before = monthCounts(r);
        GameFiles.Result wrote = EquipmentCheck.save(d, "default faculty");
        check("the city is saved", wrote.ok, wrote.message());
        JsonObject root = JsonParser.parseString(Files.readString(d.getGameFiles().saveFile(1))).getAsJsonObject();
        check("...with its research written for the faculty's counts alone", root.has("research") && root.getAsJsonObject("research").has("researchers"));
        Game back = new Game(d.getGameFiles());
        quietly(() -> back.loadGameSave(1));
        check("loaded without a failure", back.getLoadFailure() == null, String.valueOf(back.getLoadFailure()));
        check("the loaded city reads the same counts in every field before it plays a month: " + before, monthCounts(back.getResearch()).equals(before),
                monthCounts(back.getResearch()));
        ResearchView.Overview again = ResearchView.overview(back);
        check("...so its Overview (and the hub's card) show the same researchers and idle (" + again.researchers() + " and " + again.idle() + "), and no post",
                again.researchers() == ov.researchers() && again.idle() == ov.idle() && again.posts() == 0);
    }

    /* ==================== 19. a lab standing ==================== */

    static void sectionNineteen(Game g) {
        System.out.println("\n--- 19. a lab standing: the card counts it and its researchers, and the same lab is on both sides of a card that moves a grant ---");
        quietly(() -> g.buildStack(template(g, "Engineering Research Lab"), 1, true));
        quietly(() -> MaterialsCheck.handWorkers(g));
        play(g, 2);
        Research r = g.getResearch();
        ResearchView.Overview o = ResearchView.overview(g);
        ResearchView.FieldReach eng = o.fields().get(0);
        ResearchPreview.Result asIs = ResearchView.preview(g, ResearchView.Staged.none());
        ResearchView.Funding fu = ResearchView.funding(g, Field.ENGINEERING);
        check("fixture: an Engineering Research Lab stands and its researchers are counted (" + fu.labsStanding() + " lab, " + eng.labResearchers() + " researchers)", fu.labsStanding() == 1 && eng.labResearchers() > 0);
        ResearchView.Card now = ResearchView.card(g, Field.ENGINEERING, ResearchView.Staged.none(), eng, asIs, asIs);
        check("the card counts the lab and its researchers with the field's: researchers " + now.researchersNow() + " = the field's " + fu.researchers() + " and the lab's " + eng.labResearchers(),
                now.labsNow() == 1 && now.labsAfter() == 1 && now.labResearchersNow() == eng.labResearchers() && now.labResearchersAfter() == eng.labResearchers()
                        && now.researchersNow() == fu.researchers() + eng.labResearchers());
        double grant = (fu.grantPosts() - 2) * fu.postCost();
        ResearchView.Staged less = ResearchView.Staged.none().withGrant(Field.ENGINEERING, grant);
        ResearchView.Card fewer = ResearchView.card(g, Field.ENGINEERING, less, eng, asIs, ResearchView.preview(g, less));
        check("a grant staged lower moves the field's researchers and not the lab's: the lab is on both sides (" + fewer.researchersNow() + " -> " + fewer.researchersAfter() + ")",
                fewer.labResearchersAfter() == eng.labResearchers() && fewer.labsAfter() == 1 && fewer.researchersAfter() == ResearchView.atGrant(g, Field.ENGINEERING, grant).researchers() + eng.labResearchers()
                        && fewer.researchersAfter() < fewer.researchersNow());
    }
}
