package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Pieces.*;

/**
 * The land office: whether the city has room to grow, and which ground to
 * buy - since 0.7.61 round the city's map (batch J4; the project's
 * spec-land.md 2.8): a head with how the city pays; the HERO ROW, the map
 * (MapView, 600 x 400: the city's land and its forty offers hatched over the
 * world, Expand to lay it over the whole window) and beside it THE CITY - its
 * size in km2, its share of the world's iron, the four sides as chips and the
 * chosen side's ten offers as rows, each its lane, its size, its ground and
 * water as a bar, its deposits, its price, its price a dry km2 and Buy;
 * then THE GROUND - the ground free now and the best N as one bar, "Buy the
 * best N" over it; what the ground is worth to the city in four cards - the
 * margin, the ground on top of the build, the ore and oil, who is waiting;
 * the price of ground over the city's life behind "details"; and, when the
 * city is short, the funding page in the same frame. (Until 0.7.60 the best
 * nine of the forty offers stood as a three-by-three of cards.)
 *
 * BEST VALUE is the office's best: the cheapest dry ground a square foot
 * with any ore and water in it paid for inside its price (Game.landShelf()'s
 * order) - Jerus's "the best one is always at the top left", the shelf's
 * first card until 0.7.60; since 0.7.61 the tag on its row and on its side's
 * chip, and "Buy the best N" buys the shelf's first N. It is not
 * LandMarket.bestValue(), the most dry ground a dollar among the offers not
 * mostly sea, which Build's shortcut buys for room (Game.bestOffer()). MOST
 * ORE is LandMarket.richest() in iron.
 *
 * In US dollars since 0.7.6: every offer shows its dollar price and what
 * that costs in local money at today's rate, and a chip pair at the top
 * chooses whether the treasury converts cash for it (the default) or pays
 * out of the vault. Since 0.7.13 an offer's price is in the money the toggle
 * pays in, its button stays live and, short, opens the funding page sized to
 * the gap (showLandFunding()), and a control buys the best N at once
 * (nextPlots()); since 0.7.61 its size is in square kilometres, and since
 * 0.7.64 the ground free and the bar's figures too.
 *
 * WHY THE REDRAW (0.7.26). Jerus, on the screens not yet redone: "the others
 * are still full of text and the design could be more intuitive and fun". The
 * office was a ledger round its cards - a paragraph pinned over them, a
 * hand-built position bar, four statements under them - and two of its
 * verdicts were wrong: a red "USED" at 90% of the ground built on, where a
 * city sits for centuries with nothing waiting (it takes NEEDS YOU's GROUND
 * row now, CityNeeds.ground()), and "Who is waiting", which matched a word and
 * missed a sector refused at the last moment (it reads
 * Game.getLandBlockedSectors() now). Nothing it said is gone: what is not on a
 * card is behind an (i). The design study is the project's
 * spec-land-0726.md.
 *
 * What it says is worked out in methods that make no node (strip(),
 * groundFigures(), cityLine(), sideChips(), sideRows(), margin(), and the
 * rest), so a probe without the toolkit can read every word on a played
 * city; the drawing methods lay those out. Split out of UserInterface on 2026-09-18; the rail calls
 * showLandMenu(), as do Build's LAND FREE and its no-land and no-deposit
 * pages, NEEDS YOU's GROUND row, HOW THE CITY IS's GROUND USED and the
 * inbox's landlock notice.
 */
final class LandScreen {

    /** The window this screen draws into: its game, its root, its clearMenu(). */
    private final UserInterface ui;

    LandScreen(UserInterface ui) { this.ui = ui; }

    /* =====================================================================
       THE LAND OFFICE (0.7.26)

       The question is "have I room for the city to grow, and which ground
       should I buy?", and the page answers it top down (since 0.7.61):

         the head        - "Land office", its (i), the pay toggle with a line
                           under it, and the way back to Build;
         THE HERO ROW    - the map at the left, 600 x 400 (MapView), and at
                           the right THE CITY, the sides as chips and the
                           chosen side's ten offers as rows (THE CITY AND ITS
                           SIDES);
         THE BEST N      - the Buy-the-best-N control, then THE GROUND: four
                           cells (the ground free, who is waiting, what the
                           world asks, what investors pay) and the ground as
                           a bar: free now solid, then the best N offers as
                           numbered ghosts, on a scale of the two together so
                           the bar always reads - at "everything owned" the
                           free ground of a built-over city is a hair;
         WHAT THE GROUND IS WORTH - the margin, the ground on top of the
                           build, the ore and oil, who is waiting;
         details         - the world's price of ground over the city's life.

       NOT A RING OF "USED": the share of the ground built on sits at 95-100%
       for centuries with nothing wrong (the design study measured 120 months
       of 120 at 95% or more in the 2,400-month playtest city, none of them
       with a sector waiting), so a ring of it would be red for good and say
       nothing. Its verdict is the free ground's, NEEDS YOU's.

       PRICES ARE NEUTRAL, red only when no way pays without debt
       (canAffordParcel(): converting, the cash is short; from the vault, the
       cash cannot top it up either). A Buy button that needs a loan, or the
       vault short, says so in its label and is outlined (0.7.34; grey
       before) - where a green price sat beside "the vault is short" on the
       same card before. Since 0.7.61 a row's button says "Buy", filled, or
       "Fund", outlined, the price beside it in its own column.
       ===================================================================== */

    /** How many offers the best-N control buys; kept across redraws, held inside 1..listed. */
    int nextCount = 5;

    /** Whether "details" is open; kept while the game runs, as the page's place is. */
    boolean detailsOpen = false;

    /* ------------- what the page remembers between draws (0.7.26) -------------
     *
     * Only for the moving parts: a purchase widens the bar's solid ground
     * from what was free before it and pops "+N deposits" on the Ore card;
     * the plots the purchase brought onto the shelf carry NEW for the rest of
     * the month; and the figures the month moves (THE WORLD ASKS, a plot's
     * local price) pop when it turns. Every figure is still read from the
     * model on every draw; these only say what changed. A new city or a load
     * forgets them (forget(), from the window's anotherCity()).
     * ------------------------------------------------------------------------- */

    /** The ground free just before the purchase being drawn, or NaN: the solid segment grows from it. */
    private double freeBeforeBuying = Double.NaN;

    /** The deposits owned just before it, or -1: the Ore card pops the difference. */
    private int depositsBeforeBuying = -1;

    /** The plot ids on the shelf at the last draw, or null before the first. */
    private java.util.Set<Integer> lastShelf;

    /** An offer whose id is above this is NEW (ids only grow: LandMarket's next id)... */
    private int newAbove = Integer.MAX_VALUE;

    /** ...in this month only. */
    private int newMonth = -1;

    /** The month, the rate and the world's dollar price at the last draw: what the month's pop compares. */
    private int shownMonth = -1;
    private double shownRate = Double.NaN, shownWorldUsd = Double.NaN;

    /** The waiting card as last drawn, for the strip's click to scroll to. */
    private Region waitingCard;

    /** The map, the small view's and Expand's (MapView); made the first time the office is drawn, as the toolkit is up by then. */
    private MapView map;

    /** The side whose ten offers the rows show (0 north, 1 east, 2 south, 3 west), -1 until the office's best decides it; and the offer picked on it, its lane, or -1. */
    int side = -1, lane = -1;

    /** A new city or a load: nothing on its shelf is new to the player, and nothing has just been bought. */
    void forget() {
        side = -1;
        lane = -1;
        if (map != null) map.forget();
        freeBeforeBuying = Double.NaN;
        depositsBeforeBuying = -1;
        lastShelf = null;
        newAbove = Integer.MAX_VALUE;
        newMonth = -1;
        shownMonth = -1;
        shownRate = Double.NaN;
        shownWorldUsd = Double.NaN;
    }

    void showLandMenu() {
        // The month's pop is for a page being watched as it turns, as the clock's is: arriving, nothing pops.
        if (!"showLandMenu".equals(ui.currentScreen)) shownMonth = -1;
        ui.clearMenu("showLandMenu", () -> showLandMenu());
        noteShelf();

        VBox page = widePage();
        waitingCard = null;
        chooseSide();

        Next next = next();
        page.getChildren().addAll(head(null), hero());
        String receipt = ui.game.getLastLandReceipt();
        if (receipt != null && !receipt.isEmpty()) page.getChildren().add(receiptLine(receipt));
        page.getChildren().addAll(
                sectionHead(BEST_HEAD, offerInfo(), nextPlots(next)),
                groundPanel(next));
        page.getChildren().addAll(
                sectionHead("WHAT THE GROUND IS WORTH", hint("to the city, today")),
                worthCards(),
                details());
        ui.rootMenu.getChildren().add(page);

        // What was just bought has been drawn; the month's figures are the shown ones now.
        freeBeforeBuying = Double.NaN;
        depositsBeforeBuying = -1;
        shownMonth = ui.game.getMonth();
        shownRate = ui.game.getForeignAccounts().getRate();
        shownWorldUsd = ui.game.getLandManager().getGroundUsdPerSqFt();
    }

    /**
     * NEW, worked out: when the shelf's ids differ from the last draw's, the
     * plots above the last draw's highest id arrived since - the refill after
     * a purchase (LandManager.buyParcel() refills the window at once). They
     * keep the tag until the month turns.
     */
    void noteShelf() {
        int month = ui.game.getMonth();
        if (newMonth != month) newAbove = Integer.MAX_VALUE;
        java.util.Set<Integer> now = new java.util.HashSet<>();
        for (LandParcel p : ui.game.landShelf()) now.add(p.getId());
        if (lastShelf != null && !now.equals(lastShelf)) {
            int highest = Integer.MIN_VALUE;
            for (int id : lastShelf) highest = Math.max(highest, id);
            newAbove = highest;
            newMonth = month;
        }
        lastShelf = now;
    }

    /** Whether this plot came onto the shelf since the player last saw it, this month. */
    boolean isNew(LandParcel p) {
        return newMonth == ui.game.getMonth() && p.getId() > newAbove;
    }

    /* =====================================================================
       THE WORDS. Everything the office says, worked out without a node -
       what a probe on a played city reads - each from the model's own
       getters. The drawing below lays these out.
       ===================================================================== */

    /** The city's money's mark: "D$". */
    String here() { return ui.game.getCurrency().qualifiedSymbol(); }

    /** A ground price, kept a square foot, a square metre in the city's money: "D$500.74" (a square foot's D$46.52; since 0.7.68, Money.groundPrice()). */
    String local(double thousandsPerSqFt) { return marked(here(), groundPrice(thousandsPerSqFt)); }

    /** ...in the world's: "US$264.15". */
    static String dollars(double thousandsPerSqFt) { return marked(Currency.FOREIGN_SYMBOL, groundPrice(thousandsPerSqFt)); }

    /** A ground price a square metre with its direction: "+D$313.88", "−D$33.37". */
    String signedLocal(double thousandsPerSqFt) {
        return (thousandsPerSqFt < 0 ? "−" : "+") + local(Math.abs(thousandsPerSqFt));
    }

