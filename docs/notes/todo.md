# The list — what is open

Updated 2026-10-06 (0.7.53 and 0.7.54: cities past 2.1 billion, deployed and
verified as tag 1006c, not yet seen on the PC). Next: the land, the water and
the world's deposits, then fuel. Open now: the PC check of 0.7.34 to 0.7.54 and
Jerus's word on the ★ decisions; the manual is at version 13.
What shipped is in `changelog.md`,
newest first, with the state of the tree in its top block; this file is the
list alone. `index.md` maps the design notes by subsystem, and `CLAUDE.md` in
the repository is what a session reads before touching source. A session that
has been away reads the changelog's top block and section 0 here, then works.

## 0. Do this week — costs nothing, saves weeks

- **WORKING LEANER (agreed with Jerus, 2026-10-03; usage hit 98% after the
  0.7.32-0.7.39 run).** From the next session on:
  - **Start each session fresh:** read this section and the changelog's top
    block, not a long carried-over conversation.
  - **Sonnet trial:** the next records pass runs on Sonnet; the orchestrator
    checks its figures against the notes, missed ★ decisions and stale
    markers, and reports. If clean, records and write-ups stay on Sonnet;
    implementers and docs passes stay on Opus.
  - **Keep:** the fresh-context docs pass (it caught the fund's cap gap, the
    small charts' missing flags, false strings), the research spec for big
    screens, the full gate.
  - **Leaner briefs:** name the exact methods; agents navigate by `docs/map`
    instead of reading whole files.
  - **Shorter notes:** a fixed, capped shape (headline, changes, PC checks,
    ★ decisions, gate).
  - **A shared probe kit** in the tree for loading the research cities.
  - **Less repetition:** records and write-ups once a session; the
    generated docs deployed once at the end, sources each time; smaller
    batches so no agent's context fills.
  - **Jerus answers CONFIRM in one message**, and does the PC check himself
    with a checklist, sending only what looks wrong (screen control only
    for what he can't judge by eye).
  - **Model fixes he approves go in one batch.**
  - **One message per batch** from the orchestrator.

- ~~**NEXT: THE TREASURY FUND AND RESCUE-FOR-SHARES**~~ — shipped as 0.7.14
  (tag 0927a), see `the-city-takes-the-shares.md`: the fund, resolution for
  shares with the owners wiped out, TARP preferred, and the Insane start.
- ~~**NEXT: NOTES AND BONDS**~~ — shipped as 0.7.12 (tag 0926a), see
  `the-firms-sell-bonds.md`.
- ~~**NEXT: THE MANUAL, FROM 0.7.3 TO 0.7.15**~~ — published 2026-09-28 as
  version 10, see `the-manual-at-0-7-15.md`. `docs/manual.md`,
  `docs/manual.html` and the three `docs/notes/` copies deployed and verified
  the same day as tag 0928b.
- ~~**NEXT: THE MANUAL, FROM 0.7.15 TO 0.7.23**~~ — published
  2026-10-01 as version 11, see `the-manual-at-0-7-23.md`; `docs/manual.md`,
  `docs/manual.html` and the three `docs/notes/` copies deployed and verified
  the same day as tag 1001e.
- ~~**NEXT: THE RAIL'S SCREENS ONE AT A TIME**~~ — **every rail screen done,
  0.7.24 to 0.7.37** (~~0.7.25, the build card; 0.7.26, the Land office;
  0.7.27, People; 0.7.28, Services; 0.7.29, Infrastructure; 0.7.30, Sectors;
  0.7.31, Government; 0.7.32, Finances; 0.7.33, the Bank; 0.7.35, Trade;
  0.7.36, Policy; 0.7.37, City History~~, with 0.7.34's buttons between them);
  ~~0.7.38, the loose ends of the redraw~~, **done 2026-10-02** (below).
  Jerus, 2026-10-01, after seeing 0.7.24:
  "also the build card for every building, i think the card itself needs a
  redesign dont you think?", then "also im going to work, so try to ask the
  minimal amount of questions, and try to work on all the rails one by one by
  one". One batch a screen (`playing-0-7-23-ui-notes.md` §4 has a row for
  each):
  - ~~**0.7.25, the build card**~~ — **done 2026-10-01**: built and gated from
    the design study `runs/card-spec-0725.md`, deployed and verified as tag
    1001j (36 files), seen on the PC on 2026-10-01 in Jerus's own play of
    0.7.31 (below), see `one-card-for-every-building.md`;
  - ~~**0.7.26, the Land office**~~ — **done 2026-10-01**: built and gated from
    the design study `runs/spec-land-0726.md`, deployed and verified with
    0.7.27 as tag 1001k (72 files), seen on the PC on 2026-10-01 in Jerus's own
    play of 0.7.31 (below), see `the-land-office-redrawn.md`;
  - ~~**0.7.27, People**~~ — **done 2026-10-01**: built in a worktree from
    `runs/spec-people-0727.md` while 0.7.26's docs pass ran on the main tree,
    merged three ways (every merge clean, the full gate green on the merged
    tree, Maps regenerated), SAVE_FORMAT 30; deployed and verified with 0.7.26
    as tag 1001k, seen on the PC on 2026-10-01 in Jerus's own play of 0.7.31
    (below), see `people-at-a-glance.md`;
  - ~~**0.7.28, Services**~~ — **done 2026-10-01**: built in a worktree from
    `runs/spec-services-0728.md` while 0.7.27's docs pass ran on the main tree,
    merged three ways (every merge clean, the full gate green on the merged
    tree), its docs pass run on the merged tree; SAVE_FORMAT 30; deployed and
    verified with 0.7.29 to 0.7.31 as tag 1001m, seen on the PC on 2026-10-01
    in Jerus's own play of 0.7.31 (below), see `services-at-a-glance.md`;
  - ~~**0.7.29, Infrastructure**~~ — **done 2026-10-01**: built in a worktree
    from `runs/spec-infra-0729.md` while 0.7.28's docs pass ran, merged three
    ways (one conflict, in ServicesScreen, where the implementer had deleted
    `roadInfo()`/`ROAD_INFO` and the docs pass had edited their text: the
    orchestrator took the deletion; the full gate green on the merged tree),
    its docs pass run; SAVE_FORMAT 30; deployed and verified with 0.7.28,
    0.7.30 and 0.7.31 as tag 1001m, seen on the PC on 2026-10-01 in Jerus's own
    play of 0.7.31 (below), see `the-road-in-one-picture.md`;
  - ~~**0.7.30, Sectors**~~ — **done 2026-10-01**: built in a worktree from
    `runs/spec-sectors-0730.md` while 0.7.29's docs pass ran, merged three ways
    (every merge clean, the full gate green on the merged tree, 68/69 with the
    new SectorFlowCheck), its docs pass run; SAVE_FORMAT 30; deployed and
    verified with 0.7.28, 0.7.29 and 0.7.31 as tag 1001m, seen on the PC on
    2026-10-01 in Jerus's own play of 0.7.31 (below), see
    `the-sectors-as-flows.md`;
  - ~~**0.7.31, Government**~~ — **done 2026-10-01**: built in a worktree from
    `runs/spec-government-0731.md` while 0.7.30's docs pass ran, merged three
    ways (the merge clean, the full gate green on the merged tree), its docs
    pass run on the merged tree (the implementer was cut off after its gate, so
    the docs pass also wrote `runs/ui12-notes.md` §§1–8 from the code);
    SAVE_FORMAT 30; deployed and verified with 0.7.28 to 0.7.30 as tag 1001m
    (134 files: the 57 source, CLAUDE and README files staged back and
    identical byte for byte, the docs files committed), seen on the PC on
    2026-10-01 in Jerus's own play of 0.7.31 (below), see
    `earned-surplus-banked.md`;
  - ~~**0.7.32, Finances**~~ — **done 2026-10-01**: built on the main tree from
    `runs/spec-finances-0732.md` (from 0.7.31 final, after its docs pass), its
    docs pass run; SAVE_FORMAT 30; deployed and verified with 0.7.33 as tag
    1001n (25 source files identical byte for byte on the PC), seen on the PC
    in the orchestrator's one look at 0.7.33 (below), see
    `the-debt-at-a-glance.md`;
  - ~~**0.7.33, the Bank**~~ — **done 2026-10-01**: built in a worktree from
    `runs/spec-bank-0733.md` while 0.7.32's docs pass ran, merged three ways
    (clean, the full gate green on the merged tree), its docs pass run; B5
    fixed on the load path with the eight traces byte-identical; SAVE_FORMAT
    30; deployed and verified with 0.7.32 as tag 1001n, seen on the PC (below),
    see `the-bank-at-a-glance.md`;
  - ~~**0.7.34, buttons that ask to be pressed**~~ — **done 2026-10-01**:
    Jerus's one request after playing 0.7.31, with the look's two other
    findings (Finances' "later", Sectors after a load); built in a worktree
    from the brief (`runs/brief-ui15-0734.md`; there was no study) while
    0.7.33's docs pass ran, merged three ways (clean, the full gate green), its
    docs pass run; committed with 0.7.35 and 0.7.36 as tag 1001o, not yet
    verified, then deployed and verified as tag 1001p (2026-10-02), not yet
    seen on the PC (below), see `buttons-that-ask-to-be-pressed.md`;
  - ~~**0.7.35, Trade**~~ — **done 2026-10-02**: built in a worktree from
    `runs/spec-trade-0734.md` (its D4 not built: MODEL BUGS item 2) while
    0.7.34's docs pass ran, merged three ways (clean, the full gate green), its
    docs pass run; `fxParity` a new History series, SAVE_FORMAT 30; committed
    as tag 1001o, then deployed and verified as tag 1001p, not yet seen on the
    PC (below), see `trade-at-a-glance.md`;
  - ~~**0.7.36, Policy**~~ — **done 2026-10-02**: built in a worktree from
    `runs/spec-policy-0735.md` (its D7 and D18's step 1 not built: MODEL BUGS
    item 4) while 0.7.35's docs pass ran, merged three ways (clean, the full
    gate green at 69/70 with the new PolicyPreviewCheck), its docs pass run;
    SAVE_FORMAT 30; committed as tag 1001o, then deployed and verified as tag
    1001p, not yet seen on the PC (below), see `policy-at-a-glance.md`;
  - ~~**0.7.37, City History**~~ — **done 2026-10-02**: built in a worktree
    from `runs/spec-history-0736.md` while 0.7.36's docs pass ran, merged three
    ways with one conflict, in `UserInterface`'s screens index (both paragraphs
    kept; the full gate green on the merged tree), its docs pass run
    (`runs/ui18-docs-pass.md`: one player string on History, comments in five
    sources, DecisionLog's outside the changed set; the deploy set 28 paths);
    SAVE_FORMAT 30; deployed and verified as tag 1001p, not yet seen on the PC
    (below), see `city-history-finished.md`;
  - ~~**0.7.38, the loose ends of the redraw**~~ — **done 2026-10-02**: built
    in a worktree from `runs/brief-ui19-0738.md` (no study) while 0.7.37's docs
    pass ran, merged three ways (clean: two files, HistoryScreen and YearBook,
    merged with 0.7.37's docs pass's edits; the full gate green on the merged
    tree), its docs pass run (`runs/ui19-docs-pass.md`: comments in 7 files,
    no player string).
    Eleven display fixes the redraw left, none needing Jerus's word: a small
    chart's flags; the founding month's decisions on Trade's, Finances' and the
    Bank's lanes; "0.0 months" of cover; the floor in founding money on People
    and History; the fare onto `Levers.dialCard`; a shut-out sector's red name;
    Sectors' rate bar on `quoteParts()`; the drawer's "vs parity"; a method
    name in the pension card's (i); `adviceTotal()` on `quoteTotal()`; four
    dead members (nine went). Each is marked "done in 0.7.38" where this list
    carried it. SAVE_FORMAT 30; deployed and verified as tag 1001p after its
    docs pass, not yet seen on the PC (below), see
    `the-loose-ends.md`.

  Noticed for them in 0.7.24:
  - ~~Finances' and Trade's hubs look narrow in the stage 0.7.24 widened;~~ —
    done in 0.7.32 and 0.7.35, both at Build's width;
  - the drawer's content (the City overview) is still in the old ledger
    style.
- ~~**NEXT: THE CITY'S FUND AS A BROKERAGE**~~ — **done 2026-10-02 as
  0.7.39**, deployed and verified as tag 1002a (2026-10-02: 61 files, the 20
  source, CLAUDE and README files compared byte for byte). Jerus, early on
  2026-10-02, after the overnight batches: "Oh additional note, when you get
  to the city fund, when you click buy manually i want it to be like
  wealthsimple trade type kinda like a brokerage, where you can search the
  shares and bonds and see and all, and also the city fund should show pnl
  and acb and all that". Built on the main tree from the read-only study
  `runs/spec-fund-0739.md` (its ★ D1–D12 decided as it recommends) by the
  brief `runs/brief-ui20-0739.md`; the implementer's notes
  `runs/ui20-notes.md`, its changed list `runs/ui20-changed.txt` (61 paths, 8
  new); its docs pass run (`runs/ui20-docs-pass.md`: comments in nine sources
  and README, two player strings, twelve flags, the gate green again), then
  three fixes in the same version (`runs/ui20b-notes.md`: the 10% cap
  counting every order the fund has on a company, the rule's bid making way
  for the hand's, ★; a seeded rescue lot asserted; Search's young-city words;
  FundLedgerCheck 87 → 129 checks; the gate green again, the traces
  byte-identical). Finances › The city's fund as four pages (Portfolio,
  Search, Activity, Rules & cash) and a page a security with YOUR POSITION
  beside THE ORDER TICKET; `FundLedger`, each holding's adjusted cost base by
  the average-cost method with its realized P&L and its income, booked where
  the holdings already move, an older save seeded at market value ("cost
  from"); three History series; the hand names its price (★D2) and a waiting
  order can be cancelled (★D3); a hand buy stops at the 10% cap (★B1) and a
  queued buy's cash is held from the rule (★B9). FundLedgerCheck the 71st
  harness; the gate 70/71, HealthCheck the known red; SAVE_FORMAT 30; the
  eight traces byte-identical. Not yet seen on the PC (CHECK, below); its ★
  decisions under CONFIRM, its model finds MODEL BUGS items 19–22; see
  `the-fund-as-a-brokerage.md`.
- ~~**CITIES PAST 2.1 BILLION**~~ — **done 2026-10-06 as 0.7.53 and 0.7.54**,
  deployed and verified as tag 1006c (140 files; the 77 code files compared byte
  for byte), see `spec-scale.md` (the study, read-only on 0.7.52; the builds are
  in `runs/fixH1-notes.md`, `runs/fixH2-notes.md` and `runs/docs-0754.md`).
  Jerus plans cities of 5 to 10 billion and asked for confirmation that "an
  enormous city, utterly enormous, can still run with no issue"; the study said
  yes, after named fixes, and he said to start on "the pop limit fix and the
  size stuff". Built, from the study's §8: step 1 (the three order searches),
  step 3 (counts to `long`), step 4 (money tolerances and quadrillions) and step
  5 (`ScaleCheck` and `OrderSearchCheck`); SAVE_FORMAT 30, traces
  byte-identical. Not built: step 2, the land premium (Jerus's call, CONFIRM
  from 0.7.53 to 0.7.54), and steps 6 and 7, land purchases proportional to the
  city, the map and the world, which are in the NEXT item below. Not yet seen on
  the PC (CHECK 0.7.53 TO 0.7.54 ON THE PC, below).
- **NEXT: THE LAND, THE WATER AND THE WORLD'S DEPOSITS, THEN FUEL** (Jerus's
  plan, 2026-10-06: to be designed, then built). **Units and water:** land in
  km², the city's total size shown; the world is the Earth's surface, water
  included. Fresh water refills monthly up to a limit per km² that ordinary
  water plants draw on; salt water feeds a new desalination plant (needs a
  coast, costs more, uses a lot of electricity; **Jerus, 2026-10-06: salt water
  is cheap and unused for now**). **World totals** (iron ore, oil, stone,
  forest, coal, copper, uranium): buying land with a deposit moves it from the
  world to the city, extraction depletes the city's share, forest regrows; at
  first only iron ore and oil (fuel, next) are used. **Buying:** click a side
  for its offers; a Build shortcut buys the best of any side. **Old saves:**
  owned land stays the center, with new sides; the save format changes.
  - **Nine chunks:** the center (nearly all the land); four owned newest strips
    (N, S, E, W), the latest purchase each way, about 5% of the city; four offer
    strips beyond them, each touching only its strip. ~~When a side doubles its
    older part rolls into the center (rule open; the mockup did it on each
    purchase).~~ **No side rolls** (Jerus, 2026-10-06): the world is generated
    around the city and its deposits are visible on the map. **Offers** follow
    what a side's newest strip holds, by density, not presence; they are at
    least about 1% of the city and price deposits at world prices; ~~they
    refresh on a timer and after a purchase on that side~~ **10 offers a side,
    never rerolled, and each stays listed until bought** (Jerus, 2026-10-06);
    ~~optionally, surveys as estimates~~ **no surveys** (Jerus, 2026-10-06).
    **Finite fields:** a side striking oil draws a field size (most small, a few
    huge); offers follow what is left, then fall back to the background rate;
    fields come out of the world's remaining total, so grow scarcer.
  - **The map** (painted, procedural): roads grow from their ends as gravel,
    paved or highway; buildings take plots beside roads first, then the rows
    behind, never blocking one; it pans and zooms with level of detail, stored
    per strip as aggregates plus a seed, so cost follows the screen, not the
    population (10B+ people). Mockup 2026-10-06:
    https://claude.ai/artifact/2qoejEU7PqCG4xCAce4a1K (★ calls in
    `runs/mockup/map-notes.md`). ~~**Awaiting** his reaction and answers on
    double-the-side rule, water's uses, offers per side, surveys as estimates.~~
    **Answered 2026-10-06** (Jerus): no side rolls; 10 offers a side, never
    rerolled, each listed until bought; no surveys; salt water cheap and unused
    for now. **Still awaiting** his go for this batch.
  - **What the scale study binds** (`spec-scale.md` §6 to §9; Jerus plans cities
    of 5 to 10 billion, and 0.7.53 and 0.7.54 made the model ready for them).
    The design above has to keep to these:
    - **Land purchases proportional to the city,** like the mockup's 5.5% strips
      (§8 step 6): his parcels are 0.2 to 2.4 km² (`landListing`), so 10B people
      (1.76M km²) would take on the order of a million purchases at today's
      size.
    - **The map stored as districts** (§6, ★7), in a binary, deflated sidecar
      file beside the save, not in the JSON: about 11 MB at 10B (29,830
      districts at Jerus's density; 5.4 MB at 5B), against about 90 MB if tiles
      were stored (1.9M records). Tile counts are derived on demand by a stable
      per-building deal, tiles are painted from seed + counts, and terrain comes
      from the seed and is never stored; plot coordinates 64-bit or
      tile-relative (3.28B plots at the sparser density pass 2^31).
    - **The world in 64 km cells** (§7, ★8): 124,512 cells over the Earth's 510M
      km², the deposits of six kinds drawn from the seed in 52 ms; **only the
      depletion is stored**, per district and kind (about 1.4 MB at 10B); world
      totals from one 64 km pass at founding, or the closed form; visible
      deposits generated per km² in view.
    - **The land premium** (§8 step 2, ★3; `LandMarket.java` :152-155 and
      :328-329, 0.7.52's lines): linear in absolute size, about 19,500 times
      today's price per sq ft at 10B, and at 5 to 10B a city scaled to size
      builds nothing and loses about 1% of its people a month. Jerus's call
      (CONFIRM from 0.7.53 to 0.7.54); recommended: tie it to density rather
      than to absolute size (the study's other option was to saturate it); about
      half a day once he decides.
    - **A gameplay question as well as a scale one** (§9): 10B people on a 1,326
      km square, at Jerus's density, means travel, the reach of services and one
      city's two highways.
- **STANDING: THE PC CHECK OF 0.7.34 TO 0.7.54, AND THE CONFIRM LISTS AWAITING
  JERUS.** With 0.7.53 and 0.7.54 built, docs-passed and deployed (tag 1006c)
  after 0.7.52 (tag 1006b), 0.7.50 and 0.7.51 (tag 1006a), 0.7.46 to 0.7.49 (tag
  1005b), 0.7.42 to 0.7.45 (tag 1005a) and 0.7.40 and 0.7.41 (tag 1004a): the
  check by eye of 0.7.34 to 0.7.54 on his PC, none of it yet rendered (CHECK
  0.7.53 TO 0.7.54 ON THE PC, CHECK 0.7.52 ON THE PC, CHECK 0.7.50 TO 0.7.51 ON
  THE PC, CHECK 0.7.46 TO 0.7.49 ON THE PC, CHECK 0.7.42 TO 0.7.45 ON THE PC,
  CHECK 0.7.40 AND 0.7.41 ON THE PC and CHECK 0.7.34 TO 0.7.39 ON THE PC,
  below); ~~the manual,
  still version 11 at 0.7.23, which now has to run to 0.7.45, from its entries
  "And 0.7.24" to "And 0.7.39" (below), the docs passes' "The manual" sections
  and, for 0.7.40 to 0.7.45, the false sections listed here~~ — **done
  2026-10-05: published as version 12 at 0.7.49** (the same artifact, version id
  1791223550-debf; `docs/manual.md` and `docs/manual.html` regenerated), see
  `model-fixes-fund-and-transit.md` §5, **and as version 13 at 0.7.51 on
  2026-10-06** (version id 1791264114-68fa), see
  `chart-crash-and-build-advice.md` §4 (version 14, for 0.7.54, is to be
  published with the next batch); ~~Jerus's word on the model questions:
  Trade's D4 (MODEL BUGS item 2), Policy's D7 / B8 (item 4), the epidemic (item
  18) and the fund's stale mark, history precision, idle bids and cheap ten-year
  bonds (items 19–22)~~ — **all built** as A2, B8, A8, C4, C5 and C3 (item 22
  dropped with proof); and Jerus's word on the CONFIRM lists' ★ decisions:
  0.7.53 to 0.7.54's first (the land premium at 5 to 10B, history trimming, the
  arrears line, `Formats.cash()` at 2^53, the 1,500 ms bound, the halving
  proofs, H1's and H2's other ★ calls), then 0.7.52's (the strictness dial's
  ends, the 0% floor at his 0.5% target, credibility against the Standard rule,
  old autosaves left as they were), then
  0.7.50 to 0.7.51's (the build advice's 5% slack and 100% target, its
  6-month horizon with the wait capped at 12, the cash cap removed, land at the
  office's price, higher education only for students who would be hired, a
  first school half full, one building type per need; the Home Daycare counts,
  the two land figures on one card, the freeze tied to the chart fault,
  `clearMenu`'s filter, the log past its cap), then 0.7.46 to 0.7.49's (B8's
  large effect on the default playtest, the
  ensemble's two new fails K2 and R3, the fare cap at 27.6% of an unskilled
  household's take-home, fuel real and imported, the withdrawal's sale at fair
  value, the fund summary's amber, `SAVE_FORMAT` 30 against a 0.7.45 build),
  then 0.7.42 to 0.7.45's (S1's window, H1's acceleration, rent through the
  landlords' interest, the shelf cap and A2, the grocers at their floor, B16's
  smoothing, credibility's speeds), then 0.7.40's and 0.7.41's (the road's red
  line at 111% served, care over 100% on the rings, a run funded only up to a
  refusal), then 0.7.39's D2 and D7.
  - ~~**The manual's false sections at 0.7.45** (`runs/ui26-docs-pass.md` §3;
    `docs/manual.md` was not edited): the header table and §1, 2, 3, 4, 7, 8, 9,
    11, 12, 14–16, 18 and 21–25, in short:~~ **all rewritten in version 12
    (2026-10-05); the list stays as the record of what was false:**
    - **Header and intro:** 0.7.45, format 30, 252 files, ~233,000 lines, 74
      harnesses; the anchor, prices that clear, vouchers.
    - **§1 The month:** a new first step (constants struck at the expected
      level); step 3 wages half on expected inflation; step 6 the strike settles
      supplier credit; step 13 vouchers paid after the markets; step 15 the
      anchored drift, the five-part index and the anchor.
    - **§2 Households:** groceries asked for at a price (a basket a head,
      satiation 1.5× the floor, elasticity .4, its money); the plan is the
      discretionary plan; hunger is baskets got against needed (priced out, or
      short of stock); the real deposit rate less EXPECTED inflation; 37 cell
      slots (+4 groceries, +1 smoothed means income).
    - **§3, §11, §4, §9, §14, §18:** the FIXED grant at the expected level and
      the pension base struck at it (no longer frozen); §11's wages half
      expected inflation, half a forty-eighth of the gap (LabourCheck's sentence
      about DRIFT_PER_MONTH is out of date); the price at the door, "The price
      keeps up", fees and paid-in, and land, struck at Pe.
    - **§7 Goods and §8:** Groceries "cost-plus" becomes a floor, a clearing
      price over it, a 1.5× cap, a sixth a month, drifting, with supplier
      credit; §8's hurdle and the landlords' lender at the real rate (0.7.42,
      0.7.44) and Retail's planner on baskets against hand-over.
    - **§12 Policy:** Promises has six tabs (Food) and a food assistance dial;
      EARNED's walk has the vouchers.
    - **§15 and §16:** on target the rule sets the neutral real 1% plus the
      target; realDepositRate on expected inflation; the box "The channel ...
      moves very little" is overturned (the index is five parts, the shelf
      answers money); the spread at 0.7.43 is 8.61 points (§6); the dials table
      needs the anchor's (KMIN .25, KMAX .95, KSEED .80, TOLERANCE 1 point, LOSS
      24, GAIN 60) and the shelf's dials. §16: five components on trailing-year
      weights, luxury capped at 15%, chained every 120 months, "1.6× at total
      shortage" gone; rent log-sticky with the expected drift; the currency's
      anchored drift, UIP reversion .15, the real gap ex ante and five forces;
      the INFLATION tile's line is the anchor.
    - **§21 to §25:** §21 the fare, the dial at founding prices, charged at the
      struck level and shown in today's money; §22 NEEDS YOU's PRICES, Money's
      anchor and drift, Promises › Food, the shelf and margins, WHO GOES SHORT,
      History's new series and basket marks; §23 the year book's columns; §24
      ExpectationsCheck, GroceryCheck, SupplierCreditCheck; §25 "The index's
      base period", "a floor doing a price's job" and "priced by coverage, not
      by money" are answered by 0.7.42 and 0.7.43.
  - ~~**The manual's five false sections at 0.7.41** (`runs/ui21-docs-pass.md`,
    "Manual sections that are now false"; `docs/manual.md` was not edited):~~
    **all rewritten in version 12 (2026-10-05); kept as the record:**
  1. §13 The city's borrowing, "Issuance costs…": "When the treasury cannot pay
     for an order, the Build screen offers two things…". Since 0.7.40 any Build
     press that is more than the cash (a card, the order bar, Enter, Build all
     three or a suggestion) opens one page for the whole run, Build › Funding,
     in the land office's shape: the run's price against the cash, what it is
     short by, the same two offers as cards side by side, each sized to the
     run's gap (its invoice, each order priced on the yard the ones before it
     leave, less the cash) and each button saying the run. A run that would stop
     at an order short of ground, ore or licences borrows only for the orders
     before it and says so; an order not placed stays on its card.
  2. §22 The page stays put: "a Needs you line whose fix is already on site …
     falls from red to amber". No longer true of the served rows (power, water,
     road, care, schools): since 0.7.41 they keep the verdict's colour while
     their sites are on the way; sites change only the listing and the order.
  3. §22 The summary is a problem list: "yellow near the line and red past it",
     the same red-to-amber sentence, and "They read load — demand over supply —
     now, against the 75%…". Since 0.7.41 the served rows read supply over
     demand, unclamped ("233% served", "62% served · 56% flow"); they are still
     listed on the load or cover lines as before (power and water from 75% of
     capacity) but coloured by one verdict: green only at 100% or more and off
     the list, red at or under the red line, amber between ("short" under 100%,
     "tight" from it); the drawer's RESOURCES line counts "1 short · 1 tight"
     and can be red.
  4. §22 The inbox: "Since 0.7.5 … Enter … stops at the first refusal with the
     rest still pending". Add that since 0.7.40 Enter places the page's orders
     as one run, funded whole through Build › Funding when it costs more than
     the cash; it still stops at the first refusal; every order not placed stays
     on its card, the refused one included (it used to lose its count).
  5. §21 The instrument panel, "Roads: … the free-flow curve as a banded meter"
     (already stale at 0.7.29). Since 0.7.41 the Roads page shows SERVED, not
     FULL, and a curve of the flow against what the road serves (0–200%, red,
     amber and green from the left, better to the right).
  Missing, not false: §23 Time runs on its own (the redraw is held while a mouse
  button is down and drawn after the release, 0.7.40); §22 City History's chart
  (the charts follow the window and are cut at their edge); the borrow page's
  typed, scaled ask and its presets; the masthead (still Build 0.7.23, save
  format 29, 233 files, 66 harnesses).
- ~~**FIXES FROM PLAYING 0.7.39**~~ — **done 2026-10-04 as 0.7.40 and 0.7.41,
  tag 1004a** (49 files, verified byte for byte). Jerus's finds and the gauges'
  one rule, built as he chose; not yet seen on the PC (CHECK, below); see
  `fixes-from-playing-0-7-39.md`.
- ~~**INFLATION THAT ANSWERS TO MONEY**~~ — **done 2026-10-04/05 as 0.7.42 to
  0.7.45, tag 1005a** (179 files: 50 staged back from the PC identical byte for
  byte, the other 34 sources matching by size). Jerus's question why 100% QE, a
  20% target and 0% rates gave almost no inflation, answered with a Phillips
  curve mixed with clearing on money and built in four versions by fresh Opus
  agents; the five-policy ensemble went from 8/15 at 0.7.41 to 22/25 at 0.7.44,
  with A2, S1 and H1 left failing for his word. Not yet seen on the PC (CHECK,
  below); its ★ decisions under CONFIRM, its finds under FOUND ON THE WAY; see
  `inflation-that-answers-to-money.md`.
- ~~**THE MODEL FIXES, THE FUND'S WITHDRAWAL AND TRANSIT**~~ — **done 2026-10-05
  as 0.7.46 to 0.7.49, tag 1005b** (170 files: CLAUDE.md, 74 sources and 95 documents, among them the manual, the map and the notes; the 75 code files staged back and matched byte for byte). Jerus's
  "go for the model fixes, manual up to date and also, the city fund ... even 0
  or 10% a month", then "for transit, fix the leak": a read-only triage of the
  open model bugs against 0.7.45 (`runs/spec-model-fixes.md`), then four
  versions by fresh Opus agents: the load path and saved flows (0.7.46),
  government, labour and accounts (0.7.47), the fund's withdrawal dial from 0 to
  10% a month (0.7.48) and transit, the bill paid and households choosing by
  what their own commute costs (0.7.49, after a design study,
  `runs/spec-transit.md`); and the manual to 0.7.49 as version 12. The ensemble
  went from 22/25 at 0.7.44 to 20/25 at 0.7.49 (K2 and R3 new fails; A2, S1 and
  H1 still fail). Not yet seen on the PC (CHECK, below); its ★ decisions under
  CONFIRM, its finds under FOUND ON THE WAY; see
  `model-fixes-fund-and-transit.md`.
- ~~**THE CHART CRASH AND THE BUILD ADVICE**~~ — **done 2026-10-06 as 0.7.50 and
  0.7.51, tag 1006a** (53 files: CLAUDE.md, 20 sources and 32 documents, among
  them the manual, the map and dials, harnesses and month-order; the 21 code
  files staged back and matched byte for byte, the documents' sizes matched).
  Jerus, after 0.7.49: "hmm ok pretty good, but on one test run, the game got
  stuck"; then, going to sleep, "the building ideas is flawed, it doesnt take
  into account land price, and it doesnt build any slack" and universities
  "overstated and way too early". 0.7.50: the chart that drew from a history
  list that grew under it, now one snapshot, with every chart handler guarded
  and the log keeping failures past its cap (`runs/fixE-notes.md`); 0.7.51:
  Build's advice priced with its land, sized to the businesses' projection with
  5% to spare, higher education only for students the city would get and hire,
  and "Build all three" reading the run, after a design study
  (`runs/spec-build-advice.md`, `runs/fixF-notes.md`); and the manual to version
  13. The traces are byte-identical to `pt0749` and the ensemble was not run.
  Not yet seen on the PC (CHECK, below); its ★ decisions under CONFIRM, its
  finds under FOUND ON THE WAY; see `chart-crash-and-build-advice.md`.
- **THE RESEARCH SPECS' BUGS — FIXED AS EACH SCREEN IS REDONE** (found
  2026-10-01 by the read-only specs for the Land office, People, Services,
  Infrastructure, Sectors, Government, Finances, the Bank, Trade, Policy and
  City History, on the playtest's 2,400- and 600-month cities; nothing
  changed). Each went with its screen's batch, and since 0.7.37 every screen's
  batch has run; what each left is below, and the model bugs the specs found
  are the next item:
  - **The railway bills almost nothing in the first month after every load**
    (`runs/spec-infra-0729.md` §9 B1, §10 D2): a model bug, under MODEL BUGS
    FOUND BY THE SCREEN RESEARCH, below. The spec's other findings went with
    the Infrastructure screen (0.7.29, below).
  - ~~**Land office** (`runs/spec-land-0726.md` §8)~~ — **done in 0.7.26**
    (`the-land-office-redrawn.md` §4): "Who is waiting" from the blocked
    sectors; the red "% used" off the office, Build's LAND FREE and the left
    panel; the receipt in the screens' money; the funding pages on the rail
    and back to the office when nothing needs funding; the floor's wording;
    the going rate in the model; the stale tile sizes; `Palette.ORE`; vault
    mode's mixed verdicts; per-sq-ft prices through `Money`; "last month"
    from `NationalAccounts`; SectorScreen's "land tab". §8.1 (BEST VALUE not
    `bestValue()`) was decided as ★1: kept on card 1, the class comment made
    true, the tags independent. **Left:** §8.14's first half, Build's "Not
    enough land" page quoting "roughly" a block the office no longer sells,
    with ★12 (CONFIRM, below).
  - ~~**People** (`runs/spec-people-0727.md` §8, nineteen)~~ — **all done in
    0.7.27** (`people-at-a-glance.md` §4), the eighteenth on the Services side
    only: NEEDS YOU's CARE rows and Build's rings still read the beds, not the
    priced out (D16, CONFIRM below):
    - ~~Household Cash Flow's "Every kind of household covers its month" over
      red rows (only the family cells are counted);~~
    - ~~GOING SHORT is mostly empty shelves and the page never says so, and
      `HouseholdBalance.lastDelivered` is not restored on load (reads 1.0
      until the first month);~~
    - ~~the rows below the rule measured against `want()`, not a basket;~~
    - ~~Pensions' "They can afford to eat" under −$3,004 (`PolicyScreen`
      colours by one test and words by another; its three lines do not
      foot);~~
    - ~~fractional households ("4,910.54": `Pieces.cell()` unrounded) and
      fractional people ("0.02 at 1.01x");~~
    - ~~the pay row at the founding wages, not the live ones;~~
    - ~~the city's month does not foot, by the bank's account fees
      ($667,264), and the opened panel files them as "interest" and the fares
      under "Healthcare and school fees";~~
    - ~~after a load, Why people come is wrong (`Migration` saves no
      last-month figures);~~
    - ~~"Kept from last month" above the total (50,206 against 34,667);~~
    - ~~the omissions list half stale; PER WORKER's caption wrong; three
      "carry" ratios on two pages;~~
    - ~~verdict colours as categories (the working-age pyramid bars, the Born
      line);~~
    - ~~the dashboard's HUNGRY reads the sickness points, the page it opens
      the share of people;~~
    - ~~figures shown twice on the page; the retired in the unskilled column
      of "all households";~~
    - ~~Services' care coverage from beds, not `Healthcare.getCoverage()`~~
      (THINNEST COVER only; see above);
    - ~~Household Cash Flow reachable only from a bare button under the
      scroller.~~
  - ~~**Services** (`runs/spec-services-0728.md` §8, nineteen)~~ — **done in
    0.7.28** (`services-at-a-glance.md` §4), all that are the screen's.
    **Left:** bug 6, History's "Graduates" line 0 by construction (MODEL BUGS,
    item 7, below), and the second half of bug 5, `Education.everGraduated`
    counting only the months a band grew (a model record, now in the ladder's
    details under an honest label):
    - ~~power in kW labelled W, and "units a month" elsewhere for a rate;~~
    - ~~"GROUND LEFT plenty" on full ground (`monthsOfPlotsLeft` is
      `Double.MAX_VALUE` when nobody is buried);~~
    - ~~Utilities › Roads' "Spare" can never read "Over by" ("Spare 0" in
      green at 163%), struck on the raw load beside an effective "In use";~~
    - ~~Education's Books list the tuition the city covers as a cost the gross
      leaves out, and the alert says it is counted twice;~~
    - ~~"New diplomas this month" is a net band movement (−18)~~ (the leavers'
      diplomas gross since 0.7.28, `getNewDiplomas()`); the ever-taught count
      still undercounts, and History's "Graduates" line is 0 by construction
      (above);
    - ~~coverage from beds, not what the model applied; Senior care's "People
      to serve" is places; the death-chance note leaves out the elders (8%);
      the Senior care page shows only the senior band's deaths;~~
    - ~~"Four fifths of the water leaves unpaid for", a literal (measured 59%
      to 74%); "Resident draw" is not residents, and "Households have no cash
      account" is stale;~~
    - ~~the utilities called "privately owned, city-regulated" while their net
      income goes to the city's cash;~~
    - ~~Health's Books read 0 for "seen" and "raised" right after a load;~~
    - ~~verdict colours that disagree with NEEDS YOU for the same figure;~~
    - ~~the OFF SICK door opens whatever page was last open; the power page's
      two shortfalls unlabelled; "Build water →" opens Utilities on its worst
      measure; Canada's 127 prisoners per 100k a literal.~~
  - ~~**Infrastructure** (`runs/spec-infra-0729.md` §9, fourteen)~~ — **done
    in 0.7.29** (`the-road-in-one-picture.md` §4), all but three:
    - ~~"Room before it slows" and the spare capacity on the raw load (B3;
      fixed in the model's getters);~~
    - ~~`Rail.rAllowed` not saved, "$0.00" allowed just after a load (B2);~~
    - ~~Services' Utilities › Roads lines (B4; retired with the row);~~
    - ~~Freight's "paid abroad" that was the saving (B5); a commuter's 1.00×
      (B6); the drawer's three decimals and changing meaning (B7);~~
    - ~~five verdict scales for one road, the meter's bands missing NEEDS
      YOU's 85% (B8, B12); verdict colours as categories (B10);~~
    - ~~Build's transit ring "carries 62.5k" when 41.4k ride (B9); the lorry
      grid's row of noughts (B11);~~
    - **left:** B1, the railway's after-load bill (MODEL BUGS, item 1); B13,
      rail's tightness flipping across a load in a city with no track (B1's
      zero month; nothing shows it); B14, `Pieces.TILE_HEIGHT` with no reader
      (section 6, 0.7.25's flags).
  - ~~**Sectors** (`runs/spec-sectors-0730.md` §8, fourteen)~~ — **done in
    0.7.30** (`the-sectors-as-flows.md` §4), all that are the screen's:
    - ~~the balance sheet missing held abroad and other businesses' bonds
      (B1); Cash & debt missing the stolen and the shares bought back (B2);~~
    - ~~the operating rate's note "whichever is thinnest", of five (B3);
      "Staffed 100%" and "Running at 43%" in red with nothing standing (B4);~~
    - ~~"Sold" and "Could not build" in green (B7); OWES quoting a rate on no
      debt (B8); the scrapping alert with nothing to scrap (B9);~~
    - ~~verdict colours as categories (B10); a negative zero (B11); two month
      flows reading 0 after a load (B14);~~
    - **left:** B6 and B12, the sectors' own figures, and B13's Real Estate
      comma (MODEL BUGS, items 8–10); B13's other two, Agriculture's repeated
      "Brought in a month" and Heavy Industry's green "Exported 0 tonnes", in
      the old page's fold, untouched; B5, the vans block on every default
      page, which is not among the fixes the implementer lists.
  - ~~**Government** (`runs/spec-government-0731.md` §9, seventeen)~~ — **done
    in 0.7.31** (`earned-surplus-banked.md` §4), all that are the screen's:
    - ~~the GDP history empty after every load (B1; fixed on the load path,
      MODEL BUGS item 3);~~
    - ~~Repairs in the spending ring and list but not in the total, the key
      adding to 111.0% (B2); the net of care and schools printed with its sign
      inverted (B3);~~
    - ~~verdict colours as series and category colours, the amber "Paid out"
      bar among them (B4); "Steel exported" and "Scrap imported" (B5's
      labels);~~
    - ~~"of the change" at 1,038% and −1,545% (B6); `getIncome()` writing four
      of the month's fields each time the header drew (B8); the header's (i)
      under-listing what EARNED leaves out (B9);~~
    - ~~the pension alert every month (B11); "the city's own staff" that was
      care and schools (B12); `donutKey`'s unused parameter (B13); the revenue
      ring's centre counting a line its arcs dropped (B14); hyphen and minus
      mixed (B15); care fees with no care bill (B16, traced: the founding
      endowment's);~~
    - **left:** B7, EARNED moving with the dials between presses, named as the
      bridge's step "Today's dials, not the month's" rather than changed; B10
      and B5's G (MODEL BUGS, items 11 and 12); B17, THE DEBT's coupon chip for
      a month after a load (section 3, GOVERNMENT).
  - ~~**Finances** (`runs/spec-finances-0732.md` §8, B1–B19)~~ — **all done in
    0.7.32** (`the-debt-at-a-glance.md`): B15, GDP after a load, checked gone
    since 0.7.31; B17, Government's "maturity strip" sentence, fixed there;
    B13's Finances half there and its Policy half in 0.7.36. Three more found
    on the way and fixed: a dollar piece valued at the city's short rate where
    a buyback pays the world's curve; the net position taking an overdraft off
    twice; the default scar called points of the city's rate. B20's oddities
    print nothing false.
  - ~~**Bank** (`runs/spec-bank-0733.md` §8, seventeen)~~ — **done in 0.7.33**
    (`the-bank-at-a-glance.md`): B1–B15 and B17 fixed, B5 on the load path
    (MODEL BUGS item 5). **Left:** B16, a hyphen for a minus inside the
    verbatim folds and on History's money charts (`Money.tightMoney()`, every
    screen's: section 3, THE BANK).
  - ~~**Trade** (`runs/spec-trade-0734.md` §8, twenty)~~ — **done in 0.7.35**
    (`trade-at-a-glance.md`), eighteen fixed. **Left, shown honestly:** B1, the
    month's flows after a load, which read "not counted yet" (saving them is
    the spec's D4, MODEL BUGS item 2); B14, freight after a load (MODEL BUGS
    item 1).
  - ~~**Policy** (`runs/spec-policy-0735.md` §8, 21)~~ — **done in 0.7.36**
    (`policy-at-a-glance.md`), twenty fixed, B3 checked gone. **Left:** B8,
    `isPinned` against the founding floor (the spec's D7, MODEL BUGS item 4).
  - ~~**City History** (`runs/spec-history-0736.md` §8, B1–B12)~~ — **done in
    0.7.37** (`city-history-finished.md`), all addressed; B10 was already done
    in 0.7.31. **Left:** B5, the epidemic of 2,213 months, changed only in the
    screen's order and words (MODEL BUGS item 18).
- **MODEL BUGS FOUND BY THE SCREEN RESEARCH — all closed 2026-10-05, as 0.7.46
  to 0.7.49** (found 2026-10-01 and 2026-10-02 by the read-only screen specs and
  by 0.7.27's to 0.7.39's implementers and docs passes, on the playtest's 2,400-
  and 600-month cities). Items 3 and 5 were fixed on the load path in 0.7.31 and
  0.7.33. On 2026-10-05 Jerus said "go for the model fixes", and a read-only
  triage against 0.7.45 (`runs/spec-model-fixes.md`) built the other eighteen in
  four versions, each struck below with its version, and dropped items 16 and 22
  with proof; see `model-fixes-fund-and-transit.md`. The items read as they were
  found:
  1. ~~**The railway bills almost nothing in the first month after every
     load** (`runs/spec-infra-0729.md` §9 B1, §10 D2). `Rail.haul()` reads
     this month's exports and imports, month flows the save does not carry,
     so right after a load they total 0 t. At month 2,401 of the probe city:
     trade tonnes 154,577 → 2,695, billed $21.3M → $0.6M, the quote 0.337 →
     0.503 (still 0.433 two months on), goods' bands widened for a month, road
     use +1,519 trips. Every Continue does this; it is the cause of 0.7.25's
     "Rail's trade tonnes jump across a load" (`runs/ui6-notes.md` §8.3). The
     spec's fix: save last month's per-stream tonnes and truck bill in
     `Rail`, bill at them the first month after a load and do not reprice
     (0.7.25's D11 pattern). Not the Infrastructure screen's batch.
     **On screen since 0.7.29** (`runs/ui10-notes.md` §8.3,
     `the-road-in-one-picture.md` §7): the railway page shows it a month after
     any load. In the 2,400-month city 2,695 t crossed, the quote went 43% →
     57%, net income −$4.7M and "bigger than its city" fired, and goods lost
     their rail share for the month. Its twin, the spec's B13 (rail's
     tightness flipping across a load in a city with no track), shows nowhere.
     **On Trade since 0.7.35 and on City History since 0.7.37** (the Trade
     spec's B14, shown, not fixed): right after Continue every good's freight
     factor is 0, so What we trade's popover and price grid and History's
     PRICES THIS MONTH say their prices may step a month on.~~ — **done in
     0.7.46** (A1, with its twins B13 and Trade's B14): `Sector` carries the
     month's units shipped and landed across a save (`carriedExports`,
     `carriedImports`, saved as `SectorState.exported` and `imported`), so the
     railway bills a reloaded city's first month as the live one: city2400
     reload+1 reads 157,717 t, D$27.74M billed and a quote of .4017 (0.7.45:
     2,700 t, D$0.77M, .568); the traces byte-identical.
  2. ~~**Trade's month flows are not saved** (`runs/spec-trade-0734.md` B1).
     `ForeignAccounts.toSaveArray` holds trailing figures, not the month's
     exports, imports, foreign interest, financial flows, valuation, or what
     the vault bought and sold. Just loaded, both probe cities read SOLD
     ABROAD $0, BOUGHT ABROAD $0 and THE MONTH +$0 in green; the river says
     nothing crossed the city's edge while the sectors' saved statements hold
     D$393.3M of exports; and the forces page's previews read a deficit
     city's vault as selling nothing. Nothing in the month reads them before
     `takeMonth()`, so saving them moves no trace; the spec saves them with
     no SAVE_FORMAT bump (its D4).
     **Ready to build, awaits Jerus** (put to him on the evening of 2026-10-01;
     the spec's D4, not built in 0.7.35, `runs/ui16-notes.md` §4 decision 4):
     append the eight flows to `toSaveArray()` and read them by length in
     `restore()`; `isMonthCounted()` is then true after a load of a save that
     carried them, and nothing on the screen changes but that the "not counted
     yet" states stop appearing. Until then, since 0.7.35, SOLD ABROAD, BOUGHT
     ABROAD and THE MONTH read "not counted yet" after a load (or a founding),
     never $0, and What we trade shows the month the city was saved in from the
     businesses' saved books.~~ — **done in 0.7.46** (A2):
     `ForeignAccounts.toSaveArray()` slots 37–44 carry the month's exports,
     imports, foreign interest, financial flows, valuation and the vault's
     trades; `restore()` counts the month when they are there, and an older save
     still reads "not counted yet". No `SAVE_FORMAT` bump.
  3. ~~**Government's GDP history is not restored on load**
     (`runs/spec-government-0731.md` B1). `NationalAccounts` deliberately
     does not restore it, though `HistorySave` keeps every month's GDP. Just
     loaded, a 2,400-month city has "not a year of output recorded yet", and
     for the next 11 months every "of GDP" is scaled up from the months since
     the load ($5.4B against the true $5.7B; 36.39% against 34.4%). It is the
     cause of the walkthrough's §2.1, the GDP tile after a load (Housekeeping,
     "Not persisted across save/load").~~ — **done in 0.7.31** (the spec's
     D5, in the Government batch): `NationalAccounts.seedHistory()` puts the
     last 120 months back from `HistorySave.getGdp()` on the load path, so
     right after Continue every "of GDP", the Output page and the header's
     GDP tile read a year; SaveFileCheck holds it, and the eight traces
     stayed byte-identical (`earned-surplus-banked.md` §4, §5).
  4. ~~**Policy's `LabourMarket.isPinned` compares the wage with the floor in
     founding money** (`runs/spec-policy-0735.md` §8 B8, its ★D7). It tests
     against $3,460 while the wage is clamped at `cashMinimumWage()`, $3,827,
     so in the 2,400 city the Diploma band, 21,182 spare and paid exactly
     $3,827, is "not pinned", and the Policy flag, NEEDS YOU's WAGES and
     Migration's surplus departures all skip it. The fix (`cashMinimumWage()`
     in `isPinned`, with a LabourCheck section) moves migration: **the
     playtest's labour and pop traces will change**. The spec puts it in a
     small batch before the Policy screen's.
     **Ready to build, awaits Jerus** (put to him on the evening of 2026-10-01;
     0.7.36's ★D7, its D18 step 1 not built, `runs/ui17-notes.md` §4 decision
     1): `cashMinimumWage()` in `isPinned`, and a LabourCheck section "a band
     held at the indexed floor with people spare is pinned at a cost of living
     of 1.1"; the labour and pop traces will move. **On screen since 0.7.36:**
     the Wages page reads `isPinned` as it is and adds no verdict of its own,
     so in the 2,400-month city Diploma reads "has room · 21,161 spare" while
     it is paid exactly the floor ($3,827).~~ — **done in 0.7.47** (B8):
     `LabourMarket.isPinned()` reads `max(baseWage × MIN_MULTIPLE,
     cashMinimumWage())`, and Migration, CityNeeds and PolicyScreen follow. **It
     moved the default playtest a lot:** the first difference is m32; population
     at m1000 21,845 → 15,740 and at m4000 198,194 → 191,012; departures over
     the run 65,805 → 1,090,944; re-baselined as `pt0747` (CONFIRM, first).
  5. ~~**The Bank's sector rates miss their surcharge after a load**
     (`runs/spec-bank-0733.md` B5, its ★D10). Just loaded, every sector's rate
     lacks its record and its concentration charge: Mining in the 2,400 city
     reads 3.07% just loaded and 6.38% a month on, with no change in the
     sector. The charges are pushed only in the month, and the load path
     apparently prices credit before the record is restored (the spec says to
     verify the call order). The playtest's reloads match, so the month
     re-prices before any loan is written. The spec fixes it on the load path
     only if the eight traces stay byte-identical.
     **Traced by 0.7.30's implementer** (`runs/ui11-notes.md` §8, found 1;
     `the-sectors-as-flows.md` §7): the bank quotes a sector's rate without
     its record of defaults right after a load. `rebuildSimulationState()`
     prices every sector's rate before `restoreCreditRecord()` puts the
     restructure counts back, and the concentration charges are not set until
     the month: in the 2,400-month city Manufacturing (record 1.00 point)
     reads 2.92%, the prime, after Continue where its books struck 3.60%, and
     Mining 3.07% against 6.37%. Harmless to the simulation (repriced at the
     next month's top before any loan is priced); since 0.7.30 the Sectors
     screen's Cash & debt shows both rates until a month runs. A load-order
     fix, Jerus's.~~ — **done in 0.7.33** (the Bank spec's D10,
     `the-bank-at-a-glance.md`): two lines at the end of Game's load path push
     the concentration charges and re-price (`updateRates()`), so a city just
     loaded quotes each sector with its record and the book's concentration;
     Mining in the 2,400-month city reads 6.38% after Continue (it read 3.07%).
     The eight traces stayed byte-identical, and BankCheck §13b's three B5
     labels fail without the two lines. The Sectors screen's Cash & debt rate
     bar reads the parts the rate was struck from (`quoteParts()`) since
     0.7.38; the sector (i)s beside it still read the quarter as it stands
     (section 3, THE BANK).
  6. ~~**A reloaded city drifts a little in its first month**
     (`runs/ui8-notes.md` §9, "Found on the way" 1; older than 0.7.27). The
     2,400-month city, saved after a month and played one more month both live
     and from the reloaded save: 112,810 against 112,812 people, $1,072 of cash,
     arrivals 156.40 against 157.76. After a load the cells' month ledger reads
     differently, so the first month plans differently (Senior alone afterFixed
     $194 against $181; EI run out drew $0 against $422; dividends $0 against
     $49.4M; cars bought 0 against 222). The playtest's round trip does not
     compare that far.~~ — **mostly done in 0.7.46** (A1, with A2–A5): with the
     trade carried and each cell's income after fixed bills saved, the series
     that differ a month after a reload fall from 62 of 226 to 12 of 229 (by at
     most 8.3e-5; city600 0 of 223). The ★2 residual is not chased (sector net
     incomes ±D$0.4k, `bankDeposits` +D$64.6k, cash 5.6e-9).
  7. ~~**History's "Graduates" line is zero by construction**
     (`runs/spec-services-0728.md` §8 bug 6; `runs/ui9-notes.md` §8.2;
     0.7.28's D9). `HistorySave` records the sum of the net band movements,
     and every +1 has its −1: in the fixture city (the 600-month city with
     schools) it read 0.000 every month while the licences climbed to 0.59 a
     month. If it should be the gross diplomas, `Education.getNewDiplomas()`
     (0.7.28, not saved) is that figure. Not fixed in 0.7.28; Jerus's call.~~ —
     **done in 0.7.46** (A6): History records the gross gains,
     `Education.gainedThisMonth()`, the sum `everGraduated` accrues; months
     before stay 0.
  8. ~~**Automotive's page mixes nameplate with the month**
     (`runs/spec-sectors-0730.md` §8 B6). "Cars 21,780 a month" is nameplate
     while 9,178 were made; "Fabricated steel 76,230 tonnes wanted, 32,018
     bought here" is amber, but "wanted" is `getInputAtCapacity()`, and the
     month's bid was 32,018, all of it bought. 0.7.30's flow shows both
     figures; the sector's own lines need a model look.~~ — **done in 0.7.47**
     (B3): `Automotive.ownLines()` reads the month: "made N of its CAP a month ·
     N sold here · N shipped" (made is produced plus export-bound: city2400
     9,211 of 21,780) and the parts "N ordered at this month's rate · N bought
     here · N imported", a WARN only when bought plus imported fall under .9 of
     the bid; the note says what the plants would order at full rate.
  9. ~~**Construction's materials disagree with its statement**
     (`runs/spec-sectors-0730.md` §8 B12; confirmed by `runs/ui11-notes.md`
     §8, found 2). Building materials are $6.2M on the statement, all
     imported, and 0 units on its production row: `Sector.bank()` zeroes the
     `Input` counters, and the builders buy as the sites draw. 0.7.30's flow
     says "bought as it was drawn: no units on its row this month". Which
     month each figure is about needs a model look.~~ — **done in 0.7.46** (A5):
     `Sector.beforeBank()` and `Construction.beforeBank()` keep the struck
     month's materials (bought from the plant, imported), saved as extras once
     known, beside a muted "Since then, so far". The triage's "about 476 units"
     for D$6.20M was really 539 units at about D$11.5.
  10. ~~**Real Estate's word has no thousands comma**
      (`runs/spec-sectors-0730.md` §8 B13; 0.7.30's decision 22): "housing
      ahead of jobs (64210 now, 0 coming)". The word is written into a
      playtest trace (`t-house.csv`), so fixing it changes that trace; left
      for Jerus to schedule.~~ — **done in 0.7.47** (B4): `%,d now, %,d coming`;
      in t-house only the why column's digit grouping moved ("116322" →
      "116;322").
  11. ~~**The transit bill nobody seems to pay** (`runs/spec-government-0731.md`
      §9 B10, its D17; `runs/ui12-notes.md` §8, found 1;
      `runs/ui12-docs-pass.md` flag 1; `earned-surplus-banked.md` §7).
      `EconomyManager.setTransit()` keeps the INFRASTRUCTURE category's payroll
      and upkeep; it is read by `NationalAccounts.setTransitLines()`, which
      `getTotalExpenses()` does not sum, by `getTransitNet()` and by
      Infrastructure's pages, and by nothing that moves cash. `getExpenses()`
      leaves it out; the spec's probe found the treasury bridge closing to $0
      with no transit row, so the bill (the Infrastructure spec's $23.9M in the
      2,400-month city) never left the treasury, and MoneyAudit has payroll
      debits for utilities, care, schools and safety and none for transit.
      **Verify before calling it a leak.** If the bill is real,
      Infrastructure's "net cost" is a cost the treasury does not pay. Section
      4's TWO BUDGET LINES THE BALANCE OMITS has carried it since 2026-09-19 as
      "Transit wages are paid by nobody".~~ — **done in 0.7.49** (D2, the
      triage's B9, built after Jerus's "for transit, fix the leak"): a real
      leak, D$23.96M a month in city2400 against D$4.12M of fares.
      `TreasuryLine.TRANSIT` (a promise, after the police), `getExpenses()`,
      `NationalAccounts`' totals, `MoneyAudit`'s "- transit Bill" and the bridge
      without its fares step now carry it; the test player counts transit's
      wages (D3, `linesThatPay()`).
  12. ~~**The schools and transit are missing from government output** (the
      Government spec's B5, its D13 and D17; `runs/ui12-notes.md` §8, found 2;
      `runs/ui12-docs-pass.md` flag 2). G is the utilities' payroll, care's
      gross cost and police and prisons (`Game`'s `updateNationalAccounts()`
      call). Since 0.7.31 the Output page's GOVERNMENT card's (i) says what is
      counted; the manual's §12 already calls Education's cost outside G an
      inconsistency, and section 2's "Education's cost is not in GDP" is the
      same question.~~ — **done in 0.7.49** (D2): G adds the schools' gross cost
      and transit's bill, and Government's (i) and GOVERNMENT_INFO say so.
  13. ~~**After a load, Healthcare fees' panel does not add up to its line for a
      month** (`runs/ui12-notes.md` §8, found 3; `runs/ui12-docs-pass.md` flag
      5). Display only. In the implementer's probe, just loaded and
      saved-and-loaded: the line $1.0M, the panel general $0, childcare $0,
      senior $0, burials $879k; a month on, $94k, $30k, $26k and $878k.
      `Healthcare.feesFrom(care)` is a flow the load path does not restore;
      burials survive because they are counted × the fee.~~ — **done in 0.7.46**
      (A3): `Healthcare.getState()` appends the three kinds' served counts
      (`STATE_BEFORE_SERVED` = 16; lengths 12, 13, 16 and 19 restore), so a
      reload's fees add up to their line.
  14. ~~**The busy month annualises** (the Government spec's D11;
      `runs/ui12-notes.md` §8, found 6). Every "of GDP" on Government is this
      month × 12 against the year (true since B1), so a lumpy month annualises
      one-off buildings: in the 600-month city's busy month spending read
      129.81% of annual GDP. The spec's later fix reads the trailing year from
      History (`YearBook.nominalYear(h, key)`, `rollingYear()` on a saved
      flow).~~ — **done in 0.7.47** (B7): every "of GDP" on Government reads the
      trailing twelve months of its line where History records it
      (`TRAILING_MONTHS` = 12; city2400 TAX TAKE 36.4%, the month × 12 said
      34.5%); Healthcare and Education keep the month × 12, since History's
      bills are net and the rows gross.
  15. ~~**The bank's capital ratio is stored clamped at 1,000%, and its return
      on equity reaches −105%** (`runs/ui18-notes.md` §8 items 2 and 3, and its
      docs pass's flag 5; `runs/ui14-notes.md` §8 item 5 and §4 items 7 and 8).
      `HistorySave` records `bankCapitalRatio` clamped at ten ("and ten with
      nothing lent"), so a bank like the 2,400-month city's, risk-weighted at
      1,990.7% (a weighted book of $2.9M, the rest insured mortgages at no
      weight), draws a flat line at the ceiling: "1000.0%" on City History,
      which offers the series since 0.7.37 (D9). In the 600-month city's view
      its return on equity reaches −105.2%, and in both cities its provisions go
      negative (a release). Model figures, shown as they are. The Bank spec's
      D11 model half, History series `bankLeverageRatio` and
      `bankLeverageTarget` (no format bump), would make the capital chart useful
      and let the Overview's gauge grow on a month while the leverage ratio
      binds (0.7.33's ★7 and ★8). Section 4's FOUND BY 0.7.9'S IMPLEMENTER has
      the clamp's older half.~~ — **done in 0.7.46** (A7): History records
      `bankLeverageRatio` (clamped at 10) and `bankLeverageTarget`, and the
      capital card reads THE LEVERAGE RATIO while the leverage binds; the ×10
      clamp on `bankCapitalRatio` stays, and the ROE of 18.75% in city2400 is a
      true figure.
  16. ~~**Two inflation colours disagree** (`runs/ui17-notes.md` §8 item 1, ★).
      The Policy tab colours inflation amber more than a point off the target
      (the old screen's rule, kept by the spec's §3.5); the header strip calls
      it green within `STRIP_INFLATION_QUIET`, three points of the target. At
      the research city's 0.2% against 2% the header is green and Policy's tick
      and band are amber. One rule for both is Jerus's (the header's rule is
      his: "Red at target + 5 points"). Not in 0.7.38.~~ — **dropped with proof,
      2026-10-05** (the triage's §4): 0.7.45's D4 changed it. Policy's amber is
      now `Expectations.TOLERANCE` on the smoothed rate, "what trust is judged
      on", while the header keeps Jerus's 3 and 5 points; two questions, each
      labelled, no bug.
  17. ~~**A property offset's dial stops at ±10 points; the model clamps it at
      ±30** (`runs/ui17-docs-pass.md` flag 7). Every offset, property's too, is
      clamped at `MAX_OFFSET` (`TaxPolicy.clampOffset()`), while a property
      offset's ladder runs ±`MAX_PROPERTY_TAX`; since 0.7.36 its (i) says both.
      Whether the dial or the clamp is meant is Jerus's; older than 0.7.36
      (0.7.35's rows were ±10 too). Not in 0.7.38.~~ — **done in 0.7.47** (B5):
      `clampPropertyOffset()` holds a property offset at ±`MAX_PROPERTY_TAX`
      (±10), so the dial was right and the clamp wrong; an older save's offset
      past ±10 loads at ±10 at the same rates, and the (i) says wage, profit and
      sales offsets ±30, property ±10.
  18. ~~**An epidemic that never ends** (`runs/spec-history-0736.md` §8 B5;
      `runs/ui18-notes.md` §8 item 1). "Epidemic of 2015" runs 2,213 months in
      the 2,400-month city (413 in the 600), its worst "45% of the workforce
      off sick at Jun 2052": more than 10% of the workforce
      (`YearBook.EPIDEMIC_SICK`) has been off sick for the city's whole life
      since. Put to Jerus on the evening of 2026-10-01; awaits his word. Since
      0.7.37 City History lists it last, "chronic" (`YearBook.CHRONIC_MONTHS`,
      120), and never leads with it while anything else runs; the threshold and
      the sick rate are untouched, and HealthCheck is still the known red. Not
      in 0.7.38.~~ — **done in 0.7.46** (A8): epidemics are named on Health's
      outbreaks (`outbreak` recorded from `Health.getOutbreakSeverity()`;
      `YearBook.EPIDEMIC_OUTBREAK` = `Health.OUTBREAK_FLOOR`, `EPIDEMIC_SICK`
      retired); the 2,213-month epidemic of city2400 is gone, and months before
      0.7.46 name none.
  19. ~~**A share's price is its last trade, however old** (the fund study's
      B3, `runs/spec-fund-0739.md` §7; `runs/ui20-notes.md` §8). The
      2,400-month city's prices are 20–32 months old; Mining is marked at its
      March 2198 trade, D$1,644 a share, against a fair value of D$0.35, and
      the fund's D$351k line, `fundValue` and the 3% transfer rest on it.
      Re-marking moves the traces, so the study's D12 left it out of 0.7.39.
      **On screen since 0.7.39:** every page of the fund says "last traded
      <month>".~~ — **done in 0.7.48** (C4): `Exchange.cityMark(c)` marks the
      city's holdings at fair value once a share's last trade is
      `STALE_MARK_MONTHS` (12) old, and positions say "marked at fair value:
      last traded <month>" (city2400: Mining D$0.59 a share, not D$1,644).
  20. ~~**Consolidated share prices record as zero** (the fund study's B6;
      `runs/ui20-notes.md` §8). `HistorySave` rounds a price per founding
      share to four places, so Materials' and, after a reform, Mining's and
      Business Services' prices record as zero (Search: "1Y: not recorded
      precisely enough"), and a very old move reads huge ("+121,687,262.6%
      from Mar 2000 to Dec 2199" on Construction, a real 200-year move off a
      tiny founding price). Recording more places also changes the bank's
      series, which the warrants' value reads, so `fundValue` and the traces
      move: its own batch, with its trace check.~~ — **done in 0.7.48** (C5):
      `HistorySave.SHARE_PRICE_DIGITS` = 6 significant figures (`roundSig()`),
      so a consolidated company's price is a figure from 0.7.48's months on.
  21. ~~**A buy at fair value fills nothing, and the rule's bids idle** (the
      fund study's B2 and B10; `runs/ui20-notes.md` §8). Not one company book
      in either research city had an ask at or under fair at load (the desk
      asks at fair + 1%, and where nobody asks the best bid is the company's
      own buyback at fair + 10%), so a hand buy at fair filled nothing in both,
      and the 2,400-month city's rule, bidding at fair, had not filled in 13
      months, its cash idling (D$50.4M of D$375.3M). Since 0.7.39 the hand
      names its price (★D2, CONFIRM), and "Best bid" is the price that fills;
      the rule's design is Jerus's.~~ — **done in 0.7.48** (C3): the rule bids a
      share at the desk's ask, fair × (1 + `RULE_PREMIUM`), `RULE_PREMIUM` =
      `Exchange.SPREAD` / 2 (1%); bonds unchanged. C3–C5 move the traces from
      m1882; re-baselined as `pt0748`.
  22. ~~**The 600-month city's ten-year bonds rest at 75–78 per 100 against
      values of about 102** (#141–#148; the fund study's B11;
      `runs/ui20-notes.md` §8) until the fund's rule takes them, so its lots
      read "bought 24–26% below value" and +32–36% unrealized at once. A
      market observation, not chased.~~ — **dropped, 2026-10-05** (the triage's
      §4): a market observation, not a defect; the fund's rule already takes the
      bonds, its bond bids filling under value.
- **CONFIRM (from 0.7.53 to 0.7.54).** Jerus, 2026-10-06: he plans cities of 5
  to 10 billion and asked for confirmation that "an enormous city, utterly
  enormous, can still run with no issue"; after the study, before any work: "for
  now start doing the necessary stuff that can be done aka the pop limit fix and
  the size stuff". The study (`spec-scale.md`) said yes, after named fixes, and
  0.7.53 and 0.7.54 are the fixes that could be done without him. He has not yet
  answered what is below, nor given his go for the land, water, world, map and
  fuel batch (the NEXT item). Every call was the study's ★ (§9), an
  implementer's (`runs/fixH1-notes.md` §6, `runs/fixH2-notes.md` §5) or a
  docs-pass flag (`runs/docs-0754.md` §4), written down with its reason:
  - **The land premium at 5 to 10B** (the study's ★3, §8 step 2; fixH2 §6 item
    1), the most visible. It is linear in absolute size (`LandMarket.java`
    :152-155 and :328-329, 0.7.52's lines): at 10B land costs about 19,500 times
    today's price per sq ft. A ×10 copy of his city (5.1M people) saw rents
    ×3.9, prices ×2.3 and 18% of its people leave in 30 months; the same copy
    with the premium read at the original size tracked his real city. In the 5B
    copy's first month (fixH2 §3) land reads 2,490 a sq ft against 0.0013 and
    the property tax on business land comes to 3.2e13, which the sectors borrow;
    a scaled city builds nothing, pays about 3e13 a month in property tax and
    loses about 1% of its people a month. Read at the original size that copy's
    treasury moved 8.37e11 to 8.39e11, so the jump to 3.30e13 is this premium,
    not the scaler (the audit residual of 2.7e9 was the scaler's, and is fixed).
    Saturate it, or make it depend on density (about half a day once he
    decides); recommended: density, not absolute size. Bigger cities can have
    dearer land, but not that much dearer.
  - **History trimming** (the study §5 and §9): monthly forever, or monthly for
    recent decades and yearly before? History grows with months, not people (3.2
    KB a month in Jerus's city); 1,000 years would be about 37 MB on disk (the
    `.bak` copies double the disk), about 110 MB of heap and about 1 s per
    autosave, none of it depending on population. The study judged that
    acceptable; trimming is his call.
  - **Arrears paid do not reach a sector's cash-flow statement** (fixH2 §5,
    found and left: entangled). What the treasury pays down of a sector's
    arrears (`Game.payDownArrears`) reaches the sector's till but not its
    cash-flow statement, because `fromTheCity` is subsidies only; in the scaled
    copies Construction's statement missed 2.1e12. It holds at any size. Does
    the line belong on the statement? A later batch.
  - **`Formats.cash()` is compact only past 2^53 dollars** (fixH2 ★7). It hands
    sums at or past 2^53 dollars to `amount()` ("$26.0Q", "$1,317.0Q" grouped
    past a thousand) and is unchanged below, never a saturated long; the study
    had $10^12 as the switch. `ui/Money` gets the Q step too.
  - **The 1,500 ms bound** (fixH2 ★8). `ScaleCheck`'s median month must stay
    under 1,500 ms at 5B and 10B: about 170 times the 6 and 7 ms measured on
    this 2-core machine, and a sixth of 0.7.53's 8.8 s for the same city. Is
    that the right slack for his machine?
  - **The halving search's monotonicity proofs** (the study's ★2; fixH2 ★1 and
    ★3). `orderSize` and `Mortgage.decide` halve, with the proofs in their
    comments: the wait grows with the order, and the landlord's order is a run
    from one (cost convex through 0, so outright, the down payment and the
    lender's test each hold on a run from 1), with rounding margins. The trim is
    not proved (the rate falls with borrowing past leverage 1; bond fixed
    costs), so it asks the countdown's first 16 slices exactly, doubles down
    from the last failure and halves: exact for trims under 16, and otherwise
    relying on no pass, fail, pass below the counted slices. Evidence, 0
    decisions differing in every run: 1,320 months of the playtest's city and
    120 at a 15% dial, its month-400 copy at ×1000, a mortgage grid of 35,280
    orders, and `WatchProbe` on city2400 and Jerus's city at ×1, ×1000, 5B and
    10B. A plain halving had drifted 0.12% in population at ×10,000 in the
    study. Does he accept proofs, not shadows?
  - **The money tolerances** (fixH2 §2, ★6). `MoneyAudit.tolerance(scale)` is
    max(0.01, 1e-12 × scale), and `tolerance(floor, scale)` keeps a tighter
    floor exactly while 1e-12 × scale is at most 0.01 (scale up to 1e10 units).
    Only the lines `ScaleCheck` measured failing at 5B and 10B moved, plus the
    treasury journal's reconciliation, in BankCheck, CreditCheck,
    EducationCheck, HoldersCheck, MortgageCheck, HistoryCheck and TreasuryCheck
    (fixH2 §2 lists each, before and after). Never looser at today's sizes: a
    copy of `MoneyAudit` that prints whenever the relative part engages ran
    under all seven and printed 0 times in each (330 in `ScaleCheck`). Long
    cents were rejected (the study's ★6): they hold 9.2e13 units, less than a
    10B city's lifetime exports.
  - **H1's other ★ calls** (`runs/fixH1-notes.md` §6): a new `ScaleCheck`, not a
    section in an existing check (★1); the study's scaler ported except that it
    leaves the works yard alone, since the yard fills at its base a month
    whatever the size (★2); every sector held in H1's sections, so the order
    loops never ran there (★3); the month axis stays `Integer` while ten series
    became `Long` (★4); K targets 5e9, Jerus's smaller plan (★5); Retail's
    baskets, luxury coverage and the kitchens' seats and food held widened too
    (★6). Left as `int` (§4): building counts, order sizes, land blocks and
    months, the works yard and materials orders, construction output, the
    per-building template figures and the trade counters.
  - **H2's other ★ calls** (`runs/fixH2-notes.md` §5): the trim's countdown,
    gallop and halving, 16 slices chosen as cheap and above every trim seen at
    today's sizes, which is none (★1); `PRIME_SCAN_SLICES` 4096 and the
    four-slice rule past it (★2); the `OrderWatch` hook in the model and the
    countdowns in the harness (★4); a new `OrderSearchCheck`, not a `ScaleCheck`
    section (★5); `ScaleCheck`'s free fixture the playtest's city at month 400
    (★8); the scaler's names fixed (Construction's `recognisedThisMonth` had
    been left unscaled; an EXTENSIVE override list, and seven ratios no longer
    scaled) but the registers still unscaled (★9, FOUND below).
  - **The study's other ★ calls** (§9): ★1 the scaling method (extensive state
    scaled, per-household figures kept; the bank's statements, the equity and
    exchange registers, the bond market and the fund not scaled); ★4 `long` for
    counts, not `double` (built); ★5 no `SAVE_FORMAT` bump (kept at 30: an old
    build shows a save over 2^31 as unreadable rather than loading it wrong); ★6
    a relative money tolerance, not long cents (built); ★7 districts, not tiles,
    and ★8 the world in 64 km cells (the NEXT item).
  - **Not in the manual** (`runs/docs-0754.md` §4 flag 5): its open questions
    (fifty-nine) are unchanged; the land premium, described in `ScaleCheck`'s
    row, is an open question on this list only.
- **CONFIRM (from 0.7.52).** Jerus, 2026-10-06: "ok seems to be working fine"
  (0.7.50 to 0.7.51). On the ensemble's inflation fails he asked for "just a
  very simple fix, aka beside the target inflation, how strict, very strict then
  it trys to have it below the target, very loose and the target is a
  suggestion", and for the autosave bug to be fixed; K2 and R3 are therefore
  accepted, not chased. Every call below was the implementer's
  (`runs/fixG-notes.md` §5, seven ★) or a docs-pass flag (`runs/docs-0752.md`
  §4), written down with its reason:
  - **The ends' values** (★1). Very strict aims 1 point under the target, never
    below 0%, at weight 2.0; Very loose ignores 2 points either side of the
    target, at weight 1.25. One point under is the furthest below the target
    that still counts as on target for trust (`Expectations.TOLERANCE`), so
    hitting the aim costs no trust; 2.0 doubles 1.5's margin over one. Very
    loose's outer point (2 points) is a miss that trust counts and the bank does
    not answer; 1.25 halves the margin but stays above one, so the Taylor
    principle holds past the band and there is no spiral. The inner steps sit
    halfway: Strict aims 0.5 point under at weight 1.75, Loose ignores 1 point
    either side at 1.375. Standard (0, 1.5) is the old rule to the bit.
  - **At his 0.5% target, Strict and Very strict both aim at 0%** (★3). The aim
    is floored at `MIN_INFLATION_TARGET`, whose own sentence says no bank aims
    at falling prices, so there they differ only in weight; Loose and Very loose
    act only past 1.5% or 2.5% (or under −0.5% or −1.5%). Is that what he wants?
  - **Credibility is judged against the Standard rule** (★2). `Game` hands
    `Expectations` `neutralRate()` and `holdingRate()`, the Standard rule's
    advice, and not the dial's own advice. On the dial's own, a loose bank on
    the autopilot would "lean all the way" whenever inflation is past its band,
    so it would lose trust only inside its band and Loose (band = `TOLERANCE`)
    would never lose any. Measured against holding the target, a looser bank
    loses trust as far as it falls short, through the existing lean; a strict
    bank can lean no more than 1. Bit-identical at Standard. NEEDS YOU's PRICES
    row, the anchor card's "not leaning" words, `ANCHOR_INFO` and the header's
    INFLATION help read the same.
  - **Old saves are not repaired** (★6). An old mid-month autosave has already
    run month N's opening steps (`fundYearEnd`, `rollMaturities`,
    `closeDemolished`, and the bank, fund, register and bond market's
    `startMonth`), so rolling the counter back would run them twice (by reading,
    not run: `rollMaturities` would roll what falls due a second time, and
    `fundYearEnd` could pay in twice at a year's end), and a history row for N
    would invent a month that never ran. The one month's gap stays: the charts
    place points by month number and draw across it, and, by reading, the year
    book's "n" counts 11 months for that year. Jerus's own autosave (saved by
    0.7.49 as "Autosave - month 1851", history 2 to 1850) loads at 1851 with the
    gap; its last rollover was at 1693 and its fund dial is 0, so nothing was
    doubled at 1851.
  - **A 0.7.51 build drops the setting** (docs flag 2). It reads a 0.7.52 save
    at Standard and its next save drops the key: no format bump (`SAVE_FORMAT`
    stays 30), as for the other keyed dials, so the strictness is lost silently.
  - **The (i) says nothing of the downward side** (docs flag 1; fixG ★4). HOW
    STRICT's (i) says a strict bank answers "each point over" its aim, but one
    weight serves both sides of the aim, so a strict bank also cuts harder below
    it; Loose's band is symmetric, so in a deflation it holds until prices fall
    past the band. True as far as it goes; left.
  - **fixG's other ★ calls** (`runs/fixG-notes.md` §5): the halfway inner steps
    and the five names, Standard the middle one (★1); the probe city is seed 5
    (4,834 people at month 600), a calm city whose twins differ only in the rule
    from m600, playing without advice from the branch, the branch and the
    240-month horizon being the ensemble's own month and window (★5); "no
    spiral" means the dial never reaches `MAX_POLICY_RATE` and the last year
    ends within a full miss (`TOLERANCE` + `MISS_SCALE`) of the target, plus
    every weight above 1 (★7).
  - **The probe's figures** (`runs/fixG-notes.md` §2), inflation a year over 240
    months from m600 on seed 5: Very loose 3.290%, Loose 3.201%, Standard
    2.225%, Strict 2.153%, Very strict 2.036%. The ends on other seeds, Very
    strict / Standard / Very loose: seed 2 2.528 / 2.734 / 3.158; seed 3 2.057 /
    2.139 / 2.251; seed 4 2.468 / 2.795 / 3.450; seed 1 1.195 / 1.650 / 1.588,
    its Very loose 0.06 points under Standard; seed 0 1.765 / 1.572 / 1.287,
    inverted, a shrinking city with 22–28% out of work. From the founding (480
    months) the boom swamps the dial: Very strict's peaks at 56.7%, Standard's
    at 38.0%, Very loose's at 24.9%; all three come back down and none reaches
    the stop.
  - **Not in the manual** (`runs/docs-0752.md` §4): its open questions (59) are
    unchanged; the dial's ★ calls and the 0.5% target are on this list only.
- **CONFIRM (from 0.7.50 to 0.7.51).** Jerus made two requests, after 0.7.49 and
  before going to sleep (2026-10-05/06): "hmm ok pretty good, but on one test
  run, the game got stuck", with the terminal pasted, and "can you also check
  the build all three button, you see first of all, the building ideas is
  flawed, it doesnt take into account land price, and it doesnt build any slack,
  and universities and education other than elemn middl and high schools tend to
  be overstated and way too early sometimes, so fix that, and make it so that
  alot of its ideas also take into account the same as businesses do, aka a
  projection". He was not answering during the run, so every call below was the
  design study's (`runs/spec-build-advice.md` §6, 11 ★), an implementer's
  (`runs/fixE-notes.md` §6, `runs/fixF-notes.md` §6) or the orchestrator's,
  written down with its reason. **The most visible, for Jerus to confirm first
  (the build advice's ★ calls):**
  - **A 5% slack and a 100% target** (★3). `BuildAdvice.SLACK` is the
    businesses' `TARGET_HEADROOM` (.05), and a served gauge must reach 100% of
    demand × 1.05 at its projection, which turns care's 80% line and childcare's
    70% line into green (before, 40 clinics left city2400's care at 81%, back on
    the list at 80% the month they opened). The cost is bigger orders:
    city2400's clinics 40 to 55, his roads 37 Elevated Highways at k = 1 to 54
    (+17, the slack alone, his projection being flat), so more cards are on
    credit or short of land. On a big network 5% is 5% of all traffic.
  - **A 6-month horizon, the build wait capped at 12** (★4, ★5). `HORIZON` is
    `PLANNING_HORIZON` (6) and the opening's wait is capped at
    `MAX_ORDER_MONTHS` (12), so a card is sized at most 18 months out. Quoted
    city waits reach 47 months (his highways) and 122 (playtest m1,200); sizing
    to them would chase the builders' queue, and the firms refuse anything past
    12. Growth is the population trend only, through the firms' `growthFactor`;
    workers enter through posts on site; income is not projected (no income
    trend is kept). The projection is inert in a city above its homes (his: k =
    1) and strong in a young one (k 3.05 at playtest month 9, 12.4 at month 3,
    capped by the homes).
  - **The cash cap removed, credit stated on the card** (★7). One count per
    card, the one that keeps the need ahead; the cash only colours the button.
    Measured: the cap fired 0 times against credit 106 in 50 playtest looks and
    the three cities, and a capped card beside a credit card was ordered short
    and borrowed anyway. A card says "More than the treasury holds after the
    ones before, by $X: Build offers a loan" and the button "Build all three on
    credit / short $X" (his city with no cash: "short $40.4B"). To bring the cap
    back is the old BuildAdviceCheck §3b.
  - **Land at the land office's price, in the ranking and not in "all in"** (★1,
    ★2). Free ground is valued at the cheaper of the office's price and what a
    business pays, ground to buy at the office's, the city's marginal cost of
    ground; the office is below the inside price in all three cities (about ×2.7
    to ×3.7), so in practice it is one price. The card's "$X all in" stays what
    Build charges, the city paying nothing for its own ground; the land has a
    line of its own.
  - **Higher education only for students who would come and be hired, and a
    first school half full** (★6, ★11). `wanted` is the smaller of who would
    come and the posts for them (standing, on site, and those their holders
    retire from in a course); the first school needs half the smallest
    (`FIRST_SCHOOL_SHARE` .5, the firms' first-plant share); the feeder-cohort
    term never bound (+5.2% on his city's college). The playtest city sees its
    first college row at month 960 (54,218 people) and no university by month
    1,200; city2400's four red rows become one, city600's two go. A city that
    never gets graduate jobs is never advised a university (posts do arrive with
    migrants). No "ladder first" gate on colleges. Elementary, middle and high
    school keep their rule (★9).
  - **One building type per need** (★10): a bus network plus a road is not
    tried, to keep it within one batch. "Build all three" reads the run (★8).
  - **Large Home Daycare counts and their crowding of the builders** (the
    orchestrator's addition; `runs/fixF-notes.md` §7). City600's third card is
    133 Home Daycare ($20.7M, 105%), the cheapest per child with its land
    ($14.60k against the Centre's $26.13k); the count reaches 1,598 at playtest
    m1,200. The quoted wait is the model's own (`BuildingManager.waitFor()`),
    but the crew rule gives every small building its own crew: 133 daycares
    carry 1.61× the crew weight of the 5 Childcare Centres that seat the same
    children (1,759 against 1,093) and owe half the points, so they open in
    about half the time (city600 6.17 months against 12.76) and push other sites
    back: 3 Walk-in Clinics wait 4.18 months alone, 21.30 with the daycares on
    site, 14.82 with the centres (city2400 0.45, 2.32 against 1.61; his city
    2.18, 3.37 against 2.92). The ranking counts price per child, not this
    crowding. The card says how many and why, on every card with a count above
    one. Fine, or rank by something else?
  - **Two land figures on one card** (`runs/fixF-notes.md` §7). When an earlier
    card is short of land, a later card's line (3) counts its land after the
    cards before it ("798,000 sq ft more than is free"), while its own Build
    button (BuildCard's verdict, this order alone against all the free land)
    says "Short 543k". Both are true; which should the card say?
  - **The freeze tied to the chart fault, with moderate confidence, not proven**
    (E's ★; `runs/fixE-notes.md` §1). `Parent.java:1704-1705` walks the dirty
    children down from the end, so index -1 means a parent counts more dirty
    children than its 2-child list holds, the bookkeeping an aborted children
    change corrupts, and nothing in the code touches the graph off the FX
    thread; but the 23:11 damage healed (no exceptions from m399 to the cap), so
    00:03 would be a fresh recurrence past the cap, and the exact -1 sequence
    was not rebuilt. If it freezes again the log now keeps the first failure.
  - **`clearMenu`'s filter** (E's ★). A `tearingDown` flag and a filter on
    `rootMenu` that consumes MOUSE_EXITED_TARGET while the page clears, rather
    than stripping handlers or hover state node by node: one place, covering
    every handler on every page, present and future, whatever it would have
    done; the page is thrown away, so no exit needs delivering. The reused
    History charts drop their hover through the scene listener, and their stuck
    `hover` flag styles nothing.
  - **The log keeps every failure past its cap** (E's ★). Every
    `GameLog.failure()`, not only uncaught exceptions (a failed month or a
    corrupt save is the same kind of record), each once by its first four lines,
    up to `FAILURE_BYTES` 200,000 more, then the file says so and closes. The
    cap is counted per stream, so the file can near twice 2,000,000 (as at
    0.7.49).
  The other starred calls, for Jerus to confirm:
  - **The chart batch** (`runs/fixE-notes.md` §6): ★ the snapshot plus bounded
    indexes in the stack helpers (the bounds are cheap and stop a later caller
    bringing the crash back); ★ the full-screen button and the settle callback
    are left unguarded on purpose, a fault there being the screen's, and the
    crash handler logs it as before; ★ a frame that fails is cleared, not left
    half-drawn, and only `RuntimeException` is caught.
  - **The advice batch** (`runs/fixF-notes.md` §6 and §2): ★ a card's credit is
    its own shortfall (its quote less the cash the cards before leave) and the
    button's is `buildFundingGap(run)`, the loan the run will take; ★ "why so
    many" is on every card with a count above one, since a threshold would mean
    inventing a number; ★ a school's own posts do not create its demand; ★ the
    stop's ordinal names the card holding the building where the run stops, two
    cards for one building being merged in the run; ★ the Services chip wraps
    rather than abbreviating the spec's words ("1 post" when it rounds to one).
    Departures from the spec: `Suggestion` gains one field, `credit`;
    `BuildAdvice.run(advice)`; the tooltip's office price goes through
    `unitPrice()`, since `money()` rounds $1.32 to "$1"; `opening()` holds an
    infinite lead to 12; the runner-up is the true second in order.
- **CONFIRM (from 0.7.46 to 0.7.49).** Jerus made two calls: "ok sure go for the
  model fixes, manual up to date and also, the city fund, you should be able to
  click how much to withdraw automatically, even 0 or 10% a month" (2026-10-05),
  and, on the transit bill, asked mid-run: "for transit, fix the leak",
  households deciding by "the cost of their own transportation", the fare
  "anchored to inflation", and the test player taught to count bus wages. Every
  other call below was the triage's, the transit study's or an implementer's,
  written down with its reason (`runs/spec-model-fixes.md` §3,
  `runs/spec-transit.md` §6, `runs/fixA-notes.md` to `runs/fixD-notes.md` §4).
  **The most visible, for Jerus to confirm first:**
  - **B8 moved the default playtest a lot** (0.7.47; the triage's item 4, the
    Policy spec's ★D7). `LabourMarket.isPinned()` now reads the floor in today's
    money, so more bands read as pinned and Migration's surplus departures
    leave. The first difference is m32 (pop 296 → 294); population at m1000
    21,845 → 15,740, m2000 95,353 → 79,534, m3000 166,145 → 129,470 and m4000
    198,194 → 191,012; departures over the run 65,805 → 1,090,944 and arrivals
    885,622 → 1,694,356; cash at m4000 D$428.4T → D$196.0T and the fund's value
    D$521.8B → D$55.4B, the fund first valued at m1881 (it was m500, so the fund
    batch's own first differences read differently from the triage's). The
    traces were re-baselined as `pt0747`. It is the fix Jerus approved; is a
    city that sheds that many people to the floor the one he wants? B8 is one
    change, in `isPinned()` and what reads it (Migration, CityNeeds,
    PolicyScreen), with two harness premises moved (LabourCheck's settling,
    FoodProcessingCheck §5's meat prices).
  - **The ensemble's two new fails, K2 and R3** (16 seeds on 0.7.49: 20/25
    against 0.7.44's 22/25). K2, SHOCK's inflation at m720-840: 3.18% a year
    [0.95 to 6.35] against ≤3.00% (0.7.44: 2.87%; its margin over the twin,
    +0.39 against ≤1, passes). R3, HIGH's m660-720 minus its twin: the median
    -2.88 passes (≤0), but 3 seeds are over +1 point where 2 are allowed
    (0.7.44: 2). A2 (worst spell 18 months, median 6), S1 (5.52% [3.57 to
    10.92], peak yoy 32.01%) and H1 (3.67%; -0.06; +2.04) still fail as at
    0.7.44; A1 reads 2.22% [2.05 to 2.74], 16/16 in band, takeoffs 16/16 (median
    m1,458), the audit and the findings clean. The ensemble ran on 0.7.44 and
    then on 0.7.49 (AUTO2 alone on 0.7.48), so which batch moved K2 and R3 is
    not measured (B8's departures, the fund or transit). Accept them, or run the
    five policies on 0.7.46 to 0.7.48 in turn.
  - **The fare cap reaches 27.6% of an unskilled household's take-home** (D4,
    D5; the transit study's risk 1). 73% of workers in the research cities have
    no car of their own (74,948 commuters in city2400): they ride at any fare if
    a line reaches them and there is a seat, so a dearer fare loses only the
    owners who switch and fare revenue rises. At the $50 cap city2400 takes
    D$97.3M a month in fares (the fare ladder reads D$97.6M), a month's pass
    being 27.6% of an unskilled household's take-home; the fare card now says so
    in a third line. The design study's figure for the same cap was D$101M and
    about 54% of an unskilled household's disposable income (one riding worker's
    household pays about 1.7 passes). Jerus said "if you raise ransit price alot
    its ok"; capping it (a lower `MAX_TRANSIT_FARE`, or the car-less walking
    above a share of income) is his call.
  - **Fuel is real and imported** (D4, ★6). A drive's fuel costs $2.00 in world
    money at the exchange rate (`Motoring.CAR_FUEL_PER_JOURNEY` .002: fifteen
    kilometres at eight litres a hundred and about $1.65 a litre), forty
    journeys a month, so $80 at founding against the default pass of $100 (his
    example). Drivers pay it out of the fee waterfall and it leaves as a TRADE
    pair like the cars, through `MoneyAudit` and Trade's "Households' fuel";
    before, a car cost nothing to run. It is not in the price index (a fee line
    changes the index's saved shape: a follow-up), and a weak currency now puts
    people on the bus. City600's drivers burn D$75k–83k a month.
  - **The withdrawal's sale asks at fair value, where nobody bids** (C1, ★12;
    `runs/fixC-notes.md` §9). Over the default (0.25% a month) the fund sells
    its market book pro rata at fair value and is paid at the next month's top;
    SaveFileCheck's town at 10% asked D$467k and was paid D$52k late, while
    city2400 sells into household bids. Selling at the desk's bid would be C3's
    mirror (the rule now buys at the ask). At the dial's ends: at 0% the default
    playtest ends 13% smaller (pop 166,409 against 191,012 at m4000, the fund
    D$859.8B against D$55.4B), the transfer being real revenue there; at 10% the
    fund ends as its rescue book alone (pop 164,463), and city2400's market book
    falls from D$149.7M to D$16.0M in 8 months. His call.
  - **The fund summary turns amber while the dial sells** (the docs pass's flag
    2). The hero tints TO THE TREASURY LAST MONTH as a warning whenever the
    transfer is short, also over the default, where it reads "sold for, to pay
    next month" (fixC's ★: no WARN there), and TO THE TREASURY's "This year so
    far · N not paid" counts what is being sold for. Words only, left as built:
    should selling to pay read as a warning?
  - **`SAVE_FORMAT` stays 30, and a 0.7.46 or later save does not read whole in
    a 0.7.45 build** (the docs pass's flag 3). A 0.7.45 build refuses a 0.7.49
    save's cells (one new slot, A4), care state (A3) and households' statement
    (D4) whole, their widths being new: the downgrade that `GameVersion`'s
    header says the number guards, while CLAUDE.md's rule asks only old-in-new.
    House practice since 0.7.45's `meansIncome` slot. Bump it to 31, or keep the
    practice.
  The other starred calls, for Jerus to confirm:
  - **The triage** (`runs/spec-model-fixes.md` §3, 17 ★): 1, A1 saves the sector
    units, not Rail's per-stream figures; 2, A1 stops at its residual (12
    series, at most 8.3e-5); 3, A5 shows the statement's month on Construction's
    row and the month in progress as "since then"; 4, A6 Graduates are gross
    gains at every level; 5, A7 keeps the ×10 clamp and adds the leverage
    series; 6, A8 keys epidemics to Health's outbreaks; 7, B5 the dial is right,
    the property offset clamped at ±10; 8, B6 converts the fare to founding
    money through a derived unit; 9, B7 the trailing year where History records
    the line; 10, B9 to be built last, once Jerus had seen it (built as 0.7.49's
    D2); 11, C1 quarter-point steps a month, 0–10%, 41 positions; 12, C1 above
    the default the fund sells its market book pro rata, a month late, and buys
    nothing; 13, C1 the share of the whole value, the rescue book included and
    never sold; 14, C2 THE WITHDRAWAL first, full width, a year being 12 × the
    rate with a halving time; 15, C4 marks at fair value after a year, the
    city's holdings only; 16, C5 six significant figures; 17, C3 bids at the
    desk's ask, fair + 1%.
  - **The transit study** (`runs/spec-transit.md` §6, 11 ★): 1, σ = 1/3
    (`MODE_SPREAD`): a cell is all on the bus at half the drive's cost and all
    in the car at twice it; 2, `TRANSIT_MAX_SHARE` becomes reach, applied to
    each group, with the car-less first for seats (riders 58% of commuters at
    m1000 against 65% under "captive first"; the young playtest 11.6k against
    17.5k at m1000); 3, one car per household as today, so a couple's second
    earner and four of five flatmates are car-less, and a car-less commuter out
    of reach or without a seat walks; 4, the jam rule stays, walked down by its
    old fare curve; 5, the road's car factor unchanged; 6, fuel real, imported,
    not in the index; 7, fares follow riders and fuel drivers by row (the
    retired, the out of work and students stop paying fares); 8, the buyer's
    comparison widens the existing ceiling, not the adoption rate; 9, three
    carried scalars (captive share, fuel price, bill), saved as the month struck
    them; 10, the test player's gate is myopic and ignores lines on site; 11, B9
    as the triage specified, plus G at gross cost.
  - **0.7.46** (`runs/fixA-notes.md` §4, the implementer's own): RailCheck's
    fixture is its own steel town, played until the railway hauls (on 0.7.45 it
    reads 340 t and D$0.27M against 63,954 t and D$11.8M); SaveFileCheck's twin
    is a two-spur steel town with a based index; the carried getters return the
    row itself when nothing is carried; BusinessServicesCheck keeps its count by
    hand (+1) and adds "...which is CELL_SLOTS"; GroceryCheck saves and loads
    its twin every month for a year (2 of 12 months cross the threshold on
    0.7.45); A5's harness is a building town of its own; A6's is
    EducationCheck's funded town with two of each basic school; A7 records the
    ratio clamped at 10 and the card switches once two months are recorded; A8
    names an outbreak while `v >= EPIDEMIC_OUTBREAK`, not `>`.
  - **0.7.47** (`runs/fixB-notes.md` §4): B3's "made" is produced plus
    export-bound (`o.produced` alone is 99 of 9,211 in city2400); B4's
    InvestCheck town is its own; B6's `restorePolicyState()` puts the money unit
    to 1 first, and B6 adds "...and the fare dial's cap is the founding cap in
    the new unit" with a standing Bus Network in DenominationCheck's towns; B7
    keeps the month × 12 for Healthcare and Education (History's bills are net,
    the rows gross: 1,341 against 310 in city2400), reads business tax as
    `taxBusiness` + `taxIndustrial`, takes Paid out's year as revenue's less
    surplus's, and names the year's sign on the balance line and the card's bar.
  - **0.7.48** (`runs/fixC-notes.md` §4): `stepsFor()` is shared by the setter
    and the preview, so the preview's due equals `fundTransferDue()` to the bit;
    a late payment comes off the year's short no further than it holds; "neither
    book buys" means the rule's bids, and the player's own orders still post;
    the bond raise sale is capped at the face held; over the default the
    transfer readouts say "sold for, to pay next month" (no WARN) and "paid from
    what it sold"; the preview's cash is the fund's less what last month's sale
    still owes; halving time in months under two years, in years after; THE RULE
    card reads "a share at the desk's ask, fair value plus 1%", and the ticket's
    words were corrected by the docs pass; C4's tag is on the city's positions
    only; C5 records a non-finite price as 0.
  - **0.7.49** (`runs/fixD-notes.md` §4): `statementFor()` charges the row's
    fares and fuel per head, so the cell panel's lines still add to "Left over";
    Trade's "The households" row carries their fuel with their cars; two pure
    reads for the harnesses, `Motoring.getOwnershipCeiling()` and
    `Game.getRowFeesSettled(r)`; `getRowSpending()` leaves out the fuel as it
    leaves out the fares; `InfrastructureManager.reset()` resets the captive
    share (1) and the fuel (0); the owners' (i) says "would ride, and the seats
    the car-less leave carry N of them" (a flat "49% ride" was false in city600,
    which has no seats); CarCheck §8's towns are read after two months.
  - **Not built, from the triage:** Q1, the grocers at their floor
    (`Retail.estimatedMonthlyProfit()` counts no wages; the recommendation is to
    count them in the investment test only, which moves the traces) and Q2, the
    floor's catch-up from below (unmeasured). Both stay with the 0.7.42 to
    0.7.45 CONFIRM, below.
- **CONFIRM (from 0.7.42 to 0.7.45).** Jerus made four calls before the build
  (the checks re-baselined with every changed premise listed and no tolerance
  loosened; "you can deploy all, if something ill use github to bring it back to
  life"; "yes price rations, with an optional government assistance, defualt
  zero"; "full ui pass at the end"). Every other call below was the study's, an
  implementer's or the orchestrator's, written down with its reason
  (`runs/spec-inflation.md` §7, `runs/ui23-notes.md` §6, `runs/ui24-notes.md`
  §7, `runs/ui25-notes.md` §6, `runs/ui26-notes.md` §4, `runs/spec-ui-0745.md`
  §4, `runs/diag-0743.md` §7). **The most visible, for Jerus to confirm first:**
  - **S1's window is a city at takeoff, not a slack one** (the diagnosis's ★1;
    0.7.44's left-alone (a)). ZERO_SLACK holds 0% from month 600; on 0.7.43 the
    twin's unemployment was 14.9% at the switch and fell to about 5% while the
    population grew ×2.44 in 240 months. S1 read 7.07% a year [5.02 to 11.65] at
    0.7.44, peak yoy 22.77%, against a median of 3% or less and every seed 5% or
    less. The same hold from month 1800 (a scratch variant on 0.7.43) ran 3.16%
    [2.62 to 5.58], +1.07 over its twin, peak yoy 13.7%. Keep S1 as written and
    read it beside that variant, or move the window to a mature city.
  - **H1 asks for accelerating inflation, which the spec's ★1 rules out**
    (diagnosis ★3; 0.7.44's (b)). ★1 made the Phillips curve the existing level
    wage curve plus expectations, and KMIN .25 keeps expected inflation from
    being fully adaptive, so a held 0% gives a persistent burst and not a rising
    rate; and months 42 to 282 are the young city's slack years (unemployment
    about 24% in both runs). H1 read 3.60% [1.59 to 8.93], -0.81 against the
    first half and +1.68 against the twin (it asks 5% or more, +1 and +3); H2 is
    vacuous. Change the premise, or add a wage-growth term (the spec rejected
    it); KMIN .1 was measured to lock the founding at 10% (spec ★14).
  - **A held high rate pushes rent up through the landlords' mortgage interest**
    (his 0.7.11 rule, "keep it"; diagnosis ★2; 0.7.44's (c)). `rentBreakEven()`
    includes the interest and `carryLift()` puts it into rent. With 0.7.44's
    lender at the real rate the mortgaged stock is larger: in the diagnosis's
    scratch run 15% for ten years sent rent up 38-57% in seeds 0, 3 and 11
    (break-even ×1.9-2.1). The final ensemble's R3 passes with 2 seeds over +1
    (2 allowed) and R1 reads -2.85. Is that the city he wants? If not, strike
    the carry's interest at the real rate or cap the lift.
  - **The shelf cap (spec ★17) and A2's hot spells** (diagnosis ★4; 0.7.44's
    (d)). The cap is 1.5× the floor, approached a sixth of the way a month: any
    shortage that reaches it is +11% on the index (ln 1.5 × a grocery weight
    near .27). A2 read 8 [0 to 13] at 0.7.44: seed 12 13 months (its grocers
    sold shops for losses, then the shelf went to its cap) and seed 3 12 (a boom
    outrunning coverage), against a limit under 12.
  - **The grocers at their floor lose money** (0.7.44's §9, item 1). At
    RETAIL_MARKUP over cost and operating rates of .3-.65 the margin does not
    pay the staff: seed 12 m490-520 net -0.5 to -1.1M a month (payroll 1.5-1.65M
    of 5.7-6.6M revenue), seed 9 m700 -2.1M. They sell shops for losses mid-boom
    (seed 12 coverage 13,120 to 10,240 at m520; seed 9 down to 0-480 for 33
    months, hunger .84, the 0.1% of stock-outs left), and
    `Retail.estimatedMonthlyProfit()` counts no wages. Wages in the shelf floor
    were measured to bankrupt the shops (spec ★13: 81% hungry), so the answer is
    his.
  - **B16's smoothing** (0.7.45's ★). The voucher means test reads investment
    income smoothed by a twelfth of the gap a month (one appended cell slot,
    `Household.meansIncome`, seeded from the month's figure on an older save)
    instead of one lumpy month. At a 50% dial over 24 months city2400's
    unemployed row is aided every month (before, it lost its vouchers in
    m2418-m2424, when the month's bill went from $3.27M to $2.9k) and the rows
    crossing the line fall from 3 to 1; city600's retired row spends longer
    outside the dial (m606-m624 against m605-m619) because a lump decays slowly.
    It changes who gets vouchers once the dial is on.
  - **Credibility's speeds** (spec ★14): adapt 12 months, gain 60, loss 24, KMIN
    .25, KMAX .95, KSEED .80, tolerance 1 point, scale 4 points, loss
    lean-aware: hard to win and slow to lose while the bank fights. On 0.7.45
    trust bottomed at .76 over 125 held months (the spec's probe on 0.7.43,
    city600 held at 0.2%, had κ .483 by m684), and red was caused by setting
    trust to .504.
  The other starred calls, for Jerus to confirm:
  - **The spec** (`runs/spec-inflation.md` §7, 18 ★): 1, Phillips = the existing
    level wage curve plus expectations, no new rate term; 2, satiation at 1.5×
    the anchor price (today's shelves sit at 1.33-1.41× the floor); 3,
    elasticity .4 (food at home measured -0.3 to -0.6; the budget cap adds the
    poor's); 4, no wealth term in housing; 5, `want`, `planned` and
    `isGoingShort()` stay as the discretionary plan (DenominationCheck's
    share-path fault); 6, luxury's index weight capped at .15; 7, the investment
    hurdle real, floored at a quarter of the rate; 8, assistance for a basket
    over half of income after fixed bills; 9, the fare follows Pe; 10, QE
    unchanged; 11, `investAbroad` unchanged; 12, the savers' trap bounded, not
    fixed; 13, no wages in the shelf or meal floors; 14, the speeds (above); 15,
    Pe drift-only, seeded 1.0 on old saves; 16, the currency's drift at κ × the
    target and UIP at .15 a year; 17, cap 1.5, a sixth a month, floor catch-up
    .5, rent a twelfth; 18, a rebase every 120 months on trailing-year weights,
    chain-linked at 0.7.43's first month.
  - **0.7.42** (`runs/ui23-notes.md` §6): 1, constants struck at the top of each
    month (the spec said straight after the index; at month end TreasuryCheck's
    hands-off month and EducationCheck's re-strike fail; one preview premise
    moved, EducationCheck's TUITION_SHARE); 2, a hand-set building price
    survives the re-strike (BankCheck §19); 3, the FIXED grant's ceiling divides
    by the level; 4, the fare is the dial × the struck level, an old save's fare
    realised at 1.0, "at founding prices"; 5, before the basket is based
    smoothed inflation is the target; 6, MonetaryCheck's noise allowance left at
    .100 and the check red (0.165), settled on 0.7.43 (.056) with no change; 7,
    FoodProcessingCheck's growth bound 400 to 600 months; 8, new files LF; 9 and
    10, the `Game.getExpectedInflation()` and `getCredibility()` delegates and
    `LandMarket.getBasePricePerSqFt()`; 11, CurrencyCheck §1 and §2 keep their
    off-parity fixture and add the UIP term.
  - **0.7.43** (`runs/ui24-notes.md` §7; the implementer wrote none down, so P3b
    found these in the code): 1, meals and luxury enter the index only in months
    they were served (the playtest's founding step was a shut Boutique's ceiling
    margin); 2, a reload stands the stacks in the city's order (pre-existing,
    exposed by the reload parity); 3, the food assistance dial took the cheap
    path, a Levers dial card on Policy's out-of-work page (moved to Promises ›
    Food in 0.7.45); 4, `SAVE_FORMAT` stays 30; 5, MonetaryCheck's noise passes
    on this model with no change.
  - **0.7.44** (`runs/ui25-notes.md` §6): 1, the suppliers' credit is on top of
    the floored cash budget, not netted against the bills (retention of title;
    netted, seed 1 kept 2.4% stock-outs and hunger .106, this way 0% and .052,
    against 35% and .409 at 0.7.43); 2, the bound is a month of expected baskets
    at landed cost, not probe C's takings at the shelf price, net 30, no
    interest, no fee (harness: $240.3k repaid out of $384.3k of takings); 3,
    Restaurants and luxury left alone (neither served under 5% of what was
    wanted in any AUTO2 month: min 14% and 26%); 4, both sides booked (the
    world's share a foreign trade credit, $222.0k of $240.3k owed abroad in the
    harness city); 5, the lender reads both sides as if the credit were not
    there, the cash-flow test reads what the strike asked; 6, the fleet: till
    and lender only; 7, fix 1 is ★7 through one shared method, and the refusal
    and the screen name the rate; 8, `SAVE_FORMAT` 30.
  - **0.7.45** (`runs/ui26-notes.md` §4; D1-D21 of `runs/spec-ui-0745.md` §4
    stand as written; the implementer's own): B16 (above); the food preview
    priced at the sale's charged price, recorded at the sale; B19's harness
    label in GroceryCheck's twin city (PolicyPreviewCheck's city has nobody
    eligible); link words "struck again on what the city spent" (an old save's
    first link weighs one month), the marks naming every recorded link; the
    trust gauge's ticks solid (segmentBar has no dashed); trust floored to a
    whole percent, so a red "under half" never reads 50%; the lean chip giving
    the model's own reason when there is no lean (the spec's one sentence was
    wrong at city600 m601); every read of the sale saying "not counted yet"
    until an old save's first month; Government's "▸ who" reading the vouchers
    at the sale while People's waterfall keeps the household books a month
    behind; `struckWords()` giving the level only; rule names stacking on up to
    4 lines.
  - **The UI spec's D1-D21, in brief:** D1 the header's INFLATION line becomes
    the anchor and its figure takes the inflation verdict; D2 one PRICES row in
    NEEDS YOU, amber when trust fell this month and red when it fell under half;
    D3 credibility reads "trust NN%"; D4 Policy's tick and band judge the
    smoothed rate against TOLERANCE; D5 the Money page leads with the rate line,
    then the anchor and the drift; D6 food assistance gets its own Promises tab;
    D7 the vouchers are a step on EARNED's walk; D8 Retail's fifth figure is
    HANDED OVER; D9 the clearing price shows as a number only above the floor;
    D10 a shop's shortfall is judged on GOING SHORT's lines; D11 People's "Going
    hungry" reads baskets asked at the price; D12 History draws each component's
    chained level and the links as marks; D13 Pe is always worded "struck at ×N
    their founding figures"; D14 hunger is shown a month late and the (i) says
    so; D15 the supply limit is named from the sale; D16 new series are recorded
    from 0.7.45, no back-fill; D17 one year-book column per series; D18 Trade
    shows the drift as a force and UIP as a line; D19 the credibility step is a
    saved record (slot 8); D20 QE gets one plain line; D21 supplier credit is
    shown as books, with no verdict colour.
  - **The diagnosis's other ★** (`runs/diag-0743.md` §7): 5, the shops' working
    capital against the 0.7.12 rule (supplier credit ends the stock-outs and
    most of the hunger but is credit that rule did not grant; built in 0.7.44,
    ★1 and ★2 above); 6, the lender fix changes ★7's reach to the landlords
    (built in 0.7.44); 7, 0.7.42's F1 diagnosis ("the rule's rate about 10%")
    was wrong, it was land: 95 months of "no land".
- **CONFIRM (from 0.7.40 and 0.7.41).** Jerus chose the gauge rule ("Served %,
  higher = better") and the borrow design ("Type it + scaling buttons") before
  the build; every other call below was the implementers' or the orchestrator's,
  written down with its reason (`runs/ui21-notes.md` §4, `runs/ui22-notes.md`
  §5, the docs pass's flags in `runs/ui21-docs-pass.md`). **The most visible,
  for Jerus to confirm first:**
  - **The road's red line is 111% served (0.7.41's ★1; the docs pass's flag
    1).** It is FREE_FLOW, the model's own red line for the road (NEEDS YOU's)
    and where the flow starts to fall, read as served. A road serving 100–111%
    is red and "tight", where power and water go red at 100%. The alternative,
    red at 100% like the networks, would have shown NEEDS YOU's red road rows
    amber. Red means "at or under" the line, as NEEDS YOU strikes it (★2), so a
    network at exactly 100% reads red.
  - **Care over 100% on Build's rings and NEEDS YOU's rows, and two "served"
    figures for care (0.7.41's ★3; the docs pass's flag 2).** Build's and
    People's rings and NEEDS YOU's rows read staffed places ÷ people, unclamped,
    so a city with spare places reads over 100% (104% and 184% in the fixture).
    Services' care cards, LEAST SERVED, the drawer and People's (i) keep
    Healthcare's coverage for the month, cut for those the fee turned away and
    held at 100% by the model; the basic ladder keeps Education's figure, also
    held at 100%. Under 100% the numbers do not change. After a load and before
    a month runs the two can be far apart (17% against 90% in the fixture); they
    are coloured separately.
  - **A run is funded only up to an order the city would refuse (0.7.40's ★1).**
    If the second of three orders is short of ground, ore or licences, the page
    borrows for the first alone and says the run stops there; after the loan the
    first is built and the second shows its own refusal page. Why:
    `buildStack()`'s own rule is that the city is never sold a loan for a
    building it has nowhere to put, and the brief's "fund the run, then show the
    refusal" would borrow for orders that cannot be placed.
    `Game.buildRunAhead()` walks the run as it will be placed, each order
    against the ground and deposits the ones before it take.
  - **The order bar's and "Build all three"'s price can be a little under the
    press (0.7.40's ★3; the docs pass's flag 3).** The bar shows the sum of the
    orders' alone-quotes (`BuildAdvice.quoteTotal()`, which counts the yard free
    for every order); the press is charged the run's invoice
    (`Game.buildRunInvoice()`): $161.0M against $160.8M in the 600-month city.
    So the button can say GO and the press open Build › Funding.
  The implementers' other starred calls, for Jerus to confirm:
  - **0.7.40** (`runs/ui21-notes.md` §4): 2, an order not placed stays on its
    card everywhere (a card's Build, the order bar and Enter take a card's count
    off only once its order is placed; before, each card was emptied before its
    order was tried, so a refused or unfunded order lost its count); 4, the
    ask's steps: ±one unit of the leading digit and ±a tenth of it, never under
    a lot (one pair when they meet: a term loan at D$1B, ±D$100M only), ÷10 and
    ×10 once there is an ask, one lot at nothing, a step or scale starting from
    what is typed and not yet set; 5, the presets, each shown only when above
    nothing: at home the minimum, what falls due within a year, a month's
    spending, a year of tax (labelled "a year of tax", not "revenue": it is tax
    only) and overdrawn by; abroad the minimum in dollars and what the dollar
    paper asks within a year; 6, the box: empty with a prompt, Enter sets and
    redraws, leaving it sets and redraws after the button is up, a bad entry
    said in red under it with the ask kept; k, M, B and T in either case (m is
    million), commas only in groups of three, a leading D$, $ or US$ ignored, no
    negatives or exponents; 7, a `TimeChart` clips its sides only (a hover card
    may still hang below a short chart) and has min width 0; 8, `Money`'s unit
    edges fixed in `tightMoney()` for every screen (999.97B reads "$1.0T",
    999,600 compact "$1.0M"), `Pieces.tidyMoney()` now a no-op. Unstarred: the
    funding page's offers sit in three columns as the land office's (the third
    empty when converting) and Cancel is a plain button there.
  - **0.7.41** (`runs/ui22-notes.md` §5): 2 (with 1, above); 4, a served row
    takes the verdict's colour wherever it is drawn (NEEDS YOU rows, NEXT TO
    WATCH, Build's tiles, the Services strip's dot), the level still deciding
    what is listed, the order and the chip, so a short row with sites on the way
    sorts with the ambers but reads red (0.7.29 made the road amber everywhere
    while its sites were on the way; it reads red now); 5, City History keeps
    every label and series, having no load series ("Power supplied", "Water
    supplied" and "Road throughput" keep their meaning), so Services' POWER,
    WATER and ROADS KPIs lose their sparkline and change line; 6, the flow curve
    redrawn against served (0–200%, better to the right), the FULL cell now
    SERVED; 7, a higher school's ring on the Services pipeline is seats ÷ who
    would come, in the verdict's colour (it was seats in use, in teal).
    Unstarred: 8, the words ("served, short", "served, tight", "served, enough";
    NEEDS YOU rows carry the figure only; 99.5% to just under 100% prints
    "99%"); 9, suggestions still stop at NEEDS YOU's lines, so a care order
    closing at 81% reads amber "short" and the order bar turns green only at
    enough; 10, the RESOURCES header counts by the verdict and can now be red
    (it was amber at most, and a congested road was not counted).
- **CONFIRM (from 0.7.32 to 0.7.39), and what was left out of them.** The
  specs' ★ decisions were decided by the orchestrator as each spec recommended:
  Finances' D1–D20 (D20 adjusted), the Bank's D1–D18, Trade's D1–D25 but D4,
  Policy's D1–D18 but D7 and D18's step 1, City History's D1–D11, the fund's
  D1–D12 (0.7.39, `runs/spec-fund-0739.md`); 0.7.34 had no study, its design
  the brief's, from Jerus's words, and nor had 0.7.38.
  **The most visible, for
  Jerus to confirm first:**
  - **The fund's hand names its price (0.7.39's ★D2).** Fair value stays the
    default; the ticket offers Fair value (the rule's) · Best bid · Best ask ·
    Last and a ±1% stepper on fair, with no upper cap, and prints the premium
    ("Over fair value +10.0% - the seller is paid more than the register
    reckons; the fund carries it at the last trade"). It is the batch's one
    behaviour change, with cancelling (★D3): in both research cities a buy at
    fair filled nothing (MODEL BUGS item 21), and "Best bid" is the price that
    fills. The playtest places no hand orders, so it never reaches the traces.
  - **P&L in green and red, and only P&L (0.7.39's ★D7).** `GOOD_MONEY` and
    `BAD` from `BRIDGE_NOTHING` (D$0.5k) up; under it the body's ink with its
    sign and arrow ("▲ +D$368 (+0.2%)"); a figure that rounds to nothing plain
    "D$0". Prices, moves, yields, chart lines and Search's moves stay ink; one
    home, `Pieces.pnl`. Percentages to one place, two under a tenth of a
    point, none under a two-hundredth: never "+0.0%". (Unsigned under D$0.5k
    was tried: "D$451" on a 2.6% gain read as a value.)
  - **The header's rate line changes colour (0.7.35's D8).** One parity rule,
    `ForeignAccounts.PARITY_WATCH` (25%) and `PARITY_FAR` (50%) either side,
    for the Trade tab, the drawer's THE CURRENCY row and the header's RATE
    tile's second line: grey under 25% from parity, amber from 25%, red from
    50%, grey when pinned. It was grey near parity or stronger by any amount,
    amber more than 5% weaker and red past 25% weaker. So a currency 25–50%
    stronger now reads amber (both probe cities, 26–31% stronger), 5–25% weaker
    grey, and 25–50% weaker amber where it read red. The drawer's Dashboard
    TRADE "vs parity" line, amber past 15%, was a fourth reader off the rule;
    it is on the rule since 0.7.38.
  - **Build at none sets the count to 1 (0.7.34's ★1).** A press on "Build ·
    choose how many" chooses one and prices it and never orders; a second,
    separate press orders. A double-click is one press on every action button
    and pill (★2): a single click does what it did, but until now a
    double-click on the land office's Buy bought the card that slid into its
    place too.
  - **PROMISES no longer counts the tuition the city waives (0.7.36's D13):**
    revenue forgone, not money out of the treasury. The KPI, the hub card and
    "out of the treasury" changed ($8.1M in the 2,400-month city, "$5.6M of it
    the pension gap"), and Schools says "The city waived $X of tuition:
    forgone, outside the sum." in grey.
  - **The button colours.** The land office takes Build's building pink
    (0.7.34's ★6); GO is white on the pink's darker step, `BUILDING_DARK`
    #cf5590 (3.9:1, where the pink itself would carry white at 2.5:1), lighter
    under the pointer and darker pressed (★7: does it still read as the
    building pink?). Trade's exchange and Policy's every Apply are the same
    button in the money blue, and so is City History's "Write the year book",
    not the CONFIRM green its spec named (0.7.37's ★1: the GO look is built on
    an area colour).
  The implementers' other starred calls, for Jerus to confirm:
  - **0.7.32** (`runs/ui13-notes.md` §4): 1, the three icons PAPER, BANKNOTE
    and SAFE, by eye; 2, more model reads than "no new figures", so nothing
    shown is the screen's arithmetic; 3, COUPON plain while comfortable, amber
    felt, red constrained, never green; 4, the service gauge leads with the
    larger of its two marks (a note the rollover will mostly net still reads
    "constrained": 113% in the 600-month city); 5, "later" on the same scale as
    the years (since 0.7.34 drawn broken past twice the tallest); 6, the
    heaviest tag red past `CityNeeds.YEAR_WALL`, half a year of revenue; 7,
    NEXT DUE's chip, "paid ✓" or "rolled", from the rollover's record; 8, the
    last roll dated by the month it fell due; 9, import cover's one tick, the
    model's six months (the old three was a screen literal); 10, FOREIGN red
    only while the window abroad is shut; 11, the stress bars plain; 12, the
    default scar off the rate built up (it is on the world's premium only); 13,
    the offer card's green "Cash it brings", the land office's; 14, Issued's
    receipt only when the booking matches the quote; 15, NEXT DUE the soonest
    piece.
  - **0.7.33** (`runs/ui14-notes.md` §4): 1, B5 on the load path (D10); 2, the
    branch verdict `planBank()`'s, the planner read pure (D9); 3, HOW FULL at
    NEEDS YOU's THE BANK level, amber past 100%, red only failed or with no
    bank (the spec said red past 100%); 4, the action cards at the top of the
    page, not in the frame; 5, ticks named by their figure, the words in a key
    line; 6, SectorScreen's owners card untouched, with its 0.7.30 colours; 7,
    the capital gauge grows on a month only while the risk-weighted ratio binds
    (his city binds on leverage); 8, D11's leverage series for later (MODEL
    BUGS item 15); 9, the walk's totals the model's, in grey, a step under half
    a dollar "nothing"; 10, the equity's walk without opening and closing
    columns; 11, PAYOUT against last month's profit; 12, two ratios of model
    figures worked on the screen; 13, prime's move on the strip and the Prime
    rung.
  - **0.7.34** (`runs/ui15-notes.md` §4): 1, 2, 6 and 7 (above); 3, a HELD
    press opens the refusal page, whose pill is the door, though the line's "›"
    promises the door itself (the docs pass's flag 1: Jerus's call; one line in
    `orderControls()` sends the press straight there); 5, "No land free" only
    with none free, else "Short 476k sq ft of land"; 9, "Buy the next 5"
    carries the count, its total beside it; 10, the land office's credit words
    ("Buy on credit · D$1.0B"; from the vault and short "Buy · US$…" over "the
    vault is short: ways to pay"); 13, "Build all three" held when any of its
    cards is; 15, "loaded or founded" on the Sectors line; 19, `LATER_BREAK` 2
    and `LATER_CAP` 1.2.
  - **0.7.35** (`runs/ui16-notes.md` §4): 1, D8 (above); 2, D25's words (the
    section hints; the gauge chips "crisis pricing" / "thin" / "comfortable",
    "a run likely" / "thin" / "covered", "near" / "far" / "very far"; "Fuel for
    the railway"; "the treasury's position"; "openness: trade ≥ output (100%)";
    "not counted yet" with "since the city was loaded or founded: a month on,
    it is"; "WHAT WE TRADE, THE MONTH THE CITY WAS SAVED IN"; "The month's
    surplus" or "deficit"; "income from abroad, net"; "Buy reserves"; "Buy
    foreign money / choose how much above"; "‹ stronger" and "weaker ›"; "land
    cost US$X since founding, none of it from the vault"), with the docs pass's
    flag 2 (a founded city reads as just loaded); 4, D4 not built (MODEL BUGS
    item 2); 5, What we trade after a load shows the month the city was saved
    in, from the businesses' saved books; 6, the river draws the balance of
    payments only, the treasury's purchases as chips below the line; 9, D13,
    `fxParity` a History series (his parity line starts the month he first
    plays 0.7.35); 16, `ForeignAccounts.THIN_COVER` = 3 named in the model; 17,
    the pull struck as the reprice applies it (+0.142% then, +0.143% now in the
    2,400-month city).
  - **0.7.36** (`runs/ui17-notes.md` §4): 1, D7 / B8 ready to build (MODEL BUGS
    item 4); 2, the bank's row is next month's bill, taxed in arrears; 3, M11
    shows only what a pensioner household has, not what it has left after rent
    and bills; 4, D13 (above); 5, the fare's Apply restyled as the same blue
    button, through `applyBar()`; 6, D18 step 6, the fare onto the dial card,
    not done (done in 0.7.38); 7, the People screen's founding floor (done in
    0.7.38); and §8.1, the two inflation colours (MODEL BUGS item 16).
  - **0.7.37** (`runs/ui18-notes.md` §4): 1 (above); 2, no "200 years · since
    Feb 2000" hint in the head, THE CITY saying it under it; 3, "2,400 months
    recorded · since Feb 2000", not "founded Feb 2000" (a city is founded in
    month 1 and its history's axis starts at month 2); 4, RUNNING NOW's note
    for one episode alone, "since Aug 2199 · 6 months"; 8, "at" an end of a
    band within 1e-6 of the market's own index (`AT_END`); 11, the seller's
    door as its name with "›" in the figure column ("Retail ›"); 16, D9's
    labels, all 69 ("GDP: consumption", "M0, the central bank's money",
    "<Sector> net income", "<Company> fair value" and the rest); 25, the
    canvases' first draw at the last known width, 1,234, snapping on the first
    layout.
  - **0.7.38** (`runs/ui19-notes.md` §4): 2, the Bank's ITS RATES (i) no
    longer promises a drag, and the drag is not built (a pan on a small chart
    is new behaviour, not a display fix; the (i) names "Over the years", City
    History, as the way back); 4, the drawer's Import cover takes Trade's
    verdict, red under 3 months (`THIN_COVER`), amber under 6
    (`COMFORTABLE_COVER`), plain above (it was amber under a literal 3; in both
    research cities, and probably his, it turns from amber to red); 6, History
    draws no floor in today's money (neither the cost of living nor its
    adjustment is a series the history keeps, and a line from the price index
    would be invented; offering it is a saved series, a model batch); 8, the
    fare card "THE FARE" in capitals and stacked (the ladder at 520, its four
    effect rows under it), as Policy's dial cards are; 11, right after Apply,
    until the month turns, the fare card shows a move at the new fare (riders
    41,491 → 32,756): the network's fare share is struck when the month turns,
    as in Policy's decision 13.
  - **0.7.39** (`runs/ui20-notes.md` §4): D2 and D7 (above); D1, Search finds
    every listed company, every bond outstanding, the preferred and warrants
    while held, and a closed lot only when named (kind CLOSED), ranked
    starts-with, then held, then size, an issuer's bonds by maturity; D3,
    cancel only an order still waiting for the step (one already posted rests
    under ON THE BOOK, without Cancel); D4, buy by amount and sell by
    quantity, the toggle converting at the order's price, a new ticket on
    Buy, by amount, at fair; D5, one position a company (the rule's and the
    hand's pooled), two lots for the bank with their sum on its page; D6, an
    older save seeded at market value at the load month, tagged "cost from",
    the dividends before shown apart, the rescue lot exact, the headline
    exact from the counters; D8, the house ranges, 10Y the default; B1, a
    hand buy past the 10% cap stopped at the cap at posting, since past it
    the rule asks the excess back at fair from the next step, a certain loss
    the player cannot see; B9, a queued buy's cash held from the rule's bids
    (`TreasuryFund.handReserve()`, the rule's mix still reading the whole
    cash; new buys clamped to `fundCashFree()`; Draw out only free cash);
    after the docs pass (`runs/ui20b-notes.md` §4), ★ **the hand first for
    the room, as for the cash**: the 10% cap counts every order the fund has
    on a company as if it filled, and the rule's bid makes way for the hand's
    (sized at the step while the hand's orders still wait in the queue,
    filled between steps no further than the room they leave), so the rule's
    bid and every hand order, all filled, stop at the cap and the hand is
    never starved by a bid that will not fill; built rule first, as the
    brief's words read, a fund worth more than about a seventh of the market
    bid the whole 10% of every company at fair, where nothing fills (MODEL
    BUGS item 21), and the ticket said "No room" on every company while the
    fund held none, so it was withdrawn; `BARGAIN` = 5%, a new dial ("bought
    N% below value"); `ACTIVITY_ROWS` = 1000 (about 18 rows a month in the
    default playtest, about 4½ years kept; about two years of the 600-month
    city's; each row about 100 bytes of save, the dropped ones counted; its
    dial sentence still gives the study's count, the docs pass's flag 5,
    section 6).

  Not in these batches: Trade's D4 and Policy's D7 (MODEL BUGS items 2 and 4);
  the Bank's D11 leverage series (item 15); the fare onto the dial card and the
  floor on People (done in 0.7.38); the drag on the Bank's rates chart
  (0.7.38's ★2; section 3, THE BANK); the vitals counting up on Policy (People's
  `countUp` is PeopleScreen's own); a `cardChart` for History's pins (the
  spec's Trade piece did not land); Build's credit page adopting
  `Pieces.offerCard()`; Government's "Bought land with … reserves" step still
  opening the last Trade page; 0.7.39's D12, B3's mark, B6's precision and
  B10's rule (MODEL BUGS items 19–21), a bond's price history and a cap on the
  hand's limit.
- **CONFIRM (from 0.7.31), and what was left out of it.** The spec's ★
  decisions (§10, D1–D18) were made by the orchestrator while Jerus was at
  work, as the spec recommended: the four pages and their names kept (D1);
  EARNED · SURPLUS · BANKED everywhere (D2); `getEarnedToBudget()` and its
  residual in the model (D3); repairs and the fares outside the totals, named
  there (D4); the GDP history seeded on load (D5); taken in and paid out as the
  money blue and its darker step (D6); one verdict each, from NEEDS YOU (D7);
  "of the change" cut (D8); ranked lists, the lines at nothing folded (D9); the
  pension chip on NEEDS YOU's row (D10); "of GDP" as this month × 12 for now
  (D11); Output led by History's real layers (D12); B5's labels (D13); wide
  pages (D14); doors where each line is decided (D15); the open state kept
  through a month (D16); B10 and B5's G reported (D17); "Care and schools
  staff" (D18).
  **The most visible, for Jerus to confirm first: D2.** The header's line under
  the cash now reads "+$X earned a month" (it read "+$X a month"), and the
  drawer's "Net income" reads "Earned". It changes wording Jerus chose on the
  morning of 2026-10-01; the figure, `Game.getIncome()`, is unchanged. The
  tooltip and the (i) say EARNED, SURPLUS and BANKED.
  The other starred calls (`runs/ui12-notes.md` §4, written by the docs pass
  from the code), for Jerus to confirm:
  - **D3,** the walk from EARNED to the budget is the model's
    (`Game.getEarnedToBudget()`, `getEarnedResidual()`, the fares' step named
    once as `Game.EARNED_FARES`), held by TreasuryCheck §8; the list carries
    every line at $0 too, and the bridge leaves out steps under half a
    thousand;
  - **D4,** repairs and the fares stay outside the budget's totals and are
    named there, in the rings' feet and on the bridge. **For Jerus: a model
    batch** in which `NationalAccounts` carries both and the two
    `TreasuryJournal.record` calls come out; it changes `getBalance()`, so
    `getTreasurySurplus()`, the student grant's SURPLUS_SHARE basis and NEEDS
    YOU's budget row, and the traces move (section 4, TWO BUDGET LINES THE
    BALANCE OMITS);
  - **D5 / B1,** the GDP history seeded on the load path from
    `HistorySave.getGdp()` (the last 120 months; a city with no history file
    keeps the rebuild's month); `EconomyManager.setPreviousGdp()` now finds a
    year where it found a month, which only the drawer and the console's
    economy report read;
  - **B8,** `getIncome()` writes nothing (`EconomyManager.getTaxIncomeNow()`).
    The consequence: after a dial is moved between presses, a screen reading
    the month's struck wage tax, contributions and premiums (the pension card's
    contributions, the wage-tax line) keeps the month's figures until the next
    press, as the budget does; before, the header's draw quietly re-struck
    them;
  - **B7,** "Today's dials, not the month's": EARNED is read at today's rates
    and the budget at the month's, and their difference is the last step of the
    bridge's first column, a door to Policy › Taxes; $0 in any month nobody
    touched a dial (five points of wage tax on the 2,400-month city made it
    −$12.3M); the header's (i) says "read at today's tax rates";
  - **D7,** one verdict each and no thresholds of the screen's own: SURPLUS in
    NEEDS YOU's THE BUDGET colour; OWED red only at BORROWING red
    (`atCeiling()`), here and on Finances' OWED cell; EARNED and BANKED plain.
    The 60%/120% thresholds went from both OWED cells only (section 3,
    GOVERNMENT);
  - **D17,** B10 and B5's G reported, not fixed (MODEL BUGS, items 11 and 12);
  - **9,** SURPLUS alone carries a sparkline and "▲ … on last month": History
    keeps a `surplus` series and none for EARNED, and BANKED has neither; the
    spec gave the change to all three money figures, and no reason is written
    down;
  - **10,** a line under half a thousand ($500) is "nothing": no arc, no row,
    and a bridge step under it is left out (section 3, GOVERNMENT).

  Not in this batch: D4's model batch (above); D11's trailing year
  (`YearBook.nominalYear`, MODEL BUGS item 14); D18's payroll of every city
  post (`Game.getCityPayroll()`, with D4's batch); `Sparkline` moved to Pieces
  (the SURPLUS cell uses the header's `Pieces.sparkline()`); ~~Finances' two
  other 60%/120% judgements~~ (done in 0.7.32, the Finances spec's D7).
- **CONFIRM (from 0.7.28 to 0.7.30), and what was left out of them.** The
  specs' ★ decisions were decided by the orchestrator while Jerus was at work,
  as the specs recommended. Services' eighteen: an Overview first in every
  system; Utilities as Overview and Books; power in kW; the rings on the
  coverage the model applied, Build on the beds; one verdict judge,
  `CityNeeds`; category colours for the stacked bars; the sick bar
  part-to-whole; senior care's places; a read for the leavers' diplomas; the
  frame fixed; who draws in three parts; doors both ways with Build; five
  events; teal icon squares; Books last; a door on every figure; "Set the fee
  ›"; Infrastructure split out first. Infrastructure's twenty: small pure
  model reads; the after-load railway bill left for Jerus; one verdict and one
  wording for the road; the curve's fixed scale; the streams' colours; the
  per-good bars on the import price; no tonnes per good; the lorries in one
  line; Infrastructure owns the road; History opened without a pin; doors both
  ways with Build; BUS and LORRY; five figures; the fare on Transit; wide
  pages; the details state kept; the page names kept; the split first; money
  neutral. Sectors' thirteen: `SectorFlow` and its harness; the word's kind in
  the model; `operations()` in two halves; three columns in Build's order
  under their groups; one investors' record per sector; the first gate only;
  notes behind (i)s by rule; B1–B4 and B7 fixed, B6 and B12 for Jerus; money
  in and out in the revenue and spending colours; chevrons, not a Sankey; the
  owners block redrawn once, the Bank's with it; the names kept; RUNNING AT a
  door to Build.
  The two most visible, for Jerus to confirm: **power in kW** (0.7.28's D3),
  scaled to MW and GW, on Services and Build (the coal plant's 280,000 is what
  the model's own comment calls "the 325 MW this plant actually is"; water
  keeps "units a month"); and **the road worded "162% full · 56% flow"
  everywhere** (0.7.29's D3 and D4), in NEEDS YOU's one colour, in the drawer,
  NEEDS YOU, Build's tile and rings, Services' road card and Infrastructure.
  The implementers' starred calls, for Jerus to confirm:
  - **0.7.28** (`runs/ui9-notes.md` §4):
    - **D9 (the spec's), `Education.getNewDiplomas()` is not saved:** a loaded
      city's Diplomas node and NEW LICENCES read "not recorded yet" until a
      month runs; SAVE_FORMAT stays 30;
    - **1,** the figures, the systems strip and the page strip sit at the
      left, as Build's do (the old ones were centred);
    - **2,** sparklines only where the history keeps the figure, 13 cells of
      16 (none on THINNEST COVER, GROUND LEFT or THEY EARN);
    - **4,** POLICE COVER and THEY EARN are plain: no NEEDS YOU row judges
      them, and the old screen coloured both;
    - **5,** POWER and WATER show the share supplied in the colour of NEEDS
      YOU's POWER and WATER rows, which judge the load (the fixture city's
      water reads "100%" in amber at 90% load); ROADS likewise;
    - **8,** three new icons, DROP, CANE and CELL, drawn by hand; childcare
      and death care take the People page's child and headstone;
    - **10,** the road row (capacity solid, the load a tick, "free flow
      ends"); replaced by 0.7.29's road card;
    - **13,** the pipeline's connectors are the lane hints, not drawn elbows,
      which would point at nothing once the lanes wrap;
    - **16,** crime's reasons coloured in the enum's order, and three of them
      doors (Too few police → Police; No home and Crowded → Build › Homes),
      which the spec did not name;
    - **18,** the ground's words ("no ground", "full", "not in use", "100+
      years", "N mo"), coloured only while NEEDS YOU lists the plots;
    - **20,** TAUGHT EVER's figure moved to the ladder's details as
      "Diploma-holders gained since the founding", with a note that it counts
      only the months the band grew.
  - **0.7.29** (`runs/ui10-notes.md` §4):
    - **1,** the railway's two saved figures (what the rule allows it to bill,
      what went abroad) are not known after loading an older save, rather than
      read as 0: "—" and "known after a month". His Continue shows this for
      one month;
    - **2,** four more pure reads than the spec listed (the free-flow load,
      the funnel's steps, what the railway kept and its replacement sets, the
      world's margin and the railway's part of the wedge), so no figure is the
      model's arithmetic done in the screen;
    - **4,** B3 fixed in the model's getters (`getHeadroom()`,
      `getSpareCapacity()` on the effective load), not with a second getter;
      nothing in the model reads either;
    - **5,** the one wording reached NEEDS YOU's own reading and Build's road
      tile and ring; Services' ROADS figure stays the flow alone (the brief
      limited ServicesScreen to the card);
    - **6,** Build's doors: "what is on the road: Infrastructure ›" on Road
      capacity and "who rides: Infrastructure ›" on Transit, in place of
      0.7.28's "why ›";
    - **9,** the walk's rule names in the house's short numbers ("capacity
      88k", "slows past 79k"), the exact figures in their tooltips;
    - **13,** the old CONGESTED alert is the second half of the flow line's
      (i).
  - **0.7.30** (`runs/ui11-notes.md` §4):
    - **1,** a twelfth word kind, **supply** (amber), for "the world will not
      sell a beam at any price" and its like (the fixture town's Automotive
      word would otherwise read "other");
    - **2,** phrases beyond the spec's table, each with an example in
      BuildCardCheck;
    - **3,** `investorsWords()` and `gateWords()` stay on BuildScreen, and
      Sectors calls them, rather than moving to a new class;
    - **5,** the owners card's chart is the old trend chart, not a City
      History chart;
    - **6,** the flow's units and its money are two months, as the old pages
      were: the units the month the plants ran, the money the struck
      statement;
    - **7,** after a load the flow's units read "not counted yet", not 0;
    - **8,** B3 and B4 fixed in the model's base block (`plantLines()`), so
      the console and the fold say what the flow says;
    - **10,** Cash & debt's rate bar reads what new borrowing costs today
      (`getRate()`) in its parts, with the month's own rate beside it when the
      two differ;
    - **12,** RUNNING AT's door goes to the thinnest of power, the road and
      health; water and vans have no Build page and are only named;
    - **13,** WHAT YOU CONTROL's fourth door is Policy › Money's policy rate,
      not the bank's page;
    - **15,** Real Estate's housing row reads "rent on the homes let, a
      month", with no count (its units are billed per head of room, not
      doors);
    - **22,** B13 not fixed: a comma in Real Estate's word would change a
      playtest trace (MODEL BUGS, item 10).

  Not in these batches: the first-licence event (0.7.28's D13, waiting for
  per-profession licences in the history); a model read for a course's intake
  in the month (the funnel's last step); History's "Graduates" (MODEL BUGS,
  item 7); Services' ROADS figure as the pair; the railway's after-load bill
  (MODEL BUGS, item 1); the Sectors spec's B6, B12 and B13 (MODEL BUGS, items
  8–10); NEEDS YOU's CARE rows and Build's rings on the beds (0.7.27's D16,
  below).
- **CONFIRM (from 0.7.26 and 0.7.27), and what was left out of them.** The
  specs' ★ decisions were decided by the orchestrator while Jerus was at
  work, as the specs recommended. The Land office's fifteen: BEST VALUE kept
  on card 1 with independent tags; square feet leading; the ground bar's
  scale the free ground plus the N picked; no verdict colour on "% used"
  anywhere; a 3 × 3 of wide cards; neutral prices, red only when no way pays;
  badges and a pink edge on the next N; no "listed when"; the going rate as
  `LandMarket.goingUsdPerSqFt()`; `Palette.ORE` and `Icons.ORE`; waiting
  sectors by name; arriving from Build left out; the funding page in the
  frame; `landPrice` alone behind details; the receipt through `Formats`.
  People's nineteen: one page plus Household money; never green as a series;
  the settled ghost; Migration's month saved (SAVE_FORMAT 30); the dead by
  cause, the leavers and the draw's halves as getters; care rings opening
  Build › Healthcare; death care as one ring; a verdict that counts every
  row; the hunger split; the books beside the grid; the live wage; INCOME PER
  RESIDENT; four omissions cut; the Pensions fix; HUNGRY on the share of
  people; the bank's fees as a step; Work kept on People. The implementers'
  starred calls, for Jerus to confirm:
  - **0.7.26** (`runs/ui7-notes.md` §4):
    - **D1, the receipt** is its own wrapped line under ON OFFER, not a
      truncated chip beside the button: nothing may be cut, and a two-plot
      receipt is about 150 characters;
    - **D2, the left panel's LAND header** leads with the ground free ("51.0M
      free · D$46.52/sq ft") in the GROUND row's colour; "Used 95%" moved
      inside, and the red alert row reads "Land · none free" only at none;
    - **D8, MOST ORE** is its own sand tag beside "ORE ×2 · 7.7M t", not
      inside it (a pill does not wrap in the 110 px column);
    - **D12, NEW** lasts until the month turns, through redraws (the
      stepper's first click would otherwise wipe it);
    - **D16, `Icons.ORE`** is drawn by hand; Lucide's own pickaxe could not
      be checked in the cloud. Look at it.
  - **0.7.27** (`runs/ui8-notes.md` §4):
    - **D1,** the month's "start → end" is a line over the waterfall, not
      captions over its first and last columns;
    - **D2,** the fourth care ring is "Death care", as Build titles it, not
      "Burial ground";
    - **D3,** "going hungry" below the rule is a plan that could not buy a
      basket (`planned < subsistence`), not `isGoingShort()`, a plan short of
      what the household wanted; both pick the orphans in both probe cities;
    - **D4,** the retired rows keep their statement figure: Senior and Elder
      alone read $0, where against a basket they would read −$218, "living
      on savings";
    - **D5,** the homes' verdict is the old sentence's, red when doubled up or
      short of doors, so SPARE HOMES is red where it was amber;
    - **D8,** the mosaic's short words ("+ baby", "+ child, teen", "five
      sharing", "large family"), the full names in the tooltips, its glyphs
      drawn shapes;
    - **D9,** the mosaic's strips on one scale, the biggest tiles
      proportional and the rest at 56 px, the smallest folding into "+n more"
      only when the minimums do not fit (the spec's literal rule would show 4
      of 11 family shapes; its worked example shows 8).

  For Jerus to decide: **0.7.27's D16**, NEEDS YOU's CARE rows and Build's
  rings still read the beds, not the beds less the priced out
  (`CityNeeds.careCover()`, `BuildAdvice.cover()`); changing them would move
  NEEDS YOU's levels. Not in these batches: arriving at the Land office from
  Build's "Not enough land" with N preset (`Game.plotsToCover(sqFt)`) and
  that page's "roughly" quote (★12, spec §8.14); the red tick at a waiting
  sector's need (`Game.landWantedSqFt()`, ★11); Build's "Order on credit"
  page adopting `Pieces.offerCard()`; Policy's pension cover as a bullet
  bar; the Government's treasury bridge as a waterfall; the HEALTH, COIN and
  FOOD icons on People; a clickable Services › Health link in the care (i)s.
- **CONFIRM (from 0.7.25), and two left out of it.** The spec's five ★
  questions were decided while Jerus was at work, as the spec recommended:
  land as the market cards' second bar (offices: the posts the city couldn't
  staff); value added at today's prices for producers; an amber "this one:";
  the city cards' (i), "runs" and +100 back; an order bar on market pages,
  with no measure. The implementer's four for Jerus to confirm
  (`runs/ui6-notes.md` §4):
  - **D3, the market headings' subtitles.** The maker groups' come from the
    goods they make and use ("STEEL · from iron ore"); the rest are the
    implementer's words ("a door for every household", "food for every
    household", "the city's bank, a counter at a time", "the crews every site
    waits on", "work the world buys", "freight across the city's boundary",
    "somewhere for the well-off to spend", "the city's food, cooked").
  - **D4, one tag rule for every card:** two cards or more, and the lowest
    strictly below the highest. On the city cards 0.7.24 tagged every card
    when all cost the same.
  - **D9, the empty word:** "Investors: nothing recorded since the city was
    loaded or founded" (the spec had "…since the city was loaded").
  - **D11, no 0 after a load:** the luxury and restaurant notes say only "the
    counters serve C" / "the kitchens serve C" until a month runs, because
    `LuxuryRetail.getWanted()` and `Restaurants.getWanted()` are not saved.

  Not in this batch, for Jerus: saving `lastInvestment` with the city, so the
  investors' line and the Investors page survive a load (a save-format
  change); and the Restaurants note (spec §9.5), "N meals wanted; the kitchens
  serve C" beside "tables ahead of diners" (the planner builds only past 5%
  headroom: 3,383,854 wanted against 3,226,500 in the spec's probe city),
  which could say "investors build past N".
- **FOUND IN JERUS'S 0.7.14 CITY** (his year book, 2026-09-28, copy in
  the cloud at `runs/yb0714/year-book.txt`). The city ran 150 years to 1.05M
  people with no bank failure; 0.7.15 would have played it the same unless the
  holdings dial was above zero or the target above 10%. What it shows:
  - **Jobs outran people.** 584,566 posts and 388,801 workers at year 149,
    with unemployment at nothing for 120 years, so a third of all posts stood
    empty. The unskilled premium sat at 3.6 of the 4.0 cap: an unskilled post
    paid about $69k a month against a $30k average wage, so the ladder is
    upside down.
  - **That is the deficit.** Health $25.2B, schools $23.7B and the grant
    $18.2B (a share of the unskilled wage) came to 88% of $76.3B revenue.
    Deficits in most years since year 132, advances to $123B, and a
    treasury crisis from 2129 to the end.
  - **Housing stalled.** About 720 homes a year for fifty years (196,818 to
    232,728) against 280k households. Arrivals were exactly zero in 24
    whole calendar years, mostly alternate ones, from year 69 to 141: a
    locked 24-month cycle, to trace.
  - **Money piled up.** Household savings reached $5.94T, 31 years of GDP.
    Bank deposits were pinned at $6B (24 branches x 250k), and the deposit
    rate was nil because the policy rate sat at zero in 132 of 150 years.
    Luxury Retail netted $25.8B a year, 2.5x any other sector.
  - **Construction lost money every year from 114**, $6.5-6.8B a year by the
    140s, while its capacity kept growing.
  - **Materials and Mining died**; the food chain is a few hundred workers.
  - **The currency went 1 to 4.6** despite a $54.7B trade surplus.
  - **The Depression of 2089 was death care running out**: 19,905 unburied,
    31% sick, real GDP down 18%, recovered the year the dead were buried. As
    designed.
  - **Traced the same day on a copy of his save** (0.7.15, 120 months forward,
    hands off; the report is `runs/trace0714-notes.md` in the cloud, and its
    findings go into 0.7.17's note). The causes:
    - **The bottom rung is empty.** 586,214 posts for 400,856 workers at
      month 1792. The allocator fills from the top down and no unskilled
      migrant exists (`WageBand.NONE` arrival ceiling 0), so all 185,132
      missing workers are empty unskilled posts: 8.5% staffed at $68.6k a
      month, the city's highest wage (a doctor earns $60.4k). 69% of them are
      the city's own services (childcare 54,508, buses 32,290).
    - **The deficit is that wage.** Health, schools and the grant cost about
      $15.7B a year more than at the band base, twice the year-149 deficit.
      The advances ceiling had been raised to its 36-month maximum; nothing
      was carried past it by promises.
    - **The builders stand on that rung.** A depot is 70% unskilled posts, so
      the builders ran at 36% staffing: 71k points a month of 259k capacity,
      28k of it repairs. The rest is split equally per building type on site
      (`BuildingManager.advanceConstruction`), so a one-depot order takes a
      quarter and banks all but 400 points (7.82M points banked on idle
      stacks), and the landlords, allowed one order, get about 250 homes a
      month. Earlier, retirement read nameplate against the queue alone and
      sold the builders from 232,000 to 56,800 points (months 912–1020): homes
      sat flat for nine years.
    - **Arrivals are not a 24-month cycle.** `Migration.crowdingFactor()`
      returns 0 while any household is unplaced, then jumps to 0.63: one
      month of 25–32k arrivals, then zero, every 9–23 months. Summed by
      calendar year that alternates. A reload changes nothing.
    - **Construction's loss:** payroll charged at the sector's average fill
      (+$269M of its $600M a month; +$1,273M a month across all sectors, paid
      by the firms and received by nobody), the labour part of every price in
      founding dollars (`BuildingsTemplate.cashCost`), material priced at the
      order month and bought at the draw month, and VAT on material passed
      through at cost.
    - **Luxury's margin is 2.65, not the cap:** it reads customers wanted at
      the floor price, not served at the struck one. **Deposits:** 24 branches
      × a founding-money $250k cap. **Materials and Mining** died of the
      unskilled wage (160 of 250 and 45 of 60 posts unskilled).
    - **Probes:** getting the builders' output to the housing sites (staffing
      them, or sharing site work by buildings or by points) roughly triples
      the homes built in ten years (+38k to +102–109k), and the unskilled
      premium falls from 3.5 to about 1.1 with no other change.
- **NEXT: JERUS'S FIXES FROM HIS CITY, IN THREE VERSIONS** (his answers,
  2026-09-28, verbatim). One version a round (round 1 is 0.7.17, round 2
  0.7.18, round 3 0.7.19), each gated and measured on the eight-seed
  ensembles and on a copy of his slot-10 city:
  - **Round 1, the builders and housing:** "Share building work fairly
    (Recommended), Builders count repairs and staffing, Landlords hold work,
    not one order, Arrivals limited, not switched off"; and from the money
    answers, "Pay wages by job type (Recommended)" — the payroll leak, since
    the firms' bill and the households' pay must be one figure. He asked
    "sick still get paid right?": yes, payroll counts filled posts and
    sickness cuts output only; the fix keeps that.
  - **Round 2, labour:** "Every planner checks staffing, Workers take the
    best-paid job, Some unskilled migrants". Not chosen: showing the
    staffable share before a service is built.
  - **Round 3, prices:** "Builders' prices keep up, Grant follows prices, Bank
    deposit cap rises with prices"; Luxury, "The customers actually served";
    and the branches: "bank branch i think should be customers perhaps, but
    make it be sustainable even if just account fees, aka one branch
    maintence and operating costs should be less than the revenue it makes of
    fees for that specific branch, that should always be true." Needs sourced
    figures (customers per branch, a branch's running cost, account fees).
  - ~~**Round 1 built in the cloud 2026-09-28, HELD on Jerus's answers**
    (implementer's record `runs/r1-notes.md`; the tree `/home/claude/cbs`,
    0.7.16 snapshot `cbs-b12`). His city, 120 months: homes +38k → +117k,
    arrivals in 117 months of 120, the unskilled premium 1.98 → 1.11, the
    $1.26B payroll gap gone, 7.8M parked points cleared. But the suite is
    62/64 (MonetaryCheck newly red by 0.01 points) and held-25% bank failures
    went 43 → 134, both from one choice: households now get what employers
    pay, so the builders' 25% idle-pay floor reaches them. Asked: what idle
    crews get; how site work is shared (by buildings starved industry in
    the default seeds: −9% population, failures 6 → 9); and the landlords'
    fallback to a smaller building.
  - **Jerus answered, 2026-09-28:** "Lay off idle crews (Recommended)" (the
    builders keep a core crew of a quarter and lay the rest off; they are
    unemployed, draw EI, take other jobs, and are hired back when work
    returns); "By work left (Recommended)" (each site's crew in proportion to
    the work it still needs, still no parked points); "Try a smaller home
    (Recommended)" (landlords order the next smaller home type that fits in
    12 months of work). Homes on site count as the landlords' supply (told
    him; no objection). Round 1 is being revised to these and re-measured.~~
    — **shipped as 0.7.17, 2026-09-29, see `the-crew-a-building-can-use.md`.**
    "By work left" hung every site's last building (a crew shrinking
    geometrically with what it owes), so the sharing went through four
    versions: crews by full size, one crew a building, crews by size to
    Bromilow's 0.70 (V3), and Jerus's own hybrid ("every order has atleast
    one crew, and then spare crews... like a pyramid"), which cleared every
    small-building failure but gave 13 bank failures against V3's 4; by the
    rule he left as he went to sleep, V3 ships and small orders wait.
  - **Round 2 (0.7.18) built 2026-09-29** (`runs/r5-notes.md`): every planner
    runs the 80% staffing test; workers take the best-paid job they qualify
    for (Beaudry, Green & Sand 2016), a band still hiring that would out-pay
    the band above joined to it at one wage; unskilled migrants at 7.5% of
    arrivals at every band's ceiling (2021 Census, Ontario, 2016–2021 arrivals
    aged 25–54 — a Canada-wide figure would replace it). Read literally, "the
    20% it allows can't be jobs nobody can fill" shrank default cities to 14k
    (0.7.17: 163k). **Jerus, 2026-09-29: "Truly unfillable only
    (Recommended)"** — a band is "nobody can fill" only with no spare workers,
    nobody above to step down and no migrants who come for it. Being revised
    to that; EducationCheck §11's rewrite (netting the unskilled it bought,
    gross, which made it pass trivially) is being restored to full strength.
    — **shipped as 0.7.18, 2026-09-29, see
    `workers-take-the-best-paid-job.md`.** The rule never binds in play (every
    band now has migrants); EducationCheck §11 is back exactly, with unskilled
    arrivals held in its pair.
  - **Round 3 (0.7.19), prices, is next:** "Builders' prices keep up, Grant
    follows prices, Bank deposit cap rises with prices"; Luxury, "The
    customers actually served"; the branches by customers, each branch's fee
    revenue covering its own running cost; the builders' profit estimate
    with wages; and the branch's 29 posts asking the staffing test.
    - **Stopped at the branch rule, 2026-09-29** (`runs/r6-notes.md`): a
      29-staff branch costs ~$130–180k a month; at the model's $12 fee it needs
      ~11–14k households, and every city starts with one branch and 59 (0.4%
      cover). His city passes (24 branches, fees 1.41× cost). Canada has 2,743
      households a branch (StatCan 2021 / CBA 2024); TD ~16,000 clients a branch.
    - **Jerus answered, 2026-09-29:** "First branch exempt (Recommended)" — the
      founding branch is the charter; every later branch opens only while its
      customers' fees would cover it and closes when they don't (~16,000
      customers a branch); "Drop it: online banking (Recommended)" for the
      deposit cap (fall back to indexing it if it measures badly); "Businesses
      claim it back (Recommended)" — an input tax credit on buildings and
      repairs a business uses; only the city and households bear it.
    - **Built 2026-09-30; it measured badly** (`runs/r6-notes.md`, Final).
      Across 8 seeds cities ended ~30% smaller (default 166,607 → 115,002;
      schools 170,432 → 118,566), bank failures went 6 → 11 and 4 → 12, and
      the held-25% city fell 8,168 → 158. Most of it was the builders'
      sales tax that the city and the landlords couldn't claim back (−18%
      default, −32% schools on its own); the branches' upkeep and staffing
      test added the rest. In his city it did what he asked: builders went
      from −$47.0B to +$7.6B over the decade, the $73B window debt was repaid
      in month 1, every branch covered its costs, and Luxury's margin went
      2.66 → 2.26.
    - **Jerus answered, 2026-09-30:**
      - "Both rebates (Recommended)": the city gets its 100% municipal GST
        rebate (CRA, public service bodies' rebate), and landlords get the
        100% rebate on new purpose-built rental housing (CRA RC4231).
        Businesses keep their input tax credits.
      - "Keep, re-measure (Recommended)": the branch keeps its upkeep and the
        80% staffing test. Re-measure after the tax fix, and go back to him
        if the branches still cause extra bank failures.
      - "Accept the lag (Recommended)": the branch rule decides on last
        month's cost, and the next month closes a branch that no longer
        pays. No unsourced margin is added.
      - "Old saves keep theirs (Recommended)": a save keeps its grant basis;
        the basis is a lever on the Policy screen.
    - **Revised 2026-09-30** (`runs/r6-notes.md`, revision section). The
      landlords' rebate (RC4231 for 4+ unit rentals; the NRRP 36% below
      $450k) brought the population back: default 159,530 (p 0.505 against
      0.7.18's 166,607), schools 178,830. The city's rebate adds no line,
      because the city already collects the tax on its own works. Landlords
      get back about 15% of what they spend on building. But the branch rule
      still brought the extra bank failures (default 10 against 3 without
      it, p 0.033; schools 8 against 3). The cause is the first branch's
      template upkeep draining a village bank around month 120. With the
      rule, the held-25% city was 908; without it, 7,160.
    - **Jerus answered, 2026-09-30:**
      - "Exempt first branch (Recommended)": the charter branch pays its
        wages and repairs but not the template upkeep. Later branches pay
        all three. Re-measure; ship without asking again if bank failures
        return to 0.7.18's level.
      - "Leave as the law has it (Recommended)": landlords' repairs keep
        paying the tax (rent is exempt).
    — **built and gated as 0.7.19, 2026-09-30, see `the-price-keeps-up.md`;
    deployed and verified as tag 0930a, 269 files.** With the charter exempt, bank failures are back
    at 0.7.18's level (default 6 → 4, schools 4 → 4) and end populations
    within the seeds' spread (169,978 and 173,280). The held-25% city still
    collapses (8,168 → 428): it lived on the deposit cap's strain and the
    window, both removed on purpose. Whether it must live is Jerus's call.
- **AFTER 0.7.17: HEALTHCHECK SHIPS RED — ITS OWN BATCH** (Jerus,
  2026-09-25: "Ship with it red, flagged"). Read over its last 12 months (his
  change, "Read a year's average"), the dear-care twin is 8.4 points hungrier
  than the free one against a tolerance of 5, over since the bonds batch's
  round 4. Of its hungry: 25% priced out of care; 21% billed care their
  household row paid for (the documented shortcut in `HouseholdBalance`, THE
  PRICE AT THE CLINIC DOOR); 46% had money for food after their fixed bills
  and still planned less — untraced, and the first thing to trace.
  - **TRACED 2026-10-01** (while Jerus slept; nothing changed), see
    `why-the-dear-city-is-hungrier.md`. The gap went 8.4 → 77 at 0.7.17 and
    is 40.0 at 0.7.23. 0.7.17's crews by size^0.7 with the builders reading
    output after repairs leave the fixture's first depot on site when the fee
    is set; the dear twin's sickness then stalls its builders for good, so it
    never grows and its shops end banned with an empty till. The "46%" never
    planned less: the hunger line rations every plan by one city-wide share
    (`HouseholdBalance` L879). On 0.7.23 the dear city's hunger is the
    unhoused (21.0 points), families billed their row's share of care above
    their own decision (10.5) and single seniors paying care on credit until
    locked out (7.1). **For Jerus:** O1 stand one depot with the fixture
    (gap 40.0 → 1.0, premise and tolerance untouched; round 1 did this for
    eight fixtures) — recommended; O2 bill care by the heads who paid; O3 the
    clinic door without open credit (reverses his 2026-09-19 call); O4 feed
    everyone's first basket before anyone's want above it; O5 a banned firm
    can finance its stock (item below). Nothing applied.
- **UI NOTES FROM PLAYING 0.7.23, 2026-10-01** (`playing-0-7-23-ui-notes.md`,
  on his PC in his autosave city, nothing changed; then the Build mockups,
  `build-screen-mockups-round-2.md`). What 0.7.24 closed, see
  `the-build-screen-and-the-frame.md`:
  - ~~**The money barely visible:** the TREASURY tile read "TREA…", its cash
    the size of every other figure, its income cut to "+$1.5B thi…".~~ —
    **done in 0.7.24:** TREASURY is its own block by the clock, the cash at
    28 px and "+$X a month" under it.
  - ~~**The header's labels cut:** "OUT OF W…", "RATE ·…", "5,119 this m…",
    "8.9 points unde…".~~ — **done in 0.7.24:** each tile shows the longest
    wording that fits and never cuts; seen whole at 1,389 on the PC.
  - ~~**Build opens on Residential**, so a player who does not read thinks he
    must build houses.~~ — **done in 0.7.24:** Build opens on an Overview,
    and a new game or a load sets it back there.
  - ~~**The inbox popover stays open** through the main menu and across
    screens.~~ — **done in 0.7.24:** it closes on a screen change, the main
    menu and Esc.
  - ~~**The left City overview panel, the right Under construction panel and
    the NEXT DUE debt strip** sit on every screen and leave the centre about
    55% of the width.~~ — **done in 0.7.24** (frame B): a drawer behind the
    "Needs you" chip, a 44 px tab, and a card on the Finances hub with its red
    maturity as the NEEDS YOU row FALLS DUE.
  What 0.7.26 and 0.7.27 closed, see `the-land-office-redrawn.md` and
  `people-at-a-glance.md`:
  - ~~**Household cash flow and Pensions contradicting themselves in green**
    (§2.2, §2.3): "Every kind of household in this city covers its month"
    over red rows and GOING SHORT 35.2%; "They can afford to eat." under a
    shop budget of −$3,004.~~ — **done in 0.7.27:** Household money's verdict
    counts every row and says who is short, who lives on savings and who
    went hungry; the Pensions page's three lines come from the ledger and
    foot, under a red, amber or green sentence.
  - ~~**People's fractional households (5,468.87) and a pay row at the
    founding wages ($3,460)** (§2.7).~~ — **done in 0.7.27:** whole
    households, and the live wage per earner (`wagePerEarner()`).
  - ~~**§4's People row:** a ledger, the heatmap behind a button.~~ —
    **done in 0.7.27:** the drawn pyramid, the month as a waterfall, the
    household mosaic, care as rings and homes as a gauge; Household money a
    page of its own, the heatmap kept.
  - ~~**§4's Land office row:** the top sentence behind (i).~~ — **done in
    0.7.26:** the "how" sentence and every paragraph behind an (i), and the
    office redrawn round THE GROUND.
  What 0.7.28 to 0.7.30 closed, see `services-at-a-glance.md`,
  `the-road-in-one-picture.md` and `the-sectors-as-flows.md`:
  - ~~**§2.8, power units:** the grid read "Asked for 1,177,434 W" for 1.36
    million people.~~ — **done in 0.7.28:** the model's unit is the kilowatt;
    `Money.power()` writes kW, MW and GW on Services and Build (his city asks
    about 1.18 GW), and the console's report says kW.
  - ~~**§4's Services row:** label…number rows with grey paragraphs under
    them.~~ — **done in 0.7.28:** an Overview per system, care as rings, the
    sick rate as one stacked bar, capacity bars for power, water and the road,
    the schools as a pipeline; every table behind "details".
  - ~~**§4's Infrastructure row:** the road meter, then paragraphs, then dense
    mono tables (freight "25 goods × 4 columns").~~ — **done in 0.7.29:** the
    flow curve beside the three streams stacked from trips to the road,
    transit as a funnel of the three ceilings, freight as a bar a good (19
    with both prices); the grids behind "details".
  - ~~**§4's Sectors row:** a sector's page five tabs of lists, Investors as
    paragraphs.~~ — **done in 0.7.30:** inputs → plant → outputs as a flow,
    the operating rate as a ring with its cascade, the investors' decision in
    one line with an icon; the list kept as cards, in three columns with a
    running-at bar.
  What 0.7.31 closed, see `earned-surplus-banked.md`:
  - ~~**§1, two "incomes":** the tile's +$1.5B the budget's net income, while
    Finances and Government said the cash grew $2.3B, and nothing said which
    was which.~~ — **done in 0.7.31:** three figures, named everywhere: the
    header's line is what the month EARNED ("+$1.5B earned a month"), beside
    the budget's SURPLUS and what the cash BANKED; the header's (i) names all
    three, and Government's Overview walks from one to the next with every step
    named (FROM EARNED TO BANKED). D2's wording is for Jerus to confirm
    (above).
  - ~~**§2.1, GDP after a load:** "$0 / yr" on load and "annualised · first
    year" a month on.~~ — **done in 0.7.31:** the load path seeds the GDP
    history from the history file (`NationalAccounts.seedHistory()`; MODEL BUGS
    item 3).
  - ~~**§2.9, Government's "Paid out" bar amber.**~~ — **done in 0.7.31:** paid
    out in the money blue's darker step, kept or short outlined in the verdict
    colour, and no verdict colour left as a category on the tab (B4).
  - ~~**§4's Government row:** two rings, a surplus bar, the treasury bridge;
    "Keep it. Fix the amber bar, and cut the bridge's paragraphs".~~ — **done
    in 0.7.31:** the rings kept, the bar recoloured, the bridge one card of
    named steps with every paragraph behind an (i).
  What 0.7.32 to 0.7.37 closed, see `the-debt-at-a-glance.md`,
  `the-bank-at-a-glance.md`, `buttons-that-ask-to-be-pressed.md`,
  `trade-at-a-glance.md`, `policy-at-a-glance.md` and
  `city-history-finished.md`:
  - ~~**§4's Finances, Bank, Trade, Policy and History rows.**~~ — **done in
    0.7.32, 0.7.33, 0.7.35, 0.7.36 and 0.7.37**: each screen one picture first
    at Build's width, its paragraphs behind (i)s and its tables behind
    "details"; the rail's last screen redrawn in 0.7.37.
  - ~~**§2's Bank "−0.00% a year".**~~ — **done in 0.7.33** (B1: a true minus
    through `Money`'s new rate writers).
  - ~~**§2's verdict colours used as series colours (the Bank ladder,
    Trade).**~~ — **done in 0.7.33** (B2: the families teal, interest, coupons
    and reserves plain) **and 0.7.35** (D7: colours by kind, never verdict).
  - **Still open from the note's §2:** YOUR CITIES cards cut; the Taxes heading
    over one slider (Policy redrawn in 0.7.36 as dial cards under a hub: to
    look at on the PC).
- **CHECK 0.7.53 TO 0.7.54 ON THE PC** (`runs/fixH2-notes.md` §6;
  `runs/docs-0754.md` §2 and §4). **Not yet seen:** nothing rendered; the
  figures come from `ScaleCheck`'s copies at 5B and 10B (it asserts the strings)
  and from the docs pass's reading of the screens' own helpers. At 1,389 × 868.
  1. **The land premium at 5 to 10B** (fixH2 1; CONFIRM above). The copies build
     nothing, pay about 3e13 a month in property tax and lose about 1% of their
     people a month. Your call: by density, or saturated.
  2. **The arrears line** (fixH2 2; CONFIRM above). Whether what the treasury
     pays down of a sector's arrears belongs on its cash-flow statement, for a
     later batch.
  3. **AllChecks on your PC** (fixH2 3). OrderSearchCheck takes about 30 s here
     and the whole suite 408 s; check that `ScaleCheck`'s 1,500 ms median bound
     holds on your machine.
  4. **Any screen that shows a big figure** (docs flags 1 to 3). If a city of
     billions is to hand, look at People, Summary and the sector pages: a count
     past 2,147,483,647 should read whole, money past a quadrillion should read
     "$26.0Q" (grouped past a thousand), and the three known edges are Finances'
     typed ask (no Q), a sector page's "$1000.0T" and "19000.0M sq ft" (FOUND,
     below).
- **FOUND ON THE WAY IN 0.7.53 TO 0.7.54** (`runs/fixH1-notes.md` §7,
  `runs/fixH2-notes.md` §5, `runs/docs-0754.md` §4, `spec-scale.md` §9):
  - **The docs pass's flags** (`runs/docs-0754.md` §4), none changing behaviour:
    1. **Finances' typed bond amount (its typed ask) takes k, M, B or T, not Q**
       (`askFromWords`, `ASK_TYPED_INFO`). Its presets print through `Money`, so
       in a 10B city a preset reading "$26.0Q" cannot be typed back.
    2. **The sector pages print "$1000.0T"** (`Formats.amount()`): it has no
       carry like `ui/Money`'s of 0.7.40, so 999.97T prints "$1000.0T" where the
       screens print "$1.0Q", as "$1000.0B" already did below it.
    3. **The short format stops at M for land and trips**
       (`ui/Money.shortNumber()`): billions of square feet read "19000.0M sq
       ft".
    4. **`ScaleCheck.NAMED` changes nothing it scales:** `sc()` reads the names
       inside every TOP key alike (ported as `scale_save.py` had it). Its
       javadoc now says only what it lists.
  - **The scaler does not scale the bond market, the exchange, the equity
    register or the fund** (the study's ★1; fixH2 ★9). In a copy the bank is
    resolved in its first month at ×1000 and up, and the treasury journal
    carries 8.7, then about 0.1 to 0.8 a month, unnamed (read, not asserted). At
    ×1000 to ×10,000 the study's copies' currency weakened faster than at ×1 (FX
    1.43 against 0.83 after 30 months); not traced.
  - **History is never trimmed** (the study §5 and §9): about 37 MB on disk and
    110 MB of heap at 1,000 years, about 1 s per autosave, none of it depending
    on population. Jerus's call is in CONFIRM above.
  - **Arrears on the cash-flow statement** (fixH2 §5): what the treasury pays
    down reaches a sector's till but not its statement, at any size; in CONFIRM
    above.
  - **`needed` in `orderSize` saturates, not wraps,** at 2,147,483,647 at 5B
    (fixH2 §5); the order is then capped by the wait and the plots.
    `BuildingManager.weightOf` takes a long, because halving probes `needed`
    first and the saturated count plus a site's units had wrapped the int.
  - **What the docs pass found false** (prose only, `runs/docs-0754.md` §1):
    `consider()`'s javadoc ("a search from the top"; it searches below the
    counted slices) and THE LARGEST SLICE's "that test is a straight line" (two
    straight pieces at a fixed rate); TreasuryCheck's `near(..., size)` ("below
    1e6 units this is TOLERANCE exactly"; it holds to 1e10) and MoneyAudit's "a
    harness city is a few hundred thousand units" (TreasuryCheck's bus town is
    2.5 million); `Mortgage.Decision`'s "the first that failed on the way down";
    ScaleCheck's "took up to 14.8 s a month at this size" (the study's 1.1B
    city; the 10B copy, up to 143 s); HealthCheck's "productsSold is an int and
    always was" (a long since 0.7.53); `ui/Money`'s ladder, which stopped at T;
    `GameVersion`'s two entries; and six dials with blank sentences.
  - **The incidents:** none reported by H1, H2 or the docs pass; the study used
    no `/dev`.
- **CHECK 0.7.52 ON THE PC** (`runs/fixG-notes.md` §6). **Not yet seen:**
  nothing rendered; the words and figures come from the implementer's probes,
  and the fit was measured with java.awt in Plex. At 1,389 × 868.
  1. **Policy › Money** (fixG 1). THE RULE card's new ladder should sit under
     the target's, and the words line should follow as you drag it.
  2. **Your target is 0.5%** (fixG 2). Strict and Very strict both aim at 0%
     there, and Loose and Very loose act only past 1.5% or 2.5% (or under −0.5%
     or −1.5%). Decide whether that is what you want.
  3. **Your current autosave** (fixG 3) still loads at 1851 with the gap at
     1851. The next autosave, 12 months after a load, will hold a whole month.
  4. **On a loose setting** (fixG 4), NEEDS YOU's PRICES row reads "the dial X
     pts under the Standard rule" while trust falls. Check that it reads
     clearly.
- **CHECK 0.7.50 TO 0.7.51 ON THE PC** (`chart-crash-and-build-advice.md` §8;
  the full lists are `runs/fixE-notes.md` and `runs/fixF-notes.md` §8). **Not
  yet seen:** nothing rendered; every word and figure was composed by the
  implementers' probes on the playtest's 600- and 2,400-month cities and a copy
  of Jerus's own city, since JavaFX nodes cannot be built headless here (the
  strings were rebuilt from the screens' own helpers), so his figures will
  differ. At 1,389 × 868.
  1. **Try to reproduce the crash** (fixE 1). Open Government's output page
     (WHAT THE CITY MAKES, the layers chart) and run time at 20x or 50x. Rest
     the pointer on the chart, move it a little and leave it there while months
     land, then switch tab with the pointer still on it. Expect no freeze and no
     "!!!" in `log.txt`.
  2. **The same on City History** (fixE 2), with real GDP pinned and layers on,
     and once in full screen with Esc while time runs. Back on History, no
     crosshair or card should be left where the pointer was.
  3. **Hover any chart** (fixE 3: Bank, Finances, Trade, Fund) while months
     land: the card and crosshair should still follow the pointer.
  4. **A long run whose log reaches 2,000,000 bytes** (fixE 4): the cap line
     should say failures still go in, with any later failure after it.
  5. **Build › Overview at 1,389 × 868** (fixF 1). The cards are taller now
     (three lines, up to about 8 rows of text). Check the row still reads well,
     and hover the land line to see the ranking tooltip.
  6. **A city short of land** (fixF 2). The button should read HELD with "it
     stops at the first: short of land - buy it at the land office first". Buy
     the land and press it: the whole run should place, or go to funding when it
     says "on credit / short $X".
  7. **The two land figures** (fixF 3), when an earlier card takes the ground:
     the card's button says "Short 543k" while its line says "798,000 sq ft more
     than is free". Decide which the card should say.
  8. **Big Home Daycare counts** (fixF 4; 133 in city600): they open fast but
     push your other sites back (CONFIRM, above). Your call whether that is
     fine.
  9. **Services › Education** (fixF 5): a school not built reads "not built: N
     would come, M posts for them" on two lines in its node; check it.
- **FOUND ON THE WAY IN 0.7.50 TO 0.7.51** (`runs/fixE-notes.md` §7,
  `runs/fixF-notes.md` §2 and §7, `runs/spec-build-advice.md` §7,
  `runs/docs-0751.md` §4):
  - ~~**The autosave is written mid-month** (fixE). `Game.nextMonth` autosaves
    (:7553) after `month++` and the month's opening steps, but before
    `recordMonth`. A city loaded from it skips a month of history: Jerus's
    autosave holds history to 1850 at game month 1851, and a month after loading
    records 1852. Not touched (model).~~ — **fixed in 0.7.52** (2026-10-06,
    `runs/fixG-notes.md` §1): the autosave now sits after `recordMonth()`, so
    it holds a whole month. Old autosaves are not repaired (CONFIRM from
    0.7.52); Jerus's still loads at 1851 with its gap.
  - **Each card's "Opens" is quoted alone** (fixF §7): it does not count the
    cards before it, nor the crew weight they will put on the builders (the Home
    Daycare finding, CONFIRM above).
  - **The later card's credit tooltip** (docs flag 2). A card's button on credit
    says "short $(quote − all the cash)", recomputed on screen, while line (1)
    says "by $X", the shortfall after the cards before. For a later card whose
    own quote fits the cash it reads $0 or less, and the button says "on credit"
    though its own press borrows nothing. The expression is 0.7.49's; 0.7.51's
    lifted cap reaches it more often.
  - **The "cheapest" qualifier's extra words** (docs flag 3). It is on every
    card its rank gives it, true but redundant where no cheaper building sits
    lower (Jerus's roads: "...that keeps it ahead and fits the land left"). The
    card carries only its runner-up; a model field for the cheapest of all would
    let the screen drop it. Your call.
  - **The docs pass's other flags** (`runs/docs-0751.md` §4): `&mp;` is not
    broken, it is the HTML entity for ∓ ("bid/ask = mid ∓ half of a 2% spread"),
    and version 12's publish step had turned it into "mid & half" (0.7.49's
    FOUND, and its write-up, called it an invalid entity); it was restored in
    version 13 and the fix should be dropped next edition. GameLog counts the
    cap per stream (out and err each to 2,000,000 before failures only), so the
    file can near twice the cap, as at 0.7.49. The manual's §25 still counts
    0.7.49's 59 open questions.
  - **`suggest()` is slower on a cold first call** (fixF §7): 18-21 ms against
    9-10 ms at 0.7.50.
  - **What the docs pass found false** (prose only, `runs/docs-0751.md` §1): the
    advice's RULE said the ground was "priced at the land office's" (free ground
    is the lesser of that and what a business pays);
    `BuildScreen.adviceTotal()`'s javadoc had its two figures the wrong way
    round (the cards' quotes added were $85.5M and the run charged $98.4M at
    playtest month 24); `GameLog.MAX_BYTES` said "the file is rotated mid-run"
    (false at 0.7.49 too); `GameVersion`'s 0.7.51 entry covered "a college or
    university row" where it is every school above the ladder.
  - **Fixtures where the spec's premise did not hold** (fixF §2; all in
    BuildAdviceCheck): §7's ×10 does not flip the short city's roads (×1000 does
    not either; the flip is asserted with no ground free, at ×486.6); §9's short
    city had 899 would come, not past 1,000 (its fees paid, 1,171); §11 leaves
    ground for the first card only.
  - **The design study's risks, as built** (`runs/spec-build-advice.md` §7):
    bigger orders and more cards on credit or short of land; the run stops at
    card 1 for land more often; graduate posts are endogenous, so a city that
    never gets them is never advised a university; the office price is the best
    plot's, so a large purchase walks up the listing (about +0.8% a block on the
    premium); the projection is inert in a city above its homes; two model files
    were touched, bit for bit.
  - **The `/dev` rule was broken again, without harm:** E redirected to
    `/dev/stdout`, the design study and F each to `/dev/null`, and the docs pass
    listed `/dev/null` as its gate asks; `/dev/null` stayed the character
    device. No `rm`. E left an empty `sec8.java` in its scratch.
- **CHECK 0.7.46 TO 0.7.49 ON THE PC** (`model-fixes-fund-and-transit.md` §10;
  the full lists are `runs/fixA-notes.md`, `fixB-notes.md`, `fixC-notes.md` and
  `fixD-notes.md` §3; the docs pass added none). **Not yet seen:** nothing
  rendered; every word and figure was composed by the implementers' probes on
  the playtest's 600- and 2,400-month cities, since JavaFX nodes cannot be built
  headless here (the strings were rebuilt from the screens' own helpers), so his
  figures will differ. At 1,389 × 868.
  1. **A 0.7.45 save, no press (A1, A5):** Trade still says "not counted yet";
     Sectors › Construction › Materials reads "Last month, as on its statement",
     Bought and Imported "not counted yet", and the muted "Since then, so far".
  2. **Press, save to slot 10, load, don't press (A1–A5):** Trade reads the
     month; care's fees by kind add up to the treatment line; the Food tab's
     eligible count matches the count before the save; Construction shows the
     struck pair. Press once more: the Freight figures match the city that never
     reloaded.
  3. **The Bank tab in city2400 (A7):** two months after loading, the capital
     card reads THE LEVERAGE RATIO and the chip has no "(not yet recorded over
     time)"; check how its description wraps at 1,389; City History offers "Bank
     leverage ratio" and "Bank leverage target".
  4. **City History's episodes and the year book (A6, A8):** an old city's
     lifelong epidemic is gone; epidemics are named only from 0.7.46 months, and
     only for an outbreak of 3 months or more (read the (i) words); the year
     book export has `bankLeverageRatio`, `bankLeverageTarget` and `outbreak`,
     and Graduates are gains.
  5. **Policy › Money and Sectors › Retail (B1, B2):** the households' spend
     factor previewed at the dial in force sits a hair from the page's real
     rate, not about 1.6 points off; THE SHELF's floor line "struck at ×1.001"
     matches Policy › Money's struck level.
  6. **Sectors › Automotive and Real Estate (B3, B4), city2400-like:** the three
     lines and the note, with machinery mostly imported and no WARN; the hold
     reads "(64,210 now, 320 coming)" there and in the investment log.
  7. **Policy › Taxes › Property and Infrastructure › Transit's ladder (B5,
     B6):** the (i) says wage, profit and sales offsets ±30 and property ±10,
     and an older save with a property offset past ±10 loads at ±10 at the same
     rates; the fare ladder still runs to $50 a ride, and in a reformed save to
     the cap in the new money.
  8. **Government and the floor (B7, B8):** TAX TAKE's "of annual GDP, the last
     twelve months", the balance block's "a year's surplus, N%", the AGAINST THE
     ECONOMY bars and tooltips, the lists' "of GDP" column and (i) (under 12
     recorded months: the old month × 12 words); in an old city whose cost of
     living has risen, Policy's pinned bands and the WAGES flag now show, and
     departures come.
  9. **Finances › The city's fund › Rules & cash (C1, C2):** THE WITHDRAWAL
     comes first, full width: "0.25% a month", "3% a year - Norway's rule…";
     drag the ladder from 0 to 10%: the four effects follow, "…sold from its
     market book" replacing "…not paid" above 0.25%, and Apply reads "Withdraw
     0.50% a month" or "Withdraw nothing"; check the card at 1,389 px and
     History's fund flag; at 0, "nothing to the treasury" and the budget's
     Transfer from the fund is 0; a 0.7.47 save loads at 0.25%.
  10. **A fund at 10% for a few months, city2400-like (C1):** TO THE TREASURY
      shows "…sold for, to pay next month" and "…and the month before's, paid
      from what it sold"; the Finances tile reads "selling D$X to pay its
      withdrawal"; Government's Transfer line adds up to its row.
  11. **The rule and the mark (C3–C5):** THE RULE card's sentence; in city2400
      the positions Heavy Industry, Mining, Materials and Business Services read
      "marked at fair value: last traded …" (Mining D$0.59 a share, not D$1,644)
      and a security page's price note says so; a consolidated company's History
      price line is a figure from 0.7.48's months on, not 0.
  12. **Transit, People and Trade (D2–D5), city2400-like:** Infrastructure ›
      Transit shows "Riders by reason" and its (i) under the ceilings: "No car
      of their own: 74,948 (73%) · in reach 48,716 · riding 48,716", "walking:
      26,232", "With a car: 27,557 · in reach 17,912 · chose the bus 8,059",
      "Drive (chose the car): 19,498" and "Riders: 56,775", the long names
      wrapping at 230 px, the fold with the same rows; THE FARE reads "$100 a
      month for 40 journeys · set at $2.50 at founding prices, ×1.001 the prices
      people expect" and "a drive's fuel: $1.42 a journey at today's exchange
      rate · a month's pass is 1.4% of an unskilled household's take-home"
      (27.6% at $50), the ladder bottoming out at the car-less (48,716 at $50)
      with fares up to D$97.6M; Government lists "Transit fares" and "Transit",
      the two "outside the total" lines are gone and EARNED's walk has no fares
      step; People's waterfall has a "Fuel" step, its statement a fuel line and
      an opened cell its fuel; Trade lists "Households' fuel".
- **FOUND ON THE WAY IN 0.7.46 TO 0.7.49** (`runs/spec-model-fixes.md`,
  `runs/spec-transit.md` §7, `runs/fixA-notes.md` to `runs/fixD-notes.md` §9,
  `runs/docs-0749.md` §4, `runs/manual-a.md`, `runs/manual-b.md`):
  - **Closed from the earlier lists, left unstruck there:** `afterFixed` is not
    saved (A4) and a ×0.001 reform misleads the means test and ridership (B6),
    both in FOUND ON THE WAY IN 0.7.42 TO 0.7.45, and that list's docs-pass
    flags 1 (`PolicyPreview.realDepositRateAt()`, B1) and 2 (THE SHELF's level,
    B2). Still open there: `Retail.estimatedMonthlyProfit()` counting no wages,
    the grocers at their floor and the shelf's catch-up (the triage's Q1 and
    Q2).
  - **The UI probe caught a crash a batch introduced** (0.7.48): C3's
    `capRule()` put "1%, a bond" into a `String.format` pattern and Rules & cash
    threw `FormatFlagsConversionMismatchException`; fixed (%s), rebuilt and
    re-gated. No harness builds that page, and JavaFX nodes cannot be built
    headless here ("no suitable pipeline"), so screens are checked through
    probes that rebuild their strings (0 flags on both cities and ×0.001).
  - **The ★2 residual** (A1): 12 of 229 series still differ a month after a
    reload, by at most 8.3e-5 (sector net incomes ±D$0.4k, `bankDeposits`
    +D$64.6k, cash 5.6e-9). The bank's deposit snapshot and the households'
    month rate are ruled out; the sectors' energy ratio (.80360 against .80319)
    and Retail's purchase budget are candidates. City600 had an outbreak running
    at the load (.0436), which History cannot name, by design.
  - **A5's struck month is partly drawn in the same press:** city2400's planner
    orders at the top of the month, so it struck 539 units where the row the
    press before showed none; statement and pair agree. The triage's "about 476
    units" for D$6.20M (at D$13.03) is really 539 units at about D$11.5.
  - **BondCheck's "save from before 0.7.12" cuts one slot,** stale the same way
    HoldersCheck's cut was (§7, fixed in A), but it still passes, since its
    young city holds no bonds. Left alone.
  - **The fund batch's measurements moved with B8:** the playtest's fund first
    opens at m1881, not m500, so the triage's C3 (m501) and C4 (m505) first
    differences were taken on `pt0744`'s fund.
  - **The fund's two halves value a stale share differently** (0.7.48):
    `Exchange.postFund()`'s 70/30 mix reads `price()`, while `BondMarket`'s
    `fundSharesValue` reads `cityMarketValue` (the mark) since C4; the triage
    kept `price()` for trading, so it is left.
  - **G lags every service's cost by a month** (0.7.49): it is struck at the top
    of the month (`startOfMonthUpdate`) from the costs 6d struck the month
    before, so GdpCheck's fixture reads the costs a month earlier.
    `getTreasuryUnexplained()` includes the journal (a bus town's repairs);
    `getTreasuryResidual()` is what is left after it, and TreasuryCheck's bus
    town reads the residual.
  - **Trade's "+N more goods" door counts the hidden "Households' fuel" row as a
    good** and names only the railway's (`TradeScreen` ~916; the docs pass's
    flag 1). `GovernmentScreen.journalIcon()` and `journalDoor()`'s "transit"
    branches are dead now that no journal line names transit, left in place.
    People's statement column writes outflows with tightMoney's "-$", the house
    style.
  - **The design study's risks, as built** (`runs/spec-transit.md` §7): the
    young playtest grows slower (population at m1000 15,740 → 11,553, takeoff
    m1435 → m1472) and catches up by m2000; the 16-seed AUTO2 takeoffs are 16/16
    in both 0.7.48 and 0.7.49 (median m1,448 → m1,458); the playtest is chaotic,
    one change moving m4000 by up to a quarter; a Bus Network has one post per
    ten seats and fares covered 8–24% of the bill in the study's playtest, so
    transit is cheap to build and dear to run (balance it with the template's
    staff or the fare, not the model); `transitChosen()` reads two money
    figures, the family of WHOLE CARS (round `s` and `p` to a millionth if a
    twin ever parts); the prototype's last-millennium hunger read 0.40 against
    0.23 in the reference, within its variants' spread (0.16 to 0.50), to watch.
  - **The `/dev` rule was broken again, without harm:** batch A redirected to
    `/dev/stdout`, the transit study to `/dev/stderr` and read `/dev/null`, the
    docs pass wrote one `2>/dev/null`; `/dev/null` stayed the character device.
    Batch D left a stray `/tmp/x`. `javac` writes classes for the files that
    compiled even when another file fails: one partial build ran stale harnesses
    until the build output was read (fixA).
  - **The manual** (`runs/manual-a.md`, `runs/manual-b.md`): §10 carries `&mp;`
    ("mid &mp; half of a 2% spread"), an invalid entity older than both writers,
    left alone; it carries the build's transit figures (56,775 riders, 48,716 at
    $50, D$97.6M), not the design study's (56,223, 48,650, D$101M) the docs pass
    quoted; §25's 59 questions and NEEDS YOU's "twenty-two" follow the docs
    passes' running counts, not a line-by-line count; HealthCheck is still red
    (10.1 points against 5).
- **CHECK 0.7.42 TO 0.7.45 ON THE PC** (`inflation-that-answers-to-money.md` §8;
  the full list is `runs/ui26-notes.md` §3, with the spec's probe cases in
  `runs/spec-ui-0745.md` §6; 0.7.42 to 0.7.44 had no list of their own, being
  model changes whose effects show on these screens). **Not yet seen:** nothing
  rendered; every word and figure was composed by the implementers' probes on
  the playtest's 600- and 2,400-month cities, which are 0.7.41 saves and load in
  the old-save state, so his figures will differ. At 1,389 × 868.
  1. **A pre-0.7.45 city, no press:** the INFLATION line reads "expect x% ·
     trust 80%" in grey, and shortens to "exp x% · 80%" without being cut; on
     Sectors › Retail, HANDED OVER and THE SHELF say "not counted yet".
  2. **Press a month, then Policy › Money › The policy rate:** the rate line,
     then THE ANCHOR (2/3) beside THE CURRENCY'S DRIFT (1/3), then the dials;
     the gauge's 25%/95% labels and chips and the rate line's stacked names
     don't overlap.
  3. **Hold the dial low with inflation over the target:** the PRICES row and
     the header line turn amber, the hub flags "Trust is falling", and the row
     opens the rate page; put the dial at the rule and it reads "leaning against
     it" and the row clears. Red needs trust under half, which the implementer's
     125-month hold never reached (.76), so red may not show.
  4. **PRICES:** "a basket of m…", the five-part bar with its keys, luxury
     "capped at 15%".
  5. **Promises:** all 6 tabs fit, Food third; set Food to 50% and read the
     effects, THE BUDGET and WHO WOULD GET IT; apply, then press: the effects at
     rest equal what was paid, Government's Food assistance "▸ who" adds up to
     the line, People's month shows Food vouchers a month behind, and the hub's
     slice has its own colour.
  6. **Sectors › Retail › Operations:** THE SHELF's two columns; past the cap
     the "clears at $X ›" tag and the Roads pill; when slack, "clears on its
     floor"; WHAT IT CHARGES on Restaurants and Luxury Retail.
  7. **People › Household money:** WHO GOES SHORT, AND WHY, and the words and
     (i) in GOING SHORT.
  8. **Trade › The currency:** 3 forces plus next month, and the UIP and drift
     lines on the rate card.
  9. **The fare and Build:** the fare reads "$2.55 a ride" once the price level
     is past 1, with the ladder in today's money and the (i) showing founding ×
     level; Build's Commercial Bank paid-in and the money bar's (i).
  10. **City History:** the new traces and 3 presets; with an index line drawn,
     dashed "basket" hairlines with a hover card, on the big chart only; old
     cities have the series from 0.7.45 on.
  11. **The year book export** has the new columns.
  12. **An old save across the links, the first month after the load:** the
     year's rate steps (city600 in the probe: 0.59% to 2.16%, the index +1.6%),
     and in city2400 (index 1.11) the FIXED grant, the new-home rebates and the
     account fee fall about 10% as Pe is seeded at 1; say whether it looks
     wrong.
  13. **A mature city on the autopilot:** inflation near 2% a year (the
     ensemble's median from month 240 is 2.28%) with trust rising toward its 95%
     ceiling; then the dial held low or high, to see the PRICES row and the
     shelf.
- **FOUND ON THE WAY IN 0.7.42 TO 0.7.45** (`runs/ui23-notes.md` §9,
  `runs/ui24-notes.md` §10, `runs/diag-0743.md` §5 and §7, `runs/ui25-notes.md`
  §9, `runs/ui26-notes.md` §9, `runs/spec-ui-0745.md` §5,
  `runs/ui26-docs-pass.md` §4), the closed ones struck:
  - ~~**MonetaryCheck's timing noise**~~ (0.7.42's ★6): 0 / 0.024 / 0.074 /
    0.165 points against the 0.100 allowance on 0.7.42's model — **done in
    0.7.43**: 0 / .010 / .020 / .056 on its model, the allowance untouched.
  - **`AllChecks -q` hides a second failing assertion inside a known-red
    harness** (0.7.42): HealthCheck's upkeep check went red under the anchor and
    showed only as "HealthCheck FAILED"; a per-label baseline for known reds
    would catch it.
  - **Old saves lose about 10% on the FIXED grant, the new-home rebates and the
    account fee at first load** (0.7.42; city2400, index 1.11): they move from
    the index to Pe = 1, as the spec's design said (§2.3, Pe seeded 1.0).
  - ~~**`InfrastructureManager.faresAt(fare)` and the Transit page priced a ride
    at the dial, not at `chargedFare()`**~~ (0.7.42) — **done in 0.7.45** (B7).
  - **The FoodProcessing fixture city stagnates near 5k people with 12-35%
    unemployment for two centuries** (0.7.42; 0.7.41 wanted its plant at 3.3k,
    m171; the prototype slows there too): a young-city episode to watch, with
    A2's spells.
  - ~~**Hunger up about 7 points under the anchor**~~ (0.7.42: AUTO2 .239 to
    .312; the default playtest's checkpoints read 23-43%) — **done in 0.7.43**:
    AUTO2 .312 to .117 on the new definition (baskets), .033 at 0.7.44.
  - ~~**Total stock-outs, 0.7.43's new failure mode**~~ (11.9% of AUTO2 months
    after m240 under 5% delivered; seed 1: 35%, hunger .8 for years; the till
    empties, a loan refills the shelf) — **done in 0.7.44**: 0.1% of months, one
    seed (9) at 2%, its shops sold down to no coverage.
  - **The grocers at their floor lose money** (0.7.44's §9, item 1; CONFIRM,
    above): seed 12 m490-520 net -0.5 to -1.1M a month, seed 9 m700 -2.1M; they
    sell shops for losses mid-boom, which is where A2's 13 and the last 0.1% of
    stock-outs come from.
  - **`Retail.estimatedMonthlyProfit()` counts no wages** (gross margin on food
    only, since 0.7.42): the grocers' investment test; see CONFIRM, the grocers
    at their floor.
  - **The default playtest's 76-month hot spell at 0.7.43** (m639-714) was the
    occasional player holding 1.5% through a 100-month skip during the takeoff,
    the S1 mechanism; it is 30 months at 0.7.44.
  - ~~**F1: households with no home in 10/16 seeds at 0.7.43**~~ (m89-94, one
    month each) — **done in 0.7.44**: the landlords' lender tested the nominal
    rate for 26-31 months while the rule sat at 8-27%.
  - **The shelf's floor catch-up hides shortages** (0.7.44's §9, item 2): under
    its floor the shelf takes FLOOR_CATCH_UP's branch, which never reads the
    clearing price, and a floor drifting at expected inflation holds it there.
    Of AUTO2's months with the clearing price over 1.2× the floor, 41% had the
    shelf within 1% of it (0.7.43: 47%); seed 9 m728-800 had the price at 2-55×
    the floor with the shelf on it. Pricing them with a sticky step from below
    too is spec §2.7's rule and unmeasured: Jerus's call.
  - **MoneyAudit's detail prints two decimals** (a declared line reads back to
    .005); **AllChecks' map is soft-old for Stale** (0.7.44).
  - **`afterFixed` is not saved; a reload recomputes it** (0.7.45's §9; also in
    0.7.44): between presses the Food tab's eligible count can move (city600
    m601 reads 766, and 513 after a reload) and "on the shelf" too (7,160
    against 7,146); the next month equals an unsaved twin.
  - **A ×0.001 reform** (city2400, the same on 0.7.44): the next month every
    working row's `afterFixed` goes negative, so 49,133 households would qualify
    instead of 12,294; ridership reads the reformed fare against
    MAX_TRANSIT_FARE in founding money, so riders jump about 5%; the transit
    fares flow isn't rescaled until the month runs.
  - **The spec's red state did not occur** (0.7.45's §9): on 0.7.45 trust
    bottomed at .76 over 125 held months, and red was caused by setting trust to
    .504 (CONFIRM, credibility's speeds).
  - **B17, hunger is a month behind the sale** (labelled in an (i), not
    re-struck): `getHungerRate()` is struck at the top of month t from sale t-1;
    on an old save's first month city2400 reads 0.04% hungry while every row got
    .49 of a basket, and People shows "handed over 3%" while WHO GOES WITHOUT
    shows no shortage of stock.
  - **B18, an old save's first month steps the year's rate** (city600: 0.59% to
    2.16% on the shelf's first sticky month, the index +1.6%): the spec's
    "old-save step", true, so the screen shows it.
  - ~~**B16, the food assistance means test flickered**~~ (it read one month's
    lumpy investment income; city2400 at 50%, the unemployed row 11,203-14,172
    eligible at m2403-2416, none at m2417-2423) — **done in 0.7.45** (CONFIRM,
    B16's smoothing).
  - ~~**Bank's javadoc said "PAID_IN_PER_BRANCH, reformed"**~~ (0.7.45's §9) —
    **done in the docs pass**: the paid-in is struck at Pe.
  - **The docs pass's flags** (`runs/ui26-docs-pass.md` §4; no behaviour
    changed), one each:
    - **1:** `PolicyPreview.realDepositRateAt()` still subtracts the YEAR's
      inflation, so Money's "Households spend, of what they would at zero"
      differs at rest from `game.spendFactor()` by (inflation - expected).
    - **2:** THE SHELF's floor line prints `Retail.getExpectedLevel()` as
      "struck at ×N this month", which between presses is next month's, not
      `getStruckLevel()` (one getter in `SectorScreen.shelfWords()`).
    - **3:** `LongPlaytest`'s `-Dplaytest.wages` copy is the pre-0.7.42 chase
      alone, so with the flag on it parts from costOfLiving by design (comment
      fixed; LabourCheck's LagWatch has the new recurrence).
    - **4:** People's open-cell shopping block compares want with planned
      (money) while eating is baskets since 0.7.43: relabelled, it probably
      wants `groceriesGot` and `groceriesNeed`.
    - **5:** `LongPlaytest`'s report "scarcity mark-up" prints the target
      (left).
    - **6:** `BalanceSheet`'s header still calls land and bonds payable zero
      placeholders (pre-existing, not this batch).
- **CHECK 0.7.40 AND 0.7.41 ON THE PC** (`fixes-from-playing-0-7-39.md`; the
  full lists are `runs/ui21-notes.md` §3 and `runs/ui22-notes.md` §4, the docs
  pass's flags `runs/ui21-docs-pass.md`). **Not yet seen:** nothing rendered;
  every word and figure was composed by the implementers' probes on the
  playtest's 600- and 2,400-month cities, so his figures will differ (the
  600-month city's power reads 233% served). At 1,389 × 868, in his autosave
  (Continue). This supersedes the lines on Build's credit page in CHECK 0.7.34
  TO 0.7.39, below: that page is now Build › Funding (item 5).
  1. **City History, the clock at 20x, hands off:** the big chart and both pins
     stay still, the page's right edge never moves, no horizontal scroll bar.
     Resize the window or open the drawer: the charts follow once and stop (the
     big chart ends 2 px short of the page edge, the pins 2 px inside their
     cards). Full screen (P): the chart fills the window and does not grow; with
     many lines picked (a wrapped legend) the overview strip may be cut off at
     the bottom (known: FOUND ON THE WAY, below).
  2. **Bank, Finances, Trade, Government, Policy and the fund's charts:** each
     as in 0.7.39, nothing cut at its right edge (a chart may now shrink and be
     cut rather than push, but only on a row too narrow for it).
  3. **Build › any city category, two or three cards dialled up:** the order
     bar's stacked bar stays still with the clock running, its track the control
     grey with 1 px between parts; its key reads "served" for a served measure
     and "covered" for death care, police and cells.
  4. **At 20x:** click Build's +, a page chip, a Finances chip, a slider: each
     acts on the first click. Drag a Policy slider: it is not rebuilt under the
     hand, the page catching up on release.
  5. **Build › Overview, "Build all three" with too little cash:** Build ›
     Funding opens (the rail's Build lit): the run's names and price, a "short
     by" chip, the cash-against-gap bar, the 20-year bond and the 6-month note
     as cards side by side, each button "Build 3 orders · $X"; take either: all
     three are built, back on the Overview. The same from a category's order bar
     and with Enter; a single card reads "Build 5 · $X"; Cancel (or "‹ page")
     returns with the counts still on the cards. A run whose second order lacks
     ground: "Then 2 × Paved Road cannot go ahead - not enough ground is free
     for it - so the run stops there: this borrows only for the order before
     it."; take it: the first is built, then NOT ENOUGH LAND, the unbuilt orders
     still on their cards. The land office's credit page and Build's look alike.
  6. **Finances › Borrow:** HOW MUCH reads "D$2,500,000,000,000 · D$2.5T" once
     asked; type "40B" and Enter, "2.5t" and click elsewhere; "4o0B" shows the
     red line and the ask stays; ÷10 and ×10; the steps relabel with the ask (at
     D$3.4B: −D$1.0B −D$100.0M +D$100.0M +D$1.0B); the presets, only those that
     are something; clear. Type while the clock runs at 20x: the box keeps its
     focus and every key. Abroad the same in US$, "the minimum" the minimum in
     dollars, and the note "nothing under" prints the dollar minimum too.
  7. **Drawer › RESOURCES, HEALTH, SCHOOLS:** "Energy N% served", "Water N%
     served", "Roads N% served · N% flow", each green, amber or red; the header
     "all clear" or "1 short · 1 tight" in the worst colour; nothing ends in
     "…". HEALTH "General care N% served  6.2k/6.0k", plain when enough, amber
     or red when short; SCHOOLS "N% served".
  8. **NEEDS YOU:** "POWER N% served", "ROADS 62% served · 56% flow", "GENERAL
     CARE N% served", "SCHOOLS elementary N% served"; with nothing listed, NEXT
     TO WATCH is amber if a care is at 90%.
  9. **Build's rings, Overview and suggestions:** a category with nothing listed
     but a care under 100% shows that ring amber, "general care served, short",
     with no ✓ (a ✓ only when everything is enough); the Utilities, Roads,
     Healthcare and Education rings show the served figure (power can read
     233%), full when enough, "served, tight · 1.2 MW spare"; Transit blue,
     "served · room for…"; a ring's order bar "served 92% now · … · 140% with
     these", green only when enough; a suggestion's chip "Power 80% served",
     "80% → 134%", a care order stopping at 81% amber.
  10. **Services:** Utilities (POWER, WATER and ROADS served with words and no
     sparkline; the cards "233% served, enough"; the road card "62% served,
     short · 56% flow"); Health (LEAST SERVED; the care rings coloured by their
     own figure, "served, short · …"); Education (BASIC LADDER "served, short ·
     held up by …"; the pipeline's higher schools red, amber, green or blue);
     the strip's Health dot amber when a care is under 100% though not listed.
  11. **Infrastructure:** Roads, the SERVED cell "62%" ("served, short: its
     capacity over the trips on it"), the hero "62% served, short → 56% flow",
     the curve flat at 35% on the left, rising to 100% at 111%, flat after it,
     red, amber and green bands from the left, a road over 200% at the right
     edge with "›"; Transit, "Build's transit ring reads 61% served: room on the
     stock for 61% of commuters. 41% ride."
  12. **Anywhere:** nothing ends in "…" at 1,389 × 868, and no "% full", "%
     used" or "covered" stands beside a gauge that is now served (Health's (i)s
     and notes are the known exception: FOUND ON THE WAY, below).
- **FOUND ON THE WAY IN 0.7.40 AND 0.7.41** (`runs/ui21-notes.md` §8,
  `runs/ui22-notes.md` §9, `runs/ui21-docs-pass.md` flags 4 to 9), none changed
  but the two the orchestrator closed:
  - **A full-screen chart with a wrapped legend is a row too tall.**
    `TimeChart.heightBesidePlot()` assumes a one-row toolbar; with the legend
    wrapped to two rows the full-screen chart is a row taller than the window
    gives it and its foot is cut (no longer a loop).
  - **The order bar's and "Build all three"'s figure is not the run's invoice.**
    `BuildAdvice.quoteTotal()` counts the yard free for every order; the run's
    invoice is the model's own (CONFIRM, 0.7.40's ★3).
  - ~~**Finances' "nothing under D$X" under a dollar ask printed the local
    minimum**~~ (the docs pass's flag 5), next to "the minimum · US$Y" — **done
    after the docs pass**: it prints the dollar minimum.
  - ~~**The order bar's key read "served" for every measure**~~ (the docs pass's
    flag 9; 0.7.41's §9, item 3: the key was 0.7.40's "covered") — **done**:
    "covered" became "served" after the merge, and after the docs pass "served"
    only for a served measure, "covered" for death care, police and cells, whose
    first segment is `cover()`.
  - **A tile and the page it opens can disagree** (the docs pass's flag 4).
    Build's tile shows an unlisted row that is short or tight, such as water at
    120% or childcare at 90%; a click opens the category on `worstMeasure()`,
    which reads listed rows only, so it opens on power or general care instead.
  - **A higher school's NEEDS YOU row has no % served** (0.7.41's §9, item 4;
    the docs pass's flag 6): it still reads counts, "0 seats, 653 would come".
  - **Before a month runs after a load, Services' care rings and NEEDS YOU's
    care rows can differ widely** (0.7.41's §9, item 5): the month's coverage
    against staffed beds now, 17% against 90% in the fixture. They are coloured
    separately now (CONFIRM, the care's two figures).
  - **Two strings are true but loose** (the docs pass's flag 7): the quote
    line's and a suggestion's "short $X — you will be offered a bill" (the page
    offers the 20-year bond first, then the note); Health's (i) and notes still
    say "covered" beside served gauges: SICK_INFO, RECOVERY_INFO, General care's
    statement note, the living-care scale notes and People's "At this cover".
  - **Neither research city shows the Healthcare tile's ✓** (0.7.41's §9, item
    6): neither has a basic school (elementary 0%) and both list burial plots;
    BuildCardCheck's fixture holds the tick.
  - **`BuildAdvice.Measure.isLoad()` has no caller** (already true at 0.7.39).
  - **`BuildScreen.figureText()`'s SCHOOL branch and default are unreachable**
    now: only measures that are not served reach it.
  - **`CityNeeds.network()`'s `ratio` parameter is unread** (its javadoc says
    so).
  - **`Pieces.tidyMoney()` is a no-op** after Money's fix, left in place.
  - **`Game.hasDepositFor`'s run count matters only once there is a second
    MINING template** (there is one, Iron Mine).
  - **What the run's invoice adds:** in the 600-month city a run costs $190k
    more than its orders alone (6 units in the yard); the 2,400-month city's
    yard is empty, so the two agree.
- **CHECK 0.7.34 TO 0.7.39 ON THE PC** (`buttons-that-ask-to-be-pressed.md`,
  `runs/ui15-notes.md` §3; `trade-at-a-glance.md`, `runs/ui16-notes.md` §3;
  `policy-at-a-glance.md`, `runs/ui17-notes.md` §3; `city-history-finished.md`,
  `runs/ui18-notes.md` §3; `the-loose-ends.md`, `runs/ui19-notes.md` §3;
  `the-fund-as-a-brokerage.md`, `runs/ui20-notes.md` §3; the docs passes'
  corrections, `runs/ui15-docs-pass.md` to `runs/ui18-docs-pass.md`; 0.7.39's
  `runs/ui20-docs-pass.md` flag 12 and its fixes' `runs/ui20b-notes.md` §3).
  **Seen, on the evening of 2026-10-01:** Jerus played 0.7.31 on his PC
  himself and wrote "ok i
  checked it and so far im loving it, just tiny thing, everywhere you have
  build, like the build button, it should be more intuitive aka like an actual
  button that is basically asking to be pressed, cause currently its a tiny
  text"; that play saw 0.7.24's fix round to 0.7.31. He allowed one look by
  remote control ("you can open and check out the ui, but please just once")
  and went to sleep. The orchestrator deployed 0.7.32 and 0.7.33 first (tag
  1001n: 25 source files, verified byte for byte), backed up his autosave and
  settings (`restore-1001n`), opened 0.7.33 on his PC once, looked at Build,
  the Land office, People, Services, Infrastructure, Sectors, Government,
  Finances and the Bank, quit, and restored the saves (slots 1–9 untouched).
  Everything drew cleanly. It found three things, which became 0.7.34: the tiny
  Build button; Finances' "later" column squashing the twelve years; Sectors
  after a load saying "no word yet" on 14 of 15 cards. Screenshots:
  `runs/0733-look/` (bank-overview, build-healthcare-cards, build-home,
  finances-hub, government, infrastructure, land-office, people,
  sectors-after-load, services-health). So 0.7.31 is seen by Jerus, 0.7.32 and
  0.7.33 by the orchestrator's one look, and 0.7.24's fix round to 0.7.30 by
  Jerus's own play.

  **Not yet seen: 0.7.34 to 0.7.39**, none of it rendered: the toolkit cannot
  start in the cloud, and every word and figure was composed by the
  implementers' probes on the playtest's 600- and 2,400-month cities (just
  loaded, a month on, saved and loaded fresh, and in caused states), so his
  figures will differ. The look did not open Trade, Policy or City History,
  still 0.7.31's then. Two things it could not have shown: the flags that
  Finances' OWED AND THE RATE and the Bank's ITS RATES promise, which a small
  chart did not draw (0.7.38 gives it a lane: below), and the Bank's rates
  chart's drag (never built; since 0.7.38 its (i) no longer promises it). In
  his autosave at 1,389 × 868, the drawer closed:

  **Buttons that ask to be pressed (0.7.34)** (his treasury is about $64k, so
  CREDIT is the look he will see most):
  - **Throughout:** the action button as wide as its card, 40 px, radius 8, the
    Build icon (or the land icon) at the left of a bold 13 px label, a 9 px
    line under it in some looks: GO filled in the darker pink (#cf5590) with
    white words; CHOOSE and CREDIT transparent with a faint pink tint and a 1.5
    px pink edge; HELD the control grey. Hover each (GO lightens, the outlined
    looks tint deeper, HELD goes to the hover grey) and press and hold (GO
    darkens; **CHOOSE and CREDIT turn a deeper pink, which on the dark card is
    lighter**, the docs pass's correction of the notes' "each darkens"); the
    hand cursor over all of it. The pill 30 px, fully rounded, a 1 px pink edge
    on a faint tint, the icon, pink words and "›". Nothing ends in "…" ("Nobody
    licensed to work in it" and "Short 24.1M sq ft of land" on a 300 px card;
    "Build 100 on credit · $1.23B" on one line). A card must not jump when its
    count leaves 0 or its verdict changes (every look is 40 px). Verdict
    colours stay on the quote line under the button.
  - **Build › Healthcare:** each card "Build · choose how many" outlined under
    the stepper; press it at 0: the count reads 1, the border turns pink, the
    button reads "Build 1 on credit · $X" in his city, the quote line fills,
    nothing is ordered; **double-click at 0: the count goes to 1 and stops
    there**; +10 reprices in place; Build with a count opens the credit page,
    whose offers end in a filled 560 px "Build 11 · $56.1M" over "issues the
    20-year bond, then builds" [0.7.40: now Build › Funding, see CHECK 0.7.40
    AND 0.7.41, item 5]; a General Hospital ×100 goes HELD, "Short … sq ft
    of land" over "more ground: the Land office ›", and its press opens NOT
    ENOUGH LAND, whose "Go to the Land Office" is a pill; the order bar's
    "Build 21 on credit · $X".
  - **Build › Industry and the Overview:** an Iron Mine with no deposit free
    reads "No iron deposit" over "ore comes with land: the Land office ›"; an
    Engineering Services Office without engineers "Nobody licensed to work in
    it" over "a school licenses them: Education ›", its page's "Go to Build ·
    Education" a pill. On the Overview each suggestion ends in a full-width
    button with a "Show" pill above it, and "Build all three" (32 px) takes its
    cards' look.
  - **The land office:** each card's full-width "Buy on credit · D$…" in his
    city (filled "Buy · D$…" where the cash covers it; from the vault and
    short, "Buy · US$…" over "the vault is short: ways to pay"); **the cards of
    a row end their buttons on one line**; "Buy the next 5" (32 px) beside its
    total; the funding page's filled "Buy · D$…" over "issues the 20-year bond,
    then buys"; **a double-click buys one plot.**
  - **The pills,** each opening what the old link opened: Services' "Build them
    on Build" and "Build for it" (inside a clickable card it opens Build, not
    the card); Infrastructure's "Build · Roads & transit", "Build transit" and
    "Rail on Build"; Sectors' "Build · Homes" and the Investors page's "Land
    office"; People's "Build homes"; the Bank's "Build a Commercial Bank".
  - **Finances' hub:** "later" (D$721.4B) a ghost up to the plot's top, two
    slashes across it a third of the way down with the card's ground between
    them, its figure on top; the twelve years fill the height (2155's D$47.9B
    about 83% of it); Borrow's and Issued's ladders the same, the gap matching
    their ground.
  - **Sectors, right after Continue:** one grey line over the cards; fourteen
    word lines empty, Real Estate's "building · investors are building · 468 on
    site …" as before; press the clock: the line goes, the words fill in, and
    no card changes height.

  **Trade (0.7.35)** (figures the 2,400-month city's; his is bigger):
  - **Throughout:** nothing ends in "…" (the goods' names, which wrap; THE
    MONTH's note on two or three lines in its 190 px cell; "BACKING FOR THE
    MONEY THAT CAN LEAVE"; the forces' names; the river's band names); one
    scrollbar (`STAGE_REST` 36); verdict colours only on THE CURRENCY, IN THE
    VAULT, the gauges' chips and bands, the action cards' edges, "walked away
    from", the city's own reserves when negative, the exchange's cover and a
    defence month's chip, with SOLD, BOUGHT, THE MONTH, the goods, the forces,
    the claim bar and the river plain; "D$" and a true minus on every local
    figure.
  - **The frame:** the violet swatch, "Trade & the world" (i), "Over the years
    ›"; the chips, **the new EXCHANGE icon by eye**; **five figures across at
    1,389 (5 × 190)**; **right after Continue SOLD ABROAD, BOUGHT ABROAD and
    THE MONTH read "not counted yet", "since the city was loaded or founded: a
    month on, it is"**, never $0, and fill in a month on; press the clock: SOLD
    and BOUGHT count up. The action card, probably "Under 3 months of import
    cover" in red on every page, with "Buy reserves ›".
  - **Overview:** WHAT WE TRADE, bought left and sold right, eight rows with
    the goods' icons (`Icons.ofGood()`) and "+N more goods and the railway's
    fuel"; hover a row (it greys; who sold or bought it), click it (What we
    trade opens on that good's popover), press the clock (the bars grow from
    the axis); THE CURRENCY with **parity a short line at the right edge**,
    recorded since he first plays 0.7.35; the three gauge cards.
  - **The month:** the steps and the river toggle; the river's closing band an
    outlined grey "The month's surplus" (it was a red "deficit, financed");
    right after Continue "Not counted yet since the load" and no picture; the
    holdings card; the old ledger under "details".
  - **What we trade:** by good or by business; Cars' popover with "Automotive
    ›"; right after Continue "WHAT WE TRADE, THE MONTH THE CITY WAS SAVED IN";
    SINCE FOUNDING's waterfall; the price grid, building materials a unit in
    D$.
  - **The currency:** THE RATE's parity gauge either side of its middle; **THE
    RATE OVER TIME at 794 px, not the notes' 824** (the hero row 400 + 10 + 794
    = 1,204), with no Full screen button; drag, wheel and double-click pan,
    zoom and reset, and a month keeps the window; "Parity is recorded from
    0.7.35 on: N months so far."; WHAT IS MOVING IT, NEXT MONTH with no red or
    green.
  - **The reserves:** WHOSE IT IS and its claim bar; HOW LONG IT WOULD LAST
    with its "3" and "6 months" ticks; BUY OR SELL FOREIGN MONEY: the outlined
    blue "Buy foreign money" over "choose how much above" (a press picks the
    smallest step and prices it, never trades); pick "to 3 months": WHAT IT
    WOULD DO, and the button filled ("Buy D$… of foreign money"); press it: a
    "bought …" chip, the cover 3.0 months; **a double-click is one purchase**.
  - **Elsewhere:** **the header's rate line in the drawer's THE CURRENCY row's
    colour** (D8: amber if his rate is 25–50% from parity either way); that row
    opening The currency whatever page was last open; City History's "Parity"
    line; Finances' "The currency ›" and its reserves door landing on their
    pages.

  **Policy (0.7.36)** (figures the 2,400-month city's; his is about twelve
  times bigger):
  - **Throughout:** nothing ends in "…" (the dial cards' names wrap, the effect
    rows' labels, the payers' names at 220 px and their figures at 230, the
    four figures' notes); one scrollbar, and with something staged the page
    stops above the tray at the stage's foot; verdict colours only on the four
    figures in their NEEDS YOU rows' colours, WHAT IS BITING, "at its legal
    maximum", "pinned · N spare", the pensioner's chip, the schools' burden,
    the priced out, the ceiling and the inflation tick (amber more than a point
    off the target, where the header is green within three: MODEL BUGS item
    16); every Apply the blue GO beside a grey "Leave it as it is".
  - **The frame:** the four area chips, the breadcrumb and the tabs; **four
    figures across at 1,389**; **THE FLOOR his cash floor, in today's money**,
    not the founding figure; PROMISES without the waived tuition.
  - **The hub:** four area cards across at 1,389, their words wrapping; **only
    the Taxes card has a "last moved" foot** (the docs pass's correction of the
    notes); WHAT IS BITING (one green "Nothing is binding" card, or alert cards
    with NEW and a door pill); RECENT DECISIONS and its "History" pill.
  - **Taxes:** THE TAX TAKE with a GDP share right after Continue (B3); EVERY
    TAX AT ONCE: **drag it and the rows follow the thumb**, release stages it
    and the tray appears; parted (set Sales apart and come back): three chips,
    three named ticks, "three rates" in grey. The tax pages: WHO PAYS, the bank
    a Profit payer "taxed at Retail's rate, in arrears", Automotive's refund
    with a true minus, zero payers in one row; **a row opens its own offset's
    dial card, which stays open through a month**; the four columns fit at
    1,389; **on Property, an opened offset's (i) ends "...capped at 30 points
    either way; a property offset's dial stops at 10."**; WHAT A PAYSLIP LOSES
    plain; FARMLAND. The tray: chips with ×, THE BUDGET, "Apply all 2" and
    "Discard"; Apply moves the dials and puts the decisions on History's flags;
    changing page drops the staged set.
  - **Wages:** THE WAGE LADDER's chips (Diploma "has room" while paid exactly
    the floor is B8, as the model has it); a staged floor's dashed rule and
    ghosts; THE FLOOR's ladder in today's money, the founding figure behind its
    (i).
  - **Money:** THE RATE LINE's tick names not overlapping at the low end (the
    dial, savers, the rule); THE DIAL, THE RULE and THE CENTRAL BANK; PRICES;
    **"details ▸ the price level and the policy rate, since founding", a chart
    never seen: check it draws, its two axes read and it fits the card**;
    Currency reform locked or open.
  - **Promises:** Pensions' four cards, each with its own Apply; Out of work
    previewing from a zero dial; Health's patient card of five rows and its
    "Fees cover …"; Schools' statement footing with the waived tuition in grey,
    and with no school a "Build · Education" pill; Subsidies.
  - **Elsewhere:** Infrastructure's fare Apply the same blue GO ("Set the fare
    to $X", "Make it free"); the left panel's "Min wage" today's floor; save,
    load and Continue: the figures there at once; a staged tax set, open payer
    rows and folds surviving the clock's redraw.

  **City History (0.7.37)** (with its docs pass's two additions,
  `runs/ui18-docs-pass.md` flags 12 and 13; 0.7.38's renamed floor line is
  below):
  - **Throughout:** nothing ends in "…" (RUNNING NOW's note in its 190 px cell,
    the cards' names, the decision rows' labels, the goods' names at 220 px,
    the hard times' worst lines); one scrollbar under the fixed frame; **resize
    the window narrower, wider and maximised: the big chart and both pins
    follow within a moment, with no horizontal scrollbar left behind; at a
    window size other than 1,389 watch for a one-frame jump on the first draw**
    (★25); verdict colours only on RUNNING NOW, the hard times' tags and icons,
    and a failed write; a true minus on every figure the page prints (the
    crosshair card keeps its hyphen).
  - **The frame:** "City History" with its (i) and a blue "Write the year book"
    (hover lightens, press darkens; check it is not cut and the head does not
    wrap at 1,389); press it: the result card, "Written to <folder>", a line of
    files a book, and its ×. The strip: THE CITY, HARD TIMES, RUNNING NOW and
    YOUR DECISIONS; **in his city the epidemic should sit last ("chronic")
    unless it runs alone, when it reads "running since <its start>"**; HARD
    TIMES and YOUR DECISIONS scroll to their section, RUNNING NOW moves the
    chart to its episode.
  - **The page:** the pins as two cards, 120 tall (hover the move; click a
    name: that line alone over ten years, the pins unchanged; the big chart's
    plot ending above the window's foot on arrival); the big chart at the
    page's width, and **on All the first flag on the "you" lane at the axis's
    first month, its card's head beginning "Jan 2000"** (B4, where
    founding-month decisions are logged); WHAT EACH LINE DID three across, the
    range bars and their tooltips, a hidden line's card at half strength, the
    figures counting on and the dots sliding when a month lands; HARD TIMES AND
    YOUR DECISIONS (a row moves the chart; "+1 more in view ›"; a decision this
    month first with "this month"; the by-kind fold opening without the page
    jumping); PICK WHAT TO DRAW, "3 drawn · 217 lines", a group's icon, "net
    income" opening SECTORS with its 15; PRICES THIS MONTH's counts and its
    fold (the goods on their bands, "Open at one end", whose caption's (i) now
    ends "On the open side the city's market sets the bound: twice the floor
    where the world sets no ceiling, and zero where it sets no floor.", "Set by
    their seller" with "Retail ›" and the rest as doors, and right after
    Continue the freight line).
  - **Elsewhere:** the header's tiles open History as before, landing at the
    chart when already there (now in `openHistory()`'s javadoc); the doors
    into History open at the page's top; full screen as before.

  **The loose ends (0.7.38)** (`the-loose-ends.md`, `runs/ui19-notes.md` §3;
  its docs pass running, so look in `runs/ui19-docs-pass.md` for corrections;
  figures the 2,400-month city's, his 1,851-month city's will differ):
  - **Throughout:** nothing cut ("Minimum wage, founding money" on History's
    reading card; the fare card's rows wrapping in a column of about 612 at
    1,389; the drawer's "Import cover under 0.1 months" on one line in its
    290 px panel); on a small chart's lane a single flag's label ends in "…"
    where the next flag starts, as on the big chart (hover shows it whole).
  - **Bank › History, ITS RATES** (1,180 wide): a "you" lane under the years,
    18 px, "you" at its left, the chart about 20 px taller; flags as on City
    History, wherever he moved the policy rate or a rescue or preferred offer
    came in the last ten years; hover one: its card above the lane and a
    dashed line up the plot; the (i) no longer says "Drag to look back"; **the
    six small charts under it unchanged** (no lane, the same height).
  - **Finances › OWED AND THE RATE** (1,160 wide): the same lane with the
    borrowing decisions (an issue, a buyback, the rollover's setting, a
    default abroad) of the last ten years; hover one.
  - **The founding month:** out of the Bank's and Finances' ten-year view in
    his city; on Trade › The currency press All: a founding-month currency
    decision, if he has one, on the axis's first month, its card saying its
    own month.
  - **Import cover:** Finances › the currency page's WHAT IS BEHIND IT,
    "Import cover under 0.1 months" and its bar's tooltip; the drawer's TRADE
    section the same **in red** (it was amber "0.0 mo"); in a young city the
    founders' note "... - N.N months of imports" or "over 10 years of
    imports".
  - **The drawer's "vs parity":** plain at 15–25% either side where it was
    amber; amber from 25%, red past 50%, plain when pinned.
  - **People › the jobs card's foot:** "Minimum wage $X a month; ..." matching
    Policy's THE FLOOR and the left panel's "Min wage". **City History:** the
    MONEY group's "Minimum wage, founding money", the same line as before.
    **The year book:** minimumWage's note "in founding money".
  - **Infrastructure › Transit › THE FARE**, a dial card: the bus icon, "$2.50
    a ride", "$100 a month for 40 journeys, $2.50 each", the ladder at 520 px,
    WHAT IT WOULD DO's four rows over their bars and a grey caveat line with
    its (i). **Drag the thumb: the rows follow** (at $5.00 riders 41,416 →
    39,236, fares $4.1M → $7.8M, net $19.8M → $16.1M, +4,871 trips back onto
    the road; free: −4,871 trips). Release: "Set the fare to $5.00" and "Leave
    it as it is"; Apply, and the funnel's fare row moves a month on. **Right
    after Apply, until the month turns,** the rows still show a move (★11).
  - **The Bank's ladder** (Overview and Lending): a shut-out sector's name red
    with its "›" beside the red "shut N mo" chip, a click still opening its
    Cash & debt page.
  - **Sectors › a sector › Cash & debt, NEW BORROWING COSTS:** the bar's parts
    and its caption as struck; a white mark only where a concentration
    discount takes points off; the (i) itemising the same parts.
  - **Policy › Promises › Pensions, WHAT SENIORS RECEIVE (i):** ending "a
    design question the model has filed and not yet answered."; **Build's
    Overview:** "all three ≈ $X" and "Build all three" as before.

  **The city's fund as a brokerage (0.7.39)** (`the-fund-as-a-brokerage.md`,
  `runs/ui20-notes.md` §3; its docs pass's corrections,
  `runs/ui20-docs-pass.md` flag 12, and its three fixes' words,
  `runs/ui20b-notes.md` §3, in the last item; every word was read by the
  probe on the two research cities, not on his 1,855-month city of 1.38
  million):
  - **Load the city (an older save):** Finances › the hub's "The city's fund"
    card → **Portfolio**. The worth in 28 px mono; the return line "not
    recorded yet: its worth is kept from this build on, a month at a time"
    (the series start this version); "Since it began: …" with an (i) naming
    the parts: **check it reads sensibly for his fund**; CASH / INCOME LAST
    MONTH / TO THE TREASURY LAST MONTH under it. Every holding tagged "cost
    from <the load month>" with unrealized D$0 (seeded at market value); a
    rescue book, if he has one, exact with its real P&L.
  - **The hero's chart** appears after two months, 800 × 230 to fit the hero
    card at 1,389 (1,234 content − 32 padding − 360 − 24 gap = 818): **check
    it sits inside the card and nothing is cut at its right**; its range
    chips, drag and wheel move it and the return line follows ("over 10Y"
    only when the window holds a full 10Y of record, else "from <month> to
    <month>").
  - **HOLDINGS** (name 250 · units 120 · price 110 · worth 110 · average cost
    110 · unrealized 160 · share bar): **nothing ends in "…" and the long tags
    wrap** ("cost from Dec 2199 · bought 26% below value"); bonds folded by
    issuer ("▸ Automotive · 8 bonds"); lots under D$1k folded into "and N
    more under D$1k ▸".
  - **SINCE IT BEGAN, BY KIND** in a 612 card: **the GAIN column wraps its
    percentage under the figure rather than pushing past the card** ("▲
    +D$439.1M (+3,320.8%)").
  - **Let the clock run a month on the Portfolio:** the worth counts up from
    last month's (about half a second), and lots paid that month show "paid
    +D$… this month" in the money blue, popping once; arriving from another
    page must not count.
  - **Search:** type "re" quickly while the clock runs: **the box must keep
    its focus and every key**; chips All · Shares · Bonds · Held; rows 44 high
    (icon, name, tag, price, the 1Y move in ink, "unchanged" for a stale
    price, a 12-month sparkline, "held D$…", "›"). Empty: YOUR HOLDINGS,
    SHARES, BONDS.
  - **Click a holding:** the security page, the breadcrumb "Finances › The
    city's fund › Automotive", "‹ Portfolio" at the right, **no page chip
    lit**; left (the head, the price chart 782 × 220, KEY STATS 4 × 2, THE
    BOOK, ITS RECORD) and right 412 (YOUR POSITION, THE ORDER TICKET). **The
    right column's lines wrap rather than run past the card**: the longest is
    "Offered at or under it today | 193.70 for D$19.7M, D$101,771 on
    average"; those lines wrap their figures (`narrowLine`), unlike the rest
    of Finances.
  - **The ticket:** Buy | Sell, by amount | by quantity, the steps (D$1k …
    D$1M; ¼ ½ all on Sell; "all its free cash" on Buy), the price chips and
    the ±1% stepper. The action button: "Choose an amount" → "Review: buy
    D$4.0M of Construction" → "Place it: buy D$4.0M / on the book at the next
    step, good for a month"; "Change it" backs out of the review. The order
    appears under WAITING FOR THE NEXT STEP with **Cancel**. With no free cash
    the button is HELD, "The fund's free cash is D$0", with a **"Pay in ›"**
    pill that opens Rules & cash with the amount set.
  - **A month on:** the order under ON THE BOOK UNTIL THE NEXT STEP ("…: 0 of
    39.47 filled"); a month after, Activity: "Your order: bought 0 of 39.47
    shares of Construction - 39.47 lapsed, D$4.0M back to the fund" (in both
    research cities a buy at fair filled nothing; "Best bid" is the price
    that fills).
  - **The bank's page** (if he holds it): both books when both exist and
    their sum; the stake ring and **"The bank's owners ›"** → the Bank tab's
    Capital & owners.
  - **Activity:** chips All · Trades · Income · Money in and out · Events; a
    month's rule trades in one market one line ("The rule bought 36 bonds")
    that opens ▾ into the lots; "Show older" adds 60 months; before tracking,
    the rescues and the FUND and BANK decisions "(from the decision log)".
  - **Rules & cash:** THE DIAL, ITS 3% TO THE TREASURY and THE RESCUE BOOK
    (now with the preferred's terms and the warrants' strike and expiry when
    held) as three columns; PAY IN, DRAW OUT with two action buttons (Draw
    out takes only free cash); THE RULE with the aim bar and the cap in
    words, and the rescue setting's pointer.
  - **Elsewhere:** Government's "Transfer from the fund" door still lands on
    the fund, now on Portfolio; **save and reload:** Portfolio and Activity
    read the same, and the "cost from" month does not move.
  - **Since its docs pass and fixes** (none of it seen either): in a city
    with a rescue book Activity's first row reads "Cost tracking began with N
    holdings: those bought on the market counted at their market value this
    month", and the bank's rescue lot shows no "cost from" tag and its exact
    cost: **check it reads sensibly against "Since it began"**; THE ORDER
    TICKET's (i) says "A buy's money is held for it from the moment you place
    it" and that a buy counts what the fund holds and your other buys, the
    rule's own bid making way for yours; the cap line's note "the rule's bid
    makes way for N shares of yours; room for …"; a buy with no room (only
    when what the fund holds and your own orders fill the cap): the line "The
    market book against the cap | X% held, Y% more in your orders | no room
    for this order under the 10% cap: the step would post none of it" and the
    button HELD, "No room under the 10% cap"; THE RULE's cap words, a
    company's buyback the one way past it; and in a city under thirteen
    months old, Search's "1Y: its record is shorter than a year".
- ~~**CHECK 0.7.24 TO 0.7.31 ON THE PC**~~ — **seen on 2026-10-01**: 0.7.24's
  fix round to 0.7.31 in Jerus's own play of 0.7.31 that evening, and 0.7.32
  and 0.7.33 in the orchestrator's one look (CHECK 0.7.34 TO 0.7.39, above).
  Kept below as the record of what each batch asked to be looked at
  (`the-build-screen-and-the-frame.md`
  §5; `runs/ui5-notes.md` §9, "What to look at on the PC (this round)";
  `one-card-for-every-building.md` §11; `runs/ui6-notes.md` §3, "What to look
  at on the PC"; `the-land-office-redrawn.md` §9; `runs/ui7-notes.md` §3;
  `people-at-a-glance.md` §9; `runs/ui8-notes.md` §3;
  `services-at-a-glance.md` §9; `runs/ui9-notes.md` §3;
  `the-road-in-one-picture.md` §9; `runs/ui10-notes.md` §3;
  `the-sectors-as-flows.md` §9; `runs/ui11-notes.md` §3;
  `earned-surplus-banked.md` §9; `runs/ui12-notes.md` §3). On 2026-10-01
  computer use's approval had lapsed after 30 idle minutes, and re-approving
  needs Jerus, who was at work; the checks of 0.7.24's fix round, 0.7.25,
  0.7.26 and 0.7.27 all wait for him (about 9pm), and so do 0.7.28 to 0.7.31,
  deployed and verified together as tag 1001m. The check by eye of 0.7.24 came
  before tag 1001i, so the fix round and the docs pass's strings are unseen: the chip opening
  on Summary after Dashboard was picked in the drawer; the verbs per measure
  (Police, Road capacity, Education, Cells); no staff bar on roads and "needs
  no staff"; "posts the city can't fill" and Healthcare's "fewest unfilled
  posts per patient" tag; the amber TREASURY row (only on a city with less
  cash than a month's tax); Settings' Esc line. Nothing of 0.7.24 has been
  seen at 1,280 or 1,920. **0.7.25 (tag 1001j)** has not been seen rendered
  at all; every word below was read by probes on the playtest's 2,400-month
  city, so his figures will differ. In his 1,851-month autosave (Continue),
  at 1,389 × 868:
  - **Every Build page:** cards 300 px wide, left-aligned, as tall as their
    content, 10 px gaps; nothing ends in "…" (the name, the have-line, the
    hero, the detail, the bar labels, the investors' line, the needs line and
    the quote all wrap). The (i) at each card's top right: hover covers the
    card with its stat cover, a click keeps it and turns the dot blue, a
    second click clears it. The cover: the name in capitals, the
    `whatItDoes` sentences, Materials, Build points, Road load, Electricity,
    Water, Wages and the job mix; on a market card it ends "Investors'
    estimate: $X a month to its owner."; a long cover scrolls inside the card.
  - **Homes:** "HOMES · a door for every household" with the door shortfall at
    its right. Low-Rise Apartments: the violet square, a tag, "houses 252
    residents" over "in 63 homes for 4 · children welcome", the price green
    when the cash covers it, "cost per resident" (blue bar), "land per
    resident" (pink bar, land icon), "Investors holding: housing ahead of jobs
    (…)", "needs no staff · 60,000 sq ft · … · nothing to run" (no share under
    1%, as in his city with 51M sq ft free). Studio "in 80 homes for 2 · adults
    only"; House "in 1 home for 6 · children welcome".
  - **Shops:** GROCERIES with its note, THE BANK'S BRANCHES with none. Small
    Grocery Store "serves 1,600 customers a month", its posts and "runs
    $Nk/mo" ("the city could staff N%" only below 99.5%). Commercial Bank "a
    branch for 16,000 customers · brings $32.0M of shareholders' capital",
    both bars as figures with no track, the bank's own word.
  - **Industry:** seven headings in order (Food mills, Food processing, Steel,
    Fabrication & machinery, Iron, Building materials, Builders), each maker
    note "the city used N …; plants here make C". Steel Foundry "makes 1,200 t
    of steel a month", "from 1,320 t of iron ore · adds $Nk a month at today's
    prices", "its price, in months of what it adds", "land per $1k it adds a
    month". Machine Works: no "this one:" where the word names it. Iron Mine
    "lifts 2,500 t of iron ore a month" with its deposit line, and with no
    deposit free the quote at 1 reads "no iron deposit" in red. Construction
    Depot "adds 400 building points a month".
  - **Offices:** Contact Centre "exports 300 seat-months of support work a
    month · worth $N a month at $N a seat-month" and "posts the city couldn't
    staff, of 100" (teal bar, staff icon). Engineering Services Office with no
    high-tech licences: the line ends in amber "· this one: needs N spare
    high-tech engineers; the city has M", and + gives "… · nobody licensed"
    in red.
  - **Farms:** "FARMS · crops, dairy and eggs, meat, vegetables and fruit"
    with its note. Mixed Farm "grows 69 t of crops a month · and 5,000 kg of
    dairy and eggs and 1,400 kg of meat · adds $N a month at today's prices";
    built over, its needs line red ("600,000 sq ft - more than is free"), the
    line's amber "this one: no land - …", and the quote at 1 "short N sq ft of
    land".
  - **Restaurants:** "RESTAURANTS · the city's food, cooked" with "N meals
    wanted; the kitchens serve C", and right after a load only "the kitchens
    serve C" (D11). Diner "serves 13,500 meals a month", "cost per 1,000 meals
    a month", "land per 1,000 meals a month".
  - **The city cards** (Healthcare, General care): the needs line ends "· N%
    of what is free" (1% or more) and "· runs $Nk/mo"; the stepper has +100;
    the Walk-in Clinic's (i) reads "Treats 2,500 people a month when fully
    staffed." with the general-care sentence, the figures and the wage line;
    the Childcare Centre's cover carries "Childcare is the strongest lever in
    the game…"; Education › Medical School, a group of one, has both bars'
    figures and no track; no "cheapest" tag where every card costs the same
    (D4).
  - **The order bar on a market page:** on Industry, + on two cards in
    different groups gives "1 × Bakery + 1 × Steel Foundry", "$X all in, of
    your $Y", the posts and land (red when short) and "Build 2", with no
    measure and no stacked bar; the cards' borders pink while ordered; Enter
    builds both, Backspace clears them.
  - **The investors' line, both states:** their word ("Investors holding: …",
    "Investors built N X - …") with amber "· this one: …" where a gate stops
    this one and the word does not name it; hover for the whole word and the
    estimate; a click opens the owner's books on Investors. On site (in the
    probe city at month 2,406, Luxury Retail's three Boutiques): the head
    "you have N · 3 on site · under a month at today's queue", pink and
    underlined, a link to the site; the line "Investors are building · 3 on
    site · …" with no "this one:", and ", yours among them" when the city has
    an order on the same site.
  - **Just loaded:** every market card reads "Investors: nothing recorded
    since the city was loaded or founded"; after one month the bank's line
    must **not** read "no bank" (the load-path fix).

  **0.7.26 and 0.7.27 (tag 1001k)** have not been seen rendered at all; every
  word was read by probes on the playtest's 2,400- and 600-month cities
  (`ProbeLandWords`, `ProbePeopleWords`), so his figures will differ. The
  same window and autosave, with the drawer closed and the construction tab
  folded. **The Land office (0.7.26):**
  - **Throughout:** nothing ends in "…" (the cell notes, the card lines, the
    value words, the tags, the worth cards' lines and the receipt all wrap).
    Verdict colours only on GROUND FREE, WAITING ON GROUND, INVESTORS PAY and
    the margin chip, the going-rate words, a price no way pays, "ore that
    nothing is digging", the waiting rows, and the funding page's short
    figures and rate lines.
  - **The head:** "Land office" with a pink square and an (i) ("Investors
    build only on ground the city owns…"); the chips "Convert D$" and "From
    the vault" (tooltips "Pay by converting cash", "Pay from the vault");
    under them "D$x per US$ · vault US$y untouched" and an (i) holding the
    whole "Land is priced in US dollars…" sentence; "‹ Build" at the far
    right.
  - **THE GROUND:** GROUND FREE **green** on his city (about 51M free; it is
    never red for the share used), with "… km² · N% of … km² built on" under
    it; WAITING ON GROUND "0 sectors" with a "›" that scrolls to the Waiting
    card; THE WORLD ASKS; INVESTORS PAY in green; the big figures and, in
    pink, "→ … sq ft free after the next 5". The bar: a solid pink start
    (wide in his city), five pale-pink outlined ghosts numbered 1–5 with a
    sand stripe where there is ore, a grey tick "NEEDS YOU's line", the
    scale row under it. Hover a ghost ("plot 2 · … / Click for its card.")
    and click it: the page scrolls to its card. **Check** the numbers fit
    inside the ghosts and hide on any too narrow.
  - **ON OFFER:** its (i) ("The office lists 9 plots at a time… grows one
    block for every 40 blocks the city has bought, up to 15 blocks…"); "− 5
    +", the total, "US$… listed", a green "Buy the next 5". − drops a ghost,
    card 5's badge and edge, and the total; + the reverse; they stop at 1
    and 9. At one plot the button's tooltip reads "Buys this plot, as its
    own card's Buy would." (the docs pass's fix).
  - **The nine cards,** three rows of three, about 400 px each: cards 1–5
    with a pink 2 px edge and a number badge on the land icon; the size, km²
    and room; the price **white, not green**, "· US$… listed"; a short pink
    value bar with a grey going-rate tick and a fainter world hairline; "US$x
    /sq ft · N% under the going rate" in green (amber over); green BEST VALUE
    and sand "ORE ×1 · …" tags on card 1, "ORE ×2 · …" and "MOST ORE" side by
    side on the richest; a green Buy. Hover a card: a three-line summary;
    hover Buy: "Buy this plot". **Check** the value words wrap under the bar
    and the tags are not cut.
  - **Buy:** the solid ground widens over a third of a second; card 1 leaves
    and the refill carries a pink NEW until the month turns; a pink-ticked
    receipt under ON OFFER in compact amounts ("for US$…M", not
    "US$…,…k"); an ore plot pops "+1 deposit" on the Ore card. The next 2:
    "Bought 2 plots, … km² in all, for … The last: …" and "+2 deposits".
  - **WHAT THE GROUND IS WORTH:** **Margin** (blue coin; two bars on one
    scale, a green "margin +D$… a sq ft" pill, last month's land sales; its
    (i) the three old statement lines plus "Investors pay ×N …"); **On top of
    the build** (pink house; House and Food plant as pink | grey bars with
    their shares, a key); **Ore** (sand pick; deposits, tonnes, "N mines on
    them, standing or on site", amber "ore that nothing is digging"; hover
    turns the edge blue, a click opens Build › Industry; **look at the pick
    icon**, D16); **Waiting on ground** (a green tick ring and "every sector
    has room to build"; with a sector waiting, a red icon and a red row such
    as "Real Estate ›", its word in the tooltip, a click to its Investors
    page).
  - **details ▸:** "THE WORLD'S PRICE OF GROUND" with an (i), a pink line in
    US$/sq ft over the years, "spent on ground since the founding: … (… from
    the vault)"; leave and come back and it stays open; click again to close.
  - **From the vault:** the caption "vault holds US$… (D$…)"; every price the
    US$ figure, white, "· D$… at today's rate"; every Buy grey, "Buy · vault
    short" (tooltip "Buy — the vault is short…"); the next-N button grey,
    "Buy the next 5 · vault short", its total white. **No green price beside
    a "short" button any more.**
  - **The funding page from the vault** (press "Buy the next 5 · vault
    short"): the rail's Land office lit; "Land office › Funding" with a pink
    square, "Land office" clicking back; a red "short by US$…" pill; a panel
    "5 plots cost …", a bar mostly red-outlined after a thin blue start, "the
    vault holds … short …"; "WAYS TO PAY" and three cards: the 20-year dollar
    bond (yield and coupon, Face, Cash it brings in green, Monthly cost, Cost
    of the credit all in, the ending, "Market rate: … (+N pts…)", "Issue it
    abroad and buy from the vault"), the 6-month dollar note with its rate in
    amber, and "The vault's dollars, and the rest converted from cash"
    ("Take what the vault has and convert the rest"); Cancel at the foot.
    **Converting** is reachable only when the cash is short (his city has
    US$4.4B, so slot 10 or a small city): "TWO WAYS TO BORROW IT", the bond
    and note cards and an empty third column; let a month land on a city
    whose cash has since covered the gap, and the page goes back to the
    office.
  - **Elsewhere:** Build › any city category: LAND FREE **white** on his
    95%-used city (it was red, "51.0M sq ft"), amber at a block or less, red
    at none, still a door to the office. The drawer › Dashboard › LAND: the
    header "51.0M free · D$x.xx/sq ft" in plain white (it was red "95% used ·
    $x.xx/sq ft"); inside, Owned, Free, **Used 95%** and Price/sq ft with
    "D$"; the red alert block without "Land 95% used". Build › Construction:
    the gauge's queue bar (10 px band, its mark at twelve months), the
    sites' progress bars and the shells' grey bars exactly as before
    (`Pieces.segmentBar` now). Build: the cards' green tags, the stepper's −,
    +, +10, +100 and ↺, the Overview's heads and hints, and the funding
    page's colours unchanged. Sectors › a business waiting on ground: "…buy
    a plot at the Land office and it will build next month."

  **People and Household money (0.7.27):**
  - **The first look is special: his save is format 29.** Before a month
    runs, the bridge card says "Not recorded yet: this city was saved before
    0.7.27 kept the month's draw…", the Moved in / Moved out popovers say the
    same, the Died bar has a grey part "not split: a save from before
    0.7.27", and GOING SHORT reads "eating less than a full basket". **Then
    press the clock once while on People:** LIVING HERE counts from last
    month's figure to this one, the waterfall's bars grow out of the zero
    line over 0.6 s, and the bridge appears. Do the rest after that month.
  - **Throughout:** nothing ends in "…" (the vitals' notes, the waterfall
    names, the mosaic labels, the bridge captions, the ring lines and the
    chips all wrap). **The pyramid has no green.** Verdict colours only on
    the vitals, the rings, the homes' chip, the why chip and the housing
    ring, pay chips over 1.05×, profession chips short of licences, the No
    home and Orphans counts, the outbreak and crime chips, the alerts, and
    Household money's verdict chip, cells, put-by figures and Saved total.
  - **The head and the vitals:** "People" with a teal square and an (i),
    "What this model does not do yet:" with four lines; the chips People
    (raised) and "Household money ›". Five vitals, each label with a blue
    "›": LIVING HERE scrolls to the month card, OUT OF WORK to Work, SPARE
    HOMES (red where households are doubled up, D5) to Homes; OFF SICK opens
    Services › Health › General care, GOING SHORT Household money; hover
    each for its tooltip. **Check** GOING SHORT, the last cell, keeps no rule
    at its right after a hover and shows a hand.
  - **WHO LIVES HERE**, "N of working age (N%)" at the right: six rows,
    Elders at the top, each with its ages ("85–120", "0–5"), a bar on a thin
    axis, a 1 px grey outline (the settled share) and "count / share". The
    adults' bar should poke past its outline; elders and seniors sit inside
    theirs. Light teal, teal, dark teal; a key; "Each 100 working adults
    carry N" with an (i). Hover a row: count, share, settled share, % off
    sick, died of illness.
  - **THIS MONTH**, month/year chips at the right: "a → b living here a
    month ago, and now"; Born, Died (stacked dark teal of age, violet
    illness, pink killed, light blue aged out), Moved in, Moved out, Net,
    with icons, names and dashed links, a key under them. Hover a Died part:
    its figure, then the illness deaths by age. **Click Moved in:** a
    popover "Moved in: N, by the skill they hold", a skill bar, the table
    and the note. **Click year:** "a → b living here a year ago, and now" and
    the year's sums. A city that crime drove people out of shows a red chip
    under the head ("Crime drove out 5" in the 600 city).
  - **WHY PEOPLE COME** (i), "how big a city this good draws" at the right:
    [jobs × people per job · ¾] + [homes for N · ¼] = [jobs and homes] ×
    three grey pull chips = [a city this good draws]. The jobs and homes
    boxes turn blue on hover; jobs scrolls to Work, homes opens Build ›
    Homes. At the right a teal bullet bar with a white tick ("N living here
    against the draw · N room to grow"), a 44 px ring and the why chip with
    its line and (i). **Check** the boxes wrap rather than overflow at 1,389.
  - **CARE**, "Build's rings: click one to build for it": four cards with
    56 px rings as Build › Healthcare draws them (General care, Childcare,
    Senior care, Death care), each with an (i); hover blue; click → Build ›
    Healthcare with that ring picked (pinned ground, pink edge). Death care's
    alerts under the row when they fire.
  - **HOMES AND HOUSEHOLDS:** HOMES with its verdict chip, a teal bar with a
    light-teal spare end (pink only for homes on site), the key, "N
    households want a home · N homes · N on site", the line and its (i), the
    flatshare and doubled-up chips, "Build homes ›". HOUSEHOLDS ("click one
    for its books", a "Household money ›" chip): three strips, retired
    (half-filled glyphs), families (a 3 px tier ramp at each tile's foot)
    and outside; the families' strip fills the card and the others are as
    long as their households; the biggest tiles proportional, the rest at
    56 px, "+n more" only at a narrow window, its tooltip naming them. Then
    the ramp key, "N households · average N people · N adults unplaced" and
    its (i) ("Kept from last month, before flatmates pair up…"), and
    "details ▸". Click a tile → Household money with that cell open. In
    details: the pay row "one earner was paid …" at the live wages, whole
    households (no ".54"), and a "working households" totals row. **Look at
    the glyph sizes** (adult 8 px, teen 6.4, child 5.2, baby 3.6, senior
    half-filled).
  - **OUTSIDE THE FAMILIES** (i): five tiles with teal icon squares and
    120 × 24 sparklines of the last ten years (Out of work "N on EI · N run
    out", Students, No home, Orphans in red, In prison "N caught, not held").
    Students → Services › Education; In prison → Services › Safety ›
    Prisons; Out of work and No home → Household money with the out-of-work
    row open; Orphans → the biggest orphan row. THE POOL THIS MONTH ("in"
    and "out" bars on one scale, their parts named; "N reached the end of
    their EI this month" with an (i)); EI paid against premiums as a blue
    bullet bar with "The premiums cover N% of what EI paid." Then a red
    "NOBODY FEEDS THE ORPHANS" alert, and "details ▸" for the table by age
    in whole people.
  - **WORK:** WORKERS · POSTS · UNFILLED (amber) · IN A JOB · LOOKING; the
    labour line in amber with its (i); THE SKILL LADDER (i) with a key, four
    rows of teal open posts over a light-teal outlined queue, "N open · N
    queue", "chance N%" and "pay N×" chips, red pay chips over 1.05× with
    "reach N%"; "Licensed:" chips, red when short; three "details ▸", each
    remembered while the game runs; the foot "Minimum wage $… a month…".
  - **Household money:** "People › Household money" with the teal square,
    "People" clicking back, an (i) saying every figure is in dollars, the
    chips "‹ People" and Household money (raised). THEY KEEP, RENT TAKES,
    GOING SHORT ("N can't afford · the shops handed over N%…", a door to
    Build › Shops), INCOME PER RESIDENT ("an average filled job pays $…").
    The verdict row: a chip, the sentence naming who lives on savings and who
    went hungry, an (i), the One household / All of them chips. Tier heads
    in two lines ("Unskilled / $… a wage"; "no earners" where nobody earns).
    Below the rule each caption has an (i), which now says the retired read
    their statement (the docs pass): Senior/Elder alone "$0" grey,
    Senior/Elder couple green, On EI green, **EI run out amber**, orphans
    red. At 1,389 the books sit **beside** the grid ("Click a cell to open
    its books"); open Couple, no children / Unskilled: "Healthcare and school
    fees", "Transit fares" and "Bank fees" each its own line, no interest
    line. **Scroll the page:** the books stay at the top of the view while
    the grid passes and stop at its foot; under about 1,240 wide they go
    under the grid, and a click scrolls the two into view.
  - **THE CITY'S MONTH:** 17 columns from Wages to Saved, with "Bank fees"
    its own violet step; **check "Contributions" and "Health premium" are
    not cut** (narrow columns, 4 px gaps); "$… saved since founding. A
    record, not a pot."; "details ▸ the month as a statement" with "The
    bank's account fees" as a line. **WHAT THEY HAVE PUT BY:** SAVED, OWED,
    DIVIDENDS THIS MONTH, CARS with its (i); in his city the chips "N at
    their credit ceiling · M discharged ›" and "N flatsharing because one
    wage won't cover a home ›", each opening a cell.
  - **Elsewhere:** the drawer › HOW THE CITY IS › HUNGRY reads "x% of
    people" in red (it read the sickness points); its tooltip gives the
    points; a click opens Household money. Policy › Promises › Pensions ›
    "And what it does to people": "A pensioner household has to spend …",
    "…its rent and bills come to …", "…leaving for the shop …", which foot,
    then one of the green, amber ("…eating out of savings ($X drawn this
    month).") or red sentences; the page that read "−$3,004" over "They can
    afford to eat." in his city should now be amber. Build › Healthcare's
    four rings, picked and not, and Education's nine small rings exactly as
    before (`Pieces.ringCard`); Construction's status chips unchanged
    (`Pieces.chip`); Services › Health's THINNEST COVER on the month's
    coverage (the same unless fees bite).

  **0.7.28 to 0.7.30 (deployed and verified with 0.7.31 as tag 1001m)** have
  not been seen rendered at all: the toolkit cannot start in the cloud, and
  every word was read by probes on the playtest's 2,400- and 600-month cities
  (`ProbeServicesWords`, `ProbeInfraWords`, `ProbeSectorWords`), so his
  figures will differ. The same window and autosave, with the drawer closed
  and the construction tab folded. **Services (0.7.28):**
  - **Throughout:** nothing ends in "…" (the figures' notes and changes, the
    chips, the card names, the funnel and cause-bar names and keys, the node
    lines, the hints). Only the page under the frame scrolls (`FRAME_CHROME`
    268 px): a second scrollbar on the whole menu means the allowance is too
    small, a page stopping short of the stage's foot too big. Verdict colours
    only on the figures NEEDS YOU judges, the systems' dots, the care and
    basic-ladder rings, a seat-bound gate chip, the utility rows' figures, the
    crime scale and "caught, not held", and the event lines.
  - **The frame (Health › Overview):** "Services › Health" with the teal
    swatch, "Services" clicking back, a pink "build them on Build ›"; the
    systems strip at the left, Health, Education, Utilities and Safety with
    their icons and 7 px dots (hover: the worst NEEDS YOU row), a "!" chip on
    a system with the month's news until it is opened; four figures with a
    blue "›" each, OFF SICK with its 64 × 16 sparkline and "▲ … pts on last
    month", THINNEST COVER opening Build › Healthcare on that ring (pink
    edge), GROUND LEFT opening Death care, THE BILL Health › Books; the page
    strip.
  - **WHERE THE SICK RATE COMES FROM:** the figure at the left; the bar's big
    parts named under themselves, the small ones in a key ("none" greyed);
    hover every part; clicks: no doctor → General care, hunger → People ›
    Household money, no home → Build › Homes, the unburied → Death care,
    violence → Safety (the floor and outbreak are not hands). **Check the
    bar's height settles**: it lays its key out by its own width.
  - **THE CARE THE CITY RUNS:** four cards (cross, child, cane, headstone), 64
    px rings, what each buys with its range, the supply bar, "details ›" and a
    pink "Build for it ›"; hover a card for a blue edge; the two doors open
    their own targets, not the card's.
  - **Health's pages:** General care's 13 long-sick bars behind a dashed "can
    die from here", the cure scale (hover for its three figures), the "Who
    gets a doctor" funnel, the fee line's "Set the fee ›" (Policy › Promises ›
    Health); Childcare and Senior care as scale rows, infant deaths on a log
    scale, Elder deaths new; Death care's ground and this month's dead (a red
    line only in the month the last plot goes); Books, cost against fees back.
    **After a load, before a month:** "seen" and "raised" read "—" and the
    funnels' "treated" "after a month".
  - **Education (in his city: the probe city had no schools):** THE BASIC
    LADDER's nodes and rings, the chip "the narrowest stage" on the narrowest
    only; Diplomas and NEW LICENCES "not recorded yet" until a month runs;
    ADULT STUDY and THE LICENSED PROFESSIONS, "not built: N would come";
    **check the lanes wrap and "Institute of technology" wraps inside its
    node**; a node opens its page. University: the funnel with the binding
    step outlined, the two half-ring gauges, the cohort bars, "Set the subsidy
    ›". Professions 2 × 2; the Basic ladder's supply bars; Books' forgiven
    tuition in grey with its (i), and no "Tuition the city covers" or "counted
    twice" in its details.
  - **Utilities:** the power row in MW (his city asks about 1.18 GW): now, at
    full staff and asked, the white "asked" tick and a faint tick at NEEDS
    YOU's line, who draws it, "short … now · … even fully staffed", and "Build
    for it ›" opening Build › Utilities **on Power**; water's door **on
    Water**; THEY EARN plain, "the city's own, after wages"; Books' power and
    water cards and "The plants are the city's own…". The road is one card
    since 0.7.29 (below).
  - **Safety:** the crime figure on its 0–2× scale with Canada's tick; the
    seven reasons' bar and its three doors; Police, Cells and What it did; the
    Police page's two bars on one scale and "One more police station: …";
    Prisons' six bars by months served and Canada's 127 in details.
  - **Elsewhere:** the drawer's OFF SICK opens Services › Health › Overview;
    Build › any city category: a teal "why ›" at the ring heading, to its
    Services page (Road capacity and Transit go to Infrastructure since
    0.7.29); Build › Utilities › Power: "generates 8.1 MW", "cheapest per kW",
    the stat cover's Electricity in kW, the ring's and the Overview tile's "…
    MW short"; water still "units a month"; the three new icons, DROP, CANE
    and CELL, drawn by hand.

  **Infrastructure (0.7.29):**
  - **The first look is special: his save predates 0.7.29.** Before a month
    runs the railway's bar reads billed at home and a grey "paid abroad or
    kept: known after a month", and "the rule allows —"; the first month after
    is B1's (the railway bills almost nothing, the quote jumps, "bigger than
    its city" may fire): the model's, not the screen's.
  - **Throughout:** nothing ends in "…" (CARS' note wraps to two lines; the
    walk's and the funnel's row names, the rule names, the freight bars'
    figures, the drawer's road figure); only the page scrolls (`FRAME_CHROME`
    232). Verdict colours only on FULL and FLOW (and the hero's figure, the
    curve dot's ring, the CONGESTED or BUSY chip) in NEEDS YOU's road colour,
    the curve's three bands, the drawer's road row, the railway's and the
    lorries' shortfalls, and a railway losing money.
  - **The frame:** "Infrastructure" with a pink "Build › Roads & transit ›"
    (the Road capacity ring picked) and a blue "The road over the years ›"
    (City History with Road throughput alone, ten years; **check the pins did
    not change**); FULL "108%" and FLOW "83%" in amber, FLOW's change on last
    month; ON TRANSIT, CARS and BY RAIL, each opening its page; the four chips
    with their icons.
  - **Roads:** "108% full → 83% flow" with its (i) and the CONGESTED chip; the
    curve and its bands, the white dot ringed in amber with its drops, **the
    pink hollow dot for his 22 road sites** and its line under the curve; FROM
    TRIPS TO THE ROAD's five rows, **the two rule names not touching and both
    lines running through all five rows**; hover every stretch; clicks
    (commuters, the transit and car rows → Transit; goods, bulk and the rail
    row → The railway; "capacity" → Build on Road capacity); the three stream
    cards; the network strip; "details" staying open through a month.
  - **Transit:** WHO RIDES and the funnel (the road's row cut at the track's
    end with "›", the "lowest" tag); **the fare row scrolls the page to the
    fare card**; "Build's transit ring reads 61%: … ride." and "Build transit
    ›"; the books card; stage a fare and see the preview and the apply bar.
  - **The railway:** the quote's gauge with its floor and lorry ticks, the
    bill three ways and its key, the fuel line; Track and trains, What it
    hauls, The business ("its books on Sectors ›" opening Rail's Income page);
    the line rule and "Rail on Build ›".
  - **Freight:** six bulk bars then thirteen goods, each with its icon and the
    white "by lorry" mark; hover for the four old figures; the freight bill
    strip, each figure opening The railway; the lorries line (or bars and an
    amber chip with a sector short); details without Materials' row of
    noughts.
  - **Elsewhere:** the drawer's "Roads 108% full · 83% flow" in amber, never
    cut (it was red "83.23% flow"); NEEDS YOU's ROADS row wrapping; Services ›
    Utilities' one road card and its door; Build › Roads & transit: the Road
    capacity ring "83% flow · N trips over capacity", the Transit ring "room
    for 63k of 102k · 41k ride", "what is on the road: Infrastructure ›" and
    "who rides: Infrastructure ›" at the heading, the Overview's road tile
    "full · 83% flow"; BUS and LORRY.

  **Sectors (0.7.30):**
  - **The first look is special:** after Continue, before a month runs, every
    investors' word is a grey "no word yet", the flow's rows say "units not
    counted yet" (the money shown), and Cash & debt's "New borrowing costs it"
    differs from "...the month's books struck it at". **Then press the clock
    once:** KEPT and each card's figure count up from last month's over 0.4 s,
    the plant's ring sweeps up from 0, the waterfall's bars grow, and a
    "Built" word flashes its line pink once; a redraw by a click does none of
    these.
  - **Throughout:** nothing ends in "…" (card names and groups, the investors'
    words over two or three lines, the figures' notes, the flow's names, the
    grids' labels, the gate words, the rule chips); **watch the cash bridge's
    figures on a sector with many steps**, which could touch. No second
    scrollbar on the list or on any business, **Retail included** (its
    investors' line has two lines; the page is sized from the frame's laid-out
    height plus 36 px). Verdict colours only on a negative kept figure and the
    moves, MARGIN, CASH overdrawn, OWES, the ratio chips, a cost above its
    price, the cascade's thinnest throttle, the investors' kinds, borrowing
    yes or no, the loss pips and "Not accounted for"; revenue, taxes,
    interest, bids and asks plain.
  - **The list:** the four figures (RUNNING AT's "›" opening Build's Roads &
    transit, Utilities or Healthcare by the thinnest); fifteen cards in
    Build's order, three columns, **the icons MILL, CAN and INGOT, drawn by
    hand, and GEAR and PICK**, the name's tooltip the blurb, the group, the
    sparkline, the running bar ("no plant standing" for Mining, Materials and
    Business Services; "95% of its homes let" for Real Estate), the investors'
    word; a card opens Operations, its word Investors; the cards of a grid row
    one height; Show more as 3 + 2.
  - **A business:** "Sectors" clicking back, the icon square, the (i), the
    pink Build door; five figures, each a door (OWES "owes nothing" with
    nothing owed); the investors' line under them on every page (Retail's
    second line for its bank branches); the chips with icons.
  - **Operations:** the flow card's three columns and chevrons; Retail's and
    Restaurants' "▸ 13 foods" opening in place and staying open through a
    month; Construction's materials "bought as it was drawn"; the plant's ring
    and cascade with the thinnest amber, Real Estate's homes-let ring, the
    empty grey ring where nothing stands; the outputs' bars and price chips;
    ITS OWN FIGURES' (i)s; the old page in "details", its plant note naming
    six.
  - **Income:** the waterfall; **click Revenue, Bought in, Wages or Sales
    tax** and its line opens in the statement under it; the ratio chips.
  - **Balance sheet:** the two bars and their keys; the "Held abroad" and
    "Other businesses' bonds" lines; ITS OWNERS, the order book behind
    "details", the chart. **And Bank › Capital & owners:** the same card at
    560 (D11), under two headings in a row ("Its owners", then the card's "ITS
    OWNERS").
  - **Cash & debt:** the bridge with "Stolen" and "Bought back its shares"
    among its steps; the reconciliation's two new lines; the rate bar, with
    **a white mark where the rate ends inside it** if his concentration charge
    is a discount; the leverage bar; the debt by kind; the credit grid; a
    banned sector's red line.
  - **Investors:** the decision card (no "Sold" or "Could not build" in
    green); ITS BUILDINGS with each first gate (a row opens Build on its
    category); WHAT STOPS IT's tiles and pips; THE RULES IT BUILDS BY as
    chips; WHAT YOU CONTROL's four doors.
  - **Elsewhere:** a build card's investors' line, NEEDS YOU's BUILDERS row,
    the inbox's "shedding" notice and Infrastructure's "its books on Sectors
    ›" still open the right page.

  **Government (0.7.31)** has not been seen rendered at all: every figure and
  word was printed by the implementer's probe (`ProbeGovWords`) on the
  playtest's 2,400- and 600-month cities, loaded from 0.7.25, a month on, saved
  by 0.7.31 and loaded fresh, a month on, and in the busy month, so his figures
  will differ; those below are the 2,400-month city's at month 2,401. The list
  is `runs/ui12-notes.md` §3 (25 items), written by the docs pass from the
  code. The same window and autosave, with the drawer closed:
  - **Throughout:** nothing ends in "…" (the five figures' notes, the ring
    keys' names, the ranked rows' names at 236 px, the bridge's step words such
    as "Transit fares: on the cash, not the budget" and "Bought land with US$…
    of reserves", the cards' lines, the payer rows, which wrap now). One
    scrollbar: the page is sized from the frame's laid-out height plus
    `STAGE_REST` 36 (232 until laid out); a second scrollbar on the whole menu
    means 36 is too small, a page stopping short of the stage's foot too big.
    Verdict colours only on SURPLUS/DEFICIT (its figure, THE BALANCE's word and
    the "kept"/"short" outline), OWED's figure (red only when priced out of the
    market), the pension chip, the central bank's ring and its arrears rows,
    the mortgage insurance's "ahead/behind" and the fund's "Not paid, for want
    of cash": **no amber "Paid out" bar, plain totals, a grey net exports,
    plain growth figures**. A true minus everywhere ("−$3.5M"), the opened
    payer rows included. A month landing leaves the opened lines, the "details"
    folds and a column's "N more" as the player left them.
  - **The header (any tab):** under the cash **"+$1.5B earned a month"** (was
    "+$1.5B a month"), green, red, or grey at nothing; **check the extra word
    costs the tiles to its right nothing at 1,389 px**. Hover: EARNED "at
    today's tax rates", not the budget's surplus nor the change in the cash,
    "which was +$2.3B". Its (i): EARNED, SURPLUS and BANKED in turn, the steps
    between them, and "Government's Overview walks from one to the next, step
    by step." **The GDP tile right after Continue: "$…B / yr" with a
    real-growth line, not "$0 / yr" and "first year"** (B1). The drawer's
    pinned vitals: Cash · Earned · Population.
  - **The frame:** the blue swatch, "Government" and its (i), which now says
    most lines open into who paid them and every line has a door (the docs
    pass's string); "Taxes on Policy ›" (Policy › Taxes › Everything) and "Over
    the years ›" (City History with Revenue and Surplus / deficit picked, ten
    years, nothing pinned). Five figures, each a door: EARNED "+$133.5M" plain
    → the bridge; SURPLUS "+$130.3M" green, "80% of what it took in", its
    sparkline and "▲ $1.4M on last month" → THE BALANCE; BANKED "+$130.9M"
    "$102.1B → $102.3B" → the bridge; OWED "$0" "nothing owed · rated AAA" →
    Finances; TAX TAKE "34.4%" → Revenue. **In his city EARNED must equal the
    header's line to the digit.** The pages as chips with icons.
  - **Overview:** the revenue ring "taken in $162.5M" with its key **adding to
    100%** (B14), "less utility income −$471k" and "outside the total: transit
    fares +$4.1M ›"; the spending ring "paid out $32.2M" with **no Repairs**
    (B2) and "outside the total: repairs −$3.5M ›"; hover an arc; **click an
    arc or a key row: Revenue or Spending opens with that line open and
    scrolled to**, "Everything else" at the page's top. THE BALANCE: "SURPLUS"
    green, "+$130.3M", taken in in the money blue and paid out in its darker
    step on one scale, the taken-in overhang outlined green with "kept
    $130.3M", "27.6% of annual GDP". FROM EARNED TO BANKED: the three tiles and
    their steps, each a 56 × 6 bar on the card's one scale, a step with a door
    in the accent; **in his city the steps must account by name for the gap
    between EARNED (+$1.5B) and BANKED (+$2.3B), "Not accounted for" $0 or a
    coupon's timing**; the "mostly: …" and "printed $X" chips when they apply;
    "details ▸" with the old statement, one column, no "of the change" (B6).
    AGAINST THE ECONOMY right after Continue showing its bars (taken in 34.42%,
    paid out 6.83%, surplus 27.59%, care and schools staff 0.18%), not the
    empty state (B1).
  - **Revenue and Spending:** the head line ("$162.5M taken in · 34.4% of
    annual GDP · January 2200"); rows of 36 px with icon squares, "▸ who" on
    the lines that open, Utility income's −$471k drawn leftward from a grey
    axis, a door at each row's end ("set it ›" to its Policy page, "Finances
    ›", "Services ›", "Build ›", "Land office ›", "Bank ›"); click Business
    tax: one stacked bar of the payers and a row per sector and the Bank,
    staying open through a month; "nothing this month: …" in grey; the totals
    plain, with the muted "Outside the budget's total" doors under them.
    Spending's twelve lines with no Repairs, then THE DEBT ("The city owes
    nothing."; the busy 600-month city's "notes $21.0M · no coupon: the
    discount ($290k) was the price · falls due whole in 5 mo"), THE SERVICES
    THAT CHARGE ("care: fees cover 77% of $1.3M · net cost $311k": **a cost
    reads as a cost**, B3) and PENSIONS (**no chip unless NEEDS YOU lists
    PENSIONS**, B11).
  - **Output:** the layers chart at 880 × 220 over the last 120 months with its
    key, "GDP this month $446.4M", "▼ 2.0% real, 12 mo", "Over the years ›";
    the four cards, NET EXPORTS with **no "Steel exported" and no "Scrap
    imported"** (B5); THIS MONTH, AS ONE BAR, **a negative part (investment
    −$129k) drawn as a grey length that adds** (docs pass flag 7); the growth
    strip all plain; "details ▸ how many months are recorded" reading **120
    right after Continue** (it read 1, in amber, before B1).
  - **Elsewhere:** Finances › OWED white unless the market has priced the city
    out (no amber at 60% or red at 120% of GDP); City History from "Over the
    years ›" showing Revenue and Surplus / deficit together, ten years in view.
- **UI NOTES FROM PLAYING 0.7.19, 2026-09-30** (`playing-0-7-19-ui-notes.md`;
  Jerus asked for a play-through for the interface only, nothing changed).
  Likely bugs:
  - the founding screen lists a 60-house village that "Found the city" does
    not build (it is the playtest's own);
  - City History opens scrolled to the bottom;
  - Esc does not open the menu from the Build tab;
  - "no year yet" still shows at month 13;
  - a building on site is invisible on its build card, and "Needs you" still
    asks for it;
  - the build-time quote drifts from ~5 to ~9 months;
  - tooltips have no background;
  - "−0.0" people;
  - GDP ratios before a year exists.

  Layout: the vertically centred block jumps under the mouse; the net-income
  bubble covers the bottom of scrolled pages; toasts stay on top of the
  strips. Wording and ideas are in the note.
  - **Jerus agreed the plan, 2026-09-30** (the note's §6):
    - text in three layers (on screen, one click away, the manual);
    - colour by area, with red/amber/green only for good or bad;
    - a header of 5–6 headline numbers with sparklines, GDP among them;
    - Yahoo-style charts: pan, zoom, range buttons, an overview strip,
      years on the axis, a legend, **full screen**, labelled crisis bands
      and event markers including the player's decisions;
    - a real construction panel, plus **reprioritise, cancel and
      player-ordered demolition** (new gameplay, mechanics to design and
      source).

    Order: bugs and layout, then colour and header, then construction,
    then charts, then the text cut alongside. Mockups first.
  - **Mockups made, 2026-09-30:** the canvas "CityBuilderSim UI mockups"
    (seven artboards: main window, construction, demolish, history, full-screen
    chart, text layers, colour; the note's §7). Waiting on Jerus's comments.
  - **Jerus, 2026-09-30: "that is damn pretty, go for it".** His answers on
    the open mechanics:
    - **Founding village:** "3. but even the screen shouldnt say what the
      money could buy, the start screen should be real simple, and also the
      menu screen should get an uprage, cause thats the first thing players
      see and currently its bland as hell". So no village is placed, the
      founding screen loses the "what it buys" breakdown, and the main menu
      and founding screen get a redesign (mockups first).
    - **Priority: "Both".** Order the city's own sites for free, and rush a
      site to the front of the whole queue by paying overtime (the premium
      to be sourced).
    - **Cancel: "Keep the half-built shell".** Termination for convenience
      (FAR 52.249-2): pay for the work and material used plus wind-down
      costs, get the rest back. The shell stays on its land until restarted
      or demolished.
    - **Demolish: "City's, plus buy-outs (Recommended)".** The city's own
      buildings freely; a business's or landlord's only after a compulsory
      purchase at market value. Demolition cost and salvage from real
      figures.
    - **Batches:**
      - 0.7.20: the bugs and the layout;
      - 0.7.21: colour, the header, the menu and the founding screen;
      - 0.7.22: the construction panel with priority, rush, cancel and
        demolish;
      - 0.7.23: the charts;
      - the text cut screen by screen alongside.
    - **0.7.20 built, 2026-09-30** (`the-page-stays-put.md`): all nineteen
      of the bugs and the layout. Two causes were not where the notes
      guessed: a showing tooltip swallowed Esc, and "no year yet" was the
      model's basket, not a year (the first rate is about month 50). The
      model is untouched.
- **CHECK 0.7.23 ON THE PC** (`the-chart-you-can-move.md` §6; `runs/ui4-notes.md`
  §2 and §9): on slot 10 or the Doom autosave, City History: drag, wheel,
  double-click, the range buttons, the overview window, the crosshair card,
  legend chips, the named recession bands (hover/click), the episode lane,
  the "you" lane's flags (change a tax first), full screen and Esc out; years
  under the Bank's and Finances' charts; Settings' key list.
- **CHECK 0.7.22 ON THE PC** (`the-player-takes-the-queue.md` §8;
  `runs/ui3-notes.md` §2 and §10 have the script): the construction page's
  three tabs; reorder two city schools; rush one and watch month 3's warning;
  cancel one (the refund in the dialog) and restart the shell; demolish a
  school (what closes, the cost, the salvage); buy out a landlord's house;
  the right panel's "Open ›"; cold-start Settings and Load over the backdrop;
  the five (i) notes.
- **DECIDE (from 0.7.22):** salvage at the full market price is generous (a
  school's 666 units fetched $12.2M against a $1.05M demolition; real
  demolition recovers far less than new material): lower it, or keep the
  0.7.8 rule as Jerus set it? And the player's own demolitions are drawn in
  failure red.
- **CHECK 0.7.21 BY EYE ON THE PC** (`a-front-door-and-a-dashboard.md` §6;
  the full list is `runs/ui2-notes.md` §2, "On the PC"): the fonts (the log's
  "Fonts:" line), the palette, the title swatches, Build's dot key, the rail,
  the six header tiles at 1920 and 1366, a tile's click into History, the (i)
  popovers, the menu over the skyline with Continue and YOUR CITIES, the
  founding panel, a sector page's money, the cash white unless overdrawn.
- **FOR 0.7.22, decided while Jerus slept (2026-10-01):** cold-start Settings
  and Load go over the menu's backdrop too; the text cut starts with the five
  worst paragraphs (`a-front-door-and-a-dashboard.md` §5); the GDP tile's
  tooltip could say why a young city's real growth swings. For 0.7.23:
  History's multi-line series still use red, green and amber.
- **CHECK 0.7.20 BY HAND: Esc only** (`the-page-stays-put.md` §6). The rest
  was checked by eye on the PC on 2026-09-30, with two fixes. The remote
  control's Escape never reaches the game, so: on the Build tab with a
  tooltip showing, Esc opens the menu; Esc closes the Quit question and the
  receipt; Esc on the founding screen goes back. Also unseen: the header's
  price tooltip, and a toast (no urgent notice came up).
- **JERUS DECIDED (from 0.7.20), 2026-09-30:** "Keep the countdown" for the
  first inflation reading (no model change); the other sector pages' money
  form "With 0.7.21's text cut"; BuildMenuCheck "Update to the taxed price",
  and the cloud gate runs it from now on (in 0.7.21); the price card's
  "against what it cost at founding" "Leave it". The questions as asked:
- **ASKED (from 0.7.20):**
  - **The first inflation reading comes about month 50** in a Standard city
    (the basket is fixed after `PriceIndex.SETTLING_MONTHS` = 24 months of
    shopping, then a year of readings; inflation reads as zero everywhere
    until then). Keep the countdown, fix the basket earlier (a model
    change), or add a provisional reading (a new measure)?
  - **The other sector pages' money form** (`Formats.amount()`, only
    Construction now): with 0.7.21's text cut, or now?
  - **BuildMenuCheck fails 73 checks, since 0.7.19**: its price check is
    the template's cost plus material, but the quote carries the builders'
    sales tax (House 524.7 against 446.0, 1/0.85). It can run in the cloud
    with the Windows JavaFX jars. Rewriting its expected price to include
    the tax is a premise change, so it waits on Jerus.
  - The price card's "against what it cost at founding": the 1.00 is the
    month the basket was fixed (about month 37).
- **CHECK 0.7.19 BY EYE ON THE PC** (`the-price-keeps-up.md` §7).
  - The Build tab: a card's price row shows the work, then the price all in; a
    dialled city order reads "$X with $Y sales tax, back to the treasury as it
    is built"; the Commercial Bank card, "Another opens only while there are
    more than 16,000 customers for each one standing".
  - The Construction page: "Material escalation", "Paid a point of work", the
    How it bills note.
  - The Bank tab: "What it can lend against" (all deposits); customers a
    branch "against the 16,000 one serves"; a branch's fees against what a
    branch past the first cost last month; the charter note ("…pays its staff
    and repairs but no running costs"); "Do its branches still pay?" and the
    lag's sentence. Near break-even, see whether "the fees would cover another"
    sits beside a per-branch figure under the cost (found by the docs pass).
  - The Government tab's arrears can show "Material escalation on the city's
    building contracts".
  - The Schools page: a new game starts on FIXED (amount × the price index);
    his save stays on its own basis, switchable on the Policy screen.
  - The Luxury page: "who would buy at the price charged".
  - The founding screen: invoices "with the builders' sales tax on both"; the
    custom floor $6M.
- **CHECK 0.7.18 BY EYE ON THE PC** (`workers-take-the-best-paid-job.md`).
  - Every sector's investment line: "the city could staff X% of a Y; it wants
    80%", now also on Retail, Luxury Retail, Restaurants, Industry, Materials,
    Agriculture, Food Processing, Heavy Industry and Mining; Construction's
    "N months of work queued, but the city could staff…". "No one could staff
    a Y's Z posts" should never appear.
  - A new city's first months: the first shop and bakery wait for spare hands.
  - People screen, skill ladder, in a short city: "<bands> fill as one market
    this month, their wages headed for the same level: … (N over-qualified
    workers hold unskilled posts)"; the queue note's "…less any drawn further
    down by better pay".
  - People screen, "Why people come": "…every band above one is paid the going
    rate or less…" (it lists no labourers).
  - People screen, "Moved in": the No-diploma row lit when labourers are paid
    over the going rate; the note "A diploma is what an ordinary arrival
    has…".
  - Services tab, High school: "…unless a dear unskilled wage brought them
    in".
  - The summary's "N% have no diploma — only a dear unskilled wage brings them
    in…".
  - Wages in a short city: unskilled and diploma wages converge, hospitals'
    diploma posts empty first, the first year costs more.
- **CHECK 0.7.17 BY EYE ON THE PC** (`the-crew-a-building-can-use.md`).
  - **First, play a new city that orders its depots beside a coal plant.**
    Under the shipped rule the depots get a fiftieth of the plant's crew and
    ForeignCheck's founding sat at about 1,000 people for 15 years. Say
    whether "small orders wait" is acceptable as a player sees it.
  - Construction's operations page: "Crews kept on: X of Y posts" (amber at
    the core crew), "Staffed", the note.
  - A sector's wages detail: the posts the payroll is struck on, and "N of its
    M posts are laid off this month…".
  - Real Estate's investment page: "One order at a time: no"; "Work it holds
    on site: N months, of 12 it may hold"; the step-down note. Every other
    sector's page: "…finished inside 12 months at the share of the builders it
    would get…".
  - The construction panel: "Output: … (crews kept on: X of Y posts)"; "N
    site(s), M building(s): S after repairs, crews by what each building can
    use"; each site's "~N mo".
  - The build screen's quote: "under a month", "~N mo" or "stalled" — long in
    a city with a long queue.
  - The advisor: "a X would not fit in 12 months of work on site, a Y does";
    "N months of work on site already, of the builders' output".
  - Loading an old save: the log prints "Cleared N construction points parked
    on sites past what they owe."
- **CHECK 0.7.16 BY EYE ON THE PC** (`the-year-book-as-csv.md`). Reports tab,
  "Write the year book":
  - the screen note's new sentence;
  - the message: the folder once, then each book's three files (middle dots,
    the indent, no wrap inside a file name), and it survives a month;
  - six files in the game's folder, and `.bak`s beside them from the second
    press;
  - `year-book.csv` in Excel: columns split, numbers right-aligned (including
    `1.23e9`), blanks empty;
  - press again with the CSV open in Excel: that file fails; read the line's
    wording (the docs pass expects Java's long "being used by another
    process" text);
  - a new city: header-only CSVs.

  Jerus to decide, small: an episodes CSV (about 20 lines); full precision in
  the CSV against the text's three figures; whether a failed `.csv` line says
  "close it in Excel and write again".
- **CHECK 0.7.15 BY EYE ON THE PC** (`the-central-bank-as-backstop.md`).
  - **The strip's inflation readout.**
    - At the default 2% target: 5% grey; 6% amber; 7.1% red (amber
      before); −1.5% amber; −11% red.
    - At a 15% target: 14% grey; 19% amber; 21% red; 11% amber; 5% amber.
    - The tooltip's line on the colours.
  - **Policy tab, money page:** the target's ladder "0% to 20%", + to 20%,
    the rule's sentence and the "rule disagrees" alert at a 15–20% target.
  - **Policy tab, holdings:**
    - eleven chips 0–100% (check the row wraps at `SIZE_CAPTION`);
    - the ladder to 100%;
    - the note's last sentences: rolling its own, runoff past its dial,
      what the surplus pays.
  - **Finances, "Your rate":** with the central bank holding paper while
    the bank is at the window, the floor row reads "the dial, and the
    bank's cost on the rest", with a note naming the share. With no holding
    the page is as before.
  - **Figures that now read the split floor:** "A spotless city would pay",
    the Policy tab's floor lines, the credit rating letter, the build
    screen's quote colouring.
  - **Finances, "Rolling what falls due":**
    - "...of it the central bank's";
    - "...which it rolls itself, at issue" / "...which is repaid it";
    - "...which last year's surplus pays off";
    - the plan's sentence with the central bank's par on top, or "issued
      it alone".
  - **Finances, the book page, "Who holds it":** the note on the
    households selling to the central bank.
  - **Insane:** play runs before borrowing, with no orange refusal; the
    founding card's new note.
  - **The time skip's result:** no "THE SKIP STOPPED" section, an amber
    "Central bank $X advanced, $Y owed at the end" line, and "Stopped after
    n of N months" only when a month threw.
- **CHECK 0.7.14 BY EYE ON THE PC** (`the-city-takes-the-shares.md`). None of
  it can run in the cloud; `build-ui.sh` compiling it is the only check it has
  had.
  - *The fund's two pages below (Holdings, By hand) are superseded: 0.7.39
    redrew the fund as four pages and a page a security, so check it under
    CHECK 0.7.34 TO 0.7.39 instead.*
  - **Finances landing:** a sixth row, "The city's fund": its value, and last
    month's transfer or its shortfall, or the dial.
  - **The city's fund, Holdings:** the equity share against the aim; shares by
    company (value, the share owned, the rescue book's part); bonds by issuer;
    the rescue book (rescue shares, the preferred, its arrears, the warrants,
    the stake); the dial (±10 and ±50 steps, the rules sentence, this year's
    surplus, the reservation, the floor, the last pay-in); the transfer lines;
    the rescue block.
  - **The city's fund, By hand:** the amount stepper; Pay in and Draw out,
    disabled at nothing; the company chips with fair value, holding, Buy, and
    Sell a quarter / half / all; the issuer chips with each bond's line; the
    orders waiting; a city with many bonds lists all of one issuer's.
  - **Finances, both borrow pages:** "When the bank fails" under the rollover
    block (two chips, the sentence, the alert and button while the bank
    waits, the last resolution's note); the rollover block's "…netted already
    this year, or kept for the fund" and the reservation line.
  - **Bank landing:** the failed alert's new text; the resolution block and
    "Resolve the bank for its shares - $X"; the preferred offer block while an
    offer waits (Jerus's sentence, the capped note, the terms, the two funding
    offers and the circular-capital alert when short, Accept lit only when the
    treasury holds it, Decline).
  - **Bank, Balance sheet:** "Preferred shares (the city)" above paid-in and
    retained; it must still foot ("Not accounted for" absent).
  - **Bank, Capital & owners:** four new causes in "How its equity moved"; the
    city's stake and its note; "Its rescues" ("What the city has paid to
    rescue it", the pre-0.7.14 absorbed line only when not nothing, the last
    five resolutions); "The city's preferred" with "New shares it sold to
    repay the city, over its life".
  - **Government, Revenue:** "Transfer from the fund" at the end, and its
    detail.
  - **Inbox:** "The bank asks the city to buy its preferred shares" with
    "Answer the bank →"; "The bank failed, and the city took it over" with
    "See the bank's rescue →"; the offer pauses a running clock with
    pause-on-events on.
  - ~~**The clock on an Insane city before it borrows**~~ — the refusal and
    "THE SKIP STOPPED AT MONTH n" are gone since 0.7.15; see its list.
  - **Found a city:** the Insane chip first; its card (D$0 in red, the
    borrow-first note, the village, both day-0 quotes, the land bond in US$,
    the vault); the first time Insane is picked a scratch city is founded to
    strike the quotes (about a second).
- **CHECK 0.7.13 BY EYE ON THE PC** (`rolling-what-falls-due.md`). None of it
  can run in the cloud.
  - *The land office's parts below (the tiles, the FREE cell, the next-N row,
    the funding page and the receipt) are superseded: 0.7.26 redrew the
    office, so check it under CHECK 0.7.24 TO 0.7.27 instead.*
  - **Land office tiles:**
    - the large price in local money when converting and US$ from the
      vault, with the other as a caption on the same baseline;
    - km² under sq ft, still inside the tile's 172 px;
    - a short tile's button reads "Buy — borrow for it" / "Buy — the vault
      is short" and is clickable.
  - **The FREE cell and the smallest-lot sentence in km²; the Summary's
    land panel (Owned / Free), the build screen's NOT ENOUGH LAND page and
    the time-skip report's "Bought" line in km².**
  - **"Buy the next N plots" row:** −/+ (disabled at the ends), the total in
    the paying currency with its caption. Check it at a narrow window.
  - **The funding page:**
    - converting: the build screen's two offers and Cancel;
    - from the vault: every dollar figure in US$, the "dollars are in
      reserve" note, the top-up block;
    - the window shut: the red alert with its reason;
    - the note offer says it is "refinanced then by the treasury's
      rollover" when the rollover is on;
    - the receipt after a multi-buy.
  - **The build screen's INSUFFICIENT FUNDS page:** it should look exactly as
    before (a refactor shares its pieces).
  - **Finances, both borrow pages:**
    - "Rolling what falls due", its three chips (By hand / Same structure /
      12-month notes), the sentence, and next month's lines (falls due, last
      year's surplus, netted, "Raised as…", the face);
    - the summary sentence and the last rollover;
    - on the shut Abroad page the block sits above the alert;
    - "nothing falls due" when nothing does.
  - **Bank:**
    - the strip's six chips wrap cleanly; the landing's "Balance sheet" row;
    - the page's "this month" / "a year ago" columns, each line opening, the
      by-sector detail, the totals;
    - paid-in and retained opening into their sentences, "Its equity" as
      their total;
    - no "Not accounted for" when it foots; the memorandum;
    - "—" and the note on a young city or a 0.7.12 save.
  - **A new game:** the monetary page shows the autopilot on and Finances
    shows "Same structure". A 0.7.12 save keeps its hand on the dial and the
    rollover by hand.
- **CHECK 0.7.12 BY EYE ON THE PC** (`the-firms-sell-bonds.md`). None of it
  can run in the cloud; `build-ui.sh` compiling it is the only check it has
  had.
  - **Finances:** the landing's "The bond market" row; The bond market →
    Every issue (the issue table, * for untraded, amber past 0.5 points; the
    holders table, the totals and notes, "A defaulted bank loan gets back more
    of what it is owed than a defaulted bond does"); a bond's book (the chips,
    up to 12; the terms; the bid and ask grids, 12 levels; the empty cases).
  - **Sector → Cash & debt:** the bond lines in "Where the cash went" (no "Not
    accounted for"); "Its bank loans and its bonds"; "Other businesses' bonds
    it holds"; "…its bondholders lost"; "Loans and bonds outstanding" and the
    "owes lenders" cell; "…of it, interim financing" under "Loans running",
    amber, "N loans, ranked first"; "Forgiven by its creditors" only for a
    backstop (is "by its creditors" the honest label for money created?); the
    month after a sector's first plant reads as ordinary borrowing.
  - **Sector → Its owners:** "Last traded at" with its month, or "Not traded
    yet: at fair value", amber/green past the tolerance; the fair value, the
    yield on the dividend paid, "Traded this month"; the bid and ask grids (5
    levels) or "nobody is bidding / selling"; the company's own buyback resting
    at fair value plus 10% at the top of the bids — check it does not read as
    the market's price; "Dividend this month" standing alone (no special
    dividend); the share chart's legend "The last trade" and its note.
  - **Bank:** Profit (bond interest, underwriting fees, gains, the year block
    — it must foot); Lending (the weight table's two rows, "What its
    concentration costs", the bonds it holds, "…of it, interim financing",
    the working-capital line's sentence); the ladder's rung per issuer; the
    trading desk (its orders on the book, the two cap lines "…of the bank's
    equity: X% of a 50% cap" and "…its largest holding, *company*: Y% of a 25%
    cap", amber past a cap, ending ", $X over it on offer at fair value" —
    check the wrap beside a long company name); the status line and lending
    stance, both longer.
  - **People → a household:** "The businesses' bonds", coupons, sold; "Shares,
    at the last trade"; net worth and cover including them.
  - **Trade:** the world and the businesses' bonds; "The city's shares in
    foreign hands", at the price.
  - **The investor's words:** "Holding: it owes 1.62 times what it owns, over
    its last quarter, past the default point…"; a distress sale of plant that
    makes a second good ("Sold 1 Shared Services Centre - 31 months of losses…").
  - **Luxury Retail:** "What a piece cost the shop" reads the import price
    every month, its margin and ticket smooth; no two-month ripple in GDP.
  - **Nothing on screen says a sector could not pay for its stock** — decide
    whether a "did not order" line is wanted; a banned kitchen can sit at
    nothing on hand through its ban.
  - **A shell** (Mining late in a city with no ore sites): the log prints
    "…could not pay its bills… the bank lent the $0k still unpaid as interim
    financing" every month for hundreds of months; its screen shows no plant,
    dust in interim financing, and the ban restarting. Check what the player
    sees.
- **CHECK 0.7.11 BY EYE ON THE PC** (`the-landlords-take-a-mortgage.md`). None of it can run in the
  cloud.
  - **Bank tab, landing:** "Capital & owners" reads "X% leverage" while the
    leverage ratio binds and "X% capital" otherwise; the capital cell switches
    CAPITAL RATIO / LEVERAGE RATIO with its target and minimum; the status
    sentence reads "of everything it has lent (the leverage ratio)" and 3%
    while that binds.
  - **The ladder:** "An insured mortgage (10 years)" - the policy rate, the
    term premium and window share, "running the bank X", "the capital the 3.0%
    leverage minimum ties up Y", no expected loss; the ladder's top reaches
    it.
  - **Lending:** "The landlords' insured mortgages" (count and owed, average
    rate, payments a month, principal repaid, renewing within a year, all
    insured, claims this month and over the city's life, the CMHC note); the
    quotes row; the weight table's "Insured mortgages" row at 0%, the
    businesses row net of it, the footing, the note's sentence on the face and
    the leverage minimum; the trouble table's "in total" net of the insured
    part.
  - **Capital:** the bar marks the binding measure's minimum, target and top,
    the tooltip gives both ratios; the block "...and against everything it has
    lent"; the under-minimum alert names the leverage minimum when that binds.
  - **Funding:** "What it can carry"'s capital label when the leverage ratio
    binds; the under-minimum verdict; "Do its branches still pay?" and "It has
    closed N this session".
  - **Vitals on every page:** CAPITAL / LEVERAGE RATIO and its target.
  - **Real Estate's screen:** "Its insured mortgages" (owed, other debt, rate,
    payment, principal repaid, next renewal as a date), the insured write-off
    line, the conditions (15% down, 1.20×). **Every sector's owners note:**
    "...pays out 40% of what a month's profit leaves after the principal it
    repaid"; the bank's "...X% of its weighted book or Y% of everything it has
    lent, whichever asks more".
  - **Government tab:** "Mortgage insurance premiums" under revenue and
    "Mortgage insurance claims" under spending, with their detail.
  - **Advisor lines** (check the × glyph renders): "Built N X on an insured
    mortgage (trimmed from N - ...)"; "Holding: needs $X of its own for the 15%
    down payment on a X"; "Declined X - its rent would cover the mortgage
    0.88×; the lender asks 1.20×"; "Holding: the bank has failed and writes no
    mortgage - X would need one"; "Declined X - the mortgage would leave it
    owing past 1.50 times what it owns"; "Holding: the bank is short of capital
    against everything it has lent - X would need a mortgage"; the log's "The
    bank closed a branch: ..." and retail's "Sold 1 Commercial Bank" on the
    demolitions list.
  - **A slot-10 city saved mid-term with a branch streak running, then
    reloaded:** Real Estate's next renewal and payment, and the Bank tab's
    "not for N months", do not move.
- ~~**DEPLOY 0.7.10 WHEN THE PC IS BACK**~~. Done 2026-09-24 as tag 0924b:
  115 files, every one verified byte for byte on the PC. Rebuild in NetBeans
  (Clean and Build), then run `AllChecks`; 60 should pass.
- ~~**JERUS'S CALLS FROM 0.7.10**~~. He answered all three on 2026-09-24
  (`founding-a-city.md` §5):
  - keep D$100M, so the water plant is a bond from day one (its invoice is
    D$109.6M, more than the treasury);
  - keep US$25M;
  - add a 20-year bond to the build screen. It was built the same day: the
    bond comes first on the INSUFFICIENT FUNDS page, sized so its cash covers
    the gap, and the 6-month note is beside it.

  Left open from it, all small:
  - the bond is listed first even for a tiny order, where its fixed fee makes
    it a poor deal (a recommendation per order would come from the model);
  - the sized bond leaves the treasury about D$2k after the order;
  - the note's and the bond's granules are not rescaled by a currency reform,
    and the note's is still the literal 1000.0 on the screen;
  - the build card's "short $X" still subtracts on the screen (it could read
    `Game.buildFundingGap()`);
  - `NewGameCheck` §11(c)'s bound (one granule plus the fees) is looser than
    the solver, whose worst overshoot is 0.80 of a granule.

  Lean is still hard: half its seeds run dry around month 140.
- ~~**CHECK 0.7.10'S FOUNDING SCREEN BY EYE ON THE PC.**~~ Checked by Jerus
  2026-09-24: "found menu tested, all good". None of it can run in
  the cloud.
  - **Start New Game** opens FOUND A CITY. The defaults read:
    - Danzik; "the Danzik dollar, D$, DAN";
    - Standard lit;
    - the treasury D$100.0M, the village D$32.9M and D$67.1M left;
    - wind, school and police "in cash" in green, water "a bond for D$42.6M"
      in amber;
    - the vault US$25.0M with its sentence;
    - world 1.0% lit, "about 1.15x".
  - **Typing a name** changes the money line on every keystroke and keeps the
    caret. "Arden" gives "the Arden dollar, A$, ARD". An empty name greys
    Found. 25 characters is refused.
  - **"Name the currency myself"** shows the fields: "AR" is refused, "usd" is
    refused, and "crown" with "arc" gives "the crown, C$, ARC".
  - **Presets:** Lean shows -D$7.9M left in red, with every work a bond.
    Wealthy is all in cash. Custom opens on 100 and 25; "abc" and 4 are
    refused; a vault of 0 reads "No vault…".
  - **The five world chips** move the lit chip and the note.
  - **Keys:** Enter founds only when Found is lit. Esc and Back go to the menu.
    "Found with defaults" founds Danzik on D$100M and US$25M whatever is typed.
  - **After founding:**
    - the window title reads "Arden - CityBuilderSim 0.7.10";
    - Settings has no world block;
    - the Load and Save lists read "Arden - Month …", and an old save "Danzik - …";
    - the founders' notes read this city's vault (an old save US$1.0B);
    - every screen that prints the money names it: the rate strip and its
      tooltip, the Land office, Trade, the Reports axis, the currency reform
      and the year book.
  - **The build screen's INSUFFICIENT FUNDS page**, on a Standard city about a
    year in that asks for a water plant:
    - "Funding required" in red, with the order, its price and the cash on
      hand under it;
    - the 20-YEAR BOND block first: "4.2x% yield · 1.6x% coupon", face about
      $40M, the cash it brings in green, "$56k a month", the cost in all,
      "Paid over 20 years: the coupon every month, then the whole $40.3M at
      the end.", and "Issue the 20-year bond";
    - then the 6-MONTH NOTE block: "a year, taken as a discount", "none - it
      pays no coupon", "Falls due in 6 months…", and "Issue the 6-month note";
    - the bond's credit line may come out amber beside the note's green, so
      check that it doesn't read as a warning;
    - the bond button orders the plant and leaves about D$2k, with a TERM
      bond, 20 years, on Finances. The note button and Cancel behave as
      before;
    - no horizontal scroll, and the figures refresh with the clock.
- **CHECK 0.7.8'S AND 0.7.9'S SCREENS BY EYE ON THE PC** — none of it can run
  in the cloud; `build-ui.sh` compiling it is the only check it has had. Open a
  played city and a fresh one on the Bank tab, and let the clock run a few
  months on every page.
  - *The landing.* The status sentence one bold line (two at most), green,
    amber or red; under it two rows of four cells; the CAPITAL RATIO cell's
    small bar with three ticks (minimum, target, top) — the cell may be taller
    than its neighbours, say so if it pushes the bar out of line; captions
    "the last 12 months", or "the last N months" in a young city. The scroller
    fills the stage under the scorecard with no outer scrollbar when there is
    nothing to scroll and no empty band (the chrome is 330 at
    `showBankMenu()`'s `ui.scrolled(column, 330)`). The five rows each show a
    headline and a small line, and open their page at its top.
  - *The ladder.* Bars from one x, growing to the dearest rate; every figure
    "x.xx% a year"; each caption a signed step in points; the policy-rate row
    blue with "›", lit on hover, landing on Policy › Money › The policy rate;
    tooltips on "What a loan's money costs it" and "The carry trade"; sectors
    only if they owe or are shut out; each sector rung "X on prime: its own
    expected loss Y - Z of its firms default a year at L× its assets[, and its
    record W]" on one line at L ≥ 1 with a record — L and Z the last
    quarter's, which the price is struck on ("...over its last quarter"); a
    sector just written down whole quotes on its restructured books, not on
    the quarter before. The quotes grid's leverage column is the quarter's too;
    the Lending table's is the month's, which the defaults read.
  - *Every page.* "THE BANK — PAGE", four vitals, the chip strip with the
    current chip lit. **With the clock running, the page must not change, the
    scroll must hold, and an opened line must stay open.** "The bank at a
    glance" and the rail's bank icon both return to the landing.
  - *Profit.* The two-column statement lines up with the sector pages'; bold
    totals larger, last month grey; "Interest earned" opens into six rows of
    two columns that add to the line; "Provisions" three rows and the note;
    "The trading desk" with "−" on the bought lines; "Kept in the bank" red
    when negative; the twelve-month block and the ratios under it; a save from
    before this build shows "—" for last month and "Its first month on
    record".
  - *Lending.* "The businesses, one by one": eight columns in the 560px width
    with nothing overlapping or clipped (owed / pays / leverage / a year /
    stage 2 / set aside / this month), "owed" the first money column; "a year"
    reads "under 0.1%", "2.1%", "all" or "—", amber past 0.90 and red from
    1.50 like leverage; "stage 2" a share ("37%" or "—") in a wide-enough
    column, amber in stage 2; "this month" red only past the default point or
    for a sector that went under this month; a shut-out sector's name red, no
    status column; the note one wrapping paragraph ("under 0.1% at 0.60, 2.1%
    at 0.90, half at 1.50" … "starting again from the month it is written down
    whole; the defaults read the month."), nothing about a limit per business
    or debt held abroad; a sector written down whole not watched or in stage 2
    in the months after. The families' block only when they owe. "What it has
    set aside": "a year's expected defaults on a sound borrower, never under
    0.4%, and a loan's whole term of them on one in trouble". "Who has stopped
    paying": this month / in total / went under ("never" or "N time(s)") /
    status; sectors under $1 written off with no backstop or ban left off;
    "this month" the same after a reload. "How the next loan's rate is built":
    the grid "" | leverage | its own risk | its record | it pays at widths
    {150, 70, 100, 80, 110}, each owing sector then the families and the carry
    trade, the note ending "…a business decides whether to build at that
    rate."; a sector with no assets shows the whole curve (64.46%) — right by
    the rule, and may read as a bug. The weight table's five rows, the desk's
    shares last, and a total equal to "Weighed for risk and term".
  - *Funding.* "One branch reaches" $250.0M in a city that never reformed, a
    hundredth after 1:100; within reach plus beyond reach equals the city's own
    savings; the savers' block explains the share, amber with a "margin could
    not pay" sentence when rule 2 held it; the funding-mix bar has a key; "What
    it can carry" reads "nothing - it has failed" for a failed bank; the branch
    verdict one sentence, "Build a Commercial Bank ›" opening Build ›
    Commercial.
  - *Capital & owners.* The wide band bar's three labelled ticks, the target's
    and the top's labels apart; "It chose the minimum and …" one sentence;
    "What it does with its profit": the note's last sentence ("Its desk buys
    its own shares back only with what it holds over its target … at the
    desk's weight.") wraps, and "Its own shares bought back this month" reads
    sensibly beside "What it held over its target"; the equity's movement lists
    only the causes that moved it, then "Not accounted for $0" in grey, then
    the total — **a red alarm there is a bug to report, with the month**; the
    owners block with its share-price chart, then the rescues; a failed bank's
    rescue block at the bottom and at the top of the landing, the button green
    only when the treasury can cover it.
  - *History.* Six charts, the rate and capital charts' axes and legends in
    percentages, not money; months under the target, under the minimum and
    losing money, each "N of M"; the worst year of provisions; "Nothing to draw
    yet" under two months.
  - *No bank, and a failed bank* (the held-10% playtest slot, or a save where
    it failed): the landing's alerts, the red status sentence, "failed" in the
    CAPITAL RATIO cells.
  - *The sector screen.* "Its firms that default a year" only while it owes,
    amber past 0.9 and red from 1.5; "What its lenders have lost" once $1 is
    written off, with "Times it went under whole" and the grey note; the "It
    cannot borrow" alert "This sector went under - it had nothing left…"; the
    rate note (round 2's long sentence); the cash flow's "Material bought from
    scrapped plant" (Construction) and "Scrapped plant's material, sold to the
    builders" (the seller), the premises line alone when there was no sale,
    and "Stock used, paid for when bought" (Construction, in months it drew
    salvage); the bank's owners note on its capital policy.
  - *Construction.* The income statement's inputs line opens to "Material from
    scrapped plant"; the operations panel's "From scrapped plant, its own: N on
    hand, M built with this month" and the materials note.
  - *Elsewhere.* The advisor's "Declined X - not even one would cover its
    interest at N%, the rate its own risk costs at the debt it would take on",
    "Sold N X - … (plot back to the city for $Y) (its material to the builders
    for $Z)" (two brackets on one line), and the two "Holding: the bank is …"
    lines when its capital refuses a plan; the Policy tab's shut-out line "… A
    sector that went under cannot borrow to build, and a subsidy is the only
    thing on this tab that reaches it."; the inbox's "Businesses are going
    bust" — a two-column body with a 16-character sector column lined up in
    the inbox's font, three lines for a sector that went under and two for one
    past the default point, the allowance-and-profit pair only with a bank,
    "See who owes the bank →" opening Bank › Lending at the top, about once
    every two to five years in a normal city — and the failed-bank notice's new
    body; the time-skip report's "Defaults $X written off by lenders", amber
    only when the skip lost more than `BASE_LOSS_RATE` a year of the
    businesses' debt, with "Lenders wrote off debt that could not be repaid."
    only then; the strip's no-bank tooltip (window money) and the branch
    advisor's no-bank reason.
- **CHECK 0.7.7'S SCREENS BY EYE ON THE PC** — none of it can run in the
  cloud; `build-ui.sh` is the only check it has had. ~~The Bank tab: the
  landing's lead line and its "What it can lend" row (prime on the dial), the
  vitals' PRIME cell, the gauge (comfortable, nearly full, past what it can
  carry; prime in the hole; the key's three lines), "What its prime is made
  of" under it, the no-bank and failed-bank alerts, the Deposits note (35% to
  90% of the policy rate), the Funding page's "0.25-point penalty", the Income
  page's Fees line opened into its three, the tax note's late-profit sentence,
  and the Strain history's prime-against-the-dial chart.~~ — the tab was
  rebuilt in 0.7.9; the item above covers it (and the strip's no-bank text
  changed again in 0.7.8). The Summary's THE
  BANK flag ("n% lent" past capacity) and the dashboard's bank line; the
  Finances rate page with no bank row, and the issuing page's bank appetite;
  the sector page's "The bank's prime is"; the Policy page's savers preview and
  the note under the rates; the strip's "bank" tooltip; the Reports picker's
  four new traces.
- **CHECK 0.7.6'S SCREENS BY EYE ON THE PC** — none of it can run in the
  cloud; `compile-all` is the only check it has had. The ladder on every
  page that has one: its width (the reading held at 118, the slider taking
  what the buttons leave), the ends line ("x to y · one step is z · it is w",
  and "set at once" on the monetary page's target, holdings and ceiling), the
  "−" and "+" greying at the ends, the reading in accent off the city's
  value, the chips still beside the three that had them. The Schools page:
  "Every school at once" and the nine rows — each ladder beside its four
  figures, a kind with no school greyed and reading "no school", moving every
  school at once clearing the per-kind staging, the preview's line per kind
  moved. The Reports page: the "layers" chip on the real-GDP small chart and
  on the big chart's reading when real GDP is picked alone; the stacked chart
  lining up with the line chart (same plot area, the y-axes the same width);
  the three layer colours against the line; the key and its caption; the
  crosshair's four part readings; the log chip refusing while the layers are
  on. The land office: the chip pair, the sentence under it and the receipt
  after a purchase (a short vault's included), the two prices on each tile,
  the US$ market cell, "Not enough in the vault or cash" (superseded by
  0.7.26's redraw: check the office under CHECK 0.7.24 TO 0.7.27). The
  Exchange page's
  "Spent on land" lines; the Government tab's "Land bought" opened into its
  three rows; the history's land axis in US$.
- **CHECK 0.7.5'S SCREENS BY EYE ON THE PC** — none of it can run in the
  cloud; `compile-all` is the only check it has had. Jerus, on the Reports
  page: the default trio on two axes — both plot areas on the same pixels,
  the right-hand axis on the right, the bottoms level, each line's colour the
  same as its reading's swatch; the crosshair's line and box (the box flips
  to the left near the right edge; "(n months averaged)" on a long window);
  the recession bands behind the lines on all three charts; the episode ticks
  under the x-axis at the right months, their names on hover, the caption
  line; "log" — gridlines labelled as real values, the chip greyed with its
  reason when a line touches zero or three units are picked; "pin" and
  "unpin", the older pin dropping, the pins surviving a restart and another
  slot; the picker's groups closed except the picked ones, the filter box
  keeping the focus through a month's redraw and Enter handing it back; a
  group opening or a preset pressed leaving the chip under the pointer. On the
  build page: Enter with three cards pending builds all three, and stops at a
  refusal with the rest still dialled; Backspace and Delete clear; Enter with
  nothing pending still presses a focused button; the caption comes and goes;
  "−" on an empty card reads 0; the licence refusal screen lights Build.
- **CHECK 0.7.4'S SCREENS BY EYE ON THE PC** — none of them can run in the
  cloud; `compile-all` is the only check they have had. The monetary page's
  "What the rule aims at" (the chips, the half-point ladder, the sentence
  struck at two targets); the strip's third panel at a 1280-wide window (the
  brief's bar: it must not widen the strip past that, and the date and the
  money stay the loudest things); the sector list's sparklines, "no history
  yet", the workers line and "Show more" (the second row's five cells are
  sized off `STATEMENT`); each tax page's own base lever and the Everything
  page's three bases and "Every tax at once" (including staging it when the
  three have parted, where it reads "three rates"); the "at its legal maximum"
  flag naming one, two or three taxes; the bank page's "It charges".

**NEXT, AND IT IS 7.0: THE MONEY SUPPLY.** Jerus, 2026-09-21, reading his
0.6.9 city's decade book (prices 399x founding in 25 years, the currency at
its 100x guard, average inflation 29% a year): *"next up we are going to have
to delve into M2 supply and all, which will be 7.0."* What the decade book
showed, read against the model's own rules (`ForeignAccounts.repriceCurrency`,
`LabourMarket.COST_OF_LIVING_PASS_THROUGH`, `PriceIndex`, `Bank.ratePremium`):
**the model has no nominal anchor.** The currency drifts by the full
inflation differential every month (`rate *= 1 + (local - world)/12`, unbounded)
and is pulled toward a parity that itself rises with local prices; imports
reprice through the rate one for one; wages chase the whole index over two
years; rent follows the wage; the shelf is cost-plus on both. Every link in
that ring has a gain of one, so a shock never decays, and every force against
it is capped - the rate differential's support at 0.8 x 2% a month, the policy
dial at 25% (the Taylor advice at 45% inflation is 67%), the reserve's
absorption at 85%. And the money to pay 400x prices is simply created: past
its capacity the bank funds itself abroad, at 5.5x capacity in that city, so
lending is limited only by the premium's 18 points. 7.0 is the anchor: a
central bank that holds an overnight rate by operating in the market (the term
structure `DebtManager.CITY_DISCOUNT`'s note already promised — built in 0.7.0
and 0.7.1, and the constant is gone), reserves that
constrain the bank's book, and M2 as a series the player can see and the price
level answers to. Until then, the two cheap questions are ~~whether the PPP drift
should pass through less than one for one~~ — closed 2026-09-22 (0.7.2): the
drift is deleted; the inflation differential reaches the rate only through
parity and the trade balance — and ~~whether `MAX_POLICY_RATE` should
be allowed above inflation~~ — done 2026-09-22 (0.7.2): `MAX_POLICY_RATE` is
1.00, a guard against a typo, and the rate's support reads the real rate
uncapped. Not tuned; Jerus's design. *~~The second is answered
(2026-09-21, the same night): the cap stays at 25% until 7.0 — above ~13 points
over the world a higher rate buys no currency support in this model and only
reprices credit and the bank's wholesale funding — and the monetary page now
prints what the rule would set and where the dial stops. The first is still
open.~~ — superseded 2026-09-22 (0.7.2): the cap is lifted with the channel;
see `the-currency-off-its-rule.md`. The vault, the founding reserve and the strip shipped as 0.6.10, see
`a-reserve-defends-a-currency.md`. Batch A (0.6.11) and batch B (0.7.0)
shipped the ground and the central bank's books and the floor — `CITY_DISCOUNT`
and its note are gone. Batch C (0.7.1) shipped the curve, the households and
the central bank as holders of the city's paper, and the holdings dial (QE and
QT) — see `the-curve-and-the-holders.md`. Batch D (0.7.2) took the currency
off its rule — the real rate moves it, the vault is spent defending it, the
dial reaches 100% and the advances ceiling is a dial — see
`the-currency-off-its-rule.md`. ~~The households' saving response is batch E,
next~~ — done 2026-09-22 (0.7.3): see `the-demand-channel.md`; the channel is
in and moves very little, and the next rate that bites needs a shelf priced
by scarcity against money, or a deposit rate that passes the dial through.*

**And the model rule changed the same day:** both agents - the implementer and
the docs pass - run on Opus from 2026-09-21; Fable is withdrawn (he does not
have the tokens to run it for every task). `CLAUDE.md` says so.

**FROM THE STRUCTURE AUDIT, 2026-09-18** (see `the-ai-ergonomics-audit.md`), two
that take a minute each on the PC:

- **Normalise the line endings, in one commit of its own.** `.gitattributes` is
  in the tree; the files are still 120 CRLF to 55 LF. `git add --renormalize .`
  then `git commit -m "Normalise line endings to LF"`. Until it is done the
  "keep each file's own ending" rule stands.
  *(2026-09-24: the commands, step by step, went to Jerus with the cleanup of
  `Claude outputs/`, which `.gitignore` ignores since the same day. **Done by
  Jerus the same day** ("git done"): the held copies removed, tags 0.7.9 and
  0.7.10, and the line endings normalised in git.)*
- ~~**Delete the twenty-seven stale Java files from the project**~~ — done
  2026-09-18 (evening), on Jerus's word: twenty-six 2026-09-01 uploads removed;
  the project holds design notes only now.
- **Run `Regenerate maps.bat` after each batch** and commit `docs/` with it; a
  map with yesterday's line numbers is worse than no map. (The maps in the
  2026-09-18 evening deploy are already regenerated, and `JavaScan` no longer
  files an `@Override` method as an initializer.)
- ~~**`git rm` the three stubs at the root**~~ — done 2026-09-18 (Maven refused
  to build past a stub, which is why: a source path finds the file by name).
  After each split batch: Clean and Build, open the game, confirm it is the
  same game.

~~**DEPLOY THE PEOPLE OUTSIDE THE FAMILIES AND THE LONG SICK.**~~ **Deployed
2026-09-11 (evening)** when the PC came back: thirty-two files, every one
byte-for-byte on the PC. **The households remember** went the same night, six
files, and **the running totals of the dead** after it, eight, and **police,
crime and prisons** last, thirty-two. Rebuild in NetBeans and run `AllChecks`
(forty-seven harnesses). Save format is still 21; old slots load — with only
the founding constabulary, so a big saved city shows the crime notice until it
builds a station.

~~**DEPLOY THE REBALANCE.**~~ **Deployed 2026-09-09**, and five times more on
2026-09-10: the rent, migration, bank-solvency and industrial-output work, then
the bank pass, then the audit batch, then the distress batch, then the materials
pass. Every file verified byte-for-byte on the PC after each sync. *And a lesson from the third: the second deploy had carried the
industrial fix in the Java and NOT in `buildings.json`, so every figure in
`why-the-bank-kept-failing.md` was measured against a data file the game did not
have. `BuildingDataCheck` exists to catch exactly that and was failing. Run
`AllChecks` after a copy-back, not before.*

~~**DELETE EIGHT FILES IN NETBEANS.**~~ **Done 2026-09-11 (midday)** — Jerus
`git rm -f`'d the eight stubs, committed on `sector-template`, merged to
`main` and pushed; `MenuManager.java` is gone too. Git rewrote the batch as
CRLF on checkout — the mirrors were realigned. **Rebuild before playing: save
format is 21, and slots from before the template say so instead of loading.**
See `handoff-after-the-sector-template.md` for the state of the tree.

~~**Start the Steamworks paperwork.**~~ **Started 2026-09-06.** It runs in
parallel with development for free from here, which is the whole reason it went
first. The thing to watch is that Valve holds a new app for **30 days** after the
$100 is paid before it can be released — so the clock that matters started when
the fee did, not when the game is finished.

**The HTML manual is current again — 0.5.15, version 5, 2026-09-14.** Rebuilt on
2026-09-12 against the tree at 0.4.4 / save format 21: nineteen sections instead
of sixteen, with new ones for the people outside the families, crime and police,
goods and markets, and the owners and the exchange; the month order carries the
maintenance charge and the crime step; the vitals, the mortality table, the
catalogue, the harness list and the open questions were all re-struck from the
source and from the eight-seed crime ensemble. **Brought forward on 2026-09-14 to
0.5.15 / save format 26**: the three newest sectors and what each of them
changed, the farmland relief dial, the taxes area redesign (five pages, one
staged proposal, the quarter-point ladder), the cost-of-funds floor and the
measurement that closed the "real intermediary" idea, the price index's
water-marks, the clock and the speed ladder and the summary-as-problem-list, the
goods table grown to thirteen goods, and four new open questions. It lives as a
published artifact (title *CityBuilderSim*), not in the repo. **Re-read it after
any batch that changes a headline number** — build, save format, file count,
building count, harness count &mdash; and after any batch that closes one of the
open questions on it. **Assemble it from the PUBLISHED PAGE and never from the
local `.part` files** — they had quietly diverged by a whole comparison table and
three open questions, and building from them would have deleted published
content; and the publish guard will refuse until the live page has been read
line by line in the publishing session, which is the rule working. **STILL TO
DO: the share pin points at version 1**, so anybody holding the share link is
reading the 0.4.4 manual until it is moved. *And the year book
is the other half of this: a run can now be read as two text files without the
game — see the top entries — so anything written about a city from here should be
struck off `year-book.txt` rather than off a screenshot. Since 2026-09-15 the
book also carries `averageWage` and `labourForce`, and its unemployment column
and the Reports chart finally strike the same rate.* ~~**AND IT IS BEHIND AGAIN as
of 2026-09-15: save format 27, a sixth age band, and 50 harnesses.**~~ **CAUGHT UP
2026-09-15 at 0.6.0 / format 27, version 6.** ~~**And behind again by 18
September: five sectors, the transport stack, the vehicles, the interface
split.**~~ **CAUGHT UP 2026-09-18 (night) at 0.6.7 / format 27, version 7 —
twenty sections, see `the-manual-at-0-6-7.md`; the found-on-the-way list is
under Housekeeping.** ~~**AND BEHIND AGAIN as of 2026-09-19: 0.6.8 — the two
health dials and the Health page, the household that goes without care, the
openable treasury row, the desk's re-mark line; 206 files, ~126,000 lines.**
**And 0.6.9 as of 2026-09-21: the Schools page (the Tuition page renamed) — the
price of a place, the grant as a basis and an amount, a rate on the student
loan — and a new revenue line, "Student loan interest"; 206 files, ~128,000
lines; save format 27 and 57 harnesses unchanged.** If the manual's open
questions carry the tuition table's calibration, it is answered by the dial.
**And 0.6.10 as of 2026-09-21: the top strip's two new panels (prices against
founding over inflation year on year; the rate both ways, `US$1 = D$x` over
`D$1 = US¢y`), the founding vault (a new city opens with D$2.5B in the
treasury and US$1B in the vault, rather than $3.5B of cash), and the vault
kept in dollars with a monthly revaluation line; a reserve now damps only a
fall; the monetary page names the rule and the dial's stop apart; 206 files,
~129,000 lines; save format 27 and 57 harnesses unchanged.**
**And 0.6.11 as of 2026-09-21 (built; it ships with batch B): the bank pays
for the city's paper at the settle after the issue, which it never had; the
trade page's push on the rate is a preview; what the currency did to the debt
survives a reload; 206 files, ~130,000 lines; save format 27 and 57 harnesses
unchanged.** If the manual's open questions carry wages running a third above
the index, it is closed without a fix: it does not reproduce.
**And 0.7.0 as of 2026-09-21/22 (deployed with 0.6.11): the central
bank — its balance sheet, M0 as its liabilities, money made and destroyed only
through its operations; the bank's spare cash earning the policy rate and its
shortfalls borrowed at the window; the Money page under Finances (M0 and M2, a
year of each); the autopilot on the monetary page; advances to the treasury
with a ceiling and Jerus's arrears rule, on the Government tab; `CITY_DISCOUNT`
gone, so a city that owes nothing is quoted the dial, not two points under it;
209 files, ~132,000 lines; save format 27 unchanged; 58 harnesses.** If the
manual's open questions carry the overdraft with no floor, or the city
borrowing below its own central bank, both are closed.
**And 0.7.1 as of 2026-09-22 (deployed): the curve — a term premium over the
dial by maturity, listed on the borrow page one row per maturity and on the
rate page at thirty years; term loans at 10, 20, 30, 40 or 50 years only; who
holds the city's paper (the households, the bank, the central bank, abroad)
on the book page and the households' paper on the household screen; the
holdings dial (0–50% of the term paper) on the Policy tab's monetary page and
what it holds on the Money page; the bank's unearned discount on its balance
sheet; the Government tab's Subsidies line; 210 files, ~135,000 lines; save
format 27 unchanged; 59 harnesses.** If the manual's open questions carry one
rate for every maturity, or the bank as the only buyer of the city's paper,
both are closed. The money chapter is written after batches D and E.
**And 0.7.2 as of 2026-09-22 (deployed 2026-09-23):
the currency answers the real rate, not the inflation differential — the
forces page's four terms (trade, the real rate, the vault's defence, parity)
and a real-rate line on the monetary page; the vault spent defending the
currency, on the Exchange page (sold this month and since founding) and under
the central bank's equity on the Money page; the policy dial to 100% with
chips from 0 to 100; the advances ceiling a dial, 3 to 36 months, on the
monetary page; a dollar bond valued on the world's curve; 211 files, ~137,000
lines; save format 27 unchanged; 60 harnesses.** If the manual's open
questions carry the currency drifting by the inflation differential, the
dial's stop at 25%, a reserve that damps for free, or the six-month advances
ceiling, all four are closed.
**And 0.7.3 as of 2026-09-22 (deployed 2026-09-23 with 0.7.2): the demand channel — what a household spends above a
basket a head answers the real deposit rate, printed on the monetary page
("savers earn X% real, so households spend Y% of what they would at zero");
the currency's guards a billion either way, so the rate is no longer held at
100, the strip's second line turned round past a hundredth of a cent ("US¢1 =
D$1,000") and the screens' rates printed through one formatter; EI paid in
the month it is credited; the bank's quoted deposit rate no higher than its
lending rate; 211 files, ~138,000 lines; save format 27 unchanged; 60
harnesses.** If the manual's open questions carry the currency's guard at
100, a policy rate that moves no spending, or the transmission left
unasserted, all are closed.~~ **CAUGHT UP 2026-09-22 (night) at 0.7.3 / format
27, version 8 — twenty-one sections, the thirteenth the central bank; and in
the repository for the first time as `docs/manual.md` (GitHub renders it) and
`docs/manual.html`, generated from the published page by the new
`tools.ManualToMarkdown` — see `the-manual-at-0-7-3.md`; its found-on-the-way
list (sixteen places) is under Housekeeping. The two files and the tool went
to the PC with 0.7.2 and 0.7.3 on 2026-09-23, tag 0922c, verified.**
- **And 0.7.4 as of 2026-09-23 (deployed 2026-09-23, tag 0923a): the inflation target a dial (0–10% in
  half points, on the monetary page, saved under its own key); a third strip
  panel with the central bank's, the bank's and the city's rates; the sector
  list's sparkline, workers and "Show more", on two new history series per
  sector; a base rate per income tax with "Every tax at once" on the
  Everything page; 212 files, ~140,500 lines, 805 dials; save format 27
  unchanged; 60 harnesses.** What the manual now says that is not so: §13's
  rule table names `INFLATION_TARGET` (it is `DEFAULT_INFLATION_TARGET`, and a
  dial); §14's strip paragraph has two panels and "the 2% target" (three
  panels, the player's target); §11 opens "Two city-wide rates, and
  everything else is an offset … from one of them" and calls income tax three
  taxes "sharing one dial" (four bases now); §19's list of what went into
  saves without a bump lacks the target key, the three slots and the two
  series; §21's "What no harness looks at" names "the strip's two panels", and
  0.7.4's screens join the unchecked ones. No open question is closed.
- **And 0.7.5 as of 2026-09-23 (deployed 2026-09-23, tag 0923b): Enter builds what is pending on a
  build page and Backspace or Delete clears it; the Reports page redrawn — two
  pinned small charts (real GDP and the population by default, a preference
  in `settings.json`), the presets, "clear all" and a log switch beside the
  big chart, "What money costs" on a first visit, two units on two real axes,
  a crosshair, recessions shaded, named episodes (`YearBook.episodes()`) under
  the chart and in the year book's WHAT HAPPENED; the picker folded into its
  groups with a filter; 212 files, ~141,900 lines, 815 dials; save format 27
  unchanged; 60 harnesses.** What the manual now says that is not so: the
  build line (0.7.3, already behind at 0.7.4); §19's Reports sentence ("sixty-odd
  series with one heading per group", one chart — nothing of the pins, the
  second axis or the episodes); §19's clock paragraph names the space bar and
  the arrows and not Enter or Backspace on the build page; §20's
  `YearBookCheck` row lacks the named episodes; §21's "What no harness looks
  at" names the Reports page, and 0.7.5's screens join the unchecked ones. No
  open question is closed.
- **And 0.7.6 as of 2026-09-23 (deployed 2026-09-23, tag 0923d): every
  policy dial on one ladder (−, a snapping slider, +, the reading, an ends
  line), the monetary page's target, holdings and ceiling given one beside
  their chips; a price per kind of school, with a row per kind on the Schools
  page (places, students, cost, revenue); real GDP drawn in layers — C, I and
  G stacked, the line over them, net exports the gap — on four new history
  series; land priced in US dollars and paid at the day's rate, by converting
  cash (the default) or out of the vault, a toggle at the top of the land
  office; 213 files, ~144,200 lines, 831 dials; save format 27 unchanged; 60
  harnesses.** What the manual now says that is not so: the build line;
  §10's "The price of a place" (one `tuitionScale`, "five dials on one foot
  bar"); §11's ladder drawing and its ends line, and that it is the tax
  pages' alone; §11's GDP and §19's Reports sentence say nothing of the
  layers; §15's "what the city pays" is local money with nothing of dollars,
  the rate, the toggle or the vault — and its "Ten plots" is nine, older than
  this batch; §1's step 2 re-prices the land office in local money; §14's
  vault says nothing of land paid out of it; §19's list of what went in
  without a bump lacks `landPaidFromVault`, the listing's −105 marker, the
  price state's fourth slot, `ForeignAccounts` slots 31–36, the nine tuition
  scales on the policy array's tail, the schools' month by kind and the four
  GDP series; §20's rows for `EducationCheck`, `HistoryCheck`, `LandCheck` and
  `ForeignCheck` lack their new sections; §21's "What no harness looks at"
  gains 0.7.6's screens, and its open questions gain land conversion's
  missing push (below). No open question is closed.
- **And 0.7.7 as of 2026-09-23 (deployed 2026-09-23, tag 0923e): the bank prices a
  loan from its costs and the strain premium is gone; savers get a rate the
  bank chooses; an account fee and a loan fee; the window at the dial plus a
  quarter point; the bank's dividend after tax; four new history series; 213
  files, ~145,700 lines, 838 dials; save format 27 unchanged; 60 harnesses.**
  What the manual now says that is not so: the build line; §1's step 3 ("its
  premium and its cost of funds"); §12's curve formula ("+ the bank's
  premium") and "what the bank's strain and the advisor read"; §12's Business
  credit (the city's rate + 1% to 8%, floored on the cost of funds plus a
  point — prime plus a re-based spread now, and the loan fee); §12's "The bank
  is somebody" (18 points on every rate; deposits at 45% of interest income
  and the bid) and "Nothing borrows below what the money costs" (the window at
  "policy + a point", and `max(riskFree, costOfFunds + MIN_MARGIN) + its own
  premium` — loans are priced from the funds-transfer price, and the floor
  stands only under the city's paper); §13's "The rate is a floor"
  (`WINDOW_PENALTY` one point; savers paid their share of what reserves earn,
  0.675% at 3% and 2.25% at 10%) and the demand chain's "the deposit rate the
  bank pays out of what its book and its reserves earn", with `MonetaryCheck`
  §6's table (1.032 points then, 1.428 now); §19's list of what went in
  without a bump (`bankPricingHistory`, `bankLateProfit`, the bank's
  last-month array at nine, the account fee on the household statement's
  tail, the four series, `bankPremium` no longer written); §20's rows for the
  harnesses the batch changed. **§21's "Who gets 45%" is closed** — the
  constant and the quote's cap are gone — and its "spread is gone" sale rests
  on a deposit rate that is a share of the dial now; §21 gains the batch's
  opens (below).
- **And 0.7.8 and 0.7.9 as of 2026-09-24 (deployed 2026-09-24, tag 0924a): the bank keeps its capital like a business — a loss
  allowance in two stages, its own target of 10.5–16.5%, a payout rule,
  lending that tightens under its target, its own shares issued and bought
  back by its capital and booked as capital; a sector defaults a slice at a
  time off Merton's curve, and the whole-sector restructure is only the
  backstop; a loan priced off the same curve on the borrower's last quarter; a
  failing sector's plant sold to the builders for its material; the desk and
  the buybacks held to the bank's spare capital; the Bank tab rebuilt, a
  landing and five pages; 213 files, ~151,700 lines, 850 dials; save format 27
  unchanged; 60 harnesses.** What the manual now says that is not so: the
  build line and the header's figures; §1's step 13 (no provision after the
  refresh, no second resolution after the late items, and nothing of the
  capital rule the month's top hands the lenders); §8's "Capacity retires"
  (nothing of a retired plant's material sold to the builders, who build from
  it before they buy); §9's "Forty percent of a positive month is paid out"
  (not the bank's: nothing under its target, 45% in its band, the excess a
  twelfth a month, not capped at its cash), and the desk (its own-share trades
  are capital, bought back only from capital over the target and issued only
  under it; its book held to the bank's spare capital at `RISK_EQUITY`); §12's
  Business credit (the curve — prime + max(0, 60% × PD − 0.4%) + the record,
  at the leverage the loan leaves it, read on the last quarter — and loans for
  buildings as well as holes) and "The underwriter now leaves a gap" (past
  1.5× a sector's firms default a slice a month; only a sector with nothing
  left is written down whole, banned and recorded); "There is no
  concentration limit" (still none; syndication was tried and removed, and
  concentration is under every failure now); "The bank is somebody" (no
  allowance, target, payout or rationing, and "recapitalises to 1.5× the
  minimum" — a standing bank under the minimum is asked for its own target);
  §19's Bank tab, and its list of what went in without a bump
  (`bankAllowance`, `bankCapitalRecord`, `bankMonthLines`,
  `bankStatementYear`, `creditStatements`, `salvageCost`, `paidEarlier`, the
  bank's month lines at 50 and its solvency record at 4, six history series);
  §20's rows for `BankCheck` (§7–17), `CreditCheck`, `MoneyCheck`,
  `HealthCheck`, `ForeignCheck`, `HistoryCheck`, `YearBookCheck`,
  `SaveFileCheck` and `ReadPathCheck`. §21's "A concentration limit on the
  bank" wants rewriting, not closing (it is the measured cause of every
  remaining failure); "A plant that loses money before interest is still lent
  for" now meets a price off the curve at the leverage the plant leaves it at —
  re-measure before closing it; "What no harness looks at" gains 0.7.8's and
  0.7.9's screens (though `BankCheck` §13 asserts the tab's arithmetic); and
  §21 gains `ASSET_VOLATILITY`, the quarter's pricing past the default point
  and `MAX_BUFFER` binding in every seed (section 4).
- **And 0.7.10 as of 2026-09-24 (deployed 2026-09-24, tag 0924b): a founding screen,
  D$100M and US$25M the default, and the city names its money; 215 files,
  ~153,700 lines, 872 dials; save format 27 unchanged; 60 harnesses.** What the
  manual now says that is not so:
  - the build line;
  - §14's "Danzik dollars, and what moves them" (the money is the city's own
    now);
  - §14's "The founders leave one" (D$2.5B and US$1B);
  - §15's "$3.5B endowment … a coal plant at 41%";
  - §13's and §21's "the founders' billion" figures;
  - §19's "nineteen files in `ui/`" (twenty-one);
  - the founding screen itself, which the manual does not have;
  - §19's list of what went in without a bump (the eight founding keys:
    `cityName`, the five currency fields, `foundingCash`,
    `foundingReserveUsd`);
  - §20's `NewGameCheck` row (sections 6–11);
  - §12's "the Build screen still offers a six-month bill" (a 20-year bond
    sized to the gap is offered first now, `Game.BUILD_BOND_YEARS`).
- **And 0.7.11 as of 2026-09-24 (deployed 2026-09-24, tag 0924c): the landlords buy on
  insured CMHC-style mortgages, the city insures them, the bank holds a 3%
  leverage ratio beside its risk weights and closes branches that do not pay,
  and dividends are paid after principal; 217 files, ~157,600 lines, 898
  dials; save format 27 unchanged; 61 harnesses.** What the manual now says
  that is not so:
  - the build line and the header's figures (61 harnesses);
  - §8's "How an expansion is paid for" (a landlord's home: 15% of its own,
    the owners asked for what the till lacks, an insured mortgage for the
    rest, and the lender's test);
  - §9's "Forty percent of a positive month is paid out" (of net income less
    the principal repaid), and the buybacks' "six months of operating cost"
    (and of debt service);
  - §11's budget lines (mortgage insurance premiums and claims);
  - §12's Business credit ("interest-only bullets over 36 months" - not a
    landlord's building); "The bank is somebody" ("measured against 8%
    required" - and 3% of everything it has lent, whichever asks more;
    "Branches build themselves ... when strain passes 70%" - never under its
    minimum, and a branch that does not pay closes);
  - §13's demand channel (`MEASUREMENT_NOISE` 0.05, the rows 0.000 to 0.019 -
    0.10 now, and 0.000 to 0.053);
  - §19's list of what went in without a bump (`mortgageRepaid`,
    `insurancePremiums`, `insuranceClaims`, debts typed "MORTGAGE", the
    government block at 27, the bank's last-month array at ten);
  - §20's harness table (no `MortgageCheck`; `ExchangeCheck`'s cushion);
  - §21's "A concentration limit on the bank" stays open and is again the
    measured cause of the remaining failures. No open question is closed.
- **And 0.7.12 as of 2026-09-26 (deployed 2026-09-26, tag 0926a): corporate
  bonds, and shares with them, on one order book; the dealer is gone; the
  cash-flow test, credit lines that stay open, interim financing, and "buy
  only what it can pay for"; 223 files, ~168,000 lines, 953 dials; save format
  27 unchanged; 63 harnesses, HealthCheck red.** What the manual now says that
  is not so:
  - the build line and the header's figures (63 harnesses);
  - §9's exchange: the bank as the dealer quoting round fair value, the
    desk's quote, special dividends, buybacks as tenders - all gone; the book,
    the last trade, the desk as a participant with its caps, and a buyback as
    a bid whose unspent cash stays;
  - §9's "Forty percent… less the principal repaid" (net repayment now);
  - §12's Business credit (no bonds, one loss given default of 60%, the
    shortfall desk's interest reserve lent past the default point, an
    overdraft that carries): the bond beside the loan, recoveries by
    instrument, the quarter's default point on every door, the cash-flow
    test, working-capital lines outside rationing, interim financing, the
    backstop;
  - §8's distress rule (any plant that makes something the sector sells) and
    its purchases (stock and fleets held to cash and credit);
  - §6/§10's households (each cell holds its own bonds and shares and trades
    with the others);
  - §19's list of what went in without a bump (the bond market's key, the
    cells' bond and share slots by name, the exchange's and desk's appended
    counters, interim loans typed "INTERIM");
  - §20's harness table (`BondCheck`, `OrderBookCheck`; CreditCheck §12–15,
    ExchangeCheck §2b–2c and 7, HealthCheck's year);
  - §21's "A concentration limit on the bank" is answered by pricing, not a
    limit; the remaining failures are the young bank's.
- **And 0.7.13 as of 2026-09-26 (deployed 2026-09-26, tag 0926b): the land office in the
  paying currency and km², buying short and several at once; new games on the
  autopilot; the treasury's rollover; the Bank tab's Balance sheet page; 224
  files, ~171,000 lines; save format 27 unchanged; 63 harnesses.** What the
  manual now says that is not so:
  - the build line;
  - §15's land office: the dollar price first, blocks, a plot the city cannot
    afford refused; now the paying currency, km², the funding page, and
    "Buy the next N";
  - §12's borrowing: nothing refinances a maturity — now the rollover (by
    hand / same structure / 12-month notes, net of last year's surplus,
    sized to the cash);
  - §13's dial: a new city founds by hand — now on the autopilot;
  - §19's Bank tab has five pages — now six, with the balance sheet — and its
    list of what went in without a bump (`rolloverMode`, `rolloverLedger`,
    `rolloverRecord`, `bankSheetYear`, `bankPaidInOpening`,
    `bankRetainedOpening`);
  - §20's rows for LandCheck (14–16), TreasuryCheck (7), BankCheck (13) and
    NewGameCheck (12);
  - §21 gains the limit on a city's own paper and deposits on the bank's
    books.
- **And 0.7.14 as of 2026-09-27 (deployed 2026-09-27, tag 0927a): the city's
  fund; a failed bank resolved for its shares; TARP preferred for a bank under
  its minimum; the Insane start; 226 files, ~176,000 lines, 994 dials; save
  format 27 unchanged; 64 harnesses.** What the manual now says that is not
  so:
  - the build line and the header's figures (64 harnesses);
  - §9/§12's bank failure: the hole absorbed from outside and the owners
    keeping their shares — now the city takes every share and pays the hole,
    the central bank advancing what the treasury lacks, automatic or on the
    button;
  - §12's "The bank is somebody": a standing bank under its minimum topped
    up — now it asks for TARP preferred by a popup, repaid at three years;
  - §11's budget: the fund, its dial and "Transfer from the fund";
  - §9's exchange and the bonds: the fund as a holder and participant;
  - the founding screen's five presets and Insane; the clock's refusal;
  - §19's list of what went in without a bump (`fund`, `bankPreferred`,
    `bankPreferredRecord`, three register slots a company, the bonds' `city`
    field, the bank's month lines, the debt market's inputs);
  - §20's harness table (`FundCheck`, and the sections BankCheck,
    TreasuryCheck, SaveFileCheck, NewGameCheck and ReadPathCheck gained);
  - §21: "who absorbs a failed bank" is closed; the city's own paper at home
    is still unlimited.
- **And 0.7.15 as of 2026-09-28 (deployed 2026-09-28, tag 0928a): Insane
  from day one; the time skip through an empty treasury; the inflation target
  to 20% and the strip's colours from it; the holdings dial to 100%, the
  central bank buying households' paper and rolling its own at issue, the
  rate floor split by holder; ~177,000 lines; save format 27; 64
  harnesses.** What the manual now says that is not so:
  - §13's holdings dial: 0–50% of the term paper, bought from the bank
    only, compression over `MAX_QE_SHARE` (`docs/manual.md` L717, L720,
    L780) — now 0–100%, the households after the bank, compression full at
    `FULL_COMPRESSION_SHARE`, and rolled at issue;
  - §12's floor ("never less than the money costs the bank") — now split by
    who holds the paper;
  - the target dial's 0–10% — now 0–20%; the strip's colours;
  - the time skip stopping at an empty treasury.
- **CAUGHT UP 2026-09-28 at 0.7.15 / format 27, version 10** — every item in
  the twelve entries above (0.7.4 to 0.7.15) was walked by the implementer and
  again by the reviewer, see `the-manual-at-0-7-15.md`: twenty-three sections,
  §13 *The bank* and §16 *Founding a city* new; its found-on-the-way list is
  under Housekeeping. The share pin (above) is still Jerus's.
- **CAUGHT UP 2026-10-01 at 0.7.23 / format 29, version 11** — every item in
  the entries below (0.7.16 to 0.7.20) and in the docs passes' "The manual"
  sections for 0.7.19 to 0.7.23 was walked by the implementer and again by the
  reviewer, see `the-manual-at-0-7-23.md`: twenty-five sections, §9 *The
  builders & the queue* and §23 *Time, saves & logs* new; its found-on-the-way
  list is under Housekeeping. The share pin (above) is still Jerus's.
- **And 0.7.16 as of 2026-09-28: the year and decade books also write their
  tables as CSV, four files beside the text, built from one table; 226 files,
  ~177,850 lines; save format 27 unchanged; 64 harnesses.** What the manual
  now says that is not so: the build line (0.7.15) and "struck from the tree
  at 0.7.15". It never names the year book's files, so nothing there is
  wrong; the Reports passage could say the export writes CSV too, and §20's
  YearBookCheck row that each CSV is its text's table cell for cell. No open
  question is closed.
- **And 0.7.17 as of 2026-09-29: round 1 of Jerus's fixes — the builders'
  crews by Bromilow's weights with nothing parked, the builders counting
  repairs and staffing, the landlords holding 12 months of work and stepping
  down, arrivals bounded by the placement's room, payroll by job type with
  idle crews laid off; ~180,280 lines, 1,000 dials; save format 27
  unchanged; 64 harnesses.** What the manual now says that is not so:
  - the build line;
  - §1 step 10: no crews struck before the posts are counted;
  - §2 Migration: no cap at the placement's room, and the crowding ramp
    reads homes against households rather than households with a door;
  - §4 "payroll = wages × fill" (per job type now, each at its own fill);
  - §6 a sector's payroll discounted by the fill, and "a 25% idle floor" (a
    core crew of 25% of posts, paid in full, the rest laid off onto EI);
  - §8 one order at a time and twelve months of the whole city's output (the
    landlords hold 12 months of the builders' site output and step down;
    everyone else's order opens within 12 months at its share); §8's table
    for Construction and Real Estate;
  - §19 "Construction output" (depots at the share of posts kept on, the
    sites shared by crew weights);
  - §21's list of what went in without a bump (Construction's extras
    `postsOfferedShare`, `crewsNeeded`, `fillStruckOn`; parked points cleared
    on load);
  - §22's rows for Invest, Labour, Population and Infrastructure and the
    re-caused fixtures; §23 gains the founding stall, the unemployment swing
    and held 25%.
- **And 0.7.18 as of 2026-09-29: round 2 — every planner that builds posts
  checks staffing (80% from spare workers, no band nobody can fill), workers
  take the best-paid job they qualify for, some unskilled migrants for a dear
  wage; ~181,440 lines, 1,002 dials; save format 27 unchanged; 64
  harnesses.** What the manual now says that is not so:
  - the build line and the header's figures;
  - §2 Migration's skill mix "drawn from a world with far more labourers than
    graduates": at the going rate every arrival has a diploma, and labourers,
    like graduates, come only for a premium (7.5% at every band's ceiling);
  - §6's job fill ("the allocator spends the workforce across the posts"):
    the best-paid post a worker qualifies for, bands joined as one market;
  - §8: the staffing test named only for Business Services — every planner
    that builds posts asks it now, an order is held to all its buildings, the
    advisor's wording; the table rows;
  - §10's "a band's surplus cascades into the one below": one fill for the
    allocator, the wage, migration and the planners;
  - §22's LabourCheck·EducationCheck row, InvestCheck §18, the re-caused
    fixtures; §23 gains the dear first year, the late-building young town and
    the dormant band rule. No open question is closed.
- **And 0.7.19 as of 2026-09-30: round 3 — the builders' price at today's
  wages with an escalation clause and the tax in the quote, the rebates, the
  grant FIXED at the price index for new cities, the branches by customers
  with the charter exempt, the deposit cap gone, Luxury on the buyers at its
  price; 183,937 lines; save format 27 unchanged; 64 harnesses.** What the
  manual now says that is not so (the docs pass's list, with what each should
  say, in `runs/r6-docs-pass.md`, "The manual"):
  - §13 the bank: capacity from every deposit, the branch rule and the
    charter, the lag, the fees paragraph, the Bank tab;
  - §6 and §1 steps 5 and 10: repairs at today's wages with the tax; the
    builders' quote, the escalation clause, the depot's estimate with wages;
  - §11 VAT: the tax in the quote, business credits, the rental rebates, the
    city's tax coming home, the branch bearing it;
  - §10 the grant's default (FIXED × the price index; saves keep theirs);
  - §7 and §8 Luxury: the buyers at the struck price, not the queue at the
    door;
  - §16 founding: the invoices with the tax (water plant D$129.0M, village
    D$38.8M), the Lean start about two-thirds of the village, the custom floor
    D$6M, the bond example re-measured;
  - §22's MortgageCheck, BankCheck and InvestCheck rows. No open question is
    closed.

- **And 0.7.20 as of 2026-09-30: the interface's bugs and its layout; save
  format 27 unchanged; 64 harnesses.** What the manual now says that is not so
  (`runs/ui1-docs-pass.md`, "The manual", with what each should say), on top
  of 0.7.19's list:
  - §16: "Each start shows what it buys"; Insane's founding card quoting day
    0 (those figures are FundCheck's section 12 and the playtest's); "the
    treasury founds the village" (the village is the playtest's; a player's
    city starts with nothing on the ground);
  - §21: Build keeps its category; urgent notices as toasts; the keys with a
    tooltip showing; Quit asks first, "Autosave · month 13", "$3.1T"; City
    History at the top and its first-year charts; Needs you's "on the way";
    the price card's "rate in ~N mo"; the on-site line on build cards; the
    receipt's last five; the Dashboard's annualised GDP; `UserInterface`
    about 4,650 lines;
  - §11: "of GDP, annualised".
- **And 0.7.24 as of 2026-10-01: the Build screen and the frame — the money
  block, five tiles, the City overview as a drawer behind the "Needs you"
  chip, the construction tab, NEXT DUE on Finances and FALLS DUE in NEEDS
  YOU, Build's Overview and `BuildAdvice`; 236 files, 197,112 lines; save
  format 29 unchanged; 67 harnesses.** What version 11 now says that is not
  so (`runs/ui5-docs-pass.md`, "The manual", with what each should say):
  - the masthead (0.7.23, 233 files, 66 harnesses) and its closing line;
  - §9's "reached from the right panel's *Open*" (a tab at the stage's right
    edge since 0.7.24);
  - §16's "two of the header's six tiles" (five);
  - §19's Build grouping (fourteen categories under the new names;
    Healthcare's four rings);
  - §22's introduction, its header paragraph, its inbox paragraph, "The
    interface is a package" and "The summary is a problem list".

  It wants §22 to gain the money block, the five tiles, the chip and the
  drawer, the construction tab, FALLS DUE and a Build Overview subsection;
  §24 a BuildAdviceCheck row and BuildMenuCheck's new section; §25 the open
  questions the docs pass lists.
- **And 0.7.25 as of 2026-10-01: one card for every building — the one
  300 px card for all 73 (`BuildCard`), the market's nine in their sectors'
  groups, the investors' line, the city cards' (i), "runs" and +100 back,
  and `setBank()` on the load path; 238 files, 198,540 lines; save format 29
  unchanged; 68 harnesses.** What version 11 now says that is not so, on top
  of 0.7.24's list, which all still stands (`runs/ui6-docs-pass.md`, "The
  manual", with what each should say):
  - the masthead and its table (build 0.7.25; 238 files, about 198,500
    lines; 68 harnesses plus the playtest, AllChecks listing 69) and its
    closing line (the suite's 396 s on 1 October: 68 run, 67 green,
    HealthCheck red, BuildMenuCheck 73 of 73 on its own);
  - §19's paragraph after the catalogue table: "Every row of the build menu
    prices itself …" is the 0.7.21 row. Every building is one card now: the
    head, the tags, the hero, "$X all in · sticker $Y", two bars scaled
    within the group (bar 1 the price per unit, or months of value added,
    an office's months of its exports; bar 2 the land per unit, unfilled
    posts per 10,000 served on city cards, the posts the city couldn't staff
    of 100 on offices), the investors' line, the needs line, +10 and +100,
    the quote and the (i). The market's nine in groups: Industry seven,
    Shops two.

  It wants §22 to gain the one card (its parts, the groups and their notes,
  value added, the strict tag rule, no track in a group of one, the
  investors' line and its gates, the quote's "no iron deposit" and "nobody
  licensed", the city cards' (i) and +100, the market order bar); §24 a
  BuildCardCheck row (it fails without the load fix), its counts and "the
  tree, and the prose that describes it" (238 files, 198,540 lines, 1,158
  dials, 6,346 labelled assertions); optionally a line in §14 or §23 on the
  load fix; and §25 the open questions the docs pass lists (saving
  `lastInvestment`, the Restaurants note, the two unsaved month flows,
  Rail's tonnes across a load, "1 business college", the maker hero's first
  good, and its flags 1 to 5). The ★ decisions D3, D4, D9 and D11 are open.
- **And 0.7.26 as of 2026-10-01: the land office redrawn — THE GROUND bar,
  the 3 × 3 shelf with independent tags, the four worth cards, the funding
  page in the frame, "% used" without a verdict on the office, Build and the
  left panel (`CityNeeds.ground()`), the going rate as
  `LandMarket.goingUsdPerSqFt()`, the receipt in the screens' money; 238
  files, 199,964 lines; save format 29 unchanged; 68 harnesses.** What
  version 11 now says that is not so, on top of 0.7.24's and 0.7.25's lists,
  which still stand (`runs/ui7-docs-pass.md`, "The manual", with what each
  should say):
  - the masthead and its table (build 0.7.26; 238 files, about 200,000
    lines; 68 harnesses plus the playtest, AllChecks listing 69) and its
    closing line (the suite on 1 October: 68 run, 67 green, HealthCheck red;
    BuildMenuCheck 73 of 73 on its own);
  - §18 Land: "areas read in km² … and so do the Summary's land panel" (the
    office leads with square feet to three figures, the km² after them; the
    left panel's LAND header reads "N free · D$x/sq ft", and only its Owned
    and Free lines are in km²); "its button opens a funding page built from
    the build screen's own pieces" (the office's own page now, "Land office ›
    Funding": the rail lit, a summary bar and a "short by" pill, the offers
    as cards, back to the office when a month lands and nothing needs
    funding); Buy the next N could add the numbered cards and ghosts;
  - §18's "land sits at 88–90% for the last decade of most long runs" wants
    the 0.7.26 measurement beside it: the 2,400-month playtest city was 95%
    used or more in 120 months of 120 with no sector waiting, which is why
    no screen colours the share used any more.

  It wants §18 (or §22) to gain the office redrawn: THE GROUND; GROUND FREE,
  Build's LAND FREE and the left panel coloured by NEEDS YOU's GROUND row;
  the going rate as a model read; the tags (BEST VALUE is card 1, not
  `bestValue()`; MOST ORE is `richestDeposit()`; one card can carry both);
  neutral prices; the worth cards and details; the receipt in the screens'
  money; "Who is waiting" from the sectors the month found blocked. §22 a
  subsection "The land office redrawn (0.7.26)": Jerus's ask, the layout,
  no verdict on "% used", the funding pages on the rail, the `Pieces` a
  redrawn screen is built from, Construction's bars among them. §24 the
  LandCheck row's section 17 (the going rate, the GROUND row's levels, the
  receipt), "What no harness looks at" (the office's words are read by no
  harness; none of it seen rendered) and the counts (238 files, 199,964
  lines, `Game.java` 13,573 lines and thirty-seven banners, 1,168 dials,
  6,360 labelled assertions). §25 the open questions: arriving from *Not
  enough land* with N preset and that page's "roughly" quote (★12), the red
  tick at a waiting sector's need (★11), Build's "Order on credit" adopting
  `offerCard()`, `Icons.ORE` not yet seen (D16). "The interface is a
  package" still says `UserInterface` is about 5,500 lines (5,863; older
  than the batch).
- **And 0.7.27 as of 2026-10-01: people at a glance — the People page as
  pictures (the pyramid and its settled ghost, the month as a waterfall, the
  bridge, the care rings, the homes gauge, the household mosaic, the outside
  tiles, the ladder as bars), Household money as a page of its own,
  Migration's month, the dead by cause and the hunger's two halves saved,
  `wagePerEarner()` and the fees named apart, HUNGRY, Pensions and THINNEST
  COVER; 238 files, 202,529 lines; save format 30; 68 harnesses.** What
  version 11 now says that is not so, on top of 0.7.24's to 0.7.26's lists
  (`runs/ui8-docs-pass.md`, "The manual", with what each should say):
  - the masthead, its table and the footer (build 0.7.27, save format 30;
    238 files, about 202,500 lines; the suite on 1 October, 68 run in 402 s,
    67 green, HealthCheck red; BuildMenuCheck 73 of 73 on its own);
  - §23's "The save format is at 29": 30, with a sentence in the house
    shape — thirty is the month the People page draws: Migration's last
    month after its wage history, the pyramid's dead by cause after its
    flows, the shops' delivered share and the hungry at full shelves after
    the households' row array; a format-29 build refuses each array whole,
    and a format-29 save loads with those figures at 0 until a month runs;
  - §22's "The summary is a problem list": HOW THE CITY IS's *hungry* reads
    the share of people since 0.7.27, with the sick-rate points in its
    tooltip (it read Health's points, 6.3 against 42.2, and opened a page
    showing the other figure).

  It wants §2 People to gain the dead by cause saved and Migration's month
  saved (a format-29 save reads "Not recorded yet" until its first month;
  before, a reloaded page read "a city this good draws 0"), and Household
  money as a page: the live wage at each tier's head, the books beside the
  grid, the verdict that counts every row, the rows outside the families
  against a basket (the students had read −$29,448 in Jerus's city), the
  retired on their statement, INCOME PER RESIDENT for PER WORKER; its
  household-books paragraph is now true of the opened cell (until 0.7.27
  the fares sat under "Healthcare and school fees" and the account fee under
  "Interest on what they owe"). §7's hunger box the split
  (`getHungryAtFullShelves()` the money half, the delivered share the rest,
  both saved: in the 2,400-month city GOING SHORT read 42.2%, 49 people
  could not afford a basket, and the shops handed over 3% of what was
  planned). §22 a subsection "The People page redrawn (0.7.27)": Jerus's
  ask, the one page and its sections, every table behind "details", Household
  money, and the `Pieces` it added. §24's SaveFileCheck · ReadPathCheck row
  (the People page's month reloads figure for figure, format-29 arrays still
  load, an unknown length refused whole), "What no harness looks at" (none of
  0.7.27 seen rendered) and the counts (202,529 lines, 1,194 dials, 6,401
  labelled assertions). §25 the open questions: NEEDS YOU's CARE rows and
  Build's rings on the beds (D16), the first-month drift after a load,
  "People are leaving" on any departure, and the hunger said as
  people-equivalents. "The interface is a package" still says about 5,500
  lines (5,870).
- **And 0.7.28 as of 2026-10-01: services at a glance — the Services screen
  redrawn (an Overview per system: the sick rate as a bar of its causes over
  the care cards, the schools as a pipeline, power, water and the road as
  capacity rows, the crime as a bar of its reasons), power in kW, MW and GW on
  Services and Build, Build's "why ›", the Infrastructure tab moved to
  `InfrastructureScreen`, `getNewDiplomas()` and the homes' draws; 239 files,
  204,593 lines; save format 30; 68 harnesses.** What version 11 now says that
  is not so, on top of 0.7.24's to 0.7.27's lists (`runs/ui9-docs-pass.md`,
  "The manual", with what each should say):
  - the masthead, its table and the footer (build 0.7.28; 239 files, about
    204,600 lines; 68 harnesses plus the playtest, AllChecks listing 69; the
    suite on 1 October, 68 run in 410 s, 67 green, HealthCheck red;
    BuildMenuCheck 73 of 73 on its own);
  - §20's "Power and water": "The grid starts with 10,000 W free" (10,000 kW),
    "Power is $0.01 a watt" ($10 a kilowatt a month), "the water plant's own
    900 watts" (900 kW);
  - §22's "The interface is a package": "twenty-three" (twenty-four, with
    `InfrastructureScreen`), "the Infrastructure tab drawn by
    `ServicesScreen`" (by `InfrastructureScreen`), "about 5,500 lines" (about
    5,900);
  - §12's "the fare on Services" (the dial is on Infrastructure › Transit).

  It wants §22 to gain a subsection "The Services screen redrawn (0.7.28)"
  (Jerus's ask; the four Overviews and their pictures; the pages behind; every
  table behind "details", every paragraph behind an (i), Books the last chip;
  the frame's dots, "!", five events and sparklines; power in kW, MW and GW on
  Services and Build; Build's "why ›"; the `Pieces` it added), and "The
  summary is a problem list" OFF SICK opening Health's Overview on
  `CityNeeds`' lines; §20 kW, the plants the city's own (their net into the
  city's cash, a loss paid as the city's services promise) and the homes'
  draws apart from the city's own buildings; §24's rows (SicknessCheck's ring
  in people, EducationCheck's leavers' diplomas gross and not saved,
  WaterCheck's draws against the templates, ReadPathCheck's reads), "What no
  harness looks at" (none of 0.7.28 seen rendered) and the counts (239 files,
  204,593 lines; 1,226 dials; 6,412 labelled assertions). It could add to §11
  (the leavers' diplomas as a read of their own; the DIPLOMA band nets off
  every college and university finisher, which is why it read −18), §5
  (Canada's 127 prisoners per 100,000), §21 (the tab drawn by
  `InfrastructureScreen`), §23 (the console's report in kW) and §25 (History's
  "Graduates", `everGraduated`, the month's figures not saved, a course's
  "enrolling" step, Medical "held by students", `buildings.json`'s watts).
- **And 0.7.29 as of 2026-10-01: the road in one picture — the Infrastructure
  screen redrawn (the flow curve and the walk from trips to the road,
  Transit's funnel, the railway's bill three ways, Freight's bar a good), the
  road as "N% full · N% flow" in one verdict everywhere, the room before it
  slows on the effective load, the railway's allowed bill and what went abroad
  saved in its extras; 239 files, 206,384 lines; save format 30; 68
  harnesses.** What version 11 now says that is not so, on top of 0.7.24's to
  0.7.28's lists (`runs/ui10-docs-pass.md`, "The manual", with what each
  should say):
  - the masthead, its table and the footer (build 0.7.29; 239 files, about
    206,400 lines; 68 harnesses plus the playtest, AllChecks listing 69; the
    suite on 1 October, 68 run in 513 s beside the playtest, 67 green,
    HealthCheck red; BuildMenuCheck 73 of 73 on its own);
  - §21's "The instrument panel", false throughout about the pages: the banded
    meter, the streams' table, "a box", and the freight bill's "half that
    leaves inside the band" (which was the shippers' saving when the railway
    carried everything).

  It wants §21's panel rewritten for the redrawn tab, or a §22 subsection "The
  Infrastructure screen redrawn (0.7.29)": the frame and its doors both ways
  with Build; Roads' curve with the city's dot and the walk from trips to the
  road; Transit's funnel and the line that reconciles Build's ring with who
  rides; the railway's bill three ways and its quote on a gauge; Freight's bar
  a good; every table behind "details"; InfrastructureCheck's tenth section.
  §24's rows (InfrastructureCheck 10; RailCheck's bill three ways, the reload
  and an older save loaded as not known; ReadPathCheck) and the counts
  (206,384 lines; 1,250 dials; 6,435 labelled assertions). It could add to §20
  (the road as one pair everywhere; the room before it slows and the spare on
  the load the curve reads, where they read the raw trips: 585 trips of room
  on a road 161% full), §21's railway (the bill three ways, allowed and abroad
  kept across a save in its extras and "not known" from an older one, B1),
  §22's problem list (the ROADS row's pair; the drawer's road line in that
  row's colour), §23's saves (the railway's two extras), §7 or §16 (the
  wedge's four reads) and §25 (B1, B13, `Money.money(NaN)`, Services' ROADS
  figure, the flow's wording against the transit blend; not "the road getters
  mix", which B3 fixed).
- **And 0.7.30 as of 2026-10-01: the sectors as flows — the Sectors screen
  redrawn (fifteen cards under four figures; each business as a flow, a
  waterfall, two bars and an owners card shared with the Bank, a cash bridge,
  and its investors in one line), `SectorFlow` and SectorFlowCheck,
  `wordKind()`, `operations()` in two halves; 241 files, 209,056 lines; save
  format 30; 69 harnesses.** What version 11 now says that is not so, on top
  of 0.7.24's to 0.7.29's lists (`runs/ui11-docs-pass.md`, "The manual", with
  what each should say):
  - the masthead, its table and the footer (build 0.7.30; 241 files, about
    209,000 lines; 69 harnesses plus the playtest, AllChecks listing 70; the
    suite on 1 October, 69 run in 415 s beside the playtest, 68 green,
    HealthCheck red; BuildMenuCheck 73 of 73 on its own);
  - §10's *Its owners* block, false in shape: since 0.7.30 it is ITS OWNERS, a
    card on the Balance sheet page, which the Bank's Capital & owners page
    draws too (who holds it as a bar, the figures, the order book behind
    "details" in plain figures, the chart, the regime behind its (i));
  - §22's Sector economy paragraph ("Since 0.7.4 the Sector economy list draws
    on each card …"): fifteen cards in three columns in Build's order under
    four figures, each with its icon, group, running-at bar and investors'
    word;
  - §22's "The statement opens": the Sectors screen keeps each business's
    opened lines too, and its waterfall's Revenue, Bought in, Wages and Sales
    tax bars open them;
  - §24's "The sector pages are the only screen whose text a harness reads":
    they are pictures now, their figures held by SectorFlowCheck and
    BuildCardCheck, their old lines by SectorBooksCheck.

  It wants a §22 subsection "The Sectors screen redrawn (0.7.30)" (Jerus's
  ask; the list; the frame; Operations, Income, Balance sheet, Cash & debt and
  Investors; the fixes B1–B4, B7–B11 and B14; `SectorFlow`, the `Pieces` and
  icons it added); §24's rows (SectorFlowCheck new; BuildCardCheck section 8;
  ReadPathCheck; SectorBooksCheck, whose "no plant standing" passes as words)
  and the counts (241 files, 209,056 lines; 1,282 dials; 6,463 labelled
  assertions in 70 harness files). It could add to §6 (the fill and the five
  as a cascade whose last step is the rate; B3's note; B4's "no plant
  standing"), §8 (the investors' word in twelve kinds, `sectorInvestors()`)
  and §25 (B6, B12, B13, the credit book quoting without the record after a
  load, a sector with nothing standing quoted 26–28%, the docs pass's flags
  1–3).
- **And 0.7.31 as of 2026-10-01: earned, surplus, banked — the Government
  screen redrawn (five figures; the two rings with THE BALANCE; the bridge FROM
  EARNED TO BANKED; Revenue and Spending as ranked bars with doors; THE DEBT,
  THE SERVICES THAT CHARGE and PENSIONS; Output led by the GDP layers), the
  city's money named EARNED, SURPLUS and BANKED everywhere ("+$X earned a
  month"; the drawer's "Earned"), `getEarnedToBudget()` and TreasuryCheck §8,
  the GDP history seeded on load; 241 files, 210,334 lines; save format 30; 69
  harnesses.** What version 11 now says that is not so, on top of 0.7.24's to
  0.7.30's lists (`runs/ui12-docs-pass.md`, "The manual", with what each should
  say):
  - the masthead, its table and the footer (build 0.7.31; 241 files, about
    210,000 lines; 69 harnesses plus the playtest, AllChecks listing 70; the
    suite on 1 October, 69 run in 377 s beside the playtest, 68 green,
    HealthCheck red; BuildMenuCheck 73 of 73 on its own);
  - §12's *What the treasury actually did*, false in shape: since 0.7.31 the
    Overview's FROM EARNED TO BANKED is three tiles, EARNED (the tax take less
    the running programmes plus the utilities' net, at today's rates), the
    budget's SURPLUS and what the cash BANKED, with every step named and each a
    door: from EARNED to the budget `Game.getEarnedToBudget()` and "Today's
    dials, not the month's" (TreasuryCheck §8); from the budget to the cash
    paper raised and repaid, every journal line by name and "Not accounted
    for"; the old statement behind "details", its "of the change" column cut.
    And "the city's own repair bill and the transit fares are on the Government
    screen and not in the budget's totals, which are journalled by name until
    they are": neither is drawn as a budget line now (repairs had made the key
    add to 111%), both are named under the totals as "outside the budget's
    total" and on the bridge, and carrying them in `NationalAccounts` is a
    model batch for Jerus;
  - §22's "The inbox, and the money on the screen" (the bridge "opens into the
    treasury's journal and lists the arrears by line": every journal line is a
    step on the bridge card now, and the arrears are rows of the CENTRAL BANK
    card) and "The statement opens" (the bridge's last row opening into the
    journal; "every other screen's opened lines still snap shut": the
    Government tab keeps its opened lines, folds and bridge columns through a
    month, as Sectors has since 0.7.30);
  - §22's "Colour, type, the header and the menu": the line under the cash
    reads "+$1.5B earned a month", its (i) naming EARNED, SURPLUS and BANKED
    and the steps between them; the City overview's vitals read Cash · Earned ·
    Population; a loaded city's GDP is its own year at once.

  It wants a §22 subsection "The Government screen redrawn (0.7.31)" (Jerus's
  two asks, "the others are still full of text and the design could be more
  intuitive and fun if you get what i mean" and "the money one has is barely
  visible to see as well as ones income"; the three names everywhere; the five
  figures; the Overview's rings, THE BALANCE, the bridge, the central bank and
  AGAINST THE ECONOMY; Revenue and Spending as ranked bars that open into who
  pays, with doors to each line's setting; the three cards; Output led by the
  layers; the fixes B2–B6, B9 and B11–B15; Pieces' split ring, ranked bars and
  bridge); §12 GDP's load sentence (until 0.7.31 every load reset the record to
  one month, 36.4% for 34.4%, "Months recorded 1", the header's GDP tile "$0 /
  yr · first year"; since then `NationalAccounts.seedHistory()` puts the last
  ten years back from the graph history, SaveFileCheck holds it, and the traces
  did not move); §24's rows (TreasuryCheck's "and EARNED walks to the budget";
  SaveFileCheck's year after a load; ReadPathCheck's walk and EARNED read
  without striking), "What no harness looks at" (the Government tab not seen
  rendered) and the counts (241 files, 210,334 lines; 1,317 dials; 6,476
  labelled assertions in 70 harness files). It could add to §25 (B10, B5's G,
  D4's model batch, D11's trailing year, D18's payroll of every city post).
- **And 0.7.32 as of 2026-10-01: finances at a glance — the Finances screen
  redrawn (the hub as the debt's dashboard: five figures, WHEN IT FALLS DUE by
  calendar year with NEXT DUE beside it, the rollover and the rescue said once,
  six area cards; every page one picture first; Issued a receipt; "Finances ›
  Default abroad" its own page), `DebtManager.ladder()` and ForeignDebtCheck
  §9; 241 files, 211,757 lines; save format 30; 69 harnesses.** What version 11
  now says that is not so, on top of 0.7.24's to 0.7.31's lists
  (`runs/ui13-docs-pass.md`, "The manual", with what each should say):
  - the masthead, its table and the footer (build 0.7.32; 241 files, about
    212,000 lines; 69 harnesses plus the playtest, AllChecks listing 70; the
    suite on 1 October, 69 run in 357 s, 68 green, HealthCheck red;
    BuildMenuCheck 73 of 73 on its own);
  - §13's "The dial, and the curve on it": the borrow page "lists the curve one
    row per maturity" and its chips "step by ten"; since 0.7.32 the terms at
    home are columns of their rate, re-struck for the amount asked, and chips
    abroad, and Your rate draws THE CURVE at ten maturities;
  - §13's "Rolling what falls due": the setting "on both borrow pages" is said
    once, on the hub, since 0.7.32, with next month's bar (netted, the central
    bank's own, each issue, from the cash: `Rollover.Plan.fromCash()`) and the
    last roll dated by the month it fell due;
  - §13's "Borrowing in somebody else's money": the confirmation is "Finances ›
    Default abroad", two equal cards, reached from Borrow › Abroad, and the
    rail lights Finances, not Trade; the scar is on the city's premium abroad
    only;
  - §22's "The inbox, and the money on the screen": the Money page "with a year
    of each", the borrow pages carrying the rollover and the rescue, and the
    book page saying who holds each bond;
  - §22's "City History's chart" ("the Bank tab's six, Finances' two":
    Finances' charts are City History's own since 0.7.32) and "The decision
    log" ("shown nowhere but the chart": its borrowing decisions are meant as
    flags on OWED AND THE RATE, which a small chart does not draw until
    0.7.38).

  It wants a §22 subsection "The Finances screen redrawn (0.7.32)" (Jerus's
  ask; the hub as the debt's dashboard, one verdict each; the ladder and NEXT
  DUE; the settings once; the six areas; every page one picture first; Issued;
  D$ and US$; the fixes, B1–B19 and the three found on the way;
  `Pieces.columns()` and `setting()`; PAPER, BANKNOTE and SAFE), §22's NEEDS
  YOU doors (B8), §24's ForeignDebtCheck §9 row and the counts (241 files,
  211,757 lines; 1,375 dials; 6,501 labelled assertions in 70 harness files),
  and could add to §25 (the default page's 10 points; a buyback quote that
  reprices the market; the bank's capital-limited room; a new issue's holders a
  month late; the rollover's record without the central bank).
- **And 0.7.33 as of 2026-10-01: the bank at a glance — the Bank screen redrawn
  (an Overview: the stance as a banner, the capital gauge on the ratio that
  binds, eight figures with HOW FULL, the ladder of its rates in parts; six
  pages, each one picture first, the old statements under "details"),
  `quoteParts()`, a sector's rate after a load fixed on the load path; 241
  files, 214,266 lines; save format 30; 69 harnesses.** What version 11 now
  says that is not so (`runs/ui14-docs-pass.md`, "The manual"):
  - the masthead (build 0.7.33; about 214,000 lines; the suite 69 run in 333 s,
    68 green; BuildMenuCheck 73 of 73);
  - §14's "The Bank tab", false whole: the landing's status sentence, its
    scorecard with the capital ratio on a bar, the rate ladder labelled in
    points, "Behind it are six pages", "Since 0.7.14 the landing carries the
    resolution and the preferred offer" (cards at the top of every page now),
    and the branch verdict that could disagree with its own figures (the
    planner's since 0.7.33);
  - §22's "City History's chart" ("the Bank tab's six") and "The decision log"
    (the Bank's ITS RATES is meant to carry the central bank's and the bank's
    decisions, not drawn as built until 0.7.38).

  It wants a §22 subsection "The Bank screen redrawn (0.7.33)", §14's B5
  sentence (since 0.7.33 a city just loaded quotes each sector with its record
  and the book's concentration; until then Mining read 3.07% after Continue and
  6.38% a month on, the simulation never lending at the wrong rate), §24's
  BankCheck §13b row and the counts (1,398 dials; 6,522 labelled assertions),
  and could add to §25 (the Sectors rate bar; a true minus inside the
  statements; the leverage ratio as a History series and `bankCapitalRatio`
  clamped at 1,000%; the chart flags).
- **And 0.7.34 as of 2026-10-01: buttons that ask to be pressed — `Pieces`'
  action button on every Build and Buy, the door pill on every link to Build or
  the land office, Build at none choosing one; Finances' "later" drawn broken;
  the Sectors list's one line after a load; `BuildAdvice.quoteTotal()`; 241
  files, 214,752 lines; save format 30; 69 harnesses.** What version 11 now
  says that is not so (`runs/ui15-docs-pass.md`, "The manual"):
  - the masthead (build 0.7.34; about 214,750 lines; the suite 69 run in 317 s,
    68 green);
  - §19's "The catalogue" (the card's Build, a small grey button disabled at
    none: since 0.7.34 a button the card's width that says the order, in four
    looks, "Build · choose how many" at none, a double-click one press);
  - §13's "When the treasury cannot pay for an order" (each offer ends in
    "Build 3 · $37.5M" over "issues the 20-year bond, then builds");
  - §18's "Land" (a plot's Buy, "Buy the next N" and the funding offers say
    their price and their paper; a double-click buys one plot).

  It wants a §22 subsection "Buttons that ask to be pressed (0.7.34)" (Jerus's
  words; the two pieces; where each sits; what was left as it was; the
  double-click rule; Build and the land office in one pink; no keyboard focus),
  the Finances ladder's broken "later" and the Sectors list's line in their §22
  subsections, §24's counts (ReadPathCheck reads `quoteTotal()`; 1,404 dials;
  6,522 labelled assertions), and could add to §25 (the HELD line's "›"; the
  land office's pink; focus).
- **And 0.7.35 as of 2026-10-02: trade at a glance — the Trade screen redrawn
  (five pages under five figures; the goods off the businesses' books; the
  month as steps and the river; the rate on City History's chart with parity;
  the forces; the reserves and the exchange), one parity rule for the tab, the
  drawer and the header, `fxParity` a new History series; 241 files, 216,714
  lines; save format 30; 69 harnesses.** What version 11 now says that is not
  so (`runs/ui16-docs-pass.md`, "The manual"):
  - the masthead (build 0.7.35; about 216,700 lines; the suite on 2 October, 69
    run in 297 s, 68 green);
  - §16's "The city's dollar, and what moves it" ("The Trade tab's forces page
    shows the four terms": The currency's WHAT IS MOVING IT, the model's
    previews, the push and the pull adding to the move);
  - §16's "The vault" (the Exchange page's founding line; the reserves in
    0.7.35's shape, `THIN_COVER` the model's, "under 0.1 months");
  - §16's "And the header carries them" (RATE's colours on the one parity rule
    since 0.7.35);
  - §22's forces sentence, "City History's chart" (a Parity line; the Trade
    chart without full screen) and "The summary is a problem list" (the
    currency row's rule and door);
  - §23's year book (an `fxParity` column).

  It wants a §22 subsection "Trade & the world (0.7.35)" and §24's counts
  (ForeignCheck's goods footing and §15, RailCheck, HistoryCheck; 1,448 dials;
  6,548 labelled assertions), and could add to §25 (D4; B14; the small charts'
  flags; the drawer's 15%; the D25 words).
- **And 0.7.36 as of 2026-10-02: policy at a glance — the Policy screen redrawn
  (a hub of four areas; dial cards whose before → after follow the thumb; the
  staged tray and THE BUDGET), `PolicyPreview` and PolicyPreviewCheck, the
  floor in today's money, PROMISES without the waived tuition; 243 files,
  218,729 lines; save format 30; 70 harnesses.** What version 11 now says that
  is not so (`runs/ui17-docs-pass.md`, "The manual"):
  - the masthead (build 0.7.36; 243 files, about 218,700 lines; 70 harnesses
    plus the playtest, AllChecks listing 71; the suite on 2 October, 70 run in
    283 s, 69 green; BuildMenuCheck 73 of 73);
  - §12's "Setting them: five pages, one proposal" (the hub, the dial cards,
    the staged tray and THE BUDGET as `PolicyPreview`'s; every tax at once
    parted; WHO PAYS; the payslip);
  - §11's "Four bands, one price each" (the floor in today's money and the
    Wages ladder) and "The price of a place" (the tray, WHO CAN AFFORD A PLACE,
    the statement footing);
  - §4's "The price at the door" (Health's corner chip, WHO PAYS FOR CARE,
    "Fees cover 0%" no longer said of a service that cost nothing);
  - §12's PROMISES (without the waived tuition), §22's money page, and §22's
    "The interface is a package" (`Ladder`'s marks, `Levers`' dial card,
    `Pieces`' tray).

  It wants a §22 subsection "Policy (0.7.36)", §24's counts and a
  PolicyPreviewCheck row (1,506 dials; 6,615 labelled assertions in 71 harness
  files), and §25's B8, the inflation colours and the founding floor's last
  readers.
- **And 0.7.37 as of 2026-10-02: City History finished — the last rail screen
  redrawn (a fixed frame over a page at the stage's width; the pins as cards; a
  card a line; HARD TIMES AND YOUR DECISIONS; 217 lines; PRICES THIS MONTH),
  `YearBook.running()` and `ChartModel.onAxis()`; 243 files, 220,058 lines
  after its docs pass; save format 30; 70 harnesses.** What version 11 now
  says that is not so (`runs/ui18-docs-pass.md`, "The manual"):
  - the masthead, its table and the footer (build 0.7.37; 243 files, about
    220,100 lines; 70 harnesses plus the playtest, AllChecks listing 71; the
    suite on 2 October, 70 run in 312 s, 69 green, HealthCheck red;
    BuildMenuCheck 73 of 73 on its own);
  - §22's "City History's chart", in 0.7.37's shape: the head with "Write the
    year book" on 0.7.34's button and its result card; the strip (THE CITY,
    HARD TIMES, RUNNING NOW with a chronic episode last, YOUR DECISIONS); the
    page at the stage's width with the pins as cards; WHAT EACH LINE DID, a
    card a line in neutral ink with a range bar (B1); HARD TIMES AND YOUR
    DECISIONS with the by-kind details; PICK WHAT TO DRAW's 217 lines (148
    before); PRICES THIS MONTH; the founding month's decisions on the lane
    (B4; on Trade's, Finances' and the Bank's lanes too since 0.7.38); the
    episodes' red by `YearBook.isSevere()`;
  - §22's "The decision log" (Policy's RECENT DECISIONS and City History's
    lists show it too), "The interface is a package" (`Pieces`' chart card
    head and range bar, `Icons`' episode and decision icons; the reading
    cards grow a card a line) and "Colour, type, the header and the menu" (a
    tile clicked on History lands at the chart);
  - §12's "GDP" (the four parts lines of their own) and §23's year book (the
    button in History's head, its result a card with a "×").

  It wants §24's counts and ChartCheck's sections 3 and 6 (243 files, 220,058
  lines; 70 harnesses plus the playtest; 69/70 in 312 s; 6,626 labelled
  assertions; 1,520 dials) and "what no harness looks at" (City History
  redrawn, not seen rendered), and could add to §25 (B5, the epidemic; the
  bank's capital ratio drawn as a flat 1000%; the pins without flags; the ★
  decisions of `runs/ui18-notes.md` §4).
- **And 0.7.38 as of 2026-10-02: the loose ends of the redraw — the small
  charts' flags, the founding month on every lane, "under 0.1 months" of cover
  through one formatter, the floor in today's money on People, the fare on the
  dial card, and six more display fixes; 243 files, 220,148 lines by the
  implementer's Maps; save format 30; 70 harnesses.** Its docs pass is running,
  so its list of what version 11 now says that is not so will be in
  `runs/ui19-docs-pass.md`, "The manual". From the implementer's notes: the
  masthead (build 0.7.38; 6,632 labelled assertions; 1,522 dials); the 0.7.37
  list's lane (every chart's founding month on its lane, the Bank's ITS RATES
  and Finances' OWED AND THE RATE drawing their flags); "under 0.1 months" of
  cover on Finances and in the drawer, and the drawer's cover and "vs parity"
  on Trade's rules; the floor in today's money on People, History's line and
  the year book's column named founding money; Infrastructure's fare as a dial
  card; a shut-out sector's red name on the Bank's ladder; and for §25, the
  Bank chart's drag and the sector (i)s' drift.
- **And 0.7.39 as of 2026-10-02: the fund as a brokerage — Finances › The
  city's fund as four pages (Portfolio, Search, Activity, Rules & cash) and a
  page a security with YOUR POSITION and the order ticket; `FundLedger`, each
  holding's adjusted cost base by the average-cost method, its realized P&L
  and its income; `FundView`; the hand's price and cancelling; a hand buy
  stopped at the 10% cap and its cash held from the rule; three History
  series; save format 30; 71 harnesses.** What version 11 now says that is
  not so (`runs/ui20-docs-pass.md`, "The manual", eleven sections; with the
  fixes' one addition to §12, `runs/ui20b-notes.md` §10):
  - the masthead, its table and the footer (build 0.7.39, save format 30; 247
    source files, about 225,500 lines after the fixes, 225,090 at the docs
    pass; 71 harnesses plus the playtest, AllChecks listing 72; the suite on
    2 October, 71 run, 70 green, HealthCheck red, 299 s at the docs pass and
    313 s after the fixes; BuildMenuCheck PASS 73 of 73 on its own);
  - §10's "One book for each company", its last bullet: the rule at fair
    value, then the player's hand at fair value or a price the player names
    (the best bid, the best ask, the last trade, or fair value in steps of
    1%), a buy no further than the room under the 10% cap, its money held out
    of the cash the rule's bids settle with from the moment it is placed; a
    waiting order can be cancelled; one that cannot be posted lapses, and the
    fund's record says why;
  - §10's "The firms sell bonds", its last paragraph: "The player does not
    trade, bar the fund's hand" stands; where trading shows gains the fund's
    Search and a page a security (its price chart, KEY STATS, THE BOOK, its
    record, YOUR POSITION and the order ticket);
  - §12's "The city's fund": "never more than 10% of any company" now stops a
    hand buy too, counting what the fund holds and every order it has on the
    company as if filled, the rule's bid making way for the hand's (★B1, and
    the fixes' ★); the hand at fair or a price it names, cancellable before
    the step, a buy's money held from the rule's bids (★B9); "Order: buy … at
    …" in the decision log, fills and lapses on Activity; four pages and a
    security's page where there were Holdings and By hand; a new paragraph on
    the cost basis (ACB by the average-cost method; one lot a company's
    market book, the rule's and the hand's pooled, one for the rescue book,
    one a bond; income apart; what a sale, a maturity, a write-down and the
    rescue realize; a split leaves the ACB; bookkeeping only, the eight
    traces 0.7.38's to the byte; an older save at market value in its load
    month, tagged "cost from"; P&L green and red and nothing else, ★D7); its
    measured paragraph could gain the playtest's ledger line;
  - §14's "When the bank fails": the fund's market-book bank shares go with
    everyone's, for nothing, and their cost is realized as a loss; the rescue
    book sold from the bank's page under the fund, the market book first,
    each at its own cost, the page showing both books and their sum;
  - §22's "City History's chart" (the fund's worth on Portfolio and a
    security's price on the big chart, with the fund's decisions as flags;
    the three series kept but not on the picker) and "The decision log"
    ("Order: …" and "Cancelled: …"; fills and lapses on Activity);
  - §23's "The year book, as text and as CSV" (three columns, the fund's
    worth and what was put in and taken out, from 0.7.39) and "Saves" (no
    bump; the ledger and the hand's posted orders inside the fund's own
    state; an older save's ledger seeded at its load, one row saying so).

  It wants §24's counts and rows (247 files, 225,525 lines after the fixes;
  71 harnesses plus the playtest; 70/71; BuildMenuCheck 73 of 73; the eight
  traces byte-identical; 6,782 labelled assertions after the fixes, 6,735 at
  the docs pass; 1,557 dials; a FundLedgerCheck row, the cap counting every
  order among it; FundCheck's row, a named price and a cancel; SaveFileCheck
  · ReadPathCheck's row, the ledger through a save and a 0.7.38 save seeded;
  LongPlaytest's ledger identity; "what no harness looks at": the fund's
  pages not seen rendered), and could add to §25 (★D2, ★D7, ★B1 with the hand
  first, ★B9, `ACTIVITY_ROWS` 1000 and `BARGAIN` 5%; B3's stale mark, B6's
  precision, B10's idle rule, B11's cheap bonds; the fund's worth not on
  History's picker; `Money`'s "$1000k").

~~**The repo has no README.**~~ **Written 2026-09-12** — `README.md` at the repo
root, verified byte-for-byte on the PC: what the game is, requirements, build
and run both ways, `AllChecks` (its flags, its filter, its exit status, and the
run-it-after-a-copy-back rule), `buildings.json` and the working-directory-first
lookup, where saves and logs live and when a format bump is actually needed,
`Build EXE.bat` and the SmartScreen note, a map of the tree, how to add a
sector, and the two places the version lives. **Rewritten 2026-09-14 for 0.5.15 /
save format 26**: ten sectors not seven, 157 files, 55 buildings, 48 harnesses
plus the playtest (49, ~108s), **a fourth rule — never move a harness's premise
to let a change through** — a note that the month now arrives on a clock so every
panel must hold its own scroll position, and the manual link repointed at the
current artifact URL. That artifact is still **private to the account** — decide
what the link should be before the repo goes public. *Still wanted: the
player-facing README that goes in the zip beside the exe, per "Sharing it before
Steam" below.* ~~**AND IT IS BEHIND AGAIN as of 2026-09-15: save format 27 and 50
harnesses.**~~ **CAUGHT UP 2026-09-15 at 0.6.0 — top entry.**

**Make a `.ico`.** No image file exists anywhere in the project, so `--icon` is
not even in the jpackage command — the exe ships with the generic Java icon,
which is the first thing anyone sees. An hour, and it changes the impression more
than anything else on this page.

~~**`APPVER` in `Build EXE.bat` → `0.4.3`**, by hand on the PC.~~ **Both bumped
to `0.4.4` on 2026-09-12** — `GameVersion.VERSION` and `APPVER`, which are the
only two places the version lives. jpackage stamps the batch one into the exe
and cannot read it from the Java side, so they have to be moved together; bump
both on every release. *Both at `0.5.15` since 2026-09-14, and the batch's
findstr pattern was tightened the same day from `"String VERSION"` to `"final
String VERSION ="` — the old pattern also matched a comment in `GameVersion.java`
and survived only because that line happens to carry no equals sign. The
save-format changelog in `GameVersion.java` was two entries behind at the same
time (25 and 26); both are written now, and 27 with the senior split on
2026-09-15.*

*(The exe's jar and `buildings.json` were refreshed by hand on 2026-09-05 so the
bundle runs current code. That is not a substitute for a clean `Build EXE.bat`
run before handing it to anyone — and it is now much further out of date than it
was. `dist\CityBuilderSim\buildings.json` is from 2026-09-07 and the game prefers
an external file beside the exe, so anyone running the exe is on that copy.)*

---

## 1. The only thing that gates shipping: onboarding

The game never says what you are trying to do, and never teaches the causal order
the whole simulation runs on:

```
land -> housing -> people -> jobs -> industry
```

Every confusing thing a new player will hit is that order being violated:

- A steel mill in a city with no housing runs at **21%** — nobody can work it.
- Twenty houses and no employer is a correctly empty city, and nothing says so.
- Private investment could sit land-blocked for **120 months** (now bannered —
  but that was one instance of a class). *And the banner is now itself suspect:
  `Game.isPrivateInvestmentLandLocked()` never clears — see the top entries.*
- **A city with no clinics loses 18% of everything, forever**, and the only place
  that says so is the People screen and one banner.

Concretely, in rough order of value:

1. **State the goal.** A sandbox with no framing reads as unfinished even when it
   is deep.
2. **A first ten minutes that works without a guide** — the founding sequence, in
   the game, not in a doc.
3. **Explain refusals where they happen.** The advisor already knows why each
   sector is holding; the player mostly cannot see it.
4. **Make the UI look like a game.** A Steam buyer's entire first judgement is a
   screenshot. This is also why the store page comes last.

*(2026-09-06 chipped at 2 and 3: the build menu now prices every row and explains
every building on hover, and the top strip pronounces the date and cash
(2026-09-21: and prices and the exchange rate beside them, and the Exchange
page says the founders left a vault). See
`build-menu-info-and-receipt.md`. 2026-09-08 rebuilt most of the rest of the UI.
2026-09-09 gave the Real estate screen two rent markets with a plain-words note
about what a high family figure beside a low studio one means — the first screen
in the game that explains a *shape* problem rather than a quantity. That night
the main menu's Resume button learned to load the autosave on a cold start
instead of drawing an empty city — small, but it was the very first thing a new
player could get wrong. The founding sequence itself is still untold. 2026-09-11:
every sector's screen is one generic page now — what it makes, what it buys,
where it went, at what price — so a seventh sector, or a thousandth, gets a
screen for free.)*

---

## 2. The rebalance

**Stages one and two are done and deployed, and the material unit with them.**
See `the-rebalance-stage-one.md`, `the-rebalance-stage-two.md`,
`the-price-that-stopped-being-a-price.md`, `why-the-bank-kept-failing.md`,
`the-whole-codebase-audit.md`, `the-lender-gets-a-gap.md` and now
`the-unit-of-material.md`.

Stage one put the household economy on real figures: wages on NS Job Bank medians
(unskilled $800 → $3,460, spread 10x → 4.5x), the three residential templates on
real build costs (cost per head now flat with the studio dearest, as in reality),
`LANDLORD_YIELD` 17.3% → 5.8%.

Stage two did the same for everything else, at Jerus's call ("full realism
pass"): **all 41 buildings on sourced real capital costs at the size the game
actually builds them**, the iron band rebuilt on real steel and scrap prices, the
Iron Mine's crew cut from 376 to 60, and the founding endowment $500M → $3.5B to
keep the opening exactly where it was.

Between them they turned up **three real bugs** and **eleven fixtures** that had
stopped causing the conditions they test.

### ~~Three decisions waiting on Jerus~~ — decided and built 2026-09-10 (night)

See `a-firm-that-cannot-pay-sheds-plant.md`. Bankruptcy is declared and the
overdraft forgiven with the loan; the distress rule applies to all six sectors
on a two-year fuse; the food plant is a fifth of the size and idles rather than
flood its own price; Construction pays profit tax. **No sector ends the run at
negative cash.** Underneath the four: construction in progress was on nobody's
balance sheet (every plant built on credit defaulted a month later, for the
length of the build), Retail's output side was four to five times low (the
"last unpriced half" below — done), an interest reserve above the shortfall
ceiling, two money leaks the shedding exposed, three caches not carried, four
fixtures that stood next to their condition.

### Two questions that came out of it — Jerus's

- **The bust when the ore runs out.** The city grows to 24,000 by month 2,000,
  then Mining winds down and Heavy Industry loses its cheap input: jobs go
  11,849 → 6,776 in one checkpoint, half the city leaves, it settles at 12,000.
  Worst unemployment on the run is **69%**, at the trough. This morning the bust
  was invisible because the two sectors paid 1,740 wages for 1,400 months with
  money from nowhere. Whether five thousand jobs should go in two hundred months
  is `MAX_RETIREMENT_FRACTION` (25% a month); whether the ore should ever run out
  is the iron reserve's design. *Since 2026-09-11 a mine over a worked-out
  deposit closes within the year instead of borrowing to pay its crew for
  three; the bust is sharper and the bank survives it.* *And on slot 3 at a
  million people (2026-09-15) it took Manufacturing with it through the steel
  price — see the top entries.*
- **Food exports.** Spare nameplate is made for export at 0.6 of the import
  price. At 0.9 of the local price the food industry ended a run holding $72bn;
  at 0.6 of the world's it holds $7.8bn beside Retail's $7.0bn and exports 900
  units a month. The wedge is a number with an argument, not a source.
  *Materials' wedge is 0.4 since 2026-09-11, for the same kind of argument:
  at 0.6 six export plants doubled a fixture city.*

### POLICE, CRIME AND PRISONS — built 2026-09-11 (late night, last), see `crime-has-reasons.md`

Jerus: crime is a function of unemployment and tight or missing homes; police
cut it drastically and never to nothing; prisons; only adults. Every adult at
liberty sorted once into his graded reasons (no home 5, past EI 4, short of
money 3, on EI 2, crowded 2, no reason 0.1) plus 2 an adult for the police the
city does not have; full coverage is twice Canada's 180 officers per 100,000
and takes 90% off; K struck from Canada's 5,585. A quarter violent — half a
month off each on the sick rate, 0.115% killed — the rest theft, half from
savings and half from the tills, handed to the offenders. 9% of crimes times
coverage caught, six months each, caught and not held with no cell; prisoners
out of the labour force and into their own ledger, debts frozen. Four buildings,
the city pays, a Safety area on Services, graphs, a notice, `CrimeCheck`.
Eight seeds: **crime 1.8–2.3 times Canada's before the first station, a tenth of
it at the end, and all that is left is the long-term unemployed and the
crowded.** **What it opened:**

- **THE CITY'S SIZE IS HYPERSENSITIVE TO A SMALL STEADY DRAG.** Seven hundredths
  of a point added to the sick rate, with no crime at all, ends seeds 0 and 3
  9% smaller; at a thousandth of crime's strength seed 5 ends 16% smaller. The
  crime ensemble's 6% smaller cities are this, not crime. It predates the
  batch, and it means an eight-seed median can hide or invent a 10% effect.
  **The first thing to look at next.**
- **A LOCKUP.** The smallest prison is 300 cells for $360M; a city of 15,000
  needs about twenty. No seed ever built one — 2,100–2,900 people caught over
  333 years with nowhere to go. A 40-cell lockup, or cells in the headquarters,
  is Jerus's call.
- **Education's cost is not in GDP.** Healthcare's is, and now the police's.
- **Migration from Cullen and Levitt (1999)**, not the rent: a tenth off at
  twice Canada's rate. Their loss is mostly people leaving; this is mostly
  people not coming, because a push that carried it would churn a sixth of the
  city a year. The first cut (the rent's shape at half its weight) lost a
  college town 38% of its people.
- **The police are college business jobs**, and a Police Foundations type or a
  protective-services job type would be truer.

### THE RUNNING TOTALS OF THE DEAD — 2026-09-11 (late night), see `the-running-totals-of-the-dead.md`

Every month's dead by age band, and the orphans and the unhoused among them,
recorded; running totals derived on the Reports tab (THE DEAD, *Who has died*).
**What it showed:** on seed 0 more babies died over the run than adults (6,522
against 5,121) — the playtest builds clinics and hospitals and never a daycare.
Worth a daycare in the playtest's founding order, beside the college.
*Since 2026-09-15 there is a sixth band on that table: the over-85s.*

### THE HOUSEHOLDS REMEMBER — built 2026-09-11 (late night), see `the-households-remember.md`

Jerus: a record of the households of each type so the rebuild "has a reference
to try and keep but still allow change". The builder keeps last month's
households as far as the people still exist for them, 1% a month re-forming,
the rest built by the old rule, tiers refitted to the jobs. Eight seeds:
**orphans at the end 490–633 → 11–19, hunger 4% → 0%, unemployment 14.8% →
12.2%.** **What it opened:**

- **A LAID-OFF PARENT ORPHANS THEIR CHILDREN.** Families are built from working
  adults, and an out-of-work adult is a one-person household, so when the ore
  runs out 800–1,000 children have nobody for two or three years, hunger 6%.
  Jerus's call: out-of-work households with dependants, a family that keeps an
  out-of-work adult beside a working one, or orphanages. *And a parent who
  enrols does the same — 35,000 orphans and 6,000 orphan deaths a decade on
  slot 3 once the universities went up (2026-09-15, top entries). The same fix,
  and it is written up as a decision in `the-parent-who-went-to-school.md`:
  three shapes, three money models, Jerus's call. Measured 2026-09-15: a jobs
  event alone took one city from 12 orphans to 2,094, so the fix is worth more
  than the student case suggests and should be written against "an adult who
  has left work" in general. **The prisoner is the one case that should stay.***
- **The money pool's volume is not measured.** The point of remembering was
  wallets not moving between redrawn shapes; `LongPlaytest` should print it.

### THE LONG SICK — built 2026-09-11 (late night, later), see `the-long-sick.md`

Jerus: people who stay sick start dying; the base death rate halved for every
band but babies and seniors. A ring of monthly cohorts per age band; past two
months sick, 4% a month for babies and seniors, 1% for children and teens, 2%
for adults; general care cures 50–90% of the sick a month and its ×3 swing on
adults is gone; babies and seniors are ill twice as often until their own care
takes it away. Eight seeds: **1.8% of all deaths are illness** in cities that
build their clinics; the worst sick month **18% → 28%** in every seed, as
cities outgrew their care; deaths per thousand **14.8 → 15.5**. *Since
2026-09-15 the over-85s die at 8% a month past two months, twice the seniors'
4%.* **What it opened:**

- **FULL GENERAL CARE IS WORTH LESS THAN IT WAS.** A fully served adult died at
  0.15% a year (a third of 0.45%); now 0.225% plus a sliver of sickness. The
  halving does not make up for the swing at the good end. Jerus's lever: the
  halving, not the ring.
- **The playtest's cities boom between months 770 and 1,080** on this tree
  (jobs doubling in 300 months against a third) and outgrow the general care
  the advisor builds. Not isolated; the end populations overlap.
- **Where care is thin it kills in earnest** — 3% of a clinic-less city of
  2,000 in four years — and babies most: twice as often ill, 4% a month past
  two months, on top of childcare's ×40.

### THE PEOPLE OUTSIDE THE FAMILIES — built 2026-09-11 (night), see `the-people-the-books-left-out.md`

Jerus: "before police we will do that." The out of work (on EI, past it,
evicted), full-time students and orphans each keep their own books beside the
families, summed by the five bands; EI is a premium off wages into a
twelve-month ring struck on the inflow of the newly unemployed; students live
on savings, a grant and an interest-free loan the graduates repay; the
unhoused are counted, hungry and sick 3.7 times as fast. Eight seeds against
the template: unemployment **13.8% → 15.6%**, GDP a month **$62.9M → $56.4M**,
hunger **0 → 4%** (the orphans), bank failures **2 → 0**, **nobody evicted in
any seed**. **What it opened:**

- ~~**POLICE AND CRIME** — next, in Jerus's order.~~ **Built the same night** —
  see `crime-has-reasons.md`. The model reads the pool, the housing match and
  the household books cell by cell.
- **THE UNHOUSED NEED A POOR CITY TO EXIST.** Not one eviction in 32,000
  city-months: an out-of-work adult ends a run holding $3.4M. In a young city
  the path works — close the biggest employer and 549 are evicted in a year,
  384 on the street at the worst. The households' trillion (below) decides
  whether a long game ever sees one. Because a cell is an average, its
  households run out together: evictions come as a cliff.
- **GDP A HEAD IS 12% LOWER, and the lower one is honest.** Measured with the
  GDP split and two variants: EI is not it; putting the out of work back into
  the families' wages recovers 380 of the 550 a thousand people. The
  placeholder was spending wages the unemployed never earned. If it matters,
  the lever is what the out of work can spend.
- **NET LOSSES ONLY MAKES THE POOL LONG-TERM.** Four in five of the out of
  work are past their twelve months; the pool empties only as jobs are
  created. Real churn — Canada finds work for about a fifth of its
  unemployed a month — would turn it over and put most of it on EI. Jerus's
  call, as it was.
- **THE ORPHANS ARE 4% HUNGER IN EVERY SEED.** "For now." An orphanage, or
  shelters for the unhoused, is the first building this system asks for.
- **The playtest never builds a college**, so students are only exercised in
  `OutsideCheck`. A college in the founding order would put them in the
  ensemble. *And a college is what `YearBookCheck`'s new fixture builds for
  exactly this reason (2026-09-15): with nobody studying, `workforce` and the
  labour force are the same number and the broken rate cannot be told from the
  right one.*
- **The default playtest has never built a school at all** (2026-09-21): the
  advisor's list has none, so in 4,002 months the whole education system fires
  zero times — `students 0`, `grants $0k`, `student loans owed $0k` — and every
  education dial is inert in the baseline. `-Dplaytest.schools=true` builds
  them proportionately; a school in the advisor's own list would make the
  default run exercise it (and move the baseline, so its own batch). See
  `the-price-of-a-place.md` §5.
- **A graduate starts repaying the month they finish.** No six-month grace;
  one more ring. *Still none after 2026-09-21: with a rate on the loan, a grace
  is months with a balance and no instalment, and it has to say whether
  interest runs in them.*
- **Interest while studying, as a second shape.** 0.6.9 charges a graduate
  only (the Canadian shape: the government carries the interest while they
  study). A loan that accrues from the day it is drawn — the US unsubsidised
  Direct Loan — would be a second setting on the rate dial.
- **Whether the founding tuition table should index to wages**, as the pension
  base does, rather than wait on a scale of up to 5× — the alternative the
  tuition dial stands in for (2026-09-21).

### THE SECTOR TEMPLATE — built 2026-09-11, see `the-sector-template.md`

One template, seven classes, a goods economy, save format 21. Eight seeds
against the exchange baseline: bank failures **10.5 → 2** over the batch
(0 on the baseline), and **the city is a quarter smaller** — 13,750 people
against 18,300 — for a reason that is a design question rather than a bug.
**What it opened, in the order it is worth doing:**

- **DENSIFICATION IS NOW A RULE TO WRITE.** The baseline's landlord scrapped
  occupied family houses in every loss streak and rebuilt the plots as
  apartment blocks; the churn filled a nine-year queue, the queue built
  forty-seven depots, the depots' idle crews were paid by the advisor's
  subsidy, and the jobs pulled a bigger city. The template's landlord will
  not sell a door somebody lives in (`RealEstate.mayRetire`) and stops at
  "housing ahead of jobs". Whether a landlord redevelops occupied houses
  into flats when land is short — buying the tenants out, rehousing them,
  paying for the demolition — is a real mechanic, and the one that decides
  whether the city gets back to 18,000. Jerus's design.
- **THE LATE-GAME BANK, AGAIN, FROM A THIRD SIDE.** With the landlord
  repaying its loans the book goes to nothing by month 1,800 on every seed;
  the vault earns the world's rate now (`Bank.placementIncome`) so it no
  longer drains, but it does not grow either, and both failures left are
  after month 3,200: a dealer long the dying mining company's shares, in a
  city with $50bn of savings and nobody to lend to. The item that was
  "what it does with idle deposits, and why nobody borrows the money that
  exists", one door further along. A large-exposure limit was tried and
  withdrawn — see "Not doing".
- **The dealer carries every dying company.** The bank buys emigrants' and
  the world's shares at its bid and marks them; when a company's assets go
  (mines with no ore, valued at cost until they are sold), the bank eats the
  fall. Real market-maker risk, and a real reason the desk should mark a
  company on what it can earn rather than what it paid for its holes. With
  the bank item.
- **Material is drawn as the crews build.** The order-day draw is gone
  (`BuildingsStacks.materialsOwed`, `contractValue`); the builders carry
  the material price between the invoice and the draw, as a contractor
  does. What that leaves: a mature city's construction is still bursty —
  the advisor orders a batch, big crews finish it in three months, nothing
  for a year — so the materials plant's market is a spurt every ten months
  and a 480-unit shed. The seventh sector built one plant a century on seed
  0 and lost money on each until the visible-demand cap stopped it. A
  buyers' stock — builders' merchants who buy steadily and sell into the
  spurts — or a bigger shed on the plant, is the next materials question.
- **Industry is one sector and the mill's cloth is food.** Jerus: "keep it
  all as food for now." The split into Food Processing and Textiles, with
  `TEXTILES` exported at the world's price, is one class and one good when
  the basket wants it.
- **The basket is two lines.** `GROCERIES` and `HOUSING`. The third good is
  a line, not a handler; the first candidate is whatever the mills make
  when Industry splits.

### THE PRICE BATCH — IN PROGRESS, superseded by `why-there-is-no-inflation.md`

*Kept for the original reasoning. Two things in it turned out to be wrong and
they are worth keeping visible: money creation is NOT "the fall's cause" — the
money supply already grows 78x — and #50's "kill the floor" has to mean MAKE IT
LIVE rather than remove it, because removing it opens a currency deflation
spiral the constant had been holding shut. See the write-up for both.*

- **#53 MONEY CREATION.** `bank.lend()` is `cash -= amount`. There is no
  deposit creation anywhere in the model, so the money stock is fixed at the
  founding endowment plus whatever the trade surplus drags in, and the price
  level falls as output grows. Fractional-reserve deposit creation and/or
  treasury monetisation. Turns `MoneyAudit` from a conservation law into an
  identity **with a named creation term** — which is the hard part, and the
  reason this is a batch of its own.
- **#50 KILL THE DEAD SHELF FLOOR.** `OPENING_SELL_PRICE` is a hard floor of
  $300 and it is still binding after 333 years: `shelf price 0.3000` to four
  decimal places while wholesale food is $154. Replace it with a real cost
  floor that includes the shops' payroll and rent.
- **#51 WAGES INTO THE COST SIDE.** Retail's cost-plus is on wholesale food
  alone — payroll, rent and interest are all outside it — so a wage rise cannot
  reach a price. Building costs should index to the wage level too;
  `updateConstructionCost()` is currently a no-op.
- **#52 LET THE SHELF CLEAR IN MONEY TERMS.** The scarcity mark-up is
  unit-based, so it only fires on a physical shortage. Excess demand measured
  in money, not units.

### Still open, in the order it is worth doing

- ~~**`MATERIALS_WORLD_PRICE` is about 9x too low.**~~ **Done 2026-09-10
  (afternoon)** — Jerus: "I think 2 is an issue that delayed makes it worse."
  $18,000 a unit, every template's split re-derived at a real material share
  (40% a building, 25% a plant, 60% a road) with every total held to the
  dollar; the yard and the plant held at what they were worth; save format 20
  reads an old yard at its old value. **And it found that the material import
  bill had never been paid** — builders' expense $0 over 4,000 months against
  $2.75bn imported in the accounts, so the balance of payments never saw a unit
  cross the border and Construction ended every run holding the money. Paid
  now, to the dollar, with the material earned on delivery so the bank does
  not eat the accrual. See `the-unit-of-material.md`. **What it opened is in
  the three items below.**
- ~~**THE LONG RUN IS SMALLER NOW, AND IT IS NOT YET UNDERSTOOD.**~~
  **Understood, 2026-09-10 (late afternoon)** — see
  `the-surplus-has-nowhere-to-go.md`. The food-export basin was the playtest
  advisor: its trigger read the food shed, which the plant throttle now keeps
  at two months of demand, so it built 163–185 plants a run. Gated on nameplate
  against demand; unemployment back to 16% on the same seeds. What is left is
  the steel chain, below.
- ~~**THE SURPLUS HAS NOWHERE TO GO — Jerus's decision.**~~ **Decided
  ("outward investment") and built, 2026-09-10 (evening)** — see
  `outward-investment.md`. The sectors' idle cash buys the world's paper when
  the world pays more than the bank, comes home when the bank pays more, rolls
  its coupons abroad, and the currency is priced on the overall balance. On
  eight seeds: population 11,600 → 21,500, GDP $36M → $74M, unemployment
  worst 64% → 48%, exports $33M → $109M, the currency within 10% of parity in
  every seed where it sat 26–60% off, written off $4.5–7.9bn → $1–3bn, and
  the spread between seeds collapsed. Fifty-seven mines standing at the end.
- **A bank with no lending business.** *Since the exchange the bank does not
  fail — 0 failures in six seeds of eight, 2–3 in the others, against 10 —
  because the trading desk is a business and a profitable one, and a sector
  that has paid its owners borrows for its next mine (`bizDebt $320M` at the
  end of seed 0 where it read $0k). But the book is still $0 on $114bn of
  deposits at the end of seed 0, the deposit rate is 0%, and the desk is the
  bank.* The item it was, with a bank that now lives long enough to be
  asked: what it does with idle deposits, and why nobody borrows the money
  that exists. *2026-09-11: it places its idle reserves abroad now, which
  stops the drain; the rest of the item stands — see the template block.*
- ~~**THE HOARD** wants the corporate sector to have a way to pay its
  owners.~~ **It has one, 2026-09-10 (night)** — see `the-owners.md`. Seven
  companies with shareholders: the households first, the world for the rest;
  offerings sized by each company's own year and its equity target; forty
  percent of a profitable month paid out; the bank's capital sold for real.
  **And the hoard is untouched: US$1.2–2.7tn.** The payout is on net income
  and net income does not contain the coupon earned abroad — yesterday's
  rolled coupon is off the statement on purpose — so forty percent of nothing
  is paid on the thing that compounds. **Jerus's call:** put the coupon in the
  payout base (one line) and measure what it does to the currency.
- ~~**THE EXCHANGE.**~~ **Built 2026-09-11 (night)** — see `the-exchange.md`.
  The bank is the dealer: it quotes round fair value, its book moves the
  quote, it never sells what it does not hold and marks what it holds at the
  lower of the quote and fair value. Emigrants sell on the way out, the world
  trades on yield, households buy the best yield then the next, companies buy
  back a tenth a year or pay a special dividend when the market is dear, a
  household short of money sells before it borrows, and shares split at a
  hundred times their founding price. Eight seeds against the owners' batch:
  GDP $65M → $77M, worst unemployment 46% → 37%, bank failures 10 → 0,
  exports $104M → $124M, the currency within 3.5% of parity, dividends to
  households $3–18bn → $30–56bn a run, every company home-owned in six seeds.
  *And on 2026-09-15 the "best yield first" rule turned out to be undefined
  whenever the model prices two companies on their earnings, because it prices
  every such company to exactly the discount rate — see the top entry.*
  **What it opened:**
- **THE HOUSEHOLDS' TRILLION.** The hoard moved; it did not shrink. The
  sectors end holding US$22–52M abroad where they held US$1.2–2.7tn; the
  households of seed 0 end holding **US$1.1tn** abroad and $114bn at home,
  $769M of coupons a month. The same 333 years of two percent on a surplus
  nothing spends, now owned by the people who earned it. Sane over a player's
  horizon; over the playtest's, the question the owners' doc asked, moved one
  door along. **Jerus's call**, as before: the coupon in the payout base, or
  something that spends it. *And since 2026-09-11 (night) it is why nobody
  is ever evicted in a long game: the out of work draw on the unskilled
  tier's savings, and the unskilled tier holds millions. See
  `the-people-the-books-left-out.md`.* *On slot 3 at 272 years (2026-09-15)
  the coupon line alone is 2.6 times GDP a year — about $4tn abroad.*
- **THE BUBBLE.** With $65bn of household savings chasing companies worth
  $10bn and a deposit rate of 0%, the quotes sit at two to four times fair
  value until the yield hits the deposit rate plus a point. It is the ceiling
  working, and it is what a city with no lending does with its savings; the
  companies pay special dividends into it and issue into it. A bank that lent
  would end it. The bank item, from the other side.
- **The founding stakes** belong to the founders (now) or to the treasury —
  still Jerus's call; one contained change either way.
- **Do savers want the sectors' appetite?** The households' paper abroad
  uses `OutwardInvestment.APPETITE` (forty times the spread, four fifths of
  idle savings at a two-point spread); a household in the PC play-test held
  $414k abroad against $99k at home. It is the dial that fixed the currency,
  and it is a dial. Jerus's.
- **The Build tab's list scrolled back to the top after +/Build on the
  PC**, or the wheel never reached it - seen three times in the play-test,
  not reproduced on purpose. Look at `handleAllBuildingMenus` against
  `keptScroller` when next in the UI. *This is now a class rather than one
  bug: the month arrives on a clock, so every panel has to hold its own
  scroll position through a repaint nobody asked for.*
- ~~**Households' money was seven rows.**~~ **Sixty-eight cells, 2026-09-10
  (evening)** — Jerus: "per household and pay tier type... an object, being
  the basic, and then each extends." `Household` / `WorkingHousehold` /
  `RetiredHousehold` inside `HouseholdBalance`, the money following the people
  across the monthly rebuild, weighed by who carries it; the save names its
  cells; the screen shows what one of them has. See
  `every-household-keeps-its-own-books.md`. *And the weight it is carried by —
  `grownUps()` — is what silently starved the over-85s of their pension for a
  batch in 2026-09-15; see the top entry.* **What it made visible:**
- **RENT PER CELL SHOULD FOLLOW THE DOOR.** The match bills the landlords by
  unit size and puts a senior alone in the smallest unit that fits; the
  households' books charge everybody the city's average door
  (`rentPerHousehold()`). So a pensioner in a one-room flat pays a family's
  rent, and **Senior living alone** is the one cell in trouble in every seed:
  −$46 to −$294 a month after the rent for the first decade, $8,970 owed at
  the ceiling, locked out; in seed 1, 916 of them at the ceiling for nineteen
  years with 9% of the city hungry. `SocialSecurity`'s own note predicted
  pensioner poverty "concentrated among people living alone" — designed, and
  never seen, because the retired row averaged the couple with the single.
  Record the rent weight per shape in `FamilyModel.house()`, hand each cell
  its own door's rent. **Next after the bank.** *Half of it since 2026-09-11
  (night): the rent bill is split over the doors paid for, and the people
  outside the families pay their own door share (a flatsharer a fifth, a
  doubled-up seeker none). Families and the retired still pay the average
  door, so the pensioner alone is untouched.* *And since 2026-09-15 there are
  two more cells of the same kind beside it — **Elder living alone** is now the
  one to watch first when senior care is short.*
- ~~**Households' savings abroad.**~~ **Done 2026-09-11 (night)**, inside
  the exchange batch, because it had to be: the exchange returned the hoard
  to the households and the currency went 31% past parity with exports
  halved until they had the sectors' door. `Household.abroad`, in dollars,
  the sectors' four dials, sold before shares when the household is short,
  emigrants' dollars leave with them; sixteen slots a cell in the save.
- ~~**Wages are spread over every adult in a tier**, employed or not — the
  `FamilyModel` placeholder, visible now as an unskilled single adult taking
  home $1,262 in a month of 40% unemployment. Wants an employment model.~~
  **Done 2026-09-11 (night)** — the families are built from the adults who
  work; the out of work have their own books. See
  `the-people-the-books-left-out.md`.
- **A dead pensioner's savings vanish** as "taken away"; nothing inherits. A
  transfer from the retired cells to the working ones, when somebody wants it.
- **The grid's statement reads the row's average interest.** The cell's own is
  in `Household.interest()`; `statementFor()` should take it.
- **Hot money never reaches the balance of payments.** It moves after the
  audit strikes and `Bank.startMonth()` zeroes the counters before the next
  strike, so `HotMoneyIn` is always zero in the Result and the currency never
  feels an inflow. Latent while no run holds a persistent stock.
- ~~**The steel screen was in the mill's favour three ways.**~~ **Fixed
  2026-09-10.** Steel priced in dollars against ore in local money, read at
  nameplate while the sector ran at 52%, gross of payroll. Net, at the
  sector's operating rate, in the city's money — the mine's corrections,
  applied to the mill. The playtest advisor no longer sinks a mine on any
  unworked deposit or a foundry for the jobs alone unless one would pay.
- **The bank lends 90% against a mine.** Written down to 60% on default, five
  mines' worth takes its equity every cycle. A cyclical exporter is not a
  house. Look at it once the currency stops making every mine default. *Half
  of it since 2026-09-11: a mine with no ore is sold rather than borrowed
  against, so the book the bank lends on is at least a working mine.*
- **Construction's economics.** The municipal works department's 400 points a
  month are billed by the private sector at full price with no wages behind
  them — most of the $6–13bn it ends every run holding, and why 55 depots go up
  in a starving fixture city with a 437,000-point backlog. The depot's revenue
  proxy is `points × materials price`: measured $6.1k a point against an
  actual $8.8k (it was $0.68k, thirteen times low). The plant's 160 units a
  month is a value-added argument, not a source; 400 comes back if it ever buys
  its inputs abroad. *Since the template the builders buy their material as
  they build and keep the order book per site, so the statement is honest
  month by month; the works department's free 400 points are unchanged.*
- ~~**RETAIL'S OUTPUT SIDE IS THE LAST UNPRICED HALF.**~~ **Done 2026-09-10
  (night)** — a Convenience Store covered 120 people with five staff, payroll 57%
  of revenue where real convenience retail runs about ten; 480 now (shelf 1,400),
  the Small Grocery 1,600 (7,000). The same shape as the bank at 180% and
  industry at 74.7%. *What is left of this item is the floor:*
  `sectors/Retail` floors the shelf price at `openingSellPrice`, a
  founding constant of 0.30 that never falls. With food at 0.09–0.16 a unit the
  shops sell at 0.30–0.38 whatever it cost them — comfortable rather than
  obscene now, but still a constant doing a price's job, and still the reason
  the CPI cannot fall below ~0.68.
- ~~**STAGE THREE: the wage-price loop, and it now has a named victim.**~~
  **It was not a wage-price loop. Fixed 2026-09-10 (late).** Wages *fell* 41%
  over the run and steel was flat at ~$850/t in world money; what moved was the
  exchange rate, which sat 55% below parity because `WorldEconomy` reported a
  3.3% headline while its level rose 0.18%/yr, and the currency drifted on the
  headline. One line (`realisedInflation()`), and Heavy Industry does not borrow
  a cent in 4,000 months. See `the-lender-gets-a-gap.md` §6. What is left of the
  decay is the retirement decision above.
- **There is only one power plant and it is 325 MW.** A city of 74,000 needs a
  fraction of one plant, and the plant is now correctly priced at $1.43B, so the
  player buys an enormous thing they will never fill. Wants a small plant beside
  it at the same $/kW. Same argument as the three roads: they exist so each wins
  a band. *(The Wind Farm, added 2026-09-09, is the first step of this.)*

---

## 3. Things a new player will actually hit

Ranked by how likely they are to read as "this game is broken".

- ~~**THE HOUSING STOCK IS THE WRONG SHAPE**~~ **Done 2026-09-09 (evening)** and
  finished 2026-09-10. Five problems in the first pass; the sixth and largest was
  found on Jerus's month-1124 save the next day: **rent had stopped being a price
  and become a constant.** A floor taken on the whole company was applied to each
  segment separately, and in any mature city that floor binds — so every
  residential building in the game earned exactly $445 a head whatever it cost to
  put up, and the city built the cheapest one it owned for ever. 138 low-rise
  blocks, 96 houses, **0 studios, 1,847 households wanting one**. Each leg is now
  struck on its own segment's costs and the carry is made up across the blend.
  Over 4,000 months: 0 studio doors → 19,920, both markets clearing at ~1.0
  households a door. See `the-price-that-stopped-being-a-price.md`. *Slot 3 at
  272 years reads 0.98 homes a household and the crime table counts two adults
  in five as doubled up — see the top entries; the shape may be wrong again at a
  million people, or the count is.*

- ~~**The bank fails about 125 times in 4,000 months.**~~ **Fixed in two passes
  on 2026-09-10.** The morning pass (`why-the-bank-kept-failing.md`) found the
  branch cost, the missing underwriting, the branch-as-recapitalisation loop and
  the flat exclusion. The late pass (`the-lender-gets-a-gap.md`) found that the
  lending ceiling and the insolvency trigger were **the same constant**, so the
  underwriter parked every borrower exactly where a one-dollar fall cost the bank
  60%; that the borrower's default record was not saved (an 80-month ban cleared
  with Ctrl-S); that a failed bank could still lend ($5.05bn written while
  frozen); that a rescued bank came back with zero buffer and re-failed monthly,
  which is what the 308 was counting; and that a bank with no book asked for no
  capital, which is why every long run ever recorded ended "equity $0k against a
  book of $0k". Over 4,000 months: failures **308 → 20**, written off **$25.8bn
  → $1.35bn**, city recapitalisation **$3.76bn → $685M**.

- **Unemployment settles high and stays there.** *Was 41.5% at the end of the
  4,000-month playtest. The rent fix took it to 19.0%, the industrial fix to
  15.1%, the bank pass to 15.6%, the audit batch to 14.0%, and the distress batch
  to **17.1% at the end with a worst of 69.2%** — the worst is now the trough of
  the bust when the ore runs out, which is a real event the earlier runs hid
  (section 2). The end figure has been between 14% and 17% for four batches.
  13.8% on the template's eight seeds, worst 42%. 15.6% since the out of work
  left the families (2026-09-11, night), worst 40%, four in five of them past
  EI.* *Slot 3 at a million people is at full employment with 11,000 posts it
  cannot fill (2026-09-15) — a played city with universities and a land wall,
  not the ensemble's; the year book's 14% there was the students, and so was the
  5% the Reports chart drew until the same day. Every figure in this bullet is
  the model's own rate and stands.*
  (L2)
- **A city ends up 15–160% above its own target and never comes down** —
  arrivals and departures both sit at zero while births keep adding. (L1)
  *Slot 3: a quarter above target, zero arrivals for sixty years, and the whole
  natural increase — 200,000 a decade at a birth rate of 26 per thousand —
  leaving. The birth rate is the lever; see the top entries.*
- ~~**The treasury swings by its whole debt twice a year.**~~ **Closed
  2026-09-22 (struck late; 0.7.0 closed it):** the emergency note is retired —
  `EMERGENCY_NOTE_MONTHS` is gone, a treasury below zero is advanced by its
  central bank up to a ceiling the player sets, and a save still carrying a
  note runs it off; see `the-central-bank-opens.md`. *Was:* the city's entire
  borrowing ended up as ONE six-month bill that it rolled for ever: at month 512
  it repaid $1.44B it did not have, sat $1.4B overdrawn for a month, and the
  emergency-note path immediately wrote a new bill for slightly less.
- **Long bonds are strictly dominated past ~15 years.** At 20y+ they have a
  higher monthly payment *and* a higher all-in cost than a medium bond.
- **The early risk-free rate is 19%, and it is now blocking a second system.** At
  19% no residential template in the game clears its own financing. Same root as
  the 18-point bankless premium (findings #11). *The bankless premium is gone since 0.7.7 (a city
  with no bank borrows at the window's price); whether the 19% is still true was
  not measured.*
- ~~**THE CITY BORROWS TWO POINTS UNDER ITS OWN POLICY RATE.**~~ **Closed
  2026-09-22 (struck late; 0.7.0 closed it):** `CITY_DISCOUNT` is deleted; the
  city's note prices at the dial plus its spreads and the term premium sits on
  top for the longer maturities (0.7.1) — the redesign the entry below asked for
  is built. *Was:* Jerus, 2026-09-12:
  the Policy screen says 3% and Finances says 1% on the same morning, because
  `DebtManager.floorRate()` is the dial less `CITY_DISCOUNT` (.02) and a city
  with no debt sits on that floor. No borrower is cheaper than its own central
  bank. **Decided: leave the pricing, fix the screens** — "much later when we
  will redesign it realistically aka central bank stepping in to keep it at that
  rate and longer durations deviating." Both screens now name which rate they
  are showing and where the discount is; the Policy page's "so the city pays
  over it" compared the DIAL with the world's 2% while the line above it said
  the city borrowed under the world — it uses the city's own rate now, which is
  what the carry trade actually reads. The redesign is a term structure and an
  operating central bank, and it is its own batch. *Since 2026-09-14 the floor
  is also floored at the bank's own marginal cost of funds, so the discount can
  no longer take the city below what its lender pays — see "nothing borrows
  below cost".*
- ~~**Construction has a profit lever that collects nothing.**~~ **Charged,
  2026-09-10 (night)** — Jerus's call. It pays like the other five, on the net
  income it banked, and the figure is carried in the save.
- **You receive more than you asked for.** Face rounds up to the instrument's
  granularity and proceeds follow the face — ask for $5.0M, get $5.82M. (F1)
- ~~**Long bonds are gated by a silent $100M minimum face.** (F2)~~ **Closed
  2026-09-22 (struck late; 0.7.0 closed it):** `Game.minimumIssueSize()`
  replaced the silent floor and the borrow page states the rounding ("Issues
  round to …").
- ~~**A broke city cannot skip, only step.**~~ It can since 0.7.15: the skip
  runs through an empty treasury on the central bank's advances (found stale
  by the manual pass, 2026-09-28). The emergency is exactly where the
  game is slowest to play. (backlog 22)
- **"Fill %" means job fill, not occupancy.**
- **The middle column does not scroll.** Wrapping `rootMenu` in a `ScrollPane`
  touches every screen, so it wants doing on purpose.
- **`-$0` prints for any debt-free household**, and the orphan bands are three
  orders of magnitude apart — two of the four anomalies read off slot 3 at month
  2305; see the top entries.
- ~~**Two incomes on screen.** The header's TREASURY line is `Game.getIncome()`,
  the month's net income: what the budget earned. Finances' TREASURY cell is
  `Game.getTreasuryChange()`, the change in the cash, which also counts the
  city's own building and land, borrowing raised and repaid, and the rest of
  the treasury's month. In Jerus's city the tile said +$1.5B while Finances
  and Government said the cash grew $2.3B (`playing-0-7-23-ui-notes.md` §1).
  Since 0.7.24 the money block's tooltip names its figure and gives the
  change in the cash beside it; the figure itself is unchanged
  (`runs/ui5-notes.md` §8.1). Still two figures a player sees.~~
  — **closed in 0.7.31** (`earned-surplus-banked.md`): three figures, named
  everywhere: the header's line is what the month EARNED ("+$X earned a month";
  the drawer's "Earned"), beside the budget's SURPLUS and what the cash BANKED;
  the header's (i) names all three, and Government's FROM EARNED TO BANKED
  names every step between them, the first column from
  `Game.getEarnedToBudget()`. D2's wording is for Jerus to confirm (section 0).
- **THE BUILD CARD'S TWO BUGS AND ITS WORDING** (found 2026-10-01 by 0.7.25's
  docs pass and implementer: `runs/ui6-docs-pass.md` flags 1–5,
  `runs/ui6-notes.md` §8.7–8.9; `one-card-for-every-building.md` §10), none
  changed:
  - **The bank's branch card prints a founding constant.** Its detail ("brings
    $32.0M of shareholders' capital") reads `Bank.PAID_IN_PER_BRANCH`, not the
    bank's own `getPaidInPerBranch()`, which `redenominate()` scales; after a
    currency reform the card states the old unit's figure. One read plus a
    harness line (BuildCardCheck does not hold the detail line); behaviour,
    so left.
  - **"this one:" is hidden by a substring.** `Investors.showOwn()` tests
    `!word.contains(building)`, and "Bakery" is inside "Industrial Bakery",
    the only such pair in `buildings.json`: when Industry's word names an
    Industrial Bakery, the Bakery card loses its own gate. A whole-name test
    would fix it.
  - **Wording, Jerus's:** "nobody licensed" on the quote whenever the spare
    licences fall short, even with some spare (20 against the 39 one
    Engineering Services Office needs; "too few licensed" would always be
    true; it is the spec's word); "you will be offered a bill" on every
    card's quote line now (the short-of-cash page offers a 20-year bond
    first; carried from 0.7.24); the maker groups' "the city used N t of
    steel" prints the month's demand, which a short market did not meet
    ("wanted" would match the Restaurants note); "1 business college" beside
    "13 business-college graduates" (one post is `jobLabel()`, several
    `jobPlural()`); a maker's hero names its first good in `Good`'s order,
    so the Snack & Oils Plant "makes 8,000 kg of cooking fats" with "and
    10,000 kg of snacks" in the detail.
  - Not a bug: in a city 99.9% built over nearly every industry and farm card
    reads "this one: no land" and quotes "short N sq ft of land". That is
    land as bar 2 doing its job; his 95% city will show fewer.
- **THE LAND OFFICE'S WORDING** (found 2026-10-01 by 0.7.26's docs pass:
  `runs/ui7-docs-pass.md` flags 1, 2, 5 and 6; `the-land-office-redrawn.md`
  §7), none changed:
  - **The value bar's tick says "the middle of the nine a square foot"**
    (`LandScreen.plotCard()`). True while `LISTING_SIZE` is 9, which it
    always is (the listing refills to it), but it is the one player string
    that names the count, after `offerInfo()` was changed to read
    `LISTING_SIZE`. The same "nine" is in LandScreen's header and banner and
    GameVersion's 0.7.26 entry.
  - **The Margin card's (i)** works "Investors pay ×N the ground's price at
    the founding rate, because X% … is built on" live from
    `scarcityMultiplier()`, while "investors pay" beside it was struck at the
    month's re-price or the last purchase. On a city that builds between
    presses, the multiple and the two prices on the card need not multiply
    out exactly.
  - **One plot's funding tooltips** say "The vault is short of them" / "The
    cash is short of them" (`next()`): a number slip.
  - **Build's no-land and no-deposit pages' buttons read "Go to the Land
    Office"**, where the rail, the title and SectorScreen say "Land office"
    (older than the batch).
- **PEOPLE AND HOUSEHOLD MONEY: WORDING AND ODD CASES** (found 2026-10-01
  by 0.7.27's docs pass and implementer: `runs/ui8-docs-pass.md` flags 1–4,
  6, 9 and 10; `runs/ui8-notes.md` §9.4; `people-at-a-glance.md` §7), none
  changed:
  - **The hunger is people times how far short, not a headcount.**
    `getHungerRate()` sums `people × (1 − ate / subsistence)`, so a city
    where everybody ate 58% of a basket reads 42%, not 100%. Yet the
    drawer's HUNGRY says "42.2% of people", its tooltip "42.2% of the city
    ate less than a basket this month", GOING SHORT's dials "share of the
    city eating less than a basket", and "49 can't afford" is the same
    people-equivalent. In the 2,400 city the shops handed over 3% of what was
    planned, so far more than 42.2% of people ate less than a basket. "The
    city is 42.2% short of its baskets" would be exact; a wording decision
    for Jerus, not a slip.
  - **Pensions' red sentence** ("They are eating less than they need, and
    that is in the sick rate.") fires on `isGoingShort(RETIRED)`, a plan
    short of what the row wanted, so a pensioner row that bought a full
    basket and less than it wanted reads red. Spec ★16 chose this form; D3
    measures the rows below the rule by the basket instead.
  - **The matrix's "working households" row** sums the rows that have a tier
    (D11), but its last cell, under "total", is every household with the
    retired: 24,941 working against 34,667 on one row.
  - **Services disagrees with itself on care cover.** THINNEST COVER reads
    `Healthcare.getCoverage()`, net of the priced out, and its note still
    says "<care> — build that next" when what binds is the fee; the same
    page's General care / Childcare / Senior care "Covers" lines read the
    beds, and so do NEEDS YOU and Build (0.7.27's D16). Equal until fees
    bite. ServicesScreen was left alone for the 0.7.28 merge.
    **Services' half closed in 0.7.28:** its rings read `getCoverage()` (its
    D4), the priced out a step of the funnel; NEEDS YOU and Build still read
    the beds (D16, CONFIRM in section 0).
  - **The why sentence's "People are leaving. N% of the city's payroll sits
    in trades that have been shrinking…" fires on any departure**, crime's 5
    in the 600 city among them, while the decline share can be 0 (it did not
    fire there: housing won first). Kept word for word;
    `getLastWorkDepartures()` would make it true.
  - **The month's key** reads "died " + the part's name: "died of age",
    "died of illness they did not get over", "died killed", "died aged out
    at 120".
  - **"Nobody feeds the orphans"**, the alert's new title, shows when
    anybody has no home and there are no orphans: the condition is the old
    sentence's (`orphans >= .5 || unhoused >= .5`), and its first sentence
    speaks of the orphans.
  - **When the evacuation guard bites** (a month that would remove more than
    the whole population), `getLastWorkDepartures()` is scaled with the mix
    and the broke and the crime leavers are not, so the three causes can add
    to more than `getLastDepartures()`. The Moved-out bar stacks its parts in
    proportion, so its height is right; the popover's "Moved out: N - W for
    want of work, B broke, C driven out by crime" would not foot.
- **SERVICES: WORDING AND ODD CASES** (found 2026-10-01 by 0.7.28's docs pass
  and implementer: `runs/ui9-docs-pass.md` flags 1, 3 and 8;
  `runs/ui9-notes.md` §8; `services-at-a-glance.md` §7), none changed:
  - **The ADULT STUDY lane draws a chevron from College to University**
    (`educationOverview()`), a sequence the model does not have: both take
    diploma-holders (`EducationType.requires()`). The hint under the heading
    now says "go on to college or to university"; dropping the chevron is a
    code change.
  - **Death care's "Set the fee ›"** opens Policy › Promises › Health, whose
    dial does not move the funeral fees (that page prints them as "Funeral
    fees, unscaled"). The (i) beside the door now says so; whether the door
    belongs on that line is a design call.
  - **`Money.power()` can print four figures at a boundary:** 999.6 kW rounds
    to "1,000 kW", and 99.96 MW reads "100.0 MW"; its javadoc says three
    figures at most. Display only.
  - **A course's "seats free" is struck after the month's enrolment** (the
    staffed seats less those in flight, this month's intake among them), so
    the funnel's "enrolling" step is not the month's own intake; the old page
    had the same. A model read for the month's intake would make it so.
  - **Medical school "held by students" while NEEDS YOU lists it red** (the
    fixture city: 408 seats, 1,103 would come): this month the enrolment rate
    binds, in the long run the seats would. Both are the model's; the chip
    stays grey, as the spec says.
  - **Month figures not saved:** the licences, the graduates and Healthcare's
    served; a loaded city's Services pages say "not recorded yet" or "after a
    month" until a month runs, instead of 0.
  - Closed by 0.7.29: the road's getters mixing the raw and the effective load
    (`runs/ui9-notes.md` §8.5; 0.7.29's B3).
- **INFRASTRUCTURE: WORDING AND ODD CASES** (found 2026-10-01 by 0.7.29's docs
  pass and implementer: `runs/ui10-docs-pass.md` flags 1–3 and 10;
  `runs/ui10-notes.md` §8.4; `the-road-in-one-picture.md` §7), none changed:
  - **The flow is worded as every business's output, and with transit it is
    not.** The Roads hero's "every shop, plant and site works at 56% of its
    output", FLOW's "congested: output at 56%", the congested words and
    Services' road card say so, but a sector's commuters who ride are not in
    the jam (`throughputFor(mix)`): in his city, 41% riding at 83% flow, a
    commuter-heavy shop works at about 90%. Construction and freight work at
    the plain flow. The (i) now says so; the docs pass suggests for the hero
    "every site works at 56% of its output; a shop or plant at that or better
    as more of the city rides transit".
  - **Freight's "BULK · rail-eligible"** implies goods are not; across the
    boundary both are, and `Rail.haul()` takes goods once bulk is served.
    "BULK · railed first" would say what the model does.
  - **Services' ROADS figure is still the flow alone** with its status word
    ("56% congested"; 0.7.29's decision 5, the brief having limited
    ServicesScreen to the card). The pair there, if wanted.
  - **`Money.money(NaN)` prints "$0"** (`Math.round(NaN)` is 0).
    Infrastructure guards with its own `moneyOr()`; any other screen that
    shows a figure which can be NaN (the railway's allowed bill, what went
    abroad or was kept, after an older save) would print "$0".
- **SECTORS: WORDING AND ODD CASES** (found 2026-10-01 by 0.7.30's docs pass
  and implementer: `runs/ui11-docs-pass.md` flags 1–3 and 5;
  `runs/ui11-notes.md` §8; `the-sectors-as-flows.md` §7), none changed:
  - **Three leverage verdicts for one figure.** OWES, in the frame, is amber
    over 0.6; the credit grid's "Leverage" line amber over 0.5 and red over
    0.7 (the old page's literals); the LEVERAGE bar green to the bank's watch
    line (0.9), amber past it and red at the default point (1.5). A sector at
    0.8 reads amber in OWES, red in the grid and green on the bar. The model's
    lines are the bar's.
  - **Two "bank loans" on Cash & debt:** the debt mix's is
    `getTermLoanPrincipal()` (no mortgages, no interim); the grid's "Bank
    loans" line is `getLoanPrincipal()` (both in it), and "...its debt in
    bonds" divides by it. On the landlords' page they differ by the mortgages.
    A label or the figure, Jerus's choice.
  - **CAN IT BORROW THE REST reads the ban alone:** past the default point the
    bank lends nothing and the tile still says yes (the docs pass changed only
    the line under it, now "no ban: at its own rate, within the bank's
    limits"). A "no" there is a screen change.
  - **The Bank's Capital & owners page has two headings in a row,** "Its
    owners" and the card's own "ITS OWNERS". For the PC look.
  - **A sector with nothing standing is quoted 26–28%** (its risk 24.60 points
    at no leverage); OWES says "owes nothing", and the rate bar still shows
    the quote.
  - **Fractions of a van a month** (Retail's vans, the railway's rolling
    stock) on the production rows: the flow says "under 1 van".
- **GOVERNMENT: WORDING AND ODD CASES** (found 2026-10-01 by 0.7.31's docs pass
  and implementer: `runs/ui12-docs-pass.md` flags 4–8; `runs/ui12-notes.md` §8;
  `earned-surplus-banked.md` §7), none changed:
  - ~~**Finances still judges debt at 60%/120% in two places D7 did not name:**
    the landing's "The position" row (red over 120% of GDP, FinancesScreen
    ~184) and The position page's sentence, coloured ("comfortable" /
    "carryable" / "more than a year of output", ~655). OWED's cell, which D7
    named, is done. Finances is 0.7.32.~~ — **done in 0.7.32** (the Finances
    spec's D7: the thresholds gone everywhere on the tab).
  - **Half a thousand is "nothing" on the rings and lists** (`drawn()`,
    `listPage()`): a $400 line draws no arc and is listed in "nothing this
    month", though `money()` would print "$400"; `signedTight()`'s "$0" is
    under half a thousand, `money()`'s under half a dollar. Left with a
    `TODO(docs)` in `drawn()`'s javadoc (0.7.31's decision 10, CONFIRM in
    section 0). The 600-month city's mortgage claims, a fraction of a cent, are
    nothing either way.
  - **THIS MONTH, AS ONE BAR draws each part at its size,** so a negative
    investment or net exports is a grey length that adds; since B4 its colour
    no longer says it subtracts, only the key's and the tooltip's sign. For the
    PC look (the 2,400-month city's investment is −$129k).
  - **The header's line is seven characters longer** ("earned"): at 1,389 px
    check that the tiles to its right lose nothing (the CHECK item, section 0).
  - **THE DEBT's coupon chip after a load** (the spec's B17): for the first
    month after a load the card can show "coupon … a month, booked …: not
    struck yet", since the interest is struck from what was paid over a
    completed month. A load artefact; B1's scaled year, its twin, is gone.
  - Healthcare fees' panel after a load and the busy month annualising are
    under MODEL BUGS FOUND BY THE SCREEN RESEARCH (section 0, items 13 and 14).
- **FINANCES: WORDING AND ODD CASES** (found 2026-10-01 by 0.7.32's docs pass
  and implementer: `runs/ui13-docs-pass.md` flags 1–7; `runs/ui13-notes.md` §8;
  `the-debt-at-a-glance.md`), none changed:
  - **The default page's cost is out by half.** WHAT IT COSTS reads "No lender
    abroad will take this city's paper for five years, / and 10 points on its
    rate when they will again, / fading over about five years after that." The
    scar fades from the month of the default (`SCAR_DECAY` .9885, a half-life
    of about five years) and the window reopens at 60 months, when half of it
    is left: about 5 points, not 10. The alert band and WHAT EACH MEASURE HAS
    USED say it "fades over about five years", which reads as gone where it is
    half. The model is right and the words are not: a sentence for Jerus to
    choose.
  - **Money's two charts draw ten years** (`ChartModel.DEFAULT_RANGE`); the
    docs pass took ", A YEAR" off their titles. If a year was meant, the change
    is the charts' window.
  - **WHO HOLDS THEM on the bond market carries the prices' (i)**
    (`PRICES_INFO`), which says nothing of who holds them: a holders (i), or
    none.
  - **Government's "Bought back a bond" door** asks for the "Buy back" page,
    which went in 0.7.32, and lands on The book by the fallback. It works; the
    page name in GovernmentScreen is stale ("Every piece").
  - **NEXT DUE shows a serial bond's whole principal at its last date**
    ("D$570.0M SERIAL Jan 2205"), though most of it falls due in yearly slices
    before then; the ladder puts each slice in its year. As the 0.7.24 card
    did.
  - **`TERMS_INFO`** begins "Each column is the rate ..." and is the (i) on
    Abroad too, where the terms are chips.
  - ~~**Policy's floor sentence** calling the dial the floor (B13's other
    half).~~ — done in 0.7.36 (its B16).
  - ~~**OWED AND THE RATE draws no flags** (a small chart has no flag lane:
    found by 0.7.33's docs pass, confirmed by 0.7.35's implementer), and a
    founding-month borrowing decision is off its lane~~ — **both done in
    0.7.38** (`the-loose-ends.md`): a small chart handed decisions draws its
    lane, the founding month's on its first month; in a city over ten years old
    the founding month is out of the chart's fixed ten-year view (THE BANK,
    below). ~~**WHAT IS BEHIND IT's "0.0 months"** of cover~~ — **done in
    0.7.38**: "under 0.1 months", through `Money.coverMonths()`.
  - **The model's, shown as it is** (the implementer's §8):
    `Game.quoteRepurchase()` writes (it reprices the debt market and the
    standing rate; the screen reads `marketValue()` instead, and
    `repurchaseGain()` calls it); the 2,400-month city's bank has D$7.7M of
    room against D$53B of deposits, so WHO BUYS IT's ghost for any real issue
    dwarfs it; a foreign quote's `rateBefore` is the world's rate before the
    issue, not the city's; the rollover's record has no central bank part; a
    piece issued between presses settles to its buyers at the next press, so
    the holders bars read the bank before the issue for that month.
- **THE BANK: WORDING AND ODD CASES** (found 2026-10-01 by 0.7.33's docs pass
  and implementer: `runs/ui14-docs-pass.md` flags 1–9; `runs/ui14-notes.md` §8;
  `the-bank-at-a-glance.md`), none changed:
  - **ITS RATES cannot be dragged** (its flags done in 0.7.38).
    `BankScreen.chart()` builds a TimeChart that is not the main one, and a
    small chart registers no pan or zoom, so the six small charts follow a
    window nothing moves. ~~It drew no flag lane either, so the
    `flagsOf(CENTRAL_BANK, BANK)` it is handed were never drawn~~ — **done in
    0.7.38**: a small chart handed flags draws its lane, the founding month's
    bank decisions on its first month. **The drag is still not built**
    (0.7.38's ★2, CONFIRM): the (i) no longer promises it and names "Over the
    years" (City History) as the way back; building it is a pan on a small
    chart (`listen()`'s press, drag and release, without the wheel), new
    behaviour, Jerus's. Until then the founding month's bank and borrowing
    decisions are out of view on the Bank's and Finances' charts in any city
    over ten years old, and `BankScreen`'s `historyWindow` javadoc, the
    HISTORY banner, GameVersion's 0.7.33 entry, `ChartModel.flagsOf()`'s
    javadoc and BankCheck §13b still speak of a chart dragged back
    (`runs/ui19-notes.md` §8.1, §8.7).
  - ~~**A shut-out sector's name never reads red on the ladder**
    (`Pieces.rateLadder()` inks every door's name in the accent; the red "shut
    N mo" tag does show)~~ — **done in 0.7.38**: the name takes the rung's own
    tone first, red, keeping its "›" and its click.
  - ~~**The Sectors screen's Cash & debt rate bar** adds `getRiskSpread()` as it
    stands now to prime, record and concentration, where the rate was struck on
    `quoteParts()` (0.10–0.25 points apart after a month of statements)~~ —
    **done in 0.7.38**: the bar, its caption and its (i) read `quoteParts()`.
    **Left: the two sector (i)s read a risk struck earlier beside the quarter
    as it is now.** The Bank's sector (i) and, since 0.7.38, Sectors' print the
    quote's risk part beside the quarter's default rate and leverage read now
    (`runs/ui19-notes.md` §4 decision 9, §8.2). `QuoteParts` keeps the parts
    but not the default rate and leverage they were struck from; making it
    exact is a model read (keep both in `QuoteParts`), not a display fix.
  - **B16 inside the folds and on History's money charts:**
    `Money.tightMoney()` hyphenates a negative ("-$26.86"); a true minus there
    touches every screen's money, a small batch of its own (Government's B15 is
    the same).
  - **Small words, for the Bank's next pass:** the empty ladder's caption
    leaves out the record and the concentration; INTEREST MARGIN's note "net,
    on what it lent, the last 12 months" lacks "over"; a bond rung's chip says
    "holds them" whether or not the bank holds any ("would hold them" would be
    exact); NEEDS YOU's THE BANK row lands wherever the tab was left, not reset
    as Finances' rows are since 0.7.32; `stanceInfo()` names the risk-weighted
    target and minimum while the leverage ratio binds.
  - **The model's, shown as it is** (the implementer's §8): the 2,400-month
    city's bank lends 1% of what it has gathered ($664.7M of $52.8B), so BESIDE
    THE SHEET reads about 73× the sheet; its risk-weighted ratio is 1,990.7%,
    pinned at the end of the gauge's scale; `bankCapitalRatio` clamped at 10
    (MODEL BUGS item 15); CREDIT LOSSES 0.006%, non-zero only at three places.
- **THE BUTTONS: WORDING AND ODD CASES** (found 2026-10-01 by 0.7.34's docs
  pass and implementer: `runs/ui15-docs-pass.md` flags 1–4;
  `runs/ui15-notes.md` §8; `buttons-that-ask-to-be-pressed.md`), none changed:
  - **A HELD button's line promises a door its press does not open.** "more
    ground: the Land office ›", "ore comes with land: the Land office ›" and "a
    school licenses them: Education ›" each end in the "›" every door carries,
    but the press opens NOT ENOUGH LAND, NO IRON DEPOSIT or NOBODY QUALIFIED
    first, whose pill is the door (★3). Either the press goes straight there
    (one line in `orderControls()`) or the line drops the "›". Jerus's call;
    not in 0.7.38.
  - ~~**`adviceTotal()` still adds the suggestions' prices up in the screen**
    for "all three ≈ $X" and "Build all three"'s over-the-cash look, where the
    order bar reads `BuildAdvice.quoteTotal()`~~ — **done in 0.7.38**: it calls
    `BuildAdvice.quoteTotal(List<Suggestion>)`, the old sum to the bit.
  - **"Buy the next 1":** `next()` writes "Buy the next " + n, and N runs from
    one.
  - **Build's credit page is still the old statement layout** with the new
    button at its foot; `Pieces.offerCard()`'s comment still says the page is
    to take the card in Build's own pass (0.7.26's ★13).
  - **Neither piece takes keyboard focus:** the old Build could be pressed with
    Space; Enter and Backspace on Build pages are unchanged.
- **TRADE: WORDING AND ODD CASES** (found 2026-10-02 by 0.7.35's docs pass and
  implementer: `runs/ui16-docs-pass.md` flags 1–7; `runs/ui16-notes.md` §8;
  `trade-at-a-glance.md`), none changed:
  - ~~**"One parity rule" has a fourth reader not on it:** the drawer's
    Dashboard TRADE section turns "vs parity" amber past 15% either side, so at
    20% stronger THE CURRENCY row and the header are grey and that line
    amber~~ — **done in 0.7.38**: amber at `PARITY_WATCH`, red at `PARITY_FAR`,
    plain when pinned. ~~Its import cover line under a literal 3~~ — **done in
    0.7.38** too, on Trade's `coverLevel()` (CONFIRM, its ★4). **Left:** its
    banner's "Red when the currency is weakening" (the head goes amber on a
    current-account deficit; "vs parity" is now red past `PARITY_FAR` either
    side), a comment; and "vs parity" prints a hyphen-minus (`%+.1f%%`,
    "-26.2%") where the redrawn screens print a true minus, its format kept in
    0.7.38 (`runs/ui19-notes.md` §8.3, §8.4).
  - **A city just founded reads as just loaded on Trade** (`isMonthCounted()`
    is false after a founding as after a load): What we trade's "THE MONTH THE
    CITY WAS SAVED IN" and its line, the ledger's and the vault's "since the
    load" and the freight line's "Just loaded". Brief, since a month runs in
    seconds; neutral words, or a founded-or-loaded read in the model. With the
    D25 words (CONFIRM, 0.7.35).
  - ~~**"0.0 months" of cover** on Finances' WHAT IS BEHIND IT and in the
    drawer's TRADE section~~ — **done in 0.7.38**, on Trade's "under 0.1
    months" through one formatter, `Money.coverMonths()`.
  - ~~**Two (i)s promise flags their charts cannot draw** (the Bank's ITS
    RATES, Finances' OWED AND THE RATE); and a founding-month currency decision
    is off Trade's own rate chart's lane~~ — **both done in 0.7.38**.
  - **The year book's preamble** names fxRate as the exception to "rates are
    fractions" and not `fxParity`, now a rate column in the same units; its own
    column note gives the unit, and YearBookCheck asserts the fxRate wording.
  - **Loosely true, left:** `NOT_SAVED_INFO` lists the treasury's purchases
    among flows struck at the month's end (they are booked when made);
    `HOLDINGS_INFO`'s "at their price, the last trade" (the fair value before a
    first trade); `forceReadings()`' "Openness of the economy 100%" under
    details, without the chip's clamp words; "tap one" on a PC;
    `COMFORTABLE_COVER`'s dial sentence, "fully absorb" (they absorb
    `MAX_ABSORPTION`, .85).
  - **From the implementer's §8:** B14, freight after a load, shown, not fixed
    (MODEL BUGS item 1); the households' car imports are on no sector's books
    and not saved, so a loaded month's What we trade lacks them (its (i) says
    so); Finances still opens Trade through `tradeArea` and the old page arrays
    (`open(TradeScreen.CURRENCY)` and `open(TradeScreen.RESERVES)` the one-line
    replacements); `Pieces.bandMeter()` has no caller (not on 0.7.38's list;
    section 6); Government's "Bought land with … reserves" step still opens the
    last Trade page.
- **POLICY: WORDING AND ODD CASES** (found 2026-10-02 by 0.7.36's docs pass and
  implementer: `runs/ui17-docs-pass.md` flags 1–8; `runs/ui17-notes.md` §8;
  `policy-at-a-glance.md`), none changed:
  - ~~**The floor in founding money** on the People screen ("Minimum wage %s a
    month; every wage in the city is a multiple of it.", PeopleScreen ~2159:
    $3,460 under jobs paid from $3,827 in the research city; B7's other
    half)~~ — **done in 0.7.38**, in today's money (`cashMinimumWage()`).
    ~~City History's "Minimum wage" line and the year book's `minimumWage`
    column, which record the founding figure~~ — **done in 0.7.38**: "Minimum
    wage, founding money" and the column's note, the series unchanged; a
    today's-money line on History is not offered (CONFIRM, 0.7.38's ★6: it
    needs a saved series).
  - **"Pensions to N% of a wage"**, the pension's decision line, is now on the
    hub's RECENT DECISIONS as well as History's flags, where the pension is a
    share of the founding unskilled wage (B10); ChartCheck asserts the words,
    so left. SocialSecurity's comment is of the same family.
  - ~~**A method name in the player's text:** WHAT SENIORS RECEIVE's (i) ends
    "(TaxPolicy.pensionPerSenior())"~~ — **done in 0.7.38**: "a design
    question the model has filed and not yet answered."
  - **`TAKE_INFO`'s "three of the four a move off it by sector or by band"**:
    property has a move off it by sector too. Older than 0.7.36.
  - **Loosely true, left:** `LEAD_INFO`'s "nothing reaches the model until
    Apply" (the target, the holdings, the ceiling, the hand and the subsidies
    apply at once); `TRAY_INFO` lists the tax bases on Schools too; RECENT
    DECISIONS' empty line on a save from before the log; the payslip's door
    opening Pensions only; `rateInfo()`'s "up to N points" an upper bound;
    "Your per-sector changes stay" (band offsets stay too).
  - ~~**Members left without a caller:** `PolicyScreen.promisesCost()`,
    `Levers.leverHead()`, `arrow()` and `previewCaveat()`~~ — **done in
    0.7.38**, with the four the fare's dial card then left without one
    (`Levers.wouldHead()`, `wouldBe()`, `wouldTotal()`,
    `PolicyScreen.stagedLadder()`). `LabourMarket.floorForCash()` is read only
    by the harnesses.
  - **Not in 0.7.36, from its notes:** the vitals do not count up on a month
    (People's `countUp` is PeopleScreen's own); ~~the fare onto
    `Levers.dialCard` (D18 step 6)~~ — **done in 0.7.38**, its four rows the
    model's. The property offset's ±10 against ±30 and the two inflation
    colours are MODEL BUGS items 17 and 16.
  - **The model's, shown as it is** (the implementer's §8): the policy rate
    barely reaches demand (at a 5% dial savers would get 0.30%, and spending
    moves 100.2% → 99.9%); a quarter point does not move what the city borrows
    at (the floor is the bank's cost of funds, 1.01% against a 0.13% dial);
    EI's bill at the month's top ($6.6M) is not the pool's restrike ($6.4M),
    and the preview uses the pool's; after Apply on the pension, until the
    month turns, "A pensioner household has" reads against the month the
    household had.
- **CITY HISTORY: WORDING AND ODD CASES** (found 2026-10-02 by 0.7.37's
  implementer and docs pass: `runs/ui18-notes.md` §8; `runs/ui18-docs-pass.md`
  flags 1–11; `city-history-finished.md`), none changed but the one 0.7.38
  closed:
  - The epidemic of 2,213 months (MODEL BUGS item 18); the bank's capital ratio
    "1000.0%" and its return on equity at −105.2% (MODEL BUGS item 15).
  - **Rolling stock sits at exactly half its import price** in the 600-month
    city ($897,693.61 of $1,795,387.22): an import-only good nobody makes or
    takes strikes the band's middle (`GoodsMarket.strike()`); shown as "under
    the world's ceiling of …".
  - **History's pins draw no flags** (the docs pass's flag 8): since 0.7.38 a
    small chart draws the decisions it is handed, but the pins are handed none
    (`runs/ui19-notes.md` §8.5); handing them History's flags was not asked.
  - **"0.0 pts" beside "from 1.2%" and "1.1%":** a 0.04-point move, two
    one-place figures rounding apart (`changeText()`'s rounding, unchanged).
  - ~~**B4's fix is History's alone:** Trade's rate chart, Finances' and the
    Bank's draw their own flags without `ChartModel.onAxis()`, so a
    founding-month currency, borrowing or bank decision is off their lanes
    (the docs pass's flag 6)~~ — **done in 0.7.38**, one `onAxis()` each.
  - **The chart's crosshair card keeps its hyphen-minus:** `fmtUnit()` is
    unchanged, because the Bank, Finances, Government and Policy hand it to
    their charts' cards.
  - **Words, Jerus's** (the docs pass's flags 2, 3 and 7): **"N drawn" counts
    picked lines, hidden ones included** (PICK WHAT TO DRAW's head is
    `historyPicked.size()`; a line switched off in the legend is still picked;
    "N picked" would be exact, as the groups say "N of M picked"; the spec
    chose "drawn"); **the Market preset's tooltip** "what a founding share of
    each company is worth" draws the share prices, and since 0.7.37 the picker
    also offers each company's "fair value" (not false, but the two read
    alike); **"+N more" under the decisions in view** opens the by-kind
    details, ticks over the whole history, not a longer list of the rest in
    view (notes decision 18).
  - **"no ceiling" on an export-only good is the world's** (flag 4): the city's
    market caps it at twice the floor (`NO_CEILING_MULTIPLE`), which the "Open
    at one end" caption's (i) has said since the docs pass.
- **THE LOOSE ENDS: ODD CASES** (found 2026-10-02 by 0.7.38's implementer:
  `runs/ui19-notes.md` §4 and §8; `the-loose-ends.md`; its docs pass running,
  `runs/ui19-docs-pass.md` not yet written), none changed; its other finds
  are under THE BANK, TRADE and CITY HISTORY above and in section 6:
  - **A flag's label on a small chart's lane is cut with "…"** to the room
    before the next flag, as on the big chart (decision 3: canvas text cannot
    wrap; hovering shows it whole). There is no click to pin: a small chart
    takes no press.
  - **A small chart handed decisions of its kinds draws its lane even with
    none in view** (decision 1), so its height never jumps: the Bank's chart in
    the 600-month city shows an empty "you" lane, all its decisions older than
    ten years.
  - **Right after a fare Apply, until the month turns,** the fare card shows a
    move at the new fare (riders 41,491 → 32,756; CONFIRM, 0.7.38's ★11), and
    Transit's funnel row "the fare: X% still ride" keeps the old fare's share
    likewise: `getTransitRiders()` carries the fare share struck at the old
    fare until the month turns (§8.8).
- **THE CITY'S FUND: ODD CASES** (found 2026-10-02 by 0.7.39's implementer
  and docs pass: `runs/ui20-notes.md` §4 and §8; `runs/ui20-docs-pass.md`
  flags 1, 2, 4 and 10; `the-fund-as-a-brokerage.md`), none changed but the
  two its fixes closed (`runs/ui20b-notes.md`); its model finds are MODEL
  BUGS items 19–22, its comment and harness flags in section 6:
  - ~~**`Money`'s "$1000k":** `Money.tightMoney()` writes 999,500–999,999 as
    "$1000k" (and 999.95M as "$1000.0M"), so every screen that lands there
    shows it. The fund's words go through `Pieces.tidyMoney()` ("$1.0M"); the
    one-line fix in `Money` would change every screen at that edge, so it is
    left for Jerus, with THE BANK's B16 (above), the same formatter's hyphen.~~
    — **done in 0.7.40**: `tightMoney()` moves each unit up from where the one
    below would print a thousand, for every screen (999.97B reads "$1.0T",
    999,600 compact "$1.0M"); `Pieces.tidyMoney()` is now a no-op, left in
    place.
  - **The fund's worth is not on City History's picker:** its three series
    (`fundValue`, `fundPutIn`, `fundTakenOut`) are in the history and the year
    book, not in HistoryScreen's list of lines. One line; not 0.7.39's batch.
  - **A hand buy at fair rests and lapses** in a market priced over fair
    (MODEL BUGS item 21): "Your order: bought 0 of 39.47 shares of
    Construction - 39.47 lapsed, D$4.0M back to the fund". "Best bid" is the
    price that fills.
  - ~~**★B1's cap was checked against what the fund held when the hand
    posted, not against the rule's resting bid or another hand order on the
    same book** (the docs pass's flag 1), so both could fill past 10% and the
    rule then ask the excess back at fair value, the loss B1 was built to
    stop (12.9–18.1% of a company on the old model; FundLedgerCheck §8's own
    fixture was one)~~ — **done after the docs pass**, in 0.7.39:
    `Exchange.fundRoom()` counts the holding and the hand's buys on the book
    and waiting as if filled, read by the step and the ticket; the rule's bid
    makes way for the hand's (★ the hand first, CONFIRM); the ticket says "No
    room under the 10% cap" when there is none, and THE RULE names only a
    company's buyback as the way past it.
  - ~~**Search said "1Y: not recorded precisely enough" in any city younger
    than thirteen months**, for every company (the docs pass's flag 2:
    `FundView.move()` is NaN for a short history as for B6's zero prices)~~ —
    **done after the docs pass**: "1Y: its record is shorter than a year"
    when `FundView.recordShort()` holds; B6's words kept for a price recorded
    as zero (MODEL BUGS item 20).
  - **A reopened lot's income runs on while its "since" restarts** (flag 4):
    `FundLedger.reopen()` moves `since` to the month a sold-out lot is bought
    again and leaves `income` and `realized` as they were, so YOUR POSITION
    prints "Dividends D$X / since <the reopening month>" with X including the
    earlier holding's, while `Lot`'s and `FundView.Position`'s javadocs say
    "the income it paid since `since`". Which is meant is Jerus's. Akin, and
    untested: a city with two resolutions whose rescue book sold out between
    them, where `seed()` dates the rescue lot from the first and a tracked
    lot would restart (`runs/ui20b-notes.md` §9).
  - **The fund area's (i) on Finances' hub** (flag 10; `AREA_INFO[5]`: "Its
    shares and bonds, the bank's rescue, the dial, and the share of its worth
    it pays the treasury every month") says nothing of cost, P&L, search or
    orders; true as far as it goes. Words, Jerus's.

---

## 4. Engine truths worth fixing

- **CONCENTRATION IS WHAT FAILS THE BANK NOW — JERUS'S** (`a-sector-is-many-firms.md`
  §5, §7). Every remaining failure on 0.7.8 — collapse, stage-2 set-aside or
  slice — has one sector holding 30–73% of the book, 5–7 times the bank's
  equity. The levers left: a limit on a sector's share of the book (a bank's
  industry limit) or Pillar 2's concentration add-on; rescue for shares would
  make a failure cost the city less. Syndication was the structural answer
  tried, and removed ("Not doing").
  *0.7.10 adds a cause to it. Under autopilot, failures rise from 49 to 76,
  and all of the rise comes from the smaller vault. With D$100M and the old
  US$1B vault the counts are unchanged; with D$2.5B and the new US$25M vault
  they rise. Without dollars to steady the currency, the autopilot's
  rule-held rate sees more imported inflation in the city's middle years.
  The default setup is unchanged (39 against 42).
  (`founding-a-city.md` §4.)*
- **`ASSET_VOLATILITY` 0.25 — the curve's one number, Jerus's to settle**
  (`BusinessDebtManager`; the Merton/KMV literature 20–35%; 0.20 steeper,
  0.35 flatter). Not tuned to any result.
- **`MAX_BUFFER` BINDS IN EVERY SEED**: the worst year is 38–109% of the
  weighted book, so the target is 16.5% throughout and covers a sixth of it.
- **THE QUARTER PRICES THE SHORTFALL DESK PAST THE DEFAULT POINT — Jerus's
  call** (the quarter was his): 1,436 loans over the default eight, 1,435 of
  them the shortfall desk, which lends on the month's leverage while the price
  reads the quarter — 73% Luxury Retail on its restock's high month, 20% a
  sector a large slice had just cut the debt of, 11 past the point on the
  month's own reading too. Pricing the shortfall desk on the month, or on the
  lower of the two, would stop it. `BusinessDebtManager`'s quarter banner has
  the breakdown.
- **A BANK FAR OVER ITS TARGET CAN STILL BE BROKEN BY ITS DESK'S RE-MARK.**
  The capital rule holds purchases, not positions: the charge on a share is 15.75%
  (target × `RISK_EQUITY`) and the quote moves between half and five times
  fair value, and the inventory grows with the mark and never has to be sold
  down. 15 failures held at 10%, none in the other runs.
- *(small, from 0.7.8's rounds)* the desk's own buying lowers the month's
  closing quote of what it holds (`PRESSURE` × held ÷ limit, ~6% on
  `BankCheck`'s fixture), so a desk bought to its limit can end the month a
  hair under target; Luxury Retail pays dividends while losing money and
  borrowing ($32M in a month with −$206M before tax); a bank recapitalised to
  exactly `paidInPerBranch` sits on `excessCapital() == 0`, where ULPs flip
  PAYING and RETURNING — harmless in money, and where round 3's probe and the
  shipped tree parted; `ui/SectorScreen`'s leverage colours use their own
  0.5/0.6/0.7 (~L445, ~L1250), not the model's 0.9 and 1.5; the branch
  advisor's income (`BusinessInvestment.estimatedMonthlyProfit()` ~L797)
  prices a branch's book at the planning sector's quote, not at prime;
  `Game.consider()` still files `canFundProject()`'s leverage refusal as a
  land shortage (`landBlockedSectors`) — only the capital refusal is split out
  (see the NEVER CLEARS item).
- **FOUND BY 0.7.19, NOT ITS TO FIX** (`the-price-keeps-up.md` §6, §8):
  - **The held-25% city collapses** (8,168 → 428): 0.7.18's lived on the
    deposit cap's strain opening branches with outside capital and on the
    window ($181.9 trillion at a median seed). Keeping it alive needs a way in
    for money from outside that the model does not have. **Jerus,
    2026-09-30: "Let it fail (Recommended)"** — it stays a stress case whose
    findings are watched; it is not required to recover.
  - **The Bank tab's and the planner's break-even figures** leave out the
    charter's exemption, which the rule counts.
  - **Rebated rents against taxed repairs:** an oversupplied city's landlords
    sell empty buildings (53 of 120 Low-Rise in EducationCheck's free-tuition
    city). Jerus: "Leave as the law has it".
  - **A House mostly bears its tax** (about $454k at founding, past the NRRP's
    $450k).
  - **A default city runs on its charter branch all its life:** the staffing
    test held the advisor 1,562 months, at ~79,500 customers a branch.
  - *(small)* `Bank.lastKept` saved and read by nothing;
    `BuildingManager.structureCost()` never called; `Game.grantAmountAs(FIXED)`
    returns 0 with no one studying; BankCheck s.4 and FundCheck s.6 may no
    longer need their first-pass re-causes; the repairs dial's "about $34 a
    home a month" predates the batch.
- **FOUND BY 0.7.18, NOT ITS TO FIX** (`workers-take-the-best-paid-job.md` §6, §8):
  - **Food Processing is held on staffing all 120 months of Jerus's city**,
    against its own banner's old measurement that an 80% floor is wrong for
    four-post plants (`TODO(docs)` in `FoodProcessing.java`). Jerus's call.
  - **The first year of a short city costs more** (his: −$9.87B against
    −$5.10B): the fill moves at once, the wage walks at `ADJUST_RATE`.
  - **A young town that waits for staff builds late, then grows fast.**
  - **Default cities are short of college workers all their life** (median
    fill 0.57).
  - **The 7.5% unskilled share is Ontario's**; a Canada-wide figure would
    replace it.
  - *(small)* LabourCheck's new section sits inside "who moves in"; the
    People screen's "What it would take" omits labourers; "Anyone can do any
    job" in the fakes list; Automotive's and Manufacturing's "largest hiring"
    claims.
- **FOUND BY 0.7.17, NOT ITS TO FIX** (`the-crew-a-building-can-use.md` §6, §8):
  - **A founding that orders its depots beside a coal plant stalls for
    years** (the first thing to try on the PC, above).
  - **Industry waits behind a long queue:** in Jerus's city the lead-time gate
    refused Industry 94 and Materials 99 sector-months behind his 47 care
    complexes.
  - **Unemployment moves about twice as much a month** (p95 0.81 → 1.93
    points, default), from the builders hiring and laying off.
  - **Held 25%:** smaller (4,452 → 3,018, p 0.08), households with no home in
    every seed; two collapsed seeds show save/reload differences at the 6th–8th
    significant figure and the households' paper books apart by 1e-6.
    Untraced.
  - DenominationCheck's twin drifts 1e-6 to 1e-5 a decade on with the layoffs
    alone (not the shipped configuration): floating-point noise in one cell's
    share sales.
  - *(small)* three members nothing calls (`siteShareOfSector()`,
    `getBankedClearedAtLoad()`, `getStandingCostPerCapacity()`);
    `IDLE_PAYROLL_FLOOR` is a share of posts now, not of payroll;
    `getConstructionOutputAtEveryPost()` repeats the output arithmetic; the
    builders' profit estimate still counts no wages (round 3).
- **FOUND BY 0.7.15, NOT ITS TO FIX** (`the-central-bank-as-backstop.md` §6;
  the implementer's record, `dials-notes.md`):
  - **The dial at 100% doesn't limit the city's paper at home.** It makes
    the central bank the buyer when nobody else is. Held at 25%, the city
    ends at 1,047 people. The limit is still 0.7.13's item.
  - **A 0.7.15 save with the dial past 50%, opened in 0.7.14, loads at
    50%** (Jerus left it).
  - **The central bank can end a roll month over its dial** when a surplus
    pays only part of its par, and the holdings step then sells the excess
    to the bank (Jerus: "Leave it (as built)").
  - **One month of negative GDP** in Lean at dial 50% and 100%, of 0.7.14's
    untraced kind.
- **FOUND BY 0.7.14, NOT ITS TO FIX** (`the-city-takes-the-shares.md` §8; the
  implementer's record, `treasury-notes.md`):
  - **The city keeps its bank for decades** (Jerus: "New issues only"): the
    median stake is 58.8% twenty-five years after a default resolution,
    91–100% in the autopilot and held setups. A sell-down (NatWest's
    placements, trading plan and directed buybacks, 2015–2025) is his if he
    wants one; the player can sell the rescue book by hand.
  - **The preferred's funding page is circular capital** (Jerus: "Keep as
    built"): held at 25%, the city borrows from the bank to buy the bank's
    preferred, and each offer is bigger ($962T bought, the advances peaking at
    $20.4 quadrillion). The KNOWN HOLE javadoc stays open for it. The limit on
    the city's own paper at home (0.7.13's item) would close it.
  - **Insane spirals in 5 of 8 seeds** on the same missing limit. The
    playtest's player borrows exactly the village's invoice, so every Insane
    seed runs on the central bank's advances from month 3.
  - **The fund never pays for a rescue** (Jerus: "Keep as built"): at dial
    300% all 66 months on advances were resolutions', with the fund holding
    about $500B.
  - **A second reload gap, 0.7.13's:** a bond's households' face is kept on
    the bond and in the cells, and the two drift by parts per billion (17 of
    Construction's bonds, Insane seed 0); the load resets the bond to the
    cells past 1e-9, so a reloaded city differs by the drift. Find the
    operation that parts them, or have the load keep the saved figure.
  - **One month of negative GDP** in held 10% seeds 0 and 2 and dial 300% seed
    1: that month's raw-material imports outweigh everything else. Untraced.
  - **MoneyAudit's "+ bank ResolutionLoss" reads nothing** in this build, an
    older save included (`Bank.startMonth()` clears the restored month figure
    before the next strike). Whether the line goes is Jerus's.
  - **The fund can pass 10% of a company without buying**, when a company's
    buybacks shrink the shares under it and nobody bids at fair value for the
    excess: 1.5% at most in the default runs, 33% at dial 100%.
  - **Unemployment at the end follows the playtest's police purchases**: in
    every build, seeds ending with coverage of 40% or more sit 5–19 points
    higher. Crime stops pushing people out and the city outgrows its jobs.
  - *(small)* TARP's $25B cap is left out (no source converts it); the hand
    cannot sell the preferred or the warrants; the time skip still halts at
    cash ≤ 0 while the play clock now refuses only with no revenue behind it
    (0.7.0's item below); `Game.recapitaliseBank()` and its partial rescue are
    gone (0.7.9's item).
- **FOUND BY 0.7.13, NOT ITS TO FIX** (`rolling-what-falls-due.md` §6; the
  implementer's record):
  - **Nothing limits how much of its own paper a city can sell at home.**
    - The price stops at policy + 10 points plus the term premium.
    - The commercial bank buys whatever the households leave, with no
      capacity check, funded at the central bank's window (no cap).
    - Debt service is a promise paid past the advances ceiling.
    - Only abroad does a window shut.
    - With the rollover sized to the cash, Lean seed 0 compounds to $476.6T
      of paper and 2,423 people. A market limit — a buyer's refusal or a
      debt-service rule at home — is Jerus's to design.
  - **The playtest trips no finding for a runaway treasury** ($476T owed,
    43.9% unemployment).
  - **Deposits on the bank's books.** Today they are the holders' money and
    the bank only counts them for capacity. Putting them on its sheet moves:
    - `Bank`'s three statements (`totalLiabilities()`, `cashReserves()`,
      `depositFunding()`);
    - `Game.refreshBank()`, which would move money rather than read it;
    - MoneyAudit's pools, which would count the money once, in reserves;
    - every household–business payment, which would become a transfer
      between two deposits;
    - `CentralBank`'s interest on reserves and the window's size;
    - the Balance sheet page's memo line.

    The harnesses to re-derive are BankCheck's equity identity, the audit's
    conservation and ReadPathCheck. Its own batch ("Memo now, own batch
    later").
  - **Retained earnings end below zero in 27 of 32 runs, and paid-in in 4.**
    Dividends that return excess capital are charged to retained, while the
    city's rescues sit in paid-in, and a buyback comes off paid-in at its
    whole cost. Jerus kept both; revisit with the owners' wipe-out.
    *(0.7.14: a resolution writes the old owners' paid-in off to retained, and
    the city's payment is paid in.)*
  - **A young city's first dollar land loan can shut the window abroad** for
    its next one ("owes dollars and sells nothing abroad"). A dollar note
    rolled in the same structure can shut it by the 45% service rule.
  - **Rollovers print to the log, not the inbox:** an unread notice forces
    the inbox open.
  - *(small)* The Game code map files `nextMonth()` and its neighbours under
    ROLLING WHAT FALLS DUE, the banner just above them (before, under
    BORROWING IN SOMEBODY ELSE'S MONEY). Two meanings of "retained" live in
    `Bank`: `getRetained()` is the Profit page's "Kept in the bank", and
    `retainedEarnings()` is the sheet's.
- **FOUND BY 0.7.12, NOT ITS TO FIX** (`the-firms-sell-bonds.md` §7; the
  implementer's record, round 8). Jerus, 2026-09-25: the shells and the
  untraced moves ship and go here.
  - **The young bank:** the founding bank ($32M of capital) fails before
    month 1000 when Manufacturing or Automotive collapses; autopilot 4 → 9 in
    the last round, not traced to its change. A plant retired at material
    price can take a sector past the line within months (autopilot seed 7: a
    $44M backstop against a $21M bank).
  - **Shells:** a sector with no plant keeps a dust debt and a pantry it can
    neither use nor sell for up to 3,200 months, defaulting on the interest
    every month — 78–96% of all cash-flow defaults, little money, and a log
    line every month. Nothing winds it up. A real company in that state is
    liquidated.
  - **Held at 10% the cities ended half round 7's size** (4,074 people
    against 8,077) in round 8. Untraced.
  - **Landlords waiting 250 months on the 15% down payment with an empty
    till:** households with no home on default seed 5 for 161 months.
  - **The desk over its book cap** at 65% of month-ends: the excess at fair
    value rarely sells.
  - **The purchase limit binds more** (5,933 sector-months, $8.8B of fleets
    not ordered, mostly Manufacturing's vans), and **Manufacturing's ceiling
    defaults are nearly all the unpaid money** ($5.9B of $6.7B). Untraced.
  - **Output needs its inputs:** a maker's output does not read the inputs it
    bought, so limiting them would make goods from nothing ($393B skipped with
    the same output). A production recipe, a sixth throttle beside the
    operating rate's five. Jerus: "Buy inputs whole, todo".
  - **Industry held at 25%:** payroll over revenue on interim loans for up to
    74 months, three times the distress rule's 24.
  - **Companies' cash goes abroad** ($6.7T abroad against $1.7T in tills
    after eight default runs), by `OutwardInvestment`'s rule; Jerus expected
    the bank or bonds.
  - **The 1.25 single-industry correlation multiplier** is kept and flagged
    ("Keep 1.25 flagged").
  - **The share rebalancing barely fires** ($0.47M offered over eight runs):
    a cell short of money has usually sold in the waterfall already.
  - **A banned firm with an empty till cannot restock** — the ban shuts the
    working-capital line.
  - *(small)* the bank's allowance lags a month on bonds it trades at the
    market's step; the bond plan's "largest bond no dearer than the loan"
    saves the issuer nothing at the margin; round 7's two negative-GDP months
    did not recur.
- **FOUND BY 0.7.11, NOT ITS TO FIX** (`the-landlords-take-a-mortgage.md` §5 and its docs pass):
  - ~~concentration~~ - priced since 0.7.12 (the IRB charge); what is left
    of it is the young bank, under FOUND BY 0.7.12;
  - ~~the dividend deducts gross principal~~ - net repayment since 0.7.12;
  - ~~share prices read `PAYOUT` × income~~ - they read the dividend paid
    since 0.7.12;
  - a landlord whose profit is under its mortgages' principal borrows the
    difference from the desk (seed 2: $1.46B); the lender's test is per
    building, not per landlord - and the 10% city's insurance losses came
    from the same place;
  - branch openings between the minimum and the target (35% of default
    openings): the recapitalisation trap is still open in that band;
  - the playtest's advisor and the police (the smaller default city);
  - screens that still read the risk-based measure alone when the leverage
    ratio binds: the History page's capital ratio and its "months under its
    target / the minimum"; the Lending page's "What its capital lets it lend"
    note (it prints `capitalTarget()` and `CAPITAL_RATIO`); and the branch
    planner's refusal, which reads "the bank is N% lent out - room enough"
    for a bank refused because it is under its minimum.
- **FOUND BY 0.7.10, NOT ITS TO FIX:**
  - Foreign paper records no issue yield: `Debt.getIssueYield()` is 0 for a
    dollar loan, so a trace reads its coupon instead.
  - `Game` and `DataSave` have no class-header first sentence, so the code
    map prints only their sections.
  - `NewGameCheck` §6 and §11 print the game's "construction started" lines,
    because `buildStack()` runs outside `quietly()`. This affects the prose
    output only.
  - A save from before 0.6.10 that is under month 120 and has bought reserves
    would show "the founders left US$1.0B"; it was founded otherwise.
  - Held at 10%, every seed now runs dry in years 185–253, against 3 of 8
    before. The old D$2.4B had been paying for a city that loses money for
    centuries at that rate.
- **FOUND BY 0.7.9'S IMPLEMENTER, NOT ITS TO FIX:** the Summary's THE BANK
  flag (`ui/SummaryScreen` ~L738) reads only strain ("n% lent"), not the
  capital rule that rations credit since 0.7.8 — `bank.status()` or
  `payoutStance()`; every other screen's opened lines still snap shut on each
  redraw — `Statement.opens()` is a one-line `Set<String>` field for
  `SectorScreen`, Finances and the rest; `HistorySave` records
  `bankCapitalRatio` as 10 when nothing is lent, so those months sit at the
  chart's ceiling and count as not under target; `Bank.status()` for a failed
  bank names 12.0% and leaves out `resolutionExitEquity()`'s one-branch floor;
  `Bank.netInterestMargin()` calls its denominator `weighted` and divides by
  the face book (the value is right); `Game.recapitaliseBank()` puts in
  `min(amount, cash)`, so a caller can make a partial rescue — whether the
  advisor does is unchecked; `BankCheck` §13's played city borrowed nothing for
  its families in 72 months, so their interest line is caused on a fixture.
- **FOUND BY THE 0.7.8/0.7.9 DOCS PASS:** ~~the Bank tab's landing rungs, the
  Lending page's quotes grid and the sector screen's rate note printed the
  quarter's risk spread beside the month's leverage and default rate~~ —
  fixed at the gate before the deploy: the three print the quarter's leverage
  and its default rate (`BusinessDebtManager.getQuarterDefaultRate()`, new,
  in `ReadPathCheck`'s sweep), and the grid's note says the rate is at the
  leverage over the last quarter.
  `ForeignCheck`'s "the same programme costs the same in the world's money"
  counts the material built from scrapped plant since 0.7.8 — a measurement
  change, not a premise change (the import bill alone read 1.0607, counted
  whole 0.9945, and 1.0034 with the sale switched off): review it, as 0.7.7's
  trailing-year change is. The repository's three mentions of the project's
  name (`CLAUDE.md`, `docs/notes/README.md`, `docs/docs-pass.md`) were brought
  to "Civic Ledger: Java Game" at the gate.
- ~~**THE BANK AS A BUSINESS, BATCHES 2 AND 3 — BUILT IN THE CLOUD, HELD ON
  JERUS'S ANSWER**~~ — done 2026-09-24: Jerus chose C (a sector's default
  partial); built as 0.7.8 and 0.7.9 over four rounds and deployed as tag
  0924a — see `a-sector-is-many-firms.md` and `the-bank-tab.md`. The safety
  copy in `Claude outputs/bank-0.7.8-0.7.9-held/` is superseded and can be
  deleted *(2026-09-24: the `git rm` went to Jerus, and `Claude outputs/` is in
  `.gitignore`: a session's scratch on the PC is not the repository's)*. Rescue for shares, and the government bidding on the exchange,
  stays a later batch *(built 2026-09-27 as 0.7.14)*.
- **HELD AT 10%, SEED 2'S PRICES RAN TO 5.64× FOUNDING.** The recovered bank
  paid savers its chosen 3.5% over the world's 2%, the households brought
  their savings home (66% abroad to 0%, US$2.7bn into a city of about 2,000,
  months 2,280–2,580), and the currency and prices moved with it before it
  unwound (1.16 at the end). 0.7.3's channel; the root is the deposits the
  bank counts and does not hold — later work. Note §6.
- **THE EARLY-GROWTH GAP IS NOT EXPLAINED** — 26,733 against 33,350 at month
  ~1,300. Not the investment hurdle (measured both ways); the untested lead is
  Real Estate starting its plans with less of its own cash (0.03 of a unit's
  cost against 0.07), which fits the 1% fee coming out of every loan. Note §4.
  *(0.7.8's default eight reach 39,676 at month ~1,300, past 0.7.6's 33,350 —
  the gap is gone, and still unexplained.)*
- **A SKILLS TRAP, PATH-DEPENDENT** — seed 3 on the intermediate build stalled
  at 21,333: growth paused, arrivals stopped, the skilled share fell 0.58 →
  0.11 and Manufacturing and Automotive refused every plant they could not
  staff. The playtest never builds a school (the §2 line); the final build does
  not enter it.
- **The bank's running costs are 0–7% of its revenue against a real 49–60%,
  and its fees 0–6% against ~39%** — its payroll is tiny against what it
  lends. Left as measured.
- **The city's floor sits above the dial in about half of seed 0's months**
  (`DebtManager` ~L1365): the bank's blended cost of funds is near zero, so
  the floor is "the dial or 1%"; `MIN_RATE` never binds.
- **`Equity`'s share drift** — the households hold a few hundredths of a share
  more than the register (627,527.663 against .618 on seed 0 from month
  3,471); the dividend is guarded at the division, the source is open (the
  register and the cells are moved separately).
- **`ForeignCheck`'s "outflow swamps trade" reads the trailing year** since
  0.7.7, because a one-off $14.7M import shipment landed in the fixture's last
  month — a measurement change, not a premise change. Review it.
- *(small, the note's §5)* `BusinessDebtManager` ~L77/~L326: a sector's rate
  is an absolute 1% (`MIN_SPREAD`) until it is first priced *(since 0.7.8
  `MIN_SPREAD` is gone with the leverage spread and an unpriced sector reads 0
  until the month's first pricing — the same gap, a different number)*;
  `HouseholdAccounts` ~L1088: `getRowSpending` leaves out fares;
  ~~`Bank.redenominate` does not rescale the saved last-month figures; the
  bank's month flows, its fees included, are not saved (a reloaded Income page
  reads zero for a month); `Game` ~L4282: a failure caused by the late items
  shows a month late~~ — done 2026-09-24 (0.7.8); `LongPlaytest` ~L2685: the carry figures are multiplied
  by a thousand twice; seed 3 has one month of negative GDP (m1012); seed 4
  has households with no home for sixteen months (749–765).
- ~~**FOUND BY THE 0.7.7 DOCS PASS — four player-facing strings still describe
  the strain premium**, and **`CapitalFlows.arrivalsAt()` has no caller**~~ —
  done 2026-09-24 (0.7.8): `Inbox`, `BusinessInvestment` with `BankCheck`'s
  reading, `YearBook`'s `bankStrain` and `UserInterface`'s tooltip say what
  is true, and `arrivalsAt()` is deleted.
- **LAND BOUGHT BY CONVERTING PUTS NO PUSH ON THE CURRENCY — open, Jerus's
  call** (`land-in-dollars.md` §5). The 0.7.6 brief asked that "whatever
  pressure the exchange puts on the rate when the treasury buys dollars
  applies here too", and there is none to apply: a treasury purchase of
  dollars is the financing item below the line (`MoneyAudit`
  `Scope.RESERVE`), which `pressure()` and `monthDeficitUsd()` never read, so
  buying reserves has never moved the rate and `ForeignCheck` §14's equality
  is an equality with nothing. Right for reserves (the dollars stay in the
  country); arguably wrong for land, where the world sold the city an asset
  and was paid in dollars the city had to buy — a city that spends its way
  through a decade of land sees no weakening for it. The fix, if wanted, is
  small: count the dollars paid abroad for land as an outflow the pressure
  signal reads (an import-like term in the trailing balance, or a
  capital-account term of its own), for the converting path only or for both.
- **Four more, found by 0.7.6 and not its to fix** (`land-in-dollars.md` §5):
  land is always bought between presses, outside every audit window, and
  `MoneyAudit` has never declared land, so nothing asserts the moment of
  purchase (the month after is asserted); a reload clears the month's open
  land line, so a save made between presses loses that month's land from the
  budget, as it always did — the dollar figures were kept consistent with
  that rather than fixed separately; the central bank counts the vault as its
  asset, so land paid from the vault lowers its equity with no line saying
  why (selling reserves already does the same); a pre-0.7.6 save's
  `landPrice` months stay in the old unit if the player reforms the currency
  after loading.
- ~~**THE COST MODEL CANNOT PRICE A BUILDING WITH TWO OUTPUTS.**~~ **FIXED
  2026-09-15 by `Sector.costShareOf()`, which splits a line's joint costs across
  its outputs by relative sales value — and it was never only a BUILDING problem:
  Business Services (3 goods) and Manufacturing (2) had been charging each of
  their goods the whole line's bill since they shipped. See the top entry.** The
  original note, which is what the fix was written against:
  `Sector.getCostPerUnit()` and `getMarginalCostPerUnit()` both divide the whole
  line's payroll, electricity, water and input bill by **one** good's output, so
  a plant with two outputs is charged its entire cost twice over — once against
  each — and neither clears its own marginal cost. **Measured 2026-09-15: 858,000
  kg of nameplate capacity and zero produced**, while the city imported the good
  beside it. Every one of the 55 buildings makes exactly one good, so nothing has
  ever exercised the path. **The fix is to split a line's costs across its outputs
  by revenue share**, which is a change to the cost model all ten sectors use and
  wants its own batch and its own sixteen seeds. Until then a template with two
  `makes` entries silently produces nothing, and `AgricultureCheck` asserts the
  ovens have exactly one.
- **TWO PLACES NAME WHAT A SECTOR MAKES AND NOTHING CHECKS THEY AGREE.** The
  sector's own `makes(Good)` list decides what `Markets` brings to market; the
  buildings' `makes(Good, n)` decides how much. On 2026-09-15 `FoodIndustry` said
  FOOD while its ovens said BREAD and **the good simply did not exist** — no
  exception, no log line, 858,000 kg of capacity producing nothing. A harness
  asserting that every good any template makes is declared by its sector would
  cost ten lines and catch the whole class.
- ~~**WAGES ARE INDEXED A THIRD ABOVE THE PRICE LEVEL THEY CHASE.**
  `COST_OF_LIVING_PASS_THROUGH` is 1.0 and `LabourMarket.updateCostOfLiving()`
  drifts `costOfLiving` toward `livingTarget = priceIndex` at 1/24 a month, so
  over 240 months the two should converge. Measured 2026-09-15 on a played city:
  **costOfLiving 2.648 against priceIndex 1.993**, with band multiples at
  0.71–1.17 so no scarcity premium is involved. That is a free real wage gain
  with no productivity behind it, compounding for the life of a run, and it is
  the single biggest reason the city's households look rich against their food —
  the *poorest* cell holds $7,660 a head a month against an average Canadian's
  ~$2,665. **Not chased yet; it is the next batch.**~~ — closed 2026-09-21 without a fix: it does not reproduce on today's code. `LabourCheck`'s "wages against the index" holds `costOfLiving` to the published index a `DRIFT_PER_MONTH` at a time through 240 months of steady inflation, a currency reform and a reload; on all eight seeds every one of the 144 checkpoint readings equals the lag-implied level exactly (`-Dplaytest.wages`), wages over the index between 0.84 and 1.12, above one only while the index is falling. See `the-bank-that-never-paid.md` §2.
- ~~**THE BANK NEVER PAYS FOR THE CITY'S PAPER.** `Game.java` ~L3601:
  `cityDebtRaisedForBank = cityDebtRaisedThisMonth` runs after the top-of-tick
  clear (~L3562), so `bank.lend(...)` (~L3625) receives 0 for every city bond;
  the bank's `cityBook` still rises and it later receives the principal, so
  after a $20M issue the bank's cash fell only by its ordinary month.
  Invisible to `MoneyAudit` because issuance is between windows. Found
  2026-09-19 while the treasury bridge was opened; a behaviour fix, Jerus's.~~ — done 2026-09-21 (0.6.11, ships with batch B): the settlement is snapshotted before the top-of-tick clear and zeroed once the bank has paid; what it still owes is carried against its pool in `MoneyAudit` (`Game.getCityPaperUnsettled()`) and saved under two `DataSave` keys. `BankCheck` §10, which fails four ways on the old order. See `the-bank-that-never-paid.md` §1.
- **TWO BUDGET LINES THE BALANCE OMITS.** The city's own repair bill in
  spending and the transit fares in revenue — the Government screen lists both,
  `NationalAccounts.getTotalRevenue()` / `getTotalExpenses()` carry neither —
  so the surplus figure, the ring's total, the Spending page's total and the
  `surplus` series are off by them ($2.56M a month in `SaveFileCheck`'s city);
  the bridge's last row had held exactly −repairs +fares every month.
  Journalled by name for now; the fix is two lines in `NationalAccounts` and a
  save slot, and then the two `record()` calls come out. **Transit wages are
  paid by nobody** beside it: `EconomyManager.getExpenses()` has no
  `transitBill`. 2026-09-19.
  **Since 0.7.31** the Government screen draws the budget exactly as
  `NationalAccounts` strikes it: repairs left Spending's list and ring (they
  had made the key add to 111%), and both lines are named under the totals as
  "outside the budget's total" and as steps on the bridge FROM EARNED TO
  BANKED. Carrying both in `NationalAccounts` is 0.7.31's D4 model batch,
  recommended to Jerus (CONFIRM from 0.7.31, section 0); the transit bill is
  the Government spec's B10 (MODEL BUGS, item 11).
- **THREE THINGS A NEW CITY OR A REFORM DOES NOT RESET OR SCALE.**
  `buildWorld()` does not reset `treasuryRecorded` and its siblings (a second
  new game after a played one opens its first window at the old closing
  balance); the reform does not scale the carried bridge fields; `LandManager`'s
  month counters are not saved. And a new city's first treasury window opens at
  the top of the first tick, so founding purchases show as a residual once.
  2026-09-19.
- **THE DEALER BIDS AT ITS OWN BUBBLE PRICE.** The desk quotes up to 5× fair
  value to draw sellers when it has unfilled demand, then pays that bid to
  emigrants and marks the shares at fair the same day — most of "the trading
  desk is losing billions". A bid that tracked fair value while the desk is a
  net buyer (ask left where demand puts it) would close most of it without
  touching the conservative mark; one constant and a harness section, measured
  on eight seeds. Jerus's call, 2026-09-19.
- **A HAIR OF BORROWING IS A DISCRETE STATE.** `Household.fundShortfall()`
  clamps a negative `still` but a positive 1e-21 becomes a debt, and
  `HouseholdBalance.investAbroad()` gates on `debt <= 0`, so a rounding
  difference between a city and its reformed twin flips whether a household
  may hold money abroad (the twins parted a decade later on `interest 2.36e-21
  vs 0.0` in one cell). Why the care relief within a row follows heads rather
  than paying heads. 2026-09-19.
- ~~**A FULLY-WRITTEN BRAKE THAT NINE SECTORS IGNORE.**~~ **Stale as of
  2026-09-18, found by the manual pass: `Game.consider()` (L1905) applies
  `servicesItsOwnDebt()` to every sector's decision and declines with "not even
  one would cover its interest", so it is wired for all fifteen. The original
  note, kept because the second half — whether it should be a whole-economy
  change with sixteen seeds — was never measured:**
  `BusinessInvestment.servicesItsOwnDebt()` — "if the new capacity cannot
  out-earn the interest on the money that built it, by a margin, the business
  declines the project even though the lender would fund it" — had **zero
  callers** from the day it was written until 2026-09-13, when Agriculture became
  its first. Every other sector still builds anything with a positive
  profit-over-cost score whether or not it can service the debt. It has never
  mattered because land is a rounding error everywhere else; a Steel Foundry is
  $30m of plant on nine tenths of a block. It mattered enormously to a $312k barn
  on six blocks. **Either wire it up for all ten or delete it** — and wiring it
  up is a whole-economy change that needs sixteen seeds either side. See
  `farms.md` §5.
- **FIFTEEN PLANNERS, EACH ASSUMING THE GROUND IS FREE.** *(Ten when written.)* The fields plan off the
  crop market, the houses off the jobs, the fabricators off the steel price, and
  none of them can see that they are bidding for the same land. The rule that
  stopped the farms crowding out housing (`Agriculture.plan()` refuses while
  anybody is unplaced) is a patch on one sector, not a mechanism. As more
  land-hungry sectors arrive this wants a real answer: a land market the planners
  bid into. Two of the last three sectors have been land-hungry.
- **`Game.isPrivateInvestmentLandLocked()` NEVER CLEARS.** `landBlockedSectors`
  is never emptied, so once a sector has been blocked once the flag stays true
  for the rest of the run — true at month 12 with 1,974,000 sq ft free and still
  true at month 300. The "nowhere to build" inbox notice is very likely firing on
  cities with plenty of ground. Found 2026-09-14, not fixed.
- **A DERIVED FIGURE WITH TWO COPIES DRIFTS, AND THE COPY THE PLAYER SEES IS THE
  ONE THAT ROTS.** `UserInterface.historyValues()` kept its own unemployment and
  average-wage arithmetic beside the model's, and the student correction made in
  the model on 2026-09-06 never reached it — so the People screen and the Reports
  chart quoted different rates for the same city until 2026-09-15. The four
  definitions live in `YearBook` now and both readers delegate. **Anything else
  the UI recomputes from two history series is the same shape**, and the rule is
  the one `PopulationManager.getUnemployed()` already carries: one definition,
  where the model keeps it.
- ~~**THE CURRENCY DRIFT FEEDS ON THE IMPORT PRICES IT SETS.**
  `ForeignAccounts.repriceCurrency()` moves the rate by the city's own inflation
  (`(localInflation − worldInflation) / 12` a month), and in a city whose basket
  is imports that inflation is the rate's own rise. Slot 3's founding went to
  the 100× ceiling on it; a probe reproduces a permanent doubling of prices from
  one heavy founding order and a 9%-a-year spiral from a broke one. Design, not
  a bug — see the top entries. Found 2026-09-15.~~ — closed 2026-09-22 (0.7.2):
  the drift is deleted (`ForeignAccounts`, THE DRIFT THAT WAS DELETED); the
  rate's monthly push is the trade balance and the real rate.
- **AN ARGMAX OVER QUANTITIES THE MODEL PRICES TO BE EQUAL IS A STEP FUNCTION
  DRIVEN BY DUST.** `Exchange.buyForHouseholds()` sorted companies "best yield
  first" and gave the winner the whole month's unmet demand — but
  `Equity.priceOf()` values an earnings-valued company at exactly the discount
  rate, so every such company ties to the last bit and the sort was choosing
  between copies of one number. A company's quote tripled in a month on a
  rounding error, once in roughly 200 months of buying. Fixed 2026-09-15 with a
  relative dead band and a pro-rata share of the unmet demand; see the top
  entry. **The general form is worth a sweep: anywhere the model computes a
  winner, ask whether its own rules can make the candidates equal.** *And
  2026-09-16 found the same shape without any argmax at all: a BOOLEAN whose
  input is a residue. `c.debt <= 0` decided who may hold money abroad, and four
  cells were carrying ±1e-29 of debt out of the household rebuild's own
  rounding. **Anywhere the model asks a yes/no question of a quantity that can be
  arithmetic dust, the answer is luck.** See the top entry.*
- **A STATE ARRAY'S WIDTH MUST COME FROM THE SAVE, NEVER FROM THE READING
  BUILD'S OWN COUNT OF THINGS.** `PopulationCohorts.restore()` derived it from
  the build's band count, so the day `AgeBand` grew, every existing file would
  have been read at the wrong offsets — births as people over 85, deaths as
  births — and **passed every guard**, silently and for ever. Fixed 2026-09-15 by
  saving the band names beside the values and mapping by name, which is what
  `equityKeys` and `householdCellKeys` have always done for the sectors and the
  household cells. **The sectors were safe and the pyramid was not**, and the
  pass over the rest of `DataSave` that this bullet asked for was run the same
  night and **found two more of the same class**: `FamilyModel`, which would have
  restored NOTHING — orphans, household matrix and formed-household memory —
  from every save on disk, and `Sickness`, which would have refused and silently
  reseeded every existing ring. All three read against one `bandNames` field
  now. **The extended rule: after finding one, audit for the rest BEFORE changing
  the enum**, because fixing the array that prompted the question makes the
  question feel answered. *And the split that followed added a third form the
  audit missed: a site that is band-NAMED rather than band-INDEXED —
  `RetiredHousehold.grownUps()` asked for members of SENIOR and quietly gave the
  over-85s no pension. **Ask the band whether it is retirement age; do not name
  one.***
- **NOBODY LENDS THE MONEY THAT EXISTS, and it is now measured.** *Half of it
  moves now: since 2026-09-10 the idle cash goes abroad for the world's rate
  (`outward-investment.md`), which recycles the surplus and prices the
  currency, but it is still nobody's loan and nobody's dividend — see "THE
  HOARD" in section 2.* On Jerus's
  month-1124 save: Construction, Heavy Industry and Mining hold **$20.2 billion**
  of cash between them and owe **nothing**, against a $49.7M monthly GDP.
  Real Estate, Retail and Industry owe **$2.39 billion** at ~7% and both of the
  loss-making sectors lose money *below* the operating line, purely on debt
  service. Households hold **$2.3bn of savings and $0 of debt**. Nothing in the
  model moves corporate cash to corporate borrowers. **This is the largest
  unexamined structure in the economy.** *The 2026-09-10 note that "at 1.00 the
  bank lends nothing" turned out to be measuring the wrong thing — the ceiling
  and the trigger were one constant. With a gap the bank keeps a book. But the
  audit batch ends the run with three sectors holding $24.6bn of cash and owing
  nothing while three others sit at −$23.5bn, which is this item restated with
  the credit system fixed. Still the largest unexamined structure. And since the
  template even the landlord repays: `bizDebt $0k` from month 1,800 on every
  seed.* *And measured again on 2026-09-14 against the cost-of-funds floor: the
  city's savings are about 200,000x what its productive sector wants to borrow,
  so the answer cannot be to invent credit demand — see "nothing borrows below
  cost".* *Slot 3 (2026-09-15): deposits pinned at `branches × 250,000` for
  seventy years, $9.25bn gathered of $685bn saved, because `wantsBranch()`
  builds on lending strain and there is none.*
- ~~**Rent is a price, not a market.**~~ **Done 2026-09-08**, rebuilt 2026-09-09
  into two markets, and finished 2026-09-10 when the floor stopped flattening
  them into one. See `the-price-that-stopped-being-a-price.md`.
- ~~**Retail's till comes out of a currency reform wrong.**~~ **Done overnight
  2026-09-09.** **Sixteen** bugs in that family, all the same shape. *Seventeenth
  found and fixed 2026-09-10 late: the two segment housing costs in
  `CommercialHandler.redenominate()`. Eighteenth, same night: the pension
  screen. Nineteenth, 2026-09-11: the bank's resolution floor read
  `PAID_IN_PER_BRANCH` in the founding unit, so a reformed city's bank never
  left resolution — found by `DenominationCheck` the first time a bank earned
  its way out.* ~~**And a candidate twentieth, 2026-09-14, in the history rather
  than in the state:** slot 3's early years read as a price index of 227.3 and a
  currency 100x from parity, which is the shape of a reform that redrew some
  history series in the new unit and left others in the old one.~~ **Not a
  twentieth — answered 2026-09-15.** Slot 3 never reformed; the early years are
  a real currency collapse to the rate's ceiling, and the history is right. See
  the top entries. *The twentieth of the wider family — a step function decided
  by dust rather than a constant in the wrong unit — arrived 2026-09-15 as the
  exchange's yield tie. See the argmax bullet above. **The twenty-first arrived
  2026-09-16 and is the purest of them: `HouseholdBalance.moveStock()` handing a
  cell a negative slice of the pool out of `weight * g / g > weight`, and
  `investAbroad()` reading the sign of what was left. Four retired cells owed
  minus 2.7e-29 and the city's exchange rate turned on it.** And a twenty-second
  beside it, an ordinary unseeded money constant: `investAbroad()`'s `1e-12`
  sweep, the fifth of that family — found while chasing the twenty-first, not
  its cause, and fixed anyway.*
- ~~**Vacant housing costs nothing to hold.**~~ **Done 2026-09-09.**
- **The rate schedule was calibrated around an accidental export levy.** Zero-
  rating steel was right and costs the city about $4M a month net. Nothing is
  broken, but a rate or a spending line probably wants to move.
- **`rentPerHousehold()` is not the per-let-door rent, and the Real estate screen
  calls it one.** One line to fix, either the label or the divisor. *And since
  the cells, it is the rent every household is charged — section 2's "rent per
  cell should follow the door".*
- **The investment planner ignores every utilisation ratio** in five of six
  sectors. Real estate is the exception and is the pattern to copy. (L3)
  *The template's maker rule reads the sector's operating rate into the
  estimate (`estimatedMakerProfit`); the planning itself still does not.*
- **Shopping is a headcount, not a budget.** Retail demand is
  `min(coverage, population)` with no reference to what households can afford.
  *This is probably half of the retail item in section 2.*
- **Retail's trend estimator is still wrong, just bounded.**
- **One sector can claim a year of the whole city's builders.** (H1)
- ~~**Construction capacity splits per stack, not per work remaining.** (backlog 2)~~
  — done 2026-09-29 (0.7.17): every building gets the crew it can use.
- ~~**Why is a Construction Materials Plant almost never built?** Twice in 4,000
  months, against 58 depots. (D1)~~ *Answered twice. At $2,000 a unit it made
  $800k of material a month with $969k of wages. Since the template the plant
  is the seventh sector's, built when the builders' order book says one would
  sell — and a mature city's building is bursty enough that it seldom does.
  See the template block in section 2.*
- **Roads are the new ceiling.** *98–100% on the latest playtests, which is much
  better.* The advisor still only ever builds Paved Roads. (G1)
- **The churn under a pinned unskilled surplus is now honestly measurable.**
- ~~**The Food Processing Plant**~~ — closed on 2026-09-10 as "not too big,
  under-producing", output x5 with jobs unchanged; reopened the same night for
  granularity; **closed again that night at a fifth of the size**, every figure
  in proportion, and the plant idles rather than flood its own price. What is
  left is the planner: it projects customers to the reachable population, so a
  growing city builds nine plants for a demand two would meet and sheds them
  slowly. Bounded now, not urgent, and the "planner ignores every utilisation
  ratio" item below. (backlog 10)
- ~~**The Commercial Bank employs 69 people.**~~ **29 as of 2026-09-10** — US
  commercial banking runs 29 staff per branch, and `DEPOSITS_PER_BRANCH` moved
  with it, $60M → $253M. It had already come down from 278 once; that correction
  was measured against a single month, and over 1,202 months the wage bill was
  still 180% of revenue.
- **No honest "cost of carry" for debt.** Needs `Debt.amortisedMonthlyCost()`.
- ~~**Construction now pays sales tax for the first time.** Still a decision
  nobody has made~~ — **made 2026-09-10 (night)**: it pays sales tax and profit
  tax, like the other five.
- **The founding endowment bills for care it costs nothing to provide.** Seen
  on Government in 0.7.31 (the spec's B16, traced by the implementer's
  `ProbeCare`): care fees of $104k with a care bill of $0 in the 600-month city
  are the founding endowment's; the doctor, the nursery, the almshouse and the
  churchyard charge and cost nothing. In the 2,400-month city most of the $1.0M
  of care fees is burials ($878k for 293). THE SERVICES THAT CHARGE's (i) says
  so.
- **`squeezeUnplaced()`'s two valves count households, not doors.** Found
  2026-09-10 while fixing the crowding floor: given no doors at all it will still
  report every household placed, because flatshares and doubling are pure
  arithmetic with no reference to whether a door exists to double into. It does
  not currently bite — `crowdingFactor()` now asks the placement rather than the
  formula — but it is the same blindness one level down.
- **`capacityWith()` and `depositsGathered()` disagree about whether hot money is
  subject to the branch cap** (`Bank.java`). The second exempts it, under a
  comment saying the asymmetry is the point; the first re-derives the limit and
  applies the cap to all deposits. So a bank funded mostly by hot money reports
  that another branch is worth nothing and never wants one. Latent — no run has
  held a persistent hot-money stock — but live the moment one does.
- **The spread clamp binds where the risk is.** `spread = .01 + .06 × leverage`
  capped at `.08` saturates at 1.17× assets; with the shortfall ceiling now at
  0.9 it no longer binds for shortfall borrowers, but an investment borrower at
  1.5× is quoted the same as one at 1.17×. Lesser now. Do not fix it by raising
  the cap — measured: a 12% cap made the bank poorer, because the borrower paid
  the extra interest by borrowing it.
- **Households pay no power or water bill.** Found on the template (2026-09-11)
  when the landlord turned out to be paying its tenants' showers: residential
  buildings are excluded from the utilities billing now and nobody is billed
  for what the homes draw. A household bill is a basket line when the basket
  grows.
- **A HOUSEHOLD WITH $201,363 PUT BY CAN STILL DEFAULT $390 SHORT.** Read off
  slot 3 at month 2305: every other cell sells its paper abroad and then its
  shares before it borrows, and this kind appears not to be wired into that
  draw-down path at all. ~~And **a prisoner's $70 student loan was not
  frozen** — a prisoner's debts are supposed to be, and the student loan is a
  fourth ledger that was missed.~~ — done 2026-09-21: it was frozen all along
  (nothing collects a student loan except from a working family; the $70 was
  the balance carried in), and the freeze is `PrisonerHousehold`'s own now, no
  instalment and no interest, asserted in `EducationCheck` §14. Found
  2026-09-14; the $390 default is still open.
- ~~**THE DEBT'S MONTHLY REVALUATION IS NOT SAVED.** `ForeignAccounts.lastRevaluation`
  (~L825, struck in `takeForeignDebt()`) is not in the save array, so after a
  load the trade and finance pages' "the currency moved it by" line reads
  nothing until the month turns. The vault's own revaluation is saved (slot 23)
  since 0.6.10. Found 2026-09-21, `a-reserve-defends-a-currency.md` §7.~~ — done 2026-09-21 (0.6.11): slot 24 of the foreign accounts' array, asserted in `SaveFileCheck` §12b; `SAVE_FORMAT` stays 27.
- ~~**A SCREEN WRITES TO THE MODEL.** `TradeScreen.currencyForcesPage()` (~L1320)
  calls `fx.effectivePressure()`, which writes `lastPressure` and
  `lastAbsorption`. Harmless today — same state, same values — but the
  interface is meant to read the model through getters and nothing else.
  `ForeignAccounts.reset()` also leaves `rateDifferential`, `localInflation`
  and `worldInflation` from the previous city until the first month turns
  (the simulation is unaffected). Found 2026-09-21, same note.~~ — done 2026-09-21 (0.6.11): `TradeScreen.currencyForcesPage()` reads `ForeignAccounts.previewPressure()`, the same arithmetic without the writes (`ForeignCheck` §13, `ReadPathCheck`). ~~**Still open from the same line:** `ForeignAccounts.reset()` leaves `rateDifferential`, `localInflation` and `worldInflation` from the previous city until the first month turns.~~ — done 2026-09-22 (0.7.1): `reset()` clears the three.
- ~~**A DEFENCE THAT SPENDS RESERVES — for 7.0.** Absorption costs nothing: the
  vault damps a push weaker in proportion to its cover and is never drawn down
  by doing it, which is why the default player never sees it work. A defence
  that sells dollars to hold the rate belongs with the money supply. Same
  note, §8.~~ — done 2026-09-22 (0.7.2): the central bank sells `absorption()`
  × the month's own deficit of the vault, at most the vault, against a push to
  fall; a capital transaction, equity down and M0 unmoved (`CurrencyCheck`
  §3–4).
- ~~**THE EMERGENCY NOTE'S FREE MONEY WAS A FLOOR — THE ADVANCES CEILING MOVES
  INTO BATCH B.** Held at a 10% policy rate (`-Dplaytest.policyRate=0.10`),
  six of eight seeds run their treasuries dry around months 2,800–3,800,
  before the fix and after it. Before it, all six survived on emergency notes
  their bank was handed free, peaking at $0.9–6.1B owed at about 8%. After
  it the bank really funds them, its strain explodes, the notes price at
  27–36% (policy, spreads and the 18-point premium) and the debt compounds
  without limit: $193 quadrillion on seed 0, NaN on seed 6, bank failures
  16 → 161. The overdraft as advances from the central bank with a ceiling
  (`the-central-bank.md` §6) moves from batch C into batch B, and 0.6.11
  ships with it, not before. The same pathology as **THE TREASURY'S
  OVERDRAFT HAS NO FLOOR** below.~~ — done 2026-09-21/22 (0.7.0): the emergency note is
  retired; a broke treasury is advanced its gap by the central bank at the
  policy rate, up to `CentralBank.MAX_ADVANCES_MONTHS` (six) of trailing
  revenue, and past that Jerus's rule, "pay promises first, cut the rest",
  through `Game.treasuryPays()`. Held at 10%, the six seeds end owing
  $0.1–2.0B, finite, the audit closed every month (`CentralBankCheck` §5–6).
  See `the-central-bank-opens.md`.
- ~~**A BOND BUYBACK PAYS NOBODY.** `Game.repurchaseDebt()` (~L6663) takes the
  price out of the treasury, and the bank's book drops by the principal at
  the next refresh with no cash arriving: a $20,000k buyback cost the bank
  $20,067k of equity. Before 0.6.11 a round trip cost the bank nothing,
  because it had paid nothing; now it loses what it paid. The holders
  (`the-central-bank.md` §5) are who a buyback pays.~~ — done 2026-09-21/22 (0.7.0) for the
  city's own paper: `Bank.sellPaperBack()` takes the price as cash and drops
  the book by the principal at once, the difference its gain or loss
  (`CentralBankCheck` §5). The dollar case stays open — the new line below.
- ~~**THE BANK'S BOOK INCLUDES THE CITY'S DOLLAR DEBT.** `Game.refreshBank()`
  hands `bank.refresh()` `debtManager.getAllPrincipal()` (~L1555), which
  counts foreign paper as well as domestic (`getDomesticPrincipal()` is the
  one without), and the weighted loop beside it (~L1576) walks the same list:
  paper the bank never bought. `BankCheck` ~L435 builds the error in ("the
  city book IS the treasury's principal").~~ — done 2026-09-21/22
  (0.7.0): `Game.refreshBank()` hands the bank `getDomesticPrincipal()` and
  the weighted loop skips foreign paper; `BankCheck`'s line is "the city book
  IS the treasury's principal at home".
- ~~**A TERM BOND'S WHOLE DISCOUNT IS BOOKED AS INTEREST IN ITS SETTLE MONTH.**
  `bank.takeDiscount(cityDiscountForBank)` (`Game.java` ~L3747): seed 0
  borrowing at home sold $148.3M of face for about $88M, and some $60M of
  "interest" landed in one month, taxed, 45% of it payable to savers. It
  should accrete over the bond's life. Batch C, with the curve.~~ — done
  2026-09-22 (0.7.1): each piece of paper carries its own discount
  (`Debt.getIssueDiscount()`, `getDiscountLeft()`) and accretes it
  straight-line over its life; the bank earns its share a month at a time and
  carries the rest as unearned against its book (`Bank.setUnearnedDiscount()`,
  re-derived at every refresh); `BankCheck` §10.
- ~~**A POLICY RATE OF EXACTLY 0% RELOADS AS 3%.** `Game.java` ~L7333 restores
  only a positive rate (`if (loaded.getPolicyRate() > 0)`).~~ — done 2026-09-21/22
  (0.7.0): `DataSave.policyRate` is boxed and null is the save without the
  key (`CentralBankCheck` §7).
- ~~**THE TREASURY'S OVERDRAFT HAS NO FLOOR, AND COMPOUNDS TO NaN.** Found
  2026-09-21 by the schools ensemble's first draft: a founding village handed a
  University ($210M, $620k a month of upkeep, on a town of three hundred
  making $1.8M a month) went −$286M by month 263, −$1.4T by 801, −$1,378T by
  1,033 and NaN by 1,632 (control seed 1). The fixture was made proportionate;
  the pathology is not fixed. What should stop it — a borrowing limit, forced
  austerity, a default — is a design question. See `the-price-of-a-place.md` §7.~~ — done
  2026-09-21/22 (0.7.0): the advances ceiling and the arrears rule are the
  floor, and the design question it asked (a borrowing limit, forced
  austerity, a default) is answered by Jerus's rule.
- ~~**NO HARNESS CATCHES A DOUBLED CALENDAR.** A `month++` left twice at the top
  of `nextMonth()` passed the whole suite during batch B; only the playtest's
  month numbers gave it away. `CalendarCheck` should assert that one press is
  one month. Batch C. Found 2026-09-21, `the-central-bank-opens.md` §4.~~ —
  done 2026-09-22 (0.7.1): `CalendarCheck` §7 — one press is one month, five
  months of a skip five, and `SimulationEngine.simulateMonth()` none.
- ~~**THE CONSTRUCTION RETAINER IS NEVER PAID.** `Game.constructionSubsidy`
  (~L663) is set, saved, reset and reformed, and no line of the month reads
  it; the standing policy replaced it. The docs pass marked the two comments
  that said otherwise (THE CONSTRUCTION SUBSIDY and `buildWorld()`); the field
  itself is batch C's — pay it or remove it. Same note.~~ — done 2026-09-22
  (0.7.1): removed — the field, its save key and its four lines; an old save's
  key is left unread. The standing policy is the lever
  (`TreasuryLine.CONSTRUCTION_SUBSIDY`).
- ~~**THE GOVERNMENT TAB HAS NO SUBSIDIES LINE.** `GovernmentScreen.spendingNames()`
  (~L799) lists none, though `NationalAccounts.getTotalExpenses()` includes
  them. Same note.~~ — done 2026-09-22 (0.7.1): `spendingNames()` ends with
  "Subsidies", read from `NationalAccounts.getSubsidies()`.
- ~~**STUDENT GRANTS LAG A MONTH.** Households are credited month N−1's bill at
  the top of month N while the treasury pays month N's; invisible to the audit
  because the households are outside the pools. Same note.~~ — done
  2026-09-22 (0.7.1): struck and paid at the top of the month on the students
  it opens with, where they are credited (`Game.nextMonth()`,
  `payStudentGrants()`); `OutsideCheck` and `EducationCheck` assert the same
  month's figure. EI still lags — the new line below.
- ~~**A DOLLAR-BOND BUYBACK PAYS NOBODY ON THE BOOKS.** `Game.repurchaseDebt()`
  (~L7070): between presses, the price leaves the treasury and no outflow
  abroad is declared. The domestic case pays the bank since 0.7.0. Same note.~~
  — done 2026-09-22 (0.7.1): the price is carried in the treasury's pool
  (`Game.getBuybackUnsettled()`) and declared the next month as `- city
  BuybackAbroad`, a financial outflow (`HoldersCheck` §8); and a domestic
  buyback pays every holder its share.
- ~~**A FAILED BANK'S HOLE STILL GOES ABROAD — JERUS'S QUESTION.**~~ — answered
  and built 2026-09-27 (0.7.14): the city takes the shares and pays the hole,
  the central bank advancing what the treasury lacks; nothing enters from
  outside (`the-city-takes-the-shares.md` §3). Was:
  `Bank.resolveIfFailed()` and `MoneyAudit`'s `+ bank ResolutionLoss` still
  declare the shortfall as absorbed from outside the city, though the bank's
  wholesale lender is the central bank's window now, and the window is repaid
  out of that inflow at the next settle. Who absorbs a failed bank — the
  central bank as lender of last resort, the depositors, the treasury — is his
  to decide; the Bank tab's resolution note carries a `TODO(docs)` to follow
  the answer. Same note.
- **THE TERM PREMIUM IS PROVISIONAL — JERUS'S NUMBERS.** `DebtManager.TERM_PREMIUM_10Y
  … 50Y` at 0.50, 0.90, 1.15, 1.35 and 1.50 points were set to have a curve at
  all; the shape (linear from a year to ten, flat past fifty) is the
  implementer's reading of the brief. `the-curve-and-the-holders.md` §1.
- **THE "SPREAD IS GONE" SELL-BACK CAN HARDLY EVER FIRE — JERUS'S QUESTION.**
  The households' yield is read off today's curve
  (`DebtManager.householdBookYield()`), which sits on the dial, and the
  deposit rate is a share of what the bank's book earned, so the paper always
  out-yields deposits and `HouseholdBalance.sellPaperForSpread()` never sells
  in a real run; `HoldersCheck` §4 exercises it by handing it a zero yield.
  Whether the rule should read the coupon locked in at issue is his. Same
  note, §4. *And 0.7.2 saw it fire: in `BankCheck` §10's settle month the
  deposit rate rose past the paper's yield (6.825% against 6.726%) once the
  empty household cells were folded, and the desk bought $1,080k back; the
  fixture's issue was halved so it holds its premise, with a line that says
  so. `the-currency-off-its-rule.md` §3.*
- ~~**A DOLLAR BOND IS VALUED AT THE CITY'S SHORT RATE** (`DebtManager.marketValue()`,
  read by `quoteRepurchase()`), and a dollar term loan is priced flat at the
  world rate at every maturity. What the world's paper is worth is open —
  batch D, with the currency. Same note.~~ — done 2026-09-22 (0.7.2):
  `DebtManager.foreignCurveRate(months)`, the foreign rate plus the same
  term-premium table, prices a dollar issue by maturity and values a dollar
  bond at its remaining months (`ForeignDebtCheck` §8); and the dollar quote
  prices its own coupons in, which closed a money pump the old pair made
  (`RestructureCheck` §5's dollar case, failing on 0.7.1).
- ~~**EI BENEFITS LAG A MONTH, AS THE GRANTS DID.** Households are credited last
  month's bill at the top of the month; the treasury pays this month's at the
  bottom. Invisible to the audit because the households are outside the
  pools. Not touched — the brief named only the grants. Same note.~~ — done
  2026-09-22 (0.7.3): struck again on the pool the month opens with and paid
  at the top of the month, where the out of work are credited it
  (`Game.payEiBenefits()`, `Unemployment.restrikeBenefits()`); `OutsideCheck`
  §7 reads the closing month and the month after. A 0.7.2 save pays one
  month's EI twice on its first month. The premiums may carry the same lag —
  the new line below.
- ~~**A TIME SKIP HALTS AT CASH ≤ 0**~~ — closed 2026-09-28 (0.7.15): the
  skip runs on the central bank's advances, as the play clock does (Jerus:
  "Skip runs too"). Was: (`Game.java` ~L2481, `SkipReportCheck` "an
  empty treasury"), but since 0.7.0 `settleTreasury()` advances a broke
  treasury's shortfall, so the skip stops a city the month would carry. Stop
  at the ceiling, at arrears, or not at all — Jerus's call. Same note.
- **DOLLAR-LOAN PROCEEDS LAND IN THE GAP BETWEEN PRESSES.** `foreignDebtRaisedThisMonth`
  is set when the loan is booked and cleared at the top of the next month
  before the audit's pools are read, so the inflow is in no month's window.
  It leaves no residual, and no comment says so. Same note.
- ~~**THE TRADE TAB'S FORCES PAGE LEAVES OUT THE INFLATION DRIFT** that
  `ForeignAccounts.repriceCurrency()` applies. Batch D's, with the drift.
  Same note.~~ — done 2026-09-22 (0.7.2): the drift is deleted; the page shows
  the four terms that exist — trade, the real rate, the vault's defence,
  parity.
- ~~*(small)* `Game.holderShares()` has two branches that compute the same
  thing (`hh * owed / out` and `owed * hh / out`). Harmless; one line.~~ —
  done 2026-09-22 (0.7.3): one line.
- ~~**THE HOLDINGS DIAL FORGETS ITS PACE ON A RELOAD.** Found by the docs pass:
  the load path set the dial through `setTargetShare()` from the empty bank
  `restore()` founds, so `previousTarget` read 0 for every reloaded city and
  a move saved half way through resumed at the slower pace.~~ — done
  2026-09-22 (0.7.1, before the deploy): `CentralBank.restoreTargetShare()`
  sets the dial without touching the memory; `CentralBankCheck` §14 turns the
  dial down mid-move, saves, and asserts the reloaded city steps at the same
  pace (the old line fails it two ways).
- **M0 CAN READ SLIGHTLY NEGATIVE FOR ONE MONTH** when a treasury repays every
  advance and the interest is destroyed at the top of the month and remitted
  at the top of the next; the identity holds. For the Money page. Same note.
- ~~**THE CEILING IS SMALL IN A SMALL CITY.** Six months of revenue binds within
  months of first drawing, and nearly all borrowing after that is for
  promises, past the ceiling. `MAX_ADVANCES_MONTHS` as a dial on Jerus's page —
  batch D. Same note.~~ — done 2026-09-22 (0.7.2):
  `CentralBank.advancesCeilingMonths`, 0 to `MAX_ADVANCES_CEILING` (36),
  default `DEFAULT_ADVANCES_MONTHS` (6), chips on the monetary page, saved
  under its own key, `-Dplaytest.advancesMonths` (`CentralBankCheck` §16).
  Held at 10% on 0.7.2 none of the six broke seeds draws at all.
- **THE CENTRAL BANK ENDS WITH NEGATIVE EQUITY IN ALL EIGHT DEFAULT SEEDS**
  (−$1.1B to −$3.3B; 0.7.1 was −$0.2B to −$3.7B): mainly the loss carried from
  paying interest on reserves, now plus the vault spent defending the
  currency. Found 2026-09-22, `the-currency-off-its-rule.md` §4.
- **INTEREST ON RESERVES COMPOUNDS WITHOUT LIMIT AT A HELD EXTREME RATE.** Held
  at 30% or 50%, M0 reaches 10⁸ to 3×10¹⁴ $B while prices stay near 1: spare
  cash earning the rate in new money that becomes spare cash. The same
  mechanism made 0.7.1's quadrillion at 25%. Same note.
- **MANUFACTURING'S FABRICATION WORKS FAILS THE BANK** on 0.7.2's path —
  bank failures 8 → 19 on the default eight, and Manufacturing 3 → 14 of
  them: land comes free at months 127–128, Manufacturing borrows $74M against
  a bank whose equity is $30M for a plant that loses money before interest,
  written off at month 166 and the restructured remainder at 178 in seeds
  0–3, and in some seeds it rebuilds and fails again. The fix belongs in the
  investment rule (lending past the bank's equity for a plant that loses
  money before interest; an insolvent sector rebuilding). Same note.
- **THE ADVISOR REFILLS THE VAULT THE DEFENCE SPENDS** (`LongPlaytest` ~L1191,
  the war chest): in effect the treasury pays for the defence in a playtest —
  seed 1 sold US$1.29B of a US$1B vault, and held at 50% seed 1 sold US$625B.
  Same note.
- **THE RATE TERM ACTS BEFORE SETTLING.** `ForeignAccounts.pressure()` returns
  0 before `SETTLING_MONTHS` (~L319) but `ratePressure()` does not, so the
  real-rate term and the defence act from month 1. Predates the batch. Same
  note.
- **THE CENTRAL BANK'S MONTH FLOWS ARE NOT SAVED** (`CentralBank.toSaveArray()`):
  after a reload the Money page's "this month" lines read zero until the month
  turns, and `defendedThisMonth` follows the pattern (the foreign accounts'
  own defence figures are saved, slots 25–27). Same note.
- **TWO READINGS OF THE REAL RATE.** The Trade tab's forces page reads the
  differential the last reprice was handed
  (`ForeignAccounts.getRealRateDifferential()`), the monetary page the live
  figure (`Game.realRateDifferential()`); they differ between presses when the
  dial moves. Same note.
- **`HealthCheck`'S "THE DEAR CITY IS NO HUNGRIER THAN THE FREE ONE"** compares
  one end-of-run month and passes by chance; averaged over its 96 months the
  dear city is 7.5 points hungrier. Same note.
- *(small)* `InfrastructureCheck`'s known line went green on 0.7.2's first
  build by accident (the shelves 78% against 31% instead of 100% against
  100%) and red again on the final: the fixture is sensitive to the currency.
  And for the record: the brief's UIP gaps (+3, −12) assumed a world real rate
  of +2; with `WORLD_BASE_RATE` .02 and 2% world inflation they are +5 and −10,
  and `CurrencyCheck` asserts against the constants; seed 0 does not run dry at
  a held 10% on 0.7.1, as batch B's table had it. Same note.
- **THE CURRENCY'S NEW NUMBERS ARE PROVISIONAL — JERUS'S.** `ForeignAccounts.RATE_PULL`
  4 and `CapitalFlows.MAX_SPREAD` .25 (both "Jerus's number to settle"), and
  whether the defence should answer only a crisis — the founders' billion goes
  on structural deficits, not crises: all eight default seeds sell more than
  half of it and six all but 1%, while the playtest's advisor buys it back.
  `the-currency-off-its-rule.md` §2.
- **THE FOREIGN-DEFAULT RULE READS ONE MONTH'S CASH.** Found by the docs pass.
  `Game.checkForeignSolvency()` compares the cash at the bottom of the month
  with `DEFAULT_OVERDRAFT_YEARS` of revenue, but the central bank advances the
  whole shortfall at the top of every month, so it trips only on a month whose
  own bills (a balloon falling due) exceed a year of revenue; a long
  insolvency accumulates in the advances, which it does not read. Whether it
  should read them is a design question; marked `TODO(docs)` in the source.
- ~~*(small)* `MonetaryCheck` §6 sums its own M2 (households and sectors), not
  `Game.getM2()`, which adds the world's deposits; marked `TODO(docs)`. Decide
  with batch E, when the section starts asserting.~~ — done 2026-09-22
  (0.7.3): §6 reads `Game.getM2()` and asserts that inflation falls with the
  rate — within `MEASUREMENT_NOISE`, and the 3% row `TRANSMISSION_FLOOR`
  above the 40% row; 1.032 points.
- ~~**WHO GETS 45% — JERUS'S, AND THE CHEAPEST LEVER THE CHANNEL HAS.**~~ —
  done 2026-09-23 (0.7.7): `DEPOSIT_PASS_THROUGH` is gone; the bank chooses a
  share of the dial by how it is funded (`Bank.DEPOSIT_SHARE_FLUSH` .35 to
  `DEPOSIT_SHARE_AT_WINDOW` .90), 0.37–0.44 on average over the eight seeds.
  See `the-bank-prices-like-a-business.md` §2.
- ~~**THE DEPOSIT QUOTE'S CAP BINDS BEYOND THE FOUNDING**~~ — done 2026-09-23
  (0.7.7): the cap went with the income share; the rate reported is what was
  paid, and a rate chosen under the window's is under prime by construction.
- **THE SHELF DOES NOT ANSWER MONEY** — the structural reason the channel
  cannot reach the index: `sectors/Retail.java` ~L436 (the floor,
  `OPENING_SELL_PRICE`) and ~L325 (`rWantedDemand = min(coverage, wanted)`),
  so the scarcity mark-up reads the shops' coverage and a tenth less want
  moves no price. The next design. Same note, §2 and §4.
- **THE RATE GOES NaN IN A CITY WITH NO TRADE** (`ForeignAccounts` ~L798): a
  founding without the advisor goes NaN at month 26 on 0.7.2 and 0.7.3 alike —
  `pressure()`, `absorption()` and `realRateDifferential()` read NaN first,
  and the clamp passes it through. Not traced to the root. Same note.
- **EI PREMIUMS MAY CARRY THE LAG THE BENEFITS HAD** (`EconomyManager` ~L829,
  read at `Game` ~L1078): struck at the bottom of the month, read at the top.
  Same note.
- **THE LOAD PATH'S RE-STRIKE IS NOT THE SAVED MONTH'S PLAN** (`Game` ~L4164,
  `HouseholdBalance` ~L920): the spend factor is not saved, and the load path
  strikes it on the month's closing deposit rate and index — next month's
  figures — after savings have had their deposit interest. Harmless while the
  retail capacity is carried, by the design note's reading; the docs pass
  wrote the exception into the rule's comment. Same note.
- *(small)* `MonetaryCheck` ~L282 ("the currency is not against a bound") and
  `ForeignCheck` ~L594 ("inside its bounds, every month of the way") still
  pass and now assert almost nothing, at a guard of a billion either way.
  Same note.
- ~~**THE PEOPLE SCREEN'S "EI PAID THIS MONTH" IS NEXT MONTH'S BILL.** Found by
  the docs pass: `PeopleScreen` ~L889 read `Unemployment.getBenefitsPaid()`,
  which since 0.7.3 is the bill struck at the end of the month on the pool it
  produced — paid at the top of the NEXT press.~~ — done 2026-09-22 (0.7.3,
  before the deploy): the line reads what the treasury paid,
  `EconomyManager.getEiBenefits()`; "per claimant" still reads the pool's
  figure, which is the pool's own.
- *(small)* **Three loose ends of the unpinned rate, found by the docs pass.**
  `LongPlaytest`'s player divides a dollar amount by `Math.max(.01,
  fxRate(g))` (~L1711, ~L2384, ~L2394), a floor from when the rate could not
  go under .01; `GameVersion`'s 0.7.3 note says every page's rate goes through
  one formatter, but the strip's tooltip prints parity at `%.2f`; and
  `Household.plan()`'s eating-out note ("a household that got everything it
  asked for still banks a quarter") holds as written only at a spend factor
  of 1 — above it the grocer, the counter and the table all ask more of the
  same income, and what is banked can be less than a quarter of the surplus.

---

## 5. Healthcare — what is left

The buildings, the funding, sickness, mortality, births, death care and the
warnings are all in. See `healthcare-funded.md`,
`healthcare-mortality-and-births.md` and `healthcare-panel-and-endowment.md`.
What is still open:~~ — done
  2026-09-21/22 (0.7.0): the advances ceiling and the arrears rule are the
  floor, and the design question it asked (a borrowing limit, forced
  austerity, a default) is answered by Jerus's rule.
- **No per-care-type P&L.** Fees and costs are pooled, so you cannot see that a
  Memorial Cemetery breaks even at ~11 burials a month and a Crematorium only at
  92% of its throughput — both true, both invisible.
- **The infant-mortality swing is huge in ratio and small in people**, because
  `AgeBand` puts infant mortality at 0.10%/yr, a modern figure.
- **General care does not touch teen mortality separately.**
- **CPP and pension rates as Policy-tab levers** rather than constants.
- **The birth rate at full childcare is 30 per thousand.** Canada's is 10. On
  slot 3 it is what makes a land-bound city export 200,000 people a decade —
  see the top entries. A calibration to decide, 2026-09-15.
- ~~**SENIOR CARE IS MEASURED AGAINST EVERY PERSON OVER 70**~~, while real
  long-term-care residency runs 2.0% at 70–74 and 29.6% at 85+ (2011 census).
  **Done 2026-09-15 with the split**: `CareType.SENIOR` serves both retirement
  bands and `placesPerHead()` weights them, an elder needing a whole place
  against a senior's 0.19, and `Healthcare` gives the elder band its own 1.80x
  care swing against the seniors' 1.35x. **The weighting is normalised on the
  elders rather than absolute**, deliberately — the raw census rates would have
  divided the denominator by ten and handed every existing city full senior care
  overnight. The absolute version remains a separate decision. See the top entry.
- **NOBODY IS PRICED OUT OF CHILDCARE, AND THAT IS THE DIAL'S LIMIT.** The
  fee fixture showed the working poor can afford $50 a head, so a fee that
  prices a family out of a nursery is a fee `healthFeeScale` cannot yet set —
  the cliff catches only households with nothing, a few dozen to a few hundred
  people a month at ×1. The ×15 ensemble is still to be measured. 2026-09-19.
- **THE HEALTH PREMIUM HAS NO EMPLOYER HALF AND NOTHING BALANCES IT** —
  employee side only, surplus or shortfall the treasury's, per Jerus. An
  employer share, automatic balancing against the service's cost, and whether
  the fees should index to wages (the played break-even is ×7–13 because they
  do not) are three separate decisions. Funerals are unscaled by design.
- **AN ELDER LIVING ALONE IS THE CELL TO WATCH** now, the way the senior living
  alone was: 75% of the over-85s are alone against 45% of the seniors, and the
  rent-per-cell item in section 2 charges them all the city's average door.

---

## 6. Housekeeping

- **The Services tab's "What tuition raises" table strikes each course's
  revenue on the screen** — `feeFor(course) × enrolled × (1 − subsidy)` in
  `ServicesScreen` — where `Education.getFeesOf(type)` has held it since
  0.7.6. A code change (the screen should read the getter, per the house rule
  that a screen never recomputes a model figure), found by the 0.7.6 docs
  pass and left for an implementer.

**THE STRUCTURE, after the audit of 2026-09-18** (`the-ai-ergonomics-audit.md`) —
each one Jerus's call, in the order they pay back:

- ~~**Split `UserInterface.java` along its banners, screen by screen**~~ —
  **done 2026-09-18**, see `splitting-the-interface.md` §7–12: the `ui/`
  package, the toolkit (`Money`, `Statement`, `Pieces`, `Levers`), and twelve
  screen classes (eleven tabs' plus `SummaryScreen`; Infrastructure is drawn by
  `ServicesScreen`); the window is about 4,000 lines. History and People were opened on
  the PC; the other nine were built in the cloud while the PC was off and went
  over when it came back (124 files, byte-for-byte). **Still to do on the PC:
  Clean and Build, open every tab, run a few months, press the rail.**
- ~~**Then `Game.java`, more gently**: the cars, the luxury counter, the crime
  causes, what the city eats and the currency reform each have the shape of a
  class already; `Game` keeps the seam and the month.~~ **Done 2026-09-18
  (evening) for the first four** — `Motoring`, `LuxuryCounter`, `Offending`,
  `CityBasket`, playtest byte-identical, see `splitting-game.md`. The currency
  reform stays in `Game` (it redenominates twenty-two subsystems through
  Game's own fields). What is left is bigger and entangled — the investor
  (910 lines), the foreign borrowing (423), demographics (534) — and each is a
  plan of its own, not a move; §6 of the note sizes them.
- **`unhousedShareOfCity()` is public on `Game`** and its only caller is
  Game's own health step; nothing in `ui/` or a harness reads it. Narrow it or
  leave it — found by the split, 2026-09-18.
- ~~**Mirror `todo.md`, `changelog.md` and `index.md` into `docs/notes/` in the
  repository** on every deploy.~~ **Done 2026-09-18 (evening)**: the three are
  copied into `docs/notes/` as part of every deploy from the cloud loop; the
  project is the original, the copies are as of the last deploy.
- ~~**A `Stale` tool in `tools/`** — the mechanical half of the docs pass.~~
  **Done 2026-09-18 (evening)**: `tools.Stale` + `StaleCheck` (the
  fifty-seventh harness), firm categories at 0 across the tree after the
  84-javadoc cleanup; see `the-prose-that-stopped-being-true.md`. ~~**Left for
  a reader who knows the mechanic**, found by the pass and not decided: four
  comments whose numbers disagree with their constants.~~ **Decided by Jerus
  2026-09-18 (night) — "the code is correct, the comments are the outliers" —
  and rewritten**: `foundingTuition()` says 1.20 now, `FamilyModel.restore()`
  "six lengths accepted" with the six listed, `CELL_SLOTS` ends at the meals
  eaten out, `lifetimeIntervention` names `balanceFromFlows()`. Still open
  from the same pass: `docs/dials.md` shows `Equity.BANK` with
  no sentence; and thirteen soft findings name members nothing declares
  (`treasuryUnexplained()`, `refreshCommercialReport()`, `planFromLoad()`,
  `rollIron()`, `LongPlaytest.checkMonth()`, `planRetail()`,
  `computeMonthlyReport()`, `budgetPie()`, `LuxuryRetail.sellOwnPriced()`,
  `Rail.fuelBill()`, `showMiningMenu()`, `tuitionOf()`, `getPlotsLeft()`) — run
  `Stale report.bat` to see them in place.
- ~~**THE TUITION TABLE WAS CALIBRATED AGAINST THE WAGES BEFORE THE REBALANCE.**
  Found while fixing the comment above: `Education.foundingTuition()`'s prose
  measures the university fee against "a diploma wage of 1.500", and the class
  header says a university place "costs a diploma-holder half their monthly
  income and almost nobody goes" — but stage one of the rebalance (2026-09-09)
  put `PayTier.SKILLED` at 4.500, and `affordability()` reads the live band
  wage. Against 4.500 the fee is 27% of a month unsubsidised (well inside
  `MAX_BURDEN` 0.60, so half could pay with no subsidy at all) and 11% at the
  default subsidy, so the poverty trap the header calls "the most interesting
  thing on this page" has been mostly quiet since the rebalance. Either the
  fees scale with the wages (about 3x, keeping the trap) or the prose says the
  trap is gone — a balance decision, Jerus's. `EducationCheck` §8 asserts
  only the direction (free tuition graduates more than full price) and its
  own banner repeats "more than half a month's pay", so it passes either way
  and wants a strength assertion once the fee is decided.~~ — done 2026-09-21: the price is the player's dial
  now, `TaxPolicy.tuitionScale` 0–5×, default 1 (the founding table, unchanged).
  At ×3 the trap is back as the prose describes it — a one-university city at
  five years has 1,718 students and 69% of its diploma-holders willing at ×1,
  1,173 and 26% at ×3, 1,756 and 90% at ×0. `Education`'s class header and
  `foundingTuition()` say so. Carried forward from it, still open:
  `EducationCheck` §8 asserts only the direction and its banner still says
  "more than half a month's pay" (27% at today's wages, ×1) — it wants its
  strength assertion at ×1 and ×3 now the fee is a dial (§15 asserts the trap
  on its own fixture).
- **Prune this list.** Section 2 still carries the built narratives of
  2026-09-10 and 2026-09-11 next to the two open questions that came out of
  them; moved to the changelog and left as lines with pointers, the list is
  under five hundred lines. Wants Jerus's eye on what is still open.
- ~~**The manual is three versions behind** — version 5 describes 0.5.15; three
  sectors, the transport stack, the basket and the vehicles have shipped since.~~
  **Done 2026-09-18 (night): version 7 at 0.6.7** — see `the-manual-at-0-6-7.md`.
- **FOUND BY THE MANUAL PASS, 2026-09-18 — twenty places where prose disagrees
  with code**, none fixed; the first nine are a docs pass's (Opus, small), the
  rest are this list's and the notes'. In the tree: `sectors/Rail.java` L113
  says the road relief is "70%" over `RAIL_ROAD_RELIEF = .75`;
  `TaxPolicy.MAX_TRANSIT_FARE`'s javadoc calls `.05` "a multiple of the
  default" when it is $50 a ride; `HouseholdBalance`'s banner says "SIXTY-EIGHT
  CELLS" and the constructor builds seventy-eight; the Diner is sized in
  `BuildingManager` L1468, `Restaurants.java` L80 and `a-meal-out-is-food.md`
  from "eight staff at $30.8k" over a template with four posts;
  `AgeBand.java` L56 and L140 say five bands; `Good.java` L780 says
  "twenty-five more numbers" for thirty-one goods; `Game.java` L4082 says
  "eleven sets of books and a twelfth" for fifteen; `Automotive.java` L62
  says none of its customers exists yet; `buildings.json` `nextId` is 69 under
  a highest id of 72. In this list and the notes: §4's "a brake that nine
  sectors ignore" is stale (`Game.consider()` L1905 applies
  `servicesItsOwnDebt()` to every sector — struck below); §4 "ten planners" and
  §6 "eleven screen classes" are fifteen and twelve (fixed below);
  `docs/harnesses.md` lists sixteen classes no harness names, not eleven (fixed
  below); `the-freight-band-and-the-three-loads.md` says 69% ore in prose and
  68% in its table; `LuxuryRetail.java` L67 claims a current-account deficit the
  default seed does not run; `Restaurants.java` L100 and `LuxuryRetail.java`
  L78 count the money-constant family at twenty-six and twenty-five;
  `Sector.operations()` prints four of the five ratios; the transit payroll is
  outside G like education's; `Good.java`'s vehicles banner compares the cars'
  "18.2% band" with the shelf's "21–25%" on different bases; the residue
  block's "dearest thing in the catalogue per acre" is the dearest farm;
  `the-instrument-panel.md` says nine batches and lists eight. Full list with
  lines in `the-manual-at-0-6-7.md` §4.
- **FOUND BY THE MANUAL PASS, 2026-09-22 — sixteen places, and eight of the
  twenty above are still in the tree**, none fixed; the first eight are a docs
  pass's, the rest this list's and the notes'. In the tree: `DebtManager` L949
  and L959 say "ten points" a measure over `MAX_SPREAD_PER_MEASURE = .05`;
  `CentralBank.QE_SPEED`'s javadoc, `Game`'s THE HOLDINGS DIAL banner and
  `PolicyScreen` L2049 describe the QE step as two things (a share of the gap,
  the larger of two) when `stepFor()` takes the largest of three;
  `CapitalFlows.MAX_SPREAD`'s javadoc says "a 5% world" over `WORLD_BASE_RATE`
  .02 (flagged by the 0.7.2 docs pass, still there); `ForeignAccounts.MAX_RATE`'s
  javadoc says a currency at a thousand "reforms it" when the reform is never
  automatic and unlocks on the index; `GameVersion`'s 0.7.2 entry says "six
  spent all of it" for "six all but 1%"; `MonetaryCheck`'s "9,900%" label
  against `MAX_POLICY_RATE`'s "2,500%"; `BankCheck` ~L1101 and
  `Bank.fundToCover()`'s note disagree about a bank paying savers more than it
  charges (payout versus quote); `MonetaryCheck` ~L282 and `ForeignCheck` ~L594
  assert almost nothing at a guard of 1e9; `LongPlaytest` divides by
  `Math.max(.01, fxRate)` in three places, a floor from the old guard;
  `Household.plan()`'s eating-out note holds only at a spend factor of one;
  the tree says the playtest is 4,002 months in a dozen places and the run
  reports 4,005 (the front doors now say "4,000-odd"); `docs/dials.md` prints
  no sentence for 159 of 780 constants, among them dials the manual leans on
  (the five `Healthcare` fees, `LICENCE_COVER_TO_OPEN`, `MAINTENANCE_RATE`,
  `CARRY_REPAY_SPEED`, `MEAL_SHARE_OF_SURPLUS`, `MOST_MEALS_EATEN_OUT`,
  `DEFAULT_FARMLAND_RELIEF`). In the notes: `the-currency-off-its-rule.md` §5's
  "121 of 144 months with sales" is 0.7.2's figure and `CurrencyCheck` prints
  116 at 0.7.3 (§2's "two quarters" sentence is corrected in place). In this
  list: three items §3 carried after 0.7.0 closed them are struck tonight.
  Full list with lines in `the-manual-at-0-7-3.md` §4.
- **FOUND BY THE MANUAL PASS, 2026-09-28 — eight places**, none fixed:
  - `buildings.json` L61 `"nextId": 69` while the ids run to 72;
  - this list's 0.7.12 save entry types interim loans "INTERIM" (the code's
    type is "INTERIM-LOAN");
  - `changelog.md`'s 0.7.7 block gives the capital charge with 11% (since
    0.7.8 it is the bank's own target, floored by the leverage ratio);
  - `Household.java` L869 "Sold at the bid to the bank's desk", of shares — no
    dealer since 0.7.12;
  - `TreasuryFund.java` L44–48 "The hand may", of the rescue book, leaves out
    that the preferred and its warrants cannot be sold;
  - the playtest prints "ill for 301 months of 4001" for a 4,002-month run
    (the outbreak counter starts at month 2);
  - `GameVersion.java`'s version headings are titled and dated a little
    differently from this changelog's blocks;
  - every screen but the Bank tab snaps an opened statement line shut on the
    month's redraw (`Statement.opens()` is used by the Bank tab alone).
  Full list in `the-manual-at-0-7-15.md` §4.
- **FOUND BY THE MANUAL PASS, 2026-10-01 — nine places**, none fixed:
  - `DecisionLog.java` L28–30 says the playtest advisor's decisions are
    recorded the same way; its policy-rate steps are not logged;
  - `buildings.json` L61 `"nextId": 69` still (above);
  - `ui/HistoryScreen.java` L25 still says "The Reports tab";
  - `Construction.IDLE_PAYROLL_FLOOR` is a share of posts since 0.7.17; the
    name reads as the old wage floor;
  - `BuildingManager.java` L69–70 says 5.28 a point of labour for a Low-Rise
    where the 0.7.19 note says 6.59 (maybe two measures);
  - `BuildingManager.java` L3076–3078 "a four-hundredth" was measured beside
    the coal plant and the founding's other big works; beside the plant alone
    it is about a 168th;
  - `ConstructionControlCheck` §7's label says "market value" where the record
    says book value (the code defines one as the other);
  - `TODO(docs)` left at `sectors/Construction.java` L429,
    `sectors/FoodProcessing.java` L156, `Game.java` L6481,
    `BuildingManager.java` L3285;
  - `the-crew-a-building-can-use.md` §3 names its first two versions two ways.
  Full list in `the-manual-at-0-7-23.md` §4.
- **FLAGGED BY 0.7.24'S DOCS PASS, 2026-10-01** (`runs/ui5-docs-pass.md`,
  "Flags"), none changed:
  - FALLS DUE prints everything due within three months beside the soonest
    maturity's months: $1.0M next month and $4.0M in three read "$5.0M in 1
    mo". The fix is wording ("$5.0M within 3 mo, the soonest in 1") or the
    soonest's own principal; it is behaviour, so left;
  - Settings' Esc line is 72 characters on a 560 px row, about 37 px to
    spare, measured and not seen; if it cuts, a second line;
  - "you will be offered a bill" on the 0.7.21 card's quote line, the need
    card's quote line and the suggestion card's "Order on credit" tooltip:
    the short-of-cash page offers a 20-year bond first, so "offered a loan"
    would be true (older than 0.7.24);
  - `BuildAdvice.categoryOf(BuildingType)` and `categoryOf(CityNeeds.Go)` have
    no caller; `ServicesScreen.buildLink()`'s `types` parameter is unused;
    `BuildScreen.unitWords()` is reached only for care since the fix round;
    `Pieces.ring()` draws its own copy of `Icons.TICK`'s path;
  - older than the batch: BuildScreen's THE HALF OF THE CATALOGUE THAT BUILDS
    ITSELF says six types where `investorTypes()` lists twelve; README's
    "about 230 files" is 236, and "about three minutes" is about five and a
    half in the cloud.
  - Closed: `BuildAdvice`'s header points at "the project's design note for
    0.7.24", which is `the-build-screen-and-the-frame.md`.
- **FLAGGED BY 0.7.25'S DOCS PASS AND IMPLEMENTER, 2026-10-01**
  (`runs/ui6-docs-pass.md` flags 6–14, `runs/ui6-notes.md` §8.4–8.5), none
  changed; the two bugs and the wording are under section 3:
  - code with no reader: `Pieces.TILE_HEIGHT` (its javadoc now says so; the
    land office's plot was never that height); `BuildScreen.careSubtitle()`
    and `schoolSubtitle()`, no caller since 0.7.24 (Jerus's words, kept for a
    city page's rings: his call); `unitWords()`' seven non-care branches;
    `BuildCardCheck.bits()`, defined and never called;
    `BuildAdvice.categoryOf(CityNeeds.Go)` and `ServicesScreen.buildLink()`'s
    `types`, carried from 0.7.24 (`categoryOf(BuildingType)` has a caller
    now, `BuildCard`);
  - `docs/harnesses.md` counts 43 labels for BuildCardCheck against 44
    assertions: Maps cannot read the one labelled by a `String.format` (L531);
  - GameVersion's 0.7.25 entry gives bar 1 as months of value added for the
    makers, farms and vehicle plants, and does not say an office's is months
    of its exports (incomplete, not false);
  - the tooltip on a bank branch says "Click for Retail's Investors page",
    which is right (retail's money, D17), though the word is the bank's;
  - BuildScreen's class header has a ragged line ("building. The shell still
    reads"), true;
  - older than the batch: README's "in about three minutes" is about six and
    a half in the cloud (its "about 230 files" is "about 240" since this docs
    pass); THE HALF OF THE CATALOGUE THAT BUILDS ITSELF's six types against
    `investorTypes()`' twelve, carried from 0.7.24.
  - Closed: `BuildCard`'s WHY and BuildScreen's ONE CARD FOR ALL 73 point at
    "the project's design note for 0.7.25", which is
    `one-card-for-every-building.md`.
- **FLAGGED BY 0.7.26'S DOCS PASS AND IMPLEMENTER, 2026-10-01**
  (`runs/ui7-docs-pass.md` flags 3, 4 and 7–10, `runs/ui7-notes.md`
  §8.4–8.6), none changed; the wording is under section 3:
  - **ReadPathCheck** "calls every read path the UI and the treasury use",
    but not the reads 0.7.26 added or newly uses:
    `LandMarket.goingUsdPerSqFt()` and `scarcityMultiplier()`,
    `CityNeeds.ground()`, `Game.getLandBlockedSectors()` and
    `minesCommitted()`, `ForeignAccounts.getLandUsdLifetime()` and
    `getLandUsdFromVaultLifetime()`, `NationalAccounts.getLandSales()`. By
    inspection each is a pure read; adding them is a harness change, so left.
  - **Dates not checked:** `ON_TOP_INFO` and `WAITING_INFO` are "the 0.7.6
    note word for word"; no snapshot older than 0.7.6 has the screen.
  - **`UserInterface.tabFor()`'s javadoc** says the debt-issuance flow is not
    on the rail; `showDebtResultMenu` lights Finances, and since 0.7.26 the
    land office's funding pages light Land (older than the batch).
  - code with no reader: `SummaryScreen.refreshCityPanel()`'s `land` local,
    since the alert moved to `CityNeeds.ground()`; and no screen reads
    `LandManager.getLandSalesThisMonth()` / `getLandPurchasesThisMonth()` now
    (only the month, MoneyAudit and the history, where the cleared flow is
    right).
  - Stale's soft finding at `LandMarket.java:628`, "rollIron()", older than
    the batch.
  - Closed by the docs pass: UserInterface's WHERE THE SCREENS WENT
    (LandScreen holds neither of its old banners) and HistoryScreen's GOODS
    ROW (THE STATEMENT is no longer LandScreen's).
- **FLAGGED BY 0.7.27'S DOCS PASS AND IMPLEMENTER, 2026-10-01**
  (`runs/ui8-docs-pass.md` flags 5, 7, 8, 11 and 12, `runs/ui8-notes.md`
  §9.3), none changed; the wording is under section 3:
  - **`UserInterface.showSectorReport()` has no caller since 0.7.27**
    (Household Cash Flow, the last, became a page of its own), so it, its
    reveal branch reading `peopleScreen.revealTop` / `revealBottom` and its
    "Fifteen screens come through here" are dead. Its javadoc now says so;
    removing it is a code change for a cleanup.
  - **The bridge's "· ¾" and "· ¼"**, its tooltips' "three parts in four" /
    "one part in four" and `WHY_INFO`'s "three parts to the homes' one" are
    literals, true while `Migration.JOB_WEIGHT` is .75 and `HOME_WEIGHT` .25
    (as 0.7.26's "nine" is while `LISTING_SIZE` is 9).
  - **The retired rows' statement is struck at the unskilled tier**
    (`statementFor(families, shape, PayTier.values()[0])` in `otherRows()`
    and `rowLeft()`), so their fees carry the unskilled tier's interest and
    account fee per household. Older than the batch; D4 kept the figure; not
    traced whether it is what Senior and Elder alone's "$0" is made of.
  - **The design studies are cited as the project's** ("the project's
    spec-people-0727.md" in PeopleScreen's header and Pieces' 0.7.27 banner;
    LandScreen cites spec-land-0726.md the same way), but neither is in the
    claude.ai project yet; both are in `runs/`. Put them in the project.
  - older than the batch: THE TIER TABLE, AS A TABLE. sits over the opened
    cell's bar (no tier table has been drawn since before 0.7.26);
    `householdPerFamily`'s javadoc says "the per-tier table";
    `Pieces.Slice`'s javadoc mentions "what it opens into", which it has no
    field for; an empty "Small helpers..." sub-banner at PeopleScreen's foot;
    `PeopleScreen.SHAPE_COL` blank in dials.md.
  - left to keep the merge with 0.7.28 small: `ringWords()`'s "measureCard()
    draws it" (People's care row does too) and `PAGE_WIDE`'s list of pages
    (People and Household money use `widePage()` too), incomplete, not
    false.
  - Closed by the docs pass: WHERE THE SCREENS WENT (PeopleScreen holds two
    of its six old banners), `scrolled()`'s "now wraps the People screen",
    `showSectorReport()`'s javadoc, and DataSave's prose on the migration and
    household arrays (format 30).
- **FLAGGED BY 0.7.28'S DOCS PASS AND IMPLEMENTER, 2026-10-01**
  (`runs/ui9-docs-pass.md` flags 2 and 4–7, `runs/ui9-notes.md` §8.6), none
  changed; the wording is under section 3:
  - **EducationCheck's label** "with no college to leave for, the net movement
    is the leavers' diplomas": university finishers come off the DIPLOMA band
    too. The fixture has neither school, so the assertion is right and the
    label understates its condition; a docs pass does not change labels.
  - **`UtilitiesHandler.billedWaterDraw`'s javadoc** says residents "have no
    cash to pay with, so billing them would be revenue from nowhere";
    households have balance sheets, and whether residents draw "the majority"
    is a city's figure (29% of the water was billed in the 2,400-month city).
    Why households are not billed now that they could pay is `TODO(docs)`
    material for whoever owns the utilities.
  - **`buildings.json`'s `_readme`** says "watts for ELECTRICITY": kilowatts.
    The docs pass does not touch `buildings.json`.
  - **`UserInterface`'s routing comment** "THE UTILITIES' BOOKS AND THE
    BUILDERS' BOOKS ARE BUSINESS" sits over no case of its own; since 0.7.28
    the utilities' books are a Services page and the plants the city's own.
  - **`Pieces.PAGE_WIDE`'s javadoc** lists Build and the land office; People
    and Services use `widePage()` too (carried from 0.7.27). Incomplete, not
    false.
  - Closed by the docs pass: WHERE THE SCREENS WENT (ServicesScreen's nine
    banners, INFRASTRUCTURE moved whole), the console's utilities report in
    kW, TradeScreen's `bandMeter()` borrower, PolicyScreen's fare on
    Infrastructure, four blank dials. Closed by 0.7.28:
    `ServicesScreen.buildLink()` and its unused `types` (0.7.24's and 0.7.25's
    flags), gone with the old pages.
- **FLAGGED BY 0.7.29'S DOCS PASS AND IMPLEMENTER, 2026-10-01**
  (`runs/ui10-docs-pass.md` flags 4–8, `runs/ui10-notes.md` §8.2), none
  changed; the wording is under section 3:
  - **`sectors/Rail.java`'s header** says the railway's road relief "is a
    relief of 70%, not of 100%"; `InfrastructureManager.RAIL_ROAD_RELIEF` is
    .75 and has been since cbs-pristine, and the screen and the manual say
    75%. A one-word fix.
  - **`InfrastructureManager.getEffectiveLoad()`'s javadoc** leaves out the
    cars and the railway, which it counts, and says "every city that exists
    today" has neither transit nor highways; `throughputOf()`'s "Every city
    that exists today has no transit" likewise. The walk now takes the method
    apart, so its javadoc is the one a reader lands on.
  - **InfrastructureCheck's class header** lists "Three things have to hold"
    over ten sections, a summary of its first purpose.
  - **`Pieces.vitalsBar()`' javadoc** counts four, and People's five;
    Infrastructure's frame and Freight's bill strip are five too. Incomplete,
    not false.
  - **The specs are cited as the project's** ("the project's
    spec-infra-0729.md" in InfrastructureScreen, Pieces, SummaryScreen and
    ServicesScreen), as 0.7.27's flag found for People and the Land office;
    none of them is in the claude.ai project, all are in `runs/`. Put them in
    the project, or make the comments say `runs/`.
  - Found and fixed by the implementer: `Rail.rAllowed` was never scaled on a
    redenomination (harmless until it was shown and saved). Closed by the docs
    pass: WHERE THE SCREENS WENT (InfrastructureScreen redrawn, five banners),
    `INFRA_PAGES`' sentence, TradeScreen's `bandMeter()` (no borrower since
    0.7.29).
- **FLAGGED BY 0.7.30'S DOCS PASS AND IMPLEMENTER, 2026-10-01**
  (`runs/ui11-docs-pass.md` flags 4, 6 and 7), none changed; the wording is
  under section 3:
  - **The study is cited as the project's** ("the project's
    spec-sectors-0730.md" in SectorScreen's header, Sector's NOTHING STANDING,
    SectorFlow's WHY, BuildCard's two banners and Pieces' banner); it is in
    `runs/`. With 0.7.29's.
  - ~~**WHERE THE SCREENS WENT and 0.7.31:** the docs pass's SectorScreen
    sentence sits where 0.7.31's GovernmentScreen sentence would go; the merge
    keeps both.~~ — closed by 0.7.31's docs pass: GovernmentScreen's sentence
    follows the Sectors one.
  - **Comments loosely true, left:** `Game.printSectorInfo()`'s "the same
    lines the screen draws" (the fold and the grid draw them; the flow is
    SectorFlow's); `investorsLine()`'s and BuildCard's "under every page's
    strip" (the strip of five figures; the page chips are under the line);
    Pieces' FACT GRID banner names `ownLines()` only (Cash & debt's credit
    lines use it too); FinancesScreen's "the same shape as the Sector
    economy"; Icons' "the mills' ingot" (the steelworks'); HistoryCheck 2b's
    "the sector list's ... head count".
  - Closed by the docs pass: WHERE THE SCREENS WENT (SectorScreen redrawn,
    eight banners), BuildScreen's "What it takes to get a yes", CLAUDE.md's
    tree naming SectorFlow, the batch's blank dials.
- **FLAGGED BY 0.7.31'S DOCS PASS AND IMPLEMENTER, 2026-10-01**
  (`runs/ui12-docs-pass.md` flags 9–12), none changed; the wording is under
  section 3:
  - **The study is cited as the project's** ("the project's
    spec-government-0731.md" in GovernmentScreen's header, Game's FROM EARNED
    TO THE BUDGET and Pieces' banner); it is in `runs/`. With 0.7.29's and
    0.7.30's.
  - **"the probe"** in `kpis()`'s and `debtLines()`'s javadocs ("pure: the
    probe reads them") is the implementer's scratch `ProbeGovWords`, not in the
    tree.
  - **Comments loosely true, left:** GameVersion's 0.7.31 entry, the bridge's
    steps "each a door to where it is decided" ("Not accounted for" and a
    journal line `journalDoor()` does not know have none); `journalDoor()`'s
    javadoc lists six of its nine places; THE OVERVIEW banner's "when the city
    owes it or has ever printed" leaves out arrears (which need advances
    first); `DEFICIT_INFO`'s "That gap is borrowed" (it may be paid from cash;
    the old P1); `realGrowthWords()`' "in its words" (the tile's first-year
    words differ from the card's); `COUPON_INFO`'s chip "not struck yet" in a
    month a bond was issued or retired part-way.
  - **ReadPathCheck's sweep**, "whose entire premise is that nothing in it
    changes anything", still calls the striking `getTaxIncome()` and
    `getTotalIncome()`: idempotent between presses at fixed dials, as before
    0.7.31; B8 made `getIncome()` not strike, not those two.
  - Closed by the docs pass: WHERE THE SCREENS WENT (GovernmentScreen redrawn,
    six banners; 0.7.30's flag, above), Game's FROM EARNED TO THE BUDGET (ten
    budget lines EARNED leaves out, not eleven; `getTaxIncomeNow()`), the
    repairs comments in Game and TreasuryJournal, TreasuryJournal's first
    sentence, NationalAccounts' `HISTORY_MONTHS` sentence and `seedHistory()`'s
    readers, UserInterface's THE MONEY BLOCK and `moneyBlock()`, SectorScreen's
    `budgetLine()` reference (Stale's one new unresolved member),
    GovernmentScreen's verdict paragraph, `drawn()`, THE TWO LISTS and the one
    bar's comment.
- **FLAGGED BY 0.7.32'S TO 0.7.36'S DOCS PASSES AND IMPLEMENTERS, 2026-10-01
  AND 02** (`runs/ui13-docs-pass.md` flags 9–12; `runs/ui14-docs-pass.md` flags
  11–13; `runs/ui15-docs-pass.md` flags 5 and 7; `runs/ui16-docs-pass.md` flags
  6, 7 and 11; `runs/ui17-docs-pass.md` flag 9), none changed; the wording is
  under section 3:
  - **The studies are cited as the project's:** "the project's
    spec-finances-0732.md" (FinancesScreen's header, Pieces' banner), "the
    project's spec-bank-0733.md" (BankScreen's header, Pieces' banner, Game's
    load path, BusinessDebtManager's QuoteParts, BankCheck §13b, Money's banner
    by its B-numbers), "the project's spec-policy-0735.md" (PolicyScreen's
    header, PolicyPreviewCheck, GameVersion's entry; PolicyPreview's WHY says
    "runs/spec-policy-0735.md"). All are in `runs/`, none in the claude.ai
    project. With 0.7.29's to 0.7.31's.
  - **"the probe" and "a probe"** in the pure methods' javadocs (Finances'
    `kpis()` and `areaWords()`, the Bank's pure methods, `Pieces.Press.words()`
    and `orderPress()`, Trade's `kpiCells()`, `GoodRow`, `Gauge` and `Moved`,
    Policy's "(pure: the probe reads them)") are the implementers' scratch
    probes, `ProbeFinWords`, `ProbeBankWords`, `ProbeButtons`, `ProbeTrade` and
    `ProbePolicy`, not in the tree.
  - **Comments loosely true, left:** Finances' alert bands "on every page"
    (Issued and Default abroad draw their own frame), `ladderChart()`'s "a
    quarter of the heaviest" (the tallest), WHERE THE SCREENS WENT's "YOUR
    RATE, TAKEN APART" reading as two titles, `Rollover.words()`' "for the log
    and the Finances tab", TreasuryFund's "Fund page", UserInterface's "banner
    sections ... named in its own header", and CLAUDE.md's DecisionLog line,
    "the History chart's flags" (Finances' and the Bank's carry, or are meant
    to carry, theirs); GameVersion's 0.7.32 entry leaving out `YEAR_WALL` and
    the scar's words; the Bank's LENDING banner's "the landlords' mortgages
    among the businesses since 0.7.11", `statusStrip()`'s "until 0.7.33",
    `BANK_HOME`'s "lit until the player picks another" and UserInterface
    `deal()`'s "at the top of the landing"; LandScreen's plot tooltips "Buy —
    borrow for it" and "Buy — the vault is short", Sectors' LIST_INFO and THE
    LIST AS CARDS (true once a month has run), BankScreen's preferred offer "as
    the land office's cards side by side" (the land office's button is pink
    now), Pieces' "as wide as its card" (the credit page's is 560 px), and
    `fundingOffer()`'s and `offerCard()`'s "is to take this in Build's own
    pass"; Pieces' "next readers" naming Trade (`bandBar()`, `statusBanner()`,
    the split ring), which 0.7.35 drew with other pieces.
  - **A file outside the write areas:** 0.7.36's docs pass redirected a sorted
    list of 71 harness names to `/tmp/x` (1,023 bytes) by a slip, and left it
    there; it can go.
- **FLAGGED BY 0.7.37'S DOCS PASS AND 0.7.38'S IMPLEMENTER, 2026-10-02**
  (`runs/ui18-docs-pass.md` flags 9 and 11; `runs/ui19-notes.md` §4 decision
  17, §8.6), none changed; the wording is under section 3:
  - **code with no reader:** `Pieces.bandMeter()`, no caller since 0.7.35
    (`runs/ui16-notes.md` §8.8); not on 0.7.38's list, so it stays.
  - **The study is cited as the project's:** "the project's
    spec-history-0736.md" (HistoryScreen's banners, Pieces' banner,
    GameVersion's entry); it is in `runs/`. With 0.7.29's to 0.7.36's. "The
    research's 2,400-month city" in YearBook's banner is scratch-research's,
    and "the probe" in HistoryScreen's "Pure" javadocs is the implementer's
    scratch `ProbeHistory37`.
  - **HistoryScreen's class header**, "nothing else in the shell reads it but
    the rail", is the 2026-09-18 reason it was split first; the window has
    read `historyValues()` for the tiles since 0.7.21 and `openOn()` since
    0.7.37. Left as the split's why.
  - **For 0.7.38's docs pass:** the implementer left TimeChart's class javadoc
    ("or, small, the same lines and years without the controls") and
    HistoryScreen's TRACES without a new comment, to keep the merge clean
    while 0.7.37's docs pass ran.
  - Closed by 0.7.37's docs pass: HistoryScreen's "of 147" (148),
    `shownIndices()`, `pickChip()`, SEEDED ONCE, `shownBefore` and two blank
    dials; `Icons.ofEpisode()`'s "the heart"; YearBook's WHAT IS RUNNING NOW
    (flag 10); UserInterface's `openHistory()` and "the Reports tab";
    DecisionLog's "nowhere else".
- **FLAGGED BY 0.7.39'S DOCS PASS, 2026-10-02** (`runs/ui20-docs-pass.md`
  flags 3, 5, 6, 7, 9 and 11), none changed but flag 7, closed by its fixes;
  flags 1, 2, 4 and 10 are under section 3, THE CITY'S FUND, flag 8 is the
  CONFIRM list's 0.7.39 entry and flag 12 the CHECK's:
  - **The playtest report's ledger line** (flag 3) reads "1593 bond lot(s)
    repaid and 8196 written down": `ledgerWrittenDown` counts WRITTEN_DOWN
    rows, one a lot a month (twelve for #135 Mining's year), not lots, and
    "lot-month(s) traded by the rule" counts BUY and SELL rows, so a lot
    bought and sold in one month is two. A harness's output line, so left.
  - **`ACTIVITY_ROWS`' and `WRITE_DOWN_ROW`'s sentences** (flag 5): the first
    is the study's shadow-ledger count ("0-5 a month ... some fifteen years
    of a quiet fund and two of a busy one"), where the shipped rows measured
    about 18 a month in the default playtest (about 4½ years kept; CONFIRM's
    `ACTIVITY_ROWS`); the second's "the default playtest, some thirteen a
    month" is nothing the report counts. Jerus's dials.
  - **Code with no reader** (flag 6): five Game getters lost their last
    caller but ReadPathCheck with the old Holdings page,
    `fundCompanyValue()`, `fundCompanyRescueValue()`, `fundCompanyShare()`,
    `fundBondValue()` and `fundBondsValueOf()` (`fundCompanyMarketShare()`
    had none in 0.7.38 either). Remove in a later batch.
  - ~~**FundLedgerCheck's header item 5 promised an older save's rescue lot
    seeded from the counters, and no label asserted it** (flag 7;
    SaveFileCheck's fixture has the preferred but no rescue)~~ — **done after
    the docs pass**: §5's 13 assertions on a save with a rescue book loaded
    with its ledger taken out, the lot at `getRescueCost()` exactly,
    untagged, dated from the rescue, the tracked lot's twin. No bug.
  - **FundCheck §10's printed title** (flag 9), "the hand: its orders at fair
    value, and pay-in and draw-out off the surplus", heads labels about a
    named price and a cancel; a harness's output line, so left (its header
    item 10 was fixed by the docs pass).
  - **Carried** (flag 11): README's "in about three minutes" and CLAUDE.md's
    "(about two minutes)" for the suite (299 s at the docs pass, 313 s after
    the fixes, one JVM each), with 0.7.24's and 0.7.25's.
- **Do not move the harnesses to their own package yet**: about twenty
  package-private model members at seventy-odd call sites would need a seam.
- **Sixteen classes no harness names** (`docs/harnesses.md`, as of
  2026-09-18; eleven when written): `Automotive` and `LuxuryRetail` are the two
  a check ought to name; four are the mechanics moved out of `Game`, reached
  through it; the rest are reached through others or are plain data.

- ~~The eight stubs from the sector template~~ gone, 2026-09-11 midday, with
  `MenuManager.java`.
- Two lossy compound assignments in `Game.java` (`materialsConsumed`). *The
  handler's copy of the count is no longer zeroed on the second
  `updateServices()` pass, so the construction screen's "Used" line reads the
  month's figure instead of 0 — found on the way through `calculateExpenses()`.*
- Aggregation helpers ignore `instances` — latent until `addInstance()` is used.
- `LandMarket.rollIron()` comment says "a century or two"; it is 50–200 years.
- **Not persisted across save/load:** ~~the 12-month GDP history (annual GDP
  and growth read short for a year after a load)~~ (restored on load since
  0.7.31: `NationalAccounts.seedHistory()`, held by SaveFileCheck). *The
  solvency record, the placement residual, the doors let, and the bank's last
  closed month were all carried on
  2026-09-10; that night, the borrower's default record, the economy's traded
  exchange rate, the land office's prices, the world's level ring and the labour
  market's diagnostics joined them, and later the same night the shops' last
  month's sales, the food market's traded price and construction's struck
  month — and `LongPlaytest.roundTrip()` now compares every one, which it had
  not. The template carries each sector whole (`SectorState`), every market's
  price and its three-year take, and every site's material owed and contract.*
  *And since 2026-09-15 every band-indexed array is carried BY NAME — one
  `bandNames` field describing the whole file, read by the pyramid, the families
  and the ring of the long sick alike — so their width comes from the save rather
  than from the reading build, with `shapeNames` beside it doing the same for
  the household matrix. See the top entries.*
  ~~**Seen 2026-10-01:** the header's GDP tile reads "$0 / yr · first year"
  right after a load, and is scaled up from the months recorded for a year
  after (`NationalAccounts.restore()`'s javadoc says the history is not
  saved; `runs/ui5-notes.md` §8.2, and on his PC in the 0.7.23 walkthrough).~~
  **Closed by 0.7.31** (the Government spec's B1, `earned-surplus-banked.md`
  §4).
  **And `Game.lastInvestment` is not saved:** after a load every investors'
  word, on the Investors page and on 0.7.25's investors' line, reads
  "nothing recorded" until a month runs (`runs/card-spec-0725.md` §9.3;
  saving it is a save-format change, Jerus's, section 0).
  **Found by 0.7.25:** `LuxuryRetail.getWanted()` and `Restaurants.getWanted()`
  are month flows the save does not carry, and read 0 right after a load. The
  build card's notes wait a month (D11); any other screen that prints them
  right after a load shows 0 (`runs/ui6-notes.md` §8.2). And Rail's trade
  tonnes jump across a load (154,577 t right after loading month 2,400,
  2,695 t a month later, §8.3): the railway bills almost nothing in its
  first month after every load, traced by the Infrastructure spec (section
  0, MODEL BUGS FOUND BY THE SCREEN RESEARCH, with the GDP history's cause
  (fixed in 0.7.31), Trade's month flows and the Bank's sector surcharges
  after a load (fixed in 0.7.33)).
  **Carried since 0.7.27 (SAVE_FORMAT 30):** Migration's last month, the
  cohorts' dead by cause, and the shops' delivered share with the hungry at
  full shelves, so People's month, Why people come and GOING SHORT read the
  same after a load (`people-at-a-glance.md` §5).
- `SteelCheck.java`, `SolvencyCheck.java`, `PlaytestRun.java`, `MillCount.java`,
  `RealismCheck.java`, `JerusSave.java`, `HouseProbe.java` and `BankProbe2.java`
  exist in the working set but are not in the NetBeans source folder. Harmless —
  they are probes, not game code. **`RealismCheck` is the rebalance's instrument
  and `BankProbe2` is the bank's; either is the thing to run first if any of
  these numbers are ever questioned.** *`LongPlaytest` now prints credit by
  sector at the end, which is most of what `BankProbe2` was for — and since the
  template it has a monthly window on any sector (`-Dplaytest.sector`,
  `-Dplaytest.bankfrom`, `-Dplaytest.bankto`) and a yearly bank line
  (`-Dplaytest.bank`).* *`FoundingProbe.java` joined them 2026-09-15 — the
  founding-currency instrument, `-Dprobe.heavy="Coal Power Plant:2,House:200"`
  and `-Dprobe.months`.* *And `DenomProbe`/`OrphanProbe` the same week — the
  month-by-month divergence instrument that found the exchange's yield tie and
  the elders' missing pension. Neither is in the tree; both are worth rewriting
  rather than keeping, because what they measure changes each time.* *`FarmProbe`
  and `DenomLuck` joined and left the same way on 2026-09-16: the first compares
  ~650 quantities a month across a city and its reformed twin and prints the
  largest gaps, the second runs that comparison over ten foundings so a red
  harness can be told from a coin toss. **The second is the one worth rebuilding
  first next time** — it is what proved the farms batch innocent.*
- **`sendBuildingSave()` loops `i < getTemplateCount()` and looks up by id.** A
  trap, not a bug, while the ids stay dense.
- **Harness hygiene, decided:** every harness `Game` goes through
  `GameFiles.scratch()`. `AllChecks` runs all **49** harnesses plus the
  playtest — **50** in one command, **~94 seconds** (`EquityCheck` joined
  2026-09-10 night, `ExchangeCheck` 2026-09-11,
  `OutsideCheck`, `SicknessCheck`, `HouseholdMemoryCheck`, `DeathRecordCheck`
  and `CrimeCheck` 2026-09-11 night, `BusinessServicesCheck` 2026-09-12,
  `ManufacturingCheck` and `AgricultureCheck` 2026-09-13, `YearBookCheck`
  2026-09-14 with 277 assertions — 286, then **294**, on 2026-09-15)
  — `BuildMenuCheck` included since 2026-09-10 (evening), when the cached
  JavaFX 21 jars from `.m2\repository\org\openjfx` were staged into the cloud
  loop. **The whole tree compiles there now, `UserInterface` included; no UI
  change goes to the PC uncompiled again.** *All forty were migrated to the
  template's API on 2026-09-11 — the old handlers had a hundred and ninety
  call sites in them — and all forty pass on a mirror of the PC's tree.* *No
  harness was added on 2026-09-15; `PopulationCheck` gained **eleven** blocks
  across the two by-name passes — six for the pyramid, three for the families,
  two for the ring — and the split that followed added a tie block to
  `ExchangeCheck` and a pensioner block to `HouseholdCheck`.* *None on
  2026-09-16 either; `LongPlaytest` gained the negative-position flag, which is
  a guard on every seed rather than a fixture.*
- **`LongPlaytest` has a cell trace.** `-Dplaytest.cells=true` prints, yearly,
  every household cell that is short, cut off, locked out or in debt. It is how
  the pensioner alone was found; use it before believing a household number.
- **Half the tree is CRLF and half is LF.** Ten of the fifteen files in the audit
  batch were CRLF; any tool that normalises on read writes them back as
  whole-file diffs. Check `grep -c $'\r'` before a copy-back. A `.gitattributes`
  would end this. *Bitten once more on the materials pass: a text-mode edit of
  `HouseholdCheck` stripped 811 carriage returns and was caught by the count
  before the copy. Binary edits only, for CRLF files. The template's deploy
  re-ended every changed file to whatever its PC copy was — 44 CRLF, 16 LF —
  and the fifteen new files are LF.*
- **`LongPlaytest` has an ensemble mode.** `-Dplaytest.seed=N` (0–35) nudges the
  founding order; seed 0 is the run as it always was. One run before and one
  after measures the change plus the weather; eight seeds a side is the least
  that separates them, and it is fifteen seconds each. **Use it for anything
  that touches the whole economy.** *Seeds 4–7 land within a few people of
  each other on the template; the seed nudges the founding order and those
  four converge. Worth widening the seed's reach.*
- **A FIXTURE HAS TO CAUSE THE CONDITION UNDER TEST, NOT STAND NEXT TO IT.**
  **Forty-eight sightings.** It is a rule, not an observation. *And since
  2026-09-14 the README states its corollary as a fourth rule in its own right:
  **never move a harness's premise to let a change through.*** *The
  forty-seventh and forty-eighth came with the senior split, 2026-09-15, and
  both are variant (b) — the model got better and the fixture broke.
  `ForeignCheck`'s devaluation premise had never actually been fixed: it was the
  fixture's plant and roads PLUS however much private housing each city wanted,
  and the two cities grow at different speeds. It read 1.0063 for as long as the
  two household counts happened to track each other; the split pulled them apart
  and the elders' pension pulled them further. Real Estate is held out now, the
  eleventh sector to sit that fixture out, and the instrument came back SHARPER.
  And `HouseholdCheck`'s pension block had never asked whether a pensioner in
  one band draws what a pensioner in another draws, because there was only one
  band.* *The
  forty-sixth, 2026-09-15, failed on its own first run and was right to:
  `YearBookCheck`'s new "the book agrees with the model" fixture stood on the
  default founding, where after sixty months **nobody is studying** — so
  `workforce` and the labour force are the same number and the broken
  unemployment formula it was written to catch would have passed. It funds a
  city and builds a community college now, and asserts that the replaced
  formula actually disagrees with the model there.* *Forty-three to
  forty-five came with the tenth, and the first is the best example of the rule
  this project has: `SaveFileCheck` had two conditions FIGHTING EACH OTHER. Its
  squeeze - income tax at the ceiling with a punitive offset on every band -
  exists to cause hunger and does; it also makes every sector too poor to
  borrow, and a bank with no borrowers earns nothing and pays its savers
  nothing. "Somebody is hungry" and "savers are being paid" were being asked of
  the same city at the same moment, and one is the other's opposite. It held
  only while the mills made food out of nothing, because a sector with no input
  bill stays rich through any squeeze; give them a crop to buy and the bank had
  failed once, sat on $5k of equity against a $3.1m book, and had paid nothing
  for two hundred months. The fix was the ORDER - run the city healthy until
  the bank is paying, and only then squeeze it. (It also got four Mixed Farms,
  because "one of everything" stopped being true the day the mills started
  buying crops.) `ForeignCheck`'s devaluation section went for the second time
  in a day, one sentence further along the same chain. And `ManufacturingCheck`
  asserted `SAVE_FORMAT == 23` - a tripwire written that morning that went red
  the same afternoon, a harness about fabricated steel pinning a global
  constant it does not own.* *Thirty-nine to
  forty-two came with the ninth sector, and all four are the same shape — a
  sector that buys something walked into fixtures that had never had one:
  `MiningCheck` measures what a mine is worth to a mill and a fabricator took
  steel from $847 to $1,240 in both its cities; `ForeignCheck`'s devaluation
  section rests on the import side being a FIXED programme, and Manufacturing
  buys steel in proportion to how big its city got; `HousingCheck` wanted
  households doubled up before checking the crowding figures survive a save, and
  a sector that employs people grows the city into its own housing;
  `SaveFileCheck` wanted food in the mills' warehouse, which its own note two
  lines above had already retired one shelf along. The first of those produced a
  lever — `BusinessInvestment.holdSector()`, harnesses only — because it is the
  second time a new sector has walked into somebody else's fixture. *And the
  third and fourth times were the same fixture again: `ForeignCheck` has held
  Manufacturing out since the ninth, Agriculture since the tenth, and Real
  Estate since 2026-09-15.** *The
  thirty-eighth, variant (a) from a new mechanism: `OutsideCheck`'s college town
  built no police, and once crime existed the thefts handed its students enough
  that nobody took a student loan in fifteen years — in the fixture that exists
  to test the loans. It builds a station now.* *The
  thirty-seventh: `HealthCheck` asserted the founding endowment was sized off
  the pyramid by checking senior care came out smaller than childcare - the
  pyramid's shape, not the rule - and halving adult mortality let the seniors
  pass the children. It asserts the rule now. `SicknessCheck`'s first "city
  with no clinics" was 97% covered by the founding doctor.* *The
  thirty-sixth is the weak-threshold variant: `OutsideCheck`'s "graduates have
  repaid some of them" passed at **1 repaid of 7,530 lent**, because no loan
  ever left a college that does not change size. It now asserts that the
  students the census saw finish left with their loans, every month, to the
  person. A threshold of zero is not a claim.* *Thirty to
  thirty-five came with the sector template: `SaveFileCheck` stopped in a month
  its subsidy dial happened to pay, and the sector that paid it stopped losing
  money when the builders stopped buying an order's material on day one;
  `ForeignDebtCheck` borrowed "twice the treasury" and read the spread that
  happened to make against a GDP the accounts no longer inflate — it borrows
  until its own paper is dearer now; `ForeignCheck` asserted a devaluation
  improves the trade balance in dollars, and the balance is nine tenths a
  materials boom's timing now — measured and printed, the one claim the fixture
  can carry asserted; `ConservationCheck` built three mines over no ore and
  wanted them to have a payroll; `GdpCheck` asserted the work-in-hand term;
  `BankCheck`'s savers' rules assumed idle capital earned nothing. And a
  variant worth its own line: `DenominationCheck` passed for two days while the
  resolution floor was in the wrong unit, because no bank in its fixture had
  ever earned its way out of resolution — the bug was in a branch nothing
  opened until the vault earned interest.* *Twenty-four to
  twenty-nine came from the materials pass, and four of them were one city:
  `BankCheck` (three fixtures) and `SaveFileCheck` all queued the same list
  behind a power plant with no roads and no food, starved at 50–75%, and passed
  only while the landlord's default was small. They stand on month one now and
  the book is caused. `ForeignCheck` measured a devaluation in the city's money
  when a price-taker can only move volumes; `HouseholdCheck` asserted flatshares
  "however they got there"; `InboxCheck`'s empty city had nothing to say. See
  `the-unit-of-material.md` §4.* *The twentieth
  to twenty-third came in one night from one rebalance: `ConservationCheck`
  measured a warehouse that no longer existed, `InfrastructureCheck` called a
  city congested that ran at 73%, `BooksCheck` asked for a revenue that stopped
  being true when the mill started exporting, and `BankCheck` compared this
  month's tax with next month's profit and passed while they happened to be
  equal. A fixture that opens with "3 stores" is a fixture that opens with a
  number; each now derives its count from the template.*

  The original six: `LabourCheck` read whatever the 25th month held;
  `SaveFileCheck` stopped in the exact month its bank opened; `BankCheck`,
  `InfrastructureCheck`, `InboxCheck` and `ForeignCheck` all broke on the
  founding-parameter change.

  The rebalance added eleven more, and two variants worth naming:

  **(a) The model now FIXES the problem the fixture was built to show.** When
  this happens the fixture should assert the better outcome, not be bent until a
  fixed bug reappears.

  **(b) A fixture can break because the model got better and start working again
  because a price moved somewhere else entirely.** `HousingCheck`'s studio city
  did both in one night, in opposite directions.

  **The eighteenth (2026-09-10) is the cleanest example of (b) yet.**
  `ForeignDebtCheck` set the foreign-default clock to 200 months — past the sixty
  the borrowing window closes for — so its "the window is still shut" assertion
  was never testing the scar at all. It passed only because that fixture's city
  happened to export too little to satisfy the debt-to-exports test. The
  industrial fix turned the city into a food exporter, the incidental reason went
  away, and the assertion failed with nothing broken. It now winds the clock
  forward to sell the bond and back to twelve to shut the window, so the reason
  the window is shut is the reason the assertion names.

  **The nineteenth (2026-09-10, late) was decided by a data file.**
  `CapitalFlowCheck`'s "hot money actually arrives in a real city" never set a
  policy rate, and a default city prices its paper a full point under the world,
  so the spread was structurally zero. It passed or failed on whether two months
  of bank-strain premium happened to spike the rate — which depended on a
  power-plant capacity number in `buildings.json`. It sets a 6% dial now and
  asserts the spread opened.

  **And a third variant, found the same day: A TEST THAT CHANGES NO NUMBER IS NOT
  A PASSING TEST.** Two versions of `branchWouldPayForItself()` were written and
  both were silently inert — the first asked what a branch is *entitled* to
  carry, the second read flows `startMonth()` had just zeroed. Two consecutive
  4,000-month runs came back **byte-identical**, and nothing but that said so.

  **And a fourth, 2026-09-15: A HARNESS THAT ONLY CHECKS THE ARITHMETIC WILL
  CERTIFY THE WRONG NUMBER.** The year book's first edition shipped 277
  assertions and three wrong columns, because every one of them tested that the
  FOLD was right — a flow sums, a level takes December, a rate averages — and
  none tested that an input meant what its note claimed. **When a harness checks
  a derived figure, one assertion must compare it against the model's own
  definition on a played city**, and its tolerances must come from the storage's
  own precision rather than being picked.

  **And a fifth, the same day: A HARNESS CAN PIN A CONSTANT SO THAT THE NEXT
  CHANGE CANNOT SILENTLY MOVE IT.** `PopulationCheck` asserts that
  `PopulationCohorts.LEGACY_BANDS` is literally five names — not
  `AgeBand.values().length` — because the list describes a file already on
  somebody's disk. The assertion exists to FAIL if anybody ever "tidies" it into
  tracking the enum, which is the exact edit that would re-arm the landmine.

  **And a sixth, 2026-09-15: A HARNESS CAN BE GREEN AND THE MONEY AUDIT CLEAN
  WHILE A WHOLE CLASS OF HOUSEHOLD STARVES.** The elders drew no pension for a
  batch: the bill was right and the same total left the treasury, so nothing
  conserved was violated and every assertion held. **A conservation law says the
  money went somewhere, not that it went to the right somebody.** What found it
  was a probe comparing two builds' trajectories, and what keeps it found is an
  assertion per retired SHAPE rather than per row.

  **And a seventh, 2026-09-16: ONE FIXTURE IS ONE DRAW.** `DenominationCheck`
  went red on the farms batch and the natural reading was that the farms broke
  the currency reform. Ten foundings said otherwise: nine tracked to 1e-14 and
  only the harness's own fixture blew up, on a residue whose sign is luck. **When
  a harness goes red on your change, ask the same question of ten fixtures before
  believing it is your change** — and note that the same instrument found the
  DEPLOYED tree failing the same way on a different fixture, eight orders of
  magnitude inside its band.

  **What actually makes a fixture safe**, learned the hard way: size it from the
  model's own state rather than writing a number down; wait for the condition
  with a bound rather than stopping at a fixed month; when a reading has two
  doors into it, close both; **if an assertion names a cause, make the fixture set
  that cause and assert the cause as well as the effect**; and **run the whole
  cycle the game runs — `BankCheck`'s branch fixture has to open a month and
  CLOSE it, or it asserts nothing.**

## 7. Store, when the game is ready

In order, because two of these are sequential and it catches people out:

1. Assets: capsule art in several fixed sizes, a **trailer**, screenshots, short
   and long descriptions, tags.
2. IARC age-rating questionnaire.
3. Submit **store presence** for review — 3–5 business days, 7 days' notice
   asked for.
4. Only once that is approved can you submit the **build** for review.
5. Coming Soon page live **at least two weeks** before release.
6. SteamPipe depot pointing at `dist\CityBuilderSim`.
7. Click Release App yourself — approved titles do not release themselves.

Optional: `steamworks4j` for achievements and Cloud saves.

### Sharing it before Steam

The unit is the whole `dist\CityBuilderSim` folder (~115 MB, carries its own Java
runtime). Zip it. Expect **SmartScreen** to block an unsigned exe — "More info →
Run anyway" — and put that in a README. itch.io is the right home for a public
build. **Windows only**: jpackage does not cross-compile.

---

## Not doing, and why

- **Syndicating past a large-exposure limit** (0.7.8, round 3, on Jerus's
  choice): Basel's 25% of Tier 1 per counterparty with a sector as one
  connected borrower, the rest of each loan sold abroad at its own rate. 99% of
  business lending went abroad, interest abroad reached 9–14% of GDP, prices
  3.9 times founding, and the bank became an arranger with $16–120M of equity
  that still failed 101 times. Deleted (Jerus, 2026-09-24: *"Drop syndication,
  keep quarterly"*): a sector is many firms, and Basel's rule is for a firm or
  a group tied by control, not an industry. (`a-sector-is-many-firms.md` §4.)
- **A concentration charge with a stress target** (0.7.8, the gate's second
  version): 68 failures rather than 85, but unemployment 21.5% against 11.8%,
  $233bn written off against $167bn and GDP 11% lower. Reverted; `Bank
  .MAX_BUFFER`'s javadoc has the numbers.
- **Keeping the bank's excess instead of returning it** (0.7.8): two seeds
  failed 3 times each rather than 14 and 12, and ended with 78,048 and 127,198
  people against 150,518 and 141,302.
- **Tuning `MAX_ORDER_MONTHS`.** Rejected on measurement: byte-identical to no
  cap on three land seeds of four. *It is a rule for the makers since the
  template — a plant the builders cannot finish inside a year is not ordered —
  because the seventh sector's first plant sat five years in a founding queue
  paying interest.*
- **Putting interest into the housing build hurdle.** Tried twice, both times put
  the hurdle above any rent the city could bear.
- **Bending the rent floor to hit 31% of income.** The floor is the honest
  product of two independently sourced real numbers. Both inputs have a source;
  the share does not.
- **Costing the three roads for real.** They are the one group whose costing is a
  RELATIVE design rather than an absolute one — each has to win a band of land
  prices.
- **Cutting industry's jobs to fix its wage share.** The plants were not
  over-manned; they were under-producing. Cutting the crew would have hit the
  same payroll ratio while making the buildings less real, not more. Output moved
  instead. (2026-09-10) *Cutting them in proportion to a smaller plant is a
  different thing — that keeps the ratio and is the granularity decision.*
- ~~**Tightening the bank's loan-to-assets limit below the insolvency line.**~~
  **Withdrawn 2026-09-10 (late).** The measurement was honest and answered a
  different question: the ceiling and the trigger were one constant, so lowering
  the ceiling lowered the line with it and never tested a gap. With a gap
  (0.9 against 1.5) the bank keeps a book and stops failing. See
  `the-lender-gets-a-gap.md` §1.
- **Holding the investment desk to the shortfall desk's ceiling.** Measured:
  halves the early city (jobs 1,178 against 1,922 at month 266) and doubles the
  worst unemployment. A desk that funds every first expansion cannot be held to
  a rescue desk's limit. (2026-09-10)
- **A large-exposure limit on the bank** — no borrower past the bank's own
  equity, from either desk. Measured (2026-09-11): the founding bank can lend
  nothing it could not lose, so it earns nothing, drains, and lends less; the
  city ends at 3,400 people and $15,000tn of debt at 42%. Seven borrowers
  cannot be held to a regulator's quarter-of-capital; the bank grows into its
  town by lending. The concentration problem is real and the answer is the
  owners' equity in the plant, which is where the template put it. *(Tried
  again as syndication in 0.7.8's round 3, and deleted — above.)*
- **Raising `MAX_SPREAD` to price risk.** Measured at 12%: the bank got poorer,
  because the borrower paid the extra interest by borrowing it from the same
  bank. (2026-09-10) *(The spread is the curve's since 0.7.8, uncapped; under
  the shortfall desk's ceiling it charges at most 0.83 points, so the 12%
  measurement does not bite — `a-sector-is-many-firms.md` §3.)*
- **Changing `TREND_INFLATION` to fix the currency.** That reintroduces the
  2.4-million-fold price level `WorldEconomy` exists to prevent. The fix was to
  measure inflation from the level, not to change the level. (2026-09-10)
- **Liquidating a sector on six months of losses.** Measured: it killed every
  founding plant the advisor built, at month 9. The fuse on a working plant is
  two years. (2026-09-10)
- **Pricing food exports off the local shelf.** At 0.9 of the local price the
  food industry ended a run holding $72bn; the world pays its own price less a
  wedge, not the city's. (2026-09-10)
- **Counting a banned sector's write-downs.** Every month of a ban read as a
  new default and the escalating exclusion reached 525 months. One episode, one
  default. (2026-09-10)
- **A materials plant that fills its shed and idles.** It does — the shed is
  the business for a good drawn on order — but a plant that only idled with a
  full payroll was scrapped in six months; it exports the spare line at the
  floor now, as every maker does. (2026-09-11)
- **Correcting the Reports chart's unemployment line in the chart.** It would
  have left two copies of the definition that happen to agree today, which is
  how the first copy came to disagree with the model in the first place. The
  chart delegates to `YearBook` instead. (2026-09-15)
- **Splitting the senior band in the same batch that made the save safe.** The
  reader change is the thing that protects every existing city, and it wanted
  deploying on its own, with the harness written for a sixth band that does not
  exist yet. A band split is a model change and gets its own sixteen seeds.
  **Justified twice over the same night**: the audit that had to run before the
  enum could move found two more save landmines of the same class, both
  prerequisite, and neither would have been looked for inside a split batch.
  (2026-09-15) *And a third time the next day: the split's own batch found three
  more defects, none of them about age bands, and each would have been a
  confound inside a bigger one.*
- **Breaking a tied ranking by index.** The exchange's "best yield first" is a
  tie whenever two companies are valued on their earnings, and the obvious fix —
  order by company number — is deterministic and **biased**: it hands the same
  company every tied month for ever. Buyers who cannot tell two things apart
  spread between them, which is what shipped. (2026-09-15)
- **Widening `ForeignCheck`'s five-percent premise band.** It drifted to 5.58%
  and the temptation was one character. The premise was false instead — private
  housebuilding was inside a "fixed programme" — so the landlord is held out,
  the way Manufacturing and Agriculture already were, and the band never moved.
  (2026-09-15)
- **Shipping the farms and carrying `DenominationCheck` as a known finding.**
  It was one of three options and the harness had been right fifteen times, so
  it was chased instead — and it was not the farms at all but a residue in the
  household rebuild that had been deciding who may hold money abroad for months.
  **A harness that has been right fifteen times is worth one more investigation
  before it is overruled.** (2026-09-16)
- **Snapping the residue where it was read.** The first fix clamped a dust total
  to zero inside `moveStock()`; it fired on nothing and changed no digit, because
  the dust was a single small term rather than a cancellation. Reverted rather
  than shipped with a comment claiming a fix it did not make — the real defect
  was a share above one, two lines up. (2026-09-16)
- **A web version.** Applets are gone and Web Start went with JDK 11.
