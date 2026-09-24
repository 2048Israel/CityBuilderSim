# Batch 3 (0.7.9): the Bank tab redone - implementer's record

Working tree `/home/claude/cbs`, diffed against `/home/claude/cbs-b2d` (untouched). Nothing deployed, nothing written to the project. Fifteen files changed (list in section 3).

## Headline for the gate

- **The tab is rebuilt as the brief asked**: a landing that says whether the bank is healthy and why (a sentence, an eight-figure scorecard, the rate ladder), then five pages on one chip strip: Profit, Lending, Funding, Capital & owners, History. Every figure on it is a public model getter; the screen does display geometry and nothing else.
- **Nothing the bank decides moved.** LongPlaytest seed 0 (default) and seed 3 held at a 10% dial (35 bank failures, $1.2bn of rescues) are **byte-identical** to the cbs-b2d build apart from the "in 116s" timing line (`scratchpad/pt/old-*.txt` vs `new-*.txt`). The interest is booked by who paid it through a two-argument `takeInterest()` that adds the same sum in the same order.
- **Suite 59/60**: the only red is `InfrastructureCheck`'s known-fragile "...and its shops can actually be supplied" (shelves 77% against 100%). BankCheck, ReadPathCheck, SaveFileCheck and StaleCheck (0 firm findings) are green. Output: `/home/claude/runs/allchecks-b3.txt`.
- **`build-ui.sh` is silent. `endings-check.sh`: "all files keep their own".** `GameVersion.VERSION` is "0.7.9", with an entry in the house shape. `Build EXE.bat`'s findstr still matches one line. `SAVE_FORMAT` is unchanged (27).
- **The interface was not run.** It compiles against the JavaFX jars; everything below under "check by eye" is unverified on screen.

---

## 1. What was built

### The tab as a player sees it

**Landing: "THE COMMERCIAL BANK"** (the figures in these wireframes are illustrative)

```
THE COMMERCIAL BANK
Every loan in the city is its money, priced from what it costs the bank to make.

Healthy, and lending freely: its capital is 21.3% of its risk-weighted book, over the 16.5% it aims for.   <- Bank.status(), green/amber/red by stance

| PROFIT            | RETURN ON EQUITY          | CAPITAL RATIO              | CREDIT LOSSES              |
| $4.2M             | 23.80% a year             | 21.3%                      | 0.42% a year               |
| $51.0M over the   | over the last 12 months;  | [====|====|==|------]      | set aside, of its loans,   |
| last 12 months    | its owners want 12.5%     | target 16.5%, minimum 8.0% | the last 12 months         |
| INTEREST MARGIN   | COSTS                     | LENT OUT                   | DEPOSITS                   |
| 2.61% a year      | 38.0%                     | $3.2B                      | $12.4B                     |
| net, on what it   | of what it earns,         | at face value              | banked with it             |
| lent, last 12 mo  | the last 12 months        |                            |                            |

[alert: there is no bank]  [alert: the bank has failed + the rescue block]   (only when true)

THE LADDER OF ITS RATES
From the price of money to what each borrower pays. ...
  The policy rate  >          [bar]            1.50% a year      (click: Policy > Money > The policy rate)
     set by the central bank - and what the bank's reserves earn there
  What savers get             [bar]            0.53% a year
     -0.97 points on the policy rate. It aims to pass on 35% of it, because it holds reserves
     to spare, and moves a sixth of the way there a month.  [+ "Its margin could not pay..." when held]
  What a loan's money costs it [bar]           1.61% a year      (tooltip: the funds-transfer price)
     +0.11 points on the policy rate: the term premium on a 36-month loan[, and the central
     bank's 0.25-point penalty on the N% it borrows there]
  Prime                       [bar]            3.41% a year
     +1.80 points on that: running the bank +0.01 points, loans expected to go bad +0.40 points,
     the capital a loan ties up +1.39 points
  What each borrower pays to borrow now
  Manufacturing               [bar]            3.91% a year
     +0.50 points on prime: its own debts and record [- shut out for N more months]
  ...one rung per sector that owes the bank or is shut out...
  The families                [bar]            3.90% a year
     +0.49 points on prime, on average: their line starts at 3.31% a year and adds 1.50 points
     for every month of income a family owes
  The carry trade             [bar]            2.99% a year
     -0.42 points on prime: lent short and to borrowers who never default here, ...
  The city's own paper        [bar]            2.10% a year
     -1.31 points on prime: the city's rate, which the market sets on its credit ...

BEHIND IT
  [Profit            its income statement, and what it did with the profit      $4.2M this month / $51.0M over the last 12 months]
  [Lending           who owes it, what it has set aside, how the next loan ...   $3.2B lent / every borrower sound | N borrowers in trouble]
  [Funding           deposits, the central bank, and its branches                $12.4B deposited / nothing borrowed from the central bank]
  [Capital & owners  its capital against its target, its payout, its shares      21.3% capital / paying out 45% of its profit]
  [History           its rates, capital, returns and losses over time            480 months / recorded]
```

