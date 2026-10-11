package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import ham.citybuildersim.ui.Levers.DialCard;
import ham.citybuildersim.ui.Pieces.Effect;
import ham.citybuildersim.ui.Pieces.Look;
import ham.citybuildersim.ui.Pieces.Press;
import ham.citybuildersim.ui.Pieces.Segment;
import ham.citybuildersim.ui.Pieces.Slice;
import ham.citybuildersim.ui.PolicyScreen.Cell;
import ham.citybuildersim.ui.PolicyScreen.Lever;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Pieces.*;
import static ham.citybuildersim.ui.Levers.*;

/**
 * Policy › Zoning (0.7.109, batch Z2): the fifth Policy area - the city's
 * ground and its zones on one page, a sector's zone on its own page of dial
 * cards, and the forest law on a third - drawn in Policy's own style (the
 * area chips, the dial cards and the staged tray, 0.7.36).
 *
 * WHY. Z1 (0.7.107) built Jerus's zoning (2026-10-08, the project's
 * spec-zoning.md: "in policy zoning will now exist ... you can allocate a %
 * of total land or a specific km² for a specific industry, and even provide
 * the land at a lower rate, or free") as a model with doors and a preview,
 * and drew none of it. The zoning study drew three boards; these are they.
 * PolicyScreen dispatches its Zoning area here, as Finances dispatches its
 * fund to FundScreen, and keeps the frame, the staged set and the tray's
 * register: every dial here is a PolicyScreen.Lever staged in policyStaged,
 * so a page change throws it away and one press applies it.
 *
 *   THE GROUND (board 1): the four figures (ZONED, HELD FOR THEM, PRICE GIVEN
 *     UP, FOREST KEPT) across the frame; THE CITY'S GROUND as one bar of the
 *     dry ground - built, each zone used solid and held hatched, the bare
 *     ground and open forest free for anyone, the forest the law keeps - and
 *     under it a closer bar of the zones and the bare ground; THE ZONES a
 *     row each, a door to its page; THE FOREST LAW's bar with a door; and
 *     "Zone ground for an industry".
 *   A SECTOR'S ZONE (board 2): HOW MUCH GROUND (a share of the dry ground or
 *     km²), ITS PRICE (0-100% of the going price) and ITS TAXES (the
 *     sector's own three offsets - "one dial, two doors", Z5 - the same keys
 *     as Policy › Taxes' rows), each before and after; the zone taken off.
 *   THE FOREST LAW (board 3): its bar, its two shares, and what it would do
 *     in the long run - the wood kept standing for ever, the most that may
 *     be cut (Forestry's crews are I9's: what the law holds, not crews), the
 *     forest a building may take.
 *
 * EVERY FIGURE IS THE MODEL'S. They are ZoningView's (the hub, the rows, a
 * page and the forest, each on the city's zoning or a staged copy),
 * PolicyPreview's (the tax take, THE BUDGET, zoningTaxChange()) and
 * LandManager's - ZoningViewCheck holds them; nothing here adds up a figure.
 */
final class ZoningScreen {

    private final PolicyScreen policy;
    private final UserInterface ui;

    ZoningScreen(PolicyScreen policy, UserInterface ui) {
        this.policy = policy;
        this.ui = ui;
    }

    /* =====================================================================
       THE PAGES
       ===================================================================== */

    /** The area's two fixed pages: the ground and its zones (the hub), and the forest law. */
    static final String THE_GROUND = "The ground", THE_FOREST = "The forest law";

    /** ...as the tabs list them, before each zone's own. */
    static final String[] PAGES = {THE_GROUND, THE_FOREST};

    /** ...and their icons: the plot, the trees. */
    static final String[] PAGE_ICONS = {Icons.ZONING, Icons.TREES};

    /** A sector's zone's page is this and its key: "zone:Agriculture". */
    static final String ZONE_PAGE = "zone:";

    static String zonePage(String sector) { return ZONE_PAGE + sector; }

    /** The sector a page is the zone of, or null for the two fixed pages. */
    static String sectorOf(String page) {
        return page != null && page.startsWith(ZONE_PAGE) ? page.substring(ZONE_PAGE.length()) : null;
    }

    /** Whether a page is one of this area's: the two, or a sector's (zoned or about to be). */
    boolean knows(String page) {
        for (String p : PAGES) if (p.equals(page)) return true;
        String s = sectorOf(page);
        return s != null && ui.game.getSectors().byKey(s) != null;
    }

    /** A sector's name as the screens write it. */
    String nameOf(String sector) {
        Sector s = ui.game.getSectors().byKey(sector);
        return s == null ? sector : s.label();
    }

    /** "Agriculture's", "Restaurants'". */
    static String possessive(String name) { return name + (name.endsWith("s") ? "'" : "'s"); }

    /** The tabs: the two pages, then each zone in the zones' order - and the open sector's, a zone not yet applied. */
    List<String> tabPages() {
        List<String> out = new ArrayList<>(List.of(PAGES));
        for (Zoning.Zone z : ui.game.getZoning().zones()) out.add(zonePage(z.sector()));
        if (sectorOf(policy.policyPage) != null && !out.contains(policy.policyPage)) out.add(policy.policyPage);
        return out;
    }

    /** ...as their chips read: a page's name, a zone's sector's. */
    String tabName(String page) {
        String s = sectorOf(page);
        return s == null ? page : nameOf(s);
    }

    /** The tab strip under the head. */
    FlowPane tabs() {
        List<String> pages = tabPages();
        String[] names = new String[pages.size()], icons = new String[pages.size()];
        String current = "";
        for (int i = 0; i < names.length; i++) {
            String s = sectorOf(pages.get(i));
            names[i] = tabName(pages.get(i));
            icons[i] = s == null ? PAGE_ICONS[i] : Icons.ofSector(ui.game.getSectors().byKey(s));
            if (pages.get(i).equals(policy.policyPage)) current = names[i];
        }
        return chipStrip(names, icons, current, Palette.SIZE_LABEL, name -> {
            for (String p : pages) if (tabName(p).equals(name)) policy.open(PolicyScreen.ZONING, p);
        });
    }

    /** The page open, drawn into `page`. */
    void page(VBox page) {
        String sector = sectorOf(policy.policyPage);
        if (sector != null) zonePage(page, sector);
        else if (THE_FOREST.equals(policy.policyPage)) forestPage(page);
        else groundPage(page);
    }

    /** The breadcrumb on a zone's page: "Policy › Zoning › Agriculture", "Zoning" a way back to the ground. */
    HBox head(HBox head) {
        String sector = sectorOf(policy.policyPage);
        if (sector == null || head.getChildren().isEmpty() || !(head.getChildren().get(0) instanceof HBox titled)) return head;
        if (titled.getChildren().size() < 3 || !(titled.getChildren().get(2) instanceof Label where)) return head;
        where.setStyle(where.getStyle() + " -fx-cursor: hand;");
        where.setOnMouseClicked(e -> policy.open(PolicyScreen.ZONING, THE_GROUND));
        Label sep = new Label("›");
        sep.setStyle(Palette.words(Palette.SIZE_TITLE, Palette.TEXT_MUTED) + " -fx-padding: 8 0 2 0;");
        Label last = new Label(nameOf(sector));
        last.setStyle(Palette.strong(Palette.SIZE_TITLE, Palette.TEXT_HEAD) + " -fx-padding: 8 0 2 0;");
        last.setMinWidth(Region.USE_PREF_SIZE);
        titled.getChildren().addAll(3, List.of(sep, last));
        return head;
    }

