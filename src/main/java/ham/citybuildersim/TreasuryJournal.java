package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The treasury's journal: every movement of the city's cash that is neither a
 * budget line nor paper raised or repaid, recorded by name as it happens, so
 * the bridge on the Government tab can name them line by line.
 *
 * WHY. Jerus, 2026-09-18: "in the government tab, in the revenues vs deficits
 * and all that, it just says 'everything else' - that should be expandable,
 * cause a lot of times that's where a bunch of important things happen." The
 * bridge from the budget balance to the cash change had three named rows and
 * a fourth, "Everything else the treasury did", that was a residual with a
 * name on it: Game.getTreasuryUnexplained(), the change less the three. On a
 * month where the player put capital into the bank or bought reserves, that
 * row was most of the month and said nothing. The bridge's own rule is that a
 * total which quietly absorbs its own gap is worse than no total; a residual
 * that absorbs the player's decisions is the same thing one row down.
 *
 * So the movements are recorded where they happen, in the player's words,
 * signed as the treasury sees them (+ cash in, - cash out), in thousands, and
 * what is left after them - Game.getTreasuryResidual() - is printed as its
 * own line, smaller, still named. Nothing about how the cash moves changes:
 * every record() sits beside a `cash -=` or `cash +=` that was already there
 * (or, since 0.7.0, beside the Game.treasuryPays() that replaced it) - all
 * but one: land paid for out of the vault (0.7.6), where no cash moves and
 * the budget's land line says it did, so the journal carries it back.
 *
 * THE WINDOW IS PRESS TO PRESS, like the bridge's. The month in progress
 * opens where the last one was struck - Game.takeTreasuryMonth(), at the
 * bottom of nextMonth() - and not where treasuryOpening is set at the top of
 * the tick, because the player's own decisions (capital, reserves, buybacks)
 * happen between the two, and a list cleared at the top of the tick would
 * drop exactly the entries the row exists to show. close() snapshots and
 * clears in one breath, so nothing recorded is ever in both months or in
 * neither.
 *
 * SAVED, both lists. The bridge is shown for the month that has ended, so
 * last month's lines have to survive a reload as treasuryOpening and its
 * siblings do; the month in progress is carried too, because a city saved
 * between two presses has already moved cash the next strike will count
 * (the same reason cityCapitalSpending is carried). An old save has neither
 * and loads with both empty, which is the right journal for a city whose
 * movements nobody wrote down - so SAVE_FORMAT does not move.
 *
 * WHICH SITES ARE JOURNALLED, and which are not. The rule: a site goes in
 * only if it is in neither the budget balance (NationalAccounts.getBalance())
 * nor the bridge's raised/repaid rows, because the bridge already reconciles
 * those and a line here would count them twice. Every `cash -=`/`cash +=` in
 * Game.java, as of 2026-09-18 and 0.7.0:
 *
 *   resolveBank() (0.7.14)    JOURNALLED  "Resolved the bank for its shares" - no budget
 *                                         line: the city buys the failed bank's shares (it
 *                                         was "Put capital into the bank", a gift, until
 *                                         0.7.14)
 *   acceptPreferredOffer()    JOURNALLED  "Bought the bank's preferred shares" - no budget line
 *   fundYearEnd(), fundPayIn(), fundDrawOut() (0.7.14)
 *                             JOURNALLED  "Paid into the fund (the dial)", "Paid into the
 *                                         fund", "Drawn from the fund" - transfers between
 *                                         the treasury and its fund, neither revenue nor
 *                                         spending; the fund's withdrawal to the budget IS
 *                                         a budget line (NationalAccounts.getFundTransfer())
 *                                         and is not journalled
 *   buyForeignCurrency()      JOURNALLED  "Bought reserves" - no budget line
 *   sellForeignCurrency()     JOURNALLED  "Sold reserves" - no budget line
 *   repurchaseDebt()          JOURNALLED  "Bought back a bond" - retire() is not a repayment
 *   syncHouseholdAccounts()   JOURNALLED  "Lent to students, net of repayments" - the
 *                                         grant is a budget line, the loans are not;
 *                                         the interest on them (2026-09-21) is a budget
 *                                         line too (NationalAccounts' student loan
 *                                         interest) and is added to the cash there but
 *                                         NOT journalled, for the rule above
 *   chargeBuildingMaintenance() not       a budget line since 0.7.102 (A15,
 *                                         NationalAccounts' cityRepairs). It was
 *                                         JOURNALLED "Repaired the city's own buildings"
 *                                         until then, and named under spending's total,
 *                                         outside the budget's, from 0.7.31: the balance
 *                                         omitted it (measured: the residual was exactly
 *                                         -repairs every month)
 *   settleReserve() (0.7.85)  not         budget lines since 0.7.102 (A15): the reserve's
 *                                         crude bought and sold, a thing bought as land
 *                                         is. JOURNALLED "Bought crude for the strategic
 *                                         reserve" and "Sold crude from the strategic
 *                                         reserve" until then
 *   finalUpdateEconomy()      not         the transit fares and the bill they are set
 *                                         against: budget lines since 0.7.49 (B9). The
 *                                         fares were JOURNALLED "Took in transit fares"
 *                                         until then - in the cash through
 *                                         getTaxIncome(), not in getTotalRevenue()
 *                                         (measured: the residual was exactly +fares)
 *   settleTreasury() (0.7.0)  JOURNALLED  "Advanced by the central bank (printed)",
 *                                         "Repaid the central bank" - financing, like
 *                                         paper raised and repaid, but not paper. The
 *                                         advances' interest and the remittance are
 *                                         budget lines (NationalAccounts' central bank
 *                                         pair) and are not journalled; nor, since
 *                                         0.7.102 (A15), are the arrears it pays down
 *                                         to the businesses - a bill of an earlier
 *                                         month paid in this one, a budget line on the
 *                                         budget's cash basis (NationalAccounts'
 *                                         arrearsPaid), as the students' arrears were
 *                                         always on the grant line; JOURNALLED "Paid
 *                                         down arrears" until then
 *   paySubsidyIfOwed()        not         budget line (subsidies)
 *   buyLandBlock/Parcel()     not         budget line (land purchases) - EXCEPT the
 *                                         part paid out of the vault (0.7.6), JOURNALLED
 *                                         "Bought land with US$... of reserves" at +usd x
 *                                         rate: the budget carries the land at what it
 *                                         cost, and no cash paid that part (Game, LAND IS
 *                                         BOUGHT IN DOLLARS)
 *   retire()                  not         budget line (land purchases, recordBuyback)
 *   buildFor()                not         budget line (land sales, recordSale)
 *   mortgageInvestor() (0.7.11) not       budget line (mortgage insurance premiums)
 *   runPrivateInvestment()'s claims (0.7.11)
 *                             not         budget line (mortgage insurance claims),
 *                                         paid through treasuryPays()
 *   processBuildOrder()       not         budget line (capital spending)
 *   demolish(), demolishShell(), restartShell(), buyOutAndDemolish() (0.7.22)
 *                             not         budget lines (capital spending; a buy-out's
 *                                         ground is land purchases, recordBuyback)
 *   settleConstructionControl() (0.7.22)
 *                             not         the overtime on a rushed site and a cancelled
 *                                         order's refund: capital spending, as the
 *                                         escalation is (a refund negative); but
 *                             JOURNALLED  "Sold a demolition's material to the builders" -
 *                                         no budget line, as a sector's salvage is none
 *   handleTBillLogic() and the four other issues (and the emergency note,
 *   until 0.7.0 retired it)   not         the bridge's raised row (Game.treasuryRaisedSoFar)
 *   subtractCash(), repayForeignPrincipal()
 *                             not         the bridge's repaid row
 *   payForeignInterest()      not         budget line (interest)
 *   finalUpdateEconomy()'s tax take in and lines out (one cash = tempCash
 *   until 0.7.0)              not         the budget's own revenue and spending lines
 *   the government Investor.spend()
 *                             not         no game path reaches it; ShadowBasket and
 *                                         SaveFileCheck use it as a cash tap - and
 *                                         for that reason one of the two payments
 *                                         not routed through Game.treasuryPays()
 *                                         (0.7.0); the other is settleTreasury()'s
 *                                         repayment of the advances, first by rule
 *   setCashForTest()          not         a fixture's hand
 *
 * The two lines marked "measured" were budget omissions, not the player's
 * decisions. They were journalled because the row is honest only if it names
 * what is actually in it; NationalAccounts carries the fares since 0.7.49 and
 * the repairs since 0.7.102, and both record() calls came out with the
 * residual where it was.
 *
 * WHAT STAYS OUTSIDE THE BUDGET, AND WHY (0.7.102, Jerus's A15: "every
 * monthly treasury flow inside the budget's totals; the batch lists anything
 * else that sits outside, and why"). The budget is the city's revenue and
 * spending on a cash basis, with what it buys and sells that is a thing -
 * land, buildings, crude. What is journalled is a claim or a debt changing
 * hands, which Canada's public accounts call non-budgetary - loans,
 * investments and advances - or is not monthly: the students' loans (lent
 * against a repayment; their interest is a budget line), the fund's pay-ins
 * and draws (savings moved; the dial's pay-in once a year, the transfer back
 * a budget line), reserves bought and sold, the bank's shares and preferred
 * (a resolution or an offer, not a month's), a bond bought back and the
 * central bank's advances and repayments (financing, as paper raised and
 * repaid is on the bridge's own rows), a demolition's material sold (once,
 * at the site's end), and land paid for out of the vault (no cash moved).
 */
public final class TreasuryJournal {

    /**
     * One movement: the player's words for it, and the amount in thousands,
     * signed as the treasury sees it.
     */
    public record Entry(String label, double amount) { }

    /** The month in progress, in the order things happened. */
    private final List<Entry> pending = new ArrayList<>();

    /** The month that has ended - what the bridge shows. Never modified in place. */
    private List<Entry> closed = List.of();

    /**
     * Records a movement into the month in progress. A second entry with the
     * same label in the same month folds into the first - one line a name,
     * folded here at write time so nothing downstream has to sum.
     */
    void record(String label, double amount) {
        if (label == null || amount == 0 || !Double.isFinite(amount)) return;
        for (int i = 0; i < pending.size(); i++) {
            if (pending.get(i).label().equals(label)) {
                pending.set(i, new Entry(label, pending.get(i).amount() + amount));
                return;
            }
        }
        pending.add(new Entry(label, amount));
    }

    /**
     * Strikes the month: the month in progress becomes the month that has
     * ended, and a new one opens empty. Called where the bridge's other
     * figures are struck and nowhere else.
     */
    void close() {
        closed = Collections.unmodifiableList(new ArrayList<>(pending));
        pending.clear();
    }

    /** Both months emptied - a new city. */
    void reset() {
        pending.clear();
        closed = List.of();
    }

    /** Last month's lines, in the order they happened. Empty until a month has closed. */
    public List<Entry> lastMonth() { return closed; }

    /** The sum of last month's lines - what the journal explains of the bridge's last row. */
    public double lastMonthTotal() {
        double total = 0;
        for (Entry e : closed) total += e.amount();
        return total;
    }

    /** True when the journal has something to show for last month. */
    public boolean hasLines() { return !closed.isEmpty(); }

    /** The month in progress, for a save and for the harnesses; nothing on a screen reads it. */
    public List<Entry> thisMonth() { return Collections.unmodifiableList(pending); }

    /* ------------------------------- the save ------------------------------- */

    /** Last month's labels, parallel to {@link #closedAmounts()}. */
    String[] closedLabels()  { return labels(closed); }
    double[] closedAmounts() { return amounts(closed); }

    /** The month in progress, the same way. */
    String[] pendingLabels()  { return labels(pending); }
    double[] pendingAmounts() { return amounts(pending); }

    /**
     * Puts both months back from a save. Either pair may be null or of
     * mismatched length - an older save, or a hand-edited one - in which case
     * that month is left empty rather than half-read.
     */
    void restore(String[] closedLabels, double[] closedAmounts,
                 String[] pendingLabels, double[] pendingAmounts) {
        closed = Collections.unmodifiableList(entries(closedLabels, closedAmounts));
        pending.clear();
        pending.addAll(entries(pendingLabels, pendingAmounts));
    }

    /** The currency reform: every amount in both months, at the new unit. */
    void redenominate(double scale) {
        List<Entry> scaled = new ArrayList<>();
        for (Entry e : closed) scaled.add(new Entry(e.label(), e.amount() * scale));
        closed = Collections.unmodifiableList(scaled);
        for (int i = 0; i < pending.size(); i++) {
            pending.set(i, new Entry(pending.get(i).label(), pending.get(i).amount() * scale));
        }
    }

    private static String[] labels(List<Entry> list) {
        String[] out = new String[list.size()];
        for (int i = 0; i < out.length; i++) out[i] = list.get(i).label();
        return out;
    }

    private static double[] amounts(List<Entry> list) {
        double[] out = new double[list.size()];
        for (int i = 0; i < out.length; i++) out[i] = list.get(i).amount();
        return out;
    }

    private static List<Entry> entries(String[] labels, double[] amounts) {
        List<Entry> out = new ArrayList<>();
        if (labels == null || amounts == null || labels.length != amounts.length) return out;
        for (int i = 0; i < labels.length; i++) {
            if (labels[i] != null) out.add(new Entry(labels[i], amounts[i]));
        }
        return out;
    }
}
