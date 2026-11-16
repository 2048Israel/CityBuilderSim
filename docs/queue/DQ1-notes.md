# DQ1 - Young towns borrow ahead of their people and default

The row: read what lending to a young town's businesses rests on and measure it, mend it, a harness of a young
town's bank, the six GW1 holds released where they are no longer needed, with BA1-B and WN3-Y folded in
(`QUEUE.md`; the brief is GW1-notes §5). Cut from origin/main bdb28a46 (0.9.128).

## 1. Measured first, before the model moved

**The figure.** The ensemble's AUTO2 bank, months under `CAPITAL_RATIO` to month 600, sixteen seeds, rerun on
0.9.128 with the bank's flows as extra columns (`-Densemble.extra`): a median of **2 [0 to 17], in 11 seeds of
16**, 65 months in all, LF1b's baseline (`docs/ensemble/0.9.128.md`) to the month. The same method gives GW1's
figures back (first draw 17 [8 to 30], unsnapped 4 [0 to 15], reviewed draw 2 [0 to 21]).

**What the 65 months are.** Each spell under the line was read against the six months before it: **23 months in
8 write-off spells** (the bank's write-offs) and **42 months in 12 desk spells** (the trading desk's mark on the
city's shares). Without the write-off spells the median is 0. Six of the eight are one story, the young town's
Fabrication Shop (seeds 1, 2, 3, 9, 10 and 11, months 81-89); the other two are a grown city's plants losing
before interest (seed 0 month 299, Heavy Industry's furnaces at -42 to -58 a month; seed 2 month 381, a Lime
Kiln at -7, sold as idle six months open; seed 10 month 290).

**The young town's Fabrication Shop**, seed 2 read month by month (the others match it):

- Ordered at month 21 in a town of 275 people: $586k from the bank, whose equity was 790 (74% of it in one
  loan), and $46k of bonds - "steel worth more shaped than shipped".
- The planner's figure for it was 54.0 a month (65.6 at month 15): all 1,200 units abroad at a net export price
  of 0.083, its 1,260 of steel at the local price 0.005 (the landed price was 0.0077: nobody here bought steel,
  so the local price was the band's middle - `GoodsMarket.landedPrice()`'s own warning), at the operating rate
  0.81 of a sector with nothing running.
- It opened at month 45-47 and made 4 to 14 a month before interest: it ran at 0.51-0.81, its steel cost
  0.017-0.019 (the world's steel up 2.5 times since the order) and its goods fetched 0.047-0.061 (down 40%).
- The 36-month term loan fell due at month 57-59 and rolled with the policy rate at 15-38%: interest went from
  3.9 to 9-16 a month, above what it made. In seeds 3 and 11 its operating income fell to -2 to +1.
- Twenty-four months of net losses, overdrawn: `planDistressRetirement` sold its only plant (months 81-89), the
  sector was empty, a closed borrower with nothing to recover (`retirementRecovery()` 0), and the bank wrote off
  470-810, 86-100% of what it was owed, against the quarter (`LOAN_LOSS_GIVEN_DEFAULT` .25) it prices.

**The six fixtures' young town**, BankCheck 19's (400 houses, wealthy, the rate pinned) with no sector held, on
0.9.128 (a probe, `YoungTown`, month by month):

- Month 9 Manufacturing borrows 1,758; month 11 Cement & Concrete orders a Lime Kiln, $1,328k from the bank
  (equity 790: 168% of it) and $50k of bonds, on a figure of 23.4 a month on $2,812k.
- It was 41 months on site (open at month 52): 7-8 a month of interest and nothing sold, the shortfall desk
  carrying it; the term loan rolled at month 47 (interest 8.4 to 19.4).
- Open, it made +0.3 to +1.3 a month before interest at an operating rate of 0.52-0.61 - staff 0.80-0.89 x
  health 0.86 x freight 0.68-0.80, against the 0.97 its order was weighed at - and the plan's own figure for the
  last one standing was -3.4 to -0.6. ProcessingSector's idle book (`IDLE_MONTHS`, 6) sold it at month 58, six
  months open; not the distress rule, whose count `noteOpened()` had reset (6 at the sale).
- The sector was empty and the bank wrote off 1,601: equity 1,735 to 244, the ratio 0.019 against a target of
  0.165. Wood Products (2,748 by month 21, 8,603 by 58), Materials and Real Estate borrowed beside it.

**What lending to a young town's businesses rests on**, in four:

1. *An estimate* struck at a rate no plant has run at, its feed at the band's middle and the world's prices of
   the month; it came true at a quarter to a twentieth.
2. *A wait*: 23 to 41 months on site, interest and no sales.
3. *One borrower*: 59-168% of the bank's equity in one plant. The capital rule passes it, because its capital is
   Basel's single-factor formula at a loan's loss given default (THE BANK PRICES CONCENTRATION), which takes a
   sector to be many firms that default a slice at a time; a young town's sector is one plant.
4. *The recovery*: when a sale empties an indebted sector, its plant pulled down returns its plot and its scrap
   and the bank writes off the rest - 86 to 100%, where it prices and holds capital for a quarter.

**The desk's spells** (42 months) are not lending: the desk holds the shares of a few companies at the exchange's
mark (`Exchange.markToMarket`), and a young city's marks jump - in seed 2 Wood Products' alternated between
0.0114 and 0.018 a share every other month, moving equity by 400-600 on 500, and a company that fails marks to
0 (Freight in seed 2 at month 173). `RISK_EQUITY` (1.5) is light beside the trading book's weights for small
listed shares. Outside this row; see §8.

## 2. What was tried and dropped

Each form was built, run on the young town (the probe, then `YoungBankCheck`) and, where it got that far, on the
sixteen AUTO2 seeds to month 600 beside main's (`-Densemble.extra`, the bank's flows as columns). The diffs are
kept in the worktree's `target/dq1/` (`target-form.diff`, `pricing-plumbing.diff`), not in the tree.

**The kiln's pass.** A subagent re-ran month 11 term by term: the order cleared its hurdle by 1.4% (23.065 against
22.743). The queue figure had fallen from 30.5 to 24.0 months while orders were held, which cut the builders'
multiple (1.356 to 1.225), the cost and the wait carry (1.725 to 1.486): the wait read 48.3, 39.4 and 50.2 months
at months 10, 11 and 12, and the kiln took 41. The order test is a cliff on one queue figure that falls only
while nothing is ordered. The real miss is the earnings, 23 a month estimated against about -2 realised, through
the operating rate, the feed's price and the world's prices. None of it is a figure the bank can see.