    /** The area's (i). */
    static final String AREA_INFO = "Ground held for one industry: a share of the city's dry ground or a size in km², "
            + "at a share of the going price - or free. Only that sector builds on its zone; the rest of the free ground "
            + "is free for anyone, at the going price. A zone's taxes are its sector's own rates, the same dials as "
            + "Policy › Taxes. And the forest law: how much of the city's forest is protected - never cut nor built on - "
            + "and how much kept from building. Zones are amounts of ground, not places on the map.";

    /* =====================================================================
       THE FOUR FIGURES (board 1's head): ZONED, HELD FOR THEM, PRICE GIVEN
       UP, FOREST KEPT - across every page of the area, each a door.
       ===================================================================== */

    /** The four, worked out without drawing them (pure: the probe reads them). */
    List<Cell> vitalCells() {
        ZoningView.Figures f = ZoningView.figures(ui.game);
        List<Cell> out = new ArrayList<>();
        out.add(new Cell("ZONED", f.zones() == 0 ? "none" : LandManager.areaWords(f.zonedSqFt()),
                f.zones() == 0 ? "every square metre free for anyone"
                        : pct2(f.zonedShare()) + " of the dry ground · " + f.zones() + (f.zones() == 1 ? " zone" : " zones"),
                Palette.TEXT_HEAD, "The ground: the city's dry ground and its zones"));
        out.add(new Cell("HELD FOR THEM", LandManager.areaWords(f.heldSqFt()),
                f.zones() == 0 ? "no zone holds any ground" : "free ground no other business may take",
                Palette.TEXT_HEAD, "The ground: what each zone holds"));
        out.add(new Cell("PRICE GIVEN UP", money(f.priceGivenUp()),
                "on what they hold · " + money(f.taxRelief()) + " a month tax relief",
                Palette.TEXT_HEAD, "The ground: each zone's price"));
        out.add(new Cell("FOREST KEPT", f.lawSet() && f.forestSqFt() > 0 ? LandManager.areaWords(f.keptSqFt()) : "none",
                f.forestSqFt() <= 0 ? "the city owns no forest"
                        : !f.lawSet() ? "no law: every tree may be cut or built on"
                        : pct0(f.protectedShare()) + " protected · " + pct0(f.keptShare()) + " kept from building",
                Palette.TEXT_HEAD, "The forest law"));
        return out;
    }

    /** The strip: the four as limit cells, each a door. */
    HBox vitals() {
        List<Cell> four = vitalCells();
        Runnable ground = () -> policy.open(PolicyScreen.ZONING, THE_GROUND);
        Runnable[] go = {ground, ground, ground, () -> policy.open(PolicyScreen.ZONING, THE_FOREST)};
        VBox[] cells = new VBox[four.size()];
        for (int i = 0; i < cells.length; i++) {
            Cell c = four.get(i);
            cells[i] = limitCell(c.label(), c.value(), c.note(), c.tone(), c.where(), go[i]);
        }
        return vitalsBar(cells);
    }

    /* =====================================================================
       THE COLOURS: the zones in the business violet and then the other
       categories (which, not how much), built ground grey, the ground free
       for anyone the land office's pink, and the forest in three greens off
       the map's own (TileRaster.GROUND's forest, the darkest: protected).
       ===================================================================== */

    /** The zones' colours, in the zones' order: the business ramp (a zone is a sector's), then the other categories. */
    static final String[] ZONE_COLOURS = {Palette.BUSINESS, Palette.BUSINESS_LIGHT, Palette.BUSINESS_DARK,
            Palette.MONEY, Palette.PEOPLE, Palette.ORE, Palette.MONEY_LIGHT, Palette.PEOPLE_LIGHT};

    /** Built ground: the rest's grey - ground nobody can have. */
    static final String BUILT_COLOUR = Palette.RAMP_REST;

    /** Bare ground free for anyone: the land office's free ground, solid pink. */
    static final String FREE_COLOUR = Palette.BUILDING;

    /** The forest's three, open to protected: two lighter steps of the map's forest green, and the map's own. */
    static final String FOREST_OPEN = "#9cc48a", FOREST_WORKING = "#6f9a5e", FOREST_PROTECTED = "#527c45";

    /** A zone's colour, by its place in the zones' order. */
    String zoneColour(String sector) {
        List<Zoning.Zone> zones = ui.game.getZoning().zones();
        for (int i = 0; i < zones.size(); i++) {
            if (zones.get(i).sector().equals(sector)) return ZONE_COLOURS[i % ZONE_COLOURS.length];
        }
        return Palette.BUSINESS;
    }

    /** A bar part's name, as its key and its tooltip say it. */
    String partName(ZoningView.Part p) {
        return switch (p.kind()) {
            case BUILT -> "Built";
            case USED -> nameOf(p.sector()) + ", built";
            case HELD -> nameOf(p.sector()) + ", held";
            case BARE -> "Free for anyone";
            case OPEN -> "Open forest";
            case WORKING -> "Working forest";
            case PROTECTED -> "Protected forest";
        };
    }

    /** ...its colour. */
    String partColour(ZoningView.Part p) {
        return switch (p.kind()) {
            case BUILT -> BUILT_COLOUR;
            case USED, HELD -> zoneColour(p.sector());
            case BARE -> FREE_COLOUR;
            case OPEN -> FOREST_OPEN;
            case WORKING -> FOREST_WORKING;
            case PROTECTED -> FOREST_PROTECTED;
        };
    }

    /** ...its tooltip's second line. */
    static String partWhat(ZoningView.Part p) {
        return switch (p.kind()) {
            case BUILT -> "built on, by every sector outside its zone and by the city";
            case USED -> "built on by its sector, in its zone";
            case HELD -> "free, held for its sector: no other business may take it";
            case BARE -> "bare ground any business, or the city, may build on";
            case OPEN -> "forest any business may build on - and clear";
            case WORKING -> "forest that may be cut, never built on";
            case PROTECTED -> "forest never cut, never built on";
        };
    }

    /** The bar: a part a stretch, held ground hatched, a click on a zone's part its page. */
    Pieces.CauseBar groundBar(List<ZoningView.Part> parts, double band) {
        List<Pieces.Part> drawn = new ArrayList<>();
        for (ZoningView.Part p : parts) {
            if (!(p.sqFt() > 0) && p.kind() != ZoningView.Kind.BARE) continue;
            Pieces.Part d = Pieces.Part.of(partName(p), p.sqFt(), partColour(p))
                    .tip(partName(p) + "\n" + LandManager.areaWords(p.sqFt()) + " · " + partWhat(p));
            if (p.kind() == ZoningView.Kind.HELD) d = d.leftOver();
            if (p.sector() != null) {
                String sector = p.sector();
                d = d.go(() -> policy.open(PolicyScreen.ZONING, zonePage(sector)));
            }
            if (p.kind() == ZoningView.Kind.OPEN || p.kind() == ZoningView.Kind.WORKING || p.kind() == ZoningView.Kind.PROTECTED) {
                d = d.go(() -> policy.open(PolicyScreen.ZONING, THE_FOREST));
            }
            drawn.add(d);
        }
        return causeBar(drawn, 0, band, LandManager::areaWords);
    }

    /* =====================================================================
       THE GROUND (board 1)
       ===================================================================== */

    /** THE CITY'S GROUND's (i). */
    static final String GROUND_INFO = "The city's dry ground, all of it: what is built on; each zone's ground - built on "
            + "in it, solid, and held for its sector, hatched; the bare ground free for anyone; and the forest - open, "
            + "which any business may build on and clear, and what the forest law keeps: working forest, cut but never "
            + "built on, and protected forest, never either. A zone holds bare ground first, then open forest. Under it, "
            + "the zones and the bare ground alone, closer, so a small zone can be seen.";

