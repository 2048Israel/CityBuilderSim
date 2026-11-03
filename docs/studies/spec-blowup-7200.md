# The money blow-up past month 7,200: the study, 2026-11-03

Read-only study of 0.9.3 (main at 5d987e56). It starts from the wealth study's summary item 10 (`spec-wealth.md`, on its branch). In two seeds of three, past month 7,200, the central bank lends the bank tens of thousands of years of GDP within a year. The bank's cash goes to -1.5e17, and the treasury, the fund and the bank's equity jump a thousandfold, while `MoneyAudit` still closes. Nothing in the suite plays past month 4,000, and Jerus's Rotterdam is at month 7,871.

The code is in [`blowup/`](blowup/), and its README has the run line:

- `BlowupRun.java` drives the wealth study's `WealthRun` unchanged: the ensemble's AUTO2 city, in `GameFiles.scratch()`. It writes the bank, the central bank's window and every audit line each month.
- `SectorProbe.java` writes each large sector's month, line by line.
- `HaulageProbe.java` writes Haulage's price rule term by term.
- `report.py` prints the tables, kept in `out/report.txt`.
- `prototype/make-haulage.py` writes the scratch `Haulage.java` of section 4.

No code of the game's was changed and no save was written. Jerus's saves were read with `SaveDump` and Python's JSON reader.

Every figure is **measured** (seeds 0-2, earth-2 and world-1 named, 0.9.3) or **derived**, with its sum shown. Money is in the game's own units, thousands of the city's dollars, as the getters and `SaveDump` give it. Rotterdam's treasury, 6.4e10 of them, is the F$63.8T the wealth study quotes. What matters is the ratio to a month's GDP (`EconomyManager.getMonthGdp()`), given beside each figure.

## 1. Summary for Jerus

1. **The cause is the trucks' price, not the bank.** Haulage, Freight's trucks (FR2), bills each shipper for the tonnes it carried, at a price per tonne: its costs over what its trucks can carry.
   - In both seeds Freight ran at a loss for decades with an empty till, so its orders for new trucks were never placed. The fleet wore away at a 120th a month, down to 0.000015 of a truck in seed 1 and 0.0000018 in seed 0.
   - Over a fleet that small, the price of a tonne is 7e9 (seed 1) and 8e10 (seed 0). That does no harm while the fleet carries a few kilograms.
   - The month Freight can pay again, it buys trucks, and the next month's tonnes are billed at the old price. Seed 1's month 7,386 billed 2.1e14, about 1,200 months of GDP. Seed 0's month 7,515 billed 2.7e16, about 137,000 months of GDP.
2. **The shippers cannot pay, and the insolvency rule forgives their overdrafts.** In seed 0, Copper Refining, Manufacturing, Mining and four others all go bankrupt in the same month; in seed 1 the bill falls on Construction. `restructure()` forgives every overdraft, whatever its size, and books it as a loss to creditors outside (`OverdraftForgiven`, a valuation line). So every dollar is booked and the audit closes, while Freight keeps the 2.4e16 as profit.
3. **The bank comes last.** Seed 0, from month 7,515:
   - Freight pays 12% profit tax on the windfall, and the treasury grows 33 times over in a month.
   - Its cash lands in the bank's deposits, which grow 5,000 times over.
   - Its market value goes to 4.4e17, and the bank's trading desk, which holds Freight's shares, marks them up.
   - The bank's equity and its room to lend follow. Within four months its cash is at -3.9e17, and the central bank's window lends it the gap.

   That is the thousandfold jump the wealth study saw.
4. **The price falls back only a quarter of the way each month** (`REPRICE_SPEED`), so the bills keep coming: seed 0 billed 2.7e16, 4.4e16, 5.3e16 and 5.7e16 in four months.
5. **Month 7,200 has nothing to do with it.** It fires whenever a fleet that has withered is bought back.
   - Seed 0 did it once before, at month 4,704: the fleet stood at 0.02 of a truck, the bill was 15 months of GDP and the year's forgiven overdrafts 8.3 years of GDP.
   - Seed 2 is sound because its fleet never fell under 164 trucks.
   - Freight loses money from about month 3,000 in every seed. The fleet first falls under one truck at month 4,236 in seed 0 and month 6,060 in seed 1.
