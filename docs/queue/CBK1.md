# CBK1 - the Bank tab's two banks, and the central bank's own statements and page

Jerus, desk chat, 2026-11-26: "im thinking perhaps when you click bank you have
two options, the commercial bank, and the central bank, with the policy stuff
related to the commercial bank also on the central bank area, along with all
the stats relevant, and give both the banks proper statements, aka ability to
not only view summary but also the statements same way the other sectors, but
make sure to really include alot of stuff into the central bank area". He said
"yes queue both, and preferbly soon" to the design below as the desk chat put
it to him. CBK2 (QE and QT at the player's pace) comes after this batch.

## What is there now (0.9.210)

- `ui/BankScreen` (4,656 lines) is the commercial bank: an Overview plus
  `BANK_PAGE_NAMES` (Profit, Balance sheet, Lending, Funding, Capital & owners,
  History).
- The central bank (`CentralBank.java`) has no page of its own. Its book is a
  card on Finances > Money (`FinancesScreen`'s banner "MONEY (0.7.0; redrawn
  0.7.32)": what it holds, M0, equity), and a smaller card on Government
  (`GovernmentScreen.centralBankCard`: the advances against the ceiling,
  printed this month and since founding). Its dials (the policy rate, the
  advances ceiling, the holdings dial `targetShare`) are on Policy.

## What to build

1. **Bank opens on a choice** between Commercial bank and Central bank. The
   commercial bank keeps its pages as they are; make sure its statements are
   full, in the same form as the sectors' (`SectorStatements`: this month
   against a year ago).
2. **The central bank's own pages**, with a lot on them:
   - Overview.
   - Income statement: interest earned on the window's advances, on the
     treasury's advances, on the paper held and on the vault, less the
     interest paid on reserves; the profit; the remittance; the loss carried
     forward and not remitted.
   - Balance sheet: assets, liabilities and equity, against a year ago.
   - Money made and destroyed: the month's ISSUED and RETIRED by flow
     (`MoneyAudit`). It equals the change in M0 to the cent.
   - Holdings: its share of the term paper, its maturity ladder, and the book
     at market price against face, with the gain or loss.
   - Inflation and credibility: inflation against target, `Expectations`'
     belief in the bank, the real rate, and the term premium's compression.
   - Money: M0 against M2, reserves, the vault, the exchange rate.
   - History charts.
   - Its policy: the same dials as on Policy (one model, shown in two places;
     Policy keeps them too).
3. Every figure comes through a public getter: add the model getters that are
   missing (the income lines, the mark to market). Draw through `Kept`
   (`ui/Kept.java`'s rules). Finances > Money's and Government's cards link to
   the new page.

## The checks

- The central bank's income statement sums to its profit, and the remittance
  to the cent.
- Its balance sheet balances.
- The money page equals the change in M0, to the cent, in a played month.
- The screen recomputes nothing: every figure is a getter's (the pattern of
  the sectors' statement checks).
- `KeptCheck` holds: the clock's redraw keeps the scroll and builds no new
  nodes on an unchanged page.

## Not in this batch

- The pace of QE and QT, the auction share, and the law on direct purchases:
  CBK2.
- The manual's banking section is updated by the publish route (the artifact
  page, then `tools.ManualToMarkdown`), never by editing `docs/manual.*`.

Versioned (the player sees new pages). The model's behaviour does not change:
the playtest trace's md5s equal main's, so no ensemble.
