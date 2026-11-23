# Native shipped format-63 refusal fixture

These are the unedited save and history bytes produced by shipped PW5 0.9.192,
format/floor 63, commit f937d2c22. HousingFormat63Fixture ran alone against that
detached tree's compiled classes after its --fast check. The source diff was
empty before and after. It used GameFiles.scratch, twelve native months after
LongPlaytest.probeCity, and the native slot-10 writer at month 31.

No JSON field was relabelled or converted. gzip only packages the raw bytes,
with a zero timestamp. The native pair has 141 144-field household cells,
HC1 debt surveys and PW5's pending/struck grid costs. It has no RES3 class
market, home-pay history or used-trade tuple. The producer is
tools/fixtures/HousingFormat63Fixture.java.

Uncompressed SHA-256:

- save.json: `51f878c2b9bc9a635dfa1d7079cf9eee17615367ccd03c192289cef9e409a00c`
- history.json: `9bacc907f2c0c797e170208f39353ac9ab84d26e744ffb9d094f705c78cc1ad2`

SaveFileCheck pins these hashes and the native schema, then refuses fresh and
running loads at RES3's floor 64. Both files, manager identities, family floor,
history object, full saved city and money stay intact. ReadPathCheck repeats
the reads. The prior native format-62 and other shipped refusal pairs remain.
