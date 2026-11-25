# R6f - research: the 1996-2024 medicine, business and law rows with something behind them

Builder: R6f (Claude Opus, headless), branch `batch/R6f` from origin/main
3d495d293 (0.9.195, format 63). The brief: M12 and M19 on general care's
stays and the Home Care Service, each by its sourced share of stays or
Pending; B9 a minimum price step on the order book; B10 the AI assistant at
the Contact Centre, +14% a post; L8 Basel III on Bank.CAPITAL_RATIO. The
pattern is the study's 3.3 (docs/studies/research-1996-2024.md) and R6e's;
R6-G's ★1 (one central date) and ★6 (a row enters research.json only in
the batch that builds what it gives). ★ below marks a design call.

## 1. What was built

Three rows enter `research.json` after E21, in the order M12, B10, L8 (100
entries). Each is undone in a city founded the 1900 way until it is known
(history's month, or the month the city found it); a city founded the
default way knows every row from month 0 and runs as before.

- **M12, robot-assisted surgery (17 Jul 2000).** `Sickness.STAYS_ROWS`
  gains M12. Before it, general care's part of the recovery is
  `Sickness.STAYS_BEFORE_M12` (0.99962) of today's: the robot's 0.23 days
  over keyhole surgery on its 0.78% share of US stays, against the 4.662-day
  mean stay. `STAYS_BEFORE_LATER_ROW` (M5, M6, M9, M10) is re-struck so the
  four and M12 still make 1950's 10.0 days into 2022-23's 7.3
  (`LATER^4 x M12 == 0.73`, MedicineCheck); each later row's factor moves
  from 0.92434 to 0.92443. `MedicalRows.CODES` gains M12.
- **B10, the AI assistant (Nov 2020).** The Contact Centre (id 46) gains a
  recipe change, `without: jobs NO_DIPLOMA +35.7`: before B10 its agents are
  today's 255 times 1.14, 290.7, rounded once by RecipeChanges to 291, for
  the same 300 units of support work. B1's change (the DIPLOMA and
  COLLEGE_BUSINESS clerks) composes beside it.
- **L8, Basel III (1 Jan 2013).** `Bank.hearCapitalRule(boolean)`,
  `conservationBufferRequired()`, `bufferFloor()` and `BUFFER_BEFORE_L8 = 0`:
  the bank's buffer is `min(MAX_BUFFER, max(bufferFloor(), worst year))`, so
  before L8 its target is the 8% minimum plus its own worst year alone, and
  from L8 at least 10.5%. `targetReason()` says so before 2013. Game tells
  the bank in `bankHearsTheRule()`: where the month is heard (the founding,
  the top of every month) and again at a load once the founding and the
  finds are restored. ModernRowsCheck found the load saying it before
  either was back: a loaded 1900 city held today's buffer until its next
  month.
- **ModernRowsCheck** (new; in AllChecks after ModernEngineeringCheck; its
  line in docs/tree.md): 177 assertions in three sections, one per row.
  ResearchCheck (100 entries, the counts, needs, dates and work),
  MedicineCheck (M12 a STAYS row, the re-struck split) and
  PostwarRecipeCheck (30 template changes: its `postwar()` pattern takes
  B10) hold the new rows.
- **M19, B9 and the leverage floor's date are queued** (R6g, R6h, R6i; section 4).

## 2. The sources, re-opened 2026-11-23

Each page was opened again by R6f; none has stopped saying its figure.

