#!/bin/sh
# ---------------------------------------------------------------------------
#  The orchestrator's gate and ship in one call: a batch's branch merged into
#  main, its records written, checked on the merge, tagged with the version
#  this ship sets and pushed.
#
#      ./ship.sh batch/I12                      gate, merge, tag, push
#      ./ship.sh batch/I12 -m "the title"       the same, the merge's title given
#      ./ship.sh batch/I12 -F body.txt          the same, the message's body (and trailers) from a file
#      ./ship.sh --dry-run batch/I12            the gate alone: nothing committed or pushed
#      ./ship.sh --no-tag some/branch           a merge that is not a version's (tools, records): no tag
#      ./ship.sh --version 0.9.0 batch/I12      the version {next} takes, instead of the last tag's patch + 1
#      ./ship.sh --red-ok "his words" batch/I12 ships past a red line main's record does not name, and past a timing
#                                               red that is over main's by more than the margin (his word only; the
#                                               merge's body names the lines it passed)
#      ./ship.sh --dry-run batch/I12 -- money bank   the suite only the harnesses named (to try the script)
#
#  Run it in the clone where main is checked out, with nothing uncommitted.
#  Exit 0 shipped (or a dry run passed), 1 stopped, 2 usage. In order,
#  stopping at the first that fails:
#    1. main is brought up to origin/main and the tags fetched (fast-forward only);
#    2. the branch, local or origin's, with something main does not have;
#    3. the last version tag (the highest x.y.z) must be main's GameVersion.VERSION;
#    4. the branch's ship file docs/queue/<id>-ship.md decides the way: with
#       one, the new way (the version and the records are set here); without,
#       the old way (the branch wrote its own: its version, its changelog
#       block, its Queued row out), said in a line;
#    5. stops before any merge: "-- names" without --dry-run (a partial suite
#       never ships); a branch that changes docs/notes/gate.txt, or adds a
#       docs/queue/*-ship.md that is not its own; with --no-tag, one that
#       changes GameVersion's VERSION line (main's VERSION would stay untagged
#       and step 3 would stop every later ship); in the new way, one that
#       changes GameVersion's VERSION line, adds a docs/ensemble/<x.y.z>.md,
#       edits QUEUE.md or the changelog, has a "## " heading in its ship file
#       other than changelog, pending and queued, a pending or queued row with
#       no first cell, asks for a version ({next}) with --no-tag or without a
#       "## changelog" block whose first line starts "### {next} ", or is a
#       batch/ branch with no {next} and no --no-tag; in the old way, a batch/
#       branch (without --no-tag) whose VERSION is tagged already or does not
#       sort above the last tag; a --version that is not x.y.z, does not
#       sort above the last tag or is tagged here or on origin;
#    6. the version: NEXT is --version, or the last tag's patch + 1, when the
#       ship file has {next}; the tag is NEXT unless --no-tag;
#    7. the merge, uncommitted; a conflict only in the generated docs (docs/map/,
#       dials.md, month-order.md, harnesses.md) takes the branch's side, said
#       in a line, and the checks regenerate them; any other conflict is
#       aborted and its files named. From here every stop undoes the merge;
#    8. the records (new way), before the checks so the compile, the suite
#       and the maps see NEXT, all of them staged so that one abort undoes
#       them: {next} filled in the ship file, docs/queue/<id>-notes.md and
#       docs/ensemble/<id>.md, GameVersion's VERSION set; the ship file's
#       "## changelog" section on top of docs/notes/changelog.md (after its
#       first "---"); QUEUE.md's Queued row for the id out, the "## pending"
#       rows at the top of the Pending table, the "## queued" rows at the end
#       of the Queued table; the baseline moved to docs/ensemble/<NEXT>.md;
#       the ship file deleted. Of its pending and queued sections only the
#       table's data rows are taken: a header row (first cell "item" or
#       "batch") and a separator row are dropped. A {next} left in another
#       file the branch changed is named, not filled;
#    9. the checks: batch-check.sh on the merge (compile, suite, maps, Stale,
#       line endings), the suite timed soft so that timing is judged once;
#       exit 0 and 3 (red only as main's record has it) pass, 4 (a new red
#       line) passes only with --red-ok, 1 and 2 stop. On a chatgpt/ or gemini/
#       branch tools.Names too: said, never a gate. Then the timing harnesses
#       alone and hard (batch-check.sh --timing), after a quiet minute with no
#       other batch's JVM (TM2); its exit is 0, 3 or 4 alike, and the record
#       is no shortcut. Its red lines less the lines the suite step already
#       judged (main's recorded reds, or reds passed with --red-ok) and less main's recorded
#       lines are the timing's own: none, and it passes with no further run
#       (a red passed with --red-ok in one of the timing harnesses comes back
#       here and is not judged twice). Otherwise (TM3) each timing red - a line
#       whose figure missed its budget, read from the run's target/checks/timing.tsv,
#       never from the label - is judged side by side with main: the red harnesses
#       are run again CBS_TIMING_ROUNDS times (3) with main's classes (target/main-timing,
#       a worktree of main) and the merge's in turn, back to back, and the red
#       passes, named with the paired medians, unless the batch's median is more
#       than the margin (15%, tools/timing-pair.awk) over main's. A red that is
#       no timing red, one over the margin and one with no pair to compare is the
#       batch's and stops - unless --red-ok, whose words then pass those lines too,
#       named in the body; no red passed so is ever a recorded one;
#       A merge that changes only .md files outside docs/ensemble/ runs
#       batch-check.sh --fast alone;
#   10. docs/notes/gate.txt written (every gate that ran the suite) and added:
#       "#" lines (the merge's title, the date, whether earth-2.bin was named,
#       the suite's and the timing's summary lines, each timing red that passed
#       "# timing red within N% of main's (paired): ..." with the paired medians,
#       each passed on his word "# timing red passed on Jerus's word ..."), then
#       the suite's red lines, which are the next ship's "main's recorded reds". A
#       timing red is a "#" line only, never a recorded one: the next ship judges
#       it side by side with main again. Only this script writes it;
#   11. --dry-run says what it would commit, tag and push, and the records it
#       would write, then takes the merge back: the tree and HEAD as they were;
#   12. the merge committed as "Merge <branch>: <title>, <version>", the title
#       QUEUE.md's on main unless -m gives one, the body the -F file then
#       "Gate: ..." lines (green, known red only, NEW red on his word, or
#       docs only);
#   13. tagged NEXT (or, the old way, the branch's VERSION unless tagged
#       already);
#   14. main and the tag pushed together (git push --atomic), a failed push
#       tried four more times; when main moved on origin meanwhile, the
#       merge and the tag are taken back here: run ship.sh again, the number
#       comes from freshly fetched tags.
#  A failure before 12 leaves main where it was. It prints a line a step.
#
#  The ship file, docs/queue/<id>-ship.md (docs/batch.md has the shape):
#      ## changelog       "### {next} - id, title - date, state, see ..." and one paragraph
#      ## pending         table rows, optional
#      ## queued          table rows the batch decided, optional
#  {next} is the version this script sets; a batch branch never writes one.
#
#  WHY (2026-10-13): the gate was a conversation - a dozen calls, each
#  re-reading the session, to do the same seven things in the same order.
#  The judgement stays the orchestrator's (whether the batch did what its
#  brief asked, the playtest's trace); the mechanics are this.
#  WHY (BP1, 2026-10-25): side by side, batches each took main + 1 for their
#  version and had to re-merge, each wrote the same top of the changelog and
#  QUEUE.md (a conflict for the second), and every ship since FW11 was
#  finished by hand past main's known reds, a timing red compared by hand and
#  a map conflict resolved by hand. The version, the records, those reds and
#  that conflict are now this script's, and a dry run shows the real merge.
#  WHY (BP1 review, 2026-10-25): a red passed with --red-ok in one of the
#  eleven timing harnesses came back in the hard timing run and was judged
#  against main's run, where it was never red; a timing red accepted as main's
#  was written to gate.txt as a plain line, so a later regression on the same
#  assertion printed the same line and passed as "recorded"; a ship file with
#  {next} but no changelog block tagged a version with no paragraph, and one
#  with a copied table header corrupted QUEUE.md; and an old-style branch could
#  ship a version main had tagged already, or a --no-tag merge that moved
#  VERSION and stopped every later ship at step 3. tools/ship-selftest.sh runs
#  this script against a throwaway origin with a stub for the checks (no JVM):
#  run it after changing this file.
#  WHY (TM3, 2026-10-27): the timing was judged once on the merge and then against
#  one run of main's, minutes later. On 2026-10-26 HY1, MS2 and CC1-S stopped on map
#  lines main itself misses under Jerus's running game (a 59.9 ms median against 58;
#  main's own run of it read 75.5 earlier that day), and two shipped only through a
#  copy of this script changed by hand at that one stop. Now a timing red is timed
#  again with main and the merge in turn, in the same minutes, and counts only if the
#  merge is the slower by more than a margin; and --red-ok reaches that stop, naming
#  the lines. And the script runs from a copy of itself: a batch that changes ship.sh
#  (TM3 did) rewrote it under the shell that was reading it at the merge.
# ---------------------------------------------------------------------------

