#!/bin/sh
# Launcher evidence only. WHY (GR2): a newly named red used to rerun a complete suite.
# Called by ship.sh after its merge and selection, before a fresh check is needed.
RUN=$1; BRANCH_COMMIT=$2; MAIN_COMMIT=$3; SELECTION=$4
[ -f "$RUN/launcher" ] && [ "$(cat "$RUN/launcher")" = cbs-gate-v1 ] || exit 1
[ -f "$RUN/checks.complete" ] && [ ! -f "$RUN/cancelled" ] || exit 1
[ "$(cat "$RUN/branch.commit")" = "$BRANCH_COMMIT" ] || exit 1
[ "$(cat "$RUN/selection")" = "$SELECTION" ] || exit 1
[ "$(cat "$RUN/origin")" = "$(git remote get-url origin)" ] || exit 1
OLD=$(cat "$RUN/main.commit")
git merge-base --is-ancestor "$OLD" "$MAIN_COMMIT" || exit 1
git diff --name-only "$OLD" "$MAIN_COMMIT" | awk '
    $0 == "docs/notes/known-reds.txt" { next }
    /\.md$/ && !/^docs\/ensemble\// { next }
    NF { bad = 1 } END { exit bad }' || exit 1
[ -f "$RUN/checks/suite.txt" ] && [ -f "$RUN/checks/reds.txt" ] || exit 1
# Hashes bind the kept checks to the completion marker, including an exit-4 run.
(cd "$RUN" && sha256sum -c checks.sha256 >/dev/null 2>&1) || exit 1
mkdir -p target/checks
cp -R "$RUN/checks/." target/checks/ || exit 1
exit 0
