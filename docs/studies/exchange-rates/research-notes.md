# Exchange rates: research notes for the study (legwork, 2026-10-21)

Purpose: real facts with numbers and sources, so the study can calibrate a monthly model of the D$ and of
about 145 national moneys. Not a design; no recommendations except the closing cheat-sheet.

How to read it:
- `[S#]` is a source in the list at the end (full reference and URL). `(own calc)` is a figure I computed from the dataset named by the tag; the scripts are not kept, the
  recipe is in the bullet.
- `(unverified)` is from memory or from a search-engine summary I could not trace to the page.
- Data vintages: ECB reference rates 1999-01-04 to 2026-10-20 [S8]; Fed H.10 noon NY rates, to 2011-06 [S9]; BIS effective rates to 2026-08, base 2020 = 100 [S6]; World
  Bank WDI to 2025 [S26]; Economist Big Mac to July 2026 [S5]. "Monthly sd" is the standard deviation of month-end log changes. ECB rates are fixed at 14:15 CET, so
  intraday extremes are missed. Quotes are foreign currency per US$ unless a pair is named like EURUSD (US$ per EUR). "Value" changes are of the currency named.

## 1. PPP in the long run: half-lives and how far real rates stray

- Rogoff's "consensus": deviations from PPP damp at roughly 15% a year, a half-life of 3 to 5 years, "seemingly far too long to be explained by nominal rigidities"; the
  puzzle is that against this the one-month conditional volatility of real rates is "of the same order of magnitude" as that of nominal rates [S1, pp. 647-648, 657-658,
  664].
- The studies behind the 3-5 years [S1, section 5]: Frankel 1986 (dollar/pound 1869-1984) 14% a year decay, half-life 4.6 years; Abuaf-Jorion 1990 (eight currencies
  1901-72) 3.3 years; Diebold-Husted-Rush 1991 (gold-standard era) 2.8 years on average; Lothian-Taylor (two centuries) 4.7 years dollar/pound and 2.5 years franc/pound;
  Frankel-Rose 1995 (150 countries 1948-92, floating-era panel) about 4 years; Wei-Parsley 1995 (14 OECD, tradables) 4.75 years outside the EMS, 4.25 inside it.
- Rogoff's own caveat: mean reversion looks stronger when high-inflation countries are in the sample, because monetary shocks dominate there [S1, p. 657]. Mussa 1986
  showed real rates are far more volatile under floating than under fixed nominal rates [S1, p. 656, citing S10].
- Larger gaps revert faster (nonlinear): Wei-Parsley found "the rate of convergence to PPP is faster when initial deviations are large" [S1, p. 657]. Taylor-Peel-Sarno
  2001: for four real dollar rates since 1973 the log real rate is close to a random walk near equilibrium and "becoming increasingly mean-reverting with the absolute
  size of the deviation"; half-lives "vary both with the size of the shock and with the initial conditions" and are "typically much faster" than 3-5 years; low power of
  linear unit-root tests is the explanation [S3]. The half-life numbers by shock size are in their tables, which I could not read (garbled PDF): (unverified).
- Shorter half-lives: Imbs-Mumtaz-Ravn-Rey 2005 (dynamic aggregation bias): preferred half-life 11 months with a 7-12 month interval; 18 months after a bias correction; a
  plain fixed-effects panel on the same data gives 3 years 10 months [S2, intro and section IV]. Their other estimators: mean-group just above 2 years (95% interval 14-28
  months); mean-group SURE 22 months (17-27); mean-group CCE 11 months (7-12), "an upper bound ... of one year".
- Longer / wider: Lopez-Murray-Papell 2009: median-unbiased half-life confidence intervals run "from about one year to infinity"; with DF-GLS the typical lower bound is
  "just under 3 years" on long-horizon data [S4].
- BIS REER, 64 economies, monthly 1994-2025 (own calc, S6): median sd of log REER around its own mean 10.0% (p10 3.4%, p90 22.9%; Japan 24.6%, Argentina 51.9%, Germany
  6.6%, Switzerland 6.3%). Median monthly AR(1) of the log level 0.984, i.e. a half-life of 3.5 years; sampled annually the median AR(1) is 0.80, half-life 3.1 years
  (short samples bias this downward; by country the range is 0.7 years for Korea to more than 13 years for Japan).
- Trend of REER versus NEER 1994-2025 (own calc, S6): median REER drift +0.14% a year (p10 -0.94, p90 +2.10) while NEER drift is far wider (median +0.37, p10 -9.2, p90
  +1.7). High-inflation money: Turkey NEER -24.2% a year but REER -0.7%; Argentina -19.5% against -4.0%; Brazil -9.5% against -0.7%. So the nominal rate carries the
  inflation differential and the real rate stays near flat.
- Big Mac spreads (own calc from the Economist CSV, S5). Valuation = (local price / US price) / market rate - 1: July 2026 (54 economies): median -19.2%, p10 -53.5%, p90
  +19.0%, sd 26.8%; most overvalued Switzerland +45.4%, then Uruguay +43.7%, Norway +29.5%; most undervalued Indonesia -61.7%, Taiwan -61.0%, India -60.5%. Euro area
  +13.8%, Britain +19.0%, Sweden +15.1%, Canada -6.5%, Australia -4.3%, Singapore -7.2%, New Zealand -17.4%, China -37.1%, Hong Kong -47.7%, Japan -50.4%. Earlier
  editions: April 2000 sd 31.6% (Israel +59.8%, Malaysia -46.9%); July 2011 sd 43.6% (Norway +128%, India -48%); July 2015 sd 26.6% (Switzerland +59%, Venezuela -84%).
- Big Mac persistence (own calc, 1,435 country-year pairs 2000-2026): relative to the edition median, with country fixed effects removed, the annual AR(1) is 0.53
  (half-life about 1.1 years; Nickell bias makes this too fast); sd of the country-demeaned log gap 17%. Gaps under 40% close about 24-31% a year, gaps over 40% (n=30)
  about 67% a year: a nonlinear pull. Without country effects the AR(1) is 0.88 because poor countries stay cheap (Balassa-Samuelson, S1 section 6; Cumby 1993 found only
  30% of Big Mac deviations persisting a year [S1, note 9]).
- Balassa-Samuelson in one line: rich countries have higher price levels because productivity gaps are larger in tradables than in services; it lets the long-run real
  rate drift with relative productivity [S1, section 6A].

## 2. Short-run behaviour: volatility, random walk, the disconnect

Month-end log changes, 1999-2025 (ECB reference rates, own calc, S8; BRL, INR, ILS, CNY from 2000). EURUSD is US$ per EUR; in the USDxxx rows the quote is units of
xxx per US$, so a rise there is a fall of xxx against the dollar. Annualised = monthly sd x sqrt(12). 12m sd = sd of 12-month changes. AC1 = first autocorrelation of
monthly changes. min / max = the largest monthly fall and rise of the quoted number.

| pair | monthly sd | annualised | 12m sd | AC1 | monthly min / max |
|---|---|---|---|---|---|
| EURUSD | 2.68% | 9.3% | 9.7% | +0.03 | -11.4% Oct 2008 / +8.9% Dec 2008 |
| USDJPY | 2.73% | 9.4% | 9.8% | +0.05 | -7.8% Jun 2016 / +8.3% Feb 2009 |
| USDGBP | 2.47% | 8.5% | 9.0% | +0.04 | -8.3% May 2009 / +11.0% Oct 2008 |
| USDAUD | 3.40% | 11.8% | 12.0% | +0.03 | -9.0% Sep 2010 / +19.6% Oct 2008 |
| USDCAD | 2.43% | 8.4% | 7.9% | -0.06 | -7.8% May 2009 / +16.1% Oct 2008 |
| USDCHF | 2.76% | 9.6% | 8.1% | -0.06 | -12.9% Dec 2008 / +11.0% Sep 2011 |
| USDNZD | 3.63% | 12.6% | 12.6% | -0.03 | -13.3% Mar 2009 / +14.9% Oct 2008 |
| USDNOK | 3.35% | 11.6% | 11.3% | 0.00 | -8.1% Sep 2010 / +13.8% Oct 2008 |
| USDSEK | 3.20% | 11.1% | 11.8% | +0.02 | -10.0% Sep 2010 / +12.6% Oct 2008 |
| USDKRW | 2.90% | 10.0% | 9.8% | +0.01 | -10.9% Mar 2009 / +11.3% Nov 2008 |
| USDMXN | 3.13% | 10.9% | 9.9% | +0.05 | -7.7% Jan 2012 / +19.2% Mar 2020 |
| USDINR | 1.94% | 6.7% | 7.1% | +0.07 | -7.2% Jan 2012 / +9.4% Aug 2013 |
| USDZAR | 4.51% | 15.6% | 15.6% | -0.04 | -11.3% Apr 2009 / +19.6% Oct 2008 |
| USDBRL | 4.71% | 16.3% | 17.6% | 0.00 | -16.0% Apr 2003 / +25.8% Sep 2002 |
| USDTRY | 5.20% | 18.0% | 21.6% | +0.13 | -9.6% May 2003 / +39.9% Feb 2001 |

