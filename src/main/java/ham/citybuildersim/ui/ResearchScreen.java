package ham.citybuildersim.ui;

import ham.citybuildersim.CityCalendar;
import ham.citybuildersim.ChartModel;
import ham.citybuildersim.EquipmentFit;
import ham.citybuildersim.Game;
import ham.citybuildersim.HistorySave;
import ham.citybuildersim.ResearchCatalog;
import ham.citybuildersim.ResearchCatalog.Field;
import ham.citybuildersim.ResearchView;
import ham.citybuildersim.ResearchView.Discovery;
import ham.citybuildersim.ResearchView.Source;
import ham.citybuildersim.Sector;
import ham.citybuildersim.BuildingsTemplate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import static ham.citybuildersim.ui.Pieces.*;

/**
 * Policy › Research (R5, slice B1): the sixth Policy area, drawn in Policy's own style - at this slice its one page, Found, what the city has
 * discovered and what reached it from abroad.
 *
 * WHY. Research-design 5.5 and 6.2 and mockup 3: "no tree to click". A player who funds research (R3b's dial cards) and a player who never does both
 * need to see what the city knows and how it came to know it, on one screen: every discovery on a timeline by field (a hollow dot from abroad on
 * history's date, a filled one found here with a line to where the world got it and how far ahead the city was), then the recipes and the equipment
 * now at work in its buildings, and the researchers, the money and the equipment's spread over time. Every figure is ResearchView's, ResearchCatalog's
 * or HistorySave's: nothing here adds up a thing the model has not already. In a city founded the default way (every discovery known from the start,
 * R3 call 2) there is nothing to show, and the page says so in one quiet card.
 *
 * DRAWN KEPT (KP6's way): PolicyScreen's pool serves the page, each section is a scope, each row a scope keyed by what it shows (a code, a template and
 * its piece, a recipe), so a discovery that arrives hands nobody's node to a neighbour. The timeline is a pane that PLACES its children when it is laid
 * out and not when it is drawn (Timeline): where a dot stands, how many dots stack and how tall a row is all depend on the width the pane is given, which a
 * draw does not know, and a page drawn kept and the same page built new must agree to the pixel at the width they are laid out at.
 *
 * FOR R3B: the Overview and Funding pages go in PAGES before FOUND (and PAGE_ICONS beside them), and page() switches on policy.policyPage; the hub's sixth
 * card, with the Overview it opens, is R3b's - this slice adds no card to the hub.
 */
final class ResearchScreen {

    private final PolicyScreen policy;
    private final UserInterface ui;

    ResearchScreen(PolicyScreen policy, UserInterface ui) {
        this.policy = policy;
        this.ui = ui;
    }

    /* =====================================================================
       THE PAGES
       ===================================================================== */

    /** The area's page: what has been discovered. */
    static final String FOUND = "Found";

    /** ...the area's pages, in their tabs' order (a strip of one tab draws no tabs). */
    static final String[] PAGES = {FOUND};

    /** ...and their icons. */
    static final String[] PAGE_ICONS = {Icons.REPORTS};

    /** The area's (i). */
    static final String AREA_INFO = "What the city's researchers have found, and what reached the city from abroad on history's date: every "
            + "discovery on one timeline by field, the recipes and the equipment now at work in its buildings, and the researchers, the money and "
            + "the equipment's spread over the years.";

    /** The page open, drawn into `page` (R3b: its Overview and Funding pages are switched here, before Found). */
    void page(VBox page) {
        foundPage(page);
    }

    /* =====================================================================
       THE FOUND PAGE (mockup 3)
       ===================================================================== */

    static final String TIMELINE_INFO = "Every discovery the city has come to know since its founding, a row a field. A hollow dot came from abroad "
            + "on history's date. A filled dot was found here, before the world had it: its line runs to the date the world got it, and the figure "
            + "beside it is how many years ahead the city was. Hover a dot for what it touches and what it does.";

    static final String FOUND_INFO = "What the city's own researchers found before the world did, the newest first, and how many years before.";

    static final String RECIPES_INFO = "The ways of running a building, other than its usual one, that the city knows: how many of that building's "
            + "stack run it now, and where it came from.";

    static final String EQUIPMENT_INFO = "The pieces of equipment the city knows, on the buildings they suit: how many of those buildings have the "
            + "piece fitted, and the sector's own verdict on fitting the rest. A row opens the sector's Investors page.";

    static final String OVER_TIME_INFO = "Four charts of the city's research since its founding: the researchers at work, what research cost, what was "
            + "found here against what came from abroad, and how much of the equipment standing has been fitted.";

