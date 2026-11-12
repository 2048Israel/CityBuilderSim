#!/bin/sh
# Gate, merge, records, tag and atomic push. Run through tools/gate.sh (logs and stop).
# ./ship.sh [--dry-run] [--no-tag] [--version X.Y.Z] [--red-ok WORDS --red-line PART ...]
#           batch/ID [-m TITLE] [-F BODY] [-- harness names (dry runs only)]
# Exit 0 passed/shipped, 1 stopped, 2 usage. docs/orchestrator.md describes the policy.
# WHY (GF1, LM1): a stopped timing check reused the suite's status and published.
# Every check has fresh output and a completion entry, checked before records and commit.
# tools/ship-selftest.sh exercises this against a throwaway origin without a JVM.

# TM3: run from a copy of this script, made before anything is merged. A shell reads its script as it goes, at the
# offsets of the file it opened, and a batch that changes ship.sh rewrites that file in the merge. CBS_SHIP_SHELL is the
# shell that runs the copy (the self-test runs it under dash); CBS_SHIP_COPY, once set, says this is the copy.
if [ -z "$CBS_SHIP_COPY" ]; then
    CBS_SHIP_HOME=${CBS_SHIP_HOME:-$(cd "$(dirname "$0")" && pwd)}
    CBS_SHIP_COPY=$(mktemp "${TMPDIR:-/tmp}/ship-run.XXXXXX") || { echo "ship      cannot make a copy of itself in ${TMPDIR:-/tmp}"; exit 1; }
    cp "$0" "$CBS_SHIP_COPY" || { rm -f "$CBS_SHIP_COPY"; echo "ship      cannot copy $0 to $CBS_SHIP_COPY"; exit 1; }
    export CBS_SHIP_HOME CBS_SHIP_COPY
    CBS_SHIP_OWNER=$$; export CBS_SHIP_OWNER
    trap 'rm -f "$CBS_SHIP_COPY" "$CBS_SHIP_COPY.status"' EXIT
    trap 'exit 130' INT TERM HUP
    { ${CBS_SHIP_SHELL:-sh} "$CBS_SHIP_COPY" "$@"; echo $? > "$CBS_SHIP_COPY.status"; } 2>&1 |
        awk '{ print strftime("[%Y-%m-%d %H:%M:%S]"), $0; fflush() }'
    [ -f "$CBS_SHIP_COPY.status" ] || { echo "$(date '+[%Y-%m-%d %H:%M:%S]') ship      the check did not finish"; exit 1; }
    exit "$(cat "$CBS_SHIP_COPY.status")"
fi
cd "${CBS_SHIP_HOME:-$(dirname "$0")}" || exit 1

USAGE='usage: ship.sh [--dry-run] [--no-tag] [--version X.Y.Z] [--red-ok "his words" --red-line "<part of a red line>" ...] <branch> [-m title] [-F body] [-- harness names]'
DRY=0; NOTAG=0; BRANCH=""; TITLE=""; BODY=""; OVERRIDE=""; REDOK=""; REDLINES=""
NL='
'
while [ $# -gt 0 ]; do
    case "$1" in
        --dry-run) DRY=1 ;;
        --no-tag) NOTAG=1 ;;
        --version) [ $# -ge 2 ] || { echo "$USAGE"; exit 2; }; shift; OVERRIDE="$1" ;;
        --red-ok) [ $# -ge 2 ] && [ -n "$2" ] || { echo "$USAGE"; exit 2; }; shift; REDOK="$1" ;;
        --red-line) [ $# -ge 2 ] && [ -n "$2" ] || { echo "$USAGE"; exit 2; }; shift; REDLINES="${REDLINES:+$REDLINES$NL}$1" ;;
        -m) [ $# -ge 2 ] || { echo "$USAGE"; exit 2; }; shift; TITLE="$1" ;;
        -F) [ $# -ge 2 ] || { echo "$USAGE"; exit 2; }; shift; BODY="$1"; [ -f "$BODY" ] || { echo "ship      no file $BODY"; exit 2; } ;;
        --) shift; break ;;
        -*) echo "ship      unknown option $1"; echo "$USAGE"; exit 2 ;;
        *) [ -z "$BRANCH" ] || { echo "ship      two branches given: $BRANCH and $1"; echo "$USAGE"; exit 2; }; BRANCH="$1" ;;
    esac
    shift
