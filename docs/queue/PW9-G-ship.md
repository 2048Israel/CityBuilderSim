<!-- readiness: provisional -->
## changelog
### {next} — PW9-G, wires ready with the power — 2026-11-28, ready, see docs/queue/PW9-G-notes.md
A utility orders no generation past what its wires will carry when the plant opens (REV-PW9 P1-b: 0.9.218's
modern city ordered a 21 MW diesel and nine steam stations behind 15 MW, and its July ran short by the wires). At a
power order the automatic builder first orders the wires the plant's load needs at its date, through the grid's own
step and every gate it has. Where they cannot come (a protected completion, money, ground), the plant is cut to
what will be carried by the wires that open by then, with the plant's own points on the crews, or held. The grid's
forecast reads the busiest month from its opening to its horizon at the city's latitude, not this month's. No
catalogue id, no save change. The modern city: no month short, nothing unserved (main: one wire-bound month),
$1.34M less on plants. PowerWiresCheck is new; three older fixtures are isolated from the law.
## pending
| PW9-G-1 | the wires a plant is matched to keep their quoted opening against later orders ({next}) | PowerWiresCheck 3(b): the opening is a forecast at the order, overtaken by the orders after it on the same crews | docs/queue/PW9-G-notes.md |
