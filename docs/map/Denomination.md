# Denomination.java - 194 lines · 12 methods · 3 constants · model

`ham/citybuildersim/Denomination.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> The currency's unit, and the power to lop zeros off it.
> 
> ==================== WHY THIS EXISTS ====================
> 
> Jerus: "inflation cause numbers to be huge in the span of 300 years... so we
> need a button to prevent that no? like still have inflation but perhaps even
> so often the player presses a button and everything gets divided by 10, or
> 100, or whatever, so that bread doesnt show as 300M and we start getting
> binary rounding issues all over the place."
> 
> Which is a currency reform, and it is what every country that has ever had a
> long run of inflation has actually done. France lopped two zeros in 1960,
> Germany three in 1923 and again in 1948, Brazil six times between 1967 and
> 1994, Turkey six zeros in 2005. It is not a display trick and it is not a
> cheat; it is a change of UNITS, and the whole content of this class is that
> it must be nothing else.
> 
> ==================== WHAT A REFORM IS ====================
> 
> One new Danzik dollar is worth `unit` founding Danzik dollars. It starts at
> one and multiplies by ten, a hundred or a thousand each time the player
> reforms. Everything nominal - every price, wage, balance, debt, reserve and
> exchange rate in the city - is divided by the same factor at the same moment,
> so that:
> 
>   - every RATIO is unchanged. The rent burden, the capital ratio, the tax
>     take as a share of GDP, the price index: none of them move.
>   - every REAL quantity is unchanged. The same number of people live in the
>     same houses and eat the same amount of bread.
>   - the city's own history is redrawn in the new unit, so the graphs do not
>     have a cliff in them.
> 
> DenominationCheck asserts exactly that, by running one city and a lopped copy
> of it side by side for years and requiring that they stay the same city. That
> assertion is the reason this is safe to ship: a reform that missed a single
> balance would show up as two cities that drifted apart.
> 
> ==================== THE CONSTANTS ARE STATE NOW ====================
> 
> The hard part is not the balances. It is that this codebase has money written
> into it as compile-time constants - a House costs $30, a shop's opening price
> is $0.30, a bank branch gathers $60,000 of deposits, a block of land starts at
> $70 - and a `static final` cannot be divided by anything.
> 
> Left alone they would be a hundred times dearer in real terms the morning
> after a hundred-to-one reform, which is not a rounding error, it is a
> different game. So every money constant that is READ AGAINST LIVE MONEY has
> been turned into an instance field seeded from the constant, and those fields
> are redenominated with everything else. The constant survives as the founding
> value and the documentation of where the number came from.
> 
> Foreign prices are the exception and it is not an oversight: food at $0.20 and
> building materials at $2.00 abroad are quoted in DOLLARS OF THE REST OF THE
> WORLD, which no act of this city's parliament can change. They reach the city
> multiplied by the exchange rate, and the exchange rate is what gets divided.
> 
> ==================== WHEN THE BUTTON APPEARS ====================
> 
> Jerus asked for it to unlock past a threshold and then be the player's
> decision, which is also how it works in life: a currency reform is a
> ... (1 more lines in the source)

**Uses:** [Currency](Currency.md) (4)

**Used by (3):** [DenominationCheck](DenominationCheck.md), [Game](Game.md), [PolicyScreen](PolicyScreen.md)

## Sections

| line | section |
|---:|---|
| 178 | · carrying |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 94 | `Denomination.UNLOCK_AT` | `10.0` | How far prices have to have risen before the button appears. |
| 97 | `Denomination.FACTORS` | `{ 10, 100, 1000 }` | The factors the player may choose between. |
| 109 | `Denomination.MAX_UNIT` | `1e12` | The ceiling on the unit, and it is a numeric guard rather than a policy. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 77 | `private double unit` | How many FOUNDING dollars one of today's dollars is worth. |
| 80 | `private int reforms` | How many reforms this city has been through. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 68 | 127 | **type** `public class Denomination` | The currency's unit, and the power to lop zeros off it. |
| 111 | 1 | `public double getUnit()` |  |
| 112 | 1 | `public int getReforms()` |  |
| 121 | 3 | `public double money(double foundingDollars)` | A founding-dollar constant, expressed in today's money. |
| 126 | 3 | `public boolean unlocked(double priceIndex)` | Whether the reform button should be offered, given the city's price level. |
| 131 | 3 | `public boolean canLop(double factor)` | Whether a particular factor may be applied. |
| 140 | 5 | `public void lop(double factor)` | Records the reform. |
| 154 | 4 | `public String name()` | What the money is called, which changes when it is reformed. |
| 160 | 4 | `public String describeUnit()` | What one of today's dollars is worth in founding money, for the screen. |
| 165 | 12 | `private static String ordinal(int n)` |  |

### carrying (lines 178-194)

| line | len | member | says |
|---:|---:|---|---|
| 180 | 3 | `public double[] toSaveArray()` |  |
| 184 | 5 | `public void restore(double[] saved)` |  |
| 190 | 4 | `public void reset()` |  |