    void foundPage(VBox page) {
        Game g = ui.game;
        if (!g.foundedUnfitted()) {
            page.getChildren().add(Kept.scope("default", this::defaultCard));
            return;
        }
        List<Discovery> timeline = ResearchView.timeline(g);
        int here = ResearchView.count(timeline, Source.HERE), abroad = ResearchView.count(timeline, Source.ABROAD);
        page.getChildren().add(Kept.scope("lead", () -> leadCard(here, abroad)));

        page.getChildren().add(sectionHead("THE TIMELINE", TIMELINE_INFO, PolicyScreen.quiet("hollow: from abroad · filled: found here")));
        page.getChildren().add(Kept.scope("timeline", () -> timelineCard(timeline)));

        // Found here, the newest first.
        List<Discovery> found = new ArrayList<>();
        for (Discovery d : timeline) if (d.source() == Source.HERE) found.add(0, d);
        page.getChildren().add(sectionHead("FOUND HERE", FOUND_INFO, PolicyScreen.quiet(here + " so far")));
        if (found.isEmpty()) {
            page.getChildren().add(Kept.scope("found none", () -> PolicyScreen.muted("Nothing found here yet.")));
        } else {
            page.getChildren().add(Kept.scope("found list", () -> {
                VBox list = PolicyScreen.card();
                list.setSpacing(0);
                for (Discovery d : found) list.getChildren().add(Kept.scope("found:" + d.entry().code(), () -> foundRow(d)));
                return list;
            }));
        }

        List<ResearchView.RecipeUse> recipes = ResearchView.recipesInUse(g);
        page.getChildren().add(sectionHead("RECIPES IN USE", RECIPES_INFO, PolicyScreen.quiet(recipes.size() + (recipes.size() == 1 ? " way" : " ways"))));
        if (recipes.isEmpty()) {
            page.getChildren().add(Kept.scope("recipes none", () -> PolicyScreen.muted("No building runs a new way yet.")));
        } else {
            page.getChildren().add(Kept.scope("recipes list", () -> {
                VBox list = PolicyScreen.card();
                list.setSpacing(0);
                for (ResearchView.RecipeUse u : recipes) {
                    list.getChildren().add(Kept.scope("recipe:" + u.template().getId() + ":" + u.recipe().getId(), () -> recipeRow(u)));
                }
                return list;
            }));
        }

        List<ResearchView.PieceRow> pieces = ResearchView.equipment(g, null);
        page.getChildren().add(sectionHead("EQUIPMENT FITTED", EQUIPMENT_INFO, PolicyScreen.quiet(pieces.size() + (pieces.size() == 1 ? " piece" : " pieces"))));
        if (pieces.isEmpty()) {
            page.getChildren().add(Kept.scope("equipment none", () -> PolicyScreen.muted(
                    "Nothing fitted yet: the first rows appear when a sector fits its first piece.")));
        } else {
            page.getChildren().add(Kept.scope("equipment list", () -> {
                VBox list = PolicyScreen.card();
                list.setSpacing(0);
                for (ResearchView.PieceRow r : pieces) {
                    list.getChildren().add(Kept.scope("piece:" + r.template().getId() + ":" + r.piece().getId(), () -> pieceRow(r)));
                }
                return list;
            }));
        }

        page.getChildren().add(sectionHead("OVER TIME", OVER_TIME_INFO, PolicyScreen.quiet("since the founding")));
        page.getChildren().add(Kept.scope("over time", () -> overTime(timeline)));
    }

    /** What a city founded the default way says: it knew every discovery from the start, so nothing arrives and nothing is found. */
    VBox defaultCard() {
        int all = ResearchView.knownAtFounding(ui.game);
        return PolicyScreen.card(
                PolicyScreen.cardHead(Icons.FLASK, Palette.DISCOVERY, "NOTHING TO FIND", null, null),
                PolicyScreen.words("This city was founded knowing all " + all + " discoveries in the catalogue, so nothing arrives from abroad and "
                        + "nothing is found here.", Palette.SIZE_BODY + 1, Palette.TEXT_LABEL),
                PolicyScreen.muted("A grant still buys research posts, but their researchers have nothing to work on: this page fills in a city "
                        + "founded the 1900 way."));
    }

