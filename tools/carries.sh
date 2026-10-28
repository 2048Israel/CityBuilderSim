#!/bin/sh
# ---------------------------------------------------------------------------
#  The unmerged batches a branch is built on: the ids of origin's batch/
#  branches whose tip is in REF's history (default HEAD) and not in
#  origin/main's, one per line, the nearest last. REF's own branch does not
#  count (its tip, or the branch named by REF, by HEAD's branch or by the
#  second argument: its origin ref may lag the commits not yet pushed). Reads
#  the refs as last fetched; prints nothing when there is none; exit 2 for a
#  REF git does not know. POSIX sh and git.
#
#      sh tools/carries.sh [REF [OWN-BRANCH]]
#
#  WHY (RL1, 2026-10-27): a lane's next batch is cut from its predecessor's
#  tip while that one is at the gate, so "my predecessor is not on main yet"
#  needs one test: day.sh, wait.sh, ship.sh, pick.sh and batch-check.sh all
#  had read "a branch" as "cut from main".
# ---------------------------------------------------------------------------
A=${1:-HEAD}
R=$(git rev-parse -q --verify "$A^{commit}") || exit 2
[ "$A" != HEAD ] || A=$(git symbolic-ref -q HEAD)
O=${2:-$A}; O=${O##*batch/}
git for-each-ref --merged="$R" --no-merged=origin/main --sort=committerdate --format='%(objectname) %(refname)' \
    'refs/remotes/origin/batch/*' 2>/dev/null | awk -v r="$R" -v o="$O" '{ sub(/^refs\/remotes\/origin\/batch\//, "", $2) } $1 != r && $2 != o { print $2 }'
exit 0
