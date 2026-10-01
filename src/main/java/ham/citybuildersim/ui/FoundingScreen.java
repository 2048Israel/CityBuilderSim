package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Statement.*;
import static ham.citybuildersim.ui.Pieces.*;

/**
 * Found a city: its name, its money, what the founders leave in the treasury and the vault, and the world it is founded into.
 *
 * WHY THIS EXISTS (0.7.10). "Start New Game" founded the same city every time
 * - Danzik, the Danzik dollar, D$2.5B and US$1B - and the one founding choice
 * there was, the world's inflation, sat on Settings, where it did nothing
 * until the next city. Jerus: "a small thing at the start of the game where
 * you choose the name of the city and the currency ... and perhaps the
 * settings to choose the starting cash and usd cash and offworld inflation
 * rate ... (with option to just start at default)". So Start New Game - New
 * city since 0.7.21 - comes here, and game.newGame() runs only when the
 * player founds the city.
 *
 * EVERY FIGURE IS THE MODEL'S. The currency's name, code and symbols are
 * Currency.fromCityName() and Currency.typed(); the presets and the bounds on
 * a custom founding are Founding's; the world's settled level is
 * WorldEconomy.settledLevelAt(); and whether the city can be founded, and if
 * not why, is Founding.problem(). The screen holds the player's half-made
 * choices between redraws and nothing else.
 *
 * AND IT NO LONGER SAYS WHAT THE MONEY BUYS (0.7.20). It listed "...the
 * founding village" and its houses, "...and what is left", and the first
 * works "in cash" or "a bond for" (Game.whatItBuys()) - and the village is
 * the playtest's, placed by hand before its advisor takes over, which the
 * game never builds for a player: Jerus's play-through found a treasury of
 * $100.0M and one house. Jerus: "even the screen shouldnt say what the money
 * could buy, the start screen should be real simple". Each choice shows its
 * treasury and its vault, and nothing about spending them.
 * Founding.whatItBuys() stays in the model: the harnesses use it
 * (NewGameCheck, FundCheck, ReadPathCheck, LongPlaytest). So does
 * Game.dayZeroQuotes(), which only this screen called and nothing calls now.
 *
 * REAL SIMPLE (0.7.21). Jerus: "the start screen should be real simple". The
 * page is the mockups' (Found.dc.html): one panel in the middle of the window
 * over the main menu's backdrop, dimmed - the name, one line for its money,
 * four cards for what it starts with, a row of five for the world, and Found
 * and Back. Each card says how hard the start is, never what the money
 * buys; the currency's fields and Custom's open in place from a link; the
 * long explanations sit behind an (i) or a tooltip. "Found with defaults"
 * went, because the defaults are what the page opens on. It fits a window
 * 768 pixels high and scrolls in a smaller one.
 *
 * TYPING DOES NOT REDRAW THE PAGE. The page is rebuilt when a card, a link or
 * a choice of world changes it; a keystroke only refreshes the lines that
 * depend on it - the currency, and whether Found is lit and why not - so the
 * field being typed in keeps its focus and its caret.
 */
final class FoundingScreen {

    /** This screen's name for clearMenu(), the key filter and isGameMenu(). */
    static final String SCREEN = "showFoundingScreen";

    /** The window this screen draws into: its game, its root, its clearMenu(). */
    private final UserInterface ui;

    FoundingScreen(UserInterface ui) { this.ui = ui; }

    /* ------------------------------------------------ the choices, between redraws */

    private String cityName = Founding.DEFAULT_CITY_NAME;
    private boolean ownCurrency;
    private String typedName = "", typedCode = "";
    private Founding.Preset preset = Founding.Preset.STANDARD;
    /** A custom founding, as typed: millions of the city's money, and millions of US dollars. */
    private String customCash = "", customUsd = "";
    private double mean = WorldEconomy.DEFAULT_MEAN_INFLATION;

    /* ------------------------------------------------ what a keystroke refreshes */

    private Label currencyLine, whyNot;
    private Button found;
    private TextField nameField;

