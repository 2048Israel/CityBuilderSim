# RT1 - a building counted in its sector's retirement measure (0.9.19, 2026-11-05)

## WHERE IT STANDS

**Ready** (2026-11-05, 11:40). Branch `batch/RT1` from main a0661fe8 (0.9.17). **PriceLevelCheck 8 is green on
the branch** (§2): section 8 to month 103, every real series within 1e-9 (worst 5.1e-13), every live nominal
one (worst 3.9e-10). The slices: a farm and a food plant counted in money (a4aa060e), the bond holder's floor
`Debt.moved()` and the horizon (93b11638), HoldersCheck 14 (5f5f6e22), two fixtures re-caused (fff5ad9f).
**`--pick` (35 of 174): exit 4, red only in the two harnesses passed by name**, CentralBankCheck 21 (call 4)
and ScaleCheck's nine lines (ST3's class, §3); the whole suite (174) adds MapCheck 2d's two lines (DM1's
class, DM2) and DenominationCheck as main's. The trace moves: the ensemble, 24 of 26 criteria (§5). Money
changes (`Debt`): the gate's stranger's review. Calls 1-4 (★) each with a recommendation; RT2 and CB2 in the
ship file's `## queued`.

## 1. What RT1 is

The row (QUEUE.md) and its brief, R0's call 21 (`docs/queue/R0-notes.md`): Agriculture's and
FoodProcessing's spare-capacity measure (`retirementDemandAndCapacity()`) is in money, each good at its
export floor, but `BusinessInvestment.planRetirement()` divided it by `Sector.unitsOf()`, a building's
tonnes of crops or kilograms of processed meat. Money scales with `PriceLevel.FACTOR` and tonnes do not,
so at the founding's level (0.0259) the quotient was 0 and no farm or food plant retired by the
spare-capacity rule in any city; at today's prices one could. R0 measured the mend (519f8e41, Agriculture
only) and queued it with its ripple read.

- **The mend (a4aa060e, Sonnet's slice 1):** `Agriculture.unitsOf()` and `FoodProcessing.unitsOf()`, each
  good the building makes times its export floor, the floors at or under nothing skipped as the measure
  skips them: one building's share of the measure, in the same money, so the quotient is the same at every
  price level. Nothing in `Sector`, `BusinessInvestment` or `Game`. The distress rule reads `unitsOf()` only
  through `<= 0 && !makesWhatItSells()`, which a farm making anything passes as before.
- **The harnesses:** AgricultureCheck 5 and FoodProcessingCheck 7 cause it: eight of the most-held kind and
  four of another, nothing used, RETIREMENT_LOSS_MONTHS of losses. The buildings add up, one by one, to the
  measure (1e-12); the rule sells the most-held kind and the share its constants allow. On the old code the
  sum was 4,276 t against a measure of 32.6 (the farms) and 40,000 kg against 28.2 (the plants), and the
  rule sold 0 of 8; now 2 of 8.
- **How often it fires (LongPlaytest, 2,400 months):** its line "plant retired ... by the spare-capacity
  rule" now names the sellers: Agriculture 1, no food plant, of 1,609 (Retail 1,439); no finding. In the
  harnesses' founded towns it is one of the founding's two Mixed Farms in the first years: FoundingYardCheck's
  village at month 31, MonetaryCheck's town at 41, each of CentralBankCheck's twelve at 25-28 (call 3).

## 2. PriceLevelCheck 8, traced: green on the branch

Main (a0661fe8): section 8 red on one line, Manufacturing's cash at month 86, 5.8e-9 apart (BS1's drift).
The trace below ran the played twin's children by hand (`PriceLevelCheck --twin-played` at
`-Dcbs.priceLevel` the factor, 1, and each nudged by 1e-12) and compared them as `theTwin()` does.

1. **With the farm counted in money, the twins parted at month 70, and the nudge did not move it.** The
   factor and the factor nudged by 1e-12 ran equal (to 1e-9) and both parted from today's and today's nudged
   at month 70 with the same two values: Construction's cash 34,420.6 against 34,402.9 (0.00023), the
   treasury 0.12 apart. By the harness's rule, a price the factor missed.
