# R2f — Research: the medical rows (2026-11-16)

## Where it stands

Being built on `batch/R2f` from origin/main 1e88c546 (0.9.126).

## The design (decided before the code)

- **Only a city founded the 1900 way reads it** (R2 ★3). A city founded the
  default way knows every row from its first month and finds nothing here
  (`Research.idleMonth()`), so every constant below is today's there and the
  causes of death follow history's path, as on main: the default trace must
  not move.
- **The cause-of-death steps are brought forward by a find** (R0b ★3: "R2
  turns the named steps into the city's finds"). History's path is kept; a
  row the city found N months before the world takes its step N months
  earlier, and is back on history's path once history has the step too.
  A city that found nothing is history's path to the bit.
- **The mechanisms' constants are undone until the row is known** (R2's
  "the start's figures derived by undoing each entry", as R2d/R2e did for the
  engineering rows): before a row is known in a 1900 city, the constant it
  moves is its pre-row value; from history's month, or from the month the
  city found it, today's.
- **A row with nothing behind it moves nothing** (R6 ★6): where the game has
  no mechanism for the row's effect (no diabetes, kidney failure or heart
  disease among the causes; outbreaks with no disease or season), the row
  stays a dated entry with no effect, for the health study Q17 leaves causes
  of death to. The X-ray and the antitoxin enter no file.