done
[ -n "$BRANCH" ] || { echo "$USAGE"; exit 2; }
# TM3 review: his words pass only the lines they name (the lines are known before his word: he is asked after a stop, with that stop's lines in hand)
[ -z "$REDOK" ] || [ -n "$REDLINES" ] || { echo "ship      --red-ok needs at least one --red-line \"<part of a red line>\": his words pass only the lines named"; echo "$USAGE"; exit 2; }
[ -z "$REDLINES" ] || [ -n "$REDOK" ] || { echo "ship      --red-line is for --red-ok: his words, then the lines they pass"; echo "$USAGE"; exit 2; }

GV=src/main/java/ham/citybuildersim/GameVersion.java
GENERATED="docs/map docs/dials.md docs/month-order.md docs/harnesses.md"
GENRE='^docs/(map/|dials\.md$|month-order\.md$|harnesses\.md$)'
T=target/ship-tmp          # this script's scratch folder, gone when it ends
MT=target/main-timing      # the worktree of main whose classes the paired timing rounds run (TM3)
MERGING=0; COMMITTED=0

say() { echo "ship      $*"; }
die() { [ "$MERGING" = 0 ] || undo; echo "ship      STOPPED - $*"; exit 1; }
rollback() {
    [ "$COMMITTED" = 1 ] || return 0
    [ -z "$TAG" ] || git tag -d "$TAG" >/dev/null 2>&1
    git reset -q --hard "$HEAD0"
    COMMITTED=0
}
alive() {
    if ! kill -0 "$CBS_SHIP_OWNER" 2>/dev/null || { [ -n "$CBS_GATE_RUN" ] && [ -e "$CBS_GATE_RUN/cancelled" ]; }; then
        rollback; die "the gate was stopped; publication cancelled"
    fi
    if [ "$DRY" = 0 ] || [ -n "$CBS_GATE_RUN" ]; then
        [ -n "$CBS_GATE_RUN" ] && [ -f "$CBS_GATE_RUN/guardian.ready" ] &&
            [ -f "$CBS_GATE_RUN/guardian" ] && kill -0 "$(cat "$CBS_GATE_RUN/guardian")" 2>/dev/null && return 0
        rollback; die "publishing requires tools/gate.sh with a running guardian"
    fi
}
retry() {   # a network call, tried again after 2, 4, 8 and 16 seconds
    for wait in 2 4 8 16 0; do
        if [ "$1" = git ] && [ "$2" = push ]; then alive; fi
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
    if [ -n "$CBS_GATE_RUN" ] && [ -d "$T" ]; then cp -R "$T" "$CBS_GATE_RUN/stages"; fi
    if [ -d "$MT" ]; then
        git worktree remove --force "$MT" >/dev/null 2>&1
        rm -rf "$MT"
        git worktree prune
    fi
    rm -rf "$T"
    [ -z "$CBS_SHIP_HOME" ] || rm -f "$CBS_SHIP_COPY" 2>/dev/null   # the copy this run is (TM3); a shell on Windows may still hold it open: left, it is a few kilobytes in the temp folder
}
trap cleanup EXIT
trap 'rollback; exit 130' INT TERM HUP

