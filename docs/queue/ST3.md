# ST3 - a town of ScaleCheck's own plan, so section 6 stops being re-picked

Queued 2026-11-01 by ST2 (`docs/queue/ST2-notes.md`), on the orchestrator's
call under BP1 (ST2's option 1, the recommended way; Jerus can overturn it).

## Why

Section 6 copies "a growing city", the playtest's town
(`OrderSearchCheck.playtestCity()`), at `FREE_FIXTURE_MONTHS`. That month has been
re-picked eleven times since 0.7.62 by batches that moved the trace: 400, 410, 550,
450, 545, 595, 635, 600, 622, 807, 1124, and now 718. Each pick holds until the next batch
moves the trajectory. EP1 showed the trajectory is a draw: a 5e-18 debt hair at
month 56 parts any two price levels.

ST2 found no band on EP1's model with IC1 and LD1 at the factor. 718 holds, but
of m710-726 only 717, 718 and 722 hold every line (exact runs). The scans say
why no band exists on this town:

- **Its small years price migrants out.** From m150 to m530, at 3,500 to 9,000
  people, the rent burden is over `RealEstate.TARGET_RENT_BURDEN` in nearly every
  month: wages are low against rents. A small town of the playtest's is not calm.
- **Its calm years are too big for the order line.** The boom from m795 to m905
  (16,000 to 28,000 people) is calm, growing and building. Both copies there miss
  "fixture: the free copy ordered - an order sized from more than a million
  needed". A free copy's largest order is about one building per K (a building is
  one size at any K, so a copy orders K times the city's fractional gap), and K =
  5e9 / people is 180,000 to 310,000. The line holds reliably only when K
  reaches about a million, a town of about 5,000 people at the 5-billion copy.
  A few months pass on a single large order (23 to 27 buildings per K at 841,
  859 and 889).
- **Section 9 is a draw too.** Its people and unemployment lines miss through
  m710-790 in all but a few months.
- At today's prices (`-Dcbs.priceLevel=1`) the same town is another draw: m1066-1141
  holds every line but one in most months, its 5-billion copy ordering 10 to 17
  buildings per K.

## What it does

Section 6 gets a town of its own plan instead of the playtest's player:
- a founding (the playtest's, or section 1's);
- a scripted list of builds that grows it steadily;
- no advisor, so no draw of the advisor's discrete choices (ST1: HD2's police
  at m731 decided the town for 1,000 months).

The plan must hold, over a band of at least a few dozen months and at the
factor and at factor 1:
1. growing: more people than a year before;
2. ground free for a home;
3. calm: the affordability pull 1 in all `FREE_MONTHS` (rent burden at most .30);
4. small enough that both copies order from more than a million needed: about
   5,000 people or fewer, unless the plan leaves a gap the copies order in bulk;
5. its copies' landlords build homes ("...and it builds", main's red since HD2);
6. section 9's tracking within its `TRACK_` dials.

Points 3 and 4 pull against each other on the playtest's town. The plan is
where to meet them: homes and jobs placed ahead of the people, so wages hold
against rents in a small town. If no plan can be calm at that size without a
model change, say so; that is a finding for Jerus, not a premise to move.

## What does not move

ScaleCheck's premise (a growing city, with ground free for a home, whose own
rents price no migrant out, copied K times over and read against itself), its
tolerances, `FREE_PEOPLE` and the million. No model change.

## Tools

`docs/queue/ST2/`: `ST2Scan.java` plays a town once and reads sections 6 and 9
at each month asked for, about 1.5 s a month on a loaded copy. `scan.sh` is the
coarse scan; `exact.sh` reads one month at a time live, about 25 s a month. A
loaded copy of a grown town parts from the live one within six months, so only
`exact.sh` decides. Point `ST2Scan`'s trunk at the new town's builder.
