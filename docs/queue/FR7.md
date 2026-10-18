# FR7 - track that goes somewhere, and roads for two needs (2026-10-18)

The brief for batch FR7, the last of the freight batches. `QUEUE.md` links here. Jerus asked for it on 2026-10-18 from his Halifax city at 0.8.3. Three of his screenshots are FX3's (`../FX3/1-north2-boulevards.png`, `2-north4-2300.png`, `3-diagonal-highway.png`), and the far view is `FR7/1-far-dead-end-lines.png`.

His words: "there is a bunch [of rail lines] but its redudant, cause the only two rail terminals are just in one corner bundled together. so why all the other rails they are dead ends ... shouldnt it be sometrhing like needs one terminal and then splurs ... otherwise you have rails that somehow bring trains but no trains"; "too many gravel roads ... advisor chooses [gravel] over [paved] until millions of popuolation and highways nearly never ... i think gravel isnt brown, it should be grey ... i need it so that paved roads and highways late game both get built not only one, cause if we fix it via numbers, then late game only highways get build but we also need regular roads".

He also folded in **FR0-L**, what FR0 did not build (out of Pending, his review of 2026-10-18): access roads, sidings into the bulk works, the interchanges' ground, and kerb corners. That is §3.

**Highway bridges are not here.** They are ET6 (short crossings to 1,020 m, long bridges as orders), and ET4 prices highways and rail by slope.

FR7 comes after FR2, because the access roads need the Truck Depot and the highway's freight need reads Haulage's trucks. It ends the freight batches. It moves traces: §1.1 and §2 change what the city buys.

## 1. Rail: a terminal first, and track only where trains run

### What the game does today (read from the source, 2026-10-18)

- **The three rail buildings are one ladder of capacity.**
  - A Rail Spur (id 62) carries 50,000 t a month on 72 plots.
  - A Freight Line (63) carries 200,000 t on 248 plots.
  - A Rail Terminal (64) carries 500,000 t and lays no track.
  - `Rail.trackTonnes()` (`Rail.java:640`) sums them, and capacity is min(track, wagon sets × 2,500 t) (`Rail.java:525`).
- **Nothing ties one to another.** A spur alone carries its 50,000 t, and a terminal alone carries 500,000 t with no track.
- **The Rail sector buys spurs first.** `Rail.plan()` (`Rail.java:803-871`) orders the best profit per cost among the rungs whose capacity the unserved tonnes fill to `MIN_LINE_UTILISATION` (0.60).
  - That threshold is 30,000 t for a spur and 300,000 t for a terminal. So a growing city buys spurs and lines, and a terminal comes late, if ever.
  - **Suspected, not run:** capacity is limited by wagon sets (`Rail.java:552`). While the sets lag, `plan()` may count tonnes the track could already carry as unserved and order more track. The batch measures this first.
- **The map spends every spur's plots on arms.** Track plots are 72 per spur and 248 per line (`BuildingVisual.java:456-461`).
  - `CityRuns.grow()` (`CityRuns.java:635-675`) lays them as arms from the hub, the mine nearest the founding site.
  - The gate arm grows first, then the opposite arm, then the perpendicular pair, then new corridors 256 plots apart.
  - An arm waits where the city's ground or the sea stops it. That is the dead end.
- **Yards cluster round the hub.** `yardSpots` (`CityRuns.java:952-989`) sorts the yards by distance to the nearest mine, and the hub sits at that mine.
- **Trains run only to yards.** `CityMap.trainRoutes` (`CityMap.java:3227-3249`) makes one route per yard: the world line, the main arm, the hub and the yard. An arm on no yard's way never sees a train, so most of Jerus's track carries nothing on the map.
- **The sidings the study meant spurs to be were never built.** The study says "a Rail Spur is already named for [a siding], and on the map it becomes one" (spec-freight §4.2), into "the largest BULK works within one district of the line" (§4.3). D14 was not built: it was FR0-L.

### What the real thing does (sources at the end)

- **A spur exists because a shipper is on its end.** It is "a relatively short" track that joins one firm to the network [F1].
  - Railways take a spur up once its works closes or its traffic stops [F2].
  - States pay for new spurs only against a promise of carloads [F3].
