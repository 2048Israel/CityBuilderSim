# FP1 - The fund's long lists shown a page at a time (KP5's ★C, 2026-11-05)

**What it is for.** The city's fund's Search and Activity pages (`ui/FundScreen.java`) grow with the city: Search
draws a row for every share and every bond the businesses have sold, Activity a row for every month of the fund's
events. In KeptCheck's city at month 2,100 Search is 13,000 nodes and a month's redraw of it costs 58-71 ms kept
(106-173 built new), Activity 124-192 ms kept (195-253 new): past the 100 ms a month lands in at 50x, so the clock
cannot keep up while either is on show. KP5 kept both pages (`docs/queue/KP5-notes.md` §3 ★C, §5): keeping took a
third to a half off; only showing fewer rows takes the rest.

**What to do.**
- Search: its first rows (a dial, `SEARCH_ROWS`, about 40) under the search's words and chips, then "Show more"
  adding as many again (the Sectors list's way, `SectorScreen`'s Show more), the count of what is left said; typing
  or a chip starts again from the first rows. The rows are what they were, in the order they were.
- Activity: the last twelve months open, every earlier year a fold of its own, shut, its months' count and its net
  in the fold's line (the figures through `FundView`/`FundLedger`'s getters, never summed on the screen).
- KeptCheck's fund sections (61-63, 68) hold the pages kept against built new with the folds and Show more in every
  state; `KEPT_FUND_MS` set again from five runs, as KP5 set it (twice the median, rounded up), which should bring it
  near `KEPT_FINANCES_MS`.

**Not this batch:** what the fund holds or can buy, the ticket, Portfolio, the model. Screens only.
