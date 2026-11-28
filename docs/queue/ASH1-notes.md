# ASH1 - power for a small town: notes

**Resume here (2026-11-28 05:40):** REV-ASH1 said SHIP on its fourth look at c46b355e3. origin/main (UX1)
merged at 7913b708f; the ensemble on it PASS (below, `docs/ensemble/ASH1.md`). Next: "ASH1 dry run?" asked, and
on the go the dry run (`sh tools/gate.sh ASH1 --dry-run`), then the marker dropped and ready. No run is in flight.

Jerus's findings 1, 2, 9 and 8 from governing Ashbourne (`docs/queue/ASH-findings.md`), brief
`docs/queue/ASH1.md`. Each condition is caused on fixtures of the batch's own; his save was never read.

## What was built

1. **The POWER row reads the lesser of the plants and the wires** (finding 1). `CityNeeds.powerPeak(Game)`
   (public, record `PowerPeak`) compares the plants' pair and the wires' pair
   (`BuildAdvice.supplyDemand` POWER and GRID, the getter Build > Utilities reads) by served share,
   never by kW (their peaks are computed differently), and the row reads the lower: "79% served at the
   peak hour by the wires" (`BY_THE_WIRES`), red when the wires shed load, so NEEDS YOU counts it. The
   `Need` record carries `wires`; `BuildAdvice.measureOf` maps such a row to the GRID measure, so the
   card NEEDS YOU offers and the auto-builder's remit read the wires; its "on the way" counts grid units
   (a plant on site no longer turns a wires-red row amber, nor a substation a plants-red one).
2. **A grid step a small town can take** (finding 2): the **Distribution Substation**, id 173, 5 MW
   (`Substations.DISTRIBUTION_ID`, `DISTRIBUTION_MW`), at the Substation's cash, points, materials,
   land, road load and crew per MW (5%: about Cr 1.1M where the Substation is Cr 22.1M; 6,600 points;
   7 posts). The grid's carry is `Substations.carryKw`: the district Substation replaces the founding
   network as PW5 had it, each distribution substation extends whatever stands. `Substations.costs`,
   `BuildAdvice.unit`/`supplyDemand` (GRID) read both units; the advice picks by its 20-year plan
   value, so a small town is advised and built 5 MW steps.
