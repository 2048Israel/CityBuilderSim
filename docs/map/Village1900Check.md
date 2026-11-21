# Village1900Check.java - 392 lines · 15 methods · 12 constants · harnesses

`ham/citybuildersim/Village1900Check.java` - generated 2026-11-21 by CodeMap; line numbers are as of that run.

> Why the 1900 city stalls (R1g's finding), and what R1g's two switches change: the playtest's village founded the 1900 way, played the
> playtest's way for five years beside its twin founded today's way. Not part of the game.
> 
> WHAT THIS HAS TO PROVE (R1g, 2026-11-19; docs/queue/R1g-notes.md, the cause and the fix). R1d's 1900 playtest stalled: 472 people at month
> 60 and 159 at month 900, against today's city's 1,674 and 23,180. It stalls on two things together, each of which the controls in the notes
> show is not enough alone, and neither is the dated money. Sections 1-4 are the village as R1g found it, both switches off:
> 
>   1. THE TWO CITIES: one founding, the playtest's (LongPlaytest.founding()), one of them founded the 1900 way, both founded and played the
>      way LongPlaytest.main() does it (its switches, the village by hand, the advisor at the run's stops, the automatic works)
>   2. THE RENT: 1901's wages pay for today's homes, so the 1900 village's households pay well over TARGET_RENT_BURDEN of their
>      take-home in rent, the affordability pull cuts the city its conditions support under the people already in it, and today's
>      twin pays no more than the target
>   3. THE SKILLS: posts drawn for today's diploma-holders meet 1901's workers, who mostly hold none: the 1900 village's diploma posts
>      outnumber its diploma workers many times over while today's twin's are covered, and the exporters' first plant (the
>      Fabrication Shop, whose 1900 recipe asks several times today's diploma crew) cannot pass the staffing gate,
>      Sector.MIN_STAFFABLE_TO_ORDER, which today's twin's passes
>   4. THE STALL: the 1900 village draws under STALLED of today's twin's arrivals at the end of the five years, and no
>      exporter stands there, its manufacturing held at the staffing gate; and the 1900 village's treasury holds its cash and owes
>      nothing, more cash a head than today's growing twin holds, so the dated money (no central bank until 1935) is not what holds it
> 
> ...and the fix, each half alone, at its switch (on unless -Dr1g.home=off or -Dr1g.apprentices=off; each section fails with its own off):
> 
>   5. THE 1900 HOME (Recipes.HOMES_1900): each of the 1900 village's homes is built at Recipes.homeScale() of today's twin's (the cash cost
>      as the catalogue states it, the materials and the points; the land not moved), its households pay under the stalled village's
>      rent, at most DEAR_RENT times the target, and it draws at least STALLED of today's twin's arrivals at the end of the five years
>   6. THE APPRENTICES (PopulationManager.APPRENTICES_1900): the five years' apprentices are the teens who came of age times the year's
>      trade share (ArrivalMix.tradeShareAt()), there being no school, and the village holds more diploma workers than the stalled one
>   7. TODAY'S CITY DOES NOT MOVE: today's twin played with both switches on is today's twin played with both off, to the bit
> 
> Every fixture CAUSES its condition: the cities are founded on scratch folders by the playtest's own founding and played by the
> playtest's own advisor and automatic works; nothing in them is set by hand but the two switches. Figures are asserted against the
> model's own constants and methods (RealEstate.TARGET_RENT_BURDEN, Sector.MIN_STAFFABLE_TO_ORDER, Recipes.homeScale(),
> ArrivalMix.tradeShareAt()) and the twins' figures, with the margins below stated once.

