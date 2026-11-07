# PrizeCheck.java - 1,759 lines · 66 methods · 8 constants · harnesses

`ham/citybuildersim/PrizeCheck.java` - generated 2026-11-07 by CodeMap; line numbers are as of that run.

> A prize pays only for a result, a Research Lab goes for the cheapest problem the purse pays for, and the schools' share of a purse buys posts
> (R4, slice A: Research; docs/studies/research-design.md 5.3, 1.5, 5.4).
> 
> WHY. A grant pays for the work whether it finds anything or not; a prize pays only for the find, and the whole point of it (5.3) is WHO does the
> work: labs do not spread as the city's researchers do, they go for the cheapest problem a team could finish before the world, and the
> purse is split by who did the work. Every claim here is a statement about a month and a threshold ("the prize is at least the remaining work
> times a researcher-year's cost": one hair under it a lab stays away), so each threshold is caused from both sides and held to Research's own
> constants and functions and never to a number copied from the study's formula; and a wrong order in the month (the chase computed after the
> work, the purse read before the find) moves a find by a year and no assertion elsewhere would see it. Sections 1 to 10 (slice A) are the
> pure model, which has to be right before Game meets it; sections 11 to 17 (slice B) are Game meeting it.
> 
> What this has to prove:
> 
>   1. THE DIALS. A prize is an amount, never below nothing, none by default, set for its field alone; a lab's jobs are LAB_POSTS posts' jobs;
>      a reform of the money scales the prizes and the pots and nothing else new.
>   2. THE PURSE'S RULE. A purse pays for a problem when the prize is at least the remaining work times twelve times the post's cost: true at
>      exactly that and false a hair under; the remaining work floors at REMAINING_DUST; a team at its limit finishes before the world on this
>      side of history's month less its months to finish and not on that.
>   3. THE CHASE. Only open problems that pass both rules, least remaining first, ties in the catalogue's order; empty for a prize of 0, for
>      no wages; a known problem and one whose science does not exist yet or whose need is unknown never.
>   4. LABS ON THE TARGET. With and without labs the city's shares are the same; labs fill the cheapest first up to the team cap less the city's
>      share, then the next; the rest is idle; the piles gain (share + lab) / 12 and the labs' part is kept apart.
>   5. THE PURSE. A find with both kinds of work splits the prize by the work piled; all of it goes to the schools when no lab worked on the
>      problem; none with a prize of 0; the purse is listed in its month only; toLabs sums the field's; the pot gains the schools' share; a
>      world arrival drops the labs' part of a pile.
>   6. THE POT BUYS POSTS. The strike buys posts for the grant and the pot, remembers the grant's alone; the bill's draw is the difference at
>      the month's wages and fill, never more than the pot; a post nobody fills draws nothing.
>   7. INVESTORS. Labs are wanted while the n-th would keep half its researchers at work, from both sides of each threshold; the room is the
>      chase list's, less the city's last shares; the target is the chase's first and the stake its months of a lab's pay; none without a chase.
>   8. IDLE LABS. The months a field has a lab too many are counted and zeroed as the rule says, from both sides of the threshold; labs with
>      nothing to chase are all idle; a field with no lab counts none; idleMonth() counts as month() does.
>   9. THE SAVE. A run of hundreds of months saved midway and carried on finds the same codes in the same months, with the same piles, lab piles,
>      pots, idle counts and purses, as the unbroken one; a malformed entry is skipped; reset() clears everything new; isEmpty() sees each.
>  10. NO PRIZE, NO CHANGE. With no labs the work is the study's own R3 month (a reference written here), whatever the prize; with a prize of 0
>      labs sit idle and change nothing; nobody wants a lab.
> 
> R4, slice B: the labs in the city (Game, the templates, the treasury's prizes line, the inbox, the save), on villages founded the 1900 way with a
> University (DiscoveryCheck's fixtures) played to the month the model first wants a lab:
> 
>  11. THE LABS ARE BUILDINGS. Four templates in the built-in definitions and in buildings.json, one a field, named, ids 148 to 151, in Business
>      Services, making nothing and needing no licence, with Research.labJobs() job type by job type and the Engineering Services Office's figures
>      times the study's 12 / 10.9; a "researches" that is no field is reported and read as none; Business Services' plan is never a lab nor held for
>      the staff of one, in a 1900 village, a default one and one with no hands.
>  12. INVESTORS BUILD A LAB. At the prize that just makes the model want one a lab is on site within a month, the owners' raise recorded and no loan;
>      a hair under, none; a default-way village with the same prize, none; at most one a field a month, with two wanted.
>  13. LABS AT WORK. The month's lab researchers are the labs' posts' filled researchers at the month's fill, and their work lands on the target.
>  14. THE PURSE PAID. In the month of a find the treasury's prizes line pays exactly the labs' share, Business Services' books receive the same
>      figure and close, the audit closes, the national accounts carry it with the subsidies, the schools' share is in the pot and the inbox's notice
>      tells it; past the treasury's room the rest is owed as arrears.
>  15. IDLE LABS ARE SHED. A lab with nothing to chase stands for exactly LAB_IDLE_MONTHS months of play and is sold back the month after; not for a
>      sector a harness holds.
>  16. THE SAVE. The new keys are written and read back; a loaded city and the one saved play two years alike; a city with only a prize writes the key.
>  17. A CITY FOUNDED THE DEFAULT WAY orders nothing, pays nothing and has no lab, whatever the prizes; a reform of the money scales the prize, the
>      pot and the month's prize paid.

**Uses:** [Research](Research.md) (268), [Game](Game.md) (36), [DiscoveryCheck](DiscoveryCheck.md) (26), [BuildingsTemplate](BuildingsTemplate.md) (13), [TreasuryLine](TreasuryLine.md) (8), [JobType](JobType.md) (6), [Inbox](Inbox.md) (6), [MoneyAudit](MoneyAudit.md) (5), [BusinessInvestment](BusinessInvestment.md) (3), [ResearchCatalog](ResearchCatalog.md) (2), [BuildingManager](BuildingManager.md) (2), [BuildingCatalog](BuildingCatalog.md) (2), [BusinessServices](BusinessServices.md) (2), [SectorBooks](SectorBooks.md) (2), [GameFiles](GameFiles.md) (2), [EquipmentCheck](EquipmentCheck.md) (2), [PayTier](PayTier.md) (1), [Sectors](Sectors.md) (1), [MaterialsCheck](MaterialsCheck.md) (1), [BuildingType](BuildingType.md) (1), [Sector](Sector.md) (1), [EconomyManager](EconomyManager.md) (1), [Notice](Notice.md) (1)

**Used by (1):** [ResearchViewCheck](ResearchViewCheck.md)

## Sections

| line | section |
|---:|---|
| 99 | · the fixtures: the shipped catalogue, the game's wages |
| 277 | 1. the dials |
| 325 | 2. the purse's rule |
| 381 | 3. the chase |
| 515 | 4. labs on the target |
| 612 | 5. the purse |
| 744 | 6. the pot buys posts |
| 818 | 7. investors |
| 925 | 8. idle labs |
| 1024 | 9. the save |
| 1142 | 10. no prize, no change |
| 1262 | R4 SLICE B: THE LABS IN THE CITY (sections 11 to 17). A village founded the 1900 way (or the default way) with a Univ... |
| 1354 | 11. the labs are buildings |
| 1447 | 12. investors build a lab |
| 1507 | 13. labs at work |
| 1542 | 14. the purse paid |
| 1616 | 15. idle labs are shed |
| 1650 | 16. the save |
| 1722 | 17. a city founded the default way; a reform of the money |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 101 | `PrizeCheck.CAT` | `ResearchCatalog.get()` |  |
| 102 | `PrizeCheck.SEED` | `20261010L` |  |
| 105 | `PrizeCheck.BIG` | `1e12` | A prize that covers any problem there is: the chase is then ruled by the world's date alone. |
| 114 | `PrizeCheck.W` | `wages(1)` |  |
| 123 | `PrizeCheck.FULL` | `fill(1)` |  |
| 1270 | `PrizeCheck.F` | `Field.ENGINEERING` | The field the city sections work in: the University hosts it, and the first lab any field wants is wanted in it. |
| 1272 | `PrizeCheck.BS` | `Sectors.BUSINESS_SERVICES` |  |
| 1275 | `PrizeCheck.STUDY_RATIO` | `12 / 10.9` | research-design 5.3: the lab's D$ 12M against the Engineering Services Office's D$ 10.9M for 120 staff, which the lab templates' figures are written from. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 81 | `static int fails` |  |
| 82 | `static int passes` |  |
| 167 | `final Research r` |  |
| 168 | `final long seed` |  |
| 169 | `final EnumMap<Field, Double> city` |  |
| 170 | `final EnumMap<Field, Integer> labs` |  |
| 171 | `double[] wages` |  |
| 172 | `double drawn` |  |
| 174 | `Map<String, Double> pilesBefore` | what stood before the month's work: the piles, the labs' part, the pots, the chase of each field |
| 175 | `final EnumMap<Field, Double> potBefore` |  |
| 176 | `final EnumMap<Field, List<Entry>> chaseBefore` |  |
| 1146 | `final Map<String, Double> piles` |  |
| 1147 | `final Map<String, Integer> found` |  |
| 1148 | `final List<String> foundNow` |  |
| 1149 | `final EnumMap<Field, Double> idle` |  |
| 1150 | `final EnumMap<Field, Integer> open` |  |
| 1546 | `Game g` |  |
| 1547 | `Entry target` |  |
| 1548 | `double prize, potBefore` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 79 | 1681 | **type** `public class PrizeCheck` | A prize pays only for a result, a Research Lab goes for the cheapest problem the purse pays for, and the schools' share of a purse buys posts (R4, slice A: Research; docs/studies/research-design.md 5.3, 1.5, 5.4). |
| 84 | 4 | `static void check(String label, boolean ok)` |  |
| 90 | 4 | `static void check(String label, boolean ok, String found)` | The same, with what was found printed under a failure. |
| 95 | 3 | `static void near(String label, double actual, double expected, double tolerance)` |  |

### the fixtures: the shipped catalogue, the game's wages (lines 99-276)

| line | len | member | says |
|---:|---:|---|---|
| 108 | 5 | `static double[] wages(double times)` | The wages by JobType ordinal, the game's (PayTier.getMonthlyWage()), times `times`. |
| 117 | 5 | `static double[] fill(double share)` | Every job type filled to `share`. |
| 125 | 1 | `static Entry of(String code)` |  |
| 127 | 5 | `static int lastWorldMonth()` |  |
| 133 | 1 | `static double cost(Field f)` |  |
| 136 | 3 | `static double cover(Entry e, double pile)` | The prize that just covers a problem with that pile: its remaining work times twelve times the post's cost, by Research's own statics. |
| 141 | 3 | `static double prizeFor(Field f, double work)` | The prize that covers a problem of that much typical work, untouched. |
| 145 | 5 | `static EnumMap<Field, Double> map(Field f, double v)` |  |
| 151 | 5 | `static EnumMap<Field, Integer> counts(Field f, int n)` |  |
| 157 | 1 | `static int indexOf(Entry e)` |  |
| 159 | 5 | `static List<String> codes(List<Entry> list)` |  |
| 166 | 36 | **type** `static final class Sim` | One city's research month by month, in the game's order: the posts are struck, the pots pay their bill, then the month's work is done. |
| 178 | 1 | `Sim(long seed)` _(in PrizeCheck.Sim)_ |  |
| 180 | 1 | `Sim city(Field f, double n)` _(in PrizeCheck.Sim)_ |  |
| 182 | 1 | `Sim labs(Field f, int n, double people)` _(in PrizeCheck.Sim)_ |  |
| 184 | 1 | `Sim room(Field f, double n)` _(in PrizeCheck.Sim)_ |  |
| 186 | 11 | `void step(int m)` _(in PrizeCheck.Sim)_ |  |
| 198 | 3 | `void run(int from, int to)` _(in PrizeCheck.Sim)_ |  |
| 204 | 10 | `static Sim standard(long seed)` | Engineering and medicine with labs, grants, prizes and posts to buy: the city the save and the invariants are run on. |
| 216 | 6 | `static Sim potted()` | A city that has made one find with a prize and no lab: its schools' pot is full. |
| 224 | 29 | `static List<String> invariants(Sim s, int m)` | The invariants of a month's step, found broken; empty when none. |
| 254 | 22 | `public static void main(String[] args) throws Exception` |  |

### 1. the dials (lines 277-324)

| line | len | member | says |
|---:|---:|---|---|
| 279 | 45 | `static void sectionOne()` |  |

### 2. the purse's rule (lines 325-380)

| line | len | member | says |
|---:|---:|---|---|
| 327 | 4 | `static Entry firstOfSize(Size size)` |  |
| 332 | 48 | `static void sectionTwo()` |  |

### 3. the chase (lines 381-514)

| line | len | member | says |
|---:|---:|---|---|
| 384 | 1 | **type** `record Tie(Field field, int month, Entry a, Entry b, List<Entry> list)` | Two problems of one field with the same remaining work on the chase together, and the month. |
| 386 | 13 | `static Tie findTie()` |  |
| 400 | 114 | `static void sectionThree()` |  |

### 4. labs on the target (lines 515-611)

| line | len | member | says |
|---:|---:|---|---|
| 518 | 1 | **type** `record Spot(Field field, int month, List<Entry> chase, List<Entry> open)` | A field and a month where three or more problems are on the chase and one more is open and is not. |
| 521 | 13 | `static Spot findSpot(int labs)` | The first such month in which the chase's team caps whole have room for at least `labs` labs (labsFor()). |
| 536 | 6 | `static Sim spotSim(Spot s, double cityPeople, double labPeople)` | A sim at a spot: a city of researchers spread over what is open, and `labPeople` lab researchers, one month. |
| 543 | 1 | `static double room(Research r, Entry e)` |  |
| 545 | 66 | `static void sectionFour()` |  |

### 5. the purse (lines 612-743)

| line | len | member | says |
|---:|---:|---|---|
| 614 | 129 | `static void sectionFive()` |  |

### 6. the pot buys posts (lines 744-817)

| line | len | member | says |
|---:|---:|---|---|
| 746 | 71 | `static void sectionSix()` |  |

### 7. investors (lines 818-924)

| line | len | member | says |
|---:|---:|---|---|
| 820 | 104 | `static void sectionSeven()` |  |

### 8. idle labs (lines 925-1023)

| line | len | member | says |
|---:|---:|---|---|
| 928 | 5 | `static double roomNow(Research r, Field f, int m)` | The researchers the chase could take in a field at a month with no city researchers: the team caps of its problems, whole. |
| 934 | 82 | `static void sectionEight()` |  |
| 1018 | 5 | `static boolean sheddingOne(Research r, Field shed, Field kept)` | Sheds a lab of the field and says whether it alone was cleared. |

### 9. the save (lines 1024-1141)

| line | len | member | says |
|---:|---:|---|---|
| 1027 | 9 | `static String kept(Research r, int month)` | What a save keeps, as text: compared between the unbroken run and the restored one. |
| 1038 | 9 | `static String monthly(Research r)` | What the month did, as text. |
| 1048 | 93 | `static void sectionNine()` |  |

### 10. no prize, no change (lines 1142-1261)

| line | len | member | says |
|---:|---:|---|---|
| 1145 | 48 | **type** `static final class Reference` | The study's R3 month, written again here from nothing but Research's statics: what the city's own researchers do whatever labs and prizes there are. |
| 1152 | 1 | `boolean known(String code, int m)` _(in PrizeCheck.Reference)_ |  |
| 1154 | 5 | `boolean isOpen(Entry e, int m)` _(in PrizeCheck.Reference)_ |  |
| 1160 | 32 | `void month(int m, long seed, EnumMap<Field, Double> researchers)` _(in PrizeCheck.Reference)_ |  |
| 1194 | 5 | `static EnumMap<Field, Double> everyone(double each)` |  |
| 1200 | 5 | `static EnumMap<Field, Integer> everyLab(int each)` |  |
| 1207 | 18 | `static String against(Research r, int variant, long seed, EnumMap<Field, Double> staff)` | Runs a Research against the reference over all history; the first disagreement, or null. |
| 1226 | 35 | `static void sectionTen()` |  |

### R4 SLICE B: THE LABS IN THE CITY (sections 11 to 17). A village founded the 1900 way (or the default way) with a Univ... (lines 1262-1353)

| line | len | member | says |
|---:|---:|---|---|
| 1278 | 3 | `static void hand(Game g)` | Hands handed to the village (MaterialsCheck.handWorkers()), which last one month. |
| 1283 | 6 | `static void playHanded(Game g, int months)` | `months` presses of the clock, the hands handed afresh before each. |
| 1291 | 6 | `static int monthWantingALab(Field f, int labs)` | The first month in which a fresh Research with a prize no problem escapes wants `labs` labs in a field (Research.labsWanted() at the wages), or -1 for none in all of history. |
| 1299 | 5 | `static Game labCity(String label, boolean the1900Way, Field f, int labs)` | A village with a University and the hands, played to the month before `f` first wants `labs` labs. |
| 1306 | 3 | `static double coverNow(Game g, Entry e)` | What one more researcher-year of a problem costs the investors' reckoning in the city: the problem's remaining work times twelve times the post's cost at the city's wages now (Research's own statics). |
| 1314 | 16 | `static double justWantsALab(Game g, Field f)` | The least prize at which Research wants a lab in the field in the city's next month: the least of the open problems' covers (coverNow()) at which Research.labsWanted() is one or more. |
| 1332 | 7 | `static void pileJustUnder(Game g, Entry e)` | DiscoveryCheck.pileJustUnder() with the prizes, the pots, the labs' piles, the shares and the idle counts kept: it restores the research from its grants, posts, piles and finds alone, which clears them. |
| 1341 | 5 | `static String labsOf(Game g)` | The labs of every field standing and on site, as one string: "ENGINEERING 1+0 MEDICINE 0+0 ...". |
| 1348 | 5 | `static int labsInAll(Game g)` | The labs the city has in all fields, standing and on site. |

### 11. the labs are buildings (lines 1354-1446)

| line | len | member | says |
|---:|---:|---|---|
| 1356 | 90 | `static void sectionEleven()` |  |

### 12. investors build a lab (lines 1447-1506)

| line | len | member | says |
|---:|---:|---|---|
| 1449 | 57 | `static void sectionTwelve()` |  |

### 13. labs at work (lines 1507-1541)

| line | len | member | says |
|---:|---:|---|---|
| 1509 | 32 | `static void sectionThirteen()` |  |

### 14. the purse paid (lines 1542-1615)

| line | len | member | says |
|---:|---:|---|---|
| 1545 | 5 | **type** `static final class Found` | A city with a lab at work, a prize and a find just made: the city, the problem found, and what the pot held before. |
| 1552 | 17 | `static Found findCity(String label, double prize)` | A village at month 80 with a lab standing and a prize (`prize`, or the one that just wants a lab if 0), a month played with the lab at work, then the target's pile put just under its size and the month of the find pla... |
| 1570 | 45 | `static void sectionFourteen()` |  |

### 15. idle labs are shed (lines 1616-1649)

| line | len | member | says |
|---:|---:|---|---|
| 1618 | 31 | `static void sectionFifteen()` |  |

### 16. the save (lines 1650-1721)

| line | len | member | says |
|---:|---:|---|---|
| 1652 | 69 | `static void sectionSixteen() throws Exception` |  |

### 17. a city founded the default way; a reform of the money (lines 1722-1759)

| line | len | member | says |
|---:|---:|---|---|
| 1724 | 35 | `static void sectionSeventeen()` |  |

