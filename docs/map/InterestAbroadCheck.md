# InterestAbroadCheck.java - 1,177 lines · 39 methods · 11 constants · harnesses

`ham/citybuildersim/InterestAbroadCheck.java` - generated 2026-11-04 by CodeMap; line numbers are as of that run.

> The central banks of the eight moneys the vault holds and the price of money the city reads (batch XR4, interest abroad; spec-exchange-rates 3.8,
> Jerus's calls 9 and 11): PolicyRates' monthly rate of each bank - the real table to December 2025 on Historical (its January 2000 rate held through
> the stand-in century before it, EP1), the rule after it and from the second month on Random - held to the table where the table is the truth, to the rule's own constants where it rules, and to the city where the
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
>   1. THE TABLE: each of the eight has a rate in every one of its months (the file's 312, from January 2000, and the stand-in century's 1,200 before
>      them, each bank's January 2000 rate held), spot values agree with the file's history, historical() is NaN outside the table and for a code that
>      is not one of the eight, and a table that is not whole is refused naming its line.
>   2. HISTORICAL IN THREE STRETCHES (EP1): a PolicyRates over a Historical WorldMoneys runs the rule in the stand-in century (months 1 to 1,199,
>      reading the US CPI's own inflation), equals the table in every month January 2000 to December 2025 and is not ruling there, and runs the rule
>      again from January 2026; in every ruled month (the 888 after the table to December 2099 included) the target is targetFor() of the inflation
>      read, the shadow closes its gap by the half-life, every change of every money is a whole multiple of STEP, no rate falls below LOWER_BOUND, no
>      rate is further than STEP from its shadow, and no rate moves past it.
>   3. RANDOM RUNS THE RULE FROM THE SECOND MONTH: month 0 is the table's first month; ruling from month 1; two builds on one seed give identical
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

**Uses:** [PolicyRates](PolicyRates.md) (106), [WorldMoneys](WorldMoneys.md) (41), [CapitalFlows](CapitalFlows.md) (39), [DebtManager](DebtManager.md) (36), [ForeignAccounts](ForeignAccounts.md) (22), [Game](Game.md) (19), [Nations](Nations.md) (18), [WorldData](WorldData.md) (11), [World](World.md) (10), [ForeignCheck](ForeignCheck.md) (9), [HistorySave](HistorySave.md) (6), [PriceLevel](PriceLevel.md) (4), [Founding](Founding.md) (3), [WorldMarket](WorldMarket.md) (2), [Equity](Equity.md) (2), [GameFiles](GameFiles.md) (1), [Bank](Bank.md) (1), [YearBook](YearBook.md) (1), [MoneyAudit](MoneyAudit.md) (1)

**Used by (1):** [DebtMoneysCheck](DebtMoneysCheck.md)

## Sections

| line | section |
|---:|---|
| 155 | THE RUNS - helpers the sections share |
| 218 | 1. THE TABLE |
| 318 | 2. HISTORICAL REPLAYS, THEN THE RULE |
| 412 | 3. RANDOM RUNS THE RULE FROM THE SECOND MONTH |
| 538 | 6. THE SAVE (the rates) |
| 605 | 4. THE CITY READS THE US'S RATE |
| 666 | 5. NO WORLD, THE CONSTANT |
| 702 | 6. THE SAVE (the city) |
| 738 | 7. THE VAULT EARNS (ForeignAccounts, no world file needed) |
| 886 | 8. A RISK-OFF UNWINDS THE HOT MONEY AND THE CARRY BY THEIR STOCKS (CapitalFlows) |
| 1032 | 9. IN A CITY: THE VAULT EARNS, MONEY RUNS HOME, THE AUDIT CLOSES |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 62 | `InterestAbroadCheck.out` | `System.out` | Where the harness prints: the console. |
| 63 | `InterestAbroadCheck.quiet` | `new PrintStream(java.io.OutputStream.nullOutputStream())` |  |
| 66 | `InterestAbroadCheck.SEED` | `9_101` | The seed the single-world sections use, and the others the mean real rate is counted over. |
| 67 | `InterestAbroadCheck.SEEDS` | `{ 9_101, 9_102, 9_103, 9_104, 9_105 }` |  |
| 70 | `InterestAbroadCheck.LONG` | `1_200` | The months a Random run covers: a hundred years from the calendar's first January (the last month stepped is 1,199). |
| 73 | `InterestAbroadCheck.HISTORICAL_END` | `at(2099, 12) + 1` | The month after the last a Historical run steps: December 2099 is the last, the nations' path (the rule rules the 888 months after the table's end). |
| 76 | `InterestAbroadCheck.RULED` | `888` | The months the rule rules on Historical after the table: January 2026 to December 2099. |
| 79 | `InterestAbroadCheck.EXACT` | `1e-12` | Two rates or two logs that should be one differ by less than this. |
| 223 | `InterestAbroadCheck.SPOTS` | `{ { "USD", 2000, 1,.055 }, { "USD", 2000, 12,.065 }, { "USD", 2001, 1,.055 },...` | The spot values of the table, from the file's own history: bank, year, month, rate. |
| 743 | `InterestAbroadCheck.CROSS` | `{ 1, 1.10, 0.0070, 1.27, 0.14, 0.73, 0.65, 1.12 }` | The cross rates the section hands: US$ per unit of the US$, euro, yen, pound, yuan, Canadian and Australian dollar and franc. |
| 1037 | `InterestAbroadCheck.HOT_DIAL` | `.15` | The dial the hot-money city is put at: well over the US's rate in any year, so the spread is there. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 60 | `static int fails` |  |
| 94 | `int n, bad` |  |
| 95 | `String first` |  |
| 161 | `final WorldData data` |  |
| 162 | `final double[] shares` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 1120 | **type** `public class InterestAbroadCheck` | The central banks of the eight moneys the vault holds and the price of money the city reads (batch XR4, interest abroad; spec-exchange-rates 3.8, Jerus's calls 9 and 11): PolicyRates' monthly rate of each bank - the r... |
| 81 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 87 | 4 | `static void close(String label, double got, double wanted, double tol)` | Two figures that should be one, to a tolerance. |
| 93 | 12 | **type** `static final class Tally` | Figures compared one by one, reported as one line: every one has to agree, and at least one has to have been read. |
| 97 | 7 | `void see(boolean ok, Supplier<String> what)` _(in InterestAbroadCheck.Tally)_ |  |
| 106 | 6 | `static void tally(String label, Tally t)` |  |
| 113 | 1 | `static boolean same(double a, double b)` |  |
| 116 | 3 | `static void exact(String label, double got, double wanted)` | Two figures that should be one to the bit. |
| 121 | 1 | `static int at(int year, int month)` | The month index of a calendar month: months since January of the calendar's first year (Nations.FIRST_YEAR). |
| 123 | 31 | `public static void main(String[] args) throws Exception` |  |

### THE RUNS - helpers the sections share (lines 155-217)

| line | len | member | says |
|---:|---:|---|---|
| 160 | 13 | **type** `static final class World` | The world file and what is built on it: the nations' commodity shares of exports the moneys are given. |
| 164 | 6 | `World(WorldData data)` _(in InterestAbroadCheck.World)_ |  |
| 171 | 1 | `WorldMoneys moneys(Nations.Path path, long seed)` _(in InterestAbroadCheck.World)_ |  |
| 175 | 6 | `static void run(WorldMoneys w, PolicyRates r, int last)` | Steps a moneys and its rates together, each to the month last (both stand at the same month). |
| 183 | 5 | `static double[] ratesOf(PolicyRates r)` | The rates of the eight as they stand, in WorldMoneys.EIGHT's order. |
| 189 | 5 | `static void quietly(Runnable work)` |  |
| 196 | 1 | `static void play(Game city)` | One month of a city, the narration sent nowhere. |
| 199 | 18 | `static Game town(String label, boolean quietForTheD, boolean onLiterals)` | A trading town on the default founding, as WorldMoneysCheck builds it, on a scratch folder; quieted for the D$ or on the literals where asked (before its first month). |

### 1. THE TABLE (lines 218-317)

| line | len | member | says |
|---:|---:|---|---|
| 229 | 59 | `static void theTable()` |  |
| 290 | 5 | `static String validTable()` | A valid table's text: a comment and January 2000 for each of the eight at 1%, so the first line of a defect added is line 10. |
| 296 | 7 | `static double[][] parsed(String text)` |  |
| 305 | 12 | `static void refused(String defect, String text, String where)` | A table with one defect is refused: IllegalStateException, and the message says where. |

### 2. HISTORICAL REPLAYS, THEN THE RULE (lines 318-411)

| line | len | member | says |
|---:|---:|---|---|
| 322 | 89 | `static void theReplay(World world)` |  |

### 3. RANDOM RUNS THE RULE FROM THE SECOND MONTH (lines 412-537)

| line | len | member | says |
|---:|---:|---|---|
| 416 | 109 | `static void theRandomRule(World world)` |  |
| 527 | 10 | `static boolean refusedStep(PolicyRates r, int month, double[] before)` | A step to a month that is not the next is refused, and the rates are as they were. |

### 6. THE SAVE (the rates) (lines 538-604)

| line | len | member | says |
|---:|---:|---|---|
| 542 | 54 | `static void theSaveOfTheRates(World world)` |  |
| 598 | 6 | `static boolean restoredIsUnread(World world, double[] atStart)` | The unread state restores as unread: a fresh one's save, restored into another, reads NaN for a target and an inflation again. |

### 4. THE CITY READS THE US'S RATE (lines 605-665)

| line | len | member | says |
|---:|---:|---|---|
| 609 | 47 | `static void theCity()` |  |
| 657 | 8 | `static boolean historyAgrees(Game g, PolicyRates p)` |  |

### 5. NO WORLD, THE CONSTANT (lines 666-701)

| line | len | member | says |
|---:|---:|---|---|
| 670 | 31 | `static void theConstant()` |  |

### 6. THE SAVE (the city) (lines 702-737)

| line | len | member | says |
|---:|---:|---|---|
| 706 | 31 | `static void theSaveOfTheCity()` |  |

### 7. THE VAULT EARNS (ForeignAccounts, no world file needed) (lines 738-885)

| line | len | member | says |
|---:|---:|---|---|
| 746 | 11 | `static ForeignAccounts threeMoneys()` | A vault holding US$50, 100 euros and 10,000 yen at CROSS (bought at a rate of 2), the month booked: the fixture the earning is asked of. |
| 759 | 8 | `static double[] mixedRates()` | The eight rates a section hands: the dollar's 4.8%, the euro's nothing, the yen's -0.1%, and the rest at 1%. |
| 768 | 117 | `static void theVaultEarns()` |  |

### 8. A RISK-OFF UNWINDS THE HOT MONEY AND THE CARRY BY THEIR STOCKS (CapitalFlows) (lines 886-1031)

| line | len | member | says |
|---:|---:|---|---|
| 891 | 5 | `static void hotMonth(CapitalFlows f, int m, double gdp, double level)` | One month of hot money at a city paying 9% against a 2% world with a point of risk, GDP as given, a risk-off at this level. |
| 898 | 5 | `static CapitalFlows hotSettled(double gdp)` | Hot money settled to its target (a hundred and twenty months of calm). |
| 905 | 8 | `static CapitalFlows carrySettled(double headroom)` | A carry book settled to its target (a city paying 0.5% against a 2% world, a billion of headroom). |
| 915 | 5 | `static double riskOffAt(double level)` | What a risk-off of this level sends home from a settled hot money at 10,000 GDP. |
| 921 | 110 | `static void theUnwind()` |  |

### 9. IN A CITY: THE VAULT EARNS, MONEY RUNS HOME, THE AUDIT CLOSES (lines 1032-1177)

| line | len | member | says |
|---:|---:|---|---|
| 1040 | 9 | `static Game hotTown(String label, boolean quietForTheD)` | A town with banks to take hot money into, as CapitalFlowCheck's city has them. |
| 1051 | 11 | `static String playCaptured(Game city)` | One month of a city with what it printed captured. |
| 1063 | 114 | `static void inACity()` |  |

