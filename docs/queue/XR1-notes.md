# XR1 - notes

*0.8.19, 2026-10-21. The brief is the exchange-rate study,
[spec-exchange-rates.md](../studies/spec-exchange-rates.md) §3.1-3.4, §4's
board 1 and §5's XR1 row, with Jerus's answers of 2026-10-21 (★1, ★2, ★7,
★8). Jerus on today's rule: "the current one is wierd".*

## 1. What changed

The D$ is now a level, struck every month:

    ln rate = parity's trend + gate x (the real gap's level + the balance's + the cover's + the market) + interventions still felt

- **Parity** is the city's basket with rent's weight held to a third
  (`PriceIndex.RENT_CAP`, ★2), chained beside the index on the same bases
  (`getParityLevel()`), against the world's prices.
- **Its trend** (Holt: a sixth of a jump at once, the slope over two years)
  is followed one for one: no lag (`TREND_LEVEL`, `TREND_SLOPE`).
- **The real gap's level**, `-GAP_CAP x tanh(PERSISTENCE x gap / GAP_CAP)`,
  60% of a change at once and the rest with a nine-month half-life (★8).
- **The balance** (a year's overall balance against a year's GDP, x0.5,
  at most 20%) and **the vault's cover** (an empty vault 10% cheaper, none
  at `THIN_COVER` months), each reached half in a month.
- **The market**: AR(1), 2.2% a month, half-life 42 months, seeded from the
  world seed at founding (`Game.marketSeedFor()`), one seed one path (★1).
- **The gate**: the market and the fundamentals reach the rate in proportion
  to openness until the city trades a tenth of its output; nothing below
  `MIN_TRADE`.
- **Interventions move the rate** at 2% per 1% of a year's GDP, half gone in
  six months (★7): the treasury's purchases and sales, felt at the next
  reprice whenever they were made (`dealtSinceReprice`, saved), and the
  defence's sales.
- **The defence leans**: in a month the market and the fundamentals would
  cheapen the D$, the central bank sells what takes back `absorption()` of
  that fall, at most `absorption()` x the month's deficit and the vault, and
  the support fades. It leans against neither parity's trend nor its own
  support fading (§4 below).
- **Retired**: the pull at REVERSION, the anchored drift (and
  `Game.anchoredDrift()`), RATE_PULL's push and its UIP level,
  SETTLING_MONTHS, DEAD_BAND, DRIFT_SPEED, the previews.
- **The save**: `SAVE_FORMAT` 43, the floor raised to 43 (an older save's
  currency would load wrongly: its trend would open at the founding's
  parity). `HistorySave` gains `fxFair` and `fxMarket`.

## 2. The slices and their review

| slice | by | what | the review changed |
|---|---|---|---|
| 1 the model | Sonnet 5.5 xhigh | PriceIndex's capped parity, the float, Game's wiring and seed, every caller restated | (to fill) |

## 3. Found on the way: the float spirals

**XR1 as designed makes the default playtest's prices run away**, the 0.7.2
ring the study's §3.2 warned about, and no dial inside XR1 stops it. The
default playtest, 4,000 months, each variant built from 58f7d2b with one
dial changed (prices at the checkpoints near m600 / m1060 / the end, against
founding; people at the end):

| run | what | prices | people |
|---|---|---|---|
| main d2869a5 | today's rule | 4.7 / 15 / 1,036 | 573,349 |
| XR1 as built | Holt trend, level 1/6, slope 1/24 | 14 / 12,225 / 1.7e9 (the D$ at its 1e9 guard from m2160) | 10,877 |
| A | level 1/6, no slope | 8.6 / 1,502 / 2.3e9 | 167,882 |
| B | level 1/24, no slope | 63 / 346 / 2.0e6 | 325,960 |
| C | level 1/60, no slope | 7.1 / 33 / 26,790 | 494,579 |
| D | as built, the market quiet | 199 / 1,357 / 2.9e9 | 67,692 |
| E | as built, the real-rate gap off | 108 / 47,017 / 1.5e9 | 12,988 |
| F | as built, the balance off | 62 / 86,520 / 3.4e9 | 37,245 |
| G | the slope the credible target (the retired anchored drift), the level pulled to parity at a 42-month half-life | 12 / 72 / 1.6e6 | 225,194 |
| H | the same, a 120-month half-life | 7.3 / 23 / 13,638 | 101,633 |
| K | today's own anchor (a pull of 0.004 and the anchored drift) under XR1's other parts | 7.8 / 32 / 11,905 | 4,795 (stalls from m1052) |
| L | as built, rent uncapped | 13 / 6,401 / 2.7e9 | 283,547 |

