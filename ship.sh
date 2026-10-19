#!/bin/sh
# ---------------------------------------------------------------------------
#  The orchestrator's gate and ship in one call: a batch's branch merged into
#  main, checked on the merge, tagged with its version and pushed.
#
#      ./ship.sh batch/I12                      gate, merge, tag, push
#      ./ship.sh batch/I12 -m "the title"       the same, the merge's title given
#      ./ship.sh batch/I12 -F body.txt          the same, the message's body (and trailers) from a file
#      ./ship.sh --dry-run batch/I12            the gate alone: nothing committed or pushed
#      ./ship.sh --no-tag some/branch           a merge that is not a version's (tools, records): no tag
#      ./ship.sh batch/I12 -- money bank        the suite only the harnesses named (to try the script)
#
#  Run it in the clone where main is checked out, with nothing uncommitted.
#  In order, stopping at the first that fails:
#    1. main is brought up to origin/main (fast-forward only);
#    2. the branch no longer queues its own batch: QUEUE.md has no row under
#       Queued for the id after "batch/" (CLAUDE.md, the standing rules);
#    3. the merge, uncommitted; a conflict is aborted and its files named;
#    4. batch-check.sh on the merge - compile, suite, maps regenerated on the
#       merged tree, Stale, line endings - and the maps added to the merge;
#       on a chatgpt/ or gemini/ branch, tools.Names too: the names its text
#       uses that the tree does not declare, said and never a gate (a passage
#       about the past may name a method that existed then); then the timing
#       harnesses alone and hard (batch-check.sh --timing), once no other
#       batch's JVM runs (it waits up to 30 minutes, then runs hard all the
#       same): the suite above times soft when one ran beside it
#       (TM1), the gate never;
#    5. the merge committed as "Merge batch/I12: <title>, <version>", the title
#       QUEUE.md's on main unless -m gives one;
#    6. tagged with GameVersion.VERSION unless that tag exists already (a batch
#       that did not bump the version is not tagged);
#    7. main and the tag pushed, a failed push tried four more times; when
#       main moved on origin meanwhile, the merge (and tag) taken back here,
#       to be run again on the new main.
#  A failure before 5 leaves main where it was. It prints a line a step.
#
#  WHY (2026-10-13): the gate was a conversation - a dozen calls, each
#  re-reading the session, to do the same seven things in the same order.
#  The judgement stays the orchestrator's (whether the batch did what its
#  brief asked, the playtest's trace); the mechanics are this.
# ---------------------------------------------------------------------------

cd "$(dirname "$0")" || exit 1

DRY=0; NOTAG=0; BRANCH=""; TITLE=""; BODY=""
while [ $# -gt 0 ]; do
    case "$1" in
        --dry-run) DRY=1 ;;
        --no-tag) NOTAG=1 ;;
        -m) shift; TITLE="$1" ;;
        -F) shift; BODY="$1"; [ -f "$BODY" ] || { echo "ship      no file $BODY"; exit 2; } ;;
        --) shift; break ;;
        *) BRANCH="$1" ;;
    esac
    shift
done
[ -n "$BRANCH" ] || { echo "usage: ship.sh [--dry-run] [--no-tag] batch/<id> [-m title] [-F body] [-- harness names]"; exit 2; }

say() { echo "ship      $*"; }
die() { echo "ship      STOPPED - $*"; exit 1; }
retry() {   # a network call, tried again after 2, 4, 8 and 16 seconds
    for wait in 2 4 8 16 0; do
        "$@" >/dev/null 2>&1 && return 0
        [ "$wait" = 0 ] && return 1
        sleep "$wait"
    done
}

[ "$(git rev-parse --abbrev-ref HEAD)" = main ] || die "main is not checked out here"
[ -z "$(git status --porcelain)" ] || die "the tree has uncommitted changes"

# 1. main up to date
retry git fetch origin main || die "cannot fetch origin main"
git merge --ff-only -q origin/main 2>/dev/null || die "main has commits origin/main does not: push or reset them first"

# the branch: local, or origin's
if git rev-parse -q --verify "refs/heads/$BRANCH" >/dev/null; then
    REF="$BRANCH"
else
    retry git fetch origin "$BRANCH" || die "no branch $BRANCH here or on origin"
    REF="origin/$BRANCH"
fi
AHEAD=$(git rev-list --count "main..$REF")
[ "$AHEAD" -gt 0 ] || die "$BRANCH has nothing main does not"

