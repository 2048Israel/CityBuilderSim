# CurrencyRegimeCheck.java - 2,529 lines · 77 methods · 16 constants · harnesses

`ham/citybuildersim/CurrencyRegimeCheck.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

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
>      nothing owed. The months the lean sold are counted; that the defence fires
>      is caused on a bare currency (FS10: a vault and a deficit month sell).
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
>      KMIN (two cities alike, credibility set, the city's peg struck so far under its shadow that the vault breaks it, the twin's at it). (The overshoot's two sides and its fade are section 16's.)
>   4. THE CHANCE OF A BREAK, COUNTED OVER 1,000 SEEDED PEGS AGAINST hazard().
>      The breaks counted are within three standard deviations of the hazards
>      summed; the hazard's own shape; the market's path is the same pegged or
>      not.
>   5. THE DOUBT TAKES THE HOT MONEY HOME. A bare CapitalFlows: setDoubt(h)
> ... (104 more lines in the source)

**Uses:** [ForeignAccounts](ForeignAccounts.md) (209), [Game](Game.md) (57), [DebtManager](DebtManager.md) (34), [CapitalFlows](CapitalFlows.md) (30), [Expectations](Expectations.md) (13), [ExchangeRateCheck](ExchangeRateCheck.md) (13), [CurrencyCheck](CurrencyCheck.md) (11), [MoneyAudit](MoneyAudit.md) (11), [Founding](Founding.md) (10), [ForeignCheck](ForeignCheck.md) (7), [CentralBank](CentralBank.md) (7), [YieldCurve](YieldCurve.md) (5), [PriceIndex](PriceIndex.md) (4), [PriceLevel](PriceLevel.md) (3), [Nations](Nations.md) (2), [DataSave](DataSave.md) (2), [GameFiles](GameFiles.md) (1), [TreasuryJournal](TreasuryJournal.md) (1), [InterestAbroadCheck](InterestAbroadCheck.md) (1)

**Used by (6):** [GoldStandardCheck](GoldStandardCheck.md), [InterventionCheck](InterventionCheck.md), [PegAnchorCheck](PegAnchorCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [VaultMoneysCheck](VaultMoneysCheck.md)

## Sections

| line | section |
|---:|---|
| 381 | 0. Lean is today's float, to the cent |
| 451 | 1. Float sells nothing |
| 489 | 2. a peg holds to the digit, and the vault pays the pressure |
| 586 | 3. a vault at the floor breaks that month |
| 899 | 4. the chance of a break, counted over 1,000 seeded pegs |
| 986 | 5. the doubt takes the hot money home |
| 1123 | 6. a band's edges hold and its inside is free |
| 1235 | 7. under a peg the dial is the US's rate plus the spread; the aim |
| 1354 | 8. the currency aim holds a band, counted over 1,000 seeded years |
| 1398 | 9. the toggle's two payers each pay exactly what the edge bought |
| 1577 | 10. saved and loaded mid-peg |
| 1716 | 11. a real run, counted |
| 1784 | 12. the hot money's month crosses a save (FX8) |
| 1856 | 13. leaving a peg takes its doubt with it (FX8) |
| 1918 | 14. a peg pays the pressure, not the month's balance (XR5) |
| 2027 | 15. a band's edges pay the pressure (XR5) |
| 2104 | 16. the overshoot both ways (XR5) |
| 2181 | 17. the tied path prices the curve (XR5) |
| 2295 | 18. a thin vault's months are what the hold does (XR5) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 217 | `CurrencyRegimeCheck.FLOAT` | `ForeignAccounts.Regime.FLOAT, LEAN = ForeignAccounts.Regime.LEAN, BAND = Fore...` | The four regimes, by their short names. |
| 221 | `CurrencyRegimeCheck.TRADE` | `3_000, GDP = 6_000` | The trade a quiet currency settles on, each way, and the output it is weighed against: openness one (ExchangeRateCheck's). |
| 224 | `CurrencyRegimeCheck.SEED` | `20_261_022L` | The market's seed in the sections that draw one. |
| 227 | `CurrencyRegimeCheck.DEEP` | `1e9` | A vault no fixture of these can empty or come near the floor of, in dollars. |
| 230 | `CurrencyRegimeCheck.DOLLAR` | `1e-6` | What the harness's own sums are held to where they are exact arithmetic on dollars. |
| 620 | `CurrencyRegimeCheck.PEG_DEPOTS` | `12` | BA1, 2026-11-04: THE PEG TOWNS NO LONGER CAUSED THEIR CONDITION. |
| 621 | `CurrencyRegimeCheck.PEG_VAULT_MONTHS` | `2 * ForeignAccounts.COMFORTABLE_COVER` |  |
| 643 | `CurrencyRegimeCheck.DOUBT_GAP` | `.20` | The log gap the doubt fixtures revalue the peg under the shadow by: a fifth, past BREAK_GAP, so the reprice strikes more than the base chance. |
| 646 | `CurrencyRegimeCheck.HOT_SHARE` | `.5` | The share of the target the dial draws its hot money toward that the doubt fixtures place as stock: below it, so money still arrives each month. |
| 649 | `CurrencyRegimeCheck.CREDIT_START` | `.60` | The credibility the credibility fixture's cities are set to: between KMIN and KMAX, so the dent and the month's gain both have room. |
| 652 | `CurrencyRegimeCheck.BREAK_MARGIN` | `4` | The factor over what the vault can sell above its floor that the sale a break by the vault is sized at: room for the shadow's drift in the month. |
| 655 | `CurrencyRegimeCheck.BILL_MARGIN` | `10` | The factor over section 9's own floor (100 x PriceLevel.FACTOR of local money) that the bill its gap is sized for stands at. |
| 1115 | `CurrencyRegimeCheck.SLOT_DOUBT_LIFETIME` | `15, SLOT_ARRIVED = 16, SLOT_DEPARTED = 17, SLOT_DOUBT_DEPARTED = 18, SAVE_SLO...` | Where CapitalFlows.toSaveArray() puts the doubt's lifetime (XR2) and the month's three flows (FX8), and how long the array is (XR4 appended slots 19 to 23 after them, FB3 the peaks; the model's own count). |
| 1238 | `CurrencyRegimeCheck.PRICE_RISE` | `.06` | The year of prices section 7 puts the index on: rising 6%, a few points over the 2% target, so the inflation rule's dial is not the neutral one. |
| 1401 | `CurrencyRegimeCheck.EDGE_BILL` | `"The band's or the peg's dollars"` | The label the treasury's journal gives the edge's bill (Game.settleTheEdge()). |
| 1790 | `CurrencyRegimeCheck.TWIN_SLOT` | `3` | The scratch slot section 12's twin is saved to, a month before the save month, to read the save month's target ahead (slot 2 is the save the section is about). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 179 | `static int fails` |  |
| 180 | `static PrintStream out` |  |
| 181 | `static PrintStream quiet` |  |
| 271 | `double rateBefore, vaultBefore, faded, annual, balance, deal, free` |  |
| 1787 | `static Game doubtCitySaved` | Section 12's city, kept for section 13: saved to slot 2 of its own scratch folder in the month the hot money left on doubt, then played on. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 177 | 2353 | **type** `public class CurrencyRegimeCheck` | Proves the D$'s four regimes - Float, Lean against falls (the default), Band and Peg to the US$ - hold the currency where they say and cost what they should (XR2, 2026-10-22; XR5, 2026-11-06): Lean is exactly the floa... |
| 183 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 188 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 199 | 9 | `static void same(String label, double actual, double expected)` | Equal to the bit. |
| 210 | 5 | `static void quietly(Runnable work)` | Runs a piece of work with the game's narration sent nowhere. |
| 237 | 8 | `static ForeignAccounts book(double exports, double imports, double vaultUsd, long seed, ForeignAccounts.Regime regime)` | A currency past settling on this trade, its vault at this many dollars (bought at the rate of the day, then forgotten as a set-up and not a deal), its market seeded unless the seed is 0 (quiet), held in this regime (a... |
| 247 | 5 | `static void month(ForeignAccounts fx, double exports, double imports)` | One month of this trade, repriced. |
| 254 | 3 | `static double balanceUsd(ForeignAccounts fx)` | The month's own balance in dollars at the rate it is struck at, a surplus positive: the model's two accounts over the rate. |
| 259 | 3 | `static double faded(ForeignAccounts fx)` | What a hold's support fades to by the next reprice: its log, times a half for each INTERVENTION_HALF_LIFE (the model's own constant). |
| 270 | 6 | **type** `static final class Held` | One month of a held currency (a peg or a band), played with the harness's own sums beside it (XR5, THE HOLD PAYS THE PRESSURE): what the month stood on before the reprice, and what the hold should deal by the interven... |
| 274 | 1 | `double vaultMoved(ForeignAccounts fx)` _(in CurrencyRegimeCheck.Held)_ | The vault's change over the month: negative where it sold. |
| 278 | 18 | `static Held heldMonth(ForeignAccounts fx, double exports, double imports, double parityLog)` | A month of this trade on a held currency, its parity this many logs over the world's if parityLog is a number (the lever that makes the shadow weak or strong). |
| 298 | 3 | `static Game town(String label, Founding founding)` | A trading town on the default founding, as ExchangeRateCheck builds it (its own copy: that one is tied to its harness's streams). |
| 310 | 3 | `static Game town(String label, Founding founding, int depotsStanding)` | ...with that many more depots STANDING beside the four it orders (PT1-M4). |
| 315 | 29 | `static Game town(String label, Founding founding, int depotsStanding, boolean causesHeldOff)` | ...with the causes of 1900 held off from the first month where asked (R0b call 7: see savedMidPeg()). |
| 346 | 3 | `static void play(Game g, int months)` | Plays this many months of a town, narration sent nowhere. |
| 350 | 30 | `public static void main(String[] args) throws Exception` |  |

### 0. Lean is today's float, to the cent (lines 381-450)

| line | len | member | says |
|---:|---:|---|---|
| 384 | 6 | `static double[] figures(Game g)` | One city month's figures: the rate, the vault, the defence, the seven moves, M0, the treasury's cash and the bank's credibility. |
| 391 | 4 | `static boolean sameBits(double[] a, double[] b)` |  |
| 397 | 14 | `static boolean leanIsClean(Game g)` | Everything XR2 added reads nothing under Lean. |
| 412 | 38 | `static void leanIsTodaysFloat()` |  |

### 1. Float sells nothing (lines 451-488)

| line | len | member | says |
|---:|---:|---|---|
| 454 | 8 | `static void deficitMonth(ForeignAccounts fx, int m)` | A month of a city that runs a deficit most months and whose parity drifts weaker: the weather that makes the lean sell. |
| 463 | 25 | `static void floatSellsNothing()` |  |

### 2. a peg holds to the digit, and the vault pays the pressure (lines 489-585)

| line | len | member | says |
|---:|---:|---|---|
| 491 | 94 | `static void aPegHolds()` |  |

### 3. a vault at the floor breaks that month (lines 586-898)

| line | len | member | says |
|---:|---:|---|---|
| 589 | 6 | `static void driftMonth(ForeignAccounts fx, int m)` | Two currencies, one on a peg and one floating, the same seed, trade in balance and a parity drifting weaker 5% a year: the pair a peg breaks beside. |
| 603 | 3 | `static Founding steady()` | The founding the peg fixtures stand on: the default's, on the Historical path (EP1). |
| 624 | 3 | `static Game pegTown(String label)` | A trading town on the default founding as the peg fixtures need it (see PEG_DEPOTS), played its 36 months and its vault deepened: ready to be pegged. |
| 629 | 9 | `static Game pegTown(String label, int depots, boolean causesHeldOff)` | ...with this many depots standing, and the causes of 1900 held off from the first month where asked (R0b call 7: see savedMidPeg()). |
| 662 | 10 | `static Game tiedPegTown(String label, boolean causesHeldOff)` | A pegged town at the end of the month its dial was tied (a spread of 30 points over the US's rate, the market quiet so that nothing breaks by chance and no draw moves the shadow): the shadow has moved to where the tie... |
| 677 | 7 | `static void placeHotMoney(Game g, double shareOfTarget)` | Hot money placed in the city by name (CapitalFlows.placeArrivalForTest(), as SaveFileCheck places a month's arrival) up to this share of the target its dial draws it toward, and into the bank that funds it, as Game do... |
| 689 | 6 | `static Game causedDoubtTown(String label, boolean causesHeldOff)` | The doubt fixture, caused: a tied pegged town with hot money in it, the peg revalued DOUBT_GAP under its shadow. |
| 701 | 10 | `static Game creditTown(String label)` | The credibility fixture's city: a tied pegged town (tiedPegTown()) with credibility set to CREDIT_START and the smoothed inflation to the target (the bank's own save array put back, Expectations.restore()), the price ... |
| 717 | 9 | `static void aYearOfPrices(Game g, double rate)` | The price index put back as a save would carry a year of prices rising at this rate (PriceIndex.restore(), as CurrencyCheck's section 5 puts its year back): thirteen readings, the newest at today's level and each a tw... |
| 728 | 6 | `static double breakingGap(ForeignAccounts fx)` | The log gap a peg must stand under the shadow for the sale holding it to be BREAK_MARGIN times what the vault can sell above its floor: from the model's own rule, interventionLocalFor(). |
| 736 | 6 | `static double billGap(ForeignAccounts fx)` | The log gap a peg must stand over the shadow for the hold's purchase to cost BILL_MARGIN times section 9's floor of 100 x PriceLevel.FACTOR of local money (interventionLocalFor()). |
| 750 | 7 | `static ForeignAccounts pegAtTheFloor(double vaultUsd)` | A peg a few deficit months old, quiet, whose vault is then sold down to this many dollars by the treasury (a set-up, not a deal: forgotten by the rate). |
| 759 | 3 | `static double saleAt(double vaultUsd)` | The dollars the hold needs to sell in the peg's next deficit month at this vault, from the harness's sums (Held): the sale that month, whether the peg then held or broke. |
| 763 | 135 | `static void aVaultAtTheFloorBreaks()` |  |

### 4. the chance of a break, counted over 1,000 seeded pegs (lines 899-985)

| line | len | member | says |
|---:|---:|---|---|
| 901 | 84 | `static void theChanceOfABreak()` |  |

### 5. the doubt takes the hot money home (lines 986-1122)

| line | len | member | says |
|---:|---:|---|---|
| 989 | 4 | `static void hotMonth(CapitalFlows f, int m)` | One month of hot money at a 9% rate against the world's 2% and a point of risk: the standing fixture of CapitalFlowCheck. |
| 994 | 5 | `static CapitalFlows stocked(int months)` |  |
| 1000 | 113 | `static void theDoubtTakesTheHotMoneyHome()` |  |
| 1117 | 5 | `static CapitalFlows restored(CapitalFlows f)` |  |

### 6. a band's edges hold and its inside is free (lines 1123-1234)

| line | len | member | says |
|---:|---:|---|---|
| 1126 | 7 | `static ForeignAccounts bandAtTheFloor(double vaultUsd, double width)` | A band this wide centred on the rate of the day, quiet, whose vault is sold down to this many dollars by the treasury (a set-up, not a deal: forgotten by the rate). |
| 1135 | 3 | `static double bandSaleAt(double vaultUsd, double width)` | The dollars the band's hold needs to sell in its first deficit month at this vault, from the harness's sums (Held): what it would sell, whether it then sold or gave way. |
| 1139 | 95 | `static void aBandsEdgesHold()` |  |

### 7. under a peg the dial is the US's rate plus the spread; the aim (lines 1235-1353)

| line | len | member | says |
|---:|---:|---|---|
| 1241 | 4 | `static double dialAfter(Game g)` | Plays a month and returns the dial it set: nothing else moves the policy rate in a month. |
| 1247 | 6 | `static void pegWithGap(Game g, double logGap)` | Puts the city on a peg a share of the D$'s shadow under it, so the gap the aim reads is not nothing. |
| 1255 | 6 | `static void bandWithGap(Game g, double logGap)` | The same for a band, centred a share under the shadow. |
| 1262 | 91 | `static void underAPegTheDialIsTheUSes()` |  |

### 8. the currency aim holds a band, counted over 1,000 seeded years (lines 1354-1397)

| line | len | member | says |
|---:|---:|---|---|
| 1362 | 20 | `static double[] bandYears(boolean aimed)` | 12,000 months of a band narrower than the market's swing, the real rate gap fed each month from a stand-in dial: the neutral dial, or the one the currency aim sets (DebtManager.currencyRate(), the model's own). |
| 1383 | 14 | `static void theCurrencyAimHoldsABand()` |  |

### 9. the toggle's two payers each pay exactly what the edge bought (lines 1398-1576)

| line | len | member | says |
|---:|---:|---|---|
| 1408 | 10 | `static Game pressedTown(String label, ForeignAccounts.EdgePayer payer)` | CAUSED (FS10), where surplusTown() searched trial towns over SURPLUS_DEPOTS for a first month at the peg that owed the edge's bill: a pegged town of PEG_DEPOTS depots with the large spread, its peg struck ABOVE its sh... |
| 1420 | 5 | `static double billPaid(Game g)` | What the treasury paid for the edge's dollars last month, from its journal (0 when the journal has no such line). |
| 1426 | 131 | `static void theTwoPayers()` |  |
| 1559 | 3 | `static double kept(CentralBank cb)` | The central bank keeps nothing, as FX8 states it: equity less the vault, plus what it paid in new money for the vault's dollars, less the remittance due plus the loss carried. |
| 1564 | 4 | `static boolean keepsSomething(CentralBank cb)` | A miss, as LongPlaytest counts one: past a cent and past a billionth of M0. |
| 1570 | 6 | `static void tallyKept(int[] tally, Game...cities)` | One more month held to the identity in each of these cities: tally[0] counts the city-months, tally[1] the misses. |

### 10. saved and loaded mid-peg (lines 1577-1715)

| line | len | member | says |
|---:|---:|---|---|
| 1580 | 7 | `static double[] regimeFields(ForeignAccounts fx)` | The regime's state as the getters say it: every figure XR2 added to the currency, and XR5's hold, as one row. |
| 1588 | 127 | `static void savedMidPeg()` |  |

### 11. a real run, counted (lines 1716-1783)

| line | len | member | says |
|---:|---:|---|---|
| 1718 | 65 | `static void aRealRun()` |  |

### 12. the hot money's month crosses a save (FX8) (lines 1784-1855)

| line | len | member | says |
|---:|---:|---|---|
| 1792 | 63 | `static void theHotMoneysMonthCrossesASave()` |  |

### 13. leaving a peg takes its doubt with it (FX8) (lines 1856-1917)

| line | len | member | says |
|---:|---:|---|---|
| 1858 | 59 | `static void leavingAPegTakesItsDoubt()` |  |

### 14. a peg pays the pressure, not the month's balance (XR5) (lines 1918-2026)

| line | len | member | says |
|---:|---:|---|---|
| 1920 | 106 | `static void aPegPaysThePressure()` |  |

### 15. a band's edges pay the pressure (XR5) (lines 2027-2103)

| line | len | member | says |
|---:|---:|---|---|
| 2029 | 74 | `static void aBandsEdgesPayThePressure()` |  |

### 16. the overshoot both ways (XR5) (lines 2104-2180)

| line | len | member | says |
|---:|---:|---|---|
| 2107 | 6 | `static void fallMonth(ForeignAccounts fx, int m)` | A month of balanced trade and a parity falling away by 60% over 30 months and staying there: the shadow far under the peg, where the chance of a break is BREAK_BASE a month. |
| 2114 | 66 | `static void theOvershootBothWays()` |  |

### 17. the tied path prices the curve (XR5) (lines 2181-2294)

| line | len | member | says |
|---:|---:|---|---|
| 2184 | 8 | `static DebtManager tiedManager(boolean[] tie, double world)` | A bare debt manager on a dial and a world rate, struck on inflation well off its target, its tie read from this switch (null: never handed one). |
| 2194 | 9 | `static int pathsApart(DebtManager a, DebtManager b)` | How many of the manager's paths and rates differ from another's by a bit: the curve's expected dial 600 months out, the floater's path, the long yields, the rule and where it settles. |
| 2204 | 84 | `static void theTiedPathPricesTheCurve()` |  |
| 2290 | 4 | `static boolean tiedIn(Game city, DebtManager dm, ForeignAccounts.Regime regime)` | Whether the city's debt manager is tied with the city in this regime. |

### 18. a thin vault's months are what the hold does (XR5) (lines 2295-2529)

| line | len | member | says |
|---:|---:|---|---|
| 2302 | 5 | `static ForeignAccounts steadyGap(double gap, double vaultUsd)` | A bare currency pegged at its rate with its shadow a steady gap (a log) over the peg, on balanced trade and a quiet market: the parity is set once, before the first reprice, so the trend opens on it and stays (a level... |
| 2309 | 3 | `static double firstMonthUsd(ForeignAccounts fx, double gap)` | The first month's cost of holding a steady gap, in dollars, from the model's own constants and getters: interventionLocalFor(gap, a year's GDP) over the rate. |
| 2314 | 3 | `static double eachMonthUsd(ForeignAccounts fx, double gap)` | ...and each month after it: what the support's fading takes, (1 - 0.5^(1 / INTERVENTION_HALF_LIFE)) of the gap. |
| 2318 | 114 | `static void aThinVaultsMonthsAreWhatTheHoldDoes()` |  |
| 2434 | 38 | `static void aDeepRepegsPurchaseIsBounded()` | XR7/XR8: force the exponential inverse, rather than wait for a player to choose a deep repeg. |
| 2474 | 13 | `static Game supportedTown(String label)` | A real player-chosen peg carrying nonzero support and a large unpaid purchase, for save/read checks (XR7). |
| 2489 | 35 | `static boolean savedHoldSupportChecks()` | The actual DataSave JSON and Game loader must carry support already paid for, and the next bill must only replace its fade. |
| 2526 | 3 | `static String ordinal(int n)` | 21 as "21st", for a printed line. |

