# YoungBankCheck.java - 270 lines · 7 methods · 4 constants · harnesses

`ham/citybuildersim/YoungBankCheck.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> A young town's bank through its first ten years with no sector held: the
> harness of DQ1, "young towns borrow ahead of their people and default", and
> since OE1 of the first plants they borrow for.
> 
> WHY: GW1's staffing gate counts a year's draw, so a young town's sectors
> order their first works earlier, and six fixtures held sectors so that their
> banks stood (GW1's notes, section 5: BankCheck 19's town went into
> resolution at month 44). The town below is BankCheck 19's, held nowhere. On
> 0.9.128 its Cement & Concrete was lent $1,328k for a Lime Kiln at month 11,
> 168% of the bank's equity, on an estimate struck at an operating rate of
> 0.97; the kiln ran at 0.52-0.61, was sold at month 58, six months open, and
> the bank wrote off $1,601k: under its 8% minimum for ten months, down to
> 0.7%, but never in resolution. DQ1 measured four ways of holding or pricing
> a lone plant's loan at the bank and took none (docs/queue/DQ1-notes.md):
> each stunted the young towns or did no better over the sixteen AUTO2 seeds.
> The miss was the estimate's: a sector with nothing running weighed its
> first plant at the city's health alone, its staffing read 1 for want of
> posts. OE1 (docs/queue/OE1-notes.md) weighs a first plant at the rate the
> month reads for it (Sector.plannedRate()); on 0.9.162 before it, the town's
> Materials ordered a second plant before its first had opened, weighed at
> 0.85, and its sale emptied the sector for a $2,744k write-off at month 103
> and 19 months in resolution. Section 1's second line asserted that spell
> until OE1; it asserts its absence since, as DQ1's notes left it to.
> 
> What this has to prove:
>  1. The fixture causes the condition: a young town whose first works cost
>     more than its bank's whole equity, weighed by a sector with no plant;
>     and since OE1 no lone plant's default puts its bank under its minimum
>     (Bank.CAPITAL_RATIO). Every spell under the minimum is printed with the
>     write-off that began it and whether its sector was one plant or many.
>  2. With no sector held, the bank stands: it is never in resolution
>     through month MONTHS. The town's people are printed beside it.
>  3. A first plant is weighed at the rate it runs at (OE1): each order the
>     town places in a sector with nothing running is read at the month's
>     start for the rate the order weighs it at (Sector.plannedRate()) and
>     the sector's own (the estimate before OE1, BusinessInvestment
>     .operatingRateOf() of getOperatingRate()), and against the rate the
>     plant ran at while it stood alone in its first RUN_MONTHS. None is
>     weighed further from its run than its sector's own rate, and in all
>     they are weighed nearer: with the old estimate the two are one figure
>     and the second line fails.

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (11), [BankCheck](BankCheck.md) (9), [Game](Game.md) (3), [BuildingManager](BuildingManager.md) (3), [Bank](Bank.md) (3), [Sector](Sector.md) (2), [BuildingsStacks](BuildingsStacks.md) (1), [GameFiles](GameFiles.md) (1), [Founding](Founding.md) (1), [BusinessDebtManager](BusinessDebtManager.md) (1), [BusinessInvestment](BusinessInvestment.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 55 | `YoungBankCheck.MONTHS` | `120` | The young town's first ten years: the months the bank is watched for. |
| 58 | `YoungBankCheck.WEIGHED_BY` | `24` | By this month the town has weighed its first works (BankCheck 19's Lime Kiln was weighed from month 9). |
| 61 | `YoungBankCheck.RUN_MONTHS` | `12` | The months a first plant's run is read over: its first year standing alone in its sector. |
| 64 | `YoungBankCheck.RUN_MONTHS_AT_LEAST` | `3` | ...and the fewest of them that make a run to weigh it against. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 51 | `static int fails` |  |
| 52 | `static PrintStream out` |  |
| 89 | `final String sector` |  |
| 90 | `final BuildingsTemplate building` |  |
| 91 | `final int month` |  |
| 92 | `final double weighed, own` |  |
| 93 | `final java.util.List<Double> ran` |  |
| 94 | `boolean done` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 49 | 222 | **type** `public final class YoungBankCheck` | A young town's bank through its first ten years with no sector held: the harness of DQ1, "young towns borrow ahead of their people and default", and since OE1 of the first plants they borrow for. |
| 66 | 4 | `static void check(String label, boolean ok)` |  |
| 72 | 8 | `static boolean onePlant(Game g, String sector)` | A sector of one plant at most, standing or on site: one borrower, whose failure empties it. |
| 82 | 4 | `static long onSite(BuildingManager bm, BuildingsTemplate t)` | One of `t` on site. |
| 88 | 19 | **type** `static final class First` | A sector's first order, placed while it ran nothing: the rates it was weighed at when the month began, and the rates its plant ran at. |
| 96 | 7 | `First(String sector, BuildingsTemplate building, int month, double weighed, double own)` _(in YoungBankCheck.First)_ |  |
| 104 | 1 | `double run()` _(in YoungBankCheck.First)_ |  |
| 105 | 1 | `boolean counted()` _(in YoungBankCheck.First)_ |  |
| 108 | 162 | `public static void main(String[] args) throws Exception` |  |

