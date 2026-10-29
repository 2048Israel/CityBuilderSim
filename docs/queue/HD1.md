# HD1 - the hoard: interest abroad as profit, spare cash paid out, Real Estate staffed

Queued 2026-10-28 from a read-only study of his San Francisco city (the
autosave, month 2,852, 0.8.62), the histories of slots 1-5 and the decade
book (`%APPDATA%\CityBuilderSim\decade-book.csv`). Jerus: "interest abroad as
profit sure", "gotta add workers to real estate", and "Yes, queue it" to the
three as one batch. Figures are screen dollars.

## What the study found

Every run ends with the businesses, the households and the treasury all
saving and the world the only borrower (net exports 37-47% of GDP from about
month 600). At m2,852, against GDP of about $8.6T a year:

- **Businesses' US dollars abroad** (`OutwardInvestment`): $406.8T, 47x GDP.
  57% of it is the 2% coupon compounded, 44% revaluation as the D$ fell from 1
  to 24 per US$; over the run $43.7T went out and $48.8T came home. The coupon
  is reinvested abroad (`OutwardInvestment.java:198-212`) and is no line of
  net income (`SectorBooks.java:132`: "it never reaches the till"), so no tax
  or dividend sees it. Households' coupon has been paid home as income since
  2026-09-17 (`HouseholdBalance.java:3080-3083`); the businesses' never was.
- **Businesses' own cash:** $103.9T, 12x GDP. `Equity.PAYOUT` pays 40%; the
  buyback rests at fair x 1.10 (`Exchange.java:892`) and fills $0.58B of
  $4.4T bid a month, because households, who hold about 100% of every company
  from founding (`HouseholdBalance.grantFounders`), rarely sell. Shares trade
  at about 1.1x book everywhere: book is 95% cash and dollars abroad.
- **Why it goes abroad:** the bank pays savers only out of its margin
  (`Bank.java:3553-3560`), its loan book is gone, so the deposit rate is
  0.0003% and `OutwardInvestment`'s target (spread x 40, capped at 0.9) puts
  80% of each sector's wealth abroad.
- **Lending's fade is the mirror.** A business borrows only the gap in its
  cash (`BusinessDebtManager.java:1452`, `Game.java:8767`), so once cash-rich
  it never borrows and its 36-month loans and 120-month bonds run off. Business
  debt peaks at m810 and is under 1% of peak from m1400 (slot-01 m937 and
  m1536, slot-03 m806 and m1335); the last bond is m1533. Bonds are in no
  history series (`HistorySave.java:679` counts loans and mortgages only).
- **Real Estate employs nobody:** its three buildings (ids 0, 1, 6) have no
  `jobs`, against $57B a month of revenue and $48.4B of operating income.

## His earlier call, overturned

0.7.12 round 4 removed the special dividend on Jerus's words: "When a
company's buyback finds nobody selling at a fair price, the unspent cash stays
in the company. It sits in the bank or buys bonds, as real open-market
buybacks do." (`Exchange.java:852-860`). Late in a run neither happens: the
bank pays nothing on it and no company issues a bond to buy. The quote was
found after his yes and put to him with this row; if he keeps the old call,
part 2 goes to Pending and the rest ships.

## What HD1 does

1. **The coupon abroad is profit.** A business's coupon on its dollars abroad
   is a line of its net income, so the business tax and `PAYOUT` see it.
   Recommended: paid home into the till, as the households' is. The WHY at
   `OutwardInvestment.java:198-209` is why it was not (a coupon at home
   appreciated the D$ on a rentier's surplus); say what the rate does now,
   with figures. Revaluation stays out of income, an unrealised gain, but the
   business's statement shows it.
2. **Spare cash goes back to the owners.** When a company's buyback finds no
   seller, what it holds above its cushion (cash and dollars abroad together,
   over `Exchange.BUYBACK_CUSHION_MONTHS` of costs and debt service, plus what
   its building on order still needs) is paid out: a tender at the buyback's
   limit taken pro rata by every holder, or a special dividend. The batch
   picks one and says why. Large real companies return most of their earnings
   through dividends and buybacks together. A dial for the pace, one sentence
   above it.
3. **Real Estate has workers.** Posts on its buildings in `buildings.json`
   (ids unchanged), at a staffing ratio from a cited source (property
   management and upkeep per home). First check that the staffing gate
   (`Sector.java:246`, `:371-373`: 80% of posts fillable) cannot hold back
   homes in a city with nobody in it yet: homes come before workers.
4. **Bonds in the history.** Business bonds outstanding as a history series
   (`HistorySave`, `DataSave`, the load path; `SaveFileCheck`), so the fade,
   or its end, can be charted.

## Look, and report only

Under `## pending`, each with a recommendation:

- Luxury Retail's labour: 3,219 workers, payroll 1% of its value added, the
  most operating income of any sector; a 7-post Boutique sells 320 pieces at a
  2.56x markup. Construction's payroll is 5% of its value added.
- A LongPlaytest before and after: the businesses' cash and dollars abroad,
  business debt and bonds, against GDP at m600, m1200 and m2400.

## Left for a study, not decided

From the same conversation, recommended but not agreed: the bank holding
households' savings and lending them (`Bank.java:62-68` calls that "a
different and much larger model"); margins that fall when a sector earns far
above the going rate; a tax on dividends, interest and estates; a rule for a
treasury surplus; and an exchange rate whose push builds while a surplus lasts
(today a level capped near 20%, `ForeignAccounts.balanceTarget()`). World
demand that runs out is WN3's.

## Scope and checks

`OutwardInvestment`, `SectorBooks`, `Equity`, `Exchange`, `buildings.json`,
`HistorySave` and `DataSave`. Every trace moves: compare it and run the
ensemble. A harness section for each of 1-4 whose fixture causes the case: a
sector with dollars abroad and a coupon; a company over its cushion whose
buyback finds no seller; a house built in an empty city; a bond in a saved
history. A version (`{next}`).
