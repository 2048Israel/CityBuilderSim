# PrizeCheck.java - 1,912 lines · 72 methods · 8 constants · harnesses

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
> 
> R3b (the gate reviews of R3 and R4), the same villages:
> 
>  18. THE MONTH'S COUNTS ARE SAVED. The researchers, idle researchers and open problems of each field and the labs' researchers and idle ones come
>      back from a save as the saved month had them, not 0 until the next; a save without them reads them empty.
>  19. A MONTH THE BOOKS DO NOT CLOSE. When the month's cash update is blocked (a figure that is not finite) a find's purse is owed to Business Services
> ... (3 more lines in the source)

**Uses:** [Research](Research.md) (274), [Game](Game.md) (45), [DiscoveryCheck](DiscoveryCheck.md) (29), [BuildingsTemplate](BuildingsTemplate.md) (14), [TreasuryLine](TreasuryLine.md) (10), [JobType](JobType.md) (6), [Inbox](Inbox.md) (6), [MoneyAudit](MoneyAudit.md) (5), [Equity](Equity.md) (4), [BusinessInvestment](BusinessInvestment.md) (3), [SectorBooks](SectorBooks.md) (3), [GameFiles](GameFiles.md) (3), [EquipmentCheck](EquipmentCheck.md) (3), [Exchange](Exchange.md) (3), [ResearchCatalog](ResearchCatalog.md) (2), [BuildingManager](BuildingManager.md) (2), [BuildingCatalog](BuildingCatalog.md) (2), [BusinessServices](BusinessServices.md) (2), [EconomyManager](EconomyManager.md) (2), [PayTier](PayTier.md) (1), [Sectors](Sectors.md) (1), [MaterialsCheck](MaterialsCheck.md) (1), [WageLadder](WageLadder.md) (1), [BuildingType](BuildingType.md) (1), [Sector](Sector.md) (1), [Notice](Notice.md) (1)

**Used by (1):** [ResearchViewCheck](ResearchViewCheck.md)

## Sections

| line | section |
|---:|---|
| 109 | · the fixtures: the shipped catalogue, the game's wages |
| 290 | 1. the dials |
| 338 | 2. the purse's rule |
| 394 | 3. the chase |
| 528 | 4. labs on the target |
| 625 | 5. the purse |
| 757 | 6. the pot buys posts |
| 831 | 7. investors |
| 938 | 8. idle labs |
| 1037 | 9. the save |
| 1155 | 10. no prize, no change |
| 1275 | R4 SLICE B: THE LABS IN THE CITY (sections 11 to 17). A village founded the 1900 way (or the default way) with a Univ... |
| 1377 | 11. the labs are buildings |
| 1470 | 12. investors build a lab |
| 1530 | 13. labs at work |
| 1565 | 14. the purse paid |
| 1645 | 15. idle labs are shed |
| 1679 | 16. the save |
| 1752 | 17. a city founded the default way; a reform of the money |
| 1790 | 18. the month's counts are saved (R3b) |
| 1841 | 19. a month the books do not close (R3b) |
| 1871 | 20. the stake stays in the till (R3b) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 111 | `PrizeCheck.CAT` | `ResearchCatalog.get()` |  |
| 112 | `PrizeCheck.SEED` | `20261010L` |  |
| 115 | `PrizeCheck.BIG` | `1e12` | A prize that covers any problem there is: the chase is then ruled by the world's date alone. |
| 124 | `PrizeCheck.W` | `wages(1)` |  |
| 133 | `PrizeCheck.FULL` | `fill(1)` |  |
| 1283 | `PrizeCheck.F` | `Field.ENGINEERING` | The field the city sections work in: the University hosts it, and the first lab any field wants is wanted in it. |
| 1285 | `PrizeCheck.BS` | `Sectors.BUSINESS_SERVICES` |  |
| 1288 | `PrizeCheck.STUDY_RATIO` | `12 / 10.9` | research-design 5.3: the lab's D$ 12M against the Engineering Services Office's D$ 10.9M for 120 staff, which the lab templates' figures are written from. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 91 | `static int fails` |  |
| 92 | `static int passes` |  |
| 177 | `final Research r` |  |
| 178 | `final long seed` |  |
| 179 | `final EnumMap<Field, Double> city` |  |
| 180 | `final EnumMap<Field, Integer> labs` |  |
| 181 | `double[] wages` |  |
| 182 | `double drawn` |  |
| 184 | `Map<String, Double> pilesBefore` | what stood before the month's work: the piles, the labs' part, the pots, the chase of each field |
| 185 | `final EnumMap<Field, Double> potBefore` |  |
| 186 | `final EnumMap<Field, List<Entry>> chaseBefore` |  |
| 1159 | `final Map<String, Double> piles` |  |
| 1160 | `final Map<String, Integer> found` |  |
| 1161 | `final List<String> foundNow` |  |
| 1162 | `final EnumMap<Field, Double> idle` |  |
| 1163 | `final EnumMap<Field, Integer> open` |  |
| 1569 | `Game g` |  |
| 1570 | `Entry target` |  |
| 1571 | `double prize, potBefore` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 89 | 1824 | **type** `public class PrizeCheck` | A prize pays only for a result, a Research Lab goes for the cheapest problem the purse pays for, and the schools' share of a purse buys posts (R4, slice A: Research; docs/studies/research-design.md 5.3, 1.5, 5.4). |
| 94 | 4 | `static void check(String label, boolean ok)` |  |
| 100 | 4 | `static void check(String label, boolean ok, String found)` | The same, with what was found printed under a failure. |
| 105 | 3 | `static void near(String label, double actual, double expected, double tolerance)` |  |

