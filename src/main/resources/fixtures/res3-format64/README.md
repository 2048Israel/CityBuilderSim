# Native shipped format-64 refusal fixture

Unedited save and history bytes produced by shipped R6f 0.9.196,
format/floor 64, commit 94ba28823. `HousingFormat64Fixture` ran alone
against that detached tree's compiled classes after its `--fast` check.
The native source diff was empty before and after. It used
`GameFiles.scratch`, twelve months after `LongPlaytest.probeCity`, and the
native slot-10 writer at month 31, wholly inside the scratch directory.

No JSON value was relabelled or converted. gzip packages the raw bytes
with a zero timestamp. The pair includes the shipped housing inventory,
HC1 card/debt history, PW5 grid flows and PW2 used-car price. It has no
RES3 class market, home-pay index or used-trade tuple. Its producer is
`tools/fixtures/HousingFormat64Fixture.java`.

Uncompressed SHA-256:

- save.json: `5f2c197a1e26cfd33dbd01fc0dbf10ef889ba374c130721071c6f2dca6bd712c`
- history.json: `e86d6f648d9f3b806397bca6c846de2c53f6f02b45867ddfb57f54a072d8662b`

At RES3's current format/floor 66, SaveFileCheck pins the hashes and native
schema and refuses fresh and running loads. Both files, managers, family
floor, history object, full saved city and money must remain intact.
ReadPathCheck repeats the reads. All older native refusal pairs remain.
