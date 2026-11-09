# R6 medicine: facts gathered 2026-11-09 (gatherer's notes; the study chooses and writes)

*A gatherer's notes for [the near-future study](../research-near-future.md) (R6, 2026-11-09), kept as gathered. A figure is unchecked unless it is marked page; the study chooses and writes, and its section 1 says how the marks read.*

Conventions. "checked: page" = I opened the page and saw the figure. "checked: snippet" = I saw it only in a search-result summary (the figure is then attributed to the page the summary named, but I did not open it). "[mem]" = from my own background knowledge, not checked in this session; do not ship a [mem] figure without a source. "not found" = I looked and could not find it. Several ClinicalTrials.gov pages were read through its public API URL (same record). PubMed was blocked by a cookie wall, so journal abstracts were read through Europe PMC's API (same abstract). Everything written in my own words. Today is 2026-11-09; the sources contain events up to Oct 2026.

---

### 1. Alzheimer's: lecanemab (Leqembi), donanemab (Kisunla), blood tests, prevention trials
today: Lecanemab: FDA accelerated approval Jan 2023, converted to traditional approval 6 Jul 2023 on CLARITY AD (n=1,795) [1]. Donanemab (Kisunla): FDA label page stamped 2 Jul 2024, trial n=1,736 (860 vs 876) [2]; UK authorisation Oct 2024 [6]. Both are for mild cognitive impairment or mild dementia with confirmed amyloid. Blood tests: Fujirebio Lumipulse pTau217/Abeta42 ratio, first FDA-cleared blood test, 16 May 2025, adults 55+ with symptoms, specialist setting, not a screen [3]; Roche Elecsys pTau181 cleared mid-Oct 2025 for primary care, adults 55+, rule-out use, negative predictive value 97.9% in 312 people [8].
slips: Not a withdrawal, but contested: an April 2026 Cochrane review of 17 anti-amyloid trials judged the benefit not clinically meaningful; the Alzheimer's Association disputed it [7]. Serious ARIA (brain swelling or bleeding) is a boxed warning; ApoE e4 homozygotes carry more risk [1]. Donanemab ARIA-E 24.0% vs 2.1% placebo [5]; lecanemab 12.6% with edema or effusions [4].
projected: Prevention trials in people with no symptoms: TRAILBLAZER-ALZ 3 (donanemab, NCT05026866) n=2,996, active not recruiting, primary completion Nov 2027 (estimated by the registry) [10]; AHEAD 3-45 (lecanemab, NCT04468659) n=1,400, active not recruiting, primary completion 21 Dec 2028, study completion 16 Jan 2031 (estimated) [9]. No body has said when a prevention indication could be ordinary care; not found.
effect: CLARITY AD, 18 months: CDR-SB change 1.21 vs 1.66 on placebo, difference -0.45 (95% CI -0.67 to -0.23), which is 27% less decline (my arithmetic 0.45/1.66) [4]. TRAILBLAZER-ALZ 2, 76 weeks: CDR-SB -0.70 (-0.95 to -0.45) and iADRS +2.92 combined group; +3.25 and -0.67 in the low/medium-tau group [5]; 3-year extension: -1.2 CDR-SB vs an external control (not randomised) [11]. Nursing-home entry has NOT been measured in a trial; only modelled: a Clarity-AD-based patient-level model gave institutional admission delayed about 0.6 years (6.25 vs 6.85) and roughly 5% of patients avoiding a nursing home; an earlier model gave lifetime institutional-care probability 25% vs 31% [12][13]. Group: a review said only 8-15% of people with early Alzheimer's meet trial-like eligibility [14]. Cost/uptake figures: not found.
grade: likely (approved for a narrow group; effect small; the delay-of-nursing-home claim is model-only)
sources:
[1] https://www.fda.gov/news-events/press-announcements/fda-converts-novel-alzheimers-disease-treatment-traditional-approval | FDA | regulator | checked: page
[2] https://www.fda.gov/drugs/news-events-human-drugs/fda-approves-treatment-adults-alzheimers-disease | FDA | regulator | checked: page (approval date not printed; page-current stamp 2 Jul 2024)
[3] https://www.fda.gov/news-events/press-announcements/fda-clears-first-blood-test-used-diagnosing-alzheimers-disease | FDA | regulator | checked: page (91.7% of positives and 97.3% of negatives confirmed, n=499, under 20% indeterminate)
[4] https://www.ebi.ac.uk/europepmc/webservices/rest/search?query=EXT_ID:36449413&resultType=core&format=json&pageSize=1 | NEJM 2023 abstract via Europe PMC (PMID 36449413) | journal paper | checked: page
[5] https://www.ebi.ac.uk/europepmc/webservices/rest/search?query=EXT_ID:37459141&resultType=core&format=json&pageSize=1 | JAMA 2023 abstract via Europe PMC (PMID 37459141) | journal paper | checked: page
[6] https://www.caducee.net/actualite-medicale/16107/alzheimer-le-donanemab-de-lilly-reduit-significativement-le-declin-cognitif-et-fonctionnel-dans-une-etude-de-phase-3.html | Caducee | press | checked: snippet
[7] https://www.healthday.com/health-news/neurology/new-alzheimers-drugs-provide-no-meaningful-benefit-major-evidence-review-concludes | HealthDay | press | checked: snippet
[8] https://www.alzheimer-europe.org/news/fda-approves-roches-blood-test-rule-out-ad-pathology-primary-care | Alzheimer Europe | press | checked: snippet
[9] https://clinicaltrials.gov/api/v2/studies/NCT04468659 | ClinicalTrials.gov | trial registry | checked: page
[10] https://clinicaltrials.gov/api/v2/studies/NCT05026866 | ClinicalTrials.gov | trial registry | checked: page
[11] https://pubmed.ncbi.nlm.nih.gov/41330788/ | PubMed | journal paper | checked: snippet
[12] https://pubmed.ncbi.nlm.nih.gov/37009976/ | PubMed (Neurology and Therapy 2023 model) | journal paper | checked: snippet; the 0.6-year and 5% figures were attributed by the search summary to https://www.carenet.com/news/general/carenet/56219 (press, snippet)
[13] https://pubmed.ncbi.nlm.nih.gov/35469060/ | PubMed (2022 model, 25% vs 31%) | journal paper | checked: snippet
[14] https://www.news-medical.net/news/20250924/Review-shows-lecanemab-and-donanemab-slow-decline-and-explains-who-should-get-them.aspx | News-Medical | press | checked: snippet

