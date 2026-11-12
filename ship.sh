#!/bin/sh
# Gate, merge, records, tag and atomic push. Run through tools/gate.sh (logs and stop).
# ./ship.sh [--dry-run] [--reuse RUN_DIRECTORY] [--no-tag] [--version X.Y.Z] [--red-ok WORDS --red-line PART ...]
#           batch/ID [-m TITLE] [-F BODY] [-- harness names (dry runs only)]
# Exit 0 passed/shipped, 1 stopped, 2 usage. docs/orchestrator.md describes the policy.
# WHY (GF1, LM1): a stopped timing check reused the suite's status and published.
# Every check has fresh output and a completion entry, checked before records and commit.
# tools/ship-selftest.sh exercises this against a throwaway origin with a stub suite.

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

USAGE='usage: ship.sh [--dry-run] [--reuse RUN_DIRECTORY] [--no-tag] [--version X.Y.Z] [--red-ok "his words" --red-line "<part of a red line>" ...] <branch> [-m title] [-F body] [-- harness names]'
DRY=0; NOTAG=0; BRANCH=""; TITLE=""; BODY=""; OVERRIDE=""; REDOK=""; REDLINES=""; REUSE=""
NL='
'
while [ $# -gt 0 ]; do
    case "$1" in
        --dry-run) DRY=1 ;;
        --reuse) [ $# -ge 2 ] || { echo "$USAGE"; exit 2; }; shift; REUSE=$1 ;;
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
cleanup() {   # on every way out: an unfinished merge taken back, the scratch folder removed
    [ "$MERGING" = 0 ] || undo
    if [ -n "$CBS_GATE_RUN" ] && [ -d "$T" ]; then cp -R "$T" "$CBS_GATE_RUN/stages"; fi
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
: > "$T/needed"; : > "$T/finished"; : > "$T/used-parts"
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
[ "$#" -eq 0 ] || [ "$DRY" = 1 ] || die "an explicitly filtered partial suite never ships: '-- harness names' only with --dry-run"
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
    BADH=$(grep '^## ' "$T/ship-on-branch" | grep -v -x -E '## (changelog|pending|queued|known-reds)' | head -3 | tr '\n' ' ')
    [ -z "$BADH" ] || die "$SHIPF has a heading ship.sh does not know: $BADH- use changelog, pending, queued or known-reds"
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
if [ -n "$CBS_GATE_RUN" ] && [ -f "$CBS_GATE_RUN/launcher" ]; then
    printf '%s\n' "$HEAD0" > "$CBS_GATE_RUN/main.commit"
    printf '%s\n' "$GATED" > "$CBS_GATE_RUN/branch.commit"
fi
say "commits: branch $GATED, main $HEAD0"
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

# Select the actual merge before publisher-generated VERSION and records can widen it.
PICKED=$(sh tools/pick.sh "$HEAD0" 2> "$T/pick.txt") || die "the pick failed: $(cat "$T/pick.txt")"
cat "$T/pick.txt"

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

# GR2: file new unrelated reds, then remove this batch's own exemptions before judgment.
REDPARTS=docs/notes/known-reds.txt
: > "$T/removed"
if [ "$NEW" = 1 ]; then
    sect known-reds "$T/ship-on-branch" | sed '/^#/d; /^[[:space:]]*$/d' > "$T/new-parts"
    if [ -s "$T/new-parts" ]; then
        awk -F ' [|] ' 'NF != 5 || $1 !~ /^[A-Za-z0-9_]+$/ || $2 == "" || $3 !~ /^[A-Za-z0-9_-]+$/ || $4 !~ /^[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]$/ || $5 == "" { exit 1 }' "$T/new-parts" \
            || die "$SHIPF has a malformed known-reds row"
        cat "$T/new-parts" >> "$REDPARTS"
    fi
    [ -n "$REUSE" ] || REUSE=$(sed -n 's/^<!-- gate-run: \(.*\) -->$/\1/p' "$T/ship-on-branch" | head -1)
fi
if [ -f "$REDPARTS" ]; then
    awk -F ' [|] ' -v id="$ID" '$3 == id { print "! " $0 }' "$REDPARTS" > "$T/removed"
    awk -F ' [|] ' -v id="$ID" '/^#/ || $3 != id' "$REDPARTS" > "$T/parts"
    cat "$T/parts" > "$REDPARTS"
    git add -- "$REDPARTS"
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
        | tr -d '\r' | grep -v '^#' | grep -v '^$'
}
recorded > "$T/recorded"
{ cat "$T/recorded" "$T/removed"; [ ! -f "$REDPARTS" ] || cat "$REDPARTS"; } > "$T/known"
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
    rm -f "$T/code" || die "cannot clear the previous status"
    if [ -z "$TAKEN" ]; then
        rm -f "$DIR/target/checks/reds.txt" "$DIR/target/checks/suite.txt" "$DIR/target/checks/timing.tsv" \
            || die "cannot clear the previous check results for $STAGE"
    fi
    { ( cd "$DIR" && "$@" ); echo "$STAGE $?" > "$T/code"; } 2>&1 | tee "$T/check.out"
    alive
    [ -f "$T/code" ] || die "the check did not finish: $STAGE (no fresh status)"
    read -r FINISHED CHECKED < "$T/code"
    [ "$FINISHED" = "$STAGE" ] || die "the check did not finish: $STAGE (wrong status)"
    case "$STAGE:$CHECKED" in
        *:0|*:3|*:4) if [ "$STAGE" != fast ]; then
                   for f in reds.txt suite.txt; do
                       [ -f "$DIR/target/checks/$f" ] || die "the check did not finish: $STAGE ($f missing)"
                   done
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
shortlines() { awk '{ printf "%s%s", sep, substr($0, 1, 150); sep = "; " }' "$1"; }   # the lines of a file in one, each cut at 150
GAME=$(printf '%s\n' "$CHANGED" | grep '^src/main/java/.*\.java$' | grep -v -E '/tools/|/(LongPlaytest|[^/]*Check)\.java$' | grep .)
FULL=0; [ -z "$GAME" ] && ! printf '%s\n' "$CHANGED" | grep -q -E '^pom\.xml$|^src/main/resources/|^batch-check\.sh$' || FULL=1
[ "$PICKED" != ALL ] || FULL=1
SELECTION="full"; SCOPE=""; TAKEN=""
[ "$#" = 0 ] || SELECTION="manual $*"
if [ "$DOCSONLY" = 1 ]; then SELECTION=docs
elif [ "$FULL" = 0 ]; then
    SELECTION="pick $PICKED"
    SCOPE="no game code: $(printf '%s\n' "$PICKED" | awk '$0 != "NONE" {print NF} $0 == "NONE" {print 0}') harnesses; "
