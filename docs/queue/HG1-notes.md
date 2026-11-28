# HG1 - an empty shop starves a town, and the screens say nothing: notes

Builder: Claude (Opus), headless, bus HG1, branch batch/HG1 cut from origin/main bbdb5fc08.
Brief: docs/queue/HG1.md. His save copy: C:\Users\Jerus\AppData\Local\Temp\cbs-kept-hunger\ (left as it is);
the work ran on a copy in C:\Users\Jerus\AppData\Local\Temp\hg1-scratch\ (scratch drivers HgProbe, HgShops, HgStaff there).

## 1. The cause, on the copy (month 79, 0.9.219, format 67)

- **There is no shop.** The brief's "one shop stands" is the Commercial Bank: COMMERCIAL, no sector, coverage 0.
  Retail owns no building, none on site, in 79 months. getStoreCoverage() is 0, so expectedBaskets() is 0, so
  supplierCreditLimit() is 0, and recentUse() stocks for min(coverage, population) = 0: the zero cash, zero credit
  and empty pantry are all downstream of no building. Retail's operating rate is 0.873 (health alone), its fill 1.
- **Retail.plan() is held by the staffing gate**, every month: "the city could staff 13% of a Small Grocery Store now
  and 64% with a year's arrivals; it wants 80%". The Convenience Store (3 no-diploma, 3 diploma posts live) reaches
  0.622 with the draw, the Small Grocery (20/11/2) 0.636, the Filling Station 0.789. None reaches MIN_STAFFABLE_TO_ORDER.
- **Why: the diploma band is over-posted from the founding.** The city was founded the 1900 way (foundedUnfitted):
  15 people and 47 posts at month 2, and posts above the workforce every month since (591 for 516 at month 77).
  At month 79 the bands are NONE 385 posts / 389 workers, DIPLOMA 144 / 129, COLLEGE 76 / 11. The 1900 arrival mix
  (ArrivalMix) brings mostly no-diploma workers, the city's other employers hold the diploma band, so its spare
  (supply - posts) is -14.7 and every shop format has diploma posts. Unemployment 0.9%.
- **The loop.** The gate counts only spare workers (and a year's draw) -> the band is over-posted, so no shop
  passes -> no shop, no coverage, no expected sale, no supplier credit, no stock -> nothing sold, 97.5% hungry ->
  and nothing in the hunger frees a worker or moves the gate: the farms keep theirs, arrivals keep coming at 20 a
  month with the 1900 mix, and the other employers' posts keep the diploma band short. It holds at zero for ever.
- **But a shop built here would be staffed.** The fill is one rate per tier across the city
  (Sector.updateJobFillRate, PopulationManager.fillByBand): every post of a tier fills at the tier's rate, the
  shop's with them. A Convenience Store's posts joined to the bands fill at about 0.94; the gate refuses it only
  because it treats the posts already standing as owning the short band's workers.
- **The suspects.** 40806e6f8 (Retail.getCurrentGrocerySales, 11-24) reads the pending ledger right: it is 0
  because nothing is sold. CBK3's credit rules (0.9.219) are not involved: the supplier credit is 0 because the
  expected sale is 0, and no shop ever asked the bank. Neither is the cause.
- **Why harness cities base their index**: they are founded the default way, whose arrivals hold the diploma
  ("universal high school", Migration.composeArrivals()), so the diploma band has spare workers and a shop passes.
