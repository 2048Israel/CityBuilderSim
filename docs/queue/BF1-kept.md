# BF1-K — two kept UI findings reached by BF1

Main e5642145 UI reproduces both on BF1's 85fb890a inputs. Preserve
KeptCheck's comparisons and pixel band.

1. Fund Search "#3200", Bonds: retained body bounds 1466, fresh 1454.
   FundScreen.figureCell's emptied wrapped price label retains an
   85.8-wide LabeledText; the emptied held label retains 61.99. Region
   widths match fresh but child bounds do not. The held glyph extends the
   row by 11.99 pixels. Give absent values their own scope or remove their
   old glyph bounds.
2. Partners m3169: signatures agree, but 62 pixels differ in x1094–1118,
   y848–853, at CurveChart's three buyer-entry triangles. PartnersPage
   moves fixed Polygon geometry by layoutX/Y; the retained raster outline
   differs after fractional moves. A diagnostic replacing those translations
   with absolute points (x-4, plotB, x+4, plotB, x, plotB-6) passes the focused
   Trade replay. Verify the cause and preserve quiet redraw/node retention.

Controls: target/checks/bf1-main-ui-fund.txt and bf1-main-ui-trade.txt.
Main's FinancesScreen/Pieces, BF1's only changed UI classes, compile ahead
of the BF1 model. FundScreen/PartnersPage are unchanged. Both exact
failures recur; the normal prefix through Trade recurs too
(bf1-kept-capture-prefix.txt). No band is widened.

Frozen scratch slot-10/history inputs: target/bf1-ui-cases/fund and trade.
Bf1UiReplay copies these into TempDirs before replaying Fund 61–63 or
Trade 72–74. A fresh clone can regenerate with the normal KeptCheck prefix,
snapshotting before fund()/kp6Trade(). No player slots are involved.
Images: target/bf1-kept-review-shots. Bounds: target/bf1-fund-tree.txt.
BF1 makes no FundScreen/PartnersPage source change; its ship file queues
this fix and carries the two known-red parts.