alive
[ "$(git rev-parse --abbrev-ref HEAD)" = main ] || die "main is not checked out here"
[ -z "$(git status --porcelain)" ] || die "the tree has uncommitted changes"
rm -rf "$T"; mkdir -p "$T"
T=$(cd "$T" && pwd)
: > "$T/needed"; : > "$T/finished"
mkdir -p "${CBS_GATE_RUN:-target/checks}" || die "cannot create the gate output directory"
: > "${CBS_GATE_RUN:-target/checks}/unnamed.txt" || die "cannot clear the previous unnamed reds"
printf '%s\n' "$REDLINES" | grep . > "$T/redlines" || : > "$T/redlines"   # the parts of red lines his words pass, one a line

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
GATED=$(git rev-parse "$REF^{commit}")
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
elif [ "$NOTAG" = 1 ]; then
    say "no $SHIPF - its records as it wrote them"
else
    die "$BRANCH has no $SHIPF: its records go there (docs/batch.md), or ship it --no-tag (a branch with no version of its own)"
fi

# 5. stops, before any merge
[ "$#" -eq 0 ] || [ "$DRY" = 1 ] || die "a partial suite never ships: '-- harness names' only with --dry-run"
printf '%s\n' "$CHANGED" | grep -q -x 'docs/notes/gate.txt' && die "$BRANCH changes docs/notes/gate.txt: only ship.sh writes the gate's record"
CARRIED=$(sh tools/carries.sh "$REF" "$BRANCH" | tr '\n' ' ')   # RL1: a successor cut from its predecessor's tip, which main lacks; its ship file is the predecessor's too
[ -z "$CARRIED" ] || die "$BRANCH is built on ${CARRIED% }, which is not on main yet: gate that first, then $BRANCH merges main (docs/batch.md, A batch session)"
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
else   # no ship file, --no-tag (step 4): the merge carries main's version, and one that moves it leaves main untagged and stops every later ship (step 3)
    [ "$VCHG" = 0 ] || die "$BRANCH changes GameVersion's VERSION line and --no-tag was given: main's VERSION would stay untagged and stop every later ship; leave VERSION as main has it"
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

# 7. the merge, not yet committed
if printf '%s
' "$CHANGED" | grep -q -x 'ship.sh'; then
    say "$BRANCH changes ship.sh: this run goes on from its copy ($0), so the merge cannot rewrite it under the shell"
fi
HEAD0=$(git rev-parse HEAD)
[ -z "$CBS_GATE_RUN" ] || echo "$HEAD0" > "$CBS_GATE_RUN/head.before"
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

# the version this merge carries: NEXT, or what the tree reads
if [ "$WANT" = 1 ]; then V=$NEXT; else V=$(sed -n 's/^ *public static final String VERSION = "\([^"]*\)";.*/\1/p' "$GV" | head -1); fi
[ "$NOTAG" = 0 ] || [ "$V" = "$LAST" ] \
    || die "--no-tag, and the merge reads VERSION ${V:-unreadable}, not the last version tag $LAST: a --no-tag merge carries main's version, or main's VERSION is left untagged"
TAG=""; [ "$NOTAG" = 1 ] || [ "$WANT" = 0 ] || TAG=$NEXT
MSG="Merge $BRANCH: $TITLE, $V"

