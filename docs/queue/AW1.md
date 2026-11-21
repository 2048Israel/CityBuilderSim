# AW1 — The auto city always builds its works

Jerus asked for this batch in chat on 2026-11-12, about 21:50.

He loaded WG2's collapsing city behind RT3, saved by 0.9.91 at m2391.
Automatic building was off, its last pass m1412, with nine orders ever.
Debt was 924% of GDP against the 240% limit, and every card said
"No land free". Power was 15% served, streets 1%, highways 0%, general
care 10%, elementary schools 3%, and police 28%. He switched automatic
building on, raised the limit to 600%, selected "Build from cash anyway",
and bought 50 blocks. By m2664 it built 1,088 buildings and grew from
311,150 people to 578,800, with nobody out of work from m2521.
Slot 9 holds his result and slot 10 the m2391 start. Read them only.

> basically build build build, thats why i have it at always build basically, cause technically no matter how many calculations you do it wont payoff? yet it does

> a neat trick you might wanna try is that sometimes if im rich i buy lots of coal power plants, even tho i have spare energy, purely to attract workers and stimulate the economy

Find who switched automatic building off. Measure the default playtest and
the ensemble cities monthly for automatic building off, no building while
over the debt limit, and essential services' served shares. The auto city
builds power, streets, highways, health, schools and police from cash over
its limit, and buys their required land from cash. Record a ★ recommendation
on whether this is the player's default or the test city's. Consider his
stimulus trick as a ★ option, implementing it only with a defensible measure.

Cause cash, excess debt, no free land, and under-served power and streets in
a harness and assert orders and land purchases. Count LongPlaytest months
with any essential service under a fifth served. Retain its existing findings.
Run the final model's full ensemble once against docs/ensemble/0.9.97.md.
Every criterion that baseline passed must still pass for readiness.

Queue the ordinary load's allocated-land reconstruction separately, using
RT3's "Policy test" evidence. This batch is versioned with {next}. Push a
provisional ship file before the dry run, then edit only AW1-notes.md and
AW1-ship.md. Never run the real ship command.

## Allocated-land follow-up

Queue a separate fix for the ordinary load's allocation reconstruction.
RT3's "Policy test (Jerus, 19:40)" captured WG2's source allocation as
113,813,544 sq ft with 520,069,911.267 sq ft free. The standing and site
footprint was 873,813,544 sq ft. A normal load derived allocation from
the buildings and removed the free ground. The earlier and rebuilt RT3
fixtures showed the same discrepancy. Investigate the source counter and
the reconstruction together, then cause the discrepancy in a save round
trip and assert that the live city and its ordinary load have matching
land and construction behavior. AW1 leaves this repair to that batch.