    /** The House's plot, in square feet: what "room for N houses" divides by; 0 if the catalogue has none. */
    double houseSqFt() {
        BuildingsTemplate house = ui.game.getBuildingManager().getTemplateByName("House");
        return house == null ? 0 : house.getLandSqFt();
    }

    /** "room for 1,209 houses", or "" when there is no House to count in. */
    String room(double sqFt) {
        double house = houseSqFt();
        if (house <= 0) return "";
        double n = Math.floor(sqFt / house);
        return "room for " + formatter.format(n) + (n == 1 ? " house" : " houses");
    }

    /** Some plots, as the office's notes say them: "The plot", "5 plots". */
    static String plots(int n) { return n == 1 ? "The plot" : n + " plots"; }

    /* ----------------------------- the head ----------------------------- */

    /** The page's (i): who pays for the ground, and who does not. */
    static final String WHO_PAYS = "Investors build only on ground the city owns, and pay the city for each "
            + "plot they use. The city's own buildings stand on it free.";

    /** The pay toggle's two chips: "Convert D$" and "From the vault". */
    String[] payNames() {
        return new String[] { "Convert " + here(), "From the vault" };
    }

    /** The toggle's tooltips: its 0.7.6 names, which the chips shortened. */
    static final String[] PAY_TIPS = { "Pay by converting cash", "Pay from the vault" };

