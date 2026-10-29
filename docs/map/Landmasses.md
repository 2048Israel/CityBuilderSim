# Landmasses.java - 1,238 lines · 99 methods · 11 constants · model

`ham/citybuildersim/Landmasses.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

> The city's ground counted by landmass, and which landmasses the city's buildings may stand on: its books by landmass
> (each landmass's owned dry ground by relief and forest, its shore, the holding it was first owned by), the linked set
> those books are read against, and the unlinked ground a free-ground figure takes off (ET5, 2026-10-25).
> 
> WHY THIS EXISTS. Until ET5 the city's ground was one pool of square feet with no place in it: ground owned across the
> water was room for any building, though nothing could be carried or built there. The rule the design settled (the roads
> study, I2) is that a city builds on its HOME landmass and on the landmasses it has linked, and on another one only a
> port - so an island bought across a strait is owned, counted and priced as ever, but is no room for anyone until a
> port stands there and one at home. The pool stays one pool; this class says how much of it is not room (the unlinked
> landmasses' dry ground), and LandManager's free-ground arithmetic subtracts that behind `anyUnlinked()`, so a city on
> one landmass - every generated world, and every Earth city whose ground lies on its home landmass - reads the old
> expressions to the bit.
> 
> DERIVED, NEVER SAVED. The books by landmass are a pure function of the saved rectangles and the frozen Earth data
> (World.landmassAt()), recounted at every land change and at load (recount()): a reload is exact by construction, and
> a later change to the rule that places a plot cannot part the model from a save. Home's own books are the remainder of
> the land's books once the other landmasses' are taken off. The reader is gated twice, so that a city that owns nothing
> near another landmass reads no plot at all (plotsRead() stays 0): the 1-degree tiles under its ground, and then each
> 32-plot tile's window of cells (World.idsNear()).
> 
> THE LINKED SET (closure()): home, every landmass a join reaches from a linked one (Joins, ET6's bridges), and - once a
> linked landmass has a port standing - every landmass with one.
> 
> THE PORTS, SAVED (the second half of ET5, slice B). A building is a template and a count with no place in it, so where
> the city's sea terminals stand - on which landmass - is kept here, the only thing about the ground that is saved: per
> port template four lists of landmass ids, oldest first (standing, on site, a stopped shell, and the units leaving by a
> demolition on site, by the demolition's id), made only when a terminal is ordered off home or the city owns ground off
> it. Which segment a unit is in is fitted to the buildings' own counts at every month's settle (reconcile()): a
> completion takes the oldest unit on site to standing, a stop the newest to the shell. A port links a landmass once it
> STANDS: a site or a shell never does, and the demolition of the one that links is refused (freeToGo()).

**Uses:** [World](World.md) (61), [LandGrid](LandGrid.md) (15), [CityLand](CityLand.md) (8), [EarthData](EarthData.md) (4), [LandManager](LandManager.md) (1), [EarthTerrain](EarthTerrain.md) (1), [EarthProj](EarthProj.md) (1), [LandParcel](LandParcel.md) (1), [LandMarket](LandMarket.md) (1), [Ports](Ports.md) (1)

**Used by (24):** [AutoBuilder](AutoBuilder.md), [BridgeCheck](BridgeCheck.md), [BridgeGround](BridgeGround.md), [BridgeView](BridgeView.md), [Bridges](Bridges.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildScreen](BuildScreen.md), [CityMap](CityMap.md), [DataSave](DataSave.md), [Game](Game.md), [HydroSites](HydroSites.md), [Inbox](Inbox.md), [LandManager](LandManager.md), [LandMap](LandMap.md), [LandMarket](LandMarket.md), [LandmassCheck](LandmassCheck.md), [LandmassView](LandmassView.md), [MapView](MapView.md), [NewGameCheck](NewGameCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md)

## Sections

| line | section |
|---:|---|
| 72 | THE JOINS AND THE CLOSURE |
| 113 | THE BOOKS BY LANDMASS |
| 218 | RECOUNT - the books, from the grid and the frozen data |
| 511 | OFFERS - the same split of the ground a purchase would add |
| 573 | REFRESH - the linked set and the unlinked sums |
| 603 | THE PORTS - where each terminal stands, and what links the landmasses |
| 1112 | READS - plain getters; none fills anything |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 49 | `Landmasses.HOME_NAME` | `"Home"` | A landmass's name for the landmass the city stands on. |
| 52 | `Landmasses.SQ_FT_PER_PLOT` | `World.KM2_PER_PLOT * LandManager.SQ_FT_PER_KM2` | The plot's area in square feet, as LandManager turns the land's square kilometres into them. |
| 55 | `Landmasses.GATE_MARGIN_PLOTS` | `160` | The plots a margin round the owned ground's box that the 1-degree gate widens it by (it sets the cells' reach, about 124 plots, and some more): 160. |
| 58 | `Landmasses.GATE_STEP_PLOTS` | `256` | The spacing, in plots, at which the 1-degree gate samples the edges of a box for its latitudes and longitudes: 256. |
| 61 | `Landmasses.GATE_MARGIN_DEG` | `0.05` | The degrees the 1-degree gate widens a box's latitudes and longitudes by: 0.05 (twelve cells, past EarthTerrain.LANDMASS_REACH). |
| 64 | `Landmasses.GATE_TILES_MOST` | `100` | The most 1-degree tiles the gate looks through; a box over more is read by the tiles. |
| 67 | `Landmasses.GATE_LAT_MOST` | `85` | The latitude past which the gate gives up (a box near a pole is not a box in latitude and longitude). |
| 70 | `Landmasses.NEAR_KEPT` | `100_000` | The tiles whose window of cells (World.idsNear()) is remembered before the memory is cleared: 100,000 (about 8 MB). |
| 619 | `Landmasses.CHOSEN` | `- 1` | portLandmass()'s `asked`: the card's choice if there is one and it will do, else the rule. |
| 622 | `Landmasses.RULE` | `- 2` | ...the rule alone: the automatic builder and the advice never link an island by choosing it (ET5 D10). |
| 625 | `Landmasses.NO_LANDMASS` | `- 3` | portLandmass()'s answer when no landmass will do: the order is NO_COAST. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 85 | `Joins NONE` | No joins: the closure is home and the landmasses ports link. |
| 119 | `final int id` |  |
| 121 | `final long[] relief` | Its dry plots by relief (World.FLAT to World.VERY_STEEP). |
| 123 | `long forest` | Its forest plots (part of its dry ground). |
| 125 | `long shore` | Its shore: owned flat or foothill dry plots 4-adjacent to owned salt (shorePlots()). |
| 127 | `int firstHolding` | The lowest holding that owns a dry plot of it (0 the centre, k the k-th purchase). |
| 129 | `String dataName` | The data's name for it, "" for none. |
| 151 | `private int home` |  |
| 152 | `private boolean active` |  |
| 154 | `private final TreeMap<Integer, Books> books` | The landmasses off home that the city owns dry ground on, by id; home's are `homeBooks`. |
| 155 | `private Books homeBooks` |  |
| 156 | `private long plotsRead` |  |
| 157 | `private int version` |  |
| 158 | `private SortedSet<Integer> linkedSet` |  |
| 159 | `private final TreeMap<Integer, String> names` |  |
| 162 | `private final java.util.HashMap<Long, int[]> nearKept` | The ids each tile's window of cells holds (World.idsNear()), remembered for the world they were read on: the data is frozen per world, so what a tile can reach never changes. |
| 163 | `private World.Key nearKey` |  |
| 166 | `private final java.util.HashMap<Long, Long> shoreSettled` | The shore of home's tiles that cannot change any more (the tile and its four neighbours wholly owned), by tile key, kept for the land and the world they were counted on: the next recount adds it without reading the tile. |
| 167 | `private CityLand shoreFor` |  |
| 168 | `private World.Key shoreKey` |  |
| 171 | `private Stock stock` | What Landmasses reads of the buildings: Game wires it; a bare one has none and no port is placed. |
| 173 | `private Joins joins` | The joins other than a port, read by every closure (ET6: the standing long bridges, LandManager wires them); none for a bare one. |
| 175 | `private final TreeMap<Integer, Segments> ports` | Where each port template's units stand, by template id: only for a template with a unit off home (or on a city that owns ground off home). |
| 177 | `private final TreeMap<Integer, Leave> leaving` | The units a demolition on site is taking down, by the demolition's id. |
| 179 | `private final TreeMap<Integer, Integer> chosen` | The landmass the card chose for a template's next order (ONE order's: cleared by it and at the month's settle); transient, never saved. |
| 182 | `private double unlinkedDry, unlinkedForest` | The unlinked ground, square feet, worked out by refresh(): what LandManager's free-ground arithmetic takes off. |
| 183 | `private final double[] unlinkedRelief` |  |
| 184 | `private double unlinkedPorts` |  |
| 185 | `private final List<Integer> unlinkedIds` |  |
| 656 | `final List<Integer> standing` |  |
| 661 | `final int demolition, template` |  |
| 662 | `final List<Integer> landmasses` |  |
| 672 | `public int template` |  |
| 673 | `public int[] standing, onSite, shell` |  |
| 679 | `public int demolition` |  |
| 680 | `public int template` |  |
| 681 | `public int[] landmasses` |  |
| 690 | `public List<PortRow> ports` |  |
| 691 | `public List<Leaving> leaving` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 46 | 1193 | **type** `public final class Landmasses` | The city's ground counted by landmass, and which landmasses the city's buildings may stand on: its books by landmass (each landmass's owned dry ground by relief and forest, its shore, the holding it was first owned by... |

### THE JOINS AND THE CLOSURE (lines 72-112)

| line | len | member | says |
|---:|---:|---|---|
| 80 | 7 | **type** `public interface Joins` | Which landmasses a landmass joins, other than by a port: nothing in ET5, and since ET6 the standing long bridges (Bridges), and any ferry when it comes. |
| 82 | 1 | `int[] joinedTo(int id)` _(in Landmasses.Joins)_ | The landmasses this one is joined to (the ids it can reach in one join); empty for none. |
| 94 | 18 | `public static SortedSet<Integer> closure(int home, SortedMap<Integer, Integer> standingPorts, Joins joins)` | The landmasses the city's buildings may stand on, home included, ids ascending (ET5 D10): home; every landmass a join reaches from one already in; and, when some landmass in has a port standing, every landmass with on... |

### THE BOOKS BY LANDMASS (lines 113-217)

| line | len | member | says |
|---:|---:|---|---|
| 118 | 21 | **type** `private static final class Books` | One landmass's owned ground, counted in plots. |
| 131 | 1 | `Books(int id)` _(in Landmasses.Books)_ |  |
| 133 | 5 | `long dry()` _(in Landmasses.Books)_ |  |
| 141 | 9 | **type** `public record Row(int id, String name, boolean home, boolean linked, long dryPlots, long flatPlots, long fo...` | One landmass as a table lists it: what the city owns of it, in plots. |
| 145 | 1 | `public double drySqFt()` _(in Landmasses.Row)_ | Its dry ground in square feet. |
| 148 | 1 | `public double buildableSqFt()` _(in Landmasses.Row)_ | Its flat and foothill ground, the ground a building may stand on, in square feet. |
| 188 | 3 | `public Landmasses()` | A bare one: no landmass, home 0, every plot home's and linked. |
| 193 | 24 | `void reset()` | Back to a bare one (LandManager.reset(): a new city): nothing counted, nothing unlinked. |

### RECOUNT - the books, from the grid and the frozen data (lines 218-510)

| line | len | member | says |
|---:|---:|---|---|
| 229 | 24 | `void recount(World world, CityLand land)` | Counts the city's ground by landmass again (derived, ET5): home is the landmass at the land's site plot; the owned ground is read where it can lie on another landmass, and home's books are what is left of the land's. |
| 255 | 3 | `private static long plots(double km2)` | A land area in square kilometres as a whole number of plots. |
| 260 | 12 | `private void nameThem(World world)` | Names the landmasses off home: the data's name as the founding card writes it ("Charlottetown's"), else "Island N", N counting from 2 in the order the city first owned them (the lowest holding, then the lowest id). |
| 278 | 47 | `boolean onlyHome(World world, long x0, long y0, long x1, long y1)` | Whether no landmass but home's (and none at all, 0) can lie under the box of plots [x0, x1) x [y0, y1) or within the cells' reach of it: the 1-degree tiles under the box, widened, hold no other id. |
| 327 | 4 | `private boolean homeOnly(int[] near)` | Whether every landmass a tile's plots can take is home's (or none): then its plots all read home. |
| 333 | 14 | `private int[] near(World world, long tx, long ty)` | The ids a tile's window of cells holds (World.idsNear()), from the memory of this world's tiles or read once and remembered. |
| 349 | 13 | `private static long[] ownedTiles(LandGrid grid)` | The 32-plot tiles the land owns some of, sorted, each once: as keys (tx << 32 \| ty & 0xffffffff), from the grid's leaves. |
| 363 | 1 | `private static long tileX(long key)` |  |
| 365 | 1 | `private static long tileY(long key)` |  |
| 373 | 59 | `private void readOwned(World world, CityLand land, LandGrid grid)` | Reads the owned ground that can lie off home: each owned tile whose window of cells holds another landmass is read plot by plot (its landmass, class, relief, holding), counting the dry plots of every landmass but home's. |
| 434 | 8 | `private static boolean saltyBeside(long[] tiles, boolean[] salty, long tx, long ty)` | Whether one of the four tiles round (tx, ty) is among the owned tiles and has salt water in it. |
| 444 | 7 | `private void load(World world, LandGrid grid, long tx, long ty, byte[] cover, byte[] rel, int[] ids, int[] held)` | Reads one tile's plots: cover, relief, landmass and holding. |
| 456 | 33 | `private void tally(LandGrid grid, World world, long tx, long ty, byte[] cover, byte[] rel, int[] ids, int[] held, boolean dry)` | Adds a tile's owned ground to the books: the dry plots of each landmass off home (when `dry`: the tile may lie across landmasses) and the shore plots of every landmass. |
| 490 | 3 | `private static boolean ownedSalt(int holding, byte cover)` |  |
| 495 | 10 | `private static boolean saltAcrossEdge(LandGrid grid, World world, long tx, long ty, int[] held)` | Whether any owned plot on the edge of the tile (tx, ty) has owned salt water on the other side of the edge, in the tile beside. |
| 507 | 3 | `private static boolean edgeSalt(LandGrid grid, World world, long x, long y)` | Whether a plot outside the tile being read is owned salt. |

### OFFERS - the same split of the ground a purchase would add (lines 511-572)

| line | len | member | says |
|---:|---:|---|---|
| 523 | 49 | `void offerSplit(World world, CityLand land, LandParcel p)` | Splits an offer's ground by linked and unlinked landmass (derived, ET5): of its unowned plots, the dry ones on a landmass the city has not linked, in square kilometres (LandParcel.getUnlinkedDryKm2()), those of them a... |

### REFRESH - the linked set and the unlinked sums (lines 573-602)

| line | len | member | says |
|---:|---:|---|---|
| 581 | 21 | `void refresh()` | Works out the linked set and the unlinked ground again, eagerly - after a recount, and after anything that moves a port (never in a getter: ReadPathCheck holds that no read fills a cache). |

### THE PORTS - where each terminal stands, and what links the landmasses (lines 603-1111)

| line | len | member | says |
|---:|---:|---|---|
| 628 | 25 | **type** `public interface Stock` | What Landmasses reads of the buildings: Game wires it; a bare one has none, and then no port is placed and every one reads as home. |
| 630 | 1 | `int[] portTemplates()` _(in Landmasses.Stock)_ | The ids of the sea terminals' templates. |
| 633 | 1 | `boolean known(int template)` _(in Landmasses.Stock)_ | Whether the template is one of them (a saved row of any other is passed over). |
| 636 | 1 | `int standing(int template)` _(in Landmasses.Stock)_ | The finished terminals of a template that no demolition has been ordered for. |
| 639 | 1 | `int onSite(int template)` _(in Landmasses.Stock)_ | ...those under construction. |
| 642 | 1 | `int shell(int template)` _(in Landmasses.Stock)_ | ...those a stopped order left standing as a shell. |
| 645 | 1 | `List<int[]> demolitions(int template)` _(in Landmasses.Stock)_ | The demolitions on site of a template, each as {the demolition's id, its buildings}. |
| 648 | 1 | `double landSqFt(int template)` _(in Landmasses.Stock)_ | The land one terminal of the template takes, square feet. |
| 651 | 1 | `int cargo(int template)` _(in Landmasses.Stock)_ | The template's cargo, as Ports.Cargo's ordinal. |
| 655 | 3 | **type** `private static final class Segments` | One template's units by where they stand, each list the landmass ids of its units, oldest first. |
| 660 | 9 | **type** `private static final class Leave` | The units a demolition on site is taking down: the landmass ids, oldest first. |
| 664 | 4 | `Leave(int demolition, int template)` _(in Landmasses.Leave)_ |  |
| 671 | 5 | **type** `public static final class PortRow` | One template's row as saved: the landmass ids of its standing, on-site and shell units, oldest first. |
| 674 | 1 | `public PortRow()` _(in Landmasses.PortRow)_ |  |
| 678 | 6 | **type** `public static final class Leaving` | A demolition on site as saved: the landmass ids of the units it is taking down. |
| 682 | 1 | `public Leaving()` _(in Landmasses.Leaving)_ |  |
| 689 | 5 | **type** `public static final class State` | DataSave's landmasses (ET5): where the terminals stand, for a city with one off its home landmass. |
| 692 | 1 | `public State()` _(in Landmasses.State)_ |  |
| 696 | 1 | `void setStock(Stock stock)` | Wires the buildings' counts (Game, once). |
| 699 | 1 | `void setJoins(Joins joins)` | Wires the joins the closure reads besides the ports (ET6: LandManager's Bridges, once); null, none. |
| 702 | 1 | `public boolean hasPlacements()` | Whether any terminal has a place of its own: a row, or a demolition taking some down. |
| 704 | 3 | `private static void fill(List<Integer> list, int id, int n)` |  |
| 708 | 5 | `private static int count(List<Integer> list, int id)` |  |
| 714 | 4 | `private boolean allHome(List<Integer> list)` |  |
| 720 | 4 | `private void fit(List<Integer> list, int n)` | Fits a list to n units: short, home at the oldest end; long, the newest dropped. |
| 726 | 14 | `void ordered(int template, int landmass, int n, boolean instant)` | An order of n terminals of a template was placed on a landmass, after the stack took it (`instant`: built at once, no construction). |
| 742 | 11 | `void demolishing(int template, int n, int demolition)` | A demolition of n of a template's standing units was ordered (id `demolition`), the units' count already off standing: the n newest whose loss unlinks nothing go to leaving. |
| 755 | 8 | `void shellDemolishing(int template, int demolition)` | A template's shell was ordered demolished (id `demolition`): its units go to leaving. |
| 765 | 6 | `void restarted(int template)` | A template's shell was put back on site: its units go to the oldest end of on site (they were ordered first). |
| 773 | 3 | `void demolished(int demolition)` | A demolition finished: its units no longer hold their ground. |
| 783 | 19 | `void reconcile()` | Fits every template's lists to the buildings' own counts (at the month's settle and at a load), then works the linked set out again: a completion has taken the oldest unit on site to standing, a stop the newest to the... |
| 803 | 16 | `private void fitRow(int template, Segments s)` |  |
| 821 | 6 | `boolean settle()` | The month's settle (Game.settleConstructionControl()): the choices are forgotten (one order's), the lists fitted. |
| 829 | 14 | `SortedMap<Integer, Integer> standingPorts()` | The ports standing on each landmass, by id (a template with no row stands at home): what closure() links by. |
| 845 | 17 | `private int unitsOn(int template, int id)` | A template's units on a landmass, wherever they are: standing, on site, stopped, or being demolished. |
| 864 | 7 | `private List<Integer> standingUnits(int template, boolean raw)` | The standing terminals of a template as landmass ids, oldest first: fitted to the buildings' count unless `raw` (an event's, whose count has already moved). |
| 873 | 15 | `private List<Integer> removable(int template, boolean raw)` | The indices of a template's standing units (into the list standingUnits() gives) that may go, newest first: each is judged with the ones taken before it gone, and stays if its going would take a landmass out of the li... |
| 894 | 5 | `public int freeToGo(int template)` | How many of a template's standing terminals the city may demolish: those whose loss leaves every landmass it links linked (ET5 D11 - unlinking is refused, the builders'-last-depot shape). |
| 901 | 10 | `public String linkedBy(int template)` | The landmasses that would leave the linked set were all of a template's standing terminals gone, named for a sentence ("Island 2"); empty when they link nothing. |
| 913 | 1 | `public void choose(int template, int landmass)` | The card's pick of a landmass for a template's next order (ONE order's); portLandmass() uses it if the landmass will do. |
| 916 | 1 | `public int chosen(int template)` | The landmass the card picked for a template, NO_LANDMASS for none. |
| 919 | 1 | `void clearChoice(int template)` | An order was placed: its pick is spent. |
| 928 | 10 | `public int portLandmass(int template, int asked)` | The landmass an order for a sea terminal stands on (ET5 D10), or NO_LANDMASS when none will do (NO_COAST). |
| 939 | 3 | `private boolean hasShore(int id)` |  |
| 943 | 13 | `private int byRule()` |  |
| 958 | 5 | `public boolean linkedShore()` | Whether some landmass the city has linked has shore it owns - the desalination plant's coast (ET5 D9): home's if it has any, else a linked island's. |
| 971 | 4 | `public static int fieldLandmass(World world, long x, long y)` | The landmass a works field centred on plot (x, y) stands on (ET5 D9): the plot's 15" cell resolved as a dry plot's is (World.landmassOfPlot()), so a field centred on a lake plot of an island is on the island and not o... |
| 977 | 6 | `public List<Row> portChoices()` | The landmasses the card offers for a sea terminal: those the city owns shore on, home first and then as rows() lists them; empty for a city on one landmass. |
| 985 | 11 | `public int[] portsOn(int id)` | The ports standing on a landmass, by Ports.Cargo's ordinal (a template with no row stands at home). |
| 1002 | 7 | `public double padSqFt(int id, double sqFt, double aheadSqFt)` | The foothill an island's terminals are cut into when they stand on more than its flat ground, square feet (the pad a building on foothill is charged, ET2): the rise in max(0, ports - flat) that `sqFt` more would make,... |
| 1015 | 1 | **type** `public record Picture(int home, int[] linked, long[] portsByTypeAndLandmass, long stamp)` | What the map is handed (slice C): home, the linked ids ascending, the standing terminals by template and landmass as triples {template id, landmass id, units} in one array, and a stamp that changes when the linked set... |
| 1018 | 29 | `public Picture picture()` | The picture of the linked set and the terminals on it; null while the city owns nothing off home. |
| 1053 | 22 | `public State toState()` | DataSave's landmasses: where the terminals stand and which demolitions are taking some down; fresh on every call (the autosave's snapshot shares nothing), null when nothing stands off home. |
| 1076 | 5 | `private static int[] toArray(List<Integer> list)` |  |
| 1082 | 3 | `private static void into(List<Integer> list, int[] ids)` |  |
| 1087 | 24 | `void restore(State s)` | Puts the placements back as saved; null, none. |

### READS - plain getters; none fills anything (lines 1112-1238)

| line | len | member | says |
|---:|---:|---|---|
| 1117 | 1 | `public int home()` | The home landmass's id: the landmass at the land's site plot; 0 where there are no landmasses to read. |
| 1120 | 1 | `public boolean single()` | Whether the city owns dry ground on no landmass but home's: then every figure of the free ground is the old one, to the bit. |
| 1123 | 1 | `public boolean anyUnlinked()` | Whether the city owns dry ground on a landmass it has not linked. |
| 1126 | 1 | `public boolean linked(int id)` | Whether a landmass is one the city's buildings may stand on: home, none (0) and the linked set (closure()). |
| 1129 | 8 | `public int[] linkedIds()` | The ids of the landmasses the city's buildings may stand on, ascending (ET6): home and the linked set (closure()). |
| 1139 | 1 | `public List<Integer> unlinkedIds()` | The ids of the landmasses the city owns dry ground on and has not linked, ascending. |
| 1142 | 1 | `public double unlinkedDrySqFt()` | The unlinked landmasses' dry ground, square feet: what the free ground takes off the owned (LandManager). |
| 1145 | 1 | `public double unlinkedReliefSqFt(int relief)` | ...their ground of one relief (CityLand.RELIEF_FLAT to RELIEF_VERY_STEEP), square feet. |
| 1148 | 1 | `public double unlinkedForestSqFt()` | ...their forest, square feet (part of their dry ground). |
| 1151 | 1 | `public double unlinkedPortsSqFt()` | The ground the ports standing on unlinked landmasses stand on, square feet: part of the city's allocated ground, and part of what an unlinked landmass holds (0 while no port stands, is being built or is being demolish... |
| 1154 | 4 | `public double unlinkedFreeSqFt(int id)` | The dry ground of an unlinked landmass nothing stands on, square feet; 0 for a linked one or one the city owns none of. |
| 1160 | 5 | `public double unlinkedFreeSqFt()` | ...all the unlinked landmasses' together. |
| 1167 | 6 | `public double portsGroundSqFt(int id)` | The ground the ports standing, being built, stopped or being demolished on a landmass hold, square feet: 0 where there are none. |
| 1175 | 4 | `public double portRoomSqFt(int id)` | What a port may be built on on a landmass: its flat and foothill ground less the ports' on it, square feet, never below nothing. |
| 1181 | 5 | `public long shorePlots(int id)` | The plots of a landmass the city owns that are shore: flat or foothill dry ground with owned salt water beside it. |
| 1188 | 4 | `public long dryPlots(int id)` | The dry plots of a landmass the city owns (home's: the land's less the others'). |
| 1194 | 5 | `public String name(int id)` | A landmass's name: "Home", the data's name as the founding card writes it ("Charlottetown's"), else "Island N" (N from 2, in the order the city first owned them). |
| 1201 | 5 | `public String unlinkedNames()` | The unlinked landmasses' names joined for a sentence: "Island 2", "Island 2 and Island 3", "Island 2, Island 3 and Island 4"; empty for none. |
| 1208 | 5 | `private static String joined(List<String> list)` | Names joined for a sentence: "Island 2", "Island 2 and Island 3", "Island 2, Island 3 and Island 4"; empty for none. |
| 1215 | 8 | `public List<Row> rows()` | The landmasses the city owns, home first and then in the order it first owned them. |
| 1224 | 5 | `private Row row(Books b, boolean isHome)` |  |
| 1231 | 1 | `public int version()` | How many times the linked set has moved: what a cache of anything that depends on it keys on. |
| 1234 | 1 | `public long plotsRead()` | How many plots recount() and offerSplit() have read since this was made (cumulative): 0 for a city that owns nothing near another landmass, and for every generated world. |
| 1237 | 1 | `public boolean active()` | Whether the last recount read a world with landmasses: false on a generated world and an earth-1 file. |

