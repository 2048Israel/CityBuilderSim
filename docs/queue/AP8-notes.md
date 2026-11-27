# AP8 notes — slot 9 at 50x: the households' bond orders and the build quote

Built from `30e99ac31` (0.9.215, format 66). Brief: `docs/queue/AP8.md` (AP7's queued row). No saved field, no format
change, no conservation or save check touched. Model files: `OrderBook.java`, `BondMarket.java` (a harness's hook),
`BuildingManager.java`, `Game.java` (`quoteBuild` only); harnesses `OrderBookCheck` 12, `BondCheck` 9c,
`BuildAdviceCheck` 17.

## 1. What was found

Main's window recording on the slot-9 copy (`main-ui/ui-4866.jfr`, 66.2 s, 3,022 FX samples, 0 truncated), read with
AP6's `JfrAfterProfile`: `BondMarket.postHouseholds` 15.02% of the FX thread, of it `OrderBook.submit` 11.55% and
`OrderBook.rest` 4.43%, almost half of that `MoneyAudit.tolerance` under `samePrice` (2.02%). Every household cell posts
a bid on each bond, priced near the other cells'; `rest()` walked the side from its best order to the first one worse
past BF1's rounding band, computing the band (`samePrice` → `MoneyAudit.tolerance`) for every better order on the way.
`Game.quoteBuild` 8.41%, of it `BuildingManager.abroadOfOrder` 6.85%: read twice for the same arguments, once inside
`orderPriceMultiplier` (3.71%) and once directly for the work abroad (3.14%). `Game.runPrivateInvestment` 11.91% and
`HouseholdBalance.followThePeople` 9.20% were not touched (section 6).

## 2. What changed, and the calls

**The order book's known order** (`OrderBook`, its banner "each side's known order (AP8)"). A transient flag per side:
known from the moment the side is empty (the bond market withdraws every order at its monthly step), kept by a check of
the new order's two neighbours after each insert. Known, `rest()` halves the side to the first order strictly worse
than the new one, raw (`firstWorse`), and the old walk runs on from there (`walk(side, order, from)`). Every order
before that index has a price at least as good and an older seq, so the walk would have passed it: the index is the
walk's own, and the band is computed only past it.

- ★ The walk resumes after the halving rather than the halving giving the answer: inside the band an order at a raw
  better price can stand behind an earlier one (a roundoff improvement keeps arrival priority, BF1), and the walk is
  what decides where it goes. M4 (section 4) shows the halving alone red on section 10's time priority.
- ★ A rested order out of raw order forgets the side's order until it empties. The band is relative to the larger
  magnitude, so at a binade edge (0.5 in the fixture) bids can stand out of raw order; the halving would then skip
  orders the walk stops at. OrderBookCheck 12's binade block causes it.
- ★ A fill, a trim and a withdrawal only take orders off; a split and a reform multiply or divide every price by one
  positive factor, and a correctly rounded multiply or divide never reverses two numbers: all keep the order. A guard
  for a non-positive factor was written and dropped: `submit` refuses a non-positive price and both callers pass
  positive factors, so it could never be shown red.
- ★ Not saved: Gson's load gives `false`, so a loaded side is unknown until it empties; `copy()` (the autosave's
  snapshot) clones the flags with the book.
- In `walk` the raw price comparison is read before `!samePrice`: both are pure tests, so `&&`'s order changes no
  answer, and the band is no longer computed for every better order.

**The build quote's abroad terms read once** (`Game.quoteBuild`, `BuildingManager.orderPriceMultiplier(OrderAbroad,
long)`). `abroadOfOrder` is read once and the multiple struck on those terms by the same `BuildersShort.multiplier`
call; the six-argument `orderPriceMultiplier` now delegates to the new overload, so every other caller strikes the same
arithmetic. `abroadOfOrder` is pure (its caches are idempotent), so the second reading was the first's.

**The harness hook.** `OrderBook.probe` (a plain field, null in play: `rest()` reads it once and nothing more) and
`BondMarket.probeBooks(Probe)` / `bookProbe` (set by a harness, copied onto each book `openBook()` opens). No atomic,
volatile or ThreadLocal; a probe is one city's and one thread's. `bare` walks every side from its best order, as before
AP8; `checked` also walks it so and counts where the two differ.

## 3. The proof

**OrderBookCheck 12**, two books built alike (a held one checked, a bare one walking), each with its own ledger, the
lesson's four steps: warmed (84 rests, all halved); then each writer of the order, one by one, each followed by a
comparison of the two books to the raw bit (every order's seq, owner, side, price, quantity, submitted and month, the
book's totals, every fill) and of every rest against its walk (the fourth step is section 4's mutants): an order; a one-step roundoff improvement within a level (8 and 7 orders there); a fill (21 trades);
a trim (b5's cash cut); a cancel (b7 and s1); the close (`withdrawAll`); the binade roundoff at 0.5 (its fixture
asserted raw, the side forgotten, the next bid at the walk's index, the side known again once emptied); a split by 2
and a consolidation by .5; a reform at .37 (prices) and at 1000 (bond quantities); a Gson load (both sides unknown);
the autosave's `copy()`. Last line: 1,012 rests compared, 1,001 halved, 0 differed.

**BondCheck 9c**, the bond market in play: the harness's 24-month city with a checked probe on every book: 2,748 rests,
all on a side in known order, 0 differed. Saved to slot 10 (the harness's own scratch) and loaded twice, one copy
checked, one walking: they load equal to the bit, every loaded side with orders on it unknown; three months each, the
markets equal to the bit every month (each bond's face and holders, the households' bonds and savings, both books to
the bit), 556 of 557 rests halved, 0 differed. BondCheck 9b (AP6's deferred folds) is unchanged and green.

**BuildAdviceCheck 17**, the quote: House, Convenience Store, Coal Power Plant, Water Treatment Plant; 1, 7 and 600
buildings; 0, 6 and 60 months of queue ahead, with and without work already away: 72 readings, each read twice
(`.equals`) and its one-reading multiple equal to the six-argument one to the raw bit, 72 of 72. Since the merge of
RES3 (section 8) the section reads a town of its own, `ChildcareCheck.town` (it adds depots until the builders' sites
are staffed): 44.0 points a month at the sites, 47 distinct multiples, 60 of the 72 orders dearer and 12 not, 58 with
buildings handed abroad. ★ On `30e99ac31` it read the harness's short city, where all 72 were dearer (39 multiples), so
the fixture was first restated from "some orders dearer, some not" to "more than one multiple"; on its own town it asks
for both again.

## 4. Checks

On the final code (`38097bb27`), each through `./batch-check.sh <name>`, one per call: `OrderBookCheck` green (73 OK),
`BondCheck` green (9b and 9c), `BuildAdviceCheck` green (16, AP7's warrant probe, and 17), `LabourCheck` and
`MonthPostsCheck` green, `AutoBuildCheck` exit 3, its one red main's (`fixture: the saved city turned people away from
general care for its fee`, FS12, in `docs/notes/gate.txt`). `./batch-check.sh --fast` green. `tools/pick.sh origin/main
HEAD`: ALL (259; `BuildingManager.java` has 125 readers); review needed, money: `BondMarket.java`, `OrderBook.java`.

The default `LongPlaytest` with `-Dplaytest.trace`, main `30e99ac31` against AP8 (`ap8a-classes`, the first build, and
`ap8-classes`, `38097bb27`, the final one): all eight equal, so no ensemble.

| trace | main = AP8 |
|---|---|
| t-bank.csv | aa2af5f1316c33dde8ba9366884ce152 |
| t-bonds.csv | fa135be296fe76e98685d040a2d05a33 |
| t-borrow.csv | 36b47896b1f126a8b75ef1102132319d |
| t-build.csv | d2e6afe778756cef8b4c8186b0c316df |
| t-house.csv | 96cf152bb7b38f96e2198749252b88b2 |
| t-labour.csv | e9cf9e0947a832addd388e041eaeccaf |
| t-month.csv | 9989337eb618a85564fa3d65307aeabe |
| t-pop.csv | c6f5e3bacc4ae22a60a9be9775855794 |

Each invalidation removed, one at a time, in a class folder of its own (`mutations/run.sh`; the worktree's source never
edited), the reds it shows (FAILED counts):

| mutant | OrderBookCheck | BondCheck |
|---|---|---|
| M1: a rest out of raw order never forgets | 4: the binade block, and the last line's 1 differed | green |
| M2: a loaded side starts known | 3: the load block, 1 differed | 1: 9c's "each loaded side unknown" |
| M3: an empty side never becomes known | 13: every "known order" line, 0 halved | 2: 9c's played months and three months, 0 halved |
| M4: the halving's index taken, the walk not resumed | 6: section 10's time priority at both units, the roundoff within a level, the binade block | green |

★ M1 and M4 are green in BondCheck: its 24-month city never rests an order out of raw order nor one inside an earlier
one's band, so only OrderBookCheck 12's fixtures, which cause both, show them red. That is the reason the fixtures
exist; the in-play section proves the known order in a real market and on a real load, not those two edges.

## 5. Slot 9

AP6's drivers (`Perf9`, `PausedProbe` at rung 8, 5 s a screen), the slot-9 copy (dojha, m4866, 21,918,046 people) in
the evidence's own scratch, `GameFiles(dir, legacy)`, `-Xmx4g`. In order: main (1) headless and window; AP8 (1)
headless and window; main (2) window and headless; AP8 (2) headless and window. Between AP8 (1)'s window and main
(2)'s, another session's `CentralBankCheck` and an open game were running (CPU 23%, 12.6 GB free); those two window
runs are slow on most screens, so neither pair is clean: read each run beside its neighbour, not across.

Headless, 12 months after 2 warm (mean / median / max ms): main (1) 270.2 / 257.0 / 410.8 → AP8 (1) 263.7 / 254.9 /
384.8; main (2) 248.7 / 244.0 / 354.3 → AP8 (2) 234.5 / 214.7 / 361.9.

| screen (months/s; frame p50/p95 ms) | main (1) | AP8 (1) | main (2) | AP8 (2) | FRAME_P95 |
|---|---|---|---|---|---:|
| Build | 9.2; 75/143 | 7.1; 106/216 | 9.3; 86/150 | 9.3; 64/146 | 99 |
| Land | 9.4; 85/128 | 5.8; 144/313 | 8.0; 122/181 | 9.6; 64/113 | 57 |
| People | 10.0; 26/108 | 9.1; 87/144 | 8.9; 107/139 | 9.9; 20/111 | 34 |
| Services | 9.9; 11/93 | 8.3; 106/173 | 9.5; 99/142 | 9.9; 9/83 | 37 |
| Infra | 9.8; 8/63 | 9.4; 22/137 | 10.0; 10/84 | 9.8; 7/64 | 43 |
| Sectors | 9.8; 17/83 | 9.4; 62/155 | 9.5; 47/116 | 10.0; 15/87 | 65 |
| Govt | 10.0; 10/70 | 9.8; 11/81 | 9.1; 82/158 | 10.0; 10/73 | 36 |
| Finances | 9.9; 16/74 | 9.8; 11/77 | 10.0; 79/120 | 9.4; 19/144 | 32 |
| Bank | 9.8; 9/69 | 9.8; 10/83 | 10.0; 21/114 | 9.9; 16/97 | 42 |
| Trade | 9.9; 14/90 | 10.0; 11/89 | 6.8; 143/180 | 9.5; 76/134 | 20 |
| Policy | 9.8; 11/69 | 9.4; 15/125 | 9.2; 103/160 | 9.9; 78/113 | 32 |
| History | 10.0; 16/80 | 9.8; 29/114 | 7.6; 127/173 | 9.8; 58/112 | 60 |
| Rankings | 10.0; 13/73 | 9.8; 62/128 | 8.1; 125/176 | 9.6; 62/135 | 60 |

The laptop's load moves the window figures more than the change does; the headless month, 2-6% shorter in both pairs,
and the profile's shares (section 6) are the readings to trust. Every p95 still fails its budget; Build and Land stay
below 10 months a second (AP8 (2): 9.3 and 9.6).

## 6. The profile after; the next cause

Read once offline: AP8 (2)'s recording `ap8b-ui/ui-4866.jfr`, 66.4 s, 3,307 FX samples, 0 truncated; the same reader,
`-Xmx256m`, one processor, Serial GC, exit 0 (`ap8-profile/profile.txt`). Against main (1)'s:

| inclusive (% of FX) | main | AP8 |
|---|---:|---:|
| BondMarket.postHouseholds | 15.02 | 13.85 |
| OrderBook.submit | 13.60 | 11.40 |
| OrderBook.rest | 5.69 | 2.60 |
| MoneyAudit.tolerance (leaf) | 2.88 | below 0.54, the last leaf listed |
| Game.quoteBuild | 8.41 | 4.72 |
| BuildingManager.abroadOfOrder | 8.60 | 4.84 |
| AutoBuilder.pass | 28.09 | 25.58 |
| WorksValue.relief | 20.91 | 19.02 |
| Game.runPrivateInvestment | 11.91 | 13.82 |
| HouseholdBalance.followThePeople | 9.20 | 10.61 |
| Household.bondFace | 4.43 | 5.59 |

What is left under `OrderBook.submit` is the clearing: `BondMarket$Settle.settle` 3.36% and `Settle.capacity` 2.54%
(`BondMarket.cellOf` → `HouseholdBalance.cellByKey`, a `HashMap` on the cell's key string, 1.78%), and `rest`'s
neighbour check 1.48% (its first touch of the orders beside the new one).

★ Queued **AP9** (id checked free by `tools/batch-id.sh` on `origin/main` `30e99ac31`), brief `docs/queue/AP9.md`:
private investment 13.82% (`NewUnit.standingWhile` → `EquipmentFit.priced` 5.29%), `HouseholdBalance.followThePeople`
10.61% (`carryTheFlows` 6.71%; `Household.bondFace` 4.84% beneath it, of which `Integer.valueOf` 4.39%: a boxed bond
id on every read), the bond clearing's `settle` and `capacity` (with `cellOf`) about 6%. No AP9 code was built.

## 7. Evidence

`C:/Users/Jerus/AppData/Local/Temp/cbs-AP8-evidence/`: `scratchpad/original` (the slot copy, md5s equal to AP7's),
`measure.sh`, `trace.sh`, `drivers/`, `JfrAfterProfile.java` (AP8's targets added), `main-classes` (30e99ac31),
`ap8a-classes` (the first AP8 build), `ap8-classes` (38097bb27), `main-trace/`, `ap8a-trace/`, `ap8-trace/`,
`main-months.txt`, `main-ui*`, `main-profile/`, `mutations/` (`run.sh`, each mutant's diff and logs, `obc-runs.txt`),
`probe-*` (the first probe run). No player's slot was opened or written.

## 8. The merge of RES3 (0.9.216, format 67)

REV-AP8 (Codex): SHIP on `8d1ceae19`, no mends. Main moved to `6bd00e350` (RES3) and was merged at `f8e410d52`; only
generated docs conflicted, taken from main and regenerated. The trace again on both, the new main's classes built from
`git archive 6bd00e350` (`main3-classes`) and the merge's (`ap8m-classes`): all eight equal.

| trace | main `6bd00e350` = AP8 `f8e410d52` |
|---|---|
| t-bank.csv | 6ede455267023c0721bbc430e5d85c6e |
| t-bonds.csv | 31d688038b45d088318307ab47345251 |
| t-borrow.csv | ad17225a89ac4910256628b34f9e707b |
| t-build.csv | 6cff5eaf4b95e1170e9cf4698b1fcaf4 |
| t-house.csv | 66a37acc01890e7c405dff9c6eacebd6 |
| t-labour.csv | f000ef90f546b8ed4658e078784bfed0 |
| t-month.csv | 9de355db849f31706f8b4dc4c93dbdcb |
| t-pop.csv | 86d0749981733a5339b1dc2e1a33a91b |

On the merge `OrderBookCheck` and `BondCheck` are green (9c: 4,925 rests in the played months, all halved, 0 differed;
three months after the load, 747 of 747 halved, 0 differed). `BuildAdviceCheck` 17's first fixture went red: RES3 gives
a door one household, so the short city's 900 Houses no longer staff its builders' sites (`sites()` not above 0, the queue ahead
measured in months of them collapsed to none). RES3 moved section 16 to a town with the doors its posts need for the
same reason; real builders without crews have no sites, so the check, not the model, moved: section 17 now reads its
own `ChildcareCheck.town` (`0455473d6`, harness only), and is green, its fixture asking for orders on both sides of the
threshold again (section 3). `--fast` green. AP9's id checked free again on `6bd00e350`.