Other monthly sd (annualised): PLN 3.7% (12.8%), HUF 3.8% (13.2%), IDR 3.6% (12.4%), CZK 3.3% (11.5%), ILS 2.3% (7.8%), THB 2.1% (7.3%), MYR 1.9% (6.6%), PHP 1.8%
(6.2%), SGD 1.6% (5.4%), CNY 1.0% (3.3%; 2.1% daily-annualised over 1999-2007 under the old peg), HKD 0.17% (0.6%). EURUSD by period: 9.1% (1999-2007), 10.2% (2008-19),
7.3% (2020 to Oct 2026).

- G10 floats: annualised volatility 8-12% (monthly sd 2.4-3.6%); emerging floats 11-18%; Asian managed 3-7%; a currency board about 0.6% [S8 own calc]. The game's current
  1% a year is the level of Hong Kong's currency board.
- Random-walk signature: AC1 of monthly changes between -0.06 and +0.13; the 12-month variance ratio (var of 12m change over 12 x monthly var) is 1.08 EURUSD, 1.12 GBP,
  1.05 AUD, 0.88 CAD, 0.72 CHF, 0.83 MXN, 1.44 TRY (own calc). Calendar-year changes had the same sign as the previous year's in only 10 of 25 years for EURUSD, 16 of 25
  USDJPY, 12 of 25 for most others: no year-on-year momentum (own calc). Multi-year swings exist in effective rates (section 6).
- Fat tails (monthly, 1999-2025, own calc): kurtosis 3.3-4.7 EURUSD/JPY/GBP/CHF, 6-9 AUD/CAD/MXN, 18 TRY; months beyond 3 sd are 0.3-2.3% of months (normal 0.3%); a move
  over 5% occurs in 7% of EURUSD months, 12% AUD, 23% BRL/ZAR/TRY. Volatility clusters in |returns| (AC1 of |r| -0.03 to +0.34, mostly +0.08 to +0.15; KRW +0.34, BRL
  +0.27, INR +0.22).
- Fear of floating: the probability that a month's change versus the US$ stays within +-2.5% is 58.7% for US/DM and 61.2% for Japan, 79.3% on average for emerging
  "floaters", 87.5% managed floats, 92.0% limited flexibility, 95.3% pegs; Singapore 89% [S33, Tables 2-3 and note 21]. My ECB numbers imply about 60% for G10 (sd
  2.5-3%).
- Nominal and real move one for one in the short run (Mussa 1986 [S1, S10]); BIS data 1994-2025, 64 economies, monthly (own calc, S6): median correlation of monthly dREER
  with dNEER 0.90, median regression slope 0.97, annualised sd of REER 4.7% and of NEER 4.4%. Weak links only where inflation differentials are big: Brazil slope 0.44
  (corr 0.66), Argentina 0.74, Russia 0.71, Turkey 0.73; Singapore (REER sd 2.6%, NEER 2.1%) is the calm tail.
- Meese-Rogoff 1983 (J. Int. Econ. 14(1): 3-24): out-of-sample, no structural model beat a random walk at 1-12 month horizons [S11 summarises it; the 1983 paper itself
  was not read]. Cheung-Chinn-Pascual 2005: "no model consistently outperforms a random walk according to the mean squared error criterion at short horizons";
  direction-of-change tests do better (a sticky-price model beats the walk 23% of the time at the 10% level) [S11]. Rossi 2013 survey: predictability "depends on the
  predictor, forecast horizon, sample period, model and evaluation method"; most visible with Taylor-rule or net-foreign-asset predictors, linear models and few
  parameters [S12].
- The "disconnect": the Chen-Rogoff 2006 slides quote Lyons 2002, "at horizons less than two years the explanatory power of macro-fundamental-based exchange rate models
  is essentially zero" [S21].

## 3. Interest differentials: UIP, the forward premium puzzle, carry

- UIP says the high-rate currency depreciates by the differential; Fama 1984 (J. Monetary Econ. 14(3): 319-338) found the opposite sign in the forward regression, so the
  high-rate currency tends to appreciate a little ("often called the forward premium puzzle ... precisely what makes the carry trade profitable on average") [S16 intro,
  S13]. Froot-Thaler 1990 survey: average slope -0.88 (UIP implies +1) [S14, S15].
- Chinn-Meredith (six G7 rates against the dollar, 1980Q1-2000Q4, quarterly) [S14]: short-horizon slope averages about -0.8; constrained panel -0.76 at 3 months to -0.54
  at 12 months; adjusted R2 "very low, occasionally negative"; four of six estimates have the wrong sign. At long horizons (5-year bond yields) slopes are "statistically
  indistinguishable from unity", Deutschemark 0.870, pound and Canadian dollar closer to zero; no case rejects either 0 or 1. UIP holds better at long horizons because
  exchange rates then follow fundamentals; at short horizons policy reacts to shocks.
- The sign flips with the regime (Bussiere-Chinn-Ferrara-Heipertz, 1-year dollar regressions, eight currencies) [S15]: pre-2006 slopes -2.1 to -5.2; middle period (to
  April 2018) positive in all eight ("New Fama Puzzle"); late period (to September 2021) negative again, -0.9 to -10.3 except Japan. Expectations data show the pattern
  comes from forecast errors moving with the differential, not mainly from risk.
- Carry returns, equal-weighted basket of all currencies versus the base, 1976-2009 [S17]: mean 3.2% a year, sd 4.3%, Sharpe 0.75 (pound base); US$ base 1997-2009 mean
  4.4%, sd 5.1%, Sharpe 0.87; bid-ask costs cut the Sharpe to 0.51 and 0.69. Worst months: -8.9% (March 1991), -5.8% (Oct 1992), -5.1% (June 1993); 2008-09 worst months
  only -4.2%, -3.9%, -3.7%; the strategy lost 10.7% peak to trough (Jul 2008-Feb 2009) while US stocks lost 51.6% (Oct 2007-Feb 2009): a diversified basket crashed far
  less than any single pair below.
- Brunnermeier-Nagel-Pedersen 2008 [S16]: weekly and quarterly carry portfolios (1-3 long/short currencies, 1986-2006) have Sharpe 0.59-0.78, quarterly mean 1.6-2.2%,
  quarterly sd 4.5-6.8%, skewness -0.54 to -0.98 (the US stock index has -0.41 weekly, -0.88 quarterly); skewness is not diversified away; they point to large currency
  moves without news announcements, e.g. the US$ against the yen on 7-8 October 1998. They tie carry crashes to falling funding liquidity and risk appetite.
- Carry crash sizes, measured (own calc):
  - USD/JPY, Fed noon rates [S9]: 134.54 (5 Oct 1998) to 123.97 (7 Oct), 118.85 (8 Oct), 117.00 (9 Oct): yen +15% in 4 trading days; 147.14 on 11 Aug 1998 to 113.08 on 31
    Dec (yen +30%).
  - AUD/JPY, ECB crosses [S8]: 107.5 (20 Jul 2007) to 56.1 (2 Feb 2009): -47.8% in 19 months; 100.5 (1 Aug 2008) to 64.9 (31 Oct 2008): -35% in three months. AUDUSD 0.982
    (15 Jul 2008) to 0.611 (27 Oct 2008): -37.8% in 3.4 months.
  - USD/JPY, ECB [S8]: 161.9 (3 Jul 2024) to 142.2 (5 Aug 2024): -12.1% in 33 days (the Bank of Japan raised its rate to about 0.25% on 31 July 2024, from search summaries of
    AP/ABC reports, unverified); AUD/JPY -15.9% 11 Jul to 5 Aug 2024. Rate at 31 Dec 2024: 157.0, i.e. most of the move was reversed within five months.