| code | page, date | the line |
|---|---|---|
| G-M38 | ScienceDaily, 17 Jul 2000, sciencedaily.com/releases/2000/07/000717072719.htm | "The U.S. Food and Drug Administration has cleared for marketing a robotic device" (the da Vinci Surgical System) |
| G-M39 | Choi et al., Surg Endosc 38:2371 (2024), PMID 38528261; the abstract by Europe PMC's REST API (its article page answers 403) | "Compared to laparoscopic surgery, robotic surgery had fewer open conversions ..., shorter length of stay (mean difference - 0.23 days, p = 0.01)"; against open surgery, "shorter length of stay (mean difference - 1.69 days, p = 0.001)" |
| R6f-S1 | Minimally Invasive Surgery in the United States, 2022, J Surg Res 281:33 (14 Sep 2022), PMC9691544 | Table 1, 2018: "Visits with ≥ 1 robotic-assisted ... 276,325 (261,629–291,021)" of 9,605,207 inpatient OR stays; "in 2018, these rates were 11.2% ... and 2.9% ..." (keyhole, robotic) |
| R6f-S2 | HCUP Statistical Brief 281, AHRQ, Aug 2021 | "Inpatient stays (N = 35,527,500)"; "In 2018, 27.0 percent of inpatient stays involved at least one OR procedure"; its Figure 1's mean stays, 5.1 days with an OR procedure and 4.5 without |
| G-B1, G-B2, G-B3 | Brynjolfsson, Li and Raymond, Generative AI at Work, NBER w31161 (Apr 2023, rev. Nov 2023), its full text | "Access to the tool increases productivity, as measured by issues resolved per hour, by 14% on average, including a 34% improvement for novice and low-skilled workers"; the data "from 5,179 customer support agents"; "The bulk of the adoption occurs between November 2020 and February 2021." |
| G-B29 | BIS press releases, 12 Sep 2010 and 16 Dec 2010 | common equity from 2% to 4.5%, Tier 1 from 4% to 6%, a conservation buffer of 2.5%; "The total capital requirement remains at the existing level of 8.0%" |
| G-B33 | DLA Piper on Legal 500, Final Guideline for Basel III Implementation Now In Effect, 14 May 2013 | OSFI's capital guideline in effect from 1 January 2013 (the page's reading as fetched; its exact sentence was not kept) |
| R6f-S3 | BCBS history, bis.org/bcbs/history.htm | the 1988 Accord's minimum of 8% of risk-weighted assets, to be met by the end of 1992 |

## 3. The calls

- **★ M12's share.** 276,325 robot-assisted of 35,527,500 US inpatient
  stays in 2018 (R6f-S1 over R6f-S2): 0.78%. No Canadian share was found;
  the US share stands for the city's (est.), as the study's other rows take
  US trials.