    /** The lead: how many the city found itself and how many came from abroad, and how many it knew at the founding. */
    VBox leadCard(int here, int abroad) {
        int all = ResearchCatalog.get().entries().size();
        int known = ResearchView.knownAtFounding(ui.game);
        int toCome = Math.max(0, all - known - here - abroad);
        // ...the founding's share said only where it knew some: no catalogue entry is dated at or before a 1900 city's first month, so there it is none.
        String rest = toCome + " more of the catalogue's " + all + " arrive on history's date, or sooner if the city's own researchers get there first.";
        return PolicyScreen.card(
                PolicyScreen.cardHead(Icons.FLASK, Palette.DISCOVERY, "WHAT THE CITY KNOWS", null, null),
                PolicyScreen.words(here + " found here, " + abroad + " from abroad since the founding", Palette.SIZE_LEAD, Palette.TEXT_HEAD),
                PolicyScreen.muted(known > 0 ? "The city knew " + known + " at its founding; " + rest : rest));
    }

    /* ----- the words ----- */

    /** A field as it is read: "Engineering". */
    static String fieldName(Field f) {
        String n = f.name();
        return n.charAt(0) + n.substring(1).toLowerCase(Locale.ROOT);
    }

    /** A field's colour: engineering the money blue, medicine the people teal, business the business violet, law the sand (the mockup's). */
    static String fieldColour(Field f) {
        return switch (f) {
            case ENGINEERING -> Palette.MONEY;
            case MEDICINE -> Palette.PEOPLE;
            case BUSINESS -> Palette.BUSINESS;
            case LAW -> Palette.ORE;
        };
    }

    /** How the city came to know a discovery: "found here in June 1973, 4.6 years before the world" or "from abroad, June 1955". */
    static String whenWords(Discovery d) {
        return d.source() == Source.HERE
                ? String.format(Locale.ROOT, "found here in %s, %.1f years before the world", CityCalendar.format(d.month()), d.yearsAhead())
                : "from abroad, " + CityCalendar.format(d.month());
    }

    /** ...and "known since the founding" for what no discovery made known. */
    static String fromWords(Discovery d) {
        return d == null ? "known since the founding" : whenWords(d);
    }

    /** A dot's tooltip: the name, its field and how the city came to know it, what it touches and what it does. */
    static String dotWords(Discovery d) {
        StringBuilder b = new StringBuilder(d.entry().name()).append('\n').append(fieldName(d.field())).append(" · ").append(whenWords(d));
        if (d.entry().touches() != null && !d.entry().touches().isBlank()) b.append("\nTouches: ").append(d.entry().touches());
        if (d.entry().effect() != null && !d.entry().effect().isBlank()) b.append('\n').append(d.entry().effect());
        return b.toString();
    }

    /** "+1.5y": how many years ahead of the world a find was, one decimal. */
    static String aheadWords(Discovery d) {
        return String.format(Locale.ROOT, "+%.1fy", d.yearsAhead());
    }

    /** "12 of 20 buildings". */
    static String ofWords(int n, int of, String what) {
        return n + " of " + of + " " + what + (of == 1 ? "" : "s");
    }

    /** The sector's verdict on a piece, in words. */
    static String verdictWords(EquipmentFit.Kind kind) {
        return switch (kind) {
            case FITTED -> "all fitted";
            case PAYS -> "the next fit pays";
            case NOT_YET -> "not worth fitting yet";
            case NOT_KNOWN -> "not known yet";
        };
    }

    /** ...and its colour: a verdict says what to do, never how big. */
    static String verdictColour(EquipmentFit.Kind kind) {
        return switch (kind) {
            case FITTED -> Palette.GOOD;
            case PAYS -> Palette.ACCENT;
            case NOT_YET, NOT_KNOWN -> Palette.TEXT_MUTED;
        };
    }

    /* ----- the rows ----- */

    /** A fixed-width cell of a row: its lines one under another. */
    private static VBox cell(double width, Node... lines) {
        VBox c = Kept.column(1, lines);
        c.setMinWidth(width);
        c.setPrefWidth(width);
        c.setMaxWidth(width);
        return c;
    }

    /** A row's ground: a hairline under it. */
    private static final String ROW_STYLE = "-fx-padding: 5 6 5 6; -fx-border-color: " + Palette.HAIRLINE + "; -fx-border-width: 0 0 1 0;";

    /** ...and a row that is a door's: the pointer's hand and, under it, a ground. */
    private static final String DOOR_STYLE = ROW_STYLE + " -fx-background-radius: 6; -fx-cursor: hand;";
    private static final String DOOR_LIT = DOOR_STYLE + " -fx-background-color: " + Palette.CONTROL + ";";

