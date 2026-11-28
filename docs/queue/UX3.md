# UX3 - the alert row: a mark per kind at the top, left click opens, right click dismisses

From UX1 (`docs/queue/UX1-notes.md`, section 5, P3; mockups 7 and 8 in `docs/studies/ux1/mockups/`).
Jerus, 2026-11-27: "the notifcations are hearts of iron style, aka appear on the top and you can click to
open or right click to make disappear". The look is the game's own, not Hearts of Iron's.

## What is wrong today (UX1's audit 1)

Alerts are in six places and none of them is whole: the "Needs you" chip (7 in the big city) matches
nothing in its drawer; the toasts' red [!] cards stack in the bottom right; the inbox's envelope (12) buries
the open items under settled ones; the bank's "under its minimum" card heads all 16 Bank pages; Trade's
four alert cards head every Trade page in the young town; nothing can be dismissed.

## What it builds (P3 A, recommended)

1. **`Alerts`**, a model class in the root package (no toolkit): every open condition the game warns of, one
   entry per kind, each with its kind, state (bad or warning), title, one line, its figure, the step it
   crossed, the page that mends it, and when it opened. It reads what is there: `CityNeeds` (the "Needs you"
   list), `Inbox` (its open items), the bank's minimum, Trade's import cover, money leaving, hot money and
   dollar debt; the interface never measures one itself. UX1's notes, section 5, list where each of today's
   alerts goes (a mark, the inbox, or stays on its page).
2. **The row**: marks 38 px under the top strip, beside the panel (mockup 7). Bad first, then warnings, the
   newest first within each; eight show, the rest fold into one "+N" mark that opens the whole list in the
   panel. The mark is the kind's outline icon on a red or amber ground, a count where a kind has several.
3. **Hover** shows a tip: the title, one line, the figure, "left click opens, right click dismisses".
   **Left click** opens the alert in the panel ("Alerts > <title>"): what is happening, its figures, the
   figure's last 24 months against the steps, what to do (each button opens the page that mends it, as
   Build's advice does today), and "Dismiss until it worsens". **Right click** dismisses at once, with a toast
   "Dismissed · <title> · Undo" for a few seconds.
4. **The return rule** (the study's call, P3 A): a dismissed alert comes back when it worsens a step (its
   figure crosses the next threshold) or after 12 months if it still holds; it leaves the row by itself when
   its condition clears. The inbox keeps letters and every alert's history ("opened", "dismissed",
   "back, worse a step", "settled"); a letter never becomes a mark.
5. **What goes:** the "Needs you" chip and drawer (its list becomes the marks and UX4's needs), the toasts'
   [!] cards (ToastRoom keeps the short toasts such as "Dismissed"), the bank's repeated card and Trade's
   cards (each page keeps its own gauge or figure, once).

## The saved field

The dismissed set (kind, the step at dismissal, the month) is saved: the field, `DataSave` out and in, the
reader in Game's load path, then SaveFileCheck. No format bump: an old save reads as nothing dismissed.

## Files

New `Alerts.java` and `AlertsCheck.java` (root), `ui/Frame.java` (UX2's) for the row, `ui/ToastRoom.java`,
`ui/UserInterface.java` (the chip's removal), `Inbox.java` (the history), `DataSave.java` and the
reader in Game's load path. Read `docs/map/CityNeeds.md` and `docs/map/Inbox.md` first.

## Size

Medium to large: one Opus session, about 1,200 lines, a third of it AlertsCheck.

## The guard

- **AlertsCheck** (new, modelled on RestaurantsCheck), sections from this list: one mark per kind; bad
  before warnings; eight then "+N"; a dismissed alert stays out while its figure holds; it returns a step
  worse; it returns after 12 months; it leaves when the condition clears; the dismissed set survives a save
  and load; every one of today's alerts (the chip's list, the toasts, the bank's card, Trade's four) has its
  kind. Fixtures cause each condition; none reads a played city.
- **UiSteadyCheck** green on every page with the row on screen (the marks are kept nodes: a month that
  changes no alert changes no mark), InboxCheck, ToastCheck, SaveFileCheck.
- Pictures before and after by `ui.PagePictures` (the city page and the inbox, the big city and the young
  town), in the notes.

## Order and fit

After UX2. Beside UX4, UX5 and UX6. CBK3 changes the bank's rules, not its warning; if CBK3 adds an alert
of its own it gets a kind here.

## Not this batch's

The needs sidebar (UX4), any page's own layout, the manual.
