# HomeFuel.java - 131 lines · 16 methods · 2 constants · model

`ham/citybuildersim/HomeFuel.java` - generated 2026-11-07 by CodeMap; line numbers are as of that run.

> The homes' lamp oil and coal: what the households that light with a lamp and heat with a stove ask of the shops each month, as two shares of the homes.
> 
> ==================== THE HOME FORM (R0c, 2026-11-05; research-1900 section 4 and 5.3) ====================
> 
> A 1900 home was lit by a lamp burning kerosene (JET, the kerosene cut) and heated by a coal stove, and the
> family of five spent about $1.50 a week on both: coal oil at 24 cents a gallon, a fifth of the bill for light
> (1.06% against 4.19% of a working family's spending, US 1901, [P1.15]; [CYB18]), the rest for coal. Two shares,
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
> schedule that; R0e (the household's basket by year) sets the shares from the year and sizes the two goods;
> here they are built and off, and only a harness sets them. They are not saved (nothing sets them in a played
> city yet; R0e saves the bill when it goes on).
> 
> WHY NO POWER BILL: "electrified homes buy power instead", and today's homes pay nothing for power
> (EconomyManager.setElectricityConsumption(): a household utility bill "is on the list"), so the grid's draw
> for the homes is not touched here and no power bill is added: a home that leaves the lamp for the grid
> costs the grid what it did, and costs the household nothing more, until the utility bill exists.

**Uses:** [Game](Game.md) (1), [Retail](Retail.md) (1)

**Used by (2):** [Forms1900Check](Forms1900Check.md), [Game](Game.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 42 | `HomeFuel.LAMP_OIL_LITRES_A_HOME_MONTH` | `25` | Lamp oil a home lit by kerosene burned a month, in litres: light was a fifth of a working family's fuel and light (1.06% against 4.19% of spending, US 1901, [P1.15]), a fifth of research-1900 5.3's $1.50 a week at 24 ... |
| 48 | `HomeFuel.COAL_TONNES_A_HOME_MONTH` | `0.7` | Coal a home heated by a stove burned a month, in tonnes, averaged over the year: the rest of research-1900 5.3's fuel and light, about $1.20 a week, at about $6.50 a short ton (est., the price unverified: R0e sources ... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 51 | `private double lampShare` | The share of the homes lit by lamp oil, 0 to 1: 0 until a harness or R0e sets it; not saved. |
| 54 | `private double coalShare` | The share of the homes heated by a coal stove, 0 to 1: 0 until a harness or R0e sets it; not saved. |
| 57 | `private double homes, litres, tonnes, bill, cost, imported` | The month's sale as struck (strike()): the homes it was struck for, the litres of JET and tonnes of THERMAL_COAL sold, the bill the households are charged, the wholesale cost and the part of it paid to the world. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 35 | 97 | **type** `public final class HomeFuel` | The homes' lamp oil and coal: what the households that light with a lamp and heat with a stove ask of the shops each month, as two shares of the homes. |
| 60 | 3 | `public void setLampShare(double s)` | Sets the lamp share, clamped to [0, 1] (not a number reads 0). |
| 65 | 3 | `public void setCoalShare(double s)` | Sets the coal share, clamped to [0, 1] (not a number reads 0). |
| 69 | 1 | `public double getLampShare()` |  |
| 71 | 1 | `public double getCoalShare()` |  |
| 74 | 1 | `public boolean isOn()` | Whether either share is above 0: false in every played city in R0c, and then nothing here is called. |
| 77 | 1 | `public boolean hasSale()` | Whether a sale is held from a month struck while a share was set (the households are yet to be told it, or a share was since set to 0 and strike() clears it). |
| 80 | 3 | `public double lampOilLitres(double homes)` | The litres of lamp oil (JET) the homes ask for a month: the lamp share of the homes at LAMP_OIL_LITRES_A_HOME_MONTH. |
| 85 | 3 | `public double coalTonnes(double homes)` | The tonnes of coal (THERMAL_COAL) the homes ask for a month: the coal share of the homes at COAL_TONNES_A_HOME_MONTH. |
| 94 | 12 | `void strike(Game game, double homes)` | The homes' month, struck at the drivers' fuel (Game 6d): the month's sale is zeroed, then what the shares ask of `homes` homes is sold through Retail (Retail.sellHomeFuel()) and kept for the households' books. |
| 108 | 1 | `public double getHomes()` | The homes the month's sale was struck for (the doors the households pay rent on, at the strike): 0 when nothing was asked. |
| 111 | 1 | `public double getLitres()` | The month's litres of lamp oil sold to the homes. |
| 114 | 1 | `public double getTonnes()` | ...and tonnes of coal. |
| 117 | 1 | `public double getBill()` | ...what the households were charged for both: the shop's two sales' values. |
| 120 | 1 | `public double getCost()` | ...what Retail paid for them at wholesale, the city's refiners and mines and the world together. |
| 123 | 1 | `public double getImported()` | ...and of that cost what Retail paid the world: its import. |
| 126 | 5 | `void redenominate(double scale)` | The held sale, divided into the new unit in a currency reform (Game.redenominate()), as the drivers' fuel is. |