- **Cars from a siding go to a yard,** where they are sorted into trains for the line [F4].
- **Bulk is different.** A unit train of one commodity loads at the mine's or the plant's own loop and runs straight to where it is going, without a yard [F5].
- **Track is mostly main line.** In 2015 the US Class I railroads had 93,628 miles of road and 160,692 miles of track. The first main track is 58% of the track, a second main track 10%, and yards and sidings about 31% [F6].
  - A mid-sized city has tens of rail-served industries: Cedar Rapids' CRANDIC serves 16 directly, and Tacoma Rail about 60 [F7].

### Proposed

**1.1 The model: a small station first, then spurs for works (★1, ★2; traces move).**

- **A new first rung, the Rail Depot (★1, Jerus 2026-10-18: "a small freight station").** It is a small goods yard: a few sidings, a shed and a team track, handling about 50,000 t a month (est.).
  - It takes the next id in `buildings.json` (139 today). It goes at the end of `BuildingType`, sector Rail, category RAIL.
  - Price it on the rail ladder's per-tonne rule (`BuildingManager.java:1505`, "THE LADDER IS PER TONNE").
  - **Its working name is Rail Depot,** because "the freight station" already names the main yard in the map's code (`CityRuns.freightStation()`, `CityRuns.java:1040`). Jerus may rename it.
- **The railway starts with a station.** `plan()` orders no spur or line until the city has a Rail Depot or a Rail Terminal, standing or on order.
  - The first order is the Rail Depot, when the unserved tonnes reach its threshold.
  - A Rail Terminal follows when the tonnes outgrow the depots, at its own threshold, as now.
  - The real reason for the rule: cars from sidings are sorted at a yard.
- **Tonnes railed are capped by what the stations handle.** Depots plus terminals, as well as by the track and the wagon sets.
  - The exception is bulk loaded at a works' own siding (below), which runs as a unit train and needs no yard.
- **On the map, a Rail Depot is a small yard** in the TRANSPORT class: 2 to 3 sidings and a shed, laid as `yardSpots` lays a terminal. Trains route to it as to a yard.
- **A spur is a siding into one works.** A spur is ordered only for a bulk works within one district of the line that has no siding yet: the largest first, as in spec §4.3.
  - A works that railed nothing would not get one.
  - Tonnes past what the sidings serve go to a Freight Line or a further terminal.
- **The suspected overbuild** above is fixed if it is real: track is never ordered while track already exceeds the tonnes and the wagon sets are the limit.
- **Measure what a young city pays.** The batch counts the rail rungs a young city orders before and after, and what its first station costs over the playtest.
  - The Rail Depot exists so that the first station is not a $1.24bn terminal a young city half uses.

**1.2 The map: track only on a train's way (★3; identical traces).**

- **Track plots are laid in this order:**
  1. The main line, from the gateway to the freight station (the yard nearest the main arm, as FX2 laid it).
  2. One siding for each spur, into its works' estate cell. This is D14: a short spur beside the works' box, with a loading shed.
  3. A branch to a further yard.
  4. A second track on the main line, as each Freight Line adds one.
- **No arm is laid that no train's way uses.** Plots left over are not drawn as track out into the country. They are reported in the legend as the road's surplus is: "N plots of track more than the line uses".
- **A further yard goes beside the largest group of rail-served works** more than one district from a yard, not beside the mine nearest the hub. Two yards bundled together stay only where the works are bundled.
- **Trains run to the sidings too.**
  - A bulk unit train runs from the world line along the main line straight into a works' siding, as real unit trains skip the yard.
  - Mixed and container trains run to the yards, as FR1 draws them.
- **Find which lines in `FR7/1-far-dead-end-lines.png` are track and which are highway.** Jerus reads the dark lines as rail. They are the north-south line at about x = 315 running north to corridor 3, and the east-west lines at about y = 680, 790 and 885 that stop in open country west of the city.
  - If any is a highway arm, the same rule asks whether a highway that serves nothing beyond the city's last ramp should be laid at all. Say what was found.

## 2. Roads: streets and highways as two needs

### What the game does today

- **One pool, one winner.**
  - Gravel, paved and highway capacity sum into one road capacity (`ServicesManager:168, 198`; `BuildAdvice:723`).
  - `BuildAdvice.suggestFor` (`:1102-1183`) ranks them by life cost per trip served: price, plus ground at `landValue()`, plus the running cost over 20 years. The winner gets the whole order.
  - RoadCheck §2 (`RoadCheck.java:356-402`) asserts the strict ladder: gravel below the gravel-to-paved land-price crossover, paved between, and a highway only past the paved-to-highway crossover.
