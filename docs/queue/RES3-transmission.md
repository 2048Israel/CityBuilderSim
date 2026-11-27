# RES3 transmission comparison, 2026-11-24

Native main `94ba28823` (0.9.196) versus RES3 `8fdb111b6`.
The same original founding and world paths 16-31 use rates 3/10/20/40%,
holds at months 25 and 26, and the unchanged months 49-84 window.
Two serial named diagnostic invocations complete with 272 rows and 1,904
successful founding, basket, currency, crew and calendar premise checks.

Mean on-time annual inflation, percent:

| Model / measure | 3% | 10% | 20% | 40% | Low-minus-high, points |
|---|---:|---:|---:|---:|---:|
| Native actual CPI | 5.793602 | 5.435019 | 8.231521 | 8.325781 | -2.532180 |
| Native shelter | 7.185923 | 6.197521 | 10.664913 | 10.799563 | -3.613641 |
| Native common-basket non-shelter | 2.856309 | 3.781828 | 2.928217 | 2.931302 | -.074993 |
| RES3 actual CPI | 4.056172 | 3.093337 | 4.375457 | 4.046189 | .009983 |
| RES3 shelter | 3.777880 | 3.447063 | 5.266537 | 4.486215 | -.708336 |
| RES3 common-basket non-shelter | 4.527429 | 2.171282 | 2.117868 | 2.904979 | 1.622450 |

These are independently annualized price-index changes, not additive
percentage-point contributions. Means are across all sixteen declared
paths, not selected positive cases. The original town is tested separately.

Per-path low-minus-high effects, percentage points; negative means the
higher rate produces higher inflation over this window. NS denotes the
common physical non-shelter basket.

| Path | Native CPI | Native shelter | Native NS | RES3 CPI | RES3 shelter | RES3 NS |
|---:|---:|---:|---:|---:|---:|---:|
| 16 | -4.382595 | -5.929227 | -0.906967 | -1.279970 | -1.669659 | -0.249399 |
| 17 | -4.056095 | -5.620633 | -0.515691 | 2.505515 | 1.454815 | 4.985660 |
| 18 | 2.098753 | 2.495745 | 1.305657 | -2.150326 | -3.108201 | 0.245313 |
| 19 | -4.891980 | -6.627028 | -0.922818 | -2.799384 | -3.614714 | -0.726760 |
| 20 | -4.241827 | -5.900757 | -0.533406 | 1.236921 | -1.847032 | 7.743325 |
| 21 | 2.325734 | 2.714685 | 1.534000 | 4.628029 | 4.709894 | 4.447895 |
| 22 | -3.940590 | -5.440275 | -0.626987 | -3.793522 | -5.294469 | 0.188639 |
| 23 | 1.810699 | 2.138633 | 1.151592 | -3.374444 | -4.663235 | -0.083636 |
| 24 | 2.178313 | 2.475611 | 1.571711 | 5.380272 | 7.312308 | 0.047080 |
| 25 | -3.519913 | -4.893118 | -0.398906 | -0.830298 | -0.711613 | -0.773548 |
| 26 | -3.997202 | -5.535125 | -0.553108 | -3.234303 | -4.478762 | -0.059564 |
| 27 | -4.172924 | -5.818946 | -0.383700 | 2.589675 | 1.674188 | 4.515911 |
| 28 | -3.961879 | -5.492752 | -0.573132 | 1.514051 | 0.195548 | 4.488172 |
| 29 | -3.672604 | -5.119270 | -0.368142 | 1.000631 | 0.536766 | 0.922806 |
| 30 | -3.880929 | -5.408159 | -0.454502 | -1.000098 | -1.458477 | 0.165857 |
| 31 | -4.209834 | -5.857638 | -0.525495 | -0.233025 | -0.370727 | 0.101451 |

Each path fixes the quantities of groceries, meals, luxury and every service
fee from its 3% on-time twin, then prices those exact quantities at both
endpoints of every policy twin. Only rent is excluded. Component quantities
are weight/base-price; fee quantities additionally retain the service fee
shares and fee bases. Normalization cancels in the endpoint ratio. This
avoids treating different policy-dependent component weights or nested fee
shares as price movements. Own-basket non-shelter effects are -.074978
points on native main and 1.621916 on RES3. Repricing both models at the
identical native physical basket still gives RES3 1.669167 points.

Native main has a coverage gap: its original town passes a 2.231010-point
total-CPI effect, yet its declared multi-town mean is adverse and twelve
paths have adverse non-shelter effects. This historical result is recorded
as such; it is not attributed to RES3 or called general disinflation.
RES3 has eleven positive and five adverse non-shelter cases (16, 19, 23,
25, 26). The retained price assertion is on the complete declared mean,
not a universal per-town sign. Every path in both models has a lower live
mean spending-plan factor at 40% than at 3%. The price guard is necessary
in addition to that plan guard; a falling factor alone did not establish
non-shelter disinflation on native main.

The common portable probe and founding body hashes are respectively
`a445df5dd53025c049bb197c362c74ed9c16d53e3426b7db7b1fdb4dd4d7bc38` and
`ececf1257d0deb118262215a03e579a786a6ef9ab03e63f91dce5a116fe9f76c`.
Native and typed model source blobs were unchanged. Equal fixture inputs
do not imply equal month-25 state between models; policy/timing twins
within each model have exactly equal fingerprints.

Full portable source, entry-point patches, commands, unrounded path data,
opening/closing snapshots and raw logs are retained in
`target/RES3-P1-trace/`. The trace helper reports in bus mail
`20261124-033229-435-RES3-P1-trace-94a5` (15 lines). This comparison is
not a causal ablation of every housing feedback and makes no longer-run
claim beyond the measured window.
