# PW9 - a small firm plant for the early towns

## The finding

REV-PW3g (2026-11-24, its item 3) found that a 1906 town of a few thousand needs
power at m91-m116, before the Diesel Station is known (1910). The only firm row
that can open first is the Oil-Fired Plant, at about 86 MW, and both main and
PW3 order it (CY1 at m81). The planner is right that it is the only choice. The
size is the catalogue's fault: there is no 1900 firm row of about 0.5-1 MW
(PW3 notes section 5, against spec section 2.9).

## Reality

The reviewer's figures, to be checked and cited in the WHY:
- **1902 US census of central stations:** 3,620 stations with about 1.83 million
  hp, roughly 380 kW each. 815 of them were municipal, mostly small.
- **1907:** about 650 kW a station.
- **The largest stations of 1906:** tens of MW, in the big cities.

So a town of a few thousand ran a station of a few hundred kW: steam engines
driving dynamos, or a small hydro plant.

Sources:
- Central Electric Light and Power Stations 1902 (US Census Bureau)
- Scientific American Reference Book, "Central Electric Light and Power
  Stations, United States"
- the 1907 census report

## What to build

- A firm row of the period, sized as the stations of the day, in
  `buildings.json`: a small steam station with its fuel, cost, lead and years
  from the sources. Before adding a new row, look at whether an existing row
  can carry the period's size.
- The planner (PW3's need date) then weighs it like any other row.
- **Harness:** a 1906 town orders the small station, not the 86 MW plant.
  `BuildingDataCheck` passes.
- **Ensemble** against the newest baseline.
- **Saves:** a format bump only if an old save would read wrongly.
- **Order:** after PW3.