No single new force drives it (D, E, F): following realised parity one for
one does. A price rise moves parity, the rate follows it, imports get dearer
and prices rise again. The shelf is cost-plus and ratchets
(`Retail.repriceShelf()`: under its floor a price catches up at
FLOOR_CATCH_UP, over it it moves at CLEAR_SPEED and grows at expected
inflation), so every swing in the rate leaves prices higher. Today's rule
never followed parity (0.70 of it in the playtest), and that lag was the
price level's anchor. The study's replay of slot 3 was offline ("the rate
does not feed back into the city's prices or trade here"), so it could not
see this; §3.2 asked XR1 to measure it.

**Asked of Jerus, 2026-10-21:** (a) hold XR1 and restudy the anchor (the
study's §3.4 pass-through dial on import prices, or a shelf that falls as
readily as it rises), main on today's rule meanwhile; (b) ship XR1 on
today's pull and drift (K: the city stalls); (c) ship only the intervention
fix. **Jerus: "ship it as is, im going to test it to see if player led
behaviour can prevent hyperinflation, also it might just be that a future
batch finishes that other half of the issue".** So XR1 ships as the study
designed it, the Holt trend as built and none of the variants: **the
default city now hyperinflates unless the player stops it.** The two leads
for the batch that finishes the other half are under XR1-C in `QUEUE.md`.

**The defence's first cut** (sized "as today", absorption() x the month's
deficit) held the D$ 26% over its fair level in MonetaryCheck's town by
month 26 and emptied the founders' vault by month 42; the fall after it fed
a held 3% dial 45% a year. Leaning against its own fading support as well,
25% over by month 36 and the vault at 1.3 months by 60. The lean as built
takes back absorption() of each month's new fall and lets the support fade.

**The player's own purchases never reached the rate** in slice 1: they are
made between months, and the next month's startMonth() cleared them before
the reprice. They are now kept since the last reprice (slot 70).

## 4. The defence, redesigned in review

(to fill)

## 5. The checks

(to fill)

## 6. Every harness assertion restated, against main (d2869a5)

Two kinds, as the gate set them. **(a)** the same premise, measured the new way: the label is
kept word for word and only how it reads the model changes. **(b)** a premise a decided call
replaces (or an instrument XR1 retires): beside the changed assertion in the source there is a
comment quoting the call and the study line. Study = `docs/studies/spec-exchange-rates.md`;
"XR1 row" = section 5's "REVERSION's pull, the anchored drift, RATE_PULL's push and
SETTLING_MONTHS retired"; "3.2", "3.3", "3.6" its sections. Calls (section 6, Jerus):
star 1 "Like a real float"; star 2 "Basket, rent capped"; star 7 "Interventions move the rate, at
the real size ... Jerus: 'Yes, real size'"; star 8 "whatever real life does".
**red, a call** = fits neither kind: left as on main, red, and put to Jerus (one row, CurrencyCheck 4).

### Restated and retired assertions

#### CurrencyCheck

