# Native shipped format-62 refusal pair

Produced from detached, unedited `1b89c5c4c` (AP2, 0.9.191, format/floor 62).
The isolated source worktree was compiled with `--fast`; its production source
and resources were verified unchanged before emission. The named producer,
`tools/fixtures/HousingFormat62Fixture.java`, was compiled against those classes
and run alone, as for HC1's native format-61 producer. Its reflection guard
requires the actual runtime's format 62 and version 0.9.191.

It uses `LongPlaytest.probeCity(GameFiles.scratch(...))`, advances twelve months
to month 31, then writes scratch slot 10 through `prepareSaveForChecks().write()`.
Both files are copied byte for byte and compressed with gzip mtime 0. No JSON
is relabelled, converted, truncated or padded, and no player slot is touched.

The save contains RES2's inventory and 141 HC1 household cells of 144 fields
each; its native history contains two debt surveys. It has no RES3 housing
market, wage-index history or used-car trade tuple. The boundary check pins
this schema, original version and both hashes, then refuses the pair in a
fresh and a running city while preserving both files and all running state.

Uncompressed SHA-256:

| File | SHA-256 |
| --- | --- |
| save.json | `4c2d3ae2e2fd4c4168b24446ab06d0f6db158d0a0b71b0a7018a8a621ba8ad0c` |
| history.json | `953d14f3113bf858b73f242370fcdb103fe76d818dcf001a6d4384fc8fa87a87` |
