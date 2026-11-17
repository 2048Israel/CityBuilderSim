# PK1: two fixtures that pick a month

Filed back by FB4 (2026-11-17) from WG2-R's item 5, the review at WG2's gate (2026-11-11): "`ScaleCheck.java:187`
(month 191 to 172) and `FundCheck.java:217` (saved at month 74) each pick a month that passes, ScaleCheck missing at 8
of the 31 months scanned and FundCheck passing at 74 alone of 74-80: fragile, worth a fixture that causes the
condition at any month."

- **ScaleCheck.** `FREE_FIXTURE_MONTHS` is the month `ownTown()` is copied at. WG2 moved it from 191 to 172. QB1
  (2026-11-14, "Repick the scale copy at a qualifying month") moved it again, to 225. That is the third pick in a
  week, each on the path the batch before it left.
- **FundCheck.** The bank fixture (section 1, `// WG2, 2026-11-10: SEVENTY-THREE MONTHS, NOT SEVENTY-TWO.`) plays 73
  months and saves. It reads a standing bank owned by somebody other than the city, and a fund whose dial is 0.
- **Done** means each fixture causes what it reads, so any path reaching it works:
  - ScaleCheck: the town's growth, free ground, affordable rents and orders.
  - FundCheck: the bank owned by households or abroad.

  They may play until a stated condition holds, up to a bound, and fail as a fixture if it never does. They may not
  pick a month. Each premise stays as written.
- **Not done in FB4:** it is a fixture redesign in two harnesses, larger than a review follow-up. Neither red is
  open: both pass on main and on FB4.
