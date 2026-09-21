# The design notes, by subsystem

A map of the `claude/*.md` documents in this project, grouped by what they are about, one line each. Written 2026-09-18 for the structure audit, from the titles and the changelog's blocks; the line is a pointer, not a summary - read the note. Newer notes are usually the truer ones: where two disagree, the later date wins, and the changelog says which shipped.

The three files a session needs first are `todo.md` (what is open), `changelog.md` (what shipped, newest first) and this one. The published manual (artifact "CityBuilderSim") is the model written out as a whole.

## Start here

What a session reads first, and the records of the whole.

- `todo.md` - the list of what is open, by section: this week, onboarding, the rebalance, what a new player hits, engine truths, healthcare, housekeeping, the store, not doing
- `changelog.md` - every batch as it was written up the day it landed, newest first; the top block is the state of the tree
- `splitting-the-interface.md` - the plan for taking UserInterface.java apart into a ui package one screen per class, measured on the file, and the record of each batch as it landed - done 2026-09-18, twelve screen classes and the toolkit; §13 is the docs pass, a fresh-context agent that runs at the end of every batch
- `the-prose-that-stopped-being-true.md` - tools.Stale and StaleCheck, 2026-09-18: the mechanical half of the docs pass as a tool; what is firm, what is soft, the false positives tuned out, and the 84 dead javadocs the first run found
- `splitting-game.md` - four mechanics out of Game.java (Motoring, LuxuryCounter, Offending, CityBasket), 2026-09-18, playtest byte-identical; and the two-agent experiment - implementer and docs pass - with what it cost and what it needs
- `the-ai-ergonomics-audit.md` - 2026-09-18: how the repository and these docs are arranged for an AI to work in, what was changed (the generated indexes, CLAUDE.md, this split) and what is recommended next
- `handoff-after-the-sector-template.md` - the cloud working loop written out for a session that starts cold: staging, build scripts, verify copies, git
- `the-price-at-the-door.md` - healthcare fees as a dial (0-15x) and an insurance premium off wages, 2026-09-19: the household that goes without care rather than without food, the three corners, the eight-seed ensemble, the break-even that founding fees cannot reach
- `the-treasury-bridge-opens.md` - the Government tab's "everything else" row opened into a named journal, the desk's re-mark line and why the desk loses, 2026-09-19; the bank that never pays for the city's paper, found on the way
- `the-manual-at-0-6-7.md` - the manual artifact brought to 0.6.7 (version 7, 2026-09-18): the new transport section, what reading the published page found stale, how three contexts made it, and twenty places where the tree's prose disagrees with the code
- `the-documentation-catches-up.md` - the manual artifact brought up to 0.5.15; what the manual is and how it is versioned
- `the-year-book.md` - the year book and decade book the game writes so a run can be read without a save: FLOW/LEVEL/RATE rules, columns
- `reading-slot-3.md` - the decade book of Jerus's slot-3 city read end to end: the founding currency collapse, the students' orphaned children, the decade-22 bust, the branch-capped bank
- `reading-the-numbers.md` - how to read a playtest's figures without being misled by them
- `the-whole-codebase-audit.md` - 2026-09-10: every file read for money that leaves a pool without arriving in another
- `audit-2026-09.md` - the earlier conservation audit
- `analysis-2026-09-06.md` - a mid-project reading of what the simulation was doing
- `simulation-findings.md` - findings from the long runs, collected
- `known-bugs-backlog.md` - bugs known and not yet taken
- `design-queue.md` - design items queued before the sector template
- `steam-readiness.md` - what a Steam build needs and what was still missing

## Money, the bank, the currency and the world

The commercial bank, the central bank that was not built, the float, capital flows and the audit that ties every pool.

