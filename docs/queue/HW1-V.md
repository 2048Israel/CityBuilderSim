# HW1-V — refine projected ground and borrowing cash

Queued by the orchestrator through REV-HW1-4, message
20261114-100716-543-orchestrator-57cb, findings 3 and 4. These are
comparison refinements assigned to a follow-up.

- Carry forward the usable class and zoning of whole parcels bought by
  a projected works sequence. WorksValue.GroundPlan.take currently
  adds their entire linked dry area to reusable ground, and
  AdviceValueOracle.Ground.use mirrors it. Relevant HW1 source is
  WorksValue.java:134 and AdviceValueOracle.java:213.
- Retain actual quoted bond proceeds above the funding gap as cash
  available to later projected additions. Current cash updates at
  WorksValue.java:183 and :219 and AdviceValueOracle.java:40 and :83
  discard the rounded excess.

Done means independent hard fixtures purchase parcels with usable
ground below total dry area, and issue bonds whose granule leaves
cash above the gap. The next addition consumes the correct available
ground and cash. Every mended assertion must fail with its mend
removed, with the premise retained. Re-run the relevant advice,
ground, finance and save-path checks, then the batch's normal pick.

Expected files are WorksValue.java, AdviceValueOracle.java and the focused
finance/ground harnesses. The default trace can move. Versioned.
