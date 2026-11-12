# IslandLinkCheck.java - 765 lines · 25 methods · 8 constants · harnesses

`ham/citybuildersim/IslandLinkCheck.java` - generated 2026-11-12 by CodeMap; line numbers are as of that run.

> The island link checked (AL1, 2026-11-09; docs/queue/AL1.md): the rule that says what the city should order to link an
> island it owns ground on, the automatic builder that orders it once its orders lack ground, and the test player that
> orders it by the same rule. Not part of the game.
> 
> WHY. Jerus, 2026-10-31: "autobuild will build bridges and ports if sees that land is low and there is something unlinked
> (basically, if link is allowed with bridge, then it builds a highway bridge or road bridge ... if not it build a link via
> port)". Until AL1 the automatic builders never linked an island (ET5's call 5, ET6-C): ground the city owned across the
> water stayed unlinked, owned and counted, however full home was. Every part of the rule is caused here on the Earth
> fixture of LandmassCheck and BridgeCheck (Rotterdam, and Goeree-Overflakkee's square bought across the water): the
> crossing is handed to the survey (BridgeCheck.seam(), because the strait the fixture holds is open water past 45 km and no
> crossing), the ground low is cut to the condition, and each figure is asserted against the model's own constants.
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. THE RULE, A CROSSING ALLOWED. With a shallow channel the survey can cross, BuildAdvice.linkFor() is a BRIDGE toward the
>      island, one order: the Road Bridge with no highway standing and the Highway Bridge with one; its price is the quote of
>      that template with the island picked, its ground the island's buildable ground and its price a square foot that over
>      it; the card's pick of the far landmass is as the rule found it, both with none and with another set; a crossing is
>      not remembered (each ask is a survey).
>   2. THE RULE, DEEP SEA. Where the survey finds deep water, a PORT: the island's order is the cheapest known terminal that
>      stands on the island and fits its room (against every port template's quote), and with no terminal on linked ground a
>      second at home; with one standing at home the island's alone (a port shared where one serves); with one on the island
>      and none at home, the home order alone.
>   3. NO LINK, OR NOTHING TO LINK. An island owned with no shore and no crossing is NONE, in words naming both; so is one
>      with shore where home owns none; a city whose ground is all at home and a generated world are null and read nothing;
>      a survey that found no crossing stands NO_CROSSING_MONTHS and is made again after, a crossing is not remembered.
>   4. THE AUTO-BUILDER, A CROSSING. With the city's linked free ground cut below what its first order needs, the pass sets
>      groundShort(), orders one bridge for the island (a LinkStep, a bridge on site, a log entry, the spending counted), and
>      the island is unlinked while it is on site; the next passes, the ground still low, order no second; a city with room
>      sets groundShort() in none of its months and orders no link.
>   5. THE AUTO-BUILDER, DEEP SEA. The same, ports: a terminal on site on the island and one at home; once both stand the
>      island is linked and the next pass orders nothing.
>   6. THE MONEY. With the debt limit at 0 and the cash at the reserve, the link is HELD, in the words of the limit: nothing
>      ordered, no ground bought, no bond sold.
>   7. THE BRIDGE STANDS AND LINKS. The bridge the auto-builder ordered, run to standing: the island is linked, its free
>      ground counts as room, and the next pass with ground low orders nothing more for it.
>   8. THE TEST PLAYER LINKS AS THE AUTO-BUILDER DOES. LongPlaytest.keepGroundAhead() with the used share over its line orders
>      the same templates toward the same landmass the rule gives, on the crossing and on deep sea, and counts them; where the
>      rule says NONE it counts the link held.
>   9. COUNTED IN A PLAYED RUN. Rotterdam with the island square, its real strait, 240 months with the auto-builder on: how
>      many months were short of ground, how many links of each kind, the month the island linked; at most one link order
>      for an island while one is under way, every order for an island unlinked when it was ordered. A generated world with
>      its ground cut low every month meets the cue and orders no link, no bridge and no port.
> 
> Every premise is asserted before what rests on it.

**Uses:** [BuildAdvice](BuildAdvice.md) (79), [LandmassCheck](LandmassCheck.md) (67), [Landmasses](Landmasses.md) (34), [BridgeCheck](BridgeCheck.md) (34), [LongPlaytest](LongPlaytest.md) (29), [AutoBuilder](AutoBuilder.md) (28), [Game](Game.md) (27), [Bridges](Bridges.md) (14), [World](World.md) (14), [BuildingsTemplate](BuildingsTemplate.md) (13), [BridgeSurvey](BridgeSurvey.md) (4), [Founding](Founding.md) (3), [EarthCheck](EarthCheck.md) (3), [BridgeView](BridgeView.md) (3), [Timing](Timing.md) (2), [Ports](Ports.md) (2), [EarthData](EarthData.md) (2), [BuildingType](BuildingType.md) (2), [GameFiles](GameFiles.md) (2), [ChildcareCheck](ChildcareCheck.md) (2), [EarthTerrain](EarthTerrain.md) (1), [TempDirs](TempDirs.md) (1), [AutoBuildCheck](AutoBuildCheck.md) (1)

## Sections

| line | section |
|---:|---|
| 214 | MAIN |
| 251 | 1. THE RULE, A CROSSING ALLOWED |
| 303 | 2. THE RULE, DEEP SEA |
| 369 | 3. NO LINK, OR NOTHING TO LINK |
| 433 | 4. THE AUTO-BUILDER, A CROSSING |
| 513 | 5. THE AUTO-BUILDER, DEEP SEA |
| 560 | 6. THE MONEY |
| 608 | 7. THE BRIDGE STANDS AND LINKS |
| 645 | 8. THE TEST PLAYER LINKS AS THE AUTO-BUILDER DOES |
| 694 | 9. COUNTED IN A PLAYED RUN |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 60 | `IslandLinkCheck.out` | `System.out` |  |
| 63 | `IslandLinkCheck.SEED` | `Founding.DEFAULT_WORLD_SEED` | The seed every world here is drawn with: the default world's. |
| 66 | `IslandLinkCheck.ROOM_MONTHS` | `12` | Months a city with room is played in section 4: 12. |
| 69 | `IslandLinkCheck.BRIDGE_MONTHS` | `800` | Most months a bridge is given to stand on two builders' depots: 800 (BridgeCheck allows 600 for the same fixture). |
| 72 | `IslandLinkCheck.RUN_MONTHS` | `240` | Months the played Earth run and the generated world run in section 9: 240. |
| 75 | `IslandLinkCheck.LINKS_LABEL` | `"Island links"` | The label the auto-builder enters its island links under in its log (AutoBuilder's, private). |
| 105 | `IslandLinkCheck.CUE_PASSES` | `1` | Passes whose orders must meet the ground short, in a stretch of months with the ground cut low before each, for "the next passes order no second link" to mean something: 1 (the village orders only when it needs someth... |
| 108 | `IslandLinkCheck.CUE_MONTHS` | `120` | Most months such a stretch is given to meet CUE_PASSES: 120. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 59 | `static int fails` |  |
| 438 | `static LandmassCheck.City bridged` | The city section 4 played and section 7 stands the bridge of. |
| 439 | `static int bridgedIsland` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 57 | 709 | **type** `public class IslandLinkCheck` | The island link checked (AL1, 2026-11-09; docs/queue/AL1.md): the rule that says what the city should order to link an island it owns ground on, the automatic builder that orders it once its orders lack ground, and th... |
| 77 | 4 | `static void check(String label, boolean ok)` |  |
| 82 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 88 | 1 | `static boolean near(double a, double b)` | Two figures are one within a part in a billion. |
| 90 | 1 | `static void quietly(Runnable r)` |  |
| 93 | 1 | `static void month(LandmassCheck.City c)` | One month played quietly, the auto-builder's pass first in it. |
| 100 | 3 | `static void cutGround(LandmassCheck.City c)` | The condition "the linked free ground is low": the ground the buildings stand on set to what the city owns on linked landmasses (and the island terminals' ground, which is the island's room, not the linked pool's), th... |
| 114 | 12 | `static boolean linkOrdersNothing(LandmassCheck.City c)` | The pass's last line called by hand with the ground cut low: link(), asked of the city as it stands (a pass sets the cue only when it places an order, which the village does in a few months of a hundred) - whether it ... |
| 131 | 13 | `static int[] lowPasses(LandmassCheck.City c, int passes, java.util.function.BooleanSupplier holds)` | Months played, the ground cut low before each, until `passes` of them met an order short of ground (groundShort()), at most CUE_MONTHS: the months played, the passes that met it, the link steps seen in them and the mo... |
| 145 | 3 | `static void autoBuilderOn(LandmassCheck.City c)` |  |
| 150 | 4 | `static void putBack(Game g, BuildingsTemplate t, int pick)` | The card's pick of a bridge's far landmass put back as it was. |
| 155 | 4 | `static Landmasses.Row rowOf(Landmasses lms, int id)` |  |
| 161 | 9 | `static Game.BuildQuote bridgeQuote(Game g, BuildingsTemplate t, int island)` | The quote of one bridge with the island picked, the pick put back. |
| 172 | 1 | **type** `record Terminals(int known, int fitting, boolean cheaper, double cheapest)` | What the sea terminals the city knows come to for a landmass asked: how many are port templates the city knows, how many would stand there, and whether one of those is cheaper than a price. |
| 178 | 18 | `static Terminals terminals(Game g, int asked, boolean onIsland, double price)` | Every known port template, quoted for `asked`: those that stand where it is asked (an island's own shore, fitting its room; the rule's landmass for RULE) and the cheapest of them, and whether any is cheaper than `pric... |
| 198 | 7 | `static boolean terminalOrder(Game g, BuildAdvice.LinkOrder o, int asked)` | Whether the order's template is a known sea terminal that would stand where `asked` says, and the order's price is the quote of one. |
| 207 | 6 | `static String words(BuildAdvice.Link l)` | The orders of a link by template name, "Bulk Berth on Island 2, Bulk Berth on linked ground". |

### MAIN (lines 214-250)

| line | len | member | says |
|---:|---:|---|---|
| 218 | 32 | `public static void main(String[] args) throws Exception` |  |

### 1. THE RULE, A CROSSING ALLOWED (lines 251-302)

| line | len | member | says |
|---:|---:|---|---|
| 255 | 47 | `static void ruleWithACrossing(BridgeCheck.Island isle)` |  |

### 2. THE RULE, DEEP SEA (lines 303-368)

| line | len | member | says |
|---:|---:|---|---|
| 307 | 61 | `static void ruleOnDeepSea(LandmassCheck.Fixtures f, BridgeCheck.Island isle)` |  |

### 3. NO LINK, OR NOTHING TO LINK (lines 369-432)

| line | len | member | says |
|---:|---:|---|---|
| 373 | 59 | `static void noLink(LandmassCheck.Fixtures f, BridgeCheck.Island isle, BridgeCheck.Island dry, Path root)` |  |

### 4. THE AUTO-BUILDER, A CROSSING (lines 433-512)

| line | len | member | says |
|---:|---:|---|---|
| 441 | 71 | `static void autoBuilderOnACrossing(LandmassCheck.Fixtures f, BridgeCheck.Island isle)` |  |

### 5. THE AUTO-BUILDER, DEEP SEA (lines 513-559)

| line | len | member | says |
|---:|---:|---|---|
| 517 | 42 | `static void autoBuilderOnDeepSea(LandmassCheck.Fixtures f, BridgeCheck.Island isle)` |  |

### 6. THE MONEY (lines 560-607)

| line | len | member | says |
|---:|---:|---|---|
| 564 | 43 | `static void theMoney(BridgeCheck.Island isle)` |  |

### 7. THE BRIDGE STANDS AND LINKS (lines 608-644)

| line | len | member | says |
|---:|---:|---|---|
| 612 | 32 | `static void theBridgeStands(BridgeCheck.Island isle)` |  |

### 8. THE TEST PLAYER LINKS AS THE AUTO-BUILDER DOES (lines 645-693)

| line | len | member | says |
|---:|---:|---|---|
| 649 | 44 | `static void testPlayer(LandmassCheck.Fixtures f, BridgeCheck.Island isle, BridgeCheck.Island dry)` |  |

### 9. COUNTED IN A PLAYED RUN (lines 694-765)

| line | len | member | says |
|---:|---:|---|---|
| 698 | 67 | `static void playedRun(LandmassCheck.Fixtures f, Path root)` |  |

