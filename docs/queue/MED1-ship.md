<!-- readiness: provisional -->
<!-- gate-run: /tmp/cbs-gate-MED1-dcferhvX -->
## changelog
### MED1 — a health study — 2026-11-17, ready, see docs/queue/MED1-notes.md
`docs/studies/health.md`, its sheets in `docs/studies/health/`: the causes of death the game lacks, 1900 to today in
Canada, and a mental hospital. Heart disease, stroke, cancer, diabetes and kidney failure become named shares of today's
base rate, each with a history factor (heart disease 5.5 times today's in 1950), so insulin, angioplasty, statins, the
smoking ban, imatinib, checkpoint drugs and PCV7 get steps a city can take early. Polio runs on Canada's own years with
the Salk vaccine as its step, and dialysis keeps a pool the hospitals serve once M4 is known. The mental hospital stays
out: nothing measures what a served patient gains. Settles R2f's ★10, R6-G-K ★2 and ★3 and R6-G-P's medical rows. Six
calls (MED1-K); MED2-MED5 queued. No code.
## pending
| MED1-K | The health study's six calls, each recommended (a): ★1 today's causes with their history factor (the default city's deaths move 1900-2024, an ensemble); ★2 the hospitals serve the dialysis pool once M4 is known, not a Dialysis Unit; ★3 no mental hospital and no chlorpromazine; ★4 L6 a dated row, not a Policy lever; ★5 a row's step is what history took by 2024 (M16, M21, M22 move nothing); ★6 polio's deaths only, no disabled survivors | Jerus's calls; MED2-MED5 take the recommended ways unless he says otherwise | [the study §9](docs/studies/health.md#9-calls-for-jerus) |
## queued
| MED2 | Health: today's causes (MED1 ★1). Heart disease, stroke, cancer, diabetes and kidney failure as named shares of today's base by band, at Canada's 2024 rates, each with its history factor; the elders' fitted to the life tables of 1921, 1951 and 1981. Today's pneumonia, children's diarrhoea and TB named past 1980 (R6-G-K ★2). Booked, saved and on the Services table. An ensemble | [the study §10.1](docs/studies/health.md#101-med2) |
| MED3 | Health: the medical rows' steps (after R6f and MED2). PM2 by band, M7, M10, L6, M13, M17 and M11 as steps on today's causes. M11, M13-M18, M21, M22 and L6 into `research.json` in date order, the rows with no effect by 2024 as dated entries; M14 on general care's stays by R6f's rule. On MED1's ★4 and ★5 | [the study §10.2](docs/studies/health.md#102-med3) |
| MED4 | Health: polio (after MED3). A cause on Canada's own years 1927-1964, with its July-November season and its shift in age; M3, the Salk vaccine, as its step; deaths only (★6). An ensemble | [the study §10.3](docs/studies/health.md#103-med4) |
| MED5 | Health: dialysis (after MED4). A saved pool of kidney failure by band, its incidence from USRDS by age, served at general care's coverage once M4 is known (★2), the unserved booked as kidney failure. An ensemble | [the study §10.4](docs/studies/health.md#104-med5) |
