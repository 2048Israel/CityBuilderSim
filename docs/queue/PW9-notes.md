# PW9 — a small firm plant for the early towns

Builder: Codex (GPT-6.1-Sol), `batch/PW9`, worktree `codex-PW9`, from
`4f6bb2b5c` (0.9.208, format 66). Brief: [PW9](PW9.md).

## What changed and why

The catalogue gains **Small Steam Station**, permanent id **172**, 850 kW
nameplate, 733 average kW in the game's month, coal-fired, offered for new
orders in 1900–1914, with a 15-month construction setting under the existing pooled rule. Its native dispatch buys
thermal coal, charges the utility and passes those costs to the ordinary
planner. Its existing need-date and price rules apply; the generic dated
credit repair below supplies their matched inputs, including `timely()`.
The built-in fallback has the same row. PowerPlanCheck causes a seasonal power shortage in a 1906
town of 3,000, proves the old oil-plant choice with the small row excluded,
then proves the small order and its actual placement with the row available.
BuildingDataCheck pins its id, size, fuel and date; PowerPlantsCheck includes
it in the catalogue, fuel purchase and year-gate laws.

Play testing exposed a capacity defect important to a small first plant:
its hiring could reduce the jobless founding grid by more power than the
new station added. The batch repairs that generic endowment reading in
native output, native dispatch, annual dispatch cost and planner supply.
Each built plant keeps its own staffing rule; water staffing is unchanged.

The first played orders also exposed a pending-work date mismatch. The
review mend compares native queued generation and industrial load at the
same whole-month dispatch dates and next seasonal peak, keeping genuine interim and reserve
needs. Its first-unit promise protects a crew; it no longer opens every
pending generator in a preview. The existing need-date and price rules
remain, with the binding dated requirement used consistently for sizing
and ranking.

★ **Add a row; keep existing ids and sizes.** Coal's 325 MW nameplate is
about 41.5 MW in its 1900 recipe; oil offers 86 MW, diesel 19 MW from 1910,
and hydro needs a river site. Reducing any of those permanent rows would
also resize the plants a saved city already owns. None carries the missing
town-sized thermal choice. The new steam station uses the
existing utility shape and its land field; FS6 was told by bus.

## Sources checked and the calls (★)

The data below are facts or explicitly marked estimates, with no copied
source prose or images in the game. US Census and EPA publications are US
government public domain; the 1900 Engineering News issue has expired US
copyright; Peabody, ASME and the staffing account below supply facts only,
with no copied prose.

