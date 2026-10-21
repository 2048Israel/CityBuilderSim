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

**What is weird, in a sentence a player would recognise.** The D$ glides: it moves about a tenth as much as a real currency, the same way for years on end, and its gauge says it sits a quarter from parity for decades, because "parity" is mostly the city's rent.

**Measured in his new city (slot 3, 476 months):**
- **Too smooth.** The rate's monthly moves come to **1.2% a year**; a floating currency moves about 8-12% [R#]. One run went the same way for **78 months**; in slot 1, **242**.
- **A lag that never closes.** Each month the rate closes 0.4% of its gap to a parity that keeps moving. A pull of k toward a target growing g a month settles at rate/parity = k / (k + g). From month 264 parity grew 2.6% a year, so the rule settles at **0.65 of parity**; measured, 0.76. The playtest's D$ sat at **0.70-0.80 of parity for 333 years** (1.00 to 1,209 D$ a US$, parity 1,649).
- **Parity is rent.** Rent is **41% to 80%** of the basket parity is struck from. From month 192 to 240, parity rose 50%: rent rose 75%, groceries 14%. On groceries alone, the traded part, parity ends at 1.315 and the rate at 1.303.
- **Buying dollars makes the D$ dearer.** The treasury's purchases are booked as reserve transactions, outside the balance the rate reads, and a fuller vault leans harder against falls. Measured on twin cities: buying US$205M of dollars left the D$ **7.7% dearer** two years on. In the real world, buying dollars cheapens your money; that is how Switzerland and China held theirs down.

**The proposal in one line:**

    rate = parity's trend  x  the fundamentals' level  x  the market

- **Parity's trend**, on the traded part of the basket, is followed **one for one**: the lag is gone (measured: 1.000 of parity at any steady inflation gap, against 0.34-0.83 today).
- **The fundamentals' level** is where the real rate gap, the overall balance and the vault's cover put the money. A rate rise appreciates it at once and it gives the gain back as the gap is expected to close: Dornbusch's overshooting [R#].
- **The market** is the money's own swing: seeded, about 2.2% a month, fading by half in about 3.5 years, the half-life of real currencies' gaps from parity [R#].

**Measured on his city's own parity, real rate and vault:** the D$ moves **9.5% a year** instead of 1.2%, never more than about **14 months** one way, and its fair level is within 25% of parity in all but 17 months (today's rule: 78).

