# WG1 - a real minimum wage ({next}, 2026-11-10)

The brief: `QUEUE.md`'s row and `docs/studies/spec-wages.md` sections 3.1-3.3 and 6 (Jerus, 2026-11-02: "yes a real
minimum wage"; "0.85, Nova Scotia's (Recommended)"). Branch `batch/WG1` from main d9f61df3 (0.9.80). Three slices on
Sonnet 5.5 at xhigh, reviewed and committed here: WG1-A (the model and LabourCheck), WG1-B (the preview and the
floor's words), WG1-D (the money fixtures the new path no longer causes); WG1-C, the floor fixtures, written here.

## 1. The model (WG1-A: `LabourMarket`, `Game`, `PopulationManager`)

Until now every wage was `minimumWage x ratio(job) x costOfLiving x multiple`: the floor dial was the whole ladder's
anchor, and 94% of workers were paid exactly the floor (the study's Table 1). Now:

- **The going rate is the base.** `baseWage(job) = goingRate x ratio(job) x costOfLiving`, the going rate being the
  ladder's anchor (`ladder.anchor()`: the unskilled rung, `PayTier.UNSKILLED`, on today's ladder; Y1901's for a 1900
  city), in founding money, kept as `minSettable` is (set by `setLadder()`, divided by the unit in `seedConstants()`,
  scaled by `redenominate()`). `GOING_RATE` is today's ladder's, for a reader with no city. `targetWageAt(job, floor)`
  has the floor only in its `max()`, and only while the law binds, as `advanceMonth()` holds the wage.
- **The floor lifts only the wages under it** (`wage = max(wage, cashMinimumWage())`, unchanged in form), and a new
  city's starts at `DEFAULT_FLOOR_SHARE` = .85 of the going rate: Nova Scotia's $17.00 over the material-handling
  labourer's Job Bank median, $20.00 (C1). `isPinned()` is unchanged, so Migration's surplus departures read the same
  test.
- **The tie** (`FloorTie`, saved): PRICES, the default and the old rule (founding money lifted by the cost of
  living); AVERAGE_WAGE, the real floor struck each month at `tieShare` (default .45, the middle of the OECD's 37-55%)
  of last month's average wage (`followAverageWage()`, called in `startOfMonthUpdate()` from
  `PopulationManager.getAverageWage()`, the Rankings' wage bill over posts filled); NOTHING, a cash figure held while
  prices move (both `updateCostOfLiving()` overloads). `Game.setFloorTie()` is gated by the minimum-wage lever as
  `setMinimumWage()` is.
- **The getters the Wages page needs:** `getGoingRate()`, `cashGoingRate()`, `isPaidTheFloor(job | band)`,
  `isNearTheFloor(job)` (within `NEAR_FLOOR` = 10% over it), `getFloorTie()`, `getTieShare()`;
  `PopulationManager.workersPaidTheFloor()` / `workersNearTheFloor()` by band and `getAverageWage()`.

Decided while building (no call was left open):

- The tie's share is held to .20-1.00 of the average wage (`MIN_TIE_SHARE`, `MAX_TIE_SHARE`: a floor at the average
  wage is the most a tie to it can ask), and the floor the tie strikes is held to the dial's own bounds.
- Under AVERAGE_WAGE the one dial still works: `setMinimumWage()` moves the share to match the floor set, so the
  player never has two controls for one figure. The tie's control itself is WS1's.
- The going rate is not saved: it is the ladder's, which the load lays first. The tie and its share are two values
  at the end of the labour market's state array, which `DataSave` already carries; a shorter array (an older save)
  restores PRICES and the default share. **No format bump**: an old save's floor was the going rate, and it loads as
  a real minimum at the going rate, which is what that city had in force.
- The ripple (spec 3.3) is left out, as the study measured: it never fired.

## 2. The preview and the words (WG1-B)

- `PolicyPreview.wageMoveAt()` is gone (it said every wage moves by the floor's ratio). `floorMoveAt(g, floor)`
  gives the floor's own move at a staged floor: the posts filled in the jobs whose target it changes, their pay at
  the two targets, and the whole wage bill; `wageBillMoveAt()` is the bill's ratio. `cityPayrollAt()` is the city's
  payroll plus its staff's move, job by job, through the month's own three sums (`getCategoryPayroll()` for schools
  and care, `Research.payroll()` per field), so at the floor in force it is the payroll exactly and a floor under the
  staff's pay moves it by nothing. `floorJobCover()`: a full-time floor job's take-home over
  `HouseholdAccounts.costOfLivingAlone()` (the figure `livingAlonePressure()` computed inline).
- The floor page's words are true again: `FLOOR_INFO`; the labour market said in four forms (a band pinned and paid
  the floor; pinned at the market's own bottom over it, where lowering the floor changes nothing; nobody pinned and
  N paid the floor; nobody paid it); the band chip "at the floor" or "at the market's bottom"; the rule's tip and the
  card's (i) name the tie; the card's subtitle, its effects ("Workers it moves", "The wage bill, once walked"), the
  city-payroll card, the WAGES summary and the Policy landing's card. PeopleScreen and YearBook one line each.

## 3. The harnesses restated (spec-wages 3.8: Jerus's decision, so restated, not bent)

- **LabourCheck** 1 ("the going rate anchors the ladder, and the floor starts under it"), 2 ("the dial moves only
  the wages under it", against a twin at the default floor), a new 2b, "the floor's tie", and the save sections;
  **WageLadderCheck** and **PolicyDatesCheck** 10 on the going rate (PolicyDatesCheck's glut wage still under three
  quarters of the anchor, as before WG1, and under the floor); **PriceLevelCheck** reads `GOING_RATE` as the rung.
- **PolicyPreviewCheck** and **DiscoveryCheck** 20 on the lift of the jobs under the floor, each a hand sum.
  DiscoveryCheck's village has no floor law yet (1900), so it first asserts a raised floor moves nothing there.
- **ManufacturingCheck** (x4) and **BusinessServicesCheck** (x3) raised the floor in a 1900 town with no floor law,
  where until WG1 the dial moved every wage anyway. Each now opens that one lever first and asserts most workers are
  then paid the floor (884 of 884; 2,963 of 2,991). BusinessServicesCheck's had gone on passing by chance ($9k a
  month to $9k): a fixture that no longer caused its condition.
- **The money fixtures (WG1-D).** On slice A's model the full suite had six harnesses red on a fixture line the
  new path no longer caused. With `DEFAULT_FLOOR_SHARE` set to 1.0 (the old default, which the study measured
  byte-identical to main) all six were green but for KeptCheck's lines main records, so the cause is the path, not
  a fault: where the floor is law, a glut's unskilled wage now falls to the market's own level. Five of them set
  their city's floor at the going rate before the months that matter, the wage each was written against (the
  precedent: BondCheck's `quietTheWorldForTheD()`, the `WEALTHY_CASH` and `openAll()` fixtures), each with its
  figures in a comment:

  | harness | on the .85 path | at the going rate |
  |---|---|---|
  | CurveCheck 7, the steep city at m39 | 1-month 1.80, 1-year 2.44, 10-year 3.44: 1.63 points, the fixture asks 2 | 1.31, 2.21, 3.41 |
  | CentralBankCheck 20, m72 | last year's surplus unused $-349.6k against a $5.2k slice | $441.6k |
  | HoldersCheck 12-13 (`reopeningFixture()`) | the discount per face moved .0082 (asks over .01); a sale raised $59.25k on $59.53k of face | as written |
  | HousingCheck, the city held at 15% | no home closed in 240 months | 0.106 of the landlords' debt on closed homes |
  | KeptCheck 64, the desk's pictures | 0 of 6 months redrawn (DeskBars, PathChart, BuybackLadder, Columns), DeskCurve 5 of 6 | as written |

  BondCheck 1c causes its fixture directly: where no bond stands that the city's fund holds none of (on the .85
  path the fund held some of all seven at m39), the issuer is set short of $5M and issues one, as 7a does.

