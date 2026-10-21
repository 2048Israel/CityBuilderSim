# The central bank, decided in steps and kept as the Fed keeps its books: a design study

**The boards: [Central Bank Study](https://claude.ai/artifact/7UCd3e8od9pPVY87aYB8jf)** (a private artifact; it reads on a phone).

*2026-10-20. A read-only design study: no game code was changed. Jerus asked for it after the CD batches' questions the same day, in his words:* "this study doesnt go over the central bank right? im thinking that the dial should only be allowed to be steps so not 6.45% and then 5.48% and then 11.99% in the span of a month, but instead, every 3 months, its is calculated say 3.5-3.75 like in real world, and it works with what it can inside that... and on top of that, central bank remmintance being accurate aka fed balance sheet. and also currently idk why but in one occasion, central bank term paper holding is at 0%, yet central bank remittance was like 50% of revenues... which makes no sense i think and the city had cash so it wasnt overdrawn. basically also introducing the fed rate thing i forgot how its called... the overnight rate?"

*What was read:*
- *The code at `fd36e1a` (main, 0.8.11), read and not changed: `CentralBank` whole; `DebtManager`'s policy rate, autopilot, rule (`ruleRate()`, `advisedPolicyRate()`, `holdingRate()`), `floorAt()`, `bankFloorRate()` and `getRate()`; `Bank.fundToCover()`, its funding side (`borrowings()`, `depositFunding()`, `wholesaleFunding()`, `cashReserves()`, `fundingPosition()`, `windowShare()`); `Game.settleTreasury()` and the autopilot's line in `nextMonth()`; `PriceIndex.inflation()`; `Expectations`' lean; `ForeignAccounts`' vault; `ui/FinancesScreen.thisMonthCard()`; every reader of `getPolicyRate()`; `docs/month-order.md`; `spec-city-debt.md`.*
- *Jerus's saves, read and never written: the headers and central-bank state of slots 1 to 10 and the autosave, and the history of every slot. Only three load in this build (format 40): slot 1 (Bergen, month 3002), slot 3 ("for claude", Halifax, month 477) and the autosave (Halifax, month 469).*
- *Copies of slots 1 and 3, played on in a scratch folder (`GameFiles.scratch()`) by two probes outside the tree (`central-bank/probe/`): 120 months of Halifax splitting the central bank's profit into its parts, 3 months of Bergen, and four 240-month runs of Halifax with the dial on today's autopilot or in steps.*

*Every figure is printed by `central-bank/cb-model.py`, which copies the game's constants by name and marks the study's own as PROPOSED. The boards are `central-bank/central-bank-study.html`, built by `central-bank/build-boards.py`. [R#] are the sources at the end.*

---

## 0. In one page

**The remittance puzzle, answered.** The central bank's profit is struck in `closeMonth()` as `windowInterest + advancesInterest + paperCoupons + paperGains - interestOnReserves` and paid to the treasury the next month. With no paper held, only two lines can be large, and both were measured in Halifax's history month by month (§1.4):
- **With cash in the treasury, it is the window's interest**: the commercial bank borrowing past its deposits at the dial plus 0.25. Each month's remittance equals last month's window loans times (dial + 0.25%) / 12, within 5%, in 25 of 33 such months. In Halifax it reached **14% of revenue** (Sep 2036: D$3,645 on D$748k lent at the window).
- **With the treasury overdrawn, it is the treasury's own interest coming back**: the overdraft is charged the dial, the charge is the central bank's profit, and the profit is remitted. In Halifax it was over a quarter of revenue in 37 months between Jan 2022 and Nov 2025, at most **44%**. The city's fund was empty throughout.
- **No screen shows either.** The Finances "This month" card lists money destroyed as paper sold, coupons, principal and bought back; the window's interest and the overdraft's interest are not on it, and nothing else shows the central bank's income. So the remittance looks like money from nowhere.
- Two more things are wrong with the books, found on the way. **Bergen's central bank pays interest on D$11B of the bank's spare cash and owns nothing that earns**: it has carried a loss of D$35.2B, growing D$17-23M a month, which it will never earn back. And a bond the central bank buys below face is **profit the day it buys it**, at face.

**The dial today.** The autopilot sets the dial every month to the rule on year-on-year inflation: 3% plus 1.5 times inflation's distance from 2%. Halifax's year-on-year inflation ran from −11% to +15%, and one month's prices moved as much as 8.5%, which moves year-on-year inflation 8 points at once and again a year later when that month drops out. So the dial took **248 different values in 463 months**, once moved **12.3 points in a month**, and went from 22.6% to 0% in two and a half years. A copy played 20 years on moved it 230 times.

**The proposal in one sentence.** The central bank sets a target range a quarter point wide at decisions every two months, moving it in quarter points; the overnight rate trades inside the range, at the rate on reserves while the bank holds spare cash and up to the window as it borrows there; everything priced off the dial reads the overnight rate instead; and the central bank keeps an income statement whose bottom line is the remittance, or a loss carried as the Fed carries its deferred asset.

**The pieces:**
- **Decisions in steps (§2).** A range like 3.50-3.75%. Decisions on a calendar, every two months by default (Jerus said three; both measured). At a decision the autopilot moves toward the rule in quarter points, at most ¾ of a point, and holds if the rule is within an eighth. An unscheduled decision when the rule runs 3 points from the overnight, by up to a point. The player's range waits for the next decision unless they press Decide now. In a copy of Halifax played 20 years on, this moved the rate 99 times instead of 230, never more than a point, and held inflation as well as today's autopilot (§2.6).
- **The overnight rate (§3).** The overnight reverse repo at the bottom, interest on reserves ten points under the top, the window at the top, as the Fed sets them [R1]. The overnight rate reads the bank's own funding position, so no new dial. Every rate in the city that is built on the dial is built on the overnight instead; the bank's spare cash earns the reserve rate and its borrowing pays the window.
- **The books as the Fed keeps them (§4).** Interest earned on what it lent and holds, less interest paid on reserves and reverse repos, less operating costs, is the remittance; a loss is carried and recovered first (the game's `lossCarried` already does this, checked in §4.4). Paper at amortized cost. Every line on the screen.
- **What it does to the debt study (§5).** CD1's expected dial becomes a staircase: flat to the next decision, stepping at each. It matters only far from the rule (a city held at 1% by hand: −43 points of a hundredth at six months); a calm or hiking city moves 3 or less. CD5's floating notes reset on the overnight.

**Three batches (§7), CB1 to CB3,** proposed and not queued (Jerus decides in the morning): CB1 and CB2 before CD1, so CD1 prices on decisions in steps from the start, and CB3 after CD3, whose effective interest it shares. Fifteen decisions wait for Jerus in §8.

---

## 1. What the game does today

### 1.1 The dial moves every month, to any value

The autopilot's line, at the top of every month before anything is priced (`docs/month-order.md` step 46):

    if (debtManager.isAutopilot() && priceIndex.hasRate())
        debtManager.setPolicyRate(debtManager.advisedPolicyRate(priceIndex.inflation()));

`advisedPolicyRate()` is the rule held inside 0 to 100%: at Standard and the default target, **3% + 1.5 × (inflation − 2%)** (`NEUTRAL_RATE`, `TAYLOR_WEIGHT`). `PriceIndex.inflation()` is the index over the index twelve months back (`WINDOW` 13). Nothing rounds it and nothing paces it.

Halifax's history (slot 3, read from the save, `central-bank/halifax.csv`):

| | |
|---|---|
| months where the saved dial is the rule on last month's year-on-year inflation, to the hundredth | 380 of 463 (the rest: the dial in the player's hand) |
| different values the dial took | 248 |
| the biggest move in one month | 12.32 points |
| year-on-year inflation | −11.1% to +15.3% |
| the biggest one-month price move | 8.5% (year-on-year inflation moved 8.2 points that month) |
| the dial, Jun 2018 → Dec 2020 → Aug 2022 → Oct 2026 | 22.58% → 0.00% → 14.53% → 0.00% |

**Why it jumps.** Year-on-year inflation leaps the month a large price move enters the twelve-month window and leaps back the month it leaves, and the rule multiplies the leap by 1.5. The city then answers the rate with a lag, which is the shape of the history: a boom, a 20% dial, a slump into deflation, a 0% dial, a boom again.

**In the current build** (a copy of slot 3 played on, `central-bank/halifax-replay.csv` and `halifax-240-today.csv`):

| | 120 months | 240 months |
|---|---|---|
| dial range | 0.00-8.29% | 0.00-10.48% |
| months it moved a quarter point or more | 44 of 119 | |
| moves | | 230 |
| different values | 102 | 227 |
| the biggest move in a month | 2.18 points | 3.26 points |

### 1.2 What the dial prices

Every rate in the city is built on `DebtManager.getPolicyRate()`, read directly:

| what | where | today |
|---|---|---|
| the bank's spare cash at the central bank | `Bank.fundToCover()`: `placementIncome = cashReserves() × policy / 12`; `CentralBank.payInterestOnReserves()` | the dial |
| the bank's funding past its deposits (the window) | `fundToCover()`: `wholesaleFunding() × (policy + WINDOW_PENALTY) / 12`; `CentralBank.chargeWindow()` | dial + 0.25 |
| the treasury's overdraft | `Game.settleTreasury()`: `advancesInterestDue(policy)` | the dial |
| the city's own rate: the curve's floor and its short end | `DebtManager.floorAt(policy)` → `floorRate()`, `priceAt()`, `getRate()` | the dial, or the bank's cost of funds if dearer |
| prime, insured mortgages | `Bank.prime(policy)`, `insuredMortgageRate(policy)` → `EconomyManager.updateBusinessCredit()` | dial + spreads |
| household loans, car loans | `Bank.householdRate(policy)` (`Game.syncHouseholdAccounts()`, `Motoring`) | dial + spreads |
| what savers are paid | `Bank.chooseDepositRate(policy)`: a share of the dial by funding position | dial × share |
| the carry trade, hot money | `Bank.carryRate(policy)` (`Game` step 81) | dial-based |
| the real rate the currency reads | `Game.realDepositRate()`, `checkForeignSolvency()`: policy − expected inflation | the dial |
| credibility's lean | `Expectations.takeMonth(..., policy, ..., holdingRate)` | the dial against what holding the target takes |
| the advisor's "the dial is under the rule" | `CityNeeds` | rule − dial |
| the bank's preferred shares and warrants | `Game.settleThePreferred()`, the warrant's value | the dial as the risk-free rate |

### 1.3 The central bank's books

**Its balance sheet** (`CentralBank`): assets are the window's loans to the commercial bank, the advances to the treasury, the city's paper at face, the redemption due, and the vault (read from `ForeignAccounts`). Liabilities are reserves and currency (zero), which are M0. Reserves are a ledger of what this bank has made and not taken back, not the commercial bank's till (the header's RESERVES ARE A LEDGER).

**Its month**:
- `monthProfit() = windowInterest + advancesInterest + paperCoupons + paperGains − interestOnReserves`.
- `closeMonth()`, at the bottom of the month: a profit first pays down any loss carried, and the rest is owed to the treasury; a loss is added to `lossCarried`.
- `remit()`, at the top of the next month inside `settleTreasury()`: what is owed is paid, as money made, onto the revenue line "Central bank remittance".
- `paperGains` is the paper's face less its price, the day it is bought (`buyPaper()`, `buyAtIssue()`), and price less face the day it is sold. The paper is held at face.
- There are no operating costs.

**Interest on reserves is paid on the commercial bank's spare cash** (`Bank.cashReserves()`, its cash when positive), not on what the central bank made. That is a deliberate choice (the header explains why the two differ in this city), and it has two consequences the remittance shows:
- **Money the central bank makes costs it nothing unless it lands in the bank's spare cash.** A window loan covers a bank that is overdrawn; a treasury advance is spent into the city; paper bought from a bank whose cash is negative only makes it less negative. None of the three raises spare cash, so the central bank earns the asset's full rate on each and pays nothing for the money that bought it.
- **Spare cash the central bank never made still earns the reserve rate**, paid in money made for it. In Bergen the bank holds D$9-11B of cash that came from abroad and the founding, and the central bank owns nothing that earns.

### 1.4 The remittance puzzle, measured

**In the saved history** (Halifax, slot 3), each month's remittance against last month's balances:

| remitted in | the treasury | what the central bank had lent the month before | at the dial then | remittance | of revenue | balance × rate / 12 |
|---|---|---|---|---|---|---|
| Sep 2036 (m441) | cash, no overdraft, no paper | D$748,353 at the window | 5.63% + 0.25 | D$3,645 | 14% | D$3,667 |
| Jan 2022 (m265) | overdrawn | D$50,153 advanced | 9.69% | D$393 | 27% | D$405 |
| May 2022 (m269) | overdrawn | D$56,355 advanced | 15.36% | D$701 | 41% | D$721 |
| Sep 2022 (m273) | overdrawn | D$62,547 advanced | 14.53% | D$739 | 42% | D$757 |
| Jan 2025 (m301) | overdrawn | D$105,460 advanced | 4.90% | D$424 | 16% | D$431 |

The fit holds within 5% in 25 of the 33 months with a remittance and no overdraft, and 58 of the 85 with an overdraft; the misses are months where the balance moved inside the month. The window's months topped out at 14% of revenue and the overdraft's at 44%.

**In the current build** (a copy of slot 3 played 120 months on, with the profit split into its parts every month, `halifax-replay.csv`): the treasury kept cash, never drew on the central bank, the central bank bought no paper, and the bank touched the window in one month. The remittance over the ten years was D$418, all of it the window's interest, on D$8.9M of revenue.

**In Bergen** (a copy of slot 1 played 3 months, `bergen-replay.csv`): the central bank paid D$23.3M, D$19.1M and D$17.5M of interest on reserves on income of nothing. Its loss carried went from D$35.19B to D$35.25B, and its reserves equal its loss carried to the dollar: every dollar on its books is money it made to pay that interest.

**In the older saves** (played on 0.7.23 to 0.8.0, the same rule): slot 6 holds D$3.87M of the city's paper, the bank's cash is −D$1.75M, and the remittance is D$8,180 a month, a fifth of revenue: coupons, with nothing paid on the money that bought them. Slot 9 remitted 47 to 59% of revenue at a 0% dial, which only paper can do at 0%. Slot 5 carries D$21.3M of loss, Bergen's case.

**So the answer to Jerus's question.** If the treasury had cash and the central bank held no paper, the remittance was the commercial bank's window interest. If the treasury was overdrawn, even briefly at the top of a month, it was the treasury's own overdraft interest returning. In Halifax the second is what reached 40-44% of revenue. Neither shows on any screen. Real central banks earn very little from their windows, because real banks rarely borrow there: in this city the commercial bank borrows there whenever it lends past what its branches gather. That makes the game's central bank a refinancing central bank, as the ECB was until 2008 [R8], which is a defensible design, but it needs to show on screen (decision 15).

---

## 2. Rate decisions

### 2.1 How often

| central bank | scheduled decisions a year |
|---|---|
| Federal Reserve (FOMC) | 8 |
| European Central Bank | 8 |
| Bank of England | 8 |
| Bank of Canada | 8 (fixed announcement dates) |
| Bank of Japan | 8 |
| Reserve Bank of Australia | 8 (11 until 2024) |
| Swiss National Bank | 4 (a quarterly assessment) |

The game's clock is a month, so eight a year cannot be spaced evenly. **PROPOSED: every two months, six a year** (`DECISION_MONTHS` 2), in January, March, May, July, September and November. Jerus's every three months is the SNB's schedule and the other setting (decision 1). The decision months are fixed and shown ahead, as real ones are published a year ahead.

### 2.2 The range and the step

**PROPOSED:** the rate is a **target range a quarter point wide** (`RANGE_WIDTH` .0025), its bottom on a quarter point: 3.50-3.75%. It moves only in quarter points (`STEP` .0025). The range's bottom is never under zero: 0.00-0.25% is the floor, as the Fed's was from 2008 to 2015 and in 2020-22.

**Which range the rule picks.** The rule's rate is the overnight rate it wants. The committee picks the range that puts the overnight, in a flush city, nearest it: the one whose rate on reserves (§3.1, ten points under the top) is within an eighth of the rule. A calm city's rule says 3.00%; the range is 2.75-3.00% and reserves earn 2.90% (decision 4).

### 2.3 At a decision, and between

**At a decision** the autopilot reads the rule on year-on-year inflation, as today, and compares it with the overnight rate's level in a flush city:

    gap  = rule(inflation) − (range top − IORB_UNDER_TOP)
    hold   if |gap| ≤ an eighth of a point
    else   move the range by the gap rounded to quarter points, at most MAX_STEP (.0075)

So a decision holds, or moves a quarter, a half or three quarters. The Fed moved three quarters four times in 2022 [R7].

**Between decisions** the range holds. The rule's advice is still struck every month, and the screen says what the next decision would do on today's inflation ("on 3.3% inflation: down ½ next"). The overnight rate moves inside the range with the bank's funding (§3), so money's price still answers the month, within a quarter point.

**Credibility's lean.** `Expectations` keeps credibility as far as the bank leans against a miss, measured against what holding the target takes. A stepping autopilot sits short of the rule between decisions, and would read as not leaning. **PROPOSED:** a decision that moved as far as its step allowed, toward the rule, counts as leaning fully until the next decision (decision 8).

### 2.4 Bigger moves, and unscheduled ones

Central banks move between meetings in emergencies: the Fed cut half a point on 3 March 2020 and a point on 15 March [R7]; the Bank of Canada raised a full point at a scheduled meeting on 13 July 2022 [R5]. **PROPOSED:** when the rule runs **3 points** or more from the overnight between decisions (`EMERGENCY_GAP` .03), the autopilot decides at once, by up to **a point** (`EMERGENCY_STEP` .01); at a scheduled decision with a gap that large, the step may be a point too. In the copy of Halifax played 20 years on, that happened three times (§2.6).

### 2.5 The player's hand

The Policy tab's rate becomes a range picker in quarter points (board 1). A range set by hand **takes effect at the next decision**; **Decide now** makes it an unscheduled decision at once, by any number of quarter points. Moving the range takes the rate from the rule, as `takeTheDial()` does today; handing it back takes effect at the next decision. Each decision goes in the `DecisionLog` with its month, its move and whether it was scheduled (decision 7).

### 2.6 Measured

**On Halifax's past inflation** (open loop: the city's inflation as it happened, which does not answer to the stepped rate; from `cb-model.py` §1):

