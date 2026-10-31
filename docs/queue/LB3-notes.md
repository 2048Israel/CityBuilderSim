# LB3 - Rankings: your saved cities ({next}, 2026-10-31)

The batch's record. The study's LB3 row (`docs/studies/spec-leaderboards.md` §11) with §3 (the pivot and the display
for saved rows), §5 (your saved cities), §9 ("Your cities at", the rank history) and Jerus's D2, D3, D5 and D12
(2026-10-21): the other slots on the Rankings board, per year by default or static, now or at your age, the city's
rank over the years against Real world 2025, Compare on the main menu, and the manual's page. Lane B's third batch of
the night line, after LB1 and LB2. Opus wrote the two briefs, reviewed and committed; two Sonnet 5.5 slices at xhigh
wrote the model and its harness (A) and the screen (B). Nothing in the month reads it: the one model change is a
history series recorded at the month's end, so the default playtest's trace cannot move, and no ensemble (EN1).

## 1. What it is

- **`SavedCities`** (model, pure, no JavaFX, never writes): slots 1 to `GameFiles.SLOT_COUNT` of a `GameFiles`, the
  autosave (slot 0) left out as a copy of a numbered city. Each slot's header (`SaveHeader`, six more fields with
  DataSave's names, decoded by the classes that own them: `WorldEconomy`, `Denomination`, `PopulationCohorts`,
  `LandManager.km2`) and its history, parsed as `Game.loadHistory` does and cut at once to the 56 series the board
  reads (`HistorySave.slim`, `CityMeasures.SERIES` plus fxRate, fxParity, usPrices): a 26 MB history keeps about a
  fifth of its numbers. A snapshot is keyed per slot by both files' size and modified time, so `refresh` reads again
  only a slot saved since. Left off and counted: an older build's save, a newer one's, a damaged one, one with no history.
- **`Rankings`**: `Saved(slot, city)` rows of `Kind.SAVED`, id `saved:<slot>`, ranked by the one `rank` function;
  `table` and `drawer` overloads taking them; the sentence's saved clause; `cityAt` (a City as a history stood at a
  month, the one place one is built off a history); `rankOverYears` and `RankPoint`; `TIE`.
- **`CityMeasures.at(h, x, month)`**: every figure as of a month, the entries after it ignored; `of` is `at` the last
  month, figure for figure. `Extras.UNKNOWN` for a month whose land and bands the history does not keep.
- **`HistorySave.usPrices`**: the game dollar's price level each month (`WorldEconomy.getPriceLevel()`), full
  precision, beside priceIndex, out of the reform's rescale (US prices are not the city's money). YearBook's line.
- **The screen**: see §4.

## 2. The pivot for a saved row (§3, D2)

- Each save is its own world, read in the units of its last save: its rate and parity (its history's last month, or
  the month at your age), its P (usPrices; a history from before LB3 falls back on the header's world economy for its
  last month), its founding rate (its `Denomination` × `OPENING_RATE`). A reform rescales the history and the state
  together, and the pivot divides the unit out.
- Money figure X: this month's rate `X ÷ fx_i ÷ P`, PPP `X ÷ parity_i ÷ P`, Constant `X_real ÷ fx0_i`. P is the
  saved city's own under static, and the ACTIVE city's under per year (Money not real US$): the display multiplies by
  the active city's P, so per year shows `X_i ÷ fx_i`, its own year's game dollars, and static `usd15_i × P0`, the
  study's two rows. Prices locked (real US$, Constant, not money) reads as static.
- **`Rankings.TIE`** (1e-9): two figures within a billionth share a rank. A city saved before and after a reform by
  100 differs in its last digits only; side by side, with exact equality, a tie split into ranks 5 and 6.

## 3. At your age, and the rank line (D3, D12)

- **At your age** reads each saved city's history at the active city's month (months since founding, one calendar for
  every city). A city that never got there is no row; the line under the table names it and its last month. Its
  rate, parity and P are the series' at that month; its figures `CityMeasures.at` with `Extras.UNKNOWN`.
- **The rank over the years**: the active city at each December of its history, built by `cityAt`, ranked by `table`
  on Real world 2025 (no saved rows, no Your world), at the Convert picked; a December with no figure has no point.
- **Why a new series**: the US price level P at an earlier month is in no save (WorldEconomy keeps a 13-month ring),
  and the standing rule says a flow the screen needs is saved, never rebuilt. A history from before LB3 reads it empty:
  at an earlier month its money figures are absent under this month's rate and PPP (Constant needs no P), never wrong.

## 4. The screen