6. **Your Malta (slot 7, month 4,202) is in that state now.**
   - It has 0.00025 of a truck, a quote of 2.45e6 a tonne (F$2.4 billion), every sector's haulage share at 0.00000006 and nothing in Freight's till.
   - The first month Freight can buy an eighth of its need, the next bill comes to about 4e12, some 5,400 months of Malta's GDP.
   - Rotterdam (slot 3) is sound: 2,484 trucks at 0.046 a tonne. So are slots 1, 2, 6, 8 and 9, and the autosave.
7. **It is not XI1.** XI1 is the currency intervention's impact (`ForeignAccounts`); this one is Haulage's price.
   - Rotterdam shows no haulage spike before month 4,716. Freight earned its usual 52,000-71,000 a month (F$52-71 million) through months 4,600-4,714, and the treasury spent cash it had saved.
   - The two bugs share only a shape: a rule with no bound, pushed far past the sizes it was built for.
8. **The fix, HQ1, is small.** The price is re-struck over the trucks that carry the month, so the money moves at `REPRICE_SPEED` and is always spread over today's trucks.
   - Prototyped on seeds 0-2 to month 8,000: no year after month 1,200 forgives more than 0.024 years of GDP, against 107,000 and 14,700 years as the game is.
   - The one fleet bought back in those runs (seed 1, month 7,296, from 0.00027 trucks to 175) billed 0.0003 months of GDP.
   - Two guards go with it: the playtest flags any month whose forgiven overdraft passes a month's GDP, and `HaulageCheck` gets a fixture that causes the case.
   - Three calls, each with a recommendation (section 5).

## 2. What was measured

### 2.1 The first month

`BlowupRun`, seeds 1 and 0, months 7,200-7,600, a row a month (`report.py` section 1). The first month with a forgiven overdraft larger than a month's GDP:

| seed | month | forgiven | months of GDP | deposits, month before / that month | profit tax, month before / that month |
|---|---|---|---|---|---|
| 1 | 7,386 | 9.7e13 | 555 | 1.6e12 / 1.6e14 | 1.8e10 / 2.2e13 |
| 0 | 7,515 | 2.4e16 | 120,160 | 4.1e12 / 2.1e16 | 1.5e10 / 2.9e15 |

The bank then follows, seed 0, months 7,515-7,519:

| | 7,515 | 7,516 | 7,517 | 7,518 | 7,519 |
|---|---|---|---|---|---|
| the desk (shares held) | 3.2e10 | 1.8e17 | 4.6e17 | 7.9e17 | 1.1e18 |
| the bank's cash | 2.6e11 | 4.8e15 | -6.9e16 | -2.1e17 | -3.9e17 |

The window opens at 9.9e16 in month 7,519 and peaks at 1.7e18 in month 7,530. Seed 1 runs the same course from month 7,386: the window opens in month 7,390 and peaks at 4.3e17 in month 7,403. These are the wealth study's months: its seed 1 between 7,344 and 7,392, its seed 0 between 7,488 and 7,536. Today's tree reproduces them.

### 2.2 The line: who billed whom

`SectorProbe`, seed 0, month 7,515, every sector whose month moved more than half a month's GDP (a month's GDP was 1.98e11):

| sector | the month's "Haulage" input, owed to Freight | forgiven |
|---|---|---|
| Copper Refining | 1.27e16 | 1.12e16 |
| Manufacturing | 8.30e15 | 7.30e15 |
| Mining | 3.62e15 | 3.19e15 |
| Automotive | 1.73e15 | 1.53e15 |
| Cement & Concrete | 7.10e14 | 6.25e14 |
| Industry | 1.69e13 | 1.49e13 |
| Food Processing | 4.10e12 | 3.61e12 |

