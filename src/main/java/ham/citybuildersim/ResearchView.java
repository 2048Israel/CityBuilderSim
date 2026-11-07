package ham.citybuildersim;

import ham.citybuildersim.ResearchCatalog.Entry;
import ham.citybuildersim.ResearchCatalog.Field;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * What the research screens draw (R5): every discovery on a timeline by field (found here, or from abroad on history's date), the equipment each
 * sector's stacks carry with the verdict on it, the recipes in use, how a building became known to the city (the NEW mark and "where it came from"
 * on its card), and the flags a discovery puts on City History's charts. Static reads of the city and nothing more: no JavaFX, nothing kept, nothing
 * booked.
 *
 * WHY (R5; docs/studies/research-design.md 5.5, 6.2, 6.3). The screens' rule is "every figure through a public model getter, never recomputed in the
 * screen", and four screens (Policy > Research > Found, a sector's Investors page, a building's card, City History) need the same facts, so they are
 * read once, here, where a harness can hold them to the model's own numbers. The timeline is DERIVED and not saved: a find's month is already saved
 * (Research.foundMonth()), and history's month is data (research.json), so what a screen shows can always be had again from the two; a saved copy
 * could only part from them. What cannot be had again - the researchers and the money over time, the share of the equipment fitted - is a flow, and
 * HistorySave keeps it (researchersKey(), RESEARCH_SPEND, EQUIPMENT_FITTED).
 *
 * ONLY A CITY FOUNDED THE 1900 WAY HAS ANYTHING TO SHOW (Game.foundedUnfitted(); Research's and Recipes' headers say the same). In a city founded the
 * default way every discovery is known from the start, nothing arrives, nothing is chosen and nothing is unfitted, so every list here is empty, every
 * origin null and knownAtFounding() the whole catalogue: the screens then draw nothing new, and every city a player has played keeps its screens.
 *
 * TWO LAYERS, as EquipmentFit and Recipes are: the cores that take a catalogue, a research and a month (package-private) let a harness put the city at
 * any month and the catalogue at any shape; the public reads take the game and pass its own.
 */
public final class ResearchView {

    private ResearchView() { }

    /** How a discovery reached the city: found here by its own researchers before history's date, or from abroad on history's date. */
    public enum Source { HERE, ABROAD }

    /** The month every city is founded in. An entry the city knew at or before it was known at the founding, and is no discovery of the city's life. */
    public static final int FIRST_MONTH = 1;

    /**
     * How many months a building is NEW on its card after it became known in the city: it is new while its age (the city's month less the month it
     * became known) is under this. The screen also drops the mark once the player has seen the card; that is not saved (PolicyScreen's biting flags'
     * rule), so it is not here.
     */
    public static final int NEW_MONTHS = 12;

    /** The kind of the flags a discovery puts on City History's decision lane (flagEntries()); a discovery is not a decision, so it is no DecisionLog kind. */
    public static final String FLAG_KIND = "discovery";

    /* =====================================================================
       THE TIMELINE
       ===================================================================== */

    /** One discovery of the city's life: its catalogue entry, the month the city came to know it and how. */
    public record Discovery(Entry entry, int month, Source source) {

        /** The month history has it in. */
        public int worldMonth() {
            return entry.worldMonth();
        }

        /** How many years before history's date the city found it: (worldMonth - month) / 12 for one found here, 0 for one from abroad. */
        public double yearsAhead() {
            return source == Source.HERE ? (worldMonth() - month) / 12.0 : 0;
        }

        /** The field that researches it. */
        public ResearchCatalog.Field field() {
            return entry.field();
        }
    }

    /** The month the city knows an entry from: the month it found it, where that is before history's date, else history's. */
    private static int monthHere(Research research, Entry e) {
        return Math.min(e.worldMonth(), research.foundMonth(e.code()));
    }

    /** How the city came to know an entry: HERE where it found it before history's date, else ABROAD on history's date. */
    private static Source sourceOf(Research research, Entry e) {
        return research.foundMonth(e.code()) < e.worldMonth() ? Source.HERE : Source.ABROAD;
    }

    /**
     * The timeline of a research at a month, against a catalogue (the core): every entry the city came to know after FIRST_MONTH and by `month`, with
     * the month it came to know it and how, ordered by month and, inside a month, by the catalogue's order. The research is a city founded the 1900 way.
     */
    static List<Discovery> timeline(ResearchCatalog catalogue, Research research, int month) {
        List<Discovery> out = new ArrayList<>();
        for (Entry e : catalogue.entries()) {
            int m = monthHere(research, e);
            if (m <= FIRST_MONTH || m > month) continue;
            out.add(new Discovery(e, m, sourceOf(research, e)));
        }
        // List.sort is stable: the entries of one month keep the catalogue's order.
        out.sort(Comparator.comparingInt(Discovery::month));
        return out;
    }

    /**
     * The city's discoveries so far, oldest first: in a city founded the 1900 way, each entry known by the city's month whose month in this city is after
     * FIRST_MONTH - the month it was found here (Research.foundMonth()) when it was found before history's date, else history's (Entry.worldMonth()) -
     * with HERE or ABROAD. Ordered by month, then the catalogue's order. Empty in a city founded the default way. A fresh list.
     */
    public static List<Discovery> timeline(Game game) {
        return game.foundedUnfitted() ? timeline(ResearchCatalog.get(), game.getResearch(), game.getMonth()) : new ArrayList<>();
    }

    /** How many entries the city knew at the founding (the core): those whose month in this city is FIRST_MONTH or before. */
    static int knownAtFounding(ResearchCatalog catalogue, Research research) {
        int n = 0;
        for (Entry e : catalogue.entries()) if (monthHere(research, e) <= FIRST_MONTH) n++;
        return n;
    }

    /**
     * How many discoveries the city knew at the founding: the entries history dates at FIRST_MONTH or before in a city founded the 1900 way, and the whole
     * catalogue in a city founded the default way, which "founded knowing all N".
     */
    public static int knownAtFounding(Game game) {
        ResearchCatalog catalogue = ResearchCatalog.get();
        return game.foundedUnfitted() ? knownAtFounding(catalogue, game.getResearch()) : catalogue.entries().size();
    }

    /** How many of the discoveries came by a source. */
    public static int count(List<Discovery> timeline, Source source) {
        int n = 0;
        for (Discovery d : timeline) if (d.source() == source) n++;
        return n;
    }

