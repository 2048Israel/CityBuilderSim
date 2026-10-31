# The manual's Rankings page (LB3, for the next edition)

The text below is the manual's page for the Rankings tab (LB2's board and LB3's saved cities), written to go into
the published manual at its next edition (`docs/orchestrator.md`, The docs pass: "A manual edition only at a milestone
or when Jerus asks"). It follows chapter 22, *The screen*, as a chapter of its own, numbered there. Until then this
file is the page. The words in backticks are the code's names, as the manual writes them.

---

## Rankings

*The city against the world, measure by measure: the real world's nations and city regions as they were in 2025, or
the nations of the city's own world at its month, and the player's own saved cities beside them. Every money figure
is turned once into US dollars of 2015 and ranked there, so the unit a figure is shown in can never reorder a board.*

### Where it is

A rail tab, *Rankings*, with a trophy, after History (LB2, Jerus 2026-10-21: "Rail tab after History"). Two more
doors open it: the nation's card on the map ("All its measures ›", the tab on Real world 2025 with that nation's
drawer open), and *Compare* beside YOUR CITIES on the main menu, which is there while a city is in play and opens the
tab on the player's own cities (LB3).

### The two boards

**Real world 2025** is fixed: 196 nations and 62 city regions, each with its latest published figure to 2025, from
`world-2025.json` (LB1, `WorldMeasures`). It is the same at every month of every city. **Your world** is the city's
own world at the city's month (`Nations`): its nations on the ten measures the world carries for each (people and their
growth, GDP, GDP per person, real growth, ten-year growth, inflation, price level against the US, area, people per
km²); the other sixty rank on Real world 2025 only, because a figure held at 2025 in a world at 1950 or 2300 would say
nothing true. A city region on Your world is its nation's path times its 2025 share of the nation's people and output.
A city's own area is its dry ground, its lakes and sea left out, because the real figures are the World Bank's land
area, which leaves inland water out (LB3).

### Three moneys, one pivot

The game has three: the city's own, the game's US dollar (which inflates with its world's US prices), and the real US
dollar of 2015, in which a founding game dollar counts as one 2015 dollar (D1: "2015 so that its calibrated to the
other"). A money figure X becomes US$ of 2015 once: at this month's rate X ÷ fx ÷ P, at purchasing-power parity
X ÷ parity ÷ P, and at the constant 2015 rate the figure at founding's prices ÷ the founding rate, where fx is the
city's money per game US$, parity XR1's, and P its world's US price level since founding. It is ranked there, and only
then shown in the money picked. So the active city's own figure in its own money is the one the rest of the game shows.

### The four controls and the sentence

**Board** (Real world 2025 · Your world), **Money** (the city's · game US$ · real US$), **Prices** (per year · static)
and **Convert at** (this month's rate · constant 2015 · PPP). It opens on GDP, the full list, real US$, this month's
rate, per year (D9). Under them one sentence names exactly what is on screen: *"Real US dollars at 2015 prices, at
this month's exchange rates. The real world in 2025; Arden in July 2050."* Prices is greyed, its tooltip saying why,
where it would move nothing: under real US$ (2015 prices by definition), under Constant, on a measure that is not
money, and while no saved city is on the board.

### The table, the card and the drawer

The measures column (pinned first, then the ten groups, "Browse all 70"); the table, with filter chips and their
counts (All, Nations, Cities, Your size, Your cities), Full list or Around you, and for each row its rank, kind, name,
what it is of, the year of its figure, the value, a bar and its mark: ● published, ◐ a published projection, ○ the
game's estimate, with "N of M figures here are the game's estimates" under it. Ties share a rank; a lower-is-better
measure ranks ascending and says so; a place with no figure is no row, never a 0. The city's card gives its rank of
N, the places above and below, the gap to pass the next one up, its ranks on the pinned measures, and its rank over
the years. A row's drawer opens every measure for that place, the real world's figure with its mark and source
beside Your world's.

### Your saved cities (LB3)

The other slots, 1 to 10, rank beside the city, read and never written; the autosave is left out, being a copy of a
numbered city. A save an older or a newer build wrote is left out and counted in a line under the table. Each save is
its own world, with its own US prices and its own money, and the pivot is the only bridge between two: a saved city's
figures are read in the units of its last save, and a currency reform (÷10, ÷100) rescales its history and its rate
together, so **a reform never moves a rank**. The files are read off the screen's thread when the tab is entered, each
kept until its slot is saved again, and of a history only the series the board reads.

**Prices** decides how a saved city's money meets the active city's (D2, Jerus: "Per year, each its own year"). Per
year, the default, shows each saved city in its own year's dollars; static restates it at the active city's prices of
its year. It moves only the saved rows, and never under real US$.

**Your cities at: now · your age** (D3). Now shows each saved city as last saved. Your age reads each one's history
at the active city's age in months: two cities compared at the same point in their lives. A city that never got that
far is no row, and the line under the table says when it stopped ("Riverton (slot 5) stopped at Feb 2031"). At your
age the figures a history does not keep a month at a time are absent rather than guessed.

### Rank over the years

On the city's card, one point each December: the city's rank on the shown measure against Real world 2025, which holds
still, so the line moves only because the city did (D12: "Real world 2025 only"). It is worked out from the city's
history when the tab is opened. The US price level each month, which the real dollar needs and a save keeps only for its
last thirteen months, has been part of the history since LB3 (`usPrices`); a city's months from before that have no
point in real dollars at this month's rate.
