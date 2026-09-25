package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import ham.citybuildersim.ui.Pieces.Slice;
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
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Statement.*;
import static ham.citybuildersim.ui.Pieces.*;
import static ham.citybuildersim.ui.Levers.*;

/**
 * The Trade & the world tab: the landing with its vitals, the month as a
 * river, the reserves, the currency, what we trade, and the three quiet gauges.
 *
 * Split out of UserInterface on 2026-09-18: the seven banners from TRADE & THE
 * WORLD to THE THREE QUIET GAUGES exactly as they were, the shell's members
 * reached through ui. The shell still reads which area and page are open
 * (tradeArea, tradePage) for the rail, and the services screen borrows
 * bandMeter() for two of its gauges.
 */
final class TradeScreen {

    /** The window this screen draws into: its game, its root, its clearMenu(). */
    private final UserInterface ui;

    TradeScreen(UserInterface ui) { this.ui = ui; }

    /* =====================================================================
       TRADE & THE WORLD

       WHAT IT WAS: one scrolling column of Courier with the balance of
       payments written out as a ledger, the currency's drivers folded into a
       disclosure of raw percentages, and the one control - buy or sell foreign
       money - behind a button at the very bottom.

       WHAT IT IS: four subjects, each a row on the landing.

         THE MONTH     the balance of payments as a river. Every dollar that
                       crossed the city's edge, drawn as one conserved
                       quantity, because that is what a balance of payments IS
                       and a column of numbers cannot say so.
         THE RESERVES  what the vault holds, what is owed against it, and what
                       is therefore actually the city's. The exchange control
                       lives here rather than behind a button.
         THE CURRENCY  the rate, how far it has wandered from what the same
                       basket costs abroad, and the four forces on it - the
                       trade balance, the real rate, the vault's defence and
                       the pull back to parity (0.7.2; the inflation drift
                       that was a fifth is gone).
         WHAT WE TRADE what the city sells and buys, and the lifetime record.

       AND A RISK PANEL ON THE LANDING, always. Jerus: "both - quiet gauges
       plus loud blocks." The three numbers that decide whether a shock is
       survivable are import cover, what backs the hot money, and how far the
       rate has run - and every one of them was a line of monospace two
       screens down.
       ===================================================================== */

    String tradeArea = null;                 // null is the landing
    static final String TRADE_HOME    = "The picture";
    String tradePage = TRADE_HOME;

    static final String[] TRADE_MONTH_PAGES = {"The picture", "The two accounts"};
    static final String[] TRADE_VAULT_PAGES = {"What is yours", "Cover", "Exchange"};
    static final String[] TRADE_MONEY_PAGES = {"The rate", "What is moving it"};
    static final String[] TRADE_GOODS_PAGES = {"In and out", "Since founding"};

