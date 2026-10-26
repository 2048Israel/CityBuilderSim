# BL1 - buying land, revamped: the player picks the exact ground (0.8.53, 2026-10-26)

The brief: [BL1.md](BL1.md), Jerus's words of 2026-10-26: "revamp how buy land works, cause currently those blocks
are wierd and sometimes overlap with land already bought like uiwise, on map view. perhaps more freedom on the exact
selection?"

## 1. The design

### What is wrong today, at its cause

- **The overlap is the globe's.** On the plane the map draws each offer's free ground only: its rectangle less the
  city's own blocks in it (`LandMap.ownedIn()`, the quadtree's owned leaves, which never overlap), and its edge clipped
  to that ground. On an Earth world past the handoff (0.48 km a pixel and wider) `MapView.globeOffers()` fills, hatches
  and edges each offer's **whole rectangle** through the camera. An offer's rectangle takes in the city's finer owned
  steps beside its seed by design (`GridOffers`: "a coarse offer takes in the finer steps beside it"), so on the globe
  the ring of offers is painted over the city's own edge. The offers themselves are right: the office sells only their
  free ground.
- **The blocks look odd** because an offer is drawn as a rectangle whose ground is not one: an L or a notched box where
  the city's finer steps bite into it, with its edge drawn only where the rectangle's own border lies on free ground, so
  the free ground has no edge against the city, and its number sits on a box the eye does not see.
- **No freedom**: the office lists 24 rectangles of its own choosing (six places a side, the seed, the width from the
  city's size, clipped against each other). The player can take one, several, or none, never the ground between them,
  part of one, or a coast's run.

### The ways weighed

1. **Free ground, block by block, the offers kept as suggestions (recommended).** On the expanded map the player picks
   any whole blocks of the city's block grid (`market.getLevel()`, the lines the map already draws) that are not wholly
   the city's and that join the city's ground, directly or through other picked blocks. A click picks or drops one
   block; a drag from free ground at the city's frontier boxes them; a click on an offer still picks the offer. The
   picked ground is priced by the office's own rule (`LandMarket.measure()`, the price a new offer of that ground would
   be listed at today; the rule is linear in the ground and its fields, so block by block and all at once agree to the
   cent) and bought through the land office's own Buy (`Game.buyLandParcel()`), so the cash, the vault, the bond or
   note for a gap, the books and the receipt are an offer's. In play: you paint the coast you want, or an arc round two
   sides, and the card prices it; the 24 offers stay on the map and in the office as the office's suggestions, at
   their listed prices.
2. **The offers' rectangles grown and shrunk by their edges.** Each offer gets handles; a drag moves its edge by whole
   blocks, the office re-measures it. In play: still 24 rectangles in fixed places; a coast's run or a gap between two
   offers takes several, and the shapes stay boxes.
3. **Part of an offer.** A box inside an offer buys that part. In play: freedom only inside the office's footprint; the
   ground between offers stays unbuyable.

Way 1 is the only one that buys "an irregular stretch along a coast and one across two sides of the city", the eye
check the brief names. It is taken (BP1: the session takes the recommendation and names it; Jerus can overturn it).

### Way 1 as built: the player's ground is drafted into the office

- **Drafts.** The picked ground's free blocks are cut into rectangles of whole blocks (rows of blocks, runs merged down)
  and the office measures each as an offer of that rectangle at today's price: a **draft**, held beside the 24 under
  a negative id from a counter of its own, so no listing's number moves. `find()` and `take()` know drafts; the
  listing (`getListing()`), the office's rows, "Buy the best N", the going price, the reach and the AutoBuilder do not.
  Drafts are never saved: they are the view's, as the picks are, and a load or another city drops them.
- **Bought as an offer is.** The card, the funding page and Buy work on ids already, so a draft is bought by
  `Game.buyLandParcel()` like an offer: `LandManager.buyParcel()` takes it, `CityLand.extend()` makes its free plots the
  city's, the price is paid in the money the toggle says, the month's land purchases carry it.
- **An offer the player's ground bites into leaves the shelf.** A standing offer keeps the price it was listed at, and
  today its ground never shrinks (offers never meet). A draft bought on part of an offer's free ground would leave that
  offer selling less at the old price, so the office takes such an offer off the shelf (unbought) and lists the place
  again at the update that follows every purchase, against the city as it stands. MS1's picks carry onto it.
- **An offer wholly picked is bought as the offer**, at its listed price: the blocks of an offer's whole free ground,
  picked, are its id on the card, not a draft. The card names offers by place and the rest as "your ground".
- **Through the month**: the picked blocks are ground, so they stand (MS1); a block the city has bought since drops,
  named in the head; drafts are measured again when the office strikes the month's price.

### What it does not change