fi
# Changed scripts with a companion self-test run once, as part of the retained evidence.
selftests() {
    [ "${CBS_SELFTEST:-}" != 1 ] || return 0
    printf '%s\n' "$CHANGED" | while IFS= read -r file; do
        case "$file" in
            ship.sh|tools/gate*.sh|tools/gate-process.ps1) echo tools/ship-selftest.sh ;;
            *-selftest.sh) echo "$file" ;;
            *.sh) testfile="${file%.sh}-selftest.sh"; [ ! -f "$testfile" ] || echo "$testfile" ;;
        esac
    done | sort -u > "$T/selftests"
    while IFS= read -r testfile; do
        testlog="target/checks/$(basename "$testfile" .sh).log"
        sh "$testfile" > "$testlog" 2>&1 || { tail -16 "$testlog"; die "$testfile failed"; }
        tail -1 "$testlog"
    done < "$T/selftests"
}
GATE=""; SUMMARY=""; EARTH="named"

if [ "$DOCSONLY" = 1 ]; then
    say "docs only (every path the branch changed is a .md outside docs/ensemble/): batch-check.sh --fast alone, no suite, gate.txt untouched"
    echo fast > "$T/needed"
    run_check fast . ./batch-check.sh --fast
    [ "$CHECKED" = 0 ] || die "the checks failed on the merge (above); main is where it was"
    GATE="docs only, --fast"
