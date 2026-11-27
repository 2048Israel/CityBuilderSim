# ASH1 - power for a small town: notes

**Resume here (2026-11-27 10:45, before the 11:15 network window):** the model and the harness are
done; origin/main (AP8, 0.9.217) merged at af0455e50 and pushed; `--fast` green there, and on that
tip TownGridCheck, BuildAdviceCheck, SubstationCheck, BuildingDataCheck, LabourCheck,
AutoBuildCheck, PowerPlantsCheck, PowerPlanCheck and EssentialWorksCheck all exit 0. Next: on the
tip, BuildCardCheck, NewUnitCheck, SaveFileCheck, PowerPriceCheck one at a time (merge origin/main
first if it moved); regenerate the maps; the ship file is pushed provisional (793c51999); mail "ASH1 mends at
<commit>" with the pick (ALL) and the review line. No run is in flight.

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
   wires' carry the last dispatch's peak half-block used (`UtilitiesHandler.getWiresUsed()`,
   `BuildingManager` banner THE GRID UNITS' POSTS); the rest are withheld like the builders' laid-off
   crews, so the labour market (`getTotalJobs`), the utility payroll (`getJobArrayPerCategory`) and
   `Substations.costs()` (`getGridPostsOffered`) take only the posts offered. The carry does not
   depend on staffing.
4. **The months on the way at the site's pace** (finding 8): `Game.onSitePaceMonths(t, plan)`, the
   site's owed points over the points `Game.sitesPlan()` gives it (the month's plan at the builders'
   output plus the foreign contractor's month as last settled, as `advanceConstruction` takes them).
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
  held, so it needs no raise of its own. No saved field: the share reads the saved dispatch, and
  while a load is staged it reads the saved month (the load's own rebuild runs before
  `restorePendingPower`).
- ★ **The pace, not the rule's wait, on the overview only.** The rule's wait (`ruleWait`) holds every
  other site at its weight share for the whole wait; the month water-fills, so a big site among many
  small capped ones does more than its weight share (TownGridCheck 4: pace 146.6 months, the rule's
  wait 162.9). The brief said the construction card uses the pace; it does not (the card and the
  construction page read `Game.siteMonths`, the rule's wait; only the page's "% of crews" reads the
  plan). Changing `ruleWait` would move every quote, the investors' months ahead and the abroad terms,
  held to the bit by ConstructionControlCheck, BuildCardCheck, NewUnitCheck, BuildersFirstCheck and
  DeathCareFirstCheck; so the overview's months-left reads the pace, and the other screens keep the
  rule's wait. A site the plan gives no work (behind the city's order) or one handed abroad keeps the
  rule's wait.
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
  4,755 advanced, the crews alone 901); 5 the money audit over section 2's 34 months, worst residual
  2.6e-8 against a cent.
- Mutations, each red then restored: `powerPeak` the plants alone (section 1, 8 labels); the grid
  share 1 (section 3, 3 labels); `onSitePaceMonths` the rule's wait (section 4); the grid's exemption
  from rule (1) removed (section 2: 27 units, 145 MW). AP5's raise removed in
  `BuildingsStacks.addQuantity`: LabourCheck red (6) and SubstationCheck's held-grid line red.
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

## For the manual

- Utilities: the Distribution Substation (5 MW, about a twentieth of the Substation's price), the
  grid's carry (the Substation replaces the founding 10 MW, each distribution substation adds 5 MW),
  and the grid's posts offered at the share of the carry the peak uses.
- Overview: the POWER row reads the lesser of the plants and the wires, "by the wires" when the wires
  bind, and its card then offers the wires; "on the way" months are at the site's pace.
