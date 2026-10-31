# ST1 - why ScaleCheck's town stalls since HD2: a study first

Queued 2026-10-31, settling HD2-S on Jerus's word: "Study the stall first".
The test does not change until the cause is known.

## What is known

- **HD2's notes §2**: ScaleCheck section 6 copies "a growing city", the town
  `OrderSearchCheck.playtestCity()` plays on the goods' literals with the rate
  pinned.
  - On HD2 (0.8.76) its player builds two police stations at m731 where the
    base built roads and clinics. Their running costs spend the treasury
    (937k at m900 against 2.2M), and the build that took the base town off at
    m1,075 never comes.
  - The town hovers at 2,600-4,100 people (2,009 at m2,499, against the
    base's 8,097 at m1,201).
  - At m600 HD2's businesses hold 20% less cash (99k against 123k), because
    the till pays the tax and the dividend on a coupon that stays abroad.
- **EM2's notes §5**: on EM2 the homes stop at 4,920 from m600 and the
  treasury is dry from m1,125. The attentive player's town (8,672 people by
  m500, homes 12,120 from m500) also stalls; it misses three assertions at
  best.
- The ensemble's 80 real runs pass 23-24 of 26 criteria since, and young
  cities came out a little smaller on HD2 (AUTO2 median 280,956 against WN3's
  292,982).

## The question

Is it the game's or the fixture's? Would a player's small town stall the same
way since HD2?
- Find the cause on the fixture, one candidate switched off at a time, as
  RE1 did (`docs/queue/RE1.md`, its probe and switches in
  `docs/queue/RE1/`). The candidates: HD2's coupon kept abroad, the tax and
  dividend the till pays on it, the advisor's choice of police over roads
  and clinics, and the treasury's running costs.
- Compare with 0.8.75 (before HD2).
- Then read the ensemble's young cities at the same months for the same
  signature.

## What it leaves

Notes with the cause and its evidence from the runs, and a recommendation:
a model fix (queued as its own batch) or a test town of its own plan with
the premise unmoved. Docs and probes: shipped as a study (`--no-tag`) unless
the fix is trivial.