**Every page**: title "THE BANK - PROFIT" (etc.); four vitals (PROFIT this month, CAPITAL RATIO with "its target x%", PRIME, LENT OUT); the chip strip `Profit | Lending | Funding | Capital & owners | History`; the page; a "The bank at a glance" button back.

**Profit**

```
ITS INCOME STATEMENT                                   this month    last month
Interest earned  > from whom                            $10.2M        $9.8M
    From the businesses / From the families / From the city, on its paper /
    The discount on the city's paper, as it is earned / From the carry trade /
    On its reserves at the central bank                 (each this month and last)
Paid to savers                                          -$3.1M        -$3.0M
Paid for borrowing overnight from the central bank      -$0           -$0
NET INTEREST INCOME                                     $7.1M         $6.8M
Fees  > on what             (accounts / business loan fees / family loan fees, both months)
Provisions for loans expected to go bad  > what   (set aside or released; written off against it;
                                                   beyond it; what the allowance holds)
The trading desk  > what it did   (sold/bought, households/abroad, dividends, tendered, re-mark,
                                   what it holds, each company's share on the desk; the re-mark note)
Gains on the city's paper that changed hands     (only when either month had some)
Staff and branches  > what  (its staff / its branches' upkeep)
PROFIT BEFORE TAX
Tax
WHAT IT KEPT
  note: tax a month in arrears, on last month's $X
And what it did with it
Paid to its shareholders / Its own shares, bought back
KEPT IN THE BANK
  note: paid on last month's profit, by its capital - <payout decision>

THE LAST TWELVE MONTHS   (or "The N months on record" / "Its first month on record")
Net interest income, Fees, Provisions, The trading desk, Gains on the city's paper,
Staff and branches, PROFIT BEFORE TAX, Tax, WHAT IT KEPT, Paid to its shareholders,
Its own shares bought back, KEPT IN THE BANK

Read as ratios, over the last 12 months
Net interest margin, on what it lent / ...this month alone
Staff and branches, of what it earns
Return on its equity / ...this month alone / ...and what its owners want
```

**Lending**

```
WHO OWES IT          [stacked bar] businesses / city's paper / families / carry
The businesses, The city's own paper, The families, The carry trade, ON THE BOOK
THE BUSINESSES, ONE BY ONE
  sector | owed | pays (x.xx% a year) | leverage (amber past 0.90, red at 1.50) | stage | set aside | borrowing / Nmo shut
  note: leverage, the 0.90 watch line (stage 2), the 1.50 restructure line, written down to 60%
THE FAMILIES   owed; ...by families owing more than 3 months of income; stage; set aside;
               drawn / repaid / discharged this month
WHAT IT HAS SET ASIDE   against the businesses / the families; THE ALLOWANCE; this month's
               provision; written off this month; ...and over the N months on record
WHO HAS STOPPED PAYING   sector | this month | in total | restructured | status
WHAT IT LENT THIS MONTH   to the businesses / drawn by the families / to the carry trade
  What its capital lets it lend: "Lends freely." | "Rebuilding capital - a borrower's debt may
  grow 0.85% this month." (+ "...about this much growth across its book") | "Under its minimum..."
HOW THE NEXT LOAN'S RATE IS BUILT   what the money costs it; running the bank; the loans expected
  to go bad; the capital a loan ties up; PRIME; ...and what each borrower pays over it
  (sector | its own risk | it pays; the families on average; the carry trade)
WHAT A DOLLAR OF THE BOOK WEIGHS
  book | at face | term | risk weight | weighs   (businesses, city's paper, families, carry, desk's shares)
  WEIGHED FOR RISK AND TERM  = getWeightedBook()  (foots, desk included)
```

