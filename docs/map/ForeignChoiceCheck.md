# ForeignChoiceCheck.java - 847 lines · 29 methods · 14 constants · harnesses

`ham/citybuildersim/ForeignChoiceCheck.java` - generated 2026-11-24 by CodeMap; line numbers are as of that run.

> The choices that use the foreign crews (FC2b): a payer - a business by its own sum, the city from its card - hands its
> share of one site's remaining work to the foreign crews, who build it on that site at the pace and price struck then;
> held on the calm city of ConstructionTrapCheck (its landlords' Houses on site at 0.8 of the knee), the queue at home made
> long by standing Paved Roads, whose repairs take the sites' month.
> 
> WHY. Jerus, 2026-11-22 (docs/queue/FC2.md, "## FC2b"): "allowing businesses to even decide to pay more to finish sooner
> (for example, something finished in 20 months is better than something finished in 100 months, cause thats 80 more
> months of paying interest...)", and "also let the city itself do it, like per building, aka for example if roads are
> causing the issues then you could pay more to get just roads fixed". So the money spent already is sunk and only the work
> still to do moves; a business moves when the extra is under what the months saved are worth to it, and stays when it is
> over; the city pays from its treasury what its card shows and its site opens sooner; one order leaves a shared site and
> the others' progress is not touched; and every dollar is booked once. The model is BuildersShort (the arithmetic, THE
> CHOICE TO FINISH SOONER), BuildingsStacks (the home and abroad contracts of one stack), ConstructionControl (G. ABROAD),
> BuildingManager (abroadTermsOf(), handSiteAbroad(), advanceConstruction()), Game (investorsHandAbroad(), abroadPreview(),
> handSiteAbroad(), settleCrewsOnSites()) and sectors.Construction (payCrewsOnSites()). ForeignCrewsCheck holds FC2's own
> crews, the threshold and the pooled book; this holds what a payer chooses on top of them.
> 
> What this has to prove:
>   1. THE ARITHMETIC: sitePace() floors at the depot, caps at the points and at a finite build-time cap, and is the points
>      over the threshold's months otherwise; monthsAbroad() rounds up; broughtForward() is linear in its earnings, tends to
>      perMonth x (Wh - Wa) as the rate goes to nothing, is P / i x (1 + i)^-Wa for a home start endless, and is the sum of
>      the discounted months; and interestSaved(C) + broughtForward(P - C x i) is broughtForward(P): the two parts, no
>      double count.
>   2. A BUSINESS MOVES WHEN THE EXTRA IS UNDER ITS SAVING AND STAYS WHEN IT IS OVER: twin cities built the same, the
>      landlords' Houses on a long queue, differing in the exchange rate alone; the foreign crews would finish it sooner in
>      both, and the extra net of the tax recovered is under the saving at broughtForward() in the one that hands the site
>      to them and over it in the one that does not.
>   3. A HALF-BUILT SITE MOVES ON ITS REMAINING WORK ALONE: with months of work on it, a business's site and the city's
>      each hand over their payer's share of the points owed, not the points on site; the extra is struck on them; the
>      stack's progress and its finished buildings are what they were, and the record owes those points. A site FC2 holds
>      a claim on hands its share less that pooled claim (the terms alone: with his book busy the card is not open).
>   4. ONE ORDER'S BUILDINGS LEAVE A SHARED STACK AS SETTLED: the city's contract on a stack the landlords share is the same
>      at the handover, and then earned at the home part's fraction while the landlords' abroad one is earned at the abroad
>      part's, the earned dues summing to the stack's revenue due.
>   5. THE CITY'S SITE HANDED ABROAD FINISHES SOONER AT THE CARD'S PRICE: twins on a long queue, in one the card is open and
>      the handover is made; the treasury pays the card's extra, the site's last building opens sooner than in the twin,
>      the crews billed on it are its points at the card's price, and the card's months and interest saved are its getters'.
>   6. THE MONEY IS BOOKED ONCE: the payer's payment is in the builders' order book (unearned revenue up by the gross
>      extra) and is earned as the abroad part is built; the crews' bill is an import of the builders, the month after, in
>      the audit's TRADE scope and the national and foreign accounts; every month of it balanced.
>   7. THE SAVED CHOICE: a city with a record half done, saved and loaded, has the record and the abroad contracts it had
>      and plays on month for month as its live twin.
> 
> Every fixture causes its condition: the orders placed, the standing roads, the exchange rate, the payer's cash.

