# REU1 - the Real Estate page drawn as Industry is, with pictograms

Recorded 2026-11-20 on Jerus's request, relayed by the real-estate study session: "after this also a ui
update for real estate right? such that you know how industyr has pictograms and stuff, i feel like it
would be good to implement one for real estate as well, but thats later after this study".

## Waits on

- **The real-estate study, RES1** (`docs/studies/spec-real-estate.md`, on the study/real-estate branch,
  in scope discussion with Jerus since 2026-11-20), a study only, and **the housing model batches it
  specifies** (tentatively RES2 the dwellings and the catalogue, RES3 matching, prices and preferences,
  RES4 parking, HO1 ownership, RES5 the sale and condo disposal after HO1). REU1 draws their figures, so
  it starts after them, when the study says what to show. Its working brief is the study's section 3 (★4) and section 4.

## Scope as recorded

- An Industry-style Real Estate picture: pictograms of the dwelling types and the buildings, as
  `IndustryPicture` draws the industries.
- Every quantity from a public model getter (the screen recomputes nothing), drawn through `Kept` with
  its redraw rule (`ui/Kept.java`).
- The exact figures and the final order are the study's to specify. The study's scope as of
  2026-11-20: mixed dwelling types in some apartment buildings (accepted); parking, wider and taller
  forms and the households' preferences (requested, with their economic rules still open).

## Done

When the study and RES1 have settled what it shows, this brief is rewritten from the study and the
row moves to Queued.