    /** One find of the city's own: its name, its field, its month and how far ahead of the world. */
    HBox foundRow(Discovery d) {
        Label name = PolicyScreen.words(d.entry().name(), Palette.SIZE_BODY + 1, Palette.TEXT_BODY);
        Label field = chip(fieldName(d.field()), fieldColour(d.field()));
        Label when = PolicyScreen.figure(CityCalendar.format(d.month()), Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL);
        Label ahead = PolicyScreen.words(String.format(Locale.ROOT, "%.1f years before the world", d.yearsAhead()), Palette.SIZE_LABEL + 1,
                Palette.TEXT_HEAD);
        HBox row = Kept.row(12, cell(250, name), cell(90, field), cell(110, when), ahead);
        HBox.setHgrow(ahead, Priority.ALWAYS);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle(ROW_STYLE);
        return row;
    }

    /** One way of running a building: its recipe and building, how many of the stack run it as a share, and where it came from. */
    HBox recipeRow(ResearchView.RecipeUse u) {
        Label name = PolicyScreen.words(u.recipe().getName(), Palette.SIZE_BODY + 1, Palette.TEXT_BODY);
        Label building = PolicyScreen.muted(u.template().getName());
        double share = u.buildings() > 0 ? u.running() / (double) u.buildings() : 0;
        Region bar = Pieces.bar(share, Palette.DISCOVERY, 140, 8);
        Label count = PolicyScreen.figure(ofWords(u.running(), u.buildings(), "building"), Palette.SIZE_LABEL + 1, Palette.TEXT_HEAD);
        Label from = PolicyScreen.words(fromWords(u.from()), Palette.SIZE_LABEL + 1, Palette.TEXT_MUTED);
        HBox row = Kept.row(12, cell(250, name, building), cell(170, bar, count), from);
        HBox.setHgrow(from, Priority.ALWAYS);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle(ROW_STYLE);
        return row;
    }

    /** One piece on one stack: its name and building, how many of the buildings have it fitted as a share, the sector's verdict, and a door to the sector. */
    HBox pieceRow(ResearchView.PieceRow r) {
        BuildingsTemplate t = r.template();
        Label name = PolicyScreen.words(r.piece().getName(), Palette.SIZE_BODY + 1, Palette.TEXT_BODY);
        Label building = PolicyScreen.muted(t.getName());
        double share = r.buildings() > 0 ? r.fitted() / (double) r.buildings() : 0;
        Region bar = Pieces.bar(share, Palette.DISCOVERY, 140, 8);
        Label count = PolicyScreen.figure(r.fitted() + " of " + r.buildings() + " fitted", Palette.SIZE_LABEL + 1, Palette.TEXT_HEAD);
        VBox shareCell = cell(170, bar, count);
        if (r.onSite() > 0) shareCell.getChildren().add(Kept.scope("fitting", () -> PolicyScreen.muted(r.onSite() + " being fitted now")));
        Label verdict = PolicyScreen.words(verdictWords(r.verdict().kind()), Palette.SIZE_LABEL + 1, verdictColour(r.verdict().kind()));
        Label arrow = Kept.label("›");
        arrow.setStyle(Palette.strong(Palette.SIZE_HEADING + 2, Palette.TEXT_MUTED));
        HBox.setHgrow(verdict, Priority.ALWAYS);
        HBox row = Kept.row(12, cell(250, name, building), shareCell, verdict, arrow);
        row.setAlignment(Pos.CENTER_LEFT);
        Sector sector = t.isOwnedBySector() ? ui.game.getSectors().byKey(t.getSector()) : null;
        Kept.tip(row, r.piece().getName() + " on " + t.getName() + "\n" + fromWords(r.from())
                + (sector != null ? "\nClick for " + sector.label() + "'s Investors page." : ""), 300);
        if (sector != null) {
            // Kept (KP6): the look the pointer calls for is chosen from isHover() where it is set.
            row.setStyle(row.isHover() ? DOOR_LIT : DOOR_STYLE);
            row.setOnMouseEntered(e -> row.setStyle(DOOR_LIT));
            row.setOnMouseExited(e -> row.setStyle(DOOR_STYLE));
            row.setOnMouseClicked(e -> ui.sectorScreen.openSectorBooks(sector, "Investors"));
        } else {
            row.setStyle(ROW_STYLE);
        }
        return row;
    }

    /* =====================================================================
       THE TIMELINE
       ===================================================================== */

    /** A dot's width, and the gap two dots of a row keep from each other before one stacks above the other. */
    static final double DOT = 12, DOT_GAP = 3;

    /** How far a stacked dot stands above the one under it: the dot, the figure that sits over it and a margin. */
    static final double LEVEL = 26;

    /** The room a row's name takes at the left, the room kept at the right, and the least apart two year ticks stand. */
    static final double LABEL_W = 150, PAD_R = 16, TICK_ROOM = 60;