- **Reading off the screen's thread.** `showRankingsMenu()` runs at every monthly redraw, and nine histories are up to
  200 MB of JSON, so `SavedCities.refresh` runs on one daemon worker ("rankings-saved-cities", as MapView's), once per
  entry to the tab (told from a redraw by the screen shown before `clearMenu`) and once for Compare; one read in
  flight; the snapshot comes back through `Platform.runLater` into a volatile field and redraws the tab only if it is
  still shown and something was read. The worker touches no node, no `Game` and none of the screen's state.
  `at(age)` runs on the FX thread when the snapshot, the month or Now / Your age changes.
- **The table:** a **Your cities** chip after Your size, with its count ("(reading…)" until the first read is back);
  **YOUR CITIES AT: Now | Your age** beside it while a saved city is readable; saved rows with Lucide's save icon,
  the name, "slot N" where a region shows its nation, the month of the figure, value and bar, no mark; a click opens
  its drawer ("Saved city · slot 2 · as of Jul 2004"). Saved rows are in All, Your size and Your cities, not in Nations
  or Cities (the real world's places). Under the rows, quiet lines: "Not at 5 years 9 months: Danzik (slot 1) stopped
  at Nov 2002." and "10 saved cities from older builds are left out."
- **Prices** opens while a saved row is on the board (under game US$ or the city's money, this month's rate or PPP).
  The tab opens on real US$ (D9), which locks it, its tooltip saying why.
- **The sentence** ends with the saved cities: "Your saved cities in their own year's dollars, as last saved." or
  "... at Arden's prices of 2050, at 5 years 9 months."
- **The city's card:** under the pinned ranks, **RANK OVER THE YEARS**, one point a December on one canvas, rank 1 at the
  top of the city's best-to-worst span, the first and last rank labelled, the years along the foot, and "Against Real
  world 2025, which holds still."; fewer than two points or a measure nothing real publishes is a line of words.
  Worked out on entering the tab and when the measure or Convert changes (about 18 ms a hundred Decembers), never on
  the monthly redraw.
- **The main menu:** "Compare ›" in money blue beside YOUR CITIES, only while a city is in play: it resumes the city as
  Continue does and opens the tab on Your cities.

## 5. The harness, `RankingsCheck`

LB2's eleven sections and seven new (12-18), 213 checks, about 6 s, on scratch saves only (`GameFiles.scratch`):
LB2's played city saved, played on a year and saved, reformed by 100 and saved again, plus fixtures for an older
build's save (its format set below `OLDEST_LOADABLE_FORMAT`), a damaged one and one with no history. (12) reading the
slots: slot 0 left out, the four kinds left off and counted, every file's bytes and time unchanged by a read, a second
refresh reusing every slot and re-reading one re-saved; (13) Prices moves only saved rows and never under real US$ or
Constant, per year `X ÷ fx_i` and static `usd15 × P0` in game US$, static boards one order in the three moneys;
(14) a ÷100 reform leaves every rank, alone and side by side (2,160 of 2,160); (15) at your age equals the history
file's month, GDP summed from the file, a younger city stopped at its last month; (16) one rank point a December, each
the board's own rank; (17) the slim history's figures are the full one's, usPrices equals the price level each month
and the reform leaves it; (18) `CityMeasures.at` at the last month is `of`. HistoryCheck: an old history reads
usPrices empty and fills from the end.

## 6. ★ Calls taken, on the recommendation

1. **The US price level goes into the history** (`usPrices`), not rebuilt. Why: the save keeps 13 months of it, and
   the standing rule ("a flow cannot be reconstructed... save it"); no format bump, an older history reads it empty.
2. **At an earlier month, what the history does not keep is absent, not guessed**: area, density, old-age dependency,
   life expectancy and orphans at your age and on the rank line. Why: the land and the bands are in the save only as
   they stood at its last month.
3. **A city's area is its dry ground**, the live city's too (LB2 had its water and sea in it). Why: the real file's
   area is the World Bank's land area (AG.LND.TOTL.K2), inland water left out, and a save keeps only the dry ground.
4. **`Rankings.TIE`, a billionth**: why in §2.
5. **The manual's Rankings page is written, not published** (`docs/queue/LB3-manual.md`). Why: the orchestrator's
   rule is a manual edition only at a milestone or when Jerus asks, and the page is 1.1 MB to republish; it goes in
   at the next edition (LB3-M under Pending).
6. **Compare is on the menu only with a city in play.** Why: the board ranks the city in play; with none, there is
   nothing to put the saved cities beside, and opening the tab would draw the city's shell over an empty world.
7. **The stopped city is a line under the table, not a row** (the study: "the row shows 'stopped at Feb 2031'").
   Why: a place with no figure is no row everywhere else on the board, and the harness holds that.

## 7. Checks

With both data files named as the gate names them (2026-10-31): `./batch-check.sh --pick` **exit 3** (18 harnesses;
nothing red but ScaleCheck, main's recorded red; its "review needed" read: saves, HistorySave's new series, no format
bump because an older history reads it empty and its months are absent, never wrong; threading, the screen's one
worker, §4). The full suite **exit 3**: nothing red but main's recorded reds (PassThroughCheck, ScaleCheck,
`docs/notes/gate.txt`), one timing figure SOFT and not counted (SaveSlotCheck, beside lane A). Maps regenerated, Stale
0 firm. RankingsCheck green, 213 checks. UiProbe (`24 2`, soft): Rankings drawn, first build 81 ms, no exception. No
ensemble: the trace cannot move (a history series recorded at the month's end; nothing in the month reads the board).
Jerus's real save folder was read for the formats only and is unchanged. **One in, one out** does not bind: no script,
check machinery or rule a session reads changed.

## 8. Seams and follow-ups

- **Jerus's saves today**: all ten slots are formats 29 to 47 (read 2026-10-31, read only), below the oldest this
  build loads (49, WN4), so the board reads none of them and says ten are from older builds. It fills as he saves
  cities in this build.
- **fxRate and fxParity are kept to four decimals** (`round4`): after a reform that leaves the rate near 0.02, the
  months recorded after it carry two or three significant figures. Lane A's to judge; the board reads what is kept.
- **The city in play beside its own slot** (LB3-D): a city loaded from slot 3 also shows slot 3 as a saved row, the
  same name, "slot 3", the city as last saved; at your age it nearly ties with itself. The game does not know which
  slot a city came from, and Game is lane A's tonight. Recommended: mark a slot whose founding (name and world seed)
  is the city in play's as "this city, as saved", and leave it out at your age.
- **KP1** (History kept in place) is next in lane B; nothing here touches HistoryScreen; `HistoryScreen.tip` is reused.
