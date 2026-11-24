# Native format-60 refusal fixture

Produced by the actual shipped source at `01e6c2d4b` (0.9.188, format 60),
in an isolated detached worktree. No current-format JSON was relabelled,
truncated, padded or converted. `tools/fixtures/CardFormat60Fixture.java`
was copied into that source's Java package, compiled there by `--fast`, and
run alone against its classes and Maven classpath. The producer asserts
`SAVE_FORMAT == 60` before doing anything.

The producer uses `LongPlaytest.probeCity(GameFiles.scratch(...), null)`,
advances twelve normal months to month 31, and writes slot 10 through
`prepareSaveForChecks(...).write()`. The save and its separate history are
copied byte for byte, then compressed with gzip (mtime 0). Nothing writes a
player slot. Uncompressed SHA-256:

| file | SHA-256 |
|---|---|
| save.json | `9e0be412f32e7676190042f3341040a1f07d7b51e6018902cc0b79641808a186` |
| history.json | `96f979fb5aa13adf36165eb07170410ba9190f2000a553574e982863348637bf` |

The native save has 141 named cells, each 128 slots wide: its credit balance
and old cell-wide lockout slot, without HC1's holder, late and exclusion
positions. The history has no HC2 surveys. `SaveFileCheck.nativeCard60Refusal`
checks the layout, provenance hashes, refusal, unchanged save/history bytes,
and an unchanged running city's complete saved state and history.

RES2 reuses this exact pair and producer, including the absence of its new
housing inventory state. The shared refusal check also exercises
SaveFileCheck.refusedAtTheFloor on a fresh game and, from ReadPathCheck,
repeated reads after refusal into a running loaded twin. It preserves both
file streams, all saved state and history, and manager identities. Keep one
copy of this pair when integrating HC1 and RES2.
