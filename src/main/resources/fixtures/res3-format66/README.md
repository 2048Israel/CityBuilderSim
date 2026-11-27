# Native shipped format-66 refusal fixture

Unedited save and history bytes produced by shipped R6e-rest 0.9.201,
format/floor 66, commit 78ed673151e487359b44cbac44b6994b16ec6182.
The model source was a fresh clean detached checkout, compiled from its
683 verified Java/resource/pom/script blobs. No RES3 model classes or
current candidate classes were reused. The unchanged external producer is
`tools/fixtures/HousingFormat66Fixture.java` from 0cce237e23d6d5a0fff2ef180f37e53fe02a1479
(SHA-256 `d91af226d799166726e1383fcb753a422bf05b96ec9cca52224b5a3a8bce90db`).

The producer ran alone against that checkout's compiled classes with
Oracle JDK 21.0.4+8-LTS-274 and the pinned native earth-2.bin/world-1.bin.
All compile/native stages passed fresh MG1 readings. Model git diffs were
empty before and after. JVM class-load logging from this same run proves
Game, GameVersion, DataSave, HistorySave, GameFiles and LongPlaytest came
from the fresh shipped target/classes; only the external producer came
from the separate producer-classes directory. The accompanying private
producer evidence contains the complete source/JDK/assets/classpath/origin
manifests, raw bytes and exact commands/exits.

GameFiles.scratch and native slot 10 were used. The shipped probe helper
sets up month 19; the producer then simulates twelve more months and writes
at month 31. The unedited history has 30 rows (months 2 through 31), with
world seed 4127. The native save header is gameVersion `0.9.201`,
saveFormat `66`; the legacy price-index payload has 169 slots.
The actual native save lacks `newCarTrade`, `populationWageTotals`,
`bankHeldBooks` and `abroadClaimById` (R1g's key, shipped in 0.9.206 at format 66), as the shipped model does.

No JSON value was relabelled, restamped or converted. gzip packages the
exact raw bytes at compression level 9, with no filename and timestamp 0.

| File | Raw bytes | Uncompressed SHA-256 | gzip SHA-256 |
| --- | ---: | --- | --- |
| save.json | 1131101 | `43f82bba8e92d69fcb0e26625cd23e494af880643adc4201b204dc1374b35166` | `1ddaefe71c53dad0500bf5497e00b725bde9c5788f67833eeed61718c295ccc4` |
| history.json | 261602 | `9b7a70fdb88856a31c3b1e7926f51bf127590170ac279ae0d5700162ee033dd8` | `6535fdcb892dc6f73b465210678b71911c461194f773fc594f8ad6ddcb68b6a9` |

Prepared for format/floor-67 refusal coverage. No format-67 test was run by
this producer; the parent installs and reviews the real pair and hash rows
before the next exact-source native GO. Existing format-65 and older pairs
remain separate and unchanged.
