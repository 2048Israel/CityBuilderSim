# OreView.java - 71 lines · 4 methods · 0 constants · model

`ham/citybuildersim/OreView.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The land office's Ore card as the model reads it (batch I13-L): for each ore
> the city's ground holds, its deposits, the tonnes still in the ground, the
> sites the mines on it take and whether ore lies undug - and the industry
> that digs it, which the card's tooltip and click go to.
> 
> WHY. The card was iron's alone: ui/LandScreen.ore() built its figures from
> iron, its tooltip said "Iron mines are built under Build > Industry", and
> "ore that nothing is digging" was iron's flag (I6's note, carried by I13).
> The materials batches then gave the city coal, copper and stone to dig
> (ExtractionSector, one sector each), so a city could own a coal field no
> mine stood on and the card said nothing - while the sector's own page
> (ExtractionSector.ownLines()) already did. The card's figures are read here,
> from the same getters that page reads (LandManager.getSites(),
> getRemaining(), Game.committedOn()), so the two cannot disagree and the
> screen computes nothing (CLAUDE.md, "A screen section"). LandCheck 30 holds it.
> 
> Oil has its own line on the card (a well is not a mine) and the forest is not
> in fields: neither is here. Iron is always the first row, as it was the
> card's headline even with no deposit; the others only where the city owns a
> site of them.

**Uses:** [Resource](Resource.md) (6), [Game](Game.md) (2), [ExtractionSector](ExtractionSector.md) (2), [LandManager](LandManager.md) (1), [Sector](Sector.md) (1)

**Used by (2):** [LandCheck](LandCheck.md), [LandScreen](LandScreen.md)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 29 | 43 | **type** `public final class OreView` | The land office's Ore card as the model reads it (batch I13-L): for each ore the city's ground holds, its deposits, the tonnes still in the ground, the sites the mines on it take and whether ore lies undug - and the i... |
| 31 | 1 | `private OreView()` |  |
| 44 | 5 | **type** `public record Ore(Resource resource, String words, String sector, int deposits, double tonnes, int mines, b...` | One ore. |
| 47 | 1 | `public boolean hasDigger()` _(in OreView.Ore)_ | Whether an industry digs it at all: the card's click goes to its page. |
| 51 | 12 | `public static List<Ore> ores(Game game)` | The ores the card lists, in Resource's order: iron always, then each of stone, coal, copper and uranium the city owns a site of. |
| 65 | 6 | `public static String diggerOf(Game game, Resource r)` | The key of the extraction sector whose resource this is, or null. |

