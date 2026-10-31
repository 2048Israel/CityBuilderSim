# DW1 - platforms in deep water, at deep water's cost (0.8.86, 2026-10-31)

The batch's record beside its brief, `DW1.md`. Jerus, 2026-10-31: "offshore oil
platforms cant build on deep oceans... so uh", then "Allow deep, costlier
(Recommended)". Two Sonnet 5.5 slices at xhigh, reviewed and committed by Opus.
No save key, no format change, no new building: the extras and the statement's
input lines take the new names as they come.

## 1. What was there

An Offshore Platform (id 98) stood only on a sea oil field whose centre was no
deeper than `Oil.PLATFORM_MAX_DEPTH_M`, 150 m, the fixed steel jacket's limit. A
city whose sea oil is deeper (Valparaiso's, 640 and 750 m on earth-2.bin) had none,
and the card and the oil page said so.

## 2. What changed

- **Past 150 m the platform floats, to 2,914 m.** `FLOATING_MAX_DEPTH_M` is the
  Turritella FPSO's moorings at Shell's Stones field, 9,560 ft, the world's
  water-depth record for a production facility [W34]. Of the four floaters the brief
  names the game models the **FPSO**: it is the one that reaches the record depth
  (tension-leg platforms and spars stand in less: the deepest spar, Perdido's, in
  about 2,400 m), and it stores its crude and offloads it to shuttle tankers, which is how
  the game already carries platform crude ashore. 150 m stays as the line where the
  jacket ends (`isFloating()`).
- **One template whose numbers follow the depth** (the brief's preference): the
  Offshore Platform and the Platform Well of `buildings.json` stand at any depth a
  field allows, and three factors, each exactly 1 at 150 m and under, price them:

  | depth | platform's price | a well's price | upkeep |
  |---|---|---|---|
  | 150.5 m | 11.42 x | 1.30 x | 1.00 x |
  | 200 m | 11.49 x | 1.39 x | 1.04 x |
  | 656 m | 12.07 x | 2.16 x | 2.01 x |
  | 1,000 m | 12.50 x | 2.42 x | 3.43 x |
  | 2,369 m | 14.24 x | 3.42 x | 14.89 x |
  | 2,900 m | 14.92 x | 3.81 x | 21.85 x |

  They are ratios of EIA's NEMS offshore cost equations [W13] (water depth in feet,
  2003 dollars, so the dollar year and NEMS's later uniform multipliers cancel): the
  platform is eq 3-10 (a floating production system, "a semi-submersible or converted
  tanker") over eq 3-7 (the fixed platform) at 150 m, 12 slots; a well is the
  cheaper floating rig's (eq 3-5 semi-submersible, eq 3-6 drillship; they cross near
  571 m) over the jack-up's (eq 3-4) at 150 m, all at `WELL_DRILLING_DEPTH_FT`
  10,000 ft, the drilling depth at which NEMS's development-well equation gives the
  Platform Well's own D$14M at 150 m. The rig equations are where NEMS carries the
  rigs' day rates by water depth. The upkeep is eq 3-15, the platform's operating
  cost a year.
- **The premium is works on the order**, as ET6's bridge span is (`Game.deepWater()`):
  (factor - 1) x the template's construction points, owed on the order's site and
  billed with it, their money at the builders' price a point today, in the sticker
  and taxed with the work. The city's path and a business's both owe and bill them.
  The investors' `getCostOf()` sees the same money through a hook Game hands in (ET4's
  way), so what a business reckons is what the quote charges; the planner prices a
  platform and its wells together on the field it would stand them on
  (`getCostOfAt()`). A floating platform therefore takes about 12 times a jacket's
  builders' work: the research's 5+ years against 1-2 [W6].
- **The upkeep** is a service bought from the world's offshore fleet, as the shuttle
  tankers are: each floating platform, a month, (upkeep factor - 1) x the jacket's
  running cost, booked as "Deep-water upkeep" (`payDeepWaterUpkeep()`, after the
  shuttle's in `endOfMonth()`), and carried into the planner's earnings.
- **A new platform stands on a fixed field before a floating one**, and on the
  shallower of two floating ones (`freeField()`); a list with no floating field picks
  as it always did.
- **Deep wells decline faster.** The IEA's observed post-peak decline [W6], the source
  of the shallow platform well's 8.5% a year, is 10.3% a year for deep and
  ultra-deep offshore (Table 1), with deepwater from 450 m (its footnote 2). From
  `DEEP_DECLINE_FROM_M` 450 m a platform's wells keep `DEEP_KEEPS_A_YEAR` .897 a year
  past the plateau, which stays 36 months (the report gives none apart by depth);
  they wear out at 291 months, not 348. A well is not bound to a platform in this
  model, so the platform vintages are dealt to the platforms oldest to oldest, as the
  oil page already dealt them (`Oil.deal()`, moved from `OilView`), and each piece
  lifts at its platform's rate. With no platform at 450 m or deeper the lift and the
  retirements run the old code, to the bit.
- **The screens:** the platform card says jacket to 150 m, FPSO to 2,914 m, and what
  the next one costs where it would stand ("The next stands in 656 m: 12.1 times a
  jacket's price, its wells 2.2 times, its upkeep 2.0 times"); the well's card its
  factor; the quote a line "Deep water, N m down: points, money"; the refusal only
  past 2,914 m. The oil page counts floating platforms and last month's upkeep, its
  sea sites are those a platform takes, and a deep platform's phase reads -10.3% a
  year. `Oil.shallowFields()` is `platformFields()`.

## 3. Sources and licences (Jerus, 2026-10-31: only commercial-use sources ship)

| key | source | licence | what it gives |
|---|---|---|---|
| W13 | US EIA, NEMS Oil and Gas Supply Module documentation (2023), Offshore Supply Submodule, eqs 3-4 to 3-7, 3-10, 3-15 | public domain (eia.gov/about/copyrights_reuse.php; LC1's row for the EIA) | the three cost curves |
| W34 | BOEM, Deepwater Gulf of Mexico 2019, OCS Report BOEM 2021-005 | a US government work, public domain; the record depth is a fact besides | 2,914 m |
| W6 | IEA, The Implications of Oil and Gas Field Decline Rates (2025), Table 1 and footnote 2 | CC BY 4.0, printed on every page | 10.3% against 8.5%, and the 450 m line |

The 2023 PDF sets its equations in a font the text tools garble; the 2014 edition
(same equations, before the uniform multipliers) was read for the terms, and the
2023 coefficients checked against it. The brief asked for "development cost per
barrel of reserves by water depth": no source of that figure was found that allows
commercial use (the per-barrel series are GlobalData's, Wood Mackenzie's and
Rystad's), so the curve is NEMS's by structure and by well, which the brief's day
rates also come through.

## 4. The harness

`WellCheck` 8 re-premised (150.5 m takes a floating platform; past 2,914 m none),
and two sections. **14, deep water:** the factors 1 at and under 150 m and above it
past, rising over a 41-step ladder to 2,914 m, each the ratio of the model's own NEMS
functions and the curve the EIA's to three places; `freeField()`'s order; on a town
whose sea field the fixture moves to 600 m (`setPlatformFieldsForTest()`), the
quote's points and money, the total, the months, an order of two, the investors'
`getCostOf()`, the planner's package and upkeep, the order owing the points and
costing the quote, the builders finishing it, the wells quoted at its depth, the
upkeep booked as the import with the audit closing, the page's lines, and the same
platform on its real shallow depth booking nothing. **15, deep wells decline
faster:** the profile to the bit for fifty years, the 291-month life, a town with
platforms at 449.99 and 450 m whose lift is each platform's dealt wells at its own
rate and, with both short of the line, the old lift to the bit; the worn-out count
by each platform's life; the page's forecast. `EarthFoundingCheck`'s Valparaiso line
now reads "a floating platform stands in it, at deep water's cost" (640 and 750 m,
12.0 and 12.2 times a jacket's price); the North Sea line holds.

## 5. Checks

`--pick`: see the ship file's paragraph and the report. The playtest's founding owns
no sea oil field, so every changed path is closed to it (a sea field must be owned
for any of them to run); the ensemble is skipped on that ground (EN1).

## 6. Calls (★), each with the recommendation the orchestrator takes

1. ★ **The investors will not build a floating platform at today's prices.** The
   game's well lifts the same 415 t a month at any depth, so a deep barrel costs 3 to
   4.5 times a shelf one here; in the world a deepwater well lifts about 2,400 b/d
   against the shelf's 100 [W1][W15], and a deep barrel costs about the same. At
   600 m the planner holds: "a platform and its wells would not carry the interest on
   their whole cost". The player can build one from the card. *Recommended: keep it,
   as Jerus chose "costlier"; a deep well that lifts more would drain the offshore
   pool in months, which is why the research's 18,000 b/d platform was turned down
   (spec-oil 7).*
2. ★ **The upkeep is small in money** (about D$36k a month at 600 m, D$0.9M at 2,900 m),
   because the jacket's running cost is its twelve posts' wages, well under NEMS's
   operating cost. *Recommended: keep the ratio on the game's own calibration.*
3. ★ **A pipe's kilometre costs the same at any depth**; NEMS prices pipe by water
   depth too. *Recommended: leave it; the pipe rule weighs it and a deep field's pipe
   is rarely laid.*
4. ★ **Credit lines.** The EIA asks "Source: U.S. Energy Information Administration"
   with the date, the IEA its CC BY 4.0 credit; the IEA's 8.5% shipped before DW1.
   *Recommended: LC1's attribution file takes them with the rest.*