    /** THE ZONES' (i). */
    static final String ZONES_INFO = "A zone holds its size, less what its sector has built in it, out of the free "
            + "ground; when the zones together want more than is free, each holds the same share of what it wants and "
            + "fills as the city buys land. Its ground is sold, bought back and assessed at its price. Its three rates are "
            + "its sector's own - profit, sales and property - the same dials as Policy › Taxes.";

    /** THE FOREST LAW's (i) on the hub. */
    static final String LAW_INFO = "Of the city's forest ground, a protected share is never cut nor built on and a kept "
            + "share - at least the protected - is never built on; the rest is open: any business may build on it, which "
            + "clears it. With the law at none, the default, all of it is open.";

    void groundPage(VBox page) {
        Game g = ui.game;
        LandManager land = g.getLandManager();
        List<ZoningView.Part> parts = ZoningView.ground(g);

        page.getChildren().add(sectionHead("THE CITY'S GROUND", GROUND_INFO,
                PolicyScreen.quiet(LandManager.areaWords(land.getOwnedSqFt()) + " dry")));
        VBox bars = PolicyScreen.card(groundBar(parts, 18));
        if (g.getZoning().hasZones()) {
            bars.getChildren().add(PolicyScreen.muted("Closer: the zones, and the bare ground free for anyone"));
            bars.getChildren().add(groundBar(ZoningView.closer(g), 12));
        }
        page.getChildren().add(bars);

        Pieces.ActionButton zone = actionButton(Icons.ZONING, Palette.MONEY, ACTION_INLINE,
                new Press(Look.GO, "Zone ground for an industry", null), () -> {
                    if (!policy.openLines.remove(PICK)) policy.openLines.add(PICK);
                    policy.showPolicyMenu();
                });
        zone.setMinWidth(250);
        zone.setMaxWidth(Region.USE_PREF_SIZE);
        List<ZoningView.Row> rows = ZoningView.zones(g);
        page.getChildren().add(sectionHead("THE ZONES", ZONES_INFO, zone));
        if (policy.openLines.contains(PICK)) page.getChildren().add(picker());
        if (rows.isEmpty()) {
            page.getChildren().add(PolicyScreen.muted("No zone yet: every square metre the city has not built on is free "
                    + "for anyone, at the going price."));
        } else {
            VBox list = PolicyScreen.card();
            list.setSpacing(2);
            for (ZoningView.Row r : rows) list.getChildren().add(zoneRow(r));
            page.getChildren().add(list);
        }

        page.getChildren().add(sectionHead("THE FOREST LAW", LAW_INFO,
                doorPill("The forest law", Icons.TREES, Palette.ACCENT, () -> policy.open(PolicyScreen.ZONING, THE_FOREST))));
        page.getChildren().add(PolicyScreen.doorCard(lawCard(ZoningView.forest(g, g.getZoning()), g.getZoning()),
                () -> policy.open(PolicyScreen.ZONING, THE_FOREST)));
    }

    /** The open line of the sector picker. */
    static final String PICK = "zoning:pick";

    /** "Zone ground for an industry": every sector with no zone, a chip each, opening its page. */
    VBox picker() {
        FlowPane chips = new FlowPane(8, 6);
        for (String key : Sectors.KEYS) {
            if (ui.game.getZoning().zone(key) != null || ui.game.getSectors().byKey(key) == null) continue;
            Label chip = stepChip(nameOf(key), () -> {
                policy.openLines.remove(PICK);
                policy.open(PolicyScreen.ZONING, zonePage(key));
            }, false);
            chip.setGraphic(icon(Icons.ofSector(ui.game.getSectors().byKey(key)), Palette.TEXT_LABEL, 13));
            chip.setGraphicTextGap(6);
            chips.getChildren().add(chip);
        }
        return PolicyScreen.card(PolicyScreen.words("Which industry? Its page opens with nothing zoned; nothing changes "
                + "until it is applied.", Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL), chips);
    }

    /** A zone's size in words: "2.50% of the dry ground", "1.25 km²". */
    static String sizeWords(Zoning.Size kind, double size) {
        return kind == Zoning.Size.SHARE ? pct2(size) + " of the dry ground" : LandManager.areaWords(LandManager.sqFt(size));
    }

    /** A rate chip's words: "profit 15.00%", "sales 13.00% (−2.00 pts)". */
    static String rateChip(Zoning.Tax tax, double rate, double offset) {
        String name = tax.name().toLowerCase();
        String off = Math.abs(offset) > 1e-12 ? " (" + pts(offset) + ")" : "";
        return name + " " + pct2(rate) + off;
    }

    /** One zone: its sector, its size and share, used against held, its price, its three rates, its relief, a door. */
    HBox zoneRow(ZoningView.Row r) {
        Sector s = ui.game.getSectors().byKey(r.sector());
        String colour = zoneColour(r.sector());
        HBox lead = new HBox(8, iconSquare(Icons.ofSector(s), colour, 24, 13));
        VBox named = new VBox(0, PolicyScreen.words(nameOf(r.sector()), Palette.SIZE_BODY + 1, Palette.TEXT_BODY));
        if (r.isShort()) named.getChildren().add(chip("holds less than it wants", Palette.WARN));
        lead.getChildren().add(named);
        lead.setAlignment(Pos.CENTER_LEFT);
        lead.setMinWidth(170);
        lead.setPrefWidth(200);
        lead.setMaxWidth(200);

        VBox size = new VBox(0, PolicyScreen.figure(LandManager.areaWords(r.sizeSqFt()), Palette.SIZE_BODY + 1, Palette.TEXT_HEAD),
                PolicyScreen.muted(pct2(r.share()) + " of the dry ground"));
        size.setMinWidth(130);
        size.setPrefWidth(140);
        size.setMaxWidth(140);

        double scale = Math.max(r.sizeSqFt(), r.usedSqFt() + r.heldSqFt());
        SegmentBar bar = segmentBar(List.of(
                new Segment(r.usedSqFt(), colour, false, null, null, "built in it · " + LandManager.areaWords(r.usedSqFt()), null),
                new Segment(r.heldSqFt(), colour, true, null, null, "held for it · " + LandManager.areaWords(r.heldSqFt()), null)),
                scale > 0 ? scale : 1, List.of(), 0, 8);
        VBox used = new VBox(3, bar, PolicyScreen.muted(usedWords(r)));
        used.setMinWidth(150);
        HBox.setHgrow(used, Priority.ALWAYS);

        VBox price = new VBox(0, PolicyScreen.figure(pct0(r.priceShare()) + " of the price", Palette.SIZE_LABEL + 1, Palette.TEXT_HEAD),
                PolicyScreen.muted(money(LandManager.perM2(r.pricePerSqFt())) + "/m²"));
        price.setMinWidth(110);
        price.setPrefWidth(120);
        price.setMaxWidth(120);

        FlowPane rates = new FlowPane(4, 4);
        for (Zoning.Tax t : Zoning.Tax.values()) {
            rates.getChildren().add(chip(rateChip(t, r.rate()[t.ordinal()], r.offset()[t.ordinal()]),
                    Math.abs(r.offset()[t.ordinal()]) > 1e-12 ? Palette.ACCENT : Palette.TEXT_LABEL));
        }
        rates.setPrefWrapLength(200);
        rates.setMinWidth(170);
        rates.setPrefWidth(210);
        rates.setMaxWidth(210);

        VBox relief = new VBox(0, PolicyScreen.figure(money(r.taxRelief()), Palette.SIZE_LABEL + 1, Palette.TEXT_HEAD),
                PolicyScreen.muted("a month relief"));
        relief.setMinWidth(90);
        relief.setPrefWidth(96);
        relief.setMaxWidth(96);

        Label arrow = new Label("›");
        arrow.setStyle(Palette.strong(Palette.SIZE_HEADING + 2, Palette.TEXT_MUTED));
        HBox row = new HBox(10, lead, size, used, price, rates, relief, arrow);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMinHeight(44);
        String rest = "-fx-padding: 4 6 4 6; -fx-background-radius: 6;"
                + " -fx-border-color: " + Palette.HAIRLINE + "; -fx-border-width: 0 0 1 0; -fx-cursor: hand;";
        row.setStyle(rest);
        row.setOnMouseEntered(e -> row.setStyle(rest + " -fx-background-color: " + Palette.CONTROL + ";"));
        row.setOnMouseExited(e -> row.setStyle(rest));
        row.setOnMouseClicked(e -> policy.open(PolicyScreen.ZONING, zonePage(r.sector())));
        return row;
    }

