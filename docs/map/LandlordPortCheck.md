# LandlordPortCheck.java - 822 lines · 33 methods · 2 constants · harnesses

`ham/citybuildersim/LandlordPortCheck.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> The landlord port, held to FR3's brief (docs/queue/FR3.md): the city builds
> and keeps the sea terminals and Freight's Ports division works them.
> 
> WHY THIS HARNESS EXISTS. Until FR3 the four terminals (and the Bulk Berth) were
> the city's end to end: no sector, their dockers paid with the transit bill,
> the handling of a tonne nobody's. FR3 splits the two as a landlord port does:
> the city keeps the berths and their repairs, Freight works them, pays the
> crews, bills the shippers for the handling and pays the city dues. It was
> written in slices by agents from the brief and is held here from the brief's
> rules and the model's own getters and constants: no figure below is typed in
> that a getter or a constant could give. Eleven claims, numbered as FR3.md
> section 6 numbers them (the save is 9 here, so its advice is 10 and its real run, LongPlaytest's, 11):
> 
>   1. THE BULK BERTH. (slice 1's: PortCheck holds it)
>   2. THE SHIPS. (slice 1's: PortCheck holds them)
>   3. THE CREWS. A city with a terminal: the transit bill carries its upkeep
>      and not its crew; Freight's wage bill carries the crew at the same fill;
>      the two together are what portsBill() was, to the cent; the city's posts
>      are counted once, whoever pays them; the drivers' lay-off never touches
>      a terminal's posts; the railway's and Haulage's payroll lines read what
>      they read before the crews came, to the bit; and the crew by kind sums
>      to the payroll moved.
>   4. HANDLING, EACH LEG ONCE. A city landing and shipping dry bulk by sea over a Bulk Terminal: each
>      shipper's "Port handling" line is its sea tonnes of each kind x the quote it was billed at, to the
>      cent; Freight's "Port handling billed" is the shippers' lines summed; a sector that only buys at
>      home and a kind with no berth pay none. (FS7: the fixture is the Haulage chain's 14 months, a Bulk
>      Terminal laid on it and the berths' shares handed to Ports at a set share - handShares - not three
>      more months played for them to be found in force.)
>   5. THE DUES. Freight's "Port dues" are the tonnes x each kind's dues rate x the dial, to the cent, and
>      the treasury's and the national accounts' lines equal them; twins but for the dial: none at 0, twice at 2.
>   6. THE DUES' DEFAULT. duesRateOf() is the annuity worked here by hand (the kind's terminals at cash cost
>      and materials at market, the city's own rate over PORT_LIFE_MONTHS, over the berths' month at
>      DESIGN_USE), and a city owing more pays a dearer due.
>   7. THE PRICE. The first crew quote is the target itself, the next walks REPRICE_SPEED of the way, the dues passed through at the month's rate; the
>      scarcity is 1 to the berths' month and the cap at twice; a berth below DESIGN_USE bills less than
>      its crew and its dues. (FS7: the fixture is the Bulk Terminals laid in the number whose berths' month
>      at DESIGN_USE is more than the whole of the chain's dry bulk across the boundary, the shares handed
>      to Ports - handShares - and the first quote's month played once it has a crew: DESIGN_USE, REPRICE_SPEED
>      the model's.)
>   8. THE MONEY. The money audit closes every month of a city working a port that pays dues.
>   9. THE SAVE. The dial crosses the policy state (and a short array reads the default); the quotes, the
>      month's dues and handling and the treasury's lines cross a save and a reload, to the bit.
>  10. THE ADVICE. (BuildAdviceCheck 13, "a berth pays back", holds it)
>  11. A REAL RUN. (LongPlaytest's)
> 
> It tests the model through its public getters and constants and changes
> nothing in it; where the code and the brief disagree it says FAIL and the
> line says where. Every fixture is a city of its own in GameFiles.scratch().

**Uses:** [Ports](Ports.md) (52), [PortDivision](PortDivision.md) (42), [Game](Game.md) (26), [JobType](JobType.md) (17), [Freight](Freight.md) (15), [GameFiles](GameFiles.md) (14), [HaulageCheck](HaulageCheck.md) (14), [Good](Good.md) (14), [Sector](Sector.md) (11), [TaxPolicy](TaxPolicy.md) (10), [BuildingType](BuildingType.md) (9), [BuildingManager](BuildingManager.md) (6), [BuildingsTemplate](BuildingsTemplate.md) (5), [Traffic](Traffic.md) (5), [Sectors](Sectors.md) (3), [Haulage](Haulage.md) (2), [Founding](Founding.md) (1), [PopulationManager](PopulationManager.md) (1), [LabourCheck](LabourCheck.md) (1), [PortCheck](PortCheck.md) (1)

**Used by (1):** [PolicyPreviewCheck](PolicyPreviewCheck.md)

## Sections

| line | section |
|---:|---|
| 92 | THE FIXTURES |
| 166 | 3. THE CREWS |
| 344 | 4-9. THE PORT DIVISION: THE HANDLING, THE DUES, THE PRICE, THE MONEY, THE SAVE |
| 797 | THE REST OF THE BRIEF, HELD ELSEWHERE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 86 | `LandlordPortCheck.CENT` | `0.01 / 1000` | A cent, in the game's money: D$0.01, and the money is in D$ thousands. |
| 348 | `LandlordPortCheck.K` | `Ports.Cargo.values().length` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 62 | `static int fails` |  |
| 429 | `final Map<Sector, double[]> sea` |  |
| 430 | `final Map<Sector, double[]> landed` |  |
| 431 | `final double[] quote` |  |
| 432 | `final boolean[] known` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 60 | 763 | **type** `public class LandlordPortCheck` | The landlord port, held to FR3's brief (docs/queue/FR3.md): the city builds and keeps the sea terminals and Freight's Ports division works them. |
| 64 | 5 | `static void quietly(Runnable r)` |  |
| 70 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 75 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 81 | 3 | `static boolean same(double a, double b)` | Bitwise, not near. |
| 88 | 3 | `static boolean toTheCent(double a, double b)` |  |

### THE FIXTURES (lines 92-165)

| line | len | member | says |
|---:|---:|---|---|
| 105 | 23 | `static Game city(String label)` | A city with a Freight sector that has a railway and a depot, and houses, shops, roads, power and water around them, not yet played. |
| 130 | 6 | `static void standTerminals(Game g, boolean tanker)` | The terminals a section stands: a General Cargo Terminal, and a Tanker Terminal besides. |
| 138 | 5 | `static double[] fills()` | A fill rate for each tier, none alike and none whole, so a figure struck at the wrong tier's fill misses. |
| 145 | 7 | `static long[] postsOf(BuildingManager bm, String name)` | The posts of the buildings of one template, per tier, from the template: quantity x jobs, nothing from the manager's sums. |
| 153 | 5 | `static long sum(long[] a)` |  |
| 160 | 5 | `static double bill(long[] posts, double[] rates, double[] fill)` | What a month of these posts costs, at these wages and fill rates (full staffing when `fill` is null). |

### 3. THE CREWS (lines 166-343)

| line | len | member | says |
|---:|---:|---|---|
| 170 | 6 | `static void crews()` |  |
| 178 | 135 | `static void crewsOf(boolean tanker)` | One General Cargo Terminal stood in a city that had none (and a Tanker Terminal besides, when `tanker`): what moves from the city's bill to Freight's payroll. |
| 318 | 25 | `static void transitBill()` | A played city with its terminals: the transit bill has the berths' repairs and no crew, and the households are paid what the employers paid, the dockers in Freight's bill and nobody else's. |

### 4-9. THE PORT DIVISION: THE HANDLING, THE DUES, THE PRICE, THE MONEY, THE SAVE (lines 344-796)

| line | len | member | says |
|---:|---:|---|---|
| 356 | 8 | `static Game seaBase(GameFiles files)` | A city with the Haulage fixture's bulk chain (three iron mines and a pelletising plant that lands its coal and ore and ships its pellets, HaulageCheck.base()), played fourteen months, then given a Bulk Terminal and pl... |
| 369 | 13 | `static double[] boundaryTonnes(Game g)` | FS7: the tonnes of each good across the boundary, both ways, as the rows the last month left have them (every sector but the railway: units landed and shipped x the good's tonnes a unit), by Good ordinal - what Freigh... |
| 384 | 5 | `static double kindTonnes(double[] t, Ports.Cargo kind)` | FS7: a kind's tonnes across the boundary in boundaryTonnes(). |
| 396 | 22 | `static void handShares(Game g, double share)` | FS7: the berths' shares in force for the month about to be billed, handed to Ports instead of waited for: the plan Freight.haul() makes at the end of a month (planFirst, planAfter, commit at the railway's quote and ca... |
| 420 | 6 | `static Game seaHanded(GameFiles files, double share)` | FS7: the Haulage chain's 14 months, a Bulk Terminal laid on it and its berths' share in force by hand (handShares): seaBase()'s city without the three months played for them. |
| 428 | 6 | **type** `static final class Plan` | The month about to be billed, worked out from the model's own getters before the haul runs: each shipper's sea tonnes by kind, and the quotes in force. |
| 440 | 25 | `static Plan plan(Game g)` | Last month's rows, at the berths' share in force while they moved (Ports.seaShareOf() at the railway's carried share): units in + units out, x the good's tonnes a unit, x that share, by the good's kind of cargo. |
| 466 | 1 | `static double dial(Game g)` |  |
| 469 | 6 | `static double target(Game g, Ports.Cargo kind)` | The rule's target for a kind from the figures of the month just billed (the division's getters, the berths standing, the tally's boundary tonnes, the dial). |
| 477 | 5 | `static double crewTarget(Game g, Ports.Cargo kind)` | ...and its crew's part alone, the part the quote walks (PortDivision.crewTargetOf()). |
| 487 | 4 | `static double billedAt(Game g, Plan p, Ports.Cargo kind)` | The price a kind was billed at in the month just run: the crew quote in force before it (or the rule's own crew target while none was struck), and the dues at the rate that month paid the city - the dues passed throug... |
| 492 | 5 | `static double sumOf(double[] a)` |  |
| 498 | 1 | `static double lineOf(Sector s, String what)` |  |
| 500 | 99 | `static void handling()` |  |
| 600 | 40 | `static void dues()` |  |
| 641 | 43 | `static void duesDefault()` |  |
| 685 | 51 | `static void price()` |  |
| 737 | 17 | `static void money()` |  |
| 755 | 41 | `static void save() throws Exception` |  |

### THE REST OF THE BRIEF, HELD ELSEWHERE (lines 797-822)

| line | len | member | says |
|---:|---:|---|---|
| 806 | 16 | `public static void main(String[] args) throws Exception` | 1. |

