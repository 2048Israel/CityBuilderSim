## changelog
### {next} — FM1, the money's sign and its place — 2026-11-10, ready, see docs/queue/FM1-notes.md
The city's money sign is the player's, before or after the figure ("ƒ2.6M",
"2.6M kr"), with a space only where a letter touches the number. One writer
(`Currency.write`, `qualify`, `marked`) writes every money figure on the
screens and in the model's text, so the sign chosen at founding reaches every
page; beside a foreign figure the city's initial qualifies it only where a
vault mark could be mistaken for it ("D$", "T¥"), and "kr", "zł", "R$" stand
alone. The founding screen has a sign field (three characters, typed or picked
from the signs the interface's font draws), before/after chips, and its
figures shown in the money being chosen. History's own figures keep their own
marks (C$, US$). With "$" before, every string is the old one byte for byte
and the playtest's trace is main's; `MoneySignCheck` and `MoneyWordsCheck` new.
## queued
| FM2 | The money from the founding spot's nation, the city's own styled after it ("the Tokyo yen, ¥, TOK"; Jerus, 2026-11-02: "Its own, styled as yen (Recommended)"): noun, sign and place from the JDK's CLDR data, a table for the pre-ISO moneys, the default following the spot, and the qualified sign's partner half (a city sign the same as a partner's on the screen qualified too; FM1's notes §1). Folded in, **FM3**: The starting level, units of the money a US$ (Jerus, 2026-11-02: "Yes, units per US$ (Recommended)"): the nation's rate at month 0, today's or typed, as a reform on day one (the unit at 1/L), a twin harness to 1e-9 (after FM2; DZ1 shipped). Both after EP1b | [GD0 §4.7](docs/studies/spec-gold-money.md#47-the-money-from-the-nation-fm2-after-ep1b) · [GD0 §4.8](docs/studies/spec-gold-money.md#48-the-level-at-the-start-fm3-after-dz1-and-fm2) |