    /** A row's room over its top dot (the figure "+1.5y"), under its bottom one, and its least height (its two lines of words). */
    static final double ROW_TOP = 18, ROW_FOOT = 8, ROW_MIN = 46;

    /** The room under the last row for the years' names. */
    static final double AXIS_H = 20;

    /** Where a month stands across a plot this wide, from its first month to its last: 0 at the first, the width at the last, a month outside them held at the nearer end. */
    static double xOf(int month, int first, int last, double plotWidth) {
        int span = Math.max(1, last - first);
        double f = (month - first) / (double) span;
        return Math.max(0, Math.min(1, f)) * plotWidth;
    }

    /**
     * How high each dot of a row stands (pure): 0 on the row's floor, 1 above it, and so on. A dot takes the lowest level at which every dot already
     * there stands at least `slot` pixels from it, so two dots closer than their width stack upward rather than overlap, two far apart share the floor,
     * and a dot goes back down as soon as there is room. The months are taken in the order given (the timeline's: oldest first), so the same row at the
     * same width always stacks the same way.
     */
    static int[] levels(int[] months, int first, int last, double plotWidth, double slot) {
        int[] level = new int[months.length];
        double[] x = new double[months.length];
        for (int i = 0; i < months.length; i++) {
            x[i] = xOf(months[i], first, last, plotWidth);
            int l = 0;
            boolean taken = true;
            while (taken) {
                taken = false;
                for (int j = 0; j < i && !taken; j++) taken = level[j] == l && Math.abs(x[i] - x[j]) < slot;
                if (taken) l++;
            }
            level[i] = l;
        }
        return level;
    }

    /** The months of the year ticks (pure): a January in every `step`th year, the smallest step that keeps two ticks TICK_ROOM apart, inside the axis. */
    static int[] yearTicks(int first, int last, double plotWidth) {
        int span = Math.max(1, last - first);
        int[] steps = {1, 2, 5, 10, 20, 25, 50, 100};
        int step = steps[steps.length - 1];
        for (int s : steps) {
            if (plotWidth * s * 12.0 / span >= TICK_ROOM) {
                step = s;
                break;
            }
        }
        List<Integer> out = new ArrayList<>();
        int from = CityCalendar.yearOf(first);
        for (int y = ((from + step - 1) / step) * step; y <= CityCalendar.yearOf(last); y += step) {
            int m = 1 + 12 * (y - CityCalendar.EPOCH_YEAR);
            if (m >= first && m <= last) out.add(m);
        }
        int[] a = new int[out.size()];
        for (int i = 0; i < a.length; i++) a[i] = out.get(i);
        return a;
    }

    /** What a timeline child is: a field's name at the left, or a dot. */
    private enum Kind { LABEL, DOT }

    /**
     * Where a timeline child belongs, as the draw says it and layout places it: its row (a field's number); for a dot its month, the month its line
     * ends at (the world's, held at the city's month), its code, whether it was found here, its colour, the figure over it and whether its line runs
     * past the city's month.
     */
    private record Place(Kind kind, int row, int month, int to, String code, boolean here, String colour, String ahead, boolean past) { }

    /** The key a timeline child carries its Place under. */
    private static final String PLACE = "research.place";

    private static Place placeOf(Node n) {
        return n.getProperties().get(PLACE) instanceof Place p ? p : null;
    }

    /** The card with the timeline in it. */
    VBox timelineCard(List<Discovery> timeline) {
        int now = ui.game.getMonth();
        int first = ResearchView.FIRST_MONTH, last = Math.max(now, first + 12);
        Timeline line = Kept.of(Timeline.class, Timeline::new);
        line.set(first, last, Field.values().length);
        EnumMap<Field, List<Discovery>> byField = ResearchView.byField(timeline);
        for (Field f : Field.values()) line.getChildren().add(Kept.scope("row:" + f.name(), () -> rowLabel(f)));
        for (Field f : Field.values()) {
            for (Discovery d : byField.get(f)) line.getChildren().add(Kept.scope("dot:" + d.entry().code(), () -> dot(d, f.ordinal(), now)));
        }
        return PolicyScreen.card(line);
    }

    /** A field's name in its colour, and its school's words under it. */
    VBox rowLabel(Field f) {
        Label name = Kept.label(fieldName(f));
        name.setStyle(Palette.strong(Palette.SIZE_BODY + 1, fieldColour(f)));
        Label school = Kept.label(f.school());
        school.setWrapText(true);
        school.setMinWidth(0);
        school.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        VBox box = Kept.column(1, name, school);
        box.getProperties().put(PLACE, new Place(Kind.LABEL, f.ordinal(), 0, 0, null, false, null, null, false));
        return box;
    }