# TM3: run from a copy of this script, made before anything is merged. A shell reads its script as it goes, at the
# offsets of the file it opened, and a batch that changes ship.sh rewrites that file in the merge. CBS_SHIP_SHELL is the
# shell that runs the copy (the self-test runs it under dash); CBS_SHIP_COPY, once set, says this is the copy.
if [ -z "$CBS_SHIP_COPY" ]; then
    CBS_SHIP_HOME=$(cd "$(dirname "$0")" && pwd) || exit 1
    CBS_SHIP_COPY=$(mktemp "${TMPDIR:-/tmp}/ship-run.XXXXXX") || { echo "ship      cannot make a copy of itself in ${TMPDIR:-/tmp}"; exit 1; }
    cp "$0" "$CBS_SHIP_COPY" || { rm -f "$CBS_SHIP_COPY"; echo "ship      cannot copy $0 to $CBS_SHIP_COPY"; exit 1; }
    export CBS_SHIP_HOME CBS_SHIP_COPY
    exec ${CBS_SHIP_SHELL:-sh} "$CBS_SHIP_COPY" "$@"
fi
cd "${CBS_SHIP_HOME:-$(dirname "$0")}" || exit 1

USAGE='usage: ship.sh [--dry-run] [--no-tag] [--version X.Y.Z] [--red-ok "his words"] <branch> [-m title] [-F body] [-- harness names]'
DRY=0; NOTAG=0; BRANCH=""; TITLE=""; BODY=""; OVERRIDE=""; REDOK=""
while [ $# -gt 0 ]; do
    case "$1" in
        --dry-run) DRY=1 ;;
        --no-tag) NOTAG=1 ;;
        --version) [ $# -ge 2 ] || { echo "$USAGE"; exit 2; }; shift; OVERRIDE="$1" ;;
        --red-ok) [ $# -ge 2 ] && [ -n "$2" ] || { echo "$USAGE"; exit 2; }; shift; REDOK="$1" ;;
        -m) [ $# -ge 2 ] || { echo "$USAGE"; exit 2; }; shift; TITLE="$1" ;;
        -F) [ $# -ge 2 ] || { echo "$USAGE"; exit 2; }; shift; BODY="$1"; [ -f "$BODY" ] || { echo "ship      no file $BODY"; exit 2; } ;;
        --) shift; break ;;
        -*) echo "ship      unknown option $1"; echo "$USAGE"; exit 2 ;;
        *) [ -z "$BRANCH" ] || { echo "ship      two branches given: $BRANCH and $1"; echo "$USAGE"; exit 2; }; BRANCH="$1" ;;
    esac
    shift
