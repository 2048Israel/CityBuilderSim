# CurrencyRegimeCheck.java - 1,576 lines · 44 methods · 7 constants · harnesses

`ham/citybuildersim/CurrencyRegimeCheck.java` - generated 2026-10-23 by CodeMap; line numbers are as of that run.

> Proves the D$'s four regimes - Float, Lean against falls (the default), Band
> and Peg to the US$ - hold the currency where they say and cost what they
> should (XR2, 2026-10-22): Lean is exactly the float the city had before there
> were regimes, a peg holds to the digit while the vault meets the month's
> balance and gives way at the floor or by doubt at the chance the gap makes
> it, the doubt takes the hot money home, a band's edges hold and its inside is
> free, the dial is tied to the US's rate under a peg and the autopilot's aim
> picks the rule, the currency aim keeps the shadow inside a band, the strong
> edge's dollars are paid exactly by the payer chosen, and all of it is saved.
> Not part of the game.
> 
> WHY THIS EXISTS. Jerus, 2026-10-21 (docs/studies/spec-exchange-rates.md, 3.6;
> his calls star 3 to 6 and 13): the city chooses what its D$ is held to. Star 3,
> four regimes, Lean the default and the city's own history; star 4, a peg
> breaks - when the vault cannot pay the deficit (Krugman's first generation)
> or by doubt (Obstfeld's second) - and the break costs the bank its credibility;
> star 5, under a peg the dial is tied to the US's rate and only a spread is the
> city's; star 6, the dollars the vault takes at the strong edge are paid for by
> a toggle, the central bank in new money or the treasury out of its cash; star
> 13, the autopilot aims at inflation, the currency, or a mix. The study's row
> (section 5, XR2) asks for a harness that counts how often each of these fires
> rather than waiting for a run to show it: this is that harness. The default
> playtest never leaves Lean, so nothing else in the suite would ever notice a
> regime that did nothing.
> 
> What it has to prove, a section each, every one a fixture that causes the
> condition rather than waiting for it:
> 
>   0. LEAN IS TODAY'S FLOAT, TO THE CENT. Two cities on the default founding,
>      one switched to Float and back to Lean before its first month: 60 months,
>      every month's rate, vault, defence, moves, M0, cash and credibility equal
>      to the bit; under Lean the shadow is the rate, the held move is exactly
>      0, nothing is bought at an edge, no doubt, no hot money left on doubt,
>      nothing owed. The months the lean sold are counted (the comparison means
>      nothing if the defence never fired).
>   1. FLOAT SELLS NOTHING. A vault and deficits that make Lean sell, and the
>      Float twin on the same months: its defence is 0 every month, its vault
>      unchanged.
>   2. A PEG HOLDS TO THE DIGIT, AND THE VAULT PAYS THE MONTH'S BALANCE. A
>      settled currency pegged away from its rate, then months of a deficit,
>      then of a surplus: every held month the rate is the peg exactly; the
>      vault falls by exactly the deficit, rises by exactly the surplus (the
>      edge's purchase), and what is owed is that purchase at the month's rate;
>      the seven moves add to the month's log move and the held line holds it.
>   3. A VAULT AT THE FLOOR BREAKS THAT MONTH, AT THE SHADOW PLUS OVERSHOOT.
>      A peg whose deficit would take the vault under VAULT_FLOOR months of
>      imports breaks (cause VAULT, nothing sold), its twin with a vault a
>      little deeper holds; the rate lands at the shadow plus OVERSHOOT of its
>      gap, no overshoot when the shadow is the stronger; the overshoot fades at
>      MARKET_HALF_LIFE; in a city credibility falls by PEG_BREAK_DENT of its
>      distance to KMIN.
>   4. THE CHANCE OF A BREAK, COUNTED OVER 1,000 SEEDED PEGS AGAINST hazard().
>      The breaks counted are within three standard deviations of the hazards
>      summed; the hazard's own shape; the market's path is the same pegged or
>      not.
>   5. THE DOUBT TAKES THE HOT MONEY HOME. A bare CapitalFlows: setDoubt(h)
>      takes exactly h of the stock, setDoubt(0) changes nothing to the bit; in
>      a city under a peg past BREAK_GAP the month's departure is the stock
>      times the share the last reprice struck, and the audit closes. The saved
>      array is 19 long (FX8): slot 15 the doubt's lifetime, 16 to 18 the month's
> ... (46 more lines in the source)

**Uses:** [ForeignAccounts](ForeignAccounts.md) (118), [Game](Game.md) (41), [DebtManager](DebtManager.md) (32), [CapitalFlows](CapitalFlows.md) (29), [CurrencyCheck](CurrencyCheck.md) (11), [Expectations](Expectations.md) (11), [ExchangeRateCheck](ExchangeRateCheck.md) (11), [Founding](Founding.md) (8), [ForeignCheck](ForeignCheck.md) (7), [CentralBank](CentralBank.md) (7), [GameFiles](GameFiles.md) (1), [TreasuryJournal](TreasuryJournal.md) (1)

## Sections

| line | section |
|---:|---|
| 248 | 0. Lean is today's float, to the cent |
| 312 | 1. Float sells nothing |
| 350 | 2. a peg holds to the digit, and the vault pays the month's balance |
| 433 | 3. a vault at the floor breaks that month |
| 617 | 4. the chance of a break, counted over 1,000 seeded pegs |
| 704 | 5. the doubt takes the hot money home |
| 849 | 6. a band's edges hold and its inside is free |
| 936 | 7. under a peg the dial is the US's rate plus the spread; the aim |
| 1049 | 8. the currency aim holds a band, counted over 1,000 seeded years |
| 1093 | 9. the toggle's two payers each pay exactly what the edge bought |
| 1263 | 10. saved and loaded mid-peg |
| 1384 | 11. a real run, counted |
| 1452 | 12. the hot money's month crosses a save (FX8) |
| 1516 | 13. leaving a peg takes its doubt with it (FX8) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 159 | `CurrencyRegimeCheck.FLOAT` | `ForeignAccounts.Regime.FLOAT, LEAN = ForeignAccounts.Regime.LEAN, BAND = Fore...` | The four regimes, by their short names. |
| 163 | `CurrencyRegimeCheck.TRADE` | `3_000, GDP = 6_000` | The trade a quiet currency settles on, each way, and the output it is weighed against: openness one (ExchangeRateCheck's). |
| 166 | `CurrencyRegimeCheck.SEED` | `20_261_022L` | The market's seed in the sections that draw one. |
| 169 | `CurrencyRegimeCheck.DEEP` | `1e9` | A vault no fixture of these can empty or come near the floor of, in dollars. |
| 172 | `CurrencyRegimeCheck.DOLLAR` | `1e-6` | What the harness's own sums are held to where they are exact arithmetic on dollars. |
| 841 | `CurrencyRegimeCheck.SLOT_DOUBT_LIFETIME` | `15, SLOT_ARRIVED = 16, SLOT_DEPARTED = 17, SLOT_DOUBT_DEPARTED = 18, SAVE_SLO...` | Where CapitalFlows.toSaveArray() puts the doubt's lifetime (XR2) and the month's three flows (FX8), and how long the array is. |
| 1096 | `CurrencyRegimeCheck.EDGE_BILL` | `"The band's or the peg's dollars"` | The label the treasury's journal gives the edge's bill (Game.settleTheEdge()). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 121 | `static int fails` |  |
| 122 | `static PrintStream out` |  |
| 123 | `static PrintStream quiet` |  |
| 1455 | `static Game doubtCitySaved` | Section 12's city, kept for section 13: saved to slot 2 of its own scratch folder in the month the hot money left on doubt, then played on. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 119 | 1458 | **type** `public class CurrencyRegimeCheck` | Proves the D$'s four regimes - Float, Lean against falls (the default), Band and Peg to the US$ - hold the currency where they say and cost what they should (XR2, 2026-10-22): Lean is exactly the float the city had be... |
| 125 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 130 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 141 | 9 | `static void same(String label, double actual, double expected)` | Equal to the bit. |
| 152 | 5 | `static void quietly(Runnable work)` | Runs a piece of work with the game's narration sent nowhere. |
| 179 | 8 | `static ForeignAccounts book(double exports, double imports, double vaultUsd, long seed, ForeignAccounts.Regime regime)` | A currency past settling on this trade, its vault at this many dollars (bought at the rate of the day, then forgotten as a set-up and not a deal), its market seeded unless the seed is 0 (quiet), held in this regime (a... |
| 189 | 5 | `static void month(ForeignAccounts fx, double exports, double imports)` | One month of this trade, repriced. |
| 196 | 3 | `static double balanceUsd(ForeignAccounts fx)` | The month's own balance in dollars at the rate it is struck at, a surplus positive: the model's two accounts over the rate. |
| 201 | 16 | `static Game town(String label, Founding founding)` | A trading town on the default founding, as ExchangeRateCheck builds it (its own copy: that one is tied to its harness's streams). |
| 219 | 3 | `static void play(Game g, int months)` | Plays this many months of a town, narration sent nowhere. |
| 223 | 24 | `public static void main(String[] args) throws Exception` |  |

### 0. Lean is today's float, to the cent (lines 248-311)

| line | len | member | says |
|---:|---:|---|---|
| 251 | 6 | `static double[] figures(Game g)` | One city month's figures: the rate, the vault, the defence, the seven moves, M0, the treasury's cash and the bank's credibility. |
| 258 | 4 | `static boolean sameBits(double[] a, double[] b)` |  |
| 264 | 14 | `static boolean leanIsClean(Game g)` | Everything XR2 added reads nothing under Lean. |
| 279 | 32 | `static void leanIsTodaysFloat()` |  |

### 1. Float sells nothing (lines 312-349)

| line | len | member | says |
|---:|---:|---|---|
| 315 | 8 | `static void deficitMonth(ForeignAccounts fx, int m)` | A month of a city that runs a deficit most months and whose parity drifts weaker: the weather that makes the lean sell. |
| 324 | 25 | `static void floatSellsNothing()` |  |

### 2. a peg holds to the digit, and the vault pays the month's balance (lines 350-432)

| line | len | member | says |
|---:|---:|---|---|
| 352 | 80 | `static void aPegHolds()` |  |

### 3. a vault at the floor breaks that month (lines 433-616)

| line | len | member | says |
|---:|---:|---|---|
| 436 | 5 | `static ForeignAccounts quietPeg(double vaultUsd)` | A quiet peg at the rate of the day, a few balanced months old, its vault at this many dollars. |
| 443 | 6 | `static void driftMonth(ForeignAccounts fx, int m)` | Two currencies, one on a peg and one floating, the same seed, trade in balance and a parity drifting weaker 5% a year: the pair a peg breaks beside. |
| 451 | 12 | `static Game doubtTown(String label, long reseed)` | A city on the default founding pegged with a surplus at the peg and the D$ a fifth over it: the doubt fixture, five months from its break at the peg's chance. |
| 464 | 152 | `static void aVaultAtTheFloorBreaks()` |  |

### 4. the chance of a break, counted over 1,000 seeded pegs (lines 617-703)

| line | len | member | says |
|---:|---:|---|---|
| 619 | 84 | `static void theChanceOfABreak()` |  |

### 5. the doubt takes the hot money home (lines 704-848)

| line | len | member | says |
|---:|---:|---|---|
| 707 | 4 | `static void hotMonth(CapitalFlows f, int m)` | One month of hot money at a 9% rate against the world's 2% and a point of risk: the standing fixture of CapitalFlowCheck. |
| 712 | 5 | `static CapitalFlows stocked(int months)` |  |
| 718 | 121 | `static void theDoubtTakesTheHotMoneyHome()` |  |
| 843 | 5 | `static CapitalFlows restored(CapitalFlows f)` |  |

### 6. a band's edges hold and its inside is free (lines 849-935)

| line | len | member | says |
|---:|---:|---|---|
| 851 | 84 | `static void aBandsEdgesHold()` |  |

### 7. under a peg the dial is the US's rate plus the spread; the aim (lines 936-1048)

| line | len | member | says |
|---:|---:|---|---|
| 939 | 4 | `static double dialAfter(Game g)` | Plays a month and returns the dial it set: nothing else moves the policy rate in a month. |
| 945 | 6 | `static void pegWithGap(Game g, double logGap)` | Puts the city on a peg a share of the D$'s shadow under it, so the gap the aim reads is not nothing. |
| 953 | 6 | `static void bandWithGap(Game g, double logGap)` | The same for a band, centred a share under the shadow. |
| 960 | 88 | `static void underAPegTheDialIsTheUSes()` |  |

### 8. the currency aim holds a band, counted over 1,000 seeded years (lines 1049-1092)

| line | len | member | says |
|---:|---:|---|---|
| 1057 | 20 | `static double[] bandYears(boolean aimed)` | 12,000 months of a band narrower than the market's swing, the real rate gap fed each month from a stand-in dial: the neutral dial, or the one the currency aim sets (DebtManager.currencyRate(), the model's own). |
| 1078 | 14 | `static void theCurrencyAimHoldsABand()` |  |

### 9. the toggle's two payers each pay exactly what the edge bought (lines 1093-1262)

| line | len | member | says |
|---:|---:|---|---|
| 1099 | 10 | `static Game surplusTown(String label, ForeignAccounts.EdgePayer payer)` | A city pegged at 36 months with a large positive spread: the dial high, money arriving, a surplus at the peg every month. |
| 1111 | 5 | `static double billPaid(Game g)` | What the treasury paid for the edge's dollars last month, from its journal (0 when the journal has no such line). |
| 1117 | 126 | `static void theTwoPayers()` |  |
| 1245 | 3 | `static double kept(CentralBank cb)` | The central bank keeps nothing, as FX8 states it: equity less the vault, plus what it paid in new money for the vault's dollars, less the remittance due plus the loss carried. |
| 1250 | 4 | `static boolean keepsSomething(CentralBank cb)` | A miss, as LongPlaytest counts one: past a cent and past a billionth of M0. |
| 1256 | 6 | `static void tallyKept(int[] tally, Game...cities)` | One more month held to the identity in each of these cities: tally[0] counts the city-months, tally[1] the misses. |

### 10. saved and loaded mid-peg (lines 1263-1383)

| line | len | member | says |
|---:|---:|---|---|
| 1266 | 7 | `static double[] regimeFields(ForeignAccounts fx)` | The regime's state as the getters say it: every figure XR2 added to the currency, as one row. |
| 1274 | 109 | `static void savedMidPeg()` |  |

### 11. a real run, counted (lines 1384-1451)

| line | len | member | says |
|---:|---:|---|---|
| 1386 | 65 | `static void aRealRun()` |  |

### 12. the hot money's month crosses a save (FX8) (lines 1452-1515)

| line | len | member | says |
|---:|---:|---|---|
| 1457 | 58 | `static void theHotMoneysMonthCrossesASave()` |  |

### 13. leaving a peg takes its doubt with it (FX8) (lines 1516-1576)

| line | len | member | says |
|---:|---:|---|---|
| 1518 | 58 | `static void leavingAPegTakesItsDoubt()` |  |