    /** From the main menu: a fresh page on the defaults, whatever was typed last time. */
    void show() {
        cityName = Founding.DEFAULT_CITY_NAME;
        ownCurrency = false;
        typedName = "";
        typedCode = "";
        preset = Founding.Preset.STANDARD;
        // Custom opens on the Standard figures, in millions, for the player to move.
        customCash = trim(Founding.Preset.STANDARD.cash() / 1000);
        customUsd = trim(Founding.Preset.STANDARD.reserveUsd() / 1000);
        mean = WorldEconomy.DEFAULT_MEAN_INFLATION;
        draw();
        // The name is where a player's hands go first; Enter founds from it.
        if (nameField != null) {
            nameField.requestFocus();
            nameField.selectAll();
        }
    }

    /** The panel's width, as the mockups draw it. */
    static final double PANEL = 760;

    /** The panel's padding, left and right: what the cards and fields share is the rest. */
    static final double PANEL_PAD = 40;

    /* =====================================================================
       THE PAGE
       ===================================================================== */
    void draw() {
        ui.clearMenu(SCREEN, this::draw);

        Label title = new Label("Found a city");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + Palette.TEXT_HEAD + ";");

        /* ----------------------------- the name ----------------------------- */
        nameField = new TextField(cityName);
        nameField.setPromptText("Name the city");
        nameField.setPrefHeight(46);
        nameField.setStyle("-fx-font-size: 18px; -fx-background-color: " + Palette.FIELD + ";"
                + " -fx-border-color: " + Palette.EDGE + "; -fx-background-radius: 8; -fx-border-radius: 8;"
                + " -fx-padding: 0 14 0 14; -fx-text-fill: " + Palette.TEXT_HEAD + ";");
        nameField.textProperty().addListener((o, was, now) -> { cityName = now; refresh(); });
        nameField.setTooltip(new Tooltip(String.format("Up to %d characters. It names the window, the save"
                + " slot and - unless you name it yourself - the city's money.", Founding.MAX_CITY_NAME_LENGTH)));

        currencyLine = new Label("");
        currencyLine.setStyle(Palette.words(Palette.SIZE_SECTION - 1, Palette.TEXT_MUTED));
        Tooltip.install(currencyLine, new Tooltip(ownCurrency
                ? String.format("A name, and a code of exactly %d letters A to Z - %s is the world's money,"
                        + " not the city's.", Currency.CODE_LENGTH, Currency.FOREIGN_CODE)
                : "Named after the city: its first three letters are the code, and its initial and $"
                        + " mark it wherever a US dollar is beside it."));
        Hyperlink own = new Hyperlink(ownCurrency ? "name it after the city" : "name it yourself");
        own.setStyle(Palette.words(Palette.SIZE_SECTION - 1, Palette.ACCENT));
        own.setOnAction(e -> { ownCurrency = !ownCurrency; draw(); });
        HBox moneyRow = new HBox(0, currencyLine, own);
        moneyRow.setAlignment(Pos.BASELINE_LEFT);

        VBox nameBlock = new VBox(6, caption("ITS NAME"), nameField, moneyRow);
        if (ownCurrency) {
            // Today's currency fields, opened in place by the link.
            TextField money = new TextField(typedName);
            money.setPromptText("What it is called - crown, mark, thaler");
            money.setPrefWidth(PANEL - 2 * PANEL_PAD - 90 - Palette.GAP);
            money.textProperty().addListener((o, was, now) -> { typedName = now; refresh(); });
            TextField code = new TextField(typedCode);
            code.setPromptText("ABC");
            code.setPrefWidth(90);
            code.textProperty().addListener((o, was, now) -> { typedCode = now; refresh(); });
            HBox pair = new HBox(Palette.GAP, money, code);
            pair.setAlignment(Pos.CENTER_LEFT);
            Label note = new Label(String.format(
                    "A name, and a code of exactly %d letters A to Z - %s is the world's money, "
                    + "not the city's. Its plural adds an s, unless it ends in one; on the screens it is "
                    + "written $, and its initial and $ wherever a US dollar is beside it.",
                    Currency.CODE_LENGTH, Currency.FOREIGN_CODE));
            note.setWrapText(true);
            note.setMaxWidth(PANEL - 2 * PANEL_PAD);
            note.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
            nameBlock.getChildren().addAll(pair, note);
        }

