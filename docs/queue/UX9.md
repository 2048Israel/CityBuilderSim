# UX9 - People, Services, Infrastructure and the Land office in the panel

From UX1 (`docs/queue/UX1-notes.md`; `docs/studies/ux1/audit-2-people-services.md`, its pages and five worst
problems; mockup 1 is Services > Health in the panel). The first of the page rows: each page gets its narrow
form for UX2's panel, keeps every figure it shows today, and puts its wide tables behind Wide.

## What it builds

1. **The narrow form** (560 px) of every page: People and Household money; Services' four systems
   (Health, Education, Utilities, Safety) with their overview and sub-pages (General care, Childcare,
   Senior care, Death care; the basic ladder, College, University, Professions; Police, Prisons) and their
   books; Infrastructure's Roads, Transit, Freight and Ports; the Land office. Mockup 1 is the model: the
   page's figures in one row, the care cards stacked as rows (ring, line, bar, "Build for it"), a long table
   as a line and "Open wide".
2. **The alarms raised, not buried** (audit 2's worst 1): the orphans, the hungry share ("87.9% ate less
   than a basket"), "no school stands", care at 1%, the shops at 43% of output go to UX4's alert marks
   where they are alerts, and to the page's first row where they are the page's figure; never body text
   1,470 px down.
3. **The books pages whole** (worst 2): a books page shows its table, not one chart and "details"; a page
   with nothing built says so in one line ("No prison stands; nothing is spent") instead of full-size charts
   of zeros.
4. **One drawing per thing** (worst 3): one header form, one build action label ("Build for it ›", opening
   Build at its category), one served ring (UX4's reading); Roads drives the Streets need, not Utilities'
   dot, and the roads' upkeep sits in Infrastructure's books.
5. **No page contradicts itself** (worst 4): "served" and "unserved" read from one figure; a full cemetery
   or full berths are drawn as warnings; a $0 bill with staff on shift is explained or mended.
6. **Readable** (worst 5): no 8 px cells or 10 px paragraphs; Freight's 30 rows a table with its heading
   clear; Health's causes of death only those of the era.

## The stability fault it owns

UX1's F1: every Services page and Infrastructure page grows and shrinks by one physical pixel (0.8 px at
the PC's 125% scale) on every redraw, the empty toast corner moving with it; other pages in the same frame do
not, so the cause is in these pages' content. The known reds `UiSteadyCheck | -services-` and
`UiSteadyCheck | -infrastructure-` (UX1's ship file) are this batch's to retire. The gate box draws at
scale 1, so look for the cause, a fractional height snapped two ways, not for the symptom at 125%.

## Files

`ui/PeopleScreen.java` (4,897), `ui/ServicesScreen.java` (3,618), `ui/InfrastructureScreen.java` (2,218),
`ui/LandScreen.java` (2,586), and the model's views they read through getters. Use `docs/map/` and
`tools.Where`, never a large screen whole.

## Size

Large: two to three slices (People and the Land office; Services; Infrastructure), one Opus session.

## The guard

**UiSteadyCheck** with `-services-` and `-infrastructure-` green and no new red, **KeptCheck**, the pages'
own harnesses (`docs/harnesses.md`: grep the screen's name). Pictures before and after by
`ui.PagePictures` for every page of the four tabs, big city and young town; the notes keep six pairs.

## Order and fit

After UX2 and UX8; after ASH1, which changes the Utilities overview's POWER row and the small-town grid
step (this batch draws ASH1's row, it does not re-measure it). UX4 reads the same served figures.

## Not this batch's

The needs sidebar (UX4), the alert row (UX3), the map's tints (UX5), the manual.
