# FM1: the money's sign chosen and placed before or after ({next}, 2026-11-10)

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
- **Left as written**: history's real amounts (`PolicyDates`' Canadian acts,
  the Construction tip's US figures), the changelog text in `GameVersion`,
  the harnesses' own text, `tools/`, regexes and format indices.

## 2. The slices

(kept current as they land)
