# Earth 2 mockup report (task B)

Mockup agent, 2026-10-12. Nothing under `src/` changed, nothing was committed and nothing was pushed. All files are under `docs/studies/earth2/` and untracked. Together they come to about 0.9 MB.

## Files written

- `docs/studies/earth2/earth2-study.html`
  - The board page, titled "Earth 2 Study", standalone, in the game's dark Palette look with IBM Plex from Google Fonts. Inline CSS and no JS.
  - Its pictures are separate files beside it, not base64.
  - It was checked at 1280 px and 390 px wide. At 390 px it has no horizontal page scroll; wide tables scroll inside their own boxes.
- `docs/studies/earth2/mockups.py`
  - The render script. It reuses `figures.py`'s `noise()`, `at()`, `mosaic()`, `tiles_for()` and `dest()`, and its plot rule.
  - It reads earth-2.bin's own layers through `earthread.py`: `slope` 30″ decoded F/S/V, `shallow` 15″, `landmass` and its table, `depth` 1′, and earth-1's `sea`, `lake`, `landcover`, `rivers`, `lakes.small`, `far.land` and `far.lake`.
  - `python3 -I mockups.py [part…]` rewrites every PNG and prints every figure the page quotes as JSON. The whole run takes about 8 s.
- 14 PNGs:
  - `mock-medellin-a.png`, `mock-medellin-b.png`
  - `mock-medellin-l0-a.png`, `mock-medellin-l0-b.png`
  - `mock-halifax.png`, `mock-halifax-narrows.png`
  - `mock-everest.png`
  - `mock-confederation.png`
  - `mock-far-andes.png`, `mock-far-himalaya.png`, `mock-far-strait.png`
  - `mock-globe-tibet.png`, `mock-globe-maritimes.png`
  - `mock-landoffice-map.png`

## What each board shows

### 1. The map
- **A table of the tiers:** Plane L0, L1/L2, far nodes; Globe GROUND (0.48–7.41 km/px); Globe PICTURE (4′ texel). For each it says how slope, shallow sea and bridges are drawn.
- **D5, A against B on Medellín** (18 km at 1 px a plot), with an 8 px a plot close-up of each and option cards. B is recommended.
  - B mixes forest toward its slope's grey: 50% on foothill, 50% on steep, 60% on very steep (est.).
  - Measured on this render: 25% flat, 50% foothill, 24% steep, 1% very steep. 37% of the land is forest on a slope.
  - Way A shows forest on only 9% of the land; way B shows all 46%.
- **Halifax**, drawn as the expanded-map window ("Land office › Map") at 2 px a plot.
  - It shows the city's ground undimmed, the rest dimmed, with a legend overlay and a hover card.
  - The Narrows and the harbour are shallow; Bedford Basin is deep.
  - A 5 px a plot close-up shows the MacKay laid as a highway on its DECK. The Macdonald is shown dashed amber: the run would not lay it (see the findings).
- **The Khumbu** (30 km at 1 px a plot): the three greys and snow, with the 5 km circle round Tengboche.
- **The Confederation Bridge** as one order: 1 px a plot, the city's ground on both shores, one deck.
- **Far views (way B):** the Andes round Medellín at 0.5 km/px, the Himalaya at 1 km/px, and the Northumberland Strait at 0.5 km/px.
  - The strait view shows the Confederation deck and the refused Caribou–Wood Islands line in red.
- **The globe's picture tier** (4′, 7.4 km), two swatches:
  - Tibet and the Himalaya: high but flat.
  - The Maritimes: the shallow sea shows only where it is wide (the Strait, the Magdalen shallows, Sable Island Bank).

### 2. Legend and hover
- A table of every class: swatch, legend entry, hover first line, buildable or not, and how it is priced on the land line.
- The plane legend and the globe legend (PICTURE/GROUND), with the new entries tagged.
- Four mock hover cards: a Medellín foothill offer, forest on steep ground, Bedford Basin deep sea, and the Khumbu on the globe.

