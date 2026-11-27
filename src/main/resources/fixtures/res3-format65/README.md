# Native shipped format-65 refusal fixture

Unedited save and history bytes produced by shipped FC2b 0.9.198,
format/floor 65, commit e6a47dff875f35f58223ab5d9e7256530faddaf3.
`HousingFormat65Fixture` ran alone against that detached tree's compiled
classes after its `--fast` check. The native source diff was empty before
and after both runs. The external producer used `GameFiles.scratch`,
twelve months after `LongPlaytest.probeCity`, and the native slot-10 writer
at month 31, wholly inside the scratch directory.

No JSON value was relabelled or converted. gzip packages the exact raw
bytes with a zero timestamp. The pair includes housing inventory, HC1
card/debt history, PW5 grid flows, PW2 used-car price and FC2b contract
construction points. The native active contract has points
`1454.9999999999998`; this was produced by the normal model, not an assigned
fixture field. It has no RES3 class market, home-pay index or used-trade
tuple. Its producer is `tools/fixtures/HousingFormat65Fixture.java`.

Uncompressed SHA-256:

- save.json: `be8f72e5c39f0bc2147d63fb44bc84a8bffa1777cf7c3432bf622e4807352082`
- history.json: `3b4339debc58574aa470d3429e8f144f63e202ea509dd999fd4fe9b444919877`

At RES3's current format/floor 66, SaveFileCheck pins the hashes and native
schema and refuses fresh and running loads. Both files, managers, family
floor, history object, full saved city and money must remain intact.
ReadPathCheck repeats the reads. All older native refusal pairs remain.
