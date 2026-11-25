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
- **Game:** `setFarmlandReserve(boolean)` is logged as a ZONING decision. The city's build
  (processBuildOrder, buildStack, runAhead, freeGroundFor) and its demolition read `groundKeyOf`
  and `reserveKeyOf`.
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
  5. The city's Wind Farm takes 0.162 km² of the reserve. It pays its cash cost alone, and its
     ground comes back on demolition. Lifted, wind leaves the zone alone.
  6. The planner: PowerPlanCheck 6's dear town crowded orders no wind (its ground 74% of
     capital, a Solar Farm's 92%), nor with a zone at the going price. With the reserve, the
     ground is 0.3% and 1.2%, and the spread orders a Wind Farm.
  7. Saved and loaded, the same reserve, and the same month a month on. An older save's zoning
     with no reserve key reads as no reserve.

## Calls

- ★ The reserve is the fields' zone, not a zone kind of its own: the brief's "start from
  Zoning and extend it". A second farm sector, if one comes, joins through `RESERVE_SECTOR`.
- ★ The price is min(farmland, going): the reserve is never dearer than the going price.
- ★ It leaves out both the crowding premium and the scarcity multiplier. Farmland's own price
  moves with the nation's land factor, not with the city's density.
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
- The save twin, found while building section 7: a city saved by hand right after a station
  is built by hand, before the month is pressed, loads with its cash a few cents off a month
  on. This holds with no reserve too: 0.99 at month 0, 1.33 after a hand-built Wind Farm, 0
  once a month is pressed after it. The fixture presses a month before it saves. A fixture's
  artifact, not a player's path, so nothing is queued.
- The twin loads from a copy of the slot in a folder of its own. Two cities pressed a month on
  in one folder autosave into one history file, and the rename collides on Windows.

## The checks

- `./batch-check.sh --fast`: compile OK, maps regenerated, stale 0 firm.
- Named, one at a time: FarmlandReserveCheck (41 OK), ZoningCheck, ZoningViewCheck,
  PowerPlanCheck, BuildAdviceCheck and EssentialWorksCheck green. SaveFileCheck's only red
  is main's known "REV-HC1 P5: the populated format-62 city continues ..." (FRW1, known-reds.txt).
- `sh tools/pick.sh origin/main HEAD`: ALL (AllChecks changed), so a dry run follows. Its
  review line reads "not needed - no money, saves or threading in the diff".

## The default run

No code path takes the reserve unless `setFarmlandReserve(true)` is called. The policy
autopilot and the ensemble never call it, so the default run should not move.
