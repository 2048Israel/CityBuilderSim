# PrizeCheck.java - 2,008 lines · 75 methods · 10 constants · harnesses

`ham/citybuildersim/PrizeCheck.java` - generated 2026-11-20 by CodeMap; line numbers are as of that run.

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
>  12. INVESTORS BUILD A LAB. At the prize that just makes the model want one a lab is on site within a month, the owners' raise recorded, at most the
>      order's own quote (Game.getLabQuote(), RT2-P), and no loan;
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
> ... (4 more lines in the source)

**Uses:** [Research](Research.md) (274), [Game](Game.md) (52), [DiscoveryCheck](DiscoveryCheck.md) (31), [BuildingsTemplate](BuildingsTemplate.md) (14), [TreasuryLine](TreasuryLine.md) (10), [JobType](JobType.md) (6), [Inbox](Inbox.md) (6), [Equity](Equity.md) (5), [MoneyAudit](MoneyAudit.md) (5), [BusinessInvestment](BusinessInvestment.md) (3), [Sector](Sector.md) (3), [SectorBooks](SectorBooks.md) (3), [GameFiles](GameFiles.md) (3), [EquipmentCheck](EquipmentCheck.md) (3), [Exchange](Exchange.md) (3), [ResearchCatalog](ResearchCatalog.md) (2), [WageBand](WageBand.md) (2), [WageLadder](WageLadder.md) (2), [BuildingManager](BuildingManager.md) (2), [BuildingCatalog](BuildingCatalog.md) (2), [BusinessServices](BusinessServices.md) (2), [Observation](Observation.md) (2), [Household](Household.md) (2), [EconomyManager](EconomyManager.md) (2), [PayTier](PayTier.md) (1), [Sectors](Sectors.md) (1), [MaterialsCheck](MaterialsCheck.md) (1), [PopulationManager](PopulationManager.md) (1), [BuildingType](BuildingType.md) (1), [HouseholdBalance](HouseholdBalance.md) (1)... and 1 more

**Used by (1):** [ResearchViewCheck](ResearchViewCheck.md)

## Sections

| line | section |
|---:|---|
| 110 | · the fixtures: the shipped catalogue, the game's wages |
| 291 | 1. the dials |
| 339 | 2. the purse's rule |
| 395 | 3. the chase |
| 529 | 4. labs on the target |
| 626 | 5. the purse |
| 758 | 6. the pot buys posts |
| 832 | 7. investors |
| 939 | 8. idle labs |
| 1038 | 9. the save |
| 1156 | 10. no prize, no change |
| 1276 | R4 SLICE B: THE LABS IN THE CITY (sections 11 to 17). A village founded the 1900 way (or the default way) with a Univ... |
| 1437 | 11. the labs are buildings |
| 1530 | 12. investors build a lab |
| 1624 | 13. labs at work |
| 1659 | 14. the purse paid |
| 1739 | 15. idle labs are shed |
| 1773 | 16. the save |
| 1846 | 17. a city founded the default way; a reform of the money |
| 1884 | 18. the month's counts are saved (R3b) |
| 1935 | 19. a month the books do not close (R3b) |
| 1965 | 20. the stake stays in the till (R3b) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 112 | `PrizeCheck.CAT` | `ResearchCatalog.get()` |  |
| 113 | `PrizeCheck.SEED` | `20261010L` |  |
| 116 | `PrizeCheck.BIG` | `1e12` | A prize that covers any problem there is: the chase is then ruled by the world's date alone. |
| 125 | `PrizeCheck.W` | `wages(1)` |  |
| 134 | `PrizeCheck.FULL` | `fill(1)` |  |
| 1284 | `PrizeCheck.F` | `Field.ENGINEERING` | The field the city sections work in: the University hosts it, and the first lab any field wants is wanted in it. |
| 1286 | `PrizeCheck.BS` | `Sectors.BUSINESS_SERVICES` |  |
| 1289 | `PrizeCheck.STUDY_RATIO` | `12 / 10.9` | research-design 5.3: the lab's D$ 12M against the Engineering Services Office's D$ 10.9M for 120 staff, which the lab templates' figures are written from. |
| 1305 | `PrizeCheck.LAB_COLLEGE_HEADS_MORE` | `200` | The least college heads handed besides hand()'s 2,000 (MaterialsCheck.handWorkers()) to the village that orders a medical lab (section 12), where the gate's year of arrivals leaves its college posts out-running them. |
| 1360 | `PrizeCheck.FUND_BUYS_SHARE_OF_CAP` | `.1` | The share of Business Services' market value the city's fund buys at its price, to give the village's shares a current quote (labCityWithAMarket()). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 92 | `static int fails` |  |
| 93 | `static int passes` |  |
| 178 | `final Research r` |  |
| 179 | `final long seed` |  |
| 180 | `final EnumMap<Field, Double> city` |  |
| 181 | `final EnumMap<Field, Long> labs` |  |
| 182 | `double[] wages` |  |
| 183 | `double drawn` |  |
| 185 | `Map<String, Double> pilesBefore` | what stood before the month's work: the piles, the labs' part, the pots, the chase of each field |
| 186 | `final EnumMap<Field, Double> potBefore` |  |
| 187 | `final EnumMap<Field, List<Entry>> chaseBefore` |  |
| 1160 | `final Map<String, Double> piles` |  |
| 1161 | `final Map<String, Integer> found` |  |
| 1162 | `final List<String> foundNow` |  |
| 1163 | `final EnumMap<Field, Double> idle` |  |
| 1164 | `final EnumMap<Field, Integer> open` |  |
| 1663 | `Game g` |  |
| 1664 | `Entry target` |  |
| 1665 | `double prize, potBefore` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 90 | 1919 | **type** `public class PrizeCheck` | A prize pays only for a result, a Research Lab goes for the cheapest problem the purse pays for, and the schools' share of a purse buys posts (R4, slice A: Research; docs/studies/research-design.md 5.3, 1.5, 5.4). |
| 95 | 4 | `static void check(String label, boolean ok)` |  |
| 101 | 4 | `static void check(String label, boolean ok, String found)` | The same, with what was found printed under a failure. |
| 106 | 3 | `static void near(String label, double actual, double expected, double tolerance)` |  |