## 4. The checks

- **The full suite on WG1-A's model** (all 191, the data named): 179 passed. Main's recorded reds aside (HealthCheck,
  MapCheck, KeptCheck's and PriceLevelCheck's lines), the reds were PolicyPreviewCheck (WG1-B), PriceLevelCheck's
  rung and ManufacturingCheck's x4 (WG1-C), the six fixtures of WG1-D, and LongPlaytest's one finding below.
- **`./batch-check.sh --pick` on the branch's tip** (the data named): 48 of 191 harnesses for the 27 files changed,
  Game.java's 159 readers left to the gate with LongPlaytest in their place. **Exit 4 on one line, LongPlaytest's one
  finding, "industry stands farther out than the homes", once, at m2400** (industry 4.14 km from the founding site,
  the homes' band 3.99 km), the same on WG1-A's model. It is the layout ring finding of BA1's notes (section 5:
  "Jerus, 2026-11-04 evening: 'Ship it, pass both'"), which main's own m4000 runs have ended on, left for DM2 and
  whoever takes the district plan's layout next (DM2 is queued): a known class the moved path reaches, outside
  WG1's code, so it passes by name at the gate (`--red-line` on it). Every other picked harness is green or main's
  recorded reds alone; KeptCheck run alone shows exactly the nine lines `docs/notes/gate.txt` records.