- **★ M12's saving is the robot's over keyhole surgery, 0.23 days**, not its
  1.69 over open surgery: the 1.69 holds keyhole surgery's own saving,
  which M9 (the way before it, and M12's need) already gives.
- **★ The mean stay is the US 2018 mean, 4.662 days** (0.270 x 5.1 + 0.730 x
  4.5, R6f-S2), the same population as the share.
- **★ The stays rule is general.** A stays row with its own measure (M12;
  MED3's M14 takes the same rule) has its own factor, and the four later
  rows split evenly what is left of the 10.0-to-7.3 fall, so 1950's
  calibration holds. Leaving the four at 0.92434 and M12 on top would make a
  1900 city's 1950 stay 10.004 days.
- **★ B10 is the agents only.** The +14% is resolutions an hour of the
  agents given the tool: the Contact Centre's NO_DIPLOMA posts. B1's clerks
  (DIPLOMA, COLLEGE_BUSINESS) were not measured. The 34% for the novice is
  inside the 14% average and is not stacked on it.
- **★ B10 is dated Nov 2020**, the bulk of the firm's roll-out (G-B2), not
  ChatGPT's release (Nov 2022): R6-G's ★1, one central date for the thing
  measured.
- **★ B10 has no equipment piece.** The study names a piece; its capital
  cost and licence price were not measured, and a piece in today's figures
  would be a new number in every default city. The row is a recipe undo
  alone, its fee a patentability estimate, as E21's.
- **★ L8 does not move Bank.CAPITAL_RATIO.** The 8% is the total-capital
  minimum, and Basel III left it "at the existing level of 8.0%" (G-B29),
  the 1988 Accord's (R6f-S3). The game's bank holds only common equity, so
  the common-equity and Tier 1 minimums (2% to 4.5%, 4% to 6%) are under
  its 8% before the row and after it, and never bind. What Basel III
  brought that the bank reads is the 2.5-point conservation buffer: before
  L8 a bank's buffer is its own worst year alone (`BUFFER_BEFORE_L8 = 0`).
  OSFI's pre-2013 targets (7% Tier 1, 10% total) were seen only in a search
  snippet, never on an opened page, and are not used.
- **★ The leverage minimum is not dated by L8.** `leverageTarget()` is
  `LEVERAGE_RATIO_MIN x capitalTarget() / CAPITAL_RATIO`, so before L8 it
  follows the target down to the 3% minimum itself. The 3% leverage ratio
  is Basel III's too, but dating the leverage floor needs its own source
  (what limit a Canadian bank had before it) and is left as it was (queued as R6i).
- **★ One harness, ModernRowsCheck,** with a section per row, in place of
  three classes: the three share their fixtures and their pattern.
- **★ A find takes effect in its month.** A row found ahead of history is
  known the whole month it is found (`Research.known()`), so the month's
  finds are heard at once (`Game.hearTheFinds()`, after `research.month`):
  the bank its capital rule, the sickness its medicine. The rest of that
  month reads the new rule, as a reload of that month does. Only a 1900 city
  that finds a row ahead of history runs differently, and only in that
  month: the sickness ring's month (`advanceMonth`) and the bank's later
  reads now take the find.
- **The bank's rule is not saved.** It is derived from the founding and the
  finds, both saved, and heard again at a load.

## 4. Not built: queued

- **M19, hospital at home: queued as R6g, with what it needs.** The measured
  national effect is not a shorter stay: MedPAC (June 2024, ch. 6) gives
  6.6 days at home against 5.7 in hospital for the same DRGs (two-column
  text: "6.6 ... compared with 5.7 days, respectively"), and Levine's trial
  4.5 against 3.8; Leff's 3.2 against 4.9 is contradicted by the newer
  national figure. Its reach is about 6,100 discharges in 2022, "less than
  0.1 percent of all IPPS discharges" (MedPAC), with no Canadian share. The
  Home Care Service is senior care and holds no acute patients, so the
  study's recipe there has nothing to act on. It needs a mechanism for
  acute care at home with its cost a stay (MedPAC: resource use "18 percent
  lower"), and a measured share.
- **B9, the tick: queued as R6h, with what it needs.** The tick Toronto cut from
  on 15 Apr 1996 is on no opened page: La Spada, Farmer and Lillo (arXiv
  1009.2329, Oct 2010) say only "the April 15, 1996 Toronto Stock Exchange
  (TSE) reduction in the minimum tick size to five cents". And the game's
  share price is SX1's wage band (between 0.25% and 25% of the monthly
  wage), not a real share's, so a nominal step would be some 14% of a 1900
  share and 0.2% of a 1995 one. It needs the old tick and its price tiers
  sourced. Nominal against relative was the orchestrator's call (2026-11-24): relative, since a tick's effect on trading is its size against the price; so the real tick as a share of the era's real share price is carried onto the game's price.

## 5. L8: the harnesses that read Bank.CAPITAL_RATIO

None moves: every assertion that reads the capital rule runs on a bare
`new Bank()` (which requires the buffer) or a city founded the default way
(which hears it from month 0), and CAPITAL_RATIO itself is unchanged.

| harness | why it does not move |
|---|---|
| BankCheck | bare Banks; its Game is a default founding |
| BankFailureCheck | bare Banks; its Games `Founding.defaults().withPath(...)`; CAPITAL_RATIO as a constant |
| BondCheck | `BankCheck.lentOut` (bare) and a default `city()` |
| BuybackCheck | a comment only |
| CreditCrunchCheck | a bare Bank; its played crunch is `LongPlaytest.played(null)`, default unless `-Dplaytest.founded=1900` |
| FloatLinkCheck | CAPITAL_RATIO as a constant: the auction room is equity over the minimum, not the target |
| HistoryCheck | its 10.5% target assertions are on a default `new Game(scratch)` |
| LongPlaytest | `FOUNDED_1900` defaults to today; under the 1900 flag only its printed targets and rationing counts would shift, and its save round trip holds (a load hears the rule) |
| MortgageCheck | bare Banks and a default landlord city |
| ReadPathCheck | reads with no assertion on the value; default fixtures |
| YoungBankCheck | CAPITAL_RATIO as a constant; a default young city |
| ExchangeCheck, SaveFileCheck | a bare Bank; a default round trip |

The 1900-founded fixtures elsewhere (ScaleCheck, AuctionCheck 12,
AutoBuildCheck 10, NewGameCheck) assert nothing on the capital rule.

## 6. Saves (the old-saves rule)

A format bump is owed, for B10, and made: SAVE_FORMAT 64 and
OLDEST_LOADABLE_FORMAT 64 (GameVersion). A format-63 1900 city before Nov
2020 saved its Contact Centre's choice as "default", which is not written;
it now holds "default~B10". Loaded, it would run 255 agents, not 291, until
the next month's choice: an old save read wrongly, R6e's precedent for E21. RES3 wants 64 as well: whichever ships second bumps to 65 (the orchestrator, 2026-11-24).
M12 and L8 add no saved state: the stays factor is set every month and the
bank's rule is heard at a load, both from the saved founding and finds.

## 7. The default trace and the ensemble

Every change is gated: `Sickness.staysFactor` is 1 when the rows are
today's (`MedicalRows.isToday()`, a default city); `Recipes.choose` and
`apply` do nothing in a default city; the bank in a default city hears
`true` from its founding, and `capitalBuffer()` with the floor
CONSERVATION_BUFFER is the old expression. ModernRowsCheck asserts a default
city's factor 1, its Contact Centre at today's anchor and its bank requiring
the buffer at each month across the three dates. So the default trace is
expected unchanged, and the dry run confirms it: its LongPlaytest report is
byte-identical to main's after the wall-clock fields alone are normalized
(section 9). The ensemble is not owed and was not run; no 80-run score or
baseline is claimed, and the latest baseline stays docs/ensemble/0.9.195.md.

## 8. Checks run by R6f

One at a time on the PC, on 34c278b8b's code, each with the data paths
`-Dcbs.earth=.../earth-2.bin -Dcbs.world=.../world-1.bin`:

| harness | result |
|---|---|
| `./batch-check.sh --fast` | exit 0 |
| ModernRowsCheck | all 177 ok (121 at the mends of e83309220, 160 at 0a4609cd7) |
| ResearchCheck | all 245 ok |
| MedicineCheck | exit 0 |
| PostwarRecipeCheck | exit 0, 231 assertions |
| RecipeCheck | exit 0 |
| ModernEngineeringCheck | all 207 ok |
| SaveFileCheck | exit 0 |
| DiscoveryCheck | all 270 ok |
| BankCheck | 2 FAILED, both main's exact lines in docs/notes/gate.txt (FS9's tab city) |

ModernRowsCheck's causes removed one at a time, each reverted (with the
load mend not yet made, so one red stood under each): M12 out of
`Sickness.STAYS_ROWS`, 8 new reds, all in section 1; B10's 35.7 set to 0, 7,
all in section 2 (at first 2: the harness took its expected posts from the
file's own undo, and now takes them from the source's 14% and checks the
file agrees); `hearCapitalRule(true)` in the month, 7, section 3;
`bufferFloor()` always the buffer, 7, section 3. The pick
(`sh tools/pick.sh origin/main HEAD`) is ALL, Game.java's 209 readers; the
review is owed for money (Bank.java) and saves (the format and its floor).

## 8a. The review (REV-R6f, Codex, money and saves, on e83309220)

Two findings, both mended, each with ModernRowsCheck assertions that fail
without the mend:

- **P1: L8 found in a played month.** The month's only hearing of the rule
  was at its top, before the research; a city that completed L8 ahead of
  history ran the old target to the month's end, and a reload of the same
  month heard the new one. Mend: `Game.hearTheFinds()` after
  `research.month`. Assertions (section 3): a 1900 town with a Law School
  and L8's pile just under (DiscoveryCheck's route) finds L8 in a real
  month 24 before history's; in that month the bank requires the buffer,
  its target 10.5% risen from 8%; saved and loaded, the bank equals the
  live one. With the call removed: 6 reds.
- **P2: the medicine at a load.** The sickness's derived medicine (stays
  factor, long recovery, death lift) was unsaved and not restored: a 1900
  city before M12 loaded at factor 1 until its next month. The defect is
  R2f's, for every stays row; M12 makes it show in the 1990s. Mend: the
  load sets it (`sickness.setMedicine`) once the finds, the month and the
  month's general-care coverage (saved) are back. Assertions (section 1):
  a held town's live factor STAYS_BEFORE_M12, its long recovery, death
  lift, `recoveryAt` and `servedRecovery()` equal after a load, at once.
  With the line removed: 6 reds. The long recovery and death lift are at
  today's at this fixture's month, so those two do not discriminate there:
  the second review (REV-R6fb) asked for the case below.
