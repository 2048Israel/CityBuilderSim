package ham.citybuildersim.ui;

import ham.citybuildersim.BankRules;
import ham.citybuildersim.Game;
import javafx.scene.layout.VBox;

/**
 * The same saved banking dials on Policy and the central bank's page.
 * WHY (CBK3): two screens must set one rule, with Kept retaining the nodes
 * and Ladder retaining the player's thumb when a month redraws the page.
 */
final class BankRulesPane {
    static VBox build(Game game, Runnable redraw) {
        BankRules rules = game.getBank().rules();
        VBox page = Kept.column(Palette.GAP);
        page.getChildren().add(PolicyScreen.words("BANKING RULES · set at once",
                Palette.SIZE_LABEL + 2, Palette.TEXT));
        for (BankRules.Dial dial : BankRules.Dial.values()) {
            page.getChildren().add(Kept.scope(dial, () -> {
                Ladder ladder = Ladder.of(dial.min, dial.max, dial.step, v -> BankRules.reading(dial, v))
                        .current(rules.get(dial))
                        .appliesAtOnce(v -> { rules.set(dial, v); redraw.run(); })
                        .wide(420);
                return PolicyScreen.card(
                        PolicyScreen.words(dial.label, Palette.SIZE_LABEL + 1, Palette.TEXT),
                        ladder.build(),
                        PolicyScreen.words(dial.explanation, Palette.SIZE_LABEL, Palette.TEXT_LABEL));
            }));
        }
        return page;
    }
}
