# AN1 - notes

*0.8.21, 2026-10-22. The brief is [AN1](AN1.md): Jerus, 2026-10-22, "Fix
hyperinflation first", ahead of XR2; XR1-C ★1 settled the same day, "Keep as
built".*

## 1. What shipped

**The city's goods are struck at a D$ that follows the D$ slowly** (lead (a),
the study's §3.4 pass-through dial). Each month the D$ the goods are struck at
closes `EconomyManager.PASS_THROUGH` - a thirty-sixth - of its gap to the D$,
in logs; every world price (the dock's imports and exports, the band the
city's own goods trade in, the materials in a building's cost) is that rate
times the world's own price level, which still rides in at once. A step in
the D$ reaches the goods in full, half of it in two years and 29% in the
first. The D$ itself, its parity, the vault, the debts and the land office
stay at the D$ of the month: XR1's float is untouched, and none of the old
pull or anchored drift comes back.

- **Where:** `EconomyManager.strikeExchangeRate()`, called where Game set the
  rate before (`startOfMonthUpdate()`, after `world.advanceMonth()`); the
  banner THE D$ THE GOODS ARE STRUCK AT says why.
- **The save:** `DataSave.struckRate`, written beside the traded rate and
  read in the load path; a reform scales it. No `SAVE_FORMAT` bump: a save
  without the key strikes its next month at the D$ itself, which is what every
  month did before AN1 - an old save loads incompletely, never wrongly.
- **Lead (b), the shelf, is not shipped**: alone it does not anchor, and
  beside (a) it adds nothing (§2).
- **The dial's value.** Import prices at the dock pass about 46% of a move in
  a quarter and 64% in a year (Campa and Goldberg 2005, 23 OECD countries); a
  retail price is about half the import and half local distribution (Burstein,
  Neves and Rebelo 2003). Here the shelf passes the dock's price in full (the
  floor is cost times RETAIL_MARKUP, with no local share), so the dial is set
  at the shelf's evidence, not the dock's: about 30% in a year, a 36th a
  month (est.). It is ★1 below: the sweep in §2 shows that a 24th to a 120th
  all anchor the playtest, and a 12th does not.

## 2. Measured first: the variant table

The default playtest, 4,000 months, each run built from main (0ceac7b6 for
"AN1 as built", 01f9bf98 for the rest; the docs pass between them changed
comments only) with one change, run alone on the PC (`LongPlaytest`, about
90 s each). Prices are the price index against founding at the checkpoints
near m600 / m1060 / the end; people at the end; the months the D$ spent at
its 1e9 guard; the findings other than the map's (below).

| run | what | prices | people | at its guard | findings |
|---|---|---|---|---|---|
| 0.8.18 (d2869a5) | the old rule | 4.7 / 15 / 1,036 | 573,349 | - | none |
| main (0.8.19, 0.8.20) | XR1 as built | 13.8 / 12,225 / 2.9e9 | 10,980 | 1,765 months | the spiral's three |
| a12 | (a): every traded price at a D$ x the world's level that closes a 12th of its gap a month | 22 / 531 / 1.2e9 | 6,742 | 670 | MATERIALS outside its band |
| a24 | the same, a 24th | 5.7 / 105 / 31,380 | 594,126 | 0 | - |
| a36 | a 36th | 8.5 / 62 / 13,802 | 657,854 | 0 | - |
| a60 | a 60th | 5.3 / 36 / 8,714 | 618,366 | 0 | - |
| a120 | a 120th | 3.5 / 15 / 3,204 | 516,379 | 0 | - |
| s36 | (a) on the grocery shelf's cost alone, a 36th | 18 / 412 / 3.7e7 | 342,411 | 0 | - |
| b over | (b): a shelf over its target closes FLOOR_CATCH_UP of the gap down, as one under its floor does up | 65 / 698 / 1.7e9 | 38,079 | 994 | - |
| b cost | (b) where the target is the floor alone (no scarcity) | 65 / 14,200 / 2.7e9 | 82,652 | 1,241 | MATERIALS, RESIDUES outside their bands |
| a6 + b | a 6th and (b) over | 26 / 636 / 2.0e9 | 236,810 | 589 | MATERIALS, RESIDUES |
| a12 + b | a 12th and (b) over | 8.1 / 604 / 2.0e8 | 332,863 | 0 | MATERIALS |
| a36 + b | a 36th and (b) over | 6.5 / 45 / 15,959 | 622,640 | 0 | - |
| r12 | (a) with only the D$ smoothed, the world's level at once, a 12th | 13 / 249 / 1.3e6 | 581,229 | 0 | - |
| r24 | the same, a 24th | 4.3 / 149 / 88,022 | 328,303 | 0 | - |
| r36 | the same, a 36th | 8.2 / 57 / 8,853 | 622,530 | 0 | - |
| r36 + b | and (b) over | 4.7 / 40 / 179,326 | 542,843 | 0 | - |
| **AN1 as built** | r36, saved | **8.2 / 62 / 13,374** | **625,815** | **0** | - |

