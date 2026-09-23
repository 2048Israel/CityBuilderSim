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
 * Split out of UserInterface on 2026-09-18: the two banners THE LAND OFFICE
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
        String here = Currency.QUALIFIED;

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
                        + "dearer than that takes all of it and converts the rest from cash.",
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
                        String.format("%.1f blocks to build on", land.getAvailableBlocks()),
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
                + "than %.0f block%s, and stops splitting them at all as the city grows.",
                market.getMinBlocks(), market.getMinBlocks() == 1 ? "" : "s"));
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
         */
        java.util.List<LandParcel> shelf = market.getListing();
        shelf.sort(java.util.Comparator.comparingDouble(LandParcel::getUsdPerSqFt));

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

        VBox all = new VBox(0, offering, smallest, plots, column);
        all.setAlignment(Pos.CENTER);

        ui.rootMenu.getChildren().addAll(title, paying, position, ui.scrolled(all));
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

        Label blocks = new Label(String.format("%.1f blocks", parcel.getBlocks()));
        blocks.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));

        /*
         * BOTH PRICES (0.7.6): the dollars it is listed at, which do not move,
         * and what they cost here at today's rate, which does - "US$1.2M ·
         * D$1.8M at today's rate".
         */
        Label price = new Label(usd(parcel.getPriceUsd()));
        price.setStyle(Palette.figure(Palette.SIZE_SECTION,
                afford ? Palette.GOOD : Palette.BAD));
        Label local = new Label("  ·  " + marked(Currency.QUALIFIED,
                money(parcel.localPrice(ui.game.getForeignAccounts().getRate())))
                + " at today's rate");
        local.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        HBox prices = new HBox(0, price, local);
        prices.setAlignment(Pos.BASELINE_LEFT);

        Label rate = new Label(Math.abs(against) < 1
                ? String.format("US$%.2f/sq ft  ·  the going rate", perSqFt * 1000)
                : String.format("US$%.2f/sq ft  ·  %.0f%% %s the going rate",
                        perSqFt * 1000, Math.abs(against), against > 0 ? "under" : "over"));
        rate.setStyle(Palette.words(Palette.SIZE_CAPTION,
                against >= 0 ? Palette.GOOD : Palette.WARN));

        VBox face = new VBox(1, size, blocks, prices, rate);
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

        Button buy = new Button(afford ? "Buy this plot"
                : ui.game.isLandPaidFromVault() ? "Not enough in the vault or cash" : "Not enough cash");
        buy.setDisable(!afford);
        buy.setMaxWidth(Double.MAX_VALUE);
        buy.setStyle(Palette.words(Palette.SIZE_LABEL, "white")
                + " -fx-background-color: " + (afford ? Palette.CONFIRM : Palette.CONTROL) + ";");
        buy.setOnAction(e -> {
            ui.game.buyLandParcel(parcel.getId());
            showLandMenu();
        });
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
