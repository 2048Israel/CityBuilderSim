# UX5 - the map's hooks: a card on a building, a page lighting its buildings

From UX1 (`docs/queue/UX1-notes.md`; mockup 1 item 5, mockup 2 item 3, mockup 9 item 7). UX1's brief: the
map's own drawing is out of scope ("you dont actually rework the map itself"), and the hooks into it are
rows of their own: "clicking a building for its card, or tinting the districts short of a need".

## What it builds

1. **A card on a building.** A click on a building on the map opens a small card beside it: its kind and
   icon, whose it is (the city's or the market's: told apart in Building pink and Business violet, as UX7's
   tiles are), what it does with its one figure (served, makes), its staff bar, its cost a month, and a link
   to its page in the panel. Esc or a click elsewhere closes it.
2. **A page lights its buildings.** A page can ask the map to outline a set of buildings: a sector's books
   light the sector's works (mockup 2: "Manufacturing's works lit", its sites dashed); Build's selected kind
   lights the city's and the market's of that kind (mockup 9). A short legend says what is lit.
3. **A need tints its districts.** A need opened from UX4's sidebar tints the districts where it is short
   (schools out of reach, streets over their flow), where the model has the figure by district; where it
   has not, the brief's first job is to say so in the notes and light the buildings instead.
4. **The legend folds** to a button by the zoom; the Land office's map legend's content moves into it.

## Files

`ui/MapView.java` (a hook to outline a set of tiles and to report a click on a building: no change to how
the map draws its ground), `ui/MiniMapFrame.java`, `CityMap.java` (which building stands on a tile, whose it
is: read-only), `ui/Frame.java` for the card. Read `docs/map/MapView.md` and `docs/map/CityMap.md` first;
never open MapView whole.

## Size

Medium: one Opus session, about 800 lines.

## The guard

- **MapHooksCheck** (new): a click on each kind of building opens the card with its owner told right (a
  city's school, an investor's factory); a set asked lit is lit and nothing else; the card's figures are the
  model's getters'.
- **UiSteadyCheck** with a card open and a set lit: an unchanged redraw moves neither; a month keeps the
  map's place and zoom.
- Pictures before and after by `ui.PagePictures` (map-ground with a card, a sector's books with its works lit).

## Order and fit

After UX2. Beside UX3, UX4 and UX6. UX7 uses the lit set for its placing ghost.

## Not this batch's

The map's drawing, terrain and zoom; the build screen; the manual.
