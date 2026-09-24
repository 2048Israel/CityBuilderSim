# SocialSecurity.java - 150 lines · 9 methods · 4 constants · model

`ham/citybuildersim/SocialSecurity.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> Contributions off every wage, and a pension for everyone too old to work.
> 
> ==================== WHY THIS EXISTS ====================
> 
> Splitting the household books by pay tier put a row on screen that read:
> 
>     Retired (no earner)   147 homes   203 people   earned $0   -$95.8k
> 
> A seventh of the city's households had NO INCOME AT ALL. Not a low income - no
> pension, no savings to draw on, no family supporting them, nothing. They paid
> rent and bought food out of money that did not exist, and the only reason the
> city's books balanced was that nobody was tracking whose money it was.
> 
> Jerus: "this is going to hurt... but add cpp to everyones pay, aka they pay a
> tad, and make it so that the government pays for the seniors."
> 
> =========================================================
> 
> TWO HALVES, AND THEY DELIBERATELY DO NOT BALANCE.
> 
> Contributions are a slice of every wage. The pension is a flat amount paid to
> every senior. The first does not cover the second and is not meant to: the
> city carries the difference out of general revenue, which is exactly how the
> real thing works (CPP is contributory, Old Age Security is not) and exactly
> what Jerus asked for - the workers pay a tad, the government pays for the
> seniors.
> 
> The size of that gap is the interesting number, and coverage() puts it on
> screen rather than burying it. A city whose pyramid ages watches it widen
> without anything else changing, which is the first time the age structure has
> had a cost attached to it.
> 
> WHAT IT IS NOT
> 
> Not a fund. Nothing is invested, nothing accumulates, and this month's
> contributions pay this month's pensions - pay-as-you-go, which is the
> simplest honest version and the one whose failure mode (too few workers per
> pensioner) is the one worth modelling in a city builder.
> 
> Not earnings-related. Every senior gets the same, whatever they earned, which
> is a flat pension of the OAS kind. An earnings-related pension would need a
> contribution history per person, and nobody in this model is a person.

**Uses:** [PayTier](PayTier.md) (1)

**Used by (4):** [EconomyManager](EconomyManager.md), [HouseholdAccounts](HouseholdAccounts.md), [HouseholdCheck](HouseholdCheck.md), [TaxPolicy](TaxPolicy.md)

## Sections

| line | section |
|---:|---|
| 49 | · the dials |
| 91 | · the arithmetic |
| 93 | · THE RATES COME IN AS ARGUMENTS NOW. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 63 | `SocialSecurity.DEFAULT_CONTRIBUTION_RATE` | `.0595` | Taken off every wage, at the real CPP employee rate. |
| 70 | `SocialSecurity.CONTRIBUTION_RATE` | `DEFAULT_CONTRIBUTION_RATE` | Kept as the default so a screen or a harness written against the constant still names the right number. |
| 86 | `SocialSecurity.DEFAULT_PENSION_REPLACEMENT` | `.45` | What the pension replaces, as a share of an unskilled wage. |
| 89 | `SocialSecurity.PENSION_REPLACEMENT` | `DEFAULT_PENSION_REPLACEMENT` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 47 | 104 | **type** `public class SocialSecurity` | Contributions off every wage, and a pension for everyone too old to work. |

### the dials (lines 49-90)

### the arithmetic (lines 91-92)

### THE RATES COME IN AS ARGUMENTS NOW. (lines 93-150)

| line | len | member | says |
|---:|---:|---|---|
| 103 | 3 | `public static double pensionPerSenior()` | What one pensioner receives a month, at the default rate. |
| 107 | 3 | `public static double pensionPerSenior(double replacement)` |  |
| 112 | 3 | `public static double contributionsOn(double wageBill)` | Taken off the month's wage bill, at the default rate. |
| 116 | 3 | `public static double contributionsOn(double wageBill, double rate)` |  |
| 121 | 3 | `public static double pensionsFor(double seniors)` | Paid out to everyone over the retirement age, at the default rate. |
| 125 | 3 | `public static double pensionsFor(double seniors, double replacement)` |  |
| 138 | 5 | `public static double coverage(double wageBill, double seniors)` | How much of the pension bill the contributions actually cover, 0-1. |
| 145 | 3 | `public static double shortfall(double wageBill, double seniors)` | What general revenue has to find, over and above the contributions. |
| 149 | 1 | `private SocialSecurity()` |  |