- **So the kinds replace each other; they are never built side by side.**
  - Per trip of capacity, gravel costs $2.58k and 500 sq ft, paved $4.50k and 208 sq ft, and a highway $6.40k and 42 sq ft.
  - Paved overtakes gravel only where the ground is dear: about 10× `LandMarket.BASE_PRICE_PER_SQ_FT`. That price rises only with crowding (`CROWDING_MIDPOINT` 4,000 per km²).
  - The highway's one extra is a freight relief on bulk and goods (`BULK_HIGHWAY_RELIEF` 0.40, `GOODS_HIGHWAY_RELIEF` 0.15). Commuters get none.
  - So a highway wins only at the very top of the land price, after paved already has. The roads agent estimated these crossovers from the json figures; the batch measures them.
- **Gravel's 46 plots a road** (against paved's 26) buy more ground than the streets use. The legend in `../FX3/3-diagonal-highway.png` says "55,611 plots of road more than its streets carry", and in `2-north4-2300.png` "3 buildings packed without a street".
- **Gravel is drawn tan:** `TileRaster.ROAD` gravel `0xffcdb07c` (`TileRaster.java:88`), paved `0xffa4aab0`, highway `0xff3b4048`, and the track's brown dashes `0xff8a6a3c`.

### What the real thing does

- **A grown city has both, because they serve two needs.**
  - Local streets are 67-76% of urban road length and carry 6-24% of the traffic. Interstates and freeways are 1-4% of the length and carry 20-48% [R1].
  - In US urban areas today, freeways are 1.2-4.3% of the road miles and carry 25-50% of the daily vehicle-miles: Boise 25%, Chicago 33%, New York 42%, Los Angeles and Kansas City 50% [R2].
- **They grow from different things.**
  - Street length follows built-up area: road miles per square mile vary only 5.5-12.8 across US urban areas, against 2.4-9.4 per thousand people [R2][R3].
  - Freeways follow the size of the metro and the length of its trips. A city gets its first freeway at about 100,000 people and a ring road at about 1 million, and every city past 10 million has one [R4].
- **Streets are paved by their traffic, not their land price.**
  - Gravel's upkeep rises sharply past about 200 vehicles a day (Minnesota). Paving paid past 150 a day in South Dakota, whose bands are gravel to 150, a seal coat to 660, and asphalt above that [R5].
  - Only 3.8% of US urban road miles are unpaved (2018), against 43% of rural [R6].
  - US cities had paved almost all their streets by 1924 [R7].
- **Gravel is mostly grey.** 70% of US crushed stone is limestone or dolomite, 14% granite and 6% traprock [R8].
  - The tan and brown come from clay binder and from local rock. A surface course is 4-15% fines, "usually natural clays" [R9].

### Proposed

**2.1 Two needs, two measures (★4; traces move).** This is what makes both get built late, where a change of numbers would only move the one winner.

- **Streets: the access need.** Every built plot needs a street to reach it. The need grows with built-up area and the city's short trips.
  - Gravel and paved roads serve it. Their capacity is one pool, as now.
- **Highways: the through need.** The city's long trips (commutes across it) and its freight to and from the gateways, ports, yards and FR2's Truck Depots need highway.
  - The long trips' share grows with the city's extent. Calibrate it so a city of a few hundred thousand puts about a quarter of its vehicle-km on highways, and a metro of millions about 40-50% [R2].
  - Freight's share is Haulage's truck-kilometres (FR2).
  - **Only Elevated Highways serve it.** Through trips with no highway fall back onto the streets and strain them. So a city with no highway still runs, only slower.
  - A highway gives no access: no building fronts it (H5). Its capacity does not count toward the streets' need.
- **The advice measures each need on its own.** Each gets its own strain and its own line on NEEDS YOU (`MAX_SUGGESTIONS` already gives one per need).
  - In a big city both lines come up, month after month, because both needs grow.
  - Within the streets' need, gravel and paved still compete on life cost (2.2).
- **The first highway** comes when the through need first strains, which for the real thing is near 100,000 people. No population gate is written: the need is the gate.
- **RoadCheck §2's premise is retired by Jerus's decision, not moved to let a change through.** Its ladder becomes two assertions.
  - Within the streets, the kind follows the life cost and the load.
  - A city past the through-need threshold builds both highways and streets, and its NEEDS YOU lists both.

