# CB2 — The central bank and monetary checks re-measured

2026-11-15, `batch/CB2`, based on main `6b898500` (RT2, 0.9.113).
Only CentralBankCheck and MonetaryCheck change; no game code or save format.
Default model/playtest trace unchanged by construction; no ensemble run.
The initial known-reds.txt contained no entry naming CB2 or MN1.

## Central-bank measurement and calls

- ★ Keep the existing twelve shapes and append the playtest's next twelve
  seeds: the second House tranche is 21 instead of 20; the first tranche
  remains 40–43 and the initial run 3–5 months. No city was chosen for its
  result. The school/advice rhythm, month-600 branch, 240-month horizon,
  target and rule are unchanged. Existing shapedCity overloads keep twenty.
- The original 43/4/20 probe and its twins retain the held-off causes of
  death. The other 23 cities retain normal deaths. The temporary diagnostic
  first ran all 24 with normal deaths, then replaced seed 7's reading with
  its exact existing special case. Both readings hold; the normal-death
  variant is not counted as an extra city.
- ★ Keep STRICT_SHARE at 3/4: **24 of 24 hold, zero reversals**, rather than
  the earlier RT1 path's 8–9 of 12. With 24 cities the threshold is now 18,
  allowing six reversals. Today's measurements give no reason to relax it
  to the suggested 2/3. Orchestrator accepted this call in bus message
  `20261115-104029-526-orchestrator-452e`.
- Median gap: **-0.505818 points/year**; individual gaps
  -1.951140 to -0.031174. Very strict has a higher mean actual
  policy rate in 23 of 24 cities; seed 9 averages a lower rate after
  feedback, so its lower inflation does not establish a dearer-money effect.
- **Under-target question:** seven cities already read under
  `DebtManager.DEFAULT_INFLATION_TARGET` (2%) at the branch: seeds
  2, 4, 10, 13, 18, 20, 22. Every one has higher mean rates and lower prices
  under very strict. This measurement finds no case of dear money raising
  prices in an already-under-target city. All Standard twins' *whole-horizon*
  inflation exceeds 2.3%; the older RT1 clustering below that reading is
  absent today, so it supplies no present evidence of that correlation.
  This is evidence from these cities, not a universal claim about every path.
- The diagnostic also counted monthly inflation readings under the actual
  model target; see the last column below (strict/Standard, out of 240).
  Its separate comparison with Standard's unbounded rule is not used as a
  count of dearer policy: bounds can clamp both rates to zero. Mean *actual*
  policy rates below establish which comparisons have dearer money.

All inflation and policy figures below are percentages; the gap is percentage
points/year. `h/m/next` is the two House tranches and months between them.

