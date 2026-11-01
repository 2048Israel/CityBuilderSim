# WN5 - land bought from its nation: the seller from the borders, the land factor, the split at a border, the territorial sea, payment in the seller's money (0.8.97, 2026-11-01)

The brief is `docs/studies/spec-nations.md` section 9 (WN5's row) and section 6 (borders and land), with D7, D21 and
D22. Jerus: "Borders baked in; land bought from its nation", and "land costs different prices purely by who you are
buying it from". Built on main after EP1 (0.8.95): the game founded in 1900, every nominal anchor at 1900's prices,
the land's two bases and price step among them. A money, saves and map batch: Opus, with three Sonnet slices (the
model, the wiring and save, the harness and the card), each reviewed and committed by Opus.

## 1. What it is

- **The land table** (`tools/world/land.tsv` -> `tools.LandTable` -> `src/main/resources/world/land-2024.json`): each
  nation's land a hectare in US$ of 2024. Published for 25: Eurostat's arable land for the 23 EU members that report
  it (Malta's last is 2023), USDA's US cropland (US$5,570 an acre), Statistics Canada's farm land and buildings (C$5,302
  an acre, the study's own pick: Canada publishes no national figure for land alone); the euro and the C$ at the Fed's
  2024 averages. The other 171 fitted: ln price = a + 0.789 ln (output a head, current US$, world-1.bin's 2024) + 0.605
  ln (people a km2, the Rankings'), R2 0.66 over the 25, capped at the dearest published (Malta, 30 times Canada).
  Canada US$9,564 a hectare, the US 13,764, the Netherlands 104,530, Germany 41,988 (fit), Japan 35,230 (fit), Brazil
  2,868 (fit), the Central African Republic 140 (fit). Every source allows a sold game (`docs/studies/licences.md`,
  the credits in `tools/world/ATTRIBUTION.txt`).
- **`LandFactors`** (new, model): a nation's land factor, its price against Canada's (exactly 1.0, D21, by no
  arithmetic). At the founding: the published ratio taken back to 1900 along the file's own output a head (the file
  holds the century before 2000 at 2000's real figures, EP1), so a Historical city meets the published ratio in January
  2024; from then it moves with the nation's real output a head against Canada's at the fit's elasticity (0.79),
  log-linear between Januaries; kept within 0.01 and 100. Founding levels: the US 1.35, the Netherlands 10.9, Germany
  4.4, Japan 3.8, China 0.31 (six times richer a head since 2000), Venezuela 0.42.
- **`LandSellers`** (new, model): the nation that sells each plot of a real-Earth world, from world-1.bin's owner
  layer at the plot's own blend point: a nation sells its own; an area (all 15 in the file name no seller) and no one
  are the host's. **The territorial sea** (D22): the owner layer gives every sea cell of a 1-degree tile its nearest
  owned cell's owner; a sea plot whose owner is a foreign nation is that nation's only when a land cell of it (the
  Earth file's landmass layer) lies within 12 nautical miles; beyond, the host's. **The split**: an offer's unowned
  ground and fields by seller, exact to `CityLand`'s own count (slice 1 checked 14 rectangles at Windsor, Gibraltar
  and Halifax against a brute-force count); an offer away from every border reads no plot.
- **The office prices each part** (`LandMarket`, `LandParcel.Sale`): today's formula on the part's own ground and
  fields, times its seller's factor in the month it is listed, the parts summed and rounded once. One part at factor
  1 is today's price to the bit. Each offer keeps its sellers: their numbers, areas and dollars (saved beside the
  offer, as its relief is).
- **Paid in the seller's money** (`Game.buyLandParcel`, `ForeignAccounts.spendReservesOnLand(usd, code)`): from the
  vault, each part first from the vault's holding of its seller's money when that is one of the eight, the rest as
  before; converting, the treasury's cash at the day's rate, as before (one rate for both sides: the D$ a part costs is
  its US$ times the D$'s rate, whichever money the seller is paid in). The receipt names the sellers when an offer is
  not the host's alone: "Sold by United States (62%, US$1.3M) and Canada (38%, C$1.1M)."
- **What the city bought from whom** (for WN6): km2 and US$ by nation since the founding, saved
  (`DataSave.landBoughtKm2`, `landBoughtUsd`); index 0 is a purchase that named no seller (a generated world's, the
  host's).
- **A generated world** has no borders: `LandSellers` is null, every offer the host's at factor 1, priced, paid and
  logged as before.

## 2. The calls (★; each takes the recommended way, BP1)

1. ★ **The land table ships in the jar, keyed by ISO code, not in world-1.bin**: the world file is Jerus's to re-bake
   (D24), and EP1b's re-bake can fold it in. Recommend as taken.
2. ★ **Which published figure**: Eurostat's arable land, USDA's cropland, Statistics Canada's farm land and buildings
   (the study's three, 6.3). Canada's includes buildings and the others do not, so Canada reads a little dear and
   every factor a little low; StatCan publishes nothing for land alone. Recommend as taken.
3. ★ **The fit is capped at the dearest published nation** (Malta): Singapore, Monaco, Macau and Hong Kong would run
   to 55-209 times Canada. Not floored: the cheapest fitted nation is 0.015. Recommend as taken.
4. ★ **The factor moves with output a head alone, not with the money** (the study's line says "and with its money").
   A price index runs on through a change of money while the new money's rate starts afresh: moved by the US$ price
   level, every nation that redenominated fell to the floor (Zimbabwe's move to 2024 read 1.4e20). The money's real
   swings mean-revert; what the city pays follows the seller's money through the rate it is paid at. Recommend.
5. ★ **An offer's price stays fixed in US$ when listed** (LandMarket, WHAT IS LISTED STAYS LISTED) and is paid in the
   seller's money at the day's rate. Fixed in the seller's money instead, a Canadian city's land would move with the
   C$, and its trace with it (the row asks Canada's to stay as it is). Recommend.
6. ★ **The territorial sea is cut from the owner layer, not drawn from the coast**: a sea cell the layer gave the host
   stays the host's even within 12 nm of a foreign coast, and an open-sea tile (owner 0) is the host's even near
   another tile's foreign coast. Recommend; a coast-drawn 12 nm belt is a world-file layer for a re-bake.
7. ★ **An area sells as the host** (the file names a seller for none of its 15: Antarctica, W. Sahara, Kosovo,
   Palestine, Somaliland and the rest), as WorldMarket's host rule reads them. Recommend.
8. ★ **The vault pays a part from its holding of the seller's money first**, the rest from the largest holding as
   before. Recommend.
9. ★ **The factor is the real ratio, undamped**: a Rotterdam city pays about 11 times what a Halifax city pays and ends
   a third smaller (section 4). A damped factor (its square root, say: 3.3 for the Netherlands) would soften it and
   misstate the prices Jerus asked for. Recommend undamped; if a played European city feels starved of land, a dial on
   the exponent is a one-line batch.

## 3. The checks

- **The suite** (`./batch-check.sh`, both data files named, on main 67f5a05e): **exit 0**, 167 harnesses green in
  2,047 s (166 starts held for the commit charge, lane B running beside it); four timing lines SOFT, not counted
  (SaveSlotCheck 1, MapCheck 3); Stale 0 firm; the maps regenerated.
- **`LandNationsCheck`** (new, 11 sections, 98 assertions, 13 s): the table against `land.tsv` (worst 6.4e-7); Canada
  exactly 1 on every path; Historical meets the published ratio in January 2024 for 195 of 195 nations (worst 2.7e-16);
  Random within the floor and ceiling every January and July 1900-2100; the hosts equal the world market's (Windsor,
  Halifax, the Strait, whose host is the United Kingdom); the territorial sea against a brute-force search over the
  landmass layer (Morocco to 21.8 km off its coast, the host from 24.2 km; the limit 22.2); the split exact to
  `CityLand` on all 21 Windsor offers; Halifax's 22 offers, Windsor's 19 Canadian ones and a generated world's 20 priced
  to the bit as before; each part paid from its seller's money (a mutation passing no money fails it); the save; and
  in a 120-month Windsor run, 4 of 54 offers split, 4 wholly the United States', 8 of 34 purchases from it.
- **SaveFileCheck, ReadPathCheck** hold the three new keys (written, read, read as none when absent); **SaveSlotCheck**
  holds the tally copied into the snapshot.

## 4. The trace and the ensemble

- **The default playtest** (4,005 months on a generated world, both data files named), traced on WN5's model (slice 2,
  37aaf41d) and on main at 67f5a05e: all eight trace files byte-identical, the log identical but for its two run-time
  lines (133 s against 139 s); both exit 0. **No ensemble** (batch.md 6).
- **On the real Earth it moves, as the row says** (`LongPlaytest --earth rotterdam`, its own baseline, traced on both):
  a Dutch city pays its seller's factor, about 11 times Canada's (published 10.9; 10.9 at the founding), and over
  4,005 months it bought 83.4 km2 of dry ground for US$274bn abroad against main's 134.6 km2 for US$56bn, the cash
  share stopping a look short 35 times against 8, and ended at 315,469 people against 471,398 (a third smaller).
  Every month passed the audit and every reload matched (offers' sellers and the tally through each save); main's run
  ends on one map finding (industry farther out than the homes, m4000), WN5's on none. **This is the call to watch**:
  dear land is real (the study's spread, 6.3) and Jerus asked for it; a European or Japanese city now grows on less
  ground. No damping is proposed (call 9).

## 5. Found on the way

- **WN4's real effective rate meets the same fault as call 4**: `Game.realEffectiveRate()` measures each partner by
  `rate(code) / rateAtStart(code)` against its price level since the founding, so a partner that redenominated since
  1900 (Zimbabwe, Venezuela, the euro members that joined after 2000) is read wrongly. Small in the index (by trade weight);
  for WN6, which shows the rate.
- **The owner layer's sea is its tile's nearest owner**, Manhattan within the 1-degree tile (FORMAT.txt): good as "the
  coast's nation" near a coast, and why call 6 cuts it rather than redraws it.
- **Canada's own regions differ sevenfold** (spec-nations 11.9): a land factor by province or state is still a later
  batch if Jerus wants it.

## 6. For the next batches

- **WN6** (the nations' screens) should show of the purchases: on the nation's card and its full page, the land the
  city bought from it (`Game.getLandBoughtKm2(n)`, `getLandBoughtUsd(n)`; index 0 the host's on a generated world) and
  its land factor now (`LandFactors.factorAt(n, month)`, through Game's sellers, with the table's mark,
  `LandFactors.mark(n)`: published or the game's estimate) beside its published price (`usdPerHectare(n)`); on Trade ›
  Partners a column for the land factor; on the land office's map, the border an offer crosses.
- **EP1b** (the re-bake): the land table could move into world-2's nations layer (call 1), and a coast-drawn 12 nm sea
  (call 6).

## 7. One in, one out

No process change: no script, check machinery or rule a session reads was touched.
