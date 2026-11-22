# HS3 — the household money page by stage

Built from origin/main `16ee7adde` (0.9.186), for the households study §8,
with §7's accepted calls and BO1/BO1-S, HS1, HS2, HT1 and KP7's shipped model.

## What changed

Household money now gives the young (18-34) and established (35-69) their own
sections of the shape/tier matrix. Each stage opens its own ledger, including
its savings, investments and debt; young keys no longer open the established
cell. The verdict counts the stage cells it actually draws. The retired
remain separate by former tier, and the owner families retain their own line.

The city's month shows estate cash passed to heirs, estate debt repaid and
written off, and HT1's actual tax receipts on realised gains and dividends.
These lines use `HouseholdBalance.getEstatePassed`, `getEstateRepaid`,
`getEstateWrittenOff`, `getCapitalGainsTax` and `getDividendTax` directly.

Two read-only model accessors were missing: `AdultStage.getAges()` supplies
the inclusive age label, and `HouseholdAccounts.statementFor(families,
shape, tier, stage)` supplies that stage's census on the existing per-household
statement. Neither changes stored state, settlement, saves or the month.

★ The stage rows retain the accounts' existing per-household affordability
measure: per-earner wages and per-head/door bills. Their counts and wealth
come from the chosen stage. A stage does not invent a different wage rate.

★ Estate and investment-tax disclosures sit beside the month's wage/spending
statement, with words explaining their scope. Inherited cash is an internal
transfer of existing wealth; HT1 has already deducted investment taxes from
returns. Adding these to wage income or its saving total would count them
again. The estate line says **cash** because the getter counts cash, while
the native flow can also pass investments in kind.

The new sections and rows use Kept scopes keyed by stage, shape or line label;
the young and established have distinct cell keys. No threading change.

## Checks

Initial `./batch-check.sh --fast`: green, Stale 0 firm findings.
Final checks and the eight-CSV comparison will be recorded here when complete.

The first HS3 fixture run matched all five estate/tax amounts but failed its
kept/fresh comparison: it read the kept signature immediately after opening
the stage panels, before their layout had settled. The probe now follows
KeptCheck's `passes()`/`settle()` protocol on both pages; exact signature
equality is unchanged. The corrected run passed all 32 assertions. The first
receipt is retained in `target/HS3-words-first.log` and `target/HS3-words.txt`.
The final probe additionally compares each retired tier's and the owner's
open savings line with its own getter.

`MoneyWordsCheck` now has an HS3 section. Its scratch city's initial census
has both adult stages, retirees of two former tiers, an owner family, an
estate with repayable debt and an insolvent estate. Native death flows cause
the estate lines; actual order-book purchases/sales cause a gain and native
dividends cause dividend tax. Each displayed amount is compared with its
public model getter. It also checks both stage ledgers, words, kept/fresh
parity, retained nodes and the scroller across a real page redraw.

For an eye check: People › Household money, open one young and one established
cell of the same shape/tier; the caption and the wealth must follow the stage.
The retired's labels name their former tiers, and the city's month carries
the five estate/tax lines. This is guidance, not pending work.

No ensemble is planned: the batch adds only presentation and read-only
accessors. No publisher record, version or save slot is edited.
