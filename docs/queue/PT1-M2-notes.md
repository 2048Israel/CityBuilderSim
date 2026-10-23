# PT1-M2 - the wait on ground, told again each year

Jerus, 2026-10-12, on PT1's M2 ("a notice repeated"): the inbox's "Your
businesses have nowhere to build" comes again each year the wait lasts, a new
notice, dated, naming the sectors waiting and the ground each needs.

## What changed

- **Inbox.takeMonth()**: a "landlock" notice standing `LANDLOCK_REPEAT_MONTHS`
  (12) is settled and a new one raised that month, so it is dated, unread and
  toasted like any new notice. A month with no sector asking settles the
  notice, as before; a wait back within the notice's year **reopens** it
  (`Notice.reopen()`), keeping its date and not toasting again (the toast key
  is key@raised, PT1's `toToast`), so a one-month lapse does not restart the
  year or raise a second notice. A wait back after the year raises a new one.
- **The body** names each sector by its label (it printed keys) and the ground
  it asked for, in its own refusal's numbers: "Real Estate: needs 743 m², 0 m²
  free". `Game.getLandBlockedNeeds()` keeps them beside `landBlockedSectors`,
  filled by `blockOnLand()` at the three places a sector is put on it and
  cleared with it; the investment lines' wording is unchanged.
- **Acknowledging** ("Go to the land office") quiets the condition for a year,
  not two, so a clicked notice comes back on the same yearly rhythm.

## The save

No new field and no format bump. The rhythm is the notice's own raised month,
and the inbox is already saved whole with every notice's raised, read and
resolved months (InboxCheck section 5). A separate "last fired" month would be
a second copy of that state, free to disagree with it. Section 7 saves and
loads mid-wait (half a year into the second notice), carries the walk on in the
loaded city, and the next notices still come at the year's months.

## The harness

InboxCheck section 7, "the wait on ground is told again each year": a new city
with its owned ground held at what is built on, every month, automatic
building on. The wait starts in month 3 and is told at months 3, 15, 27 and 39;
month 4 has nobody asking and the wait comes back in month 5 on month 3's
notice. Asserted: told once a year, each new notice settling the one before
the same month, never two standing, every waiting sector named with its
ground, the save and load keeping the standing notice's month, and once the
ground is bought (ten times what is built) the notice settles and is not told
again for the next 14 months. The standing section ("a live notice is never raised a
second time") now allows the one re-raise this batch makes: the landlock
notice, exactly a year on, its predecessor settled that month (10 of them in
its 130 months).

## How often it fires in a real run

LongPlaytest, default (4,005 months), now counts it in its report: **1,790
months with a sector waiting, 178 "nowhere to build" notices raised**, about
one per ten months of waiting.

## The checks, the trace

- Baseline on main (b60471f4, 0.8.29): 131 harnesses, all green.
- The trace: main built apart from its own sources, and the batch, the default
  playtest traced on each (`-Dplaytest.trace`): all eight CSVs byte-identical;
  the report differs only in its run-time lines and the new tally line. So no
  ensemble (EN1).
- The batch (before main moved; numbered
  0.8.32 after CL5 and PT1): `./batch-check.sh` 131 harnesses run, all green; Stale 0
  firm findings; the maps regenerated. Timing soft beside two other batches'
  JVMs.
- `--timing` hard, the timing harnesses alone (after waiting for another batch's JVM): 10 run, 10 passed.
- Merged main a699124a (CL5 0.8.30, PT1 0.8.31); kept 0.8.32. Section 7 now
  also keeps the window's toasts through PT1's `toToast` and `markAllToasted`
  on the load: 4 toasts for its 4 notices, none for the reopened one or on the
  load. `--fast` green; PlaytestFixCheck, SaveFileCheck, ReadPathCheck and
  InboxCheck green; the maps regenerated.
