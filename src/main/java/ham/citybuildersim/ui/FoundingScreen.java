package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
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
 * rate ... (with option to just start at default)". So Start New Game comes
 * here, and game.newGame() runs only when the player founds the city.
 *
 * EVERY FIGURE IS THE MODEL'S. The currency's name, code and symbols are
 * Currency.fromCityName() and Currency.typed(); the presets and the bounds on
 * a custom founding are Founding's; what each buys is Game.whatItBuys(); the
 * world's settled level is WorldEconomy.settledLevelAt(); and whether the
 * city can be founded, and if not why, is Founding.problem(). The screen
 * holds the player's half-made choices between redraws and nothing else.
 *
 * TYPING DOES NOT REDRAW THE PAGE. The page is rebuilt when a chip or the tick
 * changes it; a keystroke only refreshes the lines that depend on it - the
 * currency, what a custom founding buys, and whether Found is lit and why not -
 * so the field being typed in keeps its focus and its caret.
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
    private VBox buysBox;
    private Button found;

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
    }

    /* =====================================================================
       THE PAGE
       ===================================================================== */
    void draw() {
        ui.clearMenu(SCREEN, this::draw);

        Label title = new Label("FOUND A CITY");
        title.setStyle(Palette.words(Palette.SIZE_TITLE, Palette.TEXT_HEAD)
                + " -fx-font-weight: bold; -fx-padding: 8 0 2 0;");

        VBox column = new VBox(0);
        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(Region.USE_PREF_SIZE);

        /* ----------------------------- the name ----------------------------- */
        column.getChildren().add(statementHead("The city"));
        TextField name = new TextField(cityName);
        name.setPromptText("Name the city");
        name.setMaxWidth(STATEMENT);
        name.textProperty().addListener((o, was, now) -> { cityName = now; refresh(); });
        column.getChildren().add(name);
        column.getChildren().add(statementNote(String.format(
                "Up to %d characters. It names the window, the save slot and - unless you "
                + "name it yourself - the city's money.", Founding.MAX_CITY_NAME_LENGTH)));

        /* ----------------------------- the money ---------------------------- */
        column.getChildren().add(statementHead("Its money"));
        currencyLine = sentence("", Palette.TEXT_BODY);
        column.getChildren().add(currencyLine);
        CheckBox own = new CheckBox("Name the currency myself");
        own.setSelected(ownCurrency);
        own.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_BODY));
        own.setOnAction(e -> { ownCurrency = own.isSelected(); draw(); });
        column.getChildren().add(own);
        if (ownCurrency) {
            TextField money = new TextField(typedName);
            money.setPromptText("What it is called - crown, mark, thaler");
            money.setPrefWidth(STATEMENT - 90);
            money.textProperty().addListener((o, was, now) -> { typedName = now; refresh(); });
            TextField code = new TextField(typedCode);
            code.setPromptText("ABC");
            code.setPrefWidth(70);
            code.textProperty().addListener((o, was, now) -> { typedCode = now; refresh(); });
            HBox pair = new HBox(Palette.GAP, money, code);
            pair.setAlignment(Pos.CENTER_LEFT);
            pair.setStyle("-fx-padding: 4 0 2 0;");
            column.getChildren().add(pair);
            column.getChildren().add(statementNote(String.format(
                    "A name, and a code of exactly %d letters A to Z - %s is the world's money, "
                    + "not the city's. Its plural adds an s, unless it ends in one; on the screens it is "
                    + "written $, and its initial and $ wherever a US dollar is beside it.",
                    Currency.CODE_LENGTH, Currency.FOREIGN_CODE)));
        } else {
            column.getChildren().add(statementNote(
                    "Named after the city: its first three letters are the code, and its "
                    + "initial and $ mark it wherever a US dollar is beside it."));
        }

        /* ----------------------- what the founders leave -------------------- */
        column.getChildren().add(statementHead("What the founders leave"));
        String[] names = new String[Founding.Preset.values().length];
        for (Founding.Preset p : Founding.Preset.values()) names[p.ordinal()] = p.label();
        javafx.scene.layout.FlowPane chips = chipStrip(names, preset.label(), Palette.SIZE_LABEL, pick -> {
            for (Founding.Preset p : Founding.Preset.values()) if (p.label().equals(pick)) preset = p;
            draw();
        });
        chips.setAlignment(Pos.CENTER_LEFT);
        chips.setMaxWidth(STATEMENT);
        column.getChildren().add(chips);
        if (preset == Founding.Preset.CUSTOM) {
            TextField cash = new TextField(customCash);
            cash.setPromptText("treasury, millions");
            cash.setPrefWidth(140);
            cash.textProperty().addListener((o, was, now) -> { customCash = now; refresh(); });
            TextField usd = new TextField(customUsd);
            usd.setPromptText("vault, US$ millions");
            usd.setPrefWidth(140);
            usd.textProperty().addListener((o, was, now) -> { customUsd = now; refresh(); });
            Label inCash = new Label("treasury, in millions");
            inCash.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            Label inUsd = new Label("vault, in US$ millions");
            inUsd.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            HBox fields = new HBox(Palette.GAP, cash, inCash, usd, inUsd);
            fields.setAlignment(Pos.CENTER_LEFT);
            fields.setStyle("-fx-padding: 6 0 2 0;");
            column.getChildren().add(fields);
            column.getChildren().add(statementNote(String.format(
                    "Between %s and %s in the treasury, and between %s and %s in the vault - "
                    + "room to try a city poorer or richer than any preset, not advice.",
                    money(Founding.MIN_CASH), money(Founding.MAX_CASH),
                    usd(Founding.MIN_RESERVE_USD), usd(Founding.MAX_RESERVE_USD))));
        }
        buysBox = new VBox(0);
        column.getChildren().add(buysBox);

        /* ------------------------------ the world ---------------------------- */
        column.getChildren().add(statementHead("The world it is founded into"));
        HBox worldRow = new HBox(Palette.GAP_TIGHT);
        worldRow.setAlignment(Pos.CENTER_LEFT);
        for (double m : WorldEconomy.FOUNDING_CHOICES) worldRow.getChildren().add(worldChip(m));
        column.getChildren().add(worldRow);
        column.getChildren().add(statementNote(String.format(
                "How fast prices rise OUT THERE, on average. The world's own price level "
                + "settles about %.2fx founding at this setting, and the city's currency is "
                + "worth its own prices against that - so a faster world is a stronger "
                + "currency here, cheaper imports, and a harder time selling abroad. %s is "
                + "the default. Chosen once: a city keeps the world it grew up in.",
                WorldEconomy.settledLevelAt(mean), percent(WorldEconomy.DEFAULT_MEAN_INFLATION))));

        /* ------------------------------ found it ----------------------------- */
        found = new Button("Found the city");
        found.setStyle(Palette.words(Palette.SIZE_LABEL, "white")
                + " -fx-background-color: " + Palette.ACCENT_FILL + "; -fx-cursor: hand;");
        found.setOnAction(e -> foundIfReady());
        Button defaults = new Button("Found with defaults");
        defaults.setOnAction(e -> ui.foundCity(Founding.defaults()));
        Button back = new Button("Back");
        back.setOnAction(e -> ui.showMainMenu());
        HBox bar = new HBox(8, found, defaults, back);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setMaxWidth(STATEMENT);
        bar.setStyle("-fx-padding: 14 0 2 0;");
        whyNot = new Label("");
        whyNot.setWrapText(true);
        whyNot.setMaxWidth(STATEMENT);
        whyNot.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.BAD_TEXT));
        column.getChildren().addAll(bar, whyNot);
        column.getChildren().add(statementNote(String.format(
                "Found with defaults: %s, its money named after it, %s and %s, a %s world. "
                + "Enter founds; Esc goes back.",
                Founding.DEFAULT_CITY_NAME, money(Game.FOUNDING_CASH), usd(Game.FOUNDING_RESERVE_USD),
                percent(WorldEconomy.DEFAULT_MEAN_INFLATION))));

        ui.rootMenu.getChildren().addAll(title, ui.scrolled(column));
        refresh();
    }

    /** One choice of world, shown as what it is. */
    private Button worldChip(double m) {
        boolean on = Math.abs(mean - m) < 1e-9;
        Button b = new Button(percent(m));
        b.setStyle(Palette.figure(Palette.SIZE_LABEL, on ? "white" : Palette.TEXT_MUTED)
                + " -fx-background-color: " + (on ? Palette.ACCENT : Palette.CONTROL) + ";"
                + " -fx-background-radius: " + Palette.RADIUS_TIGHT + ";"
                + " -fx-border-color: " + (on ? "transparent" : Palette.CONTROL_EDGE) + ";"
                + " -fx-border-radius: " + Palette.RADIUS_TIGHT + ";"
                + " -fx-min-width: 56; -fx-cursor: hand;");
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
                ? (ownCurrency ? "Its money: not named yet." : "Its money: named after the city, once it has a name.")
                : "Its money: " + money.describe() + ", written " + money.symbol()
                        + " alone and " + money.qualifiedSymbol() + " beside a US dollar"
                        + (ownCurrency || city.isEmpty() ? "." : " - after " + city + "."));

        buysBox.getChildren().clear();
        double cash = cash(), usd = reserveUsd();
        String here = unnamed ? Currency.SYMBOL : money.qualifiedSymbol();
        if (Double.isFinite(cash) && Double.isFinite(usd)
                && Founding.cashProblem(cash) == null && Founding.reserveProblem(usd) == null) {
            Founding.Buys buys = ui.game.whatItBuys(cash, usd);
            buysBox.getChildren().add(statementLine("In the treasury", marked(here, money(cash)), Palette.TEXT_HEAD));
            buysBox.getChildren().add(statementLine("...the founding village", marked(here, money(buys.village()))));
            buysBox.getChildren().add(statementNote(village()));
            buysBox.getChildren().add(statementLine("...and what is left",
                    marked(here, money(buys.leftAfterVillage())),
                    buys.leftAfterVillage() < 0 ? Palette.BAD : Palette.TEXT_BODY));
            for (Founding.Work w : buys.works()) {
                buysBox.getChildren().add(statementLine("   " + w.name() + "  " + marked(here, money(w.cost())),
                        w.fitsInCash() ? "in cash" : "a bond for " + marked(here, money(w.bondNeeded())),
                        w.fitsInCash() ? Palette.GOOD : Palette.WARN));
            }
            buysBox.getChildren().add(statementNote(
                    "Each of the first works on its own, after the village, at what a new city is "
                    + "invoiced for it - the building and the material it takes, bought abroad. What "
                    + "the treasury cannot pay for, the city borrows from the build screen, on a "
                    + Game.BUILD_BOND_YEARS + "-year bond or a " + Game.BUILD_NOTE_MONTHS + "-month note."));
            buysBox.getChildren().add(statementLine("In the vault", usd(usd), Palette.TEXT_HEAD));
            buysBox.getChildren().add(statementNote(buys.vaultSays()));
        }

        String problem = problem();
        found.setDisable(problem != null);
        whyNot.setText(problem == null ? "" : problem);
    }

    /** "60 houses, 5 convenience stores, ..." - the village, from the model's own list. */
    private static String village() {
        StringBuilder out = new StringBuilder("The village the city is founded as: ");
        for (int i = 0; i < Founding.VILLAGE.length; i++) {
            if (i > 0) out.append(i == Founding.VILLAGE.length - 1 ? " and " : ", ");
            out.append(Founding.VILLAGE[i][1]).append(" x ").append(Founding.VILLAGE[i][0]);
        }
        return out.append('.').toString();
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