    /** A discovery as a dot: hollow from abroad, filled found here; its words the tooltip. */
    Region dot(Discovery d, int row, int now) {
        String colour = fieldColour(d.field());
        boolean here = d.source() == Source.HERE;
        Region r = Kept.region();
        r.setMinSize(DOT, DOT);
        r.setPrefSize(DOT, DOT);
        r.setMaxSize(DOT, DOT);
        String round = " -fx-background-radius: " + DOT / 2 + "; -fx-border-radius: " + DOT / 2 + "; -fx-border-color: " + colour + "; -fx-border-width: 2;";
        r.setStyle("-fx-background-color: " + (here ? colour : Palette.RAISED) + ";" + round);
        Tooltip tip = Kept.tip(r, dotWords(d), 200);
        tip.setWrapText(true);
        tip.setMaxWidth(420);
        r.getProperties().put(PLACE, new Place(Kind.DOT, row, d.month(), Math.min(d.worldMonth(), now), d.entry().code(), here, colour,
                here ? aheadWords(d) : "", here && d.worldMonth() > now));
        return r;
    }

    /**
     * The timeline's pane: a row a field, its name at the left, a dot a discovery, the year ticks along the foot and each found-here dot's line to the
     * world's date. A kept box (Kept.Staging) whose children the draw makes through Kept - the names and the dots - and which PLACES them when it is laid
     * out, from the Place each carries and the width it has: that is the one width a draw cannot know, and what the stacking, the rows' heights and the
     * ticks depend on. The lines, the ticks and the rules are drawn on a canvas of its own under the children, the way a picture draws its parts.
     * Its height answers the width it is asked at (content bias horizontal), so a narrower page gets taller rows where dots stack.
     */
    static final class Timeline extends Pane implements Kept.Staging {

        private ObservableList<Node> stage;
        private final Canvas canvas = new Canvas();
        private int first = ResearchView.FIRST_MONTH, last = ResearchView.FIRST_MONTH + 12, rows;

        Timeline() {
            canvas.setMouseTransparent(true);
            super.getChildren().add(canvas);
            setMinWidth(0);
        }

        /** The dots among its children, in the order they were drawn (the harness measures where layout put them). */
        List<Node> dots() {
            List<Node> out = new ArrayList<>();
            for (Node n : getChildrenUnmodifiable()) {
                Place p = placeOf(n);
                if (p != null && p.kind() == Kind.DOT) out.add(n);
            }
            return out;
        }

        /** The months the axis runs between and how many rows it has (set by every draw). */
        void set(int first, int last, int rows) {
            this.first = first;
            this.last = last;
            this.rows = rows;
        }

        @Override public ObservableList<Node> getChildren() {
            return stage != null ? stage : super.getChildren();
        }

        @Override public void stage() {
            stage = FXCollections.observableArrayList();
            stage.add(canvas);
        }

        @Override public void commit() {
            ObservableList<Node> want = stage;
            stage = null;
            if (want != null) Kept.set(super.getChildren(), want);
            // The children are the same nodes with other places (a month on): laid out again.
            requestLayout();
        }

        /** Where everything stands at a width: the rows' tops and heights, each dot's level, and the plot's left and right. */
        private static final class Geometry {
            double x0, x1, rowsEnd, total;
            double[] top, height;
            final Map<String, Integer> level = new HashMap<>();
        }

        private Geometry geometry(double width) {
            Geometry g = new Geometry();
            g.x0 = LABEL_W;
            g.x1 = Math.max(g.x0 + 60, width - PAD_R);
            double plot = g.x1 - g.x0;
            g.top = new double[rows];
            g.height = new double[rows];
            List<List<Place>> byRow = new ArrayList<>();
            for (int r = 0; r < rows; r++) byRow.add(new ArrayList<>());
            for (Node n : getChildrenUnmodifiable()) {
                Place p = placeOf(n);
                if (p != null && p.kind() == Kind.DOT && p.row() >= 0 && p.row() < rows) byRow.get(p.row()).add(p);
            }
            double y = 0;
            for (int r = 0; r < rows; r++) {
                List<Place> dots = byRow.get(r);
                int[] months = new int[dots.size()];
                for (int i = 0; i < months.length; i++) months[i] = dots.get(i).month();
                int[] lv = levels(months, first, last, plot, DOT + DOT_GAP);
                int most = 0;
                for (int i = 0; i < lv.length; i++) {
                    g.level.put(dots.get(i).code(), lv[i]);
                    most = Math.max(most, lv[i]);
                }
                g.top[r] = y;
                g.height[r] = Math.max(ROW_MIN, ROW_TOP + most * LEVEL + DOT + ROW_FOOT);
                y += g.height[r];
            }
            g.rowsEnd = y;
            g.total = y + AXIS_H;
            return g;
        }