- [US Census, *Central Electric Light and Power Stations, 1902*, table 1,
  printed p.6](https://archive.org/details/centralelectric00goog): 3,620
  stations, 815 municipal; 5,930 steam engines with 1,379,941 hp and 1,390
  water wheels with 438,472 hp. The report separately footnotes 26,635 hp
  in gas and auxiliary steam engines. Its electrical dynamos total about
  1.625 million hp (1.212 million kW). The original census was checked,
  rather than treating the brief's mechanical hp as electrical output.
- [US Census, *Central Electric Light and Power Stations, 1907*, tables 1
  and 34](https://archive.org/details/centralelectricl00unitrich): both
  census years are tabulated; 4,714 stations in 1907, 1,252 municipal,
  2,709,225 kW of dynamos. Electrical capacity averages 335 kW per station
  in 1902 and 575 kW in 1907. The brief's approximately 380 and 650 kW are
  converted prime-mover hp, not delivered electrical kW. The 1907 table
  excludes auxiliary engines (1,830,594 and 4,032,365 hp); its footnote
  adds 14,454 and 65,823 hp, giving 380 and 648 kW per station including
  them. Both original scans were visually checked, not just their OCR. Either measure
  puts a town's station in hundreds of kW, far below the old oil row.
- ★ **850 kW, 15 months, from 1900.** [Peabody Municipal Light Plant's own
  history](https://pmlp.com/226/Our-History) dates authorization to June 18,
  1891, gives 350 + 500 kW, and dates first operation to September 27,
  1892: approximately 15 months. It also records coal-fired boilers.
  This is a period-sized example, not a claim that every station was 850
  kW. The page calls the early generators turbines; this batch uses only
  its capacity and dates, not that anachronistic label. The game starts
  in 1900, so this existing technology is available at the start.
- ★ **Capital: period $100/kW equipment estimate**, [Engineering News,
  July 26, 1900](https://upload.wikimedia.org/wikipedia/commons/3/36/Engineering_News-Record_and_American_Railway_Journal_1900-07-26-_Vol_44_Iss_4_%28IA_sim_enr_engineering-news-record-_1900-07-26_44_4%29.pdf).
  Its comparison with storage batteries prices boilers, engines, piping,
  generators, stack and switchboard at that rate. $85,000 for 850 kW at
  1900's level is $3.284 million at the catalogue's 2025 level, using the
  existing CPI table (8.333/321.943). HY1's existing conversion gives cash
  2,400, 269 construction points and 44 materials after rounding. This is
  the period equipment estimate, not Peabody's appropriation or a modern
  steam plant's price. Ground, construction labour and wires follow the
  game's existing bills.
- ★ **75,000 Btu/kWh**, [EPA, *Development Document … Steam Electric Power
  Generating*, 1974, section IV](https://nepis.epa.gov/Exe/ZyPURL.cgi?Dockey=9100Z5L9.TXT),
  gives that 1900 heat rate. The game converts heat to tonnes using its
  thermal coal's own heating value, rather than silently assuming the
  source's coal grade. This is an old, inefficient engine; no free fuel or
  modern efficiency is attached to the small size.
- ★ **Supporting estimates:** 85% availability and $9.37/MWh variable
  upkeep use the existing coal row's convention; 850 × .85 × 730/720
  rounds to 733 average kW. The initial five-post estimate used the 1907
  municipal mean of 4.45 employees per station; REV-PW9 correctly found
  that a fleet mean does not establish round-the-clock staffing for this
  station. The nine-post revision is sourced below. One acre includes the
  boiler/engine rooms and coal yard; own draw 1 kW (the existing catalogue
  uses whole kW) and road load 1 are small utility estimates. Cooling uses the existing steam rule. No
  special lifetime was invented: the planner retains its common bond-life
  assessment and the model has no per-row retirement-age field.
- ★ **Keep the existing pooled construction rule.** `buildMonths=15` caps
  a stack's total progress at its units × points / 15, so the first of two
  can open after about eight months; later orders share the pace. The
  existing per-unit completion quote and WorksValue's schedule use the same
  rule. The helper checked the documented PW2 paths on main and the new
  row (`lead-semantics.txt`); no construction or timing rule was changed.

### Review mends: the period and operating staff

★ **Last new-order year 1914, inclusive.** [ASME's Coolspring Power Museum
history](https://www.asme.org/about-asme/engineering-history/landmarks/215-coolspring-power-museum)
places internal-combustion competition for electric generation by 1900 and
for all but the largest installations by 1915, with turbines dominant at
the large end. The catalogue boundary closes this reciprocating station
at that transition. It is a modelling date drawn from a historical
transition, with December 1914 the last offered month. Diesel is already
available from 1910. `until` defaults to zero (no end) for existing rows;
menus, direct orders and the future menu honour it. Standing and queued
stations remain in the catalogue and native dispatch after it closes.
PowerPlantsCheck causes December 1914/January 1915 with funded orders,
and requires both the standing dispatch row and earlier pending order to
survive closure. BuildingDataCheck compares the field with the fallback.

★ **Nine operating posts, six unskilled and three trained.** N. M.
Argabrite's firsthand retrospective, [“We Expect a Lot from the Safety
Man,” *National Safety News*, January 1936, printed p.10](https://cdn.toxicdocs.org/VJ/VJR5dMp3DX8eeO9XOmYkraQ7o/VJR5dMp3DX8eeO9XOmYkraQ7o.pdf),
describes small powerhouses 25–30 years earlier with engineer, fireman and
oiler duties, commonly on twelve-hour days. The original scan and issue
contents were checked. Three eight-hour watch teams are the model's
allowance for those three operating duties: three trained operators and
six firemen/oilers. Nine is an explicit staffing estimate based on that
period operating organization, rather than a measured Peabody headcount.
The wage tiers are an estimate too. Five did not provide that coverage.
Availability, fuel efficiency and cost are unchanged.

## Saves

★ No format bump: appending a permanent id leaves every old id and saved
field intact. An old save does not read its existing buildings wrongly.
No conversion code and no save-slot writes.

The review's `until` is catalogue metadata, zero/unbounded on every old
row. `Ahead.powerFactor` is a transient advice value, with the existing
three-argument constructor retaining factor 1; the save classes and saved
AutoBuilder state do not store Ahead/Suggestion values. No owned save
field or saved enum changes in these mends.

## Measurements and checks

The chronology below retains the first proposal's evidence through
`141e35ee0`, including its rejected continuous frontier and adverse played
results. REV-PW9 rejected that proposal; the review-mend results follow
separately. Earlier “final” artifact names identify that earlier candidate.

The new PowerPlanCheck section was run against main's unchanged catalogue
and fallback at `4f6bb2b5c`, with the current harness copied and freshly
compiled. The 1906/3,000-person shortage/date fixture passed; its control
ordered one Oil-Fired Plant; only the small-station choice failed (one
assertion, batch exit 4). This is the required red-on-main finding, rather
than a missing-data assertion. Logs are kept in
`codex-PW9-measure/target/PW9-measure/red-main-fixed-{PowerPlanCheck.log,suite.txt,run.log}`.

Validation incidents: the first copied harness preserved an old source
mtime and Maven reused bytecode without the new section; that call is not
red proof. The fresh compile then caught my fixture's `List<Double>` where
the history stores `List<Long>`; corrected before the red proof above.
The first after-candidate compile also caught a fractional own-load value
passed to the catalogue's integral kW setter. Both definitions now use
1 kW; no model month ran on that rejected candidate. The corrected model
was `0dee796eb`; the capacity mend is `0f618e7c5`, and the dated-capacity
mend is `4a525e610`.
The initial UI-probe measurements were rejected: that path omits
`startAutomaticWorks()`, government decisions and ground watching. They
are superseded by the actual LongPlaytest main path below.

`./batch-check.sh --fast`: exit 0, compile green, maps regenerated, Stale
0 firm findings (207 soft). The first local run rewrote generated dates;
regenerating with `-Duser.timezone=UTC` retained main's date and left only
the ten affected generated files. Every check names the main worktree's
`earth-2.bin` and `world-1.bin` with the required `C:/...` paths.

### The rejected catalogue-only candidate and the capacity finding

The helper ran the actual LongPlaytest main path, with automatic works,
government decisions and ground watching on, default historical founding
first and `-Dplaytest.founded=today` second. It changed no economic inputs;
the temporary observer suppressed scratch autosaves and recorded each
month. BEFORE is main `4f6bb2b5c`; the intermediate catalogue-only AFTER
was `0dee796eb`. Logs, native CSVs, comparison CSVs and the reusable diagnostic
patch are in `codex-PW9-measure/target/PW9-measure/`.

Orders below use the **pre-calendar decision pass**: m121 is the order first
visible in the completed m122 city (PF1's convention). Openings use the
completed month. Peak share is the native planner's firm supply over its
peak-hour demand; energy served is the retained dispatch book, not a flow
reconstructed after construction completes.

| Intermediate 1900 run, before the capacity mend | main BEFORE | catalogue-only AFTER |
|---|---|---|
| First generator order | m417, 1 Diesel Station | m408, 2 Small Steam Stations |
| Population at that decision | 5,446 | 4,656 |
| Peak share / energy served at decision | 1.2784 / 100% | 1.5904 / 100% |
| First unit opens | m435 | m416 |
| Population at first opening | 7,593 | 5,296 |
| Peak share / energy served at first opening | 2.4048 / 100% | 1.1731 / 100% |
| Minimum peak share, common m2–435 | .9156 at m434 | 1.1418 at m426 |
| Minimum energy served, common m2–435 | 100% | 100% |
| Months below 95% energy served, m2–435 | 0 | 0 |
| Generator units ordered through m435 | 3 diesels | 14 small steam, 1 oil plant, 3 diesels |
| Generator units ordered through m440 | 3 diesels | 48 small steam, 1 oil plant, 3 diesels |
| Minimum energy served, m2–440 | .9926 at m439 | .9968 at m439 |

The baseline also orders a Substation at pass m409; it was counted
separately. This intermediate result was not accepted as the final result.
The quoted generator orders through m440 were about $54.3M against main's
$10.5M, excluding land. Later large plants can make shorter-lead stations
a bridge, but the retained trace did not capture each credited site schedule
and promise, so that explanation alone did not validate the follow-on buying.

★ **Mend the demonstrated capacity loss before shipping the row.** At the
first small station's opening the actual native rows offered 8,455.144 kW
from the founding grid and 619.762 kW from the station: total 9,074.907,
down from 10,000. The pooled utility fill was .845514445. There were no
founding-grid posts to lose or reassign: its endowment was multiplied by
the new utilities' staffing fraction. At zero fill the old code restored
the whole endowment, a discontinuity rather than a conserved labour trade.
The planner also previewed that fictitious loss, so changing the rank or
tuning the station's five sourced posts would conceal the defect.

The corrected law keeps `getFoundingGridKw()` fixed and scales built plants
by their own posts. Bare handlers still staff their synthetic plant output;
K founding endowments still mean K times the founding grid. The native
dispatch, production counter, `yearRunning()` and `powerSupplyDemand()` now
agree. No new fields, water change, station exception, ranking change or
price adjustment was needed. The two arithmetic expectations in
MaterialsCheck and NewUnitCheck now omit only the fictitious endowment
delta; their original fixtures, resolved-new-unit premise and other laws stay.

PowerPlanCheck 9b causes scarce staffing with the shipped five-post station,
checks the normal standing-build/services path against the preview, and
covers zero/partial/full fill, water/substation posts, the bare handler,
annual cost and K endowments. On the inherited capacity model its premise
passed and nine assertions failed; after the mend every assertion passes.
The red log is `target/PW9-grid-red.log` (suite: `target/PW9-grid-red-suite.txt`).

**Catalogue-only today's city: no change through m180.** Its before/after month and event
CSVs are byte-identical. It still orders one Diesel Station at pass m121
(visible m122), first open m151. At m89/m90 peak share is 2.1571/2.1368 and
energy served 100%; at m120 peak share is .5712 and energy served .8743.
Both minima are at m126: peak share .5636, energy served .8025. Five months
are below 95% energy served. At the m151 opening, peak share is 1.9822 and
the retained energy book is .9606. The catalogue addition does not remove
PF1's crew holds and calendar/timing outcome in this run. No row price or
planner rule was tuned to force that path to change. These are intermediate
figures; the final-model comparison follows after the new bounded trace.

### The second finding: compare queued capacity and demand at one date

The capacity-mended model `0f618e7c5` removed the fabricated founding loss,
but its retained native order at pass m433 still asked for 30 stations.
An isolated read-only replay captured the actual selected suggestion and
matched the original month/event CSVs byte for byte. The row's lead was
14.941 months and its demand date 26.941 months, growth 1.44316, slack .15.
It credited only the queued units open by the earlier date: 35.049 MW
against 53.518 MW of later demand. At that later date the queued diesels
actually offer 71.208 MW against 53.521 MW, including their own draw.
The pending non-power load is .439 MW at the lead, 22.289 MW at the horizon.

★ **Match dates, and retain genuine interim needs.** Capacity arriving
after the candidate's completion cannot be omitted while a later industrial
load is included. Conversely, a generator that arrives after an earlier
shortage cannot be counted early merely because it covers the horizon.
Reserve-only gaps must retain the existing first-work-share rule too.
The chosen correction evaluates matched demand and native commissioned
capacity at the relevant frontier, including the side just before new
generation arrives, and sizes the existing rule for its controlling need.
Saved first-unit promises and crew protection remain; a first-unit promise
does not commission the entire queue. No row price, jobs or fuel is tuned.

The intermediate replay sampled integer and generator dates and found no
reserve gap there, but did not capture every industrial commissioning time
inside (25,26]. It proves the mismatched-date sizing, not the absence of
every smaller interim bridge. The permanent regression therefore needs
both a covered schedule and controls with genuine interim reserve-only
and seasonal shortages. Exact evidence is in
`codex-PW9-measure/target/PW9-measure/final-selected-1900-selected.txt` and
`final-selected-verification.txt`; these intermediate measurements are not
the final-model result.

The dated mend is `4a525e610`. A read-only `WorksValue.PowerDates` holds
the existing native generation/load schedules for one evaluation. It
locates their actual completion transitions, including rounding, once and
reuses them for each candidate; it tests the completion/horizon endpoints,
load jumps and the instant before generation arrives. Demand, growth,
commissioned supply and reserve caps use the same date. Native dispatch,
construction and life-cost valuation are unchanged.

★ **Keep count, quote and rank coherent.** If a larger quoted count finds
a lower requirement as pending units commission, retain that feasible
count and its quoted lead, then recheck its actual supply at every date.
Do not silently shrink and re-quote while keeping a different lead's
guards. Falling farm credit still has its cap and an explicit all-date
closure check; a failed closure remains failed. Seasonal guards use native
capacity at actual completion/prior/faster-arrival dates. Annual running
cost remains evaluated at actual completion, not the binding later date.

PowerPlanCheck 9c isolates the small row while three physically pending
diesels remain off the purchase menu. It causes native commissioning
times rather than asserting a played-city count: a covered schedule, the
same load moved before the last generator (a useful reserve-only bridge),
a genuine seasonal gap with an oversized coal-only menu, and a last
generator after the horizon. Each runs with absent, expired and live
first-unit promises; the covered placement path spends nothing.

Its authoritative red run on the inherited model exits 4: all existing
laws and every new premise pass, nine behaviour assertions fail. The
unchanged fixtures after the mend pass: covered schedules order zero,
interim reserve and after-horizon controls order one small station, and
the seasonal-only control retains its coal waiver. All earlier
PowerPlanCheck laws, including 9a/9b and PF1, pass (exit 0). Evidence is
`codex-PW9-dates/target/PW9-dates/{red,green}-PowerPlanCheck.log` and the
matching suite files. Early red attempts exposed two fixture calibration
mistakes (a floating-point load transition and an advisory count used for
an automatic need); both were corrected before the authoritative red,
with production still unchanged. No premise changed between that red and
green.

The helper's three-file diff was reviewed before applying it: only
BuildAdvice, the read-only WorksValue schedule wrapper and PowerPlanCheck;
no price, staffing, save, fuel or cash-path change. The native reader is
called rather than copying its completion formula. The explicit final
count/quote/date assertion guards the retained-count choice.

`./batch-check.sh BuildingDataCheck`: exit 0, catalogue/fallback parity and
the appended row's pins green. `./batch-check.sh PowerPlanCheck`: exit 0,
including the same 1906 fixture, oil control, small-station choice, closure
and actual automatic placement. Both regenerated maps without further
changes and reported Stale 0 firm findings.

The initial PowerPlantsCheck run passed 153 of 154 assertions. Its exact
dry-city steam list still contained only coal, oil and biomass; it now also
requires the Small Steam Station to be refused without cooling water.
The physical cooling rule and every fixture remain intact.
The corrected PowerPlantsCheck then passed all 154 assertions (separate
named call, exit 0), including actual coal buying, wages, cooling and years.

After the capacity mend and FS6 merge, PowerDispatchCheck, MaterialsCheck
and NewUnitCheck each passed in separate named calls (exit 0). Stale then
reported 0 firm findings, 209 soft. FS6 was merged from `c72b19ab2`; its
changes are checks/Observation and records, not simulation model code.

Final-source `./batch-check.sh --fast`: exit 0, zero firm Stale findings,
209 soft. `./batch-check.sh NewUnitCheck` was repeated after the dated
mend because its public power reading now opens the existing pending-load
cache scope; exit 0, including the cache and fitted-unit laws. The helper's
same-source PowerPlanCheck and `--fast` also exit 0. Generated maps were
regenerated in UTC. No ensemble has run on an intermediate model.

Main's FS6 changes were merged at `3dc586b43`; the later queue-only main
update `3bf83d20e` was merged too. The final model is `4a525e610`, with
generated records at `f00e4e58c`. The current newest ensemble baseline is
`docs/ensemble/0.9.208.md` (OA1, 80/80, snapshot `dc7f9cc7d`): M1 and F1
pass for all policies, 10/10 hard criteria; AUTO2 reaches 40,000 in 16/16
seeds, median m524, with final median population 6,240,014. The PW9
ensemble is owed after the stranger's SHIP; no ensemble was started early.

`sh tools/pick.sh origin/main HEAD` reports ALL 257 harnesses, because
BuildingManager has 125 readers. Its review floor says "not needed — no
money, saves or threading in the diff"; PW9's explicit brief requires a
stranger because catalogue choices move city money, so that review remains
required. The provisional ship file passes `tools/known-reds.sh` (exit 0);
there is no owned known-red waiver.

### First review candidate's native comparison (`141e35ee0`)

Both bounded named measurements completed on the exact five model/resource
blobs from `f00e4e58c` (the model of `4a525e610`), verified before and after
compilation and at runtime. BEFORE is still faithful main `4f6bb2b5c`;
the merged FS6/queue changes do not alter that model. They use the actual
LongPlaytest main path described above, native seed 0 and unchanged economic
inputs, first the 1900 city through m440, then today's city through m180.
Both exit 0 and write zero save files. This is bounded observation, not a
full LongPlaytest validation. All helper jobs ended before this handoff.

| Final 1900 city | main BEFORE | PW9 final |
|---|---|---|
| First generator order, pass / visible month | m417 / m418, 1 Diesel | m407 / m408, 1 Small Steam |
| Population at order | 5,446 | 4,619 |
| Peak share / current energy served at order | 1.2784 / 100% | 1.6105 / 100% |
| Retained dispatch month / energy served at order | m416 / 100% | m406 / 100% |
| Retained native whole-order lead | 30.0 months | 15.0 months |
| First unit opens | m435 | m418 |
| Population at opening | 7,593 | 5,588 |
| Peak share / current energy served at opening | 2.4048 / 100% | 1.3533 / 100% |
| Retained dispatch month / energy served at opening | m434 / 100% | m417 / 100% |
| Minimum peak share, common m2–435 and full m2–440 | .9156 at m434 | 1.2784 at m417 |
| Minimum current energy served, m2–435 | 100% | 100% |
| Minimum current energy served, m2–440 | .99263 at m439 | .99418 at m439 |
| Months below 95%, m2–435 / m2–440 | 0 / 0 | 0 / 0 |
| Generator units / order events through m435 | 3 Diesel / 2 | 14 Small Steam + 1 Oil + 3 Diesel / 11 |
| Generator units / order events through m440 | 3 Diesel / 2 | 38 Small Steam + 1 Oil + 3 Diesel / 13 |

The first final station's actual building order costs $253,950.11, with
no purchased land. Exact BEFORE spend was not retained: its native rounded
quotes total about $10.5M through m435/m440. PW9's actual generator orders
total $42,926,720.55 plus $22,033.42 purchased land through m435;
$50,967,663.13 plus $145,623.88 through m440. These include the oil/diesel
orders, exclude substations and are an adverse procurement observation.
Historical population at m440 is 8,245 → 8,372. Both cities order one
Substation at pass m409; BEFORE opens it at m440, PW9 still has it on site.

| Final today's city, m2–180 | main BEFORE | PW9 final |
|---|---|---|
| First generator order, pass / visible month | m121 / m122, 1 Diesel | m101 / m102, 10 Small Steam |
| Population at order | 5,608 | 3,995 |
| Peak share / current energy served at order | .57075 / .82168 | 1.25336 / 100% |
| Retained dispatch month / energy served at order | m120 / .87430 | m100 / 100% |
| Retained native whole-order lead | 56.5 months | 22.3 months |
| First unit opens | m151 | m103 |
| Population at opening | 7,490 | 4,185 |
| Peak share / current energy served at opening | 1.98217 / .96063 | 1.31304 / 100% |
| Retained dispatch month / energy served at opening | m150 / .99156 | m102 / 100% |
| Minimum peak share | .56361 at m126 | 1.11207 at m123 |
| Minimum current energy served | .80246 at m126 | .72623 at m127 |
| Months below 95% current energy served | 5 | 12 |
| Generator units / order events | 1 Diesel / 1 | 40 Small Steam / 9 |
| Substation opening | m162 | m177 |

Today's first final order costs $1,247,423.35, with no purchased land;
generator orders total $5,629,801.59 plus $121,013.49 land, versus about
$2.0M in BEFORE's rounded native quote (exact BEFORE spend unavailable).
The row changes PF1's m120 outcome: peak share .57117 → 1.67267 and current
energy served .87430 → 100%. At m89/m90 both runs remain at peak shares
2.15712/2.13683 with 100% energy served. The actual passes still hold
three/four wanted small stations, order zero with `Cut.BUILDERS`, and
preserve Elementary's completion. The earlier crew limit remains.

★ **Report delivery and procurement costs with the earlier relief.** The
new small row does not remove wire capacity or crew constraints. Today's
Substation opens 15 months later; its current energy is below 95% in
m124–129, m139, m151, m163–164 and m175–176. Native dispatch m127, retained
at m128, still has 22.446 MW offered firm power but only 7.2 GWh generated,
6.84 GWh delivered against 9.914 GWh gross asked, consistent with the
existing 10 MW wire ceiling. The founding grid supplies that generation;
the offered steam generates zero. Thus peak adequacy is not evidence of
energy delivery. This real carry constraint and the crew hold stay; the
station's jobs, cost or size were not tuned to erase their effects.

The corrected historical pass m433 orders **one** small station, versus
the intermediate mismatched-date 30. At pass m439 it still orders **23**,
`Cut.NONE`, $7,730,759.72 plus $123,590.46 purchased land, retained native
whole-order lead 14.6 months, physical relief/prevented output loss zero.
The observed city's peak share is 3.00118 and current energy .99418 then.
Its actual selected `Ahead`, credited site and rank were not observed.
★ **Flag that order for review; do not claim its cause or an optimum.** A
current surplus alone does not settle a future/interim need. The permanent
regression proves the matched-date law and its bridge controls; no further
replay, price tuning or unrelated model repair was launched on this
uncontrolled observation. Every final actual generator-order pass records
reserve delta 1, spread delta 0 and wanted = ordered; the remaining orders
are reserve orders, not demonstrated economic-spread purchases.

Evidence is in
`codex-PW9-measure/target/PW9-measure/README-dated.txt`,
`dated-after-{1900,today}-{months.csv,events.csv,passes.csv,causal.txt}`,
the matching per-call logs/suite/times/exit files, `dated-comparison-*`,
`dated-cost-totals.csv`, `dated-source-provenance.json` and
`dated-runtime-resource-hashes.txt`. All 108 older artifacts were preserved
byte for byte. Source and runtime catalogue SHA256 is
`d59a31db3573c5660db6745030f584992e5fef3c55bf4f7f03f314663a89b7c1`.
Other sessions' named checks overlapped these runs; timing is not an
isolated-machine comparison.

## REV-PW9 and its mends

The orchestrator's review instruction `20261125-233817-850` is NOT SHIP:
two P1s, with the row's figures, money handling and old saves accepted.
It requires the monthly/seasonal planner correction, a sourced last order
year, the Substation delay attribution, better staffing and the independent
oracle's fixed founding-grid law. No further stranger review is required;
the mended findings are the go for the ensemble, followed by the requested
ALL dry-run slot. The provisional marker remains.

### P1-a: the 23-station order was a sub-month quote gap

An isolated read-only replay of exact `141e35ee0` captured the actual
selected suggestion and placement at m439. Its month and event CSVs match
the retained first-review candidate byte for byte. The selection is 23
stations, actual quoted lead 14.6206896552 months, but its binding date is
14.999999999999998, immediately before a diesel's 15-month quote. The
industrial load jumps at 14.72462586324, from 194 to 22,044 kW. The
artificial interval lasts **0.275374137 month**. The credited six small
stations give 38,035.0413 kW against 52,667.1196 kW reserve demand. At
month 15 the queued diesel counts: 56,304.9268 against 52,668.7502, covered.
At the next native northern July peak, offset 24, the same commissioned
fleet covers 55,175.6457 kW reserve demand too.

★ **Replace the continuous-frontier law.** The game commissions and
dispatches in whole months. A fraction of a month between quotes cannot
be presented as an independent dispatch at the year's peak hour. The
planner must compare both sides at monthly commissioning states, test
reserve at the next native seasonal peak with due generators included,
and retain actual-month physical shortages before that peak. The count
may fall as the gap closes; retaining `max(quoted, required)` was a
conservative over-order, not an acceptable quantity rule. This explicitly
supersedes the earlier continuous 9c law and its retained-count decision.
Its surviving law is matched dates and genuine interim need, now caused
at actual monthly resolution. Money and save checks stay intact.

Reality check: [EIA's explanation of NERC's seasonal reliability
assessment](https://www.eia.gov/todayinenergy/detail.php?id=31492) describes
reserve as expected unused generation at peak load, with existing and
planned resources assessed together. [NERC's reserve-margin
definition](https://www.nerc.com/programs/reliability-assessment--performance-analysis/reliability-indicators/m-1-reserve-margin)
also compares anticipated resource capacity with peak seasonal demand.
The game's own month sequence and seasonal curve determine its discrete
commissioning and peak dates; the reserve percentage remains the player's
existing setting.

Root-owned code for the cutoff, nine posts, catalogue parity and independent
oracle is at `785f7e2de`; compilation and named validation are pending the
helper's serial JVM slot. The independent oracle sums the fixed founding
endowment, works' surplus and each standing/added plant's own staffed firm
output; its pending draw and peak demand now match the physical law while
its cash-flow computation remains independent of WorksValue.

Diagnosis incidents: the first temporary Substation observer dereferenced
an absent stack; its corrected attempt then passed an integer to a floating
formatter. Both stopped at m85 before any Substation order and saved no
files. They are observer failures, not model reds or evidence about the
delay. The observer was corrected offline and replacement calls explicitly
bounded. A baseline compile then caught a current-only diagnostic helper
reference; no baseline month ran on that attempt. The reference was removed
before its replacement. Prior artifacts remain preserved.

### P1-b: why the first proposal's Substation slipped

The completed main/current pair uses the actual native player and logs
GRID's cuts and construction state. Both successful runs exit 0, save
zero files, and reproduce their retained month/event CSVs byte for byte.
At pre115 **both main and PW9 hold GRID with `Cut.BUILDERS` to protect the
College's first completion**: that crew-protection law is inherited.
Main releases and orders at pre116, opening m162. The first PW9 proposal
still protects College at pre116, then records `Cut.DEBT` at pre117–118,
orders at pre119 and opens m177. The delay is three months in ordering and
twelve more in construction (46 → 58 months from its order).

| Substation native construction state | main | first proposal |
|---|---|---|
| Progress at m120 | 13,042.72 | 3,093.63 |
| Progress at m150 | 105,996.43 | 90,299.51 |
| Progress at m162, out of 132,000 points | complete | 113,048.81 |
| m160 current-state home plan, allocated / total points | 1,556 / 1,556 | 1,083.48 / 1,595 |
| Builder fill at m127 | .7200 | .7160 |

The m160 allocation is a read-only plan at the observed state, not a
reconstructed struck flow. The current queue still contains steam stations
and more other work. AutoBuilder, ConstructionControl and SimulationEngine
are unchanged between the two models; BuildingManager's model change is
the catalogue row. The changed generation, finance and industrial-growth
path meets the existing College protection, debt limit and allocation
rules. The evidence does not isolate every delayed point to direct steam
construction; the tiny fill difference alone does not explain twelve
months. None of those resource/conservation laws was relaxed.

The “today” flag is modern equipment on the same 1900 calendar: pre101 is
May 1908. The 1914 last order year closes later chronological orders; it
does not remove those early eligible orders. The final combined rerun must
therefore measure delivery again rather than claiming the end year alone
restores the old result. FC2-C owns a distinct capped-site/foreign-crew
allocation question; it is not an established owner for this College/debt
hold, so it is not used as a catch-all waiver.

The first attempt at the revised monthly 9c fixture exposed a zero-clamped
first diesel quote and an invalid extrapolated interval; it is not red
proof. The corrected fixture uses native August 1900, keeping the same
three diesels and economic inputs, with positive first-unit dates and flat
history. Its seasonal control targets the actual July peak. No production
mend had been applied to that rejected fixture attempt.

The valid monthly regression then runs on unchanged `141e35ee0` planner
code with the revised catalogue: seven intended 9c behaviour failures,
zero failed premises, all earlier laws and the scarce-staffing independent
oracle equality green. After the mend all PowerPlanCheck laws pass.
The original 8a fractional deadline is retained as a late control, beside
an on-time case whose seasonal-demand forecast crosses between native
whole commissioning months; the earlier fractional “on-time” premise was
not silently deleted. Root's expected peak date calls PowerDispatch's
native peak/hemisphere helpers instead of repeating the planner's search.

★ **Keep the monthly reading coherent and lifetime valuation unchanged.**
`Ahead` carries a power demand factor alongside native growth `k`, with
all existing three-argument callers retaining their annual peak-hour
meaning. Physical-month sizing, closure, share, screening and the published
zero-reserve reading use that same factor; next-peak reserve retains the
player's setting. The finite distinct-count worklist validates each
actual quote against every applicable month and capacity cap, can fall to
a smaller count, and chooses the least feasible evaluated quantity.
Root reviewed the helper's model diff and removed an unused load snapshot
and an extra propagation of one month's factor into the multi-year
`servedOverLife` reader. The actual-completion annual dispatch and existing
life/cash-flow calculations remain unchanged. The oracle's matching factor
is its only change after the root-owned fix.

Model mend commit: `651b80a00`. Helper proof and reports:
`codex-PW9-r2/target/PW9-r2/{handoff.txt,diagnosis.txt,red-valid-PowerPlanCheck-harness.log,reviewed-PowerPlanCheck-harness.log,handoff-fast-run.log}`,
with exact tested hashes and paired grid observation logs. The helper's
reviewed named check and final standalone fast check exit 0, Stale zero
firm. It briefly overlapped a finishing named wrapper's maps/Stale with
an early fast wrapper after the harness had passed; both exited 0, with
no native playtest overlap. A final fully standalone fast check replaced
that overlapping fast as the handoff evidence. All its jobs exited before
root resumed JVM work.

Root `./batch-check.sh --fast` on the mended source: exit 0, Stale zero
firm, 209 soft. Root BuildingDataCheck: exit 0, including nine posts,
the order window and complete fallback parity. Root PowerPlantsCheck:
exit 0, all 159 checks, including native closure/refusal and preservation
of standing/pending capacity. Root PowerPlanCheck, BuildAdviceCheck,
WorksFinanceCheck and NewUnitCheck then each exit 0 in serial named
calls. No full pick ran on the laptop. The maps at `35abdbe03` are from
that source; the later project prose commit changes no model.

### Final combined review-mend comparison

Fresh worktree `codex-PW9-r2-measure` was cut at `35abdbe03`, model
`651b80a00` plus metadata/staffing `785f7e2de`. All 689 tracked src/main
files were verified before the three-file observer-only patch; all other
687 files were reverified afterward. Both authorized native calls exit 0
and save zero files, default first through m440, then modern equipment
through m180. No extra replay or model repair occurred. All helper jobs
ended. Runtime/source catalogue SHA256 in both calls is
`145c0affb5c41e9a9d7e1110d2d71521d9e1ffb9d593825f5594c204d4b2e26a`.

| Default 1900, final comparison | main BEFORE | final mends |
|---|---|---|
| First generator pass / visible month | m417 / m418, 1 Diesel | m417 / m418, 1 Diesel |
| Order population / peak share / current energy | 5,446 / 1.27844 / 100% | same |
| Retained dispatch at order | m416, 100% | same |
| Retained native whole-order lead | 30.0 months | 30.0 months |
| First unit opens / population | m435 / 7,593 | same |
| Peak share / current energy at opening | 2.40482 / 100% | 2.46939 / 100% |
| Retained dispatch at opening | m434, 100% | same |
| Minimum peak share, m2–440 | .91560 at m434 | same |
| Minimum current energy, m2–435 / m2–440 | 100% / .99263 at m439 | same |
| Months below 95%, both windows | 0 | 0 |
| Generator units / events, through m435 and m440 | 3 Diesel / 2 | 3 Diesel / 2 |
| Generator spend, through m435 and m440 | about $10.5M rounded quotes | $10,507,089.59 actual |
| Purchased generator land | exact spend unavailable | $0 |
| Substation pass / opening | m409 / m440 | same |

The final first order's actual building cost is $3,282,067.97; the second
orders two diesels at pre429 for $7,225,021.62. No oil or small steam is
ordered through m440, and no generator order occurs at m439. The default
first needs new generation in September 1934, after the station's new-order
window. The controlled 1906 town still orders the small station. The
rejected proposal's **$50,967,663.13 → $10,507,089.59**, with the same
three-diesel procurement as main. Exact main spend was not retained, so
no exact main dollar delta is claimed. Population, current energy, retained
dispatch served/asked/generated fields and fleet match main in every
monthly row through m440; current firm/peak share differ in six rows after
the first opening. This is not a comparison of every private city field.

| Modern-equipment city, m2–180 | main BEFORE | final mends |
|---|---|---|
| First generator pass / visible month | m121 / m122, 1 Diesel | m101 / m102, 9 Small Steam |
| Order population / peak share / current energy | 5,608 / .57075 / .82168 | 3,995 / 1.25336 / 100% |
| Retained dispatch at order | m120, .87430 | m100, 100% |
| Retained native whole-order lead | 56.5 months | 22.1 months |
| First unit opens / population | m151 / 7,490 | m103 / 4,186 |
| Peak share / current energy at opening | 1.98217 / .96063 | 1.31713 / 100% |
| Retained dispatch at opening | m150, .99156 | m102, 100% |
| Minimum peak share | .56361 at m126 | 1.08736 at m123 |
| Minimum current energy | .80246 at m126 | .72736 at m127 |
| Months below 95% current energy | 5 | 8 |
| Generator units / events | 1 Diesel / 1 | 31 Small Steam / 7 |
| Generator spend | about $2.0M rounded quote | $4,212,507.00 actual |
| Purchased generator land | exact spend unavailable | $33,219.07 |
| Substation pass / opening | m116 / m162 | m119 / m175 |
| Population at m180 | 8,218 | 10,471 |

Final steam orders occur at pre101–129, May 1908–September 1910, inside
the sourced order window. All 31 stand by m171. Initial order cost is
$1,120,268.08 with no purchased land. The revision reduces the first
proposal's 40 stations/$5.62980M/12 low-energy months to **31/$4.21251M/8**;
the Substation opens at m175 rather than m177. Against main, delivery and
procurement remain adverse by the ratios/counts above, despite earlier
generation and more population. The historical cutoff is not used to
pretend that a May 1908 order happened in calendar 2026.

At m89/m90 the final city retains the same populations 2,909/2,965, peak
shares 2.15712/2.13683 and 100% current energy as main. Actual passes still
hold three wanted steam units, order zero with `Cut.BUILDERS`, and protect
Elementary; m90 also holds GRID for Elementary. At m120, peak share is
1.59345 and current energy 100%, versus main's .57117/.87430. Both rows'
retained dispatch is m119 at 100%; those flow dates are kept distinct.

★ **Leave the remaining inherited wire/crew coordination to PW9-G.** Final
actual held strings protect College's quoted first completion at
pre115–118, then GRID orders at pre119. This final path does not reuse the
first proposal's pre117–118 debt-cut claim. Ordering is three months later
than main and construction lasts 56 rather than 46 months. At native
dispatch m127 (retained at m128), 21.083 MW is offered, but the 10 MW
wire ceiling allows only 7.2 GWh generated / 6.84 GWh delivered against
9.899 GWh gross asked; steam offers 11.083 MW and generates zero. Similar
retained wire-bound flows remain at m151/m163. The power curve alone does
not establish delivered service. These inherited protection/allocation
rules remain; no fixture, staffing figure, debt cap or construction point
was weakened. [PW9-G](PW9-G.md) owns the separate future GRID date/delivery
coordination finding, with no known-red waiver. It is queued in this ship
file rather than assigned to the unrelated FC2-C, SW1, MKR1 or PB1 rows.

Evidence: `codex-PW9-r2-measure/target/PW9-measure/r2-README.txt`,
`r2-after-{1900,today}-*`, `r2-comparison-*`, `r2-cost-totals.csv`,
`r2-actual-pass-evidence.txt` and full source/runtime/observer manifests.
All 333 prior artifact files were preserved byte for byte. Other sessions'
ReloadCheck overlapped; timings are not an isolated-machine comparison.

## Remaining gate work

The orchestrator accepted the mends in `20261126-005746-437`: P1s met,
including $10.507M/three diesels and the 1900–1914 window. The final modern
city's eight low-energy months versus five ships because the delivery
cause is inherited. PW9-G is **queued**, not pending Jerus: its law is that
a utility builds no more generation than its wires can carry, ordering
the Substation and lines with it; the 31-station/10 MW condition is the
red to cause. The ship file and brief reflect that instruction.

The authorized final 80-run ensemble completed on gatebox at source
snapshot `8c0bb1f9805b4d1c36f75759cabec6ccdd15e0d7`, against
`0.9.208.md`, eight runs at once. The Bash background launch had a
7,200-second timeout; its own exit file is **0**, the launcher has ended,
and all 80 completed CSVs, score and provenance were copied home. There
are no missing or short runs. The read-only helper independently checked
all 102,352 monthly rows, expected endpoints and CSV/log/time records;
the embedded baseline JSON equals `summary.json`. Local fallback was
guarded before any JVM could start and was never used. The box used
OpenJDK 21.0.13 and its earth/world data, with the baseline's data-path
flags removed from the submitted `JAVA_OPTS` as instructed. Data-copy
equality is the GB3 handoff guarantee; this run recorded paths and sizes,
not fresh content hashes.

| Criterion | 0.9.208 baseline | PW9 final |
|---|---|---|
| Hard M1/F1 | 10/10 PASS | 10/10 PASS; zero failed/unread money-audit months and zero findings in every policy |
| Overall criteria | 23/26 | 23/26; one retired criterion remains excluded |
| AUTO2 takeoff T1 | 16/16, median m524 [447–687] | 16/16, median m512 [447–917] |
| AUTO2 final population T2 | 6,240,014 median | 4,992,121 median, PASS; about 20% lower |
| ZERO_HOT H2 | FAIL, 0.67 percentage points over twin | PASS, −0.37 points, 14/16 below the second-half excess |
| HIGH final population T2 | PASS, 182,116 median | FAIL, 116,844 median; below 75% of baseline |

★ Keep both changed informational verdicts and the lower population
figures in the baseline. EN1/EI1 makes M1/F1 the shipping criteria; no
threshold, seed, policy or finding was altered to improve this score.
The published baseline is OA1's `dc7f9cc7d`, before inherited FB11 ground
funding changes in LongPlaytest on current main. PW9 does not edit that
player or the ensemble driver. These are the required published-baseline
deltas, not a controlled PW9-only attribution; the HIGH decline has no
proved isolated cause or invented queued-red owner. The native main/PW9
procurement comparison above is separate evidence. The helper's complete
evidence report is `target/PW9-ensemble-report.txt`.
The complete scored artifact, with only its heading and introductory
version changed to the publication token, is [PW9 ensemble](../ensemble/PW9.md).
Evidence is `target/ensemble/PW9/{score.md,baseline.md,summary.json,
snapshot.txt,progress.txt,times.txt}` and the 80 CSVs/logs; launcher output
is `target/PW9-ensemble.log` and `target/PW9-ensemble-exit.txt`. The remote
run is `/home/jerus/cbs/tools/8c0bb1f9805b4d1c36f75759cabec6ccdd15e0d7/target/ensemble/PW9`.
Only queue/record files differ from this source snapshot. Origin/main is
still `3bf83d20e`, already merged, at the post-run fetch.

The pick remains ALL257. The orchestrator allocated the dry run in
`20261126-013333-274` on pushed `f52c6b9ce1f826b10be801bb1e0ba3b1cab0750a`.
Main was fetched again and remained
`3bf83d20e1f76d278cc27f8e8e785756b972f3fd`, already merged. The Bash
background launcher used timeout 7,200 seconds. It waited behind RES3's
ensemble, acquired the box, compiled, and completed the whole suite.

## First full dry run: EssentialWorks findings to mend

Run `/home/jerus/cbs/runs/cbs-gate-PW9-AG6VUQWA` tested the two commits
above; `gate exit 1` followed suite exit 4 at 2026-11-26 05:57:10 UTC.
The run was copied home to
`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-PW9-AG6VUQWA` (the wrapper's
`/tmp/cbs-gate-PW9-AG6VUQWA`); root launcher
`target/PW9-dryrun.log`, exit file `target/PW9-dryrun-exit.txt` and its
tool process all ended with exit 1. This failed run is evidence, not a
reuse approval, and no gate-run comment was attached to the ship file.

All eight unnamed lines are EssentialWorksCheck's six failing assertions
plus its exit/count lines: affordable one-station cash order, whole-order
opening/horizon, power ordered past the pending diesel's deadline, the
earlier Oil-Fired choice, loaded/unbroken later orders, and earlier wind.
They have no known-red exemption. The unchanged harness's scarce-cash
premise holds: available 103.95, one Small Steam Station 102.92, next
plant 1,075.84; the planner nevertheless returns a held nine-coal order.
The initial full-plan candidate-filter hypothesis was traced and
disproved by the corrected diagnostic below. The old
lead-plus-horizon pin and pending/wind fixtures also need the new dated
law checked against native monthly need; no fixture has been moved.

LongPlaytest including conservation, SaveFile/SaveSlot/ReadPath/Replay,
Reload/Determinism, Materials and PowerPlants' 159 assertions passed.
Inspected Map/Plan/AutoBuild reds match existing PX2, PW2-B and FS12 rows.
The read-only helper owns the diagnosis and one authorized named
EssentialWorksCheck with temporary diagnostics in its own worktree,
after the gate has ended. Root runs no JVM alongside it. One accidental
SSH grep reader was closed by its verified own PID, with gate and other
jobs untouched. The new EssentialWorks findings stop readiness and are
being mended, as mailed in `20261126-015902-267`; the marker stays.

## EssentialWorks diagnosis after the first dry run

The helper's final accounting is ALL257 picked, 254 run: 236 passed and
18 red harnesses. Seventeen reds are already named on main; only
EssentialWorks is NEW. Globe, ShortSelling and OrderSearch are the three
unselected nightly checks, not failed launches; all nine display starts
ran. Three observations and three soft timings do not change the six
unexempted EssentialWorks assertions. Exact evidence is
`target/PW9-dryrun-report.txt` and the kept run's manifests,
`checks.complete`, suite/reds/unnamed and per-harness logs.

Two individually authorized, serial named EssentialWorks diagnostics in
the helper worktree reproduced the same six assertions (wrapper status
4); both fully ended. The first diagnostic's display-name filter missed
the fixtures and emitted no trace. The corrected run used unconditional
identity tags and read-only candidate/date prints. At each start commit
charge was 72%, about 15 GiB free and 7.5 GiB RAM available, meeting MG1.
All diagnostic prints and regenerated-map side effects were restored;
the helper ended with a clean tracked tree and no child JVM. Evidence:
`codex-PW9-dryrun/target/PW9-essential-diagnostic-{one,two}.log`.

The decisive cash trace finds a feasible 3,015-unit Small Steam full plan
with a 733.97-month quote. It is **not** rejected as infeasible. Instead
`timely()` vetoes it for the slightly cheaper future Diesel full plan
(cost/value 0.0872665 versus 0.087811). That future row is not orderable
today and survives the cash fallback without the corresponding budget
screen. The coding slice independently verified its first-unit quote as
1,194.30 plus zero ground, legally unfundable against 103.95. The current
15-month steam increment is fundable from 103.95 cash; the veto turns that
legal incremental step into an all-held result.

★ Apply the same current legal budget screen to a future POWER row before
it can veto a fundable present step. Reuse the existing quote/screen;
do not forecast future revenue or duplicate the payment/debt rules.
Keep valid cheaper-future waiting, the lifecycle comparison and all dated
monthly/seasonal closure laws. A fresh coding helper is implementing this
bounded guard and causative harness mends; root keeps review and commit.

The pending fixture already ordered five Oil-Fired Plants at m37, before
its old probe/checkpoint m39. Six oil units on site then cover its next
candidate peak, so an empty later order list is the correct covered
control. Preserve that observation and the real DataSave/nonempty
one-by-one parity law by adding a continuation before the real order.
The wind fixture has no forecast physical gap through m240: at the next
dated peak m42, physical demand 203,244 is below supply 229,402, while
reserve need 252,908 is above it. Steam 39/15 months closes the reserve;
Wind 2/36 months does not. Keep this scenario as its actual seasonal
control and cause a separate real early-wind need. The old exact
lead-plus-horizon pin must become the actual-count native frontier law.
No original fixture has been hidden, conservation/save law weakened, or
new known-red row proposed.

## EssentialWorks mend and focused verification

Source commit `54d4b6cc683259d63b6662d86fa4e5dff88a8cce` changes only
BuildAdvice and EssentialWorksCheck; generated maps are in `48eeba3eb`.
★ The nine-line production change quotes a future POWER row's first unit
and reuses `AutoBuilder.groundFor`/`pays` before either timely veto. Ground
uses `LandManager.rehearse`, payment uses existing `canPay`/bond quotes;
these are pure feasibility reads. No new budget model, future revenue,
lifecycle value, non-POWER comparison, price, stock, payroll or save field
is introduced. Source review checked the bounded guard and all retained
controls; both applied source files are SHA256-identical to the checked
helper tree, whose base differed from root only in notes.

The unchanged scarce-cash premise now buys one 102.91966621238079 Steam
unit of the 3,015 wanted, with zero borrowing/debt change. The future
Diesel's 1,194.30 first unit fails the same legal screen. The advice-parity
control's actual five-Coal quote is 393.588 months, with native seasonal
peak/binding month 402; every intervening native month closes and exact
card/automatic count/value parity remains asserted.

The original pending fixture and m39 save checkpoint stay intact. Added
observation proves Oil5 at m37, before that checkpoint. At m39 Oil6 and
Diesel36 are pending; the candidate peak +400 counts Oil6/Diesel0,
484,446 supply against 395,522 reserve, and no extra power order. A new
native mill project after the unchanged real save/load adds 77,000 kW
at opening +6, zero the month before. Both continuations then order Oil1
at m40 and Gravel13 at m41; the complete nonempty order sequences are
equal, one by one. No draw/history restoration equalizes the saved twins.

The original interim wind scenario retains its actual reserve-only
Steam39 choice and first-completion protection. The additional control
plays normally into 1915, so Steam retires by its real offer window;
other eligible plants, native capacity/cost and native dispatch remain.
At absolute m181 it orders Wind1 before the first physical loss at m186.
Wind opens at m217, before coal m220 and the already-covered peak m223;
at its opening it removes the native 24,674 kWh loss exactly, with
positive used/avoided value and an advanced saved deadline. This asserts
an early order and relief before coal, not commissioning before m186.
The test helper's initial kW/kWh error was corrected using native
`unserved[half] * halfHours(half)`, preserving the intended energy target;
no model size, date, threshold or zero-closure law was tuned.

Final serial wrappers in `codex-PW9-mend` all ended **0**:
EssentialWorksCheck, PowerPlanCheck, BuildAdviceCheck, WorksFinanceCheck
and `--fast` (Stale zero firm). PowerPlan's existing cheaper-future,
whole-month/seasonal, pending-count and falling-count controls stay green.
Root applied the reviewed patches, verified both source hashes and ran
its own complete `--fast`, also **0** (`target/PW9-postgate-fast.log`).
Per-name logs, hashes, failed prototype runs and source/test dispositions
are in `codex-PW9-mend/target/PW9-mend-report.txt`,
`PW9-mend-patch-sha256.txt` and its named log paths. Both helpers and all
their wrappers/JVMs ended before this next diagnosis was started.

★ Reject restoring real derived state after load to make a prototype
pass. An attempted earlier m37 save exposed a synthetic consumption
setter not in PowerState; restoring only that draw still left Oil7 versus
Oil5 and different remits. That residual is **not exonerated** by the
passing original checkpoint. The attempted restoration is absent from
the committed source. Failed evidence and exact source leads remain in
`codex-PW9-mend/target/PW9-mend-early-save-finding.txt`. A fresh bounded
PW9-save37 slice now diagnoses fixture-only versus native save/advice
fault, with at most two named probes and no model/save fix authority.
Readiness remains provisional until that finding and final measurements
are settled; this failed full gate is not reused.

## Bounded disposition of the earlier-save prototype

PW9-save37's isolated clean probe explains the specific m37 mismatch as
stale fixture-derived state after eight instant depots, not an established
native save defect. Depot stock is 5 then 13, but cached jobs remain
2,814 against 3,076 derived from stock (+183/+79 by qualification). Roads
are 5,526 versus 6,246 and built area 20,841,000 versus 21,321,000. Load
derives the new stock. After replaying only the synthetic draw, the same
448,876.9863 demand readout still meets different supply: 229,401.8759
live versus 193,725.7322 loaded, from the stale service fills. Thus equal
synthetic consumption alone never established equal model inputs.

The raw Oil5@m37 versus Oil4@m39 and draw-only Oil5 versus Oil7@m37 are
retained, reproduced cleanly and explained by that violated premise.
Trend/history, fixture holds/date and the first-unit quote agree. This
does not justify restoring a loaded cache from the unbroken twin.
★ Treat this particular prototype as invalid fixture input, preserve its
evidence, and keep the committed original m39 real DataSave plus genuine
post-save power-load/order law unchanged. No model/save fix, format bump,
new queued bug or known-red exemption follows from an unestablished
native defect.

Limits are explicit: the coherent-history/normal-services comparison
matched all observed pre-pass fields and nonempty general-service orders,
but its **power-order-nonempty premise failed** in both twins. Its
whole-stack `dueIn` also over-advanced four pending mills and does not
prove ordinary reachability. It is supporting evidence only, never a
claim of general native save correctness or a green power assertion.
The already-committed m39/post-mill case supplies the actual nonempty
power-order equality. A further ordinary early-power save test remains
the report's identified experiment, not a claimed failure to waive.

One temporary compile failed, then a generator accidentally registered
the probe three times, launching duplicate children against shared
private paths. All exited; **that run's evidence is discarded**. The
accepted diagnostic has one registry entry, fresh private roots and
`-Dcbs.parallel=1`. Its wrapper ended 4, retaining the negative controls
and failed power premise; maps/Stale had zero firm findings. The helper
restored its tracked tree and source hashes and ended with no jobs.
Full bounded source/field evidence and limitations are in
`target/PW9-save37-report.txt` and `codex-PW9-save37/target/PW9-save37`.
This extra diagnosis changed no shipped source or save law.

Origin/main then advanced to `2560ada85`, solely DM5's queue assignment;
merge `c4540c978` brings it in, with no production/resource/pom diff.
The orchestrator's `20261126-030048-184` requires a 05:00 laptop restart,
no new turns after 04:30, and commit/push/end at a stopping point by
04:30. The reviewed code/records were pushed at `c4540c978` with the
provisional marker. Final same-platform default-trace and native power
measurements now run in a fresh bounded slice; global jobs require their
allocation and enough pre-restart time, otherwise resume after boot.

## Final guard measurements and pre-restart stopping point

PW9-finalmeasure verified all 689 production-source hashes against model
`54d4b6cc6` before adding its observers. Both runs used laptop JDK
21.0.4, UTC, seed 0, `-Dcbs.parallel=1`, the named main earth/world files
and unique private GameFiles. All 2,568 compiled/resource hashes matched
between the current historical and modern runs. The older 333 artifacts
and an additional 250-file snapshot were preserved unchanged. Other
RES3/QR1 JVMs ran; timing is not an isolated-machine comparison.

The default current model's months/events are byte-identical to the
pre-guard final native artifacts through **m440**. The modern profile's
months/events are byte-identical through **m180**; its pass text differs
only by one LandParcel runtime identity at pre107. All requested crew
passes are exact. This is bounded trace equality, not private-state or
full-2,400-month equality.

| Final guard native result | 1900 default, through m440 | Modern equipment, calendar 1900, through m180 |
|---|---|---|
| Generator orders | Three Diesel in two events; no Steam/Oil | 31 Steam in seven events |
| Actual generator cash | 10,507,089.591961; land 0 | 4,212,507.000610; land 33,219.072332 |
| First order | Pre417/visible418, pop5,446, quote30 months | Steam9 pre101/visible102, pop3,995, quote22.1 months |
| First commission | m435; second Diesel2 ordered pre429/visible430, quote26 | m103; all31 standing by m171 |
| Minimum firm power ratio | 0.915599882 at m434 | 1.087359861 at m123 |
| Minimum delivered-service ratio | 0.992632506 at dispatch439 | 0.727358256 at dispatch127 |
| Dispatch months below95% energy | Zero | Eight:124–129,151,163; main five |
| Substation | Pre409, open440 | Pre119, open175; main116/162 |

Each retained PowerView D/asked/generated/delivered/unserved reading
names its **previous dispatch month** separately from closing utility E:
historical closing m439's minimum is retained at observation440; modern
dispatch127 is retained at observation128. Historical population, energy
and fleet still equal main through440; only six firm/P rows differ,
first at435. No new service improvement is claimed from the budget guard.
Actual Substation cash is 45,227,447.200645 historical and
27,038,110.096276 modern, land0. Generator plus grid totals including
land are 55,734,536.792607 and 31,283,836.169219 respectively; do not
compare these combined figures to the generator-only $10.5M baseline.

The modern crew story also remains exact: m89/m90 wants Steam3, orders0,
Cut.BUILDERS protecting Elementary; m90 GRID also waits for Elementary.
Pre115–118 GRID protects College, and119 orders Substation. Actual
Steam orders at116x2,119x1 and120x2 remain in the pass CSVs. PW9-G's
31-station/10MW finding and its queued law remain unchanged.

The extra canonical default **LongPlaytest did not pass**: compile55s,
then child exit1 at its unchanged 1,740-second AllChecks limit, wrapper
exit4, with last native event pre2051. It never reached2400. The exact
NEW red is `LongPlaytest | FAIL: timed out after 1740s`; no exception or
completed model-assertion verdict establishes a cause, and **no exemption
or queued-red attribution is invented**. The full same-platform
pre-guard control was not started because it could not safely finish
within the pre-restart window. Equality beyond440 and final full
validation remain pending. This extra partial run does not replace the
four green focused checks or the required final global checks.

The second, separate native modern measurement reached180, exit0 in
29.0s, saved0; it is measurement only, not full LongPlaytest validation.
Both complete wrappers/maps had Stale0firm/209soft. The helper restored
all tracked observers/control/maps and all689 source hashes, verified
its owned temporary processes gone, and ended with no JVM/wrapper/job.
Detailed proof is `target/PW9-finalmeasure-report.txt`; full raw partial
trace, CSV windows/events/passes/flows/costs/crews, commands/statuses,
source/class/data/JDK manifests and restoration proof are in
`codex-PW9-finalmeasure/target/PW9-finalmeasure`.

★ Keep the first80-run result as provenance of **pre-budget-mend source
8c0bb1f98** only. It is not a claim that final guard54 has passed a new
ensemble. The 03:45 ensemble-start cutoff has passed, so final-source
ensemble/full validation and a newly allocated dry run continue after
`laptop back`. The failed AG6VUQWA dry run is never reused. The readiness
marker stays; the four focused names and fast are green, but the batch
is not ready to publish while this final validation remains incomplete.

## Orchestrator's post-restart continuation

Instruction `20261126-041627-233` settles the laptop timeout disposition:
the 1,740-second run under laptop load is GB6's class (FB11 had the same),
and the box's full dry run must run LongPlaytest whole and judge it. Do
not chase another full PC run or pre-guard PC control. This adds no
known-red exemption and weakens no assertion. The orchestrator requires
no further review: the funding guard mends PW9's own dry-run reds and
leaves the measured figures unchanged.

Wait for the explicit `laptop back` signal, merge origin/main before
the final ensemble, commit/push, and run that ensemble against
`0.9.208.md`. Then ask `PW9 dry run?` and wait for the allocated slot.
Keep the provisional marker until the final checks and records are done;
the failed AG6VUQWA run supplies no reuse approval. This pre-restart
turn only records the instruction, commits/pushes, and ends before04:30;
it starts no helper, JVM or global job.
