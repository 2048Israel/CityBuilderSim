# The list — what is open

Updated 2026-09-24 (0.7.8, the bank keeps its capital and a sector defaults a slice at a time, and 0.7.9, the Bank tab rebuilt — deployed and verified as tag 0924a, one deploy; before them 0.7.6 and 0.7.7 as tags 0923d and 0923e; 0.7.4's to 0.7.9's screens are still to be checked by eye, and whether converting for land should push the currency is open, Jerus's call). What shipped is in `changelog.md`,
newest first, with the state of the tree in its top block; this file is the
list alone. `index.md` maps the design notes by subsystem, and `CLAUDE.md` in
the repository is what a session reads before touching source. A session that
has been away reads the changelog's top block and section 0 here, then works.

## 0. Do this week — costs nothing, saves weeks

- **CHECK 0.7.8'S AND 0.7.9'S SCREENS BY EYE ON THE PC** — none of it can run
  in the cloud; `build-ui.sh` compiling it is the only check it has had. Open a
  played city and a fresh one on the Bank tab, and let the clock run a few
  months on every page.
  - *The landing.* The status sentence one bold line (two at most), green,
    amber or red; under it two rows of four cells; the CAPITAL RATIO cell's
    small bar with three ticks (minimum, target, top) — the cell may be taller
    than its neighbours, say so if it pushes the bar out of line; captions
    "the last 12 months", or "the last N months" in a young city. The scroller
    fills the stage under the scorecard with no outer scrollbar when there is
    nothing to scroll and no empty band (the chrome is 330 at
    `showBankMenu()`'s `ui.scrolled(column, 330)`). The five rows each show a
    headline and a small line, and open their page at its top.
  - *The ladder.* Bars from one x, growing to the dearest rate; every figure
    "x.xx% a year"; each caption a signed step in points; the policy-rate row
    blue with "›", lit on hover, landing on Policy › Money › The policy rate;
    tooltips on "What a loan's money costs it" and "The carry trade"; sectors
    only if they owe or are shut out; each sector rung "X on prime: its own
    expected loss Y - Z of its firms default a year at L× its assets[, and its
    record W]" on one line at L ≥ 1 with a record — L and Z the last
    quarter's, which the price is struck on ("...over its last quarter"); a
    sector just written down whole quotes on its restructured books, not on
    the quarter before. The quotes grid's leverage column is the quarter's too;
    the Lending table's is the month's, which the defaults read.
  - *Every page.* "THE BANK — PAGE", four vitals, the chip strip with the
    current chip lit. **With the clock running, the page must not change, the
    scroll must hold, and an opened line must stay open.** "The bank at a
    glance" and the rail's bank icon both return to the landing.
  - *Profit.* The two-column statement lines up with the sector pages'; bold
    totals larger, last month grey; "Interest earned" opens into six rows of
    two columns that add to the line; "Provisions" three rows and the note;
    "The trading desk" with "−" on the bought lines; "Kept in the bank" red
    when negative; the twelve-month block and the ratios under it; a save from
    before this build shows "—" for last month and "Its first month on
    record".
  - *Lending.* "The businesses, one by one": eight columns in the 560px width
    with nothing overlapping or clipped (owed / pays / leverage / a year /
    stage 2 / set aside / this month), "owed" the first money column; "a year"
    reads "under 0.1%", "2.1%", "all" or "—", amber past 0.90 and red from
    1.50 like leverage; "stage 2" a share ("37%" or "—") in a wide-enough
    column, amber in stage 2; "this month" red only past the default point or
    for a sector that went under this month; a shut-out sector's name red, no
    status column; the note one wrapping paragraph ("under 0.1% at 0.60, 2.1%
    at 0.90, half at 1.50" … "starting again from the month it is written down
    whole; the defaults read the month."), nothing about a limit per business
    or debt held abroad; a sector written down whole not watched or in stage 2
    in the months after. The families' block only when they owe. "What it has
    set aside": "a year's expected defaults on a sound borrower, never under
    0.4%, and a loan's whole term of them on one in trouble". "Who has stopped
    paying": this month / in total / went under ("never" or "N time(s)") /
    status; sectors under $1 written off with no backstop or ban left off;
    "this month" the same after a reload. "How the next loan's rate is built":
    the grid "" | leverage | its own risk | its record | it pays at widths
    {150, 70, 100, 80, 110}, each owing sector then the families and the carry
    trade, the note ending "…a business decides whether to build at that
    rate."; a sector with no assets shows the whole curve (64.46%) — right by
    the rule, and may read as a bug. The weight table's five rows, the desk's
    shares last, and a total equal to "Weighed for risk and term".
  - *Funding.* "One branch reaches" $250.0M in a city that never reformed, a
    hundredth after 1:100; within reach plus beyond reach equals the city's own
    savings; the savers' block explains the share, amber with a "margin could
    not pay" sentence when rule 2 held it; the funding-mix bar has a key; "What
    it can carry" reads "nothing - it has failed" for a failed bank; the branch
    verdict one sentence, "Build a Commercial Bank ›" opening Build ›
    Commercial.
  - *Capital & owners.* The wide band bar's three labelled ticks, the target's
    and the top's labels apart; "It chose the minimum and …" one sentence;
    "What it does with its profit": the note's last sentence ("Its desk buys
    its own shares back only with what it holds over its target … at the
    desk's weight.") wraps, and "Its own shares bought back this month" reads
    sensibly beside "What it held over its target"; the equity's movement lists
    only the causes that moved it, then "Not accounted for $0" in grey, then
    the total — **a red alarm there is a bug to report, with the month**; the
    owners block with its share-price chart, then the rescues; a failed bank's
    rescue block at the bottom and at the top of the landing, the button green
    only when the treasury can cover it.
  - *History.* Six charts, the rate and capital charts' axes and legends in
    percentages, not money; months under the target, under the minimum and
    losing money, each "N of M"; the worst year of provisions; "Nothing to draw
    yet" under two months.
  - *No bank, and a failed bank* (the held-10% playtest slot, or a save where
    it failed): the landing's alerts, the red status sentence, "failed" in the
    CAPITAL RATIO cells.
  - *The sector screen.* "Its firms that default a year" only while it owes,
    amber past 0.9 and red from 1.5; "What its lenders have lost" once $1 is
    written off, with "Times it went under whole" and the grey note; the "It
    cannot borrow" alert "This sector went under - it had nothing left…"; the
    rate note (round 2's long sentence); the cash flow's "Material bought from
    scrapped plant" (Construction) and "Scrapped plant's material, sold to the
    builders" (the seller), the premises line alone when there was no sale,
    and "Stock used, paid for when bought" (Construction, in months it drew
    salvage); the bank's owners note on its capital policy.
  - *Construction.* The income statement's inputs line opens to "Material from
    scrapped plant"; the operations panel's "From scrapped plant, its own: N on
    hand, M built with this month" and the materials note.
  - *Elsewhere.* The advisor's "Declined X - not even one would cover its
    interest at N%, the rate its own risk costs at the debt it would take on",
    "Sold N X - … (plot back to the city for $Y) (its material to the builders
    for $Z)" (two brackets on one line), and the two "Holding: the bank is …"
    lines when its capital refuses a plan; the Policy tab's shut-out line "… A
    sector that went under cannot borrow to build, and a subsidy is the only
    thing on this tab that reaches it."; the inbox's "Businesses are going
    bust" — a two-column body with a 16-character sector column lined up in
    the inbox's font, three lines for a sector that went under and two for one
    past the default point, the allowance-and-profit pair only with a bank,
    "See who owes the bank →" opening Bank › Lending at the top, about once
    every two to five years in a normal city — and the failed-bank notice's new
    body; the time-skip report's "Defaults $X written off by lenders", amber
    only when the skip lost more than `BASE_LOSS_RATE` a year of the
    businesses' debt, with "Lenders wrote off debt that could not be repaid."
    only then; the strip's no-bank tooltip (window money) and the branch
    advisor's no-bank reason.
- **CHECK 0.7.7'S SCREENS BY EYE ON THE PC** — none of it can run in the
  cloud; `build-ui.sh` is the only check it has had. ~~The Bank tab: the
  landing's lead line and its "What it can lend" row (prime on the dial), the
  vitals' PRIME cell, the gauge (comfortable, nearly full, past what it can
  carry; prime in the hole; the key's three lines), "What its prime is made
  of" under it, the no-bank and failed-bank alerts, the Deposits note (35% to
  90% of the policy rate), the Funding page's "0.25-point penalty", the Income
  page's Fees line opened into its three, the tax note's late-profit sentence,
  and the Strain history's prime-against-the-dial chart.~~ — the tab was
  rebuilt in 0.7.9; the item above covers it (and the strip's no-bank text
  changed again in 0.7.8). The Summary's THE
  BANK flag ("n% lent" past capacity) and the dashboard's bank line; the
  Finances rate page with no bank row, and the issuing page's bank appetite;
  the sector page's "The bank's prime is"; the Policy page's savers preview and
  the note under the rates; the strip's "bank" tooltip; the Reports picker's
  four new traces.
- **CHECK 0.7.6'S SCREENS BY EYE ON THE PC** — none of it can run in the
  cloud; `compile-all` is the only check it has had. The ladder on every
  page that has one: its width (the reading held at 118, the slider taking
  what the buttons leave), the ends line ("x to y · one step is z · it is w",
  and "set at once" on the monetary page's target, holdings and ceiling), the
  "−" and "+" greying at the ends, the reading in accent off the city's
  value, the chips still beside the three that had them. The Schools page:
  "Every school at once" and the nine rows — each ladder beside its four
  figures, a kind with no school greyed and reading "no school", moving every
  school at once clearing the per-kind staging, the preview's line per kind
  moved. The Reports page: the "layers" chip on the real-GDP small chart and
  on the big chart's reading when real GDP is picked alone; the stacked chart
  lining up with the line chart (same plot area, the y-axes the same width);
  the three layer colours against the line; the key and its caption; the
  crosshair's four part readings; the log chip refusing while the layers are
  on. The land office: the chip pair, the sentence under it and the receipt
  after a purchase (a short vault's included), the two prices on each tile,
  the US$ market cell, "Not enough in the vault or cash". The Exchange page's
  "Spent on land" lines; the Government tab's "Land bought" opened into its
  three rows; the history's land axis in US$.
- **CHECK 0.7.5'S SCREENS BY EYE ON THE PC** — none of it can run in the
  cloud; `compile-all` is the only check it has had. Jerus, on the Reports
  page: the default trio on two axes — both plot areas on the same pixels,
  the right-hand axis on the right, the bottoms level, each line's colour the
  same as its reading's swatch; the crosshair's line and box (the box flips
  to the left near the right edge; "(n months averaged)" on a long window);
  the recession bands behind the lines on all three charts; the episode ticks
  under the x-axis at the right months, their names on hover, the caption
  line; "log" — gridlines labelled as real values, the chip greyed with its
  reason when a line touches zero or three units are picked; "pin" and
  "unpin", the older pin dropping, the pins surviving a restart and another
  slot; the picker's groups closed except the picked ones, the filter box
  keeping the focus through a month's redraw and Enter handing it back; a
  group opening or a preset pressed leaving the chip under the pointer. On the
  build page: Enter with three cards pending builds all three, and stops at a
  refusal with the rest still dialled; Backspace and Delete clear; Enter with
  nothing pending still presses a focused button; the caption comes and goes;
  "−" on an empty card reads 0; the licence refusal screen lights Build.
- **CHECK 0.7.4'S SCREENS BY EYE ON THE PC** — none of them can run in the
  cloud; `compile-all` is the only check they have had. The monetary page's
  "What the rule aims at" (the chips, the half-point ladder, the sentence
  struck at two targets); the strip's third panel at a 1280-wide window (the
  brief's bar: it must not widen the strip past that, and the date and the
  money stay the loudest things); the sector list's sparklines, "no history
  yet", the workers line and "Show more" (the second row's five cells are
  sized off `STATEMENT`); each tax page's own base lever and the Everything
  page's three bases and "Every tax at once" (including staging it when the
  three have parted, where it reads "three rates"); the "at its legal maximum"
  flag naming one, two or three taxes; the bank page's "It charges".

**NEXT, AND IT IS 7.0: THE MONEY SUPPLY.** Jerus, 2026-09-21, reading his
0.6.9 city's decade book (prices 399x founding in 25 years, the currency at
its 100x guard, average inflation 29% a year): *"next up we are going to have
to delve into M2 supply and all, which will be 7.0."* What the decade book
showed, read against the model's own rules (`ForeignAccounts.repriceCurrency`,
`LabourMarket.COST_OF_LIVING_PASS_THROUGH`, `PriceIndex`, `Bank.ratePremium`):
**the model has no nominal anchor.** The currency drifts by the full
inflation differential every month (`rate *= 1 + (local - world)/12`, unbounded)
and is pulled toward a parity that itself rises with local prices; imports
reprice through the rate one for one; wages chase the whole index over two
years; rent follows the wage; the shelf is cost-plus on both. Every link in
that ring has a gain of one, so a shock never decays, and every force against
it is capped - the rate differential's support at 0.8 x 2% a month, the policy
dial at 25% (the Taylor advice at 45% inflation is 67%), the reserve's
absorption at 85%. And the money to pay 400x prices is simply created: past
its capacity the bank funds itself abroad, at 5.5x capacity in that city, so
lending is limited only by the premium's 18 points. 7.0 is the anchor: a
central bank that holds an overnight rate by operating in the market (the term
structure `DebtManager.CITY_DISCOUNT`'s note already promised — built in 0.7.0
and 0.7.1, and the constant is gone), reserves that
constrain the bank's book, and M2 as a series the player can see and the price
level answers to. Until then, the two cheap questions are ~~whether the PPP drift
should pass through less than one for one~~ — closed 2026-09-22 (0.7.2): the
drift is deleted; the inflation differential reaches the rate only through
parity and the trade balance — and ~~whether `MAX_POLICY_RATE` should
be allowed above inflation~~ — done 2026-09-22 (0.7.2): `MAX_POLICY_RATE` is
1.00, a guard against a typo, and the rate's support reads the real rate
uncapped. Not tuned; Jerus's design. *~~The second is answered
(2026-09-21, the same night): the cap stays at 25% until 7.0 — above ~13 points
over the world a higher rate buys no currency support in this model and only
reprices credit and the bank's wholesale funding — and the monetary page now
prints what the rule would set and where the dial stops. The first is still
open.~~ — superseded 2026-09-22 (0.7.2): the cap is lifted with the channel;
see `the-currency-off-its-rule.md`. The vault, the founding reserve and the strip shipped as 0.6.10, see
`a-reserve-defends-a-currency.md`. Batch A (0.6.11) and batch B (0.7.0)
shipped the ground and the central bank's books and the floor — `CITY_DISCOUNT`
and its note are gone. Batch C (0.7.1) shipped the curve, the households and
the central bank as holders of the city's paper, and the holdings dial (QE and
QT) — see `the-curve-and-the-holders.md`. Batch D (0.7.2) took the currency
off its rule — the real rate moves it, the vault is spent defending it, the
dial reaches 100% and the advances ceiling is a dial — see
`the-currency-off-its-rule.md`. ~~The households' saving response is batch E,
next~~ — done 2026-09-22 (0.7.3): see `the-demand-channel.md`; the channel is
in and moves very little, and the next rate that bites needs a shelf priced
by scarcity against money, or a deposit rate that passes the dial through.*

**And the model rule changed the same day:** both agents - the implementer and
the docs pass - run on Opus from 2026-09-21; Fable is withdrawn (he does not
have the tokens to run it for every task). `CLAUDE.md` says so.

**FROM THE STRUCTURE AUDIT, 2026-09-18** (see `the-ai-ergonomics-audit.md`), two
that take a minute each on the PC:

- **Normalise the line endings, in one commit of its own.** `.gitattributes` is
  in the tree; the files are still 120 CRLF to 55 LF. `git add --renormalize .`
  then `git commit -m "Normalise line endings to LF"`. Until it is done the
  "keep each file's own ending" rule stands.
- ~~**Delete the twenty-seven stale Java files from the project**~~ — done
  2026-09-18 (evening), on Jerus's word: twenty-six 2026-09-01 uploads removed;
  the project holds design notes only now.
- **Run `Regenerate maps.bat` after each batch** and commit `docs/` with it; a
  map with yesterday's line numbers is worse than no map. (The maps in the
  2026-09-18 evening deploy are already regenerated, and `JavaScan` no longer
  files an `@Override` method as an initializer.)
- ~~**`git rm` the three stubs at the root**~~ — done 2026-09-18 (Maven refused
  to build past a stub, which is why: a source path finds the file by name).
  After each split batch: Clean and Build, open the game, confirm it is the
  same game.

~~**DEPLOY THE PEOPLE OUTSIDE THE FAMILIES AND THE LONG SICK.**~~ **Deployed
2026-09-11 (evening)** when the PC came back: thirty-two files, every one
byte-for-byte on the PC. **The households remember** went the same night, six
files, and **the running totals of the dead** after it, eight, and **police,
crime and prisons** last, thirty-two. Rebuild in NetBeans and run `AllChecks`
(forty-seven harnesses). Save format is still 21; old slots load — with only
the founding constabulary, so a big saved city shows the crime notice until it
builds a station.

~~**DEPLOY THE REBALANCE.**~~ **Deployed 2026-09-09**, and five times more on
2026-09-10: the rent, migration, bank-solvency and industrial-output work, then
the bank pass, then the audit batch, then the distress batch, then the materials
pass. Every file verified byte-for-byte on the PC after each sync. *And a lesson from the third: the second deploy had carried the
industrial fix in the Java and NOT in `buildings.json`, so every figure in
`why-the-bank-kept-failing.md` was measured against a data file the game did not
have. `BuildingDataCheck` exists to catch exactly that and was failing. Run
`AllChecks` after a copy-back, not before.*

~~**DELETE EIGHT FILES IN NETBEANS.**~~ **Done 2026-09-11 (midday)** — Jerus
`git rm -f`'d the eight stubs, committed on `sector-template`, merged to
`main` and pushed; `MenuManager.java` is gone too. Git rewrote the batch as
CRLF on checkout — the mirrors were realigned. **Rebuild before playing: save
format is 21, and slots from before the template say so instead of loading.**
See `handoff-after-the-sector-template.md` for the state of the tree.

~~**Start the Steamworks paperwork.**~~ **Started 2026-09-06.** It runs in
parallel with development for free from here, which is the whole reason it went
first. The thing to watch is that Valve holds a new app for **30 days** after the
$100 is paid before it can be released — so the clock that matters started when
the fee did, not when the game is finished.