# 2. the batch's row is out of the queue
ID="${BRANCH#batch/}"
queued() {   # the "what" of the row for $ID under Queued in QUEUE.md as $1 has it
    git show "$1:QUEUE.md" 2>/dev/null | sed '/^## Pending/,$d' \
        | awk -F'|' -v id="$ID" '{ gsub(/^ +| +$/, "", $2) } $2 == id { gsub(/^ +| +$/, "", $3); print $3; exit }'
}
if [ "$ID" != "$BRANCH" ]; then
    [ -z "$(queued "$REF")" ] || die "QUEUE.md on $BRANCH still queues $ID: delete its row in the batch (what it left undone under Pending)"
fi
[ -n "$TITLE" ] || TITLE=$(queued main | sed 's/^\(.\)/\L\1/')
[ -n "$TITLE" ] || TITLE=$(git log -1 --format=%s "$REF")

GENERATED="docs/map docs/dials.md docs/month-order.md docs/harnesses.md"
undo() {   # the merge taken back: the maps the checks rewrote first, or the abort refuses, and the new ones they wrote
    git checkout -q -- $GENERATED 2>/dev/null
    git merge --abort
    git clean -fq -- $GENERATED
    LEFT=$(git status --porcelain | head -5 | tr '\n' ' ')
    [ -z "$LEFT" ] || say "left in the tree after the abort (not tracked by the merge): $LEFT"
}

# 3. the merge, not yet committed
if ! git merge --no-ff --no-commit -q "$REF" >/dev/null 2>&1; then
    CONFLICTS=$(git diff --name-only --diff-filter=U | head -10 | tr '\n' ' ')
    git merge --abort
    die "$BRANCH conflicts with main in: $CONFLICTS"
fi
say "merged $AHEAD commit(s) of $BRANCH, uncommitted"

# 4. the checks on the merged tree
./batch-check.sh "$@"
CHECKED=$?
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
if [ "$CHECKED" = 0 ]; then   # TM1: timing is held hard here, and only here
    ./batch-check.sh --timing
    CHECKED=$?
fi
if [ "$CHECKED" != 0 ] || [ "$DRY" = 1 ]; then
    undo
    [ "$CHECKED" = 0 ] || die "the checks failed on the merge (above); main is where it was"
fi

VERSION=$(sed -n 's/^ *public static final String VERSION = "\([^"]*\)";.*/\1/p' src/main/java/ham/citybuildersim/GameVersion.java)
MSG="Merge $BRANCH: $TITLE, $VERSION"
TAG=""
[ "$NOTAG" = 1 ] || git rev-parse -q --verify "refs/tags/$VERSION" >/dev/null \
    || git ls-remote --exit-code --tags origin "refs/tags/$VERSION" >/dev/null 2>&1 || TAG="$VERSION"

if [ "$DRY" = 1 ]; then
    say "dry run, nothing kept: would commit \"$MSG\"${TAG:+, tag $TAG} and push"
    exit 0
fi

# 5. commit, with the maps the checks regenerated on the merge
git add $GENERATED
if [ -n "$BODY" ]; then
    { echo "$MSG"; echo; cat "$BODY"; } | git commit -q -F - || die "the commit failed"
else
    git commit -q -m "$MSG" || die "the commit failed"
fi
say "committed $(git rev-parse --short HEAD) \"$MSG\""

# 6. tag
if [ -n "$TAG" ]; then
    git tag "$TAG" || die "cannot tag $TAG"
    say "tagged $TAG"
elif [ "$NOTAG" = 1 ]; then
    say "not tagged (--no-tag)"
else
    say "not tagged: $VERSION is tagged already (the batch did not bump the version)"
fi

# 7. push
if ! retry git push origin main; then
    # the usual cause: somebody shipped while the checks ran, and the push is not a fast-forward
    git fetch -q origin main
    if ! git merge-base --is-ancestor origin/main HEAD; then
        git reset -q --keep origin/main
        [ -z "$TAG" ] || git tag -d "$TAG" >/dev/null
        die "main moved on origin while the checks ran ($(git log -1 --format='%h %s' origin/main)); this merge is taken back and main is origin's: run ship.sh again"
    fi
    die "the push of main failed five times; it is committed here, push it by hand"
fi
[ -z "$TAG" ] || retry git push origin "$TAG" || die "main is pushed, the tag $TAG is not: push it by hand"
say "pushed main${TAG:+ and $TAG}"
