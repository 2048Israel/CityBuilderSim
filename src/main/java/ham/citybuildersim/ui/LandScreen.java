package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
 * The land office: how the city pays and the city's position across the top,
 * the plots on the market as tiles you can compare - price per square foot
 * against what the office charges outside, the best value and the richest
 * deposit flagged - and the economics of the margin underneath.
 *
 * In US dollars since 0.7.6: every plot shows its dollar price and what that
 * costs in local money at today's rate, and a chip pair at the top chooses
 * whether the treasury converts cash for it (the default) or pays out of the
 * vault.
 *
 * Since 0.7.13 a plot's price is large in the money the toggle pays in, with
 * the other beside it; its size reads in square kilometres rather than
 * blocks; its button stays live and, short, opens the build screen's
 * funding page sized to the gap (showLandFunding()); and a control above the
 * cards buys the next N of them at once (nextPlots()).
 *
 * Split out of UserInterface on 2026-09-18: the banners THE LAND OFFICE
 * and THE STATEMENT (what was left of it once the statement primitives had
 * gone to Statement.java - the plot tile) exactly as they were, the shell's
 * members reached through ui. The shell only ever calls showLandMenu().
 */
final class LandScreen {

    /** The window this screen draws into: its game, its root, its clearMenu(). */
    private final UserInterface ui;

    LandScreen(UserInterface ui) { this.ui = ui; }

