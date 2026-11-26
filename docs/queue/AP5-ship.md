<!-- gate-run: /home/jerus/cbs/runs/cbs-gate-AP5-XKcPbjfP -->
<!-- readiness: provisional -->
## changelog
### {next} — AP5, the auto-builder's pass holds the labour figures — 2026-11-26, ready, see docs/queue/AP5-notes.md
Slot 9 was slow again at 50x (Jerus, 2026-11-25: "ok once again, slow down, slot 9"). The auto-builder's pass, half the
window's thread, asked again for the vacancies, the posts and a sector's buildings for every works it valued. While the
pass, or the Build Overview's draw, holds its scope, the vacancies are now held against every number their fill reads.
The stacks' posts and each sector's stacks are held against a count raised after every change to a stack's buildings, a
building's posts or sector, or the list, so after every order. It is exact: the playtest trace's eight md5s are main's,
and so is slot 9's log. On slot 9 a month went from 476 to 359 ms headless, and 50x from 5.6-9.2 to 7.7-10.0 months a
second. LabourCheck changes each input after a figure is held and compares the next reading with the bare one;
AutoBuildCheck compares 20,424 held readings in a real pass, none off by a bit. Four screens are still short of 10 a
second: AP6 is queued with the next causes, measured.
## queued
| AP6 | Slot 9 at 50x after AP5 (7.7-10.0 months a second): the posts past read once per job type in every `getTotalJobs()` (6.6% of the FX thread), the bond book's `OrderBook.submit` (10.2%), the month's `totalBySector` walks outside the pass (9.5%), `Game.runPrivateInvestment` (11.9%); measured before and after, exact (the trace's eight md5s equal) | [AP6](docs/queue/AP6.md) |
