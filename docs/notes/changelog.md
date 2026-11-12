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
