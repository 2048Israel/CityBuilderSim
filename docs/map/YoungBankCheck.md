# YoungBankCheck.java - 161 lines · 3 methods · 2 constants · harnesses

`ham/citybuildersim/YoungBankCheck.java` - generated 2026-11-21 by CodeMap; line numbers are as of that run.

> A young town's bank through its first ten years with no sector held: the
> harness of DQ1, "young towns borrow ahead of their people and default".
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
> So this pins the condition, and the premise the holds were there for.
> 
> What this has to prove:
>  1. The fixture causes the condition: a young town whose first works cost
>     more than its bank's whole equity, weighed by a sector with no plant,
>     and a lone plant's default that puts its bank under its minimum
>     (Bank.CAPITAL_RATIO). Every spell under the minimum is printed with the
>     write-off that began it and whether its sector was one plant or many.
>  2. With no sector held, the bank stands: it is never in resolution
>     through month MONTHS. The town's people are printed beside it.

**Uses:** [BankCheck](BankCheck.md) (9), [Game](Game.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (3), [Bank](Bank.md) (3), [BuildingManager](BuildingManager.md) (1), [GameFiles](GameFiles.md) (1), [Founding](Founding.md) (1), [BusinessDebtManager](BusinessDebtManager.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 38 | `YoungBankCheck.MONTHS` | `120` | The young town's first ten years: the months the bank is watched for. |
| 41 | `YoungBankCheck.WEIGHED_BY` | `24` | By this month the town has weighed its first works (BankCheck 19's Lime Kiln was weighed from month 9). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 34 | `static int fails` |  |
| 35 | `static PrintStream out` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 32 | 130 | **type** `public final class YoungBankCheck` | A young town's bank through its first ten years with no sector held: the harness of DQ1, "young towns borrow ahead of their people and default". |
| 43 | 4 | `static void check(String label, boolean ok)` |  |
| 49 | 8 | `static boolean onePlant(Game g, String sector)` | A sector of one plant at most, standing or on site: one borrower, whose failure empties it. |
| 58 | 103 | `public static void main(String[] args) throws Exception` |  |