**2.2 Paving by traffic (★5; traces move).**

- **Gravel's repairs rise with its load,** from `repairsPerYear` 0.03 at light traffic, steeply past the load equal to about 200 vehicles a day on a street. Minnesota's finding [R5] is the mechanism.
  - The life-cost ranking that exists then picks paved where streets are busy and gravel at the quiet edge. No modifier is needed.
- **The batch sets the load-to-vehicles-a-day scale** from the model's trips per street plot, and writes it down.
- **"Pave your gravel"** (`weighPaving`, `pavingBeatsPaved`) is offered by the same test, district by district, busiest first.
- **On the map,** the surface ladder (roads study §2.5) already paves from the hub outward. Check that it keeps doing so.
- **Check in a played city** that a district in the year 2300 is paved through its middle (Jerus's `2-north4-2300.png` is tan across whole districts). FX3 §5 asks whether those districts really are gravel. FR7 starts from FX3's answer.

**2.3 Gravel drawn grey (★6; identical traces).**

- **Gravel becomes a light warm grey of loose stone,** about `#bdb8ae`, with a fine speckle at L0. Paved stays the smooth `#a4aab0`. The two still read apart by lightness and texture.
- **Tan and brown stay for what is earth:** the track's brown dashes.
- **Check the new grey against its neighbours:** the verge, paved, the ballast (`#6f675c`), and the Farms and Desert legend colours. The legend's swatch follows.

**2.4 The surplus.**

- Measure the legend's "plots of road more than its streets carry" and the buildings packed without a street in Jerus's city before and after.
- Paving where streets are busy should shrink both. If it does not, say why. Do not change the legend's figure to hide it.

## 3. FR0-L, folded in (Jerus, 2026-10-18)

These are what FR0 left, from `FR0-notes.md` §4. FR0 stopped on one problem: the runs are laid city-wide before the district plans, so nothing in the runs knows where a works or a cell's streets stand. **FR7 solves it with a pass after the plans:** the runs first, then the plans, then sidings and access roads routed over what the plans laid. That pass is the map's alone.

- **Sidings into the bulk works (D14).** This is 1.2's second step: the works' place is the plan's, so the siding is laid after it.
- **Access roads (D17).**
  - Every sea terminal, yard and Truck Depot (FR2) gets a street of the new ACCESS role, from its gate to the nearest highway ramp within two districts, or else to the arterial grid.
  - It is paved first from the road plots, and gets a ramp of its own where it meets a highway (spec §4.5).
  - Under 2.1 its traffic counts toward the through need.
- **The interchanges' ground (D19).** A 19 × 19 plot reserve for a cloverleaf and an 11 × 11 one for a trumpet, laid with the corridor before cells open round it. A stack stays only where the ground is short, which is the spec's rule.
- **Kerb corners and estate T's:** drawing only (spec §4.6's table).

## 4. ★ Decisions (answered, Jerus, 2026-10-18)

His words: "1. yes a small freight station? rest rec yes".

1. **★1 How the railway starts: a small station first.** **Decided:** a new small first rung of about 50,000 t a month (1.1, the Rail Depot), then the Rail Terminal as tonnes grow. No spur or line is ordered before a station.
   - The recommendation had been the existing terminal first. He took the small rung instead.
2. **★2 A spur is a siding into one bulk works,** ordered only for a works within a district of the line that has none. **Decided: yes.**
3. **★3 No track is laid on no train's way.** Leftover plots go to the legend's surplus. **Decided: yes.**
4. **★4 Streets and highways as two needs,** each with its own measure and line on NEEDS YOU. Through trips fall back onto the streets when there is no highway. **Decided: yes.**
5. **★5 Gravel's repairs rise with its traffic,** so paving follows load, not land price. **Decided: yes.**
6. **★6 Gravel drawn grey** (about `#bdb8ae`, speckled), with brown kept for earth tracks. **Decided: yes.**

## 5. What done looks like