| rule | moves | values | biggest move | mean distance from the rule | months over a point from it | unscheduled |
|---|---|---|---|---|---|---|
| today, as saved | 277 | 248 | 12.32 | 38 bp | 59 | |
| every 2 months, ½ at most | 157 | 37 | 0.50 | 253 bp | 193 | |
| every 3 months, ¾ at most | 109 | 37 | 0.75 | 257 bp | 199 | |
| every 3 months, a point at most | 107 | 39 | 1.00 | 234 bp | 187 | |
| every 3 months, ¾, unscheduled past 3 points | 159 | 64 | 1.00 | 146 bp | 196 | 51 |
| **every 2 months, ¾, unscheduled past 3 points (PROPOSED)** | **173** | **58** | **1.00** | **124 bp** | **151** | **35** |

On the past's inflation no step rule keeps up: that inflation swung 26 points, and partly because of the dial that answered it. So the question is what a stepped rate does to the city, which only the game can say.

**In the game** (copies of slot 3 played 240 months on, Oct 2039 to Sep 2059, the dial set by a probe to the range's reserve rate; one run each, `cb-model.py` §1b):

| rule | moves | values | biggest | dial range | inflation | its spread | off 2%, on average | months 3+ points off | unemployment | GDP a month | people at the end | unscheduled |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| today's autopilot | 230 | 227 | 3.26 | 0.00-10.48% | 2.48% | 1.41 | 1.09 pt | 19 | 8.5% | D$494k | 81,746 | |
| every 3 months, ¾, unscheduled past 3 | 70 | 26 | 1.00 | 0.15-8.40% | 2.64% | 1.52 | 1.26 pt | 29 | 12.0% | D$543k | 96,792 | 6 |
| every 3 months, a point, never unscheduled | 68 | 28 | 1.00 | 0.15-8.15% | 2.58% | 1.63 | 1.32 pt | 29 | 11.8% | D$515k | 91,103 | 0 |
| **every 2 months, ¾, unscheduled past 3 (PROPOSED)** | **99** | **30** | **1.00** | **0.15-8.40%** | **2.68%** | **1.39** | **1.27 pt** | **18** | **12.1%** | **D$549k** | **97,195** | **3** |

- **The rate stops lurching.** Under half as many moves, an eighth as many values, never more than a point.
- **Inflation is held about as well.** Every two months matched today's control on the spread and on months far off target (18 against 19); every three months let 29 months stray 3 points or more.
- **The city grew faster** in every stepped run: GDP 4 to 11% higher on average, 9,000 to 15,000 more people after 20 years.
- **Unemployment ran higher** in every stepped run, 12% against 8.5% on average. One path cannot say whether that is the rate or the faster growth bringing arrivals ahead of jobs. CB1's harness has to measure it over an ensemble before the rule is settled.

The game is deterministic, so each row is one path from one save, not a distribution.

---

## 3. The overnight rate

### 3.1 The corridor

The Fed's arrangement, which the study copies [R1]:

| rate | the Fed, 17 Sept 2026 | PROPOSED |
|---|---|---|
| target range | 3.75-4.00% | the range |
| overnight reverse repo (the floor) | 3.75%, the bottom | the bottom |
| interest on reserve balances | 3.90%, ten under the top | the top less `IORB_UNDER_TOP` (.0010) |
| the window (primary credit) | 4.00%, the top | the top |
| standing repo | 4.00%, the top | not proposed: the window does its job here |
| the overnight rate (effective federal funds) | trades in the range, about 7 points under IORB while reserves are ample [R4] | below |

**The overnight rate in this city.** There is one bank, so the overnight rate is what the bank's marginal overnight dollar is worth. While it holds spare cash, nobody lends overnight for less than reserves earn, so the overnight is the reserve rate. When it funds its next loan at the window, the overnight is the window's rate. In between, it is the blend of the two by the share of its marginal money that came from the window, which the bank already strikes at its close (`Bank.windowShare()`, saved in its state):

    overnight = IORB + (top − IORB) × bank.windowShare()

No new coefficient: the Fed's 7-point gap under IORB comes from lenders without access to IORB, whom a one-bank city does not have. The overnight never leaves the range.

| range | reverse repo | reserves | window | overnight, flush | half at the window | at the window | today at the same dial: reserves / window |
|---|---|---|---|---|---|---|---|
| 0.00-0.25% | 0.00 | 0.15 | 0.25 | 0.15 | 0.20 | 0.25 | 0.13 / 0.38 |
| 2.75-3.00% | 2.75 | 2.90 | 3.00 | 2.90 | 2.95 | 3.00 | 2.88 / 3.13 |
| 3.50-3.75% | 3.50 | 3.65 | 3.75 | 3.65 | 3.70 | 3.75 | 3.63 / 3.88 |
| 5.25-5.50% | 5.25 | 5.40 | 5.50 | 5.40 | 5.45 | 5.50 | 5.38 / 5.63 |

The window gets 15 points cheaper against reserves than today's 25 (`WINDOW_PENALTY` retires): ten points over reserves, as at the Fed.

**The reverse repo has no taker** in a one-bank city: the bank earns more on reserves, and nobody else has an account at the central bank. It is the floor no rate trades under. It would gain a taker if the city's fund or the households' paper were ever given one, which this study does not propose (decision 10).

### 3.2 What reads the overnight, and what reads the corridor

| what | today | PROPOSED |
|---|---|---|
| the bank's spare cash | the dial | **IORB** |
| the bank's funding past its deposits | dial + 0.25 | **the top (the window)** |
| the treasury's overdraft | the dial | **the top** (decision 9) |
| the city's short rate and the curve's floor (`floorAt()`, `getRate()`) | the dial | **the overnight** |
| prime, mortgages, household and car loans | dial + spreads | **the overnight + today's spreads** |
| what savers are paid | dial × the bank's share | **the overnight × the share** |
| the carry trade, hot money (`carryRate()`) | the dial | **the overnight** |
| the real rate the currency reads | dial − expected inflation | **the overnight − expected inflation** |
| credibility's lean | the dial against holding the target | **the range against the step the rule allowed** (§2.3) |
| the advisor's sentence | rule − dial | **the rule against the overnight, and the next decision** |
| the preferred shares and warrants | the dial | **the overnight** |
| CD5's floating-rate notes | the policy rate (D1) | **the month's overnight + the auction's spread** |

`getPolicyRate()` stays as the name the code calls, and returns the overnight; the corridor's rates get getters of their own. A flush calm city pays 10 points less than today (2.90% against 3.00%); a calm city at the window pays the same (3.00%).

---

## 4. The remittance, kept as the Fed keeps it

### 4.1 How the Fed does it

The Reserve Banks' income is the interest on the securities they hold, at amortized cost, and on their loans; their expenses are the interest on reserve balances and reverse repos, their operating costs, and the dividend to member banks. What is left goes to the Treasury every week. When earnings fall short, the shortfall is booked as a **deferred asset** and later earnings pay it off before anything is remitted again [R2][R10].

| the Fed, 2025 | |
|---|---|
| interest earned | $155.3B |
| interest paid, mostly on reserves | −$167.4B |
| operating costs and the rest | about −$6.6B |
| **net, carried** | **−$18.7B** |
| deferred asset at the end of 2025 | $243.5B |
| deferred asset at 30 June 2026, with remittances restarting | $235.5B [R3] |

### 4.2 The proposed statement

A month's statement, struck where `closeMonth()` is, every line a figure the game already moves:

    Interest earned
        on the window's loans to the commercial bank       window loans × top / 12
        on the treasury's advances                         advances × top / 12          (decision 9)
        on the city's paper it holds                        coupons + the month's accretion of its discount (effective interest)
    Interest paid
        on reserves                                        IORB × the bank's spare cash / 12
        on overnight reverse repos                         none taken (§3.1)
    Operating costs                                        OPERATING_COST (.001) × total assets / 12   (decision 11)
    Gains and losses realised                              a sale or a buyback, against amortized cost
    = Net income
        less the loss carried, recovered first
    = Remittance, paid at the top of next month            (or: the loss carried grows)

Against today: the corridor's rates instead of the dial; paper at amortized cost, so a discount is earned month by month instead of the day it is bought; operating costs; and every line on screen, with **"of which the treasury's own interest"** beside the remittance so the overdraft's round trip reads as one.

**Paper at amortized cost.** Today a bond bought under face books its whole discount as that month's profit, at face. A term loan sells D$149.5M of face for D$100M (`spec-city-debt` §1.1), so a central bank buying a quarter of it at market books D$12.4M of profit that month and remits it the next. The Fed carries its securities at amortized cost and earns the discount as interest until maturity [R10]. CD2 retires the term loan and sells near par, which shrinks the effect but does not end it: paper bought after rates rise is bought under par. CD3's effective interest is the machinery (§5).

### 4.3 Four months, three ways

From `cb-model.py` §3, a month's profit in D$:

| month | today | proposed (CB3) | reserves as a real central bank's (decision 12) |
|---|---|---|---|
| Halifax, Sep 2036: the bank at the window, D$748k | 3,667 | 3,523 (window at the top 3,586, costs −62) | 3,523 (no reserves: the bank is overdrawn) |
| Halifax, May 2022: the treasury overdrawn, D$56k | 721 | 723 | 0 (the advance became reserves, which earn IORB) |
| Slot 6, month 267: D$3.87M of paper, the bank's cash −D$1.75M | 8,180 | 7,858 | **−5,519, carried** (IORB on the money its purchases made) |
| Bergen, month 3003: D$11.2B of spare cash, nothing owned | −23.3M, carried | −22.3M, carried | more: its unlent deposits too |

**CB3 makes the books honest on today's money but does not change the puzzle's size.** A central bank lending to an overdrawn bank earns the full rate on that loan, at the Fed too: an overdraft is not a reserve. The window's month stays at 14% of revenue, because in this city the central bank is the commercial bank's wholesale lender. The overdraft's month stays at 41%, because the treasury pays the interest and gets it back; the screen will say so on the same line.

### 4.4 The loss carried is the Fed's deferred asset: checked

`closeMonth()` adds a loss to `lossCarried` and recovers it from the next profits before anything is owed (`recovered = min(lossCarried, profit)`); `remit()` pays only what is owed; both are saved (`toSaveArray()` slots 6 and 7); equity is the remittance due less the loss carried, which `CentralBankCheck` asserts. That is the Fed's rule [R2]. Two differences, neither worth code: the Fed books the deferred asset as a negative liability and keeps its capital whole where the game lets equity go negative, which is the same number shown another way; and the Fed remits weekly where the game remits monthly in arrears. **What is missing is its name on screen**: Finances shows "the loss carried" in a sentence (`FinancesScreen` line 2892); CB3 calls it the deferred asset and shows how many months of the present profit would earn it back, or "never, at this rate" for Bergen.

### 4.5 What stays inaccurate after CB3

**Where interest on reserves is paid.** A real central bank's reserves are exactly the money it has made and not taken back, so it always pays the reserve rate on what it made and on nothing else. Here the reserve rate is paid on the commercial bank's spare cash, which differs from what the central bank made in both directions (§1.3). Making them one figure means counting the bank's unlent deposits as its reserves and bringing money from abroad in through the central bank. That moves every figure of the bank and every trace, and is the bank's books' study, which `spec-city-debt` §10 already names. This study recommends it as that study's question, not a CB batch (decision 12).

---

## 5. How it meets the debt study

**CD1's expected dial becomes a staircase.** `spec-city-debt` §3.1 builds the market's expected dial as `p(t) = R(t) + (floor now − R(0)) · e^(−t / DRIFT_MONTHS)`: a smooth drift to the rule. With decisions in steps, the market knows the range until the next decision and expects it to move at each. **PROPOSED for CD1:**

    f(t) = the overnight now,                                   until the next decision d₁
    L(dₖ) = L(dₖ₋₁) + clamp(p(dₖ) − L(dₖ₋₁), ±MAX_STEP),       at each decision dₖ (the expected move, not rounded to a quarter)
    f(t) = L(dₖ),                                               from dₖ to dₖ₊₁

`p(t)`, `DRIFT_MONTHS` (D15) and D9 ("drifts to the rule, whoever holds it") stay. The expected move is not rounded: a market expecting a 60% chance of a quarter-point cut prices 15 points, as futures do. The average expected short rate (`cb-model.py` §4), before the credit spread and the term premium, which do not change:

| city | 3 months | 6 months | 1 year | 2 years | 5 years |
|---|---|---|---|---|---|
| calm: on target, the dial at neutral | 0 | 0 | 0 | 0 | 0 |
| hiking: 6%, the dial at 7%, credibility 65% | +1 bp | +1 | +3 | +3 | +2 |
| behind: 5%, the dial at 1% by hand, credibility 60% | −24 bp | −43 | −35 | −18 | −7 |

The staircase matters only far from the rule, where the step limit slows the expected catch-up. `CurveCheck`'s fixtures stay; its "calm equals today's curve" becomes "calm equals a flat path at the overnight", and its "behind" case asserts the steps against `MAX_STEP` and `DECISION_MONTHS`.

**CD2's bills** price on the expected overnight to their maturity, which is the staircase's first months. **CD5's floating-rate notes** reset each month on that month's overnight plus the spread their auction cleared at, as notes on SOFR or CORRA do, instead of "the policy rate" (D1, D25). **CD3's effective interest** is what CB3 uses to carry the central bank's paper at amortized cost.

**Where the batches go** (they are proposals; `QUEUE.md` is unchanged). On main this evening (EN1 shipped, CL5 queued) the queue puts EG1, CL2, CL3, FX5, CL4 and CL5 first, then CD1 to CD6.
- **CB1 and CB2 between CL5 and CD1.** CD1 then prices on decisions in steps and on the overnight from its first line, instead of building the smooth drift and having it replaced. CB1 and CB2 touch `DebtManager`'s dial and `Bank`'s funding prices, which CD1 reads and does not change.
- **CB3 between CD3 and CD4.** It needs CD3's effective interest, and it is independent of CD4's buybacks except that a buyback's gain is struck against amortized cost.

---

## 6. The screens

The boards, in the artifact **[Central Bank Study](https://claude.ai/artifact/7UCd3e8od9pPVY87aYB8jf)**; Halifax twenty years on, from the copy played on the proposed rule:

1. **Policy › Central bank, the rate as a range (CB1).** Four figures: the target range (5.25-5.50%), the overnight (5.40%), the next decision (Oct 2059) and what the rule says (5.00%: down ½ next). The rate over the 20 years as a band with the overnight stepping inside it, today's autopilot dashed behind it on its own copy. The picker in quarter points, Set for the next decision and Decide now; the decision log, scheduled or unscheduled.
2. **The corridor (CB2).** The four rates on one scale, the overnight marked, with the bank's funding as three states (spare cash, half at the window, at the window); and what reads which rate.
3. **Finances › Money › The central bank (CB3).** The income statement for four months from Jerus's cities, on today's arithmetic, the proposed one and decision 12's; beside it the Fed's 2025.
4. **Why the remittance was half the revenue (CB3).** Halifax's remittance as a share of revenue by source, month by month, and the This month card as it is (Money destroyed has no segment for what was destroyed) and as proposed.
5. **Finances › Your rate (CD1, amended).** The city behind the curve: the smooth drift and the staircase over five years, and the differences for the three cities.

Every figure on a screen comes from a model getter (CLAUDE.md, *Adding things*); the decision log's record is saved, because next month's screen needs it.

---

## 7. Batches

| Batch | What | Harness |
|---|---|---|
| **CB1 ★** | **Decisions in steps** (§2): the target range on quarter points, the decision calendar (`DECISION_MONTHS`, its months), the autopilot's step rule (`STEP`, `MAX_STEP`, hold within an eighth), unscheduled decisions (`EMERGENCY_GAP`, `EMERGENCY_STEP`), the player's range waiting for the decision and Decide now, the lean against the step allowed, the `DecisionLog` record; saved: the range, the next decision, the player's pending range, the last decisions (`DataSave`, the load path; `SAVE_FORMAT` and `OLDEST_LOADABLE_FORMAT` raised, saves never matter). Until CB2, `getPolicyRate()` returns the range's IORB. **Its screen: Policy's rate as a range** (board 1; D31). | `RateDecisionCheck`: on target, every decision holds (counted); inflation stepped up 4 points, the range rises `MAX_STEP` a decision until within an eighth, asserted against the constants; a jump past `EMERGENCY_GAP` between decisions makes one unscheduled move of at most `EMERGENCY_STEP`; a range set by hand waits, Decide now does not; save and load between decisions keep the calendar. And the finding: an ensemble of Halifax-like runs against today's autopilot, counting moves, months 3 points off target and unemployment (§2.6's open question), reported rather than asserted |
| **CB2 ★** | **The overnight rate and the corridor** (§3): the reverse repo, IORB and the window placed in the range; the overnight from `windowShare()`; the bank's spare cash at IORB, its window at the top, the treasury's advances at the top; every reader in §3.2 on the overnight; `WINDOW_PENALTY` retired. **Its screen: the corridor card on Policy, and the Bank tab's ladder on the overnight** (board 2). | `CorridorCheck`: a fixture bank with spare cash puts the overnight at IORB and earns it; a bank funding past its deposits puts it at the top and pays it; a half share halfway; the overnight never leaves the range over a LongPlaytest run, and how often it sits at each end is counted; prime, deposit and carry rates differ from the overnight by exactly today's spreads; `MoneyAudit` closes |
| **CB3** | **The books as the Fed keeps them** (§4): the income statement struck each month and saved for the screen (`HistorySave`); paper at amortized cost on CD3's effective interest, no gain on the day of purchase, a sale's or a buyback's gain against amortized cost; `OPERATING_COST`; the loss carried named the deferred asset with its months to recover; the remittance with "of which the treasury's own interest". **Its screen: Finances › Money › The central bank, and the This month card with every line** (boards 3, 4). | `RemittanceCheck`: a window-only fixture remits the window's interest less costs; an overdraft fixture remits the advance's interest and the budget shows both lines and their netting; paper bought at a discount books nothing that month and accretes month by month to the discount by maturity; a loss is carried and recovered before anything is remitted; `MoneyAudit` and `CentralBankCheck`'s equity identity hold |

**Not a CB batch: reserves as a real central bank's** (decision 12), for the study of the bank's books.

Every batch moves every trace that borrows or lends: re-baseline, and the ensemble.

---

## 8. ★ Decisions

Each with a recommendation; none is taken.

1. **How often the central bank decides.** (a) Every two months, six a year, nearer the eight of the Fed, the ECB and the Bank of Canada; in Halifax it held inflation as well as today. (b) Every three months, the SNB's schedule and Jerus's words; in Halifax it let 29 months stray 3 points from target against 19. (c) A setting on the Policy tab, one, two or three months, two by default. **Recommended: (a).** (c) costs little if he wants to choose per city.
2. **The range's shape.** (a) The Fed's: a quarter point wide, the reverse repo at the bottom, reserves ten under the top, the window at the top. (b) The Bank of Canada's: one target rate, reserves at it, the window a quarter over it (today's shape, rounded to quarter points). **Recommended: (a)**, the "3.5-3.75" Jerus asked for.
3. **The step.** A quarter point, always. **Recommended: yes.**
4. **Which range the rule picks.** (a) The range that puts the overnight nearest the rule (a calm city's 3.00% rule gives 2.75-3.00%, reserves 2.90%). (b) The range the rule's rate falls inside (3.00-3.25%, reserves 3.15%). **Recommended: (a)**: the overnight is never more than an eighth from the rule.
5. **The most the autopilot moves at a scheduled decision.** ¼, ½, ¾ or a point. **Recommended: ¾**: in the game ¾ and a point gave nearly the same path, and ¾ is the most the Fed raised at any meeting of 2022.
6. **Unscheduled decisions.** (a) When the rule is 3 points from the overnight, by up to a point. (b) Never. **Recommended: (a)**: three in 20 years in Halifax; the Fed has moved between meetings seven times since 2001.
7. **The player's hand.** (a) The range set by hand waits for the next decision; Decide now is an unscheduled decision. (b) It applies at once, as today. **Recommended: (a)**: it is what makes it a decision.
8. **Credibility while the steps catch up.** (a) A decision that moved as far as its step allowed counts as leaning fully until the next. (b) The lean is measured against the rule itself, so a bank behind the curve loses trust, as the Fed did in 2021-22. **Recommended: (a)** for the autopilot and the player alike: "it works with what it can".
9. **What the treasury's overdraft costs.** (a) The window's rate, the top: last-resort money costs the most. (b) The overnight. **Recommended: (a)**. It comes back as remittance either way.
10. **The reverse repo.** (a) The range's floor, with no taker in a one-bank city. (b) Open it to the city's fund. **Recommended: (a)**: a fund paid by the central bank is paid by the treasury's own remittance.
11. **Operating costs.** (a) A tenth of a percent of its assets a year, about the Fed's. (b) None, as today. **Recommended: (a)**: small, and it makes a central bank with nothing to do cost nothing.
12. **Reserves as a real central bank's** (§4.5): the reserve rate on what the central bank made, the bank's unlent deposits counted as its reserves, money from abroad brought in through the central bank. (a) The bank's books' own study, later. (b) A CB4 batch after CB3. **Recommended: (a)**: it flips slot 6's remittance into a loss and moves every figure of the bank, which needs its own measurement.
13. **Bergen's loss, which can never be earned back.** (a) Carried and shown as the deferred asset, with "never, at this rate". (b) Written off against the treasury, as a government recapitalises its central bank. (c) No reserve rate on spare cash the central bank did not make (the bank's floor goes in flush cities). **Recommended: (a)** until decision 12 is studied.
14. **Where the batches go.** (a) CB1 and CB2 between CL5 and CD1, CB3 between CD3 and CD4. (b) All three after CD6. **Recommended: (a)**: CD1 builds on the staircase instead of a path that is then replaced.
15. **The window as the bank's routine lender.** (a) Keep it, as a refinancing central bank, and show it: its interest on the statement, the bank's window loans on the Bank tab. (b) Make the bank fund past its deposits elsewhere (by selling its paper to savers). **Recommended: (a)**: the remittance it makes is real income for a central bank that lends to its banks; what was wrong was not seeing it.

---

## 9. Found on the way

- **`Game.loadGame()` on a fresh `Game` loads a city with no buildings and says the catalogue changed.** The probe's first load printed "Save contains building id 0, which is not in buildings.json - skipped." 33 times: the templates are loaded by `initialize()`, which `loadGameSave()` and `run()` call and `loadGame()` does not. A harness that loads a save must use `loadGameSave()`. The message blames `buildings.json` when the catalogue was never read.
- **The vault earns nothing.** `ForeignAccounts` holds the reserves in dollars and pays no interest on them. Canada's Exchange Fund Account, which the treasury's vault resembles (bought with the treasury's money), earns the world's rates for the government [R9]. A small change of its own, for the treasury's revenue, not the remittance.
- **The bank's unlent deposits earn nothing.** A bank whose cash is negative but within its deposits holds money the city has deposited and not borrowed, which a real bank would hold as reserves earning the reserve rate. In Halifax at month 477 that was about D$1.0M. Part of decision 12.
- **On the past's inflation, the proposed rule would have been unscheduled 35 times; in the game, 3 times.** The swings it would have had to answer were partly the dial's own.
- **Unemployment ran higher in every stepped run** (§2.6). Open, and CB1's ensemble is the place to settle it.

---

## Sources

- **[R1]** Federal Reserve, *Implementation Note* of 16 September 2026 (the target range 3.75-4.00%, interest on reserve balances 3.90%, overnight reverse repo 3.75%, standing repo 4.00%, primary credit 4.00%), and those of 28 January to 29 July 2026 (3.50-3.75%, 3.65%, 3.50%, 3.75%, 3.75%): https://www.federalreserve.gov/newsevents/pressreleases/monetary20260916a1.htm
- **[R2]** Federal Reserve Banks, *Combined Financial Statements* for 2025 and 2024 (https://www.federalreserve.gov/aboutthefed/files/combinedfinstmt2025.pdf); the figures as reported by Bloomberg Law, "Fed Says Operating Losses Down in 2025 as Interest Expenses Fell": interest income $155.3B, interest expense $167.4B, an operating loss of $18.7B, the deferred asset at $243.5B.
- **[R3]** First Trust, "Q2 Fed financials: slow progress" (3 September 2026): net remittances of $12.2B in the second quarter, the deferred asset down to $235.5B.
- **[R4]** Federal Reserve, *FEDS Notes*, "What can public Fedwire payments data tell us about ample reserves?" (18 July 2025): the effective federal funds rate about 7 basis points under IORB from September 2021.
- **[R5]** Bank of Canada: eight fixed announcement dates a year; the target for the overnight rate, the Bank Rate a quarter point over it and the deposit rate at it since 2020; the one-point increase of 13 July 2022.
- **[R6]** Swiss National Bank: its quarterly monetary policy assessments, in March, June, September and December.
- **[R7]** Federal Open Market Committee: eight scheduled meetings a year; the cuts between meetings of 3 March 2020 (half a point) and 15 March 2020 (a point); the three-quarter-point increases of June, July, September and November 2022.
- **[R8]** European Central Bank, its operational framework before 2008: a banking system in structural liquidity deficit, refinanced weekly at the main refinancing rate, inside a corridor of the marginal lending facility above and the deposit facility below.
- **[R9]** Government of Canada, the Exchange Fund Account: held by the Minister of Finance, managed by the Bank of Canada as fiscal agent, its net income the government's.
- **[R10]** Board of Governors, *Financial Accounting Manual for Federal Reserve Banks*: securities held in the System Open Market Account at amortized cost, premiums and discounts amortized into interest income; the deferred asset for remittances to the Treasury.