# 9. the checks on the merged tree
DOCSONLY=0
if ! printf '%s\n' "$CHANGED" | grep -v -E '\.md$' | grep -q . && ! printf '%s\n' "$CHANGED" | grep -q '^docs/ensemble/'; then DOCSONLY=1; fi
recorded() {   # main's recorded reds: the non-# lines of origin/main's docs/notes/gate.txt, else main's, else the tree's
    { git show origin/main:docs/notes/gate.txt 2>/dev/null || git show main:docs/notes/gate.txt 2>/dev/null || cat docs/notes/gate.txt 2>/dev/null; } \
        | tr -d '\r' | tee "$T/gate-main.txt" | grep -v '^#' | grep -v '^$'   # the whole of it kept: its "# figure" lines are main's of the day (GT2)
}
recorded > "$T/recorded"
complete() {
    alive
    MISSING=$(grep -v -x -F -f "$T/finished" "$T/needed" | tr '\n' ' ')
    [ -z "$MISSING" ] || die "the check did not finish: $MISSING"
}
run_check() {   # stage, directory, command: no status or output survives from an earlier stage
    STAGE=$1; DIR=$2; shift 2
    alive
    STAGE_STARTED=$(date +%s)
    say "check $STAGE started"
    echo "$STAGE" >> "$T/needed"
    rm -f "$T/code" "$DIR/target/checks/reds.txt" "$DIR/target/checks/suite.txt" "$DIR/target/checks/timing.tsv" \
        || die "cannot clear the previous check results for $STAGE"
    { ( cd "$DIR" && "$@" ); echo "$STAGE $?" > "$T/code"; } 2>&1 | tee "$T/check.out"
    alive
    [ -f "$T/code" ] || die "the check did not finish: $STAGE (no fresh status)"
    read -r FINISHED CHECKED < "$T/code"
    [ "$FINISHED" = "$STAGE" ] || die "the check did not finish: $STAGE (wrong status)"
    case "$STAGE:$CHECKED" in
        pair-*:*) [ "$CHECKED" -lt 128 ] || die "the check did not finish: $STAGE (exit $CHECKED)" ;; # TM3 judges completed rounds with no figures
        *:0|*:3|*:4) if [ "$STAGE" != fast ]; then
                   for f in reds.txt suite.txt; do
                       [ -f "$DIR/target/checks/$f" ] || die "the check did not finish: $STAGE ($f missing)"
                   done
                   if [ "$STAGE" != suite ]; then
                       [ -f "$DIR/target/checks/timing.tsv" ] || die "the check did not finish: $STAGE (timing.tsv missing)"
                   fi
               fi ;;
        *) die "the check did not finish successfully: $STAGE (exit $CHECKED)" ;;
    esac
    echo "$STAGE" >> "$T/finished"
    say "check $STAGE finished in $(( $(date +%s) - STAGE_STARTED )) s (exit $CHECKED)"
}
show_unnamed() {   # WG2: keep every line even when the screen shows twelve
    cat "$1" >> "${CBS_GATE_RUN:-target/checks}/unnamed.txt"
    say "$(wc -l < "$1" | tr -d ' ') unnamed red line(s), all in ${CBS_GATE_RUN:-target/checks}/unnamed.txt"
    sed 's/^/    NOT NAMED: /' "$1" | cut -c1-260 | head -12
}
heads() { sed 's/ |.*//' "$1" | sort -u | tr '\n' ' ' | sed 's/ *$//'; }   # the harness of each red line of a file
# unnamed LINES CONTEXT: the red lines ("Harness | text") of file LINES that no --red-line passes, in order (TM3 review). A line
# passes when it contains one of the --red-line parts, as written. A harness's own count ("H | exit N", "H | N FAILED") passes
# too when H has a line that passes - in LINES, or among the lines of file CONTEXT, which are passed already - and no other
# line of H that does not: his words pass the lines he named and what only follows from them, never another red.
unnamed() {
    awk -v pf="$T/redlines" '
    BEGIN { while ((getline l < pf) > 0) if (l != "") p[++np] = l }
    function agg(s) { return s ~ /^[^ ]+ \| (exit -?[0-9]+|[0-9]+ FAILED)$/ }
    function head(s) { return substr(s, 1, index(s, " | ") - 1) }
    function named(s,   i) { for (i = 1; i <= np; i++) if (index(s, p[i])) return 1; return 0 }
    FILENAME == ARGV[1] { if (!agg($0)) okc[head($0)]++; next }
    { n++; line[n] = $0
      if (named($0)) { pass[n] = 1; if (!agg($0)) okc[head($0)]++ }
      else if (!agg($0)) bad[head($0)] = 1 }
    END { for (i = 1; i <= n; i++) {
              if (pass[i]) continue
              if (agg(line[i]) && okc[head(line[i])] > 0 && !(head(line[i]) in bad)) continue
              print line[i] } }' "$2" "$1"
}
shortlines() { awk '{ printf "%s%s", sep, substr($0, 1, 150); sep = "; " }' "$1"; }   # the lines of a file in one, each cut at 150
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
    echo fast > "$T/needed"
    run_check fast . ./batch-check.sh --fast
    [ "$CHECKED" = 0 ] || die "the checks failed on the merge (above); main is where it was"
    GATE="docs only, --fast"