    /** The timeline split by field: every field present, in the enum's order, possibly with an empty list; each list keeps the timeline's order. */
    public static EnumMap<ResearchCatalog.Field, List<Discovery>> byField(List<Discovery> timeline) {
        EnumMap<ResearchCatalog.Field, List<Discovery>> out = new EnumMap<>(ResearchCatalog.Field.class);
        for (ResearchCatalog.Field f : ResearchCatalog.Field.values()) out.put(f, new ArrayList<>());
        for (Discovery d : timeline) out.get(d.field()).add(d);
        return out;
    }

    /**
     * The count of discoveries at or before each month of City History's axis, for the graph of found here against from abroad: {here[], abroad[]}, entry
     * i the count at or before month firstMonth + i, so entry 0 holds every discovery of firstMonth and before; length lastMonth - firstMonth + 1, and
     * 0 when lastMonth is before firstMonth. A discovery after lastMonth is in neither.
     */
    public static int[][] cumulative(List<Discovery> timeline, int firstMonth, int lastMonth) {
        int n = Math.max(0, lastMonth - firstMonth + 1);
        int[] here = new int[n], abroad = new int[n];
        for (Discovery d : timeline) {
            int i = Math.max(0, d.month() - firstMonth);
            if (i >= n) continue;
            if (d.source() == Source.HERE) here[i]++;
            else abroad[i]++;
        }
        for (int i = 1; i < n; i++) {
            here[i] += here[i - 1];
            abroad[i] += abroad[i - 1];
        }
        return new int[][] { here, abroad };
    }

    /* =====================================================================
       THE FLAGS ON CITY HISTORY
       ===================================================================== */

    /**
     * One entry for each discovery of the timeline, oldest first, of kind FLAG_KIND at the discovery's month: "Found here: Electric arc steel furnace,
     * 0.8 years before the world" (one decimal, and always "years") or "From abroad: Electric arc steel furnace". They are NOT put into the decision
     * log - a discovery is not a decision - and the chart is handed them beside the log's flags (ChartModel.flags(DecisionLog, List)).
     */
    public static List<DecisionLog.Entry> flagEntries(Game game) {
        List<DecisionLog.Entry> out = new ArrayList<>();
        for (Discovery d : timeline(game)) {
            String words = d.source() == Source.HERE
                    ? String.format(Locale.ROOT, "Found here: %s, %.1f years before the world", d.entry().name(), d.yearsAhead())
                    : "From abroad: " + d.entry().name();
            out.add(new DecisionLog.Entry(d.month(), FLAG_KIND, words));
        }
        return out;
    }

    /* =====================================================================
       THE EQUIPMENT
       ===================================================================== */

    /**
     * One piece of equipment on one stack, as the Investors page and the Found page list it: how many of the stack's buildings there are, how many lack
     * the piece, how many are being fitted now, the verdict on the piece (the same call the Fit pill makes) and the discovery that made the piece known
     * (null when it was known at the founding or by a year).
     */
    public record PieceRow(BuildingsTemplate template, Equipment piece, int buildings, int unfitted, int onSite, EquipmentFit.Verdict verdict,
                           Discovery from) {

        /** The buildings that carry the piece: the buildings less those that lack it. */
        public int fitted() {
            return buildings - unfitted;
        }

        /** The share of the stack that carries the piece, 0 to 1 (the bar on the Found page): fitted() over the buildings, 0 where there are none. */
        public double share() {
            return buildings > 0 ? fitted() / (double) buildings : 0;
        }
    }

    /** One stack's piece, before it is judged: what both equipment() and fittedShare() read. */
    private record Standing(BuildingsStacks stack, Equipment piece, int buildings, int unfitted) { }

    /** The pieces standing in the city's stacks, known at the city's month, in the templates' order and each template's pieces' order; `sectorKey` null is the whole city. */
    private static List<Standing> standing(Game game, String sectorKey) {
        List<Standing> out = new ArrayList<>();
        BuildingManager buildings = game.getBuildingManager();
        int month = game.getMonth();
        Research research = game.getResearch();
        for (BuildingsTemplate t : buildings.getTemplates()) {
            if (t.getEquipment().isEmpty()) continue;
            if (sectorKey != null && !(t.isOwnedBySector() && sectorKey.equals(t.getSector()))) continue;
            BuildingsStacks stack = buildings.getStack(t);
            if (stack == null || stack.getQuantity() <= 0) continue;
            for (Equipment piece : t.getEquipment()) {
                // An unknown piece is not shown anywhere: there is no tree to see.
                if (!piece.knownAt(month, research)) continue;
                out.add(new Standing(stack, piece, stack.getQuantity(), Math.min(stack.getQuantity(), stack.getUnfitted(piece.getId()))));
            }
        }
        return out;
    }

    /**
     * The equipment standing in the city, in a city founded the 1900 way: for each template with a stack of buildings (the sector's own when `sectorKey`
     * is not null, the whole city's when it is), in the templates' order, a row for each of its pieces known at the city's month. The verdict is
     * EquipmentFit.verdict() on the stack, the very call the Fit pill makes, so the two never disagree. Empty in a city founded the default way.
     */
    public static List<PieceRow> equipment(Game game, String sectorKey) {
        List<PieceRow> out = new ArrayList<>();
        if (!game.foundedUnfitted()) return out;
        Map<String, Discovery> found = discoveriesByCode(timeline(game));
        for (Standing s : standing(game, sectorKey)) {
            BuildingsTemplate t = s.stack().getBuilding();
            out.add(new PieceRow(t, s.piece(), s.buildings(), s.unfitted(), game.getFittings().onSite(t.getId(), s.piece().getId()),
                    EquipmentFit.verdict(game, s.stack(), s.piece()), found.get(s.piece().getSince())));
        }
        return out;
    }

    /**
     * The share of the equipment standing that is fitted, 0 to 1: over every row of equipment(game, null), the buildings that carry the piece over the
     * buildings. NaN where there is no row (every city founded the default way, and a 1900 city before its first stack with a known piece). Reads no
     * price: it is what HistorySave records every month (EQUIPMENT_FITTED).
     */
    public static double fittedShare(Game game) {
        if (!game.foundedUnfitted()) return Double.NaN;
        double buildings = 0, fitted = 0;
        for (Standing s : standing(game, null)) {
            buildings += s.buildings();
            fitted += s.buildings() - s.unfitted();
        }
        return buildings > 0 ? fitted / buildings : Double.NaN;
    }

    /** The discoveries of a timeline by catalogue code. */
    private static Map<String, Discovery> discoveriesByCode(List<Discovery> timeline) {
        Map<String, Discovery> out = new HashMap<>();
        for (Discovery d : timeline) out.put(d.entry().code(), d);
        return out;
    }

    /* =====================================================================
       THE RECIPES
       ===================================================================== */

