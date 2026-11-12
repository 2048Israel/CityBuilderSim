# Own-process-tree cancellation on Windows. WHY (GF1, LM1): closing the caller left sh.exe ./ship.sh publishing.
param([ValidateSet('Watch', 'Stop')][string]$Mode, [string]$Run)
$ErrorActionPreference = 'Stop'
$identityFile = Join-Path $Run 'process'
function Stop-Gate {
    if (!(Test-Path -LiteralPath $identityFile)) { return }
    $identity = (Get-Content -LiteralPath $identityFile) -split '\|', 2
    $gateProcess = Get-Process -Id ([int]$identity[0]) -ErrorAction SilentlyContinue
    if ($gateProcess -and $gateProcess.StartTime.ToUniversalTime().Ticks -eq [long]$identity[1]) {
        $nativeRoot = Get-CimInstance Win32_Process -Filter "ProcessId = $($gateProcess.Id)"
        if ($nativeRoot.CommandLine -notmatch 'sh(\.exe)?[" ]+.*ship\.sh') { throw "Gate process identity changed: $($nativeRoot.CommandLine)" }
        # MSYS exec helpers also break native descendant links. Walk logical parents, then each native subtree.
        $rows = @(& (Join-Path (Split-Path $nativeRoot.ExecutablePath) 'ps.exe') | Select-Object -Skip 1 | ForEach-Object {
            $fields = $_.Trim() -split '\s+', 8
            [pscustomobject]@{ Shell = [int]$fields[0]; Parent = [int]$fields[1]; Native = [int]$fields[3] }
        })
        $owned = @($rows | Where-Object Native -eq $gateProcess.Id)
        if ($owned.Count -eq 0) { throw 'Gate shell is missing from the MSYS process table' }
        for ($at = 0; $at -lt $owned.Count; $at++) {
            $gateShellId = $owned[$at].Shell
            $owned += @($rows | Where-Object Parent -eq $gateShellId)
        }
        [array]::Reverse($owned)
        $stopping = @()
        foreach ($row in $owned) {
            $child = Get-Process -Id $row.Native -ErrorAction SilentlyContinue
            if ($child -and $child.StartTime -ge $gateProcess.StartTime) {
                $stopping += $child
                $ErrorActionPreference = 'Continue' # a child can exit between the identity check and taskkill
                & taskkill.exe /PID $child.Id /T /F 2>$null | Out-Null
                $ErrorActionPreference = 'Stop'
            }
        }
        for ($attempt = 0; $attempt -lt 20 -and @($stopping | Where-Object { !$_.HasExited }).Count -gt 0; $attempt++) { Start-Sleep -Milliseconds 250 }
        $remaining = @($stopping | Where-Object { !$_.HasExited })
        if ($remaining.Count -gt 0) { throw "Gate processes survived cancellation: $($remaining.Id -join ', ')" }
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
    # Include the host above MSYS (PowerShell or the app's task shell), while skipping vanished exec helpers.
    $parentId = (Get-CimInstance Win32_Process -Filter "ProcessId = $($ancestor.Id)").ParentProcessId
    while ($parentId -gt 0) {
        $parent = Get-Process -Id $parentId -ErrorAction SilentlyContinue
        if (!$parent -or $parent.StartTime -gt $ancestor.StartTime) { break }
        $ancestors += $parent
        $ancestor = $parent
        $parentId = (Get-CimInstance Win32_Process -Filter "ProcessId = $parentId").ParentProcessId
    }
    while (Test-Path -LiteralPath (Join-Path $Run 'active')) {
        if ($shipProcess.HasExited) { break }
        if ((Test-Path -LiteralPath (Join-Path $Run 'cancelled')) -or @($ancestors | Where-Object HasExited).Count -gt 0) {
            New-Item -ItemType File -Path (Join-Path $Run 'cancelled') -Force | Out-Null
            Stop-Gate
            '130' | Set-Content -LiteralPath (Join-Path $Run 'exit')
            "[$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')] gate      stopped with its caller; main and the tags are where they were"
            break
        }
        Start-Sleep -Milliseconds 250
    }
} catch {
    New-Item -ItemType File -Path (Join-Path $Run 'cancelled') -Force | Out-Null
    Stop-Gate
    throw
}
