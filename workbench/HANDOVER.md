# CityBuilderSim — handover for a fresh session (2026-10-04, at 0.7.41)

Read this, then `claude/todo.md` section 0 ("WORKING LEANER" first) and the top block of `claude/changelog.md` in the project. The game on Jerus's PC is 0.7.41, deployed and verified as tag 1004a (0.7.40, the fixes from playing 0.7.39, and 0.7.41, "served" on every gauge, see `claude/fixes-from-playing-0-7-39.md`). Nothing is in flight.

## 1. Restore the workbench (the cloud workspace starts empty)

Ask for these folders: `C:\Users\Jerus\Documents\NetBeansProjects\CityBuilderSim`, `C:\Users\Jerus\AppData\Roaming\CityBuilderSim` and `C:\Users\Jerus\.m2\repository\org\openjfx`. Then stage:

1. **`CityBuilderSim\workbench\workbench.zip`**, and `unzip` it into `/home/claude/`. It holds:
   - `cbs/`: the tree at 0.7.41, the same as the PC. **If Jerus has changed code since, re-stage the changed files from the PC**, because the PC is the source of truth. Compare the src mtimes with `device_list_dir`. Then `cp -a cbs cbs-base` before any edit.
   - `bin/`: `build.sh`, `build-ui.sh`, `checks.sh`, `buildmenu-check.sh`, `endings-check.sh` and `jedit.py`.
   - `scratch-ui1/orch-gate/pt0/t-*.csv`: **the reference playtest traces.** Every gate compares the default playtest's eight traces to these, byte for byte.
   - `cities/city600` and `cities/city2400`: the research cities. Copy them before loading.
   - `probe/`: a loader to copy from.
   - `runs/`: every brief and every set of notes. The newest templates are:
     - `brief-ui21-0740.md` (implementer, main tree);
     - `brief-ui22-0741.md` (implementer, worktree);
     - `brief-ui21-docs.md` (one docs pass over a merge);
     - `brief-records-0741.md` (records on Sonnet).
2. **The JavaFX 21 Windows jars** (`javafx-base`, `-controls`, `-fxml` and `-graphics`, each `…\21\…-21-win.jar`). Put them in `/home/claude/fx/`. Their staged copies at `/mnt/user-data/uploads/openjfx/<name>/21/` serve `buildmenu-check.sh`.
3. **gson:** `/opt/apache-maven-3.9.11/lib/gson-2.13.1.jar`, already in the image.

## 2. The gate (every batch, on the tree it changed)

- `bin/build.sh <tree>` and `bin/build-ui.sh <tree>` silent.
- `JDK_JAVA_OPTIONS=-Djava.io.tmpdir=<scratch>/tmp bin/checks.sh <tree> -q`: **70/71, HealthCheck the known red**.
- `BMC_TMP=<scratch>/tmp bin/buildmenu-check.sh <tree>`: PASS 73 of 73.
- `tools.Stale`: 0 firm.
- `LongPlaytest` with `-Dplaytest.trace=<scratch>/pt/t`, then `cmp` each of the eight `t-*.csv` with `pt0/`: all identical.
- `bin/endings-check.sh` (hardwired to `/home/claude/cbs` against `cbs-base`).
- `tools.Maps` last.

## 3. The batch shape, and what worked on 2026-10-04

- **The order:** triage by the orchestrator (grep and the maps, naming the exact methods) → implementers on Opus → one fresh-context Opus docs pass → the orchestrator merges, gates and deploys → records on **Sonnet**.
- **Two implementers in parallel worked well.** One ran on `cbs`, the other in a worktree (`cp -a cbs-base cbs-b`). Each brief said which methods the other owns. The merge was `git merge-file -p ours base theirs`, with only GameVersion and a harness conflicting. One docs pass covered both versions.
- **The Sonnet records trial was clean.** Its figures checked against the notes, no ★ was missed, and it even closed two stale markers, so records and write-ups stay on Sonnet.
- **Notes are capped** at 150 lines for implementers and 120 for docs passes. Questions with a design choice go to Jerus up front through AskUserQuestion; he answers fast.

**Rules for every agent:**
- no command that could raise a permission prompt;
- never `rm -r` or `rm -rf`, and never `rm` anything under `/dev`;
- keep each file's line endings (edit byte-safely);
- never move a harness premise or loosen a tolerance;
- no invented numbers on screens (every figure a model getter);
- verdict colours only for verdicts;
- gameplay or trace-moving changes only with Jerus's word.

## 4. Deploy to the PC

1. Copy the changed files to `/mnt/user-data/outputs/deploy-<tag>/<path>`.
2. **Deploy only the documents whose content changed:** Maps rewrites every map's date line. Diff each against the base with the "generated … by" line ignored, deploy those that differ, and put the base copies back for the date-only ones so the tree matches the PC (1004a sent 49 files instead of 274).
3. Run `device_commit_files` to `C:\Users\Jerus\Documents\NetBeansProjects\CityBuilderSim\<path>`, at most 50 per call.
4. `device_stage_files` the files back and `cmp` each one: all must be identical. Delete old staged copies first if any are there.
5. Refresh `workbench\workbench.zip` at the end of the session.

## 5. Saves and the PC check

- **Saves** live in `C:\Users\Jerus\AppData\Roaming\CityBuilderSim\saves\`. **Slots 1–9 are Jerus's; never write them. Slot 10 is the test slot.** Before running the game, stage and keep a copy of `autosave*.json`, `slot-10*.json` and `settings.json` (with their `.bak`s). Quitting autosaves, so commit those back afterwards.
- **Jerus checks the screens himself** with a checklist (the todo's CHECK items) and sends back what looks wrong.
- **If screen control is needed:**
  1. Request "Apache NetBeans IDE 18" and "File Explorer".
  2. In NetBeans, use **Run → Run Project (CityBuilderSim)**.
  3. While the game runs, resolve and request `java.exe`.
  4. The taskbar's Java icon is at about (1000, 847).
  5. The game window is 1,389 × 868. Positions: Continue (249, 269); the rail's Menu (35, 845); Quit (249, 580), confirm (744, 462).

## 6. Open, in order

1. Jerus's PC check of 0.7.40/0.7.41 (the checklist was sent 2026-10-04), then 0.7.34–0.7.39.
2. His answers to CONFIRM: 0.7.40/0.7.41's ★ first (the road red at 111% served, care over 100% on the rings, a run funded only up to a refusal), then 0.7.39's D2 and D7.
3. The model questions awaiting his word (one batch).
4. The manual: still version 11 at 0.7.23; it now runs to 0.7.41, plus the five false sections in the todo.
5. **Housekeeping:** `claude/index.md` still needs its line for `fixes-from-playing-0-7-39.md` (under The interface, after the fund's line).