else
    printf '%s\n' suite timing > "$T/needed"
    run_check suite . ./batch-check.sh "$@"   # the suite times soft (batch-check.sh does it, BP1): timing is judged once, hard, below
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
        4) if [ -n "$REDOK" ]; then
               without "$T/suite-reds.txt" "$T/recorded" > "$T/suite-new"   # the lines batch-check.sh judged NEW, as ship.sh reads the record
               [ -s "$T/suite-new" ] || die "the suite exited 4 (a NEW red line), but main's record has every red line it printed: the two records disagree; main is where it was"
               unnamed "$T/suite-new" /dev/null > "$T/suite-unnamed"
               if [ -s "$T/suite-unnamed" ]; then
                   show_unnamed "$T/suite-unnamed"
                   die "NEW red line(s) that no --red-line names (above); main is where it was. His words pass the lines named, never more: --red-line \"<part of the line>\""
               fi
               GATE="NEW red on Jerus's word: $REDOK (lines: $(shortlines "$T/suite-new"))"
           else
               without "$T/suite-reds.txt" "$T/recorded" > "$T/suite-unnamed"
               show_unnamed "$T/suite-unnamed"
               die "NEW red line(s) on the merge, the known reds aside (above); main is where it was. On his word only: --red-ok \"his words\" --red-line \"<part of each line he passes>\""; fi ;;
        *) die "the checks failed on the merge (above, exit $CHECKED); main is where it was" ;;
    esac
    say "gate: $GATE"
    grep -q 'no earth-2.bin here' "$T/suite.out" && EARTH="not named: the Earth harnesses' full-file sections did not run"
    SUMMARY=$(grep 'harness.* run in ' "$T/suite.txt" | tail -1)

    # TM1: timing held hard, here, once the other batches' JVMs are done
    run_check timing . ./batch-check.sh --timing
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
    : > "$T/accepted"; : > "$T/offenders"; : > "$T/figures"; MARGIN=""; PH=""; TODAY=$(date +%Y-%m-%d)
    if [ -s "$T/residual" ]; then
        # TM3: a timing red is judged side by side with main, not by one run of the merge's: main's classes and the merge's
        # are timed in turn, in the same minutes, and the red counts against the batch only if the merge's median figure is
        # over main's by more than the margin. tools/timing-pair.awk holds the arithmetic, and the dial (15%).
        PAIRAWK=tools/timing-pair.awk
        [ -f "$PAIRAWK" ] || die "$PAIRAWK is missing from the merge: a timing red cannot be judged against main's"
        MARGIN=${CBS_TIMING_MARGIN:-$(awk -v mode=dial -f "$PAIRAWK" /dev/null)}
        ROUNDS=${CBS_TIMING_ROUNDS:-3}
        case "$ROUNDS" in ''|*[!0-9]*|0|1) die "CBS_TIMING_ROUNDS is $ROUNDS: at least 2 rounds, a median needs two figures" ;; esac
        awk -v mode=harnesses -v margin="$MARGIN" -v rec="$T/gate-main.txt" -v today="$TODAY" -f "$PAIRAWK" "$T/hard.tsv" "$T/residual" > "$T/pair-harnesses"   # GT2: none whose every miss main's figure of the day covers
        : > target/checks/pairs.tsv
        if [ -s "$T/pair-harnesses" ]; then
            PH=$(tr '\n' ' ' < "$T/pair-harnesses"); PH=${PH% }
            rm -rf "$MT"; git worktree prune
            git worktree add -q --detach "$MT" HEAD >/dev/null 2>&1 || die "cannot make the worktree $MT of main for its paired timing runs"
            say "timing red on the merge ($PH): $ROUNDS rounds with main's classes and the merge's in turn, back to back, each round's first side the last one's second, in $MT and here..."
            r=1
            while [ "$r" -le "$ROUNDS" ]; do
                RT=$(date +%s); LINE=""
                ORDER="main batch"; [ $((r % 2)) = 1 ] || ORDER="batch main"   # SP1 item 7: a run straight after a switch of build was the slower (KeptCheck 6.2-6.9 ms against 5.2-5.5), so neither side always goes first
                for side in $ORDER; do
                    if [ "$side" = main ]; then DIR=$MT; else DIR=.; fi
                    run_check "pair-$side-$r" "$DIR" ./batch-check.sh --timing $PH > "target/checks/pair-$side-$r.out" 2>&1
                    PC=$CHECKED
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
        awk -v mode=judge -v margin="$MARGIN" -v rec="$T/gate-main.txt" -v today="$TODAY" -v recout="$T/figures" -f "$PAIRAWK" target/checks/pairs.tsv "$T/hard.tsv" "$T/residual" > target/checks/pair-judged.txt
        awk -F'\t' '$1 == "PASS" { print $2 "   [" $3 "]" }' target/checks/pair-judged.txt > "$T/accepted"
        awk -F'\t' '$1 == "FAIL" { print $2 "   [" $3 "]" }' target/checks/pair-judged.txt > "$T/offenders"
        # the notes of the Gate-timing line: each timing red's figures, not the harness's own count
        # (a label that repeats in a harness, WorldCheck's, makes the same note for each of its lines: told once)
        awk -F'\t' '$1 == "PASS" && $3 !~ /^the harness.s own count/ { h = $2; sub(/ \|.*/, "", h); n = h " [" $3 "]"; if (!seen[n]++) { printf "%s%s", sep, n; sep = "; " } }' target/checks/pair-judged.txt > "$T/pass-notes"
        awk -F'\t' '$1 == "FAIL" && $3 !~ /^the harness.s own count/ { h = $2; sub(/ \|.*/, "", h); n = h " [" $3 "]"; if (!seen[n]++) { printf "%s%s", sep, n; sep = "; " } }' target/checks/pair-judged.txt > "$T/fail-notes"
        [ -s "$T/offenders" ] && sed 's/^/    /' "$T/offenders" | cut -c1-260 | head -12
        HOW="${ROUNDS:-3} rounds of $PH in turn, medians"; [ -n "$PH" ] || HOW="no round: main's figure of the day"
        TIMINGLINE="timing    red on the merge, judged side by side with main ($HOW, margin $MARGIN%)"
        if [ -s "$T/offenders" ]; then
            if [ -z "$REDOK" ]; then
                awk -F'\t' '$1 == "FAIL" { print $2 }' target/checks/pair-judged.txt > "$T/off-left"
                show_unnamed "$T/off-left"
                die "the timing red(s) above are the batch's (over the budget with main within it, and over main's by more than $MARGIN%; or no timing red; or nothing to compare); main is where it was. On his word only: --red-ok \"his words\" --red-line \"<part of each line he passes>\""
            fi
            awk -F'\t' '$1 == "FAIL" { print $2 }' target/checks/pair-judged.txt > "$T/off-raw"
            awk -F'\t' '$1 == "PASS" { print $2 }' target/checks/pair-judged.txt > "$T/acc-raw"
            unnamed "$T/off-raw" "$T/acc-raw" > "$T/off-left"   # (TM3 review) his words pass the lines they name, never another
            if [ -s "$T/off-left" ]; then
                show_unnamed "$T/off-left"
                die "timing red(s) that no --red-line names (above); main is where it was. His words pass the lines named, never more: --red-line \"<part of the line>\""
            fi
            TIMINGLINE="$TIMINGLINE, passed on Jerus's word: $REDOK: $(cat "$T/fail-notes")"
            [ ! -s "$T/pass-notes" ] || TIMINGLINE="$TIMINGLINE; not the batch's: $(cat "$T/pass-notes")"
        else
            TIMINGLINE="$TIMINGLINE, not the batch's: $(cat "$T/pass-notes")"
        fi
        echo "$TIMINGLINE" | cut -c1-400
    elif [ -s "$T/timing-reds.txt" ]; then
        echo "timing    its red lines are ones the suite step judged (main's recorded reds, or passed with --red-ok): no further run"
    fi

    # 10. the gate's record: the next ship's "main's recorded reds" - the suite's red lines, nothing from the timing run
    #     but "#" lines (a timing red is judged again at every gate, never a recorded red; its figure is a "# figure" line, GT2)
    complete
    PART=""; [ "$#" -eq 0 ] || PART=" (partial: $*)"
    {
        echo "# gate of $MSG"
        echo "# date: $(date +%Y-%m-%d)"
        echo "# earth-2.bin: $EARTH"
        NIGHTLY=$HOME/.cbs-nightly.txt   # tools/nightly.sh's last verdict (GT1)
        [ ! -f "$NIGHTLY" ] || echo "# nightly: $(head -n 1 "$NIGHTLY" | tr -d '\r')"
        echo "# suite: ${SUMMARY:-none}$PART"
        echo "# timing: ${TSUMMARY:-none}${TIMINGLINE:+; its red lines were judged side by side with main's, below}"
        sed "s/^/# timing red not the batch's (paired): /" "$T/accepted"
        cat "$T/figures"   # GT2: the merged main's figure of each miss, which the next gate today judges its reds against first
        if [ -s "$T/offenders" ]; then sed "s/^/# timing red passed on Jerus's word, over main's by more than $MARGIN% or not a timing red ($REDOK): /" "$T/offenders"; fi
        if [ -s "$T/suite-new" ]; then sed "s/^/# suite red passed on Jerus's word ($REDOK): /" "$T/suite-new"; fi
        cat "$T/suite-reds.txt"
        [ "$EARTH" = named ] || grep -E '^(Earth|Globe)[A-Za-z0-9]* \|' "$T/recorded"   # lines this gate could not see are kept
    } | awk '/^#/ || (NF && !seen[$0]++)' > docs/notes/gate.txt
    git add -- docs/notes/gate.txt
