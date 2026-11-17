# FB2 — the kept screens' geometry and retention

Four rows (KL1, KS1-UI, BF1-K, KS1-Gov), screens and KeptCheck only: no model, no save, no dial that moves the playtest (the
ensemble's trace is unchanged: nothing a month computes is touched). Built by a Claude session (Sonnet 5.5) on `batch/FB2`, cut
from main `f38eafbc` (0.9.130).

## What 0.9.130 shows

`./batch-check.sh KeptCheck` on the unchanged tree: one red, KS1-Gov's (`the most made in one: 45 of 400`; the judge calls it NEW
because the batch's own cover is taken off). The other nine entries filed under FB2 do not reproduce: each rode a city's path that
has moved since (GW1's, KS1's, HW1's, BF1's), and the Government section's path moves from run to run besides (months advanced by
earlier timing sections: the same tree gave 45 of 400 once and 6 of 347 the next). A screen that is right on one path and wrong on
the next is the finding, so each repair is **caused** by the states that need it in a new section, KeptCheck 103
(`fb2Repairs`; 25 lines after REV-FB2-1), through hooks at the end of `KeptBench` that build the picture alone, tell it the states in turn and hand back
the kept picture beside one built new. Each was run by hand on the pre-fix classes (the same section compiled against
`git show HEAD:` sources of the five screens, overlaid on `target/classes`): **7 lines red, all 20 green after** (then 5 more lines, two of them red by hand on their own: REV-FB2-1). The figures below
are those runs.

## Rows

### KL1 — the buyback ladder compared at the picture's resolution

*Cause.* `FinancesScreen.BuybackLadder.drawsAlike()` compared the quote's ladder and `takenFace` bit for bit, so faces that moved
by 1.4e-8 of themselves (18,935 of 1.3265e12, months 2 and 6 of GW1's city, nothing typed) redrew a picture written to the unit
and a few hundred pixels long. *Change.* The picture reads the quote through one `Reading` (each slice's price and face as written,
its bar and solid part at `PIXEL_QUANTUM` = a quarter pixel, the tooltip, the fair price, the stop and most-to-pay marks); `draw()`
draws only from it and `set()` draws again only when the new quote's `Reading` differs from the one on show. A kept ladder and one
built new, told the same quote, now read the same by construction (the lengths drawn are the snapped ones). `drawsAlike()` is gone.
*Check.* 103's ladder lines, with nothing taken and with part of it taken: a drift of 1.4e-8 keeps the nodes (before: it drew again,
2 FAIL), a hundredth draws again, and each equals a ladder built new.

### KS1-UI — the header's hidden geometry, HW1's Your rate

*Header (entry 6).* `UserInterface.HeadlineTile.layoutChildren()` resized only the wording it showed, so a wording shown last month
and hidden this one kept its box (the RATE tile's `D$1,000,000,000.00 per US$`, 145 x 14 at (11, 44), where a tile built new leaves
0 x 0 at the corner); the same held for the sparkline hidden under `SPARK_MIN`. Both now stand at (0, 0, 0, 0). 103's tile probe
(widths 420, 230, 110, 60, 420, 230, 60, 110 of one tile with three wordings): before, 7 of 8 differ (`420.0x14.0` against `0.0x0.0`,
the finding's own difference); after, 0, the sparkline too (the first repair left it differing at 72 x 16, so the fixture is what found
it). `Headline` and `HeadlineTile` lost their `private` for the bench (package-private).

