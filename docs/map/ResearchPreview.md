# ResearchPreview.java - 450 lines · 26 methods · 7 constants · model

`ham/citybuildersim/ResearchPreview.java` - generated 2026-11-06 by CodeMap; line numbers are as of that run.

> What a set of research dials would find in this city over the next YEARS years, from the DISTRIBUTION of the hidden sizes and never from the
> city's own draw: the number each dial card on Policy > Research shows beside the dial (research-design 5.4, 6.2; mockups-research/1-funding.html).
> Pure: no Game, no screen, nothing kept and nothing booked; given a Research as it stands at a month and the dials (the researchers each field would
> have, the prize it would pay), it plays the next ten years DRAWS times and says what the draws found.
> 
> WHY (R3b). The card must say what money buys ("0.2% to 25%", 5.4), but "the player never sees the size" (5.2, 6.2), and the size is the one thing
> the answer turns on: a problem with 3 researcher-years piled and 60 to go is found in a year by 40 people or never by 4. So the preview does not read
> the city's draw (Research.hiddenSize(), which would give the secret away one find at a time as a dial was moved); it draws the sizes afresh, from
> the same lognormal, each problem's draw conditioned on its being above the pile already on it (a problem not found yet cannot be smaller than what
> has been put into it: research_model.py forward()), and plays the month the game plays (Research.month()) over those sizes, so that what it reports
> is the mean of what the game would do over the cities that fit what the player can see. The seed is fixed (SEED) and never the city's, so the same
> inputs give the same answer and a card does not shimmer as the screen is redrawn, and the draws are the same sizes for every set of dials (an
> as-is and a staged run share them), so the DIFFERENCE between the two is not noise.
> 
> It is Research.month() written again on arrays, because the screen runs it for every card at both the dials as they are and the dials staged, and
> the model's own step, which keeps maps and strings, is too slow for that by a factor of a few hundred; ResearchPreviewCheck holds the two to the same
> finds in the same months from the same sizes (path()). Where the step has a rule, this has it: the world's dates first, then each field in the
> enum's order (a find counts at once for the fields after it), its open problems (Research.isOpen()), its researchers spread by Research.waterFill()
> over the team caps, and, where its prize is above 0, the labs: the chase (the problems the purse covers that a team could finish before the world,
> least remaining first), the labs investors want for the room on it (Research.labsFor(), the city's share read from the month before, as
> Research.labsWanted() reads it), their researchers poured on it cheapest first, each problem taking what its team cap leaves of the city's share.
> What it leaves out, because nothing the card says depends on it: the labs' part of a pile apart from the city's (a find splits the purse by it; the
> preview only counts the whole purse as the city's money), a lab that takes months to build (the want is met the month it arises) and the pot.

**Uses:** [Research](Research.md) (18), [ResearchCatalog](ResearchCatalog.md) (1)

**Used by (3):** [ResearchPreviewCheck](ResearchPreviewCheck.md), [ResearchView](ResearchView.md), [ResearchViewCheck](ResearchViewCheck.md)

## Sections

| line | section |
|---:|---|
| 62 | THE ANSWER |
| 123 | THE PREVIEW |
| 149 | THE PLAN: what a research at a month has left to find, on arrays |
| 255 | THE DRAWS |
| 327 | THE PATH: one city's ten years, Research.month() on arrays |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 43 | `ResearchPreview.YEARS` | `10` | The years a preview plays ahead: ten, the study's horizon (research-design 5.4). |
| 46 | `ResearchPreview.DRAWS` | `400` | The draws a card's preview is made from: 400, which puts the standard error of a field's expected finds near 0.04 where it finds about one (the study's runs used 1,000, the harness 20,000). |
| 49 | `ResearchPreview.SEED` | `0x5EED_C0FFEE_1001L` | The seed the draws are made from: fixed, so that the same dials always say the same, and never the city's own (Game.getWorldSeed()), which would show the player the hidden sizes. |
| 52 | `ResearchPreview.RUNS` | `new AtomicInteger()` | The preview's runs since the program began, for a harness to see that a cached answer did not run again. |
| 60 | `ResearchPreview.NEVER` | `Integer.MAX_VALUE` | A month nothing was found in. |
| 153 | `ResearchPreview.FIELDS` | `Field.values().length` |  |
| 156 | `ResearchPreview.NOTHING` | `new Result(DRAWS, new double [ FIELDS ], new double [ FIELDS ], new double [ ...` | The answer where there is nothing to find (a city founded the default way knows every discovery from the start): nothing anywhere, from DRAWS draws. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 72 | `private final int draws` |  |
| 73 | `private final double[] finds, findsSe, chance, purse` |  |
| 74 | `private final int[] labs` |  |
| 75 | `private final double total, totalSe` |  |
| 158 | `private final int month, end` |  |
| 159 | `private final Entry[] entry` |  |
| 161 | `private final int[] start` | The first index of each field's entries (entries are in the enum's field order, the catalogue's within it); a field's are [start[f], start[f + 1]). |
| 162 | `private final double[] cap, work, pile0, share0` |  |
| 163 | `private final int[] worldMonth, openFrom` |  |
| 165 | `private final int[][] needs` | The entries each one needs that are not known at the month, as indices; a need that cannot be known inside the window is `never`. |
| 166 | `private final boolean[] never` |  |
| 167 | `private final double[] people` |  |
| 170 | `private final int[] foundAt` | The state of one path, reused from path to path. |
| 171 | `private final double[] pile` |  |
| 172 | `private double[] share, prev` |  |
| 173 | `private final double[] lab` |  |
| 174 | `private final int[] openIdx, chase` |  |
| 175 | `private final int[] cachedOpenCount` |  |
| 176 | `private final int[][] cachedOpen` |  |
| 177 | `private final double[][] cachedShare` |  |
| 178 | `private final int[] labsFirst` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 411 | **type** `public final class ResearchPreview` | What a set of research dials would find in this city over the next YEARS years, from the DISTRIBUTION of the hidden sizes and never from the city's own draw: the number each dial card on Policy > Research shows beside... |
| 55 | 3 | `static int runs()` | How many previews have been run (preview(), not path()). |

### THE ANSWER (lines 62-122)

| line | len | member | says |
|---:|---:|---|---|
| 71 | 51 | **type** `public static final class Result` | What the draws found, field by field: the mean finds here over the years, its standard error, the chance of at least one, and the purse the finds would pay (the prize times the finds: the whole purse, the labs' part a... |
| 77 | 10 | `private Result(int draws, double[] finds, double[] findsSe, double[] chance, double[] purse, int[] labs, double total, double t...` _(in ResearchPreview.Result)_ |  |
| 89 | 1 | `public int draws()` _(in ResearchPreview.Result)_ | How many draws it was made from. |
| 92 | 1 | `public double finds(Field f)` _(in ResearchPreview.Result)_ | The finds here in the field over the years, the mean of the draws. |
| 95 | 1 | `public double findsSe(Field f)` _(in ResearchPreview.Result)_ | The standard error of finds(): the draws' spread over the root of their number. |
| 98 | 1 | `public double chance(Field f)` _(in ResearchPreview.Result)_ | The share of the draws in which the field found at least one, 0 to 1. |
| 101 | 1 | `public double purse(Field f)` _(in ResearchPreview.Result)_ | The purse the field's finds would pay over the years: its prize times finds(), in the game's money unit. |
| 104 | 1 | `public double total()` _(in ResearchPreview.Result)_ | The finds of all the fields together, the mean of the draws. |
| 107 | 1 | `public double totalSe()` _(in ResearchPreview.Result)_ | The standard error of total(). |
| 110 | 5 | `public double purseTotal()` _(in ResearchPreview.Result)_ | The purse all the fields' finds would pay. |
| 120 | 1 | `public int labsFirstMonth(Field f)` _(in ResearchPreview.Result)_ | The labs investors would want in the field in the first month of the preview (Research.labsWanted() as the month would ask it); 0 where the field has no prize. |

### THE PREVIEW (lines 123-148)

| line | len | member | says |
|---:|---:|---|---|
| 134 | 6 | `public static Result preview(Research research, int month, EnumMap<Field, Double> researchers, EnumMap<Field, Double> prize, do...` | The preview of a research at a month: the finds here over the next YEARS years from `draws` draws of the hidden sizes, `researchers` held fixed in each field (a field missing has none) and `prize` the D$ a find in eac... |
| 142 | 6 | `public static Result preview(Research research, int month, EnumMap<Field, Double> researchers, EnumMap<Field, Double> prize, do...` | The same with every field's labs equally full. |

### THE PLAN: what a research at a month has left to find, on arrays (lines 149-254)

| line | len | member | says |
|---:|---:|---|---|
| 180 | 59 | `private ResearchPreview(Research research, int month, EnumMap<Field, Double> researchers, EnumMap<Field, Double> prizes, double...` |  |
| 241 | 4 | `static ResearchPreview plan(Research research, int month, EnumMap<Field, Double> researchers, EnumMap<Field, Double> prize, dou...` | The plan of a research at a month (package-private for the harness, which holds path() to Research.month()). |
| 246 | 3 | `private static double sane(Double x)` |  |
| 251 | 3 | `List<Entry> entries()` | The entries the plan plays, in the order path() takes sizes and gives months: field by field in the enum's order, the catalogue's within a field. |

### THE DRAWS (lines 255-326)

| line | len | member | says |
|---:|---:|---|---|
| 259 | 34 | `private Result draw(int draws, long seed)` |  |
| 295 | 5 | `private static double standardError(double sum, double sumSq, int draws)` | The standard error of the mean of `draws` counts, from their sum and the sum of their squares. |
| 307 | 19 | `static double drawSize(SplittableRandom rng, double work, double pile)` | A problem's size in a draw: its typical work times a lognormal draw (sigma Research.SIZE_SIGMA, mean 1), redrawn until it is above the pile on it (research_model.py forward(): a problem not found yet is not smaller th... |

### THE PATH: one city's ten years, Research.month() on arrays (lines 327-450)

| line | len | member | says |
|---:|---:|---|---|
| 335 | 4 | `int[] path(double[] size)` | One path over given sizes (`size` by entries(), what Research.hiddenSize() is in the city): the month each entry is found here, or NEVER, by entries(). |
| 341 | 3 | `int labsFirstMonth(Field f)` | The labs the first month of the last path() wanted in a field (the same as Result.labsFirstMonth() for the draw it was recorded in). |
| 345 | 3 | `private boolean knownAt(int i, int m)` |  |
| 349 | 61 | `private void run(double[] size, boolean recordLabs)` |  |
| 412 | 14 | `private void spread(int f, int k)` | The field's open problems (openIdx[0..k)) spread over by its researchers: share[] set for each, the water fill reused while the open set is the same. |
| 431 | 19 | `private int chaseOf(int f, int k, int m)` | The field's chase into chase[]: its open problems (openIdx[0..k)) that the prize pays for and a team at its limit could finish before the world, in this path's piles, least remaining work first and the catalogue's ord... |

