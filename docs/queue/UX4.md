# UX4 - the needs sidebar: a small circle and a word per need

From UX1 (`docs/queue/UX1-notes.md`, section 5, P2; mockup 1, item 4, and mockup 6 the alternative).
UX1's brief: "the needs as a sidebar of small circles and words".

## What is wrong today (UX1's audits 1 and 2)

The needs are read three ways that disagree: the "Needs you" drawer's rows, Build's Overview rings and
the Services pages' rings. "Served" is printed under a 0% ring; a red ring reads 104%; a full green ring
sits over 71 officers short; power reads "362% served" above 96,033 MWh unserved (ASH1 mends power's
reading). A city of 174,226 with no school shows it as a red dot.

## What it builds (P2 A, recommended)

1. **The sidebar** at the window's right edge, 112 px wide, over the map (mockup 1): the needs grouped by
   area (People: homes, jobs, food; Services: power, water, health, schools, safety; the ground: streets,
   freight, land), each a small circle whose ring is how far the need is met, coloured good, warning or bad,
   its word and a short figure under it. The one whose page is open is lit.
2. **Hover** shows the figure and its line ("Streets: 48% served · 4.6k trips on 2.2k of road").
   **A click** opens its page in the panel (the page CityNeeds' Go names today).
3. **One reading.** The sidebar, Build's rings and the Services rings all read the same verdict from
   `CityNeeds` (the share met, the state, the word), through public getters; the word "served" is never
   printed under a short ring, and a ring's colour and its figure never disagree.

## Files

`CityNeeds.java` (a getter per need for the share and state, if one is missing), `ui/Frame.java` (UX2's)
or a new `ui/NeedsBar.java`, `ui/BuildScreen.java` and `ui/ServicesScreen.java` only where their rings read
the verdict. Read `docs/map/CityNeeds.md` first.

## Size

Medium: one session (Opus, or a Sonnet slice for the bar behind an Opus review), about 600 lines.

## The guard

- **NeedsBarCheck** (new): each need's ring, colour and word agree with its share
  on fixtures that cause short, tight and met; no "served" under a short ring.
- **UiSteadyCheck** on every page with the sidebar shown; a month that changes no need moves no circle.
- Pictures before and after by `ui.PagePictures` (the city, Services' overview, Build's overview).

## Order and fit

After UX2 and after ASH1 (power's row reads the lesser of the plants and the wires; the sidebar reads that).
Beside UX3, UX5 and UX6.

## Not this batch's

The district tint when a need is short (UX5's), the alerts (UX3), the manual.
