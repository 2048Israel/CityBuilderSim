# R2f — Research: the medical rows (2026-11-16)

## Where it stands

Ready on `batch/R2f`: cut from origin/main 1e88c546 (0.9.126), main fb378433 merged; the dry run exit 0 at aad5e75e7.

## The design (decided before the code)

- **Only a city founded the 1900 way reads it** (R2 ★3). A city founded the
  default way knows every row from its first month and finds nothing here
  (`Research.idleMonth()`), so every constant below is today's there and the
  causes of death follow history's path, as on main: the default trace must
  not move.
- **The cause-of-death steps are brought forward by a find** (R0b ★3: "R2
  turns the named steps into the city's finds"). History's path is kept; a
  row the city found N months before the world takes its step N months
  earlier, and is back on history's path once history has the step too.
  A city that found nothing is history's path to the bit.
- **The mechanisms' constants are undone until the row is known** (R2's
  "the start's figures derived by undoing each entry", as R2d/R2e did for the
  engineering rows): before a row is known in a 1900 city, the constant it
  moves is its pre-row value; from history's month, or from the month the
  city found it, today's.
- **A row with nothing behind it moves nothing** (R6 ★6): where the game has
  no mechanism for the row's effect (no diabetes, kidney failure or heart
  disease among the causes; outbreaks with no disease or season), the row
  stays a dated entry with no effect, for the health study Q17 leaves causes
  of death to. The X-ray and the antitoxin enter no file.

## The rows and the constants each moves

Today's figure is main's (a default city's, and a 1900 city's once the row is
known); "before" is a 1900 city's until history's month or its own find.

| row | world | mechanism in the game | constant (class) | before | today |
|---|---|---|---|---:|---:|
| PM1 pasteurised milk | Jun 1914 | TB deaths: a step brought forward by a find | `MILK_TB` (CausesOfDeath), ramp from Jun 1914, a year | history | x.85 ahead |
| PM2 insulin | Jan 1922 | **nothing behind it**: no diabetes among the causes | - | - | - |
| PM3 diphtheria toxoid | Jun 1923 | the childhood outbreaks' deaths, brought forward | `TOXOID_OUTBREAK`, ramp Sep 1925 to 1931 | history | x.558 ahead |
| PM4 sulfa | Jun 1935 | pneumonia's and mothers' deaths (the knots' 1937-40), brought forward | `SULFA_PNEUMONIA`, `SULFA_CHILDBIRTH` | history | x.75, x.70 ahead |
| PM5 penicillin | Jun 1943 | mothers' deaths (the knots' 1940-44), brought forward; general care's stays | `PENICILLIN_CHILDBIRTH`; `STAYS_BEFORE_PM5` (Sickness) | x.909 of general care's recovery | x.690 ahead; 1 |
| PM6 streptomycin | Mar 1946 | TB deaths (the knots' 1948-52), brought forward | `STREPTOMYCIN_TB` | history | x.26 ahead |
| M1 TB drugs | Jun 1952 | the year-or-more slot's recovery | `LONG_RECOVERY_BEFORE_M1` (Sickness) | x.619 | 1 |
| M2 breathing machine | Sep 1952 | the served long sick's deaths | `SERVED_DEATHS_BEFORE_M2` (Sickness) | x(1 + .181 x coverage) | x1 |
| M3 polio vaccine | Apr 1955 | **nothing behind it**: outbreaks have no disease and no season, the long sick no cause | - | - | - |
| M4 dialysis | Mar 1960 | **nothing behind it**: no kidney failure among the causes | - | - | - |
| M5 hip replacement | Jun 1962 | general care's stays | `STAYS_BEFORE_LATER_ROW` (Sickness) | x.924 | 1 |
| M6 CT scanner | Oct 1971 | general care's stays (the bed-days avoided) | `STAYS_BEFORE_LATER_ROW` | x.924 | 1 |
| M7 angioplasty | Sep 1977 | **nothing behind it**: no heart disease among the causes or the sick | - | - | - |
| M8 assisted living | Jun 1981 | a building: the Assisted Living Residence (`since: "M8"`, read by R2b's planners) | buildings.json | not built | built |
| M9 keyhole and day surgery | Mar 1987 | general care's stays | `STAYS_BEFORE_LATER_ROW` | x.924 | 1 |
| M10 statins | Sep 1987 | general care's stays (hospital days -34%); its deaths have no cause | `STAYS_BEFORE_LATER_ROW` | x.924 | 1 |
| X-ray machine | 1896 | known before every founding, in 1900's rates | - | - | - |
| Diphtheria antitoxin | 1894 | known before every founding, in 1900's rates | - | - | - |

"General care's stays" is general care's part of the month's recovery,
`RECOVERY_SERVED - RECOVERY_UNTREATED`, times the stays factor, the product of
the factor of each stays row not yet known. A fully served 1900 city cures
.5 + .4 x .664 = .765 of its sick a month until June 1943 (today's .9), .792
from then until June 1962, and today's from September 1987, or sooner where
it finds the rows itself.

## The calls (★)

1. **★ Two kinds of effect.** A row whose effect is a step on history's path
   of a cause (R0b's knots, and PM1 and PM3, whose figures the studies give)
   keeps history's path and is brought forward by a find, as R0b ★3 says ("R2
   turns the named steps into the city's finds"), so a 1900 city with no
   research dies as it does on main. A row whose effect is a constant of a
   mechanism, which main holds at today's figure from 1900, is undone until
   known, as R2's brief says ("the start's figures derived by undoing each
   entry"). Recommended and taken.
2. **★ A find brings the step forward by the months it was ahead**: history's
   ramp is moved earlier, rather than started at the find. A city that found
   PM6 a year before the world (March 1945) starts its TB step in 1947,
   history's 1948 less the year, and rejoins history's path when history's
   ramp ends. Recommended: it keeps history's adoption lag, which a find
   does not explain away.
3. **★ PM1, milk: TB x.85** (about 15% of TB cases came from dairy, c.1900
   [MOH-MILK]). Its ramp is a year from Toronto's June 1914 by-law (the year
   est.). The study's "no effect on babies' deaths" moves nothing.
4. **★ PM3, the toxoid: the outbreaks x(1 - .9 x 40.3/82) = .558.**
   Diphtheria's 40.3 per 100,000 in 1900 (the US, [MMWR99], the tenth cause)
   over the outbreaks' `RATE_1900` (82), less 90% (Toronto's three doses,
   [CON-R]). The ramp runs from Connaught's trials (September 1925) to
   Hamilton's 5 cases and no deaths (1931) [CON-D].
5. **★ PM4, PM5, PM6: the knots' own figures** (-25% pneumonia, -30% mothers,
   penicillin's 2.8 per 1,000, streptomycin's .26) and their ramps. The
   harness holds each constant to its knots' ratio.
6. **★ M1: before the TB drugs, the year-or-more slot recovers at 224/362 =
   .619 of the month's rate** (Canadian TB stays fell from 362 to 224 days,
   [M-S1]). The TB sick themselves are counted beside the ring (R0b ★4) and
   keep PM6's deaths; M1 is the beds and the stays, as research-1900 8.6 1
   splits them.
