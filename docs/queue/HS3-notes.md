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

★ The five disclosures remain visible in quiet months too, showing zero from
their getters. A zero tax is useful information (the default dividend
integration can produce it), and a quiet estate month keeps the same rows.

## Checks

`./batch-check.sh --fast` passed before and after the production edits, with
Stale 0 firm findings. Final verification:

| check | result |
|---|---|
| final `--fast` | exit 0; compile 3 s, maps regenerated, Stale 0 firm findings |
| `--pick`, serial (`-Dcbs.parallel=1`) | 23 harnesses, 3,489 s: 20 passed, 3 main-known-red harnesses; saved-log native judgment exit 3 |
| final `MoneyWordsCheck`, city's native finance objects and longest caption | exit 0; 36 assertions, 0 failed, 16.4 s |
| KeptCheck, Household money 82–84 | 135 assertions passed, 54 s; kept/fresh and node-retention laws green |
| KeptCheck, the clock on Household money | 0 rebuilds over 3 months |
| KeptCheck, live heap after release | 1,328.8 MiB of 2,048 MiB; the existing 1,536 MiB hard bound passed |

The pick names 21 harnesses; its BankCheck selector also runs CentralBankCheck
and YoungBankCheck. The three red harnesses are already covered in main's
`docs/notes/known-reds.txt`: CentralBankCheck's two whole-par rollover laws
(now HT1-H), KeptCheck's Construction Cash & debt signature and Government Overview
45-of-401 allocation law (now FS5), and Forms1900Check's two lighting/door-count
premises (FS8). All six assertion lines are main-known; no NEW line or
batch-owned red. No threshold or premise was changed to pass these.

The selected run compiled the final production code; subsequent source edits
were harness strengthening and comments, checked by the final words probe.
PowerShell's original wrapper relayed a nonzero native status as 1. Rejudging
the saved pick with explicit native status forwarding returned 3; final
commands preserve `$LASTEXITCODE`. The raw pick is `target/HS3-pick-suite.txt`,
its judgment `target/HS3-pick-judge.txt`, the full Kept log
`target/HS3-pick-KeptCheck.log`, and the final words log
`target/HS3-words-final.log`.

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
The final fixture uses its Game's own exchange, equity register, bank and
constructor-wired treasury receipts, so its asset and tax reads share one city.
It also puts the longest shape/tier caption in the census, opens its stage's
books below the grid, and checks the rendered text retains the whole caption.

For an eye check: People › Household money, open one young and one established
cell of the same shape/tier; the caption and the wealth must follow the stage.
The retired's labels name their former tiers, and the city's month carries
the five estate/tax lines. This is guidance, not pending work.

## The unchanged trace

The default LongPlaytest ran once on branch source `030b791e15c2cdb9ddedef8484d9289588467a99`
and once on a fresh detached checkout of origin/main
`16ee7addeee9d718c7d066674512275e898ddf2d`. Both native wrappers exited 0.
Both used the same JDK and the prescribed earth-2/world-1 files in the main
checkout, `cbs.parallel=1`, and the default playtest inputs. Subsequent edits
only strengthened the words fixture and recorded these results; the model
and production page are those traced here.

All eight CSVs are byte-identical, with no columns excluded or runtime
adjustment. The seven monthly files have 2,404 data rows; borrow has 43.

| CSV | SHA-256, identical on both runs |
|---|---|
| month | `9E9E721730336C27F2CF88C3A4791E9EDDBA89FCBEA3E9520A016B4B74801408` |
| borrow | `9E92FD1A714C422A8983A25AB8AB66EDDD9C08C9A77EF7319E4E82768BF40088` |
| house | `6F49ED59ADA3313521FDBDCC7FF29789E287A5D81F603EC6A97D08E56C271DED` |
| pop | `7F2F26098C71BE360C6060A73958F2BD7EF5B86259E2F1017CC39B5387DDFAD4` |
| bank | `11840A89E7F19336A2CDAF26F6BA53A6C87F16DC84D2C47ED5FF0B50B1665059` |
| bonds | `DB4974AC72E2718F5E6F5FA662552A964E3385FD7591C846AB05F22B1489F284` |
| build | `4956EDA45A44D57B2A7EDC2D015D58F65DAD4F7261809A693315C68E8328859E` |
| labour | `A3173B59BED1524F4C0C0D57DA8E5C016ADECE78BFA630334442290CBA33E242` |

The CSVs are retained under `target/HS3-traces`, with the comparison in
`target/HS3-trace-comparison.txt` and the two run receipts in
`target/HS3-branch-trace-check.txt` and `target/HS3-main-trace-check.txt`.
The fresh baseline checkout was only a named LongPlaytest trace run, never
a baseline suite. Its evidence was copied home before removing that helper
worktree.

The model and trace did not move. No ensemble is owed. The pick is not ALL,
and no publisher file changes, so no dry run is owed. No publisher record,
version or save slot is edited.

## Main integration

Main advanced to `abc8fd4f5` while the final records were being prepared. Its
only changes from the traced base are `QUEUE.md` and
`docs/notes/known-reds.txt`, Jerus's Pending cleanup. No production or harness
source changed, so the source tested and traced above remains the same.
The cleanup folded FS10H into HT1-H and FS5H into FS5; the notes use their
current owners. Merge `7e20cae` integrated it. The saved pick rejudged against
that record exits 3 (`target/HS3-pick-judge-integrated.txt`), and the integrated
`--fast` exits 0 with Stale 0 firm findings (`target/HS3-fast-integrated.txt`).
The final pick still names the same 21 harnesses and says
`review not needed - no money, saves or threading in the diff`.

The provisional ship file retains GG1's marker for the orchestrator's go.