---

### 2. GLP-1 obesity drugs (semaglutide, tirzepatide, oral agents, retatrutide)
today: Oral Wegovy (semaglutide 25 mg pill) FDA approved 22 Dec 2025, first oral GLP-1 for obesity; over 600,000 US prescriptions since [1]. Orforglipron (Lilly, "Foundayo"), a once-daily non-peptide pill with no food rules: FDA approved 1 Apr 2026 (one report says 7 Apr) in 50 days under the national priority voucher; cash price $149-$349 a month [2]. Retatrutide (Lilly, triple agonist): TRIUMPH-1 reported 21 May 2026; TRIUMPH-2 and -3 reported 23 Jul 2026 [3]. Injectable Wegovy (2021) and Zepbound (Nov 2023) approvals: [mem], not checked.
slips: Retatrutide TRIUMPH-3 gave a heart-event hazard ratio of 1.12 (0.64-1.96) for MACE-3 (not powered, not a benefit) [3]. Semaglutide in Alzheimer's (EVOKE) reported no benefit in late 2025 [mem]. Adverse-event discontinuation in SELECT was 16.6% vs 8.2% on placebo [4].
projected: Retatrutide: Lilly said on 23 Jul 2026 it would file in Q1 2027 [3]. Generic semaglutide: Canada, Dr. Reddy's approved by Health Canada 28-29 Apr 2026, Apotex about 3 days later, shipping from about 19 May 2026, Apotex price about one third of the brand [5]; India, patent expired 20 Mar 2026 (the patent was refused in 2019) and generics launched within a day at under Rs 2,000 a month vs over Rs 10,000 [6]; China, patent expired 20 Mar 2026, a term extension was rejected Sep 2025, at least 17 generics in phase 3 [6]; Brazil, opens to generics from Mar 2026, Biomm with Biocon preparing [6]. US generic date: not found.
effect: SELECT (17,604 adults with overweight or obesity and heart disease, no diabetes): first heart attack, stroke or cardiovascular death 569 of 8,803 (6.5%) vs 701 of 8,801 (8.0%), hazard ratio 0.80 (0.72-0.90), about 1.5 points absolute (my arithmetic) [4]. FLOW (3,533 with type 2 diabetes and kidney disease): kidney-failure/kidney-death/kidney-decline composite 331 vs 410 events, HR 0.76 (0.66-0.88); death from any cause HR 0.80 (0.67-0.95); major cardiovascular events HR 0.82 [7]. Weight: retatrutide 21.6% and 22.6% (9 and 12 mg) vs 3.2% placebo in TRIUMPH-3 at 80 weeks, up to 20.8% in TRIUMPH-2 [3]; orforglipron 12.4% vs 0.9% at top dose (on-treatment) or 11.2% in the NEJM-based figure [2]. Hospital-stay and days-off-work effects: not found. Group: US adults with obesity 41.9% (2017-Mar 2020), severe obesity 9.2% [8]; the labels also cover overweight with a condition, so eligible share is above the obesity share (figure for it: not found).
grade: firm (injectables and the first pills approved and in use; heart and kidney benefit measured); retatrutide itself likely (phase 3 done, filing Q1 2027)
sources:
[1] https://medicaldialogues.in/amp/medicine/news/fda-approves-orforglipron-as-second-oral-glp-1-agonist-for-weight-loss-167778 | Medical Dialogues | press | checked: snippet
[2] https://www.healio.com/news/endocrinology/20260401/fda-approves-orforglipron-an-oral-glp1-for-adults-with-obesity | Healio | press | checked: snippet (also https://6abc.com/amp/post/fda-grants-speedy-approval-eli-lillys-weight-loss-glp-1-pill-foundayo-obesity/18823509/ | AP via 6abc | press | snippet)
[3] https://www.hcplive.com/view/retatrutide-meets-phase-3-weight-loss-endpoints-in-2-obesity-trials | HCPLive, reporting Lilly's 23 Jul 2026 release | press | checked: snippet (Lilly's own page: https://investor.lilly.com/node/54721 | announcement | snippet)
[4] https://www.ebi.ac.uk/europepmc/webservices/rest/search?query=EXT_ID:36449413%20OR%20EXT_ID:37952131&resultType=core&format=json&pageSize=5 | NEJM 2023 SELECT abstract via Europe PMC (PMID 37952131) | journal paper | checked: page
[5] https://www.pearceip.law/2026/04/29/first-generic-ozempic-semaglutide-approved-in-canada-for-dr-reddys-apotex/ | Pearce IP | law-firm note | checked: snippet
[6] https://www.malaymail.com/news/world/2026/03/20/india-set-to-roll-out-cheap-weight-loss-jabs-as-demand-rises-amid-obesity-surge/213345 | Malay Mail | press | checked: snippet; China/Brazil: https://www.pulse.bot/healthcare/news/ozempic-is-about-to-go-generic-in-india-china-and-canada-ae667d43-b056-4749-9172-b77cb2a0eb33 | Pulse | press | snippet
[7] https://www.ebi.ac.uk/europepmc/webservices/rest/search?query=EXT_ID:38785209&resultType=core&format=json&pageSize=1 | NEJM 2024 FLOW abstract via Europe PMC (PMID 38785209) | journal paper | checked: page
[8] https://www.cdc.gov/obesity/adult-obesity-facts/index.html | CDC | US-government page | checked: page