    /** "built 0.4 · holds 1.6 km²", and "of 4.0 it wants" when it is short. */
    static String usedWords(ZoningView.Row r) {
        return "built " + LandManager.areaWords(r.usedSqFt()) + " · holds " + LandManager.areaWords(r.heldSqFt())
                + (r.isShort() ? " of " + LandManager.areaWords(r.wantSqFt()) + " it wants" : "");
    }

    /** The forest law's card: the forest as a bar of its kinds, cleared ground grey, and what the law means. */
    VBox lawCard(ZoningView.Forest f, Zoning z) {
        if (!(f.forestSqFt() > 0)) {
            return PolicyScreen.card(PolicyScreen.cardHead(Icons.TREES, FOREST_PROTECTED, "THE CITY OWNS NO FOREST", null, null),
                    PolicyScreen.muted("A law here keeps nothing until the city buys forest ground."));
        }
        VBox c = PolicyScreen.card(PolicyScreen.cardHead(Icons.TREES, FOREST_PROTECTED, lawTitle(z), null,
                        PolicyScreen.quiet(LandManager.areaWords(f.forestSqFt()) + " of forest")),
                forestBar(f, 14), PolicyScreen.words(lawWords(f, z), Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
        return c;
    }

    /** "NO FOREST LAW", "10% PROTECTED · 25% KEPT FROM BUILDING". */
    static String lawTitle(Zoning z) {
        if (z.protectedShare() == 0 && z.keptShare() == 0) return "NO FOREST LAW";
        return pct0(z.protectedShare()) + " PROTECTED · " + pct0(z.keptShare()) + " KEPT FROM BUILDING";
    }

    /** What a law means, in one sentence. */
    static String lawWords(ZoningView.Forest f, Zoning z) {
        PolicyPreview.ForestAt a = f.at();
        if (z.protectedShare() == 0 && z.keptShare() == 0) {
            return "Every tree may be cut and every hectare built on: " + LandManager.areaWords(f.openStandingSqFt())
                    + " of open forest stands, " + LandManager.areaWords(f.clearedSqFt()) + " cleared for building.";
        }
        return LandManager.areaWords(a.protectedSqFt()) + " never cut nor built on, " + LandManager.areaWords(a.workingSqFt())
                + " cut but never built on; " + m3(a.protectedWood()) + " of wood stands for ever, "
                + m3(a.cuttableWood()) + " may be cut.";
    }

    /** Cubic metres of wood: "13.7k m³", "1.2M m³". */
    static String m3(double m3) {
        if (!(m3 > 0)) return "0 m³";
        if (m3 >= 1e6) return String.format("%,.1fM m³", m3 / 1e6);
        if (m3 >= 1e4) return String.format("%,.0fk m³", m3 / 1e3);
        return String.format("%,.0f m³", m3);
    }

    /** The forest as a bar: protected, working, the open forest standing, and the cleared ground. */
    Pieces.CauseBar forestBar(ZoningView.Forest f, double band) {
        PolicyPreview.ForestAt a = f.at();
        List<Pieces.Part> parts = new ArrayList<>();
        parts.add(Pieces.Part.of("Protected", a.protectedSqFt(), FOREST_PROTECTED)
                .tip("Protected forest\n" + LandManager.areaWords(a.protectedSqFt()) + " · never cut, never built on"));
        parts.add(Pieces.Part.of("Working", a.workingSqFt(), FOREST_WORKING)
                .tip("Working forest\n" + LandManager.areaWords(a.workingSqFt()) + " · cut, never built on"));
        parts.add(Pieces.Part.of("Open", f.openStandingSqFt(), FOREST_OPEN)
                .tip("Open forest standing\n" + LandManager.areaWords(f.openStandingSqFt()) + " · any business may build on it, and clear it"));
        parts.add(Pieces.Part.of("Cleared", Math.min(f.clearedSqFt(), a.openSqFt()), BUILT_COLOUR)
                .tip("Cleared for building\n" + LandManager.areaWords(f.clearedSqFt()) + " · built on, or built on once; never regrows"));
        return causeBar(parts, 0, band, LandManager::areaWords);
    }

    /* =====================================================================
       THE STAGED SET: the zone's and the law's keys, beside the taxes'

       A zone's three settings stage under "zone:<sector>:size", ":unit"
       (0 a share, 1 km²) and ":price", its removal under ":off"; the law's
       two under "forest:protected" and "forest:kept"; its taxes under the
       Taxes pages' own keys ("profit:<sector>", "sales:", "prop:"), so
       PolicyScreen.stagedPolicy() previews them as it does there. Each
       registers a Lever whose apply is the model's door - Game.setZone()
       with the three as staged, removeZone(), setForestLaw(), setZoneTax() -
       which is how the tray names and applies them.
       ===================================================================== */

    static String sizeKey(String s)  { return "zone:" + s + ":size"; }
    static String unitKey(String s)  { return "zone:" + s + ":unit"; }
    static String priceKey(String s) { return "zone:" + s + ":price"; }
    static String offKey(String s)   { return "zone:" + s + ":off"; }
    static final String PROTECTED_KEY = "forest:protected", KEPT_KEY = "forest:kept";

    /** A share of the dry ground's step: half a point. */
    static final double SHARE_STEP = .005;

    /** A price share's step: five points of the going price, a twentieth (PolicyScreen's TUITION_STEP's). */
    static final double PRICE_STEP = .05;

    /** The forest law's step: a point of the forest. */
    static final double LAW_STEP = .01;

    /** How many steps a km² dial is cut into across the city's dry ground, before its step is rounded to 1, 2 or 5 of a power of ten. */
    static final double KM2_STEPS = 200;

    /** The dry ground, km². */
    double dryKm2() { return LandManager.km2(ui.game.getLandManager().getOwnedSqFt()); }

    /** A km² dial's step: the dry ground over KM2_STEPS, down to 1, 2 or 5 of a power of ten. */
    double km2Step() {
        double raw = Math.max(1e-6, dryKm2() / KM2_STEPS);
        double p = Math.pow(10, Math.floor(Math.log10(raw)));
        double m = raw / p;
        return (m >= 5 ? 5 : m >= 2 ? 2 : 1) * p;
    }

    /** ...and its top: the dry ground, up to a whole step. */
    double km2Top() {
        double step = km2Step();
        return Math.max(step, Math.ceil(dryKm2() / step - 1e-9) * step);
    }

    /** A zone's settings as the city has them - a sector with no zone: a share, nothing, at the going price. */
    Zoning.Size kindNow(String s) { Zoning.Zone z = ui.game.getZoning().zone(s); return z == null ? Zoning.Size.SHARE : z.kind(); }
    double sizeNow(String s)      { Zoning.Zone z = ui.game.getZoning().zone(s); return z == null ? 0 : z.size(); }
    double priceNow(String s)     { Zoning.Zone z = ui.game.getZoning().zone(s); return z == null ? Zoning.MAX_PRICE_SHARE : z.priceShare(); }

    /** A zone's size as the city has it, said in a unit: what its size dial reads as current in the unit staged - and what an unstaged size is, the unit staged. */
    double sizeAs(String s, Zoning.Size kind) { return convert(sizeNow(s), kindNow(s), kind); }

    /** A key's value as staged, or a value of the caller's for one key (a thumb while it is dragged). */
    double value(String k, double current, String key, double v) {
        return k.equals(key) ? v : policy.staged(k, current);
    }

    Zoning.Size kindOf(double unit) { return unit >= .5 ? Zoning.Size.KM2 : Zoning.Size.SHARE; }

    static double unitOf(Zoning.Size kind) { return kind == Zoning.Size.KM2 ? 1 : 0; }

    /** A size in one unit, said in the other: a share of the dry ground as km², or km² as a share. */
    double convert(double size, Zoning.Size from, Zoning.Size to) {
        if (from == to) return size;
        double dry = dryKm2();
        return to == Zoning.Size.KM2 ? size * dry : dry > 0 ? size / dry : 0;
    }

    /** The sectors with anything of their zone staged, and `extra`. */
    Set<String> stagedSectors(String extra) {
        Set<String> out = new LinkedHashSet<>();
        for (String k : policy.policyStaged.keySet()) {
            if (k.startsWith("zone:")) {
                String rest = k.substring(5);
                int at = rest.lastIndexOf(':');
                if (at > 0) out.add(rest.substring(0, at));
            }
        }
        if (extra != null) out.add(extra);
        return out;
    }

    /** The city's zoning with everything staged put through a copy (ZoningView.with(), without(), withLaw()). */
    Zoning stagedZoning() { return stagedZoning(null, 0); }

    /** ...and one key at a value of the caller's on top. */
    Zoning stagedZoning(String key, double v) {
        Zoning z = ui.game.getZoning();
        String keySector = null;
        if (key != null && key.startsWith("zone:")) {
            String rest = key.substring(5);
            keySector = rest.substring(0, Math.max(0, rest.lastIndexOf(':')));
        }
        for (String s : stagedSectors(keySector)) {
            if (value(offKey(s), 0, key, v) > 0) {
                z = ZoningView.without(z, s);
                continue;
            }
            Zoning.Size kind = kindOf(value(unitKey(s), unitOf(kindNow(s)), key, v));
            double size = value(sizeKey(s), sizeAs(s, kind), key, v);
            double price = value(priceKey(s), priceNow(s), key, v);
            if (z.zone(s) == null && !(size > 0)) continue;   // nothing to zone yet
            z = ZoningView.with(z, s, kind, size, price);
        }
        boolean law = policy.isStaged(PROTECTED_KEY) || policy.isStaged(KEPT_KEY)
                || PROTECTED_KEY.equals(key) || KEPT_KEY.equals(key);
        if (law) {
            Zoning live = ui.game.getZoning();
            z = ZoningView.withLaw(z, value(PROTECTED_KEY, live.protectedShare(), key, v),
                    value(KEPT_KEY, live.keptShare(), key, v));
        }
        return z == ui.game.getZoning() ? z.copy() : z;
    }

    /** A zone as staged, through its door: Game.setZone() with its three as staged - nothing for a new zone of no size, nothing when it is to come off. */
    void applyZone(String s) {
        if (policy.isStaged(offKey(s))) return;
        Zoning.Size kind = kindOf(policy.staged(unitKey(s), unitOf(kindNow(s))));
        double size = policy.staged(sizeKey(s), sizeAs(s, kind));
        if (ui.game.getZoning().zone(s) == null && !(size > 0)) return;
        ui.game.setZone(s, kind, size, policy.staged(priceKey(s), priceNow(s)));
    }

    /** The law as staged, through its door. */
    void applyLaw() {
        Zoning live = ui.game.getZoning();
        ui.game.setForestLaw(policy.staged(PROTECTED_KEY, live.protectedShare()), policy.staged(KEPT_KEY, live.keptShare()));
    }

    /** THE BUDGET under everything staged - the taxes through the staged policy, the zones' relief through the staged zoning (PolicyPreview.zoningTaxChange()) - with one key at a thumb's value. */
    Effect budget(String key, double v) {
        TaxPolicy after = policy.stagedPolicyWith(key != null && key.startsWith("zone:") || PROTECTED_KEY.equals(key)
                || KEPT_KEY.equals(key) ? null : key, v);
        PolicyPreview.Budget b = PolicyPreview.budget(ui.game, after, policy.stagedShare());
        double zones = PolicyPreview.zoningTaxChange(ui.game, stagedZoning(key, v));
        return Effect.of("THE BUDGET, a month", b.before(), b.after() + zones, w -> signedTight(w, false))
                .delta(d -> signedTight(d, false)).info(BUDGET_INFO);
    }

    /** THE BUDGET's (i) on these pages. */
    static final String BUDGET_INFO = PolicyScreen.BUDGET_INFO + " On a zone's page it adds the property tax its staged "
            + "price would forgo or recover on the ground built in it (PolicyPreview's zoning relief).";

    /** The tray's (i). */
    static final String TRAY_INFO = "Everything staged on this page, applied together by one press: the zone's size and "
            + "price through the zone's own door, its taxes through the same door as Policy › Taxes - so a move here moves "
            + "the rate for every building of the sector. A staged change goes when the page does.";

    /**
     * The tray (Policy's, 0.7.36): a chip a change, THE BUDGET before and
     * after the set, "Apply it" or "Apply all N", and "Leave it as it is".
     * A unit's chip takes its size with it - the size was staged in it.
     */
    Region tray() {
        if (policy.policyStaged.isEmpty()) return null;
        List<Node> chips = new ArrayList<>();
        for (PolicyScreen.TrayItem item : policy.trayItems()) {
            chips.add(stagedChip(item.words(), () -> {
                policy.unstage(item.key());
                if (item.key().endsWith(":unit")) policy.unstage(item.key().substring(0, item.key().length() - 5) + ":size");
                policy.showPolicyMenu();
            }));
        }
        if (chips.isEmpty()) return null;
        int count = chips.size();
        Pieces.ActionButton apply = actionButton(Icons.TICK, Palette.MONEY, ACTION_INLINE,
                new Press(Look.GO, applyWords(count), null), () -> { policy.applyStaged(); policy.showPolicyMenu(); });
        Pieces.ActionButton discard = actionButton(Icons.CLOSE, Palette.TEXT_LABEL, ACTION_INLINE,
                new Press(Look.CHOOSE, "Leave it as it is", null), () -> { policy.dropProposal(); policy.showPolicyMenu(); });
        return stagedTray(chips, budget(null, 0), apply, discard, TRAY_INFO);
    }

    /** "Apply it", "Apply all 3". */
    static String applyWords(int count) { return count == 1 ? "Apply it" : "Apply all " + count; }

    /* =====================================================================
       A SECTOR'S ZONE (board 2): HOW MUCH GROUND, ITS PRICE, ITS TAXES
       ===================================================================== */

    /** HOW MUCH GROUND's (i). */
    static final String SIZE_INFO = "A share of the city's dry ground - re-read as the city buys land, so 2% stays 2% - or "
            + "a size in km². The zone holds it, less what its sector has built in it, out of the free ground: no other "
            + "business may take that ground. When the zones together want more than is free, each holds the same share "
            + "of what it wants and fills as land is bought.";

    /** ITS PRICE's (i). */
    static final String PRICE_INFO = "What the city sells the zone's ground at, as a share of the going business price - "
            + "and buys it back at, and assesses it at for the property tax. Ground already sold in it is not re-priced: "
            + "the share is what the next sale pays; the roll reads it at once.";

    /** ITS TAXES' (i). */
    static final String TAXES_INFO = "A zone's taxes are its sector's own rates: the city's rate and the sector's move off "
            + "it, in points. They are the same dials as the sector's rows on Policy › Taxes - one dial, two doors - so a "
            + "move here moves the rate for every building of the sector, zoned or not. " + PolicyScreen.OFFSET_INFO;

    /** The caveat a zone's ground owes: amounts, not places. */
    static final String GROUND_CAVEAT = "Ground as it stands: what the city buys next widens a share.";

    void zonePage(VBox page, String s) {
        Game g = ui.game;
        Sector sector = g.getSectors().byKey(s);
        String name = nameOf(s);
        boolean zoned = g.getZoning().zone(s) != null;
        boolean off = policy.isStaged(offKey(s));
        ZoningView.Row row = ZoningView.row(g, s);

        String line = !zoned ? name + " has no zone: its buildings take the ground free for anyone, at the going price."
                : name + "'s zone holds " + LandManager.areaWords(row.heldSqFt()) + " for it; it has built on "
                        + LandManager.areaWords(row.usedSqFt()) + " in it" + (row.isShort() ? " - short of what it wants." : ".");
        page.getChildren().add(sectionHead(possessive(name).toUpperCase() + " ZONE", null,
                zoned ? offControl(s, name, off) : null));
        page.getChildren().add(PolicyScreen.muted(line));
        if (off) {
            page.getChildren().add(PolicyScreen.card(PolicyScreen.words("Staged: the zone comes off. What it holds goes back to "
                    + "everyone; what was sold in it stays with its owner at what it paid, assessed at the going price from then on.",
                    Palette.SIZE_LABEL + 1, Palette.WARN)));
        }
        page.getChildren().add(sizeCard(s, name, sector));
        page.getChildren().add(priceCard(s, name));
        page.getChildren().add(taxesCard(s, name));
    }

    /** "Take the zone off" - staged, its chip in the tray - or, staged, "Keep it". */
    Node offControl(String s, String name, boolean off) {
        Lever lever = new Lever(offKey(s), possessive(name) + " zone", 0, 0, 1, 1, v -> v > 0 ? "taken off" : "standing",
                v -> { if (v > 0) ui.game.removeZone(s); });
        policy.policyLevers.put(lever.key(), lever);
        return stepChip(off ? "Keep the zone" : "Take the zone off", () -> {
            if (off) {
                policy.unstage(offKey(s));
            } else {
                for (String k : List.of(sizeKey(s), unitKey(s), priceKey(s))) policy.unstage(k);
                policy.stage(offKey(s), 1);
            }
            policy.showPolicyMenu();
        }, true);
    }

    /** HOW MUCH GROUND: the unit's two chips over the dial, staged before → after. */
    VBox sizeCard(String s, String name, Sector sector) {
        Zoning.Size kind = kindOf(policy.staged(unitKey(s), unitOf(kindNow(s))));
        double current = sizeAs(s, kind);
        boolean share = kind == Zoning.Size.SHARE;
        double step = share ? SHARE_STEP : km2Step();
        double top = share ? Zoning.MAX_SIZE_SHARE : km2Top();
        java.util.function.DoubleFunction<String> read = share ? Money::pct2 : v -> LandManager.areaWords(LandManager.sqFt(v));
        Lever unit = new Lever(unitKey(s), possessive(name) + " zone, given", unitOf(kindNow(s)), 0, 1, 1,
                v -> v >= .5 ? "in km²" : "as a share", v -> applyZone(s));
        policy.policyLevers.put(unit.key(), unit);
        Lever lever = new Lever(sizeKey(s), possessive(name) + " zone" + (share ? ", of the dry ground" : ""), current, 0, top, step,
                read, v -> applyZone(s));
        Ladder ladder = policy.ladderOf(lever);
        HBox units = new HBox(6, unitChip(s, "A share of the dry ground", Zoning.Size.SHARE, kind),
                unitChip(s, "km²", Zoning.Size.KM2, kind));
        ZoningView.Page now = ZoningView.page(ui.game, ui.game.getZoning(), s);
        String status = now.plot() == null ? "Its sector has no building that stands on ground."
                : "Measured in " + Formats.plural(now.plot().getName()) + ", "
                        + LandManager.areaWords(now.plot().getLandSqFt()) + " a plot.";
        return dialCard(new DialCard(Icons.ofSector(sector), Palette.MONEY, "HOW MUCH GROUND", SIZE_INFO, null, status,
                List.of(units), ladder, policy.staged(lever.key(), current), sizeEffects(s, now), GROUND_CAVEAT, SIZE_INFO, null),
                520, false);
    }

    /**
     * One unit's chip: lit when it is the unit; a click stages the other -
     * the zone's own ground said in it, or a staged size carried over,
     * snapped to the new unit's step (an unstaged size is the city's, in
     * whichever unit is staged: sizeAs()).
     */
    Label unitChip(String s, String words, Zoning.Size to, Zoning.Size kind) {
        Label c = stepChip(words, () -> {
            if (to == kind) return;
            if (to == kindNow(s)) policy.unstage(unitKey(s)); else policy.stage(unitKey(s), unitOf(to));
            if (policy.isStaged(sizeKey(s))) {
                double step = to == Zoning.Size.SHARE ? SHARE_STEP : km2Step();
                double size = Math.max(0, snapped(convert(policy.staged(sizeKey(s), 0), kind, to), 0, step));
                if (moved(size, sizeAs(s, to), step)) policy.stage(sizeKey(s), size); else policy.unstage(sizeKey(s));
            }
            policy.showPolicyMenu();
        }, to != kind);
        if (to == kind) c.setStyle(c.getStyle() + " -fx-border-color: " + Palette.ACCENT + "; -fx-border-width: 0 0 2 0;");
        return c;
    }

    /** HOW MUCH GROUND's effects at any value of its thumb (pure: the probe reads them): what it carries, what it holds, the ground left free for anyone, its ground at the going price. */
    java.util.function.DoubleFunction<List<Effect>> sizeEffects(String s, ZoningView.Page now) {
        String plural = now.plot() == null ? "plots" : Formats.plural(now.plot().getName());
        return v -> {
            ZoningView.Page then = ZoningView.page(ui.game, stagedZoning(sizeKey(s), v), s);
            List<Effect> out = new ArrayList<>();
            if (now.plot() != null) {
                out.add(Effect.of(plural + " it carries, in all", now.carries(), then.carries(), n -> String.format("%,.0f", n))
                        .delta(d -> String.format("%+,.0f", d).replace('-', '−')));
            }
            out.add(Effect.of("It holds for " + nameOf(s), now.zone().heldSqFt(), then.zone().heldSqFt(), LandManager::areaWords)
                    .colour(Palette.BUSINESS));
            out.add(Effect.of("Free for anyone", now.freeForAnyone(), then.freeForAnyone(), LandManager::areaWords)
                    .colour(Palette.BUILDING));
            out.add(Effect.of("What it holds, at the going price", now.zone().heldAtGoingPrice(), then.zone().heldAtGoingPrice(),
                    Money::money).delta(PolicyScreen::moneyMove));
            return out;
        };
    }

    /** ITS PRICE: 0-100% of the going price a m², and what it gives up. */
    VBox priceCard(String s, String name) {
        double current = priceNow(s);
        Lever lever = new Lever(priceKey(s), possessive(name) + " zone, of the going price", current, 0, Zoning.MAX_PRICE_SHARE,
                PRICE_STEP, PolicyScreen::pct0, v -> applyZone(s));
        Ladder ladder = policy.ladderOf(lever);
        double going = ui.game.getLandManager().getPricePerSqFt();
        String status = "The going business price is " + money(LandManager.perM2(going)) + " a m².";
        ZoningView.Page now = ZoningView.page(ui.game, ui.game.getZoning(), s);
        return dialCard(new DialCard(Icons.COIN, Palette.MONEY, "ITS PRICE", PRICE_INFO, null, status, null, ladder,
                policy.staged(lever.key(), current), priceEffects(s, now), PolicyScreen.CAVEAT_LINE, PolicyScreen.CAVEAT, null),
                520, false);
    }

    /** ITS PRICE's effects at any value of its thumb (pure: the probe reads them): a plot's ground, the price given up on what it holds, the tax relief, THE BUDGET. */
    java.util.function.DoubleFunction<List<Effect>> priceEffects(String s, ZoningView.Page now) {
        return v -> {
            ZoningView.Page then = ZoningView.page(ui.game, stagedZoning(priceKey(s), v), s);
            List<Effect> out = new ArrayList<>();
            if (now.plot() != null) {
                out.add(Effect.of("A " + now.plot().getName() + "'s ground", now.plotAtZone(), then.plotAtZone(), Money::money)
                        .delta(PolicyScreen::moneyMove).info("At the going price it would be " + money(now.plotAtGoing()) + "."));
            }
            out.add(Effect.of("Price given up, on what it holds", now.zone().priceGivenUp(), then.zone().priceGivenUp(),
                    Money::money).delta(PolicyScreen::moneyMove));
            out.add(Effect.of("Property tax relief, a month", now.zone().taxRelief(), then.zone().taxRelief(), Money::money)
                    .delta(PolicyScreen::moneyMove));
            out.add(budget(priceKey(s), v));
            return out;
        };
    }

    /** One dial of a card of several: its words, its key, its ladder (unbuilt) and where its thumb sits. */
    record Dial(Node words, String key, Ladder ladder, double at) { }

    /** A dial card of several dials (ITS TAXES, the forest law): their words and ladders down the left, what they would do at the right, following whichever thumb moves. */
    static VBox multiDialCard(String svg, String title, String info, String status, List<Dial> dials,
                              BiFunction<String, Double, List<Effect>> effectsAt, String caveat, String caveatWhole) {
        HBox head = new HBox(Palette.GAP, iconSquare(svg, Palette.MONEY, 28, 15), PolicyScreen.words(title, Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
        ((Label) head.getChildren().get(1)).setStyle(Palette.strong(Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
        if (info != null) head.getChildren().add(infoButton(info, true));
        head.setAlignment(Pos.CENTER_LEFT);
        VBox left = new VBox(6, head);
        if (status != null) left.getChildren().add(PolicyScreen.words(status, Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        VBox effects = new VBox(8);
        BiFunction<String, Double, Void> fill = (k, v) -> {
            effects.getChildren().clear();
            for (Effect e : effectsAt.apply(k, v)) effects.getChildren().add(beforeAfter(e, EFFECTS));
            return null;
        };
        fill.apply(null, 0.0);
        for (Dial d : dials) {
            left.getChildren().add(d.words());
            left.getChildren().add(d.ladder().wide(520).onTrack(v -> fill.apply(d.key(), v)).build());
        }
        left.setPrefWidth(520);
        left.setMaxWidth(520);
        left.setMinWidth(0);
        Label what = new Label("WHAT IT WOULD DO");
        what.setStyle(Palette.strong(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        VBox right = new VBox(6, what, effects);
        if (caveat != null) {
            right.getChildren().add(infoLine(caveat, caveatWhole != null ? caveatWhole : caveat, true,
                    Palette.SIZE_LABEL, Palette.TEXT_MUTED, EFFECTS));
        }
        right.setMinWidth(260);
        HBox.setHgrow(right, Priority.ALWAYS);
        right.setMaxWidth(Double.MAX_VALUE);
        VBox card = new VBox(10, new HBox(24, left, right));
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle("-fx-padding: 12; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + Palette.EDGE + ";");
        return card;
    }

    /** A tax row's key, as Policy › Taxes' rows have it. */
    static String taxKey(Zoning.Tax t, String s) {
        return switch (t) {
            case PROFIT -> "profit:" + s;
            case SALES -> "sales:" + s;
            case PROPERTY -> "prop:" + s;
        };
    }

    /** "Profit", "Sales", "Property". */
    static String taxName(Zoning.Tax t) {
        String n = t.name().toLowerCase();
        return Character.toUpperCase(n.charAt(0)) + n.substring(1);
    }

    /** A tax row's words over its dial: "PROFIT · the city's 15.00% −2.00 pts → 13.00% · $1.2M last month". */
    static String taxWords(Zoning.Tax t, double city, double offset, double rate, double then, double paid) {
        String moved = Math.abs(then - rate) > 1e-12 ? " → " + pct2(then) : "";
        return taxName(t).toUpperCase() + " · the city's " + pct2(city) + (Math.abs(offset) > 1e-12 ? " " + pts(offset) : "")
                + " = " + pct2(rate) + moved + " · " + money(paid) + " last month";
    }

    /** ITS TAXES: the sector's three offsets, as Policy › Taxes' rows, the same keys. */
    VBox taxesCard(String s, String name) {
        TaxPolicy live = policy.policy();
        TaxPolicy staged = policy.stagedPolicy();
        EconomyManager em = ui.game.getEconomyManager();
        double[] paid = { ui.game.getSectorBooks().get(s).tax(), em.getSalesTaxLedger().getNet(s), em.getPropertyTaxCharged(s) };
        double[] city = { live.getProfitTaxRate(), live.getSalesTaxRate(), live.getPropertyTaxRate() };
        double[] offset = { live.getProfitOffset(s), live.getSalesOffset(s), live.getPropertyOffset(s) };
        double[] then = { staged.effectiveProfitRate(s), staged.effectiveSalesRate(s), staged.effectivePropertyRate(s) };
        java.util.function.DoubleFunction<String> propPts = o -> String.format("%+.2f pts", o * 100).replace('-', '−');
        List<Dial> dials = new ArrayList<>();
        String[] names = {", profit", ", sales", ", property"};
        for (Zoning.Tax t : Zoning.Tax.values()) {
            int i = t.ordinal();
            boolean property = t == Zoning.Tax.PROPERTY;
            double max = property ? TaxPolicy.MAX_PROPERTY_TAX : TaxPolicy.MAX_OFFSET;
            Lever lever = new Lever(taxKey(t, s), name + names[i], offset[i], -max, max,
                    property ? PolicyScreen.STEP_PROPERTY : PolicyScreen.STEP_INCOME, property ? propPts : Money::pts,
                    v -> ui.game.setZoneTax(s, t, v));
            Ladder ladder = policy.ladderOf(lever).stepReads(PolicyScreen::stepWord);
            dials.add(new Dial(PolicyScreen.words(taxWords(t, city[i], offset[i], ui.game.zoneTaxRate(s, t), then[i], paid[i]),
                    Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL), lever.key(), ladder, policy.staged(lever.key(), offset[i])));
        }
        PolicyPreview.TaxTake now = PolicyPreview.taxTake(ui.game, live);
        BiFunction<String, Double, List<Effect>> effects = (k, v) -> {
            TaxPolicy after = policy.stagedPolicyWith(k, v == null ? 0 : v);
            PolicyPreview.TaxTake take = PolicyPreview.taxTake(ui.game, after);
            List<Effect> out = new ArrayList<>();
            out.add(Effect.of(possessive(name) + " profit tax, a month", now.profit(s), take.profit(s), PolicyScreen::amount)
                    .delta(PolicyScreen::moneyMove));
            out.add(Effect.of(possessive(name) + " sales tax, a month", now.sales(s), take.sales(s), PolicyScreen::amount)
                    .delta(PolicyScreen::moneyMove).info(PolicyScreen.SALES_SCALED));
            out.add(Effect.of(possessive(name) + " property tax, a month", now.property(s), take.property(s), PolicyScreen::amount)
                    .delta(PolicyScreen::moneyMove));
            out.add(budget(k, v == null ? 0 : v));
            return out;
        };
        return multiDialCard(Icons.GOVERNMENT, "ITS TAXES", TAXES_INFO, sameDials(name), dials, effects,
                PolicyScreen.CAVEAT_LINE, PolicyScreen.CAVEAT);
    }

    /** The study's line: "The same dials as Policy › Taxes: a move here moves it for every Agriculture building." */
    static String sameDials(String name) {
        return "The same dials as Policy › Taxes: a move here moves it for every " + name + " building.";
    }

    /* =====================================================================
       THE FOREST LAW (board 3)
       ===================================================================== */

    /** The law's (i) on its page. */
    static final String FOREST_INFO = LAW_INFO + " What it would do is the long run: the protected share of the forest's "
            + "wood stands for ever, the rest may be cut; the open forest is what a building may take. Forestry's crews, "
            + "which will cut it, come later: this is what the law holds now.";

    void forestPage(VBox page) {
        Game g = ui.game;
        Zoning live = g.getZoning();
        Zoning staged = stagedZoning();
        ZoningView.Forest now = ZoningView.forest(g, live);
        ZoningView.Forest then = ZoningView.forest(g, staged);
        boolean moved = policy.isStaged(PROTECTED_KEY) || policy.isStaged(KEPT_KEY);
        page.getChildren().add(sectionHead("THE FOREST", null, PolicyScreen.quiet(LandManager.areaWords(now.forestSqFt())
                + " of the city's " + LandManager.areaWords(g.getLandManager().getOwnedSqFt()) + " dry ground")));
        VBox bars = PolicyScreen.card(PolicyScreen.words(moved ? "Under the staged law" : "Under the law as it stands",
                Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL), forestBar(then, 18));
        if (moved) {
            bars.getChildren().addAll(PolicyScreen.muted("As it stands"), forestBar(now, 10));
        }
        page.getChildren().add(bars);
        if (!(now.forestSqFt() > 0)) {
            page.getChildren().add(PolicyScreen.muted("The city owns no forest: a law keeps nothing until it buys forest ground."));
        }

        List<Dial> dials = new ArrayList<>();
        Lever prot = new Lever(PROTECTED_KEY, "Forest protected", live.protectedShare(), 0, 1, LAW_STEP, PolicyScreen::pct0,
                v -> applyLaw());
        Lever kept = new Lever(KEPT_KEY, "Forest kept from building", live.keptShare(), 0, 1, LAW_STEP, PolicyScreen::pct0,
                v -> applyLaw());
        dials.add(new Dial(PolicyScreen.words("PROTECTED: never cut, never built on", Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL),
                prot.key(), policy.ladderOf(prot), policy.staged(prot.key(), prot.current())));
        dials.add(new Dial(PolicyScreen.words("KEPT FROM BUILDING: the protected, and the working forest - cut, never built on",
                Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL), kept.key(), policy.ladderOf(kept), policy.staged(kept.key(), kept.current())));
        BiFunction<String, Double, List<Effect>> effects = (k, v) -> forestEffects(now, k, v == null ? 0 : v);
        page.getChildren().add(multiDialCard(Icons.TREES, "THE FOREST LAW", FOREST_INFO,
                "The kept share is never under the protected: a law of more protected than kept keeps the protected.",
                dials, effects, "In the long run, on the forest as it stands.", FOREST_INFO));
    }

    /** WHAT IT WOULD DO at any value of either thumb (pure: the probe reads them): the wood kept standing for ever, the most that may be cut, the forest a building may take, the ground free for anyone. */
    List<Effect> forestEffects(ZoningView.Forest now, String key, double v) {
        ZoningView.Forest then = ZoningView.forest(ui.game, stagedZoning(key, v));
        List<Effect> out = new ArrayList<>();
        out.add(Effect.of("Forest standing for ever", now.at().protectedWood(), then.at().protectedWood(), ZoningScreen::m3)
                .colour(FOREST_PROTECTED));
        out.add(Effect.of("The most that may be cut", now.at().cuttableWood(), then.at().cuttableWood(), ZoningScreen::m3)
                .colour(FOREST_WORKING));
        out.add(Effect.of("Forest a building may take", now.openStandingSqFt(), then.openStandingSqFt(), LandManager::areaWords)
                .colour(FOREST_OPEN));
        out.add(Effect.of("Ground free for anyone", now.at().freeForAnyone(), then.at().freeForAnyone(), LandManager::areaWords)
                .colour(Palette.BUILDING));
        return out;
    }

    /* =====================================================================
       THE HUB'S CARD (Policy's fifth area card)
       ===================================================================== */

    /** Policy's ZONING area card: the zoned ground, the dry ground as slices, the zones' line and the law. */
    VBox areaCard() {
        ZoningView.Figures f = ZoningView.figures(ui.game);
        List<ZoningView.Part> parts = ZoningView.ground(ui.game);
        List<Slice> slices = List.of(
                new Slice("Built", ZoningView.total(parts, ZoningView.Kind.BUILT) + ZoningView.total(parts, ZoningView.Kind.USED), BUILT_COLOUR),
                new Slice("Held for the zones", ZoningView.total(parts, ZoningView.Kind.HELD), Palette.BUSINESS),
                new Slice("Free for anyone", ZoningView.total(parts, ZoningView.Kind.BARE) + ZoningView.total(parts, ZoningView.Kind.OPEN), FREE_COLOUR),
                new Slice("Forest kept", ZoningView.total(parts, ZoningView.Kind.WORKING) + ZoningView.total(parts, ZoningView.Kind.PROTECTED), FOREST_WORKING));
        String line = f.zones() == 0 ? "no zone: the ground is free for anyone"
                : f.zones() + (f.zones() == 1 ? " zone holds " : " zones hold ") + LandManager.areaWords(f.heldSqFt());
        String foot = !f.lawSet() ? "no forest law" : "forest: " + pct0(f.protectedShare()) + " protected, " + pct0(f.keptShare()) + " kept";
        return policy.areaCard(Icons.ZONING, "ZONING", f.zones() == 0 ? "none zoned" : LandManager.areaWords(f.zonedSqFt()) + " zoned",
                areaSlices(slices), line, foot, () -> policy.open(PolicyScreen.ZONING, THE_GROUND));
    }

    /** Area slices as one bar, a part's name and ground its tooltip. */
    static SegmentBar areaSlices(List<Slice> slices) {
        List<Segment> parts = new ArrayList<>();
        for (Slice s : slices) {
            parts.add(new Segment(Math.max(0, s.amount()), s.colour(), false, null, null,
                    s.name() + "\n" + LandManager.areaWords(s.amount()), null));
        }
        return segmentBar(parts, 0, List.of(), 0, 12);
    }

    static String pct0(double share) { return PolicyScreen.pct0(share); }
}
