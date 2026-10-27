#!/bin/sh
# ---------------------------------------------------------------------------
#  ship.sh run against a throwaway origin, with a stub in place of
#  batch-check.sh, so that every stop and every record of the script is tried
#  with no JVM. For whoever changes ship.sh next: run it before you hand the
#  change back. POSIX sh, git and awk.
#
#      sh tools/ship-selftest.sh               test the ship.sh of the current checkout
#      sh tools/ship-selftest.sh <checkout>    test the ship.sh of another checkout (a worktree)
#
#  CBS_SHIPTEST_DIR names the area it builds (default ${TEMP:-/tmp}/cbs-ship-selftest);
#  CBS_SHIPTEST_SH=dash runs ship.sh under that shell (the cloud's /bin/sh);
#  CBS_SHIPTEST_KEEP=1 leaves the area behind to look at. It refuses an area
#  that exists and is not one of its own. Exit status: the number of failed
#  cases, so 0 is green.
#
#  Steps:
#    1. the area is built: origin.git, a bare clone of the checkout's own
#       repository at its HEAD (its branches and GitHub remote dropped, so a
#       push can go nowhere else), main, where ship.sh runs, and work, where
#       the throwaway branches are written; main gets the ship.sh under test,
#       the stub, and a fake Queued row for every throwaway id;
#    2. the branches are written in work and pushed to origin.git: batches
#       with ship files that are right and ones that break each rule, old-style
#       branches, chatgpt/ and docs/ branches;
#    3. the cases run, one line each, PASS or FAIL, then "selftest: N of M
#       passed"; the area is deleted unless it is kept.
#  The stub (batch-check.sh in main) answers by environment variables:
#  STUB_EXIT and STUB_REDS the suite run, STUB_TEXIT and STUB_TREDS --timing
#  in the batch's tree (reds are ";"-separated), STUB_TTSV the figures that run
#  writes to target/checks/timing.tsv (rows ";"-separated, columns "|"-separated:
#  harness|label|measured|budget|unit|status|part), STUB_FAST_EXIT --fast,
#  STUB_TOUCHMAPS makes the generated docs change, STUB_NOEARTH says the Earth
#  file is missing, STUB_RACE=<clone> lets someone push to origin meanwhile.
#  The paired rounds of a timing red (TM3) are the --timing runs that name a
#  harness: STUB_PAIR="harness|label|budget|unit|part" is the figure's row,
#  STUB_FIGS_MAIN and STUB_FIGS_BATCH the figures of the rounds in turn, in
#  main's timing worktree and in the merge's tree (space-separated), and
#  STUB_PEXIT those runs' exit status.
#
#  It never pushes anywhere but that origin.git, and never touches the
#  checkout under test or any save. About two minutes, most of it the clone.
#
#  WHY (BP1, 2026-10-25): the first review of the new ship.sh found its
#  defects one throwaway repository at a time, by hand, each person building
#  the same fixture again; a script that merges, tags and pushes main is the
#  last place to find a bug by shipping. This keeps the fixture, and one case
#  for each defect the review found.
# ---------------------------------------------------------------------------

SRC=$(cd "${1:-.}" && pwd) || exit 2
[ -f "$SRC/ship.sh" ] || { echo "selftest: no ship.sh in $SRC"; exit 2; }
TA=${CBS_SHIPTEST_DIR:-${TEMP:-/tmp}/cbs-ship-selftest}
mkdir -p "$(dirname "$TA")" && TA=$(cd "$(dirname "$TA")" && pwd)/$(basename "$TA")
if [ -e "$TA" ] && [ ! -d "$TA/origin.git" ] && [ -n "$(ls -A "$TA" 2>/dev/null)" ]; then
    echo "selftest: $TA exists and is not a selftest area: name another with CBS_SHIPTEST_DIR"; exit 2
fi
FAILS=0; N=0
ok() { N=$((N + 1)); if "$@" >/dev/null 2>&1; then echo "PASS  $LABEL"; else echo "FAIL  $LABEL"; FAILS=$((FAILS + 1)); fi; }
clean() { [ -z "$(git -C "$TA/main" status --porcelain)" ] && [ ! -e "$TA/main/.git/MERGE_HEAD" ]; }
S() { ( cd "$TA/main" && "$@" ) ; }
run() { ( cd "$TA/main" && CBS_SHIP_SHELL=${CBS_SHIPTEST_SH:-sh} $CBS_SHIPTEST_SH ./ship.sh "$@" > "$TA/out.txt" 2>&1 ); RC=$?; }   # ship.sh in main, output in out.txt, status in RC
rc_is() { [ "$RC" = "$1" ]; }
said() { grep -q -F -- "$1" "$TA/out.txt"; }   # the output holds this text
stopped() { [ "$RC" = 1 ] && said STOPPED && said "$1"; }   # a stop, with this text in its line
GVF=src/main/java/ham/citybuildersim/GameVersion.java