**The HTML manual is current again — 0.5.15, version 5, 2026-09-14.** Rebuilt on
2026-09-12 against the tree at 0.4.4 / save format 21: nineteen sections instead
of sixteen, with new ones for the people outside the families, crime and police,
goods and markets, and the owners and the exchange; the month order carries the
maintenance charge and the crime step; the vitals, the mortality table, the
catalogue, the harness list and the open questions were all re-struck from the
source and from the eight-seed crime ensemble. **Brought forward on 2026-09-14 to
0.5.15 / save format 26**: the three newest sectors and what each of them
changed, the farmland relief dial, the taxes area redesign (five pages, one
staged proposal, the quarter-point ladder), the cost-of-funds floor and the
measurement that closed the "real intermediary" idea, the price index's
water-marks, the clock and the speed ladder and the summary-as-problem-list, the
goods table grown to thirteen goods, and four new open questions. It lives as a
published artifact (title *CityBuilderSim*), not in the repo. **Re-read it after
any batch that changes a headline number** — build, save format, file count,
building count, harness count &mdash; and after any batch that closes one of the
open questions on it. **Assemble it from the PUBLISHED PAGE and never from the
local `.part` files** — they had quietly diverged by a whole comparison table and
three open questions, and building from them would have deleted published
content; and the publish guard will refuse until the live page has been read
line by line in the publishing session, which is the rule working. **STILL TO
DO: the share pin points at version 1**, so anybody holding the share link is
reading the 0.4.4 manual until it is moved. *And the year book
is the other half of this: a run can now be read as two text files without the
game — see the top entries — so anything written about a city from here should be
struck off `year-book.txt` rather than off a screenshot. Since 2026-09-15 the
book also carries `averageWage` and `labourForce`, and its unemployment column
and the Reports chart finally strike the same rate.* ~~**AND IT IS BEHIND AGAIN as
of 2026-09-15: save format 27, a sixth age band, and 50 harnesses.**~~ **CAUGHT UP
2026-09-15 at 0.6.0 / format 27, version 6.** ~~**And behind again by 18
September: five sectors, the transport stack, the vehicles, the interface
split.**~~ **CAUGHT UP 2026-09-18 (night) at 0.6.7 / format 27, version 7 —
twenty sections, see `the-manual-at-0-6-7.md`; the found-on-the-way list is
under Housekeeping.** ~~**AND BEHIND AGAIN as of 2026-09-19: 0.6.8 — the two
health dials and the Health page, the household that goes without care, the
openable treasury row, the desk's re-mark line; 206 files, ~126,000 lines.**
**And 0.6.9 as of 2026-09-21: the Schools page (the Tuition page renamed) — the
price of a place, the grant as a basis and an amount, a rate on the student
loan — and a new revenue line, "Student loan interest"; 206 files, ~128,000
lines; save format 27 and 57 harnesses unchanged.** If the manual's open
questions carry the tuition table's calibration, it is answered by the dial.
**And 0.6.10 as of 2026-09-21: the top strip's two new panels (prices against
founding over inflation year on year; the rate both ways, `US$1 = D$x` over
`D$1 = US¢y`), the founding vault (a new city opens with D$2.5B in the
treasury and US$1B in the vault, rather than $3.5B of cash), and the vault
kept in dollars with a monthly revaluation line; a reserve now damps only a
fall; the monetary page names the rule and the dial's stop apart; 206 files,
~129,000 lines; save format 27 and 57 harnesses unchanged.**
**And 0.6.11 as of 2026-09-21 (built; it ships with batch B): the bank pays
for the city's paper at the settle after the issue, which it never had; the
trade page's push on the rate is a preview; what the currency did to the debt
survives a reload; 206 files, ~130,000 lines; save format 27 and 57 harnesses
unchanged.** If the manual's open questions carry wages running a third above
the index, it is closed without a fix: it does not reproduce.
**And 0.7.0 as of 2026-09-21/22 (deployed with 0.6.11): the central
bank — its balance sheet, M0 as its liabilities, money made and destroyed only
through its operations; the bank's spare cash earning the policy rate and its
shortfalls borrowed at the window; the Money page under Finances (M0 and M2, a
year of each); the autopilot on the monetary page; advances to the treasury
with a ceiling and Jerus's arrears rule, on the Government tab; `CITY_DISCOUNT`
gone, so a city that owes nothing is quoted the dial, not two points under it;
209 files, ~132,000 lines; save format 27 unchanged; 58 harnesses.** If the
manual's open questions carry the overdraft with no floor, or the city
borrowing below its own central bank, both are closed.
**And 0.7.1 as of 2026-09-22 (deployed): the curve — a term premium over the
dial by maturity, listed on the borrow page one row per maturity and on the
rate page at thirty years; term loans at 10, 20, 30, 40 or 50 years only; who
holds the city's paper (the households, the bank, the central bank, abroad)
on the book page and the households' paper on the household screen; the
holdings dial (0–50% of the term paper) on the Policy tab's monetary page and
what it holds on the Money page; the bank's unearned discount on its balance
sheet; the Government tab's Subsidies line; 210 files, ~135,000 lines; save
format 27 unchanged; 59 harnesses.** If the manual's open questions carry one
rate for every maturity, or the bank as the only buyer of the city's paper,
both are closed. The money chapter is written after batches D and E.
**And 0.7.2 as of 2026-09-22 (deployed 2026-09-23):
the currency answers the real rate, not the inflation differential — the
forces page's four terms (trade, the real rate, the vault's defence, parity)
and a real-rate line on the monetary page; the vault spent defending the
currency, on the Exchange page (sold this month and since founding) and under
the central bank's equity on the Money page; the policy dial to 100% with
chips from 0 to 100; the advances ceiling a dial, 3 to 36 months, on the
monetary page; a dollar bond valued on the world's curve; 211 files, ~137,000
lines; save format 27 unchanged; 60 harnesses.** If the manual's open
questions carry the currency drifting by the inflation differential, the
dial's stop at 25%, a reserve that damps for free, or the six-month advances
ceiling, all four are closed.
**And 0.7.3 as of 2026-09-22 (deployed 2026-09-23 with 0.7.2): the demand channel — what a household spends above a
basket a head answers the real deposit rate, printed on the monetary page
("savers earn X% real, so households spend Y% of what they would at zero");
the currency's guards a billion either way, so the rate is no longer held at
100, the strip's second line turned round past a hundredth of a cent ("US¢1 =
D$1,000") and the screens' rates printed through one formatter; EI paid in
the month it is credited; the bank's quoted deposit rate no higher than its
lending rate; 211 files, ~138,000 lines; save format 27 unchanged; 60
harnesses.** If the manual's open questions carry the currency's guard at
100, a policy rate that moves no spending, or the transmission left
unasserted, all are closed.~~ **CAUGHT UP 2026-09-22 (night) at 0.7.3 / format
27, version 8 — twenty-one sections, the thirteenth the central bank; and in
the repository for the first time as `docs/manual.md` (GitHub renders it) and
`docs/manual.html`, generated from the published page by the new
`tools.ManualToMarkdown` — see `the-manual-at-0-7-3.md`; its found-on-the-way
list (sixteen places) is under Housekeeping. The two files and the tool went
to the PC with 0.7.2 and 0.7.3 on 2026-09-23, tag 0922c, verified.**
- **And 0.7.4 as of 2026-09-23 (deployed 2026-09-23, tag 0923a): the inflation target a dial (0–10% in
  half points, on the monetary page, saved under its own key); a third strip
  panel with the central bank's, the bank's and the city's rates; the sector
  list's sparkline, workers and "Show more", on two new history series per
  sector; a base rate per income tax with "Every tax at once" on the
  Everything page; 212 files, ~140,500 lines, 805 dials; save format 27
  unchanged; 60 harnesses.** What the manual now says that is not so: §13's
  rule table names `INFLATION_TARGET` (it is `DEFAULT_INFLATION_TARGET`, and a
  dial); §14's strip paragraph has two panels and "the 2% target" (three
  panels, the player's target); §11 opens "Two city-wide rates, and
  everything else is an offset … from one of them" and calls income tax three
  taxes "sharing one dial" (four bases now); §19's list of what went into
  saves without a bump lacks the target key, the three slots and the two
  series; §21's "What no harness looks at" names "the strip's two panels", and
  0.7.4's screens join the unchecked ones. No open question is closed.
- **And 0.7.5 as of 2026-09-23 (deployed 2026-09-23, tag 0923b): Enter builds what is pending on a
  build page and Backspace or Delete clears it; the Reports page redrawn — two
  pinned small charts (real GDP and the population by default, a preference
  in `settings.json`), the presets, "clear all" and a log switch beside the
  big chart, "What money costs" on a first visit, two units on two real axes,
  a crosshair, recessions shaded, named episodes (`YearBook.episodes()`) under
  the chart and in the year book's WHAT HAPPENED; the picker folded into its
  groups with a filter; 212 files, ~141,900 lines, 815 dials; save format 27
  unchanged; 60 harnesses.** What the manual now says that is not so: the
  build line (0.7.3, already behind at 0.7.4); §19's Reports sentence ("sixty-odd
  series with one heading per group", one chart — nothing of the pins, the
  second axis or the episodes); §19's clock paragraph names the space bar and
  the arrows and not Enter or Backspace on the build page; §20's
  `YearBookCheck` row lacks the named episodes; §21's "What no harness looks
  at" names the Reports page, and 0.7.5's screens join the unchecked ones. No
  open question is closed.
- **And 0.7.6 as of 2026-09-23 (deployed 2026-09-23, tag 0923d): every
  policy dial on one ladder (−, a snapping slider, +, the reading, an ends
  line), the monetary page's target, holdings and ceiling given one beside
  their chips; a price per kind of school, with a row per kind on the Schools
  page (places, students, cost, revenue); real GDP drawn in layers — C, I and
  G stacked, the line over them, net exports the gap — on four new history
  series; land priced in US dollars and paid at the day's rate, by converting
  cash (the default) or out of the vault, a toggle at the top of the land
  office; 213 files, ~144,200 lines, 831 dials; save format 27 unchanged; 60
  harnesses.** What the manual now says that is not so: the build line;
  §10's "The price of a place" (one `tuitionScale`, "five dials on one foot
  bar"); §11's ladder drawing and its ends line, and that it is the tax
  pages' alone; §11's GDP and §19's Reports sentence say nothing of the
  layers; §15's "what the city pays" is local money with nothing of dollars,
  the rate, the toggle or the vault — and its "Ten plots" is nine, older than
  this batch; §1's step 2 re-prices the land office in local money; §14's
  vault says nothing of land paid out of it; §19's list of what went in
  without a bump lacks `landPaidFromVault`, the listing's −105 marker, the
  price state's fourth slot, `ForeignAccounts` slots 31–36, the nine tuition
  scales on the policy array's tail, the schools' month by kind and the four
  GDP series; §20's rows for `EducationCheck`, `HistoryCheck`, `LandCheck` and
  `ForeignCheck` lack their new sections; §21's "What no harness looks at"
  gains 0.7.6's screens, and its open questions gain land conversion's
  missing push (below). No open question is closed.
