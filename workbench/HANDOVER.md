# CityBuilderSim — handover for a fresh session (2026-10-05, at 0.7.45)

Read this, then `claude/todo.md` section 0 and the top block of `claude/changelog.md` in the project. The game on Jerus's PC is **0.7.45**, deployed as tag 1005a: the new price model (0.7.42–0.7.44) and its screens (0.7.45); see `claude/inflation-that-answers-to-money.md`. Nothing is in flight. Nothing from 0.7.34 to 0.7.45 has been seen on screen yet.

## 1. Restore the workbench (the cloud workspace starts empty)

Ask for these folders: `C:\Users\Jerus\Documents\NetBeansProjects\CityBuilderSim`, `C:\Users\Jerus\AppData\Roaming\CityBuilderSim` and `C:\Users\Jerus\.m2\repository\org\openjfx`. Then stage:

1. **`CityBuilderSim\workbench\workbench.zip`**, and `unzip` it into `/home/claude/`. It holds:
   - `cbs/`: the tree at 0.7.45, the same as the PC. Re-stage any file Jerus changed since; compare the src mtimes with `device_list_dir`. Then `cp -a cbs cbs-base` before any edit.
   - `bin/`: the build and check scripts (see §2 on `rm`).
   - `scratch-ui1/orch-gate/pt0744/`: **the current reference traces** (0.7.44 and 0.7.45 match them; `pt0` is 0.7.41's).
   - `runs/ensemble/`: **the 16-seed × 5-policy harness**. Run it with `bash runs/ensemble/run.sh <tree> <label>`, about 21 minutes. Its scores are under `out/<label>/score.md`; the CSVs aren't in the zip.
   - `runs/`: every brief, spec and set of notes. The newest are `spec-inflation.md`, `spec-ui-0745.md`, `diag-0743.md` and `ui23`–`ui26-notes.md`.
   - `cities/` and `probe/`, as before.
2. **The JavaFX 21 Windows jars** go into `/home/claude/fx/`; their staged copies sit at `/mnt/user-data/uploads/openjfx/<name>/21/`.
3. **gson:** `/opt/apache-maven-3.9.11/lib/gson-2.13.1.jar`.

## 2. The gate, and two hard lessons from 2026-10-04/05

**The gate:**
- both builds silent;
- the suite 73/74, HealthCheck the known red;
- BuildMenuCheck PASS 73 of 73;
- Stale 0 firm;
- the playtest byte-identical to `pt0744` (or re-baselined, with each change explained, only for a model change Jerus approved);
- endings clean;
- Maps last.

**The two lessons:**
- **`rm` and `/dev`.** An agent deleted `/dev/null` with a stray `rm -r /dev/null`. It was recreated with `mknod -m 666 /dev/null c 1 3`; check `ls -la /dev/null` shows `crw-rw-rw-` before running the suite. Briefs now forbid `rm` outright. `bin/build.sh` and `build-ui.sh` contain `rm -rf build/...`, so agents run copies without it.
- **Never end the orchestrator's turn while work is pending.** A session restart killed an agent, and the orchestrator then sent a progress message and stopped, so nothing ran for six hours. After any interruption, launch the next agent in the same turn; send mid-run updates with SendUserMessage and keep working.

## 3. The batch shape (Jerus's)
- **Orchestrator:** briefs, gate, deploy. **It writes no code** (Jerus, 2026-10-04: "remember you dont code, only the agents you spawn").
- **Agents:** a research or design spec for big work → an Opus implementer → a fresh Opus docs pass → the orchestrator gates and deploys → records on Sonnet. The Sonnet records trial is now confirmed twice.
- **Parallel work:** a worktree (`cbs-b`) plus `git merge-file`, or a read-only spec running beside an implementer. Run at most one JVM per agent, because the machine has 2 cores.
- **Notes are capped:** 120–150 lines.
- **Rules for every agent:** no permission prompts; no `rm`; keep line endings; never move a harness premise or loosen a tolerance (premises change only where Jerus's approved design makes them wrong, each listed); no invented numbers; verdict colours only for verdicts.

## 4. Deploy to the PC
1. Diff the tree against what the PC has. Deploy the sources and only the documents whose content changed; ignore the map date lines and put those files back to the PC's copies.
2. Run `device_commit_files` at most 50 at a time.
3. Stage the sources back and `cmp` each one.
4. Refresh `workbench\workbench.zip` at the end.

## 5. Saves
Slots 1–9 are Jerus's; never write them. Slot 10 is the test slot. Jerus checks the screens himself from the todo's CHECK lists.

## 6. Open, in order
1. **Jerus's PC check:** 0.7.42–0.7.45 first (the todo's CHECK 0.7.42 TO 0.7.45), then the older lists.
2. **His answers to CONFIRM (0.7.42 to 0.7.45):**
   - S1's window;
   - H1 against ★1;
   - high rates pushing rent through the landlords' interest;
   - the shelf cap and A2's hot spells;
   - grocers at their floor losing money;
   - B16;
   - credibility's speeds.

   Then the older CONFIRM lists.
3. **The model questions awaiting his word.**
4. **The manual:** version 11 at 0.7.23; the docs pass lists every false section.
5. **Housekeeping:** `claude/index.md` needs lines for `fixes-from-playing-0-7-39.md`, `why-easy-money-does-not-inflate.md` and `inflation-that-answers-to-money.md`.
