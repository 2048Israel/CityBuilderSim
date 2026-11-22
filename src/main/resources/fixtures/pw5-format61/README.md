# Native pre-PW5 format-61 save and history

Written by the published RES2 source `7e484c4da`, Civic Ledger 0.9.189,
SAVE_FORMAT 61, without PW5. Only tools/fixtures/GridLegacyFixture.java was
added to a detached scratch checkout for capture; production source and JSON
were not edited. The producer checks the requested native version/format and
rejects any executable that already writes PW5's grid-cost fields.

The native probe city plays twelve further months to month 31 and writes
slot 10 through prepareSaveForChecks().write(). These gzip files contain the
exact native bytes. The save has RES2's housingInventory and no pendingGridCosts
or struck gridCosts. The history has thirty monthly rows.

SHA-256 of the uncompressed records, pinned by the shared refusal harness:

- save: `f94833347a6e7bbd16cd151899b7c3984e729e96012a80d948198cf3cf97572a`
- history: `96f979fb5aa13adf36165eb07170410ba9190f2000a553574e982863348637bf`

The history is byte-identical to the canonical format-60 fixture's history,
as RES2's monetary trace is unchanged. The saved format, build and housing
inventory are natively different. The original shared format-60 pair stays
at fixtures/hc1-format60; this pair exercises the next genuine saved shape.
