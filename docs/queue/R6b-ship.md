<!-- gate-run: /tmp/cbs-gate-R6b-WnmIOf8F -->
## changelog
### {next} — R6b, Research: the near-future rows — 2026-11-17, ready, see docs/queue/R6b-notes.md
The near future's first rows, each with what it gives. `research.json` learns `projected` and `window`, held to
`ResearchCatalog.HISTORY_ENDS` (November 2026), and a projected row says so wherever its date shows: the inbox,
the research screen's dots, rows and flags, the build card's origin. A city founded today's way never knows a row
from `NEAR_FUTURE` (January 2025) on, so the default playtest is unchanged to the bit. A city founded the 1900 way
gets **FE1**, the driverless truck: a kit at the Truck Depot, a truck working 20 hours, not one 10-hour shift, with
a tenth of a post watching it (both estimates). **FE2** is the Sulphide Leach & SX-EW Plant (153): cathode from a
sulphide field at 85%, no smelter. **FE4** is the Hydrogen Steel Works (154, projected 2028): pellets and 3.48 MWh a
tonne into steel, paying where it replaces imported steel on cheap power. Saves go to format 55, and the oldest
loadable with them: a format-54 save holds no count of the depots' kits unfitted, which would read as fitted. New:
`DriverlessTruckCheck`, `SulphideLeachCheck`, `HydrogenSteelCheck`, `ResearchCheck` 11-13. FE3, FE5, FE6, FM1
and FM2 wait for their batches.
## pending
| R6b-P | The near future's rows for their owners' batches ({next}): FE3 and FE6 after PW2 and PW4, FE5 after a storage batch, FM1 and FM2 after R2f and the gap's mRNA row | a row enters `research.json` in the batch that builds what it gives (R6's ★6); PW and FM2 are held | [the near-future study §10](docs/studies/research-near-future.md#10-the-brief-for-r6b) |
## queued
| EDU1 | `EducationCheck`'s "a poor city at x3 enrols fewer than at x1: the trap is back", red on main and seen by no gate, the harness being nightly-only (HA1): main's aeac6e5a classes enrol 1,652 at x1 and 1,632 at x3 (willing .72 and .35, the test wanting under .9 of x1); find which change closed the trap's gap, and whether the model or the premise moved (found by R6b's pick, 2026-11-17) | [R6b's notes](docs/queue/R6b-notes.md) |
## known-reds
EducationCheck | a poor city at x3 enrols fewer than at x1: the trap is back | EDU1 | 2026-11-17 | main's own, a nightly harness the gate skips: main's aeac6e5a classes give the same line and figures
KeptCheck | each month hands back nine in ten of the nodes the first draw made, or more (256 of 299) | FB6 | 2026-11-17 | Section 55, the Bank's Profit page, in R6b's dry run on main ec481b4a only: the first draw 299 nodes and one month 93 made, a block of the page dropped and redrawn; run alone, main ec481b4a and this branch both draw 297 and keep 289 (at most 14 made a month), so the fixture's page is not held still under the suite's load, FB6's family (KBF1, KF63)