- **And 0.7.7 as of 2026-09-23 (deployed 2026-09-23, tag 0923e): the bank prices a
  loan from its costs and the strain premium is gone; savers get a rate the
  bank chooses; an account fee and a loan fee; the window at the dial plus a
  quarter point; the bank's dividend after tax; four new history series; 213
  files, ~145,700 lines, 838 dials; save format 27 unchanged; 60 harnesses.**
  What the manual now says that is not so: the build line; §1's step 3 ("its
  premium and its cost of funds"); §12's curve formula ("+ the bank's
  premium") and "what the bank's strain and the advisor read"; §12's Business
  credit (the city's rate + 1% to 8%, floored on the cost of funds plus a
  point — prime plus a re-based spread now, and the loan fee); §12's "The bank
  is somebody" (18 points on every rate; deposits at 45% of interest income
  and the bid) and "Nothing borrows below what the money costs" (the window at
  "policy + a point", and `max(riskFree, costOfFunds + MIN_MARGIN) + its own
  premium` — loans are priced from the funds-transfer price, and the floor
  stands only under the city's paper); §13's "The rate is a floor"
  (`WINDOW_PENALTY` one point; savers paid their share of what reserves earn,
  0.675% at 3% and 2.25% at 10%) and the demand chain's "the deposit rate the
  bank pays out of what its book and its reserves earn", with `MonetaryCheck`
  §6's table (1.032 points then, 1.428 now); §19's list of what went in
  without a bump (`bankPricingHistory`, `bankLateProfit`, the bank's
  last-month array at nine, the account fee on the household statement's
  tail, the four series, `bankPremium` no longer written); §20's rows for the
  harnesses the batch changed. **§21's "Who gets 45%" is closed** — the
  constant and the quote's cap are gone — and its "spread is gone" sale rests
  on a deposit rate that is a share of the dial now; §21 gains the batch's
  opens (below).
- **And 0.7.8 and 0.7.9 as of 2026-09-24 (deployed 2026-09-24, tag 0924a): the bank keeps its capital like a business — a loss
  allowance in two stages, its own target of 10.5–16.5%, a payout rule,
  lending that tightens under its target, its own shares issued and bought
  back by its capital and booked as capital; a sector defaults a slice at a
  time off Merton's curve, and the whole-sector restructure is only the
  backstop; a loan priced off the same curve on the borrower's last quarter; a
  failing sector's plant sold to the builders for its material; the desk and
  the buybacks held to the bank's spare capital; the Bank tab rebuilt, a
  landing and five pages; 213 files, ~151,700 lines, 850 dials; save format 27
  unchanged; 60 harnesses.** What the manual now says that is not so: the
  build line and the header's figures; §1's step 13 (no provision after the
  refresh, no second resolution after the late items, and nothing of the
  capital rule the month's top hands the lenders); §8's "Capacity retires"
  (nothing of a retired plant's material sold to the builders, who build from
  it before they buy); §9's "Forty percent of a positive month is paid out"
  (not the bank's: nothing under its target, 45% in its band, the excess a
  twelfth a month, not capped at its cash), and the desk (its own-share trades
  are capital, bought back only from capital over the target and issued only
  under it; its book held to the bank's spare capital at `RISK_EQUITY`); §12's
  Business credit (the curve — prime + max(0, 60% × PD − 0.4%) + the record,
  at the leverage the loan leaves it, read on the last quarter — and loans for
  buildings as well as holes) and "The underwriter now leaves a gap" (past
  1.5× a sector's firms default a slice a month; only a sector with nothing
  left is written down whole, banned and recorded); "There is no
  concentration limit" (still none; syndication was tried and removed, and
  concentration is under every failure now); "The bank is somebody" (no
  allowance, target, payout or rationing, and "recapitalises to 1.5× the
  minimum" — a standing bank under the minimum is asked for its own target);
  §19's Bank tab, and its list of what went in without a bump
  (`bankAllowance`, `bankCapitalRecord`, `bankMonthLines`,
  `bankStatementYear`, `creditStatements`, `salvageCost`, `paidEarlier`, the
  bank's month lines at 50 and its solvency record at 4, six history series);
  §20's rows for `BankCheck` (§7–17), `CreditCheck`, `MoneyCheck`,
  `HealthCheck`, `ForeignCheck`, `HistoryCheck`, `YearBookCheck`,
  `SaveFileCheck` and `ReadPathCheck`. §21's "A concentration limit on the
  bank" wants rewriting, not closing (it is the measured cause of every
  remaining failure); "A plant that loses money before interest is still lent
  for" now meets a price off the curve at the leverage the plant leaves it at —
  re-measure before closing it; "What no harness looks at" gains 0.7.8's and
  0.7.9's screens (though `BankCheck` §13 asserts the tab's arithmetic); and
  §21 gains `ASSET_VOLATILITY`, the quarter's pricing past the default point
  and `MAX_BUFFER` binding in every seed (section 4).

~~**The repo has no README.**~~ **Written 2026-09-12** — `README.md` at the repo
root, verified byte-for-byte on the PC: what the game is, requirements, build
and run both ways, `AllChecks` (its flags, its filter, its exit status, and the
run-it-after-a-copy-back rule), `buildings.json` and the working-directory-first
lookup, where saves and logs live and when a format bump is actually needed,
`Build EXE.bat` and the SmartScreen note, a map of the tree, how to add a
sector, and the two places the version lives. **Rewritten 2026-09-14 for 0.5.15 /
save format 26**: ten sectors not seven, 157 files, 55 buildings, 48 harnesses
plus the playtest (49, ~108s), **a fourth rule — never move a harness's premise
to let a change through** — a note that the month now arrives on a clock so every
panel must hold its own scroll position, and the manual link repointed at the
current artifact URL. That artifact is still **private to the account** — decide
what the link should be before the repo goes public. *Still wanted: the
player-facing README that goes in the zip beside the exe, per "Sharing it before
Steam" below.* ~~**AND IT IS BEHIND AGAIN as of 2026-09-15: save format 27 and 50
harnesses.**~~ **CAUGHT UP 2026-09-15 at 0.6.0 — top entry.**

**Make a `.ico`.** No image file exists anywhere in the project, so `--icon` is
not even in the jpackage command — the exe ships with the generic Java icon,
which is the first thing anyone sees. An hour, and it changes the impression more
than anything else on this page.

~~**`APPVER` in `Build EXE.bat` → `0.4.3`**, by hand on the PC.~~ **Both bumped
to `0.4.4` on 2026-09-12** — `GameVersion.VERSION` and `APPVER`, which are the
only two places the version lives. jpackage stamps the batch one into the exe
and cannot read it from the Java side, so they have to be moved together; bump
both on every release. *Both at `0.5.15` since 2026-09-14, and the batch's
findstr pattern was tightened the same day from `"String VERSION"` to `"final
String VERSION ="` — the old pattern also matched a comment in `GameVersion.java`
and survived only because that line happens to carry no equals sign. The
save-format changelog in `GameVersion.java` was two entries behind at the same
time (25 and 26); both are written now, and 27 with the senior split on
2026-09-15.*

*(The exe's jar and `buildings.json` were refreshed by hand on 2026-09-05 so the
bundle runs current code. That is not a substitute for a clean `Build EXE.bat`
run before handing it to anyone — and it is now much further out of date than it
was. `dist\CityBuilderSim\buildings.json` is from 2026-09-07 and the game prefers
an external file beside the exe, so anyone running the exe is on that copy.)*

---

## 1. The only thing that gates shipping: onboarding

The game never says what you are trying to do, and never teaches the causal order
the whole simulation runs on:

```
land -> housing -> people -> jobs -> industry
```

Every confusing thing a new player will hit is that order being violated:

- A steel mill in a city with no housing runs at **21%** — nobody can work it.
- Twenty houses and no employer is a correctly empty city, and nothing says so.
- Private investment could sit land-blocked for **120 months** (now bannered —
  but that was one instance of a class). *And the banner is now itself suspect:
  `Game.isPrivateInvestmentLandLocked()` never clears — see the top entries.*
- **A city with no clinics loses 18% of everything, forever**, and the only place
  that says so is the People screen and one banner.

Concretely, in rough order of value:

1. **State the goal.** A sandbox with no framing reads as unfinished even when it
   is deep.
2. **A first ten minutes that works without a guide** — the founding sequence, in
   the game, not in a doc.
3. **Explain refusals where they happen.** The advisor already knows why each
   sector is holding; the player mostly cannot see it.
4. **Make the UI look like a game.** A Steam buyer's entire first judgement is a
   screenshot. This is also why the store page comes last.

*(2026-09-06 chipped at 2 and 3: the build menu now prices every row and explains
every building on hover, and the top strip pronounces the date and cash
(2026-09-21: and prices and the exchange rate beside them, and the Exchange
page says the founders left a vault). See
`build-menu-info-and-receipt.md`. 2026-09-08 rebuilt most of the rest of the UI.
2026-09-09 gave the Real estate screen two rent markets with a plain-words note
about what a high family figure beside a low studio one means — the first screen
in the game that explains a *shape* problem rather than a quantity. That night
the main menu's Resume button learned to load the autosave on a cold start
instead of drawing an empty city — small, but it was the very first thing a new
player could get wrong. The founding sequence itself is still untold. 2026-09-11:
every sector's screen is one generic page now — what it makes, what it buys,
where it went, at what price — so a seventh sector, or a thousandth, gets a
screen for free.)*

---

## 2. The rebalance

**Stages one and two are done and deployed, and the material unit with them.**
See `the-rebalance-stage-one.md`, `the-rebalance-stage-two.md`,
`the-price-that-stopped-being-a-price.md`, `why-the-bank-kept-failing.md`,
`the-whole-codebase-audit.md`, `the-lender-gets-a-gap.md` and now
`the-unit-of-material.md`.

Stage one put the household economy on real figures: wages on NS Job Bank medians
(unskilled $800 → $3,460, spread 10x → 4.5x), the three residential templates on
real build costs (cost per head now flat with the studio dearest, as in reality),
`LANDLORD_YIELD` 17.3% → 5.8%.

Stage two did the same for everything else, at Jerus's call ("full realism
pass"): **all 41 buildings on sourced real capital costs at the size the game
actually builds them**, the iron band rebuilt on real steel and scrap prices, the
Iron Mine's crew cut from 376 to 60, and the founding endowment $500M → $3.5B to
keep the opening exactly where it was.

Between them they turned up **three real bugs** and **eleven fixtures** that had
stopped causing the conditions they test.

### ~~Three decisions waiting on Jerus~~ — decided and built 2026-09-10 (night)

See `a-firm-that-cannot-pay-sheds-plant.md`. Bankruptcy is declared and the
overdraft forgiven with the loan; the distress rule applies to all six sectors
on a two-year fuse; the food plant is a fifth of the size and idles rather than
flood its own price; Construction pays profit tax. **No sector ends the run at
negative cash.** Underneath the four: construction in progress was on nobody's
balance sheet (every plant built on credit defaulted a month later, for the
length of the build), Retail's output side was four to five times low (the
"last unpriced half" below — done), an interest reserve above the shortfall
ceiling, two money leaks the shedding exposed, three caches not carried, four
fixtures that stood next to their condition.

### Two questions that came out of it — Jerus's

- **The bust when the ore runs out.** The city grows to 24,000 by month 2,000,
  then Mining winds down and Heavy Industry loses its cheap input: jobs go
  11,849 → 6,776 in one checkpoint, half the city leaves, it settles at 12,000.
  Worst unemployment on the run is **69%**, at the trough. This morning the bust
  was invisible because the two sectors paid 1,740 wages for 1,400 months with
  money from nowhere. Whether five thousand jobs should go in two hundred months
  is `MAX_RETIREMENT_FRACTION` (25% a month); whether the ore should ever run out
  is the iron reserve's design. *Since 2026-09-11 a mine over a worked-out
  deposit closes within the year instead of borrowing to pay its crew for
  three; the bust is sharper and the bank survives it.* *And on slot 3 at a
  million people (2026-09-15) it took Manufacturing with it through the steel
  price — see the top entries.*
- **Food exports.** Spare nameplate is made for export at 0.6 of the import
  price. At 0.9 of the local price the food industry ended a run holding $72bn;
  at 0.6 of the world's it holds $7.8bn beside Retail's $7.0bn and exports 900
  units a month. The wedge is a number with an argument, not a source.
  *Materials' wedge is 0.4 since 2026-09-11, for the same kind of argument:
  at 0.6 six export plants doubled a fixture city.*

### POLICE, CRIME AND PRISONS — built 2026-09-11 (late night, last), see `crime-has-reasons.md`

Jerus: crime is a function of unemployment and tight or missing homes; police
cut it drastically and never to nothing; prisons; only adults. Every adult at
liberty sorted once into his graded reasons (no home 5, past EI 4, short of
money 3, on EI 2, crowded 2, no reason 0.1) plus 2 an adult for the police the
city does not have; full coverage is twice Canada's 180 officers per 100,000
and takes 90% off; K struck from Canada's 5,585. A quarter violent — half a
month off each on the sick rate, 0.115% killed — the rest theft, half from
savings and half from the tills, handed to the offenders. 9% of crimes times
coverage caught, six months each, caught and not held with no cell; prisoners
out of the labour force and into their own ledger, debts frozen. Four buildings,
the city pays, a Safety area on Services, graphs, a notice, `CrimeCheck`.
Eight seeds: **crime 1.8–2.3 times Canada's before the first station, a tenth of
it at the end, and all that is left is the long-term unemployed and the
crowded.** **What it opened:**

- **THE CITY'S SIZE IS HYPERSENSITIVE TO A SMALL STEADY DRAG.** Seven hundredths
  of a point added to the sick rate, with no crime at all, ends seeds 0 and 3
  9% smaller; at a thousandth of crime's strength seed 5 ends 16% smaller. The
  crime ensemble's 6% smaller cities are this, not crime. It predates the
  batch, and it means an eight-seed median can hide or invent a 10% effect.
  **The first thing to look at next.**
- **A LOCKUP.** The smallest prison is 300 cells for $360M; a city of 15,000
  needs about twenty. No seed ever built one — 2,100–2,900 people caught over
  333 years with nowhere to go. A 40-cell lockup, or cells in the headquarters,
  is Jerus's call.
- **Education's cost is not in GDP.** Healthcare's is, and now the police's.
- **Migration from Cullen and Levitt (1999)**, not the rent: a tenth off at
  twice Canada's rate. Their loss is mostly people leaving; this is mostly
  people not coming, because a push that carried it would churn a sixth of the
  city a year. The first cut (the rent's shape at half its weight) lost a
  college town 38% of its people.
- **The police are college business jobs**, and a Police Foundations type or a
  protective-services job type would be truer.

### THE RUNNING TOTALS OF THE DEAD — 2026-09-11 (late night), see `the-running-totals-of-the-dead.md`

Every month's dead by age band, and the orphans and the unhoused among them,
recorded; running totals derived on the Reports tab (THE DEAD, *Who has died*).
**What it showed:** on seed 0 more babies died over the run than adults (6,522
against 5,121) — the playtest builds clinics and hospitals and never a daycare.
Worth a daycare in the playtest's founding order, beside the college.
*Since 2026-09-15 there is a sixth band on that table: the over-85s.*

### THE HOUSEHOLDS REMEMBER — built 2026-09-11 (late night), see `the-households-remember.md`

Jerus: a record of the households of each type so the rebuild "has a reference
to try and keep but still allow change". The builder keeps last month's
households as far as the people still exist for them, 1% a month re-forming,
the rest built by the old rule, tiers refitted to the jobs. Eight seeds:
**orphans at the end 490–633 → 11–19, hunger 4% → 0%, unemployment 14.8% →
12.2%.** **What it opened:**

- **A LAID-OFF PARENT ORPHANS THEIR CHILDREN.** Families are built from working
  adults, and an out-of-work adult is a one-person household, so when the ore
  runs out 800–1,000 children have nobody for two or three years, hunger 6%.
  Jerus's call: out-of-work households with dependants, a family that keeps an
  out-of-work adult beside a working one, or orphanages. *And a parent who
  enrols does the same — 35,000 orphans and 6,000 orphan deaths a decade on
  slot 3 once the universities went up (2026-09-15, top entries). The same fix,
  and it is written up as a decision in `the-parent-who-went-to-school.md`:
  three shapes, three money models, Jerus's call. Measured 2026-09-15: a jobs
  event alone took one city from 12 orphans to 2,094, so the fix is worth more
  than the student case suggests and should be written against "an adult who
  has left work" in general. **The prisoner is the one case that should stay.***
- **The money pool's volume is not measured.** The point of remembering was
  wallets not moving between redrawn shapes; `LongPlaytest` should print it.

### THE LONG SICK — built 2026-09-11 (late night, later), see `the-long-sick.md`

Jerus: people who stay sick start dying; the base death rate halved for every
band but babies and seniors. A ring of monthly cohorts per age band; past two
months sick, 4% a month for babies and seniors, 1% for children and teens, 2%
for adults; general care cures 50–90% of the sick a month and its ×3 swing on
adults is gone; babies and seniors are ill twice as often until their own care
takes it away. Eight seeds: **1.8% of all deaths are illness** in cities that
build their clinics; the worst sick month **18% → 28%** in every seed, as
cities outgrew their care; deaths per thousand **14.8 → 15.5**. *Since
2026-09-15 the over-85s die at 8% a month past two months, twice the seniors'
4%.* **What it opened:**

- **FULL GENERAL CARE IS WORTH LESS THAN IT WAS.** A fully served adult died at
  0.15% a year (a third of 0.45%); now 0.225% plus a sliver of sickness. The
  halving does not make up for the swing at the good end. Jerus's lever: the
  halving, not the ring.
- **The playtest's cities boom between months 770 and 1,080** on this tree
  (jobs doubling in 300 months against a third) and outgrow the general care
  the advisor builds. Not isolated; the end populations overlap.
- **Where care is thin it kills in earnest** — 3% of a clinic-less city of
  2,000 in four years — and babies most: twice as often ill, 4% a month past
  two months, on top of childcare's ×40.

### THE PEOPLE OUTSIDE THE FAMILIES — built 2026-09-11 (night), see `the-people-the-books-left-out.md`

Jerus: "before police we will do that." The out of work (on EI, past it,
evicted), full-time students and orphans each keep their own books beside the
families, summed by the five bands; EI is a premium off wages into a
twelve-month ring struck on the inflow of the newly unemployed; students live
on savings, a grant and an interest-free loan the graduates repay; the
unhoused are counted, hungry and sick 3.7 times as fast. Eight seeds against
the template: unemployment **13.8% → 15.6%**, GDP a month **$62.9M → $56.4M**,
hunger **0 → 4%** (the orphans), bank failures **2 → 0**, **nobody evicted in
any seed**. **What it opened:**

- ~~**POLICE AND CRIME** — next, in Jerus's order.~~ **Built the same night** —
  see `crime-has-reasons.md`. The model reads the pool, the housing match and
  the household books cell by cell.
- **THE UNHOUSED NEED A POOR CITY TO EXIST.** Not one eviction in 32,000
  city-months: an out-of-work adult ends a run holding $3.4M. In a young city
  the path works — close the biggest employer and 549 are evicted in a year,
  384 on the street at the worst. The households' trillion (below) decides
  whether a long game ever sees one. Because a cell is an average, its
  households run out together: evictions come as a cliff.
- **GDP A HEAD IS 12% LOWER, and the lower one is honest.** Measured with the
  GDP split and two variants: EI is not it; putting the out of work back into
  the families' wages recovers 380 of the 550 a thousand people. The
  placeholder was spending wages the unemployed never earned. If it matters,
  the lever is what the out of work can spend.
- **NET LOSSES ONLY MAKES THE POOL LONG-TERM.** Four in five of the out of
  work are past their twelve months; the pool empties only as jobs are
  created. Real churn — Canada finds work for about a fifth of its
  unemployed a month — would turn it over and put most of it on EI. Jerus's
  call, as it was.
- **THE ORPHANS ARE 4% HUNGER IN EVERY SEED.** "For now." An orphanage, or
  shelters for the unhoused, is the first building this system asks for.
- **The playtest never builds a college**, so students are only exercised in
  `OutsideCheck`. A college in the founding order would put them in the
  ensemble. *And a college is what `YearBookCheck`'s new fixture builds for
  exactly this reason (2026-09-15): with nobody studying, `workforce` and the
  labour force are the same number and the broken rate cannot be told from the
  right one.*
- **The default playtest has never built a school at all** (2026-09-21): the
  advisor's list has none, so in 4,002 months the whole education system fires
  zero times — `students 0`, `grants $0k`, `student loans owed $0k` — and every
  education dial is inert in the baseline. `-Dplaytest.schools=true` builds
  them proportionately; a school in the advisor's own list would make the
  default run exercise it (and move the baseline, so its own batch). See
  `the-price-of-a-place.md` §5.
- **A graduate starts repaying the month they finish.** No six-month grace;
  one more ring. *Still none after 2026-09-21: with a rate on the loan, a grace
  is months with a balance and no instalment, and it has to say whether
  interest runs in them.*
- **Interest while studying, as a second shape.** 0.6.9 charges a graduate
  only (the Canadian shape: the government carries the interest while they
  study). A loan that accrues from the day it is drawn — the US unsubsidised
  Direct Loan — would be a second setting on the rate dial.
- **Whether the founding tuition table should index to wages**, as the pension
  base does, rather than wait on a scale of up to 5× — the alternative the
  tuition dial stands in for (2026-09-21).

### THE SECTOR TEMPLATE — built 2026-09-11, see `the-sector-template.md`

One template, seven classes, a goods economy, save format 21. Eight seeds
against the exchange baseline: bank failures **10.5 → 2** over the batch
(0 on the baseline), and **the city is a quarter smaller** — 13,750 people
against 18,300 — for a reason that is a design question rather than a bug.
**What it opened, in the order it is worth doing:**

- **DENSIFICATION IS NOW A RULE TO WRITE.** The baseline's landlord scrapped
  occupied family houses in every loss streak and rebuilt the plots as
  apartment blocks; the churn filled a nine-year queue, the queue built
  forty-seven depots, the depots' idle crews were paid by the advisor's
  subsidy, and the jobs pulled a bigger city. The template's landlord will
  not sell a door somebody lives in (`RealEstate.mayRetire`) and stops at
  "housing ahead of jobs". Whether a landlord redevelops occupied houses
  into flats when land is short — buying the tenants out, rehousing them,
  paying for the demolition — is a real mechanic, and the one that decides
  whether the city gets back to 18,000. Jerus's design.
- **THE LATE-GAME BANK, AGAIN, FROM A THIRD SIDE.** With the landlord
  repaying its loans the book goes to nothing by month 1,800 on every seed;
  the vault earns the world's rate now (`Bank.placementIncome`) so it no
  longer drains, but it does not grow either, and both failures left are
  after month 3,200: a dealer long the dying mining company's shares, in a
  city with $50bn of savings and nobody to lend to. The item that was
  "what it does with idle deposits, and why nobody borrows the money that
  exists", one door further along. A large-exposure limit was tried and
  withdrawn — see "Not doing".
- **The dealer carries every dying company.** The bank buys emigrants' and
  the world's shares at its bid and marks them; when a company's assets go
  (mines with no ore, valued at cost until they are sold), the bank eats the
  fall. Real market-maker risk, and a real reason the desk should mark a
  company on what it can earn rather than what it paid for its holes. With
  the bank item.
- **Material is drawn as the crews build.** The order-day draw is gone
  (`BuildingsStacks.materialsOwed`, `contractValue`); the builders carry
  the material price between the invoice and the draw, as a contractor
  does. What that leaves: a mature city's construction is still bursty —
  the advisor orders a batch, big crews finish it in three months, nothing
  for a year — so the materials plant's market is a spurt every ten months
  and a 480-unit shed. The seventh sector built one plant a century on seed
  0 and lost money on each until the visible-demand cap stopped it. A
  buyers' stock — builders' merchants who buy steadily and sell into the
  spurts — or a bigger shed on the plant, is the next materials question.
- **Industry is one sector and the mill's cloth is food.** Jerus: "keep it
  all as food for now." The split into Food Processing and Textiles, with
  `TEXTILES` exported at the world's price, is one class and one good when
  the basket wants it.
- **The basket is two lines.** `GROCERIES` and `HOUSING`. The third good is
  a line, not a handler; the first candidate is whatever the mills make
  when Industry splits.

### THE PRICE BATCH — IN PROGRESS, superseded by `why-there-is-no-inflation.md`

*Kept for the original reasoning. Two things in it turned out to be wrong and
they are worth keeping visible: money creation is NOT "the fall's cause" — the
money supply already grows 78x — and #50's "kill the floor" has to mean MAKE IT
LIVE rather than remove it, because removing it opens a currency deflation
spiral the constant had been holding shut. See the write-up for both.*

- **#53 MONEY CREATION.** `bank.lend()` is `cash -= amount`. There is no
  deposit creation anywhere in the model, so the money stock is fixed at the
  founding endowment plus whatever the trade surplus drags in, and the price
  level falls as output grows. Fractional-reserve deposit creation and/or
  treasury monetisation. Turns `MoneyAudit` from a conservation law into an
  identity **with a named creation term** — which is the hard part, and the
  reason this is a batch of its own.
- **#50 KILL THE DEAD SHELF FLOOR.** `OPENING_SELL_PRICE` is a hard floor of
  $300 and it is still binding after 333 years: `shelf price 0.3000` to four
  decimal places while wholesale food is $154. Replace it with a real cost
  floor that includes the shops' payroll and rent.
- **#51 WAGES INTO THE COST SIDE.** Retail's cost-plus is on wholesale food
  alone — payroll, rent and interest are all outside it — so a wage rise cannot
  reach a price. Building costs should index to the wage level too;
  `updateConstructionCost()` is currently a no-op.
- **#52 LET THE SHELF CLEAR IN MONEY TERMS.** The scarcity mark-up is
  unit-based, so it only fires on a physical shortage. Excess demand measured
  in money, not units.

### Still open, in the order it is worth doing

- ~~**`MATERIALS_WORLD_PRICE` is about 9x too low.**~~ **Done 2026-09-10
  (afternoon)** — Jerus: "I think 2 is an issue that delayed makes it worse."
  $18,000 a unit, every template's split re-derived at a real material share
  (40% a building, 25% a plant, 60% a road) with every total held to the
  dollar; the yard and the plant held at what they were worth; save format 20
  reads an old yard at its old value. **And it found that the material import
  bill had never been paid** — builders' expense $0 over 4,000 months against
  $2.75bn imported in the accounts, so the balance of payments never saw a unit
  cross the border and Construction ended every run holding the money. Paid
  now, to the dollar, with the material earned on delivery so the bank does
  not eat the accrual. See `the-unit-of-material.md`. **What it opened is in
  the three items below.**
- ~~**THE LONG RUN IS SMALLER NOW, AND IT IS NOT YET UNDERSTOOD.**~~
  **Understood, 2026-09-10 (late afternoon)** — see
  `the-surplus-has-nowhere-to-go.md`. The food-export basin was the playtest
  advisor: its trigger read the food shed, which the plant throttle now keeps
  at two months of demand, so it built 163–185 plants a run. Gated on nameplate
  against demand; unemployment back to 16% on the same seeds. What is left is
  the steel chain, below.
- ~~**THE SURPLUS HAS NOWHERE TO GO — Jerus's decision.**~~ **Decided
  ("outward investment") and built, 2026-09-10 (evening)** — see
  `outward-investment.md`. The sectors' idle cash buys the world's paper when
  the world pays more than the bank, comes home when the bank pays more, rolls
  its coupons abroad, and the currency is priced on the overall balance. On
  eight seeds: population 11,600 → 21,500, GDP $36M → $74M, unemployment
  worst 64% → 48%, exports $33M → $109M, the currency within 10% of parity in
  every seed where it sat 26–60% off, written off $4.5–7.9bn → $1–3bn, and
  the spread between seeds collapsed. Fifty-seven mines standing at the end.
- **A bank with no lending business.** *Since the exchange the bank does not
  fail — 0 failures in six seeds of eight, 2–3 in the others, against 10 —
  because the trading desk is a business and a profitable one, and a sector
  that has paid its owners borrows for its next mine (`bizDebt $320M` at the
  end of seed 0 where it read $0k). But the book is still $0 on $114bn of
  deposits at the end of seed 0, the deposit rate is 0%, and the desk is the
  bank.* The item it was, with a bank that now lives long enough to be
  asked: what it does with idle deposits, and why nobody borrows the money
  that exists. *2026-09-11: it places its idle reserves abroad now, which
  stops the drain; the rest of the item stands — see the template block.*
- ~~**THE HOARD** wants the corporate sector to have a way to pay its
  owners.~~ **It has one, 2026-09-10 (night)** — see `the-owners.md`. Seven
  companies with shareholders: the households first, the world for the rest;
  offerings sized by each company's own year and its equity target; forty
  percent of a profitable month paid out; the bank's capital sold for real.
  **And the hoard is untouched: US$1.2–2.7tn.** The payout is on net income
  and net income does not contain the coupon earned abroad — yesterday's
  rolled coupon is off the statement on purpose — so forty percent of nothing
  is paid on the thing that compounds. **Jerus's call:** put the coupon in the
  payout base (one line) and measure what it does to the currency.
- ~~**THE EXCHANGE.**~~ **Built 2026-09-11 (night)** — see `the-exchange.md`.
  The bank is the dealer: it quotes round fair value, its book moves the
  quote, it never sells what it does not hold and marks what it holds at the
  lower of the quote and fair value. Emigrants sell on the way out, the world
  trades on yield, households buy the best yield then the next, companies buy
  back a tenth a year or pay a special dividend when the market is dear, a
  household short of money sells before it borrows, and shares split at a
  hundred times their founding price. Eight seeds against the owners' batch:
  GDP $65M → $77M, worst unemployment 46% → 37%, bank failures 10 → 0,
  exports $104M → $124M, the currency within 3.5% of parity, dividends to
  households $3–18bn → $30–56bn a run, every company home-owned in six seeds.
  *And on 2026-09-15 the "best yield first" rule turned out to be undefined
  whenever the model prices two companies on their earnings, because it prices
  every such company to exactly the discount rate — see the top entry.*
  **What it opened:**
- **THE HOUSEHOLDS' TRILLION.** The hoard moved; it did not shrink. The
  sectors end holding US$22–52M abroad where they held US$1.2–2.7tn; the
  households of seed 0 end holding **US$1.1tn** abroad and $114bn at home,
  $769M of coupons a month. The same 333 years of two percent on a surplus
  nothing spends, now owned by the people who earned it. Sane over a player's
  horizon; over the playtest's, the question the owners' doc asked, moved one
  door along. **Jerus's call**, as before: the coupon in the payout base, or
  something that spends it. *And since 2026-09-11 (night) it is why nobody
  is ever evicted in a long game: the out of work draw on the unskilled
  tier's savings, and the unskilled tier holds millions. See
  `the-people-the-books-left-out.md`.* *On slot 3 at 272 years (2026-09-15)
  the coupon line alone is 2.6 times GDP a year — about $4tn abroad.*
- **THE BUBBLE.** With $65bn of household savings chasing companies worth
  $10bn and a deposit rate of 0%, the quotes sit at two to four times fair
  value until the yield hits the deposit rate plus a point. It is the ceiling
  working, and it is what a city with no lending does with its savings; the
  companies pay special dividends into it and issue into it. A bank that lent
  would end it. The bank item, from the other side.
- **The founding stakes** belong to the founders (now) or to the treasury —
  still Jerus's call; one contained change either way.
- **Do savers want the sectors' appetite?** The households' paper abroad
  uses `OutwardInvestment.APPETITE` (forty times the spread, four fifths of
  idle savings at a two-point spread); a household in the PC play-test held
  $414k abroad against $99k at home. It is the dial that fixed the currency,
  and it is a dial. Jerus's.
- **The Build tab's list scrolled back to the top after +/Build on the
  PC**, or the wheel never reached it - seen three times in the play-test,
  not reproduced on purpose. Look at `handleAllBuildingMenus` against
  `keptScroller` when next in the UI. *This is now a class rather than one
  bug: the month arrives on a clock, so every panel has to hold its own
  scroll position through a repaint nobody asked for.*
- ~~**Households' money was seven rows.**~~ **Sixty-eight cells, 2026-09-10
  (evening)** — Jerus: "per household and pay tier type... an object, being
  the basic, and then each extends." `Household` / `WorkingHousehold` /
  `RetiredHousehold` inside `HouseholdBalance`, the money following the people
  across the monthly rebuild, weighed by who carries it; the save names its
  cells; the screen shows what one of them has. See
  `every-household-keeps-its-own-books.md`. *And the weight it is carried by —
  `grownUps()` — is what silently starved the over-85s of their pension for a
  batch in 2026-09-15; see the top entry.* **What it made visible:**
- **RENT PER CELL SHOULD FOLLOW THE DOOR.** The match bills the landlords by
  unit size and puts a senior alone in the smallest unit that fits; the
  households' books charge everybody the city's average door
  (`rentPerHousehold()`). So a pensioner in a one-room flat pays a family's
  rent, and **Senior living alone** is the one cell in trouble in every seed:
  −$46 to −$294 a month after the rent for the first decade, $8,970 owed at
  the ceiling, locked out; in seed 1, 916 of them at the ceiling for nineteen
  years with 9% of the city hungry. `SocialSecurity`'s own note predicted
  pensioner poverty "concentrated among people living alone" — designed, and
  never seen, because the retired row averaged the couple with the single.
  Record the rent weight per shape in `FamilyModel.house()`, hand each cell
  its own door's rent. **Next after the bank.** *Half of it since 2026-09-11
  (night): the rent bill is split over the doors paid for, and the people
  outside the families pay their own door share (a flatsharer a fifth, a
  doubled-up seeker none). Families and the retired still pay the average
  door, so the pensioner alone is untouched.* *And since 2026-09-15 there are
  two more cells of the same kind beside it — **Elder living alone** is now the
  one to watch first when senior care is short.*
- ~~**Households' savings abroad.**~~ **Done 2026-09-11 (night)**, inside
  the exchange batch, because it had to be: the exchange returned the hoard
  to the households and the currency went 31% past parity with exports
  halved until they had the sectors' door. `Household.abroad`, in dollars,
  the sectors' four dials, sold before shares when the household is short,
  emigrants' dollars leave with them; sixteen slots a cell in the save.
- ~~**Wages are spread over every adult in a tier**, employed or not — the
  `FamilyModel` placeholder, visible now as an unskilled single adult taking
  home $1,262 in a month of 40% unemployment. Wants an employment model.~~
  **Done 2026-09-11 (night)** — the families are built from the adults who
  work; the out of work have their own books. See
  `the-people-the-books-left-out.md`.
- **A dead pensioner's savings vanish** as "taken away"; nothing inherits. A
  transfer from the retired cells to the working ones, when somebody wants it.
- **The grid's statement reads the row's average interest.** The cell's own is
  in `Household.interest()`; `statementFor()` should take it.
- **Hot money never reaches the balance of payments.** It moves after the
  audit strikes and `Bank.startMonth()` zeroes the counters before the next
  strike, so `HotMoneyIn` is always zero in the Result and the currency never
  feels an inflow. Latent while no run holds a persistent stock.
- ~~**The steel screen was in the mill's favour three ways.**~~ **Fixed
  2026-09-10.** Steel priced in dollars against ore in local money, read at
  nameplate while the sector ran at 52%, gross of payroll. Net, at the
  sector's operating rate, in the city's money — the mine's corrections,
  applied to the mill. The playtest advisor no longer sinks a mine on any
  unworked deposit or a foundry for the jobs alone unless one would pay.
- **The bank lends 90% against a mine.** Written down to 60% on default, five
  mines' worth takes its equity every cycle. A cyclical exporter is not a
  house. Look at it once the currency stops making every mine default. *Half
  of it since 2026-09-11: a mine with no ore is sold rather than borrowed
  against, so the book the bank lends on is at least a working mine.*
- **Construction's economics.** The municipal works department's 400 points a
  month are billed by the private sector at full price with no wages behind
  them — most of the $6–13bn it ends every run holding, and why 55 depots go up
  in a starving fixture city with a 437,000-point backlog. The depot's revenue
  proxy is `points × materials price`: measured $6.1k a point against an
  actual $8.8k (it was $0.68k, thirteen times low). The plant's 160 units a
  month is a value-added argument, not a source; 400 comes back if it ever buys
  its inputs abroad. *Since the template the builders buy their material as
  they build and keep the order book per site, so the statement is honest
  month by month; the works department's free 400 points are unchanged.*
- ~~**RETAIL'S OUTPUT SIDE IS THE LAST UNPRICED HALF.**~~ **Done 2026-09-10
  (night)** — a Convenience Store covered 120 people with five staff, payroll 57%
  of revenue where real convenience retail runs about ten; 480 now (shelf 1,400),
  the Small Grocery 1,600 (7,000). The same shape as the bank at 180% and
  industry at 74.7%. *What is left of this item is the floor:*
  `sectors/Retail` floors the shelf price at `openingSellPrice`, a
  founding constant of 0.30 that never falls. With food at 0.09–0.16 a unit the
  shops sell at 0.30–0.38 whatever it cost them — comfortable rather than
  obscene now, but still a constant doing a price's job, and still the reason
  the CPI cannot fall below ~0.68.
- ~~**STAGE THREE: the wage-price loop, and it now has a named victim.**~~
  **It was not a wage-price loop. Fixed 2026-09-10 (late).** Wages *fell* 41%
  over the run and steel was flat at ~$850/t in world money; what moved was the
  exchange rate, which sat 55% below parity because `WorldEconomy` reported a
  3.3% headline while its level rose 0.18%/yr, and the currency drifted on the
  headline. One line (`realisedInflation()`), and Heavy Industry does not borrow
  a cent in 4,000 months. See `the-lender-gets-a-gap.md` §6. What is left of the
  decay is the retirement decision above.
- **There is only one power plant and it is 325 MW.** A city of 74,000 needs a
  fraction of one plant, and the plant is now correctly priced at $1.43B, so the
  player buys an enormous thing they will never fill. Wants a small plant beside
  it at the same $/kW. Same argument as the three roads: they exist so each wins
  a band. *(The Wind Farm, added 2026-09-09, is the first step of this.)*

---

## 3. Things a new player will actually hit

Ranked by how likely they are to read as "this game is broken".

- ~~**THE HOUSING STOCK IS THE WRONG SHAPE**~~ **Done 2026-09-09 (evening)** and
  finished 2026-09-10. Five problems in the first pass; the sixth and largest was
  found on Jerus's month-1124 save the next day: **rent had stopped being a price
  and become a constant.** A floor taken on the whole company was applied to each
  segment separately, and in any mature city that floor binds — so every
  residential building in the game earned exactly $445 a head whatever it cost to
  put up, and the city built the cheapest one it owned for ever. 138 low-rise
  blocks, 96 houses, **0 studios, 1,847 households wanting one**. Each leg is now
  struck on its own segment's costs and the carry is made up across the blend.
  Over 4,000 months: 0 studio doors → 19,920, both markets clearing at ~1.0
  households a door. See `the-price-that-stopped-being-a-price.md`. *Slot 3 at
  272 years reads 0.98 homes a household and the crime table counts two adults
  in five as doubled up — see the top entries; the shape may be wrong again at a
  million people, or the count is.*

- ~~**The bank fails about 125 times in 4,000 months.**~~ **Fixed in two passes
  on 2026-09-10.** The morning pass (`why-the-bank-kept-failing.md`) found the
  branch cost, the missing underwriting, the branch-as-recapitalisation loop and
  the flat exclusion. The late pass (`the-lender-gets-a-gap.md`) found that the
  lending ceiling and the insolvency trigger were **the same constant**, so the
  underwriter parked every borrower exactly where a one-dollar fall cost the bank
  60%; that the borrower's default record was not saved (an 80-month ban cleared
  with Ctrl-S); that a failed bank could still lend ($5.05bn written while
  frozen); that a rescued bank came back with zero buffer and re-failed monthly,
  which is what the 308 was counting; and that a bank with no book asked for no
  capital, which is why every long run ever recorded ended "equity $0k against a
  book of $0k". Over 4,000 months: failures **308 → 20**, written off **$25.8bn
  → $1.35bn**, city recapitalisation **$3.76bn → $685M**.

- **Unemployment settles high and stays there.** *Was 41.5% at the end of the
  4,000-month playtest. The rent fix took it to 19.0%, the industrial fix to
  15.1%, the bank pass to 15.6%, the audit batch to 14.0%, and the distress batch
  to **17.1% at the end with a worst of 69.2%** — the worst is now the trough of
  the bust when the ore runs out, which is a real event the earlier runs hid
  (section 2). The end figure has been between 14% and 17% for four batches.
  13.8% on the template's eight seeds, worst 42%. 15.6% since the out of work
  left the families (2026-09-11, night), worst 40%, four in five of them past
  EI.* *Slot 3 at a million people is at full employment with 11,000 posts it
  cannot fill (2026-09-15) — a played city with universities and a land wall,
  not the ensemble's; the year book's 14% there was the students, and so was the
  5% the Reports chart drew until the same day. Every figure in this bullet is
  the model's own rate and stands.*
  (L2)
- **A city ends up 15–160% above its own target and never comes down** —
  arrivals and departures both sit at zero while births keep adding. (L1)
  *Slot 3: a quarter above target, zero arrivals for sixty years, and the whole
  natural increase — 200,000 a decade at a birth rate of 26 per thousand —
  leaving. The birth rate is the lever; see the top entries.*
- ~~**The treasury swings by its whole debt twice a year.**~~ **Closed
  2026-09-22 (struck late; 0.7.0 closed it):** the emergency note is retired —
  `EMERGENCY_NOTE_MONTHS` is gone, a treasury below zero is advanced by its
  central bank up to a ceiling the player sets, and a save still carrying a
  note runs it off; see `the-central-bank-opens.md`. *Was:* the city's entire
  borrowing ended up as ONE six-month bill that it rolled for ever: at month 512
  it repaid $1.44B it did not have, sat $1.4B overdrawn for a month, and the
  emergency-note path immediately wrote a new bill for slightly less.
- **Long bonds are strictly dominated past ~15 years.** At 20y+ they have a
  higher monthly payment *and* a higher all-in cost than a medium bond.
- **The early risk-free rate is 19%, and it is now blocking a second system.** At
  19% no residential template in the game clears its own financing. Same root as
  the 18-point bankless premium (findings #11). *The bankless premium is gone since 0.7.7 (a city
  with no bank borrows at the window's price); whether the 19% is still true was
  not measured.*
- ~~**THE CITY BORROWS TWO POINTS UNDER ITS OWN POLICY RATE.**~~ **Closed
  2026-09-22 (struck late; 0.7.0 closed it):** `CITY_DISCOUNT` is deleted; the
  city's note prices at the dial plus its spreads and the term premium sits on
  top for the longer maturities (0.7.1) — the redesign the entry below asked for
  is built. *Was:* Jerus, 2026-09-12:
  the Policy screen says 3% and Finances says 1% on the same morning, because
  `DebtManager.floorRate()` is the dial less `CITY_DISCOUNT` (.02) and a city
  with no debt sits on that floor. No borrower is cheaper than its own central
  bank. **Decided: leave the pricing, fix the screens** — "much later when we
  will redesign it realistically aka central bank stepping in to keep it at that
  rate and longer durations deviating." Both screens now name which rate they
  are showing and where the discount is; the Policy page's "so the city pays
  over it" compared the DIAL with the world's 2% while the line above it said
  the city borrowed under the world — it uses the city's own rate now, which is
  what the carry trade actually reads. The redesign is a term structure and an
  operating central bank, and it is its own batch. *Since 2026-09-14 the floor
  is also floored at the bank's own marginal cost of funds, so the discount can
  no longer take the city below what its lender pays — see "nothing borrows
  below cost".*
- ~~**Construction has a profit lever that collects nothing.**~~ **Charged,
  2026-09-10 (night)** — Jerus's call. It pays like the other five, on the net
  income it banked, and the figure is carried in the save.
- **You receive more than you asked for.** Face rounds up to the instrument's
  granularity and proceeds follow the face — ask for $5.0M, get $5.82M. (F1)
- ~~**Long bonds are gated by a silent $100M minimum face.** (F2)~~ **Closed
  2026-09-22 (struck late; 0.7.0 closed it):** `Game.minimumIssueSize()`
  replaced the silent floor and the borrow page states the rounding ("Issues
  round to …").
- **A broke city cannot skip, only step.** The emergency is exactly where the
  game is slowest to play. (backlog 22)
- **"Fill %" means job fill, not occupancy.**
- **The middle column does not scroll.** Wrapping `rootMenu` in a `ScrollPane`
  touches every screen, so it wants doing on purpose.
- **`-$0` prints for any debt-free household**, and the orphan bands are three
  orders of magnitude apart — two of the four anomalies read off slot 3 at month
  2305; see the top entries.

---

## 4. Engine truths worth fixing

- **CONCENTRATION IS WHAT FAILS THE BANK NOW — JERUS'S** (`a-sector-is-many-firms.md`
  §5, §7). Every remaining failure on 0.7.8 — collapse, stage-2 set-aside or
  slice — has one sector holding 30–73% of the book, 5–7 times the bank's
  equity. The levers left: a limit on a sector's share of the book (a bank's
  industry limit) or Pillar 2's concentration add-on; rescue for shares would
  make a failure cost the city less. Syndication was the structural answer
  tried, and removed ("Not doing").
- **`ASSET_VOLATILITY` 0.25 — the curve's one number, Jerus's to settle**
  (`BusinessDebtManager`; the Merton/KMV literature 20–35%; 0.20 steeper,
  0.35 flatter). Not tuned to any result.
- **`MAX_BUFFER` BINDS IN EVERY SEED**: the worst year is 38–109% of the
  weighted book, so the target is 16.5% throughout and covers a sixth of it.
- **THE QUARTER PRICES THE SHORTFALL DESK PAST THE DEFAULT POINT — Jerus's
  call** (the quarter was his): 1,436 loans over the default eight, 1,435 of
  them the shortfall desk, which lends on the month's leverage while the price
  reads the quarter — 73% Luxury Retail on its restock's high month, 20% a
  sector a large slice had just cut the debt of, 11 past the point on the
  month's own reading too. Pricing the shortfall desk on the month, or on the
  lower of the two, would stop it. `BusinessDebtManager`'s quarter banner has
  the breakdown.
- **A BANK FAR OVER ITS TARGET CAN STILL BE BROKEN BY ITS DESK'S RE-MARK.**
  The capital rule holds purchases, not positions: the charge on a share is 15.75%
  (target × `RISK_EQUITY`) and the quote moves between half and five times
  fair value, and the inventory grows with the mark and never has to be sold
  down. 15 failures held at 10%, none in the other runs.
- *(small, from 0.7.8's rounds)* the desk's own buying lowers the month's
  closing quote of what it holds (`PRESSURE` × held ÷ limit, ~6% on
  `BankCheck`'s fixture), so a desk bought to its limit can end the month a
  hair under target; Luxury Retail pays dividends while losing money and
  borrowing ($32M in a month with −$206M before tax); a bank recapitalised to
  exactly `paidInPerBranch` sits on `excessCapital() == 0`, where ULPs flip
  PAYING and RETURNING — harmless in money, and where round 3's probe and the
  shipped tree parted; `ui/SectorScreen`'s leverage colours use their own
  0.5/0.6/0.7 (~L445, ~L1250), not the model's 0.9 and 1.5; the branch
  advisor's income (`BusinessInvestment.estimatedMonthlyProfit()` ~L797)
  prices a branch's book at the planning sector's quote, not at prime;
  `Game.consider()` still files `canFundProject()`'s leverage refusal as a
  land shortage (`landBlockedSectors`) — only the capital refusal is split out
  (see the NEVER CLEARS item).
- **FOUND BY 0.7.9'S IMPLEMENTER, NOT ITS TO FIX:** the Summary's THE BANK
  flag (`ui/SummaryScreen` ~L738) reads only strain ("n% lent"), not the
  capital rule that rations credit since 0.7.8 — `bank.status()` or
  `payoutStance()`; every other screen's opened lines still snap shut on each
  redraw — `Statement.opens()` is a one-line `Set<String>` field for
  `SectorScreen`, Finances and the rest; `HistorySave` records
  `bankCapitalRatio` as 10 when nothing is lent, so those months sit at the
  chart's ceiling and count as not under target; `Bank.status()` for a failed
  bank names 12.0% and leaves out `resolutionExitEquity()`'s one-branch floor;
  `Bank.netInterestMargin()` calls its denominator `weighted` and divides by
  the face book (the value is right); `Game.recapitaliseBank()` puts in
  `min(amount, cash)`, so a caller can make a partial rescue — whether the
  advisor does is unchecked; `BankCheck` §13's played city borrowed nothing for
  its families in 72 months, so their interest line is caused on a fixture.
- **FOUND BY THE 0.7.8/0.7.9 DOCS PASS:** ~~the Bank tab's landing rungs, the
  Lending page's quotes grid and the sector screen's rate note printed the
  quarter's risk spread beside the month's leverage and default rate~~ —
  fixed at the gate before the deploy: the three print the quarter's leverage
  and its default rate (`BusinessDebtManager.getQuarterDefaultRate()`, new,
  in `ReadPathCheck`'s sweep), and the grid's note says the rate is at the
  leverage over the last quarter.
  `ForeignCheck`'s "the same programme costs the same in the world's money"
  counts the material built from scrapped plant since 0.7.8 — a measurement
  change, not a premise change (the import bill alone read 1.0607, counted
  whole 0.9945, and 1.0034 with the sale switched off): review it, as 0.7.7's
  trailing-year change is. The repository's three mentions of the project's
  name (`CLAUDE.md`, `docs/notes/README.md`, `docs/docs-pass.md`) were brought
  to "Civic Ledger: Java Game" at the gate.
- ~~**THE BANK AS A BUSINESS, BATCHES 2 AND 3 — BUILT IN THE CLOUD, HELD ON
  JERUS'S ANSWER**~~ — done 2026-09-24: Jerus chose C (a sector's default
  partial); built as 0.7.8 and 0.7.9 over four rounds and deployed as tag
  0924a — see `a-sector-is-many-firms.md` and `the-bank-tab.md`. The safety
  copy in `Claude outputs/bank-0.7.8-0.7.9-held/` is superseded and can be
  deleted. Rescue for shares, and the government bidding on the exchange,
  stays a later batch.
- **HELD AT 10%, SEED 2'S PRICES RAN TO 5.64× FOUNDING.** The recovered bank
  paid savers its chosen 3.5% over the world's 2%, the households brought
  their savings home (66% abroad to 0%, US$2.7bn into a city of about 2,000,
  months 2,280–2,580), and the currency and prices moved with it before it
  unwound (1.16 at the end). 0.7.3's channel; the root is the deposits the
  bank counts and does not hold — later work. Note §6.
- **THE EARLY-GROWTH GAP IS NOT EXPLAINED** — 26,733 against 33,350 at month
  ~1,300. Not the investment hurdle (measured both ways); the untested lead is
  Real Estate starting its plans with less of its own cash (0.03 of a unit's
  cost against 0.07), which fits the 1% fee coming out of every loan. Note §4.
  *(0.7.8's default eight reach 39,676 at month ~1,300, past 0.7.6's 33,350 —
  the gap is gone, and still unexplained.)*
- **A SKILLS TRAP, PATH-DEPENDENT** — seed 3 on the intermediate build stalled
  at 21,333: growth paused, arrivals stopped, the skilled share fell 0.58 →
  0.11 and Manufacturing and Automotive refused every plant they could not
  staff. The playtest never builds a school (the §2 line); the final build does
  not enter it.
- **The bank's running costs are 0–7% of its revenue against a real 49–60%,
  and its fees 0–6% against ~39%** — its payroll is tiny against what it
  lends. Left as measured.
- **The city's floor sits above the dial in about half of seed 0's months**
  (`DebtManager` ~L1365): the bank's blended cost of funds is near zero, so
  the floor is "the dial or 1%"; `MIN_RATE` never binds.
- **`Equity`'s share drift** — the households hold a few hundredths of a share
  more than the register (627,527.663 against .618 on seed 0 from month
  3,471); the dividend is guarded at the division, the source is open (the
  register and the cells are moved separately).
- **`ForeignCheck`'s "outflow swamps trade" reads the trailing year** since
  0.7.7, because a one-off $14.7M import shipment landed in the fixture's last
  month — a measurement change, not a premise change. Review it.
- *(small, the note's §5)* `BusinessDebtManager` ~L77/~L326: a sector's rate
  is an absolute 1% (`MIN_SPREAD`) until it is first priced *(since 0.7.8
  `MIN_SPREAD` is gone with the leverage spread and an unpriced sector reads 0
  until the month's first pricing — the same gap, a different number)*;
  `HouseholdAccounts` ~L1088: `getRowSpending` leaves out fares;
  ~~`Bank.redenominate` does not rescale the saved last-month figures; the
  bank's month flows, its fees included, are not saved (a reloaded Income page
  reads zero for a month); `Game` ~L4282: a failure caused by the late items
  shows a month late~~ — done 2026-09-24 (0.7.8); `LongPlaytest` ~L2685: the carry figures are multiplied
  by a thousand twice; seed 3 has one month of negative GDP (m1012); seed 4
  has households with no home for sixteen months (749–765).
- ~~**FOUND BY THE 0.7.7 DOCS PASS — four player-facing strings still describe
  the strain premium**, and **`CapitalFlows.arrivalsAt()` has no caller**~~ —
  done 2026-09-24 (0.7.8): `Inbox`, `BusinessInvestment` with `BankCheck`'s
  reading, `YearBook`'s `bankStrain` and `UserInterface`'s tooltip say what
  is true, and `arrivalsAt()` is deleted.
- **LAND BOUGHT BY CONVERTING PUTS NO PUSH ON THE CURRENCY — open, Jerus's
  call** (`land-in-dollars.md` §5). The 0.7.6 brief asked that "whatever
  pressure the exchange puts on the rate when the treasury buys dollars
  applies here too", and there is none to apply: a treasury purchase of
  dollars is the financing item below the line (`MoneyAudit`
  `Scope.RESERVE`), which `pressure()` and `monthDeficitUsd()` never read, so
  buying reserves has never moved the rate and `ForeignCheck` §14's equality
  is an equality with nothing. Right for reserves (the dollars stay in the
  country); arguably wrong for land, where the world sold the city an asset
  and was paid in dollars the city had to buy — a city that spends its way
  through a decade of land sees no weakening for it. The fix, if wanted, is
  small: count the dollars paid abroad for land as an outflow the pressure
  signal reads (an import-like term in the trailing balance, or a
  capital-account term of its own), for the converting path only or for both.
- **Four more, found by 0.7.6 and not its to fix** (`land-in-dollars.md` §5):
  land is always bought between presses, outside every audit window, and
  `MoneyAudit` has never declared land, so nothing asserts the moment of
  purchase (the month after is asserted); a reload clears the month's open
  land line, so a save made between presses loses that month's land from the
  budget, as it always did — the dollar figures were kept consistent with
  that rather than fixed separately; the central bank counts the vault as its
  asset, so land paid from the vault lowers its equity with no line saying
  why (selling reserves already does the same); a pre-0.7.6 save's
  `landPrice` months stay in the old unit if the player reforms the currency
  after loading.
- ~~**THE COST MODEL CANNOT PRICE A BUILDING WITH TWO OUTPUTS.**~~ **FIXED
  2026-09-15 by `Sector.costShareOf()`, which splits a line's joint costs across
  its outputs by relative sales value — and it was never only a BUILDING problem:
  Business Services (3 goods) and Manufacturing (2) had been charging each of
  their goods the whole line's bill since they shipped. See the top entry.** The
  original note, which is what the fix was written against:
  `Sector.getCostPerUnit()` and `getMarginalCostPerUnit()` both divide the whole
  line's payroll, electricity, water and input bill by **one** good's output, so
  a plant with two outputs is charged its entire cost twice over — once against
  each — and neither clears its own marginal cost. **Measured 2026-09-15: 858,000
  kg of nameplate capacity and zero produced**, while the city imported the good
  beside it. Every one of the 55 buildings makes exactly one good, so nothing has
  ever exercised the path. **The fix is to split a line's costs across its outputs
  by revenue share**, which is a change to the cost model all ten sectors use and
  wants its own batch and its own sixteen seeds. Until then a template with two
  `makes` entries silently produces nothing, and `AgricultureCheck` asserts the
  ovens have exactly one.
- **TWO PLACES NAME WHAT A SECTOR MAKES AND NOTHING CHECKS THEY AGREE.** The
  sector's own `makes(Good)` list decides what `Markets` brings to market; the
  buildings' `makes(Good, n)` decides how much. On 2026-09-15 `FoodIndustry` said
  FOOD while its ovens said BREAD and **the good simply did not exist** — no
  exception, no log line, 858,000 kg of capacity producing nothing. A harness
  asserting that every good any template makes is declared by its sector would
  cost ten lines and catch the whole class.
- ~~**WAGES ARE INDEXED A THIRD ABOVE THE PRICE LEVEL THEY CHASE.**
  `COST_OF_LIVING_PASS_THROUGH` is 1.0 and `LabourMarket.updateCostOfLiving()`
  drifts `costOfLiving` toward `livingTarget = priceIndex` at 1/24 a month, so
  over 240 months the two should converge. Measured 2026-09-15 on a played city:
  **costOfLiving 2.648 against priceIndex 1.993**, with band multiples at
  0.71–1.17 so no scarcity premium is involved. That is a free real wage gain
  with no productivity behind it, compounding for the life of a run, and it is
  the single biggest reason the city's households look rich against their food —
  the *poorest* cell holds $7,660 a head a month against an average Canadian's
  ~$2,665. **Not chased yet; it is the next batch.**~~ — closed 2026-09-21 without a fix: it does not reproduce on today's code. `LabourCheck`'s "wages against the index" holds `costOfLiving` to the published index a `DRIFT_PER_MONTH` at a time through 240 months of steady inflation, a currency reform and a reload; on all eight seeds every one of the 144 checkpoint readings equals the lag-implied level exactly (`-Dplaytest.wages`), wages over the index between 0.84 and 1.12, above one only while the index is falling. See `the-bank-that-never-paid.md` §2.
- ~~**THE BANK NEVER PAYS FOR THE CITY'S PAPER.** `Game.java` ~L3601:
  `cityDebtRaisedForBank = cityDebtRaisedThisMonth` runs after the top-of-tick
  clear (~L3562), so `bank.lend(...)` (~L3625) receives 0 for every city bond;
  the bank's `cityBook` still rises and it later receives the principal, so
  after a $20M issue the bank's cash fell only by its ordinary month.
  Invisible to `MoneyAudit` because issuance is between windows. Found
  2026-09-19 while the treasury bridge was opened; a behaviour fix, Jerus's.~~ — done 2026-09-21 (0.6.11, ships with batch B): the settlement is snapshotted before the top-of-tick clear and zeroed once the bank has paid; what it still owes is carried against its pool in `MoneyAudit` (`Game.getCityPaperUnsettled()`) and saved under two `DataSave` keys. `BankCheck` §10, which fails four ways on the old order. See `the-bank-that-never-paid.md` §1.
- **TWO BUDGET LINES THE BALANCE OMITS.** The city's own repair bill in
  spending and the transit fares in revenue — the Government screen lists both,
  `NationalAccounts.getTotalRevenue()` / `getTotalExpenses()` carry neither —
  so the surplus figure, the ring's total, the Spending page's total and the
  `surplus` series are off by them ($2.56M a month in `SaveFileCheck`'s city);
  the bridge's last row had held exactly −repairs +fares every month.
  Journalled by name for now; the fix is two lines in `NationalAccounts` and a
  save slot, and then the two `record()` calls come out. **Transit wages are
  paid by nobody** beside it: `EconomyManager.getExpenses()` has no
  `transitBill`. 2026-09-19.
- **THREE THINGS A NEW CITY OR A REFORM DOES NOT RESET OR SCALE.**
  `buildWorld()` does not reset `treasuryRecorded` and its siblings (a second
  new game after a played one opens its first window at the old closing
  balance); the reform does not scale the carried bridge fields; `LandManager`'s
  month counters are not saved. And a new city's first treasury window opens at
  the top of the first tick, so founding purchases show as a residual once.
  2026-09-19.
- **THE DEALER BIDS AT ITS OWN BUBBLE PRICE.** The desk quotes up to 5× fair
  value to draw sellers when it has unfilled demand, then pays that bid to
  emigrants and marks the shares at fair the same day — most of "the trading
  desk is losing billions". A bid that tracked fair value while the desk is a
  net buyer (ask left where demand puts it) would close most of it without
  touching the conservative mark; one constant and a harness section, measured
  on eight seeds. Jerus's call, 2026-09-19.
- **A HAIR OF BORROWING IS A DISCRETE STATE.** `Household.fundShortfall()`
  clamps a negative `still` but a positive 1e-21 becomes a debt, and
  `HouseholdBalance.investAbroad()` gates on `debt <= 0`, so a rounding
  difference between a city and its reformed twin flips whether a household
  may hold money abroad (the twins parted a decade later on `interest 2.36e-21
  vs 0.0` in one cell). Why the care relief within a row follows heads rather
  than paying heads. 2026-09-19.
- ~~**A FULLY-WRITTEN BRAKE THAT NINE SECTORS IGNORE.**~~ **Stale as of
  2026-09-18, found by the manual pass: `Game.consider()` (L1905) applies
  `servicesItsOwnDebt()` to every sector's decision and declines with "not even
  one would cover its interest", so it is wired for all fifteen. The original
  note, kept because the second half — whether it should be a whole-economy
  change with sixteen seeds — was never measured:**
  `BusinessInvestment.servicesItsOwnDebt()` — "if the new capacity cannot
  out-earn the interest on the money that built it, by a margin, the business
  declines the project even though the lender would fund it" — had **zero
  callers** from the day it was written until 2026-09-13, when Agriculture became
  its first. Every other sector still builds anything with a positive
  profit-over-cost score whether or not it can service the debt. It has never
  mattered because land is a rounding error everywhere else; a Steel Foundry is
  $30m of plant on nine tenths of a block. It mattered enormously to a $312k barn
  on six blocks. **Either wire it up for all ten or delete it** — and wiring it
  up is a whole-economy change that needs sixteen seeds either side. See
  `farms.md` §5.
- **FIFTEEN PLANNERS, EACH ASSUMING THE GROUND IS FREE.** *(Ten when written.)* The fields plan off the
  crop market, the houses off the jobs, the fabricators off the steel price, and
  none of them can see that they are bidding for the same land. The rule that
  stopped the farms crowding out housing (`Agriculture.plan()` refuses while
  anybody is unplaced) is a patch on one sector, not a mechanism. As more
  land-hungry sectors arrive this wants a real answer: a land market the planners
  bid into. Two of the last three sectors have been land-hungry.
- **`Game.isPrivateInvestmentLandLocked()` NEVER CLEARS.** `landBlockedSectors`
  is never emptied, so once a sector has been blocked once the flag stays true
  for the rest of the run — true at month 12 with 1,974,000 sq ft free and still
  true at month 300. The "nowhere to build" inbox notice is very likely firing on
  cities with plenty of ground. Found 2026-09-14, not fixed.
- **A DERIVED FIGURE WITH TWO COPIES DRIFTS, AND THE COPY THE PLAYER SEES IS THE
  ONE THAT ROTS.** `UserInterface.historyValues()` kept its own unemployment and
  average-wage arithmetic beside the model's, and the student correction made in
  the model on 2026-09-06 never reached it — so the People screen and the Reports
  chart quoted different rates for the same city until 2026-09-15. The four
  definitions live in `YearBook` now and both readers delegate. **Anything else
  the UI recomputes from two history series is the same shape**, and the rule is
  the one `PopulationManager.getUnemployed()` already carries: one definition,
  where the model keeps it.
- ~~**THE CURRENCY DRIFT FEEDS ON THE IMPORT PRICES IT SETS.**
  `ForeignAccounts.repriceCurrency()` moves the rate by the city's own inflation
  (`(localInflation − worldInflation) / 12` a month), and in a city whose basket
  is imports that inflation is the rate's own rise. Slot 3's founding went to
  the 100× ceiling on it; a probe reproduces a permanent doubling of prices from
  one heavy founding order and a 9%-a-year spiral from a broke one. Design, not
  a bug — see the top entries. Found 2026-09-15.~~ — closed 2026-09-22 (0.7.2):
  the drift is deleted (`ForeignAccounts`, THE DRIFT THAT WAS DELETED); the
  rate's monthly push is the trade balance and the real rate.
- **AN ARGMAX OVER QUANTITIES THE MODEL PRICES TO BE EQUAL IS A STEP FUNCTION
  DRIVEN BY DUST.** `Exchange.buyForHouseholds()` sorted companies "best yield
  first" and gave the winner the whole month's unmet demand — but
  `Equity.priceOf()` values an earnings-valued company at exactly the discount
  rate, so every such company ties to the last bit and the sort was choosing
  between copies of one number. A company's quote tripled in a month on a
  rounding error, once in roughly 200 months of buying. Fixed 2026-09-15 with a
  relative dead band and a pro-rata share of the unmet demand; see the top
  entry. **The general form is worth a sweep: anywhere the model computes a
  winner, ask whether its own rules can make the candidates equal.** *And
  2026-09-16 found the same shape without any argmax at all: a BOOLEAN whose
  input is a residue. `c.debt <= 0` decided who may hold money abroad, and four
  cells were carrying ±1e-29 of debt out of the household rebuild's own
  rounding. **Anywhere the model asks a yes/no question of a quantity that can be
  arithmetic dust, the answer is luck.** See the top entry.*
- **A STATE ARRAY'S WIDTH MUST COME FROM THE SAVE, NEVER FROM THE READING
  BUILD'S OWN COUNT OF THINGS.** `PopulationCohorts.restore()` derived it from
  the build's band count, so the day `AgeBand` grew, every existing file would
  have been read at the wrong offsets — births as people over 85, deaths as
  births — and **passed every guard**, silently and for ever. Fixed 2026-09-15 by
  saving the band names beside the values and mapping by name, which is what
  `equityKeys` and `householdCellKeys` have always done for the sectors and the
  household cells. **The sectors were safe and the pyramid was not**, and the
  pass over the rest of `DataSave` that this bullet asked for was run the same
  night and **found two more of the same class**: `FamilyModel`, which would have
  restored NOTHING — orphans, household matrix and formed-household memory —
  from every save on disk, and `Sickness`, which would have refused and silently
  reseeded every existing ring. All three read against one `bandNames` field
  now. **The extended rule: after finding one, audit for the rest BEFORE changing
  the enum**, because fixing the array that prompted the question makes the
  question feel answered. *And the split that followed added a third form the
  audit missed: a site that is band-NAMED rather than band-INDEXED —
  `RetiredHousehold.grownUps()` asked for members of SENIOR and quietly gave the
  over-85s no pension. **Ask the band whether it is retirement age; do not name
  one.***
- **NOBODY LENDS THE MONEY THAT EXISTS, and it is now measured.** *Half of it
  moves now: since 2026-09-10 the idle cash goes abroad for the world's rate
  (`outward-investment.md`), which recycles the surplus and prices the
  currency, but it is still nobody's loan and nobody's dividend — see "THE
  HOARD" in section 2.* On Jerus's
  month-1124 save: Construction, Heavy Industry and Mining hold **$20.2 billion**
  of cash between them and owe **nothing**, against a $49.7M monthly GDP.
  Real Estate, Retail and Industry owe **$2.39 billion** at ~7% and both of the
  loss-making sectors lose money *below* the operating line, purely on debt
  service. Households hold **$2.3bn of savings and $0 of debt**. Nothing in the
  model moves corporate cash to corporate borrowers. **This is the largest
  unexamined structure in the economy.** *The 2026-09-10 note that "at 1.00 the
  bank lends nothing" turned out to be measuring the wrong thing — the ceiling
  and the trigger were one constant. With a gap the bank keeps a book. But the
  audit batch ends the run with three sectors holding $24.6bn of cash and owing
  nothing while three others sit at −$23.5bn, which is this item restated with
  the credit system fixed. Still the largest unexamined structure. And since the
  template even the landlord repays: `bizDebt $0k` from month 1,800 on every
  seed.* *And measured again on 2026-09-14 against the cost-of-funds floor: the
  city's savings are about 200,000x what its productive sector wants to borrow,
  so the answer cannot be to invent credit demand — see "nothing borrows below
  cost".* *Slot 3 (2026-09-15): deposits pinned at `branches × 250,000` for
  seventy years, $9.25bn gathered of $685bn saved, because `wantsBranch()`
  builds on lending strain and there is none.*
- ~~**Rent is a price, not a market.**~~ **Done 2026-09-08**, rebuilt 2026-09-09
  into two markets, and finished 2026-09-10 when the floor stopped flattening
  them into one. See `the-price-that-stopped-being-a-price.md`.
- ~~**Retail's till comes out of a currency reform wrong.**~~ **Done overnight
  2026-09-09.** **Sixteen** bugs in that family, all the same shape. *Seventeenth
  found and fixed 2026-09-10 late: the two segment housing costs in
  `CommercialHandler.redenominate()`. Eighteenth, same night: the pension
  screen. Nineteenth, 2026-09-11: the bank's resolution floor read
  `PAID_IN_PER_BRANCH` in the founding unit, so a reformed city's bank never
  left resolution — found by `DenominationCheck` the first time a bank earned
  its way out.* ~~**And a candidate twentieth, 2026-09-14, in the history rather
  than in the state:** slot 3's early years read as a price index of 227.3 and a
  currency 100x from parity, which is the shape of a reform that redrew some
  history series in the new unit and left others in the old one.~~ **Not a
  twentieth — answered 2026-09-15.** Slot 3 never reformed; the early years are
  a real currency collapse to the rate's ceiling, and the history is right. See
  the top entries. *The twentieth of the wider family — a step function decided
  by dust rather than a constant in the wrong unit — arrived 2026-09-15 as the
  exchange's yield tie. See the argmax bullet above. **The twenty-first arrived
  2026-09-16 and is the purest of them: `HouseholdBalance.moveStock()` handing a
  cell a negative slice of the pool out of `weight * g / g > weight`, and
  `investAbroad()` reading the sign of what was left. Four retired cells owed
  minus 2.7e-29 and the city's exchange rate turned on it.** And a twenty-second
  beside it, an ordinary unseeded money constant: `investAbroad()`'s `1e-12`
  sweep, the fifth of that family — found while chasing the twenty-first, not
  its cause, and fixed anyway.*
- ~~**Vacant housing costs nothing to hold.**~~ **Done 2026-09-09.**
- **The rate schedule was calibrated around an accidental export levy.** Zero-
  rating steel was right and costs the city about $4M a month net. Nothing is
  broken, but a rate or a spending line probably wants to move.
- **`rentPerHousehold()` is not the per-let-door rent, and the Real estate screen
  calls it one.** One line to fix, either the label or the divisor. *And since
  the cells, it is the rent every household is charged — section 2's "rent per
  cell should follow the door".*
- **The investment planner ignores every utilisation ratio** in five of six
  sectors. Real estate is the exception and is the pattern to copy. (L3)
  *The template's maker rule reads the sector's operating rate into the
  estimate (`estimatedMakerProfit`); the planning itself still does not.*
- **Shopping is a headcount, not a budget.** Retail demand is
  `min(coverage, population)` with no reference to what households can afford.
  *This is probably half of the retail item in section 2.*
- **Retail's trend estimator is still wrong, just bounded.**
- **One sector can claim a year of the whole city's builders.** (H1)
- **Construction capacity splits per stack, not per work remaining.** (backlog 2)
- ~~**Why is a Construction Materials Plant almost never built?** Twice in 4,000
  months, against 58 depots. (D1)~~ *Answered twice. At $2,000 a unit it made
  $800k of material a month with $969k of wages. Since the template the plant
  is the seventh sector's, built when the builders' order book says one would
  sell — and a mature city's building is bursty enough that it seldom does.
  See the template block in section 2.*
- **Roads are the new ceiling.** *98–100% on the latest playtests, which is much
  better.* The advisor still only ever builds Paved Roads. (G1)
- **The churn under a pinned unskilled surplus is now honestly measurable.**
- ~~**The Food Processing Plant**~~ — closed on 2026-09-10 as "not too big,
  under-producing", output x5 with jobs unchanged; reopened the same night for
  granularity; **closed again that night at a fifth of the size**, every figure
  in proportion, and the plant idles rather than flood its own price. What is
  left is the planner: it projects customers to the reachable population, so a
  growing city builds nine plants for a demand two would meet and sheds them
  slowly. Bounded now, not urgent, and the "planner ignores every utilisation
  ratio" item below. (backlog 10)
- ~~**The Commercial Bank employs 69 people.**~~ **29 as of 2026-09-10** — US
  commercial banking runs 29 staff per branch, and `DEPOSITS_PER_BRANCH` moved
  with it, $60M → $253M. It had already come down from 278 once; that correction
  was measured against a single month, and over 1,202 months the wage bill was
  still 180% of revenue.
- **No honest "cost of carry" for debt.** Needs `Debt.amortisedMonthlyCost()`.
- ~~**Construction now pays sales tax for the first time.** Still a decision
  nobody has made~~ — **made 2026-09-10 (night)**: it pays sales tax and profit
  tax, like the other five.
- **The founding endowment bills for care it costs nothing to provide.**
- **`squeezeUnplaced()`'s two valves count households, not doors.** Found
  2026-09-10 while fixing the crowding floor: given no doors at all it will still
  report every household placed, because flatshares and doubling are pure
  arithmetic with no reference to whether a door exists to double into. It does
  not currently bite — `crowdingFactor()` now asks the placement rather than the
  formula — but it is the same blindness one level down.
- **`capacityWith()` and `depositsGathered()` disagree about whether hot money is
  subject to the branch cap** (`Bank.java`). The second exempts it, under a
  comment saying the asymmetry is the point; the first re-derives the limit and
  applies the cap to all deposits. So a bank funded mostly by hot money reports
  that another branch is worth nothing and never wants one. Latent — no run has
  held a persistent hot-money stock — but live the moment one does.
- **The spread clamp binds where the risk is.** `spread = .01 + .06 × leverage`
  capped at `.08` saturates at 1.17× assets; with the shortfall ceiling now at
  0.9 it no longer binds for shortfall borrowers, but an investment borrower at
  1.5× is quoted the same as one at 1.17×. Lesser now. Do not fix it by raising
  the cap — measured: a 12% cap made the bank poorer, because the borrower paid
  the extra interest by borrowing it.
- **Households pay no power or water bill.** Found on the template (2026-09-11)
  when the landlord turned out to be paying its tenants' showers: residential
  buildings are excluded from the utilities billing now and nobody is billed
  for what the homes draw. A household bill is a basket line when the basket
  grows.
- **A HOUSEHOLD WITH $201,363 PUT BY CAN STILL DEFAULT $390 SHORT.** Read off
  slot 3 at month 2305: every other cell sells its paper abroad and then its
  shares before it borrows, and this kind appears not to be wired into that
  draw-down path at all. ~~And **a prisoner's $70 student loan was not
  frozen** — a prisoner's debts are supposed to be, and the student loan is a
  fourth ledger that was missed.~~ — done 2026-09-21: it was frozen all along
  (nothing collects a student loan except from a working family; the $70 was
  the balance carried in), and the freeze is `PrisonerHousehold`'s own now, no
  instalment and no interest, asserted in `EducationCheck` §14. Found
  2026-09-14; the $390 default is still open.
- ~~**THE DEBT'S MONTHLY REVALUATION IS NOT SAVED.** `ForeignAccounts.lastRevaluation`
  (~L825, struck in `takeForeignDebt()`) is not in the save array, so after a
  load the trade and finance pages' "the currency moved it by" line reads
  nothing until the month turns. The vault's own revaluation is saved (slot 23)
  since 0.6.10. Found 2026-09-21, `a-reserve-defends-a-currency.md` §7.~~ — done 2026-09-21 (0.6.11): slot 24 of the foreign accounts' array, asserted in `SaveFileCheck` §12b; `SAVE_FORMAT` stays 27.
- ~~**A SCREEN WRITES TO THE MODEL.** `TradeScreen.currencyForcesPage()` (~L1320)
  calls `fx.effectivePressure()`, which writes `lastPressure` and
  `lastAbsorption`. Harmless today — same state, same values — but the
  interface is meant to read the model through getters and nothing else.
  `ForeignAccounts.reset()` also leaves `rateDifferential`, `localInflation`
  and `worldInflation` from the previous city until the first month turns
  (the simulation is unaffected). Found 2026-09-21, same note.~~ — done 2026-09-21 (0.6.11): `TradeScreen.currencyForcesPage()` reads `ForeignAccounts.previewPressure()`, the same arithmetic without the writes (`ForeignCheck` §13, `ReadPathCheck`). ~~**Still open from the same line:** `ForeignAccounts.reset()` leaves `rateDifferential`, `localInflation` and `worldInflation` from the previous city until the first month turns.~~ — done 2026-09-22 (0.7.1): `reset()` clears the three.
- ~~**A DEFENCE THAT SPENDS RESERVES — for 7.0.** Absorption costs nothing: the
  vault damps a push weaker in proportion to its cover and is never drawn down
  by doing it, which is why the default player never sees it work. A defence
  that sells dollars to hold the rate belongs with the money supply. Same
  note, §8.~~ — done 2026-09-22 (0.7.2): the central bank sells `absorption()`
  × the month's own deficit of the vault, at most the vault, against a push to
  fall; a capital transaction, equity down and M0 unmoved (`CurrencyCheck`
  §3–4).
- ~~**THE EMERGENCY NOTE'S FREE MONEY WAS A FLOOR — THE ADVANCES CEILING MOVES
  INTO BATCH B.** Held at a 10% policy rate (`-Dplaytest.policyRate=0.10`),
  six of eight seeds run their treasuries dry around months 2,800–3,800,
  before the fix and after it. Before it, all six survived on emergency notes
  their bank was handed free, peaking at $0.9–6.1B owed at about 8%. After
  it the bank really funds them, its strain explodes, the notes price at
  27–36% (policy, spreads and the 18-point premium) and the debt compounds
  without limit: $193 quadrillion on seed 0, NaN on seed 6, bank failures
  16 → 161. The overdraft as advances from the central bank with a ceiling
  (`the-central-bank.md` §6) moves from batch C into batch B, and 0.6.11
  ships with it, not before. The same pathology as **THE TREASURY'S
  OVERDRAFT HAS NO FLOOR** below.~~ — done 2026-09-21/22 (0.7.0): the emergency note is
  retired; a broke treasury is advanced its gap by the central bank at the
  policy rate, up to `CentralBank.MAX_ADVANCES_MONTHS` (six) of trailing
  revenue, and past that Jerus's rule, "pay promises first, cut the rest",
  through `Game.treasuryPays()`. Held at 10%, the six seeds end owing
  $0.1–2.0B, finite, the audit closed every month (`CentralBankCheck` §5–6).
  See `the-central-bank-opens.md`.
- ~~**A BOND BUYBACK PAYS NOBODY.** `Game.repurchaseDebt()` (~L6663) takes the
  price out of the treasury, and the bank's book drops by the principal at
  the next refresh with no cash arriving: a $20,000k buyback cost the bank
  $20,067k of equity. Before 0.6.11 a round trip cost the bank nothing,
  because it had paid nothing; now it loses what it paid. The holders
  (`the-central-bank.md` §5) are who a buyback pays.~~ — done 2026-09-21/22 (0.7.0) for the
  city's own paper: `Bank.sellPaperBack()` takes the price as cash and drops
  the book by the principal at once, the difference its gain or loss
  (`CentralBankCheck` §5). The dollar case stays open — the new line below.
- ~~**THE BANK'S BOOK INCLUDES THE CITY'S DOLLAR DEBT.** `Game.refreshBank()`
  hands `bank.refresh()` `debtManager.getAllPrincipal()` (~L1555), which
  counts foreign paper as well as domestic (`getDomesticPrincipal()` is the
  one without), and the weighted loop beside it (~L1576) walks the same list:
  paper the bank never bought. `BankCheck` ~L435 builds the error in ("the
  city book IS the treasury's principal").~~ — done 2026-09-21/22
  (0.7.0): `Game.refreshBank()` hands the bank `getDomesticPrincipal()` and
  the weighted loop skips foreign paper; `BankCheck`'s line is "the city book
  IS the treasury's principal at home".
- ~~**A TERM BOND'S WHOLE DISCOUNT IS BOOKED AS INTEREST IN ITS SETTLE MONTH.**
  `bank.takeDiscount(cityDiscountForBank)` (`Game.java` ~L3747): seed 0
  borrowing at home sold $148.3M of face for about $88M, and some $60M of
  "interest" landed in one month, taxed, 45% of it payable to savers. It
  should accrete over the bond's life. Batch C, with the curve.~~ — done
  2026-09-22 (0.7.1): each piece of paper carries its own discount
  (`Debt.getIssueDiscount()`, `getDiscountLeft()`) and accretes it
  straight-line over its life; the bank earns its share a month at a time and
  carries the rest as unearned against its book (`Bank.setUnearnedDiscount()`,
  re-derived at every refresh); `BankCheck` §10.
- ~~**A POLICY RATE OF EXACTLY 0% RELOADS AS 3%.** `Game.java` ~L7333 restores
  only a positive rate (`if (loaded.getPolicyRate() > 0)`).~~ — done 2026-09-21/22
  (0.7.0): `DataSave.policyRate` is boxed and null is the save without the
  key (`CentralBankCheck` §7).
- ~~**THE TREASURY'S OVERDRAFT HAS NO FLOOR, AND COMPOUNDS TO NaN.** Found
  2026-09-21 by the schools ensemble's first draft: a founding village handed a
  University ($210M, $620k a month of upkeep, on a town of three hundred
  making $1.8M a month) went −$286M by month 263, −$1.4T by 801, −$1,378T by
  1,033 and NaN by 1,632 (control seed 1). The fixture was made proportionate;
  the pathology is not fixed. What should stop it — a borrowing limit, forced
  austerity, a default — is a design question. See `the-price-of-a-place.md` §7.~~ — done
  2026-09-21/22 (0.7.0): the advances ceiling and the arrears rule are the
  floor, and the design question it asked (a borrowing limit, forced
  austerity, a default) is answered by Jerus's rule.
- ~~**NO HARNESS CATCHES A DOUBLED CALENDAR.** A `month++` left twice at the top
  of `nextMonth()` passed the whole suite during batch B; only the playtest's
  month numbers gave it away. `CalendarCheck` should assert that one press is
  one month. Batch C. Found 2026-09-21, `the-central-bank-opens.md` §4.~~ —
  done 2026-09-22 (0.7.1): `CalendarCheck` §7 — one press is one month, five
  months of a skip five, and `SimulationEngine.simulateMonth()` none.
- ~~**THE CONSTRUCTION RETAINER IS NEVER PAID.** `Game.constructionSubsidy`
  (~L663) is set, saved, reset and reformed, and no line of the month reads
  it; the standing policy replaced it. The docs pass marked the two comments
  that said otherwise (THE CONSTRUCTION SUBSIDY and `buildWorld()`); the field
  itself is batch C's — pay it or remove it. Same note.~~ — done 2026-09-22
  (0.7.1): removed — the field, its save key and its four lines; an old save's
  key is left unread. The standing policy is the lever
  (`TreasuryLine.CONSTRUCTION_SUBSIDY`).
- ~~**THE GOVERNMENT TAB HAS NO SUBSIDIES LINE.** `GovernmentScreen.spendingNames()`
  (~L799) lists none, though `NationalAccounts.getTotalExpenses()` includes
  them. Same note.~~ — done 2026-09-22 (0.7.1): `spendingNames()` ends with
  "Subsidies", read from `NationalAccounts.getSubsidies()`.
- ~~**STUDENT GRANTS LAG A MONTH.** Households are credited month N−1's bill at
  the top of month N while the treasury pays month N's; invisible to the audit
  because the households are outside the pools. Same note.~~ — done
  2026-09-22 (0.7.1): struck and paid at the top of the month on the students
  it opens with, where they are credited (`Game.nextMonth()`,
  `payStudentGrants()`); `OutsideCheck` and `EducationCheck` assert the same
  month's figure. EI still lags — the new line below.
- ~~**A DOLLAR-BOND BUYBACK PAYS NOBODY ON THE BOOKS.** `Game.repurchaseDebt()`
  (~L7070): between presses, the price leaves the treasury and no outflow
  abroad is declared. The domestic case pays the bank since 0.7.0. Same note.~~
  — done 2026-09-22 (0.7.1): the price is carried in the treasury's pool
  (`Game.getBuybackUnsettled()`) and declared the next month as `- city
  BuybackAbroad`, a financial outflow (`HoldersCheck` §8); and a domestic
  buyback pays every holder its share.
- **A FAILED BANK'S HOLE STILL GOES ABROAD — JERUS'S QUESTION.**
  `Bank.resolveIfFailed()` and `MoneyAudit`'s `+ bank ResolutionLoss` still
  declare the shortfall as absorbed from outside the city, though the bank's
  wholesale lender is the central bank's window now, and the window is repaid
  out of that inflow at the next settle. Who absorbs a failed bank — the
  central bank as lender of last resort, the depositors, the treasury — is his
  to decide; the Bank tab's resolution note carries a `TODO(docs)` to follow
  the answer. Same note.
- **THE TERM PREMIUM IS PROVISIONAL — JERUS'S NUMBERS.** `DebtManager.TERM_PREMIUM_10Y
  … 50Y` at 0.50, 0.90, 1.15, 1.35 and 1.50 points were set to have a curve at
  all; the shape (linear from a year to ten, flat past fifty) is the
  implementer's reading of the brief. `the-curve-and-the-holders.md` §1.
- **THE "SPREAD IS GONE" SELL-BACK CAN HARDLY EVER FIRE — JERUS'S QUESTION.**
  The households' yield is read off today's curve
  (`DebtManager.householdBookYield()`), which sits on the dial, and the
  deposit rate is a share of what the bank's book earned, so the paper always
  out-yields deposits and `HouseholdBalance.sellPaperForSpread()` never sells
  in a real run; `HoldersCheck` §4 exercises it by handing it a zero yield.
  Whether the rule should read the coupon locked in at issue is his. Same
  note, §4. *And 0.7.2 saw it fire: in `BankCheck` §10's settle month the
  deposit rate rose past the paper's yield (6.825% against 6.726%) once the
  empty household cells were folded, and the desk bought $1,080k back; the
  fixture's issue was halved so it holds its premise, with a line that says
  so. `the-currency-off-its-rule.md` §3.*
- ~~**A DOLLAR BOND IS VALUED AT THE CITY'S SHORT RATE** (`DebtManager.marketValue()`,
  read by `quoteRepurchase()`), and a dollar term loan is priced flat at the
  world rate at every maturity. What the world's paper is worth is open —
  batch D, with the currency. Same note.~~ — done 2026-09-22 (0.7.2):
  `DebtManager.foreignCurveRate(months)`, the foreign rate plus the same
  term-premium table, prices a dollar issue by maturity and values a dollar
  bond at its remaining months (`ForeignDebtCheck` §8); and the dollar quote
  prices its own coupons in, which closed a money pump the old pair made
  (`RestructureCheck` §5's dollar case, failing on 0.7.1).
- ~~**EI BENEFITS LAG A MONTH, AS THE GRANTS DID.** Households are credited last
  month's bill at the top of the month; the treasury pays this month's at the
  bottom. Invisible to the audit because the households are outside the
  pools. Not touched — the brief named only the grants. Same note.~~ — done
  2026-09-22 (0.7.3): struck again on the pool the month opens with and paid
  at the top of the month, where the out of work are credited it
  (`Game.payEiBenefits()`, `Unemployment.restrikeBenefits()`); `OutsideCheck`
  §7 reads the closing month and the month after. A 0.7.2 save pays one
  month's EI twice on its first month. The premiums may carry the same lag —
  the new line below.
- **A TIME SKIP HALTS AT CASH ≤ 0** (`Game.java` ~L2481, `SkipReportCheck` "an
  empty treasury"), but since 0.7.0 `settleTreasury()` advances a broke
  treasury's shortfall, so the skip stops a city the month would carry. Stop
  at the ceiling, at arrears, or not at all — Jerus's call. Same note.
- **DOLLAR-LOAN PROCEEDS LAND IN THE GAP BETWEEN PRESSES.** `foreignDebtRaisedThisMonth`
  is set when the loan is booked and cleared at the top of the next month
  before the audit's pools are read, so the inflow is in no month's window.
  It leaves no residual, and no comment says so. Same note.
- ~~**THE TRADE TAB'S FORCES PAGE LEAVES OUT THE INFLATION DRIFT** that
  `ForeignAccounts.repriceCurrency()` applies. Batch D's, with the drift.
  Same note.~~ — done 2026-09-22 (0.7.2): the drift is deleted; the page shows
  the four terms that exist — trade, the real rate, the vault's defence,
  parity.
- ~~*(small)* `Game.holderShares()` has two branches that compute the same
  thing (`hh * owed / out` and `owed * hh / out`). Harmless; one line.~~ —
  done 2026-09-22 (0.7.3): one line.
- ~~**THE HOLDINGS DIAL FORGETS ITS PACE ON A RELOAD.** Found by the docs pass:
  the load path set the dial through `setTargetShare()` from the empty bank
  `restore()` founds, so `previousTarget` read 0 for every reloaded city and
  a move saved half way through resumed at the slower pace.~~ — done
  2026-09-22 (0.7.1, before the deploy): `CentralBank.restoreTargetShare()`
  sets the dial without touching the memory; `CentralBankCheck` §14 turns the
  dial down mid-move, saves, and asserts the reloaded city steps at the same
  pace (the old line fails it two ways).
- **M0 CAN READ SLIGHTLY NEGATIVE FOR ONE MONTH** when a treasury repays every
  advance and the interest is destroyed at the top of the month and remitted
  at the top of the next; the identity holds. For the Money page. Same note.
- ~~**THE CEILING IS SMALL IN A SMALL CITY.** Six months of revenue binds within
  months of first drawing, and nearly all borrowing after that is for
  promises, past the ceiling. `MAX_ADVANCES_MONTHS` as a dial on Jerus's page —
  batch D. Same note.~~ — done 2026-09-22 (0.7.2):
  `CentralBank.advancesCeilingMonths`, 0 to `MAX_ADVANCES_CEILING` (36),
  default `DEFAULT_ADVANCES_MONTHS` (6), chips on the monetary page, saved
  under its own key, `-Dplaytest.advancesMonths` (`CentralBankCheck` §16).
  Held at 10% on 0.7.2 none of the six broke seeds draws at all.
- **THE CENTRAL BANK ENDS WITH NEGATIVE EQUITY IN ALL EIGHT DEFAULT SEEDS**
  (−$1.1B to −$3.3B; 0.7.1 was −$0.2B to −$3.7B): mainly the loss carried from
  paying interest on reserves, now plus the vault spent defending the
  currency. Found 2026-09-22, `the-currency-off-its-rule.md` §4.
- **INTEREST ON RESERVES COMPOUNDS WITHOUT LIMIT AT A HELD EXTREME RATE.** Held
  at 30% or 50%, M0 reaches 10⁸ to 3×10¹⁴ $B while prices stay near 1: spare
  cash earning the rate in new money that becomes spare cash. The same
  mechanism made 0.7.1's quadrillion at 25%. Same note.
- **MANUFACTURING'S FABRICATION WORKS FAILS THE BANK** on 0.7.2's path —
  bank failures 8 → 19 on the default eight, and Manufacturing 3 → 14 of
  them: land comes free at months 127–128, Manufacturing borrows $74M against
  a bank whose equity is $30M for a plant that loses money before interest,
  written off at month 166 and the restructured remainder at 178 in seeds
  0–3, and in some seeds it rebuilds and fails again. The fix belongs in the
  investment rule (lending past the bank's equity for a plant that loses
  money before interest; an insolvent sector rebuilding). Same note.
- **THE ADVISOR REFILLS THE VAULT THE DEFENCE SPENDS** (`LongPlaytest` ~L1191,
  the war chest): in effect the treasury pays for the defence in a playtest —
  seed 1 sold US$1.29B of a US$1B vault, and held at 50% seed 1 sold US$625B.
  Same note.
- **THE RATE TERM ACTS BEFORE SETTLING.** `ForeignAccounts.pressure()` returns
  0 before `SETTLING_MONTHS` (~L319) but `ratePressure()` does not, so the
  real-rate term and the defence act from month 1. Predates the batch. Same
  note.
- **THE CENTRAL BANK'S MONTH FLOWS ARE NOT SAVED** (`CentralBank.toSaveArray()`):
  after a reload the Money page's "this month" lines read zero until the month
  turns, and `defendedThisMonth` follows the pattern (the foreign accounts'
  own defence figures are saved, slots 25–27). Same note.
- **TWO READINGS OF THE REAL RATE.** The Trade tab's forces page reads the
  differential the last reprice was handed
  (`ForeignAccounts.getRealRateDifferential()`), the monetary page the live
  figure (`Game.realRateDifferential()`); they differ between presses when the
  dial moves. Same note.
- **`HealthCheck`'S "THE DEAR CITY IS NO HUNGRIER THAN THE FREE ONE"** compares
  one end-of-run month and passes by chance; averaged over its 96 months the
  dear city is 7.5 points hungrier. Same note.
- *(small)* `InfrastructureCheck`'s known line went green on 0.7.2's first
  build by accident (the shelves 78% against 31% instead of 100% against
  100%) and red again on the final: the fixture is sensitive to the currency.
  And for the record: the brief's UIP gaps (+3, −12) assumed a world real rate
  of +2; with `WORLD_BASE_RATE` .02 and 2% world inflation they are +5 and −10,
  and `CurrencyCheck` asserts against the constants; seed 0 does not run dry at
  a held 10% on 0.7.1, as batch B's table had it. Same note.
- **THE CURRENCY'S NEW NUMBERS ARE PROVISIONAL — JERUS'S.** `ForeignAccounts.RATE_PULL`
  4 and `CapitalFlows.MAX_SPREAD` .25 (both "Jerus's number to settle"), and
  whether the defence should answer only a crisis — the founders' billion goes
  on structural deficits, not crises: all eight default seeds sell more than
  half of it and six all but 1%, while the playtest's advisor buys it back.
  `the-currency-off-its-rule.md` §2.
- **THE FOREIGN-DEFAULT RULE READS ONE MONTH'S CASH.** Found by the docs pass.
  `Game.checkForeignSolvency()` compares the cash at the bottom of the month
  with `DEFAULT_OVERDRAFT_YEARS` of revenue, but the central bank advances the
  whole shortfall at the top of every month, so it trips only on a month whose
  own bills (a balloon falling due) exceed a year of revenue; a long
  insolvency accumulates in the advances, which it does not read. Whether it
  should read them is a design question; marked `TODO(docs)` in the source.
- ~~*(small)* `MonetaryCheck` §6 sums its own M2 (households and sectors), not
  `Game.getM2()`, which adds the world's deposits; marked `TODO(docs)`. Decide
  with batch E, when the section starts asserting.~~ — done 2026-09-22
  (0.7.3): §6 reads `Game.getM2()` and asserts that inflation falls with the
  rate — within `MEASUREMENT_NOISE`, and the 3% row `TRANSMISSION_FLOOR`
  above the 40% row; 1.032 points.
- ~~**WHO GETS 45% — JERUS'S, AND THE CHEAPEST LEVER THE CHANNEL HAS.**~~ —
  done 2026-09-23 (0.7.7): `DEPOSIT_PASS_THROUGH` is gone; the bank chooses a
  share of the dial by how it is funded (`Bank.DEPOSIT_SHARE_FLUSH` .35 to
  `DEPOSIT_SHARE_AT_WINDOW` .90), 0.37–0.44 on average over the eight seeds.
  See `the-bank-prices-like-a-business.md` §2.
- ~~**THE DEPOSIT QUOTE'S CAP BINDS BEYOND THE FOUNDING**~~ — done 2026-09-23
  (0.7.7): the cap went with the income share; the rate reported is what was
  paid, and a rate chosen under the window's is under prime by construction.
- **THE SHELF DOES NOT ANSWER MONEY** — the structural reason the channel
  cannot reach the index: `sectors/Retail.java` ~L436 (the floor,
  `OPENING_SELL_PRICE`) and ~L325 (`rWantedDemand = min(coverage, wanted)`),
  so the scarcity mark-up reads the shops' coverage and a tenth less want
  moves no price. The next design. Same note, §2 and §4.
- **THE RATE GOES NaN IN A CITY WITH NO TRADE** (`ForeignAccounts` ~L798): a
  founding without the advisor goes NaN at month 26 on 0.7.2 and 0.7.3 alike —
  `pressure()`, `absorption()` and `realRateDifferential()` read NaN first,
  and the clamp passes it through. Not traced to the root. Same note.
- **EI PREMIUMS MAY CARRY THE LAG THE BENEFITS HAD** (`EconomyManager` ~L829,
  read at `Game` ~L1078): struck at the bottom of the month, read at the top.
  Same note.
- **THE LOAD PATH'S RE-STRIKE IS NOT THE SAVED MONTH'S PLAN** (`Game` ~L4164,
  `HouseholdBalance` ~L920): the spend factor is not saved, and the load path
  strikes it on the month's closing deposit rate and index — next month's
  figures — after savings have had their deposit interest. Harmless while the
  retail capacity is carried, by the design note's reading; the docs pass
  wrote the exception into the rule's comment. Same note.
- *(small)* `MonetaryCheck` ~L282 ("the currency is not against a bound") and
  `ForeignCheck` ~L594 ("inside its bounds, every month of the way") still
  pass and now assert almost nothing, at a guard of a billion either way.
  Same note.
- ~~**THE PEOPLE SCREEN'S "EI PAID THIS MONTH" IS NEXT MONTH'S BILL.** Found by
  the docs pass: `PeopleScreen` ~L889 read `Unemployment.getBenefitsPaid()`,
  which since 0.7.3 is the bill struck at the end of the month on the pool it
  produced — paid at the top of the NEXT press.~~ — done 2026-09-22 (0.7.3,
  before the deploy): the line reads what the treasury paid,
  `EconomyManager.getEiBenefits()`; "per claimant" still reads the pool's
  figure, which is the pool's own.
- *(small)* **Three loose ends of the unpinned rate, found by the docs pass.**
  `LongPlaytest`'s player divides a dollar amount by `Math.max(.01,
  fxRate(g))` (~L1711, ~L2384, ~L2394), a floor from when the rate could not
  go under .01; `GameVersion`'s 0.7.3 note says every page's rate goes through
  one formatter, but the strip's tooltip prints parity at `%.2f`; and
  `Household.plan()`'s eating-out note ("a household that got everything it
  asked for still banks a quarter") holds as written only at a spend factor
  of 1 — above it the grocer, the counter and the table all ask more of the
  same income, and what is banked can be less than a quarter of the surplus.

---

## 5. Healthcare — what is left

The buildings, the funding, sickness, mortality, births, death care and the
warnings are all in. See `healthcare-funded.md`,
`healthcare-mortality-and-births.md` and `healthcare-panel-and-endowment.md`.
What is still open:~~ — done
  2026-09-21/22 (0.7.0): the advances ceiling and the arrears rule are the
  floor, and the design question it asked (a borrowing limit, forced
  austerity, a default) is answered by Jerus's rule.
- **No per-care-type P&L.** Fees and costs are pooled, so you cannot see that a
  Memorial Cemetery breaks even at ~11 burials a month and a Crematorium only at
  92% of its throughput — both true, both invisible.
- **The infant-mortality swing is huge in ratio and small in people**, because
  `AgeBand` puts infant mortality at 0.10%/yr, a modern figure.
- **General care does not touch teen mortality separately.**
- **CPP and pension rates as Policy-tab levers** rather than constants.
- **The birth rate at full childcare is 30 per thousand.** Canada's is 10. On
  slot 3 it is what makes a land-bound city export 200,000 people a decade —
  see the top entries. A calibration to decide, 2026-09-15.
- ~~**SENIOR CARE IS MEASURED AGAINST EVERY PERSON OVER 70**~~, while real
  long-term-care residency runs 2.0% at 70–74 and 29.6% at 85+ (2011 census).
  **Done 2026-09-15 with the split**: `CareType.SENIOR` serves both retirement
  bands and `placesPerHead()` weights them, an elder needing a whole place
  against a senior's 0.19, and `Healthcare` gives the elder band its own 1.80x
  care swing against the seniors' 1.35x. **The weighting is normalised on the
  elders rather than absolute**, deliberately — the raw census rates would have
  divided the denominator by ten and handed every existing city full senior care
  overnight. The absolute version remains a separate decision. See the top entry.
- **NOBODY IS PRICED OUT OF CHILDCARE, AND THAT IS THE DIAL'S LIMIT.** The
  fee fixture showed the working poor can afford $50 a head, so a fee that
  prices a family out of a nursery is a fee `healthFeeScale` cannot yet set —
  the cliff catches only households with nothing, a few dozen to a few hundred
  people a month at ×1. The ×15 ensemble is still to be measured. 2026-09-19.
- **THE HEALTH PREMIUM HAS NO EMPLOYER HALF AND NOTHING BALANCES IT** —
  employee side only, surplus or shortfall the treasury's, per Jerus. An
  employer share, automatic balancing against the service's cost, and whether
  the fees should index to wages (the played break-even is ×7–13 because they
  do not) are three separate decisions. Funerals are unscaled by design.
- **AN ELDER LIVING ALONE IS THE CELL TO WATCH** now, the way the senior living
  alone was: 75% of the over-85s are alone against 45% of the seniors, and the
  rent-per-cell item in section 2 charges them all the city's average door.

---

## 6. Housekeeping

- **The Services tab's "What tuition raises" table strikes each course's
  revenue on the screen** — `feeFor(course) × enrolled × (1 − subsidy)` in
  `ServicesScreen` — where `Education.getFeesOf(type)` has held it since
  0.7.6. A code change (the screen should read the getter, per the house rule
  that a screen never recomputes a model figure), found by the 0.7.6 docs
  pass and left for an implementer.

**THE STRUCTURE, after the audit of 2026-09-18** (`the-ai-ergonomics-audit.md`) —
each one Jerus's call, in the order they pay back:

- ~~**Split `UserInterface.java` along its banners, screen by screen**~~ —
  **done 2026-09-18**, see `splitting-the-interface.md` §7–12: the `ui/`
  package, the toolkit (`Money`, `Statement`, `Pieces`, `Levers`), and twelve
  screen classes (eleven tabs' plus `SummaryScreen`; Infrastructure is drawn by
  `ServicesScreen`); the window is about 4,000 lines. History and People were opened on
  the PC; the other nine were built in the cloud while the PC was off and went
  over when it came back (124 files, byte-for-byte). **Still to do on the PC:
  Clean and Build, open every tab, run a few months, press the rail.**
- ~~**Then `Game.java`, more gently**: the cars, the luxury counter, the crime
  causes, what the city eats and the currency reform each have the shape of a
  class already; `Game` keeps the seam and the month.~~ **Done 2026-09-18
  (evening) for the first four** — `Motoring`, `LuxuryCounter`, `Offending`,
  `CityBasket`, playtest byte-identical, see `splitting-game.md`. The currency
  reform stays in `Game` (it redenominates twenty-two subsystems through
  Game's own fields). What is left is bigger and entangled — the investor
  (910 lines), the foreign borrowing (423), demographics (534) — and each is a
  plan of its own, not a move; §6 of the note sizes them.
- **`unhousedShareOfCity()` is public on `Game`** and its only caller is
  Game's own health step; nothing in `ui/` or a harness reads it. Narrow it or
  leave it — found by the split, 2026-09-18.
- ~~**Mirror `todo.md`, `changelog.md` and `index.md` into `docs/notes/` in the
  repository** on every deploy.~~ **Done 2026-09-18 (evening)**: the three are
  copied into `docs/notes/` as part of every deploy from the cloud loop; the
  project is the original, the copies are as of the last deploy.
- ~~**A `Stale` tool in `tools/`** — the mechanical half of the docs pass.~~
  **Done 2026-09-18 (evening)**: `tools.Stale` + `StaleCheck` (the
  fifty-seventh harness), firm categories at 0 across the tree after the
  84-javadoc cleanup; see `the-prose-that-stopped-being-true.md`. ~~**Left for
  a reader who knows the mechanic**, found by the pass and not decided: four
  comments whose numbers disagree with their constants.~~ **Decided by Jerus
  2026-09-18 (night) — "the code is correct, the comments are the outliers" —
  and rewritten**: `foundingTuition()` says 1.20 now, `FamilyModel.restore()`
  "six lengths accepted" with the six listed, `CELL_SLOTS` ends at the meals
  eaten out, `lifetimeIntervention` names `balanceFromFlows()`. Still open
  from the same pass: `docs/dials.md` shows `Equity.BANK` with
  no sentence; and thirteen soft findings name members nothing declares
  (`treasuryUnexplained()`, `refreshCommercialReport()`, `planFromLoad()`,
  `rollIron()`, `LongPlaytest.checkMonth()`, `planRetail()`,
  `computeMonthlyReport()`, `budgetPie()`, `LuxuryRetail.sellOwnPriced()`,
  `Rail.fuelBill()`, `showMiningMenu()`, `tuitionOf()`, `getPlotsLeft()`) — run
  `Stale report.bat` to see them in place.
- ~~**THE TUITION TABLE WAS CALIBRATED AGAINST THE WAGES BEFORE THE REBALANCE.**
  Found while fixing the comment above: `Education.foundingTuition()`'s prose
  measures the university fee against "a diploma wage of 1.500", and the class
  header says a university place "costs a diploma-holder half their monthly
  income and almost nobody goes" — but stage one of the rebalance (2026-09-09)
  put `PayTier.SKILLED` at 4.500, and `affordability()` reads the live band
  wage. Against 4.500 the fee is 27% of a month unsubsidised (well inside
  `MAX_BURDEN` 0.60, so half could pay with no subsidy at all) and 11% at the
  default subsidy, so the poverty trap the header calls "the most interesting
  thing on this page" has been mostly quiet since the rebalance. Either the
  fees scale with the wages (about 3x, keeping the trap) or the prose says the
  trap is gone — a balance decision, Jerus's. `EducationCheck` §8 asserts
  only the direction (free tuition graduates more than full price) and its
  own banner repeats "more than half a month's pay", so it passes either way
  and wants a strength assertion once the fee is decided.~~ — done 2026-09-21: the price is the player's dial
  now, `TaxPolicy.tuitionScale` 0–5×, default 1 (the founding table, unchanged).
  At ×3 the trap is back as the prose describes it — a one-university city at
  five years has 1,718 students and 69% of its diploma-holders willing at ×1,
  1,173 and 26% at ×3, 1,756 and 90% at ×0. `Education`'s class header and
  `foundingTuition()` say so. Carried forward from it, still open:
  `EducationCheck` §8 asserts only the direction and its banner still says
  "more than half a month's pay" (27% at today's wages, ×1) — it wants its
  strength assertion at ×1 and ×3 now the fee is a dial (§15 asserts the trap
  on its own fixture).
- **Prune this list.** Section 2 still carries the built narratives of
  2026-09-10 and 2026-09-11 next to the two open questions that came out of
  them; moved to the changelog and left as lines with pointers, the list is
  under five hundred lines. Wants Jerus's eye on what is still open.
- ~~**The manual is three versions behind** — version 5 describes 0.5.15; three
  sectors, the transport stack, the basket and the vehicles have shipped since.~~
  **Done 2026-09-18 (night): version 7 at 0.6.7** — see `the-manual-at-0-6-7.md`.
- **FOUND BY THE MANUAL PASS, 2026-09-18 — twenty places where prose disagrees
  with code**, none fixed; the first nine are a docs pass's (Opus, small), the
  rest are this list's and the notes'. In the tree: `sectors/Rail.java` L113
  says the road relief is "70%" over `RAIL_ROAD_RELIEF = .75`;
  `TaxPolicy.MAX_TRANSIT_FARE`'s javadoc calls `.05` "a multiple of the
  default" when it is $50 a ride; `HouseholdBalance`'s banner says "SIXTY-EIGHT
  CELLS" and the constructor builds seventy-eight; the Diner is sized in
  `BuildingManager` L1468, `Restaurants.java` L80 and `a-meal-out-is-food.md`
  from "eight staff at $30.8k" over a template with four posts;
  `AgeBand.java` L56 and L140 say five bands; `Good.java` L780 says
  "twenty-five more numbers" for thirty-one goods; `Game.java` L4082 says
  "eleven sets of books and a twelfth" for fifteen; `Automotive.java` L62
  says none of its customers exists yet; `buildings.json` `nextId` is 69 under
  a highest id of 72. In this list and the notes: §4's "a brake that nine
  sectors ignore" is stale (`Game.consider()` L1905 applies
  `servicesItsOwnDebt()` to every sector — struck below); §4 "ten planners" and
  §6 "eleven screen classes" are fifteen and twelve (fixed below);
  `docs/harnesses.md` lists sixteen classes no harness names, not eleven (fixed
  below); `the-freight-band-and-the-three-loads.md` says 69% ore in prose and
  68% in its table; `LuxuryRetail.java` L67 claims a current-account deficit the
  default seed does not run; `Restaurants.java` L100 and `LuxuryRetail.java`
  L78 count the money-constant family at twenty-six and twenty-five;
  `Sector.operations()` prints four of the five ratios; the transit payroll is
  outside G like education's; `Good.java`'s vehicles banner compares the cars'
  "18.2% band" with the shelf's "21–25%" on different bases; the residue
  block's "dearest thing in the catalogue per acre" is the dearest farm;
  `the-instrument-panel.md` says nine batches and lists eight. Full list with
  lines in `the-manual-at-0-6-7.md` §4.
- **FOUND BY THE MANUAL PASS, 2026-09-22 — sixteen places, and eight of the
  twenty above are still in the tree**, none fixed; the first eight are a docs
  pass's, the rest this list's and the notes'. In the tree: `DebtManager` L949
  and L959 say "ten points" a measure over `MAX_SPREAD_PER_MEASURE = .05`;
  `CentralBank.QE_SPEED`'s javadoc, `Game`'s THE HOLDINGS DIAL banner and
  `PolicyScreen` L2049 describe the QE step as two things (a share of the gap,
  the larger of two) when `stepFor()` takes the largest of three;
  `CapitalFlows.MAX_SPREAD`'s javadoc says "a 5% world" over `WORLD_BASE_RATE`
  .02 (flagged by the 0.7.2 docs pass, still there); `ForeignAccounts.MAX_RATE`'s
  javadoc says a currency at a thousand "reforms it" when the reform is never
  automatic and unlocks on the index; `GameVersion`'s 0.7.2 entry says "six
  spent all of it" for "six all but 1%"; `MonetaryCheck`'s "9,900%" label
  against `MAX_POLICY_RATE`'s "2,500%"; `BankCheck` ~L1101 and
  `Bank.fundToCover()`'s note disagree about a bank paying savers more than it
  charges (payout versus quote); `MonetaryCheck` ~L282 and `ForeignCheck` ~L594
  assert almost nothing at a guard of 1e9; `LongPlaytest` divides by
  `Math.max(.01, fxRate)` in three places, a floor from the old guard;
  `Household.plan()`'s eating-out note holds only at a spend factor of one;
  the tree says the playtest is 4,002 months in a dozen places and the run
  reports 4,005 (the front doors now say "4,000-odd"); `docs/dials.md` prints
  no sentence for 159 of 780 constants, among them dials the manual leans on
  (the five `Healthcare` fees, `LICENCE_COVER_TO_OPEN`, `MAINTENANCE_RATE`,
  `CARRY_REPAY_SPEED`, `MEAL_SHARE_OF_SURPLUS`, `MOST_MEALS_EATEN_OUT`,
  `DEFAULT_FARMLAND_RELIEF`). In the notes: `the-currency-off-its-rule.md` §5's
  "121 of 144 months with sales" is 0.7.2's figure and `CurrencyCheck` prints
  116 at 0.7.3 (§2's "two quarters" sentence is corrected in place). In this
  list: three items §3 carried after 0.7.0 closed them are struck tonight.
  Full list with lines in `the-manual-at-0-7-3.md` §4.
- **Do not move the harnesses to their own package yet**: about twenty
  package-private model members at seventy-odd call sites would need a seam.
- **Sixteen classes no harness names** (`docs/harnesses.md`, as of
  2026-09-18; eleven when written): `Automotive` and `LuxuryRetail` are the two
  a check ought to name; four are the mechanics moved out of `Game`, reached
  through it; the rest are reached through others or are plain data.

- ~~The eight stubs from the sector template~~ gone, 2026-09-11 midday, with
  `MenuManager.java`.
- Two lossy compound assignments in `Game.java` (`materialsConsumed`). *The
  handler's copy of the count is no longer zeroed on the second
  `updateServices()` pass, so the construction screen's "Used" line reads the
  month's figure instead of 0 — found on the way through `calculateExpenses()`.*
- Aggregation helpers ignore `instances` — latent until `addInstance()` is used.
- `LandMarket.rollIron()` comment says "a century or two"; it is 50–200 years.
- **Not persisted across save/load:** the 12-month GDP history (annual GDP and
  growth read short for a year after a load). *The solvency record, the placement
  residual, the doors let, and the bank's last closed month were all carried on
  2026-09-10; that night, the borrower's default record, the economy's traded
  exchange rate, the land office's prices, the world's level ring and the labour
  market's diagnostics joined them, and later the same night the shops' last
  month's sales, the food market's traded price and construction's struck
  month — and `LongPlaytest.roundTrip()` now compares every one, which it had
  not. The template carries each sector whole (`SectorState`), every market's
  price and its three-year take, and every site's material owed and contract.*
  *And since 2026-09-15 every band-indexed array is carried BY NAME — one
  `bandNames` field describing the whole file, read by the pyramid, the families
  and the ring of the long sick alike — so their width comes from the save rather
  than from the reading build, with `shapeNames` beside it doing the same for
  the household matrix. See the top entries.*
- `SteelCheck.java`, `SolvencyCheck.java`, `PlaytestRun.java`, `MillCount.java`,
  `RealismCheck.java`, `JerusSave.java`, `HouseProbe.java` and `BankProbe2.java`
  exist in the working set but are not in the NetBeans source folder. Harmless —
  they are probes, not game code. **`RealismCheck` is the rebalance's instrument
  and `BankProbe2` is the bank's; either is the thing to run first if any of
  these numbers are ever questioned.** *`LongPlaytest` now prints credit by
  sector at the end, which is most of what `BankProbe2` was for — and since the
  template it has a monthly window on any sector (`-Dplaytest.sector`,
  `-Dplaytest.bankfrom`, `-Dplaytest.bankto`) and a yearly bank line
  (`-Dplaytest.bank`).* *`FoundingProbe.java` joined them 2026-09-15 — the
  founding-currency instrument, `-Dprobe.heavy="Coal Power Plant:2,House:200"`
  and `-Dprobe.months`.* *And `DenomProbe`/`OrphanProbe` the same week — the
  month-by-month divergence instrument that found the exchange's yield tie and
  the elders' missing pension. Neither is in the tree; both are worth rewriting
  rather than keeping, because what they measure changes each time.* *`FarmProbe`
  and `DenomLuck` joined and left the same way on 2026-09-16: the first compares
  ~650 quantities a month across a city and its reformed twin and prints the
  largest gaps, the second runs that comparison over ten foundings so a red
  harness can be told from a coin toss. **The second is the one worth rebuilding
  first next time** — it is what proved the farms batch innocent.*
- **`sendBuildingSave()` loops `i < getTemplateCount()` and looks up by id.** A
  trap, not a bug, while the ids stay dense.
- **Harness hygiene, decided:** every harness `Game` goes through
  `GameFiles.scratch()`. `AllChecks` runs all **49** harnesses plus the
  playtest — **50** in one command, **~94 seconds** (`EquityCheck` joined
  2026-09-10 night, `ExchangeCheck` 2026-09-11,
  `OutsideCheck`, `SicknessCheck`, `HouseholdMemoryCheck`, `DeathRecordCheck`
  and `CrimeCheck` 2026-09-11 night, `BusinessServicesCheck` 2026-09-12,
  `ManufacturingCheck` and `AgricultureCheck` 2026-09-13, `YearBookCheck`
  2026-09-14 with 277 assertions — 286, then **294**, on 2026-09-15)
  — `BuildMenuCheck` included since 2026-09-10 (evening), when the cached
  JavaFX 21 jars from `.m2\repository\org\openjfx` were staged into the cloud
  loop. **The whole tree compiles there now, `UserInterface` included; no UI
  change goes to the PC uncompiled again.** *All forty were migrated to the
  template's API on 2026-09-11 — the old handlers had a hundred and ninety
  call sites in them — and all forty pass on a mirror of the PC's tree.* *No
  harness was added on 2026-09-15; `PopulationCheck` gained **eleven** blocks
  across the two by-name passes — six for the pyramid, three for the families,
  two for the ring — and the split that followed added a tie block to
  `ExchangeCheck` and a pensioner block to `HouseholdCheck`.* *None on
  2026-09-16 either; `LongPlaytest` gained the negative-position flag, which is
  a guard on every seed rather than a fixture.*
- **`LongPlaytest` has a cell trace.** `-Dplaytest.cells=true` prints, yearly,
  every household cell that is short, cut off, locked out or in debt. It is how
  the pensioner alone was found; use it before believing a household number.
- **Half the tree is CRLF and half is LF.** Ten of the fifteen files in the audit
  batch were CRLF; any tool that normalises on read writes them back as
  whole-file diffs. Check `grep -c $'\r'` before a copy-back. A `.gitattributes`
  would end this. *Bitten once more on the materials pass: a text-mode edit of
  `HouseholdCheck` stripped 811 carriage returns and was caught by the count
  before the copy. Binary edits only, for CRLF files. The template's deploy
  re-ended every changed file to whatever its PC copy was — 44 CRLF, 16 LF —
  and the fifteen new files are LF.*
- **`LongPlaytest` has an ensemble mode.** `-Dplaytest.seed=N` (0–35) nudges the
  founding order; seed 0 is the run as it always was. One run before and one
  after measures the change plus the weather; eight seeds a side is the least
  that separates them, and it is fifteen seconds each. **Use it for anything
  that touches the whole economy.** *Seeds 4–7 land within a few people of
  each other on the template; the seed nudges the founding order and those
  four converge. Worth widening the seed's reach.*
- **A FIXTURE HAS TO CAUSE THE CONDITION UNDER TEST, NOT STAND NEXT TO IT.**
  **Forty-eight sightings.** It is a rule, not an observation. *And since
  2026-09-14 the README states its corollary as a fourth rule in its own right:
  **never move a harness's premise to let a change through.*** *The
  forty-seventh and forty-eighth came with the senior split, 2026-09-15, and
  both are variant (b) — the model got better and the fixture broke.
  `ForeignCheck`'s devaluation premise had never actually been fixed: it was the
  fixture's plant and roads PLUS however much private housing each city wanted,
  and the two cities grow at different speeds. It read 1.0063 for as long as the
  two household counts happened to track each other; the split pulled them apart
  and the elders' pension pulled them further. Real Estate is held out now, the
  eleventh sector to sit that fixture out, and the instrument came back SHARPER.
  And `HouseholdCheck`'s pension block had never asked whether a pensioner in
  one band draws what a pensioner in another draws, because there was only one
  band.* *The
  forty-sixth, 2026-09-15, failed on its own first run and was right to:
  `YearBookCheck`'s new "the book agrees with the model" fixture stood on the
  default founding, where after sixty months **nobody is studying** — so
  `workforce` and the labour force are the same number and the broken
  unemployment formula it was written to catch would have passed. It funds a
  city and builds a community college now, and asserts that the replaced
  formula actually disagrees with the model there.* *Forty-three to
  forty-five came with the tenth, and the first is the best example of the rule
  this project has: `SaveFileCheck` had two conditions FIGHTING EACH OTHER. Its
  squeeze - income tax at the ceiling with a punitive offset on every band -
  exists to cause hunger and does; it also makes every sector too poor to
  borrow, and a bank with no borrowers earns nothing and pays its savers
  nothing. "Somebody is hungry" and "savers are being paid" were being asked of
  the same city at the same moment, and one is the other's opposite. It held
  only while the mills made food out of nothing, because a sector with no input
  bill stays rich through any squeeze; give them a crop to buy and the bank had
  failed once, sat on $5k of equity against a $3.1m book, and had paid nothing
  for two hundred months. The fix was the ORDER - run the city healthy until
  the bank is paying, and only then squeeze it. (It also got four Mixed Farms,
  because "one of everything" stopped being true the day the mills started
  buying crops.) `ForeignCheck`'s devaluation section went for the second time
  in a day, one sentence further along the same chain. And `ManufacturingCheck`
  asserted `SAVE_FORMAT == 23` - a tripwire written that morning that went red
  the same afternoon, a harness about fabricated steel pinning a global
  constant it does not own.* *Thirty-nine to
  forty-two came with the ninth sector, and all four are the same shape — a
  sector that buys something walked into fixtures that had never had one:
  `MiningCheck` measures what a mine is worth to a mill and a fabricator took
  steel from $847 to $1,240 in both its cities; `ForeignCheck`'s devaluation
  section rests on the import side being a FIXED programme, and Manufacturing
  buys steel in proportion to how big its city got; `HousingCheck` wanted
  households doubled up before checking the crowding figures survive a save, and
  a sector that employs people grows the city into its own housing;
  `SaveFileCheck` wanted food in the mills' warehouse, which its own note two
  lines above had already retired one shelf along. The first of those produced a
  lever — `BusinessInvestment.holdSector()`, harnesses only — because it is the
  second time a new sector has walked into somebody else's fixture. *And the
  third and fourth times were the same fixture again: `ForeignCheck` has held
  Manufacturing out since the ninth, Agriculture since the tenth, and Real
  Estate since 2026-09-15.** *The
  thirty-eighth, variant (a) from a new mechanism: `OutsideCheck`'s college town
  built no police, and once crime existed the thefts handed its students enough
  that nobody took a student loan in fifteen years — in the fixture that exists
  to test the loans. It builds a station now.* *The
  thirty-seventh: `HealthCheck` asserted the founding endowment was sized off
  the pyramid by checking senior care came out smaller than childcare - the
  pyramid's shape, not the rule - and halving adult mortality let the seniors
  pass the children. It asserts the rule now. `SicknessCheck`'s first "city
  with no clinics" was 97% covered by the founding doctor.* *The
  thirty-sixth is the weak-threshold variant: `OutsideCheck`'s "graduates have
  repaid some of them" passed at **1 repaid of 7,530 lent**, because no loan
  ever left a college that does not change size. It now asserts that the
  students the census saw finish left with their loans, every month, to the
  person. A threshold of zero is not a claim.* *Thirty to
  thirty-five came with the sector template: `SaveFileCheck` stopped in a month
  its subsidy dial happened to pay, and the sector that paid it stopped losing
  money when the builders stopped buying an order's material on day one;
  `ForeignDebtCheck` borrowed "twice the treasury" and read the spread that
  happened to make against a GDP the accounts no longer inflate — it borrows
  until its own paper is dearer now; `ForeignCheck` asserted a devaluation
  improves the trade balance in dollars, and the balance is nine tenths a
  materials boom's timing now — measured and printed, the one claim the fixture
  can carry asserted; `ConservationCheck` built three mines over no ore and
  wanted them to have a payroll; `GdpCheck` asserted the work-in-hand term;
  `BankCheck`'s savers' rules assumed idle capital earned nothing. And a
  variant worth its own line: `DenominationCheck` passed for two days while the
  resolution floor was in the wrong unit, because no bank in its fixture had
  ever earned its way out of resolution — the bug was in a branch nothing
  opened until the vault earned interest.* *Twenty-four to
  twenty-nine came from the materials pass, and four of them were one city:
  `BankCheck` (three fixtures) and `SaveFileCheck` all queued the same list
  behind a power plant with no roads and no food, starved at 50–75%, and passed
  only while the landlord's default was small. They stand on month one now and
  the book is caused. `ForeignCheck` measured a devaluation in the city's money
  when a price-taker can only move volumes; `HouseholdCheck` asserted flatshares
  "however they got there"; `InboxCheck`'s empty city had nothing to say. See
  `the-unit-of-material.md` §4.* *The twentieth
  to twenty-third came in one night from one rebalance: `ConservationCheck`
  measured a warehouse that no longer existed, `InfrastructureCheck` called a
  city congested that ran at 73%, `BooksCheck` asked for a revenue that stopped
  being true when the mill started exporting, and `BankCheck` compared this
  month's tax with next month's profit and passed while they happened to be
  equal. A fixture that opens with "3 stores" is a fixture that opens with a
  number; each now derives its count from the template.*

  The original six: `LabourCheck` read whatever the 25th month held;
  `SaveFileCheck` stopped in the exact month its bank opened; `BankCheck`,
  `InfrastructureCheck`, `InboxCheck` and `ForeignCheck` all broke on the
  founding-parameter change.

  The rebalance added eleven more, and two variants worth naming:

  **(a) The model now FIXES the problem the fixture was built to show.** When
  this happens the fixture should assert the better outcome, not be bent until a
  fixed bug reappears.

  **(b) A fixture can break because the model got better and start working again
  because a price moved somewhere else entirely.** `HousingCheck`'s studio city
  did both in one night, in opposite directions.

  **The eighteenth (2026-09-10) is the cleanest example of (b) yet.**
  `ForeignDebtCheck` set the foreign-default clock to 200 months — past the sixty
  the borrowing window closes for — so its "the window is still shut" assertion
  was never testing the scar at all. It passed only because that fixture's city
  happened to export too little to satisfy the debt-to-exports test. The
  industrial fix turned the city into a food exporter, the incidental reason went
  away, and the assertion failed with nothing broken. It now winds the clock
  forward to sell the bond and back to twelve to shut the window, so the reason
  the window is shut is the reason the assertion names.

  **The nineteenth (2026-09-10, late) was decided by a data file.**
  `CapitalFlowCheck`'s "hot money actually arrives in a real city" never set a
  policy rate, and a default city prices its paper a full point under the world,
  so the spread was structurally zero. It passed or failed on whether two months
  of bank-strain premium happened to spike the rate — which depended on a
  power-plant capacity number in `buildings.json`. It sets a 6% dial now and
  asserts the spread opened.

  **And a third variant, found the same day: A TEST THAT CHANGES NO NUMBER IS NOT
  A PASSING TEST.** Two versions of `branchWouldPayForItself()` were written and
  both were silently inert — the first asked what a branch is *entitled* to
  carry, the second read flows `startMonth()` had just zeroed. Two consecutive
  4,000-month runs came back **byte-identical**, and nothing but that said so.

  **And a fourth, 2026-09-15: A HARNESS THAT ONLY CHECKS THE ARITHMETIC WILL
  CERTIFY THE WRONG NUMBER.** The year book's first edition shipped 277
  assertions and three wrong columns, because every one of them tested that the
  FOLD was right — a flow sums, a level takes December, a rate averages — and
  none tested that an input meant what its note claimed. **When a harness checks
  a derived figure, one assertion must compare it against the model's own
  definition on a played city**, and its tolerances must come from the storage's
  own precision rather than being picked.

  **And a fifth, the same day: A HARNESS CAN PIN A CONSTANT SO THAT THE NEXT
  CHANGE CANNOT SILENTLY MOVE IT.** `PopulationCheck` asserts that
  `PopulationCohorts.LEGACY_BANDS` is literally five names — not
  `AgeBand.values().length` — because the list describes a file already on
  somebody's disk. The assertion exists to FAIL if anybody ever "tidies" it into
  tracking the enum, which is the exact edit that would re-arm the landmine.

  **And a sixth, 2026-09-15: A HARNESS CAN BE GREEN AND THE MONEY AUDIT CLEAN
  WHILE A WHOLE CLASS OF HOUSEHOLD STARVES.** The elders drew no pension for a
  batch: the bill was right and the same total left the treasury, so nothing
  conserved was violated and every assertion held. **A conservation law says the
  money went somewhere, not that it went to the right somebody.** What found it
  was a probe comparing two builds' trajectories, and what keeps it found is an
  assertion per retired SHAPE rather than per row.

  **And a seventh, 2026-09-16: ONE FIXTURE IS ONE DRAW.** `DenominationCheck`
  went red on the farms batch and the natural reading was that the farms broke
  the currency reform. Ten foundings said otherwise: nine tracked to 1e-14 and
  only the harness's own fixture blew up, on a residue whose sign is luck. **When
  a harness goes red on your change, ask the same question of ten fixtures before
  believing it is your change** — and note that the same instrument found the
  DEPLOYED tree failing the same way on a different fixture, eight orders of
  magnitude inside its band.

  **What actually makes a fixture safe**, learned the hard way: size it from the
  model's own state rather than writing a number down; wait for the condition
  with a bound rather than stopping at a fixed month; when a reading has two
  doors into it, close both; **if an assertion names a cause, make the fixture set
  that cause and assert the cause as well as the effect**; and **run the whole
  cycle the game runs — `BankCheck`'s branch fixture has to open a month and
  CLOSE it, or it asserts nothing.**

## 7. Store, when the game is ready

In order, because two of these are sequential and it catches people out:

1. Assets: capsule art in several fixed sizes, a **trailer**, screenshots, short
   and long descriptions, tags.
2. IARC age-rating questionnaire.
3. Submit **store presence** for review — 3–5 business days, 7 days' notice
   asked for.
4. Only once that is approved can you submit the **build** for review.
5. Coming Soon page live **at least two weeks** before release.
6. SteamPipe depot pointing at `dist\CityBuilderSim`.
7. Click Release App yourself — approved titles do not release themselves.

Optional: `steamworks4j` for achievements and Cloud saves.

### Sharing it before Steam

The unit is the whole `dist\CityBuilderSim` folder (~115 MB, carries its own Java
runtime). Zip it. Expect **SmartScreen** to block an unsigned exe — "More info →
Run anyway" — and put that in a README. itch.io is the right home for a public
build. **Windows only**: jpackage does not cross-compile.

---

## Not doing, and why

- **Syndicating past a large-exposure limit** (0.7.8, round 3, on Jerus's
  choice): Basel's 25% of Tier 1 per counterparty with a sector as one
  connected borrower, the rest of each loan sold abroad at its own rate. 99% of
  business lending went abroad, interest abroad reached 9–14% of GDP, prices
  3.9 times founding, and the bank became an arranger with $16–120M of equity
  that still failed 101 times. Deleted (Jerus, 2026-09-24: *"Drop syndication,
  keep quarterly"*): a sector is many firms, and Basel's rule is for a firm or
  a group tied by control, not an industry. (`a-sector-is-many-firms.md` §4.)
- **A concentration charge with a stress target** (0.7.8, the gate's second
  version): 68 failures rather than 85, but unemployment 21.5% against 11.8%,
  $233bn written off against $167bn and GDP 11% lower. Reverted; `Bank
  .MAX_BUFFER`'s javadoc has the numbers.
- **Keeping the bank's excess instead of returning it** (0.7.8): two seeds
  failed 3 times each rather than 14 and 12, and ended with 78,048 and 127,198
  people against 150,518 and 141,302.
- **Tuning `MAX_ORDER_MONTHS`.** Rejected on measurement: byte-identical to no
  cap on three land seeds of four. *It is a rule for the makers since the
  template — a plant the builders cannot finish inside a year is not ordered —
  because the seventh sector's first plant sat five years in a founding queue
  paying interest.*
- **Putting interest into the housing build hurdle.** Tried twice, both times put
  the hurdle above any rent the city could bear.
- **Bending the rent floor to hit 31% of income.** The floor is the honest
  product of two independently sourced real numbers. Both inputs have a source;
  the share does not.
- **Costing the three roads for real.** They are the one group whose costing is a
  RELATIVE design rather than an absolute one — each has to win a band of land
  prices.
- **Cutting industry's jobs to fix its wage share.** The plants were not
  over-manned; they were under-producing. Cutting the crew would have hit the
  same payroll ratio while making the buildings less real, not more. Output moved
  instead. (2026-09-10) *Cutting them in proportion to a smaller plant is a
  different thing — that keeps the ratio and is the granularity decision.*
- ~~**Tightening the bank's loan-to-assets limit below the insolvency line.**~~
  **Withdrawn 2026-09-10 (late).** The measurement was honest and answered a
  different question: the ceiling and the trigger were one constant, so lowering
  the ceiling lowered the line with it and never tested a gap. With a gap
  (0.9 against 1.5) the bank keeps a book and stops failing. See
  `the-lender-gets-a-gap.md` §1.
- **Holding the investment desk to the shortfall desk's ceiling.** Measured:
  halves the early city (jobs 1,178 against 1,922 at month 266) and doubles the
  worst unemployment. A desk that funds every first expansion cannot be held to
  a rescue desk's limit. (2026-09-10)
- **A large-exposure limit on the bank** — no borrower past the bank's own
  equity, from either desk. Measured (2026-09-11): the founding bank can lend
  nothing it could not lose, so it earns nothing, drains, and lends less; the
  city ends at 3,400 people and $15,000tn of debt at 42%. Seven borrowers
  cannot be held to a regulator's quarter-of-capital; the bank grows into its
  town by lending. The concentration problem is real and the answer is the
  owners' equity in the plant, which is where the template put it. *(Tried
  again as syndication in 0.7.8's round 3, and deleted — above.)*
- **Raising `MAX_SPREAD` to price risk.** Measured at 12%: the bank got poorer,
  because the borrower paid the extra interest by borrowing it from the same
  bank. (2026-09-10) *(The spread is the curve's since 0.7.8, uncapped; under
  the shortfall desk's ceiling it charges at most 0.83 points, so the 12%
  measurement does not bite — `a-sector-is-many-firms.md` §3.)*
- **Changing `TREND_INFLATION` to fix the currency.** That reintroduces the
  2.4-million-fold price level `WorldEconomy` exists to prevent. The fix was to
  measure inflation from the level, not to change the level. (2026-09-10)
- **Liquidating a sector on six months of losses.** Measured: it killed every
  founding plant the advisor built, at month 9. The fuse on a working plant is
  two years. (2026-09-10)
- **Pricing food exports off the local shelf.** At 0.9 of the local price the
  food industry ended a run holding $72bn; the world pays its own price less a
  wedge, not the city's. (2026-09-10)
- **Counting a banned sector's write-downs.** Every month of a ban read as a
  new default and the escalating exclusion reached 525 months. One episode, one
  default. (2026-09-10)
- **A materials plant that fills its shed and idles.** It does — the shed is
  the business for a good drawn on order — but a plant that only idled with a
  full payroll was scrapped in six months; it exports the spare line at the
  floor now, as every maker does. (2026-09-11)
- **Correcting the Reports chart's unemployment line in the chart.** It would
  have left two copies of the definition that happen to agree today, which is
  how the first copy came to disagree with the model in the first place. The
  chart delegates to `YearBook` instead. (2026-09-15)
- **Splitting the senior band in the same batch that made the save safe.** The
  reader change is the thing that protects every existing city, and it wanted
  deploying on its own, with the harness written for a sixth band that does not
  exist yet. A band split is a model change and gets its own sixteen seeds.
  **Justified twice over the same night**: the audit that had to run before the
  enum could move found two more save landmines of the same class, both
  prerequisite, and neither would have been looked for inside a split batch.
  (2026-09-15) *And a third time the next day: the split's own batch found three
  more defects, none of them about age bands, and each would have been a
  confound inside a bigger one.*
- **Breaking a tied ranking by index.** The exchange's "best yield first" is a
  tie whenever two companies are valued on their earnings, and the obvious fix —
  order by company number — is deterministic and **biased**: it hands the same
  company every tied month for ever. Buyers who cannot tell two things apart
  spread between them, which is what shipped. (2026-09-15)
- **Widening `ForeignCheck`'s five-percent premise band.** It drifted to 5.58%
  and the temptation was one character. The premise was false instead — private
  housebuilding was inside a "fixed programme" — so the landlord is held out,
  the way Manufacturing and Agriculture already were, and the band never moved.
  (2026-09-15)
- **Shipping the farms and carrying `DenominationCheck` as a known finding.**
  It was one of three options and the harness had been right fifteen times, so
  it was chased instead — and it was not the farms at all but a residue in the
  household rebuild that had been deciding who may hold money abroad for months.
  **A harness that has been right fifteen times is worth one more investigation
  before it is overruled.** (2026-09-16)
- **Snapping the residue where it was read.** The first fix clamped a dust total
  to zero inside `moveStock()`; it fired on nothing and changed no digit, because
  the dust was a single small term rather than a cancellation. Reverted rather
  than shipped with a comment claiming a fix it did not make — the real defect
  was a share above one, two lines up. (2026-09-16)
- **A web version.** Applets are gone and Web Start went with JDK 11.
