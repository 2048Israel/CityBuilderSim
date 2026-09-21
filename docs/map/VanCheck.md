# VanCheck.java - 305 lines · 5 methods · 0 constants · harnesses

`ham/citybuildersim/VanCheck.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> The vans: what a sector needs, what it costs it, and what happens while it
> waits for them.
> 
> THE FIFTH THROTTLE, and the first one a business buys. Energy, water, road
> and health arrive from outside - the city builds the plants, lays the streets
> and staffs the clinics - and a sector takes whatever it is given. A lorry is
> the sector's own capital: it decides how many it needs, it pays for them, it
> replaces them when they wear out, and if it cannot get them its output falls.
> 
> Jerus asked for exactly that: "a constraint - a sector with too few vans can't
> move what it makes; its operating rate falls."
> 
> FIVE CLAIMS.
> 
>   1. A SECTOR WITH VEHICLES ENOUGH IS THE SECTOR IT WAS, to the bit, and so
>      is one with nothing to move. Both are early returns rather than
>      arithmetic, because have/need is 1 to within an ulp and not 1, and an
>      ulp in an operating rate is a different city.
> 
>   2. THE FLEET IS SIZED BY WHAT THE PLANT MOVES - everything it makes and
>      everything it buys, by weight, at NAMEPLATE rather than at this month's
>      rate, which would be a circle.
> 
>   3. IT IS A STOCK. It wears out at VAN_LIFE_MONTHS whether or not anything
>      can replace it, so a Commercial Vehicle Plant has a customer next year
>      as well as this one - and a sector that can no longer buy loses its
>      fleet over the life of a lorry rather than overnight.
> 
>   4. YOU CANNOT MOBILISE INSTANTLY, which is the whole of what makes this a
>      constraint rather than a bill. Built without the delivery cap it was
>      measured and every ratio in the city read 100.0%: vans are importable,
>      and a sector asking for a whole fleet got a whole fleet in one month,
>      every time.
> 
>   5. AND IT NEVER STOPS A BUSINESS DEAD. A firm short of its own lorries
>      hires haulage and sends fuller loads. The same shape as the road's own
>      floor, and for the same reason.
> 
> See claude/the-sixth-link.md and Sector's THE FLEET.

**Uses:** [Good](Good.md) (23), [Sector](Sector.md) (16), [Game](Game.md) (4), [GameFiles](GameFiles.md) (2), [SectorState](SectorState.md) (2), [BuildingManager](BuildingManager.md) (1), [GoodsMarket](GoodsMarket.md) (1)

## Sections

| line | section |
|---:|---|
| 99 | · 1. WHAT A FLEET IS FOR |
| 141 | · 2. THE FLOOR AND THE CEILING OF THE RATIO |
| 171 | · 3. YOU CANNOT PUT A FLEET ON THE ROAD IN A MONTH |
| 192 | · 4. IT WEARS OUT |
| 227 | · 5. A SAVE FROM BEFORE VANS HAD VANS |
| 257 | · 6. IN A CITY THAT RUNS |

## Fields (state)

| line | field | says |
|---:|---|---|
| 46 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 44 | 262 | **type** `public class VanCheck` | The vans: what a sector needs, what it costs it, and what happens while it waits for them. |
| 48 | 5 | `static void quietly(Runnable r)` |  |
| 54 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 59 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 65 | 3 | `static boolean same(double a, double b)` | Bitwise. |
| 69 | 236 | `public static void main(String[] args)` |  |

