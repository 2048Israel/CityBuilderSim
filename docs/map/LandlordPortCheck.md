# LandlordPortCheck.java - 739 lines · 29 methods · 2 constants · harnesses

`ham/citybuildersim/LandlordPortCheck.java` - generated 2026-10-21 by CodeMap; line numbers are as of that run.

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
>      home and a kind with no berth pay none.
>   5. THE DUES. Freight's "Port dues" are the tonnes x each kind's dues rate x the dial, to the cent, and
>      the treasury's and the national accounts' lines equal them; twins but for the dial: none at 0, twice at 2.
>   6. THE DUES' DEFAULT. duesRateOf() is the annuity worked here by hand (the kind's terminals at cash cost
>      and materials at market, the city's own rate over PORT_LIFE_MONTHS, over the berths' month at
>      DESIGN_USE), and a city owing more pays a dearer due.
>   7. THE PRICE. The first crew quote is the target itself, the next walks REPRICE_SPEED of the way, the dues passed through at the month's rate; the
>      scarcity is 1 to the berths' month and the cap at twice; a berth below DESIGN_USE bills less than
>      its crew and its dues.
>   8. THE MONEY. The money audit closes every month of a city working a port that pays dues.
>   9. THE SAVE. The dial crosses the policy state (and a short array reads the default); the quotes, the
>      month's dues and handling and the treasury's lines cross a save and a reload, to the bit.
>  10. THE ADVICE. (BuildAdviceCheck 13, "a berth pays back", holds it)
>  11. A REAL RUN. (LongPlaytest's)
> 
> It tests the model through its public getters and constants and changes
> nothing in it; where the code and the brief disagree it says FAIL and the
> line says where. Every fixture is a city of its own in GameFiles.scratch().

**Uses:** [Ports](Ports.md) (46), [PortDivision](PortDivision.md) (40), [Game](Game.md) (22), [JobType](JobType.md) (17), [GameFiles](GameFiles.md) (13), [Freight](Freight.md) (13), [HaulageCheck](HaulageCheck.md) (12), [Sector](Sector.md) (10), [TaxPolicy](TaxPolicy.md) (10), [BuildingType](BuildingType.md) (9), [Good](Good.md) (7), [BuildingManager](BuildingManager.md) (6), [BuildingsTemplate](BuildingsTemplate.md) (5), [Sectors](Sectors.md) (3), [Haulage](Haulage.md) (2), [Traffic](Traffic.md) (2), [Founding](Founding.md) (1), [PopulationManager](PopulationManager.md) (1), [LabourCheck](LabourCheck.md) (1), [PortCheck](PortCheck.md) (1)

## Sections

| line | section |
|---:|---|
| 87 | THE FIXTURES |
| 161 | 3. THE CREWS |
| 339 | 4-9. THE PORT DIVISION: THE HANDLING, THE DUES, THE PRICE, THE MONEY, THE SAVE |
| 714 | THE REST OF THE BRIEF, HELD ELSEWHERE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 81 | `LandlordPortCheck.CENT` | `0.01 / 1000` | A cent, in the game's money: D$0.01, and the money is in D$ thousands. |
| 343 | `LandlordPortCheck.K` | `Ports.Cargo.values().length` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 57 | `static int fails` |  |
| 362 | `final Map<Sector, double[]> sea` |  |
| 363 | `final Map<Sector, double[]> landed` |  |
| 364 | `final double[] quote` |  |
| 365 | `final boolean[] known` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 55 | 685 | **type** `public class LandlordPortCheck` | The landlord port, held to FR3's brief (docs/queue/FR3.md): the city builds and keeps the sea terminals and Freight's Ports division works them. |
| 59 | 5 | `static void quietly(Runnable r)` |  |
| 65 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 70 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 76 | 3 | `static boolean same(double a, double b)` | Bitwise, not near. |
| 83 | 3 | `static boolean toTheCent(double a, double b)` |  |

### THE FIXTURES (lines 87-160)

| line | len | member | says |
|---:|---:|---|---|
| 100 | 23 | `static Game city(String label)` | A city with a Freight sector that has a railway and a depot, and houses, shops, roads, power and water around them, not yet played. |
| 125 | 6 | `static void standTerminals(Game g, boolean tanker)` | The terminals a section stands: a General Cargo Terminal, and a Tanker Terminal besides. |
| 133 | 5 | `static double[] fills()` | A fill rate for each tier, none alike and none whole, so a figure struck at the wrong tier's fill misses. |
| 140 | 7 | `static long[] postsOf(BuildingManager bm, String name)` | The posts of the buildings of one template, per tier, from the template: quantity x jobs, nothing from the manager's sums. |
| 148 | 5 | `static long sum(long[] a)` |  |
| 155 | 5 | `static double bill(long[] posts, double[] rates, double[] fill)` | What a month of these posts costs, at these wages and fill rates (full staffing when `fill` is null). |

### 3. THE CREWS (lines 161-338)

| line | len | member | says |
|---:|---:|---|---|
| 165 | 6 | `static void crews()` |  |
| 173 | 135 | `static void crewsOf(boolean tanker)` | One General Cargo Terminal stood in a city that had none (and a Tanker Terminal besides, when `tanker`): what moves from the city's bill to Freight's payroll. |
| 313 | 25 | `static void transitBill()` | A played city with its terminals: the transit bill has the berths' repairs and no crew, and the households are paid what the employers paid, the dockers in Freight's bill and nobody else's. |

### 4-9. THE PORT DIVISION: THE HANDLING, THE DUES, THE PRICE, THE MONEY, THE SAVE (lines 339-713)

| line | len | member | says |
|---:|---:|---|---|
| 351 | 8 | `static Game seaBase(GameFiles files)` | A city with the Haulage fixture's bulk chain (three iron mines and a pelletising plant that lands its coal and ore and ships its pellets, HaulageCheck.base()), played fourteen months, then given a Bulk Terminal and pl... |
| 361 | 6 | **type** `static final class Plan` | The month about to be billed, worked out from the model's own getters before the haul runs: each shipper's sea tonnes by kind, and the quotes in force. |
| 373 | 25 | `static Plan plan(Game g)` | Last month's rows, at the berths' share in force while they moved (Ports.seaShareOf() at the railway's carried share): units in + units out, x the good's tonnes a unit, x that share, by the good's kind of cargo. |
| 399 | 1 | `static double dial(Game g)` |  |
| 402 | 6 | `static double target(Game g, Ports.Cargo kind)` | The rule's target for a kind from the figures of the month just billed (the division's getters, the berths standing, the tally's boundary tonnes, the dial). |
| 410 | 5 | `static double crewTarget(Game g, Ports.Cargo kind)` | ...and its crew's part alone, the part the quote walks (PortDivision.crewTargetOf()). |
| 420 | 4 | `static double billedAt(Game g, Plan p, Ports.Cargo kind)` | The price a kind was billed at in the month just run: the crew quote in force before it (or the rule's own crew target while none was struck), and the dues at the rate that month paid the city - the dues passed throug... |
| 425 | 5 | `static double sumOf(double[] a)` |  |
| 431 | 1 | `static double lineOf(Sector s, String what)` |  |
| 433 | 94 | `static void handling()` |  |
| 528 | 40 | `static void dues()` |  |
| 569 | 43 | `static void duesDefault()` |  |
| 613 | 40 | `static void price()` |  |
| 654 | 17 | `static void money()` |  |
| 672 | 41 | `static void save() throws Exception` |  |

### THE REST OF THE BRIEF, HELD ELSEWHERE (lines 714-739)

| line | len | member | says |
|---:|---:|---|---|
| 723 | 16 | `public static void main(String[] args) throws Exception` | 1. |