        /* ----------------------- what it starts with ------------------------ */
        HBox cards = new HBox(10);
        for (Founding.Preset p : Founding.Preset.values()) {
            if (p != Founding.Preset.CUSTOM) cards.getChildren().add(presetCard(p));
        }
        Hyperlink custom = new Hyperlink("Set your own amounts");
        custom.setStyle(Palette.words(Palette.SIZE_SECTION - 1, Palette.ACCENT));
        custom.setOnAction(e -> { preset = Founding.Preset.CUSTOM; draw(); });
        VBox startBlock = new VBox(8, caption("WHAT IT STARTS WITH"), cards, custom);
        if (preset == Founding.Preset.CUSTOM) {
            // Today's Custom fields, opened in place by the link.
            TextField cash = new TextField(customCash);
            cash.setPromptText("treasury, millions");
            cash.setPrefWidth(140);
            cash.textProperty().addListener((o, was, now) -> { customCash = now; refresh(); });
            TextField usd = new TextField(customUsd);
            usd.setPromptText("vault, US$ millions");
            usd.setPrefWidth(140);
            usd.textProperty().addListener((o, was, now) -> { customUsd = now; refresh(); });
            Label inCash = new Label("treasury, in millions");
            inCash.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
            Label inUsd = new Label("vault, in US$ millions");
            inUsd.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
            HBox fields = new HBox(Palette.GAP, cash, inCash, usd, inUsd);
            fields.setAlignment(Pos.CENTER_LEFT);
            Label bounds = new Label(String.format(
                    "Between %s and %s in the treasury, and between %s and %s in the vault - "
                    + "room to try a city poorer or richer than any preset, not advice.",
                    money(Founding.MIN_CASH), money(Founding.MAX_CASH),
                    usd(Founding.MIN_RESERVE_USD), usd(Founding.MAX_RESERVE_USD)));
            bounds.setWrapText(true);
            bounds.setMaxWidth(PANEL - 2 * PANEL_PAD);
            bounds.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
            startBlock.getChildren().addAll(fields, bounds);
        }

        /* --------------------------- the world ----------------------------- */
        HBox worldRow = new HBox(0);
        worldRow.setAlignment(Pos.CENTER_LEFT);
        double[] choices = WorldEconomy.FOUNDING_CHOICES;
        for (int i = 0; i < choices.length; i++) {
            worldRow.getChildren().add(worldChip(choices[i], i == 0, i == choices.length - 1));
        }
        HBox worldLine = infoLine("A faster world makes your money stronger. It can't be changed later.",
                String.format("How fast prices rise out there, on average. The world's own price level "
                        + "settles about %.2f× founding at this setting, and the city's currency is "
                        + "worth its own prices against that - so a faster world is a stronger currency "
                        + "here, cheaper imports, and a harder time selling abroad. %s is the default. "
                        + "Chosen once: a city keeps the world it grew up in.",
                        WorldEconomy.settledLevelAt(mean), percent(WorldEconomy.DEFAULT_MEAN_INFLATION)),
                false, Palette.SIZE_SECTION - 1, Palette.TEXT_MUTED, PANEL - 2 * PANEL_PAD);
        VBox worldBlock = new VBox(8, caption("THE WORLD'S INFLATION"), worldRow, worldLine);

