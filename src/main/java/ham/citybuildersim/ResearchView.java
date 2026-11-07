package ham.citybuildersim;

import ham.citybuildersim.ResearchCatalog.Entry;

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
    public record RecipeUse(BuildingsTemplate template, Recipe recipe, int running, int buildings, Discovery from) { }

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
}
