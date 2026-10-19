# PortDivision.java - 338 lines · 24 methods · 6 constants · sectors

`ham/citybuildersim/sectors/PortDivision.java` - generated 2026-10-19 by CodeMap; line numbers are as of that run.

> Freight's Ports division: the crews that work the city's berths, paid by the
> shippers who use them and paying the city for the quay (FR3, 2026-10-19;
> docs/queue/FR3.md; docs/studies/spec-freight-and-infrastructure.md 3.4, D8,
> D9).
> 
> WHY. The city built the berths and the sea terminals earned nothing: the
> crews were the city's transit bill, the tonnes crossed free, and so no city
> ever built a port that the advice or the auto-builder could justify (the
> spec's finding 9.5). Now the berths stay the city's, with their repairs
> (EconomyManager.CITY_MAINTAINED), and Freight works them: it bills every
> shipper a handling charge on each tonne that crosses a berth - the buyer for
> what it lands, the exporter for what it ships, "Port handling" on the
> shipper's input line - and pays the city port dues on the same tonnes, a
> revenue line of the treasury ("Port dues") and a dial of the city's own
> (TaxPolicy.getPortDues(), D9: the fare's shape). A tonne sold at home, railed
> or by lorry pays no handling: each leg is paid once (spec 2).
> 
> NOT A SECTOR, A DIVISION OF ONE, as Haulage is: it holds its own figures and
> its own price rule, and books through Freight (the package-private seam at
> the end of Freight's "Haulage division's seam"): its bill is Freight's other
> revenue named "Port handling billed", its dues Freight's operating expense
> "Port dues", its crews the posts of the berths Freight operates
> (BuildingsTemplate.getOperator(); Freight.portsCrewByKind()).
> 
> THE PRICE, BY KIND OF CARGO (FR3.md 2). The quote is the berths' crew over
> the tonnes - the larger of the tonnes billed and DESIGN_USE of the kind's
> berths' month, so an idle berth is carried by the division and not billed to
> the few tonnes that use it - times the scarcity when the kind asks for more
> than its berths, plus the dues a tonne pays the city. NO RETURN ON CAPITAL
> (call 3): the division has no rate base, the city owns the berths and
> recovers them through the dues.
> 
> THE DUES, A TONNE OF A KIND: the annuity on the kind's terminals standing
> (their cash cost and materials at market, BuildingManager
> .getPortBookValueByCargo()) at the city's monthly borrowing rate over
> PORT_LIFE_MONTHS, over the kind's berths' month at DESIGN_USE; times the
> dial. The city's rate is its own paper's at the berth's life (cityRate()).
> 
> THE MONTH, which Freight.haul() runs: billMonth() in step 1, beside the
> railway's and the trucks' bills, at the shares in force while the cargo
> moved; closeMonth() straight after, while the tally of the month billed and
> the class crude went in are still the ones that month was billed with.

**Uses:** [Ports](Ports.md) (27), [Freight](Freight.md) (4), [Game](Game.md) (2), [Sector](Sector.md) (2), [TaxPolicy](TaxPolicy.md) (1), [BuildingManager](BuildingManager.md) (1)

**Used by (3):** [Freight](Freight.md), [LandlordPortCheck](LandlordPortCheck.md), [LongPlaytest](LongPlaytest.md)

## Sections

| line | section |
|---:|---|
| 93 | · the arithmetic |
| 167 | · the month |
| 251 | · what it is doing |
| 275 | · save, reset, reform |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 57 | `PortDivision.DESIGN_USE` | `Ports.WORTH_A_BERTH` | The share of a berth's month the dues and the rule's floor are worked at (FR3.md call 4): Ports.WORTH_A_BERTH, .60, the test player's own threshold for ordering a berth. |
| 60 | `PortDivision.PORT_LIFE_MONTHS` | `360` | Months over which a berth's cost is recovered by the dues: 360, thirty years, the term a landlord port leases a terminal for (est., FR3.md 2 [R91][R92]). |
| 63 | `PortDivision.BILLED_AS` | `"Port handling"` | What the handling is called on the shipper's input line (FR3.md 1). |
| 66 | `PortDivision.REVENUE_NAME` | `"Port handling billed"` | ...and on Freight's revenue (Freight.nameOtherRevenue()), beside "Haulage billed". |
| 69 | `PortDivision.DUES_NAME` | `"Port dues"` | ...and the dues, on Freight's input line and the treasury's revenue. |
| 71 | `PortDivision.KINDS` | `Ports.Cargo.values().length` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 73 | `private final Freight freight` |  |
| 76 | `private final double[] quote` | The handling quote in force by kind, the city's money a tonne (D$ thousands a tonne); meaningful only when quoteKnown. |
| 77 | `private final boolean[] quoteKnown` |  |
| 80 | `private final double[] tonnes` | the month billed, by kind (the screens', the check's and the save's) |
| 81 | `private final double[] billed` |  |
| 82 | `private final double[] dues` |  |
| 83 | `private final double[] crew` |  |
| 84 | `private final double[] design` |  |
| 85 | `private final double[] duesRate` |  |
| 87 | `private double handlingTotal, duesTotal` | The month's handling billed to the shippers (the sum of their lines) and the dues paid to the city, as booked. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 54 | 285 | **type** `public final class PortDivision` | Freight's Ports division: the crews that work the city's berths, paid by the shippers who use them and paying the city for the quay (FR3, 2026-10-19; docs/queue/FR3.md; docs/studies/spec-freight-and-infrastructure.md ... |
| 89 | 3 | `PortDivision(Freight freight)` |  |

### the arithmetic (lines 93-166)

| line | len | member | says |
|---:|---:|---|---|
| 99 | 5 | `public static double monthlyRecovery(double value, double monthlyRate, double months)` | What a sum of capital costs a month to recover over its life: the annuity V x r / (1 - (1 + r)^-n), and V / n at no interest. |
| 114 | 6 | `public double cityRate()` | THE CITY'S BORROWING RATE, a year's (D9): what the city's own paper of PORT_LIFE_MONTHS is worth to a lender today (DebtManager.curveRate()), the rate a berth's thirty years are funded at; nothing without a game. |
| 122 | 7 | `public double dial()` | The dial: the multiple of the default due the city asks (TaxPolicy.getPortDues()); 1 with no game. |
| 136 | 7 | `public double duesRateOf(Ports.Cargo kind, double berthMonth)` | The default due a tonne of a kind pays the city (dial 1), the city's money: the kind's terminals' book value recovered over PORT_LIFE_MONTHS at the city's monthly rate, over its berths' month at DESIGN_USE. |
| 149 | 5 | `public static double scarcityOf(double boundaryTonnes, double berthMonth)` | The scarcity multiple on a kind's crew cost: 1 while what crossed of the kind is at most its berths' month, rising linearly to Freight .MAX_SCARCITY_MULTIPLE at twice the berths' month, capped there. |
| 160 | 6 | `public static double targetOf(double crewWages, double handled, double berthMonth, double scarcity, double dueRate, double dial)` | The rule's target for a kind: crew / max(tonnes, berths x DESIGN_USE) x scarcity + the due x the dial. |

### the month (lines 167-250)

| line | len | member | says |
|---:|---:|---|---|
| 181 | 38 | `void billMonth(Map<Sector, double[]> seaOf, Ports ports)` | Step 1 of the month: bills last month's sea tonnes, sector by sector, and pays the city its dues on them. |
| 228 | 17 | `void closeMonth(Ports ports)` | Straight after the bill: the rule, per kind. |
| 247 | 3 | `private static double boundaryOf(Ports ports, Ports.Cargo kind)` | What crossed of a kind that could go by sea, the month billed: the tightness the berths are measured against (Ports.carriable()). |

### what it is doing (lines 251-274)

| line | len | member | says |
|---:|---:|---|---|
| 254 | 1 | `public double getQuote(Ports.Cargo k)` | The handling quote in force for a kind, a tonne; 0 until one is struck (the bill meanwhile is the rule's target). |
| 257 | 1 | `public boolean isQuoteKnown(Ports.Cargo k)` | Whether the quote has been struck from a month with a berth and a crew. |
| 260 | 1 | `public double getTonnes(Ports.Cargo k)` | the month billed, by kind |
| 261 | 1 | `public double getBilled(Ports.Cargo k)` |  |
| 262 | 1 | `public double getDues(Ports.Cargo k)` |  |
| 263 | 1 | `public double getCrew(Ports.Cargo k)` |  |
| 265 | 1 | `public double getDesignTonnes(Ports.Cargo k)` | The kind's berths' month at DESIGN_USE, as the month was billed. |
| 267 | 1 | `public double getDuesRate(Ports.Cargo k)` | The default due a tonne of the kind paid (the dial not in it), as the month was billed. |
| 270 | 1 | `public double getHandlingBilled()` | The handling billed to the shippers in the month billed: the sum of their lines, Freight's "Port handling billed". |
| 273 | 1 | `public double getDuesPaid()` | The dues Freight paid the city in the month billed: its "Port dues", and the treasury's. |

### save, reset, reform (lines 275-338)

| line | len | member | says |
|---:|---:|---|---|
| 278 | 16 | `public void save(Map<String, Double> extras)` | The division's state into Freight's extras (keys port.<KIND>.*; a total for the dues the treasury reads back after a load). |
| 296 | 16 | `public void restore(Map<String, Double> extras)` | ...and back. |
| 314 | 11 | `public void reset()` | Back to a city that has never worked a berth. |
| 327 | 11 | `public void redenominate(double scale)` | Its money in the new unit (Denomination): the quote, the dues rate, the bill, the dues and the crews' wages. |

