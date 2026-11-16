# FB4 - three gate reviews' follow-ups

Bundled on 2026-11-16 from 3 Queued rows (Jerus: "Yes, bundle them"), so fixes
that share a harness or a file go through one build, one review and one dry run,
not one each. WHY: over 11-14 to 11-16 a fix batch held the dry-run slot 34
minutes on average, most of them for the whole suite.

- **Rows:** GV1-R, WG2-R, RB1-R, below as QUEUE.md had them; their briefs stand.
- **Files it expects to change:** `Inbox.java`, `GovernmentView.java`, `ui/PeopleScreen.java`, `TreasuryFund.java`, `Game.java`, `SaveFileCheck.java`, `FundCheck.java`, `HealthCheck.java`, `ScaleCheck.java`, `LongPlaytest.java`.
- **Moves the playtest:** yes: RB1-R changes what the fund sells (the ensemble's rules apply); GV1-R and WG2-R do not.
- **Not beside:** FB1 (`HealthCheck`), FB3 (`SaveFileCheck`).
- **Known reds:** none filed under these rows.
- **Done** means each row's own done condition. A row that proves bigger than a fix
  leaves the bundle before the gate: the orchestrator files it back under its own
  id, with its known-red entries, in a queue commit.

## The rows

### GV1-R

GV1's review follow-ups (the review at its gate, 2026-11-10): the band notices churn at a bound with no margin and nothing counts them (should fix, `Inbox.java:227-252`); the debt leaves out the central bank's advances and the linked bonds' indexation (low, `GovernmentView.java:44`); the buyback advice re-quotes on every Overview redraw (low, `GovernmentView.java:133`)

Brief: [GV1-R](GV1-R.md)

### WG2-R

WG2's review follow-ups (the review at its gate, 2026-11-11): a SaveFileCheck reload at months 239 and 240 asserting `brakeApplies()` and `getLastJobsPull()` (should add); the People screen's slowing word reads today's share, not the brake applied (low, `PeopleScreen.java:601`); RT3 rerun with the brake off too; the drift's units; HealthCheck's restated claim and ScaleCheck's and FundCheck's picked months (low)

Brief: [WG2-R](WG2-R.md)

### RB1-R

RB1's review follow-ups (the review at its gate, 2026-11-11): the idle cash read before last month's toRaise is paid, so with the dial past the default the due is overstated and the fund sells more (no money made; the preview likewise); the idle figures count the due, not what was paid; a real run at the default dial counting the cap sale and the idle transfer firing (GovernmentPlayer holds no shares, so the tree counts neither); two stale comments

Brief: [RB1-R](RB1-R.md)
