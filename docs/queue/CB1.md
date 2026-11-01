# CB1 - The central bank's strictness measured over several cities, not one probe city

Queued 2026-11-01 by Jerus ("queue, but after 0.9.0"), from the day's
question whether checks that test one city's history should exist: a check
whose claim is about policy should read it over many cities, not the path of
one.

## Why

CentralBankCheck section 21 ("how strict") claims very strict holds prices
lower than Standard over STRICT_HORIZON months, on ONE probe city
(`probeCity()`), and that city has been re-picked whenever a batch moved its
path: seed 7's shape since FR7, seed 5's from 0.7.139, seed 8's from 0.7.102,
seed 11's from 0.7.67, seed 2's from 0.7.58, seed 5's before. EP1 measured the
claim (2026-11-01, EP1-notes §11): strict is lower at 22 of 24 price levels
and in 11 of 12 founding shapes at the factor, and the sign flips between two
price levels 3e-10 apart. The policy property holds; one city is a draw.

## What to build

1. Section 21's strict-against-Standard claim read over a set of cities (the
   founding shapes EP1 measured, or the ensemble's seeds' foundings), each
   played from STRICT_BRANCH on STRICT_HORIZON months at both settings: very
   strict lower than Standard at the median, and in at least a stated share
   of the cities, the share a dial with its sentence (EP1's 11 of 12 is the
   evidence for it). The very loose twin's claims and the fall-of-world-prices
   fourth game keep their own fixtures as they are.
2. The probe city's re-pick history stays in the header as the reason.
3. The run time: the cities run as child JVMs or in turn within the check's
   budget; say what it costs the suite (GT1 measures the suite).

## The checks

The claim holds on main at the factor and at today's prices; one city
reversed by hand (a fixture that makes strict dearer there) does not turn the
line red while the share holds, and enough reversed ones do. No other
assertion of CentralBankCheck moves.
