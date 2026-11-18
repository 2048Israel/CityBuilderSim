#!/bin/sh
# WHY (DM1): worktree checks must name the same Earth and world files as the gate.
cbs_check_data() {
    cbs_data_main=${1:-$(dirname "$(git rev-parse --path-format=absolute --git-common-dir)")}
    cbs_data_report=
    for cbs_data_pair in earth-2:cbs.earth world-1:cbs.world; do
        case "$(printf ' %s' "${JDK_JAVA_OPTIONS:-}" | tr '\t\r\n' '   ')" in *" -D${cbs_data_pair#*:}="*|*" \"-D${cbs_data_pair#*:}="*|*" '-D${cbs_data_pair#*:}="*) cbs_data_report="$cbs_data_report ${cbs_data_pair#*:} already set;"; continue ;; esac
        cbs_data_file="$(pwd)/data/${cbs_data_pair%%:*}.bin"
        [ -f "$cbs_data_file" ] || cbs_data_file="$cbs_data_main/data/${cbs_data_pair%%:*}.bin"
        [ -f "$cbs_data_file" ] || { cbs_data_report="$cbs_data_report ${cbs_data_pair#*:} not found;"; continue; }
        case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) cbs_data_file=$(cygpath -m "$cbs_data_file") ;; esac
        JDK_JAVA_OPTIONS="${JDK_JAVA_OPTIONS:+$JDK_JAVA_OPTIONS }-D${cbs_data_pair#*:}=\"$cbs_data_file\""
        cbs_data_report="$cbs_data_report ${cbs_data_pair#*:}=\"$cbs_data_file\" named;"
    done
    export JDK_JAVA_OPTIONS
    echo "data     $cbs_data_report"
}
