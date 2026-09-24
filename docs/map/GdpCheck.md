# GdpCheck.java - 405 lines · 3 methods · 0 constants · harnesses

`ham/citybuildersim/GdpCheck.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> Verifies the national accounts: the identity, growth rates, and the government's books.

**Uses:** [NationalAccounts](NationalAccounts.md) (35), [Game](Game.md) (4), [GameFiles](GameFiles.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (1), [Debt](Debt.md) (1)

## Sections

| line | section |
|---:|---|
| 24 | · 1. the identity |
| 41 | · 2. stock is a CHANGE |
| 53 | · 3. THE BUG THIS REPLACES |
| 68 | · 3c. the two ways it went negative anyway |
| 224 | · 3b. exports, the first the city has ever had |
| 252 | · 4. annual and per capita |
| 270 | · 5. growth |
| 295 | · 6. government books |
| 327 | · 4. THE INTEREST LINE IS REAL |

## Fields (state)

| line | field | says |
|---:|---|---|
| 6 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 4 | 402 | **type** `public class GdpCheck` | Verifies the national accounts: the identity, growth rates, and the government's books. |
| 8 | 6 | `static void check(String label, double actual, double expected)` |  |
| 15 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 20 | 385 | `public static void main(String[] args)` |  |