7. **★ M2: before the breathing machine, the served long sick die x(1 + .181
   x general coverage) as often** (est.). The ICU cut respiratory polio's
   deaths from 87% to 22% [M-M5], applied to the respiratory share of deaths,
   pneumonia's 55 of about 900 per 100,000 (Canada 1946-50, [SC-B35] and the
   crude death rate, est.). The unserved die at today's chance either way.
   This is the weakest figure here. "More COLLEGE_HEALTH a bed" is the
   hospital's staffing, a building figure, and is left alone (a city's
   hospital has no recipe to choose, R1c ★6).
8. **★ The stays: PM5 x10/11, and M5, M6, M9 and M10 each x(7.3/10)^(1/4) =
   .924**, on general care's part of the recovery. PM5's is the study's own
   (11.0 to 10.0 days, 1945-50, [SC-B285]). The four later rows split evenly
   the fall from 1950's 10.0 days to 2022-23's 7.3 (CIHI, the age-adjusted
   acute stay, [CIHI-LOS],
   https://indicatorlibrary.cihi.ca/en/hospital-stays-in-canada-2022-2023).
   The even split is est.: the rows' own figures are for their own patients
   (hips, appendicitis, surgery, the heart), and no source gives their shares
   of general care.
9. **★ An undone constant is whole from the month the row is known**, with
   no diffusion ramp: a city's hospitals are its own, with no prices to
   choose a piece at (R1c ★6). Recommended.
10. **★ Nothing behind them: PM2, M3, M4, M7**, and the parts of rows the
    game cannot carry (PM4's recovery, PM5's long-sick deaths, M5's senior
    care, M10's coronary deaths). The rows stay dated in research.json and
    findable, and move nothing (R6 ★6: a row's effect enters with the batch
    that builds what it gives). They wait for the health study that Q17
    leaves causes of death to (diabetes, kidney failure, heart disease,
    polio).
11. **★ The X-ray and the antitoxin enter nothing** (R1c-R). Both were in
    hospital use before 1900 (1896, 1894), so every founding has them and
    1900's rates already include them (CausesOfDeath's first knots; the
    antitoxin's treated cases are the outbreaks that childcare's swing
    already blunts). There is no piece on the hospitals and no catalogue
    row: R1c-R's hospital part closes here.
12. **★ M8 is already behind its building**: the Assisted Living Residence's
    `since: "M8"`, which R2b's planners read. Nothing new.
13. **★ No save field and no format bump**: the rows are read each month from
    the research the save already carries (the found months that
    `Research.restore()` puts back), so an old save reads rightly.

## What the checks showed

- **MedicineCheck** (new, in `AllChecks` after `CausesOfDeathCheck`, 4-6 s):
  196 assertions, 0 failing, at 07016391c. Its seven sections: the mapping
  table (every one of the sixteen rows is a STEP, a STAYS, LONG, SERVED or
  BUILDING row, or NOTHING with its reason, and the catalogue's medicine rows
  are `MedicalRows.CODES` in file order); history's knot ratios hold each
  step's factor; a city that finds nothing is history's to the bit; each
  step's row found 120 and 12 months ahead moves its cause by the step's
  formula and nothing else; the switches flip at the world's month, or at
  the find 60 months ahead; Sickness twins (one with the row unknown) for
  M1, M2 and the stays; real villages (`FittingCheck.village` with two
  General Hospitals) founded the 1900 way, with M1 found here, a PM1 twin
  pair whose TB deaths differ by `ahead()` within 1e-6, and M8's template.
- **The effect skipped by hand, once** (`CausesOfDeath.ahead()` returning 1
  and `Sickness.setMedicine()` returning at once, then reverted): exit 1,
  32 FAILED. Among them: every step's "a month before history's ramp,
  ahead() is the product of the row's factors on the cause", "the rates()
  cell of that band is the cell with no rows times the factor" and
  "...and ahead() is the step's formula" (PM1, PM3, PM4 twice, PM5, PM6);
  "the M1-unknown ring's last slot over the one before is q over one less
  (1 - h)(1 - r x LONG_RECOVERY_BEFORE_M1)"; "with only M2 unknown at half
  coverage the death lift is SERVED_DEATHS_BEFORE_M2 times a half"; "the
  1900 village's stays factor after two months is STAYS_BEFORE_PM5 times
  STAYS_BEFORE_LATER_ROW for each later row"; "PM1 found at month 1 brings
  milk's step on TB forward: the factor in the month played is below 1".
  The 164 that stayed green are the default city, the no-find twins and the
  mapping table, which the skip leaves true.
- **The trace** (the default playtest with `-Dplaytest.trace`, 2,400
  months, all checks passed, at f6f1a90bd): the eight CSVs (bank, bonds,
  borrow, build, house, labour, month, pop) are byte-identical to the gate's
  `cbs-gate-R0h-NrRv6F33/traces/` (2,405 lines each, borrow 55). Size of the
  move: none, as designed (a default city is `MedicalRows.TODAY`, and every
  new path falls back to main's arithmetic), so no ensemble.
- **The pick** (`./batch-check.sh --pick`, at aad5e75e7): exit 3, all 210
  harnesses (AllChecks changed), known red only (InvestCheck, MapCheck,
  DeathRecordCheck, PrizeCheck, main's); `tools/pick.sh`: review not
  needed, no money, saves or threading in the diff.

## The dry run

`sh tools/gate.sh R2f --dry-run`, 2026-11-16, exit 0: the suite in 1,868 s,
known red only (DeathRecordCheck, InvestCheck, MapCheck, PrizeCheck), and no
unnamed lines. It ran batch/R2f at aad5e75e79d909ff9a39de1631385581d4b2db3d
over main at fb3784334233843d4086ef3dd6a40f81d19aafae. The log is kept in
`/tmp/cbs-gate-R2f-ZrCQW0dE/ship.log`
(`C:\Users\Jerus\AppData\Local\Temp\cbs-gate-R2f-ZrCQW0dE`). Only the notes
and the ship file changed after it.
