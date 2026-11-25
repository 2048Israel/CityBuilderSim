package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Pieces.*;
import static ham.citybuildersim.ui.Statement.*;
import static ham.citybuildersim.ui.Levers.*;

/** The debt survey on Household money, after HS3's stage page (HC1/HC2). */
final class HouseholdDebtPanel {
    String tier = "All tiers", stage = "All stages";
    boolean adult, annual;

    /** Read the selected population and year from the model's own statistics. */
    DebtStatistics.Snapshot snapshot(Game game) {
        PayTier pay = null;
        for (PayTier t : PayTier.values()) if (t.getLabel().equals(tier)) pay = t;
        DebtStatistics.Stage age = null;
        for (DebtStatistics.Stage s : DebtStatistics.Stage.values()) if (stageName(s).equals(stage)) age = s;
        if (annual && !game.getHistorySave().getDebtSurveys().isEmpty()) {
            List<DebtStatistics.Survey> surveys = game.getHistorySave().getDebtSurveys();
            String key = tier.equals("Untiered") ? "UNTIERED" : pay == null ? "All" : pay.name();
            if (age != null) key = key.equals("All") ? age.name() : key + ":" + age.name();
            return surveys.get(surveys.size() - 1).groups().get(key);
        }
        return tier.equals("Untiered") ? DebtStatistics.untiered(game.getHouseholdBalance(), age)
                : DebtStatistics.of(game.getHouseholdBalance(), pay, age);
    }

    static String stageName(DebtStatistics.Stage s) {
        return switch (s) {
            case YOUNG -> "Young";
            case ESTABLISHED -> "Established";
            case SENIOR -> "Senior";
            case ELDER -> "Elder";
            case OTHER -> "Outside families";
        };
    }

