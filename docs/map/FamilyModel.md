# FamilyModel.java - 2,354 lines · 112 methods · 12 constants · model

`ham/citybuildersim/FamilyModel.java` - generated 2026-11-17 by CodeMap; line numbers are as of that run.

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

**Uses:** [FamilyStructure](FamilyStructure.md) (89), [AgeBand](AgeBand.md) (49), [PayTier](PayTier.md) (27), [PopulationCohorts](PopulationCohorts.md) (5)

**Used by (26):** [ArrivalMixCheck](ArrivalMixCheck.md), [BusinessInvestment](BusinessInvestment.md), [CityNeeds](CityNeeds.md), [CrimeCheck](CrimeCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [DebtExchangeCheck](DebtExchangeCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [Game](Game.md), [HealthCheck](HealthCheck.md), [HistorySave](HistorySave.md), [HouseholdAccounts](HouseholdAccounts.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [KeptBench](KeptBench.md), [LongPlaytest](LongPlaytest.md), [Migration](Migration.md), [Offending](Offending.md), [OutsideCheck](OutsideCheck.md), [OwnerCheck](OwnerCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [RealEstate](RealEstate.md), [StaffingDrawCheck](StaffingDrawCheck.md)

## Sections

| line | section |
|---:|---|
| 64 | THE HOUSEHOLDS REMEMBER (2026-09-11) |
| 119 | THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11) |
| 279 | · reading |
| 297 | READING THE TIER COLUMN HONESTLY |
| 370 | · building |
| 393 | THE ADULTS WHO LEFT WORK ARE STILL AT THE KITCHEN TABLE (2026-09-15) |
| 736 | THE CHILDREN COME HOME WITH THEIR PARENTS (OX1, 2026-11-17) |
| 898 | WHEN THERE ARE NOT ENOUGH HOMES |
| 1062 | PUTTING HOUSEHOLDS BEHIND DOORS THAT FIT |
| 1091 | TWO SEGMENTS, BECAUSE A STUDIO AND A THREE-BED ARE NOT THE SAME GOOD |
| 1616 | THE ROOM THE PLACEMENT HAS LEFT (0.7.17) |
| 1736 | AND WHEN THEY CANNOT AFFORD ONE |
| 1927 | · saving |

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
| 734 | `FamilyModel.SHAPE_TAKE` | `.5` | The share of what is still possible a shape takes in one pass of the builder, every shape but the last: half, so a large family does not eat every child and the couples leave single adults behind. |
| 779 | `FamilyModel.HOME_PASSES` | `64` | The most passes the children who came back are placed in: each places at least SHAPE_TAKE of every band while the adults last, so this leaves under a child in 10^19. |
| 1135 | `FamilyModel.STUDIO_MAX_SIZE` | `2` | The largest unit that counts as a studio. |
| 1760 | `FamilyModel.MAX_SHARING` | `.85` | Not everybody doubles up, however dear the rent. |
| 1821 | `FamilyModel.COUPLED_SENIORS` | `.55` | What share of a retired band lives as a couple rather than alone. |
| 1822 | `FamilyModel.COUPLED_ELDERS` | `.25` |  |
| 1938 | `FamilyModel.LEGACY_SHAPES` | `{ "SENIOR_ALONE", "SENIOR_COUPLE", "SINGLE_ADULT", "COUPLE", "SINGLE_PARENT",...` | The shapes a save written before the names travelled must be read with. |
| 1960 | `FamilyModel.OUTSIDE_SLOTS` | `outsideSlots(AgeBand.values().length)` | What the people outside the families add to the save: see toSaveArray(). |
| 2005 | `FamilyModel.KIN_SLOTS` | `kinSlots(AgeBand.values().length)` |  |
| 2022 | `FamilyModel.MEMORY_SLOTS` | `FamilyStructure.values().length * PayTier.values().length + 1 + 4` | ...and what the households remember: the formed matrix, whether there is one, the month's four counts. |

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
| 455 | `private double ownerAdults` | Adults of the owner families (BO1): outside the families, no dependants. |
| 781 | `private double lastBackAdults, lastBackChildren, lastBackChildrenHome` |  |
| 922 | `private double doubledUp` |  |
| 1060 | `private long[] lastHomesBySize` | The door census house() was last handed. |
| 1089 | `private double rentWeight` | What the landlords can bill for, in person-equivalents. |
| 1121 | `private double studioRentWeight` | The part of rentWeight billed on units of size 1-2. |
| 1124 | `private double familyRentWeight` | ...and on units of size 3 and up. |
| 1178 | `private double crowdedHouseholds` | Households living somewhere too small for them. |
| 1181 | `private double refusedByStudio` | Households a studio turned away because they have a child. |
| 1512 | `private double stillUnplaced` | Households both valves failed to place. |
| 1657 | `public final double familyDoorsFree, studiosFree` | Doors of size three and up, and studios, that nobody would be behind. |
| 1659 | `public final double doublingRoom` | Households the families' doubling valve could still take. |
| 1661 | `public final double seeking, withADoor` | Households that want a door (householdsSeekingDoors()), and those that would have one of their own. |
| 1663 | `public final double withDependants, peoplePerHousehold` | The arrivals' mix: the share of their households with a dependant, and people to a household. |
| 1762 | `private double pricedOutShares` |  |
| 2276 | `private double carriedUnplaced` | What the save said was left with nowhere, or -1 on a save from before it was carried. |
| 2282 | `private double carriedDoubledUp` | ...and what it said was crowded. |
| 2283 | `private double[] carriedSeekersDoubled` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 49 | 2306 | **type** `public class FamilyModel` | How the city's people are arranged into households, and what each earns. |

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

### THE ADULTS WHO LEFT WORK ARE STILL AT THE KITCHEN TABLE (2026-09-15) (lines 393-735)

| line | len | member | says |
|---:|---:|---|---|
| 443 | 1 | `public double getOutsideDependants(AgeBand band)` | Children of this band living with an adult outside the families. |
| 445 | 5 | `public double getOutsideDependantsTotal()` |  |
| 452 | 1 | `public double getAtHomeAdults()` | Adults outside the families who took their children with them. |
| 457 | 1 | `public double getOwnerAdults()` |  |
| 460 | 1 | `public double getOwnerFamilies()` | The owner families this month: two adults each. |
| 468 | 3 | `public void restoreOwnerAdults(double adults)` | On a load: the owner adults the save's census counted (DataSave's, zero on a save from before BO1). |
| 479 | 3 | `public double dependantsPerOutsideHousehold()` | Dependants per outside household, for the cells that carry them. |
| 491 | 4 | `public void rebuild(PopulationCohorts cohorts, double[] jobsByTier, double awayAdults, double atHomeAdults)` | not at home either - the prisoners. |
| 502 | 222 | `public void rebuild(PopulationCohorts cohorts, double[] jobsByTier, double awayAdults, double atHomeAdults, double ownerAdults)` | are the owner families' (two a company; BO1): no job and no dependants, so they take none with them. |
| 726 | 6 | `private void recordFormed()` | What this month's builder formed, before the valves: next month's reference. |

### THE CHILDREN COME HOME WITH THEIR PARENTS (OX1, 2026-11-17) (lines 736-897)

| line | len | member | says |
|---:|---:|---|---|
| 784 | 1 | `public double getLastBackAdults()` | Adults back in the families this month: last month's adults at home less this month's, when that fell. |
| 786 | 1 | `public double getLastBackChildren()` | The children who came back with them, at last month's rate to an adult at home. |
| 788 | 1 | `public double getLastBackChildrenHome()` | ...of whom this many were placed in a household with them: all, while their adults last. |
| 791 | 17 | `private void bringHome(double hadAtHome, double[] hadWithThem, FamilyStructure[] order, double[] tierShare, double[] remaining)` | Places the adults back in the families with their children, after the kept households and before the pool; see the banner above. |
| 810 | 16 | `private double placeTogether(double[] group, FamilyStructure[] order, double[] tierShare, double[] remaining)` | Forms a group's households by the builder's order and half a shape, pass after pass, until its children are placed; returns the children placed and takes the group's placed people out of the pool. |
| 827 | 5 | `private static double childrenIn(double[] group)` |  |
| 848 | 49 | `private void fitTiers(FamilyStructure[] shapes, double[] tierShare)` | THE PAY TIERS FOLLOW THE JOBS, and each shape keeps its total. |

### WHEN THERE ARE NOT ENOUGH HOMES (lines 898-1061)

| line | len | member | says |
|---:|---:|---|---|
| 925 | 5 | `public double getDoubledUpHouseholds()` | Households with no door of their own, living in somebody else's: the families', and the seekers' with their own kind. |
| 931 | 3 | `public double getSharedHouseholds()` |  |
| 936 | 1 | `public double getDoubledUpFamilies()` | The families' own doubled-up households - guests in somebody else's home - without the seekers'. |
| 944 | 4 | `public double doubledUpShare()` | The share of the families' households living crowded because of doubling up: every guest and the host they moved in with. |
| 953 | 7 | `public double seekerCrowdedShare(Seeker g)` | The share of a seeker group WITH a door who are crowded: five to a home with their own kind, or two to a door - guest and host alike. |
| 962 | 7 | `public double homesNeeded()` | Homes actually occupied, counting doubled-up households as one home. |
| 992 | 12 | `public double minimumHomesTolerable()` | The fewest homes this household mix could crowd into before somebody would genuinely have nowhere to go. |
| 1038 | 17 | `private double doorsThatCannotHelp()` | Doors the city has that the households who need one cannot enter. |

### PUTTING HOUSEHOLDS BEHIND DOORS THAT FIT (lines 1062-1090)

### TWO SEGMENTS, BECAUSE A STUDIO AND A THREE-BED ARE NOT THE SAME GOOD (lines 1091-1615)

| line | len | member | says |
|---:|---:|---|---|
| 1126 | 1 | `public double studioRentWeight()` |  |
| 1127 | 1 | `public double familyRentWeight()` |  |
| 1130 | 3 | `public static boolean needsFamilyDoor(FamilyStructure shape)` | Whether a household of this shape needs a door a child is allowed in. |
| 1138 | 8 | `public double studioSeekers()` | Households that could live in a studio: adults only, one or two of them - the seekers outside the families among them. |
| 1148 | 7 | `public double familySeekers()` | Households that need a door of size three or more. |
| 1164 | 1 | `public double studioSeekerHeads()` | The PEOPLE in each segment, as opposed to the households. |
| 1166 | 1 | `public double familySeekerHeads()` |  |
| 1168 | 8 | `private double seekerHeads(boolean family)` |  |
| 1198 | 1 | `public double rentWeight()` | The rent base: what the let homes add up to, in people of capacity. |
| 1213 | 1 | `public void setRentWeight(double weight)` | Puts back the weight the month was actually billed on. |
| 1222 | 5 | `public void setRentWeight(double studio, double family)` | Puts back BOTH weights the month was billed on. |
| 1227 | 1 | `public double getCrowdedHouseholds()` |  |
| 1228 | 1 | `public double getRefusedByStudio()` |  |
| 1231 | 3 | `public static double rentWeightOf(int unitSize)` | What one let home of this size bills, whoever is in it. |
| 1250 | 10 | `public double marginalRentWeight(int unitSize)` | What one more home of this size would earn, in person-equivalents. |
| 1267 | 15 | `public double house(long[] homesBySize)` | Matches households to homes by size, and reports what would not fit. |
| 1294 | 106 | `private double[] match(long[] homesBySize, double[] free, boolean record)` | The match itself: house() when it records, and the room the placement has left when it does not (roomLeft(), 0.7.17) - one loop, so the two cannot come to disagree about who fits where. |
| 1409 | 6 | `private void bill(int unitSize, double homes)` | Books a let: its weight to the whole, and to the segment the DOOR is in. |
| 1421 | 7 | `public void squeeze(long homesAvailable)` | Crowds households until they fit the homes available. |
| 1436 | 64 | `public void squeezeUnplaced(double excess)` | The same two valves, on households house() could not place. |
| 1513 | 1 | `public double getStillUnplaced()` |  |
| 1523 | 92 | `public void noteUnplaced(double left)` | Records what the FINAL match left over, after both valves have run. |

### THE ROOM THE PLACEMENT HAS LEFT (0.7.17) (lines 1616-1735)

| line | len | member | says |
|---:|---:|---|---|
| 1655 | 46 | **type** `public static final class Room` | What the placement could still take, and the arithmetic it was struck from. |
| 1665 | 10 | `Room(double familyDoorsFree, double studiosFree, double doublingRoom, double seeking, double withADoor, double withDependants, ...` _(in FamilyModel.Room)_ |  |
| 1689 | 6 | `public double households()` _(in FamilyModel.Room)_ | Households of the arrivals' mix the placement could still take. |
| 1697 | 3 | `public double people()` _(in FamilyModel.Room)_ | ...and as people, at the arrivals' own people to a household. |
| 1708 | 27 | `public Room roomLeft(long[] homesBySize)` | The room the placement has left against a door census, asked of the match rather than worked out from totals (see the banner above). |

### AND WHEN THEY CANNOT AFFORD ONE (lines 1736-1926)

| line | len | member | says |
|---:|---:|---|---|
| 1765 | 1 | `public double getPricedOutShares()` | Flatshares formed because a wage could not cover a home, not because there was none. |
| 1770 | 22 | `public void shareByAffordability(double[] pressure)` |  |
| 1801 | 8 | `public void shareSeekersByAffordability(double[] pressure)` | The seekers' own affordability valve: the share of each group living alone who share five to a home rather than go short. |
| 1832 | 24 | `private void placeRetired(AgeBand band, FamilyStructure coupleShape, FamilyStructure aloneShape, double people, boolean keep, d...` | One retired band into its own two shapes. |
| 1858 | 10 | `private double capacityFor(FamilyStructure shape, double[] remaining)` | How many of this shape the remaining people could fill. |
| 1870 | 14 | `private void place(FamilyStructure shape, double count, double[] tierShare, double[] remaining)` | Commits a number of households of one shape, split across the tiers. |
| 1893 | 8 | `private static FamilyStructure[] byDependantsDescending()` | Working-age shapes, most dependants first, then most adults. |
| 1917 | 9 | `private static FamilyStructure[] formableShapes()` | The shapes rebuild() may actually form, in the order it forms them. |

### saving (lines 1927-2354)

| line | len | member | says |
|---:|---:|---|---|
| 1945 | 6 | `public static String[] saveShapes()` | The shape names this build would write beside the matrix. |
| 1953 | 5 | `private static FamilyStructure shapeNamed(String name)` | The shape of that name, or null if this build has no such shape. |
| 1977 | 3 | `private static int outsideSlots(int bands)` | The same block measured against a SAVE's band count, not this build's. |
| 1981 | 3 | `private static int outsideSlots(int bands, int shapes)` |  |
| 1986 | 3 | `private static int memorySlots(int shapes)` | The formed-household memory, measured against a SAVE's shape count. |
| 2001 | 3 | `private static int kinSlots(int bands)` | The children who went out of work with their parent, appended 2026-09-15 as a TAIL rather than widened into the outside block. |
| 2017 | 3 | `public static int slotsBeforeMemory()` | Slots a save carries before the formed-household memory. |
| 2035 | 77 | `public double[] toSaveArray()` | Flattened row by row. |
| 2145 | 3 | `public void restore(double[] saved)` | Puts the households back. |
| 2149 | 3 | `public void restore(String[] bands, double[] saved)` |  |
| 2163 | 101 | `public void restore(String[] bands, String[] shapes, double[] saved)` | BOTH AXES COME FROM THE SAVE. |
| 2266 | 5 | `private static AgeBand bandNamed(String name)` | The band of that name, or null if this build has no such band. |
| 2295 | 3 | `public void adoptCarriedUnplaced()` | The saved residual wins over the load path's one-pass re-derivation. |
| 2324 | 7 | `public void adoptCarriedDoubling()` | The saved crowding wins over the load path's one-pass re-derivation. |
| 2332 | 22 | `public void reset()` |  |