---

### 3. Pig-kidney transplants (xenotransplantation)
today: United Therapeutics' EXPAND (UKidney, 10 gene edits, NCT06878560): registry start 29 Oct 2025, first transplant announced 3 Nov 2025 at NYU Langone (NYU's ninth xenotransplant); first cohort 6 transplants at 2 centres with a 12-week wait between the first and second; registry lists 50 (estimated), recruiting [1][2]. eGenesis (EGEN-2784, 69 edits): FDA cleared a phase 1/2/3 IND on 8 Sep 2025 for dialysis patients aged 50+ on the waitlist, efficacy read at 24 weeks; AP in 2026 said 30 people [3][4]. Recipients: MGH first living recipient (2024) kidney worked 52 days, died of an unrelated cardiac cause [5]; Towana Looney (NYU, UKidney, Nov 2024) about 4 months 9 days before rejection, back on dialysis [6]; Tim Andrews (MGH, Jan 2025, under expanded access) 271 days off dialysis, then microvascular injury, graft removed, human kidney in Jan 2026 [5]; Bill Stewart (MGH, 14 Jun 2025) outcome not found [7]. A Lancet paper of 4 Sep 2026 reports 2 of 5 eGenesis recipients bridged to a human kidney [8].
slips: Kidneys have lasted months, not years; every graft above except those still functioning was lost or the patient died (52 d, ~130 d, 271 d). No stated approval date found.
projected: Sponsor: after FDA review of the first six, expand the protocol to support a Biologics License Application [1]; registry primary completion Oct 2028 (estimated), study completion Oct 2075 (long follow-up) [2]. National Kidney Foundation page (checked Mar 2026) quotes an expert "optimistic" about availability in 5-10 years, i.e. about 2031-2036 [9]. Nobody found stating an approval year.
effect: Per patient a dialysis patient is freed for months (271 days best). US need: kidney waiting list 94,337 unique candidates (OPTN snapshot 4 Jan 2026) [10]; OPTN/SRTR 2024 report counts 143,982 adult candidates listed (multiple listings) [11]; US end-stage kidney disease prevalence 808,536 at end of 2021, includes transplant recipients (USRDS, URL not isolated) [12]; dialysis-only count: 554,038 at end of 2018 (old); current dialysis count: not found.
grade: speculative (first trials, 6 to 50 patients, graft lives in months)
sources:
[1] https://www.businesswire.com/news/home/20251103435431/en/United-Therapeutics-Corporation-Announces-First-Transplant-in-EXPAND-Clinical-Trial-of-UKidney-in-Patients-With-End-Stage-Renal-Disease | United Therapeutics | announcement | checked: snippet
[2] https://clinicaltrials.gov/api/v2/studies/NCT06878560 | ClinicalTrials.gov | trial registry | checked: page
[3] https://egenesisbio.com/press-releases/egenesis-announces-ind-clearance-for-egen-2784-in-kidney-transplant-and-landmark-patient-updates-in-ongoing-expanded-access-study/ | eGenesis | announcement | checked: snippet
[4] https://abc7.com/post/pig-kidney-trasnplant-bill-stewart-dover-new-hampshire-is-2ns-mass-general-patient-get-surgery-clinical-trial/17779274/ | AP via ABC7 | press | checked: snippet (the 30-person figure is from an AP piece the summary dated 2026)
[5] https://news.massgeneralbrigham.org/en/pig-kidney-xenotransplant-bridge-to-human-transplant | Mass General Brigham | announcement | checked: page
[6] https://www.kidney.org/news-stories/two-kidney-xenotransplant-patient-stories | National Kidney Foundation | press | checked: snippet
[7] same AP piece as [4]
[8] https://www.news-medical.net/news/20260903/Pig-kidney-xenotransplantation-offers-a-bridge-for-patients-awaiting-human-donor-kidneys.aspx | News-Medical | press | checked: snippet
[9] https://www.kidney.org/node/154002 | National Kidney Foundation | press | checked: snippet
[10] https://optn.transplant.hrsa.gov/data | OPTN/HRSA | US-government page | checked: snippet
[11] https://srtr.hrsa.gov/adr/2024/Kidney/ | SRTR/HRSA | US-government page | checked: snippet
[12] USRDS (https://www.niddk.nih.gov/-/media/Files/USRDS/2019/2019-executive-summary.pdf is the 2018 figure) | NIDDK | US-government page | checked: snippet; 2021 figure's exact page: not found

---

### 4. Personalised mRNA cancer vaccines (intismeran autogene, mRNA-4157/V940; BioNTech)
today: Phase 3 INTerpath-001 (intismeran + Keytruda vs Keytruda alone, completely resected stage IIB-IV melanoma, n=1,137, 2:1): Merck and Moderna announced 19 Aug 2026 that it met its primary endpoint (recurrence-free survival) and key secondary (distant-metastasis-free survival); overall survival still maturing [1]. Companies said they will "engage with regulators" on filings, no dates given [1]. The programme has nine phase 2/3 trials across melanoma, lung, bladder and kidney cancer [1].
slips: Early benefit absent: in the first 6 months the arms were level (HR 0.99, 0.66-1.49), the effect shows after month 6 [2]. Phase 2b overall-survival HR 0.471 (0.165-1.345) is a trend, not conclusive [2]. BioNTech autogene cevumeran (pancreatic, others): status not found.
projected: Detailed phase 3 data at ESMO 2026 (October) and accepted for NEJM [2]; filing and approval dates: not found (Merck's Q3 2026 earnings transcript of 29 Oct 2026, https://www.merck.com/wp-content/uploads/sites/124/2026/10/MRK-USQ_Transcript_2026-10-29.pdf, appeared in search and may say; I did not open it). Other phase 3 read-out dates (lung, kidney, bladder): not found.
effect: Phase 2b KEYNOTE-942 at 5 years (presented ASCO 2026, published J Clin Oncol): recurrence or death HR 0.51 (0.294-0.887), 49% lower; distant metastasis or death HR 0.411 (0.200-0.843), 59% lower; 5-year overall survival 92.2% vs 71.3% (exploratory) [1][3]. The 3-year figure (44% lower, HR 0.56) is [mem], unchecked. Phase 3 at median 24.6 months follow-up: recurrence or death HR 0.58 (0.45-0.75), 42% lower; after month 6 HR 0.42 (0.30-0.58) [2]. Group: resected high-risk melanoma only (share of adults tiny); cancer is a large cause of deaths but this covers one cancer type so far.
grade: likely (phase 3 positive on the primary endpoint, filing not yet dated)
sources:
[1] https://www.merck.com/news/merck-and-moderna-announce-phase-3-interpath-001-trial-of-intismeran-autogene-plus-keytruda-met-endpoints-of-recurrence-free-survival-rfs-and-distant-metastasis-free-survival-dmfs-in-patient/ | Merck | announcement | checked: page (the release itself prints no phase 3 hazard ratios; its 5-year numbers are the phase 2b ones)
[2] https://www.onclive.com/view/intismeran-autogene-plus-pembrolizumab-meets-rfs-dmfs-end-points-in-resected-melanoma | OncLive | press | checked: snippet (phase 3 HRs also in Moderna's recap https://www.modernatx.com/recapping-the-phase-three-interpath-results | announcement | snippet)
[3] https://managedhealthcareexecutive.com/view/five-years-later-personalized-mrna-vaccine-cuts-melanoma-recurrence-risk-by-half | Managed Healthcare Executive | press | checked: snippet (OS 92.2 vs 71.3); also https://www.merck.com/news/moderna-and-merck-present-5-year-data-for-intismeran-autogene-in-combination-with-keytruda-pembrolizumab-in-patients-with-high-risk-stage-iii-iv-melanoma-following-complete-resection-at-the-20/ | Merck | announcement | snippet

---

### 5. Multi-cancer blood tests (Galleri and others)
today: NHS-Galleri (GRAIL), 142,250 people aged 50-77 in England, three annual blood draws; full results at ASCO on 30 May 2026 [1]. Galleri is not FDA cleared or approved (GRAIL's own page says so) [1].
slips: The primary endpoint failed: the combined stage III-IV diagnoses across 12 cancers did not fall, incidence rate ratio 1.03 (0.92-1.14, p=0.63) [1]. GRAIL says stage IV cases shifted earlier and stage III rose, and expects control-arm late-stage cases to catch up with longer follow-up [1]. The FDA filing (a PMA): not found.
projected: No dated step found for an FDA decision or NHS roll-out. Cancer-death results: none reported yet [1]. PATHFINDER 2 and the Medicare study: not found in what I read.
effect: Stage IV diagnoses 14% lower overall (IRR 0.86, 0.744-0.998, only nominally significant); by round 9%, 22%, 26% (third round IRR 0.74, 0.57-0.95); oesophageal stage IV down 57.1% and colorectal 34.4% in exploratory analysis [1]. Stage I-II diagnoses in the 12 cancers up 16%; specificity 99.55%; positive predictive value 52.0% [2]. Group: adults 50-77; GRAIL says over 380,000 participants across its studies [1].
grade: speculative (the trial's own primary endpoint failed; mortality unknown)
sources:
[1] https://grail.com/press-releases/grail-reports-full-results-from-nhs-galleri-trial-demonstrating-substantial-reduction-in-stage-iv-cancer-diagnoses-at-2026-asco-annual-meeting/ | GRAIL | announcement | checked: page
[2] https://clpmag.com/disease-states/cancer/galleri-multi-cancer-blood-test-reduces-stage-iv-diagnoses-nhs-trial/ | Clinical Lab Products | press | checked: snippet

---

### 6. One-time cholesterol editing, long-acting LDL and Lp(a) drugs
today: VERVE-102 (PCSK9 base editor, now Lilly's): interim phase 1b, 35 adults, released 25 May 2026 at the European Atherosclerosis Society; FDA Fast Track; Lilly plans to start phase 2 enrolment by end of 2026 [1][2]. Inclisiran (Leqvio, twice-yearly injection) is approved [mem: Dec 2021]; its two outcome trials ORION-4 (NCT03705234) and VICTORION-2-PREVENT (NCT05030428) were guided by Novartis in Aug 2024 to read out in 2026 and 2027; no result found [3]. Lp(a): pelacarsen outcome trial Lp(a)HORIZON (n=8,323) topline 4 Sep 2026 [4]; olpasiran OCEAN(a)-Outcomes (n=7,297); lepodisiran ACCLAIM-Lp(a) (about 17,300) [4].
slips: Pelacarsen failed its primary endpoint: it lowered Lp(a) but not cardiovascular events (secondary-prevention patients), so the Lp(a) idea is unproven; analysts cut olpasiran's success odds from 50% to 20% in one firm's estimate [4][5]. Whether deeper siRNA lowering (olpasiran, lepodisiran) will do better is open.
projected: Olpasiran primary completion about Mar 2028; lepodisiran completion Mar 2029 [4][5]; inclisiran ORION-4 "2026", V2P "2027" per Novartis (Aug 2024; a newer date: not found) [3]. VERVE-102 outcome-based approval: no date; phase 3 not started. Nobody found saying when gene-edited LDL lowering could be ordinary care.
effect: VERVE-102: LDL-C mean reduction 9% to 62% by dose (62%, 78 mg/dL at top dose), PCSK9 down up to 88%, durable to 18 months, median follow-up about 9 months, no deaths or dose-limiting toxicity; not designed to show fewer heart attacks [1][2]. Pelacarsen phase 2: Lp(a) down 72% monthly, 80% weekly (so its failure may be a dose-depth issue) [4]. Inclisiran roughly 50% LDL lowering [mem]; the monotherapy trial (V-MONO, 28 Aug 2024 release) gave clinically meaningful LDL cuts, figure not read [3]. Group: people with high LDL or Lp(a); share of adults with Lp(a) high: not found.
grade: speculative for gene editing and Lp(a) (phase 1b; first outcome trial negative); inclisiran likely (approved, outcomes pending)
sources:
[1] https://www.biospace.com/press-releases/a-single-dose-of-lillys-pcsk9-base-editor-verve-102-reduced-pcsk9-by-up-to-88-and-ldl-c-by-up-to-62-with-durable-effects-supporting-its-potential-as-a-one-time-treatment-for-hypercholesterolemia | Lilly via BioSpace | announcement | checked: snippet
[2] https://www.hcplive.com/view/verve-102-lowers-pcsk9-ldl-c-interim-phase-1b-hypercholesterolemia-data | HCPLive | press | checked: snippet
[3] https://www.novartis.com/news/media-releases/novartis-twice-yearly-leqvio-demonstrated-clinically-meaningful-statistically-significant-ldl-c-lowering-monotherapy-patients-low-or-moderate-ascvd-risk | Novartis | announcement | checked: snippet
[4] https://www.lipid.org/resource/top-line-results-from-the-phase-3-lpahorizon-trial/ | National Lipid Association | professional society | checked: snippet (olpasiran and lepodisiran sizes and dates came from the same search summary; the specific page not identified)
[5] https://www.tctmd.com/news/full-lpahorizon-results-affirm-lack-pelacarsen-benefit-secondary-prevention | TCTMD | press | checked: snippet

---

### 7. CRISPR and base editing beyond the first (Casgevy, bespoke edits, in-body therapies)
today: Casgevy (Vertex/CRISPR Therapeutics) approved Dec 2023 [mem for the exact FDA date]; CRISPR Therapeutics reported Casgevy revenue $54 million in Q4 2025 and $116 million for 2025, "growth in treated patients" [1]. Baby KJ (CPS1 deficiency): the first personalised in-body CRISPR therapy, May 2025 [2]. FDA draft guidance of 23 Feb 2026 on individualised genome-editing and RNA therapies, the "plausible mechanism" framework: approval without randomised trials where the cause is known and the therapy hits it; platform-level authorisation possible after several successes; officials say evidence standards are unchanged [2][3]. Intellia lonvo-z (hereditary angioedema, one-time in vivo CRISPR): phase 3 in April 2026, rolling BLA started, filing to complete 2H 2026 [4].
slips: Casgevy patient counts conflict across sources (64 US patients treated in 2025 vs over 200 worldwide); neither verified [1]. A single source says the Casgevy label was extended to children 2+ on 1 Jul 2026: unverified. Intellia's nex-z (heart amyloidosis) phase 3 read-out date has not been disclosed [4]. Experts dispute whether the FDA framework is a new pathway or existing ones [3].
projected: Lonvo-z US launch 1H 2027 if approved (Intellia, via STAT+, 2026) [4]. Next bespoke edits: not found (search for CPS1 follow-on patients returned nothing).
effect: Lonvo-z phase 3: 87% fewer attacks than placebo, 62% attack-free and off other therapy 6 months after treatment [4]. Casgevy: sickle-cell trial patients stayed free of pain crises at 2 years (91 patients per one blog; unverified) [1]. Groups are small: sickle cell, thalassaemia, hereditary angioedema and ultra-rare disease; these move few deaths or hospital days at city scale.
grade: firm for Casgevy (approved, in use, small group); likely for lonvo-z; speculative for bespoke and platform edits
sources:
[1] https://www.biospace.com/press-releases/crispr-therapeutics-provides-business-update-and-reports-fourth-quarter-and-full-year-2025-financial-results | CRISPR Therapeutics | announcement | checked: snippet
[2] https://crisprmedicinenews.com/news/fda-pathway-could-make-bespoke-crispr-therapies-viable/ | CRISPR Medicine News | press | checked: snippet
[3] https://www.fiercebiotech.com/biotech/fda-illuminates-new-approval-pathway-bespoke-gene-therapies | Fierce Biotech | press | checked: snippet
[4] https://www.pulse.bot/pharma/news/stat-intellia-says-crispr-based-treatment-for-rare-disease-reduced-swelling-attacks-in-pivotal-trial-15037c74-b5b3-4dce-a8f8-01f6f6ec87d7/ | STAT+ via Pulse | press | checked: snippet

---

### 8. Better flu vaccines (mRNA; universal candidates)
today: Moderna's mRNA flu vaccine mFLUSIVA (mRNA-1010) FDA approved 5 Aug 2026 for adults 50+: traditional approval for 50-64, accelerated approval for 65+ on immune response (a 2,992-person US trial against a high-dose vaccine) with a confirmatory study required [1][2]. Pivotal trial: 40,805 adults 50+ in 11 countries, 20,350 vs 20,353 on Fluarix (a standard-dose shot) [1]. Also under review in the EU, Canada and Australia [2].
slips: FDA first issued a refusal-to-file letter in Feb 2026 (comparator concern), reversed after a Type A meeting, set a 5 Aug 2026 goal date; the advisory panel (VRBPAC) met 18 Jun 2026 and voted in favour [1][2][3]. HHS wound down BARDA mRNA vaccine development in Aug 2025 [4]. The universal-flu field has a long record of not delivering (one commentator) [4].
projected: Universal flu: HHS/NIH "Generation Gold Standard" (May 2025, $500 million, whole-virus inactivated BPL platform): reporting says clinical trials in 2026 and FDA approval targeted for 2029 [4][5]. BPL-1357 (intranasal) finished a 45-person phase 1 (NCT05027932) [5]. No 2026 progress report found. Wider mRNA flu rollout dates: not found.
effect: Relative vaccine efficacy against the standard-dose shot 26.6% (95% CI 16.7-35.4) in adults 50+, i.e. 26.6% fewer flu cases than the standard vaccine, per the FDA clinical review; met all three prespecified criteria [1]. Absolute attack rates, hospitalisations and deaths avoided: not found. Group: adults 50+ (an age band where flu deaths cluster).
grade: firm for mFLUSIVA ages 50-64 (approved), likely for 65+ (accelerated approval); speculative for universal flu
sources:
[1] https://www.fda.gov/media/194140/download | FDA (clinical review of 5 Aug 2026) | regulator | checked: snippet (the 26.6% and CI as quoted by the search summary)
[2] https://www.hcplive.com/view/mflusiva-fda-approval-influenza-adults-50-older | HCPLive | press | checked: snippet; also https://products.modernatx.com/mflusivapro/efficacy | Moderna | announcement | snippet
[3] https://pharmacytimes.com/view/fda-approves-mflusiva-the-first-mrna-based-influenza-vaccine | Pharmacy Times | press | checked: snippet
[4] https://www.hhs.gov/press-room/hhs-nih-announces-generation-gold-standard.html | HHS | US-government page | checked: snippet
[5] https://www.beckershospitalreview.com/quality/public-health/hhs-nih-to-develop-universal-flu-vaccines-targeting-multiple-strains | Becker's | press | checked: snippet

---

### 9. Tuberculosis: M72/AS01E vaccine and shorter drug regimens
today: M72/AS01E phase 3 (NCT06062238): 20,080 enrolled (actual), started 5 Mar 2024, active not recruiting, enrolment finished about April 2025 (11 months early), 54 sites in South Africa, Kenya, Malawi, Zambia, Indonesia; funded by the Gates Foundation and Wellcome [1][2][3]. Shorter drug regimen BPaLM (bedaquiline, pretomanid, linezolid, moxifloxacin) recommended by WHO for drug-resistant TB in 2022 (BPaL May 2022; BPaLM [mem] later in 2022) [4].
slips: The trial was first planned for 26,000 before the protocol was refined to 20,000 [3]. The phase 2b "about 50%" is a single trial in already-infected adolescents and adults [mem for the population]. A 2028 results date was NOT confirmed beyond the registry's.
projected: Registry primary completion Apr 2028 (estimated) [1]; STAT said the trial takes 3-5 years depending on how fast TB cases accrue, so a later read-out is possible [3]. No licensed-vaccine date from any body; not found.
effect: Phase 2b: about 50% protection against pulmonary TB disease over three years (the sources say "about 50%", I could not confirm 49.7%) [2][3]. Phase 3 is powered to detect about 50% [3]. BPaLM: 6 months vs the older 9, 18-24 months; TB-PRACTECAL treatment success about 89% vs 52% on standard care; ZeNix about 90% of 109 patients cured with 6-9 months (a different endpoint) [5]. Group: people with drug-resistant TB (small); the vaccine would reach adolescents and adults in high-burden countries.
grade: likely for M72 (phase 3 under way with a registered read-out); firm for BPaLM
sources:
[1] https://clinicaltrials.gov/api/v2/studies/NCT06062238 | ClinicalTrials.gov | trial registry | checked: page
[2] https://www.gatesmri.org/the-phase-3-clinical-trial-of-the-m72-aso1e-tuberculosis-tb-vaccine-candidate-has-reached-full-enrollment/ | Gates Medical Research Institute | announcement | checked: snippet
[3] https://www.statnews.com/2025/04/28/major-tuberculosis-vaccine-trial-completes-enrollment-faster-than-expected/ | STAT | press | checked: snippet
[4] https://tbksp.who.int/en/node/576 | WHO | regulator-type (WHO guidance) | checked: snippet
[5] https://www.doctorswithoutborders.org/latest/who-recommends-tb-regimen-found-effective-msf-clinical-trial | MSF | announcement | checked: snippet (also https://www.tballiance.org/wp-content/uploads/2024/10/TBAlliance_BPaL-ZeNix-Practecal-WHO.pdf | TB Alliance | announcement | snippet)

---

### 10. AI reading of scans and screening (MASAI mammography; AI without a doctor)
today: MASAI, randomised screening trial in Sweden (Lund), AI used to sort scans to single or double reading and to flag suspicious ones; results in The Lancet Jan 2026 [1][2]. FDA has cleared assistive chest X-ray AI (Qure.ai qXR-Detect, Feb 2026; DeepHealth/Gleamer, Sep 2026) that flags findings for clinicians [3][4]. A tool that issues a report with no doctor: not found as an FDA clearance in 2026; [mem] a European CE-marked autonomous normal-chest-X-ray tool (Oxipit) exists since 2022, unchecked.
slips: Interval-cancer result is "non-inferior", not better (p=0.41); one screening round only; resource-poor settings may not be able to run it [1]. The earlier 44% workload figure is from the 2023 interim report, not the 2026 paper [1].
projected: No regulator date for autonomous mammography found. Screening programmes adopting AI-supported double-reading: dates not found.
effect: Interval cancers 1.55 vs 1.76 per 1,000 screened (proportion ratio 0.88, 0.65-1.18); sensitivity 80.5% (76.4-84.2) vs 73.8% (68.9-78.3), p=0.031; radiologist reading workload down 44% (interim report); 29% more cancers detected without more false positives (earlier analysis) [1][2]. No effect on hospital days or deaths measured. Group: women in breast screening (about the full screened age band).
grade: likely (large randomised trial positive on workload and sensitivity; autonomous reading unapproved)
sources:
[1] https://ascopost.com/news/february-2026/randomized-trial-shows-ai-supported-mammography-improves-sensitivity-and-lowers-interval-cancer-rate | The ASCO Post | press | checked: snippet
[2] https://www.lunduniversity.lu.se/article/ai-support-breast-cancer-screening-fewer-missed-cancer-cases | Lund University | university release | checked: snippet
[3] https://www.diagnosticimaging.com/view/fda-clears-emerging-ai-powered-cxr-software-qure-ai | Diagnostic Imaging | press | checked: snippet
[4] https://www.appliedradiology.com/news/deephealth-receives-fda-clearance-for-chest-x-ray-ai-solution | Applied Radiology | press | checked: snippet

---

### 11. Hospital care at home
today: Medicare's Acute Hospital Care at Home waiver now runs to 30 Sep 2030 (the Consolidated Appropriations Act 2026, P.L. 119-75, signed about 3 Feb 2026; the bill text strikes 30 Jan 2026) [1][2]. It lapsed briefly in late Jan 2026 during a shutdown and some systems paused new admissions (UMass Memorial stopped on 28 Jan) [1]. The law requires a federal study comparing quality and cost with inpatient care.
slips: The waiver had been extended only in short steps before (previous end date 30 Jan 2026) and lapsed once [1][2]. The evidence base is small randomised trials.
projected: The extension's own study results: dates not found. Payment beyond 2030: not found.
effect: Levine et al., Annals of Internal Medicine, Jan 2020, randomised, n=91 (43 home, 48 hospital): total direct cost 38% lower at home; 30-day readmission 7% vs 23%; length of stay not reported in the pieces I read (an earlier non-randomised study cited 3.2 vs 4.9 days) [3][4]. Advocates (AAMC and 100+ organisations) claim lower cost and shorter stays; that is advocacy, not a result [1]. Group: patients with selected acute conditions (heart failure, pneumonia, COPD, cellulitis); share of admissions suitable: not found.
grade: firm (in use, legal to 2030; the evidence is small)
sources:
[1] https://www.ama-assn.org/public-health/population-health/lawmakers-extend-cms-hospital-home-waiver-five-years | AMA | professional society | checked: snippet
[2] https://www.govinfo.gov/content/pkg/BILLS-119hr4313eh/pdf/BILLS-119hr4313eh.pdf | US Congress (H.R. 4313 text) | US-government page | checked: snippet
[3] https://news.harvard.edu/gazette/story/2019/12/home-hospital-model-reduces-costs-by-38-improves-care/ | Harvard Gazette | university release | checked: snippet
[4] https://www.eurekalert.org/news-releases/724220 | Brigham and Women's via EurekAlert | announcement | checked: snippet

---

### 12. Antibiotic resistance: the threat and the new drugs
today: Threat (GRAM, Lancet, Sep 2024): 1.14 million deaths directly due to resistant infections in 2021 and 4.71 million where resistance contributed [1]. New drugs: gepotidacin (Blujepa) FDA approved Mar 2025 for uncomplicated urinary infections and, with zoliflodacin (Nuzolvence), for uncomplicated gonorrhoea on 11-12 Dec 2025, the first oral gonorrhoea treatments since 1987; zoliflodacin cleared the bacteria in 91% vs 96% on standard treatment [2]. Zosurabalpin (Roche, a new class against carbapenem-resistant Acinetobacter): phase 3 of about 400 patients planned to start late 2025 or early 2026; start unconfirmed [3].
slips: Gepotidacin is reserved for people with few options because of limited safety data [2]. The zosurabalpin start date and phase 3 status: not confirmed. Whether any of these are new classes against the Gram-negative killers (the GRAM concern): only zosurabalpin is; not confirmed in use.
projected: GRAM forecast: 1.91 million attributable deaths a year by 2050 (+67.5% on 2021), 8.22 million associated; 39 million attributable and 169 million associated deaths cumulatively 2025-2050 [1][4]. GRAM scenario: better care and access could avert 92 million deaths by 2050; a steady flow of new Gram-negative drugs up to 11 million [4]. Roche hopes for zosurabalpin approval "by the end of the decade" (stated 2025) [3].
effect: Group: all ages, concentrated in infants and the elderly [mem for the age pattern]. Effect of the new drugs on deaths: not measured (gonorrhoea and urinary infections are not major killers); the killer infections (blood, lung, hospital-acquired Gram-negative) are what zosurabalpin targets.
grade: threat firm (measured baseline); discovery likely for the gonorrhoea/UTI drugs (approved), speculative for the Gram-negative breakthrough
sources:
[1] https://www.ox.ac.uk/news/2024-09-17-antibiotic-resistance-has-claimed-least-one-million-lives-each-year-1990 | University of Oxford (GRAM partner) | university release | checked: snippet
[2] https://www.eatg.org/hiv-news/fda-approves-two-oral-therapies-to-treat-gonorrhea/ | EATG | press | checked: snippet
[3] https://www.cidrap.umn.edu/antimicrobial-stewardship/roche-launch-phase-3-trial-new-antibiotic-targeting-acinetobacter | CIDRAP | press | checked: snippet (also https://pharmaphorum.com/news/roche-takes-new-antibiotic-phase-3-urgent-threat | snippet)
[4] https://cosmosmagazine.com/health/medicine/more-than-39-million-deaths-from-antimicrobial-resistance-estimated-by-2050 | Cosmos | press | checked: snippet

---

### 13. Lab-grown or printed organs; artificial kidneys
today: Real: only xenotransplants (item 3) are in people. No human trial of an implantable bioartificial kidney, a lab-grown organ or a bioprinted organ found. The UCSF/Vanderbilt Kidney Project showed an implanted bioreactor with kidney cells survived and worked in pigs for 7 days (2023), without immune reaction; next step was month-long animal tests required by the FDA before any human trial [1][2]. A USC-led NIH-funded project builds kidneys from progenitor cells, with "no proven success in humans" [2].
slips: The Kidney Project's earlier aim of human trials "this decade" is not confirmed to have started [1]. Miromatrix and bioprinting: not found (my search returned nothing on them).
projected: No dated human first-in-man step found, no body stating when. Treat as 2040s or later as a design choice, not as a sourced date.
effect: No human efficacy figures exist. Group if it worked: US end-stage kidney disease patients (see item 3 for counts).
grade: speculative
sources:
[1] https://physicsworld.com/a/implantable-bioartificial-kidney-aims-to-free-patients-from-dialysis/ | Physics World | press | checked: snippet
[2] https://www.news-medical.net/news/20230829/UC-San-Francisco-scientists-develop-implantable-bioreactor-to-treat-kidney-failure.aspx | News-Medical (UCSF release) | press | checked: snippet

---

## Extra treatments (budget allowed one)

### 14. RSV vaccines for adults 60+ (newly licensed 2023, in use)
today: Arexvy (GSK), Abrysvo (Pfizer) and mRESVIA (Moderna) are licensed for older adults [mem for the three names and 2023 dates; not checked here]. Real-world studies now measure hospital protection.
slips: Protection wanes: a CDC VISION analysis found 62% (56-67%) against RSV hospitalisation at a median of 279 days after the shot, and a two-season US study found 69% in the vaccination season but 48% for the prior season [1][2].
projected: Immunocompromised adults gain less (30% in the two-season US study, CI -9% to 55%; 69% vs 80% in the meta-analysis) [1][3]. Revaccination schedule: not found.
effect: Pooled real-world effectiveness against RSV hospitalisation 77% (8 studies, about 1.5 million older adults, 2026 meta-analysis) and 74.8% in another review of 6 studies; 75% (50-87%) in the first US season; 58% across two seasons [1][2][3]. Group: US adults 60+ (about one in four adults). Hospital-day and death counts avoided: not found.
grade: firm (approved, in use)
sources:
[1] https://pubmed.ncbi.nlm.nih.gov/40884491/ | PubMed (JAMA Netw Open-type US two-season study) | journal paper | checked: snippet
[2] https://pmc.ncbi.nlm.nih.gov/articles/PMC12793146/ | CDC VISION network abstract | US-government-funded abstract | checked: snippet
[3] https://pmc.ncbi.nlm.nih.gov/articles/PMC13189658/ | PMC meta-analysis | journal paper | checked: snippet

Candidates I did NOT research (budget), only suggested: twice-yearly lenacapavir for HIV prevention [mem: FDA 2025]; CETP inhibitor obicetrapib (outcomes trial); semaglutide-type drugs for heart failure.

---

## Notes for the writer
- Biggest surprises: (a) the mRNA flu vaccine (Aug 2026) and the personalised mRNA melanoma vaccine's phase 3 (Aug 2026) both succeeded in 2026; (b) two failures: NHS-Galleri missed its primary endpoint (May 2026), pelacarsen/Lp(a) missed its heart-event endpoint (Sep 2026); (c) generic semaglutide arrived in Canada/India/China/Brazil in Mar-May 2026.
- Items that need a second source before shipping in the catalogue: Casgevy patient counts and label; orforglipron exact date (1 vs 7 Apr 2026); Elecsys clearance date (Oct 2025 snippet only); Merck's filing plan (open the Q3 2026 earnings transcript); the gonorrhoea approval day (11 vs 12 Dec 2025).
- Weakest-sourced treatments: 13 (nothing in humans found), 6 (inclisiran read-outs unknown), 7 (company blogs for Casgevy counts), 10 (autonomous AI approval not found).
- Nursing-home delay for Alzheimer's drugs is modelled, never measured; the game should not cite it as a trial effect.
