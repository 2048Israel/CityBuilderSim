# GW1 - the gate counts the draw (0.9.89, 2026-11-10)

The brief is GW1's row in `QUEUE.md` and [the staffing-gate study](../studies/spec-staffing-gate.md) §5-§6, its four
calls settled on their recommendations by the orchestrator (BP1, 2026-11-09): C1a count the draw, C2a a 12-month
window, C3a WG2's aim as Jerus took it, C4a GW1 before WG2. Lane B, after WS1 (0.9.86). A model batch: Opus, with
Sonnet slices for the code.

## 1. The rule

- **Beside each band's spare workers, the gate counts the workers migration would bring for the order's own posts
  within `Sector.GATE_DRAW_MONTHS` (12) months** (`Migration.drawFor()`). The posts raise migration's target by the
  jobs' draw (`JOB_WEIGHT` x the posts x last month's residents a job x the senior, rent and crime pulls, as
  `monthlyNet()` builds the target). Only the part of the gap the order makes positive is counted
  (max(0, gap + addition) - max(0, gap), the gap being the target less the people), since `monthlyNet()` brings
  nobody while the people stand over the target. A gap closes at `ARRIVAL_RATE` x last month's crowding a month,
  so a year closes 1 - (1 - that)^12 of it. The people so drawn are cut to the doors last month left free after its
  own arrivals (`lastRoom` less `lastArrivals`), the room `monthlyNet()` caps arrivals at. The city's workers per
  resident turn people into workers, and they split by band as `composeArrivals()` splits a month's arrivals, with
  the order's posts added to each band's open posts.
