package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * What the Government screen says of the coupons the city pays abroad: the note on its "...of which paid abroad" row, from the moneys the
 * city's foreign paper is written in (FB13, CD9-G).
 *
 * WHY THIS EXISTS. The row's note said "in dollars", from before CD8, when the city could owe the US$ alone. Since CD8 a piece is written in any
 * of the eight moneys and its coupon is paid in that money (Game.payForeignInterest(own, money)), and the figure on the row is the month's
 * sum of them in D$ (Game.getForeignInterestPaidThisMonth(), "kept in LOCAL money"): right, and in D$, which "in dollars" read as US$. A pure
 * class beside the model, as LandmassView is, so that the words are written once and a harness can rebuild them from getters; the screen only
 * puts them on the row.
 *
 * @author Jerus
 */
public final class ForeignCouponView {

    private ForeignCouponView() { }

    /**
     * The moneys the city's foreign paper is written in, in WorldMoneys.EIGHT's order, as ForeignAccounts.MONEY_PLURAL names them
     * ("US dollars", "euros", "Swiss francs"): the pieces on the books now (DebtManager.hasForeignDebtIn()). Empty with no foreign paper.
     */
    public static List<String> moneys(DebtManager ledger) {
        List<String> out = new ArrayList<>();
        for (String code : WorldMoneys.EIGHT) {
            int k = ForeignAccounts.moneyIndex(code);
            if (k >= 0 && ledger.hasForeignDebtIn(code)) out.add(ForeignAccounts.MONEY_PLURAL[k]);
        }
        return out;
    }

    /**
     * The row's note: "in D$, from coupons written in US dollars, euros and Swiss francs, at the month's rates" - the figure's own money first,
     * then what the coupons are in. With no foreign paper on the books (the month's last piece fell due after its coupon was paid) the moneys
     * are not named: "in D$, at the month's rates".
     */
    public static String note(DebtManager ledger) {
        List<String> moneys = moneys(ledger);
        if (moneys.isEmpty()) return "in D$, at the month's rates";
        String names = moneys.size() == 1 ? moneys.get(0)
                : String.join(", ", moneys.subList(0, moneys.size() - 1)) + " and " + moneys.get(moneys.size() - 1);
        return "in D$, from coupons written in " + names + ", at the month's rates";
    }
}
