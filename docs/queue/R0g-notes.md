# R0g - the causes of death: R0b's review follow-ups ({next}, 2026-11-07)

## WHERE IT STANDS

**Ready.** Branch `batch/R0g` from `batch/R0e`'s ship file (RL1, cdcb0a67); main with R0e in merged at MERGE. One slice,
drafted by Sonnet 5.5 at xhigh and reviewed here (COMMIT); the probes, the CentralBankCheck comment and the two queued
briefs written here. The game's own month is untouched: a fixture's hold and two harness sections. So the default trace
is main's to the byte (section 6), and there is no ensemble. `--pick` on the merge: **EXIT**.

## 1. What R0g is

R0b's gate review (SHIP + FOLLOW-UP), three items and a minor one, each caused by a fixture first:

| item | done |
|---|---|
| 1. a reload mid-influenza played on against an unbroken run | CausesOfDeathCheck 10a: January to March 1919, to the bit (section 2) |
| 2. the fixtures' influenza hold lost on load | `Game.buildWorld()` gives the load's new `Health` the hold again; 10b (section 3) |
| 3. CentralBankCheck's very strict twin at -4.28% on 1900's deaths | measured over the twelve `STRICT_SHAPES`: the one probe city, said in its comment (section 4) |
| minor: an influenza case still sick past two months dies again through `Sickness` | measured at about 11%, not 1%: queued as R0h (section 5) |

## 2. The reload mid-influenza

10a plays a city on the playtest's founding (`LongPlaytest.founding()`) to December 1918, saves and reloads it, and plays
January to March 1919 on both. Each month it compares `DeterminismCheck.fingerprint`, every `MoneyAudit.pools` part, the
cohorts' people by band, the dead by cause and the causes' whole state, Health's sick and influenza rates, and
`Sickness`'s rings: **all three months to the bit**. The city is small, 404 people in December 1918, 2.5% of its
workforce off sick with the influenza and 0.3 of its dead that month. But each month books influenza dead and sick, so
every figure the influenza adds crosses the save.

At first it ran on the section's own city, `founded()` (SicknessCheck's fixture), and that city's cash parted, by 0.16
in January 1919. That is not the influenza. A probe (scratch, not committed) found the drift from month 3, with or
without the influenza and its hold, and it is `stock()`'s doing. With its 2,000 houses and the rest put up complete in
bulk (`addStack(t, n, true)`), the city reloads with its cash drifting (+0.0143 a month on at m3, +1.28 at m60). The
same city bare, with only `setCashForTest`, and on the playtest's founding each reload to the bit, at m3 and m12. The
last also did at m60 and m228, December 1918, mid-influenza. The slice's reflective diff named the unsaved fields that
part (the buildings' dues, the markets' trades and ring offsets, `worldBandExports`, the sectors' supplier credit and
fleet diesel, the business debt manager). Tracing which one is the leak is not R0g's: **queued as SR1**
(`docs/queue/SR1.md`), with the figures. R0b's notes saw the same drift and left it.

## 3. The fixtures' hold across a load