- **A harness section for each mechanic, with a fixture that causes it** before checking the fix:
  - 1.1: a young city's first rail order is a Rail Depot. No spur or line is ordered before a station, and a spur is ordered only for a works without a siding. The overbuild fixture as well, if it is real. The new building also passes BuildingDataCheck.
  - 1.2: every drawn track plot lies on a train's route, from the world line to a yard or a siding, and no arm ends in country.
  - 2.1: a large city's NEEDS YOU and its orders over a run include both highways and streets, and a city with no highway carries its through trips on the streets at a strain.
  - 2.2: a busy gravel district is paved and a quiet one stays gravel.
  - §3: access roads reach a ramp or an arterial, and interchange reserves stay clear of buildings (spec §7's MapCheck 8 items).
- **Count in a real run** how many highways, paved and gravel roads a long playtest builds, and in which years. Do the same for the rail rungs.
- **Headless renders** of Jerus's city before and after, in `FR7/`: the far view, a near view of a works siding, and a district of grey gravel beside paved.
- **The trace moves.** Re-baseline it, and say by how much the books move.
- **The suite green.** Name what was already red.
- **The record:**
  - the version and its entry;
  - the row out of `QUEUE.md`;
  - notes in `FR7-notes.md`;
  - the write-up in the project, since this changes how the game plays.
- **Jerus's city** is in one of his slots. Find it with `tools.SaveDump`, and read it or copy it to a scratch path. Never write slots 1-9.

## Sources

- [F1] OS MRN code list, rail service values: https://docs.os.uk/os-downloads/products/transport-network-portfolio/os-multi-modal-routing-network/os-mrn-technical-specification/code-lists/railservicevalue
- [F2] California Railroad Commission, Decision 21578 (1929), a spur with no traffic since 1924: https://files.cpuc.ca.gov/LegacyCPUCDecisionsAndResolutions/Decisions/Decisions_D11601_to_D25900/D21578_19290919_A14929.pdf
- [F3] Virginia CTB, Rail Industrial Access policy (Feb 2024): https://ctb.virginia.gov/media/ctb/agendas-and-meeting-minutes/2024/feb/pres/3_RIA-Policy-Update-(February-2024).pdf (the per-project carload minimums came from search summaries and were not opened)
- [F4] "Rail freight transport", Wikipedia: https://en.wikipedia.org/wiki/Rail_freight_transport
- [F5] "Unit train" and "Balloon loop", Wikipedia: https://en.wikipedia.org/wiki/Unit_train, https://en.wikipedia.org/wiki/Balloon_loop
- [F6] AAR, Class I Railroad Statistics (May 2016), re-hosted: https://altex-energy.com/wp-content/uploads/2017/08/Railroad_Statistics_201605_CTM_2015_Highlighted.pdf (the shares are this brief's arithmetic)
- [F7] Iowa DOT on CRANDIC: https://iowadot.gov/media/1852/download; Tacoma Rail, Tidelands: https://www.tacomarail.com/service-area/tidelands-division/
- [R1] FHWA, Highway Functional Classification (2013), §3: https://www.fhwa.dot.gov/planning/processes/statewide/related/highway_functional_classifications/section03.cfm
- [R2] FHWA, Highway Statistics 2023, HM-72: https://www.fhwa.dot.gov/policyinformation/statistics/2023/hm72.cfm (the shares and per-capita figures are arithmetic from the table)
- [R3] Bettencourt (2013), Science, roads grow as about P^0.85: https://www.colorado.edu/socialreactors/sites/default/files/attached-files/bettencourt_2013_science.pdf
- [R4] Taillanter and Barthelemy, "Evolution of road infrastructures in large urban areas" (Phys. Rev. E 2023): https://arxiv.org/html/2205.13194
- [R5] UNH T2, "When to pave a gravel road": https://t2.unh.edu/sites/default/files/media/2022-11/to-pave-or-not-to-pave.pdf; South Dakota, Local Road Surfacing Criteria: https://rosap.ntl.bts.gov/view/dot/17871/dot_17871_DS1.pdf (its bands came from a search summary)
- [R6] FHWA, Highway Statistics 2018, HM-12: https://www.fhwa.dot.gov/policyinformation/statistics/2018/hm12.cfm
- [R7] Rubenstein, "Roads", Dictionary of American History: https://www.encyclopedia.com/science-and-technology/technology/technology-terms-and-concepts/roads; McShane, quoted in the Savannah MPC paper (2014)
- [R8] USGS, Mineral Commodity Summaries 2025, crushed stone: https://pubs.usgs.gov/periodicals/mcs2025/mcs2025-stone-crushed.pdf
- [R9] FHWA and SD LTAP, Gravel Roads: Maintenance and Design Manual (2000), §III: https://19january2021snapshot.epa.gov/sites/static/files/2015-10/documents/2003_07_24_nps_gravelroads_sec3.pdf