rm -rf "$TA"; mkdir -p "$TA"; cd "$TA" || exit 1
COMMON=$(cd "$SRC" && cd "$(git rev-parse --git-common-dir)" && pwd); BASE=$(git -C "$SRC" rev-parse HEAD)
git clone --bare -q "$COMMON" origin.git && git -C origin.git remote remove origin
git -C origin.git update-ref refs/heads/main "$BASE"
for r in $(git -C origin.git for-each-ref refs/heads --format='%(refname)' | grep -v '^refs/heads/main$'); do git -C origin.git update-ref -d "$r"; done
git -C origin.git symbolic-ref HEAD refs/heads/main
V0=$(git -C origin.git show "main:$GVF" | sed -n 's/^ *public static final String VERSION = "\([^"]*\)";.*/\1/p')
for t in $(git -C origin.git tag -l | grep -E '^[0-9]+\.[0-9]+\.[0-9]+$'); do   # no version tag above main's
    [ "$(printf '%s\n%s\n' "$V0" "$t" | sort -t. -k1,1n -k2,2n -k3,3n | tail -1)" = "$t" ] && [ "$t" != "$V0" ] && git -C origin.git tag -d "$t" >/dev/null
done
git clone -q origin.git main && git clone -q origin.git work
for d in main work; do git -C $d config user.name selftest; git -C $d config user.email selftest@example.invalid; git -C $d config core.longpaths true; done
P=${V0%.*}; Q=${V0##*.}
V1=$P.$((Q + 1)); V2=$P.$((Q + 2)); V3=$P.$((Q + 3)); V4=$P.$((Q + 4)); V5=$P.$((Q + 5))
VOLD=$P.$((Q + 50))   # a version nobody has tagged, above the last tag: an old-style branch's own

# the stub: STUB_EXIT/STUB_REDS drive the suite run, STUB_TEXIT/STUB_TREDS/STUB_TTSV --timing; a --timing that names a harness is a paired round (TM3)
cat > "$TA/main/batch-check.sh" <<'EOF'
#!/bin/sh
cd "$(dirname "$0")" || exit 1
OUT=target/checks; mkdir -p "$OUT"; MODE=suite; NAMED=""
for a in "$@"; do case "$a" in --fast) MODE=fast ;; --timing) MODE=timing ;; -*) ;; *) NAMED="$NAMED $a" ;; esac; done
[ -z "$STUB_LOG" ] || echo "run: mode=$MODE args='$*' cwd=$(basename "$(pwd)") JDK='$JDK_JAVA_OPTIONS' paired='$CBS_TIMING_PAIRED'" >> "$STUB_LOG"
[ -z "$STUB_RACE" ] || { git -C "$STUB_RACE" pull -q --ff-only origin main && git -C "$STUB_RACE" commit -q --allow-empty -m "shipped meanwhile" && git -C "$STUB_RACE" push -q origin HEAD:main; STUB_RACE=; }
case "$MODE" in
  fast) EXIT=${STUB_FAST_EXIT:-0}; REDS="" ;;
  suite) EXIT=${STUB_EXIT:-0}; REDS=${STUB_REDS-} ;;
  timing) EXIT=${STUB_TEXIT:-0}; REDS=${STUB_TREDS-}
          rm -f "$OUT/timing.tsv"
          if [ -n "$NAMED" ]; then   # a paired round: the figure of this side's turn, none when the run "did not run"
              EXIT=${STUB_PEXIT:-0}; REDS=""
              case "$(pwd)" in *main-timing) FIGS=$STUB_FIGS_MAIN ;; *) FIGS=$STUB_FIGS_BATCH ;; esac
              n=$(( $(cat "$OUT/.pairn" 2>/dev/null || echo 0) + 1 )); echo "$n" > "$OUT/.pairn"
              FIG=$(echo $FIGS | cut -d' ' -f"$n")
              if [ -n "$FIG" ] && [ "$EXIT" != 1 ]; then
                  IFS='|' read -r H L B U P <<PEOF
$STUB_PAIR
PEOF
                  printf '%s	%s	%s	%s	%s	OK	%s
' "$H" "$L" "$FIG" "$B" "$U" "$P" > "$OUT/timing.tsv"
              fi
          else   # the hard run
              rm -f "$OUT/.pairn"
              printf '%s
' "$STUB_TTSV" | tr ';|' '
	' | grep . > "$OUT/timing.tsv" || : > "$OUT/timing.tsv"
          fi ;;
esac
if [ "$MODE" != fast ]; then
    printf '%s\n' "$REDS" | tr ';' '\n' | grep . > "$OUT/reds.txt" || : > "$OUT/reds.txt"
    echo "9 harnesses run in 1s: 9 passed, 0 failed" > "$OUT/suite.txt"
    echo "suite     stub (exit $EXIT)"
    [ -z "$STUB_NOEARTH" ] || echo "    note: no earth-2.bin here, so the Earth harnesses' full-file sections did not run"