else
    if [ -n "$REUSE" ] && sh tools/gate-reuse.sh "$REUSE" "$GATED" "$HEAD0" "$SELECTION"; then
        TAKEN=$REUSE
        CBS_KNOWN_REDS="$T/known" CBS_REJUDGE=rejudge run_check suite . ./batch-check.sh --judge target/checks/suite.txt
    else
        [ -z "$REUSE" ] || say "kept run refused: $REUSE; running checks"
        if [ "$FULL" = 1 ]; then CBS_KNOWN_REDS="$T/known" run_check suite . ./batch-check.sh "$@"
        elif [ "$PICKED" = NONE ]; then run_check fast . ./batch-check.sh --fast
        else CBS_KNOWN_REDS="$T/known" run_check suite . ./batch-check.sh $PICKED; fi
        [ "$FULL" = 1 ] || selftests
    fi
    if [ "$PICKED" = NONE ] && [ "$FULL" = 0 ]; then
        GATE="${SCOPE}--fast"
    else
        if [ -n "$CBS_GATE_RUN" ] && [ -f "$CBS_GATE_RUN/launcher" ] && { [ "$FULL" = 1 ] || [ "$PICKED" != NONE ]; }; then
            cp -R target/checks "$CBS_GATE_RUN/checks"
            printf '%s\n' "$SELECTION" > "$CBS_GATE_RUN/selection"
            (cd "$CBS_GATE_RUN" && sha256sum checks/* > checks.sha256) || die "cannot seal kept checks"
            touch "$CBS_GATE_RUN/checks.complete"
        fi
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
        # Rejudge every kept or fresh log against the merged registry; old tags are evidence of its old verdict.
        CBS_KNOWN_REDS="$T/known" CBS_REJUDGE=rejudge ./batch-check.sh --judge target/checks/suite.txt > "$T/judged" 2>&1
        VERDICT=$?
        cp target/checks/new-reds.txt "$T/suite-new"
        case "$VERDICT" in
            0) GATE="green" ;;
            3) GATE="known red only: $(heads "$T/suite-reds.txt")" ;;
            4) if [ -n "$REDOK" ]; then
                   while IFS= read -r part; do printf '@once %s\n' "$part"; done < "$T/redlines" >> "$T/known"
                   CBS_KNOWN_REDS="$T/known" CBS_REJUDGE=rejudge ./batch-check.sh --judge target/checks/suite.txt > "$T/judged" 2>&1
                   [ "$?" = 3 ] || {
                       show_unnamed target/checks/new-reds.txt
                       die "NEW red line(s) that no --red-line names; main is where it was"
                   }
                   GATE="NEW red on Jerus's word: $REDOK (lines: $(shortlines "$T/suite-new"))"
               else
                   show_unnamed target/checks/new-reds.txt
                   die "NEW red line(s) on the merge; enter unrelated reds with causes and queued fixes, or --red-ok with --red-line on his word"
               fi ;;
            *) cat "$T/judged"; die "the checks failed on the merge (judge exit $VERDICT)" ;;
        esac
        cp target/checks/used-reds.txt "$T/used-parts" || die "cannot retain used approval parts"
        GATE="$SCOPE$GATE${TAKEN:+; reused $TAKEN}"
        say "gate: $GATE"
        grep -q 'no earth-2.bin here' "$T/suite.out" && EARTH="not named: the Earth harnesses' full-file sections did not run"
        SUMMARY=$(grep 'harness.* run in ' "$T/suite.txt" | tail -1)

    fi
    # 10. Only a full gate replaces main's whole-suite record.
    complete
    if [ "$FULL" = 1 ] && [ "$#" = 0 ]; then
        {
            echo "# gate of $MSG"
            echo "# date: $(date +%Y-%m-%d)"
            echo "# earth-2.bin: $EARTH"
            NIGHTLY=$HOME/.cbs-nightly.txt   # tools/nightly.sh's last verdict (GT1)
            [ ! -f "$NIGHTLY" ] || echo "# nightly: $(head -n 1 "$NIGHTLY" | tr -d '\r')"
            echo "# suite: ${SUMMARY:-none}"
            if [ -s "$T/suite-new" ]; then sed "s/^/# suite red passed on Jerus's word ($REDOK): /" "$T/suite-new"; fi
            cat "$T/suite-reds.txt"
            [ "$EARTH" = named ] || grep -E '^(Earth|Globe)[A-Za-z0-9]* \|' "$T/recorded"   # lines this gate could not see are kept
        } | awk '/^#/ || (NF && !seen[$0]++)' > docs/notes/gate.txt
        git add -- docs/notes/gate.txt
    fi
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
    sed 's/^/Known-red: /' "$T/used-parts"
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
