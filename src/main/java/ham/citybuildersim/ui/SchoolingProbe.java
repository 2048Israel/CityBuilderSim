package ham.citybuildersim.ui;

import ham.citybuildersim.PopulationManager;
import ham.citybuildersim.SchoolingLadder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Reads the qualification page's actual drawing inputs without starting a JavaFX window.
 * WHY (WG3/WS2). A correct after-cost model would still mislead if a bar read
 * gross pay, clipped a negative value, or displayed a different verdict.
 */
public final class SchoolingProbe {
    private SchoolingProbe() { }

    public static List<String> differences(SchoolingLadder.View view, PopulationManager people) {
        List<String> failures = new ArrayList<>();
        if (!Arrays.asList(PolicyScreen.POLICY_WAGE_PAGES).contains("The ladder")
                || PolicyScreen.POLICY_WAGE_PAGES.length != PolicyScreen.WAGE_ICONS.length)
            failures.add("Wages navigation does not expose the ladder");
        PolicyScreen.SchoolingBars bars = PolicyScreen.schoolingBars(view, people);
        if (bars.rows().size() != view.rows().size() * 2) {
            failures.add("not one bar and one detail row per qualification");
            return failures;
        }
        for (int i = 0; i < view.rows().size(); i++) {
            SchoolingLadder.Row model = view.rows().get(i);
            Pieces.ScaleRow bar = bars.rows().get(i * 2), detail = bars.rows().get(i * 2 + 1);
            if (!bar.name().equals(model.label()) || bar.runs().size() != 2)
                failures.add(model.label() + " wrong bar shape");
            else {
                Pieces.Run paid = bar.runs().get(0), after = bar.runs().get(1);
                if (paid.from() != bars.zero() || paid.amount() != model.paid() || paid.hollow())
                    failures.add(model.label() + " paid bar differs from model");
                double end = model.afterCost() < 0 ? after.from() : after.from() + after.amount();
                if (Math.abs(end - bars.zero() - model.afterCost()) > 1e-10 || !after.hollow()
                        || after.amount() != Math.abs(model.afterCost()))
                    failures.add(model.label() + " after-cost bar differs from model");
                if (after.from() < 0 || paid.from() + paid.amount() > bars.scale()
                        || after.from() + after.amount() > bars.scale())
                    failures.add(model.label() + " bar clipped by scale");
            }
            long posts = SchoolingLadder.posts(model, people);
            String words = detail.name();
            if (!detail.caption() || !words.contains(Money.people(posts) + " posts")
                    || !words.contains(Money.people(SchoolingLadder.holders(model, people)) + " holders")
                    || !words.contains(view.verdict(model.qualification(), posts).words()))
                failures.add(model.label() + " posts, holders or verdict differs from model");
            if (model.months() > 0 && (!words.contains(model.months() + " months")
                    || !words.contains(Money.money(model.fees())) || !words.contains(Money.money(model.forgone()))))
                failures.add(model.label() + " course costs differ from model");
            for (Pieces.Tick tick : bar.marks())
                if (Math.abs(tick.at() - bars.zero() - model.target()) > 1e-10)
                    failures.add(model.label() + " wage-walk tick differs from model");
        }
        return List.copyOf(failures);
    }
}