**Funding**

```
WHAT THE CITY HAS BANKED WITH IT   [bar] families / businesses / abroad; the three; DEPOSITS
WHAT ITS BRANCHES CAN REACH   one branch reaches $X (today's money); its N branches reach; the
  city's own savings; ...within reach; ...beyond it; ...and money from abroad; DEPOSITS IT CAN
  LEND AGAINST   [hot-money alert when there is foreign money]
WHAT IT PAYS SAVERS   the policy rate; the share its funding asks it to pass on; the rate it chose;
  what savers were paid (amber if its margin held it); why; paid to families / businesses / abroad
ITS ACCOUNT AT THE CENTRAL BANK   reserves held there, earning the policy rate, paid this month;
  borrowed overnight, at the window's rate, cost this month; note: "the window" explained
HOW ITS LENDING IS FUNDED   [keyed bar] its own capital / savers' deposits / money from abroad /
  the central bank overnight; the four lines
WHAT IT CAN CARRY   what its capital carries at 8%; what its deposits carry, lent 6 times over;
  THE TIGHTER OF THE TWO ("nothing - it has failed" when failed); its book weighs; which is N%
ITS BRANCHES   standing; one more would add; costs to build; its owners put in; a branch cost to
  run last month; its share of what the book kept
  Would another one pay?  Is anything spilling over? / Would it earn its keep? / Is the bank past
  70% of what it can carry?  -> verdict sentence   [Build a Commercial Bank >]
```

**Capital & owners**

```
ITS CAPITAL   [wide band bar: fill, ticks and labels at 8% / target / top]
  Equity; its risk-weighted book; CAPITAL RATIO; the minimum; its own target
  "It chose the minimum and <why - worst year / standard buffer / cap>."; the top of its band
  [alert when under the minimum, with what takes it back to its target]
WHAT IT DOES WITH ITS PROFIT   <payout decision>; last month's profit after tax it pays on; held
  over its target; past the top; dividends this month / over 12 months; own shares bought back
  this month / over 12; new shares issued this month / over the year; the rule in a note
HOW ITS EQUITY MOVED THIS MONTH   at the start; what it kept; then only the causes that moved it
  (shareholders' capital, the city's rescue, the founding settlement, dividends, buybacks, new
  shares, absorbed on failure, the treasury's buyback gain, an older save's allowance);
  Not accounted for (always printed); AT THE END OF IT  [alarm only if it is not zero]
ITS OWNERS   (the sector screen's owners block: households / abroad, book value per share, the
  desk's quote, yield, market value, dividend, the share-price chart) + "the treasury holds none"
ITS RESCUES   times failed; what its creditors absorbed; what the city has put in
  [failed: THE BANK HAS FAILED + the rescue block]
```

**History**

```
ITS RATES                     chart: the policy rate / what savers got / prime  (x.xx% a year)
ITS CAPITAL AGAINST ITS TARGET  chart: capital ratio / its target (x.x%); months under its target
                              N of M; months under the minimum N of M
ITS RETURN ON EQUITY          chart (x.xx% a year); months it lost money N of M
WHAT IT SET ASIDE, AND WHAT IT WROTE OFF   chart (money); its worst year of provisions $X;
                              ...as its capital target reads it: x.x% of its risk-weighted book
WHAT IT LENT, AGAINST WHAT IT COULD   chart: lent out / what it could carry / deposits
ITS FEES                      chart
```

