<!-- gate-run: /home/jerus/cbs/runs/cbs-gate-OG1-cTu7Sk2M -->
<!-- readiness: provisional -->
## changelog
### {next} — OG1, a planner orders to the gap — 2026-11-22, ready, see docs/queue/OG1-notes.md
Past a nine-month backlog the builders no longer order one depot a month. They order the gap between their
sites' month and the work a typical month sends them, plus the queue past the knee over two years, never more
than 1.8 times that month. The typical month is a saved median of a year, their own depots left out. Each
batch takes at most the premium's share of a month of the sites. Food Processing orders as many plants as the
city already eats. On the 2,424-month playtest the months past the knee fell from 685 to 438 (470 to 224 with
over 100,000 people), and the city reached 629,000 people to main's 531,000. A first cut sized to the queue
ran to 7,771 depots to main's 299 and was dropped. His own city (slot 10) plays as main and gains nothing
from this batch: its builders' order is refused at the staffing gate (0.57 of a depot's posts staffable
against 0.80, with 0.0% out of work) before OG1 sizes it. GapOrderCheck holds it.
## queued
| RN3 | Real Estate's count set to none leaves its closed homes' share whole to the bit (RealEstate.setOwnedHousingCapacity: a count of none sets the share to 1, not s + (1 - s)), so ReloadCheck 3k's every-home-closed point is exercised on any city's path | docs/queue/OG1-notes.md, section 4 |
## known-reds
ReloadCheck | fixture 3k: the live city exercised it | RN3 | 2026-11-22 | Main's (FB16, 0.9.182), not OG1's code, which never touches Real Estate: on OG1's Danzik at m1230 Real Estate counts 19,710 places with a closed homes' share of 0.077046814583437140, and RealEstate.setOwnedHousingCapacity(0) makes it s + (1 - s) = 0.99999999999999990, where REV-FB16's precondition asks strandedDebtShare() == 1 exactly; on main's path the sum fell on 1. The month on counts 19,710 again (the second key's 0 is onCausedPoint reporting 0 for every key after the first that fails). Traced in OG1-notes section 4
ReloadCheck | fixture 3k, every home closed, by hand: the caused point's own city exercised it | RN3 | 2026-11-22 | Main's (FB16, 0.9.182), not OG1's code, which never touches Real Estate: on OG1's Danzik at m1230 Real Estate counts 19,710 places with a closed homes' share of 0.077046814583437140, and RealEstate.setOwnedHousingCapacity(0) makes it s + (1 - s) = 0.99999999999999990, where REV-FB16's precondition asks strandedDebtShare() == 1 exactly; on main's path the sum fell on 1. The month on counts 19,710 again (the second key's 0 is onCausedPoint reporting 0 for every key after the first that fails). Traced in OG1-notes section 4
