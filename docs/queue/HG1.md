# HG1 - an empty shop starves a town, and the screens say nothing

Jerus, desk chat, 2026-11-28, about 11:00. He sent a screenshot of his new city: 8 December 1906, month 84, 1,075 people, on 0.9.219. Its inflation tile read "rate in ~37 mo", and he asked "random question, did the inflation broke?". After the desk chat's findings he said "Yes, send it". He wants it urgently: at the top of Queued, started in the first lane that frees.

## The evidence

**The copy.** The desk chat copied his autosave: month 79, gameVersion 0.9.219, saveFormat 67. His saves are untouched. It is in `C:\Users\Jerus\AppData\Local\Temp\cbs-kept-hunger\`:
- `slot-01.json`, `slot-01-history.json` and `slot-01-map.bin`;
- beside them, `InflProbe.java`, the scratch driver.

The driver loads the copy with `GameFiles.scratch` and `Game.loadGame(1)`, plays it with `toggleNextMonth()`, and prints `Game.indexSpends()`, `PriceIndex.isBased()` and `monthsUntilRate()`, and the hunger getters. Run it with `-Dcbs.earth` and `-Dcbs.world` from the repository's data folder. Work on copies only: never read or write his saves folder beyond copying a slot out.

What it read on origin/main ffd07ee15 (CBK3's merge), with a scratch-only print just before `priceIndex.takeMonth()` (Game.java about 15131):

- **The price index never bases.** Its save array has based 0 and shoppingMonths (index 22) 0. In 79 months no month had both groceries and rent over `MIN_BASE`, so `monthsUntilRate()` reads 24 + 13 = 37 for ever.
- **`indexSpends()`:** groceries 0.000000 every month; rent 4.30-4.44 (in save units); meals 3.5-4.2.
- **The shop sells no food.** When the index reads, Retail's `pending().sold` holds JET 0.13 and THERMAL_COAL 0.16, and no GROCERIES entry at all. So `getCurrentGrocerySales()` (Retail.java:316) reads 0. That getter was changed on 11-24 in 40806e6f8, "Complete CPI acquisition coverage...".
- **Retail's saved extras are all zero:** lastMonthSales, productsSold, supplyBaskets, deliveredShare, handOver and demand. The households want 872, and the baskets needed are 953. Its cash is 0.2255, its supplierCredit.limit is 0, and every food in its pantry is 0. One shop stands; 32 farms stand.
- **The town is hungry.** `HouseholdBalance.getHungerRate()` is 0.975: priced out 0.079, short of stock 0.896. `Health.getHungerRate()` is 0.080.
- **The screens say nothing.** The top bar says "Nothing needs you", and Build says nothing is needed. The policy rate sits at 2.00%, because the rule reads zero inflation while the index is unbased.

## What to build

1. **The cause first.**
   - Find why Retail never stocks groceries in this city from its founding: no cash, no supplier credit and an empty pantry, while 32 farms produce. Where does their food go?
   - Name the loop that keeps it at zero, and break it at its cause, not with a grant. A grocer whose households ask for 870 baskets, with farms beside it, should be able to buy stock. That may be supplier credit, the bank's working capital, or the farms selling to it on terms: whichever the real case is (check reality: look it up).
   - Then check whether 40806e6f8 (11-24) or CBK3's credit rules (0.9.219) are involved.
   - Today's harness cities do base their index (CentralBankCheck 286, CurveCheck 438). So the cause may be this city's founding conditions: find which.
2. **Hunger reaches the screens.** A town where 97.5% go hungry never reads "Nothing needs you". The needs (`CityNeeds`) and NEEDS YOU count food from the households' hunger, not only from what the city builds.
3. **The index cannot wait for ever.**
   - If a city's grocery sales stay at zero, the basket still bases on what the city does buy (rent, meals, fuels), at their weights. Groceries join it when they appear.
   - The rule must never read zero inflation because the basket never formed.
   - Keep the BOTH HALVES lesson (PriceIndex, about line 270: a basket of rent alone made a closed loop). The base needs real outside prices in it: meals, fuels, or the world's food price.

## The checks

- **His city.** The save copy, played on, stocks its shop, and its hunger falls to a town's normal within a bounded number of months.
- **The trap.** A fixture causes the empty-shop trap: red on main, green after.
- **The screens.** The needs show red at high hunger.
- **The index.** A city with no grocery sales bases its index within a bounded number of months, and the rule reads a real rate.
- **The money audit** is exact to the cent.
- **The ensemble's verdict**, since the default trace may move.

## Order

- **At the top of Queued**, ahead of everything waiting (Jerus, 11-28). Nothing here is his call.
- His other cities and the old-saves rule apply as usual.
- **The manual is not this batch's.** An edition comes only at a milestone or at Jerus's request (docs/orchestrator.md). The notes carry a "For the manual" section, and `docs/manual.*` stays untouched.

Versioned. Money may move (a grocer's stock on credit): a stranger's review at "mends at". An 80-run ensemble if the default trace moves.
