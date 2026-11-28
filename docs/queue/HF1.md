# HF1 - the families keep their out-of-work adults: a lost job leaves the household standing

From ASH4 (Jerus's Ashbourne finding 16; `docs/queue/ASH4-notes.md` section 1, "Finding 16", `AshbourneCheck` section 4).

## What ASH4 found

- `FamilyModel` builds the families from the adults who WORK ("THE PEOPLE OUTSIDE THE FAMILIES", 2026-09-11, Jerus: "we
  are to add a new household structure called unemployed"). An adult who loses a job leaves the family in the month the
  job goes, takes the city's share of children with him ("THE ADULTS WHO LEFT WORK ARE STILL AT THE KITCHEN TABLE"), and
  becomes a one-adult household looking for a door (`FamilyModel.Seeker.UNEMPLOYED`).
- So the families (`FamilyModel.totalHouseholds()`: the history's "households", the People screen, `RealEstate`'s
  household count) move with this month's unemployment at full pace, and `householdsSeekingDoors()` moves the other way:
  on AshbourneCheck 4's fixture, 16% of the jobs gone takes the families -7.75% and the households seeking a door +4.3%
  in the one month, with nothing after; the jobs back, +7.72% in one month. Ashbourne: 2,069 -> 1,989 as the builders
  went 593 -> 404, and 2,164 -> 1,988 as they went 601 -> 156.
- Reality: a lost job does not dissolve a household. Household formation falls with unemployment over years, mostly
  through young adults forming fewer households of their own, while older households stay as they are (Cleveland Fed,
  Economic Commentary 2012-12, "Household Formation and the Great Recession"; FRBSF Economic Letter 2016-05, "Household
  Formation among Young Adults").

## What to build

- A household whose earner loses work stays the household it was: its door kept, the adult at the table, the
  benefit on that family's ledger, its children where they were; the families count does not move in the month of a
  layoff, and nobody new looks for a door because a job went.
- Household formation (adults leaving to form their own, couples forming, doubling up) reads unemployment over years,
  at a pace with a cited source, not one month's count.
- The money side: an out-of-work adult inside a family is not an earner at the family's tier (the reason the out of work
  were taken out in 2026-09-11); the family's income is its earners' wages plus the adult's benefit.
- Restate AshbourneCheck section 4 to the real law (the families and the households seeking a door flat in the month of
  a 16% layoff, within the formation pace), keep its "nobody is lost" identity; HousingCheck, HouseholdMemoryCheck,
  PopulationCheck and the money audit as they stand.

## Moves

- The People side, housing, `RealEstate`'s household count and the money side's cells: the default trace moves, so an
  80-run ensemble; money moves, so a stranger's review.
- It changes the design of 2026-09-11 (his "unemployed" household structure): the notes say how, for his review.