**M3, the lenders carry the plant** (`Game.retire()` keeps a sector's last plant while its lenders would carry
it, `BusinessDebtManager.lendersWouldCarry()`): the young town stays above target, but the kiln stays a zombie.
Over the sixteen seeds the bank was under the minimum for 5 [0 to 55] months, in 13 seeds; the write-offs were 3
to 5 times main's and the cities much smaller (seed 8: 27,141 to 4,106 people at month 600). Rejected.

**M4, the same, only while the plant pays its running** (operating income less property tax at least 0): the
kiln makes 0.9 a month against more than that in tax, so it is still sold at month 58. It reaches no case
measured. Dropped.

**The one borrower: a lone plant's loan held or priced at the bank.** A sector of one plant at most, standing or
on site, is one firm, and its failure empties it; the bank's capital is the asymptotic formula at a loss given
default of a quarter, which takes a sector to be many firms. The add-on was Gordy's granularity adjustment
(Gordy 2003; Gordy and Luetkebohmer 2013, the simplified form: S = min(A L, A^2 C [delta (K + R) - K] / 2K*),
L = 1, delta 4.83, gamma .25), quadratic in the loan over the bank's capital and capped at the whole loss. It was
put in four places:

| form | the young town (BankCheck 19's, nothing held) | sixteen AUTO2 seeds to m600 | why refused |
|---|---|---|---|
| main, 0.9.128 | 10 months under in one spell, lowest 0.7% (the kiln, m58); 5,778 people at m121 | under the minimum 2 [0 to 17] months, 11 seeds, 65 in all; people at m60/120/300/600 1,287 / 2,212 / 4,815 / 20,472; write-offs a seed 2,696 | (the baseline) |
| 1. weighed into the book and priced | Manufacturing's m8 loan of 1,711 (210% of equity) passed at the higher rate, and its add-on put the bank at 3.4-4.2% for m8-16: growth credit frozen, then a desk failure at m115 (equity -4,099 at m121, in resolution) | not run | the bank lends what its own capital rule then cannot carry |
| 2. weighed, and held in the project room with the deal | no month under; 7,233 people | six seeds: towns of about 600 at m60 against 1,200-1,600 (seed 0 24,896 to 3,514 at m600, seed 4 19,622 to 3,733) | every young town's first works costs 2-3 times its bank's equity, so the room refuses them all: the towns halve |
| 3. priced only, at the deal's own share | no month under; 6,736 people | 4 [0 to 27] months, 10 seeds; 7 of 16 towns stalled at about 630 at m60; people 1,048 / 1,745 / 3,986 / 17,626; write-offs 3,591 | the price fails the order test the same way |
| 4. held in the bank's own target (Pillar 2: earnings kept while a lone plant owes) | 6 months under in two spells, lowest 5.5%: Wood Products' slice default at m48 ($1,836k), then the kiln's at m64 ($1,628k, 6.9%); 7,904 people | 2.5 [0 to 37] months, 12 seeds, 120 in all; the spells starting at m81-93 (the young towns' lone works) 7 seeds and 28 months on main, 2 and 5 here; people 1,276 / 2,039 / 3,993 / 22,503; write-offs 3,699 (+37%) | the earnings it keeps are lent on (below) |