| section | label before (main) | label after | kind | the call / the line |
|---|---|---|---|---|
| 1 | fixture: past settling, trade in balance, the vault empty | same | (a) | `pressure() == 0` is `balanceTarget() == 0` |
| 1 | fixture: ...and the whole economy trades, so openness is one | same | (a) | |
| 1 | paying over the world in real terms strengthens the rate | same | (a) | read against the neutral dial's rate after 120 months, not the pull-only rate |
| 1 | ...by exactly -((real gap + the expected reversion) x RATE_PULL) x DRIFT_SPEED, and the parity pull | after 120 months, a dial of 25%: the rate is the trend and the three levels reached | (b) | star 8 "whatever real life does": the real gap is a level (3.3), not RATE_PULL's push (XR1 row) |
| 1 | paying under it weakens the rate | same | (a) | |
| 1 | ...by exactly the mirror | after 120 months, a dial of 10%: the rate is the trend and the three levels reached | (b) | star 8, as above |
| 1 | at the world's real rate the rate moves by the parity pull and the expected reversion alone | at the world's real rate the rate is parity's trend times the cover's premium alone | (b) | 3.2 parity's trend followed one for one; XR1 row "REVERSION's pull" retired |
| 1 | ...so the inflation differential no longer moves the rate directly | ...so the inflation differential reaches the rate only through the parity it raises | (b) | 3.2 "The spiral of 0.7.2" (the drift form returns slowed, through parity's trend); star 2 "Basket, rent capped" is what parity is struck from |
| 2 | fixture: ten months with no push and no deficit | same | (a) | no balance, no cover premium, no deficit |
| 2 | with the real gap at nothing and no outflow, the rate moves by the parity pull and the expected reversion alone | each month the rate is parity's trend, and nothing else moved it | (b) | 3.2; XR1 row "REVERSION's pull" retired |
| 2 | ...where the inflation drift would have carried it a third weaker | ...and after ten months it is above where it began: it followed the inflation | (b) | XR1 row "the anchored drift" retired; 3.2 follows parity one for one |
| 3 | fixture: the trailing deficit pushes the currency weaker | same | (a) | `balanceTarget() > 0` |
| 3 | fixture: the month's own accounts show a US$2M deficit; ...the vault is at six months' cover; ...so its capacity is the most there is; ...the treasury bought nothing this month | same | (a) | the main fixture now settles with the capital leaving every month, so the trailing balance is the month's own and the fall is large: the deficit's cap binds |
| 3 | the central bank sells absorption() x the deficit, to the cent | same | (a) | holds because the cap binds (asserted first: the need exceeds absorption() x the deficit, which the vault holds) |
| 3 | ...and the vault falls by exactly that; ...at the month's rate, which is what it fetches; ...and comes off the treasury's intervention record, as a sale does; the realised absorption is what the sale met of the deficit | same | (a) | |
| 3 | the push that reached the rate is total x (1 - realised) x openness | the sale lifted the D$ by INTERVENTION_IMPACT x what it fetched over a year's GDP: nothing was felt before, so the intervention's move is minus the lift | (b) | star 7 "Interventions move the rate, at the real size", Jerus: "Yes, real size"; 3.6's last point puts the defence's sale through INTERVENTION_IMPACT |
| 3 | ...and cover fell; ...to what is left, at the new rate; the dollars sold since founding count it | same | (a) | |
| 3 | an empty vault sells nothing; ...damps nothing | same | (a) | |
| 3 | ...and the whole push reaches the rate | same | (a) | the whole fall: `ln(rate/before) == getLastWouldBeMove()` |
| 3 | fixture: a month in surplus, pushed stronger | same | (a) | `balanceTarget() < 0` |
| 3 | a surplus month sells nothing; ...and buys nothing: the vault is what it was; ...not by the treasury either; ...and the rise reaches the rate in full; ...which rose | same | (a) | the rise is `getLastWouldBeMove()`, nothing felt from the set-up purchase once forgotten |
| 3 | fixture: the vault's capacity would sell more than it holds | same | (a) | |
| 3 | a US$500k vault against a US$2M deficit sells all of it; ...and is empty; ...and damps 500k / 2M; ...not absorption() | same | (a) | the fixture is given a dial 20 points under the world's, so the need exceeds the vault as well as the cap (asserted first) |
| 3 | ...and the push that reached the rate is the rest of it | ...and it lifted the D$ by what it fetched, over a year of the GDP: minus the lift | (b) | star 7, as above |
| 4 | the sale is the vault's capacity times the month's own deficit, at most the vault | same, asserted as on main | **red, a call** | neither kind: XR1's lean sells only what takes back absorption() of the month's fall (in the real city's first defended month US$921k of a cap of US$2,353k). Kept word for word and red until Jerus answers XR1-C ★1; the lean's own arithmetic is printed beside it. The cap's premise holds word for word in section 3, whose fixture binds it |
| 6 | fixture: past settling, trade in balance, the vault empty, the whole economy trading | same | (a) | |
| 6 | a month's push past the old guard of 100 takes the rate to 150, not 100 | a month's trend past the old guard of 100 takes the rate beyond it, not held at 100: the trend arrived | (b) | 3.2 "A trend, not the month"; XR1 row "RATE_PULL's push" retired |
| 6 | a rate under the old guard crosses it in a month; ...and every month is the push and the pull, never the guard | a rate under the old guard crosses it; ...and every month is the trend and the levels, never the guard | (b) | 3.2, as above |
| 6 | ...and the dollar debt is worth the dollars at it; ...the month's revaluation the whole of the move | same | (a) | |
| 6 | a month's push past the old floor of .01 takes the rate to 1/150, not .01; a rate over the old floor crosses it in a month, by the push and the pull alone; ...to under .01 | a month's trend past the old floor of .01 takes the rate under it, not held at .01: the trend arrived; a rate over the old floor crosses it | (b) | 3.2, as above |
| 6 | ...the month's revaluation the whole of the move, a gain | same | (a) | |
| 6 | three zeros off: the rate a thousandth; ...the guard above it a thousandth of MAX_RATE; ...and the one below a thousandth of MIN_RATE | same | (a) | the rate is read against the twin's, a thousandth |
| 6 | ...and the push moves it as it moves the unreformed one, a thousandth the size | same | (a) | here every month for thirty, within the same tolerance (1e-12); "push" now reads parity's trend and the levels |
| 6 | a city founded in the new unit carries the guards in it | same | (a) | |

#### ForeignCheck

| section | label before (main) | label after | kind | the call / the line |
|---|---|---|---|---|
| 3 | ...and the pressure reading with it | (retired) | (b) | XR1 row "RATE_PULL's push" retired; slot 12 is written as 0 and read by nobody |
| 6 | a sustained deficit weakens the currency; a sustained surplus strengthens it | same | (a) | each city holds a vault past THIN_COVER (the set-up, forgotten), or the empty vault's premium (0.10) outweighs a surplus (-0.05) |
| 6 | ...and the pressure that did it reads as depreciation pressure; ...and reads as appreciation pressure | same | (a) | `getBalanceReached() > 0` / `< 0` |
| 7 | balanced trade brings the rate back toward parity; ...and most of the way home | same | (a) | the displaced city rebuilds a vault first (the set-up, forgotten): its anchor is parity's trend (3.2) and an empty vault keeps the premium |
| 13 | fixture: the deficit city is pushed weaker; fixture: the surplus city is pushed stronger | same | (a) | `balanceTarget()` |
| 13 | fixture: both trade a fraction of their output; fixture: both vaults are deep enough for the most damping | same | (a) | |
| 13 | a push weaker is damped by the vault's cover | a month weaker is leaned against by the vault's cover: it sells absorption() x the deficit | (b) | star 7 "Yes, real size"; 3.6's last point. The fixture's fall is large, so the deficit's cap binds |
| 13 | ...and the absorption it records is what was applied | same | (a) | |
| 13 | (damped push, in the old line) | ...and the sale lifted the D$ by INTERVENTION_IMPACT x what it fetched over a year's GDP; ...so the move that reached the rate is the fall less the lift | (b) | star 7 |
| 13 | the same vault passes a push stronger in full; ...and records that it absorbed nothing; ...though its cover could have absorbed the most there is | same | (a) | the rise reaches the rate in full: `ln(rate/before) == getLastWouldBeMove()` |
| 13 | fixture: the support is half the trade term, unclipped | fixture: the support is a part of the trade term's level, unclipped: a real rate over the world's shortens the fall, which still weakens the D$ | (b) | star 8 "whatever real life does": the support is `gapLevelFor()` reached 60% at once (3.3), not a push of RATE_PULL per point |
| 13 | the rate's support comes off before the vault sees it | same | (a) | the fall the vault sees is shorter than without the support |
| 13 | ...and the vault damps what is left | same | (a) | the sale is `expectedSale(...)` on what is left; the lift takes back absorption() of it |
| 13 | fixture: support larger than the trade term turns the push; support outweighing the deficit reaches the rate in full | same | (a) | |
| 13 | fixture: the page's push is not the one the month recorded; previewing the push leaves the month's pressure as recorded; ...and its absorption; ...and previews exactly what the month will apply; ...which is the month's own call, and records what it applied | (retired; the premise, a screen's read does not rewrite the month, is section 15's "the readers the tab calls change nothing") | (b) | XR1 row "RATE_PULL's push" retired and with it `previewPressure()` and its four neighbours; 3.1 "each can be shown to the player as a line of its own". The gate may want this one looked at: it is an instrument retired, not a starred call |
| 14 | fixture: the city is pushed weaker | same | (a) | |
| 14 | converting for land pushes the rate as buying the dollars for the vault does | buying the dollars for the vault lifts the rate by INTERVENTION_IMPACT x their price over a year's GDP, which the next reprice feels | (b) | star 7 "Interventions move the rate, at the real size", "Yes, real size"; 3.6 "a purchase cheapens it as much". The reserve twin's purchase is left pending |
| 14 | ...which is what buying nothing pushes: a treasury's dollars are the financing item | same | (a) | land's last move equals the control's, exactly |
| 14 | ...and it leaves the vault where it began; ...and nets to nothing in the intervention record; ...and books no reserve purchase for the month; ...while the seller was paid the parcel's dollars | same | (a) | |
| 14 | ...so what the vault would absorb is the control's, not the reserve buyer's | same | (a) | `absorption()` |
| 15 | fixture: the month pushes the rate and the basket pulls it; the push and the pull the tab draws add to the move it draws | fixture: the month moved parity's trend, the real rate, the balance, the interventions and the market; the six moves the tab draws add to the move it draws | (b) | XR1 row "REVERSION's pull ... RATE_PULL's push" retired; 3.1 and board 1 "what moved it this month in ... lines that sum to the month's move" |
| 15 | ...and the month's reprice moves the rate by exactly that | same | (a) | `ln(rate/before) == getLastMove()` |
| 15 | fixture: an anchored drift handed to the currency, so the month drifts it too; the push, the drift and the pull the tab draws add to the move it draws; ...and the month's reprice moves the rate by exactly that, the drift in it | a second month's six moves add to its move, too | (b) | XR1 row "the anchored drift" retired |
| 15 | a pinned rate's previewed move is nothing | same | (a) | `getLastMove()` is the nearest reading now the preview is gone |
| 15 | ...the drift with it | (retired) | (b) | XR1 row "the anchored drift" retired |

#### ExpectationsCheck

| section | label before (main) | label after | kind | the call / the line |
|---|---|---|---|---|
| 1 | ...and its currency is handed no drift | (retired) | (b) | XR1 row "the anchored drift" retired: the currency takes none from the anchor |
| 8 | uipLevel() is e^(-gap / EXPECTED_REVERSION); at that distance from parity the rate's own pressure is nothing; ...nearer parity than that, it strengthens the currency on toward it; ...further from parity, it weakens it back | (retired) | (b) | star 8 "whatever real life does": the gap's effect is a level, `gapLevelFor()` (3.3), not a push toward a UIP level; XR1 row "RATE_PULL's push" retired |
| 8 | a real gap held for good moves the rate to a level and stops, between uipLevel() and parity | a real gap held for good moves the rate to a level and stops | (a) | the clause "between uipLevel() and parity" is retired with the UIP level (star 8); the level is `parity x exp(gate() x gapLevelFor(gap))`, held for 600 months (240 leave a month's move of 2e-11, over the 1e-12 bound) |
| 8 | with no push, the anchored drift moves the rate by its twelfth root, before the parity pull | (retired) | (b) | XR1 row "the anchored drift" retired |
| 8 | ...and a pinned currency not at all | same | (a) | no drift handed; the real gap is set |
| 8 | fixture: the city's basket is based; a city's anchored drift is the credible part of its target against the world's inflation; ...and it is what the month handed its currency | (retired) | (b) | XR1 row "the anchored drift" retired |

#### MonetaryCheck (gapLevelFor lines and the index's save)

| section | label before (main) | label after | kind | the call / the line |
|---|---|---|---|---|
| 4 | fixture: both currencies sit at parity, so nothing is expected back | (retired) | (b) | star 8: the level is a function of the gap alone (3.3); EXPECTED_REVERSION retired with the UIP level |
| 4 | paying under the world in real terms weakens the currency; ...and paying over it supports the currency | same | (a) | `gapLevelFor(gap) > 0` / `< 0` |
| 4 | ...by RATE_PULL for every point of the real gap | ...by exactly -GAP_CAP x tanh(PERSISTENCE x gap / GAP_CAP) for a gap of four points | (b) | star 8 "whatever real life does"; XR1 row "RATE_PULL's push" retired |
| 4 | ...and past any gap a city reaches, only the numerical guard | ...and past any gap a city reaches (a hundred points), held inside GAP_CAP | (b) | star 8: "at most 35% either way" (section 6) |
| 1 | a save from 0.7.43 or 0.7.44, the chain without the levels, still reads as a chain | same | (a) | the truncation is `COMPONENTS + 1` now (the capped basket's link is one more slot) |

MonetaryCheck's red (the delay allowance) is not touched.

#### CapitalFlowCheck, CarryTradeCheck, SaveFileCheck, ReadPathCheck

| harness | label before (main) | label after | kind | note |
|---|---|---|---|---|
| CapitalFlowCheck | a surplus sent abroad pushes the currency nowhere (and "fixture: the surplus alone would strengthen the currency") | same | (a) | `pressure()` is `balanceTarget()`, the GDP the fixture already passes |
| CarryTradeCheck | fixture: the surplus alone would strengthen the currency; money borrowed and taken out offsets a surplus | same | (a) | `pressure()` is `balanceTarget()` |
| SaveFileCheck | (no label changed) | | (a) | `SETTLING_MONTHS` is a local 24 for the months the fixture trades before its test month |
| ReadPathCheck | (calls, no labels) | | (a) | the retired `preview*()`/`getLastPressure()` calls are the new pure readers (`gate()`, `balanceTarget()`, `coverTarget()`, `getFairLevel()`, `deviationFromFair()`, `getParityTrend()`, the six `getMove*()`, `getLastMove()`) |

### New in XR1 (no label before; each says so in a comment beside it)

- CurrencyCheck 1: the market is quiet and the empty vault costs the whole risk premium; parity's trend is the parity it was handed. 2: nothing. 3: the rate has dealt with nothing and the fall is large; the month's move before the defence weakens the D$ and is what the fixture's figures make it; the sale is what the lean sells from the constants and the getters; the lift is less than absorption() of the fall; the small-fall case (the need binds): the fixture, the sale to the cent, under absorption() x the deficit, the lift takes back absorption() of the fall; the shallow fixture's need exceeds the vault. 6: the market is quiet.
- ForeignCheck 3: the float's trend, gap, market, move and draws survive the reload. 13: the fall is large; the month's fall weakens / strengthens the D$; the same sale from the constants; the surplus sold nothing and felt nothing; nothing is sold or absorbed when the support turns the month. 14: only the reserve twin has dealt since its last reprice; its intervention line is nothing for the land twin. 15: the treasury has dealt since the last reprice and the next reprice felt it; the six moves; the rate sits off its fair level by the market and the interventions alone; the readers change nothing; a pinned rate draws nothing.
- ExpectationsCheck 8: a quiet market, trade in balance, the vault at THIN_COVER; the level is the formula's and dearer than parity for a gap over the world's.
- MonetaryCheck: the capped level reloads; a save from before the capped basket's link still reads as a chain; the level is symmetric; the cap holds the other way.