- `commercial-bank.md` - the bank as a design: deposits, leverage, branches
- `bank-as-a-bank.md` - the bank made to behave like one: its own cost of money
- `bank-tax.md` - the bank pays profit tax
- `the-bank-prices-its-own-deposits.md` - deposit pricing from the bank's side
- `why-the-bank-kept-failing.md` - the bank's failures traced: construction in progress on nobody's balance sheet, and more
- `the-bond-that-broke-the-bank.md` - part two: the long bond's valuation on the bank's book; fixed 2026-09-14
- `nothing-borrows-below-cost.md` - no lender lends below what its money costs it; the rate floor
- `the-lender-gets-a-gap.md` - the shortfall a lender is allowed before it calls a loan
- `business-credit.md` - private-sector borrowing: loans, restructuring, default
- `the-currency-was-the-bank.md` - how the currency and the bank were one thing, and why they were separated
- `central-bank-research.md` - what a central bank would do here; research
- `central-bank-plan.md` - the plan that came out of it
- `the-reserve-money-printer.md` - reserve money and where it comes from
- `the-vault-and-the-record.md` - the vault (cash) against the record (the books)
- `hot-money.md` - capital that arrives chasing a spread and leaves when it closes
- `the-carry-trade.md` - borrowing abroad to lend at home; the carry borrowers and their risk
- `where-the-dollars-go.md` - the foreign currency's path through the city
- `foreign-debt.md` - the city borrows in somebody else's money
- `exchange-rate-progress.md` - the float, as it was being built
- `foreign-exchange-design.md` - the exchange rate's design: pressure, drift, reversion
- `a-reserve-defends-a-currency.md` - the 0.6.9 year book read (a model with no nominal anchor), the vault kept in dollars, the founding reserve, why a reserve damps only a fall, and prices and the rate on the strip, 2026-09-21, 0.6.10
- `balance-of-payments.md` - the current and capital accounts, and that they must sum
- `devaluation-elasticities.md` - how exports and imports answer a weaker currency
- `import-substitution.md` - what the city stops importing as it learns to make it
- `money-creation-does-not-ship.md` - bank money creation built and measured, and held back
- `why-there-is-no-inflation.md` - why the price level does not rise, decomposed; the addenda carry the decomposition
- `the-world-a-city-is-founded-into.md` - world prices, world inflation, what the founding year looks like from outside
- `the-two-hundred-year-plateau.md` - why cities stop growing at two centuries
- `deflation-was-not-decline.md` - falling prices read as a crisis that was not one
- `prices-mean-something.md` - prices made to carry information: the first price pass
- `the-price-that-stopped-being-a-price.md` - a price that had become a constant, and its repair
- `a-reform-is-only-a-change-of-units.md` - the currency reform (redenomination) as a change of units and nothing else
- `money-conserved-and-students-in-flight.md` - the conservation audit passes; students in flight between cities
- `conservation-fixes.md` - fixes that closed the money audit
- `the-gap-after-the-audit.md` - a leak that opened after the audit line; fixed 2026-09-12
- `the-sign-of-a-residue.md` - a residue in the books with the wrong sign, 2026-09-16
- `the-figure-that-was-not-a-figure.md` - a displayed figure that was not a measured one, 2026-09-16
- `outward-investment.md` - the city's savings invested abroad
- `the-surplus-has-nowhere-to-go.md` - a surplus with no asset to sit in
- `the-savings-that-cannot-be-spent.md` - household savings that the model would not let households spend, 2026-09-14

## Treasury, debt, taxes and the national accounts

The city's own borrowing, what it taxes, and the books it keeps.

- `treasury-and-debt-service.md` - the treasury and what its debt costs by the paper it is owed on
- `debt-rate-curve.md` - the rate the market quotes the city, by term and by credit
- `debt-system-fixes.md` - fixes to the debt system
- `debt-quotes-on-screen.md` - a loan's cost worked out before the player agrees
- `debt-buyback-and-long-bond-repricing.md` - buying the city's own paper back; the long bond repriced
- `bond-instruments-rebuilt.md` - T-bills, medium and long bonds rebuilt
- `tbill-shortfall.md` - when the bills do not cover the month
- `policy-tab-and-vat.md` - the policy tab and the sales tax (VAT) ledger
- `vat-accounts-and-duplications.md` - VAT accounting and the double counts it exposed
- `property-tax-and-solvency.md` - property tax and whether households and firms stay solvent under it
- `the-taxes-area.md` - the taxes area of the policy screen, 2026-09-14
- `whose-cost-is-it.md` - which party bears a cost in the books, 2026-09-15
- `national-accounts.md` - GDP and the national accounts as the game keeps them
- `gdp-cannot-go-negative.md` - a GDP that went negative, and why it must not
- `the-books-tell-the-truth.md` - the sector books made honest, 2026-09-09

## Sectors, firms and investment

The sector template, each sector's own note, private investment and the rebalance.

