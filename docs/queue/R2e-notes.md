# R2e - The 1951-2000 entries undone

Started on main 32ec5a5d, batch/R2e, 2026-11-15.
No known-reds entry names R2e at the fork.

## Built and design calls

28 signed changes use R2d's immutable modern anchor, saved missing-change
flags, unfitted equipment counts and one rounding. E19/E20 open Panel Mill
and Wood Pellet Mill. The 91-entry catalogue appends both permanent codes.

★ Default founding keeps its existing guard and modern figures.
The eight main CSVs are captured once on 32ec5a5d before source edits.
Its compiled classes/data are retained under target/R2e-evidence/main.

★ New equipment uses R1c call 10's capital estimate, 25% of plant cash
at MACHINERY 13.4, 10% construction points, 300 months. These are estimates.
Billing uses R2c/R2d's private-firm production-period route.
The orchestrator accepted the mapping calls in cbs-bus messages
173126, 173251 and 173708 on 2026-11-15.

## Every row's sourcing and mapping

Every applied figure's arithmetic and source keys also live in its
buildings.json source. No source partitions all of a template's crew.
Each crew mapping below is an explicit estimate.

| Entry | Earlier form, source and estimate |
|---|---|
| E1 | R1c/R2d already supply sourced Bessemer/open-hearth alternatives and E1's modern converter. Keep that process/price choice. |
| E2 | Study [T32,T34], wet 7.0 vs preheater 3.0-3.6 GJ/t. Midpoint 3.3 scales modern coal. Lime tonnes proxy clinker. E2 is scoped after PE4 to preserve its calibrated 1900 total, as recorded below. |
| E3 | [T21] gives water depth 30 vs 120-150 m, no comparable well-output ratio. Pending because the model lacks depth. |
| E4/E5/E6/E7 | Existing building gates already open pellets, container terminal, desalination and hydrocrackers. [T12], [T53,T54], [T52], [T30,T31] respectively. Keep modern figures when available. |
| E8 | [T57], also NIST publication 820148, torch-on 30% vs 90%. Unskilled assembly posts proxy welding crew, old x3 after PE13. No source partitions the plant crew. |
| E9 | Study 3.1 [T4] estimates old -10% steel, +12.5% posts, +1.2 GJ/t fuel. Production bands only, same melt inputs. Fuel-oil proxy at R2d's 36 MJ/L heating-value estimate. OTA itself gives 10-12% yield improvement,10-15% less direct labour and about 1 MBtu/short ton direct energy. Use the study's named rounded undo. |
| E10 | [T38] estimates doubled wheat yield with fertiliser. CROPS old half modern is a crop-mix estimate, additive beside PE20. Missing fertiliser good/works/prerequisite stays beside R2d-PE12. No modern fertiliser invoice exists to undo. |
| E11 | [T26], amorphous 55.5 vs zeolite 62.0% petrol. Normalize to modern 0.55 * 55.5/62. Lost petrol remains fuel oil, a mass-balance estimate. Model has no cracker coke stream. |
| E12/E13/E14/E16 | Existing gates open SX-EW, mini-mills, mechanised harvesting and wind. [T15,T16], [T3,T6], [T37], [T49,T50] respectively. No new predecessor or uncatalogued later wind technology. |
| E15 | [T17,T20], 30 to 149 t/face-worker/day and 60 to 80% recovery. Unskilled posts proxy face crews at fixed output. Dig 1/0.6 before vs modern 1/0.8. Loader/cutter independent. Whole-period productivity is a shield-crew proxy estimate. |
| E17 | [T55] gives twice the containers/train. Pending because all-cargo rail tonnes have no container share. |
| E18 | [T10,T3], reheating 2.0 to 0.5-0.8 MBtu/t, midpoint 0.65. Add missing 1.35 MBtu/t as fuel oil, 1055.056 MJ/MBtu and 36 MJ/L. Metric-tonne proxy estimate, additive beside E9. |
| B1 | BLS 1276 [M-B1d], affected-unit staff -25%. DIPLOMA/COLLEGE_BUSINESS proxy clerks, old /0.75. SRI [M-B1c] posting 245 to 33000 accounts/hour measures one operation. BLS median 29 computer staff has no per-template office scale. Record that gap without a staff subtraction. |
| B2/B4/B6 | Existing gates open Contact Centre, Walk-in Clinic and Shared Services. [M-B6], [R2], [R2]. These new buildings have no earlier instance. |
| B3 | [M-B3a], tellers 20 to 13. DIPLOMA posts proxy tellers, old x20/13. B1 composes additively. Branch-count growth is not a productivity multiplier. |
| B5 | [M-B4], study payroll reduction 5.35%. Old posts /0.9465 at fixed band mix, one rounding. Convenience/Filling have 5/3 posts and round to unchanged whole posts, explicitly tested. No minimum saving. |
| B7 | [T58,M-B5a], 26.5 to 19.1 hours/vehicle. Unskilled production-crew proxy including catalogue's Machine Works. Inventory 96 to 81 days has no per-template dial, so no stock figure is invented. |
| B8 | [M-B9] gives adoption shares without fulfilment throughput, staff or inputs. Main has no Fulfilment Centre or its new mechanic. Pending naming the missing sourced recipe/template. |