### Model getters added (all with javadoc)

`Bank.java`, new banner **WHAT THE BANK TAB READS (0.7.9)** at L3708:

| getter | what | replaces on the screen |
|---|---|---|
| `takeInterest(fromCity, fromBusinesses)`, `getInterestFromBusinesses/City/Households()`, `getDiscountAccreted()` | the interest by who paid it (with `getCarryInterest()`, `getPlacementIncome()` the whole of `interestIncome()`) | "cannot be split further" |
| `enum Line`, `thisMonth(Line)`, `lastMonth(Line)`, `knowsLastMonth()`, `overYear(Line)`, `averageOverYear(Line)`, `monthsInYear()`, `statementYearToSave()`/`restoreStatementYear()` | the year of statements: each month filed whole at the next `startMonth()` (late items in), 11 months kept, saved | nothing (new) |
| `revenue()`, `getRetained()` | revenue before provisions and costs; what it kept after paying owners | - |
| `returnOnEquityOverYear()`, `provisionRateOverYear()`, `netInterestMarginOverYear()`, `costShareOverYear()` | the scorecard's year ratios | - |
| `record Ladder` + `ladder(policy)`; `saversOverPolicy()`, `transferOverPolicy()`, `primeOverTransfer()`, `parts()`, `overPrime(rate)` | the rate ladder in one read, and its steps | the build-up and every spread |
| `status()`, `targetReason()` | the state in a sentence with its deciding figure; why the target | - |
| `enum Book`, `record WeightRow`, `weightTable()` | face, term, risk weight, weighted, desk included; foots | the L689 table (no term, no desk) and the negative "relief" |
| `getHouseholdDeposits()`, `getSectorDeposits()`, `getDepositsPerBranch()`, `getPaidInPerBranch()`, `branchReach()`, `localDeposits()`, `localDepositsReached()`, `localDepositsBeyondReach()`, `fundingLimit()` | funding, in today's money | `DEPOSITS_PER_BRANCH`/`PAID_IN_PER_BRANCH` (a reform's factor out), reached/beyond, `depositsGathered()*LEVERAGE` |
| `capacityAnotherBranchWouldAdd()`, `overflowPastComfortable()`, `runningCostPerBranch()`, `keptPerBranch()` | the two halves of `wantsBranch()`; `bookAnotherBranchWouldCarry()` and `branchWouldPayForItself()` now call them (same arithmetic, same order) | the screen's gain and overflow |
| `record EquityMovement` + `equityMovement()`, `residual()`; `getTreasuryBuybackGain()`, `getAllowanceOpened()`, `isMonthKnown()` | the equity articulation with every cause, founding settlement included | the screen's gap formula |
| `getBailoutsLifetime()` (in the solvency record), `getBooksWatched()` | rescue history; borrowers in trouble | - |

Elsewhere:
- `Game.canRecapitaliseBank()` (L1898): the rescue's guard.
- `HistorySave.monthsUnder(series, line|level)`, `monthsRecorded()`, `total()`, `worstYear()` (L861-896): the history statistics.
- `HouseholdBalance.averageRate()` (L3484): the families' debt-weighted rate.
- `Equity.deskShare(c)` (L710): the desk's share of a company. SectorScreen's owners block reads it too.
- `Exchange.deskSoldToHouseholds()/deskSoldAbroad()/deskBoughtFromHouseholds()/deskBoughtFromAbroad()` (L955): the desk's trading without the bank's own shares. `BankCheck.deskParts()` reads the same four.

**The save.** One new key, `DataSave.bankStatementYear`. The month's lines grow 44 -> 50: the interest by who paid it, the treasury's buyback gain, and whether the lines are a month's; they are read by length. The solvency record grows 3 -> 4 (the lifetime rescues), also read by length. An older save loads with no year on file: the last-month column shows "-", the twelve-month figures cover the months since the load, and the lifetime rescues count from the load.

