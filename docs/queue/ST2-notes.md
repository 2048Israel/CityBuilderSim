# ST2 - ScaleCheck section 6's town on LD1, IC1 and EP1: month 718, a draw (2026-11-01)

The brief is `docs/queue/ST2.md`. Built on unmerged EP1 (origin/batch/EP1 at
6e1cffab, then 4d6bb5ec, IC1 merged in), on the orchestrator's word: EP1 could
not ship green without this, and ST2 was waiting on EP1. EP1 ships it folded
in; this branch has no ship file.

## 1. What changed

`ScaleCheck.FREE_FIXTURE_MONTHS` goes from 1124 to 718, with a javadoc paragraph
saying it is a draw. No other line of the harness, no model change, no dial.
- At the factor: **All checks passed** (main's record has 2 FAILED).
- At `-Dcbs.priceLevel=1`: main's "...and it builds: more homes at the end than at
  the start" twice, and 2 FAILED, word for word main's record.

## 2. The town at the factor (EP1 + IC1 + LD1)

| month | people | |
|---|---|---|
| 200 | 3,680 | flat since 150, rents squeezed |
| 520 | 9,122 | slow growth from 365 |
| 715-905 | 9,278 to 27,924 | the boom: places from 11,248 to 33,998 |
| 1,000 | 21,824 | the bust from 910 |
| 1,124 | 14,726 | where section 6 sat: shrinking, priced out |
| 1,400-2,400 | 11,300-13,300 | flat |

## 3. The scan

`docs/queue/ST2/` (ST3 reruns it):
- `ST2Scan.java`: one trunk played by `playtestCity()`'s opening and rhythm, one
  month at a time. At each month asked for, a copy loaded from the trunk's save
  is read through sections 6 and 9 in ScaleCheck's own statements (the sections
  skipped do not read the month). About 1.5 s a month.
- `scan.sh`: the coarse scan.
- `exact.sh`: one month per JVM, the town read live (`-Dst2.live`), as ScaleCheck
  reads it.

Fidelity:
- The trunk at m1124 equals `playtestCity()` played fresh, to the digit.
- The live probe at 1124 equals ScaleCheck's own section 6 and 9 output, to the
  digit.
- A *loaded* copy of the grown town does not. Its free copies start equal and part
  by 0.06% of people in six months, which moves some tracking verdicts. So the coarse
  scan finds candidates and `exact.sh` decides. (A reload of a grown town playing
  otherwise than the live one is ReloadCheck's ground; noted, not traced.)

The coarse scan read every fifth month from 150 to 1,300 and every tenth to
2,400, at the factor and at factor 1. At the factor no month held every line.
What failed, by stretch:

| months | people | what fails |
|---|---|---|
| 150-530 | 3,500-9,000 | its own rents price migrants out in nearly every month; the copies' pull and "it builds" with them |
| 625-790 | 9,100-15,000 | section 9's people and unemployment, with prices, cash or "ordered" in some months |
| 795-905 | 16,000-28,000 | calm, growing and building, but both copies miss "the free copy ordered - an order sized from more than a million needed" |
| 910 on | the bust | growing and calm both fail |

**Why the boom misses the order line.** A free copy's largest order there is about
one building per K: 0.7 to 1.4 per K at 796-905, against K = 5e9 / people,
180,000 to 310,000. A building is one size at any K, so the copy orders K times
its city's fractional gap. "More than a million needed" holds reliably only when K
reaches about a million, about 5,000 people at the 5-billion copy. Single months
pass on one big order: 23 to 27 per K at 841, 859 and 889.

Exact, at the factor:
- m710-726: every line holds at **717, 718 and 722**. 710-716 and 724-726 miss
  section 9's people and unemployment; 719-721, 723 and 725 also miss "ordered".
- 850 and 900 miss "ordered" (both copies) and a tracking line; 889 misses section
  9's cash.

At factor 1, the same town is another draw. Loaded copies hold every line at 895,
945-955 and 1,125-1,140, and miss one line in most months of 1,065-1,145; there its
5-billion copy orders 10 to 17 buildings per K. Exact at factor 1: 717, 718 and 722
fail main's "it builds" twice and nothing else.

## 4. The call (★, settled by the orchestrator under BP1, the recommended way)

- ★ **718 now, and ST3 for a town of its own plan (Recommended, taken).** 718 is
  green at the factor and main's record at factor 1, but it is three months of
  seventeen, not a band: the next batch that moves the trace may leave it red.
  ST3 (`docs/queue/ST3.md`) gives section 6 a town of its own plan that is calm,
  growing and small enough that K reaches a million, and ends the re-picking.
  The other way was to build that town now: hours with an uncertain result,
  with EP1 waiting.

## 5. How it was done

Opus alone: the scanner (a probe, about 200 lines), the scans, the month, the
javadoc, ST3's brief. No coding slice went to Sonnet; the harness change is one
constant and its paragraph. No ensemble: the model is untouched.
