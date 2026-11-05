## changelog
### {next} — R0, the Policy levers on history's dates — 2026-11-04, ready, see docs/queue/R0-notes.md
A city founded in January 1900 now has each Policy lever on the date Canada first had it (`PolicyDates`): the profit tax
1916, the income tax 1917, the minimum wage's floor 1918, the sales tax 1920, zoning 1924, old-age pensions 1927, student aid
1939, EI in August 1940, the health premium 1947, the pension's contribution 1966; the property tax, fees, fares, food
assistance and the rest are open from founding. Until its date a lever is shut and refuses a setting, and the Policy screen
draws it shut with when it opens; on its month the inbox gives Canada's first terms and the lever opens - the three income
taxes at the founding rate (15%), the promises at zero, the wage floor at the going unskilled wage. Each city's openings are
saved (R3's law finds will move them); a save from before R0 loads with every lever open. Harness fixtures whose premise is a
lever, or whose condition the untaxed 1900 city does not give, open them; the playtest runs on history's dates.
The ensemble scores 24/26 against 0.9.6's 22/26. Two lines its 1900 city is the first to reach in main's code pass by
name at the gate (Jerus: "if checks were not caused by its own code, then it should always pass"): SlopeCheck's "a
building on the map moved" (DM2's) and PriceLevelCheck 8's farm never retiring at 1900 prices (RT1, queued).
## pending
| R0-S | SlopeCheck 9's layout watcher on R0's city ({next}): "a building on the map moved" once, m612, district (-1,0), 58 of its 145 boxes with the carry log unchanged - DM2's fault (the wall ways), which the 1900 city reaches inside the watcher's window; passed by name at R0's gate | DM2 mends it | [R0 notes](docs/queue/R0-notes.md) ★20 |
| R0-P | PriceLevelCheck 8 on R0's city ({next}): the played twins part at month 47 on a farm retired at today's prices and never at the founding's (the 1e-12 nudge does not move it: a missed price); passed by name at R0's gate | RT1 mends it | [R0 notes](docs/queue/R0-notes.md) ★21 |
## queued
| RT1 | A building counted in its sector's retirement measure: Agriculture's and FoodProcessing's retirement measure what is spare in money and divide it by a building's tonnes or kilograms (`Sector.unitsOf()`), so at the founding's price level no farm or food plant retires; count one in the same money (R0's trial, 519f8e41, then the ripple read: MapCheck's yard and reserve fixtures, FoundingYardCheck, ShortSellingCheck, MonetaryCheck, ScaleCheck, the playtest's map watcher at m1204-1209), mending PriceLevelCheck 8 (R0-P) | [R0 notes ★21](docs/queue/R0-notes.md) |
| R0d | Research: the 1901 wage ladder and the arrivals' skill mix by year - each `PayTier` rung's 1901 comparator, sourced or flagged est. (research-1900 §5.2, §12.1), switched on with R1's 1900 recipes (R0-notes call 2); arrivals' diplomas from the year's mix (1.2 university students per 1,000 in 1901, 4.7 in 1950; nurses 0.05 to 3) | [R0-notes §1-2](docs/queue/R0-notes.md), [research-1900 §5.2, §5.4](docs/studies/research-1900.md#52-wages) |
| R0e | Research: the household's basket by year - 1900's raw staples, lamp oil and coal, no car (after R0c's goods, and on the live demand path, not the shadow basket) | [research-1900 §4, §5.4](docs/studies/research-1900.md#54-what-batch-r0-needs-the-first-studys-q1-now-for-1900) |
| R0f | Research: Money on Canada's dates - the D$ on gold to Aug 1914 and Jul 1926-1931, the Advance Rate opening the policy rate in Aug 1914, the Bank of Canada the rest of Money in Mar 1935 (`PolicyDates.POLICY_RATE`, `CENTRAL_BANK`); after GD2 and FM1 | [research-1900 §7.2](docs/studies/research-1900.md#72-the-recommendation), [GD0](docs/studies/spec-gold-money.md) |
