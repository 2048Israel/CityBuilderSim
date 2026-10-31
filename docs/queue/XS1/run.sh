#!/bin/sh
# run.sh <label> <classes-name> <seed> [extra java opts...] : AUTO2 <seed>, one CSV and log in $XS1_DIR/out/ (setup.sh says where).
# The switches: -Dxs1.off=key[,key] -Dxs1.from=<month> (switches.patch, XS1.java); DRV=driver-probe and -Dprobe.file/.from/.to for the probe.
HERE=$(cd "$(dirname "$0")" && pwd -W)
S=${XS1_DIR:-$(cd "$HERE/../../.." && pwd -W)/target/xs1}
L=$1; C=$2; SEED=$3; shift 3
DEPS=$(tr -d '\r\n' < "$S/deps.txt")
EXTRA="hot=getCapitalFlows.getStock;carry=getCapitalFlows.getCarryStock;spread=getCapitalFlows.getSpread;ranhome=getCapitalFlows.getRiskOffDeparted;carryrisk=getCapitalFlows.getCarryRiskRepaid;worldrate=getDebtManager.worldRate;riskoff=getWorldMoneys.riskOff;vaultint=getForeignAccounts.getVaultInterest;realgap=realRateDifferential;jobs=getPopulationManager.getTotalJobs;workers=getPopulationManager.getLabourForce;withheld=getBuildingManager.getPostsWithheld;unemp=getPopulationManager.getUnemployed;offered=getSectors.construction.getPostsOfferedShare"
java -Xmx2g -Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin -Dcbs.world=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/world-1.bin "-Djava.io.tmpdir=$S/tmp" "-Densemble.extra=$EXTRA" "$@" -cp "$S/${DRV:-driver-$C};$S/$C;$DEPS" \
  ham.citybuildersim.Ensemble AUTO2 "$SEED" "$S/out/$L.csv" > "$S/out/$L.log" 2>&1
echo "$L: $(grep -E '^DONE' "$S/out/$L.log" | tail -1)"