The "a" runs smooth the product (the D$ times the world's level), so under the
world's steady 2% a year the goods sat about 6% under the world's prices for
ever; the "r" runs, and AN1, smooth the D$ alone. The paths are chaotic (r24
ends at 88,022 and a24 at 31,380 on the same dial), so read the table as a
dose and a response, not a ranking. The ensemble's 80 runs (§4) are the
better measure of the shipped dial.

**Why the spiral ran.** The playtest's float, every 12 months from m600
(`-Dplaytest.fx`): from m612 the player's dial fell behind, the real rate
gap's level went from -18% to +33% (its cap is 35%) in four years and the
market stood at +14-31%, so the rate stood 37-58% over parity's trend from
m660 to m720
- and since every traded price followed the D$ at once and parity followed
prices one for one, the price level rose by its traded share of that gap
every few months, for as long as the gap lasted: prices x209 in twelve years
(13.8 at m613, 2,875 at m756). A persistent gap between the D$ and parity
cannot close when everything nominal follows the D$; it becomes inflation.

**Why (a) anchors.** A lag on a level is a lag on a ramp too: while the D$
keeps falling, the goods sit (1 - k) / k months of the fall behind it - 35
months at a 36th - so the faster prices rise, the cheaper the traded goods
sit against parity, which pushes back on the rise. It is the old rule's lag
("0.70 of parity", XR1-notes §3), moved from the D$ to the goods. **Why (b)
does not:** it takes away the ratchet the D$'s swings left in the shelf, not
the ring itself. **Why the shelf alone does not:** the ring runs through every
traded price - the local goods' band, materials, energy - not the groceries'
cost only.

**The map's finding.** Every big city here but a120 ends with "an old works
moved" at m4000 (CL3's layout watch: of the 15 industry boxes standing at
m1001, 4 stand where they were; a district with 4 bottling plants then and 63
now). It is not AN1's and not CL4's: 0.8.19's classes with the same pass-through
(r36 on 37cbfe3f) read it too. Since XR1 the playtest's city ended at 10,980
people and never grew the districts the watch reads; 0.8.18's city did, on
another path, and read nothing. Nothing in the model reads the map. It keeps
LongPlaytest red; ★3 below.

## 3. The harness, and the checks

| slice | by | what | the review changed |
|---|---|---|---|
| the model | Opus | `EconomyManager.strikeExchangeRate()`, its dial and save, Game's call and load path | - (a few lines, cheaper made than briefed) |
| 1 the harness | Sonnet 5.5 xhigh | PassThroughCheck 1-6, CurrencyCheck 4 restated | Section 3's premise was mine and wrong (the whole index under the goods' share: rent leads it); restated to the groceries. Section 4's first wording (the ten-year end gaps) failed on the healthy-vault fixture; restated over the swing's own horizon (below). The slice moved section 4 off a thin vault, whose cover premium parted the twins by another road, before it saw the new figures, and said so. |
| 2 the new reds | Sonnet 5.5 xhigh | the three harnesses AN1 turned red, diagnosed on main's classes and the branch's | A comment's wording. |

**PassThroughCheck** (new, 64 assertions, about 8 s):

1. *The rule on the bench*: half of a step in 24.6 months, 28.7% of it in a
   year, 49.1% in two; at speed one the goods' D$ is the D$ within 1e-14 (the
   exp/log round trip, not to the bit).
2. *The wiring*: a new city's 240 months, 0 wrong to the bit - the first strike
   at the D$, each month the rule on the D$ read between months, the markets
   and the building materials at the struck rate times the world's level.
3. *A step*: twins pinned 10% apart; the goods' ratio follows the rule to 1e-9.
   The price index's share of the step is 29.7% / 60.3% / 55.6% at 12 / 24 / 36
   months against the goods' 28.7% / 49.1% / 63.7%: at a year rent (weight .52)
   has taken 46% of it, groceries 9.3% (asserted: positive and under the
   goods'), meals 28%, luxury 34%. Rent leads because its target is a home's
   structure (materials, at the struck rate) plus its ground, and the ground is
   priced in US$ at the D$ itself (`LandManager`; read, not measured): ★2.
4. *The ring*: twins of a real city on the float, each holding a vault of
   twice THIN_COVER; one buys dollars worth 5% of a year's GDP and sells them
   back 12 months later (the D$ 10.1% weaker in the purchase's month, as
   INTERVENTION_IMPACT says, to 1e-9). Over the swing's 37 months (the round
   trip and the goods' half-life, from the constants) the mean gap between the
   twins: the goods' D$ 0.64% against 3.25% one for one (both twins at speed
   one, the rule before AN1), the price index 0.50% against 0.79%, the D$ 3.03%
   against 3.26% (printed). **The premise's history, plainly:** its first
   wording asserted the ten-year end gaps. On a thin-vault fixture it held
   (the index 0.44% against 3.60%, the D$ 2.78% against 8.73%); on the
   healthy-vault fixture it did not (3.59% against 1.52%, 4.03% against 2.51%)
   while the first three years went the intended way. Two plain loads of the
   base city play 120 months alike to the bit, so every gap is the swing's,
   but ten years on it is the economy amplifying a small difference either
   way, not the ring's echo. So the premise is read over the swing's horizon,
   fixed from the constants, and the ten-year figures are printed.
