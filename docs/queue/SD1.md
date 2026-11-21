# SD1 - a scrapped building's line says it was scrapped, not "Sold"

Queued 2026-11-21 by the orchestrator, from the real-estate study (Codex, study/real-estate): "Game.retire
physically scraps even though the message says Sold". The study replaces Real Estate's disposal path
after HO1 (Jerus, 2026-11-21: "instead of demolishing it should just sell ... but not demolish"), so
this row is only the wording on main today, for every sector.

## What is wrong

`Game.retire` (Game.java:6738; `Where Game.retire -print`) scraps the buildings
(`buildingManager.retire`, `oldMetal.demolished`, the land freed, the material sold to the builders,
`demolitionLog.record`) and then logs `String.format("Sold %,d %s - %s%s%s", ...)` (about 6780). A
player reads that the plant was sold; it was demolished, and only its material was sold.

## What it does

- The line says what happened: demolished (or scrapped), with the material's sale and the plot's return
  as the line already gives them. A compulsory purchase by the city ("Sold 1 Diner to the city by
  compulsory purchase") is a real sale and keeps its word: check each path that writes "Sold" and change
  only the ones that scrap.
- Every reader of the word follows: `BuildCard.wordKind` (BuildCard.java, which classes a line by its
  first word) and its BuildCardCheck lines (about 992-993 and 1097), and any harness or screen that
  matches the line's text (git grep -n '"Sold ' -- src).

## Done

A harness line that causes a retirement by scrapping and reads its line's word, failing on main's
"Sold", and one that causes a compulsory purchase and still reads "Sold". The model's figures do not
move. Untagged if only words move; prove the default trace unmoved (the trace's files hold no log
line, or say which do).

- **Not beside:** the real-estate study's disposal batch, once queued (it replaces Real Estate's path).
