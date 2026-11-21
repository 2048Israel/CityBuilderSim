# Stops one ensemble run on Windows: its shell, its xargs, the runs' shells and their JVMs.
# Called by `tools/ensemble/ensemble.sh --stop <label>` with ENS_LABEL (the label) and ENS_B (the run's folder,
# C:/...). A process is the run's when its command line holds that folder as Java or Git Bash (/c/...) writes it,
# or `ensemble.sh <label>`, or, for a run on the gate box, `--ensemble <label>` and `--serve ensemble <label>`
# (GB3); nothing else on the machine, the suite and other runs included, matches. The shells and xargs go
# first, twice, for the one starting while the first pass looked; then a last look names what is left, exit 1.
# WHY (HN1-C): Git Bash hands a Windows program /c/... as C:/..., so no shell or xargs matched, and HN1's xargs
# outlived the stop and started five runs unsaid. tools/ensemble/stop-selftest.sh tests it.
$label = $env:ENS_LABEL
$w = $env:ENS_B -replace '\\', '/'
$bash = if ($w -match '^([A-Za-z]):(/.*)$') { '/' + $matches[1].ToLower() + $matches[2] } else { $w }
$pats = @($w, $bash) | ForEach-Object { [regex]::Escape($_) + '(?=[/\\\s"]|$)' }
$pats += '(ensemble\.sh|--ensemble|--serve ensemble) ' + [regex]::Escape($label) + '(?=[\s"]|$)'
function Find-Run {
    @(Get-CimInstance Win32_Process | Where-Object {
        $c = $_.CommandLine
        $_.ProcessId -ne $PID -and $c -and ($pats | Where-Object { $c -match $_ })
    })
}
$n = 0
foreach ($pass in 1..2) {
    $hit = @(Find-Run)
    $n += $hit.Count
    $hit | Where-Object { $_.Name -ne 'java.exe' } | ForEach-Object { Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue }
    $hit | Where-Object { $_.Name -eq 'java.exe' } | ForEach-Object { Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue }
    Start-Sleep -Seconds 1
}
"stopped $n processes of run $label"
$left = @(Find-Run)
if ($left.Count -eq 0) { "nothing of run $label is left"; exit 0 }
$left | ForEach-Object { "left: $($_.ProcessId) $($_.Name) $($_.CommandLine)" }
"$($left.Count) of run $label outlived the stop: stop it again before a resume"
exit 1
