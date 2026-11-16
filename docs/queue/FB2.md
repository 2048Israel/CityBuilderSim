# FB2 - the kept screens' geometry and retention

Bundled on 2026-11-16 from 4 Queued rows (Jerus: "Yes, bundle them"), so fixes
that share a harness or a file go through one build, one review and one dry run,
not one each. WHY: over 11-14 to 11-16 a fix batch held the dry-run slot 34
minutes on average, most of them for the whole suite.

- **Rows:** KL1, KS1-UI, BF1-K, KS1-Gov, below as QUEUE.md had them; their briefs stand.
- **Files it expects to change:** `KeptCheck.java`, `ui/FinancesScreen.java` (`drawsAlike`, `CurveChart`), `ui/SectorScreen.java` (`IndustryPane`), `ui/FundScreen.java`, `ui/PartnersPage.java` (`CurveChart`), `ui/GovernmentScreen.java`, the kept header's class (its RATE label; KS1's notes).
- **Moves the playtest:** no: screens only.
- **Not beside:** FB6 (`KeptCheck`).
- **Known reds:** the 8 entries filed under these rows are filed under FB2 now, so its ship must turn them green.
- **Done** means each row's own done condition. A row that proves bigger than a fix
  leaves the bundle before the gate: the orchestrator files it back under its own
  id, with its known-red entries, in a queue commit.

## The rows

### KL1

KeptCheck's BuybackLadder line ("no month draws it again with nothing changed in it") on GW1's path: `FinancesScreen.drawsAlike()` (ui/FinancesScreen.java:4950) compares the ladder and `takenFace` bit for bit, so a 1e-8 drift in the holders' faces redraws a picture drawn to four decimals. Compared at the picture's resolution; KeptCheck's line green

Brief: [GW1 notes §4](GW1-notes.md)

### KS1-UI

Correct kept screen geometry in the hidden header RATE label and the Wood Products IndustryPane Residues Text. The header differs in 18 states on main. The picture differs by one pixel after rendering both pages in the normal harness prefix.

Brief: [KS1 screen finding](KS1-notes.md#screen-finding)

### BF1-K

Repair retained empty Fund Search glyph bounds and Partners CurveChart buyer-entry triangles, preserving kept/fresh comparisons and pixel band

Brief: [BF1 kept UI findings](BF1-kept.md)

### KS1-Gov

Retain the Government Overview's transient cash-flow and bridge rows so its eventful monthly allocation stays within the existing tenth-of-first-draw floor

Brief: [cause and done condition](HW1-notes.md#government-allocation-follow-up-ks1-gov)
