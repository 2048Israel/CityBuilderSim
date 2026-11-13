#!/bin/sh
# Throwaway origin and stub suite; the production red judge uses Java. Run after changes to ship.sh or tools/gate.sh.
# Optional checkout argument. CBS_SHIPTEST_DIR names the area, CBS_SHIPTEST_KEEP=1 keeps it,
# CBS_SHIPTEST_SH chooses the shell. Exit status counts failed cases.
# WHY (BP1, GF1): publication defects must be reproduced without publishing the repository.

unset CBS_SHIP_COPY CBS_SHIP_HOME CBS_SHIP_OWNER CBS_GATE_RUN CBS_GATE_SCRIPT CBS_GATE_DIRECTORY CBS_KNOWN_REDS CBS_REJUDGE
SRC=$(cd "${1:-.}" && pwd) || exit 2
[ -f "$SRC/ship.sh" ] || { echo "selftest: no ship.sh in $SRC"; exit 2; }
TA=${CBS_SHIPTEST_DIR:-$(mktemp -d "${TEMP:-/tmp}/cbs-ship-selftest-XXXXXXXX")}
mkdir -p "$(dirname "$TA")" && TA=$(cd "$(dirname "$TA")" && pwd)/$(basename "$TA")
if [ -e "$TA" ] && [ ! -d "$TA/origin.git" ] && [ -n "$(ls -A "$TA" 2>/dev/null)" ]; then
    echo "selftest: $TA exists and is not a selftest area: name another with CBS_SHIPTEST_DIR"; exit 2
fi
export CBS_SELFTEST=1
FAILS=0; N=0
ok() { N=$((N + 1)); if "$@" >/dev/null 2>&1; then echo "PASS  $LABEL"; else echo "FAIL  $LABEL"; FAILS=$((FAILS + 1)); [ ! -f "$TA/out.txt" ] || cp "$TA/out.txt" "$TA/fail-$N.txt"; fi; }
clean() { [ -z "$(git -C "$TA/main" status --porcelain)" ] && [ ! -e "$TA/main/.git/MERGE_HEAD" ]; }
S() { ( cd "$TA/main" && "$@" ) ; }
run() { rm -f "$TA/direct/cancelled"; rm -rf "$TA/direct/stages"; ( cd "$TA/main" && CBS_GATE_RUN="$TA/direct" CBS_SHIP_SHELL=${CBS_SHIPTEST_SH:-sh} $CBS_SHIPTEST_SH ./ship.sh "$@" > "$TA/out.txt" 2>&1 ); RC=$?; }   # ship.sh in main, output in out.txt, status in RC
rc_is() { [ "$RC" = "$1" ]; }
said() { grep -q -F -- "$1" "$TA/out.txt"; }   # the output holds this text
stopped() { [ "$RC" = 1 ] && said STOPPED && said "$1"; }   # a stop, with this text in its line
GVF=src/main/java/ham/citybuildersim/GameVersion.java

rm -rf "$TA"; mkdir -p "$TA"; cd "$TA" || exit 1
# HA1: an unpicked nightly harness keeps its record; a run green harness clears it.
printf '2.0\tMapCheck\t0\n1.0\tGlobeCheck\t0\n' > times
printf 'MapCheck | resolved FAIL\nKeptCheck | standing FAIL\nGlobeCheck | unseen FAIL\n' > recorded
awk -v earth=named -f "$SRC/tools/gate-record.awk" times recorded > kept
LABEL='a default gate retains an unrun nightly red'; ok grep -q '^KeptCheck |' kept
LABEL='a green harness clears its old record'; ok sh -c '! grep -q "^MapCheck |\|^GlobeCheck |" kept'
awk -v earth=missing -f "$SRC/tools/gate-record.awk" times recorded > kept
LABEL='unseen full-file Earth/Globe lines remain covered'; ok grep -q '^GlobeCheck |' kept
: > times
awk -v earth=named -f "$SRC/tools/gate-record.awk" times recorded > kept
LABEL='an empty run list never loses standing reds'; ok cmp -s kept recorded
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
VOLD=$P.$((Q + 50))   # a version nobody has tagged, above the last tag: a branch that moves VERSION

# The stub drives suite outcomes and interruptions without a JVM (GF1, GR1).
cat > "$TA/main/batch-check.sh" <<'EOF'
#!/bin/sh
cd "$(dirname "$0")" || exit 1
if [ "$1" = --judge ]; then
    exec java src/main/java/ham/citybuildersim/KnownReds.java "$2" "$CBS_KNOWN_REDS" target/checks rejudge
