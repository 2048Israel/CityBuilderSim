# Sector statements: a summary and a real statement (design study)

*2026-10-07, evening. Samuel asked for a read-only UI mockup and design study of the sector pages' financial statements, while another agent works on the code. Oil is mid-revamp and is left alone. The study read the PC's tree at 0.7.65 and changed nothing. The files read were: `ui/SectorScreen.java` (3,521 lines: Income, Balance sheet, Cash & debt, Investors), `SectorBooks`, `Sector.Statement`, `BalanceSheet`, `ui/BankScreen.profitStatement()`, `Bank.Line` / `Bank.Sheet`, `BusinessDebtManager`, `Equity`, `BuildCard` and `BusinessInvestment`. The mockups are the canvas **"Sector Statements Study"** (https://claude.ai/artifact/XRJdBfU4oNqEaFxxo3tgpV): twelve boards for Manufacturing in January 2200 and the bank. Every figure on them is illustrative, but built to tie: each statement foots, the three cross-foot, and the bank's equity moves by what it kept. Nothing was read from a save.*

## 1. The ask

> "make the financial statements of the sectors ui better, cause right now its good, but its not a financial statement, just summarized, so i need both a summarized and a detailed actual statement … only focusing on the income statement, balance sheet, cash and debt, and investor … also that means that a banks income statement is different, cause interest income"

And, mid-study:

> "feel free to think of concepts to add such as capital structure decisions and more deep into finance and accounting … free to think of concepts to add or implement for the coding agents later" (§8)

## 2. Why today's pages read as a summary

Today each page is a picture, one statement in plain words, and ratio chips. The pictures are the waterfall, the two stacked bars, the cash bridge and the decision card. What stops the statement being a financial statement:

- **No classification.** There is no gross profit and no current vs long-lived split. Cash & debt is a flat list of up to 20 flows, not grouped into operating, investing and financing.
- **Equity is one plug** (`totalAssets − totalLiabilities`). There is no share capital, no retained earnings and no statement of why equity moved.
- **Debt is one line**, "Loans and bonds outstanding". It has no due-within-a-year split, so `BalanceSheet.getCurrentLiabilities()` is the suppliers alone.
- **Plain-words labels** ("Bought in", "What it kept") and minus signs. There are no notes and no common-size column.
- **Several things move a sector's equity and reach no statement at all** (F1). So "What it kept" is not what its owners gained.

The investor side has the same gap. The Investors page explains the build decision well, but nothing per share is on it: no EPS, payout, yield or P/E. The owners card sits on the Balance sheet.

## 3. The design: one switch, two views

**The switch.** A segmented **Summary | Statement** control sits at the right end of the page strip (board *SectorFrame*). It is on Income, Balance sheet, Cash & debt and Investors, and not on Operations. ★D1: it is one choice for every sector and page, kept for the session as UI state (like `detailsOpen`), not saved.

**Summary** is today's picture, tightened, plus an IN SHORT card (five lines, this month, last month and change, in $ millions) and the ratios. Its subtotals follow the statement's (★D14).

**Statement** is the formal statement, with these conventions:
- A title block: the company, the statement's name, the period, and "in $ thousands". ★D2: switch to millions when the largest figure passes seven digits, because Real Estate's $1.2B a month would run to ten.
- Columns: note · this month · last month · change · common-size ("of revenue" on Income, "of assets" on the sheets). The change and % columns can be turned off from a toolbar.
- Negatives in parentheses, on the Statement view only (★D3). One rule over a subtotal; a double rule under the bottom line.
- A note number on a line opens the line in place: today's `linesOpen` mechanism, plus "open every note / close them".
- A line shows when either month is non-zero (today's rule).
- Verdict colours only on the bottom line and the ratio chips (B10's rule).

## 4. Page by page

### 4.1 Income: statement of profit or loss (boards *IncomeSummary*, *Main*)

| line | source (struck month; last month from `SectorBooks.previous`) |
|---|---|
| Revenue (note 1: by good, in the city / abroad; work billed by name) | `revenue`; `st.sold`, `otherRevenueParts()` |
| Sales tax remitted (note 2: charged / border / credit / zero-rated) | `salesTaxPaid`; `salesTaxDetail()` |
| **Revenue after sales tax** | revenue − salesTaxPaid |
| Cost of sales (note 3: by good, home/imported, by supplier; freight) | `inputs`; `st.bought`, `purchasesBySupplier`, `otherInputParts()` |
| **GROSS PROFIT** | new subtotal |
| Wages (note 4: by tier, posts), Electricity, Water, Repairs, Property tax | `payroll`, `electricity`, `water`, `maintenance`, `propertyTax` |
| **OPERATING PROFIT** | revenue − salesTax − inputs − opex |
| Finance costs (note 5: bank loans / bonds / mortgages / interim) | `interest`; split needs **R1** |
| **PROFIT BEFORE TAX** | must equal `preTaxIncome` to the cent |
| Business tax | `tax` |
| **PROFIT FOR THE MONTH** (the trading result) | `netIncome`: tax, the dividend and the six-months-losing rule are struck on it |
| Outside the trading result (note 6) | see F1 |
| **TOTAL RESULT FOR THE MONTH** | netIncome + the above |

★D4 puts sales tax under revenue, and ★D5 puts property tax among the operating expenses. Both are presentation only: profit before tax is the model's own figure. The model's `operatingIncome` (before property and sales tax) survives as the (i) on Operating profit.

**Ratios:** gross margin, operating margin, net margin, interest cover (★D10: struck on operating profit, F6), effective tax rate, wages take, EPS.

**The Summary** adds OF EVERY DOLLAR IT TOOK: one bar of cents (5.9¢ sales tax, 39.7¢ bought in, 39.9¢ wages … 6.9¢ kept).

### 4.2 Balance sheet: financial position and changes in equity (boards *BalanceSummary*, *BalanceStatement*)

**ASSETS**
- Current assets: cash; stock at today's price (note 7); owed by its buyers.
- Long-lived assets: land; buildings at cost (note 8: by template, count and book); held abroad (note 9); other businesses' bonds.
- TOTAL ASSETS.

**LIABILITIES**
- Due within a year: owed to suppliers; debt falling due within the year (**R2**).
- Due later: bank loans, bonds, mortgages, interim (**R2**).
- TOTAL LIABILITIES.

**EQUITY**
- Share capital, subscribed (**R3**, ★D11); retained earnings and revaluation (the remainder).
- TOTAL EQUITY.
- TOTAL LIABILITIES AND EQUITY.

**Statement of changes in equity**, with columns for share capital, retained and revalued, and total:
- at the start;
- profit for the month;
- outside the trading result;
- revalued to today's prices, the computed remainder (**R6** splits it into stock, buildings and abroad);
- shares issued (`equityRaised`); paid to shareholders (`dividendsPaid`); bought back (`sharesBoughtBack`);
- at the end;
- NOT ACCOUNTED FOR, only when it does not close.

**Ratios:** current, quick (without stock), debt to equity, debt to assets, equity share, ROE a year (on average equity), book a share.

**The Summary:** the two bars with equity in two segments, IN SHORT, the ratios, and a small equity bridge. ★D7 moves ITS OWNERS to Investors; the Balance sheet keeps a door to it.

### 4.3 Cash & debt: cash flows and a debt schedule (boards *CashSummary*, *CashStatement*)

**Statement of cash flows, indirect method.** Every term of `SectorMonth.unexplained()` lands in one section, so the three sections plus opening cash are closing cash. NOT ACCOUNTED FOR = `unexplained()`, shown only when non-zero.

- **Operating:**
  - `netIncome` + `paidEarlier` (no cash) + `tradeCredit` (working capital);
  - + `depositInterest` + `bondCoupons` (interest received: operating, IAS 7 allows it);
  - + `fromTheCity` + `arrearsPaid` (grants) − `stolen`.
- **Investing:**
  - −(`spentOnBuildings` − `salvage`), − `salvage` (premises and scrapped plant: together −spentOnBuildings);
  - − `bondsBought`; − `investedAbroad` (sent abroad / brought home).
- **Financing:**
  - + `borrowed` (after fees) − `repaid`;
  - + `bondsIssued` − `bondsRepaid`;
  - + `equityRaised` − `dividendsPaid` − `sharesBoughtBack`;
  - + `forgiven` (an overdraft forgiven at a restructure).
- **For the record**, inside profit: interest paid, tax paid, interest earned abroad (no cash), and free cash flow (operating less premises).

**Debt schedule** by instrument: at the start · borrowed (gross principal, `getLentThisMonth`) · repaid · written off · at the end · rate · interest · runs to. Suppliers go on a memo row at no interest. It needs **R1**, **R2** and **R7**.

**When it falls due:** within a year / one to five years / after five years. It reuses `Palette.LADDER`, the city debt's ramp.

**The Summary:**
- The bridge, grouped: start → from trading → investing → financing → end, each step with its biggest items in words. It replaces 20 steps.
- FREE CASH FLOW, PAID TO ITS OWNERS (share of FCF), CASH ON HAND in days of trading spend.
- Today's rate, leverage and debt-mix pictures, plus WHEN IT FALLS DUE.
- CAN IT CARRY WHAT IT OWES: interest cover, debt over a year of trading cash, and can it borrow.

### 4.4 Investors (boards *InvestorsSummary*, *InvestorsStatement*)

**The Summary** keeps today's page whole:
- the decision card;
- ITS BUILDINGS with each first gate;
- WHAT STOPS IT;
- the rules, as chips;
- WHAT YOU CONTROL.

It adds FOR ITS SHAREHOLDERS: last trade and fair value, EPS, DPS, yield, P/E, P/B, and who holds it. The reads exist: `Equity.getShares`, `Exchange.price/fair/yieldAt`, `getDividendThisMonth`.

**The Statement** is the *investor report*:
- **A share**, this month against last: shares, EPS, DPS, payout, book a share, last trade, fair value, P/E, P/B, yield, market value, ROE.
- **Who holds it**, and raised and paid since founding (`getLifetimeRaised*`, `getLifetimeDividends*`).
- **How it is financed:** debt vs equity against `getTargetEquityShare()`.
- **What its capital costs:** a concept (C2), and ★D13 says not to ship it yet.
- **Every building at every gate** (**R4**, ★D8): ore · licence · staff · land · pays, each ticked, crossed or "none"; then the estimate a month, the cost to build, the payback, and one line of "so".

The mockup's Fabrication Shop shows why every gate matters. Its first gate today is land, but its estimate is −$139k a month, so it would not build with the land either. Today's page sends the player to the Land office for nothing (F11).

### 4.5 The bank (boards *BankIncome*, *BankBalance*)

The bank is not one of the 17 sectors. It has its own tab, whose Profit page already keeps a bank-format statement behind "details" (`profitStatement()`).

★D9: give Bank › Profit and Bank › Balance sheet the same switch, and keep the bank on its tab.

**Its income statement:**
- Interest income (six sources, `Bank.Line.FROM_*`, `DISCOUNT`) less interest expense (`SAVERS`, `WINDOW`) = NET INTEREST INCOME.
- + fees (`ACCOUNT_FEES`, `LOAN_FEES_PAID`, `LOAN_FEES_OWED`, `UNDERWRITING`) + `TRADING` + `PAPER_GAINS` + `BOND_GAINS` = TOTAL OPERATING INCOME.
- − `PROVISIONS` = after provisions.
- − `PAYROLL`, `UPKEEP` = PROFIT BEFORE TAX.
- − `TAX` (a month in arrears) = PROFIT.
- − `DIVIDENDS`, `BUYBACKS` = KEPT IN THE BANK.

**Its sheet**, most liquid first:
- Assets: reserves, the city's paper, bonds held, loans and advances (four kinds, gross), less the allowance, the desk's shares.
- Liabilities: deposit funding, overnight, deposits from abroad, unearned discount.
- Equity: paid-in, retained, and the preferred when the city holds any.
- A **memorandum** of the deposits held, which are not counted twice (THE THREE STATEMENTS).

**Its ratios:** NIM, cost to income, fee share, provisions of loans, ROE, payout, capital of the weighted book, equity of assets, loans to deposit funding, liquid share, allowance of loans, overnight share.

### 4.6 Six formats (board *Formats*)

One template with a format per kind of business, chosen by a new `Sector.statementFormat()` (★, recommended over a switch in the screen). The format changes the words and the subtotals; the bottom three lines are the model's in every one.

| format | sectors | its middle line |
|---|---|---|
| Makers | Manufacturing, Heavy Industry, Materials, Mining, Industry, Food Processing, Agriculture, Automotive; Oil and Refining after the revamp | gross profit |
| Merchants | Retail, Luxury Retail, Restaurants | gross margin; stock turns and days of stock |
| Landlords | Real Estate | net operating income; rent covers the mortgage; loan to value |
| Builders | Construction | gross profit on contracts; months of backlog |
| Carriers and services | Rail, Business Services | gross profit; revenue a worker |
| The bank | the bank | net interest income |

## 5. Findings from the read (flagged, none fixed)

- **F1. Ten flows move a sector's equity and reach no statement:**
  - the subsidy (`fromTheCity`) and arrears (`arrearsPaid`);
  - the bank's interest on its till (`depositInterest`), the coupons it is paid (`bondCoupons`), and the interest abroad rolled into holdings (`foreignInterest`);
  - the overdraft forgiven (`forgiven`), and debt its lenders wrote off (`writtenOff` and `getBondWrittenOffThisMonth`; `writeDownSector()` cuts the principal and moves no cash);
  - the theft (`stolen`);
  - and F2's fees.

  So the interest a sector earns is **never taxed** and never in the payout base. The design shows them as "Outside the trading result" and in the equity statement; that is presentation only. *Model question for Samuel:* should earned interest and subsidies be taxed or paid out?
- **F2. Upfront borrowing costs reach no statement.** The loan fee and the mortgage premium are paid out of proceeds (`getFeesThisMonth`, `getPremiumsThisMonth`), and so are a bond's issuing costs (**R5**). The liability is the full principal and the cash is less, so equity falls by them and nothing says why. *Presentation:* a line outside the trading result. *Model:* expense them, or amortise them into interest (the effective-interest method).
- **F3. A sector has no paid-in capital.** `Equity` keeps lifetime raised (home and abroad) but no paid-in figure: the money spent on buybacks is kept only month by month (`sharesBoughtBack`), and whether the founding stake is inside lifetime raised was not checked. The bank keeps paid-in and retained, and since 0.7.13 takes a buyback off paid-in capital.
- **F4. No debt is classed as due within a year**, though loans, mortgages and bonds all have maturities (`CorporateBond.maturityMonth`). A current ratio from `BalanceSheet` today would count suppliers alone.
- **F5. Sales tax is drawn as a cost below operating income**, while `Sector.bank()`'s note says revenue is booked net of VAT. The statement puts it under revenue (★D4).
- **F6. Interest cover is struck on operating income before property and sales tax.** In the mockup that is 209×; on the statement's operating profit it is 118×.
- **F7. The notes are this month only.** `SectorMonth` keeps no per-good maps. The screen's own comment argues for leaving it so (★D12: keep).
- **F8. Stock is carried at market, not the lower of cost and net realisable value.** Unrealised gains on unsold stock reach equity.
- **F9. "Buildings, at cost" is not cost.** Cash plus materials *at the price of the day* re-marks the whole estate every month the materials price moves. It belongs in the revaluation line, not in profit, which is where the equity statement puts it.
- **F10. `BalanceSheet`'s class comment is stale** ("land and bonds payable are always zero"). For a docs pass.
- **F11. The first gate only can hide a second refusal** (§4.4).

## 6. Reads the implementer needs (no behaviour change)

**R1** interest by instrument, **frozen at the strike**. `getLoanInterest()` and `bondBook.monthlyCoupon()` are live and would describe the next month: the `nameOtherRevenue` lesson.

**R2** principal by kind, with what falls due within 12 months and within 60, read at the strike.

**R3** paid-in capital per company: founding plus raised less bought back, in money. This needs a saved figure, so SAVE_FORMAT goes up (★D11). Without it, show "subscribed since founding" from lifetime raised, labelled as such.

**R4** every gate per building, not only the first, plus its cost to build and its split between cash, loan and shares (`BuildCard.investors()`).

**R5** a bond's issuing costs per sector per month: face sold less `getProceeds()`.

**R6** the revaluation split: the stock's mark, buildings at today's materials, FX on what it holds abroad. Optional, because the remainder already closes.

**R7** last month's principal by kind, for the roll-forward's opening column. It is in memory and not saved: after a load it reads "not counted yet", as the flow's units do (B14).

Everything else exists: `SectorMonth`, `Sector.Statement`, `Bank.Line` and `Bank.Sheet`, `Equity`, `Exchange`, `BusinessDebtManager`.

## 7. What a harness should hold (`SectorStatementCheck`, new)

These hold on the played cities, every sector, every month:
- the statement's profit before tax is `preTaxIncome` to the cent, and its profit is `netIncome`;
- the three cash sections plus opening cash are closing cash, and their residual is `unexplained()`;
- the classified sheet's sub-totals are the totals;
- the equity statement closes with the named lines and the remainder, and `equityRaised − dividendsPaid − sharesBoughtBack` are its owners' lines;
- the bank's NII, total operating income and profit are its `Bank.Line`s;
- a save and load keep every comparative.

No premise moves, and the playtest stays byte-identical: this is reading only.

## 8. Concepts for later

Each concept is written to Samuel's rule, mechanisms and not modifiers: it uses figures the model already has. For each: what it is, why it fits this game, and its size.

**Capital structure and cost of capital**
- **C1 Capital structure as a trade-off.** Today the register has a target equity share that moves with profitability. Replace it with an optimum found from two real things already in the model: the **tax shield** (interest is already deducted before the business tax) and the **expected cost of distress** (the bank's own default curve × loss given default). The sector issues or buys back toward the leverage that minimises its cost of capital. Large; changes behaviour; ensemble re-run.
- **C2 A cost-of-capital hurdle (WACC) for building.** Replace "earns 1.25× its interest" with "earns more than its capital costs": the return on the building against the blended cost of its debt (after tax) and its equity (the market's earnings or dividend yield). Ship the investor report's cost-of-capital card with this, not before. Medium; behaviour.
- **C3 Dividend policy (Lintner).** Dividends move part of the way toward a target payout each year, and firms are slow to cut. Buybacks become the flexible lever for spare cash. Small to medium.
- **C4 Covenants.** A loan carries a maximum leverage and a minimum interest cover. A breach reprices it or stops new lending well before the 1.50 default point, giving a ladder of warnings the player can see coming (his rule for the foreign window). Medium.
- **C5 Credit ratings.** Letter grades from leverage, cover and record, shown on Cash & debt, with bonds priced by grade. The pricing exists; the grade names it. Medium, mostly presentation.

**Accounting**
- **C6 Depreciation and capital spending.** Buildings wear out over a life per template, with accumulated depreciation, EBITDA against EBIT, and repairs that extend the life. Depreciation is tax-deductible, a second real tax shield. It makes "at cost" honest (F9) and gives a reason to replace plant. Large.
- **C7 Tax-loss carry-forward.** A loss earns no refund today; let losses offset the next years' profits, as real systems do (Canada carries a business loss forward twenty years). Small, and real.
- **C8 Upfront borrowing costs** expensed or amortised into interest (F2). Small; behaviour (tax and payout).
- **C9 Stock at the lower of cost and net realisable value**, with cost by weighted average. It moves the stock's mark out of equity and into gross profit when the stock sells. Medium.
- **C10 Interest capitalised during construction** (IAS 23): interest on the loan funding a building on site is added to its cost. Small to medium.
- **C11 Impairment.** When a building's value in use (its estimate, discounted) falls below its book, write it down. The appraisal already has the estimate (F11). Presentation first, books later.
- **C12 Comprehensive income.** FX on what it holds abroad goes to a translation reserve, not profit. It splits the revaluation line properly. Small once R6 exists.

**Reports**
- **C13 The annual report.** Each January, a twelve-month statement per sector with year on year (needs twelve `SectorMonth`s saved: a few KB). Medium.
- **C14 The private sector, consolidated.** One statement for all 17, with sales between sectors eliminated. `purchasesBySupplier` makes the elimination exact. What is left of revenue after what it bought from outside is the private sector's value added: a bridge to `NationalAccounts`. Medium; very instructive.
- **C15 DuPont.** ROE = margin × asset turnover × leverage, as three bars on the Balance summary. Small, presentation only.
- **C16 Working-capital cycle.** Days of stock, days to be paid and days to pay, and the cash conversion cycle, from the supplier credit of 0.7.44. Small for the figures; medium if credit terms vary and a short sector stretches its suppliers.

**The bank**
- **C17 Expected credit loss in three stages** (IFRS 9): performing, deteriorated and defaulted, with 12-month and lifetime loss on top of today's allowance. Medium.
- **C18 Liquidity ratios.** The coverage ratio (liquid assets against 30 days of outflows) and stable funding, beside the capital ratio it already has. Medium.
- **C19 Capital tiers.** Common equity vs the city's preferred, each against the weighted book. Small.

*Not recommended:* an Altman-style Z-score. Its coefficients are fitted to US manufacturers, which is the made-up multiplier Samuel does not want; the bank's default curve already says the same thing from the model.

*My order:* C15 and C7 (cheap, real); then C13; then C2 with C1 (the deep one); then C6.

## 9. Decisions for Samuel (★, each with a recommendation)

| | question | recommendation |
|---|---|---|
| D1 | one Summary/Statement choice for all pages and sectors, or per page | one, kept for the session |
| D2 | units on the statement | $ thousands, millions past seven digits |
| D3 | negatives | parentheses on the Statement view only |
| D4 | sales tax | under revenue: "revenue after sales tax" |
| D5 | property tax | among the operating expenses |
| D6 | outside-the-trading-result lines and a total result | yes; KEPT stays the trading profit |
| D7 | the owners card | to Investors, with a door from the Balance sheet |
| D8 | every gate per building | yes (R4) |
| D9 | the bank | same switch on its own tab, not an 18th card |
| D10 | interest cover | on operating profit after taxes |
| D11 | share capital | a saved paid-in figure (SAVE_FORMAT bump) |
| D12 | comparatives for the notes | keep this month only |
| D13 | the cost-of-capital card | ship with C2, not before |
| D14 | summary subtotals | the statement's (gross profit, operating profit) |

## 10. Build order (Samuel's batch shape: implementer, then a fresh docs pass)

- **Batch 1, presentation only, playtest byte-identical:**
  - the switch;
  - the three statements from existing fields;
  - the cash flow in three sections;
  - the equity statement with the remainder;
  - outside the trading result;
  - `statementFormat()`;
  - the bank's switch;
  - R1 and R2 (frozen at the strike, not saved);
  - the summaries' new cards;
  - `SectorStatementCheck`.
- **Batch 2:** R3 (save bump), R4 with the all-gates appraisal, R5, R6, R7, and the owners card moved.
- **Then the concepts**, one batch each, the model-changing ones with the ensemble.

The page still scrolls once at 1,389 × 868: the frame stays put and only the page moves. The Statement boards are 1,500–1,750 px tall.