5. *The save*: the struck rate to the bit, the next month's strike alike in
   the live and the loaded city; a cleared one strikes at the D$; a reform
   scales it.
6. *A real city's counts* (240 months): the goods' D$ moved in 239 of 239
   months; its gap to the D$ median 13.8%, widest 44.2%; more than 1% / 5% /
   10% apart in 225 / 179 / 141 months.

**CurrencyCheck 4, restated** (kind (b), XR1-notes §6; XR1-C ★1, Jerus
2026-10-22: "Keep as built"): "the sale is what takes back the vault's
capacity of the month's new fall, at most that capacity times the month's own
deficit and at most the vault", asserted against the lean's own arithmetic
(`expectedSale()`): US$922.77k of a cap of US$2,698k in the real city's first
defended month. The cap's own premise holds word for word in section 3.

**Three fixtures re-caused** (green on main, red on AN1's model, the labels
and premises word for word):

| harness | what the fixture lost | the remedy |
|---|---|---|
| CreditCheck, the quote is the deal | its curve sat over the 3% floor only because XR1's ring had the rule reading +5.5% at m61; under AN1 the city reads -0.2%, the 5-year quote 2.65%, under the floor `onCurve()` bounds it by (pinning the D$ does not help: 2.03%) | the month struck at the target, the rule's neutral, before the quotes: 3.24% (main with the same strike 3.24%) |
| PolicyPreviewCheck 7 | a pool of claimants: under AN1 the town's rate ends the five years at 0.87 (1.34 on main), imports are cheaper and the businesses hire the pool empty (0 on EI at m61, 208 on main) | the D$ held after founding: 197 on EI at m61, 129 claimants in section 7 |
| InfrastructureCheck, building a road fixes it | the twins differ by two roads but float apart (the road city's rate 1.03 to 4.25, it imports its materials); the city without roads shed its own load (70.3% through) | each twin pinned after its load: 100.0% through with the roads, 69.7% without |

**The suite on the final tree** (`./batch-check.sh`, on f8b707d1 with main at
0ceac7b6 merged): **128 harnesses in 844 s, 127 passed, 1 failed**, the maps
regenerated, Stale 0 firm findings. Main's six reds, one by one:

| main's red | on AN1 | why |
|---|---|---|
| CurrencyCheck 4, the lean | green | restated (above) |
| CentralBankCheck 21, "no spiral" | green | the probe city reaches m600 at 10,631 people and trust 0.88 (main: 3,441, 0.29); its twins run 4.20% (Very strict), 4.77% (Standard) and 6.95% (Very loose) a year over 240 months, the last years 3.65%, 4.22% and 6.43% against a full miss's 7% (main: 14.2%, 17.1% and 20.5% a year, the last years 8.26%, 9.02% and 0.92%) |
| MonetaryCheck, a month's delay against the allowance | green | 0.000 / 0.083 / 0.053 / 0.043 points against 0.100 (XR1: 1.4-2.3): a dial set a month late moves the D$ at once, but the goods take it at PASS_THROUGH |
| HealthCheck, the hunger line | green | the dear city -1.4 points hungrier than the free one against 5 (XR1: +11.5); it held on 0.8.18 while the D$ glided, and the goods glide again |
| SectorBooksCheck, imports by good | green, **by the path** | its gap (the named imported services the statement's imports hold) is still there; this path's Wood Products never stands a works with consumables in the window (★4 below) |
| LongPlaytest, the spiral's three findings | gone; **red on the map's** | "an old works moved", 3 times at m4000 (§2, ★3) |

`--timing`, alone and hard, last: **8 harnesses in 155 s, all clear.**

## 4. The trace and the ensemble

**The trace moves** - every figure a traded price touches, from month 2. The
default playtest on the final model (e0832032, run alone on the PC), against
main's:

| | 0.8.18 | main (0.8.19, 0.8.20) | AN1 |
|---|---|---|---|
| people at the end | 573,349 | 10,980 | 625,815 |
| prices near m600 / m1060 / the end | 4.7 / 15 / 1,036 | 13.8 / 12,225 / 2.9e9 | 8.2 / 62 / 13,374 |
| a year, over the run | 2.1% | 6.5%, in bursts | 2.9% |
| the D$ at its 1e9 guard | - | 1,765 months | never |
| the dial over the run (median, max) | - | 13.4%, 58.8% | 5.0%, 15.4% |
| findings | none | the spiral's three | the map's "an old works moved" (§2) |

The playtest's earth run (`--earth rotterdam`) was not run: earth-1.bin is not
on this PC.

**The ensemble** on the final model (`tools/ensemble/ensemble.sh an1`, 3 at
once, 80 of 80 runs, 10.4 minutes, against `docs/ensemble/0.8.19.md`):
**24/26** (H1 retired, not counted), committed as `docs/ensemble/0.8.21.md`.
0.8.19 read 18/25 against 0.8.17's 23/25.

| criterion | 0.8.17 | 0.8.19 | AN1 | why |
|---|---|---|---|---|
| A1 inflation m240-end, AUTO2 | 2.27%, PASS | 4.47%, 0/16 in band, FAIL | 2.43%, 16/16 in band, PASS | the ring stopped |
| A2 longest spell over 10% from m240 | 7 [1-10], PASS | 30 [9-70], FAIL | 10 [6-31], FAIL: the rule asks every seed under 12 | the median seed's spell is 10 months against 0.8.17's 7; the young city runs warm (m240-600 at 3.94% a year, the info line) |
| A3 deflation spells; final/peak | 0, PASS | 12, one seed at 21.7% of its peak, FAIL | 1 [0-9], 99.9-100%, PASS | |
| S1 a held 0% over the twin | +1.85%, FAIL | +47.4%, FAIL | +2.47% [0.25-5.29%], longest spell 57 months, FAIL | red before XR1 (EN1-C ★1); near 0.8.17's |
| K2 a world shock | 2.88%, PASS | 6.72%, FAIL | 2.90%, -0.08% over the twin, PASS | |
| R3 a held 15% | -2.34%, FAIL | +2.22%, FAIL | -4.01%, 1 above +1 point, PASS | green for the first time since EN1 (EN1-C ★2) |
| F1 ZERO_SLACK findings | 0, PASS | 1, FAIL | 0, PASS | |
| T2 AUTO2's people | 217,480 | 213,330 | 215,378, PASS | |

None of the premises moved.

## 5. ★ For Jerus

- **★1 The dial's value, PASS_THROUGH = a 36th a month** (half of a move in
  two years, 29% in the first). Set from the shelf's evidence: the dock's 64%
  in a year on a retail price half of which is local (§1, est.). The sweep
  (§2): a 24th to a 120th all anchor the playtest; a 12th - the dock's own
  speed - does not (r12 ends at 1.3e6 times founding, though never at the
  guard). Keep it; or a 24th (nearer the dock, r24 88,022 at the end); or a
  60th (slower, a60 8,714).
- **★2 The ground is priced at the D$ itself.** The land office sells in US$
  at the D$ of the month, and a home's rent target is its structure plus its
  ground, so rent - half the index - takes a D$ move faster than the goods do
  (46% of a 10% step in a year against the goods' 29%, PassThroughCheck 3).
  It is a second road of the ring that AN1 leaves alone, and the playtest
  anchors without it. Strike the ground at the goods' D$ too, or keep land a
  dollar asset?
- **★3 The map's "an old works moved"** keeps LongPlaytest red. CL3's layout
  watch, on AN1's grown city: of 15 industry boxes standing at m1001, 4 stand
  where they were at m4000 (district (0,0), 4 bottling plants then and 63
  now). 0.8.19's classes with the same lag read it too, so it is the map's,
  on a city path no playtest has grown since XR1. Queue a map batch to find
  it (a CL row), or take the finding as the watch's to restate?
- **★4 XR1-C's ★2 and ★3 are green** (MonetaryCheck's allowance, HealthCheck's
  hunger line, §3), for the reason XR1 gave them: the D$'s move reaching
  prices at once. **★4 (SectorBooksCheck's imports by good) is green by the
  path only** - the gap is the harness's, and it stands.
- **EN1-C's ★1 and ★2**: S1 reads +2.47% a year over the twin (0.8.17
  +1.85%), R3 is green (-4.01%, one seed above a point).

**Left undone, under Pending:** Trade › The currency has no line for the D$
the goods are struck at, which a real city's median month holds 14% from the
D$ (PassThroughCheck 6) - a screen line for a later batch; the Earth playtest
(earth-1.bin is not on this PC).
