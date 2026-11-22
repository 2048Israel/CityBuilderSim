# InterventionCheck.java - 607 lines · 15 methods · 6 constants · harnesses

`ham/citybuildersim/InterventionCheck.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

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
>   5. HOW OFTEN IT FIRES IN A REAL RUN. The bound by arithmetic and the
>      lean's sale by a forced deficit (FS10, bare books): the most the rule
>      sells is MAX_ABSORPTION x the deficit, under the bound from a deficit
>      just under INTERVENTION_LINEAR / MAX_ABSORPTION of a year's output and
>      past it from one just over, the lean selling in both. Then the default
>      town, Lean, nobody dealing, played 240 months: no reprice's defence
>      passes the bound, the premise of the byte-identical claim.
> 
>   6. THE MONTH'S DEALING IS ONE SUM (XI2, the model Trade's warning reads).
>      Two pieces of 9% of a year's output each are under the bound and 18%
>      together are over it: dealtYears() and yearsOfOutputWith() read the sum;
>      dealEffectOnTop() is the rule of the sum less the rule of what is dealt,
>      less than the piece alone moves it, and the effects of the pieces add to
>      the rule of their sum, which is what the reprice feels. Under a peg the
>      rate is held at the peg, the float's own rate behind it moves by the rule
>      of the sum (against a twin that dealt nothing) and the vault sells the
>      dollars to hold it; under a band the rate stops at the weak edge once the
>      move would carry it past, and moves inside the edges when it would not.
> 
> Numbering is the running order. Money is in thousands, as everywhere.

**Uses:** [ForeignAccounts](ForeignAccounts.md) (65), [CurrencyRegimeCheck](CurrencyRegimeCheck.md) (25), [ExchangeRateCheck](ExchangeRateCheck.md) (18), [CurrencyCheck](CurrencyCheck.md) (4), [Founding](Founding.md) (3), [Game](Game.md) (3)

## Sections

| line | section |
|---:|---|
| 155 | 1. below the bound nothing moves, to the bit |
| 224 | 2. past it the rule is logarithmic |
| 300 | 3. the inverse |
| 365 | 4. Rotterdam's case |
| 431 | 5. how often it fires in a real run |
| 518 | 6. the month's dealing is one sum (XI2) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 121 | `InterventionCheck.IMPACT` | `ForeignAccounts.INTERVENTION_IMPACT, LINEAR = ForeignAccounts.INTERVENTION_LI...` | The dials, by their short names. |
| 124 | `InterventionCheck.SEED` | `ExchangeRateCheck.SEED` | The market's seed in the sections that draw one (ExchangeRateCheck's). |
| 127 | `InterventionCheck.RUN_MONTHS` | `240` | Months of the real run in section 5. |
| 130 | `InterventionCheck.YEAR` | `12` | The year a played town trades before its test month in section 4. |
| 133 | `InterventionCheck.ROTTERDAM_YEARS` | `4.5` | What Rotterdam bought in a month, in years of the city's output (Jerus, 2026-11-02). |
| 434 | `InterventionCheck.JUST` | `.1` | FS10: how far either side of the line a forced deficit of section 5 stands, as a share of the deficit whose largest sale is exactly INTERVENTION_LINEAR of a year's output. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 72 | `static int fails` |  |
| 73 | `static PrintStream out` |  |
| 74 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 70 | 538 | **type** `public class InterventionCheck` | Proves a purchase or sale of dollars moves the D$ a BOUNDED amount (XI1, 2026-11-06): linear up to INTERVENTION_LINEAR of a year's GDP, exactly as it always was, and logarithmic past it, INTERVENTION_IMPACT x s0 x (1 ... |
| 76 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 81 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 92 | 9 | `static void relative(String label, double actual, double expected, double share)` | Within a share of the expected figure, for sizes that run over many orders. |
| 103 | 9 | `static void same(String label, double actual, double expected)` | Equal to the bit. |
| 114 | 5 | `static void quietly(Runnable work)` | Runs a piece of work with the game's narration sent nowhere. |
| 135 | 1 | `static double moveAt(double share, double annual)` |  |
| 137 | 17 | `public static void main(String[] args) throws Exception` |  |

### 1. below the bound nothing moves, to the bit (lines 155-223)

| line | len | member | says |
|---:|---:|---|---|
| 157 | 66 | `static void belowTheBoundNothingMoves()` |  |

### 2. past it the rule is logarithmic (lines 224-299)

| line | len | member | says |
|---:|---:|---|---|
| 226 | 73 | `static void pastItTheRuleIsLogarithmic()` |  |

### 3. the inverse (lines 300-364)

| line | len | member | says |
|---:|---:|---|---|
| 302 | 62 | `static void theInverse()` |  |

### 4. Rotterdam's case (lines 365-430)

| line | len | member | says |
|---:|---:|---|---|
| 367 | 63 | `static void rotterdam()` |  |

### 5. how often it fires in a real run (lines 431-517)

| line | len | member | says |
|---:|---:|---|---|
| 441 | 7 | `static ForeignAccounts forcedDeficit(double deficitUsd)` | FS10: a month the rule sells all it can, on bare books: CurrencyCheck.defended()'s currency with a vault at six months' cover (the capacity there is, MAX_ABSORPTION), a chronic deficit of this many US$ (so the trailin... |
| 449 | 68 | `static void howOftenItFires()` |  |

### 6. the month's dealing is one sum (XI2) (lines 518-607)

| line | len | member | says |
|---:|---:|---|---|
| 521 | 3 | `static ForeignAccounts held(ForeignAccounts.Regime regime)` | The standard trade a currency settles on, the regimes' fixture (CurrencyRegimeCheck.book()), in this regime, quiet, with a vault no sale empties. |
| 525 | 82 | `static void theMonthsDealingIsOneSum()` |  |

