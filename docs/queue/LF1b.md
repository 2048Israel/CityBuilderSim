# LF1b - the cells' share trades with the world, in the financial account

Queued 2026-11-08 by LF1 (its notes, 2), from the brief's question "main's cell-to-world share trades have no line either; say
whether that is right".

Right for the pool identity, not for the balance of payments. The households' cells buy shares from the world and sell them to it
(`Exchange.getHouseholdsBoughtAbroad()` / `getHouseholdsSoldAbroad()`, per company); both sides are outside the audited pools, so
`MoneyAudit` needs no line to balance. But `BondMarket` books the same trade for bonds as a FINANCIAL/DOMESTIC pair (`+ bonds
HouseholdsSoldAbroad` / `- bonds HouseholdsBoughtAbroad`...), so `ForeignAccounts`' financial account holds the cells' bonds and leaves
out their shares, and nothing in `MoneyAudit` reads the exchange's two getters.

Do: the same pair for the exchange's two figures (the sale into the cells' savings DOMESTIC in and FINANCIAL out, the purchase the other
way), a section in `MoneyAuditCheck` or `ShortSellingCheck` that causes a cell-to-world trade and reads the lines. It moves the financial
flows the exchange rate reads, so the trace moves: measure the size first (the cells' share trades with the world over a default
playtest against the financial account's existing flows), run the ensemble, and say in the notes how far the rate moved.
