# CarCheck.java - 986 lines · 6 methods · 0 constants · harnesses

`ham/citybuildersim/CarCheck.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

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
> EIGHT CLAIMS.
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
>      past the ceiling any city's transit share has. Since 0.7.49 owners also
>      choose the bus on cost, so the city is every commuter with a car of
>      their own, driving at no fuel, at the default fare, and the jam's
>      three quarters are walked down by that fare's curve.
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
>   7. THE PLANTS' PAGE READS THE MONTH (B3, 0.7.47): what the car plants
>      made of their nameplate, and what they ordered, bought and imported of
>      their parts at the month's rate - the full-rate order is the note's.
> 
>   8. THE BUYER WEIGHS THE FARE (0.7.49): a household without a car buys one
>      only when its full monthly cost - the payment over its life at the
>      household rate, and a month of fuel - beats a month's pass; where the
>      fare deters nobody the ceiling on ownership is 1, and that town owns
>      more cars ten years on. Since 0.7.78 (batch O2) the month of fuel is
>      petrol on the research's wholesale ladder - since 0.7.83 at the
>      forecourts' pump price on it - so at the default fare fewer owners
>      take the bus on cost alone than 0.7.77's pump price sent there: cars
>      gain on transit.
> 
> See claude/the-fifth-link.md, HouseholdBalance's cars section and
> InfrastructureManager's.

**Uses:** [HouseholdBalance](HouseholdBalance.md) (43), [InfrastructureManager](InfrastructureManager.md) (29), [Good](Good.md) (15), [Game](Game.md) (13), [Household](Household.md) (13), [FamilyStructure](FamilyStructure.md) (12), [PayTier](PayTier.md) (12), [TaxPolicy](TaxPolicy.md) (9), [Sector](Sector.md) (7), [Motoring](Motoring.md) (7), [GameFiles](GameFiles.md) (6), [Traffic](Traffic.md) (5), [BuildingManager](BuildingManager.md) (5), [LongPlaytest](LongPlaytest.md) (5), [Founding](Founding.md) (4), [Formats](Formats.md) (2), [Bank](Bank.md) (1), [Equity](Equity.md) (1), [Automotive](Automotive.md) (1), [GoodsMarket](GoodsMarket.md) (1), [Retail](Retail.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1)

## Sections

| line | section |
|---:|---|
| 102 | · 1. A CITY WITH NO CARS |
| 138 | · 2. THE PENALTY |
| 203 | · 3. THE LOOP |
| 274 | · 4, 5, 6. IN A CITY |
| 392 | · WHY THE BAR IS 95% AND NOT 100%, and why it is not the plateau this |
| 437 | · THE DEPOSIT AND THE LOAN (2026-09-17) |
| 541 | · AND A FAMILY IN TROUBLE SELLS IT (2026-09-17) |
| 754 | · 7. THE PLANTS' PAGE READS THE MONTH (B3, 0.7.47) |
| 810 | · 8. THE BUYER WEIGHS THE FARE (0.7.49) |
| 925 | · THE PLAYTEST'S PLAYER ORDERS NO MORE LINES THAN ITS RIDERS FILL |

## Fields (state)

| line | field | says |
|---:|---|---|
| 66 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 64 | 923 | **type** `public class CarCheck` | The cars: who buys one, what it costs them, and what it does to the road. |
| 68 | 7 | `static void quietly(Runnable r)` |  |
| 76 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 81 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 87 | 3 | `static boolean same(double a, double b)` | Bitwise. |
| 91 | 8 | `static InfrastructureManager network()` |  |
| 100 | 886 | `public static void main(String[] args)` |  |

