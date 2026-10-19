# OldMetal.java - 418 lines · 40 methods · 10 constants · model

`ham/citybuildersim/OldMetal.java` - generated 2026-10-19 by CodeMap; line numbers are as of that run.

> The city's old steel: what it throws out each month that a Scrap Yard can
> buy and sort into scrap - its scrapped cars, its demolitions, its
> fabricators' offcuts and its railway's worn-out wagon sets (0.7.123, batch
> I4; docs/studies/spec-materials.md 2.9, materials-chains-research.md 2.7) -
> and since 0.7.133 (batch I8) its old copper, from its cars and demolitions,
> which a Scrap Copper Refinery buys (OLD COPPER, below).
> 
> WHY. Until 0.7.123 the scrap the arc furnaces melt (Good.SCRAP, since
> 0.7.120) had no source in the city: every tonne was imported, because
> nothing here made any. A real city is one of the world's scrap mines - the
> cars its households scrap, the buildings it pulls down, the offcuts of its
> fabrication shops - and a mill on the city's own scrap is the one an arc
> furnace pays on (the study's 2.5: the Electric Arc Furnace 0.7% of its cost
> a month replacing imported steel on the city's scrap, 0.2% on imported).
> Until then the cars a household scrapped were dropped
> (HouseholdBalance.wearOutCars()'s return, which Motoring threw away), and a
> demolished building's steel went with its debris.
> 
> NOT A GOOD (the study's 2.3): old steel has no market of its own and no
> world price. It is a pool that arises each month, at RECOVERY of the steel
> in what the city throws out, kept by who threw it out (the seller):
>   - a car scrapped, STEEL_A_CAR, the households' (Motoring);
>   - a building demolished, STEEL_A_MATERIALS_UNIT a unit of the material
>     it was built with, its owner's - a sector's own when it retires its
>     plant (Game.retire()), and the builders' when the city pulls one of
>     its own down (★ Game.closeDemolished(): the demolition is the
>     builders' contract and what they recover from it is theirs, as the
>     material's salvage share is - ConstructionControl.SALVAGE_SHARE);
>   - a tonne of fabricated steel made, OFFCUTS_A_TONNE_FABRICATED, its
>     maker's (Manufacturing);
>   - a wagon set worn out, STEEL_A_SET, the railway's (sectors.Freight).
> 
> THE YARDS BUY IT (settle()), once a month, when Iron & Steel makes its
> scrap (sectors.HeavyIndustry, THE OLD STEEL): as much as its Scrap Yards
> take at the rate they run, from every seller pro rata to what each threw
> out, at PAY_SHARE of scrap's export price (Jerus's Q8, 2026-10-08: "pay
> sellers 70%"). What they do not take goes to landfill, and the pool starts
> again: old steel is not kept. With no yard standing all of it goes to
> landfill, as it always did.
> 
> WHO IS PAID, AND WHEN (★, Iron & Steel's books): the yards' purchase is
> on their input line, named LINE, from each seller (Sector.billForService(),
> the households by Trade.HOUSEHOLDS, which no sales tax credits), and each
> selling sector's sale is its other revenue, named SOLD
> (Sector.sellOldMetal()) - struck and banked at the next strike, the buyer's
> and the seller's cash in the same month's window. The households are paid
> as the yards buy, into their savings pro rata to the cars each scrapped
> (HouseholdBalance.creditScrappedCars()); the yards' cash falls at their
> strike, and the money audit's "- sectors OldMetalToHouseholds" reads it off
> their statements there.
> 
> Saved whole (DataSave.oldMetal, state() and restore()): the steel arisen
> since the yards last bought, by seller, and the last month's record - and
> the copper's beside it since 0.7.133. A save from before 0.7.123 carries
> none, which is right: that city had thrown nothing out that a yard could
> have bought; one from before 0.7.133 no copper, which had all gone to
> landfill.

**Uses:** [Trade](Trade.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (1)

**Used by (17):** [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [CopperRefining](CopperRefining.md), [CopperRefiningCheck](CopperRefiningCheck.md), [DataSave](DataSave.md), [Game](Game.md), [HeavyIndustry](HeavyIndustry.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [MaterialsCheck](MaterialsCheck.md), [MoneyAudit](MoneyAudit.md), [MoneyCheck](MoneyCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [SpreadPlanner](SpreadPlanner.md)

## Sections

| line | section |
|---:|---|
| 106 | OLD COPPER (0.7.133, batch I8; docs/studies/spec-materials.md 2.9, the |
| 138 | the steel arises |
| 177 | the yards buy |
| 215 | a pool |
| 231 | · the last month the buyers bought (settle()): the record, for the planner and the screens |
| 363 | across a save |

## Enum constants

| line | constant | says |
|---:|---|---|
| 97 | `OldMetal.Source.CARS` | Cars the households scrapped. |
| 99 | `OldMetal.Source.DEMOLITIONS` | Buildings pulled down. |
| 101 | `OldMetal.Source.OFFCUTS` | The fabricators' offcuts. |
| 103 | `OldMetal.Source.ROLLING_STOCK` | The railway's wagon sets worn out. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 71 | `OldMetal.RECOVERY` | `.85` | The share of the steel in what the city throws out that comes back as old steel: 85% for construction and cars (the study's 2.7, [I52]). |
| 74 | `OldMetal.STEEL_A_CAR` | `.9` | Tonnes of steel in a car scrapped (the study's 2.7, [I63]). |
| 77 | `OldMetal.STEEL_A_MATERIALS_UNIT` | `.3` | Tonnes of steel a unit of a demolished building's construction material holds: ★est., the study's 9.3 - about 3 t in a House's ten units. |
| 80 | `OldMetal.OFFCUTS_A_TONNE_FABRICATED` | `.05` | Tonnes of offcuts a tonne of fabricated steel made leaves: the Fabrication Shop's 1,260 t of steel into 1,200 (the study's 2.7). |
| 83 | `OldMetal.STEEL_A_SET` | `300` | Tonnes of steel in a wagon set worn out: a set's weight, a locomotive and what it pulls (Good.ROLLING_STOCK's 300 t). |
| 86 | `OldMetal.PAY_SHARE` | `.70` | What a yard pays the sellers for a tonne of old steel, a share of scrap's export price: 70% (Jerus's Q8, 2026-10-08). |
| 89 | `OldMetal.LINE` | `"Old metal"` | The yards' purchase, named on their input line. |
| 92 | `OldMetal.SOLD` | `"Old metal sold"` | ...and the sellers' sale, named in their other revenue. |
| 121 | `OldMetal.COPPER_A_CAR` | `.023` | Tonnes of copper in a car scrapped: 23 kg, a petrol car's (the study's 4.1, [K45]). |
| 124 | `OldMetal.COPPER_A_MATERIALS_UNIT` | `.1` | Tonnes of copper a unit of a demolished building's construction material holds: ★est., spec-materials 2.9 (0.1 t a unit). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 127 | `private final Pool steel` | The steel: what the Scrap Yards buy (since 0.7.123). |
| 130 | `private final Pool copper` | ...and the copper: what the Scrap Copper Refineries buy (0.7.133). |
| 226 | `private final Map<String, Double> pending` | Metal arisen since the buyers last bought, tonnes recovered, by seller (a sector's key, or Trade.HOUSEHOLDS), in the order each first arose. |
| 229 | `private final double[] pendingBySource` | ...and by source, in Source's order. |
| 233 | `private final double[] lastBySource` |  |
| 234 | `private double lastArisen, lastBought, lastLandfill, lastPrice` |  |
| 235 | `private final Map<String, Double> lastPaid` |  |
| 374 | `public Map<String, Double> pending` |  |
| 375 | `public double[] pendingBySource` |  |
| 376 | `public double[] lastBySource` |  |
| 377 | `public double lastArisen, lastBought, lastLandfill, lastPrice` |  |
| 378 | `public Map<String, Double> lastPaid` |  |
| 380 | `public State copper` | The old copper's own (0.7.133), or null. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 68 | 351 | **type** `public final class OldMetal` | The city's old steel: what it throws out each month that a Scrap Yard can buy and sort into scrap - its scrapped cars, its demolitions, its fabricators' offcuts and its railway's worn-out wagon sets (0.7.123, batch I4... |
| 95 | 10 | **type** `public enum Source` | Where old steel comes from. |

### OLD COPPER (0.7.133, batch I8; docs/studies/spec-materials.md 2.9, the (lines 106-137)

| line | len | member | says |
|---:|---:|---|---|
| 133 | 1 | `public Pool steel()` | The city's old steel, as a pool: what every method of this class not named for copper reads and moves. |
| 136 | 1 | `public Pool copper()` | ...and its old copper (0.7.133): cars and demolitions, bought by Copper Refining's scrap refineries. |

### the steel arises (lines 138-176)

| line | len | member | says |
|---:|---:|---|---|
| 141 | 3 | `public void arise(Source source, String seller, double tonnes)` | `tonnes` of steel thrown out by `seller` from `source`, of which RECOVERY comes back as old steel; nothing for none, or for nobody. |
| 146 | 4 | `public void carsScrapped(double cars)` | Cars the households scrapped (Motoring, at the top of the month): their steel, and since 0.7.133 their copper. |
| 152 | 5 | `public void demolished(BuildingsTemplate t, int n, String owner)` | `n` of a building pulled down, its steel - and since 0.7.133 its copper - its owner's (`owner`, a sector's key). |
| 159 | 3 | `public void fabricated(String maker, double tonnes)` | `tonnes` of fabricated steel made by `maker` this month: its offcuts. |
| 164 | 3 | `public void setsRetired(String railway, double sets)` | `sets` of rolling stock worn out by `railway` this month. |
| 169 | 1 | `public double available()` | Tonnes of old steel standing to be bought now: arisen since the yards last bought. |
| 172 | 1 | `public double available(Source s)` | ...and from one source. |
| 175 | 1 | `public double availableFrom(String seller)` | ...and one seller's. |

### the yards buy (lines 177-214)

| line | len | member | says |
|---:|---:|---|---|
| 180 | 1 | **type** `public record Sale(String seller, double tonnes, double paid)` | One seller's part of a month's purchase: who, the tonnes the yards took of theirs, and what they were paid. |
| 190 | 3 | `public List<Sale> settle(double tonnes, double pricePerTonne)` | The month's purchase: the yards take `tonnes` of old steel (no more than stands), from every seller pro rata to what each threw out, at `pricePerTonne`; the rest goes to landfill, and the pool starts again. |
| 195 | 1 | `public double lastArisen()` | Old steel that arose in the last month the yards bought (or would have), tonnes recovered: what the planner weighs a yard against. |
| 198 | 1 | `public double lastArisen(Source s)` | ...from one source. |
| 201 | 1 | `public double lastBought()` | ...of it the yards bought. |
| 204 | 1 | `public double lastLandfill()` | ...and sent to landfill. |
| 207 | 1 | `public double lastPrice()` | What the yards paid a tonne that month: PAY_SHARE of scrap's export price; 0 before any month. |
| 210 | 1 | `public Map<String, Double> lastPaid()` | What each seller was paid that month, by seller. |
| 213 | 1 | `public double lastPaidTotal()` | ...and in all. |

### a pool (lines 215-362)

| line | len | member | says |
|---:|---:|---|---|
| 223 | 139 | **type** `public static final class Pool` | One metal's old metal: what arose since its buyers last bought, by seller and by source, and their last month - the steel's until 0.7.133 this class's own fields, moved here whole when copper came (batch I8), so the c... |
| 238 | 6 | `public void arise(Source source, String seller, double tonnes)` _(in OldMetal.Pool)_ | `tonnes` of the metal thrown out by `seller` from `source`, of which RECOVERY comes back; nothing for none, or for nobody. |
| 246 | 5 | `public double available()` _(in OldMetal.Pool)_ | Tonnes standing to be bought now: arisen since the buyers last bought. |
| 253 | 1 | `public double available(Source s)` _(in OldMetal.Pool)_ | ...and from one source. |
| 256 | 1 | `public double availableFrom(String seller)` _(in OldMetal.Pool)_ | ...and one seller's. |
| 265 | 24 | `public List<Sale> settle(double tonnes, double pricePerTonne)` _(in OldMetal.Pool)_ | The month's purchase: the buyers take `tonnes` (no more than stands), from every seller pro rata to what each threw out, at `pricePerTonne`; the rest goes to landfill, and the pool starts again. |
| 291 | 1 | `public double lastArisen()` _(in OldMetal.Pool)_ | What arose in the last month the buyers bought (or would have), tonnes recovered: what the planner weighs a buyer against. |
| 294 | 1 | `public double lastArisen(Source s)` _(in OldMetal.Pool)_ | ...from one source. |
| 297 | 1 | `public double lastBought()` _(in OldMetal.Pool)_ | ...of it the buyers bought. |
| 300 | 1 | `public double lastLandfill()` _(in OldMetal.Pool)_ | ...and sent to landfill. |
| 303 | 1 | `public double lastPrice()` _(in OldMetal.Pool)_ | What the buyers paid a tonne that month; 0 before any month. |
| 306 | 1 | `public Map<String, Double> lastPaid()` _(in OldMetal.Pool)_ | What each seller was paid that month, by seller. |
| 309 | 5 | `public double lastPaidTotal()` _(in OldMetal.Pool)_ | ...and in all. |
| 316 | 12 | `State state()` _(in OldMetal.Pool)_ | Its state, for the save (State's own fields; its copper left null). |
| 330 | 16 | `void restore(State s)` _(in OldMetal.Pool)_ | Puts a saved state back (its own fields; not its copper); null is a city that has thrown nothing out yet. |
| 348 | 7 | `void reset()` _(in OldMetal.Pool)_ | Nothing arisen and no month recorded. |
| 357 | 4 | `void scale(double s)` _(in OldMetal.Pool)_ | A currency reform: the price paid a tonne and what each seller was paid scaled, the tonnes not. |

### across a save (lines 363-418)

| line | len | member | says |
|---:|---:|---|---|
| 373 | 9 | **type** `public static final class State` | Everything it holds, as DataSave.oldMetal keeps it: the steel arisen since the yards last bought by seller and by source, and the last month's record - and since 0.7.133 the copper's, the same fields under `copper` (n... |
| 384 | 5 | `public State state()` | Its state, for the save: the steel's, and the copper's under it. |
| 391 | 4 | `public void restore(State s)` | Puts a saved state back; null (a save from before 0.7.123) is a city that has thrown nothing out yet, and a state with no copper (before 0.7.133) one whose old copper had all gone. |
| 397 | 4 | `public void reset()` | Nothing arisen and no month recorded: a new city. |
| 407 | 4 | `public void scale(double s)` | A currency reform (Game's redenomination): the money in it scaled - the price paid a tonne and what each seller was paid - and the tonnes not, a tonne being a tonne through a reform. |
| 412 | 4 | `private static void copyInto(double[] from, double[] to)` |  |
| 417 | 1 | `private static double finite(double v)` |  |