- **The city's own buying**: the AutoBuilder and every model path buy from the 24 offers by id, and drafts are not in
  the listing they read; nothing in the month makes a draft.
- **What ground costs**: a draft is priced by the rule that prices a new offer today; an offer keeps its listed price.
- **The save**: drafts and picks are never saved; a holding bought from a draft is a holding like any.
So the default playtest's trace stays byte-identical, and there is no ensemble.

### Drawn as the ground it is (item 3)

- An offer is drawn as its free ground: hatched, and edged along that ground's own outline on the block grid
  (stepped, the city's edge's rule, `LandMap.outline()`'s), dashed pink, solid when picked; on the plane and through
  the globe's camera alike, the city's own ground inside its rectangle left clear on both.
- The picked ground is one shape: its outline on the block grid, the pick's tint inside, the card beside it.
- At every zoom: where a block is under a few pixels, the far views draw the outline only.

## 2. As built

Three slices after the design, each written by a Sonnet 5.5 agent at xhigh and reviewed by the Opus batch session. The
branch was cut from MS1's tip while MS1 was at its gate, and main (0.8.52) was merged in when it shipped.

- **C, the model.** `LandMarket.draft()` measures the picked ground's rectangles as offers of them at the price struck
  last, under ids -1, -2, ... of their own; `find()`/`take()` know them, the listing and everything that walks it do
  not; `update()` measures them again and drops one the city wholly owns; `attach()` drops them all; `quote()` prices a
  block for the hover. Taking a draft puts each standing offer whose free ground it meets off the shelf, unbought, the
  first such place listed first. `LandMap.Picks` picks whole blocks of the city's level joined to its ground
  (`GridOffers.onFrontier()`, the office's own frontier rule) or to the picks, floods a box from the city, turns a
  wholly picked offer into the offer, cuts the free blocks into rectangles (rows merged down) and drafts them only when
  they or the ground changed; `live()` gives the offers' ids, then the drafts'. A picked block the city buys, one cut
  off by a drop, or all of them when the city's level rises, drop with a line in the head. A block meeting a picked
  offer's free ground is not pickable, and picking an offer drops the blocks it meets.
- **D, the screen.** The globe draws each offer as its free cells and its free ground's edge through the camera; the
  plane edges an offer along its free ground on the block grid, dashed, solid when picked. The picked ground is one
  shape - tint and a two-pixel pink edge - drawn as blocks less the city's own ground, so a half-owned frontier block is
  outlined on its free half only. A click picks a pickable block (Shift toggles), a left drag from an offer's free
  ground, a picked block or a pickable one boxes, with the blocks it will take tinted under it while it is drawn; the
  hover quotes a block; the card names the offers and "Your ground - N blocks" (one chip, its x drops them all); offers
  are bought before drafts on every path. The receipt outlines the ground just bought. The picks and their drafts now
  stand through the funding page (they were cleared when the map collapsed to it, so a loan bought nothing of the
  player's ground); back from it, or leaving it, clears them.

## 3. Checks and the trace

- **LandCheck 29-29d**: a draft costs what `measure()` lists the ground at today (halves sum to the whole within the
  price's US$5k rounding); bought through `Game.buyLandParcel()` it leaves the books as a twin city's purchase of the
  same ground as an offer (cash or vault, the month's purchases, the owned area, the holding, the receipt); a draft of
  exactly an offer's rectangle leaves the shelf as buying the offer does, ids included; an offer it bites is off the
  shelf and its place listed again, one it does not touch stands; drafts are never in the listing, the AutoBuilder's
  pick, the going price or a save, and drafting moves no offer id; the month re-prices a draft under its id.
- **MapCheck 14e and 14f**: pickable and not; a box keeps only what joins (against an independent flood); the
  rectangles cover the picked free blocks once each; an offer wholly picked is the offer; a drop that cuts the chain
  drops what no longer joins, named; the level rising drops them; **an offer drawn beside owned ground has no owned
  plot inside its fill or on its edge**, and neither has the picked ground; the press, the box's preview equal to what
  the release takes, the card's words, the draft chip, offers bought before drafts. Each slice's deliberate breaks of
  its rules were caught.
- **The default playtest's trace** is byte-identical to main's but for its run-time lines: nothing in the month drafts,
  the AutoBuilder reads the listing alone, and no saved field moved. **No ensemble.**
- **`./batch-check.sh --pick`**, earth-2.bin named: see the ship file.

## 4. Left for Jerus (BL1-C, the eye check on the PC)

On the expanded map: buy an irregular stretch along a coast (drag along it, click a block in or out), and a stretch
across two sides of the city; at the globe's wider views, the offers sit outside the city's edge, not over it; an
offer's edge follows its ground; buy through the bond or note from the card (the funding page) and see the picked
ground bought. Whether a block of the city's level is fine enough is his to say: a finer grain is a later batch.
