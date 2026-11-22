# Native format-60 refusal fixture

Captured on 2026-11-22 from untouched production classes of main `01e6c2d4b`
(Civic Ledger 0.9.188, save format 60). Only Format60SaveFixture.java was added
to that detached scratch worktree; no production source or saved JSON was edited.
The capture method refuses to run on a different version or format.

The native game founds a scratch city, stands twenty houses and a grocery,
plays four months, and writes slot 10 with prepareSaveForChecks().write().
The resources gzip those exact save and history bytes, with no relabelling,
field stripping, hand-written history or conversion. The city is at month 5
and the history has four monthly rows. The old power book has no gridCosts or
pendingGridCosts. The map sidecar is not needed to test refusal before loading.

SHA-256 of the compressed resources:

- format60-save.json.gz: `8964fc488a8c94a6be490b1ed6b82f84ca8f816d3763f7f5dbed40ac74542942`
- format60-history.json.gz: `77e6212e0c73a7896653b9f3a48b8f42b10f45da33fa1f19d94ce464c060a00c`

SaveFileCheck and SubstationCheck load both files into scratch folders and
test a fresh game's refusal and a running loaded twin's full saved state.
ReadPathCheck uses the same fixture, running its real readers before and
after the refused load. Both files must remain byte-for-byte unchanged.