## 4. Overshooting

- Dornbusch 1976 (J. Polit. Econ. 84(6): 1161-1176) [S19, bibliographic only]: with sticky goods prices, perfect capital mobility and rational expectations, a monetary
  expansion makes the exchange rate jump beyond its long-run level and then appreciate back; UIP ties the expected path to the interest differential.
- Evidence is of "delayed overshooting": Eichenbaum-Evans 1995 (Q. J. Econ. 110(4): 975-1009) and Clarida-Gali find the exchange-rate response to a policy shock peaks
  after one to three years rather than at once [S18]. In Faust-Rogers' replication of E-E the exchange rate response to a money shock "peaks at about three years", the
  output hump at 12-18 months [S18]. E-E find UIP violated on the way to the peak (the rate keeps appreciating while UIP says it should fall). Monetary shocks explain
  "from a few percent to over one-half" of exchange rate variance [S18].
- Fragile: the same Faust-Rogers study shows the peak timing is sensitive to identification; plausible identifications put the peak very early (months or immediately) and
  imply the overshoot is not Dornbusch's UIP mechanism [S18]. Scholl-Uhlig 2008 and Kim-Roubini 2000 report early peaks with sign restrictions / structural VARs
  (unverified, not read).
- Size of the response in high-frequency data: Kearns-Manners (Australia, Canada, New Zealand, UK, 70-minute windows): an unanticipated 100bp rise lifts the currency
  about 1.5% (0.35% for 25bp; country range 0.25-0.5% per 25bp); a surprise that moves expectations by its full size gives about 0.4%, one that only brings forward an
  expected move about 0.2%; surprises explain only 10-20% of the movement in the window [S20].
- Interest-rate pass-through by regime, 130 years, annual changes in local on base-country rates: pegs 0.43 (R2 0.17) against floats 0.26 (R2 0.01); post-Bretton-Woods
  pooled 0.36; Bretton Woods era -0.20 under capital controls [S35].

## 5. Current account, capital flows, terms of trade

- Commodity currencies: Chen-Rogoff 2003 (J. Int. Econ. 60(1): 133-160) study Australia, Canada, New Zealand: the US$ price of commodity exports has a strong and stable
  influence on the floating real exchange rate, especially for Australia and New Zealand, with effects of a size in line with standard theory; a PPP puzzle remains in the
  residual (abstract via search summary, S21; the elasticities themselves were not retrievable). Cashin-Cespedes-Sahay 2004 (J. Dev. Econ. 75: 239-268): a long-run link
  of the real rate to real commodity prices for only about one third of 58 commodity exporters (two fifths in the 2002 IMF draft) [S22 via search summaries].
- Own measure, BIS REER on Brent, monthly 1994-2025 (S6, S23): elasticity of the REER to Brent, monthly changes / annual changes: Norway 0.06 (corr 0.41) / 0.11 (0.65);
  Canada 0.06 (0.38) / 0.13 (0.73); Australia 0.05 / 0.13 (0.55); New Zealand 0.02 / 0.12; South Africa 0.07 / 0.21; Brazil 0.07 / 0.16; Russia 0.05 (0.10) / 0.23 (0.42);
  Mexico 0.07 / 0.05 (weak); the US -0.03 / -0.07 (corr -0.51), Japan -0.04, Gulf states negative (dollar-pegged, inverse of the dollar cycle). A doubling of oil goes
  with about +9.3% on the Canadian and +7.6% on the Norwegian REER over a year (0.134 and 0.110 x ln 2); Brent is not each country's own export basket, and oil also
  co-moves with the dollar.
- The 2014-16 oil fall (own calc). Brent monthly averages [S23]: $111.8 (Jun 2014) to $30.7 (Jan 2016): -72.5%; annual averages 2013 $108.6, 2014 $99.0, 2015 $52.4, 2016
  $43.5. Effective exchange rates, BIS broad NEER, Jun 2014 to Jan 2016 [S6]: Norway -17.5% (REER -14.2%), Canada -18.0% (-17.7%), Australia -15.0%, Mexico -22.2%, South
  Africa -24.0%, Brazil -34.1%, Russia -47.2% (REER -36.5%); against these US +21.4%, euro area -5.7%, Sweden (not an oil exporter) -4.1%. Bilateral per US$ (ECB crosses
  [S8]): NOK -29.2%, CAD -24.1%, RUB -55.2% (35.5 on 31 Jul 2014, 60.2 on 15 Dec, 73.0 on 16 Dec 2014: -51% in 4.5 months, -17.6% on 16 Dec alone), MXN -29.2%, BRL
  -45.8%; EUR -20.0%.
- Sudden stops: Calvo 1998 (J. Appl. Econ. 1(1): 35-54), a sharp, unexpected fall in capital inflows that forces the current account to adjust through a fall in
  absorption and a real depreciation. Calvo-Izquierdo-Mejia 2004 [S24], 32 countries: a stop starts when the annual change in capital flows is 1 sd below its mean and
  contains a point 2 sd below; "large real exchange rate fluctuations coming hand in hand with Sudden Stops are basically an emerging market phenomenon": only 37% of
  emerging-market depreciation episodes (25% excluding South Africa) occurred without a sudden stop, but 83% of developed-country episodes did (their depreciation dummy
  uses a 20% real threshold, read from a figure title). Measured falls are in the section 7 crash table.
- Reversals: Freund 2000 (25 industrial-country episodes 1980-97): a reversal typically begins at a deficit near 5% of GDP and comes with slower growth and a 10-20% real
  depreciation [S25 via search summary]; Freund-Warnock extend to 26 reversals to 2003 and note wide variation [S25]. A later study finds depreciation matters only under
  flexible rates.
- Deficits financed for years (World Bank, current account % of GDP [S26], own tally): the US ran a deficit in 48 of 56 years 1970-2025, every year 1992-2025 (34 in a
  row), worst -5.8% in 2006, -3.8% in 2025; Australia a deficit in 34 of 37 years 1989-2025, every year 1989-2019 (31 in a row), worst -7.6% in 2007, surpluses 2020-22
  (+1.6, +2.4, +0.3).
- Global dollar share of FX turnover and reserves are in section 8; the US deficit is financed by the reserve-currency role, not by a depreciation trend (US REER drift
  +0.6% a year 1994-2025, S6 own calc).

## 6. Risk premia, safe havens, the dollar cycle

- Ranaldo-Soderlind 2010 (Rev. Finance 14(3): 385-407), intraday 1993-2008: CHF and JPY appreciate against the US$ when US stocks fall, US bond prices rise and FX
  volatility rises; the franc also gains against other currencies, sterling loses; effects strongest in the crisis and nonlinear in the volatility factor [S27 via search
  summaries].
- Safe havens in 2008 (BIS broad NEER, Jul 2008 to Mar 2009, own calc S6): yen +23%, US$ +17.9% (75.1 to 88.5; Jul to Nov +15%), Swiss franc +5% (Jul to Nov +2%; the
  franc was already strong and the SNB acted); losers: Korea -24%, Australia -23%, Mexico -26%, Brazil -22%, New Zealand -18%, Britain -16%, Canada -15%, Sweden -15%,
  Turkey -15%, Indonesia -15%, South Africa -12%, India -5%.
- March 2020 (BIS broad NEER, Feb to Apr 2020): US$ +6.0% (99.0 to 105.0; Feb to Mar +3.6%), yen +4.7%, franc +3.0%; Mexico -21%, South Africa -17%, Brazil -16%, Russia
  -13%, Indonesia -11%, Norway -9%, Turkey -9%, Australia -4%, Canada -4%. MXN against the US$: 18.6 (14 Feb) to 24.3 (7 Apr): -23%, back to 19.9 by 31 Dec [S8 own].
- Global dollar cycle (BIS US effective rate, narrow basket of 26-27 economies, monthly from 1964, own calc S6; swings of at least 12% marked at their extremes):