| Seed | h/m/next | Branch inflation | Very strict | Standard | Gap | Mean policy strict/Standard | Months under target strict/Standard |
|---:|---|---:|---:|---:|---:|---:|---:|
| 0 | 40/3/20 | 4.492 | 5.874 | 6.478 | -0.605 | 11.767/9.724 | 25/14 |
| 1 | 41/3/20 | 4.053 | 5.365 | 6.060 | -0.696 | 10.627/9.018 | 43/35 |
| 2 | 42/3/20 | 1.169 | 2.238 | 2.521 | -0.283 | 4.848/3.951 | 106/99 |
| 3 | 43/3/20 | 3.112 | 4.617 | 5.082 | -0.465 | 9.242/7.606 | 65/53 |
| 4 | 40/4/20 | 1.765 | 3.688 | 4.236 | -0.548 | 7.259/6.260 | 69/59 |
| 5 | 41/4/20 | 3.115 | 4.545 | 5.066 | -0.521 | 9.019/7.536 | 51/43 |
| 6 | 42/4/20 | 2.533 | 4.363 | 4.833 | -0.470 | 8.684/7.441 | 41/28 |
| 7 | 43/4/20 | 3.017 | 4.155 | 4.567 | -0.412 | 8.387/6.885 | 68/60 |
| 8 | 40/5/20 | 2.473 | 4.135 | 4.166 | -0.031 | 8.555/6.457 | 45/61 |
| 9 | 41/5/20 | 4.678 | 1.140 | 3.091 | -1.951 | 4.825/5.189 | 154/108 |
| 10 | 42/5/20 | -1.477 | 4.354 | 4.395 | -0.041 | 8.328/6.434 | 52/55 |
| 11 | 43/5/20 | 4.079 | 5.305 | 6.201 | -0.896 | 10.506/9.204 | 44/26 |
| 12 | 40/3/21 | 2.605 | 4.281 | 4.555 | -0.274 | 8.709/6.811 | 65/46 |
| 13 | 41/3/21 | -0.542 | 4.373 | 4.927 | -0.554 | 8.814/7.201 | 64/47 |
| 14 | 42/3/21 | 2.922 | 3.059 | 3.518 | -0.459 | 7.367/5.712 | 78/86 |
| 15 | 43/3/21 | 3.969 | 5.102 | 5.941 | -0.839 | 10.154/8.842 | 55/27 |
| 16 | 40/4/21 | 3.736 | 4.390 | 4.877 | -0.486 | 8.760/7.300 | 52/44 |
| 17 | 41/4/21 | 2.329 | 2.197 | 3.917 | -1.720 | 6.689/5.842 | 123/75 |
| 18 | 42/4/21 | -4.568 | 3.226 | 3.598 | -0.372 | 6.665/5.525 | 65/55 |
| 19 | 43/4/21 | 2.965 | 4.315 | 4.805 | -0.490 | 8.676/7.171 | 68/48 |
| 20 | 40/5/21 | -1.095 | 2.345 | 2.901 | -0.557 | 6.617/5.325 | 111/107 |
| 21 | 41/5/21 | 3.393 | 1.518 | 2.344 | -0.826 | 4.847/4.281 | 143/110 |
| 22 | 42/5/21 | 1.861 | 4.566 | 4.986 | -0.420 | 9.002/7.412 | 60/41 |
| 23 | 43/5/21 | 7.233 | 4.646 | 5.272 | -0.626 | 9.392/7.969 | 23/17 |

Reproduction: the package-local diagnostic uses the harness's shapedCity
founding and steps, scratch GameFiles, a saved month-600 branch and two loads,
setting VERY_STRICT and STANDARD respectively. Its annualization is exactly
strictAgainstStandard's, `(end / start)^(12 / STRICT_HORIZON) - 1`; rates are
averaged from the actual policy dial after each strictMonth. Seed 7 uses
probeCity and reapplies holdCausesOfDeathOff on each load, since it is unsaved.
Raw local logs: `target/cb2-probe/measurement.log` and `exact.log`.

## Monetary measurement and calls

- Keep the existing settled founding, held Agriculture, four held rates,
  and measurement window. Crucially, main() sets
  `BuildingManager.buildersShortOn = false` (FC1); the final diagnostics
  keep that premise too. The shelf settles after 21 extra months and the
  held window is months 46–81, from the same 507-person founding.
- The **normal harness route** reads annual inflation at 3/10/20/40% of
  **17.90/15.53/12.93/12.46%**. The full range transmits **5.446 points**,
  against the unchanged one-point `TRANSMISSION_FLOOR`.
- The same four rows held one month late differ by
  **0.000/0.276/0.322/0.320 points**. The former 0.100-point timing premise
  is missed today; the largest is now the 20% row, not the old 40% row.
- ★ Set MEASUREMENT_NOISE to **0.0065 (0.65 points)**, about twice the
  largest 0.322-point reading, rounded up to a hundredth of a point. The
  dial sentence records the setup, method, readings and lag boundary.
  Rates, founding, timing, one-point floor and acceptance laws are unchanged.
- Measurement correction before the monetary source was committed: the
  first isolated section probe omitted main's FC1 hold and read
  0.000/0.114/0.211/0.194 points, initially suggesting 0.43. The normal
  13-harness pick exposed the omitted setup. That calibration and its
  four-month lag result were discarded; all final probes below use FC1 off.
  The diagnostic with FC1 on was never a proposed premise change.

