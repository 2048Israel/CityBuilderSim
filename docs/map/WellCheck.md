# WellCheck.java - 1,645 lines · 31 methods · 7 constants · harnesses

`ham/citybuildersim/WellCheck.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

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
> ... (51 more lines in the source)

**Uses:** [Oil](Oil.md) (227), [Game](Game.md) (88), [Good](Good.md) (42), [LandManager](LandManager.md) (40), [Resource](Resource.md) (39), [OilView](OilView.md) (20), [BuildingsTemplate](BuildingsTemplate.md) (19), [Sector](Sector.md) (14), [Sectors](Sectors.md) (12), [Deposit](Deposit.md) (11), [BuildCard](BuildCard.md) (11), [OilCheck](OilCheck.md) (9), [BusinessInvestment](BusinessInvestment.md) (9), [GameFiles](GameFiles.md) (7), [Founding](Founding.md) (7), [World](World.md) (7), [BuildingManager](BuildingManager.md) (4), [LandParcel](LandParcel.md) (2), [MiningCheck](MiningCheck.md) (2), [Statement](Statement.md) (2), [Formats](Formats.md) (2), [GameVersion](GameVersion.md) (2), [BuildingsStacks](BuildingsStacks.md) (2), [LongPlaytest](LongPlaytest.md) (1), [Mining](Mining.md) (1), [BuildingCatalog](BuildingCatalog.md) (1), [JobType](JobType.md) (1), [GoodsMarket](GoodsMarket.md) (1), [SaveFileCheck](SaveFileCheck.md) (1), [EconomyManager](EconomyManager.md) (1)

**Used by (3):** [KeptCheck](KeptCheck.md), [MapCheck](MapCheck.md), [OilViewCheck](OilViewCheck.md)

## Sections

| line | section |
|---:|---|
| 249 | 1. THE PROFILES |
| 274 | 2. THEIR LIFE |
| 295 | 3. A TOWN'S LIFT |
| 363 | 4. WORN OUT, RETIRED, REFILLED |
| 452 | 5. DRY AND SEA SITES |
| 519 | 6. ACROSS A SAVE |
| 573 | 7. ONE POST |
| 591 | THE OIL AT SEA (0.7.91) |
| 630 | 8. A PLATFORM |
| 761 | 9. THE SHUTTLE TANKERS |
| 822 | 10. THE PIPELINE |
| 879 | 11. ACROSS A SAVE |
| 939 | 12. THE PLANNER |
| 999 | 13. THE TWO POOLS (0.7.93) |
| 1196 | 14. DEEP WATER (DW1) |
| 1439 | 15. THE DEEP WELLS DECLINE FASTER (DW1) |
| 1604 | 16. A WELL OPENS THE MONTH TWO WEAR OUT |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 163 | `WellCheck.FILES` | `new java.util.IdentityHashMap<>()` | Each fixture town's save folder (section 6). |
| 166 | `WellCheck.WELL_TONNES` | `415` | A well's nameplate a month, the template's: 415 t, a hundred barrels a day. |
| 169 | `WellCheck.FILL` | `50_000` | The Strategic Reserve's fill a month in the sea sections (OilCheck 16's order): more than the wells lift, so the city buys every tonne of theirs at home. |
| 172 | `WellCheck.PLENTY` | `50_000_000` | The oil a town is handed: far more than its wells lift in the months a section runs, so the ground never limits them. |
| 1002 | `WellCheck.GROUND` | `2 * WELL_TONNES` | Section 13's ground pool: two months of a land well's nameplate, so it is worked out inside the months the section runs. |
| 1005 | `WellCheck.LAST_AT_SEA` | `100` | Section 13's offshore pool left for its other half: less than a month of the platform wells' lift, so it is worked out in one. |
| 1199 | `WellCheck.FEET_PER_METRE` | `1 / 0.3048` | Feet to the metre as NEMS takes the water depth [W13]: the conversion the curve's ratios are read at. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 129 | `static int fails` |  |
| 130 | `static PrintStream out` |  |
| 131 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 127 | 1519 | **type** `public class WellCheck` | The oil wells' lives (0.7.84, batch O7; docs/studies/spec-oil.md 2.6 and 4's WellCheck, the land half) - and since 0.7.91 the sea's (batch O10; spec-oil 2.7, 2.11): the platforms, their wells, the shuttle tankers and ... |
| 133 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 138 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 143 | 5 | `static void quietly(Runnable r)` |  |
| 149 | 6 | `static<T> T quietlyGet(java.util.function.Supplier<T> s)` |  |
| 156 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 179 | 24 | `static Game town(String label, int houses, double spare, long seed)` | OilCheck's town: houses, shops, bakeries to work in, power, water, roads and builders, standing, on ground a quarter more than they take and `spare` square feet more, on the world `seed` (0 the default). |
| 205 | 5 | `static double liftOf(List<Vintage> vintages, double each, int month)` | The lift the vintages say, as the spec writes it: each vintage's wells x a well's nameplate x its profile at its age, oldest first. |
| 212 | 3 | `static boolean conserved(LandManager land, double world)` | Whether the world's oil is the town's unowned + remaining + extracted, to the tonne. |
| 216 | 32 | `public static void main(String[] args) throws Exception` |  |

### 1. THE PROFILES (lines 249-273)

| line | len | member | says |
|---:|---:|---|---|
| 251 | 22 | `static void theProfiles()` |  |

### 2. THEIR LIFE (lines 274-294)

| line | len | member | says |
|---:|---:|---|---|
| 276 | 18 | `static void theirLife()` |  |

### 3. A TOWN'S LIFT (lines 295-362)

| line | len | member | says |
|---:|---:|---|---|
| 297 | 65 | `static Game aTownsLift()` |  |

### 4. WORN OUT, RETIRED, REFILLED (lines 363-451)

| line | len | member | says |
|---:|---:|---|---|
| 365 | 86 | `static Game wornOutWellsRetire()` |  |

### 5. DRY AND SEA SITES (lines 452-518)

| line | len | member | says |
|---:|---:|---|---|
| 454 | 64 | `static void drySitesAndSea()` |  |

### 6. ACROSS A SAVE (lines 519-572)

| line | len | member | says |
|---:|---:|---|---|
| 521 | 51 | `static void acrossASave(Game g) throws Exception` |  |

### 7. ONE POST (lines 573-590)

| line | len | member | says |
|---:|---:|---|---|
| 575 | 15 | `static void onePost()` |  |

### THE OIL AT SEA (0.7.91) (lines 591-629)

| line | len | member | says |
|---:|---:|---|---|
| 594 | 3 | `static Deposit seaField()` | The sea field the sea sections stand on: section 5's, the nearest oil field to SEA_OIL_SEED's site, in the sea. |
| 605 | 24 | `static Game seaTown(String label)` | Section 4's staffed town on SEA_OIL_SEED's world, owning the ground over the sea field (bought, as section 5 buys it) and handed plenty of oil; every sector's own orders held, so the ground and the hands the sections ... |

### 8. A PLATFORM (lines 630-760)

| line | len | member | says |
|---:|---:|---|---|
| 632 | 128 | `static Game aPlatform()` |  |

### 9. THE SHUTTLE TANKERS (lines 761-821)

| line | len | member | says |
|---:|---:|---|---|
| 763 | 58 | `static void theShuttleTankers(Game g)` |  |

### 10. THE PIPELINE (lines 822-878)

| line | len | member | says |
|---:|---:|---|---|
| 824 | 54 | `static void thePipeline(Game g)` |  |

### 11. ACROSS A SAVE (lines 879-938)

| line | len | member | says |
|---:|---:|---|---|
| 881 | 57 | `static void seaAcrossASave(Game g) throws Exception` |  |

### 12. THE PLANNER (lines 939-998)

| line | len | member | says |
|---:|---:|---|---|
| 941 | 57 | `static void thePlanner()` |  |

### 13. THE TWO POOLS (0.7.93) (lines 999-1195)

| line | len | member | says |
|---:|---:|---|---|
| 1008 | 3 | `static boolean close(double a, double b)` | Whether two figures agree but for a rounding: within a billionth of the larger, or of a tonne. |
| 1013 | 4 | `static String lineValue(List<Sector.Line> lines, String label)` | The value of the line of `lines` with this label, or null. |
| 1019 | 8 | `static int untilWorkedOut(Game g, java.util.function.DoubleSupplier left, int most)` | Months played until a pool is worked out to the tonne's last bit (left == 0), at most `most`; the months played. |
| 1028 | 167 | `static void theTwoPools() throws Exception` |  |

### 14. DEEP WATER (DW1) (lines 1196-1438)

| line | len | member | says |
|---:|---:|---|---|
| 1202 | 3 | `static Deposit anotherField(Deposit d, int index, int sites)` | A field the check makes of `d`: the same place, another number in its cell and `sites` sites - another field to the platforms' records. |
| 1206 | 232 | `static void deepWater()` |  |

### 15. THE DEEP WELLS DECLINE FASTER (DW1) (lines 1439-1603)

| line | len | member | says |
|---:|---:|---|---|
| 1441 | 162 | `static void deepDecline()` |  |

### 16. A WELL OPENS THE MONTH TWO WEAR OUT (lines 1604-1645)

| line | len | member | says |
|---:|---:|---|---|
| 1606 | 39 | `static void openingAsTwoWearOut()` |  |

