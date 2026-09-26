# CarCheck.java - 733 lines · 6 methods · 0 constants · harnesses

`ham/citybuildersim/CarCheck.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> The cars: who buys one, what it costs them, and what it does to the road.
> 
> WHY THIS HARNESS EXISTS. Transport steps 1 to 4 split the road's demand into
> streams and gave the city three ways to serve it - highways, transit, rail -
> and every one of them was a RELIEF on a baseline that never moved. A car is
> the first thing in this game that makes the road worse, so for the first
> time the promise "an existing city computes exactly what it computed
> yesterday" is carried by a number that can be non-zero, and it needs holding
> down in its own file.
> 
> SIX CLAIMS.
> 
>   1. A CITY WITH NO CARS IS THE CITY IT WAS, to the bit - the load, the
>      ratio, the riders, and the pair of blends that serve a sector. Nothing
>      below matters if this fails, because it is the promise that every save
>      in existence still opens into the city it was saved from.
> 
>   2. THE PENALTY IS THE STRAIGHT LINE IT SAYS IT IS, it lands only on the
>      commuters who are still driving, and at saturation it is
>      CAR_LOAD_AT_SATURATION and not some emergent number.
> 
>   3. THE LOOP CLOSES. Jerus: "people drive until the road is full, then take
>      the tram." A motorised city with a clear road puts nobody on transit; as
>      the remembered commute worsens they come back, monotonically, and never
>      past the ceiling any city's transit share has.
> 
>   4. A CAR IS PAID FOR. What the households' savings lose is exactly what the
>      sellers and the world were paid, in the same month - the money identity
>      this codebase exists to keep - and the imported half is declared to the
>      audit, because Markets.draw() has no sector to book it against.
> 
>   5. THE FLEET IS A STOCK. It wears out at CAR_LIFE_MONTHS whether or not
>      anything can replace it, replacement is not throttled by the diffusion
>      rate, and ownership saturates at one per household rather than
>      plateauing on an accident of two constants.
> 
>   6. IT SURVIVES A SAVE, both ways: a city reloads with the fleet, the
>      ownership rate and the remembered commute it was saved with, and a save
>      from before cars existed reloads owning none.
> 
> See claude/the-fifth-link.md, HouseholdBalance's cars section and
> InfrastructureManager's.

**Uses:** [HouseholdBalance](HouseholdBalance.md) (40), [InfrastructureManager](InfrastructureManager.md) (22), [Household](Household.md) (13), [FamilyStructure](FamilyStructure.md) (12), [PayTier](PayTier.md) (12), [Traffic](Traffic.md) (4), [Game](Game.md) (4), [Good](Good.md) (3), [GameFiles](GameFiles.md) (2), [Founding](Founding.md) (1), [BuildingManager](BuildingManager.md) (1), [Bank](Bank.md) (1), [Equity](Equity.md) (1)

## Sections

| line | section |
|---:|---|
| 85 | · 1. A CITY WITH NO CARS |
| 121 | · 2. THE PENALTY |
| 186 | · 3. THE LOOP |
| 244 | · 4, 5, 6. IN A CITY |
| 362 | · WHY THE BAR IS 95% AND NOT 100%, and why it is not the plateau this |
| 407 | · THE DEPOSIT AND THE LOAN (2026-09-17) |
| 511 | · AND A FAMILY IN TROUBLE SELLS IT (2026-09-17) |

## Fields (state)

| line | field | says |
|---:|---|---|
| 49 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 47 | 687 | **type** `public class CarCheck` | The cars: who buys one, what it costs them, and what it does to the road. |
| 51 | 7 | `static void quietly(Runnable r)` |  |
| 59 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 64 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 70 | 3 | `static boolean same(double a, double b)` | Bitwise. |
| 74 | 8 | `static InfrastructureManager network()` |  |
| 83 | 650 | `public static void main(String[] args)` |  |

