package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Statement.*;
import static ham.citybuildersim.ui.Pieces.*;

/**
 * Found a city: its name, its money, what the founders leave in the treasury and the vault, the ground it stands on and the world it is founded into.
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
 *
 * AND THE GROUND IT STANDS ON (0.7.56, batch J1a). A row for the World: the
 * seed of the city's coast, lakes, river and ore (World, the project's
 * spec-land.md), rolled when the page opens and again by the dice, or typed.
 * Any whole number is a world and the same number is the same world; it
 * cannot be changed later. The city's land stands on it since 0.7.57
 * (CityLand).
 *
 * ...OR THE REAL EARTH (0.7.121, batch E4; the project's spec-earth.md 2;
 * Jerus's answers of 2026-10-10, "all rec": Earth for new cities, the default
 * once it ships, the generated world kept as the other choice). THE WORLD is
 * two joined chips, Earth | Generated, Earth chosen when the Earth's data is
 * here (EarthSite.gameData()); where it is not, Earth is greyed and a line
 * says in plain words where the file goes and, since 0.7.125, how it gets
 * there (EarthSite.dataWords()). On Earth the panel widens to two
 * columns, the city's choices on the left and the world on the right, in the
 * page's own style - captions over controls, the cards' greys:
 *   - a search field over the data's place names (EarthSite.search(): names
 *     for finding a spot, no borders - a hit is its name and its latitude and
 *     longitude), and Random spot, which rolls the seed and lets it pick a
 *     place that passes the site test (EarthSite.randomPlace());
 *   - the globe (Globe below): the planet orthographic when it is wide -
 *     since 0.7.135 the land office's map's own globe (GlobeFrame, the
 *     planet's picture GlobePicture and the far rule through its camera) -
 *     and from GROUND_KM_PX down to the 150 m view the city's own far view
 *     (MapTiles.nodeTerrain(), E5's), drawn on a worker; drag or W, A, S
 *     and D turn it, the wheel or - and + zoom it, a click picks the spot;
 *   - the card (EarthSite.measure(), on a worker): the site the snap found
 *     and how far from the spot, the ground within 5 km - dry, rivers and
 *     lakes, sea and (0.7.127) ice - and the nearest fresh water and sea, the province and its
 *     deposit multipliers, the nearest iron, and since ET1 the ground by relief, the nearest shallow
 *     water with the sea's depth, and the landmass (D21: EarthSite.reliefWords(), shallowWords(),
 *     landmassWords()); a refusal (the sea, ice) in the
 *     bad colour, which keeps Found dark, and a warning (a desert, no water
 *     near) in the warning colour, which does not.
 * The seed decides an Earth city's detail below the data, its fields and its
 * streets; it is shown under the card, and the dice beside it rolls it.
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
    /** The world's seed, as typed or rolled. */
    private String worldSeed = String.valueOf(Founding.DEFAULT_WORLD_SEED);

    /** The real Earth or the generated world (0.7.121): Earth when its data is here. */
    private boolean earth;
    /** What the game found when it looked for the Earth's data; null while it is looking. */
    private EarthSite.Data earthData;
    /** The spot picked, degrees; NaN until one is. */
    private double spotLat = Double.NaN, spotLon = Double.NaN;
    /** The place it was picked by, from the search or Random spot; null for a click. */
    private String spotPlace;
    /** The card for the spot, and the key it was measured for; null while it is measured. */
    private EarthSite.Card card;
    private World.Key cardKey;
    /** The search as typed. */
    private String searchText = "";

    /* ------------------------------------------------ what a keystroke refreshes */

    private Label currencyLine, whyNot;
    private Button found;
    private TextField nameField, searchField;
    private VBox cardBox, hitsBox;

    /** The globe, kept across redraws: its view and its last picture. */
    private final Globe globe = new Globe();

    /** The worker the card is measured on, one at a time. */
    private static final ExecutorService CARDS = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "founding-card");
        t.setDaemon(true);
        return t;
    });

    /** Which measurement is the latest: a card that comes back for an older one is dropped. */
    private long cardAsked;

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
        // A new world every time the page opens (spec-land, J1a): the dice's first roll.
        worldSeed = String.valueOf(Founding.rollWorldSeed());
        // The Earth, when its data is here (0.7.121): looked for off the screen's thread, the page redrawn when it is known.
        spotLat = spotLon = Double.NaN;
        spotPlace = null;
        card = null;
        cardKey = null;
        searchText = "";
        earthData = null;
        earth = false;
        CARDS.submit(() -> {
            EarthSite.Data d = EarthSite.gameData();
            if (d.here()) d.reader().places();
            Platform.runLater(() -> {
                earthData = d;
                earth = d.here();
                if (SCREEN.equals(ui.currentScreen)) draw();
            });
        });
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

    /** The World's field: room for a seed of nineteen digits and a sign in the figures' face at 14 px (0.7.56). */
    static final double SEED_FIELD = 200;

    /** The panel on Earth (0.7.121): two columns, the city's and the world's, 40 apart - 1,200 px, inside a 1,280 window with the page's margins. */
    static final double EARTH_PANEL = 1200;

    /** Between the two columns on Earth. */
    static final double COLUMN_GAP = 40;

    /** The globe's width, the right column's: 560 px (GlobeFrame.FOUNDING_W since 0.7.135, so a harness draws what it draws). */
    static final double GLOBE_W = GlobeFrame.FOUNDING_W;

    /** The globe's height: 270 px, so the page still fits an 868-pixel window with the longest card under it (the words probe's, 0.7.121; GlobeFrame.FOUNDING_H since 0.7.135). */
    static final double GLOBE_H = GlobeFrame.FOUNDING_H;

    /** The left column's width on Earth: what the panel leaves. */
    static final double LEFT = EARTH_PANEL - 2 * PANEL_PAD - COLUMN_GAP - GLOBE_W;

    /* =====================================================================
       THE PAGE
       ===================================================================== */
    void draw() {
        ui.clearMenu(SCREEN, this::draw);
        boolean two = earth;
        double wide = two ? LEFT : PANEL - 2 * PANEL_PAD;

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
            money.setPrefWidth(wide - 90 - Palette.GAP);
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
            note.setMaxWidth(wide);
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
            cash.setPrefWidth(two ? 110 : 140);
            cash.textProperty().addListener((o, was, now) -> { customCash = now; refresh(); });
            TextField usd = new TextField(customUsd);
            usd.setPromptText("vault, US$ millions");
            usd.setPrefWidth(two ? 110 : 140);
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
            bounds.setMaxWidth(wide);
            bounds.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
            startBlock.getChildren().addAll(fields, bounds);
        }

        /* --------------------------- the ground ---------------------------- */
        VBox groundBlock = earth ? earthBlock() : generatedBlock();

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
                false, Palette.SIZE_SECTION - 1, Palette.TEXT_MUTED, wide);
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
        whyNot.setMaxWidth((two ? EARTH_PANEL : PANEL) - 2 * PANEL_PAD);
        whyNot.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.BAD_SOFT));

        double panelWidth = two ? EARTH_PANEL : PANEL;
        VBox panel;
        if (two) {
            // On Earth (0.7.121): the city's choices on the left, the world's on the right.
            VBox left = new VBox(22, nameBlock, startBlock, worldBlock);
            left.setPrefWidth(LEFT);
            left.setMinWidth(LEFT);
            left.setMaxWidth(LEFT);
            groundBlock.setPrefWidth(GLOBE_W);
            groundBlock.setMinWidth(GLOBE_W);
            groundBlock.setMaxWidth(GLOBE_W);
            HBox columns = new HBox(COLUMN_GAP, left, groundBlock);
            panel = new VBox(22, title, columns, bar, whyNot);
        } else {
            panel = new VBox(22, title, nameBlock, startBlock, groundBlock, worldBlock, bar, whyNot);
        }
        panel.setPrefWidth(panelWidth);
        panel.setMinWidth(panelWidth);
        panel.setMaxWidth(panelWidth);
        panel.setMaxHeight(Region.USE_PREF_SIZE);
        panel.setStyle("-fx-padding: 34 " + PANEL_PAD + " 34 " + PANEL_PAD + ";"
                + " -fx-background-color: " + Palette.PANEL + "; -fx-background-radius: 14;"
                + " -fx-border-color: " + Palette.EDGE + "; -fx-border-radius: 14;");

        /*
         * IN THE MIDDLE OF THE WINDOW, AND SCROLLING WHEN IT DOES NOT FIT. The
         * panel sits in a page as tall as the viewport, centred; on a window
         * shorter than the panel the page grows past it and the scroller
         * scrolls. About 630 pixels tall with nothing opened (the implementer's
         * measure of its parts; unrendered in the cloud), and about 710 since
         * the World's row (0.7.56: a caption, a 32-pixel row and 22 between
         * blocks), about 750 since its two chips (0.7.121), so a 768-pixel
         * window still shows it whole. On Earth two columns, the right one the
         * taller - its chips, the search, the globe and the card - about 780,
         * inside an 868-pixel window.
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
        // A spot's card is the founding's as chosen now: measured again if the seed moved while the page showed the generated world.
        if (earth && !Double.isNaN(spotLat)) measure();
    }

    /** A section's caption over its controls, as the mockups set it. */
    private static Label caption(String text) {
        Label caption = new Label(text);
        caption.setStyle(Palette.strong(Palette.SIZE_HEADING, Palette.TEXT_LABEL));
        return caption;
    }

    /* =====================================================================
       THE WORLD: EARTH OR GENERATED (0.7.121)
       ===================================================================== */

    /** The two joined chips, Earth | Generated: Earth greyed while its data is being looked for or is not here. */
    private HBox kindChips() {
        boolean here = earthData != null && earthData.here();
        Button e = kindChip(earthData == null ? "Earth …" : "Earth", earth, true, here);
        e.setOnAction(x -> { if (here && !earth) { earth = true; draw(); } });
        if (!here) e.setTooltip(new Tooltip(earthData == null ? "Looking for the Earth's data." : earthData.words()));
        else e.setTooltip(new Tooltip("The real Earth: pick a spot on the globe, search for a place, or let the seed pick one."
                + " The coasts, lakes, rivers and rocks are the real ones; the seed draws the detail below the data and the fields."));
        Button g = kindChip("Generated", !earth, false, true);
        g.setOnAction(x -> { if (earth) { earth = false; draw(); } });
        g.setTooltip(new Tooltip("A world made from the seed: its own coast, lakes, river and ore."));
        HBox row = new HBox(0, e, g);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private static Button kindChip(String words, boolean on, boolean first, boolean usable) {
        Button b = new Button(words);
        String radius = first ? "8 0 0 8" : "0 8 8 0";
        b.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;"
                + " -fx-text-fill: " + (on ? Palette.ON_FILL : usable ? Palette.TEXT_LABEL : Palette.TEXT_SPENT) + ";"
                + " -fx-background-color: " + (on ? Palette.MONEY : Palette.RAISED) + ";"
                + " -fx-background-radius: " + radius + "; -fx-border-radius: " + radius + ";"
                + " -fx-border-color: " + Palette.EDGE + "; -fx-padding: 7 18 7 18; -fx-cursor: " + (usable ? "hand" : "default") + ";");
        return b;
    }

    /** THE WORLD on a generated world: the chips, and the seed's row as since 0.7.56; where the Earth's data is not here, why. */
    private VBox generatedBlock() {
        TextField seedField = new TextField(worldSeed);
        seedField.setPrefWidth(SEED_FIELD);
        seedField.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-font-size: 14px;");
        seedField.textProperty().addListener((o, was, now) -> { worldSeed = now; refresh(); });
        seedField.setTooltip(new Tooltip("The seed of the city's ground: its coast, lakes, river and ore."
                + " The same number is the same world."));
        Button dice = diceButton("Roll another world", () -> seedField.setText(String.valueOf(Founding.rollWorldSeed())));
        Label seedLine = new Label("The same number makes the same coast, lakes and ore.");
        seedLine.setStyle(Palette.words(Palette.SIZE_SECTION - 1, Palette.TEXT_MUTED));
        HBox seedRow = new HBox(Palette.GAP, seedField, dice, seedLine);
        seedRow.setAlignment(Pos.CENTER_LEFT);
        VBox block = new VBox(8, caption("THE WORLD"), kindChips(), seedRow);
        if (earthData != null && !earthData.here()) {
            Label why = new Label(earthData.words());
            why.setWrapText(true);
            why.setMaxWidth(PANEL - 2 * PANEL_PAD);
            why.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
            block.getChildren().add(2, why);
        }
        return block;
    }

    private Button diceButton(String tip, Runnable roll) {
        Button dice = new Button();
        dice.setGraphic(icon(Icons.DICE, Palette.TEXT_LABEL, 18));
        dice.setStyle("-fx-background-color: " + Palette.RAISED + "; -fx-background-radius: 8;"
                + " -fx-border-color: " + Palette.EDGE + "; -fx-border-radius: 8; -fx-padding: 6 10 6 10; -fx-cursor: hand;");
        dice.setTooltip(new Tooltip(tip));
        dice.setOnAction(e -> roll.run());
        return dice;
    }

    /** THE WORLD on the real Earth: the chips, the search and Random spot, the globe with the hits over it, and the card. */
    private VBox earthBlock() {
        searchField = new TextField(searchText);
        searchField.setPromptText("Search a place - Rotterdam, Perth, São Paulo");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchField.textProperty().addListener((o, was, now) -> { searchText = now; showHits(); });
        Button random = new Button("Random spot");
        random.setStyle("-fx-font-size: 13px; -fx-text-fill: " + Palette.TEXT_HEAD + "; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-color: " + Palette.EDGE + "; -fx-border-radius: 8; -fx-padding: 6 14 6 14; -fx-cursor: hand;");
        random.setTooltip(new Tooltip("Rolls the seed, and the seed picks a place: one of the data's towns and cities with dry ground"
                + " to found on and no ice. The same seed picks the same place."));
        random.setOnAction(e -> randomSpot());
        HBox searchRow = new HBox(Palette.GAP, searchField, random);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        hitsBox = new VBox(0);
        hitsBox.setMaxWidth(GLOBE_W - 120);
        hitsBox.setMaxHeight(Region.USE_PREF_SIZE);
        hitsBox.setStyle("-fx-background-color: " + Palette.PINNED + "; -fx-border-color: " + Palette.EDGE + ";"
                + " -fx-background-radius: 0 0 8 8; -fx-border-radius: 0 0 8 8; -fx-padding: 4 0 4 0;");
        StackPane.setAlignment(hitsBox, Pos.TOP_LEFT);
        StackPane over = new StackPane(globe.node(this), hitsBox);
        over.setPrefSize(GLOBE_W, GLOBE_H);
        over.setMaxSize(GLOBE_W, GLOBE_H);
        showHits();

        cardBox = new VBox(4);
        cardBox.setMaxWidth(GLOBE_W);
        fillCard();
        return new VBox(8, caption("THE WORLD"), kindChips(), searchRow, over, cardBox);
    }

    /** The search's hits over the globe: each its name and where it is; none while nothing is typed. */
    private void showHits() {
        if (hitsBox == null) return;
        hitsBox.getChildren().clear();
        List<EarthData.Place> hits = hits();
        for (EarthData.Place p : hits) {
            Hyperlink h = new Hyperlink(p.name().trim() + "  ·  " + EarthSite.latLonWords(p.lat(), p.lon()));
            h.setStyle(Palette.words(Palette.SIZE_HEADING, Palette.TEXT_HEAD) + " -fx-padding: 3 12 3 12;");
            h.setOnAction(e -> pickPlace(p));
            hitsBox.getChildren().add(h);
        }
        if (hits.isEmpty() && !searchText.isBlank() && earthData != null && earthData.here()) {
            Label none = new Label("No place of that name in the Earth's data");
            none.setStyle(Palette.words(Palette.SIZE_HEADING, Palette.TEXT_MUTED) + " -fx-padding: 3 12 3 12;");
            hitsBox.getChildren().add(none);
        }
        boolean shown = !hitsBox.getChildren().isEmpty();
        hitsBox.setVisible(shown);
        hitsBox.setManaged(shown);
    }

    private List<EarthData.Place> hits() {
        if (earthData == null || !earthData.here() || searchText.isBlank()) return List.of();
        return EarthSite.search(earthData.reader(), searchText, EarthSite.SEARCH_HITS);
    }

    /** A place picked from the search: the spot, the view on it, the search cleared, the card measured. */
    private void pickPlace(EarthData.Place p) {
        spotPlace = p.name().trim();
        searchText = "";
        if (searchField != null) searchField.setText("");
        pick(p.lat(), p.lon(), true);
    }

    /** Random spot: a new seed, and the place it picks. */
    private void randomSpot() {
        if (earthData == null || !earthData.here()) return;
        long seed = Founding.rollWorldSeed();
        worldSeed = String.valueOf(seed);
        EarthData d = earthData.reader();
        long asked = ++cardAsked;
        card = null;
        fillCard();
        refresh();
        CARDS.submit(() -> {
            EarthData.Place p = EarthSite.randomPlace(d, EarthData.EARTH_2, seed);
            Platform.runLater(() -> {
                if (asked != cardAsked) return;
                if (p == null) return;
                spotPlace = p.name().trim();
                pick(p.lat(), p.lon(), true);
            });
        });
    }

    /** A spot picked - by a click, the search or Random spot: the view centred on it (and closer, `closer`), its card measured. */
    void pick(double lat, double lon, boolean closer) {
        spotLat = lat;
        spotLon = EarthProj.normaliseLon(lon);
        globe.centre(spotLat, spotLon, closer);
        measure();
    }

    /** The card for the spot as chosen now, measured on the worker; the card shows the measuring until it comes back. */
    private void measure() {
        Long seed = seed();
        if (Double.isNaN(spotLat) || seed == null || earthData == null || !earthData.here()) {
            card = null;
            cardKey = null;
            fillCard();
            refresh();
            return;
        }
        World.Key key = World.Key.earth(seed, spotLat, spotLon, EarthData.EARTH_2);
        if (key.equals(cardKey) && card != null) {
            fillCard();
            refresh();
            return;
        }
        long asked = ++cardAsked;
        card = null;
        cardKey = key;
        fillCard();
        refresh();
        CARDS.submit(() -> {
            EarthSite.Card c;
            try {
                c = EarthSite.measure(key);
            } catch (RuntimeException e) {
                System.out.println("The founding card could not be measured at " + EarthSite.latLonWords(key.lat(), key.lon()) + ": " + e);
                c = null;
            }
            EarthSite.Card done = c;
            Platform.runLater(() -> {
                if (asked != cardAsked) return;
                card = done;
                fillCard();
                refresh();
                globe.repaint();
            });
        });
    }

    /* ------------------------------------------------------------ the card */

    /** The card's words under the globe: where the spot is, then what is within 5 km, its geology and iron, and why not or what to know. */
    private void fillCard() {
        if (cardBox == null) return;
        cardBox.getChildren().clear();
        cardBox.getChildren().add(caption("THE SPOT"));
        if (Double.isNaN(spotLat)) {
            cardBox.getChildren().add(cardLine(NO_SPOT_WORDS, Palette.TEXT_MUTED));
            return;
        }
        EarthSite.Card c = card;
        String where = (spotPlace != null ? spotPlace + " · " : "") + EarthSite.latLonWords(spotLat, spotLon);
        if (c == null || cardKey == null) {
            cardBox.getChildren().addAll(cardLine(where, Palette.TEXT_HEAD), cardLine("Measuring the ground within 5 km…", Palette.TEXT_MUTED));
            return;
        }
        if (!c.hasSite()) {
            cardBox.getChildren().addAll(cardLine(where, Palette.TEXT_HEAD), cardLine(c.refusal(), Palette.BAD_SOFT));
            return;
        }
        cardBox.getChildren().add(cardLine(siteWords(c), Palette.TEXT_HEAD));
        if (c.refusal() != null) {
            cardBox.getChildren().add(cardLine(c.refusal(), Palette.BAD_SOFT));
            return;
        }
        cardBox.getChildren().add(cardLine(groundWords(c), Palette.TEXT_BODY));
        cardBox.getChildren().add(cardLine(waterWords(c), Palette.TEXT_BODY));
        cardBox.getChildren().add(cardLine(geologyWords(c), Palette.TEXT_BODY));
        cardBox.getChildren().add(cardLine(ironWords(c), Palette.TEXT_BODY));
        // ET1 (D21): the ground by relief, the shallow water and the sea's depth, and the landmass, the words EarthSite writes.
        cardBox.getChildren().add(cardLine(EarthSite.reliefWords(c), Palette.TEXT_BODY));
        cardBox.getChildren().add(cardLine(EarthSite.shallowWords(c), Palette.TEXT_BODY));
        String landmass = EarthSite.landmassWords(c);
        if (landmass != null) cardBox.getChildren().add(cardLine(landmass, Palette.TEXT_BODY));
        for (String w : c.warnings()) cardBox.getChildren().add(cardLine(w, Palette.WARN));
        Long seed = seed();
        Label seedLine = cardLine(seedWords(seed == null ? 0 : seed), Palette.TEXT_MUTED);
        seedLine.setTooltip(new Tooltip("Random spot rolls it. The same seed and spot found the same city."));
        cardBox.getChildren().add(seedLine);
    }

    /** The card's line before a spot is picked; how the globe is used. */
    static final String NO_SPOT_WORDS = "None yet. Drag the globe or hold W, A, S and D to turn it, zoom with the wheel or - and +, and click to pick a spot;"
            + " or search for a place, or press Random spot.";

    /** The card's last line: what the seed decides on Earth. */
    static String seedWords(long seed) {
        return String.format("World seed %,d: the detail below the data, the fields and the streets.", seed);
    }

    /** The card's geology line: the province and its class, and the multiplier of each resource in fields. */
    static String geologyWords(EarthSite.Card c) {
        StringBuilder geo = new StringBuilder(c.provinceName() != null ? "Geology: " + c.provinceName() + ", " + kindWords(c.kind()) : "Geology: no province mapped here");
        String sep = " · deposits ";
        for (Resource r : Resource.values()) {
            if (!r.inFields()) continue;
            geo.append(sep).append(r.label().toLowerCase(java.util.Locale.ROOT)).append(String.format(" ×%.2f", c.multiplier(r)));
            sep = ", ";
        }
        return geo.toString();
    }

    /** The card's first line: where the site is, from the nearest place, and how far the snap moved it. */
    static String siteWords(EarthSite.Card c) {
        return (c.near() != null ? c.near() + " · " : "") + EarthSite.latLonWords(c.siteLat(), c.siteLon())
                + (c.snappedM() >= 50 ? String.format(" · the site %s from the spot", distance(c.snappedM() / 1000)) : "");
    }

    /** The card's ground line; ice's share after the sea's where there is any (0.7.127). */
    static String groundWords(EarthSite.Card c) {
        return String.format("Within 5 km: %s dry · %s rivers and lakes · %s sea", share(c.dryShare()), share(c.freshShare()), share(c.seaShare()))
                + (c.icePlots() > 0 ? " · " + share(c.iceShare()) + " ice" : "");
    }

    /** The card's water line. */
    static String waterWords(EarthSite.Card c) {
        return (Double.isNaN(c.freshKm()) ? "No river or lake within 5 km" : "Fresh water " + distance(c.freshKm()) + " away")
                + " · " + (Double.isNaN(c.seaKm()) ? "no sea within 5 km" : "the sea " + distance(c.seaKm()) + " away");
    }

    /** The card's iron line. */
    static String ironWords(EarthSite.Card c) {
        return Double.isNaN(c.ironKm())
                ? String.format("No iron field within %,.0f km", EarthSite.IRON_RINGS * World.CELL * World.PLOT_M / 1000.0)
                : "The nearest iron field: " + distance(c.ironKm()) + " " + c.ironWay();
    }

    private static Label cardLine(String words, String colour) {
        Label l = new Label(words);
        l.setWrapText(true);
        l.setMaxWidth(GLOBE_W);
        l.setStyle(Palette.words(Palette.SIZE_HEADING, colour));
        return l;
    }

    /** A geologic class in the card's words, as the map's hover writes it. */
    static String kindWords(Geology.Kind k) {
        return switch (k) {
            case OCEAN -> "oceanic";
            case SHIELD -> "a shield";
            case FOLD_BELT -> "a fold belt";
            case RIFT -> "a rift";
            case SHELF -> "a shelf";
            case BASIN -> "a basin";
            default -> "unclassed";
        };
    }

    /** A share as the card writes it: a whole per cent, and "under 1%" for a little. */
    static String share(double s) {
        if (s <= 0) return "0%";
        if (s < 0.005) return "under 1%";
        return String.format("%.0f%%", 100 * s);
    }

    /** A distance as the card writes it: a tenth of a km under ten, whole km from there. */
    static String distance(double km) {
        return km < 10 ? String.format("%.1f km", km) : String.format("%,.0f km", km);
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
        // The title has the row to itself and is never ellipsized: on Earth the four cards share 520 px, about
        // 90 of text each, and "Standard" beside its chip read "Stan..." (PT1, 2026-10-23).
        name.setMinWidth(Region.USE_PREF_SIZE);
        VBox top = new VBox(3, name);
        top.setAlignment(Pos.CENTER_LEFT);
        if (p == Founding.Preset.STANDARD) {
            // The chip has its own line under the title, for the same reason.
            Label chip = new Label("default");
            chip.setStyle(Palette.words(Palette.SIZE_BODY, Palette.ACCENT)
                    + " -fx-border-color: " + Palette.ACCENT + "; -fx-border-radius: 10;"
                    + " -fx-padding: 0 7 0 7;");
            chip.setMinWidth(Region.USE_PREF_SIZE);
            top.getChildren().add(chip);
        }

        Currency money = currency();
        String here = money == null ? Currency.SYMBOL : money.qualifiedSymbol();
        Label figures = new Label(p == Founding.Preset.INSANE ? "nothing"
                : compact(marked(here, money(p.cash()))) + " · " + compact(usd(p.reserveUsd())));
        figures.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-font-size: 14px;"
                + " -fx-text-fill: " + Palette.TEXT_HEAD + ";");
        figures.setWrapText(true);

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
        Tooltip tip = new Tooltip(p == Founding.Preset.INSANE ? "Nothing in hand."
                : "In the treasury " + marked(here, money(p.cash())) + ", in the vault " + usd(p.reserveUsd()) + ".");
        // Insane's bond is the plots the world's centre holds (0.7.106): priced on the seed as typed when the
        // tip opens, not on every keystroke - drawing a new world's centre takes a moment.
        if (p == Founding.Preset.INSANE) tip.setOnShowing(e -> tip.setText(insaneWords()));
        Tooltip.install(card, tip);
        card.setOnMouseClicked(e -> { preset = p; draw(); });
        return card;
    }

    /** Insane's card's tip: the advances, and the land bond on the plots this world's centre holds (Founding.landBondUsd(), 0.7.106) - the default world's while the seed will not parse; on Earth (0.7.121) the spot's, once its card has found a site, and none named till then. */
    private String insaneWords() {
        Long typed = seed();
        String head = String.format("Nothing in hand: it runs on the central bank's advances and borrows to build."
                        + " Its ground - the plots it is founded on, its lake with them - is owed abroad on a"
                        + " %d-year US-dollar bond at %.0f%%", Founding.INSANE_LAND_YEARS, Founding.INSANE_LAND_COUPON * 100);
        if (earth) {
            if (card == null || !card.foundable() || cardKey == null) return head + ", priced once a spot is picked.";
            return head + ": " + usd(Founding.landBondUsd(cardKey)) + ".";
        }
        return head + ": " + usd(Founding.landBondUsd(typed == null ? Founding.DEFAULT_WORLD_SEED : typed)) + ".";
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

    /** The world's seed as typed, or null when it is not a whole number. */
    private Long seed() {
        return Founding.parseWorldSeed(worldSeed);
    }

    /**
     * Why Found is not lit, or null: the model's reason, or the screen's when
     * a field will not parse - and on Earth (0.7.121) no spot yet, the card
     * still being measured, or its refusal. The Earth's part is read off the
     * card, which is the model's (EarthSite.measure()), so a keystroke never
     * waits on the ground; Found asks Founding.problem() whole once more.
     */
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
        if (seed() == null) return "Type the world as a whole number, like " + Founding.DEFAULT_WORLD_SEED + ", or roll the dice.";
        String p = generatedChoices().problem();
        if (p != null || !earth) return p;
        if (Double.isNaN(spotLat)) return "Pick a spot on the globe, search for a place, or press Random spot.";
        if (card == null) return "Measuring the spot…";
        return card.refusal();
    }

    /** The founding as chosen on its seed's generated world: the city's own choices, which problem() asks first. */
    private Founding generatedChoices() {
        Long seed = seed();
        Founding f = Founding.custom(cityName, cash(), reserveUsd(), mean)
                .withWorldSeed(seed != null ? seed : Founding.DEFAULT_WORLD_SEED);
        return ownCurrency ? f.withCurrency(currency()) : f;
    }

    /** The founding as chosen: on Earth (0.7.121) round the spot picked. */
    private Founding choices() {
        Founding f = generatedChoices();
        return earth && !Double.isNaN(spotLat) ? f.onEarth(spotLat, spotLon) : f;
    }

    /**
     * Found it if it can be - Found's button and the Enter key. True when it
     * was, or (0.7.121) when Enter in the search picked its first hit instead.
     */
    boolean foundIfReady() {
        if (earth && searchField != null && searchField.isFocused() && !searchText.isBlank()) {
            List<EarthData.Place> hits = hits();
            if (!hits.isEmpty()) pickPlace(hits.get(0));
            return true;
        }
        if (problem() != null) return false;
        Founding f = choices();
        if (f.problem() != null) {
            refresh();
            return false;
        }
        ui.foundCity(f);
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

    /* =====================================================================
       THE GLOBE (0.7.121, batch E4; spec-earth 2: "the globe from the 4'
       layer, with zoom down to the 150 m view"; since 0.7.135, batch E9,
       the map's own globe - docs/studies/spec-globe.md 3.8, Jerus's Q7)

       One canvas, its view in one of the map's tiers by how far it is
       zoomed:
         - WIDE, from MOST_KM_PX to FOUNDING_HANDOFF_KM: the planet as the
           land office's map draws it, a GlobeFrame north up - the planet's
           picture (GlobePicture, 12' then 4', ice and the desert in the
           map's own colours, its rivers) to PICTURE_FROM_KM, then the far
           rule through the globe's camera (GlobePicture.view()) - so both
           screens show the same planet; its zoom at the pointer and its
           drag (the point grabbed stays under the pointer) the frame's;
         - CLOSE, from FOUNDING_HANDOFF_KM (★E9: 2.32 km, half a pixel at
           the canvas's corner, where E4's 4 km parted by 1.5) down to
           LEAST_KM_PX (150 m): the city's own far view of the ground
           (MapTiles.nodeTerrain(), E5's) on an Earth world centred where
           the view is, a whole number of plots a pixel - on the spot's own
           world while the view is centred on it, so what is drawn there is
           the city's ground. There is no city yet, so no reach: any spot.
       Drawn on a worker, the newest asked for drawn and the rest dropped;
       the last picture is laid where its centre lies now until then. W,
       A, S and D held turn it while it has the focus (★E9) - never while
       the name or the search is typed, which have it then.
       ===================================================================== */
    final class Globe {

        /** The widest view: the whole planet, 12,742 km, across GlobeFrame.FIT_SHARE (nine tenths) of the globe's height. */
        static final double MOST_KM_PX = 2 * EarthProj.RADIUS_M / 1000 / (GlobeFrame.FIT_SHARE * GLOBE_H);

        /** From here closer the ground is drawn round the view's centre, not through the globe: GlobeFrame.FOUNDING_HANDOFF_KM, 2.32 km a pixel (★E9; 4 until 0.7.135). */
        static final double GROUND_KM_PX = GlobeFrame.FOUNDING_HANDOFF_KM;

        /** The closest view: 150 m a pixel, five plots (spec-earth 2's 150 m view). */
        static final double LEAST_KM_PX = 5 * World.PLOT_M / 1000.0;

        /** How close a pick from the search or Random spot opens: 0.3 km a pixel, the 5 km round the site 33 pixels across. */
        static final double PICK_KM_PX = 0.3;

        /** A wheel notch's zoom, the map's: MapFrame.ZOOM_STEP. */
        static final double STEP = MapFrame.ZOOM_STEP;

        /** Space round the globe: the page's stage. */
        static final int SPACE = 0xff0b1118;

        /** The ground's colours, as the map draws them where the city owns it (GlobePicture.colours()): the founding globe dims nothing. */
        static final int[] COLOURS = GlobePicture.colours(false);

        /** The view: its centre, degrees, and its scale, km a pixel. */
        double lat = 30, lon = 0, kmPx = MOST_KM_PX;

        /** The wide view's arithmetic: the map's globe, a canvas's size, north up (it never comes near the map's handoff, where it would turn). */
        private final GlobeFrame wide = new GlobeFrame(GLOBE_W, GLOBE_H, lat, lon);

        private Canvas canvas;
        private WritableImage picture;
        /** The view the picture was drawn for. */
        private double drawnLat = Double.NaN, drawnLon = Double.NaN, drawnKm = Double.NaN;
        private long asked;
        private double pressX, pressY, lastX, lastY;
        private boolean dragging;
        /** W, A, S and D held, and when they last turned it. */
        private boolean keyW, keyA, keyS, keyD;
        private long keyNanos;
        /** The frames that turn it while a key is held: made at the first key (an AnimationTimer needs the toolkit, which a harness's window has not started). */
        private javafx.animation.AnimationTimer keys;

        /** The worker the globe is drawn on: one at a time, the newest asked for. */
        private final ExecutorService worker = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "founding-globe");
            t.setDaemon(true);
            return t;
        });

        /** The globe's canvas with its zoom buttons, made again with the page. */
        StackPane node(FoundingScreen screen) {
            canvas = new Canvas(GLOBE_W, GLOBE_H);
            canvas.setFocusTraversable(true);
            canvas.setOnMousePressed(e -> {
                pressX = lastX = e.getX();
                pressY = lastY = e.getY();
                dragging = false;
                canvas.requestFocus();
            });
            canvas.setOnMouseDragged(e -> {
                if (Math.abs(e.getX() - pressX) + Math.abs(e.getY() - pressY) > 4) dragging = true;
                if (!dragging) return;
                turn(lastX, lastY, e.getX(), e.getY());
                lastX = e.getX();
                lastY = e.getY();
                redraw();
            });
            canvas.setOnMouseReleased(e -> {
                if (dragging) {
                    dragging = false;
                    repaint();
                } else {
                    double[] ll = latLonAt(e.getX(), e.getY());
                    if (ll != null) {
                        screen.spotPlace = null;
                        screen.pick(ll[0], ll[1], false);
                    }
                }
            });
            canvas.setOnScroll(e -> {
                // The wheel zooms the globe at the pointer, and the page under it does not scroll.
                e.consume();
                if (e.getDeltaY() == 0) return;
                zoomAt(e.getX(), e.getY(), e.getDeltaY() > 0 ? 1 / STEP : STEP);
            });
            canvas.setOnKeyPressed(e -> { if (key(e.getCode(), true)) e.consume(); });
            canvas.setOnKeyReleased(e -> { if (key(e.getCode(), false)) e.consume(); });
            canvas.focusedProperty().addListener((o, was, now) -> { if (!now) letGo(); });
            Button in = zoomButton("+", "Closer"), out = zoomButton("-", "Wider");
            in.setOnAction(e -> zoomAt(GLOBE_W / 2, GLOBE_H / 2, 1 / STEP / STEP));
            out.setOnAction(e -> zoomAt(GLOBE_W / 2, GLOBE_H / 2, STEP * STEP));
            VBox zooms = new VBox(4, in, out);
            zooms.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            StackPane.setAlignment(zooms, Pos.TOP_RIGHT);
            StackPane.setMargin(zooms, new javafx.geometry.Insets(8));
            StackPane s = new StackPane(canvas, zooms);
            s.setStyle("-fx-background-color: " + Palette.STAGE + "; -fx-background-radius: 10;");
            if (picture != null && drawnLat == lat && drawnLon == lon && drawnKm == kmPx) repaint();
            else redraw();
            return s;
        }

        private Button zoomButton(String words, String tip) {
            Button b = new Button(words);
            b.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + Palette.TEXT_HEAD + ";"
                    + " -fx-background-color: " + Palette.RAISED + "; -fx-background-radius: 6; -fx-border-color: " + Palette.EDGE + ";"
                    + " -fx-border-radius: 6; -fx-padding: 0; -fx-min-width: 28; -fx-min-height: 28; -fx-cursor: hand;");
            b.setTooltip(new Tooltip(tip));
            return b;
        }

        /** The view centred on a spot, and as close as a pick opens (`closer`) if it is wider than that. */
        void centre(double atLat, double atLon, boolean closer) {
            lat = atLat;
            lon = atLon;
            if (closer && kmPx > PICK_KM_PX) kmPx = PICK_KM_PX;
            kmPx = snapped(kmPx);
            redraw();
        }

        /** Whether the view is wide: drawn through the map's globe. */
        private boolean isWide() { return kmPx >= GROUND_KM_PX; }

        /** The wide view's frame, on the view. */
        private GlobeFrame wide() {
            if (wide.centreLat() != lat || wide.centreLon() != lon || wide.kmPx() != kmPx) wide.set(lat, lon, kmPx);
            return wide;
        }

        /** A zoom by a factor (under one closer) at a canvas point: the point under it stays under it - through the map's globe while wide, round the view's centre close - snapped to whole plots close. */
        private void zoomAt(double px, double py, double by) {
            double to = snapped(Math.max(LEAST_KM_PX, Math.min(MOST_KM_PX, kmPx * by)));
            double[] p = latLonAt(px, py);
            if (isWide() && to >= GROUND_KM_PX) {
                GlobeFrame f = wide();
                f.zoomAt(px, py, to / kmPx);
                lat = f.centreLat();
                lon = f.centreLon();
                kmPx = Math.max(GROUND_KM_PX, f.kmPx());
            } else {
                kmPx = to;
                if (p != null) {
                    // The point back under the pointer on the plane round the old centre, at the new scale.
                    EarthProj plane = new EarthProj(lat, EarthProj.normaliseLon(lon));
                    double[] xy = new double[2], ll = new double[2];
                    if (plane.forward(p[0], p[1], xy)
                            && plane.inverse(xy[0] - (px - GLOBE_W / 2) * kmPx * 1000, xy[1] + (py - GLOBE_H / 2) * kmPx * 1000, ll)) {
                        lat = Math.max(-GlobeFrame.POLE_LAT, Math.min(GlobeFrame.POLE_LAT, ll[0]));
                        lon = ll[1];
                    }
                }
            }
            redraw();
        }

        /** A scale as drawn: close, a whole number of plots a pixel. */
        private double snapped(double km) {
            if (km >= GROUND_KM_PX) return km;
            long plots = Math.max(5, Math.round(km * 1000 / World.PLOT_M));
            return plots * World.PLOT_M / 1000.0;
        }

        /** The view turned by the pointer from (x0, y0) to (x1, y1): the point grabbed stays under it - the map's globe's drag while wide, the plane round the view's centre slid close. */
        private void turn(double x0, double y0, double x1, double y1) {
            if (isWide()) {
                GlobeFrame f = wide();
                f.drag(x0, y0, x1, y1);
                lat = f.centreLat();
                lon = f.centreLon();
                return;
            }
            slide(x1 - x0, y1 - y0);
        }

        /** Close, the ground slid by (dx, dy) pixels: the new centre is the point that lay (dx, dy) back from it, on the plane round the old. */
        private void slide(double dx, double dy) {
            double[] ll = new double[2];
            double m = kmPx * 1000;
            if (new EarthProj(lat, EarthProj.normaliseLon(lon)).inverse(-dx * m, dy * m, ll)) {
                lat = Math.max(-GlobeFrame.POLE_LAT, Math.min(GlobeFrame.POLE_LAT, ll[0]));
                lon = ll[1];
            }
        }

        /** The latitude and longitude under a canvas point, in the view as it is drawn; null off the planet. */
        double[] latLonAt(double px, double py) {
            if (isWide()) return wide().latLonAt(px, py);
            double x = (px - GLOBE_W / 2) * kmPx * 1000, y = (GLOBE_H / 2 - py) * kmPx * 1000;
            double[] ll = new double[2];
            return new EarthProj(lat, EarthProj.normaliseLon(lon)).inverse(x, y, ll) ? ll : null;
        }

        /** Where a latitude and longitude lie on the canvas in the view as drawn, or null where it is not seen. */
        double[] at(double atLat, double atLon) {
            if (isWide()) return wide().at(atLat, atLon);
            double[] xy = new double[2];
            if (!new EarthProj(lat, EarthProj.normaliseLon(lon)).forward(atLat, atLon, xy)) return null;
            return new double[] { GLOBE_W / 2 + xy[0] / 1000 / kmPx, GLOBE_H / 2 - xy[1] / 1000 / kmPx };
        }

        /* ----- W, A, S and D (0.7.135; spec-globe 3.11, ★E9: the founding globe takes the keys while it has the focus) ----- */

        /** A key down or up on the focused globe: true when it was one of the four. */
        private boolean key(javafx.scene.input.KeyCode k, boolean down) {
            switch (k) {
                case W: keyW = down; break;
                case A: keyA = down; break;
                case S: keyS = down; break;
                case D: keyD = down; break;
                default: return false;
            }
            if (keyW || keyA || keyS || keyD) {
                if (keyNanos == 0) keyNanos = System.nanoTime();
                if (keys == null) keys = new javafx.animation.AnimationTimer() {
                    @Override public void handle(long now) { keysFrame(); }
                };
                keys.start();
            }
            return true;
        }

        /** The focus gone: the keys let go, whose key-ups it would not hear. */
        private void letGo() {
            keyW = keyA = keyS = keyD = false;
        }

        /** A frame of the keys held: the globe turned by MapFrame.keyVelocity() over the time since the last - the map's globe's keyPan() while wide, the ground slid close. */
        private void keysFrame() {
            if (!(keyW || keyA || keyS || keyD) || canvas == null || canvas.getScene() == null) {
                keyNanos = 0;
                if (keys != null) keys.stop();
                return;
            }
            long now = System.nanoTime();
            double dt = keyNanos == 0 ? 0 : (now - keyNanos) / 1e9;
            keyNanos = now;
            double[] v = MapFrame.keyVelocity(keyW, keyA, keyS, keyD);
            if (isWide()) {
                GlobeFrame f = wide();
                f.keyPan(v, dt);
                lat = f.centreLat();
                lon = f.centreLon();
            } else {
                double[] d = MapFrame.keyStep(v, dt);
                slide(d[0], d[1]);
            }
            redraw();
        }

        /** The picture asked for again, on the worker: the newest wins. */
        void redraw() {
            long me = ++asked;
            double vLat = lat, vLon = lon, vKm = kmPx;
            World.Key spot = spotKey();
            EarthSite.Data d = earthData;
            GlobeFrame view = isWide() ? wide().copy(GLOBE_W, GLOBE_H, vKm) : null;
            repaint();
            if (d == null || !d.here()) return;
            worker.submit(() -> {
                if (me != asked) return;
                int[] px;
                try {
                    if (view != null) {
                        GlobePicture pic = GlobePicture.best(d.reader());
                        if (pic == null) {
                            // The planet's picture made once (the map's own), and the globe drawn again when each is kept.
                            GlobePicture.prepare(d.reader(), () -> Platform.runLater(this::redraw));
                            return;
                        }
                        px = new int[(int) GLOBE_W * (int) GLOBE_H];
                        World.Key key = spot != null ? spot : World.Key.earth(Founding.DEFAULT_WORLD_SEED, vLat, vLon, EarthData.EARTH_2);
                        pic.view(view, key, COLOURS, SPACE, px);
                    } else px = groundPixels(spot, vLat, vLon, vKm);
                } catch (RuntimeException e) {
                    System.out.println("The founding globe could not be drawn at " + EarthSite.latLonWords(vLat, vLon) + ": " + e);
                    return;
                }
                if (px == null || me != asked) return;
                Platform.runLater(() -> {
                    if (me != asked) return;
                    WritableImage img = new WritableImage((int) GLOBE_W, (int) GLOBE_H);
                    img.getPixelWriter().setPixels(0, 0, (int) GLOBE_W, (int) GLOBE_H, PixelFormat.getIntArgbInstance(), px, 0, (int) GLOBE_W);
                    picture = img;
                    drawnLat = vLat;
                    drawnLon = vLon;
                    drawnKm = vKm;
                    repaint();
                });
            });
        }

        /** The spot's own world's key while the view is centred on it (the city's ground), else null. */
        private World.Key spotKey() {
            Long seed = seed();
            if (seed == null || Double.isNaN(spotLat) || spotLat != lat || spotLon != lon) return null;
            return World.Key.earth(seed, spotLat, spotLon, EarthData.EARTH_2);
        }

        /** The canvas painted: the last picture (where its centre lies now, at the view's scale), the spot, and the site with its 5 km once the card has one. */
        void repaint() {
            if (canvas == null) return;
            GraphicsContext g = canvas.getGraphicsContext2D();
            g.setFill(Color.web(Palette.STAGE));
            g.fillRect(0, 0, GLOBE_W, GLOBE_H);
            if (picture != null) {
                // The last picture where its centre lies in the view now, at the view's scale - slid by a drag or the keys,
                // grown or shrunk by a zoom - until the worker draws the view again.
                double[] c = at(drawnLat, drawnLon);
                if (c != null) {
                    double f = drawnKm / kmPx;
                    boolean current = drawnLat == lat && drawnLon == lon && drawnKm == kmPx;
                    if (!current) g.setGlobalAlpha(0.6);
                    g.drawImage(picture, c[0] - GLOBE_W / 2 * f, c[1] - GLOBE_H / 2 * f, GLOBE_W * f, GLOBE_H * f);
                    g.setGlobalAlpha(1);
                }
            }
            if (earthData == null || !earthData.here()) {
                g.setFill(Color.web(Palette.TEXT_MUTED));
                g.fillText(earthData == null ? "Looking for the Earth's data…" : "The Earth's data is not here.", 16, GLOBE_H / 2);
                return;
            }
            if (picture == null) {
                g.setFill(Color.web(Palette.TEXT_MUTED));
                g.fillText("Drawing the Earth…", 16, GLOBE_H / 2);
            }
            if (dragging) return;
            EarthSite.Card c = card;
            if (c != null && c.hasSite()) {
                double[] s = at(c.siteLat(), c.siteLon());
                if (s != null) {
                    double r = EarthSite.CARD_PLOTS * World.PLOT_M / 1000.0 / kmPx;
                    g.setStroke(Color.web(c.foundable() ? Palette.ACCENT_LIGHT : Palette.BAD_SOFT));
                    g.setLineWidth(1.5);
                    if (r >= 4) g.strokeOval(s[0] - r, s[1] - r, 2 * r, 2 * r);
                    g.strokeLine(s[0] - 6, s[1], s[0] + 6, s[1]);
                    g.strokeLine(s[0], s[1] - 6, s[0], s[1] + 6);
                }
            }
            if (!Double.isNaN(spotLat)) {
                double[] p = at(spotLat, spotLon);
                if (p != null) {
                    g.setFill(Color.web(Palette.WARN));
                    g.fillOval(p[0] - 3.5, p[1] - 3.5, 7, 7);
                }
            }
        }
    }

    /* ------------------------------------------------------- the close tier's ground */

    /** The ground close: the city's own far view (MapTiles.nodeTerrain()) on an Earth world centred on the view - the spot's own when it is there - a whole number of plots a pixel, each pixel's colour by its cover and relief as the map's far nodes are (ET1 slice D); the void past the planet's rim. Since 0.7.135 the globe's wide views are the map's (GlobePicture.view()), and this is the founding globe's own arithmetic alone. */
    static int[] groundPixels(World.Key spot, double lat0, double lon0, double kmPx) {
        World.Key key = spot != null ? spot : World.Key.earth(Founding.DEFAULT_WORLD_SEED, lat0, lon0, EarthData.EARTH_2);
        long s = Math.max(1, Math.round(kmPx * 1000 / World.PLOT_M));
        int w = (int) GLOBE_W, h = (int) GLOBE_H, n = MapFrame.NODE_PX;
        long x0 = World.SIDE / 2 - (w / 2) * s, y0 = World.SIDE / 2 - (h / 2) * s;
        int[] out = new int[w * h];
        for (int ny = 0; ny * n < h; ny++) {
            for (int nx = 0; nx * n < w; nx++) {
                byte[] relief = new byte[n * n], node = MapTiles.nodeTerrain(key, x0 + (long) nx * n * s, y0 + (long) ny * n * s, s, relief);
                for (int v = 0; v < n && ny * n + v < h; v++) {
                    for (int u = 0; u < n && nx * n + u < w; u++) {
                        int i = nx * n + u, j = ny * n + v;
                        double mx = EarthProj.metresX(x0 + (i + 0.5) * s), my = EarthProj.metresY(y0 + (j + 0.5) * s);
                        out[j * w + i] = EarthProj.onPlanet(mx, my) ? TileRaster.colour(node[v * n + u], relief[v * n + u]) : TileRaster.VOID;
                    }
                }
            }
        }
        return out;
    }
}