done
[ -n "$BRANCH" ] || { echo "$USAGE"; exit 2; }

GV=src/main/java/ham/citybuildersim/GameVersion.java
GENERATED="docs/map docs/dials.md docs/month-order.md docs/harnesses.md"
GENRE='^docs/(map/|dials\.md$|month-order\.md$|harnesses\.md$)'
T=target/ship-tmp          # this script's scratch folder, gone when it ends
MT=target/main-timing      # the worktree of main whose classes the paired timing rounds run (TM3)
MERGING=0

say() { echo "ship      $*"; }
die() { [ "$MERGING" = 0 ] || undo; echo "ship      STOPPED - $*"; exit 1; }
retry() {   # a network call, tried again after 2, 4, 8 and 16 seconds
    for wait in 2 4 8 16 0; do
        "$@" >/dev/null 2>&1 && return 0
        [ "$wait" = 0 ] && return 1
        sleep "$wait"
    done
}
tagged() { git rev-parse -q --verify "refs/tags/$1" >/dev/null || git ls-remote --exit-code --tags origin "refs/tags/$1" >/dev/null 2>&1; }
vof() {   # GameVersion.VERSION as $1 (a ref) has it
    git show "$1:$GV" 2>/dev/null | sed -n 's/^ *public static final String VERSION = "\([^"]*\)";.*/\1/p' | head -1
}
undo() {   # the merge taken back: the maps the checks rewrote first, or the abort refuses, and the new ones they wrote
    MERGING=0
    git checkout -q -- $GENERATED 2>/dev/null
    git merge --abort 2>/dev/null || git reset -q --hard HEAD
    git clean -fq -- $GENERATED
    LEFT=$(git status --porcelain | head -5 | tr '\n' ' ')
    [ -z "$LEFT" ] || say "left in the tree after the abort (not tracked by the merge): $LEFT"
}
cleanup() {   # on every way out: an unfinished merge taken back, main's timing worktree and the scratch folder removed
    [ "$MERGING" = 0 ] || undo
    if [ -d "$MT" ]; then
        git worktree remove --force "$MT" >/dev/null 2>&1
        rm -rf "$MT"
        git worktree prune
    fi
    rm -rf "$T"
    [ -z "$CBS_SHIP_HOME" ] || rm -f "$CBS_SHIP_COPY" 2>/dev/null   # the copy this run is (TM3); a shell on Windows may still hold it open: left, it is a few kilobytes in the temp folder
}
trap cleanup EXIT
trap 'exit 130' INT TERM HUP

[ "$(git rev-parse --abbrev-ref HEAD)" = main ] || die "main is not checked out here"
[ -z "$(git status --porcelain)" ] || die "the tree has uncommitted changes"
rm -rf "$T"; mkdir -p "$T"

# 1. main up to date, the tags fetched
retry git fetch -q origin main || die "cannot fetch origin main"
retry git fetch -q --tags origin || die "cannot fetch origin's tags (a local tag that differs from origin's? see git fetch --tags origin)"
git merge --ff-only -q origin/main 2>/dev/null || die "main has commits origin/main does not: push or reset them first"

# 2. the branch: local, or origin's
if git rev-parse -q --verify "refs/heads/$BRANCH" >/dev/null; then
    REF="$BRANCH"
else
    retry git fetch -q origin "$BRANCH" || die "no branch $BRANCH here or on origin"
    REF=$(git rev-parse -q --verify 'FETCH_HEAD^{commit}') || die "cannot read origin's $BRANCH"
fi
AHEAD=$(git rev-list --count "main..$REF")
[ "$AHEAD" -gt 0 ] || die "$BRANCH has nothing main does not"
CHANGED=$(git diff --name-only --no-renames "main...$REF")   # what the branch itself changes

ID="${BRANCH#batch/}"; ISBATCH=0
if [ "$ID" != "$BRANCH" ]; then ISBATCH=1; else ID=$(printf '%s' "$BRANCH" | tr '/' '-'); fi   # another branch's "/" only for its ship file's name
SHIPF="docs/queue/$ID-ship.md"