### the fixtures: the shipped catalogue, the game's wages (lines 110-290)

| line | len | member | says |
|---:|---:|---|---|
| 119 | 5 | `static double[] wages(double times)` | The wages by JobType ordinal, the game's (PayTier.getMonthlyWage()), times `times`. |
| 128 | 5 | `static double[] fill(double share)` | Every job type filled to `share`. |
| 136 | 1 | `static Entry of(String code)` |  |
| 138 | 5 | `static int lastWorldMonth()` |  |
| 144 | 1 | `static double cost(Field f)` |  |
| 147 | 3 | `static double cover(Entry e, double pile)` | The prize that just covers a problem with that pile: its remaining work times twelve times the post's cost, by Research's own statics. |
| 152 | 3 | `static double prizeFor(Field f, double work)` | The prize that covers a problem of that much typical work, untouched. |
| 156 | 5 | `static EnumMap<Field, Double> map(Field f, double v)` |  |
| 162 | 5 | `static EnumMap<Field, Long> counts(Field f, long n)` |  |
| 168 | 1 | `static int indexOf(Entry e)` |  |
| 170 | 5 | `static List<String> codes(List<Entry> list)` |  |
| 177 | 36 | **type** `static final class Sim` | One city's research month by month, in the game's order: the posts are struck, the pots pay their bill, then the month's work is done. |
| 189 | 1 | `Sim(long seed)` _(in PrizeCheck.Sim)_ |  |
| 191 | 1 | `Sim city(Field f, double n)` _(in PrizeCheck.Sim)_ |  |
| 193 | 1 | `Sim labs(Field f, long n, double people)` _(in PrizeCheck.Sim)_ |  |
| 195 | 1 | `Sim room(Field f, double n)` _(in PrizeCheck.Sim)_ |  |
| 197 | 11 | `void step(int m)` _(in PrizeCheck.Sim)_ |  |
| 209 | 3 | `void run(int from, int to)` _(in PrizeCheck.Sim)_ |  |
| 215 | 10 | `static Sim standard(long seed)` | Engineering and medicine with labs, grants, prizes and posts to buy: the city the save and the invariants are run on. |
| 227 | 6 | `static Sim potted()` | A city that has made one find with a prize and no lab: its schools' pot is full. |
| 235 | 29 | `static List<String> invariants(Sim s, int m)` | The invariants of a month's step, found broken; empty when none. |
| 265 | 25 | `public static void main(String[] args) throws Exception` |  |

### 1. the dials (lines 291-338)

| line | len | member | says |
|---:|---:|---|---|
| 293 | 45 | `static void sectionOne()` |  |

### 2. the purse's rule (lines 339-394)

