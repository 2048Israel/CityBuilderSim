# The shape of the tree, long form

What each part of `src/main/java/ham/citybuildersim/` is and how it came to
be, one entry per file or family, moved word for word out of `CLAUDE.md` on
2026-10-13 so that every session stops paying for it on every turn. Open it
when you need a part's history; for where a method is, `docs/map/README.md`
and `docs/map/NAME.md` are the index. A batch that adds or reshapes a file
adds its line here, not in `CLAUDE.md`.


    src/main/java/ham/citybuildersim/
        CityBuilderSim.java        launcher (deliberately not an Application subclass; stays here for the jar's main class)
        Game.java                  the month, the seam every system meets at; over 17,000 lines, 45 banner sections
        Motoring.java, LuxuryCounter.java, Offending.java, CityBasket.java
                                   mechanics moved out of Game on 2026-09-18, behaviour unchanged: each is
                                   called from the month and read through Game's delegating getters (the
                                   project's splitting-game.md)
        OldMetal.java              the city's old steel (0.7.123, batch I4): its scrapped cars (Motoring),
                                   demolitions, fabrication offcuts and worn wagon sets, 85% recovered,
                                   kept by seller; Iron & Steel's Scrap Yards buy it at 70% of scrap's
                                   export price, the rest to landfill - saved under one key, MaterialsCheck
                                   12 and MoneyCheck hold it (docs/studies/spec-materials.md 2.9); since 0.7.133
                                   (batch I8) its old copper in a pool of its own beside it (copper():
                                   23 kg a car, 0.1 t a unit of a demolished building's material), which
                                   Copper Refining's Scrap Copper Refineries buy the same way at 70% of
                                   COPPER_SCRAP's export price before they bid for scrap -
                                   CopperRefiningCheck and MoneyCheck hold it
        SimulationEngine.java      the order the month runs in (200 lines - read it whole)
        Sector.java / Sectors.java the template every business extends, and the registry
        sectors/                   twenty-four sector classes, RefineryFlow (0.7.80: the refinery's units, the
                                   flow through them) and SpreadPlanner (0.7.82: what a processing sector
                                   orders - the best earnings on cost at the city's own prices, past the
                                   feed, ground, staff and money gates - and the idle-then-shed rule; the
                                   refinery its first client, the materials chains' works next; since
                                   0.7.105 its prices at the margin, and Refining's run rule beside it: the
                                   crude units run only the barrels that pay; since 0.7.112 the whole cost
                                   tested whoever pays, Jerus's decision A, and outValue/inValue, the margin
                                   split at the month's trade); ProcessingSector and ExtractionSector (0.7.112,
                                   batch I2: the materials chains' two bases - a works built by SpreadPlanner,
                                   with captive use and idle-then-shed, and Mining's rule for a mine, Mining
                                   moved onto it; since 0.7.120, batch I3, Heavy Industry - labelled Iron &
                                   Steel, its arc furnaces on SCRAP - a ProcessingSector, and Mining - Iron
                                   Mining - asked the planner's money gate on the whole cost; since 0.7.123,
                                   batch I4, Iron & Steel's integrated route - the Scrap Yard, pellets, coke
                                   ovens, blast furnaces, the oxygen shop and the large mini-mill - its coke,
                                   pig iron and scrap captive and its works' power its own; since 0.7.124,
                                   batch I5, Iron Mining's four mines by type and size - hematite and
                                   magnetite, one site or twenty of one field - the plan among those that
                                   fit, LandManager.fits(), the dig and the retirement by type; since
                                   0.7.126, batch I6, CoalMining, the eighteenth sector on the end of
                                   Sectors.KEYS - its four buildings, a coking mine's raw coal washed by a
                                   preparation plant in CoalMining.flow(), its stockable coals' dig at the
                                   month's end; since 0.7.131, batch I7, Quarrying and CementConcrete, the
                                   nineteenth and twentieth - quarries on ANY stone packed onto limestone
                                   first, the limestone no buyer takes carried into aggregate, and the
                                   kilns and cement plants on ProcessingSector; since 0.7.133, batch I8,
                                   CopperMining and CopperRefining, the twenty-first and twenty-second -
                                   a sulphide field's concentrate dug as the month makes it and an oxide
                                   field's cathode leached at the mine, and the smelters, refineries and
                                   rod mills on ProcessingSector, the smelter's acid sold to the leach
                                   plants and the city's old copper bought first by the scrap
                                   refineries; Manufacturing's Wire & Cable Works draws their rod;
                                   since 0.7.138, batch I9, Forestry, the twenty-third - crews on the
                                   forest the law lets them cut, no sites, added only while it lasts
                                   120 months of every crew's cut (E), the forest regrowing a 240th of
                                   what was cut and its planted stands' growth, a Tree Plantation or a
                                   Woodlot planted on its zone while its cut outgrows the regrowth, and
                                   the standing wood a building clears sold as its salvage cut,
                                   billed by the builders; since 0.7.140, batch I10, WoodProducts,
                                   the twenty-fourth - sawmills, a panel, pulp, paper and pellet
                                   mill on ProcessingSector, the sawmills' residues and the pulp
                                   mill's pulp to its own mills first, the pulp mill's liquor its
                                   own power and 13,500 kW sold, the paper mill on fuel oil, and
                                   Forestry's logs bought before the world's) - each new sector held
                                   by its own check: CoalMiningCheck, QuarryingCheck and
                                   CementConcreteCheck, CopperMiningCheck and CopperRefiningCheck,
                                   ForestryCheck and WoodProductsCheck;
                                   Mining.java is the shape to copy for a mine (on ExtractionSector
                                   since 0.7.112), CementConcrete.java the shortest of all (0.7.131,
                                   on ProcessingSector) and Materials.java the shortest on Sector;
                                   since 0.8.5 (batch FR2; spec-freight 3.1) Freight in the
                                   railway's slot of Sectors.KEYS, where the Rail sector was - the city's
                                   carrier, its railway the Rail division - and Haulage, its trucks,
                                   the city leg of every bulk tonne billed once (HaulageCheck); since
                                   0.8.6 (batch FR7) the Rail Depot and a station before any track
                                   (RailDepotCheck); since 0.8.7 (batch FR3) PortDivision, its third
                                   division, the crews that work the city's berths and pay it port
                                   dues (LandlordPortCheck); since 0.8.9 (batch FR6) the city's own
                                   bulk and the port yard on the railway (HomeRailCheck)
                                   itself
        BondMarket.java, CorporateBond.java, OrderBook.java, InterimLoan.java
                                   the businesses' bonds (0.7.12): the market and each participant's rule, the
                                   bond, the limit-order book the bonds and (since round 2) the shares trade on
                                   - Exchange is the shares' side - and the loan lent after a default (the
                                   project's the-firms-sell-bonds.md)
        Rollover.java              the treasury's rollover of what falls due next month (0.7.13): the setting,
                                   the ledger of the surplus it has netted and the record; Game's ROLLING WHAT
                                   FALLS DUE reads the city and books the issues (the project's
                                   rolling-what-falls-due.md); since 0.8.17 (batch CD2) its own cap
        Auction.java, AuctionTally.java, CapSetting.java
                                   the city's paper sold at auction (0.8.17, batch CD2): the bidders, each by
                                   its own rule (the households, the firms, the world, the bank), the one
                                   clearing yield, capped or Fill it; the tally of the sales booked (D33);
                                   a cap as the save keeps it (docs/studies/spec-city-debt.md 4; AuctionCheck)
        YieldCurve.java            the city's curve (0.8.15, batch CD1; docs/studies/spec-city-debt.md 3): a
                                   discount factor for every month to fifty years, the forward the dial the
                                   market expects plus the credit spreads, compounded, with the term
                                   premium less the central bank's compression on top - every payment of a
                                   piece discounted at its own month's factor, so a piece's yield is solved
                                   from its price; pure, DebtManager builds it from the city - CurveCheck
                                   holds it
        TreasuryFund.java          the city's fund (0.7.14): its cash, its two dials (what goes in, and since
                                   0.7.48 what it withdraws), its rule's statics, the
                                   rescues and the bank's preferred offer as a record; the books themselves
                                   are the register's and the bonds' city holdings, and Game's THE CITY'S
                                   FUND AND THE BANK'S RESCUE runs it
        FundLedger.java, FundView.java
                                   the fund's cost basis (0.7.39): each holding's adjusted cost base by the
                                   average-cost method, what it realized and paid, and the record of what the
                                   fund did, booked where its holdings move - bookkeeping, saved inside the
                                   fund's state - and the pure door the fund's pages read it all through
                                   (positions, the return, search, a ticket's quote, the record); FundLedgerCheck
                                   holds both (the project's spec-fund-0739.md)
        ConstructionControl.java   the player's hand on the construction queue (0.7.22): the city's order of
                                   its own sites, rushes on overtime, cancels and the shells they leave,
                                   demolitions and buy-outs (since 0.7.106 a demolition recovers
                                   SALVAGE_SHARE, a quarter, of its material), and since 0.7.70 its gravel
                                   roads paved - the state, saved under one key, and each rule's
                                   arithmetic with its source; BuildingManager applies the crews and Game's
                                   THE PLAYER'S HAND ON THE QUEUE moves the money. Since 0.7.129 (batch
                                   T1-B) the model's own rule beside the hand: while anybody lies unburied,
                                   the city's death-care sites head its order and take its share of the
                                   crews first (BuildingManager, DEATH CARE FIRST WHILE THE DEAD WAIT;
                                   DeathCareFirstCheck holds it on a fixture that causes the trap); and
                                   since 0.7.130 (batch T1-C) a depot on site, the builders' own or one
                                   the city bought, that the crews' rule would keep there past the months
                                   the builders expand at, and the city could staff, is built off the top
                                   of the builders' whole month, with that death care beside it at one
                                   priority (BuildingManager, THE BUILDERS' OWN DEPOTS FIRST;
                                   BuildersFirstCheck holds it on a fixture that causes the stall)
        StrategicReserve.java      the city's strategic reserve of crude (0.7.85): what its tanks hold and cost,
                                   the fill ordered and the release standing, the city's side of crude's
                                   clearing (Markets.CityTrader) and the month the next strike settles;
                                   Game's THE STRATEGIC RESERVE pulls the levers and pays - OilCheck 16
                                   holds it (docs/studies/spec-oil.md 2.8)
        FuelSplit.java             FUEL split into the PETROL and DIESEL it became (0.7.76): a save written
                                   before then converted once, on its load, before anything is restored -
                                   every FUEL figure by the drivers' share of the month's litres, the
                                   railway's own all diesel, no money moving; OilCheck 10 holds it
                                   (docs/studies/spec-oil.md 3)
        Ports.java, BoatSchedule.java
                                   the city's sea terminals (0.7.86): each kind of cargo's berths, the share
                                   of its goods they take before or after the railway, the band they narrow
                                   (Ports.factor(), at the railway's step 5) and the month's tonnes by sea,
                                   saved under one key; and the month's ships as a pure function of time -
                                   the calls those tonnes make, each boat's place on its route, a frame's
                                   query - never saved, the map's to draw (since 0.7.97). PortCheck holds
                                   both, MapCheck 9 the boats on the map (docs/studies/spec-oil.md
                                   2.9-2.10); since 0.8.7 (batch FR3) the Bulk Berth, the Handysize and
                                   Supramax classes a berth's ships list admits, and a berth that pays
                                   back on the port dues (berthPaysBack(), payingTerminal()) -
                                   LandlordPortCheck holds them
        DecisionLog.java           what the player decided, and when (0.7.23): every change of a policy and
                                   every spend at scale, recorded where it is applied, held while a city is
                                   founded or loaded, saved under one key; the History chart's flags
        ChartModel.java            a time chart's arithmetic without the toolkit (0.7.23): the window and how
                                   pan, zoom and ranges move it, the year ticks, nice value scales, the
                                   episode lane's rows and the flags, and since 0.7.50 the copy a chart
                                   draws from and the stack's runs; ui/TimeChart.java draws it, ChartCheck
                                   holds it
        CityNeeds.java             NEEDS YOU, measured (0.7.24): everything with a lever against its own line,
                                   in the panel's order - moved out of ui/SummaryScreen whole, so the left
                                   panel, the header's "Needs you" chip and the Build overview read one list;
                                   and SERVED (0.7.41), the one verdict every service gauge is read by -
                                   supply over demand, its lines turned over; since 0.7.106 a care row's
                                   supply the staffed beds less the priced out
        BuildAdvice.java           the Build tab's categories and measures, each measure's figure before and
                                   after an order by the model's own arithmetic, and the rule behind the
                                   overview's WHAT WOULD HELP MOST (0.7.24; since 0.7.51 priced with its
                                   ground and sized to the businesses' projection, since 0.7.70 a road
                                   over its life, since 0.7.71 a care building the size that fits the
                                   need, since 0.7.101 every building its order over its life for what
                                   it will serve - power, water, roads and care as the city grows into
                                   it - and an iron field's pay-back, the test player's rule; since 0.7.106
                                   care's places the beds less the priced out); advice, not a model
                                   change - BuildAdviceCheck, RoadCheck and ChildcareCheck hold it
        AutoBuilder.java           automatic building (0.7.73): the player's switch and two dials (a
                                   spare margin, a debt limit - since 0.7.81 the city's debt over a year
                                   of GDP, since 0.7.101 all it owes over the year it has recorded, 240%
                                   by default and to 600%, and "Build from cash anyway" for when it is
                                   over), and the month's pass that orders the build advice's own cards
                                   for the city's works within the builders and the limit (a first of a
                                   kind from the cash alone) - since 0.7.104 power, water, care and death
                                   care whatever they cost to run, a school, station, prison or line only
                                   as far as it pays back over its life (the budget until then) - since
                                   0.7.77 buying the bare ground they lack as Build's land shortcut would,
                                   since 0.7.101 only an order's own, and the next card in the advice's
                                   ranking where the money, the ground or the payback will not run to the
                                   first - borrowing on the funding page's bond; its log, its inbox
                                   notices - AutoBuildCheck holds it
        BuildCard.java             one build card's figures for all 143 buildings (0.7.25): what it gives the
                                   city and in what unit, its money and scarce-resource bars, the group it is
                                   compared within and its tags, the investors' word and the first gate it
                                   fails for them (since 0.7.75 every gate, with what it would earn, cost and
                                   take to pay back: appraise(); since 0.7.122 the last of them consider()'s
                                   brake, the interest on its whole cost - INTEREST, through
                                   Game.interestTest() - a landlord's home not asked it), the verdict on an
                                   order; since 0.7.142 (batch I13) a materials building's own lines - where
                                   it stands on its typed field, how its sector's planner weighs it on its
                                   whole cost with its lorries (the investors' "this one:" its gate), its
                                   own power, the old metal a yard buys, who here buys what it makes, a
                                   crew's E - and the nine industries' pages' chain (chainLines()); pure -
                                   BuildCardCheck holds it
        SectorFlow.java            one business's month as a flow (0.7.30): each good in and out, its units off
                                   the production rows and its money off the statement, the plant's six
                                   throttles and the rate they multiply to; what the Sectors screen's
                                   Operations page draws; pure - SectorFlowCheck holds it
        RefineryView.java          the refinery's month as a picture (0.7.95): the crude and where it was bought,
                                   the column's cuts, each unit's run, spread and gate, the products and who took
                                   them, the flow traced cut to unit to product, and the pictogram's layout to
                                   scale with its words and colours; what Refining's Operations page paints;
                                   pure - RefineryViewCheck holds it (docs/studies/spec-oil.md 2.12)
        OilView.java               the oil industry on one page (0.7.96): the wells by kind and what they would
                                   lift over ten years if nothing new were built, each pool within its oil;
                                   every kind of refinery unit with its spread and the gate that stops one
                                   more; every product's price and month off the refinery picture's; the
                                   strategic reserve and its levers' reach; the chart and every word; what
                                   Oil's Operations page paints - pure, OilViewCheck holds it
                                   (docs/studies/spec-oil.md 2.13)
        IndustryView.java          each materials industry's month as a picture (0.7.141, batch I11): for the
                                   nine - Iron Mining to Wood Products - the fields by type as vessels filled to
                                   what remains, or the forest; each feed by where it came from (its own works,
                                   each seller here off the month's fills, the old metal, the world); each kind
                                   of building with its run, the last one's earnings as the idle book counts
                                   them and one more weighed at every gate in SpreadPlanner's words
                                   (ProcessingSector.appraise(), ExtractionSector.appraise()); each product
                                   made, kept, sold by buyer, carried and shipped; the power made, drawn and
                                   sold; Forestry's settling point and planting line; its words, colours and a
                                   roof per industry - pure, IndustryViewCheck holds it (every good balancing
                                   against its warehouse's move); IndustryPicture lays it out and
                                   ui/SectorScreen draws it (docs/studies/spec-materials.md 2.13)
        IndustryPicture.java       the nine's pictures laid out to scale (0.7.143, batch I12): IndustryView's
                                   month traced end to end (trace(): each good split over the kinds that made
                                   and used it) and laid out as RefineryView's picture is - a mine's fields as
                                   vessels filled to what is left, or a works' feeds by origin, the imports
                                   hatched; the works in ranks; the tank under the industry's roof (pit, camp,
                                   furnace, kiln, mill); the takers; one scale, in tonnes (the forest's m3) -
                                   and the strip of cells under it; pure, ui/SectorScreen's OPERATIONS paints
                                   it with the refinery's painter, IndustryViewCheck 12 holds it
        FreightView.java, TradeBand.java
                                   the freight screens' figures (0.8.11, batch FR4; spec-freight 6): the
                                   Freight sector's divisions as its page's strip (All, Haulage, Rail,
                                   Ports) and Infrastructure › Freight and › Ports draw them - the month's
                                   tonnes by leg and mode, each division's fleet against its need, its
                                   quote and its bill - and a works' own strip of how its tonnes move; and
                                   the Trade tab's freight band split into its legs (the world's margin,
                                   the long haul by mode, the quay, the city leg), a good's gap a unit and
                                   the whole over the month's trade; pure, every figure a model getter's -
                                   FreightViewCheck and TradeBandCheck hold them
        SectorStatements.java      one business's month as formal statements (0.7.74): profit or loss through
                                   gross and operating profit, the classified sheet, the cash flow in three
                                   sections, the changes in equity (since 0.7.75 in columns, share capital and
                                   what it kept) and the debt schedule by kind - and the bank's - each a list of
                                   rows, its format by Sector.statementFormat(); every bottom line the model's
                                   own (since 0.7.102 the sheet read at the month's close, and what borrowing
                                   cost up front expensed over each debt's life); pure - ui/StatementView
                                   draws it, SectorStatementCheck holds it
        PolicyPreview.java         what a staged set of the Policy tab's dials would do (0.7.36): the tax take
                                   under another policy line by line, and THE BUDGET before and after, each
                                   line its owner's read of a detached TaxPolicy.copy() - since 0.7.48
                                   the fund's withdrawal, and since 0.7.52 the rule at each step of its
                                   strictness (CentralBankCheck 21); advice, not a model change -
                                   PolicyPreviewCheck holds it
        Expectations.java          the anchor (0.7.42): expected inflation, the central bank's credibility and
                                   the expected price level every money constant that prices something is
                                   struck at, at the top of every month (Game.restrikeMoneyConstants());
                                   ExpectationsCheck holds it (docs/studies/spec-inflation.md)
        SupplierCredit.java        the grocers' trade credit (0.7.44): what the shops owe their suppliers for
                                   stock bought on credit when the till could not pay, struck at the clearing
                                   and repaid at the next strike out of the sale it stocked, on both sides'
                                   books, the world's share in the money audit; SupplierCreditCheck holds it
        World.java, Resource.java, Deposit.java, CityLand.java, LandConversion.java
                                   the world a city stands on (0.7.56: one seed's coast, lakes, river and
                                   fields of seven resources; since 0.7.99 a tenth as many fields, each ten
                                   times bigger, in clusters, and no iron asked of a founding) and the
                                   city's land on it (0.7.57; since 0.7.67 whole blocks of a grid, six
                                   offers a side; an older save converted once, and since 0.7.99 the ground
                                   it brings holding the old world's fields; since 0.7.106 a new city's
                                   centre its rings and its founding lake's blocks, one water plant's fresh
                                   water; since 0.7.110 each field of a type, Deposit.type() and
                                   Resource.typesOf(); since 0.7.115 a world's key, World.Key: the
                                   generated world a seed makes, or the real Earth round a spot; since
                                   0.7.127 seven kinds of ground, ICE and DESERT after the five, and one
                                   rule for dry ground, World.dry(), that every test of it asks; since
                                   0.7.142 an offer's fields by type, CityLand.fieldsOnByType(), the land
                                   office's hover) - LandManager and LandMarket sell it, WorldCheck and
                                   LandCheck hold it
                                   (docs/studies/spec-land.md and spec-grid.md)
        EarthProj.java, EarthData.java, EarthTerrain.java, Geology.java
                                   the real Earth for "the world as planet Earth" (0.7.111, batch E1;
                                   docs/studies/spec-earth.md): Lambert's azimuthal equal-area projection
                                   centred on a city's spot (forward, inverse, plots in metres, a cell's
                                   area on the planet, a tile's corners blended), and the reader of
                                   earth-1.bin (its layers, 1-degree tiles inflated into an LRU, shares,
                                   rivers, small lakes, geology, places, the file's identity), ported from
                                   tools/earth's earthread.py - EarthCheck holds both, on a fixture and on
                                   the full file; and since 0.7.115 (batch E2) an Earth world's ground,
                                   spec-earth 3's rule: a tile, line, plot or region drawn from the data's
                                   shares, rivers and small lakes with the seed's detail below them, the
                                   cells pass, the founding snap - World reaches it through an Earth key,
                                   which only harnesses made until E4 (0.7.121); EarthTerrainCheck holds
                                   it; and since 0.7.117 (batch E3) its deposits on real geology: Geology
                                   classes each USGS province (the rules' class, and a hand-checked table
                                   over them), holds spec-earth 4's weights, the Z that keeps the land's
                                   count of fields today's and the types' odds; an Earth cell's fields are
                                   drawn on its land times its provinces' weights, oil's with its
                                   petroleum and its offshore sea, and a field's type leans on the class
                                   under it (EarthTerrainCheck 10 to 14); and since 0.7.119 (batch E5) the
                                   map on Earth: a far node drawn from the data
                                   (EarthTerrain.farTerrain(): the rivers stroked from their vectors at
                                   least a pixel wide, thinned by width as the view widens, the small
                                   lakes from their points, the forest dithered, the void past the
                                   planet's rim; MapTiles.nodeTerrain() by key) and the hover card's
                                   nearest place and geologic province (LandMap.earthWords()) - MapCheck
                                   10; and since 0.7.121 (batch E4) the sea's depth there, the generated
                                   world's rule with the distance from the coast for the elevation earth-1
                                   does not have (EarthTerrain.SHELF_WIDTH_M, measured on the data);
                                   and since 0.7.127 (batch E7, docs/studies/spec-globe.md 3.9) ice and
                                   desert drawn from land cover's two bits, each its half-contour frayed
                                   by its own noise - ice never dry, the desert dry as grass is, each
                                   with its colour, name and legend entry (EarthTerrainCheck 15); and
                                   since 0.7.132 (batch E8, docs/studies/spec-globe.md 3.5) the
                                   orthographic projection round a view centre with a roll
                                   (EarthProj.Ortho) and the far rule through a camera
                                   (EarthTerrain.Camera): the city's plane, the far nodes byte for byte
                                   as before; a latitude-longitude grid, the planet's picture's; the
                                   globe's, its forest dithered from a fixed latitude-longitude grid
                                   (GlobeCheck); and since 0.8.12 (batch EG1, docs/studies/
                                   spec-earth2-terrain.md 8.5) oil and iron where the geology says: oil's
                                   weight a province's known petroleum a km2 against the Earth's mean
                                   (with an allowance where none is known, and a hand table for the
                                   provinces the USGS left at nought), iron's a shield's, a tenth of its
                                   class's elsewhere and twenty in a recorded iron district
                                   (Geology.multiplierAt()), the Sydney coalfield patched into its basin,
                                   an Earth field wider and fewer, a cluster kept on its province's ground;
                                   a generated world as before (EarthDepositsCheck)
        EarthSite.java             founding a city on the real Earth (0.7.121, batch E4; spec-earth.md 2
                                   and 6): whether the game has the Earth's data (gameData(), the file at
                                   EarthData.DATA_FILE, which is not in git - data/ is ignored; since
                                   f99d62c the release earth-1's asset, tools/earth/get-earth.bat fetching
                                   it - and the game runs without it), the place search, Random spot and
                                   the founding card - the snap's site, the ground within 5 km on the
                                   world's plots, the province's multipliers, the nearest iron, the
                                   refusals (the sea, ice) and warnings (a desert, no fresh water near);
                                   and why an Earth save cannot be loaded here. ui/FoundingScreen draws it
                                   (Earth | Generated, the globe, the card), Founding carries the spot,
                                   DataSave its four fields (SAVE_FORMAT 38) - EarthFoundingCheck holds it;
                                   since 0.7.127 the card's ice is the site's plot's class, and an Earth
                                   save from before ice (format 39, ICE_FORMAT) is refused
        WorldData.java             the nations' world as data (batch WN0, 2026-10-28; docs/studies/
                                   spec-nations.md 3.3): the reader of world-1.bin, the earth file's
                                   container under the magic "WORLD-1
" and its own header and index -
                                   the 196 nations (the 193 UN members, TWN, HKG, MAC) and the areas no
                                   nation holds, their money histories and monthly rates, the paths
                                   2000-2100 and each good's price path, the goods and the base year's
                                   trade by pair with make and use and the kernel's cost and pref, the
                                   freight table and the edges it is the closure of (closure()), the
                                   owner raster a 15" cell (the 1-degree tiles inflated into a small
                                   LRU) and Natural Earth's boundary lines, with the file's identity
                                   (name:SHA-256) and numbers 1-based as in the file; the kind 2 layers
                                   are read whole when it opens. locate() finds the file by -Dcbs.world
                                   or data/world-1.bin, which is not in git - tools/earth/get-earth.bat
                                   with "world-1" fetches it from the release world-1. Nothing in the
                                   model calls it yet
        WorldDataCheck.java        WorldData held to tools/world's worldread.py figure for figure on a
                                   fixture (the twelve nations and the Rotterdam window, WN0) and, when
                                   the file is named, on world-1.bin; and the file held to FORMAT.txt's
                                   promises - the 196 against the UN's list, make and use closing, the
                                   freight triangle rule, the kernel solved from a = b = 1 giving BACI's
                                   flows, a nation for every land cell of the earth file inside a border
                                   tile, the moneys and paths whole
        Nations.java               the nations beyond the city on the world's path (batch WN1,
                                   2026-10-28; docs/studies/spec-nations.md 7): each nation's year -
                                   output, people, prices, its money's rate - and each good's real price,
                                   a pure function of the path (Historical or Random, Path), the seed and
                                   the world file. Historical replays the file to 2025, then the game's
                                   rule with half-size seeded shocks; Random draws its own from 2000: a
                                   trend that converges on the US, world, region and own shocks, crises
                                   at Barro's rate, prices by income group, moneys by relative parity or
                                   their peg. Founding carries the path and seed, DataSave saves them
                                   (worldPath, pathSeed), ui/FoundingScreen picks them; Game.getNations()
                                   builds it lazily, and WN3's WorldMarket reads it every January
        NationsCheck.java          Nations held to its dials: one seed one path, Historical equal to the
                                   file to 2025, Random's swings and crises against their dials over a
                                   thousand seeded years, goods' swings, pegs held, output never negative,
                                   the ease past 2100 and a missing figure filled (WN1)
        WorldMarket.java           the world market (batch WN3; docs/studies/spec-nations.md 4): each traded
                                   good's mill price in each of the 196 nations on a smooth spatial
                                   equilibrium (a CES gravity model on the base year's flows, exact hat
                                   algebra), solved every January from the nations' growth and the good's
                                   real price path (Anderson-accelerated, warm), and the city's band in it
                                   - the import ceiling it pays, the export floor it nets - from its site's
                                   freight to the nations, the sale fill over the binding suppliers (each at
                                   its best buyer's netback, its sales' own slope over its displacement
                                   share, capped at what that buyer uses) and the purchase fill from the
                                   cheapest landed sellers (capped at what they make), and the world's
                                   answer to its tonnes. Base is
                                   the file's, once a process (pairs, freight, the calibration that keeps
                                   the literals the prices at Halifax in 2025); WorldMarket is one city's.
                                   Game's THE WORLD MARKET solves it every January and strikes its band
                                   into each traded good's GoodsMarket every month (WN3)
        WorldMarketCheck.java      WorldMarket held to its equations: every maker clears and the level
                                   meets the path (recomputed from the file's pairs), the reference city
                                   sees Good's literals to 1e-9, the floor never above the ceiling in every
                                   good, year, path and site, a small city a price-taker and a big one not,
                                   the month's step against a re-solve with the city's tonnes in, seeds
                                   repeat to the bit, the save round trip, the stand-in, the timings (WN3)
        WorldMeasures.java         the Rankings' real-world figures (batch LB1, 2026-10-30; docs/studies/
                                   spec-leaderboards.md 6-8): the reader of the resource
                                   world/world-2025.json, tools/world/measures.py's file - the 196 nations
                                   and 62 city regions, the 70 measures, each place's latest figure of each
                                   with its year, mark and source (an estimate with its rule), the sources'
                                   licences, the two city boundaries and the CPI-U of 2015-2025 that turns a
                                   real US$ into a 2015 one. A figure the file lacks is absent (null, NaN),
                                   never 0; the format is tools/world/FORMAT-2025.txt. Rankings' Real
                                   world 2025 board reads it (LB2); the game fetches nothing
        WorldMeasuresCheck.java    WorldMeasures held to FORMAT-2025.txt: problems() is the file's rules in
                                   one method (every nation of world-1.bin once, a missing figure absent and
                                   never 0, every mark, source and rule resolving, a licence on every source,
                                   a region's shares under 1, the 2015-over-2025 ratio the CPI-U's to the
                                   last bit), the real file has none and a copy of it changed in one place
                                   has exactly that one, and the money figures agree with each other (LB1)
        CityMeasures.java          the city's figure for 51 of the Rankings' 70 measures (batch LB2,
                                   2026-10-31; spec-leaderboards 6): a pure function of a HistorySave and
                                   three figures it does not keep (the land, the age bands, the wage floor),
                                   money in whole units of the city's money (the model's thousands times
                                   THOUSANDS), a flow a year, each absent measure's reason in the javadoc;
                                   the life table is here. LB3 runs it on a saved city's history file
        Rankings.java              the Rankings board as the screen draws it (batch LB2; spec-leaderboards
                                   3, 4, 9): one pivot into US$ of 2015, then the display in the money
                                   picked (D1, D29), at this month's rate, constant 2015 or PPP (D14); Real
                                   world 2025 from WorldMeasures, Your world from Nations at the city's year
                                   on D15's ten with the city regions by share; ties share a rank, lower is
                                   better ranks ascending, no row for no figure; the lock, the sentence and
                                   the drawer. Pure; ui/RankingsScreen paints it
        RankingsCheck.java         Rankings and CityMeasures held to the study's identities: the city's own
                                   figure comes back in its own money, Money never reorders a board, real
                                   US$ is game US$ at founding, Constant holds a steady real figure, Your
                                   world's rows are Nations', the real rows' pivot, ties, lower-is-better,
                                   no figure no row, the city's figures against the history's own readers (LB2)
        PartnerTrade.java          the city's trade by partner (batch WN3; spec-nations 6 and 8): the
                                   month's and the year's trade with each nation and by good, split from the
                                   month's own fills by WorldMarket.allocate() (Game, tallyThePartners()),
                                   the finished years' tables for the history, an immutable snapshot
                                   published through a volatile for the screens (it is TradeLanes.Feed),
                                   and the save's State
        TradeLanes.java            the city's trade as routes on the map, without the toolkit (batch WN3,
                                   slice C; docs/studies/spec-nations.md 8, board 1): the partners over
                                   MIN_LANE as a tree of great-circle lanes from the city, a partner joining
                                   the nearest vertex of a nearer one's lane within BUNDLE_DETOUR of its
                                   direct way, each edge kept once with its sums and widths; built once
                                   a month, culled to the view, projected from unit vectors and cut as one
                                   run list a width (NationsMap.Cut), timed (LANE_FRAME_MS); the card's
                                   trade rows (NationsMap.card) and a generated world's partners as
                                   arrows at the edge (Arrows); Feed is what ui/MapView is given. Tested
                                   by GlobeCheck 13 and 14, beside NationsMap (WN2) which cuts its borders
        WorldMoneys.java           the world's moneys against the US$, a month at a time (batch WN4;
                                   docs/studies/spec-exchange-rates.md 3.7, XR3 folded in): each money's
                                   PPP path, the dollar cycle, the risk-off, its nation's terms of trade from
                                   the January solve, its own swing; pegs held and soft ones broken by
                                   ForeignAccounts.hazard(); Historical the file's monthly rates to 2025;
                                   every draw a pure function of (seed, money, month); saved
                                   (DataSave.worldMoneys). Game's THE WORLD'S MONEYS IN THE MONTH steps it
                                   with the market and hands the D$ its world (ForeignAccounts, THE WORLD
                                   REACHES THE D$), the vault its cross rates (THE VAULT IN EIGHT) and the
                                   dollar the US's price path (WorldEconomy, THE US DOLLAR ON THE US'S PATH)
        WorldMoneysCheck.java      WorldMoneys held to its dials and the file: one seed one path and a
                                   reload continues it, Historical the file to the month, each swing's sd
                                   and persistence over 1,000 paths, pegs and breaks against the hazard,
                                   a risk-off by its loadings, the terms of trade a third a month, cross
                                   rates, parity, and a city on the file in step and reloaded (WN4)
        VaultMoneysCheck.java      the vault in eight (WN4): all US$ the old vault to the bit at every site,
                                   a buy by the mix, a sale from the largest, the cross revaluation, the
                                   trade mix's mapping through the pegs, the save
        RealDollar.java            the real US$ of 2015 (WN4, D29; spec-exchange-rates 3.9): the D$ per 2015
                                   US$, real GDP in 2015 US$ and the real effective rate, pure functions
                                   behind Game's three getters; nothing in the model reads them (WN6 shows)
        RealDollarCheck.java       the dollar on the US's path to 1e-12, the walk with no world file, the
                                   import shock, and RealDollar's three figures on fixtures (WN4)
        GlobeFrame.java, GlobePicture.java
                                   the Earth's map as a globe, its model half (0.7.132, batch E8;
                                   docs/studies/spec-globe.md 3): the view - its centre in latitude and
                                   longitude, its km a pixel and its roll; zoom at the pointer, a drag,
                                   Fit; the tiers PICTURE, GROUND and PLANE; the handoff to the city's
                                   plane at HANDOFF_PLOTS, close views within REACH_KM, the roll eased
                                   to the plane's up over ROLL_NOTCHES - and the planet's picture, a
                                   class a texel at 4' made by the far rule through the grid's camera,
                                   12' first, never saved, and a frame from it in bands of rows a core;
                                   pure; since 0.7.135 (batch E9) on screen: ui/MapView's expanded map
                                   on Earth and ui/FoundingScreen's globe draw by them - W, A, S and D
                                   turning it (keyPan()), the GROUND tier near the city the plane's own
                                   far nodes through the camera (ground(), out to planeDrawsKm()) and
                                   the far rule through it past that, the last picture carried (Carry),
                                   the swap crossfaded (crossfade()), the roll eased past the reach,
                                   every frame's bands on one pool (GlobePicture.bands()), the founding
                                   globe's view() and its half-pixel handoff - GlobeCheck and MapCheck
                                   13 hold them
        LandManager.java, LandMarket.java
                                   the city's land: what it owns, what is built on and what it sells - the
                                   ore and oil its ground holds less what is taken out, since 0.7.93 the
                                   oil in two pools, the ground's and the sea's (THE TWO OIL POOLS), and
                                   since 0.7.106 the land a new city is founded with (foundingLand()),
                                   since 0.7.107 the zones and the forest law (ZONES AND THE FOREST LAW),
                                   since 0.7.110 the ground by type (THE GROUND BY TYPE) and since 0.7.138
                                   the salvage cut a clearing leaves Forestry and its planted stands'
                                   growth (THE PLANTED STANDS) - and the land office's window, six offers
                                   a side, an offer's forest priced since 0.7.138; LandCheck holds them
        Zoning.java                zoning and the forest law (0.7.107, batch Z1): zones by sector, each a
                                   share of the dry ground or km2 at a share of the going price, and the
                                   law's two shares of the forest; LandManager's ZONES AND THE FOREST LAW
                                   works out what each zone holds, clears the forest built on and sells,
                                   assesses and buys back zoned ground at the zone's price; the zone's
                                   taxes are the sector's own offsets (Game.setZoneTax()); ZoningCheck
                                   holds it (docs/studies/spec-zoning.md and spec-materials.md 2.15)
        ZoningView.java            Policy › Zoning as figures (0.7.109, batch Z2): the hub's four (zoned,
                                   held, the price given up, the forest kept), the dry ground as one bar
                                   cut so its parts add up to it, a zone's row, a sector's page and the
                                   forest law's before and after a staged copy - every figure a model
                                   read; pure, ui/ZoningScreen paints it, ZoningViewCheck holds it
        LandGrid.java, GridOffers.java, LegacyLand.java, GridConversion.java
                                   the block grid (0.7.65 to 0.7.67): the ground owned as a quadtree of
                                   blocks lined up with the world, the six places a side and their
                                   rectangles, spec-land's lanes kept read-only, and a saved city's ground
                                   put on the grid once at load - GridCheck and ConversionCheck hold them
        CityMap.java, TilePainter.java, TileRaster.java, BuildingVisual.java, DistrictPicture.java
                                   the city map (0.7.60): the buildings by type in 7.68 km districts,
                                   each drawn once on its own land; since 0.7.88 a tile painted from
                                   its district's street plan, what a plan cannot hold carried to the
                                   next district and the rest packed at the city's edge (since 0.7.89
                                   within bands of CHAIN_BAND districts), and rastered when a screen
                                   asks; since 0.7.128 (batch MM1) each district's 32 x 32 picture,
                                   a pixel for 8 x 8 plots picked by a fixed hash, drawn with its plan
                                   and kept by its plots - what the far nodes draw from (MapTiles), a
                                   district under 4 of their pixels dithered by its shares, the runs
                                   and the city's edge a pixel wide at every scale; the sidecar
                                   beside the save (no picture in it); since 0.8.0 (batch I14) the
                                   materials drawn as themselves - every type of iron, oil, coal,
                                   stone and copper on its own sites by type of field, a works on
                                   as many as it takes (a Large Open-Pit Mine on twenty), each kind
                                   its look (BuildingVisual.LOOKS: pits, shafts, quarries, a leach
                                   plant, the wash plant, camps, planted forest), each processing
                                   industry's works one campus as the refinery's (GROUPS), and the
                                   zones tinted by district (CityMap.zoneTint(), ui/MapView), the
                                   sidecar FORMAT 7; since 0.8.1 (batch FR0, spec-freight 4) the
                                   network as itself - rail as ballast, sleepers and rails, double on
                                   the main line, level crossings and arterials bridging it; yards as
                                   fans of sidings with a shed, ore or boxes and wagons (the eleventh
                                   class, TRANSPORT); junctions by rule (signals, roundabouts,
                                   diamonds, a highway's end, stacks); the world line past the
                                   gateway and the far view's skeleton - the sidecar FORMAT 8;
                                   since 0.8.4 (batch FX3) a yard's fan on its line's longest clear
                                   stretch, one layout the drawing and the trains both read
                                   (TileRaster.yardLayout()); since 0.8.6 (batch FR7) the interchanges'
                                   ground (InterchangeGround), access roads from every yard, sea
                                   terminal and Truck Depot, and gravel drawn grey; since 0.8.8 (batch
                                   FR5) the trucks' routes (CityMap.truckRoutes(), accessRoutesIn());
                                   since 0.8.14 (batch CL3) each district's log of its count changes month
                                   by month, kept in the sidecar (FORMAT 9), its plan placing them in the
                                   order they came, and since 0.8.16 (batch FX6) its ground dated from the
                                   land's own records, so nothing that stands moves; since 0.8.18 (batch
                                   FX5) the railway's corners and the yards' ladders as curves the trains
                                   run along (TrackCurves), trains their real length, yards as long as a
                                   unit train along their line and drawn whole across tiles, one junction a
                                   crossing, and "Unmade street" for the legend's track (FORMAT 10); since
                                   0.8.20 (batch CL4) the four avenues and the curved streets drawn as
                                   smooth bands (TileRaster's THE AVENUES, THE CURVES); since 0.8.24
                                   (batch FX7, THE RUNS DATED) each district's journal of the plots whose
                                   kind of fixed ground (highway, track, yard) a month changed, handed to
                                   its plan and kept in the sidecar (FORMAT 11) - MapCheck 2d; since 0.8.27
                                   (batch UP3) the minimap's far node kept as three layers, land, districts
                                   and runs, each redrawn only when its key moved (MapTiles.KeptNode), and
                                   what any far node is drawn from hashed (MapTiles.nodeKey()) - MapCheck
                                   18; nothing in the model reads it - MapCheck holds it (15 the
                                   materials), RailDrawCheck and JunctionCheck the drawing
        MiniMapFrame.java          the minimap's arithmetic (0.7.134, batch MM2; docs/studies/spec-globe.md
                                   8.3): what the stamp at the rail's foot and its hover preview frame -
                                   the ground the city owns in its districts with buildings, its centre
                                   and MARGIN round them, owned ground with nothing built on it left out
                                   - at the least whole number of plots a pixel that holds it, its pixels
                                   the far nodes (MapTiles.nodePixels()) from the district pictures, and
                                   what a click opens the expanded map on; pure - ui/MiniMap draws it,
                                   MapCheck 12 holds it
        CityRuns.java              the city's highways and railway (0.7.89): laid city-wide, month by
                                   month, on corridors from the founding site's lines - straight by
                                   preference, never moved, the newest end taken first - with the
                                   rail yards near the mines; kept in the map's sidecar, each
                                   district's plan drawn round them (spec-roads-and-ports.md 2.7,
                                   2.8) - MapCheck 8 holds it; since 0.8.1 (batch FR0,
                                   docs/studies/spec-freight-and-infrastructure.md 4) each net's spine
                                   laid toward the gateway, its gateway arm first, the bearing in the
                                   sidecar (FORMAT 8), and the main line from the gateway to the
                                   freight station (the yard nearest it), double with a Freight Line -
                                   GatewayCheck and MainLineCheck hold them; since 0.8.18 (batch FX5) each
                                   yard laid along its line as long as a unit train where the ground allows
                                   (YARD_LONG), across a cell's ring street, and a station alone laying its
                                   main line (MainLineCheck); since 0.8.24 (batch FX7) it tells a witness of
                                   each district a month's layTo() is about to change, so the map can date
                                   the runs; since 0.8.30 (batch CL5) a stuck highway net opens its next
                                   corridor by a link that searches its way over the dry ground, and every
                                   piece of ground the water parts that holds buildings grows its own net
                                   (CityMap's pieces), the sidecar FORMAT 12 - IslandNetCheck holds it;
                                   (batch ET6) a long bridge the model builds is handed to the map
                                   (CityMap.bridgePicture()) and laid whole as a fixed path of its kind -
                                   the highways', the railway's, or a road bridge's ground kept clear as a
                                   reserve - never grown, taken back or moved, and no part of a net's
                                   plots - BridgeMapCheck holds it
        Gateways.java              the land's offing (0.8.1, batch FR0; spec-freight 4.1): the bearing
                                   with the longest run of land from the founding site, ties by its
                                   hash; on the Earth toward the nearest larger place along land; none
                                   for an island founding - found once by the map, kept in its runs;
                                   pure - GatewayCheck holds it
        DistrictPlan.java          a district's street plan (0.7.87): the cells it opens and their
                                   layouts, every street with its kind and width - the model's road
                                   drawn as the streets' surface - and every building's box; pure, a
                                   port of the study's roads prototype (docs/studies/spec-roads-and-ports.md 2);
                                   the painter draws from it since 0.7.88; since 0.7.90 an estate
                                   cell's streets sized to what opens it, since 0.7.92 the outer
                                   kinds on industry's leftover ground when no cell is left, since
                                   0.7.97 the refinery's units first of industry in touching cells (one
                                   campus); since 0.8.13 (batch CL2) a homes cell's streets one of three
                                   layouts by a hash of its place (the comb, dropped crossings, brick);
                                   since 0.8.14 (batch CL3) a district with a log planned in the order its
                                   buildings came (THE ORDER OF ARRIVAL), so what is placed never moves,
                                   and since 0.8.16 (batch FX6) on its ground as bought, month by month
                                   (THE GROUND BOUGHT); since 0.8.20 (batch CL4) four avenues at 45 degrees
                                   from the founding site, laid as each cell opens, and the curves - far
                                   crescents and the streets along a cut, drawn smooth (StreetCurves);
                                   since 0.8.24 (batch FX7, THE RUNS DATED) its history replayed on the runs
                                   of each month, not today's, so a box under a run laid later stays and
                                   ground a run leaves is room again (PlanCheck 15) - PlanCheck holds it
        TrackCurves.java, StreetCurves.java
                                   the map's curves as one geometry (0.8.18, batch FX5; 0.8.20, batch
                                   CL4): a polyline's corners rounded with arcs - the railway's bends and
                                   its yards' S-curve ladders, which the trains' routes run along and the
                                   tiles draw, and the highways' 45-degree joints - and a street's smooth
                                   line from the plots a cut lays it on, simplified and rounded with
                                   TrackCurves, with the plots its band reaches, which the district plan
                                   keeps free and the painter draws across: one geometry read by both
                                   (FX3's rule); pure - RailDrawCheck 25 and 26, MapCheck 9 and PlanCheck 14
                                   hold them
        CityShore.java, SeaRoutes.java, ShipShapes.java
                                   the map at the water (0.7.97): the sea terminals, tank farms and the
                                   reserve's tanks laid once on the shore, a terminal by water that opens
                                   to the sea, its quay to its berth, kept in the map's sidecar (FORMAT 6);
                                   each terminal's route out to the world, found once on a 480 m sea grid
                                   and pulled straight to the offing and the abyss; a boat's hull, deck
                                   and colours by its class - the map's, nothing in the model reads them
                                   (spec-roads-and-ports.md 2.8, 4) - MapCheck 9 holds them
        TrainSchedule.java, TrainShapes.java
                                   the trains (0.8.2, batch FR1; spec-freight 4.4): the month's trains as
                                   a pure function of time, BoatSchedule's shape - the railway's tonnes
                                   by kind over a train's payload, each train's place on its route from
                                   the world line to a yard's siding (CityMap.trainRoutes(),
                                   CityRuns.wayTo()), a frame's query - never saved; a vehicle's body,
                                   mark and colours, the ships' four - the map's, nothing in the model
                                   reads them - MapCheck 9 holds them, RailCheck 7 the tonnes by kind
        TruckSchedule.java, TruckShapes.java
                                   the trucks (0.8.8, batch FR5; spec-freight 4.7, D21): Haulage's loads
                                   as a pure function of time, TrainSchedule's shape - Freight's saved
                                   tally of the month's truck tonnes by load, one truck per N loads to
                                   at most 1,000, each on a highway route from the world line
                                   (CityMap.truckRoutes()) or an access road to its gate - drawn at the
                                   near view alone and while the setting is on; a truck's body and
                                   colour by its load - the map's - TruckCheck and TruckRouteCheck hold them
        InterchangeGround.java     the ground a highway interchange keeps (0.8.6, batch FR7; spec-freight
                                   4.6, D19): a 19 x 19 reserve on a highway crossing (a cloverleaf), an
                                   11 x 11 one where an arm ends at another highway (a trumpet), else a
                                   stack - found locally from the runs and the ground, never saved, and
                                   street ground to a plan (CityRuns.F_RESERVE); the map's alone
        ui/                        the interface: UserInterface.java is the window (about 6,000 lines: the header
                                   and its clock, the rail, the main menu, the panels, dialogs), one
                                   <Name>Screen.java per tab (split 2026-09-18 - the project's
                                   splitting-the-interface.md), Money/Statement/Pieces/Levers (what the screens
                                   share), StatementView.java (0.7.74: SectorStatements' rows set as an
                                   accountant sets them, and the Summary | Statement switch the sector pages
                                   and the bank's share), Ladder.java (every dial, since 0.7.6), Palette.java (the colours, and
                                   its nested Fonts, which loads IBM Plex since 0.7.21), Icons.java, and
                                   FoundingScreen.java (New city's page, 0.7.10, one panel since 0.7.21, two
                                   columns on the real Earth since 0.7.121 - its globe and the founding card,
                                   EarthSite's, the globe the map's own since 0.7.135 (GlobeFrame,
                                   GlobePicture); the record it fills is Founding.java), and ConstructionScreen.java (the Build
                                   tab's construction page, 0.7.22), and TimeChart.java (City History's
                                   charts, 0.7.23: pan, zoom, ranges, an overview, named crises, the
                                   decision flags, full screen), and FundScreen.java (the city's fund as a
                                   brokerage, 0.7.39: Finances dispatches its area to it), and MapView.java
                                   (the city map on a canvas, 0.7.61: small in the land office, over the
                                   window on Expand; its arithmetic is the model's MapFrame, LandMap and
                                   MapTiles, which MapCheck holds; since 0.7.135 W, A, S and D pan it,
                                   and on Earth it is the globe - GlobeFrame and GlobePicture - in three
                                   tiers by its scale, MapCheck 13; since 0.7.137, batch M6, land is
                                   bought on it - a click on an offer opens its card, shift-click and a
                                   Shift-drag box pick several, Enter buys, short the card issues the
                                   funding page's bond - the card LandScreen's words, its figures, the
                                   picks and the box LandMap's, MapCheck 14; since 0.8.0, batch I14, the
                                   zones' districts tinted under the offers and named in its legend -
                                   CityMap.zoneTint(), LandMap.zoneAreas(), MapCheck 15; since 0.8.1,
                                   batch FR0, the world line past the gateway, faded to the view's edge
                                   and labelled "to the world" (worldLines()), and the legend's
                                   TRANSPORT, the railway in four entries and the gateway its own; since
                                   0.8.8, batch FR5, Haulage's trucks at the near view (trucks(),
                                   TruckShapes), while GamePrefs' trucksOnMap is on; since 0.8.27, batch
                                   UP3, a far node re-coloured only when its MapTiles.nodeKey() moved),
                                   and ZoningScreen.java (Policy's fifth
                                   area, Zoning, 0.7.109: PolicyScreen dispatches the area to it and keeps
                                   the staged set and the tray), and MiniMap.java (the minimap, 0.7.134: a
                                   stamp of the city at the rail's foot above Menu, its preview on hover,
                                   the land office's map over the window on a click; MiniMapFrame's
                                   arithmetic; since 0.8.27, batch UP3, the stamp and the preview from
                                   kept nodes, a picture begun at most every STAMP_EVERY_MS), and
                                   CapChips.java (0.8.17, batch CD2: the highest yield a
                                   sale takes as three chips and a yield box - fair plus a point, a yield
                                   named, or Fill it - one for each cap: Borrow's, the rollover's,
                                   automatic building's and the build screen's funding card's), and
                                   UiProbe.java (0.8.25, batch UP1: the window's probe - each rail screen's
                                   build and layout, the clock at 50x, the FX thread's shares under JFR, on
                                   the playtest's city grown to the months it is given; a main() run by hand
                                   on the PC, not in AllChecks), and Kept.java (0.8.26, study UP4,
                                   docs/studies/spec-screens-in-place.md: a page kept between redraws - the
                                   screen's own drawing code run over the nodes it made last time, by scope
                                   and order, its boxes setting their children in one call where they
                                   differ; People's page the pilot, through UserInterface.keepMenu()) with
                                   KeptBench.java (the People page on a scene of its own, drawn kept and
                                   built new, for KeptCheck), and ChartFitBench.java (FW10: City History's
                                   full-screen chart on a scene of its own at a window's size, through the
                                   screen's own fitToWindow(), for ChartFitCheck), and Branding.java (BR1,
                                   2026-10-27: the game's identity on screen - the icon on the window, every
                                   size of resources/icons/ handed to the stage, and the title art on the
                                   start screen; BrandingCheck holds the files and the store's title art to
                                   it). The model never imports it.
        *Check.java                142 harnesses, each a main() with static helpers
        MarketCheck.java           the efficient market (EM1), each case on a fixture that causes it: one value for a share, one
                                   yardstick, two shares alike but for price, the buyback finding its sellers, a share earning
                                   less than dollars abroad sold, a quiet market, the save
        AllChecks.java             the runner; its HARNESSES list is the registry - a harness not in it does not run;
                                   since TM1 it hands -Dcbs.timing to each harness and --timing runs the timing ones;
                                   since SP1 three at a time (PARALLEL), each JVM under -Xmx HEAP_MB
        Timing.java                the one helper every timing assertion goes through (TM1, 2026-10-18): under
                                   -Dcbs.timing=soft a figure over its budget says SOFT and is not counted, so
                                   two batches' suites check side by side; TimingCheck holds it
        TempDirs.java              the one place a harness or a scratch city gets a temporary folder from
                                   (batch TD1, 2026-10-21): one folder per JVM under
                                   <java.io.tmpdir>/citybuildersim-tmp/<pid>, deleted by a shutdown hook
                                   when the JVM exits, and the next JVM's first call sweeping the folders
                                   of processes that are gone; in place of Files.createTempDirectory,
                                   whose folders nothing deleted (37 GB of the PC's disk)
        SaveWriter.java            the one thread saves are written on (batch UP1, 0.8.25): the autosave is
                                   filled on the FX thread and its snapshot written behind the months, one
                                   file at a time, a newer job replacing the one waiting; a load, a numbered
                                   save, a skip and Quit wait for it; at most one autosave in 30 s of real
                                   time (FLOOR_SECONDS). SaveCopy.java beside it: the lists and maps the
                                   save's setters copy, so the save holds no live object. SaveSlotCheck 7c
        ClockPace.java             the window clock's arithmetic (batch UP2, 0.8.28): a redraw waits three times
                                   what the last cost (120 ms at least), a frame stops running months past 40 ms
                                   and drops what it owed past one, and the speed's reading says when the city
                                   falls a tenth short of the rung; pure, no JavaFX, ClockCheck drives it on a
                                   clock of its own
        LongPlaytest.java          4,000-odd months, audited every one; also the fixture builder harnesses borrow;
                                   its player asks the build advice which road (0.7.70) and how many (0.7.103);
                                   since 0.7.139 it says what childcare it stands at m1,000 to m4,000, and
                                   -Dplaytest.budget=N prints the treasury's month line by line; since
                                   0.8.14 (batch CL3) its layout watch: how many boxes the map moved
                                   between month pairs, and where industry and the homes stand
        tools/                     the index generators, the two look-up tools, Stale (the prose check), Names (a branch's names checked, RV1) and ManualToMarkdown (the manual into docs/); nothing in the game uses them
    src/main/resources/buildings.json    the balance file (ids permanent); consumption.json the basket;
                                         fonts/ the eight IBM Plex files and their licence, OFL.txt (0.7.21);
                                         conversion-saves.json the five saves' land ConversionCheck converts (0.7.66);
                                         icons/ the game's icon (BR1, 2026-10-27): icon.svg and icon-small.svg,
                                         the PNGs at 16 to 256 and icon.ico for the exe, all written by
                                         tools/branding/make_icons.py
                                         earth/ EarthCheck's fixture, cut from earth-1.bin (0.7.111), which
                                         EarthTerrainCheck draws the Earth's ground on (0.7.115)
                                         world/ WorldDataCheck's fixture, cut from world-1.bin by
                                         tools/world/fixture.py, and the .expect file beside it (WN0); and
                                         world-2025.json, the Rankings' real-world measures (LB1), made by
                                         tools/world/measures.py and read by WorldMeasures
    tools/earth/                         E0's pipeline that bakes earth-1.bin (Python, outside the game; the file
                                         itself is not in the repository), its FORMAT.txt and ATTRIBUTION.txt, and
                                         fixture.py, which cuts EarthCheck's fixture from it; and since f99d62c
                                         get-earth.bat, which fetches the file into data/ from the GitHub release
                                         earth-1 and checks it against earth-1.sha256 (Play CityBuilderSim.bat
                                         and Build EXE.bat call it first); since WN0 its second argument
                                         "world-1" fetches data/world-1.bin from the release world-1
    tools/world/                         WN0's pipeline that bakes world-1.bin, the nations' world (Python,
                                         outside the game; the file is not in the repository): FORMAT.txt the
                                         layout and the expect file's, worldread.py the reference reader
                                         WorldData is ported from, common.py the shared ground (the 196, the
                                         years, the freight dials), the stages that make each layer, bake.py
                                         that joins them, and fixture.py, which cuts WorldDataCheck's fixture;
                                         and since LB1 the Rankings' measures file beside it: FORMAT-2025.txt
                                         its contract, measures.tsv the 70 measures and their sources,
                                         regions.tsv the 62 city regions, measures_fetch.py the downloads (WDI,
                                         OECD, BIS, WUP 2025, EIA, BLS) and measures.py, which writes
                                         world-2025.json
    tools/deepseek/                      agent.py, the DeepSeek coding agent a batch's Claude session hands a slice
                                         to, and system.md, its standing instructions (Python, outside the game;
                                         2026-10-17, after the FR0 test in 6fd8dda); docs/deepseek.md is the how
    tools/ensemble/                      the 16-seed x 5-policy ensemble (EN1, 2026-10-20; until then only in
                                         workbench/workbench-delta-20261010.zip and each session's copy):
                                         the driver (class Ensemble), compiled at run time against a snapshot of
                                         target/classes; ensemble.sh the runner, five runs at once on the PC;
                                         score.py the scorer (pandas); README.md. Its scores, one a version
                                         whose trace moved, are docs/ensemble/<version>.md; EnsembleCheck keeps
                                         the driver compiling against the tree and the newest score readable
    tools/branding/                      the game's icon and title art (BR1, 2026-10-27; Jerus's picks of four
                                         concepts each, "towers as a chart" and "the ledger's total"):
                                         make_icons.py writes the two SVGs, the PNGs and icon.ico into
                                         src/main/resources/icons/, make_title.py the store's title art into
                                         branding/, pngio.py renders with Edge or Chrome headless and reads and
                                         writes PNGs (Python, standard library only)
    branding/                            the game's store art (BR1): title.svg with its font inside and its PNGs
                                         600, 1200 and 2400 wide (tools/branding/make_title.py), the itch.io cover,
                                         and README.md - the name, what is where, how to remake it, the releases
    tools/jvms.ps1                       counts the other batches' JVMs and the game's processes on the PC, for
                                         Batch check.bat, which times soft while either runs, and deepseek/agent.py
                                         (TM1; GP1 took out TM2's -Quiet wait, SP1 batch-check.sh's soft-run line)
    tools/pick.sh                        the harnesses that read what a branch changed, picked from its diff against
                                         main through docs/harnesses.md's reverse index, with no JVM: one line, the
                                         names, ALL or NONE, for batch-check.sh --pick, and on stderr whether the
                                         diff touches money, saves or threading (BP1, 2026-10-24); against the tip of
                                         the unmerged batch the branch is built on, when there is one (RL1)
    tools/carries.sh                     the ids of the unmerged batch branches on origin that a branch is built on:
                                         pick.sh's base, day.sh's "waiting", wait.sh --ship's "not ready", ship.sh's
                                         stop and batch-check's note on a NEW red, one test for all (RL1, 2026-10-27)
    tools/day.sh                         the orchestrator's morning in one call: main's version against the last tag,
                                         main's recorded reds, the queue's next rows and Pending, and each unmerged
                                         batch branch as ready, waiting, working, stuck or off-queue; --due only the
                                         ready and the stuck, for the hourly check-in (BP1, 2026-10-24)
    tools/wait.sh                        waits inside one command, so a session need not end its turn: until a
                                         batch's row has left origin/main's queue (--row), a batch branch on
                                         origin has its ship file (--ship, the orchestrator's: GP1, 2026-10-27),
                                         or a line of a file matches (--line); --for MIN gives up after so long
                                         (BP1, 2026-10-24); --row also ends on a predecessor sent back and mended,
                                         for the batch built on it (RL1)
    tools/ship-selftest.sh               ship.sh run against a throwaway local origin with a stub batch-check.sh, so
                                         no JVM: its stops and its records in about two minutes; run it after any
                                         change to ship.sh (BP1's review, 2026-10-25)
    tools/wait-selftest.sh               wait.sh against a throwaway local origin, no JVM: --ship ends on a ship file
                                         pushed, not on other pushes, a merged or deleted branch, --skip or a branch
                                         built on an unmerged one; --row on a predecessor mended; about 30 seconds;
                                         run it after any change to wait.sh (GP1, 2026-10-27; RL1)
