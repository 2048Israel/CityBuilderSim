# FamilyModel.java - 2,708 lines · 129 methods · 17 constants · model

`ham/citybuildersim/FamilyModel.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

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

**Uses:** [FamilyStructure](FamilyStructure.md) (113), [AgeBand](AgeBand.md) (58), [PayTier](PayTier.md) (41), [AdultStage](AdultStage.md) (26), [PopulationCohorts](PopulationCohorts.md) (5), [MoneyAudit](MoneyAudit.md) (2), [HouseholdBalance](HouseholdBalance.md) (1)

**Used by (32):** [ArrivalMixCheck](ArrivalMixCheck.md), [BusinessInvestment](BusinessInvestment.md), [CityNeeds](CityNeeds.md), [CreditCrunchCheck](CreditCrunchCheck.md), [CrimeCheck](CrimeCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [DebtExchangeCheck](DebtExchangeCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [Game](Game.md), [HealthCheck](HealthCheck.md), [HistorySave](HistorySave.md), [HouseholdAccounts](HouseholdAccounts.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HouseholdStageCheck](HouseholdStageCheck.md), [HousingCheck](HousingCheck.md), [HousingDoorCheck](HousingDoorCheck.md), [KeptBench](KeptBench.md), [KeptCheck](KeptCheck.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [Migration](Migration.md), [MoneyWordsCheck](MoneyWordsCheck.md), [Offending](Offending.md), [OutsideCheck](OutsideCheck.md), [OwnerCheck](OwnerCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [RealEstate](RealEstate.md), [StaffingDrawCheck](StaffingDrawCheck.md)

## Sections

| line | section |
|---:|---|
| 64 | THE TWO ADULT STAGES FORM THEIR OWN HOUSEHOLDS (HS2, 2026-11-19) |
| 120 | THE HOUSEHOLDS REMEMBER (2026-09-11) |
| 196 | THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11) |
| 356 | · reading |
| 396 | READING THE TIER COLUMN HONESTLY |
| 469 | · building |
| 492 | THE ADULTS WHO LEFT WORK ARE STILL AT THE KITCHEN TABLE (2026-09-15) |
| 820 | · the children one stage cannot place (HS2, 2026-11-19) |
| 963 | THE CHILDREN COME HOME WITH THEIR PARENTS (OX1, 2026-11-17) |
| 1158 | WHEN THERE ARE NOT ENOUGH HOMES |
| 1322 | PUTTING HOUSEHOLDS BEHIND DOORS THAT FIT |
| 1351 | TWO SEGMENTS, BECAUSE A STUDIO AND A THREE-BED ARE NOT THE SAME GOOD |
| 1906 | THE ROOM THE PLACEMENT HAS LEFT (0.7.17) |
| 2026 | AND WHEN THEY CANNOT AFFORD ONE |
| 2234 | · saving |

## Enum constants

| line | constant | says |
|---:|---|---|
| 226 | `FamilyModel.Seeker.UNEMPLOYED` |  |
| 226 | `FamilyModel.Seeker.STUDENT` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 94 | `FamilyModel.YOUNG_SHARE_OF_BABIES` | `.5` | Of the babies in the families, the share with a young parent (under 35): half. |
| 96 | `FamilyModel.YOUNG_SHARE_OF_CHILDREN` | `.25` | Of the children (6-12), the share with a young parent: a quarter, the parents being six to twelve years older than at the birth. |
| 98 | `FamilyModel.YOUNG_SHARE_OF_TEENS` | `0` | Of the teens (13-17), the share with a young parent: none, so the young form no shape with a teen in it (AdultStage.forms). |
| 146 | `FamilyModel.REFORMING_EACH_MONTH` | `.01` | The share of households that re-form on their own each month. |
| 232 | `FamilyModel.SEEKERS` | `Seeker.values().length` |  |
| 836 | `FamilyModel.CHILD_BANDS` | `3` | The bands of the children in a stage's pool, in its order (babies, children, teens). |
| 961 | `FamilyModel.SHAPE_TAKE` | `.5` | The share of what is still possible a shape takes in one pass of the builder, every shape but the last: half, so a large family does not eat every child and the couples leave single adults behind. |
| 1012 | `FamilyModel.HOME_PASSES` | `64` | The most passes the children who came back are placed in: each places at least SHAPE_TAKE of every band while the adults last, so this leaves under a child in 10^19. |
| 1395 | `FamilyModel.STUDIO_MAX_SIZE` | `2` | The largest unit that counts as a studio. |
| 2050 | `FamilyModel.MAX_SHARING` | `.85` | Not everybody doubles up, however dear the rent. |
| 2122 | `FamilyModel.COUPLED_SENIORS` | `.55` | What share of a retired band lives as a couple rather than alone. |
| 2123 | `FamilyModel.COUPLED_ELDERS` | `.25` |  |
| 2245 | `FamilyModel.LEGACY_SHAPES` | `{ "SENIOR_ALONE", "SENIOR_COUPLE", "SINGLE_ADULT", "COUPLE", "SINGLE_PARENT",...` | The shapes a save written before the names travelled must be read with. |
| 2267 | `FamilyModel.OUTSIDE_SLOTS` | `outsideSlots(AgeBand.values().length)` | What the people outside the families add to the save: see toSaveArray(). |
| 2312 | `FamilyModel.KIN_SLOTS` | `kinSlots(AgeBand.values().length)` |  |
| 2329 | `FamilyModel.MEMORY_SLOTS` | `FamilyStructure.values().length * PayTier.values().length + 1 + 4` | ...and what the households remember: the formed matrix, whether there is one, the month's four counts. |
| 2428 | `FamilyModel.STAGE_SLOTS` | `2 * FamilyStructure.values().length * PayTier.values().length` | ...and the young part of the matrix and of the record (HS2): shapes by tiers, twice. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 58 | `private final double[][] households` | How many households of each shape, at each pay tier. |
| 62 | `private double unhoused` | Adults left over with no household, which should be zero. |
| 101 | `private final double[][] young` | The young part of each cell of the matrix: households[shape][tier] less this is the established. |
| 148 | `private boolean remembers` |  |
| 149 | `private final double[][] formed` |  |
| 151 | `private boolean haveFormed` |  |
| 152 | `private double lastKept, lastReformed, lastNoLongerFit, lastNew` |  |
| 167 | `private final double[][] formedYoung` | ...the young part of it (HS2): the established are getFormed less this. |
| 219 | `private double outsideAdults` | Adults this month's families were NOT built from: the out of work and the students. |
| 222 | `private final double[] orphans` | Children no family holds, by band. |
| 227 | `private final String label` |  |
| 235 | `private final double[] seekers` | One-adult households of each group looking for a door this month. |
| 238 | `private final double[] seekersSharing` | ...of whom this many live five to a home, with their own kind only. |
| 241 | `private final double[] seekersDoubled` | ...and this many doubled up two to a door, with their own kind only. |
| 244 | `private final double[] seekersUnhoused` | ...and this many with no door at all after both valves. |
| 247 | `private final double[] seekersUnplaced` | What the last match left unplaced, by group, before the valves. |
| 250 | `private final double[] seekersUnplacedHomes` | ...the same, in the households the match counts - a flatshare is one. |
| 253 | `private final double[] unhousedByShape` | Households of each shape the final match and the doubling valve could not place. |
| 256 | `private final double[] unplacedByShape` | Households of each shape the last match left unplaced. |
| 536 | `private final double[] outsideDependants` | Children of the adults who left work but not the house, by band. |
| 539 | `private double atHomeAdults` | Adults outside the families who still live with their dependants. |
| 554 | `private double ownerAdults` | Adults of the owner families (BO1): outside the families, no dependants. |
| 1014 | `private double lastBackAdults, lastBackChildren, lastBackChildrenHome` |  |
| 1182 | `private double doubledUp` |  |
| 1320 | `private long[] lastHomesBySize` | The door census house() was last handed. |
| 1349 | `private double rentWeight` | What the landlords can bill for, in person-equivalents. |
| 1381 | `private double studioRentWeight` | The part of rentWeight billed on units of size 1-2. |
| 1384 | `private double familyRentWeight` | ...and on units of size 3 and up. |
| 1438 | `private double crowdedHouseholds` | Households living somewhere too small for them. |
| 1441 | `private double refusedByStudio` | Households a studio turned away because they have a child. |
| 1692 | `private double[] occupiedBySize` | Books a let: its weight to the whole, and to the segment the DOOR is in. |
| 1800 | `private double stillUnplaced` | Households both valves failed to place. |
| 1947 | `public final double familyDoorsFree, studiosFree` | Doors of size three and up, and studios, that nobody would be behind. |
| 1949 | `public final double doublingRoom` | Households the families' doubling valve could still take. |
| 1951 | `public final double seeking, withADoor` | Households that want a door (householdsSeekingDoors()), and those that would have one of their own. |
| 1953 | `public final double withDependants, peoplePerHousehold` | The arrivals' mix: the share of their households with a dependant, and people to a household. |
| 2052 | `private double pricedOutShares` |  |
| 2628 | `private double carriedUnplaced` | What the save said was left with nowhere, or -1 on a save from before it was carried. |
| 2634 | `private double carriedDoubledUp` | ...and what it said was crowded. |
| 2635 | `private double[] carriedSeekersDoubled` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 49 | 2660 | **type** `public class FamilyModel implements HouseholdBalance.Census` | How the city's people are arranged into households, and what each earns. |

### THE TWO ADULT STAGES FORM THEIR OWN HOUSEHOLDS (HS2, 2026-11-19) (lines 64-119)

| line | len | member | says |
|---:|---:|---|---|
| 105 | 8 | `public static double youngShareOf(AgeBand band)` | Of the dependants of a band, the share living with a young parent; the adults split by the pyramid, not by this. |
| 115 | 4 | `private static double shareOfStage(AgeBand band, AdultStage stage)` | ...and of a stage: the young's share, or the rest for the established. |

### THE HOUSEHOLDS REMEMBER (2026-09-11) (lines 120-195)

| line | len | member | says |
|---:|---:|---|---|
| 155 | 1 | `public void rememberHouseholds(boolean on)` | Switches the memory on; Game does, a bare model does not. |
| 156 | 1 | `public boolean remembersHouseholds()` |  |
| 159 | 1 | `public boolean hasRecord()` | Whether there is a record to keep from - false in a new city and a save from before. |
| 162 | 3 | `public double getFormed(FamilyStructure shape, PayTier tier)` | What the builder formed last month, before the valves. |
| 171 | 5 | `public double getFormed(FamilyStructure shape, PayTier tier, AdultStage stage)` | What one stage formed last month (HS2). |
| 178 | 8 | `private double[][] formedOf(AdultStage stage)` | One stage's part of last month's record, a matrix of its own. |
| 188 | 1 | `public double getLastKept()` | Households kept from last month's record this month. |
| 190 | 1 | `public double getLastReformed()` | Households that re-formed on their own, at REFORMING_EACH_MONTH. |
| 192 | 1 | `public double getLastNoLongerFit()` | Households the pyramid no longer had the people for: a death, a child grown, a lost job. |
| 194 | 1 | `public double getLastNew()` | Households the builder formed from the people left over. |

### THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11) (lines 196-355)

| line | len | member | says |
|---:|---:|---|---|
| 225 | 6 | **type** `public enum Seeker` | The groups of one-adult households the matrix does not hold but the housing match does. |
| 228 | 1 | `Seeker(String label)` _(in FamilyModel.Seeker)_ |  |
| 229 | 1 | `public String label()` _(in FamilyModel.Seeker)_ |  |
| 259 | 1 | `public double getOutsideAdults()` | Adults outside the families this month. |
| 262 | 1 | `public double getOrphans(AgeBand band)` | Children of this band in no household. |
| 264 | 5 | `public double getOrphansTotal()` |  |
| 277 | 8 | `public void setSeekers(double unemployedHoused, double students)` | Tells the match who else wants a door this month, before house(). |
| 286 | 1 | `public double getSeekers(Seeker g)` |  |
| 287 | 1 | `public double getSeekersSharing(Seeker g)` |  |
| 288 | 1 | `public double getSeekersDoubled(Seeker g)` |  |
| 289 | 1 | `public double getSeekersUnhoused(Seeker g)` |  |
| 292 | 4 | `private double seekerHouseholds(int g)` | A group's households as doors see them: one each alone, a fifth each sharing. |
| 302 | 8 | `public double seekerDoorShare(Seeker g)` | What one household of the group pays of a door's rent: all of it alone, a fifth sharing, half doubled up, none with no door. |
| 312 | 5 | `private double seekerDoors(int g)` | Doors a group is behind: alone, and a fifth of one for each sharer. |
| 319 | 5 | `public double householdsSeekingDoors()` | Every household that wants a door: the family matrix, and the seekers outside it. |
| 326 | 4 | `public double seekerUnhousedShare(Seeker g)` | The share of a group with no door. |
| 332 | 1 | `public double getUnhoused(FamilyStructure shape)` | Households of this shape with no door after both valves. |
| 335 | 4 | `public double unhousedShareOf(FamilyStructure shape)` | The share of a shape's households with no door; they pay no rent. |
| 345 | 10 | `public double[] unhousedPeopleByBand()` | People with no door, by band: the families both valves failed, and the seekers their own kind's valves failed. |

### reading (lines 356-395)

| line | len | member | says |
|---:|---:|---|---|
| 358 | 3 | `public double get(FamilyStructure shape, PayTier tier)` |  |
| 363 | 5 | `public double get(FamilyStructure shape, PayTier tier, AdultStage stage)` | The households of one adult stage in a cell (HS2): the two add to get(shape, tier). |
| 370 | 3 | `public double households(FamilyStructure shape, PayTier tier, AdultStage stage)` | The census the households' books are struck by, a stage at a time (HS2): get(shape, tier, stage). |
| 375 | 8 | `public double workingHouseholdsOf(AdultStage stage)` | The working households of one adult stage, every shape and tier (HS2). |
| 384 | 5 | `public double totalOf(FamilyStructure shape)` |  |
| 390 | 5 | `public double totalOf(PayTier tier)` |  |

### READING THE TIER COLUMN HONESTLY (lines 396-468)

| line | len | member | says |
|---:|---:|---|---|
| 411 | 8 | `public double workingHouseholdsIn(PayTier tier)` | Households with an earner at this tier. |
| 421 | 8 | `public double peopleIn(PayTier tier)` | Everybody living in those households - earners, partners and children. |
| 431 | 7 | `public double retiredHouseholds()` | Households with nobody of working age in them. |
| 439 | 7 | `public double retiredPeople()` |  |
| 447 | 5 | `public double totalHouseholds()` |  |
| 453 | 3 | `public double getUnhousedAdults()` |  |
| 458 | 10 | `public double averageHouseholdSize()` | People per household, the figure a housing model would eventually want. |

### building (lines 469-491)

| line | len | member | says |
|---:|---:|---|---|
| 477 | 3 | `public void rebuild(PopulationCohorts cohorts, double[] jobsByTier)` | Rebuilds every household from the current pyramid and job mix. |
| 488 | 3 | `public void rebuild(PopulationCohorts cohorts, double[] jobsByTier, double outsideAdults)` | counted as away: nobody takes a child with them. |

### THE ADULTS WHO LEFT WORK ARE STILL AT THE KITCHEN TABLE (2026-09-15) (lines 492-819)

| line | len | member | says |
|---:|---:|---|---|
| 542 | 1 | `public double getOutsideDependants(AgeBand band)` | Children of this band living with an adult outside the families. |
| 544 | 5 | `public double getOutsideDependantsTotal()` |  |
| 551 | 1 | `public double getAtHomeAdults()` | Adults outside the families who took their children with them. |
| 556 | 1 | `public double getOwnerAdults()` |  |
| 559 | 1 | `public double getOwnerFamilies()` | The owner families this month: two adults each. |
| 567 | 3 | `public void restoreOwnerAdults(double adults)` | On a load: the owner adults the save's census counted (DataSave's, zero on a save from before BO1). |
| 578 | 3 | `public double dependantsPerOutsideHousehold()` | Dependants per outside household, for the cells that carry them. |
| 590 | 4 | `public void rebuild(PopulationCohorts cohorts, double[] jobsByTier, double awayAdults, double atHomeAdults)` | not at home either - the prisoners. |
| 601 | 218 | `public void rebuild(PopulationCohorts cohorts, double[] jobsByTier, double awayAdults, double atHomeAdults, double ownerAdults)` | are the owner families' (two a company; BO1): no job and no dependants, so they take none with them. |

### the children one stage cannot place (HS2, 2026-11-19) (lines 820-962)

| line | len | member | says |
|---:|---:|---|---|
| 839 | 7 | `private static void offerChildren(double[] from, double[] to)` | Moves the children a stage's builder left (`from`) into the next stage's pool (`to`), before the orphan section. |
| 855 | 20 | `private double keepDrawing(double[][] had, FamilyStructure[] order, double[][] m, double[] own, double[] young, double[] coming...` | The established's kept households (2b), out of their own people and, for the children, what the young's kept households left of theirs (`young`) beyond the children who may come home with young parents this month (2c;... |
| 877 | 41 | `private double keepFormed(double[][] had, FamilyStructure[] order, double[][] m, double[] remaining)` | One stage's kept households (2b): last month's record of the stage less the share that re-forms, shrunk to the people the stage has; returns the households kept, and takes their people out of the stage's pool. |
| 920 | 30 | `private static void build(FamilyStructure[] order, double[] tierShare, double[] remaining, double[][] m)` | The builder proper (3): every shape in the order takes half of what is still possible, and the last takes the rest. |
| 952 | 7 | `private void recordFormed()` | What this month's builder formed, before the valves: next month's reference. |

### THE CHILDREN COME HOME WITH THEIR PARENTS (OX1, 2026-11-17) (lines 963-1157)

| line | len | member | says |
|---:|---:|---|---|
| 1017 | 1 | `public double getLastBackAdults()` | Adults back in the families this month: last month's adults at home less this month's, when that fell, and no more than the families' adults grew by. |
| 1019 | 1 | `public double getLastBackChildren()` | The children who came back with them, at last month's rate to an adult at home. |
| 1021 | 1 | `public double getLastBackChildrenHome()` | ...of whom this many were placed in a household with them: all, while their adults last. |
| 1030 | 25 | `private void bringHome(double hadAtHome, double[] hadWithThem, double familiesGrew, double youngShare, FamilyStructure[][] orde...` | Places the adults back in the families with their children, after the kept households and before the pool; see the banner above. |
| 1057 | 17 | `private static double placeTogether(double[] group, FamilyStructure[] order, double[] tierShare, double[] remaining, double[][] m)` | Forms a group's households by the builder's order and half a shape, pass after pass, until its children are placed; returns the children placed and takes the group's placed people out of the pool. |
| 1076 | 8 | `private double adultsFormed()` | The adults in last month's formed households: the families' adults when the builder last ran, since the single adults take every adult the other shapes leave. |
| 1085 | 5 | `private static double childrenIn(double[] group)` |  |
| 1108 | 49 | `private static void fitTiers(FamilyStructure[] shapes, double[] tierShare, double[][] households)` | THE PAY TIERS FOLLOW THE JOBS, and each shape keeps its total. |

### WHEN THERE ARE NOT ENOUGH HOMES (lines 1158-1321)

| line | len | member | says |
|---:|---:|---|---|
| 1185 | 5 | `public double getDoubledUpHouseholds()` | Households with no door of their own, living in somebody else's: the families', and the seekers' with their own kind. |
| 1191 | 3 | `public double getSharedHouseholds()` |  |
| 1196 | 1 | `public double getDoubledUpFamilies()` | The families' own doubled-up households - guests in somebody else's home - without the seekers'. |
| 1204 | 4 | `public double doubledUpShare()` | The share of the families' households living crowded because of doubling up: every guest and the host they moved in with. |
| 1213 | 7 | `public double seekerCrowdedShare(Seeker g)` | The share of a seeker group WITH a door who are crowded: five to a home with their own kind, or two to a door - guest and host alike. |
| 1222 | 7 | `public double homesNeeded()` | Homes actually occupied, counting doubled-up households as one home. |
| 1252 | 12 | `public double minimumHomesTolerable()` | The fewest homes this household mix could crowd into before somebody would genuinely have nowhere to go. |
| 1298 | 17 | `private double doorsThatCannotHelp()` | Doors the city has that the households who need one cannot enter. |

### PUTTING HOUSEHOLDS BEHIND DOORS THAT FIT (lines 1322-1350)

### TWO SEGMENTS, BECAUSE A STUDIO AND A THREE-BED ARE NOT THE SAME GOOD (lines 1351-1905)

| line | len | member | says |
|---:|---:|---|---|
| 1386 | 1 | `public double studioRentWeight()` |  |
| 1387 | 1 | `public double familyRentWeight()` |  |
| 1390 | 3 | `public static boolean needsFamilyDoor(FamilyStructure shape)` | Whether a household of this shape needs a door a child is allowed in. |
| 1398 | 8 | `public double studioSeekers()` | Households that could live in a studio: adults only, one or two of them - the seekers outside the families among them. |
| 1408 | 7 | `public double familySeekers()` | Households that need a door of size three or more. |
| 1424 | 1 | `public double studioSeekerHeads()` | The PEOPLE in each segment, as opposed to the households. |
| 1426 | 1 | `public double familySeekerHeads()` |  |
| 1428 | 8 | `private double seekerHeads(boolean family)` |  |
| 1458 | 1 | `public double rentWeight()` | The rent base: what the let homes add up to, in people of capacity. |
| 1473 | 1 | `public void setRentWeight(double weight)` | Puts back the weight the month was actually billed on. |
| 1482 | 5 | `public void setRentWeight(double studio, double family)` | Puts back BOTH weights the month was billed on. |
| 1487 | 1 | `public double getCrowdedHouseholds()` |  |
| 1488 | 1 | `public double getRefusedByStudio()` |  |
| 1491 | 3 | `public static double rentWeightOf(int unitSize)` | What one let home of this size bills, whoever is in it. |
| 1510 | 10 | `public double marginalRentWeight(int unitSize)` | What one more home of this size would earn, in person-equivalents. |
| 1527 | 16 | `public double house(long[] homesBySize)` | Matches households to homes by size, and reports what would not fit. |
| 1554 | 4 | `static double packingRounding(double households)` | What the packing's own arithmetic may leave of households it placed exactly, at a size of `households` (OE1): MoneyAudit's rule in households for dollars, RELATIVE_TOLERANCE of the size and never under ULP_STEPS of a ... |
| 1570 | 113 | `private double[] match(long[] homesBySize, double[] free, boolean record)` | The match itself: house() when it records, and the room the placement has left when it does not (roomLeft(), 0.7.17) - one loop, so the two cannot come to disagree about who fits where. |
| 1695 | 1 | `public double[] occupiedBySize()` | Actual lets by door size; BO1-H's owner-family seekers belong in the match. |
| 1697 | 7 | `private void bill(int unitSize, double homes)` |  |
| 1710 | 7 | `public void squeeze(long homesAvailable)` | Crowds households until they fit the homes available. |
| 1725 | 63 | `public void squeezeUnplaced(double excess)` | The same two valves, on households house() could not place. |
| 1801 | 1 | `public double getStillUnplaced()` |  |
| 1811 | 94 | `public void noteUnplaced(double left)` | Records what the FINAL match left over, after both valves have run. |

### THE ROOM THE PLACEMENT HAS LEFT (0.7.17) (lines 1906-2025)

| line | len | member | says |
|---:|---:|---|---|
| 1945 | 46 | **type** `public static final class Room` | What the placement could still take, and the arithmetic it was struck from. |
| 1955 | 10 | `Room(double familyDoorsFree, double studiosFree, double doublingRoom, double seeking, double withADoor, double withDependants, ...` _(in FamilyModel.Room)_ |  |
| 1979 | 6 | `public double households()` _(in FamilyModel.Room)_ | Households of the arrivals' mix the placement could still take. |
| 1987 | 3 | `public double people()` _(in FamilyModel.Room)_ | ...and as people, at the arrivals' own people to a household. |
| 1998 | 27 | `public Room roomLeft(long[] homesBySize)` | The room the placement has left against a door census, asked of the match rather than worked out from totals (see the banner above). |

### AND WHEN THEY CANNOT AFFORD ONE (lines 2026-2233)

| line | len | member | says |
|---:|---:|---|---|
| 2055 | 1 | `public double getPricedOutShares()` | Flatshares formed because a wage could not cover a home, not because there was none. |
| 2060 | 21 | `public void shareByAffordability(double[] pressure)` |  |
| 2083 | 10 | `private void shareSingles(int t, double moving)` | Five singles at a tier into one flatshare, the young among them in the share they hold of the tier's singles, so each stage's flatshares are its own singles' (HS2). |
| 2102 | 8 | `public void shareSeekersByAffordability(double[] pressure)` | The seekers' own affordability valve: the share of each group living alone who share five to a home rather than go short. |
| 2133 | 24 | `private void placeRetired(AgeBand band, FamilyStructure coupleShape, FamilyStructure aloneShape, double people, boolean keep, d...` | One retired band into its own two shapes. |
| 2159 | 10 | `private static double capacityFor(FamilyStructure shape, double[] remaining)` | How many of this shape the remaining people could fill. |
| 2171 | 14 | `private static void place(FamilyStructure shape, double count, double[] tierShare, double[] remaining, double[][] m)` | Commits a number of households of one shape, split across the tiers, to one stage's matrix. |
| 2194 | 8 | `private static FamilyStructure[] byDependantsDescending()` | Working-age shapes, most dependants first, then most adults. |
| 2218 | 3 | `private static FamilyStructure[] formableShapes()` | The shapes rebuild() may actually form, in the order it forms them. |
| 2223 | 10 | `private static FamilyStructure[] formableShapes(AdultStage stage)` | ...the shapes one adult stage forms, in the same order (HS2): the young form none with a teen and no two-child family, and SINGLE_ADULT stays last in both. |

### saving (lines 2234-2708)

| line | len | member | says |
|---:|---:|---|---|
| 2252 | 6 | `public static String[] saveShapes()` | The shape names this build would write beside the matrix. |
| 2260 | 5 | `private static FamilyStructure shapeNamed(String name)` | The shape of that name, or null if this build has no such shape. |
| 2284 | 3 | `private static int outsideSlots(int bands)` | The same block measured against a SAVE's band count, not this build's. |
| 2288 | 3 | `private static int outsideSlots(int bands, int shapes)` |  |
| 2293 | 3 | `private static int memorySlots(int shapes)` | The formed-household memory, measured against a SAVE's shape count. |
| 2308 | 3 | `private static int kinSlots(int bands)` | The children who went out of work with their parent, appended 2026-09-15 as a TAIL rather than widened into the outside block. |
| 2324 | 3 | `public static int slotsBeforeMemory()` | Slots a save carries before the formed-household memory. |
| 2342 | 84 | `public double[] toSaveArray()` | Flattened row by row. |
| 2431 | 3 | `static int stageSlots(int shapes)` | The stages' block, at a save's own shape count. |
| 2472 | 3 | `public void restore(double[] saved)` | Puts the households back. |
| 2476 | 3 | `public void restore(String[] bands, double[] saved)` |  |
| 2490 | 126 | `public void restore(String[] bands, String[] shapes, double[] saved)` | BOTH AXES COME FROM THE SAVE. |
| 2618 | 5 | `private static AgeBand bandNamed(String name)` | The band of that name, or null if this build has no such band. |
| 2647 | 3 | `public void adoptCarriedUnplaced()` | The saved residual wins over the load path's one-pass re-derivation. |
| 2676 | 7 | `public void adoptCarriedDoubling()` | The saved crowding wins over the load path's one-pass re-derivation. |
| 2684 | 24 | `public void reset()` |  |

