# RG1 — farm ground at a farm price, by policy

Builder RG1, Claude Opus 5.5, headless in worktree cl-RG1. Cut from origin/main
ee0384007 (0.9.204, format 66); origin/main merged at 4183928ca (BX1).

Jerus, 2026-11-24 09:12: "Policy, that also lets farms crops as well be cheaper".

## What was built

- **Zoning: the farmland reserve.** The fields' zone (`Zoning.RESERVE_SECTOR`, Agriculture,
  `TaxPolicy.FARM_SECTOR`) may be made a reserve (`setFarmland()`). While it is one, its
  `priceShare()` is the farmland share struck each month: the farmland price over the going
  price, capped at `MAX_PRICE_SHARE`. `dialShare()` keeps the dial for when it is lifted. Every
  reader of a zone's price (the roll's assessment, a business's zoned ground, PolicyPreview,
  ZoningView) prices the reserve with no edit of its own. The WHY restates BC's ALR,
  Oregon's EFU zones and Ontario's Greenbelt (Zoning's class javadoc).
- **The farmland price** (`LandMarket.getFarmlandPricePerSqFt()`) is the local anchor ×
  `farmlandOfBase()` × `hostLandFactor()`.
  - `farmlandOfBase()` is Canada's farm land a hectare from LandFactors' own table (Statistics
    Canada, Table 32-10-0047-01, US$9,563.86 of 2024; `LandFactors.referenceUsdPerHectare()`),
    carried to 2025's dollars by the US CPI, a square foot over `BASE_PRICE_PER_SQ_FT`. That is
    0.130: $0.091 a square foot against the base's $0.70, between LandMarket's own US pasture
    ($0.044) and cropland ($0.134).
  - `hostLandFactor()` is the host nation's LandFactors factor at the month, 1 on a generated world.
  - The crowding premium and the city's scarcity are outside it.
- **LandManager:** `strikeFarmland()` at every `updateMarket()`. `reserveKeyOf()` and
  `groundKeyOf()` route the city's Wind and Solar Farms to the reserve's ground while one stands.
- **The city's farms' ground, apart** (the review's P1, REV-RG1). A city Wind or Solar Farm in the
  reserve is counted on the zone as `cityFarmSqFt`, saved, and never as the fields' `usedSqFt`.
  `LandManager.allocateCityFarm()` takes the reserve's held ground into it, and
  `releaseCityFarm()` gives it back from it whatever the reserve is by then. The zone's held
  ground is its target less both (`Zone.builtSqFt()`, in heldByZone, WorksValue and
  AdviceValueOracle). The roll (`EconomyManager.assessedLandValueOf`) and the fields' buyback
  read `usedSqFt` alone, as before. A missing key reads 0, so there is no format bump.
- **Game:** `setFarmlandReserve(boolean)` is logged as a ZONING decision. The city's build
  (processBuildOrder, buildStack, runAhead, freeGroundFor) reads `groundKeyOf`, and while a
  reserve stands it allocates a farm by `allocateCityFarm`. A demolished city Wind or Solar Farm
  is released by `releaseCityFarm`, whatever the reserve is at the time.
- **The planner:** `BuildAdvice.landValue(game, t, sqFt, landLeft)` prices a farm's ground in
  the reserve at the reserve's price first. capitalOf, orderLife, availableGround, WorksValue,
  AutoBuilder (lacks, groundWords) and AdviceValueOracle follow it.
- **The screen:** on Policy › Zoning, the fields' zone page has a card under ITS PRICE with
  "Make it a farmland reserve" / "Lift the reserve" and the farmland price a m² against the
  going price. The price dial reads `dialShare()`. No harness draws ZoningScreen
  (docs/harnesses.md); the card's figures are model getters.
- **FarmlandReserveCheck** (new; in AllChecks after ZoningViewCheck), seven sections, both paths caused:
  1. The price is the table's: 0.130 of the base. Crowded to 8,000 a km², the going price rises
     117-fold and the farmland price does not move.
  2. Without the policy, today's prices: no zone, or a zone at 50%, gives today's quotes and
     capitalOf, and wind stays on its own key.
  3. With it: the struck share, the dial kept, re-sized, never dearer, a copy detached.
  4. The money: a Mixed Farm's ground is quoted at $1,416.17 against $7,066.19. Cash and land
     sales move by exactly it, and the month's MoneyAudit closes.
  5. The city's Wind Farm takes 0.162 km² of the reserve, counted as the city's farms' and not
     the fields'. With a field outside the zone (the fields' ground 0.111 km², 0.0557 km² in the
     zone), the fields' land on the roll and their whole buyback stay at $8,493.58 before and
     after. It pays its cash cost alone, and its ground comes back on demolition. In both orders
     the fields' used ground is exact: built lifted and demolished under the reserve, or built in
     the reserve and demolished lifted.
     (The review's two fixtures. On the first build the old code fails both: `usedBy` grows by
     the farm, and the roll's `min(all, used)` grows with it.)
  6. The planner: PowerPlanCheck 6's dear town crowded orders no wind (its ground 74% of
     capital, a Solar Farm's 92%), nor with a zone at the going price. With the reserve, the
     ground is 0.3% and 1.2%, and the spread orders a Wind Farm.
  7. Saved and loaded, the same reserve with a city Wind Farm in it (its ground counted apart),
     and the same month a month on. An older save's zoning with no reserve key reads as no
     reserve and no city farm ground.

## Calls

- ★ The reserve is the fields' zone, not a zone kind of its own: the brief's "start from
  Zoning and extend it". A second farm sector, if one comes, joins through `RESERVE_SECTOR`.
- ★ The price is min(farmland, going): the reserve is never dearer than the going price.
- ★ It leaves out both the crowding premium and the scarcity multiplier. Farmland's own price
  moves with the nation's land factor, not with the city's density.
- ★ The city's farms' ground is its own counter on the zone, not a second zone and not the fields'
  used ground (the review's form). Released counter-first, as the zone's own ground is. A city farm
  built outside the reserve and demolished while others stand in it takes its release from the
  counter: the ground is the city's either way, and the counter cannot outlast the farms on it.
- ★ The struck share is saved (`Zoning.State.farmlandShare`), so a load prices the zone exactly
  as before it, with no wait for the next month's strike.
- ★ No format bump. Gson leaves the two new keys at false and 0 in an older save, which reads
  as no reserve: what it was.
- ★ The city pays nothing for its own ground, with the reserve as without it. The money
  difference is on the fields' purchases and the roll, both through paths that already exist.
  There is no new payer or payee.
- ★ The land office's offers to the city are unchanged. The reserve prices the city's sales,
  not its purchases.
- Agriculture's own refusal message and the business paths (doorPriceOf, wireLandByRelief)
  were left alone: no farm template passes through them.
- BuildAdvice does not take the reserve's held ground off between cards in one advice pass.
  It is minor, since a pass seldom orders two farms.
- The save twin, found while building section 7, is a player's path, not a fixture's artifact
  (the review, item 3, restated). A player builds by hand, saves before pressing the month, and
  quits; the save does not reload to the same city. Its cash a month on is a few cents off:
  0.99 for a fresh city saved at month 0, 1.33 after a hand-built Wind Farm, and 0 once a month
  is pressed after the build. Main does the same, with no reserve. A save must reload exactly,
  so it is queued as **HBS1** in the ship file: find the state a hand build leaves unsaved, with
  a SaveFileCheck fixture of build, save, load, press. Section 7 presses a month before it
  saves, so it tests the reserve and not HBS1's bug.
- The twin loads from a copy of the slot in a folder of its own. Two cities pressed a month on
  in one folder autosave into one history file, and the rename collides on Windows.

## The checks

- `./batch-check.sh --fast`: compile OK, maps regenerated, stale 0 firm.
- Named, one at a time: FarmlandReserveCheck (44 OK after the review's mend), ZoningCheck, ZoningViewCheck,
  PowerPlanCheck, BuildAdviceCheck and EssentialWorksCheck green. SaveFileCheck's only red
  is main's known "REV-HC1 P5: the populated format-62 city continues ..." (FRW1, known-reds.txt).
- `sh tools/pick.sh origin/main HEAD`: ALL (AllChecks changed), so a dry run follows. Its
  review line reads "not needed - no money, saves or threading in the diff".

## The default run, and the ensemble

No code path takes the reserve unless `setFarmlandReserve(true)` is called, and the policy
autopilot and the ensemble never call it.

The ensemble ran on 9b5b1680e (the review's mend), on the gate box, against
`docs/ensemble/0.9.204.md` (WG3's, the newest), with no JAVA_OPTS beyond the box's data files.
It is recorded as `docs/ensemble/RG1.md`.
- **Batch verdict PASS:** hard criteria 10/10, criteria 25/26 (one retired, not counted), no
  verdict changed. M1 is 0 and F1 0/16 under every policy.
- **The trace is unchanged:** all 28 figures with a baseline value equal the baseline's.
- R1g's dry run took the box partway through. The ensemble waited for it, then resumed.

## The dry run

`sh tools/gate.sh RG1 --dry-run` on the gate box, run `/home/jerus/cbs/runs/cbs-gate-RG1-GmcAQnsy`
(kept there, and copied home to `%TEMP%/cbs-gate-RG1-GmcAQnsy`).
- **Its two commits:** batch/RG1 at 5a85df455044710c25e5544a580dbf000ba2e0ae, on main
  dc573ee27b48aedb976c05cdd24253d225171265 (BX2).
- **Result:** gate exit 0. The suite exited 3 in 933 s with known reds only. Every red harness
  was main's by gate.txt or known-reds.txt, none of them RG1's. The unnamed-lines file is
  empty, and the dry run left the tree as it was.
- The dry run waited for BX2: main was red on PackagingCheck after BX1 until it shipped.
