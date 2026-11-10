# CurrencyRegimeCheck.java - 2,536 lines · 75 methods · 14 constants · harnesses

`ham/citybuildersim/CurrencyRegimeCheck.java` - generated 2026-11-10 by CodeMap; line numbers are as of that run.

> Proves the D$'s four regimes - Float, Lean against falls (the default), Band
> and Peg to the US$ - hold the currency where they say and cost what they
> should (XR2, 2026-10-22; XR5, 2026-11-06): Lean is exactly the float the city
> had before there were regimes, a peg holds to the digit while the vault pays
> the pressure - the dollars that lift the rate to the peg by the interventions'
> rule, not the month's balance - and gives way at the floor or by doubt at the
> chance the gap makes it, landing past the shadow on whichever side it was, the
> doubt takes the hot money home, a band's edges hold by the same dealing and its
> inside is free, the dial is tied to the US's rate under a peg and the curve is
> priced on that tied path, the autopilot's aim picks the rule, the currency aim
> keeps the shadow inside a band, the strong edge's dollars are paid exactly by
> the payer chosen, and all of it is saved. Not part of the game.
> 
> WHY THIS EXISTS. Jerus, 2026-10-21 (docs/studies/spec-exchange-rates.md, 3.6;
> his calls star 3 to 6 and 13): the city chooses what its D$ is held to. Star 3,
> four regimes, Lean the default and the city's own history; star 4, a peg
> breaks - when the vault cannot pay for the hold (Krugman's first generation)
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
>   2. A PEG HOLDS TO THE DIGIT, AND THE VAULT PAYS THE PRESSURE (XR5). A
>      settled currency pegged at its rate, then months of a deficit that
>      weaken its shadow, then (a twin) of a surplus that strengthens it: every
>      held month the rate is the peg exactly; the vault falls by exactly what
>      interventionLocalFor() says the lift takes (the shadow, getHoldFelt() and
>      the peg), and not the deficit; it rises by exactly the lift in the
>      surplus (the edge's purchase), and what is owed is that purchase at the
>      month's rate; the seven moves add to the month's log move and the held
>      line holds it; while held the hold is the log of the peg over the shadow.
>   3. A VAULT AT THE FLOOR BREAKS THAT MONTH, AT THE SHADOW PLUS OVERSHOOT.
>      A peg whose hold would sell the vault under VAULT_FLOOR months of
>      imports breaks (cause VAULT, nothing sold), its twin with a vault a
>      little deeper holds; the rate lands at the shadow plus OVERSHOOT of its
>      gap; in a city credibility falls by PEG_BREAK_DENT of its distance to
>      KMIN. (The overshoot's two sides and its fade are section 16's.)
>   4. THE CHANCE OF A BREAK, COUNTED OVER 1,000 SEEDED PEGS AGAINST hazard().
>      The breaks counted are within three standard deviations of the hazards
>      summed; the hazard's own shape; the market's path is the same pegged or
>      not.
>   5. THE DOUBT TAKES THE HOT MONEY HOME. A bare CapitalFlows: setDoubt(h)
> ... (97 more lines in the source)

**Uses:** [ForeignAccounts](ForeignAccounts.md) (179), [Game](Game.md) (57), [DebtManager](DebtManager.md) (34), [CapitalFlows](CapitalFlows.md) (29), [Expectations](Expectations.md) (19), [CurrencyCheck](CurrencyCheck.md) (11), [ExchangeRateCheck](ExchangeRateCheck.md) (11), [Founding](Founding.md) (10), [ForeignCheck](ForeignCheck.md) (8), [CentralBank](CentralBank.md) (7), [YieldCurve](YieldCurve.md) (5), [MoneyAudit](MoneyAudit.md) (3), [Nations](Nations.md) (2), [PriceLevel](PriceLevel.md) (2), [GameFiles](GameFiles.md) (1), [WantedPosts](WantedPosts.md) (1), [TreasuryJournal](TreasuryJournal.md) (1), [FloatLinkCheck](FloatLinkCheck.md) (1)

**Used by (5):** [GoldStandardCheck](GoldStandardCheck.md), [InterventionCheck](InterventionCheck.md), [PegAnchorCheck](PegAnchorCheck.md), [SaveFileCheck](SaveFileCheck.md), [VaultMoneysCheck](VaultMoneysCheck.md)

## Sections

| line | section |
|---:|---|
| 373 | 0. Lean is today's float, to the cent |
| 437 | 1. Float sells nothing |
| 475 | 2. a peg holds to the digit, and the vault pays the pressure |
| 572 | 3. a vault at the floor breaks that month |
| 950 | 4. the chance of a break, counted over 1,000 seeded pegs |
| 1037 | 5. the doubt takes the hot money home |
| 1183 | 6. a band's edges hold and its inside is free |
| 1295 | 7. under a peg the dial is the US's rate plus the spread; the aim |
| 1409 | 8. the currency aim holds a band, counted over 1,000 seeded years |
| 1453 | 9. the toggle's two payers each pay exactly what the edge bought |
| 1660 | 10. saved and loaded mid-peg |
| 1824 | 11. a real run, counted |
| 1892 | 12. the hot money's month crosses a save (FX8) |
| 1956 | 13. leaving a peg takes its doubt with it (FX8) |
| 2017 | 14. a peg pays the pressure, not the month's balance (XR5) |
| 2126 | 15. a band's edges pay the pressure (XR5) |
| 2203 | 16. the overshoot both ways (XR5) |
| 2280 | 17. the tied path prices the curve (XR5) |
| 2394 | 18. a thin vault's months are what the hold does (XR5) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 210 | `CurrencyRegimeCheck.FLOAT` | `ForeignAccounts.Regime.FLOAT, LEAN = ForeignAccounts.Regime.LEAN, BAND = Fore...` | The four regimes, by their short names. |
| 214 | `CurrencyRegimeCheck.TRADE` | `3_000, GDP = 6_000` | The trade a quiet currency settles on, each way, and the output it is weighed against: openness one (ExchangeRateCheck's). |
| 217 | `CurrencyRegimeCheck.SEED` | `20_261_022L` | The market's seed in the sections that draw one. |
| 220 | `CurrencyRegimeCheck.DEEP` | `1e9` | A vault no fixture of these can empty or come near the floor of, in dollars. |
| 223 | `CurrencyRegimeCheck.DOLLAR` | `1e-6` | What the harness's own sums are held to where they are exact arithmetic on dollars. |
| 606 | `CurrencyRegimeCheck.PEG_DEPOTS` | `12` | BA1, 2026-11-04: THE PEG TOWNS NO LONGER CAUSED THEIR CONDITION. |
| 607 | `CurrencyRegimeCheck.PEG_VAULT_MONTHS` | `2 * ForeignAccounts.COMFORTABLE_COVER` |  |
| 622 | `CurrencyRegimeCheck.CLIMB_LOOK_MONTHS` | `120` | BA1, 2026-11-04, REDONE 2026-11-05: the months past its 36 that a town played for the credibility fixture is given at most to find prices in the band where the bank's credibility climbs (a smoothed miss within TOLERAN... |
| 644 | `CurrencyRegimeCheck.DOUBT_CRAWL_MONTHS` | `8` | The months doubtTown() plays at the shadow, the peg following it, before the revaluation: the eight months the hot money takes to gather. |
| 652 | `CurrencyRegimeCheck.BREAK_LOOK_MONTHS` | `3` | GW1: the months after the revaluation whose breaks section 3 can use: a break by doubt needs the month before it and its own month to be months where the smoothed miss is within TOLERANCE (the city's climb, the twin's... |
| 655 | `CurrencyRegimeCheck.climbMonths` | `new java.util.HashMap<>()` | The months a probe town is played to, by (depots, causes held off): where the climbing town is pegged is found once (climbMonth()). |
| 1175 | `CurrencyRegimeCheck.SLOT_DOUBT_LIFETIME` | `15, SLOT_ARRIVED = 16, SLOT_DEPARTED = 17, SLOT_DOUBT_DEPARTED = 18, SAVE_SLO...` | Where CapitalFlows.toSaveArray() puts the doubt's lifetime (XR2) and the month's three flows (FX8), and how long the array is (XR4 appended slots 19 to 23 after them). |
| 1456 | `CurrencyRegimeCheck.EDGE_BILL` | `"The band's or the peg's dollars"` | The label the treasury's journal gives the edge's bill (Game.settleTheEdge()). |
| 1473 | `CurrencyRegimeCheck.SURPLUS_DEPOTS` | `{ PEG_DEPOTS, 18, 20, 22, 24, 30, 36 }` | BA1, 2026-11-05: THE DEPOTS A SURPLUS TOWN STANDS ON. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 172 | `static int fails` |  |
| 173 | `static PrintStream out` |  |
| 174 | `static PrintStream quiet` |  |
| 264 | `double rateBefore, vaultBefore, faded, annual, balance, deal, free` |  |
| 640 | `static int pegMonth` | The month the climbing town was pegged and the smoothed inflation it read then (GW1: the log's figures). |
| 641 | `static double pegSmoothed` |  |
| 1474 | `static int surplusDepotsFound` |  |
| 1895 | `static Game doubtCitySaved` | Section 12's city, kept for section 13: saved to slot 2 of its own scratch folder in the month the hot money left on doubt, then played on. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 170 | 2367 | **type** `public class CurrencyRegimeCheck` | Proves the D$'s four regimes - Float, Lean against falls (the default), Band and Peg to the US$ - hold the currency where they say and cost what they should (XR2, 2026-10-22; XR5, 2026-11-06): Lean is exactly the floa... |
| 176 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 181 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 192 | 9 | `static void same(String label, double actual, double expected)` | Equal to the bit. |
| 203 | 5 | `static void quietly(Runnable work)` | Runs a piece of work with the game's narration sent nowhere. |
| 230 | 8 | `static ForeignAccounts book(double exports, double imports, double vaultUsd, long seed, ForeignAccounts.Regime regime)` | A currency past settling on this trade, its vault at this many dollars (bought at the rate of the day, then forgotten as a set-up and not a deal), its market seeded unless the seed is 0 (quiet), held in this regime (a... |
| 240 | 5 | `static void month(ForeignAccounts fx, double exports, double imports)` | One month of this trade, repriced. |
| 247 | 3 | `static double balanceUsd(ForeignAccounts fx)` | The month's own balance in dollars at the rate it is struck at, a surplus positive: the model's two accounts over the rate. |
| 252 | 3 | `static double faded(ForeignAccounts fx)` | What a hold's support fades to by the next reprice: its log, times a half for each INTERVENTION_HALF_LIFE (the model's own constant). |
| 263 | 6 | **type** `static final class Held` | One month of a held currency (a peg or a band), played with the harness's own sums beside it (XR5, THE HOLD PAYS THE PRESSURE): what the month stood on before the reprice, and what the hold should deal by the interven... |
| 267 | 1 | `double vaultMoved(ForeignAccounts fx)` _(in CurrencyRegimeCheck.Held)_ | The vault's change over the month: negative where it sold. |
| 271 | 18 | `static Held heldMonth(ForeignAccounts fx, double exports, double imports, double parityLog)` | A month of this trade on a held currency, its parity this many logs over the world's if parityLog is a number (the lever that makes the shadow weak or strong). |
| 291 | 3 | `static Game town(String label, Founding founding)` | A trading town on the default founding, as ExchangeRateCheck builds it (its own copy: that one is tied to its harness's streams). |
| 303 | 3 | `static Game town(String label, Founding founding, int depotsStanding)` | ...with that many more depots STANDING beside the four it orders (PT1-M4). |
| 308 | 25 | `static Game town(String label, Founding founding, int depotsStanding, boolean causesHeldOff)` | ...with the causes of 1900 held off from the first month where asked (R0b call 7: see savedMidPeg()). |
| 335 | 3 | `static void play(Game g, int months)` | Plays this many months of a town, narration sent nowhere. |
| 339 | 33 | `public static void main(String[] args) throws Exception` |  |

### 0. Lean is today's float, to the cent (lines 373-436)

| line | len | member | says |
|---:|---:|---|---|
| 376 | 6 | `static double[] figures(Game g)` | One city month's figures: the rate, the vault, the defence, the seven moves, M0, the treasury's cash and the bank's credibility. |
| 383 | 4 | `static boolean sameBits(double[] a, double[] b)` |  |
| 389 | 14 | `static boolean leanIsClean(Game g)` | Everything XR2 added reads nothing under Lean. |
| 404 | 32 | `static void leanIsTodaysFloat()` |  |

### 1. Float sells nothing (lines 437-474)

| line | len | member | says |
|---:|---:|---|---|
| 440 | 8 | `static void deficitMonth(ForeignAccounts fx, int m)` | A month of a city that runs a deficit most months and whose parity drifts weaker: the weather that makes the lean sell. |
| 449 | 25 | `static void floatSellsNothing()` |  |

### 2. a peg holds to the digit, and the vault pays the pressure (lines 475-571)

| line | len | member | says |
|---:|---:|---|---|
| 477 | 94 | `static void aPegHolds()` |  |

### 3. a vault at the floor breaks that month (lines 572-949)

| line | len | member | says |
|---:|---:|---|---|
| 575 | 6 | `static void driftMonth(ForeignAccounts fx, int m)` | Two currencies, one on a peg and one floating, the same seed, trade in balance and a parity drifting weaker 5% a year: the pair a peg breaks beside. |
| 589 | 3 | `static Founding steady()` | The founding the peg fixtures stand on: the default's, on the Historical path (EP1). |
| 625 | 3 | `static Game pegTown(String label)` | A trading town on the default founding as the peg fixtures need it (see PEG_DEPOTS), played its 36 months and its vault deepened: ready to be pegged. |
| 630 | 3 | `static Game pegTown(String label, boolean climbing)` | ...and, if `climbing`, played on until prices are in the band where the bank's credibility can climb (see CLIMB_LOOK_MONTHS). |
| 635 | 3 | `static Game pegTown(String label, boolean climbing, int depots)` | ...with this many depots standing (see PEG_DEPOTS, and surplusDepots() for a town that needs more). |
| 668 | 27 | `static int climbMonth(int depots, boolean causesHeldOff)` | GW1: the month the climbing town is pegged in. |
| 697 | 22 | `static Game pegTown(String label, boolean climbing, int depots, boolean causesHeldOff)` | ...with the causes of 1900 held off from the first month where asked (R0b call 7: see savedMidPeg()). |
| 729 | 3 | `static Game doubtTown(String label, long reseed)` | A city on the default founding pegged with the peg revalued a fifth under its shadow: the doubt fixture, a few months from its break at the peg's chance. |
| 734 | 14 | `static Game doubtTown(String label, long reseed, boolean climbing, boolean causesHeldOff)` | ...and, if `climbing`, pegged in the month prices are back near the target (pegTown(), CLIMB_LOOK_MONTHS); with the causes of 1900 held off from the first month where asked (R0b call 7: see savedMidPeg()). |
| 753 | 3 | `static Game heldTwin(String label, long seed, int months, double[] start)` | The doubt town's twin: played to the month before `months`, its credibility (start[0]) read, its peg moved to a rate so weak the shadow sits far under it, so that month nothing breaks by chance, and played the month. |
| 757 | 8 | `static Game heldTwin(String label, long seed, int months, double[] start, boolean climbing)` |  |
| 773 | 7 | `static ForeignAccounts pegAtTheFloor(double vaultUsd)` | A peg a few deficit months old, quiet, whose vault is then sold down to this many dollars by the treasury (a set-up, not a deal: forgotten by the rate). |
| 782 | 3 | `static double saleAt(double vaultUsd)` | The dollars the hold needs to sell in the peg's next deficit month at this vault, from the harness's sums (Held): the sale that month, whether the peg then held or broke. |
| 786 | 163 | `static void aVaultAtTheFloorBreaks()` |  |

### 4. the chance of a break, counted over 1,000 seeded pegs (lines 950-1036)

| line | len | member | says |
|---:|---:|---|---|
| 952 | 84 | `static void theChanceOfABreak()` |  |

### 5. the doubt takes the hot money home (lines 1037-1182)

| line | len | member | says |
|---:|---:|---|---|
| 1040 | 4 | `static void hotMonth(CapitalFlows f, int m)` | One month of hot money at a 9% rate against the world's 2% and a point of risk: the standing fixture of CapitalFlowCheck. |
| 1045 | 5 | `static CapitalFlows stocked(int months)` |  |
| 1051 | 122 | `static void theDoubtTakesTheHotMoneyHome()` |  |
| 1177 | 5 | `static CapitalFlows restored(CapitalFlows f)` |  |

### 6. a band's edges hold and its inside is free (lines 1183-1294)

| line | len | member | says |
|---:|---:|---|---|
| 1186 | 7 | `static ForeignAccounts bandAtTheFloor(double vaultUsd, double width)` | A band this wide centred on the rate of the day, quiet, whose vault is sold down to this many dollars by the treasury (a set-up, not a deal: forgotten by the rate). |
| 1195 | 3 | `static double bandSaleAt(double vaultUsd, double width)` | The dollars the band's hold needs to sell in its first deficit month at this vault, from the harness's sums (Held): what it would sell, whether it then sold or gave way. |
| 1199 | 95 | `static void aBandsEdgesHold()` |  |

### 7. under a peg the dial is the US's rate plus the spread; the aim (lines 1295-1408)

| line | len | member | says |
|---:|---:|---|---|
| 1298 | 4 | `static double dialAfter(Game g)` | Plays a month and returns the dial it set: nothing else moves the policy rate in a month. |
| 1304 | 6 | `static void pegWithGap(Game g, double logGap)` | Puts the city on a peg a share of the D$'s shadow under it, so the gap the aim reads is not nothing. |
| 1312 | 6 | `static void bandWithGap(Game g, double logGap)` | The same for a band, centred a share under the shadow. |
| 1319 | 89 | `static void underAPegTheDialIsTheUSes()` |  |

### 8. the currency aim holds a band, counted over 1,000 seeded years (lines 1409-1452)

| line | len | member | says |
|---:|---:|---|---|
| 1417 | 20 | `static double[] bandYears(boolean aimed)` | 12,000 months of a band narrower than the market's swing, the real rate gap fed each month from a stand-in dial: the neutral dial, or the one the currency aim sets (DebtManager.currencyRate(), the model's own). |
| 1438 | 14 | `static void theCurrencyAimHoldsABand()` |  |

### 9. the toggle's two payers each pay exactly what the edge bought (lines 1453-1659)

| line | len | member | says |
|---:|---:|---|---|
| 1476 | 18 | `static int surplusDepots()` |  |
| 1496 | 9 | `static Game surplusTown(String label, ForeignAccounts.EdgePayer payer)` | A city pegged at 36 months with a large positive spread: the dial high, money arriving, the shadow under the peg and the vault buying (XR5: the lift, not the month's surplus). |
| 1507 | 5 | `static double billPaid(Game g)` | What the treasury paid for the edge's dollars last month, from its journal (0 when the journal has no such line). |
| 1513 | 127 | `static void theTwoPayers()` |  |
| 1642 | 3 | `static double kept(CentralBank cb)` | The central bank keeps nothing, as FX8 states it: equity less the vault, plus what it paid in new money for the vault's dollars, less the remittance due plus the loss carried. |
| 1647 | 4 | `static boolean keepsSomething(CentralBank cb)` | A miss, as LongPlaytest counts one: past a cent and past a billionth of M0. |
| 1653 | 6 | `static void tallyKept(int[] tally, Game...cities)` | One more month held to the identity in each of these cities: tally[0] counts the city-months, tally[1] the misses. |

### 10. saved and loaded mid-peg (lines 1660-1823)

| line | len | member | says |
|---:|---:|---|---|
| 1663 | 7 | `static double[] regimeFields(ForeignAccounts fx)` | The regime's state as the getters say it: every figure XR2 added to the currency, and XR5's hold, as one row. |
| 1671 | 152 | `static void savedMidPeg()` |  |

### 11. a real run, counted (lines 1824-1891)

| line | len | member | says |
|---:|---:|---|---|
| 1826 | 65 | `static void aRealRun()` |  |

### 12. the hot money's month crosses a save (FX8) (lines 1892-1955)

| line | len | member | says |
|---:|---:|---|---|
| 1897 | 58 | `static void theHotMoneysMonthCrossesASave()` |  |

### 13. leaving a peg takes its doubt with it (FX8) (lines 1956-2016)

| line | len | member | says |
|---:|---:|---|---|
| 1958 | 58 | `static void leavingAPegTakesItsDoubt()` |  |

### 14. a peg pays the pressure, not the month's balance (XR5) (lines 2017-2125)

| line | len | member | says |
|---:|---:|---|---|
| 2019 | 106 | `static void aPegPaysThePressure()` |  |

### 15. a band's edges pay the pressure (XR5) (lines 2126-2202)

| line | len | member | says |
|---:|---:|---|---|
| 2128 | 74 | `static void aBandsEdgesPayThePressure()` |  |

### 16. the overshoot both ways (XR5) (lines 2203-2279)

| line | len | member | says |
|---:|---:|---|---|
| 2206 | 6 | `static void fallMonth(ForeignAccounts fx, int m)` | A month of balanced trade and a parity falling away by 60% over 30 months and staying there: the shadow far under the peg, where the chance of a break is BREAK_BASE a month. |
| 2213 | 66 | `static void theOvershootBothWays()` |  |

### 17. the tied path prices the curve (XR5) (lines 2280-2393)

| line | len | member | says |
|---:|---:|---|---|
| 2283 | 8 | `static DebtManager tiedManager(boolean[] tie, double world)` | A bare debt manager on a dial and a world rate, struck on inflation well off its target, its tie read from this switch (null: never handed one). |
| 2293 | 9 | `static int pathsApart(DebtManager a, DebtManager b)` | How many of the manager's paths and rates differ from another's by a bit: the curve's expected dial 600 months out, the floater's path, the long yields, the rule and where it settles. |
| 2303 | 84 | `static void theTiedPathPricesTheCurve()` |  |
| 2389 | 4 | `static boolean tiedIn(Game city, DebtManager dm, ForeignAccounts.Regime regime)` | Whether the city's debt manager is tied with the city in this regime. |

### 18. a thin vault's months are what the hold does (XR5) (lines 2394-2536)

| line | len | member | says |
|---:|---:|---|---|
| 2401 | 5 | `static ForeignAccounts steadyGap(double gap, double vaultUsd)` | A bare currency pegged at its rate with its shadow a steady gap (a log) over the peg, on balanced trade and a quiet market: the parity is set once, before the first reprice, so the trend opens on it and stays (a level... |
| 2408 | 3 | `static double firstMonthUsd(ForeignAccounts fx, double gap)` | The first month's cost of holding a steady gap, in dollars, from the model's own constants and getters: interventionLocalFor(gap, a year's GDP) over the rate. |
| 2413 | 3 | `static double eachMonthUsd(ForeignAccounts fx, double gap)` | ...and each month after it: what the support's fading takes, (1 - 0.5^(1 / INTERVENTION_HALF_LIFE)) of the gap. |
| 2417 | 114 | `static void aThinVaultsMonthsAreWhatTheHoldDoes()` |  |
| 2533 | 3 | `static String ordinal(int n)` | 21 as "21st", for a printed line. |

