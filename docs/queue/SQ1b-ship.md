<!-- gate-run: /home/jerus/cbs/runs/cbs-gate-SQ1b-lSlwdgfr -->
## changelog
### {next} — SQ1b, a month's material draw past 2^31 and buildStack's harness — 2026-11-20, ready, see docs/queue/SQ1b-notes.md
`Game.drawMaterials` rounded a month's draw of material into an int, so a draw past 2^31 units wrapped (3 x 2^31 became
a negative count): the whole draw went unbought and unbooked. The draw is a long end to end now: the yard
(`BuildingManager.takeFromYard`), the yard's delivery to a new order's sites, the run invoice's reduction of the yard,
the quote's yard, and the month's tally `materialsConsumed`, which is saved (the yard's stock and the tally are longs
in `DataSave`; an old save's int reads into one, no format bump). `BuildingStackCheck` sections 7 and 8 order 3.2e8
Houses through `Game.buildStack` on a copy whose stack stands just under the ceiling and read back its quote, its
ground, the cash it charged, the receipt, the yard's share and the run's invoice, then draw 6.4e9 units twice (the sites'
and a repair's, yard first); each old int put back turns its own assertions red. The default playtest's trace is main's,
byte for byte (eight files, 2,405 months).