3. **The posts follow the load** (finding 9): the grid units offer their posts at the share of the
   wires' carry the last dispatch's peak half-block used (`UtilitiesHandler.getWiresUsed()`), struck
   as the month begins, before its posts are counted, and held to the next month's
   (`strikeWiresShare()`, `getWiresShare()`, saved as `PowerState.wiresShare`; `BuildingManager`
   banner THE GRID UNITS' POSTS); the rest are withheld like the builders' laid-off crews, so the
   labour market (`getTotalJobs`), the utility payroll (`getJobArrayPerCategory`) and
   `Substations.costs()` (`getGridPostsOffered`) take only the posts offered. The carry does not
   depend on staffing.
4. **The months left at the site's pace** (finding 8): `Game.sitePaceMonths(key, plan)`, the site's
   owed points over the points `Game.sitesPlan()` gives it (the month's plan at the builders' output
   plus the foreign contractor's month as last settled, as `advanceConstruction` takes them), and
   every screen's months left reads it: the overview's row, the build card's "N on site", the
   construction page's rows and chart, the right panel, the build screen's ring, the oil view. The
   right panel's "stalled" or "waiting for crews" and the construction page's "waiting for crews" are
   decided on the same plan (`Game.siteLeft`, REV-ASH1 3): a site the foreign contractor's crews build
   in a town whose own crews give the sites nothing is built on, not stalled. A site the city hands
   to the foreign crews (FC2b) is built on too (REV-ASH1 4): the decision counts their month on the
   part handed to them, the least of what is owed abroad and their pace, as `advanceConstruction`
   builds it, and the months are the later of the two parts', the part abroad's points over their
   pace beside the part at home's over the plan's work.
5. **Found in building, mended:** with 5 MW units the auto-builder's grid orders are many small
   units the builders open in turn, and `AutoBuilder.pendingCapacity`'s two drop-whole rules (HW1)
   ordered every unit still on site again: 27 units, 145 MW against a 45.6 MW peak. For GRID alone:
   the site is not dropped past its deadline (as power's is not), and where a faster grid unit could
   open sooner only the slower kind counts nothing (a 100 MW Substation on site yields to 5 MW units;
   more of the same kind would only crowd the same crews). Now 14 units, 80 MW against 46.5 MW.

## The calls (★ a design choice)

- ★ **5 MW, priced per MW like the Substation.** Real cases (looked up): Portland 1924, Northwestern
  Electric's city distribution substations from 3,000 kW, 6,000 planned (Electrical World, 15 Mar 1924,
  p.523); a Midwest 1924 station of 3 x 1,000 kVA at $46,600, $15.5/kVA all in (Electrical World,
  28 Jun 1924, pp.1325-27); Los Angeles No. 13 (1928) 10,000 kVA, No. 17 (1930) 17,500 kVA at about
  $23/kVA (waterandpower.org). The land-and-building share was no smaller at the large stations, so no
  economy of scale per kVA shows in these cases: the small unit is priced per MW (Jerus's own second
  option, "capacity priced per MW"). Consequence: the 100 MW Substation is rarely the cheaper plan,
  and the auto-builder builds 5 MW steps at every size; it stays in the catalogue (PW5's law holds
  where the small unit is not on the menu, SubstationCheck).
- ★ **Additive carry.** A distribution substation extends the network that stands; the Substation
  replaces the founding 10 MW as before (otherwise the first 5 MW unit would add nothing).
- ★ **Posts at the dispatch's share, read live.** The share is the last dispatch's highest half-block
  load over its carry (Jerus's "the kW actually carried"); the period's crews scale with the load and
  customers served (US Census of Electrical Industries 1917: 105,541 employees, about 12 per MW of
  capacity, all functions; BLS Bulletin 944, 1948: transmission and distribution about 23% of staff,
  customer service 10%). Research also found period substations were often attended (Los Angeles's
  "continuously attended" before WWII; automatic substations from 1914 on railways, 1922 on city AC),
  so the posts are not dropped, they follow the load. No floor. The grid's raw posts are held with the
  sectors' sums in AP5's `HeldPosts` (raised by the existing writers); the share is read live, never
  held by a pass, so it needs no raise of its own.
- **The share struck once a month and saved** (REV-ASH1 1). The month counts and pays its posts
  (`SimulationEngine.updatePopulation`, `updateServices`) before its own dispatch, so the share it
  uses is the one struck as it began, from the dispatch before (`Game.nextMonth`, beside the research
  posts); a reload counts them at that share, not the closing dispatch's. One new saved field,
  `PowerState.wiresShare`, no format bump: absent in an older save, it loads as 1, every post
  offered, as that save's month offered them. A new city founds at 1.
- ★ **The pace on every screen's months left; the rule's wait in the quotes.** The rule's wait
  (`ruleWait`) holds every other site at its weight share for the whole wait; the month water-fills,
  so a big site among many small capped ones does more than its weight share (TownGridCheck 4: pace
  138.9 months, the rule's wait 155.1). Every months-left on show reads `Game.sitePaceMonths` (REV-ASH1
  2: the overview alone did at first, and the card and the page read the rule's). `Game.siteMonths` and
  `onSiteMonths` stay the rule's wait, as do the quotes, the investors' months ahead and the abroad
  terms, which ConstructionControlCheck, BuildersFirstCheck, DeathCareFirstCheck and EssentialWorksCheck
  hold to the bit: a quote is an order not yet on site, which the month's plan cannot price. A site
  the plan gives no work (behind the city's order) reads the rule's wait, the queue's; a site handed
  abroad reads its two parts' pace, the later of them (REV-ASH1 4: it read the rule's wait, and a site
  handed whole said "stalled" while the foreign crews built it).
- ★ **The advice prices a new grid unit at every post** (`WorksValue`, `BuildCard`): the share over a
  20-year plan is unknown, so every post is the bound; it only favours the small unit further.
- **Mended, not left to PW9-G:** the over-ordering is the auto-builder's handling of the small unit
  this batch adds, in the default behaviour; PW9-G orders wires with the plants, which this is not.

## The checks

- **TownGridCheck** (new, 5 sections): 1 the row red by the wires and green with both over, NEEDS
  YOU's count, measureOf, on the way; 2 a town growing 8.9 to 46.5 MW in 5 MW steps, no 100 MW unit,
  the carry covering the peak and never more than a unit and the slack over the advice's need; 3 the
  posts following the load, the labour market and the payroll on the offered posts; 4 the months at
  the site's pace (the rule's wait 16 months longer; the month's real advance equal to the plan's work
  to 1e-9 with the contractor off, and within his month's change with him on: 4,962 planned against
  4,755 advanced, the crews alone 901), and the overview, the build card, the construction page and
  the right panel reading the one figure to the bit, and a town whose roads' repairs take its
  builders' whole month while the contractor's crews build the Substation (277 points settled, 208 on
  the site): the panel's line through `Game.siteLeft` says built on, at the same 631.7 months as the
  overview, the card and the page. Every way a site gets work, through `Game.siteLeft` (REV-ASH1 4):
  the city's crews (the contractor off), the contractor's month (on, and with no home output), the
  queue's wait (the city's order puts a Middle School behind a University: waiting for crews, at the
  queue's 325.1 months), a Middle School the city hands abroad whole (the plan gives it nothing; built
  on at 2,100 points over their 400 a month, 5.25 months, and the month builds exactly 400) and then
  in part (a second school ordered: the later of 2.91 months at home and 4.25 abroad), and none at
  all (him off, the roads' repairs taking the builders' month: stalled, never), each equal on the
  card and the page to the bit; 5 the money audit over section 2's months, worst residual 5.7e-8
  against a cent.
- **SubstationCheck's save section** (REV-ASH1 1): a load of 44% of the carry between the presses, a
  month struck on it that closes at 0.09%; live against reloaded, the share, the grid's posts offered
  and withheld per job type, the labour market's posts and fill, the electricity posts paid, the
  utility payroll, the wage bill and `Substations.costs()`' payroll equal to the bit, and again the
  month after.
- Mutations, each red then restored: `powerPeak` the plants alone (section 1, 8 labels); the grid
  share 1 (section 3, 3 labels); `onSitePaceMonths` the rule's wait (section 4); the grid's exemption
  from rule (1) removed (section 2: 27 units, 145 MW). AP5's raise removed in
  `BuildingsStacks.addQuantity`: LabourCheck red (6) and SubstationCheck's held-grid line red. The
  grid share read live again (`getWiresUsed`, not `getWiresShare`): SubstationCheck's save section red
  (4 labels). The build card on `onSiteMonths`: TownGridCheck 4's one-figure label red (both cases).
  `Game.siteLeft` deciding on the city's crews alone: TownGridCheck 4's no-home-output case red (STALLED).
  `Game.siteLeft` without the foreign crews' month on a site handed abroad: the whole handover red
  (STALLED); `Game.sitePaceMonths` without the part abroad: the whole and the part handovers red (37.1
  and 32.4 months, the rule's wait and the whole site over the home work).
- Restated to the real law: SubstationCheck's order (both units known: the least Distribution
  Substations that cover the peak, never a 100 MW unit; PW5's one Substation where the small unit is
  off the menu); its tariff's payroll (the posts offered at the dispatch's share); BuildAdviceCheck's
  GRID oracle (`carryKw` over both units) and section 15 (a fixture whose wires served 4% now reads
  the wires first, then PW1's asserts on the plants once the wires carry twice the peak);
  BuildCardCheck's served POWER row (the lesser share); PowerPlantsCheck's 1900 list (the
  Distribution Substation existed then); BuildingDataCheck (the twin, its year, the 5%-per-MW law).
- Green at the slices (one at a time): BuildingDataCheck, SubstationCheck, BuildAdviceCheck,
  PowerPlantsCheck, PowerPlanCheck, PowerPriceCheck, NewUnitCheck, EssentialWorksCheck,
  AutoBuildCheck, PriceLevelCheck, BuildCardCheck, AskProceedsCheck, HydroCheck, WorksFinanceCheck,
  PowerViewCheck, LabourCheck, MaterialsCheck, ElectrolyserCheck, SaveFileCheck, ReadPathCheck,
  BooksCheck, TownGridCheck; RoadCheck exit 3 (main's known reds only).

## The ensemble

80 of 80 runs on the gate box at 7913b708f (the merged tip, 0.9.217), label ASH1, scored against
`docs/ensemble/0.9.216.md`; committed as `docs/ensemble/ASH1.md`. **PASS, the hard criteria 10 of 10:** M1, the
money audit, failed in 0 months in every policy, and F1, LongPlaytest's findings, 0 of 16 runs in every policy.
Beside the baseline (info): AUTO2's final population 8.18M against 6.49M, its 40,000 people at m605 against m628
(16 of 16); A1 2.95% inflation (9 of 16 in band, FAIL as the baseline's 11 of 16 was), A2's longest hot spell 16
months against 15 (FAIL as the baseline), A3, S1, K1 and K2 PASS as the baseline; H1 retired. T2's final
population went FAIL to PASS in ZERO_HOT (6,191 against 7,096, within 75%), SHOCK (177,650 against 133,110) and
HIGH (74,812 against 56,156). The trace moves, as it should: the grid's posts follow the load and a small town's
wires grow in 5 MW steps.

## For the manual

- Utilities: the Distribution Substation (5 MW, about a twentieth of the Substation's price), the
  grid's carry (the Substation replaces the founding 10 MW, each distribution substation adds 5 MW),
  and the grid's posts offered at the share of the carry the peak uses.
- Overview: the POWER row reads the lesser of the plants and the wires, "by the wires" when the wires
  bind, and its card then offers the wires.
- Construction: a site's months left, on every screen, are at its pace this month (its points owed
  over the month's work on it); a quote for a new order keeps the queue's wait.
