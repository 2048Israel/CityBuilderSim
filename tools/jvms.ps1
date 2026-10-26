# ---------------------------------------------------------------------------
#  Who else is using the PC's Java: the other batches' checks and Jerus's
#  game, counted for Batch check.bat, batch-check.sh and ship.sh (TM1), and
#  waited for until the machine is quiet (TM2).
#
#      powershell -NoProfile -ExecutionPolicy Bypass -File tools\jvms.ps1
#      powershell -NoProfile -ExecutionPolicy Bypass -File tools\jvms.ps1 -Quiet
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
#  A check started while either is above 0 times soft (-Dcbs.timing=soft).
#  "0 0" when the processes cannot be read: timing is then hard, as before.
#
#  -Quiet is the ship gate's wait (batch-check.sh --timing, Batch check.bat
#  --timing): it looks again and again until the machine has been quiet for
#  a minute, and prints a line only when something changes, as it happens:
#    looks  4 empty looks in a row, 15 s apart; a look that finds another
#           batch's JVM starts the count over;
#    cap    30 minutes: then it ends all the same;
#    game   not waited for: either ending says so when it runs, whose load a
#           timing red may then be. CBS_QUIET_GAME=1 waits for it as for a JVM.
#  Exit 0: the quiet minute came ("a quiet minute - 4 of 4 quiet looks, 15 s
#  apart", with the time waited when the first look was busy). Exit 3: the cap
#  came first ("still ... after 30 minutes ... without the quiet minute").
#  The dials are variables below; a test shortens them with CBS_QUIET_LOOKS,
#  CBS_QUIET_SECS and CBS_QUIET_CAP_SECS.
#
#  CBS_JVMS_MARK, when set, narrows every look to the processes whose command
#  line contains that text (ordinal, case-sensitive): tools/quiet-selftest.sh
#  uses it so that another lane's real JVMs, or Jerus's game, cannot disturb it.
#  Unset, a look counts as above.
#
#  WHY (2026-10-18): batches took turns under "one JVM at a time on the
#  machine", and each session checked these command lines by hand before it
#  ran anything. Only the timing assertions ever clashed, and soft timing is
#  the answer to them; this is the look the scripts now take themselves.
#  TM2 (2026-10-25): the hard run waited for one empty look, and a runner that
#  starts its JVMs one after another (AllChecks, the ensemble's runner) leaves a
#  gap of a second or two between them; a look that fell in the gap started the
#  hard run, and the runner's next JVM ran beside it - TM1's own gate on
#  2026-10-18 waited two minutes, then ran beside FR2's ensemble. -Quiet waits
#  for a minute of empty looks instead.
# ---------------------------------------------------------------------------
param([switch]$Quiet)

# The processes to count: the batch JVMs and the game's, each once.
function Get-Counts {
    $batch = 0
    $game = 0
    $mark = $env:CBS_JVMS_MARK
    try {
        $procs = Get-CimInstance Win32_Process -ErrorAction Stop `
            -Filter "Name='java.exe' or Name='javaw.exe' or Name='CityBuilderSim.exe'"
        $kind = @{}
        foreach ($p in $procs) {
            $line = [string]$p.CommandLine
            if ($mark -and -not $line.Contains($mark)) { continue }
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
    return @($batch, $game)
}

if (-not $Quiet) {
    $c = Get-Counts
    "$($c[0]) $($c[1])"
    exit 0
}

# --- -Quiet: wait for a quiet minute (TM2) ---------------------------------

# The empty looks in a row the hard run waits for, 15 s apart: about a quiet minute, where a runner's gap between two JVMs is a second or two (TM2).
$looks = 4
# The seconds between two looks.
$secs = 15
# The most the wait takes, in seconds (30 minutes): then the hard run starts all the same.
$capSecs = 1800
# Whether Jerus's game is waited for as another batch's JVM is: off, since nobody waits on him to close it and a timing red beside it is judged against main's own run beside it (ship.sh); CBS_QUIET_GAME=1 turns it on for one gate.
$gameIsLoad = $false
if ($env:CBS_QUIET_GAME -eq '1') { $gameIsLoad = $true }
if ($env:CBS_QUIET_LOOKS) { $looks = [int]$env:CBS_QUIET_LOOKS }
if ($env:CBS_QUIET_SECS) { $secs = [int]$env:CBS_QUIET_SECS }
if ($env:CBS_QUIET_CAP_SECS) { $capSecs = [int]$env:CBS_QUIET_CAP_SECS }

# One line, written as it happens even through a pipe.
function Say([string]$text) { [Console]::Out.WriteLine('timing    ' + $text) }

# Who is running, as a look found it: only what counts as load; '' when the look is empty.
function Get-Who([int]$b, [int]$g) {
    $parts = @()
    if ($b -gt 0) { $parts += "$b other batch JVM(s)" }
    if ($g -gt 0 -and $gameIsLoad) { $parts += 'the game' }
    return ($parts -join ' and ')
}

$capWord = "$capSecs s"
if ($capSecs % 60 -eq 0) {
    $capWord = if ($capSecs -eq 60) { '1 minute' } else { "$($capSecs / 60) minutes" }
}

$clock = [Diagnostics.Stopwatch]::StartNew()
$emptyRun = 0              # empty looks in a row
$wasBusy = $false       # the look before this one found someone
$firstBusy = $false     # the very first look found someone
$lastWho = 'other JVM(s)'
$first = $true
while ($true) {
    $c = Get-Counts
    $b = [int]$c[0]
    $g = [int]$c[1]
    $who = Get-Who $b $g
    if ($who) {
        $lastWho = $who
        if ($first) { $firstBusy = $true }
        if (-not $wasBusy) {
            if ($emptyRun -eq 0) {
                Say "waiting for a quiet minute - $who running; 0 of $looks quiet looks, $secs s apart"
            } else {
                Say "$who running again after $emptyRun of $looks quiet looks: the count starts over"
            }
        }
        $emptyRun = 0
        $wasBusy = $true
    } else {
        $emptyRun++
        $wasBusy = $false
    }
    $first = $false
    $elapsed = [int][math]::Floor($clock.Elapsed.TotalSeconds)
    $rc = -1
    if ($emptyRun -ge $looks) {
        $text = "a quiet minute - $looks of $looks quiet looks, $secs s apart"
        if ($firstBusy) { $text += ", after $([math]::Floor($elapsed / 60)) min $($elapsed % 60) s of waiting" }
        Say $text
        $rc = 0
    } elseif ($elapsed -ge $capSecs) {
        Say "still $lastWho after $capWord, $emptyRun of $looks quiet looks: hard all the same, without the quiet minute - a red may be their load"
        $rc = 3
    }
    if ($rc -ge 0) {
        if (-not $gameIsLoad -and $g -gt 0) {
            Say 'the game is running: a timing red may be its load - close it if you can'
        }
        exit $rc
    }
    Start-Sleep -Seconds $secs
}
