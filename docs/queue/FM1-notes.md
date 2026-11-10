# FM1: the money's sign chosen and placed before or after (0.9.88, 2026-11-10)

The brief is `docs/studies/spec-gold-money.md` 4.6: the sign the player's,
before or after the number ("2.6M kr"), one writer for every "$", the
founding screen's sign field and its font check, byte-identical by default.
FM2 and FM3, folded into this row at the queue's triage, wait on EP1b and go
back to the queue as one row (the ship file's `## queued`).

## 1. The design, as decided

- **`Currency` gains `signAfter`** (saved `currencySignAfter`, a missing key
  reads false). `write(number)` places the sign: before or after, a space
  only where the character beside the number is a letter ("$2.6M", "€2.6M",
  "2.6M kr", "CHF 2.6M", "R$2.6M"). The space is the game's own plain one,
  as "900 kW" has. `write()` never moves a minus: the callers keep it outside
  as they did ("-$5"), and a `String.format("$%,.0f")` that printed "$-5"
  still does, so "$" before is the old text byte for byte.
- **The qualified sign** (where a foreign figure is in sight) is the city's
  initial and the sign only when the sign is "$" or is, or ends, one of the
  vault's marks (`ForeignAccounts.MONEY_MARK`): "A$", "T¥", "A€"; "kr", "zł",
  "R$" stand alone. The partner half of the rule ("or the same as a
  partner's on the screen") waits for FM2, which gives nations their signs.
- **Which city's sign a formatter writes.** `ui/Money` is static and
  `Formats` a singleton, so `Currency.writing()` names the money being
  written: set by `Currency.use()` when a `Game` is founded or loaded and
  when the window binds a game. One process holds several cities in a
  harness (Currency's own WHY); every one of those is "$" before but a
  harness that sets a sign, which sets it back.
- **One writer**: `Currency.write()`, `writeQualified()`, `qualify()` (a
  figure written in the sign, rewritten in the qualified sign in its place)
  and `marked(mark, written)` (a foreign mark before the figure, sign-aware:
  "5 kr" marked US$ is "US$5"). Money's forms, Formats' and the model's text
  call them; `Money.isZero(shown)` replaces the `"$0".equals` tests.
- **Left as written**: the changelog text in `GameVersion`, the harnesses'
  own text, `tools/`, regexes and format indices. History's real amounts are
  written in their own money's mark, not the city's: `PolicyDates`' Canadian
  acts in C$, the gold window's "US$35" and the Construction tip's US figures
  in US$, the reserves' chips "C$" and "A$" (they were "Canadian $",
  "Australian $"). `WageLadder`'s census wages keep their "$": lane B's file,
  a source note that no page draws (the words probe below finds none).
- **No `SAVE_FORMAT` bump**: an old save has no `currencySignAfter` and
  reads "before", which is what it was.

## 2. The slices

(kept current as they land)

- **A** (022e42a0): the writer in `Currency`, the save key, `Formats`,
  `ui/Money`, Game's own text, `Currency.use()` at founding, load and the
  window; `MoneySignCheck` 82. The default playtest traced on main's classes
  (52d120cb) and on A's: the eight files byte-identical, the report only its
  run time apart. Line 12522's bond face: a negative or non-finite face would
  now read "-US$5" for "US-$5", and none can reach it.
- **B** (c7c06cab): the finance-side screens (Finances, Government,
  History, Policy, Fund, Bank, Pieces, StatementView, People). The `"$0"`
  tests through `Money.isZero`; the info texts that named "D$" made methods
  through `Money.ours()`, so they read the city's sign when drawn; the typed
  amount's parser takes the city's sign before or after.
- **C** (8830f679): the other screens (Trade, Land, Partners, the window,
  the map, Build, Rankings): the qualified sign written in its place
  (`qualify`, `writeQualified`) where `marked(sym(), ...)` had put it in
  front. Its finding: with a sign after, `marked` with the city's own sign as
  the mark moved it to the front ("kr2.6M"); one fix in `Currency.marked`
  (691b1eaa): the city's own mark, bare or qualified, keeps its place.
- **E** (b88130f5): the model's own money text in 25 files (the planners'
  reasons, the debt quotes, the inbox, the land office, the views) through
  `Currency.writing()`; padded widths kept.
- **D** (d7b5f95f): the founding screen's sign field (at most
  `Currency.MAX_SIGN_LENGTH` code points, typed or picked from chips), the
  before/after chips, the problem line (`Currency.signProblem` and the font),
  the founding figures written in the money being chosen
  (`Currency.using`). `ui/SignFont` offers the signs the interface's font
  draws (IBM Plex Sans and Mono Regular, AWT's `canDisplayUpTo`): all 15
  candidates are drawn, none dropped. GD3-F's box has its place marked
  beside the money row.
- B and C ran in sibling worktrees and were applied here. A hook then
  refused E's and D's writes there ("Do not write to other worktrees' files
  from this session"), so E ran in this worktree and D in an Agent worktree
  of its own (`isolation: "worktree"`, the memory's slice-worktrees note).

## 3. The checks

- **`MoneySignCheck`** (new, 95 assertions): the golden table of the old
  strings, the placement, the qualified rule, every form with "kr" after,
  the typed sign's problems, the save roundtrip in a scratch folder, the
  font's signs and a "kr"-after city founded in scratch.
- **`MoneyWordsCheck`** (new, JavaFX, skipped where the suite skips
  `KeptCheck`): the words probe. KeptBench's 118 pages drawn for a "$" city
  and for a "kr"-after city; on the second no "$" is left but in a foreign
  mark (US$, C$, A$ and the vault's others). It found the PolicyDates facts,
  the reserves' chips and the Construction tip.
- **The trace**: the default playtest traced (`-Dplaytest.trace`) on main's
  classes (52d120cb) and on the batch's last model (d7b5f95f): the eight
  files byte-identical, the report apart only in its run times (49 s, 53 s)
  and both exiting 1 alike. The ensemble is skipped (EN1).
- **`--pick`** (the data files named): exit 3, the reds main's recorded ones
  (PriceLevelCheck, HealthCheck, MapCheck, KeptCheck, LongPlaytest), 158 of
  197 harnesses run. Its review lines: the money files (Bank, DebtQuote,
  BankScreen, FundScreen) change only how a figure is written, each computed
  as before; the save is the one key above; `Currency.writing` is volatile
  because the clock's thread and the FX thread both read it (it is set only
  at founding, load and the window's bind); `SignFont.faces()` is
  synchronized to read the two font files once.

## 4. Calls and what is left

- **GD3-F stays in Pending** (the orchestrator's call, taken): its box is
  more than a few lines. `Founding` has no regime to start on, and the
  vault's purchase of gold at founding is a model step of its own; D marked
  the box's place beside the money row.
- **The partner half of the qualified rule** ("or the same as a partner's
  on the screen") waits for FM2, which gives nations their signs; FM2, with
  FM3 folded in, both after EP1b, is the ship file's `## queued` row.
- **One in, one out**: `git diff --numstat origin/main` over the process
  files is `CLAUDE.md` 1/1 (the interface's count, "fifty-one", Stale's firm
  finding once `SignFont` was added) and `README.md` 1/1 (`MoneyWordsCheck`
  in the JavaFX-skipped list): 0 net.