fi
OUT=target/checks; mkdir -p "$OUT"; MODE=suite; NAMED=""
for a in "$@"; do case "$a" in --fast) MODE=fast ;; --timing) MODE=timing ;; -*) ;; *) NAMED="$NAMED $a" ;; esac; done
[ -z "$STUB_LOG" ] || echo "run: mode=$MODE args='$*' cwd=$(basename "$(pwd)") JDK='$JDK_JAVA_OPTIONS' shipcopy='$CBS_SHIP_COPY'" >> "$STUB_LOG"
[ -z "$STUB_REWRITE_SHIP" ] || { awk 'NR == 3 { print "echo \"ZZ-the-branchs-ship.sh-ran\"" } { print }' ship.sh > "$OUT/ship.new" && cat "$OUT/ship.new" > ship.sh; }   # ship.sh rewritten in place under the shell reading it
[ -z "$STUB_RACE" ] || { git -C "$STUB_RACE" pull -q --ff-only origin main && git -C "$STUB_RACE" commit -q --allow-empty -m "shipped meanwhile" && git -C "$STUB_RACE" push -q origin HEAD:main; STUB_RACE=; }
# GF1: faults after a successful suite, with exactly the stale state LM1 reused.
if [ "$MODE" = suite ]; then
    if [ -n "$STUB_MOVE" ]; then
        git -C "$STUB_WORK" fetch -q origin "$STUB_MOVE"
        git -C "$STUB_WORK" checkout -q -B "$STUB_MOVE" FETCH_HEAD
        [ -z "$STUB_WITHDRAW" ] || git -C "$STUB_WORK" rm -q "docs/queue/${STUB_MOVE#batch/}-ship.md"
        git -C "$STUB_WORK" commit -q --allow-empty -am 'moved during gate'
        git -C "$STUB_WORK" push -q origin "$STUB_MOVE"
    fi
    case "$STUB_FAULT" in
        killed) kill -KILL "$PPID"; exit 0 ;;
        stale) echo 'fast 0' > target/ship-tmp/code; kill -KILL "$PPID"; exit 0 ;;
        missing) exit 0 ;;
        hold) echo ready > "$STUB_READY"; for attempt in $(seq 1 120); do sleep 1; done; exit 1 ;;
    esac
fi
case "$MODE" in
  fast) EXIT=${STUB_FAST_EXIT:-0}; REDS="" ;;
  suite) EXIT=${STUB_EXIT:-0}; REDS=${STUB_REDS-} ;;
  timing) echo "unexpected hard timing at the gate"; exit 99 ;;
esac
echo "$MODE" > "$OUT/stage.txt"
if [ "$MODE" != fast ]; then
    printf '%s\n' "$REDS" | tr ';' '\n' | grep . > "$OUT/reds.txt" || : > "$OUT/reds.txt"
    { printf 'MapCheck\n'; cut -d ' ' -f1 "$OUT/reds.txt"; } | sort -u | awk '{print "0.1\t" $0 "\t0"}' > "$OUT/times.tsv"
    awk -F ' [|] ' '
        { h=$1; text=substr($0,index($0," | ")+3); if (!(h in seen)) order[++n]=h; seen[h]=1
          if (text ~ /^exit /) code[h]=substr(text,6); else lines[h]=lines[h] "    " text "\n" }
        END { for(i=1;i<=n;i++) {h=order[i]; print h "  FAILED - exit " (code[h] ? code[h] : 1) " (0.1s)"; printf "%s",lines[h]}
              print "9 harnesses run in 1s: " 9-n " passed, " n+0 " failed" }' "$OUT/reds.txt" > "$OUT/suite.txt"
    echo "suite     stub (exit $EXIT)"
    [ -z "$STUB_NOEARTH" ] || echo "    note: no earth-2.bin here, so the Earth harnesses' full-file sections did not run"
fi
if [ "$MODE" != timing ] && [ -n "$STUB_TOUCHMAPS" ]; then echo "// regenerated" >> docs/dials.md; echo "# stub" > docs/map/ZzStub.md; fi
exit "$EXIT"
EOF
cp "$SRC/ship.sh" "$TA/main/ship.sh"; chmod +x "$TA/main/ship.sh" "$TA/main/batch-check.sh"
mkdir -p "$TA/main/tools"; cp "$SRC/tools/gate-report.sh" "$SRC/tools/carries.sh" "$SRC/tools/pick.sh" "$SRC/tools/gate-reuse.sh" "$SRC/tools/gate.sh" "$SRC/tools/gate-process.ps1" "$TA/main/tools/"   # the gate's arithmetic (TM3), and the test of an unmerged predecessor (RL1): files under test
cp "$SRC/tools/gate-record.awk" "$TA/main/tools/"
cp "$SRC/src/main/java/ham/citybuildersim/KnownReds.java" "$TA/main/src/main/java/ham/citybuildersim/"
printf '\nMapCheck | stub known | FIX | 2026-11-12 | fixture\n' >> "$TA/main/docs/notes/known-reds.txt"
IDS="TST TS2 NS GV GG GN GX GH GQ GC GE GB DOC TM ER NC NC2 OS SH PP PS PR PT"; FAKE=""
for i in $IDS; do FAKE="$FAKE| $i | A fake queued batch $i | [x](y) |
"; done
printf '%s' "$FAKE" > "$TA/fake-rows.txt"
( cd "$TA/main" && awk -v f="$TA/fake-rows.txt" '{ print } /^\|---\|---\|---\|$/ && !d { while ((getline l < f) > 0) print l; d = 1 }' QUEUE.md > "$TA/q.new" && cat "$TA/q.new" > QUEUE.md \
    && git add -A && git commit -q -m "selftest: ship.sh, stub, fake rows" && git push -q origin main )

