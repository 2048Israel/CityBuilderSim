# I13-L - the landlords' card and the Ore card ({next}, 2026-10-27)

The batch's record beside its brief, `I13-L.md`. Two notes I13 carried and did not
build. Run whole on Sonnet 5.5 at xhigh, Opus at the gate only (BP1 item 7). No save
change; the order's call was refactored (below) and the trace is byte-identical.

## 1. The landlords' card asks the mortgage lender

- **The door.** `Game.lenderTest(sector, template)` returns a `Game.LenderTest` for one
  landlord's home (null for any other building): its cost, the funds the landlord would
  put in, the rent and the net operating income after carrying it, the insured rate and
  the real rate the lender tests at, and `Mortgage.decide()`'s answer for an order of
  one; `passes()`, `outright()`, `shortOfDown()`, `down()`, `loan()`, `payment()`,
  `coverage()` and `asks()` read off it. `Mortgage.paymentFor()` is new (what
  `coverage()` already computed inside).
- **One call.** `considerOnMortgage()` (the order) and `lenderTest()` (the card) both
  read the lender through the private `Game.lenderReads()`, which holds the five lines
  that were inline in the order (insured rate, real rate, the cost function, net rent,
  `Mortgage.decide()`). Same expressions, same order; the card cannot disagree with the
  order for want of a copy. The funds are `financingOf()`'s: its till, its money abroad
  and what its owners are asked for the down payment.
- **The gate.** `BuildCard.GateKind.LENDER`, appended last; a landlord's home has it
  and not INTEREST (`BuildCardCheck` 11's "a landlord's home has no interest gate"
  stays true), every other building has the dash. It is the building's first gate when it
  fails after the loss (`gate()`), and `appraise()` carries its mark, so the investor
  report's first failure is still `gate()`'s. `Gate` gained a seventh component, the
  `LenderTest`. `BuildScreen.lenderWords()`: "the lender would lend $353k, a payment of
  $1,658 a month at the insured 4.42%, and the rent left after carrying it, $851 a month,
  covers it 0.51× - the lender asks 1.20×"; short of the down payment, "the lender wants
  $81k of its own for the 15% down payment, and it would put in $41k". A passing home
  shows no gate, as a passing INTEREST does; the Real Estate page's grid ticks it.
- **The Real Estate page** (`SectorScreen`, EVERY BUILDING AT EVERY GATE) gets a seventh
  column, "lender", only on a page where a building is a landlord's home; no other
  sector's grid changes. `gateShort()`, `soWords()` and the gate icon know the new kind.
- **The harness**, `BuildCardCheck` 14: only a landlord's home has the gate (3 of 3,
  98 others none); a House from a till of twice its down payment is refused on its rent
  (cover 0.51× against 1.20×) and the order, run through `considerOnMortgage()` with the
  card's funds and rent, says the same line, to the cover, and orders none; a till of
  half the down payment fails the card for that and the order holds with the same line;
  a till of nine tenths passes, a raised insured rate (4.42% to 16.86%) takes it away on
  both, and at today's rate the order builds one House on an insured mortgage. Section 6's
  "a building that passes every gate has no this one:" now counts a home's lender among
  the gates (its predicate, not its premise).

**What the player sees.** In a city with little landlord cash, a House card now says
"this one: the lender would lend ... covers it 0.51× ...", where it said nothing. That is
what the order meets: in the check's town the lender refuses a House on its rent until
the landlord puts in about 70% of the cost.

## 2. The Ore card covers every ore

- **The model.** `OreView.ores(game)` (new, pure): iron first, always (the card's
  headline, even with no deposit), then stone, coal, copper and uranium where the city
  owns a site; each row its deposits (`LandManager.getSites()`), tonnes in the ground
  (`getRemaining()`), the sites its mines take (`Game.committedOn()`), `undug` (fewer
  taken than owned, the sector page's flag), and the key of the sector that digs it
  (`OreView.diggerOf()`, from the `ExtractionSector`s; none for uranium). Oil keeps its
  own line; the forest is not in fields.
- **The card** (`LandScreen`): iron's lines are as they were, from `OreView`'s first row
  (`ore()`'s strings unchanged, the pop-up on bought deposits too). After iron's,
  one two-line block for each other ore - "Coal: 3 deposits, 19.5M t in the ground" and
  "1 under mines, standing or on site" - and, where a deposit has no mine, "coal that
  nothing is digging" in amber; uranium, which no industry digs, says "no industry digs
  uranium yet" in grey. The oil line moved to the bottom. A block's tooltip names the
  industry and its click goes to that sector's Operations page; the card's own click
  and tooltip still go to Build > Industry ("Mines are built...", no longer "Iron
  mines"); the (i) says so.
- **The harness**, `LandCheck` 30, on `CoalMiningCheck.coalTown()` with iron by fiat:
  every row's figures are the getters the sector pages read; iron's card says
  "3 deposits" and flags undug with no mine, as always; coal has its line, flagged "coal
  that nothing is digging", Coal Mining the industry; a mine on every coal deposit takes
  the flag off coal and not iron's; an iron mine on every iron site takes iron's off,
  its words the same; uranium has a line with no industry. The screen's records are
  reached through `UserInterface` by reflection, as `ZoningViewCheck` reaches Policy's.

## 3. Checks and the trace

`--pick` (23 of 149 harnesses, the Earth file named) exits 0; maps regenerated, Stale 0
firm. `LongPlaytest` (Game.java left to the gate's suite, picked in its place) is among
them. The default playtest on main's classes (d7448ef5, built apart) and on this
branch's, side by side: identical but for the two run-time lines. **No ensemble.**

## 4. Left for Jerus's eye (the PC, 1,389 x 868)

The land office with two ores, one dug and one not (a coal field beside a standing iron
mine): the Ore card's blocks, the amber line, a click on a block. Build > Homes: the
House card's "this one:" line in a city short of landlord cash. Sectors > Real Estate >
Investors: the grid's "lender" column and its tooltips.
