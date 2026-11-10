# SQ1 - a building stack past 2^31

Queued 2026-11-09 by ST3 (`docs/queue/ST3-notes.md`, section 2), a call
taken on its recommendation (Jerus can overturn it).

## Why

`BuildingsStacks` keeps its `quantity` in an int, and a building type's
units merge into one stack. Jerus's plans are cities of 5 and 10 billion
people (`ScaleCheck.FREE_PEOPLE`). A town housed in Houses (6 people each)
holds about 0.2 Houses a person, so at 10 billion its House stack is about
2e9, against an int's 2,147,483,647. ST3 met the wall in ScaleCheck's
ten-billion copy: where the stack times K passed it the copy did not load
("the copy loads" FAIL at five months of the first plans' scans), and near
it the copy's largest order was one building (five more months). ST3's town routes round
it (apartments built ahead, 140 Houses at 7,000 people); a player's city
need not.

## What it does

Lets a stack hold past 2^31: its quantity a long (and the 150-odd callers
of `getQuantity()`, the under-construction count, `BuildingManager`'s
sums), or a second stack opened at the ceiling. Old saves read an int field
as a long, so no format bump. A harness: a stack driven past 2^31 by an
order and by `ScaleCheck.scale()` reads back whole, and its homes, posts and
places are its quantity times the template's.

## What does not move

The model's behaviour below the ceiling (the default playtest's trace is
main's), and ScaleCheck's premise.
