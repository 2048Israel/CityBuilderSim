## changelog
### {next} — I13-L, the landlords' card asks the mortgage lender, and the Ore card covers every ore — 2026-10-27, ready, see docs/queue/I13-L-notes.md
Two notes I13 carried (Jerus, 2026-10-18). A landlord's home was not asked the INTEREST gate - the mortgage lender's test decides it - so its build card showed no last gate though the model has one.
`Game.lenderTest()` is now the card's door to that test, the call the order makes (`lenderReads()`, shared with `considerOnMortgage()`); `BuildCard` has a LENDER gate for a landlord's home, in the lender's words (what it lends, the payment against the rent, the cover against what it asks, or the down payment it lacks), and the Real Estate page's grid a "lender" column.
In a city with little landlord cash a House card now says why the lender would refuse it. The land office's Ore card was iron's alone: `OreView` gives a row for every ore the city owns a site of (iron always), and the card a block for coal, stone, copper and uranium with its deposits, tonnes, the sites its mines take,
"nothing is digging" where a deposit has no mine, and a click to the industry that digs it; iron's words are as they were.
`BuildCardCheck` 14 and `LandCheck` 30 new; the default playtest's trace is byte-identical (no ensemble).
## pending
| I13-L-C | I13-L ({next}): look at it on the PC at 1,389 x 868: the land office with two ores, one dug and one not, the Ore card's blocks and the amber line; Build > Homes, a House card's "this one:" lender line in a city short of landlord cash; Sectors > Real Estate > Investors, the grid's "lender" column | his check | [I13-L notes](docs/queue/I13-L-notes.md) |
