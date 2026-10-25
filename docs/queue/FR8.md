# FR8 - the trucks' share dealt per industry

Jerus, 2026-10-20, at the review desk, on FR2's call ★5 (one carried share
for the city): "pool per industry". A model batch, player-visible: a new version
(`{next}` in its ship file; `ship.sh` sets `GameVersion.VERSION`, BP1).

## What is there (FR2, 0.8.5)

`sectors/Haulage.java` strikes one share at each haul (`getShare()`): what
the fleet held before the month's wear can carry over every city leg the
month asks. Every sector with a BULK input or output runs at
`Sector.getFreightRatio()` = the lesser of the vans' ratio and .6 + .4 x that
one share. So when a giant works lands, its legs join the need at once and
every bulk sector - mines, mills, works, farms - runs short together for
the months its trucks take to arrive (FR2-notes §2 ★5, §7). The share and
the trade's shares are struck at the haul and saved (★9).

## What to build

- **Deal the fleet's room per sector, the newest demand last** (FR2-notes
  §7's alternative): each sector's legs this month are carried in full up
  to what it was carried last month (its standing demand), in
  `Sectors.KEYS` order or by size, whichever the brief's author finds the
  more defensible; the growth over that is served from what room is left,
  pro rata. A sector's freight factor reads its own share. A giant works'
  arrival then shortens its own sector, not the town's.
- **Saved** as the one share is: each sector's share and its standing
  demand in Freight's extras (`haul.*`), struck at the haul. A flow the
  next month reads must be saved (CLAUDE.md); `SaveFileCheck` and
  `ReadPathCheck`.
- **The order, the price and the depots do not change**: the need at
  nameplate (★4), the order toward it, a truck a month at least (★11), the
  quote over what the trucks could carry (★8), the fleet account (★10).
- **HaulageCheck**: a section whose fixture lands a giant works in a town
  whose fleet is full - its own sector short, the others at the share they
  had; and the converse, a town whose fleet has room, everyone whole. Count,
  in the default playtest, the months a sector other than the newest runs
  short, against main's.
- **The ensemble**, if the trace moves (EN1's rule, docs/batch.md):
  `tools/ensemble/ensemble.sh` once on the final model, against the newest
  `docs/ensemble/<version>.md` rather than main rerun: AUTO2's median
  against the baseline's, and no new red.

## Not this batch's

The screens' per-sector truck share (FR4 shows the one share; it can read
the per-sector ones if FR8 lands first).

## Done when

The suite green but what was already red (ScaleCheck until SC1), the
playtests' audits and reload green, the maps regenerated, the changelog's
block in its ship file, `docs/queue/FR8-ship.md` (`ship.sh` puts it in
`docs/notes/changelog.md` and takes this row out of `QUEUE.md`, BP1).
