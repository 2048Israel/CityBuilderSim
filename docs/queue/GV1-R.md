# GV1-R: GV1's review follow-ups

The stranger's review at GV1's gate (Opus, xhigh, 2026-11-10) said SHIP and left three items.

1. **Should fix.** The band notices churn at a boundary. `Inbox.java:227-252` posts a band notice whenever the government's wealth crosses a band's bound, with no margin, so wealth that sits on a bound can post a notice every month, and no harness counts how many fire. Fix: a margin (hysteresis) on leaving a band, held as a dial, and an InboxCheck section that counts the notices over a long run on a bound.
2. **Low.** The debt is read through `getAllPrincipal()` (`GovernmentView.java:44`). That leaves out the central bank's advances and the indexation on the linked bonds, so a city that borrows those ways reads richer than it is. Fix: the debt the bond screens already show, through its public getter.
3. **Low.** The buyback advice re-quotes every piece on each redraw of the Government overview (`GovernmentView.java:133`). Fix: quote once a month and keep it, or quote only when a piece is on show.
