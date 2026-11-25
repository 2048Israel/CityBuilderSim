# PF1 - the power forecast reads works under construction

## The finding

REV-PW3g (2026-11-24, its item 6; PW3 notes section 5) found that the planner's
power forecast is a trend of the draw so far. It carries no works under
construction. In LongPlaytest a Steel Mini-Mill opens at m116 and adds about
6 MW in one month. PW3's need date has no slack for a step the trend cannot see,
so power served sits at 0.57-0.61 from m116 to m150 (and AUTO2 seed 3 likewise).
This is main's forecast, kept through REV-PW3f, and outside PW3's hunks.

## Reality

A utility plans from connection requests. A large load asks for its supply
before it is built, and the utility adds that load to its forecast from the
request. Look this up and cite it in the WHY (load-forecasting practice for
large new loads, for example a utility's interconnection or "large load"
tariff).

## What to build

- The forecast adds the draw of every works under construction from the month
  it is due to open, at its rated draw.
- The need date (`BuildAdvice.timely()`, PW3) reads that forecast unchanged.
- **Harness:** a city with a mill under construction orders its power so that
  it is on line when the mill opens. The same city without the mill does not
  order it. Red under today's trend-only forecast.
- **Ensemble** against the newest baseline.
- **Saves:** nothing new is saved, so no format bump.
- **Order:** after PW3.
