package ham.citybuildersim.ui;

import ham.citybuildersim.*;
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
 * buy. Since 0.7.26 in the style Build got in 0.7.24 and 0.7.25: a head with
 * how the city pays; THE GROUND - the ground free now and the plots the next
 * purchase would add, as one bar, with the position across its top; the nine
 * plots on offer as a three-by-three of cards, cheapest ground a square foot
 * first, each with its price, its value against the going rate and its tags;
 * what the ground is worth to the city in four cards - the margin, the ground
 * on top of the build, the ore, who is waiting; the price of ground over the
 * city's life behind "details"; and, when the city is short, the funding page
 * in the same frame.
 *
 * BEST VALUE is the top-left card: the cheapest ground a square foot with any
 * ore on it paid for inside its price (Game.landShelf()'s order) - Jerus's
 * "the best one is always at the top left". It is not LandMarket.bestValue(),
 * which skips ore and so sits further down the shelf; this screen never called
 * it, though this comment said it did until 0.7.26. MOST ORE is
 * LandMarket.richestDeposit(). The two are tags of their own, and one card can
 * carry both.
 *
 * In US dollars since 0.7.6: every plot shows its dollar price and what that
 * costs in local money at today's rate, and a chip pair at the top chooses
 * whether the treasury converts cash for it (the default) or pays out of the
 * vault. Since 0.7.13 a plot's price is large in the money the toggle pays in,
 * its size in square kilometres beside its square feet (the square feet to
 * three figures since 0.7.26), its button stays live and, short, opens the
 * funding page sized to the gap (showLandFunding()), and a control buys the
 * next N plots at once (nextPlots()).
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
 * groundFigures(), plot(), margin(), and the rest), so a probe without the
 * toolkit can read every word on a played city; the drawing methods lay those
 * out. Split out of UserInterface on 2026-09-18; the rail calls
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
       should I buy?", and the page answers it top down:

         the head        - "Land office", its (i), the pay toggle with a line
                           under it, and the way back to Build;
         THE GROUND      - four cells (the ground free, who is waiting, what
                           the world asks, what investors pay) and the ground
                           as a bar: free now solid, then the next N plots as
                           numbered ghosts, on a scale of the two together so
                           the bar always reads - at "everything owned" the
                           free ground of a built-over city is a hair;
         ON OFFER        - the Buy-next-N control at its right, then the nine
                           cards; the next N carry a number and a pink edge,
                           the numbers on the bar's ghosts;
         WHAT THE GROUND IS WORTH - the margin, the ground on top of the
                           build, the ore, who is waiting;
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
       same card before. Since 0.7.34 Buy is Pieces' action button, the
       card's width, and says the price: "Buy · D$1.0B".
       ===================================================================== */

    /** How many plots the next-N control buys; kept across redraws, held inside 1..listed. */
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

    /** A card whose id is above this is NEW (ids only grow: LandMarket's next id)... */
    private int newAbove = Integer.MAX_VALUE;

    /** ...in this month only. */
    private int newMonth = -1;

    /** The month, the rate and the world's dollar price at the last draw: what the month's pop compares. */
    private int shownMonth = -1;
    private double shownRate = Double.NaN, shownWorldUsd = Double.NaN;

    /** The cards and the waiting card as last drawn, for the bar's and the strip's clicks to scroll to. */
    private final List<Region> shelfCards = new ArrayList<>();
    private Region waitingCard;

    /** A new city or a load: nothing on its shelf is new to the player, and nothing has just been bought. */
    void forget() {
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
        shelfCards.clear();
        waitingCard = null;

        Next next = next();
        HBox head = head(null);
        VBox ground = groundPanel(next);
        HBox offerHead = sectionHead("ON OFFER · cheapest ground first", offerInfo(), nextPlots(next));
        page.getChildren().addAll(head, ground, offerHead);
        String receipt = ui.game.getLastLandReceipt();
        if (receipt != null && !receipt.isEmpty()) page.getChildren().add(receiptLine(receipt));
        page.getChildren().add(shelf(next));
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

    /** An area in square feet, compact to three figures: "324k sq ft", "9.68M sq ft", "8,000 sq ft". */
    static String sqFt(double v) { return compact3(v) + " sq ft"; }

    /** A count to three significant figures past ten thousand - "324k", "9.68M", "13.7M" - and whole below. */
    static String compact3(double v) {
        if (!(Math.abs(v) >= 10_000)) return formatter.format(Math.round(v));
        double r = new java.math.BigDecimal(v).round(new java.math.MathContext(3)).doubleValue();
        double a = Math.abs(r);
        double[] at = { 1e12, 1e9, 1e6, 1e3 };
        String[] unit = { "T", "B", "M", "k" };
        for (int i = 0; i < at.length; i++) {
            if (a >= at[i]) {
                return new java.math.BigDecimal(r / at[i]).round(new java.math.MathContext(3))
                        .stripTrailingZeros().toPlainString() + unit[i];
            }
        }
        return formatter.format(Math.round(r));
    }

    /** A price a square foot in the city's money: "D$46.52". */
    String local(double thousandsPerSqFt) { return marked(here(), unitPrice(thousandsPerSqFt)); }

    /** ...in the world's: "US$24.54". */
    static String dollars(double thousandsPerSqFt) { return marked(Currency.FOREIGN_SYMBOL, unitPrice(thousandsPerSqFt)); }

    /** A price a square foot with its direction: "+D$29.16", "−D$3.10". */
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
                new Cell("GROUND FREE", sqFt(free),
                        LandManager.km2Words(free) + " · " + String.format("%.1f%%", land.getUtilisation() * 100)
                                + " of " + LandManager.km2Words(land.getOwnedSqFt()) + " built on",
                        BuildScreen.verdict(ground.level())),
                new Cell("WAITING ON GROUND", waiting + (waiting == 1 ? " sector" : " sectors"),
                        waiting == 0 ? "every sector has room" : String.join(", ", waitingNames()),
                        waiting > 0 ? Palette.BAD : Palette.TEXT_HEAD),
                new Cell("THE WORLD ASKS", dollars(land.getGroundUsdPerSqFt()) + " /sq ft",
                        local(land.getAcquisitionCostPerSqFt()) + " today, for new plots", Palette.TEXT_HEAD),
                new Cell("INVESTORS PAY", local(land.getPricePerSqFt()) + " /sq ft",
                        signedLocal(margin) + " a sq ft " + (margin < 0 ? "under" : "over") + " the world",
                        margin < 0 ? Palette.BAD : Palette.GOOD) };
    }

    /** The plots "the next N" buys, in the shelf's order. */
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
     * then what the next N would leave free - "324k sq ft free", "room for 40
     * houses", "→ 37.6M sq ft free after the next 5", "room for 4,704 houses".
     * The last two are empty with nothing on the shelf.
     */
    String[] groundFigures(Next next) {
        double free = ui.game.getLandManager().getAvailableSqFt();
        double after = free;
        for (LandParcel p : nextShelf(next)) after += p.getSizeSqFt();
        boolean any = !next.ids().isEmpty();
        return new String[] {
                sqFt(free) + " free",
                room(free),
                any ? "→ " + sqFt(after) + " free after the next " + (next.n() == 1 ? "plot" : String.valueOf(next.n())) : "",
                any ? room(after) : "" };
    }

    /** The bar's scale: the ground free and the next N together. */
    double groundScale(Next next) {
        double scale = ui.game.getLandManager().getAvailableSqFt();
        for (LandParcel p : nextShelf(next)) scale += p.getSizeSqFt();
        return scale;
    }

    /**
     * The bar's segments: the ground free, solid pink; then the next N, each
     * a ghost in the light pink, numbered as its card is, with a sand stripe
     * where it holds ore, its size and price on hover and a click to its card.
     */
    List<Segment> groundSegments(Next next, java.util.function.IntFunction<Runnable> go) {
        double free = ui.game.getLandManager().getAvailableSqFt();
        String roomFree = room(free);
        List<Segment> parts = new ArrayList<>();
        parts.add(new Segment(free, Palette.BUILDING, false, null, null,
                "free now · " + sqFt(free) + (roomFree.isEmpty() ? "" : " · " + roomFree), null));
        List<LandParcel> plots = nextShelf(next);
        for (int i = 0; i < plots.size(); i++) {
            LandParcel p = plots.get(i);
            parts.add(new Segment(p.getSizeSqFt(), Palette.BUILDING_LIGHT, true, p.hasIron() ? Palette.ORE : null,
                    String.valueOf(i + 1),
                    "plot " + (i + 1) + " · " + sqFt(p.getSizeSqFt()) + " · " + priceInToggle(p)
                            + (p.hasIron() ? " · ore under it" : "") + "\nClick for its card.",
                    go == null ? null : go.apply(i)));
        }
        return parts;
    }

    /** The bar's one tick: NEEDS YOU's line, the free ground under which the GROUND row is listed. */
    List<Tick> groundTicks() {
        CityNeeds.Need ground = CityNeeds.ground(ui.game, SummaryScreen.WORDS);
        return List.of(new Tick(ground.yellow(), Palette.TEXT_MUTED, 2, "NEEDS YOU's line",
                "NEEDS YOU lists the ground at " + sqFt(ground.yellow()) + " free or less, and in red at none."));
    }

    /** A plot's price in the money the toggle pays in: "D$149.7M" converting, "US$211.7M" from the vault. */
    String priceInToggle(LandParcel p) {
        return ui.game.isLandPaidFromVault() ? usd(p.getPriceUsd())
                : marked(here(), money(p.localPrice(ui.game.getForeignAccounts().getRate())));
    }

    /* ----------------------------- buy the next N ----------------------------- */

    /**
     * What "Buy the next N" buys and says: how many, out of how many listed,
     * their ids, the total in the toggle's money (neutral, red only when no
     * way pays without debt - canAffordLandParcels()), the other money small,
     * the button's words (a Pieces.Press since 0.7.34) and whether it opens
     * the funding page.
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
        String n = nextCount == 1 ? "plot" : String.valueOf(nextCount);
        return new Next(nextCount, listed, ids,
                vault ? usd(usdTotal) : marked(here(), money(localTotal)),
                g.canAffordLandParcels(ids) ? Palette.TEXT_HEAD : Palette.BAD,
                vault ? marked(here(), money(localTotal)) + " at today's rate" : usd(usdTotal) + " listed",
                // The total stands just left of the button, so the button says the count, not the price again.
                !funding ? new Pieces.Press(Pieces.Look.GO, "Buy the next " + n, null)
                        : vault ? new Pieces.Press(Pieces.Look.CREDIT, "Buy the next " + n, "the vault is short: ways to pay")
                        : new Pieces.Press(Pieces.Look.CREDIT, "Buy the next " + n + " on credit", null),
                !funding ? (nextCount == 1 ? "Buys this plot, as its own card's Buy would."
                                : "Buys these " + nextCount + " plots, each as its own card's Buy would.")
                        : vault ? "The vault is short of them: opens the funding page - dollars borrowed abroad,"
                                + " or the vault's dollars with the rest converted from cash."
                        : "The cash is short of them: opens the funding page - a loan sized to the gap.",
                funding);
    }

    /**
     * The section's (i): the floor under every plot, worded as LandMarket
     * moves it (0.7.26: it said the office "stops splitting them at all as
     * the city grows"; the floor rises a block for every
     * BLOCKS_PER_FLOOR_STEP the city has bought, to MAX_MIN_BLOCKS), and how
     * many are listed (LISTING_SIZE, which the paragraph had as "Nine").
     */
    String offerInfo() {
        LandMarket market = ui.game.getLandManager().getMarket();
        return String.format("The office lists %d plots at a time, cheapest ground a square foot first - any ore "
                        + "under a plot is paid for inside that price - so the top-left card is the best value. "
                        + "It sells nothing smaller than %s (%s) now. The smallest plot grows one block for every "
                        + "%s blocks the city has bought, up to %s blocks. A plot listed earlier keeps the size and "
                        + "the US$ price it was listed at.",
                LandMarket.LISTING_SIZE, LandManager.km2Words(market.getMinSqFt()), sqFt(market.getMinSqFt()),
                formatter.format(LandMarket.BLOCKS_PER_FLOOR_STEP), formatter.format(LandMarket.MAX_MIN_BLOCKS));
    }

    /* ----------------------------- one plot ----------------------------- */

    /** A tag's words and colour. */
    record TagWords(String text, String colour) { }

    /**
     * Everything one card says: its badge (1..N in the next N, else 0), the
     * size and its caption, the price in the toggle's money and its colour,
     * the other money, the value bar's shares (its own dollars a square foot,
     * the going rate's and the world's, on one scale for the nine), the
     * value's words and colour, its tags, the Buy button's words (a
     * Pieces.Press since 0.7.34) and tooltip, and the card's own tooltip.
     */
    record Plot(LandParcel parcel, int badge, String size, String caption, String price, String priceTone,
                String other, double share, double goingAt, double worldAt, String value, String valueTone,
                List<TagWords> tags, Pieces.Press button, String buttonTip, boolean funding, String tip) { }

    /**
     * The value bars' scale: the dearest of the nine a square foot - or the
     * going rate, or the world's price today, if either is higher, so both
     * ticks are always on the bar.
     */
    double valueScale() {
        LandMarket market = ui.game.getLandManager().getMarket();
        double top = Math.max(market.goingUsdPerSqFt(), ui.game.getLandManager().getGroundUsdPerSqFt());
        for (LandParcel p : ui.game.landShelf()) top = Math.max(top, p.getUsdPerSqFt());
        return top;
    }

    Plot plot(LandParcel p, int index, Next next) {
        Game g = ui.game;
        LandManager land = g.getLandManager();
        LandMarket market = land.getMarket();
        boolean vault = g.isLandPaidFromVault();
        String here = here();
        double rate = g.getForeignAccounts().getRate();
        double going = market.goingUsdPerSqFt();
        double world = land.getGroundUsdPerSqFt();
        double scale = valueScale();
        double perSqFt = p.getUsdPerSqFt();
        double against = going > 0 ? (going - perSqFt) / going * 100 : 0;

        String listedUsd = usd(p.getPriceUsd());
        String todayHere = marked(here, money(p.localPrice(rate)));
        String roomWords = room(p.getSizeSqFt());

        String value = Math.abs(against) < 1
                ? dollars(perSqFt) + " /sq ft · the going rate"
                : String.format("%s /sq ft · %.0f%% %s the going rate", dollars(perSqFt), Math.abs(against),
                        against > 0 ? "under" : "over");

        List<TagWords> tags = new ArrayList<>();
        if (index == 0) tags.add(new TagWords("BEST VALUE", Palette.GOOD));
        if (p.hasIron()) {
            tags.add(new TagWords("ORE ×" + p.getDeposits() + " · " + shortNumber(p.getIronTonnes()) + " t",
                    Palette.ORE));
        }
        LandParcel richest = market.richestDeposit();
        if (richest != null && richest.getId() == p.getId()) tags.add(new TagWords("MOST ORE", Palette.ORE));
        if (isNew(p)) tags.add(new TagWords("NEW", Palette.BUILDING));

        boolean funding = g.landNeedsFunding(List.of(p.getId()));
        // The button says the price in the money the toggle pays in, as the card's big figure does.
        String priceHere = vault ? listedUsd : todayHere;
        Pieces.Press button = !funding ? new Pieces.Press(Pieces.Look.GO, "Buy · " + priceHere, null)
                : vault ? new Pieces.Press(Pieces.Look.CREDIT, "Buy · " + priceHere, "the vault is short: ways to pay")
                : new Pieces.Press(Pieces.Look.CREDIT, "Buy on credit · " + priceHere, null);
        String buttonTip = !funding ? "Buy this plot"
                : vault ? "Buy — the vault is short\nOpens the funding page: dollars borrowed abroad, or the "
                        + "vault's dollars with the rest converted from cash."
                : "Buy — borrow for it\nOpens the funding page: a loan sized to the gap.";

        String tip = String.format("%s (%s), listed at %s: %s at today's rate.%n%s a sq ft against the going rate "
                        + "of %s and the world's %s today.%s",
                sqFt(p.getSizeSqFt()), LandManager.km2Words(p.getSizeSqFt()), listedUsd, todayHere,
                dollars(perSqFt), dollars(going), dollars(world),
                p.hasIron() ? String.format("%nOre: %d deposit%s, %s t in the ground.", p.getDeposits(),
                        p.getDeposits() == 1 ? "" : "s", shortNumber(p.getIronTonnes())) : "");

        return new Plot(p, index < next.ids().size() ? index + 1 : 0,
                sqFt(p.getSizeSqFt()),
                " · " + LandManager.km2Words(p.getSizeSqFt()) + (roomWords.isEmpty() ? "" : " · " + roomWords),
                vault ? listedUsd : todayHere,
                g.canAffordParcel(p) ? Palette.TEXT_HEAD : Palette.BAD,
                " · " + (vault ? todayHere + " at today's rate" : listedUsd + " listed"),
                scale > 0 ? perSqFt / scale : 0, scale > 0 ? going / scale : -1, scale > 0 ? world / scale : -1,
                value, against > -1 ? Palette.GOOD : Palette.WARN,
                tags, button, buttonTip, funding, tip);
    }

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
    record Margin(BarWords world, BarWords investors, String chip, String chipTone, String lastMonth, String info) { }

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
        String info = "Outside, you buy at " + dollars(land.getGroundUsdPerSqFt()) + " /sq ft (" + local(world)
                + " today). Rises with the city — with the land owned, and with the people. Asked in US"
                + " dollars, so a weaker currency makes it dearer here and a stronger one cheaper.\n\n"
                + "Inside, they buy at " + local(inside) + " /sq ft. Supply against demand — the more land"
                + " standing free, the cheaper. Local money, and the exchange rate does not reach it.\n\n"
                + "Margin " + signedLocal(margin) + " /sq ft. " + (margin < 0
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
                signedLocal(margin) + " a sq ft", margin < 0 ? Palette.BAD : Palette.GOOD,
                sold > 0 ? "investors paid " + marked(here, money(sold)) + " for ground last month"
                        : "investors paid nothing for ground last month",
                info);
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
            + " a square foot today, then the building's own cash cost; the figure at its right is the ground's"
            + " share of the two.";

    /** The Ore card: the deposits, the tonnes, the mines on them, and whether ore lies undug. */
    record Ore(String deposits, String tonnes, String mines, boolean undug, String popped) { }

    Ore ore() {
        LandManager land = ui.game.getLandManager();
        int deposits = land.getIronDeposits();
        int mines = ui.game.minesCommitted();
        int gained = depositsBeforeBuying >= 0 ? deposits - depositsBeforeBuying : 0;
        return new Ore(formatter.format(deposits) + (deposits == 1 ? " deposit" : " deposits"),
                shortNumber(land.getIronReserveTonnes()) + " t in the ground",
                formatter.format(mines) + (mines == 1 ? " mine" : " mines") + " on them, standing or on site",
                mines < deposits,
                gained > 0 ? "+" + gained + (gained == 1 ? " deposit" : " deposits") : null);
    }

    /** The Ore card's (i). */
    static final String ORE_INFO = "A mine stands on one deposit, and every mine draws on the city's tonnes"
            + " together. Deposits come only with land: a plot with ORE on its card brings them, and the ore"
            + " is paid for inside its price. Click the card for Build › Industry, where the Iron Mine is.";

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
    static final String DETAILS_INFO = "What the world asks for a square foot of ground, as each month recorded"
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
        SegmentBar bar = segmentBar(groundSegments(next, i -> () -> {
            if (i < shelfCards.size()) scrollTo(shelfCards.get(i));
        }), scale, groundTicks(), 0, 18);
        if (Double.isFinite(freeBeforeBuying)) bar.animateFirst(freeBeforeBuying, 300);

        Label zero = new Label("0");
        zero.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        HBox key = new HBox(Palette.GAP_LOOSE, keySwatch(Palette.BUILDING, "free now"));
        key.setAlignment(Pos.CENTER);
        if (!next.ids().isEmpty()) {
            key.getChildren().add(ghostSwatch(Palette.BUILDING_LIGHT,
                    next.n() == 1 ? "the next plot" : "the next " + next.n() + ", numbered as their cards"));
            boolean ore = false;
            for (LandParcel p : nextShelf(next)) ore |= p.hasIron();
            if (ore) key.getChildren().add(keySwatch(Palette.ORE, "ore under it"));
        }
        Label end = new Label(sqFt(scale));
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

    /* ----------------------------- BUY THE NEXT N PLOTS (0.7.13) -----------------------------
     *
     * Jerus: "add a button to buy multiple, so for example buy the next 5
     * land options, and then also debt popup appears if not enough". The
     * first N plots in the office's order (Game.nextLandParcels()), their
     * price together in the money the toggle pays in with the other money
     * beside it, and a button that buys them each as its own card's button
     * would (Game.buyLandParcels()) - or, short, opens the same funding page,
     * sized to the whole gap. N starts at five and runs from one to what is
     * listed. Since 0.7.26 at the right of ON OFFER, and the N cards it buys
     * carry their numbers and a pink edge, as the bar's ghosts do.
     * ---------------------------------------------------------------------------------------- */
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

    /* ----------------------------- ON OFFER: the shelf -----------------------------
     *
     * SORTED, CHEAPEST GROUND FIRST, so the best buy is always the top-left
     * card. Jerus: "sorted by best value so the best one is always at the
     * top left". Price per square foot ascending, which is the only ranking
     * that means anything across plots of different sizes - a big plot is
     * not a better deal for being big. The order is the model's since 0.7.13
     * (Game.landShelf()), because "Buy the next N plots" buys the first N of
     * it and the cards must be those N.
     *
     * ORE IS A DIFFERENT QUESTION and is not folded into the ranking beyond
     * its price: whether a deposit's premium is worth paying depends on
     * whether the city wants a mine, which no single number can decide. It
     * keeps its own tag.
     *
     * AND THE "JUST BUY THE CHEAPEST" BUTTON IS GONE. Jerus: "remove the buy
     * cheapest button since best value is always better and two is
     * confusing." Cheapest by PRICE is whichever plot is smallest, which is
     * the one piece of ground least worth owning per dollar.
     *
     * THREE BY THREE, THE PAGE'S WIDTH (0.7.26): nine wide cards rather than
     * 250 px tiles centred in a page five times as wide; nine stays a square,
     * as Jerus asked ("make it so its only 9 cards").
     * ------------------------------------------------------------------------------- */
    javafx.scene.layout.GridPane shelf(Next next) {
        javafx.scene.layout.GridPane grid = equalColumns(3, TILE_GAP);
        List<LandParcel> shelf = ui.game.landShelf();
        boolean turned = shownMonth >= 0 && shownMonth != ui.game.getMonth()
                && ui.game.getForeignAccounts().getRate() != shownRate;
        for (int i = 0; i < shelf.size(); i++) {
            Region card = plotCard(plot(shelf.get(i), i, next), turned);
            shelfCards.add(card);
            grid.add(card, i % 3, i / 3);
        }
        return grid;
    }

    /**
     * One plot, as a card: the land icon in its square (with its number when
     * it is in the next N), then its size and what it holds, its price, and
     * its value - its dollars a square foot as a bar, with the going rate's
     * tick and the world's price today as a hairline, and the verdict in
     * words; at the right its tags; under it all, Buy, the card's width
     * (0.7.34). The card itself does nothing on a click; only Buy acts.
     *
     * THE PRICE PER SQUARE FOOT IS THE POINT, and it was the one figure the
     * first listing did not carry. A plot's total price says nothing on its
     * own - a big expensive plot and a small cheap one are the same deal - so
     * comparing nine meant nine divisions done in the player's head. Against
     * the going rate on this listing (LandMarket.goingUsdPerSqFt()) it is a
     * verdict: this one is a bargain, that one is not.
     *
     * BOTH PRICES (0.7.6), THE ONE THE TOGGLE PAYS IN LARGE (0.7.13). Jerus:
     * "if you are on convert currency to usd to buy, even tho you pay usd, the
     * land should show your own currency cost, and if you have it on use
     * reserves then the cost is shown is usd".
     *
     * THE BUTTON STAYS CLICKABLE (0.7.13). Short, it opens the funding page
     * rather than greying out - the build screen's way. From the vault, a
     * vault short of the dollars opens it too, even with the cash to convert
     * the rest: that way is one of the page's choices, not what the button
     * does by itself.
     *
     * @param turned the month has turned and moved the rate: the price in
     *               local money pops
     */
    Region plotCard(Plot p, boolean turned) {
        boolean vault = ui.game.isLandPaidFromVault();

        StackPane square = new StackPane(iconSquare(Icons.LAND, Palette.BUILDING, 36, 18));
        square.setMinSize(36, 36);
        square.setMaxSize(36, 36);
        if (p.badge() > 0) {
            Label badge = new Label(String.valueOf(p.badge()));
            badge.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.ON_FILL)
                    + " -fx-background-color: " + Palette.BUILDING + "; -fx-background-radius: 8;"
                    + " -fx-padding: 0 4 0 4;");
            badge.setMinSize(16, 16);
            badge.setAlignment(Pos.CENTER);
            StackPane.setAlignment(badge, Pos.TOP_LEFT);
            badge.setTranslateX(-6);
            badge.setTranslateY(-6);
            square.getChildren().add(badge);
        }
        VBox left = new VBox(square);
        left.setMinWidth(40);

        javafx.scene.text.Text size = BuildScreen.textRun(p.size(), Palette.Fonts.sansSemiBold(),
                Palette.SIZE_SECTION, Palette.TEXT_HEAD);
        javafx.scene.text.Text caption = BuildScreen.textRun(p.caption(), null, Palette.SIZE_CAPTION + .5,
                Palette.TEXT_MUTED);
        javafx.scene.text.TextFlow sizeLine = new javafx.scene.text.TextFlow(size, caption);
        sizeLine.setMinWidth(0);

        javafx.scene.text.Text price = BuildScreen.textRun(p.price(), Palette.Fonts.monoSemiBold(), 14, p.priceTone());
        javafx.scene.text.Text other = BuildScreen.textRun(p.other(), null, Palette.SIZE_CAPTION + .5,
                Palette.TEXT_MUTED);
        javafx.scene.text.TextFlow priceLine = new javafx.scene.text.TextFlow(price, other);
        priceLine.setMinWidth(0);

        List<Tick> ticks = new ArrayList<>();
        if (p.goingAt() >= 0) {
            ticks.add(new Tick(p.goingAt(), Palette.TEXT_LABEL, 2, null,
                    "the going rate on this listing: the middle of the nine a square foot"));
        }
        if (p.worldAt() >= 0) {
            ticks.add(new Tick(p.worldAt(), Palette.TEXT_MUTED, 1, null,
                    "what the world asks a square foot today, for a plot listed now"));
        }
        SegmentBar valueBar = segmentBar(List.of(Segment.of(p.share(), Palette.BUILDING)), 1, ticks, 0, 6);
        valueBar.setPrefWidth(180);
        valueBar.setMaxWidth(180);
        Label value = new Label(p.value());
        value.setWrapText(true);
        value.setMinWidth(0);
        value.setStyle(Palette.words(Palette.SIZE_CAPTION + 1, p.valueTone()));
        VBox valueBox = new VBox(2, valueBar, value);

        VBox middle = new VBox(4, sizeLine, priceLine, valueBox);
        middle.setMinWidth(0);
        HBox.setHgrow(middle, Priority.ALWAYS);

        VBox tags = new VBox(3);
        tags.setAlignment(Pos.TOP_RIGHT);
        for (TagWords t : p.tags()) tags.getChildren().add(tag(t.text(), t.colour()));
        VBox right = new VBox(4, tags);
        right.setAlignment(Pos.TOP_RIGHT);
        right.setMinWidth(Region.USE_PREF_SIZE);

        // Buy, the card's width under what it buys (0.7.34): Pieces' action button, saying the price.
        List<Integer> just = List.of(p.parcel().getId());
        Pieces.ActionButton buy = actionButton(Icons.LAND, Palette.BUILDING, ACTION_TALL, p.button(),
                () -> buyOrFund(just));
        Tooltip buyTip = new Tooltip(p.buttonTip());
        buyTip.setShowDelay(Duration.millis(250));
        Tooltip.install(buy, buyTip);

        HBox upper = new HBox(10, left, middle, right);
        VBox.setVgrow(upper, Priority.ALWAYS);
        VBox card = new VBox(10, upper, buy);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setMaxHeight(Double.MAX_VALUE);
        card.setStyle("-fx-padding: 10 12 10 12; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8;"
                + (p.badge() > 0 ? " -fx-border-color: " + Palette.BUILDING + "; -fx-border-width: 2;"
                                 : " -fx-border-color: " + Palette.EDGE + "; -fx-border-width: 1;"));
        Tooltip cardTip = new Tooltip(p.tip());
        cardTip.setShowDelay(Duration.millis(400));
        Tooltip.install(card, cardTip);

        // The month moved the rate: the local price - large converting, small from the vault - pops.
        if (turned) popText(vault ? other : price);
        return card;
    }

    /** popPip()'s quarter second, for a run of text in a flow: its flow pops. */
    private static void popText(javafx.scene.text.Text run) {
        if (run.getParent() instanceof Region flow) UserInterface.popPip(flow);
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
        return worthCard(Icons.COIN, Palette.MONEY, "Margin", m.info(),
                namedBar(m.world()), namedBar(m.investors()), chipLine, last);
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
        VBox card = worthCard(Icons.ORE, Palette.ORE, "Ore", ORE_INFO, top, tonnes, mines);
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
                v -> dollars(v) + " /sq ft"));
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
     * (BuildScreen.buildOnTheLoan()). paper is the offer taken, or null for
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