Freight's side, "Haulage billed": revenue **2.71e16**, profit before tax 2.38e16, profit tax 2.86e15.

Seed 1's month 7,386 is the same with a single shipper. Construction was billed 2.09e14 for haulage and had 9.7e13 forgiven. Freight's "Haulage billed" was 2.09e14, and no other sector moved more than half a month's GDP. The month before, Freight billed about 3.1e8 in both seeds.

### 2.3 Haulage's price, term by term

`HaulageProbe` reads the rule's own fields each month (`report.py` section 2):

| seed | month | trucks | capacity, t | carried, t | quote a tonne | allowed | bill |
|---|---|---|---|---|---|---|---|
| 1 | 7,384 | 0.0000148 | 0.045 | 0.045 | 7.02e9 | 2.0e8 | 3.1e8 |
| 1 | 7,385 | 157 (bought) | 0.044 | 0.044 | 7.08e9 | 2.0e8 | 3.1e8 |
| 1 | 7,386 | 610 | 470,000 | 29,600 | 5.31e9 | 2.0e8 | **2.1e14** |
| 1 | 7,388 | 1,450 | 3,100,000 | 1,130,000 | 2.99e9 | 2.5e8 | 4.5e15 |
| 0 | 7,513 | 256 (bought) | 0.0054 | 0.0021 | 8.12e10 | 2.8e8 | 1.7e8 |
| 0 | 7,515 | 761 | 1,530,000 | 444,000 | 4.57e10 | 3.3e8 | **2.7e16** |
| 0 | 7,518 | 1,500 | 3,770,000 | 2,230,000 | 1.93e10 | 4.4e8 | 5.7e16 |

The rule is in `Haulage.closeMonth`, the reprice:

- The target is the month's costs plus a return on the rate base (`allowed`), over what the trucks could carry (`capacityInForce`, trucks x 3,000 t), times the scarcity (at most `MAX_SCARCITY_MULTIPLE`, 1.6).
- The quote walks a quarter of the way to that target each month.
- While the trucks are full, the bill is at most `allowed` x 1.6, which is about 3e8 here, as measured.

The bill, though, is struck at the start of the next month (`billMonth`), on the trucks held then, at a quote struck over the trucks of the month before. When the fleet grows ten-million-fold between the two, the bill grows with it: 29,600 t x 7.08e9 = 2.1e14 (derived).

### 2.4 Why the fleet withered

`HaulageProbe` yearly from founding, the game as it is (`report.py` section 3):

| seed | Freight's profit before tax | trucks at months 3,600 / 4,800 / 6,000 / 7,200 | first under one truck |
|---|---|---|---|
| 0 | negative from about month 3,000 (-3e5 to -2e8 a month) | 196 / 1,110 / 0.57 / 0.000025 | 4,236 (bought back by 4,704), again at 5,940 |
| 1 | negative from about month 3,400 | 6,370 / 3,990 / 1.6 / 0.000069 | 6,060 |
| 2 | negative from about month 3,600, positive again 4,600-6,000 | 5,880 / 3,340 / 6,440 / 9,310 | never (164 at month 4,368) |

How the fleet runs down:

- Freight's till sits at nought, often with its borrowing banned after a restructure.
- The market cuts every order to what the buyer can pay (`Markets.java:311`, `purchasesLeft()`), so Freight's truck orders are noted as forgone and never placed.
- The fleet wears at `1 / VAN_LIFE_MONTHS` (a 120th) a month (`Haulage.java:538`), and nothing replaces it. At 0.992 per month, that is a tenfold fall every 23 years (derived: ln 10 / ln(120/119) = 275 months).
- The shippers pay for the wear all along, since `worn x truckPrice` is part of `allowed`, but the money goes to Freight's other losses.

The trucks come back the year Freight can borrow again: both of seed 0's times, its borrowing ban was on at the year before and off at the year of the purchase. Freight then buys an eighth of its need (`Haulage.bid()`: 2,050 trucks needed, 256 bought), and the bill follows a month later.

