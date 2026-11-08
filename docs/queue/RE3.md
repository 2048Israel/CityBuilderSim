# RE3 - the landlords' payroll billed with the rent at once (RE1-T's way (a))

Queued 2026-11-08 by RE2's ship file, on RE1-T as settled 2026-10-31 (Jerus:
"Take all (Recommended)"): leave the trap with the ensemble watching (d)
until a seed on the model RE2 ships shows it, then (a), with that seed as
the fixture. RE2's ensemble shows it. The world lane, after RE2.

## The fixture: AUTO2 seed 3 on RE2's model

The ensemble's driver, with neither data file named (as the ensemble runs):
`ham.citybuildersim.Ensemble AUTO2 3 <csv>`, about a minute. RE1's probe
(`docs/queue/RE1/probe-main.patch`, which still applies to
`tools/ensemble/Ensemble.java`) writes the landlords' books a month, and
`docs/queue/RE2/trap.py` flags the stretch. With RE2's posts, from m600 to
m2,400:

| | main (0.9.50) | RE2 |
|---|---|---|
| months the landlords ran at a loss | 0 | 125 |
| months held for the 15% down payment | 0 | 70, 59 of them in a row (m1,127-m1,238) |
| months with 3+ sectors held at the staffing gate | 0 | 2 |
| people at m1,060 / m1,220 | 21,616 / 51,953 | 22,517 / 23,995 |
| 40,000 people at | m1,153 | m1,447 |
| final population | 495,041 | 437,265 |

The chain is RE1's, short of its last link. From m980 the landlords lose
money (pre-tax -418 on 2,359 of rent, a payroll of 313 and a property tax of
1,033). Their till is empty from m1,100 to m1,220. They cannot put the 15%
down, and the town sits near 20,000 for 160 months while main's doubles. It
recovers when the till refills (32,725 at m1,260). The staffing gate does
not hold the builders here (IC1's spare crews). Seeds 1 and 11 lose money for
204 and 526 months without the stall; seed 13 (0.59 of main's final) has no
loss month, so its lag is not this.

## What it does

The landlords' rent carries their payroll the month it is paid, not through
the floor's lift at lease speed (RE1: the trap's rent stayed under its floor,
3.27 against 3.34). Find where `RealEstate.rentBreakEven()`'s payroll reaches
the rent each month and bill it with the rent; keep the floor and the hurdle
as two decisions (RealEstate's javadoc, TWO RENTS). Done when seed 3 has no
run of down-payment holds of a year or more, its town does not stand still
while main's grows, and the ensemble is scored against RE2's.

## What does not move

The posts (2 DIPLOMA a Studio and Low-Rise, homes before workers), the 15%
down payment and the lender's cover. The trace moves; saves unchanged unless
a new flow needs a field.
