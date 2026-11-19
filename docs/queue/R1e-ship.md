<!-- readiness: provisional -->
## changelog
### {next} — R1e, a new building's equipment priced and saved at its order — 2026-11-19, ready, see docs/queue/R1e-notes.md
A new building's equipment is now chosen when its order is quoted and
accepted, not when it opens. In a city founded the 1900 way, where a plant
ordered now may go without a piece that does not pay, the quote prices the
shell and the pieces the order keeps (a piece's part of the price its goods
at the catalogue's own prices and its work at the template's own price a
point); the builders bill that, the site owes that work, the tax and the
investment are booked on it, the set is saved and delivered at the finish.
A discovery or a price change afterwards changes a new order only. The
investors, the money test, the wait, the auto-builder and a stopped site's
restart read the same set. A city founded the default way is unchanged, its
traces byte for byte.
## pending
## queued
| R1h | A new building's set read on what leaving a piece off saves the order: EquipmentFit.fittedForNew() (EquipmentFit.java:281) still chooses on R1b's C (EquipmentFit.java:321: the piece's goods at city prices and its work at nonMaterialPricePerPoint()), not on what the order is discounted for it since R1e (Fitout.cashShareLeftOff(), Fitout.java:136), so the planners' choice reads a cost the invoice does not take off; REV-R1e's note, not a fault of R1e | docs/queue/R1e-notes.md, REV-R1e's mends |
## known-reds