- `the-sector-template.md` - one class extending Sector per business; the registry; what the five handlers became
- `the-eleventh-sector.md` - Food Processing
- `business-services.md` - the eighth sector
- `manufacturing.md` - the ninth
- `farms.md` - Agriculture
- `food-market.md` - the food market before the thirteen-food shelf
- `food-industry-books.md` - the food industry's books
- `shops-paid-for-what-they-sell.md` - retail pays its suppliers
- `steel-and-heavy-industry.md` - ore to steel; heavy industry
- `the-unit-of-material.md` - the material unit and what a unit of it costs
- `a-firm-that-cannot-pay-sheds-plant.md` - distress: bankruptcy declared, overdraft forgiven, plant shed on a two-year fuse
- `private-investment.md` - how a sector decides to expand
- `investment-brake-and-forecasts.md` - the brake on investment and the forecasts it reads
- `real-estate-foresight.md` - real estate builds ahead of demand, within limits
- `the-exchange.md` - the stock exchange: listings, the company index
- `the-owners.md` - who owns the firms; dividends
- `every-building-pays-to-stand.md` - maintenance on every building
- `the-wind-farm.md` - a power building added
- `the-good-that-stopped-existing.md` - a good removed from the catalogue, and what referenced it, 2026-09-15
- `the-shelf-comes-apart.md` - groceries become thirteen foods, each its own invoice, 2026-09-15
- `a-meal-out-is-food.md` - Restaurants, the fifteenth sector: a meal replaces groceries, 0.6.7
- `the-car-goes-last.md` - households buy cars, and the car is the last thing bought, 2026-09-17
- `and-they-borrow-for-it.md` - households borrow for the car, 2026-09-17
- `the-statement-opens.md` - the sector statement's lines open into their detail, 2026-09-16
- `jobs-beyond-the-stalemate.md` - when the labour market stalls: jobs beyond it, 2026-09-12
- `the-rebalance-stage-one.md` - the household economy on real figures: wages, build costs, yields
- `the-rebalance-stage-two.md` - everything else on real figures: the buildings' capital costs, the iron band, the endowment
- `deploy-manifest-rebalance.md` - the files the rebalance shipped in
- `the-founding-endowment.md` - the cash a city is founded with, and why it is what it is

## Transport

Roads, the freight band, the modes, the railway, the vehicles.

- `infrastructure-roads.md` - roads: capacity, load, throughput
- `three-roads.md` - three road kinds instead of one
- `transport-and-the-freight-band.md` - step 1: transport as a cost band on freight, 2026-09-16
- `the-freight-band-and-the-three-loads.md` - step 2: the three loads on the band
- `the-modes-and-the-fare.md` - step 3: the modes and what a fare is
- `the-railway.md` - step 4: the Rail sector
- `the-fourth-link.md` - vehicles, steps 5a and 5b
- `the-fifth-link.md` - step 5c: the cars
- `the-sixth-link.md` - step 5d: the vans
- `a-ride-is-not-a-month.md` - 0.6.6: a ride priced as a ride, not a month, 2026-09-18

## People: population, households, labour, education, health, crime

The pyramid, the households and their books, the labour market, the schools, the sick, the offenders.