# Direct publisher cases use a live fixture guardian. Launcher cases exercise the real guardian.
mkdir -p "$TA/direct"; touch "$TA/direct/guardian.ready"
( while :; do sleep 1; done ) &
FIXTURE_GUARD=$!; echo "$FIXTURE_GUARD" > "$TA/direct/guardian"

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
mk batch/TST; shipf TST; notes TST; echo "stray {next}" > "$TA/work/docs/queue/TSTX.md"; jv TST SimulationEngine
printf '# Ensemble baseline: %s\n\non the model of **{next}**\n' "$V0" > "$TA/work/docs/ensemble/TST.md"; echo "TST dials" >> "$TA/work/docs/dials.md"; fin TST batch/TST
mk batch/TS2; shipf TS2; notes TS2; jv TS2 Sector; echo "TS2 dials" >> "$TA/work/docs/dials.md"; echo "# TS2's" > "$TA/work/docs/map/ZzStub.md"; fin TS2 batch/TS2
mk batch/NS; jv NS Land; fin NS batch/NS                                 # a batch with no ship file
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
for pair in RED:Zoning RC:Rollover FM:Zoning FW:Zoning; do b=${pair%%:*}; mk batch/$b; shipf $b; notes $b; jv $b ${pair##*:}; fin $b batch/$b; done
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
# RL1: PS is cut from PP's tip (ready), PT from PR's (sent back: no ship file on its tip); both have a ship file of their own
mk batch/PP; printf '## changelog\n### PP - a predecessor - 2026-10-27, no tag\n\np.\n' > "$TA/work/docs/queue/PP-ship.md"; jv PP Markets; fin PP batch/PP
mk batch/PR; jv PR Markets; fin PR batch/PR
for pair in PP:PS PR:PT; do
    W checkout -q -B "batch/${pair##*:}" "origin/batch/${pair%%:*}"; printf '## changelog\n### %s - a successor - 2026-10-27, no tag\n\np.\n' "${pair##*:}" > "$TA/work/docs/queue/${pair##*:}-ship.md"
    jv "${pair##*:}" Zoning; fin "${pair##*:}" "batch/${pair##*:}"
done
W checkout -q -B scratch origin/main

# --- the cases ---------------------------------------------------------------------------------------------------
export STUB_LOG="$TA/stub.log"; : > "$STUB_LOG"
LABEL="usage: no branch exits 2";               run;                              ok rc_is 2
LABEL="usage: unknown option exits 2";          run --bogus batch/TST;            ok rc_is 2
for g in "GV:changes GameVersion's VERSION" "GG:changes docs/notes/gate.txt" "GH:heading ship.sh does not know" "GQ:edits QUEUE.md" "GC:edits docs/notes/changelog.md" \
         "GE:adds docs/ensemble/9.9.9.md" "GX:no {next}" "GB:no {next} to name it by"; do
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

# no ship file: --no-tag, or a stop
LABEL="a batch with no ship file stops, said";                              run --dry-run batch/NS;  ok stopped "batch/NS has no docs/queue/NS-ship.md"
LABEL="...an assistant's branch too, unless --no-tag";                      run --dry-run chatgpt/ok;  ok stopped "chatgpt/ok has no docs/queue/chatgpt-ok-ship.md"
LABEL="a batch with no ship file ships --no-tag";                           run --dry-run --no-tag batch/NS; ok sh -c "[ $RC = 0 ] && grep -q -F 'not tag it' $TA/out.txt"
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
NR='ZzCheck | exit 1;ZzCheck | a new thing     FAIL;ZzCheck | 1 FAILED'   # a harness with one red line main's record does not have
LABEL="red: exit 4 stops without --red-ok";     STUB_EXIT=4 STUB_REDS="$NR" run --dry-run batch/RED;  ok sh -c "[ $RC = 1 ] && grep -q -F 'red-ok' $TA/out.txt && grep -q -F 'red-line' $TA/out.txt"
LABEL="red: --red-ok without a --red-line is refused at the start (exit 2, the usage)"; run --dry-run --red-ok "his words" batch/RED
ok sh -c "[ $RC = 2 ] && grep -q -F -- '--red-ok needs at least one --red-line' $TA/out.txt && grep -q -F 'usage: ship.sh' $TA/out.txt"
LABEL="red: --red-line without --red-ok is refused (exit 2)"; run --dry-run --red-line "a new thing" batch/RED;  ok sh -c "[ $RC = 2 ] && grep -q -F -- '--red-line is for --red-ok' $TA/out.txt"
LABEL="red: an empty --red-line is refused (exit 2)"; run --dry-run --red-ok "his words" --red-line "" batch/RED;  ok rc_is 2
LABEL="red: exit 4 passes with --red-ok naming the line, the harness's own count with it, the line in the gate"; STUB_EXIT=4 STUB_REDS="$NR" run --dry-run --red-ok "his words" --red-line "a new thing" batch/RED
ok sh -c "[ $RC = 0 ] && grep -q -F \"gate: NEW red on Jerus's word: his words (lines: ZzCheck | exit 1; ZzCheck | a new thing     FAIL; ZzCheck | 1 FAILED)\" $TA/out.txt"
LABEL="red: ...a second red line he did not name still stops, and is named"
STUB_EXIT=4 STUB_REDS='ZzCheck | exit 1;ZzCheck | a new thing     FAIL;ZzCheck | another flaky one     FAIL;ZzCheck | 2 FAILED' run --dry-run --red-ok "his words" --red-line "a new thing" batch/RED
ok sh -c "[ $RC = 1 ] && grep -q -F 'NOT NAMED: ZzCheck | another flaky one' $TA/out.txt && grep -q -F 'no --red-line names' $TA/out.txt"
LABEL="red: ...and the tree is clean";          ok clean
LABEL="red: a crash in another harness is not passed by words naming a line of this one"
STUB_EXIT=4 STUB_REDS='ZzCheck | exit 1;ZzCheck | a new thing     FAIL;YyCheck | exit -1073741819' run --dry-run --red-ok "his words" --red-line "a new thing" batch/RED
ok sh -c "[ $RC = 1 ] && grep -q -F 'NOT NAMED: YyCheck | exit -1073741819' $TA/out.txt"
LABEL="red: ...unless a --red-line names it (repeatable, whole or in part)"
STUB_EXIT=4 STUB_REDS='ZzCheck | exit 1;ZzCheck | a new thing     FAIL;YyCheck | exit -1073741819' run --dry-run --red-ok "his words" --red-line "a new thing" --red-line "YyCheck | exit -10" batch/RED
ok sh -c "[ $RC = 0 ] && grep -q -F 'gate: NEW red on Jerus' $TA/out.txt"
LABEL="red: exit 3 passes";                     STUB_EXIT=3 STUB_REDS='MapCheck | exit 1;MapCheck | stub known FAIL' run --dry-run batch/RED;  ok sh -c "[ $RC = 0 ] && grep -q -F 'gate: known red only' $TA/out.txt"
LABEL="red: exit 1 stops";                      STUB_EXIT=1 run --dry-run batch/RED; ok rc_is 1

LABEL="gate: a passing suite runs once, timing soft"; : > "$STUB_LOG"
run --dry-run batch/RED
ok sh -c "[ $RC = 0 ] && [ \$(wc -l < $STUB_LOG) = 1 ] && grep -q 'mode=suite' $STUB_LOG"
LABEL="docs only: --fast alone, gate.txt untouched"; cp "$TA/main/docs/notes/gate.txt" "$TA/gate.before"; : > "$STUB_LOG"; run batch/DOC
ok sh -c "[ $RC = 0 ] && git -C $TA/main log -1 --format=%B | grep -q -F 'Gate: docs only, --fast'"
LABEL="docs only: tagged $V3"; ok git -C "$TA/origin.git" rev-parse -q --verify "refs/tags/$V3"
LABEL="docs only: gate.txt identical";          ok cmp -s "$TA/main/docs/notes/gate.txt" "$TA/gate.before"
LABEL="docs only: the stub saw --fast only";    ok sh -c "[ \$(wc -l < $STUB_LOG) = 1 ] && grep -q 'mode=fast' $STUB_LOG"
LABEL="push race: the merge and the tag taken back"; STUB_RACE="$TA/work" run batch/RC
ok sh -c "[ $RC = 1 ] && grep -q -F 'main moved on origin' $TA/out.txt && [ -z \"\$(git -C $TA/main tag -l $V4)\" ]"
LABEL="push race: the rerun ships";             run batch/RC; ok sh -c "[ $RC = 0 ] && git -C $TA/origin.git rev-parse -q --verify refs/tags/$V4"

# A suite-only gate still records, tags and publishes in the throwaway origin.
LABEL="ship TM ($V5): suite passes and tags without a timing step"; : > "$STUB_LOG"
STUB_EXIT=3 STUB_REDS='MapCheck | exit 1;MapCheck | stub known FAIL' run batch/TM
ok sh -c "[ $RC = 0 ] && grep -q -F \"pushed main and $V5\" $TA/out.txt && [ \$(wc -l < $STUB_LOG) = 1 ]"
G="$TA/main/docs/notes/gate.txt"
LABEL="TM: gate.txt keeps suite reds and omits timing records"
ok sh -c "grep -q -x -F 'MapCheck | exit 1' $G && ! grep -q -E '^# (timing|figure)' $G"
LABEL="TM: the merge body omits Gate-timing"
ok sh -c "! git -C $TA/main log -1 --format=%B | grep -q '^Gate-timing:'"
LABEL="TM: its header and separator rows did not reach QUEUE.md, its two rows did"
Q="$TA/main/QUEUE.md"
ok sh -c "b() { git -C $TA/main show HEAD^:QUEUE.md | grep -c \"\$1\"; }; [ \$(grep -c '^| item |' $Q) = \$(b '^| item |') ] && [ \$(grep -c '^| batch |' $Q) = \$(b '^| batch |') ] && [ \$(grep -c '^|---|---|---|---|\$' $Q) = \$(b '^|---|---|---|---|\$') ] && [ \$(grep -c '^|---|---|---|\$' $Q) = \$(b '^|---|---|---|\$') ] && [ \$(grep -c '^| TM-C |' $Q) = 1 ] && [ \$(grep -c '^| TM-N |' $Q) = 1 ]"   # as many header and separator rows as main had before TM (QUEUE.md has two | batch | tables since 2026-11-02)
LABEL="TM: the records line counts rows, not headers"; ok sh -c "grep -q -F '1 Pending row(s), 1 Queued row(s) added' $TA/out.txt"
# GF1: each fault is attempted as a real ship against the throwaway origin, and must leave its refs alone.
refs() { git -C "$TA/origin.git" for-each-ref refs/heads/main refs/tags --format='%(refname) %(objectname)'; }
refs > "$TA/refs.before"
local_refs() { git -C "$TA/main" for-each-ref refs/heads/main refs/tags --format='%(refname) %(objectname)'; }
local_refs > "$TA/local.before"
unmoved() {
    refs > "$TA/refs.after"; local_refs > "$TA/local.after"
    cmp -s "$TA/refs.before" "$TA/refs.after" && cmp -s "$TA/local.before" "$TA/local.after" && clean
}
for fault in killed stale missing; do
    LABEL="interrupted $fault suite cannot publish with stale results"
    STUB_FAULT=$fault run batch/RED
    ok sh -c "[ $RC = 1 ] && grep -q 'the check did not finish' $TA/out.txt"
    LABEL="...main, tags and the worktree stayed where they were"; ok unmoved
done
LABEL="a branch moved during its checks is refused"
STUB_WORK="$TA/work" STUB_MOVE=batch/FM run batch/FM
ok sh -c "[ $RC = 1 ] && grep -q 'batch/FM changed during the gate' $TA/out.txt"
LABEL="...main and tags did not move"; ok unmoved
LABEL="a readiness marker withdrawn during the gate is named"
STUB_WORK="$TA/work" STUB_MOVE=batch/FW STUB_WITHDRAW=1 run batch/FW
ok sh -c "[ $RC = 1 ] && grep -q 'withdrew docs/queue/FW-ship.md' $TA/out.txt"
LABEL="...main and tags did not move"; ok unmoved
LABEL="all unnamed reds survive beyond the twelve shown"
MANY=$(awk 'BEGIN {for (i=1; i<=25; i++) printf "ZzCheck | extra red %d FAIL;", i}')
STUB_EXIT=4 STUB_REDS="$MANY" run --dry-run --red-ok words --red-line unrelated batch/RED
ok sh -c "[ $RC = 1 ] && [ \"\$(wc -l < $TA/direct/unnamed.txt)\" = 26 ] && grep -q '26 unnamed red' $TA/out.txt"
# GF1 review: real publication requires its launcher, and cancellation after commit rolls it back.
LABEL="a direct real ship without a guardian is refused"
( cd "$TA/main" && unset CBS_GATE_RUN; sh ./ship.sh batch/RED > "$TA/out.txt" 2>&1 ); RC=$?
ok sh -c "[ $RC = 1 ] && grep -q 'requires tools/gate.sh with a running guardian' $TA/out.txt"
LABEL="...origin and local HEAD stayed put"; ok unmoved
cat > "$TA/main/.git/hooks/post-commit" <<'HOOK'
#!/bin/sh
echo --stop > "$CBS_GATE_RUN/cancelled"
HOOK
chmod +x "$TA/main/.git/hooks/post-commit"
LABEL="a stop after the commit rolls it back before the tag or push"
run batch/RED
ok sh -c "[ $RC = 1 ] && grep -q 'publication cancelled' $TA/out.txt"
LABEL="...main, tags and the tree stayed put"; ok unmoved
rm -f "$TA/main/.git/hooks/post-commit"
# Intercept a failed push attempt, then cancel during retry's sleep.
mkdir -p "$TA/bin"; REAL_GIT=$(command -v git); export REAL_GIT
cat > "$TA/bin/git" <<'GIT'
#!/bin/sh
if [ "$1" = push ]; then echo --stop > "$CBS_GATE_RUN/cancelled"; exit 1; fi
exec "$REAL_GIT" "$@"
GIT
chmod +x "$TA/bin/git"
LABEL="a stop between push attempts rolls back the commit and tag"
PATH="$TA/bin:$PATH" run batch/RED
ok sh -c "[ $RC = 1 ] && grep -q 'tagged' $TA/out.txt && grep -q 'publication cancelled' $TA/out.txt"
LABEL="...origin and local tags stayed put"; ok unmoved
LABEL="the branch publisher option requires a dry run"
CBS_GATE_MAIN="$TA/main" sh "$SRC/tools/gate.sh" SH --branch-gate > "$TA/authority.out" 2>&1; RC=$?
ok sh -c "[ $RC = 2 ] && grep -q -- '--branch-gate requires --dry-run' $TA/authority.out"
LABEL="a harness argument cannot grant the branch publisher option"
CBS_GATE_MAIN="$TA/main" sh "$SRC/tools/gate.sh" SH --branch-gate -- --dry-run > "$TA/authority.out" 2>&1; RC=$?
ok sh -c "[ $RC = 2 ] && grep -q -- '--branch-gate requires --dry-run' $TA/authority.out"
LABEL="a changed branch publisher is judged by main's publisher"
CBS_GATE_MAIN="$TA/main" sh "$SRC/tools/gate.sh" SH --dry-run > "$TA/authority.out" 2>&1; RC=$?
ok sh -c "[ $RC = 0 ] && ! grep -q -F 'ZZ-the-branchs-ship.sh-ran' $TA/authority.out"
LABEL="the explicit dry-run option exercises that branch publisher"
CBS_GATE_MAIN="$TA/main" sh "$SRC/tools/gate.sh" SH --branch-gate --dry-run > "$TA/candidate.out" 2>&1; RC=$?
ok sh -c "[ $RC = 0 ] && grep -q -F 'ZZ-the-branchs-ship.sh-ran' $TA/candidate.out"
LABEL="...authority tests left origin's main and tags put"; ok unmoved
# The real launcher, fresh clone, and its public stop command (Windows uses only the recorded native PID tree).
mkdir -p "$TA/runs"
STUB_FAULT=hold STUB_READY="$TA/ready" CBS_GATE_MAIN="$TA/main" TMP="$TA/runs" sh "$SRC/tools/gate.sh" RED --dry-run > "$TA/launcher.out" 2>&1 &
LAUNCH=$!
for attempt in $(seq 1 300); do [ ! -f "$TA/ready" ] || break; kill -0 "$LAUNCH" 2>/dev/null || break; sleep 0.2; done
RUNDIR=$(sed -n 's/^gate: run //p' "$TA/launcher.out" | head -1)
LABEL="launcher reached the held suite stage"; ok test -f "$TA/ready"
if [ -f "$TA/ready" ]; then
    RED_TIP=$(git -C "$TA/origin.git" rev-parse refs/heads/batch/RED)
    git -C "$TA/origin.git" update-ref refs/heads/batch/RED "$(git -C "$TA/origin.git" rev-parse refs/heads/batch/FM)"
    sh "$SRC/tools/gate.sh" --stop "$RUNDIR" > "$TA/stop.out" 2>&1
    STOP_RC=$?
    LABEL="the launcher's stop command succeeds"; ok test "$STOP_RC" = 0
    LABEL="stop reports its --stop cause and the branch ref that moved"
    ok sh -c "grep -q 'stopped by --stop; moved refs' $TA/stop.out && grep -q 'refs/heads/batch/RED' $TA/stop.out"
    git -C "$TA/origin.git" update-ref refs/heads/batch/RED "$RED_TIP"
else kill "$LAUNCH" 2>/dev/null || :; fi
wait "$LAUNCH"; RC=$?
LABEL="launcher returns nonzero for a stopped gate, retaining its log"
ok sh -c "[ $RC != 0 ] && [ -f '$RUNDIR/ship.log' ] && [ \"\$(cat '$RUNDIR/exit')\" != 0 ]"
LABEL="...main and tags stayed where they were"; ok unmoved
LABEL="...the stopped clone's HEAD is the main it started from"
ok sh -c "[ \"\$(git -C '$RUNDIR/clone' rev-parse HEAD)\" = \"\$(git -C '$RUNDIR/clone' rev-parse origin/main)\" ]"
LABEL="the stop report says unmoved after comparing unchanged origin refs"
sh "$SRC/tools/gate-report.sh" "$RUNDIR" > "$TA/unmoved.out"
ok grep -q 'stopped by --stop; origin refs unmoved' "$TA/unmoved.out"
# TM3 item 7: a batch that changes ship.sh itself - the merge rewrites the file the shell is reading; the run goes on from its copy
LABEL="ship.sh: a branch that changes it merges and gates from the copy, though ship.sh is rewritten in place meanwhile"; : > "$STUB_LOG"; STUB_REWRITE_SHIP=1 run --dry-run batch/SH
ok sh -c "[ $RC = 0 ] && grep -q -F 'batch/SH changes ship.sh: this run goes on from its copy' $TA/out.txt && grep -q -F 'dry run, nothing kept' $TA/out.txt && ! grep -q -F 'ZZ-the-branchs-ship.sh-ran' $TA/out.txt && ! grep -q -i 'syntax error\|not found' $TA/out.txt && grep -q \"shipcopy='[^']*ship-run\.[^']*'\" $STUB_LOG"
LABEL="ship.sh: ...and the tree is as it was"; ok clean
LABEL="ship.sh: the copy keeps the arguments and the clone it was started in (a branch after options)"; run --dry-run --no-tag chatgpt/ok
ok sh -c "[ $RC = 0 ] && grep -q -F 'not tag it' $TA/out.txt"
# RL1: a lane's next batch is cut from its predecessor's tip, and ships only once the predecessor is on main
LABEL="a successor (PS) cut from its predecessor's tip (PP) stops while PP is not on main";  run --dry-run --no-tag batch/PS;  ok stopped "batch/PS is built on PP, which is not on main yet"
LABEL="...so does one whose predecessor was sent back (PR: no ship file on its tip)";        run --dry-run --no-tag batch/PT;  ok stopped "batch/PT is built on PR, which is not on main yet"
LABEL="...the predecessor is not held back by its successor";                                run --dry-run --no-tag batch/PP;  ok rc_is 0
LABEL="...and the tree is clean";                                                            ok clean
LABEL="ship PP, the predecessor";                                                            run --no-tag batch/PP;            ok sh -c "[ $RC = 0 ] && git -C $TA/origin.git log -1 --format=%s main | grep -q -F 'Merge batch/PP'"
LABEL="PP on main: PS ships, with no merge of main in it first, its own ship file the only one"; run --dry-run --no-tag batch/PS;  ok sh -c "[ $RC = 0 ] && grep -q -F \"PS's block on top\" $TA/out.txt"
LABEL="PR is still not on main: PT is still stopped";                                        run --dry-run --no-tag batch/PT;  ok stopped "batch/PT is built on PR, which is not on main yet"
# GR2 fixtures exercise filing, removal, partial selection and the launcher's evidence.
mk batch/KR; shipf KR; jv KR KnownRedsCheck
cat >> "$TA/work/docs/queue/KR-ship.md" <<'EOF'
| ZFIX | Mend the unrelated fixture | fixture |
## known-reds
ZzCheck | unrelated fixture | ZFIX | 2026-11-12 | caused by the fixture
EOF
fin KR batch/KR
cp "$TA/main/docs/notes/gate.txt" "$TA/gr2-gate.before"; : > "$STUB_LOG"
STUB_EXIT=4 STUB_REDS='ZzCheck | exit 1;ZzCheck | unrelated fixture FAIL;ZzCheck | 1 FAILED' run batch/KR
LABEL="known-reds section files its entry and queue row"; ok sh -c "[ $RC = 0 ] && grep -q 'ZzCheck | unrelated fixture | ZFIX' $TA/main/docs/notes/known-reds.txt && grep -q '^| ZFIX ' $TA/main/QUEUE.md"
LABEL="used approval parts are recorded completely in the merge body"; ok sh -c "git -C $TA/main log -1 --format=%B | grep -q '^Known-red: ZzCheck | unrelated fixture | ZFIX | 2026-11-12 | caused by the fixture$'"
LABEL="a harness-only merge preserves gate.txt"; ok cmp -s "$TA/gr2-gate.before" "$TA/main/docs/notes/gate.txt"
LABEL="a harness-only gate reports and runs its pick"; ok sh -c "grep -q 'KnownRedsCheck.*TimingCheck' $STUB_LOG && git -C $TA/main log -1 --format=%B | grep -q 'no game code: 2 harnesses'"
# Exact-record cover must disappear along with the registry exemption.
echo 'ZzCheck | unrelated fixture FAIL' >> "$TA/main/docs/notes/gate.txt"
S git add docs/notes/gate.txt; S git commit -q -m fixture-exact; S git push -q origin main
mk batch/ZFIX; shipf ZFIX; jv ZFIX KnownRedsCheck; fin ZFIX batch/ZFIX
STUB_EXIT=4 STUB_REDS='ZzCheck | exit 1;ZzCheck | unrelated fixture FAIL;ZzCheck | 1 FAILED' run --dry-run batch/ZFIX
LABEL="a mending batch faces its red despite an exact gate record"; ok stopped 'NEW red'
run batch/ZFIX
LABEL="the merge removes its mending entries"; ok sh -c "[ $RC = 0 ] && ! grep -q ' | ZFIX | ' $TA/main/docs/notes/known-reds.txt"
for pair in HF:SimulationEngine AR:AllChecks; do
    b=${pair%%:*}; mk batch/$b; shipf $b; jv "$b" "${pair##*:}"; jv "$b" KnownRedsCheck; fin "$b" batch/$b
    : > "$STUB_LOG"; run --dry-run batch/$b
    LABEL="$b game or runner changes select the full suite"; ok sh -c "[ $RC = 0 ] && grep -q \"args=''\" $STUB_LOG"
done
mk batch/RF; shipf RF; jv RF SimulationEngine; fin RF batch/RF
CBS_GATE_MAIN="$TA/main" CBS_GATE_DIRECTORY="$TA/kept" sh "$SRC/tools/gate.sh" RF --dry-run > "$TA/kept.out" 2>&1; LRC=$?
LABEL="a launcher dry run seals checks and both commits"; ok sh -c "[ $LRC = 0 ] && test -f $TA/kept/checks.complete && test -s $TA/kept/branch.commit && test -s $TA/kept/main.commit"
: > "$STUB_LOG"; run --dry-run --reuse "$TA/kept" batch/RF
LABEL="the same two commits reuse the kept run with no checks rerun"; ok sh -c "[ $RC = 0 ] && test ! -s $STUB_LOG && grep -q 'reused $TA/kept' $TA/out.txt"
echo 'Markdown-only advancement' > "$TA/main/docs/queue/reuse-fixture.md"; S git add docs/queue/reuse-fixture.md; S git commit -q -m fixture-docs; S git push -q origin main
: > "$STUB_LOG"; run --dry-run --reuse "$TA/kept" batch/RF
LABEL="Markdown-only main advancement keeps evidence reusable"; ok sh -c "[ $RC = 0 ] && test ! -s $STUB_LOG"
cp -R "$TA/kept" "$TA/unlaunched"; rm "$TA/unlaunched/launcher"
: > "$STUB_LOG"; run --dry-run --reuse "$TA/unlaunched" batch/RF
LABEL="working-folder evidence is refused and checks run"; ok sh -c "[ $RC = 0 ] && test -s $STUB_LOG && grep -q 'kept run refused' $TA/out.txt"
W fetch -q origin; W checkout -q -B batch/RF origin/batch/RF; printf 'another file\n' > "$TA/work/docs/queue/RF-other.md"; W add docs/queue/RF-other.md; W commit -q -m moved; W push -q origin batch/RF
: > "$STUB_LOG"; run --dry-run --reuse "$TA/kept" batch/RF
LABEL="another branch file changed after the run refuses reuse"; ok sh -c "[ $RC = 0 ] && test -s $STUB_LOG && grep -q 'kept run refused' $TA/out.txt"
# A new launch captures the moved branch, then a Java main change invalidates it.
CBS_GATE_MAIN="$TA/main" CBS_GATE_DIRECTORY="$TA/kept2" sh "$SRC/tools/gate.sh" RF --dry-run > "$TA/kept2.out" 2>&1
printf '\n// main advanced\n' >> "$TA/main/src/main/java/ham/citybuildersim/Timing.java"; S git add -A; S git commit -q -m fixture-java; S git push -q origin main
: > "$STUB_LOG"; run --dry-run --reuse "$TA/kept2" batch/RF
LABEL="main gaining Java refuses the kept run"; ok sh -c "[ $RC = 0 ] && test -s $STUB_LOG && grep -q 'kept run refused' $TA/out.txt"
GEN_BEFORE=$(grep -c '^// regenerated' "$TA/main/docs/dials.md")
STUB_TOUCHMAPS=1 STUB_EXIT=4 STUB_REDS='ZzCheck | exit 1;ZzCheck | late unnamed FAIL;ZzCheck | 1 FAILED' CBS_GATE_MAIN="$TA/main" CBS_GATE_DIRECTORY="$TA/red-kept" sh "$SRC/tools/gate.sh" RF --dry-run > "$TA/red-kept.out" 2>&1
: > "$STUB_LOG"; run --dry-run --reuse "$TA/red-kept" batch/RF
LABEL="a completed run with still-unnamed reds cannot ship"; ok stopped 'NEW red'
printf '\nZzCheck | late unnamed | FIX | 2026-11-12 | fixture\n' >> "$TA/main/docs/notes/known-reds.txt"; S git add docs/notes/known-reds.txt; S git commit -q -m fixture-name; S git push -q origin main
: > "$STUB_LOG"; run --reuse "$TA/red-kept" batch/RF
LABEL="naming a stopped run's red permits reuse and publication"; ok sh -c "[ $RC = 0 ] && test ! -s $STUB_LOG && git -C $TA/origin.git log -1 --format=%B main | grep -q 'reused $TA/red-kept'"

LABEL="reuse restores the generated indexes produced by the kept checks"; ok test "$(grep -c '^// regenerated' "$TA/main/docs/dials.md")" = "$((GEN_BEFORE + 1))"
for b in FDOC FSC; do
    mk batch/$b
    printf '## changelog\n### %s - fixture, no tag (process)\n\nFixture.\n' "$b" > "$TA/work/docs/queue/$b-ship.md"
    [ "$b" != FSC ] || printf '#!/bin/sh\n# fixture\n' > "$TA/work/tools/zz-reuse.sh"
    fin "$b" batch/$b
    CBS_GATE_MAIN="$TA/main" CBS_GATE_DIRECTORY="$TA/kept-$b" sh "$SRC/tools/gate.sh" "$b" --dry-run --no-tag > "$TA/kept-$b.out" 2>&1
    : > "$STUB_LOG"; run --no-tag --reuse "$TA/kept-$b" batch/$b
    LABEL="$b zero-harness gate reuses its completed fast run"; ok sh -c "[ $RC = 0 ] && test ! -s $STUB_LOG && git -C $TA/main log -1 --format=%B | grep -q 'reused $TA/kept-$b'"
done

# The builder runs before its own notes and readiness marker are committed.
mk batch/RQ; jv RQ SimulationEngine; fin RQ-source batch/RQ
CBS_GATE_MAIN="$TA/main" CBS_GATE_DIRECTORY="$TA/ready-kept" sh "$SRC/tools/gate.sh" RQ --dry-run --no-tag > "$TA/ready-kept.out" 2>&1
printf '# RQ notes\n' > "$TA/work/docs/queue/RQ-notes.md"; fin RQ-notes batch/RQ
printf '<!-- gate-run: %s -->\n## changelog\n### RQ - ready, no tag (process)\n\nReady fixture.\n' "$TA/ready-kept" > "$TA/work/docs/queue/RQ-ship.md"; fin RQ-ready batch/RQ
: > "$STUB_LOG"; run --no-tag batch/RQ
LABEL="Ready flow reuses the dry run after notes and ship commits are pushed"; ok sh -c "[ $RC = 0 ] && test ! -s $STUB_LOG && git -C $TA/main log -1 --format=%B | grep -q 'reused $TA/ready-kept'"

kill "$FIXTURE_GUARD" 2>/dev/null || :; wait "$FIXTURE_GUARD" 2>/dev/null || :
echo "selftest: $((N - FAILS)) of $N passed"
[ "${CBS_SHIPTEST_KEEP:-0}" = 1 ] || { cd /; rm -rf "$TA"; }
exit "$FAILS"
