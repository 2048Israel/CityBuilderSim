<!-- Copied 2026-11-27 from C:\Users\Jerus\Documents\Ashbourne\dev-findings.md (Jerus's, the desk chat's reading of slot 2), verbatim below this line, so ASH1-ASH4's figures stay where the briefs cite them. -->

# Ashbourne Term V - findings for a fix batch

Found while governing Ashbourne (slot 2, 0.9.215, month 193 = Jan 1916, to month 241 = Jan 1920), from the save
read headless. For the orchestrator to brief, after Jerus's review. Newest at the bottom.

## Index, by kind (closed at month 241)

| # | Kind | One line |
|---|---|---|
| 1 | bug | The overview's POWER row ignores the grid: "204% served" while 4.6% of the month went unserved |
| 2 | design | The grid's only step past the free 10 MW is a Cr 22.1M, 100 MW substation (153% of a small town's GDP) |
| 9 | design | The substation's 140 posts take a small town's diploma holders and carry nothing |
| 15 | design | Business Services waits on engineering licences; the school advice counts no students, so never asks |
| 13 | design + warning | The buyback offers the bank's paper at "fair" and books the whole discount on its capital, unannounced |
| 5 | check | Households emptied 91% of their deposits into one bond |
| 6 | question | A shortage with the shelf price pinned at its floor; Retail weighs only the format it cannot staff |
| 12 | check | The yen bond bought back at 27.37 per 100 (stop 85) |
| 10 | question | The rollover nets maturities against a surplus that counts prepaid city builds |
| 14, 14b | question | The rule answers a world food shock at 22%, twice, and every swing is a bank swing |
| 16 | check | Households merge by 8% in the month a construction boom ends |
| 11 | check | The power load fell 13% while the town grew 19%; the struck price fell 77% |
| 3 | question | `FULL_STRESS_MULTIPLE` = 150: AAA at 195% of GDP |
| 4 | check | A ten-year call on a 30-year bond costs 302 bp |
| 7 | check | The rule moves 261 bp in two months |
| 8 | minor | The overview's months-left for a city build runs high (28 against about 18) |

Not a finding, for the record: there is no voluntary bank recapitalisation above the 8% minimum
(`Bank.recapitalisationNeeded()`); that is deliberate (0.7.8) and realistic.

## 1. The overview's POWER row ignores the grid (a bug: the screen says the opposite of what is happening)

- **Seen:** the needs list reads "POWER 204% served at the peak hour", green. Yet the dispatch carries
  only `carryKw` 10,000 (the founding grid, `UtilitiesHandler.getFoundingGridKw()`) against a peak load
  of 12,713 kW: 2,713 kW unserved at the peak, 331,600 kWh (4.6%) in the month, power ratio 0.924.
  The year book has power short in months 186-193. Every sector's output is scaled by that ratio (`Sector.java:1059`).
- **Why:** `CityNeeds.measure()` (`CityNeeds.java:449-451`) judges power on firm supply against the peak
  (`BuildAdvice.supplyDemand(POWER)`). The wires are a separate measure, `BuildAdvice.Kind.GRID`, which
  appears only in Build's Utilities tab (`BuildAdvice.measuresOf`, line 306), never on the overview.
- **Effect:** neither Monday's dossier nor the overview's suggestions (highway, health centre,
  childcare) mentioned the grid. A player only finds it by opening Build → Utilities.
- **Want:** the overview shows the grid. Either a GRID row, or the POWER row reads the lesser of
  plants and wires, red when the wires shed load. NEEDS YOU should count it.

## 2. The grid has one step, and it is far too big for a small town (design)

- **Seen:** past the free 10 MW, the only grid unit is the Substation (id 155, `Substations.SUBSTATION_MW` = 100):
  Cr 22.1M, 132,000 points, about 40 months with Ashbourne's builders. That is 153% of a 6,166-person
  town's GDP, for ten times the capacity it needs.
- **Reality:** grids of 1916 grew in small distribution substations of a few MVA, priced per kW.
- **Want:** a smaller unit (about 10 MW at about a tenth of the cost) or grid capacity priced per MW, so a town can grow past 10 MW in
  steps. The batch should size it.

## 3. A question, not a bug: is debt still too cheap? (`DebtManager.FULL_STRESS_MULTIPLE` = 150)

