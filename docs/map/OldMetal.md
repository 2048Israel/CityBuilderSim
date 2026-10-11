# OldMetal.java - 293 lines · 22 methods · 8 constants · model

`ham/citybuildersim/OldMetal.java` - generated 2026-10-11 by CodeMap; line numbers are as of that run.

> The city's old steel: what it throws out each month that a Scrap Yard can
> buy and sort into scrap - its scrapped cars, its demolitions, its
> fabricators' offcuts and its railway's worn-out wagon sets (0.7.123, batch
> I4; runs/spec-materials.md 2.9, materials-chains-research.md 2.7).
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
>   - a wagon set worn out, STEEL_A_SET, the railway's (sectors.Rail).
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
> since the yards last bought, by seller, and the last month's record. A
> save from before 0.7.123 carries none, which is right: that city had
> thrown nothing out that a yard could have bought.

**Uses:** [Trade](Trade.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1)

**Used by (10):** [DataSave](DataSave.md), [Game](Game.md), [HeavyIndustry](HeavyIndustry.md), [MaterialsCheck](MaterialsCheck.md), [MoneyAudit](MoneyAudit.md), [MoneyCheck](MoneyCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [SpreadPlanner](SpreadPlanner.md)

## Sections

| line | section |
|---:|---|
| 108 | · the last month the yards bought (settle()): the record, for the planner and the screens |
| 114 | the steel arises |
| 158 | the yards buy |
| 221 | across a save |

## Enum constants

| line | constant | says |
|---:|---|---|
| 93 | `OldMetal.Source.CARS` | Cars the households scrapped. |
| 95 | `OldMetal.Source.DEMOLITIONS` | Buildings pulled down. |
| 97 | `OldMetal.Source.OFFCUTS` | The fabricators' offcuts. |
| 99 | `OldMetal.Source.ROLLING_STOCK` | The railway's wagon sets worn out. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 67 | `OldMetal.RECOVERY` | `.85` | The share of the steel in what the city throws out that comes back as old steel: 85% for construction and cars (the study's 2.7, [I52]). |
| 70 | `OldMetal.STEEL_A_CAR` | `.9` | Tonnes of steel in a car scrapped (the study's 2.7, [I63]). |
| 73 | `OldMetal.STEEL_A_MATERIALS_UNIT` | `.3` | Tonnes of steel a unit of a demolished building's construction material holds: ★est., the study's 9.3 - about 3 t in a House's ten units. |
| 76 | `OldMetal.OFFCUTS_A_TONNE_FABRICATED` | `.05` | Tonnes of offcuts a tonne of fabricated steel made leaves: the Fabrication Shop's 1,260 t of steel into 1,200 (the study's 2.7). |
| 79 | `OldMetal.STEEL_A_SET` | `300` | Tonnes of steel in a wagon set worn out: a set's weight, a locomotive and what it pulls (Good.ROLLING_STOCK's 300 t). |
| 82 | `OldMetal.PAY_SHARE` | `.70` | What a yard pays the sellers for a tonne of old steel, a share of scrap's export price: 70% (Jerus's Q8, 2026-10-08). |
| 85 | `OldMetal.LINE` | `"Old metal"` | The yards' purchase, named on their input line. |
| 88 | `OldMetal.SOLD` | `"Old metal sold"` | ...and the sellers' sale, named in their other revenue. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 103 | `private final Map<String, Double> pending` | Steel arisen since the yards last bought, tonnes recovered, by seller (a sector's key, or Trade.HOUSEHOLDS), in the order each first arose. |
| 106 | `private final double[] pendingBySource` | ...and by source, in Source's order. |
| 110 | `private final double[] lastBySource` |  |
| 111 | `private double lastArisen, lastBought, lastLandfill, lastPrice` |  |
| 112 | `private final Map<String, Double> lastPaid` |  |
| 229 | `public Map<String, Double> pending` |  |
| 230 | `public double[] pendingBySource` |  |
| 231 | `public double[] lastBySource` |  |
| 232 | `public double lastArisen, lastBought, lastLandfill, lastPrice` |  |
| 233 | `public Map<String, Double> lastPaid` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 64 | 230 | **type** `public final class OldMetal` | The city's old steel: what it throws out each month that a Scrap Yard can buy and sort into scrap - its scrapped cars, its demolitions, its fabricators' offcuts and its railway's worn-out wagon sets (0.7.123, batch I4... |
| 91 | 10 | **type** `public enum Source` | Where old steel comes from. |