| line | len | member | says |
|---:|---:|---|---|
| 341 | 4 | `static Entry firstOfSize(Size size)` |  |
| 346 | 48 | `static void sectionTwo()` |  |

### 3. the chase (lines 395-528)

| line | len | member | says |
|---:|---:|---|---|
| 398 | 1 | **type** `record Tie(Field field, int month, Entry a, Entry b, List<Entry> list)` | Two problems of one field with the same remaining work on the chase together, and the month. |
| 400 | 13 | `static Tie findTie()` |  |
| 414 | 114 | `static void sectionThree()` |  |

### 4. labs on the target (lines 529-625)

| line | len | member | says |
|---:|---:|---|---|
| 532 | 1 | **type** `record Spot(Field field, int month, List<Entry> chase, List<Entry> open)` | A field and a month where three or more problems are on the chase and one more is open and is not. |
| 535 | 13 | `static Spot findSpot(int labs)` | The first such month in which the chase's team caps whole have room for at least `labs` labs (labsFor()). |
| 550 | 6 | `static Sim spotSim(Spot s, double cityPeople, double labPeople)` | A sim at a spot: a city of researchers spread over what is open, and `labPeople` lab researchers, one month. |
| 557 | 1 | `static double room(Research r, Entry e)` |  |
| 559 | 66 | `static void sectionFour()` |  |

### 5. the purse (lines 626-757)

| line | len | member | says |
|---:|---:|---|---|
| 628 | 129 | `static void sectionFive()` |  |

### 6. the pot buys posts (lines 758-831)

| line | len | member | says |
|---:|---:|---|---|
| 760 | 71 | `static void sectionSix()` |  |

### 7. investors (lines 832-938)

| line | len | member | says |
|---:|---:|---|---|
| 834 | 104 | `static void sectionSeven()` |  |

### 8. idle labs (lines 939-1037)

| line | len | member | says |
|---:|---:|---|---|
| 942 | 5 | `static double roomNow(Research r, Field f, int m)` | The researchers the chase could take in a field at a month with no city researchers: the team caps of its problems, whole. |
| 948 | 82 | `static void sectionEight()` |  |
| 1032 | 5 | `static boolean sheddingOne(Research r, Field shed, Field kept)` | Sheds a lab of the field and says whether it alone was cleared. |

### 9. the save (lines 1038-1155)

| line | len | member | says |
|---:|---:|---|---|
| 1041 | 9 | `static String kept(Research r, int month)` | What a save keeps, as text: compared between the unbroken run and the restored one. |
| 1052 | 9 | `static String monthly(Research r)` | What the month did, as text. |
| 1062 | 93 | `static void sectionNine()` |  |

### 10. no prize, no change (lines 1156-1275)

| line | len | member | says |
|---:|---:|---|---|
| 1159 | 48 | **type** `static final class Reference` | The study's R3 month, written again here from nothing but Research's statics: what the city's own researchers do whatever labs and prizes there are. |
| 1166 | 1 | `boolean known(String code, int m)` _(in PrizeCheck.Reference)_ |  |
| 1168 | 5 | `boolean isOpen(Entry e, int m)` _(in PrizeCheck.Reference)_ |  |
| 1174 | 32 | `void month(int m, long seed, EnumMap<Field, Double> researchers)` _(in PrizeCheck.Reference)_ |  |
| 1208 | 5 | `static EnumMap<Field, Double> everyone(double each)` |  |
| 1214 | 5 | `static EnumMap<Field, Long> everyLab(long each)` |  |
| 1221 | 18 | `static String against(Research r, int variant, long seed, EnumMap<Field, Double> staff)` | Runs a Research against the reference over all history; the first disagreement, or null. |
| 1240 | 35 | `static void sectionTen()` |  |

### R4 SLICE B: THE LABS IN THE CITY (sections 11 to 17). A village founded the 1900 way (or the default way) with a Univ... (lines 1276-1436)

