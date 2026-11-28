package ham.citybuildersim.ui;

import ham.citybuildersim.Expectations;
import ham.citybuildersim.Game;
import ham.citybuildersim.HoldingsRules;
import javafx.scene.layout.VBox;

/**
 * The central bank's rules for its holdings, beside its holdings card on
 * Policy and on its own Policy page: the pace, how the book shrinks, the
 * auction share and the law on direct purchases.
 * WHY (CBK2): the dial was a target the book reached at a pace that was not
 * the player's, and the bank could not bid at an auction past its own
 * rollover. BankRulesPane's shape (CBK3): Kept keeps the nodes and Ladder the
 * player's thumb when a month redraws the page.
 */
final class HoldingsRulesPane {
    static VBox build(Game game, Runnable redraw) {
        HoldingsRules rules = game.getCentralBank().holdingsRules();
        VBox page = Kept.column(Palette.GAP);
        page.getChildren().add(PolicyScreen.words("THE HOLDINGS' RULES · set at once",
                Palette.SIZE_LABEL + 2, Palette.TEXT));
        for (HoldingsRules.Dial dial : HoldingsRules.Dial.values()) {
            page.getChildren().add(Kept.scope(dial, () -> {
                Ladder ladder = Ladder.of(dial.min, dial.max, dial.step, v -> HoldingsRules.reading(dial, v))
                        .current(rules.get(dial))
                        .appliesAtOnce(v -> { rules.set(dial, v); redraw.run(); })
                        .wide(420);
                return PolicyScreen.card(
                        PolicyScreen.words(dial.label, Palette.SIZE_LABEL + 1, Palette.TEXT),
                        ladder.build(),
                        PolicyScreen.words(dial.explanation, Palette.SIZE_LABEL, Palette.TEXT_LABEL));
            }));
        }
        // What the law has cost so far, read from the model: the share lent lately and the premium on expectations.
        page.getChildren().add(Kept.scope("financing", () -> PolicyScreen.words(financingLine(game),
                Palette.SIZE_LABEL, Palette.TEXT_LABEL)));
        return page;
    }

    /** One plain line: what it has lent the treasury at auction lately against a year's GDP, and what that adds to expected inflation. */
    static String financingLine(Game game) {
        double gdp = game.getEconomyManager().getYearGdp();
        double lately = game.getCentralBank().getFinancedLately();
        double premium = game.getExpectations().getFinancingPremium();
        if (!(lately > 0)) return "It has lent the treasury nothing at auction lately. Up to "
                + PolicyScreen.pct0(Expectations.FINANCING_ANCHORED) + " of GDP a year costs no trust; "
                + PolicyScreen.pct0(Expectations.FINANCING_UNANCHORED) + " a year costs its full, as in Argentina 2020 and Ghana 2022.";
        return "It has lent the treasury " + (gdp > 0 ? Money.pct2(lately / gdp) + " of a year's GDP" : "money")
                + " at auction lately, which adds " + Money.points(premium) + " to the inflation people expect.";
    }
}
