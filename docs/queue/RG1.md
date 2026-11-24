# RG1 - farm ground at a farm price, by policy

## The question and the answer

REV-PW3b (2026-11-24, its item 4) found that a Wind or Solar Farm buys its ground
at the city's price times the crowding premium (`LandMarket`). In PW3's fixtures
that ground is 87% and 96% of the farm's capital, so a grown city builds no farm.
Real farms lease rural ground for a few per cent of their capital, and nothing in
the model prices rural ground. Agriculture's fields pay the city's price too, by
Jerus's earlier call (`sectors/Agriculture`'s header: the land price is the
sector's clock; `TaxPolicy.FARMLAND_RELIEF` relieves only the tax).

Asked 2026-11-24 09:08: power farms at a rural price? Jerus, 09:12: "Policy, that
also lets farms crops as well be cheaper".

## What to build

- **A policy, the player's choice.** Without it, ground costs what it costs today,
  so a city that never touches it behaves as now and the land trade-off he chose
  stays the default.
- **It covers every farm,** crops (Agriculture) and power (Wind Farm, Solar Farm):
  ground they buy under it is priced at the sellers' farmland price, outside the
  crowding premium.
- **The city still gives up ground.** Real farmland preservation zones ground for
  farm use, which holds its price at use value. Examples: British Columbia's
  Agricultural Land Reserve, Oregon's exclusive farm-use zones, Ontario's
  Greenbelt. The ground then cannot go to houses or works. Look these up and
  restate them in the class's WHY.
- **Start from `Zoning`.** It already holds ground for one sector "at a share of
  the going business price". Find what it does for Agriculture today. Extend it
  if it fits, rather than adding a second mechanism. Show the dial where the
  zoning dials are, through `Kept`.

## Bounds

- **Prices.** A farmland price from the sellers' own data (`LandFactors` and the
  nation's farmland figures), not a new number; if the model has none, the brief's
  first step is finding the source, cited.
- **Money.** No new payer or payee. The ground is bought as today, at the lower
  price.
- **Saves.** Saved as the zoning is. A format bump only if an old save would read
  wrongly.
- **Harness.** It causes both paths:
  - with the policy, a farm's ground costs the farmland price, and the planner
    builds farms where the default run built none;
  - without it, today's prices.
- **Ensemble** against the baseline. The trace moves where a city takes the
  policy, and the default run does not take it.
- **Order.** After PW3, which builds the power farms and their planner.
