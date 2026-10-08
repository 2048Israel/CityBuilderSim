# LandConversion.java - 216 lines · 7 methods · 9 constants · model

`ham/citybuildersim/LandConversion.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> A saved city's land put on the block grid, and a city's land drawn again to hold a new figure: a format-31 save's lanes snapped to whole blocks, an older save's one figure drawn as a centre of blocks to the plot round the site found for it, its iron as its save had it, and twenty-four offers listed round it.
> 
> WHY THIS EXISTS (0.7.57, batch J1b; the project's spec-land.md 2.9). A save
> of format 30 or older carries the land as one figure, the square feet owned,
> a pool of iron sites and tonnes, and nine parcels with no place. Loaded as
> it was, its pooled ore and its listing would be read wrongly by a build
> whose ground is real (hence SAVE_FORMAT 31), so the load converts it, once.
> 
> ON THE BLOCK GRID SINCE 0.7.67 (batch M3, SAVE_FORMAT 32; the project's
> spec-grid.md 2.6). The land is whole blocks now (CityLand, LandGrid), and
> every save written before is put on them once, at load, by GridConversion:
> 
>   format 31   (0.7.57 to 0.7.66: a centre and forty lanes; convertLanes())
>               its ground read as it was (LegacyLand), snapped one level
>               finer than its offers with its fields deciding, so no field
>               changes hands and a field it held only part of keeps exactly
>               its sites; one converted holding, its books the plots drawn,
>               its sites and amounts the save's to the bit, what it took out
>               (E) as saved; its purchase records kept as history, no money
>               moving (spec-grid stars 11 and 12);
>   format 30   (convert()) J1b's site search, a coastal cell at a time,
>   and older   with a fifth test: the L-infinity square holding the city's
>               dry ground is at least CENTRE_DRY_MIN dry, with the sea within
>               its half-side and CENTRE_SEA_KM (if none of the nearest
>               SITE_CELLS passes, the driest found); round it, rings of
>               blocks of the city's level, the last split down to the plot,
>               holding the save's dry ground to within a plot; its iron the
>               save's sites, raised to cover the mines standing and on
>               order, and its tonnes, never more (spec-land star 9), with a
>               legacy field when the world laid no iron on the ground drawn;
>               nothing taken out yet.
> 
> Either way the city's square feet become the converted ground's dry plots
> (the books follow the map), and twenty-four offers are listed afresh round
> it, once - the one exception to "never rerolled" - after the load has put
> back the prices, the buildings and the allocation (Game): at the prices the
> save last struck, as a load strikes none; the next month strikes them on
> the converted ground.
> 
> Restating (restate()) draws a city's land again round the same site, the
> centre holding the whole of a new figure as an older save's is drawn: what
> LandManager.setOwnedSqFt() does - a harness's ground by fiat, and the load
> of a save whose square feet disagree with its ground by more than a plot.
> The city's iron and what it has taken out are kept as they were; the other
> resources are the world's fields centred on the ground drawn; the figure is
> the one asked for, the ground holding it to within a plot.

**Uses:** [World](World.md) (16), [CityLand](CityLand.md) (12), [LegacyLand](LegacyLand.md) (7), [LandManager](LandManager.md) (6), [GridConversion](GridConversion.md) (6), [Resource](Resource.md) (4)

**Used by (7):** [ConversionCheck](ConversionCheck.md), [Game](Game.md), [GridConversion](GridConversion.md), [LandCheck](LandCheck.md), [LandManager](LandManager.md), [ScaleCheck](ScaleCheck.md), [WaterCheck](WaterCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 56 | `LandConversion.CENTRE_DRY_MIN` | `0.8` | The least share of a converted city's centre that is dry ground: 80% (spec-land 2.9), so a played city is not put on a peninsula two-thirds sea. |
| 59 | `LandConversion.CENTRE_SEA_KM` | `2` | ...with the sea within its half-side and this many kilometres: 2, a city on a coast. |
| 62 | `LandConversion.LAST_FORMAT_BEFORE` | `30` | The last save format whose land is one figure, converted from it: 30, the format before the land was on the world. |
| 65 | `LandConversion.LAST_LANES_FORMAT` | `31` | The last save format whose land is lanes, snapped to blocks at load: 31 (0.7.57 to 0.7.66; GameVersion.SAVE_FORMAT 32 holds blocks). |
| 68 | `LandConversion.SAME_GROUND_PLOTS` | `1` | How near a save's square feet must be to its land's dry ground to be the same ground: within one plot of it (0.7.67; a part in a billion before, when a centre held its figure exactly) - a figure set by hand is held by... |
| 71 | `LandConversion.SITE_CELLS` | `64` | How many coastal cells' sites are tried, nearest the world's middle first: 64. |
| 74 | `LandConversion.LEGACY_FIELD_KM` | `1` | A legacy iron field stands at least this far from the site: 1 km (spec-land 2.4). |
| 77 | `LandConversion.LEGACY_DRAWS` | `64` | Draws for the legacy field's dry plot: 64. |
| 80 | `LandConversion.LEGACY_STREAM` | `0x1E6AC7L` | The stream the legacy field's plot is drawn from. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 51 | 166 | **type** `public final class LandConversion` | A saved city's land put on the block grid, and a city's land drawn again to hold a new figure: a format-31 save's lanes snapped to whole blocks, an older save's one figure drawn as a centre of blocks to the plot round... |
| 53 | 1 | `private LandConversion()` |  |
| 83 | 3 | `public static boolean sameGround(double ownedSqFt, double landDrySqFt)` | Whether a save's figure of dry ground, in square feet, is its land's: within SAME_GROUND_PLOTS plots of it. |
| 94 | 20 | `public static long[] site(World world, double dryKm2)` | Where an older city's centre goes: the first site of the founding search, a coastal cell at a time, whose square holding dryKm2 passes the fifth test; or, when none of the nearest SITE_CELLS does, the one whose square... |
| 124 | 10 | `public static void convert(LandManager lm, long seed, double drySqFt, int ironSites, double ironTonnes, int minesCommitted)` | Converts an older city's land (format 30 and before; spec-grid 2.6): on the world its seed makes, at the site site() finds, a centre of blocks holding drySqFt of dry ground to within a plot (GridConversion. |
| 145 | 15 | `public static boolean convertLanes(LandManager lm, long seed, double[] centre, double[] lanes, double[][] purchases, String gam...` | Converts a format-31 save's land (spec-grid 2.6, star 11): its centre, lanes and purchases as saved, snapped (GridConversion.fromLanes()) by the build that wrote it (gameVersion: whether it held fields site by site), ... |
| 170 | 22 | `static long[] legacyPlot(World world, long seed, long sx, long sy, double reach, java.util.function.BiPredicate<Long, Long> onG...` | The legacy field's plot round the site (sx, sy) of ground reaching `reach` plots from it (L-infinity): the first of LEGACY_DRAWS draws from the seed within the larger of `reach` and LEGACY_FIELD_KM each way, at least ... |
| 204 | 12 | `public static void restate(LandManager lm, double drySqFt)` | Draws a city's land again to hold drySqFt of dry ground (spec-grid 2.6: restate() draws as a format-30 save is drawn): the centre round the same site, rings of blocks of the level the figure makes, the last split down... |

