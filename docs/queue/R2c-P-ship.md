<!-- gate-run: /tmp/cbs-gate-R2c-P-dIFjs3NN -->
<!-- readiness: provisional -->
## changelog
### {next} — R2c-P, public works' licence fees — 2026-11-18, ready, see docs/queue/R2c-P-notes.md
In a city founded the 1900 way the city's own works now pay R2c's patent rent: a patented entry its utilities use (rapid sand, PE5;
the steam turbine and pulverised coal, PE6 and PE16; the desalination plant, E6; the wind farm, E16) pays `LicenceFees.SHARE` of its
positive public gain until `TERM_MONTHS` after history's month. The gain is the water or power it delivers over what the city would get
without it, at the planners' tariff, so surplus capacity adds nothing and a building's running cost comes off. Local finds and
unpatented entries pay nothing; no berth carries a patented entry, so Freight pays nothing it never earned. Captured at production, the
bill is paid at the next strike on the city's services line, in the same month's audit as trade, the foreign accounts, the imports and
a "Patent licences" budget line; saved, no `SAVE_FORMAT` change. The public fitting verdict, a new utility's running cost and the works
value net the bill the month would strike, only while the patent runs. `PublicLicenceCheck` holds it (67 assertions); a city founded today is untouched.
## pending
| R2c-P-R | REV-R2c-P-2's four P3s on the mends ({next}): 1 the planners bill an order as n buildings beside the grid (`LicenceFees.java:516`, `RecipeChanges.java:355`), so at the demand cap they miss the standing stack's bill falling (q=2 fitted, demand 25: a third plant takes the month's bill from 4 to 1, the planner charges 0); 2 `PublicLicenceCheck` 7d passes with `- licenceOf` dropped (`AutoBuilder.java:1062`) or `capturePublic()` for the stack's bill: it wants the busy city (licenceOf above 0) or a second billed stack; 3 `HistoricalLicenceCheck.java:175` checks feesAt against `billOf()` itself, and power (PE6) has no independent figure for the verdict or forNewWork in a shortage; 4 `BuildAdvice.java:1673`'s `Math.round(at.months())` samples a power interval's k+0.5 as k+1, dropping the patent's last paid month (floor is exact) | The reviewer's SHIP: none needs mending before the gate; 1 only misses a fall (where the planner's figure is positive it is the month's), 4 is under one month's fee, 2 and 3 are harness reach | [R2c-P notes](docs/queue/R2c-P-notes.md) |
| R2c-P-C | The bank's branches' patented pieces (B1, B3) and the medical rows pay no licence ({next}) | A call, recommended (a) leave both unbilled: the branch is neither a sector's nor the city's work, so R2c and R2c-P each leave it, and its gain is the bank's own posts saved; the medical rows move health constants with no money gain to share. (b) would bill the branch through the bank's statement as R2c bills a firm, and needs a money gain for each medical row first | [R2c-P notes](docs/queue/R2c-P-notes.md) |
