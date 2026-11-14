# FloorView.java - 376 lines · 61 methods · 8 constants · model

`ham/citybuildersim/FloorView.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

> What the Policy tab's minimum-wage page draws (WS1): the floor against the going rate and the unskilled wage, against the average wage and the world's
> nations, what a full-time floor job pays for, who is paid it or near it, the tie, the move a staged floor makes, every job's target at the two floors,
> the joined market, and the words that go beside them. A read of the city at one moment and a staged floor; nothing kept, nothing booked, no JavaFX.
> 
> WHY (WS1; docs/studies/spec-wages.md 3.6). The page's rule is the screens' rule - every figure through a public model getter, never recomputed in the
> screen - and the page needs a dozen figures that no one getter holds: the floor's share of the going rate and of the average wage, the cover of a
> floor job, the workers a staged floor lifts and what they are paid at the two targets, the nations the gauge is drawn against. They are made once, here,
> where a harness (PolicyPreviewCheck) can hold each to a hand sum of the model's own getters, and the screen only paints them. The words take the
> screen's money writer as an argument, so the model makes no "$" of its own and one writer serves the whole page.
> 
> A NUMBER THAT HAS NO MEANING IS NaN, NOT 0: a share of an average nobody is paid, a share of a going rate of nothing, an average pay of no workers.
> The screen writes NaN as a dash (Formats), and a 0 would be read as a fact.

**Uses:** [WageBand](WageBand.md) (8), [LabourMarket](LabourMarket.md) (7), [JobType](JobType.md) (5), [PolicyPreview](PolicyPreview.md) (4), [Formats](Formats.md) (2), [Game](Game.md) (2), [WorldMeasures](WorldMeasures.md) (2), [PopulationManager](PopulationManager.md) (1), [HouseholdAccounts](HouseholdAccounts.md) (1)

**Used by (2):** [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md)

## Sections

| line | section |
|---:|---|
| 211 | AGAINST THE GOING RATE |
| 236 | AGAINST THE AVERAGE WAGE, AND THE NATIONS |
| 263 | WHAT A FULL-TIME FLOOR JOB PAYS FOR |
| 284 | WHO IS NEAR IT |
| 309 | THE TIE |
| 320 | THE PREVIEW: THE FLOOR'S OWN MOVE |
| 339 | PER JOB, AND THE JOINED MARKET |
| 352 | THE WORDS |

## Enum constants

| line | constant | says |
|---:|---|---|
| 48 | `FloorView.Zone.NEVER` |  |
| 48 | `FloorView.Zone.GLUT` |  |
| 48 | `FloorView.Zone.ALWAYS` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 27 | `FloorView.GAUGE_TOP` | `1.5` | The right end of the gauge against the going rate: 1.5 times it, over the floor a player would ever mean to set. |
| 30 | `FloorView.NEVER_UNDER` | `LabourMarket.MIN_MULTIPLE` | Under this share of the going rate the floor never binds: the market alone stops at it (LabourMarket.MIN_MULTIPLE). |
| 33 | `FloorView.ALWAYS_OVER` | `1.0` | Over this share of the going rate the floor binds even when hands are short: the going rate is what the market pays at exactly enough workers. |
| 36 | `FloorView.BAND_LOW_PERCENTILE` | `.10` | The nation at this percentile (nearest rank, counted from the lowest ratio) is the bottom of the middle band of nations. |
| 39 | `FloorView.BAND_HIGH_PERCENTILE` | `.90` | The nation at this percentile (nearest rank) is the top of the middle band of nations. |
| 42 | `FloorView.CANADA` | `"CAN"` | The world file's id for Canada, the nation the page names beside the lowest and the highest. |
| 45 | `FloorView.NO_POSTS_WORDS` | `"no posts here"` | What a job's row says when the city has no posts of it. |
| 64 | `FloorView.FORMAT` | `Formats.INSTANCE` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 66 | `private final WageBand lowestBand` |  |
| 68 | `private final double stagedFloor` |  |
| 71 | `private final double goingRate, floor, staged, unskilledWage` | Against the going rate. |
| 72 | `private final double floorOverGoing, stagedOverGoing, unskilledOverGoing` |  |
| 75 | `private final double averageWage, floorOverAverage, stagedOverAverage, lowBandsShare` | Against the average wage, the nations. |
| 76 | `private final List<Nation> nations` |  |
| 77 | `private final Nation lowestNation, highestNation, canada, bandLow, bandHigh` |  |
| 78 | `private final int nationsInBand` |  |
| 81 | `private final double payslipShare, takeHome, rent, food, fees, costSum, left, cover` | What a full-time floor job pays for. |
| 84 | `private final double[] paid, near` | Who is near it. |
| 85 | `private final double paidTotal, nearTotal, postsFilled, everyoneElse` |  |
| 88 | `private final LabourMarket.FloorTie tie` | The tie. |
| 89 | `private final double tieShare, enteringShare` |  |
| 92 | `private final double lifted, liftedShare, liftedPayNow, liftedPayThen, payrollNow, payrollThen, payrollMove` | The preview, the floor's own move only. |
| 95 | `private final List<JobRow> jobs` | Per job, and the joined market. |
| 96 | `private final boolean joined` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 24 | 353 | **type** `public final class FloorView` | What the Policy tab's minimum-wage page draws (WS1): the floor against the going rate and the unskilled wage, against the average wage and the world's nations, what a full-time floor job pays for, who is paid it or ne... |
| 48 | 1 | **type** `public enum Zone` | Where a floor sits against the going rate: it never binds, binds only while hands are plentiful, or binds always. |
| 51 | 1 | **type** `public record Nation(String id, String name, double kaitz)` | A nation with a minimum-wage-to-average-wage figure in the world file (key "kaitz"). |
| 54 | 1 | **type** `public record JobRow(JobType job, long posts, double filled, double wage, double targetNow, double targetSt...` | One job type's row: its posts and fill, its wage, its target at the floor in force and at the staged floor, whether the staged floor moves it. |
| 57 | 6 | `public static Zone zoneOf(double shareOfGoingRate)` | The zone a share of the going rate falls in: under NEVER_UNDER never, over ALWAYS_OVER always, else GLUT; null when the share is not a number. |
| 99 | 3 | `public static FloorView of(Game g, double stagedFloor)` | The page's figures for a city and a staged real floor, in founding money as the dial holds it (the floor in force, LabourMarket.getMinimumWage(), when nothing is staged). |
| 103 | 97 | `private FloorView(Game g, double stagedFloor)` |  |
| 202 | 3 | `private static double over(double a, double b)` | a over b, NaN when b is not a positive finite number. |
| 207 | 3 | `private static int nearestRank(double p, int n)` | The 1-based rank of the nearest-rank percentile p among n: ceil(p x n), at least 1 (a hair under it, so an exact product does not round up). |

### AGAINST THE GOING RATE (lines 211-235)

| line | len | member | says |
|---:|---:|---|---|
| 216 | 1 | `public double stagedFloor()` | The staged floor as the dial holds it: real, in founding money. |
| 218 | 1 | `public double goingRate()` | The going rate in today's money: LabourMarket.cashGoingRate(). |
| 220 | 1 | `public double floor()` | The floor in force in today's money: LabourMarket.cashMinimumWage(). |
| 222 | 1 | `public double staged()` | The staged floor in today's money: LabourMarket.cashAt(stagedFloor). |
| 224 | 1 | `public double unskilledWage()` | What the unskilled job is paid now: the lowest band's job, LabourMarket.getWage(NO_DIPLOMA). |
| 226 | 1 | `public double floorOverGoing()` | The floor over the going rate; NaN when the going rate is nothing. |
| 228 | 1 | `public double stagedOverGoing()` | The staged floor over the going rate. |
| 230 | 1 | `public double unskilledOverGoing()` | The unskilled wage over the going rate. |
| 232 | 1 | `public Zone floorZone()` | The zone the floor in force falls in; null when its share is not a number. |
| 234 | 1 | `public Zone stagedZone()` | The zone the staged floor falls in. |

### AGAINST THE AVERAGE WAGE, AND THE NATIONS (lines 236-262)

| line | len | member | says |
|---:|---:|---|---|
| 241 | 1 | `public double averageWage()` | The average wage: PopulationManager.getAverageWage(), the wage bill over the posts filled (the Rankings'); 0 with nobody employed. |
| 243 | 1 | `public double floorOverAverage()` | The floor's share of the average wage; NaN with nobody employed. |
| 245 | 1 | `public double stagedOverAverage()` | The staged floor's share of the average wage; NaN with nobody employed. |
| 247 | 1 | `public double lowBandsShare()` | The share of the posts filled that are in the two lowest wage bands (why a city reads high against the average wage); NaN with none filled. |
| 249 | 1 | `public List<Nation> nations()` | The world file's nations with a minimum-wage-to-average-wage figure, lowest first. |
| 251 | 1 | `public Nation lowestNation()` | The nation with the lowest ratio; null with none. |
| 253 | 1 | `public Nation highestNation()` | The nation with the highest ratio; null with none. |
| 255 | 1 | `public Nation canada()` | Canada (CANADA); null when the file has no ratio for it. |
| 257 | 1 | `public Nation bandLow()` | The nation at BAND_LOW_PERCENTILE: the bottom of the middle band; null with no nations. |
| 259 | 1 | `public Nation bandHigh()` | The nation at BAND_HIGH_PERCENTILE: the top of the middle band; null with no nations. |
| 261 | 1 | `public int nationsInBand()` | How many nations have a ratio inside the middle band, its two ends included. |

### WHAT A FULL-TIME FLOOR JOB PAYS FOR (lines 263-283)

| line | len | member | says |
|---:|---:|---|---|
| 268 | 1 | `public double payslipShare()` | The share of a lowest-band payslip that does not reach the worker (TaxPolicy.payslipShare()). |
| 270 | 1 | `public double takeHome()` | The floor less the payslip's share. |
| 272 | 1 | `public double rent()` | A home's rent: HouseholdAccounts.rentPerHousehold(). |
| 274 | 1 | `public double food()` | A person's basket: HouseholdAccounts.shoppingPerHead(). |
| 276 | 1 | `public double fees()` | A person's fees: HouseholdAccounts.feesPerHead(). |
| 278 | 1 | `public double costSum()` | Rent, food and fees: what living alone costs (HouseholdAccounts.costOfLivingAlone()). |
| 280 | 1 | `public double left()` | What is left of the take-home after them; negative when the floor does not carry a person. |
| 282 | 1 | `public double cover()` | The take-home over what living alone costs: PolicyPreview.floorJobCover(). |

### WHO IS NEAR IT (lines 284-308)

| line | len | member | says |
|---:|---:|---|---|
| 289 | 1 | `public double paid(WageBand band)` | The posts filled that the floor is what pays, in this band (PopulationManager.workersPaidTheFloor()). |
| 291 | 1 | `public double near(WageBand band)` | The posts filled that are paid a little over the floor, in this band (PopulationManager.workersNearTheFloor()). |
| 293 | 1 | `public double atOrNear(WageBand band)` | The posts filled in this band that are paid the floor or a little over it: paid(band) and near(band) together. |
| 295 | 1 | `public double paidTotal()` | The posts filled that the floor is what pays, in every band. |
| 297 | 1 | `public double nearTotal()` | The posts filled that are paid a little over it, in every band. |
| 299 | 1 | `public double postsFilled()` | The posts filled, unrounded: posts times fill rate over every job, as PopulationManager.getJobsFilled() counts them. |
| 301 | 1 | `public double everyoneElse()` | The posts filled that are neither paid the floor nor near it. |
| 303 | 1 | `public double paidShare()` | The posts paid the floor over the posts filled; NaN with none filled. |
| 305 | 1 | `public double nearShare()` | The posts paid a little over the floor, over the posts filled; NaN with none filled. |
| 307 | 1 | `public double everyoneElseShare()` | The posts neither paid the floor nor near it, over the posts filled; NaN with none filled. |

### THE TIE (lines 309-319)

| line | len | member | says |
|---:|---:|---|---|
| 314 | 1 | `public LabourMarket.FloorTie tie()` | What the floor keeps pace with now. |
| 316 | 1 | `public double tieShare()` | The share of the average wage the tie asks (kept, unused, under another tie). |
| 318 | 1 | `public double enteringShare()` | The share choosing the average wage now would hold: the floor's share of it, held to the tie's range; the default share with nobody employed. |

### THE PREVIEW: THE FLOOR'S OWN MOVE (lines 320-338)

| line | len | member | says |
|---:|---:|---|---|
| 325 | 1 | `public double lifted()` | The posts filled in the jobs the staged floor moves (PolicyPreview.floorMoveAt()). |
| 327 | 1 | `public double liftedShare()` | Those workers over the posts filled; NaN with none filled. |
| 329 | 1 | `public double liftedPayNow()` | Their average monthly pay at the floor in force; NaN with none lifted. |
| 331 | 1 | `public double liftedPayThen()` | Their average monthly pay at the staged floor; NaN with none lifted. |
| 333 | 1 | `public double payrollNow()` | The city's wage bill a month at every job's target at the floor in force, the posts filled (FloorMove.billNow()). |
| 335 | 1 | `public double payrollThen()` | ...and at every job's target at the staged floor, once wages have walked there (FloorMove.billThen()). |
| 337 | 1 | `public double payrollMove()` | The city's wage bill a month once wages have walked to the staged floor, less what it is at the floor in force. |

### PER JOB, AND THE JOINED MARKET (lines 339-351)

| line | len | member | says |
|---:|---:|---|---|
| 344 | 1 | `public List<JobRow> jobs()` | Every job type's row, in JobType order. |
| 346 | 1 | `public JobRow job(JobType job)` | One job type's row. |
| 348 | 1 | `public boolean joined()` | Whether diploma holders fill the unskilled posts: the lowest band filled as one market with the band above it (BandFill.joinedAbove()). |
| 350 | 1 | `public double joinedWage()` | The wage the two bands share when they are one market (the unskilled wage); NaN when they are not. |

### THE WORDS (lines 352-376)

| line | len | member | says |
|---:|---:|---|---|
| 357 | 5 | `public String holdsWords(DoubleFunction<String> money)` | Who the floor holds: how many workers are paid it, or that nobody is and what unskilled work goes for. |
| 364 | 5 | `public String joinedWords(DoubleFunction<String> money)` | That diploma holders fill the unskilled posts, so both bands pay the one wage; null when the bands are not one market. |
| 371 | 5 | `public String readsHighWords()` | Why the gauge against the average wage reads high, when it does (the floor's share of it is over the middle band's top); null otherwise. |