fi
if [ "$MODE" != timing ] && [ -n "$STUB_TOUCHMAPS" ]; then echo "// regenerated" >> docs/dials.md; echo "# stub" > docs/map/ZzStub.md; fi
exit "$EXIT"
EOF
cp "$SRC/ship.sh" "$TA/main/ship.sh"; chmod +x "$TA/main/ship.sh" "$TA/main/batch-check.sh"
mkdir -p "$TA/main/tools"; cp "$SRC/tools/timing-pair.awk" "$TA/main/tools/timing-pair.awk"   # the gate's arithmetic (TM3), the file under test
IDS="TST TS2 OLD OLD2 OLDT OLDV OLDL GV GG GN GX GH GQ GC GE GB DOC TM ER NC NC2 OS SH"; FAKE=""
for i in $IDS; do FAKE="$FAKE| $i | A fake queued batch $i | [x](y) |
"; done
printf '%s' "$FAKE" > "$TA/fake-rows.txt"
( cd "$TA/main" && awk -v f="$TA/fake-rows.txt" '{ print } /^\|---\|---\|---\|$/ && !d { while ((getline l < f) > 0) print l; d = 1 }' QUEUE.md > "$TA/q.new" && cat "$TA/q.new" > QUEUE.md \
    && git add -A && git commit -q -m "selftest: ship.sh, stub, fake rows" && git push -q origin main )

