<!-- gate-run: /home/jerus/cbs/runs/cbs-gate-AP1-8gllw3G8 -->
## changelog
### {next} — AP1, the auto-builder's month at 400,000 people — 2026-11-21, ready, see docs/queue/AP1-notes.md
On Jerus's slot 10 (421,561 people, 57 stacks on site) the auto-builder's search asked the power ahead (`WorksValue.pendingPowerLoad`) about 1,100 times a month, and each call rebuilt the site's whole completion schedule, with the builders' staffing for every stack and every template's new-unit draw.
`WorksValue.hold()` holds the schedule and the draws in a scope, for one city on its own thread, opened by `AutoBuilder.pass` around its loop and by the Build screen's Overview around its draw.
What is held is keyed on `BuildingManager.siteStamp()` with the month and the output on site, read at every call, so an order, a cancel, a rush or a priority is read afresh at the next call. Closed, every call computes as before.
The same order and products are kept, so every figure is the same to the bit. NewUnitCheck 13 proves it at every horizon and for `relief`'s avoided loss, and proves that an order, a rush and a priority inside a scope are read, one schedule is built a scope (a count), and nothing is held once it is closed.
On slot 10 over 24 months the month went from a mean of 43.6 s and a median of 40.1 s to 4.5 s and 4.3 s, on a busy laptop, and the schedules built from 38,262 to 24. The cash, people and spending at the end are equal to the bit.
The default trace does not move: all eight playtest CSVs are byte-identical to main's over 2,405 months. The run took 660 s, against 1,116 s.
## known-reds
KeptCheck | most months make no node at all (9 of 25) | FS5 | 2026-11-21 | Main's own (a9bfcd070): section 55's Bank Profit page, KBP1's quiet-month count, red at the same line on main's build on the laptop beside AP1's (AP1-notes, section 4); the gate box skips KeptCheck headless, so no gate had seen it.
KeptCheck | ...the held slider is the same node, still held, at the thumb | FS5 | 2026-11-21 | Main's own (a9bfcd070): section 78's dragged tax dial with a card in front, KRT1's section, red at the same line on main's build on the laptop beside AP1's (AP1-notes, section 4).