**The toolkit.**
- `Statement.bookTotal()` moved here from SectorScreen (same code; SectorScreen now calls the toolkit's).
- `Statement.opens()`, with `Set<String>` overloads of `bookLine(...detail...)` and `statementDisclosure(...)`: an opened line stays open through the clock's redraw. A null set keeps the old behaviour, so the other screens are unchanged.
- `Pieces.trendChart(..., DoubleFunction<String>)` writes rates and ratios on charts.

**Found wrong on the old tab, each fixed:**
- Per-branch figures in founding money: now `getDepositsPerBranch()`/`getPaidInPerBranch()`.
- The weight table did not foot: now `weightTable()`.
- The negative "relief": gone; the table foots instead.
- "99900.0% capital" with nothing lent: the screen shows "nothing lent" or "failed" wherever `getWeightedBook()` is 0 or the bank has failed.
- The under-minimum alert: fires on `payoutStance() == UNDER_MINIMUM`.
- Leverage warned at 2.0: now amber past `SECTOR_WATCH_LEVERAGE` and red from `INSOLVENCY_TRIGGER`.
- The Income note that interest "cannot be split further": it is split now.
- The history note's premium-era comparison: replaced.
- "Deposits" had three meanings: now `getDeposits()` = "Deposits", `depositsGathered()` = "Deposits it can lend against", `depositFunding()` = "Savers' deposits it has lent out".
- "IT CAN CARRY: capital is the limit" beside a frozen $0: now "nothing - it has failed".

Every value the screen used to compute for itself, as listed above, is now a getter.

### Harnesses

- **BankCheck (13) `whatTheBankTabReads()`** (L1765):
  - The ladder's four parts equal prime at six dials (0 to 15%), and every rung is the bank's own rate. The FTP step is the window penalty on its share plus the term premium, and the capital part is nothing past the owners' return.
  - The weight table foots to `getWeightedBook()` with the desk on it and the term shown, where face times risk did not.
  - A branch's reach is a hundredth after a 1:100 reform.
  - `status()` names each of the five states with its deciding figure.
  - Each interest payer lands on its own line.
  - **On a played city from its founding month, 72 months, with a treasury buyback and a rescue between presses:**
    - the equity's residual is 0 every month and between the presses;
    - the interest by who paid it is the whole interest every month;
    - last month's column is exactly what that month read;
    - the year is the last twelve months read;
    - the weight table foots every month.
  - The rescue guard: offered only when needed and the treasury holds all of it.
- (12)'s fabricated 0.7.7 save also drops the new key, since a real one would not have it. The premise is unchanged.
- **ReadPathCheck**: every new getter is in the sweep, and the year of statements, the solvency record and the known-month flag are in the bank's fingerprint.
- **SaveFileCheck**: last month's profit, the year's interest, the businesses' interest and the whole year of statements round-trip.

---

## 2. Check by eye on the PC

Open a played city (and, separately, a fresh one) on the Bank tab. What each place should show:

1. **Landing, top.** The status sentence is one bold line (two at most), coloured green, amber or red. Under it are two bars of four cells.
   - The CAPITAL RATIO cell has a small bar with three ticks (minimum, target, top).
   - The cell may be a little taller than its neighbours. If it pushes the bar out of line, say so.
   - The captions read "the last 12 months", or "the last N months" in a young city.
2. **Landing scroller.** It fills the stage under the scorecard, with no outer scrollbar when there is nothing to scroll and no empty band under it. The chrome is set to 330 at `showBankMenu()`'s `ui.scrolled(column, 330)`.
3. **The ladder.**
   - Bars grow down the list to the dearest rate, and all start at the same x.
   - Every figure reads "x.xx% a year".
   - Each caption carries a signed step in points ("-0.97 points on the policy rate...").
   - The policy rate row is blue with a "›", highlights on hover, and clicking it lands on Policy > Money > The policy rate.
   - Hovering "What a loan's money costs it" and "The carry trade" shows the explanation tooltips.
   - Sectors appear only if they owe the bank or are shut out.
4. **Landing, the five rows.** Each shows a headline figure and a small line under it. Clicking one opens that page at the top.
5. **Every page.**
   - The title reads "THE BANK - PAGE", with four vitals and the chip strip under it; the current chip is lit.
   - **Let the clock run for a few months on each page: the page must not change, the scroll position must hold, and any line you opened must stay open.**
   - "The bank at a glance" returns to the landing. Pressing the rail's bank icon also goes to the landing.
6. **Profit.**
   - The two-column statement lines up with the sector pages' statements. The bold totals are larger, and last month's column is grey.
   - Opening "Interest earned": six rows, each with two columns, which add up to the line (the discount row is usually small).
   - Opening "Provisions": three this-month rows and the note.
   - Opening "The trading desk": the desk lines, where the bought lines show a "−".
   - "Kept in the bank" can be negative in a month the bank paid out more than it earned; it is red then.
   - The twelve-month block and the ratios sit under it.
   - After loading a save made before this build, last month shows "—" and the twelve-month header reads "Its first month on record".
7. **Lending.**
   - The businesses table has seven columns and nothing overlaps. "pays" reads "x.xx% a year".
   - Leverage is amber past 0.90 and red at or past 1.50. Stage 2 is amber.
   - The families block is present only when they owe something.
   - The quotes grid lists each owing sector, then the families and the carry trade.
   - The weight table has five rows (the desk's shares last) and a total that matches "Weighed for risk and term".
8. **Funding.**
   - "One branch reaches" should be $250.0M in a city that has never reformed its currency, and a hundredth of that after a 1:100 reform.
   - Within reach plus beyond reach equals the city's own savings.
   - The savers' block explains the share. It is amber, with a "margin could not pay" sentence, when rule 2 held it.
   - The funding-mix bar has a key.
   - "What it can carry" reads "nothing - it has failed" for a failed bank.
   - The branch verdict is one sentence. "Build a Commercial Bank ›" opens Build > Commercial.
9. **Capital & owners.**
   - The wide band bar shows three labelled ticks, with no overlap between the target and top labels.
   - "It chose the minimum and ..." is one sentence.
   - The equity movement lists only the causes that moved it, then "Not accounted for $0" in grey, then the total. **A red alarm here is a bug to report**, with the month.
   - The owners block (share price chart included) and the rescues follow.
   - With a failed bank, the rescue block appears at the bottom, and at the top of the landing. The button is green only when the treasury can cover it.
10. **History.**
    - Six charts. The rate and capital charts' axis labels and legends read as percentages, not money.
    - The statistics lines: months under target, months under the minimum, and months it lost money, each "N of M"; the worst year of provisions.
    - A city under two months old shows "Nothing to draw yet".
11. **A city with no bank and a failed bank** (the held-10% playtest slot, or a save where the bank failed). Check the landing alerts, the red status sentence, and the "failed" figures in the CAPITAL RATIO cells.

---

## 3. Suite, build, endings

- `checks.sh /home/claude/cbs -q`: **59/60**. The only red line is InfrastructureCheck's "...and its shops can actually be supplied" (77% against 100%), the known-fragile one. Output: `allchecks-b3.txt`.
- `build-ui.sh`: silent (0 lines). `build.sh`: silent.
- `endings-check.sh`: "line endings: all files keep their own". The CRLF files (Bank, BankCheck, DataSave, Equity, GameVersion, HistorySave, HouseholdBalance, ReadPathCheck, SaveFileCheck) were edited with `jedit.py`. BankScreen was rewritten in LF, as it was. Game, Exchange, Pieces, Statement and SectorScreen stayed LF.
- `tools.Stale`: 0 firm findings. The soft ones touching these files are the pre-existing `ratePremium()` history prose and the stale `docs/map` pages.
- **Byte-identical playtests.** `LongPlaytest -Dplaytest.seed=0`, and `-Dplaytest.seed=3 -Dplaytest.policyRate=0.10`, each on cbs-b2d (compiled into the scratchpad) and on this tree. The only differing line is the run time.
- Files changed:

  | file | lines before | lines after |
  |---|---|---|
  | Bank | 3,654 | 4,192 |
  | BankCheck | 2,302 | 2,562 |
  | DataSave | 1,548 | 1,560 |
  | Equity | 822 | 828 |
  | Exchange | 1,062 | 1,077 |
  | Game | 9,629 | 9,648 |
  | GameVersion | 1,463 | 1,502 |
  | HistorySave | 1,098 | 1,158 |
  | HouseholdBalance | 3,855 | 3,867 |
  | ReadPathCheck | 631 | 703 |
  | SaveFileCheck | 1,295 | 1,310 |
  | ui/BankScreen | 1,728 | 1,610 |
  | ui/Pieces | 619 | 631 |
  | ui/SectorScreen | 1,469 | 1,430 |
  | ui/Statement | 379 | 453 |

---

## 4. Found on the way, and left undone

**Found (not changed unless stated):**

1. `ui/SummaryScreen.java:738`: the left panel's THE BANK flag still reads only strain (> 1, "n% lent"). Since 0.7.8, what rations the city's credit is the capital rule (rebuilding or under the minimum), and the flag says nothing about it. It is outside the tab, so I left it. The natural fix is `bank.status()`/`payoutStance()`.
2. `Bank.java:1908`: `netInterestMargin()` calls its denominator `weighted` but divides by the face book (`getBook()`). The name is misleading; the value is correct.
3. `Game.java:1878`: `recapitaliseBank()` puts in `min(amount, cash)`, so any caller can make a partial rescue. The tab now offers only a full one (`canRecapitaliseBank()`). Whether the advisor ever makes partial ones was not checked.
4. **Every screen's opened lines snapped shut on each clock redraw** (Statement's disclosures kept no state). Fixed for the Bank tab through `Statement.opens()`. SectorScreen, Finances and the rest still pass no set and still close every month. Each can adopt it with a one-line `Set<String>` field.
5. `HistorySave` records `bankCapitalRatio` as 10 (1,000%) when nothing is lent (L510). The chart shows those months at the ceiling, and "months under target" counts them as not under. The history note says so.
6. `Bank.status()` for a failed bank names 12.0% of the weighted book. `resolutionExitEquity()` is also floored at one branch's founding capital, which the sentence leaves out.
7. BankCheck (13)'s sound played city had no family borrowing in 72 months (so the families' interest line is caused on a fixture instead). Household credit is rare in a fed city.
8. `ui/UserInterface.java:84`'s "THE BANK ... ITS HISTORY (10) BankScreen" is the record of the 2026-09-18 split. BankScreen's banners are now different. That is for the docs pass (Stale does not flag it).
9. The families' rung reads the debt-weighted average of what families actually pay (`HouseholdBalance.averageRate()`), not only the starting rate. When nobody owes, it shows the starting rate and says so.