        /** The height of a dot's centre: its level up from the foot of its row. */
        private static double centreY(Geometry g, Place p) {
            return g.top[p.row()] + g.height[p.row()] - ROW_FOOT - DOT / 2 - g.level.getOrDefault(p.code(), 0) * LEVEL;
        }

        @Override public Orientation getContentBias() {
            return Orientation.HORIZONTAL;
        }

        @Override protected double computePrefWidth(double height) {
            return LABEL_W + 300;
        }

        @Override protected double computePrefHeight(double width) {
            double w = width > 0 ? width : getWidth() > 0 ? getWidth() : Pieces.PAGE_WIDE;
            return geometry(w).total;
        }

        @Override protected void layoutChildren() {
            double width = getWidth();
            Geometry g = geometry(width);
            double plot = g.x1 - g.x0;
            canvas.setWidth(Math.max(1, width));
            canvas.setHeight(Math.max(1, g.total));
            canvas.relocate(0, 0);
            for (Node n : getChildrenUnmodifiable()) {
                if (n == canvas) continue;
                Place p = placeOf(n);
                if (p == null) {
                    n.autosize();
                } else if (p.kind() == Kind.LABEL) {
                    double w = LABEL_W - 12, h = n.prefHeight(w);
                    n.resizeRelocate(0, g.top[p.row()] + (g.height[p.row()] - h) / 2, w, h);
                } else {
                    double cx = g.x0 + xOf(p.month(), first, last, plot);
                    n.resizeRelocate(cx - DOT / 2, centreY(g, p) - DOT / 2, DOT, DOT);
                }
            }
            paint(g, width);
        }