    /**
     * A way of running a template other than its default, known in the city: how many of the template's `buildings` run it now (the fitted part and the
     * unfitted part that chose it, as Recipes.apply() weighs them; 0 for one known and not chosen) and the discovery that made it known (null when it
     * was known at the founding or by a year).
     */
    public record RecipeUse(BuildingsTemplate template, Recipe recipe, int running, int buildings, Discovery from) {

        /** The share of the stack that runs the recipe, 0 to 1 (the bar on the Found page): running over the buildings, 0 where there are none. */
        public double share() {
            return buildings > 0 ? running / (double) buildings : 0;
        }
    }

    /**
     * The recipes in use, in a city founded the 1900 way: for each template with a choice of recipes and a stack of buildings, each of its recipes other
     * than the default that is known at the city's month. `running` counts the buildings of the parts that run it: the fitted part is the stack's quantity
     * less its unfitted count of the template's recipe piece, the unfitted part that count, and a template with no recipe piece is all one part (the
     * way Recipes.apply() counts them), as chosen by Recipes.chosen(). Empty in a city founded the default way.
     */
    public static List<RecipeUse> recipesInUse(Game game) {
        List<RecipeUse> out = new ArrayList<>();
        if (!game.foundedUnfitted()) return out;
        Map<String, Discovery> found = discoveriesByCode(timeline(game));
        BuildingManager buildings = game.getBuildingManager();
        int month = game.getMonth();
        Research research = game.getResearch();
        for (BuildingsTemplate t : buildings.getTemplates()) {
            if (!t.hasRecipeChoice()) continue;
            BuildingsStacks stack = buildings.getStack(t);
            if (stack == null || stack.getQuantity() <= 0) continue;
            int quantity = stack.getQuantity();
            String piece = t.recipePieceId();
            int unfitted = piece == null ? 0 : Math.min(quantity, stack.getUnfitted(piece));
            String[] picked = game.getRecipes().chosen(t.getId());
            for (Recipe r : t.getRecipes()) {
                if (r.isDefault() || !r.knownAt(month, research)) continue;
                int running = 0;
                if (picked != null && picked[0].equals(r.getId())) running += quantity - unfitted;
                if (picked != null && picked[1].equals(r.getId())) running += unfitted;
                out.add(new RecipeUse(t, r, running, quantity, found.get(r.getSince())));
            }
        }
        return out;
    }

    /* =====================================================================
       WHERE A BUILDING CAME FROM
       ===================================================================== */

    /**
     * How a template became known to the city after the founding: the month, whether the city found it or the world did, and the discovery when its
     * "since" is a catalogue code (null when it is a year).
     */
    public record Origin(BuildingsTemplate template, int month, Source source, Entry entry) {

        /** How many years before history's date the city found it: as Discovery's, 0 for one from abroad and for one with no entry. */
        public double yearsAhead() {
            return source == Source.HERE && entry != null ? (entry.worldMonth() - month) / 12.0 : 0;
        }
    }

    /**
     * The origin of a template for a research at a month, against a catalogue (the core): a since that is a catalogue code - the month and source the
     * timeline reads, and the entry; a since that is a year after 1900's January - ABROAD at that year's January, no entry. Null for a template known
     * at FIRST_MONTH (undated, or a since that is a month then or before), one not known yet by `month`, and one whose code the catalogue lacks (never known).
     */
    static Origin origin(ResearchCatalog catalogue, Research research, int month, BuildingsTemplate t) {
        String since = t.getSinceText();
        if (since.isEmpty()) return null;
        Integer year = Equipment.yearOf(since);
        if (year != null) {
            int m = PolicyDates.monthOf(year, 1);
            return m <= FIRST_MONTH || m > month ? null : new Origin(t, m, Source.ABROAD, null);
        }
        Entry e = catalogue.entry(since);
        if (e == null) return null;
        int m = monthHere(research, e);
        return m <= FIRST_MONTH || m > month ? null : new Origin(t, m, sourceOf(research, e), e);
    }

    /**
     * How a template became known after the founding, in a city founded the 1900 way (see the core). Null when the city knew it at the founding, does
     * not know it yet, or was founded the default way.
     */
    public static Origin origin(Game game, BuildingsTemplate t) {
        return game.foundedUnfitted() ? origin(ResearchCatalog.get(), game.getResearch(), game.getMonth(), t) : null;
    }

    /** Whether an origin is NEW at a month (the core): there is one, and it is fewer than NEW_MONTHS months old. */
    static boolean isNew(Origin origin, int month) {
        return origin != null && month - origin.month() < NEW_MONTHS;
    }

    /** Whether a template is NEW on its card: it has an origin, and became known fewer than NEW_MONTHS months before the city's month. */
    public static boolean isNew(Game game, BuildingsTemplate t) {
        return isNew(origin(game, t), game.getMonth());
    }

