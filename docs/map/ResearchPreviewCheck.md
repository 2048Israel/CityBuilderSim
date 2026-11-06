# ResearchPreviewCheck.java - 726 lines · 34 methods · 3 constants · harnesses

`ham/citybuildersim/ResearchPreviewCheck.java` - generated 2026-11-06 by CodeMap; line numbers are as of that run.

> The preview each dial card on Policy > Research shows: what a set of dials would find here over ten years (R3b, slice A: ResearchPreview, and the
> words and the cache in ResearchView; docs/studies/research-design.md 5.4, 6.2, 9).
> 
> WHY. The preview is the one place the game says a number about the FUTURE of a random process, and its numbers are the player's reason to move a
> dial. A step that differs from the game's own by one rule (the labs read the city's share of this month and not the last, a find not counting at once
> for the fields after it) would give a card that promises what the month will not deliver, and no other assertion in the model would see it; and a
> sampler that leaked the city's own draw would show the player a size he is not to see. So: the two cities of research-design 5.4 are held to the
> study's figures, which research_model.py made with the study's own model, within their Monte Carlo error; the preview's step is held to
> Research.month() itself, month for month, over given sizes; the answer is the same for the same inputs and is never run twice for them; and the
> words are caused from both sides of each edge.
> 
> What this has to prove:
> 
>   1. THE POSTS' COST. The wages chosen for the study's two cities give the study's post costs.
>   2. THE YOUNG CITY, June 1955. Each figure of 5.4's table and the study's run (research_model.py forward(), figures.py), game against study within
>      three standard errors of the two, the chase of the prize and the labs it brings.
>   3. THE BIG CITY, June 1979. The same, with the finds the city made, the grants staged in the four ways and the medical prize.
>  3b. THE STUDY'S MODEL, WRITTEN AGAIN. research_model.py forward() ported line for line (dicts of months and piles, the study's own effort, caps and windows,
>      its 40 discoveries), run for the as-is big city, the same with its dials staged, and the young city with a medical grant: the preview agrees with it to four times the Monte Carlo error of the
>      two (four and not three, for twelve comparisons from one fixed seed: a 3-sigma bound is crossed by chance in one run of a few hundred). A tight test
>      where the study's own 1,000 draws are a loose one: each of its figures is a single sample (its medicine ones run 2 or 3 errors under the limit they
>      converge to, the draws being shared between its scenarios), and the 1,000,000-draw preview and a 400,000-draw port agree to 0.001 of a find.
>   4. THE STEP. With the sizes the city's own draw gives (Research.hiddenSize()), the preview's single path finds what Research.month() finds, in the
>      same months, from both cities' states, without a prize and with one (the labs given to month() as labsWanted() would want them).
>   5. THE DRAW. A size is always above the pile on it; the tail sampler draws the distribution plain redrawing does; the mean size is the work.
>   6. REPRODUCIBLE AND CACHED. The same inputs give the same Result; another seed another one; ResearchView's cache returns without running again, and
>      a new month, a changed dial or a changed pile runs afresh.
>   7. THE ANSWER'S SHAPE. The purse is the prize times the finds, the chance is a share, the fields add to the total, nothing to find gives nothing.
>   8. THE WORDS. A chance when the larger of the two expected counts is under 1, else the count to two places; "none", "under 1%" and "under 0.01"
>      on their edges, both sides.
>   9. THE COST. What 400 draws take in the big city (printed).
> 
> The cities are built as the study's state restores them: a Research restored to the piles and the finds, the city's share of each problem the water fill
> of the as-is researchers over the open problems (the share the last month() would have left; Research.waterFill() is the study's), the wages chosen
> so that a post costs what the study's does. The study's game months are the study's plus 600 (the game's epoch is 1900, the study's 1950).

**Uses:** [ResearchPreview](ResearchPreview.md) (74), [ResearchView](ResearchView.md) (48), [Research](Research.md) (37), [JobType](JobType.md) (11), [Dials](Dials.md) (5), [PolicyDates](PolicyDates.md) (2), [ResearchCatalog](ResearchCatalog.md) (1)

**Used by (1):** [ResearchViewCheck](ResearchViewCheck.md)

## Sections

| line | section |
|---:|---|
| 78 | THE FIXTURES: the study's two cities as a Research |
| 174 | main |
| 237 | 1. the posts' cost |
| 248 | 2. the young city |
| 302 | 3. the big city |
| 357 | 3b. the study's model, written again |
| 464 | 4. the step |
| 536 | 5. the draw |
| 586 | 6. reproducible and cached |
| 637 | 7. the answer's shape |
| 678 | 8. the words |
| 703 | 9. the cost |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 71 | `ResearchPreviewCheck.DRAWS` | `20_000` | The draws the harness makes the game's preview from (the screen uses ResearchPreview.DRAWS): enough that the game's error is a quarter of the study's. |
| 74 | `ResearchPreviewCheck.STUDY_DRAWS` | `1000` | The draws the study's figures were made from, for the binomial error of a chance it quotes without one. |
| 76 | `ResearchPreviewCheck.ENG` | `Field.ENGINEERING, MED = Field.MEDICINE, BUS = Field.BUSINESS, LAW = Field.LAW` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 52 | `static int fails` |  |
| 53 | `static int passes` |  |
| 467 | `static int stepFinds, stepMonths` | The number of finds and of months compared, for the printout. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 50 | 677 | **type** `public class ResearchPreviewCheck` | The preview each dial card on Policy > Research shows: what a set of dials would find here over ten years (R3b, slice A: ResearchPreview, and the words and the cache in ResearchView; docs/studies/research-design.md 5.... |
| 55 | 4 | `static void check(String label, boolean ok)` |  |
| 61 | 4 | `static void check(String label, boolean ok, String found)` | The same, with what was found printed under a failure. |
| 66 | 3 | `static void near(String label, double actual, double expected, double tolerance)` |  |

### THE FIXTURES: the study's two cities as a Research (lines 78-173)

| line | len | member | says |
|---:|---:|---|---|
| 83 | 13 | `static double[] studyWages()` | The wages (D$ a month) that make a post cost what the study's does (research_model.py WAGE: COLLEGE 5.5k, PROF 8.2k, SENIOR 9.6k, ELITE 15.6k, SKILLED 4.5k). |
| 97 | 8 | `static EnumMap<Field, Double> byField(double eng, double med, double bus, double law)` |  |
| 106 | 3 | `static EnumMap<Field, Double> noPrize()` |  |
| 111 | 20 | **type** `record City(String name, int month, Map<String, Double> piles, Map<String, Integer> found, EnumMap<Field, D...` | A city's state: the month, what is piled and found, and the researchers it has as it is. |
| 114 | 16 | `Research research(EnumMap<Field, Double> prize)` _(in ResearchPreviewCheck.City)_ | A Research restored to the state, with the prizes given, and the city's share of each problem the water fill of the as-is researchers over the open problems. |
| 133 | 12 | `static City young()` | The young city of research-design 5.4: June 1955 (game month 666), one University since 1953, nothing found here, the study's piles. |
| 147 | 14 | `static City big()` | The big city of 5.4: June 1979 (game month 954), four finds here, the study's piles. |
| 163 | 6 | `static Research copyOf(Research r)` | A copy of a research with everything the preview and the month read: grants, posts, piles, finds, prizes, pots, the labs' piles and the city's shares. |
| 170 | 3 | `static ResearchPreview.Result run(City city, EnumMap<Field, Double> people, EnumMap<Field, Double> prize)` |  |

### main (lines 174-236)

| line | len | member | says |
|---:|---:|---|---|
| 178 | 14 | `public static void main(String[] args)` |  |
| 194 | 3 | `static void against(String label, double game, double gameSe, double study, double studySe, double printedUnit)` | One figure of the study against the game's: within three standard errors of the two (and half a unit of the study's last printed place). |
| 199 | 6 | `static void against(String label, double game, double gameSe, double study, double studySe, double printedUnit, int sigmas)` | The same within `sigmas` standard errors of the two. |
| 207 | 3 | `static double binomial(double p, int draws)` | The binomial standard error of a share. |
| 211 | 3 | `static void finds(String label, ResearchPreview.Result game, Field f, double study, double studySe)` |  |
| 215 | 3 | `static void chance(String label, ResearchPreview.Result game, Field f, double study)` |  |
| 219 | 3 | `static void all(String label, ResearchPreview.Result game, double study, double studySe)` |  |
| 224 | 5 | `static List<String> codes(List<Entry> entries)` | The codes of a list of entries. |
| 231 | 5 | `static double chaseRoom(Research r, Field f, int month, double[] wages)` | The room a field's chase leaves the labs: what each problem on it can still take of its team cap, less the city's share (what Research.labsWanted() sums). |

### 1. the posts' cost (lines 237-247)

| line | len | member | says |
|---:|---:|---|---|
| 239 | 8 | `static void sectionOne()` |  |

### 2. the young city (lines 248-301)

| line | len | member | says |
|---:|---:|---|---|
| 250 | 46 | `static void sectionTwo()` |  |
| 298 | 3 | `static double w2(Field f)` | The cost of a post of the field at the study's wages. |

### 3. the big city (lines 302-356)

| line | len | member | says |
|---:|---:|---|---|
| 304 | 52 | `static void sectionThree()` |  |

### 3b. the study's model, written again (lines 357-463)

| line | len | member | says |
|---:|---:|---|---|
| 365 | 77 | `static double[][] studyForward(City city, EnumMap<Field, Double> people, int draws, long seed)` | research_model.py forward() as the study wrote it (no prize), on the study's 40 discoveries: the 1951-1995 catalogue, every other of the game's (before 1950 and L5, the 1900 study's) being known, or out of the ten yea... |
| 443 | 20 | `static void sectionThreeB()` |  |

### 4. the step (lines 464-535)

| line | len | member | says |
|---:|---:|---|---|
| 473 | 35 | `static String partsAt(City city, EnumMap<Field, Double> people, EnumMap<Field, Double> prize, long seed)` | Plays a research the way the game does for the preview's ten years, with the sizes of one city's seed: Research.month() each month with the researchers held, the labs wanted as Research.labsWanted() would ask them at ... |
| 509 | 14 | `static void stepCase(String label, City city, EnumMap<Field, Double> people, EnumMap<Field, Double> prize, int seeds)` |  |
| 524 | 11 | `static void sectionFour()` |  |

### 5. the draw (lines 536-585)

| line | len | member | says |
|---:|---:|---|---|
| 538 | 47 | `static void sectionFive()` |  |

### 6. reproducible and cached (lines 586-636)

| line | len | member | says |
|---:|---:|---|---|
| 588 | 48 | `static void sectionSix()` |  |

### 7. the answer's shape (lines 637-677)

| line | len | member | says |
|---:|---:|---|---|
| 639 | 38 | `static void sectionSeven()` |  |

### 8. the words (lines 678-702)

| line | len | member | says |
|---:|---:|---|---|
| 680 | 22 | `static void sectionEight()` |  |

### 9. the cost (lines 703-726)

| line | len | member | says |
|---:|---:|---|---|
| 705 | 21 | `static void sectionNine()` |  |

