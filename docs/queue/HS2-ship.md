<!-- readiness: provisional -->
## changelog
### {next} — HS2, two adult stages — 2026-11-20, ready, see docs/queue/HS2-notes.md
The adults are two stages: the young, 18-34, who move up at 1/204 a month, and the established, 35-69, who retire at
1/420; retirement stays at 70 and the adult band is their sum. The young form seven household shapes, the established
all eleven; the young carry half the babies, a quarter of the children and no teens, and the children they cannot
place go to the established before anyone is an orphan. The books keep the young in 42 working cells beside the
established's 66, the pool's group is (tier, stage), and the young who turn 35 carry their money into the
established's cell, HL1's identity closing every month. A theft no household could take home is no longer taken. Saves
are format 58; a 57 save no longer loads. The 80 pass every hard criterion, but their cities end smaller: the final
population is 0.748 of BO1-S's in AUTO2 and 0.745 in ZERO_SLACK, so both T2 lines (soft) now FAIL. Why: a young city's
adults are mostly young and the young do not retire, so it keeps fewer seniors (9% of the people against 11%) and more
adults want the same jobs; unemployment is 9.3% against 6.7% in the take-off (m720-1440), where the size is lost. The
retired are richer (0.74 of the working households' net worth against 0.56).
## queued
| OBK1 | The household cells' cost in the bond market and the books grows with the square of the cells, and HS2 took them from 99 to 141: `OrderBook.rest` inserts each cell's order by a linear scan, the settle recounts a cell's bonds at every fill (`Household.recountBonds`), and `HouseholdBalance.foldEmptyCells` spreads every bond of a cell that empties over its group (HS2 cached its reads, the same to the bit: 30.3 -> 23.1 ms at m600). On the probes' default city under Flight Recorder, `BondMarket.takeMonth` took 432 samples on main against 2,775 on HS2, `rest` 58 against 663, the settle's recount 83 against 792; the books 8.0 against 23.4 ms a month at m600 and 12.7 against 36.0 at m2400, the whole month 29.5 against 71.1 and 51.9 against 101.0 ms (HS2's city on that path 1.5 and 2.4 times the people, so measure on one saved city at both cell counts); mend to the bit, or say what moves | docs/queue/HS2-notes.md, The month's cost |
## known-reds
HoldersCheck | fixture: every till could pay its share, so nothing was cut | FS14 | 2026-11-19 | Section 10's played city moves under HS2's households: at the settle the section reads, Real Estate's till is -0.0057 while it owes nothing (a weight of 0), so the fixture's premise reads false though nothing was cut; the settle's own lines pass. HS2 touches no till or settle code, and main's three section-2 lines (HS1's 6e-6 coupon split) pass on this city (HS2-notes.md, The checks)
KeptCheck | no month makes more than a tenth of the nodes the first draw made | FS5 | 2026-11-19 | KBP1's class on HS2's played city: one month made 39 of the first draw's 378 nodes (13 of 25 months made none); HS2 touches no page that section draws (HS2-notes.md, The checks)
KeptCheck | Spending: Healthcare opened: the redraw and the next equal a page built new | FS5 | 2026-11-19 | KRT1's class on HS2's played city: the kept content is 1454x684 against a page built new 1446x700, the fresh one narrowed by a scroll bar; HS2 touches no Spending code (HS2-notes.md, The checks)
