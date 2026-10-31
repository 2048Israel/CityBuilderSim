# MW1 - a city saved with the world file and loaded without it

Queued 2026-10-31, settling WN4-W the recommended way, "a small batch of its
own", on Jerus's "Take all (Recommended)" (BP1 item 9).

## The finding (WN4-notes §5; the gate's review, PLAUSIBLE, checked from the code: real)

`WorldEconomy.restore()` keeps the path's level and drops the path, so the
dollar walks again from there, and the walk pulls the level to its flat trend
by `TREND_PULL` (0.6% of the gap a month): from m1,200's 6.65, about 0.5% a
month, 6% a year at first, half the gap gone in about ten years. Its band
goes back to the literals as well, WN3's rule for a missing file, which
stays.

## What it does

The walk's trend starts at the level it is handed, so a city that loses its
world file carries on from where it stood instead of drifting to the flat
trend. A fixture: a city saved on the world fixture and loaded without it,
its dollar a decade on within the walk's own spread of the level it loaded
at, not pulled to the trend (asserted against `TREND_PULL` and the walk's
dials); a city that never had the file walks as before. Small: Sonnet
whole, `--pick`, the gate's suite. A fresh city's walk is unchanged, so the
trace should not move; if it does, the ensemble.
