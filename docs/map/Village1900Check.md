# Village1900Check.java - 565 lines · 19 methods · 12 constants · harnesses

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
>      twin pays under DEAR_RENT times the target, the line the village is over (0.331 at month 60 on 0.9.174, 0.232 on 0.9.159)
>   3. THE SKILLS: posts drawn for today's diploma-holders meet 1901's workers, who mostly hold none: the 1900 village's diploma posts
>      outnumber its diploma workers many times over while today's twin's are covered, and the exporters' first plant (the
>      Fabrication Shop, whose 1900 recipe asks several times today's diploma crew) cannot pass the staffing gate,
>      Sector.MIN_STAFFABLE_TO_ORDER, which today's twin's passes
>   4. THE STALL: the 1900 village draws under STALLED of today's twin's arrivals at the end of the five years, and no
>      exporter stands there, its manufacturing held at the staffing gate; and the 1900 village's treasury holds its cash and owes
>      nothing, more cash a head than today's growing twin holds, so the dated money (no central bank until 1935) is not what holds it
> 
> ...and the fix, at its switches (on unless -Dr1g.home=off, -Dr1g.apprentices=off or -Dr1g.walk=off; each section fails with its own off):
> 
>   5. THE 1900 HOME (Recipes.HOMES_1900): each of the 1900 village's homes is built at the scale it stands at (Recipes.datedScale();
>      homeScale() with the walk off) of today's twin's (the cash cost as the catalogue states it, the materials and the points; the land not
>      moved), its households pay under the stalled village's rent, at most DEAR_RENT times the target, and it draws at least STALLED of
>      today's twin's arrivals at the end of the five years
>   6. THE APPRENTICES (PopulationManager.APPRENTICES_1900): the five years' apprentices are the teens who came of age times the year's
>      trade share (ArrivalMix.tradeShareAt()), there being no school, and the village holds more diploma workers than the stalled one
>   7. TODAY'S CITY DOES NOT MOVE: today's twin played with both switches on is today's twin played with both off, to the bit
>   8. THE WALK (Recipes.HOME_WALK; Jerus, 2026-11-20: the home's "cost moves with WAGES"): each month of the 1900 home's village takes the
>      households' pay once (Recipes.homeScaleOfPay() as the month before ended), the window holding the last Recipes.PAY_MONTHS, and its
>      House stands at their mean, which the village's pay has moved off homeScale(); the months are saved, so the village loaded into a
>      fresh Game from its scratch folder stands where it stood, and a month on, where the unsaved one does, to the bit
>   9. THE LAST HORSE: a farm's crop feeds its horses, then fitting every tractor takes the horse share to zero and the reused output
>      row clears its captive feed (the 1900 played city's reload fault, mended in Sector).
>  10. SCHOOL DIPLOMAS COVER THE QUOTA BEFORE DEGREE OUTFLOW: on the actual Game month, gross school completions cover the apprentice
>      quota while college/university completions take net diploma movement below it; apprentices add nobody and the skilled heads
>      equal an identically founded control's month with apprentices off.
> 
> Every fixture CAUSES its condition: the cities are founded on scratch folders by the playtest's own founding and played by the
> playtest's own advisor and automatic works; section 9 sets fitted counts and section 10 seeds a funded school town and finishing
> students through the existing fixtures' model restores. Figures are asserted against the
> model's own constants and methods (RealEstate.TARGET_RENT_BURDEN, Sector.MIN_STAFFABLE_TO_ORDER, Recipes.homeScale(),
> ArrivalMix.tradeShareAt()) and the twins' figures, with the margins below stated once.

**Uses:** [LongPlaytest](LongPlaytest.md) (35), [Game](Game.md) (18), [Recipes](Recipes.md) (15), [EducationCheck](EducationCheck.md) (12), [PopulationManager](PopulationManager.md) (10), [WageBand](WageBand.md) (10), [BuildingsTemplate](BuildingsTemplate.md) (7), [Sector](Sector.md) (6), [AgeBand](AgeBand.md) (4), [Good](Good.md) (4), [EducationType](EducationType.md) (4), [ArrivalMix](ArrivalMix.md) (3), [GameFiles](GameFiles.md) (3), [BuildingsStacks](BuildingsStacks.md) (3), [RealEstate](RealEstate.md) (2), [TreasuryFund](TreasuryFund.md) (2), [JobType](JobType.md) (2), [Migration](Migration.md) (1), [Founding](Founding.md) (1), [PriceLevel](PriceLevel.md) (1), [Sectors](Sectors.md) (1), [Education](Education.md) (1)

## Sections

| line | section |
|---:|---|
| 146 | · 2 |
| 172 | · 3 |
| 197 | · 4 |
| 224 | · 5 |
| 256 | · 6 |
| 273 | · 7 |
| 292 | · 8 |
| 350 | · the fixture |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 56 | `Village1900Check.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 66 | `Village1900Check.MONTHS` | `60` | The months the two cities are played: five years, the stall's start (R1d: 472 people at month 60, nobody arriving from month 55). |
| 69 | `Village1900Check.GATE_MONTH` | `30` | The month the exporters' first plant is weighed: the end of the playtest's first skip, its first look after the founding's months by hand. |
| 72 | `Village1900Check.QUIET_MONTHS` | `5` | The last months the arrivals are summed over: the sixth year's first months, when the 1900 village has all but stopped drawing anybody. |
| 75 | `Village1900Check.STALLED` | `0.1` | The share of today's twin's arrivals in the quiet months under which a village is stalled: a tenth (R1g measured 0 on 0.9.157, 2.6 of 35.5 on 0.9.159). |
| 78 | `Village1900Check.DEAR_RENT` | `1.5` | How many times TARGET_RENT_BURDEN the 1900 village's households pay at the least: one and a half (R1g measured 2.0 at month 60). |
| 81 | `Village1900Check.SHORT_OF_DIPLOMAS` | `2` | How many times its diploma workers the 1900 village's diploma posts number at the least: two (R1g measured 2.4 at month 60). |
| 84 | `Village1900Check.CUTTING_CREWS` | `3` | How many times today's twin's diploma crew the 1900 Fabrication Shop wants at the least: three (R2d's cutting crews, 248 for 62). |
| 87 | `Village1900Check.EXPORTER` | `"Manufacturing"` | The exporters' sector, the advisor's first word on the 1900 village's stall (R1g's probe: "could staff 0% of a Wire & Cable Works now"). |
| 90 | `Village1900Check.FIRST_PLANT` | `"Fabrication Shop"` | The exporters' first plant. |
| 93 | `Village1900Check.HOUSE` | `"House"` | The house the homes are weighed by: the village's own, 60 of them laid by hand at the founding. |
| 96 | `Village1900Check.HOME` | `Recipes.HOMES_1900, APPRENTICES = PopulationManager.APPRENTICES_1900` | The switches as this run was started: each fix section's city plays at its own, so -Dr1g.home=off or -Dr1g.apprentices=off takes it out. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 54 | `static int fails` |  |
| 55 | `static PrintStream out` |  |
| 100 | `final boolean the1900Way` |  |
| 101 | `double gateShare` |  |
| 102 | `double arrivalsLate` |  |
| 104 | `double apprentices, apprenticesExpected` | The apprentices of every month, and the coming-of-age teens times the year's trade share less the schools' diploma-holders. |
| 106 | `final java.util.List<Double> took` | Each month played, the pay the walk took at its top (NaN with none), its window's length, and homeScaleOfPay() as the month ended. |
| 107 | `final java.util.List<Integer> windowed` |  |
| 108 | `Game g` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 52 | 514 | **type** `public class Village1900Check` | Why the 1900 city stalls (R1g's finding), and what R1g's two switches change: the playtest's village founded the 1900 way, played the playtest's way for five years beside its twin founded today's way. |
| 58 | 1 | `static void check(String label, boolean ok)` |  |
| 60 | 4 | `static void check(String label, boolean ok, String detail)` |  |
| 99 | 12 | **type** `static final class Seen` | What one city shows at the gate month and at the end. |
| 109 | 1 | `Seen(boolean the1900Way)` _(in Village1900Check.Seen)_ |  |
| 112 | 33 | `public static void main(String[] args)` |  |

### 2 (lines 146-171)

| line | len | member | says |
|---:|---:|---|---|
| 148 | 23 | `static void rent(Seen y1900, Seen today)` |  |

### 3 (lines 172-196)

| line | len | member | says |
|---:|---:|---|---|
| 174 | 22 | `static void skills(Seen y1900, Seen today)` |  |

### 4 (lines 197-223)

| line | len | member | says |
|---:|---:|---|---|
| 199 | 24 | `static void stall(Seen y1900, Seen today)` |  |

### 5 (lines 224-255)

| line | len | member | says |
|---:|---:|---|---|
| 226 | 29 | `static void home(Seen home, Seen stalled, Seen today)` |  |

### 6 (lines 256-272)

| line | len | member | says |
|---:|---:|---|---|
| 258 | 14 | `static void apprentices(Seen taught, Seen stalled)` |  |

### 7 (lines 273-291)

| line | len | member | says |
|---:|---:|---|---|
| 275 | 15 | `static void todayUnmoved(Seen on, Seen off)` |  |

### 8 (lines 292-349)

| line | len | member | says |
|---:|---:|---|---|
| 294 | 43 | `static void walk(Seen home)` |  |
| 339 | 11 | `static void quietly(Runnable step)` | Runs a step with the console quiet, the model's chatter left out of the report. |

### the fixture (lines 350-565)

| line | len | member | says |
|---:|---:|---|---|
| 360 | 65 | `static Seen play(String label, boolean the1900Way, boolean home, boolean apprentices)` | A city founded on the playtest's founding, the 1900 way or today's, founded and played as LongPlaytest.main() founds and plays it: the same switches, the automatic works on before the village is laid, the founding's m... |
| 427 | 16 | `static void months(Seen seen, int n)` | Up to n of the player's months (LongPlaytest.runPlayer()), never past MONTHS: the gate weighed at the end of GATE_MONTH, the last arrivals summed. |
| 445 | 10 | `static void weigh(Seen seen)` | The staffing gate on the exporters' first plant, and the diploma posts it wants. |
| 457 | 6 | `static double[] postsByBand(Game g)` | The city's posts by wage band, as the playtest's labour trace adds them. |
| 465 | 25 | `static void lastHorse(Game g)` | R1g: the last horse leaves no feed behind on a reused output row (the played city's reload fault). |
| 492 | 63 | `static void schoolPlusDegrees()` | REV-R1gc2-1: gross school leavers cover the quota even when degrees make the diploma band's net flow smaller. |
| 557 | 8 | `static long standing(Game g, String sector)` | How many buildings of a sector stand. |

