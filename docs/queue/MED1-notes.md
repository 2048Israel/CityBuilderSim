# MED1 - a health study: notes

The batch is docs only: `docs/studies/health.md`, its five source sheets in
`docs/studies/health/`, and the study's row in `docs/studies/README.md`. No
code, no harness, no version (no {next}; shipped `--no-tag`). The brief is the
queue's MED1 row: research-design Q17 and R2f's ★10.

## What was read

**The game's side** (Opus):

- `CausesOfDeath`: the seven 1900 causes, the knots, R2f's `STEPS` and
  `ahead()`, `rates()`, `book()`, `getState()` and `restore()`.
- `PopulationCohorts`: the one line that adds the base, `Sickness`, violence
  and the causes (L309).
- `Sickness`'s undone constants and `MedicalRows`.
- `research.json`'s medical and law rows; `buildings.json`'s healthcare
  buildings and `since` codes (the Assisted Living Residence on M8);
  `Equipment`.
- `docs/month-order.md` steps 33-37.
- R2f's notes, R6-G's study and Pending rows, research-design §4 and Q17, and
  `licences.md`.

**The sources:** five agents gathered them, one per sheet, on 2026-11-17. Every
figure is marked page, snippet or memory and keyed to a source with its licence.

## What was found and decided

- **Today's base hides heart disease and cancer.** After 1980 every death is
  `AgeBand`'s base or `Sickness`, the same rate from 1900 on. Naming five causes
  of today as shares of that base, each with a history factor H that is 1 from
  2024, gives every row in R2f's ★10 and R6-G-P something to act on, through
  R2f's own `Step` and `ahead()`. The default city's deaths move from 1900 to
  2024, so MED2 runs an ensemble (★1).
- **Heart disease and stroke are two causes**, as Statistics Canada counts them.
- **H is not applied to babies and children.** Their history is childcare's
  swing, which R0b's causes split.
- **The elders' heart and stroke H is fitted to the period's life tables.** On
  the all-ages ratios the elders' 1950 rate reaches about 30% a year, too high,
  so MED2 checks every band against Statistics Canada's life tables for 1921,
  1951 and 1981.
- **The step factors come from Ontario's split of the coronary fall**
  (Wijeysundera 2010) and the trials:
  - M7: x.978 (est.)
  - M10: x.938 (est.)
  - L6: x.944 (est.)
  - M13: x.998
  - M17: x.995 (est.)
  - PM2: by band, teens x.54
  - M11: babies' pneumonia x.61
- **Some rows move nothing.**
  - M14's trial measured repeat procedures, not deaths: it goes to stays on
    R6f's pattern.
  - M15 and M18 act on causes with almost no deaths today.
  - M16, M21 and M22 had no effect that shows by 2024 (★5).

  All of these enter as dated entries, as R2f's did.
- **Polio is a cause on Canada's own years**, 1927-1964 (PHAC, Open Government
  Licence - Canada), with M3 as its step.
- **The register with Canada's dialysis counts (CIHI's) is not licensed for a
  sold game.** USRDS's is public domain. MED5 reads it.
- **Closed, with reasons in the study §8:** PM4's recovery, PM5's long-sick
  deaths and M5's senior care. No figure acts on anything the game has.

## The calls

All six are in one Pending row, MED1-K. Each is recommended (a), with its
reason:

- **★1, the history factor (a).** Without it the rows have nothing to bring
  forward.
- **★2, the hospitals serve the dialysis pool (a).** Canada's dialysis is run by
  hospital renal programmes, the catalogue calls M4 equipment, and the General
  Hospital's figures already carry it.
- **★3, leave out the mental hospital and chlorpromazine (a).** The evidence
  measures the beds, stays, staff and cost, but no outcome a served patient
  gains. The homelessness link is weak (Winkler 2016).
- **★4, L6 a dated row, not a lever (a).** No cost of the ban was gathered, so
  a lever would be a free switch.
- **★5, steps cut at what history took by 2024 (a).** That keeps today's rates
  the game's rates from today on.
- **★6, polio's deaths only (a).** The share of survivors left disabled was not
  gathered.

## Pending rows that name MED1, settled

**R6-G-K ★2 and ★3: taken as recommended.**

- ★2: MED2 names today's pneumonia, children's diarrhoea and TB. MED3 takes M11.
- ★3: MED2's cancer cause. MED3 takes M13 and M17; M16 is a dated entry.
- FM2 needs only M17 in the catalogue, and stays held.

The row stays for ★1, ★4, ★5 and ★6, which are R6e's, R6f's and others'.

**R6-G-P's medical rows are all in MED3:**

- M14, M21 and L6;
- M11, M15, M18 and M22;
- M13, M16 and M17.

The row stays for its other owners: PW2, PW3, HO4, HT1 and FM1.

## Batches queued (the ship file's `## queued`)

Ids were checked free with `tools/batch-id.sh`. In order:

1. **MED2:** today's causes.
2. **MED3:** the rows' steps, after R6f.
3. **MED4:** polio and M3.
4. **MED5:** dialysis and M4.

Each touches `CausesOfDeath`, `CausesOfDeathCheck` and `MedicineCheck`, so none
runs beside another. FM2 and FM3 are untouched.

## Process

- **The changelog heading.** The orchestrator gave the heading as "MED1, a
  health study - <date>, ready, ...", with one em-dash. `ship.sh` requires two
  (`^### .+ — .+ — .+`), so the ship file's heading is
  "### MED1 — a health study — 2026-11-17, ready, see
  docs/queue/MED1-notes.md".
- **No process files changed**, so there is no line count to give.

## The dry run

(To be filled in after the dry run.)
