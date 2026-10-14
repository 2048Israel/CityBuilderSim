# CopperRefining.java - 251 lines · 12 methods · 4 constants · sectors

`ham/citybuildersim/sectors/CopperRefining.java` - generated 2026-10-14 by CodeMap; line numbers are as of that run.

> Smelters, refineries and rod mills - Copper Refining (0.7.133, batch I8;
> docs/studies/spec-materials.md 2.1, 2.7, 2.9, 2.12 and 5's I8 row; the
> study's - materials-chains-research.md - 4): copper's processing industry, on
> the end of Sectors.KEYS after Copper Mining (Jerus's Q21).
> 
> WHY. Copper Mining's concentrate is 30% copper and nobody uses it but a
> smelter; the city's old copper (OldMetal) is copper nobody here could use
> at all. One sector owns every building that turns copper into something
> else and builds whichever earns most on its whole cost (the study's 1.1 and
> 1.2, Jerus's A) - ProcessingSector's rule, so this class is a declaration,
> and its one rule of its own is THE OLD COPPER.
> 
> THE FOUR BUILDINGS (the study's 4.3; ids 124 to 127):
>   - the COPPER SMELTER, 9,750 t of anode a month from 33,000 t of
>     concentrate, and 29,700 t of sulphuric acid from its gas, a co-product
>     it sells (Jerus's Q12) - to the leach plants first;
>   - the ELECTROLYTIC REFINERY, 9,700 t of cathode from 9,750 t of anode;
>   - the SCRAP COPPER REFINERY, 4,000 t of cathode from 4,400 t of scrap -
>     the city's old copper first, the market's past it;
>   - the ROD MILL, 8,000 t of wire rod from 8,016 t of cathode.
> Manufacturing's Wire & Cable Works draws the rod into wire and cable, the
> chain's third industry (Manufacturing).
> 
> WHAT IT PAYS (the study's 4.4, at parity; A on imported feed for export, B
> on the city's feed at its export price, C that feed replacing imports, D
> both at import prices): the smelter -0.3, 1.2, 2.7 and 1.2% of its cost a
> month - smelting is thin, as it really is, and pays only on the city's own
> concentrate, which costs no freight; the electrolytic refinery -1.9, 0.8,
> 3.5 and 0.8%; the scrap refinery 0.0, 2.2, 2.9 and 0.7% - imported scrap
> just breaks even, so it is worth building on the city's old copper; the
> rod mill -6.7, 2.2, 11.1 and 2.2%, a thin premium on a big tonnage.
> 
> CAPTIVE (ProcessingSector, the spec's 2.7): anode and cathode go to its own
> works' need first - the smelters' anode to the refineries, the refineries'
> cathode to the rod mills - and only the rest is bought or sold.
> 
> THE OLD COPPER (OldMetal.copper(); the spec's 2.9). Old copper is not a
> good: it arises in a pool each month, and the scrap refineries take it
> before they bid for COPPER_SCRAP - as much as their intake at the rate
> (their rows' oldMetal, 4,400 t a refinery: all of their feed) and no more
> than arose, bought from each seller pro rata at OldMetal.PAY_SHARE of
> COPPER_SCRAP's export price (★ the spec's 2.9, as Jerus's Q8 rule for old
> steel) when the month's flow goods are made (produceFlow(ANODE)): the
> households paid into their savings, a sector by its other revenue, each on
> its input line, OldMetal.LINE, as Iron & Steel's yards pay for old steel.
> Its bid for scrap is the rest of its need (bid()); what the refineries do
> not take goes to landfill. One more scrap refinery is weighed with the old
> copper past what those standing take at that price, and the scrap past it
> at the market's (values()) - the last one standing with what the others
> leave it - so the rule builds one where the city's old copper is, and not
> on the world's scrap alone (the study's 4.4: "imported scrap just breaks
> even").
> 
> Who buys: Manufacturing's Wire & Cable Works the rod; Copper Mining's leach
> plants the acid; the world the rest - anode, cathode, rod and acid all
> ship.

**Uses:** [Good](Good.md) (22), [OldMetal](OldMetal.md) (10), [SpreadPlanner](SpreadPlanner.md) (3), [Trade](Trade.md) (2), [Formats](Formats.md) (2), [ProcessingSector](ProcessingSector.md) (1), [Sectors](Sectors.md) (1), [BuildingType](BuildingType.md) (1), [Sector](Sector.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [Game](Game.md) (1), [GoodsMarket](GoodsMarket.md) (1), [BuildCard](BuildCard.md) (1)

**Used by (10):** [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildingDataCheck](BuildingDataCheck.md), [CopperRefiningCheck](CopperRefiningCheck.md), [GdpCheck](GdpCheck.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [MoneyCheck](MoneyCheck.md), [ReadPathCheck](ReadPathCheck.md), [Sectors](Sectors.md)

## Sections

| line | section |
|---:|---|
| 105 | THE OLD COPPER (0.7.133, batch I8; OldMetal.copper(), the spec's 2.9) |
| 229 | · the page |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 77 | `CopperRefining.SMELTER` | `124` | The Copper Smelter's permanent id in buildings.json (0.7.133): 9,750 t of anode and 29,700 t of acid a month from 33,000 t of concentrate. |
| 80 | `CopperRefining.ELECTROLYTIC_REFINERY` | `125` | The Electrolytic Refinery's (0.7.133): 9,700 t of cathode a month from 9,750 t of anode. |
| 83 | `CopperRefining.SCRAP_REFINERY` | `126` | The Scrap Copper Refinery's (0.7.133): 4,000 t of cathode a month from 4,400 t of the city's old copper and scrap. |
| 86 | `CopperRefining.ROD_MILL` | `127` | The Rod Mill's (0.7.133): 8,000 t of wire rod a month from 8,016 t of cathode. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 74 | 178 | **type** `public final class CopperRefining extends ProcessingSector` | Smelters, refineries and rod mills - Copper Refining (0.7.133, batch I8; docs/studies/spec-materials.md 2.1, 2.7, 2.9, 2.12 and 5's I8 row; the study's - materials-chains-research.md - 4): copper's processing industry... |
| 88 | 16 | `public CopperRefining()` |  |

### THE OLD COPPER (0.7.133, batch I8; OldMetal.copper(), the spec's 2.9) (lines 105-228)

| line | len | member | says |
|---:|---:|---|---|
| 110 | 3 | `private OldMetal.Pool pool()` | The city's old copper, or null with no city (a fixture's bare sector). |
| 115 | 3 | `public double oldCopperIntake()` | Tonnes of old copper its standing scrap refineries take a month at nameplate (their rows' oldMetal). |
| 120 | 5 | `public double oldCopperPrice()` | What its refineries pay a tonne of old copper today: OldMetal.PAY_SHARE of COPPER_SCRAP's export price; 0 with no markets. |
| 127 | 4 | `public double oldCopperTaken()` | Old copper its refineries took this month, tonnes: the pool's last purchase (settled when the month's flow goods are made, before any market clears); nothing with no city. |
| 145 | 21 | `public void produceFlow(Good g)` | Makes a flow good for the month; and with its anode - the first flow good it makes, before any market clears - the month's old copper is bought (THE OLD COPPER): its refineries' intake at the rate, no more than arose,... |
| 169 | 5 | `public double bid(Good g)` | What it asks the market for of a good this month; of scrap, its need past the old copper its refineries took this month (THE OLD COPPER). |
| 177 | 6 | `protected double inputCostAtRate()` | The month's inputs at the rate, at today's prices: Sector's, its old copper at what it paid for it rather than the scrap market's price. |
| 185 | 4 | `public double spareOldCopper()` | Old copper arisen in the pool's last month past what the refineries standing take at nameplate: what one more would run on. |
| 191 | 8 | `public double leftForTheLast()` | ...and what the others leave the last one standing: the pool's last month less the rest's intake at nameplate, at most its own. |
| 211 | 17 | `protected SpreadPlanner.Values values()` | The city's values a works of its is weighed at (ProcessingSector's), with the old copper in its scrap (THE OLD COPPER): one more refinery's scrap is the old copper past what those standing take, at oldCopperPrice(), a... |

### the page (lines 229-251)

| line | len | member | says |
|---:|---:|---|---|
| 232 | 19 | `public List<Line> ownLines(Game game)` |  |

