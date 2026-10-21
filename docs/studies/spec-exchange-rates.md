# Exchange rates that behave like currencies: a design study

*2026-10-21. A read-only design study: no game code was changed. Jerus, through the nations study's session: "start another study that expands WN, that is actual currency exchange rate behaviour, cause the current one is wierd". It expands the nations study's WN4 (each nation's money and rate, the vault in eight, D29's two US dollars) and meets the central bank study's CB1-CB3, where policy rates and exchange rates meet.*

*What was read:*
- *The code at e6c8e74 (main, 0.8.15), read and not changed: `ForeignAccounts` (`repriceCurrency()`, `pressure()`, `ratePressure()`, `uipLevel()`, `effectivePressure()` and the defence, `setParity()`, `buyReserves()`, `sellReserves()`, `absorption()`, the openness), `Game`'s month lines that hand it parity, the real rate differential and the anchored drift (`realRateDifferential()`, `anchoredDrift()`), `MoneyAudit`'s RESERVE scope, `ui/TradeScreen`'s The currency page, `HistorySave`'s fxRate and fxParity, CurrencyCheck's and CarryTradeCheck's sections.*
- *The studies: `spec-nations.md` (§5 the moneys, §9 WN0-WN7, D29), `spec-central-bank.md` (CB1-CB3), `spec-city-debt.md` (CD1's curve), `docs/queue/FX4-notes.md` (the runaway of 2026-10-19), EN1-C's S1 in `QUEUE.md`.*

*What was measured:*
- *Jerus's saves, read from copies and never written: slot 1 (3,001 months), slot 3, his new city (476 months, the one he plays now), the autosave.*
- *The playtest's currency trace (`LongPlaytest -Dplaytest.fx=true`, 4,000 months, at 9ff6489).*
- *A probe of the treasury's purchases (`FxInterventionProbe`, three twins of CurrencyCheck's city), run from this worktree.*
- *The prototype `exchange-rates/RatesModel.java` (Java 21, `java RatesModel.java slot3-series.csv`), its output in `exchange-rates/rates-model-output.txt`: today's rule taken apart on slot 3, the lag, the proposal replayed on slot 3, the world's moneys over 1,000 seeded paths, pegs, and the month's cost.*

*Figures marked "measured" are from these; (est.) are estimates; [R#] are the sources at the end. The boards are `exchange-rates/1-` to `3-*.html`.*

---

## 0. In one page

**What is weird, in a sentence a player would recognise.** The D$ glides: it moves about a tenth as much as a real currency, the same way for years on end, and it sat 20-34% dearer than its parity for twenty years, because "parity" is mostly the city's rent.

**Measured in his new city (slot 3, 476 months):**
- **Too smooth.** The rate's monthly moves come to **1.2% a year**; a floating currency moves about 8-12%, and a currency board under 1% [R6]. One run went the same way for **78 months**; in slot 1, **242**.
- **A lag that never closes.** Each month the rate closes 0.4% of its gap to a parity that keeps moving. A pull of k toward a target growing g a month settles at rate/parity = k / (k + g). From month 264 parity grew 2.6% a year, so the rule settles at **0.65 of parity**; measured, 0.76. The playtest's D$ sat at **0.70-0.80 of parity for 333 years** (1.00 to 1,209 D$ a US$, parity 1,649).
- **Parity is rent.** Rent is **41% to 80%** of the basket parity is struck from. From month 192 to 240, parity rose 50%: rent rose 75%, groceries 14%. On groceries alone, the traded part, parity ends at 1.315 and the rate at 1.303.
- **Buying dollars makes the D$ dearer.** The treasury's purchases are booked as reserve transactions, outside the balance the rate reads, and a fuller vault leans harder against falls. Measured on twin cities: buying US$205M of dollars left the D$ **7.7% dearer** two years on. In the real world, buying dollars cheapens your money; that is how Switzerland and China held theirs down.

**The proposal in one line:**

    rate = parity's trend  x  the fundamentals' level  x  the market

- **Parity's trend**, on the traded part of the basket, is followed **one for one**: the lag is gone (measured: 1.000 of parity at any steady inflation gap, against 0.34-0.83 today).
- **The fundamentals' level** is where the real rate gap, the overall balance and the vault's cover put the money. A rate rise appreciates it at once and it gives the gain back as the gap is expected to close: Dornbusch's overshooting [R14].
- **The market** is the money's own swing: seeded, about 2.2% a month, fading by half in about 3.5 years, the half-life of real currencies' gaps from parity [R1].

**Measured on his city's own parity, real rate and vault:** the D$ moves **9.5% a year** instead of 1.2%, never more than about **14 months** one way, and its fair level is within 25% of parity on traded goods in all but 17 months (today's rule, against the same parity: 78).

**The city's choice (Policy › The currency):** **Float**; **Lean against falls**, today's behaviour and the default; a **Band** of its own centre, slope and width, as Singapore runs its money [R36]; or a **Peg** to the US$, and to the euro or the eight once the nations' moneys exist. A peg is held by the vault, which pays the month's whole balance at the peg and the money that leaves on doubt. It breaks when the vault falls under a month of imports, or sooner by chance as the gap grows. Interventions now move the rate, both ways.

**The world's moneys** follow their prices against the US's, the world's dollar cycle, a risk-off about once in eight years (havens up, high yielders down), their terms of trade from the January solve, and their own swing. Pegs are held, and broken by the same rule. Historical replays the real monthly rates of 2000-2025. **Measured:** majors 8-12% a year, an emerging float 17%, as 1999-2025's were; half-lives measured as an economist would, 3.5-4.9 years; the dollar's widest swing in 26 years 38% (real: 35-50% over 6-11 years); a month of all 196 moneys and the D$ costs about **10 microseconds**.

**D29's real US$** reads as D$ per 2015 US$, beside a real effective rate (the D$ against its partners in real terms, 100 at founding), for comparison only.

**Batches (§5):** **XR1** the D$ as a float that follows its parity (every trace moves); **XR2** the regimes; **XR3** the world's moneys (with WN4); **XR4** interest abroad, the vault's income and the carry. None is queued: the ★ calls in §6 wait for Jerus.

---

## 1. What the game does today

### 1.1 The rule

