# fixI9 notes - forestry, 0.7.138 (batch/I9, worktree /home/claude/git/wt-I9, base 980e99b)

## 0. Steps done
- Worktree clean at 980e99b; scratch /home/claude/scratch-i9 (build/suite/pt/pte/bmc/chk/gate.sh, gen/, diag/, basetree/, ens/).
- 031532c model + ForestryCheck; 71d187c harnesses, GameVersion, counts, BuildingVisual; 11139a2 TradeCostCheck's wood.
- PROBE pt1/pte1 on 031532c (4); ISOLATION and diag on scratch-i9/diag (a copy with switches and a log, never in the worktree).
- Suite on 71d187c: 105/106, TradeCostCheck red (wood's prices missing from its table) -> 11139a2. UI build silent, Stale 0 firm.
- ENSEMBLE 07138 (scratch-i9/ens/out/07138, 031532c's model in scratch-i9/enstree) and base AUTO2 07137b (980e99b, basetree).

## 1. What I built
- **Sectors**: "Forestry" on the end of KEYS after Copper Refining (23rd); forestry() accessor.
- **Good** (appended): SAWLOGS .110/.065, PULPWOOD .090/.035 a cubic metre (freight .016875/.020625), flow, BULK, by sea
  GENERAL; a 10-arg constructor and tonnesPerCubicMetre() (0.9), tonnesPerUnit()'s "cubic metre".
- **Resource.FOREST**: good PULPWOOD (★1), inGroundShare .02 (US$9.59k a km2 of forest in an offer's price).
  **LandMarket.bareGround()** skips FOREST.
- **Rows 129-132** (json + twin; scratch-i9/gen): Logging Camp D$3M (8/2/1/0, 1,000+1,000 m3, cons 18.82, land 9,012, road
  302, 1900); Harvesting Operation D$15M (13/4/1/0, 6,250+6,250, cons 148.52, 45,062, 1,890, 1973); Tree Plantation D$2M
  (4/1/0/0, nothing made, cons 5.66, 1,000 ha = 107,639,104 sq ft, road 0, 1900); Woodlot D$200k (1/0/0/0, cons .566,
  100 ha, 1900). No kW, no water, no diesel (★5).
- **sectors/Forestry** (ExtractionSector on FOREST): produceFlow() cuts both goods out of cuttableForest() together
  (cutTheMonth()), the salvage added half and half (salvage(m3): bill = m3 x salvageCostPerM3(), billForService("Construction",
  "Salvage cut") + Construction.earnSalvageCut()); plan() = E (roomFor(): cuttable >= 120 x (crews standing+on site + one))
  then staff, whole-cost money gate, best earns/cost, one crew in flight (stands on site do not hold a crew back);
  endOfMonth(): land.setPlantedGrowth(standing stands' growth), plant(): one a month while cut > regrowthDue(stands incl. on
  site), only where Forestry's zone holds a plantation's 1,000 ha (else a woodlot's 100), cash >= quote + zone price,
  Game.buildFor() (no interest test, no borrowing); retirement: crews spare past the cuttable forest, stands never sold
  while a crew stands; settlesAt() (Q13); ownLines (the forest, the cut, regrowth, where it settles, E's months, planting);
  extras cutThisMonth, salvageThisMonth, salvageBilledThisMonth, standsPlanted.
- **LandManager**: salvagePendingM3 (clear() adds the standing wood; takeSalvage() settles the floor first and hands it over
  once; saved in Zoning.State.salvageM3); setPlantedGrowth()/regrowthDue(); endMonth E = max(0, E(1-1/240) - planted).
- **Construction** (outside): earnSalvageCut() -> other revenue, named "Salvage cut cleared", saved in extras when non-zero.
- **BuildCard** "Wood" group; **BuildingVisual** (outside): a site only for a resource in fields.
- GameVersion 0.7.138 entry; CLAUDE.md, README counts (23 sectors, 133 buildings, 106 harnesses).

## 2. Departures, with reasons
- FOREST's good PULPWOOD, not the brief's SAWLOGS (★1). No crew in either playtest (neither city holds 17.5 km2 cuttable).

## 3. Premises changed (before -> after) and the new assertions
- BuildingDataCheck: 129 -> 133 buildings; the good rule's exceptions + Forestry's crews (forest, no site); consumables 31 ->
  35 (+129-132); since + 129-132 (1973 the Harvesting Operation).
- BuildCardCheck: Industry thirteen -> fourteen groups ("Wood" after Copper); NEW the Wood group is Forestry's four.
- ZoningCheck 7: + the standing wood waits as salvage, none lost; NEW 10 (E on the cuttable forest; no Forestry zone no
  plantation; a plantation on its zone's held ground at its price). LandCheck NEW 27 (the cut and the clearing meet).
- NEW ForestryCheck 1-9 (header).

## 4. Trace movement and the ensemble
- t-bonds.csv gains Forestry's ten columns (L22..woB22); compared on the reference's (scratch-i9/projcmp.py, firstdiff.py).
- DEFAULT (pt1 vs pt07133m): identical to m1961; m1962 the first forest a building clears (266 m3 salvaged, billed D$192.8k:
  consRevenue +193, GDP +683). No crew all run (the city never holds 17.5 km2 cuttable: 1,098 m3 owned at m1961, 510,438 at
  the end); 484,057 m3 salvaged in all. End m4000: 720,045 people (784,264), GDP $6.2T ($11.2T) at a price level 1,293 (2,310).
- EARTH (pte1 vs pte07133m): identical to m2; m3 Rotterdam's founding clears 1,111 m3 (GDP +55). Its forest 0.23 km2 at the
  start, 23.9 km2 owned at most: no crew; 326,966 m3 salvaged. End m4005: 579,709 (756,063), $5.8T ($11.5T) at 1,523 (2,356).
- Both: "Nothing. Every month passed the audit and every reload matched." No planting (no Forestry zone: the test player sets none).
- ISOLATION (scratch-i9/diag, a copy of the tree with two switches and a log; nothing of it in the worktree): A, the salvage
  off and forest priced: the reference to the byte on t-bank, bonds, borrow, build, house, labour, pop; t-month's cash column
  alone, from m2171 (default) and m761 (Earth) - the city paying for an offer's forest. B, forest unpriced and the salvage on:
  the full probe's figures exactly. So the salvage cut alone moves the runs: a few hundred cubic metres a month of export
  revenue and a builders' bill, compounding over 2,000-4,000 months.
- ENSEMBLE 07138 (scratch-i9/ens/out/07138/score.md, 80 runs, none missing or short): 21/25, the same as 07133f, the same
  four standing reds: A2 14 [6-19] (ref 14 [5-19]), S1 1.87% (2.05%), H1, R3 -3.55% (3 seeds above +1, as ref). A1 2.25%,
  T1 16/16 at m1,437 (m1,503), AUTO2 T2 195,385 (178,877), F1 0/80, M1 0, pairing 64/64.
- MAIN MOVED IT FIRST: base 980e99b's AUTO2 (07137b, basetree) already differs from 07133f from m412 (seed 1); 07138's AUTO2
  equals base's to m1471-m2323 (seed 11 to the end), median final pop 195,385 both, -10.3% to +12.1% a seed. S1's longest
  spell 31 months (ref 24) is main's: base ZERO_SLACK seeds 7 and 3 identical to 07138 (31, 27), seed 14 from m1853 (25 both).

## 6. ★ calls
- None in place of what Jerus asked: Q13 (regrowth kept, settling shown), Q14 (no sites, no limit), Q15/B (plantations only on
  Forestry's zone, a cost of cutting), E (120 months on the cuttable forest), Z7 (salvage sold), A12 (woodlot) as decided.
- ★1 FOREST's PRICING GOOD PULPWOOD, not the brief's SAWLOGS: spec 2.4 lists PULPWOOD and the study's 8 strikes the 2% "at the
  pulpwood price" (US$0.70 a m3 standing, ~US$9,590 a km2); SAWLOGS would make it 1.86x dearer with no source. Flip: Resource.
  FOREST's good -> Good.SAWLOGS (groundGood() and lifts() work either way: every crew makes both).
- ★2 THE WOODLOT'S RULE: a Tree Plantation where Forestry's zone holds its 1,000 ha free, a Woodlot where it holds 100 ha but
  not 1,000, nothing under 100 (Forestry.standFor()). A small zone fills in woodlots; a big one in plantations, not in ten
  times as many small stands. One stand a month either way.
- ★3 THE WOODLOT'S FIGURES (no study row): the plantation's ground, cost and consumables a hectare (100 ha, D$200k, .566) and
  one post (half the plantation's tenth, rounded up to a person): runs 4.026, -2.01% a month in every case.
- ★4 PLANTING WEIGHS THE STANDS ON SITE AS GROWN (regrowthDue(plantedGrowth(true))), so a stand being planted is not planted
  twice; a planted stand counts in the regrowth (LandManager) only once standing; from the first standing month, as "a
  managed estate of every age" (spec 2.11). Paid only from Forestry's cash (cash >= quote + zone price), never borrowed;
  through Game.buildFor() (the builders' queue, no interest test). A stand is a building the sector owns (capital), not an
  expense; never sold while a crew stands or is coming (mayRetire), units its growth so a crewless Forestry may sell one.
- ★5 NO POWER, WATER OR DIESEL on the four (the study gives none; the crews' machines' fuel and wear and the seedlings are
  the consumables); crews' ground the Steel Foundry's per D$ (a landing, 9,012 / 45,062 sq ft); road load logs out at 0.9 t.
- ★6 THE SALVAGE'S TIMING: taken at Forestry's first produceFlow() of the month (takeSalvage() settles the month's floor
  first), so ground set by hand is cleared and sold the same month; what clears later (between presses, past the market)
  waits in salvagePendingM3, saved in Zoning.State.salvageM3 - none lost across a save.
- ★7 THE BILL: billForService("Construction", "Salvage cut") on Forestry and Construction.earnSalvageCut() (other revenue,
  named "Salvage cut cleared", off the investment line) - Rail's haulage shape; the cost a m3 the crews' running cost at
  today's wages + consumables at the exchange factor over their nameplate cut (none standing: a Harvesting Operation's).
- ★8 ONE CREW IN FLIGHT counts crews only (a stand on site holds none back); E counts crews standing and on site.
- ★9 THE PAGE settles at max(the law's protected share, (O - C - 240 x (cut - planted)) / O), the spec's rule (the month's
  end equilibrium is 240(cut - planted) - cut; the spec's is kept, matching mockup 4's 29.1%).
- ★10 BUILDINGVISUAL (outside my files): a site only for a resource in fields, so a crew draws as a building and the map's
  districts sum to it - without it the first crew would be a LongPlaytest finding ("districts do not sum"). Coal's, stone's
  and copper's leach plant still have the old gap (first type on a non-SITED resource never placed) - for I14.
- ★11 NO SAVE_FORMAT BUMP: an older save restores Forestry empty (by key), no salvage waiting (none was owed: that wood was
  lost before I9), its forest's E and C as saved, the new goods' markets fresh, listed offers at their listed price until
  relisted. Missing, not misread.
- ★12 FORESTRY'S REFUSALS read as ORE on the build card (BuildCard.ORE_WORDS + "forest"): "no forest it may cut" and E's.

## 5. The gate, and the final commit
- On 11139a2 (scratch-i9/gate.log): both builds silent; suite 106 run, 106 passed (HealthCheck green); tools.Stale 0 firm, 463
  soft; playtests 8/8 identical to pt1 and 8/8 to pte1 (re-baseline: pt07138 = scratch-i9/pt1, pte07138 = scratch-i9/pte1);
  0 w/crlf outside .bat. BuildMenuCheck there 131/133 (the stands described as nothing) -> e278e92 (ui/BuildScreen's words,
  no model change: the suite and the playtests do not build ui/) -> BuildMenuCheck PASS 133 of 133, UI build silent, Stale 0
  firm. FINAL COMMIT e278e92. MapCheck's 5 ms bound passed in the suite. Maps left to the docs pass.

## 7. For the next batch
- I10 (Wood Products): Forestry ships SAWLOGS/PULPWOOD abroad today; the salvage alone in both playtests (no crew: neither
  city holds 17.5 km2 cuttable) - a city with big forest, or ZoningCheck/ForestryCheck's towns, is where crews appear.
- I11/I13: Forestry.settlesAt(), getCutThisMonth(), regrowthDue(), roomFor() are the page's figures; the card says nothing of E.
- I14: CityMap places the first type on a non-SITED resource nowhere (coal's mines, quarries, the leach plant): a latent
  "districts do not sum" finding the day one is built; crews avoid it by ★10.
- Docs pass: maps, dials (Forestry's 7), harnesses.md (ForestryCheck; ZoningCheck 10; LandCheck 27), ExtractionSector's
  header (Forestry), LandManager's ZONES banner (THE PLANTED STANDS), Sectors' header, docs/notes counts.

## 8. For Jerus to check on the PC
1. Build › Industry: a "Wood" group after Copper - Logging Camp, Harvesting Operation, Tree Plantation, Woodlot; the stands'
   card says what they grow and that only a Forestry zone gets one.
2. Sectors › Forestry: the forest, the cut, the salvage, "where the forest settles", E's months.
3. Policy › Zoning: zone some km2 for Forestry; with crews cutting past the regrowth a woodlot or a plantation is planted.

## 9. Outside my files
sectors/Construction (earnSalvageCut(), "Salvage cut cleared", saved in extras); BuildCard ("Wood" group, ORE_WORDS + forest);
BuildingVisual (site only for a resource in fields); ui/BuildScreen (the stands' words, a count); BuildingManager's twin rows;
AllChecks (one line); TradeCostCheck, CopperMiningCheck, CopperRefiningCheck (premises). Nothing outside worktree/scratch/notes.