2. **What it was: a hair of a bond holding, main's latent fault, which RT1's city first reaches.** Every
   field of the city dumped at months 68-70 at both levels: from month 59 one city bond's `firmPrincipal`
   was 4.4e-16 at today's prices and 0 at the factor. `Game.desksBuysFirmPaper()` takes a recall pro rata
   (`take x mine / held`) and took 1.951382221467067 of the businesses' 1.9513822214670675; `Debt`'s
   `Math.max(0, held + face)` kept the ulp. `DebtManager.firmBookRatio()` reads any face above nothing, so
   `OutwardInvestment`'s paper ratio was 0.93 at one level and 1 at the other (its yield 4.9% and the city's
   own rate, 7.2%), and at month 70 the builders bought paper at those two prices (`bondsBought` 1,521.7
   against 1,539.4: the 17.7 of cash). On main's sector code the holdings match at both levels for all 105
   months: the hair needs the farm RT1 retires.
3. **The mend (93b11638): `Debt.moved()`**, every holder's move (households, central bank, businesses,
   world) and the buyback's (`retirePart()`): a remainder within `HOLDING_ROUNDING` (1e-12) of the face
   moved is none. A share of the face, not a fixed floor, so it is the same at every price level (BA1's
   `Household.SHORT_ROUNDING` is its twin). HoldersCheck 14 (Sonnet's slice 2) causes it from both sides.
4. **Then month 104, a tie:** Manufacturing's cash, 4.9e-9 apart, where the factor nudged by 1e-12 parts
   from the factor by 3.5e-9 and today's nudged from today's by 2.1e-9: the nudge moves it, the bond
   search's stop (BS1). By the harness's own rule the horizon goes to the month before: `PLAYED_MONTHS` 105
   to 103, its javadoc saying why (call 1).
5. **PriceLevelCheck on the branch: exit 0.** Section 8, 103 months: 864 series, 552 identical to the bit,
   every real series within 1e-9 (worst 5.1e-13), every live nominal one (worst 3.9e-10), the history's 92
   money series to their rounding, the people to the person. Sections 6, 7 and 9 green as on main.

## 3. The ripple

The whole suite on the model of 93b11638 (174 harnesses; `--pick` reads 31 and none of R0's six): exit 4,
five harnesses red past main's recorded lines, each read by a stranger (three agents, on copies of the
classes: main's sector files, `Debt.moved()` alone, `unitsOf()` alone, RT1). Every one is the farm sale;
the hair's snap alone moves CentralBankCheck's paths and reverses none.

- **FoundingYardCheck 4, re-caused (fff5ad9f).** The founding village sells its second Mixed Farm at month
  31, before the rest is up, so "every building of the village stood at once" never held. The section asks
  what the yard built within TOWN_MONTHS: `villageBuilt()` counts what stands and what the retirement rules
  sold after it stood. Built at month 37 of 44 (a farm and a store sold).
- **MonetaryCheck, re-caused (fff5ad9f).** The farm sold at month 41 in all eight runs, the town outgrew the
  one it kept (850 people against 670), and the 20% rows held a month apart parted by 0.172 against the
  0.100 allowance. Its premise is a steady town; it holds Agriculture as it holds what its question is not
  about (`holdSector()`), and the rows are main's to the digit (10.44 / 9.32 / 7.77 / 7.50%, noise 0.000 /
  0.063 / 0.036 / 0.099).
- **ScaleCheck, nine lines, passed by name: its single calm month, ST3's class.** The playtest's town sells
  its farm at month 19 and is another city: 1,925 people at month 492 against 7,159 with main's
  `unitsOf()` (where 488 still holds every line). 307 months run in ScaleCheck itself, 138 to 2,480: none
  holds every line (the fewest misses one, at 336, section 9's cash). Left at 488 and red, its javadoc
  saying so, as HD2 and LD2 left it; ST3 (queued) gives the section a town of its own.
- **CentralBankCheck 21, passed by name: main's latent edge** (call 4, CB2 queued).
- **MapCheck 2d, passed by name: DM1's class.** Main's four recorded lines are green; two others of the
  same checks are red ("every box stands four months on (500 of them, 7 moved)", "a yard that goes moves
  none of the buildings ... (698 of them, 70 moved)"), the checks DM2's brief lists.
- **The rest:** ShortSellingCheck green (R0's trial had it red); DenominationCheck red word for word as main
  (OB1); LongPlaytest no finding (main's gate recorded one); PriceLevelCheck green. SaveSlotCheck's timing
  line soft, as on main.

## 4. The calls

1. **★ PriceLevelCheck 8's horizon, 105 to 103, by its own rule.** The month-104 parting is a tie the
   nudge moves (BS1's bond-search drift, the same class as main's month-86 red), not a price the factor
   misses. **Recommended:** take it; BS1, once its search stops at a fixed point, puts the horizon back to
   105 by the same nudge. The alternative is BS1 inside RT1 (the corporate issue search in `BondMarket`, a
   money change of its own).
2. **★ The bond holder's floor (`Debt.moved()`) is in RT1, beyond its row's words.** It is main's latent
   fault, but RT1's farm is what reaches it, and without it PriceLevelCheck 8 is red at month 70 by a
   missed price, not by name. It is money (bonds): the gate's stranger's review (`tools/pick.sh` says so).
   **Recommended:** keep it here.
3. **★ Every founded city now sells one of the founding's two Mixed Farms in its first years.** The
   spare-capacity rule as farms first had it (2026-09-14: crop tonnes against crop tonnes; the money
   measure of 09-16 left the farm in tonnes, and since EP1's price level the quotient was 0): losing money
   six months, "price below cost", output ahead of a town of a few hundred (2.98 times demand in
   MonetaryCheck's town at month 41). The town needs the capacity again within months (MonetaryCheck's:
   0.95 of demand by month 44, imports after), and the ground goes to homes (it grows to about 850 people
   against 670). ScaleCheck's town (the playtest's founding, the levers shut) sells its farm at month 19 and
   holds near 2,000 people from month 240, against 7,159 at 492 with main's `unitsOf()`. The ensemble's 80
   cities grow as before: AUTO2's median final population 578,707 against 572,286, all 16 seeds past
   40,000 (month 859 against 878), the lowest seed 303,226 against 451,625. Retail already sells two of the
   founding's stores at month 27 on main. Agriculture's javadoc says a farm "shrinks only on distress"; its
   measure never said so.
   **Recommended:** take it, the row's decision (R0 call 21) and the rule as written. The alternative is a
   measure of null for the farms and the food plants (the distress rule only): price-level-even too, no
   ripple, and the founding's farms kept; a founding-years guard (no spare-capacity sale while the
   founding's queue stands) would be a batch of its own.
4. **★ CentralBankCheck 21 passed by name, CB2 queued.** "...in at least STRICT_SHARE of them, 9 of 12
   (8 hold)": each of the twelve cities sells a founding farm at months 25-28 and runs another path from
   month 26; four reverse (43h/3m +0.320, 40h/4m +0.175, 41h/4m +0.297, 43h/5m +1.272 points), the median
   still holds (-0.348). Measured on four copies of the classes: main 9 of 12, `Debt.moved()` alone 9,
   `unitsOf()` alone 8, RT1 8. STRICT_SHARE's javadoc sets 3/4 on "about one city in twelve" reversing;
   main reverses three of twelve and sits on the line, so any batch that moves the paths tips it about one
   time in three. Re-picking the shapes would move the premise. The reversals cluster where the Standard
   twin ran under about 2.3% a year (9 of 13 such cities, against 3 of 23 above). **Recommended:** pass it
   by name at the gate (main's latent edge, Jerus 2026-11-04); CB2 re-dials the line on today's rate (24
   cities at 2/3: a rule that made no difference passes one time in thirteen; about 80 s more) and asks
   whether dear money raises prices in a city already under its target.
5. **RT2, queued (ship file):** the audit of every sector's measure against its `unitsOf()` found three
   more in two units, none of them money, so none parts a twin: FoodIndustry (spare in heads, a bakery
   counted in kilograms of bread: a Bakery's 66,400 heads against 232,000 kg, so about 3.5 times too few
   mills sold), CoalMining (worked out, its capacity is the washed thermal flow and a coking mine is
   counted in raw tonnes, up to 5 times) and Freight (capacity a three-way minimum of track, sets and
   stations, a line counted by its track). Each sells less than the rule means, never more.
   **Recommended:** queue RT2 as written; not in RT1, whose ripple is the farms'.

## 5. The ensemble

The trace moves (a farm sold), so it ran: `tools/ensemble/ensemble.sh RT1` on 93b11638, 80 of 80 runs, 12.8
min, committed as `docs/ensemble/RT1.md`. **24 of 26 criteria against 0.9.16's 22 of 26:** A3 (AUTO2's
deflation spell and population) and R3 (HIGH's inflation at m660-720 against its twin, 2 seeds above +1
point against 4) pass now; A2 (AUTO2's longest spell over 10%, 14 months [12 to 35] against 18 [10 to 37])
and S1 (ZERO_SLACK's inflation against its twin, 2.84% against 2.54%, longest spell 84 months against 112)
stay red as on main. The money audit failed in no month of any run.

## 6. Records

The ship file `docs/queue/RT1-ship.md`: the changelog block and RT2 in `## queued`. No pending row.
