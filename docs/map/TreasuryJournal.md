# TreasuryJournal.java - 230 lines · 16 methods · 0 constants · model

`ham/citybuildersim/TreasuryJournal.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> The treasury's journal: every movement of the city's cash that is neither a
> budget line nor paper raised or repaid, recorded by name as it happens, so
> the bridge on the Government tab can open its last row into lines.
> 
> WHY. Jerus, 2026-09-18: "in the government tab, in the revenues vs deficits
> and all that, it just says 'everything else' - that should be expandable,
> cause a lot of times that's where a bunch of important things happen." The
> bridge from the budget balance to the cash change had three named rows and
> a fourth, "Everything else the treasury did", that was a residual with a
> name on it: Game.getTreasuryUnexplained(), the change less the three. On a
> month where the player put capital into the bank or bought reserves, that
> row was most of the month and said nothing. The bridge's own rule is that a
> total which quietly absorbs its own gap is worse than no total; a residual
> that absorbs the player's decisions is the same thing one row down.
> 
> So the movements are recorded where they happen, in the player's words,
> signed as the treasury sees them (+ cash in, - cash out), in thousands, and
> what is left after them - Game.getTreasuryResidual() - is printed as its
> own line, smaller, still named. Nothing about how the cash moves changes:
> every record() sits beside a `cash -=` or `cash +=` that was already there
> (or, since 0.7.0, beside the Game.treasuryPays() that replaced it) - all
> but one: land paid for out of the vault (0.7.6), where no cash moves and
> the budget's land line says it did, so the journal carries it back.
> 
> THE WINDOW IS PRESS TO PRESS, like the bridge's. The month in progress
> opens where the last one was struck - Game.takeTreasuryMonth(), at the
> bottom of nextMonth() - and not where treasuryOpening is set at the top of
> the tick, because the player's own decisions (capital, reserves, buybacks)
> happen between the two, and a list cleared at the top of the tick would
> drop exactly the entries the row exists to show. close() snapshots and
> clears in one breath, so nothing recorded is ever in both months or in
> neither.
> 
> SAVED, both lists. The bridge is shown for the month that has ended, so
> last month's lines have to survive a reload as treasuryOpening and its
> siblings do; the month in progress is carried too, because a city saved
> between two presses has already moved cash the next strike will count
> (the same reason cityCapitalSpending is carried). An old save has neither
> and loads with both empty, which is the right journal for a city whose
> movements nobody wrote down - so SAVE_FORMAT does not move.
> 
> WHICH SITES ARE JOURNALLED, and which are not. The rule: a site goes in
> only if it is in neither the budget balance (NationalAccounts.getBalance())
> nor the bridge's raised/repaid rows, because the bridge already reconciles
> those and a line here would count them twice. Every `cash -=`/`cash +=` in
> Game.java, as of 2026-09-18 and 0.7.0:
> 
>   recapitaliseBank()        JOURNALLED  "Put capital into the bank" - no budget line
>   buyForeignCurrency()      JOURNALLED  "Bought reserves" - no budget line
>   sellForeignCurrency()     JOURNALLED  "Sold reserves" - no budget line
>   repurchaseDebt()          JOURNALLED  "Bought back a bond" - retire() is not a repayment
>   syncHouseholdAccounts()   JOURNALLED  "Lent to students, net of repayments" - the
>                                         grant is a budget line, the loans are not;
>                                         the interest on them (2026-09-21) is a budget
>                                         line too (NationalAccounts' student loan
>                                         interest) and is added to the cash there but
>                                         NOT journalled, for the rule above
>   chargeBuildingMaintenance() JOURNALLED "Repaired the city's own buildings" - the
>                                         Government screen lists it under spending but
>                                         NationalAccounts.getTotalExpenses() does not
> ... (47 more lines in the source)

**Used by (7):** [CentralBankCheck](CentralBankCheck.md), [EducationCheck](EducationCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [LandCheck](LandCheck.md), [SaveFileCheck](SaveFileCheck.md), [TreasuryCheck](TreasuryCheck.md)

## Sections

| line | section |
|---:|---|
| 178 | · the save |

## Fields (state)

| line | field | says |
|---:|---|---|
| 125 | `private final List<Entry> pending` | The month in progress, in the order things happened. |
| 128 | `private List<Entry> closed` | The month that has ended - what the bridge shows. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 116 | 115 | **type** `public final class TreasuryJournal` | The treasury's journal: every movement of the city's cash that is neither a budget line nor paper raised or repaid, recorded by name as it happens, so the bridge on the Government tab can open its last row into lines. |
| 122 | 1 | **type** `public record Entry(String label, double amount)` | One movement: the player's words for it, and the amount in thousands, signed as the treasury sees it. |
| 135 | 10 | `void record(String label, double amount)` | Records a movement into the month in progress. |
| 151 | 4 | `void close()` | Strikes the month: the month in progress becomes the month that has ended, and a new one opens empty. |
| 157 | 4 | `void reset()` | Both months emptied - a new city. |
| 163 | 1 | `public List<Entry> lastMonth()` | Last month's lines, in the order they happened. |
| 166 | 5 | `public double lastMonthTotal()` | The sum of last month's lines - what the journal explains of the bridge's last row. |
| 173 | 1 | `public boolean hasLines()` | True when the journal has something to show for last month. |
| 176 | 1 | `public List<Entry> thisMonth()` | The month in progress, for a save and for the harnesses; nothing on a screen reads it. |

### the save (lines 178-230)

| line | len | member | says |
|---:|---:|---|---|
| 181 | 1 | `String[] closedLabels()` | Last month's labels, parallel to {@link #closedAmounts()}. |
| 182 | 1 | `double[] closedAmounts()` |  |
| 185 | 1 | `String[] pendingLabels()` | The month in progress, the same way. |
| 186 | 1 | `double[] pendingAmounts()` |  |
| 193 | 6 | `void restore(String[] closedLabels, double[] closedAmounts, String[] pendingLabels, double[] pendingAmounts)` | Puts both months back from a save. |
| 201 | 8 | `void redenominate(double scale)` | The currency reform: every amount in both months, at the new unit. |
| 210 | 5 | `private static String[] labels(List<Entry> list)` |  |
| 216 | 5 | `private static double[] amounts(List<Entry> list)` |  |
| 222 | 8 | `private static List<Entry> entries(String[] labels, double[] amounts)` |  |

