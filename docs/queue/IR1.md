# IR1 - 0.9.0 to itch.io

Queued 2026-10-31 on Jerus's word: "right after wn7 a batch that does the
rerquired stuff to push the 0.9.0 to itch.io". WN7 closes the nations line
and ships as 0.9.0; this batch builds that version and puts it on the game's
itch.io page, https://2048israel.itch.io/civic-ledger, which still serves
0.8.60 (`docs/queue/BR1-notes.md` §5).

## Steps

1. **Wait for 0.9.0 on main** (WN7's merge, tagged 0.9.0), then work in a
   temp clone of origin at the tag, never the PC checkout. Name both data
   files as the gate does.
2. **Build**: `Build EXE.bat` (its zip step writes
   `dist\CityBuilderSim-<version>.zip`, the Earth inside it). Check that the
   zip's `GameVersion` is 0.9.0, that it launches, and that the title screen
   says Civic Ledger 0.9.0. A smoke run starts a new city on a scratch save
   folder and plays a few months, and never touches slots 1-9.
3. **Push with butler**, logged in already on this PC
   (`%USERPROFILE%\.config\itch\butler_creds`; never type or print a key):
   `butler status 2048israel/civic-ledger` first, to read the channel 0.8.60
   went to; then `butler push <zip> 2048israel/civic-ledger:<that channel>
   --userversion 0.9.0`, and `butler status` again to see it processed.
4. **The GitHub release** `Civic Ledger (0.9.0)` on the 0.9.0 tag, with the
   zip attached (`branding/README.md`: the titles).
5. **Report**: the build's size, the channel, butler's build id, and the
   release's URL. Changing the page itself (text, screenshots, the devlog)
   is not this batch's: say what a devlog for 0.9.0 could say, for Jerus.

If anything here needs a login, a key or a click on itch.io's site, stop and
say so: it is his to do.
