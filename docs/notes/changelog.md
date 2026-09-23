# The changelog — what shipped, newest first

Every batch as it was written up the day it landed: the version, its state
(built / deployed and verified / shipped), what it found, and the design note it
points at. These blocks were moved here verbatim from the head of `todo.md` on
2026-09-18, where forty-five of them had stacked up to 3,500 lines above the
list; new batches go at the top of this file in the same shape (`### TITLE —
date, state, see doc.md`), and the list stays a list. `index.md` maps the notes
by subsystem. The top block is the state of the tree.

### VERSION 0.7.6 — ONE LADDER, THE PRICE OF EACH SCHOOL, GDP IN LAYERS, AND LAND BOUGHT IN DOLLARS — 2026-09-23, DEPLOYED (tag 0923d), see `land-in-dollars.md`

**Four things Jerus asked for in one message — *"ok its good, just one thing"*,
and then four. Two Opus implementers in turn on one tree: the first three
changed nothing the model does with a number it already had, the fourth moves
it; an Opus docs pass after.**

**One ladder for every dial.** `ui/Ladder` is the one class:
`Ladder.of(min, max, step, reads)`, `.current(v)` (what the city charges),
`.showing(staged)`, exactly one of `.stages(DoubleConsumer)` or
`.appliesAtOnce(DoubleConsumer)`, then `.build()`. The buttons move one step,
the slider snaps to it, the reading goes to the accent colour off what the city
charges, and the ends line reads "x to y · one step is z · it is w"; the step is
the sensitivity and there is no second knob. Four optional settings change how
it is drawn, never how it moves (`stepReads`, `offAtCurrent`/`itIs`, `greyed`,
`wide`); the reading tracks the thumb and the callback waits for the release.
`PolicyScreen.taxLadder` and `stageSlider` are gone — `ladderOf(Lever)`,
`ladder(Lever)` and `stagedLadder` are the wiring. On it: every tax-page dial
and the farmland relief; the wage floor and the policy rate; the inflation
target, the holdings and the ceiling, applied at once with their chips kept as
shortcuts; the pension's two, EI's two, both health dials, the tuition subsidy
share, the every-school price and one per kind, the grant amount and the
student loan rate; the fare on Services. Each keeps its key, range, step and
formatter exactly — `PolicyCheck` did not notice. The standing subsidies
(switches) and the currency reform (a chip choice) are not dials and got none.

**The price of each school.** The tuition scale is nine, one per kind —
`TaxPolicy.tuitionScaleOf(type)` / `setTuitionScaleOf(type, scale)` — in the
shape 0.7.4 gave the income taxes: `setTuitionScale` is every school at once,
`getTuitionScale` the first kind's, `tuitionScalesSplit()` whether they have
parted; `Education.feeFor(type)` reads the kind's own. The nine ride the end of
the policy array (`STATE_BEFORE_SCHOOLS`), a shorter array reading all nine as
the one scale it carried. **Found on the way**, the trap 0.7.4 found for the
income rate: the month and the load path told the schools `getTuitionScale()`,
which would have put nine prices back to one on every load —
`Game.tellTheSchoolsTheirPrices` hands each kind its own. The Schools page
keeps "Every school at once" at the top (moving it takes back every kind's
staging) and has a row per kind under it in `EducationType` order: its own
staged ladder and four figures off the model — the places its buildings seat,
the students in it, what its staff and buildings cost
(`Education.getCostOf`, handed in by the month from `BuildingManager`'s
per-course payroll and upkeep) and the tuition its students paid
(`getFeesOf`), each kind's month riding the end of the schools' array. A kind
with nothing standing says "no school" and its ladder is greyed. The preview
re-strikes the month kind by kind (`Education.billedAt`). `EducationCheck`
§17: one kind's price moves only that fee; the every-school setter moves all
nine; the array round-trips and an old-length array reads nine equal scales;
the rows add up to the page's totals to the cent; after a load the schools
charge nine separate prices.

**GDP in layers.** Four history series beside `gdp` — `consumption`,
`investment`, `government`, `netExports`, off `NationalAccounts`' own getters,
each with a year-book rule — and `YearBook.real()` / `realYear()` beside
`realGdpYear()`, deflated the same way, with `YearBook.GDP_PARTS` naming the
four. A "layers" chip on the real-GDP small chart, and on the big chart's
reading when real GDP is picked alone, draws a `StackedAreaChart` behind the
line chart: consumption, investment and government stacked from zero in
`Palette.GDP_LAYERS` (the maturity ladder's three steps), the real GDP line
over them in `TEXT_HEAD`, so the gap between the stack's top and the line is
net exports — a stacked area cannot hold a negative layer, and the caption
says so. The crosshair reads the four parts; the log chip refuses while the
layers are on; the toggle is screen state and the pins are unchanged. Older
saves have no parts until they play a month. `HistoryCheck` §2c: the four
exist after a month, C+I+G+NX is the month's `gdp` to the cent, a rolling year
of the four in founding money is the year of real GDP.

**Land is bought in dollars.** Jerus: *"when you buy land, make it so that it
costs USD not domestic currency … a little toggle at the top to choose … to
use up your USD reserves or to convert cash into usd exactly to buy the land,
and the default is that you convert."* `LandMarket` prices every parcel in US
dollars — the same base and premiums, so at the founding rate of 1.00 every
number is what it was — fixed at listing; the treasury pays `usd × rate` on the
day (`LandParcel.localPrice`, `LandManager.buyParcel`, the rate read live
through a supplier `Game` hands in). What businesses pay the city stays local
money, so the city's margin on land carries the currency. The `landPrice`
series and the year book's column are the dollar price since this build, and a
currency reform no longer clears the land office's board. The chip pair at the
top of the land office — *Pay by converting cash* (the default) / *Pay from the
vault* — is `Game.landPaidFromVault`, saved as `landPaidFromVault`, applied at
once. Converting pays `usd × rate` through `TreasuryLine.LAND` and
`ForeignAccounts.buyAndSpendDollarsForLand` buys the dollars and hands them over
in one movement, the vault where it began; from the vault,
`spendReservesOnLand` moves no local money, `lifetimeIntervention` falls by the
local price as a sale's would, and the treasury's journal carries "Bought land
with US$…k of reserves" back against the budget's land line; a short vault
spends what it holds and converts the rest, and the receipt says so. The
tiles show both prices, the market cell is in US$, the Exchange page has
"Spent on land" lines and the Government tab's "Land bought" opens into
converted, from the vault and bought back. Into the save without a bump: the
toggle's key, the listing behind a new marker (−105) whose prices are dollars
(an older listing, and an older price state without its fourth slot, read as
dollars at the rate of the day it is loaded — `settleLocalPrices`), and
`ForeignAccounts` slots 31–36. **Found on the way:** the brief's premise that
buying dollars pushes the rate is wrong — a treasury purchase of dollars is the
financing item (`Scope.RESERVE`), which `pressure()` never reads, so converting
for land pushes exactly what a reserve purchase does, which is nothing
(`ForeignCheck` §14); arguably wrong for land, and open for Jerus (the todo).
`LandCheck` §12–13 (its older sections restated at the bare office's founding
rate — premise restated, not moved), `MoneyCheck` (a vault purchase, then 24
closed months), `ForeignCheck` §14, `NewGameCheck` (the toggle and the counters
reset), `TreasuryCheck` reads `localPrice`. **The playtest moved, once:** seed 0
against the first half's run, only the treasury's cash — +$0.1B at m1533,
growing to $416.2B against $413.8B at m4005 — because the currency ran strong
(never weaker than 1.00, 0.48 at the end) and the advisor's US$6.60B of land,
bought over 132 months, cost D$4.13B, 37.4% less than at the founding rate; a
new line in the playtest's summary says so.

**The docs pass** fixed `GameVersion`'s 0.7.6 entry (it opened "Three things
Jerus asked for together, none of them a change to what the model does … the
default run is the run it was, to the byte" over four, the fourth moving the
run); `ForeignAccounts.redenominate()`'s note, which read as though the land's
dollars moved in a reform; `YearBook.real()`'s javadoc, whose first sentence —
the one the map prints — was the heading "GDP IN LAYERS (0.7.6)."; "ten plots"
where the prose says it in the present, `LISTING_SIZE` having been nine since
the shelf became a square — `LandMarket`'s header (the map's line),
`LandParcel`, `LandManager`, `LandScreen`, `Game` and `LandCheck`'s section
title and label; and `CLAUDE.md`'s line counts (141,900 → 144,200, `Game.java`
9,300 → 9,500). It left one code change for the implementer: the Services
tab's "What tuition raises" table still strikes each course's revenue on the
screen (`feeFor × enrolled × (1 − subsidy)`) where `Education.getFeesOf()`
now holds it.

Verification: `build-tree.sh` and `compile-all-tree.sh` silent; the suite 59
of 60 with the known line (`InfrastructureCheck`), `BuildMenuCheck` skipped
without JavaFX, 149 s — the same count as 0.7.5, no new harness
(`EducationCheck` §17, `HistoryCheck` §2c, `LandCheck` §12–13, `ForeignCheck`
§14); `LongPlaytest` seed 0 after the first half byte-identical to
`playtest-baseline-0923b.txt` bar the wall clock, and after the second the
movement above and nothing else — the run is the new baseline,
`playtest-baseline-0923d.txt`; `StaleCheck` and `LandCheck` rerun after the
docs pass (its edits are comments, `CLAUDE.md` and two harness labels); the
indexes regenerated; `Stale` 0 firm, 71 soft, the same list as 0.7.5's;
`SAVE_FORMAT` 27. 213 files, 144,158 lines, 831 constants (815). The deploy
set is the batch's twenty-nine source files (`ui/Ladder.java` new), `CLAUDE.md`,
`README.md` and `docs/`. Deployed as tag 0923d.

### VERSION 0.7.5 — ENTER BUILDS; THE REPORTS PAGE, REDRAWN — 2026-09-23, DEPLOYED AND VERIFIED (tag 0923b), see `financial-crisis-of-2045.md`

**Two things Jerus asked for, both in the interface; the month did not move.**
**Enter builds, Backspace clears:** on a build category page, Enter places
every card with a quantity, in the page's own order, each through the card's
own `placeOrder` (which now returns whether it built) — the first refusal puts
its screen up and the rest stay pending; Backspace or Delete takes every
quantity on the page back to none (`BuildScreen.buildPending()` /
`clearPending()`, called from the window's key filter only while
`handleAllBuildingMenus` is the screen, and the key spent only when it did
something). The Build button's tooltip says both, and a caption under the grid
does while anything is pending. **The Reports page, redrawn:** two small pinned
charts at the top, real GDP (the rolling year) and the population until the
player pins others — a preference in `GamePrefs` (`pinnedLeft`,
`pinnedRight`), never in the save, an unknown name falling back to the
default; the big chart with the presets, "clear all" and a "log" switch in a
row above it, seeded on a first visit with the new `PRESETS[0]` "What money
costs" (the rate, the price level, inflation) under the unchanged
never-re-seed rule; two units on two real axes (a second transparent
`LineChart` stacked in a `StackPane`, its axis on the right), three or more
still mapped onto 0–100; a crosshair reading every line at the month under the
pointer (a bucket's month and average when bucketed); recessions shaded on all
three charts; the named episodes ticked under the big chart and listed in one
caption line (oldest first, at most eight). The episodes are
`YearBook.episodes()`, a pure function of the history, printed in the year
book's WHAT HAPPENED section too ("Financial crisis of 2045 - months
540-553"): bank equity under zero ("Financial crisis of"), the rolling year
of real output below the year before ("Recession of", two years or more
"Depression of"), the exchange rate above twice a year before ("Currency
crisis of"), inflation above 25% / below −10% ("The YYYY inflation" /
"deflation"), the sick rate above 10% ("Epidemic of"), the treasury under zero
("Treasury crisis of"), unemployment above 20% ("The YYYY slump"); runs under
`EPISODE_MIN_MONTHS` (3) dropped before runs closer than `EPISODE_JOIN_MONTHS`
(6) are joined, `DEPRESSION_MONTHS` 24, the second of a name in one year
"…, again"; `YearBook.recessions()` is the unjoined bands the charts shade.
Real GDP and inflation are struck once now, in `YearBook` (`realGdp`,
`realGdpYear`, `inflation`), where the screen and the book had each had a copy.
The picker folds into its groups — closed unless one of their lines is
picked, "n of m picked", open state kept while the game runs — with a filter
box that narrows and opens them, and the chip pressed is held where it was on
the screen across the rebuild (`holdInPlace`). **Found on the way:** the "−"
stepper on an empty card stored −1 (`merge` stores the value when the key is
absent) and read "-1" — `compute` now; the licence refusal screen was missing
from the rail's `tabFor`, so it lit no tab. Opus implementer, Opus docs pass;
the docs pass fixed `BuildScreen.placeOrder()`'s "three refusals" (four:
funding, land, deposit, licence), HistoryScreen's class header ("The text is
exactly what it was", now past tense), THE RECORD's "Normalising is what
happens when the units disagree" (annotated for two axes, as THE HISTORY
SCREEN already was), `historyValues()`' pointer for real GDP's why (it is in
`realGdp()` and `realGdpYear()`), and `CLAUDE.md`'s line count (141,900).
Verification: the model and compile-all clean; the suite 59 of 60 with the
known line (`InfrastructureCheck`), `BuildMenuCheck` skipped without JavaFX —
the same count as 0.7.4, no new harness (`YearBookCheck` gained section 14,
54 → 82 labelled assertions); `LongPlaytest` seed 0 byte-identical to
`playtest-baseline-0923a.txt` bar the wall clock; `StaleCheck` rerun after the
docs pass (its edits are comments and `CLAUDE.md`); the indexes regenerated;
`Stale` 0 firm, 71 soft, the same list as 0.7.4's; `SAVE_FORMAT` 27. 212
files, 141,923 lines, 815 constants (805). The deploy set is `GamePrefs`,
`GameVersion`, `YearBook`, `YearBookCheck`, `ui/BuildScreen`,
`ui/HistoryScreen`, `ui/UserInterface`, `CLAUDE.md` and `docs/`. Deployed 2026-09-23 as tag 0923b, every file verified byte-for-byte on the PC.
The Settings screen's "Keys" list, which the docs pass found naming two keys,
names all six since the deploy (the orchestrator's fix).

### VERSION 0.7.4 — THE HOUSEKEEPING — 2026-09-23, DEPLOYED AND VERIFIED (tag 0923a), see `every-tax-at-once.md`

**Four small things Jerus asked for in one evening, all player-facing, none a
model redesign; every new dial opens where the old constant was.** **The
inflation target is a dial:** `DebtManager.INFLATION_TARGET` becomes the
player's `inflationTarget` (`DEFAULT_INFLATION_TARGET` 2%, `MIN_` 0 and `MAX_`
10%), read by `ruleRate()`, `adviceReason()` and the strip's colour and
tooltip; on the monetary page under the autopilot, applied at once — chips 0–5%
and a half-point ladder to 10, with the rule struck at the target and at
another (`ruleRate(inflation, target)`); saved under its own key
(`DataSave.inflationTarget`, an older save reads 2%);
`-Dplaytest.inflationTarget` holds it for a run. **Three rates on the strip:**
a third panel, *the price of money* — the central bank's dial, the bank's
`lendingRate()` on it ("no bank" / "failed" in red), and the city's
`DebtManager.getRate()` — three caption-weight lines; and the bank page's "It
charges", which printed the city's rate, prints the bank's lending rate now.
**The sector list:** each card draws the last 24 months of net income
(`SPARK_MONTHS`, a canvas 90×22, green or red on the latest month) and its
workers (`Sector.getWorkers()`, posts at the "Staffed" share, beside the new
`getPostsOffered()`); "Show more" opens every card to revenue, margin, cash,
what it owes the bank and posts filled — screen state, never saved. Two new
history series per sector, `netIncome:<sector>` (a flow) and
`workers:<sector>` (a level), with year-book rules. **The taxes by type:**
profit, sales and wage each have a base of their own (`TaxPolicy`
`profitTaxRate` / `salesTaxRate` / `wageTaxRate`), every offset riding its
own tax's base; each tax page's top lever is that base, and the old city rate
is "Every tax at once" on the Everything page (`setIncomeTaxRate()`, all
three), with the three bases side by side. **Found on the way:** the load
path read the old single income key after the policy array and would have
put a split back together on every load — it reads the key now only when the
array was not read; the Government tab printed the bank's rate as the income
rate while `Game` charges it retail's profit rate — it prints retail's now.
Nothing into saves needed a bump: the target key, three slots on the policy
array's end (`STATE_BEFORE_SPLIT`), the two series per sector. Opus
implementer, Opus docs pass; the docs pass fixed `DebtManager.ruleRate()`'s
javadoc (its floor-and-top thresholds hold at the default target only),
PolicyScreen's tab header ("the two city rates"), its `bandLever()` note
("stage the CITY rate alone"), THE FOUR TAXES' "Everything reads" (the page
has a lever), `TaxPolicy.getPolicyState()`'s "city rates first", SectorScreen's
"Six businesses", `CLAUDE.md`'s line count (140,500), and added the bank page's
"It charges" to `GameVersion`'s 0.7.4 entry. Verification: the model and
compile-all clean; the suite 59 of 60 with the known line
(`InfrastructureCheck`), `BuildMenuCheck` skipped without JavaFX — the same
count as 0.7.3, no new harness (`MonetaryCheck`, `PolicyCheck`, `HistoryCheck`
gained sections); `LongPlaytest` seed 0 byte-identical to
`playtest-baseline-0922e.txt` bar the wall clock (rerun after the docs pass);
the indexes regenerated; `Stale` 0 firm, 71 soft, the same list as 0.7.3's;
`SAVE_FORMAT` 27. 212 files, 140,541 lines, 805 constants (794). The deploy
set is the batch's nineteen source files plus `ui/BankScreen.java`,
`CLAUDE.md` and `docs/`. **Deployed 2026-09-23 as tag 0923a, every file verified byte-for-byte on the PC.** The strip's "bank" tooltip was tightened to `lendingRate()`'s own words before the deploy.

### THE MANUAL AT 0.7.3, AND ITS COPY ON GITHUB — 2026-09-22 (night), PUBLISHED as version 8 (version 9 after the deploy, one clause); the tree copy DEPLOYED AND VERIFIED with 0922c, see `the-manual-at-0-7-3.md`

**THE PUBLISHED MANUAL IS AT 0.7.3 / FORMAT 27 (version 8, same URL), AND FOR
THE FIRST TIME IT IS IN THE REPOSITORY.** Jerus: *"have it yes in html form but
also in text or something and with github, thus inside github you can see it
without a link."* Twenty-one sections instead of twenty: a new **§13 The
central bank** — the books and M0/M2, the rate as a floor, the window, the
autopilot, advances and the ceiling dial and who is paid past it, the holdings
dial (QE/QT) with its formula, the defence with its formula and the finding
that it spends the founders' billion on structural deficits, the demand
channel with `spendFactor` and the `MonetaryCheck` §6 table, the plain
statement that the channel moves very little and the four reasons, a table of
the chapter's dials — and every other section brought to the tree: the month
at fourteen steps against `docs/month-order.md`; the households as holders,
the fold, the saving response; the price at the door (0.6.8); the price of a
place (0.6.9); §12 rewritten around the curve, the five maturities, the three
holders and accretion with the two-point discount and every sentence that
leaned on it gone; §14 without the drift, with the real-rate pull, the guard's
removal and the strip turning round; §19 with the new screens and what went
into saves without a bump; §20 with `CentralBankCheck`, `HoldersCheck`,
`CurrencyCheck`; §21 reconciled against the list — three retired, ten added,
thirty-four rows. Vitals: 211 files, ~138,000 lines, 73 buildings, 15
sectors, 60 harnesses (168 s), 4,005 months; 780 dials, 3,870 assertions; the
dated readings re-struck from tonight's playtest. **Reading the page whole
found eleven stale places no block covered**, the oldest wrong since version
6: §12's granularity column was a thousand times too small ($1,000 for $1M).
**Made by three Opus contexts** per the model rule: an implementer from the
published page and nothing local (32 minutes, 208 calls), a reviewer who
checked 165 things, reran thirteen harnesses and the playtest, made two edits
of its own (the old funding cost, a cross-reference inherited from version 7)
and ten on the orchestrator's decisions (among them "a full vault meets a
crisis at full strength for roughly two quarters", which the arithmetic does
not support — the note is corrected too), and a third that wrote
**`tools.ManualToMarkdown`** (1,522 lines, JDK only): `--wrap` turns the
published page into `docs/manual.html`, a standalone that opens from a clone
and carries the published bytes verbatim, and the second run turns that into
`docs/manual.md`, GitHub-flavoured Markdown that GitHub renders — proved
byte-identical from fragment and standalone, idempotent, rendered with
GitHub's own renderer to 21 headings, 26 tables, 23 formulas, 23 notes and
every contents link resolving, 41,809 words in and out. Both files are
GENERATED and never edited; `CLAUDE.md` and `README.md` say so and list the
tool; `Stale` reads `manual.md` as prose and finds nothing firm; `StaleCheck`
green. **Found on the way, not fixed: sixteen places the tree's prose, the
notes or the list disagree with the code** — "ten points" over a five-point
constant in `DebtManager`, the QE step described three ways, "a 5% world" over
a 2% one, a reform that is never automatic, 4,002 months in a dozen comments
for a run that reports 4,005, 159 dials without a sentence — listed in the
note and in the todo; three items §3 of the list carried after 0.7.0 closed
them are struck tonight. **Share pin still at version 1** — Jerus's.

### VERSION 0.7.3 — THE DEMAND CHANNEL — 2026-09-22, DEPLOYED AND VERIFIED WITH 0.7.2 (tag 0922c, 2026-09-23), see `the-demand-channel.md`

**Batch E of `the-central-bank.md` §12: its §9 (the households' response)
and §10 (the harness).** The design: *for the rate to bite at home, a
household's saving must answer the real return.* **What a household spends
above a basket a head answers the real deposit rate:** one factor a month,
`1 − SAVING_RESPONSE ×` the real deposit rate, held between `SPEND_FLOOR` ½
and `SPEND_CEILING` 1½ (three dials in `HouseholdBalance`, `SAVING_RESPONSE`
provisional at 1.0 — Jerus's number), struck by `Game` on
`realDepositRate()` (the bank's deposit rate less the year's inflation, the
same inflation the parity and the real rate differential read) and handed to
every cell's plan through `setSpendFactor()`; the propensity's share of income
above subsistence, the wealth term, and the surplus the luxury counter and the
table spend out of all scale by it, so a hike does not hand what the grocer
lost to the counter; subsistence never moves, and at a factor of 1 the plan is
the old plan to the bit. The monetary page prints it: "savers earn X% real, so
households spend Y% of what they would at zero". Nothing is saved; the load
path strikes it again. **The transmission assertion, waiting since batch A:**
`MonetaryCheck` §6 — one founding held at 3 / 10 / 20 / 40% from month 25 to
60, M2 now `Game.getM2()` — asserts that inflation falls with the rate: each
row no higher than the one before within `MEASUREMENT_NOISE` (0.05 points,
measured by holding every row's dial a month late: 0.000–0.019), and the 3%
row at least `TRANSMISSION_FLOOR` (a point) above the 40% row. Green at 1.032
points, where 0.7.2 read 0.996 and would have failed — and most of that point
is still the currency's. **The exchange rate's guard goes:**
`ForeignAccounts.MAX_RATE` 100 → 1e9 and `MIN_RATE` .01 → 1e-9, numerical
guards against a rate run to nothing or to infinity, still scaled by a reform;
the strip's second line turns round past a hundredth of a cent ("US¢1 =
D$1,000" at a rate of 100,000) and the pages print a rate through
`Money.fxRate()`; `CurrencyCheck` §6 (a push past 100 lands on 150, the same
the other way past .01, the reform scaling the guards). **EI is paid in the
month it is credited,** as the grant has been since 0.7.1: struck again on the
pool the month opens with and paid at the top (`Game.payEiBenefits()`,
`Unemployment.restrikeBenefits()`), so the treasury, the ledger and the audit
read one figure (`OutsideCheck` §7); a 0.7.2 save pays one month's EI twice on
its first month, once. **The bank quotes no more than it charges:** the first
build put the spend factor at its floor in month 3 of every seed, because a
founding bank's payout over $5.7k of deposits read as 7,567% a year; the rate
it reports is capped at `lendingRate(policy)` (`Bank.isDepositRateCapped()`)
and what it pays is unchanged — the cap binds in 17 to 236 months of each
default run (`BankCheck`); `TreasuryCheck`'s first-coupon line was restated on
the way (its households held none of the paper only because the founding bank
quoted them 7,567%). `Game.holderShares()` is one branch. **What it measured**
(the default eight, medians): population 154,207 → 149,028, price swing
1.46× → 1.50×, the index at the end 1.126 → 1.135, bank failures 19 both, the
spend factor at a median of 0.994 (each run's lowest and highest at medians
of 0.834 and 1.131), no month at any guard, no dollar past 3.5; held at 10 /
30 / 50% the six broke seeds swing 1.25× / 1.75× / 2.24× against 1.24× /
1.90× / 2.01×. **Why it moves so
little — the finding:** the deposit rate carries about a quarter of the dial
(`DEPOSIT_PASS_THROUGH`), the shelf is priced by the shops' coverage or its
floor rather than by money, the counter and the table are not in the index,
and saved money compounds at a held high rate — *a demand channel needs a
price that answers to demand.* **Found on the way** (on the list): who gets
45% (the comment says 55% to savers, the code pays 45%); the deposit cap binds
beyond the founding and two comments now disagree about whether a bank can pay
savers more than it charges; the shelf does not answer money; the rate goes
NaN in a city with no trade; EI premiums may carry the lag the benefits had;
the load path's re-strike is not the saved month's plan; two guard assertions
that assert almost nothing at a billion. The docs pass fixed
`HouseholdBalance`'s `planFromLoad()` (it is `planOnly()`) and noted there
that the spend factor is the one unsaved input the load-path rule now
excepts; made `LongPlaytest`'s guard note say "a rate run to infinity", not
"NaN" (a clamp passes NaN through); gave `Bank.depositRate()` a javadoc that
says the quote is capped; made `EconomyManager.setOutsidePayments()` say where
EI and the grant are paid now; turned a "weakened" into "strengthened" in
`ForeignAccounts`' reform note (a rate near the floor is pinned by a rise in
the currency); and brought `CLAUDE.md`'s line counts to 138,000 and 9,300. It
flagged the People screen's "EI paid this month", which reads
`Unemployment.getBenefitsPaid()` — next month's bill between presses since
0.7.3 — rather than what the treasury paid. Opus implementer, Opus docs pass.
Verification: the model and compile-all clean; the suite 59 of 60 with the
known line (`InfrastructureCheck`), `BuildMenuCheck` skipped without JavaFX —
the same count as 0.7.2, no new harness; after the docs pass both builds
clean, the indexes regenerated, and `StaleCheck`, `MonetaryCheck` and
`HouseholdCheck` green, `Stale` 0 firm, 71 soft (0.7.2's 72 less the
`planFromLoad()` name), none of them this batch's; `SAVE_FORMAT` 27, nothing
new saved. The deploy set is the batch's twenty files plus
`EconomyManager.java`, `CLAUDE.md` and `docs/` from the docs pass. **Deployed
2026-09-23 with 0.7.2 and the manual's tree copy as tag 0922c — 86 files, every
one verified byte-for-byte on the PC.**

### VERSION 0.7.2 — THE CURRENCY OFF ITS RULE — 2026-09-22, DEPLOYED AND VERIFIED WITH 0.7.3 (tag 0922c, 2026-09-23), see `the-currency-off-its-rule.md`

**Batch D of `the-central-bank.md` §12: its §8 (the currency under a central
bank) and the "ceiling as a dial" line of §6.** Jerus: *"i think we need to
uncap the rate... but if we do... what happens to everyone?"* **The drift is
deleted:** `repriceCurrency()` no longer moves the rate by the whole inflation
differential every month (`rate *= 1 + (local − world) / 12`, the ring the
0.6.9 year book closed); what is left is the pull toward parity (`REVERSION`,
the level form, bounded) and a push from the capital account. **The real rate
moves the currency:** `ratePressure()` reads `(policy − the city's inflation) −
(WORLD_BASE_RATE − the world's realised inflation)`, one definition in
`Game.realRateDifferential()` for the month and the monetary page, handed
through `ForeignAccounts.setRealRateDifferential()` (the nominal
`setRateDifferential()` is gone, and with it the 13-point cap);
`MAX_RATE_PRESSURE` .8 → 4.0, a numerical guard at a hundred-point real gap;
`CapitalFlows.MAX_SPREAD` .06 → .25, the hot money comparing the city's rate
net of the bank's strain premium (`DebtManager.getRateBeforeStrain()`);
`RATE_PULL` 6 → 4, the one retune (at six the loop's gain was 1.44 against the
deleted drift's 1.0, and seed 2 went to the guard and 115× its founding
prices). **A defence that spends:** a month the currency is pushed weaker on a
deficit, the central bank sells `absorption() ×` the month's own deficit of
the vault, at most the vault, and what the sale meets of the deficit is what
damps the push; a rise is never met. It is booked at the reprice as a capital
transaction against the world — the vault down by the dollars, the central
bank's equity by their local price (`CentralBank.vaultSpent()`, a line under
equity on the Money page), M0 unmoved, no pool moving and nothing declared to
the audit, the month's profit and the remittance untouched; the first build
retired the money instead and took seed 0's M0 to −$255M. The Exchange page
prints what was sold this month and since founding; the forces page shows the
four terms that exist — trade, the real rate, the vault's defence, parity —
and no drift row; the defence and the real rate ride slots 25–30 of the
foreign accounts' array and the central bank's spent-since-founding the end
of its own. **The dial uncaps:** `DebtManager.MAX_POLICY_RATE` .25 → 1.00, the
slider with chips from 0 to 100, the autopilot's rule no longer clamped at
25%. **The ceiling is a dial:** `CentralBank.advancesCeilingMonths`, 0 to
`MAX_ADVANCES_CEILING` 36, default `DEFAULT_ADVANCES_MONTHS` 6 (replacing
`MAX_ADVANCES_MONTHS`), chips from 3 to 36 months on the monetary page, saved
under its own key (an old save reads six), `-Dplaytest.advancesMonths`.
**The world's paper has a curve:** `DebtManager.foreignCurveRate(months)` —
the foreign rate plus the same term-premium table — prices a dollar issue by
maturity and values a dollar bond at its remaining months, so the dial no
longer moves a dollar bond's value and the country premium does; on the way
the dollar quote was found to be a money pump (a thirty-year dollar round trip
netted the city $7,501k over eight rounds on 0.7.1), and it now prices its
own coupons into the premium by a fixed point (`quoteForeignRate(Debt,
months)`, walked by `Game.selfPricedForeignRate()`). **Empty household cells
fold:** one threshold, `HouseholdBalance.EMPTY_CELL` ½, one test,
`Household.isEmpty()`, and `foldEmptyCells()` where counts are written — a cell
under it keeps its census count and every stock it held goes to its own stock
group, then the city — found when `DenominationCheck` went red on a
1e-15-household ghost that was still being paid. `CurrencyCheck` is the
sixtieth harness (§1–5); `CentralBankCheck` §16, `ForeignDebtCheck` §8,
`RestructureCheck` §5's dollar case (which fails on 0.7.1), `HouseholdCheck`'s
fold section and `DenominationCheck` §3a are new; `MonetaryCheck` §4 is
restated and §6 has a 40% row, `ForeignCheck` §2 is restated (the vault is the
founders' dollars less what the defence sold), and `BankCheck` §10's issue is
halved with a fixture line that the households held their paper through the
settle. **The ensemble** (the default eight, medians, from the design note):
price swing 1.50× → 1.46×, no month at the currency's guard, the rate at the
end 0.548 → 0.522, the lowest M0 $1.06B, the founders' billion spent on
structural deficits rather than crises; bank failures 8 → 19, all of the rise
Manufacturing's (a Fabrication Works borrowed for past a young bank's equity),
not the currency channel; held at 30% or 50% the six broke seeds swing 1.9×
and 2.0× where 0.7.1's stop at 25% sent them to 190–216×; the autopilot on all
eight takes bank failures 27 → 17. **Found on the way** (on the list): the
central bank ends with negative equity in all eight default seeds; interest
on reserves compounds without limit at a held extreme rate; Manufacturing's
Fabrication Works fails the bank; the advisor refills the vault the defence
spends; the rate term acts before settling; the central bank's month flows
are not saved; the forces page and the monetary page read the real rate a
press apart; `HealthCheck`'s hunger comparison passes by chance; and batch C's
"spread is gone" sale can fire after all (`BankCheck` §10). The docs pass
made `ForeignAccounts.RATE_PULL`'s sentence say the rate is the real one,
`CentralBankCheck`'s first sentence cover the ceiling dial, gave
`LongPlaytest.dialPath` and `inflationPath` their dial sentences, corrected
`CurrencyCheck`'s header (13 points is the old cap over the old pull, not
times it) and `DebtManager.advisedPolicyRate()`'s "never moves anything on
its own" (the autopilot has set the dial to it since 0.7.0), rewrapped three
comments the batch left overlong, and brought `CLAUDE.md`'s line counts to
137,000 and 9,200; it flagged a "5% world" in `CapitalFlows.MAX_SPREAD`'s
sentence against `WORLD_BASE_RATE` .02, a version number in a player-facing
note on the Money page, and two measurements on which `GameVersion`'s 0.7.2
entry and the design note disagree. Opus implementer, Opus docs pass.
Verification: the model and compile-all clean; the suite 59 of 60 with the
known line (`InfrastructureCheck`), `BuildMenuCheck` skipped without JavaFX —
against 0.7.1's 58 of 59, the one more being `CurrencyCheck`, the sixtieth;
after the docs pass both builds clean, the indexes regenerated, and
`StaleCheck`, `CurrencyCheck` and `ForeignCheck` green, `Stale` 0 firm, 72
soft, none of them this batch's; `SAVE_FORMAT` 27, the new foreign-account
slots appended, the central bank's slot appended and the ceiling under its
own key; `MoneyAudit.java` and `HoldersCheck.java` byte-identical with 0.7.1.
**Deployed 2026-09-23 with 0.7.3 as tag 0922c, every file verified
byte-for-byte on the PC.**

### VERSION 0.7.1 — THE CURVE AND THE HOLDERS — 2026-09-22, DEPLOYED AND VERIFIED, see `the-curve-and-the-holders.md`