The fourth form is the one that cost no growth, and it holds the young town's lone works: main paid out 1,006 to
2,263 in dividends over months 21-92 against the Fabrication Shops' write-offs of 557 to 958, and the form keeps
them. But `lendingGrowthLimit()` reads the ratio against `capitalTarget()`, not the target equity, so the kept
capital raised the ratio and was lent: in the young town Wood Products borrowed 13,550 by month 47 (main 8,603 by
58), nine Wood Pellet Mills at a leverage of 0.94 to 1.0, $744k to $908k a mill at months 38-44 while its
operating income (29.7 to 62 a month) stood under its interest (48 to 71). It defaulted a slice at m48 and took
the bank to 5.5% before the kiln did. Over the seeds the grown cities' spells (desk and core, months 110-595)
rose from 4 seeds to 10. Holding the add-on back from lending too is the room form again.

**The staffing fill at the order.** The estimate reads an empty sector's staff at 1; reading it at the share the
city can staff (`Sector.staffableShare()`) changed nothing: at month 11 that share was 1, and the 0.80-0.89 the kiln
opened with came 41 months later, other works having taken the workers. Dropped.

## 3. What was decided

★ **No bank-side mend** (2026-11-16, the orchestrator agreeing on the four forms' figures). Main's figure, 2 [0
to 17], is the best measured; the row's 17 was GW1's first draw and was gone by its reviewed one. A young town grows
on works costing two to three times its bank's equity, and every honest refusal or price of that loan stunts it;
keeping the bank's earnings is lent on. The other ways: (a) form 4 as it stands, the young towns' spells for the
grown cities' (+55 months, +37% write-offs); (b) form 4 with the add-on held from lending growth, the room form's
stunting; (c) a large-exposure limit on a one-plant sector, which is the limit Jerus withdrew at 0.7.8 ("No limit,
just price it", 2026-09-24) and the room form's stunting again.

The miss is the order's estimate (§7), queued as **OE1**, after SQ1: its files are SQ1's.

## 4. The harness: YoungBankCheck

`src/main/java/ham/citybuildersim/YoungBankCheck.java`, in `AllChecks.HARNESSES` at the end. BankCheck 19's town
(400 houses, wealthy, the rate pinned) on `GameFiles.scratch("youngbank")` with no sector held, 120 months, about
5 seconds. Section 1 is the fixture: a sector of one plant weighs a works costing more than the bank's equity by
month 24, and a spell under `Bank.CAPITAL_RATIO` begins with a lone plant's write-off. Every spell is printed with
the write-off that began it (that month's and the last, the largest sector) and whether its sector was one plant
or many. Section 2 is the premise the six holds were there for: with nothing held the bank is never in
resolution. On this branch (main's model):

    Materials's Construction Materials Plant at month 2, $1578k against the bank's equity of $829k
    lowest ratio 0.0070 at month 59; 10 months under 0.08 in 1 spells, 1 begun by a lone plant
    month 58: ratio 0.0187, Cement & Concrete wrote off $1602k, a sector of one plant
    0 months in resolution; 5778 people at month 121
    YoungBankCheck: all passed

**Red on main** does not apply: with no mend, main's model is the branch's. Its by-hand red is form 4's, the
assertion "no spell under Bank.CAPITAL_RATIO begins with a write-off of a sector of one plant" (dropped with the
form): it failed at month 64 under form 4 (ratio 0.0690, Cement & Concrete $1,628k) and at month 58 on main (0.0187).
OE1 is the batch that can make it pass; it is left for that batch to assert.

## 5. The six holds

GW1's notes §5 named six. Each was tried free, one harness at a time, on 0.9.128:

| harness | hold | now | what the fixture shows |
|---|---|---|---|
| BankCheck 19 | Wood Products, Cement & Concrete, Materials (GW1); Manufacturing (EP1) | **released**, all four | every line of section 19 passes with every sector free; the town is YoungBankCheck's, whose bank is never in resolution |
| HoldersCheck | every sector held, twice (the households' purse at the settle; the sales the sections are written for) | **kept**, tried | the first freed: the two-step clearing's fixture not reached, its laws skipped; the second freed: the line's discount outside 0.0213-0.0300 and eight lines fail |
| SaveFileCheck | a year of the bank paying before the squeeze | **released** to one paying month (the loop kept, WG2's resolve in it) | passes |
| CurveCheck | section 5's run-on to a month where Strict and Standard differ; the steep city's Manufacturing and Wood Products | run-on **released**; the steep city's two **kept**, tried | at m54 inflation 9.03%, Standard 13.55% and Strict 15.68%; the steep city freed: 1.39 points from the 1-month to the 10-year, under the 2 section 7 asks |
| PolicyPreviewCheck | Cement & Concrete (WN3), Heavy Industry (GW1); Freight (LD1) | Cement and Heavy Industry **released**; Freight **kept**, tried | the bank makes a profit to be taxed on at section 6 with both free; with Freight free too it does not, and the minimum wage's two fixtures are not reached |
| BondCheck 7c | Construction, so it owes nothing | **released** (the hold's overload with it) | Construction owes nothing at m24 unheld; 7c's four lines pass |

## 6. BA1-B: lending to orders on site

BA1's question (Jerus): whether lending to orders on site wants a brake of its own. Measured:

- The wait is priced. The kiln was 41 months on site, its interest carried by the shortfall desk at 7-8 a month
  (1,328 lent, 1,601 written off); the order test's carry read 39.4 months at month 11. The Fabrication Shop was
  24-26 months on site.
- The term is not. The kiln's 36-month loan fell due at month 47, five months before it opened, and was refinanced
  at the shortfall desk (interest 8.4 to 19.4 a month); the Fabrication Shop's fell due 12 months after it opened
  and rolled at a policy rate of 15-38% (3.9 to 9-16 a month, more than it made).
- The losses are the estimate's: open, the kiln made +0.3 to +1.3 a month before interest, which no term clears.
- BA1's own town, FloatLinkCheck's (0.9.16: the bank's 1,246 of equity at -3,200 by month 75), was tried with every
  sector free on 0.9.128: the harness passes but its played sale no longer covers its fixed fee, the fixture not
  reached and the sales' comparisons skipped. Its hold is for the sales' size, as BA1 wrote it, and stays.

★ No brake of its own. One would have refused both works and every young town's first works with them (each was
two to four years on site): the room form's stunting by another road. The term's clock running from the order,
not the opening, is recorded here for OE1's reading; nothing is queued for it.

## 7. WN3-Y: works the young town cannot build or run

- **Cannot build.** The wait is in the test (§6), and its cliff is §2's: the queue figure falls only while
  orders are held, and the kiln cleared by 1.4% in the one month it read 39.4. WN3's own case, PolicyPreviewCheck's
  $53M kiln 45 months on site, no longer stops the bank's profit at section 6: its hold is released (§5).
- **Cannot run.** The estimate for a sector with nothing running reads the environment with its fill at 1
  (`BusinessInvestment.operatingRateOf()`, "a plant that does not exist runs at nameplate on paper"): 0.97 for
  the kiln, which ran at 0.52-0.61 (staff 0.80-0.89 x health 0.86 x freight 0.68-0.80). The Fabrication Shop was
  weighed at 0.81 and ran at 0.51-0.81, with its steel at the band's middle (0.005, the landed price 0.0077).

★ Reasoned out here, mended in **OE1**, after SQ1: the estimate is `Sector.estimatedMonthlyProfit()` and
`operatingRateOf()`, read by `Sector` and eight classes in `sectors/` (SpreadPlanner among them), SQ1's files; six
harnesses read it too. OE1's measure is YoungBankCheck's lone-plant spell.

## 8. Outside this row

The desk's spells (§1, 42 of main's 65 months) are the trading desk's marks on a young city's few listed
companies; `RISK_EQUITY` (1.5) is light beside a trading book's weights for small listed shares. Not queued:
nobody has decided on it; it is here for whoever reads the bank's desk next.

## 9. Checks

- Trace: `-Dplaytest.trace` on this branch against `cbs-trace-main-08c8727d` (main has no source change since
  08c8727d): all eight files identical to the byte (2,405 lines each, the borrow file 39). No ensemble: the
  trace did not move.
- Pick: `./batch-check.sh --pick` is ALL (`AllChecks.java` changed: 211 harnesses), review "not needed - no money,
  saves or threading in the diff". Exit 4 on one line: CentralBankCheck "timed out after 1740 s", cut off at its
  23rd founding while 35 harness starts were held for memory (RAM down to 2.7 GB, other batches running). It
  reads none of the files changed here, and the model is main's; run alone (`./batch-check.sh CentralBankCheck`)
  it passed, exit 0. Every other harness passed; the timing assertions were soft, as off the gate.
