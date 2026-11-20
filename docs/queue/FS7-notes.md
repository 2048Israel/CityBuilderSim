# FS7 - sweep: freight, ports and the island ground (untagged, 2026-11-20)

The batch's record. Its brief is the orchestrator's (2026-11-20) and its table the FS7 rows of
the flaky-suite study (`docs/studies/flaky-suite.md` section 6, by-section rows in
`docs/studies/flaky-suite/by-section.md`), kept to the study's rules (section 5, ★1-★4).
**Harness-only**: no model class moves, so the default trace does not move and no ensemble is
owed. `Observation.java` is the one shared file edited: HomeRailCheck leaves
`Observation.HARNESSES` (★3), which ObservationCheck asserts. Built by Opus (the island
harnesses, HomeRailCheck, CarCheck's last mend, the review) and four Sonnet slices: A (Rail,
RailDepot, Haulage), B (Port, LandlordPort, TradeBand), C (TruckRoute, Van), D (Car).
Main merged at 0.9.168 (HS2's households, BI1, GB3, GB4) before the final runs.

## 1. The island ground (Opus)

- **The waits on the builders (★ call).** `LandmassCheck.Wait` replaces every month ceiling a
  terminal, a bridge or a demolition was waited for: TERMINAL_MONTHS 400 (Landmass 5, 6,
  12(AL1); IslandLink 5), IslandLink's BRIDGE_MONTHS (section 7, beyond the table, the same
  fault class), BridgeCheck 3's 600, 800 and 1,200, and IslandLink 9's played 240. A wait is
  open while the builders' work since it began (`Game.getBuildingOutput()`, read *before*
  each month: the sites' advance is the month's first act, on the output struck at the last
  month's end) is under `STAND_WORK` = 2 times the points owed on site when it began
  (`pointsOwedOnSite()`), and they have not stood idle `STALL_MONTHS` = 24 running. The
  month's output is a water fill over the sites that owe (`siteShares()`), so what was owed
  is done once the work covers it; the second half is for what the city orders meanwhile.
  Each line's detail prints the months, the work and the points owed. Two tries failed first
  and are kept as the record: a ceiling in months at the nameplate
  (`getTotalConstructionCapacity()`) failed four lines (the village's depots it cannot staff:
  1,280 nameplate against about 780 worked); the output read *after* each month closed a
  shell's 48-point demolition in its first month on the next month's 99 points.
