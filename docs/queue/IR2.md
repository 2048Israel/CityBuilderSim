# IR2 - the nations to itch.io: the build LC3 makes possible

Queued 2026-11-02 on Jerus's word: "yes once thats done put on itch". 0.9.0
went to itch.io without world-1.bin (IR1, build #2275325), because the file's
IMF paths allowed no commercial use. LC3 bakes the file again from sources
that allow it, and makes `Build EXE.bat` copy it beside the exe. This batch
puts that build on https://2048israel.itch.io/civic-ledger. IR1's steps
(`docs/queue/IR1.md`) hold, with these changes.

## Steps

1. **Wait for LC3 on main, then build at its tag.** Wait until LC3's tag is on
   origin, the orchestrator has swapped `data/world-1.bin` for LC3's file, and
   the `world-1` release is published. Work in a temp clone of origin at that
   tag, with `data\world-1.bin` and `data\earth-2.bin` copied from the PC
   checkout. The version is whatever LC3's tag says.
2. **Build and check.** Run `Build EXE.bat`. The zip carries `world-1.bin`,
   and its SHA-256 is `tools/world/world-1.sha256`'s. The title screen says
   the tag's version. The smoke run (a scratch save folder, never slots 1-9)
   plays a few months, and the nations show: Trade > Partners and a nation's
   page have figures.
3. **Push with butler** to the channel IR1 used (`butler status` first), with
   `--userversion <the version>`. Run `butler status` again to see it
   processed. Never type or print a key.
4. **No GitHub release of the game** unless Jerus asks; he named itch. (The
   `world-1` data release is LC3's, run by the orchestrator.)
5. **Report** the build's size, the channel, butler's build id, and whether
   the page's text still says the nations are missing. If it does, draft the
   new line for Jerus: the page's text is his to approve.

If anything needs a login, a key or a click on itch.io's site, stop and say
so: it is his to do.
