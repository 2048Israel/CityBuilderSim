# BO1-S - the owner line switched on

Jerus, 2026-11-19 ~18:38, from his side session: "Yes ok bring op1 and bo1-s
to next, basically if love to try them tonight". BO1-S follows OP1 and is
played tonight.

## Why it was off

BO1 (0.9.90) built the owner line (`OwnerHousehold.IN_CITIES`: the founders'
families own the city's businesses, take their dividends, are in the housing
match and have rows on the People screen) and shipped it switched off. Its
notes, `docs/queue/BO1-notes.md` section 1, measured the playtest city with
the line on: 1,102 people at month 121 against 3,500. The retired have no
income of their own at a founding and paid their senior care from the
founders' dividends (the senior-care pull's 30% gone from month 14), and the
five owner families bank or send abroad what they cannot eat (81% of the
bank's savings by month 61). The recommendation (`docs/notes/eye-checks.md`'s
BO1-S row, ★): keep it off until the retired have an income of their own in
a young city, then switch it on and run the ensemble. HS1 (0.9.159) left it
off for that reason (`docs/queue/HS1-notes.md`, BO1-S).

## What is wanted

1. **After OP1 ships** (every lever open from month 1, the pension
   contribution included), measure first: are the founding's retired paid
   an income of their own from month 1 (their `disposable` above 0, the
   pension paid), and does `careAffordability(SENIOR)` hold with the line on?
   Write it in the notes with the figures at months 1, 13, 37, 61 and 121,
   line on against line off.
2. **If they are paid:** switch the line on (`IN_CITIES = true`), the owners
   in the housing match and their rows on the People screen as BO1 built
   them, `OwnerCheck` and the harnesses whose premise changes restated, and
   the ensemble.
3. **If they are not** (OP1 opens the pension at 0, so nobody pays in until
   a player sets it): stop and mail the orchestrator the figures; Jerus
   decides (for instance a founding pension rate, or the line on anyway).
   Do not switch on into BO1's collapse unasked.
4. The owners' banked dividends (BO1's second channel): measure them with
   the line on; if they still stall the city, say so with the figures; where
   a fortune spends is the wealth study's, not this batch's.

## Where it touches, and the gate

`OwnerHousehold`, the housing match, `ui/` People screen (as BO1 built them),
`OwnerCheck`; the default trace moves when the line is on: the ensemble per
`docs/contract.md`. Saves: the owners are already saved (BO1); say if
switching on changes what an old save reads.
