#!/bin/sh
# WHY (DM1): worktree checks must name the same Earth and world files as the gate.
cbs_check_data() {
    if [ -n "${JDK_JAVA_OPTIONS:-}" ]; then echo 'data      none named (JDK_JAVA_OPTIONS already set)'; return 0; fi
    cbs_data_main=${1:-$(dirname "$(git rev-parse --path-format=absolute --git-common-dir)")}
    for cbs_data_pair in earth-2:cbs.earth world-1:cbs.world; do
        cbs_data_file="$(pwd)/data/${cbs_data_pair%%:*}.bin"
        [ -f "$cbs_data_file" ] || cbs_data_file="$cbs_data_main/data/${cbs_data_pair%%:*}.bin"
        [ -f "$cbs_data_file" ] || continue
        case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) cbs_data_file=$(cygpath -m "$cbs_data_file") ;; esac
        JDK_JAVA_OPTIONS="${JDK_JAVA_OPTIONS:+$JDK_JAVA_OPTIONS }-D${cbs_data_pair#*:}=\"$cbs_data_file\""
    done
    export JDK_JAVA_OPTIONS
    echo "data      ${JDK_JAVA_OPTIONS:-none found}"
}
