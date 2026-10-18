# ---------------------------------------------------------------------------
#  Who else is using the PC's Java: the other batches' checks and Jerus's
#  game, counted for Batch check.bat, batch-check.sh and ship.sh (TM1).
#
#      powershell -NoProfile -ExecutionPolicy Bypass -File tools\jvms.ps1
#
#  Prints one line, "<batch> <game>":
#    batch  the java.exe and javaw.exe processes running a class of
#           ham.citybuildersim that is not the game's launcher: a suite and its
#           harness, LongPlaytest, Maps, a harness run from NetBeans;
#    game   the game's own: the launcher ham.citybuildersim.CityBuilderSim
#           (NetBeans' Run), the jar (Play CityBuilderSim.bat), or the
#           packaged CityBuilderSim.exe.
#  A check started while either is above 0 times soft (-Dcbs.timing=soft).
#  "0 0" when the processes cannot be read: timing is then hard, as before.
#
#  WHY (2026-10-18): batches took turns under "one JVM at a time on the
#  machine", and each session checked these command lines by hand before it
#  ran anything. Only the timing assertions ever clashed, and soft timing is
#  the answer to them; this is the look the scripts now take themselves.
# ---------------------------------------------------------------------------

$batch = 0
$game = 0
try {
    $procs = Get-CimInstance Win32_Process -ErrorAction Stop `
        -Filter "Name='java.exe' or Name='javaw.exe' or Name='CityBuilderSim.exe'"
    foreach ($p in $procs) {
        $line = [string]$p.CommandLine
        if ($p.Name -eq 'CityBuilderSim.exe' -or $line -cmatch 'ham\.citybuildersim\.CityBuilderSim\b' `
                -or $line -cmatch 'CityBuilderSim-[^\\/"]*\.jar') {
            $game++
        } elseif ($line -cmatch 'ham\.citybuildersim\.') {
            $batch++
        }
    }
} catch { }
"$batch $game"