        /** The canvas: a rule under each row, the year ticks and their names, and the found-here dots' lines, their figures and the ends of those that run past now. */
        private void paint(Geometry g, double width) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            double plot = g.x1 - g.x0;
            gc.setLineWidth(1);
            gc.setStroke(Color.web(Palette.HAIRLINE));
            for (int r = 0; r < rows; r++) {
                double y = Math.floor(g.top[r] + g.height[r]) + .5;
                gc.strokeLine(0, y, width, y);
            }
            gc.setFont(Palette.Fonts.sansFont(10));
            for (int m : yearTicks(first, last, plot)) {
                double x = Math.floor(g.x0 + xOf(m, first, last, plot)) + .5;
                gc.setStroke(Color.web(Palette.HAIRLINE));
                gc.strokeLine(x, 0, x, g.rowsEnd);
                String name = String.valueOf(CityCalendar.yearOf(m));
                Text measure = new Text(name);
                measure.setFont(gc.getFont());
                double w = measure.getLayoutBounds().getWidth();
                double at = x + 3 + w > width ? x - 3 - w : x + 3;
                gc.setFill(Color.web(Palette.TEXT_MUTED));
                gc.fillText(name, at, g.rowsEnd + 14);
            }
            for (Node n : getChildrenUnmodifiable()) {
                Place p = placeOf(n);
                if (p == null || p.kind() != Kind.DOT || !p.here()) continue;
                double cy = centreY(g, p), xa = g.x0 + xOf(p.month(), first, last, plot), xb = g.x0 + xOf(p.to(), first, last, plot);
                Color c = Color.web(p.colour());
                gc.setStroke(c.deriveColor(0, 1, 1, .6));
                gc.setLineWidth(1.5);
                if (xb > xa + DOT / 2) gc.strokeLine(xa + DOT / 2, cy, xb, cy);
                if (p.past()) gc.strokeLine(xb, cy - 5, xb, cy + 5);
                gc.setFill(c);
                gc.setFont(Palette.Fonts.sansFont(9));
                gc.fillText(p.ahead(), xa + DOT / 2 + 2, cy - DOT / 2 - 2);
                gc.setFont(Palette.Fonts.sansFont(10));
            }
        }
    }

    /* =====================================================================
       OVER TIME: four small charts, made once and handed the month's data
       ===================================================================== */

    /** A small chart's size: the Bank's two-across cards'. */
    static final double CHART_W = 575, CHART_H = 150;

    /** The names of the history's research series: a field's researchers, the month's bill, the share of the equipment fitted. */
    static List<String> seriesNames() {
        List<String> out = new ArrayList<>();
        for (Field f : Field.values()) out.add(HistorySave.researchersKey(f));
        out.add(HistorySave.RESEARCH_SPEND);
        out.add(HistorySave.EQUIPMENT_FITTED);
        return out;
    }

    /** Whether the history holds any of the research series (a city founded the 1900 way before R5 has none, and a city founded the default way never). */
    static boolean recorded(HistorySave h) {
        for (String name : seriesNames()) if (h.seriesByName().containsKey(name)) return true;
        return false;
    }

    Node overTime(List<Discovery> timeline) {
        HistorySave h = ui.game.getHistorySave();
        if (h.months() < 2 || !recorded(h)) {
            return Kept.scope("over none", () -> PolicyScreen.muted("Nothing recorded yet: the researchers, the money and the equipment fitted are "
                    + "kept month by month from the founding, and a chart needs two months."));
        }
        return Kept.scope("over charts", () -> {
            GridPane grid = equalColumns(2, TILE_GAP);
            List<TimeChart.Line> researchers = new ArrayList<>();
            for (Field f : Field.values()) {
                researchers.add(line(HistorySave.researchersKey(f), fieldName(f), fieldColour(f), "count", h.aligned(HistorySave.researchersKey(f))));
            }
            List<Integer> axis = h.getMonth();
            int[][] counts = ResearchView.cumulative(timeline, axis.get(0), axis.get(axis.size() - 1));
            double[] here = new double[axis.size()], abroad = new double[axis.size()];
            for (int i = 0; i < axis.size(); i++) {
                int at = Math.max(0, Math.min(counts[0].length - 1, axis.get(i) - axis.get(0)));
                here[i] = counts[0].length > 0 ? counts[0][at] : Double.NaN;
                abroad[i] = counts[1].length > 0 ? counts[1][at] : Double.NaN;
            }
            VBox[] cards = {
                chartCard("THE RESEARCHERS AT WORK", "The people at work on research, a line a field: the city's own posts and the labs' researchers, "
                        + "counted at each month's close.", "count", researchers, h),
                chartCard("WHAT RESEARCH COST", "A month's research bill - the posts' pay and the labs' other costs - and the prizes the treasury "
                        + "paid the labs.", "money", List.of(line(HistorySave.RESEARCH_SPEND, "Research a month", Palette.MONEY, "money",
                        h.aligned(HistorySave.RESEARCH_SPEND))), h),
                chartCard("FOUND HERE AGAINST FROM ABROAD", "Every discovery the city has come to know, counted up month by month: the ones its own "
                        + "researchers found before the world did, and the ones that reached it from abroad on history's date.", "count",
                        List.of(line("here", "Found here", Palette.DISCOVERY, "count", here), line("abroad", "From abroad", Palette.TEXT_MUTED,
                                "count", abroad)), h),
                chartCard("HOW FAST EQUIPMENT SPREAD", "The share of the equipment standing in the city's buildings that is fitted, month by month: "
                        + "it climbs as sectors fit what pays and falls as new buildings arrive without it.", "percent",
                        List.of(line(HistorySave.EQUIPMENT_FITTED, "Equipment fitted", Palette.GOOD, "percent",
                                h.aligned(HistorySave.EQUIPMENT_FITTED))), h),
            };
            for (int i = 0; i < cards.length; i++) {
                GridPane.setFillHeight(cards[i], true);
                cards[i].setMaxHeight(Double.MAX_VALUE);
                grid.add(cards[i], i % 2, i / 2);
            }
            return grid;
        });
    }

    /** One line of a chart, in a unit of History's (HistoryScreen.plotScale() and fmtUnit()). */
    TimeChart.Line line(String key, String label, String colour, String unit, double[] values) {
        return new TimeChart.Line(key, label, colour, 0, values, v -> HistoryScreen.plotScale(unit, v),
                v -> ui.historyScreen.fmtUnit(unit, v), "");
    }

    /** A chart's card: its title and (i), the chart - made once in the card's scope, handed the month's data and its size at every draw - and its key. */
    VBox chartCard(String title, String info, String unit, List<TimeChart.Line> lines, HistorySave h) {
        return Kept.scope("chart:" + title, () -> {
            TimeChart chart = Kept.of(TimeChart.class, () -> new TimeChart(new ChartModel(), java.util.Set.of(), false));
            chart.setData(h.getMonth(), lines, HistoryScreen.axisFor(unit), null, false, false, null, List.of(), List.of(), List.of(),
                    "no months recorded yet");
            chart.setSize(CHART_W, CHART_H);
            FlowPane key = BankScreen.keyRow();
            for (TimeChart.Line l : lines) key.getChildren().add(BankScreen.swatch(l.colour(), l.label(), false));
            return PolicyScreen.card(PolicyScreen.cardHead(null, null, title, info, null), chart, key);
        });
    }
}