### the fixtures: the shipped catalogue, the game's wages (lines 109-289)

| line | len | member | says |
|---:|---:|---|---|
| 118 | 5 | `static double[] wages(double times)` | The wages by JobType ordinal, the game's (PayTier.getMonthlyWage()), times `times`. |
| 127 | 5 | `static double[] fill(double share)` | Every job type filled to `share`. |
| 135 | 1 | `static Entry of(String code)` |  |
| 137 | 5 | `static int lastWorldMonth()` |  |
| 143 | 1 | `static double cost(Field f)` |  |
| 146 | 3 | `static double cover(Entry e, double pile)` | The prize that just covers a problem with that pile: its remaining work times twelve times the post's cost, by Research's own statics. |
| 151 | 3 | `static double prizeFor(Field f, double work)` | The prize that covers a problem of that much typical work, untouched. |
| 155 | 5 | `static EnumMap<Field, Double> map(Field f, double v)` |  |
| 161 | 5 | `static EnumMap<Field, Integer> counts(Field f, int n)` |  |
| 167 | 1 | `static int indexOf(Entry e)` |  |
| 169 | 5 | `static List<String> codes(List<Entry> list)` |  |
| 176 | 36 | **type** `static final class Sim` | One city's research month by month, in the game's order: the posts are struck, the pots pay their bill, then the month's work is done. |
| 188 | 1 | `Sim(long seed)` _(in PrizeCheck.Sim)_ |  |
| 190 | 1 | `Sim city(Field f, double n)` _(in PrizeCheck.Sim)_ |  |
| 192 | 1 | `Sim labs(Field f, int n, double people)` _(in PrizeCheck.Sim)_ |  |
| 194 | 1 | `Sim room(Field f, double n)` _(in PrizeCheck.Sim)_ |  |
| 196 | 11 | `void step(int m)` _(in PrizeCheck.Sim)_ |  |
| 208 | 3 | `void run(int from, int to)` _(in PrizeCheck.Sim)_ |  |
| 214 | 10 | `static Sim standard(long seed)` | Engineering and medicine with labs, grants, prizes and posts to buy: the city the save and the invariants are run on. |
| 226 | 6 | `static Sim potted()` | A city that has made one find with a prize and no lab: its schools' pot is full. |
| 234 | 29 | `static List<String> invariants(Sim s, int m)` | The invariants of a month's step, found broken; empty when none. |
| 264 | 25 | `public static void main(String[] args) throws Exception` |  |

### 1. the dials (lines 290-337)

| line | len | member | says |
|---:|---:|---|---|
| 292 | 45 | `static void sectionOne()` |  |

### 2. the purse's rule (lines 338-393)

