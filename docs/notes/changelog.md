# The changelog - what shipped, newest first

The live changelog since 2026-10-20 (Jerus: the record "in GitHub", so no
session has to paste into the claude.ai project). One block per version, at
most 12 lines (CLAUDE.md, "Each batch is told once"), in the house shape:
`### version - batch, title - date, state, see notes`, then one paragraph.
Since BP1 (2026-10-24) a batch's block comes from its
`docs/queue/<id>-ship.md`, its `{next}` the version: `ship.sh` puts it at the
top in the merge, and the branch leaves this file alone. A branch with no
ship file - a docs pass that writes none, an old-style branch - still adds
its own block at the top in its own merge.

The older record: the project's `claude/changelog.md` ("Civic Ledger: Java
Game") holds the blocks from 2026-09-11 up to the batch before I12, and
`changelog-archive.md` beside this file the blocks before 2026-09-11. The
blocks from 0.7.143 to 0.8.10 below waited in `pending.md` for a paste that
never came, and were moved here on 2026-10-20.

---

### 0.9.202 — PW3, wind and solar by place and season, and the power planner — 2026-11-24, ready, see docs/queue/PW3-notes.md
Wind, solar and the rivers now run by calendar month (EIA's 2025 fleet), the sun by latitude and rack (PVGIS 5.3, CC BY 4.0), and a city south of the equator on its own season. A wind or solar farm's firm credit falls with the variable rows' share of the energy, to a tenth at half. The power planner, in the advice and the automatic builder alike, runs the reserve test first: firm supply against the year's peak hour with a 15% margin, the works' own power counted, a plant on site counted from its opening, and each plant at its own posts' fill. Among the firm rows its order is the one cheapest a firm kW-year for the slice of the load the gap lies in: coal short at the base, a peaker short at the peak. A wind or solar farm counts at its falling credit, so wind alone never passes the test; where one farm's credit closes the gap and its year's cost, less the running it saves, is below the firm pick's, the farm is the order. A gap under three tenths of a plant (Jerus's share for oversized works) orders it only when the peak of the year's highest month is short at the plant's own completion, and a gap only the planner's horizon and margin project waits. A plant is ordered only when no faster one, on the menu now or brought by the calendar in time, could still be on line by the date that month falls short and meet it for less, so a slow plant is committed early only as the cheapest way to meet that date, and a gap of a few megawatts waits for a diesel's last month on time. So a small town is no longer sold, on credit, a plant many times the size of its grid, and the other works wait on power only when that month's peak is short of it. A power site past its deadline is no longer dropped whole: each plant on it counts from its revised opening, so a late coal plant is not ordered twice. When the test passes, the spread orders one more plant where re-dispatching the year shows it saves more than it costs: wind beside diesel, nothing beside coal. A large plant's ground is assembled past the listing, up to 96 offers, priced at the whole offers' own prices before any money moves; an order that cannot be placed buys no ground and sells no bond. Research E25, the single-axis tracker, lifts a solar farm from the fixed rack's yield. PowerPlanCheck is new.

### 0.9.201 — R6e-rest, the engineering rows R6e held — 2026-11-24, ready, see docs/queue/R6e-rest-notes.md
A city founded the 1900 way now finds the pressure exchanger (E22, 1997). A Desalination Plant built before it draws 13,454 kW, not today's 10,880, until the player fits the exchanger, and a plant built after comes fitted. The step is Las Palmas III's measured 3.49 to 2.68 kWh a m3, on the reverse-osmosis step alone.
While its patent runs, to June 2017, a city that fits it pays the licence on the power it saves.
Reverse osmosis itself, GPS autosteer, driverless haul trucks, endless strip, calcined-clay cement and the electric long-haul truck stay as they were: no source gives a measured step the game's figures can undo, and the notes say what was searched.
Saves from before this version no longer load (format 66).

### 0.9.200 — FS5, the KeptCheck sweep from section 55 — 2026-11-24, ready, see docs/queue/FS5-notes.md
KeptCheck causes its fund settlements, standing bids, held household cells, paper holdings and policy controls, and awaits the saved-city worker's result.
Retention counts the nodes drawn again; recurring rebuilds, repeated geometry differences and every content mismatch remain hard. Trade count-up covers changed and unchanged saved-month words at the real clock rungs.
Fresh Bank and Government comparisons restore their body and target owners even after an aborted draw; no-window bench minimaps stop their retaining timer.
Blank native label skins no longer keep a font strut after their words disappear, with exact signature, pixel and node-identity controls.
The unchanged 1536 MiB object budget normalizes reconstructible renderer caches and soft-reference age while preserving Canvas targets; a separate weak-reference guard rejects deliberately retained real pages.

### 0.9.199 — CY1, the planner appraises a processing works over its cycle — 2026-11-24, ready, see docs/queue/CY1-notes.md
A processing sector (Iron & Steel, Copper Refining, Wood Products, Cement & Concrete) now tests each works it might order on the lesser of the month's earnings and their mean over its last 12 months, so a one-month spike or a cycle's best month no longer orders a works the year does not pay for (FC2's seed 1 ordered one a month to 181 Rod Mills on a three-month cycle; CU2's seed 1 a refinery on a one-month spike). Its words say when the year held an order back.
A kind counts idle when its last works earns below nothing in the month or over its year. A paying month no longer starts the count again. A kind idle over its year is sold without waiting for the whole sector's six losing months, but only in a month its last works loses in and over its year, so a recovery's paying months sell none.
Copper Refining builds no more of a kind while its works making the same goods, standing and on site, would run under 60% of nameplate on last month's make (180 mills stood at a quarter of theirs for want of cathode that paid).
Each kind's year and the month's run are saved with its sector; an older save starts with an empty year.

### 0.9.198 — FC2b, the choices that use the foreign crews — 2026-11-24, ready, see docs/queue/FC2b-notes.md
A payer may now hand its share of one site's remaining work to the foreign crews (Jerus, 2026-11-22: "something finished in 20 months is better than something finished in 100 months"). They build it on the site at their own pace, about the threshold's months, and at their price; the payer pays the extra over the home price, and money already spent is sunk. A business weighs it each month by its earnings brought forward over the months saved, which is the interest on its cost plus the earnings past it. The city hands a site from the Construction screen's Foreign crews card (Jerus: "if roads are causing the issues then you could pay more to get just roads fixed"), which shows the price, the months saved and the interest saved. A shared site splits by the points each payer put on it, and the other payers' work stays with the city's crews. The crews' bill is the builders' import of services. Save format 65.

### 0.9.197 — FC2, construction traded with the world — 2026-11-23, ready, see docs/queue/FC2-notes.md
Jerus, 2026-11-22: "make it a imported/exported good... the base cost is still in domestic currency but the excess is in foreign currency". Foreign crews are always on offer at the city's base price for a point plus an excess of 15% of the world's base price for the same work, in foreign money (`BuildersShort.FOREIGN_EXCESS`), so a strong D$ makes them cheaper and a weak one dearer.
An order's work goes abroad past the months queued at which the builders' premium reaches that excess: never under the 9-month knee, and at most 21 months whatever the currency (`SURE_MONTHS`, a year past the knee, the time an owner takes to tender work abroad and have the winner start); 20.5 at par.
A weak currency barely raises the threshold: the cap, the premium at 21 months (16%), is barely over the 15%, so a D$ about 6% under par already sits at it and every weaker one changes nothing more than the share of the price paid in foreign money, while a strong D$ lowers the threshold as far as the knee.
An order's work past the threshold goes to the foreign crews as it is billed, a building the threshold falls in split at it (so one giant building, a substation or a power plant, no longer holds the city's queue past the threshold and sends every later order abroad), at what its quote charged for them: the excess at the month the order itself makes them run, so the payer pays a big backlog's dearer price and the builders' cash carries no gap. Work quoted at home stays at home, and a restart, a demolition and the paving are priced at the premium at home. A cancelled order takes its own share of the crews' book off it, and a currency reform carries the book's price and the world's price of a point, so a reformed city is the same city. Their month is their book in the threshold's months, so a 240-month backlog is built at the threshold's pace. Their excess rises with their month, half way to +80% at twice the builders' own (`SUPPLY_HALF`), and they bring their own crews.
Under the knee the builders sell the points no site took abroad, at the world's base and the city's own excess, booked once as the builders' exports in the audit, the national accounts and the current account.
The Construction screen's gauge and rows, the build card's line and the BUILDERS flag show both offers; the Trade tab carries the builders' export on a row of its own, so its sold abroad is still the balance of payments' exports. FC1's 5% floor, its double price and its quarter-month capacity are gone.
The figures rest on the World Bank's 7.5% domestic preference, the cost of flying crews in (up to 10% of a job), expatriate costs, demand surge after disasters (10-50%), and the time to procure works abroad (270 days in Korea, years in the slowest; FIDIC's 42 days to commence).
ForeignCrewsCheck proves each bound at three rates, under domestic inflation, for the export and for a 240-month backlog, its bill the payer's quote to the bit, for the building the threshold splits, the cancels and a currency reform; ForeignCheck's founded town, with the crews on, is built partly abroad at its payers' price (139,931 points quoted for 37,152.30, billed to 1.96e-16, the 500 houses whole at month 13 with 1,253 people); ConstructionTrapCheck moved to the new truth. A saved split factor under 1e-12 now loads as saved. No format bump.

### 0.9.196 — R6f, research: the 1996-2024 medicine, business and law rows — 2026-11-24, ready, see docs/queue/R6f-notes.md
Three rows enter research with something behind them, each undone in a 1900 city until it is known.
M12, robot-assisted surgery (Jul 2000): its 0.23 days off keyhole surgery on 0.78% of stays, so
general care's stays are a little longer before it; the four later stays rows keep 1950's 10.0 days.
B10, the AI assistant (Nov 2020): the Contact Centre's agents are 14% more posts before it, for the
same calls. L8, Basel III (Jan 2013): the 8% minimum stands throughout; the 2.5-point conservation
buffer comes with the row, and before it a bank's buffer is its own worst year alone. A load or a find
re-derives the bank's rule and the medicine at once. ModernRowsCheck proves the three. A city founded
the default way is unchanged. SAVE_FORMAT and its floor rise to 64: a format-63 Contact Centre before
B10 loads modern. M19 (hospital at home) and B9 (the tick) are queued, with what each needs.

### 0.9.195 — LP-G1, keep ground ahead across the player's long skips — 2026-11-23, ready, see docs/queue/LP-G1-notes.md
The test player looks at its ground across a skip: `LongPlaytest.skipWatchingGround()` runs the skip a month at a
time and makes the unchanged `keepGroundAhead()` look whenever `BuildAdvice.HORIZON` months have passed since the
last, in the playtest's loop, its Earth city and the ensemble's controller (the cash share per look, the line and
the hold when the share cannot pay are as they were; the fixtures `probePlay()` builds keep main's rhythm). The
default seed 0 went 62 months unlooked (m742-804) with its landlords held for land m756-804; now no gap passes
six months and every month held for land follows an offer the share could not pay. The same in the ensemble's
seed 9 (no look m482-603 before). Held for land, the player's city grew slowly: seed 0 now ends at 5.7 million
people (1.3 million), in about twice the time. `GroundSkipCheck`: the old skip's hold, the timely look, the
unaffordable offer. Versioned with no change to the game: the ensemble's baseline moves (docs/ensemble/0.9.195.md).

### 0.9.194 — PW2, the power plants and their fuels — 2026-11-23, ready, see docs/queue/PW2-notes.md
Five new power plants, each burning its fuel from the market by the MWh: the Oil-Fired Plant (1900), the Diesel Station (1910), the
Oil-Fired Turbine (1949), the Biomass Plant (1970) and the Solar Farm (1982); the Coal Power Plant and the Wind Farm refigured to
spec-electricity §2.3 (coal 325 MW at $3,271 a kW, 60 months, 200 acres; wind 20 MW at 33.8%, from December 1980). Fuel oil and diesel
are bought from Refining, pellets from Wood Products, the rest imported, each paid once in the money audit and the Trade tab. The sun
runs after the wind, by its hours. A coal, oil-fired or biomass plant stands only where the city owns sea, a lake or a river (NO WATER
TO COOL IT); a plant stands no sooner than its months, whatever the crews; each kind is offered from the year it was first built
(Jerus: "the rec, later research will change this"), so a 1900 city's first power is the oil-fired plant. Natural gas (PW7) waits
for the world file's re-bake. New harness PowerPlantsCheck.

### 0.9.193 — PW1-W, water priced from its cost — 2026-11-23, ready, see docs/queue/PW1-W-notes.md
The sea's water is priced as PW1 prices power, at its margin: while the city needs it, a unit costs its tariff and the
desalination works' power over the water the sea delivers, at the struck commerce price with PW5's wires. A plant under half
used spreads its power over the water served instead, recovered once and over no more than half its output, so a sliver of
the sea has a bounded price. While the land's water (the wells and the fresh works) serves, the price is the tariff, which
pays the fresh works' power. The water's books buy all the works' power of the power's at the top of the next month, inside
the treasury, so the utilities' net is unmoved; the Services screen shows it on both books. A Desalination Plant's water
covers its power again and a patented one earns its licence: PublicLicenceCheck's four desalination reds pass. A licence's
power is now the dispatch's energy served with the plant's row moved, not the flat rule (PW1-L), and a recipe's surplus power
is weighed at the works price the strike pays it, not its class's (PW1-G). WaterPowerCheck holds the price, the bills and the
save on both sides of each edge. No format bump.

### 0.9.192 — PW5, substations — 2026-11-23, ready, see docs/queue/PW5-notes.md
Substations carry 100 MW of the peak each, ordered by the automatic builder through the
same funding, ground and construction rules as roads. Beyond the free founding 10 MW,
a city short of wires browns out in the power dispatch's existing shortfall figures.
The wires' bill now reads their capital over forty years at four percent real, repairs
and filled posts, paid through the existing city books once; industry retains its
transmission-only share. Closing costs are saved with the dispatch, so reloading keeps
the next bill exact. Currency reforms scale every wires component once. Older saves
are refused with both files and the running city intact under the raised format floor.
PW5-F1: a 45 MW town pays 11.38 cents/kWh against 5.94; each district also needs
110 repair points/month. Before HC1, seed 0's first stands at m576: builders 6,176,
standing repairs 420 (110 for the district), leaving 5,756 points for sites.

### 0.9.191 — AP2, slot 9's slow months at 17 million people — 2026-11-22, ready, see docs/queue/AP2-notes.md
On Jerus's slot 9 (17.3 million people, 321,345 buildings, 151 districts) the months the ground moved took 8-15 s. A highway piece's failed link search reruns at every purchase, and its 2-million-plot ring drew the same Earth tiles again, about 90,000 draws for 4,000 tiles, through a 256-tile memory.
The runs' ground keeps every tile a link's search reads (`CityMap.RUN_SEARCH_TILES_KEPT`, 8,192, for the search alone; `CityRuns.Ground.searching()`), and gives back its usual 256 after.
`CityLand.centreStamp()`, asked at every plot the search reads, is kept between the centre's changes. `EarthTerrain.idsNear()`, asked by every bridge survey, is kept a tile. The auction's bidders read what each sector owes from one pass over the bank's book (`BusinessDebtManager.getPrincipalsByKey()`).
Every figure is the same to the bit. GatewayCheck, LandCheck, LandmassCheck and HoldersCheck each gain a section proving their kept read equals the read afresh and moves with what it reads, and the playtest's trace is the same byte for byte over 2,405 months.
On slot 9 the month went from a mean of 5.9 s, a median of 3.1 s and a worst of 12.9 s to 2.5 s, 2.4 s and 4.8 s, with every month's population and the whole log the same. The paused Build Overview's model went from about 590 ms to 540 ms.
A waiting link's search (about 1.5 s a land month), the re-measure when an island is linked, and the Overview's auction quotes are left for Jerus.

### 0.9.190 — HC1, credit cards — 2026-11-22, ready, see docs/queue/HC1-notes.md
Households carry card balances within each cell: thin buffers meet unexpected
bills, a 3% minimum is a fixed bill, and holders can fall behind, recover or
charge off without discharging their neighbours. Borrower status ends at
estates; discharged families leave debt-free from their source cells. The
neutral card APR is 21.35%, capped at 35%; the bank books payments and losses.
Household money shows debt holders, medians, means, payment burdens, arrears
and yearly surveys by tier and stage, per household or adult. Every new flow
has one money and national-accounts path. Save format 62 refuses older saves
without conversion; refusal of formats 60/61 and exact next-month continuation are checked.
The 80-run ensemble of f78dfd076 passes its money-audit and findings checks.

### 0.9.189 — RES2, aggregate housing inventory — 2026-11-22, ready, see docs/queue/RES2-notes.md
Separates completed residential structures from their immutable dwelling mixes,
aggregate titles, occupancy and listing subsets. The three current templates
remain compatible; the founding places are a named non-saleable endowment.
Supplies pure stock/pipeline reads, retained-occupant matching, quantity transfers,
one completion mix, saved/rebuilt cohorts and fixed asset/collateral allocation.
Preserves next-month construction work and RN2 debt allocation across reloads;
refuses genuine older saves without conversion. Caused housing/save checks pass or retain named main-only reds;
all eight default trace CSVs are observed byte-identical to current main.

### 0.9.188 — RS1, the city can sell the bank it rescued — 2026-11-22, ready, see docs/queue/RS1-notes.md
The city's hand offers its shares before world and household investors choose
their purchases, so they can buy a rescued bank and pay the fund for real
ownership. The fund's rule pauses its buying of a company the hand is selling.
A crossed book quotes the last trade, or fair value before its first, and
explains the crossing instead of showing a misleading midpoint. Rounding
residue at the desk's caps or the bank's buyback capital target cannot fund a
dust trade that replaces a real last price. Regression fixtures cover all four
findings and both sorts of buyer; no new saved state is needed.

### 0.9.187 — HS3, the household money page by stage — 2026-11-22, ready, see docs/queue/HS3-notes.md
Household money separates young and established households and opens each
stage's own books, with the retired still named by former tier and owner
families on their own line. The month's estate cash, debt settlements and
taxes on realised gains and dividends read the model's own figures. Kept
redraws update the page in place and retain its scroll; no simulation or save
change.

### FS9 (untagged) — the bank's harnesses cause what they read — 2026-11-22, ready, see docs/queue/FS9-notes.md
The bank and its borrowers' twelve harnesses (Bank, YoungBank, Mortgage, CreditCrunch, SupplierCredit, Credit, CapitalFlow, CarryTrade, Invest, DistressBleed, BankFailure, OrderDoor) cause their premises, not what a played city happens to hold.
DistressBleedCheck sets the loss and operating streaks, the desk's loan and the till, and reads planDistressRetirement at DISTRESS - 1 and DISTRESS: no LIFT_AT.
YoungBankCheck lends a lone Lime Kiln twice the young bank's equity on four twins and writes off a loss under the cushion, between cushion and equity, and past equity: resolution only past it.
MortgageCheck issues its mortgages by hand and causes the lean month; CreditCrunchCheck holds the bank where leverage binds (holdInBand), stands its landlords' ground each crunch month, and strikes the desk's rule before its caused order.
OrderDoorCheck plays seed 12 to m200 and sets HN1-A's edge by hand: the yard short of a block, the market's materials 3.5% over the planner's price.
BankCheck's nine played sections set the loan, the carry, the savers' book, the desk's trades, the allowance, the slices, the distress sale and the tab's events by hand.
CapitalFlowCheck causes its carry and its twin's profit; with BankCheck it leaves Observation.HARNESSES. BankFailureCheck and InvestCheck keep their played runs as identities, their counts printed.
The 21 FS9 known reds retire, none a fault (three of them HT1's). The batch is harness-only: the model, the screens and the trace do not move.

### 0.9.186 — R6e, GM crop history — 2026-11-22, ready, see docs/queue/R6e-notes.md
GM crops enter farm history in 1996. Before E21 is known, output is the unchanged
modern 500/69 divided by 1.22: an approximate crop-mix estimate with source limits
and CC BY 4.0 credit disclosed. PE20/E10/E21 add fixed-anchor differences; livestock,
land, jobs and inputs stay unchanged. Patentability uses the native estimate.
Captured fees now follow each farm's actual credited output, so an idle farm
cannot pay for another farm's coproducts; this also mends main's combine/milking fees.
SAVE_FORMAT and its floor rise to 60: format-59 pre-E21 choices load wrong crop
figures immediately, so older saves are refused without conversion. Other rows
retain their figures and explicit conditions below; E24 waits for PW3, E27 for E18.

### 0.9.185 — RR1, the map's roads and rails — 2026-11-22, ready, see docs/queue/RR1-notes.md
Jerus's city drawn as he saw it (slot 10's copy, months 1,204 to 1,208). A works siding drawn square from the first draw after
a month keeps its arcs (`CityMap.planSidingsRoutable()`, RailDrawCheck 31). A highway S-bend's straights stop where the next
bend's arc begins (`TileRaster.highwayLegs()`, JunctionCheck 24). ★ A main line that reaches no edge is taken back to the trains'
way (`CityRuns.cutDeadSpine()`, MainLineCheck). HWW1: where the highways' gate arm ends at the city's own water, the trucks come
in at its end (`CityMap.highwayPortal()`; his city 0 world truck routes to 7, TruckRouteCheck 11). STY1: a station at the mixed
train's tier is weighed beside the quarter line too (MainLineCheck 17 (c)). ★ A highway's end with a street beneath narrows to
that street, carried past a link's bend, and an arm waiting at the city's own water has a street laid beneath its ramp
(`TileRaster.highwayEnds()`, `DistrictPlan.rampJoins()`, JunctionCheck 25, 26). FA1's frontage avenues end at their last
crossing, no stair step past it (`DistrictPlan.frontageEnds()`, PlanCheck 20). Bergen's 48-plot station through the hub is
queued (RH1). Map only: nothing in the model reads it, and the default trace is unchanged.

### 0.9.184 — HT1, household basis and investment taxes — 2026-11-22, ready, see docs/queue/HT1-notes.md
Household stock purchases and founders carry average acquisition cost with the people; sales release it and carry unused losses. Real sale and integrated dividend taxes transfer cash to two named treasury revenues, with an adjustable inclusion dial. Cash-first secured deferral, estate step-up, survivor rollover and passive unclaimed-property custody preserve the actual stocks and payments. The incompatible save format carries basis, claims, policy and struck flows. Genuine bond extinction closes every household position without inventing cash. Independent whole and scoped reviews have cleared the money/save changes; integrated .183 custody, monetary, save and scaling checks pass. The final model ensemble passes every money audit; its exact seed14 land-funding finding passes by name with FB11H queued. The versioned ALL dry completes all234 selected harnesses and its retained-run record gate passes, with remaining assertions traced and named with queued owners. L7 retains its separate Pending account-design disposition.

### 0.9.183 — OG1, a planner orders to the gap — 2026-11-22, ready, see docs/queue/OG1-notes.md
Past a nine-month backlog the builders no longer order one depot a month. They order the gap between their
sites' month and the work a typical month sends them, plus the queue past the knee over two years, never more
than 1.8 times that month. The typical month is a saved median of a year, their own depots left out. Each
batch takes at most the premium's share of a month of the sites. Food Processing orders as many plants as the
city already eats. On the 2,424-month playtest the months past the knee fell from 685 to 438 (470 to 224 with
over 100,000 people), and the city reached 629,000 people to main's 531,000. A first cut sized to the queue
ran to 7,771 depots to main's 299 and was dropped. His own city (slot 10) plays as main and gains nothing
from this batch: its builders' order is refused at the staffing gate (0.57 of a depot's posts staffable
against 0.80, with 0.0% out of work) before OG1 sizes it. GapOrderCheck holds it.

### 0.9.182 — FB16, OE1's four follow-ups — 2026-11-22, ready, see docs/queue/FB16-notes.md
OE1's four follow-ups, one commit each, every line causing its condition. AS1: AuctionCheck §11 sells a bill at
its 1,200-month run's end and counts exactly that one sale more than the run booked. CI1: Motoring strikes the
households' car imports as the spend's import share, so paid and imported are one number; a CarCheck section walks
the import price a bit at a time until the two roundings part. PT2: PassThroughCheck §3 stands grocery stores and
stocks the shelf at the DEAR pin before the step, so the shops hand over the households' need in all 12 months. RN2:
RealEstate saves the count its closed homes' share moves against, put back after the load's rebuild, so a reload's
first month moves the share as the live city does (ReloadCheck 3k, HousingCheck); an old save loads as before, no
format bump. The playtest trace is unchanged; four known reds leave.

### SD1 — Correct scrapping wording — 2026-11-22, ready, see docs/queue/SD1-notes.md
Physically retired buildings now say Scrapped, with the same quantities,
plot-return and recovered-material details. Genuine compulsory purchases
still say Sold. Build cards keep their existing classification and compact
wording. This untagged wording correction changes no economic decisions or flows.

### RES1 — Real Estate study and HO1 implementation contract — 2026-11-22, ready, see docs/queue/RES1-notes.md
Finalized the aggregate housing design and HO1 contract after Jerus's review:
fourteen initial building forms and 32 dwelling classes, complete numerical
cost/rent/parking/fee tables, measured compute and executable underwriting/cash
fixtures. Recorded tenant retention, persistent common-cost pools, real funded
sales and the immediate financial-demolition guard; supplied RES2-RES5 and
HO1/REU1 briefs. The study-review hold is closed; actual model calibration and
code dependencies remain the named implementation gates. Documentation only.

### 0.9.181 — PW6, the recorded power month — 2026-11-21, ready, see docs/queue/PW6-notes.md
Services › Utilities shows the recorded month's six supply stacks, each plant's run,
fuel and clearing receipts, clearing prices and bill parts, firm capacity against
peak load, curtailed and unserved energy, with the dispatch and billing periods named.
Power observations and history survive saves; the completed closing dispatch is retained
for the following tariff instead of being rebuilt from ending fleet, draw or fuel prices.

### 0.9.180 — PW1, power by the merit order at the market's price — 2026-11-21, ready, see docs/queue/PW1-notes.md
Power clears by the merit order at the market's price (Jerus: "Market price"): three load blocks, each a windy and calm half, with the works' surplus first, then wind, hydro and fuelled plants.
Short half-blocks clear at the cap. Coal plants burn thermal coal bought from the market. Commerce pays energy and both wires; Heavy Industry and Mining pay energy and transmission.
A saved monthly book holds the ratio, prices and fuel; the works receive their half-block prices, and licences and recipes price power at their building's class. Pre-PW1 saves seed the ratio from their loaded dispatch.
Advice plans for the calm peak, counting a Wind Farm's firm third, and the playtest player follows it. Founded cities count the free grid immediately. Services shows prices and dispatch; Trade shows plant fuel and upkeep.
The final gate-box ensemble on the .179 merge passes all 10 M1/F1 hard criteria, 25/26 overall, with no changed verdicts against .179 (`docs/ensemble/PW1.md`). AUTO2 T2 now passes; PW1d's .174 miss stays archived.
New checks: PowerDispatchCheck, PowerPriceCheck and BuildAdviceCheck 14-15. Existing power fixtures, class-price licence/recipe checks and the pre-PW1 load check follow the market book.
OilCheck establishes real fuel demand before its observed year (recovery check: first fuel month 16, limit 36); its original assertions stay hard. Named reds are traced to PW1-W, HL2 and FS9.
PW5, PW8, the planner gaps, water tariff and remaining licence/recipe valuations stay pending.

### 0.9.179 — MC2, BF1's band share under the leverage target — 2026-11-20, ready, see docs/queue/MC2-notes.md
A bank in its band, between its minimum and its target, lends growth again. BF1 bounded new
buildings' loans and, while the leverage ratio binds, new mortgages by the bank's room to its
target, which is none anywhere under it: a bank a loss put in its band lent no growth at all.
Under the target the room is now the growth limit's share (Bank.lendingGrowthLimit(): 1% a month
halfway, rising toward the target) of each borrower's debt and, as one allowance, of the bank's
book, on the equity the month's known defaults leave, and never past the room to its minimum.
At or over the target it is BF1's room to the target, as before, and none while the month's
default takes the bank under it before its book settles; the carry trade still gets nothing under
it. BankFailureCheck section 9 causes both sides, the cap and the default with hand-built banks,
and 14 of its 17 assertions fail on the old rule. In MC1's played crunch the landlords took 6
mortgages, where none were written before.

### 0.9.178 — TN1, the trains run again — 2026-11-21, ready, see docs/queue/TN1-notes.md
Jerus: "trains no longer show up... sometimes only briefly for a few years and then disappear forever". The tonnes were railed; the
trains had no way in. As a fjord city grows, every railway arm comes to wait at its own water, or the land past its edge is water
within 64 plots toward the gateway and straight on, the only two ways a line to the world was tried: no line, no route, every train
unfit. And a station laid early at its 8-plot footprint is kept at that box for good, and since FX5 holds no train. Now a railway arm
at the edge also tries its heading turned a 45-degree corner; with no line the trains come out of a portal at the main line's end;
and a station laid short is laid again longer each January where the ground allows, beside the quarter line for one that holds no
train. Jerus's city (month 1,204: 29 trains, none run) runs 4 a month from the next January; the Bergen playtest 5 to 7 from its first
station at month 840, where main ran none. MainLineCheck 17 and GatewayCheck 22 cause each and fail on main. Map only:
the default playtest's eight trace files are byte-identical to main's.

### GB5 (untagged) — the gate box runs the harnesses that need a display — 2026-11-21, ready, see docs/queue/GB5-notes.md
The gate box has no screen, so since GB1 every gate skipped the nine harnesses that start the JavaFX toolkit, KeptCheck and KeptTimingCheck among them.
Its gates and nights now draw them on JavaFX's Monocle, headless and in software (pom.xml, test scope), which tools/gate-box.sh names to every JVM they
start; Linux lays text out through Pango (libpangoft2-1.0-0, which Jerus installed), and a box without it says so in one line.
Every display harness starts the toolkit through ui.FollowBench.startJavaFx. With no display named it skips as it did, on the PC and in the cloud;
with one named (-Dcbs.display) a toolkit that cannot start is a FAIL and exit 1, never a skip. The summary's display line lists those a run started
(AllChecks.DISPLAY), and TimingCheck 10 guards the one start, the list and the skip. On the box KeptCheck prints the PC's 4,034 lines but its heap
line, and KeptTimingCheck meets all 29 budgets, the worst at 0.42 of its own. The PC's runs are as they were.

### OB1 — live provider budget and orchestration reserve — 2026-11-21, ready, see docs/queue/OB1-notes.md
The orchestrator reads live usage and warnings every turn and before dispatch, reserves its cost through reset plus running and pending commitments,
and targets projected subscription use of 98-99% at reset without intentionally exhausting a plan. Stale readings forbid uncertain-provider launches;
current MemWatch advice routes within the ceiling, with explicit Codex workers while Claude is exhausted and reassessment after reset. Dispatches
record readings and reserve forecasts, and controller handoff precedes exhaustion. This is operational policy, not a hard MemWatch launcher guard.

### 0.9.177 — FB15, HN1's three leftovers — 2026-11-21, ready, see docs/queue/FB15-notes.md
A landlord's order is sized at the door's own price (HN1-A): `Game.doorPriceOf()`, the quote and the land as
`buildFor()` charges them, prices its equity raise, the lender's test and the build card, so a down payment raised
to the plan's figure no longer fails at the door (HousingDoorCheck's seed 12 window: nine such cuts on main, none
now). A project's order the lender refuses for the bank's capital or a ban is asked again of half (HN1-B): the till
builds what it buys, the line says why it was trimmed, and the capital marks are taken back (seed 12 to m900: 18
such month-ends, none on main); past the default point or with the bank shut it holds, as before.
`tools/ensemble/stop.ps1` makes the Git Bash form of the run's folder itself, since Git Bash hands it C:/..., then
looks once more and names what outlived the stop, exit 1; with no folder or label it stops nothing, exit 2 (HN1-C,
its selftest). The new OrderDoorCheck causes HN1-A and HN1-B, eight lines red on main. HousingDoorCheck names the
landlords' homes by the door's price too (68 orders with the segment short, none cut for the down payment), and
CreditCrunchCheck 4 causes an order the owners' issue falls short of. The played cities of MortgageCheck 12 and
CreditCrunchCheck 2 moved, their fixtures known reds under FS9. ForeignDebtCheck 7 asserts a reloaded city's first
month against its twin's, not 5% of the principal, which a month's own move crossed in the moved city.

### 0.9.176 — RB3, the fund's over-limit sale and the fixed point — 2026-11-20, ready, see docs/queue/RB3-notes.md
FS13 found FundCheck 9e's town holding 11.17% of Construction against the fixed point of 10.92%. The book had last traded under fair value,
the leavers asked 230 to 400 shares a month at the desk's bid, a buyback buying 200 took theirs first, and the fund's over-limit ask at fair
value rested 34 months running. That ask is now the market's (Exchange.limitAsk()): the last trade held between the desk's bid and fair value,
so fair value while the book trades at or over it or on a trade older than the fund's stale mark, and it is posted before the leavers, so
a buyback meets it first at its price. The fixed
point does not bend, since an unsold tenth has no rest. ExchangeCheck 7c causes the month (20 owners leaving with 2,000 shares, the fund at the
fixed point, the last trade under fair value), and each of its lines but the other sides fails on main's rule. FundCheck 9e drops FS13's
world bid and holds 10.28% on the town's own sale (10.31% and 10.30% on 60 and 84 months of founding, where main reached 11.17% and 11.14%).
The default playtest never puts the fund over its cap, and its eight trace files are byte-identical to main's, at the base and merged.

### 0.9.175 — AP1, the auto-builder's month at 400,000 people — 2026-11-21, ready, see docs/queue/AP1-notes.md
On Jerus's slot 10 (421,561 people, 57 stacks on site) the auto-builder's search asked the power ahead (`WorksValue.pendingPowerLoad`) about 1,100 times a month, and each call rebuilt the site's whole completion schedule, with the builders' staffing for every stack and every template's new-unit draw.
`WorksValue.hold()` holds the schedule and the draws in a scope, for one city on its own thread, opened by `AutoBuilder.pass` around its loop and by the Build screen's Overview around its draw.
What is held is keyed on `BuildingManager.siteStamp()` with the month and the output on site, read at every call, so an order, a cancel, a rush or a priority is read afresh at the next call. Closed, every call computes as before.
The same order and products are kept, so every figure is the same to the bit. NewUnitCheck 13 proves it at every horizon and for `relief`'s avoided loss, and proves that an order, a rush and a priority inside a scope are read, one schedule is built a scope (a count), and nothing is held once it is closed.
On slot 10 over 24 months the month went from a mean of 43.6 s and a median of 40.1 s to 4.5 s and 4.3 s, on a busy laptop, and the schedules built from 38,262 to 24. The cash, people and spending at the end are equal to the bit.
The default trace does not move: all eight playtest CSVs are byte-identical to main's over 2,405 months. The run took 660 s, against 1,116 s.

### FS4 (untagged) — KeptCheck I causes what its pages read — 2026-11-20, ready, see docs/queue/FS4-notes.md
KeptCheck's sections below 55 (the city, the chrome, People, History and the KP2-KP4 pages) cause what they read on its scratch city, not what Danzik's months happen to hold.
Each page's kept rule is the identity, its share taken against what each redraw draws again, at HANDED_BACK: a page that grows or shrinks loses nothing it did not draw.
KRT1 traced held still: a difference only of place or size is drawn again twice with the model and the clock held; repeated it fails as a kept fault, gone it is a reading (none arose).
The hovers of 35, 46 and 54 are checks, and 35 records a decision so History has its row; 11's panels show a building's opening and a notice, caused.
44's covered run is found by halves inside the decade; 51's swing is sized again each month, and a business page waits on its own figure.
KT1: the timed sections (5, 10, 13, 20, 24, 28, 32, 39, 43, 52, 68, 81, 90) move whole to KeptTimingCheck, in Timing.HARNESSES, budgets and labels as they were, each page drawn first as KeptCheck drew it.
KeptCheck is correctness alone. The eight FS4 known reds retire, none a kept fault; 89's premise, moved with the city, is FS5's, whose range it is.
The batch is harness-only: the model, the screens and the trace do not move.

### 0.9.174 — OE1, a first plant weighed at the rate it will run at — 2026-11-20, ready, see docs/queue/OE1-notes.md
An order for a sector with nothing running weighed its first plant at the empty sector's rate, its staffing at 1:
BankCheck 19's Lime Kiln at 0.97, which ran at 0.52-0.61, and AUTO2's Fabrication Shop at 0.81 (0.51-0.81).
`Sector.plannedRate()` now weighs it at the rate the month will give it (`firstPlantRate()`): the city's fill over its
own posts, the roads for its own traffic, its freight at Haulage's opening share. A running sector weighs its own rate,
as before; every reader of the estimate takes it, and YoungBankCheck section 3 fails on the old one. The young town's
spell under the bank's minimum, begun by a lone plant's write-off, is gone. A staffing hold weighs a first plant
staffed (`Sector.staffed()`), so it still asks for the wage that would staff it. The household match no longer books
its own rounding as households doubled up (`FamilyModel.packingRounding()`; HousingCheck), which had let one of
DenominationCheck's twins' landlords sell and not the other's. BuildAdvice's field earnings stay on the mines' own
rate (OE1-A). A car drawn is a car paid for: the take reads each household's want at the price the draw was
sized at (`HouseholdBalance.takeCars()`), where a household on the whole-car edge had had a car drawn, booked to
its seller and never placed (the ensemble's F1, AUTO2 seed 6 m655; CarCheck).

### FS14 (untagged) — the Exchange and the bond desk cause their conditions — 2026-11-20, ready, see docs/queue/FS14-notes.md
The sweep of the Exchange and the bond desk (FS1 study 6.13): FloatLink, Desk, Call, Holders, Bond, Equity, Exchange and ShortSelling (each
...Check) now cause the premises they used to read off a played city. The households' savings, the bank's room, the tills, the dials and the
due dates are set by hand between months; the waits go (FloatLink's 60 months for room, Call's 24 for the central bank, Desk's loop of rate
rises, Bond's year of steps). SX2: ShortSelling 7's city sells the world shares and buys half back in month 61, caused as section 9 causes it,
and the 2,000 months' audit, register and shorts-off identities stay played. Holders, Bond, Desk, FloatLink and Call drop 29 Observations and
leave Observation.HARNESSES; Equity 9 and Exchange 10 count every holder and print their found counts. All twelve FS14 known-reds rows go
green. Harness-only: the model and the trace do not move.

### 0.9.173 — NG1, imported inputs the ground does not give are not bought — 2026-11-20, ready, see docs/queue/NG1-notes.md
HL1's ensemble found a month (AUTO2 seed 12, m902) in which the works imported 2.6 times a month's inputs as their exports went. That month was GDP1's January stale price, closed in 0.9.97, and the seed no longer reaches it.
The class still live was the ground: a works the ground limits ordered its inputs for its whole plant. An Iron Mine on a worked-out field bid a month's diesel after the dig had given it no ore. A surface coal mine's diesel,
a leach plant's acid and a gold mill's acid and diesel went the same way, because an empty lift reads an order share of 1. What nothing made was imported and became nothing, and GDP fell by its value. Each building's
inputs are now ordered for the share of its run its ground gives (Sector.groundShare). The share is exactly 1 wherever the ground holds the month, so the default playtest's eight trace files are byte-identical to main's.
The inputs are not bought rather than held as stock: acid is a flow good nothing holds, and diesel kept by a worked-out mine would never be burned. Kept: a falling gold grade still mills its whole ore, the fleet's diesel and
the consumables are standing bills, and inputs held over the month's end are counted in GDP's inventory term. GroundInputCheck proves the mends and what is kept.

### FS17 (untagged) — the saves, the history and the previews cause their conditions — 2026-11-20, ready, see docs/queue/FS17-notes.md
The sweep of the saves, the history and the previews (FS1 study 6.16): SaveFile, ReadPath, Reload, History, Gdp, NewGame, PolicyPreview,
MoneyDates, Calendar and BasketYear (each ...Check) now cause the premises they used to read off a played city; SkipReport's one section is an
identity and stays. The waits go (SaveFile's two of 240 months, its 120 for a moved rate, ReadPath's 60): the savers' rate, the poorest cell
without means, the world's bid for the fund's shares, the pinned rate, the depots past the builders' month are set by the model's own calls.
PolicyPreview gives the bank its profit, lays workers off past the vacancies and reads the floor on a hand-made ladder; its 19 Observations and
SaveFile's 8 go, and both leave Observation.HARNESSES. BasketYear hands its car towns a car's price over their cushion. The 15 saved-equals-loaded
identities stay played. All four FS17 known-reds rows go green. Harness-only: the model and the trace do not move.

### FS10 (untagged) — the currency, the gold and the central bank cause their conditions — 2026-11-20, ready, see docs/queue/FS10-notes.md
The sweep for the currency, the gold and the central bank (the FS1 study): ten harnesses now cause the premises they had read off played cities.
GoldStandardCheck 9 asserts the dial at the cover the month's top holds still (HP1 item 1), and 7 holds its town's cover by gold bought by name.
ExchangeRateCheck 8b's dated peg is held solvent by dollars bought by name, its shadow gap set at the eve of August 1914; 6 and 8 read the market's dials on a bare float.
PassThroughCheck's shelf is traced to its floor, and a dear goods' D$ makes its input cost bind; the base gap is struck, not inherited.
CentralBankCheck's towns are stood whole so their cells shop, 7 restores a year of prices, and 21's rule is shown half by half on bare objects.
CurrencyRegimeCheck's six Observations go: pegs struck at their gaps, hot money and doubt placed, a year of prices restored; the seed searches are removed.
ForeignCheck reads its trade in the months its cause stands; CurrencyCheck 4 and InterventionCheck 5 cause the defence's sale; InterestAbroadCheck 9 sets the hot stock.
MonetaryCheck (MQ1) holds the D$ so a month's delay cannot move its rows, read over three calendar years with its premises hard; four harnesses leave Observation.HARNESSES.
The twenty FS10 known reds are retired, none a model fault. The batch is harness-only: the model and the trace do not move.

### 0.9.172 — GY1, the graph's GDP by the year, and debt to GDP — 2026-11-20, ready, see docs/queue/GY1-notes.md
The graph's "GDP" line is now a rolling year of output ("GDP (yr)", nominal, the twelve months summed the way real GDP's
year is, drawn from the end of the first year), so it reads on the same axis as the public debt; the month it used to
draw is still there as "GDP (month)" under the real and per-capita lines. A new line, "Debt to GDP", is the public debt
over that year of output as a per cent (205% is a debt of two years of output), the measure the debt limit reads,
blank where there is no year of output behind the month. A pin, a preset or the header GDP tile's door that named GDP now
draws the year. Both figures come from `YearBook` (`gdpYear`, `debtToGdp`) and are derived from the history already kept:
no saved field, no save format change. `YearBookCheck`'s new section builds the history by hand and asserts the
twelve-month sum, the first-year rule, and the ratio at chosen months with a year of nothing giving no figure; each
fails with the getter swapped for the month. The default playtest's trace is main's, byte for byte (eight files, 2,405 months).

### FS13 (untagged) — the fund, the treasury and the trading city cause their conditions — 2026-11-20, ready, see docs/queue/FS13-notes.md
The sweep for the fund, the treasury and the trading city (the FS1 study, 6.12, with PK1's FundCheck half): seven harnesses now cause the premises they had read off played cities.
TreasuryCheck's rollovers set the year's surplus by the tax and take the notes' due months from the pieces issued. The window is set by the share of exports on both sides of WINDOW_SHUT_SERVICE_SHARE, with no press to month 60.
ForeignDebtCheck, RestructureCheck and BuybackCheck set the exports, the dial over the world and the auction's holders (the savers' rate, the bank's room, the sale's size). Their guard and borrowing loops are gone.
CurveCheck sets the inflation and the expectation through their save arrays, and its one-ulp line states MoneyAudit.tolerance (row 114 retired).
FundCheck and FundLedgerCheck build their bank at the founding and play a named 72 months, and are green at 60 and 84 too. The desk's room, the holders and the split unit are caused. Row 82 is retired: the town's sellers used up the desk's bid before the fund's ask.
CurveCheck and FundCheck leave Observation.HARNESSES. Queued: RB3, the fund's limit ask at fair resting when a company trades under fair. The batch is harness-only: the model and the trace do not move.

### FS16 (untagged) — schools, work and the households' books cause their conditions — 2026-11-20, ready, see docs/queue/FS16-notes.md
The nine schools, work and household-books harnesses cause what they used to find by playing (the flaky-suite study's FS16
rows), harness-only: no model class moves. EducationCheck's fourteen played sections set the licensed heads, posts and
students directly and seed the courses (seeded, finishing); LabourCheck's staffing, rent, arrivals and doctor sections run
on hand-fed people and markets priced by the labour market's own months, and LabourCheck leaves Observation. The ties
and the staffing draw are seated by hand in a month, not waited 328 months for; the flatshares, the real deposit rate and
the commute are struck on set books, rates and cars; the EI city is handed its adults, the college town its students;
the 1900 arrivals are drawn by hand; the owners' listing is read at the month the founding causes it. LabourCheck's
known red ("...and not the whole mix") goes green: a departures-dominated month, not a model fault. The nine run in 96 s
where they took 267.

### 0.9.171 — MC1, a crunch no longer leaves households without a home — 2026-11-20, ready, see docs/queue/MC1-notes.md
While the bank's leverage ratio binds and it stands in its band, the mortgage desk has no capital room for a landlord's block, so it refuses the mortgage. The landlord's owners had been asked only for the down payment, which buys nothing once the loan is refused: on CU2's SHOCK seed 13, Real Estate built one block in 70 months while up to 174 households had no home. Now, while the desk would refuse the order's mortgage for the bank's capital and for nothing else (`BusinessDebtManager.mortgageShortOfCapital`, the door asked first on its own loan, recording nothing; never for a landlord banned or past its default point), `Game.consider()` asks the owners for all the till lacks on the order (`ownersAskOnMortgage`). The shares are sold households first and the world for the rest, as before. HN1's halving then finds an order the till covers outright. The bank's books and rules are untouched. `CreditCrunchCheck` plays the ensemble's seed 13, cuts the landlords' homes at m150 to the doubling valve's last 12 households, and holds the bank halfway up its leverage band for 120 months through its restore setters. With the mend the owners put in 13,431, homes go 497 to 1,522 and nobody is left with no home. Without it homes stand at 497 for a hundred months and 5.45 households are left with no home (m252). BF1's room to the leverage target, which leaves the band no insured share at all, is queued as MC2.

### RN1-H-M (untagged) — the old-works watch counts the works sent on — 2026-11-20, ready, see docs/queue/RN1-H-M-notes.md
Untagged: a harness and one map accessor, no model file. LongPlaytest's oldest-works watch named district (0,0)'s Industrial Bakery at m2400 on 2026-11-19's gates (RN1-H's city): the one box standing at m601 gone. No box had moved. The district's first bakery (m43) found no estate cell and its plan sent it on at its arrival; the second (m365) was the box drawn at m601; when the count fell 4 to 1 at m906-908, CL3's newest-first rule took m745, m744 and m365 and left m43, still sent on. The watch took the oldest of the district's own count to be drawn boxes. Now, of those oldest, the ones its plan had sent on are not its to stand (`CityMap.carriedOnLog`, read by the watch alone); the run and spill exclusions, the seed and the hard finding are as they were. MapCheck 26 causes the event on a one-district city: the law as it stood flags it, the law as it stands does not, and a drawn old box moved while the count holds is flagged by both. Main's playtest has not named the line since OP1's gate; the RN1-H-M known-reds row goes with this merge. The default playtest's trace is unmoved. And LongPlaytest.cleanUp, which harnesses call to delete their scratch cities, no longer throws when a file is renamed under its walk (the gate's EarthFoundingCheck: an autosave's .tmp, renamed by its writer); it walks again, up to 3 times.

### 0.9.170 — CPB2, no feed bought for undemanded output that cannot cover it — 2026-11-20, ready, see docs/queue/CPB2-notes.md
After CPB1's buffer gate, three paths still let a works buy imported feed for output that no one at home wanted and that the world paid less for than it cost. Sector now closes all three.
The export spare: a refused buffer became spare, and the spare was priced on the joint bill split by sales value. Beside a rod mill, an Electrolytic Refinery therefore ordered anode
for 1,684 t of cathode that shipped at 9, against 11.06 of anode a unit. While the month's feed is ordered, the spare must now cover its recipe too; feed already in hand still ships at
the marginal cost. No world outlet: vans, trucks, rolling stock and luxuries have no export price, and a NaN price never refused the feed. A buffer the world will not buy is now worth
nothing, so an undemanded van plant plans none where it planned 150 and bid 2,040 units of feed. Committed recovered copper: the order share is solved over each building's committed
floor, so no anode is bought for cathode the scrap already covers. In the default playtest the spare call changes in months 3-25 and the trucks' buffer from month 1,139.
Kept: demand, captive use, profitable runs, co-products, committed feed and sales from the shed. UndemandedFeedCheck proves both the mends and what is kept.

### FS7 (untagged) — freight, ports and the island ground cause their conditions — 2026-11-20, ready, see docs/queue/FS7-notes.md
The fifteen freight, port and island harnesses cause what they used to find by playing (the flaky-suite study's FS7 rows),
harness-only: no model class moves. A terminal, a bridge or a demolition is waited for in the builders' own work, twice the
points owed on site when the wait began (LandmassCheck.Wait), not 400, 600, 800 or 1,200 months measured once; the home fill
is read in the instant it is ordered (FF1's red, LandmassCheck 12 and IslandRail 8); Slope 9 buys its steep ground; the
railway's spurs are sized from the bulk the city offers, the sized railway's bill read off its quote's own step, the ports'
month handed to Ports, the trucks' legs to Haulage, the vans' and cars' buyers given their money. HomeRailCheck leaves
Observation (its run's railway counted, its identity asserted every month). Every FS7 known red goes green, CarCheck's five
from HS2's households among them.

### 0.9.169 — BI2, the Bank page's equity-movement alert relative to the movement — 2026-11-20, ready, see docs/queue/BI2-notes.md
The Bank page's third absolute $5 (BI1 took it off the two cards): "Something moved its equity without telling it", the waterfall's red "Not accounted for" step and the Statement page's line and alert tested the month's equity-movement residual against `.005`, so float dust lit them on a city of 1e13 and more (a double's step is .002 to .008 there, and the residual is a signed sum of seventeen figures), and the same $5 let a small bank's miss of a few dollars by. The verdict is now the model's, `Bank.EquityMovement.addsUp()`: `MoneyAudit.tolerance` of the movement's `scale()` (a cent, or a part in a trillion of the figures' size, never under 64 steps of a double there), BI1's rule with its finite guard, so a residual that is infinite or not a number shows. The screen reads it at its three sites and recomputes nothing; the figures it prints are unchanged. BankCheck causes it at both sizes with hand-built movements (a small one out by twice its bound shows; 4.5e13 with float dust only does not; the same truly out, and out by $1 billion, shows; an infinite and a missing residual show) and holds the played bank's 72 months to never showing it. The default playtest's trace is byte for byte main's, so no ensemble. A cent now bounds a small bank (about $0.26).

### FS15 (untagged) — the works and the services' town cause their conditions — 2026-11-20, ready, see docs/queue/FS15-notes.md
The sweep for the works and the services' town (the FS1 study, 6.14): five harnesses now cause the premises they had read off played towns.
ChildcareCheck's sections 2 and 4 stand on a founding built with no months, and its weighing asks the advice itself. The old copy predated HW1's fixed point (row 82).
WorksFinanceCheck's sections stand on foundings: two months struck, then their people, trend and year of GDP set by hand.
EssentialWorksCheck now sets five premises. The highway forecast's traffic is scaled between its two edges, found by bisection; the old calibration moved nothing in a town with modes.
It also sets the streets' share, the spare care (people scaled, workforce held) and the staff-short plant's workforce.
BusinessServicesCheck holds the sector whose two centres it places. AuctionCheck hands the households their spare and stands depots until the builders are not short.
AuctionCheck's Observations are hard fixtures now, and it leaves Observation.HARNESSES. Section 11's 1,200 months stay played.
The nine FS15 known reds are retired, none a model fault. The batch is harness-only: the model and the trace do not move.

### HL1-M — the railway the map draws is the railway the city holds — 2026-11-20, ready, see docs/queue/HL1-M-notes.md
Untagged: the map's runs and a harness, no model file. MapCheck 1's hard assertion called a city with one Rail Depot and no Rail Spur wrong (HL1's 120-month town at m121: 83 railway plots drawn, no model track, `trackSurplus()` 0). The map was right and the law was short a term: `CityRuns.layRail()` lays the main line to the city's edge for any city with a station whatever track the model has (FX5, "there is a railwawy depot but no rails"), so the railway drawn is the model's track less what the runs could not lay plus that line, the station's own. `CityRuns.stationLine(track)` names it (the mirror of `trackSurplus()`, derived, nothing saved) and `MapCheck.Drawn` counts it as the station's, exactly and only where a station stands: the painter draws every plot the runs lay, `rail + trackSurplus == track + stationLine`, and a line past the track needs a yard laid; no tolerance, no observation. MapCheck's new section 25 causes the case (one district owned whole, one Rail Depot: 132 plots, no siding, branch or quay spur; one Rail Spur: the track is the line's first 72 plots; 4 spurs: the surplus 156 and no station's line; the depot gone: the idle yard holds the line, then both go) and finds `Drawn.drawn()` counting a Rail Depot's yard once a tile it spans (3 for one), as the painter's `bpart` and the raster already do not. The default playtest's trace is main's, byte for byte (eight files, 2,405 months).

### 0.9.168 — BI1, the Bank page's integrity cards relative to the book — 2026-11-20, ready, see docs/queue/BI1-notes.md
The Bank page's two red cards, "Its lines do not add up" and "Its two parts do not add up to its equity", and the Balance sheet page's "Not accounted for" line tested the books' residual against an absolute $5, so float dust lit them on a big city: a double's own step is .002 to .008 at 1e13, and KeptCheck's city of 4,820 months holds books of 1e13 and more. The test is the suite's rule for comparing money now, `MoneyAudit.tolerance` (a cent, or a part in a trillion of the figures' size, never under 64 steps of a double there), and it lives in `Bank` behind public getters (`sheetAddsUp`, `yearAgoSheetAddsUp`, `equitySplitAddsUp`) that the screen reads and recomputes nothing. On a small bank the floor is a cent ($0.26) where it was $5, so a few dollars out of a book under $260 billion now shows. `BankCheck`'s new section builds a small bank $6 out (the cards show), a book of 1e13 and more with float dust only (they do not), the same book truly out (they do) and a $1 billion line left off a 5.8e13 sheet (they do: a part in ten million of it would not); with the old absolute test back, eight of its assertions fail. The default playtest's trace is main's, byte for byte (eight files, 2,405 months).

### GB3 — ensembles run on the gate box first, and BW2 — 2026-11-20, ready, see docs/queue/GB3-notes.md
A builder's 80-run ensemble (`tools/ensemble/ensemble.sh <label>`) runs on `gatebox` first, with the laptop as the overflow
(Jerus, 2026-11-20, "Gatebox first"). It hands its runs to `tools/gate-box.sh --ensemble`, GB1's road: the box plays the
branch's pushed commit eight runs at once (BO1-S's 80 in 17.5 min, against 116 on the laptop), and the runs come home to
`target/ensemble/<label>/` to be scored here as before. Gates and the nightly come first: an ensemble waits for them and
holds `~/cbs/box.lock` whole while it plays; when one comes, the runs started finish (277 s at the longest), the rest
give way, and it resumes after. It runs on the laptop and says why when two heavy jobs already run or wait on the box,
when the box is down (`jerus` mailed), or when it names its own data files. Measured first: the box's scores equal the
laptop's in 47 of 50 rows, the other three one ZERO_SLACK seed's minimums, within the ensemble's noise. With it, BW2: a
gate says "published" only when origin's main holds the batch's tip, and never on a dry run (HS2's dry run beside
FB13's ship was told "HS2 shipped"). Scripts and docs only.

### GB4 — the gate box's measurements on record — 2026-11-20, ready, see docs/queue/GB4-notes.md
`docs/gatebox.md` holds what the gate box is and what its first night measured (Jerus, 2026-11-20: "put it on github
so that if other sessions ask its there"). It covers the machine, its 7-hour soak (every test passed, 83.8 °C at
most), one job alone (3% from best core to worst, +30% beside its own core's other thread) and many at once. 16 jobs
at once run about 40% slower each, yet finish about 30% sooner than two rounds of 8. A gate someone waits on runs at
most 8 at once, one per core; a pile of independent runs fills all 16 threads. The contract's gate box bullet points
to it. Docs only.

### 0.9.167 — HS2, two adult stages — 2026-11-20, ready, see docs/queue/HS2-notes.md
The adults are two stages: the young, 18-34, who move up at 1/204 a month, and the established, 35-69, who retire at
1/420 (at 70, as before). The young form seven of the eleven household shapes and carry half the babies, a quarter of
the children and no teens. The books keep the young in 42 working cells beside the established's 66, the pool's group
is (tier, stage), and the young who turn 35 carry their money with them, HL1's identity closing every month. A theft
no household could take home is no longer taken. Saves are format 58 (57 no longer loads). The 80 pass every hard
criterion but end smaller, the final population 0.748 of BO1-S's in AUTO2 and 0.745 in ZERO_SLACK, so both T2 lines
(soft) now FAIL. Why: a young city's adults are mostly young and the young do not retire, so it keeps fewer seniors
(9% of the people against 11%) and more adults want the same jobs (unemployment 9.3% against 6.7% in m720-1440, where
the size is lost). The retired are richer (0.74 of the workers' net worth against 0.56). Twice the cells need twice
the households before each holds half of one and shops: the 1900 village keeps 13% of its households in such cells at
m12 (main 9%), 3% at m60 (2%), and is no smaller (544 people at m120 against 504).

### EDU1 — the poverty trap's gap in EducationCheck — 2026-11-20, ready, no tag (harness), see docs/queue/EDU1-notes.md
EducationCheck's "a poor city at x3 enrols fewer than at x1: the trap is back" was red on main from CU2 (0.9.130), seen only at R6b's pick, the harness being nightly.
Bisected on the kept gate runs and probes: green at SQ1's and DQ1's merges (x1 1,673, x3 1,438), red at CU2's (1,652 / 1,632). The model did not move: at x3 the
willing share still falls from .72 to .35 and the month's intake to about half of x1's. The chained world prices make the fixture city a sixth larger at five years
(23,011 people at x3 against 19,779), so x3's half-rate intake filled the one university that x1 and x0 already filled. Section 15's three cities now build three
universities, as the rest of the harness does, stepped by the month, with a fixture line that none filled at x1 or x3 (at most .72 and .32 of the staffed seats).
The trap line is unchanged: 3,307 at x1, 1,543 at x3, 3,867 at x0. It goes red with one university, where the fixture line names why, and with the fee kept out of
affordability. EducationCheck's known red is retired.

### 0.9.166 — R2e-PE4, the Cement Plant calibrated rotary-first — 2026-11-20, ready, see docs/queue/R2e-PE4-notes.md
The Cement Plant's PE4 (Edison's long kiln, 1902) is defined against its rotary kiln now, as R2e and R1d recommended: a
plant on the rotary kiln without PE4 burns the study's 30% of its cement in coal (7,500 t a month, was 6,160), the long
kiln saves 394 t of it (to E2's exact 7.0/3.3 wet form, 7,106 t) and E2's preheater the rest, to today's 3,350 t. Every
kiln step saves on the one before in either learning order, so a 1900 plant fits its long kiln from June 1902 and pays
PE4's royalty on the saving, where the refit was a loss before and was refused. Today's figures, the Large Cement Plant
and the Lime Kiln do not move; the shaft kilns keep R1c's 1,340 t over the rotary (8,840 t before PE4, was 7,500).
PostwarRecipeCheck's kiln controls follow the new calibration, each red on the old one (15 of 228), and a new section
fits a standing plant's long kiln through the crews' months and reads its royalty. The default playtest's trace is
main's, byte for byte (eight files, 2,405 months).

### FS2 (untagged) — the twins and the scale cause their conditions — 2026-11-20, ready, see docs/queue/FS2-notes.md
ScaleCheck, PriceLevelCheck and DenominationCheck cause their conditions; harness-only, the model and the trace unmoved. All fifteen
FS2 rows of known-reds.txt go green. PriceLevelCheck runs three nudges a level and compares each twin through the month before any two
cities of one level part (a parting across the levels alone fails); its history is cut by what each list held, the town's reload is
read by a rerun to that month, and the lanes are given (twice and half the floor), not found. DenominationCheck's card gives its
partner twice the floor. ScaleCheck strikes its copy at the first month from 225 that has its premises (none in 60 fails as a
fixture), reads section 9 at ten copies a side (the K = 1 city's whole units fall with them: savings 1.12e-2 to 9.05e-4) with a
treasury left unscaled shown caught, and plays the 1900 town on the literals (the world's band is no K's: prices 1.09e-3 to 2e-5).

### SB1 — a template has one stack, the premise closed — 2026-11-20, ready, see docs/queue/SB1-notes.md
SB1 queued `BuildingManager.retire()` to scrap from every stack of a template, not only the first, after HS1's OutsideCheck fixture read its early return as a fault. It is not one: `addStack` is the only place a stack is made, and it merges an order into the stack of the template's name, so a second order, a building wave, an order on site and every load add to the one stack, and a save keys a stack by its id. No city has a template with two stacks, `retire()`'s return is exact, and the model and its default trace are unchanged. `BuildingStackCheck` section 10 is the finding and its tripwire: every template of the catalogue ordered three ways stands in one stack; a played city's stack count read at the end of each of 24 months, saved and loaded, never has two of a template; and the closing path (`Game.demolish`, the next month's close) and the shedding path (`Game.retire`) take the count asked, up to what stands, leave the emptied stack in place and book the count, the ground, the posts, the log line and the salvage at what was scrapped. Run with `addStack` not merging and `retire` returning the count asked, 18 lines go red. The day a second stack of one template can stand, it goes red there, and `retire()` is the first thing to mend.

### 0.9.165 — FB14, gold: GD3's review and the start-on-gold box — 2026-11-20, ready, see docs/queue/FB14-notes.md
The founding screen has a "Start on gold" box under the money: the city opens on Gold, Defend the gold its bank's aim, with
the law's 40% of its treasury in gold and never more than the vault, so every preset's whole vault (Standard's covers 25%, and
the box's line says so; a deeper custom vault keeps the rest in dollars). Selling gold now shrinks M0: the next month's top
retires what the mint paid out, from the reserves, and the audit says it twice; until now the bank's equity paid it and the
cover drained faster than a real gold standard's. Gold's cover test retires only what the reserves can, and the cover, the free
gold and the pace read M0 after the last sale's retirement. A gold break reads "came off gold", not "the peg broke", and Gold
has a thin-cover flag. Known, and opened by the box: a city founded on gold the default way can take M0 under zero from about
month 161 (GM1). Saves: no format bump; older saves read nothing retired for gold and every break a peg's. The playtest does not
move: its city is on Lean.

### 0.9.164 — SQ1b, a month's material draw past 2^31 and buildStack's harness — 2026-11-20, ready, see docs/queue/SQ1b-notes.md
`Game.drawMaterials` rounded a month's draw of material into an int, so a draw past 2^31 units wrapped (3 x 2^31 became
a negative count): the whole draw went unbought and unbooked. The draw is a long end to end now: the yard
(`BuildingManager.takeFromYard`), the yard's delivery to a new order's sites, the run invoice's reduction of the yard,
the quote's yard, and the month's tally `materialsConsumed`, which is saved (the yard's stock and the tally are longs
in `DataSave`; an old save's int reads into one, no format bump). `BuildingStackCheck` sections 7 and 8 order 3.2e8
Houses through `Game.buildStack` on a copy whose stack stands just under the ceiling and read back its quote, its
ground, the cash it charged, the receipt, the yard's share and the run's invoice, then draw 6.4e9 units twice (the sites'
and a repair's, yard first); each old int put back turns its own assertions red. The default playtest's trace is main's,
byte for byte (eight files, 2,405 months).

### 0.9.163 — FB13, the island's words, the Government screen's coupons and the build card's licence line — 2026-11-20, ready, see docs/queue/FB13-notes.md
Three screens that kept words from before the model changed under them now say what it says. Where AL1's rule would link an unlinked island by a bridge, the Land tab's "linked" cell and tooltip, the map's chip (a third line), the island's notice and its door ("Build a bridge there ›", "See the bridge ›" while one is built) and the not-enough-land page name the bridge and its crossing first and the port after it, and the Build tab's Overview gains AN ISLAND TO LINK, a card for the link the rule would order (the island and its ground, the bridge or the two terminals, the quote's price and wait, a door to the building's card with the island chosen); where the rule links by port, or cannot, every word is the old one. The screens read the rule through its own fresh memory of a refusal and a memory of their own keyed by ground, so they say what it will order and an open screen never moves when it surveys. The Government screen's "...of which paid abroad" row says "in D$, at the month's rates · paper held now in US dollars, euros and yen", not "in dollars". The build card gains the licence line of research-design mockup 5, drawn from the patents the planners' licence is made of (the building's, its recipe's, a fitted piece's; LicenceFees.rentOnNew()): a quarter of what it adds goes abroad until the month the patent ends, or on N patents with the first and last ending, with the monthly fee, or none (found here, found since, not patented, ended). The default trace is byte for byte main's. FB13Check holds all of it.

### GB2 — the nightly runs on the gate box — 2026-11-20, ready, see docs/queue/GB2-notes.md
The nightly (`tools/nightly.sh`: the 4,000-month playtest, the nightly tier and the hard timing) runs on `gatebox`,
the quiet box that judges speed best (Jerus, 2026-11-20: "the nightly timing run needs to also move to the gatebox").
It hands its night to `tools/gate-box.sh --nightly`, GB1's way to the box: this checkout's commit of `tools/` runs
there, and its red mail comes home through GB1's relay. The box is held whole: a gate holds `~/cbs/box.lock` shared
and the night exclusive, so whichever comes first runs and the other waits. A second night is refused. The verdict,
log, tier and observation records come home, where `day.sh` reads them, and the timing figures stay per machine. The
box's first night is its baseline. The box, on UTC, runs a night in the caller's time zone, so it is dated as `day.sh`
asks. When no box answers, `jerus` is mailed and the night runs on the laptop. It starts by itself, a laptop task at
02:30 on `day.sh`'s `nightly due`, now only while the last night ran on another `VERSION` (Jerus, 2026-11-20: "it
should run every night IF the version number has changed"). GB1's relay passes the box's paths to MemWatch unconverted
(Git Bash had made `/home/jerus/...` `C:/Program Files/Git/home/jerus/...`). Scripts and docs only.

### PZ1 — PrizeCheck 12: one lab a field a month, caused again — 2026-11-20, ready, see docs/queue/PZ1-notes.md
PrizeCheck section 12's medical village, which must want two labs and order one, ordered none in its month since
BO1-S: with the owner line on the founders' shares and dividends are the owner cell's, which takes no offering, so the
trading households put up $962,726 of the $3.3M the lab needed and the order held, as the model's "owners first, never
a loan" says. The model never orders two (one order a field a month); the fixture no longer caused a purse that could
pay. The village now gets the liquidity buffer the section's other villages already had, for each lab it wants: one
the first month, the second the next. Harness only.

### 0.9.162 — BO1-S, the owner line switched on — 2026-11-20, ready, see docs/queue/BO1-S-notes.md
The owner families are on in every city: one two-adult household for each listed company, holding
its founders' shares alone, living on its dividends and selling a twentieth of each stake a year;
everyone else buys at the offerings and on the exchange. They take no job, and in a small town
they are fewer: never more than one adult in twenty, so a town under 20 adults forms no family
(under one adult is still kept for it, off work) and its companies list once it has grown to
their families. The People screen shows them outside the families: a piece of the mosaic, a
column of the outside table, a row below the rule on Household money. A theft no offender could
be handed is no longer taken (it unbalanced a tiny town's books). Saves: no format bump; an older
save's companies keep their founders' shares with the households; the year book takes the owners off
its labour force, as the model does (a new history series, the owners' adults).
Ensemble (docs/ensemble/0.9.162.md): M1 and F1 pass in every policy; AUTO2 ends at 2.44 million
people (OP1 2.85); the playtest city with the line on is a fifth smaller at month 121.

### GB1 — the gate runs on the gate box — 2026-11-20, ready, see docs/queue/GB1-notes.md
The gate and its dry runs run on `gatebox`, the quiet Ryzen box beside the laptop (Jerus, 2026-11-20). `tools/gate.sh`
hands its whole call to the new `tools/gate-box.sh`, which runs this checkout's commit of `tools/` there over SSH, its
runs in `~/cbs/runs`, started headless exactly as before. Its output comes back as written and its bus calls are posted
here, so MemWatch's gate and batch state read as before, with `machine=gatebox`. The run's files, but not its clone, are
copied home at the end. A dropped connection stops the gate there, as its caller's exit does. When no box answers,
`jerus` is mailed and the gate runs on the laptop ("notify me then run on laptop"). The box pushes with its own write
deploy key. The heavy slot is per machine. Scripts and docs only.

### 0.9.161 — OP1, every policy open from the first month — 2026-11-19, ready, see docs/queue/OP1-notes.md
Every Policy lever is open from a city's founding, in every city. The profit, wage and sales taxes
open at 15%; the pension, its contribution, EI, student aid and the health premium open at zero. The
minimum wage binds and zoning is free. Money's dial, target, holdings and regime are the player's
(or the rule's) from January 1900. The Policy, Zoning and Trade screens show no dates or shut cards,
and the inbox's "a lever opens" notices are gone. A law find opens nothing now; its text says so.
History's regimes stay history's: a 1900 city founds on gold at par, tied to the US rate; 1914 and
1931 float a dollar still on it, 1926 puts a floating one back, and a player may leave it any month.
PolicyOpenCheck replaces PolicyDatesCheck. No format bump: an old save's openings are ignored.

### 0.9.160 — R1e, a new building's equipment priced and saved at its order — 2026-11-19, ready, see docs/queue/R1e-notes.md
A new building's equipment is now chosen when its order is quoted and
accepted, not when it opens. In a city founded the 1900 way, where a plant
ordered now may go without a piece that does not pay, the quote prices the
shell and the pieces the order keeps (a piece's part of the price its goods
at the catalogue's own prices and its work at the template's own price a
point); the builders bill that, the site owes that work, the tax and the
investment are booked on it, the set is saved and delivered at the finish.
A discovery or a price change afterwards changes a new order only. The
investors, the money test, the wait, the auto-builder and a stopped site's
restart read the same set. A city founded the default way is unchanged, its
traces byte for byte.

### 0.9.159 — HS1, money follows people — 2026-11-19, ready, see docs/queue/HS1-notes.md
The month's flows now carry their money. A household that retires, ages on, loses a partner, dies
alone or leaves takes its cell's average of every stock with it before the census's pool nets the
rest. A survivor keeps everything. An estate repays the bank from its savings and passes the rest
to the working households of the dead's former tier. Leavers take theirs out through the balance of
payments, so HL1's identity closes as before. The retired keep their former tier: 24 cells, 99 in
all, shown on the People screen as "Senior couple, formerly Skilled". An orphan cell weighs by its
people for what a child can hold, never a debt, a loan or a car (OH1: the register stays whole). HouseholdFlowCheck causes each flow. Save format 57,
floor 57: an older save's retired money would load into no cell. Ensemble: M1 passes. F1's one
finding (ZERO_HOT seed 0, 1.6 households with no home in months 205-207) is FB11's held land,
passed by name. The towns take off at main's pace (T1 by m786, against m788). Unemployment runs
1.6-6.6 points higher: the retired now pay for their care, so the senior care pull draws more
people to the homes. AUTO2's final population (T2, information) is 65% of main's, two long stalls
in sixteen against one.

### 0.9.158 — QN1, new-building proposals beside canonical templates — 2026-11-19, ready, see docs/queue/QN1-notes.md
A new building is now valued as it would run, beside the template it is
of: in a city founded the 1900 way, where a plant ordered now may lack a
piece the standing fleet has or run another process, the investors'
estimates, the staffing gate, the licences, the planners' candidates and
order sizes, the posts and power on site, the Build card, the advice and
the auto-builder read the new building's own posts, draw, output and
inputs (NewUnit). Orders, stacks and sites keep the canonical template;
standing buildings keep their figures. The bank opens a branch when fees
cover the branches standing plus the new one's own cost, counted once.
A city founded the default way is unchanged, its traces byte for byte.

### 0.9.157 — RN1-H, the homes' headroom a margin over demand — 2026-11-19, ready, see docs/queue/RN1-H-notes.md
Homes now plan capacity at demand plus `BusinessInvestment.TARGET_HEADROOM`, counting standing homes and those on site once. Previously the planner waited until demand exceeded capacity by that margin, then ordered only to bare demand. The planner's quantity and the credit check use the same target. HousingCheck causes equal demand and capacity, orders exactly the margin, and proves that planned and completed reserve homes stop further orders. DiscoveryCheck includes headroom in the capacity required by research jobs. The other planners' readings were audited and left unchanged.

### 0.9.156 — HL1-R, Business Services' kept share without a cliff — 2026-11-19, ready, see docs/queue/HL1-R-notes.md
HL1 kept a quarter of the city's whole upkeep in Business Services from the first seat and none before: a margin with no
capacity behind it. It now keeps the quarter (`UPKEEP_IMPORTED`'s dial) or what its seats can serve of the month, whichever is
less: `upkeepCapacity()`, every seat at what the world pays for a seat-month, the bank's bill and the services' sharing it,
and imports the rest. No seats: all imported, as before; seats enough: the same quarter, to the bit. A city with a few seats
and a large upkeep keeps less and imports more. The audit's lines and the households' identity are as HL1 left them. The
default trace is byte-identical to main's (its city has 130,200 seats), so no ensemble. `BusinessServicesCheck` section 7
causes none, one and enough seats and fails with the cliff put back. No save change.

### 0.9.155 — SZ1, steps and lots that scale — 2026-11-19, ready, see docs/queue/SZ1-notes.md
Quantity and money buttons follow the figure's leading unit and its tenth,
never below the control's least step: building, paving, land, borrowing,
fund tickets and transfers, and reserves. The five city-paper lots follow
this month's prices, capped by the minimum issue; the player's lot rounds
down to 1-2-5. Model funding, including initial offers, keeps the exact
priced and capped amount to preserve dollar scaling. Loading restores care
offered and affordability, so automatic building sees the same shortfall.

### FS1 — the tests that break when another batch moves the played city, a study — 2026-11-19, ready, see docs/queue/FS1-notes.md
`docs/studies/flaky-suite.md` and its three tables in `docs/studies/flaky-suite/`: every harness in the suite (224, 2,619
sections on 0.9.153) sorted by how each section gets its condition. 2,027 are caused; 543 read a played city (identity
167, found 258, picked 104, order 14; 66 already Observations), and of those 425 can cause their condition while 118 are
identities that rightly stay played; 77 are on the wall clock, most through `Timing` already. Sixteen sweeps (FS2-FS17)
queued after HL1, each a gate of its picked harnesses, grouped by the fixture builders they share and ordered by today's
reds. They take HP1, HP2, SX2, KFP1, FF1, PK1, KRT1, KBP1, KRW1, QW1, MQ1, BQ1, LM1-R and KS1-rest, and the 44 known-reds
rows those rows and HL1's three FS1 rows carry are re-filed to them; PX2 is a real fault and stays. One call (FS1-C): a
plain wall-clock wait becomes a clock-free wait with a hang guard, its speed a `Timing` line. No code.

### 0.9.154 — HL1, the households' books closed — 2026-11-18, ready, see docs/queue/HL1-notes.md
Every dollar households gain or lose now has a line: their savings identity is struck beside the pools each month
and asserted by LongPlaytest and the ensemble. Care, schools, research, safety and transit buy their upkeep from
Business Services, three quarters imported with seats and all of it without; the bank buys its upkeep too, on a
separate input excluded from GDP imports because its output is in no GDP term. A bankrupt's savings repay its debt
before the rest is written off, and savings above the debt stay theirs. Migrants' money and household share trades
with the world reach the balance of payments. Nine household faults are named and bounded for HL2. Current main's
public patent imports, gold minting and noncash valuations are retained; LF1b's duplicate share pairs are kept once.
New caused checks close the household identity through public licences, noncash short closures and HN1 mortgage trims.
The final 80 score 23/26 beside main's 23/26, hard 10/10 PASS, no changed verdicts or findings; every month closes.
All 2,404 default months close; the kept full suite is rejudged with named FS1 findings and the map discrepancy queued as HL1-M.

### 0.9.153 — R6d, research: the speculative rows with something behind them — 2026-11-18, ready, see docs/queue/R6d-notes.md
The speculative tier's first two rows, each projected in R6b's window and unknown to a city founded today's way (the
default playtest is unchanged to the bit: all eight trace CSVs equal main's). **FE8**, driverless field work (projected
2033, window 2030-2036; Deere's goal of an autonomous corn and soybean season): the Grain Farm and the Mixed Farm carry
an autonomy kit, and a farm with it strikes the operator of each tractor and combine from its posts and asks for a
tenth of a post's watch in his place (an estimate: no builder gives one); R2-R's gain in hands was in with R1d. **FE10**,
electrolysis at 46 kWh a kilogram of hydrogen against 55 (projected 2034, window 2031-2037; the US energy department's
target): FE4's Hydrogen Steel Works carries an electrolyser that takes 0.486 MWh off each tonne of steel, its bill and the grid's demand;
it pays at about four times the model's tariff, not at its own. Each page was opened again and says as the study did. Saves go to
format 56 and the floor with it: a format-55 save holds no count of either piece unfitted. New: `FieldOperatorsCheck`,
`ElectrolyserCheck`.

### 0.9.152 — LM1-D, the founding's water widened to six blocks — 2026-11-18, ready, see docs/queue/LM1-notes.md
A new city whose water within `CityLand.FRESH_PATCH` (3 blocks) of its first fresh block holds less than one plant's (A24)
now widens the square a block at a time to `FRESH_PATCH_MOST` (6) and takes the first that holds it (`freshWater()`): a
river 1 to 5 plots wide held 0.14 to 0.18 km2 in 3 blocks, one plant about 30% of its 60,000 units. A river alone still
falls short at 6 and buys the rest (Paris 0.33 km2 of 0.49, Rome 0.25; they would hold it at 10 and 14 blocks), and 41 of
200 generated worlds start short where 75 did. The default world and Stockholm hold it in 3: no change, the trace
byte-identical to 0.9.150's. New: LandCheck (e), the widening on painted rivers; EarthTerrainCheck's Paris restated;
MapCheck 2h-c's first view held to the watched city's sidecar (a first view appends the walls its plans find, DM2-A).
Stopped by Jerus on 2026-11-11, merged with 0.9.150.

### FB12 — fixtures on two landmasses — 2026-11-18, ready, see docs/queue/FB12-notes.md
IslandRailCheck proves a bridge-linked map island's founding-side hub, own
track and yard on BridgeMapCheck's real terrain. KeptCheck owns ground on a
second Earth landmass and checks the land office's actual table, its model
values and its retained nodes through redraws and a changed month. Harnesses
only; no game class changes.

### 0.9.151 — HN1, households housed before the matching homes finish — 2026-11-18, ready, see docs/queue/HN1-notes.md
The landlords' order is no longer held whole when the lender's door refuses it. The plan sized it on the till and the
lender's test, but the door said no to all of it: for the bank's capital while its leverage ratio binds, or for the down
payment at the purchase's own higher price. On the ensemble's AUTO2 seed 12 that held 26-35 Low-Rise Apartments a month
from m699 to m746 with five blocks' price in the till. Doubling up filled to its ceiling, and from m744 up to 13
households had no door. A no at the door is now asked again of half the order, down to one, and the advisor's line says
the order was trimmed. HousingDoorCheck plays seed 12 on today's main and fails without the mend: 5 months held a
landlord that could buy one outright while families were short of doors. With the mend the R0h path has no finding in
748 months. LongPlaytest's watch, seed and floor are unchanged. The default playtest is main's to m1683 and parts at
m1684, when the door writes 2 of the 5 homes main held whole; the landlords are held for the bank's leverage in 3 months
against main's 34. The ensemble passes (M1 0, F1 0/16) with larger cities; paired with main, debt and claims do not rise.

### BW1 — the workflow says where it is — 2026-11-18, ready, see docs/queue/BW1-notes.md
The workflow tells MemWatch where it is instead of MemWatch guessing from logs: `tools/bus.sh` is its one way to
`cbs-bus`, doing nothing where there is none and never failing a gate or a check. `tools/gate.sh` and `ship.sh` post
each step the gate begins (merge, suite, maps, tag, push) to `state\gate.txt` with the commits, main's version, the
machine, the log, the gate's pid and start, then done or stopped with the outcome, published, reused, skipped,
timed_out and known_reds; a real gate moves the batch's own file to gating, then shipped with its version or
sent-back. A publication is one info message to the batch, a stop one urgent instruction holding its reason and every
unnamed line. The nightly mails `jerus` and the gate's keeper when the playtest is red or a timing line slower, naming
the batch. The contract gives the builders' state lines (building, ready, blocked, held); `docs/orchestrator.md`'s
waking is cut to its fallback; `ship.sh`'s stops cite `docs/contract.md`. Scripts and docs only.

### R1f — the playtest's harnesses on the 1900 city — 2026-11-18, ready, see docs/queue/R1f-notes.md
The playtest's seven harnesses now exercise explicit 1900 foundings beside their modern cities. The exchange-rate check counts the existing hold move through the 1914 suspension and 1926 return; the scale copy preserves unfitted plant, counts horse hands beyond the buildings and bounds whole posts while retaining every economic tracking limit. Power building, bill auctions, steep ground, the historical price level and the university pipeline have separate historical sections. The game model and default playtest are unchanged.

### WG1-R — WG1's review fixtures cause their own conditions — 2026-11-18, ready, see docs/queue/WG1-R-notes.md
WG1-D's five going-rate pins (CurveCheck 7, CentralBankCheck 20, HoldersCheck 12-13, HousingCheck's closed homes, KeptCheck 64) were each
taken out and the section run on the default floor (.85 of the going rate): four read as written, and their pins, out of date since GW1,
are gone. CurveCheck 7's steep dial is held two points under the dial the market expects to settle at, with its control (the same city a
month before the hold: 0.50 points from the 1-month to the 10-year, 2.0 asked); HousingCheck counts the months that met the retirement's
own conditions (56 of 120); KeptCheck 63's fund is paid in from a treasury the fixture makes have the cash (the default floor's stood at
0.0 and the fund sold to pay), and 64 says whether the figures its desk draws moved (6 of 6). KeptCheck's city keeps its floor at the
going rate for two lines further on (queued KFP1). What the pins recorded about .85 does not reproduce on 0.9.149: a city held at 15%
closes 0.713 of its debt's homes on either floor, and the bond desk redraws 6 of 6 months. Harness only; no game class changed.

### 0.9.150 — R2c-P, public works' licence fees — 2026-11-18, ready, see docs/queue/R2c-P-notes.md
In a city founded the 1900 way the city's own works now pay R2c's patent rent: a patented entry its utilities use (rapid sand, PE5;
the steam turbine and pulverised coal, PE6 and PE16; the desalination plant, E6; the wind farm, E16) pays `LicenceFees.SHARE` of its
positive public gain until `TERM_MONTHS` after history's month. The gain is the water or power it delivers over what the city would get
without it, at the planners' tariff, so surplus capacity adds nothing and a building's running cost comes off. Local finds and
unpatented entries pay nothing; no berth carries a patented entry, so Freight pays nothing it never earned. Captured at production, the
bill is paid at the next strike on the city's services line, in the same month's audit as trade, the foreign accounts, the imports and
a "Patent licences" budget line; saved, no `SAVE_FORMAT` change. The public fitting verdict, a new utility's running cost and the works
value net the bill the month would strike, only while the patent runs. `PublicLicenceCheck` holds it (67 assertions); a city founded today is untouched.

### 0.9.149 — FB9, the map's renderer and MapCheck's fixtures — 2026-11-18, ready, see docs/queue/FB9-notes.md
A mine born on a site after a cell's street was laid across it kept the street in the plan, where the buildings beside it
stand by it, but the painter stood the mine over the whole site and drew the street as the mine: its surface counted and
not drawn (paved 153 of 156 on main's played city), the street beyond the mine a piece apart, the buildings it served
beyond reach. The map now draws the street through the mine (TilePainter, and TileRaster keeps its pixels inside the
mine's box); no building moves and the plans are as they were. MapCheck 8d causes it on any path, plot and pixel; 2h-c's
mine is born under houses; 14 lays a box on every offer and one across two besides its 200 scattered ones; MapCheck stops
the JavaFX toolkit before it exits. The 8 known reds filed under FB9 are green; the map only, the playtest unmoved.

### WC1 — one contract for both builders — 2026-11-18, ready, see docs/queue/WC1-notes.md
The workflow redesign's last step (Jerus, 2026-11-11): `docs/contract.md`, one file Claude and Codex builders both
read, holds his standing rules (all of `CLAUDE.md`'s, unchanged in substance) and the process, with every decision of
`docs/queue/WC1.md` in his words, the night practice (mends and ready mails, a dry run only for a pick of ALL or a
publisher edit, the gate-run line first, no JVM beyond `--fast` during a gate, foreground subagents, outside reds named
under a queued fix, `--red-ok` only on his own word), five rules that lived only in memory notes, and the calls still
his, unanswered. `CLAUDE.md` is now the map and the tools, `docs/orchestrator.md` the orchestrator's own day,
`docs/deepseek.md` the DeepSeek tool, `docs/batch.md` a pointer; `AGENTS.md` and the agents' files point at the
contract. RL1's machinery for building on an unshipped branch goes (`tools/carries.sh` and its uses in `ship.sh`,
`pick.sh`, `wait.sh`, `day.sh`). No game code changes; 163 lines fewer over the process files.

### TW1 — TreasuryCheck's dollar-note fixture causes its own window — 2026-11-18, ready, see docs/queue/TW1-notes.md
TreasuryCheck's rollover section no longer lets the path decide whether the borrowing window is open as its dollar note
falls due. It asked for a flat D$500k six-month note and asserted the window open; the window shuts when a year's
service on what is owed abroad is more than DebtManager.WINDOW_SHUT_SERVICE_SHARE of a year's exports, and the exports
are the path's, so TQ1's path shut it (five assertions red on dc8f4da6) and DQ1 and CU2 opened it again. The note is now
sized from the city's own exports to half that share, and a save of the same city at the same month gets one sized to
twice it, with the window shut and the dollar note rolled at home as the model rolls it; the D$ is pinned in both so
the share at the maturity is the one set. Every law asserted before is kept; 17 assertions are added and each fails with
its cause removed. On dc8f4da6 the harness passes. No model change: the default trace is unmoved, no ensemble.

### 0.9.148 — TR2-G, replay's copy leaves the player's prefs alone — 2026-11-18, ready, see docs/queue/TR2-G-notes.md
In a replay the window takes a copy of the player's prefs for the recorded
city's copy, and puts his back on leaving: a click in the copy on the drawer's
pin, the construction tab, a map toggle or the Reports' pins changes the copy's
values only, never the player's in memory (where the next prefs save, after
leaving, took them to his own `settings.json`; TR1's viewer clicks did the same
since 0.9.77). The copy never writes a prefs file anywhere; his own clicks
outside the copy save as before, and his F11 in a replay is not remembered.
ReplayCheck 11 (the model, and the real window) clicks the pin and the tab in
and out of the copy and fails with the mend removed. No model change: the
default trace did not move, no ensemble.

### 0.9.147 — LB5-B, a slot's previous city kept whole while its history lands — 2026-11-18, ready, see docs/queue/LB5-B-notes.md
While a saved slot's header month and its history's last month disagree (a save landed before its history, SaveWriter's order, or its
history write failed, LB3-S), the Rankings board keeps that slot's last whole city, header and history together, instead of the new
header's name and land over the old history's figures. A whole slot's two months are equal, so a healthy slot reads as before; a slot
first read with them apart is read as it stands (no whole city of it exists). The held row is read again only when a file moves (the
stamp), so a history that never lands leaves the slot at its last whole city until the next save lands both. The mend is
`SavedCities.keepWhole`; the screen is untouched, nothing new is saved and `SAVE_FORMAT` is unchanged. `RankingsCheck` 21 holds the row
between the save and its history and 22 the failed history, a second save, the landing, a newer build and a deletion; 8 assertions
fail with the mend removed. No model change: the default trace is unmoved, no ensemble.

### 0.9.146 — CPB1, the cathode buffer at a loss — 2026-11-18, ready, see docs/queue/CPB1-notes.md
The zero-demand cathode buffer no longer buys anode for an empty yard at a negative feed spread. Sector's planner gates speculative stock fills while domestic and captive demand and profitable replenishment keep running; recovered copper already committed is transformed by its own works without starting an idle refinery beside it. Real-market checks reproduce the old loss, preserve feed/output and GDP identities, and retain the original copper and cement premises as regressions. Existing stock sales and national-account rules are unchanged; the separate export-spare and no-world-outlet paths are queued as CPB2.

### 0.9.145 — FB10, the research checks and the judge — 2026-11-18, ready, see docs/queue/FB10-notes.md
R2b-J: the judge read a harness's own tally, "ResearchViewCheck: 1 FAILED of 217", as an assertion, so a harness whose
every assertion was known stayed NEW on its count. `KnownReds.count()` takes a tally headed by the line's own harness,
with or without its total (YoungBankCheck prints none), live and in a kept run alike; a tally under another name is an
assertion, and an uncovered assertion keeps its exit and tally NEW (KnownRedsCheck 8, 7 of 11 red without the mend).
R2b-V: ResearchViewCheck 6's sector filter holds over every sector the city's stacks stand in, and a second research
town builds a known Fabrication Shop, a third sector beside the Bakery's and the farms'. RT2-P: a lab order's cost
moves within the month (597.69 before it, 612.88 at the order), so `Game.getLabQuote()` keeps each field's quote, not
saved, and PrizeCheck 12 caps the owners' raise by it. PrizeCheck 20's cushion law leaves the bundle as R3b-R, the labs'
stake reserved from the Exchange's buyback. Checks and one unsaved record: the trace is unmoved.

### 0.9.144 — CR1-J, source archives beside the exe and the OpenJDK runtime — 2026-11-18, ready, see docs/queue/CR1-J-notes.md
The sold exe now runs on Eclipse Temurin 21.0.13+9, an OpenJDK build under
the GPL with the Classpath Exception, not Oracle's runtime, whose No-Fee
Terms bar a sold product. Build EXE.bat names that JDK and checks it, and
ships beside the exe, in source\, the complete source of OpenJFX 21+31 and
of the runtime with the scripts that built it, pinned by SHA-256 and fetched
once into a cache outside git. The exe and Credits carry the game's own
copyright line; Credits adds the runtime's notices, read from the image the
game runs on, Microsoft's terms for its C and C++ runtime DLLs (the Visual
C++ and Windows SDK licences), and where the source is. A missing notice,
terms file or archive stops the build with nothing published (PackagingCheck).

### DM1-P — batch-check.sh names the Earth and world files — 2026-11-18, ready, see docs/queue/DM1-P-notes.md
Batch checks fill missing Earth and world properties from the checkout's data, falling back to the main checkout.
Batch checks, nightly and the gate share one helper. Unrelated Java flags and explicit data paths are preserved;
Windows paths use C:/ form, and one line labels each property named, already set, or not found.

### 0.9.143 — CR1-L, the libraries' notices on the Credits page — 2026-11-18, ready, see docs/queue/CR1-L-notes.md
The Credits page now shows Gson's Apache 2.0 licence and OpenJFX's GPL v2
with the Classpath Exception, its additional notices and the graphics
module's JPEG and Mesa notices, after the fonts. These releases omit legal
files from their jars, so the build packages one exact upstream release
copy of each; CreditsCheck pins their versions, hashes and full page text.
Runtime notices, source delivery and the Microsoft DLL terms await CR1-J.

### 0.9.142 — FB8, DM2's three follow-ups, the map's sidecar — 2026-11-17, ready, see docs/queue/FB8-notes.md
DM2-B: LX1's 4,000-month city refused its own sidecar at m3589 and m3952 because at m3551 the map wrote a wrong site
history: `relay()`, laying a resource's works again, took a district's last site runs off without moving its version,
so its kept site list still drew the mines, the plans over it stood, and the month's history saw no change there.
`relay()` moves it now, as `markRun()` does: LX1's tree reads back 10 of 10 reloads (8 before); MapCheck 4c causes the
route on the iron square (3 lines red before). The whole-sidecar refusal stays, documented on `matchesCurrent()`: the
one mismatch on record was the writer's; a per-district recovery is DM4, for a mismatch no writer caused. DM2-A:
MapCheck 2h-f's city has an unseen twin, never planned through the wall's birth, cure, return and second cure, saved
before its first view and then dated and drawn as the watched map. DM2-C: the site history's rectangle key is laid out
from DISTRICT, `CityMap.DISTRICT_MOST` guards the journals' packing beside it, MapCheck 4b holds the district's edges.
The map only: the trace is unmoved.

### 0.9.141 — HW1-V, refine projected ground and borrowing cash — 2026-11-17, ready, see docs/queue/HW1-V-notes.md
Projected works retain only the linked ground their class and zoning let
them reuse from purchased parcels, and keep rounded bond proceeds above
each funding gap as cash for later additions. Both the value model and its
independent advice oracle carry the corrected ground and cash. Hard parcel
and bond fixtures prove successive additions, including steep and zoned
ground, unlinked forest and exhausted offers; twelve removal checks fail
with their premises intact. Advice, ground, finance and save-path checks
pass; the default playtest trace is unchanged.

### LQ1 — the luxury queue's size, a study — 2026-11-17, ready, see docs/queue/LQ1-notes.md
`docs/studies/luxury-budget.md`, its probe, two prototypes and the real figures in `docs/studies/luxury-budget/`: the
households' luxury budget on 0.9.139 against BEA, the Census, BLS and the Fed. The counter takes 13-19% of GDP (36% in
seed 0's old city) against BEA's other goods' 9.5%. Nearly all of it is the wealth term, the whole of a wealth effect
spent at one counter on a pile of 10-17 years of disposable income (the Fed's 7.9), and the shops keep 2.66 times the
landed cost against the specialty stores' 1.92. On three seeds a margin of 1.89 takes the shops' workers down 23-45%
with the spend and the pile held; a cut in the counter's share alone grows the pile. The Department Store is never
chosen, and by the Census it has no edge to give it (REV-LX1-1). Three calls (LQ1-K); LQ2 (the margin) and LQ3 (the
fortune's other spending, a study) queued. No code.

### FB6 — KeptCheck's fixtures cause their own conditions — 2026-11-17, ready, see docs/queue/FB6-notes.md
KeptCheck's sections 44, 51, 63, 64, 66, 67 and 89 now cause what they read instead of finding it on the city's path, each with
a `fixture:` line, and their Observation guards are out; no floor or premise moved. 44 draws a run both offers fall four times
short of, with the shortfall's lines and the cap's chips, beside the usual page that an offer covers (KBF1). 51 swings the plain
business's property tax by three printed steps of its net income each month; 66 and 67 put sixty months of paper on the books
(KS2). 63 holds the fund's rule still and watches the bonds that run longest, then lets the rule go one month and watches the
holdings it settles. It also sells small bonds until Search has a third page, and gives the record a second older year (KF63,
LX1's three). 64 quotes Borrow at the fill cap; 89 holds the families across its month (KHT1). 87's trace finds the Rankings
worker's read stalling under load, not the save: KRW1. 55's Bank Profit page, red only in R6b's dry run, is KBP1. The change
is harness only; the playtest's trace is unchanged.

### 0.9.140 — FB4, three gate reviews' follow-ups — 2026-11-17, ready, see docs/queue/FB4-notes.md
GV1-R: the wealth band's notice leaves a band only 10% past its bound (`GovernmentView.BAND_MARGIN`), so wealth on a
bound no longer posts a notice a month (241 over 243 readings before, 3 now, InboxCheck); the government's debt counts
the central bank's advances and the linked bonds' indexation; the buyback advice keeps its quote while nothing it
reads has moved. WG2-R: the brake migration applied is kept and saved (`Migration.getLastBrake()`), and the People
screen's slowing word reads it; SaveFileCheck reloads a city at 239 and 240 months recorded. RB1-R: the fund's idle
cash is read after last month's toRaise is paid, the idle figures count what was paid, and FundCheck 9e counts the cap
sale (230 of 240 months) and the idle transfer (90) firing at the default dial, the holding never past the fixed
point. The playtest moves from m1322, through GV1-R's debt in its player's January read; the ensemble passes
(docs/ensemble/FB4.md: 24/26, hard 10/10, F1 as the baseline). Queued: PK1 (two fixtures that pick a month) and RB2
(the fund's due struck on a value that counts the toRaise).

### KR1 — the ship file's known-reds template has no header row — 2026-11-17, ready, see docs/queue/KR1-notes.md
`docs/batch.md` step 5's ship-file template wrote the `## known-reds` section with a header row, `Harness | part of the
red line | queued fix | date | cause`. A builder copied it as a row, and `tools/known-reds.sh` rejected it (its third
field has a space), so `batch-check.sh` stopped with exit 1 and "malformed known-reds row" (SQ1). The template now
shows the line as a placeholder row, `<Harness> | <part of the red line> | <queued fix id> | <date> | <cause>`, and says
that no section takes a header row and a known-reds row is one red. The script is not changed: it is the gate's judge,
and a malformed row stays an error. Checked on scratch copies: a header row and the unfilled placeholder are still
rejected, a filled row passes. No code, no tag; one in, one out: batch.md 5 lines added, 5 removed.

### MA1 — what carries the city next — 2026-11-17, ready, see docs/queue/MA1-notes.md
`docs/studies/manufacturing-margin.md`, its probe, the census and BEA extracts and the runs' tables in
`docs/studies/manufacturing-margin/`: Manufacturing's and Automotive's margins against the 2022 Economic Census and BEA's
use tables, as CU1 did for copper. On 0.9.138 the two carry 52-76% of the exports and 42-51% of GDP. Their goods and
wages are real, but a works buys nothing beyond its recipe, where a real one buys half to two thirds of its sales, so it
keeps 21-41% against the census's 13-23%; fabricated steel's world price floats 1.1-1.8 times its chain; a works costs
two to four times the census's assets a sale; the Machine Works has three times a machine shop's crew. On five
prototypes, with everything in line two cities of three take off, on the same two sectors at the census's margin, and
the third stays at 40 thousand people. Five calls (MA1-K); MA2 (the hold) and MA3 (the books) queued, MA4 pending on
MA3's ensemble. No code.

### 0.9.139 — FB7, counters read under load — 2026-11-17, ready, see docs/queue/FB7-notes.md
Two counters that load could move are read so that it cannot; the model and the playtest are main's. SaveSlotCheck 7d's swap counter
and its seam are the whole program's: 7d now counts with the shared save writer settled and held, the seam answering only its own
thread, and asserts the writer wrote nothing meanwhile. With another job on the writer waiting for a file inside 7d, main went red in
36 of 40 runs, FB7 in none (SS1; the first red, RT1's, was the clock SS2 mended). ReplayCheck 5d's asks were getTaskCount() deltas,
which miss a job the thread has taken and not begun: a loaded glide that asked nothing could tally -1. The map's worker now counts each
job as it is handed in (MapView.Worker), a fixture causes the moment (the old count 2 to 1, the new one holding), and no frame's asks
may come out below nothing (GR2-R). Both harnesses green 4 of 4 as two copies beside a 20-thread CPU load; FB7's three known reds retire.

### MED1 — a health study — 2026-11-17, ready, see docs/queue/MED1-notes.md
`docs/studies/health.md`, its sheets in `docs/studies/health/`: the causes of death the game lacks, 1900 to today in
Canada, and a mental hospital. Heart disease, stroke, cancer, diabetes and kidney failure become named shares of today's
base rate, each with a history factor (heart disease 5.5 times today's in 1950), so insulin, angioplasty, statins, the
smoking ban, imatinib, checkpoint drugs and PCV7 get steps a city can take early. Polio runs on Canada's own years with
the Salk vaccine as its step, and dialysis keeps a pool the hospitals serve once M4 is known. The mental hospital stays
out: nothing measures what a served patient gains. Settles R2f's ★10, R6-G-K ★2 and ★3 and R6-G-P's medical rows. Six
calls (MED1-K); MED2-MED5 queued. No code.

### 0.9.138 — LX1, Luxury Retail's sales per post — 2026-11-17, ready, see docs/queue/LX1-notes.md
A Boutique covered 320 pieces of a $22,000 good a month with seven posts, so Luxury Retail's payroll was 0.5-1.7% of
its margin on main after CU2, where BEA's other retail pays 41.7% (LS1). A Boutique now covers 7 pieces with 8 posts
and the Department Store 80 with its 91 (ids 69 and 70); their floor, cost and the piece's price are held.
`LuxuryRetail.LABOUR_SHARE` (41.7%) and a factor of two either way are the band `LuxuryRetailCheck` holds the
catalogue and a town's year to (red on main: 0.6% and 1.3%). On LS1's probe the share reads 36-41% (in the band 313
of seed 0's 331 years, none on main); the shops take a quarter of the old city's business workers, and the default
city is 912k at m2399 against main's 413k. The Department Store is never the planner's choice, as on main (LQ1's
call). A road load's last stream is the remainder of the other two's sum (`TradeCostCheck`). A 4,000-month run has
the map's save refused at m3589 and m3952, FB8's. GroceryCheck's twins hold their planners, so its vouchers' poor are
caused; fixtures that read the old path go to HP2 (PriceLevelCheck, HealthCheck, ScaleCheck, DistressBleedCheck), FB9
(MapCheck 2h-c) and KRT1 (KeptCheck 78), and PlanCheck 6's + floor on its played city to PX2. No save field, no format
bump.

### 0.9.137 — FB5, the played cities' placement — 2026-11-17, ready, see docs/queue/FB5-notes.md
The map draws every building the city has. What no plot of the city's own ground holds at its edge now stands just past it, on dry
ground the city does not own, beside a street and never in the field. In MapCheck's played city 133 stand there; 135 went
undrawn on main. A + junction no longer forms where streets laid on newly bought ground meet the T of a join along the old cut (PX1).
Instead, the newest arm that can go without parting the streets is stopped a plot short. PlanCheck's new fixture breaks the plan
in 36 of 76 cuts without this, and holds in all 76 with it. The industry ring (CL6) no longer shows on the playtest's default
path (industry 2.78 km from the founding site against the homes' 3.92). Seed 1's ring is measured and goes back to the queue as
CL7: a new cell goes to the works that bought its ground, while the homes already wait at the edge. MapCheck 1's road at month
61 is the renderer's stale tile, FB9's DM3-R. Nothing in the model reads the map, and the playtest's trace is unchanged.

### 0.9.136 — FB3, reloads that read as they were saved — 2026-11-17, ready, see docs/queue/FB3-notes.md
A loaded city now reads as it was saved in four more places. The shops' want and means are put back always, zeros too: a city saved in its
second month, when its shops want nothing, came back wanting what the load's rebuild struck (0.0625; MoneyDatesCheck). An older save's
contracts on site are read after the load strikes the builders' wages, not at the founding ladder before it, so a House either side of the
rental rebate's line gets the loaded city's share (.0000625 against 0; InvestCheck green, a premise line that the line lies between). The
draws made between the presses cross a save, so a hand order keeps its seller's row; the month closes its own before its autosave, which a
load would otherwise fill twice. The hot money's peaks cross a save, so the Trade card's "the most that was ever here" is the city's.
Groceries, housing, luxury and meals now begin each month's trades afresh, where they grew for the city's whole life. ReloadCheck causes the
stop, the hand order and the limestone carry no checkpoint reaches, asks that its skips had an outbreak and a sick month, and loads the
month-end autosave; SaveFileCheck places the hot money by name (HZ1). No save format bump; the playtest's trace is unchanged.

### 0.9.135 — AQ1, A3's one seed on GW1: the bust — 2026-11-17, ready, see docs/queue/AQ1-notes.md
GW1's seed-2 bust is off main's path, but its cause is on it: a price taker with no demand reading (Business
Services, Automotive, Manufacturing) held all its plant through years of losses the bank's shortfall desk lent it,
then, refused, sold a quarter of its biggest holding a month (on 0.9.132's AUTO2 seed 2, 6,000 posts in a year and
8% to 38% out of work). Such a sector (`Sector.bleedsWhileLent()`) now sells while the desk still lends it (its own
cash is not a lender), once its plant has run at an operating loss `DISTRESS_LOSS_MONTHS` running (a new saved
streak, old metal left out): a `BusinessInvestment.BLEED_MONTHS`-th of its biggest holding a month. A loss of interest
alone holds; overdrawn with no lender the quarter stands. Ensemble 23/26, hard 10/10, SHOCK K2 now passes; on AUTO2
the largest ten-month rise in out of work is at most 15 points (nine seeds at 20-26 on 0.9.134), 41 months over 20%
against 264. New: `DistressBleedCheck`. RN1-A closed: A3's one-month red, accepted at 0.9.63, passes on 0.9.132.
At the gate five lines pass by name: LandmassCheck 12 and CopperRefiningCheck (f), red on main in full (FF1); seed
1's industry ring (FB5); PriceLevelCheck 10's town past its horizon (HP1); PopulationCheck's fill (MR1, ★).

### 0.9.134 — OX1, the orphans behind a wave that ends at once — 2026-11-17, ready, see docs/queue/OX1-notes.md
An adult back in work, or back from a course, now brings home the children who went out with them: the builder places them after last month's kept
households and before the city's pool, by its own order and half a shape (`FamilyModel.SHAPE_TAKE`, the bare .5 named), pass after pass, until every
one is in a household with the adults who came back, never more of them than the families' own adults grew by (an
adult who left the city or went to prison left the out of work without coming home). They used to come back as loose children, the half a shape left one in seven over, and the
households' memory kept them waiting for months: a fifth of 6,000 adults back at once left 67.7 orphans that month against a twin's 6.6, nine months
to fall back (DeathRecordCheck's new section 4; now 5.26 against 6.58). In the default playtest adults came home in 838 of 2,400 months and 62,000
children with them. DeathRecordCheck's played town (28.2 orphans against its twin's 27.1 on 0.9.131) is green. Its m60 twin line is
re-filed to OX2: that town's wave ends two months before its twin's, and the arrivals behind it bring the builder's ordinary orphans sooner. Nothing
new is saved. The playtest's path moves from month 14.

### 0.9.133 — R6b, Research: the near-future rows — 2026-11-17, ready, see docs/queue/R6b-notes.md
The near future's first rows, each with what it gives. `research.json` learns `projected` and `window`, held to
`ResearchCatalog.HISTORY_ENDS` (November 2026), and a projected row says so wherever its date shows: the inbox,
the research screen's dots, rows and flags, the build card's origin. A city founded today's way never knows a row
from `NEAR_FUTURE` (January 2025) on, so the default playtest is unchanged to the bit. A city founded the 1900 way
gets **FE1**, the driverless truck: a kit at the Truck Depot, a truck working 20 hours, not one 10-hour shift, with
a tenth of a post watching it (both estimates). **FE2** is the Sulphide Leach & SX-EW Plant (153): cathode from a
sulphide field at 85%, no smelter. **FE4** is the Hydrogen Steel Works (154, projected 2028): pellets and 3.48 MWh a
tonne into steel, paying where it replaces imported steel on cheap power. Saves go to format 55, and the oldest
loadable with them: a format-54 save holds no count of the depots' kits unfitted, which would read as fitted. New:
`DriverlessTruckCheck`, `SulphideLeachCheck`, `HydrogenSteelCheck`, `ResearchCheck` 11-13. FE3, FE5, FE6, FM1
and FM2 wait for their batches.

### 0.9.132 — TQ1, the staffing gate's tie rule — 2026-11-17, ready, see docs/queue/TQ1-notes.md
A planner whose buildings the staffing gate refused hands one hold to the wage (LD1), the refusal nearest to
passing, and in a city with no spare the refused templates tie exactly (GW1's snapped share). Each of thirteen
planners broke the tie by the order its buildings are listed in: in main's default playtest a tie decided 638 of
3,502 holds, mostly in the founding, and a Builders' Yard's 20 posts were priced, never the Construction Depot's 50.
The thirteen keep their hold in one place now (`Sector.Staffing.held()`), and a tie is shared: each tied building
asks for its part of its posts, so list order decides nothing (StaffingTieCheck, red on main's rule). No save field,
no format bump. The path moves from month 4. Its two findings, households with no home in the ensemble's seed 0 (once,
month 182) and in the default playtest (months 692-745), pass by name with FB11: Real Estate held for land while
nothing buys ground. In same-state probes main's rule stalls alike (the playtest's from 560 on; seed 0's, from
month 2, is the same city just under the watch's line); the escape is a works order buying ground it does not use.

### 0.9.131 — FB2, the kept screens' geometry and retention — 2026-11-17, ready, see docs/queue/FB2-notes.md
The kept screens read the same as screens built new whatever the played city does, and each repair is caused by the states that need it (KeptCheck 103, 25 lines: each repair's lines red by hand on the old classes, none now).
The buyback ladder is drawn from what it reads of a quote, as written and at a quarter pixel, so faces moving by 1.4e-8 of themselves no longer draw it again (KL1). The header's hidden wordings and
sparkline stand where a tile built new leaves them (the RATE tile's "D$1,000,000,000.00 per US$": 18 states), a bar's tick with no name has no label on it (Your rate's dial: 0 x 20 against 0 x 13), and
Partners' buyer-entry triangles stand at their own points, not moved by a fractional layout offset (62 pixels at the finding, 0 now); Search's empty price and held words are FP1's scopes, now asserted. The Government
Overview's lines that leave and come back (a tax's slice and its key row, a bridge's step, the arrears) are parked, off the page, by the new Kept.parkedScope() and return with no node made (38 nodes before, 0 now);
the busiest month of a path whose first draw made 400 nodes reads 34, not 45, and a line's first appearance still makes its nodes. Two entries are not mended and are filed: the Wood Products Residues Text (KRT1: 0 differ over 120 months
and on KS1's own classes) and the Borrow page's ladder card coming and going with the quote (FB6's KBF1 family). Screens only: the playtest does not move.

### FB1 — three fixtures that read the city's path — 2026-11-16, ready, no tag (harness)
Two fixtures that read where the played city's path happens to stand now cause their own conditions; the model and the playtest are main's.
FoodProcessingCheck 5's Meat Works bet read the lived-in city's wages, exchange rate, demand and rate: its two profit sums stood at $-244 / $-595 on main
(green by a sign, printed Infinityx), $261 / $-156 on CU2's path and $1,255 / $679 on BS1's. It sets the world band, the founding ladder, nameplate and no city
demand itself and asserts them: $296 on a herd against $-108 on a ship's meat on main, CU2 and TQ1. HealthCheck's poor city at three prices never had a household
between one bill and fifteen (x1 and x15 served 83.1% alike), so its claims compared the twins' noise: betweenTheBills() puts the cells short of a basket at the
geometric middle of the two bills and keepBedsAhead() holds a clinic place for every person (CU2's twins outgrew 30,000). The dear twin turns away 15 points of the
care the founding fee serves (100.0% against 85.1%), buries 14.6 a year per 1,000 against 9.4, its old 91.5 against 48.5, and the three played claims are hard
again. MQ1 is not mended: the index never steadies at the founding's own dial (month 97 on main and TQ1, not in 90 months on CU2) and the late rows' noise on a
steady window is 0.64-0.95 points, over the 0.650 allowance; it stays queued with the evidence. FoodProcessingCheck's known red is gone.

### 0.9.130 — CU2, World prices chained — 2026-11-16, ready, see docs/queue/CU2-notes.md
The world market's January is chained (WorldChain, forty rows from buildings.json): a made good is the cheapest
maker's landed cost of its inputs plus its own part, so rod follows its cathode and wire its rod; a root is
anchored on its reference band; the copper charge is money on Nations' own swing (the record, ★4, waits on Jerus
as WP1-C). Seed 1 stands no Rod Mill (main's 363), seed 0 four Wire & Cable Works (main's 61); at 2100 DR Congo's
terms of trade are +0.07 (main's -0.44). Checks: WorldMarketCheck's chain, recipe, guard and charge;
CopperRefiningCheck 10-11; HistoryCheck 7, the history's six-figure prices written at today's prices (15% off the
file). Ensemble: M1 passes; F1's one finding (SHOCK seed 13, up to 174 households with no home in months 738-776)
is the bank's leverage ratio refusing the landlords' mortgages for 70 months, main's capital rule on CU2's path,
passed by name with MC1 queued; AUTO2 A3, ZERO_HOT H2 and SHOCK K2 changed, information only. A young town on
CU2's prices can now lose its bank to an over-estimated first works until OE1 (YoungBankCheck 2, named). Saves:
format 54, the floor with it.

### DQ1 — Young towns borrow ahead of their people and default — 2026-11-16, ready, no tag (harness)
Measured first: the AUTO2 bank's months under its minimum to m600 are 2 [0-17] on 0.9.128, 23 of their 65 in
write-off spells, six of those a young town's lone Fabrication Shop sold empty (86-100% of its loan written off,
a quarter priced). Four ways of holding or pricing a lone plant's loan at the bank were measured; none is taken.
Weighed, it froze the young bank; held in the project room or priced, it halved or stalled the towns; kept in the
bank's own target, it was lent on (120 months under, write-offs +37%). The miss is the order's estimate (a Lime
Kiln weighed at an operating rate of 0.97 ran at 0.52-0.61), queued as OE1 after SQ1. YoungBankCheck runs
BankCheck 19's town held nowhere: the kiln's default puts its bank at 0.7%, never in resolution. Holds released:
BankCheck 19's four, SaveFileCheck's year, CurveCheck 5's run-on, PolicyPreviewCheck's Cement and Heavy Industry,
BondCheck 7c; HoldersCheck's two, the steep city's and Freight kept, each tried. The model and trace are main's.

### 0.9.129 — SQ1, a building stack past 2^31 — 2026-11-16, ready, see docs/queue/SQ1-notes.md
A building type's units merge into one stack, and the stack kept its quantity, its on-site count and its unfitted
counts in an int: a ten-billion city housed in Houses holds about 2e9 of them, past an int's 2,147,483,647, and
ScaleCheck's copy did not load where a stack times K passed it (found by ST3). The counts are a long now: the stack,
BuildingManager's sums, orders and retirements, the advisor's plan maps of buildings, and the saved arrays
(DataSave's buildings and underConstructionById, the unfitted counts); an old save's int reads into a long, so
there is no format bump. A stack driven past 2^31 by orders and by ScaleCheck.scale() reads back whole through the
save and the load, and its homes, posts and places are its quantity times the template's (BuildingStackCheck, red on
main's int). Below the ceiling nothing moves: the default playtest's eight trace files are main's, byte for byte.

### R6-G — the catalogue's years 1996-2024, a study — 2026-11-16, ready, no tag (docs)
`docs/studies/research-1996-2024.md`: the research catalogue's rows between the online shop (B8, 1995) and the
near future, as research-1900 was for 1900-1950. Thirty rows of fifty-seven subjects gathered, each a first
commercial use with its date, a measured effect and a source that could be read: twelve engineering (GM crops to
the electric long-haul truck), twelve medicine (PCV7 to the RSV vaccine), two business and four law, on Canada's
dates. Thirteen have something in the game today and are queued as R6e (eight engineering rows) and R6f (five
medicine, business and law rows); the other seventeen wait on MED1, PW2 or PW3, a storage batch, HO4, HT1 and
FM1's batch (R6-G-P). Most of the period's medicine acts on causes the game stops counting in 1980. Six calls,
each recommended (R6-G-K). Docs only: no row in research.json, and the trace is main's. See
docs/queue/R6-G-notes.md.

### 0.9.128 — LF1b, the cells' share trades with the world, in the financial account — 2026-11-16, ready, see docs/queue/LF1b-notes.md
The households' cells selling shares to the world and buying them from it are declared to the money audit as the
cells' bonds have been: a sale abroad a financial inflow into their savings, a purchase a financial outflow out of
them, each a pair the cash identity cancels (`+ shares HouseholdsSoldAbroad` with its DOMESTIC half, and the
purchase the other way). The balance of payments' financial account held the cells' bonds and left out their shares;
it holds both, so the exchange rate reads them. In the default playtest the cells sold shares abroad in 968 months,
21% of the financial inflow in months 401-800, so the currency and the trace move; ShortSellingCheck causes the trade
in a real month and reads the lines.

### RE3 — the landlords' payroll with the rent: the trap is gone on main — 2026-11-16, ready, no tag (docs)
RE1's probe on main 0.9.126, eight AUTO2 runs (seeds 1, 3, 11 and 13, with and without the data files): RE2's trap is
gone. Seed 3 unnamed, the brief's fixture, has no loss month and no down-payment hold over two months (RE2's model: 125
and 59 in a row). Seeds 3 and 11 unnamed stand at 25,000-33,000 for over 1,000 months, held by jobs: no works pays and
the landlords are rich. Seed 13 named comes nearest: a jobs-led fall, 25 loss months, then a 35-month hold. The call:
no code; the ensemble keeps watching the trap (RE1-T's way (d)), and a study of the jobs-held towns waits on Jerus
(RE3-J). Docs only; the trace is main's. See docs/queue/RE3-notes.md.

### 0.9.127 — R2f, Research: the medical rows — 2026-11-16, ready, see docs/queue/R2f-notes.md
The catalogue's sixteen medical rows now act in a city founded the 1900 way (research-design Q17). A row found here
before the world brings its step on a cause of death forward by the months it was ahead: milk and streptomycin on TB,
the diphtheria toxoid on the childhood outbreaks, sulfa on pneumonia and mothers, penicillin on mothers. A city that
finds nothing dies on history's path as before. Until a row is known its constant is the one before it: general care
cures less before penicillin and the four rows that shorten a stay (76.5% of the fully served a month until 1943,
against today's 90%), the sick of a year or more recover at 62% of the rate before the TB drugs, and the served long
sick die 18% more often before the breathing machine. Insulin, the polio vaccine, dialysis and angioplasty have nothing
in the game to act on and wait for a health study; the X-ray and the antitoxin predate 1900. A city founded the
default way, and the playtest, are unchanged. MedicineCheck proves each row.

### R6-T — the speculative tier 2030-2060, a study — 2026-11-16, ready, no tag (docs)
`docs/studies/research-speculative.md`: the research catalogue past the evidence (Jerus, 2026-11-09: "Keep to
2060"), each row on a builder's, a government's or a trial's own target for its date and its effect, projected
and held in R6b's window. Eight rows, all 2030-2035: the tuberculosis vaccine, the sodium reactor with heat
storage, driverless field work, the 30% tandem module, electrolysis at 46 kWh a kilogram, the fusion plant,
superhot rock geothermal and power from orbit. The record runs out about 2035, and business and law give no row.
The slip allowance is staged by what had been shown: +1 being built, +3 shown but not building, +5 with the
deciding test unread. Four calls, each recommended (R6-T-K); R6d queued for FE8 and FE10; the other six rows wait
on PW2-PW4, FE3's batch and R2f (R6-T-P). Docs only: no row in research.json, and the trace is main's. See
docs/queue/R6-T-notes.md.

### 0.9.126 — R1d, Research: the 1900 city switched on, with R0d's 1901 wages — 2026-11-16, ready, see docs/queue/R1d-notes.md
The founding screen offers a 1900 city beside "All fitted", and opens on All fitted: research known as of 1900, the known
equipment unfitted, 1901's wages, the dated arrivals, basket, money and licences. The playtest plays it under
-Dplaytest.founded=1900, and there it stalls (472 people at month 60 and 159 at month 900, against today's city's 1,674
and 23,180): R1g makes it grow before the screen and the playtest switch to it. A 1900 game's rule waits for the
central bank (March 1935) and takes the dial then, unless the hand took the Advance Rate first. A works' steam share
runs through a brownout, and the grid's readers value only the electric share. A building scrapped while its fitting
is on site cancels only a job left with nothing to fit, with no refund; the builders are credited what was paid; a
fitting carries the builders' sales tax and the owner's credit share; lead times count the fitting points. The electric
drive takes 1 point plus 10 a tonne to fit; the tractor's gain counts the horses' feed and hands; carters hire their
teams, $300 of 1900 money over 120 months.

### 0.9.125 — LF1c, LF1's review follow-ups (the short's lending fee) — 2026-11-16, ready, see docs/queue/LF1c-notes.md
The Fund screen says what its record holds: a month's lending fees read "Lending fees from the world on the shares
lent to a short in N companies" (the row's kind, LENDING_FEE, was on the screen), a company's shares written off with
it read "its shares gone with the company" (they said a bond's "its face gone"), and the income card and a market-book
share lot's page say their income is dividends, coupons and lending fees. The exchange's cancelling of a company with
no shares in issue and a short open declares what it takes across the border to the audit as two valuation pairs, at
the month-end quote: the world's own shares, wiped for nothing (`+ desk WorldSharesWiped`, in the city's favour, as a
bank resolution's owners wiped abroad), and the world's short closed for nothing (`- desk ShortClosedForNothing`,
against it), so ForeignAccounts' forgiven counts them and the month still foots; the Trade screen's valuation words
take the sign. ShortSellingCheck 8 follows the month's order. No short opens in a real run: the default trace is unchanged.

### OL1 — one long-lived orchestrator — 2026-11-16, ready, no tag (process), see docs/queue/OL1-notes.md
`docs/orchestrator.md`, "The day", says the orchestrator is one session from the
app environment "CBS orchestrator" that compacts in place (window 300000, hooks
under `CBS_ORCHESTRATOR=1`, the PC hand-off note put back after each compaction),
replaced only on a failed turn, a compaction that lost something, a start above
~115k or Jerus's word; the daily hand-over is one sentence. The check-in and
wait lines no longer say each orchestrator or each day. CLAUDE.md and
`docs/batch.md` already said all they should: no change. 15 lines added, 14
removed.

### CD9b — CD9's review follow-ups (the foreign debt's) — 2026-11-16, ready, no tag (harness), see docs/queue/CD9b-notes.md
Debt.effectiveRateOf's javadoc now says what getYieldToMaturity() does since CD9
(its bracket split at a price equal to the payments' total). CurrencyCheck's two
extreme-rate sections no longer build a bare ledger and assert revalueForeign's own
formula back: they drive a real city's ledger through the extreme rate and assert the
booked exchange line CD7 keeps (NationalAccounts' debtExchangeGain), red when the
revaluation is skipped. No game or save format change.

### SR1 — A city stocked complete in bulk reloads with its cash drifting — 2026-11-16, ready, no tag (harness), see docs/queue/SR1-notes.md
The bulk-stocked causes-of-death fixture now plays beside its reload at
months 3, 12 and 60, and through January–March 1919 alongside the existing
playtest-founded twin, comparing cash, every audit pool and the full death
and sickness state to the bit. Its original cash drift is reproduced on
R0g's model and traced to unallocated fixture ground being reconstructed
on load; AW2 already saved the live land ledger and cured it. The fixture
is kept unchanged and founded()'s comment now tells that history. No game
or save format changes.

### 0.9.124 — FA1, frontage avenues beside diagonal highways — 2026-11-16, ready, see docs/queue/FA1-notes.md
Diagonal highways gain a frontage avenue just outside each verge, using the
avenue planner with separate origins and dated highway plots. New cells
build along them; existing cells retain their grid and standing buildings,
and frontage uses only available plots that keep junction spacing. Streets
still pass beneath the highway. The painter keeps both strokes and their
width where avenues cross, including across tile edges. PlanCheck and
MapCheck guard strip use, both headings, late-city use against main, retained
grid and boxes, the junction floor and the new crossings. The launcher dry
run passed and the eight traces match main's. Map only; no model or save-format change.

### 0.9.123 — R2e, Research: the 1951-2000 entries undone — 2026-11-16, ready, see docs/queue/R2e-notes.md
The postwar engineering and business catalogue now removes 27 sourced changes
from unfitted templates, with independent fitting, saved process decisions and
exact modern destination figures. Completion fitting selects one paying
equipment set; refits value one count over the rounded fleet.
Zeolite composes with catalytic cracking,
keeping the thermal process and dispatched-feed licence charges consistent.
Panel Mill opens through E19 in 1981 on FPL-GTR-59; Wood Pellet Mill through
E20 at retained 1970, explicitly undated within FPL-RP-656's 1970s.
The eight default traces match main byte for byte. Source, estimates and
verification evidence are recorded in the batch notes.

### 0.9.122 — PD1b, PD1's review follow-ups (the drivers' petrol, OilCheck 20) — 2026-11-16, ready, see docs/queue/PD1b-notes.md

OilCheck reads rebuilt shopping and fuel before the saved household statement
overwrites them, and a missing-petrol control proves the comparison detects a
mismatch. Its footing includes the homes' lamp oil and coal, exercised with a
nonzero bill. Game's comment explains the saved Motoring bill's month offset;
the model and the month's order are unchanged.
The normal launcher dry run passed (exit 0; --pick exit 3, known reds only).
All eight default trace CSVs match R0h's reference byte for byte.

### 0.9.121 — R0h, The 1918 influenza's sick counted once — 2026-11-15, ready, see docs/queue/R0h-notes.md
The influenza's acute sick now stay beside the long-sickness ring, so they are
counted in People's sick by age and Services' youngest sick bar while only the
influenza's own cause books their deaths. The acute shares cross saves by age
band; an older save without them loads none. Unit, save/load and read fixtures
cover the split, and September 1918 twins prove the January-April 1919 excess
ring deaths are zero after their measured chronic drift.

### 0.9.120 — BK2b, BK2's review follow-ups (each holder's own discount) — 2026-11-15, ready, see docs/queue/BK2b-notes.md
A partial buyback taking all but rounding-level face now takes the seller's whole unearned discount, as a transfer already does, leaving no discount dust on no face. HoldersCheck causes the ulp-short case for every seller at two price levels and covers the existing retained loss when the bank pays a premium beyond its own discount. The reopening comment now describes each holder keeping its own discount until settlement. HoldersCheck and BuybackCheck pass. The fresh-clone merge dry run is green (launcher exit 0; suite exit 3, known reds only); the default playtest trace is unchanged.

### ER1 — Rotterdam's land buyer and the shifted growth hold — 2026-11-15, ready, no tag (harness), see docs/queue/ER1-notes.md
On 0.9.117, standard Rotterdam and default cities finish 4,000 months with no
findings. Rotterdam's old twelve-thousand episode has shifted: 13,575 offered
jobs hold for 784 months while its plots remain full and builders have no queue.
Recorded hunger is predominantly priced out, rather than unfilled grocery orders.
A played-state cash-only twin stays held; the same cash plus a real land purchase
opens a Fabrication Works. ER1Check causes the land/cash condition and releases
the same private job order after annexation in about two seconds. No model code changes.
The merged 0.9.119 launcher gate has only main's known reds; its kept run is recorded.

### 0.9.119 — KP2c, The kept screens' sweep, the rest — 2026-11-15, ready, see docs/queue/KP2c-notes.md
Kept redraws retain mouse handlers, sparkline transparency and unchanged
disabled state, column and row constraints, preferred sizes and margins. Services and Infrastructure write each door's
complete style once, and the scrolling page keeps its padding. A slider
keeps the player's press through greying, ungreying and a draw's temporary
reparenting, while release elsewhere still lets it go. The vitals bar
finishes cells supplied by other makers. The sweep counts every kept
screen and the formerly invisible layout requests; comparison roots,
retired picture counters and no-stage scene dirty lists are released.

### 0.9.118 — R2d, The 1900-1950 entries undone — 2026-11-15, ready, see docs/queue/R2d-notes.md
A city founded the 1900 way now runs sourced pre-entry figures, with
independent changes that compose beside process recipes and fitted equipment.
Thirty-six undos cover twenty-eight templates; cards, margins, research rows
and refinery ribbons read the actual process. Patent rents use the actual
goods and rail invoices, with saved bills through demolition and settlement.
Fixtures cross each entry's month, fit pieces in either order, and exercise
real construction, refitting, extraction, fuel and cracking. All eight default
traces are unchanged. The notes name every estimate and the sourcing gaps
left for Jerus.

### 0.9.117 — XR7, The world's pegs overshoot both ways, and the hold capped — 2026-11-15, ready, see docs/queue/XR7-notes.md
World pegs now land past their shadow on either side through the city's existing
OVERSHOOT dial. A chosen peg breaks before a month's purchase exceeds a year's
GDP; a band gives way for that month at the same bound, avoiding the hold's
exponential bill after deep devaluations. Nonzero peg support is checked through
DataSave JSON, the real load path and next-month settlement, with money audit
tolerances unchanged. Signed-break, boundary and save fixtures pass.
The 80-run ensemble passes M1/F1 in all five policies; its only changed verdict
is informational ZERO_HOT T2, FAIL to PASS against RT2's newer baseline.
The retained full-suite check has only main's known reds and is approved for reuse.

### 0.9.116 — FP1, The fund's long lists a page at a time — 2026-11-15, ready, see docs/queue/FP1-notes.md
The fund's Search shows forty results at first, with Show more adding forty
and stating how many remain. Typing or choosing a chip starts it again.
Search keeps its row nodes when the first page changes securities.
Activity keeps its latest twelve months open and each earlier calendar year
behind a fold, with its recorded month count and net realized gains plus
income supplied by FundView. KeptCheck exercises paging, year folds and
the ledger totals, with the Fund redraw budget remeasured in five runs.
Search also repairs BF1-K's stale empty price/held glyph bounds. Its Partners
triangle finding remains with BF1-K.

### 0.9.115 — UP6b, The autosave: UP6 review follow-ups — 2026-11-15, ready, see docs/queue/UP6b-notes.md
Every autosave path now shares the skip's completion guard, preserving its return point after a month throws until load or new city, including close, Quit and Simulate more. The failure page, inbox and Quit explain when autosaves are off and how loading restores them. Autosave notices name the saved job's month even when reported later, and describe the possible mismatch between an older save and newer history after a failed write. The last-save-result comment distinguishes synchronous saves from reported background results. Scratch-folder SaveSlotCheck fixtures cause the failed skip, late report and mismatched files and verify background result reporting.

### CB2 — The central bank and monetary checks re-measured — 2026-11-15, ready, no tag (harness), see docs/queue/CB2-notes.md
CentralBankCheck measures 24 founding shapes instead of 12; all hold on
RT2 main, including seven starting under the target. Its three-in-four share
stays, now needing 18 cities, and its negative fixture still tests both sides.
MonetaryCheck's one-month timing differences now peak at 0.322 points, so its
allowance becomes 0.65, about twice that reading; the one-point transmission
floor stays. Six months of policy delay misses the premise, five does not.
PO1's observations and hard laws are unchanged. No game code changes.

### 0.9.114 — R2c, Licence fees on the current account — 2026-11-15, ready, see docs/queue/R2c-notes.md
In a city founded the 1900 way, private firms pay 25% of the positive gain from
a patented discovery imported from the world until twenty years after history's
date. Local discoveries and unpatented entries are free. IP charges leave the
firm's accounts as imported services in the same month's money audit and
foreign current account. Production's bill is saved for the next strike,
including last-term production paid after expiry; saved statements and
monthly history retain paid and accrued flows. Saved per-building amounts
keep standing planner costs identical through a load.
Planners and fitting compare gains less the licence fee. Positive-payment,
expiry, exemption, equal-gain ranking and nonzero save/read fixtures cover it.

### 0.9.113 — RT2, The other buildings counted in their sector retirement measure — 2026-11-15, ready, see docs/queue/RT2-notes.md
FoodIndustry counts baked outputs per mill in heads, worked-out CoalMining
counts raw mine nameplate, and Freight compares offered tonnes with
standing track. Freight limits a spare sale to preserve operating headroom,
including station room for goods that bulk sidings cannot handle.
Each sector has a fixture that fails before its repair. Played fixtures
keep their cities, premises and assertions through month re-picks.
The full 199-harness merge dry run is green with known reds only.
All 80 final-model ensemble runs complete; M1 and F1 pass against EN2.
ZERO_HOT T2's informational regression and H2's vacuity are recorded.

### KD1 — The suite memory guard by free GB, and the ensemble names its data — 2026-11-15, ready, no tag (process), see docs/queue/KD1-notes.md
Soft suite starts reserve commitment for running growth and another 2.5 GiB
harness, require 4 GiB available RAM and CPU below 80% over six seconds,
and report each limiting resource. Injected fixtures prove admission and
holds, including CPU crossing its ceiling during a slow memory probe.
Worktree ensembles discover both data files and record their snapshot's
inputs. EN2's baseline completed 80/80 runs, with all 10 hard criteria passing.

### 0.9.112 — R2b, Buildings on history's dates — 2026-11-15, ready, see docs/queue/R2b-notes.md
A city founded the 1900 way offers planners, city works and berths only buildings known on history's date or discovered locally; direct city and private orders refuse unknown buildings before spending or drawing materials. The refinery's dated cracking and upgrading units wait for their discoveries. Default-founded cities keep every candidate. ResearchCheck causes the planner, works, berth and direct-order cases and holds their date boundaries.

### 0.9.111 — R0f, Money on Canada's dates — 2026-11-15, ready, see docs/queue/R0f-notes.md
Cities founded the 1900 way follow Canada's dated money: reserves hold the dollar at par until August 1914 and again from July 1926 to September 1931; exhaustion ends a gold episode early and survives a save. Interest follows the world on gold, the government's Advance Rate opens in August 1914, and the Bank of Canada opens the rest of Money in March 1935. Dates cannot be discovered early. Default founding retains its existing Money mechanics. Save format and oldest readable format rise to 53, because older unfitted cities had a different monetary contract. Founded-city, save and read checks cover the dates and the remembered exit.

### 0.9.110 — RT1b, RT1 money review follow-ups — 2026-11-15, ready, see docs/queue/RT1b-notes.md
The sectors' and households' paper ledgers use the paper's relative rounding
rule, so full principal payments, buybacks and central-bank purchases leave
no ulp-sized holding. HoldersCheck causes each old remainder, tests both price
levels, preserves partial holdings and cash, and measures the real firm-paper
desk against the bank's independently recorded book; a broken split fails.

### 0.9.109 — CM1b, The replay loaded move, the review follow-ups — 2026-11-15, ready, see docs/queue/CM1b-notes.md
Replay camera moves release their stored pictures, pins and prepared months immediately when stopped while Ready, and restore their starting view if their map closes during loading. Loads remember every missing kind, stop retrying failed jobs, and refuse at a stated overall limit. Their memory budget measures growth from the starting heap, with at most one collection per load. Keys are consumed during loading, and ReplayCheck closes its replay and unregisters both map pumps before its timing section. CM2's prepared maps and ordinary game behavior are preserved.

### 0.9.108 — LD3, LD2's wage on in every harness — 2026-11-14, ready, see docs/queue/LD3-notes.md
Every harness now uses LD2's wage and the temporary carryKeptOut switch is
removed. HoldersCheck asserts that its loaded twin keeps the fixture's Heavy
Industry hold, the cause of LD2's original settlement gap. The auto-builder's
post-load orders, steep curve and pegged purchase have hard fixture laws.
DeathRecordCheck measures the layoff against the same-month open twin over
two years, with its existing OX1 rehire finding retained separately.

### 0.9.107 — CM2, the replay's loaded move on the globe — 2026-11-14, ready, see docs/queue/CM2-notes.md
Loaded replay moves now ask and draw ground charts across the whole visible globe disc in GROUND, removing coarse
planet-picture patches: the seven-move probe at 1920 × 1080 reports no unpainted frames, worker asks or picture evictions.
Glides across months retain their decoded maps and already-loaded land contexts during loading; M6's worst frame gap fell
from 180.5 ms to 49.2 ms in the matched fully off-screen probe.
Full-resolution moving globes were measured and lost frames, so half resolution is retained.
Leaving replay releases the loaded move and its retained contexts immediately. ReplayCheck covers the real chart range,
a recorded land purchase, ownership and playback without the month file; GlobeCheck preserves ordinary coverage. The normal
game is unchanged. See the notes for measurements and validation.

### 0.9.106 — QB1, The test player borrows at the quoted rate — 2026-11-14, ready, see docs/queue/QB1-notes.md
The test player compares the home and dollar quotes for its actual sale by
default, so a low foreign rate before the sale cannot choose an expensive
dollar bond. `-Dplaytest.quotedBorrowing=false` retains the previous path for
comparison. ScaleCheck copies its growing town at month 225 with its original
premises and tolerances. KeptCheck selects live paper for each debt-picture
watch and verifies it lasts through the unchanged six-month laws.
The 80-run ensemble passes all 10 M1/F1 criteria (22/26 overall); ZERO_SLACK's
population criterion is informational. Checks and gate details are in the notes.

### 0.9.105 — WN5d, The hover's quote: five findings of WN5b's review — 2026-11-14, ready, see docs/queue/WN5d-notes.md
The hover retains its previous price while this month's quote runs, and a measurement that spans months is priced on FX in the current month. Pointer changes cancel obsolete quotes between tiles and seller rows, and first-map adoption re-asks a stationary hover. RuntimeException and Error always hand their requests back, with a separate failure line and timed retry. Large low-priority ground counts use their own two-worker low-priority pool. Fixtures reproduce all five review findings before their mends and hold the quote lifecycle and owner-thread frame budget.

### 0.9.104 — HW1, The auto city builds its works when they are needed — 2026-11-14, ready, see docs/queue/HW1-notes.md
Essential works target full useful service plus the player's reserve, without their payback veto or twelve-
month cap. Cards and automatic orders share counts, opening projections and twenty-year maintained-service
values on one calendar. Plans include initial and later works, actual-use owned ground, parcel cash, quoted
financing, filled-post running, physical output losses prevented and returned payroll taxes. Road wear and
capped sickness enter the sum. Current losses rank shortages and protect funding. Prospective crew quotes
preserve an unserved essential's saved completion through reserve orders, including newly promoted builders-
first depots. Expired promises trigger fresh work, and faster alternatives advance recovery. Hard laws for
funded asks, service recovery, monthly links, eligibility and actual orders remain. Notes record final service
spells, plant choices and ensemble verdicts.

### 0.9.103 — WN5c, A picked block priced off the screen's thread — 2026-11-14, ready, see docs/queue/WN5c-notes.md
Picked blocks are measured on a copied land office away from the screen thread; the card says the price is coming and allows closing while it waits. Seller and landmass geometry is retained, so the month prices picks immediately at its current factors without repeating either plot walk. Results from changed ground, changed links, cancelled or superseded picks are rejected on the screen thread. LandNationsCheck measures clicks by level and 1/10/50 monthly picks at Windsor and the Strait and holds exact prices, current-month delivery and worker ownership.

### 0.9.102 — BF1, The hands-off bank fails on the sawmills — 2026-11-14, ready, see docs/queue/BF1-notes.md
Processing mills sell stored output to the world after local buyers.
Retirement repays loans and resolves debt left on an empty borrower;
perishable stock leaves the books, while retained oil covers its claim
at net export proceeds before a haircut. Projects and bank bond purchases
share capital room, with pending loans counted once when they settle.
Insured mortgages keep their zero risk weight and finite leverage limit.
Order books find crossings across overlapping rounding bands; money floors
follow the unit. BankFailureCheck causes the faults and audits all 120
months of 24 hands-off Random cities. The apparent share loss was a
consolidation; SX1's wage-based split rule and the real-series law stay.

### 0.9.101 — SX1, The exchange's prices readable — 2026-11-14, ready, see docs/queue/SX1-notes.md
Shares now split above a quarter of the average monthly wage and consolidate
below a four-hundredth, with every holding, order and fund cost moving together
and charts kept per founding share. One shared formatter replaces percentages
above 1,000% with compound yearly growth over an observed span or a compact
multiple. Ordinary percentages keep their precision. Exact split, conservation,
wage-band, formatter and save/reload checks cover the change.
The 80-run ensemble passes money audit and findings; the full merged gate
has only the existing PlanCheck, KeptCheck and DeathRecordCheck reds.
Split-occurrence checks reject a no-split run and verify the audit in forty
actual split months of the played fixture.

### 0.9.100 — GD4, Gold mines by geology — 2026-11-14, ready, see docs/queue/GD4-notes.md
Gold fields follow geology, with declining grades at fixed ore throughput.
Gold Mining is the twenty-fifth sector and its Mine & Mill uses permanent
id 152, after the research labs. Bullion follows GoldPrice. Under Gold the
mint buys at its par, receives the metal and issues audited money when the
sale settles. New gold enters investment as valuables. Bullion quotes,
inventory and accepted offers survive a reload and currency reform.
The save format and load floor rise together for the wider land rows.

### GG1 — The new gate's gaps — 2026-11-13, ready, no tag (process), see docs/queue/GG1-notes.md

Versioned dry runs read provisional records without advertising readiness.
The builder and gate share retirement and explicit transfer of known reds,
and registry-only changes can reuse a kept run with a fresh judgment.
Missing queue titles come from the changelog heading; added source version
tokens, lost heading separators and reused batch IDs stop publication.
The batch guide records the contract; self-tests cover all seven gaps.

### PO1 — The path lines as nightly observations — 2026-11-13, ready, no tag (process), see docs/queue/PO1-notes.md
Played-path premises in 29 harnesses report OBSERVED and skip their dependent
laws. Reached laws retain their correctness verdicts, and shared helpers keep
unlisted callers hard. Nightly records each line's last hold and merge range.
Fifteen superseded repository known-red entries are retired. Review repairs
cover legacy log bytes, running-harness attribution, full price data, independent
maturity experiments, clock ownership and visible observation totals.
The full pick and fresh no-tag gate are green with known reds. Main's hard
section 63 fund-retention miss is traced and queued as KF63.

### EI1 — The ensemble's outcome criteria as information — 2026-11-13, ready, no tag (process), see docs/queue/EI1-notes.md

The ensemble gives every criterion a hard or info class. M1 (money audit) and F1 (findings) alone determine
its batch verdict. Every other criterion keeps its measurement, threshold and verdict as information,
beside the baseline's value and verdict, with changed verdicts called out. Old baselines remain readable.
The runner retains scored artifacts and returns 4 on a hard failure. Self-tests cover information failures
passing and audit/findings failures stopping in every policy. The batch and gate rules use the same split.

### HA1 — the gate's harnesses — 2026-11-13, ready, no tag (process), see docs/queue/HA1-notes.md
Harnesses start longest first from their last durations; soft runs skip
clock-only rounds. The gate's nightly tier runs when directly picked;
nightly reds use main's judge and name their merge range. Five merged fixture
groups keep their assertions. Dead old-format converters, ConversionCheck
and the listed run observations are removed. Current-format land repair
stays; damaged centres are refused before rebuild. Gate records retain the
reds of unrun harnesses. Allocated-land restoration stays AW2's.

### 0.9.99 — AW2, allocated land kept through a normal load — 2026-11-13, ready, see docs/queue/AW2-notes.md
Saves carry the live allocated-land counter and its building footprint, so an
ordinary load keeps the same free ground and construction behaviour while
allowing for later catalogue plot changes. Saves without the counter retain
the previous footprint reconstruction; explicit zero remains zero. SaveFileCheck
causes the WG2 fixture's land gap, changed plots and continued private/automatic
building with land purchases; LongPlaytest counts and traces ledger gaps.

### RT3 — the retirement count — 2026-11-12, ready, no tag (harness), see docs/queue/RT3-notes.md
LongPlaytest counts trailing sector-year windows in which retired buildings
take over one fifth of the sector's opening posts, with episodes by sector
and the largest share and month. It reads both retirement rules' actual
sales and excludes bank branches from Retail's count.
The picked check passed with recorded known reds, and all eight default
CSV traces match main byte-for-byte.

### 0.9.98 — AW1, The auto city always builds its works — 2026-11-12, ready, see docs/queue/AW1-notes.md
The standalone test city and monetary ensemble now start automatic building
with Build from cash anyway enabled, using the existing cash-funded works
and land-purchase path. A caused fixture checks power and streets ordered
with their ground beyond the debt limit. Monthly measurements count switch
inactivity, months over the limit with no order and essential services under a fifth
served. The 80-city ensemble scores 24/26 against 0.9.97's 23/26 and retains
every previous PASS. The normal-load allocation discrepancy is queued as AW2.

### 0.9.97 — GDP1, Works forecast their inputs at current prices — 2026-11-12, ready, see docs/queue/GDP1-notes.md
Works now cost their next run on the current world, exchange and freight band,
using the shared clearing calculation instead of the preceding month's price.
The common path covers flow goods, stockable output and captive inputs;
recorded trades and the inventory rule retain their own accounting periods.
Wire and rod fixtures cause a feed-price rise, refuse exports below input
cost, and retain real margins; processing and extraction fixtures prove
unsold inventory and its later export count once. The RT3 seed-11 replay's
month 902 GDP is positive and its money audits hold. The original negative-GDP
finding is unchanged. Full checks and ensemble evidence are in the notes.

### 0.9.96 — BS1, Bond settles to a fixed point, the bid tick and the buyback — 2026-11-12, ready, see docs/queue/BS1-notes.md

Corporate bond issues converge within 1e-14, and bond orders quote on a 1e-12 price tick, keeping the price-level and denomination twins equal within their bounds. Whole-piece buybacks take every offer in full, including typed faces within the quantity rounding, and cancel unpaid sales at their exact original proceeds. The bank offers its paid face, and cancelled proceeds leave its next settlement. The cancellation has its own legend and colour. Regressions cover typed faces, premium reopenings, curve consistency, the money audit and the bank's equity split through the following months. The ensemble scores 23/26. Jerus accepts A1's 10/16 against 13/16 seeds in band and FoodProcessingCheck's meat-bet line as path noise, with the latter's fixture repair queued as FPK1. KeptCheck's funding and household tooltip fixture transitions are traced and queued as KBF1 and KHT1.

### 0.9.95 — DM2, The wall ways dated in a journal in the map's sidecar — 2026-11-12, ready, see docs/queue/DM2-notes.md

The map remembers when each wall way was kept clear or released and dates resource-site and visual-
unit changes beside its runs. Replay moves only boxes intersected by new work or a new way,
preserving every other standing box. FORMAT 16 saves these journals, with older sidecars redrawn
canonically. Actual rail and yard cure, idle expiry and return, works arriving under existing boxes,
delayed views and save/load round trips are checked. The default model trace is unchanged and the
4,000-month layout watch passes.

### KS1 — KeptCheck's fixtures cause their conditions — 2026-11-12, ready, no tag (fixtures), see docs/queue/KS1-notes.md
KeptCheck supplies counted held works, chooses an expandable income note
when it opens it, settles an explicit holding payment for its clock
checks, and supplies durable paper for Spending's debt fold. Sector
reuse measurements hold plant investment while monthly figures move.
Borrow has a finite-bid city that proves clearing and short-sale asks.
The original node-share, fresh-page and animation assertions are kept.

### GR2 — known reds in the repository, and what runs where — 2026-11-12, ready, no tag (process), see docs/queue/GR2-notes.md

The repository holds 77 harness-scoped approval parts seeded from the LM1 and
PW1 gates. AllChecks, batch-check and ship share their judge, and ship files
new unrelated reds and retires the entries a batch mends. Shared-code picks
select ALL and changed harnesses select their borrowers. Gates select the
merge's coverage and retain full, partial and fast launcher evidence with its
generated indexes for reuse against current known reds. The repaired Ready flow and stable assertion labels pass the focused checks.
The accepted fresh-clone full run remains known-red only under the stricter list.

### GR1 — timing judged nightly, not at the gate — 2026-11-12, ready, no tag (process), see docs/queue/GR1-notes.md
The gate runs its suite with timing soft. Hard timing and paired rounds move to
nightly on main, with machine-local figures, loaded-run tracking, paired median
confirmation and first-parent merge attribution. The morning summary names a
confirmed slowdown's batch and version. First quiet figures establish a baseline.
The self-tests pass, including 116 ship cases and five-merge attribution. A full
fresh-clone dry run passed and the laptop nightly retained 151 timing figures.

### GF1 — the gate fails safe — 2026-11-12, ready, no tag (process), see docs/queue/GF1-notes.md
Fresh stage results and completion proofs prevent interrupted checks from publishing.
Main's publisher judges each merge. `--branch-gate` exercises the branch publisher
only with `--dry-run`. Real publication requires a live guardian and checks
cancellation before tagging and every push attempt, rolling back stopped commits
and tags. The launcher keeps timestamped logs, returns the publisher's status,
stops its process tree and reports actual ref changes and the stop cause.
AllChecks ends over-limit harnesses and descendants after 1,740 seconds, completing
KD1's child-JVM cleanup item. AGENTS routes Codex as a builder. Verified 141/141
publisher tests, 13/13 picker tests, `--pick` 0, accepted full dry run 0 and final development stop 130.

### 0.9.94 — LM1, the land office map: a compact start, one number an offer, the deposits zoomed out — 2026-11-11, ready, see docs/queue/LM1-notes.md
A new city starts compact (Jerus, 2026-11-02, his city 7.8 km west-northwest of Stockholm on a river strip 5 km long). Its
water, one plant's, is one patch within `CityLand.FRESH_PATCH` (3 blocks) of the block it starts from, never through dry
ground, where A24 ran it along the river: Stockholm 61 blocks in 30 x 14 where it was 113 in 37 x 32. Where no patch that
compact holds a plant's water the city starts short and buys the rest (Paris 0.16 km2 of 0.49; 75 of 200 generated worlds,
5 on main); the default world founds block for block as before and the trace did not move (no ensemble). The map writes one
number an offer across the city, North 1-6 to West 19-24, as its row and its name do. Past the zoom where the deposits'
shading stops (a 2 km bar under 70 px) the map draws one icon a deposit, its resource's colour and mark (Fe, O, St, C, Cu, U),
thinned to one a 12 px square, with a Resources chip beside Nations, on by default. New: LandCheck's patch section,
EarthTerrainCheck 19 (Stockholm), MapCheck LM1. Moved fixtures: LandNationsCheck's Windsor (downtown on the bank) and
MapCheck 2c's second-block share; the new founding quiets main's six MapCheck reds, the fault not reached. Red by name:
PlanCheck 6, two + junctions 4 plots apart that CL4's curves lay on the new ground (PX1).

### 0.9.93 — RB1, the fund holds to its 10% — 2026-11-11, ready, see docs/queue/RB1-notes.md
The city's fund holds at most 10% of a company on its market book, now read on the holding too (Jerus, 2026-11-02:
"Sell the excess"). Past it, where the companies' buybacks push it, a tenth of the excess is asked each month at fair
value before the buybacks bid (`Exchange.postFundOverLimit()`), so a buyback cannot outrun it. On the probe city the
fund held 24-56% of the whole market from month 2,154 on main, and 7.3-8.9% on RB1 with no company past 10.5%. Its
idle cash, past the room its caps leave in shares and in bonds, goes to the budget at a 24th a month (or at the dial,
when that is more; nothing at a dial of 0): 16% of the probe's withdrawals. With none idle the transfer is the old one
to the bit. The Fund and Government screens say both, with the idle part of last month's and next month's transfer.
The ensemble scores 23 of 26 (`docs/ensemble/RB1.md`; 0.9.92 22): A2, A3 and R3 are main's. New: ExchangeCheck 7b,
FundCheck 9c, PolicyPreviewCheck 8's idle case, LongPlaytest's cap line. Red by name: KeptCheck 51, 66 and 67, fixtures
that look for their condition in the city (KS2), beside its two recorded reds.

### 0.9.92 — WG2, senior care's excess and the out-of-work verdict — 2026-11-11, ready, see docs/queue/WG2-notes.md
Migration aims at a healthy slack. The pulls (senior care, rent, crime) lift only the homes' draw, so senior care no
longer brings workers for jobs the city lacks (`Migration.jobsPull()`, `homesPull()`). Once the city is twenty years
old (`BRAKE_FROM_MONTH` 240, on the months migration has counted, saved), arrivals slow past 6% out of work and stop
at 12%, and the jobless past 6% drift away. The staffing gate's draw reads the same pulls and brake. The People
screen's verdict is green 3-7%, amber to 12% and red past it; its draw card shows each half with its own pulls and
says when the brake bites. On 0.9.91 the auto city is out of work 6.7% in m240-600 and 11.6% after (GV1: 10.0%, 16.1%),
at 84% of GV1's people. The ensemble scores 22 of 26 (`docs/ensemble/WG2.md`): A2 and A3 are main's; ZERO_HOT's T2
(73.4% against 75%) and HIGH's R3 (5 seeds over their twins, the slack leaving) are accepted, notes §5. New:
PopulationCheck's and StaffingDrawCheck's WG2 sections. Fifteen harnesses' fixtures cause their conditions again; no
claim moved. Red by name: HealthCheck (HX1), MapCheck and SlopeCheck (DM2, DM3), KeptCheck (KS1), LongPlaytest (CL6).

### 0.9.91 — GV1, the government's wealth and the test player's January — 2026-11-10, ready, see docs/queue/GV1-notes.md
Government > Overview shows net financial wealth in years of recorded revenue, its five bands,
and the existing buyback, tax and transfer controls with monthly previews. Band changes are saved
notices; the first full year's band is a quiet baseline. GV2 is folded in: the playtest, ensemble
and WealthRun share one January player rule, moving taxes, pensions/EI and fund withdrawal by
the wealth band, replacing scripted taxes; live cities remain under the player's hand. Discount
bills and affordable partial buybacks use the existing auction without repricing on a screen read.
GovernmentViewCheck, GovernmentPlayerCheck and InboxCheck hold the figures, gates and save path;
the final picked checks exit 0, eleven passed, and the kept page and its three doors were checked.
The ensemble finishes 80/80, 20/26 criteria, all audits clean: main's A2/A3 remain, H2 and three
population-size criteria newly fail. These findings stand, with calibration left as GV1-E.

### 0.9.90 — BO1, business owners, built and switched off — 2026-11-10, ready, see docs/queue/BO1-notes.md
The owner families (Jerus, 2026-11-02: "few in nature and very rich", "one per
listed company", "No job"): a household row after the prisoners',
`OwnerHousehold`, alone takes a listing company's founders' shares, two adults
a company, out of the labour force; it offers a twentieth of each stake a year
on the exchange (`BY_OWNERS`), a year at the floor or a failed bank sends a
family back to the population with its money and none of the shares, and
everyone else still buys. Saved, no format bump (an older save's rows read
with the owners' empty); OwnerCheck, ten sections.
Shipped switched off (`OwnerHousehold.IN_CITIES`): with it on, the playtest
city had a third of its people at month 120, the retired losing the dividends
that paid their care. The trace is byte-identical; switching on is Pending.

### 0.9.89 — GW1, the gate counts the draw — 2026-11-10, ready, see docs/queue/GW1-notes.md
The staffing gate counts, beside the workers the city has spare, the ones a building's own posts would bring within
a year (`Migration.drawFor()`, `Sector.GATE_DRAW_MONTHS` 12): only the part of migration's gap the order opens,
within the doors last month left free (`lastRoom`, saved; an older save reads it as none), at the going wage. A
drawn worker takes a post at or below their band, so a building the arrivals would staff is ordered and one whose
graduate posts nobody would come for is still refused. The hold and the build card name both counts. New:
StaffingDrawCheck; 31 harnesses whose fixtures assumed the spare alone cause their conditions again, no claim moved.
The ensemble scores 24 of 26 (`docs/ensemble/GW1.md`): AUTO2 ends 6% larger, with less unemployment and a young
city's inflation 2.74% against 3.39%, and H2 passes; A2 is main's red, A3 fails in one seed (AQ1). Red by name:
MonetaryCheck (MQ1), HealthCheck (HX1), DeathRecordCheck (OX1), KeptCheck (KL1), MapCheck (DM2). The tie rule and
the young towns' borrowing the first draw showed are queued (TQ1, DQ1).

### 0.9.88 — FM1, the money's sign and its place — 2026-11-10, ready, see docs/queue/FM1-notes.md
The city's money sign is the player's, before or after the figure ("ƒ2.6M",
"2.6M kr"), with a space only where a letter touches the number. One writer
(`Currency.write`, `qualify`, `marked`) writes every money figure on the
screens and in the model's text, so the sign chosen at founding reaches every
page; beside a foreign figure the city's initial qualifies it only where a
vault mark could be mistaken for it ("D$", "T¥"), and "kr", "zł", "R$" stand
alone. The founding screen has a sign field (three characters, typed or picked
from the signs the interface's font draws), before/after chips, and its
figures shown in the money being chosen. History's own figures keep their own
marks (C$, US$). With "$" before, every string is the old one byte for byte
and the playtest's trace is main's; `MoneySignCheck` and `MoneyWordsCheck` new.

### 0.9.87 — GD3, the gold standard — 2026-11-10, ready, see docs/queue/GD3-notes.md
Gold is the fifth regime on Policy › The currency (spec-gold-money 4.3; Jerus, 2026-11-02, every call as recommended).
Entering it strikes the mint at the day's rate times gold's price, so the D$ keeps its value (D$20.6718 an ounce at
1.00 in 1900), or at a par typed. The rate is held at the gold points, 0.5% either side of the mint price over gold's:
gold is sold first at the weak point and bought at the strong. The law's cover is 40% of M0: a sale that would leave
less takes the D$ off gold (the break's new cause), and the peg's doubt is scaled by how far the cover is short of
full. Defend the gold, the autopilot's new aim under Gold, ties the dial to the US's rate and lifts it as the cover
falls, ten points at the floor. In April 1933, February 1934 and August 1971 a city on gold is asked to stay at its
par, follow the dollar or leave gold for Lean (a notice; the buttons on Policy). Slots 152-155 of foreignAccounts, no
format bump; GoldStandardCheck. GD2's two findings mended (gold's price never throws; eightOf never finds gold).
Lean is the default and the trace is main's: no ensemble.

### 0.9.86 — WS1, the minimum-wage page — 2026-11-10, ready, see docs/queue/WS1-notes.md
Policy › Wages gets the minimum wage's page (spec-wages 3.6; Jerus, 2026-11-02: a floor that says whether it is too
much or too little). Two gauges set the floor against what unskilled work goes for (under 70% it never binds, to 100%
it binds in a glut, over it even when hands are short) and against the average wage, with the world file's 33 nations
and the OECD's 38-55% striped. Cards show what a full-time minimum job pays for against one adult living alone, who is
paid the floor or within 10% over it, and the tie, chosen as the currency's regimes are. The ladder's ghost is the
floor's own move, not the market's walk; jobs with no posts are greyed; a joined market is named. FloorView holds
every figure and PolicyPreviewCheck sums each by hand. WG1's review is mended: the tie's last average is saved and
scaled by a reform (LabourCheck shows a load undoing a move before), and the tie's share is held to .15-.70, the
world file's range, short of the ratchet at 1. No format bump. The default tie is prices, so the trace is main's: no
ensemble.

### 0.9.85 — ME1, the manual's edition — 2026-11-09, ready, see docs/queue/ME1-notes.md
One edition of the manual (Jerus: "Yes, one edition now"): the six texts that waited under Pending are in the
published page, republished in place (its Version 22), and `docs/manual.html` and `docs/manual.md` are pulled from
it. The page has 26 sections where it had 25. Added: "When the builders are short" (section 9, FC1-M); "The nations
on the Trade tab" (16, WN6-M); "Founded in 1900, at 1900's prices" (17, EP1-M), and the calendar's line says January
1900 (24); "Land is sold by its nation" (18, WN5-M); Rankings, a section of its own (23, LB3-M); "Recording and
replay" (24, TR1-M). Each text was checked against main and brought up to 0.9.76: FC1b, WN5b, CM1, LB4 and LB5, the
save format at 51, and on the Rankings 47 of 70 measures with real figures and a founding dollar worth 28.4 of
2015's. The masthead keeps its date (0.8.11); a footer line says what this edition added. Docs only: no model
change, no ensemble.
The six manual rows it carries (FC1-M, WN6-M, WN5-M, EP1-M, TR1-M, LB3-M) leave Pending in the orchestrator's queue commit.

### 0.9.84 — GD2, gold in the vault — 2026-11-10, ready, see docs/queue/GD2-notes.md
Gold is the vault's ninth reserve, XAU, at the end of its moneys (spec-gold-money 4.2; Jerus, 2026-11-02: "All four,
mines last (Recommended)"). Game hands GD1's price for the month with the cross rates. Gold earns nothing, is revalued
as the other moneys are, and leaves largest-first. It comes in only by name or under Choose: the default All US$, the
world's mix and the trade's never buy it, and it anchors no peg. WorldMoneys.EIGHT stays eight, so the debt and the
policy rates know no gold. On Trade › The reserves, the exchange gets a Gold chip (the card shows the ounces and gold's
price), the chosen mix a "Gold (no interest)" row, and the vault table a gold row in ounces and US$. It is saved in
foreignAccounts' slots 149-151; an older save loads with no gold, and there is no format bump. VaultMoneysCheck 8-12,
and SaveFileCheck's fixture holds gold. The default mix buys no gold, so the trace is main's: no ensemble.

### 0.9.83 — WG1, a real minimum wage — 2026-11-10, ready, see docs/queue/WG1-notes.md
The minimum wage was the ladder's anchor: every wage was the floor times its job's ratio, so the dial moved the
doctor's pay with the labourer's and 94% of workers were paid exactly the floor. The ladder now rests on the going
rate, what an unskilled post pays where there are exactly enough workers, and the floor lifts only the wages under
it; a new city's starts at 85% of the going rate, Nova Scotia's $17.00 over a labourer's $20.00 Job Bank median
(Jerus, 2026-11-02). The floor can be tied to prices (the default, as before), to the average wage at a share, or to
nothing, a cash figure inflation wears down; the tie is saved. The Policy floor page says who it holds: pinned at the
floor or at the market's own bottom over it; its preview counts the workers a staged floor moves, the wage bill
and the city's own payroll, job by job. The ensemble scores 25/26 against 0.9.63's 24/26. No save format change: an
old city's floor was the going rate and loads as a real minimum there. Passed by name: LongPlaytest's layout-ring
finding at m2400, a known class left for DM2 (Jerus, 2026-11-04).

### 0.9.82 — HB2, the harbour's detail: its stock, its rail and a crane — 2026-11-10, ready, see docs/queue/HB2-notes.md
Jerus's picks from the renders, 1a, 2a and 3a, on the map from the 500 m scale bar in. Each terminal's box is HB1's open
yard as a picture (`HarbourYard`), its stock at one of eight steps by how busy its kind's terminals were in the month
billed (`Ports.busy()`: sea tonnes both ways over the standing berths' month; nothing saved). The railway lays a quay
spur to each quay line within half a district of the main line, from behind a box (`CityRuns.QUAY`, an arm on none of
the model's track, the sidecar's format as it was); the terminals on a railed line show their quay track, joined across
the gaps, and a train of wagons in a month their kind moved cargo (`QuayRail`). At a docked boat a crane's line carries
a box, or a grab of ore, between ship and apron, two moves a call, two cranes at a long berth, a tanker two still arms
(`QuayCrane`, `BoatSchedule.atQuay()`). PortCheck 11 and the new QuayRailCheck and QuayCraneCheck hold the model; the
screen (`MapView.harbourYards`, `cranes`) was not seen in the window. A Bergen played 2,400 months gets two spurs and
five railed terminals, Sydney and Perth none (their ports lie past the reach). The default trace is main's: no ensemble.

### 0.9.81 — GD1, gold's price — 2026-11-10, ready, see docs/queue/GD1-notes.md
`GoldPrice` gives US$ a fine troy ounce for any month of either path, a pure function of the path, the seed and the
month, never saved (spec-gold-money 4.1; Jerus, 2026-11-02: "All four, mines last (Recommended)"). The Gold Standard
Act's US$20.6718 from 1900, a ten-month path from April 1933 to Proclamation 2072's US$35, US$35 to July 1971. On
Historical, the USGS's annual price to 2025 at each July, the months between them log-linear (gold-usd-annual.csv:
Data Series 140, public domain; Mineral Commodity Summaries 2026, CC0). After it, and on Random from August 1971, an
AR(1) in gold's real price toward 2006-2025's mean, US$48.3 of 1900: 4.749% a month, a half-life of ten years. The
Credits page carries the two USGS sources. GoldPriceCheck: the pars to the bit, the table's months, a broken table
refused, the rule's slope 0.99405 against PHI 0.99424 and its sd 0.04754 against SIGMA 0.04749, a reload continuing
the path. Nothing reads it yet (GD2 puts it in the vault), so the trace is main's: no ensemble.

### 0.9.80 — EP2, the figures a screen hides under a threshold, at 1900's price level — 2026-11-10, ready, see docs/queue/EP2-notes.md
The screens said nothing of a money line under a literal half-thousand, $500 at today's prices and $19,300 of today
at 1900's: a young city's interest not in cash, its uplift, a coupon or a holder's share said "nothing owed", "none"
or nothing at all. One cut-off now, `Money.NOTHING`, half a thousand of today's dollars at the founding's level
($12.95 of 1900), in 66 tests on the Government, Finances, Trade, Bank and Fund pages and the window. The signed
ledger formats print "$0" only for a figure that rounds to no dollars (they printed it up to $499), and the Portfolio
folds a holding under a thousand of today's dollars at the founding's level ($26). The trade map's floor was already
in real terms (TP1). The audit's figures are kept by label beside its printed lines, so HoldersCheck, BondCheck and
ConstructionTrapCheck read them unrounded, at the model's cent. NewGameCheck's water-plant bond runs on the Random
path again as well as Historical. PriceLevelCheck 10 counts the lanes drawn, the holdings folded and every live line
shown, the same at the factor and at today's prices every month: 0 of 50,544 flags differ, and 11,050 lines the old
cut-off hid now show. The model is untouched.

### 0.9.79 — WP1, world prices chained, a study — 2026-11-09, ready, see docs/queue/WP1-notes.md
CU1's ★1 as Jerus took it: the January in which a made good costs its inputs in that nation plus a conversion,
designed, prototyped and measured (docs/studies/spec-world-prices.md; the prototype, tables and the refining
charge's record in docs/studies/world-prices/). Over thirteen worlds and 330 years rod stays on its cathode (1.00
of the literals' ratio; main 2.4-2.7), all 60 goods calibrate as today and the chain costs no time. An input must
be read at the cheapest maker's landed price; the price file's one-parent table mends copper only, the works' own
recipes hold every works near its literal; the roots anchored at the reference city keep copper on its path, where
main's falls to a third. Chained, seeds 0-3 have no copper industry and live on fabricated steel and cars, as well
off as main's to m1800 and at half its copper seeds' income a head at m2400. No yearly record of the refining
charge may ship as a data source; the charge is money, not a share. Main's solve can leave the numbers for good
(crude, two drawn worlds); CU2 carries the guard. Eight calls; CU2's brief is the note's last section. No model change.

### 0.9.78 — ST3, a town of ScaleCheck's own plan — 2026-11-09, ready, see docs/queue/ST3-notes.md
ScaleCheck section 6 no longer copies the playtest's town at a month re-picked thirteen times since 0.7.62. It plays a
town of its own plan (`ScaleCheck.ownTown()`), no advisor: the playtest's founding, the property tax at 4% (the one tax
open before 1916; at 1.5% the treasury stood at 100-400, and section 9's cash and the free ground missed), the advisor's
services each kept ahead on its own need, and Low-Rise Apartments when the landlords' family pressure passes 1. Every
line holds in 26 of the 33 months from 164 to 196 (5,800-8,100 people), at the factor and at factor 1;
`FREE_FIXTURE_MONTHS` is 175, the middle of 170-180. Section 9 holds the bank's branches in both copies as it holds the
sectors (`BusinessInvestment.holdBranches()`, harness-only; RE2's 975 of 1,000 closed in month 1), and ScaleCheck plays
LD2's wage. Main's two ScaleCheck reds are gone. Found: a building stack's quantity is an int, which a ten-billion city
housed in Houses reaches (SQ1 queued), and a town of 4,000 in 1910 buys whole cars, which parts a copy's savings. No
model change; the trace is main's.

### 0.9.77 — TR2, the ghost cursor and Follow in replay — 2026-11-09, ready, see docs/queue/TR2-notes.md
Replay can play the player's own input back: Follow (the bar's button, or L) runs the month on view in real time of
play at 0.5x to 4x, idle stretches skipped by default, and his clicks, drags, the wheel and his keys land in the copy as
the scene would deliver them (`ui/Reenactor`), so tabs switch, pages scroll, the map pans and hover cards show; months
turn when the log says, each loaded whole, and a join mid-month starts from its marker. The window is set to the logged
size first. The ghost cursor (H) is his pointer with a ripple at each press, shown while the copy is on his screen.
Nothing on a game menu (Save, Load, New city, Quit), under a dialog, on the rail's Menu or the clock, the clock's keys,
F11, or Esc and P where they open the menu is re-enacted (`ui/FollowRules`); the viewer's press, wheel, key or a move of
8 px ends Follow. TR1's review's four follow-ups are in (a failed log write stops, a save's recording id only of the
recorder's shape, a first write after a load clears the later months first and every task queued behind one that cannot
writes nothing, a month that autosaves filled once). ReplayCheck 7-10 (10 in the real window). Recording is off by
default and Follow lives in replay: no ensemble.

### 0.9.76 — LB5, the city in play is matched on its founding cash too — 2026-11-09, ready, see docs/queue/LB5-notes.md
LB4's review follow-ups. `SavedCities.Played` took a saved slot for the city in play on its name and world seed alone,
so a re-found on the same name and world (a second try) was marked "this city, as saved" beside the city it replaced;
it now compares the founding cash as well, read by `SaveHeader` from the `foundingCash` key every save already holds
(nothing is added to the save; a save without the key never matches). RankingsCheck 19 gains a re-found and a save with
no founding cash, neither marked. The review's second note, that a slot's stamp misses the header, is not a gap: the
header is the save file parsed, so its size and time are in the stamp already; RankingsCheck 21 proves each file alone,
and a save landing before its history, moves the stamp and the row follows. The window the note describes (the new
header over the old history until the history lands) is SaveWriter's order and stays, as LB5-B. The third, `placeSaved`
per redraw, was not taken (it is behind the cache check; nothing to measure). The default trace is main's: no ensemble.

### 0.9.75 — GW0, the staffing gate and growth, a study — 2026-11-09, ready, see docs/queue/GW0-notes.md
The wages study's question (3.4, C2): why the game's own city cannot go much under 10% out of work
(docs/studies/spec-staffing-gate.md; the kit and tables in docs/studies/staffing-gate/). A business orders a
building only when the workers spare today could fill 80% of its posts. So in seeds 0-3's first 40 years about 15
single-building orders a month fail on the spare alone (the steel and copper works, the pulp and paper mills, the
Machine Works, the Contact Centre), while the village waits with a fifth of its workers jobless. A gate that also
counts the workers migration would bring for the posts within a year (no new saved field) halves the young city's
out of work over 16 AUTO2 seeds (12.0% to 6.3%) and grows it (+58% at m240, +112% at m1,200). With WG2's aim the
grown city runs 8.3% (6.1% from m1,200) and is larger at every month; WG2's aim alone costs the young city 42% at
m240. A lower share, a 6- or 24-month draw and no share test were measured and not recommended. Four calls, each
with a recommendation; GW1's row is in the note. No model change; the trace is main's.

### 0.9.74 — CU1, Copper Refining's margin, a study — 2026-11-09, ready, see docs/queue/CU1-notes.md
A study, no model change. LS1 found Copper Refining buying its input at 52% of its sales and keeping 35%. The sector is
Rod Mills on imported cathode, exporting rod (276 by m2400 and 1,679 by m3996 in LS1's city; 363-446 by m2400 on seeds
1-3 of main, 251 on the Historical path; no smelter in any run), and its books are the world's rod standing 2.6 over
its literal ratio to cathode by 2100: each good is solved alone, the mean mill price is held to the path, and the city
buys from the cheapest maker (DR Congo's cathode) and sells to the best buyer. A real smelter, refinery or rod mill
buys 67-86% of its sales (USGS, the Economic Census): the literals are right and the played world leaves them. A
prototype that re-anchors a series' followers on its own good at the city's site, wire and cable as rod plus
fabrication, ends the mills on four seeds, keeps the people and halves GDP a head on the three that had them. The note
is docs/studies/spec-copper-margin.md, its probes and tables in docs/studies/copper-margin/. Queued: CU2 (the batch
that sets the margin), WP1 (a study), DS1 (a look). Seven calls, each with a recommendation. No trace moves.

### 0.9.73 — RI1, rail on islands — 2026-11-09, ready, see docs/queue/RI1-notes.md
Every linked piece of the city's ground now grows its own railway, as CL5 gave each its highways (Jerus ticked "Rail
on islands", 2026-10-31; CL5-C's call 1). A piece's rail hub is the railway crossing nearest its anchor, and an island
linked by a port has its berthed terminal for its anchor, so both its nets start at the port (ET5's "a linked island's
hub at its port"); home and a bridge-linked island keep the founding site's side. Track is dealt by built weight as
the highways are; a new yard goes first to an island railway with none, on its own line nearest its hub, and that
line grows to its edges as a station's does; sidings leave from an island's line. The first yard and the freight
station stay home, and no train is routed to an island yard (ET6-C's rail bridge row stands). Unlinked ground gets
none. IslandRailCheck (new, nine sections) on synthetic ground and the Rotterdam fixture; renders in docs/queue/RI1/.
Nothing in the model reads the map: the playtest's trace is byte-identical, no ensemble, no format change.

### 0.9.72 — R6, the near-future research catalogue, a study — 2026-11-09, ready, see docs/queue/R6-notes.md
The first half of R6 (Q2, 2026-10-18): what today's pilots and public roadmaps give the research catalogue after
2025 (docs/studies/research-near-future.md; the gatherers' fact sheets in docs/studies/research-near-future/). Eight
rows stand on a builder's date and a measured effect, all engineering or medicine: the driverless truck (May 2025),
sulphide heap leaching (Nov 2025), enhanced geothermal (Sep 2026) and the mRNA flu vaccine (Aug 2026) are already
history; the small modular reactor, the hundred-hour battery and the personalised cancer vaccine (2027) and
hydrogen-reduced iron (2028) are projections, each with its window. The evidence ends about 2030, not 2060; the
catalogue has no row for 1996-2024, where about 45 things belong; carbon capture at a cement works and the carbon
border tax pay only where carbon is priced, and the game prices none; every plant date traced had slipped by one to
four years. Eight calls, each with a recommendation. No model change and no row in research.json; the trace is main's.

### 0.9.71 — BR2, Steam's store art — 2026-11-09, ready, see docs/queue/BR2-notes.md
Steam's nine pictures are in `branding/steam/`, drawn by the new `tools/branding/make_steam.py` from our own art
(Jerus, 2026-10-31: "From our art (AI)"), at the sizes Steamworks' asset pages give (read today): the header, small,
main and vertical capsules (920 x 430, 462 x 174, 1232 x 706, 748 x 896), the library's capsule (600 x 900), header
(920 x 430), hero (3840 x 1240, no text, as Steam asks) and logo (1280 x 720, a transparent PNG), and the page
background (1438 x 810). The picture is the icon's "towers as a chart" drawn wide: lit towers under a rising chart line
with ringed dots, the title art in the sky above on a scrim, the name the only text; the name reads at the small
capsule's two generated sizes (184 x 69, 120 x 45). Seeded, so a rerun gives the same files. `BrandingCheck` section 5
holds the folder to Steamworks' list and the tool to the same names and sizes. No game code, so the trace cannot move:
no ensemble. The Steamworks verification, the upload and the store page stay Jerus's.

### 0.9.70 — WN4c, a peg whose anchor lost its price keeps the loaded rate — 2026-11-09, ready, see docs/queue/WN4c-notes.md
WN4b's review follow-up. A save whose peg is anchored to a money the save no longer has a US$ price for (the euro's
rate gone) falls back to the US$, as before, but its peg had kept the saved D$ per euro, which the US$ peg then held as
D$ per US$ (1.1000 held where the city stood at 1.0523); the fallback now holds the loaded rate, as the peg's own
month would have struck it (PegAnchorCheck 8, red before the fix). The review's two low notes were looked at and
neither is wrong: in a held month the hold's support is exactly the log of the held rate over the shadow, so it needs no
clamp, and the dial reads the gap against this month's anchor price, the held rate the reprice then measures against
(both pinned in PegAnchorCheck 10). Nothing in a played city reaches the fallback, so the default trace cannot move:
no ensemble.

### 0.9.69 — AL1, automatic building links the city's islands — 2026-11-09, ready, see docs/queue/AL1-notes.md
Automatic building now links ground the city owns across the water (Jerus, 2026-10-31: "autobuild will build bridges
and ports if sees that land is low and there is something unliked"). When a month's order lacks linked ground and an
island the city owns ground on is unlinked, the AutoBuilder orders, after every other order of the month: a Highway
Bridge where an Elevated Highway stands, else a Road Bridge, where the survey finds a crossing; else the cheapest sea
terminal on the island's shore, and one at home where no linked ground has one (a port already there is shared). One
rule (`BuildAdvice.linkFor()`) serves it and the test player; money, ground and refusals as its other orders. No Rail
Bridge (nothing runs a train over one). A survey that finds no crossing stands a year. The map's highway over water
links nothing: the model never reads the map. IslandLinkCheck (new) causes each case on the Rotterdam fixture;
LandmassCheck 12's "stays unlinked" is held to the cue. Overturns ET5's call 5 and ET6-C's "never orders a bridge".
One landmass reads nothing: the default playtest's trace is byte-identical, no ensemble, no format change.

### 0.9.68 — TS1, the toast stays out of the way — 2026-11-09, ready, see docs/queue/TS1-notes.md
While a toast shows, the page gives up the corner it stands in: the page's foot takes a bottom padding as tall as the
toasts and the stack's margin under them, so a page scrolled to its end has its last control clear of the toast (the
Build overview's "Turn automatic building off" went under the 33 px toast for about 50 px of scrolling; PT1, item 10,
Jerus ticked "Toast stays out of the way", 2026-10-31). The scroll keeps its pixel offset as a toast comes and goes (the
ScrollPane does it itself; a first version that moved it by hand put the page 15 px out, and ToastCheck caught it), and
the room goes back when the last toast does, when the stack is hidden on a game menu and when cinematic mode takes it
out of the window. The toast still fades in 8 s and goes when clicked. New `ui/ToastRoom` and `ToastCheck` (JavaFX,
the window's nodes on a scene of its own: the last control under the toast without the room and clear with it at one
toast, three and a wrapped one; the offset held; the room given back; a rebuild with a toast). Screens only, so the
default trace cannot move: no ensemble.

### 0.9.67 — MB1, big-city map bands — 2026-11-09, ready, see docs/queue/MB1-notes.md
In a city of more than 128 districts the map's chain is cut into bands, and they were cut of the map's order
(distance from the founding site), so a district bought nearer the site than some already on the map moved every
later band's members and root, and their boxes (MF1-B). The bands are now cut of the chain's own order: each
district ranks by its ground's first purchase (or its first site's), a new district ranks last and joins the last
band, and no band before it changes. A city of one band (Jerus's 93 districts) is drawn exactly as before.
MapCheck 2i causes it with bands of 2: the old rule moved 2,885 of 3,478 boxes in the other bands, the new 0 of
4,938. MapCheck 5's month at 5B and 10B, alone and alternated with main over five rounds, stays inside its 5 ms
(the bounded months' worst 2.9 and 3.2 against main's 2.7 and 3.0, the ranges overlapping), and section 18's
month at 5B reads as main's, alternated three ways, the chain 0.1 ms of it. Map only: no ensemble, no format change.

### 0.9.66 — BB1, the buyback card shows what the auction would book; offers capped at the call price — 2026-11-09, ready, see docs/queue/BB1-notes.md
Finances › The book's green or red stays at fair, and Close it now leads with what the reverse auction would book:
the gain or loss at the holders' asks against carrying value, large, the gain at fair for the same face under it,
and why they differ - the holders ask so much over fair, the one asking most named (CD6-C ★1, Jerus, 2026-10-31:
"show fair, but when click buyback it shows what the actual gain or loss would be"). In the render a line the
households hold reads +D$8.5k at fair and books -D$292k at the auction. The face's quick picks are a quarter, a
half and all on offer, the one picked underlined; the cheaper offers go first, so part of a line costs less for
each 100. On a line the city can call now no holder is offered more than the call price, 100 (CD4-C ★6):
BuybackCheck 9 for the cap, 10 for the auction's figure against fair's. Nothing in the month buys back, so the
default trace cannot move: no ensemble.

### 0.9.65 — UP9, x 80's chain: the lazy try backs off, the reserves kept — 2026-11-09, ready, see docs/queue/UP9-notes.md
At x 80 a grown month's carry nearly always moves, so the map's lazy try failed and its band was handed out again
with its inputs: 250 plan jobs a month, 124 of them doing nothing. A band whose lazy try failed now gathers its
inputs at once, and tries lazy again after a quiet month (UP5-B): 126 jobs, none wasted, and the far screen is
coloured once a month instead of twice (MapCheck 18, paired against main over four rounds: 29.7 to 18.1 ms now's,
34.0 to 23.3 today's; the chain's FX side 27.0 to 26.0). The interchanges' reserves are kept across months
(MP1-C): the map's guard moves only with the mines and shore works it reads, a digest of exactly those kept by
district and walked only over the districts with runs, so a month's change stays inside section 5's 5 ms (5B 1.7,
10B 2.8 ms); 185 of 401 asked a month are found again, and the kept reserves and digest are asserted equal to
fresh ones every month. `CHAIN_ROUNDS` stays 5. Map only: the default trace is byte-identical, so no ensemble.

### 0.9.64 — LB4, Rankings: the city in play beside its own slot, and a lighter read of the saved cities — 2026-11-09, ready, see docs/queue/LB4-notes.md
A saved slot holding the city in play (the same city name and world seed in its header) now reads "this city, as
saved · slot N" on the Rankings board and in its drawer, and is left out at Your age, where it only tied with
itself; the not-at-age line names it instead (LB3-D). The saved histories are read with Gson skipping every series
but the ~58 the board keeps, and those keep as `double[]` and `long[]` rather than boxed lists (LB3-R): ten
6,000-month cities hold 32 MB after the read, not 97. No model change; the trace is main's. RankingsCheck proves
both (19, 20, and 17's packed tally); UiProbe measures it with `-Dprobe.saved=N` in its own scratch folder.
(Jerus, 2026-10-31: "Take all (Recommended)".)

### 0.9.63 — RN1, a glut's rent under the carry; S1 restated — 2026-11-09, ready, see docs/queue/RN1-notes.md
A landlord that borrows dear no longer lifts the rent of an empty market to cover its debt (EN1-C ★2): a rent leg
with fewer than one household a door (`RealEstate.GLUT_PRESSURE`) takes its required return times the scarcity
multiple and no carry lift, its empty flats the landlord's loss; the other leg keeps the lift as it stood. A closed
home's mortgage no longer loads the homes left standing: the landlords keep the share of their debt whose homes are
gone (`strandedDebtShare`, saved), and the break-even counts only the rest, though the company still pays all its
interest. HousingCheck 3c holds Jerus's zero-profit floor where a market is tight or balanced; a city grown ten
years and held at 15% for ten proves the waiver fires (39 months studio, 49 family) and the share survives a save.
The ensemble's S1 now reads the currency's fall: each seed's excess inflation over its twin is within the
currency's extra fall against the dollar plus the anchor's tolerance (EN1-C ★1). Ensemble 24/26 against 0.9.57's
23/26: S1 and R3 now pass, A2 is red as before, A3 red by one month in one seed of sixteen (RN1-A).
At the gate FittingCheck c retires depots until a month's crews cannot fit the drive; HealthCheck's fee twins red by name (RN1-F).

### 0.9.62 — CR1, a Credits page from the title screen — 2026-11-09, ready, see docs/queue/CR1-notes.md
The title screen and the Esc menu have a Credits button under Settings, and the page it opens shows
the two data files' credits in full, the Earth's (`ATTRIBUTION-2.txt`) and the nations' (`world/ATTRIBUTION.txt`),
then the fonts' notice (IBM Plex Sans and Mono, Copyright © 2017 IBM Corp., SIL Open Font License 1.1) and the
licence's own text. The jar carries both files straight from `tools/` (`pom.xml`'s resources), so the page and the
copies beside the exe have one source each, and a notice a file gains is on the page with it. Both files' "ships
beside the exe" lines now name the page. `Credits` is the page's model; `CreditsCheck` proves every file
`Build EXE.bat` ships as a credit is on the page word for word, the fonts are all IBM Plex under that notice, and
a build missing a file says where it is. Checked by eye.

### 0.9.61 — MW1, a city saved with the world file and loaded without it — 2026-11-09, ready, see docs/queue/MW1-notes.md
A city saved on the US's price path and loaded without `world-1.bin` no longer walks its dollar back to the flat
trend of 1 (WN4-W): the walk's trend now starts at the level the path hands it, at the month it takes over, so the
dollar carries on from where it stood, inside the walk's own spread, instead of losing half the gap in about ten
years. `WorldEconomy` keeps the trend's base, its start month and whether the last month was on the path, three
new slots at the end of the world's saved array (no format bump: an older save reads 1, 0 and off, and walks as it
did). A city that never had the file walks as before, to the bit. `RealDollarCheck` section 8 proves the hand-over
on a bare world (a decade on, the level handed times a walk from 1) and on a scratch town saved on the file and
loaded on the literals; the pre-MW1 walk falls short of it by more than half what `TREND_PULL` closes in ten
years. The default trace is byte-identical, so no ensemble.

### 0.9.60 — LS1, the labour share by sector, a study — 2026-11-09, ready, see docs/queue/LS1-notes.md
Each sector's payroll over its value added, a year at a time, through the default playtest (seed 0 to m3996, seed 1
to m2400) and Earth's Rotterdam (to m2400), against BEA's compensation over value added by industry
(docs/studies/spec-labour-share.md; the probe, BEA's tables and the runs in docs/studies/labour-share/). Luxury
Retail's 2% (BEA's other retail 41.7%) is there from m240, and the mean wage keeps up with the price index in every
run, so it is not wages trailing prices; nor the markup (pieces bought at 41% of their price). It is a Boutique's
sales per post: 320 pieces a month of a $9,000 good sold near $22,000, for 7 posts. Seed 0's old city pays 10% of
its value added because Copper Refining's margin (its input at 52% of its sales) is 72% of it. Restaurants,
Construction and Freight sit at 0.3-0.5 of BEA's, each half under twice off. No model change; the trace is main's.

### 0.9.59 — WN4b, the D$ pegged to the euro or another of the eight — 2026-11-09, ready, see docs/queue/WN4b-notes.md
The peg (XR2) can now be kept in any of the eight moneys with a rate this month, not only the US$ (WN4's XR3 row).
The peg is kept in its anchor's money (`ForeignAccounts` slot 148, the anchor; no format bump, an older save reads
the US$), and the rate it holds this month is the peg over the anchor's US$ price, so under a euro peg the D$ moves
against the US$ as the euro does. The shadow's gap, the hazard, the hold's lift (XR5) and the break's overshoot are
all against that held rate; the shadow stays the D$'s own rule, so a peg to a money the city does not trade in
also pays that money's swing. Choosing the anchor under a peg in force re-pegs at the day's cross, as the krone
moved from the mark to the euro; a devaluation is in the anchor's money. The tied dial follows the anchor's
central bank rate (XR4's PolicyRates) plus the spread. Policy › The currency's THE PEG has the picker of the eight,
the peg in the anchor's money with today's D$ per US$ under it; Trade's peg lines name the anchor. A US$ peg is as
it was to the bit (`PegAnchorCheck`, 115 assertions); the default trace is byte-identical, so no ensemble.

### 0.9.58 — LC2, the Rankings' measures back from sources that allow sale — 2026-11-08, ready, see docs/queue/LC2-notes.md
Nine of the seventeen measures LC1 took out rank the world again, each licence read into `docs/studies/licences.md`
first: inflation as the World Bank's household consumption deflator (164 nations; LC3-L ★1), deaths under five from
WPP 2024 (196; 2024-25 its projection, mark 2), credit to firms and households from the BIS's total credit (43), and
for the OECD's 38 members only the current account, debt, budget balance, tax revenue and interest from the Economic
Outlook 119 (general government) and health spending from the health accounts. A constant-price break in a series is
no longer read as inflation (Bulgaria's -32% in 2025). The real rate stays the player's: the BIS's policy rate is not
a lending rate. Five measures are back in WorldMeasuresCheck's POSITIVE; RankingsCheck's section 16 names the real
rate for a measure still out. The month never reads the file, so no trace moved and no ensemble ran.

### 0.9.57 — CD9, the foreign debt's follow-ups — 2026-11-08, ready, see docs/queue/CD9-notes.md
A money under zero sells under zero. Each money's curve abroad floors at its own rate, not the city's 0.5%, so the
franc and the euro in their negative years sell at a yield under zero where the chip read -0.6% and the sale was struck
at 0.5%; the coupon floors at 0% (`DebtManager.MIN_FOREIGN_COUPON`) and the paper sells over par, as real zero-coupon
issues did (CD8-F). The fixed issue cost is converted into US$ as for the other seven (CD8-I). One revaluation: the old
D$-only figure, which missed the crosses, is retired for CD7's booked line, which the playtest, the Trade screen, the
debt card's chip and the Summary now read (CD8-R). EARNED's interest carries the month's coupons abroad, saved as
`cityForeignInterest`, so the walk from EARNED to the budget leaves nothing of them (CD7-E). No format bump. Found at
the gate: the fund's closed lots fold drew all 5,562 lots of an old fund, over a gigabyte, and lists the newest 200 now
(`FundScreen.CLOSED_FOLD_ROWS`); KeptCheck sells its 20-year bond first, for the sections that draw it. The ensemble:
23/26, as 0.9.54; ZERO_SLACK's median city 332,066 people against 349,699.

### 0.9.56 — WN3-R, the band's monetary study — 2026-11-08, ready, see docs/queue/WN3-R-notes.md
MonetaryCheck, PassThroughCheck and CurveCheck play on the world's band again, with no model change, because the
premise already holds there on 0.9.55. The held dial gives 7.22/5.92/5.25/4.96% at 3/10/20/40%, the month-late rows
within 0.052 points (main's recorded red on the literals is not met). R3 passes on the band: 2 seeds of 16 over +1
point, median -2.87. The R3 red in 0.9.54's baseline is the literals' run, since a worktree's ensemble names no world
file (EN2). In every seed where the 15% rate runs hotter than its twin, the cause is the landlords' carry lift as their
mortgages renew at the held rate. It binds in 99% of the months, so it is kept: it is a home's user cost and Jerus's floor of
0.7.11. The window's long draws trap no young city. FL1-E and HD2-P pass as measured. No save change; the trace is main's.

### 0.9.55 — HB1, a city's ports bundled into one harbour — 2026-11-08, ready, see docs/queue/HB1-notes.md
A harbour's terminals stand side by side: each new one takes the shore spot nearest one of the harbour's works
(`CityMap.harbourSpotBundled`), where MF1's search filled the harbour's 3 km disc tile by tile (Jerus's Bergen, 0.8.90: 27
works over 3.7 x 4.9 km). Every terminal is kept, each landmass keeps its own harbours, and works laid before never move
(MapCheck 9c: on a small coast 9 of 9 side by side against 8, the quay one piece against 3). The buildings under a new work
move off it through the district journal, as a yard's do, and the layout watch flags none. From `MapFrame.DETAIL_FROM`
(3.6 px a plot, where the scale bar first reads 500 m) boats are drawn true size, a harbour's quays as one joined wall, and
a docked ship lies alongside its quay where it fits, else stern-to, never across a box, a wall or another ship
(`BoatSchedule.berthed`; PortCheck 10, 10b); below it, as before. A berth's lane starts on its sea side (`SeaRoutes.startOf`).
The terminals' look waits on Jerus's pick of three renders in `docs/queue/HB1/`. Map only: the trace does not move, no ensemble.

### 0.9.54 — RE2, Real Estate's workers back in — 2026-11-08, ready, see docs/queue/RE2-notes.md
Jerus: "gotta add workers to real estate". A Studio Apartments and a Low-Rise Apartments each have one office and one
maintenance post, both DIPLOMA (the National Apartment Association's staffing rule), and a House has none, as HD1 built
them. The landlords build homes before anyone can staff them (they do not ask the staffing gate), and the rent floor carries
their payroll. HousingCheck section 6 is re-caused, with the landlords held through its build-up. ScaleCheck section 9's miss
is the 1,000-times copy's bank closing its branches, not the posts; ST3's brief now has it. The ensemble, run as 0.9.50's
baseline was (no data files), scores 23/26, main's count: HIGH R3 now fails and SHOCK T2 passes. Towns end at 0.59 to 1.35
of main's and none stalls. Seed 3 shows RE1's trap: the landlords at a loss, a 59-month down-payment hold, and the town at
20,000 for 160 months while main's doubles. RE3 is queued for it (RE1-T's way (a)). No save change.

### 0.9.53 — MT1, the map's words — 2026-11-08, ready, see docs/queue/MT1-notes.md
The map reads better. The chip on a landmass no port links is two lines on a dark rounded plate the width of its words
("Island 2 · 5.37 km²", then "not linked: needs a port here (none has one)"), where it was one 70-character line whose
only ground was a 3 px stroke over the island's own amber hatch (BK1-L). The six biggest landmasses are named by hand
where the data named them by their most populous place: "Afro-Eurasia" (not "Mumbai's"), "North and South America"
(not "New York's"), "Antarctica", "Australia", "Greenland" and "New Guinea", on the founding card, the bridge card,
the port card and the chip; the guard is the id and the data's place together, so another bake keeps the data's
words (ET1-C 2). The legend's and the hover's "Ice" is "Snow and ice", the study's words (ET1-C 3). `EarthData.CACHE_BYTES`
is 24 MB, 83 tiles of earth-2 where 10 MB kept 33: Rotterdam's far node at 1,024 plots a pixel falls from 127 to 69 ms
and at 512 from 31 to 8 ms, and MapCheck's "every stride within SCREEN_MS" is green on the full file (ET1-C 1). Map only:
the model, the saves and the trace do not move, no ensemble.

### 0.9.52 — LF1, the short's lending fee on the lenders' books and four edges of EM2's short book — 2026-11-08, ready, see docs/queue/LF1-notes.md
The world's lending fee for the shares it is short (EM2) reached its lenders with no record: the households' part went from the world
into their savings with no line, so the current account never saw it, and the fund's part touched its cash alone, so its income, its
"Company shares" gain and its ledger left it out. Now the households' is an audit pair like ForeignInterest's (`+ households
LendingFee` INCOME, `- households LendingFeeBanked` DOMESTIC), and the fund's has income counters (the month's and the life's, saved)
and a LENDING_FEE ledger row, in FundView's income and kinds and a line on the Fund page. EM2's four edges are fixed, each caused
by a fixture first (ShortSellingCheck 8): the fee and the lent book stop at what the lenders hold; emigrants are capped at the shares
plus the short; a short at a company with no shares in issue takes the shares it sold with it; a short at a company at minFair is still
called in. No short opens in a real run, so the trace is unmoved (not run through the ensemble); no format bump (new tail slots).

### 0.9.51 — MF2, the map's latent faults and Jerus's Bergen city — 2026-11-08, ready, see docs/queue/MF2-notes.md
The map alone; the default playtest's trace is byte-identical to main's. The city's one rail yard is dealt to the district
whose ground it stands on, not the first in the map's order (WN3-M(a): a depot arriving earlier moved boxes), and the yard's
own depot is the oldest one standing, so a depot that leaves takes only its own box: this was main's own playtest red at
m2396 (3 boxes moved), and the playtest now ends green. From his Bergen city: a bridge on a diagonal is drawn on one deck
along its line, not a staircase; an avenue no longer runs through a cloverleaf built after it, and the loops are drawn whole
over any street left there; a railway arm turns no corner sharper than 45 degrees (no V to a dead end). Each fault is caused
first by a new section (MapCheck 20, 23; PlanCheck 19; JunctionCheck 22, 23; MainLineCheck 16). A purchase among large works
and a store leaving move nothing on main (MapCheck 21, 22 kept as guards); XR4-M is left to DM2. The diagonal highway that
cuts the blocks is queued as FA1, its fixture a probe. No save change.

### 0.9.50 — BK2, each holder's own discount on a line of the city's paper — 2026-11-08, ready, see docs/queue/BK2-notes.md
A line of the city's paper kept one pool of unaccreted discount, every holder's share pro rata by face, so a reopening at a
new price re-blended it for every holder and moved the bank's unearned discount with no cash (BK1 and BD1 booked the move to
retained; up to -$1,641k in a month in AutoBuildCheck's cities). Now each holder - the bank, the households, the central bank,
the firms, the world - carries its own, set at the price it paid at the settle or the add-on, accreting straight-line, and face
that changes hands carries the seller's discount per face (`Debt.Holder`, `pass`, `takeAtSettle`). A premium first eats the
buyer's own discount. The re-strike is nothing at every reopening at a discount (HoldersCheck 12, 13, 15; CentralBankCheck 22,
BK1-C: the central bank's par rolled and added on into a line the households hold part of). Save format 51, and the floor with
it: a format-50 save is refused. The ensemble: 23 of 26 (SHOCK T2 the seeds' scatter, by a four-seed diagnostic); KeptCheck's
bench city took another path, KC1 queued.

### 0.9.49 — UP8, the 10B month: the Remark pause, and the walk once the copies are full — 2026-11-08, ready, see docs/queue/UP8-notes.md
The gates' doubled 10B month (RT1's three, XI2's 7.77 ms against main's 3.92) is G1's Remark, not the batches' code:
MapCheck's JVM holds 580-750 MB live from its earlier sections, marks about 25 times a run, and a Remark of 2-5 ms that
falls inside one of section 5's 13 timed months takes a month of 1-2 ms to 5-8. XI2's two builds rerun here came out
the other way round (main 5.25 ms, the merge 3.11). A `System.gc()` before each copy's months now measures the month
on its own garbage, and its line counts the collections inside it. A full city's month (the 10B copy after about 32
months of +0.01%) cost 12-32 ms in `roomiest()`'s scan of all 29,255 districts: THE ROOM TREE (`CityMap`, max trees
beside GT1's free plots) finds the next district with room, the cursors' next and the roomiest in about 15 steps, and
the full month is 0.4-0.9 ms (main 13-17), its placements main's month for month. MapCheck 5b grows the copy full and
times it, no budget of its own; 5 holds every log and the walks' room exact after every month.

### 0.9.48 — TP1, the map's lane floor through a currency reform — 2026-11-08, ready, see docs/queue/TP1-notes.md
The brief's first two faults (trade by partner not rescaled by a reform) were already mended by DZ1 (0.9.4); this closes the
third. The map draws a partner only over a floor, `TradeLanes.MIN_LANE` (300, the board's D$0.3M), which was absolute city
money: a reform dividing every trade by 100 left it and hid every lane and arrow, and inflation drifted it. The floor is now
`TradeLanes.minLane(priceIndex, unit)`: the board's figure at the founding's prices (times `PriceLevel.FACTOR`, as EP1 prices
every anchor), times the price index, over the currency's unit, struck into each partner snapshot and carried to the map by
the feed (`MapView` rebuilds lanes and arrows when it moves). A 1900 town's map therefore shows a lane from about D$7.8
thousand a month of trade instead of D$300 thousand. DenominationCheck's section 7 gains the floor's arithmetic and, on its two
cities either side of the reform and with prices moved, the nation card, the month's figures, the floor, the lanes and the
arrows. Display only: no save change, no pool moves, the trace unchanged.

### CX2 — a lighter start and a slower climb for the Opus sessions — 2026-11-07, ready, no tag (process), see docs/queue/CX2-notes.md
After CX1 the Opus sessions were most of the plan: 120 Opus transcripts in a week, 3.79B tokens. A batch session starts at 86.7k before its first read and re-sends a mean 249k a step over a median 161 steps (36.6M tokens); the orchestrator begun on 11-05 was still running two days on at 792k a step (323M tokens, 8.5% of the week). The start is mostly tool definitions (Artifact alone 12.5k), the system prompt and the listings; past step 150 lie 31% of a batch session's tokens, and 88% of its steps make one call. The new `.claude/settings.json` denies the tools no session in this folder has ever called (about 7k off every start), and has Opus compact near 300k instead of 1M, which keeps the session's id and lane where a hand-over cannot. `CLAUDE.md`'s "Sessions" rule says what must survive a compaction goes in the notes, independent reads go in one step and a wide search goes to an `Explore` agent on Sonnet. By the notes' model the Opus tokens fall about 32% and a batch session's mean context a third; what stays outside the repository (Artifact and the browser pane loaded whole, personal chats in this folder, unused skills) is put to Jerus. Unrun: CX2-M measures.

### 0.9.47 — GT2, a timing red main also misses re-runs nothing — 2026-11-07, ready, see docs/queue/GT2-notes.md
No player sees this one: the gate is shorter. Every gate since 11-05 had a timing red that main itself misses (SaveSlotCheck's
25 ms, main's medians 26-57 ms), and `ship.sh` timed each in three paired rounds of main's classes and the merge's, 20-80
minutes a gate (PD1's 78), to conclude it was not the batch's. Each gate now keeps the merged main's figure of every miss in
`docs/notes/gate.txt` (`# figure` lines: the merge's median over the rounds, else its hard figure); the next gate the same day
passes a miss whose recorded figure misses the budget too, with the merge's no more than 15% over it, with no round, and times
the rest as before. Replayed on 11-07's nine gates, four would have run no round and four would have run them without
SaveSlotCheck. `docs/orchestrator.md`, The gate 6, says so; `tools/ship-selftest.sh` has four cases for it, and its QUEUE.md
case, red since that file gained a second table on 11-02, compares with main before the merge. Process lines 105 in, 96 out.

### 0.9.46 — UP7b, the speed reading shows a stall the clock's own redraw keeps making — 2026-11-07, ready, see docs/queue/UP7b-notes.md
UP7's gate review, on the clock. UP7 counted every stalled frame at its clamped share, so a redraw costing more than a quarter of a second
(due every three times its cost) ran the city at about 83% of the rung while the header's reading stayed white. The frame after the
clock's own redraw now counts the redraw's measured cost as well (at most half a second), so that stall reaches the window and the reading
turns amber once the city is under nine tenths of the rung; a stall nobody made (a dragged or minimised window) is still clamped, and a
redraw begun while the clock stood still claims nothing. The post-layout listener that takes a redraw's cost reads the pace field when it
fires (`() -> pace.redrawEnded()`), no longer the pace the clock started with. ClockCheck 7 causes the recurring stall at 1x and 50x and
reads the city at 82% of the rung; it fails with the cost left out. No model change, no saved field, no ensemble.

### 0.9.45 — SS2, SaveSlotCheck 7d's hold on the swap's own count, not a clock — 2026-11-07, ready, see docs/queue/SS2-notes.md
No player sees this one: a check went red under load and no longer can. LB3's section 7d (a save's swap over a file another
program has open) held the file on a reader thread that let go after 100 ms of the wall clock, against the swap's ten tries
fifty milliseconds apart; under load the thread and the tries slipped apart, and the check went red at RT1's gate on whether
the swap "was refused and landed on a later try". The swap now calls a test seam, `GameFiles.onSwapRefused`, after each refused
try, and 7d lets go of the file from inside it on the second, so the release is a step of the swap itself and no time is
compared with a dial. The assertions are the same, with one added: the swap was refused exactly twice first. SaveSlotCheck ran
three times beside two other harness JVMs and 7d held every time; the one red in each, 7c's median due month, is main's
recorded timing red. Nothing in the model, a save or a screen changed, so the trace is PD1's and the ensemble was not run.

### 0.9.44 — XI2, Trade's warning and rule text true of the bounded intervention, a peg and a band — 2026-11-07, ready, see docs/queue/XI2-notes.md
XI1's review follow-ups, on texts only (the model's steps 5 and 7 are untouched, the trace is byte-identical, no ensemble). Trade's
THE RULE and the Interventions row's (i) stated the unbounded linear rule; they now say up to 10% of a year's output dealt between
two month ends and less for each dollar past that, and that a peg or a band holds the rate while dealing moves the rate behind the
hold. The warning on the exchange read the deal alone: two pieces of 9% each crossed the bound unwarned, and the second's figure was
its move alone (×1.2) when the model gives it on top of the first (×1.1). It now reads the month's dealing as the sum step 5 reads
(a sale on top of a big purchase counts net) and says what is dealt already. Under a peg it said "moves nothing": the rate is held
but the float's own rate behind it moves and the vault deals to hold it (XR5); under a band it said "would move the rate ×N" where
the band stops it at its edge. New pure getters on `ForeignAccounts` (`dealtYears`, `yearsOfOutputWith`, `dealEffectOnTop`,
`dealFactor`, `rateAfterDeal`, `regimeHoldsDeal`); `TradeScreen.pastBound()` has the words. InterventionCheck 6 proves the model
(peg, band, the sum); KeptCheck 74b the texts, each scenario drawn kept.

### 0.9.43 — PD1, the drivers' petrol paid once — 2026-11-07, ready, see docs/queue/PD1-notes.md
The households paid for their petrol twice. Retail books every litre as a sale to them, so it was in the shopping
their books are handed, and the same month's bill, the fuel duty in it, was also their fuel line, taken out of the
cells' cash with the clinic's fees and the fares. The second payment left their savings and reached nobody, so the
money audit, which closes over the businesses and the city, never saw it. OilCheck 20 proves it in a town that drives:
on the old code its shopping was over the grocers' sales less the petrol by exactly the petrol, and the shopping and
the fuel line over what the grocers and the treasury took by the same. The petrol is now taken out of the shopping,
as R0c did for the homes' lamp oil and coal, on the live month and the load path alike. Every city whose people drive
keeps that money now, so the trace moves: the ensemble passes 24 of 26, one more than the baseline (A3).

### 0.9.42 — R0g, the causes of death: R0b's review follow-ups — 2026-11-07, ready, see docs/queue/R0g-notes.md
A harness fixture that holds the causes of death off before loading a city now keeps the 1918 influenza's sick held
too: the load built a new `Health` and dropped that half of the hold (`Game.buildWorld()`). CausesOfDeathCheck proves a
reload in December 1918 plays January to March 1919 as the unbroken city does, to the bit, and that a held twin loaded
at September 1918 has no influenza sick or dead to March 1920. CentralBankCheck's very strict twin at -4.28% on 1900's
deaths was one city's path: over all twelve founding shapes with the causes on, its last year stays within a full miss
of the target (-1.47% to 4.57%). Two findings queued: the influenza's sick also die through the long-sick ring, about
11% more than its own dead (R0h), and a city stocked complete in bulk reloads with its cash drifting (SR1). The game's
month is unchanged; the default trace is main's to the byte.

### 0.9.41 — FD1, Freight keeps its last station while it has a station's freight — 2026-11-07, ready, see docs/queue/FD1-notes.md
MF1's ★2 (Jerus: "Into MF1"). Counted again first: the churn had moved, not gone. The default playtest sold its only
station 4 times in 4,000 months (MF1: 16), bought again after 27 and 79 months at $339m and $990m against plots of $0
and $20m; the ensemble's 16 AUTO2 seeds sold it 16 times, all bought again, 2 in the same month. Every sale was the
distress rule's, and all but one had the freight plan() orders a first station for. `Freight.mayRetire()` now also
keeps the last station while selling it would leave the stations short by that rung (`wouldOrderAgain()`, plan()'s
own through `firstStation()`): after, no such sale in the playtest or any seed. RailDepotCheck 9. Ensemble 23 of 26,
as 0.9.26. LongPlaytest red on the moved path: `a building on the map moved`, the layout watch's class, which DM2
mends; FD1 touches no map code; passed by name (FD1-D).

### 0.9.40 — R0e, research: the household's basket by year — 2026-11-07, ready, see docs/queue/R0e-notes.md
A city founded the 1900 way (off until R1d) keeps the household's basket of its year: `consumption.json`'s new `years`, knots
at the census years and today's from 1971, read by `Consumption.yearAt()`. Bread: 51% bought in 1900, the rest baked at home
from grain at the same calories (the shops' basket, `CityBasket.perHead()`). Light and heat: R0c's `HomeFuel` shares are the
year's, 96% of the homes on lamp oil and half on a coal stove in 1900, none by 1961 and 1971, and a home burns the Department
of Labour's 1900 family budget's gallon of coal oil and eighth of a ton of coal a week (19.70 L and 0.491 t a month, were
25 L and 0.7 t est.). Cars: the ownership ceiling takes the year's registrations per household, none in 1900, .22 in 1921,
.62 in 1951. The homes' held fuel sale crosses a save and is reset with the world. Sources: Canada Year Book 1918, the 1941
and 1951 censuses, Historical Statistics of Canada and of the US (Crown copyright expired, Open Licence, public domain).
BasketYearCheck. A default city's trace is main's to the byte.

### 0.9.39 — FX9, ground bought inside an open cell gets the cell's own streets — 2026-11-07, ready, see docs/queue/FX9-notes.md
Jerus, 2026-10-23, on FX6's ★1: "Extend the streets". Ground the city buys inside a cell already open now takes that
cell's own ring and streets, laid from what its opening chose (`DistrictPlan.Builder.cellsOnGrown()`) and joined to
the main network; nothing standing moves, and the new ground fills from the next month. MapCheck 2c's full district
places all 3,356 of its houses (3,188 before), its bought plots in open cells built on 533 of 667 against 233; the
playtest's m4000 city packs 462 fewer at the rim. The map's alone: the trace byte-identical, no ensemble. LongPlaytest
at 2,400 months is red by DM2's class (m1209, 20 boxes: a way FW11 kept clear, cured by a departure): FX9 moves where
those fall (over 4,000 months 99 move pairs against main's 102); ★1, pass it by name (recommended).

### 0.9.38 — R0d, research: the 1901 wage ladder and the arrivals' skill mix by year — 2026-11-07, ready, see docs/queue/R0d-notes.md
A city founded the 1900 way (off until R1d) pays 1901's wages: `WageLadder` puts each pay rung on its nearest 1901 comparator,
men's earnings at regular work (labourers $256.29, electricians $518.12, draughtsmen $723.72, civil engineers $1,316.31, bank
managers $1,784.37; the 1901 census's Bulletin I and the Canada Year Book 1907; the elite rung est.), a census dollar a founding
dollar: the unskilled wage .24 of today's, an engineer 5.1 labourers against 2.4. The labour market, the allocator, the pension's
base and the grant, the founding rents and the depot's founding quote take the city's ladder, laid at founding and on load,
nothing saved. Its arrivals come from the year's skill mix (`ArrivalMix`: 1913's arrivals by occupation, 1953's, today's model
in 2021): in 1913 three in four hold no diploma and come at the going rate, graduates bought as today on the year's ceilings.
WageLadderCheck, ArrivalMixCheck; FittingCheck's bakery window 36 to 60 months, PrizeCheck's lab villages on today's ladder.
A default city's trace is main's to the byte.

### 0.9.37 — AN2, AN1's leftovers: the D$ the goods are struck at on Trade › The currency, and the Earth playtest — 2026-11-06, ready, see docs/queue/AN2-notes.md
Jerus, 2026-10-23: "Queue both, small". THE RATE card on Trade › The currency shows, under the D$, the D$ the
city's traded goods are struck at (AN1's lag) and how far it sits from the rate, closing 1/36 of the gap a month
(`Game.struckFromRate()`, a kept scope of its own; PassThroughCheck 7: the words hold every month, to 2e-16 in
logs). Nothing in the model reads it: the playtest's trace is byte-identical, no ensemble. The Earth playtest at
Rotterdam, 4,000 months: the anchor holds (prices +3.9% a year, the world's pace; the D$ 22% under parity, never
at its guard; 973,877 people), but the city holds at 10-13 thousand people for 97 years (m618-m1778) before it
grows (★1, a diagnosis recommended).
</content>
</invoke>

### 0.9.36 — R3b, research: Policy › Research's Overview and Funding — 2026-11-06, ready, see docs/queue/R3b-notes.md
Policy › Research opens on an Overview (each field's researchers, the labs', the idle, what is in reach and what it
touches, by school; the month's bill and prizes; found here and from abroad) and a Funding page: a card a field, its
grant and prize staged through Policy's tray, the schools' room and the least prize that brings a lab marked on the
dials, rows now and after down to the finds here over ten years, a chance or a count from 400 draws of the hidden sizes
and never the city's own (ResearchPreview, research-design §6.2). ResearchPreviewCheck holds §5.4's two cities to the
study's figures within their Monte Carlo error; a prize plays the game's own chase (★1). The hub has its sixth card,
each research school its posts line, Spending a Research line and the prizes in Subsidies, every city payroll the
research posts. From the reviews: posts struck after the demolitions, the month's counts saved, a find's labs' share
owed as arrears when the books did not close. A default city's trace is R5's to the byte.

### 0.9.35 — XR5, the currency regimes' rest: the hold pays the pressure, the tied path, the overshoot both ways, a thin vault's warning — 2026-11-06, ready, see docs/queue/XR5-notes.md
Jerus's calls on XR2's ★4 and ★5 and FX8's two. Under a peg or a band the vault no longer meets the month's balance: it
pays what holding the rate takes, the interventions' rule run backwards (XI1's bounded inverse), and what it dealt
fades in the rate at INTERVENTION_HALF_LIFE (holdFelt, saved in slot 147), so a weak D$ drains the vault even in a
surplus month and a strong one fills it in a deficit month; a sale it cannot afford breaks a peg, or lets a band give
way for the month. Under a peg the curve's expected dial is the tied dial's (the US's rate plus the spread), not the
inflation rule's. A broken peg lands past the shadow on whichever side it was (the franc, 2015). Choosing a peg or a
band with a vault that would not hold two years against a 10% gap, or before the city has traded a month, shows an
amber warning; the choice goes ahead. Lean, the default, moves by nothing: the playtest's trace is byte-identical.
CurrencyRegimeCheck 14-18.

### CX1 — a lighter start and a slower climb for Sonnet slices — 2026-11-07, ready, no tag (process), see docs/queue/CX1-notes.md
Sonnet slices were about half of the plan's usage: 167 in a week, 4.46B tokens, a median 95 steps re-sending 187k of context each, and the start alone 62k before a first read, 65% of it 38 tool definitions of which a slice calls six, then the skill list, `CLAUDE.md` and the deferred-tool names. The new `.claude/agents/slice.md` (Sonnet 5.5, xhigh, six tools, no `CLAUDE.md` but its own 47 lines of standing rules, 120 steps) should start a slice near 9-19k. `docs/deepseek.md` and `docs/batch.md` say to start every slice as `slice`, brief it with the files, methods and harness, size it to about 80 steps and hand the rest to a fresh one, and send a run's output to a file; each rule has its WHY from the notes' tables. By the notes' model the definition alone saves 19% of the tokens re-sent and a cap of 120 steps 37%; the bar of under 95k a step needs the climb a third lower as well. The DeepSeek test's figures moved to `docs/notes/batch-history.md`. Not yet run on a real slice: CX1-M measures.

### 0.9.34 — UP7, the speed reading as the reached speed alone; the drawer's bar and the construction tab kept — 2026-11-06, ready, see docs/queue/UP7-notes.md
Jerus's call on UP2's ★1 ("Reached speed alone, amber"). When the city falls more than a tenth short of the rung,
the reading between the speed's arrows shows the speed reached alone, in amber ("39×", not "50× · 39×"), at one
fixed width (34 px, the widest reading "0.25×" fits it), so nothing beside it moves; at pace it is the rung as
before, the same pixels. A stalled frame is counted at its clamped quarter second, so a half-second stall no longer
reads short for two seconds; a speed that prints as the rung's own words is no shortfall ("2×", not "2× · 2×"), and
one just under 1 reads "1×", not "1.0×". The drawer's pinned bar and the construction tab are drawn kept, as the
rest of the chrome: no node or tooltip made new each month. ClockCheck 6, KeptCheck 94 and 98. No model change.

### 0.9.33 — R5, research: the screens — 2026-11-06, ready, see docs/queue/R5-notes.md
What a city founded the 1900 way has found is on screen (research-design §5.5, mockups 3-5). Policy has a sixth area,
Research, opening on Found: every discovery on one timeline a row a field (hollow from abroad on history's date, filled
found here with a line to the world's date and how far ahead), what the city found itself, the recipes in use and the
equipment fitted with each verdict, and four charts over time (three new history series, kept only by a 1900 city). A
sector's Investors page has ITS EQUIPMENT: a card a piece, each kind of building's fitted count and the verdict on the
next one; the Fit pill reads the city's month and finds. A building known after the founding says on its card where it
came from and shows NEW (a chip, a violet edge, its category's dot, a strip) for a year or until the month after it is
seen; a Research Lab's card says what a lab is for (R4 ★10). City History flags every discovery. A default city gains
one Policy chip and a quiet page, its trace R4's to the byte. Overview and Funding are R3b's.

### 0.9.32 — UP6, The autosave: a skip's end, the window's close, the player's Save off the clock — 2026-11-06, ready, see docs/queue/UP6-notes.md
A multi-month skip ends with an autosave of its own last month, unfloored, asked for and not waited for (about 5 ms on
the clock), so a skip inside the 30 s floor no longer leaves the save from before it on disk; none after a skip that
stopped part-way. Closing the window autosaves like Quit ("Autosave - on close", the real city during a replay), on disk
before the game exits. The player's own Save is written on the save writer from a snapshot: the page says "Saving..."
until the write lands, then the result as before; a second Save waits for the first, the Save menu waits as Load's does,
and the writer writes a numbered slot only when the player saves there. A history that could not be written is told:
"Saved, but its history could not be written: <reason>" on the Save page, an inbox notice for the autosave (and for an
autosave that fails whole), the .bak kept. SaveSlotCheck 7e holds each; 7b and 7c.5 read the skip's end. No model
change: no ensemble.

### 0.9.31 — KP2b, The kept screens' review follow-ups — 2026-11-06, ready, see docs/queue/KP2b-notes.md
The stranger's reviews at KP2-KP7's gates, mended: a kept page redrawn with nothing new now writes nothing on its nodes,
where every page wrote 15 to 100 styles, texts, paddings and widths a month (a plain style then the full one, a `new
Insets` each draw, a binding or a cursor made again), so CSS and layout no longer run for nothing (KeptCheck 92, about
160 pages of Sectors, the statements, Bank, Finances, the fund, Trade, Policy and Zoning, Rankings and Household money).
A slider greyed under the player's hand lets go (91); HOW STRICT's line says the held step's words when a month lands;
Rankings' hover band follows the pointer after a wheel scroll (93); the yield box after Enter has its fixture (KP5 had
mended it). The last door cell of a vitals strip no longer takes a dividing rule after the pointer has passed. The
whole kept chrome's month, measured quiet ABBA against 0.9.17: about 3 ms both ways (KP3's gate's 9.34 was its load).
No ensemble: the model is untouched.

### 0.9.30 — R4, research: prizes and the Research Lab — 2026-11-06, ready, see docs/queue/R4-notes.md
A city founded the 1900 way can now pay for results as well as for work (research-design §5.3). Four prize dials, one a
field, pay D$ for a discovery made here, and only then. A prize draws Research Labs, four new Business Services buildings
of 100 research posts of one field (ids 148-151): investors build one while the purse covers the cheapest open problem's
remaining work at the post's cost and a team could finish it before the world, on the owners' money alone, never a loan.
Labs work their target after the city's own even spread; at a find the purse is split by the work each did, the labs'
share paid by the treasury into their owners' till (`TreasuryLine.PRIZES`, a transfer as a subsidy is), the schools' a
pot that buys posts beyond the grant. A field with half a lab idle for six months sheds one. Nothing moves in a city
founded the default way or with every prize at 0: the default playtest's trace is R3's to the byte. The prize cards are R3b's.

### 0.9.29 — KP7, Screens kept: the rest (Household money, Rankings), and the rebuild path retired from the clock — 2026-11-06, ready, see docs/queue/KP7-notes.md
People's Household money and the Rankings tab are drawn over last month's nodes instead of rebuilt (study UP4's KP7,
the last KP batch): Household money's grid keyed by household shape, ledger and tier, the open cell's books by the
cell; Rankings' controls, measures, rows, card and drawer by what they show, its table's canvases painted again only
when a row moves. With them every rail screen is kept, so the clock never empties a page, holds its height or settles
its scroll: that path is left to the menu's own screens, which the clock never redraws (`UserInterface.rebuiltInPlace`
counts it; KeptCheck 88 holds 17 screens to none over three months each). KeptCheck 82-90 compare both pages with
pages built new through every view, cell, board, measure and a read of the saved cities landing; a month on Household
money fell from 16-18 ms to about 3, on Rankings from 9-11 to about 3. CLAUDE.md: a screen section draws through Kept.
No ensemble: model untouched.

### 0.9.28 — R3, research: posts and discoveries — 2026-11-06, ready, see docs/queue/R3-notes.md
A city founded the 1900 way now finds discoveries of its own (research-design §5.1-5.2). Four grant dials, one a
field, buy research posts at the post's cost (D$ 18.2k a month in engineering at today's wages, §1.8) within the
schools' room, 2.7 posts per university-band staff member; the posts are jobs in the labour market, and the treasury
pays their wages and the labs' costs as a line of its own (`TreasuryLine.RESEARCH`). The schools' staff research 40%
of their time for nothing. Each field's researchers spread evenly over its open problems; each problem's work piles up
until it reaches a size drawn from the city's seed (`Research`), and the inbox says so, here or from abroad on
history's date. A find is known in the city from that month (its recipes, equipment and buildings), and a law find
opens its Policy lever up to four years early. Nothing moves in a city founded the default way or with every grant at
0: the default playtest's trace is main's to the byte. Policy › Research, the screen, is R3b.

### 0.9.27 — KP6, Screens kept: Government, Trade, Policy and zoning — 2026-11-06, ready, see docs/queue/KP6-notes.md
The Government tab, Trade & the World with its partners page, and Policy with zoning's pages are drawn over last
month's nodes instead of rebuilt (study UP4's KP6): Government's Overview, books, lists with who pays each line, debt,
Revenue, Spending and Output; Trade's six pages, the river, the exchange, the partners' table, a nation's page and a
good's board; Policy's hub and every area's page, zoning's ground, each zone and the forest law - each page keyed by
what is open on it, each row that comes and goes by what it shows. Policy's dials keep the player's hand across a
month: each dial card is keyed by its lever, so a card that comes in front of a held dial takes nothing from it, and
the effects read the held thumb. Trade's count-up and bars grow only at 1x-10x, as on every kept screen. KeptCheck
69-81 compare each kept page with one built new through every page, fold, line, nation, regime and staged change; a
month on Government's Overview fell from 12-21 ms to 3-5, Policy's rate page from 16-27 to 4-7. No ensemble: model
untouched.

### 0.9.26 — XI1, the intervention bounded — 2026-11-06, ready, see docs/queue/XI1-notes.md
A purchase or sale of dollars now moves the D$ a bounded amount: 2% for each 1% of a year's output up to a tenth of a
year's output (`ForeignAccounts.INTERVENTION_LINEAR`), and logarithmically past it, so each further dollar moves the
rate less (`interventionMove()`; GD0 §4.5, Jerus 2026-11-02). Jerus's Rotterdam bought 4.5 years of its output in a
month and its D$ went ×11,600. The same purchase now moves it ×2.6, where the old rule gave ×8,100 (`InterventionCheck`,
on a bare currency and on a played town). Under the bound every figure is the old one to the bit. Past it, the central
bank's defence still takes back its share of a fall, and sells more dollars to do it. Trade's buy-or-sell card warns past
the bound: "This purchase is 4.5 years of the city's output". The default playtest is the same to the byte until month
2,119, where its war chest bought 11% of a year's output. The ensemble scores 23 of 26, as 0.9.23 did.

### 0.9.25 — KP5, Screens kept: Bank, Finances and the fund — 2026-11-05, ready, see docs/queue/KP5-notes.md
The Bank tab, the Finances tab and the city's fund are drawn over last month's nodes instead of rebuilt (study UP4's
KP5): the Bank's Overview and six pages in both views; Finances' hub and every area and page - the position, the book
with a line's page and Close it now, Borrow at home and abroad with the ask and the auction, Money, the bond market and
a bond's book, the foreign default and debt result pages; the fund's Portfolio, Search, Activity, Rules & cash and a
security with its ticket - each page keyed by what is open on it, each row that comes and goes by what it shows. The
curves, the desk's pictures, the buyback ladder, the auction and the column charts are told their figures and drawn
again only where one moved; the boxes keep the player's typing, caret and focus across a month; Bank's pops and gauge
and the fund's count run only at 1x-10x, as on People, History and Sectors. KeptCheck 55-68 compare each kept page with
one built new through every page, view, choice, fold and box; a month on the Bank's Overview fell from 16-19 ms to 5-8,
Borrow's from 17-24 to 8-10, the fund's Search from 106-173 to 58-71 (its long lists: FP1). No ensemble: model untouched.

### 0.9.24 — R2, Research: the catalogue and the world's dates — 2026-11-05, ready, see docs/queue/R2-notes.md
Research has its catalogue: `research.json`'s 89 discoveries (the first study's 40, the 1900 study's 48 and L5, the
Canada Pension Plan), each with its field, kind, history's month, what it needs first, whether it carries a licence fee
and the work it takes by the study's rule; the law rows carry the Policy levers' own dates (`ResearchCatalog`,
`ResearchCheck`). Every catalogue code in `buildings.json` now resolves to history's month (`knownAt()`): in a city
founded the 1900 way the tractor is known from June 1917 and the oxygen converter's recipe from November 1952, where a
code was never known, and the 29 buildings that did not exist in 1900 are dated by the discovery that opens each (the
Container Terminal by E5, the cracking units by PE11); the Oxygen Steel Shop stands from 1900. A city founded the
default way reads none of it: the playtest's trace is unchanged. The rest of the brief is queued: buildings ordered only
on their dates (R2b), licence fees (R2c), each entry's old way (R2d, R2e), medicine (R2f), the near-future list (R6).

### 0.9.23 — R1c, Research: recipes as data — 2026-11-05, ready, see docs/queue/R1c-notes.md
A building can know several ways to run (`buildings.json`'s `recipes`; `Recipe`): the top level is today's, the
default, and a recipe states only what it changes. In a city founded the 1900 way (off until R1d) each month the
fitted and the unfitted buildings of every stack run the best recipe each can at the city's prices (`Recipes`,
`SpreadPlanner.cityValue()`): the fitted the recipes that need the template's piece, the rest those needing none;
the template's figures are their mix, saved by template id, and a recipe piece pays on the two recipes' difference.
Research-1900 §3's rows with no form get their piece and their 1900 way: beehive coke, pick-and-powder coal,
hand-loaded pits, hand-drilled quarries and copper, shaft-kiln cement, groundwood and sulphite pulp (kraft from
1907), rope-and-tub unloading, Bessemer and open-hearth steel. A city founded the default way chooses nothing,
but its farms now buy their tractors' diesel and petrol (R0c ★5), 4% of their output: the trace moves, and the
ensemble scores 23/26 against 24/26, A3 short in two seeds' tails (docs/ensemble/R1c.md). RecipeCheck holds it.

### 0.9.22 — R1b, Research: the fit acted on — 2026-11-05, ready, see docs/queue/R1b-notes.md
Equipment is now bought and fitted (research-design 2.2 steps 5-6), in a city founded the 1900 way
(`Founding.withUnfitted()`, off by default until R1d, saved). Each month a sector's next building is weighed
against its best fitting that pays in `SpreadPlanner.best()` (`EquipmentFit.choose()`), one fitting on site a
sector; the goods are drawn at the city's market by the builders and the work priced a point, paid by the owner
and earned by the builders on the investment line; a job on site (`Fittings`, saved) is worked by their crews
after the repairs and before the sites, and the building counts fitted when its points are done. A depot's motor
trucks join the fleet. New buildings come without each piece that does not pay at their finish. The player's Fit
(the city pays) is a pill on a business's kept Investors page. R1's review: the 1900 forms said again after the sites
advance, so a reloaded twin keeps step, and cleared on New Game and a load. FittingCheck holds all of it; the
default playtest is main's to the byte.

### 0.9.21 — KP4, Screens kept: Sectors, the list and a business's five pages — 2026-11-05, ready, see docs/queue/KP4-notes.md
The Sectors tab is drawn over last month's nodes instead of rebuilt (study UP4's KP4): the list and every card, and a
business's frame and five pages in both views - Operations of every kind (the plant, the tonnes, the refinery, the
materials industries, the oil industry and its reserve, Freight by division, the shelf, what it charges), Income and
its waterfall, the balance sheet, cash and credit, the statements, the investors - each page keyed by the business,
the page and the view, each row that comes and goes by what it shows. The refinery's and the materials' pictures and
Freight's legs are told their figures, not rebuilt; the reserve's dials keep the player's hand across a month. The
count-up counts only at 1x-10x, as on People and History: faster, the month's figures are set at once, and a count
still running is stopped. KeptCheck 47-54 compare each kept page with one built new through every sector, page,
view, division, fold and column; the list's month fell from 22-28 ms to 7-9, a business's from 11-31 ms to 3-7
(KEPT_SECTORS_MS 18, KEPT_SECTOR_MS 15). No ensemble: the model is untouched.

### 0.9.20 — DT1, The same code, another city: what in a month reads the machine — 2026-11-05, ready, see docs/queue/DT1-notes.md
Nothing in a month reads the machine. BA1's "the same code gave another city twice under load" was two probe runs
started without `-Dcbs.world` in a worktree with no `data/`. With no world file the city has no world market and
trades on the literals: another city, and nothing said so. Rerun on BA1's commit with and without the file,
BA1's numbers come back to the digit (month 125 against 61, 1,067 people against 909). The month moved under no
JVM setting tried, for FloatLinkCheck's town and the ensemble's city at 600 months, bit for bit: every identity
hash one, `-Xint` and C1 alone, one or 32 processors, a common pool of one, soft references cleared at every
collection, the serial collector, and ten autosaves against one. New: DeterminismCheck plays a town and an Earth
city in two child JVMs set up as opposite machines and compares them to the bit. Its control, a child with no
world file, is another city from month 2. `WorldData.locate()` now says once a run, on stderr, when no world
file is found. Ensemble not run: the month is unchanged.

### 0.9.19 — RT1, A building counted in its sector's retirement measure — 2026-11-05, ready, see docs/queue/RT1-notes.md
Agriculture's and Food Processing's spare-capacity rule weighed what was spare in money (each good at its export
floor) against a building counted in tonnes or kilograms, so at the founding's price level no farm or food plant was
ever sold by it. Now a building is counted in the same money (`Agriculture.unitsOf()`, `FoodProcessing.unitsOf()`),
the rule as farms first had it: a founded town sells one of its two Mixed Farms in its first years when it loses
money with output ahead of demand. And a bond holder left within a rounding of the face moved holds none
(`Debt.moved()`): a recall struck an ulp short left the businesses 4.4e-16 of a city bond, read as paper worth 0.93
of face at one price level and 1 at the other. PriceLevelCheck 8 is green (the played horizon 103, a tie at 104:
BS1). AgricultureCheck 5, FoodProcessingCheck 7, HoldersCheck 14; FoundingYardCheck 4, MonetaryCheck and ScaleCheck
re-caused; CentralBankCheck 21 and MapCheck 2d passed by name. Ensemble: 24 of 26 criteria (0.9.16: 22).

### 0.9.18 — KP3, Screens kept: Build and the construction page — 2026-11-05, ready, see docs/queue/KP3-notes.md
Build and the construction page are drawn over last month's nodes instead of rebuilt (study UP4's KP3): Build's
Overview, every category and market page, the stopped pages (no land, deposit, coast, river, crossing or licence),
the funding and fell-short pages, each building's card and each part of it that comes and goes, a scope keyed by
what it shows; the construction page's Sites, Timeline and Demolish tabs the same, the timeline a picture told its
figures. The order bar is filled once a draw, not once a card. A kept control keeps the player's hand: automatic
building's dial held across a month, the cap chips' yield box and the construction page's find box keep their
focus, text and caret; a tile, door or card's (i) under a still pointer keeps its hover look. A move between Build's
pages is an arrival, as before. KeptCheck 36-46 compare each kept page with one built new through every page, ring,
order, pin and tab; Build's months fell from 15-30 ms to 4-6 ms, the construction page's from 8-17 ms to under 2
(KEPT_BUILD_MS 12, KEPT_CONSTRUCTION_MS 4). No ensemble: the model is untouched.

### 0.9.17 — KP2, Screens kept: Land, Services and Infrastructure; the controls' rule; KP1's hover kept — 2026-11-05, ready, see docs/queue/KP2-notes.md
Infrastructure, Services and the land office (with its funding and fell-short pages) are drawn over last month's
nodes instead of rebuilt (study UP4's KP2): every page, sub-page, card, offer and row a scope keyed by what it
shows; FlowCurve, ScaleRows, CauseBar, EffectScale and CohortBars pictures told their figures. A kept control keeps
the player's hand: the transit fare's slider is never set while held, so a month landing mid-drag leaves the thumb,
its reading and "what it would do" where the hand is; a field with the focus keeps it, its text and caret. A row,
door or card under a still pointer keeps its hover look across a month (KP1's gate review). A closed fold on
Services builds nothing. KeptCheck 21-35 compare each kept page with one built new through every page, division and
fold, hold the slider and the hover across months; the three pages' months fell from 9-20 ms to 2-7 ms
(KEPT_INFRASTRUCTURE_MS 9, KEPT_SERVICES_MS 13, KEPT_LAND_MS 5). No ensemble: nothing outside ui/ changed.

### 0.9.16 — BA1, every business may build ahead — 2026-11-05, ready, see docs/queue/BA1-notes.md
No business is held to one order on site, nor the landlords to twelve months of the builders' work (Jerus: "why
bounded? why not let contracts flow?"). A sector orders whenever an order clears its test at the price of a point it
would pay, each building priced on the queue the ones before it leave, the cost grown by the interest over the
order's whole wait and, while contracts are dear, once more by its own multiple ("stop trying to place orders until
contracts arent so expensive": a business can wait). The builders keep one depot on site, the refiners one crude unit.
The ensemble scores 22/26 against 0.9.12's 24/26. A2's spell is 14 months against 27, 40,000 people come at m878
against m1,038, and the final city is 572,286 against 323,765. A3 misses by a month in one seed, R3 in four. Known-class
lines pass by name at the gate: LongPlaytest's map finding (Jerus: "Ship it, pass both"), MapCheck's moved boxes (DM2),
PriceLevelCheck 8's drift (RT1, BS1) and DenominationCheck's twin, a price tie in the bond book (OB1).

### 0.9.15 — R1, equipment as data and on each building — 2026-11-05, ready, see docs/queue/R1-notes.md
The first part of research's recipes and equipment, built off. `buildings.json` gives a building the equipment it can be fitted with:
the electric motor drive on the 18 factories (it ends R0c's steam), the motor truck on the Truck Depot (the carts' horses) and the
tractor on three farms (the farms' horses; its date is the research catalogue's, so it is not known yet), each with what fitting it
buys, its work and its life. Each stack of buildings counts the ones still without each piece, and the count is saved; R0c's shares
are set from the counts each month, weighted by the buildings' power, land or truck room. The payback rule (a piece fitted only when
what it saves over its life repays its cost and clears the interest hurdle, as a new building's) gives each piece a verdict at the
city's prices, read on demand. Every building in a played city is fitted, so the default city's trace is unchanged. EquipmentCheck
holds it. Next: the fit bought and ranked with new buildings (R1b), recipes (R1c), and the 1900 city switched on with R0d's wages (R1d).

### 0.9.14 — R0c, the 1900 mechanics as forms — 2026-11-05, ready, see docs/queue/R0c-notes.md
The 1900 mechanics of research-1900 §4 are built as forms, each a share of a sector or of the homes that runs the 1900 way, and
all are off: 0 in every played city, so the default city's trace is unchanged, until R1 sets them from each building's recipe
(and R0e the homes'). A farm on horses feeds them 22% of its own crop and needs 1/(1-0.273) of the hands; Freight's Haulage on
horse and wagon carries its city leg by hired teams (10 t a team-month, two horses eating 0.29 t of crops each, one carter),
with no diesel and no trucks; a factory on steam draws nothing from the grid and burns 1.8 kg of coal a kWh instead; a lamp-lit
home buys 25 L of lamp oil a month and a stove-heated one 0.7 t of coal, from the grocers, a bill of its own. Forms1900Check
holds each form, with the money's audit closed every month. Found on the way: the drivers' petrol may be counted twice in the
households' spending (PD1, queued).

### 0.9.13 — R0b, causes of death in Health — 2026-11-05, ready, see docs/queue/R0b-notes.md
A city's dead now have causes (`CausesOfDeath`): typhoid, babies' summer diarrhoea, the childhood outbreaks, TB, mothers at
home births, pneumonia and the 1918 influenza, each at its 1900 rate and falling on Canada's own dates - typhoid with
chlorination by 1915, TB to 1948 and with streptomycin, mothers' deaths with sulfa and penicillin - to nothing by 1980; a city
short of water, food or general care dies of them sooner than history did. For teens, adults and the old they add to today's
rates; for babies and children, whose unserved rate childcare's swing already holds at 1900's, they name their share of that
excess and history takes it away (an unserved baby dies at 21 times today's rate by 1980, where it died at 40). A 1900 city
loses about 17 per 1,000 a year, as Canada did. The influenza kills 0.6% of the people from October 1918 and puts a quarter
off work in its waves, with an inbox notice. Services, Health lists what people die of beside 1900's rates; People's dead gain
the part. Twelve fixtures sized on today's deaths hold the causes off; the ensemble scores 24/26, the same two as 0.9.12's,
both nearer. MapCheck 2h (DM2's) and ShortSellingCheck 7 (main's orphan-share fault, OH1 queued) pass by name at the gate.

### 0.9.12 — R0, the Policy levers on history's dates — 2026-11-04, ready, see docs/queue/R0-notes.md
A city founded in January 1900 now has each Policy lever on the date Canada first had it (`PolicyDates`): the profit tax
1916, the income tax 1917, the minimum wage's floor 1918, the sales tax 1920, zoning 1924, old-age pensions 1927, student aid
1939, EI in August 1940, the health premium 1947, the pension's contribution 1966; the property tax, fees, fares, food
assistance and the rest are open from founding. Until its date a lever is shut and refuses a setting, and the Policy screen
draws it shut with when it opens; on its month the inbox gives Canada's first terms and the lever opens - the three income
taxes at the founding rate (15%), the promises at zero, the wage floor at the going unskilled wage. Each city's openings are
saved (R3's law finds will move them); a save from before R0 loads with every lever open. Harness fixtures whose premise is a
lever, or whose condition the untaxed 1900 city does not give, open them; the playtest runs on history's dates.
The ensemble scores 24/26 against 0.9.6's 22/26. Two lines its 1900 city is the first to reach in main's code pass by
name at the gate (Jerus: "if checks were not caused by its own code, then it should always pass"): SlopeCheck's "a
building on the map moved" (DM2's) and PriceLevelCheck 8's farm never retiring at 1900 prices (RT1, queued).

### 0.9.11 — CM1, the replay's camera loads its whole move first, then runs on a click — 2026-11-03, ready, see docs/queue/CM1-notes.md
In replay mode a glide (3), a fly out (O) and a fly in (I) now load first: the map says "Loading the move... N s" while
every view the move will show is painted, with no time cap and the map's pictures held, then "Ready - click to run", and a
click or a key runs it (Escape cancels), so a screen recording can start first. Measured on an Andorra recording
(`ui/ReplayMoveProbe`), black frames went from up to all of a move to none: a glide across 119 months, black in all 295
frames and 5,256 asks of the map's worker before, ran with no frame unpainted and nothing asked; loads took 5-15 s. Left
for CM2: the globe near the city still draws part of the planet from its coarse 7.4 km picture while it moves (blurry, not
black), and the half resolution on the move. ReplayCheck 5d runs a move in the real window, off screen, and asserts every
frame shows a view the load painted (a run between them asked the worker on a busy PC). The normal game is untouched.

### 0.9.10 — WN5b, WN5's review follow-ups: the map's hover priced off the screen's thread, and three wordings on the land and the rate — 2026-11-03, ready, see docs/queue/WN5b-notes.md
The expanded map's hover over a block that may be picked prices it on its own thread from a copy of the land office
(LandMarket.quoter(), under a millisecond to make), and its card says "Pricing the block…" until the price is in: since
WN5 a quote near a foreign coast splits the block by seller, measured at 45 ms to 5.3 s by level (LandNationsCheck 12).
A purchase's receipt names the island in a sentence of its own ("It lies on Island 2, which no port links."), and its
sellers' shares round by largest remainder as the card's do (three thirds read 34, 33 and 33 on both). Trade's real
effective rate names the partners it weighs ("Weighs Germany 50% · Japan 25% · United States 25%"), from the one getter
the rate is made of (RealDollarCheck 5; WN6's fallback for a money that never changed is taken by none of 196 nations on
today's file). WorldMarket.refill() marks its fills fresh only after the curves are built. A click that picks a block,
and the month's re-measure of the picks, still split on the screen's thread: WN5c, queued, measures them first.

### 0.9.9 — CB1, the central bank's strictness measured over twelve cities — 2026-11-03, ready, see docs/queue/CB1-notes.md
CentralBankCheck 21's "very strict holds prices lower than Standard" was one probe city's path, re-picked six times as
batches moved it; it is now read over twelve founding shapes (`STRICT_SHAPES`: 40 to 43 houses, 3 to 5 months, the
playtest's seeds 0 to 11, the probe city's among them), each played to month 600 and twinned 240 months at both
settings: very strict under Standard at the median and in `STRICT_SHARE` (three in four) of them, with a fixture that
reverses cities by hand (green with one, red from four). On 0.9.8 ten of twelve hold, the median 0.42 points a year
under; two shapes run the other way, moved overnight by LD2 to DM1 while the probe city held. The very loose twin, the
fourth game, the stop and trust keep the probe city. CentralBankCheck takes about 153 s, 70 to 80 s more.

### 0.9.8 — DM1, boxes moving after a reserve: the wall events found, the fix held for a journal — 2026-11-03, ready, see docs/queue/DM1-notes.md
No change a player sees: MapCheck's comment and DM2's brief. DM1 traced FC1's moved boxes (MapCheck 2h, 12 and 23 four
months on) to FW11's way kept clear for a street piece the buildings wall in, which is worked out again from each
month's arrivals, so it drifts (6, 5, 20, 22 plots) and first-fit cascades each change into moved boxes. A planner fix
that dated the way from the replay alone stopped the drift, but moved 40 boxes of a real city in SlopeCheck 9 (m608 to
m609, after a land purchase re-formed the street pieces), so it is not shipped (it stays in the branch history, commit
77e40c87). MapCheck keeps FC1 off through section 2h, its comment now naming the wall events (2d four months on and its
yard that goes, 2h's reserve month) and the drift. DM2, queued, dates the ways in a journal in the map's sidecar.

### 0.9.7 — FC1b, the foreign contractor's book and overtime after FC1 — 2026-11-03, ready, see docs/queue/FC1b-notes.md
In the builders' trap a cancelled order now comes off the foreign contractor's book by its points left (a depot of
the builders' served first, never on his book, excepted). Before, he kept building a cancelled order's points at his
price, and a cancel and restart put them on his book twice. A rushed site's overtime is now worked and paid on the
city's own crews' part of its share alone. Before, the city paid its households overtime for hours his crews worked,
and the builders earned points nobody worked. With no contractor the month is the same to the bit. ConstructionTrapCheck
9-11 cause each case: the cancel and restart, the rush beside him, and a city saved mid-trap whose next month matches
its twin's. The playtest's trace is byte-identical (no ensemble). Both fire only in a city in the trap.

### 0.9.6 — BD1, the bank's discount re-struck where an issue or a trade releases it — 2026-11-03, ready, see docs/queue/BD1-notes.md
The bank's equity is its parts again after it buys the city's paper. Its unearned discount on that paper is re-read
from the debt book at every refresh, and twice the book's figure moved with nothing to book it: a line reopened at a
premium let the proceeds over face eat the line's discount, the bank's settled share with it; and the desk handed the
bank the whole line's discount while the book counts the settled part's, which differ whenever a sale is unsettled.
The next refresh moved its equity with no cause (SectorStatementCheck on FC1's path: 7.06 in month 135, the parts off
in 226 of 479 bank-months). The bank now re-strikes it where it happens (Bank.restrikeCarriedDiscount()), a named
cause routed to retained earnings as BK1's is; not at the buyback or the call, where the settle's payment already
cancels a piece bought back before its settle (the gate's finding). HoldersCheck 13 proves the three cases in a city
and its twin; SectorStatementCheck plays FC1's builders' price again, 0 of 479 off. The trace moved in one row of the
bank's; the ensemble passes 22/26, as 0.9.2.

### 0.9.5 — LD2, the wanted posts' bounds after LD1 — 2026-11-03, ready, see docs/queue/LD2-notes.md
A business's bid for workers now lifts only its own band, and never past sqrt(2). LD1's refused posts were priced
against the workers they would have taken from the band below, which bid the diploma band up by 7% on average (48% at
most) in most months it asked nothing, and took bands that asked past LD1's own bound, to 1.9x. They are now priced
against the workers the posts that exist give, capped on the month's own posts, and a band no migrant can come for asks
nothing. The comments and WantedPostsCheck 9 now say the asks are priced the month they are made. A replay's history
cut no longer keeps a company's series from before it listed. Ensemble 24/26 against 22. Eight harnesses whose young
towns the new wage moves play without it until LD3. The hands-off bank's failures are not LD1's (5 of 12 seeds without
its bid, 3 with); the sawmills cause them, queued as BF1.

### 0.9.4 — DZ1, the trade by partner in the money of the day — 2026-11-02, ready, see docs/queue/DZ1-notes.md
A currency reform now carries the city's trade by partner. Its history rounds each finished year to a grid in money,
and the grid followed the price factor but not the reform's unit: after a reform by a hundred it was a hundred times
coarser, the reformed city's cells rounded apart, and the January terms of trade, which weigh the world's prices by
them, moved the D$ (DenominationCheck's twins parted in month 169 on the nations' file and were 5e-7 apart by month
221, under the old millionth; a worktree has no such file, so FC1 saw it only at the gate). The grid now reads the
Denomination's unit (exactly 1.0 until a reform, so no other city moves), and a reform divides what it never touched:
the month's and the year's trade by nation, every (good, nation) cell, the finished years and the save's copy of them.
DenominationCheck plays FC1's builders' price again and holds the year and the decade to 1e-9 (the twins now to
3e-15); its section 7 proves the record on bare objects and in two cities on the twelve-nation fixture reformed in July.

### 0.9.3 — GT1, faster gates: the playtest at 2,400 months, the full 4,000 nightly, main's two timing reds halved — 2026-11-02, ready, see docs/queue/GT1-notes.md
The gate plays the long playtest for 2,400 months (81 s against 149 at 4,000; the same city through m2400). The full
4,000 run once a night on main's tip: `tools/nightly.sh`, which the hourly check-in starts when `day.sh --due` says
`nightly due` (01:00-07:00), its verdict in `day.sh` and the next gate's `gate.txt`. Main's two timing reds halved
where a quiet run shows it: the month that autosaves 17.2 to 8.2 ms (the history copied on the writer's thread), the
map's month at 10 billion 5.8-8 to 2.3-3 ms (only the districts a month changed are read). ConservationCheck's food,
electricity and water laws ride on every month of the playtest, its own 156-month city gone. No two harnesses grow
the same city, so none is shared. README states no harness count; `AllChecks` writes each harness's time to
`target/checks/times.tsv`. Found: the PC's commit charge (96%) runs the gate's suite one harness at a time.

### GD0 — gold and the city's money, the study — 2026-11-02, ready, no tag (study), see docs/studies/spec-gold-money.md
Jerus asked for gold ("would massively help fx control") and the city's money chosen at founding, defaulting to the nation's,
sign and starting level with it. Measured on the regime harness's town pegged at founding: the vault runs short in month 49;
40x deeper, doubt breaks it in month 56, its vault 712 months of imports at month 48, as doubt never reads it; its deficit
(-US$753k over months 25-48) is deaf to the rate in all nine runs, so a dial defending the vault only buys capital (to month
71, at 9-12% and 13% fewer people). His Rotterdam (slot 3): a 35-year strong-side peg, and one purchase of 4.5 years' output
moving the D$ x11,600 in a month, the intervention unbounded. Gold's price is the statutes' and USGS's (the Pink Sheet's gold
is the LBMA's fix: only its 4.749% a month ships, as a dial). His four calls, all as recommended (the rate first; all four
gold batches, mines last; the city's own money styled after the nation's; the level in units per US$), queue XI1, GD1-GD4
and FM1-FM3. No model change.

### 0.9.2 — FC1, builders short: the price of a point rises, and foreign crews come when none are free — 2026-11-02, ready, see docs/queue/FC1-notes.md
The price of a point of building work now reads the builders' queue. Past nine months of work at the pace the repairs
leave the sites, every order's work carries a premium that rises smoothly to +80% with no points left for sites, each
order priced on the queue with the orders before it; the planners see it, and a depot earns it. Once the sites get
under 5% of the builders' month (Jerus's city, every crew repairing), a foreign contractor takes every new order at
twice the price, builds the builders' own depots first, is paid by them as an import of services, and stops taking
orders once the sites have points again. The build card, the Construction page's PRICE OF A POINT and NEEDS YOU's
BUILDERS row say so. ConstructionTrapCheck (new) causes the trap: the depot is built in a month, the city out the next.
Ensemble on LC3's world file 22/26 as main, three of its four reds nearer their line and S1 further; no seed met the
trap before or after. Suite green. Fifteen harnesses whose new towns the premium moved were mended; five play without
FC1 until DZ1, BD1 and DM1 (queued) mend what FC1's path reached in them.

### 0.9.1 — LC3, the nations' world without the IMF's figures — 2026-11-02, ready, see docs/queue/LC3-notes.md
world-1.bin is baked again with no figure a sold game may not ship, so the nations can reach players (0.9.0 went out
without them, IR1). Each nation's price path is now the World Bank's GDP deflator, and the US's is the BLS's CPI-U, the
dollar the game's own price level is. ILOSTAT's consumer prices were the first choice, but every one of them carries
"Repository: International Monetary Fund". A money's rate in a year the BIS has no month of it is PWT 11.0's, then the
Bank's GDP in local money over GDP in US$, which gives Somalia, Syria and Sudan their published rates. The goods' prices
layer divides by the CPI-U and moves under 0.2%. The new file has its fixture, sha, credits and licence rows; Build
EXE.bat fetches it and puts it beside the exe. The playtest's trace moves, so the ensemble ran: 22 of 26 against 23.
A latent map fault the new trajectory met is mended: houses on a resource field walled a street piece in (MapCheck 8;
MF2's EM1-M), and PlanCheck 18 is its fixture.

### 0.9.0 — WN6, nations: the screens — 2026-11-01, ready, see docs/queue/WN6-notes.md
Trade › Partners is new: who the city trades with, by nation, the month or the year (its money and D$ a unit, its
central bank's rate, tonnes, distance, land), and for a chosen good where the next unit goes - what it nets against how
much the city sells, the month's line and the world's, where each next buyer joins, the buyers taking now all netting
the same, the band's floor and ceiling named by the nation that sets each, and what moves it. A nation has a page of its
own (figures and ranks, our trade with it by good and year, its money and land, the city beside it in real 2015 US$),
opened from the table, the Overview's new partners strip or the map card. The reserves open on the vault in eight
moneys. D29: the rate card gives a real 2015 US$ in D$ and the real effective rate, now carried across a partner's
change of money (WN5-R); the GDP tooltip, the city's output in real 2015 US$. Nothing in the month reads any of it: the
playtest's eight trace files byte-identical to main's, no ensemble. Suite green (exit 0); PartnersCheck new.

### 0.8.97 — WN5, land bought from its nation — 2026-11-01, ready, see docs/queue/WN5-notes.md
Jerus: "Borders baked in; land bought from its nation", "land costs different prices purely by who you are buying it from".
On the real Earth each offer is sold by the nation its ground lies in (world-1.bin's owner layer; an area or no one sells as
the host; a foreign nation's sea only within 12 nautical miles of its land), priced at today's formula times that nation's
land factor, its land against Canada's (exactly 1): 25 published farmland prices (Eurostat, USDA, Statistics Canada, 2024)
and a fit on output a head and people a km2 for the rest (land-2024.json), taken back to 1900 on the file's output a head
and moving with it. An offer across a border is split, each part priced and paid to its own nation, from the vault's
holding of its money first; the receipt and the offer card name them ("Sold by United States 62% · Canada 38%"), and what
the city bought from whom is kept for WN6. A generated world is the host's at factor 1: the default playtest's eight trace
files byte-identical to main's, no ensemble. A Rotterdam city pays about 11 times Halifax's and ends a third smaller.
Suite green (exit 0, 167 harnesses; README's count set to 167 at the gate, beside WN7's); LandNationsCheck new: Historical meets the published ratios in 2024 to 2.7e-16.

### 0.8.96 — WN7, ships and trains to their partners — 2026-11-01, ready, see docs/queue/WN7-notes.md
The nations line's last batch: with it WN0-WN7 are whole (the world file, the nations and their paths, the map, the
market, the moneys, land from its nation, the screens, and this). Each boat and train now carries its partner, drawn
from the month's tonnes of its cargo to each nation (PartnerTrade keeps them by kind; CallPartners draws), and a boat
leaves its offing on the open exit nearest its partner's bearing: SeaRoutes gives every offing an exit per 5-degree
bearing, the open sea on it. Trains go only to the host and nations within 2,500 km by land, on the one world line.
Each lane forks at its offing toward the exits a twentieth of its kind's tonnes take (at most 8 a kind; 1-7 from the
13 ports measured), and a boat or a train under the pointer names itself and its partner ("Capesize · from Brazil").
Nothing in the model reads any of it: the playtest's eight trace files are main's to the byte, so no ensemble.
PartnerCallsCheck (new, 9 sections); a frame's boats at 10B 0.249 ms with partners, 0.227 without (budget 0.5).

### 0.8.95 — EP1, the game starts in 1900 — 2026-11-01, ready, see docs/queue/EP1-notes.md
Jerus, 2026-10-30: "1900 start date with todays tech", at "1900 price level". Month 1 is January 1900, and today's real
economy is priced at 1900's level: every nominal anchor is today's figure times the US CPI's 1900 over its 2025
(PriceLevel.FACTOR, 1/38.6) - the six wage rungs (the unskilled D$3,460 a month is D$89.56), the 68 goods, the building
costs, the opening treasury and vault, about forty dollar dials - and no ratio, rate or quantity moves: PriceLevelCheck's
twin runs four cities at the factor and at today's prices, equal within 1e-9, and seven anchors it caught are mended. The
world file still starts in 2000, so its reader stands in for the century before it: today's real world at each year's US
prices, the moneys and goods held, the eight banks on their rule; Historical replays the real years from 2000, and the real
century is EP1b's. The rankings' real dollar bridges 1900 to 2015 (x28.4). Saves before format 50 are refused. The audit's cent is
scaled too (MoneyAudit.CENT). The order search also asks the first slice that borrows (OS1: Game.largestSlice() said 0 where the
countdown found 10, month 1226, Retail's 94 Convenience Stores: a bond's cheaper rate passes a few slices the doubling stepped over).
Suite green on IC1 (0.8.94): ScaleCheck's section 6 copies the playtest's town at month 718, 1124 having sat in its bust (ST2,
folded in; a draw, of months 710-726 only 717, 718 and 722 hold: ST3 queued), PriceLevelCheck 8 to month 105 (a rounding tie at 106); ensemble 23/26 as 0.8.91.

### 0.8.94 — IC1, the builders' idle crews free for other work — 2026-11-01, ready, see docs/queue/IC1-notes.md
After a boom the builders go idle at their core crew and lay off most of their posts, and the staffing gate counted those
posts as spoken for (0.7.17: they "are hired back when work returns"), so its spare was the unemployed less the crews:
8-11 sectors stood at "the city could staff 33%", nothing was ordered and the crews stayed laid off (XS1: seed 11 held
1,300 months at 20,000 people). Now an idle builder's laid-off posts are spare for every planner's gate (Sector.staffing());
a working builder's, and other sectors' withheld posts, stay spoken for. Over the 16 AUTO2 seeds the town is 77% larger at
m300 and passes 40,000 at m976 against m1,033, the median end unchanged (335,718); seed 11 passes it at m770. A 12- or
24-month grace before the crews are spare was measured and not taken: the slow seeds are the tail, reshuffled. A founding
depot's village now opens its shop. WantedPostsCheck 11 causes the trap and shows it released; nine harnesses' fixtures
cause their conditions again on the moved paths. ScaleCheck's scaler now copies Freight's railway shares as shares (its
copy hauled K squared tonnes once the town had a line), and section 9 holds every sector in both copies, a building
being one size at any K (SK1); City History's kept page follows its frame's height, where it stood 4 px short (KH1).
Ensemble 22/26 (LD1 23/26: A3, one seed at its boundary).

### 0.8.93 — OV1, a well that opens the month two wear out keeps its own month — 2026-11-01, ready, see docs/queue/OV1-notes.md
Latent on main, met by LD1's path (WellCheck 4, the three red lines Jerus passed on "Ship, OV1 next"): `Oil.view()` nets the
wells standing against the stored vintages as one figure, so when a well opened in a month two wore out it dropped one of the
old vintages and never recorded the fresh well; the old well went on standing at its worn-out profile and was retired the month
after. `Game.retireWornOutWells()` now strikes the vintages before the month's retirements (a well opened so far is a vintage
of its month) and after each kind's (a well opening later is not netted against them). The line LD1's slice found, the strike
after alone, mends WellCheck 4 and not the other order; section 16 (new) causes that one, five wells with two a month short of
their life, and neither strike mends both alone. The default playtest's trace is main's but for its run time, so no ensemble.
Nothing new is saved; no format bump. WellCheck is green.

### 0.8.92 — TR1, replay a recorded city (the trailer's camera tools) — 2026-10-31, shipped, see docs/queue/TR1-notes.md
"Record this city" (the Menu, kept in the save) keeps every month - the save and the map, gzipped - in recordings\<id>\, written on
SaveWriter's thread; the month pays only the save's fill (3-5 ms on a 78,000-person city), about 0.2-0.5 MB a month on disk, and a
rewind rewrites from the month played on. The player's input is logged beside it for TR2's ghost. "Replay a recording" opens any
month on a throwaway copy in its own scratch folder, so nothing sticks and no save or slot is written: a time bar plays both ways
at 1-16 months a second (the map swapped while moving, the month loaded whole at rest, 0.2-0.8 s). Cinematic mode (C) hides the
chrome and the cursor, and a button sizes the window to 1920 x 1080. On the map: the wheel and W A S D ease, a drift, a glide
from view A to B through time (1, 2, 3), a fly out to the whole Earth and back (O, I), and faint GDP and population lines behind
the city (G). Recording is off by default and changes no month (ReplayCheck 6): no ensemble.

### 0.8.91 — LD1, a business short of workers bids the wage up — 2026-10-31, ready, see docs/queue/LD1-notes.md
Jerus's loop, from his own city (2026-10-31: "if the reason is not enough workers and the business can... then it raises its
wage?"): a building the staffing gate refuses posts no jobs, so its band's wage never saw it and the band's migrants had no
reason to come. Now a planner's staffing hold carries its test, and the posts of the bands it is short in are priced into
next month's wage as if offered (WantedPosts): only while the building would still carry the interest on its whole cost
at prime at the higher wage, never past the band's own posts again (the wage at most 1.41x from the bid), rebuilt each
month so it decays when the gate passes. A hold the builders' laid-off crews alone cause asks nothing (XS1's trap, IC1's).
Over the 16 AUTO2 seeds the Machine Works' longest young-city wait falls from 132 months to 40 at the median, cities pass
40,000 at m1,033 against m1,066 and end 3% lower; seed 11 passes it at m973 on a moved path. Saved (no format bump); the
People page's skill ladder names the posts priced in. WantedPostsCheck (new); eleven harnesses' fixtures cause their
conditions again on the moved paths; WellCheck 4 meets a fault of main's, queued as OV1. Ensemble 23/26 (0.8.88 22/26).

### ST1 — why ScaleCheck's town stalls since HD2, found — 2026-10-31, ready, no tag (study), see docs/queue/ST1-notes.md
The stall is the fixture's, not HD2's. Section 6's town never grew by its own plan: on 0.8.75 its growing months were a
public-works boom (cemeteries, police, buses, a health centre from m1,066, paid from a 3.0M treasury), and it fell from
8,104 at m1,200 to 5,254 by m2,500, dry from m1,165. On HD2 the town is ahead until m730, then 8% bigger, so it takes the
advisor's police at m731; their running costs (1.29M) and 1.03M less tax leave 390k for the m1,066 order and no boom. One
switch at a time, at m1,200: main 2,789; the coupon to the till 2,801, none 4,150, untaxed 3,233; no police 3,557; no
running costs 3,990; 0.8.75 8,104. No ensemble city is on that path (HD2's slowest: 15,178 at m900, the fixture 3,542).
Beneath it, the staffing gate holds the founding town (0-48% staffable, 29% unemployed, idle crews, flat wages): off from
m0 the town is 34,105 at m900, from m600 nothing changes - LD1's and IC1's ground. ST2 rescans section 6 after them.
No model change.

### 0.8.90 — CD8, the city borrows in any of the eight moneys — 2026-10-31, ready, see docs/queue/CD8-notes.md
The city sells bills and bonds abroad in any of the vault's eight moneys, not only US$ (Jerus, 2026-10-23): each on its
own money's curve - its central bank's policy rate (XR4), the city's one country premium and the term premium - sold to
the world as dollar paper is. A piece keeps its books in its own money and is read in D$ at the month's rate; its
coupons and face are paid at that rate, CD7's exchange line moves it by the D$'s move and its money's cross, the buyback
prices it on its money's curve, a piece falling due rolls into paper in its money, and proceeds held as reserves go into
the money borrowed (★). Borrow › Abroad picks the money, each one's fair yield beside it and the risk in words; the book
tags each piece with its money. A money with no rate this month is not offered. DebtMoneysCheck (new). No save key and
no format bump (the piece's money rides in its saved record). The default playtest's trace is byte-identical to main's,
so no ensemble; the suite exits 3, main's recorded reds only.

### XS1 — seed 11's stall under XR4 found at its cause — 2026-10-31, ready, no tag (study), see docs/queue/XS1-notes.md
No rate of XR4's holds AUTO2 seed 11 at 20,000. The staffing gate's own rule does: an idle builder's laid-off crews count
as spoken for (0.7.17). After a boom the builders stood idle with 3,187 crews laid off against 3,074 unemployed, so 8 to
11 sectors held at "the city could staff 33%" in 314 of 381 months, nothing was ordered and the builders stayed idle.
One switch at a time, end populations: main 68,654; XR4's unwind off 130,276, vault 351,006, US's rate 298,376, no
risk-off 363,853; the world quieted 23,327; the gate counting the crews as spare, armed inside the trap, 298,722.
0.8.87 met the same trap at m1,000 and left it by m1,074; every seed passes through it (122-482 months). IC1 is queued
to free an idle builder's crews (tested: no seed stalls, median 329,143 against 347,140). No model change.

### 0.8.89 — KP1, Screens kept: City History — 2026-10-31, ready, see docs/queue/KP1-notes.md
City History is drawn over last month's nodes instead of rebuilt (study UP4's KP1; Jerus, 2026-10-23: "Queue KP1-KP7"):
every part and row a scope keyed by what it shows, so a line picked or a hard time begun slides nothing onto its
neighbour's nodes. The big chart and the pins never leave the scene, so their window, zoom and crosshair hold as the
months land; the legend's chips and the range buttons are kept in the chart's own pool and keep their hover. The
range bar is a picture told its figures; a label is handed its graphic where it is made (People's and the chrome's
swatches no longer leave the scene each draw). A figure counts up only at 1x-10x ("Count up at 1x-10x only", ★4):
faster, a month sets it at once, on People and History. KeptCheck sections 14-20 compare the kept page with one built
new through 25 months and 29 choices; KEPT_HISTORY_MS 13. ScaleRows (two closed folds) stays rebuilt for KP2. No ensemble.

### 0.8.88 — XR4, exchange rates: interest abroad — 2026-10-31, ready, see docs/queue/XR4-notes.md
The eight central banks of the vault's moneys have policy rates (Jerus, 2026-10-21, ★9 and ★11): on Historical their
real monthly rates of 2000-2025, then a Taylor rule on each nation's inflation in quarter-point steps; on Random the
rule from February 2000. The US's is the world's price of money where the city has the world file: the real rate gap,
the hot money, the carry, the dollar curve, the peg's tie and the world's bids read it in place of the constant 2%
(the equity's required return keeps the constant). The vault earns each money's rate and keeps it; in a risk-off the
hot money and the carry unwind by their stocks. Policy names the US's rate, Trade shows the vault's interest and what
risk-offs sent home, a nation's card its policy rate. InterestAbroadCheck (new). The suite: main's reds and
LongPlaytest's two map findings at m4,000 (XR4-M); the trace moved, and the ensemble scores 22 of 26 against 24 (A3
and R3 newly red; AUTO2 seed 11 held near 20,000 people for 1,300 months, XR4-S).

### 0.8.87 — LB3, Rankings: your saved cities — 2026-10-31, ready, see docs/queue/LB3-notes.md
The other slots (1-10, the autosave left out) rank beside the city on the Rankings board, read and never written
(`SavedCities`): each its own world, pivoted by its own rate, parity and US prices in the units of its last save, so a
currency reform never moves a rank. Prices opens with them (D2): per year, each in its own year's dollars, or static.
Your cities at: Now or Your age (D3), one that never got there named with the month it stopped. The city's card draws
its rank over the years, one point a December, against Real world 2025 (D12); Compare beside YOUR CITIES opens it. The
history records the US price level (`usPrices`); a city's area is its dry ground, as the real figures are land area. A
save now waits up to half a second for a file another reader holds open (Windows refused the move, and the slot kept
its old history). Today's ten saves are from older builds and stay off the board. No ensemble.

### 0.8.86 — DW1, platforms in deep water, at deep water's cost — 2026-10-31, ready, see docs/queue/DW1-notes.md
An Offshore Platform now stands in sea deeper than 150 m (Jerus, 2026-10-31: "Allow deep, costlier"): past the fixed
jacket's limit it floats, an FPSO, down to 2,914 m, the Turritella FPSO's record at Shell's Stones field (BOEM). It is
the same building, and its price, its wells' and its upkeep follow the depth by the ratios of the EIA's NEMS offshore
cost equations (public domain): at 656 m 12.1, 2.2 and 2.0 times the jacket's, at 2,900 m 14.9, 3.8 and 21.8. The
premium is builders' work on the order, as a bridge's span is, in the quote and the investors' reckoning; the upkeep a
service bought from the world. From 450 m the wells lose 10.3% a year past their plateau, the IEA's deep-offshore rate,
against 8.5% (CC BY 4.0). The card, the quote and the Oil page say the depth and its cost, and refuse only past 2,914 m.
WellCheck 14 and 15 cause a deep field; EarthFoundingCheck's Valparaiso sea now takes one. The suite has only main's
known reds; the playtest owns no sea oil, so no ensemble. At today's prices only the player builds one (★1).

### 0.8.85 — LC1, data licences for a sold game — 2026-10-31, ready, see docs/queue/LC1-notes.md
Every data source the game ships, and every series EP0 found for EP1, read for commercial use on the day (Jerus,
2026-10-31: "only use sources for the rankings that allow that"); `docs/studies/licences.md` is the table, source by
source. The World Bank labels every WDI series CC BY 4.0, but its terms leave a third party's figure to that party, so
the 17 Rankings measures the IMF, UNODC, the WHO, the WFE, the UN IGME or UNESCO make (inflation, public finance,
banks, reserves, homicides, health spending, child deaths, tertiary enrolment) now rank the player's cities only; the
price level and the money against parity are the Bank's own market GDP over its PPP GDP, and the OECD's wages are
divided by the Bank's own rate. world-2025.json holds 7,049 figures (9,643). The exe's folder now carries the world's
credits beside the Earth's, each brought to what its licence asks; EP0's study names what EP1 uses in place of each
refused series. The suite's picked harnesses are green; the month never reads the file, so no ensemble.

### 0.8.84 — SG1, the student grant on a surplus share paid on cash — 2026-10-31, ready, see docs/queue/SG1-notes.md
The student grant on its surplus-share basis is now a share of last month's CASH surplus (Jerus, 2026-10-31, settling
CD7-G: "Cash only (Recommended)"), not of the budget's balance. The balance holds the exchange gain on the dollar debt
(CD7) and the gain on paper bought back below its book (CD3), which the budget books and no cash moves on; the grant
paid them out the next month as cash, and a loss that took the balance below nothing paid no grant on a cash surplus.
`Game.getTreasuryCashSurplus()` is the surplus plus the bridge's own not-cash row, read by the grant's bill and the
dial a picked basis starts at; the grant card names the cash part when it differs (★ it also adds back the discount
amortised, which the treasury never pays: the few tenths of a percent the grant moves with discount paper and no
gain). Nothing saved. DebtExchangeCheck's sections 11 and 12 pin the D$ 10% up and down on a schooled city and buy a
bond back below book; reverted, they fail 14 assertions. The default grant is FIXED, so no trace moves: no ensemble.

### 0.8.83 — CD7, the city's foreign debt revalued — 2026-10-31, ready, see docs/queue/CD7-notes.md
The city's dollar paper now books what the D$ does to it (Jerus, 2026-10-23: "both as you said, but queud right with
the WN batches"): at each month's reprice, every dollar piece's carrying value in US$ times the move from the rate it
was last valued at, WN4's rule for the vault, as an "Exchange gain (loss) on foreign debt" in the budget's result below
the operating lines. It moves no cash: the treasury bridge takes it out again, the EARNED steps name it, and the
rollover's netting and the fund's pay-in and floor leave it out (★). The book shows each dollar line's D$ move since
it was sold, green or red, in D$ and as a % of what it is carried at, with the total and the month's line; the
Government and Fund tabs show the line. Saved: a government slot, the history's debtExchange series and each sale's
rate. DebtExchangeCheck (new) pins the D$ 10% weaker, then back. The suite has only main's known reds; the trace moved,
and the ensemble scores 24 of 26, every figure as 0.8.81's.

### 0.8.82 — LB2, Rankings: the board — 2026-10-31, ready, see docs/queue/LB2-notes.md
A Rankings tab on the rail after History (Lucide's trophy, in money blue; Jerus, D5): the city ranked measure by measure
among the real world's 196 nations and 62 city regions (Real world 2025, LB1's file), or among the nations of its own
world at its month on the ten they carry (Your world, D15). It opens on GDP's full list in real US$ at this month's rate
(D9). Every money figure becomes US$ of 2015 once (a founding game dollar counts as a 2015 one, D1) and is only then
shown in the city's money, game US$ or real US$, so Money never reorders a board; Convert at this month's rate,
constant 2015 or PPP (D14). The page: four controls, a sentence that says what is on screen, the measures with pins, the
table with its marks (● ◐ ○), Around you, Your size, the city's card, and a place's drawer (the map's nation card opens
it). The city has 51 of the 70 measures, from its history (`CityMeasures`, with a life table); `Rankings` is the pure
model, held by `RankingsCheck`'s 130 checks. Prices waits for LB3. Nothing in the month reads it: no ensemble.

### 0.8.81 — WN4, the world's moneys, the vault in eight and the real US$ — 2026-10-31, ready, see docs/queue/WN4-notes.md
Every money of the world moves against the US$ each month on the exchange-rate study's rule (XR3 folded in): its PPP path,
the dollar cycle, risk-offs about once in eight years (the yen and the franc rise, the Australian dollar and the emerging
floats fall), terms of trade from the January solve, its own swing; pegs hold, soft ones break by the D$'s own rule, and
Historical replays the real monthly rates to 2025. The D$ feels the world: the cycle, a risk-off by its traits (a deep vault
and a surplus make it a haven), the prices of what the city sells. The vault keeps a balance in each of the eight moneys,
filled by a reserve mix (all US$ by default) and revalued as they move, with a money picker on Trade › The reserves. The
in-game US$ inflates on the US's path and the founding screen's dollar chips are gone (D29); the real 2015 US$ waits for
WN6's screens. SAVE_FORMAT and its floor 49: a save of format 48 or older is refused, as it would load wrongly (its dollar
would jump to the US's path in its first month). Ensemble 24 of 26 (main 23): R3 passes now; A2 and S1 red as on main.

### 0.8.80 — LB1, Rankings: the measures file — 2026-10-31, ready, see docs/queue/LB1-notes.md
The real world's latest figures for the Rankings' Real world 2025 board, baked into the resources: `world-2025.json`
holds 196 nations and 62 city regions on the study's 70 measures, each figure with its year, its mark (● published,
◐ a projection, ○ the game's estimate, with the rule that made it) and its source, and no 0 for a missing one; GDP in
current, constant 2015 and PPP dollars, the cities' shares of their nations, and the CPI-U 2015-2025 that turns any
year's dollar into a 2015 one. From the WDI (49 series, each CC BY 4.0), the OECD, the BIS, the UN's WUP 2025, the EIA
and the BLS, every licence read first; 55 measures have real figures and 15 rank only your cities (nothing a game may
ship publishes them; UNODC's prisoners and police wait on Jerus). `tools/world/measures.py` builds it beside WN0's
pipeline; `WorldMeasures` reads it and `WorldMeasuresCheck` holds it to its contract, 36 one-place breakages each
caught. Nothing reads it yet (LB2), so the trace is main's and no ensemble ran.

### EP0 — the study for EP1: the 1900 price level, Historical from 1900, the nations and the calendar — 2026-10-30, ready, no tag (study), see docs/studies/spec-1900-start.md
The factor is the US CPI's 1900 over 2025, 8.333 (est., Rees's index joined at 1913) over 321.943: 0.02588, or 1/38.6
(the BLS, the Minneapolis Fed). Canada's is 1/36.8 (Statistics Canada, its wholesale index joined at 1913), 5% apart.
No one place reaches every dollar: 68 goods, 6 wage rungs, 148 building costs, about 12 founding amounts and 75 dials,
beside ratios that must not move; one constant through four funnels and about 35 literals is recommended. People reach
1900 for 195 of the 196 nations, real output for 47 (Maddison), prices for 7 from an open source, the eight moneys
through the gold pars, four of their banks' rates, ten of the 16 commodity series as US prices; each gap has a rule.
55 of the 196 were sovereign on 1 January 1900: the rule recommended is today's places under their 1900 ruler, the
world file's territory rule with dates. The epoch breaks one reader (the world file's first year) and four saved
years, so old saves are refused. Thirteen calls for EP1, each with a recommendation; the series as ten CSVs in
docs/studies/1900-start/. Docs only.

### 0.8.79 — FN1, the Found-a-city name starts blank — 2026-10-30, ready, see docs/queue/FN1-notes.md
Jerus: "remove the default name of danzik, have it just blank, or if you search for a city, its that city name (unless
someone already typed something, then it doesnt change the city name)". The Found-a-city page's name field opens blank
with its prompt, "Name the city", showing, at the first open and at every reopening from the main menu, not on Danzik.
A place picked from the Earth search (a hit clicked, or Enter in the search) names the city with that place's name,
cut to 24 characters, while the player has not written a name; the next place searched replaces a name the search
filled, a name the player typed (or changed from the search's) stays, and a field the player clears is filled by the
next place again. Found with the field blank waits: it is the model's refusal, "Name the city.", under the greyed
button, as it was for any cleared field. The rule is `FoundingName`, outside the screen (NewGameCheck 16). Danzik stays
in the model: `Founding.DEFAULT_CITY_NAME`, old saves, the harnesses. A screen change: no save, no money, the trace is main's.

### 0.8.78 — EM2, a share quoted at the middle of its book, and short selling — 2026-10-30, ready (two of main's reds with moved figures named for the gate), see docs/queue/EM2-notes.md
A share's price is its quote: the middle of the best bid and ask, the last trade held inside the one side that stands, the
last trade with neither, fair value before the first; every reader of the price reads it, and a quote set by a book that
stands is current however old the trade. The world sells short where the quote stands 20% over value, an ask at that
band, lent by the households and the fund for .5% a year (more when hard to borrow), paying the dividend on what it is short,
held to Reg T's 150% and FINRA's 30% (a margin call or a recall buys in at the market), at most 20% of a company short. On
main's playtest a last trade stood over 10x the best bid in 638 company-months (Business Services: last 18.80 over a bid of
.0104 for 167 months, its asks resting at the stale print); now 85, and no quote stands over 10x value. The short's ask does
it: nobody bids that high, so no short opens in a real run. Saved as new keys. Ensemble 23 of 26, as FL1's (R2 in, R3 out).
HD2-S: no month on EM2's town, nor on the attentive player's; ScaleCheck stays red (the cash line's worst moved), as does
PassThroughCheck's price index (0.68% against main's 0.74%).

### RE1 — Real Estate's stall found at its cause — 2026-10-30, ready, no tag (study), see docs/queue/RE1-notes.md
On HD1's own tree, seed 4 reproduced exactly (44,157 at the end). The posts' payroll, 9% of the landlords' rent, took a
margin that the property-tax step had squeezed into a loss from m1,000. The loss emptied their till, and with no till they
could neither put 15% down nor pass the lender's cover without it. No home came after m1,057, and the staffing gate held
six to eight sectors, so the town sat at 25,000 for 900 months. Migrants were never priced out: the affordability pull stayed
at 1.00. One switch at a time, forked at m960: with the wages off the town reaches 98,855 at m1,800, and with the posts off
139,090. The floor, the latent demand, the gate and the down payment alone leave it stalled. On 0.8.77 the posts stall
no seed (16 AUTO2 seeds past 40,000 by m1,208), so RE2 puts them back as built and watches for the trap. The model is
main's: no ensemble.

### 0.8.77 — FL1, a works making a flow good stops running at a loss — 2026-10-30, ready (Jerus: "Ship, fix next (Recommended)"; FL1-R on "Fix first"), see docs/queue/FL1-notes.md
A works that makes a flow good - rod, wire and cable, pig iron, steel, pellets - no longer buys its inputs for exports
that sell under them. WN3's order rule held a works' inputs to what its month would make only for goods that keep, so
with the world file named Rotterdam's Rod Mills went on importing cathode at 120.1 to ship rod at 68.8 after a January
re-solve (GDP -997k), and Medellin's sank rod's world price to 3.33 against cathode at 443 (GDP -60.7M). Now a flow
good's home demand and its works' own need are made at any price; the spare is exported only while it nets over the
building's own inputs (an input the sector makes itself at what not using it saves), and on the world's slope only so
far as keeps next month's price 2% over that cost; a by-product comes out of its run. No negative-GDP month left in
Rotterdam, Medellin or the playtest's founding (7, 16 and 0 before). A reloaded bank now carries its desk's shares as
saved, not re-marked from the quote, which a share traded since the exchange's step had moved (28.21 of equity apart
after a load). Ensemble 23 of 26 against 0.8.76: R2 newly red, R3 passing.

### 0.8.76 — HD2, the coupon abroad as profit, kept abroad — 2026-10-30, ready (Jerus: "Kept abroad (Recommended)"; two reds on "Ship, fix after (Recommended)", 2026-10-30), see docs/queue/HD2-notes.md
The coupon a business earns on its dollars abroad is a line of its profit, as HD1 built it (finance income below the
operating line, taxed, in the dividend), and its cash now stays abroad: the dollars grow by it, the till pays the tax and
the dividend struck on it and calls dollars home only when it is short, and the balance of payments books it as
reinvested earnings, income in and the same out as investment. The cash flows carry it as a non-cash line; the share
register values the business without it, after its tax, so EM1's fair value counts the dollars once. MonetaryCheck's
rate dial keeps its sign (10.75/9.71/9.04/8.72% at 3/10/20/40%): EM1's market has already emptied HD1's hoard (a
hundredth of a year's GDP abroad), so even paid home it would. Ensemble 23 of 25 against WN3's 23 of 26: R3 passes,
the young cities a little smaller (AUTO2 median 280,956, WN3's 292,982). Shipped on "Ship, fix after (Recommended)", two reds pending:
ScaleCheck's town no longer grows into a city (HD2-S), PassThroughCheck's price-index line reads a housing event (HD2-P).

### 0.8.75 — WN3, the world market — 2026-10-29, ready (Jerus: "WN3 yes ship, ill look into it later"), see docs/queue/WN3-notes.md
The world outside the city stops being a price list: where `world-1.bin` is named, 196 nations trade each of 60 goods on
a smooth spatial equilibrium solved every January (19-31 ms), and each traded good's import ceiling and export floor
come from where the city stands, what it traded last month (a big seller depresses its own price) and the good's path,
calibrated so Halifax in 2025 sees the old literals. A new world's path defaults to Random (Jerus, 2026-10-29: "A new
world defaults to the Random path instead of Historical"), Historical a chip on both pages. A works now orders its
inputs for what its month will make (an idle refinery had bought 114-242k a month of anode for nothing, GDP negative in
6 of 16 ensemble seeds). A sale of linked paper is quoted on the debt the books show after it, its indexation counted.
The city's trade is kept by nation (PartnerTrade, the History by partner and good, saved; SAVE_FORMAT and its floor 48),
drawn on the map as lanes and on each nation's card. Ensemble 23 of 26: AUTO2 median 292,982 (EM1's 261,977), F1 clean,
every seed past 40,000; R3 red in 3 seeds (in two a higher rate lifts rent more than the stronger D$ cools imports),
shipped on his word with its study queued (WN3-R).

### 0.8.74 — EM1, an efficient market: one value for a share, every holder trading on it — 2026-10-29, ready, see docs/queue/EM1-notes.md
A share has one value: the company's money past a six-month cushion at face, plus its last year's net income at the world
rate plus 3% (a company in its first year on its prospects, as before); and one expected return, its income and what its
money earns over its price. Households, the world and the fund sell a tenth of a holding a month into a bid 5% over the
value, households also when its return is 5% under the deposit rate, the dollars abroad, the city's paper or another share
on offer, and buy the best return; the desk quotes on the new value. So a buyback, bidding 10% over the value, finds its
sellers and a company's cash reaches its owners (his 0.7.12 call kept: no special dividend). On the default playtest the
companies' tills fall from 9.17x GDP to 0.39x at m4,000 and their dollars abroad from 36.59x to 0.01x, every buyback bid
fills (540B a year was left resting), P/E sits about 22 across companies, and the town ends at 656,580 people against
599,734. The sector screen shows the value as money plus business, with its return; MarketCheck causes each case. The
ensemble: 23 of 25 as before, growth up (AUTO2 261,977 against 255,250), its inflation spells shorter.

### 0.8.73 — HD1, the hoard: business bonds in the history — 2026-10-29, ready, see docs/queue/HD1-notes.md
Business bonds go into the history: the face of every business bond outstanding, beside the business debt (loans and
mortgages only), so the bonds' fade in his runs (the last issued at m1,533) can be charted, on the History screen and in
the year book; SaveFileCheck 13b holds it across a save and a reform. The brief's other two items were built and taken
back, the orchestrator's call on the session's recommendation, as calls for Jerus. The coupon on the businesses' dollars
abroad, as profit paid home, halved the hoard abroad by m4,000 (35x GDP to 18x) but turned the rate dial upside down in
a young city (inflation 7.9/6.3/6.8/8.2% at 3/10/20/40% against main's 10.4/9.7/9.0/8.8%): paid home, it lifts the D$
at low rates more than the dial restrains (HD1-C; recommended: counted as profit, kept abroad). Real Estate's posts (2
DIPLOMA a Studio and Low-Rise, by the NAA's rule) stalled the ensemble's seed 4 near 25,000 people (44,157 at the end
against 249,634; RE1, its cause first). The model is main's and so is the trace: no ensemble.

### 0.8.72 — BK1, the bank's equity split and its interest margin — 2026-10-29, ready, see docs/queue/BK1-notes.md
Issue #5, Jerus's San Francisco city. The bank's equity no longer drifts from its two parts: a line of the city's
paper keeps one pool of discount shared by face, and reopened at another price while households, firms, the world or
the central bank hold part of it, the pool re-blended and the bank's unearned discount moved with no cause named
(found at m260 of 0.8.52's San Francisco playtest, +$0.856k; $212.5M in his city). The re-blend is now measured at the
settle and the central bank's add-on and routed to retained earnings, a row of the equity's movement; no profit, tax
or dividend moves (HoldersCheck 12, against a twin that books none). His save keeps its $212.5M and its red card (no
conversion) but no longer grows. The interest margin is on earning assets - the book and the reserves, which earn
the policy rate - month and year: San Francisco's 9,972% was reserves 700 times the loans. The trace is main's.

### 0.8.71 — UP5, the map's month, the rest — 2026-10-29, ready, see docs/queue/UP5-notes.md
The map's month at his size and at 5B, at its causes. A month in one district no longer re-plans every district after
it: a plan read by another keeps only its edges' hash, and a district whose carry and edges hold takes its old plan
(x 80: 11 plans against 134, the worker 1.9 s to 0.3, every picture current after the month). The close-up reads a
tile again only when its key moves (5B's L0 6.0 to 1.6 ms); a far node's runs key is its own, so a month that lays a
highway re-colours the nodes it crosses (6 of 40 at 5B, not all). FX7's two: a plan replays the runs on a kept ground
(96.9 to 6.5 ms on the plan with most records), and an interchange's reserve is dated, so one that appears moves only
the boxes under it. MAP_MONTH_MS 58 to 19 (5B month in turn: main 21.3, UP5 17.2). The 10B month's red line measured:
nothing grew since MP1, its worst month is the one after a big month (UP8 queued). The map's alone: the trace is
main's, no ensemble; the map's sidecar FORMAT 15.

### 0.8.70 — MF1, the map's faults in his For Claude city — 2026-10-29, ready, see docs/queue/MF1-notes.md
Jerus's eight map faults from 13 shots of his city (the save no longer loads; reproduced on copies of slots 7 and 8 and
the playtest's city). Buildings under or beside a highway, track or yard laid after them move off it and are rebuilt
nearby, the same buildings, nothing else moving (PlanCheck's save section 9: 7 of 14 districts red, now 14 of 14). Every
tile reads a crossing alike, so a roundabout is one disc; a cloverleaf is four leaves, not four rings; a street several
plots wide is one road. The plans' sidings curve and run trains, keep apart, and a T no longer breaks the line; one no
unit train fits is not drawn. The yard relaid 16 times was the model selling its only depot and buying it back (FD1,
queued): the map keeps such a yard idle 120 months. The overflow stands by the streets, not one to a tile on the far rim.
A coast's berths gather in one or two harbours on short common quays, the rest beach (slot 8: 106 piers, now two
harbours). The map's alone: the trace is main's, no ensemble; the map's sidecar FORMAT 14.

### 0.8.69 — WN2, the nations on the map: borders, dots, labels and the card — 2026-10-28, ready, see docs/queue/WN2-notes.md
On a city on the real Earth, the expanded map's head has two new chips, **Nations** and **Borders**, both on until the player turns them off, and the map remembers them. The nations' borders are drawn dashed white at the far views. Up close, a border is drawn only where one crosses the city's land or its offers. Each nation has a dot at its largest port or city, sized by its output, with its name, output and people written on its land wherever they fit. Hovering a dot shows its figures; clicking a dot or a nation's land opens its card: GDP, people and GDP a head, each ranked of 196, this year's growth and prices, and its money's rate. The legend now folds at its head. `NationsMap` keeps the borders at five levels of detail and culls them to the view, and the map stays as fast: GlobeCheck times a view's borders at under 1 ms. Trade routes, with their chip, come with WN3's trade by partner. Nothing in the month reads the nations, and the playtest's trace is unchanged.

### 0.8.68 — SL1, fewer ports and the sea lanes shared — 2026-10-28, ready, see docs/queue/SL1-notes.md
Jerus was right that a late city's sea terminals are its slowest part: measured on main, a month that laid one more terminal cost 1.6 s at 300 standing (the model's month is 15 ms) and the map took 183 s to draw, because every plot the shore read asked every work on it, and every terminal laid found every route again. Now each terminal is a real one of several berths, cited (Tanker 30 Mt a year, Bulk Terminal 50, Container 24.3, General Cargo 2; price, jobs and ground scaled with it), ordered by the kind's whole trade and as many as the tonnes pay back: the default playtest stands 21 terminals at the end where main stood 46 (8 General Cargo where it stood 37), at 599,734 people against 544,112. A terminal's route joins the lane within 10 km by water and goes on along it, so a coast's ships share one lane that branches only where no lane is in reach; routes are found once and kept, each leg is drawn once, and the shore is laid at the cost of the change: at 300 terminals the map draws in 0.6 s and a month's terminal takes 0.6 ms; at 10 billion every route is found in 0.2 s. The ensemble's two reds are the baseline's; growth is within the noise and a little up.

### 0.8.67 — WN1, the nations on the world's path: Historical or Random — 2026-10-28, ready, see docs/queue/WN1-notes.md
The founding screen has a new step, THE WORLD BEYOND YOUR CITY: the world's path, Historical or Random, and the path's own seed with its dice. The five inflation chips are now THE DOLLAR'S OWN INFLATION, which is what they always were. `Nations` gives each of the 196 nations its year (output, people, prices, its money's rate) and each good's real price, from the path, the seed and WN0's world file:
- Historical replays 2000-2025 from the file, then the game's own rule with small seeded shocks.
- Random draws its own path from 2000: growth that converges on the richest, world, region and own swings, a crisis now and then, inflation by income group, and moneys by parity or their peg.
The path and seed are saved with the city. Nothing in the economy reads the nations yet (the market is WN3), and the playtest's trace is unchanged.

### WN0 — the nations' world file: 196 nations, their borders, trade by pair and good, paths 2000-2100, sea distances, monthly rates — 2026-10-28, ready, no tag (data nothing reads yet), see docs/queue/WN0-notes.md
`tools/world/`, a pipeline in Python's standard library, bakes `world-1.bin` (8.7 MB, not in git: SHA-256 in `tools/world/world-1.sha256`, the release `world-1` as earth-2's, `get-earth.bat nopause world-1` fetches it). It holds the 193 UN members, Taiwan, Hong Kong and Macau, and 15 areas no nation of them holds. Its ten layers:
- the owner of every 15" cell, from Natural Earth's borders, and the border lines;
- each nation's node, label, money and region; people 2000-2100 (WPP 2024), and output, CPI and rate 2000-2025 (WDI; PWT and Maddison fill, marked);
- 158 moneys' monthly rates 2000-2025 (BIS, ★11);
- 2024's trade by pair and good (BACI HS22 through a concordance to the 60 traded goods), kept by Jerus's partner rule (93.6-99.9% of the value), with make, use and the preferences the base year's solve reproduces exactly;
- CERDI's freight closed under the triangle rule, and each good's real price path for WN1.
`WorldData` reads it; `WorldDataCheck` checks it on a twelve-nation fixture and the full files. The playtest's trace is unchanged.

### 0.8.66 — FR9, one truck, one whole trip — 2026-10-28, ready, see docs/queue/FR9-notes.md
A truck on the map is now one whole trip (Jerus, 2026-10-20: the trucks "disappear into the void... but still inside the city"). An import runs in from the world line along the highways, off at a ramp, along the streets (arterials first) and down an access road to its works' gate, then back out empty; an export the other way; a load bought at home (violet) runs from one works' gate to another's. The street leg is one search a drawn district from the highways' ramps over the plans already drawn (`CityMap.tripsDrawn()`): nothing is planned for it, and a district not drawn takes no trip. A trip runs at the trains' pace, 6 to 10 s a way by its length, and the legend's "one per N loads" is unchanged. A district's trips carry its works' truck tonnes, each sector's from `Haulage.movesOf()`, and those of the districts within two that have no trips of their own (a Truck Depot's sector moves no goods). A city whose highways do not yet leave it keeps FR5's access-road trucks. TruckRouteCheck 7-10 are new (148 assertions); MapCheck 9 times the trucks at 10 billion, 0.03 ms a frame against 0.5. Renders are in `docs/queue/FR9/`. The map's alone: the playtest's trace is byte-identical, no ensemble, no save change.

### 0.8.65 — FR8, the trucks' share dealt per industry — 2026-10-28, ready, see docs/queue/FR8-notes.md
When the trucks cannot carry the whole need, their room is now dealt per industry at each haul (Jerus, 2026-10-20: "pool per industry", on FR2's ★5): each sector first keeps the room it was dealt last month, the smallest first, and the growth over it - the newest demand, a works just opened - is served last, pro rata, from what room is left. A sector runs and is billed at its own share, so a giant works landing slows its own industry and no other; the city's share, the order, the price and the depots are as they were. Each sector's share and standing room are saved under Freight's `haul.*` extras (an older save deals its first month as the one share). HaulageCheck section 11 proves it, and LongPlaytest counts the months a sector other than the newest ran short: 67 in the default run, where the one share had every bulk sector short in 780. The ensemble passes with no new red, AUTO2's final population 246,893 against 258,838. ★ calls: the standing demand served smallest first, held at the room dealt at nameplate, a sector asking nothing dealt a whole 1, and HomeRailCheck's real run reading the room the haul saw; MapCheck's highway, district-picture and picked-block fixtures now cause their conditions on any path.

### 0.8.64 — FW11, no street piece walled in by buildings — 2026-10-28, ready, see docs/queue/FW11-notes.md
A street piece the plan's own buildings (and ground the city does not own) enclosed was left apart: `join()` walks free ground and never over a building, so it marked the piece parted though the ground does not part it (W1 at 0.7.99; since PT1-M4, MapCheck 8's recorded red: district (0, 0) of the played city, a 22-plot boulevard remnant in the iron blob, one house on it and four between it and the street). PlanCheck missed it because its districts have no carry and the drawn plan does. Now a piece with no free way looks for the way over the fewest buildings' plots (at most 16), and `make()` plans the district again, up to 3 times, with that way kept clear of buildings: the buildings on it find other lots in the district, or go on to the next as any building a district has no place for, and the join lays the street. The played city's district joins (24 plots kept clear, 227 of 231 houses placed, 4 more packed at the edge); the same case moved MapCheck 2c's and 2d's full founding-site districts (by 11 and 6 boxes) and the river fixture (4 pieces to 3), none failing. PlanCheck 17 is new (MapCheck 1's city as the map draws it; red with the way off); MapCheck 8 is green, one known red fewer. The map's alone: the playtest's eight trace files are byte-identical, the layout watch's counts the same, no ensemble; no save or sidecar change (FORMAT 13); no plan slower.

### 0.8.63 — CC1-M, the city map across a save — 2026-10-28, ready, see docs/queue/CC1-M-notes.md
The map's failure of 2026-10-12 (CC1's childcare playtest, m1,184) was a reset railway net keeping its hub, so the map read back from a save was not the same; FR6 mended it on 2026-10-19 (64ab8773) without knowing it was this row, and CC1's own tree rebuilt here fails without that line and passes with it. Looking at the month after a load (FW6-C's ★1) found the one map that still moved: on a coast where the railway's main line runs on past the sea, the first month after a load took the line back past its sea turn and laid it again shorter, for good (293 plots against 297), because what had stopped each arm (`CityRuns.Arm.stoppedBy`) was not saved. The sidecar keeps it now, FORMAT 13 (a byte an arm; an older sidecar reads as before). GatewayCheck 21 causes it and holds the map and its sidecar byte for byte across a save and 30 months on; ReloadCheck compares `mapStamp` again. The map's alone: no save-format change, the trace is main's, no ensemble.

### BM1 — the batch manual split by reader, and one in, one out — 2026-10-28, ready, no tag (docs), see docs/queue/BM1-notes.md
`docs/batch.md` was 499 lines and every batch session read all of it, though it needs about the 130 of "A batch session". It is now that checklist alone (10 steps, to a pushed branch with its ship file last), and the orchestrator's part (the day, the gate, the screen-check session, the PC, the Earth file, the other assistants, which model, the docs pass) is the new `docs/orchestrator.md`; `CLAUDE.md` names both and says who reads which.
Every rule TM3, GP1, RL1 and SP1 added is kept, each with a one-line WHY naming its incident; the lessons (a)-(f) and the longer stories are `docs/notes/batch-history.md`. A batch's report is now "ready, see the ship file" plus its ★ calls with recommendations.
One in, one out is a standing rule in `CLAUDE.md` (Jerus, 2026-10-27): a batch that changes the process removes about as many lines as it adds, and its notes give the count. BM1's own: a batch session reads 391 lines (`CLAUDE.md` and `docs/batch.md`) where it read 750, the orchestrator 657; 479 lines added, 511 removed.

### SC1 — ScaleCheck's "...and it builds" already green; its landlords in the log — 2026-10-27, ready, no tag (a check), see docs/queue/SC1-notes.md
The red the batch was queued for (Jerus, 2026-10-20, "Queue a check fix"), the ten-billion copy's "...and it builds: more homes at the end than at the start", was FR7's finding at the fixture month 600; XR1 (622) and PT1-M4 (807, every assertion holding from 792 to 822) rescanned the month since, and on main it is green for both copies, so no scan and no fixture change was needed.
What the batch added is the brief's third item: `ScaleCheck.Lender` counts the landlords' mortgage orders in the months read, the city's and each copy's - how many the lender passed, held for the down payment and declined on the rent - and prints them (6 of 6 passed at 807; the 10-billion copy ordered none), so a later model change that moves the lender's verdict shows in the log. Printed, not asserted; the fixture's household places are printed too.
No game change, no version bump, no trace.

### SP1 — the suite three at a time, each harness JVM under a memory cap; a paired timing red judged by its budget too — 2026-10-27, ready, no tag (tooling), see docs/queue/SP1-notes.md
`AllChecks` runs up to three harnesses at once (`PARALLEL`, `-Dcbs.parallel=N` for one run, at most half the cores), the longest first, its output in `HARNESSES` order as before; `--timing`, a hard run and the gate's paired rounds stay one at a time. Each harness JVM runs under `-Xmx2048m` (`HEAP_MB`, measured: none committed more than 1.5 GB) and starts at `-Xms64m` (`HEAP_START_MB`: the default 508 MB was committed at once, BooksCheck 616 MB of commit against 167 now), and so does the runner.
Memory, not cores, is the PC's limit (41-46 GB, 80-96% busy this evening), so no harness starts beside another while the commit charge is over 75% (`COMMIT_PCT`), and the suite says how many starts it held. `timing.tsv` is appended under a file lock.
Item 7, on Jerus's word: a paired timing red is not the batch's when main's median misses the budget too or the batch's is within it, each pass says why, and the paired rounds alternate which side goes first. Not taken: the timing harnesses once per gate (`gate.txt` is the suite's soft lines, a hard run's differ; SP1-D).
Out: `batch-check.sh`'s who-else-runs look, `ship.sh`'s redundant soft `JDK_JAVA_OPTIONS`. Scripts and rules 77 lines added, 63 removed; `ship-selftest.sh` 109 of 109. The N=3 against N=1 suite and `--pick` are not run (SP1-C): the PC never fell under the 85% rule long enough tonight, so the gate's suite is their first run.

### 0.8.62 — BR1, the name and the look: Civic Ledger, its icon and its title art — 2026-10-27, ready, see docs/queue/BR1-notes.md
The game is Civic Ledger, Jerus's pick of a shortlist checked against Steam, itch.io and the US trademark register (Ledger City turned out a live mark for game software). The start screen and the window's title say so; the repository, the exe and the save folder keep CityBuilderSim, so no player's cities are left behind.
It has an icon at last, "towers as a chart" (four towers rising like a bar chart, each lit in one of the palette's colours, the money line along their roofs), on the window, the taskbar and, through Build EXE.bat's new --icon, the exe; and title art, "the ledger's total" (the name over an account's closing double rule, its top line climbing like a chart), which the start screen draws itself and branding/ holds for the stores.
tools/branding/ makes all of it from the title screen's palette and font with Edge headless and the standard library; BrandingCheck holds the files to ui/Branding's lists and the store's title art to the start screen's proportions.
The itch.io page has a new cover, eight new screenshots from a San Francisco run, and Graphics ticked in its AI disclosure.

### 0.8.61 — MP1, The map fast again — 2026-10-27, ready, see docs/queue/MP1-notes.md
On a quiet PC main missed one map timing line, the month's map work at 5B (58 ms): 66.4, 56.9, 59.1 ms in three
hard rounds; every other line in MP1's table passed there, so what the gates saw on 2026-10-26 was mostly the PC's
load. Profiled, the 5B month was three wastes, all there since UP3 set the line (no later batch slowed it): the
interchanges' reserve guard walked all 14,661 districts' mined sites on every call (FR7), now once a change of the
map; the world line read each plot four times, now once; and the district index's keys all collided (a Long's hash
of dx << 32 | dy is dx ^ dy), now spread. The far node over Medellin parsed every tile's rivers and sorted their
widths on each call (E5); each tile's drawable runs are now kept, and that line's time is judged on a line of its
own (the gate's paired rounds can read it). Quiet, in turn with main: the 5B month 21.3 ms, Medellin's far node
54.5 -> 31.4, every line passing three rounds in a row. Same pixels and trace; at 50x a five-billion city's map now
follows the clock month by month, and at his city's size it never slowed the game.

### RL1 — a lane's next batch starts at its predecessor's ship file, not at its merge — 2026-10-27, ready, no tag (tooling), see docs/queue/RL1-notes.md
The batch behind a gated one waited for its Queued row to leave `QUEUE.md`, so every minute from ship file to merge (a median of about 60) was paid again. Now the successor is cut from the predecessor's tip (`git checkout -b batch/<id> origin/batch/PRED`) when its ship file lands, builds on it, merges `origin/main` when the predecessor lands, or its fix when it is sent back, and pushes its own ship file last.
`tools/carries.sh` is the one test of "built on an unmerged batch", asked by the five places that read a branch as cut from main: `pick.sh` diffs against the predecessor's tip, `ship.sh` stops a branch built on one main lacks, `day.sh` calls it waiting and `wait.sh --ship` leaves it out, `batch-check.sh` says under a NEW red that the predecessor's may not be recorded yet, and `wait.sh --row` also ends when the predecessor is ready again after a send-back.
Out: `day.sh`'s old-style ready test, `ship.sh`'s old way (a batch with no ship file ships `--no-tag` or stops), BP1's "calls" section of `docs/batch.md` (folded into its rules). `pick-selftest.sh` 13 of 13, `wait-selftest.sh` 24 of 24, `ship-selftest.sh` 106 of 106.
One in, one out: 261 lines of script and rules added, 182 removed (192 against 160 without the selftests).

### 0.8.60 — I13-L, the landlords' card asks the mortgage lender, and the Ore card covers every ore — 2026-10-27, ready, see docs/queue/I13-L-notes.md
Two notes I13 carried (Jerus, 2026-10-18). A landlord's home was not asked the INTEREST gate - the mortgage lender's test decides it - so its build card showed no last gate though the model has one.
`Game.lenderTest()` is now the card's door to that test, the call the order makes (`lenderReads()`, shared with `considerOnMortgage()`); `BuildCard` has a LENDER gate for a landlord's home, in the lender's words (what it lends, the payment against the rent, the cover against what it asks, or the down payment it lacks), and the Real Estate page's grid a "lender" column.
In a city with little landlord cash a House card now says why the lender would refuse it. The land office's Ore card was iron's alone: `OreView` gives a row for every ore the city owns a site of (iron always), and the card a block for coal, stone, copper and uranium with its deposits, tonnes, the sites its mines take,
"nothing is digging" where a deposit has no mine, and a click to the industry that digs it; iron's words are as they were.
`BuildCardCheck` 14 and `LandCheck` 30 new; the default playtest's trace is byte-identical (no ensemble).

### 0.8.59 — ZT1, one palette for zones — 2026-10-27, ready, see docs/queue/ZT1-notes.md
Jerus's call (2026-10-18, "rec all"): a zone is one colour on the map and on Policy > Zoning. Zoning coloured a zone by its place in the list, eight colours cycled, so a sector's colour changed when an earlier zone came off and was never the map's.
Now Zoning paints a zone in its sector's colour, the map's own (`LandMap.zoneColourHex`), and the old table is gone. Checked on Zoning's own ground (built grey, the land office's pink, the forest's three greens), two sector colours were lost against it:
Luxury Retail's pink was the land office's pink and Forestry's green the open forest's; both moved in `LandMap.ZONE_COLOURS`, so the map's tint moves with them. `ZONE_GROUND_APART` (20, CIE76) is the least any sector's colour may differ from each of the five.
`ZoningViewCheck` 10 holds it: every sector's colour on the screen is the map's, a later zone keeps its colour when an earlier one is removed, the map's legend and the screen agree, and none is within the dial of the ground.
No save or model change; the default playtest cannot move.

### 0.8.58 — MS3, The slanted ground — 2026-10-27, ready, see docs/queue/MS3-notes.md
Far from the city, the expanded map at the near scales drew every patch of cover as a thin diagonal streak and sawed
the shores (Goose Bay from a Singapore city). Measured first: the city's plane is an equal-area projection whose shape
distortion, 2 / (1 + cos c) at an angle c out, stretches its noise-drawn patches along the line to the city, 4.5 to 1
at Goose Bay; the node lookup and the turn to north were cleared. Past a distortion of 1.25 (5,908 km out) the near
view is now drawn from charts: north-up pieces of the globe about anchors every 10 degrees, cached as the plane's
nodes are, the Earth's cells read at each pixel's own point and the cover ranked through a noise on the sphere,
blended in from the plane's over 100 km so no line shows. They also draw the ground off the world's square, where the
far rule's speckle was. Far patches read 1.0 to 1 again (the plane's 2.4 at Goose Bay); the model and the trace are
unchanged. GlobeCheck 2d measures the streak, the fix and the seam.

### GP1 — the gate starts at once: no quiet minute, a ready branch picked up when its ship file lands — 2026-10-27, ready, no tag (tooling), see docs/queue/GP1-notes.md
From ship file to merge a gate took a median of about 60 minutes, of which `ship.sh` is 22-25: the rest was the hourly check-in, and TM2's quiet minute (up to 30 minutes for no other batch's JVM) when the other lane was running.
The quiet minute is out, with all that served only it (`quiet_minute` and its hook, `jvms.ps1 -Quiet`, the `CBS_QUIET_*` dials, `Batch check.bat`'s wait, `quiet-selftest.sh`, `CBS_TIMING_PAIRED`): `--timing` runs at once, and a red under load is judged against main's own run in the same minutes (TM3).
`sh tools/wait.sh --ship [--skip ID...]` is one background wait that ends when an unmerged `batch/` branch on origin has its `docs/queue/<id>-ship.md` (a branch ready already ends it at once; a merged or deleted one does not count), so the orchestrator's gate starts within minutes of the push; the hourly check-in stays as the fallback.
`docs/batch.md` (The day) says how it is armed and re-armed after each gate. `tools/wait-selftest.sh` tries it against a throwaway origin, 14 of 14; `ship-selftest.sh` 103 of 103.
One in, one out: 239 lines of script and rules added, 555 removed.

### TM3 — the gate's timing judged side by side, main against the batch — 2026-10-27, ready, no tag (tooling), see docs/queue/TM3-notes.md
A timing red at the gate was judged by one run of the merge's and one of main's minutes later, so a PC busy with Jerus's game stopped HY1, MS2 and CC1-S on map lines main itself misses.
Now every timing assertion writes its figure and budget to `target/checks/timing.tsv` (`Timing.verdict(ok, label)`), and when the hard run has a red, `ship.sh` runs the red harnesses
again with main's classes and the merge's in turn, three rounds, no quiet minute between them, and passes the red unless the batch's median is more than 15% over main's
(`tools/timing-pair.awk` holds the arithmetic and the dial); the Gate-timing line and `gate.txt`'s `#` lines carry both medians. `--red-ok` reaches that step, and (the review) passes only the lines `--red-line` names, in the suite step too: without a `--red-line` it is refused.
MapCheck's "the view adds nothing a tile", a comparison of two measured times that stayed hard in the soft suite, is a `Timing` assertion, and TimingCheck fails any other.
`ship.sh` runs from a copy of itself, so a batch that changes it cannot rewrite it under the shell, and `--pick` picks TimingCheck whenever a `*Check.java` is in the diff (`pick-selftest.sh`).
`ship-selftest.sh` 104 of 104 with stub figures; one end-to-end rehearsal on real JVMs.

### 0.8.57 — ET6, Bridges — 2026-10-26, ready, see docs/queue/ET6-notes.md
Short crossings: a highway or railway now bridges up to 1,020 m (34 plots; 14 before) of fresh water or shallow sea, so a
Halifax city crosses the Narrows where its two bridges stand; a street keeps 180 m, an arterial 420 m, and deep sea still
stops a run. Long bridges: a Highway, Rail or Road Bridge (ids 145-147) is ordered toward another landmass the city owns
ground on. The model surveys the shortest chain of fresh or shallow water to the city's buildable ground on a linked
landmass, all or nothing, up to 36 km, and refuses before land and money. The span is priced by length, kind and depth,
from ET4's deck rate up to the Confederation Bridge's (C$1.3 billion as built), and holds the builders for years. Once it
stands, it links the far landmass as a port does, is saved, and cannot be demolished; the map lays it as a highway or
railway of its own, drawn as deck with its landings kept clear. Borden-Carleton to Cape Jourimain surveys 13.3 km (the
real bridge 12.9). The default trace moved only in the map's layout watch, where generated lakes are now bridged.
CrossingCheck, BridgeCheck and BridgeMapCheck are new.

### 0.8.56 — CC1-S, care held essential, and why the auto-built villages stand still — 2026-10-26, ready, see docs/queue/CC1-S-notes.md
Jerus's call (2026-10-18): care stays essential, built and kept when it does not pay for itself. It already was
(AutoBuilder.essential(), since 0.7.104); AutoBuildCheck 8 now holds it for all three kinds of care on a deficit, and a
school to its payback. Four of eight auto-built seeds (0, 1, 4, 5) sit at about 2,000 people for some 310 months, not
for care (a fifth of the revenue): at month 32 the test player borrowed $28.8M abroad for two Bakeries at 18.45%,
comparing the rates before the sale (2.00% against 3.00%), not its quotes (18.45% against 3.03%). The coupon took two
fifths of the tax, the cash ran out and no ground was bought: "no land" from month 140 to 357. LongPlaytest's
borrowsAbroad() reads the quotes (ForeignDebtCheck 10), behind -Dplaytest.quotedBorrowing, off: on, five harnesses on
the played city go red and seed 4 still waits, on jobs. Jerus's theory that schools start the growth did not hold: no
seed builds one before 3,000 people, a school pulls only as an employer, and built early as essential the schools keep
all eight seeds villages for 900 months. Schools stay weighed for their payback. The default playtest is unchanged
but for one summary line: no ensemble.

### 0.8.55 — MS2, the near view drawn the same past the reach — 2026-10-26, ready, see docs/queue/MS2-notes.md
Zoomed in past 300 km from the city the map drew the far rule through the globe's camera: speckled ground, every small
lake a perfect circle, the globe's short legend. It now draws the plane's own ground there - its far nodes at the
scale's stride, down to a plot a pixel, through the camera - wherever the world's square holds the ground (all but less
than the fifth of the planet farthest from the city, where the far rule stays), with the plane's legend, hover and
block lines, and the deposits' tint the plane's tiles have. At Jerus's 2 km scale the ground drawn equals the plane's
plot for plot on both sides of the reach: no seam. GlobeCheck 2c new. The default playtest's trace is byte-identical
(no ensemble).

### 0.8.54 — HY1, the Hydro Station — 2026-10-26, ready, see docs/queue/HY1-notes.md
A Hydro Station (id 144): 10 MW at 45%, $30M at $3,000 a kW, five posts, on any ground, steep too. It needs a river
with a drop on the city's own ground, on a linked landmass. Earth-2 has no heights on land, so the drop is read from
the banks: along each GRWL river, both banks' rise over 300 m at each slope class's least grade, the head the lower
bank's (10 m at least); the flow from the river's width (w = 7.2 Q^0.5, capped at 550 m, Niagara's flow). One site a
3.84 km cell, the best point, the city's when it owns the dam's plot; a site holds as many stations as its mean power
allows. An order past them is refused NO_RIVER before land and money, and the card names the slots and the best site.
Real hydro towns read sites (Shawinigan 6, Niagara 3), Rotterdam and Winnipeg none; a generated world has none. At real
costs the advice still names wind farms (★4): hydro is the 1900 city's. Sites are derived, never saved. HydroCheck new.
The default playtest's trace is unchanged: no ensemble.

### 0.8.53 — BL1, buying land revamped: the player picks the exact ground — 2026-10-26, ready, see docs/queue/BL1-notes.md
On the expanded map the player now picks the exact ground: any whole blocks of the city's grid joined to its ground,
by a click or a drag (a preview shows what the box takes), priced by the office's own rule at today's price and bought
through the office's own Buy, the bond or note for a gap included. The office drafts the picked ground as offers that
are never listed, saved or seen by the city's own buying; an offer the purchase bites into leaves the shelf and its
place is listed again; an offer wholly picked is bought as itself at its listed price; the 24 offers stay as the
office's suggestions. No offer is drawn over the city's ground any more: the globe painted each offer's whole
rectangle, and now draws its free ground as the plane does, and every offer is edged along its own ground. The picks
now survive the funding page. LandCheck 29 and MapCheck 14e-14f new. The default playtest's trace is byte-identical.

### 0.8.52 — MS1, the expanded map: its head, a drag that selects, picks that last, the near view anywhere — 2026-10-26, ready, see docs/queue/MS1-notes.md
The expanded map lies under the window's header, so population, GDP, the cash, the rate, the currency, inflation and
the clock stay in view and live. A left drag from an offer's free ground boxes offers; from anywhere else it pans, a
right or middle drag always pans, and the cursor says which (★, the recommendation). The picks no longer drop on a
month: each is keyed to its ground, carried onto the offer now listed there, and dropped only when the city bought it or
nothing offers it, the head saying which. On Earth the wheel reaches the closest scale anywhere (past the reach the far
rule draws it), and the "wall" - the pan stopped 300 km from the city at the near view - is gone. GlobeCheck 2b and
MapCheck 14b-14d new. The default playtest's trace is byte-identical (no ensemble).

### 0.8.51 — ET5, landmasses and the island rule — 2026-10-26, ready, see docs/queue/ET5-notes.md
A city's ground is counted by landmass, from earth-2's layer (a dry plot on a no-land cell takes the nearest land
cell's, within 3.7 km). Ground on a landmass no port links is owned and priced as ever but is no room: the free ground,
zones, pickers and investors read the linked ground alone, no works stands on a field across the water, and only a sea
terminal may be built there, on the island's shore by the port card's chooser. Two standing terminals, one on a linked
landmass, link it; ET6's bridges fill the closure's slot. Where each terminal stands is saved (SAVE_FORMAT 47, an older
Earth save refused). The map hides unlinked ground from districts, plans, runs and CL5's pieces, and hatches it; the Land
tab's LANDMASSES table, the ISLAND tag, a notice and the words follow. LandmassCheck new. The default, Rotterdam and
Medellín traces are byte-identical (no ensemble): both Earth cities own nothing off home at month 4,005.

### TM2 — the ship gate's timing waits for a quiet minute — 2026-10-25, ready, no tag (tooling), see docs/queue/TM2-notes.md
`--timing` started its hard run at the first look that found no other
batch's JVM, and a runner that starts JVMs one after another leaves gaps a
look can fall in. Now `tools/jvms.ps1 -Quiet`, which both scripts call on the
PC (the same loop in sh elsewhere), waits for four empty looks in a row, 15 s
apart; a busy look starts the count over; at 30 minutes it runs hard all the
same and says so. The game is said, not waited for (`CBS_QUIET_GAME=1` waits).
`Batch check.bat` refuses an unknown `--option`; `agent.py` times a harness
soft beside other runs. `tools/quiet-selftest.sh` (new) proves it against
real JVMs with gaps, 17 of 17; a real gate beside a loop of JVMs waited it out.

### 0.8.50 — FW10, the full-screen chart's foot stays on the screen — 2026-10-25, ready, see docs/queue/FW10-notes.md
City History's chart in full screen, with many lines picked, ran past the
window's foot: `TimeChart.heightBesidePlot()` counted the toolbar as one row of
34 px, where a row is 27 and each row the legend wraps to adds about 29, so from
three rows the overview strip and the caption were cut (at 1280x720, 16 lines
ended 43 px below the window). The chart now measures what stands beside its
plot at the width it is drawn at, and `fitTo()` gives the plot what is left.
ChartFitCheck lays it out at four window sizes with 2 to 16 lines: red on
main's sizing (23 FAIL), green on the fix. Renders in `docs/queue/FW10/`. The
model is untouched; no ensemble.

### 0.8.49 — FW9, ScaleCheck's copy tracks its city — 2026-10-25, ready, see docs/queue/FW9-notes.md
ScaleCheck's copies lost their people: a 1,000-times copy of the playtest's
town lost 1.3% of them in its first month and 6.1% in six, and its treasury
went negative. The founding endowments were one founded city's: the 100
household places, the founding care and police, the grid and the wells. A city
now holds a saved count of them, 1 for every founded city, and `scale()` gives
its copy K; at 1 nothing moves, and the trace is byte-identical. The scaler now
scales the bond market, the exchange, the register of shares, the fund, the
bank's records and about fifty more keys. The households' shares and bonds stay
per household, and the registers still add up. ScaleCheck 9 holds the copy to K
times its town within 1.5% for six months (red on main's scaler, 5 FAIL).
OrderSearchCheck's twin trims at a 40% policy rate. No ensemble.

### 0.8.48 — FW8, WHAT IS BITING flags a zone short of what it wants — 2026-10-25, ready, see docs/queue/FW8-notes.md
The Policy hub's WHAT IS BITING had no zoning flag. Now a zone short of what
it wants raises one: the zones together want more than the free ground, so
each holds the same share of its want and nothing is free for anyone else.
"Agriculture's zone holds less than it wants" ("It holds 78.5 km² of the 90
km² it wants"), or "2 zones hold less than they want", with a door to the
zone's page or to Zoning's The ground. The figures and words are the model's
(`ZoningView.shortfall()`); the hub is not drawn kept. ZoningViewCheck 9
makes one zone and then two short and asserts the flag, on the hub's own list
too, and none for a zone with enough or for no zone. The map's zone tint was
I14's (0.8.0), so nothing is left undone. The month is untouched: no trace
moved, no ensemble.

### FW7 — a known red told from a NEW one — 2026-10-25, ready, no tag (tooling), see docs/queue/FW7-notes.md
`AllChecks -q` once showed a second red inside a harness that was already
red (HealthCheck's upkeep check) as only "HealthCheck FAILED". Now
`AllChecks -q` and `-f` read main's record of its reds (`docs/notes/gate.txt`,
which BP1 began and only `ship.sh` writes) and say `known red` or `NEW red: N
of M red lines` of each failed harness, and `-f` tags every red line `[known]`
or `[NEW]`. `batch-check.sh` hands AllChecks the record it judges against,
fails a run where the two judges count differently, and gains `--judge LOG`;
`ship.sh`'s gate line says `known red only` or `NEW red`. KnownRedsCheck (new)
causes both cases with a fixture harness and judges its logs through the
script. The model is untouched: no trace moved, no ensemble.

### 0.8.47 — ET4, highways and rail priced by slope — 2026-10-25, ready but MapCheck 8 (main's, FW11), see docs/queue/ET4-notes.md

A highway or a railway line (the Elevated Highway, the Rail Spur, the Freight Line) is now a **run**: its land comes from
every class of ground in the city's proportions, not the buildable pool alone, so a mountain city's highways use its steep
ground (`LandManager.Use.RUN`, `runsSqFt` saved, `runRoom()` keeping the buildings room). An order pays the **cuts,
viaducts and tunnels** its land needs at the city's mix (`RunWorks`: a highway's works $110 a m² on foothill, a $2,080
viaduct on steep, a $2,330 tunnel on very steep; a railway's 2.7 times), as builders' work owed on the site, in the
quote, Build's card and the Freight investor's cost. On the map a run on steep ground is a viaduct, on very steep a tunnel
(portals, a dashed line). New RunWorksCheck. A flat city reads a run as a building: the default trace and Rotterdam's
byte-identical (no ensemble); Medellín ends at 245,314 people (432,191), its Rail Spurs priced as runs (382,036 if not).

### 0.8.46 — FW6, a reloaded city plays its next month as the one that kept running — 2026-10-25, shipped, see docs/queue/FW6-notes.md
Measured first: every history series already matched to the bit 24 months
after a reload, but the save did not. Now saved and put back: each market's
month tally (the first investment pass after a load weighed every works on an
empty one), Quarrying's carry, the books' stock at close (the month after a
load counted no revaluation), both months' shares and the sudden stop's words.
Construction's materials drawn and the index's rent weight are read right
after a load. Two live-city figures went stale: the index booked an older
month's meals and luxuries when no counter sold, and the skip report added up
earlier skips. ReloadCheck (new) is red on main and green here. The trace is
byte-identical, so no ensemble was run.

### 0.8.45 — FW5, two reads that miss litres and one check that counts them twice — 2026-10-25, shipped, see docs/queue/FW5-notes.md
The month's draws are kept now. The railway's and its trucks' diesel, and
bitumen ordered before the strike, are drawn before the strike clears the
rows; `Markets.rowDrawsBeforeStrike()` puts them back on their sellers'
"sold at home" (the sellers' rows only, units only: the railway bills from
the buyers'). Every draw (the forecourts' petrol, the vans' diesel, the
railway's, cars, materials) now stays among the market's fills past the
clearing until `Markets.closeMonth()`, the last statement of the month. The
industry card's buyers sum to the refinery's sold at home. OilCheck 6's "L
shipped" no longer adds exportBound to exported. OilCheck 18 and 19 and
RefineryViewCheck 4 are red on main and green here. The playtest's trace is
byte-identical, so no ensemble was run.

### 0.8.44 — ET3, a price for each class of ground — 2026-10-25, ready but MapCheck 8 (main's, FW11), see docs/queue/ET3-notes.md

Each class of ground now has a price, by the **residual rule**: flat at the going price, foothill at the going price less its
pad's preparation (never under the base price of unwanted ground), steep and very steep at the base price, none dearer than flat.
The land office prices its offers by the ground they hold, a business pays for its plot class by class (buildings on flat first,
works on the steep first, a zone's share on each class), and the pickers rank offers by **buildable ground a dollar, preparation
counted**, so a cheap mountain loses to the valley floor. The books value a sector's land at each class's price, pro rata to the
city's split; the balance sheet's Land line has **note 11**, its land by class, and the equity's land row revalues by class
(`SectorMonth.landByRelief`, null in an older save). The land office and the offer card show the classes. New LandClassCheck. A
flat city is priced by the old expressions: the default trace and Rotterdam's byte-identical (no ensemble); Medellín ends at
432,191 people, hunger 0% (main 257,761, 50%), with 37.7 km² of idle steep bought alongside. Four calls under Pending as ET3-C.
### BP1 — the batch process, cheaper and quicker — 2026-10-25, ready, no tag (tooling and documents), see docs/queue/BP1-notes.md

A batch runs no baseline suite: `./batch-check.sh --pick` runs the harnesses that read what its diff changed (`tools/pick.sh`; a class most
harnesses read, Game.java and the like, left to the gate with LongPlaytest in its place), every run but `--timing` times soft, and the one full
suite is the gate's. A batch writes `{next}` and its records in `docs/queue/<id>-ship.md`; `ship.sh` sets the version (the last tag's patch + 1),
splices the changelog block and the queue rows, takes a conflict only in the generated docs from the branch, passes main's reds recorded in the new
`docs/notes/gate.txt` word for word (a new one only with `--red-ok` and Jerus's words), compares a timing red with main's own run that day, gates a
docs-only merge on `--fast`, pushes main and the tag at once, and its dry run now shows the branch's version; `tools/ship-selftest.sh` tests it with
no JVM. `tools/day.sh` is the orchestrator's morning and the hourly check-in's probe, `tools/wait.sh` a wait inside a turn. "How a batch is done"
moved from CLAUDE.md to `docs/batch.md`, with the fresh orchestrator each day, the screen-check session (slots 7-9), the orchestrator taking the
recommended ★ calls and the night's lessons. Built with ultracode workflows, 58 agents; no game file; five calls under Pending as BP1-C.

### 0.8.43 — ET2, buildable by slope — 2026-10-24, ready but MapCheck 8 (main's, FW11), see docs/queue/ET2-notes.md

Buildings now stand on **flat and foothill ground only**; steep and very steep are refused them, and mines, quarries and wells
take the steep first, then foothill, then flat (one land pool, the works' part and the foothill prepared saved beside it). A
building on foothill pays for its **pad's cut, fill and wall as builders' work**: about $110 a m² at founding prices (a 19%
grade, $20 a m³ of earth, $430 a m² of wall), priced in points on the House's price a point, in the quote, owed on the site
before its first building finishes and billed to Construction (a House pad 3.1 points beside its 10). On the map buildings and
streets keep off steep ground and ice, flat cells first; the founding snaps to buildable ground and refuses a spot with none,
naming the nearest. Earth cities saved before are refused (the Earth floor at format 46). New SlopeCheck; the default trace and
Rotterdam's byte-identical (no ensemble); Medellín ends at 257,761 people (main 278,258). Five calls under Pending as ET2-C.
### 0.8.42 — FW4, a young auto-built city measured, and PlanCheck's save section traced — 2026-10-24, ready but MapCheck 8 (main's, FW11), see docs/queue/FW4-notes.md

Automatic building in a young city is measured: the autobuild playtest over 40 seeds to month 1,000 ends 66,270 to 209,195
people, and none stalls (0.7.89's single seed at 2,626 was RD4's dropped tree; 0.7.101's P2 cured the cause, a refused order
holding its whole measure at a 60% limit). **AutoBuildCheck 7** now holds it: a young city at month 60, its limit pinned and
its cash $0.28M short, refuses a Small Childcare Centre, walks on to the 11 measures ranked after it, and with the limit
back at the default orders it a month later and grows (1,704 to 2,175 people). `PlanCheck <save>` section 9's red is FX7's
as-built rule, not the plan's: 233 boxes in 9 of 25 districts of the playtest's month-4,000 city touch a highway laid after
they stood (87 under it, 146 on its verge); every placement keeps the verge. Left red for his call, written into MF1 and
FW4-C. No model change: the traces cannot move (no ensemble).

### 0.8.41 — ET1, Earth 2 in the game, and drawn — 2026-10-24, ready but MapCheck 8 (main's, FW11), see docs/queue/ET1-notes.md

The real Earth is now **earth-2.bin** (the 15″ release, 281,761,695 bytes, its SHA-256 the one in both `earth-2.sha256`; the
study's 135,965,097 was the superseded 30″ bake), and earth-1's ten layers read from it byte for byte (52.6 MB compared). A
plot now has a **relief** beside its ground, flat, foothill, steep or very steep, drawn from the slope shares (Medellín
20.7/42.5/31.2/5.6% against the data's 19.7/43.9/31.1/5.3), and the sea of 30 m or less is **SHALLOW**, which every sea rule
reads as sea. The map paints slope's greys, forest shaded toward them, snow over all and the shallow blue, with legend, hover,
far views and the globe. Depth is GEBCO's, so E4's shelf guess is retired (off Rotterdam 22/31 m at 40/80 km, not 69/150). The
founding card shows the relief, the shallow water and the landmass, and each holding keeps its ground by relief under keys of
its own (format 45 unchanged; earth-1 cities are refused by identity). No rule uses relief yet (ET2). Both playtests' traces
are byte-identical to main's (no ensemble); three calls for Jerus under Pending as ET1-C.

### 0.8.40 — FW3, a river's far bank surfaced after the hub's side — 2026-10-24, ready, see docs/queue/FW3-notes.md

A district cut by a river no bridge crosses spread its road over the streets on both banks, by cell rank, so the
far bank's streets, which are not on the city's network, were paved while streets on the hub's side were left
tracks (fixN3 §7's "counts its whole grid", outlived by the tile deal it was found in). The plan now marks its
parted streets before the surface (`markParted()` reads the streets themselves) and surfaces the network first,
half width then full, the parted streets after in the same order, then the one-sided seams. A plan with none
parted, or all, is surfaced as before. PlanCheck section 16 lays a 16-plot river across a root district: road
short of the network goes on the network alone, and the far bank is dealt only what the network leaves (red on
main: 883 plots of 5,293 beyond the river). The map's alone; the trace unmoved.

### 0.8.39 — PT1-M4, the works department retired — 2026-10-24, ready but MapCheck 8 (FW11), see docs/queue/PT1-M4-notes.md

Construction was paid for 400 points a month nobody worked (PT1's month 183: a 72% margin with 0 workers; Jerus: *"not
meant ... perhaps a starting depot or something instead"*). The free points are gone: the builders' capacity is their yards'
and depots' nameplate, worked at the posts they keep on and their fill. Every city founds with a **Builders' Yard** (a new
template, id 143: the Construction Depot at two fifths, 20 posts, 160 points), because a depot's 37 laid-off posts, counted as
spoken for, left a hands-off village no hands for a shop in 240 months; and the builders' last yard or depot is never sold.
The default playtest moved: 8,601 / 44,440 / 765,819 people near m600 / m1060 / the end (main 3,934 / 16,288 / 625,815),
prices 3.2 / 9.8 / 712 (8.2 / 62 / 13,374), the village's homes by month 17 where main's stood by 5. The ensemble passes 23
of 25 against 0.8.29's 24 of 26, no new red (A2 and S1 better, H2 vacuous). Twenty-three harnesses re-caused and T1-C's
trap retired with the free points; MapCheck 8 is red on a street remnant walled in by houses, FW11's case (PT1-M4-C). Format 45.

### 0.8.38 — FW2, the bank's sheet and the books' sheet the same after a load — 2026-10-24, ready, see docs/queue/FW2-notes.md

After a load the bank's sheet re-read its deposits off the city as it stood after the month (F-S1-1), and every
sector's sheet showed its debt as one line, its split by kind and when it falls due not saved (F-S1-2's load half;
the live half was fixed in 0.7.102). The bank's deposit book, R1 and R2 for both months, and R7's running totals
are saved and put back, so each sheet reads after a load what it read before, to the cent, and the debt schedule
is counted the first month after. SectorStatementCheck section 6 now loads through `loadGameSave()` (it read the
save into an unbuilt game, which had no bank branch) and compares every line. No format bump; the trace unmoved.

### 0.8.37 — FW1, a refused load leaves the city in play alone — 2026-10-24, ready, see docs/queue/FW1-notes.md

`loadGameSave()` rebuilt the world before it read the save, so a save it then refused (below the format floor, from a
newer build, on a missing Earth, empty or damaged) replaced the city in play with a blank one though nothing loaded:
reachable from the pause menu by code only, the load list's button being off for such a slot. The refusals are now
asked first (`Game.refusalBeforeRebuild()`, the checks themselves one method, `refusal()`, which `loadGame()` asks too),
and a refused load returns before `buildWorld()`. A newer build's save is now refused in words naming the build that
wrote it and opens it (`GameVersion.fromNewerBuild()`), as an older one's are. SaveFileCheck's new section loads both
into a running city: same managers, month and cash, and its save key for key the same; red on main, green here. The
month is untouched; the default playtest's trace is byte-identical to main's, so no ensemble.

### 0.8.36 — CD6, the rest of the debt's screens — 2026-10-23, ready, see docs/queue/CD6-notes.md

The last of the debt study's screens (spec-city-debt §7, boards 1, 2 and 5; D31). Finances › The book is a desk: the book's
figures, every line on today's curve, face, carried and worth by kind, and a row a line with its coupon, issue yield, price,
yield, duration and holders, and what closing it now would book in green or red, in D$ and per cent, with Close it now beside
it (Jerus: *"have it so that it shows green or red, and a % value and dollar value ... make it juicy to buyback"*). A line's
page draws its price since the sale against what it is carried at, its next six months on the books, its sales and the honest
line; Your rate draws the curve today, a year and two years ago, the dial the market expects, the 10-year rate taken apart and
real rates. The coupon is labelled cash next month beside the budget's interest, the Government grid now adds up to the
budget's line, a linker's last-month uplift is no longer "coupon", and Close it now's figures come from the model (DeskCheck).
The month is untouched: the default playtest's trace is byte-identical, so the ensemble was not run.

### 0.8.35 — CD5, floating and linked paper — 2026-10-23, ready, see docs/queue/CD5-notes.md

The city can sell two more kinds of paper at its auction (spec-city-debt §2.4, §2.5, §5.3; D25, D31). A floating-rate
note at 2 or 5 years pays each month's policy rate plus a margin fixed at the sale, so it sells near 100 and the city
carries the dial's risk; its effective rate resets with the coupon. An inflation-linked bond at 10 or 30 years pays a
real coupon on a principal that follows the price index and repays the indexed face, never under the original (the
deflation floor); each indexation is booked as "Inflation uplift on linked bonds", an expense paid in cash only at
maturity. Both are priced, bought back and valued on the dial and the index the market expects, and roll into their own
kind. Borrow has Floating and Linked tiles with today's fair margin and real coupon; Finances and the Government tab
carry the uplift. The money closes (FloatLinkCheck); nothing sells them unasked, so the default playtest's trace is
byte-identical and the ensemble was not run.

### 0.8.34 — PT1-M3, the advice card says what automatic building holds back — 2026-10-23, ready, see docs/queue/PT1-M3-notes.md

WHAT WOULD HELP MOST offered a Middle School and a Police Station that automatic building held back, and only the inbox
said why (Jerus, 2026-10-12: "yes do rec"). A card whose building the last pass held back as not paying back now carries
that verdict under it, in amber, in the inbox's own words: "Automatic building holds it back; it would not pay back: the
6,720 residents it would cover over its 20 years pay the city $914.63 a month each and cost it $3,821 each, which leaves
nothing for the $382,036 a month it runs at." The line is the model's (AutoBuilder.heldBackWords()), kept for every
building the pass passed over and saved with its state; neither rule moves. AutoBuildCheck 6 causes the disagreement.
The default playtest's trace is byte-identical, so the ensemble was not run.

### 0.8.33 — CD4, closing early — 2026-10-23, ready, see docs/queue/CD4-notes.md

The city closes its paper early as a treasury does (spec-city-debt §6, §2.6): a buyback is a reverse auction in which each
holder offers its paper in slices at the price where its own rule stops wanting it, never under fair; the bank offers at
fair (D34), the central bank does not offer (D18), and the city takes the cheapest and pays each its own price (D19), any
part of a line. Bonds of 10 years and more may be sold callable (D22), the option priced on a Ho-Lee tree fitted to the
curve at the city's rate volatility, floored at 0.75 points (D23), and called whole at 100 from year 5 or 10; "worth
calling" past 3% of face (D24). Finances' Close it now shows the offers as a ladder, the ticket, the three comparisons,
the journal and the call card; Borrow has a callable switch. CD3's ★5: History keeps the gain on debt retired, so the
year's "Paid out" and monthOfSpending no longer net it into spending. The money closes (BuybackCheck, CallCheck); the
default playtest never buys back or calls, so its trace does not move and the ensemble was not run.

### 0.8.32 — PT1-M2, the wait on ground told again — 2026-10-23, ready, see docs/queue/PT1-M2-notes.md

Jerus's call on PT1's M2 ("a notice repeated"): the inbox's "Your businesses have nowhere to build" is told again each
year the wait lasts, a new notice, dated and toasted, naming each sector waiting and the ground it asked for ("Real
Estate: needs 743 m², 0 m² free"; it printed sector keys and no figures). A month with nobody asking settles it as
before, and a wait back inside the year reopens that year's notice rather than raising another. The rhythm is the
notice's own raised month, saved with the inbox: no new field, no format bump. "Go to the land office" quiets it a year,
not two. InboxCheck section 7 holds a city's ground at what is built for three years with automatic building on: told at
months 3, 15, 27 and 39 across a save and load, silent once the ground is bought. In the default playtest: 1,790 months
waiting, 178 notices; the trace byte-identical, so no ensemble.

---

### 0.8.31 — PT1, eleven fixes from a played city — 2026-10-23, ready, see docs/queue/PT1-notes.md

Ten of the eleven, the model's month untouched. A founded city's bank, standing before its first month opens it, reads
"opens with the first month" and is not in NEEDS YOU, and the Bank page's door to build one is gone till then; that door
lands on the bank's card in Build › Shops. A served share from five times what is asked prints as a multiple, "30×", not
"13333%". A chart of a series that never goes under zero never floors under it. A notice raised before a save does not
toast on load. The founding card's "Standard" and its chip are no longer cut; People's bridge says "room for 634 people";
the drawer's footnote closes NEEDS YOU. The log prints a dash for debt-to-GDP with no GDP, and "3.92%". The two money
signs are Jerus's convention, kept. The toast over a page's controls waits on Jerus (PT1-C). PlaytestFixCheck is new.

### 0.8.30 — CL5, highways that reach the whole city — 2026-10-23, ready, see docs/queue/CL5-notes.md

A highway net that stops no longer stops for good (CityRuns): when no arm can grow and no corridor line crosses the net, a
LINK searches the piece's dry ground from the net to the nearest open line (k districts from its hub), straightened into
straight and 45-degree legs it lays plot by plot; the line's two arms open where it arrives. Every piece of ground the
water parts that holds buildings (CityMap.MapPieces: 32-plot tiles, eight ways; a purchase only joins its new tiles)
grows its own net from its hub, its tile nearest the founding site; the model's plots are dealt by built ground, taken
back only when the model's fall, and a purchase that joins two pieces moves nothing. The sidecar is FORMAT 12. On the
default playtest's city at month 4,005: 301 of the model's 1,687 highway plots laid before, all 1,687 now, and the
district on the other island has a highway. IslandNetCheck (new) causes each case. The model's trace is main's but for
the map's own layout-watch counts (0 boxes moved), so no ensemble. Calls for Jerus: CL5-C (the railway on other pieces).
### 0.8.29 — CD3, the city's books — 2026-10-23, ready, see docs/queue/CD3-notes.md

The city's paper is carried at amortised cost by effective interest (spec-city-debt §5, D10, D16, D26): each line opens
at what its sale raised, solves the rate its payments are worth that at, books carrying x rate / 12 a month and closes to
nothing; a reopening blends the rate; dollar paper keeps its books in dollars, read at the month's D$ (not the goods'
struck rate). The budget's "Interest on the city's paper" is the coupons and the discount wearing off; the gain or loss on
debt retired is against carrying value; the treasury's bridge has a not-cash row, so it still foots. The screens show it:
the bridge's steps, the debt card's two parts, the balance at carrying value with the fair value as a note, carried and
effective on the book. The money closes: the new figures are no cash, and MoneyCheck holds the interest less the
amortisation equal to the cash coupons, holder by holder, every month. TreasuryCheck 9 (new, 113 assertions). The bank's
straight-line accretion is unchanged. SAVE_FORMAT 44 and its floor. Ensemble 24/26, 0.8.21's score and its two reds (A2,
S1). Calls for Jerus: CD3-C.

### 0.8.28 — UP2, the clock keeps its frame — 2026-10-23, ready, see docs/queue/UP2-notes.md

The window's clock is paced by what it costs (ClockPace, new): a redraw waits three times what the last cost, 120 ms at
least; a frame stops running months past 40 ms and drops the time it owed past one month instead of paying it back in a
burst; the speed reads "50× · 39×" when the city falls a tenth short of the rung. The header's tiles read only their last
ten years of history. The chrome is kept with ui/Kept (UP4's pattern, its §7): the header, the rail, the city panel, the
construction panel and the inbox's list set their figures on last month's nodes. At month 4,005, against main in the same
state of the PC: the chrome went from 11-40 ms of every redraw to 2-11, every screen's redraw is 4-34 ms cheaper (People
39 to 19), and at 50x People's frame p95 went from 42 ms to 26 (FRAME_P95_MS 34). KeptCheck 6-13 compare the kept chrome
with one built new, node by node and pixel by pixel, over 25 months; renders of the real window are the same pixels as
0.8.26's at months 1,000 and 4,000. ClockCheck (new) drives the pace on a clock of its own. UiProbe times the pulse's own
passes. The playtest's trace is 0.8.27's but for its run-time lines, so no ensemble. Calls for Jerus: UP2-C.

### Docs pass 2026-10-23 — over AN1, XR2, FX8, FX7, UP1, UP4 and UP3 — shipped, no tag

The daily docs pass by a fresh session over the seven batches since the 2026-10-22 pass (0ceac7b6..9f6ece08).
Comments, indexes and the record only. UP1's skip comments said a skip leaves its files "as they were before" UP1;
they now say what the floor does: the "before skipping" autosave starts it again, so a skip inside 30 s ends with that
one on disk (UP1-C star 4). GameVersion's history had no 0.8.23: FX8's entry is written. ForeignAccounts' header names
the regimes; Expectations.pegBroke() is priced by the same month's takeMonth(), not the next; GoodsMarket's and Markets'
rate is the D$ the goods are struck at (AN1); ui/Kept said 0.8.25 for UP4's 0.8.26; PeopleScreen's and Pieces' headers
say the People page is kept; ten constants got their sentence. docs/tree.md: 129 harnesses, Game's 45 banners, FX7's
dated runs, UP3's kept nodes, ui/Kept and KeptBench. ui/UserInterface.java was left for tomorrow's pass (UP2 is in it).
Suite on its tree 130 of 130 in 892 s, beside another batch's JVM (MapCheck 1's timing SOFT, not counted); Stale 0 firm.

### 0.8.27 — UP3, the map's month redraws only what moved — 2026-10-23, ready, see docs/queue/UP3-notes.md

Measured first on copies of Jerus's city grown a month at a time (x 1; x 80, 125 districts to his 115; 5B): a month moves 1-2
districts of 125, yet the minimap's stamp and the far view were redrawn whole. A far node now keeps its land, districts
and runs as layers (MapTiles.KeptNode), each redrawn only when its key moved, and composes the same pixels as the full
redraw: the stamp fell from 2.3-6.5 ms a month to 0.15-0.8, and is begun at most every 250 ms (STAMP_EVERY_MS), four a
second at 50x. The far view re-colours a node only when its nodeKey() moved: x 1's screen 12.0 to 2.8 ms, 5B's 29.6 to
18.2. A link's stamp is worked out once. MapCheck 18 (new) compares the kept pictures with a full redraw over 73 months,
9.25 million pixels, all equal. Left (UP3-C): the chain's early exit (at x 80 a month re-plans all 121 pictures, 23-28
ms of the FX thread and 1.5 s of the worker) and the L0 screen's band check (32 ms at 5B). The trace is 0.8.26's, so no ensemble.

### 0.8.26 — UP4, the People page kept — 2026-10-23, ready, see docs/studies/spec-screens-in-place.md

The study UP4 timed every rail screen's redraw at month 4,000 through the pulse's own passes: 29-68 ms, the chrome
12-27 of it, while 90-99% of a page's nodes show the same thing month to month. Its pattern, ui/Kept, runs a screen's
own drawing code over the nodes it made last month (by scope and order), its boxes setting their children in one call
where they differ, so nothing kept is styled, measured or synced again and the scroll never moves. The People page is
the pilot (keepMenu, drawPage, the Pieces helpers it reaches; Mirror, Strip, Pair, SegmentBar and Waterfall told their
figures): at month 4,101 its redraw went from 53.0 to 32.1 ms (the page's part 34.7 to 12.8; the rest is the chrome),
and at 50x the FX thread from 50% to 41% busy, frames p95 51 to 20 ms. Its renders are the same pixels before and after.
KeptCheck (new, with ui/KeptBench; no window, skipped where JavaFX cannot start) draws the page kept and new for 25
months and compares them node by node and picture by picture. The rest is KP1-KP7 after UP2, which takes the chrome
(Pending UP4-C). No model change: the playtest's trace is 0.8.25's but for its run-time lines, so no ensemble.

### 0.8.25 — UP1, the autosave off the clock — 2026-10-23, ready, see docs/queue/UP1-notes.md

The autosave held the window's thread 170 ms every 12 months at month 4,000 (31% of it at 50x), the history two
thirds of it. Now the save is filled on the FX thread and its snapshot written on one writer thread (SaveWriter)
while the months go on: the autosave costs the clock 4-5 ms at months 1,000 to 4,000 (it was 50-176 ms), and its
share of the FX thread at 50x is 0.0%. The save holds no live object (the setters copy: the inbox, the build log,
the bond market, the exchange, the construction control, the fund, the bank's preferred), the history is written
compact and streamed, and an autosave comes at most once in 30 s of real time (Jerus's call). A load, a numbered
save, a skip and Quit wait for the writer. ui/UiProbe is the window's probe, run by hand. SaveSlotCheck 7c new: the
background files equal the old way's exactly. No save-format change; the trace is byte-identical, so no ensemble.

### 0.8.24 — FX7, the runs dated — 2026-10-22, ready, see docs/queue/FX7-notes.md

LongPlaytest's one red since AN1, "an old works moved", was the map's: a district's plan replayed its whole history on
the city's runs as they stood today, so when the runs laid something across built ground (the founding district's Rail
Depot yard came and went a dozen times from m1,128, a highway arm from m2,131) or took it away, every arrival was
placed again and 100-260 of its ~300 boxes moved. Now the runs are dated as the land is: the map journals each plot
whose highway, track or yard kind changed, month by month, and keeps it in the sidecar (FORMAT 11); a plan replays its
history on the runs of each month, a box under a run laid later stays where it stands (both are drawn), and
ground a run leaves is room again. The playtest's watch: 14 of 14 old works stand, no finding. MapCheck 2d and PlanCheck
15 new. The trace is byte-identical, so no ensemble. Interchange reserves stay undated; Jerus's calls in the notes.

### 0.8.23 — FX8, XR2's loose ends off the default path — 2026-10-22, ready, see docs/queue/FX8-notes.md

Five things the orchestrator's review of XR2 found, none on the default path. The central bank keeps nothing, restated:
equity less the vault, plus what it paid in new money for the vault's dollars (the strong edge's default payer), is the
remittance due less the loss carried; the old statement was false by that term under a peg or a band, and never asserted
under one. Leaving a peg between two presses clears its doubt, so the hot money no longer leaves at the dead peg's hazard
for one more month. The hot money's month (arrived, departed, on doubt) is saved, so Policy's "of which ... left on doubt"
survives a load. Trade's "net bought into the vault" counts the central bank's edge purchases, as it counted its defence's
sales. HoldersCheck pins the capital-flows array at its 19 slots. CurrencyRegimeCheck 5, 9 and 11 restated and extended,
12 and 13 new, each red on 0.8.22's model. Under Lean all of it is off: the playtest's trace is unchanged but for its run
time, so no ensemble. Saved, no format bump.

### 0.8.22 — XR2, the currency's regimes — 2026-10-22, ready, see docs/queue/XR2-notes.md

Policy › Money › The currency chooses how the D$ is held (★3): Float; Lean against falls, XR1's float and the default;
a Band whose edges the vault holds, its centre crawling; or a Peg to the US$, held while the vault meets the month's whole
balance. Beside every regime runs the shadow rate, where the float would be. A peg breaks (★4) when the vault would fall
under a month of imports, or by doubt at a chance that rises past a 10% gap, landing at the shadow plus a quarter of the
gap; the hot money leaves at that chance, and credibility loses half its way to the floor. Under a peg the dial is tied to
the US's rate with a spread to defend (★5); the autopilot aims at inflation, the currency or a mix (★13); the strong
edge's dollars are paid by the central bank in new money or by the treasury (★6). Trade › The currency's seventh force is
the hold. Lean computes every figure it did: the playtest's trace is unchanged, so no ensemble. CurrencyRegimeCheck
(new): 1,000 seeded pegs break at their summed hazard, median life 7.5 years, median break 31% (the study's 7.4 and 31%).
Saved, no format bump. ★ for Jerus: does a band break; a float's currency aim; the dent's size (XR2-notes §4).

### 0.8.21 — AN1, the price level's anchor — 2026-10-22, ready, see docs/queue/AN1-notes.md

The default city no longer hyperinflates under XR1's float. The city's goods - the dock's imports and exports, the band
its own goods trade in, a building's materials - are struck at a D$ that follows the D$ slowly: a 36th of the gap a month
in logs (EconomyManager.PASS_THROUGH), half of a move in two years, the world's own level at once; the D$, its parity and
every conversion of money are XR1's. Measured first on the default playtest, 17 runs (AN1-notes §2): the lag anchors from
a 24th a month slower, not at a 12th, and not on the grocery shelf alone; a shelf that falls as readily as it rises does
not anchor and adds nothing beside it. The playtest: prices 8.2 / 62 / 13,374 times founding near m600 / m1060 / the end
(0.8.19 13.8 / 12,225 / 2.9e9; 0.8.18 4.7 / 15 / 1,036), 625,815 people (10,980; 573,349), the D$ never at its guard.
Saved (DataSave.struckRate), no format bump. PassThroughCheck (new); CurrencyCheck 4 restated to the lean as built
(XR1-C ★1, "Keep as built"); three fixtures re-caused. Ensemble 24/26 (0.8.19 18/25). ★ for Jerus: the dial's value, land at the D$
itself, the map's "an old works moved" in a city that grows again (AN1-notes §5).

### Docs pass 2026-10-22 — over FR4, EG1, CL2, CL3, CD1, FX6, CD2, FX5, TD1, XR1 and CL4 — shipped, no tag

The daily docs pass by a fresh session over the eleven batches since the 2026-10-19 pass (abfca4fb..01f9bf98).
Comments, indexes and the record only. FX5 was 0.8.15 in seven comments (its number before CD1, FX6 and CD2 merged
first) and CD2 0.8.15 in docs/tree.md: both are their shipped numbers. What CD2 retired outside the files it touched:
the term loan, the underwriting spread on the city's paper (Rollover, BondMarket, Game's issue costs, Founding,
DebtManager's curve); XR1's retired push in CentralBank's defence; FR4's strip and port dues door said as done.
docs/tree.md has entries for YieldCurve, FreightView, TradeBand, TrackCurves, StreetCurves, TempDirs and CapChips,
the reshaped map, Earth and plan files' batches, 126 harnesses; README counts 127 run and lists tools/ensemble/; the changelog's stray separator gone; the maps
regenerated. Suite on its tree 121 of 127: main's six reds, nothing else (CurrencyCheck 4, MonetaryCheck's
allowance, HealthCheck's hunger line, SectorBooksCheck's imports by good, CentralBankCheck's spiral, LongPlaytest).

### 0.8.20 — CL4, slanted avenues and curved streets — 2026-10-21, ready, see docs/queue/CL4-notes.md

Four avenues at 45 degrees run out from the lattice node nearest the founding site, laid by every district's plan as
arterials wherever they cross an open cell - as the cell opens, or as ground is bought later - and drawn as one smooth
band, with roundabouts where five roads meet; a lattice street meets one only 8 steps from the next junction. The
shore's streets are drawn as curves (FX5's TrackCurves), and past 15 km a third of the long-block homes cells are
crescents, their cross street a quarter circle. No building that stands moves: the playtest's layout watch moved 0 of
171,104 boxes. Two faults on main fixed in commits of their own: the near view threw at a depot with no track (FX5's,
0.8.18), and the interchanges' loops were never drawn at the near view in the game (FR7's, MapTiles.copy()). The map's
alone: the trace is 0.8.19's, no ensemble. ★ for Jerus: six calls (CL4-notes §2), above all ★2 and ★3.

### 0.8.19 — XR1, the D$ as a float that follows its parity — 2026-10-21, ready, see docs/queue/XR1-notes.md

**The default city now hyperinflates unless the player stops it** (Jerus: "ship it as is, im going to test it to see
if player led behaviour can prevent hyperinflation"). The D$ is a level now: parity's trend, on the basket with rent
held to a third, followed one for one; where the real rate (60% of a change at once, at most 35%), the year's balance
and the vault's cover hold it; and a seeded market of its own, about 8% a year. Buying dollars now cheapens the D$, at
2% per 1% of a year's GDP, and the central bank's defence leans against a month's new fall the same way. Trade › The
currency is the study's board 1. With the rate following prices and prices following the rate, the default playtest
ends at 2.9 billion times founding's prices with the D$ at its guard, 10,980 people (main: 1,036 times, 573,349);
Rotterdam 284,646 (main 761,080). Under the autopilot the ensemble's cities live (213,000 at the median) at 4.5% a
year: 18/25 against 0.8.17's 23/25. Save format 43; older saves are refused. ★ for Jerus: XR1-C, the anchor's leads.

### TD1, the checks' temp folders deleted when they finish — 2026-10-21, shipped, no tag

Every harness, `GameFiles.scratch` and `LongPlaytest` made its temp folders
with `Files.createTempDirectory` straight into %TEMP% and never removed one:
about 95 a suite run, 37 GB on the PC in a week, the disk nearly full. All 93
calls now go through the new `TempDirs.create(prefix)`, the same call into
`%TEMP%\citybuildersim-tmp\<pid>`, which a shutdown hook deletes when the JVM
exits; the next JVM's first call deletes any folder there whose process is gone
(a run killed hard). Deleting never follows a link or leaves that folder. The
model is untouched: no ensemble, no version. CLAUDE.md now forbids
`Files.createTempDirectory` in a harness, and has a session delete its
`%TEMP%\cbs-<id>*` folders once its batch ships (24 left behind held 5 GB).

### 0.8.18 — FX5, the map's rail and roads finished — 2026-10-21, ready, see docs/queue/FX5-notes.md

Rail curves at its corners and the trains follow them: a new `TrackCurves` rounds every route's turn (3 plots) and
the tiles draw the same arcs, with the yard ladders as S curves, so no car swings on the spot. Trains are their real
length (a unit train 80 wagons, 1.24 km), only onto a siding that holds them and no yard's standing wagons, one a
siding, easing in and out; yards are laid along their line as long as the longest train the ground allows (48 plots
for a unit train, 33, 31, 15), across a cell's ring street, and drawn whole across tiles. A station alone now lays its
main line (his Halifax depot had no rails). One junction a crossing, mostly signals (a roundabout only where five
roads meet); the diagonal highway joins the grid as one carriageway; the world lines' labels apart; "Track" is
"Unmade street". The map's alone: the trace is 0.8.17's, no ensemble; the map's sidecar FORMAT 10, no save change.
★ for Jerus: eleven calls (FX5-notes §2), above all ★3, ★5 and ★2.

### 0.8.17 — CD2, the lineup and the auction — 2026-10-21, ready, see docs/queue/CD2-notes.md

The city sells bills (1-12 months), bonds (2-50 years, the coupon the clearing yield rounded down to an eighth, in
lines that reopen) and the amortizing bond at auction (spec-city-debt §2, §4); the term loan and the 0.75% spread
go. The households bid first at the auction's yield out of their savings (D32), the firms by their own rule (D33),
the world as hot money, the bank its risk capital (★6); one clearing yield, capped at fair + a point or Fill it,
priced on the curve so no round trip makes money. The firms and the world hold what they buy. The rollover and
automatic building each sell at their own cap: a short rollover is paid from cash and the advances, a short build
sale keeps its ask open. Borrow is the auction's screen. The study's model, rerun with the firms first: the tight
city's tail 28 bp, not 503. Save format 42. Traces move (default 573,349 people against 619,699, Rotterdam 761,080
against 834,678; one domestic sale between them, at fair); ensemble 23/25 against 22/25, A2 green. AuctionCheck.

### 0.8.16 — FX6, a district that buys land inside itself keeps its buildings where they stand — 2026-10-21, ready, see docs/queue/FX6-notes.md

Land bought inside a district no longer deals its buildings out again. A district's plan read its owned ground as
of today, so a purchase replayed every arrival on the larger ground; on the playtest's city at month 3,998, 2,069
of district (-2,-2)'s 2,946 buildings moved. The plan's ground is now dated, each plot the month the city bought
it, from the land's own records (`Input.boughtAt`). The plan starts on the ground its first batch stands on and
adds the land bought before each arrival's month: what stands never moves, and new arrivals take the new ground.
LongPlaytest is green again (its layout watch: 0 moves in 48 month pairs). The map's alone: the trace is
byte-identical to 0.8.15's, so no ensemble; no save change. ★1 for Jerus: ground bought inside a cell already
open gets no new streets, so less of it is built on (FX6-C). Harness: MapCheck 2c, PlanCheck 12.

### 0.8.15 — CD1, the curve from the dial the market expects — 2026-10-21, shipped, see docs/queue/CD1-notes.md

The city's curve no longer sits on the dial in parallel (spec-city-debt §3, D9). The market expects the dial to drift
back to what the city's rule would set at the inflation it expects, over six months (DRIFT_MONTHS, D15); each month's
forward is that plus the credit spreads, the zero rate compounds them and carries the term premium less QE. A hiking
city's curve inverts, a dial held low by hand no longer buys cheap long money, and a calm city prices as before: the
study's four cities to the hundredth. Every payment of every piece is discounted at its own month's factor, so a
piece's yield is solved from its price; every issue is priced the same way (the serial's coupon at par on the curve,
the term loan's face on its factors), at home and abroad, and no round trip makes money. The short end is unchanged.
Your rate shows the thirty-year rate in its new parts and where the market expects the dial. No save-format bump.
The traces move (default 619,699 people against 677,355; Rotterdam 834,678 against 711,024); the ensemble scores
22/25, H1 retired and A2 shipped as noise at its threshold, by Jerus's word (2026-10-21). Harness: CurveCheck.

### 0.8.14 — CL3, buildings that stay where they were put — 2026-10-21, shipped, see docs/queue/CL3-notes.md

The map's alone; the playtest's trace is byte-identical. Each district keeps a log of its count changes month by
month, saved in the map's sidecar (FORMAT 9; an older one loads into today's plans, box for box), and its plan
places the buildings in the order they came: the first batch as before, then each month's departures (the newest
leave, their lots freed) and arrivals (a month's types dealt in turn), each at the first place it fits, a cell
opened for the use of what opens it. Farms, power and water works, yards, car plants and truck depots open cells
from the district's far side; homes keep off mines; a cell's squares and boulevard are decided as it opens. A
district is full at PLAN_FILL (0.68) of its plots, and what spills keeps its month. His 1.7M city over a year:
41 buildings moved against 5,080; the playtest's ring gone (industry 7.0 km from the founding site, homes 10.2,
where they stood at 9.8 and 7.5). The playtest's city, out of ground, now turns away its newest houses at the edge
rather than its works: 3,570 packed without a street against 450. PlanCheck 11, MapCheck's sidecar sections and
LongPlaytest's layout watch are new.

### 0.8.13 — CL2, streets not a grid inside the blocks — 2026-10-20, shipped, see docs/queue/CL2-notes.md

The map's alone. Each homes cell, long blocks or squared (a boulevard's interior too), takes one of three street
layouts by a hash of the world's seed and its place: the comb as before, dropped crossings (T junctions, bends,
merged blocks, now and then a small green hole), or brick (the cross streets at alternating lines), a third each
(Jerus, the CL1 study's star 1). A cell a highway, railway, yard or reserve crosses keeps the comb: varied pieces
joined along a 45-degree highway drew a staircase. A merge in a varied cell takes up the stubs it leaves or is not
made. On his 1.7M city: dead ends 155 and buildings placed 17,987, both as before; a squared cell's streets 27.5%
of its ground against 30.6%. At the city's scale it still reads as a grid (CL4 changes that). PlanCheck 10 is
new; the trace is byte-identical to 0.8.12's, so no ensemble (EN1).


### 0.8.12 — EG1, deposits where the geology says — 2026-10-20, shipped, see docs/queue/EG1-notes.md

On the real Earth the fields now lie where the geology puts them (spec-earth2-terrain §8.5), so Jerus's Nova Scotia
cities no longer sit in a ring of oil and iron. Oil is drawn by each province's known petroleum a km2, with a tenth
of its class's weight where none is known; 40 estimated hand rows fill the US provinces and the Scotian Shelf, which
earth-1's USGS figures leave at nought. Iron keeps the shields' weight, a tenth elsewhere, and twenty in 96 recorded
iron districts (the Pilbara to Wabana). The Sydney coalfield reads its basin. An Earth field is three times wider and
a third as many, and a cluster stays on its own province's ground. Within 150 km of Halifax: iron 21 fields (417
before), oil on land none (53). earth-1.bin is unchanged. A generated world is drawn to the bit as before: the default
trace is byte-identical; Rotterdam's moves from month 1,464, when its city buys an iron field (711,024 people at the
end against 588,872). Save format 41 refuses an Earth save only. Harness: EarthDepositsCheck.

### EN1 — the ensemble only when the trace moves, in the tree, with a stored baseline — 2026-10-20, shipped untagged, see docs/queue/EN1-notes.md

The 80-run ensemble (16 seeds x 5 monetary policies) now runs only for a batch whose playtest trace moves (Jerus: "yes, do
that"), once, against the newest stored score, and main is never rerun. Its runner, until now only in the workbench zip
and in sessions' scratchpads, is `tools/ensemble/`: the driver; `ensemble.sh`, which snapshots `target/classes` and runs
five at a time on the PC (one under the cloud's lock); and the scorer, which reads pandas from `~/.cache/cbs-ensemble/py`.
`docs/ensemble/0.8.9.md` holds the first stored score: FR6's final run, still main's model. The scorer had counted 26
of its 27 criteria (A3's verdict was numpy's bool). Two premises out of date are restated: A2 from m240, past the
founding's rent boom, and H2 against the twin, as H1 is. So 0.8.9 scores 25/27, and S1 and R3 wait on Jerus (EN1-C),
with R3's fix proposed as RN1. A harness, EnsembleCheck, keeps the driver compiling against the tree. No game change.

### 0.8.11 — FR4, the freight screens — 2026-10-20, shipped, see docs/queue/FR4-notes.md

The last freight batch draws what FR2-FR7 built. Build's "Roads & transit" is Infrastructure again and holds the
ports and the Strategic Reserve, out of Industry; "Rail" is Freight (the rungs, the Rail Depot, the Truck Depot);
a berth's card says when it pays back, from the advice's own verdict. Sectors › Freight opens on a division strip
(All in legs, Haulage, Rail, Ports) and its Income page lists the shippers by division; every works' Operations
page shows How its tonnes move (each good's legs by mode, the bill by division, what a siding would save; Iron &
Steel's pellet plant by truck and with a siding). Infrastructure's railway and freight pages are one Freight page,
with a Ports page beside it; Policy has the port dues dial with its preview; Trade draws its band in legs. The
manual's freight pages are brought to 0.8.11. New pure models FreightView and TradeBand with their harnesses; one
saved tally (each sector's moves, read by nothing in the model). The trace is byte-identical to 0.8.10's.

### Docs pass 2026-10-19 — over FX3, TM1, FR2, FR7, FR3, FR5, FR6 and FX4 — shipped, no tag

The daily docs pass by a fresh session over the eight batches since the 2026-10-18 pass (71cf051..4b27f49).
Comments and one log line's text only: the freight sector model's comments, Game's, the timing helper's and the
interface's; every harness header against its sections (NewGameCheck 15, RailDrawCheck 20-21, TruckRouteCheck's
run order); the map's comments for FR5-FR7 (the Rail Depot's fan, kerb corners and interchanges, trucks, the
legend's 52 entries); docs/tree.md's entries for FR2-FR7's files, 143 buildings, the Rail sector named; the maps
regenerated. Flagged for a code batch: `Freight.getKept()` may read low since FR6; the Rail build card still says
"Owned by the rail sector"; the plane legend's 52 entries against LEGEND_ROWS 17; MapCheck's trackSurplus() labels.
Suite 118 of 120 on its tree: ScaleCheck as on main, StaleCheck fixed in its last commit and green on the merge.

### 0.8.10 — FX4, a new city after a reformed one founds at unit 1 — 2026-10-19, shipped, see docs/queue/FX4-notes.md

Not a regression of the freight batches (0.8.4 does the same): the denomination was a final field that
`Game.buildWorld()`, New Game, the load path and the constructor share, never reset, so a city founded in a window
that had loaded a reformed city kept its unit and money constants, parity read 0.0000 from month 2 and the rate ran
from 1.00 to 0.04. `buildWorld()` now resets the denomination and re-seeds the fifteen money constants at unit 1
first (`seedMoneyConstants()`, the load path's list made one method). Diagnosed on scratch copies of his autosave
and slot 2, never written. NewGameCheck 15. Gate: 120 harnesses, 119 passed, ScaleCheck's "...and it builds" main's
red; the trace byte-identical.

### 0.8.9 — FR6, the railway carries the city's own bulk, and the port yard — 2026-10-19, shipped, see docs/queue/FR6-notes.md

Of every bulk tonne bought at home the siding's part (.75) is offered the railway after the boundary's trade, on
the room the track, sets, stations and sidings leave, billed "Rail freight" at the railway's quote; with a station
and a sea terminal standing, .45 of the berths' bulk rides the port yard quay to door. City tonnes burn a third of a
truck's diesel, join the revenue the quote is struck over and order no track. The trucks carry a third fewer city
legs in the default playtest, 71% fewer on the Earth; the Earth playtest's city ends 23.5% smaller (FR6-C). A reset
rail net clears its hub across a save. HomeRailCheck new. Gate: 120 run, 119 passed, ScaleCheck's line main's.

### 0.8.8 — FR5, trucks on the map — 2026-10-19, shipped, see docs/queue/FR5-notes.md

Haulage's loads drawn as trucks at the near view alone, while Settings' "Trucks on the map" is on (default on;
D21): `TruckSchedule` in TrainSchedule's shape, from Freight's saved tally of the month's truck tonnes by load
(import blue, export orange, empty grey, home violet), one truck per N loads past 1,000 a month; on the highways
from the world line and on FR7's access roads to each gate; `TruckShapes` the body and colour. The model gains one
tally nothing in it reads; the trace byte-identical. TruckCheck and TruckRouteCheck new. Suite: 119 run beside FR6,
117 passed - ScaleCheck's line, and StaleCheck's README harness count, put right.

### 0.8.7 — FR3, the landlord port and the Bulk Berth — 2026-10-19, shipped, see docs/queue/FR3-notes.md

The city builds and keeps the sea terminals; Freight's third division, Ports (`sectors/PortDivision.java`), works
them on its payroll, bills every shipper's sea tonnes "Port handling" at the kind's quote, and pays the city port
dues the same month (a revenue line, "Port dues"). The dues' default is the annuity on the kind's berths over 30
years at the city's own rate, times the new dial `TaxPolicy.portDues`. The Bulk Berth (id 142, 2.5 Mt a year,
D$60M), Handysize and Supramax calls; a berth is ordered when its dues would cover it. Found: every id now crosses a
save (an id past a gap, 142 before 141, was lost). LandlordPortCheck new. Suite: 117 run, 116 passed, ScaleCheck's
line main's.

### 0.8.6 — FR7, track that goes somewhere, and roads for two needs — 2026-10-19, shipped, see docs/queue/FR7-notes.md

From his Halifax city at 0.8.3: track to dead ends, gravel everywhere, highways nearly never. The Rail Depot (id
141, 50,000 t a month) is a railway's first order, a station before any spur or line; goods through a station only;
the overbuild while the wagon sets lagged was real and is gone. The highways carry the through trips and the freight
gates' trucks, the streets the rest: two measures, two NEEDS YOU rows; a gravel road's repairs rise past 200
vehicles a day. The map: gravel grey, track only on a train's way, interchanges' ground, access roads to every
yard, terminal and Truck Depot, kerb corners. RailDepotCheck new. Suite: 116 run, 115 passed; ScaleCheck's
ten-billion copy builds no home (FR7-C, his call).

### 0.8.5 — FR2, the Freight sector — 2026-10-18, shipped, see docs/queue/FR2-notes.md

Freight in Rail's slot of `Sectors.KEYS`, its railway the Rail division. Haulage, its trucks, bills the city leg of
every bulk tonne once, to its buyer or exporter, at a quote its own books set; the trucks (D$215k, from the Heavy
Truck Plant, id 140, or the world) sized to the plant at nameplate, kept in Truck Depots of 100 (id 139); a bulk
works short of them runs at .6 + .4 x the share they carry. Vans carry goods only. The playtests move from month 3
and pass every audit and reload. SAVE_FORMAT 40 and OLDEST_LOADABLE_FORMAT 40: older saves no longer load.
HaulageCheck new. Suite: 115 run, 113 passed - ScaleCheck's rents-pull lines and CopperRefiningCheck's Fabrication
Shop line (FR2-C, his review).

### TM1 — soft timing, two batches' checks at once — 2026-10-18, shipped untagged, see docs/queue/TM1-notes.md

`Timing` is the one helper every timing assertion goes through (21 in 8 harnesses): under `-Dcbs.timing=soft` a
figure over its budget says SOFT and is not counted, so two batches' suites run side by side on the PC. The check
scripts set it by themselves while another batch's JVM or the game runs (`tools/jvms.ps1`), and `--timing` waits
for the other batches and runs the timing harnesses alone and hard, as `ship.sh`'s gate now does. TimingCheck new.
No game change.

### 0.8.4 — FX3, the freight map's faults, again — 2026-10-18, shipped, see docs/queue/FX3-notes.md

From his eye check of 0.8.3. One yard layout read by both the drawing and the trains (`TileRaster.yardLayout()`):
the fan lies on the line's longest clear stretch, a line crossing the yard is drawn through its throat, and every
train comes in along the line onto a siding, no longer across the drawn ones. A 45-degree highway is one
carriageway with its centre line, not a stair of stacks; a road two plots wide is one road, so no signal or
roundabout on every plot. The map's alone; the trace byte-identical. RailDrawCheck 21, MainLineCheck 12,
JunctionCheck 10 and 11.

### 0.8.3 — FX2, the freight map's faults — 2026-10-18, shipped, see docs/queue/FX2-notes.md

From his eye check of 0.8.1 and 0.8.2 on his Halifax city. The line to the world starts where an arm's track ends
on dry ground at the city's edge and runs out over land: his gate arm waited at the harbour, so the line began on
open water and every train ran out to it and back, a V over the sea; corridor 2's north arm now carries it, and the
main line, station and trains' way run from that arm. A highway is drawn only once it crosses an arterial at a ramp
(the stub alone in a block is gone); a ramp mid-highway is no longer its end; the world line's label stays in view.
Slices by Sonnet 5.5 at xhigh, reviewed by Claude. No save change; the trace identical but its run-time line. Gate:
112 of 113, EarthTerrainCheck 8 and 9's timings, red at the baseline too with his game running.

### 0.8.2 — FR1, trains on the map, as the ships are — 2026-10-18, shipped, see docs/queue/FR1-notes.md

The railway tallies its railed tonnes by kind of cargo, landed and shipped (saved, never billed), and
`TrainSchedule`, in BoatSchedule's shape, turns them into trains: unit trains of 8,000 t, mixed of 1,500 t,
intermodal of 100 boxes both ways, past 30 a month each drawn train standing for several. Each runs from the world
line along the main line and corridors through a yard's ladder onto a siding, stands and runs back out, a
locomotive at each end; drawn from L1 in, each wagon's mark from 8 px a plot, in the ships' four colours. The
freight station is a container yard when the railway moved boxes. Found: in a steel city the mixed train is the
common one. No DeepSeek key on the PC: Claude wrote it, the last slice by Sonnet 5.5. Playtests byte-identical.
Gate: 113 of 113, earth-1.bin named.

### Docs pass 2026-10-18 — over GS1, RV1, DeepSeek and FR0 — shipped, no tag

The docs pass by a fresh session over 5a7a66b..b0c777c. Comments only: TileRaster's header and banners for yards
and junctions, BuildingVisual's TRANSPORT class, MapView's world line and legend rows, CityRuns' gateway and main
line, CityMap's sidecar FORMAT 8, Gateways' WHY; three harness headers; docs/tree.md's 112 harnesses, README's
tools/deepseek/; 0.8.1's record into docs/notes/pending.md. Flagged: tools.Stale does not see a record's
components; MapCheck 1, 8 and 14 print joined titles, so docs/harnesses.md shows no heading for them; TileRaster's
RAIL_DASH unread since 0.8.1. Gate: 113 of 113, earth-1.bin named.

### 0.8.1 — FR0, the map's network, the first freight batch — 2026-10-18, shipped, see docs/queue/FR0-notes.md

The land's gateway: the bearing with the longest run of land from the founding site (on the Earth, toward the
nearest larger place along land), none for an island founding; each net's spine laid toward it, its arm grown
first, and the world's highway and line drawn on past the city's ground, "to the world". The main line from the
rail gateway to the freight station, double with a Freight Line. Rail drawn as rail (ballast, sleepers, two rails;
level crossings; an arterial over the main line a bridge); yards as fans of eight sidings with a shed, ore or boxes
and wagons, in an eleventh class, TRANSPORT; junctions by rule (signals, roundabouts, diamonds, stacks). Slices by
DeepSeek V4 Pro through tools/deepseek, reviewed by Claude. Found: half of twelve generated worlds' default sites
have no gateway. Map sidecar FORMAT 8; no save change (39); playtests byte-identical. Gate: 113 of 113 on the PC.

### 0.8.0 — the merges after I14: the other assistants and the tools — 2026-10-13 to 2026-10-17, no version bump

MapCheck's bounds read on the wall clock where the thread's CPU clock is too coarse (Windows); AllChecks -f writes
and reads its logs in UTF-8; ship.sh takes its merge back when main moved on origin during the checks. ChatGPT and
Gemini given briefs of their own (openai-dontread.md, gemini-notes.md), their work on branches, never main:
ChatGPT's current-code handoffs for R0 (docs/studies/research-1900.md) and CD1-H (spec-city-debt.md 8.1); Gemini's
GS1, ten of Stale's soft findings, six corrected in review, and the Prose fixes rules it taught. RV1: tools.Names
(the names a branch's new text uses that the tree does not declare, run by ship.sh on their branches) and the
assistant-review subagent. DeepSeek as the batch's coder (Jerus, 2026-10-17): tools/deepseek/agent.py and
docs/deepseek.md, CLAUDE.md's step 3; tried on an FR0 slice (6fd8dda, reverted, then restored as FR0's slice G).

### 0.8.0 — I14, the release that finishes industry: the materials on the map — 2026-10-13, shipped, see docs/queue/I14-notes/fixI14-notes.md

The oil chain (0.7.76-0.7.98) and the five materials chains (0.7.110-0.7.143) are whole, and now drawn as
themselves: every type of iron, oil, coal, stone and copper stands on its resource's sites by its type of field, on
as many as it takes (a Large Open-Pit Mine on twenty hematite sites, counted once) - coal's, stone's and copper's
first types had been placed nowhere. Each kind has a look of its own (open pits in the ore's colour, a shaft,
quarries, a leach plant, the wash plant, camps, planted forest), each processing industry's works one campus in its
colours (the arc furnaces keep their places), and a zone's districts are tinted in its sector's colour. Found: a
footprint of a tile's 32 plots threw the district's plan; capped at a cell's 31. MapCheck 15 holds it. Sidecar
FORMAT 7; no save change; playtests byte-identical. Gate: BuildMenuCheck 139, Stale 0 firm, MapCheck and PlanCheck
green; EarthTerrainCheck 8's Earth screen bound red, as on main on the same machine.

### 0.7.143 — I12, the materials pictograms drawn — 2026-10-13, shipped, see docs/queue/I12-notes/fixI12-notes.md

The nine materials industries' Operations pages open on their month as a picture, the refinery's pictogram made
general (spec-materials 2.13): a mine's fields as vessels filled to what is left, or a works' feeds by where they
came from, imports hatched; the works in ranks, each with its run and in a word the gate one more would fail; the
tank of what left the works under the industry's roof; who took it; the power and the month's note - every ribbon
one scale, in tonnes, the forest's in cubic metres. New model class IndustryPicture (pure), painted by
ui/SectorScreen with the refinery's painter. IndustryViewCheck 12 holds it: ribbons to scale, every label's figure
the view's, nothing off the frame at 1,389 x 868. A words probe and nine headless renders are in
docs/queue/I12-notes/. No save change. Gate: suite 109 of 109, BuildMenuCheck 139, Stale 0 firm, playtests
byte-identical.
