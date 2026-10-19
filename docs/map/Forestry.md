# Forestry.java - 632 lines · 35 methods · 9 constants · sectors

`ham/citybuildersim/sectors/Forestry.java` - generated 2026-10-18 by CodeMap; line numbers are as of that run.

> The crews that cut the city's forest, and the plantations they plant -
> Forestry (0.7.138, batch I9; docs/studies/spec-materials.md 2.1, 2.11 and
> 5's I9 row; the study's - materials-chains-research.md - 1.3, 5 and 10's
> Q13 to Q15; the zoning study, spec-zoning.md 2 and 3): the fifth materials
> chain's cutting industry, on the end of Sectors.KEYS after Copper Refining
> (Jerus's Q21, 2026-10-08: iron, coal, stone, copper, wood).
> 
> WHY. Forest has been a third of the land since 0.7.56 - 13,700 m3 of
> standing timber a square kilometre, Canada's 137 m3 a hectare [W1][W2] -
> and since 0.7.107 (batch Z1) the forest law has said which of it may be
> cut and a building on open forest has cleared it, its wood counted as
> taken out and lost. Nothing cut it, so the city's forest was worth nothing
> and its wood went into the ground under the houses. Forestry cuts it into
> the two goods a harvest really makes, about half sawlogs and half pulpwood
> [W14][W15], sold to the world until Wood Products (batch I10) buys them.
> 
> NO SITES, AND NO CUTTING LIMIT (Jerus's Q14: "the map will include
> forest"). A crew stands on its own small landing and cuts the CUTTABLE
> FOREST (LandManager.cuttableForest(): what stands less the wood the law
> protects - the working and the open forest's, Z8), as much as its
> nameplate at the sector's rate asks, as far as the forest goes. Its four
> buildings (the study's 5.4; ids 129 to 132):
>   - the LOGGING CAMP, 1,000 m3 of sawlogs and 1,000 of pulpwood a month, 11
>     posts, D$3M, running D$61k;
>   - the HARVESTING OPERATION, 6,250 and 6,250, a harvester and forwarder,
>     18 posts, D$15M, D$217k - 1973 (research-design.md E14);
>   - the TREE PLANTATION, 1,000 ha growing 20 m3 a hectare a year [W4], 5
>     posts, D$2M, D$24k;
>   - the WOODLOT, 100 ha the same (Jerus's A12, 2026-10-10: "Yes" to "a
>     100-ha woodlot (167 m3 a month) so a small city zones forestry in
>     steps"), a tenth of a plantation and one post.
> At parity (the study's 5.5) a camp earns 1.3% of its cost a month
> exporting and 4.6% replacing imports, a harvesting operation 2.7% and
> 6.9%; a plantation loses 1.2%, nothing to sell for 25 years, which is why
> it is never an investment but a cost of cutting.
> 
> E, THE CREWS' RULE (Jerus's E and Z13, 2026-10-08: "yes 120"): a crew is
> a candidate only while the cuttable forest lasts FOREST_CREW_MONTHS - 120
> months, a crew's working life rather than its payback - of the cut of
> every crew standing, on site and the one ordered (roomFor()). A Logging
> Camp then needs 17.5 km2 of cuttable forest and a Harvesting Operation 109
> km2; a 10 km2 forest gets none. Past it the plan is ExtractionSector's:
> the best earnings on the whole cost, its freight in both, through the
> money gate, one crew in flight.
> 
> REGROWTH (Jerus's Q13: keep the rule, and show where it settles): what was
> cut grows back by LandManager.FOREST_REGROWTH, 1/240, a month - so a
> forest cut by H a month settles with 240 H cut, and the regrowth pays most
> at none standing (spec-zoning 3.3): only the law's protected share keeps
> forest standing. The planted stands add PLANTED_M3_A_HA_A_YEAR on their
> hectares a month to it (plantedGrowth(), handed to the land at the month's
> end, LandManager, THE PLANTED STANDS), never past the forest's full stock.
> The page shows where the forest settles (settlesAt()).
> 
> PLANTING (Jerus's Q15 and B, 2026-10-08: "No Forestry zone, no
> plantation"): at the month's end (endOfMonth()), one stand a month while
> the month's cut - the crews', the salvage not counted, as spec-zoning 2.2
> recommends - exceeds the regrowth the month's end will give, the stands
> on site counted as grown; ONLY on ground Forestry's zone holds free, at
> the zone's price, paid from Forestry's cash outside the interest test
> ... (14 more lines in the source)

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (22), [Good](Good.md) (18), [BusinessInvestment](BusinessInvestment.md) (13), [LandManager](LandManager.md) (9), [ProcessingSector](ProcessingSector.md) (6), [Resource](Resource.md) (5), [Game](Game.md) (5), [BuildCard](BuildCard.md) (4), [Sectors](Sectors.md) (3), [Formats](Formats.md) (2), [ExtractionSector](ExtractionSector.md) (1), [BuildingType](BuildingType.md) (1), [Investor](Investor.md) (1)

**Used by (12):** [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [BuildingDataCheck](BuildingDataCheck.md), [ForestryCheck](ForestryCheck.md), [IndustryPicture](IndustryPicture.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [Sectors](Sectors.md), [WoodProductsCheck](WoodProductsCheck.md), [ZoningCheck](ZoningCheck.md), [ZoningScreen](ZoningScreen.md)

## Sections

| line | section |
|---:|---|
| 133 | · what each building is |
| 175 | · the month's cut |
| 302 | · the month's end: the planted stands, and planting |
| 374 | · E, and the plan |
| 466 | · the retirement |
| 496 | · where the forest settles (Q13) |
| 527 | · the page |
| 580 | · the save |
| 619 | THE PICTOGRAMS' READS (0.7.141, batch I11; ExtractionSector's, and |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 98 | `Forestry.LOGGING_CAMP` | `129` | The Logging Camp's permanent id in buildings.json (0.7.138): 1,000 m3 of sawlogs and 1,000 of pulpwood a month. |
| 101 | `Forestry.HARVESTING_OPERATION` | `130` | The Harvesting Operation's (0.7.138): 6,250 and 6,250, a harvester and forwarder crew. |
| 104 | `Forestry.TREE_PLANTATION` | `131` | The Tree Plantation's (0.7.138): 1,000 ha planted on Forestry's zone. |
| 107 | `Forestry.WOODLOT` | `132` | The Woodlot's (0.7.138; Jerus's A12): 100 ha, a tenth of a plantation. |
| 110 | `Forestry.FOREST_CREW_MONTHS` | `120` | E (Jerus, 2026-10-08, Z13: "yes 120"): a crew only while the cuttable forest lasts this many months of every crew's cut with it - a crew's working life, not its payback (spec-zoning 3.4). |
| 113 | `Forestry.PLANTED_M3_A_HA_A_YEAR` | `20` | What a planted stand grows, cubic metres a hectare a year (the study's 5.1 and 5.7 [W4]: plantations 15-40, the managed Nordic 4-6): a plantation's 1,000 ha 1,667 m3 a month, a woodlot's 100 ha 167. |
| 116 | `Forestry.SAWLOG_SHARE` | `.5` | The share of a cubic metre cut that is a sawlog, the rest pulpwood: the harvest about 50:50 sawlog to pulpwood (the study's 5.1 [W14][W15]) - a crew's rows and the salvage cut alike. |
| 119 | `Forestry.SALVAGE_LINE` | `"Salvage cut"` | The line the builders' bill for a salvage cut is booked under on Forestry's inputs (Sector.billForService()). |
| 122 | `Forestry.SQ_FT_PER_HA` | `LandManager.SQ_FT_PER_KM2 / 100` | Square feet in a hectare: a hundredth of a square kilometre. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 178 | `private final Map<Good, Double> due` | The month's wood by good, struck once at the first of its two goods' produceFlow() and handed out a good at a time (cutTheMonth()). |
| 181 | `private final Map<Good, Double> asked` | ...and what each good's crews asked for, for its Output row. |
| 184 | `private double cutThisMonth` | The crews' cut this month, cubic metres: what the forest gave their ask (the salvage not in it) - what planting weighs. |
| 187 | `private double salvageThisMonth` | The salvage cut sold this month, cubic metres. |
| 190 | `private double salvageBilledThisMonth` | ...and what the builders billed for it. |
| 193 | `private BuildingsTemplate plantedThisMonth` | The stand planted this month, or null. |
| 196 | `private int standsPlanted` | Stands planted since the city was founded, for the page. |
| 322 | `private String plantingWord` | Why the month planted nothing, for the page: set by plant(). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 95 | 538 | **type** `public final class Forestry extends ExtractionSector` | The crews that cut the city's forest, and the plantations they plant - Forestry (0.7.138, batch I9; docs/studies/spec-materials.md 2.1, 2.11 and 5's I9 row; the study's - materials-chains-research.md - 1.3, 5 and 10's... |
| 124 | 8 | `public Forestry()` |  |

### what each building is (lines 133-174)

| line | len | member | says |
|---:|---:|---|---|
| 136 | 3 | `public static double cutOf(BuildingsTemplate t)` | Cubic metres of forest a building of its cuts a month at nameplate - sawlogs and pulpwood - nothing for a planted stand. |
| 141 | 3 | `public static boolean isCrew(BuildingsTemplate t)` | Whether a building is one of its crews: it cuts the forest. |
| 146 | 3 | `public static boolean isStand(BuildingsTemplate t)` | Whether a building is a planted stand: Forestry's plantation or woodlot. |
| 151 | 3 | `public static double growthOf(BuildingsTemplate t)` | What a planted stand grows a month, cubic metres: its hectares - its ground - at PLANTED_M3_A_HA_A_YEAR, a twelfth; nothing for anything else. |
| 157 | 3 | `protected boolean lifts(BuildingsTemplate t)` | A crew's lifts its ground (ExtractionSector's plan and retirement test): any building that cuts. |
| 162 | 5 | `public double crewsCut(boolean coming)` | The crews' cut a month at nameplate, cubic metres: those standing, and with `coming` those on site too - E's "every crew". |
| 169 | 5 | `public double plantedGrowth(boolean coming)` | The planted stands' growth a month, cubic metres: those standing, and with `coming` those being planted too. |

### the month's cut (lines 175-301)

| line | len | member | says |
|---:|---:|---|---|
| 205 | 15 | `public void produceFlow(Good g)` | A flow good this month - sawlogs and pulpwood from one cut: the crews' ask of both at the rate taken out of the cuttable forest at once (LandManager.extract(FOREST, m3): never the protected wood), each good its share ... |
| 222 | 16 | `private void cutTheMonth()` | The month's cut, once: the crews' ask of both goods out of the cuttable forest together, and the salvage. |
| 246 | 11 | `public double salvage(double m3)` | THE SALVAGE CUT (Z7): `m3` of standing wood a building cleared, added to the month's cut - half sawlogs, half pulpwood (SAWLOG_SHARE) - and billed to Forestry by the builders who cleared it at its crews' running cost ... |
| 265 | 19 | `public double salvageCostPerM3()` | What felling and hauling a cubic metre costs (★, spec 2.11): its crews' running cost - wages at today's rates, power and water (BusinessInvestment.runningCostOf()) and their consumables at the exchange factor - over t... |
| 286 | 3 | `double crewRunning(BuildingsTemplate t, BusinessInvestment plans)` | One crew's running cost a month at today's wages: its posts, power and water, and its consumables at the exchange factor. |
| 291 | 1 | `public double getCutThisMonth()` | The crews' cut this month, cubic metres, the salvage not in it. |
| 294 | 1 | `public double getSalvageThisMonth()` | The salvage cut sold this month, cubic metres. |
| 297 | 1 | `public double getSalvageBilled()` | ...and what the builders billed Forestry for it. |
| 300 | 1 | `public BuildingsTemplate getPlantedThisMonth()` | The stand planted this month, or null. |

### the month's end: the planted stands, and planting (lines 302-373)

| line | len | member | says |
|---:|---:|---|---|
| 310 | 10 | `public void endOfMonth(Game game)` | Once every good is made: the planted stands' growth handed to the land for the month's end (LandManager.setPlantedGrowth()), then planting (plant()). |
| 325 | 1 | `public String getPlantingWord()` | ...read. |
| 336 | 28 | `BuildingsTemplate plant(Game game, LandManager land)` | PLANTING (Q15, B; spec 2.11): one stand a month while the month's cut exceeds the regrowth the month's end will give - the stands on site counted as grown, so a stand is not planted twice - on ground Forestry's zone h... |
| 366 | 7 | `public BuildingsTemplate standFor(double held)` | The stand `held` square feet of Forestry's zone holds room for (★, A12): a Tree Plantation where it holds its 1,000 ha, a Woodlot where it holds 100 ha but not 1,000; null where it holds neither. |

### E, and the plan (lines 374-465)

| line | len | member | says |
|---:|---:|---|---|
| 381 | 3 | `public static boolean roomFor(double cuttable, double committed, BuildingsTemplate t)` | E (Z13): whether the cuttable forest lasts FOREST_CREW_MONTHS of the cut of every crew - `committed` m3 a month standing and on site - and one more `t` (pure). |
| 394 | 64 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Whether to put another crew on the forest (E; spec 2.11): one crew in flight (a stand being planted holds none back); the cuttable forest lasting FOREST_CREW_MONTHS of every crew's cut with it (roomFor()); of the crew... |
| 460 | 5 | `static String eWords(double cuttable, double committed, BuildingsTemplate t)` | E's refusal in words: the forest it may cut, and the months it would last with the smallest crew. |

### the retirement (lines 466-495)

| line | len | member | says |
|---:|---:|---|---|
| 470 | 3 | `public double unitsOf(BuildingsTemplate t)` | What one of its buildings contributes to the measures it is retired on: a crew's cut a month, a stand's growth. |
| 480 | 8 | `public double[] retirementDemandAndCapacity(Game game)` | Its crews against the forest they may cut: spare past what stands to cut - all of them with none - and the distress rule only while the forest holds a month of their cut. |
| 491 | 4 | `public boolean mayRetire(BuildingsTemplate t)` | A crew may be sold; a planted stand only once no crew stands or is coming - it is the cut's cost while there is a cut. |

### where the forest settles (Q13) (lines 496-526)

| line | len | member | says |
|---:|---:|---|---|
| 510 | 8 | `public static double settlesAt(double full, double cleared, double cut, double planted, double protectedShare)` | WHERE THE FOREST SETTLES (Jerus's Q13: keep the regrowth rule, and show it; spec 2.11, spec-zoning 3.3), pure: the share of the forest's full stock `full` standing in the long run at a crews' cut of `cut` m3 a month w... |
| 520 | 6 | `public double settlesAt()` | ...this city's, at the month's cut: its forest, cleared ground, planted growth and law. |

### the page (lines 527-579)

| line | len | member | says |
|---:|---:|---|---|
| 530 | 49 | `public List<Line> ownLines(Game game)` |  |

### the save (lines 580-618)

| line | len | member | says |
|---:|---:|---|---|
| 583 | 7 | `protected void saveExtras(Map<String, Double> extras)` |  |
| 592 | 9 | `protected void restoreExtras(Map<String, Double> extras)` |  |
| 603 | 1 | `public int getStandsPlanted()` | Stands planted since the city was founded. |
| 607 | 11 | `public void reset()` | Everything back to a founding sector: no cut, no salvage, nothing planted. |

### THE PICTOGRAMS' READS (0.7.141, batch I11; ExtractionSector's, and (lines 619-632)

| line | len | member | says |
|---:|---:|---|---|
| 626 | 6 | `protected String groundRefusal(BuildingsTemplate t)` | A crew's ground is the forest it may cut, in plan()'s words: none at all, or E - room for 120 months of every crew's cut with one more (roomFor(), eWords()). |

