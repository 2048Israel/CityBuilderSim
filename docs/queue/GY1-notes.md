# GY1 - the graph's GDP by the year, and debt to GDP

Jerus, 2026-11-20 19:4x: "i just want it so that in the graph, gdp can be shown yearly not monthly, sure real is yearly,
but not normal, basically i want to be able to compare gdp with public debt, and also perhaps a debt to gdp ratio on the
graph options, if possible as soon as possible". It goes ahead of the queue. One builder (Sonnet), no slices; no review
(nothing of money's books, saves or threading moves: `tools/pick.sh` says so).

## 1. What was built

- **`YearBook.gdpYear(h)`**: nominal GDP, a rolling year of it, `rollingYear(h.aligned("gdp"), h)` - the sum
  `realGdpYear` makes with the price index left in, so the same first-year rule (NaN until twelve months stand behind a
  month; a NaN inside the window poisons that window), the same fold across a year-entry.
- **`YearBook.debtToGdp(h)`**: the `debt` series (the city's own borrowing outstanding at the month's end, thousands)
  over `gdpYear(h)` at the same month, **a fraction** (1.5 is 150%, as unemployment 0.055 is 5.5%). NaN where the
  year's GDP is NaN or not above zero (no year behind the month, or a year of nothing) and where the debt was not
  recorded; owing nothing against a year of output is 0.
- **`ui/HistoryScreen`** (the MONEY picker): "GDP" is now `gdp`, **"GDP (yr)"**, drawn from `YearBook.gdpYear`; the
  month is its own trace **`gdpMonth`, "GDP (month)"**, under "GDP, real (yr)" and "GDP per capita (yr)" and above
  GDP's four parts, drawn from `h.aligned("gdp")` as `gdp` was; **`debtToGdp`, "Debt to GDP"**, unit `percent`, beside
  "Public debt". All three through `historyValues()`'s switch, off the getters. `areaOf` colours `gdpMonth` as `gdp`
  (business violet); `debtToGdp` takes MONEY's blue as "Public debt" does. The empty chart's line "from the end of the
  first year" now also covers a chart of Debt to GDP alone (it only knew the labels ending "(yr)").
- **`YearBookCheck` section 16**, the finding (below). Nothing else moved: no saved field, no save format, no dial.

## 2. The calls (★ = a design choice)

1. ★ **The key `gdp` stays the yearly line**, and the month takes a new key `gdpMonth`, rather than a new yearly key and
   a map from the old one. Where a graph choice is kept: the page's picks (`historyPicked`) are screen state, never
   saved, set from a preset, a door or the picker; the two pins are in the preferences file by key (`getPinnedLeft/Right`,
   default `realGdp`); the doors name keys. Everything that names `gdp` - a pin, the "How it is going" preset (gdp,
   population, borrowing rate), the header GDP tile's door (`UserInterface` ~2281) - now draws the year with no
   mapping to forget and no old name left that draws something else. A pin on `gdp` shows "GDP (yr)" in its card.
2. ★ **The header tile opens on the year, on purpose.** The tile reads "$X / yr" (the last twelve months) with a
   sparkline of the month's output; its door opened History on the month's line, under a yearly figure. It now opens on
   the line that is its figure. The sparkline stays the month (`UserInterface.sparkSeries`, `ClockCheck`'s `whole()` read
   `h.aligned`); `historyValues`' comment now says `gdp` is not one of the five keys the tiles mirror.
3. ★ **`debtToGdp` is a fraction, not x100.** The brief's "debt / gdpYear x 100" is what the screen prints: the
   `percent` unit multiplies by 100 on the axis (`plotScale`), the card and the tooltip (`fmtUnit`), and the percent axis
   (`axisFor`, its second argument) is the one unemployment and the borrowing rate are drawn on. A getter that returned
   x100 would print 20,512%. The check asserts the fraction and its x100 reading (205.1...%).
4. ★ **NaN, not absent, where there is no figure** (the screen's lines break at a NaN everywhere else): month 1-11,
   a year of nothing, a debt not yet recorded. A single month of nothing inside a year of output is not "no GDP": the
   year is the eleven others and the figure stays (asserted).
5. ★ **The same measure as the debt limit, with the numerator the Public debt line is.** `AutoBuilder.ratio()` (0.7.81)
   is debt over a year of output; CityMeasures' "debt" share (the rankings, `RankingsCheck` ~806 asserts it) is the
   history's `debt` over a year of output, and so is this. The limit's own debt has since 0.7.101 two more terms (the
   overdraft and the central bank's advances, not in the stored `debt` series, which is `DebtManager.getAllPrincipal()`),
   and its year scales up from fewer than twelve months where the graph draws nothing (the first-year rule the brief
   named, and `realGdpYear`'s). So the line is the limit's measure on the debt the "Public debt" line draws, which is
   what Jerus asked to compare; it can sit a little under the limit's own reading in a city that is overdrawn or has
   advances.
6. ★ **"GDP (month)" sits among GDP's lines**, not at the foot of the picker: "further down" is under real GDP and per
   capita, above the four parts (which are monthly too and read beside it). Easy to move.
7. ★ **No screen harness is committed.** The only harness that draws the page is `KeptCheck` (a walker of 435 s whose
   known reds sit on the same page); a section there would have been run only at the gate. The screen's three cases were
   proved by a scratch program in package `ui` (not committed; it builds a 24-month history by hand and reads
   `HistoryScreen.historyValues` and `traceFor`): `gdp` 780 at month 12 (the year, YearBook's), `gdpMonth` 120 (the
   month), `debtToGdp` 2.0513 printed "205.1%", `plotScale` 205.13, units and areas as above. If Jerus wants it a
   standing assertion, it is a `KeptBench` accessor and a `KeptCheck` section, a small batch of its own.

## 3. The checks

- **`YearBookCheck` section 16** ("the graph's GDP is a year, and the debt is over it"), a history built by hand, GDP 10
  a month more each month and debt 1,000 + 50 a month, 24 months: gdpYear NaN for months 1-11 and the ratio with it;
  month 12 = 10 x (1+...+12) = 780 (not the month's 120), month 13 = months 2-13, month 18 = 1,500 (not 180), month 24
  = 2,220 (not 240); NaN at the same months as `realGdpYear`, equal to it at a price index of 1 and twice it at 2
  (so it is the nominal line, the same sum); debtToGdp month 12 = 1,600/780 (x 100 = 205.1...%), month 18 = 1,900/1,500,
  month 24 = 2,200/2,220; a city that made nothing for 18 months has a year of 0 from month 12 and **no ratio** at any of
  those months (not infinity, not 0), the ratio back at month 19 (debt over 10); one dead month inside a year of output
  keeps its figure (110, debt 330 over it = 3); a debt recorded only from month 13 has no ratio before it; owing nothing
  is 0. 758 assertions, green.
- **Each line fails if the getter is swapped for the month**, proved by doing it: `gdpYear` returning `h.aligned("gdp")`
  fails 15 assertions of the section; `debtToGdp` dividing by the month's GDP instead of the year's fails 8.
  (The source was put back and rebuilt after each.)
- `./batch-check.sh --fast`: compile, maps, Stale (0 firm), tree: green. `./batch-check.sh YearBookCheck`: green.
- The pick (`tools/pick.sh origin/main HEAD`): ChartCheck ClockCheck HistoryCheck InterestAbroadCheck RankingsCheck
  ReadPathCheck ResearchViewCheck ScaleCheck StaffingDrawCheck TimingCheck WorldMoneysCheck YearBookCheck (12 of 232;
  `HistoryScreen.java` is read by no harness by name; `KeptCheck` draws it and runs at the gate). Not ALL, so no dry run.
  Run as `./batch-check.sh --pick` with `-Dcbs.parallel=1` on the tree with origin/main (3294dde04, FS13 merged) merged
  in: **"suite OK - 12 harnesses"** in 3 min 16 s. The merge's only conflicts were the generated `docs/harnesses.md` and
  `docs/map/README.md`, taken from main and regenerated; the date stamp the laptop writes (2026-11-20 against main's
  -21) was left out of the commit.

## 4. The default trace

The default playtest ran traced (`-Dplaytest.trace`, 2,405 months, both data files named, one JVM at a time) on
main's classes (b7a9edfc9, built apart from `git archive`) and on this branch's (17fe7a23c, the code as shipped):
**all eight CSVs byte-identical** (month, borrow, house, pop, bank, bonds, build and labour: 2,405 lines each, the
borrow one 38), and the two logs identical but for their run times (693 s and 711 s; the layout line's "took 15.9 s of
693 s" against "16.0 s of 711 s") and the clock stamps of two narrated lines. Both passed the audit ("All checks
passed"). As it had to: the diff is two read-only getters nothing in the model calls and the screen, which the
headless playtest never loads. So the 80-run ensemble is not run (contract 4: it runs only when the default trace moves
beyond runtime).

(A first attempt at the pair was cut by the background call's ten-minute default; main's JVM finished on its own and its
files are the ones compared, GY1's was run after it.)
