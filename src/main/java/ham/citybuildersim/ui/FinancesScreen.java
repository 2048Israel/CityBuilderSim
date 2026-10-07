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
                        One page: a card a piece, Buy back on each.
         BORROW         what would this paper cost, and what would it do to
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
    /** The book is one page since 0.7.32: Buy back is on each piece's card. */
    static final String[] BOOK_PAGES   = {"Every piece"};
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
     * The four numbers were scattered across six button handlers - three
     * domestic and three foreign, each passing the same min, max and rounding
     * as literals. They are one table now, so a change to what a term loan is
     * happens once.
     *
     * AND THE STEP BETWEEN TERMS (0.7.1): Jerus, "i think we should only be
     * able to issue 10y 20y 30y 40y and 50y ... serial and tbills are fine
     * tho." A term loan's terms step by ten (LongTermBond.MATURITIES) -
     * columns at home since 0.7.32, chips abroad; the note's and the
     * serial's by one, as they always did.
     *
     * ...AND ITS TILE (0.7.32): the range it is issued over and one line, the
     * blurb behind the tile's (i).
     */
    record Instrument(String key, String name, String short_,
                              int min, int max, int step, double rounding,
                              String unit, String blurb, String colour, String range, String line) { }

    /** The three kinds of paper, short to long, in the ladder's order: the note, the serial bond and the term loan. */
    static final Instrument[] INSTRUMENTS = {
        new Instrument("Note", "Notes", "NOTE", 3, 12, 1, 1000, "months",
                "No coupon at all. The lender pays less than the face and collects the "
                + "whole face at the end, so it costs nothing monthly and everything at "
                + "once.", Palette.LADDER[0], "3–12 months", "No coupon: the discount is the price."),
        new Instrument("Serial", "Serial bonds", "SERIAL", 1, 10, 1, 10000, "years",
                "A coupon every month on what is still out, and a slice of principal "
                + "every year. It pays itself off: both the debt and the coupon shrink "
                + "without you doing anything.", Palette.LADDER[1], "1–10 years", "Pays itself off, a slice a year."),
        new Instrument("Term", "Term loans", "TERM", 10, 50, 10, 100000, "years",
                "A coupon every month on the whole face, and the entire face at the end. "
                + "The smallest monthly payment there is, and the only one with a cliff "
                + "behind it. Issued at 10, 20, 30, 40 or 50 years.", Palette.LADDER[2], "10–50 years",
                "The smallest monthly cost; the whole face at the end."),
    };

    static Instrument instrument(String key) {
        for (Instrument i : INSTRUMENTS) if (i.key().equals(key)) return i;
        return INSTRUMENTS[2];
    }

    /** A kind's colour on the ladder, by Debt.getType(): DebtManager.LADDER_KINDS in Palette.LADDER's three. */
    static String instrumentColour(String type) {
        return Palette.LADDER[DebtManager.ladderKind(type)];
    }

    /** The ladder's three kinds, as the key and a tooltip say them. */
    static final String[] KIND_NAMES = {"Notes", "Serial bonds", "Term loans"};

    /* what the borrow page is currently asking for */
    String borrowType = "Term";
    int borrowTerm = 10;
    double borrowAsk = 0;
    boolean borrowHold = false;
    /** What is typed in the ask's box and not yet set (0.7.40), kept through a month's redraw; null while the box is empty. */
    private String askTyped;
    /** ...the ask it was typed over: a step, a preset or an issue that moves the ask drops it. */
    private double askTypedOver;
    /** What the last entry that could not be read says, under the box until the ask is next set; null for none. */
    private String askRefused;
    /** The ask's box, and whether it had the focus when the page was last redrawn - a month landing rebuilds it under the typing (FundScreen's search box). */
    private javafx.scene.control.TextField askField;
    private boolean askTyping;

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
        if (!onPage && pages.length > 0) financePage = pages[0];

        List<CityNeeds.Need> all = CityNeeds.measure(ui.game, SummaryScreen.WORDS);
        targets.clear();

        VBox page = widePage();
        if (financeArea == null) {
            hubPage(page, all);
        } else {
            switch (financeArea) {
                case "The book" -> bookPage(page);
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
        "Every piece of paper, and what each would cost to retire - Buy back is on each piece's card.",
        "Notes, serial bonds and term loans - here and abroad - with the quote struck on every click and the "
                + "ladder it would build.",
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
        String couponNote = "a month · " + (revenue > 0
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
                        "Debt service: what the paper costs against what the city takes in", null),
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

    /** A column's tooltip: "2210 · D$579.0M: term loans D$579.0M, D$563.3M of it in dollars · 30% of a year's take" (the spec's section 3). */
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

    /** One piece on NEXT DUE: its amount in its urgency, its kind's tag and its date; a click opens it on The book. */
    HBox nextDueRow(Debt debt, int month) {
        int due = debt.getMaturityMonth();
        Label amount = figure(d(debt.getOustandingPrincipal()), 13, urgency(due - month));
        Label kind = tag((debt.isForeign() ? "US$ " : "") + debt.getType(), instrumentColour(debt.getType()));
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
        row.setOnMouseClicked(e -> open("The book", null, debt));
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
     * the issue there.
     */
    /** The rollover's three settings as chips, in Rollover.Mode's order. */
    static final String[] ROLLOVER_CHIPS = { "By hand", "Same structure", Rollover.BILL_MONTHS + "-month notes" };

    /** Each setting's one line (the spec's T35). */
    static final String[] ROLLOVER_LINES = {
        "Nothing automatic: what falls due is paid from the cash, and what it can't cover the central bank advances.",
        "A month ahead, what falls due is sold again as the same paper, once last year's surplus has paid its part.",
        "A month ahead, what falls due is sold again as " + Rollover.BILL_MONTHS + "-month notes at home, once last "
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
                + "rest a month ahead as " + Rollover.BILL_MONTHS + "-month notes at home.",
    };

    /** The rollover's (i): the three settings, the central bank's own roll (T36) and the new paper's size (T39). */
    static final String ROLLOVER_INFO = "By hand, nothing is automatic: what falls due is paid out of the treasury's "
            + "cash, and what the cash cannot cover the central bank advances. The two automatic settings look at what "
            + "falls due next month, net last year's surplus from it, and issue the rest a month ahead - as the same "
            + "paper (instrument, term and currency, dollars abroad in dollars, at home while the window abroad is "
            + "shut), or as " + Rollover.BILL_MONTHS + "-month notes at home.\n\n"
            + "The central bank replaces what it holds of the paper falling due with the same par of the new paper, "
            + "at the price the market pays, on top of what is sold - so the issue is sized for the rest, and with "
            + "nothing to sell the market its par is issued it alone. What it pays under par comes back as its "
            + "remittance. Last year's surplus pays the market's part first and then the central bank's, so a surplus "
            + "the size of what falls due pays it all. While it holds more than its dial, what it holds past the dial "
            + "is repaid it instead, and raised with the rest. By hand, it rolls what it holds at issue, beside what "
            + "you sell; sell none and its holding is repaid it out of the treasury's cash.\n\n"
            + "The new paper is sized so its cash covers what falls due. It sells under its face - by its discount, a "
            + "term loan's redemption premium, its costs - so its face is more than the part of the maturity it "
            + "refinances: rolling for cash borrows the old paper's interest into the new principal, at every roll. "
            + "The proceeds wait a month for the maturity - what pre-funding a redemption costs. A treasury short of "
            + "cash for its spending is still the central bank's to advance.";

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
            case TWELVE_MONTH_BILL -> "What falls due rolls into " + Rollover.BILL_MONTHS + "-month notes";
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
                        coupon >= .5 ? d(coupon) + " coupon a month"
                                : pieces > 0 ? "no coupon: notes pay at maturity" : "nothing outstanding",
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
            case 1 -> segmentBar(List.of(Segment.of(ledger.getPrincipalOf("NOTE"), Palette.LADDER[0]),
                    Segment.of(ledger.getPrincipalOf("SERIAL"), Palette.LADDER[1]),
                    Segment.of(ledger.getPrincipalOf("TERM"), Palette.LADDER[2])), 0, null, 0, 6);
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
            + "has advanced.";

    /** THE BALANCE: the cash against what is owed, two bars on one scale, and the net position (the spec's section 3 B). */
    VBox balanceCard() {
        Game g = ui.game;
        DebtManager ledger = g.getDebtManager();
        double cash = g.getCash();
        double held = Math.max(0, cash);
        double principal = ledger.getAllPrincipal();
        double overdraft = ledger.getOverdraft();
        double advanced = g.getCentralBank().getAdvancesToTreasury();
        double scale = Math.max(held, principal + overdraft + advanced);
        VBox card = card(caption("THE BALANCE", BALANCE_INFO));
        card.getChildren().add(cardLine("In the treasury", d(cash), cash < 0 ? Palette.BAD : null));
        card.getChildren().add(segmentBar(List.of(seg(held, Palette.MONEY, "in the treasury " + dFull(cash))),
                scale > 0 ? scale : 1, null, 0, 16));
        card.getChildren().add(cardLine("Owed", d(principal), null));
        card.getChildren().add(segmentBar(List.of(seg(principal, Palette.MONEY_DARK, "owed on paper " + dFull(principal)),
                        seg(overdraft, Palette.BAD, "overdrawn " + dFull(overdraft)),
                        seg(advanced, Palette.BAD, "advanced by the central bank " + dFull(advanced))),
                scale > 0 ? scale : 1, null, 0, 16));
        if (overdraft > 0 || advanced > 0) {
            card.getChildren().add(words((overdraft > 0 ? "+ " + d(overdraft) + " overdrawn  " : "")
                    + (advanced > 0 ? "+ " + d(advanced) + " advanced by the central bank" : ""),
                    Palette.SIZE_LABEL + 1, Palette.BAD));
        }
        HBox net = new HBox(Palette.GAP, figure(signedD(g.getNetPosition()), 22, Palette.TEXT_HEAD),
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
        VBox left = new VBox(4, words(year >= month ? "THE NEXT 12 MONTHS" : "A MONTH'S COUPON", Palette.SIZE_LABEL + 1,
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
                List.of(new Tick(Math.min(month, top), Palette.TEXT_HEAD, 3, null, "a month's coupon " + pc1(month)),
                        new Tick(Math.min(year, top), Palette.ACCENT, 3, null, "the next 12 months " + pc1(year))), 0, 14);
        javafx.scene.layout.FlowPane marks = keyRow(keySwatch(Palette.TEXT_HEAD, "a month's coupon " + pc1(month)
                                + (month > top ? " (past the end)" : "")),
                keySwatch(Palette.ACCENT, "the next 12 months " + pc1(year) + (year > top ? " (past the end)" : "")),
                words("comfortable to " + pc(CityNeeds.SERVICE_FELT) + " · felt to " + pc(CityNeeds.SERVICE_CONSTRAINED)
                        + " · constrained past it, to " + pc(top) + " drawn", Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        VBox right = new VBox(8, caption("WHAT THE PAPER ASKS, AGAINST THE TAKE", SERVICE_INFO), gauge, marks,
                line(String.format("%s due in the next twelve months, coupons and principal, against twelve months of "
                        + "this month's take, %s a month; the coupon alone is %s a month.", d(next12), d(revenue), d(coupon))));
        return heroCard(left, 220, right);
    }

    /** LAST MONTH's (i): the spec's T14. */
    static final String LAST_MONTH_INFO = "Only the coupon is an expense; the principal is a balance-sheet movement. "
            + "Both leave the treasury, which is why both are on the ladder, and separated on the Government spending "
            + "page. What fell due was netted from last year's surplus, raised again as new paper by the rollover, or "
            + "paid from the cash.";

    /** LAST MONTH: the coupon, and the principal that fell due at the last press split as the rollover booked it. */
    VBox lastMonthCard() {
        Game g = ui.game;
        DebtManager ledger = g.getDebtManager();
        Rollover r = g.getRollover();
        VBox card = card(caption("LAST MONTH", LAST_MONTH_INFO));
        card.getChildren().add(cardLine("Coupon on everything outstanding, a month", d(ledger.getMonthlyCoupon()), null));
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
            + "paper with money it makes, then the households' once the bank has none left, and sells to the bank - "
            + "the holdings dial on the Policy tab. It replaces what it holds of a maturing piece with the same par "
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

    /** WHO HOLDS IT: the households, the bank, the central bank and the world, one bar, each with its amount and share. */
    VBox holdersCard(double all) {
        DebtManager ledger = ui.game.getDebtManager();
        double[] held = {ledger.householdPrincipal(), ledger.bankPrincipal(), ledger.centralBankPrincipal(),
                         ledger.getForeignPrincipal()};
        String[] who = {"households", "the bank", "the central bank", "abroad"};
        String[] colour = {Palette.PEOPLE, Palette.MONEY, Palette.MONEY_DARK, Palette.BUSINESS};
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

    /** THE CURVE's (i): the spec's T24, and the world's curve. */
    static final String CURVE_INFO = "The note is the floor - the dial, or what the bank's money costs it, whichever "
            + "is higher - plus what the city's own debt adds. Longer money carries a term premium on top - half a "
            + "point at ten years, a point and a half at fifty - less whatever the central bank's holdings of term "
            + "paper take off it. The world lends at its own base and the city's premium abroad, on the same shape. "
            + "The dashed line is the dial.";

    /** THE CURVE: the city's rate by maturity at home and, while the window is open, abroad, with the dial dashed and the thirty-year point taken apart. */
    VBox curveCard() {
        DebtManager ledger = ui.game.getDebtManager();
        boolean world = ledger.foreignWindowOpen();
        double[] home = new double[CURVE_MONTHS.length], abroad = world ? new double[CURVE_MONTHS.length] : null;
        for (int i = 0; i < CURVE_MONTHS.length; i++) {
            home[i] = ledger.curveRate(CURVE_MONTHS[i]);
            if (world) abroad[i] = ledger.foreignCurveRate(CURVE_MONTHS[i]);
        }
        double premium = DebtManager.termPremium(360), comp = ledger.compression(360);
        VBox card = card(caption("THE CURVE", CURVE_INFO),
                new CurveChart(home, abroad, ledger.getPolicyRate(), 7,
                        String.format("+%.2f term premium − %.2f the central bank", premium * 100, comp * 100)),
                keyRow(keySwatch(Palette.MONEY, "at home"), world ? keySwatch(Palette.BUSINESS, "abroad, in dollars") : null,
                        keySwatch(Palette.TEXT_MUTED, "the dial, dashed")),
                line(String.format("Thirty-year money costs %s: the short end, %s the term premium and %s for what the "
                                + "central bank holds.", pct2(ledger.curveRate(360)), pts(premium),
                        comp > 0 ? pts(-comp) : "nothing off")));
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
                double ax = Math.min(x.applyAsDouble(marked) - 40, W - 260);
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
       THE BOOK (0.7.32)

       What is each piece, and what would it cost to retire? One page since
       0.7.32 (Every piece and Buy back until then): a card a piece by when
       it falls due - its life from issue to its date, its coupon, who holds
       it, what it is worth today and Buy back on it, the old BUY BACK banner's own
       intent ("on the bond it is about"). The old table is behind "details".

       A PIECE IS VALUED AT WHAT A BUYBACK PAYS: DebtManager.marketValue(),
       the city's curve for its own paper and the world's for a dollar
       piece. The Buy back page valued a dollar piece at the city's short
       rate while Game.repurchaseDebt() charged the world's curve, and
       painted a price under par green under a sentence saying it was "not
       the bargain it looks like" (the spec's B6): the price is plain now.
       ===================================================================== */

    /** The book's (i): the spec's T32 and T34. */
    String bookInfo() {
        DebtManager ledger = ui.game.getDebtManager();
        return String.format("Every piece is priced at what it is worth today: the present value of everything it still "
                        + "owes, discounted at the curve's rate for the months it has left - %s for a note, %s for "
                        + "thirty-year money - and a dollar piece at the world's curve. Above par means getting out costs "
                        + "a premium. Below par means your own paper has become cheap to retire - which happens when your "
                        + "credit has got worse, so it is not the bargain it looks like.\n\n%s of face for %s of cash. "
                        + "Retiring paper lowers the city's rate, which raises what the rest of it is worth - so these "
                        + "prices are quoted off the market as it stands, not as it will stand after the first trade.",
                pct2(ledger.getRate()), pct2(ledger.curveRate(360)), d(ledger.getAllPrincipal()),
                d(ledger.getTotalMarketValue()));
    }

    /** A note's (i): the spec's T28. */
    static final String NOTE_INFO = "No coupon at all - the lender's return was the discount, taken out of the proceeds "
            + "at issue. It costs nothing monthly and the whole face falls due at once.";

    /** A price against face, in words (the spec's T33, D16). */
    static final String PREMIUM_INFO = "Buying a piece back pays its holders what it is worth today. Under its face, "
            + "the city clears more debt than it pays - because its credit has got worse since the piece was sold, or "
            + "rates have risen. Over its face, a premium: this piece pays more than the city could borrow at today.";

    void bookPage(VBox page) {
        Game g = ui.game;
        DebtManager ledger = g.getDebtManager();
        List<Debt> paper = new ArrayList<>(ledger.getDebt());
        paper.sort(java.util.Comparator.comparingInt(Debt::getMaturityMonth));
        if (paper.isEmpty()) {
            page.getChildren().add(card(caption("EVERY PIECE", null), line("Nothing outstanding."),
                    door("Borrow", Palette.ACCENT, () -> open("Borrow", "At home", null))));
            return;
        }
        String head = String.format("%d %s · %s owed · %s coupon a month · cleared today for %s · %s in the treasury",
                paper.size(), paper.size() == 1 ? "piece" : "pieces", d(ledger.getAllPrincipal()), d(ledger.getMonthlyCoupon()),
                d(ledger.getTotalMarketValue()), d(g.getCash()));
        page.getChildren().add(infoLine(head, bookInfo(), true, Palette.SIZE_BODY + 1, Palette.TEXT_LABEL, 1200));
        GridPane cards = equalColumns(2, TILE_GAP);
        for (int i = 0; i < paper.size(); i++) {
            Debt debt = paper.get(i);
            VBox c = pieceCard(debt);
            GridPane.setFillHeight(c, true);
            targets.put(debt, c);
            targets.putIfAbsent(CityCalendar.yearOf(debt.getMaturityMonth()), c);
            cards.add(c, i % 2, i / 2);
        }
        page.getChildren().add(cards);
        page.getChildren().add(fold("book:grid", "every piece, as a table", () -> bookGrid(paper)));
    }

    /** A piece's name in words: "6-month note", "5-year serial bond", "20-year term loan", "... in dollars". */
    static String pieceName(Debt debt) {
        int months = debt.getDuration();
        String what = switch (debt.getType()) {
            case "NOTE"   -> months + "-month note";
            case "SERIAL" -> Math.max(1, Math.round(months / 12f)) + "-year serial bond";
            default       -> Math.max(1, Math.round(months / 12f)) + "-year term loan";
        };
        return what + (debt.isForeign() ? " in dollars" : "");
    }

    /** One piece as a card (the spec's section 3 C). */
    VBox pieceCard(Debt debt) {
        Game g = ui.game;
        DebtManager ledger = g.getDebtManager();
        int month = g.getMonth();
        int due = debt.getMaturityMonth(), started = debt.getMonthStarted();
        String colour = instrumentColour(debt.getType());
        String when = urgency(due - month);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        Label name = words(pieceName(debt), Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD);
        name.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        HBox head = new HBox(Palette.GAP, tag((debt.isForeign() ? "US$ " : "") + debt.getType(), colour), name, gap,
                figure(CityCalendar.formatShort(due) + " · " + CityCalendar.until(month, due), Palette.SIZE_LABEL + 1, when));
        head.setAlignment(Pos.CENTER_LEFT);

        HBox face = new HBox(Palette.GAP, figure(d(debt.getOustandingPrincipal()), 17, Palette.TEXT_HEAD));
        if (debt.isForeign()) face.getChildren().add(words(usd(debt.principalInCurrency()) + " at "
                + perUsd(debt.getExchangeRate()), Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
        face.setAlignment(Pos.BASELINE_LEFT);

        int life = Math.max(1, due - started);
        Region lifeBar = segmentBar(List.of(seg(Math.max(0, month - started), Palette.MONEY_DARK,
                                "issued " + CityCalendar.format(started)),
                        ghost(Math.max(0, due - month), colour, null, "falls due " + CityCalendar.format(due))),
                life, null, 0, 8);
        Region g2 = new Region();
        HBox.setHgrow(g2, Priority.ALWAYS);
        HBox ends = new HBox(muted("issued " + CityCalendar.formatShort(started)), g2,
                words("falls due " + CityCalendar.formatShort(due), Palette.SIZE_LABEL, when));

        double coupon = debt.getMonthlyInterestExpense();
        Node couponLine = coupon >= .5 ? line("coupon " + d(coupon) + " a month")
                : noteLine("no coupon - the discount was the price", NOTE_INFO, 560);

        Node holders;
        if (debt.isForeign()) {
            holders = muted("held abroad");
        } else {
            double p = debt.getOustandingPrincipal();
            holders = segmentBar(List.of(
                    seg(debt.householdPrincipal(), Palette.PEOPLE, "households " + d(debt.householdPrincipal())),
                    seg(debt.bankPrincipal(), Palette.MONEY, "the bank " + d(debt.bankPrincipal())),
                    seg(debt.centralBankPrincipal(), Palette.MONEY_DARK, "the central bank " + d(debt.centralBankPrincipal()))),
                    p > 0 ? p : 1, null, 0, 6);
        }

        double rate = ledger.valuationRate(debt);
        double worth = ledger.marketValue(debt);
        double under = ledger.underFace(debt);
        String price = String.format("worth today %s · %.1f of par · yields %s", d(worth),
                debt.getPriceAsPercentOfPar(rate), pct2(debt.getCurrentYield(rate)));
        String against = Math.abs(under) < .5 ? "at its face" : under > 0 ? d(under) + " under face" : "a premium of " + d(-under);
        boolean affordable = worth > 0 && worth <= g.getCash();
        Button buy = new Button(affordable ? "Buy back" : "Too dear");
        buy.setMinWidth(Region.USE_PREF_SIZE);
        buy.setDisable(!affordable);
        if (affordable) buy.setStyle("-fx-background-color: " + Palette.CONFIRM + "; -fx-text-fill: white;");
        buy.setOnAction(e -> {
            ui.game.repurchaseDebt(debt);
            // A freshly built page: retiring paper moves the rate, so every other card's price is now stale.
            open("The book", null, null);
        });
        Region g3 = new Region();
        HBox.setHgrow(g3, Priority.ALWAYS);
        VBox says = new VBox(2, words(price, Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL),
                infoLine("clears " + d(debt.getOustandingPrincipal()) + " for " + d(worth) + " · " + against,
                        PREMIUM_INFO, true, Palette.SIZE_LABEL, Palette.TEXT_MUTED, 420));
        HBox foot = new HBox(Palette.GAP, says, g3, buy);
        foot.setAlignment(Pos.CENTER_LEFT);
        return card(head, face, lifeBar, ends, couponLine, holders, foot);
    }

    /** The book as the old page's table, in the fold: kind, owed, coupon a month, when it matures, how far off. */
    Node bookGrid(List<Debt> paper) {
        GridPane t = grid(new double[] {104, 116, 110, 100, 122}, rightAfterFirst(5));
        gridHead(t, "", "owed", "coupon /mo", "matures", "how far off");
        int month = ui.game.getMonth();
        int line = 1;
        for (Debt debt : paper) {
            Label kind = new Label((debt.isForeign() ? Currency.FOREIGN_CODE + " " : "") + debt.getType());
            kind.setStyle(Palette.words(Palette.SIZE_CAPTION, instrumentColour(debt.getType())));
            t.add(kind, 0, line);
            t.add(gridCell(d(debt.getOustandingPrincipal()), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 1, line);
            t.add(gridCell(debt.getMonthlyInterestExpense() > 0 ? d(debt.getMonthlyInterestExpense()) : "none",
                    debt.getMonthlyInterestExpense() > 0 ? Palette.TEXT_BODY : Palette.TEXT_SPENT,
                    Palette.SIZE_CAPTION, true), 2, line);
            t.add(gridCell(CityCalendar.formatShort(debt.getMaturityMonth()), Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 3, line);
            t.add(gridCell(CityCalendar.until(month, debt.getMaturityMonth()),
                    urgency(debt.getMaturityMonth() - month), Palette.SIZE_CAPTION, true), 4, line);
            line++;
        }
        DebtManager ledger = ui.game.getDebtManager();
        return column(t, statementTotal("On the book altogether", dFull(ledger.getAllPrincipal()), Palette.TEXT_HEAD),
                statementLine("Coupon a month", dFull(ledger.getMonthlyCoupon())));
    }

    /* =====================================================================
       BORROW (0.7.32)

       What would this paper cost, and what would it do to the ladder?
       THE ASK beside THE QUOTE: the three instruments as tiles, the terms
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
       ===================================================================== */

    /** The terms' (i): the spec's T55. */
    static final String TERMS_INFO = "Each column is the rate this paper would cost at that term today - for the "
            + "amount asked, once there is one, priced with it on the books. The note is the floor - the dial, or "
            + "what the bank's money costs it, whichever is higher - plus what the city's own debt adds; longer money "
            + "carries a term premium on top, less whatever the central bank's holdings of term paper take off it. "
            + "Abroad, the world lends on its own curve.";

    /** The ask's (i): the spec's T56. */
    static final String LOTS_INFO = "Issues round to a lot, and the market will not arrange anything under the "
            + "smallest issue for a city this size - the legal and rating work costs the same however little you raise.";

    /** The quote's empty state: the spec's T57. */
    static final String NO_ASK = "Ask for something and the quote appears here, with the ladder it would build. Nothing "
            + "is committed until you press the button.";

    /** Why the proceeds are not the ask: the spec's T58. */
    static final String PROCEEDS_INFO = "Paper is sold in lots and the face is grossed up for the discount, so the "
            + "proceeds land on whichever side of the request the rounding puts them - and the smallest issue worth "
            + "arranging can be more than a lot.";

    /** Who buys it: the spec's T61, its first half. */
    static final String BUYERS_INFO = "The households first, when it pays them more than the bank does: up to %s of "
            + "an issue, out of what they have saved past a cushion, at the settle next month. Then your own bank, for "
            + "the rest. Treasury paper is risk-weighted at %s, so it ties up far less of the bank's room than a "
            + "business loan of the same size. Borrow enough and that changes - for every borrower in the city, not "
            + "only for the treasury.";

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

        Instrument kit = instrument(borrowType);
        borrowTerm = Math.max(kit.min(), Math.min(kit.max(), borrowTerm));
        // ...and onto the instrument's step: a term loan at 10, 20, 30, 40 or 50.
        borrowTerm = kit.min() + Math.round((borrowTerm - kit.min()) / (float) kit.step()) * kit.step();

        DebtQuote quote = borrowAsk > 0
                ? (foreign ? g.quoteForeign(kit.key(), borrowAsk, borrowTerm, kit.rounding())
                           : g.quoteDebt(kit.key(), borrowAsk, borrowTerm, kit.rounding()))
                : null;
        page.getChildren().add(pair(askCard(kit, foreign), quoteCard(kit, quote, foreign)));
        if (quote != null && !quote.isEmpty()) {
            double toLocal = foreign ? ledger.getExchangeRate() : 1;
            DebtManager.Ladder ladder = ledger.ladder(g.getMonth(), quote.schedule(toLocal));
            page.getChildren().add(card(caption("THE LADDER THIS WOULD BUILD", LADDER_INFO,
                            hint("the ghosts are this issue")),
                    ladderChart(ladder, kit.colour(), "this issue", 200), ladderKey(ladder, kit.colour(), "this issue")));
        }
        page.getChildren().add(pair(foreign ? dollarsCard() : whoBuysCard(quote), null));
        if (foreign && ledger.hasForeignDebt()) page.getChildren().add(foreignDoor());
    }

    /** THE ASK: the three instruments, the terms as columns of their rate (chips abroad), and how much. */
    VBox askCard(Instrument kit, boolean foreign) {
        Game g = ui.game;
        VBox card = card(caption(foreign ? "THE ASK, ABROAD, IN " + Currency.FOREIGN_CODE : "THE ASK, AT HOME", null));
        GridPane tiles = equalColumns(INSTRUMENTS.length, TILE_GAP);
        for (int i = 0; i < INSTRUMENTS.length; i++) {
            VBox t = instrumentTile(INSTRUMENTS[i], INSTRUMENTS[i] == kit);
            GridPane.setFillHeight(t, true);
            tiles.add(t, i, 0);
        }
        card.getChildren().add(tiles);

        card.getChildren().add(caption("FOR HOW LONG, IN " + kit.unit().toUpperCase(), TERMS_INFO));
        card.getChildren().add(foreign ? termChips(kit) : termColumns(kit));

        double minimum = g.minimumIssueSize();
        card.getChildren().add(caption(foreign ? "HOW MANY " + Currency.FOREIGN_CODE : "HOW MUCH", ASK_TYPED_INFO));
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
        return card;
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

    /** One instrument as a tile: its swatch-tinted icon, its name and (i), the range it is issued over, its line; picked, its ground and edge lit. */
    VBox instrumentTile(Instrument kit, boolean on) {
        Label name = new Label(kit.name());
        name.setStyle(Palette.strong(Palette.SIZE_BODY + 1, Palette.TEXT_HEAD));
        name.setMinWidth(0);
        name.setWrapText(true);
        HBox top = new HBox(6, iconSquare(Icons.PAPER, kit.colour(), 24, 13), name, infoButton(kit.blurb(), true));
        top.setAlignment(Pos.CENTER_LEFT);
        VBox t = new VBox(4, top, muted(kit.range()), words(kit.line(), Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        t.setMaxWidth(Double.MAX_VALUE);
        t.setMaxHeight(Double.MAX_VALUE);
        String rest = "-fx-padding: 8 10 8 10; -fx-background-radius: 6; -fx-border-radius: 6; -fx-cursor: hand;";
        String ground = on ? " -fx-background-color: " + Palette.PINNED + "; -fx-border-color: " + Palette.ACCENT + ";"
                : " -fx-background-color: " + Palette.PANEL + "; -fx-border-color: " + Palette.EDGE + ";";
        t.setStyle(rest + ground);
        t.setOnMouseClicked(e -> {
            if (borrowType.equals(kit.key())) return;
            borrowType = kit.key();
            borrowTerm = kit.min();
            borrowAsk = 0;
            showFinanceMenu();
        });
        return t;
    }

    /** The terms at home as columns of their rate: the chosen in the money blue, the rest grey; a click picks one. */
    Node termColumns(Instrument kit) {
        Game g = ui.game;
        DebtManager ledger = g.getDebtManager();
        List<Column> cols = new ArrayList<>();
        for (int t = kit.min(); t <= kit.max(); t += kit.step()) {
            final int chosen = t;
            int months = "Note".equals(kit.key()) ? t : t * 12;
            double rate = borrowAsk > 0 ? g.quoteDebt(kit.key(), borrowAsk, t, kit.rounding()).marketRate()
                                        : ledger.curveRate(months);
            double comp = ledger.compression(months);
            cols.add(new Column(t + ("Note".equals(kit.key()) ? " mo" : " y"), null, pct2(rate),
                    List.of(Segment.of(rate, t == borrowTerm ? Palette.MONEY : Palette.TEXT_SPENT)), false, null, null,
                    t + " " + kit.unit() + ": " + pct2(rate) + (borrowAsk > 0 ? " for this amount" : " today")
                            + (comp > 1e-6 ? String.format(", %.2f points off for the central bank", comp * 100) : ""),
                    () -> { borrowTerm = chosen; showFinanceMenu(); }));
        }
        return columns(cols, 0, 0, 120);
    }

    /** The terms abroad as chips, each with the world's rate for it in its tooltip. */
    Node termChips(Instrument kit) {
        DebtManager ledger = ui.game.getDebtManager();
        List<String> names = new ArrayList<>();
        for (int t = kit.min(); t <= kit.max(); t += kit.step()) names.add(String.valueOf(t));
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

    /** What the quoted paper is called on its card: "20-year term loan", "6-month note in dollars". */
    String quoteName(Instrument kit, boolean foreign) {
        String what = switch (kit.key()) {
            case "Note"   -> borrowTerm + "-month note";
            case "Serial" -> borrowTerm + "-year serial bond";
            default       -> borrowTerm + "-year term loan";
        };
        return what + (foreign ? " in dollars" : "");
    }

    /** THE QUOTE: the land office's offer card on exactly the terms the button books, and why the proceeds are not the ask. */
    VBox quoteCard(Instrument kit, DebtQuote quote, boolean foreign) {
        Game g = ui.game;
        DebtManager ledger = g.getDebtManager();
        if (quote == null || quote.isEmpty()) {
            return card(caption("THE QUOTE", null), words(NO_ASK, Palette.SIZE_BODY + 1, Palette.TEXT_MUTED));
        }
        int month = g.getMonth();
        int months = "Note".equals(kit.key()) ? borrowTerm : borrowTerm * 12;
        String ending = switch (kit.key()) {
            case "Serial" -> "a slice of the face falls due every year, the last in " + CityCalendar.formatShort(month + months);
            default       -> "the whole face falls due in " + CityCalendar.formatShort(month + months);
        };
        java.util.function.DoubleFunction<String> w = foreign ? Money::usd : this::d;
        DebtQuote booked = quote;
        int term = borrowTerm;
        double ask = borrowAsk;
        boolean hold = borrowHold;
        VBox offer = offerCard(quoteName(kit, foreign), kit.blurb(), quote, pct2(quote.marketRate()) + " a year",
                rateColour(quote, ledger), ending, "Issue for " + w.apply(quote.cashReceived()), () -> {
                    String summary = foreign ? g.handleForeignLogic(kit.key(), ask, term, kit.rounding(), hold)
                                             : executeDebtLogic(kit.key(), ask, term, kit.rounding());
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

    /** WHO BUYS IT: the households, then the bank, its room used as a bar with this issue as a ghost; the bank's three alarms make the card red. */
    VBox whoBuysCard(DebtQuote quote) {
        Bank bank = ui.game.getBank();
        double room = bank.capacity();
        String info = String.format(BUYERS_INFO, pc(HouseholdBalance.MAX_HOUSEHOLD_PAPER_SHARE), pc(Bank.RISK_CITY));
        VBox card = card(caption("WHO BUYS IT", info));
        if (bank.isInsolvent()) {
            card.setStyle(cardStyle(Palette.BAD));
            card.getChildren().add(words("The bank has failed and cannot buy this. Anything issued now is funded at the "
                    + "central bank's window. Resolve it first - the hub's card, or the Bank tab.", Palette.SIZE_BODY + 1,
                    Palette.BAD_TEXT));
            return card;
        }
        if (room <= 0) {
            card.setStyle(cardStyle(Palette.BAD));
            card.getChildren().add(words("There is no bank to buy this. The households take what share of it pays them "
                    + "better than a deposit would, and the rest is funded at the central bank's window, the money every "
                    + "borrower in a city without a branch is already lent. One branch changes that.",
                    Palette.SIZE_BODY + 1, Palette.BAD_TEXT));
            return card;
        }
        card.getChildren().add(line("The households first, up to " + pc(HouseholdBalance.MAX_HOUSEHOLD_PAPER_SHARE)
                + " of an issue; then your own bank, for the rest."));
        double weighted = bank.getWeightedBook();
        double issue = quote == null || quote.isEmpty() ? 0 : quote.faceValue() * Bank.RISK_CITY;
        card.getChildren().add(segmentBar(List.of(seg(weighted, Palette.MONEY, "the bank's book, risk-weighted " + d(weighted)),
                        ghost(issue, Palette.MONEY, null, "this issue, if the bank took all of it, at treasury paper's weight")),
                Math.max(room, weighted + issue), List.of(new Tick(room, Palette.TEXT_HEAD, 2, "its room " + d(room), null)), 0, 10));
        card.getChildren().add(line(pc(weighted / room) + " of its room used · treasury paper weighs " + pc(Bank.RISK_CITY)));
        if (bank.strain() > Bank.EASY_STRAIN) {
            card.setStyle(cardStyle(Palette.BAD));
            card.getChildren().add(words(String.format("The bank is past comfortable already: %s lent out. Treasury paper "
                            + "ties up less room than a business loan of the same size - but it is still room the shops "
                            + "and the mills were going to use.", pc(weighted / room)), Palette.SIZE_BODY + 1,
                    Palette.BAD_TEXT));
        }
        return card;
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

    String executeDebtLogic(String type, double amount, int duration, double rounding) {
        return switch (type) {
            case "Note" -> ui.game.handleTBillLogic(amount, duration, rounding);
            case "Serial" -> ui.game.handleMediumBondLogic(amount, duration, rounding);
            case "Term" -> ui.game.handleLongBondLogic(amount, duration, rounding);
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
            int months = "Note".equals(quote.instrument()) ? quote.duration() : quote.duration() * 12;
            String first = summary.substring(0, Math.max(0, summary.indexOf('\n'))).trim();
            VBox receipt = card(caption(first.isEmpty() ? "ISSUED" : first.toUpperCase(), null),
                    cardLine("Face - what the city owes", w.apply(quote.faceValue()), null),
                    cardLine("Cash received", w.apply(quote.cashReceived()), null),
                    cardLine("Issued at", String.format("%.2f of par", quote.pricePerPar()), null),
                    cardLine("Coupon", quote.monthlyInterest() > 0
                            ? w.apply(quote.monthlyInterest()) + " a month"
                                    + ("Term".equals(quote.instrument()) ? String.format(", %.2f%% on the face", quote.couponRate() * 100) : "")
                            : "none - the discount is the lender's return", null),
                    "Serial".equals(quote.instrument())
                            ? cardLine("Principal", "repaid in " + Math.max(1, quote.duration()) + " annual slices", null)
                            : cardLine("Falls due, the whole face", CityCalendar.format(month + months), null),
                    cardLine("Cost of the credit, all in", w.apply(quote.totalCost()), null),
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