★ E11 is scoped to PE24's catalytic process. Saved thermal remains
0.24 even with E11 known first. Both discovery orders reach 0.55.
PE24's existing undo is unchanged. Fees use the marginal actual process
yield and dispatched feed, including when E11 remains absent.

★ E19 is January 1981, monthKnown false. FPL-GTR-59 explicitly dates
first commercial OSB to 1981, replacing unsourced 1980. E20 stays
January 1970, monthKnown false. FPL-RP-656 dates pellets only to the 1970s.
No source dates exact 1970. Both work figures use study 1.8's engineering
estimate 400 * 1.053^(year-1950). Both fee flags use the study's
patentable-building estimate and the existing private billing route.

## Sources checked during this batch

- EPA ENERGY STAR Cement Guide 2013, table 1 and wet-kiln discussion.
  https://www.energystar.gov/sites/default/files/tools/ENERGY%20STAR%20Guide%20for%20the%20Cement%20Industry%2027_08_2013_Rev%20js%20reformat%2011192014.pdf
- NIST publication 820148, torch-on comparison.
  https://tsapps.nist.gov/publication/get_pdf.cfm?pub_id=820148
- OTA 1979, Benefits of Increased Use of Continuous Casting, part 3.
  https://www.princeton.edu/~ota/disk3/1979/7902/790203.PDF
- BLS Bulletin 1276, affected-unit and computer staff.
  https://fraser.stlouisfed.org/title/4552/fulltext
- SRI, Banking automation ERMA.
  https://www.sri.com/hoi/banking-automation-erma/
- Basker 2012, study source M-B4.
  https://www.nber.org/papers/w17825
- FPL-GTR-59, first commercial OSB 1981.
  https://www.fpl.fs.usda.gov/documnts/fplgtr/fplgtr59.pdf
- Spelter/Toth 2009, FPL-RP-656, pellets in the 1970s.
  https://www.fpl.fs.usda.gov/documnts/fplrp/fpl_rp656.pdf

## Verification

Final checks, comparison and coordinated gate are pending.
Baseline host PID 38520. No save slot is written.

The Commercial Bank has no Sector owner on main. Its B1/B3 estimates
therefore keep the existing non-Sector royalty exemption. Orchestrator
accepted that gap beside R2c-P (mail 174617); no bank payment route is added.

Initial fixture setup needed the same quiet streams as R2d's harness.
The catalyst ordering fixture also now stands before PE24's actual 1942
month, so withholding a local find really leaves PE24 unknown. Its
initial 1950 date could not cause that premise. No existing law was changed.

Focused postwar fixtures pass 82/82. The identical source compiled against
the retained whole main 32ec5a5d classes/data fails43/78 (wood candidate
checks are reached only when each new catalogue entry exists). All 28 row
transition assertions fail on main. Both composition directions, actual
clock, real petrol/fuel-oil change and positive E11 captured bill fail too.
Evidence is target/R2e-evidence/before/{compile,check}.log.
HistoricalRecipeCheck 120, HistoricalLicenceCheck 80, RecipeCheck,
EquipmentCheck, ResearchCheck 207 and DiscoveryCheck pass.

BuildingDataCheck kept its exact catalogue/fallback equality and exposed
the two built-in wood dates. The fallback now names E19/E20 too. Its
source comparison and dated-set law are preserved.

## Bank fixture restatement, accepted by the orchestrator

★ Mail 175317 settles the caused FittingCheck finding in the same
way as R2d's KeptCheck 96. The catalogue now gives the founding Bank
B1/B3 pieces. The actual 1900 founding must have those pieces unfitted.

Old section 3 assertion, verbatim
"the founding's own buildings (the bank, the builders' yard) carry no piece, and a village placed after it without construction comes as placed"

New section 3 assertion, verbatim
"the founding Bank is wholly unfitted for B1/B3, while the hand-placed village comes fitted"

It requires both actual Bank pieces, a positive standing quantity, both
unfitted counts equal to that quantity, every hand-placed village piece
fitted, and the existing empty driven-forms condition.

Section 4's existing fixture assertion and global law are unchanged
"fixture: a city founded the 1900 way is founded the 1900 way, with nothing unfitted and Industry and Agriculture held"
"...and nothing was unfitted by it: Industry's steam share is 0"

The builtVillage factory now explicitly fits only the founding Bank
before its Bakery experiment, causing that original all-fitted premise.
No no-other-completion observation, global !anyUnfitted assertion or
steam-share floor is changed. The real founding Bank is covered by
section 3 and PostwarRecipeCheck's B1/B3 transitions and both fitting orders.