Why Freight loses money from month 3,000 in every seed is not this study's question. Haulage's rule recovers its costs by construction, so the losses are elsewhere in Freight's books. It is a follow-up (section 6).

### 2.5 Seed 0's first one, at month 4,704

The same thing, smaller. The fleet fell to 0.021 of a truck by month 4,692, with a quote of 8.0e4 a tonne. By month 4,704 it held 2,150 trucks, and the month's bill was 3.2e10, 15 months of GDP. The year's forgiven overdrafts came to 8.3 years of GDP. Any series read across months 4,700-4,720 of seed 0 carries it.

### 2.6 Jerus's cities (read-only)

Read with `SaveDump` and Python's JSON reader; nothing written. Quotes are in game units, so 0.046 is F$46 a tonne.

| slot | city | build | month | trucks | quote a tonne | haulage share | Freight's till |
|---|---|---|---|---|---|---|---|
| 1 | Bergen | 0.8.10 | 3,002 | 16,380 | 16.8 | 1 | 2.5e10 |
| 2 | Andorra | 0.8.93 | 643 | 296 | 0.017 | 1 | 3.6e5 |
| 3 | Rotterdam | 0.8.95 | 7,871 | 2,484 | 0.046 | 1 | 3.8e7 |
| 6 | bergen | 0.8.90 | 2,418 | 6,601 | 0.47 | 1 | 2.5e6 |
| **7** | **Malta** | 0.8.83 | 4,202 | **0.000253** | **2,447,573** | **5.7e-8** | **0** |
| 8 | San Francisco | 0.8.71 | 4,845 | 13,220 | 9.9 | 1 | 1.3e11 |
| 9 | f | 0.8.35 | 10,043 | 24,190 | 0.16 | 1 | 3.9e14 |
| auto | Stockholm | 0.9.1 | 61 | 0.007 | 0.0001 | 1 | 0.005 |

- Slots 4, 5 and 10 predate Freight's trucks.
- Malta's need is 13.4 million tonnes a month (4,475 trucks), and its GDP is 7.6e8 a month. An eighth of the need bought (559 trucks, 1.68 million tonnes) and billed at the quote comes to about 4e12, some 5,400 months of its GDP (derived: 1.68e6 x 2.45e6 / 7.6e8). Even one truck would bill about 7.3e9, ten months of GDP.
- The autosave's town is young: its 0.007 trucks are the founding's fleet growing, not a withered one.

### 2.7 XI1, compared

The gold study's XI1 (`spec-gold-money.md` 4.5) bounds `ForeignAccounts.INTERVENTION_IMPACT`. That rule moved Rotterdam's currency x11,600 at month 4,716, after the treasury bought US dollars for F$137.6B. Rotterdam's history file (read-only) shows no haulage event there:

- Freight's net income was 51,700-70,700 a month through months 4,600-4,714. It rose only after the currency fell and prices went up four times.
- The treasury's cash had grown steadily to 1.37e8 (the F$137B), and the intervention spent it.
- The bank's deposits jumped 230 times over at month 4,720, after the intervention, not before it.

So **the two are different bugs, in different code.** XI1 is set off by a decision about the vault, this one by a fleet bought back. What they share is worth one line in each batch: a rule with no bound, which a rare state pushes far past what it was built for. Each batch bounds its own rule, and HQ1's flag in the playtest (4.2) catches the money such a rule makes, wherever it starts.

## 3. The cause, with file and line

What sets it off:

- **`sectors/Haulage.java:500`**, `billMonth()`: `double price = getQuote();`. The month's tonnes are billed at the quote struck at the last close, whatever fleet carried them. The capacity is re-read seventeen lines above (`:483`) and caps the tonnes (`:497`), but not the price.
- **`sectors/Haulage.java:968`**, `rawTarget()`: `allowed / capacityInForce * (scarcity)`. Cost over capacity, with nothing under the capacity, so a fleet of 0.0000018 of a truck prices a tonne at 8e10.
- **`sectors/Haulage.java:617`**: `quote += (target - quote) * Freight.REPRICE_SPEED`. What is smoothed is the price per tonne, so after a fleet is bought back ten-million-fold larger, the price comes down by only a quarter a month. Getting from 7e9 to 700 takes about 56 months (derived: ln(1e7) / ln(4/3)).

