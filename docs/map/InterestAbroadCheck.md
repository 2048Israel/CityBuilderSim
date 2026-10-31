# InterestAbroadCheck.java - 1,126 lines · 39 methods · 9 constants · harnesses

`ham/citybuildersim/InterestAbroadCheck.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The central banks of the eight moneys the vault holds and the price of money the city reads (batch XR4, interest abroad; spec-exchange-rates 3.8,
> Jerus's calls 9 and 11): PolicyRates' monthly rate of each bank - the real table to December 2025 on Historical, the rule after it and from
> February 2000 on Random - held to the table where the table is the truth, to the rule's own constants where it rules, and to the city where the
> US's rate stands for the world's price of money (DebtManager.worldRate()).
> 
> WHY. The world's price of money was a constant 2% against a world whose inflation is a path, so the world's real rate swung with the path and the
> city's real gap with it. A table can be mistranscribed, a rule can step off its grid or through zero, a rate can be handed to the city late or
> not at all, a save can lose the state that the next month's step reads: each is checked here against the constants the class publishes, never a
> formula copied from it.
> 
> THE DATA. Section 1 needs no world file. The rest need the full world-1.bin (-Dcbs.world=<path>, WorldData.locate()), for its 196 nations and their
> price levels; a line says when it is not named, and those sections did not run.
> 
> What this has to prove:
>   1. THE TABLE: each of the eight has a rate in every one of the 312 months, spot values agree with the file's history, historical() is NaN outside
>      the table and for a code that is not one of the eight, and a table that is not whole is refused naming its line.
>   2. HISTORICAL REPLAYS, THEN THE RULE: a PolicyRates over a Historical WorldMoneys equals the table in every month to 311 and is ruling from 312;
>      from 312 to 1,199 every month's change of every money is a whole multiple of STEP, no rate falls below LOWER_BOUND, no rate is further than
>      STEP from its shadow, and no rate moves past it.
>   3. RANDOM RUNS THE RULE FROM FEBRUARY 2000: month 0 is the table's January 2000; ruling from month 1; two builds on one seed give identical
>      rates; the US's mean real rate is within a point of its neutral real rate; the target is the one targetFor() gives for the inflation read, the
>      real target rises TAYLOR_WEIGHT for each point of inflation over the bank's target, never goes under LOWER_BOUND, and the shadow closes its gap
>      to the target by the half-life.
>   4. THE CITY READS THE US'S RATE: a city with the world file has worldRate() equal to the US's policy rate every month; the real rate gap reads
>      it, and the tied rate, the currency rate, the foreign rate and the peg spread's bounds with it; the equity's required return stays on the constant.
>   5. NO WORLD, THE CONSTANT: a bare DebtManager, a city on the literals and a city quieted for the D$ all read WORLD_BASE_RATE.
>   6. THE SAVE: toSaveArray/restore continues bit for bit on both paths, a save that is not this one's is refused and changes nothing, nothing in the
>      save is a number that is none, and a city saved and loaded has the same eight rates, the same price of money and the same history.
>   7. THE VAULT EARNS EACH MONEY'S RATE TO THE CENT (no world file needed): a ForeignAccounts with balances in three moneys earns balance x rate / 12
>      in each one's own units on a positive, a zero and a negative rate, and keeps it; the month's US$ is their sum at the month's cross rates and
>      the cross move is the same before and after; a negative rate never takes a balance below zero; a money with no rate this month, or a bank
>      whose rate is no number, earns nothing; a month it is not called reads 0 (the lifetime kept); the month's, the lifetime, the driver and the
>      yield ride the array at slots 142-146 and an older array reads nothing earned.
>   8. A RISK-OFF UNWINDS THE HOT MONEY AND THE CARRY BY THEIR STOCKS (no world file needed): settled to a stock, a month of a risk-off at size 1
>      sends home RISK_OFF_HOT_UNWIND of the stock the ordinary month left, inside the month's departures; twice the stock unwinds twice as much,
>      half the level half; a level past 1 / the dial takes the whole stock and no more; level 0 (or no number, or below zero) is the untouched twin
>      to the bit; the carry the same with RISK_OFF_CARRY_UNWIND, inside the month's repaid and the change returned; the months are counted once
>      though both books are in one; the save, reset and a reform carry the lot.
>   9. IN A CITY: the vault's interest each month is the sum over the eight of the balance before x that bank's rate / 12 at the month's cross
>      rates, to the cent; a forced risk-off in a city that holds hot money sends some home the month it starts, the audit sees it cross the edge
>      and closes, the log says so; a city quieted for the D$ earns nothing and unwinds nothing though its own moneys are in a risk-off; a save
>      carries the lot and the loaded city does not earn the saved month twice.
> 
> Every fixture causes its condition: a Historical moneys for the table's replay, a Random one for the rule from month 1, a town for the city's
> reading, and a table with one line wrong for each refusal.

**Uses:** [PolicyRates](PolicyRates.md) (87), [CapitalFlows](CapitalFlows.md) (39), [DebtManager](DebtManager.md) (36), [WorldMoneys](WorldMoneys.md) (35), [ForeignAccounts](ForeignAccounts.md) (22), [Game](Game.md) (19), [Nations](Nations.md) (16), [WorldData](WorldData.md) (11), [World](World.md) (10), [ForeignCheck](ForeignCheck.md) (9), [HistorySave](HistorySave.md) (6), [Founding](Founding.md) (3), [WorldMarket](WorldMarket.md) (2), [Equity](Equity.md) (2), [GameFiles](GameFiles.md) (1), [Bank](Bank.md) (1), [YearBook](YearBook.md) (1), [MoneyAudit](MoneyAudit.md) (1)

**Used by (1):** [DebtMoneysCheck](DebtMoneysCheck.md)

## Sections

| line | section |
|---:|---|
| 146 | THE RUNS - helpers the sections share |
| 209 | 1. THE TABLE |
| 294 | 2. HISTORICAL REPLAYS, THEN THE RULE |
| 360 | 3. RANDOM RUNS THE RULE FROM FEBRUARY 2000 |
| 486 | 6. THE SAVE (the rates) |
| 554 | 4. THE CITY READS THE US'S RATE |
| 615 | 5. NO WORLD, THE CONSTANT |
| 651 | 6. THE SAVE (the city) |
| 687 | 7. THE VAULT EARNS (ForeignAccounts, no world file needed) |
| 835 | 8. A RISK-OFF UNWINDS THE HOT MONEY AND THE CARRY BY THEIR STOCKS (CapitalFlows) |
| 981 | 9. IN A CITY: THE VAULT EARNS, MONEY RUNS HOME, THE AUDIT CLOSES |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 59 | `InterestAbroadCheck.out` | `System.out` | Where the harness prints: the console. |
| 60 | `InterestAbroadCheck.quiet` | `new PrintStream(java.io.OutputStream.nullOutputStream())` |  |
| 63 | `InterestAbroadCheck.SEED` | `9_101` | The seed the single-world sections use, and the others the mean real rate is counted over. |
| 64 | `InterestAbroadCheck.SEEDS` | `{ 9_101, 9_102, 9_103, 9_104, 9_105 }` |  |
| 67 | `InterestAbroadCheck.LONG` | `1_200` | The months a long run covers: January 2000 to December 2099, the nations' path (the last month stepped is 1,199). |
| 70 | `InterestAbroadCheck.EXACT` | `1e-12` | Two rates or two logs that should be one differ by less than this. |
| 214 | `InterestAbroadCheck.SPOTS` | `{ { "USD", 2000, 1,.055 }, { "USD", 2000, 12,.065 }, { "USD", 2001, 1,.055 },...` | The spot values of the table, from the file's own history: bank, year, month, rate. |
| 692 | `InterestAbroadCheck.CROSS` | `{ 1, 1.10, 0.0070, 1.27, 0.14, 0.73, 0.65, 1.12 }` | The cross rates the section hands: US$ per unit of the US$, euro, yen, pound, yuan, Canadian and Australian dollar and franc. |
| 986 | `InterestAbroadCheck.HOT_DIAL` | `.15` | The dial the hot-money city is put at: well over the US's rate in any year, so the spread is there. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 57 | `static int fails` |  |
| 85 | `int n, bad` |  |
| 86 | `String first` |  |
| 152 | `final WorldData data` |  |
| 153 | `final double[] shares` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 55 | 1072 | **type** `public class InterestAbroadCheck` | The central banks of the eight moneys the vault holds and the price of money the city reads (batch XR4, interest abroad; spec-exchange-rates 3.8, Jerus's calls 9 and 11): PolicyRates' monthly rate of each bank - the r... |
| 72 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 78 | 4 | `static void close(String label, double got, double wanted, double tol)` | Two figures that should be one, to a tolerance. |
| 84 | 12 | **type** `static final class Tally` | Figures compared one by one, reported as one line: every one has to agree, and at least one has to have been read. |
| 88 | 7 | `void see(boolean ok, Supplier<String> what)` _(in InterestAbroadCheck.Tally)_ |  |
| 97 | 6 | `static void tally(String label, Tally t)` |  |
| 104 | 1 | `static boolean same(double a, double b)` |  |
| 107 | 3 | `static void exact(String label, double got, double wanted)` | Two figures that should be one to the bit. |
| 112 | 1 | `static int at(int year, int month)` | The month index of a calendar month: months since January 2000. |
| 114 | 31 | `public static void main(String[] args) throws Exception` |  |

### THE RUNS - helpers the sections share (lines 146-208)

| line | len | member | says |
|---:|---:|---|---|
| 151 | 13 | **type** `static final class World` | The world file and what is built on it: the nations' commodity shares of exports the moneys are given. |
| 155 | 6 | `World(WorldData data)` _(in InterestAbroadCheck.World)_ |  |
| 162 | 1 | `WorldMoneys moneys(Nations.Path path, long seed)` _(in InterestAbroadCheck.World)_ |  |
| 166 | 6 | `static void run(WorldMoneys w, PolicyRates r, int last)` | Steps a moneys and its rates together, each to the month last (both stand at the same month). |
| 174 | 5 | `static double[] ratesOf(PolicyRates r)` | The rates of the eight as they stand, in WorldMoneys.EIGHT's order. |
| 180 | 5 | `static void quietly(Runnable work)` |  |
| 187 | 1 | `static void play(Game city)` | One month of a city, the narration sent nowhere. |
| 190 | 18 | `static Game town(String label, boolean quietForTheD, boolean onLiterals)` | A trading town on the default founding, as WorldMoneysCheck builds it, on a scratch folder; quieted for the D$ or on the literals where asked (before its first month). |

### 1. THE TABLE (lines 209-293)

| line | len | member | says |
|---:|---:|---|---|
| 220 | 44 | `static void theTable()` |  |
| 266 | 5 | `static String validTable()` | A valid table's text: a comment and January 2000 for each of the eight at 1%, so the first line of a defect added is line 10. |
| 272 | 7 | `static double[][] parsed(String text)` |  |
| 281 | 12 | `static void refused(String defect, String text, String where)` | A table with one defect is refused: IllegalStateException, and the message says where. |

### 2. HISTORICAL REPLAYS, THEN THE RULE (lines 294-359)

| line | len | member | says |
|---:|---:|---|---|
| 298 | 61 | `static void theReplay(World world)` |  |

### 3. RANDOM RUNS THE RULE FROM FEBRUARY 2000 (lines 360-485)

| line | len | member | says |
|---:|---:|---|---|
| 364 | 109 | `static void theRandomRule(World world)` |  |
| 475 | 10 | `static boolean refusedStep(PolicyRates r, int month, double[] before)` | A step to a month that is not the next is refused, and the rates are as they were. |

### 6. THE SAVE (the rates) (lines 486-553)

| line | len | member | says |
|---:|---:|---|---|
| 490 | 55 | `static void theSaveOfTheRates(World world)` |  |
| 547 | 6 | `static boolean restoredIsUnread(World world, double[] atStart)` | The unread state restores as unread: a fresh one's save, restored into another, reads NaN for a target and an inflation again. |

### 4. THE CITY READS THE US'S RATE (lines 554-614)

| line | len | member | says |
|---:|---:|---|---|
| 558 | 47 | `static void theCity()` |  |
| 606 | 8 | `static boolean historyAgrees(Game g, PolicyRates p)` |  |

### 5. NO WORLD, THE CONSTANT (lines 615-650)

| line | len | member | says |
|---:|---:|---|---|
| 619 | 31 | `static void theConstant()` |  |

### 6. THE SAVE (the city) (lines 651-686)

| line | len | member | says |
|---:|---:|---|---|
| 655 | 31 | `static void theSaveOfTheCity()` |  |

### 7. THE VAULT EARNS (ForeignAccounts, no world file needed) (lines 687-834)

| line | len | member | says |
|---:|---:|---|---|
| 695 | 11 | `static ForeignAccounts threeMoneys()` | A vault holding US$50, 100 euros and 10,000 yen at CROSS (bought at a rate of 2), the month booked: the fixture the earning is asked of. |
| 708 | 8 | `static double[] mixedRates()` | The eight rates a section hands: the dollar's 4.8%, the euro's nothing, the yen's -0.1%, and the rest at 1%. |
| 717 | 117 | `static void theVaultEarns()` |  |

### 8. A RISK-OFF UNWINDS THE HOT MONEY AND THE CARRY BY THEIR STOCKS (CapitalFlows) (lines 835-980)

| line | len | member | says |
|---:|---:|---|---|
| 840 | 5 | `static void hotMonth(CapitalFlows f, int m, double gdp, double level)` | One month of hot money at a city paying 9% against a 2% world with a point of risk, GDP as given, a risk-off at this level. |
| 847 | 5 | `static CapitalFlows hotSettled(double gdp)` | Hot money settled to its target (a hundred and twenty months of calm). |
| 854 | 8 | `static CapitalFlows carrySettled(double headroom)` | A carry book settled to its target (a city paying 0.5% against a 2% world, a billion of headroom). |
| 864 | 5 | `static double riskOffAt(double level)` | What a risk-off of this level sends home from a settled hot money at 10,000 GDP. |
| 870 | 110 | `static void theUnwind()` |  |

### 9. IN A CITY: THE VAULT EARNS, MONEY RUNS HOME, THE AUDIT CLOSES (lines 981-1126)

| line | len | member | says |
|---:|---:|---|---|
| 989 | 9 | `static Game hotTown(String label, boolean quietForTheD)` | A town with banks to take hot money into, as CapitalFlowCheck's city has them. |
| 1000 | 11 | `static String playCaptured(Game city)` | One month of a city with what it printed captured. |
| 1012 | 114 | `static void inACity()` |  |