    /** The templates that are NEW, in the templates' order. */
    public static List<BuildingsTemplate> newTemplates(Game game) {
        List<BuildingsTemplate> out = new ArrayList<>();
        if (!game.foundedUnfitted()) return out;
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) if (isNew(game, t)) out.add(t);
        return out;
    }

    /** Whether a template is a Research Lab (R4's four): it researches a field. The words a lab's card says are the screen's. */
    public static boolean isLab(BuildingsTemplate t) {
        return t != null && t.getResearches() != null;
    }

    /* =====================================================================
       THE LAID-ON AXIS AND WHAT IS STILL TO COME (R3b, from R5's gate review: the Found page worked these out itself)
       ===================================================================== */

    /**
     * The counts of found here and from abroad laid on History's months: {here[], abroad[]}, entry i the count at or before the axis' month i, each as long
     * as the axis (cumulative() over the axis' first to last month, then read at each month's distance from the first; a month outside it is clamped to
     * its ends). Both empty for an empty axis, and NaN throughout where the axis runs backwards (cumulative() has no entry to read).
     */
    public static double[][] cumulativeOn(List<Discovery> timeline, List<Integer> axis) {
        int n = axis == null ? 0 : axis.size();
        double[] here = new double[n], abroad = new double[n];
        if (n == 0) return new double[][] { here, abroad };
        int[][] counts = cumulative(timeline, axis.get(0), axis.get(n - 1));
        for (int i = 0; i < n; i++) {
            int at = Math.max(0, Math.min(counts[0].length - 1, axis.get(i) - axis.get(0)));
            here[i] = counts[0].length > 0 ? counts[0][at] : Double.NaN;
            abroad[i] = counts[1].length > 0 ? counts[1][at] : Double.NaN;
        }
        return new double[][] { here, abroad };
    }

    /** How many discoveries are still to come (the core): the catalogue's `all` less those known at the founding, found here and come from abroad, never below none. */
    public static int stillToCome(int all, int knownAtFounding, int here, int abroad) {
        return Math.max(0, all - knownAtFounding - here - abroad);
    }

    /** How many of the catalogue's discoveries are still to come in the city: those history has not yet dated and the city has not found, in a city founded the 1900 way; none in any other. */
    public static int stillToCome(Game game) {
        List<Discovery> timeline = timeline(game);
        return stillToCome(ResearchCatalog.get().entries().size(), knownAtFounding(game), count(timeline, Source.HERE), count(timeline, Source.ABROAD));
    }

    /* =====================================================================
       THE OVERVIEW (R3b; docs/studies/research-design.md 5.5, 6.2)
       ===================================================================== */

    /** A school standing that hosts a field's research, by its template's name and how many of it stand. */
    public record SchoolCount(String name, int count) { }

    /**
     * One field on the Overview page: its researchers (the city's own and the labs'), the idle among them, the problems open to them and what those
     * touch, what the field has found here and had come from abroad, and the schools that host it.
     */
    public record FieldReach(Field field, double researchers, double labResearchers, double idle, int inReach, List<String> touches, int foundHere,
                             int fromAbroad, List<SchoolCount> schools) {

        /** Everyone at the field's research this month: the city's researchers and the labs'. */
        public double total() {
            return researchers + labResearchers;
        }

        /** The people at work on a problem: everyone less the idle. */
        public double working() {
            return Math.max(0, total() - idle);
        }

        /** "1 in reach: rail", or "nothing in reach" (reachWords()). */
        public String reachWords() {
            return ResearchView.reachWords(inReach, touches);
        }

        /** "2 Institutes of Technology, 2 Universities" (schoolsWords()). */
        public String schoolsWords() {
            return ResearchView.schoolsWords(schools);
        }
    }

    /**
     * The Overview page: a row for each field in the enum's order, the month's research bill and the prizes the treasury paid the labs, the discoveries
     * found here and come from abroad since the founding, the researchers there are to each 1,000 people at work (NaN where nobody works), and the
     * posts the grants and pots bought this month, all fields together (Research.posts(); none where the researchers are the faculty's own alone).
     */
    public record Overview(List<FieldReach> fields, double researchBill, double prizesPaid, int foundHere, int fromAbroad, double perThousandWorkers,
                           int posts) {

        /** The people at research in all the fields, the labs' too. */
        public double researchers() {
            double sum = 0;
            for (FieldReach r : fields) sum += r.total();
            return sum;
        }

        /** The idle among them. */
        public double idle() {
            double sum = 0;
            for (FieldReach r : fields) sum += r.idle();
            return sum;
        }

        /** The people at work on a problem in all the fields: everyone less the idle (R3b's Overview shows it, so the screen adds nothing up). */
        public double working() {
            double sum = 0;
            for (FieldReach r : fields) sum += r.working();
            return sum;
        }

        /** The problems open to the city in all the fields. */
        public int inReach() {
            int sum = 0;
            for (FieldReach r : fields) sum += r.inReach();
            return sum;
        }
    }

    /** The words for what is in reach: "nothing in reach" for none, else "2 in reach: Wind Farm; Coal Power Plant", the distinct things the problems touch, in the catalogue's order. */
    public static String reachWords(int inReach, List<String> touches) {
        if (inReach <= 0) return "nothing in reach";
        return inReach + " in reach" + (touches == null || touches.isEmpty() ? "" : ": " + String.join("; ", touches));
    }

    /** What a list of problems touches, as the catalogue words it (Entry.touches()): each distinct text once, in the order of the problems. */
    static List<String> touchesOf(List<Entry> problems) {
        List<String> out = new ArrayList<>();
        for (Entry e : problems) {
            String t = e.touches() == null ? "" : e.touches().trim();
            if (!t.isEmpty() && !out.contains(t)) out.add(t);
        }
        return out;
    }

    /** The words for the schools that host a field: "2 Institutes of Technology, 2 Universities", or "no school" where none stands. */
    public static String schoolsWords(List<SchoolCount> schools) {
        if (schools == null || schools.isEmpty()) return "no school";
        List<String> parts = new ArrayList<>();
        for (SchoolCount s : schools) parts.add(s.count() + " " + (s.count() == 1 ? s.name() : plural(s.name())));
        return String.join(", ", parts);
    }

    /** A school's name in the plural: "University" to "Universities", "Institute of Technology" to "Institutes of Technology", "Law School" to "Law Schools". */
    static String plural(String name) {
        int of = name.indexOf(" of ");
        String head = of >= 0 ? name.substring(0, of) : name, tail = of >= 0 ? name.substring(of) : "";
        boolean consonantY = head.length() > 1 && head.endsWith("y") && "aeiou".indexOf(Character.toLowerCase(head.charAt(head.length() - 2))) < 0;
        return (consonantY ? head.substring(0, head.length() - 1) + "ies" : head.endsWith("s") ? head + "es" : head + "s") + tail;
    }

    /** The standing schools that host a field, the field's own kind first and the Universities after, each by its template's name and how many stand. */
    static List<SchoolCount> schoolsOf(Game game, Field f) {
        List<SchoolCount> own = new ArrayList<>(), universities = new ArrayList<>();
        for (BuildingsStacks stack : game.getBuildingManager().getStacksView()) {
            BuildingsTemplate t = stack.getBuilding();
            if (stack.getQuantity() <= 0 || t.getTeaches() == null || !Research.staffByField(t.getTeaches(), null).containsKey(f)) continue;
            (t.getTeaches() == EducationType.UNIVERSITY ? universities : own).add(new SchoolCount(t.getName(), stack.getQuantity()));
        }
        own.addAll(universities);
        return own;
    }

    /**
     * One field's row of the Overview (the core): the research as the last month left it, the problems open to the field at `month` and what they touch,
     * and the field's part of the timeline. `schools` is what hosts it; `researching` is false in a city founded the default way, where every discovery is
     * known from the start and nothing is open, whatever the catalogue's dates say of the month.
     */
    static FieldReach reach(Research research, int month, Field f, List<Discovery> timeline, List<SchoolCount> schools, boolean researching) {
        List<Entry> open = researching ? research.openProblems(f, month) : new ArrayList<>();
        int here = 0, abroad = 0;
        for (Discovery d : timeline) {
            if (d.field() != f) continue;
            if (d.source() == Source.HERE) here++;
            else abroad++;
        }
        return new FieldReach(f, research.researchers(f), research.labResearchers(f), research.idle(f) + research.labsIdle(f), open.size(), touchesOf(open), here,
                abroad, schools);
    }

    /** Researchers to each 1,000 people at work (the core): NaN where nobody works. */
    public static double perThousandWorkers(double researchers, long workers) {
        return workers > 0 ? 1000 * researchers / workers : Double.NaN;
    }

    /**
     * The Overview page of the city: each field's researchers (the city's own and the labs'), the idle, the problems in reach and what they touch, the
     * discoveries here and from abroad, and the schools that host it; the month's research bill (EconomyManager.getResearchBill()) and the prizes paid
     * the labs (Game.getPrizePaid()); the researchers to each 1,000 at work (the jobs filled, as the OECD's researchers per thousand employed). In a
     * city founded the default way every discovery is known, so every field has nothing in reach and nothing found, whatever its posts.
     */
    public static Overview overview(Game game) {
        Research research = game.getResearch();
        int month = game.getMonth();
        List<Discovery> timeline = timeline(game);
        List<FieldReach> rows = new ArrayList<>();
        double people = 0;
        int posts = 0;
        for (Field f : Field.values()) {
            FieldReach row = reach(research, month, f, timeline, schoolsOf(game, f), game.foundedUnfitted());
            rows.add(row);
            people += row.total();
            posts += research.posts(f);
        }
        return new Overview(rows, game.getEconomyManager().getResearchBill(), game.getPrizePaid(), count(timeline, Source.HERE), count(timeline, Source.ABROAD),
                perThousandWorkers(people, game.getPopulationManager().getJobsFilled()), posts);
    }

    /* =====================================================================
       THE FUNDING PAGE (R3b; docs/studies/research-design.md 5.1, 5.4)
       ===================================================================== */

    /**
     * One field's dial card as it stands: the grant and the prize, the posts struck this month (the grant's own and the pot's), the room the schools have
     * for posts and the grant that would fill it, the researchers the faculty and the filled posts give, what the field's posts cost this month and the
     * part of the grant they did not use, the labs standing and on site and the least prize that would bring one (NaN where none could be).
     */
    public record Funding(Field field, double grant, double prize, int posts, int grantPosts, double room, double postCost, double grantToFillRoom,
                          double faculty, double postResearchers, double bill, double unspent, int labsStanding, int labsOnSite, double labThreshold) {

        /** The posts the field's pot bought beyond the grant (Research.strikePosts()). */
        public int potPosts() {
            return posts - grantPosts;
        }

        /** Everyone at the field's own research at these dials: the faculty and the posts' researchers. */
        public double researchers() {
            return faculty + postResearchers;
        }

        /** The whole posts the schools have room for (Research.posts() buys whole posts and never past the room): "room for 59 posts". */
        public int roomPosts() {
            return room > 0 && Double.isFinite(room) ? (int) Math.min(Integer.MAX_VALUE, Math.floor(room)) : 0;
        }
    }

    /** What a grant would do, staged and not applied: the posts it buys, the researchers they and the faculty give and what the posts cost a month. */
    public record AtGrant(double grant, int posts, int grantPosts, double postResearchers, double researchers, double bill) { }

    /**
     * The dials a player has staged and not applied: a grant and a prize for the fields the player has moved. A field missing from a map is as it is. Copied, so
     * the maps given are not kept.
     */
    public record Staged(Map<Field, Double> grants, Map<Field, Double> prizes) {

        public Staged {
            grants = grants == null ? new EnumMap<>(Field.class) : new EnumMap<>(grants);
            prizes = prizes == null ? new EnumMap<>(Field.class) : new EnumMap<>(prizes);
        }

        /** Nothing staged: every dial as it is. */
        public static Staged none() {
            return new Staged(null, null);
        }

        /** The same with a field's grant staged. */
        public Staged withGrant(Field f, double grant) {
            Staged out = new Staged(grants, prizes);
            out.grants.put(f, grant);
            return out;
        }

        /** The same with a field's prize staged. */
        public Staged withPrize(Field f, double prize) {
            Staged out = new Staged(grants, prizes);
            out.prizes.put(f, prize);
            return out;
        }
    }

    /**
     * Everything the funding reads of a city, taken once: the research, the month, the wages and the fill the posts were struck at (null before any month:
     * every post then counts as filled), whether the city researches at all (founded the 1900 way), and the university-band staff of the standing schools by field, nominal (the room) and staffed (the faculty).
     */
    record Pieces(Research research, int month, boolean researching, double[] wages, double[] fill, Map<Field, Double> nominalStaff,
                   Map<Field, Double> staffedStaff) { }

    static Pieces piecesOf(Game game) {
        double[] fill = game.getResearchFill();
        return new Pieces(game.getResearch(), game.getMonth(), game.foundedUnfitted(), game.getPopulationManager().getWagesPerType(), fill, game.researchStaff(null), game.researchStaff(fill));
    }

    /** The posts a field's schools have room for (Research.room() of their university-band staff), and nothing where none stands. */
    static double roomOf(Pieces p, Field f) {
        return Research.room(p.nominalStaff().getOrDefault(f, 0.0));
    }

    /** The researcher-FTE a field's faculty give this month (Research.faculty() of the staffed staff). */
    static double facultyOf(Pieces p, Field f) {
        return Research.faculty(p.staffedStaff().getOrDefault(f, 0.0));
    }

    /**
     * The least prize that would bring a lab to a field (the core): the least remaining work of an open problem that a team at its limit could finish
     * before the world, times twelve researcher-years and a post's cost (the purse's rule: Research.purseCovers()); NaN where no open problem could be
     * finished in time, or a post has no cost.
     */
    static double labThreshold(Research research, int month, Field f, double[] wages) {
        double cost = Research.postCost(f, wages), least = Double.NaN;
        if (!(cost > 0)) return Double.NaN;
        for (Entry e : research.openProblems(f, month)) {
            double pile = research.pile(e.code());
            if (!Research.beforeTheWorld(e, pile, month)) continue;
            double needs = Research.remaining(e, pile) * Research.MONTHS_A_YEAR * cost;
            if (Double.isNaN(least) || needs < least) least = needs;
        }
        return least;
    }

    /** One field's dial card as it stands (the core), the labs being the ones standing and on site. */
    static Funding funding(Pieces p, Field f, int labsStanding, int labsOnSite) {
        Research r = p.research();
        double cost = Research.postCost(f, p.wages()), room = roomOf(p, f), grant = r.grant(f);
        int posts = r.posts(f), grantPosts = r.grantPosts(f);
        // ...the grant's own posts' bill is what the grant paid; the pot's posts are paid from the pot (Research.drawPots()).
        double bill = Research.bill(f, posts, p.wages(), p.fill());
        double used = Research.bill(f, grantPosts, p.wages(), p.fill());
        return new Funding(f, grant, r.prize(f), posts, grantPosts, room, cost, room * cost, facultyOf(p, f), Research.filledResearchers(f, posts, p.fill()), bill,
                Math.max(0, grant - used), labsStanding, labsOnSite, p.researching() ? labThreshold(r, p.month(), f, p.wages()) : Double.NaN);
    }

    /** One field's dial card as it stands in the city. */
    public static Funding funding(Game game, Field f) {
        return funding(piecesOf(game), f, game.labsStanding(f), game.labsOnSite(f));
    }

    /**
     * What a grant in a field would do (the core): the posts Research.posts() says it and the field's pot buy at the wages now within the room, those the
     * grant alone buys, the researchers the filled ones give at the fill the posts were last struck at (full where there is none yet), those and the
     * faculty together, and what the posts cost a month. The grant already in force buys the posts already struck, not the posts the wages of today would
     * buy: staging what is set changes nothing (wages move between a strike and a read, and a card that showed a change for no change would be wrong).
     */
    static AtGrant atGrant(Pieces p, Field f, double grant) {
        double cost = Research.postCost(f, p.wages()), room = roomOf(p, f);
        Research r = p.research();
        boolean inForce = grant == r.grant(f);
        int posts = inForce ? r.posts(f) : Research.posts(grant + r.pot(f), cost, room), grantPosts = inForce ? r.grantPosts(f) : Research.posts(grant, cost, room);
        double postResearchers = Research.filledResearchers(f, posts, p.fill());
        return new AtGrant(grant, posts, grantPosts, postResearchers, facultyOf(p, f) + postResearchers, Research.bill(f, posts, p.wages(), p.fill()));
    }

    /** What a staged grant in a field would do in the city. */
    public static AtGrant atGrant(Game game, Field f, double grant) {
        return atGrant(piecesOf(game), f, grant);
    }

    /* ----- the preview ----- */

    /**
     * The dials a preview is made at, field by field (by Field.ordinal()): the researchers held fixed (the faculty and the filled posts of the grant), the
     * prize and the share of a lab's posts filled; and the wages. The staged dials replace the city's own for the fields they name: a staged grant buys the
     * posts atGrant() says, and a field not staged keeps the posts struck.
     */
    record Dials(double[] researchers, double[] prize, double[] labFill, double[] wages) { }

    static Dials dialsOf(Pieces p, Staged staged) {
        Staged s = staged == null ? Staged.none() : staged;
        int n = Field.values().length;
        double[] researchers = new double[n], prize = new double[n], labFill = new double[n];
        for (Field f : Field.values()) {
            int k = f.ordinal();
            Double grant = s.grants().get(f);
            researchers[k] = grant == null
                    ? facultyOf(p, f) + Research.filledResearchers(f, p.research().posts(f), p.fill())
                    : atGrant(p, f, grant).researchers();
            Double stagedPrize = s.prizes().get(f);
            prize[k] = stagedPrize == null ? p.research().prize(f) : stagedPrize;
            labFill[k] = p.fill() == null ? 1 : Research.filledResearchers(f, Research.LAB_POSTS, p.fill()) / Research.LAB_POSTS;
        }
        return new Dials(researchers, prize, labFill, p.wages().clone());
    }

    /** The preview's answers kept, the least recently read dropped first: a card's dials are read again at every redraw of the screen, and the answer does not change until a dial or the month does. */
    private static final int CACHE_SIZE = 32;
    private static final Map<Object, ResearchPreview.Result> CACHE = new java.util.LinkedHashMap<>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Object, ResearchPreview.Result> eldest) {
            return size() > CACHE_SIZE;
        }
    };

    private static List<Double> boxed(double[]... arrays) {
        List<Double> out = new ArrayList<>();
        for (double[] a : arrays) for (double x : a) out.add(x);
        return out;
    }

    /**
     * The preview at some dials (the core): ResearchPreview.preview() of the research at the month, CACHED. The key is everything the answer turns on,
     * the month, the work piled, the finds, the city's share of each problem, the dials, the draws and the seed, so the same inputs return the same
     * Result without running again, and a new month or a changed input runs afresh.
     */
    static ResearchPreview.Result preview(Research research, int month, Dials dials, int draws, long seed) {
        List<Object> key = List.of(month, research.piles(), research.foundHere(), research.citySharesForSave(),
                boxed(dials.researchers(), dials.prize(), dials.labFill(), dials.wages()), draws, seed);
        synchronized (CACHE) {
            ResearchPreview.Result kept = CACHE.get(key);
            if (kept != null) return kept;
            EnumMap<Field, Double> researchers = new EnumMap<>(Field.class), prize = new EnumMap<>(Field.class), fill = new EnumMap<>(Field.class);
            for (Field f : Field.values()) {
                researchers.put(f, dials.researchers()[f.ordinal()]);
                prize.put(f, dials.prize()[f.ordinal()]);
                fill.put(f, dials.labFill()[f.ordinal()]);
            }
            ResearchPreview.Result made = ResearchPreview.preview(research, month, researchers, prize, dials.wages(), fill, draws, seed);
            CACHE.put(key, made);
            return made;
        }
    }

    /**
     * What the city's research would find here over the next ResearchPreview.YEARS years at the dials as they are, or with some staged (null or none:
     * as they are): ResearchPreview.DRAWS draws from ResearchPreview.SEED, cached. A card shows its field's figures at the dials as they are and at the
     * ones staged. In a city founded the default way every discovery is known, so there is nothing to find.
     */
    public static ResearchPreview.Result preview(Game game, Staged staged) {
        if (!game.foundedUnfitted()) return ResearchPreview.NOTHING;
        return preview(game.getResearch(), game.getMonth(), dialsOf(piecesOf(game), staged), ResearchPreview.DRAWS, ResearchPreview.SEED);
    }

    /* ----- the preview's words ----- */

    /**
     * Whether a card shows its preview as a chance and not a count: when the larger of the expected finds before and after the staged dials is under 1,
     * a chance of at least one is what is worth saying ("0.2% to 25%"); from 1 up the expected number is.
     */
    public static boolean asChance(double before, double after) {
        return Math.max(before, after) < 1;
    }

    /**
     * The preview in words: the chance of at least one find as a whole percent when `asChance`, else the expected finds to two places. "none" for
     * exactly nothing, and "under 1%" or "under 0.01" for what is above nothing and would show as 0% or 0.00.
     */
    public static String previewWords(double finds, double chance, boolean asChance) {
        if (asChance) {
            if (!(chance > 0)) return "none";
            return chance * 100 < 0.5 ? "under 1%" : Math.round(chance * 100) + "%";
        }
        if (!(finds > 0)) return "none";
        return finds < 0.005 ? "under 0.01" : String.format(Locale.ROOT, "%.2f", finds);
    }

    /* =====================================================================
       WHAT THE SCREENS' CARDS ARE DRAWN FROM (R3b, slice B: "no arithmetic on model figures in ui/")

       The Overview's four figures, the Funding page's dials and the rows of a field's card each need a figure that is a sum, a rounding or a choice
       among the reads above; the screen does not make them, because a harness can hold them here to the model's own numbers.
       ===================================================================== */

    /** "engineering 6 · medicine 2 · business and law none": the problems in reach by field, the fields with none named together at the end. */
    public static String reachByFieldWords(List<FieldReach> fields) {
        List<String> some = new ArrayList<>(), none = new ArrayList<>();
        for (FieldReach r : fields) {
            String name = r.field().name().toLowerCase(Locale.ROOT);
            if (r.inReach() > 0) some.add(name + " " + r.inReach());
            else none.add(name);
        }
        if (some.isEmpty()) return "nothing in reach in any field";
        if (none.isEmpty()) return String.join(" · ", some);
        String last = none.get(none.size() - 1), rest = String.join(", ", none.subList(0, none.size() - 1));
        return String.join(" · ", some) + " · " + (rest.isEmpty() ? last : rest + " and " + last) + " none";
    }

    /** A dial's range: from `min` to `max` in steps of `step`. */
    public record Dial(double min, double max, double step) { }

    /** How far past the room the grant dial reaches, as a share of the room, and the least number of posts it reaches past it (a grant past the room buys nothing, and the player may try one). */
    public static final double GRANT_REACH = 1.2;
    public static final int GRANT_REACH_POSTS = 3;

    /**
     * The grant dial of a field: from nothing in steps of a round figure (1, 2 or 5 times a power of ten) not above a post's cost, to a little past the
     * grant that fills the room (GRANT_REACH) - and past the grant in force, whatever the room has become. A step of a post's cost itself would have been
     * a post a step, but the cost walks with the wages and a month landing under a held thumb would move every step of the dial under it; a round step
     * stays put until the cost passes one. Where a post has no cost (no wages to read) a plain dial of a hundred steps.
     */
    public static Dial grantDial(Funding f) {
        double cost = f.postCost();
        if (!(cost > 0) || !Double.isFinite(cost)) return new Dial(0, Math.max(100, Math.ceil(f.grant())), 1);
        int room = f.roomPosts();
        double posts = Math.max(Math.max(room + GRANT_REACH_POSTS, Math.ceil(room * GRANT_REACH)), Math.ceil(f.grant() / cost));
        double step = niceFloor(cost);
        return new Dial(0, Math.ceil(posts * cost / step - 1e-9) * step, step);
    }

    /** The greatest of 1, 2 and 5 times a power of ten that is not above `x` (0 for x not above nothing). */
    static double niceFloor(double x) {
        if (!(x > 0) || !Double.isFinite(x)) return 0;
        double power = Math.pow(10, Math.floor(Math.log10(x)));
        for (double m : new double[] {5, 2, 1}) if (m * power <= x * (1 + 1e-12)) return m * power;
        return power;
    }

    /** The researcher-years of work a prize dial reaches for, at the least: a problem of that size, paid at a post's cost a month, is its top. */
    public static final double PRIZE_DIAL_YEARS = 1000;

    /** How many times the least prize that brings a lab the prize dial reaches, at the least. */
    public static final double PRIZE_REACH = 2;

    /** The steps a prize dial is divided into. */
    public static final int PRIZE_STEPS = 100;

    /** The least of 1, 2 and 5 times a power of ten that is not below `x` (0 for x not above nothing). */
    static double niceCeil(double x) {
        if (!(x > 0) || !Double.isFinite(x)) return 0;
        double power = Math.pow(10, Math.floor(Math.log10(x)));
        for (double m : new double[] {1, 2, 5, 10}) if (m * power >= x * (1 - 1e-12)) return m * power;
        return 10 * power;
    }

    /**
     * The prize dial of a field: from nothing to a round number (1, 2 or 5 times a power of ten) that reaches PRIZE_DIAL_YEARS researcher-years of a post's
     * cost, twice the least prize that brings a lab (PRIZE_REACH) and the prize in force; in PRIZE_STEPS steps. Where a post has no cost a plain dial.
     */
    public static Dial prizeDial(Funding f) {
        double cost = f.postCost();
        double base = cost > 0 && Double.isFinite(cost) ? PRIZE_DIAL_YEARS * Research.MONTHS_A_YEAR * cost : 1000;
        double lab = Double.isNaN(f.labThreshold()) ? 0 : PRIZE_REACH * f.labThreshold();
        double max = niceCeil(Math.max(Math.max(base, lab), f.prize()));
        return new Dial(0, max, max / PRIZE_STEPS);
    }

    /** The researchers that `labs` Research Labs give once they are staffed: Research.LAB_POSTS posts each. */
    public static int labPosts(int labs) {
        return Math.max(0, labs) * Research.LAB_POSTS;
    }

    /** THE BUDGET a month before and after what is staged: the balance as the accounts booked it and the same less what the staged grants' posts would add to the research bill. */
    public record Budget(double before, double after) {
        public double change() {
            return after - before;
        }
    }

    /**
     * The balance with the staged grants in place of the ones in force: a staged grant buys the posts atGrant() says, and their bill replaces the bill of the
     * posts struck. A prize is paid on a find and not a month, so it moves nothing here.
     */
    public static Budget budget(Game game, Staged staged) {
        double before = game.getEconomyManager().getNationalAccounts().getBalance();
        Pieces p = piecesOf(game);
        double now = 0, then = 0;
        if (staged != null) {
            for (Map.Entry<Field, Double> e : staged.grants().entrySet()) {
                now += atGrant(p, e.getKey(), p.research().grant(e.getKey())).bill();
                then += atGrant(p, e.getKey(), e.getValue()).bill();
            }
        }
        return new Budget(before, before - (then - now));
    }

    /**
     * One field's dial card as the Funding page draws it, at the dials shown on it (the grant and prize in force, or staged ones): the researchers the
     * field has now and with them, in grant posts and in labs, what the posts cost a month, the part of the grant they leave unspent, and what the field
     * would find here before the world over the next ResearchPreview.YEARS years. `nowResult` and `afterResult` are the previews at the dials in force and
     * at the dials shown (the same Result where nothing is staged): a screen reads each once for all its cards. A field not moved has the same figure
     * on both sides.
     */
    public record Card(Field field, FieldReach reach, Funding funding, AtGrant at, boolean researching, double grant, double prize, boolean grantMoved,
                       boolean prizeMoved, int roomPosts, double researchersNow, double researchersAfter, int grantPostsNow, int grantPostsAfter,
                       double labResearchersNow, double labResearchersAfter, int labsNow, int labsAfter, double billNow, double billAfter,
                       double unspentNow, double unspentAfter, double findsNow, double findsAfter, double chanceNow, double chanceAfter) {

        /** Whether something is staged on the card. */
        public boolean moved() {
            return grantMoved || prizeMoved;
        }

        /** Whether the card shows its preview as a chance and not a count (asChance() of the two figures). */
        public boolean asChance() {
            return ResearchView.asChance(findsNow, findsAfter);
        }

        /** "none", "0.2%", "1.10": the preview at the dials in force. */
        public String findsNowWords() {
            return previewWords(findsNow, chanceNow, asChance());
        }

        /** ...and at the dials shown. */
        public String findsAfterWords() {
            return previewWords(findsAfter, chanceAfter, asChance());
        }

        /** Whether the shown grant buys every post the room has: more money buys nothing. */
        public boolean roomFull() {
            return roomPosts > 0 && grantPostsAfter >= roomPosts;
        }

        /** Whether some of the shown grant goes unspent: at least half a dollar of it (the money is counted in thousands). */
        public boolean unspends() {
            return unspentAfter >= 0.0005;
        }
    }

    /** The field's card at the dials `staged` names (a dial it does not name is as it is in force): see Card. */
    public static Card card(Game game, Field f, Staged staged, FieldReach reach, ResearchPreview.Result nowResult, ResearchPreview.Result afterResult) {
        Pieces p = piecesOf(game);
        Research r = p.research();
        Funding fu = funding(p, f, game.labsStanding(f), game.labsOnSite(f));
        Staged s = staged == null ? Staged.none() : staged;
        Double stagedGrant = s.grants().get(f), stagedPrize = s.prizes().get(f);
        double grant = stagedGrant == null ? r.grant(f) : stagedGrant, prize = stagedPrize == null ? r.prize(f) : stagedPrize;
        boolean grantMoved = stagedGrant != null && stagedGrant != r.grant(f), prizeMoved = stagedPrize != null && stagedPrize != r.prize(f);
        AtGrant at = atGrant(p, f, grant);
        int labsAfter = prizeMoved ? afterResult.labsFirstMonth(f) : fu.labsStanding();
        double labsNowResearchers = reach.labResearchers();
        double labsAfterResearchers = labsAfter == fu.labsStanding() ? labsNowResearchers : labPosts(labsAfter);
        double unspentAfter = Math.max(0, grant - Research.bill(f, at.grantPosts(), p.wages(), p.fill()));
        return new Card(f, reach, fu, at, p.researching(), grant, prize, grantMoved, prizeMoved, fu.roomPosts(), fu.researchers() + labsNowResearchers,
                at.researchers() + labsAfterResearchers, fu.grantPosts(), at.grantPosts(), labsNowResearchers, labsAfterResearchers, fu.labsStanding(),
                labsAfter, fu.bill(), at.bill(), fu.unspent(), unspentAfter, nowResult.finds(f), afterResult.finds(f), nowResult.chance(f), afterResult.chance(f));
    }

    /* ----- the schools ----- */

    /** The posts of a field that a kind of school carries: the posts struck in the field by this kind's share of its room, the researchers they give, and the room this kind has. */
    public record SchoolPosts(Field field, double posts, double filled, double room) { }

    /** Whether a kind of school hosts research in any field (the University, and the Institute of technology, Medical, Business and Law schools). */
    public static boolean hostsResearch(EducationType kind) {
        return kind != null && !Research.staffByField(kind, null).isEmpty();
    }

    /** The nominal university-band staff a kind of school stands up in each field, as Game.researchStaff(null) sums them over every kind. */
    static EnumMap<Field, Double> staffOfKind(Game game, EducationType kind) {
        EnumMap<Field, Double> out = new EnumMap<>(Field.class);
        for (BuildingsStacks stack : game.getBuildingManager().getStacksView()) {
            BuildingsTemplate t = stack.getBuilding();
            if (t.getTeaches() != kind || stack.getQuantity() <= 0) continue;
            double[] staff = new double[JobType.values().length];
            for (JobType type : JobType.values()) {
                if (Research.servedBy(type).isEmpty()) continue;
                staff[type.ordinal()] = (double) t.getJobs(type) * stack.getQuantity();
            }
            for (Map.Entry<Field, Double> share : Research.staffByField(kind, staff).entrySet()) out.merge(share.getKey(), share.getValue(), Double::sum);
        }
        return out;
    }

    /**
     * What a kind of school carries of the city's research posts: posts are struck by field and not by school, so a kind has its share of each field it
     * hosts - the posts struck there and the researchers they give, by the kind's share of the field's room - and the room it has itself. A field a
     * kind hosts but has no staff in (none of it stands) is left out; a kind that hosts none, or of which none stands, has an empty list.
     */
    public static List<SchoolPosts> schoolPosts(Game game, EducationType kind) {
        List<SchoolPosts> out = new ArrayList<>();
        if (!hostsResearch(kind)) return out;
        Pieces p = piecesOf(game);
        EnumMap<Field, Double> mine = staffOfKind(game, kind);
        for (Field f : Field.values()) {
            double staff = mine.getOrDefault(f, 0.0);
            if (!(staff > 0)) continue;
            double whole = p.nominalStaff().getOrDefault(f, 0.0);
            double share = whole > 0 ? staff / whole : 0;
            int posts = p.research().posts(f);
            out.add(new SchoolPosts(f, share * posts, share * Research.filledResearchers(f, posts, p.fill()), Research.room(staff)));
        }
        return out;
    }

    /** A hosting kind of school standing in the city, as the Overview lists it: its name, how many stand and what it carries. */
    public record SchoolRow(EducationType kind, String name, int count, List<SchoolPosts> posts) { }

    /** The kinds of school that host research and stand in the city, in the education kinds' order. */
    public static List<SchoolRow> schoolRows(Game game) {
        List<SchoolRow> out = new ArrayList<>();
        for (EducationType kind : EducationType.values()) {
            if (!hostsResearch(kind)) continue;
            int count = 0;
            String name = null;
            for (BuildingsStacks stack : game.getBuildingManager().getStacksView()) {
                if (stack.getBuilding().getTeaches() != kind || stack.getQuantity() <= 0) continue;
                count += stack.getQuantity();
                name = stack.getBuilding().getName();
            }
            if (count > 0) out.add(new SchoolRow(kind, name, count, schoolPosts(game, kind)));
        }
        return out;
    }
}
