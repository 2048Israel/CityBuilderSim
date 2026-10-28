# ---------------------------------------------------------------------------
#  Who else is using the PC's Java: the other batches' checks and Jerus's
#  game, counted for Batch check.bat and tools/deepseek/agent.py (TM1; SP1 took batch-check.sh off).
#
#      powershell -NoProfile -ExecutionPolicy Bypass -File tools\jvms.ps1
#
#  Prints one line, "<batch> <game>":
#    batch  the runs of a class of ham.citybuildersim that is not the game's
#           launcher: a suite, LongPlaytest, Ensemble, Maps, a harness run
#           from NetBeans;
#    game   the game's own: the launcher ham.citybuildersim.CityBuilderSim
#           (NetBeans' Run), the jar (Play CityBuilderSim.bat), or the
#           packaged CityBuilderSim.exe.
#  A run is counted once: a matching java.exe or javaw.exe whose parent
#  matches too is not counted again - the JVM the PATH's javapath shim starts
#  under itself, or a suite's harness under AllChecks.
#  A check started while either is above 0 times soft (-Dcbs.timing=soft) and
#  says so. "0 0" when the processes cannot be read.
#
#  WHY (2026-10-18): batches took turns under "one JVM at a time on the
#  machine", and each session checked these command lines by hand before it
#  ran anything. Only the timing assertions ever clashed, and soft timing is
#  the answer to them; this is the look the scripts now take themselves.
#  TM2 (2026-10-25) added -Quiet, a wait for a quiet minute before the gate's
#  hard timing; GP1 (2026-10-27) took it out: a timing red is judged side by
#  side with main's own run in the same minutes (TM3, ship.sh), which is what
#  the wait only approximated, and it cost the gate up to 30 minutes.
# ---------------------------------------------------------------------------
$batch = 0
$game = 0
try {
    $procs = Get-CimInstance Win32_Process -ErrorAction Stop `
        -Filter "Name='java.exe' or Name='javaw.exe' or Name='CityBuilderSim.exe'"
    $kind = @{}
    foreach ($p in $procs) {
        $line = [string]$p.CommandLine
        if ($p.Name -eq 'CityBuilderSim.exe' -or $line -cmatch 'ham\.citybuildersim\.CityBuilderSim\b' `
                -or $line -cmatch 'CityBuilderSim-[^\\/"]*\.jar') {
            $kind[$p.ProcessId] = 'game'
        } elseif ($line -cmatch 'ham\.citybuildersim\.') {
            $kind[$p.ProcessId] = 'batch'
        }
    }
    foreach ($p in $procs) {
        if (-not $kind.ContainsKey($p.ProcessId) -or $kind.ContainsKey($p.ParentProcessId)) { continue }
        if ($kind[$p.ProcessId] -eq 'game') { $game++ } else { $batch++ }
    }
} catch { }
"$batch $game"
