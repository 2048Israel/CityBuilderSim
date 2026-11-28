# UX7 - the build screen: groups, an icon per building, the city's own told from the market's

From UX1 (`docs/queue/UX1-notes.md`, section 5, P4; mockup 9 the recommendation, mockup 10 the alternative,
in `docs/studies/ux1/mockups/`). UX1's brief: "the build screen: Industry in groups, an icon and colour per
building, the city's own buildings told from the market's".

## What is wrong today (UX1's audit 1)

Build's Industry page is about 75 cards, all open, over 13,606 px, with no filter or sort; a "best buy"
chip sits on a card that loses $576.9M a month; "you have N" counts the investors' buildings with the
city's, and only Construction splits them. Every category is a wall of the same tall card.

## What it builds (P4 A, recommended: mockup 9)

1. **Build in the panel**, 560 to 640 px. At its top the categories as icon buttons in two labelled sets:
   "The city's job" (Utilities, Infrastructure, Healthcare, Education, Safety; each with its need's state
   as a dot, from UX4's reading) and "The market's · you may add" (Homes, Shops, Industry, Offices, Farms,
   Freight, Vehicles, Luxury shops, Restaurants). The path line's "Build" is the Overview.
2. **Groups in families.** Industry's 18 groups (BuildCard.GROUP_ORDER) gather into five families, each a
   header that folds: Food (Food mills, Food processing), Metals (Iron Mining, Iron & Steel, Copper, Gold
   Mining), Making (Fabrication & machinery), Materials (Stone, Cement, Wood, Building materials, Builders),
   Energy (Coal, Oil, Oil platforms, Oil pipelines, Refining, Oil storage). Each group heads its row with its
   supply line (what the city used and what is made here) and a small bar. A folded family keeps its counts.
3. **A tile per building**: UX6's icon and colour, its name, and two counts told apart: the city's own in
   Building pink, the market's in Business violet. A mark where its owner would lose on it (no "best buy"
   chip on a loser); the star "most per $" only among those that pay.
4. **Filters and a sort**: "Needed now" (the city is short of what it makes), "Can staff", "All", and a
   toggle "The city's own"; sort by the game's order, its price in months of what it adds, the shortfall, or
   how many stand. (Mockup 9 labels the first "Worth building"; the desk chat's words are the ones above.)
5. **One card at the panel's foot** for the selected tile: makes against what the city used, all in, its
   price in months of what it adds, staff, and the investors' verdict as a warning when the market would
   lose; one button, "Place on the map", and the best plot. No stepper.
6. **Placing on the map**: the kind's footprint follows the cursor as a ghost with "fits" or why not; the
   city's and the market's of that kind stand outlined (UX5's lit set). Esc stops.
7. **Wide** opens the catalogue (mockup 10, P4 B's form): every kind at once in a table by family and group,
   the same columns, sortable, "Place on the map" at its foot.
8. The city's five categories and the market's other eight take the same tiles; Construction keeps its
   Sites, Timeline and Demolish, reached from the strip's "building" chip.

## The stability fault it owns

UX1's F3: on `012-build-industry` a month moves the page's scroller 4056.5 to 4057.1 px of 12,185.6,
because the place is held as a fraction of a page that grew. The new page holds its pixel; UiSteadyCheck's
known red `UiSteadyCheck | -build-industry` (UX1's ship file) is this batch's to retire.

## Files

`ui/BuildScreen.java` (6,655 lines: read `docs/map/BuildScreen.md` and use `tools.Where`), `BuildCard.java`
(the groups and families), `BuildAdvice.java` (the verdict, read-only unless the "best buy" rule lives
there), UX6's `ui/BuildingLook.java`, `ui/ConstructionScreen.java` for the chip's way in.

## Size

Large: one Opus session, about 1,500 to 2,000 lines, split in two slices (the panel and tiles; the card,
filters, placing and the wide catalogue) if the contract's slicing asks.

## The guard

- **BuildCardCheck** (the groups and families, the city's and the market's counts on fixtures where both
  own one), **BuildMenuCheck** (BMD1's exit code), **UiSteadyCheck** with its `-build-industry` red gone and
  no new red on any Build page, KeptCheck.
- Pictures before and after by `ui.PagePictures` (Build's Overview, Industry and Homes on the big city and
  the young town), in the notes.

## Order and fit

After UX2, UX5 and UX6, and after BMD1 (the Build menu's lab descriptions; BuildMenuCheck exits nonzero on a
failed description). ASH1 changes Build's small-town power step; if it lands first its card is a tile here.

## Not this batch's

The icons themselves (UX6), the map's drawing, the manual.
