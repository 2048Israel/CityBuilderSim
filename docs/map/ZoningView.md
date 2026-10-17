# ZoningView.java - 356 lines · 20 methods · 1 constants · model

`ham/citybuildersim/ZoningView.java` - generated 2026-10-17 by CodeMap; line numbers are as of that run.

> Policy › Zoning as figures (0.7.109, batch Z2; the project's
> spec-zoning.md 4, the zoning study's three boards): the hub's four
> figures, the city's dry ground as one bar - built, each zone's used and
> held ground, the bare ground and the open forest free for anyone, and the
> forest the law keeps - the zones a row each, a sector's page before and
> after a staged zone, and the forest law's page before and after a staged
> law.
> 
> WHY. Z1 (0.7.107) built zoning and the forest law as a model with doors
> (Game.setZone(), removeZone(), setForestLaw(), setZoneTax()) and a preview
> (PolicyPreview.zoneAt(), forestAt()), and no screen: nothing a player
> could open showed what a zone holds, what it costs the city or what the
> law keeps. The study drew it as a fifth Policy area (Taxes, Wages, Money,
> Promises, Zoning). These are its figures, held by a harness
> (ZoningViewCheck) rather than worked out in the screen, as OilView's are:
> every one the model's own read - LandManager's ground, its zones' held
> and used ground, PolicyPreview's price given up and tax relief and the
> forest's three kinds - and the bar's parts the same ground cut so they
> add up to the dry ground the city owns. The screen (ui/ZoningScreen)
> only paints them.
> 
> STAGED IS A COPY. A sector's page and the law's page read a detached
> Zoning (with(), without(), withLaw(): Zoning.copy() with the dial moved),
> through the same reads PolicyPreview's zoning section makes, so the
> "after" of a staged zone is what the city reads once it is applied
> (ZoningViewCheck 5 and 6). The zone's tax rows are the sector's own
> offsets (Z5), previewed where they always were (PolicyPreview.taxTake()).
> 
> Pure: it reads the city and changes nothing.

**Uses:** [Zoning](Zoning.md) (22), [PolicyPreview](PolicyPreview.md) (9), [Game](Game.md) (8), [LandManager](LandManager.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (5), [Resource](Resource.md) (2), [EconomyManager](EconomyManager.md) (1), [TaxPolicy](TaxPolicy.md) (1), [BuildingManager](BuildingManager.md) (1), [BuildingsStacks](BuildingsStacks.md) (1)

**Used by (3):** [LandScreen](LandScreen.md), [ZoningScreen](ZoningScreen.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 44 | THE HUB'S FOUR FIGURES (board 1): ZONED, HELD FOR THEM, PRICE GIVEN |
| 89 | THE CITY'S GROUND (board 1): ONE BAR OF THE DRY GROUND |
| 193 | THE ZONES (board 1): A ROW EACH |
| 252 | A SECTOR'S PAGE (board 2), BEFORE AND AFTER |
| 327 | THE FOREST LAW (board 3), BEFORE AND AFTER |

## Enum constants

| line | constant | says |
|---:|---|---|
| 118 | `ZoningView.Kind.BUILT` | Built on, by anyone but a zone's sector in its zone. |
| 120 | `ZoningView.Kind.USED` | Built on by a zone's sector, in its zone. |
| 122 | `ZoningView.Kind.HELD` | Free, held by a zone for its sector. |
| 124 | `ZoningView.Kind.BARE` | Bare ground free for anyone. |
| 126 | `ZoningView.Kind.OPEN` | Open forest standing, free for anyone: a building there clears it (Z7). |
| 128 | `ZoningView.Kind.WORKING` | Working forest: cut, never built on. |
| 130 | `ZoningView.Kind.PROTECTED` | Protected forest: never cut, never built on. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 42 | `ZoningView.SHORT_SHARE` | `1e-6` | A zone holds less than it wants by more than this share of its want: SHORT (Z10's "holds 12.4 of 40 km²"), not a rounding. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 37 | 320 | **type** `public final class ZoningView` | Policy › Zoning as figures (0.7.109, batch Z2; the project's spec-zoning.md 4, the zoning study's three boards): the hub's four figures, the city's dry ground as one bar - built, each zone's used and held ground, the ... |
| 39 | 1 | `private ZoningView()` |  |

### THE HUB'S FOUR FIGURES (board 1): ZONED, HELD FOR THEM, PRICE GIVEN (lines 44-88)

| line | len | member | says |
|---:|---:|---|---|
| 65 | 8 | **type** `public record Figures(double drySqFt, double zonedSqFt, int zones, double heldSqFt, double priceGivenUp, do...` | The four. |
| 69 | 1 | `public double zonedShare()` _(in ZoningView.Figures)_ | The zones' share of the dry ground. |
| 71 | 1 | `public boolean lawSet()` _(in ZoningView.Figures)_ | Whether the law keeps anything: "none" at 0 and 0, the default. |
| 74 | 14 | `public static Figures figures(Game g)` |  |

### THE CITY'S GROUND (board 1): ONE BAR OF THE DRY GROUND (lines 89-192)

| line | len | member | says |
|---:|---:|---|---|
| 116 | 16 | **type** `public enum Kind` | What a stretch of the bar is. |
| 134 | 1 | **type** `public record Part(Kind kind, String sector, double sqFt)` | One stretch: its kind, its zone's sector (USED and HELD; null otherwise), and its ground in square feet. |
| 137 | 34 | `public static List<Part> ground(Game g)` | The whole bar, in its order. |
| 173 | 5 | `public static List<Part> closer(Game g)` | The closer bar under it: the zones' used and held ground and the bare ground free for anyone, so a small zone is seen. |
| 180 | 5 | `public static double total(List<Part> parts, Kind kind)` | The parts of a kind together (every zone's, for USED and HELD). |
| 187 | 5 | `public static double total(List<Part> parts)` | The whole of a bar. |

### THE ZONES (board 1): A ROW EACH (lines 193-251)

| line | len | member | says |
|---:|---:|---|---|
| 216 | 10 | **type** `public record Row(String sector, Zoning.Size kind, double size, double sizeSqFt, double drySqFt, double use...` | One zone as its row shows it. |
| 220 | 1 | `public double share()` _(in ZoningView.Row)_ | Its share of the dry ground, whatever its size is given in. |
| 222 | 1 | `public double wantSqFt()` _(in ZoningView.Row)_ | What it wants of the free ground: its size less what it has used. |
| 224 | 1 | `public boolean isShort()` _(in ZoningView.Row)_ | Whether it holds less than it wants - the free ground is short (Z10) - past a rounding. |
| 228 | 5 | `public static List<Row> zones(Game g)` | Every zone, in Zoning.zones()'s order (Sectors.KEYS). |
| 235 | 16 | `public static Row row(Game g, String sector)` | One sector's row; null with no zone. |

### A SECTOR'S PAGE (board 2), BEFORE AND AFTER (lines 252-326)

| line | len | member | says |
|---:|---:|---|---|
| 257 | 5 | `public static Zoning with(Zoning base, String sector, Zoning.Size kind, double size, double priceShare)` | The city's zoning with one sector's zone set to these, on a copy: what a sector's page stages. |
| 264 | 5 | `public static Zoning without(Zoning base, String sector)` | ...with one sector's zone taken off. |
| 271 | 5 | `public static Zoning withLaw(Zoning base, double protectedShare, double keptShare)` | ...with the forest law at these two shares (the model's clamp: kept at least protected). |
| 283 | 16 | `public static BuildingsTemplate plotOf(Game g, String sector)` | A sector's building a zone's page measures its ground by - what "it carries" counts and what "a plot" is: of its buildings with ground, the one with the most ground standing and on site (the one it builds), else its f... |
| 311 | 2 | **type** `public record Page(PolicyPreview.ZoneAt zone, double freeForAnyone, BuildingsTemplate plot, long carries, d...` | One sector's zone under a zoning, on the land as it stands. |
| 315 | 11 | `public static Page page(Game g, Zoning z, String sector)` | ...struck from PolicyPreview.zoneAt() and LandManager.groundUnder() on `z` - the city's zoning or a staged copy. |

### THE FOREST LAW (board 3), BEFORE AND AFTER (lines 327-356)

| line | len | member | says |
|---:|---:|---|---|
| 343 | 5 | **type** `public record Forest(PolicyPreview.ForestAt at, double forestSqFt, double clearedSqFt, double standingM3, d...` | The forest under a zoning's law. |
| 346 | 1 | `public double openStandingSqFt()` _(in ZoningView.Forest)_ | The open forest still standing: what a building may take, and clear. |
| 349 | 7 | `public static Forest forest(Game g, Zoning z)` |  |

