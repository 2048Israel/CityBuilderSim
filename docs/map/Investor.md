# Investor.java - 44 lines · 6 methods · 0 constants · model

`ham/citybuildersim/Investor.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> Whoever is paying for a building.
> 
> Before this existed, processBuildOrder() ended in `cash -= totalCost` - the
> city paid for everything, and that one line was the only thing in the whole
> build path that cared who was buying. Materials, the construction queue and
> addStack() are all indifferent. So making businesses build their own premises
> is a matter of naming the payer, not of duplicating the build path.

**Used by (1):** [Game](Game.md)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 12 | 33 | **type** `public interface Investor` | Whoever is paying for a building. |
| 14 | 1 | `String getName()` |  |
| 16 | 1 | `double getCash()` |  |
| 19 | 1 | `void spend(double amount)` | Take the money. |
| 29 | 1 | `boolean canBorrow(double amount)` | Whether this investor can raise `amount` beyond its cash. |
| 32 | 1 | `void borrow(double amount, int month)` | Raise `amount` on credit. |
| 41 | 3 | `default void receive(double amount)` | Money coming the other way - selling a plot back to the city when the business scraps a building it can no longer afford to hold. |