| phase | nominal | real | length |
|---|---|---|---|
| Sep 1969 to Oct 1978 fall | 125.8 to 99.4, -21% | Jul 1964 to Oct 1978: -34% | 9 y |
| Oct 1978 to Mar 1985 rise | 99.4 to 150.8, +52% | 78.0 to 116.2, +49% | 6.4 y |
| Mar 1985 to 1992/95 fall | to Apr 1995 87.3, -42% | to Aug 1992 74.1, -36% | 7.4 y real, 10.1 y nominal |
| 1992/95 to Feb 2002 rise | 87.3 to 122.5, +40% | 74.1 to 98.9, +33% | 6.8-9.5 y |
| Feb 2002 to 2008 fall | to Mar 2008 79.6, -35% | to Apr 2008 74.0, -25% | 6.1 y |
| 2008-03 to 2009-03 flight | +20% | +20% | 1 y |
| 2009-03 to 2011-07/08 fall | -18% | -17% | 2.3 y |
| Aug 2011 to Oct 2022 rise | 78.2 to 114.8, +47% | 73.5 to 111.9, +52% | 11.2 y |

- Broad index (64 economies, from 1994): nominal 102.8 (Feb 2002) to 75.1 (Jul 2008) -27%; 74.6 (Jul 2011) to 110.5 (Jan 2025) +48% in 13.5 years; after
January 2025 it fell about 6% by June 2025 (103.7) and was 101.7 in Aug 2026. So the dollar's long swings are 35-50% over 6-11 years, with a 1-2 year
flight spike of 15-20% inside them.
- Real and nominal swings of the dollar differ by only a few points (table above): the cycles are real swings, not inflation differentials.

## 7. Regimes, pegs, crises

- IMF AREAER classification (10 de facto categories, effective Feb 2009, three groups plus a residual): hard pegs (no separate legal tender, currency board); soft pegs
  (conventional peg, stabilized arrangement, crawling peg, crawl-like, peg within horizontal bands); floating (floating, free floating); residual (other managed) [S30,
  Table 1].
- Shares of IMF members as of 30 April 2021 (193 reporting entities) [S30, Table 3 and text]: hard pegs 25 = 13.0% (no separate legal tender 14 = 7.3%, currency board 11
  = 5.7%); soft pegs 92 = 47.7% (conventional peg 40 = 20.7%, stabilized 24 = 12.4%, crawling peg 3 = 1.6%, crawl-like 24 = 12.4%, band 1 = 0.5%); floating 64 = 33.2%
  (floating 32 = 16.6%, free floating 32 = 16.6%); other managed 12 = 6.2%. In 2013 the shares were hard 13.1%, soft 42.9%, floating 34.0%, other 9.9%. The
  conventional-peg share of soft pegs fell from 58% (April 2016) to 43.5%. The 2022 and 2023 reports exist (the latter published 19 December 2024) but the IMF site
  refuses scripted download, so newer counts are missing. Hard-peg membership "changes are rare" [S30].
- Who is where, AREAER 2021 (Table 4) [S30]. Free floating (32): Australia, Canada, Chile (from June 2020), Czech Republic, Japan, Mexico, Norway, Poland, Russia, Sweden,
  United Kingdom, United States, Somalia and the 19 euro-area members (the IMF has classified all euro-area members as floating since 2007 [S34]). Floating (32) includes
  Brazil, Colombia, Hungary, Iceland, India, Indonesia, Israel, Korea, New Zealand, Peru, South Africa, Thailand, Turkey, Ukraine, Uruguay. Crawl-like: China (composite
  anchor), Switzerland (euro anchor, reclassified from floating in 2020), Argentina, Vietnam; the only band peg is Morocco; stabilized: Singapore (anchored to its NEER),
  Cambodia, Croatia, Maldives, Trinidad and Tobago. Anchors of the 80 members with an exchange rate anchor: US dollar 37, euro 26, composite 8, other single currency 9;
  the rest target money (25), inflation (45) or nothing (43).
- Floating in name only: Ilzetzki-Reinhart-Rogoff (194 countries 1946-2016) say that, by their own classification, less flexible arrangements are about 80% of countries
  or one half of world GDP, because the IMF's euro-area-as-floater and similar choices overstate flexibility; the dollar is anchor or reference for 62% of 195 countries
  (2016); the share of countries with limited flexibility is about what it was under Bretton Woods [S34]. Calvo-Reinhart's "fear of floating" is in section 2 [S33].
- Currency boards, in the AREAER 2021 list [S30]: Hong Kong, Djibouti, Bosnia, Bulgaria, Brunei (to the S$), the six ECCU members. Hong Kong: Fed H.10 shows HK$ 8.70 on 3
  Oct 1983 and 7.80 by 31 Oct 1983 (the peg date, 17 Oct 1983, is from memory, unverified); range 7.7155-8.05 until 1998, 7.7085-7.8095 to May 2005 [S9, own calc]. On 18
  May 2005 the HKMA added a strong-side convertibility undertaking at 7.75 and moved the weak side from 7.80 to 7.85 (phased 7.81 on 23 May to 7.85 on 20 June 2005)
  [S38]. ECB-cross US$ in HK$ (own calc, S8) touched 7.7499 on 2 May 2025 and 7.8500 on 1 Jul 2025. Macau: pataca at HK$1 = MOP 1.03 (set 1983) through a
  currency-board-like AMCM rule (Wikipedia mirrors, S48).
- Argentina convertibility, 1 April 1991 at 1 peso = US$1 (CRS) [S45]: 6 Jan 2002 end announced, 1.4 per US$ for "major" trades; 11 Jan 1.7; 15 Jan as low as 2.05 [S45].
  World Bank average rate 3.063 in 2002 (-67% in value); GDP growth 1998 +3.9%, 1999 -3.4, 2000 -0.8, 2001 -4.4, 2002 -10.9, 2003 +8.8 [S26]; open unemployment 18% (Dec
  2001) [S45].
- Crash table (own calc from S8, S9, S26 unless marked):

| episode | pre | post | value move | speed |
|---|---|---|---|---|
| Sterling, ERM, 1992 [S9] | DM 2.7833/GBP 15 Sep; US$ 1.996 on 1 Sep | DM 2.5168 30 Sep; US$ 1.513 on 31 Dec | -9.6% vs DM in 2 weeks; -24.2% vs US$ in 4 months | left ERM 16 Sep |
| Italian lira, 1992-93 [S9] | ITL 764.7/DM 1 Sep | 833.6 on 17 Sep; 988.8 on 31 Mar 1993 | -8.3% in 2.5 weeks; -22.7% in 7 months | |
| Swedish krona, Nov 1992 [S9] | SEK 3.766/DM 13 Nov | 4.153 on 19 Nov; 4.371 31 Dec | -9.3% in a week; -13.8% by year-end | floated 19 Nov (date from memory) |
| Mexican peso, Dec 1994 [S9] | 3.47 on 19 Dec | 4.85 on 22 Dec; 7.60 on 9 Mar 1995 | -28.5% in 3 days; -54.3% in 11 weeks | GDP 1995 -5.9% |
| Thai baht, 1997 [S9] | 24.52 on 1 Jul | 30.18 on 2 Jul; 54.3 on 13 Jan 1998 | -18.8% in a day; -54.8% in 6.5 months | GDP 1998 -7.6% |
| Malaysian ringgit, 1997-98 [S9] | 2.5245 on 30 Jun 1997 | 4.23 on 30 Jan 1998 | -40.3% in 7 months | re-pegged 3.80 |
| Korean won, 1997 [S9] | 970 on 31 Oct 1997 | 1,710 on 22 Dec | -43.3% in 7 weeks | GDP 1998 -4.9% |
| Indonesian rupiah, 1997-98 [S26] | avg 2,342 (1996) | avg 10,014 (1998) | -76.6% in two years | GDP 1998 -13.1% |
| Russian ruble, 1998 [S26, S46] | 6.29 on 14 Aug | avg 24.6 in 1999 | "over two thirds in three weeks" [S46] | GDP 1998 -5.3% |
| Brazilian real, 1999 [S9] | 1.2108 on 12 Jan | 2.07 on 29 Jan; 2.20 on 3 Mar | -41.5% in 2.5 weeks; -45% in 7 weeks | GDP 1999 +0.5% |
| Turkish lira, Aug 2018 [S8] | 4.88 on 31 Jul | 6.90 on 13 Aug | -29.2% in 9 trading days | |
| Turkish lira, Dec 2021 [S8] | 13.14 on 30 Nov | 17.78 on 20 Dec | -26.1% in 3 weeks | 42.96 end-2025, 49.6 Oct 2026 |
| Russian ruble, 2014 [S8] | 35.5 on 31 Jul | 73.0 on 16 Dec | -51% in 4.5 months | |
| Swiss franc, 15 Jan 2015 [S8] | EURCHF 1.2010 on 14 Jan | 1.0280 on 15 Jan (ECB 14:15) | franc +16.8% vs EUR in a day | floor gone |

- Intraday EUR/CHF touched about 0.85 on 15 Jan 2015 (unverified, from memory).
- Frankel-Rose 1996 define a currency crash as a nominal depreciation of at least 25% that is also at least 10 points above the previous year's [S37] (a usable trigger
  for the model's crisis events).
- How long pegs last. Klein-Shambaugh (125 countries 1973-2004, non-euro; spells of continuous de facto pegs) [S31]: median peg spell 2 years, mean 4.67 years (floats:
  median 2, mean 5.21); 56% of peg spells survive two years, 30% survive five; 82% of the pegs in a given year are still pegs next year; 65.75% last three more years,
  55.29% five more; the hazard falls with age (a peg that has lasted is likely to last) and about 44% of peg spells last only one year; broken pegs often re-form
  ("flipping"). Husain-Mody-Rogoff 2005, with very small countries and smoothed regimes, report an average peg spell of 28 years [S31 note 44]. Obstfeld-Rogoff 1995 ("The
  Mirage of Fixed Exchange Rates", J. Econ. Perspectives 9(4): 73-96): industrial-country pegs are hard to defend as capital markets grow because the cost depends on
  belief, not on reserves; they list 27 small countries with long-lasting pegs [S32 abstract; S31 note 4 for the 27].
- Black Wednesday, 16 Sep 1992: the Bank of England raised its rate from 10% to 12% and announced 15% the same day, then left the ERM that evening; estimates of reserves
  spent: GBP 3.4 billion, or 40% of reserves (secondary, search summaries, S47) (unverified). Sweden's overnight rate reached 500% in Sept 1992 (unverified).
- Crisis generations in a sentence each. First generation, Krugman 1979 (J. Money, Credit, Banking 11(3): 311-325): a fixed rate plus a persistent money-financed deficit
  drains reserves until a speculative attack at a predictable date ends the peg. Second generation, Obstfeld 1994 ("The Logic of Currency Crises", Cahiers Economiques et
  Monetaires 43: 189-213): when the government weighs the cost of defending the peg against other goals, expectations of abandonment can themselves trigger the exit;
  multiple equilibria; a peg sound enough to last can fall if markets expect it to [S36].
- Impossible trinity: a country can hold only two of a fixed exchange rate, free capital mobility and monetary independence (Mundell 1963 / Fleming 1962, from memory,
  unverified). Empirical support, 130 years: pegs with open capital markets have the highest pass-through from base-country rates (0.43 versus 0.26 for floats, section 4)
  and capital controls break the link ("largely borne out by history") [S35].

## 8. Reserves and intervention

- Reserve adequacy rules: (a) three months of imports (a rule of thumb; reported by the Hungarian central bank as losing favour to (b)); (b) Greenspan-Guidotti: reserves
  at least equal short-term external debt by residual maturity (ratio 1), stated by Guidotti at a G-33 seminar in 1999 and popularised by Greenspan [S44, secondary]; (c)
  the IMF ARA metric: reserves 100-150% of a weighted sum of exports, broad money, short-term debt and other portfolio liabilities, weights floating 5% exports + 5% money
  + 30% short-term debt + 15% other liabilities; fixed 10% + 10% + 30% + 20% (the 2016 Guidance Note; weights seen in search summaries of its table and in IMF staff
  reports, with a note that broad money is cut to 2.5% where capital-flow measures exist) [S43]. A 2026 IMF staff report gives a country at 102.9% of the metric at
  end-2025 [S43 search summary].
- Does sterilised intervention work? Fratzscher-Gloede-Menkhoff-Sarno-Stohr (33 countries, daily, 1995-2011) [S40]: a success rate "in excess of 80 percent under some
  criteria": smoothing the path works in about 80% of cases whatever the regime; stabilising narrow-band rates at least 80%; moving the level in floating regimes only
  about 60%. More effective when large, in the direction of the prior trend, towards long-run fundamentals, and with public communication. Incidence: 19.1% of trading
  days overall, 8.7% in floating and broad bands, 34% in narrow bands; 76.1% of intervention days are purchases; average daily net volume US$ 44.3 million (122 million
  buying, 103 million selling in large advanced economies, 30 million in emerging); relative to GDP 0.02% of GDP (floats), 0.03% (broad band), 0.05% (narrow).
- Japan [S41, own calc on S8]: 2022: 22 Sep JPY 2.84 trillion; 21 Oct JPY 5.62 trillion in one day; October JPY 6.35 trillion; the two total about JPY 9.2 trillion.
  USD/JPY (ECB 14:15) moved 144.0 to 140.8 on 22 Sep (-2.2%), 151.7 to 149.0 after 21 Oct (-1.8%), but sat at 148-150 for another three weeks and fell to 140.4 only by 14
  Nov after US data. 2024: 29 Apr JPY 5.92 trillion, 1 May 3.87 trillion (total April-June 9.79 trillion); 11-12 Jul 3.17 + 2.37 = 5.53 trillion; calendar year about 15.3
  trillion. USD/JPY 157.0 (30 Apr) to 153.2 (3 May), -2.4%; back at 160.6 on 28 Jun; 161.6 (11 Jul) to 158.7 (12 Jul), -1.8%; 150.3 on 31 Jul. Net: single rounds of JPY
  3-10 trillion (about US$ 20-60 billion) bought a 2-2.5% move for days to weeks, fading when the interest differential or US data did not turn; the move per trillion
  fell with size.
- SNB, minimum rate EUR/CHF 1.20 (6 Sep 2011 to 15 Jan 2015) [S39, S8]: ECB quoted the SNB as "no longer tolerate a EUR/CHF exchange rate below the minimum rate of CHF
  1.20" (the ECB's press release of 6 Sep 2011). The ECB-fixed rate jumped 1.1111 (5 Sep) to 1.2036 (6 Sep): +8.3% in a day, and was 1.2072 on the last day of 2012 and
  1.2024 on the last day of 2014. SNB data portal [S39, own calc]: foreign currency investments CHF 188.7 billion (Jul 2011), 281.0 (Aug 2011, +49% in one month), 432.2 (Dec
  2012), 443.3 (Dec 2013), 510.1 (Dec 2014), 593.2 (Dec 2015), 696.1 (Dec 2016), 790.1 (Dec 2017); highest month-end 977.0 (Jan 2022); 677.4 (Dec 2023); 813.1 (Aug 2026).
  Total assets CHF 249.6 billion (Jul 2011), 561.2 (Dec 2014), 640.2 (Dec 2015), 1,056.8 (Dec 2021), highest 1,069.3 (May 2022). The 15 Jan 2015 SNB statement: floor
  discontinued and the sight deposit rate cut to -0.75% [S39 headline]; the franc +16.8% vs EUR that day (section 7 table).
- China: WB total reserves incl. gold [S26]: US$ 2,914 billion (2010), 3,388 (2012), 3,880 (2013), 3,900 (2014), 3,405 (2015), 3,098 (2016), 3,236 (2017), 3,168 (2018),
  3,749 (2025): -US$ 802 billion (-20.6%) from 2014 to 2016. CNY per US$ (ECB crosses, S8): 6.21 (10 Aug 2015), 6.39 (12 Aug 2015: -2.8% in two days after the fixing
  change, date from memory), 6.49 (end-2015), 6.94 (end-2016: -10.6% in 16 months). Earlier peg: 8.28 until 21 Jul 2005, 6.84 mid-2008, 6.48 in June 2011 [S9]. Reserve
  peak of US$ 3.99 trillion in June 2014 (unverified, from memory).
- MAS, Singapore: policy is a managed S$ nominal effective exchange rate: "MAS undertakes foreign exchange operations to keep the Singapore dollar nominal effective
  exchange rate within a policy band" [S42]. Three parameters are set at the statements (twice a year to 2023, then quarterly: Jan, Apr, Jul, Oct; plus off-cycle ones):
  slope (rate of appreciation), width, centre; levels are not disclosed. Decisions [S42]: 2018-04 slope increase slightly; 2018-10 increase slightly; 2019-10 reduce
  slightly; 2020-03 slope set at 0% and re-centre downwards; 2021-10 increase slightly; 2022-01 (off-cycle) increase slightly; 2022-04 increase slightly and re-centre
  upwards; 2022-07 (off-cycle) and 2022-10 re-centre upwards; 2023 none; 2024 none; 2025-01 and 2025-04 reduce slightly; 2025-07 to 2026-01 unchanged; 2026-04 increase
  slightly; 2026-07 increase very slightly; 2026-10-14 unchanged. The width has never changed in the table. BIS broad S$ NEER 1994-2025 (own calc, S6): trend +1.4% a
  year, annualised sd 2.1% (median economy 4.4%), after 2000 residual sd about a linear trend 2.7%, never more than 5.7% away; the MAS basket differs. The band is thought
  to be about +-2% wide (unverified, analyst lore).
- Dollar dominance (for the eight-currency vault): BIS Triennial April 2025 [S28]: turnover US$ 9.6 trillion a day (7.5 in 2022, +28%); shares of the 200% total: USD
  89.2%, EUR 28.9%, JPY 16.8%, GBP 10.2%, CNY 8.5%, CHF 6.4%, AUD about 6%, CAD about 6%; USD/CNY 8.1% of pair turnover, USD/CHF 4.9%, USD/HKD 3.6%; the top ten pairs all
  include the dollar. IMF COFER 2026Q2 [S29]: total reserves US$ 13.22 trillion; USD 56.70% (57.18% in 2026Q1), EUR 20.60%, JPY 4.96%, CNY 2.11%; the page gives only
  quarter changes for GBP (+0.16 pp), AUD (+0.13), CAD (-0.01), CHF (+0.03); "other" is above 6%.

## 9. What a city-sized economy would feel; what small states do

- The hard pegs are almost all small. The 14 members with no separate legal tender (AREAER 2021, Table 4 [S30]): Ecuador, El Salvador, Marshall Islands, Micronesia,
  Palau, Panama, Timor-Leste (US dollar); Andorra, Kosovo, Montenegro, San Marino (euro); Kiribati, Nauru, Tuvalu (Australian dollar). The 11 currency boards: Hong Kong,
  Djibouti, Brunei, the six ECCU members, Bosnia, Bulgaria. The free-floating list is the big and rich (US, Japan, UK, euro area, Canada, Australia, Norway, Sweden) plus a
  few emerging economies (Mexico, Poland, Czech Republic, Russia, Chile). Singapore is the one rich city-state with its own managed currency (stabilized arrangement,
  anchored to its NEER; S30, S42). Luxembourg, Malta and Cyprus use the euro (free-floating group, S30). My reading of the lists: micro-states use another country's money
  or a board, and mid-sized open economies mostly manage (Calvo-Reinhart, section 2).
- Not IMF members and so absent from the AREAER matrix (absence in my read of the 2021 list): Monaco (euro under an EU monetary agreement negotiated by France,
  eumonitor), Liechtenstein (Swiss franc since 1920, formalised 1924 (elordenmundial); coin-issue right since 1980), Macau (HKD-linked, section 7), the Vatican (euro).
  Luxembourg: franc at par with the Belgian franc since the 1920s union (BCL timeline: convention 1921, parity 1929; sources disagree), euro from 1999 [search summaries,
  unverified in detail].
- Panama: classified "no separate legal tender", the US dollar being its legal tender [S30]; dollarised since 1904 (unverified, from memory; the balboa exists only as a
  par-valued unit and coins).
- Obstfeld-Rogoff counted 27 small countries with long-lasting pegs; very small ones (Panama and others) have far longer spells, which is why Husain et al. get a 28-year
  mean spell while the 125-country panel with a 400,000-population cut gets 4.7 [S31]. Klein-Shambaugh keep only 8 of the 27 once population below 400,000 and currency
  unions are dropped; those still pegging: Bahrain, Djibouti, Lesotho, Namibia, Oman, Qatar, Swaziland, UAE (plus Luxembourg) [S31 note 4].
- Why they peg (theory from Frankel 1999 and Alesina-Barro 2002, not read; one search found that small size and low export diversification are the strongest correlates of
  pegging; openness is a weaker and contested predictor) (unverified).
- Interest rates under a peg: Hong Kong's HK$ 7.75-7.85 band, Macau's 1.03 and Panama's dollar all import the anchor's interest rates; Obstfeld-Shambaugh-Taylor find pegs
  with open capital accounts have the highest pass-through of base-country rates (annual coefficient 0.43 for pegs, 0.26 for non-pegs; with open capital markets 0.61 (R2
  0.30) for pegs against 0.53 (R2 0.06) for floats; 0.56 without capital controls against 0.26 with them) [S35].

## 10. Measures: REER, constant-dollar GDP

- BIS effective exchange rates [S6, S7, Klau-Fung 2006]: broad basket 64 economies (monthly from 1994, daily from 1996), narrow 26 (nominal)/27 (real) economies (monthly
  from 1964); base 2020 = 100. NEER is a geometric trade-weighted average of bilateral rates; REER is the NEER adjusted by relative consumer prices (CPI). Weights come
  from manufacturing trade flows (SITC 5-8) with "double-weighting" for third-market competition (Turner-Van't dack 1993) and are time-varying on a three-year average
  (the 2009 index uses 2008-10 weights; pre-1990 uses the 1990-92 set).
- Formulae. Let E_ij,t be units of partner j's currency per unit of country i's (a rise is an i appreciation), P the CPI index, w_ij the weight of partner j in i's basket
  (sum over j of w_ij = 1), base period 0. The BIS description (geometric weighted average; REER = NEER adjusted by relative consumer prices [S7]) gives NEER_i,t = 100 x
  PRODUCT_j ( E_ij,t / E_ij,0 )^w_ij,  i.e. ln NEER = SUM_j w_ij x ln(E_ij,t / E_ij,0) + const; REER_i,t = 100 x PRODUCT_j [ (E_ij,t / E_ij,0) x (P_i,t / P_i,0) / (P_j,t
  / P_j,0) ]^w_ij,  i.e. ln REER = ln NEER + SUM_j w_ij x ln(relative CPI index): a home inflation above partners' raises the REER at a given NEER (the exponent and sign
  convention are mine; the BIS text gives words, not this formula). Weight (Turner-Van't dack scheme in the BIS box [S7]): w_ij = [m_i/(x_i+m_i)] x w_ij^M +
  [x_i/(x_i+m_i)] x w_ij^X, where m_i, x_i are i's manufactured imports and exports, w_ij^M = m_ij/m_i is j's simple share of i's imports, and w_ij^X combines direct
  export competition (i's export share to j times j's import openness) and third-market competition (i's and j's exports meeting in other markets). A level of 120 means a
  20% appreciation versus 2020, "not over- or undervaluation" [S6].
- World Bank "GDP (constant 2015 US$)", NY.GDP.MKTP.KD [S26]: GDP at constant 2015 prices in local currency converted to US$ at the 2015 official exchange rate:
  GDPK_t(US$) = GDP_t(LCU, constant 2015 prices) / E_2015 (LCU per US$) (the conversion rule is from memory of the long definition; the API's own note says only "constant
  prices ... reference year 2015 ... expressed in US dollars"; where the official rate misleads the WB uses an alternative factor) (unverified). Consequence: the series
  moves only with real local growth (one fixed conversion rate for all years), so year-to-year exchange rate swings do not enter; it compares volumes across countries at
  2015 market rates, not price levels (a PPP-based series would use PPP conversion factors instead).

## 11. Calibration cheat-sheet for a monthly model

1. Float volatility: G10 monthly sd 2.4-3.6% (8-12% a year); emerging floats 3-5% (11-18%); Asian managed 1-2%; a currency board 0.2% a month [S8].
2. Monthly changes are uncorrelated (AC1 -0.06 to +0.13), tails are fat (kurtosis 3-9, up to 18) and volatility clusters [S8].
3. Real-rate half-life: 3-5 years as the linear consensus [S1]; 1 year or less for disaggregated data or large gaps [S2, S3]; in the Big Mac data gaps over 40% close
   about two thirds a year, gaps under 30% about a quarter [S5].
4. How far real rates stray: sd of log REER about its mean 10% (p90 23%); Big Mac PPP gaps of -60% to +45% [S6, S5].
5. Nominal-real link: monthly correlation 0.90, slope 0.97 [S6]; the nominal rate carries the inflation differential (Turkey -24% a year nominal, real flat).
6. Interest differentials: short-horizon UIP slope about -0.8, long-yield slopes 0.4-0.9 (unity not rejected); carry mean 3-4% a year, Sharpe 0.5-0.9, worst month -5% to
   -9% [S14, S17].
7. Carry crash: -13% in four days (USD/JPY 1998), -38% in 3.4 months (AUD 2008), -12% in 5 weeks (yen 2024) [S9, S8].
8. Policy shock: an unexpected +25bp gives +0.35% on the day; the full response peaks after one to three years in the delayed-overshooting literature [S20, S18].
9. Dollar cycle: 35-50% swings over 6-11 years; flight-to-safety spikes +18% (2008), +6% (2020) in the broad index [S6].
10. Crashes: -40% to -55% in 2-6 months for broken EM pegs and baskets, GDP -5% to -13% the year after [S9, S26]; sudden stops are an emerging-market event [S24].
11. Pegs: median spell 2 years, mean 4.7, 30% survive five; shares of members 13% hard, 48% soft, 33% floating, 6% other [S31, S30].
12. Intervention: smoothing works in about 80% of episodes, moving the level in about 60%; Japanese rounds of JPY 3-10 trillion moved the yen 2-2.5% for days to weeks;
    the SNB's floor took its balance sheet from CHF 250 to 640 billion in 4.5 years; China's reserves fell 21% in two years [S40, S41, S39, S26].
13. Reserve rules: ARA 100-150% of the weighted metric, Greenspan-Guidotti 100% of short-term debt, three months of imports [S43, S44].

## 12. Gaps, doubts, not verified

- Could not retrieve: AREAER 2022/2023 tables (IMF blocks scripts); Taylor-Peel-Sarno half-life table; Chen-Rogoff elasticities (the JIE paper and IMF WP 02/27 are behind
  blocks; only abstract-level statements); Obstfeld-Rogoff 1995 text (OCR garbled); Meese-Rogoff 1983 itself; Ranaldo-Soderlind magnitudes; E-E 1995 original impulse
  responses; FRED (timeouts).
- From memory or search summaries only, not traced to the page: Mundell-Fleming attribution; intraday EUR/CHF 0.85 on 15 Jan 2015; China reserves peak US$ 3.99 trillion
  (June 2014) and the 11 Aug 2015 fixing change; Panama dollarised since 1904; HK peg date 17 Oct 1983; Sweden floating 19 Nov 1992 and the 500% overnight rate;
  Scholl-Uhlig and Kim-Roubini early-peak results; the +-2% S$ band width; the ARA weight table (seen only in search summaries of the Guidance Note and IMF staff
  reports); the World Bank conversion rule for constant US$; Frankel 1999 and Alesina-Barro on country size; the Bank of Japan's 31 July 2024 rate rise; Black Wednesday
  rates and reserve figures; Macau's 1.03 rate; Luxembourg/Liechtenstein/Monaco histories; page numbers of journal articles not shown on a fetched page.
- The statement that "about four fifths of IMF members float or hard-peg" that one IMF handbook summary suggested is NOT used: the AREAER 2021 tables give 46% (hard 13% +
  floating 33%); the 80% figure belongs to Ilzetzki-Reinhart-Rogoff's different classification.
- Big Mac: the Economist notebook (not read) defines the GDP-adjusted index; the slope of log Big Mac US$ price on log GDP per person in the adjusted file is 0.11-0.15
  (own calc, indicative only, the column's definition is ambiguous).
- Own-calc caveats: ECB 14:15 CET fixes, Fed noon NY buying rates and BIS monthly averages differ from intraday extremes; month-end log changes; pairs crossed through the
  euro (ECB data publish no direct US$/X quotes except via EUR); the BIS REER sample starts in 1994 and is short for half-life estimation.

## Sources

S1. Rogoff, K. (1996), "The Purchasing Power Parity Puzzle", Journal of Economic Literature 34(2): 647-668. https://web.williams.edu/Economics/macro/rogoff.pdf (read in full; page refs approximate).
S2. Imbs, J., Mumtaz, H., Ravn, M., Rey, H. (2005), "PPP Strikes Back: Aggregation and the Real Exchange Rate", Quarterly Journal of Economics 120(1): 1-43. https://igier.unibocconi.eu/sites/default/files/media/attach/091104.pdf ; https://www.imf.org/en/Publications/WP/Issues/2016/12/30/PPP-Strikes-Back-Aggregation-and-the-Real-Exchange-Rate-16389
S3. Taylor, M.P., Peel, D.A., Sarno, L. (2001), "Nonlinear Mean-Reversion in Real Exchange Rates: Toward a Solution to the Purchasing Power Parity Puzzles", International Economic Review 42(4): 1015-1042 (CEPR DP 2658). https://wrap.warwick.ac.uk/1687/
S4. Lopez, C., Murray, C.J., Papell, D.H. (2009), "Median-Unbiased Estimation in DF-GLS Regressions and the PPP Puzzle", working paper, 29 July 2009. https://www.uh.edu/~dpapell/Median%20Unbiased.pdf
S5. The Economist, Big Mac index data and README. https://github.com/TheEconomist/big-mac-data (files big-mac-raw-index.csv, big-mac-adjusted-index.csv, editions Apr 2000 to Jul 2026).
S6. BIS, Effective exchange rate indices (broad 64 / narrow 26-27 economies; data portal and SDMX API). https://data.bis.org/topics/EER ; data pulled from https://stats.bis.org/api/v2/data/dataflow/BIS/WS_EER/1.0/M.R.B.?format=csv (R/N, B/N variants).
S7. Klau, M., Fung, S.S. (2006), "The new BIS effective exchange rate indices", BIS Quarterly Review, March 2006, pp. 51-65. https://www.bis.org/publications/qr-200603.pdf
S8. ECB euro foreign exchange reference rates, daily, 1999-2026. https://data-api.ecb.europa.eu/service/data/EXR/D.USD.EUR.SP00.A (27 currencies; cross rates through the euro are my own).
S9. Federal Reserve Board, H.10 historical foreign exchange rates (noon buying rates, New York), country files dat89/dat96/dat00. https://www.federalreserve.gov/releases/h10/hist/
S10. Mussa, M. (1986), "Nominal Exchange Rate Regimes and the Behavior of Real Exchange Rates: Evidence and Implications", Carnegie-Rochester Conference Series on Public Policy 25: 117-214 (cited in S1; not read).
S11. Cheung, Y.-W., Chinn, M.D., Garcia Pascual, A. (2005), "Empirical exchange rate models of the nineties: Are any fit to survive?", J. Int. Money and Finance 24(7); NBER WP 9393. https://www.nber.org/papers/w9393 ; Meese, R., Rogoff, K. (1983), J. Int. Economics 14(1): 3-24 (via S11 and S12).
S12. Rossi, B. (2013), "Exchange Rate Predictability", Journal of Economic Literature 51(4): 1063-1119. https://repositori.upf.edu/handle/10230/20816 (abstract).
S13. Fama, E.F. (1984), "Forward and spot exchange rates", Journal of Monetary Economics 14(3): 319-338 (referred to through S14-S16).
S14. Chinn, M.D., Meredith, G. (2004), "Monetary Policy and Long-Horizon Uncovered Interest Parity", IMF Staff Papers 51(3): 409-430; NBER WP 11077. https://www.nber.org/papers/w11077
S15. Bussiere, M., Chinn, M., Ferrara, L., Heipertz, J. (2018), "The New Fama Puzzle", NBER WP 24342. https://www.nber.org/papers/w24342
S16. Brunnermeier, M.K., Nagel, S., Pedersen, L.H. (2009), "Carry Trades and Currency Crashes", NBER Macroeconomics Annual 2008, vol. 23; NBER WP 14473. https://www.nber.org/papers/w14473
S17. Burnside, C., Eichenbaum, M., Kleshchelski, I., Rebelo, S. (2011), "Do Peso Problems Explain the Returns to the Carry Trade?", Review of Financial Studies 24(3): 853-891; NBER WP 14054. https://www.nber.org/papers/w14054
S18. Faust, J., Rogers, J.H. (2003), "Monetary policy's role in exchange rate behavior", J. Monetary Economics 50(7): 1403-1424 (IFDP 652, 1999). https://federalreserve.gov/Pubs/IFDP/1999/652/ifdp652.pdf ; Eichenbaum, M., Evans, C.L. (1995), Q. J. Economics 110(4): 975-1009 (reported in it).
S19. Dornbusch, R. (1976), "Expectations and Exchange Rate Dynamics", Journal of Political Economy 84(6): 1161-1176. https://users.ssc.wisc.edu/~cengel/IMF/DornbuschJPE1976.pdf (not read).
S20. Kearns, J., Manners, P. (2006), "The Impact of Monetary Policy on the Exchange Rate: A Study Using Intraday Data", Int. J. Central Banking 2(4); RBA RDP 2005-02. https://www.rba.gov.au/publications/rdp/2005/2005-02.html (via search summary).
S21. Chen, Y.-c., Rogoff, K. (2003), "Commodity currencies", J. International Economics 60(1): 133-160 (IMF WP 02/27); slides "Are the Commodity Currencies an Exception to the Rule?", Bank of Canada, July 2006. https://www.bankofcanada.ca/wp-content/uploads/2010/08/chen_pre.pdf
S22. Cashin, P., Cespedes, L.F., Sahay, R. (2004), "Commodity currencies and the real exchange rate", J. Development Economics 75(1): 239-268. https://www.bcentral.cl/contenido/-/detalle/documento-de-trabajo-n-236 (abstract).
S23. U.S. EIA, Europe Brent spot price FOB, monthly. https://www.eia.gov/dnav/pet/xls/PET_PRI_SPT_S1_M.xls
S24. Calvo, G.A. (1998), "Capital flows and capital-market crises: the simple economics of sudden stops", J. Applied Economics 1(1): 35-54; Calvo, G.A., Izquierdo, A., Mejia, L.-F. (2004), "On the Empirics of Sudden Stops", NBER WP 10520. https://www.nber.org/papers/w10520
S25. Freund, C. (2000), "Current Account Adjustment in Industrialized Countries", Federal Reserve IFDP 692. https://www.federalreserve.gov/Pubs/IFDP/2000/692/default.htm ; Freund, C., Warnock, F. (2005), NBER WP 11823. https://www.nber.org/system/files/working_papers/w11823/w11823.pdf
S26. World Bank, World Development Indicators API: FI.RES.TOTL.CD, BN.CAB.XOKA.GD.ZS, NY.GDP.MKTP.KD.ZG, PA.NUS.FCRF, NY.GDP.MKTP.KD. https://api.worldbank.org/v2/ (queried 2026-10-21; data updated 2026-10-08).
S27. Ranaldo, A., Soderlind, P. (2010), "Safe Haven Currencies", Review of Finance 14(3): 385-407; SNB WP 2007-17. https://www.snb.ch/en/publications/research/working-papers/2007/working_paper_2007_17 (via search summaries).
S28. BIS, Triennial Central Bank Survey, OTC foreign exchange turnover in April 2025. https://www.bis.org/statistics/rpfx25_fx.htm
S29. IMF, COFER data brief, 2026Q2 (published 30 Sep 2026). https://data.imf.org/en/news/imf%20data%20brief%20september%2030
S30. IMF, Annual Report on Exchange Arrangements and Exchange Restrictions 2021 (Overview; 72nd issue, published 2022). Copy read: https://eclass.unipi.gr/modules/document/file.php/DES366/IMF%20EXCHANGE%20RATE%20ARRANGEMENTS%202022.pdf ; landing pages https://www.imf.org/en/publications/annual-report-on-exchange-arrangements-and-exchange-restrictions/issues/2024/12/19/annual-report-on-exchange-arrangements-and-exchange-restrictions-2023-541890
S31. Klein, M.W., Shambaugh, J.C. (2008), "The dynamics of exchange rate regimes: Fixes, floats, and flips", J. International Economics 75(1): 70-92; NBER WP 12729 "The Nature of Exchange Rate Regimes" (2006). https://nber.org/system/files/working_papers/w12729/w12729.pdf
S32. Obstfeld, M., Rogoff, K. (1995), "The Mirage of Fixed Exchange Rates", J. Economic Perspectives 9(4): 73-96; NBER WP 5191. https://www.nber.org/papers/w5191 (abstract only).
S33. Calvo, G.A., Reinhart, C.M. (2002), "Fear of Floating", Quarterly Journal of Economics 117(2): 379-408; NBER WP 7993. https://drum.lib.umd.edu/bitstreams/e3aa581a-a42f-47a9-b6ef-e44313087076/download
S34. Ilzetzki, E., Reinhart, C.M., Rogoff, K.S. (2019), "Exchange Arrangements Entering the Twenty-First Century: Which Anchor Will Hold?", Quarterly Journal of Economics 134(2): 599-646; NBER WP 23134. https://www.nber.org/papers/w23134
S35. Obstfeld, M., Shambaugh, J.C., Taylor, A.M. (2005), "The Trilemma in History", Review of Economics and Statistics 87(3): 423-438; NBER WP 10396. https://www.nber.org/papers/w10396
S36. Krugman, P. (1979), J. Money, Credit and Banking 11(3): 311-325; Obstfeld, M. (1994), "The Logic of Currency Crises", Cahiers Economiques et Monetaires 43: 189-213 (NBER WP 4640, number from memory: https://www.nber.org/papers/w4640). Both known to me from search summaries, not read.
S37. Frankel, J.A., Rose, A.K. (1996), "Currency crashes in emerging markets: An empirical treatment", J. International Economics 41: 351-366. https://www.federalreserve.gov/pubs/ifdp/1996/534/ifdp534.pdf
S38. HKMA, "Refinements to the Operation of the Linked Exchange Rate System", press release 18 May 2005. https://www.hkma.gov.hk/eng/news-and-media/press-releases/2005/05/20050518-4
S39. SNB: 15 Jan 2015 press release https://www.snb.ch/en/publications/communication/press-releases/2015/pre_20150115 ; ECB press release 6 Sep 2011 https://www.ecb.europa.eu/press/pr/date/2011/html/pr110906.en.html ; SNB data portal, balance sheet items https://data.snb.ch/api/cube/snbbipo/data/csv/en
S40. Fratzscher, M., Gloede, O., Menkhoff, L., Sarno, L., Stohr, T. (2019), "When Is Foreign Exchange Intervention Effective? Evidence from 33 Countries", AEJ: Macroeconomics 11(1): 132-156. https://openaccess.city.ac.uk/id/eprint/19099/1/When_is_foreign_exchange_intervention_effective_FINAL.pdf
S41. Japan Ministry of Finance, Foreign exchange intervention operations (quarterly/monthly): https://www.mof.go.jp/english/policy/international_policy/reference/feio/quarter/2024_2Qe.html ; .../2024_3Qe.html ; .../2022_4Qe.html (2022 amounts via search summary and Nippon.com).
S42. Monetary Authority of Singapore, past monetary policy decisions and exchange-rate-centred policy. https://www.mas.gov.sg/monetary-policy/past-monetary-policy-decisions ; https://www.mas.gov.sg/monetary-policy
S43. IMF (2016), Guidance Note on the Assessment of Reserve Adequacy and Related Considerations. https://www.imf.org/external/np/pp/eng/2016/060316.pdf ; IMF (2015) Press release 15/176 https://www.imf.org/external/np/sec/pr/2015/pr15176.htm (weights read in search summaries only).
S44. Guidotti-Greenspan rule (secondary): https://en.wikipedia.org/wiki/Guidotti%E2%80%93Greenspan_rule ; MNB note on the three-months-of-imports rule: https://2015-2019.kormany.hu/download/a/6e/e0000/Hungarian%20Outlook_MNB%20forex%20reserves%20at%20an%20adequate%20level.pdf
S45. Congressional Research Service, "The Argentine Financial Crisis: A Chronology of Events" (RS21130). https://www.everycrsreport.com/reports/RS21130.html ; IMF IEO, "The IMF and Argentina, 1991-2001" https://www.imf.org/external/np/ieo/2004/arg/eng/
S46. Economics Observatory, "Russia's 1998 currency crisis". https://economicsobservatory.com/russias-1998-currency-crisis-what-lessons-for-today (secondary).
S47. Black Wednesday (secondary): https://en.wikipedia.org/wiki/Black_Wednesday ; https://www.econlib.org/wednesday-black-and-white/
S48. Macanese pataca (secondary): https://en.wikipedia.org/wiki/Macanese_pataca
