# HomeFuel.java - 171 lines · 20 methods · 2 constants · model

`ham/citybuildersim/HomeFuel.java` - generated 2026-11-13 by CodeMap; line numbers are as of that run.

> The homes' lamp oil and coal: what the households that light with a lamp and heat with a stove ask of the shops each month, as two shares of the homes.
> 
> ==================== THE HOME FORM (R0c, 2026-11-05; research-1900 section 4 and 5.3) ====================
> 
> A 1900 home was lit by a lamp burning kerosene (JET, the kerosene cut) and heated by a coal stove, and the
> family of five spent about $1.50 a week on both: coal oil at 24 cents a gallon, a fifth of the bill for light
> (1.06% against 4.19% of a working family's spending, US 1901, [P1.15]; [CYB18]), the rest for coal and wood
> (R0e reads the same budget's quantities for the two dials below; the game has no firewood good). Two shares,
> 0..1, of the homes run the 1900 way: lampShare, the homes lit by lamp oil and not by the grid, and coalShare,
> the homes heated by a coal stove. At 0, the default and every played city in R0c, nothing here runs, nothing
> is drawn or told and the month is today's, to the bit.
> 
> At a share above 0 the month asks litres = lampShare x homes x LAMP_OIL_LITRES_A_HOME_MONTH of JET and tonnes =
> coalShare x homes x COAL_TONNES_A_HOME_MONTH of THERMAL_COAL (strike(), at the drivers' fuel, Game 6d); Retail
> sells them to the households at the pump's rule, each on its own wholesale (Retail.sellHomeFuel()), the city's
> refiners and mines first and the world for the rest; and the households pay the bill like the drivers' fuel,
> split over the homes (HouseholdAccounts.setHomeFuel()). "The homes" are the doors the households pay rent
> on (Game.homeFuelHomes(): the sum of the rows' doors, HouseholdAccounts.rowDoors), the figure the bill is
> split over, so a flat two families share has one stove.
> 
> WHY A SHARE: the electric light and the gas range spread when they paid, and nothing in the game needs to
> schedule that; R0e (the household's basket by year) sets the shares from the year, in a city founded the 1900
> way (Game.basketHearsTheYear()), and sizes the two goods; a city founded the default way leaves them at 0, and
> a harness may set them. The shares are not saved: they follow the year, which the month and the founding give
> back on a load. The held sale is (heldSale(), restoreSale(); DataSave.homeFuelSale, R0e), since it is struck at
> 6d and charged to the households the month after, and a save between the two must carry it.
> 
> WHY NO POWER BILL: "electrified homes buy power instead", and today's homes pay nothing for power
> (EconomyManager.setElectricityConsumption(): a household utility bill "is on the list"), so the grid's draw
> for the homes is not touched here and no power bill is added: a home that leaves the lamp for the grid
> costs the grid what it did, and costs the household nothing more, until the utility bill exists.

**Uses:** [Game](Game.md) (1), [Retail](Retail.md) (1)

**Used by (3):** [BasketYearCheck](BasketYearCheck.md), [Forms1900Check](Forms1900Check.md), [Game](Game.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 45 | `HomeFuel.LAMP_OIL_LITRES_A_HOME_MONTH` | `4.54609 * 52 / 12` | Lamp oil a home lit by kerosene burned a month, in litres: the Department of Labour's family budget, December 1900, buys a gallon of coal oil a week (at 24.0 cents; Canada Year Book 1918, p. |
| 52 | `HomeFuel.COAL_TONNES_A_HOME_MONTH` | `0.125 * 52 / 12 * 0.90718474` | Coal a home heated by a coal stove burned a month, in tonnes: the same budget buys a sixteenth of a short ton of anthracite and a sixteenth of bituminous a week, beside an eighth of a cord of wood (a wood-heated home,... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 55 | `private double lampShare` | The share of the homes lit by lamp oil, 0 to 1: 0 until the year (R0e) or a harness sets it; not saved, it follows the year. |
| 58 | `private double coalShare` | The share of the homes heated by a coal stove, 0 to 1: 0 until the year (R0e) or a harness sets it; not saved, it follows the year. |
| 61 | `private double homes, litres, tonnes, bill, cost, imported` | The month's sale as struck (strike()): the homes it was struck for, the litres of JET and tonnes of THERMAL_COAL sold, the bill the households are charged, the wholesale cost and the part of it paid to the world. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 38 | 134 | **type** `public final class HomeFuel` | The homes' lamp oil and coal: what the households that light with a lamp and heat with a stove ask of the shops each month, as two shares of the homes. |
| 64 | 3 | `public void setLampShare(double s)` | Sets the lamp share, clamped to [0, 1] (not a number reads 0). |
| 69 | 3 | `public void setCoalShare(double s)` | Sets the coal share, clamped to [0, 1] (not a number reads 0). |
| 73 | 1 | `public double getLampShare()` |  |
| 75 | 1 | `public double getCoalShare()` |  |
| 78 | 1 | `public boolean isOn()` | Whether either share is above 0: false in every played city in R0c, and then nothing here is called. |
| 81 | 1 | `public boolean hasSale()` | Whether a sale is held from a month struck while a share was set (the households are yet to be told it, or a share was since set to 0 and strike() clears it). |
| 84 | 3 | `public double lampOilLitres(double homes)` | The litres of lamp oil (JET) the homes ask for a month: the lamp share of the homes at LAMP_OIL_LITRES_A_HOME_MONTH. |
| 89 | 3 | `public double coalTonnes(double homes)` | The tonnes of coal (THERMAL_COAL) the homes ask for a month: the coal share of the homes at COAL_TONNES_A_HOME_MONTH. |
| 98 | 12 | `void strike(Game game, double homes)` | The homes' month, struck at the drivers' fuel (Game 6d): the month's sale is zeroed, then what the shares ask of `homes` homes is sold through Retail (Retail.sellHomeFuel()) and kept for the households' books. |
| 112 | 1 | `public double getHomes()` | The homes the month's sale was struck for (the doors the households pay rent on, at the strike): 0 when nothing was asked. |
| 115 | 1 | `public double getLitres()` | The month's litres of lamp oil sold to the homes. |
| 118 | 1 | `public double getTonnes()` | ...and tonnes of coal. |
| 121 | 1 | `public double getBill()` | ...what the households were charged for both: the shop's two sales' values. |
| 124 | 1 | `public double getCost()` | ...what Retail paid for them at wholesale, the city's refiners and mines and the world together. |
| 127 | 1 | `public double getImported()` | ...and of that cost what Retail paid the world: its import. |
| 133 | 4 | `void reset()` | Both shares and the held sale back to 0, as a new city has them (Game.buildWorld()): this object outlives the city, and a share the last city (founded the 1900 way, R0e) set must not burn lamp oil in the next, nor its... |
| 143 | 3 | `double[] heldSale()` | The held sale as six numbers {homes, litres, tonnes, bill, cost, imported}, a fresh array, for the save: null when no sale is held (hasSale() false), so a month that struck nothing writes nothing. |
| 151 | 9 | `void restoreSale(double[] v)` | Puts back a sale from heldSale(), as a load does. |
| 161 | 3 | `private static double finite(double d)` |  |
| 166 | 5 | `void redenominate(double scale)` | The held sale, divided into the new unit in a currency reform (Game.redenominate()), as the drivers' fuel is. |