| line | len | member | says |
|---:|---:|---|---|
| 340 | 4 | `static Entry firstOfSize(Size size)` |  |
| 345 | 48 | `static void sectionTwo()` |  |

### 3. the chase (lines 394-527)

| line | len | member | says |
|---:|---:|---|---|
| 397 | 1 | **type** `record Tie(Field field, int month, Entry a, Entry b, List<Entry> list)` | Two problems of one field with the same remaining work on the chase together, and the month. |
| 399 | 13 | `static Tie findTie()` |  |
| 413 | 114 | `static void sectionThree()` |  |

### 4. labs on the target (lines 528-624)

| line | len | member | says |
|---:|---:|---|---|
| 531 | 1 | **type** `record Spot(Field field, int month, List<Entry> chase, List<Entry> open)` | A field and a month where three or more problems are on the chase and one more is open and is not. |
| 534 | 13 | `static Spot findSpot(int labs)` | The first such month in which the chase's team caps whole have room for at least `labs` labs (labsFor()). |
| 549 | 6 | `static Sim spotSim(Spot s, double cityPeople, double labPeople)` | A sim at a spot: a city of researchers spread over what is open, and `labPeople` lab researchers, one month. |
| 556 | 1 | `static double room(Research r, Entry e)` |  |
| 558 | 66 | `static void sectionFour()` |  |

### 5. the purse (lines 625-756)

| line | len | member | says |
|---:|---:|---|---|
| 627 | 129 | `static void sectionFive()` |  |

### 6. the pot buys posts (lines 757-830)

| line | len | member | says |
|---:|---:|---|---|
| 759 | 71 | `static void sectionSix()` |  |

### 7. investors (lines 831-937)

| line | len | member | says |
|---:|---:|---|---|
| 833 | 104 | `static void sectionSeven()` |  |

### 8. idle labs (lines 938-1036)

| line | len | member | says |
|---:|---:|---|---|
| 941 | 5 | `static double roomNow(Research r, Field f, int m)` | The researchers the chase could take in a field at a month with no city researchers: the team caps of its problems, whole. |
| 947 | 82 | `static void sectionEight()` |  |
| 1031 | 5 | `static boolean sheddingOne(Research r, Field shed, Field kept)` | Sheds a lab of the field and says whether it alone was cleared. |

### 9. the save (lines 1037-1154)

| line | len | member | says |
|---:|---:|---|---|
| 1040 | 9 | `static String kept(Research r, int month)` | What a save keeps, as text: compared between the unbroken run and the restored one. |
| 1051 | 9 | `static String monthly(Research r)` | What the month did, as text. |
| 1061 | 93 | `static void sectionNine()` |  |

### 10. no prize, no change (lines 1155-1274)

| line | len | member | says |
|---:|---:|---|---|
| 1158 | 48 | **type** `static final class Reference` | The study's R3 month, written again here from nothing but Research's statics: what the city's own researchers do whatever labs and prizes there are. |
| 1165 | 1 | `boolean known(String code, int m)` _(in PrizeCheck.Reference)_ |  |
| 1167 | 5 | `boolean isOpen(Entry e, int m)` _(in PrizeCheck.Reference)_ |  |
| 1173 | 32 | `void month(int m, long seed, EnumMap<Field, Double> researchers)` _(in PrizeCheck.Reference)_ |  |
| 1207 | 5 | `static EnumMap<Field, Double> everyone(double each)` |  |
| 1213 | 5 | `static EnumMap<Field, Integer> everyLab(int each)` |  |
| 1220 | 18 | `static String against(Research r, int variant, long seed, EnumMap<Field, Double> staff)` | Runs a Research against the reference over all history; the first disagreement, or null. |
| 1239 | 35 | `static void sectionTen()` |  |

### R4 SLICE B: THE LABS IN THE CITY (sections 11 to 17). A village founded the 1900 way (or the default way) with a Univ... (lines 1275-1376)

