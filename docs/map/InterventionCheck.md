# InterventionCheck.java - 446 lines · 12 methods · 5 constants · harnesses

`ham/citybuildersim/InterventionCheck.java` - generated 2026-11-06 by CodeMap; line numbers are as of that run.

> Proves a purchase or sale of dollars moves the D$ a BOUNDED amount (XI1,
> 2026-11-06): linear up to INTERVENTION_LINEAR of a year's GDP, exactly as it
> always was, and logarithmic past it, INTERVENTION_IMPACT x s0 x (1 + ln(s /
> s0)), a sale the mirror. Not part of the game.
> 
> WHY THIS EXISTS. Jerus's Rotterdam (2026-11-02): the treasury bought dollars
> worth 4.5 years of the city's output in one month, and ForeignAccounts, THE
> FLOAT, step 5 added INTERVENTION_IMPACT x dealt / annual GDP to the felt
> interventions, linear without end: 2.0 x 4.5 = 9.0 in logs, the D$ x8,100
> from that line and x11,600 in the month. A rule fitted to the IMF's 1.7-2.0%
> per 1% of a year's GDP (R33) was being asked about a purchase 450 times the
> size it was fitted on. The study (docs/studies/spec-gold-money.md, 4.5) bound
> it, and Jerus chose the bound ("Rate running away (Recommended)"): linear to
> a tenth of a year's output and logarithmic past it, so each further dollar
> moves the rate less, and Rotterdam's 4.5 years moves it 2.0 x 0.10 x (1 + ln
> 45) = 0.961 in logs, the D$ x2.6. What must not move is every figure at or
> under the bound: the gate checks the default playtest's trace bit for bit,
> so section 1 asserts the old expression's bits and section 5 asserts that a
> real run's defence never reaches the bound.
> 
> What it has to prove, a section each, every one a fixture that causes the
> condition rather than waiting for it:
> 
>   1. BELOW THE BOUND NOTHING MOVES, BIT FOR BIT. interventionMove(), its
>      inverse interventionLocalFor() and dealEffect() equal the old
>      expressions to the bit on sizes up to and at INTERVENTION_LINEAR, both
>      signs; a purchase of 9% of a year's GDP moves the rate by exactly
>      INTERVENTION_IMPACT x the purchase over a year's GDP.
>   2. PAST IT THE RULE IS LOGARITHMIC. At 2, 10 and 45 times the bound the
>      move is INTERVENTION_IMPACT x s0 x (1 + ln k); it is continuous at the
>      bound and so is its slope; it only rises, each further dollar moves it
>      less, and a sale is the mirror of a purchase.
>   3. THE INVERSE. interventionLocalFor(interventionMove(x)) returns x from a
>      millionth of a year's output to a hundred years', both signs, and 0 with
>      no GDP; the Lean defence in a fall it takes more than the bound to take
>      back sells interventionLocalFor(absorption() x fall) and lifts the D$ by
>      exactly the share of the fall it took back.
>   4. ROTTERDAM'S CASE. Twins on one seed, one buys 4.5 years of output: the
>      D$ moves by interventionMove(), I x s0 x (1 + ln 45), the rate x2.6 and
>      not x8,100 - on a bare currency, and on a played town under Float.
>   5. HOW OFTEN IT FIRES IN A REAL RUN. The default town, Lean, nobody
>      dealing, played 240 months: no reprice's defence passes the bound, the
>      premise of the byte-identical claim.
> 
> Numbering is the running order. Money is in thousands, as everywhere.

**Uses:** [ForeignAccounts](ForeignAccounts.md) (37), [ExchangeRateCheck](ExchangeRateCheck.md) (18), [CurrencyRegimeCheck](CurrencyRegimeCheck.md) (9), [Founding](Founding.md) (3), [Game](Game.md) (3), [CurrencyCheck](CurrencyCheck.md) (2)

## Sections

| line | section |
|---:|---|
| 139 | 1. below the bound nothing moves, to the bit |
| 208 | 2. past it the rule is logarithmic |
| 284 | 3. the inverse |
| 349 | 4. Rotterdam's case |
| 415 | 5. how often it fires in a real run |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 106 | `InterventionCheck.IMPACT` | `ForeignAccounts.INTERVENTION_IMPACT, LINEAR = ForeignAccounts.INTERVENTION_LI...` | The dials, by their short names. |
| 109 | `InterventionCheck.SEED` | `ExchangeRateCheck.SEED` | The market's seed in the sections that draw one (ExchangeRateCheck's). |
| 112 | `InterventionCheck.RUN_MONTHS` | `240` | Months of the real run in section 5. |
| 115 | `InterventionCheck.YEAR` | `12` | The year a played town trades before its test month in section 4. |
| 118 | `InterventionCheck.ROTTERDAM_YEARS` | `4.5` | What Rotterdam bought in a month, in years of the city's output (Jerus, 2026-11-02). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 57 | `static int fails` |  |
| 58 | `static PrintStream out` |  |
| 59 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 55 | 392 | **type** `public class InterventionCheck` | Proves a purchase or sale of dollars moves the D$ a BOUNDED amount (XI1, 2026-11-06): linear up to INTERVENTION_LINEAR of a year's GDP, exactly as it always was, and logarithmic past it, INTERVENTION_IMPACT x s0 x (1 ... |
| 61 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 66 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 77 | 9 | `static void relative(String label, double actual, double expected, double share)` | Within a share of the expected figure, for sizes that run over many orders. |
| 88 | 9 | `static void same(String label, double actual, double expected)` | Equal to the bit. |
| 99 | 5 | `static void quietly(Runnable work)` | Runs a piece of work with the game's narration sent nowhere. |
| 120 | 1 | `static double moveAt(double share, double annual)` |  |
| 122 | 16 | `public static void main(String[] args) throws Exception` |  |

### 1. below the bound nothing moves, to the bit (lines 139-207)

| line | len | member | says |
|---:|---:|---|---|
| 141 | 66 | `static void belowTheBoundNothingMoves()` |  |

### 2. past it the rule is logarithmic (lines 208-283)

| line | len | member | says |
|---:|---:|---|---|
| 210 | 73 | `static void pastItTheRuleIsLogarithmic()` |  |

### 3. the inverse (lines 284-348)

| line | len | member | says |
|---:|---:|---|---|
| 286 | 62 | `static void theInverse()` |  |

### 4. Rotterdam's case (lines 349-414)

| line | len | member | says |
|---:|---:|---|---|
| 351 | 63 | `static void rotterdam()` |  |

### 5. how often it fires in a real run (lines 415-446)

| line | len | member | says |
|---:|---:|---|---|
| 417 | 29 | `static void howOftenItFires()` |  |

