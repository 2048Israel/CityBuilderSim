# Fitout.java - 179 lines · 19 methods · 0 constants · model

`ham/citybuildersim/Fitout.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

> One new building's equipment as its order fixes it (R1e): the pieces of its template it comes WITHOUT, chosen when the order
> is quoted and frozen on site when it is accepted, and what that makes one building's cash cost and construction points - the
> template's shell and each piece's part of it, defined here once for the quote, the investors' estimate, the site and the bill.
> 
> WHY (R1e, 2026-11-19; docs/queue/R1e.md, R1b-R's call 3 left open by R1d). A new building paid its template's whole cost at
> the order, and what it came with was decided at its finish (Game.fitWhatFinished() asked EquipmentFit.fittedForNew() then): a
> building that came without a piece had still paid for it, and no discount could be given safely while the finish might deliver
> another set than the one priced. Now the set is chosen at the quote (of(): the pieces fittedForNew() keeps, the same rule), the
> order is priced on it, the site owes its points and keeps it (BuildingsStacks' lots, saved), and the finish delivers it: a
> discovery or a price change after the order is a new fitting decision on a standing building (EquipmentFit.choose()).
> 
> THE SPLIT, SOURCED (R1c call 10; R2e's capital estimate, "25% of plant cash at MACHINERY 13.4, 10% construction points"). The
> pieces were sized as parts of their templates: one built into the plant buys a quarter of the plant's cash in MACHINERY at its
> import price and its work is a tenth of the plant's points; one carried in (the truck, the tractor, the shovel, the drill, the
> cutter) buys its machines and takes no work. So the split is read back off the catalogue and not off today's market: a piece
> with work is in its template's price, its part of the cash its goods at the table's import price (Good.worldImportPrice(), the
> price its quantity was sized at) over the template's catalogue cash, its part of the points its work (inThePrice(),
> cashShare()), and with that work the cash a point of the template holds beside its pieces' goods (shareAPoint(): the frame,
> its labour and the rest, over its points), so a building that does fewer points pays the same for each and none for the work
> left off (REV-R1e: the shell had kept the left-off work's cash, which a refit then charged again). A piece with no work is
> bought apart and no part of the price, so a building without it costs what it did. The shell is the template less every
> built-in piece, never less than nothing. Not Game.quoteFitting(), a refit's goods drawn from the market today and its work at
> the city's price a point, which nothing bounds by what the template charges for the piece.
> 
> A city founded the default way fits every piece to every building, so there an order's fitout is whole() and chosen by no one
> (isChosen() false): the template's own cash and points, the same numbers in the same arithmetic as before, no lot on site and
> nothing saved.

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (8), [Equipment](Equipment.md) (5), [Game](Game.md) (1), [NewUnit](NewUnit.md) (1), [LicenceFees](LicenceFees.md) (1), [EquipmentFit](EquipmentFit.md) (1), [Good](Good.md) (1)

**Used by (7):** [BuildingManager](BuildingManager.md), [BuildingsStacks](BuildingsStacks.md), [BusinessInvestment](BusinessInvestment.md), [FitoutCheck](FitoutCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignCrewsCheck](ForeignCrewsCheck.md), [Game](Game.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 39 | `private final BuildingsTemplate template` |  |
| 41 | `private final Set<String> without` | The pieces left off, by id, sorted; empty for the whole template. |
| 43 | `private final boolean chosen` | Whether an order chose it (a city founded the 1900 way, a template with equipment): only a chosen fitout is kept on site. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 37 | 143 | **type** `public final class Fitout` | One new building's equipment as its order fixes it (R1e): the pieces of its template it comes WITHOUT, chosen when the order is quoted and frozen on site when it is accepted, and what that makes one building's cash co... |
| 45 | 5 | `private Fitout(BuildingsTemplate template, Set<String> without, boolean chosen)` |  |
| 52 | 3 | `public static Fitout whole(BuildingsTemplate t)` | The whole template, chosen by no one: every piece, its own cash and points. |
| 60 | 6 | `public static Fitout without(BuildingsTemplate t, Collection<String> ids)` | A chosen fitout without these pieces (an order's, or one read back from a save or a shell): ids the template does not carry are dropped, so a piece taken out of the catalogue leaves the building as its template now st... |
| 74 | 9 | `public static Fitout of(Game game, BuildingsTemplate t)` | What an order of `t` placed now comes with: in a city founded the 1900 way, for a template that carries equipment, the pieces EquipmentFit.fittedForNew() keeps (the rule the finish used to ask, asked at the quote), th... |
| 85 | 1 | `public BuildingsTemplate template()` | The template it is of: what is ordered and stacked. |
| 88 | 1 | `public boolean isChosen()` | Whether an order chose it, and so whether a site keeps it (BuildingsStacks' lots). |
| 91 | 1 | `public boolean isWhole()` | Whether it leaves nothing off: the template's own cash and points. |
| 94 | 1 | `public Set<String> without()` | The pieces it comes without, by id, sorted; unmodifiable. |
| 97 | 1 | `public boolean comesWith(String pieceId)` | Whether a building of it comes with this piece. |
| 100 | 3 | `public static boolean inThePrice(Equipment piece)` | Whether a piece is part of its template's price: one with fitting work, built into the plant (R1c call 10). |
| 110 | 11 | `public static double cashShare(BuildingsTemplate t, Equipment piece)` | A piece's part of its template's cash cost, sourced (see THE SPLIT above): its goods at the table's import price over the template's catalogue cash, for a piece in the price; 0 for one carried in, for a good the world... |
| 128 | 6 | `public static double shareAPoint(BuildingsTemplate t)` | The share of its template's cash one point of its work holds beside the built-in pieces' goods: the cash less every piece's cashShare(), the frame with all its labour and the rest, over the template's points. |
| 136 | 6 | `public double cashShareLeftOff()` | The share of the template's cash this fitout leaves off: its pieces' goods and their work's cash (shareAPoint()), never more than the whole. |
| 144 | 4 | `public double cashCost()` | One building's cash cost (thousands, struck as the template's is): the template's, less the pieces left off, their goods and their work; the template's own for the whole. |
| 150 | 9 | `public double pointsLeftOff()` | The construction points the pieces left off would have taken a building: their work. |
| 161 | 4 | `public double points()` | One building's construction points: the template's, less the work of the pieces left off; the template's own for the whole. |
| 167 | 4 | `public boolean equals(Object o)` |  |
| 173 | 1 | `public int hashCode()` |  |
| 176 | 3 | `public String toString()` |  |

