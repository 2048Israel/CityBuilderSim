## changelog
### {next} — GD2, gold in the vault — 2026-11-10, ready, see docs/queue/GD2-notes.md
Gold is the vault's ninth reserve, XAU, at the end of its moneys (spec-gold-money 4.2; Jerus, 2026-11-02: "All four,
mines last (Recommended)"). Game hands GD1's price for the month with the cross rates. Gold earns nothing, is revalued
as the other moneys are, and leaves largest-first. It comes in only by name or under Choose: the default All US$, the
world's mix and the trade's never buy it, and it anchors no peg. WorldMoneys.EIGHT stays eight, so the debt and the
policy rates know no gold. On Trade › The reserves, the exchange gets a Gold chip (the card shows the ounces and gold's
price), the chosen mix a "Gold (no interest)" row, and the vault table a gold row in ounces and US$. It is saved in
foreignAccounts' slots 149-151; an older save loads with no gold, and there is no format bump. VaultMoneysCheck 8-12,
and SaveFileCheck's fixture holds gold. The default mix buys no gold, so the trace is main's: no ensemble.