`Game.loadGameSave()` calls `buildWorld()`, which makes a new `Health` and so dropped its influenza hold, while
`causesOfDeath`, a final field, kept its own. So a fixture that held a new Game and then loaded it (CentralBankCheck's
twins, DeskCheck, FundCheck, AutoBuildCheck and the rest) got the influenza's sick back from October 1918.
`buildWorld()` now gives the new `Health` the hold when the causes are held; `Health.isInfluenzaHeldOff()` is its
getter. The hold is still not saved (a NEW Game is held by its fixture again), and the javadocs say so. 10b saves the
city at September 1918, holds a new Game and loads it (the fixtures' order), and plays October 1918 to March 1920: no
influenza sick and no influenza dead in any month, against the model's sick in those months. With `buildWorld()`'s
line taken out, 10b's two lines fail (the slice ran it).

## 4. The very strict central bank on 1900's deaths

The probe city's very strict twin read -4.28% in its last year on R0b's tree, and failed "within a full miss of the
target, no spiral" (`Expectations.TOLERANCE + MISS_SCALE`, 5 points either side of the 2% target), so R0b held its
causes off. Measured here over CB1's twelve `STRICT_SHAPES`, all with 1900's causes on, each played as
`CentralBankCheck.shapedCity()` plays it to `STRICT_BRANCH` and its twins `STRICT_HORIZON` months on R0e's tree:

| houses, months | people at m600 | very strict: a year, last year, dial top | Standard: a year, last year |
|---|---|---|---|
| 40, 3 | 14,217 | 3.773%, 2.89%, 19.89% | 4.226%, 2.08% |
| 41, 3 | 11,691 | 3.069%, 2.65%, 21.56% | 3.850%, 1.75% |
| 42, 3 | 13,755 | 2.060%, 0.28%, 33.68% | 3.593%, 2.43% |
| 43, 3 | 20,348 | 3.968%, 1.00%, 20.09% | 4.393%, 1.18% |
| 40, 4 | 21,571 | 2.478%, -1.47%, 28.82% | 2.678%, -1.77% |
| **41, 4** (the probe's) | 16,429 | 4.822%, **1.19%**, 32.05% | 5.483%, -2.83% |
| 42, 4 | 14,103 | 1.510%, 0.58%, 13.18% | 1.707%, 0.55% |
| 43, 4 | 20,775 | 3.843%, 4.57%, 43.98% | 3.878%, **-7.52%** |
| 40, 5 | 13,544 | 4.930%, 1.00%, 22.42% | 5.545%, 2.11% |
| 41, 5 | 16,779 | 1.051%, -1.11%, 19.88% | 1.162%, **-3.27%** |
| 42, 5 | 15,658 | 4.370%, 1.58%, 21.88% | 5.208%, 2.12% |
| 43, 5 | 16,177 | 3.841%, 2.01%, 20.85% | 4.497%, 1.84% |

Very strict's last year is within a full miss in all twelve (-1.47% to 4.57%), and its dial never reaches
`MAX_POLICY_RATE`. Even the probe's own shape reads 1.19% on this tree. So the -4.28% was that one city's path on R0b's
tree, not 1900's deaths tipping the bank into a spiral, and `probeCity()`'s comment now says so; the hold stays for the
calm path its other premises need. No row. Beside it, not R0g's, and with no line in the harness to read it: Standard's
last year is past a full miss in two shapes (43/4 at -7.52%, 41/5 at -3.27%). And very strict held prices lower than
Standard in all twelve here (CB2, queued, re-dials `STRICT_SHARE`; nothing is re-dialled here).

## 5. The influenza's sick die twice

The influenza's sick are a term of `Health`'s sick rate, and `Game` hands the whole rate to `Sickness`. So whoever is
still sick of it past two months dies at the long sick's chance, on top of the influenza's own dead. The review guessed
about 1%, in December 1918 only. A probe on `founded()`'s city (9,000 people in October 1918), two twins loaded at
September 1918 with Health's influenza sick held off in one, gives the ring's extra dead:

| month | the ring's extra dead | the influenza's own dead |
|---|---|---|
| October to December 1918 | 0.000, -0.000, -0.001 | 22.40, 22.17, 6.27 |
| January to April 1919 | +2.24, +2.65, +1.12, +0.38 | 1.87, 1.86, 1.85, 1.85 |
| January to March 1920 | +0.03, +0.26, -0.06 | 1.56, 1.55, 1.54 |

That is about 7 extra on the influenza's 63, about 11% (9.5, 15%, counting the months between, where the twins drift
about 0.3 a month apart through their economies). In January and February 1919 the ring killed more of the influenza's
sick than the influenza did. None of it falls in October to December: the ring's first deadly slot is three months
on. Counting once is not small. The ring holds shares, not causes, so it needs the influenza's share carried beside the
ring (a saved field, the screens' sick by age and the long-sick bars), and the default trace would move (the ensemble).
**Queued as R0h** (`docs/queue/R0h.md`), with the shape of the fix and its alternative.

## 6. The checks

- **The trace.** The default playtest (2,405 months) traced on R0e's tip (cdcb0a67, built before the slice) and on
  R0g's tree, run side by side: all eight CSVs byte-identical; the logs differ only in "in 57s"/"in 58s" and the layout
  windows' "2.1 s"/"2.2 s". So no ensemble (`docs/batch.md` 6). The model's one new line runs only when a fixture
  has held the causes, which the game never does.
- **CausesOfDeathCheck**: exit 0. 10a's city takes 1.6 s to December 1918 and 0.3 s for its three months on both cities;
  10b takes 0.4 s.
- **`--pick`** on the branch before the merge (30 of 186 harnesses for 52 files against 154eb0b9, R0e's among them;
  LongPlaytest for Game.java, BuildingManager.java and Good.java): **exit 3**, main's recorded MapCheck red only
  (`docs/notes/gate.txt`). PICKMERGE
- One in, one out (`docs/batch.md` 10) does not apply: no process file changed.

The scratch probes (`R0gProbe`, `R0gDrift`) and their output were deleted once their figures were in these notes.
