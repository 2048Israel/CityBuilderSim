# Village1900Check.java - 282 lines · 12 methods · 9 constants · harnesses

`ham/citybuildersim/Village1900Check.java` - generated 2026-11-19 by CodeMap; line numbers are as of that run.

> Why the 1900 city stalls (R1g's finding): the playtest's village founded the 1900 way, played the playtest's way for five years beside
> its twin founded today's way. Not part of the game.
> 
> WHAT THIS HAS TO PROVE (R1g, 2026-11-19; docs/queue/R1g-notes.md, the cause). R1d's 1900 playtest stalled: 472 people at month 60 and
> 159 at month 900, against today's city's 1,674 and 23,180. It stalls on two things together, each of which the controls in the notes
> show is not enough alone, and neither is the dated money:
> 
>   1. THE TWO CITIES: one founding, the playtest's (LongPlaytest.founding()), one of them founded the 1900 way, both played the way
>      LongPlaytest.probeCity() and main() play it (the same village, the advisor and the automatic works every month)
>   2. THE RENT: 1901's wages pay for today's homes, so the 1900 village's households pay well over TARGET_RENT_BURDEN of their
>      take-home in rent, the affordability pull cuts the city its conditions support under the people already in it, and today's
>      twin pays no more than the target
>   3. THE SKILLS: posts drawn for today's diploma-holders meet 1901's workers, who mostly hold none: the 1900 village's diploma posts
>      outnumber its diploma workers many times over while today's twin's are covered, and the exporters' first plant (the
>      Fabrication Shop, whose 1900 recipe asks several times today's diploma crew) cannot pass the staffing gate,
>      Sector.MIN_STAFFABLE_TO_ORDER, which today's twin's passes
>   4. THE STALL: nobody arrives in the 1900 village at the end of the five years while people still arrive in today's twin, and no
>      exporter stands there, its manufacturing held at the staffing gate; and the 1900 village's treasury holds its cash and owes
>      nothing while today's twin has borrowed, so the dated money (no central bank until 1935) is not what holds it
> 
> Every fixture CAUSES its condition: both cities are founded on scratch folders by the playtest's own founding and played by the
> playtest's own advisor and automatic works; nothing in them is set by hand. Figures are asserted against the model's own constants
> (RealEstate.TARGET_RENT_BURDEN, Sector.MIN_STAFFABLE_TO_ORDER) and the twin's figures, with the margins below stated once.

**Uses:** [LongPlaytest](LongPlaytest.md) (28), [Game](Game.md) (8), [Sector](Sector.md) (5), [WageBand](WageBand.md) (4), [TreasuryFund](TreasuryFund.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [JobType](JobType.md) (2), [RealEstate](RealEstate.md) (1), [Migration](Migration.md) (1), [Founding](Founding.md) (1), [GameFiles](GameFiles.md) (1), [BuildingsStacks](BuildingsStacks.md) (1)

## Sections

| line | section |
|---:|---|
| 100 | · 2 |
| 125 | · 3 |
| 150 | · 4 |
| 173 | · the fixture |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 35 | `Village1900Check.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 45 | `Village1900Check.MONTHS` | `60` | The months the two cities are played: five years, the stall's start (R1d: 472 people at month 60, nobody arriving from month 55). |
| 48 | `Village1900Check.GATE_MONTH` | `30` | The month the exporters' first plant is weighed: the end of the playtest's first skip, its first look after the founding's months by hand. |
| 51 | `Village1900Check.QUIET_MONTHS` | `5` | The last months the arrivals are summed over: the sixth year's first months, when the 1900 village has stopped drawing anybody. |
| 54 | `Village1900Check.DEAR_RENT` | `1.5` | How many times TARGET_RENT_BURDEN the 1900 village's households pay at the least: one and a half (R1g measured 2.0 at month 60). |
| 57 | `Village1900Check.SHORT_OF_DIPLOMAS` | `2` | How many times its diploma workers the 1900 village's diploma posts number at the least: two (R1g measured 2.4 at month 60). |
| 60 | `Village1900Check.CUTTING_CREWS` | `3` | How many times today's twin's diploma crew the 1900 Fabrication Shop wants at the least: three (R2d's cutting crews, 248 for 62). |
| 63 | `Village1900Check.EXPORTER` | `"Manufacturing"` | The exporters' sector, the advisor's first word on the 1900 village's stall (R1g's probe: "could staff 0% of a Wire & Cable Works now"). |
| 66 | `Village1900Check.FIRST_PLANT` | `"Fabrication Shop"` | The exporters' first plant. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 33 | `static int fails` |  |
| 34 | `static PrintStream out` |  |
| 70 | `final boolean the1900Way` |  |
| 71 | `double gateShare` |  |
| 72 | `double arrivalsLate` |  |
| 73 | `Game g` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 31 | 252 | **type** `public class Village1900Check` | Why the 1900 city stalls (R1g's finding): the playtest's village founded the 1900 way, played the playtest's way for five years beside its twin founded today's way. |
| 37 | 1 | `static void check(String label, boolean ok)` |  |
| 39 | 4 | `static void check(String label, boolean ok, String detail)` |  |
| 69 | 7 | **type** `static final class Seen` | What one city shows at the gate month and at the end. |
| 74 | 1 | `Seen(boolean the1900Way)` _(in Village1900Check.Seen)_ |  |
| 77 | 22 | `public static void main(String[] args)` |  |

### 2 (lines 100-124)

| line | len | member | says |
|---:|---:|---|---|
| 102 | 22 | `static void rent(Seen y1900, Seen today)` |  |

### 3 (lines 125-149)

| line | len | member | says |
|---:|---:|---|---|
| 127 | 22 | `static void skills(Seen y1900, Seen today)` |  |

### 4 (lines 150-172)

| line | len | member | says |
|---:|---:|---|---|
| 152 | 20 | `static void stall(Seen y1900, Seen today)` |  |

### the fixture (lines 173-282)

| line | len | member | says |
|---:|---:|---|---|
| 182 | 60 | `static Seen play(String label, boolean the1900Way)` | A city founded on the playtest's founding, the 1900 way or today's, founded and played as LongPlaytest.main() founds and plays it: the same switches, the automatic works on before the village is laid, the founding's m... |
| 244 | 8 | `static void months(Seen seen, int n)` | Up to n of the player's months (LongPlaytest.runPlayer()), never past MONTHS: the gate weighed at the end of GATE_MONTH, the last arrivals summed. |
| 254 | 10 | `static void weigh(Seen seen)` | The staffing gate on the exporters' first plant, and the diploma posts it wants. |
| 266 | 6 | `static double[] postsByBand(Game g)` | The city's posts by wage band, as the playtest's labour trace adds them. |
| 274 | 8 | `static long standing(Game g, String sector)` | How many buildings of a sector stand. |