        /* ------------------------------ found it ----------------------------- */
        found = new Button("Found the city");
        found.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + Palette.ON_FILL + ";"
                + " -fx-background-color: " + Palette.GOOD + "; -fx-background-radius: 8;"
                + " -fx-border-color: transparent; -fx-padding: 12 26 12 26; -fx-cursor: hand;");
        found.setOnAction(e -> foundIfReady());
        Button back = new Button("Back");
        back.setStyle("-fx-font-size: 15px; -fx-text-fill: " + Palette.TEXT_HEAD + ";"
                + " -fx-background-color: " + Palette.RAISED + "; -fx-background-radius: 8;"
                + " -fx-border-color: " + Palette.EDGE + "; -fx-border-radius: 8;"
                + " -fx-padding: 12 20 12 20; -fx-cursor: hand;");
        back.setOnAction(e -> ui.showMainMenu());
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        Label keys = new Label("Enter founds · Esc goes back");
        keys.setStyle(Palette.words(Palette.SIZE_HEADING, Palette.TEXT_MUTED));
        HBox bar = new HBox(10, found, back, gap, keys);
        bar.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(bar, new javafx.geometry.Insets(4, 0, 0, 0));

        whyNot = new Label("");
        whyNot.setWrapText(true);
        whyNot.setMaxWidth(PANEL - 2 * PANEL_PAD);
        whyNot.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.BAD_SOFT));

        VBox panel = new VBox(22, title, nameBlock, startBlock, worldBlock, bar, whyNot);
        panel.setPrefWidth(PANEL);
        panel.setMinWidth(PANEL);
        panel.setMaxWidth(PANEL);
        panel.setMaxHeight(Region.USE_PREF_SIZE);
        panel.setStyle("-fx-padding: 34 " + PANEL_PAD + " 34 " + PANEL_PAD + ";"
                + " -fx-background-color: " + Palette.PANEL + "; -fx-background-radius: 14;"
                + " -fx-border-color: " + Palette.EDGE + "; -fx-border-radius: 14;");

        /*
         * IN THE MIDDLE OF THE WINDOW, AND SCROLLING WHEN IT DOES NOT FIT. The
         * panel sits in a page as tall as the viewport, centred; on a window
         * shorter than the panel the page grows past it and the scroller
         * scrolls. About 630 pixels tall with nothing opened (the implementer's
         * measure of its parts; unrendered in the cloud), so a 768-pixel window
         * shows it whole.
         */
        StackPane page = new StackPane(panel);
        page.setStyle("-fx-padding: 16;");
        javafx.scene.control.ScrollPane scroller = new javafx.scene.control.ScrollPane(page);
        scroller.setFitToWidth(true);
        scroller.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scroller.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        page.minHeightProperty().bind(scroller.heightProperty().subtract(2));
        ui.titleContent().getChildren().add(scroller);
        refresh();
    }

    /** A section's caption over its controls, as the mockups set it. */
    private static Label caption(String text) {
        Label caption = new Label(text);
        caption.setStyle(Palette.strong(Palette.SIZE_HEADING, Palette.TEXT_LABEL));
        return caption;
    }

    /**
     * One of the four starts, as a card: its name, its money, and one line on
     * how hard it is - never on what the money buys (Jerus: "even the screen
     * shouldnt say what the money could buy"). The figures in full, and an
     * Insane city's debt, are its tooltip. Standard carries "default".
     */
    private VBox presetCard(Founding.Preset p) {
        boolean on = p == preset;
        Label name = new Label(p.label());
        name.setStyle(Palette.strong(Palette.SIZE_SECTION + 1, Palette.TEXT_HEAD));
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox top = new HBox(6, name, gap);
        top.setAlignment(Pos.CENTER_LEFT);
        if (p == Founding.Preset.STANDARD) {
            Label chip = new Label("default");
            chip.setStyle(Palette.words(Palette.SIZE_BODY, Palette.ACCENT)
                    + " -fx-border-color: " + Palette.ACCENT + "; -fx-border-radius: 10;"
                    + " -fx-padding: 0 7 0 7;");
            top.getChildren().add(chip);
        }

        Currency money = currency();
        String here = money == null ? Currency.SYMBOL : money.qualifiedSymbol();
        Label figures = new Label(p == Founding.Preset.INSANE ? "nothing"
                : compact(marked(here, money(p.cash()))) + " · " + compact(usd(p.reserveUsd())));
        figures.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-font-size: 14px;"
                + " -fx-text-fill: " + Palette.TEXT_HEAD + ";");

        Label line = new Label(switch (p) {
            case INSANE -> "Nothing. The ground is owed on a " + Founding.INSANE_LAND_YEARS + "-year bond.";
            case LEAN -> "Tight. Borrow early.";
            case STANDARD -> "The usual start.";
            case WEALTHY -> "Plenty to spare.";
            default -> "";
        });
        line.setWrapText(true);
        line.setStyle(Palette.words(Palette.SIZE_HEADING, Palette.TEXT_LABEL));

        VBox card = new VBox(6, top, figures, line);
        card.setPrefWidth(0);
        card.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setStyle("-fx-padding: 14; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-width: 2;"
                + " -fx-cursor: hand; -fx-background-color: " + (on ? Palette.PINNED : Palette.RAISED) + ";"
                + " -fx-border-color: " + (on ? Palette.MONEY : Palette.EDGE) + ";");
        Tooltip.install(card, new Tooltip(p == Founding.Preset.INSANE
                ? String.format("Nothing in hand: it runs on the central bank's advances and borrows to build."
                        + " Its ground is owed abroad on a %d-year US-dollar bond at %.0f%%: %s.",
                        Founding.INSANE_LAND_YEARS, Founding.INSANE_LAND_COUPON * 100, usd(Founding.landBondUsd()))
                : "In the treasury " + marked(here, money(p.cash())) + ", in the vault " + usd(p.reserveUsd()) + "."));
        card.setOnMouseClicked(e -> { preset = p; draw(); });
        return card;
    }

    /** A whole amount without its ".0": "D$100.0M" is "D$100M" on a card, where the room is a card's. */
    private static String compact(String amount) {
        return amount.replaceAll("\\.0([kMBT])$", "$1");
    }

    /** One choice of world, as one of a row of five joined segments. */
    private Button worldChip(double m, boolean first, boolean last) {
        boolean on = Math.abs(mean - m) < 1e-9;
        Button b = new Button(percent(m));
        String radius = first ? "8 0 0 8" : last ? "0 8 8 0" : "0";
        b.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-font-size: 13px;"
                + " -fx-text-fill: " + (on ? Palette.ON_FILL : Palette.TEXT_LABEL) + ";"
                + " -fx-background-color: " + (on ? Palette.MONEY : Palette.RAISED) + ";"
                + " -fx-background-radius: " + radius + "; -fx-border-radius: " + radius + ";"
                + " -fx-border-color: " + Palette.EDGE + "; -fx-padding: 8 16 8 16; -fx-cursor: hand;");
        b.setOnAction(e -> { mean = m; draw(); });
        return b;
    }

    /* =====================================================================
       WHAT A KEYSTROKE CHANGES
       ===================================================================== */
    private void refresh() {
        if (currencyLine == null) return;
        Currency money = currency();
        String city = cityName == null ? "" : cityName.trim();
        boolean unnamed = money == null || (!ownCurrency && Founding.cityNameProblem(city) != null);
        currencyLine.setText(unnamed
                ? (ownCurrency ? "Its money: not named yet · " : "Its money: named after the city, once it has a name · ")
                : "Its money: the " + money.name() + ", " + money.qualifiedSymbol() + " · ");

        String problem = problem();
        found.setDisable(problem != null);
        whyNot.setText(problem == null ? "" : problem);
        whyNot.setManaged(problem != null);
        whyNot.setVisible(problem != null);
    }

    /* =====================================================================
       THE CHOICES, AS THE MODEL READS THEM
       ===================================================================== */

    /** The money as chosen, or null when a typed pair does not pass. */
    private Currency currency() {
        if (ownCurrency) return Currency.typed(typedName, typedCode);
        return Currency.fromCityName(cityName);
    }

    /** The treasury in thousands, or NaN when a custom figure will not parse. */
    private double cash() {
        return preset == Founding.Preset.CUSTOM ? millions(customCash) : preset.cash();
    }

    /** The vault in thousands of US dollars, or NaN. */
    private double reserveUsd() {
        return preset == Founding.Preset.CUSTOM ? millions(customUsd) : preset.reserveUsd();
    }

    /** Why Found is not lit, or null: the model's reason, or the screen's when a field will not parse. */
    private String problem() {
        if (ownCurrency) {
            String p = Currency.nameProblem(typedName);
            if (p != null) return p;
            p = Currency.codeProblem(typedCode);
            if (p != null) return p;
        }
        if (preset == Founding.Preset.CUSTOM) {
            if (!Double.isFinite(cash())) return "Type the treasury in millions: 100 is " + money(100_000) + ".";
            if (!Double.isFinite(reserveUsd())) return "Type the vault in millions of US dollars: 25 is " + usd(25_000) + ".";
        }
        return choices().problem();
    }

    /** The founding as chosen. */
    private Founding choices() {
        Founding f = Founding.custom(cityName, cash(), reserveUsd(), mean);
        return ownCurrency ? f.withCurrency(currency()) : f;
    }

    /** Found it if it can be - Found's button and the Enter key. True when it was. */
    boolean foundIfReady() {
        if (problem() != null) return false;
        ui.foundCity(choices());
        return true;
    }

    private static double millions(String typed) {
        try {
            double m = Double.parseDouble(typed == null ? "" : typed.replace(",", "").trim());
            return Double.isFinite(m) ? m * 1000 : Double.NaN;
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    private static String trim(double v) {
        return v == Math.rint(v) ? String.format("%.0f", v) : String.valueOf(v);
    }

    private static String percent(double m) {
        return String.format("%.2g%%", m * 100).replace("0.0%", "0%");
    }
}
