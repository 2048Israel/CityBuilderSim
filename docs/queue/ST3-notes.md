# ST3 - a town of ScaleCheck's own plan (0.9.78, 2026-11-09)

The brief is `docs/queue/ST3.md` (queued by ST2, 2026-11-01). ScaleCheck
section 6 stops copying the playtest's town at a re-picked month and plays a
town of its own plan; section 9 holds the bank's branches in both copies.
No model change, no save change; the default playtest's trace is main's.
ScaleCheck passes every line (main's two recorded reds, the affordability
pull and section 9's cash, are gone), at the factor and at factor 1.

## 1. What changed

- **`ScaleCheck.ownTown()`** replaces `OrderSearchCheck.playtestCity()` and
  `lookAt()` for section 6: the playtest's founding and player settings, the
  construction subsidy, 40 Houses, 3 Convenience Stores and 2 Mixed Farms,
  then a month at a time `keepAhead()`. No advisor, so no draw of its
  discrete choices.
- **`keepAhead()`**: `LongPlaytest.keepGroundAhead()`, then one of each when
  its own need is on and none is on site: a Wind Farm (power), a Water
  Treatment Plant, a Paved Road, a Walk-in Clinic, a Construction Depot, a
  Convenience Store (the advisor's throttles, each on its own measure), and
  Low-Rise Apartments when the landlords' family pressure passes
  `OWN_PRESSURE` (1.0).
- **`OWN_PROPERTY_TAX`** (.04 a year, the founding's .015), set once at the
  founding. Section 2 says why.
- **`FREE_FIXTURE_MONTHS` is 175**, its javadoc rewritten short (the picks'
  history is in git and in ST2's and RE2's notes).
- **The bank's branches, held** (`BusinessInvestment.holdBranches()`,
  harness-only and not saved, like `holdSector()`): `planBank()` opens none
  and Game's retirement closes none while it is on. `tracks()` calls it in
  both copies. RE2's cause of section 9's cash miss (the 1,000-times copy
  closed 975 of its 1,000 branches in month 1) is gone: section 9's cash is
  within 6.1e-3 (against .0125) in every month scanned since the plan's
  treasury was fixed.
- **LD2's wage is on in ScaleCheck** (`WantedPosts.carryKeptOut = false`
  removed from `main()`, as its comment promised for ST3). For LD3: this
  check now plays the game's wage.

## 2. What the scans found

Tools: `docs/queue/ST3/ST3Probe.java` plays a town by a scripted plan and
prints a line a month (people, places, burden, pull, cash, free ground,
family pressure); `ST3Track.java` plays section 9's two copies at one month
and prints which household figure parts first. They replace ST2's scanner
for this section. Only ScaleCheck itself, one month a run, decides (ST2).

- **The treasury before 1916 is the property tax alone.** The profit tax
  opens in 1916 (month 192), the income tax in 1917. With the founding's
  1.5% the town's cash stood at 100 to 400 through its small years, so
  section 9's cash line (a share of the city's cash) missed by 1.5% to 61%
  (worst at 155), and the ground ran out: `keepGroundAhead()` spends a share
  of the cash, and the free ground fell to 7,271 sq ft at 135 and 4,345 at
  180 (both "ground free for a home" misses). At 4% the cash is 600 to 1,500
  at months 110-155 and 1,700 to 6,300 at 163-196, and the rents stay under
  the line with the apartments ahead (alone, 4% put the burden at .30-.33).
- **The int stack ceiling (a finding, SQ1 queued).** `BuildingsStacks`'s
  `quantity` is an int, and every House is one stack. Housed in Houses (0.2
  a person at 3,000-5,000 people) the ten-billion copy's House stack nears
  2^31: in the first plans' scans that copy's largest order was 1 (110,
  115, 125, 130, 140) or it did not load (95, 105, 115, 125, 140: "the copy
  loads" FAIL). With the apartments
  the town had 140 Houses and 37 Low-Rises at 220, against 698 Houses. A
  real city of Jerus's ten-billion plan housed in Houses would meet the same
  wall, so SQ1 (`docs/queue/SQ1.md`) is queued.
- **Cars are bought whole (not this batch's).** Section 9's savings miss at
  122-143 (1.6e-2 to 4.6e-2 against .016): at 122 the city's households took
  13 cars where the copy's took 22.9 per K, and the copy traded used cars
  where the city traded none. A town of 4,000 in 1910 buys a few whole cars
  a month; past 160 the granule is small (savings within 6e-3 from 168).
- **The band.** Every line holds at 164-168, 170-180 and 187-196: 26 of the
  33 months from 164 to 196, 5,821 to 8,089 people (the copies' largest
  orders 2.3 to 10 million needed). The misses: 169 (section 9's prices
  past .004); 181-186 (both copies' largest order one building: their
  landlords placed no mortgage order in those six months, where at 179 they
  ordered 2.5 million homes); 160 and 200 (the town's own rents). Before
  160: the cars above, and the copies' largest order under a million at
  134, 143, 155 and 158. At factor 1 the town is the same month for month,
  and 170, 175 and 180 hold every line. 175 is the middle of 170-180.
- **Time.** A whole ScaleCheck run at 175 takes about 28 s (main's 22 s at
  488): the town is younger, its copies larger.

## 3. Calls taken

- **A tax in the plan (recommended, taken).** The brief names builds; the
  property tax is the one lever a 1900 player has for the treasury, and
  without it no month from 90 to 200 held section 9's cash. Jerus can
  overturn it: the cost is a band with the cash line red.
- **A band of 26 of 33 months, not every month (recommended, taken).** The
  misses inside it have the causes above; none is a draw of a plan's
  choice. The longest unbroken run is 11 months (170-180), and 175 sits in
  its middle. A batch that moves the trace a little should land in a
  passing month; one that moves it a lot rescans with the two tools.
- **SQ1 queued (recommended, taken)**: a stack's quantity a long, or a
  second stack past 2^31. Model work in 150-odd callers; old saves read the
  int as a long, so no format bump.

## 4. How it was done

The plan's code was written in the batch session as each scan needed it
(the fixture is the scan's own); the docs pass (FREE_FIXTURE_MONTHS's
javadoc, section 6's comments, the class header, OrderSearchCheck's stale
sentence, the probe's options) went to one Sonnet slice and was reviewed
here. The ensemble is skipped: the only model lines are behind
`holdBranches()`, which only ScaleCheck calls, so the default playtest's
trace is main's by construction.