**Uses:** [LongPlaytest](LongPlaytest.md) (28), [Game](Game.md) (9), [WageBand](WageBand.md) (6), [Recipes](Recipes.md) (5), [PopulationManager](PopulationManager.md) (5), [Sector](Sector.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (3), [RealEstate](RealEstate.md) (2), [ArrivalMix](ArrivalMix.md) (2), [TreasuryFund](TreasuryFund.md) (2), [JobType](JobType.md) (2), [Migration](Migration.md) (1), [Founding](Founding.md) (1), [GameFiles](GameFiles.md) (1), [AgeBand](AgeBand.md) (1), [BuildingsStacks](BuildingsStacks.md) (1)

## Sections

| line | section |
|---:|---|
| 129 | · 2 |
| 154 | · 3 |
| 179 | · 4 |
| 206 | · 5 |
| 238 | · 6 |
| 255 | · 7 |
| 273 | · the fixture |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 45 | `Village1900Check.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 55 | `Village1900Check.MONTHS` | `60` | The months the two cities are played: five years, the stall's start (R1d: 472 people at month 60, nobody arriving from month 55). |
| 58 | `Village1900Check.GATE_MONTH` | `30` | The month the exporters' first plant is weighed: the end of the playtest's first skip, its first look after the founding's months by hand. |
| 61 | `Village1900Check.QUIET_MONTHS` | `5` | The last months the arrivals are summed over: the sixth year's first months, when the 1900 village has all but stopped drawing anybody. |
| 64 | `Village1900Check.STALLED` | `0.1` | The share of today's twin's arrivals in the quiet months under which a village is stalled: a tenth (R1g measured 0 on 0.9.157, 2.6 of 35.5 on 0.9.159). |
| 67 | `Village1900Check.DEAR_RENT` | `1.5` | How many times TARGET_RENT_BURDEN the 1900 village's households pay at the least: one and a half (R1g measured 2.0 at month 60). |
| 70 | `Village1900Check.SHORT_OF_DIPLOMAS` | `2` | How many times its diploma workers the 1900 village's diploma posts number at the least: two (R1g measured 2.4 at month 60). |
| 73 | `Village1900Check.CUTTING_CREWS` | `3` | How many times today's twin's diploma crew the 1900 Fabrication Shop wants at the least: three (R2d's cutting crews, 248 for 62). |
| 76 | `Village1900Check.EXPORTER` | `"Manufacturing"` | The exporters' sector, the advisor's first word on the 1900 village's stall (R1g's probe: "could staff 0% of a Wire & Cable Works now"). |
| 79 | `Village1900Check.FIRST_PLANT` | `"Fabrication Shop"` | The exporters' first plant. |
| 82 | `Village1900Check.HOUSE` | `"House"` | The house the homes are weighed by: the village's own, 60 of them laid by hand at the founding. |
| 85 | `Village1900Check.HOME` | `Recipes.HOMES_1900, APPRENTICES = PopulationManager.APPRENTICES_1900` | The switches as this run was started: each fix section's city plays at its own, so -Dr1g.home=off or -Dr1g.apprentices=off takes it out. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 43 | `static int fails` |  |
| 44 | `static PrintStream out` |  |
| 89 | `final boolean the1900Way` |  |
| 90 | `double gateShare` |  |
| 91 | `double arrivalsLate` |  |
| 93 | `double apprentices, apprenticesExpected` | The apprentices of every month, and the coming-of-age teens times the year's trade share less the schools' diploma-holders. |
| 94 | `Game g` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 41 | 352 | **type** `public class Village1900Check` | Why the 1900 city stalls (R1g's finding), and what R1g's two switches change: the playtest's village founded the 1900 way, played the playtest's way for five years beside its twin founded today's way. |
| 47 | 1 | `static void check(String label, boolean ok)` |  |
| 49 | 4 | `static void check(String label, boolean ok, String detail)` |  |
| 88 | 9 | **type** `static final class Seen` | What one city shows at the gate month and at the end. |
| 95 | 1 | `Seen(boolean the1900Way)` _(in Village1900Check.Seen)_ |  |
| 98 | 30 | `public static void main(String[] args)` |  |

### 2 (lines 129-153)

| line | len | member | says |
|---:|---:|---|---|
| 131 | 22 | `static void rent(Seen y1900, Seen today)` |  |

### 3 (lines 154-178)

| line | len | member | says |
|---:|---:|---|---|
| 156 | 22 | `static void skills(Seen y1900, Seen today)` |  |

### 4 (lines 179-205)

| line | len | member | says |
|---:|---:|---|---|
| 181 | 24 | `static void stall(Seen y1900, Seen today)` |  |

### 5 (lines 206-237)

| line | len | member | says |
|---:|---:|---|---|
| 208 | 29 | `static void home(Seen home, Seen stalled, Seen today)` |  |

### 6 (lines 238-254)

| line | len | member | says |
|---:|---:|---|---|
| 240 | 14 | `static void apprentices(Seen taught, Seen stalled)` |  |

### 7 (lines 255-272)

| line | len | member | says |
|---:|---:|---|---|
| 257 | 15 | `static void todayUnmoved(Seen on, Seen off)` |  |

### the fixture (lines 273-392)

| line | len | member | says |
|---:|---:|---|---|
| 283 | 65 | `static Seen play(String label, boolean the1900Way, boolean home, boolean apprentices)` | A city founded on the playtest's founding, the 1900 way or today's, founded and played as LongPlaytest.main() founds and plays it: the same switches, the automatic works on before the village is laid, the founding's m... |
| 350 | 12 | `static void months(Seen seen, int n)` | Up to n of the player's months (LongPlaytest.runPlayer()), never past MONTHS: the gate weighed at the end of GATE_MONTH, the last arrivals summed. |
| 364 | 10 | `static void weigh(Seen seen)` | The staffing gate on the exporters' first plant, and the diploma posts it wants. |
| 376 | 6 | `static double[] postsByBand(Game g)` | The city's posts by wage band, as the playtest's labour trace adds them. |
| 384 | 8 | `static long standing(Game g, String sector)` | How many buildings of a sector stand. |

