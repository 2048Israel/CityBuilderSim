## changelog
### PF1 — the power forecast reads works under construction — 2026-11-25, ready, see docs/queue/PF1-notes.md
REV-PW3g read LongPlaytest's m116-m150 power served at 0.57-0.61 as a power forecast blind to a mill under construction.
PF1's trace on 0.9.208 found that the forecast already carries every non-power works on site, at its rated draw, from the
month its queue opens it (`WorksValue.pendingPowerLoad()` in `BuildAdvice.powerSupplyDemand()`, since HW1), and the need
date reads it, so no game code changes. PowerPlanCheck 9 now pins the law end to end. Two towns know only the Diesel
Station. In the one with a Large Mini-Mill on site, whose opening falls a month at a time, 6 diesels are ordered on line 12
months before the mill opens. Its twin without the mill orders nothing at any of 48 steps, and under a trend-only forecast
three of its checks go red. A utility plans a large load the same way, from its signed request on its ramp schedule
(ERCOT's 2025 Long-Term Load Forecast). The 1900 default city never learns the mill in 240 months. Today's city still steps
at m120, from plant leads of 30-130 months against a 29-month build, and has no small firm row before the 1910 diesel (PW9's).
Untagged: the game is unchanged.