*HW1's Your rate (entry 37), carried here by the orchestrator (HW1 notes, "Complete fresh dry run and two Finances findings").* The
hidden empty 9 px Label of 0 x 20 against 0 x 13 is `Pieces.SegmentBar`'s tick name: the dial's tick has no name while the rate is
near the dial (`FinancesScreen.realCard`, `near ? null : "the dial ..."`), and a Label emptied after it had words keeps the laid-out
text node of them where one that never had any has none, so even a resize to 0 x 0 left a 13 px box (the probe shows the
`LabeledText` child at 0 x 13 under a 0 x 0 Label). A tick with no name now has no Label on the bar: `set()` puts the bar's children
in the one order (track, parts, marks, the names that have words), set where they differ and never added to. 103's bar probe (the
dial named, left out, named...; six states): before, 3 of 6 differ (`0.0x20.0` against `0.0x13.0`: HW1's line to the digit);
after, 0.

*Wood Products' Residues Text (entry 22): not mended, filed back as KRT1* (orchestrator's call, 2026-11-17). Run here and not found:
`IndustryPane` through 120 months of the 0.9.130 city (KeptCheck's section 50 with its loop lengthened, the kept page against a page
built new each month): 0 differ, 5,595 of 5,783 parts kept; and KS1's own classes (`codex-KS1/target/classes`, 62 parts as it
logged) through section 50: 0 differ. The finding's two readings read as one Text measured twice: Plex Sans 11.5 alone measures
14.95 high (the kept one, `15.0`); the fresh `16.0` is a line a pixel taller, which only a stroke or another font gives, and the
printed `-fx-font` and the CSS properties of both are equal. Nothing in `SectorScreen.text()/run()/halo()` sets a property that
differs between a kept and a new Text, and a standalone probe of 250 transitions (KS1) found none. It needs KS1's month-1,868 state.

### BF1-K — Partners' triangles and Search's empty words

