#!/bin/sh
# Launcher evidence only. WHY (GR2): a newly named red used to rerun a complete suite.
# ship.sh saves and restores completed checks with their generated indexes.
MODE=$1; RUN=$2
if [ "$MODE" = save ]; then
    [ -f "$RUN/launcher" ] || exit 0
    mkdir -p "$RUN/checks" "$RUN/generated" || exit 1
    cp -R target/checks/. "$RUN/checks/" &&
    cp -R --parents docs/map docs/dials.md docs/month-order.md docs/harnesses.md "$RUN/generated/" &&
    printf '%s\n' "$3" > "$RUN/selection" &&
    (cd "$RUN" && find checks generated -type f -print0 | sort -z | xargs -0 sha256sum > checks.sha256) &&
    touch "$RUN/checks.complete"
    exit $?
fi
[ "$MODE" = load ] || exit 2
BRANCH_COMMIT=$3; MAIN_COMMIT=$4; SELECTION=$5
[ -f "$RUN/launcher" ] && [ "$(cat "$RUN/launcher")" = cbs-gate-v1 ] || exit 1
[ -f "$RUN/checks.complete" ] && [ ! -f "$RUN/cancelled" ] || exit 1
[ "$(cat "$RUN/branch.commit")" = "$BRANCH_COMMIT" ] || exit 1
[ "$(cat "$RUN/selection")" = "$SELECTION" ] || exit 1
[ "$(cat "$RUN/origin")" = "$(git remote get-url origin)" ] || exit 1
OLD=$(cat "$RUN/main.commit")
git merge-base --is-ancestor "$OLD" "$MAIN_COMMIT" || exit 1
ADVANCED=$(git diff --name-only "$OLD" "$MAIN_COMMIT") || exit 1
printf '%s\n' "$ADVANCED" | awk '
    $0 == "docs/notes/known-reds.txt" { next }
    /\.md$/ && !/^docs\/ensemble\// { next }
    NF { bad = 1 } END { exit bad }' || exit 1
case "$SELECTION" in
    "docs version="*|"pick NONE version="*) ;;
    *) [ -f "$RUN/checks/suite.txt" ] && [ -f "$RUN/checks/reds.txt" ] || exit 1 ;;
esac
[ -d "$RUN/generated/docs/map" ] || exit 1
# Hashes bind the kept checks to the completion marker, including an exit-4 run.
(cd "$RUN" && sha256sum -c checks.sha256 >/dev/null 2>&1) || exit 1
mkdir -p target/checks
cp -R "$RUN/checks/." target/checks/ && cp -R "$RUN/generated/." . || exit 1
exit 0
