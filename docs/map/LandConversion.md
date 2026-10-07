# LandConversion.java - 159 lines · 5 methods · 8 constants · model

`ham/citybuildersim/LandConversion.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> An older city's land put on the world, and a city's land drawn again to hold a new figure: the site found for it, the centre holding exactly the dry ground it owned, its iron as its save had it, and forty offers listed from the centre's edge.
> 
> WHY THIS EXISTS (0.7.57, batch J1b; the project's spec-land.md 2.9). A save
> of format 30 or older carries the land as one figure, the square feet owned,
> a pool of iron sites and tonnes, and nine parcels with no place. Loaded as
> it was, its pooled ore and its listing would be read wrongly by a build
> whose ground is real (hence SAVE_FORMAT 31), so the load converts it, once:
> 
>   the seed    the save's own (DataSave.getFounding(): kept since 0.7.56, or
>               derived from its name, founding treasury, ground and month);
>   the site    the founding search, a coastal cell at a time, with a fifth
>               test: the L-infinity square holding the city's dry ground is
>               at least CENTRE_DRY_MIN dry, with the sea within its half-side
>               and CENTRE_SEA_KM - the founding site itself passes for a new
>               city's 0.28 km2, not for a played city's 90 km2 on a coast;
>               if none of the nearest SITE_CELLS passes, the driest found;
>   the centre  sized to hold exactly the ground the save owned;
>   the iron    the save's sites, raised to cover the mines standing and on
>               order, and its tonnes, never more (spec-land star 9) - the
>               centre's own fields are the map's, scaled to that (batch J3),
>               or one legacy field when the world put none in it;
>   the offers  forty, at today's prices; the old nine parcels go, once;
>   E           nothing taken out yet.
> 
> Restating (restate()) draws a city's land again round the same site, the
> centre holding the whole of a new figure: what LandManager.setOwnedSqFt()
> does once the land exists - a harness's ground by fiat, and the load of a
> save whose square feet disagree with its ground, which is what a copy of a
> city K times over is (ScaleCheck). The city's iron and what it has taken out
> are kept as they were; the other resources are what the world holds in the
> new centre.

**Uses:** [World](World.md) (15), [CityLand](CityLand.md) (14), [Resource](Resource.md) (5), [LandManager](LandManager.md) (4)

**Used by (5):** [Game](Game.md), [LandCheck](LandCheck.md), [LandManager](LandManager.md), [ScaleCheck](ScaleCheck.md), [WaterCheck](WaterCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 41 | `LandConversion.CENTRE_DRY_MIN` | `0.8` | The least share of a converted city's centre that is dry ground: 80% (spec-land 2.9), so a played city is not put on a peninsula two-thirds sea. |
| 44 | `LandConversion.CENTRE_SEA_KM` | `2` | ...with the sea within its half-side and this many kilometres: 2, a city on a coast. |
| 47 | `LandConversion.LAST_FORMAT_BEFORE` | `30` | The last save format whose land is converted rather than read: 30, the format before the land was on the world (GameVersion.SAVE_FORMAT 31). |
| 50 | `LandConversion.SAME_GROUND` | `1e-9` | How near a save's square feet must be to its land's dry ground to be the same ground: a part in a billion of it (spec-land 2.9) - a centre's dry ground is stored in square kilometres, and back in square feet it is the... |
| 53 | `LandConversion.SITE_CELLS` | `64` | How many coastal cells' sites are tried, nearest the world's middle first: 64. |
| 56 | `LandConversion.LEGACY_FIELD_KM` | `1` | A legacy iron field stands at least this far from the site: 1 km (spec-land 2.4). |
| 59 | `LandConversion.LEGACY_DRAWS` | `64` | Draws for the legacy field's dry plot: 64. |
| 62 | `LandConversion.LEGACY_STREAM` | `0x1E6AC7L` | The stream the legacy field's plot is drawn from. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 36 | 124 | **type** `public final class LandConversion` | An older city's land put on the world, and a city's land drawn again to hold a new figure: the site found for it, the centre holding exactly the dry ground it owned, its iron as its save had it, and forty offers liste... |
| 38 | 1 | `private LandConversion()` |  |
| 70 | 20 | `public static long[] site(World world, double dryKm2)` | Where an older city's centre goes: the first site of the founding search, a coastal cell at a time, whose square holding dryKm2 passes the fifth test; or, when none of the nearest SITE_CELLS does, the one whose square... |
| 98 | 14 | `public static void convert(LandManager lm, long seed, double drySqFt, int ironSites, double ironTonnes, int minesCommitted)` | Converts an older city's land (spec-land 2.9): on the world its seed makes, the site site() finds, a centre holding drySqFt of dry ground, its iron the save's - at least the mines standing and on order - and its tonne... |
| 119 | 20 | `static void legacyField(World world, CityLand land, int sites)` | The map's iron for a converted centre the world put none in: one field of the city's sites at a dry plot drawn from the seed, at least LEGACY_FIELD_KM from the site and within the centre when the centre reaches that f... |
| 148 | 11 | `public static void restate(LandManager lm, double drySqFt)` | Draws a city's land again to hold drySqFt of dry ground: the centre round the same site, holding all of it, the purchases folded in and the lanes back at its edge; the city's iron sites and tonnes and what it has take... |

