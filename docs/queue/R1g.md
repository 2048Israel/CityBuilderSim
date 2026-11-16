# R1g — The 1900 city grows

R1d judged the 1900 city on the 2,400-month playtest
(`-Dplaytest.founded=1900`: research known as of 1900, the known equipment
unfitted, R0d's 1901 wages) and found it stalls. Because of that, R1d left
the founding screen opening on "All fitted" (1900 is one chip away) and the
gate's playtest on today's city. This batch makes the 1900 city grow, then
switches both to it.

## What R1d measured (2026-11-16)

| month | today's city (main) | the 1900 city | 1900 on today's wages (a control) |
|---|---|---|---|
| 60 | 1,674 | 472 | 709 |
| 900 | 23,180 | 159 | 518 |
| 1200 | 24,889 | 1,581 | 379 |
| 2400 | 465,165 | 19,616 | 130 |

GDP at month 2400: 6.5e9 against 6.7e6. In the 1900 run the advisor tried
20 builds in 2,400 months, of which 8 were refused for want of money, and the
construction sector sat idle from month 55 (`consUtil` 0, 5 depots, no
points left). The city borrowed nothing before month 534. Unemployment ran at
32-38% from month 90 to month 240, with the posts falling from 178 to 94
between months 60 and 90. The control is the same city on today's ladder
(`WageLadder.of()` forced to `TODAY` for one run and then removed). It grows
faster for 20 years and then shrinks for good, so 1901's wages alone are
not the cause.

## What to find

Why nobody builds: the owners' cash and credit at 1901's wages (R0d-R's
PrizeCheck 12: a $733k lab out of reach by month 81), what the 1900
research leaves to build, the dated money (no central bank until March
1935, R0f) and the city's borrowing, and the unfitted equipment's returns.
The traces are in R1d's runs: the playtest's eight CSVs under
`-Dplaytest.trace` with `-Dplaytest.founded=1900`.

## Done when

The playtest grows on 1900, to a pace Jerus accepts (his call, with the
figures). Then the founding screen opens on "1900"
(`FoundingScreen.historicalEquipment`), the playtest's default founding
becomes 1900 (`LongPlaytest.FOUNDED_1900`), and the ensemble runs on it,
committed as `docs/ensemble/R1g.md`. R1f's harness findings (the D$ through
the 1914 suspension; `ScaleCheck` on a 1900 city) can go first or beside it.
