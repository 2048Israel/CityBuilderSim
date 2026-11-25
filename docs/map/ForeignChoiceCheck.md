# ForeignChoiceCheck.java - 1,180 lines · 37 methods · 18 constants · harnesses

`ham/citybuildersim/ForeignChoiceCheck.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

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
>   8. A WAIT AT HOME THAT NEVER ENDS CROSSES A SAVE (REV-FC2b-1): the terms of a site read at a wait output of nothing have an endless
>      wait at home; the record made of them keeps it as the greatest finite wait and every figure it keeps finite, so that DataSave's Gson,
>      which writes no infinity, saves the city; loaded, the record is the same field for field and plays on as its live twin. In the city's
>      own read no wait is endless: the contractor's month keeps orderWaitOutput() over nothing when the repairs take every point.
>   9. A PAVING HANDED ABROAD (ConstructionControl, F): the city's Paved Road site that paves its gravel roads is handed by its card; the
>      record owes the paving's whole work, nothing of it stays at home, and the gravel roads retire as the paved roads open, all of them in
>      fewer months than in the twin that keeps the paving at home.
>  10. THE PAYMENT'S TWO REFUSALS, BOTH SIDES EACH: a business that must borrow its extra moves when the bank lends it (its debt up by the
>      shortfall, its cash what the loan and the extra leave) and stays, nothing booked, when the bank will not (its borrowing barred); the
>      city moves when its treasury holds the card's extra and is refused, nothing booked, when it holds less (it cannot borrow).
> 
> Every fixture causes its condition: the orders placed, the standing roads, the exchange rate, the payer's cash, the paving ordered, the
> borrowing ban on the credit record, the treasury's cash.

**Uses:** [BuildersShort](BuildersShort.md) (60), [Game](Game.md) (41), [ConstructionControl](ConstructionControl.md) (35), [BuildingsStacks](BuildingsStacks.md) (26), [BuildingManager](BuildingManager.md) (25), [BuildingsTemplate](BuildingsTemplate.md) (17), [ConstructionTrapCheck](ConstructionTrapCheck.md) (11), [Construction](Construction.md) (6), [MoneyAudit](MoneyAudit.md) (6), [ForeignCrewsCheck](ForeignCrewsCheck.md) (4), [EconomyManager](EconomyManager.md) (3), [DeathCareFirstCheck](DeathCareFirstCheck.md) (2), [TaxPolicy](TaxPolicy.md) (2), [Sector](Sector.md) (2), [GameFiles](GameFiles.md) (2), [BusinessDebtManager](BusinessDebtManager.md) (2), [TempDirs](TempDirs.md) (1), [BuildAdvice](BuildAdvice.md) (1), [LandManager](LandManager.md) (1)

## Sections

| line | section |
|---:|---|
| 185 | 1. THE ARITHMETIC |
| 290 | THE DECIDING QUANTITIES |
| 364 | 2. MOVE OR STAY |
| 434 | 3. A HALF-BUILT SITE |
| 501 | 4. A SHARED STACK |
| 599 | 5. THE CITY'S SITE |
| 693 | 6. THE MONEY |
| 778 | 7. THE SAVED CHOICE |
| 873 | 8. A WAIT AT HOME THAT NEVER ENDS, SAVED |
| 1001 | 9. A PAVING HANDED ABROAD |
| 1089 | 10. THE PAYMENT'S TWO REFUSALS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 88 | `ForeignChoiceCheck.STRONG_SHARE_OF_CAP` | `0.125` | The first share of the foreign crews' excess at the strong D$ of section 2's mover (BuildersShort.capShare() over this many): the stayer keeps the city's own rate, the share the cap. |
| 91 | `ForeignChoiceCheck.SEARCH_FROM` | `12, SEARCH_STEP = 1` | The months queued at home at which the search for the twins' queue starts, and the step it takes: a month at a time, so the crossing is not stepped over. |
| 94 | `ForeignChoiceCheck.SEARCH_TO` | `60` | The months queued at home past which the search gives up: a premium past the crews' excess makes every move free, and there is no pair of rates to tell apart. |
| 97 | `ForeignChoiceCheck.PAYER_CASH_TIMES` | `100` | The landlords hold this many times the cost of their Houses on site in cash, so the extra is never borrowed: the saving is then judged at the loan rate of nothing borrowed. |
| 100 | `ForeignChoiceCheck.HALF_BUILT_MONTHS` | `3` | Section 3's months worked on a site before it is handed: some of its buildings finished, one part built. |
| 103 | `ForeignChoiceCheck.CLAIM_SCHOOLS` | `6` | ...and the city's Middle Schools ordered past the threshold in its second case, so that FC2 keeps a claim on the site (REV-FC2). |
| 106 | `ForeignChoiceCheck.CITY_HOUSES` | `60` | Section 4's Houses the city orders on the landlords' site: a share of it. |
| 109 | `ForeignChoiceCheck.SCHOOL` | `"Middle School"` | Section 5's site: Middle Schools the city orders when the landlords' Houses are built, and the months queued at home it stands at: long, and under the point where the home premium passes the crews' excess. |
| 110 | `ForeignChoiceCheck.SCHOOLS` | `2` |  |
| 111 | `ForeignChoiceCheck.CITY_QUEUE_MONTHS` | `25` |  |
| 114 | `ForeignChoiceCheck.MOST_MONTHS` | `60` | The most months a site is played for to see its last building opened, and the months a record is followed. |
| 117 | `ForeignChoiceCheck.BEFORE_THE_SAVE` | `2, AFTER_THE_SAVE = 4` | The months played on before a save (the record half done), and after it, in both twins. |
| 120 | `ForeignChoiceCheck.PAVINGS` | `2` | Section 9's gravel roads the city orders paved: a pair, whose paving at 25 months queued at home still leaves the crews' excess over the home premium (the extra is something). |
| 123 | `ForeignChoiceCheck.SHORT_CASH_SHARE` | `0.5` | Section 10(a)'s landlords hold this share of the gross extra they would pay, so that they must borrow the rest. |
| 126 | `ForeignChoiceCheck.TREASURY_OVER` | `2, TREASURY_UNDER = 0.5` | Section 10(b)'s treasury holds this many times the card's extra when it can pay and this many when it cannot (the city never borrows: Game's government). |
| 129 | `ForeignChoiceCheck.SCRATCH_SLOT` | `ConstructionTrapCheck.SCRATCH_SLOT` | The slot the saved city is written to, in its own scratch folder. |
| 187 | `ForeignChoiceCheck.INF` | `Double.POSITIVE_INFINITY` |  |
| 876 | `ForeignChoiceCheck.RECORD_FIGURES` | `{ "points", "owed", "materials", "pace", "pricePerPoint", "paid", "monthsHome...` | The figures a record keeps as numbers: the ones differ() compares. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 132 | `static int checked` | The assertions made here. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 85 | 1096 | **type** `public class ForeignChoiceCheck` | The choices that use the foreign crews (FC2b): a payer - a business by its own sum, the city from its card - hands its share of one site's remaining work to the foreign crews, who build it on that site at the pace and... |
| 135 | 5 | `static void check(String label, boolean ok)` | One assertion: the label reads as a sentence; a failing one prints FAIL, and `fails` (ConstructionTrapCheck's) is the exit code. |
| 142 | 5 | `static void check(String label, boolean ok, String shown)` | ...with what was read, printed whether or not it held. |
| 149 | 4 | `static void near(String label, double actual, double expected, double tol)` | Two figures within `tol` of each other, absolutely or of the expected, whichever is the larger. |
| 155 | 3 | `static void exact(String label, double actual, double expected)` | Two figures the same to the bit. |
| 159 | 25 | `public static void main(String[] args) throws Exception` |  |

### 1. THE ARITHMETIC (lines 185-289)

| line | len | member | says |
|---:|---:|---|---|
| 189 | 100 | `static void arithmetic()` |  |

### THE DECIDING QUANTITIES (lines 290-363)

| line | len | member | says |
|---:|---:|---|---|
| 298 | 2 | **type** `record Verdict(BuildingManager.AbroadTerms terms, double gross, double cost, double perMonth, double rate, ...` | What a business's test reads for its site (Game.investorsHandAbroad()), read the way it reads them and from the model's own getters: the terms a handover would strike, the extra grossed up for the builders' tax and ne... |
| 301 | 4 | `static BuildingsStacks.Contract contractOf(BuildingsStacks s, String payer)` |  |
| 306 | 17 | `static Verdict verdict(Game g, BuildingsTemplate t, String payer)` |  |
| 324 | 1 | `static boolean soonerAbroad(Verdict v)` |  |
| 332 | 26 | `static Game choiceCity(Path root, String name, int played, long cityHouses)` | A calm city with the landlords' Houses on site, rich in cash, the queue at home made long by standing roads until the landlords' sum can go either way on the exchange rate alone: at the strong D$ the extra they would ... |
| 360 | 1 | `static double strongRate(Game g)` | The rate a mover is struck at: the first share of the crews' excess the fixture's strong D$ gives. |
| 362 | 1 | `static String key(Game g, String building)` |  |

### 2. MOVE OR STAY (lines 364-433)

| line | len | member | says |
|---:|---:|---|---|
| 366 | 57 | `static void theTwins(Path root)` |  |
| 425 | 1 | `static double costOnSite(Game g, BuildingsTemplate t, Verdict v)` | The cost of the buildings a verdict's terms stand for, as the landlords' sum read it. |
| 427 | 6 | `static void printVerdict(String who, Verdict v)` |  |

### 3. A HALF-BUILT SITE (lines 434-500)

| line | len | member | says |
|---:|---:|---|---|
| 440 | 40 | `static void halfBuilt(String who, Game g, BuildingsTemplate t, String payer, boolean claimed, Runnable hand)` | One half-built site handed over: the terms read before it (its payer's share of the points owed less FC2's pooled claim, not its points on site; the extra struck on them), the handover made by {@code hand}, and the st... |
| 481 | 19 | `static void theHalfBuilt(Path root)` |  |

### 4. A SHARED STACK (lines 501-598)

| line | len | member | says |
|---:|---:|---|---|
| 504 | 7 | **type** `record Snap(double value, double units, double allowance, double points)` | One contract's figures. |
| 505 | 1 | `static Snap of(BuildingsStacks.Contract c)` _(in ForeignChoiceCheck.Snap)_ |  |
| 506 | 4 | `boolean same(Snap o)` _(in ForeignChoiceCheck.Snap)_ |  |
| 512 | 4 | `static BuildingsStacks.Contract abroadContractOf(BuildingsStacks s, String payer)` |  |
| 517 | 81 | `static void theSharedStack(Path root)` |  |

### 5. THE CITY'S SITE (lines 599-692)

| line | len | member | says |
|---:|---:|---|---|
| 605 | 12 | `static Game citySite(Path root, String name, long schools, int worked, double queueMonths)` | A calm city whose landlords' Houses are built (nothing else on site), the city's Middle Schools ordered, and the queue at home made long by standing roads to CITY_QUEUE_MONTHS; the city's cash given for the extra. |
| 618 | 74 | `static void theCitysSite(Path root)` |  |

### 6. THE MONEY (lines 693-777)

| line | len | member | says |
|---:|---:|---|---|
| 699 | 46 | `static void followTheMoney(String who, Game g, String key, double unearnedBefore)` | The money of one handed site, followed from the handover to its end and two months more: the bills as the builders' imports the month after, in the audit's TRADE scope and the accounts; the payment earned as the abroa... |
| 746 | 1 | `static boolean near2(double actual, double expected, double tol)` |  |
| 748 | 29 | `static void theMoney(Path root)` |  |

### 7. THE SAVED CHOICE (lines 778-872)

| line | len | member | says |
|---:|---:|---|---|
| 781 | 17 | `static List<String> differ(ConstructionControl.Abroad a, ConstructionControl.Abroad b)` | The fields of a record that differ between two, by name; every one compared to the bit. |
| 799 | 10 | `static boolean sameContracts(java.util.Collection<BuildingsStacks.Contract> a, java.util.Collection<BuildingsStacks.Contract> b)` |  |
| 810 | 62 | `static void theSave(Path root)` |  |

### 8. A WAIT AT HOME THAT NEVER ENDS, SAVED (lines 873-1000)

| line | len | member | says |
|---:|---:|---|---|
| 879 | 10 | `static String notFinite(ConstructionControl.Abroad r)` | The first of a record's figures that is not finite, or null when every one is. |
| 894 | 20 | `static Game savedAndLoaded(Path root, String name, Game live, String slotName, String[] why)` | The city saved to the scratch slot of its own folder and loaded into a fresh Game over that folder, held as the live one is (a hold is a harness lever a save does not carry). |
| 915 | 85 | `static void theEndlessWait(Path root)` |  |

### 9. A PAVING HANDED ABROAD (lines 1001-1088)

| line | len | member | says |
|---:|---:|---|---|
| 1007 | 17 | `static Game pavingCity(Path root, String name)` | A calm city whose landlords' Houses are built (nothing else on site), PAVINGS Gravel Roads standing and their paving ordered on the Paved Road's site, the queue at home made long by standing Paved Roads to CITY_QUEUE_... |
| 1025 | 63 | `static void thePaving(Path root)` |  |

### 10. THE PAYMENT'S TWO REFUSALS (lines 1089-1180)

| line | len | member | says |
|---:|---:|---|---|
| 1092 | 4 | `static double savingBorrowing(Game g, Verdict v, double borrowed)` | The saving the landlords' months saved are worth at the rate of a loan for the shortfall (Game.judged(): the loan's rate; a bond's cheaper coupon can only raise it). |
| 1098 | 8 | `static Game shortMover(Path root, String name)` | Section 2's mover at the strong D$, its landlords' cash cut to SHORT_CASH_SHARE of the gross extra they would pay: they must borrow the rest. |
| 1107 | 73 | `static void thePayment(Path root)` |  |