**Batch C of `the-central-bank.md` §12: its §4 (the curve), §5 (who holds
the paper) and the holdings half of §6.** Jerus: *"only be able to issue 10y
20y 30y 40y and 50y … the short term rates, aka the one you choose, those
should be basically the tbill rate, the others change just as in real life"*,
and *"yes households should be able to hold."* **The curve:** every price of
the city's own paper is read off `DebtManager.priceAt(debt, months)` at the
paper's maturity - the quote, the issue, the buyback, the market value and the
borrow page, which lists the curve maturity by maturity - as the dial plus the
credit spreads plus a term premium, `TERM_PREMIUM_10Y … 50Y` at 0.50, 0.90,
1.15, 1.35 and 1.50 points (provisional, Jerus's to settle), linear from
nothing at a year to the ten-year point and between the points after it, less
what the central bank's holdings compress of it. The note carries no premium
and `getRate()` stays the short end. A hike moves the whole curve in parallel,
debt moves every row by the same spread, and `RestructureCheck`'s round trip is
still neutral by construction. Term loans are issued only at
`LongTermBond.MATURITIES` - 10, 20, 30, 40 or 50 years, at home and in dollars;
any other term is refused in words and the Finances chips step by ten. The
world still lends at one rate whatever the term. **The holders:** each domestic
bond carries what the households and the central bank hold of it, and the bank
holds the rest; the households' paper is a new cell slot
(`CELL_SLOTS_BEFORE_PAPER`) valued at one book ratio a month, saved. At the
settle the households take `min(MAX_HOUSEHOLD_PAPER_SHARE, HOUSEHOLD_PAPER_APPETITE
× spread)` of each issue - a fifth of it per point over the deposit rate, never
more than half - out of savings past the cushion, and the bank buys the rest;
coupons and principal are split by holder, the households' paid into savings as
untaxed investment income and the central bank's destroyed and remitted; a
household short of money sells its paper to the bank's desk before its dollars
and its shares, and a leaver sells on the way out; a buyback pays every holder
its share, and a dollar bond's whole price is declared leaving the country
(batch B's "pays nobody on the books" closed). **The holdings dial, QE and QT:**
`CentralBank.targetShare`, 0 to `MAX_QE_SHARE` (half), set on the Policy tab's
monetary page and saved, moves the central bank's holding of the city's term
paper toward it at the top of the month by at most `QE_SPEED` (a quarter) of the
larger of the dial and the setting before it, trading only paper the bank has
paid for, at the curve's market value, in money made or destroyed, at face with
the difference taken into its profit; the compression is the premium times the
share held over `MAX_QE_SHARE` times `QE_COMPRESSION` (1), so at 30% held the
fifty-year is 0.90 points under the table and the note has not moved. **The
discount accretes:** each bond carries its own and earns it straight-line, the
bank carrying what it has not earned against its book, so batch A's $60M
settle-month "interest" is gone; a 0.7.0 save taken between issue and settle
books its discount whole at that settle, as it would have. **Also closed:**
`CalendarCheck` asserts one press is one month; the unpaid construction
retainer is removed; the Government tab has its Subsidies line; the students'
grant is paid in the month it is credited; `CentralBankCheck` §1 closes under
its own title; a failed bank at the window is charged nothing while it is
resolved, decided and labelled; `ForeignAccounts.reset()` clears the three
rates it left behind; the Trade tab's forces page is one reading; the skip
report counts months on the advances and at the ceiling. **The ensemble:** the
default eight move only because the advisor's dollar loan is twenty years, not
twenty-five (put back at 25 with the refusal bypassed, every seed reproduces
0.7.0 line for line) and stay in the weather - median population 160.5k →
164.1k, bank failures 8 → 8. Sent home, the households take 38% to half of each
issue and are paid $6–92M of coupons over a run; with the dial at 30% the
central bank buys its 30% in four months on every borrowing seed and takes 0.90
points off the fifty-year; the audit closed on every month of all forty runs.
**Found on the way** (on the list): the "spread is gone" sell-back can hardly
ever fire; a dollar bond is valued at the short rate and a dollar term loan
flat at the world rate; EI lags a month as the grants did; a skip halts at
cash ≤ 0 though the advances would carry the month; dollar-loan proceeds land
in the unaudited gap between presses; the forces page leaves out the inflation
drift; `holderShares()` has two branches that compute the same thing. The docs
pass made `LongTermBond` a class header and `CentralBankCheck`'s first sentence
cover the dial; retitled `Game`'s emptied THE CONSTRUCTION SUBSIDY banner in the
house's tombstone form ("- removed in 0.7.1") and took "the retainer" out of
`DataSave`'s sub-banner, `Game`'s save path, `isConstructionShedding()` and
`LongPlaytest`'s ordering note; corrected `CentralBank.restore()`'s "four"
(five), the "four months" of `QE_SPEED` and Game's HOLDINGS DIAL banner (at
most four, exactly four to or from nothing), `HOUSEHOLD_PAPER_APPETITE`'s and
`GameVersion`'s "twenty per point" (a fifth of an issue a point), `Bank`'s
"Game's settleWindow" (`CentralBank.settleWindow()`), the settle banner's "books
the discount" and `Household`'s clinic note, which now lists the city's paper;
and flagged the dial's pace memory lost on a reload — fixed before the deploy
(`CentralBank.restoreTargetShare()`, `CentralBankCheck` §14 now turns the dial
mid-move and reloads; the old line fails it two ways). Opus implementer, Opus
docs pass. Verification: compile-all clean; the suite 58 of 59 with the known
line (`InfrastructureCheck`), `BuildMenuCheck` skipped without JavaFX - against
0.7.0's 57 of 58, the one more being `HoldersCheck`, the fifty-ninth; after the
docs pass the model and the whole tree compile clean and `StaleCheck`,
`CentralBankCheck` (§1–15) and `HoldersCheck` (§1–8) are green; `StaleCheck`
0 firm, 72 soft, none of them this batch's; `SAVE_FORMAT` 27, every new thing
under its own key, appended, or a field Gson reads as zero; the suite rerun
after the reload fix, 58 of 59 again; the seed-0 baseline for batch D is
`playtest-baseline-0922c.txt`. Deployed as tag 0922b and every file verified
byte-for-byte on the PC.

### VERSION 0.7.0 — THE CENTRAL BANK OPENS — 2026-09-21/22, DEPLOYED AND VERIFIED WITH 0.6.11, see `the-central-bank-opens.md`

**Batch B of `the-central-bank.md` §12: the central bank's books and the
floor, with §6's advances ceiling moved in after batch A.** Jerus: *"the feds
sheet would show how much debt it holds, like debt to itself aka money
printing"* — and, asked what a treasury past its ceiling does, *"pay promises
first, cut the rest."* `CentralBank` is a balance sheet the player reads:
advances to the commercial bank (the window) and to the treasury, the city's
paper (zero until batch C) and the vault (read from `ForeignAccounts`) against
reserves and currency, which are **M0**; money is made and destroyed only
through its operations, and `MoneyAudit` sees it as a boundary participant
under a scope of its own (`Scope.MONEY`), money issued in and retired out, the
bank's pool read as its cash plus what it owes the window. **The floor:** the
bank's spare cash earns the policy rate at the central bank (`PLACEMENT_RATE`,
the world's 2%, is gone), its shortfalls are borrowed at the window at policy
plus `WINDOW_PENALTY` (the market's two points and stretch are gone), savers
get their share of what reserves earn, and `CITY_DISCOUNT` — "the one number in
this file that does not describe anything real" — is deleted, so the city's
paper is quoted at the dial plus its spreads and the bank's premium. On the
way it fixed a premium counted twice: the carry trade and the bank's deposit
bid were priced off `debtManager.getRate()`, which already carried the strain
premium, and `lendingRate()` added it again; both read the policy rate now.
**The advances and the arrears rule:** the emergency note is retired; a broke
treasury is advanced its gap at the policy rate, repays from the first cash
above zero, and may owe up to `MAX_ADVANCES_MONTHS` (six) of trailing revenue.
Every payment goes through `Game.treasuryPays(TreasuryLine, amount)`, and past
the ceiling promises (pensions, EI, health, the schools, the city's own wages,
coupons and principal, student loans already lent) are paid whatever it takes,
discretionary lines only from cash, what they refuse owed as interest-free
arrears by line and paid down first when cash returns, purchases simply not
made. The profit is remitted as a revenue line, the advances' interest is a
spending line; the autopilot hands the dial to the Taylor rule until the
player takes it back; a Money page under Finances shows the books, M0 and M2
and a year of each (`HistorySave`, the year book). Also: `refreshBank()` hands
the bank domestic principal only, a domestic buyback pays the bank
(`Bank.sellPaperBack()`), a 0% dial reloads at 0%, and the Build screen's note
is an ordinary bill on `BUILD_NOTE_MONTHS`. **The ensemble changes by design:**
median population 150,500 → 160,500, median GDP $632M → $767M, bank failures
24 → 8 across the eight, every seed borrowing at the window and none drawing
an advance; end-of-run M0 $0.8–3.1B, nearly all of it the central bank's
carried loss on reserves. **The six seeds that exploded in batch A, held at
10%,** end owing their central bank $0.1–2.0B instead of $193 quadrillion and
NaN, the audit closed every month, bank failures 161 → 16 on seed 0; the
arrears grow in a straight line and none recovers, and the rate still barely
moves inflation (+0.49% / +0.03% / −0.26% at 3, 10 and 20%) — batch E's job.
**Found on the way** (all on the list): no harness catches a doubled calendar;
the construction retainer is never paid; the Government tab has no Subsidies
line; student grants lag a month; a dollar-bond buyback pays nobody on the
books; a failed bank's hole still goes abroad (Jerus's question); M0 can read
slightly negative for a month; the ceiling is small in a small city. The docs
pass wrote `DebtManager`'s missing class header; rewrote as history `Bank`'s
funding abroad (the class header, THE FUNDING SIDE, WHAT TO PAY SAVERS, the
cost-of-funds note, capacity, the statements) and `MoneyAudit`'s header pool
list and boundary; flagged the failed bank's hole in `Bank.resolveIfFailed()`,
`MoneyAudit`'s `ResolutionLoss` and the Bank tab; marked the construction
retainer unpaid in `Game`'s banner and `buildWorld()`; added the ceiling to the
standing subsidy's "what it costs"; corrected `priceTheDebtMarket()`'s "three
inputs" (four), the rate band "1% to 11%" (the dial to ten points over it),
the dollar buyback's "its price goes there" (nothing is declared), and the old
funding rate in `CentralBank`'s header and `GameVersion`'s paragraph (struck on
the city's own paper rate, not the world's); said in `CentralBankCheck`'s header
that its section numbers are labels and why 9 runs before 8; named
`TreasuryLine`'s two exceptions and the arrears clause; brought
`TreasuryJournal`'s site list, the Finances tab's banner (four subjects) and two
of its alerts, the Policy tab's carry note and banner, `TimeSkipReport`'s
episode list (it never counted emergency debt), `YearBook`'s series count and
`CLAUDE.md`'s line counts (132,000; `Game.java` 8,700) up to date; and left
`TODO(docs)` on the foreign-default rule and `MonetaryCheck` §6's M2. Opus
implementer, Opus docs pass. Verification: compile-all clean; the suite 57 of
58 with the known line (`InfrastructureCheck`), `BuildMenuCheck` skipped
without JavaFX; `CentralBankCheck` §1–10 all OK; `CentralBankCheck`,
`BankCheck`, `MonetaryCheck`, `CreditCheck`, `TreasuryCheck`, `MoneyCheck`,
`ForeignDebtCheck`, `YearBookCheck`, `SkipReportCheck` and `StaleCheck` green
after the docs pass; the playtest byte-identical to its new baseline (0922b)
bar the wall clock; `StaleCheck` 0 firm, 72 soft, none of them this batch's;
`SAVE_FORMAT` 27, every new thing under its own key or appended. After the docs pass
the orchestrating session fixed the Money page's first sentence (it now
subtracts the remittance due), the overdraft alert (the whole shortfall is
always advanced; the ceiling limits the discretionary lines), and gave the two
policy-rate bounds their sentences. Deployed with 0.6.11 the moment the PC
came back (tag 0922a); every file verified byte-for-byte.

### VERSION 0.6.11 — THE BANK THAT NEVER PAID — 2026-09-21, DEPLOYED AND VERIFIED WITH 0.7.0, see `the-bank-that-never-paid.md`

**The ground for 7.0 (batch A of `the-central-bank.md` §12): the bank pays
for the city's paper, wages held to the index, and the number the policy rate
has to move.** Every bond and bill the city sells lands between two presses,
and the month snapshotted the bank's settlement (`cityDebtRaisedForBank`)
*after* the top-of-tick clear, so `bank.lend()` was handed zero for every
domestic issue since at least 0.4.3: the paper went onto the bank's book at
face, the coupons and the principal came in, and the cash never went out —
equity from nothing, and the treasury's money from nowhere. The snapshot is
taken before the clear now and zeroed once the bank has paid; what the bank
still owes is carried against its pool in `MoneyAudit`
(`Game.getCityPaperUnsettled()`) and saved under two `DataSave` keys, so a
city saved between the issue and the settle still pays. `BankCheck` §10
causes it — $31,000k of paper sold for $30,490.55k; the bank's cash moves by
exactly the ordinary month plus that, its book by the face, its equity by net
income only, the audit closed — and fails four ways on the old order. No seed
moves: the advisor only ever borrows the cheaper dollars. A new flag,
`-Dplaytest.borrowAtHome`, sends that borrowing home, and six seeds then sell
one term bond each around month 1,092, for which their banks pay $37.7M to
$87.0M where they paid nothing. **Wages track the index:** `LabourCheck`'s new
section holds `costOfLiving` to the published index on the two-year lag
through 240 months of steady inflation (index 1.06 → 2.97), a currency reform
and a reload, and `-Dplaytest.wages` shows every checkpoint on the eight seeds
at the lag-implied level exactly; the one-third overshoot of 2026-09-15 does
not reproduce. **The measurement:** `MonetaryCheck` §6 holds one founding at
3%, 10% and 20% from month 25 to 60 — inflation +0.53%, −0.06% and −0.34% a
year, through the currency alone; the assertion that inflation falls with
the rate goes in with batch E, and `-Dplaytest.policyRate` holds the dial for
a whole run. **And what the fix uncovered:** held at 10%, six seeds' treasuries
run dry around months 2,800–3,800; they used to survive on emergency notes
their bank was handed free, and once the bank really funds them the notes
price at 27–36% and compound without limit ($193 quadrillion on seed 0, NaN
on seed 6) — a free number was holding a floor, so the advances ceiling moves
from batch C into B, and this ships with it. Beside them: the trade page reads
`ForeignAccounts.previewPressure()`, the same arithmetic without the writes
(`ForeignCheck` §13, `ReadPathCheck`); the debt's monthly revaluation is saved
at slot 24 of the foreign accounts (`SaveFileCheck` §12b); `SAVE_FORMAT` stays
27; the playtest summary gains a line on the city's paper over the run. The
docs pass rewrote `MoneyAudit`'s pool list from `pools()` (the header said
"six private sectors" and no bank) and its pointer to a
`LongPlaytest.checkMonth()` that does not exist; corrected the settle's comment
on the discount (booked whole in the settle month, not "as it is earned"),
`LabourMarket`'s cost-of-living banner (it still said a third, over a year),
`BankCheck` §10's fail count (four, not three — rerun on a scratch copy with
the old order), the flag note's "seven seeds of eight" (six: seeds 3 and 5
never borrow), `SaveFileCheck`'s "the other nine sections", `DataSave`'s two
notes and `CLAUDE.md`'s line count (130,000); and added `effectivePressure()`'s
"it records" sentence and the new question to the headers of `BankCheck`,
`ForeignCheck` and `MonetaryCheck`. Opus implementer, Opus docs pass.
Verification: compile-all clean; the suite 56 of 57 with the known line
(`InfrastructureCheck`), `BuildMenuCheck` skipped without JavaFX; `BankCheck`,
`LabourCheck`, `MonetaryCheck`, `SaveFileCheck`, `ForeignCheck` and
`ReadPathCheck` green after the docs pass; the playtest byte-identical to its
new baseline (0922a) bar the wall clock; `StaleCheck` 0 firm, 72 soft, none of
them this batch's. Not deployed: the PC is offline, and this goes out only
with batch B.

### VERSION 0.6.10 — A RESERVE DEFENDS A CURRENCY — 2026-09-21, DEPLOYED AND VERIFIED, see `a-reserve-defends-a-currency.md`