| line | len | member | says |
|---:|---:|---|---|
| 1291 | 3 | `static void hand(Game g)` | Hands handed to the village (MaterialsCheck.handWorkers()), which last one month. |
| 1296 | 6 | `static void playHanded(Game g, int months)` | `months` presses of the clock, the hands handed afresh before each. |
| 1304 | 6 | `static int monthWantingALab(Field f, int labs)` | The first month in which a fresh Research with a prize no problem escapes wants `labs` labs in a field (Research.labsWanted() at the wages), or -1 for none in all of history. |
| 1312 | 3 | `static Game labCity(String label, boolean the1900Way, Field f, int labs)` | A village with a University and the hands, played to the month before `f` first wants `labs` labs. |
| 1321 | 6 | `static Game labCity(String label, boolean the1900Way, Field f, int labs, boolean todaysLadder)` | The same, on today's wage ladder or on the founding's (R0d ★7). |
| 1329 | 3 | `static double coverNow(Game g, Entry e)` | What one more researcher-year of a problem costs the investors' reckoning in the city: the problem's remaining work times twelve times the post's cost at the city's wages now (Research's own statics). |
| 1337 | 16 | `static double justWantsALab(Game g, Field f)` | The least prize at which Research wants a lab in the field in the city's next month: the least of the open problems' covers (coverNow()) at which Research.labsWanted() is one or more. |
| 1355 | 7 | `static void pileJustUnder(Game g, Entry e)` | DiscoveryCheck.pileJustUnder() with the prizes, the pots, the labs' piles, the shares and the idle counts kept: it restores the research from its grants, posts, piles and finds alone, which clears them. |
| 1364 | 5 | `static String labsOf(Game g)` | The labs of every field standing and on site, as one string: "ENGINEERING 1+0 MEDICINE 0+0 ...". |
| 1371 | 5 | `static int labsInAll(Game g)` | The labs the city has in all fields, standing and on site. |

### 11. the labs are buildings (lines 1377-1469)

| line | len | member | says |
|---:|---:|---|---|
| 1379 | 90 | `static void sectionEleven()` |  |

### 12. investors build a lab (lines 1470-1529)

| line | len | member | says |
|---:|---:|---|---|
| 1472 | 57 | `static void sectionTwelve()` |  |

### 13. labs at work (lines 1530-1564)

| line | len | member | says |
|---:|---:|---|---|
| 1532 | 32 | `static void sectionThirteen()` |  |

### 14. the purse paid (lines 1565-1644)

| line | len | member | says |
|---:|---:|---|---|
| 1568 | 5 | **type** `static final class Found` | A city with a lab at work, a prize and a find just made: the city, the problem found, and what the pot held before. |
| 1575 | 3 | `static Found findCity(String label, double prize)` | A village at month 80 with a lab standing and a prize (`prize`, or the one that just wants a lab if 0), a month played with the lab at work, then the target's pile put just under its size and the month of the find pla... |
| 1580 | 18 | `static Found findCity(String label, double prize, java.util.function.Consumer<Game> beforeTheFind)` | ...and with `beforeTheFind` run on the city just before the month of the find is played (R3b: a month whose books do not come to a finite figure), or none. |
| 1599 | 45 | `static void sectionFourteen()` |  |

### 15. idle labs are shed (lines 1645-1678)

| line | len | member | says |
|---:|---:|---|---|
| 1647 | 31 | `static void sectionFifteen()` |  |

### 16. the save (lines 1679-1751)

| line | len | member | says |
|---:|---:|---|---|
| 1681 | 70 | `static void sectionSixteen() throws Exception` |  |

### 17. a city founded the default way; a reform of the money (lines 1752-1789)

| line | len | member | says |
|---:|---:|---|---|
| 1754 | 35 | `static void sectionSeventeen()` |  |

### 18. the month's counts are saved (R3b) (lines 1790-1840)

| line | len | member | says |
|---:|---:|---|---|
| 1793 | 8 | `static String countsOf(Research r)` | The five counts of the month, field by field, as one string (full precision): researchers, idle, open, the labs' researchers, the labs' idle. |
| 1802 | 38 | `static void sectionEighteen() throws Exception` |  |

### 19. a month the books do not close (R3b) (lines 1841-1870)

| line | len | member | says |
|---:|---:|---|---|
| 1843 | 27 | `static void sectionNineteen()` |  |

### 20. the stake stays in the till (R3b) (lines 1871-1912)

| line | len | member | says |
|---:|---:|---|---|
| 1873 | 39 | `static void sectionTwenty()` |  |