What sets it up:

- **`Markets.java:311`**: an order is cut to `purchasesLeft()`, so a Freight with an empty till buys no trucks.
- **`Haulage.java:538`**: the fleet wears at a 120th a month.

The path the money takes:

- **`BusinessDebtManager.java:2963`** and **`EconomyManager.java:1426`**: whatever overdraft a sector shows at the restructure is forgiven and added back to its cash.
- **`MoneyAudit.java:849`** books it as `+ sectors OverdraftForgiven`, a valuation from outside. So the audit closes on money that no one outside lost.
- From there the money goes through Freight's profit and its tax, its share price, the bank's desk (`Bank.markSecurities`, `Bank.java:1504`) and the window (`Game.java:12688`, `centralBank.settleWindow(bank.wholesaleFunding())`, which has no bound) to the bank and the central bank. Each works as built and carries what it is handed.

## 4. The fix: HQ1, the haulage price re-struck over the trucks that carry the month

### 4.1 The price (what sets it off)

`Haulage` keeps a new figure, `quotedOver`: the tonnes a month that the quote was struck over.

- **In `billMonth`:** when the trucks in force can carry more than `quotedOver`, the price is the quote x `quotedOver / capacityInForce`. That is the same money, spread over the trucks that carried the month.
- **At the reprice:** the quote and the steady quote (what a planner weighs a works at) are first carried over to the trucks in force the same way. Then they walk `REPRICE_SPEED` toward the target, and `quotedOver` takes the new capacity.

In effect the money is smoothed and the price per tonne is not. With it, the promise the code's own comment makes holds ("the bill was capped at the same capacity"): a month's bill is never more than the money struck x the scarcity.

`prototype/make-haulage.py --no-floor` writes it as five edits to a scratch copy of `Haulage.java`. It was run on seeds 0-2 to month 8,000 (`report.py` sections 4 and 5; years of GDP are the year's sums over the year's GDP):

| seed | version | worst year's forgiven (valuation), yrs GDP (month) | worst after month 1,200 | the window's peak, yrs GDP (month) |
|---|---|---|---|---|
| 0 | as is | **107,000 (7,524)** | 107,000 (7,524) | 320,000 (7,524) |
| 0 | HQ1 | 0.12 (144) | 0.022 (6,900) | 0.76 (120) |
| 1 | as is | **14,700 (7,404)** | 14,700 (7,404) | 368,000 (7,404) |
| 1 | HQ1 | 0.32 (84) | 0.024 (1,332) | 3.7 (228) |
| 2 | as is | 0.11 (648) | 0.016 (4,320) | 0.96 (492) |
| 2 | HQ1 | 0.13 (132) | 0.015 (6,432) | 1.0 (120) |

- The worst years left are a young town's, the same size as the game makes them (0.34 years at month 84 for seed 1 as is).
- The window is used only in the first century in every version, and stands at nought from month 1,200 to 7,300.
- In these runs the fixed rule met a withered fleet bought back once: seed 1, month 7,296, from 0.00027 trucks to 175. The quote went from 4.05e8 to 646 a tonne in that month, and the bill was 5.2e7, 0.0003 months of GDP. At the same yearly marks after its own buy-backs, the game as it is billed 34,800 and 58,500 months of GDP (`report.py` section 5).
- One case is little, so HQ1's own fixture (4.2) is what proves the bound.
- The fleets still wither under the fix (seeds 0 and 1: 0.0009 and 0.0005 trucks at month 7,200), because what sets the case up is untouched (section 6).

A second variant also floored the divisor at one truck's month (`MIN_TRUCKS_A_MONTH` x 3,000 t), so a withered fleet's quote reads 7e4 and not 7e9. It ends the blow-up too, but it moves the young town. A town is founded with 0.07 of a truck, so the floor cut a young town's price about fourteenfold, and seeds 1 and 2 forgave 3.1 and 3.0 years of GDP in months 84-96, against 0.34 and 0.05 as the game is. **Recommended: no floor** (call 2).