# the branches, written in work
W() { git -C "$TA/work" "$@"; }
mk() { W fetch -q origin; W checkout -q -B "$1" origin/main; }
fin() { W add -A && W commit -q -m "$1" && W push -q -f origin "$2"; }
shipf() { cat > "$TA/work/docs/queue/$1-ship.md" <<EOF
## changelog
### {next} - $1, the throwaway $1 - 2026-10-25, ready, see docs/queue/$1-notes.md

$1's paragraph; its number is {next}.

## pending
| $1-C | $1 ({next}) leaves a call | Test row | [notes](docs/queue/$1-notes.md) |
## queued
| $1-N | A follow-up $1 decided, {next} | [$1](docs/queue/$1-notes.md) |
EOF
}
notes() { printf '# %s notes ({next})\n' "$1" > "$TA/work/docs/queue/$1-notes.md"; }
jv() { echo "// $1" >> "$TA/work/src/main/java/ham/citybuildersim/$2.java"; }
setv() { sed -i "s/^\( *public static final String VERSION = \"\)[^\"]*\";/\1$1\";/" "$TA/work/$GVF"; }   # the branch's own VERSION
rowout() { sed -i "/^| $1 |/d" "$TA/work/QUEUE.md"; }   # the old way: the branch deletes its own Queued row
mk batch/TST; shipf TST; notes TST; echo "stray {next}" > "$TA/work/docs/queue/TSTX.md"; jv TST SimulationEngine
printf '# Ensemble baseline: %s\n\non the model of **{next}**\n' "$V0" > "$TA/work/docs/ensemble/TST.md"; echo "TST dials" >> "$TA/work/docs/dials.md"; fin TST batch/TST
mk batch/TS2; shipf TS2; notes TS2; jv TS2 Sector; echo "TS2 dials" >> "$TA/work/docs/dials.md"; echo "# TS2's" > "$TA/work/docs/map/ZzStub.md"; fin TS2 batch/TS2
mk batch/OLD2; jv OLD2 Land; setv "$VOLD"; fin OLD2 batch/OLD2           # old-style, its row left in QUEUE.md
mk batch/OLDT; jv OLDT Land; rowout OLDT; fin OLDT batch/OLDT           # old-style, VERSION as main has it (tagged)
mk batch/OLDV; jv OLDV Land; setv "$VOLD"; rowout OLDV; fin OLDV batch/OLDV   # old-style, a VERSION of its own
mk batch/OLDL; jv OLDL Land; setv 0.0.5; rowout OLDL; fin OLDL batch/OLDL     # old-style, a VERSION below the last tag
mk batch/GV; shipf GV; setv 0.7.77; fin GV batch/GV
mk batch/GG; shipf GG; mkdir -p "$TA/work/docs/notes"; echo "# mine" > "$TA/work/docs/notes/gate.txt"; fin GG batch/GG
mk batch/GN; shipf GN; jv GN Markets; fin GN batch/GN
mk batch/GX; printf '## changelog\n### 9.9.9 - GX\n\np.\n' > "$TA/work/docs/queue/GX-ship.md"; jv GX Markets; fin GX batch/GX
mk batch/GH; shipf GH; sed -i 's/^## pending/## Pending/' "$TA/work/docs/queue/GH-ship.md"; fin GH batch/GH
mk batch/GQ; shipf GQ; echo "<!-- GQ -->" >> "$TA/work/QUEUE.md"; fin GQ batch/GQ
mk batch/GC; shipf GC; echo "<!-- GC -->" >> "$TA/work/docs/notes/changelog.md"; fin GC batch/GC
mk batch/GE; shipf GE; echo "# x" > "$TA/work/docs/ensemble/9.9.9.md"; fin GE batch/GE
mk batch/GB; printf '## changelog\n### 9.9.9 - GB\n\np.\n' > "$TA/work/docs/queue/GB-ship.md"; echo "# x" > "$TA/work/docs/ensemble/GB.md"; jv GB Markets; fin GB batch/GB
mk batch/DOC; shipf DOC; notes DOC; fin DOC batch/DOC
for pair in RED:Zoning RC:Rollover; do b=${pair%%:*}; mk batch/$b; shipf $b; notes $b; jv $b ${pair##*:}; fin $b batch/$b; done
mk batch/CQ; shipf CQ; jv CQ SimulationEngine; fin CQ batch/CQ
# TM: a ship file as a person copies a table, header and separator rows included
mk batch/TM; notes TM; jv TM Bank
cat > "$TA/work/docs/queue/TM-ship.md" <<'EOF'
## changelog
### {next} - TM, the throwaway TM - 2026-10-25, ready, see docs/queue/TM-notes.md

TM's paragraph; its number is {next}.

## pending
| item | what is left | why it waits | brief |
|---|---|---|---|
| TM-C | TM ({next}) leaves a call | Test row | [notes](docs/queue/TM-notes.md) |

## queued
| batch | what | brief |
|---|---|---|
| TM-N | A follow-up TM decided, {next} | [TM](docs/queue/TM-notes.md) |
EOF
fin TM batch/TM
mk batch/ER; notes ER; jv ER Markets   # a pending row with no id
printf '## changelog\n### {next} - ER\n\np.\n\n## pending\n| | a row with an empty first cell ({next}) | w | b |\n' > "$TA/work/docs/queue/ER-ship.md"; fin ER batch/ER
mk batch/NC; notes NC; jv NC Markets   # {next} in a pending row only, the changelog heading mistyped
printf '## pending\n| NC-C | NC leaves a call ({next}) | w | b |\n\n##changelog\n### {next} - NC\n\np.\n' > "$TA/work/docs/queue/NC-ship.md"; fin NC batch/NC
mk batch/NC2; notes NC2; jv NC2 Markets   # {next} in a pending row only, the changelog block headed with a number
printf '## changelog\n### 9.9.9 - NC2\n\np.\n\n## pending\n| NC2-C | NC2 leaves a call ({next}) | w | b |\n' > "$TA/work/docs/queue/NC2-ship.md"; fin NC2 batch/NC2
mk batch/SH; shipf SH; notes SH; sed -i '3i echo "ZZ-the-branchs-ship.sh-ran"' "$TA/work/ship.sh"; fin SH batch/SH   # a branch that changes ship.sh itself (TM3 did)
mk batch/OS; shipf OS; notes OS; jv OS Markets; printf '## changelog\n### {next} - ZZ\n\np.\n' > "$TA/work/docs/queue/ZZ-ship.md"; fin OS batch/OS   # another id's ship file
mk docs/pass-t; printf '## changelog\n### docs pass - a pass\n\np.\n' > "$TA/work/docs/queue/docs-pass-ship.md"; fin DPT docs/pass-t   # a docs pass's, misnamed
mk docs/pass-ok; printf '## changelog\n### docs pass - a pass\n\np.\n' > "$TA/work/docs/queue/docs-pass-ok-ship.md"; fin DPO docs/pass-ok   # and named by its branch
mk chatgpt/vt; jv CGV Land; setv "$VOLD"; fin CGV chatgpt/vt           # a branch that moves VERSION
mk chatgpt/ok; jv CGO Land; fin CGO chatgpt/ok                         # one that leaves it
W checkout -q -B scratch origin/main

# --- the cases ---------------------------------------------------------------------------------------------------
export STUB_LOG="$TA/stub.log"; : > "$STUB_LOG"
LABEL="usage: no branch exits 2";               run;                              ok rc_is 2
LABEL="usage: unknown option exits 2";          run --bogus batch/TST;            ok rc_is 2
for g in "GV:changes GameVersion's VERSION" "GG:changes docs/notes/gate.txt" "GH:heading ship.sh does not know" "GQ:edits QUEUE.md" "GC:edits docs/notes/changelog.md" \
         "GE:adds docs/ensemble/9.9.9.md" "GX:no {next}" "OLD2:still queues OLD2" "GB:no {next} to name it by"; do
    b=${g%%:*}; m=${g#*:}; LABEL="guard $b stops, '$m', tree clean"; run --dry-run batch/$b; ok sh -c "[ $RC = 1 ] && grep -q -F -- \"$m\" \"$TA/out.txt\" && grep -q STOPPED \"$TA/out.txt\""
    LABEL="guard $b leaves the tree clean"; ok clean
done
LABEL="guard: {next} with --no-tag";            run --dry-run --no-tag batch/GN;  ok sh -c "[ $RC = 1 ] && grep -q -F 'and --no-tag was given' $TA/out.txt"
LABEL="guard: -- names without --dry-run";      run batch/TST -- StaleCheck;      ok sh -c "[ $RC = 1 ] && grep -q -F 'partial suite never ships' $TA/out.txt"
for v in 0.0.1 $V0 notaversion; do LABEL="guard: --version $v"; run --dry-run --version $v batch/TST; ok rc_is 1; done
LABEL="guard: main's VERSION not the last tag"; S git tag 9.9.9; run --dry-run batch/TST; ok sh -c "[ $RC = 1 ] && grep -q -F 'last version tag 9.9.9' $TA/out.txt"; S git tag -d 9.9.9 >/dev/null

# the ship file's rows, its changelog block, another id's file
LABEL="ship file: a pending row with no first cell stops";                  run --dry-run batch/ER;  ok stopped "row with no first cell"
LABEL="ship file: {next} and no ## changelog block (heading mistyped) stops"; run --dry-run batch/NC;  ok stopped "has no '## changelog' block headed '### {next} ...'"
LABEL="ship file: {next} and a changelog block with no {next} heading stops"; run --dry-run batch/NC2; ok stopped "has no '## changelog' block headed '### {next} ...'"
LABEL="ship file: ...and the tree is clean";                                ok clean
LABEL="another id's ship file on a batch stops";                            run --dry-run batch/OS;  ok stopped "adds a ship file for another id: docs/queue/ZZ-ship.md; this branch's is docs/queue/OS-ship.md"
LABEL="a docs pass's misnamed ship file stops";                             run --dry-run --no-tag docs/pass-t;  ok stopped "adds a ship file for another id: docs/queue/docs-pass-ship.md; this branch's is docs/queue/docs-pass-t-ship.md"
LABEL="a docs pass's ship file named by its branch records, no tag";        run --dry-run --no-tag docs/pass-ok; ok sh -c "[ $RC = 0 ] && grep -q -F \"docs-pass-ok's block on top\" $TA/out.txt"

# the old way: a version another batch took, and --no-tag with a version of its own
LABEL="old-style batch, VERSION tagged already, stops";                     run --dry-run batch/OLDT; ok stopped "batch/OLDT's VERSION $V0 is tagged already: merge main and bump to $V1, or write a ship file"
LABEL="old-style batch, VERSION below the last tag, stops";                 run --dry-run batch/OLDL; ok stopped "does not sort above the last version tag $V0"
LABEL="old-style batch, a VERSION of its own, would tag it";                run --dry-run batch/OLDV; ok sh -c "[ $RC = 0 ] && grep -q -F \"tag $VOLD\" $TA/out.txt"
LABEL="old-style batch, VERSION as main has it, ships --no-tag";            run --dry-run --no-tag batch/OLDT; ok sh -c "[ $RC = 0 ] && grep -q -F 'not tag it' $TA/out.txt"
LABEL="--no-tag with a branch that changes VERSION stops (batch)";          run --dry-run --no-tag batch/OLDV; ok stopped "changes GameVersion's VERSION line and --no-tag was given"
LABEL="--no-tag with a branch that changes VERSION stops (chatgpt/)";       run --dry-run --no-tag chatgpt/vt; ok stopped "changes GameVersion's VERSION line and --no-tag was given"
LABEL="--no-tag with a branch that leaves VERSION ships (chatgpt/)";        run --dry-run --no-tag chatgpt/ok; ok sh -c "[ $RC = 0 ] && grep -q -F 'not tag it' $TA/out.txt"
LABEL="...and the tree is clean";                                           ok clean

HEAD0=$(git -C "$TA/main" rev-parse HEAD)
LABEL="dry run TST: would commit $V1, tag $V1, records"; STUB_TOUCHMAPS=1 run --dry-run batch/TST
ok sh -c "[ $RC = 0 ] && grep -q -F 'would commit \"Merge batch/TST: a fake queued batch TST, $V1\", tag $V1' $TA/out.txt && grep -q -F '1 Pending row(s)' $TA/out.txt && grep -q -F 'its Queued row out' $TA/out.txt"
LABEL="dry run leaves the tree and HEAD as they were"; ok sh -c "[ \"\$(git -C $TA/main rev-parse HEAD)\" = $HEAD0 ]"; ok clean
LABEL="ship TST ($V1) and the generated docs it regenerates"; STUB_TOUCHMAPS=1 STUB_NOEARTH=1 run batch/TST
ok sh -c "[ $RC = 0 ] && grep -q -F \"pushed main and $V1\" $TA/out.txt"
LABEL="TST: VERSION set to $V1"; ok sh -c "grep -q 'VERSION = \"$V1\"' $TA/main/$GVF"
LABEL="TST: tag $V1 on origin"; ok sh -c "git -C $TA/origin.git rev-parse -q --verify refs/tags/$V1"
LABEL="TST: block on top of the changelog, one blank line each side"; ok sh -c "awk '/^---\$/ { d=1; next } d && NF { print; exit }' $TA/main/docs/notes/changelog.md | grep -q -F '### $V1 - TST'"
LABEL="TST: Pending row at the top of the table"; ok sh -c "awk '/^## Pending/ {p=1} p && /^\\|---/ { getline; print; exit }' $TA/main/QUEUE.md | grep -q -F '| TST-C |'"
LABEL="TST: Queued row out, its follow-up row in"; ok sh -c "! grep -q '^| TST |' $TA/main/QUEUE.md && grep -q '^| TST-N |' $TA/main/QUEUE.md"
LABEL="TST: baseline renamed to $V1.md, first line set, ship file gone"; ok sh -c "head -1 $TA/main/docs/ensemble/$V1.md | grep -q -F '# Ensemble baseline: $V1' && [ ! -e $TA/main/docs/ensemble/TST.md ] && [ ! -e $TA/main/docs/queue/TST-ship.md ]"
LABEL="TST: a stray {next} left in another file, with a note"; ok sh -c "grep -q -F '{next}' $TA/main/docs/queue/TSTX.md && grep -q -F 'docs/queue/TSTX.md' $TA/out.txt"
LABEL="TST: gate.txt written, earth-2.bin not named"; ok sh -c "grep -q '^# earth-2.bin: not named' $TA/main/docs/notes/gate.txt"
LABEL="ship TS2 ($V2): generated docs in conflict, the branch's side"; STUB_TOUCHMAPS=1 run batch/TS2
ok sh -c "[ $RC = 0 ] && grep -q -F 'generated docs in conflict' $TA/out.txt && grep -q -F \"pushed main and $V2\" $TA/out.txt"
LABEL="TS2: both blocks, newest on top"; ok sh -c "grep -n '^### $V2 \\|^### $V1 ' $TA/main/docs/notes/changelog.md | head -2 | tr -d '\\n' | grep -q '### $V2.*### $V1'"
LABEL="TS2: Pending rows newest first"; ok sh -c "grep -n '^| TS2-C \\|^| TST-C ' $TA/main/QUEUE.md | head -2 | tr -d '\\n' | grep -q 'TS2-C.*TST-C'"
LABEL="a real (non-generated) conflict stops and names the file"; run --dry-run batch/CQ; ok sh -c "[ $RC = 1 ] && grep -q -F \"conflicts with main in: src/main/java/ham/citybuildersim/SimulationEngine.java\" $TA/out.txt"; LABEL="...and the tree is clean"; ok clean
LABEL="red: exit 4 stops without --red-ok";     STUB_EXIT=4 STUB_REDS='MapCheck | exit 1' run --dry-run batch/RED;  ok sh -c "[ $RC = 1 ] && grep -q -F 'red-ok' $TA/out.txt"
LABEL="red: exit 4 passes with --red-ok";       STUB_EXIT=4 STUB_REDS='MapCheck | exit 1' run --dry-run --red-ok "his words" batch/RED;  ok sh -c "[ $RC = 0 ] && grep -q -F \"gate: NEW red on Jerus's word: his words\" $TA/out.txt"
LABEL="red: exit 3 passes";                     STUB_EXIT=3 STUB_REDS='MapCheck | exit 1' run --dry-run batch/RED;  ok sh -c "[ $RC = 0 ] && grep -q -F 'gate: known red only' $TA/out.txt"
LABEL="red: exit 1 stops";                      STUB_EXIT=1 run --dry-run batch/RED; ok rc_is 1

# timing (TM3): judged once, hard, and only what the suite step did not judge; a timing red is then timed again with main's classes and the
# merge's in turn, and counts against the batch only if the merge is slower by more than the margin (15%)
PL='the plan is within PLAN_MS (40 ms)'
PR="PlanCheck | exit 1;PlanCheck | $PL    FAIL;PlanCheck | 1 FAILED"   # the hard run's red lines: one timing miss and the harness's own two
PT="PlanCheck|$PL|45|40|ms|FAIL|1"                                      # the figure that missed, in the hard run's timing.tsv
PP="PlanCheck|$PL|40|ms|1"                                              # the row each paired round writes
turns() { grep "args='--timing PlanCheck'" "$STUB_LOG" | sed 's/.*cwd=\([^ ]*\) .*/\1/' | tr '\n' ' '; }   # whose tree each paired round ran in
turns_are() { [ "$(turns)" = "$1 " ]; }
tm() { STUB_TEXIT=4 STUB_TREDS="$PR" STUB_TTSV="$PT" STUB_PAIR="$PP" "$@"; }   # a timing red where a paired round's figures are the case's
LABEL="timing: a red where the batch is as fast as main passes, named with the paired medians"; : > "$STUB_LOG"
STUB_FIGS_MAIN="44 45 46" STUB_FIGS_BATCH="46 45 44" tm run --dry-run batch/RED
ok sh -c "[ $RC = 0 ] && grep -q -F 'judged side by side with main' $TA/out.txt && grep -q -F 'PlanCheck [main 45 ms, batch 45 ms' $TA/out.txt"
LABEL="timing: ...main's classes and the merge's in turn, three rounds, in the same minutes (no quiet minute)"
ok turns_are "main-timing main main-timing main main-timing main"
LABEL="timing: ...the paired rounds ask for no quiet minute"; ok sh -c "[ \$(grep -c \"paired='1'\" $STUB_LOG) = 6 ]"
LABEL="timing: a batch 10% slower than main is inside the margin and passes"
STUB_FIGS_MAIN="40 40 40" STUB_FIGS_BATCH="44 44 44" tm run --dry-run batch/RED
ok sh -c "[ $RC = 0 ] && grep -q -F 'PlanCheck [main 40 ms, batch 44 ms (+10.0%)' $TA/out.txt"
LABEL="timing: a red where the batch is 30% slower than main stops, with the medians"
STUB_FIGS_MAIN="40 41 42" STUB_FIGS_BATCH="52 53 54" tm run --dry-run batch/RED
ok sh -c "[ $RC = 1 ] && grep -q -F 'over main' $TA/out.txt && grep -q -F 'main 41 ms, batch 53 ms (+29.3%)' $TA/out.txt && grep -q -F 'red-ok' $TA/out.txt"
LABEL="timing: ...and the tree is clean";       ok clean
LABEL="timing: one slow round in three is not the median"
STUB_FIGS_MAIN="40 41 90" STUB_FIGS_BATCH="40 41 90" tm run --dry-run batch/RED
ok sh -c "[ $RC = 0 ] && grep -q -F 'PlanCheck [main 41 ms, batch 41 ms' $TA/out.txt"
LABEL="timing: CBS_TIMING_MARGIN moves the margin, the same 30% passes at 40%"
CBS_TIMING_MARGIN=40 STUB_FIGS_MAIN="40 41 42" STUB_FIGS_BATCH="52 53 54" tm run --dry-run batch/RED
ok sh -c "[ $RC = 0 ] && grep -q -F 'margin 40%' $TA/out.txt"
LABEL="timing: --red-ok reaches the timing step: the 30% red ships on his word, the lines and medians named"
STUB_FIGS_MAIN="40 41 42" STUB_FIGS_BATCH="52 53 54" tm run --dry-run --red-ok "his words" batch/RED
ok sh -c "[ $RC = 0 ] && grep -q -F \"passed on Jerus's word: his words: PlanCheck [over main's by more than 15%: main 41 ms, batch 53 ms (+29.3%)\" $TA/out.txt"
LABEL="timing: a paired round that did not run leaves nothing to compare, and the red stops"
STUB_PEXIT=1 STUB_FIGS_MAIN="40 41 42" STUB_FIGS_BATCH="40 41 42" tm run --dry-run batch/RED
ok sh -c "[ $RC = 1 ] && grep -q -F 'no pair to compare' $TA/out.txt"
LABEL="timing: a red that is no clock's (no figure behind it) stops at once, with no paired round"; : > "$STUB_LOG"
STUB_TEXIT=4 STUB_TREDS='TimingCheck | exit 1;TimingCheck | took 123 ms' run --dry-run batch/RED
ok sh -c "[ $RC = 1 ] && grep -q -F 'not a timing red' $TA/out.txt && ! grep -q 'cwd=main-timing' $STUB_LOG"
LABEL="timing: --red-ok passes that too, named";  STUB_TEXIT=4 STUB_TREDS='TimingCheck | exit 1;TimingCheck | took 123 ms' run --dry-run --red-ok "his words" batch/RED
ok sh -c "[ $RC = 0 ] && grep -q -F \"passed on Jerus's word: his words: TimingCheck [not a timing red\" $TA/out.txt"
LABEL="timing: main's worktree removed on every path"; ok sh -c "[ ! -d $TA/main/target/main-timing ] && [ \$(git -C $TA/main worktree list | wc -l) = 1 ]"
R2='PlanCheck | exit 1;PlanCheck | FAIL: a new thing'
LABEL="timing: --red-ok for a red in a timing harness ships, no paired round (the suite step judged it)"; : > "$STUB_LOG"
STUB_EXIT=4 STUB_REDS="$R2" STUB_TEXIT=4 STUB_TREDS="$R2" run --dry-run --red-ok "his words" batch/RED
ok sh -c "[ $RC = 0 ] && grep -q -F \"gate: NEW red on Jerus's word: his words\" $TA/out.txt && ! grep -q 'side by side' $TA/out.txt && ! grep -q 'cwd=main-timing' $STUB_LOG"
LABEL="timing: that red and a timing miss of its own: the miss is judged on its own, and his words reach it"
STUB_EXIT=4 STUB_REDS="$R2" STUB_FIGS_MAIN="40 41 42" STUB_FIGS_BATCH="52 53 54" tm run --dry-run --red-ok "his words" batch/RED
ok sh -c "[ $RC = 0 ] && grep -q -F 'timing    red on the merge, judged side by side' $TA/out.txt"
LABEL="timing: exit 3 is no shortcut, an unjudged line is judged (no clock behind it: stops)"
STUB_TEXIT=3 STUB_TREDS='TimingCheck | took 123 ms' run --dry-run batch/RED
ok sh -c "[ $RC = 1 ] && grep -q -F 'not a timing red' $TA/out.txt"
LABEL="docs only: --fast alone, gate.txt untouched"; cp "$TA/main/docs/notes/gate.txt" "$TA/gate.before"; : > "$STUB_LOG"; run batch/DOC
ok sh -c "[ $RC = 0 ] && git -C $TA/main log -1 --format=%B | grep -q -F 'Gate: docs only, --fast'"
LABEL="docs only: tagged $V3"; ok git -C "$TA/origin.git" rev-parse -q --verify "refs/tags/$V3"
LABEL="docs only: gate.txt identical";          ok cmp -s "$TA/main/docs/notes/gate.txt" "$TA/gate.before"
LABEL="docs only: the stub saw --fast only";    ok sh -c "[ \$(wc -l < $STUB_LOG) = 1 ] && grep -q 'mode=fast' $STUB_LOG"
LABEL="push race: the merge and the tag taken back"; STUB_RACE="$TA/work" run batch/RC
ok sh -c "[ $RC = 1 ] && grep -q -F 'main moved on origin' $TA/out.txt && [ -z \"\$(git -C $TA/main tag -l $V4)\" ]"
LABEL="push race: the rerun ships";             run batch/RC; ok sh -c "[ $RC = 0 ] && git -C $TA/origin.git rev-parse -q --verify refs/tags/$V4"

# TM ships: a recorded suite red, a timing red that is within the margin of main's, a ship file with a copied table's header and separator rows
LABEL="ship TM ($V5): a timing red as fast as main's passes, named, the suite's red passes as recorded"
STUB_EXIT=3 STUB_REDS='MapCheck | exit 1' STUB_FIGS_MAIN="44 45 46" STUB_FIGS_BATCH="46 45 44" tm run batch/TM
ok sh -c "[ $RC = 0 ] && grep -q -F \"pushed main and $V5\" $TA/out.txt && grep -q -F 'timing    red on the merge, judged side by side' $TA/out.txt"
G="$TA/main/docs/notes/gate.txt"
LABEL="TM: gate.txt holds the suite's red as a line and each timing red only as a # line"
ok sh -c "grep -q -x -F 'MapCheck | exit 1' $G && grep -q '^# timing red within 15% of main.s (paired): PlanCheck | the plan is within PLAN_MS' $G && grep -q '^# timing red within 15% of main.s (paired): PlanCheck | exit 1' $G && ! grep -q '^PlanCheck' $G && grep -q '^# date: [0-9-]*\$' $G"
LABEL="TM: the merge body names the timing line, with the medians"; ok sh -c "git -C $TA/main log -1 --format=%B | grep -q -F 'Gate-timing: red on the merge, judged side by side with main' && git -C $TA/main log -1 --format=%B | grep -q -F 'PlanCheck [main 45 ms, batch 45 ms'"
LABEL="TM: its header and separator rows did not reach QUEUE.md, its two rows did"
Q="$TA/main/QUEUE.md"
ok sh -c "[ \$(grep -c '^| item |' $Q) = 1 ] && [ \$(grep -c '^| batch |' $Q) = 1 ] && [ \$(grep -c '^|---|---|---|---|\$' $Q) = 1 ] && [ \$(grep -c '^|---|---|---|\$' $Q) = 1 ] && [ \$(grep -c '^| TM-C |' $Q) = 1 ] && [ \$(grep -c '^| TM-N |' $Q) = 1 ]"
LABEL="TM: the records line counts rows, not headers"; ok sh -c "grep -q -F '1 Pending row(s), 1 Queued row(s) added' $TA/out.txt"
LABEL="timing: the passed red is no recorded red, a 30% slower one the next time still stops"
STUB_FIGS_MAIN="40 41 42" STUB_FIGS_BATCH="52 53 54" tm run --dry-run batch/RED
ok sh -c "[ $RC = 1 ] && grep -q -F 'over main' $TA/out.txt"
LABEL="timing: main's recorded red, repeated by the timing run, is not judged again"; : > "$STUB_LOG"
STUB_EXIT=3 STUB_REDS='MapCheck | exit 1' STUB_TEXIT=3 STUB_TREDS='MapCheck | exit 1' run --dry-run batch/RED
ok sh -c "[ $RC = 0 ] && grep -q -F 'gate: known red only' $TA/out.txt && ! grep -q 'cwd=main-timing' $STUB_LOG"
# TM3 item 7: a batch that changes ship.sh itself - the merge rewrites the file the shell is reading; the run goes on from its copy
LABEL="ship.sh: a branch that changes it merges and gates from the copy, the new one's lines never run"; run --dry-run batch/SH
ok sh -c "[ $RC = 0 ] && grep -q -F 'batch/SH changes ship.sh: this run goes on from its copy' $TA/out.txt && grep -q -F 'dry run, nothing kept' $TA/out.txt && ! grep -q -F 'ZZ-the-branchs-ship.sh-ran' $TA/out.txt"
LABEL="ship.sh: ...and the tree is as it was"; ok clean
LABEL="ship.sh: the copy keeps the arguments and the clone it was started in (a branch after options)"; run --dry-run --no-tag chatgpt/ok
ok sh -c "[ $RC = 0 ] && grep -q -F 'not tag it' $TA/out.txt"
echo "selftest: $((N - FAILS)) of $N passed"
[ "${CBS_SHIPTEST_KEEP:-0}" = 1 ] || { cd /; rm -rf "$TA"; }
exit "$FAILS"