### the last month the yards bought (settle()): the record, for the planner and the screens (lines 108-113)

### the steel arises (lines 114-157)

| line | len | member | says |
|---:|---:|---|---|
| 117 | 6 | `public void arise(Source source, String seller, double tonnes)` | `tonnes` of steel thrown out by `seller` from `source`, of which RECOVERY comes back as old steel; nothing for none, or for nobody. |
| 125 | 3 | `public void carsScrapped(double cars)` | Cars the households scrapped (Motoring, at the top of the month). |
| 130 | 4 | `public void demolished(BuildingsTemplate t, int n, String owner)` | `n` of a building pulled down, its steel its owner's (`owner`, a sector's key). |
| 136 | 3 | `public void fabricated(String maker, double tonnes)` | `tonnes` of fabricated steel made by `maker` this month: its offcuts. |
| 141 | 3 | `public void setsRetired(String railway, double sets)` | `sets` of rolling stock worn out by `railway` this month. |
| 146 | 5 | `public double available()` | Tonnes of old steel standing to be bought now: arisen since the yards last bought. |
| 153 | 1 | `public double available(Source s)` | ...and from one source. |
| 156 | 1 | `public double availableFrom(String seller)` | ...and one seller's. |

### the yards buy (lines 158-220)

| line | len | member | says |
|---:|---:|---|---|
| 161 | 1 | **type** `public record Sale(String seller, double tonnes, double paid)` | One seller's part of a month's purchase: who, the tonnes the yards took of theirs, and what they were paid. |
| 171 | 24 | `public List<Sale> settle(double tonnes, double pricePerTonne)` | The month's purchase: the yards take `tonnes` of old steel (no more than stands), from every seller pro rata to what each threw out, at `pricePerTonne`; the rest goes to landfill, and the pool starts again. |
| 197 | 1 | `public double lastArisen()` | Old steel that arose in the last month the yards bought (or would have), tonnes recovered: what the planner weighs a yard against. |
| 200 | 1 | `public double lastArisen(Source s)` | ...from one source. |
| 203 | 1 | `public double lastBought()` | ...of it the yards bought. |
| 206 | 1 | `public double lastLandfill()` | ...and sent to landfill. |
| 209 | 1 | `public double lastPrice()` | What the yards paid a tonne that month: PAY_SHARE of scrap's export price; 0 before any month. |
| 212 | 1 | `public Map<String, Double> lastPaid()` | What each seller was paid that month, by seller. |
| 215 | 5 | `public double lastPaidTotal()` | ...and in all. |

### across a save (lines 221-293)

| line | len | member | says |
|---:|---:|---|---|
| 228 | 7 | **type** `public static final class State` | Everything it holds, as DataSave.oldMetal keeps it: the steel arisen since the yards last bought by seller and by source, and the last month's record. |
| 237 | 12 | `public State state()` | Its state, for the save. |
| 251 | 16 | `public void restore(State s)` | Puts a saved state back; null (a save from before 0.7.123) is a city that has thrown nothing out yet. |
| 269 | 7 | `public void reset()` | Nothing arisen and no month recorded: a new city. |
| 282 | 4 | `public void scale(double s)` | A currency reform (Game's redenomination): the money in it scaled - the price paid a tonne and what each seller was paid - and the tonnes not, a tonne being a tonne through a reform. |
| 287 | 4 | `private static void copyInto(double[] from, double[] to)` |  |
| 292 | 1 | `private static double finite(double v)` |  |

