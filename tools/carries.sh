#!/bin/sh
# ---------------------------------------------------------------------------
#  Which unmerged batch a branch is built on: the ids of origin's batch/
#  branches whose tip is in REF's history (default HEAD) and not in
#  origin/main's, one per line, the nearest last. A lane's next batch is cut
#  from its predecessor's tip while that one is at the gate (RL1), so this is
#  the one test of "my predecessor is not on main yet", for day.sh, wait.sh,
#  ship.sh, pick.sh and batch-check.sh. REF's own branch does not count (a
#  tip equal to REF's, or the branch named by REF, by HEAD's branch, or by the
#  optional second argument: its origin ref may lag the commits not yet pushed).
#  Reads the refs as last fetched; prints nothing, exit 0, when there is none;
#  exit 2 for a REF git does not know. POSIX sh and git.
#
#      sh tools/carries.sh [REF [OWN-BRANCH]]
#
#  WHY (RL1, 2026-10-27): every minute from a predecessor's ship file to its
#  merge was paid again by the batch behind it, which waited for the row to
#  leave the queue. Starting at the ship file puts the successor on a base
#  main does not have yet, and four scripts had read "a branch" as "cut from
#  main".
# ---------------------------------------------------------------------------
A=${1:-HEAD}
R=$(git rev-parse -q --verify "$A^{commit}") || exit 2
[ "$A" != HEAD ] || A=$(git symbolic-ref -q HEAD)
O=${2:-$A}; O=${O##*batch/}
git for-each-ref --merged="$R" --no-merged=origin/main --sort=committerdate --format='%(objectname) %(refname)' \
    'refs/remotes/origin/batch/*' 2>/dev/null | awk -v r="$R" -v o="$O" '{ sub(/^refs\/remotes\/origin\/batch\//, "", $2) } $1 != r && $2 != o { print $2 }'
exit 0