- `population-revamp-phase-1.md` - the age pyramid replaces the headcount
- `population-revamp-phase-2.md` - births, deaths, ageing across the bands
- `population-goes-live.md` - the pyramid becomes the only source of population
- `workforce-and-the-people-screen.md` - who is in the workforce; the People screen
- `residents-per-job-and-the-tier-matrix.md` - residents per job and the tier matrix households are sorted by
- `household-books-per-tier.md` - household income statements per tier
- `household-balance-sheet.md` - assets and debts per household
- `every-household-keeps-its-own-books.md` - every household cell keeps its own books, 2026-09-10
- `household-cash-flow-redo.md` - the household cash-flow screen redone
- `household-screen-readable.md` - the household screen made readable
- `the-households-remember.md` - households carry memory month to month, 2026-09-11
- `the-people-the-books-left-out.md` - the retired, the students, the prisoners, the orphans, the out of work: households of their own
- `the-running-totals-of-the-dead.md` - cumulative death records by cause
- `the-long-sick.md` - long-term sickness and its rate
- `workforce-sickness.md` - sickness in the workforce
- `healthcare-buildings.md` - the healthcare buildings
- `healthcare-funded.md` - how healthcare is paid for
- `healthcare-mortality-and-births.md` - care coverage moves mortality and births
- `healthcare-panel-and-endowment.md` - the healthcare panel, and the endowment
- `pensions-and-cheaper-housing.md` - pensions, and housing made affordable to the retired
- `labour-market-design.md` - the labour market's design: bands, premiums, the floor
- `labour-market-built.md` - the labour market built
- `labour-mobility.md` - who moves between jobs and cities
- `arrivals-have-diplomas.md` - migrants arrive with an education
- `education-built.md` - schools, tuition, who goes
- `the-price-of-a-place.md` - the student grant as a menu (a share of the wage, a fixed sum, a share of last month's surplus, a share of tuition; one rule), a rate on the student loan in the Canadian shape, and a price on a place (0-5x), 2026-09-21, 0.6.9; the default playtest that never built a school, the schools' books missing from the load path, the treasury overdraft with no floor
- `the-parent-who-went-to-school.md` - a parent in school orphans the household unless the same mechanism as a laid-off parent holds it, 2026-09-15
- `one-definition-of-unemployment.md` - the pool over the labour force less students and prisoners, one definition everywhere, 2026-09-15
- `the-band-that-was-two-bands.md` - a wage band that had split in two, 2026-09-15
- `the-pyramid-is-saved-by-name.md` - the band arrays are saved by name, not by index
- `everybody-gets-the-same-matrix.md` - the tier matrix is the same for every household, 2026-09-14
- `what-a-household-eats.md` - the consumption basket: Engel's and Bennett's laws, thirteen foods, 2026-09-15
- `why-the-households-were-rich.md` - why household wealth ran high, and the real cause, 2026-09-17
- `who-is-playing.md` - who the player is in the model's terms, 2026-09-17
- `crime-has-reasons.md` - crime from graded reasons per adult; police, prisons, the sick and the killed
- `the-cities-that-empty-out.md` - out-migration when a city fails its people, 2026-09-14
- `demolition-log-and-households.md` - the demolition log and the households it displaces

## Land, housing and water

Two prices for land, two markets for housing, the water supply.

- `land.md` - land as a system
- `land-two-prices.md` - land has a market price and a use price
- `land-parcels-and-iron.md` - parcels, and the iron under them
- `land-parcels-round-two.md` - parcels, second pass
- `rent-is-a-market.md` - rent set by the market, not a dial
- `rent-per-home.md` - rent per home by size
- `homes-have-sizes.md` - dwellings have sizes and households fit them or double up
- `housing-is-two-markets.md` - owning and renting are two markets
- `the-land-tab.md` - the land office screen
- `water-supply.md` - water: draw, supply, the ratio

## The interface

Every screen and strip, in the order they were redone.

- `javafx-commercial-port.md` - the first JavaFX port
- `javafx-info-panels.md` - the info panels
- `javafx-sector-screens.md` - the sector screens
- `the-palette.md` - the theme: colours picked to be read against dark
- `the-rail-and-the-inbox.md` - the navigation rail and the inbox
- `status-strips-and-calendar.md` - the two strips and the calendar
- `fullscreen-and-keys.md` - full screen and the keyboard
- `build-menu-info-and-receipt.md` - the build menu's info card and the receipt indicator
- `the-build-tab.md` - the build tab
- `the-city-panel.md` - the city panel
- `doors-and-defaults.md` - doors (what a screen opens onto) and defaults
- `reports-redo.md` - the reports screen redone
- `policy-redo.md` - the policy screen redone
- `trade-redo.md` - the trade screen redone
- `bank-redo.md` - the bank screen redone
- `finances-redo.md` - the finances screen redone
- `sector-economy-redo.md` - the sector economy screen redone
- `services-screen-redo.md` - the services screen redone
- `people-screen-redo.md` - the people screen redone
- `the-wheel-and-the-dial.md` - a wheel notch worth the same every time; the dial, 2026-09-14
- `seven-tries-at-a-scrollbar.md` - the scroll position across rebuilds, and what it took
- `the-clock.md` - the month arrives on a clock; speed, pause, the day inside the month; 0.5.0
- `the-summary-is-a-problem-list.md` - the summary screen as a list of problems, 2026-09-14
- `what-the-player-can-solve.md` - which problems the summary should name: the ones the player can act on
- `the-instrument-panel.md` - 0.6.5: the instrument panel, 2026-09-17
- `city-history-graphs.md` - the history screen's graphs
- `time-skip-report.md` - the report after a time skip
- `month-report-carried.md` - the month report carried in the save
- `play-test-and-ui-notes.md` - notes from playing, on the interface
- `the-advisor-was-the-model.md` - the advisor was the model explaining itself

## Saves, files, logging and packaging

Where the game keeps its files, how it loads them, and how it ships.

- `save-system.md` - slots, headers, atomic writes
- `save-load-flows.md` - the flows through save and load
- `save-load-stack-mismatch.md` - a stack that did not match after load
- `save-location-and-safety.md` - the platform data folder; backups
- `load-path-parity.md` - the loaded game must agree with the one saved: ReadPathCheck
- `load-path-workforce-bug.md` - a workforce that vanished on load
- `new-game-reset.md` - starting over resets everything
- `logging-and-crash-handling.md` - every println to the log; crashes caught
- `packaging-and-distribution.md` - jpackage, the app image, SmartScreen
- `buildings-data-file.md` - buildings.json as the balance file; ids permanent

## Playtests

Long runs and hand-played cities, and what each found.

- `playtest-2026-09-01.md` - the first playtest
- `playtest-2-long-run.md` - the second, long
- `playtest-4000-months.md` - 4,002 months audited every one: LongPlaytest
- `playtest-hand-played-200k.md` - a hand-played city to 200,000
- `playtest-hand-played-116k.md` - a hand-played city to 116,000
- `phase-5-complete.md` - phase 5 done: where the project stood