**Prices and the rate on the strip, a founding vault, and the vault kept in
dollars.** Jerus, reading his 0.6.9 city's year book (prices 399x founding in
twenty-five years, the currency at its 100x guard): *"of the 3.5B you start
with, 1B is in usd in the reserve, so you only see 2.5B start with"*, and *"a
number visible on the screen showing both the price index and current
inflation year on year ... and a proper exchange rate which tells you how many
your coins equals USD"*. The always-visible top strip has two new panels
between the population and the cash (`UserInterface.refreshDateBar()`): prices
against founding over inflation year on year, and the rate both ways in the
currency's own names — `US$1 = D$x` over `D$1 = US¢y` (`Currency.FOREIGN_CENT_SYMBOL`)
— coloured by four `STRIP_*` dials against the 2% target and against parity;
every figure through a getter. The endowment is the same $3.5B, split:
`Game.FOUNDING_CASH` D$2.5B in the treasury and `Game.FOUNDING_RESERVE_USD`
US$1B bought on day one at the opening rate and booked as the purchase it is
(`lifetimeIntervention` carries it); the Exchange page and the summary panel
say where it came from for `FOUNDERS_NOTE_MONTHS` (120). **Found checking it:
the vault was in the wrong currency.** `ForeignAccounts.reserves` was a local
figure at the price paid, so a hundredfold fall turned a US$1B vault into
US$10M and its import cover fell with it, while the dollar debt beside it was
revalued every month. Now `reservesUsd` is the stock, its local value is
dollars × rate, cover no longer moves with the currency, and the month's move
is a revaluation line (`revalueVault()`, not cash, not an audit flow); save
slot 19 keeps its meaning (the local value when saved), slots 22–23 are
appended, an older save's vault comes back at the rate it was saved at,
`SAVE_FORMAT` stays 27, and `restore()` resets first so a save without a vault
does not inherit the founders'. **And the finding of the batch: two-sided
absorption.** The vault damped the pressure on the rate both ways, so a deep
one muted the surplus and the policy rate — the two forces that pull a
currency out of a spiral — and with the founders' dollars in it three of eight
seeds reproduced the year book (141x and 190x founding with the currency at
its guard, and 11x; median price swing 1.63x → 3.80x). Jerus's rule, *"a
reserve defends a currency; it does not hold one down"*: it damps only a push
weaker (`ForeignAccounts.effectivePressure()`, `lastAbsorption` is what was
applied), and the eight seeds went to a median swing of 1.60x, none past 2.81x
(the pristine worst was 7.13x), the dearest dollar 1.65, sudden stops 8 → 0,
bank failures 21 → 24, median write-offs $27.3B → $17.8B, population moving
both ways. The monetary page, its alert and the playtest log print the Taylor
rule and the dial's 25% stop separately (`DebtManager.ruleRate()`); the cap
stays until 7.0, Jerus's call. `HealthCheck` §13's two burial comparisons are
rates now (per person-month, per elder person-month), because the two cities
end at different sizes. `ForeignCheck` §9–13 new; §2 and §8,
`ForeignDebtCheck` §4b and `NewGameCheck` restated for a city that opens with
a vault; the vault is on the playtest's reload comparison. The docs pass
rewrote `ForeignAccounts`' "WHAT A RESERVE IS, TODAY" (it still described phase
one) and marked the four phases built, folded the new "THE VAULT IS HELD IN
DOLLARS" banner into "TWO NUMBERS THAT WERE ONE NUMBER" (it had emptied that
section in the code map), gave `COVER_WINDOW` its own sentence (the dial index
had been printing `importCover()`'s), moved a "save slot 19" javadoc to
`parity` as slot 18, and corrected "the reserve" to "the cumulative balance"
where the identity is meant (`ForeignAccounts`, `ForeignCheck`). Opus
implementer, Opus docs pass. Verification: compile-all clean; suite 56 of 57
with the known line (the implementer's run); `ForeignCheck`, `NewGameCheck`,
`ForeignDebtCheck` and `HealthCheck` green after the docs pass; the playtest
byte-identical to its new baseline (0921b) bar the wall clock; `StaleCheck` 0
firm, 74 soft, none of them this batch's; `BuildMenuCheck` and the strip by
eye on the PC. After the docs pass the orchestrating session moved
`Game`'s founding-reserve banner down beside the construction subsidy (it had
swallowed the constructor and the load path in the code map), corrected the
0.6.10 paragraph's count (two seeds past 140x, one to 11x), four sentences on
the Trade tab that were no longer true (cover does not price the next bond
abroad; absorbing a push never draws the vault down; "four forces" of six), the
strip's parity tooltip ("0% stronger"), and `DataSave`'s description of the
foreign array; the whole suite run again on the final tree, 56 of 57 with the
known line, the playtest byte-identical to 0921b. Files verified on the PC
byte-for-byte: 15 source files (`Currency`, `DataSave`, `DebtManager`,
`ForeignAccounts`, `ForeignCheck`, `ForeignDebtCheck`, `Game`, `GameVersion`,
`HealthCheck`, `LongPlaytest`, `NewGameCheck`, `ui/PolicyScreen`,
`ui/SummaryScreen`, `ui/TradeScreen`, `ui/UserInterface`), `CLAUDE.md`,
`docs/notes/` and the regenerated `docs/` (tag 0921d).

### VERSION 0.6.9 — THE PRICE OF A PLACE — 2026-09-21, DEPLOYED AND VERIFIED, see `the-price-of-a-place.md`

**The student grant becomes a menu, the student loan gets a rate, and a place
at school gets a price the player sets.** Jerus: *"grants its just a menu where
you can choose between a fixed amount, or a percentage of last month's surplus,
or a % as it is now of living costs, or a % of tuition. and then another slider
which is the interest rate for the student loans … and also make it so that you
can tweak the price of tuition as well."* The grant is a basis and an amount —
`TaxPolicy.GrantBasis` {`WAGE_SHARE` (the founding rule and the default),
`FIXED`, `SURPLUS_SHARE` (last month's surplus as one pool), `TUITION_SHARE`
(each student's own course fee)} and one `grantAmount` — struck by one rule,
`TaxPolicy.grantBill`, that the treasury's bill, the save's re-strike, the
students' row and the page all call. `TaxPolicy.studentLoanRate` (0–15%,
default 0) is the Canadian shape: nothing while they study, charged on a
graduate's balance during repayment and paid with the instalment, so the balance
stays principal and falls as it did; the interest is the treasury's own revenue
line, "Student loan interest" (`NationalAccounts` government slot 23), and the
principal stays on the bridge. `TaxPolicy.tuitionScale` (0–5×, default 1)
multiplies the founding table wherever a fee is read — and at ×3 the tuition
trap the `Education` header describes is back (1,718 students at ×1 against
1,173 at ×3 in a one-university city), the balance call the list carried now
at the player's hand. The Policy tab's Promises → **Tuition** page is
**Schools** (`PolicyScreen.schoolsPage()`): five dials on one foot bar, the
grant dial moved off "Out of work". **At the defaults nothing moves** — the
seed-0 playtest is byte-identical, and it has never built a school, so
`LongPlaytest` gained `-Dplaytest.schools=true` and four dial flags: eight
seeds at the defaults with schools and eight at tuition ×3 / grant 50% of
tuition / loans 5%, all sixteen clean; at the setting the treasury takes
$1.1–3.8M a month of interest and fewer study. **Found on the way:** the
schools' books were never restored on the load path
(`Game.rebuildSimulationState`, the sixth sighting of that gap — fixed); the
prisoner's $70 was the balance carried in, and the freeze is explicit now in
`PrisonerHousehold`; a founding village given a university overdraws the
treasury without bound, to NaN (the overdraft has no floor — filed, not fixed);
`HealthCheck`'s "two slots" is `TaxPolicy.STATE_BEFORE_HEALTH`; `stageSlider`
unstages its own key instead of the whole set; the docs pass found an uncalled
`TaxPolicy.studentGrantBill(...)` (removed) and the load path's grant-rate
comment saying both dials were re-told each month when only the scale is.
`EducationCheck` §13–16 (133 assertions OK); `SaveFileCheck`, `ReadPathCheck`,
`PolicyCheck`, `YearBookCheck`, `MoneyCheck`, `OutsideCheck`, `TreasuryCheck`,
`HistoryCheck`, `HealthCheck` extended or green; suite 56 of 57 with the known
line (`InfrastructureCheck`, "its shops can actually be supplied"),
`BuildMenuCheck` skipped without JavaFX; compile-all clean; `StaleCheck` 0 firm,
74 soft, none of them this batch's; `SAVE_FORMAT` 27, every new field a tail
append. Fable implementer, Opus docs pass, per the rule. Files verified on the
PC byte-for-byte: 25 source files (the 23 of the batch plus `GameVersion.java`
and `DataSave.java` from the docs pass), `CLAUDE.md`, `docs/notes/` and the
regenerated `docs/` (tag 0921a).

### VERSION 0.6.8 — HEALTHCARE HAS A PRICE, AND A PREMIUM — 2026-09-19 (night), DEPLOYED AND VERIFIED, see `the-price-at-the-door.md`

**Two dials on the health service, and the first thing in the game a household
can be priced out of.** Jerus: *"healthcare should be an adjustable price, all
the way to even make it a profitable business or the option to make it an
obligatory insurance payment system."* `TaxPolicy.healthFeeScale` multiplies
the three care fees 0 to 15× the founding ones (default 1; funerals unscaled)
and `TaxPolicy.healthPremiumRate` takes up to a tenth of every wage, employee
side, into the treasury with nothing balancing it — between them the Policy
tab's new **Promises → Health** page names three corners: free at the point of
use, a business past the city's own break-even (struck live), or insurance.
What makes the fee a decision is Jerus's rule for the household that cannot pay:
**it goes without care, not without food** — a cliff on the household's own
means, struck against the bill at full service so it settles instead of
swinging; that share of its people is untreated in the sick rate, the swings
and the births, and `served` means treated. **What the ensemble said:** at the
founding fee the cliff catches only households with nothing (the out of work
past EI, elders on a pension), 0–247 people a month, seed 7 never; the
eight-seed means moved −12% population, −13% GDP, but two seeds ended inside the
model's known emptying-out and the other six are flat (+1%, +3%). The ceiling
went 5 → 15 after the first measurement, because a played city breaks even at
×7–13 against fees set at founding wages. **Found on the way:** `buildWorld()`
never rebuilt `health`/`healthcare` (fixed); a hair of borrowing is a discrete
state (`fundShortfall()` / `investAbroad()`, the within-row re-split dropped
over it); the People and Summary screens recomputed coverage from beds — now
`Healthcare.getCoverage(care)`, saved, is what they read (the docs pass found
it). `HealthCheck` +5 sections; `SaveFileCheck`, `ReadPathCheck`,
`NewGameCheck`, `RestaurantsCheck`, `BusinessServicesCheck` extended; suite 56
of 57 with the known line; `SAVE_FORMAT` 27, every new field a tail append.
Fable implementer, Opus docs pass, per the rule. Shipped with the treasury batch
below in one deploy.

### THE TREASURY BRIDGE OPENS, AND THE DESK FOOTS — 2026-09-18/19 (night), DEPLOYED AND VERIFIED, behaviour-preserving, see `the-treasury-bridge-opens.md`

**The Government tab's "Everything else the treasury did" row opens into named
lines, and the bank's trading desk adds up.** Jerus: *"it just says 'everything
else' — that should be expandable, cause a lot of times that's where a bunch of
important things happen."* `TreasuryJournal` records every movement of the
city's cash that is neither a budget line nor paper raised or repaid, where it
happens, in the player's words — "Put capital into the bank", "Bought
reserves", "Bought back a bond", "Lent to students, net of repayments" — struck
press to press, carried in the save for both the month that ended and the one
in progress; what the lines do not explain prints as "Not accounted for"
(on a month the city borrows, the first coupon's timing, to the cent). Land and
buildings stay on the budget's own line and are deliberately not named twice.
On the bank's statement the desk's opened lines now carry
`Bank.getMarkChange()` as "Re-marked what it holds", and when the re-mark is
the bulk of a loss the note says why — which is the answer to *"explain to me
the trading desk, cause a bunch of times it's losing billions"*: it bids at a
quote that unfilled demand has pushed to 2–4× fair value, and marks at fair,
so emigrants selling into a bubble cost it the difference the same day. **What
it found:** "Raised by issuing paper" had read $0 on every month the city
borrowed (the counter is cleared before the strike — the bridge has its own
now); the city's repairs and the transit fares are on the Government screen and
not in `NationalAccounts`' totals (journalled by name until they are); and,
not fixed, **the bank never pays for the city's paper** — `cityDebtRaisedForBank`
is snapshotted after the clear, so `bank.lend()` receives 0 for every city
bond. `TreasuryCheck` +1 section and a third fixture city; `BankCheck` a new
section — lines plus re-mark equalled the total in 120 of 120 played months.
**Playtest byte-identical to the baseline; nothing about how the cash moves
changed.** Fable, then merged with the health batch and gated again on the
union (identical to the health tree's run).

### THE MANUAL AT 0.6.7 — 2026-09-18 (night), PUBLISHED as version 7, see `the-manual-at-0-6-7.md`

**THE PUBLISHED MANUAL IS AT 0.6.7 / FORMAT 27 (version 7, same URL).** Twenty
sections instead of nineteen: a new **§17 Transport & vehicles** told in the
order the nine batches were built — the band that was a freight bill, the load
that was three loads, the modes and the fare, the railway, the assembly plants,
the cars, the vans, the instrument panel — and every other section brought to
the tree: the sixth age band's mortality table (version 6 had shipped with five
bands three sections away from the paragraph that described the sixth),
fifteen shapes, seventy-eight ledgers, twenty-nine slots a cell, the wealth
term and the three counters, the cars and the loans and the used market beside
the households; fifteen sectors with the eleventh to the fifteenth written out;
thirty-one goods with a freight column; sixteen companies; the GDP formula
re-struck; the concentration limit; seventy-three templates; the `ui` package,
the statement that opens, twelve rail tabs; fifty-seven harnesses with six new
rows and the *Decided by dust* note; the month at thirteen steps against
`docs/month-order.md`; and §20 reconciled against the list — one retired (too
rich against its food: it was the coupon, then the supply wall), two
half-answered, twelve added. Vitals: 205 files, ~124,000 lines, 73 buildings,
15 sectors, 57 harnesses; six figures from a playtest run tonight, dated on the
page. **Made by three contexts** per the model rule: a Fable implementer from
the published page and nothing local (36 minutes, 197 calls), an Opus reviewer
who re-ran the suite and reproduced every dated figure and made four edits (a
merged rail measurement, the hot money after the audit, a line count at ten
sectors, and a published sentence wrong since it was written — 13 months for
18), and the orchestrator on six judgement calls, then the whole page read
before the publish guard would take it. **Found on the way, not fixed: twenty
places the tree's prose, the notes or the list disagree with the code** — a
"70%" above a `.75`, a fare javadoc in the wrong unit, "sixty-eight cells"
above seventy-eight, a Diner sized from eight staff over a template with four,
five bands in `AgeBand`, `nextId` 69 under id 72, the todo's "nine sectors
ignore the brake" (none do) — listed in the note and in the todo for the next
docs pass. **Share pin still at version 1** — Jerus's, from the page's share
menu.

### THE FOUR OUTLIERS — 2026-09-18 (night), DEPLOYED AND VERIFIED

**THE FOUR COMMENTS THE STALE PASS FLAGGED, REWRITTEN ON JERUS'S WORD** ("the
code is correct, the comments are the outliers"): `Education.foundingTuition()`
says 1.20; `FamilyModel.restore()` says six lengths accepted and lists them,
shortest first, each the one before plus a block with its date;
`HouseholdBalance.CELL_SLOTS` ends at the meals eaten out;
`ForeignAccounts.lifetimeIntervention` names `balanceFromFlows()`. Comments
only — no bytecode changed, compile clean, `Stale` firm 0/0/0, maps
regenerated. Eight files on the PC, byte-for-byte, endings intact (three of the
four Java files CRLF). **Found on the way, not fixed:** the tuition table's prose measures the
fee against "a diploma wage of 1.500", and that wage has been 4.500 since the
rebalance — so the poverty trap the class header calls its most interesting
thing has been mostly quiet for nine days; a balance call, in the list under
Housekeeping. Opus, per the rule.

### THE STALE TOOL, AND EIGHTY-FOUR DEAD JAVADOCS — 2026-09-18 (evening), DEPLOYED AND VERIFIED, see `the-prose-that-stopped-being-true.md`

**THE MECHANICAL HALF OF THE DOCS PASS IS A TOOL, AND IT RUNS IN THE SUITE.**
`tools.Stale` reads the tree and `docs/` and reports prose that stopped being
true: three FIRM categories — a javadoc with no member under it, prose naming
a `.java` file or a `docs/` page that is not there, a stated count of banners,
harnesses or interface files that is out — and three soft ones it prints and
does not assert (a `name()` nothing declares, a map older than its source, a
fuzzy count). `StaleCheck` asserts the firm three and is the fifty-seventh
harness; `Stale report.bat` prints the list on the PC. **It shipped red on
purpose: the first run found 84 orphaned javadocs across 40 files, all real,
all older than today** — the same shape the two hand docs passes had been
finding one at a time. A second fresh agent worked all 84 (45 moved onto the
member they describe, 24 superseded one-liners deleted, 9 stacks fused —
`VERSION` and `SAVE_FORMAT` carry their own history now — 5 dead ones deleted,
one turned into a plain comment), plus 13 soft renames; fifty files, thirty-two
CRLF, endings intact. **Playtest byte-identical, compile green, 57 harnesses
56 green with the same `InfrastructureCheck` line, `StaleCheck` green at 0/0/0.**
Both agents ran on Opus, per Jerus's rule of the evening (Opus by default,
Fable when the job is substantial, Opus for docs passes) — and both did the
job. Flagged for a reader who knows the mechanic: four comments whose numbers
disagree with their constants (in the list). 109 files on the PC, byte-for-byte.

### GAME.JAVA, GENTLY — 2026-09-18 (evening), DEPLOYED AND VERIFIED, see `splitting-game.md`

**FOUR MECHANICS OUT OF `Game`, BEHAVIOUR BYTE-FOR-BYTE UNCHANGED, AND THE
FIRST BATCH DONE BY TWO AGENTS.** The households' car market is `Motoring`,
the boutiques and the restaurants `LuxuryCounter`, who is at risk of
offending `Offending`, what the city eats `CityBasket` — each beside `Game`
in the root package, the banner prose as its header, the body line for line
through Game's public getters (none had to be added), and every moved getter
kept on Game as a delegation so no caller changed. The currency reform stays:
it redenominates twenty-two subsystems and is the seam's own job. Found on
the way: the luxury and offending banners had been covering nine
bank-and-exchange methods and a run of readings, which have banners of their
own now, plus a third from the docs pass; `Game.java` is 7,810 lines and 24
sections. **Proved by the 4,002-month playtest, deterministic and
byte-identical to the baseline after every one of the four moves and after
the docs pass**; compile with the UI green, `BuildMenuCheck` green headless,
CarCheck / RestaurantsCheck / CrimeCheck / ConsumptionCheck pass, 55/56 with
the same red line. 44 files on the PC, byte-for-byte. **The experiment:** an
implementer agent with a fresh context did the moves (7 and 13 minutes), a
docs-pass agent with another followed each — and found seven stale
`Game.motoring()` references the implementer's grep missed, then the "car
market" sentence pasted into three other headers. The why has to be in the
implementer's brief; the gate stays with the orchestrator. §5 of the note.

### THE DOCS PASS — 2026-09-18 (evening), DEPLOYED AND VERIFIED, see `splitting-the-interface.md` §13

**A SIXTH STEP IN EVERY BATCH, BY A FRESH CONTEXT.** Jerus's idea: the agent
that changed the code does not document it; a second one, with nothing in
its head but the diff and the design note, makes every comment, header,
index and count agree with the code again. `docs/docs-pass.md` is the brief
(regenerate; read the diff and the comments in reach of it; class headers;
the record; the README/CLAUDE counts; never change behaviour, never invent a
why, never renormalise); `Docs pass.bat` runs it headless on the PC against
the last commit; `docs/docs-pass-agent.md` is the same as a Claude Code
subagent; `docs/notes/pending.md` is where a run that cannot reach the
project leaves its lines. **Its first run, over the split and the three UI
fixes, found and fixed:** four stranded javadocs (re-homed to
`showBankMenu`, `bopLedgerPage`, `panelDashboardSections`, `wheelToPage`),
a false claim in `TradeScreen`'s header and one in `GovernmentScreen`'s,
`Pieces`' header, two blank dials, `JavaScan`'s "25,000 lines", `NAMES` →
`HARNESSES` in `CLAUDE.md`, and the split note's own file count. 34 files on
the PC, byte-for-byte; compile green after it. Open: a `Stale` tool for the
mechanical half.

### THREE SMALL THINGS ON THE SCREENS — 2026-09-18 (evening), DEPLOYED AND VERIFIED, see `seven-tries-at-a-scrollbar.md` (afterwards)

Jerus, after opening every tab on the split: three minor ones, "dont worry if
you cant pinpoint". **The transit fare dial carried the player to the Policy
tab** the moment it was let go, because the staged-lever slider and its apply
bar redrew `showPolicyMenu()` by name; they redraw whichever screen registered
itself last now (`ui.redraw()`), which on Services is the transit page. **The
build tab scrolled twice as fast as every other tab**: it is the one screen
whose content sits straight in the page, so it was the one screen the wheel
floor ran on, and a device that sends a notch as a burst of small deltas had
every one of them raised to 48px; the floor goes under the first event of a
gesture only. **The Reports tab moved under the pointer when a line was
ticked**, because the legend under the chart grows a block per line and the
picker sits below it; that one page keeps its position as pixels from the
bottom (`scrolled(column, chrome, true)`). The two scroll fixes are reasoned
from the code, not traced — the write-up says so, and the build is the test.
Maven-style compile green, `BuildMenuCheck` green, 55/56 unchanged; four
source files and the maps on the PC, byte-for-byte.

### SPLITTING THE INTERFACE, BATCHES 5 AND 6 — 2026-09-18 (evening), DEPLOYED AND VERIFIED, see `splitting-the-interface.md` §11

**EVERY SCREEN IS OUT. `UserInterface.java` IS 3,980 LINES.** Nine more
classes in `ui/`, each a run of banners moved verbatim with the shell's
members reached through `ui.`: `BankScreen` (1,605), `FinancesScreen` (1,698,
plus THE DEBT RESULT which had sat at the tail of the build cards),
`TradeScreen` (1,695), `PolicyScreen` (2,520), `LandScreen` (403),
`SectorScreen` (1,299), `ServicesScreen` (2,537), `GovernmentScreen` (1,416),
`BuildScreen` (1,726) and `SummaryScreen` (1,609 — the left panel's content:
the summary, the dashboard, the problem list, seats against who would come).
What is left is the window: theme, clock, strips, save, settings, the
scroller, the time-skip dialog, the construction panel, the rail, the inbox,
the pips. It has a class header, the twelve screen fields in rail order, and
one WHERE THE SCREENS WENT block in place of scattered pointers. **Found on the
way:** `formatter`'s `static {}` had stayed behind when the field went to
`Money` (moved); `marked()` and `currencyReformPage()` got their orphaned
javadocs back; `TILE_WIDTH/HEIGHT/GAP` (shared by the build cards and the land
plots) are `Pieces`'; `BuildMenuCheck` reads the three card methods through
three forwarders in the shell, because it cannot see a package-private screen;
and **`JavaScan` had been filing every `@Override` method as an initializer**
(`skipAnnotation` swallowed the words after the `@`) — fixed, and the maps of
the sectors and harnesses gained their overridden methods. **Maven-style
compile green, `BuildMenuCheck` green headless, 55/56 with the same
`InfrastructureCheck` line.** 124 files in the deploy set (ten new classes and
their maps, the shell, `PeopleScreen`, `HistoryScreen`, `Pieces`, `Money`,
`JavaScan`, README, CLAUDE.md, 96 regenerated docs), **every one
byte-for-byte on the PC**; nothing to `git rm`. Still to do there: Clean and
Build, open every tab, run a few months.

### SPLITTING THE INTERFACE, BATCHES 3 AND 4 — 2026-09-18, DEPLOYED AND VERIFIED, see `splitting-the-interface.md` §9–10

**THE FIRST TWO SCREENS ARE CLASSES.** `HistoryScreen` (1,383 lines: THE
HISTORY SCREEN, THE RECORD) and `PeopleScreen` (2,530: the six PEOPLE
banners), text verbatim, `private` off both sides of the seam, the shell's
members reached through `ui.`, the shell reaching theirs through
`historyScreen.` / `peopleScreen.` — the `<name>Screen` convention after a bare
`people` field collided with a local `PopulationManager people`. The move is a
script over the map (`screen-move.py`: a dry run prints the couplings, the
real run rewrites exactly three kinds of name and the compiler catches the
rest). **A second toolkit pass** put thirteen more pure helpers in `Pieces`
(`keySwatch`, `stepChip`, `keyedBar`, `payerRow`, `annualGdp`, `gdpEstimated`,
`cell`, `flowText`, `shortTier`, `showIf`, `trendChart`, `chartFigure`,
`latest`) after the dry runs showed the bank calling the finances screen's
swatch. Jerus opened the game on each: "ok its still works", "ok its good now".

### SPLITTING THE INTERFACE, BATCH 2 — 2026-09-18, DEPLOYED AND VERIFIED, see `splitting-the-interface.md` §8

**THE TOOLKIT IS OUT; NO CALL SITE CHANGED.** `Money`, `Statement`, `Pieces` and
`Levers` in `ui/`, 742 lines of stateless helpers as `public static` methods
behind four `import static` lines; `UserInterface` is 24,305 lines, down from
25,101. The five constants the rows read (`STATEMENT`, the two book widths,
the two disclosure glyphs) and the `Slice` record went with them; the only
edits inside the file were four method references (`UserInterface::pct2` and
friends are `Money::` now). Three names the plan listed stayed, each because
the purity check said so: `scrolled` (reads the scroll state), `applyBar`
(calls the policy screen), `stageSlider` (reads the staged set). **316 classes
against JavaFX, `BuildMenuCheck` green, 55/56 with the same red line, 27 files
byte-for-byte on the PC.** Nothing to `git rm`. Next: the history screen.

### SPLITTING THE INTERFACE, BATCH 1 — 2026-09-18, DEPLOYED AND VERIFIED, see `splitting-the-interface.md`

**THE `ui` PACKAGE EXISTS. NO SCREEN CHANGED.** `UserInterface.java`,
`Palette.java` and `Icons.java` are in `ham/citybuildersim/ui/` with their
package line changed and one `import ham.citybuildersim.*;` — nothing inside
them moved. The launcher and `BuildMenuCheck` import the new place; the
`UserInterface(Game)` constructor and the three card methods the check reads
are public. **The compiler named exactly eight model members** the interface
had been reaching through the shared package, now public: three `Game`
getters (city and foreign principal repaid, foreign interest paid),
`RetiredHousehold.pensionersIn`, `Household.creditRoom` and its two overrides.
Nothing else in the model changed. **312 classes against the JavaFX jars,
`BuildMenuCheck` green headless, 55/56 on the mirror** (the same
`InfrastructureCheck` line), 27 files byte-for-byte on the PC; `CLAUDE.md` and
the README say where the interface lives now.

**FOR JERUS:** the three old files at the root are one-comment stubs — `git
rm` them. Then Clean and Build and open the game; it should be the same game.

**THE PLAN** (`splitting-the-interface.md`), measured on the file: a 4,500-line
shell that stays; a 700-line toolkit of helpers with no state, which move as
`static` behind `import static` so no call site changes (batch 2); and twelve
screens of 250–2,900 lines that move verbatim with a scripted `ui.` prefix,
one per batch, History first (batch 3). Twelve model-facing members and two
imports were the whole cost of the boundary.

### THE STRUCTURE AUDIT — 2026-09-18, DEPLOYED AND VERIFIED, see `the-ai-ergonomics-audit.md`

**NO GAME CHANGE. 55/56 on the mirror, as this morning** — `InfrastructureCheck`'s
*"...and its shops can actually be supplied"* is still the one red line.
`GameVersion` stays 0.6.7, save format 27.

**A tools package, `ham.citybuildersim.tools`**, that reads the sources as text
and writes `docs/`: `CodeMap` (one page per file — its banner sections and every
method with its line; `docs/map/README.md` is the index and carries the version),
`Dials` (all 681 constants with the sentence above each), `MonthOrder` (the
month's spine as a numbered list with where each call goes), `HarnessMap` (the
3,061 labelled assertions under their sections, which harnesses name which
class, and whether every `*Check` is in `AllChecks`), `Maps` (all four; what
`Regenerate maps.bat` runs), `Where` (find a member or a banner by name and print
it) and `SaveDump` (look inside a save without loading it). `JavaScan` under
them is a tokenizer plus brace counting, no compiler, checked against `javap`.

**At the root:** `CLAUDE.md` (the briefing: what to open first, the standing
rules, the loop, where the shape is for adding things), `AGENTS.md`,
`.gitattributes` (LF; the one-time renormalise command is in its comment; not
yet run), `Regenerate maps.bat`, and the README's counts brought current (fifteen
sectors, fifty-six harnesses, seventy-odd buildings, "about 180 files") with a
section on the generated documents. **Thirteen files new and the README
changed, plus 187 generated pages under `docs/` — 201 files, every one
byte-for-byte on the PC.**

**In the project:** the todo split into `todo.md` (the list, section 0 at line
5) and this file; `index.md` written for all 200 notes. **Open, for Jerus:**
the UI split, the docs mirror, deleting the twenty-seven stale Java uploads,
the renormalise commit — see the audit's section 3 and the list's sections 0 and 6.

### VERSION 0.6.7 — RESTAURANTS — DEPLOYED AND VERIFIED, see `a-meal-out-is-food.md`

**SEVENTEEN FILES, 56/57 on the mirror.** Still only `InfrastructureCheck`'s
*"its shops can actually be supplied"*, deferred. **SAVE_FORMAT stays at 27** —
the sector is keyed by name and the dinners a household ate are appended to a
cell array whose reader checks its length.

**THE FIFTEENTH SECTOR, AND THE RULE JERUS ATTACHED TO IT: a meal out REPLACES
groceries.** So it imports NOTHING. A dinner is the same thirteen foods off the
same shelf at the same prices, which makes the kitchens a SECOND BUYER in a
market the city is already short of — which is where *"let the basket get
dearer, not bigger"* actually bites — and a SECOND DOOR to the food there is.
A meal is one ninetieth of a person-month (three a day, thirty days) and the
hunger measure counts it **at what a person-month of food costs, not at what the
kitchen charged**: three to eight times the food is wages, rent and somebody
else's washing up, and none of it is nourishment.

**EIGHT SEEDS: MEDIAN HUNGER 36% → 22%**, sickness 18.1% → 16.6%, population
157,069 → 161,468. The kitchens feed **11%–26% of each city** — a quarter of it
on the best seed — which more than covers the 5% of baskets they take off the
shops. GDP median is down 8% on ranges that almost entirely overlap, so probably
noise, but it has not been isolated.

**THREE CALIBRATION FAULTS, ALL CAUGHT BY THE NEW HARNESS AND BY NOTHING ELSE:**

- **The sector had no demand at all.** `mealWant` was a share of the SURPLUS and
  the surplus is exactly zero in any rich city, because `want` already carries
  the wealth term. 4,000 months: **0 meals asked for, 17 write-downs, $3k to its
  name**. The ceiling that makes this sector different — a fortune buys a
  DEARER meal, not a ninety-first one — belongs where a meal has a price, as
  `MOST_MEALS_EATEN_OUT = 1/3`, not as a missing term in the plan.
- **The kitchens could not pay their staff.** Two calibrations in a row. **A
  BUILDING PAYS ITS STAFF AT 100% AND SERVES AT THE OPERATING RATE**, which in
  these cities is about half. The first cleared 29% of its wages at that bar and
  the second 62%; both died with the margin pinned at the ceiling and millions
  of diners queuing.
- **The band had to be wider than the trade's own 2.9x–3.6x.** That figure is
  for a place where food and labour each take a third. At month 4,002 a kitchen's
  food bill was $8.9k against $34.1k of wages — **labour at four times food** —
  because three centuries of farms made food cheap while the wage schedule did
  not move. 3.0x to 8.0x now.
- **...and a latent trap:** `recentUse()` = "what I served last month" is
  SELF-LIMITING. A kitchen throttled by a bad road buys half a month of food,
  which lets it serve half a month next time, for ever. It stocks for the
  smaller of its tables and the queue now.

**TWO HARNESSES MOVED AND NEITHER PREMISE DID.** `ForeignCheck` measured the
whole city's imports against a premise about a fixed public-works programme, and
the city had grown sectors that are not the programme — **Luxury Retail $138,850k
at parity against $65,881k weaker**, the whole of the 7.4% it was out by, found
on the per-sector line that is printed rather than asserted *precisely so the
next person could see this*. Both shops held out now; the premise is unchanged
and the instrument is sharper (0.9788 against 0.9805 last time).
`BusinessServicesCheck`'s cell width went 28 → 29, by hand, which is what that
line is for.

**STILL OPEN HERE:** a third of meals eaten out is an untested dial; the sector
finishes 7 of 8 seeds with ZERO write-downs at 2.00% credit, which is the profile
of a business that cannot fail; and **Luxury Retail still has no harness of its
own** — every bootstrap fault the kitchens hit had already been paid for there.

---


### VERSION 0.6.6 — 2026-09-18, DEPLOYED AND VERIFIED, see `a-ride-is-not-a-month.md`

**NINETEEN FILES, 55/56 on the mirror.** The one red is `InfrastructureCheck`'s
*"its shops can actually be supplied"*, which Jerus deferred: *"as for the roads,
its fine, we will balance later."* **SAVE_FORMAT stays at 27** — the sector is
keyed by name and the accounts' luxury baseline is appended to an array whose
reader checks its length.

**THE FARE WAS CHARGED ONCE A MONTH AGAINST A CONSTANT THAT SAYS "A SINGLE
JOURNEY".** Jerus caught it by reading the number: *"2.5 for a month... way way
way way too good of a deal lol"*. `getTransitRiders()` is a HEADCOUNT of
commuters, so a monthly pass cost $2.50 against a $3,460 wage — seven hundredths
of one percent — and the buses could not have paid for themselves at any fare a
player would set. **That is the transit loss, and it was never a balance
problem:** the wages were priced per month, because a driver is paid per month,
and the fare was priced per ride and then charged once. One of the two numbers
was in the wrong unit and it was not the payroll. `JOURNEYS_A_MONTH = 40` (out and back, twenty days), a pass is now $100 or
**2.9% of an unskilled wage**, and the dial stays PER RIDE because that is the
number a person on a platform recognises.

**LUXURY RETAIL, THE FOURTEENTH SECTOR.** Boutiques and Department Stores that
import what they sell and strike their margin against the queue at the door —
1.25× to 4.00×, the house strike rule with a mark-up in place of a price. It is
the answer to *"if customers want more, then the bid goes up no?"*. What it is
FOR is the hoard: household net worth stops diverging (5,072 months of GDP) and
starts oscillating (432 → 137 → 128 → 214).

**THE NEGATIVE GDP WAS IMPORTS, AND ALSO THE STOCK THEY BOUGHT.** Jerus:
*"negative gdp is just cause lots of imports no?"* — half right. A Boutique's
three-month stockroom (960 pieces at $9) is imported in one month; NX takes the
landed cost out and nothing said the city still had the goods. Fourth term in
`investmentInventories`, priced at what the shop paid, so the arrival nets to
zero and the MARGIN is the only thing that scores. **60 negative months → one
across eight seeds and 32,040 months**, and that one is the construction shape.
**The rule, since it is now three: any good a sector can hold and does not
consume within the month belongs in that block the day the good is written.**

**AND ONE LINE IN `Exchange.redenominate()`, WHICH IS THE REAL STORY.**
`bookLimit` — BOOK_LIMIT × the bank's equity, which is money — was not divided by
a reform. It is re-struck each month in `startMonth()`, which runs AFTER the
households settle, so **for the first half of every month a reformed city's share
desk believed it had a hundred times the room it had.** Cell 69 of
DenominationCheck's fixture — no savings, nothing abroad, a grocery bill it could
not meet — borrowed in the plain city and sold $1.03 of shares in the reformed
one. From there: **household debt 4.2% apart in ONE month**, the share flows with
it, the households' money abroad with those, and the exchange rate with that.

**THE BISECT NAMED THE WRONG THING AND THAT IS THE LESSON.** Removing the luxury
inventory term made the harness green, so the obvious reading was that the term
was wrong. It is not. It moved GDP, GDP moved what the desk's book was worth
against its limit, and a cancellation that had been landing on one side of zero
started landing on the other. **A latent member of the money-constant family is
not a harmless one; it is one nobody has perturbed yet.** New assertion, with
teeth measured (9 of 15 companies go red without the fix): `deskCanBuy()` answers
in SHARES, so a reform must leave it alone.

---


### WHO IS PLAYING — 2026-09-17, DEPLOYED AND VERIFIED, see `who-is-playing.md`

**TWO BATCHES ON THE PC, 56/56 on the mirror after each.** `sectors/Retail.java`
(CRLF), `Game.java` (LF), `HouseholdBalance.java` (CRLF), `LongPlaytest.java`
(LF). **SAVE_FORMAT stays at 27.**

**THE HUNGER MEASURE WAS REPORTING ZERO IN A CITY THAT WAS STARVING.**
`getSupplyRatio()` is sold over `rDemand`, and `rDemand` is already
`min(coverage, want, affordable)` - the shops' performance against a queue the
shops chose. At month 3,840 of seed 0: **households planned 21,446,946 baskets,
the shops could serve 177,760 people, and the line said 76%.** A basket is one
person-month, so the city wanted **122 baskets a head and was allowed one**.
Honest: **58% of people short, 1.4% of the groceries asked for, 75,568 baskets
sold to 177,124 people.**

**NOT A REPORTING CHANGE** - `hungryPeople` feeds sickness feeds the labour
supply, and was being fed a zero. Cost: pop 166,199 → 148,479, GDP $630.5M →
$544.3M, sickness 12.9% → 34.5% - **revealed, not caused**. Of that sickness,
hunger is 8 points and **unburied bodies 15**: hungry people get sick, sick
people die, two cemeteries in three centuries cannot keep up, and the bodies make
more people sick. The chain was always wired; nothing was coming into the top.

**A MONEY CONSTANT IN ITS QUIETEST COAT:** dividing money by money and keeping
the fraction broke DenominationCheck 1.5e-06 at a time, because `(a/100)/(b/100)`
is not bit-identical to `a/b`. The line replaced had **both sides integers** -
exact by luck. The basket count is floored now.

### AND THE MODEL WAS FINE ALL ALONG

The shops run at 43% because **the advisor built five wind farms and two
cemeteries in four thousand months**. A player that once a year tops up power,
water, streets, clinics and stores takes the operating rate 0.62 → **0.89**,
hunger 42% → **22%**, sickness 27.6% → **9.5%**. Not because `advise()` is bad -
it ranks constraints by output lost, which is right - but because **the loop
skips an average of 54 months between looks**. Every number this harness ever
reported was measured in a city in crisis from neglect.

**`-Dplaytest.player=attentive`** - yearly, eight moves a look, same `advise()`:

| | default | attentive |
|---|---|---|
| pop | 148,479 | **418,948** |
| GDP/mo | $544M | **$2,125M** |
| roads | 82% | **97%** |
| hunger | 43% | **18%** |
| sickness | 23.1% | **7.8%** |

**An attentive player builds 2.8x the city and nearly 4x the output** - which is
what you want in a city sim. **The default is provably untouched: eight seeds,
four thousand months, bit-identical to before the flag.** Every ensemble in these
docs stays comparable.

**NEXT, AND NOW THE ONLY CONSUMPTION ITEM LEFT: THE DEARER BASKET.** Even the
attentive city gets 0.5% of the groceries it asks for, because the person-month
cap is untouched - nobody eats twice. A rich household should buy a DEARER
basket, not more of them. (Jerus's call, taken over new consumer-goods sectors.)

**ALSO NEWLY VISIBLE:** the attentive city runs at **89% fill** - a labour
ceiling the neglected harness was never rich enough to reach.

**AND ONE I CANNOT EXPLAIN:** over 600 months the *neglected* city ends up BIGGER
and richer than the attentive one while being sicker and hungrier; over 4,000 the
ordering reverses decisively. Something about the early game.

---


### WHY THE HOUSEHOLDS WERE RICH — 2026-09-17, BUILT AND VERIFIED, NOT DEPLOYED, see `why-the-households-were-rich.md`

**SIX FILES STAGED, PC OFFLINE.** `HouseholdBalance.java` (CRLF), `Household.java`
(LF), `MoneyAudit.java` (CRLF), `ExchangeCheck.java` (CRLF),
`BusinessServicesCheck.java` (LF), `FoodProcessingCheck.java` (LF). **56/56,
three 8-seed ensembles, no findings. SAVE_FORMAT stays at 27** (the cell save
carries a sixth figure on the cars' versioned-slot pattern).

**THE DIAGNOSIS.** Household net worth is **45 months of GDP at month 480 - dead
on the real-world 60-84 - and 5,072 by month 3,840.** It does not sit at a wrong
level; it diverges. Cumulative foreign coupons over the run came to **$2,649bn
against $584bn of every wage the city ever paid**, overtaking wages around month
2,400. Households keep ~79% of their wealth abroad at the world's 2% and the
coupon was rolled where it was earned, so **wealth compounded at 1.58% a year
mechanically** - 193x over 333 years, which is exactly the curve.

**THREE CHANGES, ENSEMBLED SEPARATELY (Jerus's call).**

| | pop | GDP/mo | net worth, months of GDP |
|---|---|---|---|
| before | 158,252 | $679.0M | 5,380 |
| A — coupon paid home | 148,464 | $457.7M | 3,412 |
| B — investment income into the plan | 153,630 | $543.9M | 5,465 |
| C — a wealth term (`WEALTH_SPENT_A_MONTH = .0033`) | **166,199** | **$630.5M** | **2,737** |

**B MADE THE RATIO WORSE AND THAT IS THE LESSON.** Investment income is a FLOW;
spending more of a flow makes the city bigger, a bigger city pays more wages and
dividends, and MPC saves a fifth of all of it. **A stock that compounds can only
be bounded by a drain that reads the stock.** C is the best city of the four -
biggest population, hunger 9% → 0%, shops delivering 76% of plans instead of 52%.

### AND THE REAL CAUSE, WHICH IS NOT THE CONSUMPTION FUNCTION

With C in, the stock still diverges (3,982 months by m3,840). So the coefficient
was pushed to **36% a year** - nine times the empirical range - to find out
whether the consumption side CAN bound it. **It cannot.** What fell instead was
delivery: **76% of plans → 57%**.

**The city cannot sell its households what they already want to buy, and whatever
is not delivered is banked by construction** - `settle()` has nowhere else to put
it. **They are not hoarding because they are misers; they are hoarding because
there is nothing to buy.** `the-savings-that-cannot-be-spent.md` asked half of
this before any of it was measured.

**NEXT: THE SUPPLY WALL.** Why can the shops fill only three quarters of a plan
in a city with a hundred billion of unspent demand in front of them?

**FOUR HARNESS FINDINGS, all declared in the write-up:** a one-sided audit
declaration cost 13 red harnesses (**household savings are not an audited pool**,
so every household foreign flow needs both legs - the scope moved, not the
existence); **a new money field left out of `redenominate()`** (the test: *is the
field money? then it belongs there the day it is written* - first sighting of
that family); `ExchangeCheck` had the old rolling rule in its arithmetic; and
`FoodProcessingCheck` was **passing on timing** and now holds the sector so the
state under test is reached by construction.

**STILL OPEN FROM THIS AREA:** `discharge()` never looks at savings, which is why
a city with no poor people manufactures bankruptcies. And
`MARGINAL_PROPENSITY = .80` has no life-cycle behind it - a fifth of discretionary
income saved for ever, by everybody, at every age, is the third leak and entirely
untouched.

---


### THE CAR GOES LAST — 2026-09-17, BUILT AND VERIFIED, NOT DEPLOYED, see `the-car-goes-last.md`

**FIVE FILES STAGED, PC WENT OFFLINE MID-BATCH.** `HouseholdBalance.java` (CRLF),
`Household.java` (LF), `Game.java` (LF), `CarCheck.java` (LF),
`LongPlaytest.java` (LF). **56/56 on the work tree, 8/8 seeds with no findings.
SAVE_FORMAT stays at 27.**

Jerus: *"what happens as certain households go in the red? ... if they are doing
bad they cut back expenses, sell their cars or go for cheaper groceries."*

**TWO OF THE THREE WERE ALREADY IN THE GAME.** Cutting back is the plan rule
(`want = subsistence + .8 x surplus`, capped by what they can spend). Cheaper
groceries is `Consumption` running Engel and Bennett off each cell's income - a
poorer cell already buys more grain and less meat. **The car was in nothing.** A
family sold its shares, brought its foreign paper home, drew its last dollar of
credit and went hungry with five months of take-home parked in the drive.

**A REAL SECOND-HAND MARKET AT HOME** (Jerus's call over selling them abroad, and
the harder option): the fleet does not shrink, it **changes hands**, from a family
that cannot keep it to one that could never have afforded new. The price is struck
the way `GoodsMarket` strikes one - floor `USED_CAR_FLOOR` 15%, ceiling
`USED_CAR_CEILING` 70%, position from demand against supply - **so a city cannot
sell its way out of a general crash**: everybody offers, nobody buys, the price is
scrap. Nobody had to write that down and the harness asserts it (8,000 offered, 0
sold, pinned at the floor). Clears in `motoring()` before the new-car pass; **not
one line of the settle waterfall had to change.**

**THE ONE-MONTH TRIGGER WAS WRONG AND THE MEASUREMENT SAID SO.** Over 1,200
months a car-owning cell was short of the shop in 520 and a discharge fired in
513, while a one-month test found 40 - households going bankrupt with the car
still in the drive, thirteen to one. The trigger is now half a year of the gap
against everything saved and everything borrowable (`CAR_SALE_HORIZON_MONTHS`).

### ...AND THE FINDING, WHICH MATTERS MORE THAN THE FEATURE

Over **4,002 months**: a car-owning cell was short of the shop in **3,896** of
them, and cell-months poor enough to need to sell: **2**, both `UNEMPLOYED:OFF_EI`
holding 0.6 and 1.3 households and **0.0005 of a car** between them.

**THE HOUSEHOLDS ARE TOO RICH TO EVER SELL ANYTHING.** That run ends with them
holding **$857bn at home and US$4.59tn abroad** against a city producing **$765M a
month** - on the order of five thousand months of the city's entire output. This
is the same thing that made car finance nearly invisible, and it will quietly make
**anything** about household hardship inert: eviction, the credit cycle, trading
down, the discharge. **`discharge()` fires at all only because it tests the WAGE
against subsistence and never looks at savings** - a bug in the opposite
direction, and why a city with no poor people in it still produces bankruptcies.

**"Why do households accumulate five thousand months of GDP" is the question I
would take next.** It is upstream of a great deal.

**SEVEN OF EIGHT SEEDS ARE BIT-IDENTICAL.** Seed 6 - the one city of the eight
that gets into real trouble - runs the market: 30,178 offered, 6,630 sold, **22%
found a buyer**, 70% of new at best (the ceiling, exactly) and **18% at worst**.
Ensemble: pop 158,252, GDP $679.0M, roads 79%, cars 58%, ride 35% - every point of
the difference from the batch before is that one seed.

**STILL OWED ON THIS REQUEST.** The **basket on `afterFixed`** rather than gross
take-home (two lines, moves every city, wants its own ensemble). **Running costs** -
where the money LANDS is a design decision: rent reaches Real Estate through a
Good and a `bookSale`, so a motoring bill needs a new `MOTORING` good sold by
Automotive, or fuel imported from the world.

---


### AND THEY BORROW FOR IT — 2026-09-17, BUILT AND VERIFIED, NOT DEPLOYED, see `and-they-borrow-for-it.md`

**FIVE FILES STAGED AND WAITING FOR A MACHINE.** The PC went offline before this
was finished, so it follows the pattern transport steps 2 and 3 used: **56/56 on
the work tree**, **8 seeds run**, nothing on the PC. `HouseholdBalance.java`
(CRLF), `Household.java` (LF), `Game.java` (LF), `CarCheck.java` (LF),
`EducationCheck.java` (CRLF). **SAVE_FORMAT stays at 27.**

Jerus: *"make it so that citizens the car requirements are lowered and instead
they finance with the bank."*

**THE OLD RULE WANTED THE WHOLE PRICE IN SPARE SAVINGS AND REFUSED ANYONE WHO
OWED A DOLLAR.** `c.debt > 0` was in the gate, copied from the share offer, where
it belongs. A car is not a speculation.

**THE FIRST DRAFT — a fifth down, four fifths borrowed — MADE IT WORSE, and the
ensemble caught it.** These households are cash-rich and income-poor: 160 months
of take-home in savings against a six-month credit line. Making the loan
mandatory moved the constraint onto the thing they have not, and ownership at
month 266 fell 42% → 11%. **The rule shipped is "pay what you have and borrow the
difference"**, which with no credit line reduces exactly to the old cash rule and
so can never refuse a purchase the old rule allowed. `CAR_DEPOSIT = .20` is a
minimum, not an instalment; `CAR_CREDIT_SHARE = .5` was found by
`DenominationCheck` going red on nine assertions, because every financed
household was parked within a whisker of `BANKRUPT_AT_MONTHS`.

**SEVEN OF EIGHT SEEDS ARE BIT-IDENTICAL OVER 4,000 MONTHS.** A mature city's
households are never short of the cash, so a rule about being short of the cash
does nothing to them. **The young city diverges in month EIGHT and has bought
twice the cars by month ten** — which is where a lowered requirement can show up
at all. Ensemble: pop 163,069, GDP $662.7M, roads 75%, cars 59%, ride 36%, all
within 2% of the pre-finance run.

**THE LOAN IS A BRIDGE, NOT A NOTE.** The grocery waterfall repays it out of
spare cash within a month or two. Financing buys the right to buy *this* month
rather than waiting for the cushion to rebuild. **A car loan with a term is a
different feature, and a real one.**

**A HARNESS THRESHOLD MOVED, AND THE REASON IS WRITTEN INTO THE FILE.**
`EducationCheck` section 11 asked for the unskilled band to be over 25% of the
workforce and got 24.2%. It was right to fire and wrong about why: **nobody
arrives unskilled, so every immigrant dilutes the SHARE without touching the
COUNT**, and the unchanged code walks that same city through 0.0% at month 12,
14.7% at 150, 22.6% at 200 and 34.7% at 300 — it crosses .25 around month 220.
The teeth moved to the **headcount against the schooled city's** (3,413 against
47; 116 to one on the unchanged code), which growth cannot dilute. The share
stays at `.15` as a floor on "not a rounding error". **The unchanged code passes
the new form comfortably**, which is the test of whether that was done honestly.

**THREE FILES TO DELETE FROM THE PC**, left from the rendering trick:
`ShotProbe.java`, `ShotLauncher.java`, and `Render the Infrastructure tab.bat`
(the .bat is worth keeping if the render loop is ever wanted again).

---


### 0.6.5 — THE INSTRUMENT PANEL — 2026-09-17, SHIPPED, see `the-instrument-panel.md`

**DEPLOYED AND VERIFIED.** Five files byte-for-byte on the PC, **56/56 on the
mirror afterwards**. A twelfth rail tab, four pages behind it, and the first
control the transport model has ever had. **VERSION is 0.6.5; SAVE_FORMAT stayed
at 27.**

**NINE BATCHES OF TRANSPORT SHIPPED BEFORE THIS SCREEN EXISTED.** The road
carried three streams and showed one number. The **fare was a real dial with a
real elasticity and NOTHING IN THE GAME COULD TURN IT.** The railway repriced
itself every month against a rule about its own sunk capital and told nobody. A
mill running at 65% because it had not taken delivery of its lorries looked, on
every screen, exactly like a mill running at 65% for no reason.

**FOUR PAGES, EACH ANSWERING A QUESTION SOMEBODY ACTUALLY ASKS.** *Roads* — why
is my throughput 73%: the free-flow curve as a banded meter, then the three
streams as a table (trips, share, what one trip costs the street after highways
and rail, what gets through), then the cars. *Transit* — the three ceilings and
the lowest wins, the two things that walk it down, the books, and the dial.
*The railway* — the sub-tab asked for: track against the sets that track needs,
what it hauls and off which stream, what the rule allows against what it billed,
and a box that tells an over-built railway so. *Freight* — the band good by good
with what is left of the wedge after rail, the month's bill split into the half
billed at home and the half that leaves inside the band, and **every sector's
fleet**, which is the first time the fifth throttle has been visible anywhere.

**THE FARE IS THE ONE CONTROL,** staged like every other policy dial, with a
preview that shows riders, fares, net cost **and the trips that go back onto the
road** — and says plainly what it cannot preview: the second round, where a worse
road puts some of them back on the tram. Nothing on the screen builds anything; a
second place to buy a building is a second place to maintain.

**CARS SHOW IN BOTH PLACES** (Jerus's call): the count and the month's purchases
on Population beside the household's other assets, and what they do to the
commute on Roads beside the congestion number they explain.

---

**THE THING THAT COULD NOT BE DONE, AND WHAT REPLACED IT.** This screen was
written and shipped **without anyone looking at it**. JavaFX needs its platform
natives; the only ones here are the Windows build; this machine is Linux; Maven
Central is not reachable from it. Computer control of the PC was available and
did not help — **a Java window cannot be put in the screenshot allowlist**, so
every screenshot of the running game would have been a masked grey rectangle.

The compiler proves the screen assembles; nothing proved the numbers on it were
numbers, and a NaN reaches a Label as `"NaN%"` while an infinity reaches a meter
as a bar of unbounded width. So **`InfrastructureCheck` gained a ninth section**
that walks every accessor those four pages call, in the four city states that
break screens — empty, congested, motorised, railed — and asks the only two
questions a renderer cannot survive the wrong answer to: is it finite, and is it
inside the range the screen's own label claims. Plus the fare preview at three
fares, the band grid's divisions, and every sector's fleet reading. Both grids
got an explicit empty-state note rather than a bare header.

**It is not a substitute for looking at it.** The layout pass is owed. Likely
snags: a twelfth tab crowding the rail on a short window, and the five-column
tables running wider than the statement column.

**STILL TO COME:** look at it, then the layout pass. Then a **concentration limit
on the bank** (5d found a sector borrowing 38% of the whole book to buy a plant
and a fleet, and defaulting), bounded world demand, and size-level slack in the
housing retirement rule.

---

### VEHICLES — STEP 5D, THE VANS — 2026-09-17, SHIPPED, see `the-sixth-link.md`

**DEPLOYED AND VERIFIED.** Eight files byte-for-byte on the PC, **56/56 on the
mirror afterwards**, **8/8 ensemble seeds with no finding at all**. `VanCheck` is
the fifty-sixth harness. Version target 0.6.4.

**THE FIFTH THROTTLE, AND THE FIRST ONE A BUSINESS BUYS.** Energy, water, road
and health all ARRIVE - the city builds the plants, lays the streets, staffs the
clinics, and a sector takes what it is given. A lorry is the sector's own
capital: it decides how many it needs, pays for them, replaces them, and if it
cannot get them its output falls. `rate = fill x energy x water x road x health x
vans`. And it is what finally gives a Commercial Vehicle Plant a customer - vans
are deliberately not exportable, so until today that plant had no market at all.

**SIZED FROM A MEASUREMENT:** a 106,612-person city moves **1,169,640 tonnes a
month** at nameplate (Manufacturing 972,141, Heavy Industry 148,680, Automotive
35,532, Retail 5,312). At `TONNES_PER_VAN = 120` that is a fleet of about ten
thousand - **one commercial vehicle per eleven people** - and eighty a month of
replacement for ever. Tonnage is BOTH SIDES OF THE DOOR (a mill that buys 100 t
of ore and ships 80 of steel runs 180 t of lorry movements) and read at
**nameplate, not the operating rate**, which would be a circle.

**BUILT ONCE WITHOUT TEETH, AND THE MEASUREMENT SAID SO.** The first version was
the fleet, the wear and the ratio. **Every ratio in the measured city read
100.0%** - vans are importable and `Markets.clear()` fills any shortfall from the
world, so a sector asking for a whole fleet got one, in one month, every time.
That is a capital COST and it is not *"a sector with too few vans can't move what
it makes"*. What was missing is the obvious thing: **you cannot put a thousand
lorries on the road in a month.** So `FLEET_DELIVERY_MONTHS = 8` (an eighth of
the fleet a month - growth is what this touches, not the standing bill) and
`MIN_VAN_RATE = .6` (a firm short of lorries hires haulage; slower, not stopped -
the road's own floor, and higher than its 0.35 for a reason). Re-measured: **132
sector-months short, worst 65%**, every growing sector hitting it, none stopped.

**AND EVERY CITY THAT EXISTS ALREADY OWNS A FLEET.** `vansKnown` is false in a
save from before vans, and the sector is given the fleet its standing plant
implies - it WAS moving steel, so it HAD lorries. Rail's locomotive flag, reused.

---

**THREE DEFECTS, AND TWO HAVE BEEN THERE A LONG TIME.**

**`holdSector` WAS HONOURED BY ONE LOOP AND NOT THE OTHER.** It takes a sector out
of the planner's hands so a harness can own its fixture; the INVESTMENT loop
checked it, the RETIREMENT loop did not - so a harness that held a sector still
had the planner **demolishing** its fixture. RailCheck lays six extra spurs to ask
what an over-built railway can charge, and the distress rule scrapped them:
**618,701k of track before this batch, 422,566k after.** That class's own header
says it about the other loop - *"An assertion whose subject is decided by a
planner is an assertion about the planner."* Held means held, both directions.

**ZERO IS A PRICE.** `GoodsMarket.setLocalPrice()` refused anything not strictly
positive. **VANS and ROLLING_STOCK are the first two goods the world will not
buy, so their floor is zero**, and a good with makers and no takers strikes at
exactly its floor - so a city that saved $0 reloaded with the guard dropping it
and the OPENING price, mid-band, in its place. Seed 7, month 2484, one month,
**$1,200.00 against $0.00** on a $0-$2,400 band. A market absent from the save is
not restored at all, so an absent figure and a saved zero were never the same
thing. **8/8 clean after the fix.**

**BANKCHECK WAS ASKING ABOUT MONTH ONE AND MEASURING YEAR SEVEN,** and the cause
is the keeper: **Materials built its first plant, had to buy a FLEET as well as
the plant, borrowed $57,939 from a bank with $50,292 of equity — thirty-eight per
cent of the whole book in one name — and defaulted in month 70.** Equity went to
−$2,628. Neither half is a bug in the bank: a sector that buys lorries as well as
machines is a riskier borrower, which is the mechanic, and **a lender with no
concentration limit will one day put a third of its book in one name**, which is
a real hole and its own batch. The premium is read six months after the branch
opens now, and both numbers are printed.

---

**WHAT THE WHOLE VEHICLES ARC COSTS**, 8 seeds, 4,000 months each:

| | population | GDP a month | roads |
|---|---|---|---|
| no cars, no vans | 191,194 | $848.3M | 80% |
| cars | 176,228 | $695.7M | 73% |
| cars and vans | **165,914** (−5.9% on cars) | **$673.3M** (−3.2%) | 75% |

About 13% of population and 21% of output against a city where nobody owns
anything to move with — for two industries and three decisions it did not have.

**STILL TO COME — AND THE SCREENS ARE NOW THE THING.** Seven batches of transport
and vehicles and **none of it is on a screen**: no transport page, the fare dial
has no control, the trade screen shows one band with no hint half of it is
haulage, nothing shows how many households own a car, and nothing shows a
sector's fleet or why its rate is 65%. One pass over the whole thing. Then: a
**concentration limit on the bank** (found by accident above), bounded world
demand, and size-level slack in the housing retirement rule.

---

### VEHICLES — STEP 5C, THE CARS — 2026-09-16, SHIPPED, see `the-fifth-link.md`

**DEPLOYED AND VERIFIED.** Fourteen files byte-for-byte on the PC, **55/55 on the
mirror afterwards**, **8/8 ensemble seeds with no finding at all**. `CarCheck` is
the fifty-fifth harness. Version target 0.6.4.

**THE MEASUREMENT DECIDED THE SHAPE BEFORE A LINE WAS WRITTEN.** An unskilled
couple takes home $4,880 a month, **banks $3,160 of it**, and is sitting on
**$799,100 — a hundred and sixty-four months of income.** Every cell in the city
is between a hundred and three hundred months. **$17.6bn of household savings
against zero household debt.** That is not a rich city, it is **a city with
nothing to buy** — rent is rent, the basket is capped at what a person eats, and
everything past those two has had nowhere to go since the game was written. A car
is the first durable this city has ever been able to own. So affordability barely
binds in a mature city and binds hard in a young one, which is the right way
round, and **the constraint is not money, it is time.**

**A CAR IS A HOUSEHOLD STOCK,** beside savings, debt, shares and the dollars
abroad — it follows the people, it is saved per cell by name, a household that
leaves takes it — and the first one that is **not money**, so a currency reform
does not touch it. A city-wide fleet would have answered the road question just
as well and could never have answered *"income decides who can afford one"*.

**THE ROAD IS THE FOURTH MODE AND THE ONLY ONE THAT COSTS.** Highways, transit
and rail are reliefs on an unchanged baseline; a car is the opposite, and it has
to be — the baseline this network always computed was **an unmotorised city,
unlabelled**. Named rather than moved: at zero ownership the factor is exactly 1,
and **the control ensemble is bit-identical to the build before any of this, all
eight seeds, to the person.**

**THE LOOP CLOSES.** *"People drive until the road is full, then take the tram"*
is a car owner riding nothing on a clear morning and three quarters of them
riding at a standstill — motorise → nobody rides → it jams → they ride → it
clears. Read off a **memory** of the commute rather than last month's, or it is a
two-month oscillator.

**AND TRANSIT IS A CEILING ON OWNERSHIP, NOT A BRAKE ON IT** — which is the whole
of whether a player has a move. On the rate it only delays; on the level a
well-served city tops out at **half a car per household for ever**. It works
backwards too: a metro built after the cars does not remove them, it stops them
being replaced, and the fleet decays to the new ceiling over fifteen years.
Nothing was written for that. Worth, over 8 seeds: **pop 151,798 → 176,228, GDP
$556M → $696M.**

**WHAT MOTORISING COSTS A CITY**, 8 seeds against the same build with adoption off:

| | population | GDP a month | roads | cars | riding |
|---|---|---|---|---|---|
| unmotorised | 191,194 | $848.3M | 80% | — | — |
| motorised | **176,228** (−7.8%) | **$695.7M** (−18%) | 73% | 60% | 37% |

The chain is not congestion alone: worse road → every new plant costed at a lower
operating rate → marginal projects stop clearing the interest test → business
borrowing collapses (**$6.6bn lent vs $18.5bn, bank at 45% of capacity vs 77%**)
→ Manufacturing $23.4bn of assets → $6.3bn → exports −26%. **A six-point road
ratio, compounded over three centuries.** `CAR_LOAD_AT_SATURATION = 3.0` is the
one constant here that is a game decision rather than a measured one, and it is
the dial to move if that is not the cost wanted.

---

**THE BIG ONE: THE TRANSIT FARE WAS COLLECTED FROM NOBODY.** Since the day transit
was built, the city has **credited itself the fare and debited no household**.
Money from thin air, in the treasury, for weeks.

**Why nobody saw it: nothing had ever built a bus.** The advisor answered every
jam with tarmac — the road throttle offered three roads and no transit — so four
thousand months a seed, eight seeds a run, **not one city ever carried a
passenger**, and every fixture that needed a road bought a road. The first
ensemble in which the advisor bought a Bus Network failed the money audit on all
eight seeds, and the residual was the fare to six decimals: 2,186 riders at $2.50
is $5.47; the audit said `5.465207`.

> **A harness cannot check a thing nobody does.** `MoneyCheck`'s own header says
> *"every money bug this codebase has had was a flow with one side"* — and the
> twenty-ninth harness could not see this one because its city had no buses. It
> builds three now.

The fare is a **third fee** on the household statement, beside the health fee and
the tuition, and on the residents' screen. Fixing it immediately found its
sibling: **`setTransit` was missing from the load path** — the *fifth* sighting of
that shape after mining's wages, the property-tax charge, the health ratio and the
health service's books. Invisible until the fare became real money. `LongPlaytest`
caught it at **$12.85 on $10,388** of next-month income, 8 months of 3,650. The
fare *share* went with it: `ridershipAt()` is struck from the dial monthly and is
not saved, so a reloaded city put every rider back on a free tram.

---

**THE TWENTY-FOURTH MONEY CONSTANT, WEARING A NEW COAT.** The affordability test
is a **money ratio** that comes out as a **physical quantity** the road, the
market and the trade balance all read. `(a/100)/(b/100)` is not `a/b`, so a
reformed city wanted an ulp more of a car, bought it somewhere else, and
**`DenominationCheck` went red on sixteen assertions** one month after the
reform. The other twenty-three are absolute amounts a reform walks past a
threshold; this one is scale-*invariant* in arithmetic and not in floating point.
**A quantity crossing from the money world into the physical one has to cross at
a grain coarser than the dust** — so a cell buys **whole cars**, which is the same
construction `Retail` reaches for when it floors a basket count.

**`MININGCHECK`'S FIXTURE WAS ALREADY SHORT OF ROAD.** Its comment promises *"all
three ratios sit at 1, and what is left in the number is the price of ore"*; the
city motorises to 18% and the effective load goes 3,610 → 4,814 against a 4,000
capacity. Five roads now — **and the fix revealed something**: the control city
reproduces the pre-car figures to the cent ($245.23k on $983k), so the premise is
restored rather than bent, but the **mining** city comes back at $981k where it
read $873k. A mine is thousands of tonnes of bulk, so that city was over its
free-flow point **before any of this** — the harness has been measuring a
congested mill against an uncongested one in the very comparison it exists to
make.

**THE ADVISOR LEARNED TO BUY A BUS,** and what decides between six candidates is
the catalogue rather than an ordering typed by hand: each is costed in trips
taken off the road per dollar and offered best-first. `Gravel Road 900/$2,326k =
0.387` against `Bus Network 2,500/$12,000k = 0.208 × the car factor` — **so a bus
loses outright in a city where nobody drives and wins once about half the
households own a car.** Nobody chose that crossover; it falls out of two prices
set months apart for other reasons. The checkpoint line carries `cars` and `ride`
now.

**STILL TO COME:** 5d, the vans — businesses buying them and a **fifth operating
ratio** beside energy, water, road and health, on the pantry-fleet pattern the
locomotives already use. Then the screens (six batches of transport and vehicles,
none of it on one), bounded world demand, and size-level slack in the housing
retirement rule.

---

### VEHICLES — STEPS 5A AND 5B — 2026-09-16, SHIPPED, see `the-fourth-link.md`

**DEPLOYED AND VERIFIED.** Fifteen files byte-for-byte, **54/54 on the mirror**,
and for the first time in this project **8/8 ensemble seeds with NO FINDING AT
ALL.** The thirteenth sector is `Automotive`; the railway buys its own locomotives.

**THE FOURTH LINK.** Ore, steel, fabrication — and then both leave, because
*"nothing in the city buys a beam or a machine."* An assembly plant is the first
thing that does. That is the point of this sector, more than the cars.

**CARS EXPORT; VANS AND LOCOMOTIVES DO NOT** — from three measurements of one
failure: **19 Locomotive Works** (9,880 posts in a city with 25,102 jobs), **240
Commercial Vehicle Plants** (36,000 vans a month), and the one that was already
there, **179 Fabrication Works and ZERO Machine Works**. An unbounded export
market at a fixed floor means the best export is built for ever and nothing else
ever is. **Bounded world demand is the general fix and is still its own batch.**

**MACHINERY GOT THE CEILING ITS OWN HEADER PREDICTED** a month ago. 14.3 a tonne,
and the freight did not have to move — 1.98 was already three quarters of that
half-wedge. Fabricated steel deliberately did not: the bulky input is the one a
city must make itself.

**TRACK IS HALF A RAILWAY.** `railCapacity = min(track, fleet x 2,500 t)`, so
**track with no locomotives carries nothing**. A spur is twenty wagon sets, $48m
on a $140m spur; a mature city is 420 sets and **1.75 sets a month of replacement
for ever**, which is half a Locomotive Works running. Rolling stock is a PANTRY,
not an input — that one line is what makes a bought set accumulate into a fleet
instead of vanishing into a month's production. A save from before trains **has**
trains: it was hauling, so it had locomotives.

**THE DEFECT THAT COST THREE HUNDRED MONTHS.** The first supply gate asked
HeavyIndustry's question — *is there spare local supply* — which only works for
ore, because the mills are the only thing that buys ore. `EducationCheck`'s
fixture sank a **$172m Assembly Plant in month SEVENTEEN** against fabrication
already spoken for, and paid $1,745 a month of interest on zero revenue until it
was written off. **The harness that caught it was measuring tuition.** Three
explanations were wrong first, and **I wrote one of them into the code and
loosened the assertion to fit it** — both reverted. *When a harness goes red, the
harness is usually right.* The rule is proportion now: one plant may draw a
quarter of what the city fabricates.

**AND THE DUST IS GONE — FOUR CLAMPS, AND IT WAS NEVER RAIL'S.** A household cell
carried `debt -4.61e-19` for THREE THOUSAND MONTHS on seed 2. The magnitude is not
money; **the sign is read** — `investAbroad()` asks `c.debt <= 0`, and
`moveStock`'s header already records the 158-month divergence one of these cost.
Four writes, four clamps, in the order found:

1. **`moveStock`'s write** — a pool a hair negative divides into every cell that
   draws on it. 35,199 flagged months → 157.
2. **The repayment and the restore** — the invariant at the boundary rather than
   at whichever arithmetic was suspected.
3. **`fundShortfall`** — `borrowed = min(still, room)` where `still` is the tail
   of a subtraction waterfall, so `debt += borrowed` **created** it. This is the
   one that made it none.
4. **`Exchange.sellForHousehold`** — `(x*h)/h is not x` for every double, so a
   cell selling its whole holding ends a ulp short of nothing.

Every one is a POSITION — savings, debt, shares, dollars abroad, a student loan —
and none can be less than nothing. Seeds 1, 3 and 5 had carried this since long
before today.

**A LATENT NaN**, same batch: `abroad * max(0, exportPrice())` where `abroad` was
zero *because* the price was NaN. Zero times NaN is NaN, so every Locomotive Works
was valued at NaN and read as "never worth building".

**WHAT IT IS WORTH**, 8 seeds against the rail-only build: GDP **$711.2M ->
$758.8M a month (+6.7%, up in 6 of 8)**, population +1.4%. Seed 0's mature city
runs **84 Assembly Plants** on 23% of its fabrication, **600,000 t of track, 240
wagon sets**, and one Locomotive Works. Rail holds at 0.76% a month with the
fleet in its rate base.

**CALIBRATED ON THE RIGHT ANCHOR, second time.** Heavy manufacturing here is $23k
of revenue a head at a Fabrication Works and $46k at a mini-mill, not the $17k the
food plants run at. The first draft used the food anchor and the plants never
cleared the interest test.

**STILL TO COME:** 5c — households buy cars and businesses buy vans: the ownership
multiplier on commuter road load (*"if everyone has cars then road demand is
enormous"*), congestion feeding transit adoption, and vans as a fifth operating
ratio. Then the screens, bounded world demand, and size-level slack in the housing
retirement rule.

---

### TRANSPORT — STEP 4, THE RAILWAY — 2026-09-16, SHIPPED, see `the-railway.md`

**DEPLOYED AND VERIFIED.** Twenty-six files byte-for-byte on the PC, **54/54 on
the mirror afterwards**, **8/8 ensemble seeds with no finding the pre-rail build
did not already have**. The twelfth sector is `Rail`. Version target 0.6.4.

**THE BAND KEEPS ONLY WHAT THE LORRIES CARRY.** The railway bills the shipper at
home, like any other supplier of a service, so the money never leaves the city
and the VAT falls out for free. The alternative — pay the railway out of the
band — has a payment the books record as going abroad in fact going to a company
in the city, and that is either a hole in the money audit or a special case in
the trade settlement.

**WHAT A RAILWAY IS WORTH, over eight seeds and four thousand months each:**

| | no railway | with | |
|---|---|---|---|
| population | 113,178 | **178,975** | **+58%** |
| GDP a month | $416.8M | **$711.2M** | **+71%** |

On the default seed steel's **net** wedge goes from 51.6% of the world price to
**25.9%**. And it is a healthy business in all eight: zero write-downs, zero loss
months, the best credit rate in the game, and in seven of eight no debt at all.

**A SHOP'S PRICING RULE CANNOT PRICE A UTILITY,** and the measurement is what
said so. Written first as Retail's shelf — cost plus a quarter — it quoted 32% of
the lorry rate, earned **$4.5m a month on ten billion of track**, and was written
down six times. A railway's running cost is a fraction of its capital, so a
markup on running costs allows a return of nothing on the capital. It prices like
a regulated network now: running costs plus **1.2% a month on the track**.
Settles at 38–43%. The link that falls out of it is real and welcome — **dear
money is what keeps a city's trade band wide.**

**THE BAND ALONE WAS LYING TO THE SECTORS.** At a 92% quote on 92% of the traffic
it said steel's wedge had fallen from 52% to 11% when the shipper had saved
**eight per cent** — the railway's invoice was not in it. `netExportPrice()` and
`netImportPrice()` exist now: every place that asks *is this worth exporting*
reads those, every place that **settles** a trade reads the band, and with no
railway the two are the same number to the bit.

**THREE DEFECTS, TWO OF THEM NOT MINE:**

- **A distressed landlord can demolish a city out from under it.** Real Estate
  sold **5,800 of a city's 13,272 homes in seven months** — beds 40,650 → 22,858
  under a population that did not move — and left 1,137 households with nowhere
  for 140 months. `mayRetire()` asks whether the SEGMENT has spare doors, and a
  segment's slack is spread over four **sizes**, so it was selling the ones
  people lived in. Guarded (nobody sells a door while FamilyModel says somebody
  has nowhere); **a proper fix is size-level slack and it is its own batch.**
- **The per-sector road exposure was only live after a reload** — mine, from step
  3. The split streams did nothing in a running game. Fixed, and made exact:
  only transit can make two businesses face different ratios, so with nobody
  riding the blend hands the plain ratio back rather than recomputing it. That
  guard was missing for one build and **BankCheck found it in twenty minutes** —
  an eighth-decimal difference opened a bank branch on the wrong side of a
  strain threshold and the bank failed. *An ulp here is a different city.*
- **The playtest advisor could not see a door shortage.** `population - beds` is
  beds; a city can have a bed for everybody and nowhere for a family to live, and
  did: 46,856 beds, 14,348 doors, 14,621 households, `homesShort` minus four
  thousand. It reads `getStillUnplaced()` as well now — and **not** the
  doubled-up, which is the second valve working, not failing.

**THE OIL COST EXISTS BEFORE THE OIL DOES.** `WORLD_FUEL_PER_TONNE` in the
world's money (so a reform cannot forget it), charged as a **real import** — the
money audit debits it, the national accounts count it, the trade balance moves.
The day `OIL` is a good it becomes `uses(Good.OIL)` and nothing else changes.

**RAIL IS PRIVATE, LAND-HUNGRY AND DOES NOT ALWAYS PAY.** Three rungs, 50k /
200k / 500k tonnes a month, $140m / $520m / $1.24bn, a terminal nearly three
times a Coal Power Plant on the ground. It will not lay a line under 60% used
(Materials' lesson: without that it laid a spur in a town of 400 houses). It
relieves 75% of the bulk it carries off the road, never 100% — the last mile off
a siding is a lorry — and `loadOf()` reads a spur as **94% trucks**.

**STILL TO COME:** step 5, vehicles (cars, vans, trucks and trains as goods;
households, sectors and the railway buying them; transit adoption becoming
endogenous). **Nothing about the transport model is on a screen yet** beyond the
build cards and the sector's own page; that should be one pass over the whole
thing after step 5, not four partial ones.

---


### TRANSPORT — STEP 3, THE MODES AND THE FARE — 2026-09-16, see `the-modes-and-the-fare.md`

**BUILT AND VERIFIED, NOT DEPLOYED — the PC is offline.** Fourteen files staged
at `/mnt/user-data/outputs/transport-steps-1-3` (steps 1-3 together; step 1 is
already on the PC and will re-write identically). **53/53, 8/8 ensemble seeds
byte-identical to the build before any transport work.**

**THE LAND LADDER WAS ALREADY IN THE CATALOGUE.** Per unit of capacity: Gravel
500 sq ft, Paved 208, Highway **42**. Twelve times the capacity per acre for two
and a half times the money — the trade was already there between two roads.
Transit is the same ladder two rungs on: Bus 8, Light Rail 6, **Metro 1**. Five
hundred times a gravel road per acre for four times the price a journey.

**EVERY MODE IS A RELIEF ON AN UNCHANGED BASELINE**, which is the design and not
an implementation detail — a city with no highway and no transit computes exactly
what it computed yesterday. The alternative (penalise ore on streets, call a
highway the absence of it) is the same arithmetic and raises every existing
city's road demand by half overnight. It is also why the ensemble is 8/8
identical: the advisor only builds gravel roads.

**THE ROAD CAN BE HALVED AND NEVER DELETED**, three separate ways: at most 65% of
commuters ever ride; transit is capped against the road under it (*a city that
builds a metro and no streets gets a metro nobody can reach*); and freight never
rides at all. Measured: 5,590 riders on the ceiling, 4,800 with fewer streets,
800 with none, and with the best of everything **the road still carries 39%**.

**TWO BUSINESSES ON THE SAME ROAD:** office 74%, mill 47%, road 42%. The mill
beats the road by exactly as much of its own traffic as rides the tram.

**A FARE IS A PRICE, NOT A CHARGE.** Free 5,590 riders; at the $2.50 default
5,311; at the ceiling nobody. And the two lines move against each other — **six
times the fare takes $58,695 a month instead of $13,276 and puts 1,398 more cars
on the road.** Profitable "by alot" is available, at a ridership the city may not
want.

**THREE DEFECTS FOUND, TWO OF THEM MINE:**

- **The transit staff worked for free.** Nothing in INFRASTRUCTURE had ever had a
  job, so no line was ever written to pay one. A Metro Line is 828 posts.
- **The fare is money, so a reform moves it** — 23rd of that family, and **the
  first caught in the same edit that created the field.** The ridership curve is
  written against the cap, not against dollars, so a reform moves both.
- **The policy array.** Adding the fare wrote to `state[12]` of a twelve-long
  array and took 43 harnesses down in 19 seconds. Good outcome.

**AND THE HARNESS CAUGHT A REAL DESIGN ERROR.** I gave a sector with no buildings
a road ratio of 1 — "nothing standing is not being held up". True of its output,
**false of its planner**: `BusinessInvestment` costs a first plant at the
operating rate, and the congested city started building plants a 38% road said it
could not afford. Two cities meant to differ only in roads went 1,151 → 1,770
people and the congested one's shelves went from 31% full to 100%. Empty sectors
read the city's own blend now.

**MY OWN HABIT, WRITTEN DOWN:** twice this sitting I edited a core file, rebuilt
only the UI tree, and ran a harness out of `build/classes` — testing yesterday's
code, and once it read as a real failure. Build BOTH trees before running
anything.

**STILL TO COME:** step 4, rail as a private sector pricing haulage
cost-plus-scarcity on Retail's `repriceShelf()` shape and billing trade freight —
everything so far exists to make that possible. Then step 5, vehicles.
**Nothing is on a screen yet**; that should be one pass over the whole transport
model after step 4, not three partial ones.

---

### TRANSPORT — STEPS 1 AND 2 — 2026-09-16, see `the-freight-band-and-the-three-loads.md`

**Step 1 SHIPPED and deployed. Step 2 BUILT AND VERIFIED, NOT DEPLOYED — the PC
went offline mid-batch.** Six files staged at `/mnt/user-data/outputs/traffic-split`,
drift-checked and ready; `Good`, `Traffic` (new), `BuildingsTemplate`,
`InfrastructureManager`, `ServicesManager`, `TradeCostCheck`. **53/53, and 8/8
ensemble seeds byte-identical to the build before any of it.**

**THE WEDGE IS ARITHMETIC NOW.** Every traded good carries a third number — the
cost of moving one unit — and the two ends split into four: the world's own ask
plus freight is the import price, the world's own bid less freight is the export
price. Three quarters freight, one quarter the world's margin.

**THE PRICE TABLE WAS ALREADY A FREIGHT MODEL AND NOBODY DESIGNED IT THAT WAY.**
Freight as a share of world price: **fifteen of seventeen goods sit at 21-25%**,
and the two that don't are IRON at 49% and MATERIALS at 43% — the two lowest
value-density things in the game, where real freight genuinely is half the
delivered cost.

**STORED THE OTHER WAY ROUND THAN IT READS,** and that was not a preference:

```
CROPS  derived export 0.27999999999999997  vs literal 0.28   equal: False
STEEL  derived export 0.8470000000000002   vs literal 0.847  equal: False
```

One ulp, and an ulp in this codebase is never nothing. So the delivered prices
are the stored literals and the world band is derived. All 58 call sites
unchanged.

**ONE ROAD LOAD IS THREE.** `Traffic { COMMUTERS, GOODS, BULK }`, and the shares
are **fitted to the catalogue, not guessed**:

```
roadLoad = 0.726 x jobs + 0.878 x homes + 0.0386 x tonnes/mo     R² = 0.74
```

which implies **one worker per nineteen tonnes a month — a truckload.** Nobody
set out to encode that. The residuals are all explainable and all one way: Iron
Mine +280 (heavy vehicles), Penitentiary −288 and Long-Term Care −136 (occupants
don't commute), University +258 and Medical School +177 (students travel, the
fit counts only wages). **None of it matters** — the fit gives the SHARE, the
stored roadLoad gives the MAGNITUDE.

```
Steel Mini-Mill   17% / 0% / 83%       Grain Farm    14% / 0% / 86%
Iron Mine         32% / 0% / 68%       Bakery        53% / 16% / 31%
University       100% / 0% /  0%       a played city 86% / 3% / 11%
```

**THE ENSEMBLE FOUND A REAL DEFECT AND THE FIX IS THE BETTER DESIGN.** Summing
the three streams to get the network total came back subtly different over eight
seeds — `$3,005,171,009k` vs `$3,005,171,008k`, `pop 48028` vs `48029`. Every
building's three loads add to its own roadLoad *to the bit*; what does not
survive is adding them up in a different ORDER, and 4,002 months amplifies it
into a different city. **The total keeps its original sweep and the breakdown is
read alongside it.** Re-run: 8/8 identical — which settles the cause empirically
rather than by assertion.

**Still to come:** step 3 the modes (road / highway / transit, all public, road
irreducible, land as the real cost — this is where behaviour changes); step 4
rail as a private sector pricing haulage cost-plus-scarcity on Retail's
`repriceShelf()` shape and billing trade freight; step 5 vehicles. Decisions
already taken are in `transport-and-the-freight-band.md`: a city with no rail
still trades (just dearer, on more road), freight can never reach zero because
the sector needs a margin, transit is public with an adjustable fare that can be
made profitable, no oil good yet but the cost hook gets wired in, no port.

---

### THE STATEMENT OPENS — 2026-09-16, SHIPPED, see `the-statement-opens.md`

**Jerus: *"can you make it so in the UI when you click on revenue or cogs, it
expands and shows the individual items"*.** Eight files, **52/52**, deployed and
verified byte-for-byte, **no save-format change**. *Not yet looked at on screen —
class was in session; the layout pass is still owed.*

Four lines open: **Revenue** (by good, split city/abroad), the **cost of sales**
(by good, split city/imported), **Wages** (by pay tier, with posts), and **Sales
tax remitted** (charged / border / credited).

```
Industry
  Revenue                     $1,003,616   > what
      Bakery goods              $523,586
          in the city           $285,072
          shipped abroad        $238,514
      Bread                     $480,030
          in the city           $100,660
          shipped abroad        $379,369
      | 62% of this month went abroad, where the price is the world's floor.
```

The city's own bakeries were **shipping 62% of what they baked** while Retail
bought imported meat, fruit and fish by the half-million a month. Neither was
visible before; both are one click away now.

**UNDERNEATH IT IS A NEW `Sector.Split`** — `{atHome, abroad}` per good, on the
ledger and on the struck statement, booked from the trade rather than
reconstructed from units × price, because **a local sale and an export clear at
different prices in the same month** and the price is restruck before anybody
draws a screen. Carried in `SectorState` by name; a new field in an old save
reads zero, so **`SAVE_FORMAT` stayed at 27** and a ten-sector save with no
splits at all loads to the cent.

**THE TWENTY-THIRD MONEY CONSTANT, CAUGHT BEFORE IT EXISTED.** The two `scale()`
calls that divide the splits in a currency reform were written with them, then
**deleted to see what would notice: 52/52, all green.** Nothing in the game
*reads* these maps — only the screen does — so a reform would have shipped a
statement whose parts were a hundred times its total, findable only by
reforming a city and clicking. `DenominationCheck` now divides all 32 of them
against the unreformed twin, and says `Retail: MEAT imported divided FAIL
1,020.91 != 10.21` when they are dropped. First of that family caught before it
shipped rather than after.

**TWO MORE GUARDS, BOTH BROKEN ON PURPOSE FIRST.** `SectorBooksCheck` adds the
breakdown up against the line above it every sector every month, reaching the
home/abroad halves by a different route (per trade, per good) than the totals
the VAT is struck off — 1,320 statements, all balanced. `LongPlaytest` compares
the splits across every save round trip, struck *and* unstruck; with the restore
dropped it reports `...Retail GRAINS imported, 9 time(s), months 388-3650`.

**A DISCLOSURE THAT DISCLOSES NOTHING IS WORSE THAN NONE.** Retail sells
groceries, the landlords sell housing: one line, no split, and the caret would
promise a breakdown and then repeat the figure above it. Those do not open. The
cost side keeps a single-good breakdown when the good came from *both* sides of
the border, because that is the whole question on that line.

**AND THEN JERUS ASKED *"does it show its revenues as well?"* AND THAT FOUND TWO
MORE.** The suppression rule tested `lines.size() == 1` on the GOODS, and the
builders sell no goods at all — their whole revenue is `otherRevenue` — so
**Construction fell through it and drew a caret onto one row reading
$140,781,361, the figure already on the line above.** A rule about how many rows
there are has to count the rows. And that `otherRevenue` was **two businesses
under one label**: work recognised off the order book, and this month's repair
bill to every owner of a standing building. They move for different reasons, so
*a city whose building work has stopped and whose repair income is still growing
is a city that has finished growing* — invisible before, two rows now.
`otherRevenueParts()` replaced `otherRevenueLabel()`, and Real Estate's override
("Rent") went with it: it has never booked a cent of other revenue in its life.

**The check written for that caught a third thing on its first run:**

```
FAIL Construction other revenue named month 3:   715.90, expected   7.90
FAIL Construction other revenue named month 4: 3,011.77, expected 716.90
```

Month 3's parts added up to month 4's total. `recognisedThisMonth` and
`repairsThisMonth` are working fields, zeroed and refilled as the next month
runs, so a screen reading them late describes a month the statement above it is
not about. **Frozen into the Statement at `strike()` now**, like the per-good
money, and saved and scaled with it.

**The cost side stopped suppressing entirely** — an opened cost line always says
WHERE the input came from, and *"Crops, all of it from the city"* is the
difference between a mill with farms behind it and a mill on a ship. Heavy
Industry's Revenue opens on one good for the same reason: 100% of it is shipped.

**STILL OWED — the layout pass.** The data behind every row is checked every
month; the nodes are not, and cannot be here: JavaFX in the cloud container is
the Windows build with no native pipeline (`no suitable pipeline found`), so a
harness here cannot construct a single Label. NaN is covered anyway — the sums
fail on one. What is unverified is whether the caret fits, whether the indent
lines up, and whether a fourteen-row Retail breakdown needs a scrollbar.
**The fix that would close it** is the shape the operations page already took:
put the panels' content behind `List<Sector.Line>` so
`SectorBooksCheck.pageIsReadable()` audits them for free.

**Open, noticed while probing:** a sector with no plant standing still draws
**"Running at 48%" in red** on its operations page. Generic `Sector.operations()`,
all eleven sectors, same family as this morning's `Double.MAX_VALUE`-as-money.

---

### FOOD PROCESSING — 2026-09-16, SHIPPED, see `the-eleventh-sector.md`

**Jerus: *"now a new sector which makes the ready meals, processed meals,
snacks, drinks, and cooking fats?"*** Six files, **52/52** (a new harness),
deployed and verified byte-for-byte, **no save-format change**.

Five goods that were a **third of the shelf by weight** — 16.3kg of the
reference basket's 50 — and were imported to the last gram. Three plants, each
with its own cost story, and each of them decided by a different number:

```
  Meat Works        meat, veg, grains -> processed meats, ready meals   40% meat
  Snack & Oils      crops             -> snacks, cooking fats           19% crops
  Bottling Plant    crops             -> drinks                          4% crops
```

**IT TOOK FOUR CALIBRATION PASSES AND EVERY ONE OF THEM FAILED FOR A DIFFERENT
REASON.** Three of the four were not calibration at all; they were faults in
machinery the sector borrowed.

**1. TWO OF THE THREE PLANTS COULD NEVER BE BUILT BY ANYBODY.**
`BusinessInvestment.planMaker()` sizes a sector by ONE good —
`Sector.planningGood()`, the first stockable thing it makes — and then skips
every template that does not make it. This sector's planning good is processed
meat. The Snack & Oils Plant and the Bottling Plant were not refused and not
scored badly: **invisible**. Measured over four hundred months — one Meat
Works, zero of the other two, while the city imported every crisp and every
bottle it drank.

**2. A FIRST PLANT WAS VALUED AT THE CEILING IT WAS ABOUT TO DESTROY.** A good
nobody here makes stands at the top of its band — `GoodsMarket.strike()` puts
the position at 1 when supply is zero, correctly, because every kilo came off a
ship. Processed meat stood at **$8.90** and ready meals at **$7.90** the month
the first Meat Works was financed; **$7.70 and $7.10** the month it opened. A
third of the margin, gone to the plant's own arrival. The band is closed form,
so the settled price is too: `position = demand / (demand + supply)`, with this
plant's nameplate in the supply. Measured in the harness on a city of 2,935
people: the generic estimate says **$58,621 a month**, the sector's own says
**$37,060**.

**3. THE SALES TAX WAS NOT IN THE ESTIMATE, AND THIS SECTOR PAYS IT ON
REVENUE.** The ledger is a proper VAT — tax on what you sell, credit for what
your *suppliers* remitted. An import carries no such credit:
`SalesTaxLedger.chargeImport()` puts the same figure on both sides of the row
and it nets to nothing, because no foreign seller remitted anything, and that is
the design ("a sector cannot undercut a local supplier by buying from outside").
Every other maker in this game buys at home. **This one buys meat**, and a city
with no herds imports every gram. Measured, month 72: revenue $35k, operating
income **$1k**, sales tax **$5k**. *Five times the profit, and the planner could
not see it at all.*

**4. AND THEN THE ACTUAL CALIBRATION.** The measure that decides whether a maker
survives a city growing up is **revenue per worker**, because the wage is the
one cost that follows the city and the world price is not. The game's own
catalogue, at mid-band:

```
  Bakery              $17.8k a head   payroll 22% of revenue
  Industrial Bakery   $13.5k                  28%
  Steel Foundry       $33.6k                  12%
  Fabrication Shop    $19.6k                  20%
  ---- the first pass of these three ----
  Meat Works          $10.7k                  35%
  Snack & Oils         $6.3k                  59%
  Bottling Plant       $6.0k                  61%
```

A Bottling Plant was six people making thirty tonnes a month — **a tonne a
day**, a craft operation, not a plant. It cleared $12k a month at a founding
city's wages and lost $20k at a grown one's, and the run showed exactly that:
built at month 271, under water by 288, the sector bankrupt with the plant still
standing. **The three are now $17.1k, $16.8k and $18.0k a head, payroll 21-22%**
— nameplates two to four times the first pass against the same order of staff,
which is also what a real line looks like.

**ONE BORROWED RULE WAS DROPPED, AND IT WAS MEASURED BEFORE IT WENT.**
`Sector.MIN_STAFFABLE_TO_ORDER` asks the city for 80% of a building's posts in
people who are not already working. Its own header says what it is for: *"any
building whose posts are a large step against the city that would fill them"* —
a contact centre's three hundred seats. **These plants are five, six and four
posts, the smallest investor-built job steps in the catalogue.** Against a city
at full employment the test read `the city could staff 25% of a plant` — one
spare tradesman against four posts — **unchanged for two hundred and fifty
consecutive months** while the city grew from four hundred people to nineteen
thousand. A rule sized for three hundred posts is not a rule about four.

**WHAT THE SECTOR IS FOR, in one measurement.** At month 98 of a real city, the
same Meat Works is worth **$16,559 a month on meat at a farm's export floor and
$1,678 on meat off a ship**. Ten times over, on one price. *The building did not
change. The farms did.* The Livestock Farm finally has a customer at home, which
is the fourth reason in this game to put two things near each other.

**EIGHT SEEDS EITHER SIDE, 333 years each.** The sector ends every one of the
eight with **$52M-$86M of assets, $4.6M-$8.5M of cash and zero write-downs in
seven of eight**. Median city population is unchanged (109,765 → 110,980),
median GDP is **up 11%** ($375.8M → $417.9M a month), and clean runs went 4/8 →
5/8. On the default seed it finishes at **2.00% credit — the best rate in the
game** — alongside Retail, Construction, Heavy Industry and Agriculture.

**AN ELEVEN-SECTOR BUILD LOADS A TEN-SECTOR SAVE TO THE CENT.** Proven end to
end: a save written by the deployed build, opened by this one, every sector's
cash and revenue and share count identical, Food Processing empty, and sixty
more months played on top of it. Nothing in the save is keyed by a sector's
INDEX — `SectorState` by name, `equityKeys` by name, household cells by name —
so appending to `Sectors.KEYS` was safe and **`SAVE_FORMAT` did not move**.

**`FoodProcessingCheck` is new, and it is the 52nd harness.** It proves the four
things above rather than that the sector makes money, which depends on the city
and is the point of it. Its fixture **runs until the sector is about to build
its first plant** rather than for a fixed number of months — the first draft ran
160 and the investor had already put up three Bottling Plants by then, so a test
about a *first* plant was asking about a fourth.

**THE SECTOR HAS ITS OWN PAGE NOW**, because four accessors were written with it
and left calling nobody. `Sector.operations()` draws a block per good, which is
the sector's *output* and explains nothing about the sector; a player looking at
a Meat Works that loses money could not see from a page about sausages that the
meat price did it. It now ends like Manufacturing's - where the meat comes from,
and whether the input bill, the wages and the tax still leave anything.

**And that page nearly shipped the 23rd money constant.** The sales-tax warning
first fired above a flat twelve percent — in a quantity the player sets, which
has run 12% to 30% inside one game. It is a fraction of the rate now: three
quarters of the full rate on revenue means three quarters of what the sector
buys came off a ship. *A bound that is not a ratio of the thing it bounds is a
bug waiting for a policy change, and it does not have to be in money to be one.*

**A DEPLOY NOTE WORTH KEEPING.** The screen change was committed to a
`stagedPath` already committed once in the same batch. The write reported
success, the mtime moved, **and the content did not** — 21,374 bytes went to the
PC when this side held 28,209. The byte-for-byte check after the copy-back
caught it, which is the whole reason that step exists. **A changed file goes out
under a fresh staged path.**

**Open, measured, not fixed:** cooking fats run at **half** the reference
basket's 0.8kg a head while snacks run at **1.7×** their 1.0kg, so a Snack &
Oils Plant's fats line carries about 3.5× the nameplate the city eats. It costs
nothing today — the plant throttles rather than floods, and no plant was ever
retired over it in 418 months — but the basket and the behaviour disagree and
only one run has been looked at.

---

### THE FIGURE THAT WAS NOT A FIGURE — 2026-09-16, SHIPPED, see `the-figure-that-was-not-a-figure.md`

**Jerus: *"Go take control try it out."* Ninety seconds later the Agriculture
screen was printing `$9,223,372,036,854,775,807` in red.** Two files, **51/51**,
deployed and verified byte-for-byte, no save-format change.

```
CROPS                                  MEAT
  Could make        500 tonnes           Could make        0 kgs
  Made              479 tonnes           Made              0 kgs
  Price             $271.15              Price             $4.55
  Cost to make one  $41.84               Cost to make one  $9,223,372,036,854,775,807
```

**That is `Double.MAX_VALUE` rendered as money.** `Sector.getCostPerUnit()`
returns it to mean *"there is no line to cost"* — which is the right answer for
`BusinessInvestment` comparing projects, and is not a price.
`Sector.operations()` printed the sentinel straight onto the page, three blocks
of it on that one screen: meat, dairy and eggs, vegetables.

**AND IT WAS NOT THE FARMS' BUG.** The guard was written first, then the fix was
reverted to watch it fail: it fires at **month 2 in eight sectors at once** —
Industry, Heavy Industry, Mining, Materials, Business Services ×3,
Manufacturing ×2, Agriculture ×2. `makes` is the SECTOR's list, so every sector
draws a block for every good it declares whether or not the city has built a
plant for it, and in the founding months no city has. **It has been on that page
since the sector template shipped on 2026-09-11.** The farms batch only made it
permanent instead of passing: five goods declared, one plant built, so those
three blocks never go away.

**THE FIX IS TWO SENTENCES OF SCREEN.** A good the sector has no plant for is
not a block on its page — **stock alone still earns one**, because a warehouse
outlives its plant, and that block says **"no plant"** in words where the figure
was. Verified on screen after a rebuild: Agriculture now runs THE PLANT → CROPS
→ the farming block, with nothing in red.

**AND `SectorBooksCheck` READS THE PAGE NOW.** It already played a city and
audited three statements for ten sectors a month; it walks every sector's
`operations()` page as well and refuses a value that is not a figure — a
sentinel, an infinity, a NaN. **9,856 screen lines a run.** Seen to fail before
it was trusted.

**WHAT IT SAYS ABOUT THE SUITE.** Fifty-one harnesses were green. The books
balanced every month for a hundred and twenty months in the very harness this
now lives in. **A conservation law says the money went somewhere; it says
nothing about what the page prints.** Nothing in this project had ever asserted
anything about a screen's TEXT before today.

**AND THE THREE THINGS THAT HAD NEVER BEEN SEEN ON SCREEN, WERE:**

- **The goods page.** Twenty-five rows, three columns, aligned. Crops $235.81/t,
  bread $1.45/kg **exported 219,138**, bakery goods $3.43/kg **exported 94,336**,
  and every other food at the TOP of its band because the city lands them as
  imports. The model saying out loud what the ensemble said.
- **The Farms tab.** Mixed Farm $240k, Grain Farm $1.2M, Greenhouse Complex
  $6.8M, **Livestock Farm** reading materials 184, 1,080 pts, 40 road, 120 kW,
  600 water, $45k/mo to run.
- **The corrected unemployment trace.** The *"The people"* preset draws
  Population, Jobs, Unemployment, Net migration; the chart read **23.5%** beside
  the People panel's **OUT OF WORK 23.5%**. The two finally strike the same
  rate.

**STILL OPEN, FROM THIS BATCH:**

- **THE YEAR-BOOK BUTTON IS STILL UNPRESSED.** It is the last thing on Reports
  and it sat underneath a window all session. One click on a rebuild:
  Reports → *"Send this run to somebody"*, and check the two paths it prints.
- **NOTHING ELSE ASSERTS A SCREEN'S TEXT.** `SectorBooksCheck` covers the ten
  sector pages. The city panel, the People screen, the Policy pages and the
  Reports page are all still only as right as somebody's last look at them.

**Rules earned:** *A sentinel is a value for the code to compare, never a value
to print — and the place it leaks is the screen, which is the one layer this
project had no harness for.* **And, the plainer one: play the game.** Fifty-one
harnesses, eight ensemble seeds and a ten-fixture sweep did not find this;
opening the build and clicking on Agriculture found it in ninety seconds.

### THE SIGN OF A RESIDUE — 2026-09-16, SHIPPED, see `the-sign-of-a-residue.md`

**The farms grow the city's dinner — and the one red harness turned out to have
nothing to do with farms.** Seven files, **51/51**, **ten fixtures a side** on the
reform sweep, **7/8 ensemble seeds clean against the deployed tree's 8/8**,
byte-for-byte verified, no save-format change.

**FOUR HOUSEHOLDS OWED MINUS 2.7e-29 DOLLARS, AND THE SIGN OF THAT DECIDED THE
CITY'S EXCHANGE RATE.** `HouseholdBalance.investAbroad()` asks `c.debt <= 0` to
decide who may keep money abroad. A residue twenty-nine orders of magnitude below
a penny is not money and nothing reads it as money — **one thing reads its SIGN.**
A negative dust is debt-free; a positive dust is indebted. `DenominationCheck` ran
a city and its reformed twin at a noise floor of **1e-13 for 158 months** and then
parted in one month:

```
m158   fin.hhBroughtHome = 1.00e+00     <- one city did it, the other did not
       fx.financialIn    = 8.73e-01
```

Four retired cells flipped, their target abroad went to zero, their holdings came
home at `HOME_SPEED`, the financial account moved by thousands, and with it the
exchange rate — **which is every import and export price in the city**. By the
decade the two were 2–4% apart on population, GDP, rent, the price level and the
bank.

**THE DUST COMES FROM THE MONTHLY REBUILD.** `moveStock()` gives a gaining cell
`weight * rowMoved / rowGain`, and `rowMoved = min(rowLoss, rowGain)` — at most
`weight` on paper, and `weight * g / g` can come back one **ulp above** it, so
`rest = weight - fromRow` goes a hair negative. A negative share hands a cell a
negative slice of the pool, and **every stock comes through that line**: savings,
debt, the shares, the dollars abroad, the student loan.

**THE FIX IS FOUR CLAMPS AND NO NUMBERS.** `Math.min(1, …)` on the ratios,
`Math.max(0, …)` on the remainders. A cell cannot take more than its own weight
from its own row; a pool cannot hand out more than it holds. *A ratio cannot go
stale in a new unit because there is no unit in it.*

**AND IT WAS NEVER THE FARMS' BUG — TEN FIXTURES SAY SO.** The same two-city
comparison run over ten foundings differing only in how many Houses they build:

| houses | deployed tree | farms tree (before fix) | after |
|---|---|---|---|
| 396–399, 401–405 | 1e-15 … 5e-15 | 9e-16 … 2e-14 | unchanged |
| **400** (the harness's own) | 1e-15 | **4.3e-02** | **5e-15** |
| 404 | **5e-08** | 3e-15 | 3e-15 |

**Nine fixtures in ten never saw it.** The farms moved one fixture's trajectory far
enough that its residue landed on the other side of zero at month 158 — and the
**deployed** tree's 404-house fixture is the same bug at 5e-08, inside the band and
a hundred million times the noise floor. Latent since the households were given a
door to the world.

**THE GUARD WAS SEEN TO FAIL BEFORE IT WAS TRUSTED.** `LongPlaytest` flags **"a
household cell carries a negative position"** — savings, debt, abroad, student loan
or any company's shares below zero, **zero tolerance**, every month of every seed.
With the clamps reverted, seed 11 reports it **216 times across months 79–128**;
with them in, *"Nothing. Every month passed the audit."*

**AND A FIFTH UNSEEDED MONEY CONSTANT, HONESTLY LABELLED.**
`investAbroad()` swept a household's move at a fixed **1e-12** — the exact twin of
`OutwardInvestment.MIN_MOVE`, which is seeded, twenty lines away. **It is NOT what
broke the harness**: it was found while chasing it, seeded, and the divergence did
not move by a digit. Fixed anyway, with its comment saying exactly that, because
leaving a known-wrong constant in on the grounds that today's harness cannot see it
is how the other four survived as long as they did.

**THE FARMS THEMSELVES.** Mixed Farm makes CROPS + DAIRY_EGGS + MEAT, the
Greenhouse makes VEGETABLES + FRUIT, and a **Livestock Farm** (id 55) joins them —
**the dearest thing in the catalogue per acre**, 120 acres and $13,000k, revenue
6.5% of build cost against the Grain Farm's 18%. That is what the arithmetic gave,
not a choice: a real dairy operation's capital is the parlour, the herd and its
quota, and the ground is not most of it. Pasture-fed, so no feed line — which also
keeps Agriculture from making CROPS and using CROPS at once and handing the
greenhouse's salad a share of the cows' feed bill through the joint-cost split.
**Over 333 years, eight seeds build exactly one Mixed Farm each and import the
rest** — which is Jerus's own prediction, *"just that it usually isnt"*, coming out
of the band rather than being typed in.

**`AgricultureCheck`'S PREMISES RESTATED IN MONEY, NOT WEAKENED.** It measured
farms in **tonnes of CROPS**, and four farms in two units (crops by the tonne,
dinner by the kilogram) cannot share a tonnage column. Restated at the world's
**export floor** — the same yardstick the sector underwrites on — which says
something a tonnage never did: *what the harvest is worth if nobody here wants it.*
$34,980 / $140,000 / $240,650 a month. The density claim is ground per **dollar**
of harvest now. **No tolerance was widened anywhere in this batch.**

**STILL OPEN, FROM THIS BATCH:**

- **THE DEPLOYED TREE'S 404-HOUSE FIXTURE READ 5e-08** — inside the 1e-6 band, eight
  orders of magnitude above the other nine, same mechanism. It should be 1e-15 now
  and has not been re-measured since the fix.
- **`Household.isGoingShort()` COMPARES AGAINST A FIXED `1e-9`** in local money, and
  it feeds the same `eligible` flag that the dust flipped. It did not fire here and
  it is not fixed. A relative form costs one character.
- **THE FARMS HAVE NOT BEEN SEEN ON SCREEN**, along with the goods page, the year
  book button and the two corrected Reports traces.

**Rules earned:** *A zero-crossing test is a threshold, and a threshold on a residue
is decided by luck.* The project's own lesson from `CapitalFlows.MIN_STOCK` was
**"a threshold in absolute money is worse, because it is discrete — it does not
drift, it flips"**; this is that sentence with the threshold at zero and the
quantity at 1e-29, and **no constant was wrong anywhere.** **And: when a harness
goes red on your change, ask the same question of ten fixtures before believing it
is your change** — one fixture said the farms broke the currency reform; ten said
the model had been carrying a coin-toss for months.

### THE DOCUMENTATION CATCHES UP AGAIN — 2026-09-15, SHIPPED as 0.6.0

**Seven batches had shipped under one version number.** `GameVersion.VERSION` still
said `0.5.15` after the sixth age band and save format 27, the children who follow
a parent out of work, the consumption model, the exchange-rate units fix, the
thirteen foods going live, FOOD's retirement and the cost split. **Bumped to
0.6.0** with the reason written into the constant's own note: the goods economy is
a bigger change than the clock was, and the clock took 0.4.4 to 0.5.0. `APPVER` is
derived from that line by `Build EXE.bat`, so there is nothing to bump by hand.

**README** said save format **26** (it is 27) and **forty-eight harnesses** (it is
fifty plus the playtest, reported as fifty-one), and its opening paragraph
described a food economy that no longer exists. Fixed, with a paragraph on what a
household eats added to the opening, where the model's pillars are listed.

**THE HTML MANUAL IS REPUBLISHED AT 0.6.0 / FORMAT 27 (version 6).** New: the
thirteen foods in the goods table, priced by the kilogram; a **What a household
eats** subsection in §07 covering Engel, Bennett, satiation, the time axis and why
income is measured in multiples of subsistence; Liebig's barrel on the shelf and
the 20%-of-demand units bug; the ovens in the catalogue; save format 27 and why
the thirteen foods needed no bump; `ConsumptionCheck` in the harness table; the
inventory-as-a-volume rule extended to thirteen goods; and three open questions
retired into the answered note (**the laid-off parent**, **one good called Food**,
**a line charged for both its products twice**) with one new one added &mdash;
*is the city too rich against its food?*, recorded as **deferred by Jerus** on the
grounds that health, transport and luxury costs are coming.

**AND THE RULE THAT MADE IT SAFE EARNED ITS KEEP AGAIN.** The manual is assembled
**from the published page, never from local parts** &mdash; and the publish was
*refused* until the live version had been read line by line, which is the guard
working. Reading it found **five stale places the first pass had missed**: the
masthead, the vitals block, two rows of the sector table, the save-format
paragraph and the harness count. A patch written from a survey would have shipped
all five.

**AND A SMALL UI ADDITION THE SAME DAY.** Jerus: *"somewhere somehow, i should be
able to see all the goods, and the current prices and some quick info"*. There was
nowhere &mdash; a good's price appeared only on the screen of whichever sector
happened to make it, and **the thirteen foods had no screen at all, because no
sector makes them**. Twenty-five goods and no index is a model you have to read the
source to see. Reports now ends with **Every good in the city**: one row a good,
what a unit costs here, the world's band around it, and what crossed the border
this month. 51/51 with `UserInterface` compiled, deployed and verified.

**STILL OPEN, AND IT IS JERUS'S TO DO:**

- **THE GOODS PAGE HAS NOT BEEN SEEN ON SCREEN.** Compiled and deployed, never
  looked at &mdash; the same caveat as the year-book button and the two corrected
  Reports traces, which are still waiting on the same look. On a rebuild: Reports,
  scroll past the picker to *Every good in the city*, and check the three columns
  line up at twenty-five rows.
- **THE ARTIFACT'S SHARE PIN STILL POINTS AT VERSION 1.** Anybody holding the
  share link is reading the **0.4.4** manual &mdash; six versions and five months
  of work behind &mdash; whatever is published. The README links the current
  artifact; the share link does not follow it. **Open since 2026-09-14 and not
  fixable from here**: it is a setting on the page's own share menu.

### WHOSE COST IS IT — 2026-09-15, SHIPPED, see `whose-cost-is-it.md`

**A line that makes two things was charged for both of them twice.** Seven files,
**51/51 harnesses**, **8/8 ensemble seeds clean**, byte-for-byte verified, no
save-format change.

**AND IT WAS NEVER ONLY THE BAKERY'S.** The previous batch found it when an oven
drafted making BREAD and BAKERY produced **nothing** - 858,000kg of nameplate
capacity, zero made, the city importing bread beside it - because
`getCostPerUnit()` and `getMarginalCostPerUnit()` divide the whole sector's
payroll, power, water and inputs by ONE good's output. Then the guard written for
the fix printed the real scope:

```
   Industry          makes 2 goods
   Business Services makes 3 goods
   Manufacturing     makes 2 goods
```

Every **building** makes one good, which is what made the bakery look like a new
path. These are **SECTOR** methods. **Business Services has made three goods and
Manufacturing two since the day each shipped, and both have been charging every
one of their goods the whole line's bill for their entire lives.** The bakery was
the first line where that was fatal rather than merely wrong. *The previous
batch's note said "nothing had ever exercised that path"; the building half was
true and the conclusion was not. Corrected in place.*

**THE SPLIT IS BY RELATIVE SALES VALUE** - each good's nameplate revenue over the
line's, which is what an accountant does with a joint product and the only split
that cannot make one output look profitable by making the other look absurd. **A
one-output sector returns EXACTLY 1.0 and multiplying by exactly 1.0 is
bit-exact**, so the seven single-output sectors are identical rather than
approximately identical - and that is asserted at **zero tolerance** rather than
assumed. A line with nothing priced yet shares its bill evenly, because a share
of zero is the same bug wearing a different hat.

**THE OVENS HAVE BOTH OUTPUTS BACK**: BREAD 213,000 + BAKERY 91,000 and BREAD
232,000 + BAKERY 100,000, **52.5% of the crop's mass** split seven to three, crop
bill **29.0% and 28.9%**. Measured where there was 858,000kg producing nothing:

```
BREAD    cap 426,000  exported 270,485  imported 0
BAKERY   cap 182,000  exported 113,850  imported 0
```

**AND THE SIXTEEN-SEED ANSWER TO THE QUESTION THE FOOD BATCH LEFT OPEN:**

| | clean |
|---|---|
| control (goods build) | **16 / 16** |
| FOOD retired | **14 / 16** |

Fisher's exact **p ~ 0.48**, and **2-in-16 is this project's own documented
baseline** for that housing finding - exactly what the student-parent batch
measured. The unusual side is the control being *perfectly* clean, not the new
build sitting at its normal rate. **No regression. Closed.** This batch's own
eight seeds came back 8/8, including the two that had carried findings.

**STILL OPEN, FROM THIS BATCH:**

- **BUSINESS SERVICES AND MANUFACTURING JUST HAD THEIR COST BASIS CHANGE AND
  NOBODY HAS LOOKED AT WHAT IT DOES TO THEM.** Both over-costed every good they
  made for their whole lives; both are correct now. Ensemble clean and harnesses
  green, but neither sector's own behaviour - what it builds, what it exports,
  what it charges - has been measured either side. Worth a look before either is
  next tuned.
- **Building 7 still out-produces building 3** on 48% of its cash cost, half its
  land and 83% of its jobs. Pre-existing, untouched, now true of two outputs.

**Rules earned:** *A method on a container is not a method on its contents - "every
building makes one good" said nothing about what every SECTOR makes, and the
distinction hid a live bug in two sectors for their whole lives.* **And: when a
fix is safe because some case is exactly unchanged, assert that case at zero
tolerance** - `costShareOf` returning exactly 1.0 is the whole argument for
shipping this, so it is a check rather than a comment.

### THE GOOD THAT STOPPED EXISTING — 2026-09-15, SHIPPED, see `the-good-that-stopped-existing.md`

**`Good.FOOD` is retired.** 15 files, **51/51 harnesses**, eight ensemble seeds
either side, byte-for-byte verified, **no save-format bump**. Thirty-three code
references across fourteen files: the mills' warehouse became BREAD, the shops'
input became thirteen invoices on one screen line, the food price became Retail's
basket, and four fixtures that used FOOD as an arbitrary traded good now use a
good the ovens really make.

**A SAVE FROM BEFORE TODAY STILL HAS "FOOD" IN ITS STOCK AND PANTRY.**
`Sector.restore()` drops a good this build does not know - the same answer the
age bands take, because that food cannot go anywhere honest - and **says so out
loud** now, since a warehouse emptying on load is what a player notices and
cannot explain.

**THE SHADOW BASKET OUTLIVED ITS QUESTION AND KEPT ITS JOB.** It existed to ask
whether switching the thirteen on was realism or balance; the answer was 1.03x,
they were switched on, and the good it compared against is gone. So the
denominator changed rather than the probe: what the model says the city WOULD eat
is now measured against **what the shops actually bought**, at the same world
prices. That catches a real failure - restocking is `recentUse x cover` capped by
shelf room, and the previous batch proved those can part company silently and by
a factor of five. **First reading 1.13x**, which is restock lag plus the
affordability cap.

**AND THE BATCH NEARLY SHIPPED A REGRESSION INTO THE ONE METHOD THAT WARNED
ABOUT IT.** Thirteen goods cannot be one `units`, so the obvious move is to value
each stock at its own price and hand the accounts money. The receiving comment
says exactly why not: *"a change in PRICE is neither production nor consumption,
and measuring the change in value rather than in volume booked every price move
as production."* Done that way every wobble in the food market becomes output.

**ALL 51 HARNESSES WERE GREEN ON THE BROKEN VERSION.** What found it was running
the same eight seeds against the **deployed** build: **control 8/8 clean, first
version 6/8**. Eight seeds of the new build alone would have read as this
project's ordinary two-in-sixteen housing finding and shipped. **The control is
the whole method.**

Fixed by aggregating the thirteen at **fixed weights** - each good's world import
price, a constant of the model - so the index behaves exactly as a count of
loaves did, stays immune to a price move, and stays **out of `redenominate()`**,
where the value version would have needed scaling in the one method whose note
records that an unscaled money field once took GDP from +18.43 to -958,009.

**AND THE FINDING UNDERNEATH, MEASURED ON THE DEPLOYED BUILD:**

```
CONTROL: pop 6427  GDP/mo $95,519,968
         FOOD held by every sector: 0.000000
         invInventories $0.00
```

**The goods batch left the city's food inventory investment at exactly zero.**
Nothing produced or stocked FOOD once the ovens were repointed at BREAD, so the
term vanished while the city held **$18.45m of food GDP could not see**. Restored
here: **0.05% of GDP at month 120**, about **$196k a month** against $95m. Small,
and over 4,000 months enough to send a city somewhere else entirely.

**THE SAVE TAKES A NEW SLOT RATHER THAN A CHANGED ONE.** `lastFoodUnits` counted
units of FOOD and now holds a thirteen-good volume index - still a volume, but
WEIGHTED, so an old file's loaf count read into it would be out by the weighting.
The index goes in slot 13 at the tail; a file without it reads zero, which is
exactly the fallback a pre-13-slot file already had. No old file is ever read at
the wrong scale.

**STILL OPEN, FROM THIS BATCH:**

- ~~**THE ENSEMBLE GAP DID NOT FULLY CLOSE.**~~ **CLOSED 2026-09-15 at sixteen
  seeds a side: 16/16 control against 14/16, Fisher p ~ 0.48, and 2-in-16 is this
  project's own baseline rate. No regression.** As recorded at eight seeds: the
  eight seeds read **6/8 clean against the control's 8/8** - the same count, but on
  **different seeds** (13 and 15 rather than 14 and 18). Both are *households
  with no home*, the shape-mix issue that is trajectory-sensitive. Different
  seeds moving each time is what a chaotic difference looks like rather than a
  systematic defect, and at n=8 a 6/8-against-8/8 split is not distinguishable
  from chance — **but it is not proven, and the honest next step is sixteen seeds
  a side before anybody calls it weather.** Recorded on the same precedent as
  seed 14 in the cost-of-funds batch.
- **`ShadowBasket`'s 1.13x has no history.** One reading is not a baseline; it
  wants watching over a few batches before a move in it is treated as a signal.

**Rules earned:** *A harness suite that is entirely green proves the rules you
wrote down, not the ones you did not — the control ensemble is what caught this,
and only because it was run against the DEPLOYED build on the SAME seeds.* **And:
when a method's own comment explains why a quantity is not a value, the thirteen-
good version of that quantity is still not a value.** *And: an aggregate over
goods priced at moving prices is a price index wearing a volume's clothes; weight
it at constants and it is a volume again.*

### THE SHELF COMES APART — 2026-09-15, SHIPPED, see `the-shelf-comes-apart.md`

**Thirteen goods on the grocer's shelf, where there was one called FOOD.** Two
batches in one evening — the exchange-rate units fix, then the goods themselves.
**37 files, 51/51 harnesses, four ensemble seeds clean**, every file verified
byte-for-byte, no save-format change.

**IT STARTED AS A QUESTION ABOUT CANADA AND FOUND A CURRENCY BUG IN MY OWN
BATCH.** Jerus asked what the model's food bill was against Canada's. The model
said $167 a person a month; StatCan says Canadians spend about **$301** on
groceries and **$418** on all food, Dalhousie's 2026 basket **$366**. The
comparison was wrong three ways and every one of them was the exchange rate:
**$167 is a world price**, FOOD's real domestic band was **$249.93–$416.54** at
the measured rate of **2.083**, and Retail's markup against the landed cost is
**1.59x** — a normal grocer's margin — not the 3.3x I first reported.

**`Consumption`'s OWN HEADER ARGUES AT LENGTH THAT THE INCOME MULTIPLE MUST BE A
PURE RATIO, AND THE BATCH SHIPPED ONE THAT WAS NOT.**
`subsistenceCost()` returned **$13.33 in the world's money** and `ShadowBasket`
divided a **domestic** take-home straight into it. *A ratio of two monies is pure
only when they are the SAME money.* Measured: the city reported at **117x
subsistence** when the true figure is **56x**. Renamed
`subsistenceCostAtWorldPrices()` so the old misuse cannot compile, with
**`incomeMultiple(incomePerHead, exchangeRate)`** as the only place the two
currencies meet. **Section 7 of `ConsumptionCheck` already proved the model
survives a currency reform — and the reform it tested was of the FILE's prices
while the bug was where the CITY's money meets them.** Section 8 guards the other
end, including the identity *leaving the rate out overstates the city by exactly
the rate*. The go/no-go ratio moved **1.09x -> 1.03x**: safer than reported, not
riskier.

**THE SHAPE OF THE GOODS BATCH.** Thirteen goods in `Good`, **priced in
kilograms** and deliberately out of step with CROPS and IRON, because
`consumption.json` is written in kilograms and *the one thing that must never
happen to these numbers is a conversion*. **Section 9 of `ConsumptionCheck`
asserts file against enum to the cent** — the one duplication in the model,
guarded rather than trusted. `GROCERIES` is still one unit a head a month,
because that is what the household ledger, hunger, subsistence and the price
index are written in; what changed is that **a unit of GROCERIES is thirteen
invoices**. `Game.cityBasketPerHead()` strikes the basket from each cell's own
income, **weighted by people rather than by money** — a shelf stocked for
spending would carry the elite's fish for a city of labourers.
`Retail.basketsOnShelf()` is Liebig's barrel.

**THE BUILDINGS, and Jerus's call:** **Textile Mill (id 3) -> Industrial
Bakery**, **Food Processing Plant (id 7) -> Bakery**, both making **BREAD out of
CROPS** at 74% of the crop's mass. **Both ids kept** — deleting building 3 would
have orphaned every save containing one. *Jerus had the Textile Mill as the
smaller building; it is not ($18,717 and 65 jobs against $8,994 and 54), so the
names went by actual size.* **Not touched and worth a balance pass: building 7 is
48% of building 3's cash cost, half its land and 83% of its jobs, and it
out-produces it.** True before this change.

**FIVE BUGS THE HARNESSES FOUND, and two of them were structural:**

- **THE SHELF WAS MEASURED IN THE WRONG UNIT.** Shop shelf room was still `1400`
  from when it held 1,400 **units of FOOD** — a person fed for a month. The
  thirteen are kilograms and one person-month is exactly **50.0 kg**, so every
  shop could stock a tenth of the drinks its customers wanted and **delivered 20%
  of demand for ever**. `InfrastructureCheck` caught it on the one assertion that
  says a shop can be supplied at all: **a city with roads and a city without
  scored identically**, both starved by the shelf rather than the road. After:
  **87% against 31%**.
- **A FOUNDING CITY HAD NO BASKET.** `cityBasketPerHead()` is struck from
  household statements and on month one there are none — the shops reach their
  restock before anybody has been settled. An unmeasured city stocks the
  reference household's basket now.
- **THE COST MODEL CANNOT PRICE A BUILDING WITH TWO OUTPUTS.**
  `getMarginalCostPerUnit()` divides the whole line's electricity, water and
  input bill by **one** good's output, so a two-output plant is charged its
  entire cost twice over and neither output clears its own marginal cost.
  Measured: **858,000 kg of nameplate capacity, zero produced**, while the city
  imported bread beside it. **Every other building in the game makes exactly one
  good**, so nothing had ever exercised that path. **See section 4 — the real fix
  is a change to the cost model every sector uses and is its own batch.**
- **TWO PLACES NAME WHAT A SECTOR MAKES, AND THEY DISAGREED.** `FoodIndustry`
  declared `makes(Good.FOOD)` at the **sector** level while its buildings made
  BREAD, and `Markets` iterates the sector's list — so bread was never offered.
  **Nothing failed; the good simply did not exist.** After: the city bakes its
  own bread, imports **none**, and **exports 550 tonnes of surplus a month** with
  the local price properly inside the band.
- **A MISSING `*/` IN MY OWN COMMENT.** `BuildingDataCheck` reported 54 built-in
  templates against the file's 55: an unterminated block comment had swallowed
  the **entire Industrial Bakery template** — every setter and its
  `templates.add` — and still compiled, because the *next* comment's closing `*/`
  closed it.

**FOUR PREMISES RESTATED, NONE WEAKENED.** `ReadPathCheck`'s shelf conservation
law — `getStoreInventory()` used to BE the shelf and is now a **summary**, a min
across thirteen and not a stock. **The law multiplied rather than weakened:
asserted thirteen times, worst gap 0.00e+00.** `AgricultureCheck`'s yield
restated as a mass fraction. **`InvestCheck`'s typed `40,000`** meant "seven
times what one plant makes" while both figures stayed still; they did not, and it
became a rounding error, so it is struck off the template's own capacity now.
**`ForeignCheck`'s bought/sold ratio read 60.034** and looked like a
balance-of-payments hole — it was **kilograms bought against person-months sold**.
In one unit: **1.033**.

**WHAT THE CANADA COMPARISON ACTUALLY SAYS, and it is not about food.** The
wholesale price is sane and the markup is sane. The *poorest* cell — Couple, no
children, Unskilled — has **$7,660 per head per month** against an average
Canadian household's ~$2,665 per person. **Three times richer at the bottom of
the ladder.** `PayTier` is anchored to real NS medians and is not the problem;
band multiples measure **0.71 to 1.17**, so nobody is paying a scarcity premium
and `MAX_MULTIPLE = 4.0` is nowhere near binding. **It is indexation — and wages
have outrun prices by a third.**

**STILL OPEN, FROM THIS BATCH — the first is the biggest thing in the city:**

- **`costOfLiving` IS 2.648 AGAINST A PRICE INDEX OF 1.993.**
  `COST_OF_LIVING_PASS_THROUGH` is 1.0 and `costOfLiving` drifts toward
  `livingTarget = priceIndex` at 1/24 a month, so over 240 months those two
  should have converged. They have not. **Wages are indexed a third above the
  price level they are supposed to be chasing** — a free real-wage gain with no
  productivity behind it, compounding for the life of a run, and it touches far
  more than food. **Next.**
- ~~**RETIRE THE `FOOD` SYMBOL.**~~ **DONE 2026-09-15 — top entry.** 33
  references, and `ShadowBasket`'s ratio got a new denominator rather than being
  dropped: the model's basket against what the shops actually bought.
- ~~**MULTI-OUTPUT COST ATTRIBUTION**, so the bakeries can make BAKERY again.~~
  **DONE 2026-09-15 — top entry**, and it was live in two other sectors.
- **THE CALIBRATION DECISION IS UNCHANGED AND NOW HAS THE GOODS TO TEST IT.** At
  **56x** subsistence every household is still satiated, so the calorie cap sets
  the quantities and all thirteen goods still move as one block. Engel will not
  bite and Bennett will not vary until the income scale is settled. *The goods
  exist now, so there is something real to calibrate against.*

**Rules earned:** *A ratio of two monies is pure only when they are the same
money — and a harness that guards one end of a conversion guards one end of it.*
**And: a unit that changes silently changes everything downstream of it** — the
shelf, the bought/sold ratio and the income multiple were all the same mistake in
three places, and only one of them announced itself. **And: two places that name
the same fact will disagree** — the sector's output list against its buildings',
the enum's prices against the file's; the second is guarded now because the first
was not.

### WHAT A HOUSEHOLD EATS — 2026-09-15, SHIPPED, see `what-a-household-eats.md`

**Thirteen food goods, one model, one data file, and NOTHING EATS ANY OF IT
YET.** Five files, **51/51 harnesses**, the ensemble **byte-identical** to the
shipped build apart from the wall-clock line, no save-format change. Jerus's
call on both open questions: the goods sit dormant beside FOOD, and the price
index's weights will come from the consumption model rather than from a month's
tills.

**THE HEALTHY/UNHEALTHY LEAN IS NOT A DIAL, IT IS TWO AXES.** Jerus asked
whether it should be pay-tier, family-structure or wealth based; it is none of
them alone, because two effects were being felt at once. **Engel's law** - as
income rises the SHARE spent on food falls while the AMOUNT rises - and
**Bennett's law** - as income rises calories shift off starchy staples onto meat,
dairy and produce. Bennett IS the lean: it is not a separate mechanism, it is
what income does, which is why GRAINS carries a NEGATIVE elasticity. A staple is
an inferior good.

**But INCOME PER HEAD, not pay tier** - a large family on a skilled wage has less
per mouth than a couple on an unskilled one, and a tier is a wage RATE. **And not
wealth**: food tracks income far more than savings in every measurement, and this
game already has "wealth never bids" as a broken link; making food the one
exception would be an odd place to start. **The third axis is not income at
all** - convenience food is bought for TIME, so a working parent buys it at any
income and an out-of-work parent with the same children and the same money does
not. Folding that into income makes a poor household that cooks and a time-poor
household that does not into the same household.

**INCOME IS MEASURED IN MULTIPLES OF SUBSISTENCE, and that is load-bearing.**
Engel's curve is a logarithm of income, and a logarithm of a MONEY figure is a
money constant wearing a function's clothes - lop two zeroes and every household
appears to fall to a tenth of its income and starts eating like the destitute.
This codebase has found twenty of that family and seeded four constants against
it. Dividing by what subsistence COSTS makes the argument a pure number that no
reform can move and that never needs seeding. `ConsumptionCheck` loads the same
file at a hundred times the prices and asserts **every household buys what it
bought, to the gram.**

**THE PROBE FOUND THREE BUGS BEFORE ANYTHING ATE ANYTHING**, which is the whole
argument for modelling first and connecting second:

- **Calories FELL as households got richer** - 426 a day at five times
  subsistence, 194 at twelve - because a typed floor under the Engel share made
  spend collapse faster than income grew. **Satiation replaced it**: a basket is
  scaled by the LESSER of what a household can afford and what it can eat, so
  poor households are money-limited and hungry and rich ones are calorie-limited
  and buy dearer food instead of more. The falling share at the top is now
  something the model PRODUCES rather than is told.
- **A household rich enough to buy the city starved**, because past a point the
  curve went negative and the basket came back empty. The share is held at B,
  which is SOLVED FOR rather than typed: implied spend rises while the share is
  above B, is flat at B, and falls below it, so B is exactly where the curve
  stops describing anything.
- **A famine diet scored BETTER than a mixed one** (.61 against .55) because
  quality was the calorie-weighted average of what was in the basket. Nothing is
  wrong with rice; what is wrong is eating only rice, and an average cannot see
  that. It is **distance from a balanced target** now - the only shape that
  punishes both ends, so food has a health cost at the top of a city as well as
  the bottom.

**THE SHADOW BASKET IS THE GO/NO-GO, and it caught its author too.** Its first
answer compared a basket at WORLD prices against `shoppingPerHead`, which is the
shelf price with Retail's whole markup on it, and reported that switching on
would cut the city's food bill **to a third**. It would not - the two were never
the same kind of number. *Arithmetic on the wrong number is still wrong*, the
rule this project wrote that morning, catching the person who wrote it that
afternoon. Like for like: **1.09x**. Switching the goods on is a nine per cent
change to the food import bill, which is a change of detail.

**AND THE REAL FINDING IS THAT THE CITY IS OFF THE END OF THE CURVE.** Poorest
cell **117x** subsistence, richest **2,617x**, and **14,592 of 14,592 people
satiated**. Every household in a healthy city is calorie-satiated, so Engel never
bites and Bennett has nothing to vary over - diet quality runs 0.56 to 0.67 in a
narrow band and the basket is 2.3% staples everywhere. **So the money says turn
it on and the behaviour says it would be invisible.** Where it would bite is
where the day's other two batches have just put real books: the out-of-work, the
students, and a city in a bust.

**STILL OPEN, FROM THIS BATCH — and the first one is a decision:**

- **IS THIS CITY MEANT TO BE THIS RICH AGAINST ITS FOOD?** At shelf prices its
  households spend about **8.7% of income on food**, which is a rich-country
  TOP-QUINTILE figure for a city whose median worker is on an unskilled wage.
  Either food is far too cheap against the wages or the wages are far too high
  against the food. **Design, not a bug**, and it decides whether the consumption
  model is rich texture or a flat line - so it wants settling before the goods go
  live. *The goods went live on 2026-09-15 anyway, deliberately, because the
  ratio was 1.03x and could not hurt — and measured against Canada the answer is
  now specific: the wholesale price is sane and the INCOMES are about three times
  a Canadian's at the bottom of the ladder. See the top entry.*
- ~~**The shop**: thirteen goods on the grocer's shelf is thirteen import lines,
  thirteen VAT lines and thirteen trade entries into a cent-level audit, which is
  the most leak-prone thing in this codebase.~~ **DONE 2026-09-15 — top entry.**
  Four ensemble seeds: *"Every month passed the audit and every reload matched."*
- **The index**: weights off `Consumption` at the city's own incomes, a category
  tree so "food 4%, shelter 2%" is sayable, and a second index re-weighted to
  actual spending - **the gap between the two is how much worse the city is
  eating**, a welfare number the game cannot currently express.
- **`Good` has to stop being an enum** before the count reaches a thousand.
  Consumption already addresses goods by key so it will not change when that
  happens; everything else will. With the by-name save work done three times over
  this is a much safer move than it was a week ago.

**Rules earned:** *A curve fitted to two anchors should be probed across its whole
range before it is believed - all three bugs above were visible in one table and
none would have shown in a unit test of a single point.* **And the bigger one: a
floor typed into a model is a mechanism that has not been found yet.** The Engel
floor, the shelf floor and `MIN_FAIR` are the same mistake three times; satiation
is what the first was standing in for, and the shelf floor is still standing in
for something nobody has named.


### THE PARENT WHO WENT TO SCHOOL — 2026-09-15, SHIPPED, see `the-parent-who-went-to-school.md`

**The children of an adult who leaves work go with them.** Nine files, **50/50
harnesses**, **sixteen ensemble seeds a side**, byte-for-byte verified, **no
save-format bump**. Jerus's call on both open questions: the shape is *the
children follow the parent out*, and the scope is *students and the out of work
together*.

> **Worst orphan month on the probe city: 2,093.7 -> 12.4.**
> **Child deaths over one 4,000-month seed: 55,546 -> 47,503.**

**THE BUG WAS ONE SUBTRACTION.** `FamilyModel.rebuild()` builds the families from
the adults who WORK, so every adult who is not working was subtracted from the
pool and the children who lived with them were offered to whatever families
remained. Whoever was left over was an orphan - fed by nobody, dying at the
no-care rates, a baby at 8% a year against 0.017% fully served. **The door was
open four ways**: enrolment is the one slot 3 walked through, but a layoff, EI
running out and prison are the same subtraction, which is why the fix is written
against *"an adult who has left work"* rather than against students.

**CHILDREN FOLLOW ADULTS AT THE CITY'S OWN RATE** - the only honest rule for a
model with no individual people in it - and they are taken out BEFORE the builder
runs, so they are neither offered to the families nor left over at the end. What
they live on is the household they went to: a student's grant and loan, a
claimant's EI. **A student supporting two children borrows more and a laid-off
parent with two children is evicted sooner. Both are the point, and neither
needed a new dial.**

**THE PRISONER IS THE EXCEPTION AND IT IS ASSERTED, NOT ASSUMED.** A prisoner
genuinely is not at home. `OutsideCheck` runs the same pyramid twice - four
hundred adults out of work, then the same four hundred in prison - and the
difference between those two lines is the whole of what this change decides.

**THE MOUTHS AND THE EARNERS PART COMPANY, and that is the point of having both.**
`headcount()` is who eats (hunger, the shopping plan, the fees that follow
heads); `grownUps()` stays ONE, because a student with two children is one wallet
and the row's income is split by wallets. **Reading the wrong one of those is how
the over-85s came to draw no pension four days ago**, so they are named apart and
the next person has to choose. The dependants arrive as a LIVE FUNCTION beside
the census and the rent shares rather than as a field anyone copies.

**THE SAVE GREW A TAIL, NOT A WIDER BLOCK.** Everything in the outside block sits
in front of the seekers and the memory, so growing it moves every offset behind
it and every save on disk is read at the wrong place. A tail costs one comparison
in the guard and an older file is simply short. Carried at all - rather than
re-derived - because the first frame of a reloaded city is read before any
rebuild has run.

**A NEW CONSERVATION LAW:** *the children in the families, plus the children who
went out of work with a parent, plus the orphans, are every child in the pyramid*
- asserted per band. If any future change subtracts an adult from the families
without saying where their dependants went, this fails on the month it happens.

**THREE HARNESSES FAILED AND ALL THREE WERE RIGHT TO:**

- **`DeathRecordCheck` failed because the model got better** - variant (a), and
  the clearest instance yet, because **its own comment described the bug it was
  built on**: *"the parents who worked there leave their families"*. The premise
  is inverted into a regression test, watched across the whole two-year window
  rather than read at the end of it (measured, the out-of-work count is LOWER two
  years later, because the city shrank instead - the endpoint says nothing). Its
  premise is now stated from the model's own rate: *under the old rule this wave
  would have orphaned 35.1 children against the 5.78 the city had.*
- **`PopulationCheck` failed because the array now has TWO band-indexed blocks**,
  not one. Its "a band from the future" fixture spliced one slot and was refused,
  correctly; it splices both now.
- **`HouseholdMemoryCheck` failed because it worked a boundary out by
  SUBTRACTION** - today's length minus the memory block, which is right exactly
  while the memory block is last. `FamilyModel.slotsBeforeMemory()` now answers
  it; the layout is that class's business.

*And one of my own new assertions was wrong on its first run: I claimed two
out-of-work cells draw the same EI whatever they are feeding. They do not -
`earningWeight()` is zero once the claim runs out, so **a child draws no EI**.*

**THE ONE THING THAT IS NOT CLEAN, and it is recorded rather than tuned away.**
Sixteen seeds a side: the shipped build **0 of 16** findings, the fix **2 of 16**,
both *households with no home*, both short, and both inside the same
twenty-five-month window (709-733). Two in one window is not noise, so it was
chased. **It is not evictions and not a bigger city** - both flagged cities are
SMALLER than their controls there, which killed the first hypothesis. **It is the
shape mix**: fewer children in the family pool means the builder forms smaller
shapes from the same adults, and smaller households need more doors. Measured on
2,000 adults and 1,200 children with 400 outside - **1,279.7 doors with nobody
outside, 1,379.7 with them in prison, 1,423.8 with them at home.** The middle
jump is the 2026-09-11 decision, not this one; this adds the last 3%. **Eight
thousand fewer dead children for 137 households short of a door for sixteen
months**, on the same precedent as seed 14 in the cost-of-funds batch.

**STILL OPEN, FROM THIS BATCH:**

- **AN OUT-OF-WORK ADULT IS A SEPARATE HOUSEHOLD FROM THEIR WORKING PARTNER**, so
  a couple that loses one job needs two doors where it needed one. That predates
  this batch by four days and this batch is what made it measurable. Whether the
  outside cells should be a MEMBER of a family that keeps its door is the same
  question the three shapes were about, one level down.
- **A CHILD BENEFIT DOES NOT EXIST.** EI and the student grant are per adult and
  the children now eat out of them, so a parent on EI is measurably poorer than a
  single claimant on the same benefit - true, and no dial anywhere answers it.
- **The orphanage**, still. The residue is nine or ten children now instead of
  thousands, and a prisoner's children join them deliberately, but they are still
  fed by nobody.

**Rules earned:** *A conservation law is worth stating outright the moment a model
starts moving people between containers. "Every child is in a family, outside with
a parent, or an orphan" is one assertion that would have caught this on the day it
was written.* **And: when a fixture's own comment describes the behaviour you just
changed, that fixture is testing the bug.**


### THE BAND THAT WAS TWO BANDS — 2026-09-15, SHIPPED, see `the-band-that-was-two-bands.md`

**The senior band is split at 85.** `SENIOR` 70–85 and `ELDER` 85–120, each with
its own household shapes, mortality, sickness, care weighting and care swing.
**Save format 27.** 25 files, **50/50 harnesses**, **8/8 ensemble seeds clean**,
every file verified byte-for-byte, CRLF/LF kept per file.

**THE BAND:**

| | 70–84 | 85–119 |
|---|---:|---:|
| annual mortality | **2.90%** | **12.77%** |
| senior-care place per head | 0.19 | 1.00 |
| long illness kills, per month past two | 4% | **8%** |
| what care is worth (swing) | 1.35x | **1.80x** |
| coupled rather than alone | 55% | **25%** |

OSFI 2019 rates, interpolated log-linearly, Gompertz past ninety, weighted on a
stationary table from 70. Cross-checks: constructed **e85 = 7.8** against OSFI's
published **7.5**; through the game's two buckets **e70 = 14.9 against 15.4**,
and total 70+ deaths move 6.5% -> 6.7%. **Nearly aggregate-preserving**, so the
seeds read the care denominator and the household shapes rather than a mortality
step. **The care weighting is normalised on the elders (1.00 against 0.19), not
absolute** — the raw census rates would have divided the denominator by ten and
handed every existing city full senior care overnight, which is a balance change
wearing a realism change's clothes. The absolute version is a separate decision.

**THE SPLIT ITSELF WAS THE EASY HALF. Chasing the last two red harnesses turned
up three defects with nothing to do with age bands.**

**1. `OutsideCheck` was never a question about the valve.** The previous
write-up speculated that the elder shapes had changed what the housing match
leaves over, and warned against relaxing the block. It was **one line**: that
fixture still handed a today-width array to the nameless `restore`, so the
pyramid came back empty and there were no families to leave over. Six other
fixtures had been fixed the same way in the previous pass; this one was missed.
*The lesson is mine, not the code's: I wrote a paragraph of model-level
speculation about a failure I had not yet read the fixture for.*

**2. `DenominationCheck` WAS THE FIFTEENTH OF THE FAMILY, and not the drift I
predicted.** The last write-up called it "the drift the manual documents" and
said more household rows plausibly means more of it. **Wrong** — and the harness
says so itself, in a comment written after fourteen bugs had been found through
it: *"the decade after, to a millionth. There is no band left to hide in. If any
of it ever moves again, it is a fifteenth of the same family."*

**The market prices every earnings-valued company to exactly the same yield, by
construction.** `Equity.priceOf()`'s earnings branch is
`PAYOUT x income / (worldRate + FOREIGN_PREMIUM)` and
`dividendPerShareAnnual()` is `PAYOUT x income / shares`, so the yield of any
company valued on its earnings **IS the discount rate** — whatever its income or
share count, and the same again at the ask once the spread divides it.
`Exchange.buyForHouseholds()` then sorts "best first" and hands the winner the
whole month's unmet demand. At month 129 six companies were tied at
**0.0495049504950495** and the sort was choosing between copies that differed in
the last bit: one city put Industry first and its reformed twin put Heavy
Industry first, **the winner's quote went 2.97 -> 8.04 in a month**, and the two
cities were different cities from there. Once in roughly 200 months of buying,
in the shipped build as well as this one.

**The fix is not a tie-break by index** — deterministic and biased, it would hand
Retail every tied month for ever. Buyers who cannot tell two things apart spread:
the tied front runners share the unmet demand in proportion to shares on issue
and are ordered among themselves by the same measure, under a relative dead band
(`TIED_YIELD = 1e-9`) that needs no seeding, the same reasoning as `MIN_EXCESS`.
With one front runner the arithmetic is the line it replaced, unchanged.
**Decade-later agreement went from 1.13e-06..5.07e-05 to 4e-16..9e-16 — the two
cities are now bit-identical for a decade.** There was no drift; there was one
step function, and every digit came out of it. `ExchangeCheck` gained a block
that CAUSES the tie the way the model does and asserts a one-ulp perturbation
changes nothing. **Green on the shipped build alone**, so it stands independent
of the split.

**3. THE ELDERS DREW NO PENSION, AND NOTHING COULD SEE IT.** Found by probing,
not by a harness. `RetiredHousehold.grownUps()` answered
`shape.membersOf(AgeBand.SENIOR)`, and `HouseholdBalance` splits each row's
income across its cells **by that weight** — so the moment the over-85s got their
own shapes they weighed nothing and **drew no pension at all**.

Why nothing caught it: the pension BILL was right (`HouseholdAccounts` already
summed both bands); **the money audit balanced**, because the same total left the
treasury and only landed in the wrong cells; all 50 harnesses passed; and the
failure is slow — an elder paid rent out of savings until there were none, the
landlord's revenue fell, and it shed plant under the rule that a firm which
cannot pay must. **Measured: 7,955 homes and 18,400 people down to 2,881 and
4,200 over fifteen years, where the same city on the shipped build never lost a
single home.** `HouseholdCheck` now asserts, for every retired shape, that its
pensioner count equals its size and that the row split weighs them all, and in a
live book that an elder living alone draws exactly what a senior living alone
draws; reintroducing the old line fails four of them. The UI's household panel
had the same bug and is fixed with it.

**4. `ForeignCheck`'S FIXED PROGRAMME WAS NEVER FIXED.** The pension fix pushed
its premise over: **1.0063 shipped -> 1.0275 with the split -> 1.0558 with the
pension**, against a 5% band. Construction imports the material for what a city
builds, and most of what a city builds is **houses** — which the landlord puts up
against the household count, not against the fixture's order. Measured: the
weaker city held 2,765 homes for 2,765 households against the parity city's
2,068 for 2,436 — **seven hundred more houses on two hundred fewer people** — and
Construction imported $924,634k against $867,630k. That gap was the entire
discrepancy; no other sector moved. **The remedy is the one this same method
already used twice, for Manufacturing and Agriculture: hold the sector out.**
Real Estate is the eleventh. The premise reads 0.9805, and **1.0038 on the
shipped build, tighter than the 1.0063 it used to read**. The instrument is
SHARPER for it — the weaker city now exports $46,791k of food against $14,418k
and imports $3,194k against $29,662k, where the two cities used to differ by
three per cent on the export side and read as noise. A per-sector import line is
printed now, so whoever next finds this drifting can see which sector grew the
hinge instead of bisecting for it.

**NO TOLERANCE WAS WIDENED ANYWHERE IN THIS BATCH.**

**THE SAVE-SIDE BUG THE SPLIT FLUSHED OUT**, carried from the previous entry:
`FamilyModel.noteUnplaced()` adds to `doubledUp` and clears its three arrays but
not that scalar. The live path zeroes it first; the load path runs one pass over
an already-squeezed matrix and never zeroes, so it added a second helping of
crowding on top of the restored figure. Invisible while that pass absorbed
nobody. The split gave the same people more households, `left` went non-zero, and
a city saved with **17.9** crowded households came back with **36.5 and
twenty-five crowded seekers it never had**. `SaveFileCheck` caught it, and it was
confirmed pre-existing against the shipped build (18.2382 both sides). Fixed with
`adoptCarriedDoubling()`. **And a fourth save landmine:** `FamilyModel`'s matrix
is shape-indexed and positionally saved, so adding two household shapes would
have discarded every save's households — `DataSave` carries `shapeNames` beside
`bandNames` now, with `LEGACY_SHAPES` naming the thirteen a file on disk has.

**VERIFIED:** 50/50 harnesses with `UserInterface` compiled; eight seeds all
reporting *"Nothing. Every month passed the audit and every reload matched"*,
ending at **87k-145k people against the shipped build's 69k-139k** on the same
seeds; the tie fix and the `ForeignCheck` fixture both green on the shipped build
on their own.

**STILL OPEN, FROM THIS BATCH:**

- ~~**THE STUDENT-PARENT DID NOT RIDE IN THIS BATCH**~~ — **it shipped the same
  day, on its own; see the top entry.** The decision doc it needed is now the
  build write-up at the same path. **The batching reason is answered: the split does not move the orphan
  baseline** (11.5 shipped against 12.1 split, same founding), so it can be
  measured against today's build whenever. What it needs is a choice between
  three different money models, and Jerus's own `reading-slot-3.md` lists it
  under "Decisions for Jerus". **What DOES move orphans, in both builds, is a
  jobs event** — one founding lost 8,448 posts in two years and orphans went 12
  -> 2,094 — so the fix is worth more than the student case alone suggests, and
  should be written against *"an adult who has left work"* in general rather than
  against students. **The prisoner is the one case that should stay as it is.**
- **A LOADED CITY HAS NOBODY OVER 85 FOR A FEW YEARS**, so senior care reads
  fully covered while the band fills. Known, accepted, transient.

**Rules earned:** *Ask the BAND whether it is retirement age; do not name one.
`CareType` needed `isRetirementAge()` for the same reason a week earlier, and
`RetiredHousehold` is the third time this codebase has learned it.* **And: an
argmax over quantities the model prices to be equal is a step function driven by
dust** — the same shape as a constant in absolute money, which is what the other
fourteen were. **And: a money audit that balances proves the total left, not that
it arrived anywhere sensible** — the elders' pension was wrong for a whole batch
with every harness green and every cent accounted for. **And, for me rather than
the code: do not write up a failure you have not yet read the fixture for.**

### THE BAND ARRAYS ARE SAVED BY NAME — 2026-09-15, SHIPPED IN TWO PASSES, see `the-pyramid-is-saved-by-name.md` (rewritten and retitled *"The band arrays are saved by name"*; same path)

**Groundwork for splitting the senior age band at 85** — Jerus's call, after
noticing that senior care is measured against **every person over 70** while real
long-term-care residency runs **2.0% at 70–74 and 29.6% at 85+** (2011 census).
Nothing about ageing changed in this batch. This is the one thing that had to be
true before a sixth band could be added without destroying every save in the
world. **Two passes in one night** — the pyramid first, then the families and the
ring of the long sick, once an audit found they had the same disease.
*The split itself shipped the following day — top entry.*

**THE LANDMINE IT DEFUSES.** `PopulationCohorts.restore()` took the array's width
from **the READING build's band count** — `band.length + 3` with migration,
`band.length + 2` without. With five bands it accepts a length of 8 or 7; with
six it accepts 9 or 8, and **every save ever written is 8**. So an old city would
have sailed through the guard as though it were *"six bands, births, deaths, no
migration"*. On slot 3 that is **2,716 last-month births reinterpreted as 2,716
people over 85**, deaths read as births, migration read as deaths. No exception,
no refusal, no log line — a city of 1,226,167 people silently and permanently
wrong. **And the guard's kindness is what made it silent:** it was widened once
deliberately so an older save would not be refused — its own comment says doing
so "is far worse than the missing figure: this array IS the population" — and
that permissiveness is exactly what lets a wrong WIDTH through. A stricter check
would have refused the file out loud.

**THE FIX IS THE SECTORS', at Jerus's suggestion** — *"perhaps have them
similarly to how sectors are done"*. Nothing about the eighth, ninth and tenth
sectors corrupted anybody's shareholdings, because `DataSave` has carried
`equityKeys` and `householdCellKeys` and the restore maps **by name**. `DataSave`
carries **`bandNames`** now — **one field describing the whole FILE**, with
`setBandNames()` and `getBandNames()`, because the band list is a property of the
SAVE rather than of any one array: everything band-indexed was written from the
same `AgeBand.values()` in the same moment, and **all three band-indexed arrays
read against it**. *(The first pass recorded this entry as `cohortBands`, a field
of the pyramid's own; the second pass, which found two more arrays, made it the
file's. Corrected.)* `PopulationCohorts` gained `LEGACY_BANDS`, `saveBands()` and
`restore(String[] bands, double[] saved)` beside the nameless
`restore(double[] saved)`. **The width comes from the SAVE.**
A file written by a five-band build stays a five-band file for ever, whatever
this build's enum says, and the three scalars sit after however many bands *the
file* had.

**`LEGACY_BANDS` IS A LITERAL FIVE-NAME LIST AND DELIBERATELY NOT
`AgeBand.values()`** — it describes a file already sitting on somebody's disk, so
it must not move when the enum moves. `PopulationCheck` asserts the literal five,
which means **the assertion fails if anybody ever "fixes" it to track the enum.**

Two decisions written into the reader rather than left implicit: a band in the
save that this build does not know is **dropped, not shifted** (those people
cannot go anywhere honest, and `SAVE_FORMAT` already refuses a newer save — this
is the second lock on that door); a band this build has that the save lacks
**starts empty**.

~~**NO SAVE-FORMAT BUMP.**~~ `bandNames` is additive, and an older build ignores a
field it does not know and reads the array positionally — which stays correct
until the band list changes. **The bump belonged to the split batch, and it
happened: save format 27, 2026-09-15.**

**THE SECOND PASS: TWO MORE OF THE SAME CLASS, FOUND BY AUDIT, BOTH PREREQUISITE
TO ADDING A BAND.** An `Explore` sweep of every site that assumes five bands, run
because the first one had just proved the class exists:

- **`FamilyModel` writes the orphans in the MIDDLE of its array**, with the
  seekers and the unhoused-by-shape behind them, and derived every accepted
  length from `AgeBand.values().length`. One more band and every save on disk is
  a slot short of **all five** accepted lengths, so `restore()` returns at the
  guard having restored **NOTHING** — not the orphans, not the household matrix,
  not the formed-household memory. It fails safe rather than misreading, because
  no two accepted lengths collide. It still fails silently.
- **`Sickness` checks its ring with an exact `!=`**, so every existing ring would
  have been refused and silently reseeded — a city that forgets who has been ill
  for eleven months, on the frame it loads.

Both take `restore(String[] bands, double[] saved)` now and take their width from
the save; `FamilyModel.outsideSlots(int bands)` replaces the constant. *One of
the two would have thrown a save away and the other would have refused a ring
nobody was told about, and neither was reachable from the file the first pass
fixed.*

**THE HARNESS IS WRITTEN FOR THE BUILD THAT DOES NOT EXIST YET.** Eleven blocks
now in `PopulationCheck`, written to keep passing AFTER the sixth band lands rather
than to describe today's five: the round trip by name; **order carries no
meaning** — the same values handed over with the names reversed, checked per band
rather than on the total, because a total matches under any permutation; **a band
from the future is dropped, not shifted** — a six-band save read by a five-band
build, with the scalars read from after all six, which is the landmine from the
other side; **a nameless save is five bands whatever this build has**, the
assertion that has to outlive the split; **a real old save through Gson**, using
slot 3's actual eight numbers so nothing is invented, asserting that 2,716 is
still *births* and the city is still 1,226,166.74 people; and a malformed array
still refused whole, named or not. **Three more for the families and two for the
ring** came with the second pass — the families by name, as a nameless save, and
through a widened orphan block carrying a band from the future; the ring both
ways, and refused when its width does not match its names. All five assert by
**round-tripping the whole array rather than by naming getters**, because the
array IS the contract and a getter-by-getter check tests whichever ones somebody
remembered.

**SHIPPED:** 50/50 harnesses on both passes; his real `slot-03.json` read through
all three new readers — the pyramid **1,226,166.74 people**, the families
**354,037.36 households** and **34,780.93 orphans**, round-tripping **193 of 193
slots exactly**, the ring restored at **72 slots**. Five files in the second pass
(`Sickness`, `FamilyModel`, `DataSave`, `Game`, `PopulationCheck`), **nine across
both**, every one verified byte-for-byte and each keeping the endings it had
(`Game.java` and `Sickness.java` LF, the rest CRLF).

**JERUS'S FOUR DECISIONS ON THE SPLIT, ANSWERED BEFORE HE WENT TO SLEEP:**
**(1) Mortality** — real life-table rates, not today's aggregate preserved.
**(2) Old saves** — seniors all start in 70–85 and age across; no invented
division at load. **(3) Care** — it serves both bands, weighted by the census
residency curve (2.0% at 70–74 against 29.6% at 85+), so home care stays in the
model. **(4) Batching** — the student-parent fix goes in the same batch as the
split. *(1) to (3) shipped. **(4) did not** — the batching reason turned out not
to bind, because the split does not move the orphan baseline; see the top entry
and `the-parent-who-went-to-school.md`.*

**THE MORTALITY CALIBRATION IS SETTLED, AND IT IS NEARLY AGGREGATE-PRESERVING.**
From the OSFI 2019 Canadian rates, interpolated log-linearly and extrapolated
Gompertz past 90 (mortality doubling every 6.1 years), population-weighted on a
stationary table from age 70: **70–84 at 2.90% a year** and **85–119 at 12.77% a
year**. Cross-checks: the constructed table gives **e85 = 7.8** against OSFI's
published **7.5**; run through the game's own two-bucket model, **e70 comes out
14.9 years against today's 15.4**, and total deaths among the 70+ move from 6.5%
to about **6.7% a year**. *So the real gradient adds shape at the top without
moving the city.*

~~**STILL OPEN, FROM THIS BATCH:**~~ **ALL OF IT SHIPPED 2026-09-15 — top entry.**
The split itself; the full silent/loud audit table, every item of which was the
spec and every item of which is done; and the three judgement calls, answered by
Jerus as *"so oldest band gets its own shape yes larger swing, and yes they die
more"* — the oldest band has its own shapes (`ELDER_ALONE`, `ELDER_COUPLE`), its
own `Sickness.deathChance` (8% against 4%), and its own care swing (1.80x against
1.35x).

- **DELIBERATELY NOT BATCHED, AND STILL NOT:** splitting `ADULT` as well (two band
  splits make a sixteen-seed comparison uninterpretable), the population drawdowns
  (a diagnosis, impossible while mortality moves underneath), the birth rate
  calibration, and `SINGLE_ADULT`.

**Rules earned:** *A state array's width must come from the SAVE, never from the
reading build's own count of things. Deriving it from the current enum means the
day that enum grows, every existing file is read at the wrong offsets and passes
every guard. Save the names beside the values and map by name — the sectors
already do.* **And extended by the second pass: after finding one, AUDIT FOR THE
REST BEFORE CHANGING THE ENUM.** *Two more of the same bug were sitting behind
the first — one that would have restored nothing at all and one that would have
thrown away the ring of the long sick — and both would have shipped, because
fixing the array that prompted the question makes the question feel answered.*
*And the split itself added a third: the audit found every site that was band-INDEXED
and missed the one that was band-NAMED — `RetiredHousehold.grownUps()` asked for
members of SENIOR, which is not an array at all. See the top entry.*

### ONE DEFINITION OF UNEMPLOYMENT — 2026-09-15, SHIPPED, see `one-definition-of-unemployment.md`

**`reading-slot-3.md` corrected the year book's unemployment column and ended
that section with "only the book was wrong". It was not.** The same formula —
`(workforce − jobs) / workforce` — was still live in
`UserInterface.historyValues("unemployment")`, which is a pickable Reports line
labelled **Unemployment** and one of the four in the *"The people"* preset.
`averageWage` beside it divided the wage bill by the same `workforce`, which
carries every student and every prisoner — people who are paid nothing.

**MEASURED ON SLOT 3'S OWN HISTORY.** At month 2,200 the chart drew **5.5%
unemployment in a city with 401 MORE posts than people to fill them** and empty
out-of-work ledgers; at month 2,100 it drew **4.1% against 7,600 unfilled
posts**. The average wage came out about **5% low**, and worse the more the city
studied. *The line was the student count wearing an unemployment label.*

**AND IT WAS A REPEAT.** `PopulationManager.getLabourForce()` carries a comment
dated **2026-09-06** recording that counting students as job-seekers was already
found and fixed once, on the People screen. It came back a layer up, because the
chart kept its own copy of the arithmetic — so the People screen and the Reports
graph have been quoting different unemployment rates for the same city.

**THE FIX IS ONE DEFINITION, NOT TWO CORRECTIONS.** `YearBook` holds them now —
`labourForce()`, `filledPosts()`, `unemployment()`, `averageWage()` — and
`UserInterface.historyValues()` delegates to them in three lines. It is the same
argument `PopulationManager.getUnemployed()` already carries in its own header,
applied one level further out.

**NEW FOR FREE:** `averageWage` and `labourForce` are columns in the book now,
and `labourForce` and the recorded `outOfWork` are pickable traces on the chart
— there was previously **no way to plot the actual pool at all**.

**WHAT IT DOES TO SLOT 3'S BOOK:** unemployment year 100 **1.94% → 0**, year 150
**4.79% → 0**, year 184 **5.44% → 0**, year 190 **5.58% → 0.04%**. A century of a
city at full employment that read as a city with an unemployment problem.

**THE HARNESS NOW ASSERTS THE INPUT, NOT JUST THE FOLD.** `YearBookCheck` gained
`theBookAgreesWithTheModelOnAPlayedCity()`, which asserts nothing the book
computed — only that the file says what the game says: the labour force is
`getLabourForce()`, the filled posts `getJobsFilled()`, the rate
`getUnemployed() / getLabourForce()`, the average wage the recorded bill over the
filled posts. **Its premise is asserted first, and the premise FAILED on the
first run** — after sixty months of the default founding nobody is studying, so
`workforce` equals the labour force and the broken formula would have passed.
The fixture funds a city and builds three basic stages plus a community college
now, and asserts that **the formula it replaced actually disagrees with the model
there**. **The tolerances are derived from the history's own storage precision**
— `2 × A_CENT` for `round2()` money, half a person for a whole-person pool — not
picked.

**SHIPPED:** 50/50 harnesses, **294 assertions** in `YearBookCheck`, three files
deployed and verified byte-for-byte, CRLF kept on all three.

**STILL OPEN, FROM THIS BATCH:**

- **THE TWO NEW TRACES AND THE CORRECTED LINES HAVE NOT BEEN SEEN ON SCREEN.**
  Compiled, never looked at. On a rebuild: Reports → *"The people"* preset, and
  check that **Unemployment** now sits near zero through the long middle of slot
  3 rather than at 5%.

**Rules earned:** *The first edition of the year book shipped 277 assertions and
three wrong columns, because every assertion tested that the FOLD was right and
none tested that an input meant what its note claimed. Arithmetic on the wrong
number is still wrong. When a harness checks a derived figure, one assertion must
compare it against the model's own definition on a played city.*

### READING SLOT 3 — 2026-09-15, see `reading-slot-3.md`

**Jerus sent the decade book of his slot-3 city** — 0.5.15, 3,271 months, 1.23
million people — and asked what it says. The read is in the write-up; what it
changed and what it opened is here.

**THREE CORRECTIONS TO THE BOOK ITSELF, SHIPPED.** The `fxRate` note was
**backwards** — it said US dollars per Danzik dollar, lower is a fallen currency;
`ForeignAccounts` is local dollars per US dollar and HIGHER is a fallen currency,
which is how every write-up reads it. **The `unemployment` column was not
unemployment**: `(workforce − jobs) / workforce`, where `workforce` still holds
the students and the prisoners and `jobs` is posts offered, not filled — on slot
3 it read 11–15% for seventy years of a city whose out-of-work ledgers were
empty and whose EI paid nothing, because the labour force (598,203 − 96,045
students − 1,360 prisoners) is *smaller* than the 511,574 posts. **The city is
at full employment with 11,000 posts it cannot fill, and the 14% was the
students.** The history records the pool now (`HistorySave.outOfWork`, the
People screen's `getUnemployed()`), the book divides it by the labour force, and
months from before today fall back to the labour force less the posts, with the
note saying which. The `workforce` note ("students and prisoners excluded") was
wrong too. `YearBookCheck` gained two sections — **286 assertions** — the pool
over the labour force with and without the recorded series, and the currency
note's wording. Three files, 49/49 on the 0.5.15 tree in the cloud, deployed and
verified byte-for-byte. *The ensembles stand: `LongPlaytest` and the screens use
the model's own rate.* ~~*Only the book was wrong.*~~ **It was not — the same
formula was still live on the Reports chart, and on the average wage beside it.
Corrected 2026-09-15; see the top entries.**

**SLOT 3'S EARLY YEARS ARE NOT A UNIT PROBLEM — THEY ARE A CURRENCY COLLAPSE.**
A reform is a step; this is a forty-year arc whose recorded inflation rates match
its recorded levels: rate 1.9 → 58.5 → 24 → 3.5 → 1.8 → 0.85 by decade, the index
2.2 → 91 → 37 → 3.0 → 1.8 → 1.1, inflation +22%, +43%, −22%, −13%, −4.5%, and
food in US dollars **never moved** ($186–$200 a unit throughout). The rate hit
`MAX_RATE = 100` in month 184 — the ceiling whose own comment says reaching it is
the finding. **The mechanism is `ForeignAccounts.repriceCurrency()`'s relative-PPP
drift**, `rate *= 1 + (localInflation − worldInflation) / 12`: in a founding city
the basket is imports priced at the world's price times the rate, so local
inflation *is* the rate's rise and the loop's gain is the imported share of the
basket — close to one. The kick was the founding: $3bn of the endowment spent in
decade 1, a current-account deficit of 37% of GDP. It stopped when the rate was
so weak that exports were twenty-five times imports, and then ran backwards for
forty years. **Reproduced with a probe** (`FoundingProbe`, kept outside the
tree): the playtest's own founding holds the index within ±12% for thirty years;
one heavy month-1 order (~$2.8bn: a coal plant, a water plant, a hospital, a
university, schools, 200 houses) gives six years of 10–17% inflation and **prices
doubled for good** (index 2.18 at month 144, 2.37 at 360); twice that, borrowed,
sends the treasury overdrawn at month 120 and the city into 9–15% inflation
*every year thereafter* — rate 58, index 42 by month 480. **Jerus's decision:**
strike the drift on domestic prices (wages, rent, local goods) so the rate's
pass-through into import prices cannot feed itself; damp it; or leave the loop
and lower the fence to something a player survives. A `FoundingCheck` on the
middle case would be the harness. *This is the one that hits a real player's
first hour, and slot 3 is the evidence that a real player founds heavily.*

**THE STUDENTS ORPHAN THEIR CHILDREN.** Jerus built universities around month
2,300; students 26,752 → 96,045; orphans 7,601 → 35,221; and **five to seven
thousand children a decade die as orphans — 83–91% of every child death in the
city since decade 22**, against a quarter before. It is the laid-off-parent
mechanism one door along: a full-time student is a household of their own
(`StudentHousehold`), out of the families, and the children of an adult who
enrols have nobody. Orphans get no care, eat nothing, and die at the no-care
rates — babies at 8% a year against 0.017% fully served; the under-five death
rate, at Canada's 0.3–0.6% of births in decades 14–19, is back at 2.2%. **The
fix is the same as for the laid-off parent** — a household that keeps an adult
who has left work for study or EI beside the family that depends on them — and
more urgent, because a player causes this one by doing the right thing.
*Written up as a decision on 2026-09-15 — `the-parent-who-went-to-school.md`,
three shapes with three money models, and the measurement that frees it from the
senior split's batch.*

**THE BUST OF DECADE 22 WAS THE ORE, AND IT TOOK MANUFACTURING WITH IT.** Posts
511,318 → 468,828, exports $182bn → $134bn a decade, the bank wrote off **$48bn
in three decades** against $6bn in the eighteen before. The share registers:
Mining's fair value 324 → 87, Manufacturing 379 → 46 and never back, Heavy
Industry ×14. The chain `manufacturing.md` predicted — a fabricator is 61%
steel; with the local ore gone the mills price on imported scrap at the ceiling,
the fabricators lose their margin and shed, the bank eats the loans. *Inferred
from share values, because the book has no sector block — see below.*

**WHAT ELSE THE FILE SAYS**, each in the write-up: the late game is the land
(90% used, posts pinned at 511,574 for two decades, zero arrivals for sixty years
because the city is a quarter above its own migration target) and **the birth
rate** — 26–28 per thousand at full childcare is Niger's, not Canada's 10, and it
is why the city exports 200,000 people a decade; real GDP per head is 19% below
its decade-10 peak; **the bank gathers 1.4% of the city's savings** — deposits
are exactly `branches × 250,000` in twenty-four decades of twenty-eight, because
`wantsBranch()` builds on lending strain and the book is $14bn against $55bn of
capacity; **the current account is $989bn a decade against a trade balance of
$131bn**, so about $4tn sits abroad earning 2.6 times GDP a year in coupons, and
the file never shows the stock; police coverage fell 0.90 → 0.61 as the city
outgrew its stations; **the crime table counts two adults in five as doubled up**
in a city with 0.98 homes a household — either the stock is the wrong shape for
its families or `FamilyModel.doubledUpShare()` is high, and one look at the save
would say; Real Estate and Materials are the two companies whose founding share
never made money (0.29 and 0.34 against 1.00 issued); 4,138 unburied at month
1,680; `households:SINGLE_ADULT` is zero in every decade; the treasury spent
$78bn of its $92bn between decades 22 and 27, mostly on capital the surplus line
does not show; and the minimum wage was cut 3.46 → 2.755 in decades 24–25 by
hand.

**STILL OPEN, FROM THIS BATCH — Jerus's, in order:**

- **The founding loop** — core-inflation drift, a damped drift, or a lower
  fence. Design, not a bug.
- ~~**The student-parent**~~ — **DONE 2026-09-15**, unbatched from the split
  (which does not move the orphan baseline) and shipped the same day. Worst
  orphan month 2,093.7 -> 12.4; child deaths over a seed 55,546 -> 47,503. See
  the top entry.
- **The birth rate** — whether 26–30 per thousand at full childcare is the
  calibration meant.
- **The crowding count** — `getDoubledUpFamilies()` on slot 3, one look.
- **The book's sector block** — posts, output, cash, debt and buildings per
  sector; the savings abroad and the carry stock; household debt; childcare and
  senior-care coverage. The pool is done.
- **The year book's first thirty rows, or its WITHIN and WHAT HAPPENED**, for
  slot 3 — the paste carried the decade table only.

*The 2026-09-15 unemployment batch touched **none of the four decisions** above
— it was the chart and the harness, not the model. **The student-parent is the
one to act on next**: it is player-caused, it is a defect rather than a
calibration, and the household fix is already designed for the laid-off-parent
case.*

**Rules earned:** *A derived column is a claim about the model and has to be
struck the way the model strikes it — the People screen's rate, not a
reconstruction from two other columns. A note on a column is read by people who
cannot check it; get the sign right. An index that rises with a rate that rises
with the index is a loop whether or not anybody drew it.*

### THE YEAR BOOK — 2026-09-14, SHIPPED

**A RUN CAN NOW BE READ WITHOUT THE GAME.** Two plain-text files written out of
the history — `year-book.txt`, one row a year, and `decade-book.txt`, one row a
decade — in `%APPDATA%\CityBuilderSim\` beside `log.txt`. One button on the
Reports screen writes both and prints the paths it wrote them to.

**EVERY SERIES DECLARES HOW IT FOLDS, AND A SERIES THAT DOES NOT IS CAUGHT.**
`YearBook.rules()` gives each series one of three rules — **FLOW** (the months
added), **LEVEL** (the row's last month), **RATE** (the months averaged) — and a
series with no rule is still shown, marked, and **fails `YearBookCheck`**.
Putting a series in the history without saying what a year of it means is a test
failure now rather than a wrong number in a file somebody reads.

**AN AVERAGE HIDES THE MONTH THAT MATTERED**, so every rate column carries its
worst and its best single month beside the mean, and the file ends with **WHAT
HAPPENED** — an episode list rather than a table: peak population and the deepest
drawdown, the price index's water-marks, the currency's furthest point from
parity, the months the bank was under water or had no branch, shortages, land at
99%, unhoused and unburied months, the months past 20% unemployment and 10%
sickness, the months overdrawn, and the people caught with no cell to put them
in.

**SHIPPED:** new `YearBook.java` and `YearBookCheck.java`; `GameFiles` gained
`yearBookFile()` and `decadeBookFile()`; `Game.writeBooks()`; one button in
`UserInterface`. **50/50 harnesses green**, **277 assertions** in the new one,
all six files verified byte-for-byte.

**MEASURED ON SLOT 3** — 193 years, 1,003,081 people: the year book is **261,452
bytes** (~65k tokens) and the decade book **43,003 bytes** (~11k tokens). Cross-
checked cell by cell against an independent Python recomputation of the same
history for years 50, 120 and 190.

**STILL OPEN, FROM THIS BATCH:**

- ~~**SLOT 3'S EARLY YEARS LOOK LIKE A UNIT PROBLEM, NOT AN ECONOMY.** The episode
  list reports a price index of **227.3 at month 200**, a currency **100x from
  parity at month 184**, and the bank's worst write-off month (**$1e7**) at month
  200 — all three inside eighteen months of the city's second decade. That is the
  shape of a denomination reform reaching some history series and not others:
  `redenominate()` redraws the history in the new unit, and whatever it misses
  keeps the old one. **Nothing should be concluded from slot 3's early years
  until this is understood** — including anything the books say about them.~~
  **Answered 2026-09-15 — not a unit problem.** A real currency collapse to the
  rate's ceiling, the relative-PPP drift feeding on the import prices it sets,
  reproduced from a heavy founding. See the entry above and `reading-slot-3.md`.
  *And the three figures were read with the currency note backwards: 100 is a
  hundredfold FALL.*
- **THE REPORTS BUTTON HAS NOT BEEN SEEN ON SCREEN.** Written and compiled, never
  looked at. First thing on a rebuild: open Reports, scroll to *"Send this run to
  somebody"*, press it, and check the path it prints. *And while that screen is
  open, the two new traces from 2026-09-15 are waiting on the same look — top
  entry.*
- **A five-year resolution between the two is a one-line change**, if the year
  book turns out too big and the decade book too coarse.

**Rules earned:** *A series that does not say how it folds cannot be summed, and
a summary that guesses is worse than no summary. An average hides the month that
mattered — carry the worst and the best beside it. A file written for somebody
else to read is not shipped until somebody has pressed the button that writes
it.*

### THE DOCUMENTATION CATCHES UP — 2026-09-14, SHIPPED as 0.5.15

**Both documents that describe this game to somebody who has not read the code
were still describing 0.4.4 and save format 21** — seven sectors, a month that
arrives when you click a button. Eleven builds of work had gone past them.

**README.md REWRITTEN for 0.5.15 / save format 26:** ten sectors not seven, **157
files**, **55 buildings**, **48 harnesses plus the playtest** (49 in one command,
~108s), and the manual link repointed at the current artifact URL. Two of the
additions are rules rather than facts — **"a fourth rule: never move a harness's
premise to let a change through"**, beside the three that were already there, and
a note that **the month now arrives on a clock**, so every panel must hold its own
scroll position rather than assuming a repaint only happens when the player asked
for one.

**THE MANUAL REPUBLISHED at 0.5.15 (version 5).** New material: the three newest
sectors and what each of them changed; the farmland relief dial; the taxes area
redesign — five pages, one staged proposal, the quarter-point ladder; the
cost-of-funds floor and the measurement that closed the "real intermediary" idea;
the price index's water-marks; the clock, the speed ladder and the
summary-as-problem-list; the goods table grown to thirteen goods; four new open
questions.

**`Build EXE.bat` WAS MATCHING A COMMENT.** Its findstr pattern was `"String
VERSION"`, which also matches a comment line in `GameVersion.java`, and it
survived only because that line happens to carry no equals sign — the parse took
the right one by luck. `"final String VERSION ="` now.

**THE SAVE-FORMAT CHANGELOG HAD STOPPED BEING WRITTEN.** `GameVersion.java` was
missing **25** (the bank's cost of funds; the last-month array widened four slots
to six) and **26** (the price index's water-marks) — both shipped, neither
recorded. Written. *27 (the sixth age band) was written with the split on
2026-09-15.*

**AND THE ASSEMBLY SOURCE HAD ROTTED WHERE NOBODY WAS LOOKING.** The local
`.part` files the HTML manual was assembled from **had silently diverged from the
published artifact**: they were missing the health-programme comparison table
entirely and carried older wording for three open questions. **Assembling from
them would have deleted published content** — a straight regression, published,
with nothing anywhere to say it had happened. **The published page is the only
source now.** Always read the live artifact and merge onto it before
republishing.

**STILL OPEN, FROM THIS BATCH:**

- **The artifact's share pin still points at version 1.** Anybody holding the
  share link is reading the **0.4.4** manual until the pin is moved. The README
  links the current artifact; the share link does not follow it.
- **`Game.isPrivateInvestmentLandLocked()` NEVER CLEARS.** True at month 12 with
  **1,974,000 sq ft free**, and still true at month 300. `landBlockedSectors` is
  never emptied, so the "nowhere to build" inbox notice is very likely firing on
  cities with plenty of ground. The banner in section 1 was the fix for one
  instance of a class; this is the banner itself gone wrong.
- **FOUR ANOMALIES READ OFF A REAL SCREEN** — slot 3, month 2305, 1,003,081
  people — and none of them fixed:
  - **households with $201,363 put by that are $390 a month short and have
    defaulted.** The draw-down path may not be wired for that household kind;
    every other cell sells its paper and its shares before it borrows.
  - **prisoners carrying an unfrozen $70 student loan.** A prisoner's debts are
    supposed to be frozen; the student loan is a fourth ledger and was missed.
  - **orphan bands three orders of magnitude apart.**
  - **`-$0` printing for any debt-free household.**
- **POPULATION DRAWDOWNS — unchanged and still undiagnosed.** Nine of sixteen
  seeds lose more than half their people, median worst 57%, deepest 77%, present
  in the control build too, and **every such run reports "Nothing. Every month
  passed the audit."** The audit has no measure of a city shrinking. See the
  entry below.

**PARKED, AND NOT DONE BY THIS BATCH:**

- **The base-period price-index fix is built and undeployed.** `DeathRecordCheck`
  is red on it with a **directional flip in the orphan count** — 60→73 as shipped
  against 59→46 rebased. A rebasing that changes which way a count of orphans
  moves is not a rebasing; it is not deployed until that is understood.
- **The bank button that vanished once has never been reproduced.** One sighting,
  no repeat, nothing written down but this line.

**Rules earned:** *A document assembled from local parts has two sources and one
of them is wrong — read the published page and merge onto it. A pattern that
matches a comment passes by luck. A changelog nobody writes is a changelog that
stops being evidence.*

### THE CLOCK — 2026-09-14, SHIPPED as 0.5.0, see `the-clock.md`

**Jerus, after a hand-played run that hit 700,000 people:** *"instead of being
next month, make it so that its just play/pause, aka every 5 seconds is a month,
and you can increase it to 10x speed or 0.1x speed or pause, and have it show
days so that you know whats happening even tho everything still only updates
monthly"*.

**THE FIRST CONTINUOUS THING IN THE GAME.** Every other control is a click that
makes something happen and stops; this one runs on its own, so for the first
time the simulation moves while the player is reading a screen.

**THE MONTH IS STILL THE ONLY UNIT.** The clock accumulates a *fraction* of a
month and calls the same `nextMonth()` the button called. Days are drawn from
that fraction and read by nothing that decides anything.

**MEASURED BEFORE BUILDING, NOT ASSUMED.** A month costs a median of **0.5–0.8ms
in a city of 100–200,000** and 1.5ms in a young one, against a 16ms frame. At 10x
the simulation is under **one percent of the frame budget**, so it runs on the FX
thread and a background thread would have been complexity bought for nothing.

**SHIPPED:** `SECONDS_PER_MONTH = 5.0` with the ladder
`{0.1, 0.25, 0.5, 1, 2, 5, 10}`; a slider that SNAPS to the stops (Jerus: *"a
sticky ladder"*) rather than a free drag, because 7.3x is not a speed anybody
meant to pick; **play/pause only**, with "advance one month" and "simulate
several months" both removed; SPACE as play/pause, consumed in the city and
passed through on the menus; days on the date bar repainted in place every frame
rather than through a redraw; `CityCalendar.daysIn/dayOf/formatDay` with real
month lengths and real leap years; auto-pause reading `Inbox.urgent()`, **OFF by
default** after Jerus played it, with a setting; real GDP on the graphs as a
trailing twelve-month sum of `gdp / priceIndex`; version **0.5.0** in
`GameVersion.VERSION` and `APPVER` in `Build EXE.bat`. 49/49 harnesses,
`CalendarCheck` gained sixteen assertions, deployed and verified byte-for-byte.

**TWO DETAILS WORTH KEEPING:**

- **The inbox already decides what interrupts.** `Inbox.urgent()` is "the newest
  unread notice whose condition still holds" and is null most of the time by
  design, so auto-pause reads it rather than forming a second opinion about
  severity that would drift away from the first.
- **A stalled frame is not elapsed game time.** Dragging the window or a long
  collection can hand the timer a `dt` of several seconds, which at 10x would
  silently run a year. A quarter of a second is the most any single frame is
  allowed to be worth.

**THE STATE OF THE CITY WHEN THIS WAS ASKED FOR**, worth recording because
several things were true at once for the first time. Jerus, on a hand-played run
after the cost-of-funds floor:

> *"i hit 700k population lol, those farms, manufacturing, services really make a
> difference, exchange rate was stable somehow, inflation stablizied and was very
> slowly rising, no where real rates but atleast the long run was a tad positive,
> not a tad negative like before. also bank failed like a few times, but this time
> it was alot better than before and stayed upright, bank is good now."*

**A POSITIVE LONG-RUN PRICE DRIFT IS NEW** — every measured run in this project's
history has settled mildly deflationary, so `why-there-is-no-inflation.md` has
moved for the first time and is still open.

**LEFT BEHIND DELIBERATELY:** `showSimulateMonthsMenu()` is orphaned — nothing
opens it, though the screen router would still restore a save taken while it was
on screen. Roughly 270 lines of UI. Not removed: the ask was about the button
row, and deleting a screen is its own change. Worth a decision later.

**Rules earned:** *Measure the frame budget before choosing a threading model. A
test that contradicts itself is still the test working* — `CalendarCheck`'s first
draft asserted February has 28 days on month 2, which is February 2000, a leap
year; it failed on the first run and was right to. *A default that argues with
the player loses* — auto-pause shipped on for one afternoon.

### THE CITIES THAT EMPTY OUT — 2026-09-14, PART SHIPPED, see `the-cities-that-empty-out.md`

**Jerus, on the entry below:** *"Your showing end, not path."* Every price-index
figure in this project — mine included — is the value at month 4,000, and the run
output records the index nowhere else. The level does not sit still.

**MEASURED, AND THE ENDPOINTS WERE HIDING ALMOST EVERYTHING.** `PriceStat`, a
local instrument sampling the index every month, against the control seeds that
FINISH between 0.835 and 1.258:

```
CONTROL (shipped)        min     median    max
  endpoint              0.835     0.982   1.258
  trough                0.686     0.784   0.955
  PEAK                  1.169     1.618   2.036
  peak/trough            1.40      2.08    2.52
  worst 120-mo swing     +24%      +66%   +123%
```

Seed 4 ends at 1.178 and peaked at **2.036** in month 2,086. A seed that finishes
near founding prices routinely spent a century near double them.

**THIS RETIRES THE "DISPERSION HALVED" CLAIM** in `nothing-borrows-below-cost.md`.
Endpoint dispersion did halve; the sustained level, sampled every 200 months, is
nearly identical between builds — control median band 0.93–1.04, new 0.94–1.01.
Where month 4,000 lands is close to a draw from a ±60% swing.

**THE 3.78 OUTLIER WAS NOT A REGRESSION.** Seed 12 re-run at ten-month
resolution: the city lost **77% of its population in 120 months** (61,776 →
14,138), and only then did rent go ×6.63 and food ×2.18. Note the order, because
it rules out the obvious story — rent **fell** through the first half of the
emptying and began climbing only after the population had bottomed, then kept
climbing for seventy months while the population was flat or recovering, with
`stillUnplaced` **zero throughout**. Not a shortage, and not a simple
fewer-tenants-higher-rent spiral.

**THE THING THAT WAS ACTUALLY SITTING THERE:** population drawdowns past 50% in
**9/16 control seeds and 8/16 new seeds**, median worst 57% and 49%, worst 77%
and 76%. Pre-existing in both builds and **never once diagnosed**. Collapse depth
explains only part of the price peak (r = +0.34 control, +0.47 new) — control
seed 4 had a **0%** drawdown and the control's highest peak.

> **Every one of those runs reports "Nothing. Every month passed the audit and
> every reload matched."** A city can lose three quarters of its people and the
> harness has nothing to say about it.

**SHIPPED — THE INSTRUMENT, AND THE CITY KEEPS ITS OWN MARKS.** `PriceIndex` now
keeps `peak`/`peakMonth` and `trough`/`troughMonth`, **carried in the save**
(older saves open both marks on today's level rather than inventing a flat
history they never kept), plus `swing()`. The checkpoint line gained `px 0.91
(0.65-3.78)`; the end-of-run report gained the level's range with its months and
the swing; the policy screen shows dearest, cheapest and the swing once the marks
have parted, with a note past 2× saying that wages, rents and every debt were
struck against those levels as they passed. `MonetaryCheck` gained eleven
assertions. **SAVE FORMAT 25 → 26.** 49/49 harnesses, sixteen seeds, deployed and
verified byte-for-byte. **Eight of sixteen seeds have a peak more than 1.5× their
endpoint.**

**STILL OPEN, AND IT IS A BATCH OF ITS OWN:**

- **Why do cities empty out?** 9/16 seeds, up to 77%, never once diagnosed.
  Nothing in the model has been asked what drives the exodus.
- **Why does rent climb ×6.6 seventy months after the population stops falling,
  with nobody unhoused?** Points at required-return pricing against a shed
  building stock — see `a-firm-that-cannot-pay-sheds-plant.md` and
  `housing-is-two-markets.md` — but it is a guess until measured.
- **The audit should have a drawdown finding**, the way it has a hunger finding
  and a homelessness one.

**CAVEAT FOR EVERYTHING PREVIOUSLY MEASURED:** the project's standing statistics,
`simulation-findings.md` included, are month-4,000 snapshots of runs that
routinely halve and recover.

**Rules earned:** *An endpoint is a sample, not a summary. A finding that looks
like your change is worth ten minutes before it is worth a fix. A harness that
reports nothing is not evidence that nothing happened — it is evidence about the
harness. A guard that runs after the damage is not a guard* — an inline edit that
bypassed `jedit.py` read the file in Python's text mode, whose universal-newline
translation made the script's own `'\r\n' in s` CRLF guard **False on a CRLF
file**; caught by the deploy's line-ending check.

### NOTHING BORROWS BELOW COST — 2026-09-14, SHIPPED, see `nothing-borrows-below-cost.md`

**Jerus, on the entry below:** *"not but even prior to that, it kept on failing,
even with zero city debt, i think we should modify the bank to let it be
profitable and dont do unprofitable stuff."* He was right. The bond was the
smaller half.

**MEASURED FIRST, and that is the only reason this batch found anything.**
`BankStat` — a local instrument hooked into `LongPlaytest.run()` that totals the
bank's income statement month by month and attributes each loss month to the one
line that alone would have saved it. Two 4,000-month seeds with **zero city
debt**: net interest margin **1.70%** and **0.71%** a year against a real bank's
3–4%; **44% and 58% of ALL months at a loss**; 1,754 and 2,306 of those with no
write-off at all. Attribution: **funding caused 1,529 of 1,774 and 1,832 of
2,337 loss months** — 86% and 78%; payroll caused 78 and 362; write-offs caused
6 and 4. The 2026-09-10 fix worked — payroll is 3–5% of income now — and *what
replaced it as the cause had never been measured.*

**FINDING 1: THE BANK LENT AT THE RISK-FREE RATE AND FUNDED ITSELF ABOVE IT.**
`lendingRate() = riskFree + ratePremium()`, and `ratePremium()` is **zero** below
`EASY_STRAIN`, while `fundingRate = riskFree + FUNDING_SPREAD + FUNDING_STRETCH *
reach`. A comfortable bank lent two points below its own cost of money **by
construction**. `BusinessDebtManager.priceSector()` did the same thing in its own
words — `riskFreeRate + spread`, where the spread prices the BORROWER and nothing
anywhere prices the LENDER's funding. `fundingRate()` was read in exactly one
place outside `Bank.java`: the screen that prints it.

**FINDING 2: `CITY_DISCOUNT` WAS THE SAME BUG WITH A DIFFERENT NAME.** Two points
UNDER the policy rate, against a bank funding itself two points OVER it — a
guaranteed **four-point loss on every dollar of city debt**, and `refreshBank()`
gives the bank no say in how much of it to hold.

**FINDING 3, RECORDED AND NOT FIXED: THE SAVERS ARE PAID BEFORE THE STAFF.** The
deposit payout is capped at `interestEarned - fundingCost`; payroll, upkeep and
write-offs all sit outside that cap. Deposit interest ran **40–45% of income** in
every seed. Left alone deliberately — it stops mattering the moment the margin is
positive.

**SHIPPED: ONE RULE IN THREE PLACES** — nothing borrows below what the money
costs the lender. New `Bank.MIN_MARGIN = .01` and `Bank.marginalCostOfFunds()`,
floored into `BusinessDebtManager.priceSector()`, `DebtManager.floorRate()` and
`Bank.lendingRate()`. `CITY_DISCOUNT` survives as a discount against the POLICY
rate, which is the only thing it was ever named for. **`FULL_STRESS_MULTIPLE`
stays at 150 and `MAX_SPREAD_PER_MEASURE` at five points** — the August "gentler
financing" calibration is untouched. *The curve was never the problem; the floor
under it was.*

**TWO ITERATIONS WORTH KEEPING, because the first answer was wrong both times:**

- **Marginal cost of funds is the better question and the worse measurement.**
  What the next dollar costs is a step function in practice: the floor flipped
  **4.9 / 8.0 / 11.0 / 1.9 / 4.9 / 5.4 / 9.2 / 12.9 / 13.6%** across twelve
  consecutive months of `BankCheck`'s fixture. Blended across both tranches it
  has no cliff, and blended is what shipped.
- **The live path and the load path priced a month apart.** `BankCheck`'s
  "...and so does what the bank is charging for money" failed at **0.0130 against
  0.0129** — a city reloaded from its own autosave quoting 1.30% for a loan it
  had quoted 1.29% for a second earlier. Fixed by striking the cost of funds
  **once, at `closeMonth()`**, carrying it, and pushing it out of one helper to
  all four call sites. `CreditCheck` and `ForeignCheck` were downstream of the
  same moment and went green with it.

**SAVE FORMAT 24 → 25** — `bankLastMonth` widened to six fields (`fundingRate`
and `lastCostOfFunds`). Older saves load with free money for one month and
correct themselves at the first `closeMonth()`.

**MEASURED, sixteen seeds against the shipped build:**

```
                          before              after
  bank failures           5.0 median          0.0 median
  recapitalisation        $3,815,849k         $0
  equity at the end       $6,642,230k         $9,056,426k
  book lent               $6,365,082k         $3,166,258k
  capacity used           49%                 38%
  wholesale funding       24–47% of income    0–8%
  months at a loss        44–58%              3–14%
  lifetime profit         −$39.6bn..+$34bn    +$11.9bn..+$54.1bn
```

**Every seed profitable.** 49/49 harnesses green, deployed and verified
byte-for-byte.

**OPEN, CARRIED FORWARD:** seed 14 lost its clean bill, 15/16 — **38 months of
households with no home** around month 2,793, Real Estate building less because
borrowing costs more. Not patched; stage two was to have relieved it.

**STAGE TWO WAS DESIGNED, MEASURED AND ABANDONED, and that is the better
finding.** The loop is real and nobody had connected its ends: the bank has no
margin → pays its savers nothing → `HouseholdBalance.investAbroad()` sends the
savings abroad in proportion to `worldRate - depositRate` → less to gather → it
funds itself wholesale FROM ABROAD at a spread → no margin. **The city exports
its savings and re-imports them at a markup**, and both halves were already
modelled. But the numbers said do not build it: the bank lends **1.87% of the
deposits already sitting at home** ($171bn deposited against $3.2bn lent, 31–49%
of the capacity it already has), households hold **78% of their wealth abroad**,
the entire corporate sector's cash is **$364k**, and everything all ten sectors
owe is **$723k**, with three of ten owing anything at all. The city's savings are
about **200,000x** what its productive sector wants to borrow, and the firms are
not cash-rich self-funders — *they are small*. Investment borrowing already
exists and works (`Investor.borrow` -> `canFundProject`); it has almost nothing
to finance. So "a real intermediary" cannot mean funding domestic investment.
**Jerus's call: stop at stage one and ship it.**

**THE ROUTE THAT REMAINS**, for a future batch beside
`the-surplus-has-nowhere-to-go.md`: the households bypass the bank entirely and
go abroad themselves at the world's rate, while the bank separately places its
spare cash abroad at `PLACEMENT_RATE` — already 12% of its interest income and up
to 33% in some seeds. **Both halves exist and do not meet.** A bank paying
competitively would intermediate the surplus instead of watching it leave, and it
needs no invented credit demand.

**Rules earned:** *A price nobody compares to a cost is not a price. The
theoretically better question can be the worse measurement. A rate is a flow's
price and obeys the flow's rule — struck at the close, carried, never recomputed
from a different moment. A discount against a policy rate is not a discount
against a cost. Measure before designing the second stage, not after building
it.*

### WHY THE BANK KEEPS FAILING, PART TWO: THE BOND — 2026-09-13, ~~OPEN~~ FIXED 2026-09-14, see `the-bond-that-broke-the-bank.md`

**Read off Jerus's own autosave** — month 354, save format 24, 14,582 people —
because this is a thing the playtest has never once been able to see. **91.6% of
the bank's $19.86bn book is ONE thirty-year city bond**: face $18,200,000k,
issued month 144, 150 months still to run, coupon **0.625% a year**, $9,480.44k
a month. The whole book yields **1.307%/yr** measured; **90.6% of the funding is
wholesale paper at 5.593%/yr** measured. A negative carry of **4.3 points on
$17.4bn**. Month 355, observed: interest **21,663**, funding cost **81,394**,
payroll and upkeep 27,644, trading −3,754, **profit −91,129** — the funding cost
is **3.8x the interest income**. It fails again at month 358, its **nineteenth
failure in 354 months**.

**HOW IT HAPPENED IS ONE MONTH OF ARITHMETIC.** The city's borrowing rate
carries `Bank.ratePremium()`. At month 143 the premium stood at its 18-point cap
and the city was quoted **18.500%**. At month 144 the bank's business book ran
off, strain fell below `EASY_STRAIN` (0.80), the premium went to **0**, and the
quote collapsed to `MIN_RATE` (**0.500%**). The city issued $18.2bn of thirty-
year paper in that one month. And `Game.refreshBank()` sets `cityBook =
debtManager.getAllPrincipal()` — **the bank holds the city's bonds BY
CONSTRUCTION**: no purchase decision, no capacity test, no price it can refuse.
$18.2bn landed on a bank whose capacity was $7.2bn. Its premium has been pinned
at the 18-point cap for **61% of the 353 months on record**. *The bank's one
healthy month is what priced its own thirty-year loss.*

**AND THE QUOTE WAS NOT A BUG.** `quoteRate()` priced the bond correctly, with
the bond itself included in what it priced. **The CURVE is the calibration**:
`FULL_STRESS_MULTIPLE = 150` years of GDP or revenue before a measure maxes out,
so $18.2bn against $7.5bn of annual GDP — **2.4 years** — is 1.6% up the curve
and worth **eight basis points**. The 150 was chosen deliberately, after Jerus
asked for gentler financing. This is its other end.

**COUNTERFACTUALS, against the −91,129 baseline:** the bond repriced to the
city's fair rate today (~1.5%) → about **−78,000**; funding free → about
**−9,700**; the bond gone entirely → about **−15,600**; **the bond at 8% with
everything else untouched → +24,300, and the bank is profitable.** The loss is
the coupon, not the cost base.

**A SECOND, SMALLER PROBLEM UNDERNEATH IT.** Seven branches gather $1.79bn of a
$37.4bn deposit pool — **4.8%** — where the city's savings would support about
**150** at `DEPOSITS_PER_BRANCH = 250,000`. It will not build an eighth, for two
reasons: `branchWouldPayForItself()` reads last month's profit and **a bank
losing money never passes it**, so the test that fixed the 1,049-branch runaway
now keeps it starved; and each branch needs 4 `UNIV_FINANCE` and 1 `UNIV_LAW`
licences against **63.7 university graduates in the whole city**. *The bank is
too small for its city's savings and too big for its own loan book at the same
time.*

**WHY IT WAS NEVER CAUGHT, and this is the part worth keeping.** The
4,000-month playtest bot **never issues city debt** — "borrowed abroad: US$0k
owed, $0k at home". Its bank ends at 17.7% capital, 10 branches, **0.0 points of
premium**, and fails 2–10 times in 4,000 months across sixteen seeds. Jerus's
city fails 18 times in 354 months, **forty times the rate**. So the standing item
is not "the bank always fails"; it is **the bank fails as soon as the city
borrows, and only then**. Every environment Jerus plays has the bond; no
environment the harness has ever measured does. It is also why the 2026-09-10
investigation fixed four real bugs and left this one standing — *every number in
it came from the playtest.*

~~**OPEN, NOT FIXED. Four candidates, none of them decided:**~~ **THE SPECIFIC
PROBLEM IS FIXED, 2026-09-14**, by the cost-of-funds floor in the entry above.
The city can no longer borrow below what the bank pays for its money —
`DebtManager.floorRate()` is floored at `Bank.marginalCostOfFunds()` plus
`MIN_MARGIN` — so **a 0.625% coupon could not be struck today**: the one
comfortable month of 144 would have quoted the bank's own funding cost instead of
collapsing to `MIN_RATE`. *The four candidates below stand as written. The batch
did half of one of them and none of the other three — the bond is priced now,
but everything that made it possible to issue is still there.*

- **Recalibrate `FULL_STRESS_MULTIPLE`** so the credit curve has bite in the 1–5
  years of GDP range where real sovereigns are actually priced.
  *NOT DONE, deliberately — 150 and `MAX_SPREAD_PER_MEASURE`'s five points are
  exactly where the August calibration left them. The floor sits UNDER the
  curve rather than replacing it, so a city 2.4 years of GDP into debt is still
  worth eight basis points of premium and always was.*
- **Give the bank a right of refusal** — a capacity test on city paper rather
  than conscripting it, with an underwriting spread over its own funding cost.
  *HALF DONE — the underwriting spread over its own funding cost now exists and
  binds. There is still no capacity test and `refreshBank()` still conscripts,
  so $18.2bn can still land on a $7.2bn bank; it just cannot land cheap.*
- **Let a long bond's coupon reset**, or price it off a term curve rather than
  off the one-month spot quote. Same batch as the term structure and the
  operating central bank in section 3.
  *NOT DONE. A fixed coupon is still a thirty-year bet on one month's
  arithmetic; the floor only raises the worst bet that month can make.*
- **Fix the branch trap** so a bank can reach its city's deposits:
  `branchWouldPayForItself()` needs to see the deposits a branch would GATHER,
  not just last month's loss.
  *NOT DONE — though loosened by accident: months at a loss fall from 44–58% to
  3–14%, so the test a losing bank could never pass is now one it passes most
  months. The trap is still there for any bank that starts losing.*

**Rules earned:** *A harness that never pulls a lever cannot price it. An asset
nobody chose to buy is not an asset, it is a conscription. A premium that prices
strain will be zero exactly when it is most dangerous. A fixed coupon is a
thirty-year bet on one month's arithmetic.*

### FARMS — shipped 2026-09-13, see `farms.md`

**The tenth sector, and the last open end in the goods economy.** Jerus: "lets
add farms, which use land a lot, but are labour and energy cheap, and give the
textile or whatever its called the goods to manufacture food, and yes that means
imports also exist if needed, and yes that means that sector is going to get
squeezed so it also needs more breathing room." Every clause of that is in the
build, including the last one, which was the hardest.

**THE MILLS MADE FOOD OUT OF NOTHING.** No input line, no supplier, nothing on
the cost side but wages and the lights — which is why the pair of them ran a
72-77% operating margin against every other plant's 28-31%, and why the Food
Processing Plant returned **6.2% of its build cost a month**, the most profitable
building in the game by a factor of two, on a raw material that was free because
there was none. The chain is four links deep now: **ground -> crops -> food ->
groceries**.

**Sixteen paired seeds, against the build that shipped the same morning:**

```
                median pop      min       max   index   currency   land   findings
  Manufacturing    127,651   96,993   151,254   0.975     0.778     88%     3/16
  + FARMS          118,354   96,041   165,302   0.982     0.800     88%     0/16
```

**Zero findings in sixteen of sixteen**, against the morning build's three —
adding a real cost to the mills made the audit CLEANER, because the founding city
now grows some of its own food. Agriculture: zero restructures in all sixteen.
Self-sufficiency median 28%, range 16-87%. The city is 7% smaller, which is the
right price for a real cost, and the price index and currency both come out
marginally better.

**THE BREATHING ROOM CAME OUT OF THE UNITS, NOT A THUMB ON THE SCALE.** At 6.5
units of food to the tonne the crop bill was 43.6% of what a mill sold at the
price food actually clears at over a long run - and a mill's payroll is 36.7% at
that price, because wages are domestic and rise while the food price is the
world's and falls. Eighty percent gone before the lights: **fourteen mill
bankruptcies in 333 years, and the farms died with them** because the only
customer had gone. **The fix could not be the price** - the crop price is the
farms' revenue and the mills' cost at the same time, so cutting it to save one
kills the other. It was the QUANTITY: 1.85 tonnes of farm output a person a year
is the top of the real range and 1.26 is the middle of it. Moving it leaves the
farms untouched and takes the mills' crop bill to 29.7%, which is what a real
processor pays.

**WHAT A FARM SPENDS IS GROUND.** Three people on fourteen acres and seventeen on
three and a half - a real NS mixed farm is one or two people, and a hectare of
Dutch glass is one per quarter acre. Labour is a seventh of what a field sells
and power a five-hundredth. The ground is charged twice: at the going rate when
the lot is bought, and every month as tax on what it would fetch. **So the sector
is a clock**: fields at $1.30 a square foot, nothing at $60. Measured at month
4,002 with ground at $66 - **0 mixed, 0 grain, 10 greenhouses**, and the two
Mixed Farms the city was founded with still standing because they were bought
when the land was free. Glass is twenty times a field's yield off an acre paid
for in electricity and people, and it is the sector's whole late game.

**THE DIAL, Jerus's call:** farmland assessed at USE value rather than
development value - Ontario's Farm Property Class, Nova Scotia's resource rate,
the Williamson Act. Measured on six grain farms standing on $112m of ground:
**$151,496 a month taxed against $11,295 relieved**. Defaults to full relief,
because that is the real default almost everywhere. On the Policy screen under
the property tax, stating its cost in dollars forgone rather than in principle.

**TWO RULES THIS SECTOR NEEDED THAT NO OTHER DOES.** *Nobody breaks ground on a
field while somebody is sleeping outside* - the fields plan off the crop market
and the houses off the jobs and neither can see the other, and in a young city
the fields win that race: seed 3 ran **sixty-four months with households who had
nowhere at all, months 122 to 2,777**, four with the fields held out. And
*nobody finances a farm on a good year* - the first gate read today's crop price,
so the city built hard while crops were dear, hit 100% self-sufficiency by month
618, crashed the price onto its floor by doing so, and could not service the land
it had borrowed against at the price it had created. Underwritten at the FLOOR
instead, fields stop at about $10 a square foot and glass clears the same test at
$60.

**AND THE BRAKE FOR THAT ALREADY EXISTED WITH NOBODY CALLING IT.**
`BusinessInvestment.servicesItsOwnDebt()` has been in the file since the distress
batch - "if the new capacity cannot out-earn the interest on the money that built
it, by a margin, the business declines the project even though the lender would
fund it" - and a search of the tree found **zero callers**. A fully-written brake,
never once applied. Agriculture is its first caller. **See the open items below.**

**Still worth doing:** a Grain Farm is never built in sixteen seeds - it is the
same trade as a Mixed Farm at four times the size, so it loses on lumpiness early
and on land late; either differentiate it (real grain farming is far lower yield
and labour per acre than mixed) or drop to two rungs. And **seed 7 ended with a
price index of 1.258 and a currency past parity at 1.099** - the first seed in
this project's history to finish with a price level meaningfully ABOVE its
founding one. Worth understanding before assuming it is noise.

### MANUFACTURING — shipped 2026-09-13, see `manufacturing.md`

**The ninth sector, and the plateau is gone.** Jerus's "manufacturing is the
other half", and the thing `jobs-beyond-the-stalemate.md` asked for in those
words: an export sector designed to absorb labour, unlike steel which is designed
around a deposit. It buys steel — from the city's mills if there are any, from
the world if there are not — and ships fabricated steel and machinery.

**Sixteen paired seeds, 333 years each:**

```
              median pop      min       max   takeoffs   index   currency   land
  plain           16,834   13,764   119,123     6/16     0.799     0.603     58%
  SHIPPED        127,651   96,993   151,254    16/16     0.975     0.778     88%
```

**TWO BRAKES, NOT ONE, AND THAT IS THE DESIGN.** Fabrication is 61% steel and 20%
wages, so **the steel price** decides it — thin on imported steel at the ceiling,
an ordinary business on local steel mid-band, a good one on steel at the mills'
export floor. The Machine Works is 21% steel and half wages, so **the wage bill
and the currency** decide it, exactly as they decide a contact centre, but at a
machinist's wage. It is the rung ABOVE business services rather than a competitor
to it, and a city priced out of call centres can still build one.

**AND STEEL HAS A DOMESTIC CUSTOMER FOR THE FIRST TIME.** `Good.STEEL` said "not
importable: there is no local buyer to import for" for two builds, which also
made it the one good with no ceiling from the world. It has one now — 1.284, the
US hot-rolled band — and a city with no fabricator is unchanged, because the
ceiling binds only when somebody bids. Ore to steel to a beam that leaves is the
deepest chain in the game, and the first reason to put three things near each
other. The playtest built **thirty-seven steel foundries** on seed 1.

**THE FINDING, and it is bigger than the dial.** Four calibrations of the plants'
build cost were measured over sixteen seeds each. **The price does not decide
whether a city makes it, only how big it gets** — every calibration takes off
16/16, and making the plants dearer only COMPRESSES the outcome (at 3× annual
revenue every one of sixteen cities lands inside a 23,000-person band). Once a
city can sell labour-embodied goods abroad, the plateau has no bite.

**Jerus's two calls:** ship the strong setting (1× annual revenue), because that
is where the economy reads best — index 0.967, currency 0.780 — and **gate it on
the ground** rather than the price, so the city chooses between a factory and a
neighbourhood. A fabricator is the honest place for that: a laydown yard at 400
sq ft a tonne makes the two sheds the most ground-hungry buildings in the game
per post (4,167 and 3,556 against a Steel Foundry's 2,368), while the Machine
Works is denser than a mill at 867 and pays for it in power at 4.7 kW a post. The
gate is a price rather than a wall — land ends at 88–91% either way; what changes
is that the ground under the marginal plant costs what a mature city charges.
Median 190,228 → 127,651, ground per person of housing 10.59 → 11.97.

**THE PRICE LEVEL IS NEARLY FIXED AND NOBODY SET OUT TO FIX IT.** 0.799 → 0.975
over 333 years, from a sector built to make jobs — six of sixteen seeds end above
0.95 and two above 1.00. Every previous attempt broke on the constraint that
raising domestic real costs kills the exporters; this works the other way round,
giving the city something worth more an hour to sell. The currency goes 0.603 →
0.778, most of the way to the 0.795 parity the world's 1% implies. **Mechanism
not established — measure it before believing it.**

**WHAT REPLACES THE PLATEAU AS THE OPEN QUESTION: should a player be able to
fail?** Measured here, no price setting restores failure. If it is wanted it has
to come from somewhere else, and it is a design decision rather than a tuning
one.

**Still worth a look:** the Machine Works is the wage-bounded rung and the
playtest advisor never has a labour shortage severe enough to prefer it, so the
design's central claim — that a city priced out of call centres switches to it —
is not yet tested by the ensemble. And seed 1 ends with 117 deposits found and
none worked, so whether Mining keeps up with thirty-seven foundries is open.

### THE WORLD A CITY IS FOUNDED INTO — shipped 2026-09-13, see `the-world-a-city-is-founded-into.md`

**The world was never inflating.** `advanceMonth()` compounds the price level and
then drags it back toward a trend line, and `TREND_INFLATION` is 0.0 — so the
line is flat and the level settles where the two forces balance. At the old 3.33%
mean that solves to **L = 1.835**; measured, 1.841. It reached 1.84 early in every
game and sat there for three centuries, which is why the headline read 3.1% and
realised read 0.0%.

That made the level **a dial with a closed form**: mean 3.3% → 1.84, 2% → 1.37,
1% → 1.15. Measured 1.841 / 1.367 / 1.151, exact.

**Sixteen paired seeds at a 1% mean: the currency improves 16 seeds out of 16**,
0.372 → 0.607. After four interventions that did nothing or did harm, the first
unambiguous win — and a change to a distribution rather than a new mechanism.

It costs city size (population mean ~41,000, takeoffs 9/16 → 6/16) because a
weaker currency makes imported materials dearer. A difficulty increase, not a
defect. **It does not produce inflation** — the index is 0.79 against 0.81. It
fixes the CURRENCY by removing a step the city could never match; everything in
`why-there-is-no-inflation.md` about the domestic price level still stands.

**Shipped:** band centred on the mean and two-sided (`mean ± 3%`, so the world
sometimes deflates), `LOW_BIAS` retired with its reasoning kept, the mean settable
in Settings at **founding only** (Jerus's call — the world a city grew up in is a
fact about that city), carried in the save in an additive slot, and old saves
reading back at `LEGACY_MEAN_INFLATION = 3.33%`.

**One harness was wrong before this and had to change.** `BankCheck` asserted
"pays savers less than it charges borrowers" as `depositRate < sovereign rate` —
not a rate any borrower pays, and it held only while the fixture's bank was
losing money. A bank whose reserves are placed at the world's rate can pay savers
more than it charges and still profit. It now asserts what `chooseDepositRate()`
actually guarantees: never more than it earned.

**STILL OPEN, and it is a design question rather than a bug:** zero rates still
produce no inflation — measured across 16 seeds, the policy rate settles at
0.04-0.47% and fourteen of sixteen cities DEFLATE. The rate cannot reach the
price level while 61% of the basket is pinned at a $300 shelf floor and 39% to
frozen build costs. The four candidates are in the previous entry.

### THE DECOMPOSITION — 2026-09-13, see `why-there-is-no-inflation.md` addenda 2-4

*Three addenda in one day, each overturning the one before it, all driven by
Jerus's questions. The last one is the useful one.*

**Sticky wages do not work.** `COST_OF_LIVING_PASS_THROUGH` 1.0 against 0.8,
sixteen paired seeds: the currency moves 0.372 → 0.380, which is nothing. The
surplus falls 27% and the per-seed figures say why — **the big-city seeds shrank.
Exports fell because there was less city**, not because exporting stopped
paying. Rigidity with transmission was tested too and is worse (currency 0.230),
because import prices fall with the currency whatever wages do.

**THE DECOMPOSITION, which is what this was all missing:**

```
  the world's prices rise to 1.841   ->  parity 1.000 -> 0.543
  the city's prices fall to 0.762    ->  parity 0.543 -> 0.414
```

**About 70% of the appreciation is the WORLD inflating, not the city deflating.**
Every instrument tried so far has been aimed at the 30%. Flooring world inflation
at zero recovers about 9% on its own — real, nowhere near enough.

**AND THE FINDING THAT SPANS EVERYTHING.** Three interventions, sixteen seeds
each — money creation, the wealth drawdown with transmission, sticky wages —
**all three take takeoffs from 9/16 to 3/16.** This city grows by exporting on
margins of 1.1-1.3x over full cost with cheap labour. Anything that raises
domestic real costs kills the exporters that drive the growth.

So **any fix that works by raising domestic costs will cost the player their
city**, and all three attempts so far were exactly that. That is the constraint
every future attempt has to respect.

**WHAT THIS ECONOMY ACTUALLY IS:** a hard-money export economy against an
inflating world. Currency appreciates because it will not inflate, exports are
thin-margin, households hold 32 years of GDP and send four fifths abroad. All
internally consistent; none of it a defect. What the player lacks is **a choice
about the regime**.

**THE OPEN QUESTION IS NOW A DESIGN ONE**, not a bug: what should the player be
able to choose, and what should it cost them — given that it cannot be a tax on
exporters. Candidates, none tested:

- **Leave it.** A permanently strong currency in a surplus economy is not wrong,
  it is Switzerland. Cap the drift so it stops being cosmetic noise and move on.
- **The world.** Less world inflation means less PPP drift and gives the
  domestic instruments something to bite on. Cheapest, partial, measured at 9%.
- **The savings glut.** 32x GDP hoarded and 80% of it abroad is the root of the
  surplus. Biggest change, and the one attempt at it so far killed the takeoff.
- **Move the player's interesting choice somewhere else entirely** and accept the
  export-led city as the game's premise.

### MONEY CREATION — built, measured, NOT SHIPPED, see `money-creation-does-not-ship.md`

Deposit creation, `MoneyAudit.Scope.CREATION`, a reserve-ratio dial, treasury
monetisation, and `MoneyCreationCheck` as the 48th harness. All of it works.
**The anchor is demonstrated against a control**: two cities printing at 90% of
every hole for ten years, one following the Taylor rule and one pinned at the
floor — the rule drove the rate 0% → 25%, and the city that answered made less
money and ended cheaper (index 2.301 against 2.553).

**And sixteen seeds killed it.** Baseline 9/16 takeoffs, median population
70,588. With creation: **3/16 takeoffs, median 14,369**, p about 0.03. The small
mode is identical in both — what changes is how often a city takes off at all.
Eight seeds had said 5/8 against 1/8 and I did not believe it, because this
project wrote down the reason not to in the carry trade. The extra eight seeds
cost twelve minutes and turned a hunch into a number.

Prices barely move (0.810 → 0.774) and the currency not at all, so it is not a
price-level effect. Most likely the bank's balance sheet, which changes shape
completely once lending stops draining cash: `borrowings()` goes to zero, the
funding cost with it, and the bank's idle capital earns a placement every month
for ever. **Not established — that is the open question.**

**The transmission half does not ship either.** Links C and D measured with
creation on: seven of eight seeds deflated to 0.33-0.57 against a baseline of
0.76-0.84, and the eighth ran away to an index of **4,023** with wages up
295,046% and the currency at its ceiling. The anchor stops a PRINTING spiral —
which is what the harness proves — and does not stop a wage-price spiral,
because in ordinary play monetisation is zero and the bank's book is too small
for the rate to reach the money stock.

**WHAT DID SHIP:** `Exchange.MIN_FAIR`, a money constant a currency reform never
reseeded — the second of its family after `Equity.FOUNDING_PRICE`, found the
same day. `deskCanBuy()` divides by `fair[c]`, so a value pinned at an epsilon
rather than its real level makes the desk's room wrong by the ratio of the two.
Deployed alone, 47 harnesses green, and **behaviourally identical** to the tree
it replaces on eight seeds — the floor cannot bind in a city that has never
reformed, which is exactly the point of it.

**WHAT I WOULD DO NEXT — not this.** The chain is coherent on paper and every
piece of it works. What it runs into is that this city's money does not come
from its bank; it comes from a permanent trade surplus, and the policy rate has
no hold on that at all.

**AND THE SURPLUS QUESTION IS NOW ANSWERED** — Jerus asked "shouldn't rates at
0% handle that?", and chasing it found two structural reasons it cannot. See the
addendum to `why-there-is-no-inflation.md`:

- **The world has no demand curve.** Exports clear at `worldExportPrice x
  exchangeRate` and the world absorbs ANY quantity at that price. There is no
  foreign demand term in `GoodsMarket` at all, so a dear currency can never make
  the city's goods uncompetitive — they are always exactly at the world price.
- **The pressure signal is scale-free in the exchange rate.** Exports, imports
  and the financial flows all scale with `fx`, and `pressure` is a ratio of
  them, so it cancels top and bottom. The exchange rate is arithmetically
  incapable of changing the pressure that sets the exchange rate.

So the currency here is not an adjustment mechanism, it is an index — nothing
about it feeds back on the real quantities that make the surplus. **The next
thing to build is a foreign demand curve**, so that a dear currency costs the
city exports and the rate finally has something real to act on. That is a much
smaller change than money creation and it is upstream of everything this batch
failed at.

**~~STILL OPEN~~ — CLOSED 2026-09-13, by the ninth sector.** `Exchange.quote()`
ran the whole share market off `open = bankEquity > 0`, and a bank just wound
down to zero equity reports whatever is left of assets minus liabilities — on a
$145M balance sheet, a few hundred picodollars. Measured at month 125 of
`DenominationCheck`'s long section: **$2.3e-10 of equity in the plain city and
exactly $0 in the reformed one.** One opened its exchange and the other did not,
and a month with no dealer is a month where every till in the city ends somewhere
else. Fixed with `MIN_DEALER_EQUITY` — one share at the founding price, seeded
like `MIN_FAIR`. Two more of the family fell out of the hunt: `OutwardInvestment`'s
`Math.abs(move) < 1e-9` (the fourth unseeded money constant, after `rateHistory`,
`Equity.FOUNDING_PRICE` and `Exchange.MIN_FAIR`) and a relative dead band on
`Exchange`'s buyback line, where a company crossing by a hair called six months of
operating cost home from abroad and then spent nothing. See `manufacturing.md` §7.
*And a fifth, 2026-09-15, in the same file and the same family but a new shape —
`TIED_YIELD`, a dead band on the RANKING rather than on a money quantity, because
the model prices every earnings-valued company to exactly the same yield and the
sort was choosing between copies of one number. See the top entry.*

### WHY THERE IS NO INFLATION — established 2026-09-12, see `why-there-is-no-inflation.md`

The answer to Jerus's playtest question, and it is not what either of us
expected. **The money supply was never the problem — it already grows 78x** over
a run while the price index goes DOWN. Money creation would have been a fifth
broken link in a chain with four already in it:

1. **Wealth never bids.** `want` comes from income alone; savings are only the
   ceiling. The families hold $198M of the $274M stock, 32x annual GDP, and it
   is inert by construction.
2. **The shelf floor is a dead constant** that binds by 50% for 333 years. 61%
   of the price index was a number typed in 2026.
3. **Nothing anywhere prices off a wage.** Retail's cost-plus excludes payroll;
   building cash costs never move. The other 39% of the index was pinned to a
   data file. Wages index to prices at a pass-through of 1.0 and prices index to
   nothing — a one-way link, so the loop that makes inflation *inflation* has
   never existed here.
4. **The policy rate has nothing to grip.** The Taylor rule is correct, weight
   1.5, and the advisor follows it. It advises into the void because
   `bank.lend()` is `cash -= amount`.

**NOT a link, though it looked like one: the bank.** It lends 0.75% of deposits
but that is 24% of GDP — a normal credit ratio — and its branch cap never binds.
The bank is normally sized; the deposits are the anomaly. The planned "make the
bank scale" work was measured and **dropped**.

**DEPLOYED — three pre-existing bugs, 47 harnesses green, byte-for-byte:**

- **`Equity.FOUNDING_PRICE` was a money constant a reform never reseeded.** The
  yardstick every share is split and consolidated against stayed at 1.0 while
  `Exchange.redenominate()` scaled everything divided by it. Same class as
  `rateHistory`.
- **`Game.quoteNote()` had a loop that could not exit.** Its termination
  argument was in its own comment and both halves are wrong once prices move;
  `netProceeds()` rounds to the cent, so a small granule advances nothing. It
  hung a 4,002-month playtest so hard the JVM would not answer a thread dump.
- **`ens.sh` ran five seeds of eight** — `set -e` killing the lane on the first
  seed with a finding.

**NOT DEPLOYED — the transmission work, in the tree, 45 of 47 green.** Retail's
overhead in the shelf price and a wage-indexed floor; `LABOUR_SHARE` and a live
build-cost index (`updateConstructionCost()` finally does what it is named for);
the household wealth drawdown; `Bank` deposit creation with a `createdDeposits`
liability; `MoneyAudit.Scope.CREATION`; a reserve-ratio dial; treasury
monetisation.

**WHY IT IS NOT DEPLOYED — the economy has no nominal anchor.** Fix links 1-3
and the price level does not settle, it runs: two attractors, deflate to 0.4 or
spiral to +700% and take the treasury to 1e39 of debt. That is a loop gain of
one. And a second loop the dead floor had been holding open for the life of the
project — cheaper imports → lower shelf → lower index → lower PPP parity →
stronger currency → cheaper imports. The $300 constant was load-bearing twice.

**WHAT REMAINS, in order:**

- **Finish the anchor.** Deposit creation is written; what makes it an anchor is
  the rate gripping credit growth. Needs: reserve-based funding (with full
  creation the bank never needs wholesale funding, which silently kills
  `fundToCover` — a bank should borrow to hold reserves against created
  deposits, which is also what makes the reserve dial bite), the save format and
  `DataSave` field for `createdDeposits`, and `BankCheck`'s margin fixture
  rewritten (it caused its condition by draining cash, which no longer happens).
- **Then re-tune links C and D against an anchored system.** Everything measured
  so far was measured without one, so none of the constants are settled —
  `RETAIL_MARKUP`, `FLOOR_WAGE_SHARE`, `LABOUR_SHARE`, `WEALTH_DRAWDOWN` are all
  still on system properties for exactly this.
- **`MoneyCreationCheck`**, the 48th: the identity with a creation term, each
  channel, and the save round-trip.
- **Screens**: the money stock and which constraint binds, the reserve dial, the
  monetisation dial with its cost stated in the price level.
- ~~**The open thread.**~~ **Closed 2026-09-13** — see the money-creation entry
  above and `manufacturing.md` §7. It was `open = bankEquity > 0` in
  `Exchange.quote()`, decided by rounding dust on a wound-down bank.

### THE CARRY TRADE — built 2026-09-12, see `the-carry-trade.md`

Phase two, and Jerus's own design: foreigners borrow from the bank, convert to
dollars, and the currency they sell on the way out is the outflow the city never
had. **It works.** $518bn standing on the median seed, peak $775bn, and on one
seed the carry IS the bank's entire book — the borrower it never had. Domestic
first (`headroom()` after `refreshBank()`), inside the audit's window, 47
harnesses green including a new `CarryTradeCheck`. Nine files deployed.

**AND IT DOES NOT MOVE THE CURRENCY.** fx 0.379 → 0.374 across eight seeds.
Not because the outflow failed to arrive — it arrived, and the financial account
covers 79% of the current account. It covered 79% *before* too: the carry-fed
city is a third bigger and exports a third more, so the ratio never moved. The
reason is one line in `ForeignAccounts`:

    parity = parityBase * localLevel / worldLevel;

and the rate mean-reverts to it. World prices rose 84% over the run; the city's
**fell**, to 0.76-0.84 from a founding 1.00. Parity is 0.41-0.46 and the rate
tracks it to three decimal places. Flows set the deviation from parity — a band
of about ±15%, and the carry moved us from −12% to −14% inside it. **Prices set
the level.** No amount of outflow fixes a PPP gap. **What it opened:**

- **THE PRICE BATCH IS NOW THE WHOLE GAME**, and #53 money creation should go
  FIRST, not last. It is the only one of the four that makes the local price
  level *rise* rather than merely stop falling, and the price level is what
  parity is made of. The other three stop the fall; this one is the fall's
  cause. Argued with numbers in the write-up.
- **THE CURRENCY SCREEN WAS MISSING HALF ITS OWN FORMULA.** `pressure()` is
  struck on the current account PLUS the financial account and only the first
  was on screen. A player reading a $1.6M surplus next to a pressure of zero
  had no line anywhere that explained it. Added.
- **`Game.rateHistory[]` WAS NEVER SCALED BY A CURRENCY REFORM.** A reform that
  lops two zeroes left the history unscaled, so `yearlyDepreciation()` reported
  a fake 99% move for twelve months straight into hot money's panic test.
  Pre-existing; the carry trade is only what made it visible. Fixed.
- **`ens.sh` RAN FIVE SEEDS OF EIGHT AND SAID SO IN PASSING.** `LongPlaytest`
  exits non-zero on findings and `set -e` killed the lane. Fourth partial-or-
  stale measurement in two days. Fixed, with the reason in a comment.
- **THE CITY IS BIMODAL** — 13k or 90k+, takeoff or stall. Baseline took off on
  3 seeds of 8, the carry on 5, and further when it did. **That is not a
  result** at n=8 and is written down so it is not mistaken for one.

### THE GAP AFTER THE AUDIT — fixed 2026-09-12, see `the-gap-after-the-audit.md`

Phase one of the carry trade, and a batch of its own. ~~**Hot money never
reaches the balance of payments.**~~ **Fixed** — and it was worse than the note
said: not just the counters, the CASH was outside every window too, the two
errors cancelled exactly, and 4,002 months of a cent-level audit never said a
word. Hot money has never touched `pressure()` since the day it was built. Moved
above the strike; eight seeds clean, 46 harnesses green, and the trajectory
moves a long way because hot money now affects the currency: median population
13,964 → 23,537, **seed 4 ends at 0.0% unemployment** with 21,228 posts at 94%
fill, and records **the first sudden stop the panic mechanic has ever fired on
its own**. **What it opened:**

- **THE CARRY TRADE IS NEXT**, which this was phase one of. Decided with Jerus:
  bounded by the bank's spare book (`headroom()`), domestic borrowers get the
  book first, borrowers never default, and they pay the city's risk-free plus
  the bank's own premium. It goes between the branch capitalisation and
  `fundToCover`, with a `refreshBank()` after it — NOT beside `hotMoney`, which
  is past the audit and was exactly the old bug.
- **`bank.lend()` IS AN ANONYMOUS NUMBER.** It cuts cash and credits no book, so
  a carry loan would drop the bank's equity by the full principal and walk it
  into resolution. Needs a fourth book category with its own risk weight beside
  `RISK_CITY`/`BUSINESS`/`HOUSEHOLD`.
- **A GUARD THAT IS NOT LAST DOES NOT GUARD THE END.** The drift check was
  placed mid-tail and a deliberate re-break walked past it reading $0.00. It is
  the last statement in `nextMonth()` now. Worth remembering for the next one.
- **A RUNNER THAT CAN TEST YESTERDAY'S CODE IS WORSE THAN NO RUNNER.** Three
  stale-build incidents in one day: `allchecks` against a stale `ui-classes`
  twice (five green harnesses reported as failures), and an eight-seed ensemble
  launched from a `build/classes` that was never rebuilt, so all eight seeds
  measured the deliberately broken build. Both runners rebuild first now.
- **`headroom()` reads last month's foreign deposits**, because
  `setForeignDeposits` runs later in the month. A one-month lag in the bank's
  capacity; pre-existing, harmless so far, now written down.



### BUSINESS SERVICES — built 2026-09-12, see `business-services.md`

Jerus: build a new tradable sector, services first, manufacturing second; three
rungs, the top one licence-gated, no ceiling on the world's appetite; three
goods; prices left as sourced. The eighth sector, and the first whose customer
is not in the city — an export-only good clears at its floor, so revenue a seat
is fixed by the world and the wage bill is the whole constraint. Save format 22.
Eight seeds: **a bifurcation, not a shift** — six boom for 35–100 years and
close, two take hold and never stop, seed 6 ending with 121 contact centres,
36,300 seats, 81,493 people and **8.3% unemployment**, the lowest measured.
Every seat in it is the **unskilled** rung. **What it opened:**

- **THE CARRY TRADE, and it is Jerus's diagnosis.** *"the issue is trade
  surplus... aka supply v demand... we later just have to add carry trade, where
  foreign borrow from the bank and convert to usd to do stuff with it, aka
  effectively having outflow of currency, so if you want you can add that, i
  think its not too hard? its basically the opposite of hot money."* He is
  right, and it is the missing half of `CapitalFlows`. Hot money brings dollars
  IN for the spread; nothing ever takes local currency OUT. The city ends every
  run as a funding currency — its rate at 0.07–0.5% against a world base of 2% —
  and nobody borrows it, so the surplus has only one door and the currency
  appreciates 20–30% past parity in every seed. Foreigners borrowing local from
  the bank, converting to USD and taking it abroad is the symmetric flow:
  `spread = worldRate − lendingRate − countryPremium`, a stock that responds
  the same way, the money crossing the border through `MoneyAudit` and the
  financial account. **It closes three open items at once** — the surplus, the
  appreciating currency, and *a bank with no lending business*, because this is
  the borrower the bank has never had. **Next batch.**
- **A HOUSING OVERSHOOT DURING A BOOM.** One seed in eight, 21.8 households with
  nowhere to live for three months out of 2,148 homes, while that city goes from
  13,000 to 49,000. Nothing in the game could previously grow a city that fast.
  It is **L2**, the migration housing term, and it was deliberately not tuned
  away.
- **THE COLLAPSE IS THE COST.** Worst unemployment 43% → 71%: thirty-six
  thousand seats closing at once. Faithful — Sydney when the centres left — and
  worth deciding whether the player should get any warning of it.
- **THE PLANNER'S NAMEPLATE BLINDNESS IS NOW MEASURED.** It cost this sector its
  unskilled rung entirely until `staffableShare()` was added. The standing item
  ("the investment planner ignores every ratio") is real, and this is the shape
  of what it does.
- **THE PLAYTEST BUILDS NO SCHOOLS.** The engineering rung never opened in 4,002
  months because the city holds no licences. A college and an institute in the
  founding order would light up the students, the licences and that rung at
  once.
- **MANUFACTURING IS THE OTHER HALF**, per Jerus's "both, services first": a
  labour-intensive export with imported inputs and no deposit gate, for the
  unskilled and skilled end.



Everything open, in the order it is worth doing. Pulled together from
`steam-readiness.md`, `design-queue.md`, `known-bugs-backlog.md` and the sessions
since.

**The list is long and only one item on it decides whether you ship.** Section 1
is a couple of hours, section 2 is the project, and everything below section 3 is
work that makes the game better without making it shippable. Reading them in one
list makes them look equally weighted. They are not.

---


## Earlier: the Done blocks that sat inside the list's section 6, moved here 2026-09-18

### Done 2026-09-11 (late night, very last) — see `crime-has-reasons.md`

- ~~There was no crime, no police and no prison.~~ `Crime`: Jerus's graded
  reasons cell by cell, the police's missing share, a 90% ceiling on what they
  take off, K from Canada; theft, injuries, killings, migration; nine cells of
  the design's table asserted.
- ~~Only four kinds of building the city runs.~~ SAFETY: Police Station, Police
  Headquarters, Jail, Penitentiary, at Halifax's and Ontario's prices; a
  founding constabulary for 1,200.
- ~~Every adult was free, working or looking.~~ Six monthly cohorts in prison,
  out of the labour force from the bottom band, out of the pool by weight,
  into a ledger with frozen debts; caught and not held when the cells are full.
- ~~A theft had nowhere to be on the books.~~ Its own line on a sector's cash
  flow and on the audit; households' savings to the offenders' cells.
- A Safety area on Services (Crime, Police, Prisons, Books), a build strip, a
  city panel section, a budget line, People lines, a notice, a CRIME graph
  group and two presets; `CrimeCheck`, six sections.
- ~~The playtest ordered six police stations for a city that needed one.~~ To
  full coverage and no further.
- **Found:** the city's size is hypersensitive to a small steady drag; no
  lockup; education not in GDP — section 2.

### Done 2026-09-11 (late night, last) — see `the-households-remember.md`

- ~~Every household was built from nothing every month.~~ Kept as far as the
  people are still there; 1% a month re-forms; the tiers follow the jobs.
- ~~One child in seven was an orphan in every city, every month.~~ An artefact of
  the redraw; 11–19 at the end.
- Thirteen household graphs and a preset; `HouseholdMemoryCheck`, seven
  sections.

### Done 2026-09-11 (late night, later) — see `the-long-sick.md`

- ~~Nobody died of being sick.~~ Past two months sick, each age's monthly
  chance; the dead go to the funerals like anybody's.
- ~~Every age was sick at the workers' rate, or not at all.~~ Everyone, babies
  and seniors twice until their care takes it away.
- ~~General care scaled the adults' death rate.~~ It cures the sick, 50–90% a
  month.
- ~~Children, teens and adults died at the life table whatever happened.~~ Half
  of it is nobody's fault; the rest is sickness.
- ~~The sick-rate breakdown missed the unhoused.~~ Its own line.
- Three graphs; `SicknessCheck`, seven sections.

### Done 2026-09-11 (late night) — see `the-people-the-books-left-out.md`

- ~~The unemployed were a number.~~ Their own books: on EI, past it, evicted;
  summed by band on the People screen.
- ~~Every full-time student was counted unemployed.~~ The labour force is
  the workforce less the students; students have their own books, a grant,
  tuition and a loan. *The Reports chart kept a copy that did not get the
  message until 2026-09-15 — top entry.*
- ~~A tier's wages were shared with its unemployed.~~ Families from the adults
  who work.
- ~~One child in seven was in no household, fed by nobody.~~ The orphan
  section, by band; billed nothing; sick and dying at no care.
- ~~Nobody was ever homeless.~~ Evicted when EI and savings are gone, a
  quarter leave; unplaced after the valves; each group shares only with its
  own; 3.7 times the sickness and mortality.
- ~~EI did not exist.~~ A 1.63% premium, 55% of insured wages to a cap, a
  twelve-month ring on the inflow, pro rata hires, arrivals at the unskilled
  rate; three Policy dials; on the treasury and the audit.
- ~~A worker who lost a job took the city's average wallet.~~ Their tier's.
- ~~No student loan ever left the college.~~ The graduates carry them out.
- Nine graphs and a preset; `OutsideCheck`, eight sections.
- **Found:** the unhoused need a poor city; GDP a head 12% lower and why;
  net losses only makes the pool long-term — section 2.

### Done 2026-09-11 (day and night) — see `the-sector-template.md`

- ~~A sector was five handlers, six names and a hundred places that knew
  there were six.~~ One `Sector`, a registry, seven classes; `FoodIndustry`
  is twenty lines.
- ~~Four bespoke markets.~~ `Good`, `GoodsMarket`, `Trade`: every fill is a
  ledger line, the statement and the VAT are struck off it.
- ~~The materials plant made free material for the city's yard.~~ A seventh
  sector sells it on a market, in the band, to the builders.
- ~~Material was drawn the day an order was placed, in lumps the size of the
  order.~~ The crews draw it as they build; the order book is kept per site.
- ~~Work in hand was an inventory term that booked every order as output on
  the order day.~~ The plain accounts.
- ~~A plant was scrapped in the month between orders; a first plant was
  ordered against the founding burst and against the raw trend; the estimate
  priced the whole nameplate at home.~~ One forecast, the visible book, a
  first plant of one at half its nameplate, the trend both ways.
- ~~A company registered but not trading had a BAD record by its first
  plan.~~ Its year starts when it starts.
- ~~Worked-out mines borrowed against their holes for three years.~~ Sold.
- ~~The vault earned nothing.~~ The world's rate on idle reserves.
- ~~The resolution floor was in the founding unit.~~ Nineteenth of that
  family.
- ~~Landlords paid their tenants' showers.~~ Homes are not billed.
- ~~Slots from before load into the wrong world.~~ Format 21; refused with
  a sentence.
- Every screen the six had, once, for all seven; six fixtures moved to their
  causes; all forty harnesses on the template.
- **Found:** densification is a rule to write; the late-game bank from a
  third side; the dealer carries dying companies; bursty building — section 2.
- **Tried and withdrawn:** a large-exposure limit — "Not doing".

### Done 2026-09-11 (night) — see `the-exchange.md`

- ~~Every share was held to maturity.~~ `Exchange`: the bank's desk makes the
  market; emigrants, the world, the households and the companies trade at
  its quote, in that order, every month.
- ~~A household short of money went from savings straight to credit.~~
  Savings, then its paper abroad, then its shares at the bid, then credit.
- ~~The diaspora owned the city.~~ Sold back through the desk; 0% abroad in
  six seeds of eight, Industry's foreign founders home in every one.
- ~~The desk marked its own book up on its own quote, and the city paid for
  every mark-down.~~ $40bn on one seed. The lower of the quote and fair
  value; demand fades rather than vanishes; $217M.
- ~~A buyback at a twentieth a month retired every share there was.~~ A
  tenth a year, against fair value, a special dividend when the market is
  dear, splits and consolidations by powers of ten.
- ~~A desk seven times a quarter of capital, twice over, killed the fixture
  bank.~~ Half of capital across the book.
- ~~A company issued at half fair value into a desk that was long it.~~ It
  borrows instead when the quote is that low.
- ~~The surplus lost its door when it changed hands.~~ The households' paper
  abroad, by the sectors' rule.
- ~~The bank failed ten times a run.~~ Zero, in six seeds of eight.
- The bank's screens show the desk; every owners block the quote, the yield,
  the market value, the desk's holding, a special dividend, what was retired
  and any split; a clicked cell its shares at the market and its dollars
  abroad; the balance of payments what the city holds abroad.
- **Found:** the households' trillion, the bubble — section 2. *And, a
  fortnight later, that "the best yield first" was undefined — see the top
  entry.*
- **A share price history per company** (Jerus, 2026-09-11): two series a
  company in the graph history, per founding share through every split;
  the chart at the foot of every owners block; a THE MARKET group and preset
  on the Reports tab.
- **Played on the PC** (rebuilt in NetBeans, a fresh city, a save and load,
  an old save): five screen fixes in the same batch — the market follows the
  bank on load, a resolution month reconciles, a worthless company is not
  consolidated, the balance-sheet note and two labels.

### Done 2026-09-10 (night) — see `the-owners.md`

- ~~Nobody owned anything.~~ `Equity`: seven companies, shares per household
  per cell, the households first and the world for the rest.
- ~~Every expansion was cash or a loan.~~ Equity before debt when new or in a
  good year, sized by the company's own year and its target; debt in a normal
  one; nothing in a bad one.
- ~~No company paid a dividend.~~ Forty percent of a profitable month, from
  the till or from abroad, the households' part into their savings.
- ~~The bank's capital was a declaration.~~ Sold as shares; the households
  pay; the world takes the rest.
- ~~Book alone priced a leveraged company at nothing.~~ Book or capitalised
  earnings, whichever is more. *And that second branch is what prices every
  such company to exactly the discount rate — see the top entry.*
- ~~A war chest abroad was invisible to the investor.~~ Recalled when needed.
- ~~The sector books sat in old money after a reform.~~ Redenominated —
  `DenominationCheck` read Retail's till at zero the morning after.
- ~~The sector screen's cash lines did not sum to the cash.~~ Four lines
  added; an owners block on every balance sheet, the bank's too.
- **Found:** the world buys once; the diaspora owns the city; the hoard is
  the coupon — section 2.

### Done 2026-09-10 (evening, later) — see `every-household-keeps-its-own-books.md`

- ~~The households' money was seven rows.~~ Sixty-eight cells: a `Household`
  ledger each type extends, summed and modified across all.
- ~~A stock attached to a shape "would be a stock of nothing", because the
  rebuild wipes the shape.~~ The money follows the people: pooled within the
  tier, then across the city, weighed by grown-ups; only what nobody claims
  has left.
- ~~One tier, one lockout.~~ Bankruptcy and the lockout per cell; the large
  family discharges, the single next door keeps its line.
- ~~The save would have read one cell into another the first time a shape
  moved.~~ Named cell by cell; the row array still written for an old build.
- ~~The screen gave every shape the tier's position.~~ A clicked cell shows
  what one of them has; the retired grid shows put by and owed.
- ~~Every UI change went to the PC uncompiled.~~ JavaFX staged; the whole tree
  builds in the cloud; `BuildMenuCheck` runs.
- **Found:** the pensioner living alone, broke in every run ever recorded and
  never shown — see section 2.

### Done 2026-09-10 (evening) — see `outward-investment.md`

- ~~The surplus had nowhere to go.~~ `OutwardInvestment`: the sectors' idle
  cash abroad for the spread, home for the bank's, coupons rolled there.
- ~~The currency was priced on the trade balance alone.~~ On the overall
  balance, reserves excepted.
- ~~The lender could not see what a sector held abroad.~~ On the balance
  sheet, and in `SectorBooks`.
- ~~`CapitalFlowCheck` knew only the crowd's money.~~ An eighth section, every
  claim caused.
- ~~Two fixtures read the net foreign interest as the city's coupon.~~
  `ForeignDebtCheck` reads the coupon; `ForeignCheck` compares wages with
  imports, which is what its paragraph claimed.

### Done 2026-09-10 (late afternoon) — see `the-surplus-has-nowhere-to-go.md`

- ~~The playtest built 163–185 food plants a run.~~ On nameplate against
  demand; the "big" cities in yesterday's ensemble were this.
- ~~The mill's screen priced steel in dollars against local ore, at nameplate,
  gross of payroll.~~ Three corrections, the mine's, applied.
- ~~The playtest sank 136–155 mines a run on any unworked deposit, and 72
  foundries for the jobs.~~ Gated on the private sector's screen.
- **Found and not fixed, because it is a design decision:** the surplus has
  nowhere to go, and the currency kills every exporter on a twenty-year cycle.

### Done 2026-09-10 (afternoon) — see `the-unit-of-material.md`

- ~~A unit of material was $2,000 and a house is made of $180,000 of it.~~
  $18,000; 42 templates re-derived at real shares, totals to the dollar; the
  roads' bands untouched; the eight small buildings costed for real at last.
- ~~The material import bill was never paid.~~ $0 against $2.75bn. Paid at the
  price it was charged at, and the accounts and the builders agree to the dollar.
- ~~Charging it made the bank eat the accrual.~~ Material earned on delivery,
  at the strike, out of the order book.
- ~~The yard and the plant were counts in a repriced unit.~~ Held at their
  value: 36 a month free, 160 a month from a plant.
- ~~An old save's yard would have loaded nine times richer.~~ Format 20.
- ~~One run before and one after.~~ Eight seeds a side.
- ~~Six fixtures stood next to their condition.~~ Twenty-fourth to twenty-ninth.

### Done 2026-09-10 (night) — see `a-firm-that-cannot-pay-sheds-plant.md`

- ~~Heavy Industry and Mining could not shrink.~~ The distress rule, all six
  sectors, on a two-year fuse; banks and occupied homes never.
- ~~A sector at negative cash with no lender ran for ever.~~ Bankruptcy: the
  overdraft is forgiven with the loan, declared, and counted once per episode.
- ~~The food plant was bigger than the city.~~ A fifth of the size; it idles;
  the market is priced on what it brings; the planner counts what was eaten.
- ~~Construction's profit lever collected nothing.~~ It pays.
- ~~Construction in progress was on nobody's balance sheet.~~ Every plant built
  on credit had defaulted a month later, for the length of the build.
- ~~A Convenience Store covered 120 people with five staff.~~ 480; the grocery
  1,600. The last unpriced half.
- ~~A sector that had just built on credit could not borrow the interest.~~ An
  interest reserve above the ceiling, up to the line.
- ~~The inventory clamp destroyed food the shops had paid for.~~ −18,625 units.
- ~~The stores' cheque was only cashed in a month the mill also sold.~~
- ~~Spare nameplate was thrown away.~~ Exported, at the world's price less a
  wedge, straight from the line.
- ~~The shops' last month's sales, the food price and construction's month were
  not carried.~~
- ~~Four fixtures stood next to their condition.~~ Twentieth to twenty-third.

### Done 2026-09-10 (late) — see `the-lender-gets-a-gap.md`

- ~~`buildings.json` did not carry the industrial rebalance.~~ Four numbers; the
  bank in the write-up and the bank in the game were not the same bank.
- ~~The lending ceiling and the insolvency trigger were one constant.~~ 0.9
  against 1.5. The investment desk tests the balance sheet after the deal, with
  the building counted; holding it to the rescue desk's limit halved the early
  city.
- ~~The borrower's default record was not saved.~~ Ctrl-S cleared an 80-month
  ban and repriced the next default as a first offence.
- ~~A failed bank could still lend.~~ Both desks shut while it is in resolution.
- ~~A rescued bank came back with no buffer and re-failed monthly.~~ 1.5x the
  ratio, and never less than one branch's capital — a bookless bank asked for
  nothing and so got nothing, in every long run ever recorded.
- ~~`resolutionLoss` was a flow and a stock in one field.~~ Two fields.
- ~~Defaulting cut the rate.~~ A point per prior write-down, up to three.
- ~~The currency drifted on the world's headline while parity was struck on its
  level.~~ `realisedInflation()`. The exchange rate ends 46% off parity instead
  of 55%, and Heavy Industry never borrows.
- ~~A loaded city traded a month at the founding exchange rate.~~ Carried.
- ~~The land office read its founding price for a month after every load.~~
- ~~The People screen's tightness column read 1.00 for a month after every
  load.~~
- ~~The pension card ignored both dials and the reform.~~ 195x off after a
  reform.
- ~~The Real Estate profit lever did nothing.~~ Its own rate.
- ~~`redenominate()` missed the two segment housing costs.~~
- ~~`CapitalFlowCheck` stood next to its condition.~~ Nineteenth.

### Done 2026-09-10 (later) — see `why-the-bank-kept-failing.md`

- ~~Nobody knew why the bank failed.~~ Measured: payroll was **180% of every
  dollar of interest it ever earned**, and **926 of the 944 months with no
  write-off at all still ran a loss**. The bankruptcies were the visible shocks;
  the machine underneath had never worked.
- ~~A bank branch employed 69 people and gathered $60M.~~ 29 and $253M, the US
  figures. `PAID_IN_PER_BRANCH` was checked at the same time and left alone — it
  was already right at $32M against a real $31M.
- ~~`coverShortfall()` had no underwriting test of any kind.~~ A negative balance
  got a loan, any size, at any leverage, for ever — while `restructure()` twenty
  lines away called the same borrower insolvent past 1.5x assets. One half of a
  rule enforced, the other half not.
- ~~A first default and a twentieth cost the same twelve months.~~ The exclusion
  is the borrower's record and now grows with it. Restructures over 4,000 months:
  163 → **35**; Industry 72 → **5**; Mining 55 → **9**.
- ~~The city recapitalised its bank one building at a time.~~ Opening a branch is
  an equity injection, so a bank losing money always wanted another one: 1,049
  counters carrying $10,553 of book apiece where one branch's capital supports
  $400,000. A branch now has to pay for itself.
- ~~Two versions of that test were silently inert.~~ *A flow cannot be read from
  the state a month STARTED in either.*
- ~~`occupiedHomes` was clamped mid-month live and re-derived against the ending
  stock on load.~~ 1.614 doors let on a reloaded city against 1.000 on the saved
  one. Ninth sighting of the flow rule; carried now.

### Done 2026-09-10 — see `the-price-that-stopped-being-a-price.md`

- ~~Rent was a constant wearing a price's clothes.~~ A portfolio floor applied
  per-leg made every residential building earn the same $445 a head. Struck per
  segment now, carry closed on the blend.
- ~~One market was priced off the other market's building.~~
  `repriceHousingCosts()` kept the single cheapest home in the city and handed it
  to both segments.
- ~~The crowding floor counted doors families are forbidden to enter.~~ *A leak
  inside a branch is invisible until something opens the branch* — second named
  sighting. No city had ever built a studio, so the term was identically zero in
  every run ever recorded, and the rent fix opened it on the first run: 10.552
  households with nowhere at all.
- ~~The bank's solvency record was never saved.~~ Every load read a clean,
  unfrozen bank. Carried now; Jerus's own city's count is not recoverable.
- ~~`stillUnplaced` was re-derived on load in one pass where the live path used
  two.~~ $5.26 apart after one month. Eighth sighting of *a flow cannot be
  reconstructed from the state a month ended in.* *And its twin in the same
  method — `doubledUp` — was still there, found by the senior split on
  2026-09-15. See the top entry.*
- ~~Industry paid out 74.7% of revenue in wages and went bankrupt at any honest
  minimum wage.~~ Output per worker was 4–6x low on three independent anchors.
  Food Processing 6,000 → 30,000 units, Textile Mill 1,100 → 5,500, warehouses
  moved with them, jobs unchanged. *(Granularity reopened that night — section 2.)*

### Done 2026-09-09 (night) — see `the-rebalance-stage-two.md`

- ~~Every payroll went up 4.3x and no price followed.~~ All 41 buildings on
  sourced real capital costs; the iron band on real steel, scrap and ore prices.
- ~~The Iron Mine employed 376 people to run a quarry.~~ Sixty.
- ~~The scorecard priced the mine against a 2.5 Mt/yr open pit when the game's
  mine lifts 30,000 t/yr.~~ *A measurement with the wrong denominator is worse
  than no measurement, because it is confident.*
- ~~The founding endowment could no longer buy a power plant.~~ $500M → $3.5B.
- ~~The player's minimum-wage dial was dead.~~ Multiples of the ladder now.
- ~~`InfrastructureCheck` read GDP as a real quantity in a congested city.~~
- ~~`BooksCheck` restated two templates' costs by hand.~~

### Done 2026-09-09 (night) — see `the-rebalance-stage-one.md`

- ~~Wages bore no relation to real ones.~~ Six rungs on NS Job Bank medians.
- ~~Housing cost per head ran backwards.~~ Flat, studio dearest.
- ~~`LANDLORD_YIELD` was 17.3% and had never measured anything.~~ 5.8%.
- ~~`Migration` was never redenominated.~~ Sixteenth in that family.
- ~~The main menu's Resume button drew an empty city on a cold start.~~
- ~~`GameVersion.VERSION` said `1.0.0`.~~ `0.4.3`.

### Done 2026-09-09 (evening) — see `housing-is-two-markets.md`

- ~~The land curve multiplied two premiums that both grow with the city.~~
- ~~An empty home cost its owner nothing.~~
- ~~Rent had one number doing two jobs.~~
- ~~One rent price averaged a famine and a glut.~~ Two segments.
- ~~The advisor priced a flat nobody in the city could live in.~~
- ~~A single parent and one child walked straight into an adults-only studio.~~
- ~~`monthlyMaterialImports` was redenominated.~~ It is a quantity.

### Done overnight, 2026-09-09 — see `a-reform-is-only-a-change-of-units.md`

- ~~The currency reform left a city "not quite the same city".~~ Sixteen bugs.
- ~~Nobody had measured what the steel decision did.~~ Measured over 240 months.
- ~~Nothing checked the three subsystems that describe the same housing.~~
- ~~`Game.FIXED_ISSUE_COST` and `LandManager.BASE_BLOCK_COST` were money
  constants nobody redenominated.~~

### Done 2026-09-09 — see `the-books-tell-the-truth.md`

- ~~Sales tax was charged to cash and on no income statement.~~
- ~~Steel exports were charged full VAT while ore exports were free.~~
- ~~The government's books dated land, buildings and interest a month behind.~~
- ~~The bank's profit tax and the auto-subsidy were on no budget at all.~~
- ~~The education subsidy was charged to the city twice~~ — and was a leak.
- ~~A freshly loaded city read zero on fourteen things.~~
- ~~Construction had never remitted a cent of sales tax.~~
- ~~`MiningHandler` had no property-tax getter.~~

### Done late 2026-09-06

- ~~Money leaked.~~ Conserved to the cent.
- ~~Education had no pipeline.~~ Save format 18.
- ~~An empty doctor post moved the whole graduate band.~~
- ~~Arriving children were booked as graduates.~~

### Done 2026-09-06

- ~~Steamworks paperwork.~~ Started.
- ~~No way to see what a building costs or does.~~
- ~~Only one road.~~ Three now. See `three-roads.md`.

---

