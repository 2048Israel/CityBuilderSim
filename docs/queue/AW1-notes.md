# AW1 — The auto city always builds its works

## Investigation

Slot 10 was read directly from its JSON. It is 0.9.91, m2391, automatic
building off, last pass m1412, nine orders and nine buildings, debt limit
240%, cash-anyway off. Its decision log records on at m1410, off m1411,
on m1412, off m1413. KeptCheck's section 38 performs precisely these four
Build-screen interactions through KeptBench.buildAuto, then restores the
original off setting. The default LongPlaytest starts with automatic
building off and uses its scored advisor. The ensemble copies that founding.
AutoBuilder.pass never switches itself off. The m1412 pass was a UI fixture's
temporary exercise, followed by the fixture's restoration.

The checkout's known-reds file contains no entry naming AW1.

Initial batch-check --fast passed, with zero firm stale findings.