The final postwar fixtures pass 85/85. On retained whole main code/data
they fail 45/81, including PE24's actual pre-zeolite fee (9,817,788.49
instead of the sourced marginal 7,991,638.97). E11's bill is 1,826,149.52
on its positive dispatched feed. The older 82-assertion results above
precede this added stage-fee control.

Final focused serial run is green. FittingCheck passes its unchanged
global all-fitted and no-other-completion laws with the explicit Bank
setup; PostwarRecipeCheck passes85/85. BuildingDataCheck and
LicenceFeesCheck passed in the preceding run. No fixture floor changed.
Logs are target/R2e-evidence/focused/{FittingCheck,PostwarRecipeCheck}.log
and BuildingDataCheck.log, LicenceFeesCheck.log in that same directory.
The before evidence is 45 failures of 81 on whole main 32ec5a5d.
The final --fast exits 0, regenerates maps and has zero firm Stale.

A JSON-value comparison confirms every top-level numeric modern anchor is
identical to 32ec5a5d. Original 89 catalogue entries are identical; only two
permanent codes append. Default LongPlaytest on 005c1d9f is now running,
serially after the focused job, under persistent host PID 31920.

## First review and calibrated process stages

REV-R2e-1 of005c1d9f found one new-cracker appraisal reader still passing
an undecorated base recipe into LicenceFees.one. The standing charge
already had the saved flags. forNew now resolves the same independent
decisions before pricing, so PE24's pre-zeolite marginal yield is
0.55 *55.5/62 -0.24 in both paths. New controls price the real prospective
plant and compare its patent charge with the captured production charge
at identical feed and price roles, for both PE24 and E11.

★ Orchestrator mail175932 scopes E2 after PE4. R2d calibrated all
missing kiln changes together to30% coal/clinker. E2 now leaves that old
figure intact until the long-kiln process is present. Only E2 missing
gives modern coal *7.0/3.3, both present gives modern coal exactly.
PE4's data and R1c's shaft recipe are unchanged.

★ Mail180337 accepts E8 and B7 after PE13 on the three vehicle
templates, preserving its earlier hand-assembly crew. Machine Works B7
is independent. E10 remains the accepted distinct wheat/corn crop-mix
estimate beside PE20.

★ PE19's row is the ratio for a distinct rolling operation,0.5 to8
tonnes/worker-shift, with +15 modern rolling crews (R2d-notes). E9's
continuous casting yield, direct crew and soaking/primary-rolling fuel
saving is separately sourced by OTA. E18's thin-slab reheating saving is
its own2.0 to0.5-0.8 MBtu/t ratio [T10,T3]. Neither earlier row calibrates
an all-missing1900 total of that quantity as PE4 did. These operations
therefore keep independent signed changes under mail180337's rule.

★ A scoped equipment gain uses the actual available prerequisite
fraction. Independent piece counts provide the aggregate product of
fractions, an explicit mixed-fleet estimate since no per-unit pairing is
saved. A new plant prices its own paying prerequisite through the existing
verdictForNew, so an older unfitted fleet cannot suppress that new plant's
preheater. Prospective and captured equipment fees use those same
contexts. The default founding guard remains the first branch.

The first complete branch default trace on005c1d9f exits0. All eight
CSVs are byte-identical to main32ec5a5d. Evidence and hashes are in
target/R2e-evidence/branch. The final gate will capture the final model's
eight files again after these scoped historical changes.

The scoped/appraisal fixtures pass102/102. The retained005c1d9f runtime
classes and packaged data, ahead of the final classes, fail exactly9/102.
The two PE24 appraisal failures quote9,849,080.66 instead of8,017,110.66.
The three vehicle hand-assembly controls fail, and the all-missing Cement
Plant consumes11,256.06 instead of its exact7,500 tonnes. The zero/partial
prerequisite gain controls fail too.
Evidence is target/R2e-evidence/review-before/{compile,check}.log.
Its commit.txt names005c1d9f; only the three changed model classes and
their packaged resources are overlaid, preserving all other classes.

The scope audit preserves the separate PE19 rolling ratio and E9/E18
operation savings. No PE4, PE13 or R1c recipe figure is changed.
The new-cracker comparison explicitly converts the standing total to
the proposed unit's feed and appraisal price roles before requiring
money equality. Both actual feed and both price differences are positive.

With all final scope controls included, whole main32ec5a5d fails56/98.
The branch passes102/102; the four additional wood-candidate assertions
run when the new catalogue entries exist. Main's baseline runtime and
data are unchanged. The retained pre-mend overlay fails9/102.
All ten serial named harnesses are green, including R2d's120/80 assertions.
A final fast rebuild and four focused readers repeat after the small
new-building prerequisite choice refinement and the explicit second kiln
order capture.

The final exact-source rebuild and serial PostwarRecipeCheck,
EquipmentCheck, LicenceFeesCheck and FittingCheck run all pass.
PostwarRecipeCheck remains102/102, main56/98, pre-mend9/102.
Final --fast exits0 with zero firm Stale. Source and record files are LF.
The scope/appraisal mends add no new pending or known-red row.
