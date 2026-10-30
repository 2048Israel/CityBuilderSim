# RE2 - Real Estate's workers, back in

Queued 2026-10-30 by RE1's ship file, once the stall's cause was found
(Jerus, 2026-10-29: "sure queue the small thing to find cause"; 2026-10-28:
"gotta add workers to real estate"). The landlords let every home in the city
and employ nobody: their buildings (ids 0, 1, 6) have no `jobs`, against $57B
a month of rent in his San Francisco city at m2,852. The world lane, after
RE1. It goes after EM2 where ScaleCheck is concerned (below).

## What goes back (HD1's fec704a7, as built)

Two DIPLOMA posts a Studio Apartments (80 units) and a Low-Rise Apartments (63
units), one office post and one maintenance post, by the National Apartment
Association's staffing rule; a House none. The landlords do not ask the
staffing gate (`Sector.ordersOnlyWhatItCanStaff()`, false for `RealEstate`),
the build card follows, and `rentBreakEven()` carries the payroll. Take
fec704a7's `buildings.json`, `BuildingManager`, `Sector`, `sectors/RealEstate`,
`BuildCard`, and its edits to `HousingCheck` (section 6) and
`SectorStatementCheck`. On 0.8.77 they apply cleanly. Leave out its
`HistorySave`, `YearBook`, `ui/HistoryScreen` and SaveFileCheck (shipped in
HD1, or not needed now), and its EducationCheck sum (another line now).

## What RE1 found (docs/queue/RE1-notes.md)

The stall was HD1's town, not the posts alone. There, the posts' payroll (9%
of the landlords' rent) took a margin that the driver's property-tax step had
squeezed into a sustained loss. The loss emptied the till, and with no till
the landlords could neither put 15% down nor pass the lender's cover without
it. Home building stopped, and the staffing gate held every other builder.
On 0.8.77 the landlords' margin carries the payroll. With the posts, RE1's
runs give:

- seed 4 ends at 208,664 against 286,350;
- the 16 AUTO2 seeds reach 40,000 people at m916-m1,208 (baseline
  m956-m1,145) and end 0.63 to 1.31 of the baseline, median 1.02;
- ZERO_SLACK seed 4 ends at 160,184 against 196,744;
- no seed stalls.

## What it must do

1. **The posts**, as above.
2. **EducationCheck's surplus fixture**, at its cause. "fixture: last month
   ran a surplus, on the bridge" is red with the posts: the 50% income tax's
   surplus no longer shows within its three months. WN3's comment above it
   says how it is caused.
3. **ScaleCheck section 9**: with the posts its only red is the cash line
   (1.95e-02 against main's recorded 1.20e+00, the same label), on section
   6's town, which has no growing month on main (HD2-S, EM2's). Read it again
   once EM2 has landed. A red that is still the posts' own is traced to its
   flow, as HD1's notes began (Retail's pool).
4. **The ensemble** on the final model. In every seed, look for RE1's
   signature: the landlords at a loss with the down-payment hold for months
   together, population flat, the staffing gate holding the builders.
   `docs/queue/RE1/probe-main.patch` puts each investor's last decision and
   the landlords' books in a TSV a month. If a seed shows the signature, it
   is the fixture for RE1-T (pending, Jerus's call; recommended way: the
   payroll billed with the rent at once).
5. HealthCheck, OrderSearchCheck, SaveFileCheck, LabourCheck, MonetaryCheck,
   BuildingDataCheck, HousingCheck and SectorStatementCheck passed with the
   posts on 0.8.77 (RE1, both data files named). The full suite runs as for
   any model batch.

Every trace moves. A version (`{next}`).