- Each credit measure only reaches its 5-point maximum at 150 years of GDP or revenue (`DebtManager.spreadFor`, dial at line 683).
  Ashbourne carries 4 years of revenue and pays 0.32 points over the floor. Cr 40M (10 years of revenue)
  would still rate AAA.
- The comment at line 679 says this was deliberate (the $900k-on-2,000-people quote at 18.23%).
  Worth Jerus's look whether the ramp is now too flat at the other end: a city can borrow 10 years of revenue at AAA.

## 4. Worth a look: the call costs 302 bp

- Borrow → Bonds, 30 years, Callable at 100 from Jan 1926: "the call adds 302 bp to the coupon"
  (8⅛% against a fair 5.19%). Real callable munis usually pay tens of basis points for a ten-year call,
  up to 1-2 points in very volatile years. It may be correct for this city's rate history (3% to 12% and back). Check what
  volatility the price uses.

## 5. Worth a look: households emptied their bank accounts into one bond (month 193 to 196)

- A Cr 23.0M 30-year city bond: households took Cr 9.65M of it, and household deposits fell from Cr 10.8M to
  Cr 0.94M (-91%) in three months. The bank's overnight borrowing at the central bank rose from Cr 40.4M to 47.7M,
  and business debt fell from Cr 25.0M to 21.3M.
- Reality: households keep a transaction balance; a long bond draws on savings, not on the money for next month's rent.
  Check whether the household bond demand (householdBondRatio / householdPaperRatio) leaves any floor under deposits.

## 6. Question: a shortage with the price pinned at its floor (month 195-196)

- Hunger went from 0% to 5.7%, then 3.9%: baskets asked 6,031, handed 5,724, hungerPricedOut 0, and shelfPrice equal to
  shelfFloor all the while. Retail will not add a shop because it can staff only 61% of a Small Grocery Store
  (2 college posts at an 11% fill), and it does not seem to look at the smaller Convenience Store, which has no college post.
- Two things to check: whether a short shelf should raise the price above the floor before anyone goes hungry,
  and whether Retail should weigh every format it could staff.
- Follow-up: 3 city-built Convenience Stores ended the hunger within two months (0% by month 199).

## 7. Worth a look: the monetary rule moves in jumps (months 192-199)

- Policy rate by month: 2.86%, 3.38%, 2.85%, 3.85%, 3.13%, 1.24%, 1.95%, 2.65%: 261 bp down in two months, then back up,
  with inflation and expectations steady (expected 2.4% to 2.2%). The price index fell 1% in one month (month 196), then rose 0.65-0.8% a month.
- Reality: real central banks move 25-50 bp at a time and smooth their rule. Rates this volatile also price the
  302 bp call in finding 4, and moved the bank's funding cost in the 1911 crisis.

## 8. Minor: the substation's months on the overview

- The overview's POWER row says "1 on the way, ~28 mo" at month 199, while the site has done 32,091 of 132,000 points at
  5.0-5.7k a month (months 193-199), which is about 17-18 months. Check which pace the estimate uses.
- Month 205 it said "~16 mo" with 66,197 points left at 5.6k a month (about 12). Same gap.

## 9. The substation's 140 posts drain a small town's workforce, and carry nothing (design)

- Substation (id 155) jobs: DIPLOMA 100, COLLEGE_ENGINEERING 30, UNIV_HIGHTECH_ENG 10. `Substations.costs()`: carry is
  `n * SUBSTATION_MW * 1000` whatever the staffing; the posts only add payroll.
- In Ashbourne (7,342 people, DIPLOMA fill 0.938, roughly 1,100 diploma posts) the 100 posts are about 9% of the town's
  diploma holders. Every business there is held back by diploma and college staffing already (finding 6, and every
  "could staff 50-61%" investor line), so opening the grid will pull workers out of the shops and works.
- Reality: distribution substations are largely unattended; the line crews of a district utility scale with the load
  they carry. Want: posts that scale with the kW actually carried, or come with the smaller unit of finding 2.

## 10. The rollover re-borrows when the city is in surplus (question)

- Rollover "Same structure" nets a maturity against the last 12 months' surplus (`Rollover`, Jerus's "net of last year's surplus").
  A prepaid city build counts in that surplus: the Cr 22.1M substation (month 193) made the year's surplus about -Cr 22M,
  so with an operating surplus of Cr 170-360k a month (months 200-205) the rollover still re-borrowed the Cr 2M serial bond's
  December slice as a new Cr 448k 5-year serial at 5.256%, and would have rolled the Cr 1.2M floating note at about 12.1%.