### 3. The land office and the founding card
- **The land office**, laid out as LandScreen draws it: the 600×400 map (Medellín at 2 px a plot, East's six offers), THE CITY line by class, side chips, and rows.
  - The rows' ground bar runs flat · foothill · steep · fresh · sea.
  - The last price column is "a buildable km², prepared".
  - The East 3 offer card shows its ground by class with km², price a m², value, and buildable ground per US$1M as listed and prepared.
- **Three founding cards**, in fillCard's order, with the new lines marked:
  - Halifax: flat 85%, foothill 15%; all its sea within 5 km shallow; 19 m deepest 1′ cell; the snap moves 0.3 km.
  - Medellín: flat 42%, foothill 45%, steep 12%; 68.6 km² buildable.
  - Tengboche: refused, with the refusal words and a pointer: "the nearest is 5.5 km west".
  - Each card has a landmass line ("Its landmass: New York's, 36.7 million km² (id 2)").

### 4. The land line
- Manufacturing's statement of financial position with Land ▾ 11 open: flat and foothill, by StatementView's noteRows.
- Quarrying's long-lived assets with note 11: flat, foothill, steep and very steep, all at the base price for the steep classes.
- The city is a crowded Medellín (going price M$170/m²), so the foothill subtraction shows (M$38.75). Every figure is illustrative and the arithmetic is exact.

### 5. Build › Infrastructure › Bridges
- **Highway Bridge** card, a valid order:
  - links 5,596.5 km² Prince Edward Island (landmass 103) to the home landmass (2);
  - 12.8 km over shallow sea, 1–24 m deep;
  - T$1.70B all in (est.), with bars and the needs line;
  - "One order" and "Build the crossing".
- **Rail Bridge** card and a compact **Road Bridge** card.
- **Four refused orders.** Each uses the study's §6.3 words verbatim with this data's figures:
  - No bridgeable path: Caribou–Wood Islands, deep water (32 m) 11.1 km out, 47 m at the deepest. Measured.
  - The far side is not dry land. Illustrative.
  - Longer than a bridge can be: Bahrain–Qatar, 41.8 km of shallow sea, 6 m at the deepest. Measured.
  - Both ends on Halifax's landmass. Measured.

## Figures not taken from the code or the data (marked on the page)

- **Foothill preparation:** about US$105/m² (est.). This is the study's §4.1 figure; the board's intro restates its arithmetic.
- **Forest mixes for B:** 50%, 50% and 60% (est.).
- **Bridge prices (est.):**
  - Based on C$1.3 billion (Wikipedia, read today) for 12.9 km, scaled ×1.8 for prices since 1995 at C$1 = US$0.73, giving about US$133M a km.
  - Rail is ×1.35 and a road ×0.55.
  - Running cost, landings, sales tax and the ~43-month queue are also estimates. The 43 months is the Confederation Bridge's own build time.
- **Illustrative (a made-up city's figures):**
  - The cities' holdings, offer places and months.
  - The going prices: US$21.40/m² for the office; M$170 (and M$168.20 last month) for the statements.
  - The M$1.25 exchange rate, the T$ money, and every balance sheet line except the land arithmetic.
  - The side chips for North, South and West.
  - The stone field on East 2.
  - "The far side is not dry land" example.
- **Medellín's geology multipliers** are taken as the fold belt's at no known petroleum, the same as Halifax's from the code report's probe (est.). The province is "West-Central Cordillera", a fold belt with 0 MMBOE.
- **The "nearest iron field: 18.0 km south-west"** on Halifax's card is the code report's probe field (seed 758,809,812), measured from this site.

## Findings while rendering (for the orchestrator and the study)

1. **The 14-plot short crossing does not fit Halifax, which contradicts the study's D12 and §6.2.**
   - Along the real bridges' lines, earth-1's 15″ sea share passes ½ for 820 m (Macdonald) and 805 m (MacKay).
   - With the plot rule's fray, a run meets 555–1,000 m under the Macdonald and 295–915 m under the MacKay over 8 seeds. In 7 seeds of 8 the MacKay is over 420 m; in all 8 the Macdonald is.
   - Halifax and Dartmouth are one landmass, so the long order refuses the crossing too (D14). **On most seeds neither real bridge could be built.**
   - The BIO "300 to 400 m" is the sill's width under water, not the width from shore to shore.
   - The study needs one of two changes:
     - a highway or rail short cap of about 34 plots (1 km) on Earth (est.); or
     - long orders allowed within one landmass where the water is wider than a run's bridge.
2. **The shallow share must be interpolated weighted by the sea share.**
   - It is a share of a cell's sea, and 0 on land. Read plainly, it drew a fringe of deep sea along every coast; Halifax's nearest "deep" plot was 0.58 km away when all its sea is shallow.
   - `figures.py` reads it plainly too, so its halifax.png has the same fringe. `mockups.py` weights it. The game's rule (ET3) should do the same.
3. **The coast's fray (±0.3 of a share in the water noise) moves a narrow channel's width by about ±250 m between seeds.** That alone decides whether a run bridges it.
4. **The Confederation Bridge's cost:** Wikipedia says C$1.3 billion; the study's §6.4 says C$1.0 billion (about C$78,000 a m). This needs settling in ET6's brief.
5. **PEI's area:** the landmasses table gives **5,596.50 km²** (38,320 cells, Charlottetown). The brief's 5,686 km² is the real-world figure. The card uses the table's.
6. **Odd shares in the Khumbu, from earth-1's land cover rather than earth-2's slope:**
   - The 2′ ice bit covers 77% of a 20 km box round Everest, bare rock faces included (38% of the 30 km box).
   - A few desert-bit (bare ground) patches appear on high moraine.
7. **The study's §3.4 card spec includes a landmass line.** I added it ("Its landmass: New York's, …"). The landmass table's place name is the only name it has; it does not have "the Americas".
8. **Limits of the mock renderer** (not the game's):
   - The noise is relative to each box, so another crop of the same place draws different detail.
   - The plane is drawn in latitude and longitude, not Lambert.
   - Small lakes are frayed discs; beaches are not drawn; buildings and streets are left out.
   - Tunnels and viaducts for runs (study §4.3) are not drawn.

## Data used, all measured here

**Profiles** (GEBCO 2026 at 15″, earth-2's shallow, depth and landmass layers along straight lines; the planner's own search could find shorter ways):

| line | water | depth | shallow | landmasses |
|---|---|---|---|---|
| Confederation | 12.8 km | GEBCO 1–24 m | all 15/15 | 103 (PEI) ↔ 2 |
| Caribou–Wood Islands | 21.8 km | deep from 11.1 km out (32 m), 47 m deepest | — | 2 → 103 |
| Bahrain–Qatar | 41.8 km | 6 m deepest | all shallow | 400 (618.45 km², Manama) → 1 |
| Halifax–McNabs | 1.79 km | 2–20 m | — | 2 → 9757 (3.32 km²) |
| Macdonald | — | 6–21 m GEBCO; 1′ layer 4–11 m | — | 2 → 2 |
| MacKay | — | 7–14 m GEBCO; 1′ layer 13–19 m | — | 2 → 2 |

**Founding cards** (within 5 km, plot by plot on the renders):

| spot | the snap | land | water | landmass |
|---|---|---|---|---|
| Halifax | 0.34 km | 74% dry, 2% fresh, 24% sea; flat 84.9%, foothill 15.1%; 49.5 + 8.8 km² buildable | fresh 3.0 km, sea 0.6 km, all shallow, 19 m 1′ max | 2 |
| Medellín | 0 km | 99% dry; 42/45/12/<1; 33.1 + 35.5 km² buildable | fresh 1.0 km | 2 |
| Tengboche | no site within 5 km; nearest 5.5 km west | <1/6/34/59, ice 1% | — | — |

## Update after the study's change (34-plot short crossing; landmasses re-baked)

Re-ran `mockups.py` on the re-baked earth-2.bin (135,965,097 bytes, SHA-256 6ccb4794…fa8c3). `SHORT_BRIDGE_M` is now 34 plots (1,020 m).

### Figures that moved

Every render and every other figure is unchanged, byte for byte where they are drawn from the same layers. Only these moved:

| figure | before | after |
|---|---|---|
| Prince Edward Island | landmass 103, 5,596.50 km² | **101, 5,611.10 km²** (38,449 cells) |
| Bahrain | landmass 400, 618.45 km² | **340, 681.75 km²** |
| The Americas (id 2) | 36,718,576 km² | **36,812,247 km²**, so the cards now say 36.8 million km² |
| Qatar | landmass 1 | landmass 1 (unchanged) |
| McNabs | landmass 9,757 | **joined to landmass 2** (not quoted on the page) |
| the Macdonald on the Halifax render (800 m, 27 plots) | not laid | **laid** |

### Page changes
- **Board 1, Halifax:**
  - Both bridges are drawn as decks; the amber dashed line is gone.
  - The paragraph and caption now give the 34-plot (1,020 m) cap, raised from 14 because the Narrows is about 800 m of water from shore to shore.
  - The amber "Finding" box is now a green "Found while drawing, and settled" note: the sill against shore-to-shore water, and the study's change (§6.2, D12; §2.6, D16).
- **Board 1, Confederation:** landmass 101, 5,611.10 km².
- **Board 2:** the deck's hover line is now "27 plots (810 m)", the Macdonald on board 1, so the plot count reads true.
- **Board 3:** the founding cards' landmass line says 36.8 million km².
- **Board 5:**
  - The Highway Bridge links 5,611.1 km², landmass 101; Bahrain is landmass 340, 682 km².
  - The same-landmass refusal now says water within one landmass is crossed by the runs' short bridges, 34 plots (1,020 m) at most, and that the Narrows' ~800 m is inside that.
  - The cost note: the study takes C$1.3 billion (Wikipedia); some sources give C$1.0 billion; ET6 settles which.
- **Footer:** the new file size and hash, and the fray note now says the 34-plot cap keeps a margin over 820 m.
