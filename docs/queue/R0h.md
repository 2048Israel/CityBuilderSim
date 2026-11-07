# R0h - the 1918 influenza's sick counted once

Queued 2026-11-07 by R0g (`docs/queue/R0g-notes.md`, section 3), from R0b's gate review, item 10 (R0b's call 5).

**The double count.** The influenza's sick are a term of `Health`'s sick rate (`Health.java`, THE 1918 INFLUENZA), and
`Game` hands that whole rate to `Sickness.advanceMonth()` (Game ~15369; the seed ~14916). So they enter the ring, and
whoever is still sick past two months (`Sickness.DEADLY_FROM`) dies at the long sick's chance, on top of the influenza's
own dead, which `CausesOfDeath` books. The review guessed 1%. R0g measured it at about 11% in `CausesOfDeathCheck`'s real
city (9,000 people, causes on): two twins loaded at September 1918, one with Health's influenza sick held off. The ring
killed 2.24, 2.65, 1.12 and 0.38 more in January to April 1919, against the influenza's 1.87, 1.86, 1.85 and 1.85, and
0.3 more in January and February 1920. That is about 7 more dead on the influenza's 63 (the twins drift about 0.3 a
month apart in the months between, through their economies). Nothing in October to December 1918: the ring's first
deadly slot is three months on.

**What to do.** An influenza case is acute: it is over, or kills, within weeks, and its dead are the influenza's. So
carry the month's influenza share BESIDE the ring, not in it. For each band, an `acute` share is
`shareFor(b, the full rate) - shareFor(b, the rate without the influenza)`. The ring meets the share without it, and
`deathRates()` and `pastTwoMonths()` read the ring alone. `share(b)`, `peopleInSlot(0)` and `peopleSick()` add `acute`, so
People's sick-by-age and Services' long-sick bars still show the epidemic (the TB sick are counted beside the ring the
same way, R0b ★4). `Health` gets a getter for its sick rate without the influenza (capped as `sickRate` is). The acute
share is saved (a `DataSave` field, the load path, `SaveFileCheck`; null in an old save is none). A four-argument
overload of `advanceMonth`/`seed` keeps SicknessCheck's callers. The alternative was to hand `Sickness` the rate
without the influenza. It is smaller, but the screens would lose the influenza's sick from the ring while Services'
sick rate showed them.

**The harness.** A unit fixture on `Sickness`: months with an acute rate leave the ring exactly as an instance fed the
rate without it, and `share(b)` is the full rate's. And `CausesOfDeathCheck` 10: the probe above, as a fixture. The
ring's extra dead in January to April 1919 are nought within the twins' drift, against R0g's figures on main. The
default trace moves (the playtest crosses 1918), so the ensemble runs.