### 4.2 The guards

- **The playtest:** `LongPlaytest.run()` flags any month whose `getOverdraftForgiven()` passes the month's GDP, beside the conservation laws GT1 added. In the measured runs a normal month forgives 0.001-0.05 months of GDP; the blow-ups forgave 555 and 120,160.
- **`HaulageCheck`, a new section after 8 (the price):** a city whose Freight trucks are set to 0.00001 and repriced for a year, then given 200 trucks. The next month's bill must be at most `allowed` x `MAX_SCARCITY_MULTIPLE`, asserted against the constants, and the steady quote must fall the same month. The fixture causes the condition rather than waiting 7,000 months for it.

### 4.3 The save

`quotedOver` is saved in Freight's extras (`haul.quotedOver`) and read back. A save without it, Malta's among them, reads the old rule's own divisor in its place: `haul.capacity`, the capacity the saved quote was struck over. That is the same figure under its old name, not a conversion, so the format needs no bump. Malta then loads under the fixed rule, and its first bill is struck over the trucks that carry it (call 3).

### 4.4 What HQ1 leaves alone

- The forgiveness itself: who should bear a bill that one of the city's firms cannot pay another (call 1).
- The central bank's window, the bank's desk and its marks. Each carried a bad input as built, and the playtest's flag catches the next bad input wherever it starts.
- Why Freight loses money from month 3,000 (section 6).

## 5. The calls

1. **Who bears an unpaid bill between two of the city's own firms?**
   - Today `restructure()` forgives the shipper's overdraft and books it as a loss to creditors outside, so the supplier keeps the revenue and money is made.
   - (a) Leave it, with the playtest's flag (4.2) to catch the next runaway.
   - (b) Take a domestic supplier's unpaid bill off the supplier's revenue, so it bears the loss as a creditor would. That changes the insolvency rule and the audit.
   - **Recommended: (a), in HQ1.** (b) is right in principle, but it moves every restructure in every city and needs its own study of who the restructure's creditors are. Once HQ1 removes what sets it off, no measured year after month 1,200 forgave more than 0.024 years of GDP.
2. **A floor under the haulage price's divisor?**
   - **Recommended: no** (4.1). The re-strike alone bounds the bill, and the floor moves the young town.
3. **Malta's quote on load:**
   - (a) Read `haul.capacity` where `haul.quotedOver` is missing (4.3).
   - (b) Bump `SAVE_FORMAT` and refuse saves from before HQ1, your rule for an old save that would load wrongly.
   - **Recommended: (a).** It is the same figure under its old name, and (b) closes every city you have.

## 6. The batch, and what is left

| batch | what | size | the trace | waits for |
|---|---|---|---|---|
| **HQ1** | The haulage price re-struck over the trucks that carry the month (4.1); the playtest's flag on a month's forgiven overdraft, and `HaulageCheck`'s fixture (4.2); `haul.quotedOver` saved, with `haul.capacity` as its default (4.3) | small (Sonnet, the whole batch: `Haulage`, `HaulageCheck`, `LongPlaytest`) | moves: ensemble (the price moves a little every month the fleet changes) | nothing; it can run beside XI1 (different files) |

HQ1 belongs near the top of the queue: Malta is a live case, and any long city whose Freight loses money can reach it.

Left open, not queued:

- **Why Freight loses money from about month 3,000 in every seed.** Its empty till is what sets the case up, and it starves the city of trucks: every bulk sector's haulage share sat near nought for 15-26 years in seeds 0 and 1, and sits there now in Malta. A short study of Freight's railway and ports books would say whether that is a bug or a price.
- **Freight's truck replacements while its till is empty.** The shippers pay for the trucks' wear in the bill, but the money pays Freight's other losses. Whether Haulage's replacement orders should come first is a design question for that study, not for HQ1.
