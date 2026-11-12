# Own-process-tree cancellation on Windows. WHY (GF1, LM1): closing the caller left sh.exe ./ship.sh publishing.
param([ValidateSet('Watch', 'Stop')][string]$Mode, [string]$Run)
$ErrorActionPreference = 'Stop'
$identityFile = Join-Path $Run 'process'
function Stop-Gate {
    if (!(Test-Path -LiteralPath $identityFile)) { return }
    $identity = (Get-Content -LiteralPath $identityFile) -split '\|', 2
    $gateProcess = Get-Process -Id ([int]$identity[0]) -ErrorAction SilentlyContinue
    if ($gateProcess -and $gateProcess.StartTime.ToUniversalTime().Ticks -eq [long]$identity[1]) {
        $command = (Get-CimInstance Win32_Process -Filter "ProcessId = $($gateProcess.Id)").CommandLine
        if ($command -notmatch 'sh(\.exe)?[" ]+.*ship\.sh') { throw "Gate process identity changed: $command" }
        & taskkill.exe /PID $gateProcess.Id /T /F | Out-Null
    }
}
if ($Mode -eq 'Stop') {
    New-Item -ItemType File -Path (Join-Path $Run 'cancelled') -Force | Out-Null
    # A stop just after launch waits for the watcher to record the native identity.
    for ($attempt = 0; $attempt -lt 100 -and !(Test-Path -LiteralPath $identityFile); $attempt++) { Start-Sleep -Milliseconds 100 }
    if (!(Test-Path -LiteralPath $identityFile)) { throw 'Gate identity was never recorded' }
    Stop-Gate
    exit 0
}
try {
    for ($attempt = 0; $attempt -lt 100 -and !(Test-Path -LiteralPath (Join-Path $Run 'ship.native')); $attempt++) { Start-Sleep -Milliseconds 100 }
    $shipId = [int](Get-Content -LiteralPath (Join-Path $Run 'ship.native'))
    $shipProcess = Get-Process -Id $shipId
    "$shipId|$($shipProcess.StartTime.ToUniversalTime().Ticks)" | Set-Content -LiteralPath $identityFile
    $ancestors = @()
    foreach ($ownerText in Get-Content -LiteralPath (Join-Path $Run 'owners')) {
        $ancestor = Get-Process -Id ([int]$ownerText) -ErrorAction SilentlyContinue
        if (!$ancestor) { throw 'Gate caller ended during startup' }
        $ancestors += $ancestor
    }
    while (Test-Path -LiteralPath (Join-Path $Run 'active')) {
        if ($shipProcess.HasExited) { break }
        if ((Test-Path -LiteralPath (Join-Path $Run 'cancelled')) -or @($ancestors | Where-Object HasExited).Count -gt 0) {
            New-Item -ItemType File -Path (Join-Path $Run 'cancelled') -Force | Out-Null
            Stop-Gate
            break
        }
        Start-Sleep -Milliseconds 250
    }
} catch {
    New-Item -ItemType File -Path (Join-Path $Run 'cancelled') -Force | Out-Null
    Stop-Gate
    throw
}
