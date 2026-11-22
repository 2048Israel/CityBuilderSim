# EDU2 - the founding schools

Jerus, 2026-11-22 (desk chat): "remember the education issue, what if we help
it by doing the same as services, aka city with nothing starts with some
space, aka say 50 seats at each step, including college or something, whoever
does it comes up with the actual numbers".

(Proposed as FE1; that code is `research.json`'s "Driverless truck", so the
batch is EDU2.)

## The batch

A founded city starts with school places at every level: elementary, high
school, college, and the further levels if the builder finds them warranted.
This is the way it already starts with care (`Healthcare.FOUNDING_CITY`,
1,200; `Healthcare.foundingCapacity`) and a constabulary:

- no staffing discount;
- one set per founding endowment (`BuildingManager.getFoundingEndowments()`,
  FW9);
- added in `BuildingManager.getStaffedEducationPlaces` and
  `getBuiltEducationPlaces`, as `getCareCapacity` adds the founding care.

Jerus's "say 50 seats at each step" is the starting point. The builder sets
the numbers from the model, as one dial a level in `Education` with one
sentence each, and says in the notes why each number is what it is.

## The founding faculty

Jerus, 2026-11-22 (desk chat): "perhaps give the starting base a few high
level posts as well? like only a few? or no?"

Research posts come only from a school's university-band staff
(`Research.POSTS_PER_STAFF`, 2.7 each). Founding places with no staff would
give a new city no research at all. So EDU2 adds a small founding faculty:

- a few university-band staff;
- researchers at the faculty's 40%;
- the posts they make room for.

The builder chooses between two ways to provide them, and says why:

- an endowment like the founding doctor, with no wage and no worker taken;
- real posts that the labour market fills.

Either way, it keeps to Jerus's "only a few". RW1, the world's delay, asks
whether it waits on this faculty.

## Why

R1g's notes, "The skills: today's posts meet 1901's workers" (branch
batch/R1g, `docs/queue/R1g-notes.md`):

- A 1900 city builds no school posts in 2,400 months. `AutoBuilder.kept()`
  builds one only when it is warranted and there is somebody to teach.
- So the native-born get no diploma.
- So the diploma workers die out, from 59 at month 60 to 0.3 at month 1800,
  while the diploma posts stand vacant.

## Beside R1g

R1g's skills mend is the apprentices, which Jerus approved before. The
founding schools are the other candidate, or a complement to it.

R1g's notes say to measure the candidates as controls on the 2,400-month 1900
playtest. EDU2's notes give that playtest with the schools and without them.

EDU2 does not wait on R1g's negative-GDP trace. When R1g returns, its
apprentices are measured on top of EDU2.

## Bounds

The founding schools touch every founded city, not only 1900's, so the
default trace moves and the ensemble is owed.

If the places derive from the founding endowments, an old save gains them on
load with no new field. Say so under the old-saves rule.

A harness causes, through the model's inputs:

- a founded city with no school built, which has the places;
- a copy with K endowments, which has K sets;
- a staffed school, which adds to the places rather than replacing them;
- the places, which reach the diploma counts.
