# LB2 - Rankings: the board (0.8.82, 2026-10-31)

The batch's record. The study's LB2 row (`docs/studies/spec-leaderboards.md` §11) with §2-4 (the moneys, the pivot, the
boards), §6's city side, §7 (the marks), §9 (the screen) and Jerus's D1, D5-D16 (2026-10-21): the Rankings rail tab
after History, its two boards Real world 2025 (LB1's `world-2025.json`) and Your world (the game's own nations, WN1).
Lane B's second batch of the night line, after LB1. Opus wrote the two briefs, reviewed and committed; two Sonnet 5.5
slices at xhigh wrote the model and its harness (A) and the screen (B). Nothing in the month reads it: the default
playtest's trace cannot move, so no ensemble (EN1).

## 1. What it is

- **`CityMeasures`** (model, pure): the city's figure for **51 of the 70 measures**, from its `HistorySave` and three
  figures a history does not keep (`Extras`: the land it owns, the people in each age band, the wage floor in today's
  money). Money in whole units of the city's money (the model counts thousands: `THOUSANDS`), a flow a year (the last
  12 months, scaled 12/n in a city younger than a year), a percent a percent. A measure it cannot make is absent, never
  0. Each figure carries a few words on how it is made (the drawer's line). The life table is here.
- **`Rankings`** (model, pure, the `*View` manner): `City.of(Game)` off public getters only; `table(...)` one measure's
  ranked board; `around`, `yourSize`, `pricesLocked(Why)`, `sentence`, `display`, `drawer`.
- **`ui/RankingsScreen`**: the tab (§4). **`RankingsCheck`**: the harness (§5). `Icons.TROPHY` (Lucide's, ISC);
  the tab, `tabFor` and `areaOf` in `UserInterface`; the nation's card's "All its measures" door in `MapView`;
  Rankings in `UiProbe`'s screens at History's budget.

## 2. The pivot and the boards, as built (§3, §4)

- **One pivot, then display.** A money figure becomes US$ of 2015 once and is ranked there; Money only changes how it
  is shown, so it can never reorder a board (the harness: 30 of 30 boards in one order across the three moneys).
- **The city:** this month's rate `X ÷ fx ÷ P`; PPP `X ÷ parity ÷ P`; constant `X_real ÷ fx₀`, where P is
  `WorldEconomy.getPriceLevel()` (the game US$'s level since founding, 1 at founding: D1), fx₀ is
  `Denomination.money(OPENING_RATE)`, and X_real is the same months each over that month's price index. These are the
  getters and the division WN4's `Game.realGdp2015Usd()` uses on its branch, so WN4 changes nothing here: once the
  game's US$ follows the US path, P does too.
- **Real world 2025:** a current-US$ figure of year y times CPI-U 2015 over CPI-U y; constant takes the `.k` figures
  (household spending by gdp.k ÷ gdp); PPP the `.ppp` figures (household spending and wages by gdp.ppp ÷ gdp). Wages
  have no constant series and show this month's rate under Constant, and the sentence says so.
- **Your world:** the nations at the city's year on D15's ten. This month's rate:
  `output × [P_n(y) ÷ P_n(2015)] × [rate_n(2015) ÷ rate_n(y)] × [P_US(2015) ÷ P_US(y)]`, the world's own US prices;
  constant: the output; PPP: that over the price level, which is the file's 2025 price level moved by the path's
  relative prices and rates since (none published: 1 and ○, D13). City regions: the nation's figure times the 2025
  share of people or output, every figure ○.
- **Display:** real US$ as is; game US$ × P (× 1 under Constant); the city's money × P × fx (× P × parity under PPP,
  × fx₀ under Constant). So the city's own figure in its own money is the one the rest of the game shows.
- **Ranking:** arrow DOWN ascending, UP and NONE descending; ties share a rank and the next skips (1, 2, 2, 4), by name
  within a tie; a place with no figure is no row.

## 3. The city's measures (§6)

51 at 44 months (Danzik, the harness's city: GDP per person 77,554 D$ a year, the average wage 44,193, Kaitz 0.95,
price level 1.18). growth_10y joins at 132 monthly months. The rules, each against the series' own unit where
`recordMonth` writes it:
- **The book's definitions where they differ from the brief's:** unemployment over the labour force (the workforce
  less students and prisoners, `YearBook.unemploymentTail`), the average wage over posts filled (`YearBook.averageWage`).
- **Deaths from the six bands' dead added**, not the `deaths` series, which rounds each month to whole people and reads
  0 for a village's 0.4 a month. The orphans' and the unhoused deaths are inside the bands (the harness holds the sum).
- **Life expectancy:** a period life table over AgeBand's six bands, each band's year of deaths over its people now, a
  flat hazard within a band, the last open (a flat hazard m gives 1/m exactly: the harness). Labelled "computed".
- **On the city's bands:** old-age dependency is 70+ over 18-69 (the study's "seniors over adults"); child deaths
  are under 6; aged 65+ is left absent (the bands break at 70).
- **Police added:** `policeCoverage` × `Crime.FULL_OFFICERS_PER_100K`, officers per 100,000, capped at full coverage.
- **Absent, each with its reason in `CityMeasures`' javadoc:** participation, tertiary, yield_10y, debt_foreign (lane A
  is rebuilding the city's foreign-money debt), interest_revenue, credit_rating, bad_loans, stock_market, hh_debt,
  aged_65, sharing, price_income, electricity_pp, water_pp, renewable_share, cars_per_1000, transit, oil.

## 4. The screen (§9)

- **A head that stays** (as City History's): "Rankings" with the money-blue swatch and the (i) (the three marks, the
  bridge, the data build's date and counts); the city's name, month and people; the four controls (Board, Money,
  Prices, Convert at) as chip strips; the sentence. **Under it, scrolled** through `ui.scrolled` (the shell keeps its
  position across the month's redraw and a click): the measures column (pinned first, then the ten groups, folded to
  the shown measure's group until "Browse all 70"; a pin on each line; Your world's other 60 greyed), the table
  (filter chips with counts: All, Nations, Cities, Your size; Full list or Around you; rank, kind, name, of, as of,
  value, bar, mark; your row raised with the accent edge; "lowest ranks first" on a DOWN measure; the legend with
  "N of M figures here are the game's estimates"), and the city's card (rank of N, above and below you, the gap to pass,
  your ranks on the pinned measures), with the drawer above it when a row is opened (all 70 measures by group, Real
  world 2025 with its mark and source beside Your world's). Wide, three columns; narrower the card goes on top; narrow,
  they stack.
- **It opens on** GDP, the full list, real US$, this month's rate, per year (D9). The city is made once a month.
- **The rows are painted, not nodes:** a node per row (259 rows, some twenty nodes each) redrew in 142 ms, 119 of them
  the CSS pass; painted on canvas tiles of 40 rows the page redraws in 17.9 ms (UiProbe at month 24: first draw 88 ms;
  at 50x 10 months a second, frame p95 42 ms, max 129; History's p95 45). A row's click and hover are found by its
  height. The cost: no tooltip on a row's mark (the legend and the drawer say the same).
- **The map's door:** the nation's card has "All its measures ›": the map is put away and the tab opens on Real world
  2025 with that nation's drawer open. Not clicked on a live expanded map (LB2-C); `openOn("JPN")` was exercised.

## 5. The harness, `RankingsCheck`

Eleven sections, 130 checks, 3.3 s, on the full world file and a scratch city played to month 45 (`GameFiles.scratch`,
the playtest's probe city), and a fresh founded one for D1: (1) the pivot's identities (the city's figure back in its
own money; Money never reorders, both boards); (2) at founding real US$ equals game US$; (3) Constant holds a steady
real figure while the rate moves; (4) Your world's rows equal `Nations`' figures on both paths at 2012 and 2050,
Historical's the world file's to 2025, regions their nation's × share, exactly D15's ten; (5) lower-is-better ascending;
(6) ties share a rank (the real file with three figures set equal); (7) no figure, no row (one removed), and a D10
measure has the city alone; (8) the real rows' pivot (a 2025 and a region's 2021 figure, `.k`, `.ppp`, by gdp.k and
gdp.ppp); (9) Around you and Your size at the top, middle, bottom and off the board; (10) the city's figures against
the history's own readers times `THOUSANDS`, a six-month city, a folded history, the life table; (11) the lock and the
sentence.

## 6. ★ Calls taken, on the recommendation

1. **Prices is shown, and locked while no saved city is on the board** (always in LB2), its tooltip saying why. Why:
   the study's lock rule is "locked where it moves nothing"; LB3's saved cities unlock it with no change to the screen.
2. **A measure with no real figure (D10) shows your city alone** and a line saying the real world publishes none; the
   drawer's real column shows "—". Why: 258 rows of dashes would bury the one row that ranks.
3. **The US price level is `WorldEconomy.getPriceLevel()`**, the getter WN4's own real dollar reads. Why: it is the
   game US$'s level whatever drives it, so LB2 needs nothing from WN4 and WN4 needs nothing from LB2.
4. **Your world's PPP is relative PPP from 2025's price level** (the file's), moved by the path's prices and rates.
   Why: the world file carries no PPP, and this keeps the US at 1 and every nation on its own path.
5. **City regions on Your world rank on people, growth, GDP, GDP per person (the 16 with one in 2025), area and
   density**, not real growth or inflation. Why: with its shares held a region's growth and prices are its nation's,
   which would only repeat the nation's row.
6. **The city's figures follow the book where the brief's rule did not fit the series** (§3): unemployment over the
   labour force, the average wage over posts filled, deaths from the bands. Why: they are what the rest of the game shows.
7. **Old-age dependency on the city's own bands** (70+ over 18-69), aged 65+ left absent. Why: the study names the first;
   the second would be 70+ under a 65+ name.
8. **The pinned measures** start as GDP, GDP per person, real growth, people, unemployment and inflation, held in the
   screen and not saved. Why: the study names no default; a save field for six keys is not worth a format change.
9. **The screen is drawn on the rebuild path, its rows painted**, not kept (UP4's `Kept`). Why: painted rows already cost
   the page no CSS; KP7, which retires the rebuild path, takes the head and the columns with the rest.

## 7. Seams for what comes next

- **LB3 (your saved cities):** `Rankings.Kind.SAVED` exists and ranks like the city; `pricesLocked(.., anySaved)` is
  the Prices toggle's switch; `CityMeasures.of(HistorySave, Extras)` runs on a saved city's history file as it runs on
  the live one (it reads only the history and `Extras`). What LB3 must bring: a saved city's fx, parity, P, fx₀ and its
  `Extras` (land, the six bands' people, the wage floor) from `SaveHeader`, since History keeps fx and parity but not P
  or the bands; "at your age" needs `CityMeasures` read at an earlier month (today it reads the last). The screen adds
  the saved rows and the "Your cities at: now · your age" chip; Compare on the main menu calls `openOn`.
- **KP1 (History kept in place):** RankingsScreen takes nothing from HistoryScreen but its `tip()` helper and the shape
  of its frame (copied, not shared); the rail's tab sits after History's and touches none of its code.
- **WN6 (the nation's page):** the nation's card's door is one line in `MapView.fillNationCard()`, after the grid.
- **After WN4:** nothing to change; `Game.realGdp2015Usd()` computes what `Rankings` does for gdp under Constant.

## 8. Checks

With both data files named as the gate names them (2026-10-31): `./batch-check.sh --pick` **exit 0** (8 harnesses;
its "review needed - threading: ui/UserInterface.java" read: the edit there is a field, a tab and two switch cases, and
the screen reads the game on the FX thread as every screen does); the full suite **exit 3**, nothing red but main's
recorded reds (PassThroughCheck, ScaleCheck: `docs/notes/gate.txt`), one timing figure SOFT and not counted
(SaveSlotCheck, side by side with lane A); maps regenerated; Stale 0 firm. RankingsCheck green: 130 checks. WN4
(0.8.81) then reached main and was merged in (two one-line conflicts: AllChecks' list, README's count, 159); on the
merged tree `--pick` **exit 0** again, one timing figure SOFT (MapCheck). UiProbe (`24 2`): no exception, the Rankings line above. **One in, one
out** does not bind: no script, check machinery or rule a session reads changed (CLAUDE.md's one edit is a count, 35
to 36 files of the interface, which Stale asks for).