- Reality: governments net maturities against the operating balance; capital spending is financed apart.
  Check whether the netting should leave city capital spending out (`cityCapitalSpending` exists in the save).

## 11. Unexplained: the power load fell while the town grew (check)

- Peak block load in the dispatch: 12,713 kW (month 193) to 11,072 kW (month 205), -13%, while population rose 6,166 to
  7,342 (+19%). The struck commerce and industry price fell from 0.0487 to 0.0110 (-77%).
- It may be real (power-hungry works shrinking under 11.7% rates), but check that the load and the struck price are
  the same units in both months.

## 12. Check: the yen bond bought back at 27.37 per 100 (month 205)

- "Bought back Cr 992,435 of TERM paper for Cr 271,652 (27.37 per 100 on average, stop 27.37)": ¥72M, 3.5% coupon,
  312 months left. My stop was 85. At 27.37 the implied yen yield is about 14%; real 1917 yen yields were near 5-6%,
  where the bond is worth about 70-75. Either the world's yen curve sits that high in this path, or the price is
  read in the wrong money. The city gained about Cr 720k on it.

## 13. The bank's hidden loss on city paper, and a buyback that looks like a bargain (design, plus a warning)

- At month 211 (policy 22.4%) the fair prices of the city's paper are 65-85 per 100; the bank carries about Cr 15.5M of it at
  amortised cost (`Bank.sellPaperBack`, 0.7.1), so about Cr 4.9M of loss is unbooked, near its whole equity. Realistic (held-to-maturity,
  as with SVB in 2023).
- But Finances offers the player that paper at "fair" 65 as if it were a bargain: a buyback with a stop near fair takes
  only the bank's slices, and books the whole discount against its capital the same moment. Nothing on the buyback screen says so.
- Want: the buyback card shows the bank's loss on the slices it would take, and its capital ratio after; and the same
  for the central bank's holdings dial, whose trades with the bank book the same loss.

## 14. Question: the rule answers a food shock at 22% (months 205-211)

- At Standard the rule set 11.7% then 22.4% on year-on-year inflation of 14.7%, driven by groceries (+9.8% in 6 months)
  and food (+9.4%), while the crown rose 30% and the last month ran at 7.1% annualized. The bank went from +Cr 313k a month
  to -Cr 194k, business debt fell Cr 2.5M in one month, unemployment rose to 16%.
- Reality: central banks look through food and energy (core inflation) and react to momentum, not the 12-month figure alone.
  Worth asking whether the rule should read a core index or a shorter window.

## 15. A sector waits on a school the advice never asks for (design)

- Business Services has said "Holding: 39 more engineering licences before a practice could open" every month from
  193 to 217. The licences come from engineering graduates (an Institute of Technology).
- The school advice counts only who "would come and be hired" into posts (`BuildAdvice.school()` / `schoolNeed()`):
  at month 217 an Institute of Technology quotes need 0.0, so no card or need ever points at it. The licences a sector waits
  for are not posts, so they never reach the count.
- Want: a sector's licence wait counts as demand for its school, or the need list names the sector waiting on one.

## 14b. The same cycle twice (months 226-232), adding to finding 14

- Back on Standard at month 226, the rule took the rate from 9.7% to 18.6% in six months while the one-month inflation
  fell from 14.6% to 4.3% annualized. The bank went from +Cr 159k a month to -Cr 311k, capital 12.7% to 10.9%;
  business debt fell 21% (Cr 13.2M to 10.4M). The same as months 205-217.
- The bank's model (Cr 46-50M overnight at the policy rate, loans and city paper at fixed rates) makes every rule
  swing a bank swing. Worth asking whether the bank should fund at term, or the rule smooth.

## 16. Check: households merge by 8% in the month a construction boom ends

- Month 216 to 217: households 2,069 to 1,989 (and 1,913 by month 220) with the population flat. Month 231 to 232: 2,164
  to 1,988 in one month, population 8,114 to 8,090. Both are the months the builders were laid off (593 to 404, 601 to 156).
- Doubling up when work goes is real, but 8% of all households in one month is fast. Check the household formation rule's
  reaction to a job loss: whether it reads one month's unemployment, and whether it unwinds as the jobs return (it did: 2,135 by month 226).
