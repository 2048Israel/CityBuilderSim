# CBK2 - QE and QT at the player's pace, the auction share, and the law on direct purchases

Builder: Claude (Opus 5.5), bus CBK2, branch batch/CBK2 cut from origin/main ffd07ee15 (0.9.219).
Brief: docs/queue/CBK2.md (written on 0.9.210).

## Reality checked (a research helper, 2026-11-28; every source its own)

The brief's list, verified or corrected:

1. **Bank of Canada: partly right.** It buys nominal GoC bonds at auction on a
   non-competitive basis for its balance sheet: a minimum 13% of each nominal
   bond auction since Dec 2017 (14% before, 15% before Feb 2017;
   https://www.bankofcanada.ca/2017/12/bank-canada-announces-reduction-minimum-amount/).
   The "up to 40%" of 2020 was treasury bills only, from a 25% maximum, 15 Apr 2020
   (https://www.bankofcanada.ca/2020/04/bank-canada-announces-increase-amount-government-canada-treasury-bills/),
   back to 20% from 27 Jul 2020 and 10% by 21 Sep 2020 (https://www.bankofcanada.ca/?p=214142).
   Its QE bought bonds in the secondary market, at least C$5B a week from 1 Apr 2020
   (https://www.bankofcanada.ca/2020/03/press-release-2020-03-27/).
2. **The Fed: right.** Federal Reserve Act s.14(b): Treasuries "only in the open market"
   (https://www.federalreserve.gov/aboutthefed/section14.htm); direct purchases barred in 1935,
   a wartime exemption lapsed in 1981
   (https://libertystreeteconomics.newyorkfed.org/2014/09/direct-purchases-of-us-treasury-securities-by-federal-reserve-banks/);
   rollovers are "non-competitive tenders ... treated as add-ons to announced auction sizes"
   (https://www.newyorkfed.org/markets/treasury-rollover-faq.html).
3. **The EU: right.** TFEU Art. 123(1): "...as shall the purchase directly from them by the
   European Central Bank or national central banks of debt instruments"
   (https://www.legislation.gov.uk/eut/teec/article/123/data.htm?view=plain).
4. **Japan: right.** Public Finance Act Art. 5 forbids the BoJ underwriting, "only within the
   limits of the amount sanctioned ... at the National Diet" excepted
   (https://jahis.law.nagoya-u.ac.jp/lawdb/l/322a0034en); the "BOJ Rollover" line was ¥2.2T in
   FY2021 (MOF debt management report 2021).
5. **UK Ways and Means: half right.** Extended 9 Apr 2020 with no size cap, "repaid as soon as
   possible before the end of the year"
   (https://www.bankofengland.co.uk/news/2020/april/hmt-and-boe-announce-temporary-extension-to-ways-and-means-facility);
   no 2020 drawing confirmed; the £20bn for six weeks was 2008. The game has it as the
   treasury's advances.
6. **Paces: right, one date wrong.** The Fed's $80B Treasuries + $40B MBS a month began
   10 Jun 2020 (in March 2020 it bought ~$75B a *day*;
   https://www.newyorkfed.org/markets/opolicy/operating_policy_200610). BoE 2009: £200bn over
   about 11 months, ~14% of GDP (BoE QB 2011). BoJ Apr 2013: "7+ trillion yen" a month.
   Fed QT: runoff under caps, $30B/$17.5B from Jun 2022, $60B/$35B from Sep 2022
   (https://www.newyorkfed.org/markets/opolicy/operating_policy_220504), $25B from Jun 2024,
   $5B from Apr 2025, ended 1 Dec 2025. BoE QT: £80bn a year, maturities plus **sales at
   auction** from 1 Nov 2022 (https://www.bankofengland.co.uk/asset-purchase-facility/2022/2022-q4);
   the APF's realised losses fall on HM Treasury under its indemnity (gov.uk s.231 report).

Monetary financing and credibility, the calibration's cases:

- **Indonesia 2020-22, anchored:** BI primary-market purchases ~3%, 2%, 1.4% of GDP a year;
  inflation ~1.7%; S&P outlook to negative Apr 2020 (BI MPR Dec 2021; Tempo).
- **Ghana 2022, unanchored:** BoG overdraft 7.2% of GDP; inflation 12.6% -> 54.1% in 12 months,
  cedi -29.9%, ratings to default (IMF CR 2023/001; BoG QEB 2022Q4).
- **Argentina 2020, unanchored:** monetary financing 7.4% of GDP (IMF CR 22/92); the survey's
  12-month expectation 40% -> 49% in about 3 months (BCRA REM Apr 2020); peso -40%+.
- **Japan 1932-36:** BoJ underwriting ~4-8% of GNP gross, resold to the market; anchored while
  resold, later the post-war inflation (Takeda, U-Tokyo; BIS r131028c).
- **Suriname 2015, 2019-20:** 11.6% and 15.9% of GDP; inflation 25-61% (IMF DP/2022/001).
- **IMF DP/2022/001** ("Monetary Finance: Do Not Touch, or Handle with Care"): a 10% rise in base
  money raises inflation ~1.5pp on impact, at most 0.9pp from 2% inflation; low independence
  triples it; no significant link under a 3%-of-GDP deficit; an EM purchase announcement of 1%
  of GDP in 2020 raised expectations only 0.04pp.
- Speed: surveyed expectations and the currency within 1-3 months; inflation in 6-12.

## What was built

- **`HoldingsRules`** (new; owned by `CentralBank`, saved under its own key
  `DataSave.holdingsRules`): four dials in `Dial`'s saved order, each change a
  decision-log line.
  - *Pace*: Auto, or 0.25% to 25% of the term paper a month.
  - *Shrinking the book*: runoff and sales at market, or runoff only.
  - *Auction share*: 0 to 100% of each term issue.
  - *Direct purchases*: not allowed, or allowed.
- **The pace.** `CentralBank.stepFor`: Auto is main's QE_SPEED rule, bit for
  bit. A set pace is pace × the term paper a month, both ways. In sale mode a
  set pace bounds runoff and sales together: `rollMaturities` records the
  month's runoff (`centralBankRanOff`), and `openMarketOperation` sells only
  what is left of the step.
- **QT.** `CentralBank.runoffCap`: uncapped at Auto with sales (main's rule).
  Under a set pace or runoff only, `Game.centralBankRolls` caps what runs off
  at the press at the step. Runoff only sells nothing.
- **The auction share.** `Game.rollCentralBankAtIssue`, under the law:
  - share × the face the market bought, at every term issue waiting to
    settle, pro rata over the pieces;
  - each piece at its own price (`settleDue / unsettledFace`, the auction's);
  - within the dial's room (`Game.auctionShareTake`);
  - paid through `CentralBank.buyAtAuction`, then `buyPaper`, and booked
    ISSUED in MoneyAudit (Scope.MONEY);
  - recorded as lines `BOUGHT_AT_AUCTION` and `PAR_AT_AUCTION`, a MADE row
    on the money statement, and a sale note (`Debt.CENTRAL_BANK_AUCTION_SHARE`).
- **The cost.** `CentralBank.turnFinancing` keeps a stock of auction purchases
  (a twelfth forgotten a month) and reads it against the national accounts'
  rolling year of GDP. `Expectations.financed`:
  - the strength is 0 up to FINANCING_ANCHORED (3% of GDP), 1 at
    FINANCING_UNANCHORED (7%), linear between;
  - credibility falls strength/6 of the way to KMIN at once;
  - the month's premium on expected inflation is FINANCING_PREMIUM (9 points)
    × strength × (KMAX − κ)/(KMAX − KMIN);
  - the currency moves through the real rate gap (`Game.realRateDifferential`).
- **Saves, no format bump.**
  - The rules are under their own key; a save without it reads the defaults.
  - `CentralBank`'s array has three slots appended (the auction's two
    lifetimes and the stock), guarded. The line block is length-prefixed, so
    the two new Lines read zero from an older save.
  - `Expectations.SAVE_SLOTS` is 9 (the premium), guarded.
  - CentralBankPaceCheck 7 strips all of this from a save. It reads the
    defaults and plays its next month to the bit as the same save with them.
- **Screens.**
  - `ui/HoldingsRulesPane` is BankRulesPane's shape (Kept + Ladder). It is on
    Policy's "Bank rules" page and on the central bank's own Policy page, with
    a line on what the bank has lent at auction lately and the premium.
  - Policy's holdings card reads main's words exactly at the defaults
    (`PolicyScreen.holdingsLaw`, `holdingsPace`). Otherwise it states the set
    pace, the auction share and runoff only.

## Design calls

1. ★ **A class of its own, not four more `BankRules` dials.**
   BankRulesCheck section 4 fences `BankRules.Dial` at the nine measured
   rules of CBK3 Part 1. A key of its own also means an old save lacks it
   and reads the defaults: no format bump under the old-saves rule.
2. ★ **Auto is the default pace, and it is main's rule** ("the pace default
   reproduces QE_SPEED's path"). A set pace is a fixed share of the term
   paper a month, as the Fed's purchases ($80B + $40B from June 2020) and its
   QT caps are fixed amounts, not a share of the gap.
3. ★ **A set pace bounds runoff and sales together.** The Fed's caps bound
   what runs off; the BoE's £80bn a year was its sales and its runoff
   together. At Auto the runoff stays uncapped, as in main.
4. ★ **"Sale at market" is a sale to the commercial bank at the curve's
   market value.** The city's paper has no order book (`BondMarket` and
   `OrderBook` are corporate bonds and shares), so the commercial bank is the
   market for city paper here. The gain or loss lands in `paperGains`, then
   the bank's profit and its remittance. The BoE sells at auction to dealers
   at market prices, and HM Treasury indemnifies its losses. Here a loss is
   the bank's own, through its profit.
5. ★ **The auction share bids at every term issue, the rollover's
   refinancing included.** The Bank of Canada takes its 13% minimum at every
   nominal bond auction. The share is an add-on over the market's face
   (Fed-style), at that issue's own price, and is bounded by the dial (the
   cap), not by the pace ("always within the cap"). The open market's step
   reads the book after it.
6. ★ **With the law off, the bank buys at auction only its own maturing
   paper** (main's rollover). This follows the Federal Reserve Act s.14(b)
   and the NY Fed's rollover practice, TFEU Article 123, and Japan's Public
   Finance Act Article 5. The share's dial can be set with the law off, and
   it does nothing.
7. ★ **The cost of leaning on it, calibrated from the cases above.**
   - Nothing up to 3% of GDP a year: Indonesia 2020-22 stayed anchored, and
     the IMF found no link under a 3% deficit.
   - The full cost at 7%: Argentina 2020 (7.4%) and Ghana 2022 (7.2%), both
     unanchored within a year.
   - At the full cost, a sixth of the way to KMIN a month: both moved within
     1-3 months.
   - Up to 9 points of premium, Argentina's survey going from 40% to 49%. The
     premium is in proportion to the trust already lost: the IMF found that
     low independence triples the effect.
   - The first draft's anchored line was 1%. It moved to 3% when the harness
     set the line against the cases it cites.
   - Measured on CentralBankPaceCheck 6's twin towns, lending 12.3% of a
     year's GDP over ten months:
     - credibility went from .70 to .37;
     - expected inflation went from 2.99% to 11.85%;
     - the D$ was 10% weaker (1.351 to 1.483 per US$);
     - stopped, the stock fell under 3% of GDP in 16 months, and credibility
       went on falling while it was over.
8. ★ **The stock is read against `NationalAccounts.getAnnualGdp()`, the
   rolling year.** The first draft used `EconomyManager.getYearGdp()`, which
   is struck only at a load. The harness found it: the twins read 799% of
   GDP.
9. ★ **Held figures (AP5-AP8): no new writer.** The held figures are the
   stacks' posts (AP5), the month's sector walks (AP6), `warranted()` (AP7)
   and the bond market's books (AP8). CBK2 writes none of them. Its writes go
   through `Debt.addOnForCentralBank` (the rollover's writer, now with a sale
   tag) and `CentralBank.buyPaper`. DebtManager's curve cache is keyed by
   value. So there is nothing new to raise.
10. **An observation, not this batch's.** The treasury's advances, the line
    through which the bank lends the treasury directly, carry no credibility
    cost, in main as here. Leaning on them is monetary financing too.
11. **The fixture's scale.** CentralBankCheck 19's town carries a serial of
    $20,000k against a year's GDP of about $128k, about 156× its GDP. With
    the share at 100%, the serial's first slice, refinanced at auction, is
    19 years of GDP. Section 6's twins roll that slice alike before the
    share opens, so the share meets only the treasury's own sales.

## Checks

**CentralBankPaceCheck (new): all passed, 22 s.** It plays 78 months, every
one held to the money audit; the worst residual is 0.000000 (a cent is
0.000259). Each section is caused on its own copy of one town:
CentralBankCheck 19's five-year serial, held by the households, the bank and
the central bank at a .4 dial, saved the month before its first slice.

1. The pace both ways. A set pace moves the book by exactly pace × the term
   paper, up and down. Auto's step is the QE_SPEED rule to the bit in all
   three of its arms. When the dial is nearer than the step, the book moves
   only to the dial.
2. The runoff's caps. Runoff only sells nothing. What runs off is the least
   of its par falling due, its excess over the dial and the pace.
3. Sales at market. PAPER_GAINS is SOLD_PAPER less the face, exactly, with
   the paper at, under and over face. The price per face is the curve's to
   `CURVE_DRIFT` (3e-4), the drift measured between the window and the probe;
   a sale at face would be off by 5e-2.
4. Auction purchases. The take is the share of the face the market bought,
   at each piece's own price and pro rata over two pieces. The dial's room
   binds exactly, and nothing is taken at or over the dial. The MADE lines
   sum to `getIssued()`.
5. The law off. At a share of .5 nothing is taken, and the rollover is
   exactly its own par. With the law on and a share of 0, nothing either.
6. Credibility, unit by unit to the bit:
   - the strength's line;
   - one month at half and at the full cost;
   - fifty years bounded both ways;
   - the cases (Indonesia's 3% free; Argentina's nine points at KMIN;
     three months of Ghana's 7.2% over half the premium; a year of it under
     a quarter of the trust above KMIN);
   - `takeMonth` adding the premium to the bit.

   The twin towns are described in design call 7. Their lending stock
   turned to the bit every month, and stopped, it is forgotten a twelfth a
   month.
7. Defaults and saves. The defaults are the rules before CBK2. Every rule,
   both lifetimes, the stock, the auction lines and the premium come back
   from a save to the bit. The saved city and the one it was saved from play
   their next month to the bit. A save stripped to before CBK2 reads the
   defaults and plays to the bit as the same save with them.
8. The money audit over every month played.

**Run here, one JVM at a time:**
- `--fast` is green, with Stale at 0 firm findings after CLAUDE.md's
  interface count went to fifty-eight (`ui/HoldingsRulesPane`).
- Exit 0: ExpectationsCheck, BankRulesCheck, CentralBankStatementCheck,
  SaveFileCheck, ReadPathCheck, and KeptCheck (28 min).
- HoldersCheck exits 3: main's known red only.
- CentralBankCheck, run directly (it is not for a background batch-check here),
  exits 0 in 3,011 s with no FAIL line.

**The pick** (`sh tools/pick.sh origin/main HEAD`) is ALL, 265 harnesses
("AllChecks.java runs the suite"). The review line is "needed - money:
CentralBank.java, CentralBankStatements.java, Debt.java, Expectations.java,
ui/CentralBankScreen.java; saves: DataSave.java".

**The default playtest does not move.** The default `LongPlaytest` ran with
`-Dplaytest.trace`, `-Xmx4g` and both data files named, one JVM each. Main
ffd07ee15 was built apart from `git archive` (1,974 s); CBK2 ran at
e054864e1's model (2,057 s). Both exited 0, and all eight files are
identical:

| trace file | md5, main ffd07ee15 and CBK2 |
|---|---|
| t-bank.csv | 06f5e52b70d26ef718312f43827c0685 |
| t-bonds.csv | 9ed120b19e0ecc1a62296b4012df94ea |
| t-borrow.csv | c87d567686deb691c85ac53ad23e47f7 |
| t-build.csv | b1ba2dcf969de44591c5d2db138f015b |
| t-house.csv | e1d7a1b4b241ce0b4520ddc7bcbde827 |
| t-labour.csv | 723a0c4e3f825fc49b464163adeb89e3 |
| t-month.csv | 6f7a0008b9e310c37226e4d86ffd262c |
| t-pop.csv | 2243deb50f586248f10ef60abac81404 |

## For the manual

(For the page's next edition; `docs/manual.*` is not this batch's.)

**The holdings' rules** (Policy › Bank rules, and the central bank's own
Policy page). The holdings dial is a cap: the share of the city's term paper
the central bank may hold. Four rules say how it gets there.

- **Pace.** Auto moves the book a quarter of the way to the dial a month, as
  it always has. A set pace moves it by that share of the term paper a month,
  up or down, until it reaches the dial. The Fed bought $120B a month in 2020
  and capped its runoff at $60B a month in 2022.
- **Shrinking the book.** *Runoff and sales at market*: paper over the dial
  that falls due is not rolled, and the bank sells the rest of the month's
  step to the commercial bank at its market value. A gain or a loss on the
  sale lands in the bank's profit, so in its remittance to the treasury. With
  a set pace, runoff and sales together stay within it. *Runoff only*: it
  sells nothing, and lets no more run off than the pace.
- **Direct purchases.** *Not allowed* (the default) is the law at the Fed,
  the ECB and the Bank of Japan: the bank buys at the treasury's auctions only
  to replace its own maturing paper. *Allowed* opens the auction share.
- **Auction share.** Under the law, the bank bids this share of each new term
  issue, on top of what the market buys, at the auction's own price and never
  past the dial. The Bank of Canada takes at least 13% of every bond auction.
  The money it pays is new money, and the money statement shows it as "Paper
  bought from the treasury at auction".

**What leaning on it costs.** Up to 3% of a year's GDP a year costs nothing:
Indonesia lent its treasury about that in 2020 and stayed believed. Past it,
people trust the bank less each month. They expect more inflation, by up to
nine points at 7% of GDP a year (Argentina 2020), and the D$ weakens with
it (Ghana 2022: the cedi lost 30%). When the bank stops, the lending is
forgotten over about a year, but trust comes back only as it always does:
by holding the target, over years. The page shows what the bank has lent
lately and what that adds to the inflation people expect.