| line | len | member | says |
|---:|---:|---|---|
| 1292 | 3 | `static void hand(Game g)` | Hands handed to the village (MaterialsCheck.handWorkers()), which last one month. |
| 1297 | 6 | `static void playHanded(Game g, int months)` | `months` presses of the clock, the hands handed afresh before each. |
| 1308 | 17 | `static void handMore(Game g)` | hand(), at least LAB_COLLEGE_HEADS_MORE college heads besides, and the model-quoted deficit for two medical labs: all last one month. |
| 1327 | 6 | `static void playHandedMore(Game g, int months)` | `months` presses of the clock, the hands and the college heads besides handed afresh before each. |
| 1335 | 6 | `static int monthWantingALab(Field f, int labs)` | The first month in which a fresh Research with a prize no problem escapes wants `labs` labs in a field (Research.labsWanted() at the wages), or -1 for none in all of history. |
| 1343 | 3 | `static Game labCity(String label, boolean the1900Way, Field f, int labs)` | A village with a University and the hands, played to the month before `f` first wants `labs` labs. |
| 1352 | 6 | `static Game labCity(String label, boolean the1900Way, Field f, int labs, boolean todaysLadder)` | The same, on today's wage ladder or on the founding's (R0d ★7). |
| 1375 | 12 | `static Game labCityWithAMarket(String label, boolean the1900Way, Field f, int labs)` | A village played as labCity() does, with a market for its Business Services shares at the lab's month: the owners' order for a lab is an issue of shares, which the Exchange allows only while the quote is within BUYBAC... |
| 1389 | 3 | `static double coverNow(Game g, Entry e)` | What one more researcher-year of a problem costs the investors' reckoning in the city: the problem's remaining work times twelve times the post's cost at the city's wages now (Research's own statics). |
| 1397 | 16 | `static double justWantsALab(Game g, Field f)` | The least prize at which Research wants a lab in the field in the city's next month: the least of the open problems' covers (coverNow()) at which Research.labsWanted() is one or more. |
| 1415 | 7 | `static void pileJustUnder(Game g, Entry e)` | DiscoveryCheck.pileJustUnder() with the prizes, the pots, the labs' piles, the shares and the idle counts kept: it restores the research from its grants, posts, piles and finds alone, which clears them. |
| 1424 | 5 | `static String labsOf(Game g)` | The labs of every field standing and on site, as one string: "ENGINEERING 1+0 MEDICINE 0+0 ...". |
| 1431 | 5 | `static long labsInAll(Game g)` | The labs the city has in all fields, standing and on site. |

### 11. the labs are buildings (lines 1437-1529)

| line | len | member | says |
|---:|---:|---|---|
| 1439 | 90 | `static void sectionEleven()` |  |

### 12. investors build a lab (lines 1530-1623)

| line | len | member | says |
|---:|---:|---|---|
| 1532 | 91 | `static void sectionTwelve()` |  |

### 13. labs at work (lines 1624-1658)

| line | len | member | says |
|---:|---:|---|---|
| 1626 | 32 | `static void sectionThirteen()` |  |

### 14. the purse paid (lines 1659-1738)

| line | len | member | says |
|---:|---:|---|---|
| 1662 | 5 | **type** `static final class Found` | A city with a lab at work, a prize and a find just made: the city, the problem found, and what the pot held before. |
| 1669 | 3 | `static Found findCity(String label, double prize)` | A village at month 80 with a lab standing and a prize (`prize`, or the one that just wants a lab if 0), a month played with the lab at work, then the target's pile put just under its size and the month of the find pla... |
| 1674 | 18 | `static Found findCity(String label, double prize, java.util.function.Consumer<Game> beforeTheFind)` | ...and with `beforeTheFind` run on the city just before the month of the find is played (R3b: a month whose books do not come to a finite figure), or none. |
| 1693 | 45 | `static void sectionFourteen()` |  |

### 15. idle labs are shed (lines 1739-1772)

| line | len | member | says |
|---:|---:|---|---|
| 1741 | 31 | `static void sectionFifteen()` |  |

### 16. the save (lines 1773-1845)

| line | len | member | says |
|---:|---:|---|---|
| 1775 | 70 | `static void sectionSixteen() throws Exception` |  |

### 17. a city founded the default way; a reform of the money (lines 1846-1883)

| line | len | member | says |
|---:|---:|---|---|
| 1848 | 35 | `static void sectionSeventeen()` |  |

### 18. the month's counts are saved (R3b) (lines 1884-1934)

| line | len | member | says |
|---:|---:|---|---|
| 1887 | 8 | `static String countsOf(Research r)` | The five counts of the month, field by field, as one string (full precision): researchers, idle, open, the labs' researchers, the labs' idle. |
| 1896 | 38 | `static void sectionEighteen() throws Exception` |  |

### 19. a month the books do not close (R3b) (lines 1935-1964)

| line | len | member | says |
|---:|---:|---|---|
| 1937 | 27 | `static void sectionNineteen()` |  |

### 20. the stake stays in the till (R3b) (lines 1965-2008)

| line | len | member | says |
|---:|---:|---|---|
| 1967 | 41 | `static void sectionTwenty()` |  |

