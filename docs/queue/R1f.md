# R1f — The playtest's harnesses on the 1900 city

R1d founds the playtest's own run the 1900 way only under a flag
(`-Dplaytest.founded=1900`, `LongPlaytest.played()`), and leaves
`LongPlaytest.founding()`, which twenty harnesses borrow, all fitted, the
premise they were written on. While `founding()` itself was the
1900 city (R1d's named run 6, 2026-11-16), three of them found something a
1900 city does that no harness asserts yet:

1. **The D$ through the 1914 suspension.** `ExchangeRateCheck` 8 ("in a
   real city the D$ moves like a float"): "the six moves add to the month's
   move, every month" missed by 0.4116, so one month's move is none of the
   six. The 1900 playtest's trace has month 176 (August 1914, gold
   suspended) at 1.0000 to 0.6619 in one month (-33.8%, policy rate 0.75%,
   parity 0.871), and month 319 (the 1926 return) at 1.1293 to 1.0000. Name
   the regime's step as a move of its own, or start the float from where
   gold left it, and judge whether a third in the first month is the float
   or a fault.
2. **A K-scaled 1900 city does not stay K times the city.** `ScaleCheck`'s
   copy missed by, at worst over six months: people 6.2e-2, cash 6.1e-2,
   savings 1.1e-1, deposits 1.3e-1 (K times), prices 3.2e-2 and
   unemployment 1.5e-1 (equal), against tolerances of 4e-3 to 3.3e-2.
   Something on the 1900 path is lumpy in the city's size: the horse form's
   whole posts (R1d call 7), the carters' teams, a fitting's whole
   buildings, the 1901 ladder. Find it and scale it, or bound it.
3. **`ScaleCheck`'s posts identity leaves out the posts past the
   buildings** (`BuildingManager.getPostsPast()`, R0c's horse hands): 6
   posts in 2.4 billion, "the total is every post less those withheld".

The rest red on the 1900 founding needed a modern city and showed no
defect: `AutoBuildCheck` (general care supply), `AuctionCheck` (sales at
auction), `SlopeCheck` (steep ground owned), `PriceLevelCheck` (a partner's
lane) and `BuildAdviceCheck` 9 (a class bound for a university).

Recommended: run `ScaleCheck` and `ExchangeRateCheck` 8 on a 1900 founding
(`founding().withUnfitted(true)`) beside today's city, each as a section of its own, mend what they find, and
give each of the other five a 1900 section where its mechanic exists in
1900. A harness is the finding: no tolerance moves to let 1900 through.