- **Where the ground stays cut.** Landmass 12(AL1) and IslandLink 5 and 9 cut the linked
  ground before each month of the wait (`setAllocatedSqFt` to the linked and unlinked
  terminals' ground), as the generated-world half of IslandLink 9 already did, so nothing is
  ordered beside the two terminals: "completes the two terminals at once".
- **LandmassCheck 12, FF1 item 1.** The houses are ordered and `getAvailableSqFt()`, the
  books' link and the audit are read in the same instant, the month that draws them after;
  main's red (69a29638: 12,178,110 sq ft free of 12,094,868 a month later) is that month.
  Now 12,093,809 of 12,094,868, read in the instant. The link itself stays the grown run's
  (KEEP: the every-month audit and the map looks); GROWN_MONTHS 480 is its breadth, and a
  Wait extends it if the link comes late (it came at m432: 702,721 points worked of 311,112
  owed, the grown city's own orders beside it).
- **IslandRailCheck 8**: the same hazard; the fill is ordered in the link's instant, its
  ground read then (105,172,078 sq ft of the half's 105,172,554), and the map read in the one
  month that draws it; LINK_MONTHS (36) is gone.
- **LandmassCheck 13**: "the test player never builds a terminal off its home landmass" is
  counted, not asserted (section 8 and IslandLink 8 cause the only-island shelf).
- **SlopeCheck 9**: a square of steep ground (STEEP_SQUARE 32 plots, at least half steep, by
  rings from the founding site) bought at the founding with `buyRectForTest`; 3.316 km2
  steep of 60.378 owned at the end. "The preparation fires" is section 5's to cause and is
  counted here. The off-steep and findings identities are kept over the 2,000 months.
- **HomeRailCheck 9**: the identity (never a home share without a station or spur, nor a
  quay share without a yard) is asserted over every month, room or none; whether the advisor
  lays a railway is counted (none in 1,255 months on this path). The two Observation lines
  went: sections 2 and 3 cause the railway on `railed()` and assert it hard.

## 2. The freight harnesses (slices A-C, reviewed)

- **RailCheck** (A): the trade asked against `Freight.MIN_LINE_UTILISATION` of a spur's
  tonnes, not 10,000 t; the sized railway's bill read off the quote's own last step
  (billed = allowed x quotePrior / target, the target strictly inside (RAIL_FLOOR, 1)), not
  .25 of allowed; four times the track laid as ceil(3 x sized track / P) spurs and the
  quote's walk to the ceiling asserted as 1 - (1 - q0)(1 - REPRICE_SPEED)^n over the n months
  the model's walk takes to close 99%, not six spurs and forty months.
- **RailDepotCheck** (A): floor(bulk offered / P) - 1 spurs, P a spur's sidings, so the cap
  binds by construction (a fixture gate that it is at least one); the depot comes with the
  spurs that put the offer past the stations' and sidings' tonnes. The labels "then two
  spurs" and the depot's moved (no known-reds row reads them).
- **HaulageCheck** (A): 4's spurs sized the same way (0 < r < 1 by construction); 3's months
  short of trucks and with enough caused by `setPantry(Good.TRUCKS)` at half and twice the
  need, read the month after each; the per-month identity kept (its 1e-9 is already a ratio).
- **PortCheck 5 and 7, LandlordPortCheck 4 and 7, TradeBandCheck 2-5** (B): the railway's
  carried shares set (`Freight.toState`/`restore`) and the month's boundary tonnes handed to
  Ports by `PortCheck.handMonth`, the steps `Freight.haul()` takes (planFirst, planAfter,
  commit, the road shares, `reapplyBand`); the crude's MR both sides of `crudeKept()`; the
  Bulk Terminals laid past DESIGN_USE of the chain's dry bulk; TradeBand on a built city
  (`built()`), its section 6 still on the played `full()` (KEEP).
- **TruckRouteCheck 1, 2, 9 and 6's premise, VanCheck** (C): the base city handed its plant's
  rows, a fleet and a share (`legs()`), hauled by hand; the counts from MOST_A_MONTH and
  TONNES_A_LOAD; the vans' town built (no construction), its fleet seeded by `runFleet()`, the
  shops' pantry set short and one month's order read inside the van's floor and ceiling.

## 3. CarCheck (slice D, then Opus on HS2's households)

HS2 had not edited CarCheck (`git diff origin/main...origin/batch/HS2` empty) and shipped in
the 0.9.168 merge, so its five CarCheck rows are FS7's. The buyers: every cell's debt cleared,
lockout lifted and a car's price over its cushion banked, one month played on it, then
`Motoring.month` read at once (11 cars, the price inside the draw's shelf and import prices,
the savings' drop to the cent). Why the month (★): on HS2's path the cells large enough to want
a whole car (1 / CAR_ADOPTION households) are the retired and the unemployed, discharged and
going short; given money alone, a month discharges them again (their debts). The plants' page
on rows set at half rate; the fare's ten years from the two towns' ceilings by
HouseholdBalance's own rules (0.891 against 0.430); the jam town's road load and GDP set, a
line paying at twice the break-even and none at half. Left: the default-fare line now reads
the towns at month 2 and its cost-alone side is 0.0% (against 33.7%): still the law, thinner.

## 4. Known reds: every FS7 row goes green

LandmassCheck "home filled by a fixture"; IslandLinkCheck "...the island is linked by the
end"; SlopeCheck "fixture: the city owns steep ground by the end" (relabelled with its
fixture, the same prefix); CarCheck "...and ten years on..." and HS2's five (car sales, the
same price, a Bus Network pays, the road's gap, the bus move). No row is left and none is
queued: no law failed on a caused fixture, so no model fault is filed.

## 5. The checks

- `./batch-check.sh --fast` on the merged tree: compile OK, maps regenerated, Stale 0 firm.
- Each harness alone on the merged tree, one JVM at a time, all exit 0 and 0 FAIL: CarCheck,
  VanCheck, ObservationCheck, RailCheck, RailDepotCheck, HaulageCheck, TruckRouteCheck,
  PortCheck, LandlordPortCheck, TradeBandCheck, BridgeCheck, IslandRailCheck, SlopeCheck
  (308 s), HomeRailCheck, IslandLinkCheck, LandmassCheck (356 s).
- Main moved again (FS15, HL1-M's CityRuns) and was merged at 5f472bf5: `--fast` again green
  (maps regenerated), and the harnesses that read the map or Observation run again, all exit 0
  and 0 FAIL: ObservationCheck, IslandRailCheck, BridgeCheck, TruckRouteCheck, HomeRailCheck,
  SlopeCheck, LandmassCheck (546 s).
- `sh tools/pick.sh origin/main HEAD`: 65 of 230 (Observation.java brings its users), not
  ALL, so no dry run is owed and no dry-run log is kept.

## 6. The law failing on a fixture that breaks it

- The Wait: read after the month, LandmassCheck 6's shell demolition FAILs ("not done in 1
  months, 99 points against 48 owed"); at the nameplate ceiling four lines FAIL (above).
- LandmassCheck 12 and IslandRail 8: main's FF1 red is the fill read a month late.
- SlopeCheck 9 and IslandLinkCheck 9: their known-reds rows are the played premise failing
  (no steep owned on BO1-S's path; unlinked in 240 months on OP1's).
- RailCheck: with 3 works the sized line FAILs (the quote at 100%); one spur over, "four times
  the track" and "earns LESS" FAIL (slice A's mutations).
- RailDepotCheck: spurs at floor(offered / P) + 2 FAIL "then spurs, no station ... the bulk,
  which is more" (200,000 t of sidings over 116,502 t of bulk).
- CarCheck: without the debts cleared, on HS2's path, "a city with money in the bank
  motorises" FAILs as the fixture it is (0 wanted of 53 able cells).
- Not shown: Haulage 3 and 4, Port 5 and 7, LandlordPort 4 and 7, TradeBand, TruckRoute, Van.

## 7. Calls

- ★ Ceilings in builders' work, not months (section 1): the verdicts offered a derived ceiling
  or instant terminals; instant terminals would lose the on-site months the sections assert
  (unlinked while on site), so the work-derived ceiling was taken.
- ★ CarCheck's buyers given debt-free money and a month (section 3): the alternative, a larger
  town, depends on HS2's cell layout and on jobs the town does not have.
- IslandLink 7's and BridgeCheck 3's month ceilings went too, beyond the table: the same fault.
- Still played, and said in their headers: RailCheck's ten works (a built town);
  RailDepotCheck's goods offered (the shops' imports, no setter); HaulageCheck 3's leg kinds;
  LandlordPort 7's first quote, a play-until of at most 4 months that fails as the fixture
  (PortDivision's closeMonth is the haul's, with no door); TradeBand 6 on `full()` (KEEP).

## 8. What the review changed

Slices A to C came back green and were taken as built; D came back green on main and red on
HS2's households after the merge (0 wanted), mended here (section 3). The reviews read each
diff for model files (none), borrowed builders (HaulageCheck.base/played/lay,
CarCheck.marginsHeld, AutoBuildCheck.noGround: untouched) and measured numbers in laws.