**Choices the gate may want to see:**

- **Spreads to two decimals** ("+0.50 points"), not the brief's "x.x points". The rates are x.xx%, and at one decimal the parts of prime would not visibly add up (0.40 + 1.02 + 0.98 against 2.40). This is the same reason `Money.pts()` went to two decimals.
- **Leverage colours:** amber from the watch line (0.90, stage 2), red from `INSOLVENCY_TRIGGER` (1.50). Both lines are the model's own.
- **The scorecard's ROE, margin, costs and credit losses are over the trailing year.** This month's ROE and margin are on the Profit page as "...this month alone".
- **"Written off since founding"** is the bank's history total over the months on record (`HistorySave.total("bankWriteOffs")`). The per-sector lifetime totals are in the "who has stopped paying" table.
- **The gauge and the two-limits bars are gone** (strain prices nothing since 0.7.7). Strain survives as a share under Funding's "What it can carry".

**Left undone:**

- The screen is unrendered. See section 2.
- `docs/map`, the harness index, the manual and the design note are for the docs pass and the orchestrator.
- The other screens' disclosures (finding 4) and the Summary flag (finding 1) are outside the brief.
- Nothing about sector defaults: the tab reads `capitalTarget()`, `capitalTop()`, `payoutStance()`, the allowance and the payout getters as batch 2 left them.