    /** Cards lead with the share affected; the full comparison remains behind details. */
    VBox draw(Game game, Runnable redraw, boolean details) {
        DebtStatistics.Snapshot s = snapshot(game);
        DebtStatistics.Measure card = (adult ? s.perAdult() : s.perHousehold()).get(DebtStatistics.Kind.CARD);
        GridPane vitals = equalColumns(3, Palette.GAP);
        vitals.add(Kept.scope("holding", () -> PeopleScreen.statCard("HOLDING A CARD BALANCE", pct(card.shareHolding()),
                Palette.TEXT_HEAD, adult ? "of adults" : "of households", null)), 0, 0);
        vitals.add(Kept.scope("service", () -> PeopleScreen.statCard("DEBT SERVICE RATIO", pct(s.debtServiceRatio()),
                Palette.TEXT_HEAD, "required payments / disposable income", null)), 1, 0);
        vitals.add(Kept.scope("late", () -> PeopleScreen.statCard("LATE CARD BALANCES", pct(card.delinquency()),
                Palette.WARN, "of the card book", null)), 2, 0);
        List<String> tiers = new ArrayList<>(List.of("All tiers"));
        for (PayTier t : PayTier.values()) tiers.add(t.getLabel());
        tiers.add("Untiered");
        List<String> stages = new ArrayList<>(List.of("All stages"));
        for (DebtStatistics.Stage a : DebtStatistics.Stage.values()) stages.add(stageName(a));
        VBox out = Kept.column(Palette.GAP,
                Kept.scope("basis", () -> chipStrip(new String[]{"Per household", "Per adult"},
                        adult ? "Per adult" : "Per household", Palette.SIZE_LABEL,
                        v -> { adult = v.equals("Per adult"); redraw.run(); })),
                Kept.scope("tiers", () -> chipStrip(tiers.toArray(String[]::new), tier, Palette.SIZE_LABEL,
                        v -> { tier = v; redraw.run(); })),
                Kept.scope("stages", () -> chipStrip(stages.toArray(String[]::new), stage, Palette.SIZE_LABEL,
                        v -> { stage = v; redraw.run(); })),
                Kept.scope("year", () -> chipStrip(new String[]{"Current month", "Latest yearly survey"},
                        annual ? "Latest yearly survey" : "Current month", Palette.SIZE_LABEL,
                        v -> { annual = v.equals("Latest yearly survey"); redraw.run(); })), vitals);
        List<DebtStatistics.Survey> surveys = game.getHistorySave().getDebtSurveys();
        String when = annual && !surveys.isEmpty()
                ? "Yearly survey, month " + surveys.get(surveys.size() - 1).month()
                : "Current month" + (annual ? " — no yearly survey yet" : "");
        out.getChildren().add(PeopleScreen.words(when + " · " + String.format(Locale.ROOT, "%.0f households · %.0f adults", s.households(), s.adults()),
                Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        if (details) out.getChildren().add(Kept.scope("table", () -> table(s)));
        return out;
    }

    /** Each kind's amounts are among holders; the late rates name their denominator. */
    VBox table(DebtStatistics.Snapshot s) {
        GridPane grid = Kept.grid();
        grid.setHgap(Palette.GAP); grid.setVgap(Palette.GAP_TIGHT);
        String[] heads = {"Debt kind", "Holding", "Median", "Mean", "Late", "60+ days*", "Late balances", "Charged off / year"};
        for (int col = 0; col < heads.length; col++) grid.add(heading(heads[col]), col, 0);
        int row = 1;
        for (DebtStatistics.Kind k : DebtStatistics.Kind.values()) {
            DebtStatistics.Measure m = (adult ? s.perAdult() : s.perHousehold()).get(k);
            String name = switch (k) {
                case TOTAL -> "All debt";
                case CARD -> "Credit cards";
                case STUDENT -> "Student loans";
                case CAR -> "Separate car loans";
                case MORTGAGE -> "Mortgages";
            };
            String[] values = {name, pct(m.shareHolding()), tightMoney(toDollars(m.medianAmongHolders()), false),
                    tightMoney(toDollars(m.meanAmongHolders()), false), pct(m.lateShare()), pct(m.late60Share()),
                    pct(m.delinquency()), pct(m.annualChargeOff())};
            for (int col = 0; col < values.length; col++) grid.add(heading(values[col]), col, row);
            row++;
        }
        ScrollPane scroll = Kept.of(ScrollPane.class, ScrollPane::new);
        scroll.setContent(grid); scroll.setFitToHeight(true); scroll.setPrefViewportHeight(150);
        scroll.setStyle("-fx-background-color: transparent;");
        VBox out = Kept.column(Palette.GAP, scroll);
        out.getChildren().addAll(
                statementLine("Debt / annual disposable income", pct(s.debtToDisposableIncome())),
                statementLine("Debt / assets", pct(s.debtToAssets())),
                statementLine("Paying over 40% of disposable income", pct(adult ? s.adultsPayingOver40() : s.payingOver40())),
                statementLine("...among debtors", pct(adult ? s.adultDebtorsPayingOver40() : s.debtorsPayingOver40())),
                statementLine("Bankruptcies this month", String.format(Locale.ROOT, "%.2f", adult ? s.bankruptAdults() : s.bankruptcies())),
                statementLine("Homes sold in arrears this month", String.format(Locale.ROOT, "%.2f", s.forcedSales())),
                PeopleScreen.words("Median and mean are among holders. Holding and late shares use the selected population; late balances and annualised net charge-offs use the debt book. "
                        + "*60+ days is an estimate from the late stock. Total debt uses an approximate spread; adult amounts split household debts evenly. "
                        + "Car finance is on the card. Separate car loans, mortgages and forced home sales await their model; homes are not yet in assets.",
                        Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        return out;
    }

    private static Label heading(String text) {
        Label label = Kept.label(text);
        label.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_BODY));
        label.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        return label;
    }

    private static String pct(double value) { return String.format(Locale.ROOT, "%.2f%%", 100 * value); }
}
