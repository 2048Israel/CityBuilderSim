# RefineryFlow.java - 371 lines · 29 methods · 5 constants · sectors

`ham/citybuildersim/sectors/RefineryFlow.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> The refinery as a campus of units, and a month's flow through them (0.7.80,
> batch O4; runs/spec-oil.md 2.3): the crude units' run cut into its streams,
> the conversion units taking their feed stream by stream, and what is left
> of each stream sold as the product it is worth with no unit to upgrade it.
> 
> WHY. Until 0.7.80 a refinery was only its crude units (Refining, THE
> SLATE): a barrel of medium crude made 70 L of petrol, 169 L of diesel and
> a fuel oil worth less than the crude, and nothing could turn the cheap
> cuts into dear ones. A real refinery is a crude unit and the units behind
> it - a reformer, a cracking unit, a hydrocracker, alkylation, a coker, a
> lube plant, an asphalt unit - each taking one cut and making more of the
> products the city wants (the research's 1.2). Each is a building here
> (ids 77 to 90, a small and a large of each kind; BuildingsTemplate's
> refineryUnit() and feedPerMonth()), and this is what they do together.
> 
> PURE. solve() reads only its arguments - each kind's feed a month, the
> crude, its mix of grades and the products' values - so Refining caches it
> on those (Refining.flow()) and a check can call it with any it likes.
> 
> THE PROTOTYPE'S, PORTED (scratch-oil's spread.py, its solve()), in its
> order, so that with no conversion unit the flow is Refining.slate() of the
> same crude and mix to the bit (RefineryCheck asserts it):
>   1. the crude's litres split into the cuts, grade by grade in
>      Deposit.Grade's order, the heavy crude's residue kept apart;
>   2. hydrogen: the reformers' make on the straight-run heavy naphtha, at
>      HYDROGEN_MADE a barrel, which caps the hydrocrackers at
>      HYDROGEN_USED a barrel (Q12: no other hydrogen);
>   3. the streams in flow order - gas oil, residue, heavy naphtha, cracked
>      gas - each taken by its units in order of spread, the widest first; a
>      unit at a spread of nothing or less takes none and idles; what a unit
>      makes of another unit's feed (cracked gas, heavy naphtha, diesel,
>      gas) joins that stream, so it may be taken further down the order;
>   4. what is left of each stream to its product: gas and cracked gas to
>      PETROLEUM GAS, light naphtha to PETROL, heavy naphtha to NAPHTHA,
>      kerosene to JET, diesel to DIESEL, gas oil to FUEL_OIL;
>   5. the residue to FUEL_OIL only when cut three to one with the pool's
>      diesel (Q5), and what the diesel cannot cut burned at no value.
> 
> ★ O4-2: THE RESIDUE IS ONE STREAM. The prototype solved the coker's
> residue and then, apart, the asphalt units' heavy residue, so a coker took
> heavy residue ahead of an asphalt unit whatever their spreads - and at the
> world's middle prices the asphalt unit's is the wider (its bitumen is worth
> $0.559 a litre of residue, the coker's products $0.392). The spec's rule
> is that within a stream the units take feed in order of spread, the heavy
> part kept apart: so the coker and the asphalt units share one stream in
> spread order, the coker taking the rest of the residue before the heavy
> part and an asphalt unit the heavy part only.
> 
> ★ O4-3: AN IDLE REFORMER MAKES NO HYDROGEN. The prototype counted the
> reformers' hydrogen from their capacity whether they ran or not; here it
> is counted only when they run (a spread above nothing), as they then take
> at least the straight-run naphtha the hydrogen was counted on.
> 
> Litres throughout, but for the coke and the bitumen (tonnes, at the
> residue's RESIDUE_LITRES_PER_TONNE): the crude's tonnes are turned into
> litres at Refining.CRUDE_LITRES_PER_TONNE, as the slate turns them.

**Uses:** [Good](Good.md) (52), [Refining](Refining.md) (12), [Deposit](Deposit.md) (3)

**Used by (16):** [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [BuildingCatalog](BuildingCatalog.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingManager](BuildingManager.md), [BuildingsTemplate](BuildingsTemplate.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [ReadPathCheck](ReadPathCheck.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [Refining](Refining.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 192 | · what a litre is worth (spec-oil 2.4's arithmetic, pure) |
| 226 | · the flow |

## Enum constants

| line | constant | says |
|---:|---|---|
| 96 | `RefineryFlow.Stream.GAS` |  |
| 97 | `RefineryFlow.Stream.LIGHT_NAPHTHA` |  |
| 98 | `RefineryFlow.Stream.HEAVY_NAPHTHA` |  |
| 99 | `RefineryFlow.Stream.KEROSENE` |  |
| 100 | `RefineryFlow.Stream.DIESEL` |  |
| 101 | `RefineryFlow.Stream.GAS_OIL` |  |
| 102 | `RefineryFlow.Stream.RESIDUE` |  |
| 103 | `RefineryFlow.Stream.HEAVY_RESIDUE` |  |
| 104 | `RefineryFlow.Stream.CRACKED_GAS` |  |
| 142 | `RefineryFlow.Kind.REFORMER` | Heavy naphtha into reformate (petrol) and gas, making the hydrogen a hydrocracker needs. |
| 145 | `RefineryFlow.Kind.CRACKER` | Gas oil cracked into petrol, diesel and fuel oil, and the cracked gas alkylation takes. |
| 149 | `RefineryFlow.Kind.HYDROCRACKER` | Gas oil and hydrogen into diesel and jet fuel, some heavy naphtha and gas. |
| 153 | `RefineryFlow.Kind.ALKYLATION` | Cracked gas into alkylate, a petrol. |
| 156 | `RefineryFlow.Kind.COKER` | Residue into gas, naphtha, diesel and fuel oil, and coke of 30% of the residue's weight. |
| 160 | `RefineryFlow.Kind.LUBE` | Gas oil into lubricants, the rest fuel oil. |
| 163 | `RefineryFlow.Kind.ASPHALT` | Heavy-crude residue into bitumen, 95% of it, by weight at the residue's litres a tonne. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 76 | `RefineryFlow.HYDROGEN_MADE` | `1400` | A barrel's hydrogen a reformer makes, in standard cubic feet, the middle of the research's 1.3 range [R6]. |
| 79 | `RefineryFlow.HYDROGEN_USED` | `1850` | ...and a barrel of gas oil a hydrocracker uses [R7]. |
| 82 | `RefineryFlow.RESIDUE_LITRES_PER_TONNE` | `1010` | Litres in a tonne of residue: fuel oil's [P35] (Good.FUEL_OIL), what the coke's and the bitumen's weights are struck at. |
| 85 | `RefineryFlow.LITRES_A_MONTH_PER_BARREL_A_DAY` | `30.44 * 158.987` | A litre a month of a barrel a day: 30.44 days of 158.987 L - how the units' sizes in barrels a day become their feed in litres (spec-oil 2.3). |
| 190 | `RefineryFlow.FLOW_ORDER` | `{ Stream.GAS_OIL, Stream.RESIDUE, Stream.HEAVY_NAPHTHA, Stream.CRACKED_GAS }` | The streams in the order they are solved (3 in the header). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 106 | `private final String words` |  |
| 107 | `private final Good leftover` |  |
| 166 | `private final String unitName` |  |
| 167 | `private final Stream feed` |  |
| 168 | `private final List<Yield> yields` |  |
| 230 | `private final double crude` |  |
| 231 | `private final double[] cuts, runs, spreads, spare` |  |
| 232 | `private final double hydrogen, spareHydrogen` |  |
| 233 | `private final Refining.Slate slate` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 71 | 301 | **type** `public final class RefineryFlow` | The refinery as a campus of units, and a month's flow through them (0.7.80, batch O4; runs/spec-oil.md 2.3): the crude units' run cut into its streams, the conversion units taking their feed stream by stream, and what... |
| 73 | 1 | `private RefineryFlow()` |  |
| 95 | 28 | **type** `public enum Stream` | The streams a crude unit's run is cut into and the units pass between them. |
| 109 | 4 | `Stream(String words, Good leftover)` _(in RefineryFlow.Stream)_ |  |
| 115 | 1 | `public String words()` _(in RefineryFlow.Stream)_ | The stream in a sentence: "heavy naphtha". |
| 118 | 1 | `public Good leftover()` _(in RefineryFlow.Stream)_ | The product what is left of it is sold as (4 in the header); null for the residue, which is cut with diesel (5). |
| 121 | 1 | `public boolean residue()` _(in RefineryFlow.Stream)_ | Whether it is residue, the heavy part or the rest. |
| 125 | 7 | **type** `public record Yield(Stream stream, Good good, double perLitre)` | One line of what a unit makes of a litre of its feed: into a stream, or a product (litres; tonnes of coke and bitumen). |
| 126 | 1 | `static Yield of(Stream s, double f)` _(in RefineryFlow.Yield)_ |  |
| 127 | 1 | `static Yield of(Good g, double f)` _(in RefineryFlow.Yield)_ |  |
| 130 | 1 | `public Good product()` _(in RefineryFlow.Yield)_ | What the line is sold as when nothing downstream takes it: its good, or its stream's leftover. |
| 140 | 48 | **type** `public enum Kind` | The kinds of conversion unit, each with the stream it is fed from and what it makes of a litre of it: the research's 1.2 (vol% of the feed), in the prototype's order. |
| 170 | 5 | `Kind(String unitName, Stream feed, Yield...yields)` _(in RefineryFlow.Kind)_ |  |
| 177 | 1 | `public String unitName()` _(in RefineryFlow.Kind)_ | The large unit's name: "Cracking Unit" (the small one's is "Small " before it). |
| 180 | 1 | `public Stream feed()` _(in RefineryFlow.Kind)_ | The stream it is fed from. |
| 183 | 1 | `public List<Yield> yields()` _(in RefineryFlow.Kind)_ | What it makes of a litre of its feed, in the research's order. |
| 186 | 1 | `public Stream solvedIn()` _(in RefineryFlow.Kind)_ | The stream it is solved in: the residue's for the coker and the asphalt unit alike (★ O4-2). |

### what a litre is worth (spec-oil 2.4's arithmetic, pure) (lines 192-225)

| line | len | member | says |
|---:|---:|---|---|
| 195 | 5 | `public static double[] values(ToDoubleFunction<Good> price)` | Each product's value, by Good's ordinal, from a price for each: what the flow and the spread are struck at. |
| 207 | 4 | `public static double feedValue(Stream s, double[] value)` | What a litre of a stream is worth with no unit to take it: the product its leftover is sold as, and residue (4 x fuel oil - diesel) / 3 - the fuel oil three litres of it make with a litre of diesel, less that litre (s... |
| 213 | 5 | `public static double spread(Kind k, double[] value)` | A unit's spread a litre of its feed: what it makes of the litre at the values, less what the litre is worth with no unit (the prototype's spread()). |
| 220 | 5 | `public static Map<Good, Double> madeAlone(Kind k, double litres)` | What `litres` of a kind's feed make with nothing downstream to take any of it, by product (litres; tonnes of coke and bitumen): a unit's card. |

### the flow (lines 226-371)

| line | len | member | says |
|---:|---:|---|---|
| 229 | 48 | **type** `public static final class Flow` | One month's flow: the crude's litres, the cuts, each kind's run and spread, the streams left, the hydrogen, and the products. |
| 235 | 11 | `Flow(double crude, double[] cuts, double[] runs, double[] spreads, double[] spare, double hydrogen, double spareHydrogen, Refin...` _(in RefineryFlow.Flow)_ |  |
| 248 | 1 | `public double crude()` _(in RefineryFlow.Flow)_ | The crude run, in litres. |
| 251 | 1 | `public double cut(Stream s)` _(in RefineryFlow.Flow)_ | A straight-run cut of the crude, before any unit: litres (CRACKED_GAS none). |
| 254 | 1 | `public double run(Kind k)` _(in RefineryFlow.Flow)_ | Litres of feed a kind of unit took. |
| 257 | 1 | `public double spread(Kind k)` _(in RefineryFlow.Flow)_ | A kind's spread a litre of its feed, at the flow's values. |
| 260 | 1 | `public double spare(Stream s)` _(in RefineryFlow.Flow)_ | What was left of a stream after the units, before it went to its product. |
| 263 | 1 | `public double hydrogen()` _(in RefineryFlow.Flow)_ | Hydrogen the reformers made for the hydrocrackers, standard cubic feet. |
| 266 | 1 | `public double spareHydrogen()` _(in RefineryFlow.Flow)_ | ...and what the hydrocrackers left of it. |
| 269 | 1 | `public Refining.Slate slate()` _(in RefineryFlow.Flow)_ | The products and the residue burned, as the slate reads them. |
| 272 | 1 | `public double of(Good g)` _(in RefineryFlow.Flow)_ | A product made: litres, or tonnes of coke and bitumen. |
| 275 | 1 | `public double burned()` _(in RefineryFlow.Flow)_ | Residue burned for want of diesel to cut it, litres. |
| 284 | 87 | `public static Flow solve(double[] feed, double crudeTonnes, double[] mix, double[] value)` | One month's flow (pure): `crudeTonnes` of crude at `mix` (each grade's share of the run in Deposit.Grade's order) through units able to take `feed` litres a month of each Kind (by its ordinal), their spreads struck at... |

