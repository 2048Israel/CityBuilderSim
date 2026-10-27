# SC1 - ScaleCheck's "...and it builds" on a city that needs homes (no tag, 2026-10-27)

The batch's record beside its brief, `SC1.md`. A check batch: no game change, no
version, no trace (the model is untouched), no ensemble. Run whole on Sonnet 5.5 at
xhigh, Opus at the gate only (BP1 item 7).

## 1. The red is already mended

The brief (Jerus, 2026-10-20, "Queue a check fix") was written against 0.8.6-0.8.18,
where `FREE_FIXTURE_MONTHS` was 600 and the ten-billion copy's "...and it builds"
was red: the fixture city had spare places and its landlords declined every month on
the lender's test. Two rescans by other batches have moved the month since, each by
the javadoc's own rule (every assertion read over a scan): XR1 (0.8.19) to 622, and
PT1-M4 (0.8.37, the works department retired) to **807**, where every assertion
holds from 792 to 822. On main (0.8.57, d7448ef5) ScaleCheck passes: "it builds" is
green for both copies (the 5-billion copy 1.99 homes built per K, the 10-billion
0.93, the city 6), and `docs/notes/gate.txt` records MapCheck as main's only red. So
brief item 1 is done and items 2 (a fixture that causes the condition, no window
holding) does not apply: a window holds. The javadoc over `FREE_FIXTURE_MONTHS` says
so, in one paragraph (no new scan: nothing moved).

## 2. What the batch added (brief item 3)

"Count, in the run, how often the fixture city's landlords pass the lender's test in
the months read." `ScaleCheck.Lender`, an `OrderWatch` that counts
`Game.considerOnMortgage()`'s calls: the mortgage orders decided, how many the lender
passed, held for the down payment and declined on the rent. It is attached to the
city for the `FREE_MONTHS` its copies are read against (`Original`) and to each copy
(`Orders`), and printed, not asserted - a later model change that moves the landlords
shows in the log, and nothing here moves a premise or a budget:

    the city's landlords, in the 6 months read: 6 mortgage orders, the lender passed 6, held 0 for the down payment, declined 0 on their rent
    the copy's landlords, in the 6 months read: 6 mortgage orders, the lender passed 6, ...   (5 billion)
    the copy's landlords, in the 6 months read: 0 mortgage orders, ...                        (10 billion)

At 807 the lender passes all six of the city's orders - the opposite of the 600
fixture, which it declined every month - so the line will show the next move. The
fixture's line now also prints its household places ("month 812, 5,319 people in
6,756 household places"): 1,437 of them are spare, so the fixture is not a city
short of homes in the brief's sense, and yet it and its copies build (the 10-billion
copy ordered no mortgage at all and still built 0.93 homes a K, so its homes are not
the landlords' orders alone; I did not trace where they come from). Whether the
fixture should *assert* a shortage is a call the brief left to the case where no window
holds; one holds, so it is left as printed - a call for the orchestrator if Jerus wants
the premise stated as an assertion.

## 3. Checks

`--pick` (ScaleCheck and TimingCheck, the Earth file named) exits 0; maps regenerated,
Stale 0 firm. The ship file has no `{next}`: ship it `--no-tag`. Its Queued row is
SC1's, which `ship.sh` takes out.