## Fault probes

The fault this allowance must leave detectable is the opposite price
response from an inverted policy selector: the reached inflation-ordering
and one-point transmission laws must still fail.

- **Strict share:** preserve the existing negative fixture. It swaps actual
  measured strict/Standard inflation pairs one city at a time: green up to
  six reversals, red from seven onward at the 18-of-24 threshold. The normal
  CentralBankCheck run confirms this boundary. The fixture uses the real
  measured gaps, not an arbitrary epsilon.
- **Monetary response:** invert the held-rate row selector in a temporary
  JVM, run the same eight real cities, and restore the array in finally.
  Same-founding and ending-dial assertions hold; the timing differences are
  0.320/0.322/0.276/0.000, within 0.65. Two ordering assertions and the
  one-point floor print FAIL: **three hard failures**. The smallest adjacent
  difference, 20% against 40%, is within the allowance; the other two and
  the full-range floor still reject the opposite gradient. This is an
  intentionally inverted harness selector, not a production model defect.
- **Policy lag:** heldRun at the existing 40% row, with HELD_FROM offsets
  0–6 and the original measurement window. All seven fingerprints are
  bit-equal and every ending dial is 40%. Six months misses the 0.65-point
  premise; five does not. Thus a half-year policy lag is detected, but
  delays of up to five months fit within this calibrated headroom.

| Months late at 40% | Annual inflation | Difference (points) | Within 0.65? |
|---:|---:|---:|---|
| 0 | 12.457838% | 0.000000 | yes |
| 1 | 12.778331% | 0.320493 | yes |
| 2 | 12.823079% | 0.365241 | yes |
| 3 | 12.892774% | 0.434935 | yes |
| 4 | 13.025652% | 0.567814 | yes |
| 5 | 13.031834% | 0.573995 | yes |
| 6 | 13.122581% | 0.664743 | no; observed premise miss |

★ Keep PO1's classifications: a real sample reversal or timing lag now
prints **OBSERVED rather than failing**. The negative strict-share fixture,
same-founding/dial laws, reached ordering and transmission laws stay hard.
This is information for Jerus, not a CB2 classification change. Orchestrator
confirmed in `20261115-104636-367-orchestrator-c1e5`; the calibration rule and
recording the actual detectable lag in `20261115-105156-397-orchestrator-ccef`.

Raw final local logs: `target/cb2-probe/lag6-final.log`, `fault-final.log`,
and `target/checks/MonetaryCheck.log`. Reproduction for the monetary probes:
set buildersShortOn false as main does; call heldRun with rate .40 and
HELD_FROM + lag for the table; call inflationFallsWithTheRate with a reversed
copy of HELD_RATES for the negative selector, restoring its original order
in finally. All cities use scratch GameFiles/TempDirs, never real save slots.

## Verification and publication

- Initial unmodified-main `./batch-check.sh --fast`: exit 0, build/maps,
  zero firm Stale findings and LF checks, tree unchanged.
- Post-edit fast check passed. Local serial `./batch-check.sh --pick`:
  **exit 0, 13 harnesses pass**, including CentralBankCheck's final 24-city
  sample and its negative fixture, MonetaryCheck and LongPlaytest.
  This run still had the provisional 0.43 allowance; it supplied the
  correct normal-route monetary measurement. Log:
  `target/cb2-probe/final-pick.log`.
- Final `./batch-check.sh MonetaryCheck` on the 0.65-point allowance:
  **exit 0**, one harness pass, compile/maps and zero firm Stale findings.
  Log: `target/cb2-probe/monetary-final-check.log`.
- Retained merge dry run follows. No ensemble:
  only harness code changes, so the default model trace is unchanged.
- No scripts, runner or process rules change. No known-red entry is retired,
  transferred or added; PO1 already removed the former CB2 entries.
