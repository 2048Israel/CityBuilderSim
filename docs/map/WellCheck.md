# WellCheck.java - 1,576 lines · 30 methods · 7 constants · harnesses

`ham/citybuildersim/WellCheck.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The oil wells' lives (0.7.84, batch O7; docs/studies/spec-oil.md 2.6 and 4's
> WellCheck, the land half) - and since 0.7.91 the sea's (batch O10; spec-oil
> 2.7, 2.11): the platforms, their wells, the shuttle tankers and the pipes;
> and since 0.7.93 the two pools they lift (batch O10b).
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. THE PROFILES, TO THE BIT: a land well lifts LAND_KEEPS_A_YEAR (nine
>      tenths) of its nameplate to the power of its years - 10% less a year -
>      and a platform well all of it through PLATFORM_PLATEAU_MONTHS, then
>      PLATFORM_KEEPS_A_YEAR to the power of its years past them.
> 
>   2. RETIRED AT 263 MONTHS ON LAND AND 348 ON A PLATFORM: the first age at
>      which a well lifts under ten barrels a day, 41.5 t of its 415.
> 
>   3. A TOWN'S LIFT IS ITS VINTAGES': each batch of wells, from the month it
>      opened, at its profile, summed oldest first - the nameplate the month
>      is struck on, to the bit; a new well lifts its whole nameplate the
>      month it opens; a read moves nothing; and the world's oil is
>      conserved to the tonne.
> 
>   4. A WORN-OUT WELL IS RETIRED, OLDEST FIRST, AND ITS SITE REFILLED: at 263
>      months, by the month's retirements, and the sector's one-well-a-month
>      rule drills its site again.
> 
>   5. DRY AND SEA SITES ARE KEPT APART: a land well stands only on a dry site
>      - the order, the card and the investors all refuse a sea one - and
>      wells already standing past the dry sites, as a save from before 0.7.84
>      carries them on the sea's, are kept, lifting the ground pool.
> 
>   6. THE VINTAGES CROSS A SAVE, to the bit, as the Oil sector's extras; and a
>      save from before them reads every well new, opened the first month the
>      city plays.
> 
>   7. A WELL HAS ONE POST, a diploma's (the research's Q13: 0.2-0.4 workers a
>      well [W12]; three until 0.7.84).
> 
>   8. A PLATFORM STANDS ON A SEA FIELD, its wells in its slots: a field in
>      the sea FLOATING_MAX_DEPTH_M deep or less (a fixed jacket to
>      PLATFORM_MAX_DEPTH_M, a floating platform past it, DW1); slots at most 12
>      and at most the field's sea sites; a platform's well only in a free
>      slot, a land well only on a dry site - the two kept apart; the
>      platform profile's lift, to the bit; W conserved; a platform well
>      retired at 348 months and its slot refilled.
> 
>   9. THE SHUTTLE TANKERS: platform crude sold at home pays CRUDE's band
>      freight a tonne, the offshore pool's share of the month's lift, to the
>      bit, booked as the Oil sector's imported "Shuttle tankers"; crude
>      shipped abroad pays none; the audit closes.
> 
>  10. THE PIPELINE: the rule passes on a big field and fails on a small one,
>      and on land; a field's whole pipe stands for its crude and the shuttle
>      tankers carry none of it.
> 
>  11. THE PLATFORMS AND PIPELINES CROSS A SAVE, to the bit; a save from
>      before them reads the platforms standing anew on their field.
> 
>  12. THE PLANNER WEIGHS THE THREE KINDS AT SEA after the land well: a
>      platform's jacket as a package with its wells, by the interest test; a
>      platform well into a free slot - since 0.7.113 each, and the land well
> ... (46 more lines in the source)

**Uses:** [Oil](Oil.md) (225), [Game](Game.md) (87), [Good](Good.md) (42), [LandManager](LandManager.md) (39), [Resource](Resource.md) (37), [OilView](OilView.md) (20), [BuildingsTemplate](BuildingsTemplate.md) (18), [Sector](Sector.md) (12), [Sectors](Sectors.md) (11), [Deposit](Deposit.md) (11), [BuildCard](BuildCard.md) (11), [OilCheck](OilCheck.md) (9), [BusinessInvestment](BusinessInvestment.md) (9), [GameFiles](GameFiles.md) (7), [Founding](Founding.md) (7), [World](World.md) (7), [BuildingManager](BuildingManager.md) (4), [LandParcel](LandParcel.md) (2), [MiningCheck](MiningCheck.md) (2), [Statement](Statement.md) (2), [Formats](Formats.md) (2), [GameVersion](GameVersion.md) (2), [BuildingsStacks](BuildingsStacks.md) (2), [LongPlaytest](LongPlaytest.md) (1), [Mining](Mining.md) (1), [BuildingCatalog](BuildingCatalog.md) (1), [JobType](JobType.md) (1), [GoodsMarket](GoodsMarket.md) (1), [SaveFileCheck](SaveFileCheck.md) (1), [EconomyManager](EconomyManager.md) (1)

**Used by (2):** [MapCheck](MapCheck.md), [OilViewCheck](OilViewCheck.md)

## Sections

| line | section |
|---:|---|
| 243 | 1. THE PROFILES |
| 268 | 2. THEIR LIFE |
| 289 | 3. A TOWN'S LIFT |
| 357 | 4. WORN OUT, RETIRED, REFILLED |
| 427 | 5. DRY AND SEA SITES |
| 494 | 6. ACROSS A SAVE |
| 548 | 7. ONE POST |
| 566 | THE OIL AT SEA (0.7.91) |
| 605 | 8. A PLATFORM |
| 736 | 9. THE SHUTTLE TANKERS |
| 797 | 10. THE PIPELINE |
| 854 | 11. ACROSS A SAVE |
| 914 | 12. THE PLANNER |
| 974 | 13. THE TWO POOLS (0.7.93) |
| 1171 | 14. DEEP WATER (DW1) |
| 1412 | 15. THE DEEP WELLS DECLINE FASTER (DW1) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 158 | `WellCheck.FILES` | `new java.util.IdentityHashMap<>()` | Each fixture town's save folder (section 6). |
| 161 | `WellCheck.WELL_TONNES` | `415` | A well's nameplate a month, the template's: 415 t, a hundred barrels a day. |
| 164 | `WellCheck.FILL` | `50_000` | The Strategic Reserve's fill a month in the sea sections (OilCheck 16's order): more than the wells lift, so the city buys every tonne of theirs at home. |
| 167 | `WellCheck.PLENTY` | `50_000_000` | The oil a town is handed: far more than its wells lift in the months a section runs, so the ground never limits them. |
| 977 | `WellCheck.GROUND` | `2 * WELL_TONNES` | Section 13's ground pool: two months of a land well's nameplate, so it is worked out inside the months the section runs. |
| 980 | `WellCheck.LAST_AT_SEA` | `100` | Section 13's offshore pool left for its other half: less than a month of the platform wells' lift, so it is worked out in one. |
| 1174 | `WellCheck.FEET_PER_METRE` | `1 / 0.3048` | Feet to the metre as NEMS takes the water depth [W13]: the conversion the curve's ratios are read at. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 124 | `static int fails` |  |
| 125 | `static PrintStream out` |  |
| 126 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 122 | 1455 | **type** `public class WellCheck` | The oil wells' lives (0.7.84, batch O7; docs/studies/spec-oil.md 2.6 and 4's WellCheck, the land half) - and since 0.7.91 the sea's (batch O10; spec-oil 2.7, 2.11): the platforms, their wells, the shuttle tankers and ... |
| 128 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 133 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 138 | 5 | `static void quietly(Runnable r)` |  |
| 144 | 6 | `static<T> T quietlyGet(java.util.function.Supplier<T> s)` |  |
| 151 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 174 | 24 | `static Game town(String label, int houses, double spare, long seed)` | OilCheck's town: houses, shops, bakeries to work in, power, water, roads and builders, standing, on ground a quarter more than they take and `spare` square feet more, on the world `seed` (0 the default). |
| 200 | 5 | `static double liftOf(List<Vintage> vintages, double each, int month)` | The lift the vintages say, as the spec writes it: each vintage's wells x a well's nameplate x its profile at its age, oldest first. |
| 207 | 3 | `static boolean conserved(LandManager land, double world)` | Whether the world's oil is the town's unowned + remaining + extracted, to the tonne. |
| 211 | 31 | `public static void main(String[] args) throws Exception` |  |

### 1. THE PROFILES (lines 243-267)

| line | len | member | says |
|---:|---:|---|---|
| 245 | 22 | `static void theProfiles()` |  |

### 2. THEIR LIFE (lines 268-288)

| line | len | member | says |
|---:|---:|---|---|
| 270 | 18 | `static void theirLife()` |  |

### 3. A TOWN'S LIFT (lines 289-356)

| line | len | member | says |
|---:|---:|---|---|
| 291 | 65 | `static Game aTownsLift()` |  |

### 4. WORN OUT, RETIRED, REFILLED (lines 357-426)

| line | len | member | says |
|---:|---:|---|---|
| 359 | 67 | `static Game wornOutWellsRetire()` |  |

### 5. DRY AND SEA SITES (lines 427-493)

| line | len | member | says |
|---:|---:|---|---|
| 429 | 64 | `static void drySitesAndSea()` |  |

### 6. ACROSS A SAVE (lines 494-547)

| line | len | member | says |
|---:|---:|---|---|
| 496 | 51 | `static void acrossASave(Game g) throws Exception` |  |

### 7. ONE POST (lines 548-565)

| line | len | member | says |
|---:|---:|---|---|
| 550 | 15 | `static void onePost()` |  |

### THE OIL AT SEA (0.7.91) (lines 566-604)

| line | len | member | says |
|---:|---:|---|---|
| 569 | 3 | `static Deposit seaField()` | The sea field the sea sections stand on: section 5's, the nearest oil field to SEA_OIL_SEED's site, in the sea. |
| 580 | 24 | `static Game seaTown(String label)` | Section 4's staffed town on SEA_OIL_SEED's world, owning the ground over the sea field (bought, as section 5 buys it) and handed plenty of oil; every sector's own orders held, so the ground and the hands the sections ... |

### 8. A PLATFORM (lines 605-735)

| line | len | member | says |
|---:|---:|---|---|
| 607 | 128 | `static Game aPlatform()` |  |

### 9. THE SHUTTLE TANKERS (lines 736-796)

| line | len | member | says |
|---:|---:|---|---|
| 738 | 58 | `static void theShuttleTankers(Game g)` |  |

### 10. THE PIPELINE (lines 797-853)

| line | len | member | says |
|---:|---:|---|---|
| 799 | 54 | `static void thePipeline(Game g)` |  |

### 11. ACROSS A SAVE (lines 854-913)

| line | len | member | says |
|---:|---:|---|---|
| 856 | 57 | `static void seaAcrossASave(Game g) throws Exception` |  |

### 12. THE PLANNER (lines 914-973)

| line | len | member | says |
|---:|---:|---|---|
| 916 | 57 | `static void thePlanner()` |  |

### 13. THE TWO POOLS (0.7.93) (lines 974-1170)

| line | len | member | says |
|---:|---:|---|---|
| 983 | 3 | `static boolean close(double a, double b)` | Whether two figures agree but for a rounding: within a billionth of the larger, or of a tonne. |
| 988 | 4 | `static String lineValue(List<Sector.Line> lines, String label)` | The value of the line of `lines` with this label, or null. |
| 994 | 8 | `static int untilWorkedOut(Game g, java.util.function.DoubleSupplier left, int most)` | Months played until a pool is worked out to the tonne's last bit (left == 0), at most `most`; the months played. |
| 1003 | 167 | `static void theTwoPools() throws Exception` |  |

### 14. DEEP WATER (DW1) (lines 1171-1411)

| line | len | member | says |
|---:|---:|---|---|
| 1177 | 3 | `static Deposit anotherField(Deposit d, int index, int sites)` | A field the check makes of `d`: the same place, another number in its cell and `sites` sites - another field to the platforms' records. |
| 1181 | 230 | `static void deepWater()` |  |

### 15. THE DEEP WELLS DECLINE FASTER (DW1) (lines 1412-1576)

| line | len | member | says |
|---:|---:|---|---|
| 1414 | 162 | `static void deepDecline()` |  |