**Uses:** [BuildersShort](BuildersShort.md) (56), [BuildingsStacks](BuildingsStacks.md) (22), [Game](Game.md) (22), [ConstructionControl](ConstructionControl.md) (21), [BuildingManager](BuildingManager.md) (18), [BuildingsTemplate](BuildingsTemplate.md) (13), [ConstructionTrapCheck](ConstructionTrapCheck.md) (10), [MoneyAudit](MoneyAudit.md) (6), [ForeignCrewsCheck](ForeignCrewsCheck.md) (4), [Construction](Construction.md) (4), [DeathCareFirstCheck](DeathCareFirstCheck.md) (2), [TaxPolicy](TaxPolicy.md) (2), [TempDirs](TempDirs.md) (1), [EconomyManager](EconomyManager.md) (1), [BuildAdvice](BuildAdvice.md) (1), [Sector](Sector.md) (1)

## Sections

| line | section |
|---:|---|
| 160 | 1. THE ARITHMETIC |
| 265 | THE DECIDING QUANTITIES |
| 339 | 2. MOVE OR STAY |
| 409 | 3. A HALF-BUILT SITE |
| 476 | 4. A SHARED STACK |
| 574 | 5. THE CITY'S SITE |
| 668 | 6. THE MONEY |
| 753 | 7. THE SAVED CHOICE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 75 | `ForeignChoiceCheck.STRONG_SHARE_OF_CAP` | `0.125` | The first share of the foreign crews' excess at the strong D$ of section 2's mover (BuildersShort.capShare() over this many): the stayer keeps the city's own rate, the share the cap. |
| 78 | `ForeignChoiceCheck.SEARCH_FROM` | `12, SEARCH_STEP = 1` | The months queued at home at which the search for the twins' queue starts, and the step it takes: a month at a time, so the crossing is not stepped over. |
| 81 | `ForeignChoiceCheck.SEARCH_TO` | `60` | The months queued at home past which the search gives up: a premium past the crews' excess makes every move free, and there is no pair of rates to tell apart. |
| 84 | `ForeignChoiceCheck.PAYER_CASH_TIMES` | `100` | The landlords hold this many times the cost of their Houses on site in cash, so the extra is never borrowed: the saving is then judged at the loan rate of nothing borrowed. |
| 87 | `ForeignChoiceCheck.HALF_BUILT_MONTHS` | `3` | Section 3's months worked on a site before it is handed: some of its buildings finished, one part built. |
| 90 | `ForeignChoiceCheck.CLAIM_SCHOOLS` | `6` | ...and the city's Middle Schools ordered past the threshold in its second case, so that FC2 keeps a claim on the site (REV-FC2). |
| 93 | `ForeignChoiceCheck.CITY_HOUSES` | `60` | Section 4's Houses the city orders on the landlords' site: a share of it. |
| 96 | `ForeignChoiceCheck.SCHOOL` | `"Middle School"` | Section 5's site: Middle Schools the city orders when the landlords' Houses are built, and the months queued at home it stands at: long, and under the point where the home premium passes the crews' excess. |
| 97 | `ForeignChoiceCheck.SCHOOLS` | `2` |  |
| 98 | `ForeignChoiceCheck.CITY_QUEUE_MONTHS` | `25` |  |
| 101 | `ForeignChoiceCheck.MOST_MONTHS` | `60` | The most months a site is played for to see its last building opened, and the months a record is followed. |
| 104 | `ForeignChoiceCheck.BEFORE_THE_SAVE` | `2, AFTER_THE_SAVE = 4` | The months played on before a save (the record half done), and after it, in both twins. |
| 107 | `ForeignChoiceCheck.SCRATCH_SLOT` | `ConstructionTrapCheck.SCRATCH_SLOT` | The slot the saved city is written to, in its own scratch folder. |
| 162 | `ForeignChoiceCheck.INF` | `Double.POSITIVE_INFINITY` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 110 | `static int checked` | The assertions made here. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 72 | 776 | **type** `public class ForeignChoiceCheck` | The choices that use the foreign crews (FC2b): a payer - a business by its own sum, the city from its card - hands its share of one site's remaining work to the foreign crews, who build it on that site at the pace and... |
| 113 | 5 | `static void check(String label, boolean ok)` | One assertion: the label reads as a sentence; a failing one prints FAIL, and `fails` (ConstructionTrapCheck's) is the exit code. |
| 120 | 5 | `static void check(String label, boolean ok, String shown)` | ...with what was read, printed whether or not it held. |
| 127 | 4 | `static void near(String label, double actual, double expected, double tol)` | Two figures within `tol` of each other, absolutely or of the expected, whichever is the larger. |
| 133 | 3 | `static void exact(String label, double actual, double expected)` | Two figures the same to the bit. |
| 137 | 22 | `public static void main(String[] args) throws Exception` |  |

### 1. THE ARITHMETIC (lines 160-264)

| line | len | member | says |
|---:|---:|---|---|
| 164 | 100 | `static void arithmetic()` |  |

### THE DECIDING QUANTITIES (lines 265-338)

| line | len | member | says |
|---:|---:|---|---|
| 273 | 2 | **type** `record Verdict(BuildingManager.AbroadTerms terms, double gross, double cost, double perMonth, double rate, ...` | What a business's test reads for its site (Game.investorsHandAbroad()), read the way it reads them and from the model's own getters: the terms a handover would strike, the extra grossed up for the builders' tax and ne... |
| 276 | 4 | `static BuildingsStacks.Contract contractOf(BuildingsStacks s, String payer)` |  |
| 281 | 17 | `static Verdict verdict(Game g, BuildingsTemplate t, String payer)` |  |
| 299 | 1 | `static boolean soonerAbroad(Verdict v)` |  |
| 307 | 26 | `static Game choiceCity(Path root, String name, int played, long cityHouses)` | A calm city with the landlords' Houses on site, rich in cash, the queue at home made long by standing roads until the landlords' sum can go either way on the exchange rate alone: at the strong D$ the extra they would ... |
| 335 | 1 | `static double strongRate(Game g)` | The rate a mover is struck at: the first share of the crews' excess the fixture's strong D$ gives. |
| 337 | 1 | `static String key(Game g, String building)` |  |

### 2. MOVE OR STAY (lines 339-408)

| line | len | member | says |
|---:|---:|---|---|
| 341 | 57 | `static void theTwins(Path root)` |  |
| 400 | 1 | `static double costOnSite(Game g, BuildingsTemplate t, Verdict v)` | The cost of the buildings a verdict's terms stand for, as the landlords' sum read it. |
| 402 | 6 | `static void printVerdict(String who, Verdict v)` |  |

### 3. A HALF-BUILT SITE (lines 409-475)

| line | len | member | says |
|---:|---:|---|---|
| 415 | 40 | `static void halfBuilt(String who, Game g, BuildingsTemplate t, String payer, boolean claimed, Runnable hand)` | One half-built site handed over: the terms read before it (its payer's share of the points owed less FC2's pooled claim, not its points on site; the extra struck on them), the handover made by {@code hand}, and the st... |
| 456 | 19 | `static void theHalfBuilt(Path root)` |  |

### 4. A SHARED STACK (lines 476-573)

| line | len | member | says |
|---:|---:|---|---|
| 479 | 7 | **type** `record Snap(double value, double units, double allowance, double points)` | One contract's figures. |
| 480 | 1 | `static Snap of(BuildingsStacks.Contract c)` _(in ForeignChoiceCheck.Snap)_ |  |
| 481 | 4 | `boolean same(Snap o)` _(in ForeignChoiceCheck.Snap)_ |  |
| 487 | 4 | `static BuildingsStacks.Contract abroadContractOf(BuildingsStacks s, String payer)` |  |
| 492 | 81 | `static void theSharedStack(Path root)` |  |

### 5. THE CITY'S SITE (lines 574-667)

| line | len | member | says |
|---:|---:|---|---|
| 580 | 12 | `static Game citySite(Path root, String name, long schools, int worked, double queueMonths)` | A calm city whose landlords' Houses are built (nothing else on site), the city's Middle Schools ordered, and the queue at home made long by standing roads to CITY_QUEUE_MONTHS; the city's cash given for the extra. |
| 593 | 74 | `static void theCitysSite(Path root)` |  |

### 6. THE MONEY (lines 668-752)

| line | len | member | says |
|---:|---:|---|---|
| 674 | 46 | `static void followTheMoney(String who, Game g, String key, double unearnedBefore)` | The money of one handed site, followed from the handover to its end and two months more: the bills as the builders' imports the month after, in the audit's TRADE scope and the accounts; the payment earned as the abroa... |
| 721 | 1 | `static boolean near2(double actual, double expected, double tol)` |  |
| 723 | 29 | `static void theMoney(Path root)` |  |

### 7. THE SAVED CHOICE (lines 753-847)

| line | len | member | says |
|---:|---:|---|---|
| 756 | 17 | `static List<String> differ(ConstructionControl.Abroad a, ConstructionControl.Abroad b)` | The fields of a record that differ between two, by name; every one compared to the bit. |
| 774 | 10 | `static boolean sameContracts(java.util.Collection<BuildingsStacks.Contract> a, java.util.Collection<BuildingsStacks.Contract> b)` |  |
| 785 | 62 | `static void theSave(Path root)` |  |

