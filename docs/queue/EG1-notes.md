# EG1 - deposits where the geology says: notes

*0.8.12, 2026-10-20. The batch `docs/studies/spec-earth2-terrain.md` §8.5 recommends (decision D20; Jerus, 2026-10-12: "all rec"; queued 2026-10-20: "Queue it next after FR4"). §8 found why Jerus's Nova Scotia cities had oil and iron all round them: not the Earth's data but the weights' design, which only rescaled a generated world's densities within a narrow range. EG1 draws each Earth field where the geology puts it. **earth-1.bin did not change**: everything EG1 reads is in the file already (the provinces' classes and known petroleum, the geology grid, the far layers), and what the file lacks comes as hand rows in `Geology`. The slices were Sonnet 5.5 subagents' at extra effort (Jerus's rule since 2026-10-20), each reviewed by Claude (§3). Only the Earth moves: a generated world draws its fields to the bit as before, so the default playtest trace is byte-identical to main's (§4). Save format 41, refusing Earth saves only (§1).*

## 1. What shipped

- **Oil follows known petroleum** (`Geology`, OIL, IRON AND THE DISTRICTS). A province's oil weight is `OIL_ALLOWANCE` (0.1) x its class's E3 weight, plus its known petroleum per km2 of the province over the Earth's (`known(code) / area(code) / knownMean()`). E3's petroleum factor (1 + log10(1 + known/1,000), at most x3.9) is gone. A province's area is counted on the geology grid every Geology is made from, so the fixture and the full file agree. The Earth's mean is 0.005795 MMBOE a km2. The Greater Ghawar Uplift weighs 351.5; a province with no known petroleum weighs a tenth of its class's E3 weight.
- **The KNOWN hand table**: 40 rows, 366,000 MMBOE. The USGS World Petroleum Assessment 2000 behind earth-1's figures assessed no United States province (all 1023 rows: 118 carry known petroleum, none in the US), and the Scotian Shelf reads nought despite Sable and Deep Panuke (spec §8.2). Without the rows, "oil by known petroleum" would have emptied Texas, the Gulf of Mexico and Alaska. Each row: code, name, MMBOE (cumulative production and reserves, rounded estimates), and why. The constructor refuses a row whose code is not the data's, whose name differs, or whose file figure is not nought, so a re-baked file is checked again.
- **Iron goes to its geology**: a shield keeps E3's weight of 4; every other class keeps `IRON_ALLOWANCE` (0.1) of its E3 weight; inside a **recorded iron district** (the DISTRICTS hand table, 96 rows from the Pilbara to Wabana, each a centre and a radius of at least 20 km), the weight is `IRON_DISTRICT_WEIGHT` (20) where that is greater. The districts cover 817,473 km2, 0.6% of the land. `multiplierAt()` is a point's multiplier; the cells pass and the founding card read it. A district is found through 1-degree bins, at about 3% of the cells pass's time.
- **The Sydney coalfield** (the PATCHES table): the onshore coalfield, rasterised into the Appalachian Foldbelt (5219), reads the Sidney Basin (5220), a basin, so its coal is twice the fold belt's. `Geology.provinceAt()` holds the patch, so every reader of a province sees it; `EarthData.provinceAt()` stays the raw grid.
- **Z and the odds measured again** (`measure()`, `zOf(r, g)`): iron 0.9706, oil 0.3616 (its sea adds 32.8% on top, E3's 51.6%), coal 0.7884, copper 0.7323, uranium 0.7518, stone 0.6915; coking odds 0.135296, limestone 4.10885. Z still keeps the land's count of each resource's fields where it was: EG1 moves fields, it does not add or remove them.
- **Fewer and bigger on Earth** (`World.EARTH_FIELD_SCALE` 3): an Earth field stands for 30 of the old world's fields, so a third as many and each three times wider (up to 15,360 sites, `MAX_SITES`). A generated world's stay at 10; the first 5,120 places of the larger site table are the old table's, in its order.
- **A cluster stays on its province's ground** (`World.drawPool`'s Earth branch): a cluster's centre is drawn among its cell's 16 cells-pass points by their weights (`EarthTerrain.pointFieldWeights()`), then inside that point's 15 km square. A centre or a field is drawn again, up to `SEA_REDRAWS` times, while it lies on ground that holds none of its resource (`EarthTerrain.fieldGround()`); oil too, so no oil lands in a fold belt's or a shield's water. Five counters say how often (§5).
- **The save**: `SAVE_FORMAT` 41. An Earth city saved before is refused with `EarthSite.BEFORE_DEPOSITS_WORDS`: its holdings name fields by cell and place, and those now lie elsewhere. A generated save of format 40 still loads, so the general floor stays 40, as E7's ice floor did. **Jerus's current Halifax city (month 477, format 40) no longer loads in 0.8.12**; the build that saved it opens it.
- **Harnesses**: EarthDepositsCheck (new, §5); EarthTerrainCheck 11-13 and 15 and EarthFoundingCheck 2 and 6 moved onto EG1 (§3).

## 2. ★ calls

1. **★1 `OIL_ALLOWANCE` 0.1** (est.): a province with no known petroleum keeps a tenth of its class's weight, so unexplored basins are not empty. With Z_OIL at 0.36, such a province holds about a seventeenth of E3's oil fields, and a third as many again once they are bigger.
2. **★2 The KNOWN rows are estimates** (±30%; from cumulative production and reserves as the USGS national assessment and the EIA report them, not read from those reports in this batch). The world's other unassessed provinces (Cooper-Eromanga, Mozambique's coast, and others) keep the allowance; a row each is a line of `Geology` if Jerus wants them.
3. **★3 Iron's recorded deposits are 96 hand-drawn districts**, not a layer from the USGS Mineral Resources Data System. MRDS needs a download and a new earth-1.bin (its bake pipeline is not in the repository), and it is mostly American. The centres and radii are estimates from each district's mines.
4. **★4 `IRON_DISTRICT_WEIGHT` 20**, five times a shield's: Mesabi has 854 iron fields within 50 km (§8.3 had 456-608), and the Lorraine minette reads as one dense patch on Rotterdam's map. A lower weight thins the districts and gives the shields more.
5. **★5 `IRON_ALLOWANCE` 0.1** (est.): off a shield and outside a district, a tenth of E3's iron.
6. **★6 `EARTH_FIELD_SCALE` 3** (est.; "huge and rare"): a field is three times the ground and tonnes, so it costs about three times as much to buy whole. Rotterdam's city bought its first iron field at 47,922 people (US$378M, 58 sites, 402.8 Mt).
7. **★7 Oil's sea on top**, as E3 had it: Z keeps the land's count, and the shelves', basins' and rifts' sea adds 32.8%.
8. **★8 No cap on a rich province's oil**: the Greater Ghawar Uplift weighs 351.5, Villahermosa and Qatar near it. A city there sits in a sea of oil fields, as on the real Earth.
9. **★9 The Earth-only floor at 41**, as E7's: generated cities keep loading.

## 3. The slices, and what the review changed

| slice | by | what the review changed |
|---|---|---|
| 1, the model (Geology, EarthTerrain, World, Deposit, EarthSite) | Sonnet | nothing in the code. Its additions kept: a fifth counter (`centresOnNone`), `weightOf()`, `Deposit.mostReach()` at the Earth's widest (the map's and the conversion's pad only). The brief's javac line lacked JavaFX; corrected for the later slices |
| 2, the save (GameVersion, EarthSite, DataSave) | Claude | - |
| 3, EarthDepositsCheck (789 lines, new) | Sonnet | nothing. Its additions kept: detector controls in 7 (a sea plot reads "holds none" and an oil sea's plot "holds some", each as the model's `fieldGround` reads it, so a count of nought proves something), the full file's Rotterdam at the playtest's seed (4127) beside the fixture's, and a check that every world drew clusters by weight |
| 4, the Earth checks moved (EarthTerrainCheck, EarthFoundingCheck) | Sonnet | nothing. EarthTerrainCheck 11 works EG1's multiplier out from the constants and the code's known petroleum, never Geology's tables; 12 tests purity on EG1's iron weights (cells with a district point left out: 75 of 3,283 land cells on the fixture, 458 of 41,759 on the full file; the shield/basin ratio 80, the pools' 78.6 at -1.8%, 2 sigma); 13 measures EG1's Z, and replays E3's with E3's weights against a new `E3_Z` table (within 0.0001: the patch moves copper's replay to 0.73226); 15 proves both Earth floors and that a generated save of 40 loads. **EarthFoundingCheck 6's fixture remade** (Jerus, 2026-10-20: a harness that asserted the old deposits' outcome has its fixture remade to cause its condition on the new ones): the nearest oil field a platform can stand in is 55.3 km out with 112 sites (main's 10.1 km, 21 sites), past the 200 purchases the loop was bounded by; it now buys toward the field until the city owns it, bounded by the field's distance in plots (it takes 242, then 197 sea sites, 4 wells, "Platform A: 55 m deep"); every assertion as it was |

## 4. What moved in the trace

Against main at 52f689e (0.8.11), EG1 at 128e032 (the model). Both pairs of runs: "Nothing. Every month passed the audit and every reload matched."

- **The default world: byte-identical**, all eight files. A generated world reads no geology, and its draws are the lines they were.
- **Earth (Rotterdam): identical to month 1,464**, when the city first finds a field to buy.

| | main | EG1 |
|---|---|---|
| people at m1071 / m1465 / m1775 / m2022 / m2500 / m3000 / m3500 | 16,255 / 47,969 / 80,928 / 121,684 / 239,397 / 371,620 / 479,492 | 16,255 / 47,969 / 75,187 / 113,535 / 241,258 / 409,841 / 584,776 |
| the end, m4005 | 588,872 people, GDP $5.7T a month at a price index of 1,300.78 | 711,024 (+20.7%), $6.7T at 1,618.37 (real GDP -5.5%) |
| iron | 209 looks for a field found none listed; never a mine | an iron field bought at m1464 (West 4, 58 sites, 402.8 Mt, US$378M, the city 47,922 people); one Iron Mine, 40 of 58 sites worked by m1534 and 49 by m1775; worked out about m2000 ("the iron ore is worked out"); 80 looks found none listed |
| oil | its first well m3656, 16 wells on 16 sites of its own ground at the end, 1.48 Mt of crude left | never a well: the fields its ground held on main's draw are not on EG1's (the Anglo-Dutch Basin's oil is drawn again, by its known petroleum, in bigger fields) |

**A second Earth run, at Halifax** (`LongPlaytest --earth halifax`, the same seed, 4127), added because Jerus's cities are there. It is identical to main's to month 1,086.

| | main | EG1 |
|---|---|---|
| people at m1071 / m1500 / m2022 / m2500 / m3000 / m3500 | 26,876 / 45,776 / 78,233 / 101,449 / 102,078 / 115,503 | 26,876 / 39,993 / 49,237 / 76,411 / 104,972 / 122,231 |
| the end, m4001 | 179,019 people, $1.2T a month at 1,537.69 | 162,406 (-9.3%), $899.8B at 1,169.52 (real GDP -1.4%) |
| iron | 208 looks found none listed; never a mine | a field bought at m1086 (West 4, 137 sites, 1,897.9 Mt, US$930M, the city 31,872 people); six Iron Mines built |
| oil | never | never |

**Why.** Both Earth cities find iron that main's never listed. The cause is the seed, and EG1 only changed which pattern it lands. Rotterdam's main world has its nearest iron 24 km south, Halifax's 9.6 km west, and none of those fields came into the city's 24 offers. EG1 draws a third as many fields, and outside the shields only one cluster to a pool. Those clusters land a field 5-8 km west of both sites (8.2 km and 58 sites at Rotterdam, 6.4 km and 137 sites at Halifax), and the West 4 offer holds it. **These positions are the seed's, not the geology's** (§8, found on the way): with seed 4127, Paris, Pittsburgh and Berlin have the same three fields at the same offsets, and main does the same at 34.5 km. A year of mining slows each city while it buys and builds: Rotterdam has 75,187 people at m1775 against 80,928, Halifax 49,237 at m2022 against 78,233. Rotterdam's mine works its 402.8 Mt out by about m2000 ("the iron ore is worked out"). Rotterdam's later city has no wells of its own: the oil under its ground on main's draw is not on EG1's, so it imports its crude, and it still grows past main's (584,776 against 479,492 at m3500). Halifax's ends 9.3% smaller. One run swings a long way with the city's path (FR2-notes §4), and the ensemble reads only generated worlds (§6), so these two runs are all the Earth has. The renders `EG1/rotterdam-before-after.png` and `EG1/halifax-before-after.png` show the fields within 350 km on both builds.

## 5. The rules, counted (EarthDepositsCheck)

**Nova Scotia, spec §8's fixture**: the worlds of Jerus's two saves, fields within 50 / 150 / 300 km of the founding site, EG1 against §8.3's:

| | Halifax (44.65 N 63.60 W, seed 758,809,812) | Eastern Shore (45.17 N 61.72 W, seed 284,719,415) |
|---|---|---|
| iron | 0 / 21 / 47 (45 / 417 / 941) | 5 / 22 / 31 (72 / 320 / 699) |
| oil | 0 / 0 / 32, 31 at sea (14 / 155 / 661) | 0 / 21 / 25, all at sea (8 / 182 / 646) |
| oil on land within 150 km | 0 (53) | 0 |
| coal | 0 / 0 / 7 (2 / 11 / 20) | 0 / 0 / 5 (0 / 3 / 15) |
| oil at sea, by province | Scotian Shelf 28, Fundy Basin 3; none in the fold belt's water | Scotian Shelf 25; none in the fold belt's water (§8.3: 7 of the 8 within 50 km lay there) |

The Scotian Shelf's oil multiplier is 0.3178 against its allowance alone of 0.1085: its hand row is two thirds of it. Glace Bay, New Waterford and Sydney Mines read the Sidney Basin (the patch moves the 3 grid cells in its box). Controls, iron within 50 km: Mesabi 925, a Superior Province shield spot outside every district (49.5 N 90.0 W) 126, Tom Price 794; Calgary's oil within 50 km 8 fields, all on land (§8.3: 37-44, before the fields were three times wider). The largest oil multipliers: Ghawar 127.1, Villahermosa 99.5, the Los Angeles Basin 97.5, the Qatar Arch 73.4, the Mesopotamian Foredeep 70.7; the Permian 14.7, the Anglo-Dutch Basin 7.6. 840 province rows stand on the allowance alone, 183 hold known petroleum.

**How often the draw's rules fire** (World's counters, every pool each world drew):

| world | clusters drawn by the points' weights | centres drawn again | centres left on none | fields drawn again | fields left on none |
|---|---|---|---|---|---|
| Rotterdam, the fixture | 529 | 16 | 0 | 78 | 0 |
| Rotterdam, the full file at the playtest's seed | 623 | 32 | 1 | 171 | 5 |
| Halifax, the full file | 91 | 1 | 0 | 35 | 0 |

Within 300 km the harness's own count of fields on ground that holds none is 0 of 3,839, 1 of 3,988 (a stone field) and 0 of 197. **Fewer and bigger**: 221 Earth pools and 222 generated ones hold exactly the fields the pool rule allows; Earth fields run 32 to 886 sites, a mean of 76.28 against 75.72 expected (1.37 standard errors); generated 10 to 774, 25.29 against 25.24.

## 6. The ensemble

**The baseline is FR6's final run (fr6s1), not main rerun** (Jerus, 2026-10-20: main's ensemble is not run again while main's model has not moved; FX4, FR5 and FR4 all shipped with identical traces, so fr6s1 was scored on main's model). The 16-seed x 5-policy ensemble ran once, on EG1's model (the runner FR2 adapted for the PC, six at a time, from a classes snapshot, outside the tree): 80 runs, none missing or short. **All 80 runs are byte-identical to fr6s1's.** The FR2 scorer gives the same score as fr6s1's own, 22/26, with four reds: A2, S1, H2 and R3. EN1's scorer, merged from main during this batch, scored against `docs/ensemble/0.8.9.md` (the stored fr6s1), gives 25/27 as 0.8.9 does, S1 and R3 red (EN1-C). No premise moved, because the ensemble's cities stand on generated worlds, whose draws EG1 leaves to the bit; the growth EG1 changes is the Earth's (§4). By EN1's rule a batch whose default trace is byte-identical skips the ensemble; it ran once here because the orchestrator asked, before that was known. No `docs/ensemble/0.8.12.md` is stored, since its table would be 0.8.9's, and 0.8.9.md stays the next batch's baseline.

## 7. The suite

`Batch check.bat --maps` on the branch with main merged in (ba7ed2c, the model of 128e032), earth-1.bin named: **123 harnesses run, 121 passed, 2 failed**. ScaleCheck's "...and it builds" was red on main already (122 run, 121 passed at 52f689e; queued as SC1). The second was StaleCheck: README's "121 harnesses", one short since EarthDepositsCheck. It was fixed and is green on its own. One timing line was soft beside the other sessions' JVMs: EarthTerrainCheck 9's cells pass at 1,046 ms against its 1,000 ms budget, where the slice measured 406 ms alone (the districts add about 3%). The maps were regenerated. Main's merge brought no source. **`Batch check.bat --timing`**, last and alone (it waited for two other batches' JVMs): 8 harnesses run, 7 passed, ScaleCheck's same line the one red; the cells pass 408 ms hard, against its 1,000 ms budget.

## 8. Not built, and what is left

- **For Jerus (Pending, EG1-C)**: the deposits round one of his cities on his own map. The renders beside these notes are the harness's picture, not the game's.
- **Not built**: undiscovered petroleum (not in the file); MRDS as a baked layer; hand rows for the world's other unassessed provinces (★2); the far views' and the founding card's words naming a district.
- **Found on the way: two Earth worlds of one seed share their fields' pattern.** A world's draws are keyed by its seed and its cells' numbers, and an Earth world puts its spot at the world's centre. So two spots founded with one seed draw the same pattern round their sites wherever their ground is alike. With seed 4127 (`Founding.DEFAULT_WORLD_SEED`, the playtests'), Paris, Pittsburgh, Berlin, Halifax and Rotterdam all have iron fields 5.0, 6.4 and 8.2 km west of the site on EG1, and main's Paris and Pittsburgh both have their nearest at 34.5 km. It predates EG1 (main has it), and the founding screen rolls a new seed every time it opens, so a player meets it only by typing one seed for two cities. Mixing the spot into an Earth world's draws would end it; that moves every Earth field again, and it is its own batch if Jerus wants it.