    void showForeignMenu() {
        ui.clearMenu("showForeignMenu", () -> showForeignMenu());

        if (tradeArea != null) {
            drawTradeScreen();
            return;
        }

        ForeignAccounts fx = ui.game.getForeignAccounts();
        CapitalFlows hot = ui.game.getCapitalFlows();

        Label title = new Label("TRADE & THE WORLD");
        title.setStyle(Palette.words(Palette.SIZE_TITLE, Palette.TEXT_HEAD)
                + " -fx-font-weight: bold; -fx-padding: 8 0 2 0;");

        Label lead = new Label("What the city sells, what it buys, and what it owes "
                + "in somebody else's money.");
        lead.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED)
                + " -fx-padding: 0 0 10 0;");

        VBox column = new VBox(0);
        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(Region.USE_PREF_SIZE);

        double balance = fx.balance();
        double yours = ownReserves();

        column.getChildren().add(tradeRow("The month",
                "every dollar that crossed the city's edge, and which way",
                (balance >= 0 ? "+" : "−") + money(Math.abs(balance)),
                balance >= 0 ? "the city took in more than it paid out"
                             : "the city paid out more than it took in",
                balance >= 0 ? Palette.GOOD : Palette.BAD,
                "The month", "The picture"));

        column.getChildren().add(tradeRow("The reserves",
                "what is in the vault, what is owed against it, and what is left",
                money(fx.getReserves()),
                yours >= 0
                        ? money(yours) + " of it is genuinely the city's"
                        : money(-yours) + " more is owed than is held",
                yours < 0 ? Palette.BAD : Palette.GOOD,
                "The reserves", "What is yours"));

        column.getChildren().add(tradeRow("The currency",
                "the rate, and the four forces on it: trade, the real rate, the vault, parity",
                fxRate(fx.getRate()),
                fx.isPinned() ? "held fixed"
                        : Math.abs(fx.deviationFromParity()) < .005 ? "sitting at parity"
                        : String.format("%.1f%% %s than parity",
                                Math.abs(fx.deviationFromParity()) * 100,
                                fx.deviationFromParity() > 0 ? "weaker" : "stronger"),
                Math.abs(fx.deviationFromParity()) > .25 ? Palette.WARN : Palette.ACCENT,
                "The currency", "The rate"));

        column.getChildren().add(tradeRow("What we trade",
                "steel, ore and food out; stock, scrap and materials in",
                money(fx.getExports()) + " out",
                money(fx.tradeImports()) + " in, a month",
                fx.tradeBalance() >= 0 ? Palette.GOOD : Palette.WARN,
                "What we trade", "In and out"));

        /* ========================== THE QUIET GAUGES ==========================
         *
         * Always present, deliberately understated, and the same three every
         * month - so a player learns what to watch BEFORE the shock rather
         * than reading about it afterwards in a red box.
         */
        column.getChildren().add(statementHead("What decides whether a shock is survivable"));
        column.getChildren().add(coverMeter(fx));
        column.getChildren().add(backingMeter(fx, hot));
        column.getChildren().add(parityMeter(fx));

        /* =========================== AND THE LOUD ONES =========================== */
        if (hot.isStopped()) {
            column.getChildren().add(alert("The money is leaving",
                    String.format("%s. %d month%s before anybody abroad looks at this city "
                    + "again. Nothing new will be lent here and what is here is going home.",
                    hot.getStopReason(), hot.stopMonthsLeft(),
                    hot.stopMonthsLeft() == 1 ? "" : "s")));
        }

        if (fx.monthlyImports() > 0 && fx.importCover() < 3) {
            column.getChildren().add(alert("Under three months of import cover",
                    String.format("The vault holds %s against %s a month of buying abroad. "
                    + "Three months is the line below which the world stops pricing a "
                    + "currency on its trade balance and starts pricing it for a crisis.",
                    money(fx.getReserves()), money(fx.monthlyImports()))));
        }

        double backing = hot.getStock() > 0 ? fx.getReserves() / hot.getStock() : 1;
        if (hot.getStock() > 0 && backing < CapitalFlows.PANIC_BACKING) {
            column.getChildren().add(alert("The hot money is barely backed",
                    String.format("%s of foreign money is parked in this city's bank and the "
                    + "vault holds %s against it — %.0f%% cover. Under %.0f%% the next "
                    + "shock stops being a wobble and becomes a run.",
                    money(hot.getStock()), money(fx.getReserves()), backing * 100,
                    CapitalFlows.PANIC_BACKING * 100)));
        }

        if (fx.netForeignPosition() < 0 && fx.getForeignDebt() > 0) {
            column.getChildren().add(alert("The city owes the world more than it holds",
                    String.format("%s of dollar paper against %s of reserves. A city with "
                    + "deep reserves and deeper dollar debt is not rich, and the reserve "
                    + "line on its own says it is.",
                    money(fx.getForeignDebt()), money(fx.getReserves()))));
        }

        ui.rootMenu.getChildren().addAll(title, lead, tradeVitals(), ui.scrolled(column, 210));
    }

    /**
     * What is left of the reserve once everything owed against it is taken off.
     *
     * Jerus: "reserve (split across fx and fx that is yours)" - and then,
     * fairly: "i think the game tells you exactly whats yours no?"
     *
     * HALF. The reserve is ONE POT: buyReserves() is the only door in, and
     * parking a foreign bond's proceeds is implemented as the treasury taking
     * the local money it just raised and buying foreign currency with it. So
     * the model cannot say which dollar was borrowed and which was earned.
     *
     * It does not need to, because money is fungible and both sides of the
     * balance sheet ARE known exactly: the vault, the dollar paper outstanding
     * (re-read from the bonds themselves each month), and the hot money (which
     * CapitalFlows tracks to the cent). Hold a hundred, owe eighty, and twenty
     * is yours - whichever dollar came from where.
     *
     * HOT MONEY IS COUNTED, and netForeignPosition() does not count it. That is
     * the one addition this makes to the model's own figure, and it is the
     * right one: a claim that can be exercised at no notice is a heavier claim
     * than a bond with a maturity date on it, not a lighter one.
     */
    double ownReserves() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        return fx.getReserves() - fx.getForeignDebt() - ui.game.getCapitalFlows().getStock();
    }

    /** One subject on the trade landing. */
    HBox tradeRow(String name, String blurb, String figure, String sub,
                          String tone, String area, String page) {

        Label heading = new Label(name);
        heading.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_BODY));

        Label what = new Label(blurb);
        what.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));

        VBox left = new VBox(0, heading, what);
        left.setAlignment(Pos.CENTER_LEFT);

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        Label big = new Label(figure);
        big.setStyle(Palette.figure(Palette.SIZE_LEAD, tone));

        Label small = new Label(sub);
        small.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));

        VBox right = new VBox(0, big, small);
        right.setAlignment(Pos.CENTER_RIGHT);

        HBox row = new HBox(Palette.GAP, left, gap, right);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPrefWidth(STATEMENT);
        row.setMaxWidth(STATEMENT);
        String rest = "-fx-padding: 10 12 10 12; -fx-cursor: hand;";
        row.setStyle(rest + Palette.block(Palette.CONTROL));
        row.setOnMouseClicked(e -> {
            tradeArea = area;
            tradePage = page;
            ui.innerScrollAt.remove("showForeignMenu:body");
            showForeignMenu();
        });
        row.setOnMouseEntered(e -> row.setStyle(rest
                + Palette.block(Palette.RAISED, Palette.ACCENT)));
        row.setOnMouseExited(e -> row.setStyle(rest + Palette.block(Palette.CONTROL)));
        VBox.setMargin(row, new javafx.geometry.Insets(0, 0, 6, 0));
        return row;
    }

    HBox tradeVitals() {

        ForeignAccounts fx = ui.game.getForeignAccounts();
        double balance = fx.balance();
        double yours = ownReserves();

        return vitalsBar(
                limitCell("SOLD ABROAD", money(fx.getExports()),
                        fx.monthlyExports() > 0
                                ? money(fx.monthlyExports()) + " a month on average"
                                : "nothing yet",
                        fx.getExports() > 0 ? Palette.GOOD : Palette.TEXT_SPENT),
                limitCell("BOUGHT ABROAD", money(fx.tradeImports()),
                        fx.monthlyImports() > 0
                                ? money(fx.monthlyImports()) + " a month on average"
                                : "nothing yet",
                        fx.tradeImports() > 0 ? Palette.WARN : Palette.TEXT_SPENT),
                limitCell("THE MONTH", (balance >= 0 ? "+" : "−")
                                + money(Math.abs(balance)),
                        balance >= 0 ? "into the position" : "out of the position",
                        balance >= 0 ? Palette.GOOD : Palette.BAD),
                limitCell("IN THE VAULT", money(fx.getReserves()),
                        yours >= 0 ? money(yours) + " of it is yours"
                                   : money(-yours) + " more owed than held",
                        yours < 0 ? Palette.BAD
                                : fx.getReserves() > 0 ? Palette.GOOD : Palette.TEXT_SPENT));
    }

    /* =====================================================================
       ONE SUBJECT, ITS OWN STRIP
       ===================================================================== */

    void drawTradeScreen() {

        String[] pages = switch (tradeArea) {
            case "The reserves"   -> TRADE_VAULT_PAGES;
            case "The currency"   -> TRADE_MONEY_PAGES;
            case "What we trade"  -> TRADE_GOODS_PAGES;
            default               -> TRADE_MONTH_PAGES;
        };

        Label title = new Label(tradeArea.toUpperCase());
        title.setStyle(Palette.words(Palette.SIZE_TITLE, Palette.TEXT_HEAD)
                + " -fx-font-weight: bold; -fx-padding: 8 0 2 0;");

        javafx.scene.layout.FlowPane strip =
                chipStrip(pages, tradePage, Palette.SIZE_LABEL, name -> {
                    tradePage = name;
                    ui.innerScrollAt.remove("showForeignMenu:body");
                    showForeignMenu();
                });
        strip.setStyle("-fx-padding: 8 0 10 0;");

        VBox column = new VBox(0);
        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(Region.USE_PREF_SIZE);

        switch (tradeArea) {
            case "The reserves" -> {
                switch (tradePage) {
                    case "Cover"    -> reserveCoverPage(column);
                    case "Exchange" -> exchangePage(column);
                    default         -> reserveOwnPage(column);
                }
            }
            case "The currency" -> {
                if ("What is moving it".equals(tradePage)) currencyForcesPage(column);
                else                                       currencyRatePage(column);
            }
            case "What we trade" -> {
                if ("Since founding".equals(tradePage)) tradeRecordPage(column);
                else                                    tradeGoodsPage(column);
            }
            default -> {
                if ("The two accounts".equals(tradePage)) bopLedgerPage(column);
                else                                      bopPicturePage(column);
            }
        }

        Button back = new Button("All of trade");
        back.setOnAction(e -> {
            tradeArea = null;
            ui.innerScrollAt.remove("showForeignMenu:body");
            showForeignMenu();
        });

        ui.rootMenu.getChildren().addAll(title, tradeVitals(), strip,
                ui.scrolled(column, 250), back);
    }

    /* =====================================================================
       THE MONTH, AS A RIVER

       A balance of payments IS a conserved quantity - every dollar that came
       in went somewhere - and a ledger cannot say that. It prints two columns
       and leaves the reader to notice they add up.

       So: sources on the left, uses on the right, one trunk in the middle, and
       a band width proportional to money throughout. Because the two sides
       must balance, whichever side is short gets a band for the difference:
       a deficit is FINANCED out of the position, a surplus is ADDED to it, and
       both are drawn as flows rather than as a footnote, because that is what
       they are.
       ===================================================================== */

    /** One band of the river. */
    record Flow(String name, String note, double amount, String colour) { }

    void bopPicturePage(VBox column) {

        ForeignAccounts fx = ui.game.getForeignAccounts();

        java.util.List<Flow> in = new java.util.ArrayList<>();
        java.util.List<Flow> out = new java.util.ArrayList<>();

        if (fx.getExports() > 0) {
            in.add(new Flow("Sold abroad", "steel, ore, surplus food",
                    fx.getExports(), Palette.REVENUE_RAMP[4]));
        }
        if (fx.getFinancialIn() > 0) {
            in.add(new Flow("Capital in", "borrowed abroad, and money parked here",
                    fx.getFinancialIn(), Palette.REVENUE_RAMP[2]));
        }
        if (fx.getSoldThisMonth() > 0) {
            in.add(new Flow("Reserves sold", "the treasury cashing dollars it held",
                    fx.getSoldThisMonth(), Palette.REVENUE_RAMP[0]));
        }

        if (fx.tradeImports() > 0) {
            out.add(new Flow("Bought abroad", "shop stock, mill scrap, materials",
                    fx.tradeImports(), Palette.SPENDING_RAMP[4]));
        }
        if (fx.getForeignInterest() > 0) {
            out.add(new Flow("Interest abroad", "on the city's dollar paper",
                    fx.getForeignInterest(), Palette.SPENDING_RAMP[2]));
        }
        if (fx.getFinancialOut() > 0) {
            out.add(new Flow("Capital out", "repaid abroad, and money going home",
                    fx.getFinancialOut(), Palette.SPENDING_RAMP[3]));
        }
        if (fx.getBoughtThisMonth() > 0) {
            out.add(new Flow("Reserves bought", "the treasury putting money abroad",
                    fx.getBoughtThisMonth(), Palette.SPENDING_RAMP[0]));
        }

        column.getChildren().add(statementHead("Every dollar that crossed the city's edge"));

        double sumIn = 0, sumOut = 0;
        for (Flow f : in)  sumIn  += f.amount();
        for (Flow f : out) sumOut += f.amount();

        if (sumIn <= 0 && sumOut <= 0) {
            column.getChildren().add(sentence(
                    "Nothing crossed the city's edge this month. No exports, no imports, "
                    + "nothing borrowed and nothing repaid — which is a city that is not "
                    + "trading rather than a screen with nothing to say.",
                    Palette.TEXT_MUTED));
            return;
        }

        /* ------------------- and the band that makes it balance ------------------- */
        double gap = sumIn - sumOut;
        if (gap < -.005) {
            in.add(new Flow("Out of the position", "the deficit, financed",
                    -gap, Palette.BAD));
        } else if (gap > .005) {
            out.add(new Flow("Into the position", "the surplus, kept",
                    gap, Palette.GOOD));
        }

        column.getChildren().add(bopRiver(in, out));

        /* -------------------------------- in words -------------------------------- */
        double balance = fx.balance();
        column.getChildren().add(sentence(balance >= 0
                ? String.format("The city took in %s more than it paid out. A surplus month "
                        + "is one where the world owes the city a little more than it did, "
                        + "and that is what makes the next dollar of borrowing cheaper.",
                        money(balance))
                : String.format("The city paid out %s more than it took in. That has to be "
                        + "settled in somebody else's money: out of the vault, or by "
                        + "borrowing abroad, or by the currency falling until the trade "
                        + "balance closes it.", money(-balance)),
                balance >= 0 ? Palette.GOOD : Palette.BAD_TEXT));

        if (Math.abs(fx.valuationChange()) > .005) {
            column.getChildren().add(alert("...and something that is not on the river",
                    String.format("%s of foreign claims were written off this month. It "
                    + "improves what the city owes the world and it is NOT a dollar "
                    + "earned — nobody paid it, so it never crossed the edge and it is not "
                    + "drawn above. It changes the position, which is what a valuation "
                    + "change is.", money(Math.abs(fx.valuationChange())))));
        }
    }

    /**
     * The river itself.
     *
     * THREE STAGES, not two. A two-column Sankey needs a mapping from each
     * source to each use, and there is none here - exports do not specifically
     * pay for imports, they pay for whatever the city bought. So every source
     * merges into one trunk and the trunk splits into every use, which is both
     * the honest structure and the one that draws "the month" as a single
     * quantity.
     *
     * The ribbons are cubic paths with equal heights at both ends, because the
     * amount does not change on the way across. Control points at the midpoint
     * give the standard Sankey S-curve.
     */
    VBox bopRiver(java.util.List<Flow> in, java.util.List<Flow> out) {

        final double W = STATEMENT;
        final double H = 250;
        final double LABEL = 128;         // room for a name on each side
        final double BAR = 11;            // the source and use bars
        final double TRUNK = 52;          // the city in the middle
        final double GAP = 3;             // between stacked bands

        double sum = 0;
        for (Flow f : in) sum += f.amount();
        if (sum <= 0) return new VBox();

        int bands = Math.max(in.size(), out.size());
        double usable = H - GAP * Math.max(0, bands - 1);
        double scale = usable / sum;

        double leftBarX  = LABEL;
        double trunkX0   = W / 2 - TRUNK / 2;
        double trunkX1   = W / 2 + TRUNK / 2;
        double rightBarX = W - LABEL - BAR;

        javafx.scene.layout.Pane face = new javafx.scene.layout.Pane();
        face.setPrefSize(W, H);
        face.setMinSize(W, H);
        face.setMaxSize(W, H);

        /* ------------------------------- the trunk ------------------------------- */
        Region trunk = new Region();
        trunk.setPrefSize(TRUNK, H);
        trunk.setMinSize(TRUNK, H);
        trunk.setStyle("-fx-background-color: " + Palette.RAISED
                + "; -fx-background-radius: 3;");
        trunk.setLayoutX(trunkX0);
        trunk.setLayoutY(0);
        face.getChildren().add(trunk);

        /* --------------------------- the incoming half --------------------------- */
        // A LABEL PER BAND, PUSHED APART. A band worth $347k next to one worth
        // $46M is two pixels tall, and its name lands on top of its neighbour's.
        // The labels are laid out on their own after the bands, each one
        // starting at its band's middle and then shoved down until it clears
        // the one above - which keeps a thin band nameable without pretending
        // it is thicker than it is.
        double sourceY = 0;
        double trunkInY = 0;
        double labelFloor = 0;
        for (Flow f : in) {
            double h = Math.max(2, f.amount() * scale);

            face.getChildren().add(ribbon(
                    leftBarX + BAR, sourceY, trunkX0, trunkInY, h, f.colour()));

            Region bar = new Region();
            bar.setPrefSize(BAR, h);
            bar.setMinSize(BAR, h);
            bar.setStyle("-fx-background-color: " + f.colour() + ";");
            bar.setLayoutX(leftBarX);
            bar.setLayoutY(sourceY);
            Tooltip.install(bar, new Tooltip(f.name() + "\n" + moneyFull(f.amount())
                    + "\n" + f.note()));
            face.getChildren().add(bar);

            double at = Math.max(labelFloor, sourceY + h / 2 - 14);
            face.getChildren().add(bandLabel(f, 0, at, LABEL - 8, true));
            labelFloor = at + 28;

            sourceY  += h + GAP;
            trunkInY += h + GAP;
        }

        /* ---------------------------- and the outgoing ---------------------------- */
        double useY = 0;
        double trunkOutY = 0;
        labelFloor = 0;
        for (Flow f : out) {
            double h = Math.max(2, f.amount() * scale);

            face.getChildren().add(ribbon(
                    trunkX1, trunkOutY, rightBarX, useY, h, f.colour()));

            Region bar = new Region();
            bar.setPrefSize(BAR, h);
            bar.setMinSize(BAR, h);
            bar.setStyle("-fx-background-color: " + f.colour() + ";");
            bar.setLayoutX(rightBarX);
            bar.setLayoutY(useY);
            Tooltip.install(bar, new Tooltip(f.name() + "\n" + moneyFull(f.amount())
                    + "\n" + f.note()));
            face.getChildren().add(bar);

            double at = Math.max(labelFloor, useY + h / 2 - 14);
            face.getChildren().add(bandLabel(f, rightBarX + BAR + 8, at,
                    LABEL - 8, false));
            labelFloor = at + 28;

            useY      += h + GAP;
            trunkOutY += h + GAP;
        }

        /* ------------------------------ the city ------------------------------ */
        Label here = new Label("THE\nCITY");
        here.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED)
                + " -fx-text-alignment: center;");
        here.setLayoutX(trunkX0 + 6);
        here.setLayoutY(H / 2 - 14);
        face.getChildren().add(here);

        /* ------------------------------- the ends ------------------------------- */
        Label inHead = new Label("CAME IN   " + money(sum));
        inHead.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));

        Region spread = new Region();
        HBox.setHgrow(spread, Priority.ALWAYS);

        Label outHead = new Label(money(sum) + "   WENT OUT");
        outHead.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));

        HBox heads = new HBox(inHead, spread, outHead);
        heads.setMaxWidth(W);
        heads.setPrefWidth(W);

        // The two sides always balance, so the closing band says which way the
        // month went. Naming the wrong colour is worse than naming none: the
        // first cut always said "and that is the red band" on a surplus month
        // whose closing band was green.
        boolean financed = false, kept = false;
        for (Flow f : in)  financed |= "Out of the position".equals(f.name());
        for (Flow f : out) kept     |= "Into the position".equals(f.name());

        Label foot = new Label("Band width is money. The two sides balance because they "
                + "must" + (financed
                        ? " \u2014 this month spent more than it earned, and the red band "
                        + "is where the difference came from."
                        : kept
                        ? " \u2014 this month earned more than it spent, and the green "
                        + "band is where the difference went."
                        : " \u2014 what came in and what went out are the same money "
                        + "seen twice.")); 
        foot.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_SPENT));
        foot.setWrapText(true);
        foot.setMaxWidth(W);

        VBox box = new VBox(4, heads, face, foot);
        box.setMaxWidth(W);
        box.setStyle("-fx-padding: 4 0 12 0;");
        return box;
    }

    /**
     * One ribbon: a filled cubic band of constant height between two columns.
     *
     * Both ends are the same height because the amount does not change on the
     * way across - which is the whole claim a Sankey makes, and the reason a
     * band that narrows would be a lie about a conserved quantity.
     */
    javafx.scene.shape.Path ribbon(double x0, double y0,
                                           double x1, double y1,
                                           double h, String colour) {

        double mid = (x0 + x1) / 2;
        javafx.scene.shape.Path band = new javafx.scene.shape.Path();
        band.getElements().addAll(
                new javafx.scene.shape.MoveTo(x0, y0),
                new javafx.scene.shape.CubicCurveTo(mid, y0, mid, y1, x1, y1),
                new javafx.scene.shape.LineTo(x1, y1 + h),
                new javafx.scene.shape.CubicCurveTo(mid, y1 + h, mid, y0 + h, x0, y0 + h),
                new javafx.scene.shape.ClosePath());
        band.setFill(javafx.scene.paint.Color.web(colour));
        band.setStroke(null);
        // Translucent, so a ribbon crossing another still reads as two ribbons
        // rather than as whichever was drawn last.
        band.setOpacity(.55);
        return band;
    }

    /** A band's name and figure, beside its bar. */
    VBox bandLabel(Flow f, double x, double y, double width,
                           boolean rightAlign) {

        Label name = new Label(f.name());
        name.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_BODY));

        Label figure = new Label(money(f.amount()));
        figure.setStyle(Palette.figure(Palette.SIZE_CAPTION, f.colour()));

        VBox box = new VBox(-2, name, figure);
        box.setPrefWidth(width);
        box.setMinWidth(width);
        box.setMaxWidth(width);
        box.setAlignment(rightAlign ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        box.setLayoutX(x);
        box.setLayoutY(Math.max(0, y));
        return box;
    }

    /* --------------------------------------------------------------------- */

    /**
     * THE BALANCE OF PAYMENTS.
     *
     * Everything on this screen was already being computed - MoneyAudit has
     * tracked every flow across the city's edge since the day it was written.
     * What it could not do was tell a HOUSEHOLD from a FOREIGNER, because both
     * sat outside the audited pools for entirely different reasons. Tagging
     * those apart is all this screen is.
     *
     * Written as a statement, in the same shape a real balance of payments takes:
     * the current account (what was sold and bought abroad, and what was paid to
     * foreign lenders), the financial account (capital moving), and the position
     * the two of them leave behind.
     *
     * (The trade screen's first header, and this ledger page is what is left of
     * that screen; the river above is the redo. It had sat in the shell with no
     * method under it, and came here on 2026-09-18.)
     */
    void bopLedgerPage(VBox column) {

        ForeignAccounts fx = ui.game.getForeignAccounts();

        column.getChildren().add(statementHead("The current account"));
        column.getChildren().add(statementLine("Sold abroad",
                signedTight(fx.getExports(), false), Palette.GOOD));
        column.getChildren().add(statementLine("Bought abroad",
                signedTight(fx.tradeImports(), true), Palette.WARN));
        column.getChildren().add(statementTotal("TRADE BALANCE",
                (fx.tradeBalance() >= 0 ? "+" : "−")
                        + money(Math.abs(fx.tradeBalance())),
                fx.tradeBalance() >= 0 ? Palette.GOOD : Palette.BAD));
        column.getChildren().add(statementLine("Paid abroad in interest and dividends, net",
                signedTight(fx.getForeignInterest(), true),
                fx.getForeignInterest() > 0 ? Palette.WARN : Palette.TEXT_SPENT));
        column.getChildren().add(statementTotal("CURRENT ACCOUNT",
                (fx.currentAccount() >= 0 ? "+" : "−")
                        + money(Math.abs(fx.currentAccount())),
                fx.currentAccount() >= 0 ? Palette.GOOD : Palette.BAD));

        column.getChildren().add(statementNote(
                "What the city earned from the world by selling it things, less what it "
                + "spent buying things and servicing what it owes. This is the line a "
                + "country is judged on: it can be negative for years and it cannot be "
                + "negative for ever."));

        column.getChildren().add(statementHead("The financial account"));
        column.getChildren().add(statementLine("Capital in",
                signedTight(fx.getFinancialIn(), false),
                fx.getFinancialIn() > 0 ? Palette.ACCENT : Palette.TEXT_SPENT));
        column.getChildren().add(statementLine("Capital out",
                signedTight(fx.getFinancialOut(), true),
                fx.getFinancialOut() > 0 ? Palette.WARN : Palette.TEXT_SPENT));
        column.getChildren().add(statementTotal("FINANCIAL ACCOUNT",
                (fx.financialAccount() >= 0 ? "+" : "−")
                        + money(Math.abs(fx.financialAccount())),
                fx.financialAccount() >= 0 ? Palette.ACCENT : Palette.WARN));

        column.getChildren().add(statementNote(
                "Borrowing abroad and foreign money parking here are both inflows, and "
                + "neither is income. A country running a current-account deficit funded "
                + "by capital inflows is borrowing to buy — which works until the money "
                + "decides to go home."));

        column.getChildren().add(statementHead("The two together"));
        column.getChildren().add(statementTotal("THE MONTH'S BALANCE",
                (fx.balance() >= 0 ? "+" : "−") + money(Math.abs(fx.balance())),
                fx.balance() >= 0 ? Palette.GOOD : Palette.BAD));

        if (Math.abs(fx.valuationChange()) > .005) {
            column.getChildren().add(statementLine("Claims written off",
                    (fx.valuationChange() >= 0 ? "+" : "−")
                            + money(Math.abs(fx.valuationChange())),
                    Palette.TEXT_MUTED));
            column.getChildren().add(statementNote(
                    "What foreign creditors gave up on. It improves the position and it is "
                    + "not a dollar earned, so it is below the line and out of the river."));
        }

        /*
         * WHAT THE CITY HOLDS ABROAD, AND WHAT THE WORLD HOLDS HERE. The
         * stocks the flows above add up to: the sectors' paper (since the
         * outward batch), the households' (since the exchange), and the
         * city's shares in foreign hands at the desk's quote. The rough shape
         * of an international investment position.
         */
        OutwardInvestment sectorsAbroad = ui.game.getOutwardInvestment();
        HouseholdBalance savers = ui.game.getHouseholdBalance();
        Equity register = ui.game.getEquity();
        Exchange exchange = ui.game.getExchange();
        double sharesAbroad = 0;
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            sharesAbroad += register.getForeignShares(c) * (exchange.isOpen() ? exchange.mid(c) : register.getLastPrice(c));
        }
        if (sectorsAbroad.totalUsd() > 0 || savers.totalAbroadUsd() > 0 || sharesAbroad > 0) {
            column.getChildren().add(statementHead("What the city holds abroad, and what the world holds here"));
            column.getChildren().add(statementLine("The businesses' paper abroad",
                    money(sectorsAbroad.totalLocalValue())
                            + " (US" + money(sectorsAbroad.totalUsd()) + ")", Palette.GOOD));
            column.getChildren().add(statementLine("The households' paper abroad",
                    money(savers.totalAbroadValue())
                            + " (US" + money(savers.totalAbroadUsd()) + ")", Palette.GOOD));
            column.getChildren().add(statementLine("The city's shares in foreign hands",
                    "−" + money(sharesAbroad), sharesAbroad > 0 ? Palette.WARN : Palette.TEXT_SPENT));
            column.getChildren().add(statementNote(
                    "Idle money buys the world's paper when the world pays more than the bank, and "
                    + "comes home when the bank pays more or its owner needs it - the businesses' "
                    + "tills and the households' savings by the same rule. The coupon is rolled where "
                    + "it is earned; it reaches the currency only when the money comes home."));
        }

        /* ---------------------------- and the treasury ---------------------------- */
        if (fx.getBoughtThisMonth() > 0 || fx.getSoldThisMonth() > 0) {
            column.getChildren().add(statementHead("...and what the treasury did about it"));
            if (fx.getBoughtThisMonth() > 0) {
                column.getChildren().add(statementLine("Bought foreign money",
                        money(fx.getBoughtThisMonth()), Palette.WARN));
            }
            if (fx.getSoldThisMonth() > 0) {
                column.getChildren().add(statementLine("Sold foreign money",
                        money(fx.getSoldThisMonth()), Palette.GOOD));
            }
            column.getChildren().add(statementNote(
                    "Below the line, and deliberately: an intervention does not earn or "
                    + "spend anything abroad, it swaps one currency for another. It is the "
                    + "financing item, which is what makes it the last resort rather than "
                    + "the first."));
        }
    }

    /* =====================================================================
       THE RESERVES
       ===================================================================== */

    void reserveOwnPage(VBox column) {

        ForeignAccounts fx = ui.game.getForeignAccounts();
        CapitalFlows hot = ui.game.getCapitalFlows();

        double gross = fx.getReserves();
        double debt = fx.getForeignDebt();
        double parked = hot.getStock();
        double yours = ownReserves();

        column.getChildren().add(statementHead("What is in the vault, and whose it is"));

        if (gross <= 0 && debt <= 0 && parked <= 0) {
            column.getChildren().add(sentence(
                    "The vault is empty and nothing is owed abroad. Export earnings go to "
                    + "the firms that earned them — a treasury holds foreign money only if "
                    + "it has bought some, or borrowed it abroad.", Palette.TEXT_MUTED));
            return;
        }

        /*
         * THE PICTURE IS A CLAIM AGAINST A STOCK, so it is drawn as one bar
         * with the claims eating into it rather than as two bars side by side.
         * Two bars invite the reader to compare heights; one bar says what is
         * left, which is the question.
         */
        column.getChildren().add(claimBar(gross, debt, parked));

        column.getChildren().add(statementLine("In the vault", moneyFull(gross),
                gross > 0 ? Palette.GOOD : Palette.TEXT_SPENT));
        column.getChildren().add(statementLine("Owed abroad on paper",
                signed(debt, true), debt > 0 ? Palette.WARN : Palette.TEXT_SPENT));
        column.getChildren().add(statementLine("Foreign money parked in the bank",
                signed(parked, true), parked > 0 ? Palette.BAD : Palette.TEXT_SPENT));
        column.getChildren().add(statementTotal("What is genuinely the city's",
                (yours >= 0 ? "" : "−") + moneyFull(Math.abs(yours)),
                yours >= 0 ? Palette.GOOD : Palette.BAD));

        column.getChildren().add(sentence(
                "The vault is one pot — the game does not tag a dollar as borrowed or "
                + "earned, and it does not need to, because money is fungible. Hold a "
                + "hundred, owe eighty, and twenty is yours whichever dollar came from "
                + "where. Both sides of that subtraction are known exactly: the paper is "
                + "re-read from the bonds themselves each month, and the parked money is "
                + "tracked to the cent.",
                Palette.TEXT_BODY));

        /* -------------------- the two claims are not alike -------------------- */
        column.getChildren().add(statementHead("The two claims are not alike"));

        javafx.scene.layout.GridPane t = grid(
                new double[] {160, 126, 130, 130}, rightAfterFirst(4));
        gridHead(t, "", "amount", "when it is due", "whose claim");

        int line = 1;
        t.add(gridCell("Dollar paper", Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
        t.add(gridCell(money(debt), Palette.WARN, Palette.SIZE_CAPTION, true), 1, line);
        t.add(gridCell("on a maturity date", Palette.TEXT_MUTED,
                Palette.SIZE_CAPTION, true), 2, line);
        t.add(gridCell("the treasury's", Palette.TEXT_MUTED,
                Palette.SIZE_CAPTION, true), 3, line);
        line++;
        t.add(gridCell("Parked money", Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
        t.add(gridCell(money(parked), Palette.BAD, Palette.SIZE_CAPTION, true), 1, line);
        t.add(gridCell("whenever it likes", Palette.BAD, Palette.SIZE_CAPTION, true), 2, line);
        t.add(gridCell("the bank's", Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 3, line);
        column.getChildren().add(t);

        column.getChildren().add(statementNote(
                "The city's own model already subtracts the paper — that is "
                + "netForeignPosition(). The parked money is the addition this screen "
                + "makes, and it is the right one: a claim that can be exercised at no "
                + "notice is a heavier claim than a bond with a date on it, not a lighter "
                + "one."));

        if (fx.getRepudiated() > 0) {
            column.getChildren().add(statementHead("...and what was walked away from"));
            column.getChildren().add(statementLine("Repudiated",
                    moneyFull(fx.getRepudiated()), Palette.TEXT_MUTED));
        }
    }

    /**
     * One bar of reserves with the claims against it eaten out of the left.
     *
     * Drawn as ONE stock with bites taken out rather than as claims stacked
     * beside it, because the question is what is left rather than how the
     * three compare. When the claims exceed the stock the bar is entirely
     * eaten and the overhang is drawn past its end, which is the state worth
     * seeing.
     */
    VBox claimBar(double gross, double debt, double parked) {

        final double WIDE = STATEMENT - 40;
        final double TALL = 26;

        double claims = debt + parked;
        double scale = Math.max(gross, claims);
        if (scale <= 0) return new VBox();

        HBox strip = new HBox(0);
        strip.setAlignment(Pos.CENTER_LEFT);
        strip.setMaxWidth(WIDE);

        double yours = Math.max(0, gross - claims);
        double[] parts = { debt, parked, yours };
        String[] tones = { Palette.WARN, Palette.BAD, Palette.GOOD };
        String[] names = { "owed abroad on paper", "parked here and free to leave",
                           "the city's own" };

        for (int i = 0; i < 3; i++) {
            if (parts[i] <= 0) continue;
            double w = Math.max(2, parts[i] / scale * WIDE);
            Region seg = new Region();
            seg.setPrefSize(w, TALL);
            seg.setMinSize(w, TALL);
            seg.setMaxSize(w, TALL);
            seg.setStyle("-fx-background-color: " + tones[i] + ";");
            Tooltip.install(seg, new Tooltip(names[i] + "\n" + moneyFull(parts[i])));
            strip.getChildren().add(seg);
            // A 2px gap of the ground between fills, so two segments do not
            // read as one.
            Region gap = new Region();
            gap.setPrefSize(2, TALL);
            gap.setMinSize(2, TALL);
            strip.getChildren().add(gap);
        }

        // WHERE THE VAULT ACTUALLY ENDS. When the claims run past it, the mark
        // is the honest thing on the picture: everything to the right of it is
        // owed against money the city does not have.
        Region mark = new Region();
        mark.setPrefSize(2, TALL + 10);
        mark.setMinSize(2, TALL + 10);
        mark.setStyle("-fx-background-color: " + Palette.TEXT_MAX + ";");
        javafx.scene.layout.Pane over = new javafx.scene.layout.Pane(mark);
        over.setPrefSize(WIDE, TALL + 10);
        over.setMinSize(WIDE, TALL + 10);
        over.setMaxSize(WIDE, TALL + 10);
        mark.setLayoutX(Math.max(0, Math.min(WIDE - 2, gross / scale * WIDE)));
        mark.setLayoutY(-5);
        Tooltip.install(mark, new Tooltip("the vault ends here: " + moneyFull(gross)));

        StackPane bar = new StackPane(strip, over);
        StackPane.setAlignment(strip, Pos.CENTER_LEFT);
        StackPane.setAlignment(over, Pos.CENTER_LEFT);
        bar.setMaxWidth(WIDE);

        javafx.scene.layout.FlowPane key = new javafx.scene.layout.FlowPane(14, 4);
        key.setStyle("-fx-padding: 8 0 0 0;");
        for (int i = 0; i < 3; i++) {
            if (parts[i] <= 0) continue;
            key.getChildren().add(keySwatch(tones[i], names[i]));
        }

        VBox box = new VBox(2, bar, key);
        box.setMaxWidth(STATEMENT);
        box.setStyle("-fx-padding: 8 0 12 0;");
        return box;
    }

    /* --------------------------------------------------------------------- */

    void reserveCoverPage(VBox column) {

        ForeignAccounts fx = ui.game.getForeignAccounts();
        CapitalFlows hot = ui.game.getCapitalFlows();

        column.getChildren().add(statementHead("How long the vault would last"));
        column.getChildren().add(coverMeter(fx));

        column.getChildren().add(statementLine("In the vault", moneyFull(fx.getReserves())));
        column.getChildren().add(statementLine("Bought abroad, a month",
                moneyFull(fx.monthlyImports()), Palette.TEXT_MUTED));
        column.getChildren().add(statementTotal("Which is",
                fx.monthlyImports() > 0
                        ? coverReading(fx.importCover()) + " of imports"
                        : "nothing to cover yet",
                fx.monthlyImports() <= 0 ? Palette.TEXT_SPENT
                        : fx.importCover() < 3 ? Palette.BAD
                        : fx.importCover() < ForeignAccounts.COMFORTABLE_COVER
                                ? Palette.WARN : Palette.GOOD));

        column.getChildren().add(sentence(String.format(
                "Import cover is the oldest test there is: if every dollar of earnings "
                + "stopped tomorrow, how long could the city go on buying? %.0f months is "
                + "the comfortable line in this model, three is where the world stops "
                + "pricing a currency on its trade balance and starts pricing it for a "
                + "crisis.", ForeignAccounts.COMFORTABLE_COVER), Palette.TEXT_BODY));

        /* ------------------------------ the hot money ------------------------------ */
        column.getChildren().add(statementHead("And what is backing the money that can leave"));
        column.getChildren().add(backingMeter(fx, hot));

        if (hot.getStock() <= 0) {
            column.getChildren().add(sentence(
                    "No foreign money is parked in this city's bank. Nothing can leave on "
                    + "no notice, which is a real form of safety and one that a high "
                    + "policy rate gives away.", Palette.GOOD));
        } else {
            column.getChildren().add(statementLine("Parked in the bank",
                    moneyFull(hot.getStock()), Palette.WARN));
            column.getChildren().add(statementLine("...as a share of its funding",
                    String.format("%.0f%%", ui.game.getBank().hotFundingShare() * 100),
                    ui.game.getBank().hotFundingShare() > .25 ? Palette.BAD : Palette.TEXT_MUTED));
            column.getChildren().add(statementLine("The excess return pulling it",
                    String.format("%.2f points", hot.getSpread() * 100), Palette.ACCENT));
            column.getChildren().add(statementLine("What would come at that rate",
                    moneyFull(hot.getTarget()), Palette.TEXT_MUTED));
            column.getChildren().add(statementNote(
                    "What the city pays over the world rate, less what the world charges "
                    + "it for its own risk. Only the rest is a reason to come — and the "
                    + "same money leaves the moment it stops being one."));

            if (hot.getStopsSuffered() > 0) {
                column.getChildren().add(statementLine("Sudden stops since founding",
                        String.valueOf(hot.getStopsSuffered()), Palette.BAD));
            }
            if (hot.getPeakStock() > 0) {
                column.getChildren().add(statementLine("The most that was ever here",
                        moneyFull(hot.getPeakStock()), Palette.TEXT_MUTED));
            }
        }
    }

    /* --------------------------------------------------------------------- */

    /**
     * TURNING RESERVES INTO CASH, AND CASH INTO RESERVES.
     *
     * This was called "intervening in the currency market", which is what an
     * economist calls it and is not what a player is looking for. Jerus, having
     * borrowed abroad and parked the dollars: "add somewhere where you can
     * convert currency you have to cash, idk if thats the intervene button".
     *
     * It was the intervene button, behind a button at the foot of a scrolling
     * page. It is a page now, on the subject it belongs to.
     */
    void exchangePage(VBox column) {

        ForeignAccounts fx = ui.game.getForeignAccounts();

        column.getChildren().add(statementHead(
                tradeBuying ? "Buy foreign money" : "Sell foreign money"));

        column.getChildren().add(chipStrip(
                new String[] {"Buy — cash into the vault", "Sell — the vault into cash"},
                tradeBuying ? "Buy — cash into the vault" : "Sell — the vault into cash",
                Palette.SIZE_CAPTION, pick -> {
                    boolean nowBuying = pick.startsWith("Buy");
                    if (nowBuying != tradeBuying) tradeExchange = 0;
                    tradeBuying = nowBuying;
                    showForeignMenu();
                }));

        column.getChildren().add(sentence(tradeBuying
                ? "Local money out of the treasury, foreign money into the vault. It "
                + "costs the city cash it could have spent, and it buys cover — months of "
                + "imports the vault could pay for, which is what the central bank sells "
                + "to meet a push against the currency."
                : "Foreign money out of the vault, local money into the treasury. This is "
                + "how a treasury gets at money it holds abroad, AND it is everything a "
                + "central bank has ever been able to do about an exchange rate. Both are "
                + "true at once.", Palette.TEXT_BODY));

        /*
         * THE VAULT IN THE MONEY IT IS HELD IN, AND WHAT THE CURRENCY DID TO
         * IT (2026-09-21). The vault is kept in dollars now, so its dollar
         * figure is the one that stays put and its local figure is the one
         * that moves - and the move is booked as a line of its own, the way
         * the dollar debt's always was. Positive is the currency falling: the
         * one thing a city owns that gains when its money loses.
         */
        column.getChildren().add(statementLine("In the vault",
                usdFull(fx.getReservesUsd()),
                fx.getReservesUsd() > 0 ? Palette.TEXT_HEAD : Palette.TEXT_SPENT));
        column.getChildren().add(statementLine("...worth at home, at today's rate",
                moneyFull(fx.getReserves()), Palette.TEXT_MUTED));
        double revalued = fx.getLastVaultRevaluation();
        if (Math.abs(revalued) > .005) {
            column.getChildren().add(statementLine("The currency moved it this month by",
                    (revalued > 0 ? "+" : "−") + moneyFull(Math.abs(revalued)),
                    revalued > 0 ? Palette.GOOD : Palette.WARN));
            column.getChildren().add(statementNote(revalued > 0
                    ? "The currency fell and the same dollars are worth more at home. Nobody "
                    + "was paid anything: it is not cash until they are sold, and a sale is "
                    + "at that day's rate. It is what a reserve is for."
                    : "The currency rose and the same dollars fetch less at home. Nothing "
                    + "was spent and nothing left the vault; sold today, this is what they "
                    + "would raise."));
        }

        /*
         * WHAT THE DEFENCE SPENT (0.7.2). The central bank sells the vault's
         * dollars when the currency is pushed down on a month the city is
         * short of them (ForeignAccounts, A DEFENCE THAT SPENDS): capital
         * spent against the world, off the central bank's equity, and the
         * local money they fetch does not come here. The month's sale and the
         * dollars sold since founding, both saved.
         */
        if (fx.getDefenceUsdLifetime() > 0) {
            column.getChildren().add(statementLine("Sold this month defending the currency",
                    usdFull(fx.getDefenceUsd()),
                    fx.getDefenceUsd() > 0 ? Palette.WARN : Palette.TEXT_SPENT));
            column.getChildren().add(statementLine("...and since founding",
                    usdFull(fx.getDefenceUsdLifetime()), Palette.TEXT_MUTED));
            column.getChildren().add(statementNote(String.format(
                    "Sold %s this month defending the currency; %s since founding. The "
                    + "central bank sells when the currency is pushed down on a month the "
                    + "city is short of dollars - up to %.0f%% of what the month wanted, the "
                    + "share falling with the cover - and what they fetch is the central "
                    + "bank's capital spent, not paid to the treasury. That is what the founders' "
                    + "dollars were for; they are not bought back.",
                    usdFull(fx.getDefenceUsd()), usdFull(fx.getDefenceUsdLifetime()),
                    ForeignAccounts.MAX_ABSORPTION * 100)));
        }

        /*
         * WHAT THE LAND OFFICE SPENT (0.7.6). Land is priced in US dollars,
         * and the treasury pays it one of two ways - the land office's
         * toggle: converting cash, which buys the dollars and hands them
         * straight over so the vault never sees them, or out of this vault.
         * The vault's part, the month the budget struck and since founding,
         * both saved (ForeignAccounts, THE LAND OFFICE IS PAID IN DOLLARS);
         * the note says what the land cost in dollars altogether.
         */
        if (fx.getLandUsdLifetime() > 0) {
            column.getChildren().add(statementLine("Spent on land this month",
                    usdFull(fx.getLandUsdFromVaultThisMonth()),
                    fx.getLandUsdFromVaultThisMonth() > 0 ? Palette.WARN : Palette.TEXT_SPENT));
            column.getChildren().add(statementLine("...and since founding",
                    usdFull(fx.getLandUsdFromVaultLifetime()), Palette.TEXT_MUTED));
            column.getChildren().add(statementNote(String.format(
                    "Land is priced in US dollars. The city paid %s for it this month and %s "
                    + "since founding; what is above came out of this vault, and the rest was "
                    + "cash converted at the day's rate, which buys exactly the dollars and pays "
                    + "them straight over. The land office's toggle chooses which.",
                    usdFull(fx.getLandUsdThisMonth()), usdFull(fx.getLandUsdLifetime()))));
        }

        /*
         * WHERE THE DOLLARS CAME FROM, on a young city. Jerus: "99% players
         * wont add to reserves most probably cause they have no clue" - so the
         * page says there is something in the vault the player did not put
         * there, and what it would carry the city through.
         */
        // THIS city's founders' dollars (0.7.10): the founding chose them.
        if (ui.game.getMonth() <= Game.FOUNDERS_NOTE_MONTHS && fx.getReservesUsd() > 0
                && ui.game.getFoundingReserveUsd() > 0) {
            column.getChildren().add(statementNote(String.format(
                    "The founders left %s in this vault on the first day, bought at %s%.2f "
                    + "to the dollar out of the city's endowment. %s",
                    usdFull(ui.game.getFoundingReserveUsd()), ui.game.getCurrency().qualifiedSymbol(),
                    ForeignAccounts.OPENING_RATE,
                    fx.monthlyImports() > 0
                            ? "What the vault holds now would pay for "
                            + coverReading(fx.importCover()) + " of what the city buys abroad."
                            : "The city is not buying anything abroad yet.")));
        }

        double ceiling = tradeBuying ? Math.max(0, ui.game.getCash()) : fx.sellableReserves();
        tradeExchange = Math.max(0, Math.min(tradeExchange, ceiling));

        column.getChildren().add(statementLine(
                tradeBuying ? "The treasury holds" : "The vault holds",
                moneyFull(ceiling), ceiling > 0 ? Palette.TEXT_HEAD : Palette.TEXT_SPENT));

        if (ceiling <= 0) {
            column.getChildren().add(alert(
                    tradeBuying ? "There is no cash to buy with"
                                : "There is nothing in the vault to sell",
                    tradeBuying
                        ? "The treasury is empty. Buying reserves is spending money the "
                        + "city does not have."
                        : "You cannot sell what you do not hold. A city that needs foreign "
                        + "money and has none has to earn it, borrow it abroad, or let the "
                        + "currency do the work."));
            return;
        }

        // The big figure needs saying what it is. Without the caption above it the
        // screen read "The treasury holds $2.8B" and then an unlabelled "nothing
        // yet" underneath, which looks like a contradiction rather than a field.
        Label asking = new Label(tradeBuying ? "HOW MUCH TO BUY" : "HOW MUCH TO SELL");
        asking.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL)
                + " -fx-padding: 10 0 0 0;");
        column.getChildren().add(asking);

        Label ask = new Label(tradeExchange <= 0 ? "nothing yet" : moneyFull(tradeExchange));
        ask.setStyle(Palette.figure(Palette.SIZE_TITLE,
                tradeExchange > 0 ? Palette.TEXT_HEAD : Palette.TEXT_SPENT));
        column.getChildren().add(ask);

        double[] steps = {ceiling * .05, ceiling * .25, ceiling * .5};
        javafx.scene.layout.FlowPane pick = new javafx.scene.layout.FlowPane(6, 6);
        pick.setMaxWidth(STATEMENT);
        for (double step : steps) {
            if (step <= 0) continue;
            pick.getChildren().add(stepChip("+" + money(step), () -> {
                tradeExchange = Math.min(ceiling, tradeExchange + step);
                showForeignMenu();
            }, false));
        }
        pick.getChildren().add(stepChip("all of it", () -> {
            tradeExchange = ceiling;
            showForeignMenu();
        }, false));
        pick.getChildren().add(stepChip("clear", () -> {
            tradeExchange = 0;
            showForeignMenu();
        }, true));
        column.getChildren().add(pick);

        if (tradeExchange <= 0) return;

        /* ------------------------ what it does to the cover ------------------------ */
        double reservesAfter = tradeBuying
                ? fx.getReserves() + tradeExchange
                : fx.getReserves() - tradeExchange;
        double coverNow = fx.importCover();
        double coverAfter = fx.monthlyImports() > 0
                ? reservesAfter / fx.monthlyImports() : coverNow;

        column.getChildren().add(statementHead("What it would do"));
        column.getChildren().add(statementLine("In the vault",
                moneyFull(fx.getReserves()) + "  →  " + moneyFull(reservesAfter),
                tradeBuying ? Palette.GOOD : Palette.WARN));
        column.getChildren().add(statementLine("In the treasury",
                moneyFull(ui.game.getCash()) + "  →  " + moneyFull(
                        tradeBuying ? ui.game.getCash() - tradeExchange
                                    : ui.game.getCash() + tradeExchange),
                tradeBuying ? Palette.WARN : Palette.GOOD));
        column.getChildren().add(statementLine("Import cover",
                fx.monthlyImports() > 0
                        ? coverReading(coverNow) + "  \u2192  " + coverReading(coverAfter)
                        : "nothing to cover",
                fx.monthlyImports() <= 0 ? Palette.TEXT_SPENT
                        : coverAfter < 3 ? Palette.BAD : Palette.TEXT_HEAD));
        column.getChildren().add(statementLine("In " + Currency.FOREIGN_CODE,
                usdFull(fx.getRate() > 0 ? tradeExchange / fx.getRate() : 0)
                        + "   at " + fxRate(fx.getRate()),
                Palette.TEXT_MUTED));

        column.getChildren().add(statementNote(
                "The cost of a defence is not the cash. It is the cover you no longer "
                + "have — which is why the two figures are shown before and after."));

        if (!tradeBuying && coverAfter < 3 && coverNow >= 3) {
            column.getChildren().add(alert("This would take the city under three months of cover",
                    "Below three months the world stops pricing the currency on the trade "
                    + "balance and starts pricing it for a crisis. Selling reserves to "
                    + "defend a rate, and ending up under the line doing it, is the "
                    + "sequence every currency crisis in the literature has in common."));
        }

        Button confirm = new Button(tradeBuying
                ? "Buy " + money(tradeExchange) + " of foreign money"
                : "Sell " + money(tradeExchange) + " of the vault");
        confirm.setStyle("-fx-background-color: #2f7d52; -fx-text-fill: white;"
                + " -fx-padding: 8 18 8 18;");
        confirm.setOnAction(e -> {
            if (tradeBuying) ui.game.buyForeignCurrency(tradeExchange);
            else             ui.game.sellForeignCurrency(tradeExchange);
            tradeExchange = 0;
            ui.innerScrollAt.remove("showForeignMenu:body");
            showForeignMenu();
        });
        HBox act = new HBox(confirm);
        act.setAlignment(Pos.CENTER_LEFT);
        act.setStyle("-fx-padding: 12 0 4 0;");
        column.getChildren().add(act);
    }

    boolean tradeBuying = true;
    double tradeExchange = 0;

    /* =====================================================================
       THE CURRENCY
       ===================================================================== */

    void currencyRatePage(VBox column) {

        ForeignAccounts fx = ui.game.getForeignAccounts();
        double drift = fx.deviationFromParity();

        column.getChildren().add(statementHead("What one of theirs costs"));

        Label rate = new Label(fxRate(fx.getRate()));
        rate.setStyle(Palette.figure(Palette.SIZE_TITLE, Palette.TEXT_HEAD));
        column.getChildren().add(rate);
        column.getChildren().add(sentence(ui.game.getCurrency().rateUnit()
                + " — how many of ours one of theirs buys.", Palette.TEXT_MUTED));

        column.getChildren().add(parityMeter(fx));

        column.getChildren().add(statementLine("Where the basket says it should be",
                fxRate(fx.getParity()), Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("Where it actually is",
                fxRate(fx.getRate()), Palette.TEXT_HEAD));
        column.getChildren().add(statementTotal(
                fx.isPinned() ? "It is held fixed"
                        : Math.abs(drift) < .005 ? "Sitting at parity"
                        : drift > 0 ? "Weaker than parity by" : "Stronger than parity by",
                Math.abs(drift) < .005 || fx.isPinned() ? ""
                        : String.format("%.1f%%", Math.abs(drift) * 100),
                Math.abs(drift) > .25 ? Palette.WARN : Palette.TEXT_HEAD));

        column.getChildren().add(sentence(
                fx.isPinned()
                    ? "The rate is pinned. A fixed rate is a promise the reserves have to "
                    + "keep, and the market will find out whether they can."
                    : drift > .005
                    ? "A rate ABOVE parity means it takes more of ours to buy one of "
                    + "theirs — the currency is weaker than the same basket of goods says "
                    + "it should be. Imports cost more; what the city sells abroad earns "
                    + "more at home."
                    : drift < -.005
                    ? "A rate BELOW parity means the currency is stronger than the basket "
                    + "says. Imports are cheap and the exporters are being squeezed."
                    : "The rate is where the price of the same basket at home and abroad "
                    + "says it should be. That is not luck — it is what the pull back to "
                    + "parity does over time.",
                Math.abs(drift) > .25 ? Palette.WARN : Palette.TEXT_BODY));

        column.getChildren().add(statementNote(
                "Parity here is relative purchasing power: the city's own price index "
                + "against the world's. A currency that has wandered a long way from what "
                + "the same basket costs abroad gets pulled back, slowly, whatever else is "
                + "happening to it."));

        /* ------------------------------ what it means ------------------------------ */
        column.getChildren().add(statementHead("What the rate is doing to the city"));

        double debt = fx.getForeignDebt();
        if (debt > 0) {
            column.getChildren().add(statementLine("Owed abroad, in "
                            + Currency.FOREIGN_CODE,
                    usdFull(fx.getForeignDebtUsd())));
            column.getChildren().add(statementLine("...which at this rate is",
                    moneyFull(debt), Palette.WARN));
            if (Math.abs(fx.getLastRevaluation()) > .005) {
                column.getChildren().add(statementLine("The currency moved it by",
                        (fx.getLastRevaluation() > 0 ? "+" : "−")
                                + moneyFull(Math.abs(fx.getLastRevaluation())),
                        fx.getLastRevaluation() > 0 ? Palette.BAD : Palette.GOOD));
                column.getChildren().add(statementNote(fx.getLastRevaluation() > 0
                        ? "Dearer this month, and nobody was paid a cent for it. That is "
                        + "what a devaluation does to a country that borrowed in somebody "
                        + "else's money."
                        : "Cheaper this month — the same paper, a stronger currency. It "
                        + "works both ways and it is not income either way."));
            }
            if (Math.abs(fx.getLifetimeRevaluation()) > .005) {
                column.getChildren().add(statementLine("Since founding, the currency has",
                        (fx.getLifetimeRevaluation() > 0 ? "added " : "taken off ")
                                + moneyFull(Math.abs(fx.getLifetimeRevaluation())),
                        fx.getLifetimeRevaluation() > 0 ? Palette.BAD : Palette.GOOD));
            }
        } else {
            column.getChildren().add(sentence(
                    "Nothing is owed abroad, so the rate cannot make the city's debt "
                    + "bigger. It still decides what imports cost and what exports earn.",
                    Palette.GOOD));
        }
    }

    /* --------------------------------------------------------------------- */

    void currencyForcesPage(VBox column) {

        ForeignAccounts fx = ui.game.getForeignAccounts();

        /*
         * ONE READING, AND IT IS THE NEXT MONTH'S (0.7.1): every figure on this
         * page is what the next reprice will do on the accounts as they stand
         * now - the pressure, what the vault takes of it, the push and the
         * pull. It printed the month's RECORDED pressure and absorption
         * beside this preview's push, and once the month's accounts had moved
         * the three did not add up to each other. Previews, all of them, and
         * none records anything: effectivePressure() is the month's own call,
         * and a page must not rewrite what the month wrote.
         */
        double pressure  = fx.previewRawPressure();
        double absorbed  = fx.previewAbsorption();
        double effective = fx.previewPressure();
        double push = effective * ForeignAccounts.DRIFT_SPEED;
        double pull = (fx.getParity() - fx.getRate()) * ForeignAccounts.REVERSION
                / Math.max(.0001, fx.getRate());

        double account = fx.monthlyCurrentAccount();
        double capital = fx.monthlyFinancialAccount();

        column.getChildren().add(statementHead("The forces on the rate"));

        if (fx.isPinned()) {
            column.getChildren().add(alert("The rate is pinned",
                    "None of this is moving it, because the treasury has promised it will "
                    + "not move. What the forces below would have done is what the "
                    + "reserves are now absorbing instead."));
        }

        column.getChildren().add(statementNote(
                "One reading: what the next month does to the rate, on the accounts as "
                + "they stand today. Four forces: the trade balance, the real rate, the "
                + "vault's defence against a fall, and the pull back to parity. The "
                + "city's inflation is not one of them - it reaches the rate through the "
                + "parity it raises and the trade a dear currency loses."));

        boolean nothing = Math.abs(account) < .5;
        forceLine(column, "The current account, 12 months",
                nothing ? "nothing crossed" : signed(account, false),
                nothing ? Palette.TEXT_SPENT : account > 0 ? Palette.GOOD : Palette.BAD,
                nothing
                        ? "Nothing has crossed the city's edge in either direction, so "
                          + "there is no imbalance for the rate to answer and the basket "
                          + "is the only thing left holding it where it is."
                        : account > 0
                        ? "A surplus is the world handing the city more of its money "
                          + "than the city hands back. That has to be held somewhere, "
                          + "and the somewhere pushes the rate stronger."
                        : "A deficit has to be paid for in somebody else's money. The "
                          + "city either earns it, borrows it, or spends the vault - "
                          + "and whichever it is, it pushes the rate weaker.");

        /*
         * THE OTHER HALF OF THE SIGNAL, AND IT WAS NOT ON THIS SCREEN. The
         * pressure below is struck on the current account PLUS this one, and
         * a playtest measured the two very nearly cancelling: a $1.6M monthly
         * surplus against $1.27M going back out, for a net push of almost
         * nothing. A player reading a huge surplus next to a pressure of zero
         * had no line on any screen that explained it.
         */
        boolean still = Math.abs(capital) < .5;
        forceLine(column, "The financial account, 12 months",
                still ? "nothing moved" : signed(capital, false),
                still ? Palette.TEXT_SPENT : capital < 0 ? Palette.WARN : Palette.GOOD,
                still
                        ? "No money is crossing the edge except for goods, so the trade "
                          + "balance above is the whole of the imbalance."
                        : capital < 0
                        ? "Money leaving to be invested or lent abroad - the families' "
                          + "savings going out for the world's rate, and foreigners "
                          + "borrowing here to hold dollars. Every dollar of it is local "
                          + "currency sold, which offsets a surplus one for one. A city "
                          + "can run a permanent surplus and a flat rate at the same "
                          + "time, and this is how."
                        : "Money coming in to be lent or invested here. It has to be "
                          + "bought with somebody's dollars first, so it pushes the same "
                          + "way a surplus does.");

        double trade = fx.pressure();
        forceLine(column, "The trade term",
                Math.abs(trade) < 1e-9 ? "none"
                        : String.format("%.1f%% %s", Math.abs(trade) * 100,
                                trade > 0 ? "weaker" : "stronger"),
                trade > 0 ? Palette.BAD : trade < 0 ? Palette.GOOD : Palette.TEXT_SPENT,
                "Both imbalances together, as a share of everything that crosses in "
                + "either direction. A big number on a small trade is a small push - "
                + "and a surplus financed by money going back out is no push at all.");

        /*
         * THE REAL RATE (0.7.2): the differential the last reprice was handed
         * and what it is worth, with its four halves, so a player can see
         * that a dial under inflation is a rate the world is paid to leave.
         */
        double real = fx.getRealRateDifferential();
        double worth = fx.ratePressure();
        forceLine(column, "The real rate, over the world's",
                String.format("%+.2f pts, worth %.1f%% %s", real * 100, Math.abs(worth) * 100,
                        worth > 0 ? "weaker" : "stronger"),
                real >= 0 ? Palette.GOOD : Palette.BAD,
                String.format("The dial less the city's inflation (%s less %+.1f%%), against the "
                        + "world's rate less the world's (%s less %+.1f%%). Money goes where it "
                        + "is paid better in real terms: a dial under inflation is a rate the "
                        + "world is paid to leave, and the currency falls on the outflow; a dial "
                        + "over it holds the currency however fast prices are rising.",
                        pct2(ui.game.getDebtManager().getPolicyRate()), fx.getLocalInflation() * 100,
                        pct2(DebtManager.WORLD_BASE_RATE), fx.getWorldInflation() * 100));

        forceLine(column, "Pressure on the rate",
                Math.abs(pressure) < 1e-9 ? "none"
                        : String.format("%.1f%% %s", Math.abs(pressure) * 100,
                                pressure > 0 ? "weaker" : "stronger"),
                pressure > 0 ? Palette.BAD : pressure < 0 ? Palette.GOOD
                        : Palette.TEXT_SPENT,
                "The two together: what the month would push the currency by before the "
                + "vault and the openness have had their say.");

        /*
         * THE VAULT'S DEFENCE (0.7.2): what it would sell and meet on these
         * figures, and - a fact, not a preview - what it did sell at the last
         * reprice. Its capacity is absorption(); what damps is what is sold.
         */
        forceLine(column, "The vault's defence",
                absorbed > 0 ? String.format("sells %s, %.0f%%", usdFull(fx.previewDefenceUsd()),
                        absorbed * 100) : "sells nothing",
                absorbed > .5 ? Palette.WARN : Palette.TEXT_BODY,
                String.format("Against a push to fall, the central bank sells the vault's dollars: "
                        + "up to %.0f%% of what the month's own accounts are short of (the vault "
                        + "could do %.0f%% at %s of cover), and what the dollars meet is taken off "
                        + "the push, at the cost of the central bank's equity. A push to rise is never met, "
                        + "and a month that is not short of dollars sells none. At the last reprice "
                        + "it sold %s and met %.0f%% of that month's deficit.",
                        ForeignAccounts.MAX_ABSORPTION * 100, fx.absorption() * 100,
                        coverReading(fx.importCover()), usdFull(fx.getDefenceUsd()),
                        fx.getLastAbsorption() * 100));

        forceLine(column, "Openness of the economy",
                String.format("%.0f%%", fx.getOpenness() * 100),
                Palette.TEXT_BODY,
                "Trade measured against output. A city that barely trades barely "
                + "moves its own rate, whatever the pressure reads.");

        /* -------------------------- what it comes to -------------------------- */

        column.getChildren().add(statementHead("Which comes to"));

        java.util.List<Slice> parts = new java.util.ArrayList<>();
        if (Math.abs(push) > 1e-9) {
            parts.add(new Slice(push > 0 ? "the month, pushing it weaker"
                                         : "the month, pushing it stronger",
                    Math.abs(push), push > 0 ? Palette.BAD : Palette.GOOD));
        }
        if (Math.abs(pull) > 1e-9) {
            parts.add(new Slice(pull > 0 ? "the basket, pulling it weaker"
                                         : "the basket, pulling it stronger",
                    Math.abs(pull), Palette.ACCENT_FILL));
        }
        if (!parts.isEmpty()) {
            column.getChildren().add(keyedBar(parts, STATEMENT - 40));
            column.getChildren().add(statementNote(
                    "Band width is how hard each one is pulling this month. They "
                    + "pull against each other when the signs differ, and the rate "
                    + "moves by whatever is left over."));
        }

        column.getChildren().add(statementLine("This month's push",
                String.format("%+.3f%%", push * 100),
                push > 0 ? Palette.BAD : push < 0 ? Palette.GOOD : Palette.TEXT_SPENT));
        column.getChildren().add(statementLine("Pull back to parity",
                String.format("%+.3f%%", pull * 100), Palette.ACCENT));
        column.getChildren().add(statementTotal("Net movement",
                String.format("%+.3f%%", (push + pull) * 100),
                push + pull > 0 ? Palette.WARN : Palette.GOOD));

        column.getChildren().add(statementNote(
                "The push is what the month is doing to the currency; the pull is the "
                + "basket dragging it back to what the same goods cost abroad. A city "
                + "whose central bank sells dollars into a fall feels less of it - while "
                + "the vault lasts - and none of a push to rise is ever met; the pull is "
                + "the force that never lets go."));
    }

    /**
     * A reading, and the sentence that says what it means, under it.
     *
     * The first cut of this page put the sentence in a third column and every
     * one of them was cut off mid-word. A reason that does not fit is not a
     * reason; it goes under the line where it has the whole width.
     */
    void forceLine(VBox column, String label, String value,
                           String tone, String what) {
        column.getChildren().add(statementLine(label, value, tone));
        column.getChildren().add(statementNote(what));
    }

    /* =====================================================================
       WHAT WE TRADE
       ===================================================================== */

    void tradeGoodsPage(VBox column) {

        ForeignAccounts fx = ui.game.getForeignAccounts();
        EconomyManager em = ui.game.getEconomyManager();

        column.getChildren().add(statementHead("This month, across the edge"));

        java.util.List<Slice> sides = new java.util.ArrayList<>();
        if (fx.getExports() > 0) {
            sides.add(new Slice("sold abroad", fx.getExports(), Palette.REVENUE_RAMP[4]));
        }
        if (fx.tradeImports() > 0) {
            sides.add(new Slice("bought abroad", fx.tradeImports(),
                    Palette.SPENDING_RAMP[4]));
        }
        if (!sides.isEmpty()) column.getChildren().add(keyedBar(sides, STATEMENT - 40));

        column.getChildren().add(statementLine("Sold abroad",
                moneyFull(fx.getExports()), Palette.GOOD));
        column.getChildren().add(statementLine("Bought abroad",
                moneyFull(fx.tradeImports()), Palette.WARN));
        column.getChildren().add(statementTotal("The visible balance",
                (fx.tradeBalance() >= 0 ? "+" : "−")
                        + moneyFull(Math.abs(fx.tradeBalance())),
                fx.tradeBalance() >= 0 ? Palette.GOOD : Palette.BAD));

        /* ------------------------------ at what price ------------------------------ */
        column.getChildren().add(statementHead("What the world is charging"));

        javafx.scene.layout.GridPane t = grid(
                new double[] {186, 130, 122, 108}, rightAfterFirst(4));
        gridHead(t, "", "world price", "at this rate", "which way");

        WorldEconomy world = ui.game.getWorldEconomy();

        // Every good the world trades with the city, both ways where it does.
        int line = 1;
        for (GoodsMarket m : ui.game.getMarkets().all()) {
            Good g = m.good();
            // The world's price today is the constant times the world's own
            // price level; the market's rate carries both that and the currency.
            double level = world == null ? 1 : world.getPriceLevel();
            if (g.exportable()) {
                line = priceRow(t, line, g.label() + ", exported",
                        g.worldExportPrice() * level, fx.getRate(), true);
            }
            if (g.importable()) {
                line = priceRow(t, line, (g == Good.IRON ? "Scrap" : g.label()) + ", imported",
                        g.worldImportPrice() * level, fx.getRate(), false);
            }
        }
        column.getChildren().add(t);

        if (world != null) {
            column.getChildren().add(statementLine("The world's own price level",
                    String.format("%.3f", world.getPriceLevel()), Palette.TEXT_MUTED));
            column.getChildren().add(statementLine("...moving at",
                    String.format("%+.2f%% a year", world.getInflation() * 100),
                    Palette.TEXT_MUTED));
        }

        column.getChildren().add(statementNote(
                "Every world price is quoted in the world's money and converted at the "
                + "rate. So a weaker currency raises what imports cost at home and what "
                + "exports earn at home, both at once — which is the whole mechanism by "
                + "which a devaluation closes a trade deficit, and the whole reason it "
                + "hurts while it does."));

        /* ------------------------------- what moved ------------------------------- */
        column.getChildren().add(statementHead("What the city actually ships"));
        column.getChildren().add(sentence(
                "Out: iron ore the mines lift and the mills do not want, steel the mills "
                + "make and the city cannot use, and surplus food. In: shop stock the "
                + "industry has not made, scrap for the foundries, and the materials every "
                + "building is put up with.", Palette.TEXT_BODY));
    }

    int priceRow(javafx.scene.layout.GridPane table, int line, String label,
                         double worldPrice, double rate, boolean earned) {
        table.add(gridCell(label, Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
        table.add(gridCell(marked(Currency.FOREIGN_SYMBOL, unitPrice(worldPrice)),
                Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 1, line);
        table.add(gridCell(unitPrice(worldPrice * rate),
                Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 2, line);
        table.add(gridCell(earned ? "earned" : "paid",
                earned ? Palette.GOOD : Palette.WARN, Palette.SIZE_CAPTION, true), 3, line);
        return line + 1;
    }

    /* --------------------------------------------------------------------- */

    void tradeRecordPage(VBox column) {

        ForeignAccounts fx = ui.game.getForeignAccounts();

        column.getChildren().add(statementHead("Since the city was founded"));

        column.getChildren().add(statementLine("Sold abroad",
                moneyFull(fx.getLifetimeExports()), Palette.GOOD));
        column.getChildren().add(statementLine("Bought abroad",
                signed(fx.getLifetimeImports(), true), Palette.WARN));
        column.getChildren().add(statementLine("Paid abroad in interest and dividends, net",
                signed(fx.getLifetimeInterest(), true),
                fx.getLifetimeInterest() > 0 ? Palette.WARN : Palette.TEXT_SPENT));
        column.getChildren().add(statementLine("Capital taken",
                (fx.getLifetimeFinancial() >= 0 ? "+" : "−")
                        + moneyFull(Math.abs(fx.getLifetimeFinancial())),
                Palette.ACCENT));
        column.getChildren().add(statementTotal("The whole record",
                (fx.balanceFromFlows() >= 0 ? "+" : "−")
                        + moneyFull(Math.abs(fx.balanceFromFlows())),
                fx.balanceFromFlows() >= 0 ? Palette.GOOD : Palette.BAD));

        column.getChildren().add(sentence(
                "One month says whether a mill was staffed. The run says whether the city "
                + "earns its living. Nobody can spend this figure — it is a record, not a "
                + "stock.", Palette.TEXT_MUTED));

        double trade = fx.getLifetimeExports() - fx.getLifetimeImports();
        column.getChildren().add(statementHead("Does the city earn its living?"));
        column.getChildren().add(statementLine("On trade alone",
                (trade >= 0 ? "+" : "−") + moneyFull(Math.abs(trade)),
                trade >= 0 ? Palette.GOOD : Palette.BAD));
        column.getChildren().add(sentence(trade >= 0
                ? "Over its whole life this city has sold the world more than it has "
                + "bought from it. That is a city that pays its own way, and it is the "
                + "position from which borrowing abroad is a choice rather than a "
                + "necessity."
                : "Over its whole life this city has bought more from the world than it "
                + "has sold to it. Every dollar of that gap was financed — borrowed, or "
                + "paid out of the vault, or taken out of the currency. It is not a debt "
                + "in itself, but it is what a debt is made of.",
                trade >= 0 ? Palette.GOOD : Palette.BAD_TEXT));

        if (fx.getLifetimeIntervention() != 0) {
            column.getChildren().add(statementLine("Net bought into the vault",
                    (fx.getLifetimeIntervention() >= 0 ? "+" : "−")
                            + moneyFull(Math.abs(fx.getLifetimeIntervention())),
                    Palette.TEXT_MUTED));
        }
        if (fx.getForgiven() != 0) {
            column.getChildren().add(statementLine("Claims forgiven or settled",
                    moneyFull(fx.getForgiven()), Palette.TEXT_MUTED));
        }
        if (fx.getRepudiated() > 0) {
            column.getChildren().add(statementLine("Walked away from",
                    moneyFull(fx.getRepudiated()), Palette.BAD));
        }

        column.getChildren().add(statementHead("...and where it stands now"));
        column.getChildren().add(statementLine("Held abroad",
                moneyFull(fx.getReserves()), Palette.GOOD));
        column.getChildren().add(statementLine("Owed abroad",
                signed(fx.getForeignDebt(), true),
                fx.getForeignDebt() > 0 ? Palette.WARN : Palette.TEXT_SPENT));
        column.getChildren().add(statementTotal("Net position",
                (fx.netForeignPosition() >= 0 ? "+" : "−")
                        + moneyFull(Math.abs(fx.netForeignPosition())),
                fx.netForeignPosition() >= 0 ? Palette.GOOD : Palette.BAD));
    }

    /* =====================================================================
       THE THREE QUIET GAUGES

       Always on the landing, deliberately understated, and the same three
       every month. Each is a band meter rather than a number, because "4.2
       months of cover" means nothing on its own and "just inside the line"
       means something to anybody.
       ===================================================================== */

    /**
     * Import cover, in words a player can act on.
     *
     * The first cut clamped at 999 and printed "999.0 months", which is a lie
     * dressed as a measurement - a city with $100M in the vault and $36k a month
     * of imports has 2,777 months of cover, and neither figure means anything.
     * Past ten years the answer is not a number, it is "this is not the problem".
     */
    String coverReading(double months) {
        if (months >= 120) return "over 10 years";
        return String.format("%.1f months", months);
    }

    VBox coverMeter(ForeignAccounts fx) {
        double cover = fx.monthlyImports() > 0 ? fx.importCover() : -1;
        return bandMeter("Import cover",
                cover < 0 ? "nothing bought yet" : coverReading(cover),
                cover < 0 ? 0 : Math.min(1, cover / (ForeignAccounts.COMFORTABLE_COVER * 2)),
                new double[] {3.0 / (ForeignAccounts.COMFORTABLE_COVER * 2),
                              ForeignAccounts.COMFORTABLE_COVER
                                      / (ForeignAccounts.COMFORTABLE_COVER * 2), 1},
                new String[] {Palette.BAD, Palette.WARN, Palette.GOOD},
                String.format("crisis pricing under 3 · comfortable over %.0f",
                        ForeignAccounts.COMFORTABLE_COVER),
                cover < 0);
    }

    VBox backingMeter(ForeignAccounts fx, CapitalFlows hot) {
        boolean none = hot.getStock() <= 0;
        double backing = none ? 1 : fx.getReserves() / hot.getStock();
        return bandMeter("Backing for the money that can leave",
                none ? "nothing parked here"
                     : backing >= 5 ? "over 500%"
                     : String.format("%.0f%%", backing * 100),
                Math.min(1, backing / 2),
                new double[] {CapitalFlows.PANIC_BACKING / 2, .5, 1},
                new String[] {Palette.BAD, Palette.WARN, Palette.GOOD},
                String.format("a run becomes likely under %.0f%%",
                        CapitalFlows.PANIC_BACKING * 100),
                none);
    }

    VBox parityMeter(ForeignAccounts fx) {
        double drift = Math.abs(fx.deviationFromParity());
        return bandMeter("How far the rate has run from parity",
                fx.isPinned() ? "held fixed" : String.format("%.1f%%", drift * 100),
                Math.min(1, drift / .5),
                new double[] {.10 / .5, .25 / .5, 1},
                new String[] {Palette.GOOD, Palette.WARN, Palette.BAD},
                "the basket pulls it back, slowly, however far it goes",
                fx.isPinned());
    }

    /**
     * A meter with named bands and the city's mark on it.
     *
     * @param at    where the city sits, 0 to 1 across the whole meter
     * @param edges the right-hand edge of each band, in the same 0-to-1 space
     * @param muted true when the reading does not apply, so the mark is hidden
     *              rather than parked at zero and read as a bad one
     */
    VBox bandMeter(String label, String reading, double at,
                           double[] edges, String[] tones, String note, boolean muted) {

        final double WIDE = STATEMENT - 230;

        Label name = new Label(label);
        name.setPrefWidth(200);
        name.setMinWidth(200);
        name.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_BODY));

        HBox track = new HBox(0);
        track.setAlignment(Pos.CENTER_LEFT);
        double from = 0;
        for (int i = 0; i < edges.length; i++) {
            double w = Math.max(2, (edges[i] - from) * WIDE);
            Region band = new Region();
            band.setPrefSize(w, 10);
            band.setMinSize(w, 10);
            band.setMaxSize(w, 10);
            band.setStyle("-fx-background-color: " + tones[i] + "; -fx-opacity: .30;");
            track.getChildren().add(band);
            from = edges[i];
        }

        Region mark = new Region();
        mark.setPrefSize(3, 18);
        mark.setMinSize(3, 18);
        mark.setStyle("-fx-background-color: " + Palette.TEXT_MAX + ";");
        showIf(mark, !muted);
        javafx.scene.layout.Pane over = new javafx.scene.layout.Pane(mark);
        over.setPrefSize(WIDE, 18);
        over.setMinSize(WIDE, 18);
        over.setMaxSize(WIDE, 18);
        mark.setLayoutX(Math.max(0, Math.min(WIDE - 3, at * WIDE)));
        mark.setLayoutY(-4);

        StackPane bar = new StackPane(track, over);
        StackPane.setAlignment(track, Pos.CENTER_LEFT);
        StackPane.setAlignment(over, Pos.CENTER_LEFT);
        bar.setMaxWidth(WIDE);

        Label figure = new Label(reading);
        figure.setPrefWidth(140);
        figure.setMinWidth(140);
        figure.setAlignment(Pos.CENTER_RIGHT);
        figure.setStyle(Palette.figure(Palette.SIZE_CAPTION,
                muted ? Palette.TEXT_SPENT : Palette.TEXT_HEAD));

        HBox row = new HBox(Palette.GAP_TIGHT, name, bar, figure);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(STATEMENT);

        Label under = new Label("      " + note);
        under.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_SPENT));

        VBox box = new VBox(1, row, under);
        box.setMaxWidth(STATEMENT);
        box.setStyle("-fx-padding: 5 0 8 0;");
        return box;
    }
}
