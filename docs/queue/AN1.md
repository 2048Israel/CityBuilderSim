# AN1

**The price level's anchor: the default city no longer hyperinflates under
XR1's float.** Jerus, 2026-10-22, asked what follows 0.8.20: "Fix
hyperinflation first", ahead of XR2. On XR1-C ★1, the same day: "Keep as
built".

## What is wrong

Since XR1 (0.8.19) the default playtest's prices run away
(`docs/queue/XR1-notes.md` §3, its variant table): 14 / 12,225 / 1.7e9 times
founding at about m600 / m1060 / the end, the D$ at its 1e9 guard from m2160,
10,877 people at the end. 0.8.18 read 4.7 / 15 / 1,036 and 573,349 people.
No single new force drives it (variants D, E, F): following realised parity
one for one does. A price rise moves parity, the rate follows it, imports get
dearer, and prices rise again. The shelf is cost-plus and ratchets
(`Retail.repriceShelf()`: under its floor a price catches up at
FLOOR_CATCH_UP, over it it moves at CLEAR_SPEED and grows at expected
inflation), so every swing in the rate leaves prices higher. The old rule
followed only 0.70 of parity, and that lag was the price level's anchor.

## The two leads (XR1-C)

- **(a) Import prices reach the shelf slowly**: the pass-through dial of
  `docs/studies/spec-exchange-rates.md` §3.4.
- **(b) A shelf that falls as readily as it rises**: `Retail.repriceShelf()`'s
  ratchet.

## What done looks like

1. **Measure first**, as XR1-notes §3 did: the default playtest (4,000
   months) for (a) alone, (b) alone and both, each built from the same main
   with one change, prices at the same checkpoints and people at the end,
   in a table in `docs/queue/AN1-notes.md`. Ship the smallest change that
   anchors the price level. If neither lead anchors it, stop and report: the
   row goes under Pending for his call. No dial moved only to pass.
2. **XR1's float stays.** The D$ still follows its parity; the old pull and
   anchored drift are not brought back (variant K stalled the city).
3. **The default city, with no player action:** the D$ never at its guard,
   and the price level and population reported against 0.8.18's figures
   above. CentralBankCheck's "no spiral" and LongPlaytest's spiral findings
   are expected to go green; if one does not, the notes say why.
4. **A harness for the mechanic** (README, *The checks*): a fixture that
   causes the ring (a fall in the D$ that moves parity), asserting that
   prices settle rather than ratchet, against the model's own constants.
5. **★1 settled as built:** CurrencyCheck 4 restated to the lean as built
   (XR1-notes §4's table and §6), so it leaves main's reds.
6. The trace moves, so the ensemble runs once on the final model against
   the newest `docs/ensemble/<version>.md` (0.8.19's), with 2 runners if
   another batch is running, and its baseline is committed. Player-visible:
   bump `GameVersion.VERSION`.

## Out of scope

XR2's regimes; XR1-C's ★2-★4 (Jerus's calls still, though the notes say
which of those reds the fix turns green); `HistorySave.round2`'s 9.2e16 bound
(stays in XR1-C, and matters far less once prices stay bounded).
