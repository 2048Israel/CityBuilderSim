<!-- gate-run: /tmp/cbs-gate-FB2-SoO0ydg8 -->
## changelog
### {next} — FB2, the kept screens' geometry and retention — 2026-11-17, ready, see docs/queue/FB2-notes.md
The kept screens read the same as screens built new whatever the played city does, and each repair is caused by the states that need it (KeptCheck 103, 25 lines: each repair's lines red by hand on the old classes, none now).
The buyback ladder is drawn from what it reads of a quote, as written and at a quarter pixel, so faces moving by 1.4e-8 of themselves no longer draw it again (KL1). The header's hidden wordings and
sparkline stand where a tile built new leaves them (the RATE tile's "D$1,000,000,000.00 per US$": 18 states), a bar's tick with no name has no label on it (Your rate's dial: 0 x 20 against 0 x 13), and
Partners' buyer-entry triangles stand at their own points, not moved by a fractional layout offset (62 pixels at the finding, 0 now); Search's empty price and held words are FP1's scopes, now asserted. The Government
Overview's lines that leave and come back (a tax's slice and its key row, a bridge's step, the arrears) are parked, off the page, by the new Kept.parkedScope() and return with no node made (38 nodes before, 0 now);
the busiest month of a path whose first draw made 400 nodes reads 34, not 45, and a line's first appearance still makes its nodes. Two entries are not mended and are filed: the Wood Products Residues Text (KRT1: 0 differ over 120 months
and on KS1's own classes) and the Borrow page's ladder card coming and going with the quote (FB6's KBF1 family). Screens only: the playtest does not move.

## queued
| batch | what | brief |
|---|---|---|
| KRT1 | KeptCheck's Wood Products picture: a `Residues` Text's bounds differ between the kept `IndustryPane` and a page built new at month 1,868 of KS1's path (151.8,249.8 48.2x15.0 kept, 151.8,249.6 48.2x16.0 fresh; KS1 notes, Second pick). FB2 could not cause it: KeptCheck's section 50 with 120 months (not 6) on the 0.9.130 city, kept page against a page built new each month, 0 differ (5,595 of 5,783 parts kept); KS1's own classes (codex-KS1 target/classes, 62 parts) through section 50, 0 differ. Plex Sans 11.5 alone measures 14.95 high, so the fresh 16.0 is the taller line, and the printed font and CSS properties of both Texts are equal. Needs KS1's month-1,868 state: capture the page's Words at that month (`IndustryPicture.Picture.words()`), then find which property of a handed-back Text (or which context its bounds were first computed in) differs. Carries the entry below | [FB2 notes](FB2-notes.md) |

## known-reds
KeptCheck | a materials industry's picture (Wood Products): the page equals a page built new at every month | KRT1 | 2026-11-17 | Not reproduced on 0.9.130 (0 differ over 120 months of the Wood Products IndustryPane, nor on KS1's own classes through section 50); main's unchanged prefix reproduced a Residues Text kept 249.8 / 15.0 against fresh 249.6 / 16.0 at KS1's month 1868; see KRT1 and FB2 notes
KeptCheck | the Columns on Borrow at home, asking for a sixth of the cash: each month's redraw hands back nine in ten of the pictures it had, or more (7 of 10) | FB6 | 2026-11-17 | The Borrow page draws "THE LADDER THIS WOULD BUILD" (a Columns picture) only while its quote is not empty, so a fixture whose auction coverage flips from month to month drops and re-adds that card; 10 pictures before and 7 kept over six months read as the card present in four months and two in a row; the premise (a stable quote for the six months) is the fixture's, KBF1's family; see FB2 notes