    /**
     * The line under the toggle, and the whole sentence behind its (i) - the
     * "how" sentence (0.7.6; its vault half 0.7.13's), word for word:
     * converting, "D$0.7072 per US$ · vault US$6.8M untouched"; from the
     * vault, "vault holds US$6.8M (D$4.8M)".
     */
    String[] payCaption() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        String here = here();
        if (ui.game.isLandPaidFromVault()) {
            return new String[] {
                    "vault holds " + usd(fx.getReservesUsd()) + " (" + marked(here, money(fx.getReserves())) + ")",
                    String.format("Land is priced in US dollars. Paying from the vault spends its "
                            + "dollars and moves no cash; it holds %s (%s at today's rate), and a plot "
                            + "dearer than that opens the funding page: dollars borrowed abroad into the vault, or what the "
                            + "vault holds with the rest converted from cash.",
                            usdFull(fx.getReservesUsd()), marked(here, moneyFull(fx.getReserves()))) };
        }
        return new String[] {
                here + fxRate(fx.getRate()) + " per US$ · vault " + usd(fx.getReservesUsd()) + " untouched",
                String.format("Land is priced in US dollars. Converting buys exactly the dollars "
                        + "a plot costs out of cash, at %s%s to the dollar today, and leaves the "
                        + "vault's %s where it is.",
                        here, fxRate(fx.getRate()), usdFull(fx.getReservesUsd())) };
    }

    /* ----------------------------- the ground ----------------------------- */

    /** One cell of THE GROUND's strip: its label, its figure, its line, and the figure's colour. */
    record Cell(String label, String value, String note, String tone) { }

    /**
     * The four cells. GROUND FREE is coloured by NEEDS YOU's GROUND row
     * (CityNeeds.ground(): green, a block free or less amber, none red), as
     * Build's LAND FREE and the left panel are - not by the share used, which
     * is in its line, in no colour. WAITING ON GROUND is red while any sector
     * is; INVESTORS PAY is green or red by the margin's sign.
     */
    Cell[] strip() {
        Game g = ui.game;
        LandManager land = g.getLandManager();
        double free = land.getAvailableSqFt();
        CityNeeds.Need ground = CityNeeds.ground(g, SummaryScreen.WORDS);
        java.util.Set<String> blocked = g.getLandBlockedSectors();
        int waiting = blocked.size();
        double margin = land.getMarginPerSqFt();
        return new Cell[] {
                new Cell("GROUND FREE", LandManager.areaWords(free),
                        String.format("%.1f%%", land.getUtilisation() * 100)
                                + " of " + LandManager.areaWords(land.getOwnedSqFt()) + " built on",
                        BuildScreen.verdict(ground.level())),
                new Cell("WAITING ON GROUND", waiting + (waiting == 1 ? " sector" : " sectors"),
                        waiting == 0 ? "every sector has room" : String.join(", ", waitingNames()),
                        waiting > 0 ? Palette.BAD : Palette.TEXT_HEAD),
                new Cell("THE WORLD ASKS", dollars(land.getGroundUsdPerSqFt()) + "/m²",
                        local(land.getAcquisitionCostPerSqFt()) + " today · crowding " + times(land.getCrowdingPremium()),
                        Palette.TEXT_HEAD),
                new Cell("INVESTORS PAY", local(land.getPricePerSqFt()) + "/m²",
                        signedLocal(margin) + " a m² " + (margin < 0 ? "under" : "over") + " the world",
                        margin < 0 ? Palette.BAD : Palette.GOOD) };
    }

    /** The offers "the best N" buys, in the shelf's order. */
    List<LandParcel> nextShelf(Next next) {
        List<LandParcel> out = new ArrayList<>();
        LandMarket market = ui.game.getLandManager().getMarket();
        for (int id : next.ids()) {
            LandParcel p = market.find(id);
            if (p != null) out.add(p);
        }
        return out;
    }

    /**
     * The big figures beside the strip: the ground free and what it holds,
     * then what the best N would leave free - "0.0301 km² free", "room for 40
     * houses", "→ 3.49 km² free after the best 5", "room for 4,704 houses"
     * (square feet until 0.7.64; under a hundredth of a km² in m² since
     * 0.7.68, LandManager.areaWords()).
     * The last two are empty with nothing on the shelf.
     */
    String[] groundFigures(Next next) {
        double free = ui.game.getLandManager().getAvailableSqFt();
        double after = free;
        for (LandParcel p : nextShelf(next)) after += p.getSizeSqFt();
        boolean any = !next.ids().isEmpty();
        return new String[] {
                LandManager.areaWords(free) + " free",
                room(free),
                any ? "→ " + LandManager.areaWords(after) + " free after the best " + (next.n() == 1 ? "offer" : String.valueOf(next.n())) : "",
                any ? room(after) : "" };
    }

    /** The bar's scale: the ground free and the best N together. */
    double groundScale(Next next) {
        double scale = ui.game.getLandManager().getAvailableSqFt();
        for (LandParcel p : nextShelf(next)) scale += p.getSizeSqFt();
        return scale;
    }

    /**
     * The bar's segments: the ground free, solid pink; then the best N, each
     * a ghost in the light pink, numbered in the shelf's order, with a sand
     * stripe where it holds ore, its side and lane, size and price on hover,
     * and a click that picks it - its side's rows, its band lit on the map.
     */
    List<Segment> groundSegments(Next next, java.util.function.IntFunction<Runnable> go) {
        double free = ui.game.getLandManager().getAvailableSqFt();
        String roomFree = room(free);
        List<Segment> parts = new ArrayList<>();
        parts.add(new Segment(free, Palette.BUILDING, false, null, null,
                "free now · " + LandManager.areaWords(free) + (roomFree.isEmpty() ? "" : " · " + roomFree), null));
        List<LandParcel> plots = nextShelf(next);
        for (int i = 0; i < plots.size(); i++) {
            LandParcel p = plots.get(i);
            parts.add(new Segment(p.getSizeSqFt(), Palette.BUILDING_LIGHT, true, p.hasIron() ? Palette.ORE : null,
                    String.valueOf(i + 1),
                    "offer " + (i + 1) + " · " + p.where() + " · " + LandManager.areaWords(p.getSizeSqFt()) + " dry · " + priceInToggle(p)
                            + (p.hasIron() ? " · ore under it" : "") + "\nClick to pick it on the map.",
                    go == null ? null : go.apply(i)));
        }
        return parts;
    }

    /** The bar's one tick: NEEDS YOU's line, the free ground under which the GROUND row is listed. */
    List<Tick> groundTicks() {
        CityNeeds.Need ground = CityNeeds.ground(ui.game, SummaryScreen.WORDS);
        return List.of(new Tick(ground.yellow(), Palette.TEXT_MUTED, 2, "NEEDS YOU's line",
                "NEEDS YOU lists the ground at " + LandManager.areaWords(ground.yellow()) + " free or less, and in red at none."));
    }

    /** A plot's price in the money the toggle pays in: "D$149.7M" converting, "US$211.7M" from the vault. */
    String priceInToggle(LandParcel p) {
        return ui.game.isLandPaidFromVault() ? usd(p.getPriceUsd())
                : marked(here(), money(p.localPrice(ui.game.getForeignAccounts().getRate())));
    }

    /* ----------------------------- buy the best N ----------------------------- */

    /**
     * What "Buy the best N" buys and says: how many, out of how many listed,
     * their ids, the total in the toggle's money (neutral, red only when no
     * way pays without debt - canAffordLandParcels()), the other money small,
     * the button's words (a Pieces.Press since 0.7.34) and whether it opens
     * the funding page. The best N are the shelf's first N
     * (Game.nextLandParcels()): the cheapest dry ground a square foot, from
     * every side ("Buy the next N" until 0.7.60).
     */
    record Next(int n, int listed, List<Integer> ids, String total, String totalTone, String other,
                Pieces.Press button, String buttonTip, boolean funding) { }

    Next next() {
        Game g = ui.game;
        int listed = g.landShelf().size();
        nextCount = Math.max(1, Math.min(nextCount, Math.max(1, listed)));
        List<Integer> ids = g.nextLandParcels(nextCount);
        boolean vault = g.isLandPaidFromVault();
        double usdTotal = g.landPriceUsd(ids);
        double localTotal = g.landPriceLocal(ids);
        boolean funding = g.landNeedsFunding(ids);
        String n = nextCount == 1 ? "offer" : String.valueOf(nextCount);
        return new Next(nextCount, listed, ids,
                vault ? usd(usdTotal) : marked(here(), money(localTotal)),
                g.canAffordLandParcels(ids) ? Palette.TEXT_HEAD : Palette.BAD,
                vault ? marked(here(), money(localTotal)) + " at today's rate" : usd(usdTotal) + " listed",
                // The total stands just left of the button, so the button says the count, not the price again.
                !funding ? new Pieces.Press(Pieces.Look.GO, "Buy the best " + n, null)
                        : vault ? new Pieces.Press(Pieces.Look.CREDIT, "Buy the best " + n, "the vault is short: ways to pay")
                        : new Pieces.Press(Pieces.Look.CREDIT, "Buy the best " + n + " on credit", null),
                !funding ? (nextCount == 1 ? "Buys the best offer, as its own row's Buy would."
                                : "Buys the best " + nextCount + " offers, each as its own row's Buy would.")
                        : vault ? "The vault is short of them: opens the funding page - dollars borrowed abroad,"
                                + " or the vault's dollars with the rest converted from cash."
                        : "The cash is short of them: opens the funding page - a loan sized to the gap.",
                funding);
    }

    /** The section's head over THE GROUND: the best N, from every side. */
    static final String BEST_HEAD = "THE BEST OFFERS · cheapest dry ground first, from every side";

    /**
     * The section's (i): the twenty-four offers, six a side, place by place;
     * how the office ranks them (BEST VALUE, the best N); and the blocks
     * every offer is made of, as LandMarket lists them (0.7.67: whole blocks
     * of the city's level against its edge, spec-grid 2.2).
     */
    String offerInfo() {
        LandMarket market = ui.game.getLandManager().getMarket();
        return String.format("The office lists up to %d offers on each side of the city, %d in all: each a rectangle "
                        + "of whole blocks against the city's edge in one of a side's %d places, numbered 1 to %d from the "
                        + "left as you face out; a place with no room waits. Pick a side by its chip or on the map to see "
                        + "its offers; the map hatches them all, and a row lights its own. "
                        + "BEST VALUE is the cheapest dry ground a km², any ore and water in an offer paid for "
                        + "inside its price; \"Buy the best N\" buys the N cheapest so, from every side. Blocks are %,.0f m "
                        + "a side now, the largest of which six fit across the city. "
                        + "An offer keeps the ground and the US$ price it was listed at; buying one lists the next in its "
                        + "place, the nearest free ground there.",
                LandMarket.OFFERS_A_SIDE, LandMarket.OFFERS, LandMarket.OFFERS_A_SIDE, LandMarket.OFFERS_A_SIDE,
                market.getBlockPlots() * World.PLOT_M);
    }

    /* ----------------------------- THE CITY AND ITS SIDES (0.7.61) -----------------------------
     *
     * Jerus's plan for the office: "click a side to see its 10 offers (km2,
     * water share, deposits with amounts, price)", the map always in view,
     * the city's total size in km2 shown. THE CITY's two lines, the four
     * sides as chips - each with its cheapest dry ground a km2, and BEST
     * VALUE on the side holding the office's best - and the chosen side's ten
     * offers as rows, lane 1 to 10 from the left facing out.
     * ------------------------------------------------------------------------------------------ */

    /** A size in km2 as THE CITY writes it: a decimal from 1 km2 up, grouped - "107.8", "1,760,034.2" - three figures under it, "0.312". */
    static String km2Figure(double km2) {
        if (!(km2 > 0)) return "0";
        if (km2 >= 1) return String.format("%,.1f", km2);
        return new BigDecimal(km2).round(new MathContext(3)).stripTrailingZeros().toPlainString();
    }

    /** THE CITY's size, the land's five areas but forest: "107.8 km² · 89.6 dry · 0 fresh · 18.2 sea" (spec-land 2.8). */
    String cityLine() {
        CityLand land = ui.game.getCityLand();
        return km2Figure(land.totalKm2(CityLand.TOTAL)) + " km² · " + km2Figure(land.totalKm2(CityLand.DRY)) + " dry · "
                + km2Figure(land.totalKm2(CityLand.FRESH)) + " fresh · " + km2Figure(land.totalKm2(CityLand.SEA)) + " sea";
    }

    /** The world's line: "of the world's 1.12 Pt of iron, the city owns 0.0000452%" - its share of the world's tonnes, to three figures. */
    String worldLine() {
        LandManager land = ui.game.getLandManager();
        double world = land.getWorldTotal(Resource.IRON), owned = land.getOwnedAmount(Resource.IRON);
        String share = owned > 0 && world > 0
                ? new BigDecimal(100 * owned / world).round(new MathContext(3)).stripTrailingZeros().toPlainString() + "%"
                : "none of it";
        return "of the world's " + LandMap.tonnes(world) + " of iron, the city owns " + share;
    }

    /** Thousands of US dollars in the money the toggle pays in: as listed from the vault, at today's rate converting. */
    String inToggle(double usdThousands) {
        return ui.game.isLandPaidFromVault() ? usd(usdThousands)
                : marked(here(), money(usdThousands * ui.game.getForeignAccounts().getRate()));
    }

    /** An offer's price a dry km2, in the toggle's money: "D$5.6M"; "—" with no dry ground. */
    String perDryKm2(LandParcel p) {
        return p.getDryKm2() > 0 ? inToggle(p.getPriceUsd() / p.getDryKm2()) : "—";
    }

    /** The office's best offer, the shelf's first (BEST VALUE), or null with nothing listed. */
    LandParcel officeBest() {
        List<LandParcel> shelf = ui.game.landShelf();
        return shelf.isEmpty() ? null : shelf.get(0);
    }

    /** The side shown before the player picks one: the office's best's, or the north. */
    void chooseSide() {
        if (side >= 0 && side < CityLand.SIDES) return;
        LandParcel best = officeBest();
        side = best == null ? 0 : best.getSide();
        lane = -1;
    }

    /** One side's chip: its name, its cheapest dry ground a km2 in the toggle's money, and whether it holds the office's best. */
    record SideChip(int side, String name, String best, boolean bestValue) { }

    List<SideChip> sideChips() {
        LandMarket market = ui.game.getLandManager().getMarket();
        LandParcel office = officeBest();
        List<SideChip> out = new ArrayList<>();
        for (int s = 0; s < CityLand.SIDES; s++) {
            LandParcel cheapest = null;
            for (LandParcel p : market.offersOn(s)) {
                if (p.getDryKm2() > 0 && (cheapest == null || p.getUsdPerSqFt() < cheapest.getUsdPerSqFt())) cheapest = p;
            }
            out.add(new SideChip(s, CityLand.sideName(s), cheapest == null ? "no dry ground" : perDryKm2(cheapest) + " a km²",
                    office != null && office.getSide() == s));
        }
        return out;
    }

    /** A deposit on a row: its resource's colour, its sites and amount - "3 · 38.4 Mt" - and the tooltip's line. */
    record DepositWords(String colour, String words, String tip) { }

    /**
     * One offer as its row: its lane (1 to 10), its size in km2, its dry,
     * fresh and sea shares for the bar, its deposits (the first ROW_DEPOSITS,
     * and "+N" for the rest), its price in the toggle's money and colour (red
     * only when no way pays without debt), its price a dry km2, its tag
     * (BEST VALUE, NEW, MOST ORE or MOSTLY SEA, the first that holds), Buy or
     * Fund, and the row's tooltip.
     */
    record Row(LandParcel parcel, String lane, String km2, double dry, double fresh, double sea, List<DepositWords> deposits,
               String more, String price, String priceTone, String perKm2, TagWords tag, String button, String buttonTip,
               boolean funding, String tip) { }

    /** Deposits a row shows by name before "+N": 2... */
    static final int ROW_DEPOSITS = 2;

    /** ...while their words run to no more than this many characters together, else one: 20 - "8 · 136 Mt" and "1 · 179 kt" and "+1" measured 152.8 px in the column's 158 at 9 px Plex Mono. */
    static final int ROW_DEPOSIT_CHARS = 20;

    /** A side's ten offers, lane by lane. */
    List<Row> sideRows(int side) {
        LandMarket market = ui.game.getLandManager().getMarket();
        LandParcel office = officeBest(), richest = market.richest(Resource.IRON);
        List<Row> out = new ArrayList<>();
        for (LandParcel p : market.offersOn(side)) out.add(row(p, office, richest));
        return out;
    }

    Row row(LandParcel p, LandParcel office, LandParcel richest) {
        Game g = ui.game;
        boolean vault = g.isLandPaidFromVault();
        double rate = g.getForeignAccounts().getRate();
        double km2 = p.getKm2();
        List<DepositWords> deposits = new ArrayList<>();
        for (Resource r : Resource.values()) {
            if (!r.inFields() || p.getSites(r) <= 0) continue;
            String amount = LandMap.amountWords(r, p.getAmount(r));
            deposits.add(new DepositWords(MapView.css(0xff000000 | r.colour()), formatter.format(p.getSites(r)) + " · " + amount,
                    r.label() + ": " + p.getSites(r) + (p.getSites(r) == 1 ? " site, " : " sites, ") + amount));
        }
        int shown = Math.min(deposits.size(), ROW_DEPOSITS);
        if (shown == 2 && deposits.get(0).words().length() + deposits.get(1).words().length() > ROW_DEPOSIT_CHARS) shown = 1;
        String more = deposits.size() > shown ? "+" + (deposits.size() - shown) : "";
        TagWords tag = office != null && office.getId() == p.getId() ? new TagWords("BEST VALUE", Palette.GOOD)
                : isNew(p) ? new TagWords("NEW", Palette.BUILDING)
                : richest != null && richest.getId() == p.getId() ? new TagWords("MOST ORE", Palette.ORE)
                : p.isMostlySea() ? new TagWords("MOSTLY SEA", Palette.TEXT_MUTED) : null;
        boolean funding = g.landNeedsFunding(List.of(p.getId()));
        String listedUsd = usd(p.getPriceUsd());
        String todayHere = marked(here(), money(p.localPrice(rate)));
        StringBuilder tip = new StringBuilder(String.format("%s: %s, %s dry, listed in month %,d at %s: %s at today's rate.",
                p.where(), LandMap.area(km2), LandManager.partFigure(LandManager.sqFt(p.getDryKm2()), LandManager.sqFt(km2)),
                p.getListedMonth(), listedUsd, todayHere));
        if (p.getKm2(CityLand.FRESH) + p.getKm2(CityLand.SEA) > 0) {
            tip.append(String.format("%nWater: %s fresh, %s sea.", LandMap.area(p.getKm2(CityLand.FRESH)), LandMap.area(p.getKm2(CityLand.SEA))));
        }
        for (DepositWords d : deposits) tip.append("\n").append(d.tip()).append(WHOLE_FIELDS);
        return new Row(p, String.valueOf(p.getPlace() + 1), LandMap.area(km2),
                km2 > 0 ? p.getKm2(CityLand.DRY) / km2 : 0, km2 > 0 ? p.getKm2(CityLand.FRESH) / km2 : 0,
                km2 > 0 ? p.getKm2(CityLand.SEA) / km2 : 0,
                deposits.subList(0, shown), more,
                vault ? listedUsd : todayHere, g.canAffordParcel(p) ? Palette.TEXT_HEAD : Palette.BAD, perDryKm2(p), tag,
                funding ? "Fund" : "Buy",
                !funding ? "Buy " + p.where()
                        : vault ? "Buy — the vault is short\nOpens the funding page: dollars borrowed abroad, or the "
                                + "vault's dollars with the rest converted from cash."
                        : "Buy — borrow for it\nOpens the funding page: a loan sized to the gap.",
                funding, tip.toString());
    }

    /**
     * What a row's tooltip says after each resource's sites and tonnes
     * (0.7.64, batch L): an offer holds every field centred in its band
     * whole, all its sites and tonnes, wherever its sites lie (CityLand) -
     * Jerus: "whole iron fields as one offer". Until 0.7.64 the line ended
     * "in the ground, paid for in its price", a share of each field.
     */
    static final String WHOLE_FIELDS = " in the ground: every field of it centred in this offer, whole, paid for in its price.";

    /** A tag's words and colour. */
    record TagWords(String text, String colour) { }

    /* ----------------------------- what the ground is worth ----------------------------- */

    /** One of a card's bars: its name, its figure, its colour and its share of the card's scale. */
    record BarWords(String name, String figure, String colour, double share) { }

    /**
     * The Margin card: the world's price today and what investors pay, on one
     * scale; the margin; what investors paid for ground last month
     * (NationalAccounts.getLandSales() - LandManager's own month's flow is
     * cleared at the month's strike and reads 0 on a screen); and its (i): the
     * three statement lines with their notes, as they stood, and one line on
     * why investors pay what they pay, from the model's own reads.
     */
    record Margin(BarWords world, BarWords investors, String chip, String chipTone, String lastMonth, String info,
                  String parts) { }

    /**
     * THE WORLD'S PRICE, IN ITS PARTS (0.7.55): the founding's dollars, what
     * US prices have done since, and the premium for the city's crowding -
     * "US$7.53 × 1.14 US prices × 104 crowding: 5,684 people a km² of the
     * city's land", the founding's dollars a square metre since 0.7.68 - each the model's, as the ground was last priced
     * (LandManager.getUsPriceLevel(), getCrowdingPremium(), getCrowding()).
     * A save from before 0.7.55 knows its premium and not its crowding until
     * its first month here, and says only the premium.
     */
    String partsWords() {
        LandManager land = ui.game.getLandManager();
        double crowding = land.getCrowding();
        return dollars(LandMarket.openingUsdPerSqFt()) + " × " + String.format("%.2f", land.getUsPriceLevel())
                + " US prices × " + times(land.getCrowdingPremium()).substring(1) + " crowding"
                + (crowding > 0 ? String.format(": %,.0f people a km\u00b2 of the city's land", crowding) : "");
    }

    /** A multiple as the land office writes it: "×1.65", "×36.0", "×104". */
    static String times(double multiple) {
        return "×" + (multiple < 10 ? String.format("%.2f", multiple)
                : multiple < 100 ? String.format("%.1f", multiple) : String.format("%.0f", multiple));
    }

    Margin margin() {
        Game g = ui.game;
        LandManager land = g.getLandManager();
        double world = land.getAcquisitionCostPerSqFt();
        double inside = land.getPricePerSqFt();
        double margin = land.getMarginPerSqFt();
        double scale = Math.max(world, inside);
        double sold = g.getEconomyManager().getNationalAccounts().getLandSales();
        double multiple = land.getMarket().scarcityMultiplier(land.getOwnedSqFt(), land.getAllocatedSqFt());
        String here = here();
        String info = "Outside, you buy at " + dollars(land.getGroundUsdPerSqFt()) + "/m² (" + local(world)
                + " today): " + partsWords() + ". The more people on each km² the city owns, the dearer the"
                + " next plot - buying ground spreads them out and makes it cheaper; a bigger city as crowded pays"
                + " the same. It follows US prices, not the city's own. Asked in US"
                + " dollars, so a weaker currency makes it dearer here and a stronger one cheaper.\n\n"
                + "Inside, they buy at " + local(inside) + "/m². Supply against demand — the more land"
                + " standing free, the cheaper. Local money, and the exchange rate does not reach it.\n\n"
                + "Margin " + signedLocal(margin) + "/m². " + (margin < 0
                        ? "You are selling ground for less than it would cost you today - more of it"
                                + " stands free than anyone wants to build on, or the currency has made"
                                + " the world's price dear."
                        : "Land is tight enough that the city profits on every sale.") + "\n\n"
                + String.format("Investors pay ×%.2f the ground's price at the founding rate, because %.1f%% of"
                                + " the city's ground is built on; the world's price reaches you at %s%s to the US$.",
                        multiple, land.getUtilisation() * 100, here, fxRate(g.getForeignAccounts().getRate()));
        return new Margin(
                new BarWords("the world, today", local(world), Palette.MONEY, scale > 0 ? world / scale : 0),
                new BarWords("investors pay", local(inside), Palette.BUILDING, scale > 0 ? inside / scale : 0),
                signedLocal(margin) + " a m²", margin < 0 ? Palette.BAD : Palette.GOOD,
                sold > 0 ? "investors paid " + marked(here, money(sold)) + " for ground last month"
                        : "investors paid nothing for ground last month",
                info, partsWords());
    }

    /** One stacked bar on the second card: the building, its ground and its build, and the ground's share. */
    record OnTop(String name, double ground, double build, String words, String share) { }

    /**
     * On top of the build: a House's plot and a food plant's (the Bakery's) at
     * what investors pay today, each against the building's own cash cost -
     * "House: ground D$372k · build D$266k · 58%".
     */
    List<OnTop> onTop() {
        List<OnTop> out = new ArrayList<>();
        LandManager land = ui.game.getLandManager();
        String here = here();
        String[][] which = { { "House", "House" }, { "Bakery", "Food plant" } };
        for (String[] w : which) {
            BuildingsTemplate t = ui.game.getBuildingManager().getTemplateByName(w[0]);
            if (t == null) continue;
            double ground = land.priceFor(t.getLandSqFt());
            double build = t.getCashCost();
            double all = ground + build;
            out.add(new OnTop(w[1], ground, build,
                    "ground " + marked(here, money(ground)) + " · build " + marked(here, money(build)),
                    all > 0 ? String.format("%.0f%%", ground / all * 100) : "—"));
        }
        return out;
    }

    /** The second card's (i), the 0.7.6 note word for word, and what the bars are. */
    static final String ON_TOP_INFO = "The ground is charged on top of the build, so a cheap building on"
            + " expensive land is not a cheap building.\n\nEach bar is the building's plot at what investors pay"
            + " a square metre today, then the building's own cash cost; the figure at its right is the ground's"
            + " share of the two.";

    /** The Ore card: the deposits, the tonnes, the mines on them, whether ore lies undug, and the oil (0.7.61). */
    record Ore(String deposits, String tonnes, String mines, boolean undug, String popped, String oil) { }

    Ore ore() {
        LandManager land = ui.game.getLandManager();
        int deposits = land.getIronDeposits();
        int mines = ui.game.minesCommitted();
        int gained = depositsBeforeBuying >= 0 ? deposits - depositsBeforeBuying : 0;
        long oilSites = ui.game.getCityLand().totalSites(Resource.OIL);
        return new Ore(formatter.format(deposits) + (deposits == 1 ? " deposit" : " deposits"),
                shortNumber(land.getIronReserveTonnes()) + " t in the ground",
                formatter.format(mines) + (mines == 1 ? " mine" : " mines") + " on them, standing or on site",
                mines < deposits,
                gained > 0 ? "+" + gained + (gained == 1 ? " deposit" : " deposits") : null,
                oilSites > 0 ? "oil: " + formatter.format(oilSites) + (oilSites == 1 ? " site, " : " sites, ")
                        + LandMap.tonnes(land.getRemaining(Resource.OIL)) + " in the ground" : "oil: none owned");
    }

    /** The Ore card's (i). */
    static final String ORE_INFO = "A mine stands on one deposit, and every mine draws on the city's tonnes"
            + " together. Deposits come only with land, a whole field at a time: an offer holds every field whose"
            + " centre lies in it - all its sites and all its tonnes, its row says how many - and the ore is paid"
            + " for inside its price. Oil lies in the world's fields as ore does, sold the same way, and an Oil Well"
            + " stands on an oil site. Click the card for Build › Industry, where the Iron Mine is.";

    /** The sectors waiting on ground, by name: Game.getLandBlockedSectors(), as the sector pages and the inbox read it. */
    List<String> waitingNames() {
        List<String> out = new ArrayList<>();
        for (String key : ui.game.getLandBlockedSectors()) {
            Sector s = ui.game.getSectors().byKey(key);
            out.add(s == null ? key : s.label());
        }
        return out;
    }

    /** The Waiting card's (i), the 0.7.6 note word for word. */
    static final String WAITING_INFO = "As of last month — the sectors decide once a month, so ground bought"
            + " now shows up here next month.";

    /* ----------------------------- details ----------------------------- */

    /** The line under the chart: what the city has paid the world for ground, and how much of it the vault paid. */
    String spentLine() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        return "spent on ground since the founding: " + usd(fx.getLandUsdLifetime()) + " ("
                + usd(fx.getLandUsdFromVaultLifetime()) + " from the vault)";
    }

    /** The chart's (i). */
    static final String DETAILS_INFO = "What the world asks for a square metre of ground, as each month recorded"
            + " it - in US dollars since 0.7.6; the months an older save recorded before then are in local money,"
            + " and are drawn as they were recorded. The share of the ground built on is not drawn: it sits near"
            + " 100% for centuries.";

    /* =====================================================================
       THE DRAWING
       ===================================================================== */

    /**
     * The head: "Land office" with the building area's swatch and the page's
     * (i), and at the right the pay toggle with its line, and "‹ Build". On
     * the office's own pages - "Land office › Funding", "Land office › Not
     * bought" - the title is a way back, and "‹ Land office" stands alone at
     * the right.
     */
    HBox head(String sub) {
        Label title = ui.pageTitle("Land office");
        HBox titled = new HBox(10, title);
        titled.setAlignment(Pos.CENTER_LEFT);
        if (sub != null) {
            title.setStyle(title.getStyle() + " -fx-cursor: hand;");
            title.setOnMouseClicked(e -> showLandMenu());
            Label sep = new Label("›");
            sep.setStyle(Palette.words(Palette.SIZE_TITLE, Palette.TEXT_MUTED) + " -fx-padding: 8 0 2 0;");
            Label where = new Label(sub);
            where.setStyle(Palette.strong(Palette.SIZE_TITLE, Palette.TEXT_HEAD) + " -fx-padding: 8 0 2 0;");
            titled.getChildren().addAll(sep, where);
        }
        javafx.scene.Node info = infoButton(WHO_PAYS, false);
        HBox.setMargin(info, new javafx.geometry.Insets(6, 0, 0, 0));
        titled.getChildren().add(info);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox head = new HBox(Palette.GAP_LOOSE, titled, gap);
        head.setAlignment(Pos.CENTER_LEFT);
        head.setMaxWidth(Double.MAX_VALUE);
        if (sub == null) head.getChildren().add(payToggle());
        head.getChildren().add(sub == null
                ? stepChip("‹ Build", () -> ui.buildScreen.showBuildMenu(), true)
                : stepChip("‹ Land office", this::showLandMenu, true));
        head.setStyle("-fx-padding: 0 0 4 0;");
        return head;
    }

    /** The pay toggle, applied at once and saved with the city, and the line under it with its (i). */
    VBox payToggle() {
        String[] names = payNames();
        boolean vault = ui.game.isLandPaidFromVault();
        javafx.scene.layout.FlowPane chips = chipStrip(names, vault ? names[1] : names[0], Palette.SIZE_LABEL,
                name -> {
                    ui.game.setLandPaidFromVault(names[1].equals(name));
                    showLandMenu();
                });
        chips.setAlignment(Pos.CENTER_RIGHT);
        chips.setPrefWrapLength(320);
        for (int i = 0; i < chips.getChildren().size() && i < PAY_TIPS.length; i++) {
            if (chips.getChildren().get(i) instanceof Button b) {
                Tooltip tip = new Tooltip(PAY_TIPS[i]);
                tip.setShowDelay(Duration.millis(250));
                b.setTooltip(tip);
            }
        }
        String[] caption = payCaption();
        HBox line = infoLine(caption[0], caption[1], false, Palette.SIZE_CAPTION, Palette.TEXT_MUTED, 340);
        VBox box = new VBox(3, chips, line);
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setMinWidth(Region.USE_PREF_SIZE);
        return box;
    }

    /* ----------------------------- THE GROUND ----------------------------- */

    /**
     * THE GROUND: the strip and the big figures, then the bar and its scale -
     * one panel, the shape of Construction's builders' gauge. The bar is
     * redrawn with the stepper, so − and + move the ghosts and the numbers.
     */
    VBox groundPanel(Next next) {
        Cell[] cells = strip();
        VBox[] drawn = new VBox[cells.length];
        for (int i = 0; i < cells.length; i++) {
            Cell c = cells[i];
            drawn[i] = i == 1
                    ? limitCell(c.label(), c.value(), c.note(), c.tone(), "Who is waiting, and on what: the card below",
                            () -> { if (waitingCard != null) scrollTo(waitingCard); })
                    : limitCell(c.label(), c.value(), c.note(), c.tone());
        }
        HBox cellsBar = vitalsBar(drawn);
        boolean turned = shownMonth >= 0 && shownMonth != ui.game.getMonth();
        if (turned && ui.game.getLandManager().getGroundUsdPerSqFt() != shownWorldUsd
                && drawn[2].getChildren().size() > 1 && drawn[2].getChildren().get(1) instanceof Region figure) {
            UserInterface.popPip(figure);
        }

        String[] f = groundFigures(next);
        Label big = new Label(f[0]);
        big.setStyle(Palette.figure(Palette.SIZE_LEAD, Palette.TEXT_HEAD));
        big.setWrapText(true);
        Label room = new Label(f[1]);
        room.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        room.setWrapText(true);
        VBox figures = new VBox(1, big, room);
        if (!f[2].isEmpty()) {
            Label after = new Label(f[2]);
            after.setStyle(Palette.figure(Palette.SIZE_SECTION, Palette.BUILDING));
            after.setWrapText(true);
            Label afterRoom = new Label(f[3]);
            afterRoom.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            afterRoom.setWrapText(true);
            VBox.setMargin(after, new javafx.geometry.Insets(4, 0, 0, 0));
            figures.getChildren().addAll(after, afterRoom);
        }
        figures.setMinWidth(0);
        figures.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(figures, Priority.ALWAYS);
        HBox top = new HBox(Palette.GAP_SECTION, cellsBar, figures);
        top.setAlignment(Pos.CENTER_LEFT);

        double scale = groundScale(next);
        List<LandParcel> best = nextShelf(next);
        SegmentBar bar = segmentBar(groundSegments(next, i -> () -> {
            if (i < best.size()) picked(best.get(i).getSide(), best.get(i).getPlace());
        }), scale, groundTicks(), 0, 18);
        if (Double.isFinite(freeBeforeBuying)) bar.animateFirst(freeBeforeBuying, 300);

        Label zero = new Label("0");
        zero.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        HBox key = new HBox(Palette.GAP_LOOSE, keySwatch(Palette.BUILDING, "free now"));
        key.setAlignment(Pos.CENTER);
        if (!next.ids().isEmpty()) {
            key.getChildren().add(ghostSwatch(Palette.BUILDING_LIGHT,
                    next.n() == 1 ? "the best offer" : "the best " + next.n() + ", cheapest dry ground first"));
            boolean ore = false;
            for (LandParcel p : nextShelf(next)) ore |= p.hasIron();
            if (ore) key.getChildren().add(keySwatch(Palette.ORE, "ore under it"));
        }
        Label end = new Label(LandManager.areaWords(scale));
        end.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        Region a = new Region(), b = new Region();
        HBox.setHgrow(a, Priority.ALWAYS);
        HBox.setHgrow(b, Priority.ALWAYS);
        HBox scaleRow = new HBox(0, zero, a, key, b, end);
        scaleRow.setAlignment(Pos.CENTER_LEFT);

        VBox panel = new VBox(8, top, bar, scaleRow);
        panel.setStyle("-fx-padding: 10 12 10 12;" + Palette.block(Palette.PANEL, Palette.EDGE));
        panel.setMaxWidth(Double.MAX_VALUE);
        return panel;
    }

    /** A key's swatch for a ghost: tinted and outlined as the ghost is drawn. */
    static HBox ghostSwatch(String colour, String name) {
        Region dot = new Region();
        dot.setMinSize(10, 10);
        dot.setPrefSize(10, 10);
        dot.setMaxSize(10, 10);
        dot.setStyle("-fx-background-color: " + colour + "40; -fx-border-color: " + colour + ";"
                + " -fx-border-width: 1; -fx-background-radius: 2; -fx-border-radius: 2;");
        Label text = new Label(name);
        text.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        HBox row = new HBox(5, dot, text);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** The page brought to a card: the menu's scroller set so it sits near the top. */
    private void scrollTo(javafx.scene.Node target) {
        javafx.scene.control.ScrollPane scroller = ui.menuScroller;
        if (scroller == null || target == null || target.getScene() == null) return;
        javafx.scene.Node content = scroller.getContent();
        if (content == null) return;
        javafx.geometry.Bounds at = content.sceneToLocal(target.localToScene(target.getBoundsInLocal()));
        double span = content.getBoundsInLocal().getHeight() - scroller.getViewportBounds().getHeight();
        if (at == null || span <= 0) return;
        scroller.setVvalue(Math.max(0, Math.min(1, (at.getMinY() - 12) / span)));
    }

    /* ----------------------------- BUY THE BEST N (0.7.13; "the next N" until 0.7.61) -----------------------------
     *
     * Jerus: "add a button to buy multiple, so for example buy the next 5
     * land options, and then also debt popup appears if not enough". The
     * first N offers in the office's order (Game.nextLandParcels()), their
     * price together in the money the toggle pays in with the other money
     * beside it, and a button that buys them each as its own row's button
     * would (Game.buyLandParcels()) - or, short, opens the same funding page,
     * sized to the whole gap. N starts at five and runs from one to what is
     * listed. Since 0.7.61 at the right of THE BEST OFFERS, over the bar
     * whose ghosts are the N it buys (each a click to pick on the map).
     * ---------------------------------------------------------------------------------------------------------------- */
    HBox nextPlots(Next next) {
        Button fewer = stepper("−", 26, () -> { nextCount--; showLandMenu(); });
        fewer.setDisable(next.n() <= 1);
        Label count = new Label(String.valueOf(next.n()));
        count.setStyle(Palette.figure(Palette.SIZE_BODY, Palette.TEXT_HEAD));
        count.setMinWidth(Region.USE_PREF_SIZE);
        Button more = stepper("+", 26, () -> { nextCount++; showLandMenu(); });
        more.setDisable(next.n() >= next.listed());

        Label total = new Label(next.total());
        total.setStyle(Palette.figure(Palette.SIZE_SECTION, next.totalTone()));
        total.setMinWidth(Region.USE_PREF_SIZE);
        Label other = new Label(next.other());
        other.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        other.setMinWidth(Region.USE_PREF_SIZE);

        // Pieces' action button, inline (0.7.34); with nothing listed it is not drawn.
        List<Integer> ids = next.ids();
        Pieces.ActionButton buy = actionButton(Icons.LAND, Palette.BUILDING, ACTION_INLINE, next.button(),
                () -> buyOrFund(ids));
        buy.setMinWidth(Region.USE_PREF_SIZE);
        showIf(buy, !ids.isEmpty());
        Tooltip tip = new Tooltip(next.buttonTip());
        tip.setShowDelay(Duration.millis(250));
        Tooltip.install(buy, tip);

        HBox row = new HBox(6, fewer, count, more, total, other, buy);
        HBox.setMargin(total, new javafx.geometry.Insets(0, 0, 0, 8));
        HBox.setMargin(buy, new javafx.geometry.Insets(0, 0, 0, 6));
        row.setAlignment(Pos.CENTER_RIGHT);
        return row;
    }

    /** This month's receipt, under ON OFFER: a pink tick and the receipt, wrapped rather than cut. */
    HBox receiptLine(String receipt) {
        Label words = new Label(receipt);
        words.setWrapText(true);
        words.setMinWidth(0);
        words.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_BODY));
        HBox.setHgrow(words, Priority.ALWAYS);
        HBox line = new HBox(6, icon(Icons.TICK, Palette.BUILDING, 13), words);
        line.setAlignment(Pos.CENTER_LEFT);
        line.setStyle("-fx-padding: 4 10 4 8; -fx-background-color: " + Palette.BUILDING + "14;"
                + " -fx-background-radius: 6;");
        return line;
    }

    /** Buys these plots the way the toggle pays, or - the city short - opens the funding page for them. */
    void buyOrFund(List<Integer> ids) {
        if (ui.game.landNeedsFunding(ids)) {
            showLandFunding(ids);
            return;
        }
        rememberBeforeBuying();
        ui.game.buyLandParcels(ids);
        showLandMenu();
    }

    /** What a purchase is measured against when the page is drawn after it. */
    void rememberBeforeBuying() {
        freeBeforeBuying = ui.game.getLandManager().getAvailableSqFt();
        depositsBeforeBuying = ui.game.getLandManager().getIronDeposits();
    }

    /* ----------------------------- THE HERO ROW (0.7.61) -----------------------------
     *
     * The map at the left, 600 x 400 (MapView: a click on it picks a side, or
     * an offer's band, and Expand lays it over the window), and at the right,
     * in what the content area leaves at 1,389 x 868 (CITY_PANEL, 657 px),
     * THE CITY's two lines, the sides as chips and the chosen side's ten
     * offers as ROW_H rows: hovering one lights its band on the map, a click
     * picks it, Buy buys it - or, short, Fund opens the funding page sized to
     * it, as a card's Buy did.
     *
     * WHAT A ROW STOPPED SAYING: the 0.7.26 card's value bar against the
     * going rate and its words ("44% under the going rate"). Ten rows of
     * 28 px have no room for them; the price a dry km2 in its own column is
     * the same comparison read down a side, and BEST VALUE marks the best of
     * all forty.
     * ---------------------------------------------------------------------------------- */

    /** The map's width and the hero row's height (spec-land 2.8). */
    static final double MAP_W = MapView.SMALL_W, HERO_H = MapView.SMALL_H;

    /** The gap between the map and THE CITY... */
    static final double HERO_GAP = 16;

    /** ...and THE CITY's width: what the content area's 1,273 px leave at 1,389 x 868 (spec-land 2.8's 657). */
    static final double CITY_PANEL = 657;

    /** A row's height: 28 px (spec-land 2.8), ten of them and THE CITY's head inside HERO_H. */
    static final double ROW_H = 28;

    /** The rows' columns, in pixels: lane, size, ground and water, deposits, price, a dry km2, tag, button - with the gaps, CITY_PANEL. */
    static final double[] ROW_COLUMNS = { 24, 74, 64, 158, 82, 78, 76, 58 };

    /** The gap between a row's columns. */
    static final double ROW_GAP = 4;

    /** The rows' column names. */
    static final String[] ROW_HEADS = { "lane", "size", "dry · fresh · sea", "deposits", "price", "a dry km²", "", "" };

    HBox hero() {
        if (map == null) map = new MapView(ui, this::picked, this::showLandMenu);
        map.refresh(side, lane);
        VBox city = cityPanel();
        city.setPrefWidth(CITY_PANEL);
        city.setMinWidth(0);
        HBox.setHgrow(city, Priority.ALWAYS);
        HBox row = new HBox(HERO_GAP, map.smallNode(), city);
        row.setAlignment(Pos.TOP_LEFT);
        row.setMinHeight(HERO_H);
        return row;
    }

    /** The map picked a side, and an offer's lane on it (-1 for none): its rows, the offer's lit. */
    void picked(int side, int lane) {
        this.side = side;
        this.lane = lane;
        showLandMenu();
    }

    /** THE CITY, its sides, and the chosen side's offers. */
    VBox cityPanel() {
        Label title = new Label("THE CITY");
        title.setStyle(Palette.strong(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        Label size = new Label(cityLine());
        size.setStyle(Palette.figure(Palette.SIZE_SECTION, Palette.TEXT_HEAD));
        Label world = new Label(worldLine());
        world.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        VBox head = new VBox(1, title, size, world);
        // The month moved the rate: a row's price in local money pops, as a card's did (0.7.26).
        boolean turned = shownMonth >= 0 && shownMonth != ui.game.getMonth() && ui.game.getForeignAccounts().getRate() != shownRate
                && !ui.game.isLandPaidFromVault();
        VBox panel = new VBox(6, head, sideChipsNode(), rowsNode(sideRows(side), turned));
        panel.setMaxHeight(HERO_H);
        return panel;
    }

    /** The four sides as Pieces' chips, each its cheapest dry ground a km2 after its name and BEST VALUE on the best's side. */
    javafx.scene.layout.FlowPane sideChipsNode() {
        List<SideChip> chips = sideChips();
        String[] names = new String[chips.size()];
        for (int i = 0; i < names.length; i++) names[i] = chips.get(i).name();
        javafx.scene.layout.FlowPane strip = chipStrip(names, CityLand.sideName(side), Palette.SIZE_LABEL, name -> {
            for (SideChip c : chips) if (c.name().equals(name)) side = c.side();
            lane = -1;
            showLandMenu();
        });
        strip.setAlignment(Pos.CENTER_LEFT);
        strip.setPrefWrapLength(CITY_PANEL);
        for (int i = 0; i < chips.size() && i < strip.getChildren().size(); i++) {
            if (!(strip.getChildren().get(i) instanceof Button b)) continue;
            SideChip c = chips.get(i);
            Label best = new Label(c.best());
            best.setStyle(Palette.figureRegular(Palette.SIZE_CAPTION, c.side() == side ? Palette.TEXT_HEAD : Palette.TEXT_MUTED));
            HBox graphic = new HBox(5, best);
            graphic.setAlignment(Pos.CENTER_LEFT);
            if (c.bestValue()) graphic.getChildren().add(tag("BEST VALUE", Palette.GOOD));
            b.setGraphic(graphic);
            b.setContentDisplay(javafx.scene.control.ContentDisplay.RIGHT);
            b.setGraphicTextGap(7);
            Tooltip tip = new Tooltip(c.name() + "'s ten offers. Its cheapest dry ground: " + c.best()
                    + (c.bestValue() ? ", and the office's BEST VALUE." : "."));
            tip.setShowDelay(Duration.millis(250));
            b.setTooltip(tip);
        }
        return strip;
    }

    /** The rows: the column names, then the side's offers, lane by lane; `turned`, their local prices pop. */
    VBox rowsNode(List<Row> rows, boolean turned) {
        HBox heads = new HBox(ROW_GAP);
        for (int c = 0; c < ROW_HEADS.length; c++) {
            heads.getChildren().add(column(gridCell(ROW_HEADS[c], Palette.TEXT_LABEL, Palette.SIZE_CAPTION, c != 2 && c != 3), c));
        }
        heads.setStyle("-fx-padding: 0 6 1 6;");
        VBox box = new VBox(0, heads);
        for (Row r : rows) box.getChildren().add(rowNode(r, turned));
        return box;
    }

    /** A cell held to its column's width. */
    private static Region column(Region cell, int c) {
        cell.setMinWidth(ROW_COLUMNS[c]);
        cell.setPrefWidth(ROW_COLUMNS[c]);
        cell.setMaxWidth(ROW_COLUMNS[c]);
        return cell;
    }

    /** One offer's row: hover lights its band, a click picks it, its button buys it or opens the funding page. */
    HBox rowNode(Row r, boolean turned) {
        LandParcel p = r.parcel();
        boolean picked = p.getPlace() == lane && p.getSide() == side;
        HBox bar = new HBox(0);
        double[] shares = { r.dry(), r.fresh(), r.sea() };
        int[] ground = { TileRaster.GROUND[World.GRASS], TileRaster.GROUND[World.FRESH], TileRaster.GROUND[World.SALT] };
        double barW = ROW_COLUMNS[2] - 6;
        for (int k = 0; k < 3; k++) {
            if (!(shares[k] > 0)) continue;
            Region part = new Region();
            double w = Math.max(1, shares[k] * barW);
            part.setMinSize(w, 8);
            part.setPrefSize(w, 8);
            part.setMaxSize(w, 8);
            part.setStyle("-fx-background-color: " + MapView.css(ground[k]) + ";");
            bar.getChildren().add(part);
        }
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setMaxHeight(8);
        HistoryScreen.tip(bar, String.format("dry %.0f%% · fresh water %.0f%% · sea %.0f%%", r.dry() * 100, r.fresh() * 100, r.sea() * 100));
        HBox deposits = new HBox(6);
        deposits.setAlignment(Pos.CENTER_LEFT);
        for (DepositWords d : r.deposits()) {
            Region dot = new Region();
            dot.setMinSize(8, 8);
            dot.setMaxSize(8, 8);
            dot.setStyle("-fx-background-color: " + d.colour() + "; -fx-background-radius: 4; -fx-border-color: #0b1118;"
                    + " -fx-border-radius: 4; -fx-border-width: 1;");
            Label words = new Label(d.words());
            words.setStyle(Palette.figureRegular(Palette.SIZE_CAPTION, Palette.TEXT_BODY));
            HBox one = new HBox(3, dot, words);
            one.setAlignment(Pos.CENTER_LEFT);
            HistoryScreen.tip(one, d.tip());
            deposits.getChildren().add(one);
        }
        if (!r.more().isEmpty()) deposits.getChildren().add(gridCell(r.more(), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, false));
        Region tagCell = r.tag() == null ? new Region() : tag(r.tag().text(), r.tag().colour());
        HBox tagBox = new HBox(tagCell);
        tagBox.setAlignment(Pos.CENTER_LEFT);
        Button buy = new Button(r.button());
        String colour = Palette.BUILDING;
        buy.setStyle(Palette.strong(Palette.SIZE_LABEL, r.funding() ? colour : Palette.ON_FILL)
                + " -fx-background-color: " + (r.funding() ? "transparent" : colour) + "; -fx-border-color: " + colour + ";"
                + " -fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 2 0 2 0; -fx-cursor: hand;");
        buy.setMaxWidth(Double.MAX_VALUE);
        List<Integer> just = List.of(p.getId());
        buy.setOnAction(e -> buyOrFund(just));
        // The press is the button's: the row under it does not pick its offer over the page the button opened.
        buy.setOnMouseClicked(javafx.event.Event::consume);
        Tooltip buyTip = new Tooltip(r.buttonTip());
        buyTip.setShowDelay(Duration.millis(250));
        buy.setTooltip(buyTip);
        Label price = gridCell(r.price(), r.priceTone(), Palette.SIZE_BODY, true);
        if (turned) UserInterface.popPip(price);
        HBox row = new HBox(ROW_GAP,
                column(gridCell(r.lane(), Palette.TEXT_MUTED, Palette.SIZE_LABEL, true), 0),
                column(gridCell(r.km2(), Palette.TEXT_HEAD, Palette.SIZE_BODY, true), 1),
                column(new HBox(bar), 2),
                column(deposits, 3),
                column(price, 4),
                column(gridCell(r.perKm2(), Palette.TEXT_MUTED, Palette.SIZE_LABEL, true), 5),
                column(tagBox, 6),
                column(buy, 7));
        ((HBox) row.getChildren().get(2)).setAlignment(Pos.CENTER_LEFT);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMinHeight(ROW_H);
        row.setPrefHeight(ROW_H);
        row.setMaxHeight(ROW_H);
        String rest = "-fx-padding: 0 6 0 6; -fx-background-radius: 4;"
                + (picked ? " -fx-background-color: " + Palette.RAISED + "; -fx-border-color: " + Palette.BUILDING
                        + "; -fx-border-width: 0 0 0 2;" : "");
        row.setStyle(rest + " -fx-cursor: hand;");
        row.setOnMouseEntered(e -> {
            row.setStyle(rest + " -fx-cursor: hand; -fx-background-color: " + Palette.CONTROL + ";");
            map.light(p.getSide(), p.getPlace());
        });
        row.setOnMouseExited(e -> {
            row.setStyle(rest + " -fx-cursor: hand;");
            map.light(-1, -1);
        });
        row.setOnMouseClicked(e -> {
            lane = p.getPlace();
            showLandMenu();
        });
        Tooltip tip = new Tooltip(r.tip());
        tip.setShowDelay(Duration.millis(400));
        Tooltip.install(row, tip);
        return row;
    }

    /* ----------------------------- WHAT THE GROUND IS WORTH ----------------------------- */

    /** The four cards, a quarter of the page each. */
    javafx.scene.layout.GridPane worthCards() {
        javafx.scene.layout.GridPane grid = equalColumns(4, TILE_GAP);
        grid.add(marginCard(), 0, 0);
        grid.add(onTopCard(), 1, 0);
        grid.add(oreCard(), 2, 0);
        Region waiting = waitingCard();
        waitingCard = waiting;
        grid.add(waiting, 3, 0);
        return grid;
    }

    /** A worth card's frame: its icon square, its name and its (i), then what it says. */
    VBox worthCard(String svg, String colour, String name, String info, javafx.scene.Node... body) {
        Label title = new Label(name);
        title.setWrapText(true);
        title.setMinWidth(0);
        title.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox top = new HBox(8, iconSquare(svg, colour, 28, 15), title, gap, infoButton(info, false));
        top.setAlignment(Pos.CENTER_LEFT);
        VBox card = new VBox(8, top);
        card.getChildren().addAll(body);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setMaxHeight(Double.MAX_VALUE);
        card.setMinHeight(140);
        card.setStyle(WORTH_CARD + Palette.EDGE + ";");
        return card;
    }

    /** A worth card's style, its edge's colour last. */
    private static final String WORTH_CARD = "-fx-padding: 10 12 10 12; -fx-background-color: " + Palette.RAISED + ";"
            + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: ";

    /** One labelled bar on a worth card: its name and figure on a line, the bar under them. */
    VBox namedBar(BarWords w) {
        Label name = new Label(w.name());
        name.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        name.setWrapText(true);
        name.setMinWidth(0);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        Label figure = new Label(w.figure());
        figure.setStyle(Palette.figure(Palette.SIZE_BODY, Palette.TEXT_HEAD));
        figure.setMinWidth(Region.USE_PREF_SIZE);
        HBox line = new HBox(6, name, gap, figure);
        line.setAlignment(Pos.CENTER_LEFT);
        return new VBox(2, line, segmentBar(List.of(Segment.of(w.share(), w.colour())), 1, null, 0, 6));
    }

    VBox marginCard() {
        Margin m = margin();
        Label chip = new Label(m.chip());
        chip.setStyle(Palette.figure(Palette.SIZE_BODY, m.chipTone()) + " -fx-padding: 1 8 1 8;"
                + " -fx-border-color: " + m.chipTone() + "; -fx-border-radius: 9; -fx-background-radius: 9;"
                + " -fx-background-color: " + m.chipTone() + "1f;");
        chip.setMinWidth(Region.USE_PREF_SIZE);
        Label margin = new Label("margin");
        margin.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        HBox chipLine = new HBox(6, margin, chip);
        chipLine.setAlignment(Pos.CENTER_LEFT);
        Label last = new Label(m.lastMonth());
        last.setWrapText(true);
        last.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        // The world's price in its parts, under its bar (0.7.55), in the
        // On-top card's caption style.
        Label parts = new Label(m.parts());
        parts.setWrapText(true);
        parts.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        return worthCard(Icons.COIN, Palette.MONEY, "Margin", m.info(),
                new VBox(2, namedBar(m.world()), parts), namedBar(m.investors()), chipLine, last);
    }

    VBox onTopCard() {
        List<javafx.scene.Node> body = new ArrayList<>();
        for (OnTop t : onTop()) {
            Label name = new Label(t.name());
            name.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
            Region gap = new Region();
            HBox.setHgrow(gap, Priority.ALWAYS);
            Label share = new Label(t.share());
            share.setStyle(Palette.figure(Palette.SIZE_BODY, Palette.TEXT_HEAD));
            share.setMinWidth(Region.USE_PREF_SIZE);
            HBox line = new HBox(6, name, gap, share);
            line.setAlignment(Pos.CENTER_LEFT);
            SegmentBar bar = segmentBar(List.of(
                    new Segment(t.ground(), Palette.BUILDING, false, null, null, "the ground", null),
                    new Segment(t.build(), Palette.CONTROL_EDGE, false, null, null, "the build", null)), 0, null, 0, 6);
            Label words = new Label(t.words());
            words.setWrapText(true);
            words.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            body.add(new VBox(2, line, bar, words));
        }
        HBox key = new HBox(Palette.GAP_LOOSE, keySwatch(Palette.BUILDING, "ground"), keySwatch(Palette.CONTROL_EDGE, "build"));
        body.add(key);
        return worthCard(Icons.HOMES, Palette.BUILDING, "On top of the build", ON_TOP_INFO,
                body.toArray(new javafx.scene.Node[0]));
    }

    VBox oreCard() {
        Ore o = ore();
        Label deposits = new Label(o.deposits());
        deposits.setStyle(Palette.figure(Palette.SIZE_SECTION, Palette.TEXT_HEAD));
        deposits.setMinWidth(Region.USE_PREF_SIZE);
        HBox top = new HBox(8, deposits);
        top.setAlignment(Pos.CENTER_LEFT);
        Label popped = null;
        if (o.popped() != null) {
            popped = tag(o.popped(), Palette.ORE);
            top.getChildren().add(popped);
        }
        Label tonnes = new Label(o.tonnes());
        tonnes.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_BODY));
        Label mines = new Label(o.mines());
        mines.setWrapText(true);
        mines.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_BODY));
        Label oil = new Label(o.oil());
        oil.setWrapText(true);
        oil.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        VBox card = worthCard(Icons.ORE, Palette.ORE, "Ore and oil", ORE_INFO, top, tonnes, mines, oil);
        if (o.undug()) {
            Label undug = new Label("ore that nothing is digging");
            undug.setWrapText(true);
            undug.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.WARN));
            card.getChildren().add(undug);
        }
        String rest = WORTH_CARD;
        card.setStyle(rest + Palette.EDGE + "; -fx-cursor: hand;");
        card.setOnMouseEntered(e -> card.setStyle(rest + Palette.ACCENT + "; -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle(rest + Palette.EDGE + "; -fx-cursor: hand;"));
        Tooltip tip = new Tooltip("Iron mines are built under Build › Industry. Click to go there.");
        tip.setShowDelay(Duration.millis(300));
        Tooltip.install(card, tip);
        card.setOnMouseClicked(e -> ui.buildScreen.openCategory(BuildAdvice.INDUSTRY));
        if (popped != null) UserInterface.popPip(popped);
        return card;
    }

    VBox waitingCard() {
        List<String> keys = new ArrayList<>(ui.game.getLandBlockedSectors());
        List<String> names = waitingNames();
        boolean none = keys.isEmpty();
        List<javafx.scene.Node> body = new ArrayList<>();
        if (none) {
            Label words = new Label("every sector has room to build");
            words.setWrapText(true);
            words.setMinWidth(0);
            words.setStyle(Palette.words(12, Palette.TEXT_HEAD));
            HBox.setHgrow(words, Priority.ALWAYS);
            HBox row = new HBox(10, ring(1, Palette.GOOD, 40, 4, null, 0), words);
            row.setAlignment(Pos.CENTER_LEFT);
            body.add(row);
        } else {
            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                Label row = new Label(names.get(i) + "  ›");
                row.setWrapText(true);
                row.setStyle(Palette.words(Palette.SIZE_BODY, Palette.BAD) + " -fx-cursor: hand;");
                String word = ui.game.getLastInvestment(key);
                Tooltip tip = new Tooltip((word == null || word.isEmpty() ? "No word recorded" : word)
                        + "\nClick for its investors.");
                tip.setShowDelay(Duration.millis(250));
                row.setTooltip(tip);
                Sector sector = ui.game.getSectors().byKey(key);
                if (sector != null) row.setOnMouseClicked(e -> ui.sectorScreen.openSectorBooks(sector, "Investors"));
                body.add(row);
            }
        }
        return worthCard(Icons.ALERT, none ? Palette.TEXT_MUTED : Palette.BAD, "Waiting on ground", WAITING_INFO,
                body.toArray(new javafx.scene.Node[0]));
    }

    /* ----------------------------- details ----------------------------- */

    /** "details ▸": the world's price of ground over the city's life, and what the city has paid for it. */
    VBox details() {
        Label toggle = new Label("details " + (detailsOpen ? Statement.OPENED : Statement.CLOSED));
        toggle.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.ACCENT) + " -fx-cursor: hand;");
        toggle.setOnMouseClicked(e -> { detailsOpen = !detailsOpen; showLandMenu(); });
        VBox box = new VBox(4, toggle);
        if (!detailsOpen) return box;
        HistorySave h = ui.game.getHistorySave();
        box.getChildren().add(sectionHead("THE WORLD'S PRICE OF GROUND", DETAILS_INFO, null));
        box.getChildren().add(trendChart(h.getMonth(),
                new String[] { "The world's price of ground" },
                new double[][] { h.aligned("landPrice") },
                new String[] { Palette.BUILDING },
                v -> dollars(v) + "/m²"));
        Label spent = new Label(spentLine());
        spent.setWrapText(true);
        spent.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_BODY));
        box.getChildren().add(spent);
        return box;
    }

    /* =====================================================================
       WHEN THE CITY IS SHORT (0.7.13), the land office's own page since 0.7.26

       Jerus: "if you are on buy by converting and you dont have enough, you
       can stilll click buy, just the popup to issue debt appears, but if you
       are in buy with reserves, and click buy, then pop up to issue foreign
       debt should appear (aka the short or the 20y, like with buildings)".
       Sized to the gap in the money the toggle pays in - every figure the
       model's (Game, WHEN THE CITY IS SHORT, AND SEVERAL AT ONCE):
         - converting, the build screen's two offers in local money:
           Game.BUILD_BOND_YEARS' bond and Game.BUILD_NOTE_MONTHS' note;
         - from the vault, the same two terms in dollars on the world's
           curve, issued abroad and held in reserve, and, when the cash
           covers it, the vault's dollars with the rest converted: a choice,
           never the default. With the window abroad shut, it says why and
           offers what remains.
       Cancel leaves everything as it was. Either offer books exactly the
       quote on its card and then buys the plots.

       IN THE OFFICE'S FRAME (0.7.26): "Land office › Funding", the rail's
       Land office lit (it lit nothing), a card with the price against what
       the city holds as a bar, and the offers as cards side by side
       (Pieces.offerCard()) - the bond first, then the note, the build page's
       order and reasoning - with every line each offer had. A redraw (a month
       landing) that finds nothing needs funding any more goes back to the
       office rather than quoting a loan of nothing.
       ===================================================================== */

    /** What the funding page's card says: the price, what is held, the gap in money, and the bar's two parts. */
    record Funding(String priceLine, String heldWords, String gapMoney, double held, double gap) { }

    Funding funding(List<Integer> ids) {
        Game g = ui.game;
        String here = here();
        String what = plots(ids.size());
        String s = ids.size() == 1 ? "s" : "";
        if (!g.isLandPaidFromVault()) {
            double gap = g.landCashGap(ids);
            double cash = g.getCash();
            return new Funding(
                    String.format("%s cost%s %s at today's rate", what, s, marked(here, money(g.landPriceLocal(ids)))),
                    "the treasury holds " + marked(here, money(cash)),
                    marked(here, money(gap)),
                    Math.max(0, Math.min(cash, g.landPriceLocal(ids))), gap);
        }
        double gapUsd = g.landVaultGapUsd(ids);
        double vault = g.getForeignAccounts().getReservesUsd();
        return new Funding(
                String.format("%s cost%s %s", what, s, usd(g.landPriceUsd(ids))),
                "the vault holds " + usd(vault),
                usd(gapUsd),
                Math.max(0, Math.min(vault, g.landPriceUsd(ids))), gapUsd);
    }

    /** The dollar offers' (i), the 0.7.13 note word for word. */
    static final String ABROAD_INFO = "Issued abroad; the dollars are in reserve, and the "
            + "vault pays for the land. On the world's curve at each term: a dollar owed is "
            + "owed in dollars, whatever the currency does.";

    /** One loan on the funding page, worded - what Pieces.offerCard() draws: its name, its (i), its quote, the rate's words and colour, its ending, its button (a Pieces.Press since 0.7.34: the purchase, and the paper under it) and what the button does, and how its figures are written. */
    record Offer(String name, String info, DebtQuote quote, String rate, String tone, String ending, Pieces.Press action,
                 Runnable issue, java.util.function.DoubleFunction<String> written) { }

    /** What the funding page's buttons buy (0.7.34): "Buy · D$1.0B" for a plot, "Buy 5 plots · D$9.8B" for several, in the money the toggle pays in. */
    String buyWords(List<Integer> ids) {
        Game g = ui.game;
        String price = g.isLandPaidFromVault() ? usd(g.landPriceUsd(ids)) : marked(here(), money(g.landPriceLocal(ids)));
        return (ids.size() == 1 ? "Buy" : "Buy " + ids.size() + " plots") + " · " + price;
    }

    /**
     * The loans the funding page offers for these plots, in the build page's
     * order - the bond, then the note: converting, in local money for the
     * cash gap; from the vault, in dollars on the world's curve for the
     * vault's gap, or none while the window abroad is shut. Every figure is
     * the quote's; a button books exactly it, then buys the plots. Nothing is
     * booked until one is pressed.
     */
    List<Offer> offers(List<Integer> ids) {
        Game game = ui.game;
        List<Offer> out = new ArrayList<>();
        String buy = buyWords(ids);
        if (!game.isLandPaidFromVault()) {
            double gap = game.landCashGap(ids);
            DebtQuote bond = game.quoteLongBondForCash(gap, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);
            DebtQuote note = game.quoteTBill(gap, Game.BUILD_NOTE_MONTHS, Game.BUILD_NOTE_GRANULE);
            out.add(new Offer(Game.BUILD_BOND_YEARS + "-year bond", null, bond,
                    pct2(bond.marketRate()) + " yield  ·  " + pct2(bond.couponRate()) + " coupon",
                    rateColour(bond, game.getDebtManager()),
                    String.format("Paid over %d years: the coupon every month, then the whole %s at the end.",
                            bond.duration(), money(bond.faceValue())),
                    new Pieces.Press(Pieces.Look.GO, buy, "issues the " + Game.BUILD_BOND_YEARS + "-year bond, then buys"),
                    () -> {
                        game.handleLongBondForCash(gap, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);
                        buyOnTheLoan(ids, "bond");
                    }, Money::money));
            out.add(new Offer(Game.BUILD_NOTE_MONTHS + "-month note", null, note,
                    pct2(note.marketRate()) + " a year, taken as a discount",
                    rateColour(note, game.getDebtManager()),
                    String.format(game.getRollover().getMode() == Rollover.Mode.MANUAL
                                    ? "Falls due in %d months: the whole %s at once, out of the treasury."
                                    : "Falls due in %d months: the whole %s at once, refinanced then by the treasury's rollover.",
                            note.duration(), money(note.faceValue())),
                    new Pieces.Press(Pieces.Look.GO, buy, "issues the " + Game.BUILD_NOTE_MONTHS + "-month note, then buys"),
                    () -> {
                        game.handleTBillLogic(gap, Game.BUILD_NOTE_MONTHS, Game.BUILD_NOTE_GRANULE);
                        buyOnTheLoan(ids, "note");
                    }, Money::money));
        } else if (game.foreignWindowOpen()) {
            double gapUsd = game.landVaultGapUsd(ids);
            DebtQuote bond = game.quoteForeignForCash("Term", gapUsd, Game.BUILD_BOND_YEARS,
                    Game.BUILD_BOND_GRANULE);
            DebtQuote note = game.quoteForeignForCash("Note", gapUsd, Game.BUILD_NOTE_MONTHS,
                    Game.BUILD_NOTE_GRANULE);
            out.add(new Offer(Game.BUILD_BOND_YEARS + "-year dollar bond", ABROAD_INFO, bond,
                    pct2(bond.marketRate()) + " yield  ·  " + pct2(bond.couponRate()) + " coupon",
                    rateColour(bond, game.getDebtManager()),
                    String.format("Paid over %d years in dollars: the coupon every month, then the whole %s"
                            + " at the end.", bond.duration(), usd(bond.faceValue())),
                    new Pieces.Press(Pieces.Look.GO, buy, "issues it abroad, then buys from the vault"),
                    () -> {
                        game.handleForeignForCash("Term", gapUsd, Game.BUILD_BOND_YEARS,
                                Game.BUILD_BOND_GRANULE, true);
                        buyOnTheLoan(ids, "dollar bond");
                    }, Money::usd));
            out.add(new Offer(Game.BUILD_NOTE_MONTHS + "-month dollar note", ABROAD_INFO, note,
                    pct2(note.marketRate()) + " a year, taken as a discount",
                    rateColour(note, game.getDebtManager()),
                    String.format("Falls due in %d months: the whole %s at once, in dollars.",
                            note.duration(), usd(note.faceValue())),
                    new Pieces.Press(Pieces.Look.GO, buy, "issues it abroad, then buys from the vault"),
                    () -> {
                        game.handleForeignForCash("Note", gapUsd, Game.BUILD_NOTE_MONTHS,
                                Game.BUILD_NOTE_GRANULE, true);
                        buyOnTheLoan(ids, "dollar note");
                    }, Money::usd));
        }
        return out;
    }

    void showLandFunding(List<Integer> ids) {
        // Nothing to fund - the cash or the vault has caught up since the page was opened.
        if (!ui.game.landNeedsFunding(ids)) {
            showLandMenu();
            return;
        }
        ui.clearMenu("showLandFunding", () -> showLandFunding(ids));
        Game game = ui.game;
        boolean vaultPays = game.isLandPaidFromVault();
        String here = here();

        VBox page = widePage();
        page.getChildren().add(head("Funding"));

        Funding f = funding(ids);
        Label shortChip = tag("short by " + f.gapMoney(), Palette.BAD);
        Label title = new Label(f.priceLine());
        title.setWrapText(true);
        title.setMinWidth(0);
        title.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        HBox.setHgrow(title, Priority.ALWAYS);
        HBox top = new HBox(Palette.GAP, title, shortChip);
        top.setAlignment(Pos.CENTER_LEFT);
        SegmentBar bar = segmentBar(List.of(
                new Segment(f.held(), Palette.MONEY, false, null, null, f.heldWords(), null),
                new Segment(f.gap(), Palette.BAD, true, null, null, "short " + f.gapMoney(), null)), 0, null, 0, 14);
        Label held = new Label(f.heldWords());
        held.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        Label gapWords = new Label("short " + f.gapMoney());
        gapWords.setStyle(Palette.figure(Palette.SIZE_BODY, Palette.BAD));
        Region spread = new Region();
        HBox.setHgrow(spread, Priority.ALWAYS);
        HBox under = new HBox(6, held, spread, gapWords);
        under.setAlignment(Pos.CENTER_LEFT);
        VBox summary = new VBox(8, top, bar, under);
        summary.setStyle("-fx-padding: 12 14 12 14;" + Palette.block(Palette.PANEL, Palette.EDGE));
        summary.setMaxWidth(Double.MAX_VALUE);
        page.getChildren().addAll(summary, sectionHead(vaultPays ? "WAYS TO PAY" : "TWO WAYS TO BORROW IT",
                hint("each books exactly the quote on its card, then buys")));

        javafx.scene.layout.GridPane offers = equalColumns(3, TILE_GAP);
        List<Offer> loans = offers(ids);
        for (int i = 0; i < loans.size(); i++) {
            Offer o = loans.get(i);
            offers.add(offerCard(o.name(), o.info(), o.quote(), o.rate(), o.tone(), o.ending(), o.action(),
                    Icons.LAND, Palette.BUILDING, o.issue(), o.written()), i, 0);
        }
        if (vaultPays) {
            if (!game.foreignWindowOpen()) {
                VBox shut = alert("Nobody abroad will lend the dollars",
                        game.foreignWindowReason() + ". Only what the city already has can pay.");
                shut.setMaxWidth(Double.MAX_VALUE);
                shut.setPrefWidth(Region.USE_COMPUTED_SIZE);
                shut.setMaxHeight(Double.MAX_VALUE);
                offers.add(shut, 0, 0, 2, 1);
            }

            /* ...and the third way, a choice and never the default. */
            if (game.landTopUpCovers(ids)) {
                offers.add(topUpCard(ids, here), 2, 0);
            } else if (!game.foreignWindowOpen()) {
                Label none = new Label("The cash does not cover the rest either. Paying by "
                        + "converting cash borrows at home for it.");
                none.setWrapText(true);
                none.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_MUTED));
                VBox card = new VBox(none);
                card.setMaxWidth(Double.MAX_VALUE);
                card.setMaxHeight(Double.MAX_VALUE);
                card.setStyle(WORTH_CARD + Palette.EDGE + ";");
                offers.add(card, 2, 0);
            }
        }
        page.getChildren().add(offers);

        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> showLandMenu());
        page.getChildren().add(cancel);
        ui.rootMenu.getChildren().add(page);
    }

    /**
     * The third way, from the vault: what the vault holds, the rest converted
     * from cash - no debt. The 0.7.13 heading is its title, its note its line.
     */
    VBox topUpCard(List<Integer> ids, String here) {
        Game game = ui.game;
        Label title = new Label("The vault's dollars, and the rest converted from cash");
        title.setWrapText(true);
        title.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        Label line = new Label("No debt: the vault is emptied and the treasury buys the rest of the "
                + "dollars out of its cash.");
        line.setWrapText(true);
        line.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        VBox figures = new VBox(3,
                offerLine("From the vault", usd(game.getForeignAccounts().getReservesUsd()), Palette.TEXT_BODY),
                offerLine("Converted from cash", usd(game.landVaultGapUsd(ids)), Palette.TEXT_BODY),
                offerLine("...at today's rate", marked(here, money(game.landTopUpLocal(ids))), Palette.TEXT_BODY));
        Region push = new Region();
        VBox.setVgrow(push, Priority.ALWAYS);
        Pieces.ActionButton topUp = actionButton(Icons.LAND, Palette.BUILDING, ACTION_TALL, topUpPress(ids),
                () -> buyOnTheLoan(ids, null));
        VBox card = new VBox(6, title, figures, line, push, topUp);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setMaxHeight(Double.MAX_VALUE);
        card.setStyle(WORTH_CARD + Palette.EDGE + ";");
        return card;
    }

    /** The third way's button (0.7.34): the purchase, and what it takes - the 0.7.13 button's words, under it. */
    Pieces.Press topUpPress(List<Integer> ids) {
        return new Pieces.Press(Pieces.Look.GO, buyWords(ids), "takes what the vault has and converts the rest");
    }

    /**
     * The money is in: the plots are bought, each as its own button would
     * buy it, and whatever the purchase answers is shown - the build
     * screen's rule that a refusal nobody reads is a silent one
     * (BuildScreen.goAhead()). paper is the offer taken, or null for
     * the vault's dollars with the rest converted. Short of all of them -
     * which should not happen - one red card says so, in the office's frame.
     */
    private void buyOnTheLoan(List<Integer> ids, String paper) {
        rememberBeforeBuying();
        int bought = ui.game.buyLandParcels(ids);
        if (bought >= ids.size()) {
            showLandMenu();
            return;
        }
        freeBeforeBuying = Double.NaN;
        depositsBeforeBuying = -1;
        ui.clearMenu("showLandFellShort", () -> showLandMenu());
        VBox page = widePage();
        page.getChildren().add(head("Not bought"));
        VBox card = alert(paper == null ? "The land is not bought" : "The money is in, the land is not",
                String.format("%s%d of %d plot%s bought: the rest still cost more than the city "
                                + "holds. Nothing beyond %s was spent.",
                        paper == null ? "" : "The " + paper + " was issued. ",
                        bought, ids.size(), ids.size() == 1 ? "" : "s",
                        paper == null ? "the plots bought" : "the " + paper + " and the plots bought"));
        Button back = new Button("Back to the land office");
        back.setOnAction(e -> showLandMenu());
        page.getChildren().addAll(card, back);
        ui.rootMenu.getChildren().add(page);
    }
}
