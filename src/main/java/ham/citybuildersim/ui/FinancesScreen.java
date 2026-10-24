package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import ham.citybuildersim.ui.Pieces.Column;
import ham.citybuildersim.ui.Pieces.Segment;
import ham.citybuildersim.ui.Pieces.Tick;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Statement.*;
import static ham.citybuildersim.ui.Pieces.*;

/**
 * The Finances tab: what the city owes and when it falls due, what its paper
 * costs and who holds it, why its money costs what it does, every piece and
 * what it would cost to retire, borrowing at home and abroad, the money
 * itself, the businesses' bond market and the city's fund - a hub and six
 * areas under five figures.
 *
 * WHY, and why it looks like this (0.7.32). Split out of UserInterface on
 * 2026-09-18 with its banners verbatim, it was a 560 px statement column:
 * six rows on a landing, thirteen pages of lines and some seventy
 * paragraphs, two settings repeated on three pages, and a ladder that
 * called its "later" bin "year 13" and labelled its bars a year early.
 * Jerus, on the screens not yet redone: "the others are still full of text
 * and the design could be more intuitive and fun". Redrawn in Build's style
 * (the project's spec-finances-0732.md): the hub is the debt's dashboard -
 * the ladder by calendar year with NEXT DUE beside it (the 0.7.24 card,
 * folded in), the rollover and the bank's rescue said once each, and the six
 * areas as cards; every page leads with the one picture that answers its
 * question; the paragraphs are behind an (i) and the old tables behind
 * "details". The figures are the model's: the ladder, the next twelve
 * months, the coupon and each kind's principal are DebtManager's (0.7.32),
 * the rollover's cash Rollover.Plan's, the bond market's sums BondMarket's.
 * One verdict each: TREASURY in NEEDS YOU's colour, OWED red only when the
 * market has priced the city out, NEXT DUE by FALLS DUE's line, the service
 * bands CityNeeds' constants. Local money is written D$ throughout, because
 * the tab shows dollars too.
 *
 * The shell sets the area and page back to the hub (financeArea,
 * financePage; its resetSection()) before the rail and the header's money
 * block open showFinanceMenu(), and Government's doors set them and call it
 * too (GovernmentScreen.openFinances()); open() is every other door in.
 * THE DEBT RESULT - what an issue booked, shown after it - came here from
 * the build cards on 2026-09-18, because it is this tab's.
 */
final class FinancesScreen {

    /** The window this screen draws into: its game, its root, its clearMenu(). */
    private final UserInterface ui;

    FinancesScreen(UserInterface ui) {
        this.ui = ui;
        this.fundScreen = new FundScreen(ui, this);
        this.borrowChips = new CapChips(() -> borrowCap, cap -> borrowCap = cap, this::showFinanceMenu, ui::redrawSoon);
        this.rolloverChips = new CapChips(() -> ui.game.getRolloverCap(), cap -> ui.game.setRolloverCap(cap),
                this::showFinanceMenu, ui::redrawSoon);
    }

    /** The city's fund's pages and a security's (0.7.39): the area is drawn by its own class. */
    final FundScreen fundScreen;

    /* =====================================================================
       FINANCES

       WHAT IT WAS: one long page of buttons, then (2026-09) a landing of six
       rows in a 560 px column, each opening a strip of statement pages.

       WHAT IT IS (0.7.32): a hub and six areas, each answering one question.

         THE HUB        what does the city owe, when does it fall due, and
                        what happens when it does? The ladder, NEXT DUE, the
                        rollover and the rescue, and the six areas as cards.
         THE POSITION   can the city carry it, and does the market mind?
                        Overview, Debt service, Home & abroad, Your rate.
         THE BOOK       what is each piece, and what would it cost to retire?
                        One page, a desk since CD6: every line a row, and a
                        page for each line, which has Close it now (CD4).
         BORROW        what would this paper cost, and what would it do to
                        the ladder? At home, Abroad.
         MONEY          who made the money, and how much is there (0.7.0)?
         THE BOND       who borrows on the market, and who lends to them
         MARKET         (0.7.12)? Every issue, a bond's book.
         THE CITY'S     what does it hold, what did it cost, and what has it
         FUND           made (0.7.14; 0.7.39)? Portfolio, Search, Activity,
                        Rules & cash, and a page a security - FundScreen's.

       THE BANK IS NOT HERE ANY MORE. Jerus: "i think we are going to have 11
       rails, bank is its own thing." It is a rail tab now.
       ===================================================================== */

    /** Which area is open; null is the hub. */
    String financeArea = null;
    /** The page an area opens on when none is named: the first of any area's pages that is called this. */
    static final String FINANCE_HOME  = "Overview";
    String financePage = FINANCE_HOME;

    /** The six areas, in the hub's order. */
    static final String[] AREAS = {"The position", "The book", "Borrow", "Money", "The bond market", "The city's fund"};
    /** ...and each one's icon, on its card. */
    static final String[] AREA_ICONS = {Icons.FINANCES, Icons.PAPER, Icons.COIN, Icons.BANKNOTE, Icons.REPORTS, Icons.SAFE};

    /** The position's four pages (0.7.32: "The ladder" became the hub's hero). */
    static final String[] POSITION_PAGES = {"Overview", "Debt service", "Home & abroad", "Your rate"};
    /** ...and their icons on the chips. */
    static final String[] POSITION_ICONS = {Icons.OVERVIEW, Icons.COIN, Icons.TRADE, Icons.POLICY};
    /**
     * The book is one page since 0.7.32, a desk of lines since CD6. A line's
     * own page, LINE_PAGE, and Close it now's, CLOSE_PAGE (CD4: it was Buy
     * back), are no chips': each is open while its piece is on the books
     * (lineOpenNow(), closeOpen()).
     */
    static final String[] BOOK_PAGES   = {"Every piece"};
    /** The page Close it now opens on, under The book (CD4): one piece's reverse auction, its ticket, its ladder and its call. */
    static final String CLOSE_PAGE     = "Close it now";
    /** Borrow's two pages: the city's own paper at home, and dollars from the world. */
    static final String[] BORROW_PAGES = {"At home", "Abroad"};
    /** ...and their icons on the chips. */
    static final String[] BORROW_ICONS = {Icons.HOMES, Icons.TRADE};
    /** The central bank's books and the money supply, on one page (0.7.0). */
    static final String[] MONEY_PAGES  = {"Money"};
    /** The businesses' bonds: every issue, and one bond's order book (0.7.12). */
    static final String[] BOND_PAGES   = {"Every issue", "A bond's book"};
    /** ...and their icons on the chips. */
    static final String[] BOND_ICONS   = {Icons.REPORTS, Icons.PAPER};
    /** The city's fund as a brokerage (0.7.39; "Holdings" and "By hand" from 0.7.14): FundScreen's four pages. */
    static final String[] FUND_PAGES   = FundScreen.PAGES;
    /** ...and their icons on the chips. */
    static final String[] FUND_ICONS   = FundScreen.ICONS;

    /** An area's pages, by its name; the position's for a name it does not know. */
    static String[] pagesOf(String area) {
        if (area == null) return new String[0];
        return switch (area) {
            case "The book"        -> BOOK_PAGES;
            case "Borrow"          -> BORROW_PAGES;
            case "Money"           -> MONEY_PAGES;
            case "The bond market" -> BOND_PAGES;
            case "The city's fund" -> FUND_PAGES;
            default                -> POSITION_PAGES;
        };
    }

    /** ...and their icons on the chips. */
    static String[] iconsOf(String area) {
        if (area == null) return new String[0];
        return switch (area) {
            case "Borrow"          -> BORROW_ICONS;
            case "The bond market" -> BOND_ICONS;
            case "The city's fund" -> FUND_ICONS;
            case "The book", "Money" -> new String[] {null};
            default                -> POSITION_ICONS;
        };
    }

    /**
     * One kind of paper the city can sell.
     *
     * The numbers were scattered across six button handlers - three domestic
     * and three foreign, each passing the same terms and rounding as
     * literals. They are one table now, so a change to what a kind is
     * happens once.
     *
     * THE TERMS ARE A LIST (CD2): a bill is sold at 1, 3, 6 or 12 months, a
     * bond at 2, 5, 10, 20, 30 or 50 years, an amortizing bond at 5 to 30 in
     * fives. They were a min, a max and a step until the lineup stopped being
     * evenly spaced (Jerus, 0.7.1: "i think we should only be able to issue
     * 10y 20y 30y 40y and 50y"); the lists are the model's own
     * (Auction.BILL_MONTHS, LongTermBond.MATURITIES,
     * Auction.AMORTIZING_YEARS). The KEYS stay - "Note", "Serial", "Term" are
     * Debt.getType()'s and the menus' names - while the words say what each
     * is now: bills, amortizing bonds, bonds.
     *
     * ...AND ITS TILE (0.7.32): the range it is issued over and one line, the
     * blurb behind the tile's (i).
     */
    record Instrument(String key, String name, String short_,
                              int[] terms, double rounding,
                              String unit, String blurb, String colour, String range, String line) { }

    /**
     * The five kinds of paper the city sells, in the board's tile order:
     * bills, bonds, the amortizing bond (CD2), then the floating-rate note
     * and the inflation-linked bond (CD5, spec-city-debt.md 2.4 and 2.5).
     * The ladder's order is KIND_NAMES' and Palette.LADDER's, and it draws a
     * floater and a linker as bonds (DebtManager.ladderKind()); their tiles
     * are cousins of the bonds' blue - the ramp's fourth step and the accent
     * fill, neither on the ladder - and tell the ghost of "this issue" from
     * the bonds already on it. The amortizing bond, the floater and the
     * linker are home paper only (homeOnly(): there are none in dollars,
     * spec 2.7).
     */
    static final Instrument[] INSTRUMENTS = {
        new Instrument("Note", "Bills", "BILL", ShortTermTBill.TERMS, 1000, "months",
                "Sold at a discount to the face and repaid at the face: the buyer's return is the difference, so a "
                + "bill pays no coupon, costs nothing monthly and everything at once. Sold at auction at 1, 3, 6 or "
                + "12 months, each sale its own line, never reopened.",
                Palette.LADDER[0], "1–12 months", "At a discount: no coupon."),
        new Instrument("Term", "Bonds", "BOND", LongTermBond.MATURITIES, 100000, "years",
                "A coupon every month on the whole face and the whole face at the end. A new line's coupon is the "
                + "auction's yield rounded down to an eighth of a point, so it sells just under 100; a sale at a term "
                + "whose line still has " + pc(Auction.BENCHMARK_LIFE) + " of its life left reopens that line, at its "
                + "coupon and its date, at today's price. Sold at 2, 5, 10, 20, 30 or 50 years.",
                Palette.LADDER[2], "2–50 years", "The market's coupon, near par; lines reopen."),
        new Instrument("Serial", "Amortizing", "AMORTIZING", MediumTermBond.YEARS, 10000, "years",
                "The face is repaid in equal yearly slices and one coupon, the auction's yield itself, is paid "
                + "monthly on what is left, so the debt and the coupon shrink together and nothing falls due in a "
                + "lump. Sold at 5 to 30 years in fives, each sale its own line, never reopened.",
                Palette.LADDER[1], "5–30 years", "Repaid in yearly slices, on one coupon."),
        // CD5: the floating-rate note and the inflation-linked bond. The ladder draws both as bonds, so their tiles
        // are cousins of the bonds' blue (not Palette.LADDER's, which stays three) and differ from every other tile.
        new Instrument("Float", "Floating", "FLOATING", FloatingRateNote.MATURITIES, 100000, "years",
                "A coupon every month on the whole face and the whole face at the end, as a bond's - but the coupon "
                + "is the policy rate of the month plus a margin the sale fixes, never under nothing. The margin is "
                + "what prices the note at 100 on today's curve, plus the tail the auction cleared at, so a note "
                + "sells near 100 whatever the dial does; and the city carries the dial's risk: a rise in the dial "
                + "costs it more every month, a fall costs it less. Sold at 2 or 5 years, each sale its own line, "
                + "never reopened, at home only.",
                Palette.REVENUE_RAMP[3], "2 or 5 years", "The dial each month, plus a fixed margin."),
        new Instrument("Linked", "Linked", "LINKED", InflationLinkedBond.MATURITIES, 100000, "years",
                "A real coupon every month on a principal that follows the city's price index, and at the end the "
                + "indexed principal - never less than the face: if prices end lower than they began, the city still "
                + "repays what it borrowed. The real coupon is what prices the bond at 100 on the inflation the "
                + "market expects, plus the tail the auction cleared at, rounded down to an eighth of a point; and "
                + "the city carries the inflation. What the index adds to the principal is booked as an expense "
                + "every month, and paid with the last coupon. Sold at 10 or 30 years, each sale its own line, "
                + "never reopened, at home only.",
                Palette.ACCENT_FILL, "10 or 30 years", "A real coupon; the principal follows prices."),
    };

    /* ----- the floating-rate note and the inflation-linked bond on the screens (CD5, spec-city-debt.md 2.4, 2.5, 5.4 and 7) -----
       Two more tiles on Borrow after the amortizing bond (home only, as it is), their term columns in the margin
       that prices a floater at 100 and the real coupon that prices a linker (the model's fairMargin(),
       fairRealCoupon() and breakevenInflation()), the auction card's and the quote card's coupon words (settlesAt(),
       monthlyWords(), totalWords()), the receipt's coupon line, a piece's name (pieceName(); its card is a
       line's page since CD6), the hub's bar of principal by kind (DebtManager.getPrincipalOfLadderKind(): the ladder draws
       both as bonds), and Debt service's line for the uplift. The Government tab carries the rest (its spending
       list, its bridge, its debt card). The coupon field of a floater's quote is the MARGIN and of a linker's the
       real coupon: never couponRate(), which backs out the whole coupon at today's dial and index. */

    /**
     * Whether an instrument is home paper only (spec-city-debt.md 2.7): the amortizing bond, the floating-rate
     * note and the inflation-linked bond have no dollar twin, so abroad their tiles are not drawn and a page that
     * was on one is sent back to a bond.
     */
    static boolean homeOnly(Instrument kit) {
        return "Serial".equals(kit.key()) || "Float".equals(kit.key()) || "Linked".equals(kit.key());
    }

    /** How many of the kinds are on offer abroad: the ones that are not home paper only. */
    static int abroadKinds() {
        int n = 0;
        for (Instrument i : INSTRUMENTS) if (!homeOnly(i)) n++;
        return n;
    }

    /** A floater's margin over the policy rate in words (CD5): "policy + 18 bp", "policy − 15 bp", "policy flat" under half a basis point either way. */
    static String marginWords(double margin) {
        if (Math.round(margin * 10_000) == 0) return "policy flat";
        return "policy " + (margin < 0 ? "− " : "+ ") + bp(Math.abs(margin));
    }

    /** A floater's coupon laid out as it is made (CD5): "3.00% + 0.18%", the dial it follows and the margin over it; "3.00% − 0.15%" for a margin below nothing. */
    static String dialPlusMargin(double dial, double margin) {
        return pct2(dial) + (margin < 0 ? " − " : " + ") + pct2(Math.abs(margin));
    }

    /** The one of an instrument's terms nearest this. */
    static int nearestTerm(Instrument kit, int term) {
        int best = kit.terms()[0];
        for (int t : kit.terms()) if (Math.abs(t - term) < Math.abs(best - term)) best = t;
        return best;
    }

    /** An instrument by its key; the bond for a key it does not know. */
    static Instrument instrument(String key) {
        for (Instrument i : INSTRUMENTS) if (i.key().equals(key)) return i;
        return INSTRUMENTS[1];
    }

    /** The auction's name for an instrument's kind of paper. */
    static Auction.Kind auctionKind(Instrument kit) {
        return switch (kit.key()) {
            case "Note"   -> Auction.Kind.BILL;
            case "Serial" -> Auction.Kind.AMORTIZING;
            case "Float"  -> Auction.Kind.FLOATING;
            case "Linked" -> Auction.Kind.LINKED;
            default       -> Auction.Kind.BOND;
        };
    }

    /** An instrument's term as the months the auction counts it in: a bill's months, a bond's years times twelve. */
    static int termMonths(Instrument kit, int term) {
        return "Note".equals(kit.key()) ? term : term * 12;
    }

    /** A kind's colour on the ladder, by Debt.getType(): DebtManager.LADDER_KINDS in Palette.LADDER's three. */
    static String instrumentColour(String type) {
        return Palette.LADDER[DebtManager.ladderKind(type)];
    }

    /** The ladder's three kinds, in DebtManager.LADDER_KINDS' order, as the key and a tooltip say them. */
    static final String[] KIND_NAMES = {"Bills", "Amortizing bonds", "Bonds"};

    /** A piece's kind as its tag says it, by Debt.getType(): the keys are the model's, the words are the board's. */
    static String kindTag(String type) {
        return switch (type) {
            case "NOTE"   -> "BILL";
            case "SERIAL" -> "AMORTIZING";
            case "FLOAT"  -> "FLOATING";
            case "LINKED" -> "LINKED";
            default       -> "BOND";
        };
    }

    /* what the borrow page is currently asking for */
    String borrowType = "Term";
    int borrowTerm = 10;
    double borrowAsk = 0;
    boolean borrowHold = false;
    /**
     * Whether the bond is asked callable (CD4, spec-city-debt.md 2.6): off by
     * default, kept through the clock's redraw like borrowAsk, and in force
     * only where it can be - a home bond of ten years or more
     * (callableAsked()).
     */
    boolean borrowCallable = false;
    /** What a callable quote says of its call (CD4), struck once a redraw by borrowPage(): "from Jan 2216 · the call adds 34 bp to the coupon"; null for paper that cannot be called. */
    private String callWords;
    /**
     * The highest yield the sale will take (CD2, spec-city-debt.md 4.1): fair plus a point
     * by default, a yield the player names, or Fill it. Kept through the clock's
     * redraw like borrowAsk; the quote runs at it and the button books at it.
     */
    Auction.Cap borrowCap = Auction.Cap.DEFAULT;
    /** The Borrow page's cap chips and their yield box (CapChips), and the rollover card's own: each keeps its typing through a month's redraw. */
    private final CapChips borrowChips;
    private final CapChips rolloverChips;
    /** What is typed in the ask's box and not yet set (0.7.40), kept through a month's redraw; null while the box is empty. */
    private String askTyped;
    /** ...the ask it was typed over: a step, a preset or an issue that moves the ask drops it. */
    private double askTypedOver;
    /** What the last entry that could not be read says, under the box until the ask is next set; null for none. */
    private String askRefused;
    /** The ask's box, and whether it had the focus when the page was last redrawn - a month landing rebuilds it under the typing (FundScreen's search box). */
    private javafx.scene.control.TextField askField;
    private boolean askTyping;

    /** The piece the Close it now page is on (CD4), by identity: the page is open while it is on the books. */
    private Debt closing;
    /** What is typed in the Close it now page's two boxes and not yet drawn on (CD4), kept through a month's redraw: the face to buy back, and the most to pay per 100. Empty reads as everything on offer, and no limit. */
    private String closeFaceText = "", closeMaxText = "";
    /** The two boxes, and which had the focus when the page was last redrawn (0 neither, 1 the face, 2 the most to pay) - a month landing rebuilds them under the typing. */
    private javafx.scene.control.TextField closeFaceField, closeMaxField;
    private int closeTyping;

    /**
     * What is standing open - a "details" fold, by key. Kept on the screen
     * rather than on the fold, because the panel is rebuilt under the player
     * every month the clock ticks (GovernmentScreen's, 0.7.31).
     */
    private final java.util.Set<String> open = new java.util.HashSet<>();

    /** Where the page is to be scrolled to once it is drawn: LADDER or RESCUE on the hub, a piece of paper or a calendar year on The book; null for the top. */
    private Object scrollTarget;

    /** The nodes a door on this tab can scroll to, by the same keys, as the page draws them. */
    private final java.util.Map<Object, Node> targets = new java.util.HashMap<>();

    /** The page's scroller. */
    private javafx.scene.control.ScrollPane body;

    /** The hub's scroll targets: the ladder card and the rescue card. */
    static final String LADDER = "#ladder", RESCUE = "#rescue";

    /** How much of the stage the fixed frame takes above the page's scroller - the head, the five figures with THE RATE's change, the pages, and their gaps - until the frame is laid out and its own height is read (0.7.32; Government's 232). */
    static final double FRAME_CHROME = 232;

    /** What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - GovernmentScreen's, for the same frame. */
    static final double STAGE_REST = 36;

    /**
     * The tab opened on an area and a page (0.7.32, the spec's D17): the hub
     * when `area` is null, an area's first page when `page` is not one of
     * its own, and with `anchor` - LADDER or RESCUE on the hub, a piece of
     * paper (a Debt) or a calendar year (an Integer) on The book - scrolled
     * into view. NEEDS YOU's rows, the strip's doors, the ladder's columns
     * and NEXT DUE's rows come in here, so a door lands where it says rather
     * than on whatever page was last open.
     */
    void open(String area, String page, Object anchor) {
        financeArea = area;
        financePage = page == null ? FINANCE_HOME : page;
        scrollTarget = anchor;
        fundScreen.security = null;
        ui.innerScrollAt.remove("showFinanceMenu:body");
        showFinanceMenu();
    }

    void showFinanceMenu() {
        // Redrawn where it stands - a month landing, a click on it - rather than arrived at: only then does the fund's worth count on (0.7.39).
        fundScreen.here = ui.isShowing("showFinanceMenu");
        // ...and whether the player was typing an ask, so the new box takes the focus back (0.7.40).
        askTyping = askField != null && askField.isFocused();
        // ...and in the Close it now page's boxes (CD4).
        closeTyping = closeFaceField != null && closeFaceField.isFocused() ? 1
                : closeMaxField != null && closeMaxField.isFocused() ? 2 : 0;
        closeFaceField = null;
        closeMaxField = null;
        borrowChips.noteFocus();
        rolloverChips.noteFocus();
        ui.clearMenu("showFinanceMenu", () -> showFinanceMenu());

        boolean known = financeArea == null;
        for (String a : AREAS) if (a.equals(financeArea)) known = true;
        if (!known) financeArea = null;
        // The fund's security page is left by any door but its own (0.7.39); an older door's page name is its new one's.
        if (FundScreen.AREA.equals(financeArea)) {
            String now = FundScreen.pageFor(financePage);
            if (now != null && !now.equals(financePage)) {
                financePage = now;
                fundScreen.security = null;
            }
        } else {
            fundScreen.security = null;
        }
        String[] pages = pagesOf(financeArea);
        boolean onPage = false;
        for (String p : pages) if (p.equals(financePage)) onPage = true;
        // Close it now is no chip's (CD4), nor is a line's page (CD6): each stays open while its piece is on the books, and falls back to The book's page when it is gone.
        if (closeOpen() || lineOpenNow()) onPage = true;
        if (!onPage && pages.length > 0) financePage = pages[0];

        List<CityNeeds.Need> all = CityNeeds.measure(ui.game, SummaryScreen.WORDS);
        targets.clear();

        VBox page = widePage();
        if (financeArea == null) {
            hubPage(page, all);
        } else {
            switch (financeArea) {
                case "The book" -> {
                    if (closeOpen())        closePage(page);
                    else if (lineOpenNow()) linePage(page);
                    else                    bookPage(page);
                }
                case "Borrow"   -> borrowPage(page, "Abroad".equals(financePage));
                case "Money"    -> moneyPage(page);
                case "The bond market" -> {
                    if ("A bond's book".equals(financePage)) bondBookPage(page);
                    else                                     bondMarketPage(page);
                }
                case "The city's fund" -> fundScreen.draw(page, financePage);
                default -> {
                    switch (financePage) {
                        case "Debt service"  -> debtServicePage(page);
                        case "Home & abroad" -> homeAndAbroadPage(page);
                        case "Your rate"     -> yourRatePage(page);
                        default              -> positionPage(page, all);
                    }
                }
            }
        }

        VBox frame = new VBox(Palette.GAP, head(), vitals(all));
        frame.getChildren().addAll(alertBands());
        if (pages.length > 1) {
            String area = financeArea;
            // A security's page is none of the area's chips (0.7.39), so none is lit on it.
            frame.getChildren().add(chipStrip(pages, iconsOf(area), fundScreen.security != null ? null : financePage,
                    Palette.SIZE_LABEL, name -> open(area, name, null)));
        }
        frameOver(frame, page);

        if (scrollTarget != null) {
            Object target = scrollTarget;
            scrollTarget = null;
            javafx.application.Platform.runLater(() -> {
                Node n = targets.get(target);
                // A calendar year with no piece maturing in it: the next that has one.
                if (n == null && target instanceof Integer y) {
                    for (int later = y + 1; n == null && later <= y + 60; later++) n = targets.get(later);
                }
                if (n == null || body == null) return;
                body.applyCss();
                body.layout();
                scrollTo(n);
            });
        }
    }

    /** The fixed frame over a scrolling page, at the page's width, the page as tall as what is left under the frame as laid out (GovernmentScreen's, 0.7.31). */
    private void frameOver(VBox frame, VBox page) {
        frame.setMaxWidth(PAGE_WIDE);
        frame.setFillWidth(true);
        frame.setPadding(new javafx.geometry.Insets(0, 18, 4, 18));
        body = ui.scrolled(page, FRAME_CHROME);
        body.prefHeightProperty().unbind();
        body.prefHeightProperty().bind(javafx.beans.binding.Bindings.createDoubleBinding(
                () -> Math.max(260, ui.menuScroller.getHeight()
                        - (frame.getHeight() > 0 ? frame.getHeight() + STAGE_REST : FRAME_CHROME)),
                ui.menuScroller.heightProperty(), frame.heightProperty()));
        final javafx.scene.control.ScrollPane scroller = body;
        page.prefWidthProperty().bind(javafx.beans.binding.Bindings.createDoubleBinding(
                () -> Math.min(PAGE_WIDE, Math.max(320, scroller.getViewportBounds().getWidth())),
                scroller.viewportBoundsProperty()));
        ui.rootMenu.getChildren().addAll(frame, body);
    }

    /** Scroll the page to a node on it (InfrastructureScreen's). */
    void scrollTo(Node target) {
        if (body == null || target == null || body.getContent() == null) return;
        double contentH = body.getContent().getBoundsInLocal().getHeight();
        double viewH = body.getViewportBounds().getHeight();
        if (contentH <= viewH) return;
        javafx.geometry.Bounds at = target.localToScene(target.getBoundsInLocal());
        javafx.geometry.Bounds top = body.getContent().localToScene(body.getContent().getBoundsInLocal());
        if (at == null || top == null) return;
        double y = at.getMinY() - top.getMinY() - 12;
        body.setVvalue(Math.max(0, Math.min(1, y / (contentH - viewH))));
    }

    /** On the hub, scroll to `target` where it is; anywhere else, the hub opened on it. */
    void showOnHub(String target) {
        if (financeArea == null && targets.get(target) != null) scrollTo(targets.get(target));
        else open(null, null, target);
    }

    /** The tab's (i): what the hub is, in a breath. */
    static final String HEAD_INFO = "What the city owes, when it falls due, and what it does when it does. "
            + "The ladder is every payment the city's paper still asks - coupons and principal - a column a "
            + "calendar year; NEXT DUE is the next five pieces. Under them, the two settings the treasury runs "
            + "by itself, and the six areas, each a card that opens.";

    /** Each area's (i), in AREAS' order: the landing's blurbs until 0.7.32 (the spec's T2), with what the card shows. */
    static final String[] AREA_INFO = {
        "What the city holds, what it owes, and why the rate is the rate: the balance and the credit band, what "
                + "the paper costs against the take, who holds it, and the rate taken apart.",
        "Every line of the city's paper on one desk - its coupon, the yield it was sold at and today's, what it is "
                + "worth and what it is carried at, what closing it would book - and a page for each line, with Close it now on it.",
        "Bills, bonds, amortizing bonds, floating-rate notes and inflation-linked bonds sold at auction at home, "
                + "bills and bonds in dollars abroad - the auction run on the city as it stands on every click, and "
                + "the ladder it would build.",
        "The central bank's books, and how much money the city has.",
        "The businesses' bonds: every issue, who holds it, and its order book. Not the city's paper.",
        "Its shares and bonds, the bank's rescue, the dial, and the share of its worth it pays the treasury "
                + "every month.",
    };

    /** An area's (i) by its name. */
    static String areaInfo(String area) {
        for (int i = 0; i < AREAS.length; i++) if (AREAS[i].equals(area)) return AREA_INFO[i];
        return HEAD_INFO;
    }

    /**
     * The head: "Finances" with the money blue's swatch; on an area the
     * breadcrumb "Finances › The book", the first word a way back. At its
     * right the debt and the rate over the years, in City History, and on an
     * area "‹ Finances"; on the fund's security page, FundScreen's (0.7.39).
     */
    HBox head() {
        Label history = door("Over the years", Palette.ACCENT, () -> ui.historyScreen.openOn("debt", "interestRate"));
        if (financeArea == null) {
            return pageHead("Finances", Palette.MONEY, null, null, HEAD_INFO, history);
        }
        if (FundScreen.AREA.equals(financeArea) && fundScreen.security != null) return fundScreen.head(history);
        return pageHead("Finances", Palette.MONEY, () -> open(null, null, null), financeArea, areaInfo(financeArea),
                history, backDoor("Finances", () -> open(null, null, null)));
    }

    /** A door back: "‹ Finances", in the accent, underlined under the pointer (Pieces.door()'s, pointing the other way). */
    static Label backDoor(String text, Runnable go) {
        Label l = new Label("‹ " + text);
        String rest = Palette.words(Palette.SIZE_LABEL, Palette.ACCENT) + " -fx-cursor: hand;";
        l.setStyle(rest);
        l.setMinWidth(Region.USE_PREF_SIZE);
        l.setOnMouseEntered(e -> l.setStyle(rest + " -fx-underline: true;"));
        l.setOnMouseExited(e -> l.setStyle(rest));
        l.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, e -> {
            e.consume();
            go.run();
        });
        return l;
    }

    /* ---------------------------------------------------------------------
       THE FIVE FIGURES (0.7.32; four until then)

       TREASURY · OWED · THE RATE › · COUPON · NEXT DUE ›, one verdict each
       (the spec's D7): TREASURY in NEEDS YOU's TREASURY colour - nothing on
       a city in hand, amber under a month's tax, red overdrawn; OWED neutral,
       red only when NEEDS YOU's BORROWING is (the market has priced the
       city out); THE RATE red at the ceiling; COUPON by the service bands
       (CityNeeds.SERVICE_FELT, SERVICE_CONSTRAINED), nothing while
       comfortable; NEXT DUE red inside FALLS DUE's three months and amber
       inside FALLS_DUE_SOON_MONTHS. SERVICE was the coupon alone under a
       name that meant coupon and principal (the spec's B3): it is COUPON,
       the same read under its own name.
       --------------------------------------------------------------------- */

    /** One of the five figures, worked out without drawing it: its label, its figure, its note, its colour, where its click goes, and the red line under it (null: none). */
    record Kpi(String label, String value, String note, String tone, String where, String alarm) { }

    /** The five figures (pure: the probe reads them as the strip shows them). */
    List<Kpi> kpis(List<CityNeeds.Need> all) {
        Game g = ui.game;
        DebtManager ledger = g.getDebtManager();
        NationalAccounts na = g.getEconomyManager().getNationalAccounts();
        double cash = g.getCash();
        double principal = ledger.getAllPrincipal();
        double annual = annualGdp(na);
        double revenue = na.getTotalRevenue();
        double coupon = ledger.getMonthlyCoupon();
        double notes = ledger.getNotePrincipal();
        double overdraft = ledger.getOverdraft();
        double advanced = g.getCentralBank().getAdvancesToTreasury();

        int treasury = needLevel(all, CityNeeds.Kind.TREASURY);
        String alarm = overdraft > 0 || advanced > 0
                ? (overdraft > 0 ? d(overdraft) + " overdrawn" : "")
                  + (overdraft > 0 && advanced > 0 ? " · " : "")
                  + (advanced > 0 ? d(advanced) + " advanced" : "")
                : null;

        String owedNote = principal < .5 ? "nothing owed"
                : annual > 0 ? String.format("%.0f%% %s", principal / annual * 100, ofAnnualGdp(na))
                : "no output to compare";

        int band = revenue > 0 ? CityNeeds.serviceLevel(coupon / revenue) : 0;
        // The coupon is the cash the paper pays NEXT month, not the interest the budget charged last month (CD6): the
        // note says so, and the tooltip puts the budget's line beside it.
        String couponNote = "in cash next month · " + (revenue > 0
                ? String.format("%.1f%% of a month's take", coupon / revenue * 100) : "nothing came in")
                + (notes > 0 ? " · " + d(notes) + " more pays at maturity" : "");

        Debt soonest = soonest();
        String dueValue, dueNote, dueTone;
        if (soonest == null) {
            dueValue = "nothing";
            dueNote = "no city debt";
            dueTone = Palette.TEXT_SPENT;
        } else {
            int month = g.getMonth();
            int due = soonest.getMaturityMonth();
            dueValue = d(soonest.getOustandingPrincipal());
            dueNote = CityCalendar.formatShort(due) + " · " + CityCalendar.until(month, due)
                    + (g.getRolloverMode() == Rollover.Mode.MANUAL ? " · by hand" : " · the rollover's");
            dueTone = urgency(due - month);
        }

        return List.of(
                new Kpi("TREASURY", d(cash),
                        g.hasTreasuryMonth() ? "banked " + signedD(g.getTreasuryChange()) + " last month"
                                             : "no month closed yet",
                        treasury >= 2 ? Palette.BAD : treasury == 1 ? Palette.WARN : Palette.TEXT_HEAD,
                        "What the cash did: Government's walk from EARNED to BANKED", null),
                new Kpi("OWED", d(principal), owedNote,
                        needLevel(all, CityNeeds.Kind.BORROWING) >= 2 ? Palette.BAD : Palette.TEXT_HEAD,
                        "The book: every piece of paper, and what each would cost to retire", alarm),
                new Kpi("THE RATE", pct2(g.getInterestRate()),
                        "rated " + g.getCreditRating() + " · floor " + pct2(ledger.floorRate()),
                        ledger.atCeiling() ? Palette.BAD : Palette.TEXT_HEAD,
                        "Go to the policy rate, which this one is built on", null),
                new Kpi("COUPON", d(coupon), couponNote,
                        band >= 2 ? Palette.BAD : band == 1 ? Palette.WARN : Palette.TEXT_HEAD,
                        "Debt service: what the paper costs against what the city takes in. The coupon is the cash the paper pays "
                                + "next month; the budget charged " + d(na.getInterestExpense()) + " of interest last month, "
                                + "the coupons and the discount wearing off.", null),
                new Kpi("NEXT DUE", dueValue, dueNote, dueTone,
                        "When it falls due: the ladder", null));
    }

    HBox vitals(List<CityNeeds.Need> all) {
        List<Kpi> k = kpis(all);
        Runnable[] go = {
                () -> ui.governmentScreen.open("Overview", GovernmentScreen.BRIDGE),
                () -> open("The book", null, null),
                () -> openPolicy("Money", "The policy rate"),
                () -> open("The position", "Debt service", null),
                () -> showOnHub(LADDER)};
        VBox[] cells = new VBox[k.size()];
        for (int i = 0; i < cells.length; i++) {
            Kpi c = k.get(i);
            cells[i] = limitCell(c.label(), c.value(), c.note(), c.tone(), c.where(), go[i]);
            if (c.alarm() != null) {
                Label red = new Label(c.alarm());
                red.setWrapText(true);
                red.setMaxWidth(LIMIT_CELL - 28);
                red.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.BAD));
                cells[i].getChildren().add(red);
            }
        }
        withSpark(cells[0], "cash", null);
        withSpark(cells[1], "debt", null);
        withSpark(cells[2], "interestRate", rateChange());
        return vitalsBar(cells);
    }

    /** A limit cell with a year of a History series as a sparkline at the right of its figure, and a line under the note when `change` is not null (GovernmentScreen.withSpark()'s shape). */
    void withSpark(VBox cell, String series, String change) {
        HistorySave history = ui.game.getHistorySave();
        double[] all = history.aligned(series);
        List<Integer> months = history.getMonth();
        int from = Math.max(0, all.length - 12);
        double[] year = java.util.Arrays.copyOfRange(all, from, all.length);
        List<Integer> when = months.size() == all.length ? months.subList(from, all.length) : null;
        Node figure = cell.getChildren().get(1);
        cell.getChildren().remove(1);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox top = new HBox(6, figure, gap, sparkline(year, when, Palette.MONEY, 52, 16));
        top.setAlignment(Pos.CENTER_LEFT);
        cell.getChildren().add(1, top);
        if (change != null) {
            Label moved = new Label(change);
            moved.setWrapText(true);
            moved.setMaxWidth(LIMIT_CELL - 28);
            moved.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            cell.getChildren().add(moved);
        }
    }

    /** THE RATE's move on last month, from History's last two points: "▲ 0.01 pts on last month"; null with fewer than two or under half a hundredth of a point (the spec's section 5). */
    String rateChange() {
        double[] s = ui.game.getHistorySave().aligned("interestRate");
        double last = Double.NaN, before = Double.NaN;
        for (int i = s.length - 1; i >= 0; i--) {
            if (!Double.isFinite(s[i])) continue;
            if (Double.isNaN(last)) last = s[i];
            else { before = s[i]; break; }
        }
        if (!Double.isFinite(last) || !Double.isFinite(before)) return null;
        double points = HistoryScreen.plotScale("percent", last) - HistoryScreen.plotScale("percent", before);
        if (Math.abs(points) < .005) return null;
        return (points > 0 ? "▲ " : "▼ ") + String.format("%.2f pts on last month", Math.abs(points));
    }

    /** The piece that falls due soonest, or null with nothing owed. */
    Debt soonest() {
        Debt first = null;
        for (Debt d : ui.game.getDebtManager().getDebt()) {
            if (first == null || d.getMaturityMonth() < first.getMaturityMonth()) first = d;
        }
        return first;
    }

    /** A maturity's colour, by how many months off it is: red inside FALLS DUE's line (NEEDS YOU's), amber inside FALLS_DUE_SOON_MONTHS, the headings' ink after. */
    static String urgency(int monthsOff) {
        return monthsOff <= CityNeeds.FALLS_DUE_MONTHS ? Palette.BAD
                : monthsOff <= CityNeeds.FALLS_DUE_SOON_MONTHS ? Palette.WARN : Palette.TEXT_HEAD;
    }

    /** NEEDS YOU's level for a kind: 0 green, 1 amber, 2 red, -1 not measured. */
    static int needLevel(List<CityNeeds.Need> all, CityNeeds.Kind kind) {
        for (CityNeeds.Need n : all) if (n.kind() == kind) return n.level();
        return -1;
    }

    /* ---------------------------------------------------------------------
       THE ALERT BANDS (0.7.32): the hub's standing warnings, as one red line
       each under the strip on every page, the paragraph behind an (i) and a
       door where there is one - overdrawn, the rate pinned, a default still
       charged for abroad, the bank failed and waiting for the city.
       --------------------------------------------------------------------- */

    List<Node> alertBands() {
        Game g = ui.game;
        DebtManager ledger = g.getDebtManager();
        List<Node> out = new ArrayList<>();
        if (ledger.getOverdraft() > 0) {
            out.add(alertBand("Overdrawn by " + d(ledger.getOverdraft()) + ": the central bank advances it next month",
                    String.format("%s of it. At the top of next month the central bank advances it, at the policy "
                            + "rate, in money it makes. Past %.0f months of revenue owed, only the city's promises are "
                            + "still paid that way; the rest waits for cash, and what waits is owed as arrears. The "
                            + "market prices it as principal meanwhile - it is already in the rate.",
                            d(ledger.getOverdraft()), g.getCentralBank().getAdvancesCeilingMonths()),
                    null));
        }
        if (ledger.atCeiling()) {
            out.add(alertBand("The rate is pinned at the top of the curve",
                    "Both measures the market prices on - debt against output and debt against what the city can "
                            + "collect - have said everything they have to say. Borrowing more will not make it dearer, "
                            + "because it cannot get dearer. That is not good news.",
                    door("Your rate", Palette.BAD_SOFT, () -> open("The position", "Your rate", null))));
        }
        if (ledger.getDefaultScar() > 0) {
            out.add(alertBand(String.format("A default abroad is still charged for: %.1f points on what the world lends at",
                            ledger.getDefaultScar() * 100),
                    String.format("%.1f points on the city's premium abroad, %d months after it happened, fading over "
                            + "about five years from when it happened. The world charges it on every dollar the city "
                            + "borrows; the city's own paper at home does not carry it.",
                            ledger.getDefaultScar() * 100, Math.max(0, ledger.getMonthsSinceForeignDefault())),
                    door("Abroad", Palette.BAD_SOFT, () -> open("Borrow", "Abroad", null))));
        }
        if (g.canResolveBank()) {
            out.add(alertBand("The bank has failed and waits for the city: resolving it costs "
                            + d(g.bankRecapitalisationNeeded()),
                    RESOLVE_INFO,
                    door("Resolve", Palette.BAD_SOFT, () -> showOnHub(RESCUE))));
        }
        return out;
    }

    /** What resolving a failed bank does (the spec's T41). */
    static final String RESOLVE_INFO = "Resolving it costs the hole and the capital to reopen it. Its owners lose "
            + "everything and every share becomes the city's - the fund's rescue book. The treasury's cash pays "
            + "first, and what it lacks the central bank advances.";

    /** One alert band: a red ground, the alert icon, one line, its (i), and a door at the right (null: none). */
    static HBox alertBand(String line, String whole, Node door) {
        Label says = new Label(line);
        says.setWrapText(true);
        says.setMinWidth(0);
        says.setStyle(Palette.words(Palette.SIZE_BODY, Palette.BAD_TEXT));
        HBox row = new HBox(Palette.GAP, icon(Icons.ALERT, Palette.BAD, 14), says, infoButton(whole, true));
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        row.getChildren().add(gap);
        if (door != null) row.getChildren().add(door);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMinHeight(36);
        row.setMaxWidth(Double.MAX_VALUE);
        row.setStyle("-fx-padding: 6 12 6 12; -fx-background-color: " + Palette.ALERT_GROUND + ";"
                + " -fx-border-color: " + Palette.ALERT_EDGE + "; -fx-background-radius: 6; -fx-border-radius: 6;");
        return row;
    }

    /* ----------------------------- the screen's own pieces ----------------------------- */

    /** The city's money's mark where a foreign figure is on the tab: "D$" (the spec's D9). */
    String sym() { return ui.game.getCurrency().qualifiedSymbol(); }

    /** Local money with its mark and a true minus: "D$2.4M", "−D$725k" (the spec's D9, B9). */
    String d(double thousands) {
        String shown = money(Math.abs(thousands));
        return (thousands < 0 && !"$0".equals(shown) ? "−" : "") + marked(sym(), shown);
    }

    /** ...with every digit. */
    String dFull(double thousands) {
        String shown = moneyFull(Math.abs(thousands));
        return (thousands < 0 && !"$0".equals(shown) ? "−" : "") + marked(sym(), shown);
    }

    /** ...signed, as a movement: "+D$1.2M", "−D$3.5M", "D$0". */
    String signedD(double thousands) {
        String shown = money(Math.abs(thousands));
        if ("$0".equals(shown)) return marked(sym(), shown);
        return (thousands < 0 ? "−" : "+") + marked(sym(), shown);
    }

    /** An exchange rate with its unit, as the land office writes it: "D$0.7035 per US$" (the spec's B10). */
    String perUsd(double rate) { return sym() + fxRate(rate) + " per " + Currency.FOREIGN_SYMBOL; }

    /** A share as a whole per cent: "58%". */
    static String pc(double share) { return String.format("%.0f%%", share * 100).replace('-', '−'); }

    /** ...to one place: "1.5%". */
    static String pc1(double share) { return String.format("%.1f%%", share * 100).replace('-', '−'); }

    /** Points of a rate, signed: "+0.02 pts". */
    static String pts(double rate) { return String.format("%+.2f pts", rate * 100).replace('-', '−'); }

    /** A yield to three places, as an auction stops: "3.602%". */
    static String pct3(double rate) { return String.format("%.3f%%", rate * 100).replace('-', '−'); }

    /** A tail in basis points: "28 bp". */
    static String bp(double rate) { return String.format("%.0f bp", rate * 10_000).replace('-', '−'); }

    /** Words that wrap, at a size, in a colour. */
    static Label words(String text, double size, String tone) {
        Label l = new Label(text);
        l.setWrapText(true);
        l.setMinWidth(0);
        l.setStyle(BuildScreen.wordsAt(size, tone));
        return l;
    }

    /** A figure that is never cut, at a size, in a colour. */
    static Label figure(String text, double size, String tone) {
        Label l = new Label(text);
        l.setMinWidth(Region.USE_PREF_SIZE);
        l.setStyle(BuildScreen.figureAt(size, tone));
        return l;
    }

    /** A picture's caption: a few words in capitals, with an (i) after them when `info` is not null. */
    static HBox caption(String text, String info) {
        Label l = new Label(text);
        l.setStyle(Palette.strong(Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
        l.setMinWidth(Region.USE_PREF_SIZE);
        HBox row = new HBox(Palette.GAP, l);
        if (info != null) row.getChildren().add(infoButton(info, true));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** ...with words at its right. */
    static HBox caption(String text, String info, Node right) {
        HBox row = caption(text, info);
        if (right != null) {
            Region gap = new Region();
            HBox.setHgrow(gap, Priority.ALWAYS);
            row.getChildren().addAll(gap, right);
        }
        return row;
    }

    /** A card on Build's ground: RAISED, radius 8, a 1 px edge, padding 14. */
    static VBox card(Node... rows) {
        VBox c = new VBox(8);
        for (Node n : rows) if (n != null) c.getChildren().add(n);
        c.setMaxWidth(Double.MAX_VALUE);
        c.setStyle(cardStyle(Palette.EDGE));
        return c;
    }

    /** The card's ground, with its edge in a colour - red when the card is in alarm. */
    static String cardStyle(String edge) {
        return "-fx-padding: 14; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + edge + ";";
    }

    /** A line of a card: words at the left, wrapping, and a figure at the right. */
    static HBox cardLine(String label, String value, String tone) {
        Label what = words(label, Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL);
        HBox.setHgrow(what, Priority.ALWAYS);
        what.setMaxWidth(Double.MAX_VALUE);
        Label v = figure(value, Palette.SIZE_LABEL + 1, tone == null ? Palette.TEXT_HEAD : tone);
        HBox row = new HBox(Palette.GAP, what, v);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** ...with an (i) after its words. */
    static HBox cardLine(String label, String value, String tone, String info) {
        HBox row = cardLine(label, value, tone);
        if (info != null) row.getChildren().add(1, infoButton(info, true));
        return row;
    }

    /** A muted line with its whole behind an (i), wrapping at `wide`. */
    static HBox noteLine(String shown, String whole, double wide) {
        return infoLine(shown, whole, true, Palette.SIZE_LABEL, Palette.TEXT_MUTED, wide);
    }

    /** A plain line of words in a card, wrapping. */
    static Label line(String text) { return words(text, Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL); }

    /** ...muted. */
    static Label muted(String text) { return words(text, Palette.SIZE_LABEL, Palette.TEXT_MUTED); }

    /** A statement column, for a fold: the old rows at their old width (the spec's D10). */
    static VBox column(Node... rows) {
        VBox c = new VBox(0);
        for (Node n : rows) if (n != null) c.getChildren().add(n);
        c.setMaxWidth(STATEMENT);
        return c;
    }

    /** A fold kept on this screen: "details ▸ caption". */
    VBox fold(String key, String caption, java.util.function.Supplier<Node> inside) {
        return details(key, caption, open, this::showFinanceMenu, inside);
    }

    /** Two nodes side by side in equal columns, each as tall as the taller. */
    static GridPane pair(Node left, Node right) {
        GridPane two = equalColumns(2, TILE_GAP);
        if (left instanceof Region r) GridPane.setFillHeight(r, true);
        if (right instanceof Region r) GridPane.setFillHeight(r, true);
        two.add(left, 0, 0);
        if (right != null) two.add(right, 1, 0);
        return two;
    }

    /** A key's entry: a swatch and a name, the swatch outlined when `ghost`. */
    static HBox swatch(String colour, String name, boolean ghost) {
        HBox s = keySwatch(colour, name);
        if (ghost && !s.getChildren().isEmpty() && s.getChildren().get(0) instanceof Region r) {
            r.setStyle("-fx-background-color: " + colour + "33; -fx-border-color: " + colour + "; -fx-border-width: 1;");
        }
        return s;
    }

    /** A stretch of a segment bar with its tooltip. */
    static Segment seg(double amount, String colour, String tip) {
        return new Segment(amount, colour, false, null, null, tip, null);
    }

    /** ...hollow: what would be there, or what is not. */
    static Segment ghost(double amount, String colour, String label, String tip) {
        return new Segment(amount, colour, true, null, label, tip, null);
    }

    /** A row of a key: entries with a gap, wrapping. */
    static javafx.scene.layout.FlowPane keyRow(Node... entries) {
        javafx.scene.layout.FlowPane key = new javafx.scene.layout.FlowPane(14, 4);
        for (Node n : entries) if (n != null) key.getChildren().add(n);
        return key;
    }

    /** Policy, on one of its areas and pages, at its top (GovernmentScreen.openPolicy()'s). */
    void openPolicy(String area, String page) {
        ui.policyScreen.policyArea = area;
        ui.policyScreen.policyPage = page;
        ui.policyScreen.dropProposal();
        ui.innerScrollAt.remove("showPolicyMenu:body");
        ui.policyScreen.showPolicyMenu();
    }

    /**
     * Trade, on the page an area names: "The currency" and "The reserves"
     * were Trade's areas until 0.7.35 and are its pages now - TradeScreen
     * reads tradeArea as the page, so `page` is overwritten.
     */
    void openTrade(String area, String page) {
        ui.tradeScreen.tradeArea = area;
        ui.tradeScreen.tradePage = page;
        ui.innerScrollAt.remove("showForeignMenu:body");
        ui.tradeScreen.showForeignMenu();
    }

    /* =====================================================================
       THE HUB (0.7.32)

       The question: what does the city owe, when does it fall due, and what
       happens when it does? WHEN IT FALLS DUE leads - the ladder, a column a
       calendar year stacked by instrument, the dollar part striped, "later"
       a ghost, with NEXT DUE beside it (the 0.7.24 card, folded in: the five
       maturities, "+N more", and the totals, which are the strip's OWED and
       COUPON now). Under it the two settings the treasury runs by itself,
       each said once, here (the spec's D3: they stood on both Borrow pages
       and the fund's): rolling what falls due, with next month's bar, and
       what happens when the bank fails. Then the six areas as one row of
       cards, a figure and a thin bar each.
       ===================================================================== */

    void hubPage(VBox page, List<CityNeeds.Need> all) {
        VBox ladder = ladderCard();
        targets.put(LADDER, ladder);
        page.getChildren().add(ladder);
        VBox rescue = rescueCard();
        targets.put(RESCUE, rescue);
        page.getChildren().add(pair(rolloverCard(), rescue));
        page.getChildren().add(sectionHead("FINANCES, OPENED", hint("tap one")));
        page.getChildren().add(areaCards());
    }

    /* ------------------------------ when it falls due ------------------------------ */

    /** The ladder's (i): the spec's T12, and why a column is a calendar year. */
    static final String LADDER_INFO = "Every payment the city's paper still asks, coupons and principal together, "
            + "because what a treasury has to find in a given year is the sum of both. A tall column is a year "
            + "the city has to have the money, or refinance before it arrives. Each column is a calendar year - "
            + "the year NEXT DUE dates a piece in - and the first starts next month. \"later\" is every year "
            + "after the twelfth, together: a ghost, because it is many years summed, never one to be weighed "
            + "against the rest. When it is more than twice the tallest year it is drawn broken, a little above "
            + "that year, so the twelve keep the height to be read; its figure is still the whole of it. A "
            + "striped edge is the part owed in dollars. Click a column for the paper that falls due in it.";

    /** The ladder with nothing on it (the spec's T11). */
    static final String NOTHING_DUE = "The city owes nothing, so nothing falls due. The Borrow page is where that "
            + "changes.";

    /** A wall on the ladder (the spec's T13), in the heaviest year's tooltip. */
    static final String WALL_WORDS = "more than half a year of revenue - a city meets a wall like that by "
            + "refinancing it before it arrives, not on the morning, and refinancing is dearest exactly when "
            + "everybody can see the wall";

    /** How many times the tallest year "later" may be before it is drawn broken (0.7.34): past it, on one scale, the twelve would be slivers. */
    static final double LATER_BREAK = 2;

    /** ...and how tall a broken "later" stands, in tallest years: a little above the tallest, so it still reads as the most. */
    static final double LATER_CAP = 1.2;

    VBox ladderCard() {
        Game g = ui.game;
        DebtManager ledger = g.getDebtManager();
        DebtManager.Ladder ladder = ledger.ladder(g.getMonth());
        VBox left = new VBox(8, caption("WHEN IT FALLS DUE", LADDER_INFO, hint("coupons and principal, a column a year")));
        if (ledger.getDebt().isEmpty()) {
            left.getChildren().add(words(NOTHING_DUE, Palette.SIZE_BODY + 1, Palette.TEXT_MUTED));
        } else {
            left.getChildren().add(ladderChart(ladder, null, null, 220));
            left.getChildren().add(ladderKey(ladder, null, null));
        }
        left.setMinWidth(0);
        left.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox inner = new HBox(20, left, nextDueCard());
        return card(inner);
    }

    /**
     * The ladder as columns (Pieces.columns()): a column a calendar year,
     * each kind's payments stacked from the foot - notes, serial bonds, term
     * loans, Palette.LADDER's three - the part owed in dollars striped down
     * its edge, and a proposed issue's payments as a ghost on top in
     * `extraColour` (null: none). "later" is a ghost column on the same
     * scale, or broken (below). A figure stands over the first year, the heaviest and every
     * year at a quarter of the heaviest or more (the 0.7.x ladder's rule:
     * the shape answers the question, the rest are on hover); the heaviest
     * of the twelve is tagged, red when it passes CityNeeds.YEAR_WALL of a
     * year of revenue. A click opens The book at the first piece that falls
     * due in that year.
     *
     * "LATER" BROKEN (0.7.34): more than LATER_BREAK times the tallest year,
     * it was drawn on the same scale and the twelve were slivers (Jerus's
     * city: D$721.4B after twelve years of D$2.2B to D$47.9B). Past that it
     * is a broken column LATER_CAP times the tallest year high, the scale
     * set to it, so the years fill the height; its figure is its whole.
     */
    Pieces.Columns ladderChart(DebtManager.Ladder ladder, String extraColour, String extraName, double height) {
        double revenueYear = ui.game.getEconomyManager().getNationalAccounts().getTotalRevenue() * 12;
        double peak = tallestYear(ladder);
        boolean broken = laterBroken(ladder);
        double scale = ladderScale(ladder);
        List<Column> cols = new ArrayList<>();
        for (int y = 0; y < ladder.years().size(); y++) {
            DebtManager.Rung r = ladder.years().get(y);
            boolean heaviest = y == ladder.heaviest() && r.owed() > 0;
            boolean say = r.total() >= .5 && (y == 0 || heaviest || r.total() >= peak * .25);
            boolean wall = heaviest && revenueYear > 0 && r.owed() > revenueYear * CityNeeds.YEAR_WALL;
            String sub = y == 0 && r.fromMonth() >= 0 && CityCalendar.monthOfYear(r.fromMonth()) != 1
                    ? CityCalendar.shortMonthName(r.fromMonth()) + "–" + CityCalendar.shortMonthName(r.toMonth()) : null;
            int year = r.fromYear();
            cols.add(new Column(String.valueOf(year), sub, say ? d(r.total()) : null, rungParts(r, extraColour), false,
                    heaviest ? "heaviest" : null, wall ? Palette.BAD : Palette.TEXT_LABEL,
                    rungTip(r, revenueYear, extraName) + (wall ? ": " + WALL_WORDS : ""),
                    () -> open("The book", null, year)));
        }
        if (ladder.later() != null) {
            DebtManager.Rung r = ladder.later();
            int year = r.fromYear();
            cols.add(new Column("later", null, d(r.total()), rungParts(r, extraColour), true, null, null,
                    rungTip(r, 0, extraName), () -> open("The book", null, year), broken));
        }
        return columns(cols, scale, 0, height);
    }

    /** The tallest of the twelve years' columns, a proposed issue's ghost included. */
    static double tallestYear(DebtManager.Ladder ladder) {
        double peak = 0;
        for (DebtManager.Rung r : ladder.years()) peak = Math.max(peak, r.total());
        return peak;
    }

    /** Whether "later" is drawn broken (0.7.34): more than LATER_BREAK times the tallest year. */
    static boolean laterBroken(DebtManager.Ladder ladder) {
        double peak = tallestYear(ladder);
        return ladder.later() != null && peak > 0 && ladder.later().total() > peak * LATER_BREAK;
    }

    /** The ladder's scale: the tallest column - or, "later" broken, LATER_CAP tallest years, so the years fill the height. */
    static double ladderScale(DebtManager.Ladder ladder) {
        double peak = tallestYear(ladder);
        if (ladder.later() == null) return peak;
        return laterBroken(ladder) ? peak * LATER_CAP : Math.max(peak, ladder.later().total());
    }

    /** A rung's segments, foot first: each kind at home, then its dollar part striped; then a proposed issue's, as a ghost. */
    List<Segment> rungParts(DebtManager.Rung r, String extraColour) {
        List<Segment> parts = new ArrayList<>();
        for (int k = 0; k < 3; k++) {
            double abroad = r.abroadByKind()[k];
            double home = r.byKind()[k] - abroad;
            if (home > 0) parts.add(Segment.of(home, Palette.LADDER[k]));
            if (abroad > 0) parts.add(new Segment(abroad, Palette.LADDER[k], false, Palette.BUSINESS, null, null, null));
        }
        if (r.proposed() > 0 && extraColour != null) parts.add(ghost(r.proposed(), extraColour, null, null));
        return parts;
    }

    /** A column's tooltip: "2210 · D$579.0M: bonds D$579.0M, D$563.3M of it in dollars · 30% of a year's take" (the spec's section 3). */
    String rungTip(DebtManager.Rung r, double revenueYear, String extraName) {
        StringBuilder s = new StringBuilder(r.fromYear() == r.toYear() ? String.valueOf(r.fromYear())
                : r.fromYear() + "–" + r.toYear());
        s.append(" · ").append(d(r.owed()));
        boolean first = true;
        for (int k = 0; k < 3; k++) {
            if (r.byKind()[k] < .5) continue;
            s.append(first ? ": " : ", ").append(KIND_NAMES[k].toLowerCase()).append(' ').append(d(r.byKind()[k]));
            if (r.abroadByKind()[k] >= .5) s.append(", ").append(d(r.abroadByKind()[k])).append(" of it in dollars");
            first = false;
        }
        if (first) s.append(": nothing owed falls in it");
        if (revenueYear > 0 && r.owed() >= .5) s.append(" · ").append(pc(r.owed() / revenueYear)).append(" of a year's take");
        if (extraName != null && r.proposed() >= .5) s.append(" · ").append(extraName).append(' ').append(d(r.proposed()));
        return s.toString();
    }

    /** The ladder's key: only the kinds drawn, "in dollars" when any is, the proposed issue's ghost; at the right, everything still to pay. */
    HBox ladderKey(DebtManager.Ladder ladder, String extraColour, String extraName) {
        double[] any = new double[3];
        double abroad = 0;
        List<DebtManager.Rung> rungs = new ArrayList<>(ladder.years());
        if (ladder.later() != null) rungs.add(ladder.later());
        for (DebtManager.Rung r : rungs) {
            for (int k = 0; k < 3; k++) any[k] += r.byKind()[k];
            abroad += r.abroad();
        }
        javafx.scene.layout.FlowPane key = keyRow();
        for (int k = 0; k < 3; k++) if (any[k] > 0) key.getChildren().add(keySwatch(Palette.LADDER[k], KIND_NAMES[k]));
        if (abroad > 0) key.getChildren().add(keySwatch(Palette.BUSINESS, "the edge: in dollars"));
        if (extraColour != null && ladder.proposed() > 0) key.getChildren().add(swatch(extraColour, extraName, true));
        HBox.setHgrow(key, Priority.ALWAYS);
        key.setMaxWidth(Double.MAX_VALUE);
        Label total = words("everything still to pay " + d(ladder.owed())
                + (ladder.proposed() > 0 ? " · with it " + d(ladder.total()) : ""),
                Palette.SIZE_LABEL, Palette.TEXT_LABEL);
        total.setMinWidth(Region.USE_PREF_SIZE);
        HBox row = new HBox(Palette.GAP, key, total);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /* ------------------------------ next due ------------------------------ */

    /**
     * NEXT DUE: the next five pieces to fall due, beside the ladder (0.7.32;
     * a card at the top of the hub from 0.7.24, the window's foot until
     * then). Each its amount - red inside FALLS DUE's three months, NEEDS
     * YOU's row, amber inside FALLS_DUE_SOON_MONTHS - its kind and its date;
     * a click opens the piece on The book. "+N more" past five. What fell
     * due at the last press stands at its top for the month, "rolled" or
     * "paid ✓" (the spec's section 5).
     *
     * CITY DEBT ONLY. The business sectors borrow too, and their paper is
     * their own problem - it is serviced out of sector cash and the player
     * cannot pay it off. Mixing the two here would put numbers in front of
     * the player that they have no control over, next to numbers they very
     * much do.
     */
    VBox nextDueCard() {
        Game g = ui.game;
        int month = g.getMonth();
        List<Debt> debts = new ArrayList<>(g.getDebtManager().getDebt());
        debts.sort(java.util.Comparator.comparingInt(Debt::getMaturityMonth));

        // In the money area's colour since 0.7.21, as the mockups' status bar has it.
        Label heading = new Label("NEXT DUE");
        heading.setStyle(Palette.strong(Palette.SIZE_LABEL + 1, Palette.MONEY));
        VBox box = new VBox(6, heading);
        String fell = fellDueWords();
        if (fell != null) box.getChildren().add(chip(fell, Palette.TEXT_LABEL));
        if (debts.isEmpty()) {
            box.getChildren().add(words("No city debt outstanding.", Palette.SIZE_LABEL + 1, Palette.TEXT_MUTED));
            box.getChildren().add(door("Borrow", Palette.ACCENT, () -> open("Borrow", "At home", null)));
        } else {
            for (int i = 0; i < Math.min(5, debts.size()); i++) box.getChildren().add(nextDueRow(debts.get(i), month));
            if (debts.size() > 5) {
                box.getChildren().add(door("+" + (debts.size() - 5) + " more", Palette.ACCENT, () -> open("The book", null, null)));
            }
        }
        box.setMinWidth(214);
        box.setPrefWidth(214);
        box.setMaxWidth(214);
        return box;
    }

    /** One piece on NEXT DUE: its amount in its urgency, its kind's tag and its date; a click opens the line's page (CD6). */
    HBox nextDueRow(Debt debt, int month) {
        int due = debt.getMaturityMonth();
        Label amount = figure(d(debt.getOustandingPrincipal()), 13, urgency(due - month));
        Label kind = tag((debt.isForeign() ? "US$ " : "") + kindTag(debt.getType()), instrumentColour(debt.getType()));
        Label when = words(CityCalendar.formatShort(due) + " · " + CityCalendar.until(month, due),
                Palette.SIZE_CAPTION + 1, Palette.TEXT_LABEL);
        HBox under = new HBox(6, kind, when);
        under.setAlignment(Pos.CENTER_LEFT);
        VBox both = new VBox(1, amount, under);
        HBox row = new HBox(both);
        row.setMinHeight(36);
        row.setAlignment(Pos.CENTER_LEFT);
        String rest = "-fx-padding: 2 4 2 4; -fx-background-radius: 4; -fx-cursor: hand;";
        row.setStyle(rest);
        row.setOnMouseEntered(e -> row.setStyle(rest + " -fx-background-color: " + Palette.CONTROL + ";"));
        row.setOnMouseExited(e -> row.setStyle(rest));
        row.setOnMouseClicked(e -> openLine(debt));
        return row;
    }

    /** What fell due at the last press, for the month after it (the spec's section 5): "D$272.0M fell due · rolled", off the rollover's record or, by hand, the principal the treasury repaid; null when nothing did. */
    String fellDueWords() {
        Game g = ui.game;
        Rollover r = g.getRollover();
        if (r.getLastMonth() == g.getMonth() - 1 && r.getLastDue() >= .5) {
            return d(r.getLastDue()) + " fell due · " + (r.getLastRaised() > 0 ? "rolled" : "paid ✓");
        }
        if (g.hasTreasuryMonth() && g.getTreasuryRepaid() >= .5) {
            return d(g.getTreasuryRepaid()) + " fell due · paid ✓";
        }
        return null;
    }

    /* ------------------------------ the two settings ------------------------------ */

    /* ---------------------- ROLLING WHAT FALLS DUE (0.7.13) ----------------------
     *
     * Jerus: "you should have an option, where it defualt toggles on, so
     * basically the game checks whats going to mature next month, and issues
     * what the treasury is lacking ... either manual, aka you do it yourself,
     * or that it defualts to same structure, or that it defualts to 12 month
     * tbill". On the hub since 0.7.32 (both borrow pages until then): the
     * three chips, the chosen one's line, and NEXT MONTH - what falls due as
     * one bar, the part last year's surplus nets, the central bank's own
     * roll, each issue as a ghost with its paper, and what the cash pays -
     * Game.rolloverPlan(), the function the press books, so the bar here is
     * the issue there. Since CD2 (slice 9, D28 and D29) the card carries the
     * rollover's own cap under its chips - the same three chips as Borrow
     * (CapChips), on Game.getRolloverCap() - and a line on what a sale that
     * falls short at the cap does: the rest of the maturity is paid from the
     * cash, then the central bank's advances.
     */
    /** The rollover's three settings as chips, in Rollover.Mode's order. */
    static final String[] ROLLOVER_CHIPS = { "By hand", "Same structure", Rollover.BILL_MONTHS + "-month bills" };

    /** Each setting's one line (the spec's T35). */
    static final String[] ROLLOVER_LINES = {
        "Nothing automatic: what falls due is paid from the cash, and what it can't cover the central bank advances.",
        "A month ahead, what falls due is sold again as the same paper, once last year's surplus has paid its part.",
        "A month ahead, what falls due is sold again as " + Rollover.BILL_MONTHS + "-month bills at home, once last "
                + "year's surplus has paid its part.",
    };

    /** ...and each in full, on its chip (the old block's sentences). */
    static final String[] ROLLOVER_TIPS = {
        "Nothing automatic: what falls due is paid out of the treasury's cash, and what the cash cannot cover the "
                + "central bank advances.",
        "Each month the treasury looks at what falls due the next, nets last year's surplus from it, and issues the "
                + "rest a month ahead as the same paper - the same instrument, term and currency, dollars abroad in "
                + "dollars - at home instead while the window abroad is shut.",
        "Each month the treasury looks at what falls due the next, nets last year's surplus from it, and issues the "
                + "rest a month ahead as " + Rollover.BILL_MONTHS + "-month bills at home.",
    };

    /** The rollover's (i): the three settings, the central bank's own roll (T36) and the new paper's size (T39). */
    static final String ROLLOVER_INFO = "By hand, nothing is automatic: what falls due is paid out of the treasury's "
            + "cash, and what the cash cannot cover the central bank advances. The two automatic settings look at what "
            + "falls due next month, net last year's surplus from it, and issue the rest a month ahead - as the same "
            + "paper (instrument, term and currency, dollars abroad in dollars, at home while the window abroad is "
            + "shut), or as " + Rollover.BILL_MONTHS + "-month bills at home.\n\n"
            + "The central bank replaces what it holds of the paper falling due with the same par of the new paper, "
            + "at the price the market pays, on top of what is sold - so the issue is sized for the rest, and with "
            + "nothing to sell the market its par is issued it alone. What it pays under par comes back as its "
            + "remittance. Last year's surplus pays the market's part first and then the central bank's, so a surplus "
            + "the size of what falls due pays it all. While it holds more than its dial, what it holds past the dial "
            + "is repaid it instead, and raised with the rest. By hand, it rolls what it holds at issue, beside what "
            + "you sell; sell none and its holding is repaid it out of the treasury's cash.\n\n"
            + "The new paper is sized so its cash covers what falls due. It sells under its face - a bill by its "
            + "discount, a bond a little under 100, either by the issue's costs - so its face is more than the part of "
            + "the maturity it refinances: rolling for cash borrows the old paper's interest into the new principal, "
            + "at every roll. "
            + "The proceeds wait a month for the maturity - what pre-funding a redemption costs. A treasury short of "
            + "cash for its spending is still the central bank's to advance.";

    /** The rollover's cap block: its caption, and its (i), which says what the cap is and what a short sale does (D28, D29). */
    static final String ROLLOVER_CAP_TITLE = "THE HIGHEST YIELD IT WILL TAKE";
    static final String ROLLOVER_CAP_INFO = "The most the rollover will pay to sell what it refinances: fair plus a "
            + "point (the default, each piece's own fair), a yield you name (one yield for every piece, a short bill "
            + "and a long bond alike), or Fill it, which names none. Each piece sells at one yield, the lowest at which "
            + "the bids cover it, and everyone is filled at it, so the limit only costs anything if the bids need it.\n\n"
            + "A maturity is a promise and is paid in full. If the bids do not cover a sale at your limit it sells only "
            + "what was bid, and the rest of what falls due is paid from the treasury's cash and, past the cash, from "
            + "the central bank's advances: that is printing, and the inbox says how much. Fill it lets the yield rise "
            + "until the bidders' own rules cover the sale; the inbox names the tail when it passes fair plus a point.";

    /** The line under the rollover's chips: what the cap is for, and what a short sale does (D29); by hand, that nothing is sold for you. Pure. */
    static String rolloverCapWords(Rollover.Mode mode) {
        String short_ = "A sale that falls short at the cap pays the rest of what falls due from the cash, then the "
                + "central bank's advances.";
        return mode == Rollover.Mode.MANUAL
                ? "By hand nothing is sold for you; this is the cap the automatic settings sell at. " + short_
                : "Every piece the rollover sells is held to this. " + short_;
    }

    /** What the rollover's "at …" chip starts from: the 10-year bond's fair yield today, since the rollover sells many papers and has no one fair. */
    double rolloverFair() {
        return ui.game.getDebtManager().fairYield(Auction.Kind.BOND, 120, 0);
    }

    VBox rolloverCard() {
        Game g = ui.game;
        Rollover.Mode mode = g.getRolloverMode();
        Rollover.Plan plan = g.rolloverPlan();
        VBox card = card(setting("ROLLING WHAT FALLS DUE", ROLLOVER_INFO, ROLLOVER_CHIPS,
                ROLLOVER_CHIPS[mode.ordinal()], ROLLOVER_LINES, ROLLOVER_TIPS, picked -> {
                    for (int i = 0; i < ROLLOVER_CHIPS.length; i++) {
                        if (ROLLOVER_CHIPS[i].equals(picked)) g.setRolloverMode(Rollover.Mode.values()[i]);
                    }
                    showFinanceMenu();
                }));
        // THE CAP (CD2, D28): the same three chips as Borrow, on the rollover's own cap, and what a short sale does (D29).
        card.getChildren().addAll(rolloverChips.controls(ROLLOVER_CAP_TITLE, ROLLOVER_CAP_INFO, rolloverFair(), false));
        card.getChildren().add(muted(rolloverCapWords(mode)));
        if (!(plan.due() > 0)) {
            card.getChildren().add(line("Next month: nothing falls due."));
        } else {
            List<Segment> parts = new ArrayList<>();
            if (plan.netted() > 0) parts.add(seg(plan.netted(), Palette.MONEY,
                    "netted from last year's surplus " + d(plan.netted())));
            if (plan.centralBankPar() > 0) parts.add(seg(plan.centralBankPar(), Palette.MONEY_LIGHT,
                    "the central bank rolls its own at issue " + d(plan.centralBankPar())));
            for (Rollover.Issue issue : plan.issues()) {
                parts.add(ghost(issue.cash(), Palette.MONEY, issue.paper().replaceFirst("^a ", ""),
                        d(issue.cash()) + " raised as " + issue.paper() + ", " + issue.why(plan.mode())
                                + " · its face, as quoted today, " + d(issue.face())));
            }
            if (plan.fromCash() > 0) parts.add(seg(plan.fromCash(), Palette.MONEY_DARK,
                    "paid from the treasury's cash " + d(plan.fromCash())));
            card.getChildren().add(segmentBar(parts, plan.due(), null, 0, 12));
            card.getChildren().add(noteLine(planWords(plan), surplusWords(plan), 560));
        }
        String last = lastRollWords();
        if (last != null) card.getChildren().add(muted(last));
        return card;
    }

    /** NEXT MONTH in one line (the spec's T37): what falls due and where each part of it goes - the bar's parts in words, so the bar needs no key. */
    String planWords(Rollover.Plan plan) {
        StringBuilder s = new StringBuilder("Next month " + d(plan.due()) + " falls due");
        if (plan.dueAbroad() > 0) s.append(" (").append(d(plan.dueAbroad())).append(" of it abroad)");
        if (plan.mode() == Rollover.Mode.MANUAL) {
            s.append(": by hand, the treasury's cash pays ").append(d(plan.fromCash()));
            if (plan.centralBankPar() > 0) s.append(", and the central bank rolls its own ").append(d(plan.centralBankPar()));
            return s.append('.').toString();
        }
        s.append(": last year's surplus nets ").append(d(plan.netted()));
        if (plan.centralBankPar() > 0) s.append(", the central bank rolls its own ").append(d(plan.centralBankPar()));
        if (plan.issues().isEmpty()) {
            s.append(plan.fromCash() > 0 ? ", and " + d(plan.fromCash()) + " - under the smallest deal worth "
                    + "arranging - is paid from the cash" : ", and nothing is raised");
        } else {
            s.append(", and ").append(d(plan.toRaise())).append(" is raised as ");
            for (int i = 0; i < plan.issues().size(); i++) {
                Rollover.Issue issue = plan.issues().get(i);
                s.append(i == 0 ? "" : i == plan.issues().size() - 1 ? " and " : ", ").append(issue.paper());
                if (plan.issues().size() > 1) s.append(" (").append(d(issue.cash())).append(')');
            }
            s.append(", about ").append(d(plan.issued())).append(" of new face");
            if (plan.fromCash() > 0) s.append("; the cash pays ").append(d(plan.fromCash()));
        }
        return s.append('.').toString();
    }

    /** NEXT MONTH's (i): the year's surplus and what is used of it, and the central bank's part (the old block's lines). */
    String surplusWords(Rollover.Plan plan) {
        StringBuilder s = new StringBuilder(String.format("Last year's surplus: %s. Of it netted already this year, "
                + "or kept for the fund: %s.", d(plan.surplus()), d(plan.used())));
        if (ui.game.fundReservation() > 0) {
            s.append(" Kept for the fund's pay-in at the year end: ").append(d(ui.game.fundReservation())).append('.');
        }
        if (plan.centralBankDue() > 0) {
            s.append(String.format(" Of what falls due the central bank holds %s: last year's surplus pays off %s of "
                    + "it, it rolls %s itself at issue, and %s is repaid it as more than its dial.",
                    d(plan.centralBankDue()), d(plan.centralBankNetted()), d(plan.centralBankPar()),
                    d(plan.centralBankRunsOff())));
        }
        return s.toString();
    }

    /**
     * The last rollover, in a muted line (the spec's T38), or null when none
     * has run: dated by the month what it rolled fell due in - the month
     * after the press it ran at, Rollover.getLastMonth().
     */
    String lastRollWords() {
        Rollover r = ui.game.getRollover();
        if (r.getLastMonth() < 0) return null;
        return String.format("Last, falling due in %s: %s, %s netted from the surplus, %s.",
                CityCalendar.format(r.getLastMonth() + 1), d(r.getLastDue()), d(r.getLastNetted()),
                r.getLastIssued() >= .5 ? d(r.getLastIssued()) + " of new paper raised " + d(r.getLastRaised())
                        : "nothing re-borrowed");
    }

    /* ---------------------- WHEN THE BANK FAILS (0.7.14) ----------------------
     *
     * Jerus: "Treasury setting, auto". On the hub since 0.7.32 (both borrow
     * pages and the fund's until then): the two chips, the chosen one's
     * line, the card red with the button while the bank waits frozen, and
     * the last resolution. The rescue is Game.resolveBank() whichever way it
     * is triggered.
     */
    /** The rescue's two settings as chips, in TreasuryFund.RescueMode's order. */
    static final String[] RESCUE_CHIPS = { "Automatic", "Wait for my button" };

    /** Each setting's one line (the spec's T40). */
    static final String[] RESCUE_LINES = {
        "The month it fails, the city resolves it and it reopens.",
        "It stays frozen until you press Resolve, here or on the Bank tab.",
    };

    /** The rescue's (i): both settings in full (the old block's T40). */
    static final String RESCUE_INFO = "Automatic: the month the bank fails, the city resolves it - its owners lose "
            + "everything, the city pays the hole and the capital to reopen, from the treasury's cash first and what "
            + "that lacks the central bank advances, and every share goes to the fund's rescue book. The bank reopens "
            + "that month.\n\nWait for my button: a failed bank stays frozen, carrying its hole and lending nothing "
            + "new, until you press the button here or on the Bank tab. The resolution is then the same as the "
            + "automatic one.";

    VBox rescueCard() {
        Game g = ui.game;
        TreasuryFund.RescueMode mode = g.getRescueMode();
        VBox card = card(setting("WHEN THE BANK FAILS", RESCUE_INFO, RESCUE_CHIPS, RESCUE_CHIPS[mode.ordinal()],
                RESCUE_LINES, RESCUE_LINES, picked -> {
                    for (int i = 0; i < RESCUE_CHIPS.length; i++) {
                        if (RESCUE_CHIPS[i].equals(picked)) g.setRescueMode(TreasuryFund.RescueMode.values()[i]);
                    }
                    showFinanceMenu();
                }));
        if (g.canResolveBank()) {
            card.setStyle(cardStyle(Palette.BAD));
            double cost = g.bankRecapitalisationNeeded();
            card.getChildren().add(infoLine("The bank has failed and waits for the city: resolving it costs " + d(cost)
                    + ".", RESOLVE_INFO, true, Palette.SIZE_BODY, Palette.BAD_TEXT, 560));
            Button resolve = new Button("Resolve the bank – " + d(cost));
            resolve.setStyle("-fx-background-color: " + Palette.CONFIRM + "; -fx-text-fill: white;"
                    + " -fx-padding: 6 16 6 16;");
            resolve.setOnAction(e -> {
                g.resolveBank();
                showFinanceMenu();
            });
            card.getChildren().add(resolve);
        }
        TreasuryFund.Resolution last = g.getLastResolution();
        if (last != null) {
            Label foot = muted(String.format("Last: %s - the city put in %s.", CityCalendar.format(last.month()),
                    d(last.paid())));
            Tooltip whole = new Tooltip(String.format("The last, in %s: the city put in %s, %s from the treasury's cash "
                            + "and %s advanced by the central bank; the old owners lost %s at the last price.",
                    CityCalendar.format(last.month()), d(last.paid()), d(last.fromCash()), d(last.advanced()),
                    d(last.ownersLost())));
            whole.setWrapText(true);
            whole.setMaxWidth(POPOVER_WIDTH);
            whole.setShowDelay(Duration.millis(250));
            foot.setTooltip(whole);
            card.getChildren().add(foot);
        }
        return card;
    }

    /** The rollover and the rescue, as Borrow and the fund's Rules & cash (its Holdings until 0.7.39) point at them (the spec's D3): "What falls due rolls as the same structure; a failed bank is resolved automatically." */
    String settingsWords() {
        Game g = ui.game;
        String roll = switch (g.getRolloverMode()) {
            case MANUAL -> "What falls due is paid by hand";
            case SAME_STRUCTURE -> "What falls due rolls as the same structure";
            case TWELVE_MONTH_BILL -> "What falls due rolls into " + Rollover.BILL_MONTHS + "-month bills";
        };
        String rescue = g.getRescueMode() == TreasuryFund.RescueMode.AUTOMATIC
                ? "a failed bank is resolved automatically" : "a failed bank waits for your button";
        return roll + "; " + rescue + ".";
    }

    /** ...as a line with its door to the hub's two cards. */
    HBox settingsPointer() {
        Label says = words(settingsWords(), Palette.SIZE_LABEL + 1, Palette.TEXT_MUTED);
        HBox row = new HBox(Palette.GAP, says, door("Change on Finances", Palette.ACCENT, () -> showOnHub(RESCUE)));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /* ------------------------------ the six areas ------------------------------ */

    /** One area's card, worked out without drawing it: its figure, its line and the line's colour, and an amber chip (null: none). */
    record AreaCard(String area, String figure, String line, String lineTone, String chip) { }

    /** The six areas' figures (pure: the probe reads them as the cards show them). */
    List<AreaCard> areaWords() {
        Game g = ui.game;
        DebtManager ledger = g.getDebtManager();
        NationalAccounts na = g.getEconomyManager().getNationalAccounts();
        double principal = ledger.getAllPrincipal();
        double annual = annualGdp(na);
        int pieces = ledger.getDebt().size();
        double coupon = ledger.getMonthlyCoupon();
        CentralBank cb = g.getCentralBank();
        BondMarket market = g.getBondMarket();
        double bondFace = market.totalFace();
        double business = g.getEconomyManager().getBusinessDebtManager().getEverythingOwed();
        TreasuryFund fund = g.getFund();
        double fundValue = g.fundValue();
        return List.of(
                new AreaCard("The position", pct2(g.getInterestRate()),
                        "rated " + g.getCreditRating() + " · " + (principal < .5 ? "nothing owed"
                                : annual > 0 ? pc(principal / annual) + " of GDP" : d(principal) + " owed"),
                        Palette.TEXT_LABEL, null),
                new AreaCard("The book", pieces == 0 ? "nothing" : pieces + (pieces == 1 ? " piece" : " pieces"),
                        coupon >= .5 ? d(coupon) + " coupon a month in cash"
                                : pieces > 0 ? "no coupon: bills pay at maturity" : "nothing outstanding",
                        Palette.TEXT_LABEL, null),
                new AreaCard("Borrow", pct2(g.getInterestRate()),
                        ledger.foreignWindowOpen() ? pct2(ledger.foreignRate()) + " abroad, the window open"
                                : "the window abroad is shut",
                        ledger.foreignWindowOpen() ? Palette.TEXT_LABEL : Palette.BAD, null),
                new AreaCard("Money", d(g.getM2()), "held by the public · " + d(cb.m0()) + " made",
                        Palette.TEXT_LABEL, cb.getAdvancesToTreasury() > 0 ? "printed " + d(cb.getAdvancesToTreasury()) : null),
                new AreaCard("The bond market", bondFace > 0 ? d(bondFace) : "no bonds",
                        bondFace > 0 ? (business > 0 ? pc(bondFace / business) + " of what businesses owe · " : "")
                                + market.getBonds().size() + (market.getBonds().size() == 1 ? " bond" : " bonds")
                                : "the businesses borrow from the bank",
                        Palette.TEXT_LABEL, null),
                new AreaCard("The city's fund", fundValue > 0 ? d(fundValue) : "empty",
                        fund.getToRaise() > 0 ? "selling " + d(fund.getToRaise()) + " to pay its withdrawal"
                                : fund.getTransferShort() > 0 ? d(fund.getTransferShort()) + " of its transfer unpaid"
                                : fund.getTransferPaid() > 0 ? "paid " + d(fund.getTransferPaid()) + " to the treasury"
                                : String.format("the dial at %.0f%% of the year's surplus", fund.getDial() * 100),
                        fund.getTransferShort() > 0 && !(fund.getToRaise() > 0) ? Palette.WARN : Palette.TEXT_LABEL, null));
    }

    GridPane areaCards() {
        List<AreaCard> words = areaWords();
        GridPane row = equalColumns(AREAS.length, TILE_GAP);
        for (int i = 0; i < AREAS.length; i++) {
            VBox c = areaCard(i, words.get(i), areaBar(i));
            GridPane.setFillHeight(c, true);
            row.add(c, i, 0);
        }
        return row;
    }

    /** Each area's thin bar: the credit band, the principal by kind, the bank's room, M2's parts, the bonds' holders, the fund's shares against its aim. */
    Node areaBar(int i) {
        Game g = ui.game;
        DebtManager ledger = g.getDebtManager();
        return switch (i) {
            case 0 -> segmentBar(List.of(Segment.of(ledger.baseComponent(), Palette.MONEY_DARK),
                            Segment.of(ledger.gdpSpread(), Palette.MONEY), Segment.of(ledger.revenueSpread(), Palette.MONEY_LIGHT)),
                    ledger.ceilingRate(), List.of(new Tick(g.getInterestRate(), Palette.TEXT_HEAD, 2, null, null)), 0, 6);
            // By the ladder's kinds (CD5): a floating-rate note's face and an inflation-linked bond's are the bonds'.
            case 1 -> segmentBar(List.of(Segment.of(ledger.getPrincipalOfLadderKind(0), Palette.LADDER[0]),
                    Segment.of(ledger.getPrincipalOfLadderKind(1), Palette.LADDER[1]),
                    Segment.of(ledger.getPrincipalOfLadderKind(2), Palette.LADDER[2])), 0, null, 0, 6);
            case 2 -> {
                Bank bank = g.getBank();
                double room = bank.capacity();
                yield segmentBar(List.of(Segment.of(room > 0 ? bank.getWeightedBook() : 0, Palette.MONEY)),
                        room > 0 ? room : 1, null, 0, 6);
            }
            case 3 -> segmentBar(List.of(Segment.of(g.getHouseholdDeposits(), Palette.PEOPLE),
                    Segment.of(g.getSectorDeposits(), Palette.BUSINESS),
                    Segment.of(g.getBank().getForeignDeposits(), Palette.ORE),
                    Segment.of(g.getCentralBank().getCurrency(), Palette.MONEY_LIGHT)), 0, null, 0, 6);
            case 4 -> {
                BondMarket m = g.getBondMarket();
                yield segmentBar(List.of(Segment.of(m.faceHeldByHouseholds(), Palette.PEOPLE),
                        Segment.of(m.faceHeldByBank(), Palette.MONEY), Segment.of(m.faceHeldByCompanies(), Palette.BUSINESS),
                        Segment.of(m.faceHeldByWorld(), Palette.ORE), Segment.of(m.faceHeldByCity(), Palette.MONEY_DARK)),
                        0, null, 0, 6);
            }
            default -> segmentBar(List.of(Segment.of(g.fundEquityShare(), Palette.BUSINESS)), 1,
                    List.of(new Tick(TreasuryFund.EQUITY_WEIGHT, Palette.TEXT_HEAD, 2, null, null)), 0, 6);
        };
    }

    /** One area as a card (the spec's section 3 A.3): its icon, name and (i), its figure, its line, its chip, its bar; hover lights its edge, a click opens it. */
    VBox areaCard(int i, AreaCard w, Node bar) {
        String area = AREAS[i];
        String tint = i == 4 ? Palette.BUSINESS : Palette.MONEY;
        Label name = new Label(area);
        name.setWrapText(true);
        name.setMinWidth(0);
        name.setStyle(Palette.strong(Palette.SIZE_HEADING, Palette.TEXT_HEAD));
        Label more = new Label("›");
        more.setStyle(Palette.words(Palette.SIZE_HEADING, Palette.TEXT_MUTED));
        more.setMinWidth(Region.USE_PREF_SIZE);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox top = new HBox(6, iconSquare(AREA_ICONS[i], tint, 24, 13), name, infoButton(AREA_INFO[i], true), gap, more);
        top.setAlignment(Pos.CENTER_LEFT);
        VBox c = new VBox(5, top, figure(w.figure(), 17, Palette.TEXT_HEAD),
                words(w.line(), Palette.SIZE_LABEL, w.lineTone()));
        if (w.chip() != null) c.getChildren().add(chip(w.chip(), Palette.WARN));
        Region push = new Region();
        VBox.setVgrow(push, Priority.ALWAYS);
        c.getChildren().addAll(push, bar);
        c.setMinHeight(104);
        c.setMaxWidth(Double.MAX_VALUE);
        c.setMaxHeight(Double.MAX_VALUE);
        String rest = "-fx-padding: 10 12 12 12; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand; -fx-border-color: ";
        c.setStyle(rest + Palette.EDGE + ";");
        c.setOnMouseEntered(e -> c.setStyle(rest + Palette.ACCENT + ";"));
        c.setOnMouseExited(e -> c.setStyle(rest + Palette.EDGE + ";"));
        c.setOnMouseClicked(e -> open(area, null, null));
        return c;
    }

    /* =====================================================================
       THE POSITION (0.7.32)

       Can the city carry what it owes, and does the market mind? Four
       pages, each one picture first: the balance and the credit band;
       the debt service gauge; who holds the paper; the rate built up and
       the curve. The ladder that was the second page is the hub's hero.

       NO VERDICT OF THE SCREEN'S OWN (the spec's D7, B4). The page judged
       debt at 60% and 120% of a year's output - "comfortable", "carryable",
       "more than a year of output, and the market is pricing it" - while
       the market priced a city at 157% of its output at 1.32%, rated AAA.
       What the market says is the rate and its two measures (Your rate);
       the sentence says how far along them the city is.
       ===================================================================== */

    void positionPage(VBox page, List<CityNeeds.Need> all) {
        page.getChildren().add(pair(balanceCard(), creditBandCard()));
        page.getChildren().add(economyCard());
        page.getChildren().add(owedChartCard());
    }

    /** THE BALANCE's (i): the spec's T7. */
    static final String BALANCE_INFO = "A negative net position is not by itself a problem - a city that borrows to "
            + "build owns the building. What decides whether the debt is carryable is when it falls due (the hub's "
            + "ladder) and whether the month's revenue covers what the month's paper costs (Debt service). The net "
            + "position is the cash - below nothing when it is overdrawn - less the paper and what the central bank "
            + "has advanced. The paper is counted at what the books carry it at: its face less the discount not yet "
            + "worn off, or plus the premium. What the market would pay for it today is a note under the figure, "
            + "not on the books.";

    /** The line under THE BALANCE's bars: three readings of the same paper. */
    static final String PAPER_VALUES_INFO = "The same paper three ways. Its face is what falls due. What the books "
            + "carry it at is the face less the discount not yet worn off, so it climbs to the face by the day it "
            + "falls due; the bar and the net position use it. What it is worth today is what the market would pay "
            + "for it now at the going rate: a note a government discloses and does not book.";

    /** THE BALANCE: the cash against what is owed, two bars on one scale, and the net position (the spec's section 3 B). */
    VBox balanceCard() {
        Game g = ui.game;
        DebtManager ledger = g.getDebtManager();
        double cash = g.getCash();
        double held = Math.max(0, cash);
        // The paper at what the books carry it at (CD3), not at its face: the face and the market's worth are
        // the line under the bars.
        double face = ledger.getAllPrincipal(), principal = ledger.getCarryingValue(), worth = ledger.getFairValue();
        double overdraft = ledger.getOverdraft();
        double advanced = g.getCentralBank().getAdvancesToTreasury();
        double scale = Math.max(held, principal + overdraft + advanced);
        VBox card = card(caption("THE BALANCE", BALANCE_INFO));
        card.getChildren().add(cardLine("In the treasury", d(cash), cash < 0 ? Palette.BAD : null));
        card.getChildren().add(segmentBar(List.of(seg(held, Palette.MONEY, "in the treasury " + dFull(cash))),
                scale > 0 ? scale : 1, null, 0, 16));
        card.getChildren().add(cardLine("Owed, as carried", d(principal), null));
        card.getChildren().add(segmentBar(List.of(seg(principal, Palette.MONEY_DARK, "owed on paper, as carried " + dFull(principal)),
                        seg(overdraft, Palette.BAD, "overdrawn " + dFull(overdraft)),
                        seg(advanced, Palette.BAD, "advanced by the central bank " + dFull(advanced))),
                scale > 0 ? scale : 1, null, 0, 16));
        if (overdraft > 0 || advanced > 0) {
            card.getChildren().add(words((overdraft > 0 ? "+ " + d(overdraft) + " overdrawn  " : "")
                    + (advanced > 0 ? "+ " + d(advanced) + " advanced by the central bank" : ""),
                    Palette.SIZE_LABEL + 1, Palette.BAD));
        }
        // ...the face, the carrying value and the market's worth in one line (spec-city-debt.md 5.4): the last
        // is a note a government discloses and does not book, so it is said not to be on the books.
        if (face > 0 || principal > 0) {
            card.getChildren().add(noteLine("at face " + d(face) + " · carried " + d(principal) + " · worth today "
                            + d(worth) + " (a note: what the market would pay; not on the books)", PAPER_VALUES_INFO, 440));
        }
        HBox net = new HBox(Palette.GAP, figure(signedD(g.getNetPositionAtCarrying()), 22, Palette.TEXT_HEAD),
                words("net", Palette.SIZE_BODY + 1, Palette.TEXT_LABEL));
        net.setAlignment(Pos.BASELINE_LEFT);
        card.getChildren().add(net);
        return card;
    }

    /** THE CREDIT BAND: the rate on the band from what a spotless city pays to what a hopeless one does, its parts the floor and the two measures (the spec's T8 cut: the band labels its ends). */
    VBox creditBandCard() {
        Game g = ui.game;
        DebtManager ledger = g.getDebtManager();
        double floor = ledger.floorRate(), rate = g.getInterestRate(), ceiling = ledger.ceilingRate();
        VBox card = card(caption("THE CREDIT BAND", null, door("taken apart", Palette.ACCENT,
                () -> open("The position", "Your rate", null))));
        card.getChildren().add(new HBox(Palette.GAP, figure(pct2(rate), 22, ledger.atCeiling() ? Palette.BAD : Palette.TEXT_HEAD),
                words("rated " + g.getCreditRating(), Palette.SIZE_BODY + 1, Palette.TEXT_LABEL)));
        card.getChildren().add(segmentBar(List.of(
                        seg(ledger.baseComponent(), Palette.MONEY_DARK, "the floor " + pct2(ledger.baseComponent())),
                        seg(ledger.gdpSpread(), Palette.MONEY, "debt against a year of output " + pts(ledger.gdpSpread())),
                        seg(ledger.revenueSpread(), Palette.MONEY_LIGHT, "debt against a year of tax " + pts(ledger.revenueSpread()))),
                ceiling, List.of(new Tick(floor, Palette.TEXT_MUTED, 2, null, "spotless " + pct2(floor)),
                        new Tick(rate, Palette.TEXT_HEAD, 3, null, "you " + pct2(rate))), 0, 14));
        Label spotless = words("spotless " + pct2(floor), Palette.SIZE_LABEL, Palette.TEXT_MUTED);
        Label you = words("you " + pct2(rate), Palette.SIZE_LABEL, Palette.TEXT_HEAD);
        Label hopeless = words("hopeless " + pct2(ceiling), Palette.SIZE_LABEL, Palette.TEXT_MUTED);
        Region a = new Region(), b = new Region();
        HBox.setHgrow(a, Priority.ALWAYS);
        HBox.setHgrow(b, Priority.ALWAYS);
        card.getChildren().add(new HBox(Palette.GAP, spotless, a, you, b, hopeless));
        card.getChildren().add(keyRow(keySwatch(Palette.MONEY_DARK, "the floor"), keySwatch(Palette.MONEY, "debt vs output"),
                keySwatch(Palette.MONEY_LIGHT, "debt vs tax")));
        return card;
    }

    /** AGAINST THE ECONOMY's (i): the spec's T9, rewritten from the market's own measure (B4). */
    String economyInfo(double ratio) {
        DebtManager ledger = ui.game.getDebtManager();
        return String.format("The city owes %s of a year of what it produces. The market prices that on its own "
                        + "scale: debt against output adds up to %.0f points to the city's rate, all of it at %.0f years "
                        + "of output, and it has used %s of that so far - Your rate takes it apart. No line here is a "
                        + "verdict of this page's own: the rate is the market's.",
                pc(ratio), DebtManager.maxSpreadPerMeasure() * 100, DebtManager.fullStressMultiple(),
                pc(ledger.gdpStress()));
    }

    /** AGAINST THE ECONOMY: the debt, a year of its coupon and a year of revenue, each a share of a year's output, on one scale. */
    VBox economyCard() {
        Game g = ui.game;
        DebtManager ledger = g.getDebtManager();
        NationalAccounts na = g.getEconomyManager().getNationalAccounts();
        double annual = annualGdp(na);
        double principal = ledger.getAllPrincipal();
        if (!(annual > 0)) {
            return card(caption("AGAINST THE ECONOMY", null),
                    line("There is no output recorded yet, so nothing here can be put in proportion."));
        }
        double[] amounts = {principal, ledger.getMonthlyCoupon() * 12, na.getTotalRevenue() * 12};
        String[] names = {"Debt outstanding", "A year of coupon", "A year of revenue"};
        String[] tips = {"owed " + dFull(principal), "a month's coupon " + dFull(ledger.getMonthlyCoupon()) + ", a year of it",
                "a month's revenue " + dFull(na.getTotalRevenue()) + ", a year of it"};
        double most = 0;
        for (double v : amounts) most = Math.max(most, v / annual);
        VBox card = card(caption("AGAINST THE ECONOMY", economyInfo(principal / annual),
                hint(ofAnnualGdp(na) + " " + d(annual))));
        for (int i = 0; i < amounts.length; i++) {
            Label name = words(names[i], Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL);
            name.setMinWidth(180);
            name.setPrefWidth(180);
            Region bar = segmentBar(List.of(seg(amounts[i] / annual, Palette.MONEY, tips[i])), most > 0 ? most : 1, null, 0, 10);
            HBox.setHgrow(bar, Priority.ALWAYS);
            Label share = figure(pc1(amounts[i] / annual), Palette.SIZE_LABEL + 1, Palette.TEXT_HEAD);
            share.setMinWidth(64);
            share.setAlignment(Pos.CENTER_RIGHT);
            HBox row = new HBox(Palette.GAP, name, bar, share);
            row.setAlignment(Pos.CENTER_LEFT);
            card.getChildren().add(row);
        }
        if (gdpEstimated(na)) {
            card.getChildren().add(noteLine("a year scaled up from " + na.getMonthsRecorded() + " months",
                    String.format("This city has only %d months of output recorded, so the year is scaled up from "
                            + "those rather than measured.", na.getMonthsRecorded()), 560));
        }
        return card;
    }

    /** OWED AND THE RATE: City History's public debt and borrowing rate on two axes, the borrowing decisions as flags (the spec's section 5). */
    VBox owedChartCard() {
        HistorySave h = ui.game.getHistorySave();
        VBox card = card(caption("OWED AND THE RATE", "Public debt on the left axis, the borrowing rate on the right, "
                        + "month by month; a flag is a borrowing decision - an issue, a buyback, the rollover's setting, "
                        + "a default abroad. City History has every line, over the whole of the city's life.",
                door("Over the years", Palette.ACCENT, () -> ui.historyScreen.openOn("debt", "interestRate"))));
        if (h.months() < 2) {
            card.getChildren().add(line("Not enough months recorded yet."));
            return card;
        }
        HistoryScreen.Trace debt = ui.historyScreen.traceFor("debt");
        HistoryScreen.Trace rate = ui.historyScreen.traceFor("interestRate");
        TimeChart chart = new TimeChart(new ChartModel(), java.util.Set.of(), false);
        chart.setData(h.getMonth(), List.of(
                        new TimeChart.Line("debt", debt.label(), Palette.MONEY, 0, ui.historyScreen.historyValues(h, "debt"),
                                v -> HistoryScreen.plotScale(debt.unit(), v), v -> ui.historyScreen.fmtUnit(debt.unit(), v), ""),
                        new TimeChart.Line("interestRate", rate.label(), Palette.ORE, 1,
                                ui.historyScreen.historyValues(h, "interestRate"),
                                v -> HistoryScreen.plotScale(rate.unit(), v), v -> ui.historyScreen.fmtUnit(rate.unit(), v), "")),
                HistoryScreen.axisFor(debt.unit()), HistoryScreen.axisFor(rate.unit()), false, false, null,
                List.of(), List.of(), ChartModel.onAxis(ChartModel.flags(ui.game.getDecisions(), DecisionLog.BORROWING),
                        h.getMonth().get(0)),
                "no months recorded yet");
        chart.setSize(1160, 160);
        card.getChildren().add(chart);
        card.getChildren().add(keyRow(keySwatch(Palette.MONEY, debt.label() + ", left"),
                keySwatch(Palette.ORE, rate.label() + ", right")));
        return card;
    }

    /* =====================================================================
       DEBT SERVICE (0.7.32)

       Can the take carry the paper? THE GAUGE leads, with two marks on
       the comfortable-felt-constrained bands (CityNeeds.SERVICE_FELT and
       SERVICE_CONSTRAINED): a month's coupon against a month's revenue, and
       the next twelve months of coupons and principal - the first twelve
       of every piece's payments, the ladder's own read - against twelve
       months of it. The page added last month's principal to the coupon
       and called it "debt service" while the strip called the coupon alone
       the same (the spec's B3): the month after a note fell due it read
       170.6% of revenue, red, on a city paying 1.5%. Last month's
       principal is LAST MONTH's now, split as the rollover booked it.
       ===================================================================== */

    void debtServicePage(VBox page) {
        page.getChildren().add(serviceCard());
        page.getChildren().add(pair(lastMonthCard(), ui.game.getDebtManager().hasForeignDebt() ? foreignCard() : null));
    }

    /** The band words, by CityNeeds.serviceLevel(). */
    static final String[] SERVICE_WORDS = {"comfortable", "felt", "constrained"};

    /** The gauge's (i): the spec's T15 and T17, on two marks. */
    static final String SERVICE_INFO = "What the city pays its lenders against what it collects, because a lender is "
            + "repaid out of what the city collects and not out of what the economy produces. Two marks: a month's "
            + "coupon against a month's revenue - the running cost - and everything the paper asks over the next "
            + "twelve months, coupons and principal, against twelve months of revenue - what has to be found or "
            + "refinanced. Comfortable under 12%, felt to 25%, constrained past it: the level at which a real city "
            + "stops being able to choose what it spends on, because the choice has already been made.";

    Node serviceCard() {
        Game g = ui.game;
        DebtManager ledger = g.getDebtManager();
        double revenue = g.getEconomyManager().getNationalAccounts().getTotalRevenue();
        double coupon = ledger.getMonthlyCoupon();
        double next12 = ledger.dueWithin(12);
        if (!(revenue > 0)) {
            return card(caption("THE NEXT TWELVE MONTHS", SERVICE_INFO),
                    line("Nothing came in this month, so nothing can be put against it."));
        }
        double month = coupon / revenue, year = next12 / (revenue * 12);
        double lead = Math.max(month, year);
        int level = CityNeeds.serviceLevel(lead);
        String tone = BuildScreen.verdict(level);
        VBox left = new VBox(4, words(year >= month ? "THE NEXT 12 MONTHS" : "A MONTH'S COUPON, IN CASH", Palette.SIZE_LABEL + 1,
                        Palette.TEXT_LABEL),
                figure(pc1(lead), 28, tone), words(SERVICE_WORDS[level], Palette.SIZE_BODY + 1, tone),
                words("of the take", Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        double top = .5;
        Pieces.SegmentBar gauge = segmentBar(List.of(
                        ghost(CityNeeds.SERVICE_FELT, Palette.GOOD, null, "comfortable: under " + pc(CityNeeds.SERVICE_FELT)),
                        ghost(CityNeeds.SERVICE_CONSTRAINED - CityNeeds.SERVICE_FELT, Palette.WARN, null,
                                "felt: " + pc(CityNeeds.SERVICE_FELT) + " to " + pc(CityNeeds.SERVICE_CONSTRAINED)),
                        ghost(top - CityNeeds.SERVICE_CONSTRAINED, Palette.BAD, null,
                                "constrained: past " + pc(CityNeeds.SERVICE_CONSTRAINED))), top,
                List.of(new Tick(Math.min(month, top), Palette.TEXT_HEAD, 3, null, "a month's coupon in cash " + pc1(month)),
                        new Tick(Math.min(year, top), Palette.ACCENT, 3, null, "the next 12 months " + pc1(year))), 0, 14);
        javafx.scene.layout.FlowPane marks = keyRow(keySwatch(Palette.TEXT_HEAD, "a month's coupon in cash " + pc1(month)
                                + (month > top ? " (past the end)" : "")),
                keySwatch(Palette.ACCENT, "the next 12 months " + pc1(year) + (year > top ? " (past the end)" : "")),
                words("comfortable to " + pc(CityNeeds.SERVICE_FELT) + " · felt to " + pc(CityNeeds.SERVICE_CONSTRAINED)
                        + " · constrained past it, to " + pc(top) + " drawn", Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        VBox right = new VBox(8, caption("WHAT THE PAPER ASKS, AGAINST THE TAKE", SERVICE_INFO), gauge, marks,
                line(String.format("%s due in the next twelve months, coupons and principal, against twelve months of "
                        + "this month's take, %s a month; the coupon alone is %s a month, in cash. The budget charged %s of "
                        + "interest last month: the coupons and the discount wearing off (LAST MONTH, below).",
                        d(next12), d(revenue), d(coupon),
                        d(g.getEconomyManager().getNationalAccounts().getInterestExpense()))));
        return heroCard(left, 220, right);
    }

    /** LAST MONTH's (i): the spec's T14. */
    static final String LAST_MONTH_INFO = "Only the interest is an expense; the principal is a balance-sheet movement. "
            + "The interest is the coupon paid in cash and the discount wearing off the paper, which no cash pays "
            + "until the paper falls due (paper sold over its face runs the other way). The coupon and the principal "
            + "leave the treasury, which is why both are on the ladder, and separated on the Government spending "
            + "page. A linked bond's indexation is an expense beside the interest, listed under it. What fell due "
            + "was netted from last year's surplus, raised again as new paper by the rollover, or paid from the cash.";

    /** The inflation uplift line's (i) on LAST MONTH (CD5, spec-city-debt.md 5.4). */
    static final String UPLIFT_INFO = "A linked bond's principal follows the price index, and the books catch up with "
            + "it every month: what the index added to what the city will owe is charged as an expense, beside the "
            + "interest and not part of it. It is not cash until the bond falls due: that month the city pays all the "
            + "uplift its months have booked, with the face, so the cash line can be more than the month's expense. In "
            + "a month the index fell it is below nothing, and the face is still owed whole.";

    /** LAST MONTH: the interest on the city's paper (the coupon paid and the discount wearing off), and the principal that fell due at the last press split as the rollover booked it. */
    VBox lastMonthCard() {
        Game g = ui.game;
        Rollover r = g.getRollover();
        // The interest is the effective interest (CD3): the coupons paid in cash and the discount the month
        // amortised, which the budget charges and no cash paid. The principal is not interest.
        NationalAccounts na = g.getEconomyManager().getNationalAccounts();
        VBox card = card(caption("LAST MONTH", LAST_MONTH_INFO));
        card.getChildren().add(cardLine("Interest on the city's paper", d(na.getInterestExpense()), null));
        card.getChildren().add(cardLine("...the coupon paid", d(na.getInterestPaidInCash()), Palette.TEXT_LABEL));
        if (Math.abs(na.getInterestNotCash()) >= .5) {
            card.getChildren().add(cardLine(na.getInterestNotCash() > 0 ? "...the discount wearing off (no cash)"
                    : "...less the premium wearing off (no cash)", d(na.getInterestNotCash()), Palette.TEXT_LABEL));
        }
        // The linked bonds' indexation (CD5): an expense beside the interest and not part of it, mostly not cash.
        if (Math.abs(na.getInflationUplift()) >= .5) {
            card.getChildren().add(cardLine("Inflation uplift on linked bonds", d(na.getInflationUplift()), null, UPLIFT_INFO));
            if (na.getUpliftPaidInCash() >= .5) {
                card.getChildren().add(cardLine("...paid in cash at maturity (earlier months' too)", d(na.getUpliftPaidInCash()), Palette.TEXT_LABEL));
            }
        }
        double repaid = g.hasTreasuryMonth() ? g.getTreasuryRepaid() : 0;
        card.getChildren().add(cardLine("Principal that fell due last month", d(repaid), null));
        if (r.getLastMonth() == g.getMonth() - 1 && r.getLastDue() >= .5) {
            card.getChildren().add(segmentBar(List.of(
                            seg(r.getLastNetted(), Palette.MONEY, "netted from last year's surplus " + d(r.getLastNetted())),
                            seg(r.getLastRaised(), Palette.MONEY_LIGHT, "re-borrowed as new paper " + d(r.getLastRaised()))),
                    r.getLastDue(), null, 0, 10));
            card.getChildren().add(line(String.format("%s fell due: %s paid from last year's surplus, %s.",
                    d(r.getLastDue()), d(r.getLastNetted()), r.getLastRaised() >= .5
                            ? d(r.getLastRaised()) + " re-borrowed (" + d(r.getLastIssued()) + " of new face)"
                            : "none re-borrowed")));
        } else if (repaid >= .5) {
            card.getChildren().add(line(d(repaid) + " of principal fell due and was repaid; the rollover did not run on it."));
        } else {
            card.getChildren().add(line("No principal fell due last month."));
        }
        return card;
    }

    /** FOREIGN's (i): the spec's T16. */
    static final String FOREIGN_INFO = "Foreign paper is repaid in somebody else's money, and the only way the city "
            + "earns that is by selling abroad. Exports are the denominator the world actually uses: it shuts its "
            + "window when a year's service stops being covered by what the city sells (Borrow, Abroad).";

    /** FOREIGN: a year of the dollar paper's service on a bar of a year of exports, red only when the world has shut its window. */
    VBox foreignCard() {
        DebtManager ledger = ui.game.getDebtManager();
        double exports = ledger.getMonthlyExports() * 12;
        double next = ledger.nextYearService();
        boolean shut = !ledger.foreignWindowOpen();
        VBox card = card(caption("FOREIGN", FOREIGN_INFO));
        card.getChildren().add(cardLine("A year of foreign debt service", d(next), null));
        card.getChildren().add(cardLine("A year of exports, at this month's", d(exports), null));
        card.getChildren().add(segmentBar(List.of(seg(next, shut ? Palette.BAD : Palette.BUSINESS, "a year's foreign service "
                        + d(next))), Math.max(exports, next) > 0 ? Math.max(exports, next) : 1,
                List.of(new Tick(exports, Palette.TEXT_HEAD, 2, null, "a year of exports " + d(exports))), 0, 10));
        card.getChildren().add(line(exports > 0 ? pc(next / exports) + " of exports" + (shut ? " - the window abroad is shut" : "")
                : "nothing exported"));
        return card;
    }

    /* =====================================================================
       HOME AND ABROAD (0.7.32)

       Who holds the paper, and in whose money? One bar of its four
       holders (The book's "who holds it" until 0.7.32, the spec's D19), the
       dollar debt as the sum it is - dollars times the rate - and what
       stands behind it.
       ===================================================================== */

    /** WHO HOLDS IT's (i): the spec's T19 and T29. */
    static final String HOLDERS_INFO = "Domestic paper is bought at home, so its coupon is income at home and none of "
            + "it crosses the city's edge; foreign paper is bought by somebody else, is repaid in their money, and every "
            + "payment genuinely leaves. The households buy their share when an issue settles and it pays them better "
            + "than a deposit, and sell to the bank's desk when they are short. The central bank buys the bank's term "
            + "paper with money it makes, then the households' once the bank has none left, then the businesses' - "
            + "the holdings dial on the Policy tab. Businesses with idle money buy a share of an issue out of their "
            + "tills when it pays them more than the deposit and the world, are paid its coupons, and sell to the bank's "
            + "desk when the spread has gone, when they borrow or when they need the money. The world buys a share of an "
            + "issue, from abroad and outside the bank's room, while its appetite for the city's yield is not met; "
            + "its coupon and principal leave the city, and it sells to the bank's desk when its money leaves. The central bank sells "
            + "to the bank only. It replaces what it holds of a maturing piece with the same par "
            + "of the new paper, at issue, less what it holds past its dial and what last year's surplus pays off. The "
            + "bank holds the rest. Dollar paper is held abroad.";

    void homeAndAbroadPage(VBox page) {
        DebtManager ledger = ui.game.getDebtManager();
        double away = ledger.getForeignPrincipal();
        double all = ledger.getDomesticPrincipal() + away;
        if (!(all > 0)) {
            page.getChildren().add(card(caption("WHO HOLDS IT", HOLDERS_INFO),
                    line("The city owes nothing, at home or abroad.")));
            return;
        }
        page.getChildren().add(holdersCard(all));
        if (away > 0) {
            page.getChildren().add(pair(dollarDebtCard(), behindCard()));
        } else {
            page.getChildren().add(card(caption("ABROAD", null), noteLine("Nothing is owed abroad.",
                    "Every piece of the city's paper is held at home, so its debt cannot be made bigger by the exchange "
                            + "rate - which is a real form of safety, and one that is easy to give up for a lower "
                            + "headline coupon.", 560)));
        }
    }

    /** WHO HOLDS IT: the households, the bank, the central bank, the businesses (CD2, slice 4) and the world, one bar, each with its amount and share. */
    VBox holdersCard(double all) {
        DebtManager ledger = ui.game.getDebtManager();
        double[] held = {ledger.householdPrincipal(), ledger.bankPrincipal(), ledger.centralBankPrincipal(),
                         ledger.firmPrincipal(), ledger.worldPrincipal(), ledger.getForeignPrincipal()};
        String[] who = {"households", "the bank", "the central bank", "businesses", "the world", "abroad"};
        String[] colour = {Palette.PEOPLE, Palette.MONEY, Palette.MONEY_DARK, Palette.BUSINESS_DARK, Palette.BUILDING_DARK,
                           Palette.BUSINESS};
        List<Segment> parts = new ArrayList<>();
        javafx.scene.layout.FlowPane key = keyRow();
        for (int i = 0; i < held.length; i++) {
            String share = pc1(held[i] / all);
            parts.add(new Segment(held[i], colour[i], false, null, who[i] + " " + share,
                    who[i] + " " + dFull(held[i]) + " · " + share, null));
            key.getChildren().add(keySwatch(colour[i], who[i] + " " + d(held[i]) + " · " + share));
        }
        return card(caption("WHO HOLDS IT", HOLDERS_INFO, hint(d(all) + " owed")), segmentBar(parts, all, null, 0, 18), key);
    }

    /** THE DOLLAR DEBT's (i): the spec's T20, both ways. */
    static final String DOLLAR_INFO = "Owed in dollars, which do not move; worth in the city's money whatever the "
            + "exchange rate says. A weaker currency makes the same paper a bigger debt - nobody was paid a cent for it, "
            + "which is what a devaluation does to a country that borrowed in someone else's money. A stronger one "
            + "makes it smaller. It works both ways and it is not income either way.";

    /** THE DOLLAR DEBT: dollars times the rate is the local figure; what the currency did to it last month as a chip. */
    VBox dollarDebtCard() {
        DebtManager ledger = ui.game.getDebtManager();
        ForeignAccounts fx = ui.game.getForeignAccounts();
        VBox card = card(caption("THE DOLLAR DEBT", DOLLAR_INFO, door("The currency", Palette.ACCENT,
                () -> openTrade("The currency", TradeScreen.TRADE_MONEY_PAGES[0]))));
        card.getChildren().add(figure(usd(ledger.getForeignPrincipalUsd()) + " × " + perUsd(ledger.getExchangeRate())
                + " = " + d(ledger.getForeignPrincipal()), Palette.SIZE_BODY + 2, Palette.TEXT_HEAD));
        double moved = fx.getLastRevaluation();
        if (Math.abs(moved) > .005) {
            card.getChildren().add(chip("the currency moved it " + signedD(moved) + " last month",
                    moved > 0 ? Palette.BAD : Palette.GOOD));
        }
        return card;
    }

    /** WHAT IS BEHIND IT's (i): the spec's T21 and T22, as the model reads cover (ForeignAccounts.COMFORTABLE_COVER). */
    static final String BEHIND_INFO = "Reserves are the city's dollars. Import cover is how many months of imports "
            + "they would pay for: the currency's buffer. At " + (int) ForeignAccounts.COMFORTABLE_COVER + " months or "
            + "more the reserves absorb as much of a month's pressure on the currency as they ever can; under it, "
            + "less, and with none the defence is over. The world also prices dollar paper on the city's exports. "
            + "The Trade tab is where reserves are bought and sold.";

    /** WHAT IS BEHIND IT: the reserves, and the import cover on a year's bar with the model's comfortable line. */
    VBox behindCard() {
        DebtManager ledger = ui.game.getDebtManager();
        ForeignAccounts fx = ui.game.getForeignAccounts();
        double cover = ledger.getImportCover();
        double end = 12;
        VBox card = card(caption("WHAT IS BEHIND IT", BEHIND_INFO, door("The reserves", Palette.ACCENT,
                () -> openTrade("The reserves", TradeScreen.TRADE_VAULT_PAGES[0]))));
        card.getChildren().add(cardLine("Reserves held", d(fx.getReserves()), null));
        card.getChildren().add(cardLine("Import cover", coverMonths(cover), null));
        card.getChildren().add(segmentBar(List.of(seg(Math.min(Math.max(0, cover), end), Palette.MONEY,
                        coverMonths(cover) + " of imports")), end,
                List.of(new Tick(ForeignAccounts.COMFORTABLE_COVER, Palette.TEXT_HEAD, 2,
                        "absorbs all it can, " + (int) ForeignAccounts.COMFORTABLE_COVER + " mo", null)), 0, 10));
        return card;
    }

    /* =====================================================================
       YOUR RATE, TAKEN APART (0.7.32)

       Why does money cost this city what it does? THE RATE, BUILT UP on
       the band from nothing to what a hopeless city pays - the floor, then
       what the debt adds against output and against a year of tax - with
       the dial and the city's rate marked; each part's figure, what moves
       it and a door to it. THE CURVE by maturity, home and abroad, and how
       much each measure has left to say.

       THE CURVE'S THIRTY-YEAR POINT IS THE NEW PARTS (2026-10-20, CD1): the
       dial the market expects over thirty years, what the city's debt adds,
       the term premium, less what the central bank holds - the four getters
       that add up to curveRate(360) - and a line on where the market expects
       the dial in a year and in five, against today.

       THE FLOOR IS NOT THE DIAL (the spec's B13, this page's T23): it is
       the dial or what the bank's money costs it, whichever is higher, on
       the part of the paper the central bank does not hold.
       ===================================================================== */

    void yourRatePage(VBox page) {
        page.getChildren().add(builtUpCard());
        GridPane two = new GridPane();
        two.setHgap(TILE_GAP);
        javafx.scene.layout.ColumnConstraints wide = new javafx.scene.layout.ColumnConstraints();
        wide.setPercentWidth(66);
        wide.setHgrow(Priority.ALWAYS);
        javafx.scene.layout.ColumnConstraints narrow = new javafx.scene.layout.ColumnConstraints();
        narrow.setPercentWidth(34);
        narrow.setHgrow(Priority.ALWAYS);
        two.getColumnConstraints().addAll(wide, narrow);
        two.setMaxWidth(Double.MAX_VALUE);
        VBox curve = curveCard(), measures = measuresCard();
        GridPane.setFillHeight(curve, true);
        GridPane.setFillHeight(measures, true);
        two.add(curve, 0, 0);
        two.add(measures, 1, 0);
        page.getChildren().add(two);
    }

    /** The floor's (i): the spec's T23, rewritten (B13), and the central bank's share (0.7.15). */
    String floorInfo() {
        DebtManager ledger = ui.game.getDebtManager();
        String s = String.format("The floor is the dial or what the bank's money costs it, whichever is higher - now "
                        + "%s against the dial's %s. Nobody lends for less than money earns sitting still, and the "
                        + "bank that buys the city's paper lends what its own money costs it, plus its margin.",
                pct2(ledger.floorRate()), pct2(ledger.getPolicyRate()));
        double held = ledger.centralBankShareOfPaper();
        if (held > 0) {
            s += String.format("\n\nThe central bank holds %.0f%% of the city's paper, and that share is priced at the "
                            + "dial, %s. Only the rest is priced at the bank's floor, %s - so the floor is %s. The more "
                            + "the central bank holds, the less the bank's own state can raise what the city pays.",
                    held * 100, pct2(ledger.getPolicyRate()), pct2(ledger.bankFloorRate()), pct2(ledger.floorRate()));
        }
        return s;
    }

    /** THE RATE, BUILT UP: the floor and the two measures on the band from nothing to the ceiling, the dial and the city's rate marked, then each part with what moves it. */
    VBox builtUpCard() {
        Game g = ui.game;
        DebtManager ledger = g.getDebtManager();
        double floor = ledger.baseComponent(), gdp = ledger.gdpSpread(), rev = ledger.revenueSpread();
        double rate = g.getInterestRate(), ceiling = ledger.ceilingRate(), dial = ledger.getPolicyRate();
        // Two names under the bar that would touch become one, under the city's mark.
        boolean near = Math.abs(rate - dial) < ceiling * .08;
        VBox card = card(caption("THE RATE, BUILT UP", floorInfo(), hint("from nothing to what a hopeless city pays, "
                + pct2(ceiling))));
        card.getChildren().add(segmentBar(List.of(
                        new Segment(floor, Palette.MONEY_DARK, false, null, "floor", "the floor " + pct2(floor), null),
                        new Segment(gdp, Palette.MONEY, false, null, null, "debt against a year of output " + pts(gdp), null),
                        new Segment(rev, Palette.MONEY_LIGHT, false, null, null, "debt against a year of tax " + pts(rev), null)),
                ceiling, List.of(new Tick(dial, Palette.TEXT_MUTED, 2, near ? null : "the dial " + pct2(dial),
                                "the policy rate " + pct2(dial)),
                        new Tick(rate, Palette.TEXT_HEAD, 3, near ? "you " + pct2(rate) + " · the dial " + pct2(dial)
                                : "you " + pct2(rate), "the city's rate")), 0, 18));
        GridPane cells = equalColumns(3, TILE_GAP);
        cells.add(rateCell("THE FLOOR", pct2(floor), "the dial, or what the bank's money costs it, whichever is higher",
                door("Policy rate", Palette.ACCENT, () -> openPolicy("Money", "The policy rate")), Palette.MONEY_DARK), 0, 0);
        cells.add(rateCell("DEBT AGAINST A YEAR OF OUTPUT", pts(gdp), gdp <= 0 ? "nothing owed" : "grow, or owe less",
                door("Output", Palette.ACCENT, () -> ui.governmentScreen.open("Output", null)), Palette.MONEY), 1, 0);
        cells.add(rateCell("DEBT AGAINST A YEAR OF TAX", pts(rev), rev <= 0 ? "nothing owed" : "tax more, or owe less",
                door("Taxes", Palette.ACCENT, () -> openPolicy("Taxes", PolicyScreen.POLICY_HOME)), Palette.MONEY_LIGHT), 2, 0);
        card.getChildren().add(cells);
        card.getChildren().add(cardLine("What the market quotes, the short end", pct2(rate),
                ledger.atCeiling() ? Palette.BAD : Palette.TEXT_HEAD));
        return card;
    }

    /** One part of the rate: its swatch and name, its figure, what moves it, and its door. */
    static VBox rateCell(String name, String value, String moves, Node door, String colour) {
        HBox head = new HBox(6, keySwatch(colour, ""), words(name, Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        head.setAlignment(Pos.CENTER_LEFT);
        VBox c = new VBox(4, head, figure(value, Palette.SIZE_LEAD, Palette.TEXT_HEAD),
                words(moves, Palette.SIZE_LABEL, Palette.TEXT_MUTED), door);
        c.setStyle("-fx-padding: 8 10 8 10; -fx-background-color: " + Palette.PANEL + "; -fx-background-radius: 6;");
        c.setMaxWidth(Double.MAX_VALUE);
        return c;
    }

    /** The curve's maturities, in months. */
    static final int[] CURVE_MONTHS = {3, 6, 12, 24, 60, 120, 240, 360, 480, 600};
    /** ...and how they are written under it. */
    static final String[] CURVE_NAMES = {"3m", "6m", "1y", "2y", "5y", "10y", "20y", "30y", "40y", "50y"};

    /** THE CURVE's (i): the spec's T24, and the world's curve - CD1 priced it on the dial the market expects. */
    static final String CURVE_INFO = "The short end is the floor - the dial, or what the bank's money costs it, "
            + "whichever is higher - plus what the city's own debt adds. Further out, each month is priced at where the "
            + "market expects the dial to be then: the rule's rate at the inflation it expects, with the dial drifting "
            + String.format("there over about %d months whoever holds it", YieldCurve.DRIFT_MONTHS)
            + " - plus the debt's spread. Longer money adds a term premium on top, less whatever the central bank's "
            + "holdings of term paper take off it. The world lends at its own base and the same premium abroad. "
            + "The dashed line is the dial today.";

    /** THE CURVE: the city's rate by maturity at home and, while the window is open, abroad, with the dial dashed and the thirty-year point taken apart. */
    VBox curveCard() {
        DebtManager ledger = ui.game.getDebtManager();
        boolean world = ledger.foreignWindowOpen();
        double[] home = new double[CURVE_MONTHS.length], abroad = world ? new double[CURVE_MONTHS.length] : null;
        for (int i = 0; i < CURVE_MONTHS.length; i++) {
            home[i] = ledger.curveRate(CURVE_MONTHS[i]);
            if (world) abroad[i] = ledger.foreignCurveRate(CURVE_MONTHS[i]);
        }
        double expected = ledger.expectedDialOver(360), credit = ledger.creditSpread();
        double premium = DebtManager.termPremium(360), comp = ledger.compression(360);
        VBox card = card(caption("THE CURVE", CURVE_INFO),
                new CurveChart(home, abroad, ledger.getPolicyRate(), 7,
                        String.format("%.2f the dial expected + %.2f credit + %.2f term − %.2f the central bank",
                                expected * 100, credit * 100, premium * 100, comp * 100)),
                keyRow(keySwatch(Palette.MONEY, "at home"), world ? keySwatch(Palette.BUSINESS, "abroad, in dollars") : null,
                        keySwatch(Palette.TEXT_MUTED, "the dial, dashed")),
                line(String.format("Thirty-year money costs %s: the dial the market expects over thirty years, %s; "
                                + "what the city's debt adds, %s; the term premium, %s; and %s.",
                        pct2(ledger.curveRate(360)), pct2(expected), pts(credit), pts(premium),
                        comp > 0 ? String.format("%.2f pts off for what the central bank holds", comp * 100) : "nothing off")),
                line(String.format("The market expects the dial at %s in a year and %s in five, against %s today.",
                        pct2(ledger.expectedDialIn(12)), pct2(ledger.expectedDialIn(60)), pct2(ledger.getPolicyRate()))));
        return card;
    }

    /**
     * The curve, drawn: a point a maturity at equal steps, the city's in the
     * money blue, the world's in violet, the dial dashed across, and the
     * point at `marked` annotated with `note` - a fixed size, as a card's
     * picture is; every figure is the model's curve at that maturity.
     */
    static final class CurveChart extends javafx.scene.layout.Pane {
        /** Its size and its margins, in pixels: a card's picture, at a fixed size. */
        static final double W = 720, H = 190, LEFT = 44, RIGHT = 16, TOP = 26, FOOT = 22;

        CurveChart(double[] home, double[] abroad, double dial, int marked, String note) {
            double top = dial;
            for (double v : home) top = Math.max(top, v);
            if (abroad != null) for (double v : abroad) top = Math.max(top, v);
            top = Math.max(.001, top * 1.15);
            double plotW = W - LEFT - RIGHT, plotH = H - TOP - FOOT;
            int n = home.length;
            java.util.function.IntToDoubleFunction x = i -> LEFT + (n > 1 ? plotW * i / (n - 1) : 0);
            final double scale = top;
            java.util.function.DoubleUnaryOperator y = v -> TOP + plotH * (1 - Math.max(0, v) / scale);
            for (double tick : new double[] {0, scale / 2, scale}) {
                javafx.scene.shape.Line grid = new javafx.scene.shape.Line(LEFT, y.applyAsDouble(tick), W - RIGHT, y.applyAsDouble(tick));
                grid.setStroke(javafx.scene.paint.Color.web(Palette.EDGE));
                Label at = new Label(pct2(tick));
                at.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
                at.relocate(0, y.applyAsDouble(tick) - 7);
                getChildren().addAll(grid, at);
            }
            javafx.scene.shape.Line dialLine = new javafx.scene.shape.Line(LEFT, y.applyAsDouble(dial), W - RIGHT, y.applyAsDouble(dial));
            dialLine.setStroke(javafx.scene.paint.Color.web(Palette.TEXT_MUTED));
            dialLine.getStrokeDashArray().addAll(5.0, 4.0);
            getChildren().add(dialLine);
            draw(abroad, x, y, Palette.BUSINESS, "abroad");
            draw(home, x, y, Palette.MONEY, "at home");
            for (int i = 0; i < n; i++) {
                Label name = new Label(CURVE_NAMES[i]);
                name.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
                name.relocate(x.applyAsDouble(i) - 8, H - FOOT + 4);
                getChildren().add(name);
            }
            if (marked >= 0 && marked < n) {
                Label at = new Label(pct2(home[marked]) + "  " + note);
                at.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
                double ax = Math.min(x.applyAsDouble(marked) - 40, W - 420);
                at.relocate(Math.max(LEFT, ax), Math.max(0, y.applyAsDouble(home[marked]) - 22));
                getChildren().add(at);
            }
            setMinSize(W, H);
            setPrefSize(W, H);
            setMaxSize(W, H);
        }

        private void draw(double[] values, java.util.function.IntToDoubleFunction x,
                          java.util.function.DoubleUnaryOperator y, String colour, String who) {
            if (values == null) return;
            javafx.scene.shape.Polyline line = new javafx.scene.shape.Polyline();
            line.setStroke(javafx.scene.paint.Color.web(colour));
            line.setStrokeWidth(2);
            getChildren().add(line);
            for (int i = 0; i < values.length; i++) {
                double px = x.applyAsDouble(i), py = y.applyAsDouble(values[i]);
                line.getPoints().addAll(px, py);
                javafx.scene.shape.Circle dot = new javafx.scene.shape.Circle(px, py, 3.5, javafx.scene.paint.Color.web(colour));
                Tooltip tip = new Tooltip(CURVE_NAMES[i] + " " + who + ": " + pct2(values[i]));
                tip.setShowDelay(Duration.millis(150));
                Tooltip.install(dot, tip);
                getChildren().add(dot);
            }
        }
    }

    /** WHAT EACH MEASURE HAS USED's (i): the spec's T25. */
    static String measuresInfo() {
        return String.format("Each measure adds up to %.0f points and then stops, so being sound on one and hopeless "
                        + "on the other is priced as exactly half the worst case. Both run out at %.0f years of whatever "
                        + "they measure against: output, and a year of tax.",
                DebtManager.maxSpreadPerMeasure() * 100, DebtManager.fullStressMultiple());
    }

    /** WHAT EACH MEASURE HAS USED: how much of its worst case each of the two measures has used, and a default's scar abroad. */
    VBox measuresCard() {
        DebtManager ledger = ui.game.getDebtManager();
        VBox card = card(caption("WHAT EACH MEASURE HAS USED", measuresInfo()));
        card.getChildren().add(stressRow("Debt against output", ledger.gdpStress()));
        card.getChildren().add(stressRow("Debt against revenue", ledger.revenueStress()));
        if (ledger.getDefaultScar() > 0) {
            card.getChildren().add(infoLine(String.format("A default abroad: +%.1f points on what the world charges",
                            ledger.getDefaultScar() * 100),
                    String.format("%.1f points on the city's premium abroad, %d months after it happened. It fades over "
                                    + "about five years; the city's own paper at home does not carry it.",
                            ledger.getDefaultScar() * 100, Math.max(0, ledger.getMonthsSinceForeignDefault())),
                    true, Palette.SIZE_LABEL + 1, Palette.BAD, 380));
        }
        return card;
    }

    /** One measure: its name, a bar of how much of its worst case it has used, and the share. */
    static VBox stressRow(String label, double stress) {
        double used = Math.max(0, Math.min(1, stress));
        Region bar = segmentBar(List.of(seg(used, Palette.MONEY, label + ": " + pc(used) + " of its worst case")), 1,
                null, 0, 8);
        HBox head = new HBox(Palette.GAP, words(label, Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        head.getChildren().addAll(gap, figure(pc(used), Palette.SIZE_LABEL + 1, Palette.TEXT_HEAD));
        return new VBox(3, head, bar);
    }

    /* =====================================================================
       THE BOOK (0.7.32; a desk since CD6, 2026-10-23)

       What is each line, and what would it cost to retire? One page since
       0.7.32 (Every piece and Buy back until then). It was a card a piece,
       and since CD6 (spec-city-debt.md section 7, board 1; D31) it is a bond
       desk, as a treasury reads its lines:

         THE FIVE       what is owed at face, what the books carry it at,
         FIGURES        what it is worth on today's curve, the coupon a
                        month in cash, and what a point on the curve moves.
         ON THE CURVE   today's zero curve and a dot a line at (months left,
                        yield); a floater and a linker are off it.
         IN BARS        face, carried and worth on one scale, each by kind.
         EVERY LINE     a row a line by when it falls due: its coupon, the
                        yield it was sold at and today's, price, worth,
                        carried, what closing it would book, duration and
                        holders. A click opens the line's page (A LINE'S
                        PAGE, below); the ladder's columns and NEXT DUE
                        scroll to the row.

       A LINE IS VALUED AT WHAT A BUYBACK PAYS: DebtManager.marketValue(),
       the city's curve for its own paper and the world's for a dollar
       piece. The Buy back page valued a dollar piece at the city's short
       rate while Game.repurchaseDebt() charged the world's curve, and
       painted a price under par green under a sentence saying it was "not
       the bargain it looks like" (the spec's B6): the price is plain now,
       and so is a gain or a loss - no colour on either.

       Every figure is the model's (BookDesk, struck ONCE by Game.bookDesk()
       for the page); the screen works out none. Since CD4 worth today is
       the floor of what closing costs, not the cost: a buyback is a reverse
       auction that pays each holder what its own rule asks, never under
       fair (Game.fairValueOf()); a line's page reads the whole line's quote
       (Game.quoteBuyback()) for the gain, and opens Close it now (below).
       ===================================================================== */

    /** The book's (i): the spec's T32 and T34. */
    String bookInfo() {
        DebtManager ledger = ui.game.getDebtManager();
        return String.format("Every piece is priced at what it is worth today: the present value of everything it still "
                        + "owes, discounted at the curve's rate for the months it has left - %s for a bill, %s for "
                        + "thirty-year money - and a dollar piece at the world's curve. Above par means getting out costs "
                        + "a premium. Below par means your own paper has become cheap to retire - which happens when your "
                        + "credit has got worse, so it is not the bargain it looks like.\n\n%s of face for %s of cash. "
                        + "Retiring paper lowers the city's rate, which raises what the rest of it is worth - so these "
                        + "prices are quoted off the market as it stands, not as it will stand after the first trade.",
                pct2(ledger.getRate()), pct2(ledger.curveRate(360)), d(ledger.getAllPrincipal()),
                d(ledger.getTotalMarketValue()));
    }

    /** A bill's (i): the spec's T28. */
    static final String NOTE_INFO = "No coupon. The lender's return was the discount, taken out of the proceeds at "
            + "issue. The city books it as interest, a little every month, so the bill is carried under its face and "
            + "climbs to it by the day it falls due. No cash leaves until then, and the whole face falls due at once.";

    /* ----- the books (CD3) ----- */

    /** The "carried · effective" line on a line's page: its (i). */
    static final String CARRIED_INFO = "What the books hold the piece at, and the rate they charge it at. It starts "
            + "at what the sale raised, so a piece sold under its face is carried under it. Each month the interest "
            + "is that figure at the effective rate; what the coupon does not pay of it is added to what is carried, "
            + "so the figure climbs to the face by the day it falls due. Paper sold over its face runs down to it "
            + "instead. The effective rate is the yield the sale made of the piece's payments, not its coupon.";

    /** The same line on an inflation-linked bond's card (CD6): its rate is a real rate. */
    static final String LINKED_BOOKS_INFO = "What the books hold the piece at, and the rate they charge it at. The books "
            + "follow the bond's schedule at today's index held flat: the real coupon on the principal as the index has "
            + "moved it, and at the end the face, or the indexed principal when that is more. So the effective rate is "
            + "a real rate, the yield over the index, and not a nominal one. What the index adds to what the city will "
            + "owe is booked beside it, as the inflation uplift, and is not in this rate.";

    /** A price against face, in words (the spec's T33, D16). */
    static final String PREMIUM_INFO = "Buying a piece back pays its holders what it is worth today. Under its face, "
            + "the city clears more debt than it pays - because its credit has got worse since the piece was sold, or "
            + "rates have risen. Over its face, a premium: this piece pays more than the city could borrow at today. "
            + "The gain or loss the books show is against what the piece is carried at, which is not its face while "
            + "a discount is still wearing off.";

    /** A callable line's (i) on its card (CD4, spec-city-debt.md 2.6 and 6.4). */
    static final String CALLABLE_INFO = "The city sold this line with the right to call it: from its first call date "
            + "it can pay every holder 100 for each 100 of face, whole, and be done with it. The coupon was set "
            + "higher at the sale to pay for that right. Close it now has the call.";

    /** A floating-rate note's coupon line's (i) on its card (CD5, spec-city-debt.md 2.4). */
    static final String FLOATING_INFO = "The coupon is the policy rate plus the margin the sale fixed, a twelfth of it "
            + "every month on the whole face, and it moves with the dial: the first figure is the dial the coupon now "
            + "follows, the second the margin, which never changes. The coupon is never under nothing: a margin "
            + "below nothing, on a dial that falls far enough, leaves it at nothing. The note is carried at what "
            + "the sale raised, and the rate that charges it is solved again whenever the dial moves.";

    /** An inflation-linked bond's coupon line's (i) on its card (CD5, spec-city-debt.md 2.5). */
    static final String INDEXED_INFO = "The real coupon is paid every month on the principal as the price index has "
            + "moved it since the sale, so the coupon grows with prices. The index is the city's own price level now "
            + "over what it was at the sale. The books catch up with it every month and charge the change as an "
            + "inflation uplift, an expense that is not cash; the last month pays it, with the face - and the face is "
            + "paid whatever happened, so prices that fell cost the lender nothing and the city nothing less.";

    /** "Worth calling"'s (i) (D24). */
    static final String WORTH_CALLING_INFO = "The present value the call would save: what this line's remaining payments "
            + "are worth today, as a bond that cannot be called, less the 100 the call pays and less what selling the "
            + "paper again costs. It is worth calling when that passes " + pc(CallTree.WORTH_CALLING) + " of the face, the "
            + "rule municipal refunding goes by. Whether it can be called yet is the call date's.";

    /** THE CURVE card's (i) (CD6, board 1): what the dots are and what is left off. */
    static final String DESK_CURVE_INFO = "Today's zero curve is the line: what the city pays to borrow for each term. Each "
            + "line of paper is a dot at the months it has left and the yield the market prices it at now, in its kind's "
            + "colour, and a bond is named beside its dot where there is room. A floating-rate note has no fixed yield, "
            + "and a linked bond's is a real yield, so neither is on the chart. Dollar paper is priced on the world's curve, "
            + "not this one: its dot is hollow.";

    /** THE THREE BARS' (i): the board's paragraph. */
    static final String DESK_BARS_INFO = "Carried is what the books say the city owes: what it raised, plus the discount "
            + "amortised since at each line's effective rate. Worth is what the remaining payments are worth on today's "
            + "curve. The gap is what a buyback at today's prices would book. The dashed line marks what the book is worth "
            + "today across all three.";

    /** THE DESK's (i): the two readings under the table. */
    static final String DESK_INFO = "If closed now: the carrying value less the worth. Above nothing is a gain the "
            + "statement would book buying the line back at today's fair price, before the premium a buyback pays; below "
            + "nothing, a loss. It is above nothing when rates have risen since the line was sold: the paper is worth less "
            + "than it is carried at, and borrowing again to close it costs more. Vs face: the face less the worth, what "
            + "the market's price saves against what is owed. No colour on either: a price under what a line is carried at "
            + "means the city's paper got cheaper because its rates rose, and that is not good news (the 0.7.32 spec's B6).";

    /**
     * THE BOOK, AS A DESK (CD6, spec-city-debt.md section 7, board 1): the book's five figures, where each line sits on
     * today's curve, the book at face, carried and worth in bars, and every line a row by when it falls due - the
     * coupon, the yield it was sold at and today's, the price, what it is worth and carried at, what closing it would
     * book, how long it lasts and who holds it. A click on a row opens the line's page. The desk strikes the market
     * ONCE (Game.bookDesk()); every figure on the page is the desk's, and the page works out none.
     */
    void bookPage(VBox page) {
        Game g = ui.game;
        DebtManager ledger = g.getDebtManager();
        if (ledger.getDebt().isEmpty()) {
            page.getChildren().add(card(caption("EVERY LINE", null), line("Nothing outstanding."),
                    door("Borrow", Palette.ACCENT, () -> open("Borrow", "At home", null))));
            return;
        }
        BookDesk desk = g.bookDesk();
        List<BookDesk.Line> lines = desk.lines();
        int month = g.getMonth();
        int n = lines.size();
        page.getChildren().add(figureStrip(new String[][] {
                {"OWED, AT FACE", d(desk.face()), n + (n == 1 ? " line" : " lines")},
                {"CARRIED", d(desk.carried()), "what the books say"},
                {"WORTH TODAY", d(desk.worth()), "on today's curve"},
                {"COUPON", d(desk.coupon()), "a month, in cash"},
                {"A POINT ON THE CURVE", d(desk.valueOfAPoint()), "moves what it is worth"}}));
        // The lines are sorted by when they fall due: the first is the nearest.
        int nearest = lines.get(0).debt().getMaturityMonth();
        String head = String.format("%d %s · %s in the treasury · the nearest falls due %s, %s", n, n == 1 ? "line" : "lines",
                d(g.getCash()), CityCalendar.formatShort(nearest), CityCalendar.until(month, nearest));
        page.getChildren().add(infoLine(head, bookInfo(), true, Palette.SIZE_BODY + 1, Palette.TEXT_LABEL, 1200));
        page.getChildren().add(pair(deskCurveCard(desk, ledger), deskBarsCard(desk)));
        page.getChildren().add(deskCard(desk));
    }

    /**
     * Figures in the strip's look (CD6): a label, a figure and a note in each of equal columns on the strip's ground,
     * a hairline between. They share the page's width, so at a narrow window a note wraps and the row never runs off
     * the edge, as the 190 px cells of limitCell() would.
     */
    static GridPane figureStrip(String[][] cells) {
        GridPane row = equalColumns(cells.length, 0);
        row.setStyle("-fx-padding: 8 6 8 6;" + Palette.block(Palette.PANEL));
        for (int i = 0; i < cells.length; i++) {
            VBox cell = new VBox(0, words(cells[i][0], Palette.SIZE_CAPTION, Palette.TEXT_LABEL),
                    figure(cells[i][1], Palette.SIZE_SECTION, Palette.TEXT_HEAD),
                    words(cells[i][2], Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            cell.setAlignment(Pos.CENTER_LEFT);
            cell.setMaxHeight(Double.MAX_VALUE);
            cell.setStyle("-fx-padding: 0 14 0 14;" + (i < cells.length - 1
                    ? " -fx-border-color: " + Palette.HAIRLINE + "; -fx-border-width: 0 1 0 0;" : ""));
            row.add(cell, i, 0);
        }
        return row;
    }

    /** A kind's colour on the desk (CD6): the ladder's three for the bills, the amortizing bonds and the bonds, and a floater's and a linker's of their own, which the ladder folds into the bonds. */
    static String bookKindColour(String type) {
        return switch (type) {
            case "FLOAT"  -> Palette.WARN;
            case "LINKED" -> Palette.PEOPLE;
            default       -> instrumentColour(type);
        };
    }

    /** The kinds of paper as the desk names them, in DebtManager.BOOK_KINDS' order. */
    static final String[] BOOK_KIND_NAMES = {"bills", "amortizing bonds", "bonds", "floating-rate notes", "inflation-linked bonds"};

    /** A type's place in DebtManager.BOOK_KINDS; a type that is none of them is a term loan's (the manager's own reading). */
    static int bookKindOf(String type) {
        for (int i = 0; i < DebtManager.BOOK_KINDS.length; i++) if (DebtManager.BOOK_KINDS[i].equals(type)) return i;
        return 2;
    }

    /** The kinds a key names: a swatch for each kind that is in `present`, in the kinds' colours. */
    static List<Node> kindKey(boolean[] present) {
        List<Node> key = new ArrayList<>();
        for (int k = 0; k < present.length; k++) {
            if (present[k]) key.add(swatch(bookKindColour(DebtManager.BOOK_KINDS[k]), BOOK_KIND_NAMES[k], false));
        }
        return key;
    }

    /** WHERE EACH LINE SITS ON THE CURVE: today's zero curve at DebtManager.CURVE_TENORS, a dot a line at (months left, yield). */
    VBox deskCurveCard(BookDesk desk, DebtManager ledger) {
        // One curve, read ten times: ledger.curveRate(m) would build it ten times.
        YieldCurve curve = ledger.curve();
        int[] tenors = DebtManager.CURVE_TENORS;
        double[] zero = new double[tenors.length];
        for (int i = 0; i < tenors.length; i++) zero[i] = curve.zero(tenors[i]);
        List<DeskCurve.Dot> dots = new ArrayList<>();
        boolean[] present = new boolean[DebtManager.BOOK_KINDS.length];
        boolean dollars = false;
        for (BookDesk.Line l : desk.lines()) {
            Debt debt = l.debt();
            int kind = bookKindOf(debt.getType());
            present[kind] = true;
            dollars |= debt.isForeign();
            // A floater has no fixed yield and a linker's is real: neither is a dot on this chart.
            if (kind >= 3) continue;
            String name = pieceName(debt);
            dots.add(new DeskCurve.Dot(Math.max(1, l.monthsLeft()), l.yield(), bookKindColour(debt.getType()), debt.isForeign(),
                    name + " · yields " + pct2(l.yield()) + " · " + l.monthsLeft() + " months left",
                    debt instanceof LongTermBond && !debt.isForeign() ? name : null));
        }
        List<Node> key = new ArrayList<>();
        key.add(swatch(Palette.TEXT_LABEL, "today's curve", false));
        List<Node> kinds = kindKey(present);
        key.addAll(kinds);
        if (present[3]) key.add(muted("floating: off the chart, no fixed yield"));
        if (present[4]) key.add(muted("linked: off the chart, its yield is real"));
        if (dollars) key.add(swatch(Palette.TEXT_MUTED, "hollow: dollar paper, priced on the world's curve", true));
        return card(caption("WHERE EACH LINE SITS ON THE CURVE", DESK_CURVE_INFO), muted("today's yield, by what it has left"),
                new DeskCurve(tenors, zero, dots), keyRow(key.toArray(new Node[0])));
    }

    /** AT FACE, CARRIED, AND WORTH TODAY: three bars on one scale, each stacked by kind, and what the book is worth across all three. */
    VBox deskBarsCard(BookDesk desk) {
        int kinds = DebtManager.BOOK_KINDS.length;
        double[][] by = new double[3][kinds];
        boolean[] present = new boolean[kinds];
        for (int k = 0; k < kinds; k++) {
            String kind = DebtManager.BOOK_KINDS[k];
            by[0][k] = desk.faceOf(kind);
            by[1][k] = desk.carriedOf(kind);
            by[2][k] = desk.worthOf(kind);
            present[k] = by[0][k] > 0 || by[1][k] > 0 || by[2][k] > 0;
        }
        return card(caption("AT FACE, CARRIED, AND WORTH TODAY", DESK_BARS_INFO),
                muted("one scale · each bar by kind · the dashed line is the book's worth"),
                new DeskBars(by, desk.worth(), this::d), keyRow(kindKey(present).toArray(new Node[0])),
                cardLine("Carried less worth · what closing the book at fair would book", signedD(desk.ifClosedNow()), null),
                cardLine("Face less worth · what the market's price saves against what is owed", signedD(desk.vsFace()), null));
    }

    /** EVERY LINE: the desk, a row a line by when it falls due; a click on a row opens the line's page, and the ladder's columns and NEXT DUE scroll to its row. */
    VBox deskCard(BookDesk desk) {
        final int cols = 12;
        GridPane t = grid(new double[] {250, 58, 84, 64, 44, 58, 60, 60, 74, 58, 48, 80}, rightAfterFirst(cols));
        t.setHgap(4);
        t.setVgap(0);
        // The first column takes what a wide window leaves; a narrow one scrolls the table across (acrossWhenNarrow()).
        t.getColumnConstraints().get(0).setMaxWidth(Double.MAX_VALUE);
        t.getColumnConstraints().get(0).setHgrow(Priority.ALWAYS);
        gridHead(t, "Line", "Face", "Coupon", "Issue yield", "Price", "Yield", "Worth", "Carried", "If closed now", "vs face", "Duration", "Holders");
        // The headings are words, in the words' face: the figures' face is wider than a heading's column.
        for (Node head : t.getChildren()) if (head instanceof Label h) h.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        final String resting = "-fx-border-color: " + Palette.HAIRLINE + "; -fx-border-width: 0 0 1 0;";
        final String lit = resting + " -fx-background-color: " + Palette.CONTROL + ";";
        int row = 1;
        for (BookDesk.Line l : desk.lines()) {
            Debt debt = l.debt();
            String type = debt.getType();
            boolean linker = debt instanceof InflationLinkedBond, floater = debt instanceof FloatingRateNote;
            Region bg = new Region();
            bg.setStyle(resting);
            GridPane.setColumnSpan(bg, cols);
            t.add(bg, 0, row);
            targets.put(debt, bg);
            targets.putIfAbsent(CityCalendar.yearOf(debt.getMaturityMonth()), bg);

            List<Node> cells = new ArrayList<>();
            cells.add(lineCell(l));
            cells.add(gridCell(d(l.face()), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true));
            cells.add(gridCell(lineCoupon(l), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true));
            cells.add(stacked(pct2(l.bookedRate()), linker ? "real" : null));
            cells.add(gridCell(String.format("%.2f", l.pricePer100()), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true));
            Node yieldCell = linker ? stacked(pct2(l.realYield()), "real") : floater ? stacked(pct2(l.yield()), "expected")
                    : gridCell(pct2(l.yield()), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true);
            if (floater || linker) Tooltip.install(yieldCell, new Tooltip(floater
                    ? "A floater has no fixed yield: this is the return on the payments the market expects of it at today's price, the dial following its expected path."
                    : "A linked bond's real yield: the return over the price index. Its nominal yield is in its page."));
            cells.add(yieldCell);
            cells.add(gridCell(d(l.worth()), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true));
            cells.add(gridCell(d(l.carried()), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true));
            // No colour on a gain or a loss: the sign says which (the 0.7.32 spec's B6).
            cells.add(gridCell(signedD(l.ifClosedNow()), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true));
            cells.add(gridCell(signedD(l.vsFace()), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true));
            cells.add(gridCell(String.format("%.1f", l.duration()), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true));
            cells.add(segmentBar(holderParts(l), 1, null, 76, 6));
            for (int c = 0; c < cells.size(); c++) {
                Node cell = cells.get(c);
                GridPane.setMargin(cell, new javafx.geometry.Insets(5, 0, 5, 0));
                GridPane.setValignment(cell, javafx.geometry.VPos.CENTER);
                t.add(cell, c, row);
            }
            // The whole row is the door: every cell, and the ground under them, opens the line.
            List<Node> hot = new ArrayList<>(cells);
            hot.add(bg);
            for (Node h : hot) {
                h.setCursor(javafx.scene.Cursor.HAND);
                h.setOnMouseClicked(e -> openLine(debt));
                h.setOnMouseEntered(e -> bg.setStyle(lit));
                h.setOnMouseExited(e -> bg.setStyle(resting));
            }
            row++;
        }
        // The foot: the book, and its totals.
        Region rule = new Region();
        rule.setMinHeight(1);
        rule.setPrefHeight(1);
        rule.setMaxHeight(1);
        rule.setStyle("-fx-background-color: " + Palette.EDGE + ";");
        GridPane.setColumnSpan(rule, cols);
        t.add(rule, 0, row++);
        String[] foot = {"The book", d(desk.face()), d(desk.coupon()) + " a month", "", "", "", d(desk.worth()), d(desk.carried()),
                signedD(desk.ifClosedNow()), signedD(desk.vsFace()), "", ""};
        for (int c = 0; c < cols; c++) {
            if (foot[c].isEmpty()) continue;
            Label cell = gridCell(foot[c], Palette.TEXT_HEAD, Palette.SIZE_CAPTION, c > 0);
            GridPane.setMargin(cell, new javafx.geometry.Insets(5, 0, 2, 0));
            t.add(cell, c, row);
        }
        Node holdersKey = keyRow(muted("Holders:"), swatch(Palette.PEOPLE, "households", false), swatch(Palette.MONEY, "the bank", false),
                swatch(Palette.MONEY_DARK, "the central bank", false), swatch(Palette.BUSINESS_DARK, "the businesses", false),
                swatch(Palette.BUILDING_DARK, "the world", false));
        return card(caption("EVERY LINE", DESK_INFO, hint("by when it falls due · click a line for its page")), acrossWhenNarrow(t),
                holdersKey, muted("If closed now: the carrying value less the worth. Above nothing is a gain the statement would "
                        + "book buying the line back at today's fair price, before the premium a buyback pays."));
    }

    /**
     * A table that scrolls across when the window is narrower than it, and fills the card when it is wider. A scroll pane
     * asks its content how tall it wants to be before the content has wrapped a word, so the pane is told the height the
     * table laid out at (and room for its bar when it scrolls) and a wrapped row is never cut off the foot.
     */
    static Node acrossWhenNarrow(javafx.scene.layout.Region table) {
        javafx.scene.control.ScrollPane across = new javafx.scene.control.ScrollPane(table);
        across.setFitToWidth(true);
        across.setFitToHeight(true);
        across.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);
        across.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        across.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");
        Runnable fit = () -> {
            double bar = table.getWidth() > across.getViewportBounds().getWidth() + 1 ? 16 : 0;
            if (table.getHeight() > 0) across.setPrefHeight(table.getHeight() + bar + 2);
        };
        table.heightProperty().addListener((o, was, now) -> fit.run());
        table.widthProperty().addListener((o, was, now) -> fit.run());
        across.viewportBoundsProperty().addListener((o, was, now) -> fit.run());
        return across;
    }

    /** The desk's first cell: the kind's tag, the line's name and, muted under them, its term, when it was sold, what it has left, and whether it was reopened or can be called. */
    Node lineCell(BookDesk.Line l) {
        Debt debt = l.debt();
        Label name = new Label(pieceName(debt));
        name.setStyle(Palette.strong(Palette.SIZE_BODY, Palette.TEXT_HEAD));
        name.setMinWidth(Region.USE_PREF_SIZE);
        // The tag and the name wrap, the name to a second line where they do not fit the column together.
        javafx.scene.layout.FlowPane top = new javafx.scene.layout.FlowPane(Palette.GAP, 2,
                tag((debt.isForeign() ? "US$ " : "") + kindTag(debt.getType()), bookKindColour(debt.getType())), name);
        top.setAlignment(Pos.CENTER_LEFT);
        List<String> parts = new ArrayList<>();
        parts.add(termWords(debt.getDuration()));
        parts.add("sold " + CityCalendar.formatShort(debt.getMonthStarted()));
        parts.add(l.monthsLeft() + (l.monthsLeft() == 1 ? " month left" : " months left"));
        if (debt.getSales().size() > 1) parts.add("reopened");
        if (debt instanceof LongTermBond bond && bond.isCallable()) {
            parts.add("call from " + CityCalendar.formatShort(bond.getFirstCallMonth()));
        }
        // A phrase at a time, so the line wraps between phrases and never inside one.
        javafx.scene.layout.FlowPane sub = new javafx.scene.layout.FlowPane(0, 0);
        for (int i = 0; i < parts.size(); i++) {
            Label part = new Label(parts.get(i) + (i < parts.size() - 1 ? " · " : ""));
            part.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
            part.setMinWidth(Region.USE_PREF_SIZE);
            sub.getChildren().add(part);
        }
        return new VBox(2, top, sub);
    }

    /** A line's coupon in words (CD6): a bill "discount", a floater "policy + 18 bp", a linker "1⅝% real", anything else its coupon in eighths. */
    static String lineCoupon(BookDesk.Line l) {
        return switch (l.debt().getType()) {
            case "NOTE"   -> "discount";
            case "FLOAT"  -> marginWords(l.couponRate());
            case "LINKED" -> couponWords(l.couponRate()) + " real";
            default       -> couponWords(l.couponRate());
        };
    }

    /** A table figure with a muted word under it, right-aligned: "3.84%" over "real". */
    static Node stacked(String figure, String under) {
        Label top = gridCell(figure, Palette.TEXT_BODY, Palette.SIZE_CAPTION, true);
        if (under == null) return top;
        Label low = new Label(under);
        low.setStyle(Palette.words(Palette.SIZE_CAPTION - 1, Palette.TEXT_MUTED));
        VBox both = new VBox(0, top, low);
        both.setAlignment(Pos.CENTER_RIGHT);
        return both;
    }

    /** A line's holders as shares of its face, in the holders bar's colours: the households, the bank, the central bank, the businesses and the world. */
    static List<Segment> holderParts(BookDesk.Line l) {
        return List.of(
                seg(l.households(), Palette.PEOPLE, "households " + pc(l.households())),
                seg(l.bank(), Palette.MONEY, "the bank " + pc(l.bank())),
                seg(l.centralBank(), Palette.MONEY_DARK, "the central bank " + pc(l.centralBank())),
                seg(l.firms(), Palette.BUSINESS_DARK, "the businesses " + pc(l.firms())),
                seg(l.world(), Palette.BUILDING_DARK, "the world " + pc(l.world())));
    }

    /**
     * A coupon in eighths of a point, the way a bond is quoted: 0.035 is
     * "3½%", 0.04125 "4⅛%", 0.00125 "⅛%". A coupon that is not a whole eighth
     * (a dollar bond's, the world's) in hundredths, "4.21%". Pure.
     */
    static String couponWords(double coupon) {
        double eighths = coupon * 100 * 8;
        if (!(eighths >= 0) || Math.abs(eighths - Math.rint(eighths)) > 1e-6) return pct2(coupon);
        long e = Math.round(eighths);
        long whole = e / 8;
        int part = (int) (e % 8);
        String fraction = part == 0 ? "" : String.valueOf("⅛¼⅜½⅝¾⅞".charAt(part - 1));
        return (whole == 0 && part != 0 ? "" : String.valueOf(whole)) + fraction + "%";
    }

    /**
     * A piece's name in words (CD2): a bond at home is its line, "3½% Jan
     * 2211" - its coupon in eighths and the month and year it falls due, as
     * a treasury names a line -; a bill "6-month bill"; an amortizing bond
     * "20-year amortizing bond at 4.21%"; dollar paper "10-year bond in
     * dollars". A floating-rate note (CD5) is "floating, policy + 18 bp, Jan
     * 2205": its margin over the dial and the month it falls due; an
     * inflation-linked bond "1⅝% linked Jan 2215": its real coupon and its
     * date, as a line is named.
     */
    static String pieceName(Debt debt) {
        int months = debt.getDuration();
        int years = Math.max(1, Math.round(months / 12f));
        if (!debt.isForeign() && debt instanceof LongTermBond line) {
            return couponWords(line.couponRate()) + " " + CityCalendar.formatShort(debt.getMaturityMonth());
        }
        if (!debt.isForeign() && debt instanceof FloatingRateNote note) {
            return "floating, " + marginWords(note.getMargin()) + ", " + CityCalendar.formatShort(debt.getMaturityMonth());
        }
        if (!debt.isForeign() && debt instanceof InflationLinkedBond linked) {
            return couponWords(linked.getRealCoupon()) + " linked " + CityCalendar.formatShort(debt.getMaturityMonth());
        }
        String what = switch (debt.getType()) {
            case "NOTE"   -> months + "-month bill";
            case "SERIAL" -> years + "-year amortizing bond"
                    + (!debt.isForeign() && debt instanceof MediumTermBond slices ? " at " + pct2(slices.couponRate()) : "");
            case "FLOAT"  -> years + "-year floating-rate note";
            case "LINKED" -> years + "-year inflation-linked bond";
            default       -> years + "-year bond";
        };
        return what + (debt.isForeign() ? " in dollars" : "");
    }

    /** Who holds a piece, one bar (the card's, and the Close it now page's): the households, the bank, the central bank, the businesses and the world; "held abroad" for a dollar piece. */
    Node holdersBar(Debt debt) {
        if (debt.isForeign()) return muted("held abroad");
        double p = debt.getOustandingPrincipal();
        return segmentBar(List.of(
                seg(debt.householdPrincipal(), Palette.PEOPLE, "households " + d(debt.householdPrincipal())),
                seg(debt.bankPrincipal(), Palette.MONEY, "the bank " + d(debt.bankPrincipal())),
                seg(debt.centralBankPrincipal(), Palette.MONEY_DARK, "the central bank " + d(debt.centralBankPrincipal())),
                seg(debt.firmPrincipal(), Palette.BUSINESS_DARK, "the businesses " + d(debt.firmPrincipal())),
                seg(debt.worldPrincipal(), Palette.BUILDING_DARK, "the world " + d(debt.worldPrincipal()))),
                p > 0 ? p : 1, null, 0, 6);
    }

    /* ----- a line's page (CD6, board 2) ----- */

    /*
       A LINE'S PAGE (CD6, spec-city-debt.md section 7, board 2; D31).

       WHAT IT WAS: the book was a card a piece, each with its own Close it now, and a table behind a fold. The
       desk is the table now and a line's page is the card: a click on a desk row, or on a row of NEXT DUE, opens
       it, as the card's button opened Close it now (openClose(), which a line's page offers in its turn).

         ITS HEAD       the kind, the line's name, its term, when it was sold and falls due.
         ITS FIGURES    the coupon, the yield it was sold at, its price and yield today, its worth and what it is
                        carried at; below them the coupon's own lines where the kind has any and the books' line.
         ITS PRICE      the price per 100 of face since the first month it was on the books, against what it was
                        carried at: carried creeps to 100 and the price does not.
         IF YOU CLOSED  worth, carried, the gain or loss against carrying, against face, what closing all of it on
         IT TODAY       offer would pay at the reverse auction, and the honest line; Close it now and Call it.
         THE BOOKS      the next six months of effective interest: opening, interest, coupon, amortised, repaid
                        and closing, the table that turns the price paid at the sale into the face.
         WHO HOLDS IT, ITS SALES AND ITS RISK.

       Every figure is the model's (BookDesk.line(), Debt.booksAhead(), the price and carried paths, the sales,
       Game.quoteBuyback() and honestLine()); the page works out none, and colours no gain or loss.
     */

    /** THE PRICE card's (i): board 2, note A. */
    static final String LINE_PRICE_INFO = "The price is the market's: what the line's remaining payments are worth on "
            + "each month's curve, per 100 of what is owed. What it is carried at is the books': what it raised, with the "
            + "issue discount amortised month by month at the effective rate, so it creeps to 100 by the day the line "
            + "falls due, while the price follows the curve. Both are recorded at each month's close, from the month "
            + "after the sale.";

    /** IF YOU CLOSED IT TODAY's (i): board 2, note B. */
    static final String LINE_CLOSE_INFO = "Worth is what closing the line would cost at its fair price; a buyback pays "
            + "more, since every holder offers at or over fair, and the reverse auction below prices what it really "
            + "would. The gain or loss is the statement's: the carrying value less what it costs. It is real on the "
            + "statement, but the cash to pay for it costs today's rate - the honest line says both.";

    /** THE NEXT SIX MONTHS' (i): board 2, note C. */
    static final String LINE_BOOKS_INFO = "The books in full: what the line is carried at when the month opens, the "
            + "interest a month of it at the effective rate, the coupon paid, the discount amortised (the interest less "
            + "the coupon), the principal repaid with the payment and what it is carried at when the month closes - "
            + "amortised cost, the way a ledger shows it. A floater's and a linker's rows hold today's dial and today's "
            + "index flat: their coupons would move with them.";

    /** ITS SALES, AND ITS RISK's (i). */
    static final String LINE_SALES_INFO = "Each sale into the line: when, how much face, what it raised, the price it "
            + "made for each 100 and the yield it cleared at - a reopened line has several. A point on the curve is "
            + "what the line's worth falls by if every rate on its curve rises a point; its duration is that over its "
            + "worth and the point, in years.";

    /** The name of a line's page, under The book: no chip's, open while its line is on the books (lineOpenNow()). */
    static final String LINE_PAGE = "A line";

    /** The line a line's page is open on (CD6), by identity; null while none has been. */
    private Debt lineOpen;

    /** Whether Close it now was opened from the line's page, so its back door returns there. */
    private boolean closeFromLine;

    /** True while a line's page is open on a line that is still on the books; else the book is drawn in its place. */
    boolean lineOpenNow() {
        return "The book".equals(financeArea) && LINE_PAGE.equals(financePage) && lineOpen != null
                && ui.game.getDebtManager().getDebt().contains(lineOpen);
    }

    /** A desk row's, or NEXT DUE's, door: the page of this line. */
    void openLine(Debt debt) {
        lineOpen = debt;
        closeFromLine = false;
        open("The book", LINE_PAGE, null);
    }

    /** A line named by its term and kind: "10-year bond", "6-month bill", "2-year floating-rate note", "5-year bond in dollars". */
    static String kindNoun(Debt debt) {
        String what = switch (debt.getType()) {
            case "NOTE"   -> "bill";
            case "SERIAL" -> "amortizing bond";
            case "FLOAT"  -> "floating-rate note";
            case "LINKED" -> "inflation-linked bond";
            default       -> "bond";
        };
        return termWords(debt.getDuration()) + " " + what + (debt.isForeign() ? " in dollars" : "");
    }

    /** A gain or a loss, in words, against what a line is carried at: "a gain of D$1.2M", "a loss of D$3k", or "no gain or loss". No colour (the 0.7.32 spec's B6). */
    String gainWords(double gain) {
        return Math.abs(gain) < .5 ? "no gain or loss" : (gain > 0 ? "a gain of " : "a loss of ") + d(Math.abs(gain));
    }

    /**
     * THE HONEST LINE's sentence (CD6, spec-city-debt.md 6.3), for a buyback of this quote: what it stops a month of
     * coupon and what the price paid, borrowed again at today's rate for the months the line has left, would cost a
     * month. Close it now's page and a line's page both say it, from the model's Game.honestLine().
     */
    String honestSentence(Debt debt, Buyback.Result q) {
        Buyback.HonestLine hl = ui.game.honestLine(debt, q);
        double gain = q.gainOnCarrying();
        double stopped = hl.couponStopped();
        double again = hl.costAgain();
        String stops = stopped >= .5 ? "It stops " + d(stopped) + " a month of coupon; "
                : "It has no coupon to stop - a bill's interest is the discount wearing off; ";
        return stops + d(q.paid()) + " borrowed again at today's " + termWords(hl.months()) + " rate, "
                + pct2(hl.rate()) + ", would cost " + d(again) + " a month."
                + (stopped >= .5 && Math.abs(again - stopped) >= .5
                ? " So it books " + (gain >= 0 ? "a gain" : "a loss") + " and "
                        + (again > stopped ? "raises" : "lowers") + " the city's interest by "
                        + d(Math.abs(again - stopped)) + " a month." : "");
    }

    /** A line's page (CD6, board 2): see the banner above. */
    void linePage(VBox page) {
        Game g = ui.game;
        DebtManager ledger = g.getDebtManager();
        Debt debt = lineOpen;
        int month = g.getMonth();
        int due = debt.getMaturityMonth(), started = debt.getMonthStarted();
        // The market struck once (Game.bookLine()), for the line's figures and for the quote below.
        BookDesk.Line l = g.bookLine(debt);
        boolean linker = debt instanceof InflationLinkedBond, floater = debt instanceof FloatingRateNote;
        String colour = bookKindColour(debt.getType());

        // ----- the head -----
        Label name = words(pieceName(debt), Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD);
        name.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        HBox top = new HBox(Palette.GAP, tag((debt.isForeign() ? "US$ " : "") + kindTag(debt.getType()), colour), name);
        top.setAlignment(Pos.CENTER_LEFT);
        Label dueOn = new Label(CityCalendar.formatShort(due));
        dueOn.setStyle(Palette.words(Palette.SIZE_LABEL, urgency(due - month)));
        dueOn.setMinWidth(Region.USE_PREF_SIZE);
        HBox when = new HBox(0, muted(kindNoun(debt) + " · sold " + CityCalendar.formatShort(started) + " · falls due "), dueOn,
                muted(" · " + l.monthsLeft() + (l.monthsLeft() == 1 ? " month left" : " months left")));
        when.setAlignment(Pos.CENTER_LEFT);
        page.getChildren().add(card(backDoor("The book", () -> open("The book", null, lineOpen)), top, when));

        // ----- its five figures -----
        double coupon = debt.getMonthlyCoupon();
        String yieldNote = linker ? "real yield today" : floater ? "yield today, as the market expects" : "yield today";
        String yieldNow = linker ? pct2(l.realYield()) : pct2(l.yield());
        page.getChildren().add(figureStrip(new String[][] {
                {"COUPON", lineCoupon(l), coupon >= .5 ? d(coupon) + " a month" : "none: the discount wears off as interest"},
                {"ISSUE YIELD", pct2(l.bookedRate()) + (linker ? " real" : ""), "the effective rate the books charge"},
                {"PRICE · YIELD", String.format("%.2f", l.pricePer100()) + " · " + yieldNow, "per 100 of face · " + yieldNote},
                {"WORTH TODAY", d(l.worth()), "on today's curve"},
                {"CARRIED", d(l.carried()), String.format("%.2f", l.carriedPer100()) + " per 100"}}));

        // ----- the paper's own lines: the coupon's, the books', the call's -----
        VBox terms = new VBox(4);
        terms.setStyle("-fx-padding: 0 4 0 4;");
        if (debt instanceof FloatingRateNote note) {
            terms.getChildren().add(infoLine("coupon " + d(coupon) + " a month · the policy rate " + dialPlusMargin(note.getIndex(), note.getMargin())
                    + ", reset monthly", FLOATING_INFO, true, Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL, 560));
        } else if (debt instanceof InflationLinkedBond linked) {
            terms.getChildren().add(infoLine("coupon " + d(coupon) + " a month · real " + couponWords(linked.getRealCoupon()) + " on "
                    + d(linked.getIndexedPrincipal()) + " indexed (the index ×" + String.format("%.3f", linked.getRatio())
                    + " since the sale)", INDEXED_INFO, true, Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL, 560));
            terms.getChildren().add(line("uplift last month " + d(linked.getLastUplift()) + " · repays " + d(linked.owedAtMaturity())
                    + " at today's index"
                    + (linked.getUpliftDue() >= .5 ? " · next month pays the uplift, " + d(linked.getUpliftDue()) + ", with the face" : "")));
        } else if (coupon < .5) {
            terms.getChildren().add(noteLine("no coupon - the discount wears off as interest", NOTE_INFO, 560));
        }
        terms.getChildren().add(linker
                ? infoLine("carried " + d(l.carried()) + " · effective " + pct2(debt.getEffectiveRate()) + " real",
                        LINKED_BOOKS_INFO, true, Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL, 560)
                : infoLine("carried " + d(l.carried()) + " · effective " + pct2(debt.getEffectiveRate()),
                        CARRIED_INFO, true, Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL, 560));
        if (debt instanceof LongTermBond bond && bond.isCallable()) {
            terms.getChildren().add(infoLine("callable at " + String.format("%.0f", CallTree.CALL_PRICE) + " from "
                            + CityCalendar.formatShort(bond.getFirstCallMonth()) + (g.canCall(debt) ? " · callable now" : ""),
                    CALLABLE_INFO, true, Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL, 560));
            if (g.worthCalling(debt)) {
                terms.getChildren().add(infoLine("Worth calling: saves " + d(g.callSaving(debt)),
                        WORTH_CALLING_INFO, true, Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL, 560));
            }
        }
        page.getChildren().add(terms);

        // ----- its price since it was sold, and what closing it today would book -----
        double[] price = debt.getPricePath(), carried = debt.getCarriedPath();
        VBox priceCard = card(caption("ITS PRICE SINCE IT WAS SOLD", LINE_PRICE_INFO), muted("per 100 of face · and what it is carried at"));
        if (price.length >= 2) {
            priceCard.getChildren().addAll(new PathChart(price, carried, debt.getPathFirstMonth()),
                    keyRow(swatch(Palette.MONEY, "price, at the curve each month", false),
                            swatch(Palette.TEXT_LABEL, "carried, per 100", false),
                            swatch(Palette.TEXT_SPENT, "par, 100", false)));
        } else {
            priceCard.getChildren().add(line(price.length == 0
                    ? "Sold this month: nothing is recorded yet. The first point comes when the month closes."
                    : "One month on the books so far."));
        }
        priceCard.getChildren().add(muted("The price is drawn from the line's second month on the books: a month closes before it is recorded."
                + (price.length >= 2 ? "" : " Two points make a line.")));

        Buyback.Result whole = g.quoteBuyback(debt, g.buybackOfferable(debt), Double.NaN);
        boolean taken = whole.takenFace() > 0 && whole.paid() > 0;
        double against = l.vsFace();
        String againstWords = Math.abs(against) < .5 ? "at its face" : against > 0 ? d(against) + " under face" : "a premium of " + d(-against);
        VBox close = new VBox(8, caption("IF YOU CLOSED IT TODAY", LINE_CLOSE_INFO),
                cardLine("Worth today · the fair price", d(l.worth()), null),
                cardLine("Carried · what the books hold it at", d(l.carried()), null),
                cardLine("Carried less worth · what the books would show", gainWords(l.ifClosedNow()), null),
                cardLine("Against its face of " + d(l.face()), againstWords, null, PREMIUM_INFO));
        if (taken) {
            close.getChildren().addAll(
                    cardLine("All of it on offer, " + d(whole.takenFace()) + ", at the reverse auction now", d(whole.paid()), null),
                    cardLine("...its gain against carrying", gainWords(whole.gainOnCarrying()), null),
                    words(honestSentence(debt, whole), Palette.SIZE_LABEL + 1, Palette.TEXT_BODY));
        } else {
            close.getChildren().add(muted("Nothing is on offer now: the central bank does not sell (D18), and a line sold lately can only be bought back whole."));
        }
        boolean affordable = taken && whole.paid() <= g.getCash();
        Button closeIt = new Button("Close it now…");
        closeIt.setMinWidth(Region.USE_PREF_SIZE);
        if (affordable) closeIt.setStyle("-fx-background-color: " + Palette.CONFIRM + "; -fx-text-fill: white;");
        else if (taken) closeIt.setTooltip(new Tooltip("Closing all of it costs " + d(whole.paid()) + "; the treasury has " + d(g.getCash())
                + ". The page can close part of it."));
        closeIt.setOnAction(e -> openClose(debt));
        boolean callable = debt instanceof LongTermBond bond && bond.isCallable();
        Button callIt = new Button(callable ? "Call it…" : "Call it · not callable");
        callIt.setMinWidth(Region.USE_PREF_SIZE);
        callIt.setDisable(!callable);
        // Close it now is where the call is: the page that holds the call card.
        callIt.setOnAction(e -> openClose(debt));
        HBox buttons = new HBox(Palette.GAP, closeIt, callIt);
        buttons.setAlignment(Pos.CENTER_LEFT);
        close.getChildren().add(buttons);
        close.setStyle("-fx-padding: 14; -fx-background-color: " + Palette.PINNED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + Palette.ACCENT_FILL + ";");
        page.getChildren().add(pair(priceCard, close));

        // ----- the books, who holds it, its sales and its risk -----
        // Three cards side by side - the books' table the half of the row, the others a quarter each - and, in a window
        // narrower than a table of the books and two cards, the table across the top and the two under it.
        GridPane three = equalColumns(12, TILE_GAP);
        VBox books = booksCard(debt, month, l);
        VBox holders = holdersCard(debt, l);
        VBox sales = salesCard(debt, l);
        for (VBox c : List.of(books, holders, sales)) GridPane.setFillHeight(c, true);
        three.getChildren().addAll(books, holders, sales);
        arrangeThree(books, holders, sales, false);
        three.widthProperty().addListener((o, was, now) -> arrangeThree(books, holders, sales, now.doubleValue() < THREE_NARROW));
        page.getChildren().add(three);
    }

    /** The width under which a line's page stacks its three cards (CD6): the books' table, then the other two side by side. */
    static final double THREE_NARROW = 1000;

    /** The three cards of a line's page on a grid of twelve columns: 6, 3 and 3 across, or, narrow, the first over the other two. */
    static void arrangeThree(VBox books, VBox holders, VBox sales, boolean narrow) {
        GridPane.setConstraints(books, 0, 0, narrow ? 12 : 6, 1);
        GridPane.setConstraints(holders, narrow ? 0 : 6, narrow ? 1 : 0, narrow ? 6 : 3, 1);
        GridPane.setConstraints(sales, narrow ? 6 : 9, narrow ? 1 : 0, narrow ? 6 : 3, 1);
    }

    /** THE NEXT SIX MONTHS ON THE BOOKS: Debt.booksAhead(6) - opening, interest, coupon, amortised, repaid (only when any is) and closing. */
    VBox booksCard(Debt debt, int month, BookDesk.Line l) {
        boolean linker = debt instanceof InflationLinkedBond;
        List<Debt.BooksMonth> ahead = debt.booksAhead(6);
        VBox card = card(caption("THE NEXT SIX MONTHS ON THE BOOKS", LINE_BOOKS_INFO),
                muted("effective interest at " + pct2(debt.getEffectiveRate()) + (linker ? " real" : "")
                        + (debt instanceof FloatingRateNote || linker ? " · today's " + (linker ? "index" : "dial") + " held flat" : "")));
        if (ahead.isEmpty()) {
            card.getChildren().add(muted("Nothing is left to book."));
            return card;
        }
        boolean repaid = false;
        for (Debt.BooksMonth b : ahead) repaid |= b.repaid() >= .5;
        GridPane t = repaid
                ? grid(new double[] {52, 62, 52, 52, 60, 56, 62}, rightAfterFirst(7))
                : grid(new double[] {52, 66, 56, 56, 64, 66}, rightAfterFirst(6));
        t.setHgap(4);
        if (repaid) gridHead(t, "Month", "Opening", "Interest", "Coupon", "Amortised", "Repaid", "Closing");
        else gridHead(t, "Month", "Opening", "Interest", "Coupon", "Amortised", "Closing");
        for (Node head : t.getChildren()) if (head instanceof Label h) h.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        int row = 1;
        for (Debt.BooksMonth b : ahead) {
            int c = 0;
            t.add(gridCell(CityCalendar.formatShort(month + b.ahead()), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, false), c++, row);
            t.add(gridCell(d(b.opening()), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), c++, row);
            t.add(gridCell(d(b.interest()), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), c++, row);
            t.add(gridCell(d(b.coupon()), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), c++, row);
            t.add(gridCell(signedD(b.amortised()), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), c++, row);
            if (repaid) t.add(gridCell(d(b.repaid()), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), c++, row);
            t.add(gridCell(d(b.closing()), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), c, row);
            row++;
        }
        card.getChildren().add(acrossWhenNarrow(t));
        return card;
    }

    /** WHO HOLDS IT: the holders bar and a line a holder with its face and its share. */
    VBox holdersCard(Debt debt, BookDesk.Line l) {
        VBox card = card(caption("WHO HOLDS IT", null), holdersBar(debt));
        if (debt.isForeign()) {
            card.getChildren().add(holderLine("the world", Palette.BUILDING_DARK, usd(debt.principalInCurrency()) + " · all of it"));
            card.getChildren().add(muted("Dollar paper: nobody at home holds it."));
            return card;
        }
        Object[][] who = {
                {"households", Palette.PEOPLE, debt.householdPrincipal(), l.households()},
                {"the bank", Palette.MONEY, debt.bankPrincipal(), l.bank()},
                {"the central bank", Palette.MONEY_DARK, debt.centralBankPrincipal(), l.centralBank()},
                {"the businesses", Palette.BUSINESS_DARK, debt.firmPrincipal(), l.firms()},
                {"the world", Palette.BUILDING_DARK, debt.worldPrincipal(), l.world()}};
        for (Object[] w : who) {
            // A holder with nothing of it is no line (a share that rounds to nothing is none).
            if ((double) w[2] < .5) continue;
            card.getChildren().add(holderLine((String) w[0], (String) w[1], d((double) w[2]) + " · " + pc((double) w[3])));
        }
        if (debt.centralBankPrincipal() > 0) {
            card.getChildren().add(muted("The central bank's share follows its holdings dial, and it does not sell (D18)."));
        }
        return card;
    }

    /** One holder's line: its swatch and name at the left, its face and its share at the right. */
    static HBox holderLine(String who, String colour, String text) {
        HBox key = swatch(colour, who, false);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox row = new HBox(Palette.GAP, key, gap, figure(text, Palette.SIZE_LABEL + 1, Palette.TEXT_HEAD));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** ITS SALES, AND ITS RISK: each sale as it was made, then its duration and the value of a point. */
    VBox salesCard(Debt debt, BookDesk.Line l) {
        VBox card = card(caption("ITS SALES, AND ITS RISK", LINE_SALES_INFO));
        boolean dollars = debt.isForeign();
        List<Debt.Sale> sales = debt.getSales();
        if (sales.isEmpty()) {
            card.getChildren().add(muted("Not sold at auction: no sale is recorded for it, as the founding's bond has none."));
        }
        for (Debt.Sale s : sales) {
            String face = dollars ? usd(s.getFace()) : d(s.getFace());
            String raised = dollars ? usd(s.getProceeds()) : d(s.getProceeds());
            card.getChildren().add(new VBox(1,
                    words(CityCalendar.formatShort(s.getMonth()) + " · " + s.getHow() + (dollars ? " · in dollars" : ""),
                            Palette.SIZE_LABEL + 1, Palette.TEXT_HEAD),
                    muted(face + " sold, " + raised + " raised · " + String.format("%.2f", s.getPricePer100()) + " per 100 · yield "
                            + pct2(s.getYield()))));
        }
        card.getChildren().add(cardLine("Duration", String.format("%.2f years", l.duration()), null));
        card.getChildren().add(cardLine("A point on the curve moves it", d(l.valueOfAPoint()), null));
        if (debt instanceof FloatingRateNote) {
            card.getChildren().add(muted("A floater's coupon follows the dial, so a point moves it only until its next reset."));
        } else if (debt instanceof LongTermBond bond && bond.isCallable()) {
            card.getChildren().add(muted("A callable line is priced as a bond that cannot be called: the right to call is not repriced."));
        }
        return card;
    }

    /* ----- the desk's pictures (CD6) ----- */

    /**
     * A picture that draws itself again at whatever width it is given and a fixed height (BuybackLadder's way): the
     * desk's curve and bars and a line's price path sit in half a page, at 900 px or at 1,480.
     */
    abstract static class Drawn extends javafx.scene.layout.Pane {
        private final double first;
        private double drawnAt;

        Drawn(double least, double first, double height) {
            this.first = first;
            setMinSize(least, height);
            setPrefSize(first, height);
            setMaxSize(Double.MAX_VALUE, height);
        }

        /** Everything in the picture, drawn at this width: the children have been cleared. */
        abstract void draw(double w);

        /** The first drawing, at the width the picture asks for; a subclass calls it once its figures are set. */
        final void drawFirst() {
            drawnAt = first;
            getChildren().clear();
            draw(first);
        }

        @Override protected void layoutChildren() {
            double w = Math.max(getMinWidth(), getWidth());
            if (Math.abs(w - drawnAt) > .5) {
                drawnAt = w;
                getChildren().clear();
                draw(w);
            }
            super.layoutChildren();
        }

        /** Words at a place, their top left at (x, y); in a box `width` wide, aligned, when width is above nothing. */
        final Label put(String text, double x, double y, double width, Pos align, String style) {
            Label l = new Label(text);
            l.setStyle(style);
            if (width > 0) {
                l.setMinWidth(width);
                l.setPrefWidth(width);
                l.setMaxWidth(width);
                l.setAlignment(align);
            }
            l.relocate(x, y);
            getChildren().add(l);
            return l;
        }

        /** The caption's words in a tone. */
        static String wordsIn(String tone) { return Palette.words(Palette.SIZE_CAPTION, tone); }

        /** A figure's, in a tone. */
        static String figureIn(String tone) { return Palette.figure(Palette.SIZE_CAPTION, tone); }

        /** A straight stroke. */
        final javafx.scene.shape.Line stroke(double x1, double y1, double x2, double y2, String colour, double width, double... dash) {
            javafx.scene.shape.Line s = new javafx.scene.shape.Line(x1, y1, x2, y2);
            s.setStroke(javafx.scene.paint.Color.web(colour));
            s.setStrokeWidth(width);
            for (double d : dash) s.getStrokeDashArray().add(d);
            getChildren().add(s);
            return s;
        }

        /** A tooltip that shows at once. */
        static Tooltip tip(String text) {
            Tooltip t = new Tooltip(text);
            t.setShowDelay(Duration.millis(150));
            return t;
        }

        /** A round number to put the axis's steps on: the smallest of these that fits the span into `ticks` steps or fewer. */
        static double niceStep(double span, int ticks, double... steps) {
            double pick = steps[steps.length - 1];
            for (double s : steps) {
                if (span / s <= ticks) {
                    pick = s;
                    break;
                }
            }
            return pick;
        }
    }

    /**
     * WHERE EACH LINE SITS ON THE CURVE (CD6, board 1): today's zero curve at the tenors, months across (to the square
     * root, as the board draws it, so the short end has room) and the yield up; each line a dot at (months left,
     * yield) in its kind's colour, hollow for dollar paper; a bond named beside its dot where the name does not
     * collide with another dot or name.
     */
    static final class DeskCurve extends Drawn {
        /** One line on the curve: its months left, its yield, its colour, whether it is dollar paper (hollow), what its tooltip says, and its name beside the dot (null: none). */
        record Dot(double months, double yield, String colour, boolean hollow, String tip, String name) { }

        static final double H = 232, LEFT = 46, RIGHT = 14, TOP = 12, FOOT = 26;

        private final int[] tenors;
        private final double[] zero;
        private final List<Dot> dots;

        DeskCurve(int[] tenors, double[] zero, List<Dot> dots) {
            super(300, 560, H);
            this.tenors = tenors;
            this.zero = zero;
            this.dots = dots;
            drawFirst();
        }

        private static String tenorName(int months) {
            return months < 12 ? months + "m" : months / 12 + "y";
        }

        private static boolean overlaps(double[] box, List<double[]> taken) {
            for (double[] t : taken) {
                if (box[0] < t[2] && box[2] > t[0] && box[1] < t[3] && box[3] > t[1]) return true;
            }
            return false;
        }

        @Override void draw(double w) {
            final double plotL = LEFT, plotR = Math.max(LEFT + 100, w - RIGHT), plotT = TOP, plotB = H - FOOT;
            final double maxMonths = tenors[tenors.length - 1];
            double lo = Double.MAX_VALUE, hi = -Double.MAX_VALUE;
            for (double v : zero) { lo = Math.min(lo, v); hi = Math.max(hi, v); }
            for (Dot dot : dots) { lo = Math.min(lo, dot.yield()); hi = Math.max(hi, dot.yield()); }
            double step = niceStep(hi - lo, 5, .0025, .005, .01, .02, .05);
            lo = Math.floor(lo / step + 1e-9) * step;
            hi = Math.ceil(hi / step - 1e-9) * step;
            if (hi - lo < step) hi = lo + step;
            final double bottom = lo, span = hi - lo;
            java.util.function.DoubleUnaryOperator y = v -> plotB - (v - bottom) / span * (plotB - plotT);
            java.util.function.DoubleUnaryOperator x = m -> plotL + Math.sqrt(Math.max(0, m) / maxMonths) * (plotR - plotL);

            for (double v = lo; v <= hi + 1e-9; v += step) {
                stroke(plotL, y.applyAsDouble(v), plotR, y.applyAsDouble(v), Palette.EDGE, 1);
                put(String.format(step < .005 ? "%.2f%%" : "%.1f%%", v * 100), 0, y.applyAsDouble(v) - 7, LEFT - 6, Pos.CENTER_RIGHT,
                        figureIn(Palette.TEXT_MUTED));
            }
            stroke(plotL, plotB, plotR, plotB, Palette.TEXT_SPENT, 1);
            // The axis: every tenor has its tick, and the labels claim room round ones first.
            List<Double> labelled = new ArrayList<>();
            int[] first = {12, 60, 120, 360, 6, 24, 240, 3, 480, 600};
            for (int months : tenors) stroke(x.applyAsDouble(months), plotB, x.applyAsDouble(months), plotB + 4, Palette.TEXT_SPENT, 1);
            for (int want : first) {
                for (int months : tenors) {
                    if (months != want) continue;
                    double px = x.applyAsDouble(months);
                    boolean room = true;
                    for (double at : labelled) if (Math.abs(at - px) < 26) room = false;
                    if (!room) continue;
                    labelled.add(px);
                    put(tenorName(months), px - 15, plotB + 6, 30, Pos.CENTER, wordsIn(Palette.TEXT_MUTED));
                }
            }
            // Today's curve.
            javafx.scene.shape.Polyline curve = new javafx.scene.shape.Polyline();
            curve.setStroke(javafx.scene.paint.Color.web(Palette.TEXT_LABEL));
            curve.setStrokeWidth(2);
            getChildren().add(curve);
            for (int i = 0; i < tenors.length; i++) {
                double px = x.applyAsDouble(tenors[i]), py = y.applyAsDouble(zero[i]);
                curve.getPoints().addAll(px, py);
                javafx.scene.shape.Circle mark = new javafx.scene.shape.Circle(px, py, 2.5, javafx.scene.paint.Color.web(Palette.TEXT_LABEL));
                Tooltip.install(mark, tip(tenorName(tenors[i]) + " zero rate: " + pct2(zero[i])));
                getChildren().add(mark);
            }
            // The lines.
            List<double[]> taken = new ArrayList<>();
            for (Dot dot : dots) {
                double px = x.applyAsDouble(dot.months()), py = y.applyAsDouble(dot.yield());
                javafx.scene.shape.Circle c = new javafx.scene.shape.Circle(px, py, 4.5);
                javafx.scene.paint.Color colour = javafx.scene.paint.Color.web(dot.colour());
                if (dot.hollow()) {
                    c.setFill(javafx.scene.paint.Color.TRANSPARENT);
                    c.setStroke(colour);
                    c.setStrokeWidth(2);
                } else {
                    c.setFill(colour);
                    c.setStroke(javafx.scene.paint.Color.web(Palette.RAISED));
                    c.setStrokeWidth(1.5);
                }
                Tooltip.install(c, tip(dot.tip()));
                getChildren().add(c);
                taken.add(new double[] {px - 6, py - 6, px + 6, py + 6});
            }
            // The bonds' names, beside their dots where they do not collide.
            for (Dot dot : dots) {
                if (dot.name() == null) continue;
                double px = x.applyAsDouble(dot.months()), py = y.applyAsDouble(dot.yield());
                double tw = dot.name().length() * 5.3 + 4, th = 12;
                double[][] tries = {{px + 8, py - th - 1}, {px + 8, py + 1}, {px - 8 - tw, py - th - 1}, {px - 8 - tw, py + 1}};
                for (double[] at : tries) {
                    double[] box = {at[0], at[1], at[0] + tw, at[1] + th};
                    if (box[0] < plotL || box[2] > w - 2 || box[1] < plotT - 4 || box[3] > plotB || overlaps(box, taken)) continue;
                    taken.add(box);
                    put(dot.name(), at[0], at[1], 0, null, wordsIn(Palette.TEXT_LABEL));
                    break;
                }
            }
        }
    }

    /**
     * AT FACE, CARRIED, AND WORTH TODAY (CD6, board 1): three bars on one scale, each stacked by kind in the kinds'
     * colours with its total at its end, and what the book is worth marked across all three as a dashed line.
     */
    static final class DeskBars extends Drawn {
        static final double H = 158, LEFT = 84, RIGHT = 76, TOP = 26, ROW = 42, BAR = 26;
        static final String[] ROWS = {"at face", "carried", "worth today"};

        private final double[][] byKind;
        private final double worth;
        private final java.util.function.DoubleFunction<String> money;

        DeskBars(double[][] byKind, double worth, java.util.function.DoubleFunction<String> money) {
            super(300, 480, H);
            this.byKind = byKind;
            this.worth = worth;
            this.money = money;
            drawFirst();
        }

        @Override void draw(double w) {
            double[] totals = new double[ROWS.length];
            double max = Math.max(worth, 1);
            for (int r = 0; r < ROWS.length; r++) {
                for (double v : byKind[r]) totals[r] += v;
                max = Math.max(max, totals[r]);
            }
            final double plotW = Math.max(40, w - LEFT - RIGHT);
            for (int r = 0; r < ROWS.length; r++) {
                double top = TOP + r * ROW;
                put(ROWS[r], 0, top + 4, LEFT - 8, Pos.CENTER_RIGHT, wordsIn(Palette.TEXT_LABEL));
                double acc = 0;
                for (int k = 0; k < byKind[r].length; k++) {
                    double v = byKind[r][k];
                    if (v <= 0) continue;
                    double x0 = LEFT + acc / max * plotW, x1 = LEFT + (acc + v) / max * plotW;
                    javafx.scene.shape.Rectangle bar = new javafx.scene.shape.Rectangle(x0, top, Math.max(1, x1 - x0 - 1), BAR);
                    bar.setFill(javafx.scene.paint.Color.web(bookKindColour(DebtManager.BOOK_KINDS[k])));
                    bar.setArcWidth(3);
                    bar.setArcHeight(3);
                    Tooltip.install(bar, tip(ROWS[r] + ", " + BOOK_KIND_NAMES[k] + ": " + money.apply(v)));
                    getChildren().add(bar);
                    acc += v;
                }
                put(money.apply(totals[r]), LEFT + acc / max * plotW + 6, top + 3, 0, null, figureIn(Palette.TEXT_HEAD));
            }
            double wx = LEFT + worth / max * plotW;
            stroke(wx, TOP - 8, wx, TOP + ROWS.length * ROW - 10, Palette.TEXT_HEAD, 1, 3, 3);
            put("worth today", Math.max(0, Math.min(w - 80, wx - 40)), 1, 80, Pos.CENTER, wordsIn(Palette.TEXT_LABEL));
        }
    }

    /**
     * ITS PRICE SINCE IT WAS SOLD (CD6, board 2): the price per 100 of face a month, in the money blue, and what
     * the line is carried at per 100, muted and dashed, against a horizontal at 100, the axis in years. Carried creeps
     * to 100 and the price does not.
     */
    static final class PathChart extends Drawn {
        static final double H = 212, LEFT = 40, RIGHT = 16, TOP = 14, FOOT = 26;

        private final double[] price, carried;
        private final int firstMonth;

        PathChart(double[] price, double[] carried, int firstMonth) {
            super(300, 440, H);
            this.price = price;
            this.carried = carried;
            this.firstMonth = firstMonth;
            drawFirst();
        }

        /** Whether a month carries a tick of this step: January's of the years it counts in, or the months it counts in. */
        private static boolean ticked(int month, int step) {
            int moy = CityCalendar.monthOfYear(month);
            return step <= 12 ? (moy - 1) % step == 0 : moy == 1 && CityCalendar.yearOf(month) % (step / 12) == 0;
        }

        @Override void draw(double w) {
            final double plotL = LEFT, plotR = Math.max(LEFT + 100, w - RIGHT), plotT = TOP, plotB = H - FOOT;
            final int n = price.length;
            double lo = 100, hi = 100;
            for (int i = 0; i < n; i++) {
                lo = Math.min(lo, Math.min(price[i], carried[i]));
                hi = Math.max(hi, Math.max(price[i], carried[i]));
            }
            double pad = Math.max(.5, (hi - lo) * .1);
            double step = niceStep(hi - lo + 2 * pad, 5, .5, 1, 2, 5, 10, 20);
            lo = Math.floor((lo - pad) / step) * step;
            hi = Math.ceil((hi + pad) / step) * step;
            final double bottom = lo, span = hi - lo;
            java.util.function.DoubleUnaryOperator y = v -> plotB - (v - bottom) / span * (plotB - plotT);
            java.util.function.IntToDoubleFunction x = i -> plotL + (n > 1 ? (plotR - plotL) * i / (n - 1) : 0);
            for (double v = lo; v <= hi + 1e-9; v += step) {
                stroke(plotL, y.applyAsDouble(v), plotR, y.applyAsDouble(v), Palette.EDGE, 1);
                put(String.format(step < 1 ? "%.1f" : "%.0f", v), 0, y.applyAsDouble(v) - 7, LEFT - 6, Pos.CENTER_RIGHT, figureIn(Palette.TEXT_MUTED));
            }
            stroke(plotL, plotB, plotR, plotB, Palette.TEXT_SPENT, 1);
            // The axis in years: the smallest step that leaves a label room.
            int every = 1;
            for (int s : new int[] {1, 2, 3, 6, 12, 24, 60, 120}) {
                every = s;
                if ((plotR - plotL) * s / Math.max(1, n - 1) >= 58) break;
            }
            for (int i = 0; i < n; i++) {
                int month = firstMonth + i;
                if (!ticked(month, every)) continue;
                stroke(x.applyAsDouble(i), plotB, x.applyAsDouble(i), plotB + 4, Palette.TEXT_SPENT, 1);
                put(every >= 12 ? String.valueOf(CityCalendar.yearOf(month)) : CityCalendar.formatShort(month),
                        x.applyAsDouble(i) - 28, plotB + 6, 56, Pos.CENTER, wordsIn(Palette.TEXT_MUTED));
            }
            // Par.
            stroke(plotL, y.applyAsDouble(100), plotR, y.applyAsDouble(100), Palette.TEXT_SPENT, 1, 5, 4);
            // Carried under the price, dashed; the price over it, solid.
            polyline(carried, x, y, Palette.TEXT_LABEL, 1.5, true);
            polyline(price, x, y, Palette.MONEY, 2.2, false);
            double lastP = price[n - 1], lastC = carried[n - 1];
            for (double[] end : new double[][] {{lastP, 0}, {lastC, 1}}) {
                javafx.scene.shape.Circle c = new javafx.scene.shape.Circle(plotR, y.applyAsDouble(end[0]), end[1] == 0 ? 3.5 : 3,
                        javafx.scene.paint.Color.web(end[1] == 0 ? Palette.MONEY : Palette.TEXT_LABEL));
                getChildren().add(c);
            }
            boolean priceAbove = lastP >= lastC;
            put(String.format("%.2f", lastP), plotR - 96, y.applyAsDouble(lastP) + (priceAbove ? -15 : 5), 90, Pos.CENTER_RIGHT, figureIn(Palette.TEXT_HEAD));
            put("carried " + String.format("%.2f", lastC), plotR - 96, y.applyAsDouble(lastC) + (priceAbove ? 5 : -15), 90, Pos.CENTER_RIGHT,
                    figureIn(Palette.TEXT_LABEL));
        }

        private void polyline(double[] values, java.util.function.IntToDoubleFunction x, java.util.function.DoubleUnaryOperator y,
                              String colour, double width, boolean dashed) {
            javafx.scene.shape.Polyline line = new javafx.scene.shape.Polyline();
            line.setStroke(javafx.scene.paint.Color.web(colour));
            line.setStrokeWidth(width);
            line.setStrokeLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
            if (dashed) line.getStrokeDashArray().addAll(4.0, 3.0);
            for (int i = 0; i < values.length; i++) line.getPoints().addAll(x.applyAsDouble(i), y.applyAsDouble(values[i]));
            getChildren().add(line);
        }
    }

    /* =====================================================================
       CLOSE IT NOW (CD4, 2026-10-23)

       WHAT IT WAS: a button on each piece's card, "Buy back", that paid the
       piece's whole worth in one press - one price, no say in how much and
       none in what to pay, and a gain figured against the face.

       WHAT IT IS (spec-city-debt.md 6.2 to 6.4, board 3; D31, each CD batch
       ships with its own screen): the card's button opens a page of its own
       for the piece, as The book and Borrow are pages.

         THE TICKET   the face to buy back (everything on offer when the box
                      is empty) and the most to pay per 100 (no limit when
                      it is empty), and under them what the reverse auction
                      makes of it: offered and its cover, taken from each
                      holder, the stop and the average, fair, the price, and
                      the button. Game.quoteBuyback() is the quote and
                      Game.buyBack() the booking, struck on the same city.
         THE OFFERS   a row a slice, the cheapest first, a colour a holder;
                      what the ticket takes filled, the rest outlined as the
                      ghost segments are; the stop and the most to pay drawn.
         THE THREE    against carrying value (the one the books keep),
         COMPARISONS  against face (what the debt-to-GDP measure sees) and
                      over fair (what the auction cost), and the journal.
         THE HONEST   what it stops a month of coupon and what the price
         LINE         borrowed again would cost a month.
         THE CALL     a callable line only: from when, what it costs, the
                      gain or loss against carrying value, what it saves, and
                      Call it (Game.callLine()).

       Every figure is the model's (Buyback.Result, Game.fairValueOf(),
       callCost(), callSaving(), worthCalling(), DebtManager.curveRate()); the
       page prices nothing. The sums it does are the spec's own: a coupon
       scaled to the face taken, a price times a rate over twelve, the
       carrying value less the face. No colour on a gain or a loss (the
       0.7.32 spec's B6). The two boxes keep their typing through a month's
       redraw as the ask's does (askBox()).
       ===================================================================== */

    /** THE TICKET's (i): the spec's 6.2. */
    static final String CLOSE_INFO = "Closing a piece early is a reverse auction, not one price. Name the face you want "
            + "back and, if you like, the most you will pay for each 100 of it. Every holder but the central bank offers "
            + "what it holds in slices, each at the price where its own buying rule would stop wanting it - never under "
            + "what the piece is worth today. You take the cheapest offers first, up to the face, skipping any dearer "
            + "than your limit, and pay each its own price. The stop is the dearest offer taken; the average is what you "
            + "paid over the face. The central bank's holdings follow its own dial and are not for sale (D18). It is "
            + "paid in cash: the treasury does not borrow to buy its own paper back.";

    /** THE OFFERS' (i). */
    static final String OFFERS_INFO = "Each bar is one slice a holder offers: its length is the slice's face, the number "
            + "at the left its price for each 100 of face. Filled is what your ticket takes; outlined is offered and "
            + "left. Fair is what the piece is worth today, and nobody offers under it. The stop is the dearest offer "
            + "taken; the most to pay is your limit, and nothing over it is taken.";

    /** THE THREE COMPARISONS' (i): the spec's 6.3. */
    static final String COMPARE_INFO = "One buyback, read three ways. Against carrying value is the figure the books keep, "
            + "the statement's Gain (loss) on debt retired: what the face taken is carried at, less what it cost. Against "
            + "face is what the debt-to-GDP measure sees: the face that comes off, less the cash that goes. Over fair is "
            + "what the auction cost: what you paid over what the same face is worth today. The journal is the entry the "
            + "first of them books. A buyback at fair moves nothing in present value; it changes when the city pays and "
            + "what its measures say.";

    /** THE CALL's (i): the spec's 6.4 and D22 to D24. */
    static final String CALL_INFO = "From its first call date the city can pay every holder 100 for each 100 of face, "
            + "whole, and close the line - the right it paid for in the coupon when it sold it. The cash goes at once to "
            + "every holder, the central bank's too. The gain or loss is the line's carrying value less the face. It is "
            + "worth calling when the present value it saves passes " + pc(CallTree.WORTH_CALLING) + " of the face.";

    /** The colour a holder has on the Close it now page: the same as on the card's holders bar. */
    static String holderColour(Buyback.Holder holder) {
        return switch (holder) {
            case HOUSEHOLDS -> Palette.PEOPLE;
            case BANK       -> Palette.MONEY;
            case FIRMS      -> Palette.BUSINESS_DARK;
            case WORLD      -> Palette.BUILDING_DARK;
        };
    }

    /** ...and its name. */
    static String holderName(Buyback.Holder holder) {
        return switch (holder) {
            case HOUSEHOLDS -> "households";
            case BANK       -> "the bank";
            case FIRMS      -> "the businesses";
            case WORLD      -> "the world";
        };
    }

    /** A most-to-pay typed: dollars for each 100 of face, "98.5" (a comma may stand for the point); NaN when it is not a number above nothing and under a thousand. Pure. */
    static double perHundredFromWords(String typed) {
        if (typed == null || typed.isBlank()) return Double.NaN;
        try {
            double v = Double.parseDouble(typed.trim().replace(',', '.'));
            return v > 0 && v < 1000 ? v : Double.NaN;
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    /** A term of this many months in words, as the honest line names the rate: "6-month", "10-year", "7.5-year". Pure. */
    static String termWords(int months) {
        if (months < 24) return Math.max(1, months) + "-month";
        return (months % 12 == 0 ? String.valueOf(months / 12) : String.format("%.1f", months / 12.0)) + "-year";
    }

    /** True while the Close it now page is open on a piece that is still on the books. */
    boolean closeOpen() {
        return "The book".equals(financeArea) && CLOSE_PAGE.equals(financePage) && closing != null
                && ui.game.getDebtManager().getDebt().contains(closing);
    }

    /** The card's Close it now: the page opened on this piece, its boxes empty (everything on offer, no limit). */
    void openClose(Debt debt) {
        // From a line's page the back door returns there (CD6); from anywhere else, to the book.
        closeFromLine = lineOpenNow() && lineOpen == debt;
        closing = debt;
        closeFaceText = "";
        closeMaxText = "";
        open("The book", CLOSE_PAGE, null);
    }

    /** One of the two boxes set to this, or emptied, and the page drawn on it. */
    private void setCloseBox(int which, String text) {
        if (which == 1) closeFaceText = text;
        else            closeMaxText = text;
        closeFaceField = null;
        closeMaxField = null;
        showFinanceMenu();
    }

    /**
     * One of the Close it now page's boxes (1 the face, 2 the most to pay):
     * the house's search box, its text kept on the screen as it is typed so a
     * month's redraw puts it back, and the focus with it. Enter draws the
     * page on what is typed; so does leaving the box, once the leaving is
     * over (UserInterface.redrawSoon()), if the text changed.
     */
    private javafx.scene.control.TextField closeBox(int which, String prompt) {
        String shown = which == 1 ? closeFaceText : closeMaxText;
        javafx.scene.control.TextField box = searchBox(shown, prompt, 260, typed -> {
            if (which == 1) closeFaceText = typed;
            else            closeMaxText = typed;
        }, null);
        box.setOnAction(e -> showFinanceMenu());
        box.focusedProperty().addListener((o, was, now) -> {
            if (now) return;
            javafx.application.Platform.runLater(() -> {
                if (box.getScene() != null && !box.isFocused() && !(box.getText() == null ? "" : box.getText()).equals(shown)) {
                    ui.redrawSoon();
                }
            });
        });
        if (closeTyping == which) {
            closeTyping = 0;
            javafx.application.Platform.runLater(() -> {
                box.requestFocus();
                box.positionCaret(box.getText().length());
            });
        }
        if (which == 1) closeFaceField = box;
        else            closeMaxField = box;
        return box;
    }

    /** A face as a box takes it back: whole dollars with commas ("1,250,000,000"), so a chip that fills the box says it exactly. */
    static String boxAmount(double thousands) {
        return String.format("%,.0f", Money.toDollars(thousands));
    }

    /**
     * Why a ticket takes nothing, in a sentence (null when it takes something):
     * nothing on offer, nothing asked, a sale not yet settled that can only go
     * whole, or a limit under the cheapest offer.
     */
    String closeWhyNot(Buyback.Result q, Debt debt, double offerable, double asked, double typedMax) {
        if (q.takenFace() > 0) return null;
        if (!(offerable > 0)) {
            return "The central bank holds all of this piece and does not sell (D18): there is nothing to buy back.";
        }
        if (!(asked > 0)) return "Nothing is asked for: type a face, or clear the box for everything on offer.";
        if (q.ladder().isEmpty()) {
            if (debt.unsettledFace() > 0 || debt.getSettleDue() > 0) {
                return "This piece was sold lately and its buyers have not paid for it yet, so it can be bought back "
                        + "whole or not at all" + (debt.centralBankPrincipal() > 0
                        ? ", and the central bank holds part of it, which does not sell (D18)."
                        : ": ask for everything on offer.");
            }
            return "No holder offers any of it.";
        }
        if (!Double.isNaN(typedMax)) {
            return String.format("No holder offers anything at or under %.3f for each 100: the cheapest offer is %.3f. "
                    + "Raise the most to pay, or clear it.", typedMax, q.ladder().get(0).per100());
        }
        return "Nothing is taken.";
    }

    /** The Close it now page (CD4): one piece's ticket, its offers, the three comparisons and the journal, the honest line and, on a callable line, the call. */
    void closePage(VBox page) {
        Game g = ui.game;
        DebtManager ledger = g.getDebtManager();
        Debt debt = closing;
        int month = g.getMonth();
        int due = debt.getMaturityMonth();
        double principal = debt.getOustandingPrincipal();
        double offerable = g.buybackOfferable(debt);
        double cash = g.getCash();

        // The ticket as typed: an empty box is everything on offer, and no limit; one that cannot be read is said, and read as empty.
        double typedFace = closeFaceText.isBlank() ? Double.NaN : askFromWords(closeFaceText);
        boolean faceUnread = !closeFaceText.isBlank() && Double.isNaN(typedFace);
        final double face = Double.isNaN(typedFace) ? offerable : typedFace;
        double maxRead = closeMaxText.isBlank() ? Double.NaN : perHundredFromWords(closeMaxText);
        boolean maxUnread = !closeMaxText.isBlank() && Double.isNaN(maxRead);
        final double typedMax = maxRead;
        Buyback.Result q = g.quoteBuyback(debt, face, typedMax);
        double asked = Math.min(Math.max(0, face), offerable);
        boolean taken = q.takenFace() > 0 && q.paid() > 0;

        // ----- the piece -----
        String colour = instrumentColour(debt.getType());
        Label name = words(pieceName(debt), Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD);
        name.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox head = new HBox(Palette.GAP, tag((debt.isForeign() ? "US$ " : "") + kindTag(debt.getType()), colour), name, gap,
                figure(CityCalendar.formatShort(due) + " · " + CityCalendar.until(month, due), Palette.SIZE_LABEL + 1,
                        urgency(due - month)));
        head.setAlignment(Pos.CENTER_LEFT);
        double coupon = debt.getMonthlyCoupon();
        Node facts = muted(String.format("%s owed · %s carried · %s · worth today %s · %.3f for each 100", d(principal),
                d(debt.getCarryingValue()), coupon >= .5 ? d(coupon) + " a month of coupon" : "no coupon",
                d(g.fairValueOf(debt)), ledger.priceOfPar(debt)));
        Node held = holdersBar(debt);
        Node central = debt.isForeign()
                ? muted("Dollar paper: the world holds all of it, and offers every slice at fair (D26).")
                : debt.centralBankPrincipal() > 0
                ? muted("The central bank holds " + d(debt.centralBankPrincipal()) + " of it and does not sell (D18): "
                        + d(offerable) + " is on offer.")
                : null;
        // The way back is the line's page when Close it now was opened from there (CD6), else the book.
        final Debt back = closing;
        page.getChildren().add(card(closeFromLine ? backDoor("The line", () -> openLine(back))
                : backDoor("The book", () -> open("The book", null, back)), head, facts, held, central));

        // ----- the ticket -----
        VBox ticket = card(caption("THE TICKET", CLOSE_INFO, hint("a reverse auction, struck on the city as it stands")));
        ticket.getChildren().add(caption("HOW MUCH FACE TO BUY BACK", null));
        javafx.scene.layout.FlowPane faceChips = new javafx.scene.layout.FlowPane(6, 6);
        faceChips.getChildren().addAll(
                stepChip("everything on offer", () -> setCloseBox(1, ""), true),
                stepChip("half", () -> setCloseBox(1, boxAmount(offerable / 2)), false),
                stepChip("a tenth", () -> setCloseBox(1, boxAmount(offerable / 10)), false));
        HBox faceRow = new HBox(Palette.GAP, closeBox(1, "everything on offer: " + d(offerable)),
                words("Enter sets it", Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        faceRow.setAlignment(Pos.CENTER_LEFT);
        ticket.getChildren().addAll(faceRow, faceChips);
        // The default, in words: everything on offer is the principal less the central bank's face, which does not sell (D18).
        ticket.getChildren().add(muted(debt.isForeign()
                ? "Everything on offer is " + d(offerable) + ": dollar paper is the world's alone (D26)."
                : "Everything on offer is " + d(offerable) + ": the principal less the central bank's face - the central bank does not sell (D18)."));
        if (faceUnread) {
            ticket.getChildren().add(words("“" + closeFaceText.trim() + "” is not an amount: digits, then k, M, B or T - "
                    + "500M, 2.5B, 12,000,000. Everything on offer is asked for instead.", Palette.SIZE_LABEL, Palette.BAD));
        } else if (!Double.isNaN(typedFace) && typedFace > offerable + .5) {
            ticket.getChildren().add(muted("Only " + d(offerable) + " is on offer; that is what the ticket asks for."));
        }
        ticket.getChildren().add(caption("THE MOST TO PAY, FOR EACH 100 OF FACE", null));
        javafx.scene.layout.FlowPane maxChips = new javafx.scene.layout.FlowPane(6, 6);
        maxChips.getChildren().add(stepChip("no limit", () -> setCloseBox(2, ""), true));
        HBox maxRow = new HBox(Palette.GAP, closeBox(2, "no limit · type 98.5"),
                words("Enter sets it", Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        maxRow.setAlignment(Pos.CENTER_LEFT);
        ticket.getChildren().addAll(maxRow, maxChips);
        if (maxUnread) {
            ticket.getChildren().add(words("“" + closeMaxText.trim() + "” is not a price: dollars for each 100 of face, "
                    + "98.5. There is no limit instead.", Palette.SIZE_LABEL, Palette.BAD));
        }

        VBox result = new VBox(6);
        result.getChildren().add(caption("WHAT IT BUYS", null));
        result.getChildren().add(cardLine("Offered by the holders", d(q.offeredFace())
                + (asked > 0 && q.offeredFace() > 0 ? String.format(" · %.2f× the face asked", q.cover()) : ""),
                q.offeredFace() > 0 ? null : Palette.TEXT_MUTED));
        for (Buyback.Holder h : Buyback.Holder.values()) {
            double hf = switch (h) {
                case BANK -> q.bankFace();
                case HOUSEHOLDS -> q.householdFace();
                case FIRMS -> q.firmFace();
                case WORLD -> q.worldFace();
            };
            double hp = switch (h) {
                case BANK -> q.bankPaid();
                case HOUSEHOLDS -> q.householdPaid();
                case FIRMS -> q.firmPaid();
                case WORLD -> q.worldPaid();
            };
            if (hf > 0) result.getChildren().add(takenLine(holderName(h), holderColour(h), hf, hp));
        }
        if (taken) {
            result.getChildren().add(cardLine("The stop · the average, for each 100",
                    String.format("%.3f · %.3f", q.stopPer100(), q.averagePer100()), null));
        }
        if (q.fairPer100() > 0) result.getChildren().add(cardLine("Fair, for each 100", String.format("%.3f", q.fairPer100()), null));
        result.getChildren().add(cardLine("The price", taken ? d(q.paid()) : "nothing", taken ? Palette.TEXT_HEAD : Palette.TEXT_MUTED));
        result.getChildren().add(cardLine("In the treasury", d(cash), null));

        String why = closeWhyNot(q, debt, offerable, asked, typedMax);
        boolean afford = q.paid() <= cash;
        Button buy = new Button(taken ? "Buy back " + d(q.takenFace()) + " · pay " + d(q.paid()) : "Buy back");
        buy.setMinWidth(Region.USE_PREF_SIZE);
        buy.setDisable(!taken || !afford);
        if (taken && afford) buy.setStyle("-fx-background-color: " + Palette.CONFIRM + "; -fx-text-fill: white;");
        buy.setOnAction(e -> {
            ui.game.buyBack(debt, face, typedMax);
            // A freshly built page: retiring paper moves the rate, so every other card's price is now stale.
            open("The book", null, debt);
        });
        ticket.getChildren().add(result);
        if (why != null) {
            ticket.getChildren().add(words(why, Palette.SIZE_LABEL, Palette.BAD_SOFT));
        } else if (!afford) {
            ticket.getChildren().add(words("The treasury has " + d(cash) + "; this costs " + d(q.paid())
                    + ". A buyback is paid in cash, never by borrowing: ask for less, or limit the price.",
                    Palette.SIZE_LABEL, Palette.BAD_SOFT));
        }
        ticket.getChildren().add(buy);
        page.getChildren().add(ticket);

        // ----- the offers -----
        VBox offers = card(caption("THE OFFERS", OFFERS_INFO, hint("cheapest first, a bar a slice")));
        if (q.ladder().isEmpty()) {
            offers.getChildren().add(muted("No offers to draw: the ticket takes none."));
        } else {
            offers.getChildren().add(new BuybackLadder(q, typedMax, this::d));
            offers.getChildren().add(keyRow(swatch(holderColour(Buyback.Holder.BANK), "the bank", false),
                    swatch(holderColour(Buyback.Holder.HOUSEHOLDS), "households", false),
                    swatch(holderColour(Buyback.Holder.FIRMS), "the businesses", false),
                    swatch(holderColour(Buyback.Holder.WORLD), "the world", false),
                    swatch(Palette.TEXT_MUTED, "offered and not taken, outlined", true)));
        }
        page.getChildren().add(offers);

        // ----- the three comparisons, and the journal -----
        if (taken) {
            VBox compare = card(caption("THE THREE COMPARISONS", COMPARE_INFO),
                    cardLine("Against carrying value · the statement's Gain (loss) on debt retired", signedD(q.gainOnCarrying()), null),
                    cardLine("Against face · what the debt-to-GDP measure sees", signedD(q.gainOnFace()), null),
                    cardLine("Over fair · what the auction cost", d(q.overFair()), null));
            double sideways = q.unamortisedTaken();
            double gain = q.gainOnCarrying();
            VBox journal = card(caption("THE JOURNAL", null, hint("what the first of them books")),
                    cardLine("Dr  City paper payable", d(q.takenFace()), null),
                    sideways > .5 ? cardLine("Dr  Unamortised premium", d(sideways), null)
                                  : cardLine("Cr  Unamortised discount", d(Math.max(0, -sideways)), null),
                    cardLine("Cr  Cash", d(q.paid()), null),
                    gain >= 0 ? cardLine("Cr  Gain on debt retired", d(gain), null)
                              : cardLine("Dr  Loss on debt retired", d(-gain), null));
            page.getChildren().add(pair(compare, journal));

            // ----- the honest line -----
            String honest = honestSentence(debt, q);
            page.getChildren().add(card(caption("THE HONEST LINE", null),
                    words(honest, Palette.SIZE_BODY + 1, Palette.TEXT_BODY),
                    muted("A buyback at fair moves nothing in present value; it changes when the city pays and what the "
                            + "measures say. This page says both.")));
        }

        // ----- the call -----
        if (debt instanceof LongTermBond bond && bond.isCallable()) page.getChildren().add(callCard(bond));
    }

    /** One holder's line on the ticket: its swatch and name, what was taken from it, and what it is paid. */
    HBox takenLine(String who, String colour, double face, double paid) {
        HBox name = keySwatch(colour, "Taken from " + who);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox row = new HBox(Palette.GAP, name, gap, figure(d(face) + " for " + d(paid), Palette.SIZE_BODY, Palette.TEXT_BODY));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /**
     * THE CALL (spec-city-debt.md 6.4, D22 to D24), on a callable line's
     * page: from when it can be called, what it costs, the gain or loss
     * against carrying value (carrying less the face), what it saves in
     * present value and whether that passes the rule, and Call it.
     */
    VBox callCard(LongTermBond bond) {
        Game g = ui.game;
        int month = g.getMonth();
        int first = bond.getFirstCallMonth();
        boolean now = bond.isCallableNow(month);
        double cash = g.getCash();
        double cost = g.callCost(bond);
        double carried = bond.getCarryingValue();
        double saving = g.callSaving(bond);
        boolean worth = g.worthCalling(bond);

        VBox card = card(caption("THE CALL", CALL_INFO));
        card.getChildren().add(words(String.format("Callable at %.0f from %s", CallTree.CALL_PRICE, CityCalendar.format(first))
                + (now ? "." : " - not yet callable: " + CityCalendar.until(month, first) + "."),
                Palette.SIZE_BODY + 1, Palette.TEXT_BODY));
        card.getChildren().add(cardLine("What the call costs · every holder paid in full", d(cost), null));
        card.getChildren().add(cardLine("Gain (loss) against carrying value · " + d(carried) + " carried, less the face",
                signedD(g.callGain(bond)), null));
        card.getChildren().add(cardLine("What calling saves, in present value", signedD(saving), null));
        card.getChildren().add(worth
                ? infoLine("Worth calling: it saves " + d(saving) + ", over " + pc(CallTree.WORTH_CALLING) + " of the face.",
                        WORTH_CALLING_INFO, true, Palette.SIZE_BODY + 1, Palette.TEXT_BODY, 560)
                : infoLine("Not worth calling yet: the saving is under " + pc(CallTree.WORTH_CALLING) + " of the face.",
                        WORTH_CALLING_INFO, true, Palette.SIZE_LABEL, Palette.TEXT_MUTED, 560));

        boolean can = g.canCall(bond);
        boolean afford = cost <= cash;
        Button call = new Button(can ? "Call it · pay " + d(cost) : "Call it");
        call.setMinWidth(Region.USE_PREF_SIZE);
        call.setDisable(!can || !afford);
        if (can && afford) call.setStyle("-fx-background-color: " + Palette.CONFIRM + "; -fx-text-fill: white;");
        call.setOnAction(e -> {
            ui.game.callLine(bond);
            open("The book", null, null);
        });
        String why = !now ? "It cannot be called until " + CityCalendar.format(first) + "."
                : bond.unsettledFace() > 0 || bond.getSettleDue() > 0
                ? "This line was sold lately and its buyers have not paid for it yet: it can be called once they have."
                : !can ? "This line cannot be called now."
                : !afford ? "The treasury has " + d(cash) + "; the call costs " + d(cost)
                        + ". A call is paid in cash, never by borrowing."
                : null;
        if (why != null) card.getChildren().add(words(why, Palette.SIZE_LABEL, Palette.BAD_SOFT));
        card.getChildren().add(call);
        return card;
    }

    /**
     * The reverse auction's offers, drawn (board 3): a row a slice, the
     * cheapest first, the price for each 100 at the left and the slice's face
     * after its bar; a colour a holder, the slice outlined and faintly filled
     * as a ghost segment is, and filled where the ticket takes it (the last
     * one taken in part). Three dashed lines across the plot: fair at the top
     * (nobody offers under it), the stop under the dearest slice taken, and
     * the most to pay between the last slice at or under it and the first
     * over; the last two named at the right. A fixed row height and the
     * card's width: it is drawn again when it is given another.
     */
    static final class BuybackLadder extends javafx.scene.layout.Pane {
        /** The margins, in pixels: the prices at the left; at the right the room for a slice's face (FACE_ROOM) and for the marks' names (MARKS). */
        static final double LEFT = 74, FACE_ROOM = 76, MARKS = 134, TOP = 26, FOOT = 10, FIRST_W = 720, LEAST_W = 440;

        private final Buyback.Result q;
        private final double maxPer100;
        private final java.util.function.DoubleFunction<String> money;
        /** A row's pitch, and the width it is drawn at now (0 before the first). */
        private final double row;
        private double drawnAt;

        BuybackLadder(Buyback.Result q, double maxPer100, java.util.function.DoubleFunction<String> money) {
            this.q = q;
            this.maxPer100 = maxPer100;
            this.money = money;
            int n = q.ladder().size();
            this.row = n > 24 ? 13 : 18;
            double h = TOP + n * row + FOOT;
            setMinSize(LEAST_W, h);
            setPrefSize(FIRST_W, h);
            setMaxSize(Double.MAX_VALUE, h);
            draw(FIRST_W);
        }

        @Override protected void layoutChildren() {
            double w = Math.max(LEAST_W, getWidth());
            if (Math.abs(w - drawnAt) > .5) draw(w);
            super.layoutChildren();
        }

        /** The ladder at a width: everything in it, drawn again from the quote. */
        private void draw(double w) {
            drawnAt = w;
            getChildren().clear();
            List<Buyback.Offer> ladder = q.ladder();
            int n = ladder.size();
            double biggest = 0;
            for (Buyback.Offer o : ladder) biggest = Math.max(biggest, o.face());
            final double barMax = Math.max(60, w - LEFT - FACE_ROOM - MARKS);
            final double plotRight = w - MARKS;
            double barH = Math.max(6, row - 5);

            int lastTaken = -1, lastWithin = -1;
            double left = q.takenFace();
            for (int i = 0; i < n; i++) {
                Buyback.Offer o = ladder.get(i);
                double y = TOP + i * row;
                double len = biggest > 0 ? barMax * o.face() / biggest : 0;
                double took = Math.min(o.face(), Math.max(0, left));
                left -= took;
                if (took > 0) lastTaken = i;
                if (Double.isNaN(maxPer100) || o.per100() <= maxPer100) lastWithin = i;
                String colour = holderColour(o.holder());

                // The whole slice as a ghost; what the ticket takes of it, solid over it.
                javafx.scene.shape.Rectangle ghost = new javafx.scene.shape.Rectangle(LEFT, y + 2.5, Math.max(2, len), barH);
                ghost.setFill(javafx.scene.paint.Color.web(colour, .25));
                ghost.setStroke(javafx.scene.paint.Color.web(colour));
                ghost.setArcWidth(4);
                ghost.setArcHeight(4);
                Tooltip tip = new Tooltip(holderName(o.holder()) + " offers " + money.apply(o.face()) + String.format(" at %.3f", o.per100())
                        + " for each 100" + (took > 0 ? "; " + money.apply(took) + " of it is taken." : "; none of it is taken."));
                tip.setShowDelay(Duration.millis(150));
                Tooltip.install(ghost, tip);
                getChildren().add(ghost);
                if (took > 0) {
                    javafx.scene.shape.Rectangle solid = new javafx.scene.shape.Rectangle(LEFT, y + 2.5,
                            Math.max(2, biggest > 0 ? barMax * took / biggest : 0), barH);
                    solid.setFill(javafx.scene.paint.Color.web(colour));
                    solid.setArcWidth(4);
                    solid.setArcHeight(4);
                    Tooltip.install(solid, tip);
                    getChildren().add(solid);
                }
                label(String.format("%.3f", o.per100()), LEFT - 8, y + (row - 12) / 2, javafx.geometry.Pos.CENTER_RIGHT, LEFT - 10,
                        took > 0 ? Palette.TEXT_HEAD : Palette.TEXT_MUTED);
                label(money.apply(o.face()), LEFT + len + 6, y + (row - 12) / 2, javafx.geometry.Pos.CENTER_LEFT, FACE_ROOM - 6,
                        Palette.TEXT_MUTED);
            }

            // Fair, at the top: every offer is at or over it.
            horizontal(TOP, plotRight, Palette.TEXT_MUTED);
            label(String.format("fair %.3f", q.fairPer100()), plotRight + 8, TOP - 15, javafx.geometry.Pos.CENTER_LEFT, MARKS - 8, Palette.TEXT_LABEL);
            label("price for each 100 of face", LEFT - 8, 2, javafx.geometry.Pos.CENTER_LEFT, 200, Palette.TEXT_MUTED);

            // The stop, under the dearest slice taken: the name above its line, in the right-hand room.
            if (lastTaken >= 0) {
                double y = TOP + (lastTaken + 1) * row;
                horizontal(y, plotRight, Palette.WARN);
                label(String.format("stop %.3f", ladder.get(lastTaken).per100()), plotRight + 8, y - 14,
                        javafx.geometry.Pos.CENTER_LEFT, MARKS - 8, Palette.WARN);
            }
            // The most to pay, between the last slice at or under it and the first over: its name below the line.
            if (!Double.isNaN(maxPer100)) {
                double y = TOP + (lastWithin + 1) * row;
                horizontal(y, plotRight, Palette.BAD);
                label(String.format("most to pay %.3f", maxPer100), plotRight + 8, y + 1,
                        javafx.geometry.Pos.CENTER_LEFT, MARKS - 8, Palette.BAD_SOFT);
            }
        }

        /** A dashed horizontal line across the plot at a height, in a colour. */
        private void horizontal(double y, double right, String colour) {
            javafx.scene.shape.Line line = new javafx.scene.shape.Line(LEFT - 8, y, right, y);
            line.setStroke(javafx.scene.paint.Color.web(colour));
            line.setStrokeWidth(1.2);
            line.getStrokeDashArray().addAll(4.0, 3.0);
            getChildren().add(line);
        }

        /** A label: `x` is where the text starts, ends or centres by `align`, in a box `wide` pixels across (AuctionChart's). */
        private void label(String text, double x, double y, javafx.geometry.Pos align, double wide, String colour) {
            Label l = new Label(text);
            l.setStyle(Palette.figure(Palette.SIZE_CAPTION, colour));
            l.setAlignment(align);
            l.setMinWidth(wide);
            l.setPrefWidth(wide);
            l.setMaxWidth(wide);
            double at = align == javafx.geometry.Pos.CENTER_RIGHT ? x - wide : align == javafx.geometry.Pos.CENTER ? x - wide / 2 : x;
            l.relocate(at, y);
            l.setMouseTransparent(true);
            getChildren().add(l);
        }
    }

    /* =====================================================================
       BORROW (0.7.32)

       What would this paper cost, and what would it do to the ladder?
       THE ASK beside THE QUOTE: the instruments as tiles, the terms
       as columns of their rate - each re-struck for the amount once there
       is one - and the amount; the quote as the land office's offer card,
       which books exactly what it shows. Under them THE LADDER THIS WOULD
       BUILD, the hub's chart with the issue as ghosts in its kind's colour,
       and who buys it: the households, then the bank's room. The rollover
       and the rescue are the hub's (one line here, with a door).

       ONE PAGE since 2026-09. It was three - instrument, then duration,
       then amount - and the quote only existed on the third. Every control
       re-strikes the quote, because the rate is a FUNCTION of the size of
       the ask: ask for twice as much and you are charged more than twice as
       much. That was true and invisible.

       AND AN AUCTION SINCE CD2 (board 4 of docs/studies/city-debt/, the
       spec's section 7): the city's paper is sold at auction, so the page
       is the auction's. THE ASK has five kinds as tiles (bills, bonds, the
       amortizing bond, and since CD5 the floating-rate note and the
       inflation-linked bond), the terms as columns of today's FAIR yield (a
       bond term whose line is still open says which it would reopen; a
       floater's column is its fair margin, a linker's its fair real coupon),
       the face, and THE HIGHEST YIELD the city
       will take as three chips - fair + a point, a yield, Fill it. THE QUOTE
       is the auction run on the city as it stands at that cap (Game's
       quoteDebt(..., cap)), and its button books that same quote at that
       same cap, so THE AUCTION under them is exactly what pressing it does:
       the four bids against yield with the issue and the highest yield
       drawn, then the result line by line. A sale the bids would not cover
       says so in red before the button. Abroad is as it was, less the
       dollar serial, floater and linker: dollar paper is the world's, not an
       auction.

       Every figure is the model's: Auction.Result for the sale, Auction.
       bidsAt() on a grid of yields for the chart (the arithmetic that
       clears, not a copy of it), DebtManager.fairYield() for the columns.
       ===================================================================== */

    /** The terms' (i): the spec's T55, on the auction's fair yield (CD2). */
    static final String TERMS_INFO = "Each column is today's fair yield for this paper at that term: what the curve "
            + "says it is worth, for the amount asked once there is one, priced with it on the books. Nobody bids "
            + "under it, and the sale clears at it or a little over. The short end is the floor - the dial, or what "
            + "the bank's money costs it, whichever is higher - plus what the city's own debt adds; longer money "
            + "carries a term premium on top, less whatever the central bank's holdings of term paper take off it. "
            + "A bond term whose line is still open says which one it would reopen. A floating note's column is the "
            + "margin over the policy rate that prices it at 100 at that term, and a linked bond's the real coupon "
            + "that does, with the inflation the term prices in. Abroad, the world lends on its own curve.";

    /** THE HIGHEST YIELD's (i): the spec's 4.1 and 4.3 in two sentences. */
    static final String CAP_INFO = "The most the city will pay to sell this: fair plus a point, a yield you name, or "
            + "Fill it, which names none. The sale clears at one yield, the lowest at which the bids cover the issue, "
            + "and everyone is filled at it, so the limit only costs anything if the bids need it. If they do not "
            + "cover the issue at your limit it sells only what was bid, and the card says so before the button; "
            + "Fill it lets the yield rise until the bidders' own rules cover the issue - their savings, their cash, "
            + "the world's appetite, not the bank's room - and says what that costs.";

    /** The ask's (i): the spec's T56. */
    static final String LOTS_INFO = "Issues round to a lot, and the market will not arrange anything under the "
            + "smallest issue for a city this size - the legal and rating work costs the same however little you raise.";

    /** The quote's empty state: the spec's T57. */
    static final String NO_ASK = "Ask for something and the quote appears here - at home the auction itself, run on "
            + "the bidders as they stand - with the ladder it would build. Nothing is committed until you press the "
            + "button.";

    /** Why a bill's proceeds are not the ask: the spec's T58 (a bill's ask is the cash; a bond's is the face). */
    static final String PROCEEDS_INFO = "A bill's ask is the cash it should raise, and its face is the lot that brings "
            + "it, so the proceeds land on whichever side of the request the rounding puts them - and the smallest "
            + "issue worth arranging can be more than a lot. A bond's ask is its face; what it raises is the face at "
            + "the auction's price, less the fixed cost.";

    /** Who bids: the spec's T61 and 4.2 (CD2). */
    static final String BUYERS_INFO = "Each bidder bids by the rule it follows elsewhere in the game. The households "
            + "first: a fifth of the issue for each point the auction's yield pays over the deposit rate, out of "
            + "what they have saved past a cushion, filled in full. The firms with idle cash, as they send it "
            + "abroad. The world, as hot money, by the excess return. Your own bank, up to its room, which is what "
            + "is left of its capital: treasury paper is risk-weighted at %s, so it ties up far less of it than a "
            + "business loan of the same size. Nobody bids under fair, because the paper trades at fair next month. "
            + "The central bank never bids, except to roll its own. Borrow enough and the room changes - for every "
            + "borrower in the city, not only for the treasury.";

    /** Where the dollars go: the spec's T62. */
    static final String DOLLARS_INFO = "Spending it leaves a dollar debt with nothing behind it, and the next "
            + "devaluation makes that debt bigger for free. Holding it leaves the net position unchanged and the next "
            + "bond cheaper - and buys nothing today. Every finance ministry in the literature has made this trade.";

    void borrowPage(VBox page, boolean foreign) {
        Game g = ui.game;
        DebtManager ledger = g.getDebtManager();
        page.getChildren().add(settingsPointer());

        if (foreign && !ledger.foreignWindowOpen()) {
            VBox shut = card(caption("THE WINDOW ABROAD IS SHUT", null),
                    words(ledger.foreignWindowReason() + ". Everything already borrowed still falls due on schedule - a "
                            + "shut window stops new lending, not old obligations.", Palette.SIZE_BODY + 1, Palette.BAD_TEXT));
            shut.setStyle(cardStyle(Palette.BAD));
            page.getChildren().add(shut);
            if (ledger.hasForeignDebt()) page.getChildren().add(foreignDoor());
            return;
        }

        // There is no dollar serial since CD2, and no dollar floater or linker since CD5 (spec-city-debt.md 2.7): abroad, a bond.
        if (foreign && homeOnly(instrument(borrowType))) {
            borrowType = "Term";
            borrowTerm = LongTermBond.MATURITIES[2];
        }
        Instrument kit = instrument(borrowType);
        // ...onto one of the terms it is sold at: a bond at 2, 5, 10, 20, 30 or 50.
        borrowTerm = nearestTerm(kit, borrowTerm);

        // At home the quote is the auction at the player's highest yield; abroad it is the world's price.
        // ...a bond asked callable (CD4) goes through the callable overload of the same quote.
        boolean callable = callableAsked(kit, foreign);
        DebtQuote quote = borrowAsk > 0
                ? (foreign ? g.quoteForeign(kit.key(), borrowAsk, borrowTerm, kit.rounding())
                   : callable ? g.quoteLongBond(borrowAsk, borrowTerm, kit.rounding(), borrowCap, true)
                           : g.quoteDebt(kit.key(), borrowAsk, borrowTerm, kit.rounding(), borrowCap))
                : null;
        // What the call adds to the coupon, read once off the model for the three places that say it.
        callWords = quote != null && quote.callable()
                ? "from " + CityCalendar.formatShort(quote.firstCallMonth()) + " · the call adds "
                        + bp(g.callSpread(quote)) + " to the coupon"
                : null;
        Auction.Result sale = foreign || quote == null ? null : quote.auction();
        // Fair for this paper, with the ask on the books: what the cap's chips are read against.
        double fair = sale != null ? sale.ask().fair()
                : ledger.fairYield(auctionKind(kit), termMonths(kit, borrowTerm), Math.max(0, borrowAsk));
        page.getChildren().add(pair(askCard(kit, foreign, quote, fair), quoteCard(kit, quote, foreign)));
        if (!foreign) {
            page.getChildren().add(auctionCard(kit, sale, quote));
            Node red = shortCard(sale);
            if (red != null) page.getChildren().add(red);
        }
        if (quote != null && !quote.isEmpty()) {
            double toLocal = foreign ? ledger.getExchangeRate() : 1;
            DebtManager.Ladder ladder = ledger.ladder(g.getMonth(), quote.schedule(toLocal));
            page.getChildren().add(card(caption("THE LADDER THIS WOULD BUILD", LADDER_INFO,
                            hint("the ghosts are this issue")),
                    ladderChart(ladder, kit.colour(), "this issue", 200), ladderKey(ladder, kit.colour(), "this issue")));
        }
        if (foreign) page.getChildren().add(pair(dollarsCard(), null));
        if (foreign && ledger.hasForeignDebt()) page.getChildren().add(foreignDoor());
    }

    /**
     * THE ASK: the kinds as tiles, the terms as columns of today's fair yield
     * (chips abroad), how much, and at home the highest yield the city will
     * take. `quote` is the one the page struck on all of it (null: nothing
     * asked), `fair` the paper's fair yield with the ask on the books.
     */
    VBox askCard(Instrument kit, boolean foreign, DebtQuote quote, double fair) {
        Game g = ui.game;
        VBox card = card(caption(foreign ? "THE ASK, ABROAD, IN " + Currency.FOREIGN_CODE : "THE ASK, AT HOME", null));
        GridPane tiles = equalColumns(foreign ? abroadKinds() : INSTRUMENTS.length, TILE_GAP);
        int column = 0;
        for (int i = 0; i < INSTRUMENTS.length; i++) {
            // Abroad the home paper is not on offer: no dollar amortizing bond since CD2, no dollar floater or linker since CD5 (spec-city-debt.md 2.7).
            if (foreign && homeOnly(INSTRUMENTS[i])) continue;
            VBox t = instrumentTile(INSTRUMENTS[i], INSTRUMENTS[i] == kit, foreign);
            GridPane.setFillHeight(t, true);
            tiles.add(t, column++, 0);
        }
        card.getChildren().add(tiles);

        card.getChildren().add(caption(foreign ? "FOR HOW LONG, IN " + kit.unit().toUpperCase()
                : "FOR HOW LONG, IN " + kit.unit().toUpperCase() + " · TODAY'S FAIR " + fairWord(kit), TERMS_INFO));
        card.getChildren().add(foreign ? termChips(kit) : termColumns(kit, quote));
        if (!foreign) card.getChildren().add(muted(lineNote(kit)));
        if (callableOffered(kit, foreign)) card.getChildren().add(callableSwitch(quote));

        double minimum = g.minimumIssueSize();
        card.getChildren().add(caption(foreign ? "HOW MANY " + Currency.FOREIGN_CODE
                : "Note".equals(kit.key()) ? "HOW MUCH CASH" : "HOW MUCH FACE", ASK_TYPED_INFO));
        /*
         * TYPED, AND BUTTONS THAT SCALE WITH IT (0.7.40). Jerus: "there should
         * be an option to raise trillions or tens of trillions right now max
         * is 1B but if city is big or inflation you have to click endlessly
         * ... i think that selection itself needs a redesign button-wise";
         * asked, he chose "Type it + scaling buttons". The steps were one,
         * two, five and ten lots - D$1B a click at most on a term loan. Now:
         * the ask in full and short; a box it is typed in; ÷10 and ×10; one
         * unit of its leading digit and a tenth of that each way, never under
         * a lot; and presets off the model's own figures. The model caps no
         * issue's size, so neither does this - the quote says what the
         * market takes.
         */
        String[] shown = askShown(borrowAsk, foreign);
        javafx.scene.layout.FlowPane figures = new javafx.scene.layout.FlowPane(8, 2);
        figures.getChildren().add(figure(shown[0], Palette.SIZE_TITLE, borrowAsk > 0 ? Palette.TEXT_HEAD : Palette.TEXT_SPENT));
        if (shown[1] != null) figures.getChildren().add(figure("· " + shown[1], Palette.SIZE_TITLE, Palette.TEXT_LABEL));
        card.getChildren().addAll(figures, askBox(foreign));
        if (askRefused != null) card.getChildren().add(words(askRefused, Palette.SIZE_LABEL, Palette.BAD));

        java.util.function.DoubleFunction<String> w = v -> foreign ? usd(v) : d(v);
        double base = askBase();
        double[] by = askSteps(base, kit.rounding());
        javafx.scene.layout.FlowPane steps = new javafx.scene.layout.FlowPane(6, 6);
        if (base > 0) {
            steps.getChildren().add(stepChip("÷10", () -> setAsk(askBase() / 10), false));
            for (double step : by) steps.getChildren().add(stepChip("−" + w.apply(step), () -> setAsk(Math.max(0, askBase() - step)), false));
        }
        for (int i = by.length - 1; i >= 0; i--) {
            double step = by[i];
            steps.getChildren().add(stepChip("+" + w.apply(step), () -> setAsk(askBase() + step), false));
        }
        if (base > 0) steps.getChildren().add(stepChip("×10", () -> setAsk(askBase() * 10), false));
        javafx.scene.layout.FlowPane presets = new javafx.scene.layout.FlowPane(6, 6);
        for (AskPreset p : askPresets(kit, foreign)) {
            presets.getChildren().add(stepChip(p.name() + " · " + w.apply(p.ask()), () -> setAsk(p.ask()), false));
        }
        presets.getChildren().add(stepChip("clear", () -> setAsk(0), true));
        card.getChildren().addAll(steps, presets);
        card.getChildren().add(noteLine("lots of " + w.apply(kit.rounding()) + " · nothing under "
                + w.apply(foreign ? g.minimumIssueSizeUsd() : minimum),
                LOTS_INFO, 560));
        if (!foreign) card.getChildren().addAll(capControls(fair));
        return card;
    }

    /**
     * What the term's line is, in a sentence, under the columns (the board's
     * bw-line): a bond sold into the line it reopens, or opening a new one; a
     * bill, an amortizing bond, a floater or a linker is always its own. Every
     * figure the model's (DebtManager.benchmarkLine(), Auction.BENCHMARK_LIFE).
     */
    String lineNote(Instrument kit) {
        switch (kit.key()) {
            case "Note":
                return "A bill is sold at a discount and is its own line: its yield is the whole return, "
                        + "and nothing is reopened.";
            case "Serial":
                return "Each amortizing sale is its own line: one coupon, the auction's yield itself, prices the "
                        + "whole schedule at 100 and the face is repaid in yearly slices.";
            // CD5 (spec-city-debt.md 2.4 and 2.5): neither is a line that reopens.
            case "Float":
                return "Each floating sale is its own line: its coupon is the policy rate of the month plus the "
                        + "margin fixed at the sale, so it sells near 100 and the city carries the dial's risk.";
            case "Linked":
                return "Each linked sale is its own line: its real coupon is paid on a principal that follows the "
                        + "price index, and at maturity it repays the indexed principal, never under the face.";
            default:
                boolean callable = callableAsked(kit, false);
                Debt line = ui.game.getDebtManager().benchmarkLine(borrowTerm, callable);
                if (line == null) {
                    return String.format("This opens a new %d-year %sline: its coupon is the stop, rounded down to an "
                            + "eighth of a point, so it sells just under 100.", borrowTerm, callable ? "callable " : "");
                }
                return String.format("This reopens the %s%s, the %d-year line: same coupon, same date, sold at today's "
                        + "price. It is the line until it has under %s of its %d years left.",
                        callable ? "callable " : "", pieceName(line), borrowTerm, pc(Auction.BENCHMARK_LIFE), borrowTerm);
        }
    }

    /** The CALLABLE switch's (i) (CD4, spec-city-debt.md 2.6 and D22 to D24). */
    static final String CALLABLE_SALE_INFO = "A bond of ten years or more can be sold callable: from its first call date "
            + "(five years on a 10-year bond, ten on a 20, 30 or 50) the city may pay every holder 100 for each 100 of "
            + "face, whole, and close the line. The buyers are paid for the right it keeps - the sale's fair yield is "
            + "the non-call fair plus the option's price, worked out on a rate tree fitted to the curve at the "
            + "volatility the city's own rate has shown, never under "
            + String.format("%.2f", CallTree.RATE_VOL_FLOOR * 100) + " points a year - so the coupon you see already "
            + "carries it. A callable sale reopens only a callable line of the same call terms. Nothing sells callable "
            + "unless it is asked.";

    /** True when the Borrow page's bond may be sold callable (CD4): a bond at home of ten years or more. */
    boolean callableOffered(Instrument kit, boolean foreign) {
        return !foreign && "Term".equals(kit.key()) && CallTree.canBeCallable(borrowTerm);
    }

    /** True when the switch is on and the paper may be callable: the quote, the booking and the line note all read this. */
    boolean callableAsked(Instrument kit, boolean foreign) {
        return borrowCallable && callableOffered(kit, foreign);
    }

    /**
     * The CALLABLE switch (CD4), beside the bond's term: two chips, off by
     * default, kept across the clock's redraw in borrowCallable. On, it says
     * from when the call is and what it adds to the coupon - the quote's own
     * (callWords, Game.callSpread()) - or, with nothing asked yet, that the
     * quote will.
     */
    VBox callableSwitch(DebtQuote quote) {
        String[] names = {"Not callable", "Callable"};
        String on = quote != null && quote.callable() && callWords != null
                ? "Callable at " + String.format("%.0f", CallTree.CALL_PRICE) + " " + callWords + "."
                : "Callable at " + String.format("%.0f", CallTree.CALL_PRICE) + " from year "
                        + CallTree.firstCallYears(borrowTerm) + " on: the coupon pays for the right. Ask for something and "
                        + "the quote says what it adds.";
        return setting("CALLABLE", CALLABLE_SALE_INFO, names, borrowCallable ? names[1] : names[0],
                new String[] {"The city cannot call it before it falls due.", on}, null, pick -> {
                    borrowCallable = pick.equals(names[1]);
                    showFinanceMenu();
                });
    }

    /** A callable sale's line on the auction card (CD4): from when it can be called and what the call adds to the coupon. Called only for a callable quote. */
    HBox callableLine() {
        return cardLine("Callable at " + String.format("%.0f", CallTree.CALL_PRICE), callWords == null ? "" : callWords,
                null, CALLABLE_SALE_INFO);
    }

    /* ----------------------------- the highest yield (CD2) ----------------------------- */

    /**
     * THE HIGHEST YIELD: fair + a point (the default), a yield the player
     * types in per cent, or Fill it - the cap chips (CapChips), bound to the
     * Borrow page's own cap. `fair` is what the paper is worth now.
     */
    List<Node> capControls(double fair) {
        return borrowChips.controls("THE HIGHEST YIELD YOU WILL TAKE", CAP_INFO, fair, true);
    }

    /* ----------------------------- the ask, typed (0.7.40) ----------------------------- */

    /** HOW MUCH's (i): what the box takes - the case rules - and what the buttons do. */
    static final String ASK_TYPED_INFO = "Type an amount in dollars: digits, with or without commas, a decimal "
            + "point if you like, and k, M, B or T after them for thousands, millions, billions or trillions - "
            + "capital or small, so 40b is 40B and 750m is 750M (an m is never a thousandth). A D$, $ or US$ in "
            + "front is ignored. Enter, or leaving the box, sets the ask; what cannot be read is said under the box "
            + "and the ask stays as it was. ÷10 and ×10 scale it; the steps are one unit of its leading digit and "
            + "a tenth of that, never less than a lot; the presets are the city's own figures.";

    /** A typed amount: an optional mark, digits (grouped by commas or not), an optional fraction, an optional unit. */
    private static final java.util.regex.Pattern ASK_WORDS = java.util.regex.Pattern.compile(
            "(?:[A-Za-z]{0,3}\\$)?\\s*((?:\\d{1,3}(?:,\\d{3})+|\\d+)(?:\\.\\d+)?|\\.\\d+)\\s*([kKmMbBtT])?");

    /** The ask a typed amount sets, in the model's thousands (Money.toDollars()'s unit), by ASK_TYPED_INFO's rules; NaN when it is not an amount. Pure. */
    static double askFromWords(String typed) {
        if (typed == null) return Double.NaN;
        java.util.regex.Matcher m = ASK_WORDS.matcher(typed.trim());
        if (!m.matches()) return Double.NaN;
        double scale = switch (m.group(2) == null ? "" : m.group(2).toLowerCase(java.util.Locale.ROOT)) {
            case "k" -> 1e3;
            case "m" -> 1e6;
            case "b" -> 1e9;
            case "t" -> 1e12;
            default  -> 1;
        };
        double dollars = Double.parseDouble(m.group(1).replace(",", "")) * scale;
        return Double.isFinite(dollars) ? dollars / 1000 : Double.NaN;
    }

    /** What the box says under itself when an entry cannot be read. Pure. */
    static String askRefusal(String typed) {
        return "“" + typed.trim() + "” is not an amount: digits, then k, M, B or T - 40B, 2.5T, 750M "
                + "or 12,000,000. The ask stays as it was.";
    }

    /** The ask written in full and short: {"D$2,500,000,000,000", "D$2.5T"} - the second null when it would say the same; nothing asked, {"nothing asked for yet", null}. Pure. */
    String[] askShown(double ask, boolean foreign) {
        if (!(ask > 0)) return new String[] { "nothing asked for yet", null };
        String full = marked(foreign ? Currency.FOREIGN_SYMBOL : sym(), Money.cash(ask));
        String brief = foreign ? usd(ask) : d(ask);
        return new String[] { full, full.equals(brief) ? null : brief };
    }

    /** The ± steps for an ask: one unit of its leading digit and a tenth of that - D$1B and D$100M at D$3.4B - never under a lot; at nothing, one lot. Pure. */
    static double[] askSteps(double ask, double lot) {
        if (!(ask > 0)) return new double[] { lot };
        double big = Math.max(lot, Math.pow(10, Math.floor(Math.log10(ask))));
        double fine = Math.max(lot, big / 10);
        return fine < big ? new double[] { big, fine } : new double[] { big };
    }

    /** What a step or a scale starts from: what is typed and not yet set, when it reads as an amount, else the ask. */
    double askBase() {
        double typed = askTyped == null ? Double.NaN : askFromWords(askTyped);
        return Double.isNaN(typed) ? borrowAsk : typed;
    }

    /** The ask set by a step, a preset or clear: what was typed and the refusal go, and the page is drawn on it. */
    void setAsk(double ask) {
        borrowAsk = Double.isFinite(ask) ? Math.max(0, ask) : 0;
        askTyped = null;
        askRefused = null;
        askField = null;
        showFinanceMenu();
    }

    /** One preset of the ask: what it is called, and the model's figure it sets. */
    record AskPreset(String name, double ask) { }

    /**
     * The ask's presets, every figure a model getter's, each offered only
     * when it is something: at home, the minimum issue (at least a lot, as
     * "the minimum" always set it), what falls due in the next twelve months
     * (DebtManager.dueWithin()), a month of the treasury's spending
     * (Game.monthOfSpending()), a year of tax as the market prices it
     * (DebtManager.annualCapacityRevenue()) and what the treasury is
     * overdrawn by (Game.cashShortfall()); abroad, in the dollars the ask is
     * in, the minimum (Game.minimumIssueSizeUsd()) and what the dollar paper
     * asks in the next twelve months (DebtManager.dueAbroadWithinUsd()).
     * Pure.
     */
    List<AskPreset> askPresets(Instrument kit, boolean foreign) {
        Game g = ui.game;
        DebtManager ledger = g.getDebtManager();
        List<AskPreset> out = new ArrayList<>();
        out.add(new AskPreset("the minimum", Math.max(foreign ? g.minimumIssueSizeUsd() : g.minimumIssueSize(), kit.rounding())));
        double[] figures = foreign
                ? new double[] { ledger.dueAbroadWithinUsd(12) }
                : new double[] { ledger.dueWithin(12), g.monthOfSpending(), ledger.annualCapacityRevenue(), g.cashShortfall() };
        String[] names = foreign
                ? new String[] { "falls due abroad within a year" }
                : new String[] { "falls due within a year", "a month's spending", "a year of tax", "overdrawn by" };
        for (int i = 0; i < figures.length; i++) {
            if (figures[i] > 0 && Double.isFinite(figures[i])) out.add(new AskPreset(names[i], figures[i]));
        }
        return out;
    }

    /**
     * The box the ask is typed in (0.7.40), the house's search box: what is
     * typed and not yet set lives through a month's redraw, and the focus
     * with it. Enter sets the ask; so does leaving the box - checked once the
     * leaving is over, so a redraw that took the box away is not mistaken
     * for it.
     */
    Node askBox(boolean foreign) {
        if (askTyped != null && askTypedOver != borrowAsk) askTyped = null;
        javafx.scene.control.TextField box = searchBox(askTyped == null ? "" : askTyped,
                foreign ? "type it: 40M, 2.5B" : "type it: 40B, 2.5T, 750M", 240, typed -> {
                    askTyped = typed;
                    askTypedOver = borrowAsk;
                }, null);
        box.setOnAction(e -> commitAsk(box, true));
        box.focusedProperty().addListener((o, was, now) -> {
            if (now) return;
            javafx.application.Platform.runLater(() -> {
                if (box.getScene() != null && !box.isFocused()) commitAsk(box, false);
            });
        });
        boolean again = askTyping;
        askTyping = false;
        askField = box;
        if (again) {
            javafx.application.Platform.runLater(() -> {
                box.requestFocus();
                box.positionCaret(box.getText().length());
            });
        }
        HBox row = new HBox(Palette.GAP, box, words("Enter sets it", Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /**
     * What is in the box, set as the ask - or, when it cannot be read, said
     * under the box with the ask left as it was. Enter draws the page at
     * once; leaving the box asks the window for a redraw, which waits for the
     * mouse button to come up (UserInterface.redrawSoon()), so the press that
     * took the focus away still lands on what it was aimed at.
     */
    private void commitAsk(javafx.scene.control.TextField box, boolean enter) {
        String typed = box.getText() == null ? "" : box.getText().trim();
        boolean changed;
        if (typed.isEmpty()) {
            changed = askRefused != null;
            askTyped = null;
            askRefused = null;
        } else {
            double ask = askFromWords(typed);
            if (Double.isNaN(ask)) {
                changed = !askRefusal(typed).equals(askRefused);
                askRefused = askRefusal(typed);
                askTyped = typed;
                askTypedOver = borrowAsk;
            } else {
                changed = true;
                borrowAsk = ask;
                askTyped = null;
                askRefused = null;
            }
        }
        askField = null;
        if (enter) showFinanceMenu();
        else if (changed) ui.redrawSoon();
    }

    /** A kind's tile line abroad: dollar paper is the world's, at its price, not an auction's (spec-city-debt.md 2.7). */
    static String abroadLine(Instrument kit) {
        return "Note".equals(kit.key()) ? "At a discount: no coupon." : "The world's price; the whole face at the end.";
    }

    /** ...and its (i): what dollar paper is. */
    static String abroadBlurb(Instrument kit) {
        return "Note".equals(kit.key())
                ? "Dollars borrowed at a discount to the face, repaid at the face: no coupon, nothing monthly and "
                        + "everything at once. Sold to the world at its price for the term, not at auction."
                : "A coupon every month on the whole face, in dollars, and the whole face at the end. Sold to the "
                        + "world at its price for the term, not at auction, and never reopened.";
    }

    /**
     * One instrument as a tile: its swatch-tinted icon, its name and (i), the range it is issued over, its line;
     * picked, its ground and edge lit. At home there are five of them in a half-width card (CD5), so the tile is
     * drawn a size down - a smaller icon, a name a point smaller, less padding - and "Amortizing" does not break.
     */
    VBox instrumentTile(Instrument kit, boolean on, boolean foreign) {
        boolean compact = !foreign;
        Label name = new Label(kit.name());
        name.setStyle(Palette.strong(compact ? Palette.SIZE_BODY : Palette.SIZE_BODY + 1, Palette.TEXT_HEAD));
        name.setMinWidth(0);
        name.setWrapText(true);
        HBox top = new HBox(compact ? 4 : 6, iconSquare(Icons.PAPER, kit.colour(), compact ? 20 : 24, compact ? 11 : 13), name,
                infoButton(foreign ? abroadBlurb(kit) : kit.blurb(), true));
        top.setAlignment(Pos.CENTER_LEFT);
        VBox t = new VBox(4, top, muted(kit.range()),
                words(foreign ? abroadLine(kit) : kit.line(), Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        t.setMaxWidth(Double.MAX_VALUE);
        t.setMaxHeight(Double.MAX_VALUE);
        String rest = (compact ? "-fx-padding: 8 6 8 8;" : "-fx-padding: 8 10 8 10;")
                + " -fx-background-radius: 6; -fx-border-radius: 6; -fx-cursor: hand;";
        String ground = on ? " -fx-background-color: " + Palette.PINNED + "; -fx-border-color: " + Palette.ACCENT + ";"
                : " -fx-background-color: " + Palette.PANEL + "; -fx-border-color: " + Palette.EDGE + ";";
        t.setStyle(rest + ground);
        t.setOnMouseClicked(e -> {
            if (borrowType.equals(kit.key())) return;
            borrowType = kit.key();
            borrowTerm = nearestTerm(kit, "Note".equals(kit.key()) ? 6 : 10);
            borrowAsk = 0;
            showFinanceMenu();
        });
        return t;
    }

    /**
     * The terms at home as columns of today's fair yield (DebtManager.
     * fairYield() of the kind at the term's months, with the ask on the
     * books): the chosen in the money blue, the rest grey; a click picks one.
     * The chosen term's is the quote's own fair, so the column and the
     * auction under it agree to the digit. A bond term whose line is still
     * open says under itself which it would reopen, and is struck on the
     * months that line has left, as the quote is.
     */
    Node termColumns(Instrument kit, DebtQuote quote) {
        DebtManager ledger = ui.game.getDebtManager();
        Auction.Kind kind = auctionKind(kit);
        Auction.Result sale = quote == null ? null : quote.auction();
        boolean floating = kind == Auction.Kind.FLOATING, linked = kind == Auction.Kind.LINKED;
        List<Column> cols = new ArrayList<>();
        boolean reopening = false;
        // The tallest bar a floater's margin is drawn against: never under 25 basis points, so a margin near nothing stays low.
        double marginScale = 0;
        if (floating) {
            for (int t : kit.terms()) marginScale = Math.max(marginScale, ledger.fairMargin(termMonths(kit, t), Math.max(0, borrowAsk)));
            marginScale = Math.max(marginScale, 0.0025);
        }
        for (int t : kit.terms()) {
            final int chosen = t;
            // A callable ask (CD4) reopens a callable line, and the columns stay the curve's own yields: the quote's fair is the chosen one's only when the paper cannot be called (its fair carries the option).
            boolean option = callableAsked(kit, false) && CallTree.canBeCallable(t);
            Debt line = "Term".equals(kit.key()) ? ledger.benchmarkLine(t, option) : null;
            int months = line != null ? line.getRemainingMonths() : termMonths(kit, t);
            double comp = ledger.compression(months);
            String off = comp > 1e-6 ? String.format(", %.2f points off for the central bank", comp * 100) : "";
            Runnable pick = () -> { borrowTerm = chosen; showFinanceMenu(); };
            boolean thisOne = t == borrowTerm && sale != null && !sale.isEmpty();
            if (floating || linked) {
                // CD5 (spec-city-debt.md 2.4 and 2.5): the figure is what prices the paper at 100 - a floater's margin over
                // the dial, a linker's real coupon and the inflation it prices - and the chosen term's is the sale's own.
                double face = thisOne ? sale.ask().face() : Math.max(0, borrowAsk);
                String colour = t == borrowTerm ? Palette.MONEY : Palette.TEXT_SPENT;
                String when = t + " " + kit.unit() + ": fair ";
                if (floating) {
                    double margin = thisOne ? sale.ask().fairCoupon() : ledger.fairMargin(months, face);
                    cols.add(new Column(t + " y", null, marginWords(margin),
                            List.of(Segment.of(Math.max(0, margin), colour)), false, null, null,
                            when + marginWords(margin) + " (the policy rate is " + pct2(ledger.getPolicyRate())
                                    + " today)" + (borrowAsk > 0 ? ", for this amount" : "") + off, pick));
                } else {
                    double real = thisOne ? sale.ask().fairCoupon() : ledger.fairRealCoupon(months, face);
                    double inflation = ledger.breakevenInflation(months, face);
                    cols.add(new Column(t + " y", "prices " + pc1(inflation) + " inflation", "real " + pct2(real),
                            List.of(Segment.of(Math.max(0, real), colour)), false, null, null,
                            when + "real coupon " + pct3(real) + ", pricing in " + pc1(inflation) + " a year of inflation"
                                    + (borrowAsk > 0 ? ", for this amount" : "") + off, pick));
                }
                continue;
            }
            double fair = thisOne && !option ? sale.ask().fair()
                    : ledger.fairYield(kind, months, Math.max(0, borrowAsk));
            String sub = line == null ? null : "reopens\n" + pieceName(line);
            reopening |= line != null;
            cols.add(new Column(t + ("Note".equals(kit.key()) ? " mo" : " y"), sub, pct2(fair),
                    List.of(Segment.of(fair, t == borrowTerm ? Palette.MONEY : Palette.TEXT_SPENT)), false, null, null,
                    t + " " + kit.unit() + ": fair " + pct3(fair) + (borrowAsk > 0 ? " for this amount" : " today")
                            + (line != null ? ", reopening the " + pieceName(line) : "") + off,
                    pick));
        }
        Node picture = columns(cols, floating ? marginScale : 0, 0, 120);
        if (!reopening) return picture;
        // The line's name takes a second line under the label, which the columns' foot has no room for.
        Region room = new Region();
        room.setMinHeight(14);
        room.setPrefHeight(14);
        return new VBox(0, picture, room);
    }

    /** What the term columns' fair figure is called, by kind (CD5): a yield for a bill and the bonds, a margin for a floater, a real coupon for a linker. */
    static String fairWord(Instrument kit) {
        return switch (kit.key()) {
            case "Float"  -> "MARGIN";
            case "Linked" -> "REAL COUPON";
            default       -> "YIELD";
        };
    }

    /** The terms abroad as chips, each with the world's rate for it in its tooltip. */
    Node termChips(Instrument kit) {
        DebtManager ledger = ui.game.getDebtManager();
        List<String> names = new ArrayList<>();
        for (int t : kit.terms()) names.add(String.valueOf(t));
        String[] all = names.toArray(new String[0]);
        javafx.scene.layout.FlowPane chips = chipStrip(all, String.valueOf(borrowTerm), Palette.SIZE_LABEL, pick -> {
            borrowTerm = Integer.parseInt(pick);
            showFinanceMenu();
        });
        chips.setAlignment(Pos.CENTER_LEFT);
        for (int i = 0; i < all.length && i < chips.getChildren().size(); i++) {
            int t = Integer.parseInt(all[i]);
            int months = "Note".equals(kit.key()) ? t : t * 12;
            if (chips.getChildren().get(i) instanceof Button b) {
                Tooltip tip = new Tooltip("the world's rate at " + t + " " + kit.unit() + ": " + pct2(ledger.foreignCurveRate(months)));
                tip.setShowDelay(Duration.millis(200));
                b.setTooltip(tip);
            }
        }
        return chips;
    }

    /** What the quoted paper is called on its card: "10-year bond", "6-month bill", "20-year amortizing bond", "5-year floating-rate note", "30-year inflation-linked bond", "... in dollars". */
    String quoteName(Instrument kit, boolean foreign) {
        String what = switch (kit.key()) {
            case "Note"   -> borrowTerm + "-month bill";
            case "Serial" -> borrowTerm + "-year amortizing bond";
            case "Float"  -> borrowTerm + "-year floating-rate note";
            case "Linked" -> borrowTerm + "-year inflation-linked bond";
            default       -> borrowTerm + "-year " + (callableAsked(kit, foreign) ? "callable " : "") + "bond";
        };
        return what + (foreign ? " in dollars" : "");
    }

    /**
     * THE QUOTE: the offer card on exactly the terms the button books. At
     * home that is the auction at the player's highest yield (auctionQuote());
     * abroad the land office's card, and why the proceeds are not the ask.
     */
    VBox quoteCard(Instrument kit, DebtQuote quote, boolean foreign) {
        Game g = ui.game;
        DebtManager ledger = g.getDebtManager();
        Auction.Result sale = foreign || quote == null ? null : quote.auction();
        if (quote == null || quote.isEmpty()) {
            if (sale != null && !sale.isEmpty()) {
                // Asked, and not a dollar sold: the bids do not reach any of it at the highest yield taken.
                VBox none = card(caption("THE QUOTE", null), words(sale.cap().fill()
                        ? "No bidder's rule takes any of this at any yield: nothing would be sold."
                        : String.format("No bidder bids anything at or under %s, the highest yield you will take: "
                                + "nothing would be sold. Raise it, or choose Fill it.", pct3(sale.ceiling())),
                        Palette.SIZE_BODY + 1, Palette.BAD_TEXT));
                none.setStyle(cardStyle(Palette.BAD));
                return none;
            }
            return card(caption("THE QUOTE", null), words(NO_ASK, Palette.SIZE_BODY + 1, Palette.TEXT_MUTED));
        }
        if (sale != null) return auctionQuote(kit, quote, sale);
        int month = g.getMonth();
        int months = "Note".equals(kit.key()) ? borrowTerm : borrowTerm * 12;
        String ending = "the whole face falls due in " + CityCalendar.formatShort(month + months);
        java.util.function.DoubleFunction<String> w = foreign ? Money::usd : this::d;
        DebtQuote booked = quote;
        int term = borrowTerm;
        double ask = borrowAsk;
        boolean hold = borrowHold;
        Auction.Cap cap = borrowCap;
        VBox offer = offerCard(quoteName(kit, foreign), foreign ? abroadBlurb(kit) : kit.blurb(), quote, pct2(quote.marketRate()) + " a year",
                rateColour(quote, ledger), ending, "Issue for " + w.apply(quote.cashReceived()), () -> {
                    String summary = foreign ? g.handleForeignLogic(kit.key(), ask, term, kit.rounding(), hold)
                                             : executeDebtLogic(kit.key(), ask, term, kit.rounding(), cap);
                    borrowAsk = 0;
                    showDebtResultMenu(booked, foreign, summary);
                }, w);
        if (Math.abs(quote.cashReceived() - borrowAsk) > borrowAsk * .005) {
            Node why = noteLine(String.format("That is %s %s than you asked for.",
                    w.apply(Math.abs(quote.cashReceived() - borrowAsk)),
                    quote.cashReceived() > borrowAsk ? "more" : "less"), PROCEEDS_INFO, 560);
            offer.getChildren().add(Math.max(0, offer.getChildren().size() - 1), why);
        }
        return offer;
    }

    /**
     * THE QUOTE at home (CD2): the offer card of a sale at auction, in the
     * land office's shape - the paper's name and (i), what the sale settles
     * at, the face, the cash, the monthly cost and the cost all in, when it
     * falls due, how the auction went - and the button, which books THIS
     * quote at THIS cap (executeDebtLogic() hands the cap to the same
     * handler). A sale the bids would not cover says what it sells instead
     * of what was asked, and the button says so.
     */
    VBox auctionQuote(Instrument kit, DebtQuote quote, Auction.Result sale) {
        Game g = ui.game;
        DebtManager ledger = g.getDebtManager();
        java.util.function.DoubleFunction<String> w = this::d;
        Label title = new Label(quoteName(kit, false));
        title.setWrapText(true);
        title.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        HBox head = new HBox(Palette.GAP_TIGHT, title, infoButton(kit.blurb(), false));
        head.setAlignment(Pos.CENTER_LEFT);

        Label settles = new Label(settlesAt(kit, sale));
        settles.setWrapText(true);
        settles.setStyle(Palette.figure(Palette.SIZE_BODY, rateColour(quote, ledger)));

        VBox figures = new VBox(3,
                offerLine("Face - what the city owes", w.apply(quote.faceValue()), Palette.TEXT_BODY),
                offerLine("Cash it brings", w.apply(quote.cashReceived()), Palette.GOOD),
                offerLine("Monthly cost", quote.monthlyInterest() > 0 ? w.apply(quote.monthlyInterest()) + " a month"
                        + monthlyWords(kit) : "none - it pays no coupon", Palette.TEXT_BODY),
                offerLine(totalWords(kit), w.apply(quote.totalCost()), Palette.TEXT_BODY));

        int due = g.getMonth() + sale.ask().months();
        Label end = new Label("Serial".equals(kit.key())
                ? "a slice of the face falls due every year, the last in " + CityCalendar.formatShort(due)
                : "Linked".equals(kit.key())
                ? "the face, or the indexed principal if that is more, falls due in " + CityCalendar.formatShort(due)
                : "the whole face falls due in " + CityCalendar.formatShort(due));
        end.setWrapText(true);
        end.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        Label how = new Label(String.format("Stops at %s, %s over fair, covered %.2f×", pct3(sale.stop()), bp(sale.tail()), sale.cover()));
        how.setWrapText(true);
        how.setStyle(Palette.words(Palette.SIZE_LABEL, tailTone(sale)) + " -fx-font-weight: bold;");

        VBox card = new VBox(6, head, settles);
        // A callable sale says from when, and what the call adds to the coupon (CD4).
        if (quote.callable() && callWords != null) {
            Label call = new Label(String.format("Callable at %.0f ", CallTree.CALL_PRICE) + callWords);
            call.setWrapText(true);
            call.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
            card.getChildren().add(call);
        }
        card.getChildren().addAll(figures, end, how);
        if (sale.shortSale()) {
            // A bill's ask is the cash, and its face the lot that would bring it: a short bill says it in cash.
            Label sells = new Label("Note".equals(kit.key())
                    ? String.format("Raises %s of the %s asked: the bids do not reach the face that needs, at %s.",
                            w.apply(quote.cashReceived()), w.apply(borrowAsk), pct3(sale.ceiling()))
                    : String.format("Sells %s of the %s asked: the bids do not reach more at %s.",
                            w.apply(sale.sold()), w.apply(sale.ask().face()), pct3(sale.ceiling())));
            sells.setWrapText(true);
            sells.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.BAD_TEXT) + " -fx-font-weight: bold;");
            card.getChildren().add(sells);
        } else if ("Note".equals(kit.key()) && Math.abs(quote.cashReceived() - borrowAsk) > borrowAsk * .005) {
            card.getChildren().add(noteLine(String.format("That is %s %s than you asked for.",
                    w.apply(Math.abs(quote.cashReceived() - borrowAsk)),
                    quote.cashReceived() > borrowAsk ? "more" : "less"), PROCEEDS_INFO, 560));
        }
        Region push = new Region();
        VBox.setVgrow(push, Priority.ALWAYS);
        card.getChildren().add(push);

        DebtQuote booked = quote;
        int term = borrowTerm;
        double ask = borrowAsk;
        Auction.Cap cap = borrowCap;
        boolean callable = quote.callable();
        Button go = new Button((sale.shortSale() ? "Sell what the bids cover · raise " : "Hold the auction · raise ")
                + w.apply(quote.cashReceived()));
        go.setWrapText(true);
        go.setMaxWidth(Double.MAX_VALUE);
        go.setStyle(Palette.words(Palette.SIZE_LABEL, "white") + " -fx-background-color: " + Palette.CONFIRM + ";");
        go.setOnAction(e -> {
            String summary = executeDebtLogic(kit.key(), ask, term, kit.rounding(), cap, callable);
            borrowAsk = 0;
            showDebtResultMenu(booked, false, summary);
        });
        // A sale that does not cover its own fixed cost raises nothing: the button does not offer it.
        if (!(quote.cashReceived() > 0)) {
            go.setText("Nothing is raised after the fixed cost of " + w.apply(sale.ask().fee()));
            go.setDisable(true);
        }
        card.getChildren().add(go);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setMaxHeight(Double.MAX_VALUE);
        card.setStyle("-fx-padding: 10 12 10 12; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + Palette.EDGE + ";");
        return card;
    }

    /** What the monthly cost is read at (CD5): a floater's at today's dial, a linker's at today's index; nothing to add for the rest. */
    static String monthlyWords(Instrument kit) {
        return switch (kit.key()) {
            case "Float"  -> " at today's dial";
            case "Linked" -> " at today's index";
            default       -> "";
        };
    }

    /** The cost of the credit's label (CD5): a floater's is at today's dial, a linker's before any indexation; the rest, all in. */
    static String totalWords(Instrument kit) {
        return switch (kit.key()) {
            case "Float"  -> "Cost of the credit, at today's dial";
            case "Linked" -> "Cost of the credit, before any indexation";
            default       -> "Cost of the credit, all in";
        };
    }

    /**
     * What a sale settles at, in a line: a bill's yield and its price for 100; a bond's coupon, line and yield; the
     * amortizing bond's one coupon; a floater's margin over the policy rate, reset monthly, and the coupon it makes
     * at today's dial; a linker's real coupon on the indexed principal and the inflation its term prices in (CD5).
     */
    String settlesAt(Instrument kit, Auction.Result r) {
        return switch (r.ask().kind()) {
            case BILL -> String.format("yields %s · %.3f for 100 of face", pct3(r.stop()), r.pricePer100());
            case BOND -> couponWords(r.coupon()) + " coupon, " + (r.ask().isNewLine() ? "a new line" : "the line's own")
                    + " · yields " + pct3(r.stop());
            case AMORTIZING -> pct3(r.coupon()) + " coupon, one for the whole schedule · repaid in yearly slices";
            // CD5: a floater's coupon field is the margin, not the coupon; the coupon is the dial now and the margin, never under nothing.
            case FLOATING -> "policy rate " + (r.coupon() < 0 ? "− " : "+ ") + pct2(Math.abs(r.coupon()))
                    + " margin, reset monthly · now "
                    + pct2(FloatingRateNote.couponAt(ui.game.getDebtManager().getPolicyRate(), r.coupon()));
            case LINKED -> couponWords(r.coupon()) + " real coupon on an indexed principal · prices "
                    + pc1(ui.game.getDebtManager().breakevenInflation(r.ask().months(), r.ask().face())) + " inflation";
        };
    }

    /** The tail's colour: none is good, some is amber, the highest yield reached is red. */
    static String tailTone(Auction.Result r) {
        if (r.shortSale() || r.tail() >= Auction.MAX_TAIL - 1e-9) return Palette.BAD;
        return r.tail() > 1e-9 ? Palette.WARN : Palette.GOOD;
    }

    /* ----------------------------- the auction, shown (CD2) ----------------------------- */

    /** STOPS AT's (i): the stop and the tail (the spec's 4.3). */
    static final String STOP_INFO = "The one yield at which the bids first cover the issue - never under fair, never "
            + "over your highest yield. Everyone is filled at it, the households first and in full, the rest pro "
            + "rata. The tail is the stop over fair: what selling cost the city, in yield, and the figure to watch.";

    /** BIDS' (i): the cover. */
    static final String COVER_INFO = "What was bid up to your highest yield, each bidder counted at most the issue, "
            + "over the issue. Over 1× the issue is covered with room to spare; under it, the sale is short.";

    /**
     * THE AUCTION: the four bids against yield with the issue, fair and the
     * highest yield drawn (AuctionChart), then the result line by line, and
     * the bank's room. Run on the city as it stands; with nothing asked, the
     * bank's room alone. `r` is the quote's own result, and `quote` the quote
     * (null: none): a callable one adds its line to the result (CD4).
     */
    VBox auctionCard(Instrument kit, Auction.Result r, DebtQuote quote) {
        Game g = ui.game;
        String info = String.format(BUYERS_INFO, pc(Bank.RISK_CITY));
        VBox card = card(caption("THE AUCTION", info, hint("run on the city as it stands")));
        if (r == null || r.isEmpty()) {
            card.getChildren().add(words("Ask for something and the auction runs here: the four bids against yield, "
                    + "where it stops, and who takes how much.", Palette.SIZE_BODY + 1, Palette.TEXT_MUTED));
            card.getChildren().addAll(bankRoomRows(null));
            return card;
        }
        Auction.Bidders bidders = g.auctionBidders(r.ask().months());
        boolean fill = r.cap().fill();
        card.getChildren().add(new AuctionChart(r, bidders, this::d));
        card.getChildren().add(keyRow(
                keySwatch(AuctionChart.WHO_COLOURS[0], "households"), keySwatch(AuctionChart.WHO_COLOURS[1], "the firms"),
                keySwatch(AuctionChart.WHO_COLOURS[2], "the world"), keySwatch(AuctionChart.WHO_COLOURS[3], "the bank"),
                keySwatch(Palette.TEXT_HEAD, "the issue"), keySwatch(Palette.TEXT_MUTED, "fair, dashed"),
                fill && !r.shortSale() ? null : keySwatch(Palette.BAD, fill ? "the most any rule bids, dashed" : "your highest yield, dashed")));

        Auction.Fill f = r.fills();
        String coupon, couponLabel = "Coupon · price";
        switch (r.ask().kind()) {
            case BILL -> {
                couponLabel = "Price · discount rate · yield";
                coupon = String.format("%.3f · %s · %s", r.pricePer100(), pct3(r.discountRate()), pct3(r.stop()));
            }
            case BOND -> coupon = (r.ask().isNewLine() ? couponWords(r.coupon()) : "the line's " + couponWords(r.coupon()))
                    + String.format(" · %.3f", r.pricePer100());
            // CD5: the sale's coupon is a floater's margin over the dial and a linker's real coupon.
            case FLOATING -> {
                couponLabel = "Margin over the dial · price";
                coupon = marginWords(r.coupon()) + String.format(" · %.3f", r.pricePer100());
            }
            case LINKED -> {
                couponLabel = "Real coupon · price";
                coupon = couponWords(r.coupon()) + String.format(" · %.3f", r.pricePer100());
            }
            default -> coupon = pct3(r.coupon()) + String.format(" · %.3f", r.pricePer100());
        }
        // The result line by line at the left, and what the bank has to bid with at the right.
        VBox result = new VBox(8);
        result.getChildren().addAll(
                cardLine("Stops at · tail over fair", pct3(r.stop()) + " · " + bp(r.tail()), tailTone(r), STOP_INFO),
                cardLine(fill ? "Bids, up to the most any rule bids (" + pct3(r.ceiling()) + ")"
                                : "Bids, up to your highest yield (" + pct3(r.ceiling()) + ")",
                        String.format("%.2f× the issue", r.cover()), r.cover() >= 1 ? null : Palette.BAD_SOFT, COVER_INFO),
                cardLine(couponLabel, coupon, null),
                fillLine("Households, first", f.households(), r.sold()),
                fillLine("The firms", f.firms(), r.sold()),
                fillLine("The world", f.world(), r.sold()),
                fillLine("The bank", f.bank(), r.sold()),
                cardLine("The central bank", "none · it bids only to roll its own", Palette.TEXT_MUTED),
                cardLine("You raise", d(r.proceeds()), Palette.GOOD, "The price per 100 times the face sold, less the "
                        + "fixed cost of " + d(r.ask().fee()) + " a sale."));
        // A callable sale says from when, and what the call adds to the coupon (CD4): after the coupon's line.
        if (quote != null && quote.callable()) result.getChildren().add(3, callableLine());
        if (!r.shortSale() && r.tail() > Auction.MAX_TAIL + 1e-9) {
            result.getChildren().add(muted(String.format("The tail costs the city %s a year: %s sold, %s over fair.",
                    d(r.sold() * r.tail()), d(r.sold()), bp(r.tail()))));
        }
        VBox bank = new VBox(8);
        bank.getChildren().addAll(bankRoomRows(r));
        card.getChildren().add(pair(result, bank));
        return card;
    }

    /** One bidder's fill: its face, and its share of what was sold. */
    HBox fillLine(String who, double face, double sold) {
        return cardLine(who, d(face) + (sold > 0 ? " · " + pc(face / sold) : ""), face > 0 ? null : Palette.TEXT_MUTED);
    }

    /**
     * THE RED CARD (the spec's section 7, board 4): a sale the bids would not
     * cover. Capped, it says what the bids reach, what that sells at and how
     * much of the face is not sold; with Fill it and still short, that no
     * bidder's rule takes more at any yield. Null when the sale covers.
     */
    Node shortCard(Auction.Result r) {
        if (r == null || r.isEmpty() || !r.shortSale()) return null;
        String what = r.cap().fill()
                ? String.format("Fill it, and still short: no bidder's rule takes more at any yield. The most they "
                        + "bid, up to %s, is %s - %.2f× the issue. It sells that at %s, a tail of %s, and %s of face "
                        + "is not sold.",
                        pct3(r.ceiling()), d(r.sold()), r.cover(), pct3(r.stop()), bp(r.tail()), d(r.shortBy()))
                : String.format("Capped at %s: the bids reach only %s, %.2f× the issue. It sells that at %s, a tail "
                        + "of %s, and %s of face is not sold.",
                        pct3(r.ceiling()), d(r.sold()), r.cover(), pct3(r.stop()), bp(r.tail()), d(r.shortBy()));
        Label head = new Label("THIS SALE WOULD NOT COVER");
        head.setStyle(Palette.strong(Palette.SIZE_LABEL + 1, Palette.BAD_TEXT));
        VBox red = new VBox(6, head,
                words(what, Palette.SIZE_BODY + 1, Palette.BAD_TEXT),
                words(r.cap().fill()
                        ? "Make the sale smaller, or sell it in more than one piece."
                        : "Raise the highest yield, choose Fill it, or make the sale smaller. What is not sold is not "
                                + "raised: the treasury's bills are paid from its cash, and past that from the central "
                                + "bank's advances, as ever.",
                        Palette.SIZE_LABEL, Palette.BAD_SOFT));
        red.setMaxWidth(Double.MAX_VALUE);
        red.setStyle(cardStyle(Palette.BAD));
        return red;
    }

    /**
     * THE BANK'S ROOM, under the auction (what WHO BUYS IT's bar was, before
     * CD2): its book, risk-weighted, against its room, with what the bank
     * takes of this sale as a ghost; or why it bids nothing - failed, or no
     * room. `r` is the sale (null: nothing asked).
     */
    List<Node> bankRoomRows(Auction.Result r) {
        Bank bank = ui.game.getBank();
        double room = bank.isInsolvent() ? 0 : bank.equity() / Bank.CAPITAL_RATIO;   // D20: its room is its capital (CD2, star 6)
        List<Node> out = new ArrayList<>();
        out.add(caption("THE BANK'S ROOM", null));
        if (bank.isInsolvent()) {
            out.add(words("The bank has failed and bids nothing: the auction sells what the others bid, at your highest "
                    + "yield. Resolve it first - the hub's card, or the Bank tab.", Palette.SIZE_BODY + 1, Palette.BAD_SOFT));
            return out;
        }
        if (room <= 0) {
            out.add(words("The bank has no capital room, so it bids nothing: the auction sells what the households, "
                    + "the firms and the world bid, at your highest yield. In its first months, before it has any "
                    + "capital, the bank stands behind the sale at fair, funded at the central bank's window.",
                    Palette.SIZE_BODY + 1, Palette.BAD_SOFT));
            return out;
        }
        double weighted = bank.getWeightedBook();
        double issue = r == null || r.isEmpty() ? 0 : r.fills().bank() * Bank.RISK_CITY;
        out.add(segmentBar(List.of(seg(weighted, Palette.MONEY, "the bank's book, risk-weighted " + d(weighted)),
                        ghost(issue, Palette.MONEY, null, "what the bank takes of this sale, at treasury paper's weight")),
                Math.max(room, weighted + issue), List.of(new Tick(room, Palette.TEXT_HEAD, 2, "its room " + d(room), null)), 0, 10));
        out.add(line(pc(weighted / room) + " of its room used · treasury paper weighs " + pc(Bank.RISK_CITY)));
        if (bank.strain() > Bank.EASY_STRAIN) {
            out.add(words(String.format("The bank is past comfortable already: %s lent out. Treasury paper ties up less "
                    + "room than a business loan of the same size - but it is still room the shops and the mills were "
                    + "going to use.", pc(weighted / room)), Palette.SIZE_BODY + 1, Palette.BAD_SOFT));
        }
        return out;
    }

    /**
     * The four bids against yield, drawn (board 4): the households, the firms,
     * the world and the bank stacked from the foot, so the top edge is the
     * four summed, over a yield axis from fair less a fifth of a point to the
     * highest yield plus half a point (Fill it: to the stop plus half a
     * point); the face on the vertical axis, the issue a horizontal line at
     * the face, a dashed vertical at fair and one at the highest yield (the
     * most any rule bids, for Fill it, when the sale is short), and a dot at
     * the stop. Every point is Auction.bidsAt() - the arithmetic that clears,
     * not a copy of it - on a grid of yields, with the bank's step at fair
     * plus what its funding needs drawn square. A fixed height and the card's
     * width: it is drawn again when it is given another.
     */
    static final class AuctionChart extends javafx.scene.layout.Pane {
        /** The bidders' colours in Auction.Fill's order, as the holders' bar has them. */
        static final String[] WHO_COLOURS = { Palette.PEOPLE, Palette.BUSINESS, Palette.ORE, Palette.MONEY };
        /** ...and their names, in the same order. */
        static final String[] WHO ={ "households", "the firms", "the world", "the bank" };
        /** Its height and its margins, in pixels; the width it is first drawn at, and the least it is drawn at. */
        static final double H = 240, LEFT = 62, RIGHT = 16, TOP = 16, FOOT = 26, FIRST_W = 720, LEAST_W = 420;
        /** How many yields the bids are read at across the axis. */
        static final int SAMPLES = 60;

        private final Auction.Result r;
        private final Auction.Bidders bidders;
        private final java.util.function.DoubleFunction<String> money;
        /** The width it is drawn at now; 0 before the first. */
        private double drawnAt;

        AuctionChart(Auction.Result r, Auction.Bidders bidders, java.util.function.DoubleFunction<String> money) {
            this.r = r;
            this.bidders = bidders;
            this.money = money;
            setMinSize(LEAST_W, H);
            setPrefSize(FIRST_W, H);
            setMaxSize(Double.MAX_VALUE, H);
            draw(FIRST_W);
        }

        @Override protected void layoutChildren() {
            double w = Math.max(LEAST_W, getWidth());
            if (Math.abs(w - drawnAt) > .5) draw(w);
            super.layoutChildren();
        }

        /** The chart at a width: everything in it, drawn again from the sale. */
        private void draw(double w) {
            drawnAt = w;
            getChildren().clear();
            Auction.Ask ask = r.ask();
            double face = ask.face(), fair = ask.fair();
            boolean fill = r.cap().fill();
            double lo = fair - .002;
            double hi = (fill ? r.stop() : r.ceiling()) + .005;
            if (hi - lo < .01) hi = lo + .01;
            final double from = lo, to = hi;
            final double top = face * 1.4;
            final double plotW = w - LEFT - RIGHT, plotH = H - TOP - FOOT;
            java.util.function.DoubleUnaryOperator x = v -> LEFT + plotW * (v - from) / (to - from);
            java.util.function.DoubleUnaryOperator y = v -> TOP + plotH * (1 - Math.min(Math.max(v, 0), top) / top);

            // The grid: the foot, half the face and the face, labelled in money.
            for (double v : new double[] { 0, face / 2, face }) {
                javafx.scene.shape.Line grid = new javafx.scene.shape.Line(LEFT, y.applyAsDouble(v), w - RIGHT, y.applyAsDouble(v));
                grid.setStroke(javafx.scene.paint.Color.web(Palette.EDGE));
                getChildren().add(grid);
                label(money.apply(v), LEFT - 6, y.applyAsDouble(v) - 7, javafx.geometry.Pos.CENTER_RIGHT, LEFT - 8, Palette.TEXT_MUTED);
            }
            // ...and the yield axis, at a step that leaves a handful of ticks.
            double[] steps = { .0005, .001, .0025, .005, .01, .02, .05 };
            double step = steps[steps.length - 1];
            for (double s : steps) if ((to - from) / s <= 8) { step = s; break; }
            for (double v = Math.ceil(from / step) * step; v <= to + 1e-12; v += step) {
                label(String.format(step < .0025 ? "%.3f%%" : "%.2f%%", v * 100), x.applyAsDouble(v), H - FOOT + 5,
                        javafx.geometry.Pos.CENTER, 70, Palette.TEXT_MUTED);
            }

            // The yields the bids are read at: an even grid, and the points where something changes.
            java.util.TreeSet<Double> ys = new java.util.TreeSet<>();
            for (int i = 0; i <= SAMPLES; i++) ys.add(from + (to - from) * i / SAMPLES);
            double bankStep = fair + bidders.bankExtra();
            for (double special : new double[] { fair, bankStep, r.stop(), r.ceiling() }) {
                if (special > from && special < to) ys.add(special);
            }
            if (bankStep > from && bankStep < to) ys.add(bankStep - 1e-9);
            int n = ys.size();
            double[] at = new double[n];
            double[][] cum = new double[4][n];
            int i = 0;
            for (double yv : ys) {
                Auction.Fill bids = Auction.bidsAt(yv, bidders, ask);
                at[i] = yv;
                cum[0][i] = bids.households();
                cum[1][i] = cum[0][i] + bids.firms();
                cum[2][i] = cum[1][i] + bids.world();
                cum[3][i] = cum[2][i] + bids.bank();
                i++;
            }
            // Stacked areas, foot first; each between its running total and the one under it.
            for (int k = 0; k < 4; k++) {
                javafx.scene.shape.Polygon area = new javafx.scene.shape.Polygon();
                for (int j = 0; j < n; j++) area.getPoints().addAll(x.applyAsDouble(at[j]), y.applyAsDouble(cum[k][j]));
                for (int j = n - 1; j >= 0; j--) area.getPoints().addAll(x.applyAsDouble(at[j]), y.applyAsDouble(k == 0 ? 0 : cum[k - 1][j]));
                area.setFill(javafx.scene.paint.Color.web(WHO_COLOURS[k], .6));
                area.setStroke(null);
                getChildren().add(area);
            }
            // ...and the four summed, the line the stop is read off.
            javafx.scene.shape.Polyline sum = new javafx.scene.shape.Polyline();
            for (int j = 0; j < n; j++) sum.getPoints().addAll(x.applyAsDouble(at[j]), y.applyAsDouble(cum[3][j]));
            sum.setStroke(javafx.scene.paint.Color.web(Palette.ACCENT));
            sum.setStrokeWidth(2.2);
            getChildren().add(sum);

            // The issue: a line at the face across the whole plot.
            javafx.scene.shape.Line issue = new javafx.scene.shape.Line(LEFT, y.applyAsDouble(face), w - RIGHT, y.applyAsDouble(face));
            issue.setStroke(javafx.scene.paint.Color.web(Palette.TEXT_HEAD));
            issue.setStrokeWidth(1.4);
            getChildren().add(issue);
            label("the issue " + money.apply(face), w - RIGHT - 4, y.applyAsDouble(face) - 15, javafx.geometry.Pos.CENTER_RIGHT, 220, Palette.TEXT_HEAD);

            // Fair, dashed, named over its top at its right.
            double fairX = x.applyAsDouble(fair);
            vertical(fairX, Palette.TEXT_MUTED, TOP, H - FOOT);
            label("fair " + pct3(fair), fairX + 4, TOP + 2, javafx.geometry.Pos.CENTER_LEFT, 120, Palette.TEXT_LABEL);

            // The highest yield, dashed: shown when there is one, or when Fill it ran out of rules at it. Named at
            // its left over its top - or at its right when fair's name is in the way.
            double ceiling = r.ceiling();
            if ((!fill || r.shortSale()) && ceiling > from && ceiling < to) {
                double ceilX = x.applyAsDouble(ceiling);
                vertical(ceilX, Palette.BAD, TOP, H - FOOT);
                String named = (fill ? "the most any rule bids " : "your highest yield ") + pct3(ceiling);
                if (ceilX - fairX > 340) label(named, ceilX - 5, TOP + 2, javafx.geometry.Pos.CENTER_RIGHT, 220, Palette.BAD_SOFT);
                else label(named, ceilX + 5, TOP + 14, javafx.geometry.Pos.CENTER_LEFT, 220, Palette.BAD_SOFT);
            }

            // The stop: a dot where the sale clears, on the issue when it covers and on the bids when it does not.
            double sx = x.applyAsDouble(r.stop()), sy = y.applyAsDouble(r.sold());
            javafx.scene.shape.Circle dot = new javafx.scene.shape.Circle(sx, sy, 5, javafx.scene.paint.Color.web(Palette.WARN));
            dot.setStroke(javafx.scene.paint.Color.web(Palette.STAGE));
            Tooltip tip = new Tooltip(String.format("Stops at %s, %s over fair: %s sold, %.2f× the issue bid up to %s.",
                    pct3(r.stop()), bp(r.tail()), money.apply(r.sold()), r.cover(), pct3(ceiling)));
            tip.setShowDelay(Duration.millis(150));
            tip.setWrapText(true);
            tip.setMaxWidth(320);
            Tooltip.install(dot, tip);
            getChildren().add(dot);
            String stopWords = (r.shortSale() ? "short: sells " + money.apply(r.sold()) + " at " : "stops at ") + pct3(r.stop())
                    + " · tail " + bp(r.tail());
            // Under the dot, or over it when it sits too near the axis for the words.
            double wordsY = sy + 18 > H - FOOT ? sy - 20 : sy + 6;
            if (sx > w - 300) label(stopWords, sx - 9, wordsY, javafx.geometry.Pos.CENTER_RIGHT, 280, Palette.TEXT_HEAD);
            else label(stopWords, sx + 9, wordsY, javafx.geometry.Pos.CENTER_LEFT, 280, Palette.TEXT_HEAD);
        }

        /** A dashed vertical line at x, in a colour, between two heights. */
        private void vertical(double x, String colour, double from, double to) {
            javafx.scene.shape.Line line = new javafx.scene.shape.Line(x, from, x, to);
            line.setStroke(javafx.scene.paint.Color.web(colour));
            line.setStrokeWidth(1.2);
            line.getStrokeDashArray().addAll(4.0, 3.0);
            getChildren().add(line);
        }

        /**
         * A label of the chart's: `x` is where the text starts, ends or centres
         * by `align`, in a box `wide` pixels across (a Pane gives its children
         * their preferred size, so the box is the alignment's room).
         */
        private void label(String text, double x, double y, javafx.geometry.Pos align, double wide, String colour) {
            Label l = new Label(text);
            l.setStyle(Palette.figure(Palette.SIZE_CAPTION, colour));
            l.setAlignment(align);
            l.setMinWidth(wide);
            l.setPrefWidth(wide);
            l.setMaxWidth(wide);
            double left = align == javafx.geometry.Pos.CENTER_RIGHT ? x - wide : align == javafx.geometry.Pos.CENTER ? x - wide / 2 : x;
            l.relocate(left, y);
            l.setMouseTransparent(true);
            getChildren().add(l);
        }
    }

    /** AND THE DOLLARS: convert and spend, or hold as reserves (the spec's W8). */
    VBox dollarsCard() {
        String[] names = {"Convert and spend it", "Hold it as reserves"};
        return card(setting("AND THE DOLLARS", DOLLARS_INFO, names, borrowHold ? names[1] : names[0],
                new String[] {"Spending it leaves a dollar debt with nothing behind it.",
                        "Holding it leaves the net position unchanged and the next bond cheaper - and buys nothing today."},
                null, pick -> {
                    borrowHold = pick.startsWith("Hold");
                    showFinanceMenu();
                }));
    }

    /** The door marked do not open, outlined in red. Only where there is something to walk away from. */
    HBox foreignDoor() {
        Button repudiate = new Button("Default on the foreign debt…");
        repudiate.setStyle("-fx-background-color: transparent; -fx-border-color: " + Palette.BAD + ";"
                + " -fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: " + Palette.BAD_SOFT + ";");
        repudiate.setOnAction(e -> showForeignDefaultMenu());
        HBox row = new HBox(repudiate);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 6 0 4 0;");
        return row;
    }

    /** The default page's (i). */
    static final String DEFAULT_INFO = "Walking away from every dollar the city owes abroad. The gain is immediate and "
            + "enormous and the cost is five years away, which is precisely the shape of decision a confirmation page "
            + "exists for - both halves are on it in the same size type.";

    /**
     * Asking twice, with the bill written out: "Finances › Default abroad"
     * (0.7.32, the spec's D14), two equal cards in the same type, "Keep
     * paying" first. It lit the Trade tab, its old place (B7); it is this
     * tab's page, reached from Borrow › Abroad.
     */
    void showForeignDefaultMenu() {
        ui.clearMenu("showForeignDefaultMenu", () -> showForeignDefaultMenu());
        DebtManager market = ui.game.getDebtManager();
        VBox page = widePage();
        VBox wipes = card(caption("WHAT IT WIPES", null),
                cardLine("Written off", d(market.getForeignPrincipal()), null),
                line(usd(market.getForeignPrincipalUsd()) + " of paper, at " + perUsd(market.getExchangeRate())),
                cardLine("A year of payments, gone", d(market.nextYearService()), null));
        VBox costs = card(caption("WHAT IT COSTS", null),
                line("No lender abroad will take this city's paper for five years,"),
                line(String.format("and %.0f points on its rate when they will again,", DebtManager.DEFAULT_SCAR * 100)),
                line("fading over about five years after that."),
                line("Every ratio on the trade screen will look better tomorrow."),
                line("None of them is what stops the next bond being sold."));
        page.getChildren().add(pair(wipes, costs));
        Button keep = new Button("Keep paying");
        keep.setOnAction(e -> open("Borrow", "Abroad", null));
        Button confirm = new Button("Default. I understand nobody will lend abroad for five years.");
        confirm.setStyle("-fx-background-color: " + Palette.ALERT_EDGE + "; -fx-text-fill: white;");
        confirm.setOnAction(e -> {
            ui.game.defaultOnForeignDebt("the city chose to");
            open(null, null, null);
        });
        HBox act = new HBox(Palette.GAP_LOOSE, keep, confirm);
        act.setAlignment(Pos.CENTER_LEFT);
        page.getChildren().add(act);
        VBox frame = new VBox(Palette.GAP, pageHead("Finances", Palette.MONEY, () -> open(null, null, null),
                "Default abroad", DEFAULT_INFO, backDoor("Borrow", () -> open("Borrow", "Abroad", null))));
        frameOver(frame, page);
    }

    /* =====================================================================
       MONEY (0.7.0; redrawn 0.7.32)

       Jerus: "the feds sheet would show how much debt it holds, like debt to
       itself aka money printing ... and that is the M2 supply or what do you
       think?" Who made the money, and how much is there? THE CENTRAL BANK'S
       BOOK as two bars on one scale - what it holds against what it owes,
       equity the difference, a shortfall red - beside WHAT THE PUBLIC HOLDS,
       M2 as a bar of its parts; THIS MONTH, made and destroyed as bars of
       their parts; and M2, M0 and the advances over their last ten years,
       as City History's charts open. Every figure is a
       getter on CentralBank, Game or Bank; nothing is worked out here.
       ===================================================================== */

    /** The page's (i): the old page's two sentences, the spec's T64 to T66. */
    String moneyInfo() {
        CentralBank cb = ui.game.getCentralBank();
        DebtManager ledger = ui.game.getDebtManager();
        String s = String.format("The central bank has made %s and not taken it back: %s lent to the bank at its window, "
                        + "%s advanced to the treasury, and %s paid out on reserves beyond what it has earned%s.",
                d(cb.m0()), d(cb.getAdvancesToBank()), d(cb.getAdvancesToTreasury()), d(cb.getLossCarried()),
                cb.getRemittanceDue() > 0 ? ", less the " + d(cb.getRemittanceDue()) + " it owes the treasury at the next press" : "");
        if (cb.getPaperHeld() > 0 || cb.getTargetShare() > 0) {
            s += String.format(" It holds %s of the city's own paper, bought with money it made; the 30-year rate is "
                            + "%.2f points lower for it. Its dial aims at %.0f%% of the city's term paper and it holds %.0f%%.",
                    d(cb.getPaperHeld()), ledger.compression(360) * 100, cb.getTargetShare() * 100,
                    ledger.centralBankShareOfTerm() * 100);
        }
        return s + String.format(" %s is chasing goods - everything the public holds at the bank - against a month's "
                + "output of %s.", d(ui.game.getM2()), d(ui.game.getEconomyManager().getMonthGdp()));
    }

    void moneyPage(VBox page) {
        CentralBank cb = ui.game.getCentralBank();
        page.getChildren().add(infoLine("The central bank has made " + d(cb.m0()) + " and not taken it back; the public "
                + "holds " + d(ui.game.getM2()) + ".", moneyInfo(), true, Palette.SIZE_BODY + 1, Palette.TEXT_LABEL, 900));
        page.getChildren().add(pair(centralBookCard(cb), publicCard(cb)));
        page.getChildren().add(thisMonthCard(cb));
        HistorySave h = ui.game.getHistorySave();
        if (h.months() >= 2) {
            page.getChildren().add(pair(
                    smallChart("M2 AND M0", h, new String[] {"m2", "m0"}, new String[] {"M2", "M0"},
                            new String[] {Palette.ACCENT, Palette.PEOPLE}),
                    smallChart("ADVANCED TO THE TREASURY", h, new String[] {"advancesToTreasury"},
                            new String[] {"Advanced to the treasury"}, new String[] {Palette.MONEY})));
        }
    }

    /** The book's (i): the spec's T67 and T68. */
    String centralBookInfo(CentralBank cb) {
        Bank bank = ui.game.getBank();
        String s = String.format("Its liabilities are every dollar it has made and not taken back, which is what M0 is. "
                        + "Nobody here holds cash outside the bank, so all of it is reserves. The commercial bank's own "
                        + "spare cash - %s - is what it is paid the policy rate on, %s a year. The vault is the city's "
                        + "dollars, which a central bank holds; the treasury still buys and sells them, and since 0.7.2 the "
                        + "central bank sells them when the currency is pushed down - capital spent, which takes equity "
                        + "down with the vault and leaves M0 where it was.",
                d(bank.cashReserves()), pct2(ui.game.getDebtManager().getPolicyRate()));
        if (cb.getLossCarried() > 0) {
            s += String.format("\n\nIt is carrying a loss of %s: what it has paid on reserves beyond what its loans have "
                    + "earned. Nothing is remitted to the treasury until that is made good out of profit.", d(cb.getLossCarried()));
        }
        return s;
    }

    /** THE CENTRAL BANK'S BOOK: what it holds against what it owes, one scale; its equity on the owing side, or a shortfall red on the holding side. */
    VBox centralBookCard(CentralBank cb) {
        double assets = cb.totalAssets(), m0 = cb.m0(), equity = cb.equity();
        double scale = Math.max(assets, m0);
        if (!(scale > 0)) scale = 1;
        List<Segment> held = new ArrayList<>(List.of(
                seg(cb.getAdvancesToBank(), Palette.MONEY_DARK, "lent to the bank at the window " + d(cb.getAdvancesToBank())),
                seg(cb.getAdvancesToTreasury(), Palette.MONEY_LIGHT, "advanced to the treasury - printed " + d(cb.getAdvancesToTreasury())),
                seg(cb.getPaperHeld(), Palette.MONEY, "the city's paper it holds " + d(cb.getPaperHeld())),
                seg(cb.getVault(), Palette.ORE, "the vault, at today's rate " + d(cb.getVault()))));
        if (equity < 0) held.add(new Segment(-equity, Palette.BAD, true, null, "short", "what it owes past what it holds "
                + d(-equity), null));
        List<Segment> owes = new ArrayList<>(List.of(
                seg(cb.getReserves(), Palette.MONEY_DARK, "reserves - the money it has made " + d(cb.getReserves())),
                seg(cb.getCurrency(), Palette.MONEY, "currency in circulation " + d(cb.getCurrency()))));
        if (equity > 0) owes.add(seg(equity, Palette.TEXT_SPENT, "equity " + d(equity)));
        VBox card = card(caption("THE CENTRAL BANK'S BOOK", centralBookInfo(cb)));
        card.getChildren().add(cardLine("What it holds", d(assets), null));
        card.getChildren().add(segmentBar(held, scale, null, 0, 14));
        card.getChildren().add(keyRow(keySwatch(Palette.MONEY_DARK, "window " + d(cb.getAdvancesToBank())),
                keySwatch(Palette.MONEY_LIGHT, "the treasury " + d(cb.getAdvancesToTreasury())),
                keySwatch(Palette.MONEY, "the city's paper " + d(cb.getPaperHeld())),
                keySwatch(Palette.ORE, "the vault " + d(cb.getVault()))));
        card.getChildren().add(cardLine("What it owes - M0", d(m0), null));
        card.getChildren().add(segmentBar(owes, scale, null, 0, 14));
        card.getChildren().add(keyRow(keySwatch(Palette.MONEY_DARK, "reserves " + d(cb.getReserves())),
                keySwatch(Palette.MONEY, "currency " + d(cb.getCurrency())),
                equity > 0 ? keySwatch(Palette.TEXT_SPENT, "equity") : null));
        card.getChildren().add(cardLine("Equity", d(equity), equity < 0 ? Palette.BAD : null));
        if (cb.vaultSpent() > 0) card.getChildren().add(muted("spent defending the currency since founding "
                + d(-cb.vaultSpent())));
        return card;
    }

    /** M2's (i): the spec's T69. */
    static final String M2_INFO = "M2 is the bank's deposits - the households', the businesses' and the world's - plus "
            + "currency, which nobody here holds. It grows when the bank lends on into somebody's account or the "
            + "treasury spends money that was printed for it; it does not grow when the central bank lends to the bank.";

    /** WHAT THE PUBLIC HOLDS: M2 as a bar of its parts, and M0 beside it. */
    VBox publicCard(CentralBank cb) {
        Game g = ui.game;
        Bank bank = g.getBank();
        VBox card = card(caption("WHAT THE PUBLIC HOLDS - M2", M2_INFO, hint("made: " + d(cb.m0()))));
        card.getChildren().add(figure(d(g.getM2()), 22, Palette.TEXT_HEAD));
        card.getChildren().add(segmentBar(List.of(
                seg(g.getHouseholdDeposits(), Palette.PEOPLE, "the households' deposits " + d(g.getHouseholdDeposits())),
                seg(g.getSectorDeposits(), Palette.BUSINESS, "the businesses' deposits " + d(g.getSectorDeposits())),
                seg(bank.getForeignDeposits(), Palette.ORE, "money from abroad on deposit " + d(bank.getForeignDeposits())),
                seg(cb.getCurrency(), Palette.MONEY_LIGHT, "currency " + d(cb.getCurrency()))), 0, null, 0, 14));
        card.getChildren().add(keyRow(keySwatch(Palette.PEOPLE, "households " + d(g.getHouseholdDeposits())),
                keySwatch(Palette.BUSINESS, "businesses " + d(g.getSectorDeposits())),
                keySwatch(Palette.ORE, "from abroad " + d(bank.getForeignDeposits())),
                keySwatch(Palette.MONEY_LIGHT, "currency " + d(cb.getCurrency()))));
        card.getChildren().add(cardLine("M0 - what the central bank has made", d(cb.m0()), null));
        return card;
    }

    /** THIS MONTH: the money made and the money destroyed, each a bar of its parts on one scale, and what M0 moved by. */
    VBox thisMonthCard(CentralBank cb) {
        double scale = Math.max(cb.getIssued(), cb.getRetired());
        if (!(scale > 0)) scale = 1;
        VBox card = card(caption("THIS MONTH", null, hint("M0 moved by " + signedD(cb.getM0Moved()))));
        card.getChildren().add(cardLine("Money made", d(cb.getIssued()), null));
        card.getChildren().add(segmentBar(List.of(
                seg(cb.getInterestOnReserves(), Palette.MONEY_DARK, "interest on reserves " + d(cb.getInterestOnReserves())),
                seg(cb.getAdvancedToBank(), Palette.MONEY, "lent at the window " + d(cb.getAdvancedToBank())),
                seg(cb.getAdvancedToTreasury(), Palette.MONEY_LIGHT, "printed for the treasury " + d(cb.getAdvancedToTreasury())),
                seg(cb.getRemitted(), Palette.PEOPLE, "remitted to the treasury " + d(cb.getRemitted())),
                seg(cb.getBoughtPaper(), Palette.ORE, "paid for the city's paper it bought " + d(cb.getBoughtPaper()))),
                scale, null, 0, 12));
        card.getChildren().add(keyRow(keySwatch(Palette.MONEY_DARK, "interest on reserves"),
                keySwatch(Palette.MONEY, "the window"), keySwatch(Palette.MONEY_LIGHT, "printed for the treasury"),
                keySwatch(Palette.PEOPLE, "remitted to the treasury"), keySwatch(Palette.ORE, "paper it bought")));
        card.getChildren().add(cardLine("Money destroyed", d(cb.getRetired()), null));
        card.getChildren().add(segmentBar(List.of(
                seg(cb.getSoldPaper(), Palette.ORE, "paid it for paper it sold " + d(cb.getSoldPaper())),
                seg(cb.getPaperCoupons(), Palette.MONEY_DARK, "coupons on its paper " + d(cb.getPaperCoupons())),
                seg(cb.getPaperRedeemed(), Palette.MONEY, "principal on its paper " + d(cb.getPaperRedeemed())),
                seg(cb.getBoughtBack(), Palette.MONEY_LIGHT, "its paper the city bought back " + d(cb.getBoughtBack()))),
                scale, null, 0, 12));
        card.getChildren().add(keyRow(keySwatch(Palette.ORE, "paper it sold"), keySwatch(Palette.MONEY_DARK, "coupons"),
                keySwatch(Palette.MONEY, "principal"), keySwatch(Palette.MONEY_LIGHT, "bought back")));
        card.getChildren().add(muted("Since founding: printed for the treasury " + d(cb.getPrintedLifetime())
                + " · remitted to it " + d(cb.getRemittedLifetime())));
        return card;
    }

    /** History's money series as a small chart without the controls, on the last ten years (ChartModel.DEFAULT_RANGE) or the whole history while it is younger - TimeChart, as Government's Output draws it. */
    VBox smallChart(String title, HistorySave h, String[] keys, String[] names, String[] colours) {
        List<TimeChart.Line> lines = new ArrayList<>();
        for (int i = 0; i < keys.length; i++) {
            lines.add(new TimeChart.Line(keys[i], names[i], colours[i], 0, h.aligned(keys[i]),
                    v -> HistoryScreen.plotScale("money", v), v -> ui.historyScreen.fmtUnit("money", v), ""));
        }
        ChartModel window = new ChartModel();
        TimeChart chart = new TimeChart(window, java.util.Set.of(), false);
        chart.setData(h.getMonth(), lines, HistoryScreen.axisFor("money"), null, false, false, null,
                List.of(), List.of(), List.of(), "no months recorded yet");
        chart.setSize(560, 140);
        javafx.scene.layout.FlowPane key = keyRow();
        for (int i = 0; i < keys.length; i++) key.getChildren().add(keySwatch(colours[i], names[i]));
        return card(caption(title, null), chart, key);
    }

    /* =====================================================================
       THE BOND MARKET (0.7.12; redrawn 0.7.32)

       The businesses' bonds, not the city's: who borrows on the market, and
       who lends to them? Four figures, who holds them as one bar, a card an
       issuer, the month and the order books; a bond's book as its facts and
       its depth - bids and asks either side of the price, face as length.
       The tables are behind "details". On the Finances tab because it is a
       market beside the city's own paper; a sector's own bonds are on its
       Cash & debt page, the bank's on its Lending page, the world's on the
       Trade tab. Every figure is the market's own getter (BondMarket),
       tinted the businesses' violet (the spec's D12).
       ===================================================================== */

    /** A bond's price, per 100 of face. */
    static String per100(double price) {
        return Double.isFinite(price) && price > 0 ? String.format("%.2f", price * 100) : "—";
    }

    /** No bond outstanding: the spec's T70. */
    static final String NO_BONDS = "None outstanding. A business sells a bond when the book would take it for no more "
            + "than the bank's loan costs, its issuing costs spread over its ten years; a small amount goes to the bank, "
            + "because the fixed part of those costs makes a small bond dear.";

    /** The prices' (i): the spec's T71. */
    static final String PRICES_INFO = "The price is per 100 of face, at the last trade on its book; * where it has not "
            + "traded yet, what it is worth at the city's curve for the months it has left and what a holder expects "
            + "to lose on its issuer a year. The yield is the one that price gives.";

    /** The order books' (i): the spec's T74. */
    static final String BOOKS_INFO = "Everybody posts buy and sell orders at prices, and an order fills only when it "
            + "meets one on the other side, at the price of the one that was there first; the rest wait, and nobody has "
            + "to trade. The households buy by the rule they buy the city's paper by, the companies with idle cash and "
            + "the world by the rules that send money where the return is, and the bank only at the yield an equal loan "
            + "would earn it. Orders are good for a month: each is posted again from that month's rates. A household "
            + "short of money sells into what rests there when that is cheaper than borrowing, and waits when it is not.";

    /** The bonds' holders, in a bar's order: the households, the bank, the companies, the world, the city's fund. */
    static final String[] BOND_HOLDERS = {"households", "the bank", "companies", "the world", "the city's fund"};
    /** ...and their colours on it. */
    static final String[] BOND_HOLDER_COLOURS = {Palette.PEOPLE, Palette.MONEY, Palette.BUSINESS, Palette.ORE, Palette.MONEY_DARK};

    /** A holders bar over five amounts, each keyed when `key` is set. */
    Node holdersBar(double[] held, double band, boolean key) {
        List<Segment> parts = new ArrayList<>();
        double all = 0;
        for (double v : held) all += v;
        javafx.scene.layout.FlowPane k = keyRow();
        for (int i = 0; i < held.length; i++) {
            String share = all > 0 ? " · " + pc(held[i] / all) : "";
            parts.add(seg(held[i], BOND_HOLDER_COLOURS[i], BOND_HOLDERS[i] + " " + d(held[i]) + share));
            if (key && held[i] >= .5) k.getChildren().add(keySwatch(BOND_HOLDER_COLOURS[i], BOND_HOLDERS[i] + " "
                    + d(held[i]) + share));
        }
        Region bar = segmentBar(parts, 0, null, 0, band);
        return key ? new VBox(6, bar, k) : bar;
    }

    void bondMarketPage(VBox page) {
        Game g = ui.game;
        BondMarket market = g.getBondMarket();
        double face = market.totalFace();
        double business = g.getEconomyManager().getBusinessDebtManager().getEverythingOwed();
        List<BondMarket.Issuer> issuers = market.byIssuer();
        page.getChildren().add(vitalsBar(
                limitCell("OUTSTANDING", d(face), market.getBonds().size() + " bonds", Palette.TEXT_HEAD),
                limitCell("OF BUSINESS DEBT", business > 0 ? pc1(face / business) : "—",
                        "bank loans and bonds together, " + d(business), Palette.TEXT_HEAD),
                limitCell("COUPONS", face > 0 ? pct2(market.averageCoupon()) : "—", "weighted by face", Palette.TEXT_HEAD),
                limitCell("ISSUERS", String.valueOf(issuers.size()), "businesses with a bond out", Palette.TEXT_HEAD)));
        if (market.getBonds().isEmpty()) {
            page.getChildren().add(card(caption("EVERY ISSUE", null), line(NO_BONDS)));
        } else {
            page.getChildren().add(card(caption("WHO HOLDS THEM", PRICES_INFO), holdersBar(new double[] {
                    market.faceHeldByHouseholds(), market.faceHeldByBank(), market.faceHeldByCompanies(),
                    market.faceHeldByWorld(), market.faceHeldByCity()}, 16, true)));
            page.getChildren().add(sectionHead("EVERY ISSUER", hint("tap one for its largest bond's book")));
            GridPane cards = equalColumns(4, TILE_GAP);
            for (int i = 0; i < issuers.size(); i++) {
                VBox c = issuerCard(issuers.get(i));
                GridPane.setFillHeight(c, true);
                cards.add(c, i % 4, i / 4);
            }
            page.getChildren().add(cards);
        }
        page.getChildren().add(pair(bondMonthCard(market), orderBooksCard(market)));
        if (!market.getBonds().isEmpty()) {
            page.getChildren().add(fold("bonds:grids", "every bond, and who holds each, as tables", () -> bondGrids(market)));
        }
    }

    /** One issuer as a card: its sector's icon, its face, its bonds and their coupons, its nearest maturity, its holders; a click opens its largest bond's book. */
    VBox issuerCard(BondMarket.Issuer is) {
        Sector sector = ui.game.getSectors().byKey(is.issuer());
        HBox head = new HBox(6, iconSquare(Icons.ofSector(sector), Palette.BUSINESS, 24, 13),
                words(is.issuer(), Palette.SIZE_HEADING, Palette.TEXT_HEAD));
        head.setAlignment(Pos.CENTER_LEFT);
        String coupons = Math.abs(is.highCoupon() - is.lowCoupon()) < 5e-5 ? pct2(is.lowCoupon())
                : pct2(is.lowCoupon()) + "–" + pct2(is.highCoupon());
        VBox c = new VBox(5, head, figure(d(is.face()), 17, Palette.TEXT_HEAD),
                words(is.bonds() + (is.bonds() == 1 ? " bond" : " bonds") + " · " + coupons + " · next due "
                        + CityCalendar.formatShort(is.nearestMaturity()), Palette.SIZE_LABEL, Palette.TEXT_LABEL),
                holdersBar(new double[] {is.households(), is.bank(), is.companies(), is.world(), is.city()}, 6, false));
        c.setMaxWidth(Double.MAX_VALUE);
        c.setMaxHeight(Double.MAX_VALUE);
        String rest = "-fx-padding: 10 12 12 12; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand; -fx-border-color: ";
        c.setStyle(rest + Palette.EDGE + ";");
        c.setOnMouseEntered(e -> c.setStyle(rest + Palette.BUSINESS + ";"));
        c.setOnMouseExited(e -> c.setStyle(rest + Palette.EDGE + ";"));
        c.setOnMouseClicked(e -> {
            bookBondId = is.largestId();
            open("The bond market", "A bond's book", null);
        });
        return c;
    }

    /** THIS MONTH on the bond market: sold, coupons, repaid, written off, and the last issue. */
    VBox bondMonthCard(BondMarket market) {
        VBox card = card(caption("THIS MONTH", null));
        card.getChildren().add(cardLine(String.format("Sold, %d issue%s", market.getIssues(), market.getIssues() == 1 ? "" : "s"),
                d(market.getIssuedFace()), null));
        if (market.getIssuedCosts() > 0) {
            card.getChildren().add(cardLine("...its costs, paid to the bank as underwriter", d(market.getIssuedCosts()),
                    Palette.TEXT_MUTED));
        }
        card.getChildren().add(cardLine("Coupons paid", d(market.getCouponsPaid()), null));
        if (market.getPrincipalRepaid() > 0) card.getChildren().add(cardLine("Repaid at maturity", d(market.getPrincipalRepaid()), null));
        if (market.getWrittenOffThisMonth() > 0) {
            card.getChildren().add(cardLine("Written off in defaults", d(market.getWrittenOffThisMonth()), Palette.BAD,
                    String.format("The households lost %s of it, the bank %s, the companies %s and the world %s. A "
                                    + "defaulted bank loan gets back more of what it is owed than a defaulted bond does.",
                            d(market.getLossHouseholds()), d(market.getLossBank()), d(market.getLossCompanies()),
                            d(market.getWorldWrittenOff()))));
        }
        if (market.getLastIssuer() != null) {
            card.getChildren().add(muted(String.format("The last issue: %s sold %s of %d-year bonds at %.2f%%, against the "
                            + "bank's %.2f%%, in %s.", market.getLastIssuer(), d(market.getLastIssueFace()),
                    CorporateBond.TERM_MONTHS / 12, market.getLastIssueCoupon() * 100, market.getLastIssueLoanRate() * 100,
                    CityCalendar.format(market.getLastIssueMonth()))));
        }
        return card;
    }

    /** THE ORDER BOOKS, last month: offered for sale, how much of it sold, and how many sellers waited. */
    VBox orderBooksCard(BondMarket market) {
        VBox card = card(caption("THE ORDER BOOKS, LAST MONTH", BOOKS_INFO));
        card.getChildren().add(cardLine("Offered for sale", d(market.getLastPostedSell()), null));
        card.getChildren().add(cardLine("...of it sold", market.getLastPostedSell() > 0
                ? d(market.getLastFilled()) + " · " + pc(market.getLastFilled() / market.getLastPostedSell()) : "—", null));
        if (market.getLastPostedSell() > 0) {
            card.getChildren().add(segmentBar(List.of(seg(market.getLastFilled(), Palette.BUSINESS, "sold " + d(market.getLastFilled()))),
                    market.getLastPostedSell(), null, 0, 8));
        }
        card.getChildren().add(line(market.getLastSellsPosted() > 0
                ? String.format("%,d of %,d sellers waited", market.getLastSellsWaited(), market.getLastSellsPosted())
                : "nobody offered to sell"));
        return card;
    }

    /** The old page's two tables, in the fold: every bond with its price and yield, and who holds each. */
    Node bondGrids(BondMarket market) {
        int month = ui.game.getMonth();
        List<CorporateBond> bonds = new ArrayList<>(market.getBonds());
        bonds.sort(java.util.Comparator.comparingInt(CorporateBond::maturityMonth));
        GridPane t = grid(new double[] {130, 96, 64, 84, 70, 70}, rightAfterFirst(6));
        gridHead(t, "", "owed", "coupon", "matures", "price", "yield");
        int line = 1;
        for (CorporateBond b : bonds) {
            double last = market.lastPrice(b);
            t.add(gridCell(b.issuer(), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
            t.add(gridCell(d(b.face()), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 1, line);
            t.add(gridCell(pct2(b.coupon()), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 2, line);
            t.add(gridCell(CityCalendar.formatShort(b.maturityMonth()), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 3, line);
            t.add(gridCell(Double.isNaN(last) ? per100(market.modelPrice(b, month)) + "*" : per100(last),
                    Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 4, line);
            t.add(gridCell(pct2(market.lastYield(b, month)), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 5, line);
            line++;
        }
        GridPane h = grid(new double[] {130, 100, 90, 100, 100}, rightAfterFirst(5));
        gridHead(h, "held by", "households", "the bank", "companies", "the world");
        line = 1;
        for (CorporateBond b : bonds) {
            h.add(gridCell(b.issuer(), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
            h.add(gridCell(d(b.households()), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 1, line);
            h.add(gridCell(d(b.bank()), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 2, line);
            h.add(gridCell(d(b.companiesTotal()), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 3, line);
            h.add(gridCell(d(b.world()), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 4, line);
            line++;
        }
        return column(t, statementNote(PRICES_INFO), subHead("...and who holds each"), h);
    }

    /** The bond whose book is open, by its number; the largest one when it has gone. */
    int bookBondId = -1;

    /** A bond's book's (i): the spec's T75, T77 and T78. */
    static final String DEPTH_INFO = "What rests on the book after the month's step: a bid under every ask, since "
            + "whatever could meet has already traded. Bids at the left, in the money blue; asks at the right, in "
            + "violet; the length is the face at that price, its yield on hover. The orders are withdrawn at next "
            + "month's step and posted again.";

    void bondBookPage(VBox page) {
        Game g = ui.game;
        BondMarket market = g.getBondMarket();
        int month = g.getMonth();
        List<CorporateBond> bonds = new ArrayList<>(market.getBonds());
        if (bonds.isEmpty()) {
            page.getChildren().add(card(caption("A BOND'S BOOK", null), line("No bond outstanding, so no book.")));
            return;
        }
        bonds.sort((a, b) -> Double.compare(b.face(), a.face()));
        CorporateBond open = market.bond(bookBondId);
        if (open == null) open = bonds.get(0);

        // The twelve largest as chips, and the open one among them.
        java.util.Map<String, Integer> byName = new java.util.LinkedHashMap<>();
        for (CorporateBond b : bonds) {
            if (byName.size() >= 12 && b != open) continue;
            byName.put(b.issuer() + " " + b.id(), b.id());
        }
        javafx.scene.layout.FlowPane chips = chipStrip(byName.keySet().toArray(new String[0]), open.issuer() + " " + open.id(),
                Palette.SIZE_CAPTION, name -> {
                    bookBondId = byName.get(name);
                    showFinanceMenu();
                });
        chips.setAlignment(Pos.CENTER_LEFT);
        page.getChildren().add(chips);

        CorporateBond b = open;
        OrderBook book = market.bookOf(b);
        double last = market.lastPrice(b);
        VBox facts = card(caption(b.issuer().toUpperCase() + "'S BOND " + b.id(), String.format(
                        "Its issuer's firms default at %s a year at its leverage, and a holder of its bonds loses %.0f%% of "
                                + "what defaults, where the bank's loans lose %.0f%% - what bonds and loans have given back "
                                + "on average, 1987-2024.", BankScreen.defaultShare(market.defaultRate(b.issuer(), 0, 0)),
                        BusinessDebtManager.BOND_LOSS_GIVEN_DEFAULT * 100, BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT * 100)),
                cardLine("Owed", d(b.face()), null),
                cardLine("Coupon", pct2(b.coupon()) + " a year, paid monthly", null),
                cardLine("Sold", CityCalendar.format(b.issueMonth()), null),
                cardLine("Matures", CityCalendar.format(b.maturityMonth()) + " (" + CityCalendar.until(month, b.maturityMonth()) + ")", null),
                cardLine("Worth, at the curve and its issuer's risk", per100(market.modelPrice(b, month)) + " per 100, "
                        + pct2(market.modelYield(b, month)), null),
                cardLine("Last traded", Double.isNaN(last) ? "not yet"
                        : per100(last) + " per 100, in " + CityCalendar.format(book.lastTradeMonth()), null));
        page.getChildren().add(pair(facts, card(caption("DEPTH", DEPTH_INFO), depthChart(book, b, month))));
        page.getChildren().add(fold("bond:levels", "the bids and asks, as tables", () -> levelGrids(book, b, month)));
    }

    /** The book's depth: a row a price level, the asks over the bids, best nearest the middle; bids drawn leftward, asks rightward, face as length. */
    Node depthChart(OrderBook book, CorporateBond b, int month) {
        List<OrderBook.Level> bids = book.levels(OrderBook.Side.BUY), asks = book.levels(OrderBook.Side.SELL);
        int shown = 10;
        double most = 0;
        for (int i = 0; i < Math.min(shown, bids.size()); i++) most = Math.max(most, bids.get(i).quantity());
        for (int i = 0; i < Math.min(shown, asks.size()); i++) most = Math.max(most, asks.get(i).quantity());
        if (bids.isEmpty() && asks.isEmpty()) return line("Nobody is bidding and nobody is selling.");
        VBox rows = new VBox(2);
        List<OrderBook.Level> askRows = new ArrayList<>(asks.subList(0, Math.min(shown, asks.size())));
        java.util.Collections.reverse(askRows);
        for (OrderBook.Level l : askRows) rows.getChildren().add(depthRow(l, false, most, b, month));
        if (asks.isEmpty()) rows.getChildren().add(muted("nobody is selling"));
        if (bids.isEmpty()) rows.getChildren().add(muted("nobody is bidding"));
        for (int i = 0; i < Math.min(shown, bids.size()); i++) rows.getChildren().add(depthRow(bids.get(i), true, most, b, month));
        rows.getChildren().add(keyRow(keySwatch(Palette.MONEY, "bids"), keySwatch(Palette.BUSINESS_LIGHT, "asks")));
        return rows;
    }

    /** One price level of the depth chart. */
    Node depthRow(OrderBook.Level l, boolean bid, double most, CorporateBond b, int month) {
        double side = 200;
        double w = most > 0 ? Math.max(2, l.quantity() / most * side) : 2;
        Region bar = new Region();
        bar.setMinSize(w, 12);
        bar.setPrefSize(w, 12);
        bar.setMaxSize(w, 12);
        bar.setStyle("-fx-background-color: " + (bid ? Palette.MONEY : Palette.BUSINESS_LIGHT) + "; -fx-background-radius: 2;");
        HBox left = new HBox(), right = new HBox();
        left.setMinWidth(side);
        left.setPrefWidth(side);
        right.setMinWidth(side);
        right.setPrefWidth(side);
        left.setAlignment(Pos.CENTER_RIGHT);
        right.setAlignment(Pos.CENTER_LEFT);
        (bid ? left : right).getChildren().add(bar);
        Label price = figure(per100(l.price()), Palette.SIZE_LABEL, Palette.TEXT_HEAD);
        price.setMinWidth(70);
        price.setAlignment(Pos.CENTER);
        HBox row = new HBox(6, left, price, right);
        row.setAlignment(Pos.CENTER_LEFT);
        Tooltip tip = new Tooltip(String.format("%s at %s per 100 · yields %s · %s · %d order%s", bid ? "bid" : "asked",
                per100(l.price()), pct2(CorporateBond.yieldAtPrice(b.coupon(), b.remainingMonths(month), l.price())),
                d(l.quantity()), l.orders(), l.orders() == 1 ? "" : "s"));
        tip.setShowDelay(Duration.millis(150));
        Tooltip.install(row, tip);
        return row;
    }

    /** The old page's bid and ask tables, in the fold. */
    Node levelGrids(OrderBook book, CorporateBond b, int month) {
        VBox c = column();
        for (OrderBook.Side side : OrderBook.Side.values()) {
            boolean bids = side == OrderBook.Side.BUY;
            List<OrderBook.Level> levels = book.levels(side);
            c.getChildren().add(subHead(bids ? "Bids, best first" : "Asks, best first"));
            if (levels.isEmpty()) {
                c.getChildren().add(sentence(bids ? "Nobody is bidding." : "Nobody is selling.", Palette.TEXT_MUTED));
                continue;
            }
            GridPane t = grid(new double[] {110, 90, 120, 80}, rightAfterFirst(4));
            gridHead(t, "price per 100", "its yield", "face", "orders");
            int line = 1;
            for (OrderBook.Level level : levels) {
                if (line > 12) break;
                t.add(gridCell(per100(level.price()), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
                t.add(gridCell(pct2(CorporateBond.yieldAtPrice(b.coupon(), b.remainingMonths(month), level.price())),
                        Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 1, line);
                t.add(gridCell(d(level.quantity()), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 2, line);
                t.add(gridCell(String.valueOf(level.orders()), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 3, line);
                line++;
            }
            c.getChildren().add(t);
        }
        return c;
    }

    /* =====================================================================
       THE CITY'S FUND (0.7.14; redrawn 0.7.32; its own class since 0.7.39)

       Jerus: "Rule plus your hand". Its pages - Portfolio, Search, a page a
       security with the order ticket, Activity, and Rules & cash - are
       FundScreen's since 0.7.39 (the project's spec-fund-0739.md, 4): the
       tab dispatches the area to it, and its head on a security's page.
       What stays here is the amount PAY IN, DRAW OUT asks for, so that it
       outlives the clock's redraws as it did, and the hub's area card.
       ===================================================================== */

    /** What the player's hand is asking to pay into the fund or draw out of it, on Rules & cash (FundScreen.moveCard()). */
    double fundAsk = 0;

    /* =====================================================================
       THE DEBT RESULT

       Issuing the paper a screen asked for, and the page that says what was
       raised. The borrow page comes through here; the two offers the build
       screen makes when the treasury is short do not - each books its own
       quote on Game and places the run (BuildScreen's runOffers(), since
       0.7.40). Its own section since 2026-09-18, on the way out of
       the stat card and into the finances screen.

       A RECEIPT SINCE 0.7.32 (the spec's D13, B11): "Finances › Issued" reads
       the quote it booked - the one the card showed, which Game's booking
       struck again on the same city and returned the summary of - rather
       than printing that summary in a fixed-width font with every digit and
       a dollar bond's face as local money. The summary is still Game's
       return value and the log's, unchanged.
       ===================================================================== */

    /** Books the sale the page quoted, at the highest yield it was quoted at (CD2): the handler struck on the same city at the same cap. */
    String executeDebtLogic(String type, double amount, int duration, double rounding, Auction.Cap cap) {
        return executeDebtLogic(type, amount, duration, rounding, cap, false);
    }

    /** ...a bond sold callable if the quote was (CD4): the handler struck on the same city, at the same cap, with the same option. */
    String executeDebtLogic(String type, double amount, int duration, double rounding, Auction.Cap cap, boolean callable) {
        return switch (type) {
            case "Note" -> ui.game.handleTBillLogic(amount, duration, rounding, cap);
            case "Serial" -> ui.game.handleMediumBondLogic(amount, duration, rounding, cap);
            case "Term" -> ui.game.handleLongBondLogic(amount, duration, rounding, cap, callable);
            // CD5: the floating-rate note and the inflation-linked bond, at the page's cap, never callable.
            case "Float" -> ui.game.handleFloatingNoteLogic(amount, duration, rounding, cap);
            case "Linked" -> ui.game.handleLinkedBondLogic(amount, duration, rounding, cap);
            default -> "Unknown instrument.";
        };
    }

    /** Shows the terms the player just agreed to: the quote booked as a receipt, and the ladder with it on the books; or what the booking said when nothing was booked. */
    void showDebtResultMenu(DebtQuote quote, boolean foreign, String summary) {
        ui.clearMenu("showDebtResultMenu", () -> showDebtResultMenu(quote, foreign, summary));
        Game g = ui.game;
        DebtManager ledger = g.getDebtManager();
        VBox page = widePage();
        boolean booked = quote != null && summary != null && summary.endsWith(quote.summary());
        String back = foreign ? "Abroad" : "At home";
        if (!booked) {
            page.getChildren().add(card(caption("NOTHING WAS ISSUED", null),
                    line(summary == null ? "Nothing issued." : summary.trim())));
        } else {
            java.util.function.DoubleFunction<String> w = foreign ? Money::usd : this::d;
            int month = g.getMonth();
            Auction.Result sale = quote.auction();
            // A reopened line falls due on the line's date: the auction's months are the line's left.
            int months = sale != null && !sale.isEmpty() ? sale.ask().months()
                    : "Note".equals(quote.instrument()) ? quote.duration() : quote.duration() * 12;
            String first = summary.substring(0, Math.max(0, summary.indexOf('\n'))).trim();
            VBox receipt = card(caption(first.isEmpty() ? "ISSUED" : first.toUpperCase(), null),
                    sale != null && !sale.isEmpty() ? cardLine("Sold at auction", String.format("stopped at %s, %s over fair, "
                            + "covered %.2f×", pct3(sale.stop()), bp(sale.tail()), sale.cover()), null) : null,
                    sale != null && sale.shortSale() ? cardLine("Asked for, and not sold", w.apply(sale.shortBy()) + " of "
                            + w.apply(sale.ask().face()), Palette.BAD_SOFT) : null,
                    cardLine("Face - what the city owes", w.apply(quote.faceValue()), null),
                    cardLine("Cash received", w.apply(quote.cashReceived()), null),
                    cardLine("Issued at", String.format("%.2f of par", quote.pricePerPar()), null),
                    cardLine("Coupon", quote.monthlyInterest() > 0
                            ? w.apply(quote.monthlyInterest()) + " a month"
                                    + ("Term".equals(quote.instrument()) ? ", " + couponWords(quote.couponRate()) + " on the face"
                                            : "Serial".equals(quote.instrument()) ? ", " + pct3(quote.couponRate()) + " on what is left"
                                            // CD5: a floater's coupon field is its margin, a linker's its real coupon (never couponRate(), which backs out the whole coupon at today's dial and index).
                                            : "Float".equals(quote.instrument()) ? " at today's dial, " + marginWords(quote.coupon()) + ", reset monthly"
                                            : "Linked".equals(quote.instrument()) ? " at today's index, " + couponWords(quote.coupon()) + " real, on an indexed principal" : "")
                            : "none - the discount is the lender's return", null),
                    "Serial".equals(quote.instrument())
                            ? cardLine("Principal", "repaid in " + Math.max(1, quote.duration()) + " annual slices", null)
                            : "Linked".equals(quote.instrument())
                            ? cardLine("Falls due, the indexed principal", CityCalendar.format(month + months) + " · never under the face", null)
                            : cardLine("Falls due, the whole face", CityCalendar.format(month + months), null),
                    cardLine(totalWords(instrument(quote.instrument())), w.apply(quote.totalCost()), null),
                    foreign ? line("In dollars, at " + perUsd(ledger.getExchangeRate()) + ".") : null,
                    // A dollar quote's rates are the world's for this paper, not the city's own.
                    line(foreign ? String.format("Priced at %s on the world's curve (%s before this issue); the city's "
                                    + "own rate is %s.", pct2(quote.marketRate()), pct2(quote.rateBefore()),
                                    pct2(g.getInterestRate()))
                            : String.format("The city's rate is now %s (it was %s).", pct2(g.getInterestRate()),
                                    pct2(quote.rateBefore()))));
            page.getChildren().add(pair(receipt, null));
            DebtManager.Ladder ladder = ledger.ladder(month);
            page.getChildren().add(card(caption("THE LADDER, WITH IT ON THE BOOKS", LADDER_INFO),
                    ladderChart(ladder, null, null, 200), ladderKey(ladder, null, null)));
        }
        VBox frame = new VBox(Palette.GAP, pageHead("Finances", Palette.MONEY, () -> open(null, null, null),
                "Issued", null, backDoor("Borrow", () -> open("Borrow", back, null))));
        frameOver(frame, page);
    }
}
