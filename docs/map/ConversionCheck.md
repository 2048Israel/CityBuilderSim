# ConversionCheck.java - 718 lines · 20 methods · 2 constants · harnesses

`ham/citybuildersim/ConversionCheck.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> Saved cities put on the block grid (0.7.66, batch M2): the five saves the design was measured on, each converted - a format-31 save's lanes snapped to blocks with no field changing hands and its part fields' sites on the right side, an older save's figure drawn to the plot - with the books the plots drawn and the sites and tonnes the save's to the bit.
> 
> WHY THIS EXISTS (the project's spec-grid.md 2.6 and 3, batch M2). From batch
> M3 every city saved before the grid is put on it once, at load, by
> GridConversion; a conversion that handed the city a field it never bought,
> dropped one it did, or left its books saying other than its map would be a
> player's city quietly changed. So the conversion is held here first, pure,
> on copies of the five saves (the resource conversion-saves.json, copied key
> for key from them): Jerus's live city and his slot 3, written by 0.7.63,
> which held fields site by site; his older city, 0.7.49; and the research
> cities city600 and city2400, 0.7.38.
> 
> What it has to prove:
>   1. LegacyLand reads a save's centre, lanes and purchases as CityLand
>      did until 0.7.66 (its figures frozen in the fixture by that build's
>      lane code, cityLand): the same totals to the bit and the same plots
>      owned, and its count of a block's owned plots the plot-by-plot count;
>   2. a format-31 save that held fields site by site (Jerus's live city),
>      snapped one level finer than its offers: its sites and tonnes the
>      save's to the bit, and the world's fields on the converted ground
>      recount them to the bit; no field held whole changes hands; every site
>      of a field held in part on the right side of the ground; a block
>      decided by the save's half unless a field's point lay in it, and split
>      only where points of both kinds did; what it took out (E) as saved;
>      the books the drawn plots, counted again plot by plot; the ground's
>      rectangles rebuilding it node for node; offers listed round it apart,
>      each against it, whole blocks;
>   3. his slot 3, the same land at month 212, converts to the same ground,
>      books and fields, with its own E;
>   4. a city whose fields go whole (written by 0.7.66), founded on the
>      default world and bought evenly, its bands measured as that land
>      office measured them, then one lane pushed out exactly to a field's
>      centre so the field decides its block - frozen in the fixture
>      (wholeFieldCity) before batch M3 took the lane-selling code out: the
>      same as 2, with no field held in part;
>   5. the three older saves (format 30): rings of blocks of the city's level
>      round J1b's site, the one block split down to the plot, holding the
>      save's dry ground to within a plot and never less; the iron the
>      save's, at least its mines, its tonnes exact, nothing taken out; a
>      legacy iron field on its dry ground a kilometre or more from the site
>      where the world laid no iron on it; every other resource the world's
>      fields centred on it; the books, the rectangles and the offers as in 2;
>   6. the game itself converting a format-31 save at load (0.7.67): Jerus's
>      live land written into a fresh city's save as 0.7.63 wrote it, loaded
>      to exactly the conversion's ground, books, sites and part fields, its
>      figure its dry plots, E and its purchase history as saved, no money
>      moving, the world's totals kept; every field near it held once, whole
>      or by sites, the offers' contents exactly the fields on their free
>      plots; saved again as format 32 and loaded, the same, and both play
>      their next month alike.

**Uses:** [World](World.md) (36), [Resource](Resource.md) (35), [LegacyLand](LegacyLand.md) (30), [CityLand](CityLand.md) (30), [GridConversion](GridConversion.md) (28), [LandGrid](LandGrid.md) (12), [Game](Game.md) (11), [GridOffers](GridOffers.md) (6), [Deposit](Deposit.md) (5), [LandConversion](LandConversion.md) (3), [LandManager](LandManager.md) (3), [LandParcel](LandParcel.md) (3), [GameFiles](GameFiles.md) (2), [LandMarket](LandMarket.md) (2), [Founding](Founding.md) (1), [GridCheck](GridCheck.md) (1), [GameVersion](GameVersion.md) (1)

## Sections

| line | section |
|---:|---|
| 116 | THE FIXTURE |
| 168 | 1. LEGACYLAND IS CITYLAND'S LANES |
| 240 | 2 TO 4. A FORMAT-31 SAVE SNAPPED |
| 442 | 4. A CITY WHOSE FIELDS GO WHOLE |
| 464 | 6. THE GAME CONVERTS A SAVE AT LOAD, ONCE (0.7.67, batch M3) |
| 628 | 5. AN OLDER SAVE: A CENTRE OF BLOCKS TO THE PLOT |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 75 | `ConversionCheck.FIXTURE` | `"conversion-saves.json"` | The fixture: the five saves' land, copied key for key (src/main/resources). |
| 78 | `ConversionCheck.WHOLE_CITY_PURCHASES` | `133` | Purchases the whole-field city of section 4 is bought to: 133, as many as Jerus's live city had made. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 67 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 65 | 654 | **type** `public class ConversionCheck` | Saved cities put on the block grid (0.7.66, batch M2): the five saves the design was measured on, each converted - a format-31 save's lanes snapped to blocks with no field changing hands and its part fields' sites on ... |
| 69 | 4 | `static void check(String label, boolean ok)` |  |
| 81 | 2 | **type** `record Save(String name, int format, String version, int month, long seed, double landOwned, int mines, dou...` | One save's land as the fixture holds it. |
| 84 | 31 | `public static void main(String[] args) throws Exception` |  |

### THE FIXTURE (lines 116-167)

| line | len | member | says |
|---:|---:|---|---|
| 120 | 27 | `static List<Save> load() throws Exception` |  |
| 148 | 7 | `static double[] doubles(com.google.gson.JsonObject o, String key)` |  |
| 156 | 11 | `static double[][] rows(com.google.gson.JsonObject o, String key)` |  |

### 1. LEGACYLAND IS CITYLAND'S LANES (lines 168-239)

| line | len | member | says |
|---:|---:|---|---|
| 172 | 52 | `static void legacyIsCityLand(List<Save> lanes) throws Exception` |  |
| 226 | 3 | `static long hex(com.google.gson.JsonObject o, String key, int i)` | A frozen double's bits, as M3Freeze wrote them (hex). |
| 231 | 6 | `static com.google.gson.JsonObject fixture() throws Exception` | The fixture's whole JSON object. |
| 238 | 1 | `static long bits(double v)` |  |

### 2 TO 4. A FORMAT-31 SAVE SNAPPED (lines 240-441)

| line | len | member | says |
|---:|---:|---|---|
| 245 | 1 | `static String fieldKey(Resource r, int cell, int index)` | A field by its kind, cell and index. |
| 247 | 5 | `static Set<String> partsOf(GridConversion.Result r)` |  |
| 254 | 127 | `static GridConversion.Result snapped(String name, LegacyLand old, String version, double[] depletion, int mines, boolean causes)` | Converts a format-31 save's land and checks it; `causes` asks that the save's fields decided at least one block. |
| 388 | 53 | `static void books(World w, GridConversion.Result r, double all, double dry, double fresh, double sea, double forest, double dra...` | What every conversion must hold, whatever the save: the books the ground's plots counted again plot by plot, its forest's timber its area's; its rectangles rebuilding the ground node for node; and offers listed round ... |

### 4. A CITY WHOSE FIELDS GO WHOLE (lines 442-463)

| line | len | member | says |
|---:|---:|---|---|
| 446 | 17 | `static void wholeFields() throws Exception` |  |

### 6. THE GAME CONVERTS A SAVE AT LOAD, ONCE (0.7.67, batch M3) (lines 464-627)

| line | len | member | says |
|---:|---:|---|---|
| 479 | 5 | `static void quietly(Runnable work)` |  |
| 485 | 69 | `static void atLoad(Save s) throws Exception` |  |
| 555 | 5 | `static Set<String> partsOfLand(CityLand land)` |  |
| 567 | 60 | `static void heldOnce(Game g)` | Every field near a city held at most once (spec-grid 2.6): a whole field by the holding whose ground holds its centre plot or by the offer whose free plots do, never both; a field held in part, each site the same; and... |

### 5. AN OLDER SAVE: A CENTRE OF BLOCKS TO THE PLOT (lines 628-718)

| line | len | member | says |
|---:|---:|---|---|
| 633 | 10 | `static double[] j1bKm2(String name)` | J1b's centre for an older save, as 0.7.66 drew it (frozen in the fixture): all, dry, fresh, sea and forest km2 - what the books are printed against. |
| 645 | 73 | `static boolean centred(Save s)` | Converts a format-30 save and checks it; true when the world laid no iron on its ground, so it stands a legacy field. |