`ForeignAccounts.repriceCurrency()`, once a month after the month is taken (Game.java, the lines after `setParity()`):

    push   = [ the balance  +  the real rate ]  x (1 - the vault's share) x openness
    the balance  = -(current + financial account, 12-month means) / what crossed, less a 5% dead band; zero for 24 months
    the real rate = -4 x (real gap + 0.15 x ln(rate / parity))          (the UIP level: rate/parity = e^(-gap/0.15))
    rate  *= 1 + 0.02 x push
    rate  *= (1 + credibility x target) / (1 + world inflation), a twelfth of it a month   (the anchored drift)
    rate  += (parity - rate) x 0.004                                     (the pull: a half-life of 14 years)
    parity = parityBase x the city's price index / the world's price level

Nothing else moves it. There is no noise, nothing from the world but its price level, and the vault leans only against falls ("a reserve defends a currency; it does not hold one down", Jerus 2026-09-21).

### 1.2 What it did in his new city (slot 3), measured

| month | rate | parity (basket) | rate/parity | parity on groceries | dial | real gap | vault (reservesUsd, as saved) |
|---|---|---|---|---|---|---|---|
| 0 | 1.000 | 0.999 | 1.00 | 0.999 | 3.0% | +1.0 | 10,000 |
| 48 | 0.989 | 0.961 | 1.03 | 0.961 | 3.0% | -0.8 | 1,647 |
| 96 | 0.929 | 1.076 | 0.86 | 0.748 | 11.4% | +6.7 | 1,647 |
| 144 | 0.999 | 0.843 | 1.18 | 0.773 | 0% | -1.9 | 12.7 |
| 192 | 0.862 | 0.742 | 1.16 | 0.779 | 0% | -1.4 | 12.7 |
| 240 | 0.753 | 1.114 | 0.68 | 0.907 | 10.3% | +2.0 | 12.7 |
| 288 | 0.782 | 1.185 | 0.66 | 0.960 | 6.0% | +1.4 | 0 |
| 336 | 0.952 | 1.205 | 0.79 | 1.040 | 3.5% | -0.6 | 0 |
| 384 | 1.055 | 1.402 | 0.75 | 1.145 | 7.2% | +1.9 | 0 |
| 432 | 1.205 | 1.500 | 0.80 | 1.216 | 5.6% | +1.8 | 0 |
| 475 | 1.303 | 1.691 | 0.77 | 1.315 | | | 0 |

*(The real gap is the dial less expected inflation, less the world's base rate less its realised inflation, as `realRateDifferential()` strikes it, in points. Parity on groceries is chained from month 58, when the basket is first weighted.)*

- **The moves:** 1.2% a year; runs of up to 78 months one way, a median of 13 (measured).
- **The gauge:** the Trade tab's parity gauge read amber, more than 25% from parity, for 74 of 476 months, all on the dear side; never red.
- **What made the move** from 1.000 to 1.303 (+0.265 in logs), month by month: the anchored drift **+0.435**, the pull toward parity **+0.305**, the push **-0.475** (measured). Three large forces, none of them the city's own inflation, cancel to a small one.
- **The drift was the world's.** His world's price level rose about 0.4% a year (the city's index over its parity), while the drift assumed a credible 1.5-1.6% at home. So the D$ fell about 1.1% a year by rule, whatever the city's prices did.
- **The dial's swings came through whole.** At month 221 the dial stood at 22.7% with 4.5% expected: a real gap of 16.9 points. The real rate's term held the D$ up through a 47% rise in its basket (months 192-240).

### 1.3 Over the long run: slot 1 and the playtest

- **Slot 1** (3,001 months): 1.00 to **2,749 D$ a US$**, parity 3,496; 1.1% a year; **242 months** in a row one way (measured).
- **The playtest** (4,000 months): 1.00 to 1,209 D$ a US$, parity 1,649. The rate sat at **0.70-0.80 of parity** from about year 15 to the end, while the current account ran a surplus (measured, `playtest.fx`).
- **The lag is arithmetic, not a balance.** A pull of k = 0.004 a month toward a parity growing g a month settles at k / (k + g):

| parity's growth, a year | today's rule settles at rate/parity | the proposal |
|---|---|---|
| 1% | 0.83 | 1.000 |
| 2.1% (the playtest) | 0.70 | 1.000 |
| 4% | 0.55 | 1.000 |
| 10% | 0.34 | 1.000 |

*(measured, RatesModel §2, after 10, 50, 100 and 333 years: the figures are the same from year 50.)*

### 1.4 The treasury's purchases move the rate the wrong way

- `buyReserves()` and `sellReserves()` are booked in `MoneyAudit`'s RESERVE scope, the financing item, outside the current and financial accounts that `pressure()` reads. That was right for the books (a purchase must not finance itself), but it means the market never sees the treasury's dollars.
- The vault's only effect on the rate is the defence: it sells a share of the month's deficit, the share rising with its cover, and damps a falling push by that share.
- **Measured** (`FxInterventionProbe`, three twins of CurrencyCheck's city at month 37): one twin bought US$205M of dollars (57 months of imports), one sold its last US$158, one did nothing.

| months on | control | bought dollars | sold dollars |
|---|---|---|---|
| 1 | 1.0259 | 1.0235 (-0.2%) | +0.002% |
| 6 | 1.0472 | 1.0282 (-1.8%) | +0.005% |
| 12 | 1.0806 | 1.0388 (-3.9%) | +0.006% |
| 24 | 1.1713 | 1.0816 (**-7.7%**) | +0.006% |

  Buying dollars made the D$ **dearer**: the bigger vault took more of every month's fall. In the real world a central bank that buys dollars sells its own money and cheapens it: by 1.7-2.0% for a purchase of 1% of GDP [R33].

### 1.5 What the player sees

- **Trade › The currency:** the rate at 28 px, a gauge against parity (amber past 25%, red past 50%), the rate's chart with parity, and the forces on next month as diverging bars: the trade term, the real rate, the vault's defence, the push and the pull.
- **Policy:** the dial. **Trade › The reserves:** buy or sell dollars.
- What the player cannot see is a reason the rate moved this month that they could recognise. A month's move is a fraction of a percent, and its cause is three large forces netting to a small one.

---

## 2. What real currencies do

*Each point with its figure and its source. The research notes beside the study (`exchange-rates/research-notes.md`) have the longer form, the own calculations from the ECB's, the Fed's and the BIS's data, and what could not be verified.*

### 2.1 The long run: parity, and how slowly

- **Relative purchasing-power parity holds in the long run and hardly at all in a year.** Rogoff's survey put the consensus half-life of real rates' gaps from parity at **3 to 5 years**, about 15% of the gap closing a year [R1].
- **Shorter when goods are not lumped together**, 11 months to 2 years [R2]; **and large gaps close faster than small ones** [R3]. In the Big Mac data, gaps over 40% close about two thirds in a year, gaps under 30% about a quarter [R4].
- **The gaps are large.** The BIS's real effective rates for 64 economies stray about **10%** either side of their own mean at the median (one standard deviation), 23% at the 90th percentile [R5]; Big Mac gaps run from -60% to +45% [R4].
- **What holds best is traded goods.** Haircuts and rents are dearer in richer places, so a rich place's prices are high without its money being dear: the Balassa-Samuelson effect [R37].

### 2.2 The month: noise

- **Floating rates move 8-12% a year** among the big moneys: the euro 9.3%, the yen 9.4%, the pound 8.5%, the Canadian dollar 8.4%, the franc 9.6%, the Australian dollar 11.8% (1999-2025); emerging floats 11-18%; Singapore's managed dollar 5.4%; Hong Kong's board 0.6% [R6]. **Today's D$, at 1.2%, moves like a currency board.**
- **Nominal and real rates move together** under floating, because prices are sticky and the rate is not [R8]: across the BIS's 64 economies the monthly correlation is 0.90, the slope 0.97. The nominal rate carries the inflation gap over the years: Turkey's nominal effective rate fell 24% a year while its real one fell under 1% [R5].
- **At a month's horizon nothing beats a coin.** A random walk forecasts rates better than the models, out of sample, from a month to a year [R9]. Monthly moves are uncorrelated (first autocorrelation -0.06 to +0.13), their tails fat [R6].

### 2.3 Interest rates: parity that fails, the carry and its crashes

- **Uncovered interest parity** says a money paying more should be expected to fall by the difference. Over months it fails the wrong way, the forward premium puzzle [R10]: the slope is about -0.8 at short horizons. Over five to ten years it holds roughly, 0.4-0.9 [R11].
- **So the carry trade pays** on average: a basket of carry trades earned **3.2% a year** with a Sharpe ratio of 0.75 in 1976-2009 [R12]; its worst months lose 5-9%.
- **And then it crashes**, "up the stairs, down the elevator" [R13]. The yen rose **15% in four trading days** in October 1998 [R7]; the Australian dollar fell **35% against the yen in three months** of 2008 [R6]; it fell 16% against the yen from 11 July to 5 August 2024, most of that reversed within five months [R6].

### 2.4 Overshooting

- **Dornbusch:** with sticky prices, a money tightening jumps up at once, further than it will end, and then falls back as the rate gap is expected to close [R14].
- **In the data the peak comes later**, one to three years after the shock: "delayed overshooting" [R15]. On the day, a surprise rise of a point lifts a currency about **1.5%** [R16].

### 2.5 Trade, capital and the terms of trade

- **Commodity currencies** move with their export prices [R17]. From June 2014 to January 2016 Brent fell 72.5% [R18]; the krone fell 29%, the Canadian dollar 24%, the ruble 55% [R6]. Measured on the BIS's real effective rates, a doubling of oil lifts the Canadian dollar's about 9% and the krone's about 8% over a year [R5][R18].
- **Deficits can run for decades** while capital flows in: the US ran one in 48 of the 56 years to 2025 [R35]. A currency falls hard when the financing stops, a "sudden stop" [R19]: broken emerging-market pegs and baskets lost **40-55% in two to six months**, and output fell 5-13% the next year [R7][R35].

### 2.6 Risk and safe havens; the dollar cycle

- **In a panic money runs to the Swiss franc, the yen and the US dollar** [R20]: the broad dollar rose **18%** in late 2008 and **6%** in March 2020 [R5].
- **The dollar moves in long swings** of **35-50% over 6-11 years**: up 52% from 1978 to 1985, then down; down from 2002 to 2011; up from 2011 to 2022 [R5].

### 2.7 Regimes, and how pegs break

- **Most of the world does not float freely.** The IMF's 2021 classification: hard pegs **13%** of members, soft pegs **48%**, floating **33%**, other 6% [R21]. Declared floaters lean on their rate too: "fear of floating" [R22].
- **A currency board** backs its whole money with reserves and lends to nobody: Hong Kong's, its dollar held within **7.75-7.85** a US$ since 2005 [R23]. Argentina's board (one peso a dollar from 1991) ended in January 2002, and the peso lost about two thirds of its value that year as output fell 10.9% [R24][R35].
- **Pegs end in two ways.** The vault runs down until speculators finish it, Krugman's first-generation crisis; or doubt itself makes defending it too costly, Obstfeld's second generation [R25]. Sterling fell **10%** against the mark in the two weeks after it left the ERM in September 1992, and 24% against the dollar in four months; the baht lost **55%** in six and a half months of 1997; the won 43% in seven weeks; the real 42% in two and a half weeks of 1999 [R7]. A crash, by Frankel and Rose's rule, is a fall of at least 25% that is also 10 points more than the year before [R26].
- **The Swiss National Bank** held a floor of 1.20 francs a euro from 6 September 2011 and dropped it on 15 January 2015: the franc rose **16.8%** against the euro in a day [R27].
- **Pegs rarely last.** By 1995 almost no sizeable economy had held one five years [R28]. Among 125 countries from 1973 to 2004, the median peg lasted **2 years** and the mean 4.7; 30% survived five years; and many broken pegs re-peg [R29].
- **The impossible trinity:** a fixed rate, an open border for capital and a monetary policy of one's own cannot all be had at once. Pegs with open capital accounts take most of the anchor's interest rates [R30].

### 2.8 Reserves and intervention

- **How much is enough:** three months of imports is the old rule; reserves covering short-term debt abroad, Greenspan and Guidotti's; the IMF's own metric weighs both and more, with 100-150% of it adequate [R31].
- **Intervention works, a little and for a while.** In 33 countries it smoothed the rate in about 80% of episodes and moved its level in about 60% [R32]. A purchase of foreign money worth **1% of GDP** cheapens the home money by **1.7-2.0%** nominal and 1.4-1.7% real [R33].
- **The big ones:** Japan sold about ¥9.2 trillion of dollars in 2022 and about ¥15.3 trillion in 2024 to hold the yen up; each round of ¥3-10 trillion bought a move of 2-2.5% for days to weeks [R34]. The SNB's balance sheet went from CHF 250 billion to 640 billion in 4.5 years while it held the franc down [R27]. China's reserves fell about a fifth in two years, 2014-16 [R35].
- **Singapore runs its money by its rate:** the Monetary Authority keeps the trade-weighted Singapore dollar inside a band whose slope, width and centre it sets at each statement, and intervenes at its edges [R36].

### 2.9 What a city would feel

- **The small manage or borrow a money; the big float.** The members with no money of their own and the currency boards are almost all small; the free floaters are the big and rich [R21]. The city-states show it: Singapore's band, Hong Kong's board, Macau's pataca on the Hong Kong dollar, Brunei's board, and Monaco, Luxembourg, Liechtenstein and Panama on others' moneys [R21][R36].
- So a city-sized money would feel **the world's swings far more than its own policy**: the dollar cycle, risk-offs, the price of what it sells. Its own levers are the real rate, its vault and its regime, which is what the design gives the player.

### 2.10 The measures

- **The real effective exchange rate** is a trade-weighted geometric average of the bilateral rates, each deflated by the two price levels; the BIS publishes it for 64 economies, weighted by trade [R5].
- **Constant 2015 US$:** the World Bank values each economy's GDP at its 2015 prices and converts it at 2015's exchange rate [R35].

---

## 3. The design

### 3.1 The rule

    ln rate = ln parity's trend  +  the fundamentals' level reached  +  the market  +  interventions still felt

Each piece is a level, not a speed, so nothing lags a moving target, and each can be shown to the player as a line of its own.

### 3.2 Parity's trend, on traded goods, with no lag

- **On traded goods.** Parity is struck from the part of the basket that crosses borders: groceries and the luxury goods, against the US's goods prices (★2). Rent, services and meals stay out: nobody arbitrages a flat across a border. Real currencies' long-run anchor is closest for traded goods, and the gap between traded and non-traded prices is the Balassa-Samuelson effect, a reason a rich city's prices are high without its money being dear [R37].
- **Against the US's prices**, the in-game US$'s path (D29), once WN1 gives the US a path; until then the world's level, as today.
- **A trend, not the month.** The rate follows parity's level and slope (Holt's filter: a sixth of a month's jump at once, the slope over two years). A steady inflation gap passes one for one, so nothing lags; a single month's price spike moves the rate a sixth of it. Measured: at 1%, 2.1%, 4% and 10% a year of parity growth, rate/parity is 1.000 from year 10 on; through a 50% burst in four years, 0.97-1.02.
- **The spiral of 0.7.2.** This is the drift form that 0.7.2 deleted, slowed: the inflation gap passes into the rate in the long run. 0.7.2 deleted it because, with imports repricing one for one, wages chasing the index and the shelf cost-plus, a gain of one let inflation feed itself through the currency. Since then inflation answers to money (P2, P3) and the real rate holds the currency. XR1 must measure it on the ensemble's held-0% and held-15% runs (S1, R3) and count any runaway; the trend's smoothing is the dial if it bites.

### 3.3 The fundamentals' level

Where the city's own figures put the money, as a log gap from parity's trend, reached half in a month (MARKET_SPEED):

- **The real rate** (★8). -GAP_CAP x tanh(PERSISTENCE x gap / GAP_CAP). The gap is today's: the dial less expected inflation, against the world's real rate (XR4: the US's). PERSISTENCE is how many years the market expects the gap to last: 2.5, so a sustained point holds the D$ about 2.5% dearer, half of it in its first month: on the day, a surprise point lifts a real currency about 1.5% [R16]. GAP_CAP, 35%, keeps a 17-point gap like slot 3's month 221 to 25% instead of 68%. **A rise appreciates the money at once, then it depreciates as the gap is expected to close**: Dornbusch's overshooting [R14], the textbook answer to "why did my currency jump when I raised rates".
- **The balance.** The overall balance, current plus financial account over twelve months, against a year's GDP, times BALANCE_WEIGHT (0.5, capped at 20%): a city that runs a deficit year after year owes more abroad and its money cheapens. Today's trade term made into a level.
- **The vault's cover.** RISK_PREMIUM (10%) times how far the cover falls short of SAFE_COVER (3 months of imports, the oldest of the rules [R31]): an empty vault costs the money a tenth; a vault of three months or more, nothing.
- **Debt abroad and hot money** (XR3): add to the risk-off's bite, not to the level (§3.5).

### 3.4 The market

- The money's own swing (★1): AR(1), MARKET_SIGMA = 2.2% a month, MARKET_HALF_LIFE = 42 months. Its spread around the fair level is about 13% either way (one standard deviation). Most of a real float's month is this: no model beats a random walk at a month's horizon [R9].
- **Seeded** from the city's seed (WN1's, once it exists; until then a seed drawn at founding and saved), so one seed is one path, as the nations study's D9 asks.
- **Gated by trade.** Below MIN_TRADE a month (today's floor) the rate is not a real price and holds; the market's swing is scaled by the city's openness until it trades a tenth of its GDP.
- **What it does to prices.** Imports reprice through the rate one for one in the game, so a city whose basket is a third imported takes about a third of the swing into its prices: about 3% a year of noise in its inflation at ★1's (a), half at (b). Real economies pass far less of a move into prices in the short run (est.). XR1 measures the ensemble's inflation spread before and after; if it bites, a pass-through dial on import prices is the fix, not a calmer currency.

### 3.5 The world reaches the D$ (XR3)

Once the nations' moneys move (§3.7), the D$ against the US$ also carries:
- **The dollar cycle**, with a loading of 1: when the dollar rises against everything, it rises against the D$.
- **A risk-off.** About once in eight years, money runs to safety for a few months. The D$'s move in one is its traits': +10% weaker for a small open money, less with a deep vault and a surplus, more with hot money and debt abroad. A city with six months of cover and a surplus is a haven and rises, as the Swiss franc does [R20].
- **Its terms of trade**, from its own trade by good (WN3): a city that sells lumber cheapens when lumber does, by half its share of lumber in its exports, as commodity currencies do [R17]; measured on the BIS's data, Canada's real rate moves about 0.13 with the log of oil's price [R5][R18].

### 3.6 The city's regimes, and what the vault and the central bank can do (XR2)

**Policy › The currency** offers four (★3):

| regime | the rate | the vault | what it costs |
|---|---|---|---|
| **Float** | the rule of §3.1 | does nothing | nothing; the D$ swings |
| **Lean against falls** (default) | the rule, a fall damped | sells dollars against a fall, as today | dollars, in the bad months |
| **Band** | free inside centre ± width, the centre crawling at the slope | holds the edges, buying at the strong one and selling at the weak | dollars at the weak edge; new D$ at the strong edge |
| **Peg** | held at the peg | meets the month's whole balance at the peg, plus the money that leaves on doubt | the vault, every month the shadow rate is weaker |

- **The shadow rate** is the float's rule run beside the peg every month: where the D$ would be. Its gap from the peg, x, is the peg's pressure, shown.
- **What a peg costs a month:** the current and financial accounts at the peg (the vault settles the balance of payments, as every fixed-rate central bank does), plus the money that leaves on doubt: the hot money and the bank's foreign book times the month's break chance.
- **How a peg breaks** (★4): when the vault falls under VAULT_FLOOR (one month of imports), or by chance, at hazard(x) = 0.1% + 3% x (x - 10%) / 10% a month past a 10% gap. That is a first-generation crisis (Krugman 1979: the vault runs down until speculators finish it [R25]) with a second-generation chance (Obstfeld: doubt itself can break a peg [R25]). The rate jumps to the shadow plus OVERSHOOT (a quarter) of the gap again, the regime becomes Float, and Expectations' credibility takes a dent. The player can also devalue (re-peg lower) as a decision.
- **Measured** (RatesModel §5, a peg that does not discipline its own inflation, a vault of six months): 2 points of inflation over the US hold for a median **7.4 years** and break by **31%**; 5 points, **4.5 years** and **37%**; 10 points, **2.9 years** and **47%**. History: sterling fell 10% against the mark in two weeks and 24% against the dollar in four months after it left the ERM in 1992; the baht lost 55% in six and a half months of 1997; Argentina's peso about two thirds in 2002 [R7][R24]. Among 125 countries the median peg lasted 2 years, the mean 4.7 [R29]: the prototype's pegs last longer because their vault is deep and their inflation steady.
- **Under a peg the dial stays the player's** (★5). Hold it under the US's and money leaves for the US; the vault pays. The impossible trinity [R30] is felt, not imposed: a free dial, an open border and a fixed rate cannot all hold at once.
- **Buying at the strong edge** (a band's or a peg's) makes D$: the central bank buys the dollars with new money (M0 rises), as the SNB did from 2011 to 2015, its balance sheet going from CHF 250 billion to 640 billion [R27]. The treasury's own purchases stay the treasury's (★6).
- **Interventions move the rate.** A sale of dollars, the treasury's, the defence's or the band's, makes the D$ dearer by INTERVENTION_IMPACT (2%) per 1% of a year's GDP, fading by half in INTERVENTION_HALF_LIFE (6 months); a purchase cheapens it as much (★7). The size is the IMF's panel estimate, 1.7-2.0% nominal per 1% of GDP [R33]; Japan's rounds of ¥3-10 trillion bought 2-2.5% for days to weeks [R34]. This fixes §1.4.
- **A currency board and dollarisation** are not offered: a board forbids the central bank to lend to the treasury, which the CB study's advances need, and dollarisation removes the D$ every screen is in.

### 3.7 The world's moneys (XR3, on WN1 and WN4)

Each of the ~145 moneys, against the US$:

    ln rate_i = its PPP path  +  b_i x the dollar cycle  +  r_i x the risk-off  +  t_i x its terms of trade  +  its own swing

- **Its PPP path:** its inflation against the US's on its own path (WN1).
- **The dollar cycle:** one AR(1) for the world, half-life five years, 1.6% a month; b_i about 1, less for a money tied to the US (Canada's 0.6). The broad dollar's swings were 35-50% over 6-11 years [R5]; measured in the prototype, the widest swing in 26 years is 38% at the median seed, 53% at the 90th.
- **The risk-off:** about once in eight years, fading with a three-month half-life. r_i: the yen -8%, the franc -5% (they rise), the euro +3%, the pound +6%, the Canadian dollar +7%, the Australian dollar +14%, an emerging float +20%. At one, the A$ falls about 20% against the yen in its month; 2008's fall was larger and slower (35% in three months), 2024's smaller (16% in four weeks) [R6].
- **Its terms of trade:** from each January's solve (WN3), its export prices over its import prices, passed at t_i = half its commodity share of exports, spread over the next three months. Chen and Rogoff's commodity currencies move with their export prices [R17]; Canada's measured 0.13 to the log of oil's price fits a commodity share of exports of about a quarter [R5][R18].
- **Its own swing:** AR(1), half-life 3.5 years, 1.8-4.0% a month by kind (the euro 2.0%, the Australian dollar 2.8%, an emerging float 4.0%).
- **Pegs:** held where the IMF classes the money as a peg (the euro's users are the euro; the CFA francs on the euro; the Gulf and Hong Kong on the US$) [R21]. A soft peg breaks by §3.6's rule on its own shadow rate (Random), or on its real date (Historical). A board (Hong Kong) breaks only if its vault falls under its money.
- **Historical** replays the real monthly rates of 2000-2025 (★11) and starts the swings from each money's real gap from PPP in December 2025. **Random** draws all of it from the seed.
- **In the yearly solve and between:** the January solve prices in US$ (the nations study's unit of account); it hands each nation its terms of trade, which reach the rate over the next three months. Between solves, each month steps the cycle, the risk-off and the swings. Cross rates go through the dollar. The rates move no nation's quantities (D8: the world answers the city in prices only).
- **Measured** over 1,000 seeded 26-year paths, against 1999-2025's real ones [R6]:

| money | volatility a year, model | real | half-life an economist would measure (the model's own swing: 3.5 years) | worst month, median path |
|---|---|---|---|---|
| euro | 8.9% | 9.3% | 4.7 years | 8.3% |
| yen | 9.9% | 9.4% | 4.4 | 10.9% |
| pound | 8.6% | 8.5% | 4.5 | 8.8% |
| Swiss franc | 9.1% | 9.6% | 4.9 | 8.6% |
| Canadian dollar | 8.1% | 8.4% | 3.8 | 9.2% |
| Australian dollar | 12.3% | 11.8% | 3.5 | 18.1% |
| an emerging float | 16.6% | 11-18% | 3.5 | 26.5% |

  The half-lives an economist would measure come out at Rogoff's 3-5 years [R1], longer than the model's own swing because the dollar cycle's is five.

### 3.8 Interest abroad, the vault's income and the carry (XR4)

- **The eight central banks' rates:** Historical from their real policy rates 2000-2025 (the BIS's series of central bank policy rates [R38]); Random and after 2025, a rule on each one's inflation path, in quarter-point steps (CB1's step).
- **The city's real gap** is read against the US's real rate (★9), not the constant WORLD_BASE_RATE of 2% against a world inflation that moves (in slot 3's world, 0.4% a year, which made the world's real rate 1.6%).
- **The vault earns** each money's rate, as Canada's Exchange Fund Account earns for its government (spec-central-bank.md, its R9) (the CB study's "the vault earns nothing").
- **The carry:** the bank's foreign book (CarryTrade) borrows in the cheapest of the eight, and the hot money (CapitalFlows) compares the dial with the US's. Both unwind in a risk-off, by their stock: a city that drew hot money with a high dial falls harder when money runs home. Measured on the prototype's A$ against the yen: the carry earns 4.0% a year with a Sharpe ratio of 0.27, a monthly skew of -1.34 and a worst month of -34% in 26,000 years of paths. One pair is riskier than the real basket's 3.2% a year at a Sharpe ratio of 0.75 [R12]; both go "up the stairs, down the elevator" [R13].

### 3.9 D29's real US$ and the real effective rate (WN6, ★12)

- **The comparison rate:** D$ per 2015 US$ = D$ per US$ x (the US's price level now / its level in 2015). A real 2015 US$ buys what a dollar bought in 2015.
- **Real GDP in 2015 US$**, the World Bank's way [R35]: the city's GDP at founding's prices, divided by founding's rate, carried back to 2015 dollars by the US's price path: real GDP_D$ / rate_founding x (P_US,2015 / P_US,founding).
- **The real effective rate:** 100 x exp(sum of w_j x ln(the D$'s price in money j x P_city / P_j)), with w_j the city's trade by partner (WN3), 100 at founding, the BIS's way [R5]. Above 100, the D$ is dearer in real terms than it was. Before WN3 it is the D$ against the US$ alone.
- Nothing in the model reads any of the three. They answer "is my money dear?", which today's gauge answers against a parity made of rent.

### 3.10 What it costs, measured

- **A month** of all 196 moneys and the D$: about **10 microseconds** (9.0-13.6 across runs), against the nations study's 50 ms January solve.
- **The save** gains, for the D$: the trend's level and slope, the level reached, the market's swing, the interventions still felt, the regime and its settings, the seed. For the world: the cycle, the risk-off, each money's swing and peg state (about 3 KB). History gains fxFair and fxMarket for the chart.

### 3.11 What does not change

- The rate is still D$ per US$; parity's anchor (`parityBase`) and the currency reform (`Denomination`) are untouched, so FX4's fix stands.
- MoneyAudit's scopes: the treasury's purchases stay RESERVE in the books; only the rate now reads them.
- The defence's dollars come off the central bank's equity as today (CentralBank, THE DEFENCE).
- Dollar bonds stay dollar bonds (CD1-CD6); debt in other moneys stays later (spec-nations §11).

---

## 4. The screens

- **Board 1, Trade › The currency** (XR1): the rate with its month and year; against parity on traded goods and against the fair level; D$ per 2015 US$ and the real effective rate (WN6); the chart with parity's trend, the fair level, the rate, and today's rule's path faintly for comparison; **what moved it this month** in five lines that sum to the month's move: parity's trend, the real rate, the balance, the vault's cover, the market; **where the fundamentals hold it**: the fair level, and how fast the market's swing fades.
- **Board 2, Policy › The currency** (XR2): the four regimes with a line each; the peg's anchor and rate; the shadow rate, this month's cost, the vault in months, "the floor in 8 months at this pace"; the band's chart; buy or sell the D$ with the expected effect; the dial under a peg.
- **Board 3, Trade › Partners and a nation's card** (XR3, XR4): the world's moneys this month (rate, month, year, against parity, how each is set, its policy rate); the dollar's chart 2000-2040 with a risk-off marked; the risk-off notice ("the D$ fell 4.3%: the vault covers 2.1 months and D$310M of hot money was here"); Canada's card with its money's line.
- **Alerts:** a move past 5% in a month, a risk-off abroad, a peg's vault under three months, a break.
- **The forces' words** stay in the money blues, never a verdict's colours (TradeScreen's B19).

---

## 5. Batches

Fresh ids: XR appears nowhere in `QUEUE.md`, `docs/` or the git log (only inside images' base64).

| Batch | What | Harness | Traces |
|---|---|---|---|
| **XR1 ★** | **The D$ as a float that follows its parity** (§3.1-3.4): parity on traded goods (★2), its trend (level and slope) followed one for one; the fundamentals' level (the real gap's capped level, the overall balance, the vault's cover) reached at MARKET_SPEED; the market's seeded swing, gated by trade; interventions move the rate, the defence through the same channel (§3.6's last point); REVERSION's pull, the anchored drift, RATE_PULL's push and SETTLING_MONTHS retired; saved (`DataSave`, the load path; `HistorySave` fxFair, fxMarket). **Its screen:** The currency's five forces and the chart (board 1). | `ExchangeRateCheck`: a parity rising 4% a year for 50 years is followed within 2% with the market off; 1,000 seeded years give the volatility and half-life of the dials (asserted against MARKET_SIGMA, MARKET_HALF_LIFE); a held gap holds the level the formula says; buying US$ cheapens the D$ by INTERVENTION_IMPACT per 1% of GDP, fading at its half-life; one seed one path, two seeds differ; a save mid-swing continues exactly. And the finding: the spiral counted on the ensemble's held-0% and held-15% runs | every trace moves; re-baseline; the ensemble; EN1's S1, which measured the old channel, restated |
| **XR2** | **The regimes** (§3.6): Policy › The currency with Float, Lean (default), Band, Peg to the US$; the shadow rate; the vault paying the balance at the peg and the money leaving on doubt; the break (VAULT_FLOOR, hazard(), OVERSHOOT), the jump, credibility's dent, re-peg and devalue; the strong edge making M0. **Its screen:** board 2. | `CurrencyRegimeCheck`: a peg holds to the digit while the vault pays exactly the month's balance and the outflow; a vault at the floor breaks that month and lands at the shadow plus OVERSHOOT; a band's edges hold and its inside is free; Float sells nothing; Lean equals XR1's default to the cent; the chance of a break counted over 1,000 seeded pegs against hazard(); saved and loaded mid-peg | identical while Lean is the default |
| **XR3** | **The world's moneys move** (§3.5, §3.7), with or right after WN4 (★10): each money's PPP path, the dollar cycle, the risk-off, the terms of trade from the January solve, its own swing; pegs held and broken; Historical's monthly rates 2000-2025 (a table in WN0's world file); the D$ feels the cycle, the risk-off by its traits, and its terms of trade; the euro and the eight as anchors for XR2's peg. **Its screen:** board 3's table, the dollar's chart, the risk-off notice, the nation's card's money. | `WorldMoneysCheck`: each kind's volatility and half-life over 1,000 seeded paths within the dials'; pegs hold; breaks counted against the rule; one seed one path; Historical equals the table to 2025; a risk-off moves the havens up and the high yielders down by their loadings | every trace moves (the D$ now feels the world) |
| **XR4** | **Interest abroad, the vault's income and the carry** (§3.8): the eight's policy rates (Historical and Random); the city's real gap against the US's real rate (★9); the vault earning its moneys' rates; the carry book and the hot money on the US's rate, unwinding in a risk-off by their stock. **Its screen:** the policy rates column, the vault's interest line on The reserves. | `InterestAbroadCheck` (CarryTradeCheck's sections extended for the unwind): the vault earns each money's rate to the cent; a risk-off unwinds the carry and the hot money by their stocks; the real gap reads the US's real rate; `MoneyAudit` closes | every trace moves |

**Order and fit** (★10):
- **XR1** needs nothing new. Recommended: after the two fixes at the top of the queue (FX6, FX5), before CL4, since the currency is what Jerus calls weird today. It must go before WN3 in any case, so the ensemble measures it alone before WN3 moves every trace again.
- **XR2** right after XR1. It does not need CB1, but if CB1 is queued, after it: under a peg the dial's decisions are the peg's defence.
- **XR3 is WN4's rate rule.** Recommended: fold it into WN4, so the rule is built once, and put the monthly rates table into WN0's world file. WN4's brief then points here. The alternative is XR3 right after WN4, replacing WN4's simple drift.
- **XR4** after XR3, before WN6 (which shows D29's real US$ with §3.9's formulas).
- **CB1-CB3** are not queued. Where they land changes nothing here but the dial's path; CB2's overnight rate is the gap's rate once it exists.

---

## 6. ★ Decisions

*Open: asked of Jerus at the end of this session. Until he answers, XR1-XR4 wait under Pending in `QUEUE.md` with these calls.*

1. **★1 The market's own swing.** (a) Like a real float: 2.2% a month, fading by half in 3.5 years, about 8-9% a year in all; his city replayed moves 9.5% a year and never more than about 14 months one way. (b) Half that, about 4% a year: a calmer game. (c) None: only the fundamentals move it (his city replayed: 5.7% a year, all of it from the dial's own swings). **Recommended: (a).** A currency the player can watch move, for reasons the screen gives.
2. **★2 Parity on traded goods.** (a) The goods part of the basket (groceries and the luxury goods) against the US's goods. (b) The whole basket, as today: in slot 3, 41-80% rent. (c) The whole basket with rent held to a real share, about a third. **Recommended: (a).** Rents do not cross borders; on groceries alone his D$ ends 1% from parity, on the basket 23%.
3. **★3 The regimes and the default.** (a) Float, Lean against falls, Band and Peg, with Lean the default, today's behaviour. (b) Float, Lean and Peg, no Band. (c) Float and Lean only. **Recommended: (a).** Lean keeps "a reserve defends a currency; it does not hold one down"; the Band is Singapore's way, the regime of the one real city-state that runs its own money by its rate.
4. **★4 How a peg breaks.** (a) When the vault falls under a month of imports, or sooner by a chance that rises past a 10% gap (measured: 2.9-7.4 years at 2-10 points of inflation over the US, breaks of 31-47%). (b) Only when the vault runs out. (c) Never by itself: the player devalues or floats. **Recommended: (a).** Both real ways pegs end: the vault runs out, and doubt finishes it early.
5. **★5 The dial under a peg.** (a) It stays the player's, and a gap from the US's rate shows in the vault's monthly bill. (b) It is tied to the US's rate. **Recommended: (a).** The impossible trinity is learned, not imposed.
6. **★6 Who pays at the strong edge.** (a) The central bank buys the dollars with new money (M0 rises), as the SNB did. (b) The treasury buys them out of its cash. **Recommended: (a).** It is the central bank's job, and its inflation is the cost the player sees.
7. **★7 Interventions move the rate.** (a) 2% per 1% of a year's GDP, half gone in 6 months (the IMF's panel estimate). (b) Smaller and shorter: 1%, 3 months. (c) Not at all, as today. **Recommended: (a).** It fixes buying dollars making the D$ dearer.
8. **★8 The real gap's effect.** (a) 2.5% a point, the market expecting a gap to last 2.5 years, at most 35% either way. (b) Today's: 6.9% a point, no cap (slot 3's 16.9-point gap would hold the D$ 68% dear). **Recommended: (a).**
9. **★9 The world's real rate.** (a) The US's real policy rate (XR4). (b) The city's partners', weighted by trade. (c) Today's constant 2% less the world's inflation. **Recommended: (a)**, the D$ being quoted against the US$; (b) after WN3 if he wants the partners'.
10. **★10 Where the batches go.** (a) XR1 after FX6 and FX5, before CL4; XR2 right after; XR3 folded into WN4 (its monthly rates table into WN0's file); XR4 after WN4, before WN6. (b) XR1 and XR2 after the CD batches. (c) All four after WN7. **Recommended: (a).** The currency is what he calls weird now, and XR1 must be measured alone before WN3 moves every trace.
11. **★11 Historical's rates.** (a) The real monthly rates of 2000-2025 (BIS) in the world file. (b) The yearly rates, with the months drawn from the seed. **Recommended: (a).**
12. **★12 D29's display.** (a) D$ per 2015 US$ and the real effective rate (100 at founding), in WN6. (b) D$ per 2015 US$ only. **Recommended: (a).** The real effective rate answers "is my money dear?" against all the city trades with.

---

## 7. Found on the way

1. **Buying dollars makes the D$ dearer** (§1.4, measured: -7.7% after two years), and selling them moves nothing. XR1 fixes it; until then, the Trade tab's exchange card says nothing about it.
2. **Parity is mostly rent** (41-80% of the basket in slot 3). The parity gauge, the drawer's THE CURRENCY row and the header's rate line judge the D$ against the city's rents.
3. **The anchored drift reads the world's realised inflation against the city's credible target**, so in a world inflating 0.4% a year the D$ fell about 1.1% a year by rule (+0.435 in logs over slot 3's 476 months), whatever the city's prices did.
4. **WORLD_BASE_RATE is a constant 2%** while the world's inflation is a path: the world's real rate swings with it, and the city's real gap with it. XR4 reads the US's.
5. **His vault went from 10,000 to 1,647 by month 48 and to nothing by month 264**, and was never refilled; under the proposal an empty vault costs the D$ its 10% discount, which the screen should say.
6. **The dial reached 22.7% with 4.5% expected** (month 221, a 16.9-point real gap): the central bank study's autopilot swing, felt here as a 25% level; GAP_CAP bounds it, CB1's steps would soften it.
7. **slot 1's D$ is 2,749 a US$** after 250 years: a nominal rate that tracks a city's price index without limit is right for a money that inflates, and the currency reform exists for it (`Denomination`); nothing to change.

---

## Sources

*Gathered for this study by a research pass on 2026-10-21; `exchange-rates/research-notes.md` has each figure with its own source tag (S1-S48), the own calculations on the ECB's, the Fed's, the BIS's, the World Bank's and the EIA's data, and what could not be verified. Where a source was read only through another or a summary, it says so.*

- **[R1]** Rogoff, K. (1996), "The Purchasing Power Parity Puzzle", *Journal of Economic Literature* 34(2): 647-668. https://web.williams.edu/Economics/macro/rogoff.pdf
- **[R2]** Imbs, J., Mumtaz, H., Ravn, M., Rey, H. (2005), "PPP Strikes Back: Aggregation and the Real Exchange Rate", *Quarterly Journal of Economics* 120(1): 1-43. https://www.imf.org/en/Publications/WP/Issues/2016/12/30/PPP-Strikes-Back-Aggregation-and-the-Real-Exchange-Rate-16389
- **[R3]** Taylor, M.P., Peel, D.A., Sarno, L. (2001), "Nonlinear Mean-Reversion in Real Exchange Rates: Toward a Solution to the Purchasing Power Parity Puzzles", *International Economic Review* 42(4): 1015-1042. https://wrap.warwick.ac.uk/1687/ (its half-life table not retrieved)
- **[R4]** The Economist, the Big Mac index data, editions April 2000 to July 2026. https://github.com/TheEconomist/big-mac-data (gaps and their closing, own calculation)
- **[R5]** BIS, effective exchange rate indices, broad (64 economies), monthly 1994-2025. https://data.bis.org/topics/EER ; Klau, M., Fung, S.S. (2006), "The new BIS effective exchange rate indices", *BIS Quarterly Review*, March 2006: 51-65. https://www.bis.org/publications/qr-200603.pdf (spreads, the nominal-real link, the dollar's swings, oil's elasticities: own calculations)
- **[R6]** ECB, euro foreign exchange reference rates, daily, 1999-2026. https://data-api.ecb.europa.eu/service/data/EXR/D.USD.EUR.SP00.A (volatilities, autocorrelations, crosses: own calculations)
- **[R7]** Federal Reserve Board, H.10 historical foreign exchange rates (noon buying rates, New York). https://www.federalreserve.gov/releases/h10/hist/ (the crash table: own calculation)
- **[R8]** Mussa, M. (1986), "Nominal Exchange Rate Regimes and the Behavior of Real Exchange Rates: Evidence and Implications", *Carnegie-Rochester Conference Series on Public Policy* 25: 117-214 (through R1)
- **[R9]** Meese, R., Rogoff, K. (1983), "Empirical exchange rate models of the seventies: Do they fit out of sample?", *Journal of International Economics* 14(1-2): 3-24 (through the next two); Cheung, Y.-W., Chinn, M.D., Garcia Pascual, A. (2005), "Empirical exchange rate models of the nineties: Are any fit to survive?", *Journal of International Money and Finance* 24(7). https://www.nber.org/papers/w9393 ; Rossi, B. (2013), "Exchange Rate Predictability", *Journal of Economic Literature* 51(4): 1063-1119.
- **[R10]** Fama, E.F. (1984), "Forward and spot exchange rates", *Journal of Monetary Economics* 14(3): 319-338 (through R11)
- **[R11]** Chinn, M.D., Meredith, G. (2004), "Monetary Policy and Long-Horizon Uncovered Interest Parity", *IMF Staff Papers* 51(3): 409-430. https://www.nber.org/papers/w11077
- **[R12]** Burnside, C., Eichenbaum, M., Kleshchelski, I., Rebelo, S. (2011), "Do Peso Problems Explain the Returns to the Carry Trade?", *Review of Financial Studies* 24(3): 853-891. https://www.nber.org/papers/w14054
- **[R13]** Brunnermeier, M.K., Nagel, S., Pedersen, L.H. (2009), "Carry Trades and Currency Crashes", *NBER Macroeconomics Annual 2008*, vol. 23. https://www.nber.org/papers/w14473
- **[R14]** Dornbusch, R. (1976), "Expectations and Exchange Rate Dynamics", *Journal of Political Economy* 84(6): 1161-1176.
- **[R15]** Eichenbaum, M., Evans, C.L. (1995), "Some Empirical Evidence on the Effects of Shocks to Monetary Policy on Exchange Rates", *Quarterly Journal of Economics* 110(4): 975-1009; Faust, J., Rogers, J.H. (2003), "Monetary policy's role in exchange rate behavior", *Journal of Monetary Economics* 50(7): 1403-1424. https://federalreserve.gov/Pubs/IFDP/1999/652/ifdp652.pdf
- **[R16]** Kearns, J., Manners, P. (2006), "The Impact of Monetary Policy on the Exchange Rate: A Study Using Intraday Data", *International Journal of Central Banking* 2(4). https://www.rba.gov.au/publications/rdp/2005/2005-02.html
- **[R17]** Chen, Y.-c., Rogoff, K. (2003), "Commodity currencies", *Journal of International Economics* 60(1): 133-160; Cashin, P., Céspedes, L.F., Sahay, R. (2004), "Commodity currencies and the real exchange rate", *Journal of Development Economics* 75(1): 239-268 (both at the abstract's level).
- **[R18]** U.S. EIA, Europe Brent spot price FOB, monthly. https://www.eia.gov/dnav/pet/xls/PET_PRI_SPT_S1_M.xls
- **[R19]** Calvo, G.A. (1998), "Capital flows and capital-market crises: the simple economics of sudden stops", *Journal of Applied Economics* 1(1): 35-54; Calvo, G.A., Izquierdo, A., Mejía, L.-F. (2004), "On the Empirics of Sudden Stops", NBER WP 10520. https://www.nber.org/papers/w10520
- **[R20]** Ranaldo, A., Söderlind, P. (2010), "Safe Haven Currencies", *Review of Finance* 14(3): 385-407. https://www.snb.ch/en/publications/research/working-papers/2007/working_paper_2007_17
- **[R21]** IMF, *Annual Report on Exchange Arrangements and Exchange Restrictions 2021* (published 2022), the de facto classification and its tables.
- **[R22]** Calvo, G.A., Reinhart, C.M. (2002), "Fear of Floating", *Quarterly Journal of Economics* 117(2): 379-408.
- **[R23]** HKMA, "Refinements to the Operation of the Linked Exchange Rate System", 18 May 2005. https://www.hkma.gov.hk/eng/news-and-media/press-releases/2005/05/20050518-4
- **[R24]** Congressional Research Service, "The Argentine Financial Crisis: A Chronology of Events" (RS21130). https://www.everycrsreport.com/reports/RS21130.html ; IMF IEO, "The IMF and Argentina, 1991-2001". https://www.imf.org/external/np/ieo/2004/arg/eng/
- **[R25]** Krugman, P. (1979), "A Model of Balance-of-Payments Crises", *Journal of Money, Credit and Banking* 11(3): 311-325; Obstfeld, M. (1994), "The Logic of Currency Crises", *Cahiers Économiques et Monétaires* 43: 189-213 (both through summaries).
- **[R26]** Frankel, J.A., Rose, A.K. (1996), "Currency crashes in emerging markets: An empirical treatment", *Journal of International Economics* 41: 351-366. https://www.federalreserve.gov/pubs/ifdp/1996/534/ifdp534.pdf
- **[R27]** SNB, press release of 15 January 2015; ECB press release of 6 September 2011 (the SNB's floor); SNB data portal, balance sheet items. https://www.snb.ch/en/publications/communication/press-releases/2015/pre_20150115 ; https://data.snb.ch/api/cube/snbbipo/data/csv/en
- **[R28]** Obstfeld, M., Rogoff, K. (1995), "The Mirage of Fixed Exchange Rates", *Journal of Economic Perspectives* 9(4): 73-96. https://www.nber.org/papers/w5191 (the abstract)
- **[R29]** Klein, M.W., Shambaugh, J.C. (2008), "The dynamics of exchange rate regimes: Fixes, floats, and flips", *Journal of International Economics* 75(1): 70-92. https://nber.org/system/files/working_papers/w12729/w12729.pdf
- **[R30]** Obstfeld, M., Shambaugh, J.C., Taylor, A.M. (2005), "The Trilemma in History", *Review of Economics and Statistics* 87(3): 423-438. https://www.nber.org/papers/w10396
- **[R31]** IMF (2016), *Guidance Note on the Assessment of Reserve Adequacy and Related Considerations*. https://www.imf.org/external/np/pp/eng/2016/060316.pdf ; the Greenspan-Guidotti rule and the three months of imports (secondary, in the research notes, S44).
- **[R32]** Fratzscher, M., Gloede, O., Menkhoff, L., Sarno, L., Stöhr, T. (2019), "When Is Foreign Exchange Intervention Effective? Evidence from 33 Countries", *American Economic Journal: Macroeconomics* 11(1): 132-156. https://openaccess.city.ac.uk/id/eprint/19099/
- **[R33]** Adler, G., Lisack, N., Mano, R. (2019), "Unveiling the effects of foreign exchange intervention: A panel approach", *Emerging Markets Review* 40; IMF Working Paper 15/130 (2015). https://ideas.repec.org/p/imf/imfwpa/2015-130.html (the abstract's 1.7-2.0% nominal and 1.4-1.7% real per 1% of GDP, checked for this study)
- **[R34]** Japan, Ministry of Finance, foreign exchange intervention operations. https://www.mof.go.jp/english/policy/international_policy/reference/feio/quarter/2024_2Qe.html (and 2024_3Qe, 2022_4Qe)
- **[R35]** World Bank, World Development Indicators (reserves, current accounts, growth, exchange rates, GDP in constant 2015 US$), queried 2026-10-21. https://api.worldbank.org/v2/
- **[R36]** Monetary Authority of Singapore, monetary policy and past decisions. https://www.mas.gov.sg/monetary-policy ; https://www.mas.gov.sg/monetary-policy/past-monetary-policy-decisions
- **[R37]** Balassa, B. (1964), "The Purchasing-Power Parity Doctrine: A Reappraisal", *Journal of Political Economy* 72(6): 584-596; Samuelson, P.A. (1964), "Theoretical Notes on Trade Problems", *Review of Economics and Statistics* 46(2): 145-154 (cited from memory, not fetched).
- **[R38]** BIS, central bank policy rates, monthly. https://data.bis.org/topics/CBPOL (named for XR4's data, not fetched by the research pass).

*The small states' moneys (Macau's pataca, Liechtenstein's franc, Monaco's euro) are from secondary sources in the research notes, §9: Monaco, Liechtenstein and Macau are not IMF members and so not in R21's tables.*
