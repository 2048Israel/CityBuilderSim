# R0c - the 1900 mechanics ({next}, 2026-11-05)

## WHERE IT STANDS

Started 00:41 from `batch/R0b` (R0b's ship file up, at its gate). Notes
first; the slices follow.

## 1. What it builds

The brief is research-1900 §4, the four rows that are tech (hydro is the
electricity run's, the causes of death R0b's, the rest "no new mechanism" or
"leave out"):

| row | the 1900 form | what replaces it, and when |
|---|---|---|
| horses on farms | part of the farm's own `CROPS` fed to its horses (3.1 a farm, ~3 acres of crop each), more posts, no `PETROL` | the tractor (PE14, 1917), fitted when it pays |
| horses for cartage | road freight at a horse's rate a tonne-km, eating `CROPS` | the motor truck (§3, known in 1900), fitted when it pays |
| household light and heat | an unelectrified home buys lamp oil (`JET`) and heat (`THERMAL_COAL`) | the grid's reach: an electrified home buys power for light |
| steam in factories | a factory burns `THERMAL_COAL` on site (~1.8 kg a kWh at the shaft) for the power it would draw | electric drive (§3), fitted when it pays |

## 2. The calls (★)

1. **★ The 1900 forms are built off, and switch on with R1.** Recommended and
   taken. Each form is a share of its sector's capacity that runs the 1900
   way (0 = today's recipe, 1 = all of it 1900's), and this batch leaves it at
   0 in a played city; the harnesses set it. Why: research-1900 §3 says the
   truck, the motor and the tractor spread "when they pay" and "nothing in
   the game needs to schedule that", so what moves the share is R1's
   equipment fitted when it pays (and R2's PE14), not a date this batch could
   honestly write. And R0's call 2 the other way round: 1900's tech beside
   today's wages is a fraction of the profit per worker, so the forms go on
   with R0d's ladder, which R0 already ties to R1. Off, the default city's
   trace does not move, so no ensemble (batch.md 6).
