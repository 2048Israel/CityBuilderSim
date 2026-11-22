# Native format-61 refusal fixture

Produced by shipped `7e484c4da` (0.9.189, format/floor 61), in an isolated
worktree. `tools/fixtures/CardFormat61Fixture.java` was compiled against that
source with `--fast`, then run alone against its classes. It asserts native
format 61, uses `GameFiles.scratch`, advances twelve months to month 31 and
writes slot 10 through the native save path. The saved JSON and history were
copied byte for byte and compressed with gzip (mtime 0). Nothing was relabelled,
padded, truncated or converted; no player slot was written.

The save has RES2 housing inventory and 141 old 128-slot household cells.
It has neither the card share positions nor pending discharged departures;
its history has no HC2 surveys. Format 62 refuses that layout. The shared
refusal helper checks the original hashes, the actual old schema, fresh and
running loaded cities, unchanged save/history bytes and saved state/history,
manager identity, month and cash, including repeated reads.

Uncompressed SHA-256:

| file | SHA-256 |
|---|---|
| save.json | `1d352d140f18d816e3f3f81797ab23805e719677905cf306fb35fee782a464de` |
| history.json | `96f979fb5aa13adf36165eb07170410ba9190f2000a553574e982863348637bf` |