    /* =====================================================================
       THE LAND OFFICE.

       Everything this screen said, it still says. What changed is that it is a
       MARKET now rather than a statement about one.

       Ten plots were ten monospaced lines in the middle of five report
       sections, and the thing you came here to do - decide which one to buy -
       was the hardest thing on it. You could see each plot's size and its total
       price, so comparing two meant dividing one by the other in your head, ten
       times. The market already knew which was the best value and which had the
       most ore under it - LandMarket.bestValue() and richestDeposit() have
       existed the whole time and nothing on screen had ever called them.

       So: the position across the top, the plots as cards you can compare, and
       the economics underneath where they belong - because how the margin works
       is something you read once, and which plot to buy is something you decide
       every time.
       ===================================================================== */
    void showLandMenu() {
        ui.clearMenu("showLandMenu", () -> showLandMenu());

        LandManager land = ui.game.getLandManager();
        LandMarket market = land.getMarket();

        double free = land.getAvailableSqFt();
        double used = land.getUtilisation();
        double outside = land.getAcquisitionCostPerSqFt();
        double outsideUsd = land.getGroundUsdPerSqFt();
        double inside = land.getPricePerSqFt();
        double margin = land.getMarginPerSqFt();
        ForeignAccounts fx = ui.game.getForeignAccounts();
        String here = ui.game.getCurrency().qualifiedSymbol();

        Label title = new Label("LAND OFFICE");
        title.setStyle(Palette.words(Palette.SIZE_TITLE, Palette.TEXT_HEAD)
                + " -fx-font-weight: bold; -fx-padding: 8 0 2 0;");

        /* =================== HOW THE LAND IS PAID FOR (0.7.6) ===================
         *
         * Jerus: "a little toggle at the top to choose, when you buy land, to
         * use up your USD reserves or to convert cash into usd exactly to buy
         * the land, and the default is that you convert." At the top because
         * it decides what every Buy button below it does; applied at once and
         * saved with the city (Game.setLandPaidFromVault()). The sentence under
         * it says what each way does and what the vault holds, and the last
         * purchase's receipt - which says so when a short vault was topped up
         * by converting - sits under that.
         */
        final String converting = "Pay by converting cash";
        final String fromVault = "Pay from the vault";
        boolean vaultPays = ui.game.isLandPaidFromVault();
        javafx.scene.layout.FlowPane payWith = chipStrip(new String[] { converting, fromVault },
                vaultPays ? fromVault : converting, Palette.SIZE_LABEL, name -> {
                    ui.game.setLandPaidFromVault(fromVault.equals(name));
                    showLandMenu();
                });
        Label how = new Label(vaultPays
                ? String.format("Land is priced in US dollars. Paying from the vault spends its "
                        + "dollars and moves no cash; it holds %s (%s at today's rate), and a plot "
                        + "dearer than that opens the funding page: dollars borrowed abroad into the vault, or what the "
                        + "vault holds with the rest converted from cash.",
                        usdFull(fx.getReservesUsd()), marked(here, moneyFull(fx.getReserves())))
                : String.format("Land is priced in US dollars. Converting buys exactly the dollars "
                        + "a plot costs out of cash, at %s%s to the dollar today, and leaves the "
                        + "vault's %s where it is.",
                        here, fxRate(fx.getRate()), usdFull(fx.getReservesUsd())));
        how.setWrapText(true);
        how.setMaxWidth(TILE_WIDTH * 3 + TILE_GAP * 2);
        how.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED)
                + " -fx-padding: 4 0 2 0;");
        VBox paying = new VBox(2, payWith, how);
        paying.setAlignment(Pos.CENTER);
        String receipt = ui.game.getLastLandReceipt();
        if (receipt != null && !receipt.isEmpty()) {
            Label last = new Label(receipt);
            last.setWrapText(true);
            last.setMaxWidth(TILE_WIDTH * 3 + TILE_GAP * 2);
            last.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_BODY)
                    + " -fx-padding: 0 0 4 0;");
            paying.getChildren().add(last);
        }

        /* ===================== WHERE THE CITY STANDS =====================
         *
         * The same four-cell bar the build screen uses, and for the same
         * reason: these are the figures every decision below them is made
         * against, so they go above the decisions rather than inside a section
         * three scrolls down.
         */
        HBox position = new HBox(0);
        position.setAlignment(Pos.CENTER);
        position.setMaxWidth(Region.USE_PREF_SIZE);
        position.setStyle("-fx-padding: 8 6 8 6;" + Palette.block(Palette.PANEL));

        VBox lastCell = limitCell("THEY BUY AT", String.format("%s%.2f", here, inside * 1000),
                String.format("per sq ft inside  ·  %s%s%.2f margin",
                        margin < 0 ? "-" : "+", here, Math.abs(margin) * 1000),
                margin < 0 ? Palette.BAD : Palette.GOOD);
        lastCell.setStyle("-fx-padding: 0 14 0 14;");

        position.getChildren().addAll(
                limitCell("FREE", shortNumber(free) + " sq ft",
                        LandManager.km2Words(free) + " to build on",
                        free <= 0 ? Palette.BAD : Palette.TEXT_HEAD),
                limitCell("USED", String.format("%.0f%%", used * 100),
                        used >= .90 ? "businesses will stop building"
                                    : "of everything the city owns",
                        used >= .90 ? Palette.BAD : used >= .75 ? Palette.WARN : Palette.GOOD),
                limitCell("MARKET RATE", String.format("US$%.2f", outsideUsd * 1000),
                        String.format("per sq ft outside  ·  %s%.2f today", here, outside * 1000),
                        Palette.TEXT_HEAD),
                lastCell);

        /* ========================= WHAT IS ON OFFER ========================= */
        Label offering = new Label("ON OFFER");
        offering.setStyle(Palette.words(Palette.SIZE_HEADING, Palette.ACCENT)
                + " -fx-font-weight: bold; -fx-padding: 14 0 0 0;");

        Label smallest = new Label(String.format(
                "Nine plots, cheapest ground first \u2014 the top-left card is always the "
                + "best value per square foot. The office will not split a lot smaller "
                + "than %s, and stops splitting them at all as the city grows.",
                LandManager.km2Words(market.getMinSqFt())));
        smallest.setWrapText(true);
        smallest.setMaxWidth(TILE_WIDTH * 3 + TILE_GAP * 2);
        smallest.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED)
                + " -fx-padding: 0 0 6 0;");

        LandParcel richest = market.richestDeposit();

        javafx.scene.layout.FlowPane plots =
                new javafx.scene.layout.FlowPane(TILE_GAP, TILE_GAP);
        plots.setAlignment(Pos.CENTER);
        /*
         * THREE ACROSS, CAPPED, so nine plots are a square rather than a
         * ribbon. The bind to the scroller is still there underneath it: on a
         * window too narrow for three tiles the row wraps at two and the grid
         * degrades to 2-2-2-2-1 rather than running off the edge.
         */
        final double THREE = TILE_WIDTH * 3 + TILE_GAP * 2 + 2;
        plots.prefWrapLengthProperty().bind(javafx.beans.binding.Bindings.min(
                ui.menuScroller.widthProperty().subtract(TILE_GAP * 4), THREE));
        plots.maxWidthProperty().bind(javafx.beans.binding.Bindings.min(
                ui.menuScroller.widthProperty().subtract(TILE_GAP * 4), THREE));

        /*
         * COMPARED AGAINST THE LISTING, not against the office's quoted rate.
         *
         * The obvious baseline was LandManager.getAcquisitionCostPerSqFt() -
         * what the market says ground costs outside the city. It does not work:
         * on a played save that figure reads $0.70 a square foot while every
         * plot actually on the shelf is priced between $7 and $28, so every card
         * came out "3260% over the office", which is not a verdict, it is
         * noise. A parcel's price is frozen at the moment it is listed and the
         * market rate has moved since; either way, comparing today's plots to a
         * number they were not priced from tells the player nothing.
         *
         * The going rate ON THIS LISTING does work, because the nine plots were
         * all priced the same way and the question a player actually has is
         * "which of these nine". The median rather than the mean, so one enormous
         * ore-bearing plot cannot drag the line it is being judged against.
         */
        double[] rates = new double[market.getListing().size()];
        int at = 0;
        for (LandParcel parcel : market.getListing()) rates[at++] = parcel.getUsdPerSqFt();
        java.util.Arrays.sort(rates);
        double going = rates.length == 0 ? 0 : rates[rates.length / 2];

        /*
         * SORTED, CHEAPEST GROUND FIRST, so the best buy is always the top-left
         * card. Jerus: "sorted by best value so the best one is always at the
         * top left".
         *
         * Price per square foot ascending, which is the only ranking that means
         * anything across plots of different sizes - a big plot is not a better
         * deal for being big. It is also why the listing's own order was worth
         * throwing away: that was the order the office happened to generate
         * them in, which is no order at all.
         *
         * ORE IS A DIFFERENT QUESTION and is deliberately not folded into the
         * ranking. A deposit carries a price premium, so on ground value alone
         * it looks dear; whether the premium is worth paying depends on whether
         * the city wants a mine, which no single number can decide. It keeps
         * its own badge instead.
         *
         * AND THE "JUST BUY THE CHEAPEST" BUTTON IS GONE. Jerus: "remove the
         * buy cheapest button since best value is always better and two is
         * confusing." He is right, and it was worse than redundant - cheapest
         * by PRICE is whichever plot is smallest, which is the one piece of
         * ground least worth owning per dollar. The top-left card is the answer
         * that button was pretending to be.
         *
         * THE ORDER IS THE MODEL'S since 0.7.13 (Game.landShelf()), because
         * "Buy the next N plots" buys the first N of it and the cards must be
         * those N.
         */
        java.util.List<LandParcel> shelf = ui.game.landShelf();

        for (int i = 0; i < shelf.size(); i++) {
            LandParcel parcel = shelf.get(i);
            plots.getChildren().add(parcelTile(parcel, going, i == 0,
                    richest != null && richest.getId() == parcel.getId()));
        }

        /* ===================== AND HOW THE MARGIN WORKS =====================
         *
         * Still a statement, because that is what it is - the mechanism, read
         * once, rather than a decision made every visit. It moved below the
         * plots for exactly that reason, and it is built from statementLine
         * rather than from padded strings: see the note on the statement
         * primitives above.
         */
        VBox column = new VBox(0);
        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(Region.USE_PREF_SIZE);

        column.getChildren().add(statementHead("What the city makes on it"));
        column.getChildren().add(statementLine("Outside, you buy at",
                String.format("US$%.2f /sq ft  ·  %s%.2f today", outsideUsd * 1000,
                        here, outside * 1000)));
        column.getChildren().add(statementNote(
                "Rises with the city — with the land owned, and with the people. Asked in US"
                + " dollars, so a weaker currency makes it dearer here and a stronger one cheaper."));
        column.getChildren().add(statementLine("Inside, they buy at",
                String.format("%s%.2f /sq ft", here, inside * 1000)));
        column.getChildren().add(statementNote(
                "Supply against demand — the more land standing free, the cheaper. Local"
                + " money, and the exchange rate does not reach it."));
        column.getChildren().add(statementTotal("Margin",
                String.format("%s%s%.2f /sq ft", margin < 0 ? "-" : "+", here,
                        Math.abs(margin) * 1000),
                margin < 0 ? Palette.BAD : Palette.GOOD));
        column.getChildren().add(statementNote(margin < 0
                ? "You are selling ground for less than it would cost you today - more of it"
                        + " stands free than anyone wants to build on, or the currency has made"
                        + " the world's price dear."
                : "Land is tight enough that the city profits on every sale."));

        /* WHAT THE PRICE COMES TO, since "per square foot" is not a number
         * anybody can price a decision from. */
        BuildingsTemplate house = ui.game.getBuildingManager().getTemplateByName("House");
        BuildingsTemplate plant = ui.game.getBuildingManager()
                .getTemplateByName("Bakery");

        column.getChildren().add(statementHead("What that comes to"));
        if (house != null) {
            column.getChildren().add(statementLine("A house plot",
                    marked(here, money(land.priceFor(house.getLandSqFt())))
                    + "  on " + marked(here, money(house.getCashCost())) + " to build"));
        }
        if (plant != null) {
            column.getChildren().add(statementLine("A food plant's plot",
                    marked(here, money(land.priceFor(plant.getLandSqFt())))
                    + "  on " + marked(here, money(plant.getCashCost())) + " to build"));
        }
        column.getChildren().add(statementNote(
                "The ground is charged on top of the build, so a cheap building on"
                + " expensive land is not a cheap building."));

        /* ------------------- WHAT IS UNDER THE GROUND ------------------- */
        column.getChildren().add(statementHead("Ore"));
        column.getChildren().add(statementLine("Deposits owned",
                formatter.format(land.getIronDeposits()),
                land.getIronDeposits() > 0 ? Palette.TEXT_BODY : Palette.TEXT_LABEL));
        // Rounded: a hundred and fifty million tonnes does not have eighteen
        // hundredths of a tonne in it, and the decimals were the widest thing
        // on the statement.
        column.getChildren().add(statementLine("Still in the ground",
                formatter.format(Math.round(land.getIronReserveTonnes())) + " tonnes"));
        column.getChildren().add(statementLine("Mines standing on them",
                formatter.format(ui.game.minesCommitted()),
                ui.game.minesCommitted() < land.getIronDeposits() ? Palette.WARN : null));
        if (ui.game.minesCommitted() < land.getIronDeposits()) {
            column.getChildren().add(statementNote(
                    "There is ore under this city that nothing is digging."));
        }

        /* ------------------- WHO IS WAITING ON LAND ------------------- */
        column.getChildren().add(statementHead("Who is waiting"));

        boolean anyone = false;
        for (String sector : Sectors.KEYS) {
            String last = ui.game.getLastInvestment(sector);
            if (last != null && last.contains("no land")) {
                column.getChildren().add(statementLine(sector,
                        "waiting on ground", Palette.BAD));
                anyone = true;
            }
        }
        if (!anyone) {
            column.getChildren().add(statementLine("Every sector",
                    "has room to build", Palette.GOOD));
        }
        column.getChildren().add(statementNote(
                "As of last month — the sectors decide once a month, so ground bought"
                + " now shows up here next month."));

        VBox all = new VBox(0, offering, smallest, nextPlots(), plots, column);
        all.setAlignment(Pos.CENTER);

        ui.rootMenu.getChildren().addAll(title, paying, position, ui.scrolled(all));
    }

    /* ====================== BUY THE NEXT N PLOTS (0.7.13) ======================
     *
     * Jerus: "add a button to buy multiple, so for example buy the next 5
     * land options, and then also debt popup appears if not enough". One
     * control above the cards: the first N of them, in the office's order
     * (Game.nextLandParcels()), their price together in the money the toggle
     * pays in with the other money beside it, and a button that buys them
     * each as its own card's button would (Game.buyLandParcels()) - or, short,
     * opens the same funding page, sized to the whole gap. N starts at five
     * and runs from one to what is listed.
     */

    /** How many plots the next-N control buys; kept across redraws, held inside 1..listed. */
    int nextCount = 5;

    HBox nextPlots() {
        int listed = ui.game.landShelf().size();
        nextCount = Math.max(1, Math.min(nextCount, Math.max(1, listed)));
        java.util.List<Integer> ids = ui.game.nextLandParcels(nextCount);
        boolean vaultPays = ui.game.isLandPaidFromVault();
        String here = ui.game.getCurrency().qualifiedSymbol();
        double usdTotal = ui.game.landPriceUsd(ids);
        double localTotal = ui.game.landPriceLocal(ids);
        boolean afford = ui.game.canAffordLandParcels(ids);
        boolean funding = ui.game.landNeedsFunding(ids);

        Button buy = new Button("Buy the next " + nextCount + (nextCount == 1 ? " plot" : " plots"));
        buy.setDisable(ids.isEmpty());
        buy.setStyle(Palette.words(Palette.SIZE_LABEL, "white")
                + " -fx-background-color: " + (funding ? Palette.CONTROL : Palette.CONFIRM) + ";");
        buy.setOnAction(e -> buyOrFund(ids));

        Button fewer = new Button("−");
        fewer.setDisable(nextCount <= 1);
        fewer.setOnAction(e -> { nextCount--; showLandMenu(); });
        Button more = new Button("+");
        more.setDisable(nextCount >= listed);
        more.setOnAction(e -> { nextCount++; showLandMenu(); });

        Label total = new Label(vaultPays ? usd(usdTotal) : marked(here, money(localTotal)));
        total.setStyle(Palette.figure(Palette.SIZE_SECTION, afford ? Palette.GOOD : Palette.BAD)
                + " -fx-padding: 0 0 0 10;");
        Label other = new Label("  ·  " + (vaultPays
                ? marked(here, money(localTotal)) + " at today's rate"
                : usd(usdTotal) + " listed"));
        other.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));

        HBox row = new HBox(6, buy, fewer, more, total, other);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(TILE_WIDTH * 3 + TILE_GAP * 2);
        row.setStyle("-fx-padding: 0 0 8 0;");
        return row;
    }

    /** Buys these plots the way the toggle pays, or - the city short - opens the funding page for them. */
    void buyOrFund(java.util.List<Integer> ids) {
        if (ui.game.landNeedsFunding(ids)) {
            showLandFunding(ids);
            return;
        }
        ui.game.buyLandParcels(ids);
        showLandMenu();
    }

    /* ====================== WHEN THE CITY IS SHORT (0.7.13) ======================
     *
     * Jerus: "if you are on buy by converting and you dont have enough, you
     * can stilll click buy, just the popup to issue debt appears, but if you
     * are in buy with reserves, and click buy, then pop up to issue foreign
     * debt should appear (aka the short or the 20y, like with buildings)".
     * The build screen's INSUFFICIENT FUNDS page, its pieces reused
     * (BuildScreen.fundingOffer(), rateStyle()), sized to the gap in the
     * money the toggle pays in - every figure the model's (Game, WHEN THE
     * CITY IS SHORT, AND SEVERAL AT ONCE):
     *   - converting, the build screen's two offers in local money:
     *     Game.BUILD_BOND_YEARS' bond and Game.BUILD_NOTE_MONTHS' note;
     *   - from the vault, the same two terms in dollars on the world's
     *     curve, issued abroad and held in reserve - a six-month dollar note
     *     is a term the treasury already sells (the Finances tab's notes run
     *     three to twelve months, at home and abroad) - and, when the cash
     *     covers it, the vault's dollars with the rest converted: a choice,
     *     never the default. With the window abroad shut, it says why and
     *     offers what remains.
     * Cancel leaves everything as it was. Either offer books exactly the
     * quote above it and then buys the plots.
     */
    void showLandFunding(java.util.List<Integer> ids) {
        ui.clearMenu("showLandFunding", () -> showLandFunding(ids));
        Game game = ui.game;
        boolean vaultPays = game.isLandPaidFromVault();
        String here = game.getCurrency().qualifiedSymbol();
        String what = ids.size() == 1 ? "The plot" : ids.size() + " plots";

        Label warning = new Label("INSUFFICIENT FUNDS");
        warning.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        VBox column = new VBox(0);
        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(Region.USE_PREF_SIZE);

        if (!vaultPays) {
            double gap = game.landCashGap(ids);
            column.getChildren().addAll(
                    statementLine("Funding required", marked(here, money(gap)), Palette.BAD),
                    statementNote(String.format("%s cost%s %s at today's rate, and the treasury holds %s.",
                            what, ids.size() == 1 ? "s" : "", marked(here, money(game.landPriceLocal(ids))),
                            marked(here, money(game.getCash())))));
            DebtQuote bond = game.quoteLongBondForCash(gap, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);
            DebtQuote note = game.quoteTBill(gap, Game.BUILD_NOTE_MONTHS, Game.BUILD_NOTE_GRANULE);
            column.getChildren().add(ui.buildScreen.fundingOffer(Game.BUILD_BOND_YEARS + "-year bond", bond,
                    pct2(bond.marketRate()) + " yield  ·  " + pct2(bond.couponRate()) + " coupon",
                    String.format("Paid over %d years: the coupon every month, then the whole %s at the end.",
                            bond.duration(), money(bond.faceValue())),
                    "Issue the " + Game.BUILD_BOND_YEARS + "-year bond and buy",
                    () -> {
                        game.handleLongBondForCash(gap, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);
                        buyOnTheLoan(ids, "bond");
                    }));
            column.getChildren().add(ui.buildScreen.fundingOffer(Game.BUILD_NOTE_MONTHS + "-month note", note,
                    pct2(note.marketRate()) + " a year, taken as a discount",
                    String.format(game.getRollover().getMode() == Rollover.Mode.MANUAL
                                    ? "Falls due in %d months: the whole %s at once, out of the treasury."
                                    : "Falls due in %d months: the whole %s at once, refinanced then by the treasury's rollover.",
                            note.duration(), money(note.faceValue())),
                    "Issue the " + Game.BUILD_NOTE_MONTHS + "-month note and buy",
                    () -> {
                        game.handleTBillLogic(gap, Game.BUILD_NOTE_MONTHS, Game.BUILD_NOTE_GRANULE);
                        buyOnTheLoan(ids, "note");
                    }));
        } else {
            double gapUsd = game.landVaultGapUsd(ids);
            column.getChildren().addAll(
                    statementLine("Dollars required", usd(gapUsd), Palette.BAD),
                    statementNote(String.format("%s cost%s %s, and the vault holds %s.",
                            what, ids.size() == 1 ? "s" : "", usd(game.landPriceUsd(ids)),
                            usd(game.getForeignAccounts().getReservesUsd()))));
            if (game.foreignWindowOpen()) {
                DebtQuote bond = game.quoteForeignForCash("Term", gapUsd, Game.BUILD_BOND_YEARS,
                        Game.BUILD_BOND_GRANULE);
                DebtQuote note = game.quoteForeignForCash("Note", gapUsd, Game.BUILD_NOTE_MONTHS,
                        Game.BUILD_NOTE_GRANULE);
                column.getChildren().add(ui.buildScreen.fundingOffer(
                        Game.BUILD_BOND_YEARS + "-year dollar bond", bond,
                        pct2(bond.marketRate()) + " yield  ·  " + pct2(bond.couponRate()) + " coupon",
                        String.format("Paid over %d years in dollars: the coupon every month, then the whole %s"
                                + " at the end.", bond.duration(), usd(bond.faceValue())),
                        "Issue it abroad and buy from the vault",
                        () -> {
                            game.handleForeignForCash("Term", gapUsd, Game.BUILD_BOND_YEARS,
                                    Game.BUILD_BOND_GRANULE, true);
                            buyOnTheLoan(ids, "dollar bond");
                        }, Money::usd));
                column.getChildren().add(ui.buildScreen.fundingOffer(
                        Game.BUILD_NOTE_MONTHS + "-month dollar note", note,
                        pct2(note.marketRate()) + " a year, taken as a discount",
                        String.format("Falls due in %d months: the whole %s at once, in dollars.",
                                note.duration(), usd(note.faceValue())),
                        "Issue it abroad and buy from the vault",
                        () -> {
                            game.handleForeignForCash("Note", gapUsd, Game.BUILD_NOTE_MONTHS,
                                    Game.BUILD_NOTE_GRANULE, true);
                            buyOnTheLoan(ids, "dollar note");
                        }, Money::usd));
                column.getChildren().add(statementNote("Issued abroad; the dollars are in reserve, and the "
                        + "vault pays for the land. On the world's curve at each term: a dollar owed is "
                        + "owed in dollars, whatever the currency does."));
            } else {
                column.getChildren().add(alert("Nobody abroad will lend the dollars",
                        game.foreignWindowReason() + ". Only what the city already has can pay."));
            }

            /* ...and the third way, a choice and never the default. */
            if (game.landTopUpCovers(ids)) {
                Button topUp = new Button("Take what the vault has and convert the rest");
                topUp.setStyle(Palette.words(Palette.SIZE_LABEL, "white")
                        + " -fx-background-color: " + Palette.CONFIRM + ";");
                topUp.setOnAction(e -> buyOnTheLoan(ids, null));
                column.getChildren().addAll(
                        statementHead("Or the vault's dollars, and the rest converted from cash"),
                        statementLine("From the vault", usd(game.getForeignAccounts().getReservesUsd())),
                        statementLine("Converted from cash", usd(game.landVaultGapUsd(ids)) + "  ·  "
                                + marked(here, money(game.landTopUpLocal(ids))) + " at today's rate"),
                        statementNote("No debt: the vault is emptied and the treasury buys the rest of the "
                                + "dollars out of its cash."),
                        topUp);
            } else if (!game.foreignWindowOpen()) {
                column.getChildren().add(sentence("The cash does not cover the rest either. Paying by "
                        + "converting cash borrows at home for it.", Palette.TEXT_MUTED));
            }
        }

        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> showLandMenu());
        ui.rootMenu.getChildren().addAll(warning, ui.scrolled(column), cancel);
    }

    /**
     * The money is in: the plots are bought, each as its own button would
     * buy it, and whatever the purchase answers is shown - the build
     * screen's rule that a refusal nobody reads is a silent one
     * (BuildScreen.buildOnTheLoan()). paper is the offer taken, or null for
     * the vault's dollars with the rest converted.
     */
    private void buyOnTheLoan(java.util.List<Integer> ids, String paper) {
        int bought = ui.game.buyLandParcels(ids);
        if (bought >= ids.size()) {
            showLandMenu();
            return;
        }
        ui.clearMenu("showLandFellShort", () -> showLandMenu());
        Label heading = new Label(paper == null ? "THE LAND IS NOT BOUGHT" : "THE MONEY IS IN, THE LAND IS NOT");
        heading.setStyle("-fx-text-fill: #ff6b6b; -fx-font-weight: bold; -fx-font-size: 14px;");
        Label why = new Label(String.format("%s%d of %d plot%s bought: the rest still cost more than the city "
                        + "holds. Nothing beyond %s was spent.",
                paper == null ? "" : "The " + paper + " was issued. ",
                bought, ids.size(), ids.size() == 1 ? "" : "s",
                paper == null ? "the plots bought" : "the " + paper + " and the plots bought"));
        why.setWrapText(true);
        why.setMaxWidth(460);
        Button back = new Button("Back to the land office");
        back.setOnAction(e -> showLandMenu());
        ui.rootMenu.getChildren().addAll(heading, why, back);
    }

    /* =====================================================================
       THE STATEMENT.

       The second visual language of the three, and the one that earns its
       place: the accounting screens in this game ARE statements, and should
       look like statements. What they should not look like is a terminal.

       WHAT THIS REPLACES. reportSection() lines up its columns by padding a
       String to twenty-four characters and rules them off with rows of "-", and
       its headings are wrapped in "===". That is exactly right for a console
       and it has three costs on a screen: the alignment breaks the moment a
       label is twenty-five characters long, a label and its figure are one
       string so they cannot be coloured separately - which forfeits the one
       rule the whole palette is built on - and it reads as a printout rather
       than as a screen.

       These four do the same job with layout instead of spaces: the label is
       grey on the left, the figure is Courier on the right, the gap between
       them is a Region rather than padding, and a rule is a one-pixel Region
       rather than sixty hyphens. Built here because Land needed them; they are
       the primitives every remaining report screen will be rebuilt on.
       Since 2026-09-18 the four live in Statement.java, beside the two-column
       book and the disclosures; what is left here is the land office's own tile.
       ===================================================================== */

    /**
     * One plot on the market.
     *
     * THE PRICE PER SQUARE FOOT IS THE POINT, and it was the one figure the old
     * listing did not carry. A plot's total price says nothing on its own - a
     * big expensive plot and a small cheap one are the same deal - so comparing
     * ten of them meant ten divisions done in the player's head. Against what
     * the office charges OUTSIDE, it becomes a verdict: this one is a bargain,
     * that one is not.
     *
     * The two badges come from the market itself rather than from arithmetic
     * here. LandMarket has known which plot was the best value and which had the
     * most ore under it since it was written; nothing had ever asked it.
     */
    StackPane parcelTile(LandParcel parcel, double going,
                                 boolean best, boolean richest) {

        // Affordable the way the toggle pays: the model's answer, not arithmetic here.
        boolean afford = ui.game.canAffordParcel(parcel);
        double perSqFt = parcel.getUsdPerSqFt();
        double against = going > 0 ? (going - perSqFt) / going * 100 : 0;

        Label size = new Label(formatter.format(parcel.getSizeSqFt()) + " sq ft");
        size.setStyle(Palette.words(Palette.SIZE_HEADING,
                afford ? Palette.TEXT_HEAD : Palette.TEXT_FAINT) + " -fx-font-weight: bold;");

        // Its area in square kilometres (0.7.13), in place of blocks: Jerus,
        // "purely for visual purposes, instead of blocks, say km^2" - the
        // model's square feet converted exactly, to three significant figures
        // (LandManager.km2Words()).
        Label area = new Label(LandManager.km2Words(parcel.getSizeSqFt()));
        area.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));

        /*
         * BOTH PRICES (0.7.6), THE ONE THE TOGGLE PAYS IN LARGE (0.7.13).
         * Jerus: "if you are on convert currency to usd to buy, even tho you
         * pay usd, the land should show your own currency cost, and if you
         * have it on use reserves then the cost is shown is usd". Converting,
         * what it costs here at today's rate is large and the dollars it is
         * listed at the caption - "D$1.8M · US$1.2M listed"; from the vault,
         * the other way up - "US$1.2M · D$1.8M at today's rate".
         */
        boolean vaultPays = ui.game.isLandPaidFromVault();
        String here = ui.game.getCurrency().qualifiedSymbol();
        String listedUsd = usd(parcel.getPriceUsd());
        String todayHere = marked(here, money(parcel.localPrice(ui.game.getForeignAccounts().getRate())));
        Label price = new Label(vaultPays ? listedUsd : todayHere);
        price.setStyle(Palette.figure(Palette.SIZE_SECTION,
                afford ? Palette.GOOD : Palette.BAD));
        Label local = new Label("  ·  " + (vaultPays ? todayHere + " at today's rate" : listedUsd + " listed"));
        local.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        HBox prices = new HBox(0, price, local);
        prices.setAlignment(Pos.BASELINE_LEFT);

        Label rate = new Label(Math.abs(against) < 1
                ? String.format("US$%.2f/sq ft  ·  the going rate", perSqFt * 1000)
                : String.format("US$%.2f/sq ft  ·  %.0f%% %s the going rate",
                        perSqFt * 1000, Math.abs(against), against > 0 ? "under" : "over"));
        rate.setStyle(Palette.words(Palette.SIZE_CAPTION,
                against >= 0 ? Palette.GOOD : Palette.WARN));

        VBox face = new VBox(1, size, area, prices, rate);
        face.setStyle("-fx-padding: 8 10 8 10;");

        if (parcel.hasIron()) {
            Label ore = new Label(parcel.getDeposits() > 1
                    ? String.format("IRON ×%d  ·  %,.0fk tonnes",
                            parcel.getDeposits(), parcel.getIronTonnes() / 1000)
                    : String.format("IRON  ·  %,.0fk tonnes", parcel.getIronTonnes() / 1000));
            ore.setStyle(Palette.figure(Palette.SIZE_CAPTION, "#ce93d8")
                    + " -fx-padding: 4 0 0 0;");
            face.getChildren().add(ore);
        }

        // What a plot IS, in the only unit that matters on a build screen.
        BuildingsTemplate house = ui.game.getBuildingManager().getTemplateByName("House");
        if (house != null && house.getLandSqFt() > 0) {
            Label room = new Label(String.format("room for %s houses",
                    formatter.format(Math.floor(parcel.getSizeSqFt() / house.getLandSqFt()))));
            room.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            face.getChildren().add(room);
        }

        Region push = new Region();
        VBox.setVgrow(push, Priority.ALWAYS);
        face.getChildren().add(push);

        /*
         * THE BUTTON STAYS CLICKABLE (0.7.13). Short, it opens the funding
         * page (showLandFunding()) rather than greying out - the build
         * screen's way. From the vault, a vault short of the dollars opens it
         * too, even with the cash to convert the rest: that way is one of the
         * page's choices now, not what the button does by itself.
         */
        java.util.List<Integer> just = java.util.List.of(parcel.getId());
        boolean funding = ui.game.landNeedsFunding(just);
        Button buy = new Button(!funding ? "Buy this plot"
                : vaultPays ? "Buy — the vault is short" : "Buy — borrow for it");
        buy.setMaxWidth(Double.MAX_VALUE);
        buy.setStyle(Palette.words(Palette.SIZE_LABEL, "white")
                + " -fx-background-color: " + (funding ? Palette.CONTROL : Palette.CONFIRM) + ";");
        buy.setOnAction(e -> buyOrFund(just));
        face.getChildren().add(buy);

        StackPane tile = new StackPane(face);
        tile.setPrefSize(TILE_WIDTH, 172);
        tile.setMinSize(TILE_WIDTH, 172);
        tile.setMaxSize(TILE_WIDTH, 172);
        tile.setStyle(Palette.block(Palette.CONTROL,
                best ? Palette.GOOD : richest ? "#ce93d8" : Palette.HAIRLINE));

        if (best || richest) {
            Label flag = new Label(best ? "BEST VALUE" : "MOST ORE");
            flag.setStyle(Palette.words(Palette.SIZE_CAPTION, "#101820")
                    + " -fx-font-weight: bold; -fx-padding: 1 5 1 5;"
                    + " -fx-background-color: " + (best ? Palette.GOOD : "#ce93d8") + ";"
                    + " -fx-background-radius: 0 " + Palette.RADIUS + " 0 " + Palette.RADIUS + ";");
            flag.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            StackPane.setAlignment(flag, Pos.TOP_RIGHT);
            tile.getChildren().add(flag);
        }
        return tile;
    }
}
