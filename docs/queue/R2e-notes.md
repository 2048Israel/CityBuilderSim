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

★ New equipment uses R1c call10's capital estimate, 25% of plant cash
at MACHINERY13.4, 10% construction points, 300 months. These are estimates.
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
| E2 | Study [T32,T34], wet7.0 vs preheater3.0-3.6 GJ/t. Midpoint3.3 scales modern coal. Lime tonnes proxy clinker. PE4 stays a separate additive long-kiln estimate. No source measures nonchronological combinations. |
| E3 | [T21] gives water depth30 vs120-150 m, no comparable well-output ratio. Pending because the model lacks depth. |
| E4/E5/E6/E7 | Existing building gates already open pellets, container terminal, desalination and hydrocrackers. [T12], [T53,T54], [T52], [T30,T31] respectively. Keep modern figures when available. |
| E8 | [T57], also NIST publication820148, torch-on30% vs90%. Unskilled assembly posts proxy welding crew, old x3. No source partitions the plant crew. |
| E9 | Study3.1 [T4] estimates old -10% steel, +12.5% posts, +1.2 GJ/t fuel. Production bands only, same melt inputs. Fuel-oil proxy at R2d's36 MJ/L heating-value estimate. OTA itself gives10-12% yield improvement,10-15% less direct labour and about1 MBtu/short ton direct energy. Use the study's named rounded undo. |
| E10 | [T38] estimates doubled wheat yield with fertiliser. CROPS old half modern is a crop-mix estimate, additive beside PE20. Missing fertiliser good/works/prerequisite stays beside R2d-PE12. No modern fertiliser invoice exists to undo. |
| E11 | [T26], amorphous55.5 vs zeolite62.0% petrol. Normalize to modern0.55 *55.5/62. Lost petrol remains fuel oil, a mass-balance estimate. Model has no cracker coke stream. |
| E12/E13/E14/E16 | Existing gates open SX-EW, mini-mills, mechanised harvesting and wind. [T15,T16], [T3,T6], [T37], [T49,T50] respectively. No new predecessor or uncatalogued later wind technology. |
| E15 | [T17,T20],30 to149 t/face-worker/day and60 to80% recovery. Unskilled posts proxy face crews at fixed output. Dig1/0.6 before vs modern1/0.8. Loader/cutter independent. Whole-period productivity is a shield-crew proxy estimate. |
| E17 | [T55] gives twice the containers/train. Pending because all-cargo rail tonnes have no container share. |
| E18 | [T10,T3], reheating2.0 to0.5-0.8 MBtu/t, midpoint0.65. Add missing1.35 MBtu/t as fuel oil,1055.056 MJ/MBtu and36 MJ/L. Metric-tonne proxy estimate, additive beside E9. |
| B1 | BLS1276 [M-B1d], affected-unit staff -25%. DIPLOMA/COLLEGE_BUSINESS proxy clerks, old /0.75. SRI [M-B1c] posting245 to33000 accounts/hour measures one operation. BLS median29 computer staff has no per-template office scale. Record that gap without a staff subtraction. |
| B2/B4/B6 | Existing gates open Contact Centre, Walk-in Clinic and Shared Services. [M-B6], [R2], [R2]. These new buildings have no earlier instance. |
| B3 | [M-B3a], tellers20 to13. DIPLOMA posts proxy tellers, old x20/13. B1 composes additively. Branch-count growth is not a productivity multiplier. |
| B5 | [M-B4], study payroll reduction5.35%. Old posts /0.9465 at fixed band mix, one rounding. Convenience/Filling have5/3 posts and round to unchanged whole posts, explicitly tested. No minimum saving. |
| B7 | [T58,M-B5a],26.5 to19.1 hours/vehicle. Unskilled production-crew proxy including catalogue's Machine Works. Inventory96 to81 days has no per-template dial, so no stock figure is invented. |
| B8 | [M-B9] gives adoption shares without fulfilment throughput, staff or inputs. Main has no Fulfilment Centre or its new mechanic. Pending naming the missing sourced recipe/template. |

★ E11 is scoped to PE24's catalytic process. Saved thermal remains
0.24 even with E11 known first. Both discovery orders reach0.55.
PE24's existing undo is unchanged. Fees use the marginal actual process
yield and dispatched feed, including when E11 remains absent.

★ E19 is January1981, monthKnown false. FPL-GTR-59 explicitly dates
first commercial OSB to1981, replacing unsourced1980. E20 stays
January1970, monthKnown false. FPL-RP-656 dates pellets only to the1970s.
No source dates exact1970. Both work figures use study1.8's engineering
estimate400 *1.053^(year-1950). Both fee flags use the study's
patentable-building estimate and the existing private billing route.

## Sources checked during this batch

- EPA ENERGY STAR Cement Guide2013, table1 and wet-kiln discussion.
  https://www.energystar.gov/sites/default/files/tools/ENERGY%20STAR%20Guide%20for%20the%20Cement%20Industry%2027_08_2013_Rev%20js%20reformat%2011192014.pdf
- NIST publication820148, torch-on comparison.
  https://tsapps.nist.gov/publication/get_pdf.cfm?pub_id=820148
- OTA1979, Benefits of Increased Use of Continuous Casting, part3.
  https://www.princeton.edu/~ota/disk3/1979/7902/790203.PDF
- BLS Bulletin1276, affected-unit and computer staff.
  https://fraser.stlouisfed.org/title/4552/fulltext
- SRI, Banking automation ERMA.
  https://www.sri.com/hoi/banking-automation-erma/
- Basker2012, study sourceM-B4.
  https://www.nber.org/papers/w17825
- FPL-GTR-59, first commercial OSB1981.
  https://www.fpl.fs.usda.gov/documnts/fplgtr/fplgtr59.pdf
- Spelter/Toth2009, FPL-RP-656, pellets in the1970s.
  https://www.fpl.fs.usda.gov/documnts/fplrp/fpl_rp656.pdf

## Verification

Final checks, comparison and coordinated gate are pending.
Baseline host PID38520. No save slot is written.

The Commercial Bank has no Sector owner on main. Its B1/B3 estimates
therefore keep the existing non-Sector royalty exemption. Orchestrator
accepted that gap beside R2c-P (mail174617); no bank payment route is added.

Initial fixture setup needed the same quiet streams as R2d's harness.
The catalyst ordering fixture also now stands before PE24's actual1942
month, so withholding a local find really leaves PE24 unknown. Its
initial1950 date could not cause that premise. No existing law was changed.