*Partners (entry 29).* `PartnersPage.CurveChart` made each buyer-entry triangle once at fixed points and moved it with `layoutX/Y`;
a node moved by a fractional layout offset is rasterised differently from one made at that place (a stand-alone probe over sixteen
pairs of fractional places: a triangle moved from one to the other against one made there, up to 46 pixels apart; at its own points, 0). The triangles now take
their points in the chart's coordinates (`x-4, plotB, x+4, plotB, x, plotB-6`), set at layout, never moved. 103's chart probe (the
buyers' places moved a fraction of a pixel, five tellings, kept against built new): before 53, 83, 75, 81 pixels differ in the box
of the triangles (the finding's 62, at x355-623); after, 0 in all five.

*Search (entry 28).* Already mended: FP1 (0.9.116, `7cc8df92e`) gave Search's empty price, move and held words scopes of their own,
which is the brief's own remedy ("give absent values their own scope"), and its notes say so. 103's rows probe (price, move and held
words that come and go over four states of three rows, FP1's rows kept in place): with the three scopes taken out of a copy of
`FundScreen`, 3 of 4 states differ (the row's `1035.3` against `1007.0` wide, the finding's class); as shipped, 0. No change to
`FundScreen`; the entry goes with this batch.

### KS1-Gov — the Overview's lines that leave and come back

*Cause.* The Overview's lines come and go with the month: a ring's slice (and its key's row), a bridge's step, a statement's row, the
central bank's card and its arrears. Each is a scope keyed by its words, and a scope not asked for in a draw was let go, so a line
that came back made its nodes again: six for a slice, ten for a step. The month the lines of the 47-of-378, 40-of-384, 46-of-400 and
45-of-400 paths returned together was past the tenth KeptCheck allows. *Change.* `Kept.parkedScope()` (a scope whose nodes wait,
off the page, for the draw that asks for it again; at most `Kept.PARKED_MAX` = 48 of them in a scope), used at `Pieces.splitRing`
and `ringKey` (a slice), `Pieces.bridgeColumn` (a step), `GovernmentScreen.moved` (a statement row), `netted`, the arrears' block,
the "beside" pair and the central bank's card, and the bridge's two chips. A parked row is handed back as any kept node is, so it
equals a row built new; it is off the page while absent (truthful disappearance: the box's children are the draw's own). A line's
first appearance still makes its nodes, once, and no floor moves. A scope parks the rows asked for last (an access-ordered map, the
eldest let go first: REV-FB2-1, below).
*Check.* 103's rows probe (a ring, its key, a bridge's steps and a statement's rows told six states in which lines leave and come
back): the first draw made 56 nodes, the lines' first coming 38, two lines never seen 19; the lines that came back made **0** (before: 38),
the unchanged redraw made none in any state, and the page equals a page built new in all six. On the real page: this tree's full
KeptCheck now reads 34 of 400 on the same path that gave 45 (the rest is first-time lines), and 6 of 347 with 24 of 25 months
making none on a quieter run (the four returns that made 6 each on the same city before are now free).

## REV-FB2-1 (84be0139): two mends

*(1) The cull.* A parked row is keyed by its words, and some words carry a figure (`Game.java:3251`, "Bought land with %s of
reserves": a bridge step and a statement row per vault-paid parcel), so one-offs park rows under keys never asked again, and past
`PARKED_MAX` the scope kept the first 48 in `HashMap` order, not the newest: dead rows could stay and the recurring lines go.
`Scope.parkedKeys` is now an access-ordered `LinkedHashMap` (asking for a key moves it to the end) and `end()` lets go the eldest
beyond `PARKED_MAX`; the keys are not stripped of their figures (a one-off is the first to go, which is all the cull needs).
KeptCheck 103's `parkedCullProbe`: fourteen recurring lines (the bridge's and the ring's, asked every fourth draw) and a "Bought land
with D$12.4M of reserves" a draw, 144 one-offs against 48: the draws that ask for the recurring lines made **149** of them anew on
the `HashMap` cull (red, by hand, on the pre-mend `Kept`: the only FAIL), **0** on the mended one. (A first fixture with eight
`"recurring N"` keys was green on the `HashMap` by luck of their hash buckets; the real lines' names are not.)

*(2) The quantum.* The ladder's drift scaled every face alike, so no bar moved and `PIXEL_QUANTUM`'s snapping was never exercised.
`ladderQuantumProbe`: one holder's face (the second) moved so its bar moves by 0.01 px (the sign chosen to stay in its quantum's cell)
and by 0.5 px, the faces written to a tenth of a trillion so that only the bar tells the quotes apart: the first leaves the nodes
(and equals a ladder built new), the second draws it again (and equals one built new). By hand with `snap()` made the identity, the
only FAIL is the first line (the 0.01 px bar redraws the ladder).

## Entry 38 (HW1's Columns on Borrow at home, 7 of 10): to FB6

Not a screen's, as far as the numbers say (the failing path is not here to run): of its six months `had`/`kept` read `[1+P(t-1)]` and `[1+P(t-1)&P(t)]` where P is the "THE LADDER THIS WOULD BUILD"
card, which `borrow()` draws only while `quote != null && !quote.isEmpty()`; 10 and 7 mean the quote was there in four of six months
and in two consecutive ones only, i.e. the page legitimately dropped and re-added a card as the auction's coverage flipped. A
returning card's picture cannot count as kept in the month it is not on the page, so no retention mends it: the premise (a stable
quote for the six months) is the fixture's, FB6's KBF1 family (cause stable auction coverage). Carried to FB6 by a ship row
(`## known-reds`), the cause there; the floor is untouched.

## Calls

- `Kept.parkedScope()` is a new Kept rule, opt-in: nothing else in the tree changes behaviour. Its rule is in the class javadoc.
- `PIXEL_QUANTUM` (.25 px) and `PARKED_MAX` (48) are the only new constants; both are in the dials index with their sentences.
- The bench's hooks (`KeptBench`, the FB2 banner at its end) are public so KeptCheck can reach `ui` package pictures that are
  package-private; `UserInterface.Headline` and `HeadlineTile` are package-private for the same reason.
- No process change: no script, check machinery or session rule is touched.

## The dry run

`sh tools/gate.sh FB2 --dry-run` (the version it planned: 0.9.131), run directory `/tmp/cbs-gate-FB2-SoO0ydg8` (kept; the log is its
`ship.log`, the launch output `target/FB2-dry.txt`): **exit 0**, `unnamed.txt` empty. It was of branch
`dd379f2835beab980816c45a3c9e8bd051633dc5` (batch/FB2 at the two mends; origin/main had not moved, so no merge) and main
`f38eafbcf90edb68d06b6423f785a9aa7c3596d9` (0.9.130). The pick named 15 of 212 harnesses for the diff, with the nightly tier's
KeptCheck besides; the check suite finished in 1,216 s (exit 3) with **known reds only: DeathRecordCheck, InvestCheck, MapCheck,
YoungBankCheck**, main's own, none of this batch's. KeptCheck ALL OK on the merged tree: section 103's 25 lines, and the Government
Overview at 34 of 400 (6 of 25 months made none; the Overview of the first page at 26 of 712), where main reads 45 of 400 on the
same sort of path. The eight entries filed under FB2 were retired by the merge; 22 went to KRT1 and 38 to FB6 by the ship's
`## known-reds` rows. After the dry run only these notes and the ship file changed.