# 3. the last version tag is main's version
LAST=$(git tag -l | grep -E '^[0-9]+\.[0-9]+\.[0-9]+$' | sort -t. -k1,1n -k2,2n -k3,3n | tail -1)
[ -n "$LAST" ] || die "no x.y.z tag here (git fetch --tags origin)"
MAINV=$(vof main)
[ "$MAINV" = "$LAST" ] || die "main's VERSION is ${MAINV:-unreadable}, the last version tag $LAST: tag or mend main first"
LASTPLUS=${LAST%.*}.$(( ${LAST##*.} + 1 ))
above_last() {   # $1 sorts above the last version tag
    [ "$1" != "$LAST" ] && [ "$(printf '%s\n%s\n' "$LAST" "$1" | sort -t. -k1,1n -k2,2n -k3,3n | tail -1)" = "$1" ]
}

# 4. the way: a ship file on the branch is the new way
queued() {   # the "what" of the row for $ID under Queued in QUEUE.md as $1 has it
    git show "$1:QUEUE.md" 2>/dev/null | sed '/^## Pending/,$d' \
        | awk -F'|' -v id="$ID" '{ gsub(/^ +| +$/, "", $2) } $2 == id { gsub(/^ +| +$/, "", $3); print $3; exit }'
}
sect() {   # the lines of the ship file's section $1 (the file $2, else the one in the tree), its heading excluded
    tr -d '\r' < "${2:-$SHIPF}" | awk -v s="## $1" '$0 == s { on = 1; next } /^## / { on = 0 } on'
}
shiprows() {   # the table rows of section $1 that are data: no separator row, no header row (first cell item or batch)
    sect "$1" "$2" | grep '^|' | grep -v -E '^\|[-| :]+\|[[:space:]]*$' \
        | awk -F'|' '{ c = tolower($2); gsub(/^ +| +$/, "", c) } c != "item" && c != "batch"'
}
NEW=0; WANT=0; NEXT=""
if git cat-file -e "$REF:$SHIPF" 2>/dev/null; then
    NEW=1
    git show "$REF:$SHIPF" | tr -d '\r' > "$T/ship-on-branch"
    grep -q -F '{next}' "$T/ship-on-branch" && WANT=1
    say "ship file $SHIPF: the version and the records are set here"
else
    say "old-style: no $SHIPF - its records as it wrote them"
fi

# 5. stops, before any merge
[ "$#" -eq 0 ] || [ "$DRY" = 1 ] || die "a partial suite never ships: '-- harness names' only with --dry-run"
printf '%s\n' "$CHANGED" | grep -q -x 'docs/notes/gate.txt' && die "$BRANCH changes docs/notes/gate.txt: only ship.sh writes the gate's record"
OTHERSHIP=$(git diff --name-only --diff-filter=A --no-renames "main...$REF" | grep -E '^docs/queue/.+-ship\.md$' | grep -v -x -F "$SHIPF" | head -3 | tr '\n' ' ')
[ -z "$OTHERSHIP" ] || die "$BRANCH adds a ship file for another id: ${OTHERSHIP% }; this branch's is $SHIPF (the branch's name, each / a -: docs/batch.md)"
VCHG=0
git diff -U0 --no-renames "main...$REF" -- "$GV" | grep -q -E '^[+-] *public static final String VERSION = ' && VCHG=1
if [ "$NEW" = 1 ]; then
    [ "$VCHG" = 0 ] || die "$BRANCH changes GameVersion's VERSION line: a batch writes {next} in its ship file, and ship.sh sets the version (docs/batch.md)"
    ADDED=$(git diff --name-only --diff-filter=A --no-renames "main...$REF" | grep -E '^docs/ensemble/[0-9]+\.[0-9]+\.[0-9]+\.md$' | head -1)
    [ -z "$ADDED" ] || die "$BRANCH adds $ADDED: a batch commits its baseline as docs/ensemble/$ID.md, and ship.sh names it by the version"
    RECORDS=$(printf '%s\n' "$CHANGED" | grep -x -e QUEUE.md -e docs/notes/changelog.md | tr '\n' ' ' | sed 's/ *$//')
    [ -z "$RECORDS" ] || die "$BRANCH edits $RECORDS: records go in the ship file $SHIPF (docs/batch.md)"
    BADH=$(grep '^## ' "$T/ship-on-branch" | grep -v -x -E '## (changelog|pending|queued)' | head -3 | tr '\n' ' ')
    [ -z "$BADH" ] || die "$SHIPF has a heading ship.sh does not know: $BADH- only '## changelog', '## pending' and '## queued'"
    if [ "$WANT" = 0 ] && git cat-file -e "$REF:docs/ensemble/$ID.md" 2>/dev/null; then
        die "docs/ensemble/$ID.md is a baseline and $SHIPF has no {next} to name it by"
    fi
    for SEC in pending queued; do   # a row with no id in its first cell is not a row of QUEUE.md
        NOID=$(shiprows "$SEC" "$T/ship-on-branch" | awk -F'|' '{ c = $2; gsub(/^ +| +$/, "", c) } c == "" { print; exit }')
        [ -z "$NOID" ] || die "$SHIPF has a ## $SEC row with no first cell (the id): $(printf '%s' "$NOID" | cut -c1-60)"
    done
    if [ "$WANT" = 1 ]; then
        [ "$NOTAG" = 0 ] || die "$SHIPF asks for a version ({next}) and --no-tag was given"
        sect changelog "$T/ship-on-branch" | awk 'NF { print; exit }' | grep -q '^### {next} ' \
            || die "$SHIPF asks for a version ({next}) but has no '## changelog' block headed '### {next} ...': the version would ship with no paragraph"
    elif [ "$ISBATCH" = 1 ] && [ "$NOTAG" = 0 ]; then
        die "$SHIPF has no {next}: write {next} in its changelog heading, or ship it --no-tag"
    fi
elif [ "$NOTAG" = 1 ]; then   # a --no-tag merge carries main's version: one that moves it leaves main untagged and stops every later ship (step 3)
    [ "$VCHG" = 0 ] || die "$BRANCH changes GameVersion's VERSION line and --no-tag was given: main's VERSION would stay untagged and stop every later ship; leave VERSION as main has it"
elif [ "$ISBATCH" = 1 ]; then   # the old way: a number another batch took is a stop, not a second copy of a version (lesson f)
    BV=$(vof "$REF")
    [ -n "$BV" ] || die "cannot read GameVersion's VERSION on $BRANCH"
    ! tagged "$BV" || die "$BRANCH's VERSION $BV is tagged already: merge main and bump to $LASTPLUS, or write a ship file (--no-tag if no player sees the change)"
    above_last "$BV" || die "$BRANCH's VERSION $BV does not sort above the last version tag $LAST: merge main and bump to $LASTPLUS, or write a ship file (--no-tag if no player sees the change)"
fi
if [ -n "$OVERRIDE" ]; then
    [ "$WANT" = 1 ] || die "--version names the number {next} takes, and $BRANCH has no {next}"
    printf '%s\n' "$OVERRIDE" | grep -q -E '^[0-9]+\.[0-9]+\.[0-9]+$' || die "--version $OVERRIDE is not x.y.z"
    above_last "$OVERRIDE" || die "--version $OVERRIDE does not sort above the last version tag $LAST"
    ! tagged "$OVERRIDE" || die "--version $OVERRIDE is tagged already, here or on origin"
fi

# 6. the version this ship sets
if [ "$WANT" = 1 ]; then
    NEXT=${OVERRIDE:-$LASTPLUS}
    ! tagged "$NEXT" || die "$NEXT is tagged already, here or on origin (the tags above $LAST?): fetch the tags and run again"
fi

# the merge's title: QUEUE.md's row on main (before the records take it out), or -m
[ -n "$TITLE" ] || TITLE=$(queued main | awk '{ print tolower(substr($0, 1, 1)) substr($0, 2) }')
[ -n "$TITLE" ] || TITLE=$(git log -1 --format=%s "$REF")
if [ "$NEW" = 0 ] && [ "$ISBATCH" = 1 ]; then   # the old way: the branch deleted its own row
    [ -z "$(queued "$REF")" ] || die "QUEUE.md on $BRANCH still queues $ID: delete its row in the batch (what it left undone under Pending)"
fi

# 7. the merge, not yet committed
if printf '%s
' "$CHANGED" | grep -q -x 'ship.sh'; then
    say "$BRANCH changes ship.sh: this run goes on from its copy ($0), so the merge cannot rewrite it under the shell; the new one runs from the next ship"
fi
HEAD0=$(git rev-parse HEAD)
MERGING=1
if ! git merge --no-ff --no-commit -q "$REF" >/dev/null 2>&1; then
    CONFLICTS=$(git diff --name-only --diff-filter=U)
    OTHER=$(printf '%s\n' "$CONFLICTS" | grep -v -E "$GENRE" | grep .)
    if [ -n "$CONFLICTS" ] && [ -z "$OTHER" ]; then   # only generated docs: the branch's side, regenerated by the checks
        for p in $CONFLICTS; do
            if git cat-file -e "$REF:$p" 2>/dev/null; then git checkout -q "$REF" -- "$p"; else git rm -q -- "$p"; fi
        done
        say "generated docs in conflict: the branch's, regenerated below ($(printf '%s\n' "$CONFLICTS" | wc -l | tr -d ' ') file(s))"
    elif [ -n "$OTHER" ]; then
        die "$BRANCH conflicts with main in: $(printf '%s\n' "$OTHER" | head -10 | tr '\n' ' ')"
    else
        die "the merge of $BRANCH failed with no conflict to name: run git merge --no-ff $BRANCH by hand to see why"
    fi
fi
say "merged $AHEAD commit(s) of $BRANCH, uncommitted"

# 8. the records (new way): staged, so one abort undoes them
NFILL=0; NPEND=0; NQUEUED=0; GONE=0; BLOCK=0
fill() {   # {next} -> NEXT in the file $1, when it is in the tree and has one
    [ -f "$1" ] && grep -q -F '{next}' "$1" || return 0
    sed "s/{next}/$NEXT/g" "$1" > "$T/fill" && cat "$T/fill" > "$1" && git add -- "$1" || die "cannot fill {next} in $1"
    NFILL=$((NFILL + 1))
}
records() {
    if [ "$WANT" = 1 ]; then
        ENSTOKEN=0; [ -f "docs/ensemble/$ID.md" ] && grep -q -F '{next}' "docs/ensemble/$ID.md" && ENSTOKEN=1
        fill "$SHIPF"; fill "docs/queue/$ID-notes.md"; fill "docs/ensemble/$ID.md"   # exactly these: no glob catches FW10 for FW1
        [ "$(grep -c '^ *public static final String VERSION = "' "$GV")" = 1 ] || die "$GV has not exactly one VERSION line"
        sed "s/^\( *public static final String VERSION = \"\)[^\"]*\";/\1$NEXT\";/" "$GV" > "$T/gv" && cat "$T/gv" > "$GV" && git add -- "$GV" \
            || die "cannot set VERSION in $GV"
        [ "$(grep -c "^ *public static final String VERSION = \"$NEXT\";" "$GV")" = 1 ] || die "$GV does not read VERSION $NEXT once after the edit"
    fi
    if [ -f "docs/ensemble/$ID.md" ]; then   # the baseline: named by the batch, renamed to the version
        [ "$WANT" = 1 ] || die "docs/ensemble/$ID.md is a baseline with no version: its ship file needs {next}"
        [ ! -e "docs/ensemble/$NEXT.md" ] || die "docs/ensemble/$NEXT.md exists already"
        [ "$ENSTOKEN" = 1 ] || say "note: docs/ensemble/$ID.md has no {next}: its first line is set to $NEXT, the text under it is as written"
        git mv -f "docs/ensemble/$ID.md" "docs/ensemble/$NEXT.md" || die "cannot move the baseline to docs/ensemble/$NEXT.md"
        sed "1s/^# Ensemble baseline: .*/# Ensemble baseline: $NEXT/" "docs/ensemble/$NEXT.md" > "$T/ens" && cat "$T/ens" > "docs/ensemble/$NEXT.md" \
            && git add -- "docs/ensemble/$NEXT.md" || die "cannot rewrite the baseline's first line"
    fi
    # the changelog: the block on top, after the first line that is only ---, one blank line each side
    sect changelog | awk 'NF { while (b) { print ""; b-- } s = 1; print; next } s { b++ }' > "$T/block"
    if [ -s "$T/block" ]; then
        awk -v f="$T/block" '
            skip { if ($0 == "") next; skip = 0 }
            { print }
            !d && $0 == "---" { print ""; while ((getline l < f) > 0) print l; close(f); print ""; d = 1; skip = 1 }
            END { if (!d) exit 3 }' docs/notes/changelog.md > "$T/cl" || die "docs/notes/changelog.md has no line that is only --- to put the block after"
        cat "$T/cl" > docs/notes/changelog.md
        BLOCK=1
    fi
    # QUEUE.md: its Queued row out, the pending rows at the top of Pending, the queued rows at the end of Queued
    shiprows pending > "$T/prows"; shiprows queued > "$T/qrows"   # data rows only: no header, no separator
    NPEND=$(wc -l < "$T/prows" | tr -d ' '); NQUEUED=$(wc -l < "$T/qrows" | tr -d ' ')
    awk -v id="$ID" -v pf="$T/prows" -v qf="$T/qrows" -v cnt="$T/gone" '
        function put(f,  l) { while ((getline l < f) > 0) print l; close(f) }
        /^## / { sec = substr($0, 4) }
        sec == "Queued" && /^\|/ { k = $0; sub(/^\| */, "", k); sub(/ *\|.*$/, "", k); if (k == id) { gone++; next } }
        sec == "Queued" && !/^\|/ && last ~ /^\|/ && !qd { put(qf); qd = 1 }
        { print; last = $0 }
        sec == "Pending" && /^\|[-| :]+\|$/ && !pd { put(pf); pd = 1 }
        END { print gone + 0 > cnt; if (!qd || !pd) exit 3 }' QUEUE.md > "$T/q" \
        || die "QUEUE.md has no Queued table with an end or no Pending table with a header line to put the rows in"
    cat "$T/q" > QUEUE.md
    GONE=$(cat "$T/gone")
    git add -- docs/notes/changelog.md QUEUE.md
    git rm -q -f -- "$SHIPF" || die "cannot delete $SHIPF"
    if [ "$WANT" = 1 ]; then   # a {next} left in another file the branch changed: said, not filled
        printf '%s\n' "$CHANGED" | grep -v -x -F "$SHIPF" > "$T/changed"
        if [ -s "$T/changed" ]; then
            STRAY=$(xargs git --literal-pathspecs grep -l -F '{next}' -- < "$T/changed" 2>/dev/null | head -3 | tr '\n' ' ')
            [ -z "$STRAY" ] || say "note: {next} also in $STRAY- not a record file, left as written"
        fi
    fi
}
if [ "$NEW" = 1 ]; then
    records
    [ "$GONE" -gt 0 ] || say "note: QUEUE.md has no Queued row for $ID"
    RECS=""
    [ "$WANT" = 0 ] || RECS="{next}=$NEXT in $NFILL file(s), "
    [ "$BLOCK" = 0 ] || RECS="$RECS$ID's block on top, "
    RECS="${RECS}$NPEND Pending row(s)"
    [ "$NQUEUED" = 0 ] || RECS="$RECS, $NQUEUED Queued row(s) added"
    [ "$GONE" = 0 ] || RECS="$RECS, its Queued row out"
    say "records: $RECS"
fi

# the version this merge carries: NEXT, or what the tree reads (the old way: the branch's own)
if [ "$WANT" = 1 ]; then V=$NEXT; else V=$(sed -n 's/^ *public static final String VERSION = "\([^"]*\)";.*/\1/p' "$GV" | head -1); fi
[ "$NOTAG" = 0 ] || [ "$V" = "$LAST" ] \
    || die "--no-tag, and the merge reads VERSION ${V:-unreadable}, not the last version tag $LAST: a --no-tag merge carries main's version, or main's VERSION is left untagged"
TAG=""
if [ "$NOTAG" = 1 ]; then :
elif [ "$NEW" = 1 ]; then [ "$WANT" = 0 ] || TAG=$NEXT
else tagged "$V" || TAG=$V
fi
MSG="Merge $BRANCH: $TITLE, $V"

# 9. the checks on the merged tree
DOCSONLY=0
if ! printf '%s\n' "$CHANGED" | grep -v -E '\.md$' | grep -q . && ! printf '%s\n' "$CHANGED" | grep -q '^docs/ensemble/'; then DOCSONLY=1; fi
recorded() {   # main's recorded reds: the non-# lines of origin/main's docs/notes/gate.txt, else main's, else the tree's
    { git show origin/main:docs/notes/gate.txt 2>/dev/null || git show main:docs/notes/gate.txt 2>/dev/null || cat docs/notes/gate.txt 2>/dev/null; } \
        | tr -d '\r' | grep -v '^#' | grep -v '^$'
}
recorded > "$T/recorded"
run_check() {   # the command given, its output on the screen and in $T/check.out, its exit status in CHECKED
    { "$@"; echo $? > "$T/code"; } 2>&1 | tee "$T/check.out"
    CHECKED=$(cat "$T/code")
}
heads() { sed 's/ |.*//' "$1" | sort -u | tr '\n' ' ' | sed 's/ *$//'; }   # the harness of each red line of a file
without() {   # the lines of file $1 that are in none of the files after it, whole lines as written; an empty file takes nothing out
    cp "$1" "$T/without.cur"; shift
    for f in "$@"; do
        [ -s "$f" ] || continue   # grep -f with an empty pattern file: some greps take every line out, some none
        { grep -v -x -F -f "$f" "$T/without.cur" || :; } > "$T/without.next"
        cat "$T/without.next" > "$T/without.cur"
    done
    cat "$T/without.cur"
}
GATE=""; TIMINGLINE=""; SUMMARY=""; TSUMMARY=""; EARTH="named"
if [ "$DOCSONLY" = 1 ]; then
    say "docs only (every path the branch changed is a .md outside docs/ensemble/): batch-check.sh --fast alone, no suite, no timing, gate.txt untouched"
    run_check ./batch-check.sh --fast
    [ "$CHECKED" = 0 ] || die "the checks failed on the merge (above); main is where it was"
    GATE="docs only, --fast"
else
    SOFT="${JDK_JAVA_OPTIONS:+$JDK_JAVA_OPTIONS }-Dcbs.timing=soft"   # the suite times soft: timing is judged once, hard, below
    run_check env JDK_JAVA_OPTIONS="$SOFT" ./batch-check.sh "$@"
    cp target/checks/reds.txt "$T/suite-reds.txt" 2>/dev/null || : > "$T/suite-reds.txt"
    cp target/checks/suite.txt "$T/suite.txt" 2>/dev/null || : > "$T/suite.txt"
    cp "$T/check.out" "$T/suite.out"
    case "$BRANCH" in
        chatgpt/*|gemini/*)   # RV1: what a review used to ask by hand, a name at a time
            if [ -f target/classes/ham/citybuildersim/tools/Names.class ]; then
                java -cp target/classes ham.citybuildersim.tools.Names "$REF" HEAD 2>&1 \
                    | grep -v '^Picked up JAVA_TOOL_OPTIONS' > target/checks/names.txt
                say "names: $(tail -1 target/checks/names.txt)"
                sed '$d' target/checks/names.txt | head -20 | sed 's/^/    /'
                MORE=$(( $(wc -l < target/checks/names.txt) - 21 ))
                [ "$MORE" -le 0 ] || echo "    ... and $MORE more: target/checks/names.txt"
            else
                say "names: not run - no tools.Names in the build"
            fi ;;
    esac
    case "$CHECKED" in
        0) GATE="green" ;;
        3) GATE="known red only: $(heads "$T/suite-reds.txt")" ;;
        4) if [ -n "$REDOK" ]; then GATE="NEW red on Jerus's word: $REDOK"
           else die "NEW red line(s) on the merge, the known reds aside (above); main is where it was. On his word only: --red-ok \"his words\""; fi ;;
        *) die "the checks failed on the merge (above, exit $CHECKED); main is where it was" ;;
    esac
    say "gate: $GATE"
    grep -q 'no earth-2.bin here' "$T/suite.out" && EARTH="not named: the Earth harnesses' full-file sections did not run"
    SUMMARY=$(grep 'harness.* run in ' "$T/suite.txt" | tail -1)

    # TM1: timing held hard, here, once the other batches' JVMs are done
    run_check ./batch-check.sh --timing
    cp target/checks/reds.txt "$T/timing-reds.txt" 2>/dev/null || : > "$T/timing-reds.txt"
    cp target/checks/timing.tsv "$T/hard.tsv" 2>/dev/null || : > "$T/hard.tsv"   # TM3: the hard run's figures
    TSUMMARY=$(grep 'harness.* run in ' target/checks/suite.txt 2>/dev/null | tail -1)
    case "$CHECKED" in
        0|3|4) ;;
        *) die "the timing harnesses failed on the merge (above, exit $CHECKED); main is where it was" ;;
    esac
    # BP1 review: exit 3 is no shortcut (a timing red is never main's recorded one) and exit 4 is not all the batch's: the
    # lines the suite step judged (main's recorded reds, or reds passed with --red-ok) and main's recorded ones come back in
    # this run whatever harness they are in, and are not judged twice; what is left is the timing's own
    without "$T/timing-reds.txt" "$T/suite-reds.txt" "$T/recorded" > "$T/residual"
    : > "$T/accepted"; : > "$T/offenders"; MARGIN=""
    if [ -s "$T/residual" ]; then
        # TM3: a timing red is judged side by side with main, not by one run of the merge's: main's classes and the merge's
        # are timed in turn, in the same minutes, and the red counts against the batch only if the merge's median figure is
        # over main's by more than the margin. tools/timing-pair.awk holds the arithmetic, and the dial (15%).
        PAIRAWK=tools/timing-pair.awk
        [ -f "$PAIRAWK" ] || die "$PAIRAWK is missing from the merge: a timing red cannot be judged against main's"
        MARGIN=${CBS_TIMING_MARGIN:-$(awk -v mode=dial -f "$PAIRAWK" /dev/null)}
        ROUNDS=${CBS_TIMING_ROUNDS:-3}
        case "$ROUNDS" in ''|*[!0-9]*|0|1) die "CBS_TIMING_ROUNDS is $ROUNDS: at least 2 rounds, a median needs two figures" ;; esac
        awk -v mode=harnesses -f "$PAIRAWK" "$T/hard.tsv" "$T/residual" > "$T/pair-harnesses"
        : > target/checks/pairs.tsv
        if [ -s "$T/pair-harnesses" ]; then
            PH=$(tr '\n' ' ' < "$T/pair-harnesses"); PH=${PH% }
            rm -rf "$MT"; git worktree prune
            git worktree add -q --detach "$MT" HEAD >/dev/null 2>&1 || die "cannot make the worktree $MT of main for its paired timing runs"
            say "timing red on the merge ($PH): $ROUNDS rounds with main's classes and the merge's in turn, back to back, in $MT and here..."
            r=1
            while [ "$r" -le "$ROUNDS" ]; do
                RT=$(date +%s); LINE=""
                for side in main batch; do
                    if [ "$side" = main ]; then DIR=$MT; else DIR=.; fi
                    rm -f "$DIR/target/checks/timing.tsv"
                    ( cd "$DIR" && CBS_TIMING_PAIRED=1 ./batch-check.sh --timing $PH ) > "target/checks/pair-$side-$r.out" 2>&1
                    PC=$?
                    case "$PC" in 0|3|4) ;; *) LINE="$LINE $side: did not run (exit $PC, target/checks/pair-$side-$r.out);" ;; esac
                    if [ -s "$DIR/target/checks/timing.tsv" ]; then
                        awk -v s="$side" -v r="$r" 'BEGIN { FS = OFS = "\t" } { print s, r, $0 }' "$DIR/target/checks/timing.tsv" >> target/checks/pairs.tsv
                    else
                        LINE="$LINE $side: no figures written;"
                    fi
                done
                say "  round $r of $ROUNDS done in $(( $(date +%s) - RT )) s${LINE:+ -$LINE}"
                r=$((r + 1))
            done
            cp "$T/timing-reds.txt" target/checks/reds.txt; cp "$T/hard.tsv" target/checks/timing.tsv   # the hard run's, which the orchestrator reads if the ship stops
        fi
        awk -v mode=judge -v margin="$MARGIN" -f "$PAIRAWK" target/checks/pairs.tsv "$T/hard.tsv" "$T/residual" > target/checks/pair-judged.txt
        awk -F'\t' '$1 == "PASS" { print $2 "   [" $3 "]" }' target/checks/pair-judged.txt > "$T/accepted"
        awk -F'\t' '$1 == "FAIL" { print $2 "   [" $3 "]" }' target/checks/pair-judged.txt > "$T/offenders"
        # the notes of the Gate-timing line: each timing red's figures, not the harness's own count
        # (a label that repeats in a harness, WorldCheck's, makes the same note for each of its lines: told once)
        awk -F'\t' '$1 == "PASS" && $3 !~ /^the harness.s own count/ { h = $2; sub(/ \|.*/, "", h); n = h " [" $3 "]"; if (!seen[n]++) { printf "%s%s", sep, n; sep = "; " } }' target/checks/pair-judged.txt > "$T/pass-notes"
        awk -F'\t' '$1 == "FAIL" && $3 !~ /^the harness.s own count/ { h = $2; sub(/ \|.*/, "", h); n = h " [" $3 "]"; if (!seen[n]++) { printf "%s%s", sep, n; sep = "; " } }' target/checks/pair-judged.txt > "$T/fail-notes"
        [ -s "$T/offenders" ] && sed 's/^/    /' "$T/offenders" | cut -c1-260 | head -12
        TIMINGLINE="timing    red on the merge, judged side by side with main (${ROUNDS:-3} rounds in turn, medians, margin $MARGIN%)"
        if [ -s "$T/offenders" ]; then
            if [ -z "$REDOK" ]; then
                die "the timing red(s) above are the batch's (over main's by more than $MARGIN%, or no timing red, or nothing to compare); main is where it was. On his word only: --red-ok \"his words\""
            fi
            TIMINGLINE="$TIMINGLINE, passed on Jerus's word: $REDOK: $(cat "$T/fail-notes")"
            [ ! -s "$T/pass-notes" ] || TIMINGLINE="$TIMINGLINE; within the margin: $(cat "$T/pass-notes")"
        else
            TIMINGLINE="$TIMINGLINE, within it: $(cat "$T/pass-notes")"
        fi
        echo "$TIMINGLINE" | cut -c1-400
    elif [ -s "$T/timing-reds.txt" ]; then
        echo "timing    its red lines are ones the suite step judged (main's recorded reds, or passed with --red-ok): no further run"
    fi

    # 10. the gate's record: the next ship's "main's recorded reds" - the suite's red lines, nothing from the timing run
    #     but "#" lines (a timing red is judged again against main's run at every gate, never taken from the record)
    PART=""; [ "$#" -eq 0 ] || PART=" (partial: $*)"
    {
        echo "# gate of $MSG"
        echo "# date: $(date +%Y-%m-%d)"
        echo "# earth-2.bin: $EARTH"
        echo "# suite: ${SUMMARY:-none}$PART"
        echo "# timing: ${TSUMMARY:-none}${TIMINGLINE:+; its red lines were judged side by side with main's, below}"
        sed "s/^/# timing red within $MARGIN% of main's (paired): /" "$T/accepted"
        if [ -s "$T/offenders" ]; then sed "s/^/# timing red passed on Jerus's word, over main's by more than $MARGIN% or not a timing red ($REDOK): /" "$T/offenders"; fi
        cat "$T/suite-reds.txt"
        [ "$EARTH" = named ] || grep -E '^(Earth|Globe)[A-Za-z0-9]* \|' "$T/recorded"   # lines this gate could not see are kept
    } | awk '/^#/ || (NF && !seen[$0]++)' > docs/notes/gate.txt
    git add -- docs/notes/gate.txt
fi

# 11. a dry run: what would be done, then the merge taken back
if [ "$DRY" = 1 ]; then
    WOULD="would commit \"$MSG\"${TAG:+, tag $TAG} and push"
    [ -n "$TAG" ] || WOULD="would commit \"$MSG\", not tag it and push"
    [ "$NEW" = 0 ] || WOULD="$WOULD; records: $RECS"
    say "dry run, nothing kept: $WOULD"
    undo
    [ "$(git rev-parse HEAD)" = "$HEAD0" ] && [ -z "$(git status --porcelain)" ] && say "dry run: the tree is as it was" \
        || say "dry run: the tree is NOT as it was (above)"
    exit 0
fi

# 12. commit, with the maps the checks regenerated on the merge
git add $GENERATED
{
    echo "$MSG"; echo
    if [ -n "$BODY" ]; then   # the body, trailing blank lines dropped; the Gate lines join a trailer block at its end
        tr -d '\r' < "$BODY" | awk 'NF { while (b) { print ""; b-- } print; next } { b++ }' > "$T/body"
        cat "$T/body"
        tail -n 1 "$T/body" | grep -q -E '^[A-Za-z][A-Za-z0-9-]*: ' || echo
    fi
    echo "Gate: $GATE"
    [ -z "$TIMINGLINE" ] || echo "Gate-timing: ${TIMINGLINE#timing    }"
} > "$T/msg"
git commit -q -F "$T/msg" || die "the commit failed"
MERGING=0
say "committed $(git rev-parse --short HEAD) \"$MSG\""

# 13. tag
if [ -n "$TAG" ]; then
    git tag "$TAG" || die "cannot tag $TAG"
    say "tagged $TAG"
elif [ "$NOTAG" = 1 ]; then
    say "not tagged (--no-tag)"
elif [ "$NEW" = 1 ]; then
    say "not tagged: the ship file has no {next}"
else
    say "not tagged: $V is tagged already (the batch did not bump the version)"
fi

# 14. push, main and the tag together
if ! retry git push --atomic origin main ${TAG:+"refs/tags/$TAG"}; then
    # the usual cause: somebody shipped while the checks ran, and the push is not a fast-forward
    git fetch -q origin main
    if ! git merge-base --is-ancestor origin/main HEAD; then
        git reset -q --keep origin/main
        [ -z "$TAG" ] || git tag -d "$TAG" >/dev/null
        die "main moved on origin while the checks ran ($(git log -1 --format='%h %s' origin/main)); this merge is taken back and main is origin's: run ship.sh again"
    fi
    die "the push of main failed five times; it is committed here, push it by hand"
fi
say "pushed main${TAG:+ and $TAG}"
