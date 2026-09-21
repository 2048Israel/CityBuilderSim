# MonetaryCheck.java - 303 lines · 4 methods · 0 constants · harnesses

`ham/citybuildersim/MonetaryCheck.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> Money: what a basket costs, what the world charges, and what the rate does.
> 
> WHAT IS ACTUALLY BEING ASKED
> 
>   1. Does the price index measure what households BUY, on a basket fixed at a
>      base period? A CPI that re-weights as spending shifts shows no inflation
>      for a family that switched to cheaper food while eating worse.
> 
>   2. Do prices RATION? A shop that can meet a fifth of demand and charges
>      cost-plus is not a shop, it is a queue - and a model with no demand-pull
>      channel gives a policy rate nothing to cool.
> 
>   3. Is the world a real place? Its own inflation is the one price shock the
>      player cannot cause and cannot stop.
> 
>   4. And does the rate DO anything - to credit, to the currency, and to the
>      city that has to live with it?

**Uses:** [PriceIndex](PriceIndex.md) (12), [Retail](Retail.md) (8), [ForeignAccounts](ForeignAccounts.md) (7), [WorldEconomy](WorldEconomy.md) (6), [DebtManager](DebtManager.md) (6), [Game](Game.md) (5), [GameFiles](GameFiles.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2)

## Sections

| line | section |
|---:|---|
| 53 | · 1. the basket |
| 77 | · · the high and low water marks |
| 129 | · 2. prices ration |
| 149 | · 3. the world is a real place |
| 182 | · 4. and the rate does something |
| 227 | · 5. in a city, and across a reload |

## Fields (state)

| line | field | says |
|---:|---|---|
| 29 | `static int fails` |  |
| 30 | `static PrintStream out` |  |
| 31 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 27 | 277 | **type** `public class MonetaryCheck` | Money: what a basket costs, what the world charges, and what the rate does. |
| 33 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 38 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 48 | 248 | `public static void main(String[] args) throws Exception` |  |
| 297 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |

