# FamilyModel.java - 2,072 lines · 97 methods · 10 constants · model

`ham/citybuildersim/FamilyModel.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> How the city's people are arranged into households, and what each earns.
> 
> ==================== THIS IS A PLACEHOLDER ====================
> 
> It rebuilds every month, it is saved, and it is displayed. It changes NOTHING.
> `PopulationCheck` asserts a city with it running is identical to one without.
> See the note in PopulationCohorts about why that assertion exists.
> 
> ==============================================================
> 
> WHAT IT DOES
> 
> Two inputs: the age pyramid (how many babies, children, teens, adults,
> seniors) and the employed job mix (how many adults are in each pay tier). Out
> of those it assembles households - a count for every FamilyStructure crossed
> with every PayTier.
> 
> ALLOCATION, NOT SIMULATION. Nobody is tracked. The model takes the totals and
> arranges them into the shapes that fit, in a fixed order, drawing people down
> until it runs out. Run it twice on the same inputs and you get the same
> answer, which is what makes it safe to rebuild from scratch every month
> instead of carrying households across the tick - and carrying them would be a
> far larger change than this is meant to be.
> 
> THE ORDER IS THE MODEL, and it is worth being honest that it is a choice
> rather than a derivation:
> 
>   1. Seniors first, into their own households, because they cannot be in any
>      other shape and so cannot compete for anything.
>   2. Then the shapes with dependants, largest first, so children actually end
>      up in families rather than being left over after every adult has been
>      packed into a childless couple.
>   3. Then couples, then single adults, to soak up whoever is left.
> 
> Doing it in any other order changes the answer. Largest-first is the version
> that leaves no orphans, which is the property worth having.
> 
> WHAT IT DOES NOT DO, so this list is on the record rather than discovered:
> 
>   - No multi-generational households. Seniors live alone or as couples.
>   - No mixed-tier couples, per the design: one tier per household.
>   - Unemployed adults are given a tier by proportion rather than left out,
>     because a household with no tier has nowhere to sit in the matrix.
>   - Households do not persist. Nobody has a family they keep.

**Uses:** [FamilyStructure](FamilyStructure.md) (84), [AgeBand](AgeBand.md) (47), [PayTier](PayTier.md) (27), [PopulationCohorts](PopulationCohorts.md) (4)

**Used by (20):** [BusinessInvestment](BusinessInvestment.md), [CrimeCheck](CrimeCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [Game](Game.md), [HealthCheck](HealthCheck.md), [HistorySave](HistorySave.md), [HouseholdAccounts](HouseholdAccounts.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [LongPlaytest](LongPlaytest.md), [Migration](Migration.md), [Offending](Offending.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [RealEstate](RealEstate.md), [SummaryScreen](SummaryScreen.md)

## Sections

| line | section |
|---:|---|
| 64 | THE HOUSEHOLDS REMEMBER (2026-09-11) |
| 119 | THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11) |
| 279 | · reading |
| 297 | READING THE TIER COLUMN HONESTLY |
| 370 | · building |
| 393 | THE ADULTS WHO LEFT WORK ARE STILL AT THE KITCHEN TABLE (2026-09-15) |
| 759 | WHEN THERE ARE NOT ENOUGH HOMES |
| 923 | PUTTING HOUSEHOLDS BEHIND DOORS THAT FIT |
| 952 | TWO SEGMENTS, BECAUSE A STUDIO AND A THREE-BED ARE NOT THE SAME GOOD |
| 1458 | AND WHEN THEY CANNOT AFFORD ONE |
| 1649 | · saving |

## Enum constants

| line | constant | says |
|---:|---|---|
| 149 | `FamilyModel.Seeker.UNEMPLOYED` |  |
| 149 | `FamilyModel.Seeker.STUDENT` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 90 | `FamilyModel.REFORMING_EACH_MONTH` | `.01` | The share of households that re-form on their own each month. |
| 155 | `FamilyModel.SEEKERS` | `Seeker.values().length` |  |
| 996 | `FamilyModel.STUDIO_MAX_SIZE` | `2` | The largest unit that counts as a studio. |
| 1482 | `FamilyModel.MAX_SHARING` | `.85` | Not everybody doubles up, however dear the rent. |
| 1543 | `FamilyModel.COUPLED_SENIORS` | `.55` | What share of a retired band lives as a couple rather than alone. |
| 1544 | `FamilyModel.COUPLED_ELDERS` | `.25` |  |
| 1660 | `FamilyModel.LEGACY_SHAPES` | `{ "SENIOR_ALONE", "SENIOR_COUPLE", "SINGLE_ADULT", "COUPLE", "SINGLE_PARENT",...` | The shapes a save written before the names travelled must be read with. |
| 1682 | `FamilyModel.OUTSIDE_SLOTS` | `outsideSlots(AgeBand.values().length)` | What the people outside the families add to the save: see toSaveArray(). |
| 1727 | `FamilyModel.KIN_SLOTS` | `kinSlots(AgeBand.values().length)` |  |
| 1744 | `FamilyModel.MEMORY_SLOTS` | `FamilyStructure.values().length * PayTier.values().length + 1 + 4` | ...and what the households remember: the formed matrix, whether there is one, the month's four counts. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 58 | `private final double[][] households` | How many households of each shape, at each pay tier. |
| 62 | `private double unhoused` | Adults left over with no household, which should be zero. |
| 92 | `private boolean remembers` |  |
| 93 | `private final double[][] formed` |  |
| 95 | `private boolean haveFormed` |  |
| 96 | `private double lastKept, lastReformed, lastNoLongerFit, lastNew` |  |
| 142 | `private double outsideAdults` | Adults this month's families were NOT built from: the out of work and the students. |
| 145 | `private final double[] orphans` | Children no family holds, by band. |
| 150 | `private final String label` |  |
| 158 | `private final double[] seekers` | One-adult households of each group looking for a door this month. |
| 161 | `private final double[] seekersSharing` | ...of whom this many live five to a home, with their own kind only. |
| 164 | `private final double[] seekersDoubled` | ...and this many doubled up two to a door, with their own kind only. |
| 167 | `private final double[] seekersUnhoused` | ...and this many with no door at all after both valves. |
| 170 | `private final double[] seekersUnplaced` | What the last match left unplaced, by group, before the valves. |
| 173 | `private final double[] seekersUnplacedHomes` | ...the same, in the households the match counts - a flatshare is one. |
| 176 | `private final double[] unhousedByShape` | Households of each shape the final match and the doubling valve could not place. |
| 179 | `private final double[] unplacedByShape` | Households of each shape the last match left unplaced. |
| 437 | `private final double[] outsideDependants` | Children of the adults who left work but not the house, by band. |
| 440 | `private double atHomeAdults` | Adults outside the families who still live with their dependants. |
| 783 | `private double doubledUp` |  |
| 921 | `private int[] lastHomesBySize` | The door census house() was last handed. |
| 950 | `private double rentWeight` | What the landlords can bill for, in person-equivalents. |
| 982 | `private double studioRentWeight` | The part of rentWeight billed on units of size 1-2. |
| 985 | `private double familyRentWeight` | ...and on units of size 3 and up. |
| 1039 | `private double crowdedHouseholds` | Households living somewhere too small for them. |
| 1042 | `private double refusedByStudio` | Households a studio turned away because they have a child. |
| 1354 | `private double stillUnplaced` | Households both valves failed to place. |
| 1484 | `private double pricedOutShares` |  |
| 1996 | `private double carriedUnplaced` | What the save said was left with nowhere, or -1 on a save from before it was carried. |
| 2002 | `private double carriedDoubledUp` | ...and what it said was crowded. |
| 2003 | `private double[] carriedSeekersDoubled` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 49 | 2024 | **type** `public class FamilyModel` | How the city's people are arranged into households, and what each earns. |

### THE HOUSEHOLDS REMEMBER (2026-09-11) (lines 64-118)

| line | len | member | says |
|---:|---:|---|---|
| 99 | 1 | `public void rememberHouseholds(boolean on)` | Switches the memory on; Game does, a bare model does not. |
| 100 | 1 | `public boolean remembersHouseholds()` |  |
| 103 | 1 | `public boolean hasRecord()` | Whether there is a record to keep from - false in a new city and a save from before. |
| 106 | 3 | `public double getFormed(FamilyStructure shape, PayTier tier)` | What the builder formed last month, before the valves. |
| 111 | 1 | `public double getLastKept()` | Households kept from last month's record this month. |
| 113 | 1 | `public double getLastReformed()` | Households that re-formed on their own, at REFORMING_EACH_MONTH. |
| 115 | 1 | `public double getLastNoLongerFit()` | Households the pyramid no longer had the people for: a death, a child grown, a lost job. |
| 117 | 1 | `public double getLastNew()` | Households the builder formed from the people left over. |

### THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11) (lines 119-278)

| line | len | member | says |
|---:|---:|---|---|
| 148 | 6 | **type** `public enum Seeker` | The groups of one-adult households the matrix does not hold but the housing match does. |
| 151 | 1 | `Seeker(String label)` _(in FamilyModel.Seeker)_ |  |
| 152 | 1 | `public String label()` _(in FamilyModel.Seeker)_ |  |
| 182 | 1 | `public double getOutsideAdults()` | Adults outside the families this month. |
| 185 | 1 | `public double getOrphans(AgeBand band)` | Children of this band in no household. |
| 187 | 5 | `public double getOrphansTotal()` |  |
| 200 | 8 | `public void setSeekers(double unemployedHoused, double students)` | Tells the match who else wants a door this month, before house(). |
| 209 | 1 | `public double getSeekers(Seeker g)` |  |
| 210 | 1 | `public double getSeekersSharing(Seeker g)` |  |
| 211 | 1 | `public double getSeekersDoubled(Seeker g)` |  |
| 212 | 1 | `public double getSeekersUnhoused(Seeker g)` |  |
| 215 | 4 | `private double seekerHouseholds(int g)` | A group's households as doors see them: one each alone, a fifth each sharing. |
| 225 | 8 | `public double seekerDoorShare(Seeker g)` | What one household of the group pays of a door's rent: all of it alone, a fifth sharing, half doubled up, none with no door. |
| 235 | 5 | `private double seekerDoors(int g)` | Doors a group is behind: alone, and a fifth of one for each sharer. |
| 242 | 5 | `public double householdsSeekingDoors()` | Every household that wants a door: the family matrix, and the seekers outside it. |
| 249 | 4 | `public double seekerUnhousedShare(Seeker g)` | The share of a group with no door. |
| 255 | 1 | `public double getUnhoused(FamilyStructure shape)` | Households of this shape with no door after both valves. |
| 258 | 4 | `public double unhousedShareOf(FamilyStructure shape)` | The share of a shape's households with no door; they pay no rent. |
| 268 | 10 | `public double[] unhousedPeopleByBand()` | People with no door, by band: the families both valves failed, and the seekers their own kind's valves failed. |

### reading (lines 279-296)

| line | len | member | says |
|---:|---:|---|---|
| 281 | 3 | `public double get(FamilyStructure shape, PayTier tier)` |  |
| 285 | 5 | `public double totalOf(FamilyStructure shape)` |  |
| 291 | 5 | `public double totalOf(PayTier tier)` |  |

### READING THE TIER COLUMN HONESTLY (lines 297-369)

| line | len | member | says |
|---:|---:|---|---|
| 312 | 8 | `public double workingHouseholdsIn(PayTier tier)` | Households with an earner at this tier. |
| 322 | 8 | `public double peopleIn(PayTier tier)` | Everybody living in those households - earners, partners and children. |
| 332 | 7 | `public double retiredHouseholds()` | Households with nobody of working age in them. |
| 340 | 7 | `public double retiredPeople()` |  |
| 348 | 5 | `public double totalHouseholds()` |  |
| 354 | 3 | `public double getUnhousedAdults()` |  |
| 359 | 10 | `public double averageHouseholdSize()` | People per household, the figure a housing model would eventually want. |

### building (lines 370-392)

| line | len | member | says |
|---:|---:|---|---|
| 378 | 3 | `public void rebuild(PopulationCohorts cohorts, double[] jobsByTier)` | Rebuilds every household from the current pyramid and job mix. |
| 389 | 3 | `public void rebuild(PopulationCohorts cohorts, double[] jobsByTier, double outsideAdults)` | counted as away: nobody takes a child with them. |

### THE ADULTS WHO LEFT WORK ARE STILL AT THE KITCHEN TABLE (2026-09-15) (lines 393-758)

| line | len | member | says |
|---:|---:|---|---|
| 443 | 1 | `public double getOutsideDependants(AgeBand band)` | Children of this band living with an adult outside the families. |
| 445 | 5 | `public double getOutsideDependantsTotal()` |  |
| 452 | 1 | `public double getAtHomeAdults()` | Adults outside the families who took their children with them. |
| 461 | 3 | `public double dependantsPerOutsideHousehold()` | Dependants per outside household, for the cells that carry them. |
| 473 | 212 | `public void rebuild(PopulationCohorts cohorts, double[] jobsByTier, double awayAdults, double atHomeAdults)` | not at home either - the prisoners. |
| 687 | 6 | `private void recordFormed()` | What this month's builder formed, before the valves: next month's reference. |
| 709 | 49 | `private void fitTiers(FamilyStructure[] shapes, double[] tierShare)` | THE PAY TIERS FOLLOW THE JOBS, and each shape keeps its total. |

### WHEN THERE ARE NOT ENOUGH HOMES (lines 759-922)

| line | len | member | says |
|---:|---:|---|---|
| 786 | 5 | `public double getDoubledUpHouseholds()` | Households with no door of their own, living in somebody else's: the families', and the seekers' with their own kind. |
| 792 | 3 | `public double getSharedHouseholds()` |  |
| 797 | 1 | `public double getDoubledUpFamilies()` | The families' own doubled-up households - guests in somebody else's home - without the seekers'. |
| 805 | 4 | `public double doubledUpShare()` | The share of the families' households living crowded because of doubling up: every guest and the host they moved in with. |
| 814 | 7 | `public double seekerCrowdedShare(Seeker g)` | The share of a seeker group WITH a door who are crowded: five to a home with their own kind, or two to a door - guest and host alike. |
| 823 | 7 | `public double homesNeeded()` | Homes actually occupied, counting doubled-up households as one home. |
| 853 | 12 | `public double minimumHomesTolerable()` | The fewest homes this household mix could crowd into before somebody would genuinely have nowhere to go. |
| 899 | 17 | `private double doorsThatCannotHelp()` | Doors the city has that the households who need one cannot enter. |

### PUTTING HOUSEHOLDS BEHIND DOORS THAT FIT (lines 923-951)

### TWO SEGMENTS, BECAUSE A STUDIO AND A THREE-BED ARE NOT THE SAME GOOD (lines 952-1457)

| line | len | member | says |
|---:|---:|---|---|
| 987 | 1 | `public double studioRentWeight()` |  |
| 988 | 1 | `public double familyRentWeight()` |  |
| 991 | 3 | `public static boolean needsFamilyDoor(FamilyStructure shape)` | Whether a household of this shape needs a door a child is allowed in. |
| 999 | 8 | `public double studioSeekers()` | Households that could live in a studio: adults only, one or two of them - the seekers outside the families among them. |
| 1009 | 7 | `public double familySeekers()` | Households that need a door of size three or more. |
| 1025 | 1 | `public double studioSeekerHeads()` | The PEOPLE in each segment, as opposed to the households. |
| 1027 | 1 | `public double familySeekerHeads()` |  |
| 1029 | 8 | `private double seekerHeads(boolean family)` |  |
| 1059 | 1 | `public double rentWeight()` | The rent base: what the let homes add up to, in people of capacity. |
| 1074 | 1 | `public void setRentWeight(double weight)` | Puts back the weight the month was actually billed on. |
| 1083 | 5 | `public void setRentWeight(double studio, double family)` | Puts back BOTH weights the month was billed on. |
| 1088 | 1 | `public double getCrowdedHouseholds()` |  |
| 1089 | 1 | `public double getRefusedByStudio()` |  |
| 1092 | 3 | `public static double rentWeightOf(int unitSize)` | What one let home of this size bills, whoever is in it. |
| 1111 | 10 | `public double marginalRentWeight(int unitSize)` | What one more home of this size would earn, in person-equivalents. |
| 1128 | 114 | `public double house(int[] homesBySize)` | Matches households to homes by size, and reports what would not fit. |
| 1251 | 6 | `private void bill(int unitSize, double homes)` | Books a let: its weight to the whole, and to the segment the DOOR is in. |
| 1263 | 7 | `public void squeeze(int homesAvailable)` | Crowds households until they fit the homes available. |
| 1278 | 64 | `public void squeezeUnplaced(double excess)` | The same two valves, on households house() could not place. |
| 1355 | 1 | `public double getStillUnplaced()` |  |
| 1365 | 92 | `public void noteUnplaced(double left)` | Records what the FINAL match left over, after both valves have run. |

### AND WHEN THEY CANNOT AFFORD ONE (lines 1458-1648)

| line | len | member | says |
|---:|---:|---|---|
| 1487 | 1 | `public double getPricedOutShares()` | Flatshares formed because a wage could not cover a home, not because there was none. |
| 1492 | 22 | `public void shareByAffordability(double[] pressure)` |  |
| 1523 | 8 | `public void shareSeekersByAffordability(double[] pressure)` | The seekers' own affordability valve: the share of each group living alone who share five to a home rather than go short. |
| 1554 | 24 | `private void placeRetired(AgeBand band, FamilyStructure coupleShape, FamilyStructure aloneShape, double people, boolean keep, d...` | One retired band into its own two shapes. |
| 1580 | 10 | `private double capacityFor(FamilyStructure shape, double[] remaining)` | How many of this shape the remaining people could fill. |
| 1592 | 14 | `private void place(FamilyStructure shape, double count, double[] tierShare, double[] remaining)` | Commits a number of households of one shape, split across the tiers. |
| 1615 | 8 | `private static FamilyStructure[] byDependantsDescending()` | Working-age shapes, most dependants first, then most adults. |
| 1639 | 9 | `private static FamilyStructure[] formableShapes()` | The shapes rebuild() may actually form, in the order it forms them. |

### saving (lines 1649-2072)

| line | len | member | says |
|---:|---:|---|---|
| 1667 | 6 | `public static String[] saveShapes()` | The shape names this build would write beside the matrix. |
| 1675 | 5 | `private static FamilyStructure shapeNamed(String name)` | The shape of that name, or null if this build has no such shape. |
| 1699 | 3 | `private static int outsideSlots(int bands)` | The same block measured against a SAVE's band count, not this build's. |
| 1703 | 3 | `private static int outsideSlots(int bands, int shapes)` |  |
| 1708 | 3 | `private static int memorySlots(int shapes)` | The formed-household memory, measured against a SAVE's shape count. |
| 1723 | 3 | `private static int kinSlots(int bands)` | The children who went out of work with their parent, appended 2026-09-15 as a TAIL rather than widened into the outside block. |
| 1739 | 3 | `public static int slotsBeforeMemory()` | Slots a save carries before the formed-household memory. |
| 1757 | 77 | `public double[] toSaveArray()` | Flattened row by row. |
| 1867 | 3 | `public void restore(double[] saved)` | Puts the households back. |
| 1871 | 3 | `public void restore(String[] bands, double[] saved)` |  |
| 1885 | 99 | `public void restore(String[] bands, String[] shapes, double[] saved)` | BOTH AXES COME FROM THE SAVE. |
| 1986 | 5 | `private static AgeBand bandNamed(String name)` | The band of that name, or null if this build has no such band. |
| 2015 | 3 | `public void adoptCarriedUnplaced()` | The saved residual wins over the load path's one-pass re-derivation. |
| 2044 | 7 | `public void adoptCarriedDoubling()` | The saved crowding wins over the load path's one-pass re-derivation. |
| 2052 | 20 | `public void reset()` |  |