**The city's choice (Policy › The currency):** **Float**; **Lean against falls**, today's behaviour and the default; a **Band** of its own centre, slope and width, as Singapore runs its money [R#]; or a **Peg** to the US$, and to the euro or the eight once the nations' moneys exist. A peg is held by the vault, which pays the month's whole balance at the peg and the money that leaves on doubt. It breaks when the vault falls under a month of imports, or sooner by chance as the gap grows. Interventions now move the rate, both ways.

**The world's moneys** follow their prices against the US's, the world's dollar cycle, a risk-off about once in eight years (havens up, high yielders down), their terms of trade from the January solve, and their own swing. Pegs are held, and broken by the same rule. Historical replays the real monthly rates of 2000-2025. **Measured:** majors 6-10.5% a year, an emerging float 14%; half-lives measured as an economist would, 2.9-4.0 years; a month of all 196 moneys and the D$ costs about **10 microseconds**.

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

  Buying dollars made the D$ **dearer**: the bigger vault took more of every month's fall. In the real world a central bank that buys dollars sells its own money and cheapens it [R#].

### 1.5 What the player sees

- **Trade › The currency:** the rate at 28 px, a gauge against parity (amber past 25%, red past 50%), the rate's chart with parity, and the forces on next month as diverging bars: the trade term, the real rate, the vault's defence, the push and the pull.
- **Policy:** the dial. **Trade › The reserves:** buy or sell dollars.
- What the player cannot see is a reason the rate moved this month that they could recognise. A month's move is a fraction of a percent, and its cause is three large forces netting to a small one.

---

## 2. What real currencies do

*Each point with its figure and its source; the research notes beside the study (`exchange-rates/research-notes.md`) have the longer form.*

### 2.1 The long run: parity, and how slowly

- **Relative purchasing-power parity holds in the long run and almost not at all in a year.** Rogoff's survey put the consensus half-life of real exchange rates' gaps from parity at **3 to 5 years** {ROGOFF}. Later work finds shorter half-lives once goods are not lumped together {IMBS}, and that large gaps close faster than small ones {TPS}.
- **The gaps are large.** Price levels of rich countries differ by tens of percent at market rates, as the Big Mac index shows every year {BIGMAC}; a gap of 20-30% that takes a decade to close is ordinary.
- **What holds best is traded goods.** Non-traded goods (rent, haircuts) are dearer in richer places, so a rich place's prices are high without its money being dear: the Balassa-Samuelson effect {BALASSA}.

### 2.2 The month: noise

- **Floating rates move about 8-12% a year** for the big moneys and more for emerging ones {VOL}.
- **Nominal and real rates move together** under floating: prices are sticky and the rate is not, so a float's real rate is as volatile as its nominal one {MUSSA}.
- **At a month's horizon nothing beats a coin.** Meese and Rogoff showed a random walk forecasts rates better than the models, out of sample, from one month to a year {MEESE}; the "disconnect" of rates from the fundamentals is one of the field's standing puzzles {OBSTFELD-ROGOFF-6}.

### 2.3 Interest rates: parity that fails, the carry and its crashes

- **Uncovered interest parity** says a money paying more should be expected to fall by the difference. Over months and years it fails, and the wrong way: high-yield moneys tend to rise, the forward premium puzzle {FAMA}. It holds better over five to ten years {CHINN}.
- **So the carry trade pays** on average: borrow the low-yield money, hold the high-yield one. Over decades a spread of carry trades earned several percent a year {BURNSIDE}.
- **And then it crashes.** "Up the stairs, down the elevator": its returns are skewed to the left, and the crashes come when risk appetite falls and carry traders unwind together {BNP}. October 1998 (the yen against the dollar), 2008 (the Australian dollar against the yen), August 2024 (the yen again) {CRASHES}.

### 2.4 Overshooting

- **Dornbusch:** with sticky prices, a money tightening jumps up at once, further than it will end, and then falls back as the rate gap is expected to close {DORNBUSCH}.
- **In the data the peak comes later**, a year or more after the shock: "delayed overshooting" {EICHENBAUM}. A point of surprise tightening moves a major money a few percent {SURPRISE}.

### 2.5 Trade, capital and the terms of trade

- **Commodity currencies** move with their export prices: the Australian, New Zealand and Canadian dollars' real rates follow world commodity prices {CHEN-ROGOFF}. In 2014-16, as oil fell by about three quarters, the ruble, the Norwegian krone and the Canadian dollar fell with it {OIL1416}.
- **Deficits can run for decades** while capital flows in to finance them (the US, Australia); a currency falls when the financing stops, a "sudden stop", and then by a third or more {CALVO}.

### 2.6 Risk and safe havens; the dollar cycle

- **In a panic money runs to the Swiss franc, the yen and the US dollar** {RANALDO}; in late 2008 and March 2020 the broad dollar rose sharply against nearly everything {DOLLAR}.
- **The dollar moves in long swings**, up by a third and down by a third over a decade at a time: 1980-85 up, 1985-95 down, 2002-11 down, 2011-22 up {DOLLAR}.

### 2.7 Regimes, and how pegs break

- **Most of the world does not float freely.** The IMF's classification puts about a third of its members' moneys in floating regimes and most of the rest in pegs, hard or soft {AREAER}. Even declared floaters lean on their rate: "fear of floating" {CALVO-REINHART}.
- **A currency board** backs its whole money with reserves and lends to nobody: Hong Kong's since 1983, its dollar held within 7.75-7.85 a US$ {HKMA}. Argentina's ended in 2002 and the peso lost about three quarters of its value {ARGENTINA}.
- **Pegs end in two ways.** The vault runs down until speculators finish it, Krugman's first-generation crisis {KRUGMAN}; or doubt itself makes defending it too costly, Obstfeld's second generation {OBSTFELD-94}. Sterling left the ERM in September 1992 and fell about 15%; the baht in 1997 lost about half; the ruble in 1998, more {PEGS}. The Swiss National Bank held a floor of 1.20 francs a euro from 2011 and dropped it on 15 January 2015: the franc rose about 20% in a day {SNB}.
- **Pegs rarely last.** By 1995 almost no sizeable economy had held a peg five years {MIRAGE}; pegs that break often re-peg {KLEIN}.
- **The impossible trinity:** a fixed rate, an open border for capital and a monetary policy of one's own cannot all be had at once {TRINITY}.

### 2.8 Reserves and intervention

- **How much is enough:** three months of imports is the old rule; reserves covering short-term debt abroad, Greenspan-Guidotti's; the IMF's own metric weighs both {RESERVES}.
- **Intervention works, a little and for a while.** In 33 countries' data it moved the rate the intended way most of the time, mostly when it leaned against a move already under way {FRATZSCHER}. A purchase of foreign money worth 1% of GDP cheapens the home money by about 1.7-2% in emerging markets {ADLER}.
- **The big ones:** Japan sold about ¥9.2 trillion of dollars in 2022 and about ¥15 trillion in 2024 to hold the yen up {JAPAN}; the Swiss National Bank bought hundreds of billions of francs' worth of foreign money to hold the franc down {SNB}; China spent about a trillion dollars of its reserves in 2014-16 {CHINA}.
- **Singapore runs its money by its rate:** the Monetary Authority sets a band for the trade-weighted Singapore dollar, its slope, width and centre, and intervenes at its edges; its interest rate follows {MAS}.

### 2.9 What a city would feel

- **Small, very open economies manage or peg their moneys far more than large ones** {AREAER}. The city-states show it: Singapore's band, Hong Kong's board, Macau's pataca on the Hong Kong dollar, Brunei's dollar at par with Singapore's, and Monaco, Luxembourg, Liechtenstein and Panama on others' moneys {CITYSTATES}.
- So a city-sized money would feel **the world's swings far more than its own policy**: the dollar cycle, risk-offs, the price of what it sells. Its own levers are the real rate, its vault, and its regime.

### 2.10 The measures

- **The real effective exchange rate** is a trade-weighted geometric average of the bilateral rates, each deflated by the two price levels; the BIS publishes it for 64 economies with weights from trade {BIS-EER}.
- **Constant 2015 US$:** the World Bank values GDP at a base year's prices and converts it at that year's exchange rate {WB}.

---

## 3. The design

### 3.1 The rule

    ln rate = ln parity's trend  +  the fundamentals' level reached  +  the market  +  interventions still felt

Each piece is a level, not a speed, so nothing lags a moving target, and each can be shown to the player as a line of its own.

### 3.2 Parity's trend, on traded goods, with no lag

- **On traded goods.** Parity is struck from the part of the basket that crosses borders: groceries and the luxury goods, against the US's goods prices (★2). Rent, services and meals stay out: nobody arbitrages a flat across a border. Real currencies' long-run anchor is closest for traded goods, and the gap between traded and non-traded prices is the Balassa-Samuelson effect, a reason a rich city's prices are high without its money being dear [R#].
- **Against the US's prices**, the in-game US$'s path (D29), once WN1 gives the US a path; until then the world's level, as today.
- **A trend, not the month.** The rate follows parity's level and slope (Holt's filter: a sixth of a month's jump at once, the slope over two years). A steady inflation gap passes one for one, so nothing lags; a single month's price spike moves the rate a sixth of it. Measured: at 1%, 2.1%, 4% and 10% a year of parity growth, rate/parity is 1.000 from year 10 on; through a 50% burst in four years, 0.97-1.02.
- **The spiral of 0.7.2.** This is the drift form that 0.7.2 deleted, slowed: the inflation gap passes into the rate in the long run. 0.7.2 deleted it because, with imports repricing one for one, wages chasing the index and the shelf cost-plus, a gain of one let inflation feed itself through the currency. Since then inflation answers to money (P2, P3) and the real rate holds the currency. XR1 must measure it on the ensemble's held-0% and held-15% runs (S1, R3) and count any runaway; the trend's smoothing is the dial if it bites.

### 3.3 The fundamentals' level

Where the city's own figures put the money, as a log gap from parity's trend, reached half in a month (MARKET_SPEED):

- **The real rate** (★8). -GAP_CAP x tanh(PERSISTENCE x gap / GAP_CAP). The gap is today's: the dial less expected inflation, against the world's real rate (XR4: the US's). PERSISTENCE is how many years the market expects the gap to last: 2.5, so a sustained point holds the D$ about 2.5% dearer. GAP_CAP, 35%, keeps a 17-point gap like slot 3's month 221 to 25% instead of 68%. **A rise appreciates the money at once, then it depreciates as the gap is expected to close**: Dornbusch's overshooting [R#], the textbook answer to "why did my currency jump when I raised rates".
- **The balance.** The overall balance, current plus financial account over twelve months, against a year's GDP, times BALANCE_WEIGHT (0.5, capped at 20%): a city that runs a deficit year after year owes more abroad and its money cheapens. Today's trade term made into a level.
- **The vault's cover.** RISK_PREMIUM (10%) times how far the cover falls short of SAFE_COVER (3 months of imports, the usual rule [R#]): an empty vault costs the money a tenth; a vault of three months or more, nothing.
- **Debt abroad and hot money** (XR3): add to the risk-off's bite, not to the level (§3.5).

### 3.4 The market

- The money's own swing (★1): AR(1), MARKET_SIGMA = 2.2% a month, MARKET_HALF_LIFE = 42 months. Its spread around the fair level is about 13% either way (one standard deviation). Most of a real float's month is this: no model beats a random walk at a month's horizon [R#].
- **Seeded** from the city's seed (WN1's, once it exists; until then a seed drawn at founding and saved), so one seed is one path, as the nations study's D9 asks.
- **Gated by trade.** Below MIN_TRADE a month (today's floor) the rate is not a real price and holds; the market's swing is scaled by the city's openness until it trades a tenth of its GDP.
- **What it does to prices.** Imports reprice through the rate one for one in the game, so a city whose basket is a third imported takes about a third of the swing into its prices: about 3% a year of noise in its inflation at ★1's (a), half at (b). Real economies pass far less through in the short run [R#]. XR1 measures the ensemble's inflation spread before and after; if it bites, a pass-through dial on import prices is the fix, not a calmer currency.

### 3.5 The world reaches the D$ (XR3)

Once the nations' moneys move (§3.7), the D$ against the US$ also carries:
- **The dollar cycle**, with a loading of 1: when the dollar rises against everything, it rises against the D$.
- **A risk-off.** About once in eight years, money runs to safety for a few months. The D$'s move in one is its traits': +10% weaker for a small open money, less with a deep vault and a surplus, more with hot money and debt abroad. A city with six months of cover and a surplus is a haven and rises, as the Swiss franc does [R#].
- **Its terms of trade**, from its own trade by good (WN3): a city that sells lumber cheapens when lumber does, by half its share of lumber in its exports, as commodity currencies do [R#].

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
- **How a peg breaks** (★4): when the vault falls under VAULT_FLOOR (one month of imports), or by chance, at hazard(x) = 0.1% + 3% x (x - 10%) / 10% a month past a 10% gap. That is a first-generation crisis (Krugman 1979: the vault runs down until speculators finish it [R#]) with a second-generation chance (Obstfeld: doubt itself can break a peg [R#]). The rate jumps to the shadow plus OVERSHOOT (a quarter) of the gap again, the regime becomes Float, and Expectations' credibility takes a dent. The player can also devalue (re-peg lower) as a decision.
- **Measured** (RatesModel §5, a peg that does not discipline its own inflation, a vault of six months): 2 points of inflation over the US hold for a median **7.4 years** and break by **31%**; 5 points, **4.5 years** and **37%**; 10 points, **2.9 years** and **47%**. History: sterling left the ERM in 1992 and fell about 15%; the baht 1997, about half; Argentina 2002, about 70% [R#].
- **Under a peg the dial stays the player's** (★5). Hold it under the US's and money leaves for the US; the vault pays. The impossible trinity [R#] is felt, not imposed: a free dial, an open border and a fixed rate cannot all hold at once.
- **Buying at the strong edge** (a band's or a peg's) makes D$: the central bank buys the dollars with new money (M0 rises), as the SNB did from 2011 to 2015 [R#]. The treasury's own purchases stay the treasury's (★6).
- **Interventions move the rate.** A sale of dollars, the treasury's, the defence's or the band's, makes the D$ dearer by INTERVENTION_IMPACT (2%) per 1% of a year's GDP, fading by half in INTERVENTION_HALF_LIFE (6 months); a purchase cheapens it as much (★7). The size is the IMF's panel estimate for emerging markets [R#]; Japan's yen interventions of 2022 and 2024 are the famous large ones [R#]. This fixes §1.4.
- **A currency board and dollarisation** are not offered: a board forbids the central bank to lend to the treasury, which the CB study's advances need, and dollarisation removes the D$ every screen is in.

### 3.7 The world's moneys (XR3, on WN1 and WN4)

Each of the ~145 moneys, against the US$:

    ln rate_i = its PPP path  +  b_i x the dollar cycle  +  r_i x the risk-off  +  t_i x its terms of trade  +  its own swing

- **Its PPP path:** its inflation against the US's on its own path (WN1).
- **The dollar cycle:** one AR(1) for the world, half-life four years, 1.1% a month; b_i about 1, less for a money tied to the US (Canada's 0.6). The broad dollar's real swings were a quarter to a third over a decade [R#]; measured in the prototype, the widest swing in 26 years is 24% at the median seed, 32% at the 90th.
- **The risk-off:** about once in eight years, fading with a three-month half-life. r_i: the yen -8%, the franc -5% (they rise), the euro +3%, the pound +6%, the Canadian dollar +7%, the Australian dollar +14%, an emerging float +20%. At one, the A$ falls about 20% against the yen in its month; 2008's fall was larger and slower, 2024's smaller [R#].
- **Its terms of trade:** from each January's solve (WN3), its export prices over its import prices, passed at t_i = half its commodity share of exports, spread over the next three months. Chen and Rogoff's commodity currencies move with their export prices [R#].
- **Its own swing:** AR(1), half-life 3.5 years, 1.4-3.2% a month by kind.
- **Pegs:** held where the IMF classes the money as a peg (the euro's users are the euro; the CFA francs on the euro; the Gulf and Hong Kong on the US$) [R#]. A soft peg breaks by §3.6's rule on its own shadow rate (Random), or on its real date (Historical). A board (Hong Kong) breaks only if its vault falls under its money.
- **Historical** replays the real monthly rates of 2000-2025 (★11) and starts the swings from each money's real gap from PPP in December 2025. **Random** draws all of it from the seed.
- **In the yearly solve and between:** the January solve prices in US$ (the nations study's unit of account); it hands each nation its terms of trade, which reach the rate over the next three months. Between solves, each month steps the cycle, the risk-off and the swings. Cross rates go through the dollar. The rates move no nation's quantities (D8: the world answers the city in prices only).
- **Measured** over 1,000 seeded 26-year paths: volatility a year, the euro 7.4%, the yen 9.0%, the pound 7.6%, the franc 8.1%, the Canadian dollar 6.0%, the Australian dollar 10.5%, an emerging float 13.8%; the half-life an economist would measure on 26 years of it, 2.9-4.0 years (the model's own, 3.5); the worst month of the median path, 7-26% by kind.

### 3.8 Interest abroad, the vault's income and the carry (XR4)

- **The eight central banks' rates:** Historical from their real policy rates 2000-2025 [R#]; Random and after 2025, a rule on each one's inflation path, in quarter-point steps (CB1's step).
- **The city's real gap** is read against the US's real rate (★9), not the constant WORLD_BASE_RATE of 2% against a world inflation that moves (in slot 3's world, 0.4% a year, which made the world's real rate 1.6%).
- **The vault earns** each money's rate, as Canada's Exchange Fund Account earns for its government [R#] (the CB study's "the vault earns nothing").
- **The carry:** the bank's foreign book (CarryTrade) borrows in the cheapest of the eight, and the hot money (CapitalFlows) compares the dial with the US's. Both unwind in a risk-off, by their stock: a city that drew hot money with a high dial falls harder when money runs home. Measured on the prototype's A$ against the yen: the carry earns 4.0% a year with a Sharpe ratio of 0.29, a monthly skew of -1.60, and a worst month of -33%: the "picking up nickels in front of a steamroller" of the literature [R#].

### 3.9 D29's real US$ and the real effective rate (WN6, ★12)

- **The comparison rate:** D$ per 2015 US$ = D$ per US$ x (the US's price level now / its level in 2015). A real 2015 US$ buys what a dollar bought in 2015.
- **Real GDP in 2015 US$**, the World Bank's way [R#]: the city's GDP at founding's prices, divided by founding's rate, carried back to 2015 dollars by the US's price path: real GDP_D$ / rate_founding x (P_US,2015 / P_US,founding).
- **The real effective rate:** 100 x exp(sum of w_j x ln(the D$'s price in money j x P_city / P_j)), with w_j the city's trade by partner (WN3), 100 at founding, the BIS's way [R#]. Above 100, the D$ is dearer in real terms than it was. Before WN3 it is the D$ against the US$ alone.
- Nothing in the model reads any of the three. They answer "is my money dear?", which today's gauge answers against a parity made of rent.

### 3.10 What it costs, measured

- **A month** of all 196 moneys and the D$: about **10 microseconds** (9.3-13.6 across runs), against the nations study's 50 ms January solve.
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
| **XR4** | **Interest abroad, the vault's income and the carry** (§3.8): the eight's policy rates (Historical and Random); the city's real gap against the US's real rate (★9); the vault earning its moneys' rates; the carry book and the hot money on the US's rate, unwinding in a risk-off by their stock. **Its screen:** the policy rates column, the vault's interest line on The reserves. | `CarryCheck`: the vault earns each money's rate to the cent; a risk-off unwinds the carry and the hot money by their stocks; the real gap reads the US's real rate; `MoneyAudit` closes | every trace moves |

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

*(filled from the research notes, `exchange-rates/research-notes.md`)*