- Stale: 0 firm findings. PolicyScreen and PeopleScreen are read by no harness, and no probe reads `FLOOR_INFO` or
  the four forms of the floor page's words; what they say is the model's (`isPinned()`, `isPaidTheFloor()`,
  `workersPaidTheFloor()`). LabourCheck asserts `isPaidTheFloor()` and `isNearTheFloor()`; the two counts by band,
  `workersPaidTheFloor()` and `workersNearTheFloor()`, are read by no harness yet: WS1's page and its harness are
  their first readers.

## 5. The ensemble

`docs/ensemble/WG1.md`: 80 runs on the final model (1232eb31; WG1-D after it touches only harnesses), five at once,
11 minutes, without the data files as 0.9.57's and 0.9.63's ran. **25/26 against 0.9.63's 24/26.** A3 (deflation
spells from m240, 0 [0 to 7] months) passes where 0.9.63 failed it; A2 (the longest spell of inflation over 10% from
m240, 11 [6 to 16] months, < 12 in every seed) fails as it did there (11 [8 to 14]). AUTO2's inflation 2.23% a year
as before; its final population 581,556 [277,938 to 745,456] against 557,672; 16 of 16 seeds reach 40,000 people,
at m1,021 against m1,106; no run has a LongPlaytest finding. 0.9.63 is on 0.9.61's model, so these differences hold
what shipped from 0.9.62 to 0.9.80 as well as WG1.

## 6. Not done

- The Wages page itself, the tie's control among it, is WS1 (queued).
- **The manual** (pending as WG1-M, for the docs pass, which changes the page, publishes and pulls it): section 2's
  wage formula (`docs/manual.md` 800) and the paragraph under it (804, "The **minimum wage is the base** of the whole
  ladder...") are false now. The text for them:
  - the formula's line: `target = goingRate × ratio[job] × clamp(tightness^0.5, 0.70, 4.0)`, and under it
    `wage = max(wage, floor)` "where the minimum wage is law";
  - the paragraph's first two sentences: "The ladder rests on the **going rate**, what an unskilled post pays where
    there are exactly enough workers (the founding unskilled rung), and since {next} the **minimum wage is a real
    minimum**: it lifts only the wages under it, so the Policy dial moves the bottom of the ladder, and reaches the
    treasury's own payroll only once it is raised past their pay. A new city's floor starts at 85% of the going
    rate (Nova Scotia's $17.00 over a material-handling labourer's $20.00 median); under the going rate it binds only
    in a deep glut, and over it an unskilled glut cannot be priced away, so it clears in people instead, which is
    what a binding minimum wage does. The floor is tied to prices (the default: set in founding money and lifted by
    the cost of living), to the average wage at a share, or to nothing (a cash figure that inflation wears down)."
    The rest of the paragraph stands.
- Job Bank's row is in `docs/studies/licences.md` and its credit in `tools/world/ATTRIBUTION.txt` (Open Government
  Licence - Canada, which allows commercial use with the credit).