fi

# GF1, LM1: publish only the tip checked, while its readiness marker still exists.
complete
REMOTE=$(git ls-remote --exit-code origin "refs/heads/$BRANCH") || die "cannot read origin's $BRANCH before commit"
REMOTE=${REMOTE%%[[:space:]]*}
if [ "$REMOTE" != "$GATED" ]; then
    retry git fetch -q origin "$BRANCH" || die "cannot inspect changed $BRANCH"
    if [ "$NEW" = 1 ] && ! git cat-file -e "FETCH_HEAD:$SHIPF" 2>/dev/null; then
        die "$BRANCH withdrew $SHIPF during the gate ($GATED -> $REMOTE)"
    fi
    die "$BRANCH changed during the gate ($GATED -> $REMOTE)"
fi
if [ "$NEW" = 1 ]; then
    git cat-file -e "$REMOTE:$SHIPF" 2>/dev/null || die "$BRANCH withdrew $SHIPF during the gate"
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
complete
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
[ -z "$CBS_GATE_RUN" ] || printf '%s\n' "$TAG" > "$CBS_GATE_RUN/tag"
git commit -q -F "$T/msg" || die "the commit failed"
COMMITTED=1; MERGING=0
say "committed $(git rev-parse --short HEAD) \"$MSG\""

# 13. tag
alive
if [ -n "$TAG" ]; then
    git tag "$TAG" || die "cannot tag $TAG"
    say "tagged $TAG"
elif [ "$NOTAG" = 1 ]; then
    say "not tagged (--no-tag)"
else
    say "not tagged: the ship file has no {next}"
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
COMMITTED=0
say "pushed main${TAG:+ and $TAG}"
