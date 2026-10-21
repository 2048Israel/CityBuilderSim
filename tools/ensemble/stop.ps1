# Stops one ensemble run on Windows: its shell, its xargs, the runs' shells and their JVMs.
# Called by `tools/ensemble/ensemble.sh --stop <label>` with ENS_LABEL (the label) and ENS_A, ENS_B
# (the run's folder as Git Bash and as Java write it) in the environment. A process is the run's when
# its command line holds the run's folder (the JVMs, the runs' shells, xargs) or `ensemble.sh <label>`
# (the script's own shell); nothing else on the machine, the suite and other runs included, matches.
# The shells and xargs go first so that no run starts after the JVMs are gone; twice, for the one
# that was starting while the first pass looked.
$label = $env:ENS_LABEL
$pats = @($env:ENS_A, $env:ENS_B) | ForEach-Object { [regex]::Escape($_) + '(?=[/\\\s"]|$)' }
$pats += 'ensemble\.sh ' + [regex]::Escape($label) + '(\s|$)'
$n = 0
foreach ($pass in 1..2) {
    $hit = @(Get-CimInstance Win32_Process | Where-Object {
        $c = $_.CommandLine
        $_.ProcessId -ne $PID -and $c -and ($pats | Where-Object { $c -match $_ })
    })
    $n += $hit.Count
    $hit | Where-Object { $_.Name -ne 'java.exe' } | ForEach-Object { Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue }
    $hit | Where-Object { $_.Name -eq 'java.exe' } | ForEach-Object { Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue }
    Start-Sleep -Seconds 1
}
"stopped $n processes of run $label"
