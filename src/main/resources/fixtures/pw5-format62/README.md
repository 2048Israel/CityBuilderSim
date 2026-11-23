# Native pre-PW5 format-62 save and history

Written by the published HC1 source `efca2c5fd`, Civic Ledger 0.9.190,
SAVE_FORMAT 62, without PW5. The detached checkout's production source and
JSON were not edited. `tools/fixtures/GridLegacyFixture.java` was compiled
separately against that checkout; it checks the requested native version
and format and rejects an executable that writes PW5's grid-cost fields.

The native probe city plays twelve further months to month 31, then writes
slot 10 through `prepareSaveForChecks().write()`. These gzip files contain
the exact native bytes. The save has RES2 housing inventory and HC1's
144-slot household cells, with no pendingGridCosts or struck gridCosts.
The thirty-row history includes HC1's debtSurveys.

SHA-256 of the uncompressed records, pinned by the refusal harness:

- save: `c28b7a4c3e30b7cb90686a93e6d3832d33717532cb85ba040d511fbcdd5721a3`
- history: `c12d06bde4e5844f4c4e260175f5e51f7828b77a126dc610fbb21a6dca712a6b`

The canonical format-60 pair remains at fixtures/hc1-format60; the two
independently captured RES2 pairs remain under hc1-format61 and pw5-format61.
This pair proves the refusal at PW5's raised format-63 floor using the real
immediately previous shipped shape, without converting or restamping JSON.