- **At the going wage.** The draw reads no band premium: it lands in the diploma band, and in the university band
  only through the professions' licences. A band's premium is priced on the posts the gate refused (LD1,
  `WantedPosts.pricedOn`), so read here it let a refused order's own posts raise the wage that drew the workers
  that passed it (the review's loop; WantedPostsCheck 7 had to put the wage back at its base to repeat a hold).
- **Each band takes its spare first, then the drawn workers carried down from its own band and the bands above.** A
  drawn worker may take a post at or below their band, never above, so a building whose graduate posts are more
  than a fifth is still refused for them.
- **GW1's first draw** counted every order's whole addition, whatever the gap and the room: YearBookCheck's
  city had 7,738 posts against a labour force of 6,326 at month 60.
- **The share stays at 80% and the band test is untouched** (a band nobody here can fill and no migrant comes for).
- **One saved figure more: `lastRoom`**, the 17th of migration's last-month scalars (`LAST_SCALARS`, saved since
  0.7.27), written -1 for "no census" since DataSave's Gson cannot write NaN. A save from before GW1 has sixteen,
  and `Migration.restore()` reads them and the mixes with the room NaN, which `drawFor()` reads as no cap: the
  tolerant read WG1, GD2, GD3 and WS1 use for an appended field, not conversion code, so there is no format bump.
  GW1's first version refused such an array whole, and its history, streaks, months recorded and month reset on
  load without a word (the gate's review); SaveFileCheck's migration section now cuts a saved array to the sixteen
  and asserts every other slot back as saved. The
  year's arrival mix of a 1900 city is passed in (`ArrivalMix.at(game.getMonth())`) rather than read from
  `Migration.yearMix`, which Game pushes only at the month's migration step (after the planners) and never saves:
  read from the field, a loaded 1900 city's first planners would have read none and parted from its twin
  (StaffingDrawCheck 6 asserts the twin).
- **A share the draw filled is snapped to `Sector.DRAWN_SHARE_GRID` (1e-9).** Where every band's chance is whole
  (`Migration.opportunity()` at 1) the draw grows with the posts, so two templates whose posts only the draw fills
  share alike but for the last bit, and the bit is the price level's. Construction keeps the held template with the
  larger share: DenominationCheck's twins held a Construction Depot in one and a Builders' Yard in the other (game
  month 123), the unskilled wage parted by 3e-4 the month after through the posts WantedPosts prices (LD1), and the
  reformed city was 4e-6 off its twin a year on. Snapped, the tie is a tie at every price level and the first template
  keeps it. A share the spare alone filled is the ratio of counts it was, unsnapped (StaffingDrawCheck 2 asserts it to
  the bit). The same family as BA1's `SHARE_ROUNDING` (a Machine Works against a Fabrication Shop).
- **No brake yet.** Main has no WG2 brake, so the draw reads the pulls as they are: the study's `draw12` row. WG2's
  row already says its brake and its pull rule apply to `drawFor()` too.
- **Who follows.** Every reader of `Staffing.share` (every planner, `staffableCount`, the build card, the advisor,
  WantedPosts' choice of hold) reads the gate's share. `Staffing.shareNow` keeps the spare-only share and
  `Staffing.drawn()` the drawn workers per band.

## 2. The words

- The hold: "the city could staff 31% of a Blast Furnace now and 64% with a year's arrivals; it wants 80%", or
  "..., now or with a year's arrivals; ..." when the two round equal. One sentence (`Staffing.why()`), so the build
  card's gate, the sector screen's "so" line and the planners' hold reasons all say it.
- The build card's posts line: "the city could staff 31% now, 64% with a year's arrivals" (or "all of it with a
  year's arrivals"). `BuildCard.Figures` gains `staffableWithArrivals`; `staffable` is the spare alone. An office's
  bar of the posts it could not staff reads the gate's share, so the bar and the gate agree.

## 3. The checks

`StaffingDrawCheck` (new), each fixture causing its case:

1. A village with room (crowding 1): a Sawmill the spare fills 43% of passes at 89% on 21.1 drawn workers, the
   total equal to the formula from migration's getters.
2. No room (the city's people far past its doors, crowding 0): nothing is drawn, the share is the spare's to the
   bit, and the order the spare refuses is refused.
3. The pulls (main's stand-in for WG2's brake, which will add its section): rent at twice the target halves the
   draw, crime cuts it by its pull, and at ten times the rent the order that passed on the draw is refused.
4. The Shared Services Centre in a town with no graduates: 17% now, 30% with the draw (its diploma posts' share
   exactly), refused by the share; with university arrivals held closed, the band test refuses as before. The
   college wage bid up (reach 0.33) draws the gate the same workers to the bit: the gate reads no band premium.
5. The words name both counts, and one where the draw is nothing.
6. A loaded city's gate is its twin's, to the bit, for a default city and one founded the 1900 way (whose twin's
   Migration holds no year's mix), with orders cut to the room left among them; the room rides the save, NaN too.
7. A played village, 120 months: of 12,240 asks the draw opened 5,721, the spare alone passed 1,737 and 4,782
   were refused; in a month of no room it opens none.
8. The gap's positive part: a 1900 city at month 19 stands 31 people over migration's target; 38 of its 102 orders
   add less than that and draw nothing to the bit, and the Hydrocracker's 35.5 draws only the 4.4 it makes
   positive (1.86 workers, where its whole addition would have drawn 14.88).
9. Within the room: an order whose uncapped draw is more than the doors last month left free (622.7 of room, 220.8
   taken by its arrivals) draws the 401.9 left, an order under it is not cut, and a city with no census is not cut.

## 4. The harnesses that named the gate

Run on the gate alone (slice A), nine of the 25 harnesses that read the gate went red. Each was read for a premise of
"only the spare". None was a finding against the rule; each fixture was made to cause its condition again, and no
assertion moved:

- **WantedPostsCheck** caused its holds in a village with room, where the draw now opens them: they are caused in a
  city with no room (`noRoom()`, crowding 0), where the gate is the spare's to the bit. Its reprice claim is main's,
  caused there too, and it holds in a city with room as well (the share 0.7977 before and after the reprice, to the
  bit, while the college premium moves from 1.0000 to 1.0090): the gate reads no band premium. Its repeat hold is
  main's section 7 again. GW1's first version asserted on `shareNow` instead, which hid the loop (§1).
- **InvestCheck** (BA1's "the second store fails the test") keeps main's claims, caused in a city with no room. The
  first version of GW1 asserted on `shareNow`.
- **BuildCardCheck 6** causes the card's staffing gate on a Shared Services Centre (210 of 300 posts graduate, which the
  draw does not bring) instead of a Rail Terminal the draw now staffs.
- **RefineryCheck** holds every sector but Refining, as its fixture held the ones it named before the draw let the rest
  build. **PrizeCheck** hands the lab village its college heads (`LAB_COLLEGE_HEADS_MORE`, 200).
  **PolicyPreviewCheck** holds Heavy Industry (a Blast Furnace ordered at month 60 sank the bank's profit) and causes
  the not-joined market by handing the city unskilled workers.
- **BankCheck 19** holds Wood Products, Cement & Concrete and Materials beside Manufacturing: their works came earlier,
  and their defaults put the town's bank in resolution (month 44), where `Bank.branchesToClose()` is 0 by its rule.
- **LabourCheck** runs the doctor-short city on until its labourers are paid the going rate (its unskilled posts
  outran them for ten years). **HousingCheck** places the held dial's glut (homes for twice the seekers) where its
  city's people used to leave under the 15% dial: with the draw they stay and grow.

Run on the whole of GW1 (the gate and its snap), 21 more harnesses went red beside main's recorded reds. Read the
same way, each fixture was made to cause its condition on the draw's path, with the numbers in a `GW1:` comment:

- **SaveFileCheck** ordered the warned city's depots from the first city's template. A stack keeps the instance it
  was ordered with and `BuildingManager.stackOfKey()` finds it by identity, so the month the draw put the depots in
  the planners' first group, `plan()` threw. The harness orders from the city's own template, as the game always
  does. Its bank (in resolution from month 151) is waited for until it has paid a year running before the squeeze,
  and the month's hot money is placed (40, the order the loop's months brought before), since the history's world
  rate (23% at months 227-242 against the city's 3.6%) leaves no spread for any to arrive for. It is placed through
  the raw indices of `CapitalFlows.toSaveArray()` ([0], [3], [16]), which a reordering would move silently
  (queued, low: `HZ1`).
- **CurveCheck** holds Manufacturing and Wood Products (a plant on a bank loan before month 26 had the bank at the
  central bank's window and the curve flat) and runs on to a month where Strict and Standard advise apart (the
  founding was deflating at month 54). **PassThroughCheck** holds every sector in both twins, as its `month()` says
  nobody builds. **MonetaryCheck** plays the founding on until the groceries shelf has moved under a point a month
  for a year (`SETTLED_MONTHS`), then runs the dial rows over the same 36 months: the shelf based at month 38 fell
  20-28% as the food came ahead of the people, and the window measured that, not the dial. On the first draw that
  was month 59 and every line held; on the reviewed draw it is month 46 (503 people), and one fixture line is red
  (below). **CurrencyRegimeCheck** waits for a price rate read before it pegs, and pegs its climbing
  town in the one run of months its own path keeps in the band: a probe of the same town (`climbMonth()`) finds months
  100-111, the only run of 200 played long enough for the eight months the doubt's money gathers and the three after
  the revaluation a break can use (`DOUBT_CRAWL_MONTHS`, `BREAK_LOOK_MONTHS`). The young town's groceries swing as
  migration brings it from 2,500 to 5,500 people (0.69 of their base at month 56, 0.92 at 80), so its smoothed
  inflation passes through the band in short spells. The window is narrow: of nine seeds pegged at 100, two break in
  it. GW1's first version held the smoothed inflation on the target before each month; the review found that an
  overwrite of the model, and it is gone.
- **BondCheck** 7c runs on a city of its own with Construction held (it borrowed in the young town). **FloatLinkCheck**
  hands the driven index over at the linkers' base (prices fell 3.9% after they were sold). **GdpCheck** hands the
  town back the cost of the sheds it places. **HoldersCheck**, on the reviewed draw, is
  main's but for three holds. Section 6's reloaded twin holds the sector the live city holds (a hold is the harness's
  and is not saved, and with Heavy Industry unheld in the twin the households' savings parted, 686.9 against 303.3, a
  month after the load). Section 9 holds every sector (the month between sale and settle took the households' spare to
  0.00). The reopening fixture of section 12 holds every sector (unheld, the first sale is 31.06 of face, the
  households 0.00, the bank 31.04; held, 367.55, the households 279.32 and the bank 88.19, 24.0%). Its four fixtures
  that "the bank took part" ask for over `MATERIAL_PART` (1%) of the face: on the first draw the bank's 3.2e-14 of a
  sale passed them as rounding. The first draw's savers' wait, two-year sizing and six-point reopening are gone.
- **AutoBuildCheck** turns the pass off while its clinics open. **FittingCheck** prices bread to put a Bakery between
  the two drives, and the coal at half the break-even. **OilCheck** places two Grain Farms. **YearBookCheck** holds
  every sector (the posts had outrun the workers and nobody was out of work). **HealthCheck** adds two clinics at the
  drain (the free twin peaked at 25,609 people against 25,000 places). **DeathRecordCheck** reads the children with
  the out of work at the wave's peak (the city had taken them all back into work within the two years).
- **MapCheck** 2c and 2h cut their blocks from the founded ground, and its land-office claims are read on a map the
  city fits at the opening zoom. **HomeRailCheck** plays the railway's share on to fractional (60 months, not 12).

Run on the draw the review asked for (49c2d522), eleven went red. Six are restated here, with CurrencyRegimeCheck,
which the review sent back; five stay red by name (below), MonetaryCheck among them:

- **CentralBankCheck 20** and **BuybackCheck**'s played city hold every sector: their subject is the paper, and the
  businesses' earlier borrowing took the treasury's surplus (D$-18.8k at month 72 against a slice of D$5.2k; held,
  D$81.5k) and stood the savers' rate over the callable line's clearing yield (6.64% against 6.584%, so the
  households bid nothing and the bank took D$10,322k of 10,328k; held, 12.816% over 6.60%, the households 16,347k).
- **ScaleCheck** reads its free fixture at month 191, not 175, where the town stood on a plateau (10,538 people, no
  arrivals, no apartment ordered). The copy and the city still draw in proportion (at K = 1,000,000 the draw
  62,635,761 against 62,806,220; the room 5.041e9 against 5.075e9). Of months 189-200, 191 and 196 hold every line.
- **PopulationCheck**'s decade holds the landlords: they built 1,510 homes, and the claim's target counts the homes but
  not the rents' pull, which priced the filled city (1,819 at month 67) back to 1,457. Held, 1,138 against 1,201.
- **EducationCheck** 1-2 hold every sector: the bare city grew to 87,053 (main's 40,781), its licensed doctors
  arriving with the people (1.449 graduates to one licensed, the claim's 1.5). Held, it stays the fixture's city.
- **HoldersCheck** and **CurrencyRegimeCheck**, above.

**Red, and not GW1's to mend.** Each is a fault of main's that GW1's path is the first to reach, or a class main
already records, and each claim fails while its fixture holds:

- **DeathRecordCheck**, "the orphan section does not fill behind it": the closed town's orphans peak at 27.7 against
  its open twin's 21.5, where the claim allows one more. The family builder places children at half of what is
  possible a shape (`FamilyModel` `take = possible * .5`, a bare literal) and leaves about one child in seven, as its
  class note says. The wave had kept the closed town's children out of the builder (1.3 orphans); when the city took
  all 319 back into work in one month, their 149 children came back to it together. Then the family memory
  (`REFORMING_EACH_MONTH`, .01) kept 99% of last month's households, which already held every adult, so the
  leftover children waited months for an adult free to take them. On main the wave never ended inside the two
  years, so the claim never saw its end. Queued (`OX1`).
- **HealthCheck**, "its old first" (the old die at 101.39 against 101.37 a year per 1,000) and "the households who
  skipped a bill ate with it" (the dear city 5.9 points hungrier: -9.4 priced out of a basket, 15.3 short on the
  shelf): the beds' fixture, main's recorded red, now holds, and these claims run for the first time on a city they can
  be read on. The fixture's drain zeroes savings and shuts credit, so a cell can spend its income after rent and a
  basket and nothing more (`Household.affordCare`); every cell either clears fifteen times the bill or cannot clear
  one (coverage 85.69% against 85.57% on the first draw). The claims then compare the twins' noise, their size and
  their shelves. Queued (`HX1`): a drain that leaves cells between one bill and fifteen, and a fixture line that the
  dear fee turns away a material share.
- **MapCheck**: main's recorded lines (a yard laid across a district's buildings, and the layout watch beside it),
  with new numbers (53 of 590 boxes under the yard, 60 gone), and "the highways reaching a street" (86 lie across, 87
  gone), the same class: DM2's.
- **PlanCheck**, **SlopeCheck** and **PriceLevelCheck** went red on the first draw (a shore street crossing an estate
  cell's line at month 121; a purchase of 384 plots moving 10 boxes at months 610-611; the twins parting at month
  105). On the reviewed draw all three are green, so no row is queued for them.
- **MonetaryCheck**, "...and a month's delay in the hand moves them less than the allowance" (a fixture line): the
  town the settle finds at month 46 inflates 22.95% / 20.68% / 17.90% / 16.19% a year at the four dials over months
  46-81, and the same rows a month late differ by 0.000 / 0.275 / 0.369 / 0.189 points against the 0.100 allowance.
  With every sector held through the window they differ by 0.000 / 0.255 / 0.331 / 0.123, so the businesses' orders
  are not the cause. The shelf has settled and the prices have not: the fixture's premise, a steady town, does not hold
  on this path, and a settle test on the price index rather than the shelf is the restatement. Not mended here (it is
  not a re-tuning of the dials); queued (`MQ1`).
- **KeptCheck**, "the BuybackLadder on Close it now: no month draws it again with nothing changed in it": in months 2
  and 6, with nothing typed, the quote is the same (most to pay 98.5, fair 100.0, taken 0) but the holders' faces
  drift by about 1e-8 (18,935 of 1.3265e12). `FinancesScreen.drawsAlike()` (ui/FinancesScreen.java:4950) compares the
  ladder and `takenFace` bit for bit, so the drift redraws a picture drawn to four decimals. A fault of the screen's,
  queued (`KL1`). Main's recorded KeptCheck lines do not show on this tree.

## 5. Measured

The 80-run ensemble on 49c2d522 (the draw of §1), `docs/ensemble/GW1.md`, against main's newest baseline
(`docs/ensemble/0.9.83.md`): 24 of 26 criteria pass, against main's 25 of 26. The model has not moved since: the
commits after it are harnesses, and GD3, merged after it, leaves main's trace as it was. A2 fails as it fails on main, A3 fails
in one seed (below), H1 is retired (Jerus, CD1). The third column is GW1's first ensemble (99429bf8: the snapped share
and the first draw, which counted the whole of migration's gap at the band's premium):

| measure (median [min to max]) | 0.9.83 | GW1 | GW1 first |
|---|---|---|---|
| AUTO2 inflation m240-end, a year (A1) | 2.23% | 2.08% | 2.06% |
| AUTO2 young city, inflation m240-600 | 3.39% | 2.74% | 2.52% |
| AUTO2 founding m120-240, longest spell of yoy > 10% | 9 [0 to 28] | 13 [0 to 27] | 8 |
| AUTO2 founding m120-240, longest spell of yoy < -10% | 0 [0 to 0] | 0 [0 to 5] | 1 [0 to 19] |
| A2: longest spell of yoy > 10% from m240 (< 12 in every seed) | 11 [6 to 16] | 10 [7 to 24] | 15 [0 to 30] |
| A3: longest spell of yoy < -10% from m240 (< 12 in every seed) | 0 [0 to 7] | 1 [0 to 12] | 4 [0 to 11] |
| H2: ZERO_HOT m342-402 minus its twin (median at most +1%) | 0.66% | 0.13%, 15/16 | vacuous |
| AUTO2 bank, months under `CAPITAL_RATIO` to m600 | | 2 [0 to 21] | 17 |
| AUTO2 final population | 581,556 | 618,792 | 737,604 |
| ZERO_SLACK final population | 389,740 | 409,599 | 539,172 |
| ZERO_HOT final population | 9,262 | 12,898 | |
| SHOCK final population | 29,630 | 34,660 | 56,563 |
| HIGH final population | 13,722 | 14,570 | 24,096 |
| ZERO_SLACK unemployment in the window: run; twin | 7.12%; 7.84% | 6.30%; 7.29% | 4.04%; 4.40% |

The study's case holds, at a smaller size than the first draw claimed: cities that stalled for want of the hands a
building would bring now build it, end 6% larger under AUTO2 and ZERO_SLACK, carry less unemployment, and the young
city's inflation is lower. The first draw's three costs are gone: H2 is no longer vacuous, the founding has no long
deflation, and A2's median is under main's (its worst seed, 24, is longer).

**What the first ensemble's costs came from** (the gate's review). H2 going vacuous, the founding's deflation and
A2's longer spells came with the tie rule, the snapped share against the unsnapped, not with the draw: the unsnapped
run (c3ca14fb) had none of them (25 of 26, A2 11 [2 to 17]). In a city with no spare, every template at or below the
diploma band shares drawn/posts (about 0.60), so the refused templates tie. `Sector.staffing()` (Sector.java:679),
with the strict `>` at `Construction.java:796` and `BusinessInvestment.java:923`, gives every hold to the first
template, and WantedPosts then prices it. Snapped, the tie is exact; unsnapped, float noise broke it. The snap is
kept (the twins need it, §1) and the tie rule is queued (`TQ1`); the young towns that borrowed ahead of their people
and defaulted, which six fixtures worked around (§4: BankCheck 19, HoldersCheck, SaveFileCheck, CurveCheck,
PolicyPreviewCheck, BondCheck 7c), are queued as `DQ1`: the bank's months under `CAPITAL_RATIO` to m600 measured 17
on the first draw against 4 unsnapped (0.9.55 had 15), 2 now.

**A3 in one seed.** AUTO2-2 is a small town (20,500 people at month 600, 17% out of work). Between months 610 and 620
its unemployment went from 19% to 48% and the policy rate from 7% to 0, and a year's prices then read below -10% for
12 months (months 621-632, -17% at the worst), A3's limit, while its people fell to 17,871 by month 645. Main's worst
seed was 7, the first GW1 ensemble's 11. Not read further here; queued (`AQ1`). WG2's brake on the draw, next in
lane B, is the first place to look: the bust takes unemployment from 19% to 48%.

**PriceLevelCheck's twins** failed "pop" and "workforce the same to the person" on the first draw (they parted at month
105). On the fixed draw PriceLevelCheck is green.

HousingCheck shows the draw from the other side: a city whose people left under a 15% dial now stays and grows under
it (384 people at month 121, 569 at 169).

## 6. Not done

- **WG2's brake.** StaffingDrawCheck 3 tests the pulls main has; WG2 adds its brake's section, and its row says the
  brake and its pull rule apply to `drawFor()` as they apply to the month's arrivals.
- **A2 and A3.** A2 is main's red; A3's one seed is queued (`AQ1`). The tie rule (`TQ1`) and the young towns'
  borrowing (`DQ1`) are queued, not mended: neither is the gate's.
- **Not measured:** the draw with WantedPosts' pricing of the refused posts (LD1) turned off, through which the
  twins' tie travelled before the snap.
- **The reds above** go to their rows: DM2 (MapCheck) and the rows this batch queues (its ship file: OX1, HX1, KL1,
  and the measured TQ1, DQ1, AQ1, HZ1).
