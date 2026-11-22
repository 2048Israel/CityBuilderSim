# YoungBankCheck.java - 423 lines · 14 methods · 9 constants · harnesses

`ham/citybuildersim/YoungBankCheck.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> A young town's bank against the lone plant it lends for, and the first plant
> that plant is weighed as: the harness of DQ1, "young towns borrow ahead of
> their people and default", and since OE1 of the first plants they borrow for.
> 
> WHY: GW1's staffing gate counts a year's draw, so a young town's sectors
> order their first works earlier, and six fixtures held sectors so that their
> banks stood (GW1's notes, section 5: BankCheck 19's town went into
> resolution at month 44). On 0.9.128 its Cement & Concrete was lent $1,328k for
> a Lime Kiln at month 11, 168% of the bank's equity, on an estimate struck at
> an operating rate of 0.97; the kiln ran at 0.52-0.61, was sold at month 58,
> and the bank wrote off $1,601k: under its 8% minimum for ten months, down to
> 0.7%, but never in resolution. DQ1 measured four ways of holding or pricing a
> lone plant's loan at the bank and took none (docs/queue/DQ1-notes.md). The
> miss was the estimate's: a sector with nothing running weighed its first plant
> at the city's health alone, its staffing read 1 for want of posts. OE1
> (docs/queue/OE1-notes.md) weighs a first plant at the rate the month reads for
> it (Sector.plannedRate()). The first form of this harness played that town
> for 120 months with no sector held and read what happened. FS9: the played
> town moved under it - on main 0.9.181 its bank spends 42 months in resolution
> on its desk's trading (equity $2,732k at month 50, -$500k at month 55, no
> lone plant's write-off behind it), and its Bottling Plant, weighed at 0.458
> where its sector's own rate was 0.803, ran at 0.742 as its freight recovered
> over twelve months - both a played path's and not the laws', outside this
> harness's subject.
> 
> WHAT IS SET, AND HOW: nothing is played to a condition. Every premise is
> caused on BankCheck 19's founding (the same buildStack list), a scratch town
> each, and each threshold is read from both sides against the model's own
> constants.
> 
> Sections 1 and 2, a lone plant against the young bank. The founding is played
> FOUNDING_MONTHS with Cement & Concrete held, so it orders nothing of its own
> and the bank, which has opened its first branch by then, carries the town's
> lending as a book. Cement & Concrete is then given one Lime Kiln by
> buildStack and a loan of LOAN_PER_EQUITY times the bank's equity by
> BusinessDebtManager.issueLoan(), with the loan's proceeds in the sector's
> till (setSectorCash()) and the bank's cash out (Bank.lend(): the month's
> settle does it for a loan written in the month, and the desk's tally of it is
> cleared as a month begins, so a loan set before the month is lent by hand or
> is equity from nowhere). The sector's assets are not set (setAssets()):
> EconomyManager.refreshCreditAssets() restates them from its balance sheet as
> each month settles, and the till is what keeps it solvent. One month is
> played, so the loan stands on the bank's book as the write-off month begins.
> The plant then closes by the model's own
> path: BuildingManager.retire(), BusinessDebtManager.settleRetirement(sector,
> 0, true) (an empty borrower), and the month's default judgment writing off
> what the sector's till did not pay (Game's bank.writeOffSector(), read as
> bank.getWrittenOff()). The loss is sized by the till: the claim is paid from
> it first, the rest is written off, so the till is the loan less the loss.
> Bank.writeOffSector() alone is not used: it moves the month's statement and
> not the book, so it moves no equity and no ratio. Four twins of one founding
> (built the same way, one after another) differ only in that till:
>  - a control, whose till pays the claim in full: nothing is written off, and
>    its month's close is the bank a loss is taken from, with its own income
>    known. A loss takes the lone loan off the book whole (what is paid and
>    what is written off both leave it), so the ratio a loss leaves is read
>    against the control's book, and its cushion is the control's equity less
>    Bank.CAPITAL_RATIO of its weighted book, not the start's, which still
>    carries the lone loan's weight;
>  - (a) a loss of LOSS_UNDER_CUSHION of that cushion;
> ... (35 more lines in the source)

**Uses:** [Game](Game.md) (11), [BankCheck](BankCheck.md) (11), [Bank](Bank.md) (11), [BuildingsTemplate](BuildingsTemplate.md) (6), [Sector](Sector.md) (3), [JobType](JobType.md) (2), [Sectors](Sectors.md) (1), [BuildingManager](BuildingManager.md) (1), [GameFiles](GameFiles.md) (1), [Founding](Founding.md) (1), [BusinessDebtManager](BusinessDebtManager.md) (1), [TreasuryFund](TreasuryFund.md) (1), [NewUnit](NewUnit.md) (1), [BusinessInvestment](BusinessInvestment.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 107 | `YoungBankCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 110 | `YoungBankCheck.FOUNDING_MONTHS` | `12` | The months the young town is played, with only its lone sector held, before that sector is lent for: the bank has opened its first branch and carries the town's own lending, so its minimum stands a distance under its ... |
| 113 | `YoungBankCheck.LOAN_PER_EQUITY` | `2` | What the lone plant's loan is, in times the bank's equity as the founding ends: above the whole of it, and small enough that the largest loss sized below (LOSS_PAST_EQUITY of equity) is less than the loan. |
| 116 | `YoungBankCheck.LOSS_UNDER_CUSHION` | `.5` | Side (a)'s loss, in times the cushion the control's close leaves over Bank.CAPITAL_RATIO: under it. |
| 119 | `YoungBankCheck.LOSS_PAST_EQUITY` | `1.5` | Side (c)'s loss, in times the control's equity at its close: past all of it. |
| 122 | `YoungBankCheck.LABOUR_MONTHS` | `2` | The months section 3's all-held town is played before its first plant is weighed: the labour market has read the town's people (until it has, every tier's fill reads 1). |
| 125 | `YoungBankCheck.RUN_MONTHS_AT_LEAST` | `3` | The fewest months a first plant's rate is read over, standing alone: a run to weigh it against. |
| 128 | `YoungBankCheck.LONE` | `Sectors.CEMENT_CONCRETE, LONE_PLANT = "Lime Kiln"` | The sector with nothing in the founding that borrows alone, and the plant BankCheck 19's loan was for. |
| 131 | `YoungBankCheck.FIRST_PLANT` | `"Wire & Cable Works"` | Section 3's first plant: Manufacturing's wire works, whose posts are more than the young town's people fill. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 105 | `static int fails` |  |
| 106 | `static PrintStream out` |  |
| 188 | `final Game town` |  |
| 189 | `final Bank bank` |  |
| 190 | `final BusinessDebtManager credit` |  |
| 191 | `final BuildingsTemplate plant` |  |
| 193 | `boolean aloneAtFounding` | The founding: what the sector held, and what the loan was set against. |
| 194 | `double loan, till` |  |
| 196 | `double equity0, weighted0, principal0, interest0, ratio0, cushion0` | The write-off month's start. |
| 197 | `boolean onePlant0` |  |
| 198 | `long people` |  |
| 199 | `int failures0` |  |
| 200 | `boolean resolution0` |  |
| 201 | `final java.util.Map<String, Double> last` |  |
| 202 | `final java.util.Map<String, Boolean> wasOnePlant` |  |
| 204 | `double equity, weighted, ratio, writtenOff, principal` | Its close. |
| 205 | `boolean resolution, spell, lone` |  |
| 206 | `int failures` |  |
| 207 | `String cause` |  |
| 208 | `double most` |  |
| 289 | `final String sector` |  |
| 290 | `final BuildingsTemplate building` |  |
| 291 | `final int month` |  |
| 292 | `final double weighed, own` |  |
| 293 | `final java.util.List<Double> ran` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 103 | 321 | **type** `public final class YoungBankCheck` | A young town's bank against the lone plant it lends for, and the first plant that plant is weighed as: the harness of DQ1, "young towns borrow ahead of their people and default", and since OE1 of the first plants they... |
| 133 | 4 | `static void check(String label, boolean ok)` |  |
| 139 | 4 | `static void quietly(Runnable r)` | `r` with the model's own printing muted. |
| 144 | 3 | `static boolean near(double a, double b)` |  |
| 149 | 8 | `static boolean onePlant(Game g, String sector)` | A sector of one plant at most, standing or on site: one borrower, whose failure empties it. |
| 162 | 23 | `static Game young(String label, boolean holdAll, int months)` | BankCheck 19's young town (0.7.x, GW1) founded: the same buildStack list, every sector free to order but the ones held, and `months` played. |
| 187 | 39 | **type** `static final class Lone` | One twin of the lone plant's founding: its readings as the write-off month begins and as it closes. |
| 210 | 6 | `Lone(String label)` _(in YoungBankCheck.Lone)_ |  |
| 218 | 1 | `double loss()` _(in YoungBankCheck.Lone)_ | The loss the month's default judgment wrote off at the lone sector's name: what the sector's till did not pay of its claim. |
| 221 | 4 | `String spellLine()` _(in YoungBankCheck.Lone)_ | The spell line, as the first form of this harness printed it. |
| 233 | 53 | `static Lone lone(String label, java.util.function.ToDoubleFunction<Lone> tillOf)` | A twin: the founding played, the lone sector given its plant and its loan, a month played so the loan stands on the bank's book, and then the plant closed with the sector's till at what `tillOf` reads off the twin's s... |
| 288 | 18 | **type** `static final class First` | A first plant: the rates it was weighed at when the month began, and the rates it ran at. |
| 295 | 7 | `First(String sector, BuildingsTemplate building, int month, double weighed, double own)` _(in YoungBankCheck.First)_ |  |
| 303 | 1 | `double run()` _(in YoungBankCheck.First)_ |  |
| 304 | 1 | `boolean counted()` _(in YoungBankCheck.First)_ |  |
| 307 | 1 | `static String k(double v)` |  |
| 309 | 114 | `public static void main(String[] args) throws Exception` |  |