- **The medicine half of hearTheFinds()** (R6f's own, the same defect for a
  stays row found in a month): a town with a Medical School finds M12 in a
  real month 24 before history's; its factor is 1 in that month and equal
  after a load. With the line removed: 2 reds.

After the mends, on the PC, one at a time: `--fast` exit 0, ModernRowsCheck
all 160 ok, MedicineCheck exit 0, SaveFileCheck exit 0, DiscoveryCheck all
270 ok.

**REV-R6fb** (01:08): P1 and P2 closed, the default city unchanged; one
finding, mended. The load case above sits after M1 and M2, where the long
recovery (1) and death lift (0) are a fresh Sickness's defaults, so their
equalities passed with the load's medicine omitted, and the order against
the coverage's restore was untested. Added (section 1, 17 assertions): the
stock 1900 town played to month 5, before M1 (month 630) and M2 (633),
neither found, its general care coverage the month noted 0.8347 (strictly
between 0 and 1, no building stood for it). Live: long recovery
`LONG_RECOVERY_BEFORE_M1` (0.61878), death lift `SERVED_DEATHS_BEFORE_M2`
times the coverage (0.15071), stays factor 0.66364. Saved and loaded, at
once, before any month: the coverage, the stays factor, the long recovery
and the death lift equal the live ones and the constants. Red with the
load's `setMedicine` removed: 12 of 177 (this group's six: stays 1.0 vs
0.66364, long recovery 1.0 vs 0.61878, death lift 0.0 vs 0.15071, two
labels each; and the M12 group's six). Red with the line moved before
`healthcare.restore`, so the coverage is restored after it: 2 of 177, the
death lift's two (0.18056, at the default coverage 1, vs 0.15071); the long
recovery does not read the coverage and stays green. ModernRowsCheck all
177 ok after, `--fast` exit 0.

## 9. The dry run

`sh tools/gate.sh R6f --dry-run` on the gate box, 2026-11-24, after the
orchestrator's go: run `/home/jerus/cbs/runs/cbs-gate-R6f-ZLaak94X`, kept
(copied to the PC's Temp), main.commit bfbaae192, branch.commit 062310f4c.
The suite finished in 1,100 s with exit 3, known red only: BankCheck,
CurrencyCheck, DeathRecordCheck, DeskCheck, FloatLinkCheck, Forms1900Check,
FundCheck, HoldersCheck, InvestCheck, KeptCheck, PlanCheck, RailDrawCheck,
RoadCheck, ScaleCheck; unnamed.txt is empty; `gate exit 0`, "the tree is as
it was". No harness of R6f's is red.

**The default trace.** Main's report is LP-G1's: its real ship gate
(cbs-gate-LP-G1-EvJqQsEm, 0c7f55602, 0.9.195) reused the dry run
cbs-gate-LP-G1-3DhXEO5s, whose `checks/LongPlaytest.log` has the same
SHA256; that run's tree differs from 0c7f55602's in `src` only by the
version stamp, and bfbaae192's `src` is 0c7f55602's. Both reports have 614
lines. Four line pairs differ raw, none a model number: the header's run
seconds (284 s, 269 s), the layout watch's runtime suffix, and two
`[HH:MM:SS]` prefixes on lines whose money and land figures are equal.
Normalizing only those (target/R6f-trace/normalize.sed), the two are
byte-identical. SHA256: main raw
48555df9cab92bc14e684b7604e33e6be71ef270a0469e96e7c2fbfcbcabdc91, R6f raw
8b78a8bf2c0ca61b4ef3d47a9e0eafdcdb22ec0c40c2050bfbf8975fab70adff, both
normalized 7d11f65d8f26429fd8ce5446c3875ed2dae0b8c8e82c34718dc75913193c6d3f.
