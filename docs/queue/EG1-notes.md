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
- **Harnesses**: EarthDepositsCheck (new, §6); EarthTerrainCheck 11-13 and 15 and EarthFoundingCheck 2 and 6 moved onto EG1 (§3).

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
| 3, EarthDepositsCheck | Sonnet | (to come) |
| 4, the Earth checks moved | Sonnet | (to come) |

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

**Why.** Near Rotterdam EG1 has fewer iron fields, but they are bigger and grouped. The one that came within the land office's reach at m1464 was bought, and the city built a mine on it and dug for 36 years. The years with the mine slow the city a little (75,187 against 80,928 at m1775). Without its own wells from m3656, the later city imports its crude and grows past main's (584,776 against 479,492 at m3500). One run swings a long way with the city's path (FR2-notes §4), so the ensemble is the measure, and it reads only generated worlds (§5). The renders `EG1/rotterdam-before-after.png` and `EG1/halifax-before-after.png` show the fields within 350 km on both builds.

## 5. The ensemble

(to come)

## 6. The suite

(to come)

## 7. Not built, and what is left

- **For Jerus (Pending, EG1-C)**: the deposits round one of his cities on his own map. The renders beside these notes are the harness's picture, not the game's.
- **Not built**: undiscovered petroleum (not in the file); MRDS as a baked layer; hand rows for the world's other unassessed provinces (★2); the far views' and the founding card's words naming a district.
