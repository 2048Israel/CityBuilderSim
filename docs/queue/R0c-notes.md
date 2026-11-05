# R0c - the 1900 mechanics ({next}, 2026-11-05)

## WHERE IT STANDS

Started 00:41 from `batch/R0b` (R0b's ship file up, at its gate). Slices 1
(the farm's horses, the factory's steam) and 2 (road freight's horse and
wagon) committed; slice 3 (the homes' lamp oil and coal) with Sonnet. Then
`--pick`, the trace against main's, R0b's merge, the ship file.

## 1. What it builds

The brief is research-1900 §4, the rows that are tech (hydro is the
electricity run's, the causes of death R0b's, the rest "no new mechanism" or
"leave out"). Each is a **form**: a share, 0 to 1, of a sector (or of the
homes) that runs the 1900 way, built and **off**: 0 in every played city, so
the month is today's to the bit (Forms1900Check 1, twin cities over twelve
months); only the harness sets a share.

| form | at share 1 | the setter | what replaces it, and when |
|---|---|---|---|
| horses on farms | 22% of the farm's own crop eaten captive by its horses; posts x 1/(1-0.273); a tonne's cost over the crop sold | `Agriculture.setHorseShare` | the tractor (PE14, 1917), fitted when it pays |
| horses for cartage | Haulage's city leg carried by hired teams (10 t a team-month, two horses, one carter), the horses' feed (0.29 t a horse-month) bought as `CROPS`, the carters NO_DIPLOMA posts, no diesel, no trucks | `Haulage.setHorseShare` | the motor truck (§3, known in 1900), fitted when it pays |
| steam in factories | no grid draw (the bill and the city's demand fall with it); `THERMAL_COAL` bought for the draw at 1.8 kg a kWh | `Sector.setSteamShare`, any sector | electric drive (§3), fitted when it pays |
| household light and heat | slice 3 | `HomeFuel` | the grid's reach; R0e sizes it by year |

The farm's and the factory's forms go through paths the tree already had: a
farm feeding its horses is CAPTIVE USE (Sector, the coke oven's own coke),
the steam coal an ordinary input (`Sector.formInput()`, read by
`getInputAtCapacity()` and the market pass's `inputToMake()`). Posts past a
sector's buildings reach the labour market through
`BuildingManager.setPostsPast()` (the farm's extra hands, the carters).

## 2. The calls (★)

1. **★ The 1900 forms are built off, and switch on with R1.** Recommended and
   taken. Why: research-1900 §3 says the truck, the motor and the tractor
   spread "when they pay" and "nothing in the game needs to schedule that", so
   what moves a share is R1's equipment fitted when it pays (and R2's PE14),
   not a date this batch could honestly write; and R0's call 2 the other way
   round: 1900's tech beside today's wages is a fraction of the profit per
   worker (slice 2's fixture: a carter's month is about 27 times the horses'
   feed), so the forms go on with R0d's ladder, which R0 already ties to R1.
   Off, the trace does not move, so no ensemble (batch.md 6).
2. **★ The shares are not saved.** Recommended and taken: nothing sets one in
   a played city, so a saved field would be a zero written and read forever;
   R1 saves each building's recipe, from which it sets them, and R0e the
   homes'. ReadPathCheck and SaveFileCheck see a field in neither.
3. **★ A share of the sector, not of each building.** Recommended and taken:
   R1 fits equipment building by building and sets the sector's share from
   its count; the forms read one number, so R1 changes none of this code.
4. **★ The horse feed is the study's share of the crop, not 3 acres a horse.**
   The farm templates' acres and tonnes are the game's (a Grain Farm's 55
   acres make 500 t a month), so the 22% of US cropland that fed horses
   [P2.47] is the share used, and Canada's 1.58 million horses at 3 acres
   agree (est.). The horse farm's extra hands are the tractor's 27.3% of farm
   jobs (PE14) undone.
5. **★ The tractor's petrol and the farm's own vans are R1's.** Today's farm
   buys no `PETROL` (it has no tractor fuel), so "the tractor switches it to
   PETROL" is a change to today's recipe that would move every city's trace:
   R1 writes it with the recipes. The farms' delivery vans (`runFleet()`,
   diesel) stay as they are.
6. **★ The carts' teams are hired, their capital left out.** A team and wagon
   is not a good the game has (a truck is `Good.TRUCKS`), so the horses' and
   wagons' price is not in the quote; R1's equipment is where the team, the
   truck's 1900 form, gets a price. Recommended: leave it to R1.
7. **★ The carters are sized on the works' nameplate need, as the trucks
   are.** Haulage sizes its fleet on the plant standing at nameplate, not the
   month's legs (its own comment: a firm buys lorries for the factory it
   has), and its drivers with it; the carters follow. In slice 2's fixture
   that is about 30,000 posts for about 5,800 t carried, because its works run
   far under nameplate. Recommended: keep the trucks' rule, and R1 judges it
   on a real 1900 city.
8. **★ Steam under a brownout.** The grid's energy ratio still scales the
   whole sector's rate, its steam share included. Recommended: keep (a
   share-weighted ratio is one line in `getOperatingRate()`, every sector's
   hottest path, and R1 can add it when it sets real shares).
9. **★ The Bus Network's horse omnibus is left out.** The bus is a city
   building that burns nothing and buys nothing, and the city has no path to
   buy feed; it goes to Pending for R1 or the city's own works' recipes.
10. **★ Hydro is not this batch's.** The study's §4 gives it a new template
    needing a river and a drop on Earth's map (Q4), for the electricity run.

## 3. Known reds (the orchestrator's list, 2026-11-05)

PriceLevelCheck 8 (R0's farm retirement, RT1), ReplayCheck 5d "asks -1"
(CM1b), MapCheck 2h and SlopeCheck's "a building on the map moved" (DM2),
ShortSellingCheck 7 (orphans' shares, OH1) pass by name at the gate.
